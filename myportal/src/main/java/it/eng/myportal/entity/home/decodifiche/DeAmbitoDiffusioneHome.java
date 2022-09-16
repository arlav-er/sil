package it.eng.myportal.entity.home.decodifiche;

// Generated 1-set-2011 12.32.46 by Hibernate Tools 3.4.0.CR1

import it.eng.myportal.dtos.DeAmbitoDiffusioneDTO;
import it.eng.myportal.entity.decodifiche.DeAmbitoDiffusione;
import it.eng.myportal.utils.ConstantsSingleton;

import javax.ejb.Stateless;

/**
 * Home object for domain model class DeAmbitoDiffusione.
 * 
 * @see it.eng.myportal.entity.decodifiche.DeAmbitoDiffusione
 * @author Turrini.
 */
@Stateless
public class DeAmbitoDiffusioneHome extends AbstractSuggestibleHome<DeAmbitoDiffusione, DeAmbitoDiffusioneDTO> {

	private static final String SELECT_ITEMS_QUERY = "SELECT new javax.faces.model.SelectItem(t.codAmbitoDiffusione,t.descrizione)"
	        + " from DeAmbitoDiffusione t";

	public DeAmbitoDiffusione findById(final String id) {
		return findById(DeAmbitoDiffusione.class, id);
	}

	@Override
	public DeAmbitoDiffusioneDTO toDTO(DeAmbitoDiffusione entity) {
		if (entity == null)
			return null;
		DeAmbitoDiffusioneDTO dto = super.toDTO(entity);
		dto.setId(entity.getCodAmbitoDiffusione());
		dto.setDescrizione(entity.getDescrizione());
		return dto;
	}

	@Override
	public DeAmbitoDiffusione fromDTO(DeAmbitoDiffusioneDTO dto) {
		if (dto == null)
			return null;
		final DeAmbitoDiffusione entity = super.fromDTO(dto);
		entity.setCodAmbitoDiffusione(dto.getId());
		entity.setDescrizione(dto.getDescrizione());
		return entity;
	}

	public String getSelectItemQuery() {
		return SELECT_ITEMS_QUERY;
	}

	/**
	 * Restituisce true se il codice di diffusione passato indica una
	 * sincronizzazione con cliclavoro, false altrimenti
	 * 
	 * @param codDeAmbitoDiffusione
	 * @return
	 */
	public boolean isSincronizzatoClicLavoro(String codDeAmbitoDiffusione) {
		if (codDeAmbitoDiffusione != null && !codDeAmbitoDiffusione.isEmpty()) {
			if (codDeAmbitoDiffusione.equals(ConstantsSingleton.DeAmbitoDiffusione.NAZIONALE)
			        || codDeAmbitoDiffusione.equals(ConstantsSingleton.DeAmbitoDiffusione.EUROPEO)) {
				return true;
			}
		}

		return false;
	}
}
