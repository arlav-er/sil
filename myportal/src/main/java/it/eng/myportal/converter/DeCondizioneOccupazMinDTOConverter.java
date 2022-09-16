package it.eng.myportal.converter;

import it.eng.myportal.dtos.DeCondizioneOccupazMinDTO;
import it.eng.myportal.entity.home.decodifiche.DeCondizioneOccupazMinHome;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("deCondizioneOccupazMinDTOConverter")
public class DeCondizioneOccupazMinDTOConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeCondizioneOccupazMinHome deCondizioneOccupazMinHome = null;
			try {
				ic = new InitialContext();
				deCondizioneOccupazMinHome = (DeCondizioneOccupazMinHome) ic
						.lookup("java:module/DeCondizioneOccupazMinHome");
			} catch (NamingException e1) {
				return null;
			}
			DeCondizioneOccupazMinDTO deCondizioneOccupazMinDTO = deCondizioneOccupazMinHome.findDTOById(value);
			return deCondizioneOccupazMinDTO;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeCondizioneOccupazMinDTO) value).getId());
		}
	}
}
