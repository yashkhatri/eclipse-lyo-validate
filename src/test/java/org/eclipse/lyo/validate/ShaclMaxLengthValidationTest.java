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
import junit.framework.Assert;

/**
 * The Class ShaclMaxLengthValidationTest.
 */
public class ShaclMaxLengthValidationTest {

	/** The a resource. */
	AResource aResource;

	/**
	 * Shacl max length negativetest.
	 * 
	 * This test is failing because the allowed max length of the StringProperty is 10 but here it is more than 10
	 */
	@Test
	public void ShaclMaxLengthNegativetest()  {
		
		try {
			aResource =  new AResource(new URI("http://www.sampledomain.org/sam#AResource"));
			//Invalid Value. The max allowed length of String is 10.
			aResource.setAStringProperty("Between two and four");
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
		String expectedError = "sh:maxLengthError";
		Assert.assertEquals(expectedError, actualError );
		Assert.assertEquals(1, result.errors().size());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception should not be thrown");
		}
		
	}
	
	/**
	 * Shacl max length positivetest.
	 */
	@Test
	public void  ShaclMaxLengthPositivetest()  {
		
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
