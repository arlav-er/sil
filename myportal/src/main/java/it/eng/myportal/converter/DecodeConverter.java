package it.eng.myportal.converter;

import it.eng.myportal.dtos.GenericDecodeDTO;
import it.eng.myportal.dtos.IDecode;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

/**
 * Convertitore da String a generico oggetto di Decodifica.
 * 
 * @author Rodi A.
 * 
 */
@FacesConverter("decodeConverter")
public class DecodeConverter implements Converter {

	public DecodeConverter() {

	}

	@Override
	public IDecode getAsObject(FacesContext facesContext, UIComponent uiComponent, String param) {
		GenericDecodeDTO ret = new GenericDecodeDTO(param);
		return ret; 
	}

	@Override
	public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object arg) {
		if (arg == null) return null;
		if (arg instanceof IDecode) {
			return ((IDecode) arg).getId();
		}
		throw new ConverterException(new FacesMessage("Errore di conversione"));
	}

}