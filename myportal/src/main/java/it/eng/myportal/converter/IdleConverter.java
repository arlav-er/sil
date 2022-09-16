package it.eng.myportal.converter;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;

/**
 * Convertitore che non converte.
 * Bella idea, no?
 * 
 * @author Rodi A.
 * 
 */
@FacesConverter("idleConverter")
public class IdleConverter implements Converter {

	
	private static final String CSV_SEPARATOR = ",";
	
	public IdleConverter() {

	}

	@Override
	public List<String> getAsObject(FacesContext facesContext, UIComponent uiComponent, String param) {
		if (StringUtils.isNotBlank(param)) {
			final String[] strelem = param.split(CSV_SEPARATOR);
			String[] elem = new String[strelem.length];
			for (int i = 0; i < strelem.length; i++) {
				final String codice = strelem[i];
				elem[i] = codice;
			}
			List<String> list = Arrays.asList(elem);
			return list;
		}
		return null;
	}

	@Override
	public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object arg) {
		if (arg == null) return null;
		if (arg instanceof List<?>) {			
			Iterator<?> it = ((List<?>) arg).iterator();
			StringBuilder sb = new StringBuilder();
			while (it.hasNext()) {
				Object val = it.next();
				sb.append(val.toString());
				if (it.hasNext()) {
					sb.append(CSV_SEPARATOR);
				}
			}
			return sb.toString();
		}
		return "";
	}

}