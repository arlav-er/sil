package it.eng.myportal.converter.nodto;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import it.eng.myportal.entity.decodifiche.sil.DeContrattoSil;
import it.eng.myportal.entity.home.decodifiche.DeContrattoSilHome;
import it.eng.myportal.exception.MyPortalException;

@FacesConverter("deContrattoSilConverter")
public class DeContrattoSilConverter implements Converter {

	protected final Log log = LogFactory.getLog(this.getClass());

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeContrattoSilHome deContrattoSilHome = null;
			try {
				ic = new InitialContext();
				deContrattoSilHome = (DeContrattoSilHome) ic.lookup("java:module/DeContrattoSilHome");
			} catch (NamingException e1) {
				log.warn(e1.getMessage());
				return null;
			}
			DeContrattoSil deContrattoSil = null;
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
			return String.valueOf(((DeContrattoSil) value).getCodContrattoSil());
		}
	}

}
