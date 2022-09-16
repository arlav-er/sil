package it.eng.myportal.converter;

import it.eng.myportal.dtos.DeFbCategoriaDTO;
import it.eng.myportal.entity.home.decodifiche.DeFbCategoriaHome;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("deFbCategoriaDTOConverter")
public class DeFbCategoriaDTOConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeFbCategoriaHome deFbCategoriaHome = null;
			try {
				ic = new InitialContext();
				deFbCategoriaHome = (DeFbCategoriaHome) ic.lookup("java:module/DeFbCategoriaHome");
			} catch (NamingException e1) {
				return null;
			}
			DeFbCategoriaDTO deFbCategoriaDTO = deFbCategoriaHome.findDTOById(value);
			return deFbCategoriaDTO;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeFbCategoriaDTO) value).getId());
		}
	}
}
