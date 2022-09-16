package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.DeStatoInvioClDTO;
import it.eng.myportal.dtos.VaVacancyClDTO;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.VaVacancyCl;
import it.eng.myportal.entity.decodifiche.DeStatoInvioCl;
import it.eng.myportal.entity.ejb.ClicLavoroEjb;
import it.eng.myportal.entity.home.decodifiche.DeMotivoChiusuraHome;
import it.eng.myportal.entity.home.decodifiche.DeProvenienzaHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoInvioClHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoComunicazioneClHome;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.apache.commons.lang3.time.DateUtils;

/**
 * @author Enrico D'Angelo
 */
@Stateless
public class VaVacancyClHome extends AbstractUpdatableHome<VaVacancyCl, VaVacancyClDTO> {

	@EJB
	DeStatoInvioClHome deStatoInvioClHome;

	@EJB
	DeTipoComunicazioneClHome deTipoComunicazioneClHome;

	@EJB
	VaDatiVacancyHome vaDatiVacancyHome;

	@EJB
	DeProvenienzaHome deProvenienzaHome;

	@EJB
	DeMotivoChiusuraHome deMotivoChiusuraHome;

	@EJB
	ClicLavoroEjb clicLavoroEjb;

	public VaVacancyCl findById(Integer id) {
		return findById(VaVacancyCl.class, id);
	}

	@Override
	public VaVacancyClDTO toDTO(VaVacancyCl entity) {
		if (entity == null) {
			return null;
		}
		VaVacancyClDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdVaDatiVacancy());
		dto.setCodComunicazione(entity.getCodComunicazione());
		dto.setCodComunicazionePrec(entity.getCodComunicazionePrec());
		DeStatoInvioCl deStatoInvioCl = entity.getDeStatoInvioCl();
		if (deStatoInvioCl != null) {
			dto.setDeStatoInvioCl(deStatoInvioClHome.findDTOById(deStatoInvioCl.getCodStatoInvioCl()));
		} else {
			dto.setDeStatoInvioCl(null);
		}
		dto.setDeTipoComunicazioneCl(deTipoComunicazioneClHome.toDTO(entity.getDeTipoComunicazioneCl()));
		dto.setDtInvio(entity.getDtInvio());

		return dto;
	}

	@Override
	public VaVacancyCl fromDTO(VaVacancyClDTO dto) {
		if (dto == null) {
			return null;
		}
		VaVacancyCl entity = super.fromDTO(dto);

		entity.setIdVaDatiVacancy(dto.getId());
		entity.setVaDatiVacancy(vaDatiVacancyHome.findById(dto.getId()));
		entity.setCodComunicazione(dto.getCodComunicazione());
		entity.setCodComunicazionePrec(dto.getCodComunicazionePrec());
		DeStatoInvioClDTO deStatoInvioClDTO = dto.getDeStatoInvioCl();
		if (deStatoInvioClDTO != null) {
			entity.setDeStatoInvioCl(deStatoInvioClHome.findById(deStatoInvioClDTO.getId()));
		}
		entity.setDeTipoComunicazioneCl(deTipoComunicazioneClHome.findById(dto.getDeTipoComunicazioneCl().getId()));
		entity.setDtInvio(dto.getDtInvio());

		return entity;
	}

	public void copyById(Integer pfPrincipalId, VaVacancyCl vaVacancyCl, VaDatiVacancy vaDatiVacancy) {
		if (vaVacancyCl != null) {
			Date now = new Date();
			entityManager.detach(vaVacancyCl);
			vaVacancyCl.setIdVaDatiVacancy(vaDatiVacancy.getIdVaDatiVacancy());
			vaVacancyCl.setVaDatiVacancy(vaDatiVacancy);
			vaVacancyCl.setDtmIns(now);
			vaVacancyCl.setDtmMod(now);
			vaVacancyCl.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
			vaVacancyCl.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));
			persist(vaVacancyCl);
		} else {
			throw new EJBException(
					"Impossibile trovare l'entity corrispondente a vaVacancyCL associato a va_dati_vacancyi con id "
							+ vaDatiVacancy.getIdVaDatiVacancy());
		}
	}

	/**
	 * Restituisce true se la comunicazione di chiusura della VA su cliclavoro
	 * e' gia' stata confermata da parte di cliclavoro.
	 * 
	 * @param idCvDatiPersonali
	 * @return
	 */
	public boolean isChiusuraInviata(VaVacancyCl vaVacancyCl) {
		if (vaVacancyCl != null) {
			DeStatoInvioCl deStatoInvioCl = vaVacancyCl.getDeStatoInvioCl();

			if (deStatoInvioCl != null
					&& deStatoInvioCl.getCodStatoInvioCl().equals(ConstantsSingleton.DeStatoInvioCl.CHIUSURA_INVIATA)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Restituisce true se la vacancy si trova in uno stato di chiusura ma non
	 * di chiusura inviata, cioe' CA o CE.
	 * 
	 * @param cvCandidaturaCl
	 * @return
	 */
	public boolean isChiusuraNonInviata(VaVacancyCl vaVacancyCl) {
		if (vaVacancyCl != null) {
			DeStatoInvioCl deStatoInvioCl = vaVacancyCl.getDeStatoInvioCl();

			if (deStatoInvioCl != null
					&& (deStatoInvioCl.getCodStatoInvioCl().equals(
							ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_INVIO_CHIUSURA) || deStatoInvioCl
							.getCodStatoInvioCl().equals(ConstantsSingleton.DeStatoInvioCl.CHIUSURA_INVIATA_ERRORE))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Restituisce true se la vacancy si trova in uno stato di primo invio a
	 * cliclavoro ma non di primo invio inviato, cioe' PA o PE
	 * 
	 * @param cvCandidaturaCl
	 * @return
	 */
	public boolean isPrimoInvioNonInviato(VaVacancyCl vaVacancyCl) {
		if (vaVacancyCl != null) {
			if (vaVacancyCl.getDeStatoInvioCl().getCodStatoInvioCl()
					.equals(ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_PRIMO_INVIO)
					|| vaVacancyCl.getDeStatoInvioCl().getCodStatoInvioCl()
							.equals(ConstantsSingleton.DeStatoInvioCl.PRIMO_INVIO_COMPLETATO_ERRORE)) {
				return true;
			}
		}
		return false;
	}

	public VaVacancyCl updateVacancy(Integer idPfPrincipal, Integer idVaDatiVacancy, VaVacancyCl vaVacancyCl,
			ConstantsSingleton.AzioneCliclavoro azione) {
		VaDatiVacancy vaDatiVacancy = vaDatiVacancyHome.findById(idVaDatiVacancy);
		if (vaVacancyCl == null) {
			vaVacancyCl = new VaVacancyCl();
			vaVacancyCl.setVaDatiVacancy(vaDatiVacancy);
		}
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(idPfPrincipal);
		String codStatoInvioClCorrente = vaVacancyCl.getDeStatoInvioCl().getCodStatoInvioCl();
		Date now = new Date();

		String codStatoInvioClNuovo = deStatoInvioClHome.calcolaStatoInvioCL(codStatoInvioClCorrente, azione);

		/* la scadenza della vacancy viene settata a 60 gg da oggi */
		vaDatiVacancy.setDtScadenza(DateUtils.addDays(now, 60));

		/* aggiorno lo stato di invio */
		vaVacancyCl.setDeStatoInvioCl(deStatoInvioClHome.findById(codStatoInvioClNuovo));

		/* aggiorno la data di invio */
		vaVacancyCl.setDtInvio(now);

		/* se lo stato e' cambiato devo aggiornare i codici comunicazione */
		if (!codStatoInvioClNuovo.equals(codStatoInvioClCorrente)) {
			/* mando un messaggio di primo invio */
			if (codStatoInvioClNuovo.equals(ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_PRIMO_INVIO)) {
				/* modifico solo se NON passo dallo stato PE allo stato PA */
				if (!ConstantsSingleton.DeStatoInvioCl.PRIMO_INVIO_COMPLETATO_ERRORE.equals(codStatoInvioClCorrente)) {
					/* aggiorno i cod_comunicazione */
					String codComunicazione = clicLavoroEjb.calcolaNuovoCodComunicazioneInvioVacancy();
					vaVacancyCl.setCodComunicazione(codComunicazione);
					vaVacancyCl.setCodComunicazionePrec(null);

					vaVacancyCl.setPfPrincipalIns(pfPrincipal);
					vaVacancyCl.setDtmIns(now);

					/*
					 * nel caso sia la prima volta che invio la VA, non un
					 * ripristino
					 */
					if (codStatoInvioClCorrente == null || codStatoInvioClCorrente.isEmpty()) {
						vaDatiVacancy.setDeProvenienzaVacancy(deProvenienzaHome
								.findById(ConstantsSingleton.DeProvenienza.COD_MYPORTAL));
					}
				}
			} else {
				/* aggiorno cod_comunicazione_prec sse non presente */
				if (vaVacancyCl.getCodComunicazionePrec() == null) {
					vaVacancyCl.setCodComunicazionePrec(vaVacancyCl.getCodComunicazione());
				}

				/* aggiorno cod_comunicazione */
				String codComunicazione;
				if (codStatoInvioClNuovo.equals(ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_INVIO_CHIUSURA)) {
					codComunicazione = clicLavoroEjb.calcolaNuovoCodComunicazioneChiusuraVacancy();
				} else {
					codComunicazione = clicLavoroEjb.calcolaNuovoCodComunicazioneInvioVacancy();
				}

				vaVacancyCl.setCodComunicazione(codComunicazione);
			}
		}

		// se lo stato della candidatura è relativo ad una chiusura imposta
		// anche la tipologia di comunicazione appropriata
		if (ConstantsSingleton.DeStatoInvioCl.CHIUSURA_INVIATA.equals(codStatoInvioClNuovo)
				|| ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_INVIO_CHIUSURA.equals(codStatoInvioClNuovo)
				|| ConstantsSingleton.DeStatoInvioCl.CHIUSURA_INVIATA_ERRORE.equals(codStatoInvioClNuovo)) {
			/* la comunicazione e' di tipo CHIUSURA */
			vaVacancyCl.setDeTipoComunicazioneCl(deTipoComunicazioneClHome
					.findById(ConstantsSingleton.DeTipoComunicazioneCl.CHIUSURA));
			/* aggiorno il motivo chiusura della VA */
			vaDatiVacancy.setMotivoChiusura(deMotivoChiusuraHome
					.findById(ConstantsSingleton.DeMotivoChiusura.RITIRO_OFFERTA));
		} else {
			/* la comunicazione e' di tipo INVIO */
			vaVacancyCl.setDeTipoComunicazioneCl(deTipoComunicazioneClHome
					.findById(ConstantsSingleton.DeTipoComunicazioneCl.INVIO));

			/* se non e' un messaggio di chiusura cancello il motivo chiusura */
			vaDatiVacancy.setMotivoChiusura(null);
		}

		vaVacancyCl.setPfPrincipalMod(pfPrincipal);
		vaVacancyCl.setDtmMod(now);

		return vaVacancyCl;
	}

	public VaVacancyCl findByCodComunicazione(String codComunicazione) {
		try {
			VaVacancyCl comunicazione = entityManager
					.createNamedQuery("findVacancyByCodComunicazione", VaVacancyCl.class)
					.setParameter("codComunicazione", codComunicazione).getSingleResult();
			if (comunicazione != null)
				return comunicazione;
			else
				return null;
		} catch (Exception e) {
			return null;
		}

	}

	public VaVacancyCl findByCodComunicazionePrec(String codComunicazione) throws MyPortalException {
		try {
			VaVacancyCl comunicazione = entityManager
					.createNamedQuery("findVacancyByCodComunicazionePrec", VaVacancyCl.class)
					.setParameter("codComunicazione", codComunicazione).getSingleResult();
			return comunicazione;
		} catch (NoResultException e) {
			return null;
		}

	}
}
