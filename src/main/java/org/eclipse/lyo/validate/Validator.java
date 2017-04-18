package org.eclipse.lyo.validate;

import java.lang.reflect.InvocationTargetException;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.jena.rdf.model.Model;
import org.eclipse.lyo.oslc4j.core.exception.OslcCoreApplicationException;
import org.eclipse.lyo.oslc4j.core.model.AbstractResource;

import es.weso.rdf.jena.RDFAsJenaModel;
import es.weso.schema.Result;
import es.weso.schema.Schema;

/**
 * The Interface Validator.
 */
public interface Validator {

	/**
	 * Gets the schema.
	 *
	 * @param fileName the file name
	 * @return the schema
	 * 
	 * This method is used to get the schema from the turtle file. 
	 */
	public Schema getSchema(String fileName);

	/**
	 * Validate.
	 *
	 * @param RDFAsJenaModel The data graph
	 * @param Schema The shapes schema
	 * @return the validation result
	 * 
	 * Validate Method. Takes as parameter RDFAsJenaModel and Schema.
	 * Validates the Data Graph against the Schema and returns result.
	 */
	public Result validate(final RDFAsJenaModel rdf, final Schema schema);

	/**
	 * Validate.
	 *
	 * @param DataModel the Jena data model
	 * @param Shape the shape in form of abstract resource
	 * @return the validation result
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws DatatypeConfigurationException the datatype configuration exception
	 * @throws OslcCoreApplicationException the oslc core application exception
	 * 
	 * This validate method takes parameters as JenaModel and Abstract Resource.
	 * Validates the Data Model against Shape and returns result. 
	 */
	public Result validate(Model dataModel, AbstractResource shape)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			DatatypeConfigurationException, OslcCoreApplicationException;

	/**
	 * Validate.
	 *
	 * @param resourceAsRDFReader the resource as RDF reader
	 * @param shapeAsRDFReader the shape as RDF reader
	 * @return the result
	 */
	public Result validate(RDFAsJenaModel resourceAsRDFReader, RDFAsJenaModel shapeAsRDFReader);

	/**
	 * Validate.
	 *
	 * @param aResource the a resource
	 * @param shape the shape
	 * @return the result
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws DatatypeConfigurationException the datatype configuration exception
	 * @throws OslcCoreApplicationException the oslc core application exception
	 * 
	 * This validate method takes parameters as Abstract Resource and Abstract Resource.
	 * Validates the Data graph against Shape Graph and returns result. 
	 */
	public Result validate(AbstractResource aResource, AbstractResource shape)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			DatatypeConfigurationException, OslcCoreApplicationException;

	/**
	 * Validate.
	 *
	 * @param resourceAsModel the resource as model
	 * @param shapeAsModel the shape as model
	 * @return the result
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws DatatypeConfigurationException the datatype configuration exception
	 * @throws OslcCoreApplicationException the oslc core application exception
	 * 
	 * This validate method takes parameters as JenaModel and JenaModel.
	 * Validates the Data Model against Shapes Model and returns result. 
	 */
	public Result validate(Model resourceAsModel, Model shapeAsModel)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			DatatypeConfigurationException, OslcCoreApplicationException;
}
