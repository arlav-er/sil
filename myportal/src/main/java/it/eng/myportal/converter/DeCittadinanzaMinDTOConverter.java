package it.eng.myportal.converter;

import it.eng.myportal.dtos.DeCittadinanzaMinDTO;
import it.eng.myportal.entity.home.decodifiche.DeCittadinanzaMinHome;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("deCittadinanzaMinDTOConverter")
public class DeCittadinanzaMinDTOConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeCittadinanzaMinHome deCittadinanzaMinHome = null;
			try {
				ic = new InitialContext();
				deCittadinanzaMinHome = (DeCittadinanzaMinHome) ic.lookup("java:module/DeCittadinanzaMinHome");
			} catch (NamingException e1) {
				return null;
			}
			DeCittadinanzaMinDTO deCittadinanzaMinDTO = deCittadinanzaMinHome.findDTOById(value);
			return deCittadinanzaMinDTO;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeCittadinanzaMinDTO) value).getId());
		}
	}

}
