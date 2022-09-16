package it.eng.sil.myaccount.controller.converters;

import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.decodifiche.DeProvincia;
import it.eng.sil.mycas.model.manager.decodifiche.DeProvinciaEJB;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@FacesConverter("deProvinciaConverter")
public class DeProvinciaConverter implements Converter {
	protected final Log log = LogFactory.getLog(this.getClass());

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeProvinciaEJB deProvinciaEJB = null;
			try {
				ic = new InitialContext();
				deProvinciaEJB = (DeProvinciaEJB) ic.lookup("java:module/DeProvinciaEJB");
			} catch (NamingException e1) {
				log.warn(e1.getMessage());
				return null;
			}
			DeProvincia deProvincia = null;
			try {
				deProvincia = deProvinciaEJB.findById(value);
			} catch (MyCasNoResultException e) {
				log.warn(e.getMessage());
			}
			return deProvincia;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeProvincia) value).getCodProvincia());
		}
	}
}
