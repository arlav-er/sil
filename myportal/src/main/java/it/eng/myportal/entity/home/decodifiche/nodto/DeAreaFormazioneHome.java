package it.eng.myportal.entity.home.decodifiche.nodto;


import java.util.List;

import javax.ejb.Stateless;

import it.eng.myportal.entity.decodifiche.DeAreaFormazione;
import it.eng.myportal.entity.home.AbstractHibernateHome;

@Stateless
public class DeAreaFormazioneHome extends AbstractHibernateHome<DeAreaFormazione, String> {

	@Override
	public DeAreaFormazione findById(String id) {
		return findById(DeAreaFormazione.class, id);
	}

	public List<DeAreaFormazione> findBySuggestionNoDto(String word) {
		List<DeAreaFormazione> deList = entityManager
				.createNamedQuery("DeAreaFormazioneFindSuggestionValide", DeAreaFormazione.class)
				.setHint("org.hibernate.cacheable", true).setParameter("query", "%"+word+"%").getResultList();
		
		return deList;
	}

	public List<DeAreaFormazione> findAllValide() {
		return findBySuggestionNoDto("");
		
	}
	
	 

}
