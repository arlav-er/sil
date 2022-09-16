package it.eng.myportal.converter;

import it.eng.myportal.dtos.DeAmbienteSilDTO;
import it.eng.myportal.entity.home.decodifiche.DeAmbienteSilHome;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("deAmbienteSilDTOConverter")
public class DeAmbienteSilDTOConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeAmbienteSilHome deAmbienteSilHome = null;
			try {
				ic = new InitialContext();
				deAmbienteSilHome = (DeAmbienteSilHome) ic.lookup("java:module/DeAmbienteSilHome");
			} catch (NamingException e1) {
				return null;
			}
			return deAmbienteSilHome.findDTOById(Integer.parseInt(value));
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeAmbienteSilDTO) value).getId());
		}
	}
}
