package it.eng.myportal.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import it.eng.myportal.dtos.DeNaturaGiuridicaDTO;
import it.eng.myportal.entity.home.decodifiche.DeNaturaGiuridicaHome;

@FacesConverter("deNaturaGiuridicaDTOConverter")
public class DeNaturaGiuridicaDTOConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeNaturaGiuridicaHome deNaturaGiuridicaHome = null;
			try {
				ic = new InitialContext();
				deNaturaGiuridicaHome = (DeNaturaGiuridicaHome) ic.lookup("java:module/DeNaturaGiuridicaHome");
			} catch (NamingException e1) {
				return null;
			}
			DeNaturaGiuridicaDTO deNaturaGiuridicaDTO = deNaturaGiuridicaHome.findDTOById(value);
			return deNaturaGiuridicaDTO;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeNaturaGiuridicaDTO) value).getId());
		}
	}

}
