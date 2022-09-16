package it.eng.sil.myaccount.controller.converters;

import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpFunzione;
import it.eng.sil.mycas.model.manager.gestioneprofilo.GpFunzioneEJB;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@FacesConverter("gpFunzioneConverter")
public class GpFunzioneConverter implements Converter {
	protected final Log log = LogFactory.getLog(this.getClass());

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value != null && !value.trim().isEmpty()) {
			InitialContext ic;
			GpFunzioneEJB gpFunzioneEJB = null;
			try {
				ic = new InitialContext();
				gpFunzioneEJB = (GpFunzioneEJB) ic.lookup("java:module/GpFunzioneEJB");
				return gpFunzioneEJB.findById(Integer.parseInt(value));
			} catch (NamingException | MyCasNoResultException | NumberFormatException e) {
				log.error("Errore " + e.getClass() + " nella string2object nel converter di GpFunzione: "
						+ e.getMessage());
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("") || !(value instanceof GpFunzione)) {
			return "";
		} else {
			return ((GpFunzione) value).getIdGpFunzione().toString();
		}
	}
}
