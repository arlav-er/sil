package it.eng.myportal.entity.home.decodifiche;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import it.eng.myportal.dtos.DeAmbienteSilDTO;
import it.eng.myportal.entity.decodifiche.DeAmbienteSil;
import it.eng.myportal.entity.home.AbstractUpdatableHome;

@Stateless
public class DeAmbienteSilHome extends AbstractUpdatableHome<DeAmbienteSil, DeAmbienteSilDTO> {

	private static final String QUERY_SELECT_ITEMS = "select new javax.faces.model.SelectItem(c.idAmbienteSil,c.descrizione ) from DeAmbienteSil c where c.cpi.codCpi = :codCpiParam";

	@EJB
	private DeCpiHome deCpiHome;

	@EJB
	private DeProvinciaHome deProvinciaHome;

	@Override
	public DeAmbienteSil findById(Integer id) {
		return findById(DeAmbienteSil.class, id);
	}

	@Override
	public DeAmbienteSilDTO findDTOById(Integer id) {
		return toDTO(findById(DeAmbienteSil.class, id));
	}

	@Override
	public DeAmbienteSilDTO toDTO(DeAmbienteSil entity) {
		if (entity == null) {
			return null;
		}

		DeAmbienteSilDTO dto = super.toDTO(entity);

		dto.setCpiDTO(deCpiHome.toDTO(entity.getCpi()));
		dto.setDescrizione(entity.getDescrizione());
		dto.setDtInizioVal(entity.getDtInizioVal());
		dto.setDtFineVal(entity.getDtFineVal());
		dto.setId(entity.getIdAmbienteSil());
		dto.setPrgAmbienteSil(entity.getPrgAmbienteSil());
		dto.setProvinciaSilDTO(deProvinciaHome.toDTO(entity.getProvinciaSil()));

		return dto;
	}

	@Override
	public DeAmbienteSil fromDTO(DeAmbienteSilDTO dto) {
		if (dto == null) {
			return null;
		}

		DeAmbienteSil entity = super.fromDTO(dto);

		entity.setCpi(deCpiHome.fromDTO(dto.getCpiDTO()));
		entity.setDescrizione(dto.getDescrizione());
		entity.setDtInizioVal(dto.getDtInizioVal());
		entity.setDtFineVal(dto.getDtFineVal());
		entity.setIdAmbienteSil(dto.getId());
		entity.setPrgAmbienteSil(dto.getPrgAmbienteSil());
		entity.setProvinciaSil(deProvinciaHome.fromDTO(dto.getProvinciaSilDTO()));

		return entity;
	}

	public List<DeAmbienteSilDTO> findDTOByCodCpi(String codCpi) {
		TypedQuery<DeAmbienteSil> query = entityManager.createNamedQuery("findDeAmbienteSilValideByCpi",
				DeAmbienteSil.class);
		query.setHint("org.hibernate.cacheable", true);
		query.setParameter("codCpi", codCpi);
		query.setParameter("now", new Date());
		List<DeAmbienteSil> queryResult = query.getResultList();
		List<DeAmbienteSilDTO> finalResult = new ArrayList<DeAmbienteSilDTO>();
		for (DeAmbienteSil result : queryResult) {
			finalResult.add(toDTO(result));
		}
		return finalResult;
	}

	public List<SelectItem> getListaSelectItem(String codCpi) {
		TypedQuery<SelectItem> typedQuery = entityManager.createQuery(QUERY_SELECT_ITEMS, SelectItem.class).setHint(
				"org.hibernate.cacheable", true);
		typedQuery.setParameter("codCpiParam", codCpi);
		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		try {
			/* elemento vuoto */
			selectItems.add(0, new SelectItem(null, ""));
			selectItems.addAll(typedQuery.getResultList());
		} catch (NoResultException nre) {
			log.error("Non sono stati trovati risultati per il dato SelectItem usando la seguente query: " + typedQuery + " - errore: " + nre.getMessage());

		} catch (RuntimeException re) {
			log.error("Non è possibile trovare risultati per il dato SelectItem usando la seguente query: " + typedQuery + " - errore: " + re.getMessage());
		}
		return selectItems;
	}
}
