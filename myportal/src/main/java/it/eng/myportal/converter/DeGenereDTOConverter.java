package it.eng.myportal.converter;

import it.eng.myportal.dtos.DeGenereDTO;
import it.eng.myportal.entity.home.decodifiche.DeGenereHome;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("deGenereDTOConverter")
public class DeGenereDTOConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeGenereHome deGenereHome = null;
			try {
				ic = new InitialContext();
				deGenereHome = (DeGenereHome) ic.lookup("java:module/DeGenereHome");
			} catch (NamingException e1) {
				return null;
			}
			DeGenereDTO deGenereDTO = deGenereHome.findDTOById(value);
			return deGenereDTO;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeGenereDTO) value).getId());
		}
	}
}
