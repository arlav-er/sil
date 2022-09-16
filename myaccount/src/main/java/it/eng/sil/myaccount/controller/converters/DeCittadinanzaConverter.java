package it.eng.sil.myaccount.controller.converters;

import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.decodifiche.DeCittadinanza;
import it.eng.sil.mycas.model.manager.decodifiche.DeCittadinanzaEJB;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@FacesConverter("deCittadinanzaConverter")
public class DeCittadinanzaConverter implements Converter {
	protected final Log log = LogFactory.getLog(this.getClass());

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeCittadinanzaEJB deCittadinanzaEJB = null;
			try {
				ic = new InitialContext();
				deCittadinanzaEJB = (DeCittadinanzaEJB) ic.lookup("java:module/"
						+ DeCittadinanzaEJB.class.getSimpleName());
			} catch (NamingException e1) {
				log.warn(e1.getMessage());
				return null;
			}
			DeCittadinanza deCittadinanza = null;
			try {
				deCittadinanza = deCittadinanzaEJB.findById(value);
			} catch (MyCasNoResultException e) {
				log.warn(e.getMessage());
			}
			return deCittadinanza;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeCittadinanza) value).getCodCittadinanza());
		}
	}
}
