package it.eng.myportal.converter;

import it.eng.myportal.dtos.DeMansioneDTO;
import it.eng.myportal.entity.home.decodifiche.DeMansioneHome;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("deMansioneDTOConverter")
public class DeMansioneDTOConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeMansioneHome deMansioneHome = null;
			try {
				ic = new InitialContext();
				deMansioneHome = (DeMansioneHome) ic.lookup("java:module/DeMansioneHome");
			} catch (NamingException e1) {
				return null;
			}
			DeMansioneDTO deMansioneDTO = deMansioneHome.findDTOById(value);
			return deMansioneDTO;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeMansioneDTO) value).getId());
		}
	}

}
