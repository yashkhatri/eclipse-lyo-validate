package org.eclipse.lyo.validate;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.text.ParseException;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.jena.rdf.model.Model;
import org.eclipse.lyo.oslc4j.core.exception.OslcCoreApplicationException;
import org.eclipse.lyo.oslc4j.core.model.AbstractResource;
import org.eclipse.lyo.validate.model.ValidationResultModel;
import org.eclipse.lyo.validate.shacl.ShaclShape;

/**
 * The Interface ValidationHelper. "@author Yash Khatri
 * (yash.s.khatri@gmail.com)
 */
public interface ValidationHelper {

	/**
	 * Validate.
	 *
	 * @param dataModel
	 *            the data model
	 * @param shapeModel
	 *            the shape model
	 * @return the validation result model
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 * @throws DatatypeConfigurationException
	 *             the datatype configuration exception
	 * @throws OslcCoreApplicationException
	 *             the oslc core application exception
	 * 
	 *             This method takes JenaModels as parameters, validates the
	 *             dataModel against shapeModel and return the
	 *             ValidationResultModel
	 */
	ValidationResultModel validate(Model dataModel, Model shapeModel) throws IllegalAccessException,
	InvocationTargetException, DatatypeConfigurationException, OslcCoreApplicationException;

	/**
	 * Validate.
	 *
	 * @param dataModel
	 *            the data model
	 * @param shapeClass
	 *            the shape class
	 * @return the validation result model
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws IllegalArgumentException
	 *             the illegal argument exception
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 * @throws DatatypeConfigurationException
	 *             the datatype configuration exception
	 * @throws OslcCoreApplicationException
	 *             the oslc core application exception
	 * 
	 * 
	 * @throws ParseException
	 * @throws URISyntaxException
	 * 
	 *             This method take data as JenaModel and shape as ShaclShape
	 *             instance, creates the JenaModel from the ShaclShape and
	 *             Validate the dataModel against the ShaclShape based on
	 *             targetClass.
	 */
	ValidationResultModel validate(Model dataModel, Class<? extends AbstractResource> clazz)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			DatatypeConfigurationException, OslcCoreApplicationException, URISyntaxException, ParseException;

	/**
	 * Validate generic.
	 *
	 * @param dataModel
	 *            the data model
	 * @param clazz
	 *            the clazz
	 * @return the validation result model
	 * @throws OslcCoreApplicationException
	 *             the oslc core application exception
	 * @throws URISyntaxException
	 *             the URI syntax exception
	 * @throws ParseException
	 *             the parse exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 * @throws DatatypeConfigurationException
	 *             the datatype configuration exception
	 *             
	 *             This method take data as JenaModel and shape as ShaclShape
	 *             instance, creates the JenaModel from the ShaclShape and
	 *             Validate the dataModel against the ShaclShape based on
	 *             targetNodes.
	 * 
	 */
	ValidationResultModel validateGeneric(Model dataModel, Class<? extends AbstractResource> clazz)
			throws OslcCoreApplicationException, URISyntaxException, ParseException, IllegalAccessException,
			InvocationTargetException, DatatypeConfigurationException;

	/**
	 * Validate type.
	 *
	 * @param dataModel
	 *            the data model
	 * @param shapeModel
	 *            the shape model
	 * @param type
	 *            the type
	 * @return the validation result model
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 * @throws DatatypeConfigurationException
	 *             the datatype configuration exception
	 * @throws OslcCoreApplicationException
	 *             the oslc core application exception
	 *
	 *             This method validates the rdf:type of the resources.
	 */
	ValidationResultModel validateType(Model dataModel, String type) throws IllegalAccessException,
	InvocationTargetException, DatatypeConfigurationException, OslcCoreApplicationException; // Re-check

	/**
	 * Validate URI.
	 *
	 * @param dataModel
	 *            the data model
	 * @param shapeModel
	 *            the shape model
	 * @param URI
	 *            the uri
	 * @return the validation result model
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 * @throws DatatypeConfigurationException
	 *             the datatype configuration exception
	 * @throws OslcCoreApplicationException
	 *             the oslc core application exception
	 * 
	 *             This method validates the URI of the resources
	 */
	ValidationResultModel validateURI(Model dataModel, String uriPattern) throws IllegalAccessException,
	InvocationTargetException, DatatypeConfigurationException, OslcCoreApplicationException;

	/**
	 * Validate.
	 *
	 * @param resource
	 *            the resource
	 * @param clazz
	 *            the clazz
	 * @return the validation result model
	 * @throws OslcCoreApplicationException
	 *             the oslc core application exception
	 * @throws URISyntaxException
	 *             the URI syntax exception
	 * @throws ParseException
	 *             the parse exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws IllegalArgumentException
	 *             the illegal argument exception
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 * @throws DatatypeConfigurationException
	 *             the datatype configuration exception
	 * 
	 *             This method takes AbstractResource as parameters, creates the
	 *             JenaModels, validates the dataModel against shapeModel and
	 *             return the ValidationResultModel
	 */
	ValidationResultModel validate(AbstractResource resource, Class<? extends AbstractResource> clazz)
			throws OslcCoreApplicationException, URISyntaxException, ParseException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, DatatypeConfigurationException;

	/**
	 * Creates the shacl shape.
	 *
	 * @param resourceClass
	 *            the resource class
	 * @return the shacl shape
	 * @throws OslcCoreApplicationException
	 *             the oslc core application exception
	 * @throws URISyntaxException
	 *             the URI syntax exception
	 * @throws ParseException
	 *             the parse exception
	 *             
	 *            This method takes abstract resource as a parameter and returns the instance
	 *            of ShaclShape. 
	 *      
	 */
	ShaclShape createShaclShape(Class<? extends AbstractResource> resourceClass)
			throws OslcCoreApplicationException, URISyntaxException, ParseException;

}
