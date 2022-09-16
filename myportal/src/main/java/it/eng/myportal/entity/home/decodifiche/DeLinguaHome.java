package it.eng.myportal.entity.home.decodifiche;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.DeLinguaDTO;
import it.eng.myportal.entity.decodifiche.DeLingua;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 * Home object for domain model class DeLingua.
 * 
 * @see it.eng.myportal.entity.decodifiche.DeLingua
 * @author Rodi A.
 */
@Stateless
public class DeLinguaHome extends AbstractTreeableHome<DeLingua, DeLinguaDTO> {

	private static final String QUERY_RICERCA = "select c from DeLingua c where current_date between c.dtInizioVal and c.dtFineVal order by c.denominazione";

	private static final String TS_QUERY = " select e.* from de_lingua e where to_tsvector(lower(e.denominazione)) @@ to_tsquery(lower(?)) ORDER BY denominazione LIMIT 10";

	public DeLingua findById(String id) {
		return findById(DeLingua.class, id);
	}

	@Override
	public DeLinguaDTO toDTO(DeLingua lingua) {
		if (lingua == null)
			return null;
		DeLinguaDTO ret = super.toDTO(lingua);
		ret.setId(lingua.getCodLingua());
		ret.setDenominazione(lingua.getDenominazione());
		return ret;
	}

	@Override
	public DeLingua fromDTO(DeLinguaDTO lingua) {
		if (lingua == null)
			return null;
		DeLingua ret = super.fromDTO(lingua);
		ret.setCodLingua(lingua.getId());
		ret.setDenominazione(lingua.getDenominazione());
		return ret;
	}
	
	/**
	 * E' ad un solo livello!
	 */
	@Override
	public List<DeLinguaDTO> findByCodPadre(String par) {
		if ("0".equals(par))
			return findAllDTO();
		else
			return new ArrayList<DeLinguaDTO>();
	}

	@Override
	public String getFieldName() {
		return "denominazione";
	}
	
	
	public List<DeLingua> getAll() {		
		TypedQuery<DeLingua> query = entityManager.createQuery(QUERY_RICERCA, DeLingua.class).setHint("org.hibernate.cacheable", true);
		List<DeLingua> resultList = query.getResultList();		
		return resultList;
	}


	public List<DeLingua> findBySuggest(String par) {
		String queryString = TS_QUERY;
		par = this.checkSqlParameter(par);

		String queryPar = splitStringForTSQuery(par);
		Query query = entityManager.createNativeQuery(queryString, DeLingua.class);
		query.setParameter(1, queryPar);

		return query.getResultList();
	}
}
