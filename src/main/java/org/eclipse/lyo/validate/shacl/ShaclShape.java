/**
 * @author Yash Khatri
 * 	       yash.s.khatri@gmail.com
 */


package org.eclipse.lyo.validate.shacl;

import java.net.URI;
import java.util.TreeMap;

import org.eclipse.lyo.oslc4j.core.annotation.OslcDescription;
import org.eclipse.lyo.oslc4j.core.annotation.OslcName;
import org.eclipse.lyo.oslc4j.core.annotation.OslcNamespace;
import org.eclipse.lyo.oslc4j.core.annotation.OslcPropertyDefinition;
import org.eclipse.lyo.oslc4j.core.annotation.OslcRange;
import org.eclipse.lyo.oslc4j.core.annotation.OslcReadOnly;
import org.eclipse.lyo.oslc4j.core.annotation.OslcResourceShape;
import org.eclipse.lyo.oslc4j.core.annotation.OslcTitle;
import org.eclipse.lyo.oslc4j.core.annotation.OslcValueType;
import org.eclipse.lyo.oslc4j.core.model.AbstractResource;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.ValueType;

@OslcNamespace(ShaclConstants.SHACL_CORE_NAMESPACE)
@OslcName("Shape")
@OslcResourceShape(title = "Shacl Resource Shape", describes = ShaclConstants.TYPE_SHACL_SHAPE)
public final class ShaclShape extends AbstractResource {
	private URI targetClass;  //can it be more than 1?
	private URI targetNode;
	private final TreeMap<URI, Property> properties = new TreeMap<URI, Property>();
	private URI isDefinedBy;
	private String label;
	private URI type;

	public ShaclShape() {
		super();
	}

	public ShaclShape(final URI about) {
		super(about);
	}
	
	public void addtargetClass(final URI targetClass) {
		this.targetClass = targetClass;
	}
	
	
	@OslcDescription("Type or types of resource described by this shape")
	@OslcPropertyDefinition(OslcConstants.RDF_NAMESPACE + "type")
	@OslcReadOnly
	@OslcTitle("RDF Type")
	public URI getType() {
		return type;
	}

	public void setType(URI type) {
		this.type = type;
	}

	@OslcDescription("Type or types of resource described by this shape")
	@OslcPropertyDefinition(ShaclConstants.SHACL_CORE_NAMESPACE + "targetClass")
	@OslcReadOnly
	@OslcTitle("targetClass")
	public URI getTargetClass() {
		return targetClass;
	}
	
	public void setTargetClass(final URI targetClass) {
		if (targetClass != null) {
			this.targetClass = targetClass;
		}
	}
	public void addProperty(final Property property) {
		this.properties.put(property.getPredicate(), property);
	}
	
	public Property getShaclProperty(URI definition) {
		return properties.get(definition);
	}

	@OslcDescription("The properties that are allowed or required by this shape")
	@OslcName("property")
	@OslcPropertyDefinition(ShaclConstants.SHACL_CORE_NAMESPACE + "property")
	@OslcRange(ShaclConstants.TYPE_PROPERTY)
	@OslcReadOnly
	@OslcTitle("Properties")
	@OslcValueType(ValueType.LocalResource)
	public Property[] getShaclProperties() {
		return properties.values().toArray(new Property[properties.size()]);
	}

	@OslcDescription("Specified Is Defined By")
	@OslcPropertyDefinition(OslcConstants.RDFS_NAMESPACE + "isDefinedBy")
	@OslcTitle("isDefinedBy")
	public URI getIsDefinedBy() {
		return isDefinedBy;
	}

	@OslcDescription("Specified Label")
	@OslcPropertyDefinition(OslcConstants.RDFS_NAMESPACE + "label")
	@OslcTitle("label")
	public String getLabel() {
		return label;
	}
	
	@OslcDescription("Focus Node")
	@OslcPropertyDefinition(ShaclConstants.SHACL_CORE_NAMESPACE + "targetNode")
	@OslcReadOnly
	@OslcTitle("targetNode")
	public URI getTargetNode() {
		return targetNode;
	}
	
	public void setTargetNode(final URI targetNode) {
		if (targetNode != null) {
			this.targetNode = targetNode;
		}
	}

	public void setIsDefinedBy(URI isDefinedBy) {
		this.isDefinedBy = isDefinedBy;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setShaclProperties(final Property[] properties) {
		this.properties.clear();
		if (properties != null) {
			for(Property prop :properties) {
				this.properties.put(prop.getPredicate(), prop);
			}
		}
	}

	@Override
	public String toString() {
		return "Shape [targetClass=" + targetClass + ", properties=" + properties + "]";
	}

}
