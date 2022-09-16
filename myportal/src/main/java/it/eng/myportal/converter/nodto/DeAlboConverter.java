package it.eng.myportal.converter.nodto;
 
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import it.eng.myportal.entity.decodifiche.DeAlbo;
import it.eng.myportal.entity.home.decodifiche.DeAlboHome;

@FacesConverter("deAlboConverter")
public class DeAlboConverter implements Converter {
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			DeAlboHome deAlboEJB = null;
			DeAlbo deAlbo = null;
			try {
				InitialContext ic = new InitialContext();
				deAlboEJB = (DeAlboHome) ic.lookup("java:module/DeAlboHome");
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
			return String.valueOf(((DeAlbo) value).getCodAlbo());
		}
	}
}
