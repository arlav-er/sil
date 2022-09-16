package it.eng.myportal.converter;

import it.eng.myportal.dtos.DeStatoFbChecklistDTO;
import it.eng.myportal.entity.home.decodifiche.DeStatoFbChecklistHome;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("deStatoFbChecklistDTOConverter")
public class DeStatoFbChecklistDTOConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeStatoFbChecklistHome deStatoFbChecklistHome = null;
			try {
				ic = new InitialContext();
				deStatoFbChecklistHome = (DeStatoFbChecklistHome) ic.lookup("java:module/DeStatoFbChecklistHome");
			} catch (NamingException e1) {
				return null;
			}
			DeStatoFbChecklistDTO deStatoFbChecklistDTO = deStatoFbChecklistHome.findDTOById(value);
			return deStatoFbChecklistDTO;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeStatoFbChecklistDTO) value).getId());
		}
	}
}
