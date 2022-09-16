/**
 *
 */
package it.eng.myportal.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * @author girotti
 *
 */
@Target({ ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD,
		ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { ValidatorAnnoPassato.class })
@Documented
public @interface AnnoPassato {

	public abstract String message() default "{it.eng.myportal.validator.AnnoPassato.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
