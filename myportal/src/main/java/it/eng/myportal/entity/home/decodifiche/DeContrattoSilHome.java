package it.eng.myportal.entity.home.decodifiche;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import it.eng.myportal.dtos.DeContrattoDTO;
import it.eng.myportal.entity.decodifiche.sil.DeContrattoSil;

@Stateless
public class DeContrattoSilHome extends AbstractSuggestibleHome<DeContrattoSil, DeContrattoDTO> {
	private static final String QUERY_RICERCA = "select c from DeContrattoSil c where current_date between c.dtInizioVal and c.dtFineVal order by c.descrizione";

	@Override
	public DeContrattoSil findById(String id) {
		return findById(DeContrattoSil.class, id);
	}

	@Override
	public DeContrattoDTO toDTO(final DeContrattoSil entity) {
		if (entity == null)
			return null;
		DeContrattoDTO dto = new DeContrattoDTO();
		dto.setDtInizioVal(entity.getDtInizioVal());
		dto.setDtFineVal(entity.getDtFineVal());
		dto.setId(entity.getCodContrattoSil());
		dto.setDescrizione(entity.getDescrizione());
		dto.setFlagTirocini(entity.getFlagTirocini());

		return dto;
	}

	@Override
	public DeContrattoSil fromDTO(final DeContrattoDTO dto) {
		if (dto == null)
			return null;
		final DeContrattoSil entity = super.fromDTO(dto);
		entity.setCodContrattoSil(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		entity.setFlagTirocini(dto.getFlagTirocini());

		return entity;
	}

	public boolean isScaduto(String codContratto) {
		DeContrattoSil deContrattoSil = findById(codContratto);
		return (deContrattoSil.getDtFineVal().before(new Date()));
	}

	public List<DeContrattoSil> getAll() {
		TypedQuery<DeContrattoSil> query = entityManager.createQuery(QUERY_RICERCA, DeContrattoSil.class).setHint(
				"org.hibernate.cacheable", true);
		List<DeContrattoSil> resultList = query.getResultList();
		return resultList;
	}

	public List<DeContrattoSil> findBySuggestionNoDto(String word) {
		List<DeContrattoSil> deList = entityManager
				.createNamedQuery("DeDeContrattoSilFindSuggestionValide", DeContrattoSil.class)
				.setHint("org.hibernate.cacheable", true).setParameter("query", "%"+word+"%").getResultList();
		
		return deList;
	}
	
	public List<DeContrattoSil> findBySuggestionFlagIdoTrueNoDto(String word) {
		List<DeContrattoSil> deList = entityManager
				.createNamedQuery("deContrattoSilFindSuggestionValideFlagIdoTrue", DeContrattoSil.class)
				.setHint("org.hibernate.cacheable", true).setParameter("query", "%"+word+"%").getResultList();
		
		return deList;
	}
	
	public List<DeContrattoSil> findValideByFlagIdo(boolean flagIdo) {
		List<DeContrattoSil> deContrattoSilS = entityManager
					.createNamedQuery("deContrattoValideByFlagIdo", DeContrattoSil.class)
					.setParameter("flagIdo", flagIdo)
					.getResultList();
		return deContrattoSilS;
	}
}
