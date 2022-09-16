package it.eng.myportal.converter;

import it.eng.myportal.dtos.DeStatoFbConvenzioneDTO;
import it.eng.myportal.entity.home.decodifiche.DeStatoFbConvenzioneHome;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("deStatoConvenzioneDTOConverter")
public class DeStatoConvenzioneDTOConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeStatoFbConvenzioneHome deStatoConvenzioneHome = null;
			try {
				ic = new InitialContext();
				deStatoConvenzioneHome = (DeStatoFbConvenzioneHome) ic.lookup("java:module/DeStatoFbConvenzioneHome");
			} catch (NamingException e1) {
				return null;
			}
			DeStatoFbConvenzioneDTO deStatoConvenzioneDTO = deStatoConvenzioneHome.findDTOById(value);
			return deStatoConvenzioneDTO;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeStatoFbConvenzioneDTO) value).getId());
		}
	}
}
