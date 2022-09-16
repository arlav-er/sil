package it.eng.sil.myaccount.controller.converters;

import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.decodifiche.DeTitoloSoggiorno;
import it.eng.sil.mycas.model.manager.decodifiche.DeTitoloSoggiornoEJB;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@FacesConverter("deTitoloSoggiornoConverter")
public class DeTitoloSoggiornoConverter implements Converter {

	protected final Log log = LogFactory.getLog(this.getClass());

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeTitoloSoggiornoEJB deTitoloSoggiornoEJB = null;
			try {
				ic = new InitialContext();
				deTitoloSoggiornoEJB = (DeTitoloSoggiornoEJB) ic.lookup("java:module/DeTitoloSoggiornoEJB");
			} catch (NamingException e1) {
				log.warn(e1.getMessage());
				return null;
			}
			DeTitoloSoggiorno deTitoloSoggiorno = null;
			try {
				deTitoloSoggiorno = deTitoloSoggiornoEJB.findById(value);
			} catch (MyCasNoResultException e) {
				log.warn(e.getMessage());
			}
			return deTitoloSoggiorno;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeTitoloSoggiorno) value).getCodTitoloSoggiorno());
		}
	}
}
