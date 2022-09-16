package it.eng.myportal.entity.home.decodifiche;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import it.eng.myportal.dtos.DeTemaConsulenzaDTO;
import it.eng.myportal.entity.decodifiche.DeTemaConsulenza;
import it.eng.myportal.entity.decodifiche.DeTemaConsulenza_;
import it.eng.myportal.utils.ConstantsSingleton;

@Stateless
public class DeTemaConsulenzaHome extends AbstractDecodeHome<DeTemaConsulenza, DeTemaConsulenzaDTO> {
	@Override
	public DeTemaConsulenza findById(String id) {
		return findById(DeTemaConsulenza.class, id);
	}

	public List<SelectItem> getListItems(boolean addBlank, ConstantsSingleton.DeTemaConsulenza.utente utente,
			boolean valide) {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		try {
			List<DeTemaConsulenza> res = this.findAll(utente, valide);
			if (addBlank)
				selectItems.add(0, new SelectItem(null, ""));
			for (DeTemaConsulenza entity : res) {
				DeTemaConsulenzaDTO dto = toDTO(entity);
				selectItems.add(new SelectItem(dto, dto.getDescrizione()));
			}

		} catch (NoResultException nre) {
			log.error("No result found for type SelectItem using this statement");

		} catch (RuntimeException re) {
			log.error("Cannot find results for type SelectItem using statement: ");
		}
		return selectItems;
	}

	public List<DeTemaConsulenza> findAll(ConstantsSingleton.DeTemaConsulenza.utente utente, boolean valide) {

		// Costruisco la base della query.
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<DeTemaConsulenza> query = cb.createQuery(DeTemaConsulenza.class);

		// Creo la clausola FROM della query.
		Root<DeTemaConsulenza> deTemaConsulenza = query.from(DeTemaConsulenza.class);

		// Creo la clausola SELECT della query
		query.select(deTemaConsulenza);

		List<Predicate> whereConditions = new ArrayList<Predicate>();

		whereConditions.add(cb.equal(deTemaConsulenza.get(getFlagUtente(utente)), "Y"));

		if (valide) {
			whereConditions.add(cb.between(cb.currentDate(), deTemaConsulenza.get(DeTemaConsulenza_.dtInizioVal),
					deTemaConsulenza.get(DeTemaConsulenza_.dtFineVal)));
		}

		// Order by descrizione
		query.orderBy(cb.desc(deTemaConsulenza.get(DeTemaConsulenza_.descrizione)));

		// Costruisco la query.
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		TypedQuery<DeTemaConsulenza> tquery = entityManager.createQuery(query);

		List<DeTemaConsulenza> result = tquery.getResultList();

		return result;
	}

	@Override
	public DeTemaConsulenzaDTO toDTO(DeTemaConsulenza entity) {
		if (entity == null) {
			return null;
		}
		DeTemaConsulenzaDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodTema());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DeTemaConsulenza fromDTO(DeTemaConsulenzaDTO dto) {
		if (dto == null) {
			return null;
		}
		final DeTemaConsulenza entity = super.fromDTO(dto);
		entity.setCodTema(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}

	private String getFlagUtente(ConstantsSingleton.DeTemaConsulenza.utente utente) {

		String flagUtente = "";

		switch (utente) {
		case CITTADINO:
			flagUtente = "flagCittadino";
			break;
		case AZIENDA:
			flagUtente = "flagAzienda";
			break;
		default:
			break;
		}

		return flagUtente;
	}
}