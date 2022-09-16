package it.eng.myportal.converter;

import it.eng.myportal.dtos.DeTitoloDTO;
import it.eng.myportal.entity.home.decodifiche.DeTitoloHome;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("deTitoloDTOConverter")
public class DeTitoloDTOConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeTitoloHome deTitoloHome = null;
			try {
				ic = new InitialContext();
				deTitoloHome = (DeTitoloHome) ic.lookup("java:module/DeTitoloHome");
			} catch (NamingException e1) {
				return null;
			}
			return deTitoloHome.findDTOById(value);
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeTitoloDTO) value).getId());
		}
	}
}
