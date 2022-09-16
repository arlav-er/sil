package it.eng.myportal.converter;

import it.eng.myportal.dtos.DeAttivitaMinDTO;
import it.eng.myportal.entity.home.decodifiche.DeAttivitaMinHome;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("deAttivitaMinDTOConverter")
public class DeAttivitaMinDTOConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeAttivitaMinHome deAttivitaMinHome = null;
			try {
				ic = new InitialContext();
				deAttivitaMinHome = (DeAttivitaMinHome) ic.lookup("java:module/DeAttivitaMinHome");
			} catch (NamingException e1) {
				return null;
			}
			DeAttivitaMinDTO deAttivitaMinDTO = deAttivitaMinHome.findDTOById(value);
			return deAttivitaMinDTO;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeAttivitaMinDTO) value).getId());
		}
	}
}
