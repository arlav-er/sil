package it.eng.sil.myaccount.controller.converters;

import it.eng.sil.base.enums.GpDeMacroTipoEnum;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpDeMacroTipo;
import it.eng.sil.mycas.model.manager.gestioneprofilo.GpDeMacroTipoEJB;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@FacesConverter("gpDeMacroTipoConverter")
public class GpDeMacroTipoConverter implements Converter {
	protected final Log log = LogFactory.getLog(this.getClass());

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value != null && !value.trim().isEmpty()) {
			InitialContext ic;
			GpDeMacroTipoEJB gpDeMacroTipoEJB = null;
			try {
				ic = new InitialContext();
				gpDeMacroTipoEJB = (GpDeMacroTipoEJB) ic.lookup("java:module/GpDeMacroTipoEJB");
				return gpDeMacroTipoEJB.findById(GpDeMacroTipoEnum.valueOf(value));
			} catch (NamingException | MyCasNoResultException | NumberFormatException e) {
				log.warn("Errore " + e.getClass() + " nella string2object del converter di GpDeMacroTipo: "
						+ e.getMessage());
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("") || !(value instanceof GpDeMacroTipo)) {
			return "";
		} else {
			return ((GpDeMacroTipo) value).getCodMacroTipo().toString();
		}
	}
}
