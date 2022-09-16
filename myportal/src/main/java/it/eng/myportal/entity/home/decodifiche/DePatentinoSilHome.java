package it.eng.myportal.entity.home.decodifiche;

import java.util.List;

import javax.ejb.Stateless;

import it.eng.myportal.dtos.DePatentinoDTO;
import it.eng.myportal.entity.decodifiche.sil.DePatentinoSil;

@Stateless
public class DePatentinoSilHome extends AbstractSuggestibleHome<DePatentinoSil, DePatentinoDTO> {

	@Override
	public DePatentinoSil findById(String id) {
		return findById(DePatentinoSil.class, id);
	}

	@Override
	public DePatentinoDTO toDTO(final DePatentinoSil patentino) {
		if (patentino == null)
			return null;
		DePatentinoDTO dto = new DePatentinoDTO();
		dto.setDtInizioVal(patentino.getDtInizioVal());
		dto.setDtFineVal(patentino.getDtFineVal());
		dto.setId(patentino.getCodPatentinoSil());
		dto.setDescrizione(patentino.getDescrizione());
		return dto;
	}

	@Override
	public DePatentinoSil fromDTO(final DePatentinoDTO patentino) {
		if (patentino == null)
			return null;
		DePatentinoSil ret = super.fromDTO(patentino);
		ret.setCodPatentinoSil(patentino.getId());
		ret.setDescrizione(patentino.getDescrizione());
		return ret;
	}

	public List<DePatentinoSil> findBySuggestionNoDto(String word) {

		List<DePatentinoSil> deList = entityManager
				.createNamedQuery("DePatentinoSilFindSuggestionValide", DePatentinoSil.class)
				.setHint("org.hibernate.cacheable", true).setParameter("query", "%" + word + "%").getResultList();

		return deList;
	}
}
