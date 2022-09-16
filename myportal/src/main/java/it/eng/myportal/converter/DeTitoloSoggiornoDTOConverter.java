package it.eng.myportal.converter;

import it.eng.myportal.dtos.DeTitoloSoggiornoDTO;
import it.eng.myportal.entity.home.decodifiche.DeTitoloSoggiornoHome;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("deTitoloSoggiornoDTOConverter")
public class DeTitoloSoggiornoDTOConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeTitoloSoggiornoHome deTitoloSoggiornoHome = null;
			try {
				ic = new InitialContext();
				deTitoloSoggiornoHome = (DeTitoloSoggiornoHome) ic.lookup("java:module/DeTitoloSoggiornoHome");
			} catch (NamingException e1) {
				return null;
			}
			return deTitoloSoggiornoHome.findDTOById(value);
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeTitoloSoggiornoDTO) value).getId());
		}
	}

}
