package it.eng.myportal.converter;

import it.eng.myportal.dtos.DePresenzaItaliaMinDTO;
import it.eng.myportal.entity.home.decodifiche.DePresenzaItaliaMinHome;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@FacesConverter("dePresenzaItaliaMinDTOConverter")
public class DePresenzaItaliaMinDTOConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value.trim().equals("")) {
			return null;
		} else {
			InitialContext ic;
			DePresenzaItaliaMinHome dePresenzaItaliaMinHome = null;
			try {
				ic = new InitialContext();
				dePresenzaItaliaMinHome = (DePresenzaItaliaMinHome) ic.lookup("java:module/DePresenzaItaliaMinHome");
			} catch (NamingException e1) {
				return null;
			}
			DePresenzaItaliaMinDTO dePresenzaItaliaMinDTO = dePresenzaItaliaMinHome.findDTOById(value);
			return dePresenzaItaliaMinDTO;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((DePresenzaItaliaMinDTO) value).getId());
		}
	}

}
