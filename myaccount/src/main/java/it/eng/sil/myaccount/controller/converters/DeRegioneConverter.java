package it.eng.sil.myaccount.controller.converters;

import it.eng.sil.mycas.exceptions.MyCasException;
import it.eng.sil.mycas.model.entity.decodifiche.DeRegione;
import it.eng.sil.mycas.model.manager.decodifiche.DeRegioneEJB;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@FacesConverter("deRegioneConverter")
public class DeRegioneConverter implements Converter {
	protected final Log log = LogFactory.getLog(this.getClass());

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeRegioneEJB deRegioneEJB = null;
			try {
				ic = new InitialContext();
				deRegioneEJB = (DeRegioneEJB) ic.lookup("java:module/DeRegioneEJB");
			} catch (NamingException e1) {
				log.warn(e1.getMessage());
				return null;
			}
			DeRegione deRegione = null;
			try {
				deRegione = deRegioneEJB.findById(value);
			} catch (MyCasException e) {
				log.warn(e.getMessage());
			}
			return deRegione;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("") || !(value instanceof DeRegione)) {
			return "";
		} else {
			return String.valueOf(((DeRegione) value).getCodRegione());
		}
	}
}
