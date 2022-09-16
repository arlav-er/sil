package it.eng.myportal.converter.nodto;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.entity.decodifiche.sil.DeOrarioSil;
import it.eng.myportal.entity.home.decodifiche.DeOrarioSilHome;
import it.eng.myportal.exception.MyPortalException;

@FacesConverter("deOrarioSilConverter")
public class DeOrarioSilConverter implements Converter {

	protected final Log log = LogFactory.getLog(this.getClass());

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeOrarioSilHome deContrattoSilHome = null;
			try {
				ic = new InitialContext();
				deContrattoSilHome = (DeOrarioSilHome) ic.lookup("java:module/DeOrarioSilHome");
			} catch (NamingException e1) {
				log.warn(e1.getMessage());
				return null;
			}
			DeOrarioSil deContrattoSil = null;
			try {
				deContrattoSil = deContrattoSilHome.findById(value);
			} catch (MyPortalException e) {
				log.warn(e.getMessage());
			}
			return deContrattoSil;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeOrarioSil) value).getCodOrarioSil());
		}
	}

}
