package it.eng.myportal.converter;

import it.eng.myportal.dtos.DePosizioneProfessionaleMinDTO;
import it.eng.myportal.entity.home.decodifiche.DePosizioneProfessionaleMinHome;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("dePosizioneProfessionaleMinDTOConverter")
public class DePosizioneProfessionaleMinDTOConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DePosizioneProfessionaleMinHome dePosizioneProfessionaleMinHome = null;
			try {
				ic = new InitialContext();
				dePosizioneProfessionaleMinHome = (DePosizioneProfessionaleMinHome) ic
						.lookup("java:module/DePosizioneProfessionaleMinHome");
			} catch (NamingException e1) {
				return null;
			}
			DePosizioneProfessionaleMinDTO dePosizioneProfessionaleMinDTO = dePosizioneProfessionaleMinHome
					.findDTOById(value);
			return dePosizioneProfessionaleMinDTO;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DePosizioneProfessionaleMinDTO) value).getId());
		}
	}
}
