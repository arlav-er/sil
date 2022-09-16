package it.eng.myportal.converter;

import it.eng.myportal.dtos.DeTipoFbConvenzioneDTO;
import it.eng.myportal.entity.home.decodifiche.DeTipoFbConvenzioneHome;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("deTipoConvenzioneDTOConverter")
public class DeTipoConvenzioneDTOConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeTipoFbConvenzioneHome deTipoConvenzioneHome = null;
			try {
				ic = new InitialContext();
				deTipoConvenzioneHome = (DeTipoFbConvenzioneHome) ic.lookup("java:module/DeTipoFbConvenzioneHome");
			} catch (NamingException e1) {
				return null;
			}
			DeTipoFbConvenzioneDTO deTipoConvenzioneDTO = deTipoConvenzioneHome.findDTOById(value);
			return deTipoConvenzioneDTO;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeTipoFbConvenzioneDTO) value).getId());
		}
	}
}
