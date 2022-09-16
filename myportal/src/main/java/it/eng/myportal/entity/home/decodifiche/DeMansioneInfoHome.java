package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeMansioneInfoDTO;
import it.eng.myportal.entity.decodifiche.DeMansioneInfo;

import javax.ejb.Stateless;

/**
 * 
 * 
 * @author Girotti
 */
@Stateless
public class DeMansioneInfoHome extends AbstractSuggestibleHome<DeMansioneInfo, DeMansioneInfoDTO> {

	//private final static String SQL_SUGGEST = "  " + " SELECT t " + " FROM DeMansioneInfo t "
	//		+ " WHERE lower(trim(t.descrizioneInfo)) LIKE '%' || :query || '%' " + " ORDER BY t.descrizioneInfo";
	
	//private static final String SELECT_ITEM_QUERY = "select new javax.faces.model.SelectItem(c.codMansione,c.descrizioneInfo)"
	//		+ " from DeMansioneInfo c ";

	public DeMansioneInfo findById(final String id) {
		return findById(DeMansioneInfo.class, id);
	}

	@Override
	public DeMansioneInfoDTO toDTO(final DeMansioneInfo entity) {
		if (entity == null) {
			return null;
		}
		DeMansioneInfoDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodMansione());
		dto.setTitoloInfo(entity.getTitoloInfo());
		dto.setDescrizione(entity.getDescrizioneInfo());
		return dto;
	}

	@Override
	public DeMansioneInfo fromDTO(final DeMansioneInfoDTO dto) {
		if (dto == null) {
			return null;
		}
		DeMansioneInfo entity = super.fromDTO(dto);
		entity.setCodMansione(dto.getId());
		entity.setTitoloInfo(dto.getTitoloInfo());
		entity.setDescrizioneInfo(dto.getDescrizione());
		return entity;
	}
	
//	@Override
//	public String getSelectItemQuery() {
//		return SELECT_ITEM_QUERY;
//	}


	/* (non-Javadoc)
	 * @see it.eng.myportal.entity.home.AbstractSuggestibleHome#getFieldName()
	 */
	@Override
	public String getFieldName() {
		return "descrizioneInfo";
	}
}
