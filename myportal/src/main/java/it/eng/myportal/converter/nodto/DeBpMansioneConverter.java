package it.eng.myportal.converter.nodto;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import it.eng.myportal.entity.decodifiche.DeBpMansione;
import it.eng.myportal.entity.home.decodifiche.DeBpMansioneHome;

@FacesConverter("deBpMansioneConverter")
public class DeBpMansioneConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeBpMansioneHome deMansioneHome = null;
			try {
				ic = new InitialContext();
				deMansioneHome = (DeBpMansioneHome) ic.lookup("java:module/DeBpMansioneHome");
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
			return String.valueOf(((DeBpMansione) value).getCodMansione());
		}
	}
}
