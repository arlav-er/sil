package it.eng.myportal.converter;

import it.eng.myportal.dtos.GenericDecodeDTO;
import it.eng.myportal.dtos.IDecode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;

/**
 * Un semplice convertitore String csv vs List.
 * 
 * @author Girotti S.
 * 
 */
@FacesConverter("tokenConverter")
public class TokenConverter implements Converter {

	private static final String CSV_SEPARATOR = ",";

	public TokenConverter() {

	}

	@Override
	public List<IDecode> getAsObject(FacesContext facesContext, UIComponent uiComponent, String param) {
		// uiComponent.get
		if (StringUtils.isNotBlank(param)) {
			final String[] strelem = param.split(CSV_SEPARATOR);
			List<IDecode> list = new ArrayList<IDecode>();
			for (int i = 0; i < strelem.length; i++) {
				final String codice = strelem[i];
				list.add(new GenericDecodeDTO(codice));
			}
			return list;
		}
		// Se il parametro è vuoto invece
		return null;
	}

	@Override
	public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object arg) {
		if (arg instanceof List<?>) {
			List<IDecode> list = (List<IDecode>) arg;
			Iterator<IDecode> it = list.iterator();
			StringBuilder sb = new StringBuilder();
			while (it.hasNext()) {
				IDecode nextDecode = it.next();
				if (nextDecode != null) {
					String string = nextDecode.getId();
					sb.append(string);
					if (it.hasNext()) {
						sb.append(CSV_SEPARATOR);
					}
				}
			}
			return sb.toString();
		}
		return "";
	}

}