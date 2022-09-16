package it.eng.myportal.entity.home.decodifiche;

import it.eng.myportal.dtos.DeTurnoDTO;
import it.eng.myportal.entity.decodifiche.sil.DeTurnoSil;

import javax.ejb.Stateless;

@Stateless
public class DeTurnoSilHome extends AbstractSuggestibleHome<DeTurnoSil, DeTurnoDTO> {

	@Override
	public DeTurnoSil findById(String id) {
		return findById(DeTurnoSil.class, id);
	}

	@Override
	public DeTurnoDTO toDTO(DeTurnoSil turno) {
		if (turno == null)
			return null;
		DeTurnoDTO ret = new DeTurnoDTO();
		ret.setDtInizioVal(turno.getDtInizioVal());
		ret.setDtFineVal(turno.getDtFineVal());
		ret.setId(turno.getCodTurnoSil());
		ret.setDescrizione(turno.getDescrizione());
		return ret;
	}

	@Override
	public DeTurnoSil fromDTO(final DeTurnoDTO turno) {
		if (turno == null)
			return null;
		DeTurnoSil ret = super.fromDTO(turno);
		ret.setCodTurnoSil(turno.getId());
		ret.setDescrizione(turno.getDescrizione());
		return ret;
	}

	// @Override
	// public List<SelectItem> getListItems(boolean addBlank) {
	// List<SelectItem> selectItems = new ArrayList<SelectItem>();
	//
	// try {
	// final TypedQuery<DeTurnoSil> typedQuery = entityManager.createQuery("SELECT a FROM DeTurnoSil a",
	// DeTurnoSil.class);
	// List<DeTurnoSil> res = typedQuery.getResultList();
	// if (addBlank)
	// selectItems.add(0, new SelectItem(null, ""));
	// for (DeTurnoSil deTurnoSil : res) {
	// DeTurnoDTO dto = toDTO(deTurnoSil);
	// selectItems.add(new SelectItem(dto, deTurnoSil.getDescrizione()));
	// }
	// } catch (NoResultException nre) {
	// log.error("No result found for type SelectItem using this statement");
	// } catch (RuntimeException re) {
	// log.error("Cannot find results for type SelectItem using statement: ");
	// }
	// return selectItems;
	// }
}
