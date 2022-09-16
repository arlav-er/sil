package it.eng.myportal.converter.nodto;

import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("deComuneConverter")
public class DeComuneConverter implements Converter {
	DeComuneHome comuneHome;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeComuneHome comuneHome = null;
			try {
				ic = new InitialContext();
				comuneHome = (DeComuneHome) ic.lookup("java:module/DeComuneHome");
			} catch (NamingException e1) {
				return null;
			}
			DeComune deComune = comuneHome.findById(value);
			return deComune;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeComune) value).getCodCom());
		}
	}

}
