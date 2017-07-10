package org.eclipse.lyo.validate.constants;

import java.net.URI;

import org.eclipse.lyo.oslc4j.core.model.OslcConstants;

/**
 * The Enum DataType.
 * 
 * This enums contains the data types that are supported by SHACL
 * 
 * @author Yash Khatri (yash.s.khatri@gmail.com) 
 */
public enum DataType {
	
	/** The Boolean. */
	Boolean(OslcConstants.XML_NAMESPACE + "boolean"),
	
	/** The Date time. */
	DateTime(OslcConstants.XML_NAMESPACE + "dateTime"),
	
	/** The Date. */
	Date(OslcConstants.XML_NAMESPACE + "date"),
	
	/** The Decimal. */
	Decimal(OslcConstants.XML_NAMESPACE + "decimal"),
	
	/** The Double. */
	Double(OslcConstants.XML_NAMESPACE + "double"),
	
	/** The Float. */
	Float(OslcConstants.XML_NAMESPACE + "float"),
	
	/** The Integer. */
	Integer(OslcConstants.XML_NAMESPACE + "integer"),
	
	/** The String. */
	String(OslcConstants.XML_NAMESPACE + "string"),
	
	/** The XML literal. */
	XMLLiteral(OslcConstants.RDF_NAMESPACE + "XMLLiteral"),
	
	/** The uri. */
	URI(OslcConstants.XML_NAMESPACE + "anyURI");
	

	/** The uri. */
	private final String uri;

	/**
	 * Instantiates a new data type.
	 *
	 * @param uri the uri
	 */
	DataType(final String uri) {
		this.uri = uri;
	}

	@Override
	public String toString() {
		return uri;
	}

	/**
	 * From string.
	 *
	 * @param string the string
	 * @return the data type
	 */
	public static DataType fromString(final String string) {
		final DataType[] values = DataType.values();
		for (final DataType value : values) {
			if (value.uri.equals(string)) {
				return value;
			}
		}
		return null;
	}

	/**
	 * From URI.
	 *
	 * @param uri the uri
	 * @return the data type
	 */
	public static DataType fromURI(final URI uri) {
		return fromString(uri.toString());
	}
}
