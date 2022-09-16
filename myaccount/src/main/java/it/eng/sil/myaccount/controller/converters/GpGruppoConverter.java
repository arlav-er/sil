package it.eng.sil.myaccount.controller.converters;

import it.eng.sil.myaccount.model.ejb.stateless.gestioneprofilo.GpGruppoMyAccountEJB;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpGruppo;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@FacesConverter("gpGruppoConverter")
public class GpGruppoConverter implements Converter {
	protected final Log log = LogFactory.getLog(this.getClass());

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value != null && !value.trim().isEmpty()) {
			InitialContext ic;
			GpGruppoMyAccountEJB gpGruppoEJB = null;
			try {
				ic = new InitialContext();
				gpGruppoEJB = (GpGruppoMyAccountEJB) ic.lookup("java:module/GpGruppoMyAccountEJB");
				return gpGruppoEJB.fullLookup(Integer.parseInt(value));
			} catch (NamingException | MyCasNoResultException | NumberFormatException e) {
				log.error("Errore " + e.getClass() + " nella string2object nel converter di GpGruppo: "
						+ e.getMessage());
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("") || !(value instanceof GpGruppo)) {
			return "";
		} else {
			return ((GpGruppo) value).getIdGpGruppo().toString();
		}
	}
}
