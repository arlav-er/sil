package it.eng.myportal.entity.home.decodifiche.nodto;


import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import it.eng.myportal.entity.decodifiche.DeAreaTitolo;
import it.eng.myportal.entity.decodifiche.DeGruppoAreaTitolo;
import it.eng.myportal.entity.home.AbstractHibernateHome;

@Stateless
public class DeAreaTitoloHome extends AbstractHibernateHome<DeAreaTitolo, String> {

	@Override
	public DeAreaTitolo findById(String id) {
		return findById(DeAreaTitolo.class, id);
	}
	
	public List<DeAreaTitolo> findByPadre(DeGruppoAreaTitolo padre) {
		TypedQuery<DeAreaTitolo> query = entityManager.createNamedQuery("findValideByPadre", DeAreaTitolo.class)
				.setParameter("parDate", new Date()).setHint("org.hibernate.cacheable", true)
				.setParameter("parPadre", padre);		
		return query.getResultList();
	}


}
