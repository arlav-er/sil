package it.eng.myportal.converter;

import it.eng.myportal.dtos.DeFbTipoTirocinioDTO;
import it.eng.myportal.entity.home.decodifiche.DeFbTipoTirocinioHome;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("deFbTipoTirocinioDTOConverter")
public class DeFbTipoTirocinioDTOConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeFbTipoTirocinioHome deFbTipoTirocinioHome = null;
			try {
				ic = new InitialContext();
				deFbTipoTirocinioHome = (DeFbTipoTirocinioHome) ic.lookup("java:module/DeFbTipoTirocinioHome");
			} catch (NamingException e1) {
				return null;
			}
			DeFbTipoTirocinioDTO deFbTipoTirocinioDTO = deFbTipoTirocinioHome.findDTOById(value);
			return deFbTipoTirocinioDTO;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeFbTipoTirocinioDTO) value).getId());
		}
	}
}
