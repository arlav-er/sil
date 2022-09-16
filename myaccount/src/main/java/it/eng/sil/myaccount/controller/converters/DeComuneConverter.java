package it.eng.sil.myaccount.controller.converters;

import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.decodifiche.DeComune;
import it.eng.sil.mycas.model.manager.decodifiche.DeComuneEJB;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@FacesConverter("deComuneConverter")
public class DeComuneConverter implements Converter {

	protected final Log log = LogFactory.getLog(this.getClass());

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeComuneEJB deComuneManager = null;
			try {
				ic = new InitialContext();
				deComuneManager = (DeComuneEJB) ic.lookup("java:module/DeComuneEJB");
			} catch (NamingException e1) {
				log.warn(e1.getMessage());
				return null;
			}
			DeComune deComune = null;
			try {
				deComune = deComuneManager.findById(value);
			} catch (MyCasNoResultException e) {
				log.warn(e.getMessage());
			}
			return deComune;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeComune) value).getCodCom());
		}
	}

}
