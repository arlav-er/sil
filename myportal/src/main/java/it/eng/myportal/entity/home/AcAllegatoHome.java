package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.AcAllegatoDTO;
import it.eng.myportal.entity.AcAllegato;
import it.eng.myportal.entity.AcAllegato_;
import it.eng.myportal.entity.AcCandidatura;
import it.eng.myportal.entity.AcCandidatura_;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Tuple;
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
public class AcAllegatoHome extends
		AbstractUpdatableHome<AcAllegato, AcAllegatoDTO> {

	@EJB
	CvDatiPersonaliHome cvDatiPersonaliHome;
	@EJB
	AcCandidaturaHome acCandidaturaHome;

	@Override
	public AcAllegato findById(Integer id) {
		return findById(AcAllegato.class, id);
	}

	@Override
	public AcAllegatoDTO toDTO(AcAllegato entity) {
		if (entity == null)
			return null;
		AcAllegatoDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdAcAllegato());
		dto.setContenuto(entity.getContenuto());
		dto.setFilename(entity.getFilename());
		dto.setIdAcCandidatura(entity.getAcCandidatura()
				.getIdAcCandidatura());

		return dto;
	}

	@Override
	public AcAllegato fromDTO(AcAllegatoDTO dto) {
		if (dto == null)
			return null;
		AcAllegato entity = super.fromDTO(dto);

		entity.setContenuto(dto.getContenuto());
		entity.setFilename(dto.getFilename());
		entity.setAcCandidatura(acCandidaturaHome.findById(dto
				.getIdAcCandidatura()));

		return entity;
	}


	private List<AcAllegato> findByCurriculumId(Integer idAcCandidatura) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AcAllegato> query = cb.createQuery(AcAllegato.class);
		Root<AcAllegato> f = query.from(AcAllegato.class);
		Join<AcAllegato, AcCandidatura> cvDatiPersonali = f
				.join(AcAllegato_.acCandidatura);
		Predicate equalPredicate = cb.equal(
				cvDatiPersonali.get(AcCandidatura_.idAcCandidatura),
				idAcCandidatura);
		query.where(equalPredicate);
		// query.orderBy(cb.desc(f.get(CvDatiPersonali_.dtmMod)));
		return entityManager.createQuery(query).getResultList();
	}
	
	/**
	 * 
	 * carica i file allegati al CV contenuto compreso<br>
	 * NB attualmente si consente 1 solo allegato x CV
	 * @param idAcCandidatura
	 * @return
	 */
	public AcAllegatoDTO findDTOByCandidaturaId(Integer idAcCandidatura) {
		List<AcAllegato> l = findByCurriculumId(idAcCandidatura);
		if (l == null || l.isEmpty()) {
			return null;
		}
		AcAllegato cvAllegato = l.get(0);
		return toDTO(cvAllegato);
	}

	/**
	 * carica solo id e nome dei file allegati alla Candidatura - non il contenuto<br>
	 * NB attualmente si consente 1 solo allegato x Candidatura 
	 * 
	 * @param idAcCandidatura
	 * @return lista
	 */
	public List<AcAllegatoDTO> findDTOByAcCandidaturaId(Integer idAcCandidatura) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tuple> query = cb.createTupleQuery();
		Root<AcAllegato> f = query.from(AcAllegato.class);
		Path<String> pFileName = f.get(AcAllegato_.filename);
		Path<Integer> pIdAcAllegato = f.get(AcAllegato_.idAcAllegato);
		query.multiselect(pFileName, pIdAcAllegato);
		Join<AcAllegato,AcCandidatura> cvDatiPersonali = f
				.join(AcAllegato_.acCandidatura);
		Predicate equalPredicate = cb.equal(
				cvDatiPersonali.get(AcCandidatura_.idAcCandidatura),
				idAcCandidatura);
		query.where(equalPredicate);
		List<Tuple> list = entityManager.createQuery(query).getResultList();
		List<AcAllegatoDTO> ldto = new ArrayList<AcAllegatoDTO>();
		if (list == null) {
			return ldto;
		}
		for (Tuple tuple : list) {
			AcAllegatoDTO dto = new AcAllegatoDTO();
			dto.setId(tuple.get(pIdAcAllegato));
			dto.setFilename(tuple.get(pFileName));
			dto.setIdAcCandidatura(idAcCandidatura);
			ldto.add(dto);
		}
		return ldto;
	}

}
