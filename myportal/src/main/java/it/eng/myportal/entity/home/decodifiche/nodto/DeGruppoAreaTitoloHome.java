package it.eng.myportal.entity.home.decodifiche.nodto;

import java.util.List;
import javax.ejb.Stateless;
import it.eng.myportal.entity.decodifiche.DeGruppoAreaTitolo;
import it.eng.myportal.entity.home.AbstractHibernateHome;
/**
 * Area dei titoli di studio, nata per la gestione del CV 2020.
 * Per questo motivo taglio AbstractSuggestibleHome&co. dalla gerarchia, evito i DTO
 *  
 *  * @author Ale
 *
 */
@Stateless
public class DeGruppoAreaTitoloHome extends AbstractHibernateHome<DeGruppoAreaTitolo, String> {

	

	@Override
	public DeGruppoAreaTitolo findById(String id) {
		return findById(DeGruppoAreaTitolo.class, id);
	}
	
	
	public List<DeGruppoAreaTitolo> findBySuggestion(String sugg){
		List<DeGruppoAreaTitolo> deQualificaSrqList = entityManager
				.createNamedQuery("DeGruppoAreaTitoloFindSuggestion", DeGruppoAreaTitolo.class)
				.setHint("org.hibernate.cacheable", true).setParameter("query", "%" + sugg + "%").getResultList();

		return deQualificaSrqList;
	}

	
	

}
