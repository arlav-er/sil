package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.VaAgevolazioneDTO;
import it.eng.myportal.entity.VaAgevolazione;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.home.decodifiche.DeAgevolazioneHome;
import it.eng.myportal.entity.home.decodifiche.DeAgevolazioneSilHome;

import java.util.Date;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;

/**
 * @author Rodi A.
 */
@Stateless
public class VaAgevolazioneHome extends AbstractVacancyEntityListHome<VaAgevolazione, VaAgevolazioneDTO> {

	@EJB
	DeAgevolazioneHome deAgevolazioneHome;

	@EJB
	DeAgevolazioneSilHome deAgevolazioneSilHome;

	public VaAgevolazione findById(Integer id) {
		return findById(VaAgevolazione.class, id);
	}

	/**
	 * Recupera i dati partendo dall'id del curriculum
	 * 
	 * @param vacancyId
	 *            int
	 * @return Set<VaIstruzione>
	 */
	public Set<VaAgevolazione> findByVacancyId(int vacancyId) {
		return vaDatiVacancyHome.findById(vacancyId).getVaAgevolaziones();
	}

	/**
	 * Converte un'Entity in un DTO.
	 * 
	 * @param entity
	 *            VaIstruzione
	 * @return VaIstruzioneDTO
	 */
	@Override
	public VaAgevolazioneDTO toDTO(VaAgevolazione entity) {
		if (entity == null)
			return null;
		VaAgevolazioneDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdVaAgevolazione());
		// dto.setIdVaDatiVacancy(entity.getVaDatiVacancy().getIdVaDatiVacancy());
		dto.setOpzIndispensabile(entity.getOpzIndispensabile());

		if (entity.getDeAgevolazione() != null) {
			dto.setCodice(entity.getDeAgevolazione().getCodAgevolazione());
			dto.setDescrizione(entity.getDeAgevolazione().getDescrizione());
		} else if (entity.getDeAgevolazioneSil() != null) {
			dto.setCodice(entity.getDeAgevolazioneSil().getCodAgevolazioneSil());
			dto.setDescrizione(entity.getDeAgevolazioneSil().getDescrizione());
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
	public VaAgevolazione fromDTO(VaAgevolazioneDTO dto) {
		if (dto == null)
			return null;
		VaAgevolazione entity = super.fromDTO(dto);
		entity.setVaDatiVacancy(vaDatiVacancyHome.findById(dto.getIdVaDatiVacancy()));
		entity.setIdVaAgevolazione(dto.getId());
		entity.setOpzIndispensabile(dto.getOpzIndispensabile());
		if (VaDatiVacancy.OpzTipoDecodifiche.SIL.equals(entity.getVaDatiVacancy().getOpzTipoDecodifiche())) {
			entity.setDeAgevolazioneSil(deAgevolazioneSilHome.findById(dto.getCodice()));
		} else {
			entity.setDeAgevolazione(deAgevolazioneHome.findById(dto.getCodice()));
		}

		return entity;
	}

	public void copyById(Integer pfPrincipalId, VaAgevolazione vaAgevolazione, VaDatiVacancy vaDatiVacancy) {
		if (vaAgevolazione != null) {
			Date now = new Date();
			entityManager.detach(vaAgevolazione);
			vaAgevolazione.setIdVaAgevolazione(null);
			vaAgevolazione.setVaDatiVacancy(vaDatiVacancy);
			vaAgevolazione.setDtmIns(now);
			vaAgevolazione.setDtmMod(now);
			vaAgevolazione.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
			vaAgevolazione.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));
			persist(vaAgevolazione);
		} else {
			throw new EJBException(
					"Impossibile trovare l'entity corrispondente a va_agevolazione associato a va_dati_vacancy con id "
							+ vaDatiVacancy.getIdVaDatiVacancy());
		}
	}
}
