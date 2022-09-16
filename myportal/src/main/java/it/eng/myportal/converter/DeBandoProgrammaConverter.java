package it.eng.myportal.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import it.eng.myportal.dtos.DeBandoProgrammaDTO;
import it.eng.myportal.entity.home.decodifiche.DeBandoProgrammaHome;

@FacesConverter("deBandoProgrammaConverter")
public class DeBandoProgrammaConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeBandoProgrammaHome deBandoProgrammaHome = null;
			try {
				ic = new InitialContext();
				deBandoProgrammaHome = (DeBandoProgrammaHome) ic.lookup("java:module/DeBandoProgrammaHome");
			} catch (NamingException e1) {
				return null;
			}
			DeBandoProgrammaDTO ret = deBandoProgrammaHome.toDTO(deBandoProgrammaHome.findById(value));
			return ret;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return ((DeBandoProgrammaDTO) value).getId();
		}
	}

	
}
