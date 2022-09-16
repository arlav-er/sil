package it.eng.myportal.converter.nodto;
 
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import it.eng.myportal.entity.decodifiche.sil.DeAlboSil;
import it.eng.myportal.entity.home.decodifiche.DeAlboSilHome;

@FacesConverter("deAlboSilConverter")
public class DeAlboSilConverter implements Converter {
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			DeAlboSilHome deAlboEJB = null;
			DeAlboSil deAlbo = null;
			try {
				InitialContext ic = new InitialContext();
				deAlboEJB = (DeAlboSilHome) ic.lookup("java:module/DeAlboSilHome");
			} catch (NamingException e1) {
				return null;
			}
			try {
				deAlbo = deAlboEJB.findById(value);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return deAlbo;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeAlboSil) value).getCodAlboSil());
		}
	}
}
