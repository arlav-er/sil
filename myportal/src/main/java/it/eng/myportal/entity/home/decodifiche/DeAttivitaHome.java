package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeAttivitaDTO;
import it.eng.myportal.entity.decodifiche.DeAttivita;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;

/**
 * Home object for domain model class DeAttivita.
 *   
 * @see it.eng.myportal.entity.decodifiche.DeAttivita
 * @author Rodi A.
 */
@Stateless
public class DeAttivitaHome extends AbstractTreeableHome<DeAttivita, DeAttivitaDTO> {

	private final static String SQL_SUGGEST_COD_PADRE = "  " + " SELECT t " + " FROM DeAttivita t "
			+ " WHERE t.padre.codAteco = :query " + " ORDER BY  t.descrizione, t.codAteco ";

	private static final String QUERY_RICERCA = "select c from DeAttivita c where current_date between c.dtInizioVal and c.dtFineVal order by c.descrizione";
	private final static String SQL_SUGGEST_COD_PADRE_ORFANI = "  " + " SELECT t " + " FROM DeAttivita t "
			+ " WHERE t.padre IS NULL ORDER BY  t.descrizione, t.codAteco ";

	public DeAttivita findById(final String id) {
		return findById(DeAttivita.class, id);
	}

	@Override
	public DeAttivitaDTO toDTO(final DeAttivita entity) {
		if (entity == null) return null;
		DeAttivitaDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodAteco());
		dto.setDescrizione(entity.getDescrizione());
		if (entity.getPadre() != null) {
			dto.setCodPadre(entity.getPadre().getCodAteco());
		}
		dto.setNumeroFigli(entity.getFigli().size());
		return dto;
	}

	@Override
	public DeAttivita fromDTO(final DeAttivitaDTO dto) {
		if (dto == null) return null;
		DeAttivita entity = super.fromDTO(dto);   
		entity.setCodAteco(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		if (StringUtils.isNotBlank(dto.getCodPadre())) {
			entity.setPadre(findById(dto.getCodPadre()));
		}
		return entity;
	}


	public List<DeAttivita> getAll() {		
		TypedQuery<DeAttivita> query = entityManager.createQuery(QUERY_RICERCA, DeAttivita.class).setHint("org.hibernate.cacheable", true);
		List<DeAttivita> resultList = query.getResultList();		
		return resultList;
	}

	@Override
	public List<DeAttivitaDTO> findByCodPadre(String par) {

		if (par.equals("0")) {
			TypedQuery<DeAttivita> query = entityManager.createQuery(SQL_SUGGEST_COD_PADRE_ORFANI, DeAttivita.class).setHint("org.hibernate.cacheable", true);
			query.setHint("org.hibernate.cacheable", true);
			return findDTOByQuery(query);
		}

		TypedQuery<DeAttivita> query = entityManager.createQuery(SQL_SUGGEST_COD_PADRE, DeAttivita.class).setHint("org.hibernate.cacheable", true);
		query.setHint("org.hibernate.cacheable", true).setParameter("query", par.toUpperCase());
		return findDTOByQuery(query);
	}
}
