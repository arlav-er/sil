package it.eng.myportal.converter;

import it.eng.myportal.dtos.DeTitoloDTO;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("titoloSuggestionListConverter")
public class TitoloSuggestionListConverter implements Converter
{

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		List<DeTitoloDTO> lista = new ArrayList<DeTitoloDTO>();
		if (arg2 != null) {
			DeTitoloDTO titolo = new DeTitoloDTO();
			titolo.setId(arg2);
			lista.add(titolo);
		}
		//else lista.add(new String());
		return lista;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		if (!(arg2 instanceof List)) return null;
		if (((List) arg2).isEmpty()) return null;
		if (!(((List) arg2).get(0) instanceof DeTitoloDTO)) return null;
		if ( ((DeTitoloDTO)((List) arg2).get(0)).getId() == null ) return null;
		return String.valueOf(((DeTitoloDTO)((List) arg2).get(0)).getId());
	}

}
