package it.eng.myportal.entity.home;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;

import it.eng.myportal.dtos.VaTurnoDTO;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaTurno;
import it.eng.myportal.entity.home.decodifiche.DeTurnoHome;
import it.eng.myportal.entity.home.decodifiche.DeTurnoSilHome;

/**
 * @author Rodi A.
 */
@Stateless
public class VaTurnoHome extends AbstractVacancyEntityListHome<VaTurno, VaTurnoDTO> {

	@EJB
	DeTurnoHome deTurnoHome;

	@EJB
	DeTurnoSilHome deTurnoSilHome;

	public VaTurno findById(Integer id) {
		return findById(VaTurno.class, id);
	}

	/**
	 * Recupera i dati partendo dall'id del curriculum
	 * 
	 * @param vacancyId
	 *            int
	 * @return Set<VaIstruzione>
	 */
	public Set<VaTurno> findByVacancyId(int vacancyId) {
		return vaDatiVacancyHome.findById(vacancyId).getVaTurnos();
	}

	/**
	 * Converte un'Entity in un DTO.
	 * 
	 * @param entity
	 *            VaIstruzione
	 * @return VaIstruzioneDTO
	 */
	@Override
	public VaTurnoDTO toDTO(VaTurno entity) {
		if (entity == null)
			return null;
		VaTurnoDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdVaTurno());
		if (entity.getDeTurno() != null) {
			dto.setCodice(entity.getDeTurno().getCodTurno());
			dto.setDescrizione(entity.getDeTurno().getDescrizione());
		} else if (entity.getDeTurnoSil() != null) {
			dto.setCodice(entity.getDeTurnoSil().getCodTurnoSil());
			dto.setDescrizione(entity.getDeTurnoSil().getDescrizione());
		}

		return dto;
	}

	/**
	 * Genera l'Entity a partire dal DTO.
	 * 
	 * @param dto
	 *            VaIstruzioneDTO
	 * @return VaIstruzione
	 */
	@Override
	public VaTurno fromDTO(VaTurnoDTO dto) {

		if (dto == null)
			return null;
		VaTurno entity = super.fromDTO(dto);
		entity.setIdVaTurno(dto.getId());
		entity.setVaDatiVacancy(vaDatiVacancyHome.findById(dto.getIdVaDatiVacancy()));
		if (VaDatiVacancy.OpzTipoDecodifiche.SIL.equals(entity.getVaDatiVacancy().getOpzTipoDecodifiche())) {
			entity.setDeTurnoSil(deTurnoSilHome.findById(dto.getCodice()));
		} else {
			entity.setDeTurno(deTurnoHome.findById(dto.getCodice()));
		}

		return entity;
	}

	public void copyById(Integer pfPrincipalId, VaTurno vaTurno, VaDatiVacancy vaDatiVacancy) {
		if (vaTurno != null) {
			Date now = new Date();
			entityManager.detach(vaTurno);
			vaTurno.setIdVaTurno(null);
			vaTurno.setVaDatiVacancy(vaDatiVacancy);
			vaTurno.setDtmIns(now);
			vaTurno.setDtmMod(now);
			vaTurno.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
			vaTurno.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));
			persist(vaTurno);
		} else {
			throw new EJBException(
					"Impossibile trovare l'entity corrispondente a va_turno associato a va_dati_vacancy con id "
							+ vaDatiVacancy.getIdVaDatiVacancy());
		}

	}

	public List<VaTurno> findProperByVacancyId(Integer vacancyId) {
		List<VaTurno> deQualificaSrqList = entityManager.createNamedQuery("VaTurnoFindByVacancyId", VaTurno.class)
				.setParameter("vaId", vacancyId).getResultList();

		return deQualificaSrqList;
	}
}
