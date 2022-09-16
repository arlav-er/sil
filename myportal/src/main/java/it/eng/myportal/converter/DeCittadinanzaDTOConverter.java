package it.eng.myportal.converter;

import it.eng.myportal.dtos.DeCittadinanzaDTO;
import it.eng.myportal.entity.home.decodifiche.DeCittadinanzaHome;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("deCittadinanzaDTOConverter")
public class DeCittadinanzaDTOConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeCittadinanzaHome deCittadinanzaHome = null;
			try {
				ic = new InitialContext();
				deCittadinanzaHome = (DeCittadinanzaHome) ic.lookup("java:module/DeCittadinanzaHome");
			} catch (NamingException e1) {
				return null;
			}
			DeCittadinanzaDTO deCittadinanzaDTO = deCittadinanzaHome.findDTOById(value);
			return deCittadinanzaDTO;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeCittadinanzaDTO) value).getId());
		}
	}

}
