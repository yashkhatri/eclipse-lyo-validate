package org.eclipse.lyo.validate.shacl;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.lyo.oslc4j.core.annotation.OslcDescription;
import org.eclipse.lyo.oslc4j.core.annotation.OslcName;
import org.eclipse.lyo.oslc4j.core.annotation.OslcNamespace;
import org.eclipse.lyo.oslc4j.core.annotation.OslcPropertyDefinition;
import org.eclipse.lyo.oslc4j.core.annotation.OslcResourceShape;
import org.eclipse.lyo.oslc4j.core.annotation.OslcValueType;
import org.eclipse.lyo.oslc4j.core.exception.OslcCoreApplicationException;
import org.eclipse.lyo.oslc4j.core.exception.OslcCoreDuplicatePropertyDefinitionException;
import org.eclipse.lyo.oslc4j.core.exception.OslcCoreInvalidPropertyDefinitionException;
import org.eclipse.lyo.oslc4j.core.exception.OslcCoreMissingAnnotationException;
import org.eclipse.lyo.oslc4j.core.model.IReifiedResource;
import org.eclipse.lyo.oslc4j.core.model.InheritedMethodAnnotationHelper;
import org.eclipse.lyo.oslc4j.core.model.ResourceShapeFactory;
import org.eclipse.lyo.oslc4j.core.model.ValueType;
import org.eclipse.lyo.validate.shacl.annotations.RDFType;
import org.eclipse.lyo.validate.shacl.annotations.RdfsIsDefinedBy;
import org.eclipse.lyo.validate.shacl.annotations.RdfsLabel;
import org.eclipse.lyo.validate.shacl.annotations.ShaclClassType;
import org.eclipse.lyo.validate.shacl.annotations.ShaclClosed;
import org.eclipse.lyo.validate.shacl.annotations.ShaclDataType;
import org.eclipse.lyo.validate.shacl.annotations.ShaclIn;
import org.eclipse.lyo.validate.shacl.annotations.ShaclLanguageIn;
import org.eclipse.lyo.validate.shacl.annotations.ShaclMaxCount;
import org.eclipse.lyo.validate.shacl.annotations.ShaclMaxExclusive;
import org.eclipse.lyo.validate.shacl.annotations.ShaclMaxInclusive;
import org.eclipse.lyo.validate.shacl.annotations.ShaclMaxLength;
import org.eclipse.lyo.validate.shacl.annotations.ShaclMinCount;
import org.eclipse.lyo.validate.shacl.annotations.ShaclMinExclusive;
import org.eclipse.lyo.validate.shacl.annotations.ShaclMinInclusive;
import org.eclipse.lyo.validate.shacl.annotations.ShaclMinLength;
import org.eclipse.lyo.validate.shacl.annotations.ShaclName;
import org.eclipse.lyo.validate.shacl.annotations.ShaclPattern;
import org.eclipse.lyo.validate.shacl.annotations.ShaclTargetNode;
import org.eclipse.lyo.validate.shacl.annotations.ShaclUniqueLang;


public final class ShaclShapeFactory extends ResourceShapeFactory{
	private static final String METHOD_NAME_START_GET = "get";
	private static final String METHOD_NAME_START_IS  = "is";

	private static final int METHOD_NAME_START_GET_LENGTH = METHOD_NAME_START_GET.length();
	private static final int METHOD_NAME_START_IS_LENGTH  = METHOD_NAME_START_IS.length();


	private ShaclShapeFactory() {
		super();
	}

	public static ShaclShape createShaclShape(final Class<?> resourceClass)
		   throws OslcCoreApplicationException, URISyntaxException {
		final HashSet<Class<?>> verifiedClasses = new HashSet<Class<?>>();
		verifiedClasses.add(resourceClass);

		return createShaclShape(resourceClass, verifiedClasses);
	}

	private static ShaclShape createShaclShape(final Class<?> resourceClass,
													final Set<Class<?>> verifiedClasses)
		   throws OslcCoreApplicationException, URISyntaxException {
		final OslcResourceShape resourceShapeAnnotation = resourceClass.getAnnotation(OslcResourceShape.class);
		if (resourceShapeAnnotation == null) {
			throw new OslcCoreMissingAnnotationException(resourceClass, OslcResourceShape.class);
		}

		OslcNamespace oslcNamespace = resourceClass.getAnnotation(OslcNamespace.class);
		OslcName oslcName = resourceClass.getAnnotation(OslcName.class);
		
		final URI about = new URI(oslcNamespace.value() + oslcName.value());
		final ShaclShape shaclShape = new ShaclShape(about);
		
		shaclShape.addtargetClass(new URI(oslcNamespace.value() + oslcName.value()));
		
		RdfsLabel label = resourceClass.getAnnotation(RdfsLabel.class);
		if(label!=null) {
		shaclShape.setLabel(label.value());
		} 
		
		RdfsIsDefinedBy isDefinedBy = resourceClass.getAnnotation(RdfsIsDefinedBy.class);
		if(isDefinedBy!=null) {
		shaclShape.setIsDefinedBy(new URI(isDefinedBy.value()));
		}
		
		final RDFType rdfTypeAnotation  = resourceClass.getAnnotation(RDFType.class);
		if(rdfTypeAnotation!=null) {
			shaclShape.setType(new URI(rdfTypeAnotation.value()));
		}
		
		final Set<String> propertyDefinitions = new HashSet<String>();

		for (final Method method : resourceClass.getMethods()) {
			if (method.getParameterTypes().length == 0) {
				final String methodName = method.getName();
				final int methodNameLength = methodName.length();
				if (((methodName.startsWith(METHOD_NAME_START_GET)) && (methodNameLength > METHOD_NAME_START_GET_LENGTH)) ||
					((methodName.startsWith(METHOD_NAME_START_IS)) && (methodNameLength > METHOD_NAME_START_IS_LENGTH))) {
					final OslcPropertyDefinition propertyDefinitionAnnotation = InheritedMethodAnnotationHelper.getAnnotation(method, OslcPropertyDefinition.class);
					if (propertyDefinitionAnnotation != null) {
						final String propertyDefinition = propertyDefinitionAnnotation.value();
						if (propertyDefinitions.contains(propertyDefinition)) {
							throw new OslcCoreDuplicatePropertyDefinitionException(resourceClass, propertyDefinitionAnnotation);
						}

						propertyDefinitions.add(propertyDefinition);

						final Property property = createProperty(resourceClass, method, propertyDefinitionAnnotation, verifiedClasses);
						shaclShape.addProperty(property);

						validateSetMethodExists(resourceClass, method);
					}
				}
			}
		}

		return shaclShape;
	}

	@SuppressWarnings("rawtypes") // supress warning when casting Arrays.asList() to a Collection
	private static Property createProperty( final Class<?> resourceClass, final Method method, final OslcPropertyDefinition propertyDefinitionAnnotation, final Set<Class<?>> verifiedClasses) throws OslcCoreApplicationException, URISyntaxException {
		final String name;
		
		final OslcName nameAnnotation = InheritedMethodAnnotationHelper.getAnnotation(method, OslcName.class);
		if (nameAnnotation != null) {
			name = nameAnnotation.value();
		} else {
			name = getDefaultPropertyName(method);
		}

		final String propertyDefinition = propertyDefinitionAnnotation.value();

		if (!propertyDefinition.endsWith(name)) {
			throw new OslcCoreInvalidPropertyDefinitionException(resourceClass, method, propertyDefinitionAnnotation);
		}

		final Class<?> returnType = method.getReturnType();
		
		Class<?> componentType = getComponentType(resourceClass, method, returnType);
		
		// Reified resources are a special case.
		if (IReifiedResource.class.isAssignableFrom(componentType))
		{
			final Type genericType = componentType.getGenericSuperclass();

			if (genericType instanceof ParameterizedType)
			{
				final ParameterizedType parameterizedType = (ParameterizedType) genericType;
				final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
				if (actualTypeArguments.length == 1)
				{
					final Type actualTypeArgument = actualTypeArguments[0];
					if (actualTypeArgument instanceof Class)
					{
						componentType = (Class<?>) actualTypeArgument;
					}
				}
			}
		}

		
		final Property property = new Property();
		property.setPredicate(new URI(propertyDefinition));


		//Setting Value Type
		ValueType valueType = null;
		final OslcValueType valueTypeAnnotation = InheritedMethodAnnotationHelper.getAnnotation(method, OslcValueType.class);
		if (valueTypeAnnotation != null) {
			valueType = valueTypeAnnotation.value();
			validateUserSpecifiedValueType(resourceClass, method, valueType, componentType); }
//		} else {
//			valueType = getDefaultValueType(resourceClass, method, componentType);
//		}

		final ShaclDataType dataTypeAnotation  = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclDataType.class);
		if(dataTypeAnotation!=null) {
		property.setDataType(dataTypeAnotation.value());
		}
		
		final OslcDescription oslcDescription = InheritedMethodAnnotationHelper.getAnnotation(method, OslcDescription.class);
		if (oslcDescription != null) {
			property.setDescription(oslcDescription.value());
		}
		
		final ShaclMaxCount maxCountAnnotation = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclMaxCount.class);
		if (maxCountAnnotation != null) {
			property.setMaxCount(new BigInteger(String.valueOf(maxCountAnnotation.value())));
		}
		
		final ShaclMinCount minCountAnnotation = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclMinCount.class);
		if (minCountAnnotation != null) {
			property.setMinCount(new BigInteger(String.valueOf(minCountAnnotation.value())));
		}
		
		final ShaclMinExclusive minExclusiveAnnotation = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclMinExclusive.class);
		if (minExclusiveAnnotation != null) {
			property.setMinExclusive(new BigInteger(String.valueOf(minExclusiveAnnotation.value())));
		}
		
		final ShaclMaxExclusive maxExclusiveAnnotation = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclMaxExclusive.class);
		if (maxExclusiveAnnotation != null) {
			property.setMaxExclusive(new BigInteger(String.valueOf(maxExclusiveAnnotation.value())));
		}
		
		final ShaclMinInclusive minInclusiveAnnotation = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclMinInclusive.class);
		if (minInclusiveAnnotation != null) {
			property.setMinInclusive(new BigInteger(String.valueOf(minInclusiveAnnotation.value())));
		}
		
		final ShaclMaxInclusive maxInclusiveAnnotation = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclMaxInclusive.class);
		if (maxInclusiveAnnotation != null) {
			property.setMaxInclusive(new BigInteger(String.valueOf(maxInclusiveAnnotation.value())));
		}
		
		final ShaclClassType shaclClass = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclClassType.class);
		if (shaclClass != null) {
			property.setClassType(new URI(shaclClass.value()));
		}
		
		final ShaclName shaclName = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclName.class);
		if (shaclName != null) {
			property.setName(shaclName.value());
		}
		
		final ShaclPattern shaclPattern = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclPattern.class);
		if (shaclPattern != null) {
			property.setPattern(shaclPattern.value());
		}
		
		final ShaclUniqueLang shaclUniqueLang = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclUniqueLang.class);
		if (shaclUniqueLang != null) {
			property.setUniqueLang(shaclUniqueLang.value());
		}
		
		final ShaclClosed shaclClosed = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclClosed.class);
		if (shaclClosed != null) {
			property.setClosed(shaclClosed.value());
		}
		
		final ShaclTargetNode shaclTargetNode = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclTargetNode.class);
		if (shaclTargetNode != null) {
			property.setTargetNode(new URI(shaclTargetNode.value()));
		}
		
		final ShaclLanguageIn shaclLanguageIn = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclLanguageIn.class);
		if (shaclLanguageIn != null) {
			property.setLanguageIn(shaclLanguageIn.value());
		}
		
		final ShaclMinLength shaclMinLength = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclMinLength.class);
		if (shaclMinLength != null) {
			property.setMinLength(new BigInteger(String.valueOf(shaclMinLength.value())));
		}
		
		final ShaclMaxLength shaclMaxLength = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclMaxLength.class);
		if (shaclMaxLength != null) {
			property.setMaxLength(new BigInteger(String.valueOf(shaclMaxLength.value())));
		}
		
		final ShaclIn inAnnotation = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclIn.class);
		if (inAnnotation != null) {
			Object[] object = new Object[inAnnotation.value().length];
			for(int i = 0;i < inAnnotation.value().length; i++) {
				if(inAnnotation.valueType() == Integer.class) {
					object[i] =  new BigInteger(inAnnotation.value()[i]);
				} else if (inAnnotation.valueType() == String.class){
					object[i] =  inAnnotation.value()[i];
				} else if(inAnnotation.valueType() == URI.class) {
					object[i] =  new URI(inAnnotation.value()[i]);
				}
			}
			property.setIn(object);
		}

		return property;
	}
}
