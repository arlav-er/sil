package it.eng.myportal.entity.home;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;

import it.eng.myportal.dtos.AtpConsulenzaDTO;
import it.eng.myportal.dtos.DeTemaConsulenzaDTO;
import it.eng.myportal.dtos.GenericDecodeDTO;
import it.eng.myportal.dtos.IDecode;
import it.eng.myportal.dtos.MsgAllegatoDTO;
import it.eng.myportal.dtos.MsgCertificatoDTO;
import it.eng.myportal.dtos.MsgContattoDTO;
import it.eng.myportal.dtos.MsgEspertoDTO;
import it.eng.myportal.dtos.MsgMessaggioDTO;
import it.eng.myportal.dtos.MsgSupportoDTO;
import it.eng.myportal.entity.AcCandidatura;
import it.eng.myportal.entity.AppNotifica;
import it.eng.myportal.entity.AziendaInfo;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.CvLetteraAcc;
import it.eng.myportal.entity.MsgAllegato;
import it.eng.myportal.entity.MsgContatto;
import it.eng.myportal.entity.MsgMessaggio;
import it.eng.myportal.entity.MsgMessaggioCl;
import it.eng.myportal.entity.MsgMessaggioLetto;
import it.eng.myportal.entity.MsgMessaggioLetto_;
import it.eng.myportal.entity.MsgMessaggio_;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.PfPrincipal_;
import it.eng.myportal.entity.Provincia;
import it.eng.myportal.entity.UtenteInfo;
import it.eng.myportal.entity.VaDatiVacancy;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.decodifiche.DeRuoloPortale;
import it.eng.myportal.entity.decodifiche.DeRuoloPortale_;
import it.eng.myportal.entity.decodifiche.DeTemaConsulenza;
import it.eng.myportal.entity.decodifiche.DeTipoMessaggio;
import it.eng.myportal.entity.decodifiche.DeTipoMessaggio_;
import it.eng.myportal.entity.enums.TipoAbilitazione;
import it.eng.myportal.entity.enums.TipoNotificaEnum;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.entity.home.decodifiche.DeRuoloPortaleHome;
import it.eng.myportal.entity.home.decodifiche.DeTemaConsulenzaHome;
import it.eng.myportal.entity.home.decodifiche.DeTipoMessaggioHome;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.ConstantsSingleton.PfAttivita;
import it.eng.myportal.utils.Utils;

/**
 * @author Rodi A.
 */
@Stateless
public class MsgMessaggioHome extends AbstractUpdatableHome<MsgMessaggio, MsgMessaggioDTO> {

	@EJB
	DeTipoMessaggioHome deTipoMessaggioHome;

	@EJB
	MsgMessaggioLettoHome msgMessaggioLettoHome;

	@EJB
	CvDatiPersonaliHome cvDatiPersonaliHome;

	@EJB
	CvLetteraAccHome cvLetteraAccHome;

	@EJB
	DeTemaConsulenzaHome deTemaConsulenzaHome;

	@EJB
	MsgAllegatoHome msgAllegatoHome;

	@EJB
	AziendaInfoHome aziendaInfoHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	@EJB
	DeRuoloPortaleHome deRuoloPortaleHome;

	@EJB
	AppNotificaHome appNotificaHome;

	public MsgMessaggio findById(Integer id) {
		return findById(MsgMessaggio.class, id);
	}

	/**
	 * Esegue una query che cerca messaggi di contatto, restituendo il risultato come DTO.
	 * 
	 */
	protected List<MsgContattoDTO> findContattoDTOByQuery(TypedQuery<MsgMessaggio> query) {
		List<MsgMessaggio> result = null;
		result = query.getResultList();

		final List<MsgContattoDTO> ret = new ArrayList<MsgContattoDTO>();
		for (MsgMessaggio entity : result) {
			ret.add(toContattoDTO(entity));
		}

		return ret;
	}

	/**
	 * Restituisce il nome con il quale un utente appare nel messaggio. Per gli utenti è nome e cognome, per le aziende
	 * è la ragione sociale, eccetera.
	 */
	public String getNomeMessaggio(PfPrincipal pfPrincipal) {
		// Ottengo il ruolo dell'utente.
		DeRuoloPortale ruoloUtente = pfPrincipal.getDeRuoloPortale();

		// Se l'utente è un cittadino, consulente o coordinatore: restituisco
		// nome e cognome.
		if (ConstantsSingleton.DeRuoloPortale.CITTADINO.equals(ruoloUtente.getCodRuoloPortale())) {
			return pfPrincipal.getNome() + " " + pfPrincipal.getCognome();
		} else if (ConstantsSingleton.DeRuoloPortale.COORDINA.equals(ruoloUtente.getCodRuoloPortale())) {
			return "coordinatore: " + pfPrincipal.getNome() + " " + pfPrincipal.getCognome();
		} else if (ConstantsSingleton.DeRuoloPortale.CONSULEN.equals(ruoloUtente.getCodRuoloPortale())) {
			return "consulente: " + pfPrincipal.getNome() + " " + pfPrincipal.getCognome();
		}

		// Se l'utente è un'azienda: restituisco la ragione sociale.
		else if (ConstantsSingleton.DeRuoloPortale.AZIENDA.equals(ruoloUtente.getCodRuoloPortale())) {
			AziendaInfo aziendaInfo = aziendaInfoHome.findById(pfPrincipal.getIdPfPrincipal());
			return "azienda: " + aziendaInfo.getRagioneSociale();
		}

		// Se l'utente è un amministratore: restituisco una stringa fissa.
		else if (ConstantsSingleton.DeRuoloPortale.SYSTEM.equals(ruoloUtente.getCodRuoloPortale())) {
			return "Amministratore di sistema";
		}

		// Se l'utente è una provincia o una regione: restituisco il suo nome.
		else if (ConstantsSingleton.DeRuoloPortale.PROVINCIA.equals(ruoloUtente.getCodRuoloPortale())) {
			return pfPrincipal.getNome();
		} else if (ConstantsSingleton.DeRuoloPortale.REGIONE.equals(ruoloUtente.getCodRuoloPortale())) {
			return pfPrincipal.getNome();
		} else if (ConstantsSingleton.DeRuoloPortale.PATRONATO.equals(ruoloUtente.getCodRuoloPortale())) {
			return pfPrincipal.getNome();
		}

		// Se non ho trovato nulla...
		return "Utente";
	}

	/**
	 * Crea il DTO di un messaggio a partire dalla entity.
	 */
	@Override
	public MsgMessaggioDTO toDTO(MsgMessaggio entity) {
		if (entity == null)
			return null;

		// Se il messaggio è una consulenza, creo un DTO di tipo "consulenza", altrimenti un DTO normale.
		MsgMessaggioDTO dto;
		if (entity.getAtpConsulenza() != null) {
			dto = new AtpConsulenzaDTO();
			((AtpConsulenzaDTO) dto).setMinuti(entity.getAtpConsulenza().getMinuti());
			((AtpConsulenzaDTO) dto).setNote(entity.getAtpConsulenza().getNote());
		} else {
			dto = new MsgMessaggioDTO();
		}

		// Popolo i campi "base" del DTO.
		popolaDTO(dto, entity);
		popolaMessaggioDTO(dto, entity);

		// Scorro la lista delle risposte al messaggio, inserendole nel campo "risposte".
		dto.setRisposte(new ArrayList<MsgMessaggioDTO>());
		MsgMessaggio current = entity;
		while (current.getSuccessivo() != null) {
			current = current.getSuccessivo();
			MsgMessaggioDTO risposta = toDTO(current);
			dto.getRisposte().add(risposta);

			// Se è un messaggio ATIPICI, aggiungo al messaggio principale gli allegati dele sue risposte.
			if (!ConstantsSingleton.TipoMessaggio.ATIPICI.equals(dto.getCodTipoMessaggio())) {
				if (risposta.getAllegati() != null && !risposta.getAllegati().isEmpty())
					dto.getAllegati().addAll(risposta.getAllegati());
			}
		}

		// Setto l'ID del messaggio inoltrante.
		if (entity.getInoltrante() != null && entity.getInoltrante().getIdMsgMessaggio() != null) {
			dto.setIdMsgMessaggioInoltrante(entity.getInoltrante().getIdMsgMessaggio());
		}
		return dto;
	}

	/**
	 * Crea il DTO di un messaggio di contatto a partire dalla entity.
	 */
	public MsgContattoDTO toContattoDTO(MsgMessaggio entity) {
		if (entity == null)
			return null;
		MsgContattoDTO dto = new MsgContattoDTO();
		popolaDTO(dto, entity);
		popolaMessaggioDTO(dto, entity);
		aggiungiInformazioniContattoDTO(dto, entity);

		return dto;
	}

	/**
	 * Popola un MsgMessaggioDTO con i dati contenuti nella entity corrispondente.
	 */
	private void popolaMessaggioDTO(MsgMessaggioDTO dto, MsgMessaggio entity) {
		dto.setId(entity.getIdMsgMessaggio());
		dto.setCorpo(entity.getCorpo());

		PfPrincipal from = entity.getPfPrincipalFrom();
		if (from != null) {
			dto.setIdFrom(from.getIdPfPrincipal());
			dto.setMittente(getNomeMessaggio(from));
		}

		PfPrincipal to = entity.getPfPrincipalTo();
		DeProvincia provinciaTo = entity.getDeProvinciaTo();
		DeRuoloPortale ruoloPortaleTo = entity.getDeRuoloPortaleTo();

		if (provinciaTo != null) {
			dto.setProvinciaTo(deProvinciaHome.toDTO(provinciaTo));
			dto.setDestinatario(provinciaTo.getDenominazione());
		}

		if (ruoloPortaleTo != null) {
			dto.setRuoloPortaleTo(deRuoloPortaleHome.toDTO(ruoloPortaleTo));
			dto.setDestinatario(ruoloPortaleTo.getDescrizione());
		}

		if (to != null) {
			dto.setIdTo(to.getIdPfPrincipal());
			dto.setDestinatario(getNomeMessaggio(to));
		}

		if (to == null && provinciaTo == null && ruoloPortaleTo == null) {
			dto.setDestinatario("Broadcast");
		}

		DeTemaConsulenza temaConsulenza = entity.getDeTemaConsulenza();
		if (temaConsulenza != null) {
			dto.setTemaConsulenza(deTemaConsulenzaHome.toDTO(temaConsulenza));
		}

		Set<MsgAllegato> allegati = entity.getMsgAllegatos();
		if (allegati != null) {
			for (MsgAllegato msgAllegato : allegati) {
				dto.getAllegati().add(msgAllegatoHome.toDTO(msgAllegato));
			}
		}

		List<MsgMessaggioDTO> listaInoltri = new ArrayList<MsgMessaggioDTO>();
		if (entity.getInoltrati() != null) {
			for (MsgMessaggio m : entity.getInoltrati()) {
				listaInoltri.add(toDTO(m));
			}
		}
		dto.setInoltri(listaInoltri);

		if (entity.getInoltrante() != null) {
			dto.setIdMsgMessaggioInoltrante(entity.getInoltrante().getIdMsgMessaggio());
		}

		dto.setOggetto(entity.getOggetto());
		dto.setScadenza(entity.getDtScadenza());
		dto.setCodTipoMessaggio(entity.getDeTipoMessaggio().getCodTipoMessaggio());

		if (entity.getPrecedente() != null) {
			dto.setIdMsgMessaggioPrecedente(entity.getPrecedente().getIdMsgMessaggio());
		}

		dto.setTicket(entity.getTicket());
	}

	/**
	 * Popola un MsgContattoDTO con le informazioni (quelle specifiche per i messaggi "contatto") contenute nella
	 * entity.
	 */
	private void aggiungiInformazioniContattoDTO(MsgContattoDTO dto, MsgMessaggio entity) {
		MsgContatto msgContatto = entity.getMsgContatto();
		if (msgContatto != null) {
			AcCandidatura acCandidatura = msgContatto.getAcCandidatura();
			VaDatiVacancy vaDatiVacancy = msgContatto.getVaDatiVacancy();

			// Se è interno a myportal ho la candidatura.
			if (acCandidatura != null) {
				if (acCandidatura.getVaDatiVacancy() != null)
					dto.setIdVaDatiVacancy(acCandidatura.getVaDatiVacancy().getIdVaDatiVacancy());
				if (acCandidatura.getCvDatiPersonali() != null)
					dto.setIdCvDatiPersonali(acCandidatura.getCvDatiPersonali().getIdCvDatiPersonali());
				if (acCandidatura.getIdAcCandidatura() != null)
					dto.setIdAcCandidatura(acCandidatura.getIdAcCandidatura());
			} else if (vaDatiVacancy != null) {
				// Altrimenti ho direttamente la vacancy.
				dto.setIdVaDatiVacancy(vaDatiVacancy.getIdVaDatiVacancy());
			}

			dto.setEsito(entity.getMsgContatto().getFlagEsito());

			// Aggiungo un eventuale curriculum.
			CvDatiPersonali cvDatiPersonali = msgContatto.getCvDatiPersonali();
			if (cvDatiPersonali != null)
				dto.setIdCvDatiPersonali(cvDatiPersonali.getIdCvDatiPersonali());
		}
	}

	/**
	 * Trasorma un MsgMessaggioDTO in una entità MsgMessaggio.
	 */
	@Override
	public MsgMessaggio fromDTO(MsgMessaggioDTO dto) {
		if (dto == null)
			return null;
		MsgMessaggio entity = super.fromDTO(dto);

		entity.setIdMsgMessaggio(dto.getId());
		entity.setCorpo(dto.getCorpo());
		if (dto.getProvinciaTo() != null && dto.getProvinciaTo().getId() != null) {
			entity.setDeProvinciaTo(deProvinciaHome.findById(dto.getProvinciaTo().getId()));
		}
		if (dto.getRuoloPortaleTo() != null && dto.getRuoloPortaleTo().getId() != null) {
			entity.setDeRuoloPortaleTo(deRuoloPortaleHome.findById(dto.getRuoloPortaleTo().getId()));
		}

		DeTemaConsulenzaDTO temaConsulenza = dto.getTemaConsulenza();
		if (temaConsulenza.getId() != null) {
			entity.setDeTemaConsulenza(deTemaConsulenzaHome.findById(temaConsulenza.getId()));
		}

		if (dto.getCodTipoMessaggio() != null)
			entity.setDeTipoMessaggio(deTipoMessaggioHome.findById(dto.getCodTipoMessaggio()));

		entity.setDtScadenza(dto.getScadenza());
		entity.setOggetto(dto.getOggetto());
		Integer idTo = dto.getIdTo();
		if (idTo != null)
			entity.setPfPrincipalTo(pfPrincipalHome.findById(idTo));
		Integer idFrom = dto.getIdFrom();
		if (idFrom != null)
			entity.setPfPrincipalFrom(pfPrincipalHome.findById(idFrom));

		entity.setTicket(dto.getTicket());

		if (dto.getIdMsgMessaggioPrecedente() != null) {
			entity.setPrecedente(findById(dto.getIdMsgMessaggioPrecedente()));
		}

		if (dto.getIdMsgMessaggioInoltrante() != null) {
			entity.setInoltrante(findById(dto.getIdMsgMessaggioInoltrante()));
		}

		return entity;
	}

	/**
	 * Restituisce la lista di record che determinano se un messaggio è stato letto o meno da un determinato utente.
	 */
	public List<MsgMessaggioLetto> getLettos(Integer pfPrincipalId, Integer messageId) {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<MsgMessaggioLetto> query = qb.createQuery(MsgMessaggioLetto.class);

		Root<MsgMessaggioLetto> messaggioLetto = query.from(MsgMessaggioLetto.class);
		Join<MsgMessaggioLetto, MsgMessaggio> messaggio = messaggioLetto.join(MsgMessaggioLetto_.msgMessaggio);
		Join<MsgMessaggioLetto, PfPrincipal> pfPrincipal = messaggioLetto.join(MsgMessaggioLetto_.pfPrincipal);

		List<Predicate> whereConditions = new ArrayList<Predicate>();
		whereConditions.add(qb.equal(messaggio.get(MsgMessaggio_.idMsgMessaggio), messageId));
		whereConditions.add(qb.equal(pfPrincipal.get(PfPrincipal_.idPfPrincipal), pfPrincipalId));
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));

		TypedQuery<MsgMessaggioLetto> q = entityManager.createQuery(query);
		return q.getResultList();
	}

	/**
	 * Controlla se un certo messaggio è stato letto da un certo utente.
	 */
	public boolean hasLettos(Integer pfPrincipalId, Integer messageId) {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = qb.createQuery(Long.class);

		Root<MsgMessaggioLetto> messaggioLetto = query.from(MsgMessaggioLetto.class);
		Join<MsgMessaggioLetto, MsgMessaggio> messaggio = messaggioLetto.join(MsgMessaggioLetto_.msgMessaggio);
		Join<MsgMessaggioLetto, PfPrincipal> pfPrincipal = messaggioLetto.join(MsgMessaggioLetto_.pfPrincipal);

		List<Predicate> whereConditions = new ArrayList<Predicate>();
		whereConditions.add(qb.equal(messaggio.get(MsgMessaggio_.idMsgMessaggio), messageId));
		whereConditions.add(qb.equal(pfPrincipal.get(PfPrincipal_.idPfPrincipal), pfPrincipalId));
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));

		query.select(qb.count(messaggioLetto));
		TypedQuery<Long> q = entityManager.createQuery(query);
		Long numLettos = q.getSingleResult();
		return (numLettos > 0);
	}

	/**
	 * Restituisce le notifiche ricevute da un utente. I parametri "startResultsFrom" e "maxResults" servono per la
	 * paginazione: settarli a 0 per avere tutte le notifiche.
	 */
	public List<MsgMessaggioDTO> findNotificheRicevute(Integer principalId, int startResultsFrom, int maxResults) {

		List<MsgMessaggioDTO> ret = findMessages(principalId, true, ConstantsSingleton.MsgMessaggio.NOTIFICA,
				startResultsFrom, maxResults);

		for (MsgMessaggioDTO msg : ret) {
			msg.setLetto(hasLettos(principalId, msg.getId()));
		}
		return ret;
	}

	/** Versione senza paginazione: restituisce tutte le notifiche ricevute. */
	public List<MsgMessaggioDTO> findNotificheRicevute(Integer principalId) {
		return findNotificheRicevute(principalId, 0, 0);
	}

	/**
	 * Restituisce il numero di notifiche ricevute dall'utente.
	 */
	public Long findNotificheRicevuteCount(Integer principalId) {
		return findMessagesCount(principalId, true, ConstantsSingleton.MsgMessaggio.NOTIFICA);
	}

	/**
	 * Restituisce tutte le notifiche inviate dall'utente. I parametri "startResultsFrom" e "maxResults" servono per la
	 * paginazione: settarli a 0 per avere tutte le notifiche.
	 */
	public List<MsgMessaggioDTO> findNotificheInviate(Integer principalId, int startResultsFrom, int maxResults) {
		return findMessages(principalId, false, ConstantsSingleton.MsgMessaggio.NOTIFICA, startResultsFrom, maxResults);
	}

	/** Versione senza paginazione: restituisce tutte le notifiche. */
	public List<MsgMessaggioDTO> findNotificheInviate(Integer principalId) {
		return findNotificheInviate(principalId, 0, 0);
	}

	/**
	 * Restituisce il numero di notifiche inviate dall'utente
	 */
	public Long findNotificheInviateCount(Integer principalId) {
		return findMessagesCount(principalId, false, ConstantsSingleton.MsgMessaggio.NOTIFICA);
	}

	/**
	 * Cerca i messaggi di un certo tipo 'diretti a' o 'inviati da' un utente. I parametri "startResultsFrom" e
	 * "maxResults" servono per la paginazione: settarli a 0 per avere tutte le notifiche.
	 */
	protected List<MsgMessaggioDTO> findMessages(Integer principalId, Boolean ricevute, String codTipoMessaggio,
			int startResultsFrom, int maxResults) {
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(principalId);
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<MsgMessaggio> query = qb.createQuery(MsgMessaggio.class);
		Root<MsgMessaggio> messaggio = query.from(MsgMessaggio.class);
		Join<MsgMessaggio, DeTipoMessaggio> tipoMessaggio = messaggio.join(MsgMessaggio_.deTipoMessaggio);

		// Creo la clausola WHERE della query.
		List<Predicate> whereConditions = new ArrayList<Predicate>();
		if (ricevute) {
			toMeCondition(pfPrincipal, qb, messaggio, whereConditions);
		} else {
			Join<MsgMessaggio, PfPrincipal> principal = messaggio.join(MsgMessaggio_.pfPrincipalFrom);
			whereConditions.add(qb.equal(principal.get(PfPrincipal_.idPfPrincipal), principalId));
		}
		whereConditions.add(qb.greaterThan(messaggio.get(MsgMessaggio_.dtScadenza), new Date()));
		whereConditions.add(qb.equal(tipoMessaggio.get(DeTipoMessaggio_.codTipoMessaggio), codTipoMessaggio));
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));

		// Ordino i messaggi per id, partendo dal più recente.
		query.orderBy(qb.desc(messaggio.get(MsgMessaggio_.idMsgMessaggio)));

		// Aggiungo i parametri per la paginazione, se ci sono.
		TypedQuery<MsgMessaggio> q = entityManager.createQuery(query);
		if (startResultsFrom > 0) {
			q.setFirstResult(startResultsFrom);
		}

		if (maxResults > 0) {
			q.setMaxResults(maxResults);
		}

		// Eseguo la query e restituisco il risultato.
		return findDTOByQuery(q);
	}

	/**
	 * Conta il numero di messaggi di un certo tipo 'ricevuti da' o 'inviati a' un utente.
	 */
	protected Long findMessagesCount(Integer principalId, Boolean ricevute, String codTipoMessaggio) {
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(principalId);
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = qb.createQuery(Long.class);

		Root<MsgMessaggio> messaggio = query.from(MsgMessaggio.class);
		Join<MsgMessaggio, DeTipoMessaggio> tipoMessaggio = messaggio.join(MsgMessaggio_.deTipoMessaggio);

		List<Predicate> whereConditions = new ArrayList<Predicate>();
		if (ricevute) {
			toMeCondition(pfPrincipal, qb, messaggio, whereConditions);
		} else {
			Join<MsgMessaggio, PfPrincipal> principal = messaggio.join(MsgMessaggio_.pfPrincipalFrom);
			whereConditions.add(qb.equal(principal.get(PfPrincipal_.idPfPrincipal), principalId));
		}

		whereConditions.add(qb.greaterThan(messaggio.get(MsgMessaggio_.dtScadenza), new Date()));
		whereConditions.add(qb.equal(tipoMessaggio.get(DeTipoMessaggio_.codTipoMessaggio), codTipoMessaggio));
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));

		query.select(qb.count(messaggio));
		TypedQuery<Long> q = entityManager.createQuery(query);
		return q.getSingleResult();
	}

	/**
	 * Aggiunge ad una query sui messaggi la condizione: seleziona i messaggi diretti ad un utente. Questa condizione è
	 * composta da tre sotto-condizioni: 1) Il messaggio è diretto a questo utente. 2) Il messaggio è diretto alla
	 * provincia di cui l'utente fa parte. 3) Il messaggio è diretto al ruolo_portale dell'utente 4) Il messaggio è di
	 * broadcast (diretto a utente NULL, provincia NULL e ruolo_portale NULL).
	 */
	private void toMeCondition(PfPrincipal principal, CriteriaBuilder qb, Root<MsgMessaggio> messaggio,
			List<Predicate> whereConditions) {
		Join<MsgMessaggio, PfPrincipal> messaggioPrincipalJoin = messaggio.join(MsgMessaggio_.pfPrincipalTo,
				JoinType.LEFT);
		Predicate messaggioVersoUtente = qb.equal(messaggioPrincipalJoin.get(PfPrincipal_.idPfPrincipal),
				principal.getIdPfPrincipal());

		Predicate messaggioVersoProv = qb.or(); // Sempre falso di default, se l'utente non è una provincia
		if (principal.isProvincia()) {
			DeProvincia deProvincia = principal.getProvinciasForIdPfPrincipal().iterator().next().getDeProvincia();
			messaggioVersoProv = qb.equal(messaggio.get(MsgMessaggio_.deProvinciaTo), deProvincia);
		}

		Predicate messaggioVersoRuolo = qb.equal(messaggio.get(MsgMessaggio_.deRuoloPortaleTo),
				principal.getDeRuoloPortale());

		Predicate messaggioInBroadcast = qb.and(qb.isNull(messaggio.get(MsgMessaggio_.deProvinciaTo)),
				qb.isNull(messaggioPrincipalJoin.get(PfPrincipal_.idPfPrincipal)),
				qb.isNull(messaggio.get(MsgMessaggio_.deRuoloPortaleTo)));

		whereConditions.add(qb.or(messaggioVersoUtente, messaggioVersoProv, messaggioVersoRuolo, messaggioInBroadcast));
	}

	/**
	 * Restituisce tutti i messaggi di supporto da mostrare all'utente, raggruppati per ticket. Nel caso di un
	 * cittadino, restituirà tutte le richieste di supporto inviate da lui. Nel caso di una provincia, restituirà tutte
	 * le richieste di supporto pervenute.
	 */
	public List<MsgSupportoDTO> findMessaggiSupporto(Integer idPrincipal, int startResultsFrom, int maxResults) {

		// Trova la lista dei ticket.
		List<BigDecimal> tickets = findTickets(idPrincipal, ConstantsSingleton.MsgMessaggio.SUPPORTO, startResultsFrom,
				maxResults);

		// Per ogni ticket costruisco un DTO, inserendoci messaggio principale, risposte, allegati eccetera.
		List<MsgSupportoDTO> ret = new ArrayList<MsgSupportoDTO>();
		for (BigDecimal ticket : tickets) {
			ret.add(findMsgSupportoByTicket(ticket.toString(), idPrincipal));
		}

		return ret;
	}

	/** Versione senza paginazione: restituisce tutti i messaggi */
	public List<MsgSupportoDTO> findMessaggiSupporto(Integer idPrincipal) {
		return findMessaggiSupporto(idPrincipal, 0, 0);
	}

	/** Count per paginazione */
	public Long findMessaggiSupportoCount(Integer idPrincipal) {
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(idPrincipal);

		if (pfPrincipal.isUtente() || pfPrincipal.isAzienda()) {
			TypedQuery<Long> q = entityManager.createQuery(QUERY_COUNT_BY_TICKET, Long.class);
			q.setParameter("codTipoMessaggio", ConstantsSingleton.MsgMessaggio.SUPPORTO);
			q.setParameter("idPrincipal", idPrincipal);
			return q.getSingleResult();
		}

		if (pfPrincipal.isProvincia()) {
			Provincia provincia = pfPrincipal.getProvinciasForIdPfPrincipal().iterator().next();
			DeProvincia deProvincia = provincia.getDeProvincia();
			TypedQuery<Long> q = entityManager.createQuery(QUERY_COUNT_RECEIVED_PROVINCIA_BY_TICKET, Long.class);
			q.setParameter("codTipoMessaggio", ConstantsSingleton.MsgMessaggio.SUPPORTO);
			q.setParameter("codProvincia", deProvincia.getCodProvincia());
			return q.getSingleResult();
		}

		return new Long(0);
	}

	/**
	 * Restituisce tutti i messaggi di domanda all'esperto da mostrare all'utente, raggruppati per ticket. Nel caso di
	 * un cittadino, restituirà tutti i messaggi inviati da lui. Nel caso di una provincia, restituirà tutti i messaggi
	 * pervenuti.
	 */
	public List<MsgEspertoDTO> findMessaggiEsperto(Integer idPrincipal, int startResultsFrom, int maxResults) {
		// Recupero la lista dei ticket.
		List<BigDecimal> tickets = findTickets(idPrincipal, ConstantsSingleton.MsgMessaggio.ESPERTO, startResultsFrom,
				maxResults);

		// Per ogni ticket costruisco un DTO, inserendoci messaggio principale, risposte, allegati eccetera.
		List<MsgEspertoDTO> ret = new ArrayList<MsgEspertoDTO>();
		for (BigDecimal ticket : tickets) {
			ret.add(findMsgEspertoByTicket(ticket.toString(), idPrincipal));
		}

		return ret;
	}

	/**
	 * Conta il numero di messaggi "consulenza all'esperto" da mostrare all'utente.
	 */
	public Long findMessaggiEspertoCount(Integer idPrincipal) {
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(idPrincipal);

		if (pfPrincipal.isUtente() || pfPrincipal.isAzienda()) {
			TypedQuery<Long> q = entityManager.createQuery(QUERY_COUNT_BY_TICKET, Long.class);
			q.setParameter("codTipoMessaggio", ConstantsSingleton.MsgMessaggio.ESPERTO);
			q.setParameter("idPrincipal", idPrincipal);
			return q.getSingleResult();
		}

		if (pfPrincipal.isProvincia()) {
			Provincia provincia = pfPrincipal.getProvinciasForIdPfPrincipal().iterator().next();
			DeProvincia deProvincia = provincia.getDeProvincia();
			TypedQuery<Long> q = entityManager.createQuery(QUERY_COUNT_RECEIVED_PROVINCIA_BY_TICKET, Long.class);
			q.setParameter("codTipoMessaggio", ConstantsSingleton.MsgMessaggio.ESPERTO);
			q.setParameter("codProvincia", deProvincia.getCodProvincia());
			return q.getSingleResult();
		}

		return new Long(0);
	}

	/**
	 * Restituisce la lista delle richieste di certificati inviate dall'utente o ricevute dalla provincia. I messaggi di
	 * richiesta certificati sono raggruppati per ticket.
	 */
	public List<MsgCertificatoDTO> findMessaggiCertificati(Integer idPrincipal) {
		// Recupero la lista dei ticket.
		List<BigDecimal> tickets = findTickets(idPrincipal, ConstantsSingleton.MsgMessaggio.CERTIFICATO, 0, 0);

		// Per ogni ticket costruisco un DTO, inserendoci messaggio principale, risposte, allegati eccetera.
		List<MsgCertificatoDTO> ret = new ArrayList<MsgCertificatoDTO>();
		for (BigDecimal ticket : tickets) {
			ret.add(findMsgCertificatoByTicket(ticket.toString(), idPrincipal));
		}

		return ret;
	}

	/**
	 * Trova i ticket di tutti i messaggi di tipo "primo contatto" inviati dall'utente; poi recupera gli oggetti
	 * MsgContatto corrispondenti a quei ticket, li converte in DTO, li ordina per data e li restituisce.
	 */
	public List<MsgContattoDTO> findMessaggiContattiInviati(Integer idPrincipal, int startResultsFrom, int maxResults) {

		// Recupero la lista dei ticket.
		TypedQuery<String> q = entityManager.createNamedQuery("findMessagesInviati", String.class);
		q.setParameter("idPrincipalFrom", idPrincipal);
		q.setParameter("codTipoMessaggio", ConstantsSingleton.MsgMessaggio.PRIMO_CONTATTO);
		if (startResultsFrom > 0)
			q.setFirstResult(startResultsFrom);
		if (maxResults > 0)
			q.setMaxResults(maxResults);
		List<String> tickets = q.getResultList();

		// Per ogni ticket costruisco un DTO, inserendoci messaggio principale, risposte, allegati eccetera.
		List<MsgContattoDTO> ret = new ArrayList<MsgContattoDTO>();
		for (String ticket : tickets) {
			ret.add(findMsgContattoByTicket(ticket, idPrincipal));
		}

		return ret;
	}

	/** Versione senza paginazione */
	public List<MsgContattoDTO> findMessaggiContattiInviati(Integer idPrincipal) {
		return findMessaggiContattiInviati(idPrincipal, 0, 0);
	}

	/** Count per paginazione */
	public Long findMessaggiContattiInviatiCount(Integer idPrincipal) {
		TypedQuery<Long> q = entityManager.createNamedQuery("findMessagesInviatiCount", Long.class);
		q.setParameter("idPrincipalFrom", idPrincipal);
		q.setParameter("codTipoMessaggio", ConstantsSingleton.MsgMessaggio.PRIMO_CONTATTO);
		return q.getSingleResult();
	}

	/**
	 * Trova i ticket di tutti i messaggi di "primo contatto" inviati A questo utente. Poi recupera gli oggetti
	 * MsgContatto corrispondenti a questi ticket, li converte in DTO, li ordina per data e li restituisce.
	 */
	public List<MsgContattoDTO> findMessaggiContattiRicevuti(Integer idPrincipal, int startResultsFrom,
			int maxResults) {
		// Recupero la lista dei ticket.
		TypedQuery<String> q = entityManager.createNamedQuery("findMessagesRicevuti", String.class);
		q.setParameter("idPrincipalTo", idPrincipal);
		q.setParameter("codTipoMessaggio", ConstantsSingleton.MsgMessaggio.PRIMO_CONTATTO);
		if (startResultsFrom > 0)
			q.setFirstResult(startResultsFrom);
		if (maxResults > 0)
			q.setMaxResults(maxResults);
		List<String> tickets = q.getResultList();

		// Per ogni ticket costruisco un DTO, inserendoci messaggio principale, risposte, allegati eccetera.
		List<MsgContattoDTO> ret = new ArrayList<MsgContattoDTO>();
		for (String ticket : tickets) {
			ret.add(findMsgContattoByTicket(ticket, idPrincipal));
		}

		return ret;
	}

	/** Versione senza paginazione */
	public List<MsgContattoDTO> findMessaggiContattiRicevuti(Integer idPrincipal) {
		return findMessaggiContattiRicevuti(idPrincipal, 0, 0);
	}

	/** Count per paginazione */
	public Long findMessaggiContattiRicevutiCount(Integer idPrincipal) {
		TypedQuery<Long> q = entityManager.createNamedQuery("findMessagesRicevutiCount", Long.class);
		q.setParameter("idPrincipalTo", idPrincipal);
		q.setParameter("codTipoMessaggio", ConstantsSingleton.MsgMessaggio.PRIMO_CONTATTO);
		return q.getSingleResult();
	}

	/**
	 * Estraggo tutti i ticket distinti di messaggi partiti dall'utente.
	 */
	private final static String QUERY_BY_TICKET = " SELECT DISTINCT(to_number(m.ticket, '99999999999')) AS ticketNum "
			+ " FROM MsgMessaggio m " + " WHERE m.deTipoMessaggio.codTipoMessaggio = :codTipoMessaggio "
			+ " AND m.pfPrincipalFrom.idPfPrincipal = :idPrincipal " + " ORDER BY ticketNum DESC";

	/**
	 * Conto i ticket distinti di messaggi parititi dall'utente.
	 */
	private final static String QUERY_COUNT_BY_TICKET = "SELECT COUNT(DISTINCT m.ticket) " + " FROM MsgMessaggio m "
			+ " WHERE m.deTipoMessaggio.codTipoMessaggio = :codTipoMessaggio "
			+ " AND m.pfPrincipalFrom.idPfPrincipal = :idPrincipal ";

	/**
	 * Estraggo tutti i ticket distinti di messaggi mandati o ricevuti dall'utente.
	 */
	private final static String QUERY_BY_TICKET_TO_AND_FROM = " SELECT DISTINCT(to_number(m.ticket, '99999999999'))  AS ticketNum"
			+ " from MsgMessaggio m " + " where m.deTipoMessaggio.codTipoMessaggio = :codTipoMessaggio "
			+ " AND (m.pfPrincipalFrom.idPfPrincipal = :idPrincipalFrom "
			+ "		  OR m.pfPrincipalTo.idPfPrincipal = :idPrincipalTo) " + " ORDER BY ticketNum desc";

	/**
	 * Estraggo tutti i ticket distinti di messaggi ricevuti dall'utente (utenti PROVINCIALI).
	 */
	private final static String QUERY_RECEIVED_PROVINCIA_BY_TICKET = " SELECT DISTINCT(to_number(m.ticket, '99999999999')) AS ticketNum"
			+ " FROM MsgMessaggio m " + " WHERE m.deTipoMessaggio.codTipoMessaggio = :codTipoMessaggio "
			+ " AND m.deProvinciaTo.codProvincia = :codProvincia " + " ORDER BY ticketNum desc";

	/**
	 * Conto i ticket distinti di messaggi ricevuti dall'utente (utenti PROVINCIALI).
	 */
	private final static String QUERY_COUNT_RECEIVED_PROVINCIA_BY_TICKET = " SELECT COUNT(DISTINCT m.ticket)"
			+ " FROM MsgMessaggio m " + " WHERE m.deTipoMessaggio.codTipoMessaggio = :codTipoMessaggio "
			+ " AND m.deProvinciaTo.codProvincia = :codProvincia ";

	/**
	 * Estrae un elenco di ticket distinti dei messaggi che si vuole recuperare per l'utente. Questa funzione va
	 * chiamata quando si vuole ottenere una lista di messaggi raggruppati per ticket. Si esegue prima questa query e
	 * poi, per ogni ticket, si cercano i messaggi.
	 */
	private List<BigDecimal> findTickets(Integer idPrincipal, String codTipoMessaggio, int startResultsFrom,
			int maxResults) {
		// Inizializzo i parametri relativi ad utente, provincia e query.
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(idPrincipal);
		List<BigDecimal> tickets = null;
		String attivita = null;
		boolean isPrimoContatto = false;

		// Setto l'attività in base al tipo di messaggi che sto cercando.
		if (it.eng.myportal.utils.ConstantsSingleton.MsgMessaggio.SUPPORTO.equals(codTipoMessaggio)) {
			attivita = PfAttivita.MSG_SUPPORTO;
		} else if (it.eng.myportal.utils.ConstantsSingleton.MsgMessaggio.CERTIFICATO.equals(codTipoMessaggio)) {
			attivita = PfAttivita.MSG_CERTIFICATO;
		} else if (it.eng.myportal.utils.ConstantsSingleton.MsgMessaggio.ESPERTO.equals(codTipoMessaggio)) {
			attivita = PfAttivita.MSG_ESPERTO;
		} else if (it.eng.myportal.utils.ConstantsSingleton.MsgMessaggio.PRIMO_CONTATTO.equals(codTipoMessaggio)) {
			isPrimoContatto = true;
			attivita = PfAttivita.MSG_ESPERTO; // TODO Non c'è un'attività apposta.
		} else {
			throw new EJBException("codTipoMessaggio '" + codTipoMessaggio
					+ "' sconosciuto. Impossibile recuperare la lista messaggi.");
		}

		// Se cerco i messaggi diprimo contatto, faccio una query adeguata.
		if (isPrimoContatto) {
			TypedQuery<BigDecimal> q = entityManager.createQuery(QUERY_BY_TICKET_TO_AND_FROM, BigDecimal.class);
			q.setParameter("codTipoMessaggio", codTipoMessaggio);
			q.setParameter("idPrincipalFrom", idPrincipal);
			q.setParameter("idPrincipalTo", idPrincipal);
			tickets = q.getResultList();
			return tickets;
		}

		// Se l'utente è di tipo PROVINCIA, uso una query apposta.
		if (pfPrincipal.isProvincia()) {
			// Controllo se l'utente ha i permessi necessari per farlo.
			Boolean allowed = pfPrincipalHome.checkAbilitazione(idPrincipal, attivita, TipoAbilitazione.LETTURA);
			if (allowed) {
				DeProvincia deProvincia = pfPrincipal.getProvinciasForIdPfPrincipal().iterator().next()
						.getDeProvincia();
				tickets = findByCodProvincia(codTipoMessaggio, deProvincia.getCodProvincia(), startResultsFrom,
						maxResults);
			} else {
				throw new EJBException(
						"Non disponi dei permessi sufficienti alla lettura dei messaggi di tipo " + codTipoMessaggio);
			}
			return tickets;
		} else {
			// Se l'utente non è di tipo PROVINCIA, cerco i ticket per ID e tipo di messaggio.
			TypedQuery<BigDecimal> q = entityManager.createQuery(QUERY_BY_TICKET, BigDecimal.class);
			q.setParameter("codTipoMessaggio", codTipoMessaggio);
			q.setParameter("idPrincipal", idPrincipal);
			if (startResultsFrom > 0)
				q.setFirstResult(startResultsFrom);
			if (maxResults > 0)
				q.setMaxResults(maxResults);
			tickets = q.getResultList();
			return tickets;
		}
	}

	/**
	 * Restituisce una lista di ticket distinti di messaggi di un certo tipo ed indirizzati a una provincia.
	 */
	private List<BigDecimal> findByCodProvincia(String codTipoMessaggio, String codProvincia, int startResultsFrom,
			int maxResults) {
		TypedQuery<BigDecimal> q = entityManager.createQuery(QUERY_RECEIVED_PROVINCIA_BY_TICKET, BigDecimal.class);
		q.setParameter("codTipoMessaggio", codTipoMessaggio);
		q.setParameter("codProvincia", codProvincia);
		if (startResultsFrom > 0)
			q.setFirstResult(startResultsFrom);
		if (maxResults > 0)
			q.setMaxResults(maxResults);
		return q.getResultList();
	}

	/**
	 * Questo metodo recupera tutti i messaggi di tipo "contatto" che fanno riferimento ad una certa candidatura.
	 */
	public List<MsgContatto> findMsgContattoByIdAcCandidatura(Integer idAcCandidatura) {
		List<MsgContatto> result = new ArrayList<MsgContatto>();
		TypedQuery<MsgContatto> query = entityManager.createNamedQuery("findAllByIdAcCandidatura", MsgContatto.class);
		query = query.setParameter("idAcCandidatura", idAcCandidatura);
		result = query.getResultList();
		return result;
	}

	/**
	 * Partendo dall'ID di un ticket di tipo "richiesta all'esperto", ne costruisco il DTO. Poi recupero la lista delle
	 * risposte e le inserisco nel DTO, con eventuali allegati.
	 */
	public MsgContattoDTO findMsgContattoByTicket(String ticket, Integer principalId) {
		// Recupero tutti i messaggi relativi al ticket, in ordine cronologico.
		TypedQuery<MsgMessaggio> messaggiQuery = entityManager.createNamedQuery("findAllByTicketAsc",
				MsgMessaggio.class);
		messaggiQuery.setParameter("ticket", ticket);
		List<MsgMessaggio> messaggi = messaggiQuery.getResultList();

		if (messaggi == null || messaggi.size() < 1) {
			throw new EJBException("Nessun messaggio trovato con ticket " + ticket + " e principal_id " + principalId);
		}

		// Creo il DTO del messaggio capostipite del ticket.
		MsgContattoDTO firstDTO = toContattoDTO(messaggi.get(0));

		// Controllo se il messaggio è già stato letto.
		Boolean hasLetto = hasLettos(principalId, messaggi.get(0).getIdMsgMessaggio());
		if (!hasLetto && !firstDTO.isProprietary(principalId)) {
			firstDTO.setLetto(hasLetto);
		}

		// Creo dei DTO anche per gli altri messaggi del ticket (le risposte).
		for (int i = 1; i < messaggi.size(); i++) {
			MsgMessaggio risposta = messaggi.get(i);
			MsgContattoDTO rispostaDTO = toContattoDTO(risposta);

			// Controllo se la risposta è già stata letta.
			// Se no, setto come "non letto" anche il messaggio principale.
			if (risposta.getPfPrincipalFrom().getIdPfPrincipal().intValue() != principalId.intValue()) {
				hasLetto = hasLettos(principalId, risposta.getIdMsgMessaggio());
				rispostaDTO.setLetto(hasLetto);
				if (!hasLetto) {
					firstDTO.setLetto(hasLetto);
				}
			}

			// Aggiungo eventuali allegati al messaggio padre.
			if (!rispostaDTO.getAllegati().isEmpty())
				firstDTO.getAllegati().addAll(rispostaDTO.getAllegati());

			// Aggiungo la risposta al messaggio principale.
			firstDTO.getRisposte().add(rispostaDTO);
		}
		return firstDTO;
	}

	/**
	 * Partendo dall'ID di un ticket di tipo "richiesta all'esperto", ne costruisco il DTO. Poi recupero la lista delle
	 * risposte e le inserisco nel DTO, con eventuali allegati.
	 */
	public MsgEspertoDTO findMsgEspertoByTicket(String ticket, Integer principalId) {
		// Estraggo tutti i messaggi appartenenti a questo ticket, in ordine cronologico.
		TypedQuery<MsgMessaggio> messaggiQuery = entityManager.createNamedQuery("findAllByTicketAsc",
				MsgMessaggio.class);
		messaggiQuery.setParameter("ticket", ticket);
		List<MsgMessaggio> messaggi = messaggiQuery.getResultList();
		if (messaggi == null || messaggi.size() < 1) {
			throw new EJBException("Nessun messaggio trovato con ticket " + ticket + " e principal_id " + principalId);
		}

		// Creo il DTO del messaggio capostipite del ticket.
		MsgEspertoDTO firstDTO = new MsgEspertoDTO();
		popolaDTO(firstDTO, messaggi.get(0));
		popolaMessaggioDTO(firstDTO, messaggi.get(0));

		// Controllo se il messaggio era già stato letto o no.
		Boolean hasLetto = hasLettos(principalId, messaggi.get(0).getIdMsgMessaggio());
		firstDTO.setLetto(hasLetto);

		// Creo dei DTO anche per gli altri messaggi del ticket.
		for (int i = 1; i < messaggi.size(); i++) {
			MsgMessaggio risposta = messaggi.get(i);
			MsgMessaggioDTO rispostaDTO = new MsgMessaggioDTO();
			popolaDTO(rispostaDTO, risposta);
			popolaMessaggioDTO(rispostaDTO, risposta);

			// Controllo se la risposta è stata letta.
			// Se la risposta non è stata letta, segno come "non letto" anche il messaggio principale.
			if (risposta.getPfPrincipalFrom().getIdPfPrincipal().intValue() != principalId.intValue()) {
				hasLetto = hasLettos(principalId, risposta.getIdMsgMessaggio());
				rispostaDTO.setLetto(hasLetto);
				if (!hasLetto) {
					firstDTO.setLetto(false);
				}
			}

			// Aggiungo gli allegati al messaggio padre.
			if (!rispostaDTO.getAllegati().isEmpty())
				firstDTO.getAllegati().addAll(rispostaDTO.getAllegati());

			// Aggiungo la risposta al DTO del ticket.
			firstDTO.getRisposte().add(rispostaDTO);
		}
		return firstDTO;
	}

	/**
	 * Partendo dall'ID di un ticket di supporto, ne costruisco il DTO. Poi recupero la lista delle risposte e le
	 * inserisco nel DTO, e poi anche i curriculum, le lettere e gli allegati relativi.
	 */
	public MsgSupportoDTO findMsgSupportoByTicket(String ticket, Integer principalId) {

		// Estraggo tutti i messaggi appartenenti a questo ticket, dal primo all'ultimo.
		TypedQuery<MsgMessaggio> messaggiQuery = entityManager.createNamedQuery("findAllByTicketAsc",
				MsgMessaggio.class);
		messaggiQuery.setParameter("ticket", ticket);
		List<MsgMessaggio> messaggi = messaggiQuery.getResultList();
		if (messaggi == null || messaggi.size() < 1) {
			throw new EJBException("Nessun messaggio trovato con ticket " + ticket + " e principal_id " + principalId);
		}

		// Creo un DTO corrispondente al primo messaggio del ticket.
		MsgSupportoDTO firstDTO = new MsgSupportoDTO();
		popolaDTO(firstDTO, messaggi.get(0));
		popolaMessaggioDTO(firstDTO, messaggi.get(0));

		// Segno se ho letto o no il messaggio.
		Boolean hasLetto = hasLettos(principalId, messaggi.get(0).getIdMsgMessaggio());
		firstDTO.setLetto(hasLetto);

		// Scorro la lista delle risposte relative al ticket.
		// Trasformo ciascuna risposta in DTO e la aggiungo al campo "risposte" del DTO del messaggio principale.
		for (int i = 1; i < messaggi.size(); i++) {
			MsgMessaggio risposta = messaggi.get(i);
			MsgMessaggioDTO rispostaDTO = new MsgMessaggioDTO();
			popolaDTO(rispostaDTO, risposta);
			popolaMessaggioDTO(rispostaDTO, risposta);

			// Segno se ho letto o no la risposta.
			// Se non ho letto una risposta, segno come "non letto" anche il messaggio principale.
			if (risposta.getPfPrincipalFrom().getIdPfPrincipal().intValue() != principalId.intValue()) {
				hasLetto = hasLettos(principalId, risposta.getIdMsgMessaggio());
				rispostaDTO.setLetto(hasLetto);
				if (!hasLetto) {
					firstDTO.setLetto(false);
				}
			}

			// Aggiungo eventuali allegati.
			if (!rispostaDTO.getAllegati().isEmpty())
				firstDTO.getAllegati().addAll(rispostaDTO.getAllegati());

			// Aggiungo la risposta alla lista.
			firstDTO.getRisposte().add(rispostaDTO);
		}

		// Aggiungo al DTO eventuali curricula del messaggio capostipite.
		Set<CvDatiPersonali> curricula = messaggi.get(0).getCurricula();
		if (curricula != null) {
			for (CvDatiPersonali curriculum : curricula) {
				String codice = curriculum.getIdCvDatiPersonali().toString();
				String descrizione = curriculum.getDescrizione();
				if (ConstantsSingleton.COD_REGIONE == ConstantsSingleton.COD_REGIONE_RER) { 
					firstDTO.getListaCurricula().add(new GenericDecodeDTO(codice, descrizione,curriculum.getFlagIdo()));
				}else {
				    firstDTO.getListaCurricula().add(new GenericDecodeDTO(codice, descrizione));
				}
			}
		}

		// Aggiungo al DTO eventuali lettere del messaggio capostipite.
		Set<CvLetteraAcc> lettere = messaggi.get(0).getLettere();
		if (lettere != null) {
			for (CvLetteraAcc lettera : lettere) {
				String codice = lettera.getIdCvLetteraAcc().toString();
				String descrizione = lettera.getNome();
				firstDTO.getListaLettere().add(new GenericDecodeDTO(codice, descrizione));
			}
		}

		// Aggiungo al DTO eventuali allegati relativi al messaggio capostipite.
		Set<MsgAllegato> allegati = messaggi.get(0).getMsgAllegatos();
		if (allegati != null) {
			for (MsgAllegato allegato : allegati) {
				String filename = allegato.getFilename();
				MsgAllegatoDTO e = new MsgAllegatoDTO();
				Integer idMsgAllegato = allegato.getIdMsgAllegato();
				e.setId(idMsgAllegato);
				e.setFilename(filename);
				firstDTO.getAllegati().add(e);
				// un solo allegato consentito
				firstDTO.setIdMsgAllegato(idMsgAllegato);
				firstDTO.setAllegatoFileName(filename);
			}
		}

		// Restituisco il DTO risultante.
		return firstDTO;
	}

	/**
	 * Partendo dall'ID di un messaggio "certificato", ne costruisco il DTO. Poi recupero la lista delle risposte e le
	 * inserisco nel DTO, con eventuali allegati.
	 */
	public MsgCertificatoDTO findMsgCertificatoByTicket(String ticket, Integer principalId) {

		// Recupero tutti i messaggi relativi a questo ticket, in ordine cronologico.
		TypedQuery<MsgMessaggio> messaggiQuery = entityManager.createNamedQuery("findAllByTicketAsc",
				MsgMessaggio.class);
		messaggiQuery.setParameter("ticket", ticket);
		List<MsgMessaggio> messaggi = messaggiQuery.getResultList();

		if (messaggi == null || messaggi.size() < 1) {
			throw new EJBException("Nessun messaggio trovato con ticket " + ticket + " e principal_id " + principalId);
		}

		// Costruisco il DTO del messaggio principale del ticket.
		MsgCertificatoDTO firstDTO = new MsgCertificatoDTO();
		popolaDTO(firstDTO, messaggi.get(0));
		popolaMessaggioDTO(firstDTO, messaggi.get(0));

		// Costruisco anche i DTO di tutte le risposte.
		for (int i = 1; i < messaggi.size(); i++) {
			MsgMessaggio risposta = messaggi.get(i);
			MsgMessaggioDTO rispostaDTO = new MsgMessaggioDTO();
			popolaDTO(rispostaDTO, risposta);
			popolaMessaggioDTO(rispostaDTO, risposta);

			// Se la risposta non è stata letta, segno come "non letto" il messaggio principale.
			if (risposta.getPfPrincipalFrom().getIdPfPrincipal().intValue() != principalId.intValue()) {
				Boolean hasLetto = hasLettos(principalId, risposta.getIdMsgMessaggio());
				rispostaDTO.setLetto(hasLetto);
				if (!hasLetto) {
					firstDTO.setLetto(hasLetto);
				}
			}

			// Aggiungo eventuali allegati al messaggio padre
			if (!rispostaDTO.getAllegati().isEmpty())
				firstDTO.getAllegati().addAll(rispostaDTO.getAllegati());

			// Aggiungo la risposta al DTO del mesaggio principale.
			firstDTO.getRisposte().add(rispostaDTO);
		}
		return firstDTO;
	}

	/**
	 * Segna un messaggio come letto o non letto. Nel caso l'utente in questione sia una provincia o un
	 * consulente/coordinatore, il messaggio risulterà letto anche da tutti gli utenti della stessa provincia o dello
	 * stesso ruolo.
	 * 
	 * @param messageId
	 *            il messaggio da segnare come letto
	 * @param read
	 *            letto o no? se è impostato a false segna il messaggio come 'non letto'.
	 */
	public void signAsRead(Integer pfPrincipalId, Integer messageId, boolean read) {
		Date now = new Date();
		signMsg(pfPrincipalId, messageId, read, now);
	}

	/**
	 * Questo metodo segna un messaggio come "letto" o "non letto" da un certo utente, aggiungendo/eliminando la entry
	 * appropriata dalla tabella msg_messaggio_letto.
	 */
	private void signMsg(Integer pfPrincipalId, Integer messageId, boolean read, Date now) {
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(pfPrincipalId);
		MsgMessaggio mex = findById(messageId);
		List<MsgMessaggioLetto> lettos = getLettos(pfPrincipalId, messageId);

		// Se devo segnare il messaggio come 'letto', creo una riga nella tabella msgMessaggioLetto.
		if (read && lettos.isEmpty()) {
			MsgMessaggioLetto letto = new MsgMessaggioLetto();
			letto.setDtmIns(now);
			letto.setDtmLettura(now);
			letto.setDtmMod(now);
			letto.setPfPrincipal(pfPrincipal);
			letto.setMsgMessaggio(mex);
			letto.setPfPrincipalIns(pfPrincipal);
			letto.setPfPrincipalMod(pfPrincipal);
			msgMessaggioLettoHome.persist(letto);
		} else if (!read && !lettos.isEmpty()) {
			// Se devo segnare il messaggio come "non letto", rimuovo le righe dalla tabella msgMessaggioletto.
			for (MsgMessaggioLetto letto : lettos) {
				msgMessaggioLettoHome.remove(letto);
			}
		}
	}

	/**
	 * Scrive un nuovo messaggio sul database, partendo dal DTO. Non esegue niente di specifico per i vari tipi di
	 * messaggio. Vedere i metodi appositi:
	 * 
	 * @see persistMsgSupportoDTO
	 * @see persistMsgEspertoDTO
	 * @see persistMsgCertificatoDTO
	 */
	@Override
	public MsgMessaggioDTO persistDTO(MsgMessaggioDTO data, Integer idPrincipalIns) {

		// Se c'è un allegato da inserire, creo un MsgAllegatoDTO e lo metto nel DTO del messaggio.
		if (data.getAllegatoFileName() != null && data.getAllegatoFileNameTmp() != null) {
			String filename = data.getAllegatoFileName();
			String filenametemp = data.getAllegatoFileNameTmp();
			if (!filename.isEmpty() && !filenametemp.isEmpty()) {
				try {
					if (StringUtils.isNotBlank(data.getAllegatoFileName()) && StringUtils.isNotBlank(filenametemp)) {
						byte[] contenuto;
						contenuto = Utils.fileToByte(ConstantsSingleton.TMP_DIR + File.separator + filenametemp);
						MsgAllegatoDTO msgAllegato = new MsgAllegatoDTO();
						msgAllegato.setFilename(filename);
						msgAllegato.setContenuto(contenuto);
						data.getAllegati().add(msgAllegato);
					}
				} catch (Exception e) {
					throw new MyPortalException("messaggio.allegato.error", e);
				}
			}
		}

		// Se il messaggio non è di risposta (si tratta di un nuovo ticket) si rimuove l'eventuale chiave che
		// rappresenta una richiesta di assistenza da App
		if (data.getTicket() == null)
			data.setOggetto(StringUtils.strip(StringUtils.removeStart(data.getOggetto(),
					ConstantsSingleton.App.MSG_RICHIESTA_ASSISTENZA_OGGETTO_KEY)));

		// Aggiungo il messaggio al database con la "persist".
		MsgMessaggioDTO msg = super.persistDTO(data, idPrincipalIns);

		// Se il messaggio è il capostipite di un nuovo ticket, gli assegno il suo id come ticket.
		if (data.getTicket() == null) {
			msg.setTicket(msg.getId().toString());
			msg = mergeDTO(msg, idPrincipalIns);
		}

		// Segno il messaggio come "letto" dal suo creatore.
		signAsRead(idPrincipalIns, msg.getId(), true);
		msg.setLetto(true);

		// Salvo gli eventuali allegati.
		List<MsgAllegatoDTO> allegati = data.getAllegati();
		if (allegati != null) {
			for (MsgAllegatoDTO allegato : allegati) {
				allegato.setIdMsgMessaggio(msg.getId());
				msg.getAllegati().add(msgAllegatoHome.persistDTO(allegato, idPrincipalIns));
			}
		}

		/*
		 * Eventuale invio di notifica ad app
		 */
		if (data.getSendNotifyToApp()) {
			String titolo = null;
			String sottotitolo = null;
			String messaggio = null;
			TipoNotificaEnum tipoNotifica = null;

			if (StringUtils.isBlank(data.getTicket())) {
				// Nuovo ticket: per eventuali usi futuri...
				titolo = "Richiesta";
				sottotitolo = "Richiesta";
			} else {
				// Risposta ad un ticket esistente
				titolo = "Risposta";
				sottotitolo = "Risposta";
			}
			titolo += " ticket n° " + msg.getTicket();
			sottotitolo += " ticket n° " + msg.getTicket();
			messaggio = StringUtils.abbreviate(data.getCorpo(), 2000);

			// Se presente il destinatario il tipo di notifica è RISP_ASS, altrimenti BROADCAST
			if (data.getIdTo() != null)
				tipoNotifica = TipoNotificaEnum.RISP_ASS;
			else
				tipoNotifica = TipoNotificaEnum.BROADCAST;

			AppNotifica appNotifica = appNotificaHome.persistAndSend(titolo, sottotitolo, messaggio,
					ConstantsSingleton.App.ONESIGNAL_NOTIFICATION_DELIVERY_TIME_OF_DAY /* deliveryTimeOfDay */,
					data.getIdTo(), tipoNotifica, idPrincipalIns);

			if (appNotifica != null) {
				msg.setIdNotifyToApp(appNotifica.getIdAppNotifica());
				msg.setStatoNotifyToApp(appNotifica.getStato());
			}
		}

		return msg;
	}

	/**
	 * Crea un nuovo messaggio di supporto. Oltre a salvare il messaggio collega anche i CV e le lettere. Determina in
	 * automatico chi deve ricevere il messaggio basandosi sulla provincia di riferimento dell'utente e, se assente, sul
	 * comune di domicilio, se assente su quello di nascita, se ancora assente lancia un'eccezione.
	 */
	public MsgSupportoDTO persistMsgSupportoDTO(MsgSupportoDTO data, Integer principalId) {
		// Calcolo il destinatario del messaggio a seconda del mittente.
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(principalId);
		setDestinatario(data, pfPrincipal);

		// Salvo la parte "generica" del messaggio (non specifica dei messaggi di supporto).
		MsgMessaggioDTO msg = persistDTO(data, principalId);
		MsgMessaggio msgEntity = findById(msg.getId());

		// Salvo l'eventuale collegamento ai CV.
		List<IDecode> curricula = data.getListaCurricula();
		if (curricula != null) {
			for (IDecode cvDatiPersonali : curricula) {
				CvDatiPersonali curriculum = cvDatiPersonaliHome.findById(Integer.valueOf(cvDatiPersonali.getId()));
				msgEntity.getCurricula().add(curriculum);
			}
		}

		// Salvo l'eventuale collegamento alle lettere di accompagnamento.
		List<IDecode> lettere = data.getListaLettere();
		if (lettere != null) {
			for (IDecode lettera : lettere) {
				CvLetteraAcc letteraEntity = cvLetteraAccHome.findById(Integer.valueOf(lettera.getId()));
				msgEntity.getLettere().add(letteraEntity);
			}
		}

		// Restituisco il DTO "aggiornato".
		return findMsgSupportoByTicket(msg.getTicket(), principalId);
	}

	/**
	 * Imposta il destinatario in un messaggio in base alla provincia di riferimento dell'utente. Se è un cittadino
	 * verifica la provincia riferimento, comune di domicilio e comune di nascita. Se è un'azienda verifica la provincia
	 * del comune dove vi è la sede.
	 */
	public void setDestinatario(MsgMessaggioDTO messaggio, PfPrincipal pfPrincipal) {
		DeProvincia provincia = null;
		if (pfPrincipal.isUtente()) {
			// Se chi invia è un cittadino:
			UtenteInfo utenteInfo = pfPrincipal.getUtenteInfo();
			if (utenteInfo.getDeProvincia() != null) {
				provincia = utenteInfo.getDeProvincia();
			} else if (utenteInfo.getDeComuneDomicilio() != null) {
				provincia = utenteInfo.getDeComuneDomicilio().getDeProvincia();
			} else if (utenteInfo.getDeComuneNascita() != null) {
				provincia = utenteInfo.getDeComuneNascita().getDeProvincia();
			} else {
				throw new EJBException("Impossibile spedire il messaggio per l'utente '" + pfPrincipal.getUsername()
						+ "' in quanto non ha una provincia di riferimento.");
			}
		} else if (pfPrincipal.isAzienda()) {
			// Se chi invia è un'azienda:
			provincia = aziendaInfoHome.getProvinciaRiferimento(pfPrincipal.getAziendaInfo());
		} else {
			throw new EJBException("Impossibile identificare il tipo di utente.");
		}

		messaggio.setProvinciaTo(deProvinciaHome.toDTO(provincia));
		log.info("Il messaggio dell'utente '" + pfPrincipal.getUsername() + "' verrà  inviato alla provincia "
				+ provincia.getDenominazione());
	}

	/**
	 * Crea un nuovo messaggio di tipo "richiesta all'esperto".
	 */
	public MsgEspertoDTO persistMsgEspertoDTO(MsgEspertoDTO data, Integer principalId) {
		// Calcolo il destinatario in base al mittente.
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(principalId);
		setDestinatario(data, pfPrincipal);

		// Salvo il messaggio sul database e restituisco il DTO "aggiornato".
		MsgMessaggioDTO msg = persistDTO(data, principalId);
		return findMsgEspertoByTicket(msg.getTicket(), principalId);
	}

	/**
	 * Crea un nuovo messaggio di tipo "Richiesta Certificato".
	 */
	public MsgCertificatoDTO persistMsgCertificatoDTO(MsgCertificatoDTO data, Integer principalId) {
		// Calcolo il destinatario in base al mittente.
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(principalId);
		setDestinatario(data, pfPrincipal);

		// Salvo il messaggio sul database e restituisco il DTO "aggiornato".
		MsgMessaggioDTO msg = persistDTO(data, principalId);
		return findMsgCertificatoByTicket(msg.getTicket(), principalId);
	}

	/**
	 * Restituisce il numero di messaggi non letti da un utente per ogni tipologia. La mappa è nel formato tipo-numero e
	 * non restituisce mai null come numero ma 0 al massimo. (Non fa altro che chiamare la funzione specifica a seconda
	 * del tipo di utente).
	 */
	public Map<String, Integer> findNumMessaggiNonLetti(Integer pfPrincipalId) {
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(pfPrincipalId);
		if (pfPrincipal.isUtente()) {
			return findNumMessaggiNonLettiUtente(pfPrincipalId);
		} else if (pfPrincipal.isAzienda()) {
			return findNumMessaggiNonLettiUtente(pfPrincipalId);
		} else if (pfPrincipal.isProvincia()) {
			return findNumMessaggiNonLettiProvincia(pfPrincipalId);
		} else if (pfPrincipal.isRegione()) {
			return findNumMessaggiNonLettiProvincia(pfPrincipalId);
		} else {
			throw new EJBException("Impossibile recuperare il numero di messaggi non letti.");
		}
	}

	/**
	 * Questo metodo restituisce il numero di messaggi non letti dall'utente per tipo. Da usare per utenti "cittadino" o
	 * "azienda".
	 */
	private Map<String, Integer> findNumMessaggiNonLettiUtente(Integer principalId) {
		// Recupero dati sull'utente attuale e se è una provincia, quale.
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(principalId);

		// Inizio a costruire la query
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Object[]> query = qb.createQuery(Object[].class);
		Root<MsgMessaggio> messaggio = query.from(MsgMessaggio.class);
		List<Predicate> whereConditions = new ArrayList<Predicate>();
		Join<MsgMessaggio, PfPrincipal> principalTo = messaggio.join(MsgMessaggio_.pfPrincipalTo, JoinType.LEFT);

		// Condizione 1: seleziono i messaggi destinati all'utente O alla provincia O
		// al ruolo, O broadcast.
		Predicate conditionPrincipalTo = qb.equal(principalTo.get(PfPrincipal_.idPfPrincipal), principalId);
		Predicate conditionProvinciaTo = qb.or(); // Falso di default, se non è una provincia
		if (pfPrincipal.isProvincia()) {
			DeProvincia provinciaUtente = pfPrincipal.getProvinciasForIdPfPrincipal().iterator().next()
					.getDeProvincia();
			conditionProvinciaTo = qb.equal(messaggio.get(MsgMessaggio_.deProvinciaTo), provinciaUtente);
		}
		Predicate conditionBroadcast = qb.and(qb.isNull(messaggio.get(MsgMessaggio_.deProvinciaTo)),
				qb.isNull(messaggio.get(MsgMessaggio_.pfPrincipalTo)),
				qb.isNull(messaggio.get(MsgMessaggio_.deRuoloPortaleTo)));
		Predicate conditionRuoloTo = qb.equal(messaggio.get(MsgMessaggio_.deRuoloPortaleTo),
				pfPrincipal.getDeRuoloPortale());

		// Condizione 2: seleziono i messaggi che NON siano di tipo "atipico".
		DeTipoMessaggio tipoMsgAtipico = deTipoMessaggioHome.findById("ATIP");
		Predicate conditionD = qb.notEqual(messaggio.get(MsgMessaggio_.deTipoMessaggio), tipoMsgAtipico);
		whereConditions.add(qb.or(conditionPrincipalTo, conditionProvinciaTo, conditionRuoloTo, conditionBroadcast));
		whereConditions.add(qb.and(conditionD));

		// Suddivido i messaggi per tipo (tramite un join con la tabella "DeTipoMessaggio") e
		// conto il numero di messaggi per ogni tipo.
		Join<MsgMessaggio, DeTipoMessaggio> tipoMessaggio = messaggio.join(MsgMessaggio_.deTipoMessaggio);
		query.select(qb.array(tipoMessaggio.get(DeTipoMessaggio_.codTipoMessaggio), qb.count(messaggio)));

		// Creo una subquery che seleziona gli ID di tutti i messaggi che ho già
		// letto.
		Subquery<Integer> subquery = query.subquery(Integer.class);
		Root<MsgMessaggioLetto> subMessaggioLetto = subquery.from(MsgMessaggioLetto.class);
		Join<MsgMessaggioLetto, MsgMessaggio> subMessaggio = subMessaggioLetto.join(MsgMessaggioLetto_.msgMessaggio);
		subquery.select(subMessaggio.get(MsgMessaggio_.idMsgMessaggio));
		subquery.where(qb.equal(subMessaggioLetto.get(MsgMessaggioLetto_.pfPrincipal), pfPrincipal));
		// Fine della subquery. I messaggi già letti da questo utente sono stati
		// selezionati.

		// Condizione 3: i messaggi già letti vengono esclusi dalla query
		// principale.
		whereConditions.add(qb.not(messaggio.get(MsgMessaggio_.idMsgMessaggio).in(subquery)));

		// Condizione 4: seleziono solo i messaggi non scaduti.
		Predicate dataScadenza = qb.or(qb.greaterThan(messaggio.get(MsgMessaggio_.dtScadenza), new Date()),
				qb.isNull(messaggio.get(MsgMessaggio_.dtScadenza)));
		whereConditions.add(dataScadenza);

		// Eseguo effettivamente la query.
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		query.groupBy(tipoMessaggio.get(DeTipoMessaggio_.codTipoMessaggio));
		TypedQuery<Object[]> q = entityManager.createQuery(query);
		List<Object[]> list = q.getResultList();

		// Costruisco una HashMap personalizzata, che restituisce "0" invece di
		// "null"
		// se si prova a fare get su una key che non esiste.
		Map<String, Integer> ret = new HashMap<String, Integer>() {
			private static final long serialVersionUID = 147666768119277156L;

			@Override
			public Integer get(Object arg0) {
				Integer res = super.get(arg0);
				return res == null ? 0 : res;
			}
		};

		// Inserisco gli oggetti ritornati dalla query nella HashMap
		// personalizzata.
		// Nella mappa che restituisco, il tipo di messaggio è la chiave ed il
		// numero di messaggi è il valore.
		for (Object[] objects : list) {
			String codice = (String) objects[0];
			Long numero = (Long) objects[1];
			ret.put(codice, numero.intValue());
		}
		return ret;
	}

	/**
	 * Trova il numero di messaggi non ancora letti per ciascun tipo di messaggio. Da usare per gli utenti di tipo
	 * "provincia".
	 */
	private Map<String, Integer> findNumMessaggiNonLettiProvincia(Integer principalId) {
		PfPrincipal pfPrincipalCollegato = pfPrincipalHome.findById(principalId);

		// Costruisco la query.
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Object[]> query = qb.createQuery(Object[].class);
		Root<MsgMessaggio> messaggio = query.from(MsgMessaggio.class);
		List<Predicate> whereConditions = new ArrayList<Predicate>();
		Join<MsgMessaggio, DeTipoMessaggio> tipoMessaggio = messaggio.join(MsgMessaggio_.deTipoMessaggio);
		Join<MsgMessaggio, PfPrincipal> pfPrincipalTo = messaggio.join(MsgMessaggio_.pfPrincipalTo, JoinType.LEFT);

		// Condizione 1: Seleziono i messaggi inviati a me, O alla mia provincia, O al mio ruolo, O in broadcast.
		Predicate conditionDiretto = qb.equal(pfPrincipalTo.get(PfPrincipal_.idPfPrincipal), principalId);
		Predicate conditionProv = qb.or(); // Falso si default, se non è una provincia
		if (pfPrincipalCollegato.isProvincia()) {
			DeProvincia provinciaCollegata = pfPrincipalCollegato.getProvinciasForIdPfPrincipal().iterator().next()
					.getDeProvincia();
			conditionProv = qb.equal(messaggio.get(MsgMessaggio_.deProvinciaTo), provinciaCollegata);
		}
		Predicate conditionRuolo = qb.equal(messaggio.get(MsgMessaggio_.deRuoloPortaleTo),
				pfPrincipalCollegato.getDeRuoloPortale());
		Predicate conditionBroadcast = qb.and(qb.isNull(messaggio.get(MsgMessaggio_.deProvinciaTo)),
				qb.isNull(messaggio.get(MsgMessaggio_.pfPrincipalTo)),
				qb.isNull(messaggio.get(MsgMessaggio_.deRuoloPortaleTo)));
		whereConditions.add(qb.or(conditionProv, conditionBroadcast, conditionDiretto, conditionRuolo));

		// Condizione 2: Escludo i messaggi di tipo "atipico".
		DeTipoMessaggio tipoMsgAtipico = deTipoMessaggioHome.findById("ATIP");
		Predicate conditionD = qb.notEqual(messaggio.get(MsgMessaggio_.deTipoMessaggio), tipoMsgAtipico);
		whereConditions.add(qb.and(conditionD));

		// Suddivido i messaggi per tipo, e conto i messaggi per ogni tipo.
		query.select(qb.array(tipoMessaggio.get(DeTipoMessaggio_.codTipoMessaggio), qb.count(messaggio)));

		// Apro una subquery per selezionare tutti i messaggi che ho già letto.
		Subquery<Integer> subquery = query.subquery(Integer.class);
		Root<MsgMessaggioLetto> subMessaggioLetto = subquery.from(MsgMessaggioLetto.class);
		Join<MsgMessaggioLetto, MsgMessaggio> subMessaggio = subMessaggioLetto.join(MsgMessaggioLetto_.msgMessaggio);

		// QUA c'è l'unica differenza con l'altro metodo!
		// E' una pezza, ma seleziono tutti i messaggi letti da qualcuno con ruolo "provincia".
		Join<MsgMessaggioLetto, PfPrincipal> pfPrincipal = subMessaggioLetto.join(MsgMessaggioLetto_.pfPrincipal);
		Join<PfPrincipal, DeRuoloPortale> deRuoloPortaleJoin = pfPrincipal.join(PfPrincipal_.deRuoloPortale);
		subquery.select(subMessaggio.get(MsgMessaggio_.idMsgMessaggio));
		subquery.where(qb.equal(deRuoloPortaleJoin.get(DeRuoloPortale_.codRuoloPortale),
				ConstantsSingleton.DeRuoloPortale.PROVINCIA));
		// Fine della subquery. Sono stati selezionati i messaggi già letti.

		// Condizione 3: I messaggi già letti vengono esclusi dalla query
		// principale.
		whereConditions.add(qb.not(messaggio.get(MsgMessaggio_.idMsgMessaggio).in(subquery)));

		// Condizione 4: I messaggi non devono essere scaduti.
		Predicate dataScadenza = qb.or(qb.greaterThan(messaggio.get(MsgMessaggio_.dtScadenza), new Date()),
				qb.isNull(messaggio.get(MsgMessaggio_.dtScadenza)));
		whereConditions.add(dataScadenza);

		// Eseguo effettivamente la query.
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		query.groupBy(tipoMessaggio.get(DeTipoMessaggio_.codTipoMessaggio));
		TypedQuery<Object[]> q = entityManager.createQuery(query);
		List<Object[]> list = q.getResultList();

		// Creo una HashMap personalizzata, che restituisce 0 invece di "null"
		// se si fa una ricerca su una chiave che non corrisponde a nulla.
		Map<String, Integer> ret = new HashMap<String, Integer>() {
			private static final long serialVersionUID = 147666768119277156L;

			@Override
			public Integer get(Object arg0) {
				Integer res = super.get(arg0);
				return res == null ? 0 : res;
			}
		};

		// Inserisco i risultati della query nella HashMap personalizzata.
		// Chiavi: tipi di messaggio. Valori: numero di messaggi non letti di
		// quel tipo.
		for (Object[] objects : list) {
			String codice = (String) objects[0];
			Long numero = (Long) objects[1];
			ret.put(codice, numero.intValue());
		}
		return ret;
	}

	/**
	 * Determina se un messaggio è stato inviato da un utente che appartiene alla stessa provincia dell'utente corrente.
	 */
	public Boolean isGroupProprietary(Integer idMsgMessaggio, Integer idPfPrincipal) {
		PfPrincipal pfPrincipal = pfPrincipalHome.findById(idPfPrincipal);
		MsgMessaggio messaggio = findById(idMsgMessaggio);

		if (pfPrincipal.isProvincia()) {
			DeProvincia provinciaUtente = pfPrincipal.getProvinciasForIdPfPrincipal().iterator().next()
					.getDeProvincia();
			DeProvincia provinciaMsg = messaggio.getDeProvinciaTo();
			if (provinciaMsg != null && provinciaUtente != null) {
				return (provinciaMsg.getCodProvincia().equals(provinciaUtente.getCodProvincia()));
			}
		}

		return false;
	}

	/**
	 * Elimina dal messaggio corridpondente a idMsgMessaggio il riferimento al CV corrispondente a idCvDatiPersonali.
	 */
	public void removeCurriculumReference(Integer idMsgMessaggio, Integer idCvDatiPersonali) {
		MsgMessaggio msgMessaggio = findById(idMsgMessaggio);

		Set<CvDatiPersonali> curricula = msgMessaggio.getCurricula();
		for (CvDatiPersonali cvDatiPersonali : curricula) {
			if (cvDatiPersonali.getIdCvDatiPersonali().equals(idCvDatiPersonali)) {
				curricula.remove(cvDatiPersonali);
				break;
			}
		}

		merge(msgMessaggio);
	}

	/**
	 * Elimina dal messaggio corridpondente a idMsgMessaggio il riferimento alla lettera corrispondente a idCvLetteraAcc
	 */
	public void removeLetteraReference(Integer idMsgMessaggio, Integer idCvLetteraAcc) {
		MsgMessaggio msgMessaggio = findById(idMsgMessaggio);

		Set<CvLetteraAcc> lettere = msgMessaggio.getLettere();
		for (CvLetteraAcc lettera : lettere) {
			if (lettera.getIdCvLetteraAcc().equals(idCvLetteraAcc)) {
				lettere.remove(lettera);
				break;
			}
		}

		merge(msgMessaggio);
	}

	/**
	 * Restituisce tutti i messaggi di un determinato tipo.
	 */
	public List<MsgMessaggio> findAll(String tipoMessaggio) {
		TypedQuery<MsgMessaggio> q = entityManager.createNamedQuery("findMsgMessaggioByTipo", MsgMessaggio.class);
		q.setParameter("tipoMessaggio", tipoMessaggio);
		return q.getResultList();
	}

	/**
	 * Restituisce il primo messaggio_cl con un determinato cod_comunicazione.
	 */
	public MsgMessaggioCl findByCodComunicazione(String codComunicazione) {
		TypedQuery<MsgMessaggioCl> q = entityManager.createNamedQuery("findMessaggioByCodComunicazione",
				MsgMessaggioCl.class);
		q.setParameter("codComunicazione", codComunicazione);
		List<MsgMessaggioCl> list = q.getResultList();
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}
}
