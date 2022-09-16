package it.eng.myportal.converter;

import it.eng.myportal.dtos.DeMotivoPermessoDTO;
import it.eng.myportal.entity.home.decodifiche.DeMotivoPermessoHome;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("deMotivoPermessoDTOConverter")
public class DeMotivoPermessoDTOConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeMotivoPermessoHome deMotivoPermessoHome = null;
			try {
				ic = new InitialContext();
				deMotivoPermessoHome = (DeMotivoPermessoHome) ic.lookup("java:module/DeMotivoPermessoHome");
			} catch (NamingException e1) {
				return null;
			}
			return deMotivoPermessoHome.findDTOById(value);
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeMotivoPermessoDTO) value).getId());
		}
	}
}
