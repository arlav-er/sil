package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeSvTemplateDTO;
import it.eng.myportal.entity.decodifiche.DeSvTemplate;

import javax.ejb.Stateless;

/**
 * Home per i template
 * 
 * @author rodi
 */
@Stateless
public class DeSvTemplateHome extends AbstractDecodeHome<DeSvTemplate, DeSvTemplateDTO> {

	private static final String SQL_SUGGEST_TEMPLATE = " SELECT t " + " FROM DeSvTemplate t "
			+ " WHERE lower(t.descrizione) LIKE '%' || :query || '%' " + " ORDER BY t.descrizione ";

	private static final String SELECT_ITEMS_QUERY = "SELECT new javax.faces.model.SelectItem(t.codSvTemplate,t.descrizione) from DeSvTemplate t";

	public String getSuggestionQuery() {
		return SQL_SUGGEST_TEMPLATE;
	}

	public String getSelectItemQuery() {
		return SELECT_ITEMS_QUERY;
	}

	@Override
	public DeSvTemplate findById(String id) {
		return findById(DeSvTemplate.class, id);
	}

	public DeSvTemplateDTO toDTO(DeSvTemplate template) {
		if (template == null) return null;
		DeSvTemplateDTO dto = super.toDTO(template);
		dto.setId(template.getCodSvTemplate());
		dto.setDescrizione(template.getDescrizione());
		return dto;
	}

}
