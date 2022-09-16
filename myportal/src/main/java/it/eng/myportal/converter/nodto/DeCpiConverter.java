package it.eng.myportal.converter.nodto;

import it.eng.myportal.entity.decodifiche.DeCpi;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("deCpiConverter")
public class DeCpiConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeCpiHome deCpiHome = null;
			try {
				ic = new InitialContext();
				deCpiHome = (DeCpiHome) ic.lookup("java:module/DeCpiHome");
			} catch (NamingException e1) {
				return null;
			}
			DeCpi deCpi = deCpiHome.findById(value);
			return deCpi;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeCpi) value).getCodCpi());
		}
	}
}
