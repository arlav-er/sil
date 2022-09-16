package it.eng.myportal.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.convert.DateTimeConverter;
import javax.faces.convert.FacesConverter;

@FacesConverter("dateValidators")
public class DateValidators extends DateTimeConverter {

	public DateValidators() {
		setPattern("dd/MM/yyyy");
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value != null && value.length() != getPattern().length()) {
			String message = "La data deve essere nel formato gg/mm/aaaa";
			FacesMessage msg = new FacesMessage(message);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			context.addMessage(component.getClientId(), msg);
			throw new ConverterException(msg);
		}

		return super.getAsObject(context, component, value);
	}
}
