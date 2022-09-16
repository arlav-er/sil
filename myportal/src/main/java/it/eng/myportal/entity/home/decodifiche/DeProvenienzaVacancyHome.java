package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeProvenienzaVacancyDTO;
import it.eng.myportal.entity.decodifiche.DeProvenienzaVacancy;

import javax.ejb.Stateless;

/**
 *
 * @author Rodi A.
 */
@Stateless
public class DeProvenienzaVacancyHome extends AbstractSuggestibleHome<DeProvenienzaVacancy, DeProvenienzaVacancyDTO> {

	public DeProvenienzaVacancy findById(final String id) {
		return findById(DeProvenienzaVacancy.class, id);
	}

	@Override
	public DeProvenienzaVacancyDTO toDTO(DeProvenienzaVacancy provenienzaVacancy) {
		if (provenienzaVacancy == null) return null;
		DeProvenienzaVacancyDTO ret = super.toDTO(provenienzaVacancy);
/*		ret.setId(provenienzaVacancy.getCodProvenienzaVacancy());
		ret.setDescrizione(provenienzaVacancy.getDescrizione());
*/		return ret;
	}

	@Override
	public DeProvenienzaVacancy fromDTO(final DeProvenienzaVacancyDTO provenienzaVacancy) {
		if (provenienzaVacancy == null) return null;
		DeProvenienzaVacancy ret = super.fromDTO(provenienzaVacancy);
/*		ret.setCodProvenienzaVacancy(provenienzaVacancy.getId());
		ret.setDescrizione(provenienzaVacancy.getDescrizione());	
*/		return ret;
	}
}
