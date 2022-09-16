package it.eng.myportal.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Un semplice convertitore a Boolean.
 * 
 * @author Rodi A.
 *
 */
@FacesConverter("booleanConverter")
public class BooleanConverter implements Converter{
	
	public BooleanConverter(){
		
	}
	
	@Override
	public Boolean getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		return (arg2 != null && !"".equals(arg2));
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		Boolean bool = (Boolean)arg2;
		if (bool) return "true";
		else	return "";
	}

}