package it.eng.myportal.converter;

import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("deComuneDTOConverter")
public class DeComuneDTOConverter implements Converter {	
	DeComuneHome comuneHome;
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {		
			InitialContext ic;
			DeComuneHome comuneHome = null;
			try {
				ic = new InitialContext();
				comuneHome = (DeComuneHome) ic.lookup("java:module/DeComuneHome");
			} catch (NamingException e1) {				
				return null;
			}						
			DeComuneDTO deComuneDTO = comuneHome.findDTOById(value);			
			return deComuneDTO;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeComuneDTO) value).getId());
		}
	}

}
