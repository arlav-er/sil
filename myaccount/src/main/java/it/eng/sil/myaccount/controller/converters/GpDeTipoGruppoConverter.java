package it.eng.sil.myaccount.controller.converters;

import it.eng.sil.base.enums.GpDeTipoGruppoEnum;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpDeTipoGruppo;
import it.eng.sil.mycas.model.manager.gestioneprofilo.GpDeTipoGruppoEJB;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@FacesConverter("gpDeTipoGruppoConverter")
public class GpDeTipoGruppoConverter implements Converter {
	protected final Log log = LogFactory.getLog(this.getClass());

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value != null && !value.trim().isEmpty()) {
			InitialContext ic;
			GpDeTipoGruppoEJB gpDeTipoGruppoEJB = null;
			try {
				ic = new InitialContext();
				gpDeTipoGruppoEJB = (GpDeTipoGruppoEJB) ic.lookup("java:module/GpDeTipoGruppoEJB");
				return gpDeTipoGruppoEJB.findById(GpDeTipoGruppoEnum.valueOf(value));
			} catch (NamingException | MyCasNoResultException | NumberFormatException e) {
				log.warn("Errore " + e.getClass() + " nella string2object del converter di GpDeTipoGruppo: "
						+ e.getMessage());
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || value.equals("") || !(value instanceof GpDeTipoGruppo)) {
			return "";
		} else {
			return ((GpDeTipoGruppo) value).getCodTipoGruppo().toString();
		}
	}
}
