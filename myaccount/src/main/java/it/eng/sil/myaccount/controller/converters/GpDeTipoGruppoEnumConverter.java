package it.eng.sil.myaccount.controller.converters;

import it.eng.sil.base.enums.GpDeTipoGruppoEnum;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@FacesConverter("gpDeTipoGruppoEnumConverter")
public class GpDeTipoGruppoEnumConverter implements Converter {
	protected final Log log = LogFactory.getLog(this.getClass());

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		// Controllo che la stringa esista e non sia vuota.
		if (value != null && !value.trim().isEmpty()) {
			try {
				return GpDeTipoGruppoEnum.valueOf(value);
			} catch (Exception e) {
				// Se viene passata una stringa che non corrisponde ad un valore dell'enum.
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		// Controllo che l'oggetto esista e sia di tipo giusto.
		if (value != null && value instanceof GpDeTipoGruppoEnum) {
			return value.toString();
		} else {
			return null;
		}
	}

}
