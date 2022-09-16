package it.eng.myportal.converter;

import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("deProvinciaDTOConverter")
public class DeProvinciaDTOConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeProvinciaHome deProvinciaHome = null;
			try {
				ic = new InitialContext();
				deProvinciaHome = (DeProvinciaHome) ic.lookup("java:module/DeProvinciaHome");
			} catch (NamingException e1) {
				return null;
			}
			return deProvinciaHome.findDTOById(value);
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeProvinciaDTO) value).getId());
		}
	}
}
