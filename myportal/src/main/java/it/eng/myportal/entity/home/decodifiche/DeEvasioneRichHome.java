package it.eng.myportal.entity.home.decodifiche;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import it.eng.myportal.dtos.DeEvasioneRichDTO;
import it.eng.myportal.entity.decodifiche.DeEvasioneRich;
import it.eng.myportal.entity.decodifiche.DeEvasioneRich_;
@Stateless
public class DeEvasioneRichHome extends AbstractSuggestibleHome<DeEvasioneRich, DeEvasioneRichDTO> {
	
	public DeEvasioneRich findById(final String id) {
		return findById(DeEvasioneRich.class, id);
	}
	
	public List<DeEvasioneRich> findByCodEvasione(String ... codEvasione){
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<DeEvasioneRich> cq = cb.createQuery(DeEvasioneRich.class);
		Root<DeEvasioneRich> root = cq.from(DeEvasioneRich.class);
		cq.where(root.get(DeEvasioneRich_.codEvasione)
				  .in(Arrays.asList(codEvasione)));
		TypedQuery<DeEvasioneRich> typedQuery = entityManager.createQuery(cq);
		return typedQuery.getResultList();
	}

}
