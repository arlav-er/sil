package it.eng.sil.myaccount.controller.converters;

import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpRuoloMyAccountEJB;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuolo;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@FacesConverter("gpRuoloConverter")
public class GpRuoloConverter implements Converter {
	protected final Log log = LogFactory.getLog(this.getClass());

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value != null && !value.trim().isEmpty()) {
			InitialContext ic;
			GpRuoloMyAccountEJB gpRuoloEJB = null;
			try {
				ic = new InitialContext();
				gpRuoloEJB = (GpRuoloMyAccountEJB) ic.lookup("java:module/GpRuoloMyAccountEJB");
				return gpRuoloEJB.findById(Integer.parseInt(value));
			} catch (NamingException | MyCasNoResultException | NumberFormatException e) {
				log.warn("Errore " + e.getClass() + " nella string2object del converter di GpRuolo: " + e.getMessage());
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("") || !(value instanceof GpRuolo)) {
			return "";
		} else {
			return ((GpRuolo) value).getIdGpRuolo().toString();
		}
	}

}
