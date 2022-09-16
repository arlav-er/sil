package it.eng.sil.myaccount.controller.converters;

import it.eng.sil.myaccount.model.ejb.stateless.myportal.PtPortletEJB;
import it.eng.sil.myaccount.model.entity.myportal.PtPortlet;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@FacesConverter("ptPortletConverter")
public class PtPortletConverter implements Converter {
	protected final Log log = LogFactory.getLog(this.getClass());

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			PtPortletEJB ptPortletEJB = null;
			try {
				ic = new InitialContext();
				ptPortletEJB = (PtPortletEJB) ic.lookup("java:module/" + PtPortletEJB.class.getSimpleName());
			} catch (NamingException e1) {
				log.warn(e1.getMessage());
				return null;
			}
			PtPortlet ptPortlet = null;
			try {
				ptPortlet = ptPortletEJB.findById(Integer.parseInt(value));
			} catch (Exception e) {
				log.error(e.getMessage());
			}
			return ptPortlet;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((PtPortlet) value).getIdPtPortlet().toString());
		}
	}

}
