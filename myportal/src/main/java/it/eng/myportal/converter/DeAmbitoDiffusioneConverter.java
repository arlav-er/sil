package it.eng.myportal.converter;

import it.eng.myportal.entity.decodifiche.DeAmbitoDiffusione;
import it.eng.myportal.entity.home.decodifiche.DeAmbitoDiffusioneHome;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("deAmbitoDiffusioneConverter")
public class DeAmbitoDiffusioneConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeAmbitoDiffusioneHome deAmbitoDiffusioneHome = null;
			try {
				ic = new InitialContext();
				deAmbitoDiffusioneHome = (DeAmbitoDiffusioneHome) ic.lookup("java:module/DeAmbitoDiffusioneHome");
			} catch (NamingException e1) {
				return null;
			}
			return deAmbitoDiffusioneHome.findById(value);
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeAmbitoDiffusione) value).getCodAmbitoDiffusione());
		}
	}
}
