package it.eng.myportal.converter;

import it.eng.myportal.dtos.DeIscrizioneCorsoMinDTO;
import it.eng.myportal.entity.home.decodifiche.DeIscrizioneCorsoMinHome;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("deIscrizioneCorsoMinDTOConverter")
public class DeIscrizioneCorsoMinDTOConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeIscrizioneCorsoMinHome deIscrizioneCorsoMinHome = null;
			try {
				ic = new InitialContext();
				deIscrizioneCorsoMinHome = (DeIscrizioneCorsoMinHome) ic.lookup("java:module/DeIscrizioneCorsoMinHome");
			} catch (NamingException e1) {
				return null;
			}
			DeIscrizioneCorsoMinDTO deIscrizioneCorsoMinDTO = deIscrizioneCorsoMinHome.findDTOById(value);
			return deIscrizioneCorsoMinDTO;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeIscrizioneCorsoMinDTO) value).getId());
		}
	}
}
