package it.eng.myportal.converter.nodto;

import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.exception.MyPortalException;

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
			DeProvinciaHome deProvinciaHome = null;
			try {
				ic = new InitialContext();
				deProvinciaHome = (DeProvinciaHome) ic.lookup("java:module/DeProvinciaHome");
			} catch (NamingException e1) {
				log.warn(e1.getMessage());
				return null;
			}
			DeProvincia deProvincia = null;
			try {
				deProvincia = deProvinciaHome.findById(value);
			} catch (MyPortalException e) {
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
