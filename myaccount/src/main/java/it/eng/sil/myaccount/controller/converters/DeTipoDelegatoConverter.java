package it.eng.sil.myaccount.controller.converters;

import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.decodifiche.DeTipoDelegato;
import it.eng.sil.mycas.model.manager.decodifiche.DeTipoDelegatoEJB;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@FacesConverter("deTipoDelegatoConverter")
public class DeTipoDelegatoConverter implements Converter {

	protected final Log log = LogFactory.getLog(this.getClass());

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeTipoDelegatoEJB deTipoDelegatoEJB = null;
			try {
				ic = new InitialContext();
				deTipoDelegatoEJB = (DeTipoDelegatoEJB) ic.lookup("java:module/DeTipoDelegatoEJB");
			} catch (NamingException e1) {
				log.warn(e1.getMessage());
				return null;
			}
			DeTipoDelegato deTipoDelegato = null;
			try {
				deTipoDelegato = deTipoDelegatoEJB.findById(value);
			} catch (MyCasNoResultException e) {
				log.warn(e.getMessage());
			}
			return deTipoDelegato;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeTipoDelegato) value).getCodTipoDelegato());
		}
	}
}
