package org.eclipse.lyo.validate.shacl;

public interface ShaclConstants {


	public static final String SHACL_CORE_NAMESPACE		= "http://www.w3.org/ns/shacl#";
	public static final String SHACL_CORE_NAMESPACE_PREFIX = "sh";
	
	public static final String EXAMPLEORG_CORE_NAMESPACE		= "http://example.org/";
	public static final String EXAMPLEORG_CORE_NAMESPACE_PREFIX = "ex";

	public static final String XML_NAMESPACE			= "http://www.w3.org/2001/XMLSchema#";
	public static final String XML_NAMESPACE_PREFIX	    = "xsd";

	//check if below properties are needed or not
	public static final String TYPE_ALLOWED_VALUES			 = SHACL_CORE_NAMESPACE + "AllowedValues";
	public static final String TYPE_PREFIX_DEFINITION		 = SHACL_CORE_NAMESPACE + "PrefixDefinition";
	public static final String TYPE_PROPERTY				 = SHACL_CORE_NAMESPACE + "Property";
	public static final String TYPE_SHACL_SHAPE			     = SHACL_CORE_NAMESPACE + "Shape";

	public static final String PATH_SHACL_SHAPE = "shaclShape";

	public static final String ANYURI = XML_NAMESPACE + "anyURI";
}
