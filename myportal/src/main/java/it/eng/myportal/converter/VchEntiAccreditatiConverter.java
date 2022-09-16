package it.eng.myportal.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import it.eng.myportal.dtos.VchEntiAccreditatiDTO;
import it.eng.myportal.entity.home.VchEntiAccreditatiHome;

@FacesConverter("vchEntiAccreditatiConverter")
public class VchEntiAccreditatiConverter implements Converter {
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			VchEntiAccreditatiHome vchEntiAccreditatiHome = null;
			try {
				ic = new InitialContext();
				vchEntiAccreditatiHome = (VchEntiAccreditatiHome) ic.lookup("java:module/VchEntiAccreditatiHome");
			} catch (NamingException e1) {
				return null;
			}
			VchEntiAccreditatiDTO ret = vchEntiAccreditatiHome.toDTO(vchEntiAccreditatiHome.findById(Integer.parseInt(value)));
			return ret;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((VchEntiAccreditatiDTO) value).getId());
		}
	}
}
