/**
 *
 */
package it.eng.myportal.validator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author Rodi, Girotti
 *
 */
@Documented
@Constraint(validatedBy = {})
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Pattern(regexp = "(\\+)?([0-9]|\\/|\\s|-)*?", message= "Formato numero telefonico non valido")
@Size(max=30)
public @interface Telefono {

	String message() default "{it.eng.myportal.validator.Telefono.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
