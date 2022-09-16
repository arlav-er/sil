package it.eng.myportal.converter.nodto;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import it.eng.myportal.entity.decodifiche.DeTrasferta;
import it.eng.myportal.entity.home.decodifiche.DeTrasfertaHome;

@FacesConverter("deTrasfertaConverter")
public class DeTrasfertaConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeTrasfertaHome deTrasfertaHome = null;
			try {
				ic = new InitialContext();
				deTrasfertaHome = (DeTrasfertaHome) ic.lookup("java:module/DeTrasfertaHome");
			} catch (NamingException e1) {
				return null;
			}
			return deTrasfertaHome.findById(value);
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeTrasferta) value).getCodTrasferta());
		}
	}
}
