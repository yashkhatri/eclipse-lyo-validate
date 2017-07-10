package org.eclipse.lyo.validate;

import java.math.BigInteger;
import java.net.URI;
import java.util.Date;

import org.apache.jena.rdf.model.Model;
import org.eclipse.lyo.oslc4j.provider.jena.JenaModelHelper;
import org.eclipse.lyo.validate.impl.ValidatorImpl;
import org.eclipse.lyo.validate.shacl.ShaclShape;
import org.eclipse.lyo.validate.shacl.ShaclShapeFactory;
import org.json.JSONObject;
import org.junit.Test;

import es.weso.schema.Result;
import org.junit.Assert;

/**
 * The Class ShaclMinLengthValidationTest.
 * @author Yash Khatri (yash.s.khatri@gmail.com)
 */
public class ShaclMinLengthValidationTest {

	/** The a resource. */
	AResource aResource;

	/**
	 * Shacl min length negativetest.
	 * 
	 * This test will fail becasue the AStringProperty have the constraint set as
	 * ShaclMinLength =  7. The minimum length of the value "Betwee" is 6 therefore invalid. 
	 *
	 * 
	 */
	@Test
	public void ShaclMinLengthNegativetest() {
		
		try {
			aResource =  new AResource(new URI("http://www.sampledomain.org/sam#AResource"));
			//Invalid Value. Length should be at least 7.
			aResource.setAStringProperty("Betwee");
			aResource.setAnotherIntegerProperty(new BigInteger("12"));
			aResource.addASetOfDates(new Date());
		
		Model dataModel =  JenaModelHelper.createJenaModel(new Object[] {aResource});
		ShaclShape shaclShape = ShaclShapeFactory.createShaclShape(AResource.class);
		Model shapeModel =  JenaModelHelper.createJenaModel(new Object[] {shaclShape});
		
		Validator validator =  new ValidatorImpl();
		Result result = validator.validate(dataModel, shapeModel);
		
		JSONObject obj = new JSONObject(result.toJsonString2spaces());
		String actualError =  obj.getJSONArray("details").getJSONObject(0).getString("error").split(" ")[0];
		
		Assert.assertFalse(result.isValid());
		String expectedError = "sh:minLengthError";
		Assert.assertEquals(expectedError, actualError );
		Assert.assertEquals(1, result.errors().size());
		
	} catch (Exception e) {
		e.printStackTrace();
		Assert.fail("Exception should not be thrown");
	}

		
	}
	
	/**
	 * Shacl min length positivetest.
	 *
	 */
	@Test
	public void ShaclMinLengthPositivetest() {
		
		try {
			aResource =  new AResource(new URI("http://www.sampledomain.org/sam#AResource"));
			aResource.setAnotherIntegerProperty(new BigInteger("12"));
			aResource.setAStringProperty("Between");
			aResource.addASetOfDates(new Date());
			
		Model dataModel =  JenaModelHelper.createJenaModel(new Object[] {aResource});
		ShaclShape shaclShape = ShaclShapeFactory.createShaclShape(AResource.class);
		Model shapeModel =  JenaModelHelper.createJenaModel(new Object[] {shaclShape});
		
		Validator validator =  new ValidatorImpl();
		Result result = validator.validate(dataModel, shapeModel);
		
		Assert.assertTrue(result.isValid());
		Assert.assertEquals(0, result.errors().size());
		
	} catch (Exception e) {
		e.printStackTrace();
		Assert.fail("Exception should not be thrown");
	}
	}

}
