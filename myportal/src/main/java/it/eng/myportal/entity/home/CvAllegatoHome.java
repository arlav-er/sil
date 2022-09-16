package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.CvAllegatoDTO;
import it.eng.myportal.entity.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @author girotti
 *
 */
@Stateless
public class CvAllegatoHome extends
		AbstractUpdatableHome<CvAllegato, CvAllegatoDTO> {

	@EJB
	CvDatiPersonaliHome cvDatiPersonaliHome;

	@Override
	public CvAllegato findById(Integer id) {
		return findById(CvAllegato.class, id);
	}

	@Override
	public CvAllegatoDTO toDTO(CvAllegato entity) {
		if (entity == null)
			return null;
		CvAllegatoDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdCvAllegato());
		dto.setContenuto(entity.getContenuto());
		dto.setFilename(entity.getFilename());
		dto.setIdCvDatiPersonali(entity.getCvDatiPersonali()
				.getIdCvDatiPersonali());

		return dto;
	}

	@Override
	public CvAllegato fromDTO(CvAllegatoDTO dto) {
		if (dto == null)
			return null;
		CvAllegato entity = super.fromDTO(dto);

		entity.setContenuto(dto.getContenuto());
		entity.setFilename(dto.getFilename());
		entity.setCvDatiPersonali(cvDatiPersonaliHome.findById(dto
				.getIdCvDatiPersonali()));

		return entity;
	}

	/**
	 * @param idCurriculum - id del cv a cui cancellare i file allegati
	 * @return numero di file allegati cancellati
	 */
	public int removeByCurriculumId(Integer idCurriculum) {
		Query q = entityManager
				.createQuery("DELETE FROM CvAllegato cva WHERE cva.cvDatiPersonali.idCvDatiPersonali = :idCurriculum");
		q.setParameter("idCurriculum", idCurriculum);
		int deleted = q.executeUpdate();
		return deleted;
	}
	
	
	private List<CvAllegato> findByCurriculumId(Integer idCurriculum) {
		List<CvAllegato> result;
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<CvAllegato> query = cb.createQuery(CvAllegato.class);
		Root<CvAllegato> f = query.from(CvAllegato.class);
		Join<CvAllegato, CvDatiPersonali> cvDatiPersonali = f
				.join(CvAllegato_.cvDatiPersonali);
		Predicate equalPredicate = cb.equal(
				cvDatiPersonali.get(CvDatiPersonali_.idCvDatiPersonali),
				idCurriculum);
		query.where(equalPredicate);
		// query.orderBy(cb.desc(f.get(CvDatiPersonali_.dtmMod)));
		
		TypedQuery<CvAllegato> typedQuery = entityManager.createQuery(query);
		
		result = typedQuery.getResultList();
		
		return result;
	}
	
	/**
	 * 
	 * carica i file allegati al CV contenuto compreso<br>
	 * NB attualmente si consente 1 solo allegato x CV
	 * @param idCurriculum
	 * @return
	 */
	public CvAllegatoDTO findDTOByCurriculumId(Integer idCurriculum) {
		CvAllegato cvAllegato = findEntityByCurriculumId(idCurriculum);
		if (cvAllegato == null) {
			return null;
		}
		return toDTO(cvAllegato);
	}

	/**
	 * @param idCurriculum
	 * @return
	 */
	public CvAllegato findEntityByCurriculumId(Integer idCurriculum) {
		List<CvAllegato> l = findByCurriculumId(idCurriculum);
		if (l == null || l.isEmpty()) {
			return null;
		}
		CvAllegato cvAllegato = l.get(0);
		return cvAllegato;
	}

	/**
	 * carica solo id e nome dei file allegati al CV - non il contenuto<br>
	 * NB attualmente si consente 1 solo allegato x CV
	 * 
	 * @param idCurriculum
	 * @return lista
	 */
	public List<CvAllegatoDTO> findLightDtoByCurriculumId(Integer idCurriculum) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tuple> query = cb.createTupleQuery();
		Root<CvAllegato> f = query.from(CvAllegato.class);
		Path<String> pFileName = f.get(CvAllegato_.filename);
		Path<Integer> pIdCvAllegatoName = f.get(CvAllegato_.idCvAllegato);
//		Path<CvDatiPersonali> pCvDatiPersonali = f
//				.get(CvAllegato_.cvDatiPersonali);
		query.multiselect(pFileName, pIdCvAllegatoName);
//		query.where(cb.equal(
//				pCvDatiPersonali.get(CvDatiPersonali_.idCvDatiPersonali),
//				idCurriculum));
		Join<CvAllegato, CvDatiPersonali> cvDatiPersonali = f
				.join(CvAllegato_.cvDatiPersonali);
		Predicate equalPredicate = cb.equal(
				cvDatiPersonali.get(CvDatiPersonali_.idCvDatiPersonali),
				idCurriculum);
		query.where(equalPredicate);
		List<Tuple> list = entityManager.createQuery(query).getResultList();
		List<CvAllegatoDTO> ldto = new ArrayList<CvAllegatoDTO>();
		if (list == null) {
			return ldto;
		}
		for (Tuple tuple : list) {
			CvAllegatoDTO dto = new CvAllegatoDTO();
			dto.setId(tuple.get(pIdCvAllegatoName));
			dto.setFilename(tuple.get(pFileName));
			dto.setIdCvDatiPersonali(idCurriculum);
			ldto.add(dto);
		}
		return ldto;
	}


	public CvAllegato persistAllegato(CvDatiPersonali entity, CvAllegato cvAllegato, Integer actingUser) {
		PfPrincipal usr = pfPrincipalHome.findById(actingUser);
		cvAllegato.setCvDatiPersonali(entity);
		cvAllegato.setDtmIns(new Date());
		cvAllegato.setDtmMod(new Date());
		cvAllegato.setPfPrincipalIns(usr);
		cvAllegato.setPfPrincipalMod(usr);
		entityManager.persist(cvAllegato);
		entityManager.flush();// forza insert, per avere ID
		return cvAllegato;
	}

	public CvAllegato merge(CvAllegato entity, Integer actingUser) {
		PfPrincipal usr = pfPrincipalHome.findById(actingUser);

		entity.setDtmMod(new Date());
		entity.setPfPrincipalMod(usr);
		//entity.setPfPrincipalIns(usr);
		return entityManager.merge(entity);
	}


}
