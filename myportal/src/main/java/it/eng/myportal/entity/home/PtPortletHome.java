package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.PtPortletDTO;
import it.eng.myportal.entity.PtPortlet;
import it.eng.myportal.entity.home.decodifiche.DeRuoloPortaleHome;
import it.eng.myportal.utils.ConstantsSingleton;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

/**
 * 
 * @author Rodi A.
 *
 */
@Stateless
public class PtPortletHome extends AbstractUpdatableHome<PtPortlet, PtPortletDTO> {

	@EJB
	private DeRuoloPortaleHome deRuoloPortaleHome;

	@Override
	public PtPortlet findById(Integer id) {
		return findById(PtPortlet.class, id);
	}

	@Override
	public PtPortletDTO toDTO(PtPortlet entity) {
		PtPortletDTO dto = null;
		if (entity != null) {
			dto = new PtPortletDTO();
			dto.setId(entity.getIdPtPortlet());
			dto.setNome(entity.getNome());
			dto.setDescrizione(entity.getDescrizione());
			dto.setCodRuoloPortale(entity.getDeRuoloPortale().getCodRuoloPortale());
		} else {
			log.warn("Entity PtPortlet null");
		}
		return dto;
	}

	@Override
	public PtPortlet fromDTO(PtPortletDTO dto) {
		PtPortlet entity = null;
		if (dto != null) {
			entity = new PtPortlet();
			entity.setIdPtPortlet(dto.getId());
			entity.setNome(dto.getNome());
			entity.setDescrizione(dto.getDescrizione());
			entity.setDeRuoloPortale(deRuoloPortaleHome.findById(dto.getCodRuoloPortale()));
		} else {
			log.warn("DTO PtPortletDTO null");
		}
		return entity;
	}

	public List<PtPortlet> findByCodRuoloPortale(String codRuoloPortale) {
		// Per gli utenti di tipo CPI, prendo le portlet della provincia.
		if (ConstantsSingleton.DeRuoloPortale.CPI.equals(codRuoloPortale)) {
			codRuoloPortale = ConstantsSingleton.DeRuoloPortale.PROVINCIA;
		}

		TypedQuery<PtPortlet> query = entityManager.createNamedQuery("findPtPortletByCodRuoloPortale", PtPortlet.class);
		query.setParameter("codRuoloPortale", codRuoloPortale);
		return query.getResultList();
	}

	public List<PtPortletDTO> findDTOByCodRuoloPortale(String codRuoloPortale) {
		List<PtPortlet> entityList = findByCodRuoloPortale(codRuoloPortale);
		List<PtPortletDTO> dtoList = new ArrayList<PtPortletDTO>();
		for (PtPortlet entity : entityList) {
			dtoList.add(toDTO(entity));
		}

		return dtoList;
	}

}
