package org.eclipse.lyo.validate.shacl.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.eclipse.lyo.validate.constants.DataType;

/**
 * 
 * @author Yash Khatri (yash.s.khatri@gmail.com)
 *
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ShaclIn {

	DataType dataType();
	String[] value();
}
