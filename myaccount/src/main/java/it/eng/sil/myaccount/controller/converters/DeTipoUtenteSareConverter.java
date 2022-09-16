package it.eng.sil.myaccount.controller.converters;

import it.eng.sil.myaccount.model.ejb.stateless.decodifiche.DeTipoUtenteSareEJB;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.decodifiche.profilatura.DeTipoUtenteSare;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@FacesConverter("deTipoUtenteSareConverter")
public class DeTipoUtenteSareConverter implements Converter {
	protected final Log log = LogFactory.getLog(this.getClass());

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeTipoUtenteSareEJB deTipoUtenteSareEJB = null;
			try {
				ic = new InitialContext();
				deTipoUtenteSareEJB = (DeTipoUtenteSareEJB) ic.lookup("java:module/DeTipoUtenteSareEJB");
			} catch (NamingException e1) {
				log.warn(e1.getMessage());
				return null;
			}
			DeTipoUtenteSare deTipoUtenteSare = null;
			try {
				deTipoUtenteSare = deTipoUtenteSareEJB.findById(value);
			} catch (MyCasNoResultException e) {
				log.warn(e.getMessage());
			}
			return deTipoUtenteSare;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeTipoUtenteSare) value).getCodTipoUtenteSare());
		}
	}
}
