package it.eng.sil.myaccount.controller.converters;

import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuoloGruppo;
import it.eng.sil.mycas.model.manager.gestioneprofilo.GpRuoloGruppoEJB;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@FacesConverter("gpRuoloGruppoConverter")
public class GpRuoloGruppoConverter implements Converter {
	protected final Log log = LogFactory.getLog(this.getClass());

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value != null && !value.trim().isEmpty()) {
			InitialContext ic;
			GpRuoloGruppoEJB gpRuoloGruppoEJB = null;
			try {
				ic = new InitialContext();
				gpRuoloGruppoEJB = (GpRuoloGruppoEJB) ic.lookup("java:module/GpRuoloGruppoEJB");
				return gpRuoloGruppoEJB.findById(Integer.parseInt(value));
			} catch (NamingException | MyCasNoResultException | NumberFormatException e) {
				log.warn("Errore " + e.getClass() + " nella string2object del converter di GpRuoloGruppo: "
						+ e.getMessage());
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("") || !(value instanceof GpRuoloGruppo)) {
			return "";
		} else {
			return ((GpRuoloGruppo) value).getIdGpRuoloGruppo().toString();
		}
	}
}
