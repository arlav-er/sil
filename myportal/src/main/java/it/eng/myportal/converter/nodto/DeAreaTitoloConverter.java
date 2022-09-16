package it.eng.myportal.converter.nodto;

import it.eng.myportal.entity.decodifiche.DeAreaTitolo;
import it.eng.myportal.entity.decodifiche.DeTitolo;
import it.eng.myportal.entity.home.decodifiche.DeTitoloHome;
import it.eng.myportal.entity.home.decodifiche.nodto.DeAreaTitoloHome;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("deAreaTitoloConverter")
public class DeAreaTitoloConverter implements Converter {
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DeAreaTitoloHome deAreaTitoloHome = null;
			try {
				ic = new InitialContext();
				deAreaTitoloHome = (DeAreaTitoloHome) ic.lookup("java:module/DeAreaTitoloHome");
			} catch (NamingException e1) {
				return null;
			}
			return deAreaTitoloHome.findById(value);
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DeAreaTitolo) value).getCodAreaTitolo());
		}
	}
}
