package it.eng.myportal.converter.nodto;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import it.eng.myportal.entity.decodifiche.DeAreaFormazione;
import it.eng.myportal.entity.home.decodifiche.nodto.DeAreaFormazioneHome;

@FacesConverter("deAreaFormazioneConverter")
public class DeAreaFormazioneConverter implements Converter {
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeAreaFormazioneHome deAreaTitoloHome = null;
			try {
				ic = new InitialContext();
				deAreaTitoloHome = (DeAreaFormazioneHome) ic.lookup("java:module/DeAreaFormazioneHome");
			} catch (NamingException e1) {
				return null;
			}
			return deAreaTitoloHome.findById(value);
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeAreaFormazione) value).getCodAreaFormazione());
		}
	}
}
