package it.eng.myportal.converter;

import it.eng.myportal.dtos.DeQualificaSrqDTO;
import it.eng.myportal.entity.home.decodifiche.DeQualificaSrqHome;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("deQualificaSrqDTOConverter")
public class DeQualificaSrqDTOConverter implements Converter {
	
	DeQualificaSrqHome deQualificaSrqHome;
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {		
			InitialContext ic;			
			try {
				ic = new InitialContext();
				deQualificaSrqHome = (DeQualificaSrqHome) ic.lookup("java:module/DeQualificaSrqHome");
			} catch (NamingException e1) {				
				return null;
			}						
			DeQualificaSrqDTO deQualificaSrqDTO = deQualificaSrqHome.findDTOById(value);			
			return deQualificaSrqDTO;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeQualificaSrqDTO) value).getId());
		}
	}
}
