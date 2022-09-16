package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.DeMotivoEtaSilDTO;
import it.eng.myportal.dtos.VaCandidatoDTO;
import it.eng.myportal.dtos.VaEsperienzeDTO;
import it.eng.myportal.dtos.VaPubblicazioneDTO;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaEsperienze;
import it.eng.myportal.entity.VaPubblicazione;
import it.eng.myportal.entity.home.local.IVacancyEntityHome;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Stateless(name = "VaCandidatoHome")
public class VaCandidatoHome implements IVacancyEntityHome<VaCandidatoDTO> {
	protected static Log log = LogFactory.getLog(VaCandidatoHome.class);

	@PersistenceContext
	private EntityManager entityManager;

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;

	@EJB
	VaEsperienzeHome vaEsperienzeHome;

	@EJB
	VaPubblicazioneHome vaPubblicazioneHome;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@Override
	public VaCandidatoDTO findDTOByVacancyId(int vacancyId) {
		VaDatiVacancy vacancy = vaDatiVacancyHome.findById(vacancyId);
		return toDTO(vacancy);
	}

	private VaCandidatoDTO toDTO(VaDatiVacancy vacancy) {
		VaCandidatoDTO dto = new VaCandidatoDTO();
		dto.setIdVaDatiVacancy(vacancy.getIdVaDatiVacancy());

		if (vacancy.getVaPubblicazione() != null) {
			dto.setVaCaratteristiche(vaPubblicazioneHome.toDTO(vacancy.getVaPubblicazione()));
		} else {
			dto.setVaCaratteristiche(new VaPubblicazioneDTO());
		}

		if (vacancy.getVaEsperienze() != null) {
			dto.setVaEsperienze(vaEsperienzeHome.toDTO(vacancy.getVaEsperienze()));
			if (dto.getVaEsperienze().getDeMotivoEtaSil() == null) {
				dto.getVaEsperienze().setDeMotivoEtaSil(new DeMotivoEtaSilDTO());
			}
		} else {
			dto.setVaEsperienze(new VaEsperienzeDTO());
		}

		return dto;
	}

	@Override
	public void removeById(Integer deleteId, Integer idPfPrincipalMod) {
		VaDatiVacancy dati = vaDatiVacancyHome.findById(deleteId);
		dati.setDtmMod(new Date());
		dati.setPfPrincipalMod(pfPrincipalHome.findById(idPfPrincipalMod));
		vaDatiVacancyHome.merge(dati);

		VaPubblicazione pubblicazione = dati.getVaPubblicazione();
		if (pubblicazione != null) {
			entityManager.remove(pubblicazione);
		}

		VaEsperienze esperienze = dati.getVaEsperienze();
		if (esperienze != null) {
			entityManager.remove(esperienze);
		}
	}

	@Override
	public VaCandidatoDTO persistDTO(VaCandidatoDTO data, Integer idPrincipalIns) {
		// TODO Va fatto? Boh, sull'altra home è così.
		return data;
	}

	@Override
	public VaCandidatoDTO mergeDTO(VaCandidatoDTO data, Integer idPrincipalMod) {
		// ESPERIENZE
		VaEsperienze vaEsperienze = vaEsperienzeHome.findByVacancyId(data.getIdVaDatiVacancy());
		if (vaEsperienze == null) {
			vaEsperienze = vaEsperienzeHome.fromDTO(data.getVaEsperienze());
			vaEsperienzeHome.persist(vaEsperienze);
		} else {
			vaEsperienze = vaEsperienzeHome.fillFromDTO(vaEsperienze, data.getVaEsperienze());
			vaEsperienzeHome.merge(vaEsperienze);
		}

		// CONDIZIONI
		VaPubblicazione vaPubblicazione = vaPubblicazioneHome.findByVacancyId(data.getIdVaDatiVacancy());
		if (vaPubblicazione == null) {
			vaPubblicazione = vaPubblicazioneHome.fromDTO(data.getVaCaratteristiche());
			vaPubblicazioneHome.persist(vaPubblicazione);
		} else {
			vaPubblicazione = vaPubblicazioneHome.fillFromDTO(vaPubblicazione, data.getVaCaratteristiche());
			vaPubblicazioneHome.merge(vaPubblicazione);
		}

		return data;
	}
}
