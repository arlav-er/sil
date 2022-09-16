package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.PfTestiDTO;
import it.eng.myportal.entity.PfTesti;
import it.eng.myportal.entity.PfTesti_;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Home object for domain model class PfTesti.
 * 
 * @see it.eng.myportal.entity.PfTesti
 * @author Rodi A.
 */
@Stateless
public class PfTestiHome extends AbstractUpdatableHome<PfTesti, PfTestiDTO> {

	public PfTesti findById(Integer id) {
		return findById(PfTesti.class, id);
	}

	/**
	 * Restituisce tutti i testi per la regione selezionata
	 * @param codRegione
	 * @return
	 */
	public HashMap<String, String> loadTesti(String codRegione) {
		List<Predicate> whereConditions = new ArrayList<Predicate>();
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<PfTesti> query = qb.createQuery(PfTesti.class);
		Root<PfTesti> pfTesti = query.from(PfTesti.class);		
		whereConditions.add(qb.equal(pfTesti.get(PfTesti_.codRegione), codRegione));		
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		
		TypedQuery<PfTesti> q = entityManager.createQuery(query);
		List<PfTesti> results = q.getResultList();
		HashMap<String,String> map = new HashMap<String, String>();
		for (PfTesti pfTesto : results) {
			map.put(pfTesto.getCodice(), pfTesto.getTesto());
		}
		return map;
	}
	
	@Override
	public PfTestiDTO toDTO(PfTesti entity) {
		if (entity == null)
			return null;
		PfTestiDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdPfTesti());
		dto.setCodice(entity.getCodice());
		dto.setCodRegione(entity.getCodRegione());
		dto.setTesto(entity.getTesto());
		dto.setContesto(entity.getContesto());
		dto.setTipo(entity.getTipo());
		return dto;
	}

	@Override
	public PfTesti fromDTO(PfTestiDTO dto) {
		if (dto == null)
			return null;
		PfTesti entity = super.fromDTO(dto);
		entity.setIdPfTesti(dto.getId());
		entity.setCodice(dto.getCodice());
		entity.setCodRegione(dto.getCodRegione());
		entity.setTesto(dto.getTesto());
		entity.setTipo(dto.getTipo());
		entity.setContesto(dto.getContesto());
		
		return entity;
	}


}
