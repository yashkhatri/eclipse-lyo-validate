package org.eclipse.lyo.validate.shacl;


import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.lyo.oslc4j.core.annotation.OslcDescription;
import org.eclipse.lyo.oslc4j.core.annotation.OslcMaxSize;
import org.eclipse.lyo.oslc4j.core.annotation.OslcName;
import org.eclipse.lyo.oslc4j.core.annotation.OslcNamespace;
import org.eclipse.lyo.oslc4j.core.annotation.OslcOccurs;
import org.eclipse.lyo.oslc4j.core.annotation.OslcPropertyDefinition;
import org.eclipse.lyo.oslc4j.core.annotation.OslcResourceShape;
import org.eclipse.lyo.oslc4j.core.annotation.OslcValueShape;
import org.eclipse.lyo.oslc4j.core.annotation.OslcValueType;
import org.eclipse.lyo.oslc4j.core.exception.OslcCoreApplicationException;
import org.eclipse.lyo.oslc4j.core.exception.OslcCoreDuplicatePropertyDefinitionException;
import org.eclipse.lyo.oslc4j.core.exception.OslcCoreInvalidPropertyDefinitionException;
import org.eclipse.lyo.oslc4j.core.exception.OslcCoreMissingAnnotationException;
import org.eclipse.lyo.oslc4j.core.exception.OslcCoreMissingSetMethodException;
import org.eclipse.lyo.oslc4j.core.model.IReifiedResource;
import org.eclipse.lyo.oslc4j.core.model.InheritedMethodAnnotationHelper;
import org.eclipse.lyo.oslc4j.core.model.Occurs;
import org.eclipse.lyo.oslc4j.core.model.ResourceShapeFactory;
import org.eclipse.lyo.oslc4j.core.model.ValueType;
import org.eclipse.lyo.validate.constants.DataType;
import org.eclipse.lyo.validate.shacl.annotations.RDFType;
import org.eclipse.lyo.validate.shacl.annotations.RdfsIsDefinedBy;
import org.eclipse.lyo.validate.shacl.annotations.RdfsLabel;
import org.eclipse.lyo.validate.shacl.annotations.ShaclClassType;
import org.eclipse.lyo.validate.shacl.annotations.ShaclClosed;
import org.eclipse.lyo.validate.shacl.annotations.ShaclDataType;
import org.eclipse.lyo.validate.shacl.annotations.ShaclDisjoint;
import org.eclipse.lyo.validate.shacl.annotations.ShaclEquals;
import org.eclipse.lyo.validate.shacl.annotations.ShaclGroup;
import org.eclipse.lyo.validate.shacl.annotations.ShaclHasValue;
import org.eclipse.lyo.validate.shacl.annotations.ShaclIgnoredProperties;
import org.eclipse.lyo.validate.shacl.annotations.ShaclIn;
import org.eclipse.lyo.validate.shacl.annotations.ShaclLanguageIn;
import org.eclipse.lyo.validate.shacl.annotations.ShaclLessThan;
import org.eclipse.lyo.validate.shacl.annotations.ShaclLessThanOrEquals;
import org.eclipse.lyo.validate.shacl.annotations.ShaclMaxCount;
import org.eclipse.lyo.validate.shacl.annotations.ShaclMaxExclusive;
import org.eclipse.lyo.validate.shacl.annotations.ShaclMaxInclusive;
import org.eclipse.lyo.validate.shacl.annotations.ShaclMaxLength;
import org.eclipse.lyo.validate.shacl.annotations.ShaclMinCount;
import org.eclipse.lyo.validate.shacl.annotations.ShaclMinExclusive;
import org.eclipse.lyo.validate.shacl.annotations.ShaclMinInclusive;
import org.eclipse.lyo.validate.shacl.annotations.ShaclMinLength;
import org.eclipse.lyo.validate.shacl.annotations.ShaclName;
import org.eclipse.lyo.validate.shacl.annotations.ShaclNode;
import org.eclipse.lyo.validate.shacl.annotations.ShaclOrder;
import org.eclipse.lyo.validate.shacl.annotations.ShaclPattern;
import org.eclipse.lyo.validate.shacl.annotations.ShaclTargetClass;
import org.eclipse.lyo.validate.shacl.annotations.ShaclTargetNode;
import org.eclipse.lyo.validate.shacl.annotations.ShaclTargetObjectsOf;
import org.eclipse.lyo.validate.shacl.annotations.ShaclTargetSubjectsOf;
import org.eclipse.lyo.validate.shacl.annotations.ShaclUniqueLang;


public final class ShaclShapeFactory extends ResourceShapeFactory{
	private static final String METHOD_NAME_START_GET = "get";
	private static final String METHOD_NAME_START_IS  = "is";

	private static final int METHOD_NAME_START_GET_LENGTH = METHOD_NAME_START_GET.length();
	private static final int METHOD_NAME_START_IS_LENGTH  = METHOD_NAME_START_IS.length();
	
	private static boolean readShaclAnnotations = false;


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

		populateFromClassLevelAnnotations(shaclShape, resourceClass, oslcNamespace, oslcName, about);

		final Set<String> propertyDefinitions = new HashSet<String>();

		createProperties(resourceClass, verifiedClasses, shaclShape, propertyDefinitions, true);

		if(!readShaclAnnotations) {
			shaclShape.setShaclProperties(null);
			propertyDefinitions.clear();
			createProperties(resourceClass, verifiedClasses, shaclShape, propertyDefinitions, false);
		}

		//resetting it to false again.
		readShaclAnnotations = false;
		return shaclShape;
	}

	/**
	 * @param resourceClass
	 * @param oslcNamespace
	 * @param oslcName
	 * @param about
	 * @return
	 * @throws URISyntaxException
	 */
	private static void populateFromClassLevelAnnotations(ShaclShape shaclShape, final Class<?> resourceClass, OslcNamespace oslcNamespace,
			OslcName oslcName, final URI about) throws URISyntaxException {

		//Target Constraints Start
		final ShaclTargetNode shaclTargetNode = resourceClass.getAnnotation(ShaclTargetNode.class);
		if (shaclTargetNode != null) {
			shaclShape.setTargetNode(new URI(shaclTargetNode.value()));
		}

		final ShaclTargetObjectsOf shaclTargetObjectsOf = resourceClass.getAnnotation(ShaclTargetObjectsOf.class);
		if (shaclTargetObjectsOf != null) {
			shaclShape.setTargetObjectsOf(new URI(shaclTargetObjectsOf.value()));
		}

		final ShaclTargetSubjectsOf shaclTargetSubjectsOf = resourceClass.getAnnotation(ShaclTargetSubjectsOf.class);
		if (shaclTargetSubjectsOf != null) {
			shaclShape.setTargetSubjectsOf(new URI(shaclTargetSubjectsOf.value()));
		}

		final ShaclTargetClass shaclTargetClass = resourceClass.getAnnotation(ShaclTargetClass.class);
		if (shaclTargetClass != null) {
			shaclShape.setTargetClass(new URI(shaclTargetClass.value()));
		} else {
			shaclShape.setTargetClass(new URI(oslcNamespace.value() + oslcName.value()));

		}
		//Target Constraints End

		RdfsIsDefinedBy isDefinedBy = resourceClass.getAnnotation(RdfsIsDefinedBy.class);
		if(isDefinedBy!=null) {
			shaclShape.setIsDefinedBy(new URI(isDefinedBy.value()));
		}

		RdfsLabel label = resourceClass.getAnnotation(RdfsLabel.class);
		if(label!=null) {
			shaclShape.setLabel(label.value());
		} 

		final RDFType rdfTypeAnotation  = resourceClass.getAnnotation(RDFType.class);
		if(rdfTypeAnotation!=null) {
			shaclShape.setType(new URI(rdfTypeAnotation.value()));
		}

		final ShaclClosed shaclClosed = resourceClass.getAnnotation(ShaclClosed.class);
		if (shaclClosed != null) {
			shaclShape.setClosed(shaclClosed.value());
		}

		final ShaclIgnoredProperties shaclIgnoredProperties = resourceClass.getAnnotation(ShaclIgnoredProperties.class);
		if (shaclIgnoredProperties != null) {
			List<URI> ignoredPropertiesList = new ArrayList<URI>();
			for (String ignoredProperty: shaclIgnoredProperties.values()) {   
				ignoredPropertiesList.add(new URI(ignoredProperty));
			}
			shaclShape.setIgnoredProperties(ignoredPropertiesList);
		}
	}

	/**
	 * @param resourceClass
	 * @param verifiedClasses
	 * @param shaclShape
	 * @param propertyDefinitions
	 * @throws OslcCoreDuplicatePropertyDefinitionException
	 * @throws URISyntaxException
	 * @throws OslcCoreApplicationException
	 * @throws OslcCoreMissingSetMethodException
	 */
	private static void createProperties(final Class<?> resourceClass, final Set<Class<?>> verifiedClasses,
			final ShaclShape shaclShape, final Set<String> propertyDefinitions, boolean chooseShacl)
			throws OslcCoreDuplicatePropertyDefinitionException, URISyntaxException, OslcCoreApplicationException,
			OslcCoreMissingSetMethodException {
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

						if(chooseShacl) {
							final Property property = createPropertiesFromShaclAnnotations(resourceClass, method, propertyDefinitionAnnotation, verifiedClasses);
							shaclShape.addProperty(property);
						} else {
							final Property property = 	createPropertiesFromOslcAnnotations(resourceClass, method, propertyDefinitionAnnotation, verifiedClasses);
							shaclShape.addProperty(property);
						} 
							
						

						validateSetMethodExists(resourceClass, method);
					}
				}
			}
		}
	}

	private static Property createPropertiesFromOslcAnnotations(Class<?> resourceClass, Method method, OslcPropertyDefinition propertyDefinitionAnnotation, Set<Class<?>> verifiedClasses) throws URISyntaxException, OslcCoreApplicationException {

		Class<?> componentType = 	createPropertyCommon(resourceClass, method, propertyDefinitionAnnotation, verifiedClasses);

		final Property property = new Property();
		property.setPredicate(new URI(propertyDefinitionAnnotation.value()));


		//Setting Value Type
		ValueType valueType = null;
		final OslcValueType valueTypeAnnotation = InheritedMethodAnnotationHelper.getAnnotation(method, OslcValueType.class);
		if (valueTypeAnnotation != null) {
			valueType = valueTypeAnnotation.value();
			validateUserSpecifiedValueType(resourceClass, method, valueType, componentType);
			property.setDataType(DataType.fromString(valueType.toString()));

		}

		//Cardinality Constraint Components Start
		final OslcOccurs oslcOccurs = InheritedMethodAnnotationHelper.getAnnotation(method, OslcOccurs.class);
		if (oslcOccurs != null && oslcOccurs.value().equals(Occurs.ExactlyOne)) {
			property.setMaxCount(new BigInteger("1"));
			property.setMinCount(new BigInteger("1"));
		}

		if (oslcOccurs != null && oslcOccurs.value().equals(Occurs.OneOrMany)) {
			property.setMinCount(new BigInteger("1"));
		}

		if (oslcOccurs != null && oslcOccurs.value().equals(Occurs.ZeroOrMany)) {
			property.setMinCount(new BigInteger("0"));
		}
		
		if (oslcOccurs != null && oslcOccurs.value().equals(Occurs.ZeroOrOne)) {
			property.setMinCount(new BigInteger("0"));
			property.setMaxCount(new BigInteger("0"));
		}
		//Cardinality Constraint Components End

		
		//String Based Constraints Start
		final OslcMaxSize oslcMaxSize = InheritedMethodAnnotationHelper.getAnnotation(method, OslcMaxSize.class);
		if (oslcMaxSize != null) {
			property.setMaxLength(new BigInteger(String.valueOf(oslcMaxSize.value())));
		}
		//String Based Constraints End


		final OslcName oslcName = InheritedMethodAnnotationHelper.getAnnotation(method, OslcName.class);
		if (oslcName != null) {
			property.setName(oslcName.value());
		}
		
		final OslcDescription oslcDescription = InheritedMethodAnnotationHelper.getAnnotation(method, OslcDescription.class);
		if (oslcDescription != null) {
			property.setDescription(oslcDescription.value());
		}

		final OslcValueShape oslcValueShape = InheritedMethodAnnotationHelper.getAnnotation(method, OslcValueShape.class);
		if (oslcValueShape != null) {
			property.setNode(new URI(oslcValueShape.value()));
		}
		
//		final OslcRange oslcRange = InheritedMethodAnnotationHelper.getAnnotation(method, OslcRange.class);
//		if (oslcRange != null) {
//			property.setHasValue(new URI(oslcRange.value()));   //have multiple values check this..
//		}

		return property;
	}

	//***

	@SuppressWarnings("rawtypes") // supress warning when casting Arrays.asList() to a Collection
	private static Class<?> createPropertyCommon( final Class<?> resourceClass, final Method method, final OslcPropertyDefinition propertyDefinitionAnnotation, final Set<Class<?>> verifiedClasses) throws OslcCoreApplicationException, URISyntaxException {
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

		return componentType;
	}

	/**
	 * @param resourceClass
	 * @param method
	 * @param propertyDefinition
	 * @param componentType
	 * @return
	 * @throws URISyntaxException
	 * @throws OslcCoreApplicationException 
	 */
	private static Property createPropertiesFromShaclAnnotations(final Class<?> resourceClass, final Method method,
			final OslcPropertyDefinition propertyDefinitionAnnotation , final Set<Class<?>> verifiedClasses)
					throws URISyntaxException, OslcCoreApplicationException {


		Class<?> componentType = 	createPropertyCommon(resourceClass, method, propertyDefinitionAnnotation, verifiedClasses);

		final Property property = new Property();
		property.setPredicate(new URI(propertyDefinitionAnnotation.value()));


		//Setting Value Type
		ValueType valueType = null;
		final OslcValueType valueTypeAnnotation = InheritedMethodAnnotationHelper.getAnnotation(method, OslcValueType.class);
		if (valueTypeAnnotation != null) {
			valueType = valueTypeAnnotation.value();
			validateUserSpecifiedValueType(resourceClass, method, valueType, componentType);
		}

		final ShaclDataType dataTypeAnotation  = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclDataType.class);
		if(dataTypeAnotation!=null) {
			property.setDataType(dataTypeAnotation.value());
			readShaclAnnotations = true;
		}

		//Cardinality Constraint Components Start
		final ShaclMaxCount maxCountAnnotation = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclMaxCount.class);
		if (maxCountAnnotation != null) {
			property.setMaxCount(new BigInteger(String.valueOf(maxCountAnnotation.value())));
			readShaclAnnotations = true;
		}

		final ShaclMinCount minCountAnnotation = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclMinCount.class);
		if (minCountAnnotation != null) {
			property.setMinCount(new BigInteger(String.valueOf(minCountAnnotation.value())));
			readShaclAnnotations = true;
		}
		//Cardinality Constraint Components End

		//Value Range Constraint Components Start
		final ShaclMinExclusive minExclusiveAnnotation = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclMinExclusive.class);
		if (minExclusiveAnnotation != null) {
			property.setMinExclusive(new BigInteger(String.valueOf(minExclusiveAnnotation.value())));
			readShaclAnnotations = true;
		}

		final ShaclMaxExclusive maxExclusiveAnnotation = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclMaxExclusive.class);
		if (maxExclusiveAnnotation != null) {
			property.setMaxExclusive(new BigInteger(String.valueOf(maxExclusiveAnnotation.value())));
			readShaclAnnotations = true;
		}

		final ShaclMinInclusive minInclusiveAnnotation = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclMinInclusive.class);
		if (minInclusiveAnnotation != null) {
			property.setMinInclusive(new BigInteger(String.valueOf(minInclusiveAnnotation.value())));
			readShaclAnnotations = true;
		}

		final ShaclMaxInclusive maxInclusiveAnnotation = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclMaxInclusive.class);
		if (maxInclusiveAnnotation != null) {
			property.setMaxInclusive(new BigInteger(String.valueOf(maxInclusiveAnnotation.value())));
			readShaclAnnotations = true;
		}
		//Value Range Constraint Components End


		final ShaclClassType shaclClass = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclClassType.class);
		if (shaclClass != null) {
			property.setClassType(new URI(shaclClass.value()));
			readShaclAnnotations = true;
		}

		final ShaclNode shaclNode = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclNode.class);
		if (shaclNode != null) {
			property.setNode(new URI(shaclNode.value()));
			readShaclAnnotations = true;
		}

		//Non Validating Constraints Start
		final ShaclName shaclName = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclName.class);
		if (shaclName != null) {
			property.setName(shaclName.value());
			readShaclAnnotations = true;
		}

		final OslcDescription oslcDescription = InheritedMethodAnnotationHelper.getAnnotation(method, OslcDescription.class);
		if (oslcDescription != null) {
			property.setDescription(oslcDescription.value());
		}

		final ShaclGroup shaclGroup = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclGroup.class);
		if (shaclGroup != null) {
			property.setGroup(new URI(shaclGroup.value()));
			readShaclAnnotations = true;
		}

		final ShaclOrder shaclOrder = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclOrder.class);
		if (shaclOrder != null) {
			property.setOrder(new BigDecimal(shaclOrder.value()));
			readShaclAnnotations = true;
		}
		//Non Validating Constraints End

		//String Based Constraints Start
		final ShaclPattern shaclPattern = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclPattern.class);
		if (shaclPattern != null) {
			property.setPattern(shaclPattern.value());
			readShaclAnnotations = true;
		}

		final ShaclUniqueLang shaclUniqueLang = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclUniqueLang.class);
		if (shaclUniqueLang != null) {
			property.setUniqueLang(shaclUniqueLang.value());
			readShaclAnnotations = true;
		}

		final ShaclLanguageIn shaclLanguageIn = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclLanguageIn.class);
		if (shaclLanguageIn != null) {
			property.setLanguageIn(shaclLanguageIn.value());
			readShaclAnnotations = true;
		}

		final ShaclMinLength shaclMinLength = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclMinLength.class);
		if (shaclMinLength != null) {
			property.setMinLength(new BigInteger(String.valueOf(shaclMinLength.value())));
			readShaclAnnotations = true;
		}

		final ShaclMaxLength shaclMaxLength = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclMaxLength.class);
		if (shaclMaxLength != null) {
			property.setMaxLength(new BigInteger(String.valueOf(shaclMaxLength.value())));
			readShaclAnnotations = true;
		}
		//String Based Constraints End

		//Other Constraint Components
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
			readShaclAnnotations = true;
		}

		final ShaclHasValue shaclHasValue = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclHasValue.class);
		if (shaclHasValue != null) {
			property.setHasValue(new URI(shaclHasValue.value()));
			readShaclAnnotations = true;
		}

		//Property Pair Constraint Components Start
		final ShaclEquals shaclEquals = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclEquals.class);
		if (shaclEquals != null) {
			property.setEquals(new URI(shaclEquals.value()));
			readShaclAnnotations = true;
		}

		final ShaclDisjoint shaclDisjoint = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclDisjoint.class);
		if (shaclDisjoint != null) {
			property.setDisjoint(new URI(shaclDisjoint.value()));
			readShaclAnnotations = true;
		}

		final ShaclLessThan shaclLessThan = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclLessThan.class);
		if (shaclLessThan != null) {
			property.setLessThan(new URI(shaclLessThan.value()));
			readShaclAnnotations = true;
		}

		final ShaclLessThanOrEquals shaclLessThanOrEquals = InheritedMethodAnnotationHelper.getAnnotation(method, ShaclLessThanOrEquals.class);
		if (shaclLessThanOrEquals != null) {
			property.setLessThanOrEquals(new URI(shaclLessThanOrEquals.value()));
			readShaclAnnotations = true;
		}
		return property;
	}
}
