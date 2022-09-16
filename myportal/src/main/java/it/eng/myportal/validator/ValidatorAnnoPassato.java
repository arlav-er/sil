package it.eng.myportal.validator;

import it.eng.myportal.utils.ConstantsSingleton;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.math.NumberUtils;

/**
 */
public class ValidatorAnnoPassato implements
		ConstraintValidator<AnnoPassato, Object> {

	private int maxYear;
	private int minYear;

	@Override
	public void initialize(AnnoPassato constraintAnnotation) {
		maxYear = ConstantsSingleton.CURR_YEAR;
		minYear = ConstantsSingleton.MIN_YEAR;
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		if (value == null) {
			// non è un dato obbligatorio, controllo solo se il dato è presente
			return true;
		}
		// potrebbe arrivare in stringa, gestiamo anche quello
		Number anno = null;
		if (value instanceof String) {
			String str = value.toString();
			try {
				anno = NumberUtils.createNumber(str);
			} catch (Exception e) {
				// non è convertibile a numero - falso
				return false;
			}
		} else if (value instanceof Number) {
			anno = (Number) value;
		} else {
			return false;
		}

		if (anno.longValue() < maxYear || anno.longValue() > minYear) {
			return true;
		}
		return false;
	}

}
