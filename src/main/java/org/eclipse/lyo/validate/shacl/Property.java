/**
 * @author Yash Khatri
 * 	       yash.s.khatri@gmail.com
 */

package org.eclipse.lyo.validate.shacl;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.eclipse.lyo.oslc4j.core.annotation.OslcAllowedValue;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDescription;
import org.eclipse.lyo.oslc4j.core.annotation.OslcName;
import org.eclipse.lyo.oslc4j.core.annotation.OslcNamespace;
import org.eclipse.lyo.oslc4j.core.annotation.OslcOccurs;
import org.eclipse.lyo.oslc4j.core.annotation.OslcPropertyDefinition;
import org.eclipse.lyo.oslc4j.core.annotation.OslcRdfCollectionType;
import org.eclipse.lyo.oslc4j.core.annotation.OslcReadOnly;
import org.eclipse.lyo.oslc4j.core.annotation.OslcResourceShape;
import org.eclipse.lyo.oslc4j.core.annotation.OslcTitle;
import org.eclipse.lyo.oslc4j.core.annotation.OslcValueType;
import org.eclipse.lyo.oslc4j.core.model.AbstractResource;
import org.eclipse.lyo.oslc4j.core.model.Occurs;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.ValueType;
import org.eclipse.lyo.validate.constants.DataType;
import org.eclipse.lyo.validate.shacl.annotations.ShaclDataType;


@OslcNamespace(ShaclConstants.SHACL_CORE_NAMESPACE)
@OslcName("property")
@OslcResourceShape(title = "SHACL Property Resource Shape", describes = ShaclConstants.TYPE_PROPERTY)
public final class Property extends AbstractResource {

	private URI predicate;
	
	//Value Type Constraints
	private URI classType;
	private DataType dataType;
	private URI nodeKind;
	
	//Target Constraints
	private URI targetNode;	
	
	//Cardinality Constraints
	private BigInteger minCount;
	private BigInteger maxCount;
	
	//Value Range Constraints
	private BigInteger minExclusive;
	private BigInteger maxExclusive;
	private BigInteger minInclusive;
	private BigInteger maxInclusive;
	
	//String Based Constraints
	private BigInteger minLength;
	private BigInteger maxLength;
	private String pattern;
	private String[] languageIn;
	private Boolean uniqueLang;
	
	//Values Based Constraints
	private  Object[] in;
	
	//Non Validating Property Shape Characteristics.
	private String name;
	private String description;
	
	public Property() {
		super();
	}

	public Property(final URI predicate,
			final DataType dataType, final BigInteger minCount, final BigInteger maxCount) {
		this();

		this.predicate = predicate;
		this.dataType = dataType;
		this.minCount = minCount;
		this.maxCount = maxCount;
	}

	@OslcDescription("predicate of a property")
	@OslcPropertyDefinition(ShaclConstants.SHACL_CORE_NAMESPACE + "predicate")
	@OslcReadOnly
	@OslcName("predicate")
	public URI getPredicate() {
		return predicate;
	}

	public void setPredicate(URI predicate) {
		this.predicate = predicate;
	}

	@OslcAllowedValue({OslcConstants.XML_NAMESPACE + "boolean",
		OslcConstants.XML_NAMESPACE + "dateTime",
		OslcConstants.XML_NAMESPACE + "decimal",
		OslcConstants.XML_NAMESPACE + "double",
		OslcConstants.XML_NAMESPACE + "float",
		OslcConstants.XML_NAMESPACE + "integer",
		OslcConstants.XML_NAMESPACE + "string",
		OslcConstants.RDF_NAMESPACE + "XMLLiteral"})
	@OslcDescription("See list of allowed values for sh:datatype")
	@OslcOccurs(Occurs.ExactlyOne)
	@OslcPropertyDefinition(ShaclConstants.SHACL_CORE_NAMESPACE + "datatype")
	@OslcReadOnly
	@OslcName("datatype")
	@OslcTitle("Data Type")
	public URI getDataType() {
		if (dataType != null) {
			try {
				return new URI(dataType.toString());
			} catch (final URISyntaxException exception) {
				// This should never happen since we control the possible values of the ValueType enum.
				throw new RuntimeException(exception);
			}
		}

		return null;
	}

	@OslcDescription("Specifies the description")
	@OslcPropertyDefinition(ShaclConstants.SHACL_CORE_NAMESPACE + "description")
	@OslcTitle("Description")
	@OslcValueType(ValueType.String)
	@OslcName("description")
	public String getDescription() {
		return description;
	}

	@OslcDescription("target node")
	@OslcPropertyDefinition(ShaclConstants.SHACL_CORE_NAMESPACE + "targetNode")
	@OslcReadOnly
	@OslcName("targetNode")
	@OslcValueType(ValueType.String)
	@ShaclDataType(DataType.URI)
	public URI getTargetNode() {
		return targetNode;
	}
	
	@OslcDescription("Specifies the name")
	@OslcPropertyDefinition(ShaclConstants.SHACL_CORE_NAMESPACE + "name")
	@OslcTitle("Name")
	@OslcValueType(ValueType.String)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OslcDescription("Specifies the min count")
	@OslcPropertyDefinition(ShaclConstants.SHACL_CORE_NAMESPACE + "minCount")
	@OslcReadOnly
	@OslcTitle("Min Count")
	@OslcValueType(ValueType.Integer)
	public BigInteger getMinCount() {
		return minCount;
	}

	@OslcDescription("Specifies the max count")
	@OslcPropertyDefinition(ShaclConstants.SHACL_CORE_NAMESPACE + "maxCount")
	@OslcReadOnly
	@OslcTitle("Max Count")
	@OslcValueType(ValueType.Integer)
	public BigInteger getMaxCount() {
		return maxCount;
	}

	@OslcDescription("Specifies the range: Min Exclusive")
	@OslcPropertyDefinition(ShaclConstants.SHACL_CORE_NAMESPACE + "minExclusive")
	@OslcValueType(ValueType.String)
	@OslcTitle("Range Min Exclusive")
	public BigInteger getMinExclusive() {
		return minExclusive;
	}

	@OslcDescription("Specifies the range: Max Exclusive")
	@OslcPropertyDefinition(ShaclConstants.SHACL_CORE_NAMESPACE + "maxExclusive")
	@OslcValueType(ValueType.String)
	@OslcTitle("Range Max Exclusive")
	public BigInteger getMaxExclusive() {
		return maxExclusive;
	}

	@OslcDescription("Specifies the range: Min Inclusive")
	@OslcPropertyDefinition(ShaclConstants.SHACL_CORE_NAMESPACE + "minInclusive")
	@OslcValueType(ValueType.String)
	@OslcTitle("Range Min Inclusive")
	public BigInteger getMinInclusive() {
		return minInclusive;
	}

	@OslcDescription("Specifies the range: Max Inclusive")
	@OslcPropertyDefinition(ShaclConstants.SHACL_CORE_NAMESPACE + "maxInclusive")
	@OslcValueType(ValueType.String)
	@OslcTitle("Range Max Inclusive")
	public BigInteger getMaxInclusive() {
		return maxInclusive;
	}

	@OslcDescription("Specifies the minimum string length of each value node that satisfies the condition.")
	@OslcPropertyDefinition(ShaclConstants.SHACL_CORE_NAMESPACE + "minLength")
	@OslcValueType(ValueType.Integer)
	@OslcTitle("Minimum Length")
	public BigInteger getMinLength() {
		return minLength;
	}

	@OslcDescription("Specifies the maximum string length of each value node that satisfies the condition.")
	@OslcPropertyDefinition(ShaclConstants.SHACL_CORE_NAMESPACE + "maxLength")
	@OslcValueType(ValueType.Integer)
	@OslcTitle("Maximum Length")
	public BigInteger getMaxLength() {
		return maxLength;
	}

	@OslcDescription("Specifies a regular expression that each value node matches to satisfy the condition.")
	@OslcPropertyDefinition(ShaclConstants.SHACL_CORE_NAMESPACE + "pattern")
	@OslcValueType(ValueType.String)
	@OslcTitle("Pattern")
	public String getPattern() {
		return pattern;
	}

	@OslcDescription("Specifies the allowed language tags for each value node limited by a given list of language tags.")
	@OslcPropertyDefinition(ShaclConstants.SHACL_CORE_NAMESPACE + "languageIn")
	@OslcValueType(ValueType.String)
	@OslcTitle("LanguageIn")
	public String[] getLanguageIn() {
		return languageIn;
	}

	@OslcDescription("Specifies that no pair of value nodes may use the same language tag if set true.")
	@OslcPropertyDefinition(ShaclConstants.SHACL_CORE_NAMESPACE + "uniqueLang")
	@OslcValueType(ValueType.Boolean)
	@OslcTitle("UniqueLang")
	public Boolean getUniqueLang() {
		return uniqueLang;
	}

	@OslcDescription("Specifies the Class of a node")
	@OslcPropertyDefinition(ShaclConstants.SHACL_CORE_NAMESPACE + "class")
	@OslcTitle("Class")
	@OslcName("class")
	public URI getClassType() {
		return classType;
	}

	@OslcDescription("Specifies the node kind. Values can be: sh:BlankNode, sh:IRI, sh:Literal sh:BlankNodeOrIRI, sh:BlankNodeOrLiteral and sh:IRIOrLiteral")
	@OslcPropertyDefinition(ShaclConstants.SHACL_CORE_NAMESPACE + "nodeKind")
	@OslcTitle("Node Kind")
	public URI getNodeKind() {
		return nodeKind;
	}

	@OslcDescription("specifies the condition that each value node is a member of a provided SHACL list.")
	@OslcPropertyDefinition(ShaclConstants.SHACL_CORE_NAMESPACE + "in")
	@OslcTitle("In")
	@OslcRdfCollectionType
	public Object[] getIn() {
		return in;
	}

	public void setIn(Object[] in) {
		this.in = in;
	}

	public void setNodeKind(URI nodeKind) {
		this.nodeKind = nodeKind;
	}

	public void setClassType(URI classType) {
		this.classType = classType;
	}

	public void setMinExclusive(BigInteger minExclusive) {
		this.minExclusive = minExclusive;
	}

	public void setMaxExclusive(BigInteger maxExclusive) {
		this.maxExclusive = maxExclusive;
	}

	public void setMinInclusive(BigInteger minInclusive) {
		this.minInclusive = minInclusive;
	}

	public void setMaxInclusive(BigInteger maxInclusive) {
		this.maxInclusive = maxInclusive;
	}

	public void setMinLength(BigInteger minLength) {
		this.minLength = minLength;
	}

	public void setMaxLength(BigInteger maxLength) {
		this.maxLength = maxLength;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public void setLanguageIn(String[] languageIn) {
		this.languageIn = languageIn;
	}

	public void setUniqueLang(Boolean uniqueLang) {
		this.uniqueLang = uniqueLang;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
	
	public void setMinCount(BigInteger minCount) {
		this.minCount = minCount;
	}

	public void setMaxCount(BigInteger maxCount) {
		this.maxCount = maxCount;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setTargetNode(URI targetNode) {
		this.targetNode = targetNode;
	}

	@Override
	public String toString() {
		return "Property [predicate=" + predicate + ", classType=" + classType + ", dataType=" + dataType
				+ ", nodeKind=" + nodeKind + ", minCount=" + minCount + ", maxCount=" + maxCount + ", minExclusive="
				+ minExclusive + ", maxExclusive=" + maxExclusive + ", minInclusive=" + minInclusive + ", maxInclusive="
				+ maxInclusive + ", minLength=" + minLength + ", maxLength=" + maxLength + ", pattern=" + pattern
				+ ", languageIn=" + languageIn + ", uniqueLang=" + uniqueLang + ", in=" + Arrays.toString(in)
				+ ", name=" + name + ", description=" + description + "]";
	}

}
