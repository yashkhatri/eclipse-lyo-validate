package org.eclipse.lyo.validate.impl;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shared.PropertyNotFoundException;
import org.apache.log4j.Logger;
import org.eclipse.lyo.oslc4j.core.exception.OslcCoreApplicationException;
import org.eclipse.lyo.oslc4j.core.exception.OslcCoreRelativeURIException;
import org.eclipse.lyo.oslc4j.core.model.AbstractResource;
import org.eclipse.lyo.oslc4j.provider.jena.JenaModelHelper;
import org.eclipse.lyo.validate.ValidationHelper;
import org.eclipse.lyo.validate.Validator;
import org.eclipse.lyo.validate.model.ResourceModel;
import org.eclipse.lyo.validate.model.ValidationResultModel;
import org.eclipse.lyo.validate.shacl.ShaclShape;
import org.eclipse.lyo.validate.shacl.ShaclShapeFactory;

import es.weso.schema.Result;

/**
 * 
 * @author Yash Khatri (yash.s.khatri@gmail.com)
 *
 */

public class ValidationHelperImpl implements ValidationHelper {

	static Logger log = Logger.getLogger(ValidationHelperImpl.class);

	long totalNumberOfResources;
	long invalidCount;
	long validCount;
	List<ResourceModel> validNodes;
	List<ResourceModel> inValidNodes;
	boolean isValid;


	public ValidationHelperImpl() {
		validator = new ValidatorImpl();
		totalNumberOfResources = 0;
		invalidCount = 0;
		validCount = 0;
		validNodes = new ArrayList<ResourceModel>();
		inValidNodes = new ArrayList<ResourceModel>();
		isValid = false;
	}

	private static Validator validator = new ValidatorImpl();


	@Override
	public ValidationResultModel validate(Model dataModel, Model shapeModel) throws IllegalAccessException,
	InvocationTargetException, DatatypeConfigurationException, OslcCoreApplicationException {
		return getValidationResults(dataModel, shapeModel, null, null);
	}


	@Override
	public ValidationResultModel validate(Model dataModel, Class<? extends AbstractResource> clazz)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			DatatypeConfigurationException, OslcCoreApplicationException, URISyntaxException, ParseException {
		ShaclShape shaclShape = createShaclShape(clazz);
		Model shapeModel = JenaModelHelper.createJenaModel(new Object[] { shaclShape });
		return getValidationResults(dataModel, shapeModel, null, null);
	}

	@Override
	public ValidationResultModel validateGeneric(Model dataModel, Class<? extends AbstractResource> clazz)
			throws OslcCoreApplicationException, URISyntaxException, ParseException, IllegalAccessException,
			InvocationTargetException, DatatypeConfigurationException {
		ShaclShape genericShaclShape = createShaclShape(clazz);
		genericShaclShape.setTargetClass(null);
		return getValidationResults(dataModel, null, genericShaclShape, null);
	}

	@Override
	public ValidationResultModel validateURI(Model dataModel, String uriPattern)
			throws IllegalAccessException, InvocationTargetException, DatatypeConfigurationException,
			OslcCoreApplicationException {
		return getValidationResults(dataModel, null, new ShaclShape(), uriPattern);
	}
	
	@Override
	public ValidationResultModel validate(AbstractResource resource, Class<? extends AbstractResource> clazz)
			throws OslcCoreApplicationException, URISyntaxException, ParseException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, DatatypeConfigurationException {
		ShaclShape shaclShape = createShaclShape(clazz);
		Model shapeModel = JenaModelHelper.createJenaModel(new Object[] { shaclShape });
		Model dataModel = JenaModelHelper.createJenaModel(new Object[] { resource });
		return getValidationResults(dataModel, shapeModel, null, null);
	}
	
	@Override
	public ShaclShape createShaclShape(Class<? extends AbstractResource> resourceClass)
			throws OslcCoreApplicationException, URISyntaxException, ParseException {
		return ShaclShapeFactory.createShaclShape(resourceClass);
	}

	private ValidationResultModel getValidationResults(Model dataModel, Model shapeModel,
			ShaclShape genericShaclShape, String uriPattern) throws IllegalAccessException,
	InvocationTargetException, DatatypeConfigurationException, OslcCoreApplicationException {

		ResourceModel resourceModel;

		ResIterator iterator = dataModel.listSubjects();
		Model model = ModelFactory.createDefaultModel();
		// Iterating over the model on reach resource.
		while (iterator.hasNext()) {

			resourceModel = new ResourceModel();

			final Resource resource = iterator.next();
			model.add(resource.listProperties());

			// Setting the graph name as the target node.
			try {
				if (genericShaclShape != null) {
					//NOt validating for blank node.
					shapeModel = setTargetNode(shapeModel, genericShaclShape, model, resource);
				}
			} catch (URISyntaxException | OslcCoreRelativeURIException e) {
				model.remove(resource.listProperties());
				continue;
			}

			if (uriPattern!=null && !uriPattern.isEmpty()) {
				patternValidator(model.getSeq(resource).toString(), uriPattern);
			} else {
				Result validationResult = validator.validate(model, shapeModel);
				if (validationResult.isValid()) {
					isValid = true;
				}
			}

			populateResourceModel(resourceModel, model, resource);

			populateCounts(resourceModel);

			model.remove(resource.listProperties());

			totalNumberOfResources++;
			log.info("Total Number Of Resources " + totalNumberOfResources);

		}

		log.info("Validations Conmpleted; Returning ValidationResultModel");
		return populateValidationModel();

	}

	private void populateCounts(ResourceModel resourceModel) {
		if (isValid) {
			validCount++;
			validNodes.add(resourceModel);
			log.info("Datamodel valid, Added to the parent datamodel.");
		} else {
			invalidCount++;
			inValidNodes.add(resourceModel);
			log.error("Datamodel Invalid");
		}
	}

	private static void populateResourceModel(ResourceModel resourceModel, Model model, final Resource resource) {
		try {
			resourceModel.setTitle(resource.getRequiredProperty(model.getProperty("http://purl.org/dc/terms/title"))
					.getObject().toString());
		} catch (PropertyNotFoundException e) {
			resourceModel.setTitle(resource.getRequiredProperty(model.getProperty("http://purl.org/dc/terms#title"))
					.getObject().toString());
		} catch (Exception e) {
			resourceModel.setTitle("NO TITLE EXISTS");
		}

		try {
			resourceModel.setDescription(
					resource.getRequiredProperty(model.getProperty("http://purl.org/dc/terms/description")).getObject()
					.toString().replaceAll("\u0027", ""));
		} catch (PropertyNotFoundException e) {
			resourceModel.setDescription(
					resource.getRequiredProperty(model.getProperty("http://purl.org/dc/terms#description")).getObject()
					.toString().replaceAll("\u0027", ""));
		} catch (Exception e) {
			resourceModel.setDescription("No Desciption Exists");
		}

		try {
			resourceModel.setURI(model.getSeq(resource).toString());
		} catch (Exception e) {
			resourceModel.setURI("NO URI Exists");
		}
	}

	private ValidationResultModel populateValidationModel() {
		ValidationResultModel validationResultModel = new ValidationResultModel();
		validationResultModel.setInvalidCount(invalidCount);
		validationResultModel.setValidCount(validCount);
		validationResultModel.setInvalidResources(inValidNodes);
		validationResultModel.setValidResources(validNodes);
		validationResultModel.setTotalNumberOfResources(totalNumberOfResources);
		return validationResultModel;
	}

	private static Model setTargetNode(Model shapeModel, ShaclShape genericShaclShape, Model model,
			final Resource resource) throws DatatypeConfigurationException, IllegalAccessException,
	InvocationTargetException, OslcCoreApplicationException, URISyntaxException {
		//System.out.println(new URI(model.getSeq(resource).toString()));
		genericShaclShape.setTargetNode(new URI(model.getSeq(resource).toString()));
		shapeModel = JenaModelHelper.createJenaModel(new Object[] { genericShaclShape });
		return shapeModel;
	}

	private void patternValidator(String stringToValidate, String patternString) {

		try {
			Pattern pattern = Pattern.compile(patternString);
			Matcher matcher = pattern.matcher(stringToValidate);
			if (matcher.matches())
				isValid = true;
			else if (stringToValidate.contains(patternString))
				isValid = true;
		} catch (PatternSyntaxException e) {
			if (stringToValidate.contains(patternString))
				isValid = true;
		}

	}
}
