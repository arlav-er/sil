package it.eng.myportal.entity.home.decodifiche;

import java.util.List;

import javax.ejb.Stateless;

import it.eng.myportal.dtos.DePatentinoDTO;
import it.eng.myportal.entity.decodifiche.DePatentino;

/**
 * 
 * @author Rodi A.
 */
@Stateless
public class DePatentinoHome extends AbstractSuggestibleHome<DePatentino, DePatentinoDTO> {

	public DePatentino findById(final String id) {
		return findById(DePatentino.class, id);
	}

	@Override
	public DePatentinoDTO toDTO(final DePatentino patentino) {
		if (patentino == null)
			return null;
		DePatentinoDTO ret = super.toDTO(patentino);
		ret.setId(patentino.getCodPatentino());
		ret.setDescrizione(patentino.getDescrizione());
		return ret;
	}

	@Override
	public DePatentino fromDTO(final DePatentinoDTO patentino) {
		if (patentino == null)
			return null;
		DePatentino ret = super.fromDTO(patentino);
		ret.setCodPatentino(patentino.getId());
		ret.setDescrizione(patentino.getDescrizione());		
		return ret;
	}

	public List<DePatentino> findBySuggestionNoDto(String word) {
		List<DePatentino> deList = entityManager
				.createNamedQuery("DePatentinoFindSuggestionValide", DePatentino.class)
				.setHint("org.hibernate.cacheable", true).setParameter("query", "%"+word+"%").getResultList();
		
		return deList;
	}

}
