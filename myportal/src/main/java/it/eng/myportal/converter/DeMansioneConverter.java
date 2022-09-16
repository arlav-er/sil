package it.eng.myportal.converter;

import it.eng.myportal.entity.decodifiche.DeMansione;
import it.eng.myportal.entity.home.decodifiche.DeMansioneHome;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("deMansioneConverter")
public class DeMansioneConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeMansioneHome deMansioneHome = null;
			try {
				ic = new InitialContext();
				deMansioneHome = (DeMansioneHome) ic.lookup("java:module/DeMansioneHome");
			} catch (NamingException e1) {
				return null;
			}
			return deMansioneHome.findById(value);
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeMansione) value).getCodMansione());
		}
	}
}
