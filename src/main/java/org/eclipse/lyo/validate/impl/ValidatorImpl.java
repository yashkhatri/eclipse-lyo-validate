package org.eclipse.lyo.validate.impl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.eclipse.lyo.oslc4j.core.exception.OslcCoreApplicationException;
import org.eclipse.lyo.oslc4j.core.model.AbstractResource;
import org.eclipse.lyo.oslc4j.provider.jena.JenaModelHelper;
import org.eclipse.lyo.validate.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.weso.rdf.PrefixMap;
import es.weso.rdf.jena.RDFAsJenaModel;
import es.weso.schema.Result;
import es.weso.schema.Schema;
import es.weso.schema.Schemas;
import scala.Option;
import scala.util.Try;

public class ValidatorImpl implements Validator {

	public static final Option<String> OPTION_NONE = Option.apply(null);
	private final static Logger LOGGER = LoggerFactory.getLogger(ValidatorImpl.class);

	public static CharSequence streamAsCharSequence(final InputStream inputStream) throws IOException {
		return IOUtils.toString(inputStream, Charset.forName("UTF-8"));
	}

	private static CharSequence load(final String resourcePath, final String msg) throws IOException {
		InputStream schemaStream = ValidatorImpl.class.getResourceAsStream(resourcePath);
		CharSequence streamAsCharSequence;
		try {
			streamAsCharSequence = streamAsCharSequence(schemaStream);
		} catch (IOException e) {
			LOGGER.error(msg, e);
			throw e;
		}
		return streamAsCharSequence;
	}

	@Override
	public Schema getSchema(String fileName) {
		Schema schema = null;
		try {
			CharSequence charSequence = ValidatorImpl.load(fileName, "Error reading schema from stream");
			Try<Schema> schemaTry = Schemas.fromString(charSequence, "TURTLE", "SHACLex", OPTION_NONE);

			if (schemaTry.isSuccess()) {
				schema = schemaTry.get();
			}
		} catch (IOException e) {
			LOGGER.error("Error invoking validator", e);
		}
		return schema;
	}

	@Override
	public Result validate(final RDFAsJenaModel rdf, final Schema schema) {
		PrefixMap nodeMap = rdf.getPrefixMap();
		PrefixMap shapesMap = schema.pm();
		return schema.validate(rdf, "TargetDecls", OPTION_NONE, OPTION_NONE, nodeMap, shapesMap);
	}

	@Override
	public Result validate(Model dataModel, AbstractResource shape)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			DatatypeConfigurationException, OslcCoreApplicationException {
		Model resourceAsModel = JenaModelHelper.createJenaModel(new Object[] { dataModel });
		RDFAsJenaModel resourceAsRDFReader = new RDFAsJenaModel(resourceAsModel);
		Model shapeAsModel = JenaModelHelper.createJenaModel(new Object[] { shape });
		RDFAsJenaModel shapeAsRDFReader = new RDFAsJenaModel(shapeAsModel);
		return validate(resourceAsRDFReader, shapeAsRDFReader);
	}

	@Override
	public Result validate(RDFAsJenaModel resourceAsRDFReader, RDFAsJenaModel shapeAsRDFReader) {
		Schema schema = null;
		Try<Schema> schemaTry = Schemas.fromRDF(shapeAsRDFReader, "SHACLex");
		if (schemaTry.isSuccess()) {
			schema = schemaTry.get();
		}
		return validate(resourceAsRDFReader, schema);
	}

	@Override
	public Result validate(AbstractResource aResource, AbstractResource shape)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			DatatypeConfigurationException, OslcCoreApplicationException {
		Model resourceAsModel = JenaModelHelper.createJenaModel(new Object[] { aResource });
		RDFAsJenaModel resourceAsRDFReader = new RDFAsJenaModel(resourceAsModel);
		Model shapeAsModel = JenaModelHelper.createJenaModel(new Object[] { shape });
		RDFAsJenaModel shapeAsRDFReader = new RDFAsJenaModel(shapeAsModel);
		return validate(resourceAsRDFReader, shapeAsRDFReader);
	}

	@Override
	public Result validate(Model resourceAsModel, Model shapeAsModel)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			DatatypeConfigurationException, OslcCoreApplicationException {
		RDFAsJenaModel resourceAsRDFReader = new RDFAsJenaModel(resourceAsModel);
		RDFAsJenaModel shapeAsRDFReader = new RDFAsJenaModel(shapeAsModel);
		return validate(resourceAsRDFReader, shapeAsRDFReader);
	}
}
