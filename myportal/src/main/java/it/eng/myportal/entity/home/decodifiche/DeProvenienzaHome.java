package it.eng.myportal.entity.home.decodifiche;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import it.eng.myportal.dtos.DeProvenienzaDTO;
import it.eng.myportal.entity.decodifiche.DeProvenienza;

/**
 *
 * @author Rodi A.
 */
@Stateless
public class DeProvenienzaHome extends AbstractSuggestibleHome<DeProvenienza, DeProvenienzaDTO> {

	public DeProvenienza findById(final String id) {
		return findById(DeProvenienza.class, id);
	}

	@Override
	public DeProvenienzaDTO toDTO(DeProvenienza provenienzaVacancy) {
		if (provenienzaVacancy == null) return null;
		DeProvenienzaDTO ret = super.toDTO(provenienzaVacancy);
		ret.setId(provenienzaVacancy.getCodProvenienza());
		ret.setDescrizione(provenienzaVacancy.getDescrizione());
		return ret;
	}

	@Override
	public DeProvenienza fromDTO(final DeProvenienzaDTO provenienzaVacancy) {
		if (provenienzaVacancy == null) return null;
		DeProvenienza ret = super.fromDTO(provenienzaVacancy);
		ret.setCodProvenienza(provenienzaVacancy.getId());
		ret.setDescrizione(provenienzaVacancy.getDescrizione());	
		return ret;
	}
	
	public List<DeProvenienza> findByCodMyPortalAndSil(){
		TypedQuery<DeProvenienza> query = entityManager.createNamedQuery(
					"findByCodMyPortalAndSil", DeProvenienza.class);
		return query.getResultList();
	}
}
