package it.eng.myportal.converter;

import it.eng.myportal.entity.decodifiche.DeEvasioneRich;
import it.eng.myportal.entity.home.decodifiche.DeEvasioneRichHome;
import it.eng.myportal.exception.MyPortalException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("deEvasioneRichConverter")
public class DeEvasioneRichConverter implements Converter {

	protected final Log log = LogFactory.getLog(this.getClass());

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeEvasioneRichHome deEvasioneRichHome = null;
			try {
				ic = new InitialContext();
				deEvasioneRichHome = (DeEvasioneRichHome) ic.lookup("java:module/DeEvasioneRichHome");
			} catch (NamingException e1) {
				log.warn(e1.getMessage());
				return null;
			}
			DeEvasioneRich deEvasioneRich = null;
			try {
				deEvasioneRich = deEvasioneRichHome.findById(value);
			} catch (MyPortalException e) {
				log.warn(e.getMessage());
			}
			return deEvasioneRich;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeEvasioneRich) value).getCodEvasione());
		}
	}
}
