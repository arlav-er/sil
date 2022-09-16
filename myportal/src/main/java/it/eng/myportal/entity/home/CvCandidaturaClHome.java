package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.CvCandidaturaClDTO;
import it.eng.myportal.entity.CvCandidaturaCl;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.decodifiche.DeStatoInvioCl;
import it.eng.myportal.entity.ejb.ClicLavoroEjb;
import it.eng.myportal.entity.home.decodifiche.DeAmbitoDiffusioneHome;
import it.eng.myportal.entity.home.decodifiche.DeMotivoChiusuraHome;
import it.eng.myportal.entity.home.decodifiche.DeProvenienzaHome;
import it.eng.myportal.entity.home.decodifiche.DeStatoInvioClHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoComunicazioneClHome;
import it.eng.myportal.utils.ConstantsSingleton;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.apache.commons.lang3.time.DateUtils;

/**
 * @author Turrini, D'Angelo
 */
@Stateless
public class CvCandidaturaClHome extends AbstractUpdatableHome<CvCandidaturaCl, CvCandidaturaClDTO> {

	@EJB
	DeStatoInvioClHome deStatoInvioClHome;

	@EJB
	DeTipoComunicazioneClHome deTipoComunicazioneClHome;

	@EJB
	CvDatiPersonaliHome cvDatiPersonaliHome;

	@EJB
	DeProvenienzaHome deProvenienzaHome;

	@EJB
	DeMotivoChiusuraHome deMotivoChiusuraHome;

	@EJB
	DeAmbitoDiffusioneHome deAmbitoDiffusioneHome;

	@EJB
	ClicLavoroEjb clicLavoroEjb;

	public CvCandidaturaCl findById(Integer id) {
		return findById(CvCandidaturaCl.class, id);
	}

	@Override
	public CvCandidaturaClDTO toDTO(CvCandidaturaCl entity) {
		if (entity == null)
			return null;
		CvCandidaturaClDTO dto = super.toDTO(entity);

		dto.setId(entity.getIdCvDatiPersonali());
		if (entity.getDeStatoInvioCl() != null) {
			dto.setDeStatoInvioCl(deStatoInvioClHome.findDTOById(entity.getDeStatoInvioCl().getCodStatoInvioCl()));
		}
		dto.setDeTipoComunicazioneCl(deTipoComunicazioneClHome.toDTO(entity.getDeTipoComunicazioneCl()));
		dto.setCodComunicazione(entity.getCodComunicazione());
		dto.setCodCandidaturaPrec(entity.getCodComunicazionePrec());
		dto.setDtInvio(entity.getDtInvio());

		return dto;
	}

	@Override
	public CvCandidaturaCl fromDTO(CvCandidaturaClDTO dto) {
		if (dto == null)
			return null;
		CvCandidaturaCl entity = super.fromDTO(dto);

		entity.setIdCvDatiPersonali(dto.getId());
		entity.setCvDatiPersonali(cvDatiPersonaliHome.findById(dto.getId()));
		entity.setCodComunicazione(dto.getCodComunicazione());
		entity.setCodComunicazionePrec(dto.getCodComunicazionePrec());
		entity.setDeStatoInvioCl(deStatoInvioClHome.findById(dto.getDeStatoInvioCl().getId()));
		entity.setDeTipoComunicazioneCl(deTipoComunicazioneClHome.findById(dto.getDeTipoComunicazioneCl().getId()));
		entity.setDtInvio(dto.getDtInvio());

		return entity;
	}

	public void copyById(Integer pfPrincipalId, CvCandidaturaCl cvCandidaturaCl, CvDatiPersonali cvDatiPersonali) {
		if (cvCandidaturaCl != null) {
			Date now = new Date();
			entityManager.detach(cvCandidaturaCl);
			cvCandidaturaCl.setIdCvDatiPersonali(cvDatiPersonali.getIdCvDatiPersonali());
			cvCandidaturaCl.setCvDatiPersonali(cvDatiPersonali);
			cvCandidaturaCl.setDtmIns(now);
			cvCandidaturaCl.setDtmMod(now);
			cvCandidaturaCl.setPfPrincipalIns(pfPrincipalHome.findById(pfPrincipalId));
			cvCandidaturaCl.setPfPrincipalMod(pfPrincipalHome.findById(pfPrincipalId));
			persist(cvCandidaturaCl);
		} else {
			throw new EJBException(
			        "Impossibile trovare l'entity corrispondente a cv_candidatura_cl associato a cv_dati_personali con id "
			                + cvDatiPersonali.getIdCvDatiPersonali());
		}
	}

	public CvCandidaturaCl findByCodComunicazione(String codComunicazione) {
		try {
			CvCandidaturaCl candidatura = entityManager
			        .createNamedQuery("findCandidaturaByCodComunicazione", CvCandidaturaCl.class)
			        .setParameter("codComunicazione", codComunicazione).getSingleResult();
			if (candidatura != null)
				return candidatura;
			else
				return null;
		} catch (Exception e) {
			return null;
		}

	}

	public CvCandidaturaCl findByCodComunicazionePrec(String codComunicazione) {
		try {
			CvCandidaturaCl candidatura = entityManager.createNamedQuery("findCandidaturaByCodComunicazionePrec", CvCandidaturaCl.class).setParameter("codComunicazione", codComunicazione).getSingleResult();		
			return candidatura;			
		} catch(NoResultException e) {
	        return null;
	    }	
		
	}

	/**
	 * Restituisce true se la comunicazione di chiusura del CV su cliclavoro e'
	 * gia' stata confermata da parte di cliclavoro.
	 * 
	 * @param cvCandidaturaCl
	 * @return
	 */
	public boolean isChiusuraInviata(CvCandidaturaCl cvCandidaturaCl) {
		if (cvCandidaturaCl != null) {
			DeStatoInvioCl deStatoInvioCl = cvCandidaturaCl.getDeStatoInvioCl();

			if (deStatoInvioCl != null
			        && deStatoInvioCl.getCodStatoInvioCl().equals(ConstantsSingleton.DeStatoInvioCl.CHIUSURA_INVIATA)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Restituisce true se il CV si trova in uno stato di chiusura ma non di
	 * chiusura inviata, cioe' CA o CE.
	 * 
	 * @param cvCandidaturaCl
	 * @return
	 */
	public boolean isChiusuraNonInviata(CvCandidaturaCl cvCandidaturaCl) {
		if (cvCandidaturaCl != null) {
			DeStatoInvioCl deStatoInvioCl = cvCandidaturaCl.getDeStatoInvioCl();

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
	 * Restituisce true se il CV si trova in uno stato di primo invio a
	 * cliclavoro ma non di primo invio inviato, cioe' PA o PE
	 * 
	 * @param cvCandidaturaCl
	 * @return
	 */
	public boolean isPrimoInvioNonInviato(CvCandidaturaCl cvCandidaturaCl) {
		if (cvCandidaturaCl != null) {
			if (cvCandidaturaCl.getDeStatoInvioCl().getCodStatoInvioCl()
			        .equals(ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_PRIMO_INVIO)
			        || cvCandidaturaCl.getDeStatoInvioCl().getCodStatoInvioCl()
			                .equals(ConstantsSingleton.DeStatoInvioCl.PRIMO_INVIO_COMPLETATO_ERRORE)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Aggiorna un'entity che rappresenta la comunicazione di un CV a cliclavoro
	 * nei casi di rattifica e chiusura
	 * 
	 * @param cvCandidaturaCl
	 * @param codStatoInvioClNuovo
	 */
	public CvCandidaturaCl updateCandidatura(Integer idPfPrincipal, Integer idCvDatiPersonali,
	        CvCandidaturaCl cvCandidaturaCl, ConstantsSingleton.AzioneCliclavoro azione) {
		CvDatiPersonali cvDatiPersonali = cvDatiPersonaliHome.findById(idCvDatiPersonali);
		if (cvCandidaturaCl == null) {
			cvCandidaturaCl = new CvCandidaturaCl();
			cvCandidaturaCl.setCvDatiPersonali(cvDatiPersonali);
		}
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(idPfPrincipal);
		String codStatoInvioClCorrente = cvCandidaturaCl.getDeStatoInvioCl().getCodStatoInvioCl();
		Date now = new Date();

		String codStatoInvioClNuovo = deStatoInvioClHome.calcolaStatoInvioCL(codStatoInvioClCorrente, azione);

		/* la scadenza del curriculum viene settata a 60 gg da oggi */
		if (ConstantsSingleton.COD_REGIONE != ConstantsSingleton.COD_REGIONE_TRENTO) {			
			cvDatiPersonali.setDtScadenza(DateUtils.addDays(now, 30));
		}
		else {
			cvDatiPersonali.setDtScadenza(DateUtils.addDays(now, 60));
		}

		/* aggiorno lo stato di invio */
		cvCandidaturaCl.setDeStatoInvioCl(deStatoInvioClHome.findById(codStatoInvioClNuovo));

		/* aggiorno la data di invio */
		cvCandidaturaCl.setDtInvio(now);

		/* se lo stato e' cambiato devo aggiornare i codici comunicazione */
		if (!codStatoInvioClNuovo.equals(codStatoInvioClCorrente)) {
			/* mando un messaggio di primo invio */
			if (codStatoInvioClNuovo.equals(ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_PRIMO_INVIO)) {
				/* modifico solo se NON passo dallo stato PE allo stato PA */
				if (!ConstantsSingleton.DeStatoInvioCl.PRIMO_INVIO_COMPLETATO_ERRORE.equals(codStatoInvioClCorrente)) {
					/* aggiorno i cod_comunicazione */
					String codComunicazione = clicLavoroEjb.calcolaNuovoCodComunicazioneInvioCandidatura();
					cvCandidaturaCl.setCodComunicazione(codComunicazione);
					cvCandidaturaCl.setCodComunicazionePrec(null);

					cvCandidaturaCl.setPfPrincipalIns(pfPrincipal);
					cvCandidaturaCl.setDtmIns(now);

					/*
					 * nel caso sia la prima volta che invio il CV, non un
					 * ripristino
					 */
					if (codStatoInvioClCorrente == null || codStatoInvioClCorrente.isEmpty()) {
						cvDatiPersonali.setProvenienzaCurriculum(deProvenienzaHome
						        .findById(ConstantsSingleton.DeProvenienza.COD_MYPORTAL));
						cvDatiPersonali.setPfPrincipalPalese(null);
					}
				}
			} else {
				/* aggiorno cod_comunicazione_prec sse non presente */
				if (cvCandidaturaCl.getCodComunicazionePrec() == null) {
					cvCandidaturaCl.setCodComunicazionePrec(cvCandidaturaCl.getCodComunicazione());
				}

				/* aggiorno cod_comunicazione */
				String codComunicazione;
				if (codStatoInvioClNuovo.equals(ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_INVIO_CHIUSURA)) {
					codComunicazione = clicLavoroEjb.calcolaNuovoCodComunicazioneChiusuraCandidatura();
				} else {
					codComunicazione = clicLavoroEjb.calcolaNuovoCodComunicazioneInvioCandidatura();
				}

				cvCandidaturaCl.setCodComunicazione(codComunicazione);
			}
		}

		// se lo stato della candidatura è relativo ad una chiusura imposta
		// anche la tipologia di comunicazione appropriata
		if (ConstantsSingleton.DeStatoInvioCl.CHIUSURA_INVIATA.equals(codStatoInvioClNuovo)
		        || ConstantsSingleton.DeStatoInvioCl.IN_ATTESA_INVIO_CHIUSURA.equals(codStatoInvioClNuovo)
		        || ConstantsSingleton.DeStatoInvioCl.CHIUSURA_INVIATA_ERRORE.equals(codStatoInvioClNuovo)) {
			/* la comunicazione e' di tipo CHIUSURA */
			cvCandidaturaCl.setDeTipoComunicazioneCl(deTipoComunicazioneClHome
			        .findById(ConstantsSingleton.DeTipoComunicazioneCl.CHIUSURA));
			/* aggiorno il motivo chiusura del CV */
			cvDatiPersonali.setDeMotivoChiusura(deMotivoChiusuraHome
			        .findById(ConstantsSingleton.DeMotivoChiusura.RITIRO_OFFERTA));
		} else {
			/* la comunicazione e' di tipo INVIO */
			cvCandidaturaCl.setDeTipoComunicazioneCl(deTipoComunicazioneClHome
			        .findById(ConstantsSingleton.DeTipoComunicazioneCl.INVIO));

			/* se non e' un messaggio di chiusura cancello il motivo chiusura */
			cvDatiPersonali.setDeMotivoChiusura(null);
		}

		cvCandidaturaCl.setPfPrincipalMod(pfPrincipal);
		cvCandidaturaCl.setDtmMod(now);

		return cvCandidaturaCl;
	}
}
