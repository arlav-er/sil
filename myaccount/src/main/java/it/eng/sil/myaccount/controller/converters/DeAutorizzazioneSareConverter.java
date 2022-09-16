package it.eng.sil.myaccount.controller.converters;

import it.eng.sil.myaccount.model.ejb.stateless.decodifiche.DeAutorizzazioneSareEJB;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.decodifiche.profilatura.DeAutorizzazioneSare;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@FacesConverter("deAutorizzazioneSareConverter")
public class DeAutorizzazioneSareConverter implements Converter {
	protected final Log log = LogFactory.getLog(this.getClass());

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeAutorizzazioneSareEJB deAutorizzazioneSareEJB = null;
			try {
				ic = new InitialContext();
				deAutorizzazioneSareEJB = (DeAutorizzazioneSareEJB) ic.lookup("java:module/DeAutorizzazioneSareEJB");
			} catch (NamingException e1) {
				log.warn(e1.getMessage());
				return null;
			}
			DeAutorizzazioneSare deAutorizzazioneSare = null;
			try {
				deAutorizzazioneSare = deAutorizzazioneSareEJB.findById(value);
			} catch (MyCasNoResultException e) {
				log.warn(e.getMessage());
			}
			return deAutorizzazioneSare;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeAutorizzazioneSare) value).getCodAutorizzazioneSare());
		}
	}

}
