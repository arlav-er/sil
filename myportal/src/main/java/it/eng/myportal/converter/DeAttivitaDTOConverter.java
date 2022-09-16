package it.eng.myportal.converter;

import it.eng.myportal.dtos.DeAttivitaDTO;
import it.eng.myportal.entity.home.decodifiche.DeAttivitaHome;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("deAttivitaDTOConverter")
public class DeAttivitaDTOConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeAttivitaHome deAttivitaHome = null;
			try {
				ic = new InitialContext();
				deAttivitaHome = (DeAttivitaHome) ic.lookup("java:module/DeAttivitaHome");
			} catch (NamingException e1) {
				return null;
			}
			DeAttivitaDTO deAttivitaDTO = deAttivitaHome.findDTOById(value);
			return deAttivitaDTO;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeAttivitaDTO) value).getId());
		}
	}
}
