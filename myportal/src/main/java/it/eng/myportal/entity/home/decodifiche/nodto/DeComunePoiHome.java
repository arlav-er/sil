package it.eng.myportal.entity.home.decodifiche.nodto;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import it.eng.myportal.entity.decodifiche.DeComune;
import it.eng.myportal.entity.decodifiche.DeComunePoi;
import it.eng.myportal.entity.home.AbstractHibernateHome;
import it.eng.myportal.exception.MyPortalNoResultFoundException;

@Stateless
public class DeComunePoiHome extends AbstractHibernateHome<DeComunePoi, String> {

	@Override
	public DeComunePoi findById(String id) {
		return findById(DeComunePoi.class, id);
	}

	public DeComunePoi findByDeComune(DeComune input) throws MyPortalNoResultFoundException {
		 
			TypedQuery<DeComunePoi> query = entityManager.createNamedQuery("findComuneByCodComune", DeComunePoi.class)
					.setParameter("input", input).setHint("org.hibernate.cacheable", true);
			List<DeComunePoi> li = query.getResultList();
			
			if (li.isEmpty())
				throw new MyPortalNoResultFoundException("DE_COMUNE_POI non trovata per: " + input.getCodCom());
			
			return li.get(0);
		 

	}
}