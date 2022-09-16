package it.eng.myportal.converter;

import it.eng.myportal.dtos.DeCpiDTO;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("deCpiDTOConverter")
public class DeCpiDTOConverter implements Converter {

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
			return deCpiHome.findDTOById(value);
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeCpiDTO) value).getId());
		}
	}
}