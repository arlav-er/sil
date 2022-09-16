package it.eng.sil.myaccount.controller.converters;

import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.decodifiche.profilatura.DeTipoAbilitato;
import it.eng.sil.mycas.model.manager.decodifiche.DeTipoAbilitatoEJB;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@FacesConverter("deTipoAbilitatoConverter")
public class DeTipoAbilitatoConverter implements Converter {

	protected final Log log = LogFactory.getLog(this.getClass());

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeTipoAbilitatoEJB deTipoAbilitatoEJB = null;
			try {
				ic = new InitialContext();
				deTipoAbilitatoEJB = (DeTipoAbilitatoEJB) ic.lookup("java:module/DeTipoAbilitatoEJB");
			} catch (NamingException e1) {
				log.warn(e1.getMessage());
				return null;
			}
			DeTipoAbilitato deTipoAbilitato = null;
			try {
				deTipoAbilitato = deTipoAbilitatoEJB.findById(value);
			} catch (MyCasNoResultException e) {
				log.warn(e.getMessage());
			}
			return deTipoAbilitato;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeTipoAbilitato) value).getCodTipoAbilitato());
		}
	}
}
