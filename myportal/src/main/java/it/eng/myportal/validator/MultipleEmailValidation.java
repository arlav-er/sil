package it.eng.myportal.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator(value = "multipleEmailValidation")
public class MultipleEmailValidation implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

		if (value != null) {
			List<String> listEmailAddress = new ArrayList<String>();
			String address = (String) value;
			String[] addrssArray = address.split(";");
			listEmailAddress = Arrays.asList(addrssArray);
			Pattern p = Pattern.compile(
					"([A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+(\\.[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+)*@[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+(\\.[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+)*)?");
			for (int i = 0; i < listEmailAddress.size(); i++) {
				Matcher m = p.matcher(listEmailAddress.get(i).replaceAll(" ", ""));
				boolean matchFound = m.matches();

				if (!matchFound) {
					String message = "L'indirizzo di posta elettronica non è valido = " + listEmailAddress.get(i);
					FacesMessage msg = new FacesMessage(message);
					msg.setSeverity(FacesMessage.SEVERITY_ERROR);
					context.addMessage(component.getClientId(), msg);
					throw new ValidatorException(msg);
				}
			}

		}
	}

}
