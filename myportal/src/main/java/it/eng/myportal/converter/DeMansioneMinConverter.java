package it.eng.myportal.converter;

import it.eng.myportal.entity.decodifiche.min.DeMansioneMin;
import it.eng.myportal.entity.home.decodifiche.DeMansioneMinHome;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("deMansioneMinConverter")
public class DeMansioneMinConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeMansioneMinHome deMansioneMinHome = null;
			try {
				ic = new InitialContext();
				deMansioneMinHome = (DeMansioneMinHome) ic.lookup("java:module/DeMansioneMinHome");
			} catch (NamingException e1) {
				return null;
			}
			return deMansioneMinHome.findById(value);
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeMansioneMin) value).getCodMansioneMin());
		}
	}
}