package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeTurnoDTO;
import it.eng.myportal.entity.decodifiche.DeTurno;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

/**
 *
 * @author Rodi A.
 */
@Stateless
public class DeTurnoHome extends AbstractSuggestibleHome<DeTurno, DeTurnoDTO> {

	public DeTurno findById(final String id) {
		return findById(DeTurno.class, id);
	}

	@Override
	public DeTurnoDTO toDTO(DeTurno turno) {
		if (turno == null) return null;
		DeTurnoDTO ret = super.toDTO(turno);
		ret.setId(turno.getCodTurno());
		ret.setDescrizione(turno.getDescrizione());		
		return ret;
	}

	@Override
	public DeTurno fromDTO(final DeTurnoDTO turno) {
		if (turno == null) return null;
		DeTurno ret = super.fromDTO(turno);
		ret.setCodTurno(turno.getId());
		ret.setDescrizione(turno.getDescrizione());		
		return ret;
	}

	
	/**
	 * Metodo per ottenere una lista di SelectItem a partire da una query
	 * generica. Questo metodo non è esposto nelle interfaccie per evitare che
	 * venga usato impropriamente.
	 *
	 * @param query
	 *            String da utilizzare per la ricerca delle opzioni;
	 * @param addBlank
	 *            booleano per aggiungere una riga bianca in cima alle opzioni;
	 * @return lista delle opzioni per un elemento ti tipo select
	 * @see it.eng.myportal.entity.home.local.IDecodeHome#getListItems(java.lang.String,boolean)
	 */
	public List<SelectItem> getListItems(boolean addBlank) {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		try {
			final TypedQuery<DeTurno> typedQuery = entityManager.createQuery("SELECT a FROM DeTurno a", DeTurno.class);
			List<DeTurno> res = typedQuery.getResultList();
			if (addBlank)
				selectItems.add(0, new SelectItem(null, ""));
			for (DeTurno deTurno : res) {
				selectItems.add(new SelectItem(toDTO(deTurno),deTurno.getDescrizione()));
			}

		} catch (NoResultException nre) {
			log.error("No result found for type SelectItem using this statement");

		} catch (RuntimeException re) {
			log.error("Cannot find results for type SelectItem using statement: ");
		}
		return selectItems;
	}

}
