package it.eng.myportal.converter;

import it.eng.myportal.entity.decodifiche.DeProvenienza;
import it.eng.myportal.entity.home.decodifiche.DeProvenienzaHome;
import it.eng.myportal.exception.MyPortalException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("deProvenienzaConverter")
public class DeProvenienzaConverter implements Converter {

	protected final Log log = LogFactory.getLog(this.getClass());

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeProvenienzaHome deProvenienzaHome = null;
			try {
				ic = new InitialContext();
				deProvenienzaHome = (DeProvenienzaHome) ic.lookup("java:module/DeProvenienzaHome");
			} catch (NamingException e1) {
				log.warn(e1.getMessage());
				return null;
			}
			DeProvenienza deProvenienza = null;
			try {
				deProvenienza = deProvenienzaHome.findById(value);
			} catch (MyPortalException e) {
				log.warn(e.getMessage());
			}
			return deProvenienza;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeProvenienza) value).getCodProvenienza());
		}
	}

}
