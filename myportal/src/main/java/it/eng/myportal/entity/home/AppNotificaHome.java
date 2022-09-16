package it.eng.myportal.entity.home;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import it.eng.myportal.dtos.AppNotificaDTO;
import it.eng.myportal.entity.AppNotifica;
import it.eng.myportal.entity.AppNotifica_;
import it.eng.myportal.entity.CvDatiPersonali;
import it.eng.myportal.entity.PfIdentityDevice;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.PfPrincipal_;
import it.eng.myportal.entity.Provincia;
import it.eng.myportal.entity.RvAttivita;
import it.eng.myportal.entity.RvContratto;
import it.eng.myportal.entity.RvLingua;
import it.eng.myportal.entity.RvMansione;
import it.eng.myportal.entity.RvOrario;
import it.eng.myportal.entity.RvPatente;
import it.eng.myportal.entity.RvTestata;
import it.eng.myportal.entity.RvTestata_;
import it.eng.myportal.entity.RvTitolo;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.decodifiche.DeProvincia_;
import it.eng.myportal.entity.enums.TipoNotificaEnum;
import it.eng.myportal.rest.app.exception.NoRecipientException;
import it.eng.myportal.rest.app.exception.ProviderNotificationException;
import it.eng.myportal.rest.app.helper.AdditionalDataNotification;
import it.eng.myportal.rest.app.helper.AppUtils;
import it.eng.myportal.rest.app.helper.ProviderNotification;
import it.eng.myportal.rest.app.helper.ProviderNotificationFactory;
import it.eng.myportal.rest.app.helper.SolrQueryCreator;
import it.eng.myportal.rest.app.helper.SolrQueryCreatorFactory;
import it.eng.myportal.rest.app.helper.StatoNotifica;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;

/**
 * 
 * @author
 */
@Stateless
public class AppNotificaHome extends AbstractUpdatableHome<AppNotifica, AppNotificaDTO> {

	@EJB
	private PfIdentityDeviceHome pfIdentityDeviceHome;

	@EJB
	private RvTestataHome rvTestataHome;

	@EJB
	private ProvinciaHome provinciaHome;

	@EJB
	private AppNotificaHome appNotificaHome;

	public AppNotifica findById(Integer id) {
		return findById(AppNotifica.class, id);
	}

	public AppNotifica findBySid(String sid) {
		TypedQuery<AppNotifica> query = entityManager.createNamedQuery("appNotifica.findBySid", AppNotifica.class);

		query.setParameter("sidNotifica", sid);

		// E' presente indice univoco per sidNotifica, viene garantita l'univocità
		AppNotifica ret = query.getSingleResult();

		return ret;
	}

	@Override
	public AppNotificaDTO toDTO(AppNotifica entity) {
		if (entity == null)
			return null;

		HashMap<StatoNotifica, Integer> countStato = null;

		if (entity.getSidNotifica() != null) {
			if (entity.isBroadcast() || entity.getStato() == null || entity.getStato().isModificabile()) {
				// Notifica broadcast o notifica puntuale il cui stato non è ancora presente o non finale, lo si
				// recupera dal provider

				try {
					countStato = aggiornaStatoNotifica(entity);

				} catch (ProviderNotificationException e) {
					/*
					 * Per un'eventuale eccezione in fase di recupero dello stato della notifica non è necessaria alcuna
					 * rollback della transazione, anzi è dannosa nel caso in cui si arrivi qui post persist.
					 */
					log.error("Errore nella chiamata al provider di notifica per il recupero stato della notifica");
				}
			}
		}

		AppNotificaDTO dto = super.toDTO(entity);
		dto.setId(entity.getIdAppNotifica());
		dto.setTitolo(entity.getTitolo());
		dto.setSottotitolo(entity.getSottotitolo());
		dto.setMessaggio(entity.getMessaggio());

		// Per il destinatario si ritorna l'id e l'email
		if (entity.getPfPrincipalDest() != null) {
			dto.setIdPfPrincipalDest(entity.getPfPrincipalDest().getIdPfPrincipal());
			dto.setEmail(entity.getPfPrincipalDest().getEmail());
		}

		dto.setSidNotifica(entity.getSidNotifica());
		dto.setTipoNotifica(entity.getTipoNotifica());
		dto.setStato(entity.getStato());

		if (countStato != null) {
			dto.setNumSend(countStato.get(StatoNotifica.S));
			dto.setNumRead(countStato.get(StatoNotifica.R));
			dto.setNumFail(countStato.get(StatoNotifica.F));
			dto.setNumDelay(countStato.get(StatoNotifica.D));
		}

		return dto;
	}

	public void modificaStato(AppNotifica entity, StatoNotifica statoNotifica, boolean forceModificaStato,
			PfPrincipal pfPrincipalMod) {

		if (forceModificaStato || entity.getStato() == null || entity.getStato().isModificabile()) {
			entity.setStato(statoNotifica);
			// La data di modifica viene aggiornata in automatico dal listener
			if (pfPrincipalMod != null)
				entity.setPfPrincipalMod(pfPrincipalMod);

			this.merge(entity);
		}
	}

	@Override
	public AppNotifica fromDTO(AppNotificaDTO dto) {
		if (dto == null)
			return null;
		AppNotifica entity = super.fromDTO(dto);

		entity.setIdAppNotifica(dto.getId());

		if (dto.getEmail() != null) {
			// Recupero del PfPrincipal attraverso l'email. Non viene utilizzato
			// idPfPrincipal per gestire la casistica il cui l'operatore non ha selezionato
			// l'elemento dalla lista (ma ha scritto solamente l'email)

			/*
			 * Recupero della provincia di riferimento dal pfPrincipal di inserimento da utilizzare per recuperare i
			 * PfIdentityDevice rispetto all'email ma che fanno parte della provincia di riferimento (ciò evita di poter
			 * agganciare utenti fuori provincia di riferimento)
			 */
			Provincia provincia = provinciaHome.findByIdPfPrincipal(entity.getPfPrincipalMod().getIdPfPrincipal());

			List<PfIdentityDevice> listPfPrincipal = pfIdentityDeviceHome.findUsersApp(dto.getEmail(),
					provincia.getDeProvincia().getCodProvincia());

			if (listPfPrincipal == null || listPfPrincipal.isEmpty()) {
				log.error("Non è stato trovato alcun utente con l'email indicata");
				throw new EJBException("Non è stato trovato alcun utente con l'email indicata");
			} else if (listPfPrincipal.size() > 1) {
				log.error("E' stato trovato più di un utente con l'email indicata");
				throw new EJBException("E' stato trovato più di un utente attivo con l'email indicata");
			} else if (!listPfPrincipal.get(0).getPfPrincipal().isUtente()) {
				log.error("L'utente destinatario della notifica non è un cittadino");
				throw new EJBException("L'utente destinatario della notifica non è un cittadino");
			} else {
				entity.setPfPrincipalDest(listPfPrincipal.get(0).getPfPrincipal());
				entity.setDeProvincia(AppUtils.getDeProvinciaRiferimento(listPfPrincipal.get(0).getPfPrincipal()));
			}
		}

		// Se presente il destinatario il tipo di notifica è PUNT_CPI, altrimenti BROADCAST
		if (entity.getPfPrincipalDest() != null)
			entity.setTipoNotifica(TipoNotificaEnum.PUNT_CPI);
		else
			entity.setTipoNotifica(TipoNotificaEnum.BROADCAST);

		if (StringUtils.isNotBlank(dto.getTitolo())) {
			entity.setTitolo(dto.getTitolo());
		}
		if (StringUtils.isNotBlank(dto.getSottotitolo())) {
			entity.setSottotitolo(dto.getSottotitolo());
		}
		if (StringUtils.isNotBlank(dto.getMessaggio())) {
			entity.setMessaggio(dto.getMessaggio());
		}

		if (StringUtils.isNotBlank(dto.getSidNotifica())) {
			entity.setSidNotifica(dto.getSidNotifica());
		}

		entity.setStato(dto.getStato());

		return entity;
	}

	@Override
	public AppNotificaDTO persistDTO(AppNotificaDTO data, Integer idPrincipalIns) {

		Date now = new Date();
		data.setDtmIns(now);
		data.setDtmMod(now);
		data.setIdPrincipalIns(idPrincipalIns);
		data.setIdPrincipalMod(idPrincipalIns);

		AppNotifica entity = fromDTO(data);
		// Dati addizionali
		AdditionalDataNotification additionalData = new AdditionalDataNotification(entity, null);

		persistAndSend(entity, additionalData,
				ConstantsSingleton.App.ONESIGNAL_NOTIFICATION_DELIVERY_TIME_OF_DAY /* deliveryTimeOfDay */);
		return toDTO(entity);
	}

	@Override
	public AppNotificaDTO mergeDTO(AppNotificaDTO data, Integer idPrincipalMod) {

		AppNotificaDTO dto = super.mergeDTO(data, idPrincipalMod);

		return dto;
	}

	public HashMap<StatoNotifica, Integer> aggiornaStatoNotifica(AppNotifica appNotifica)
			throws ProviderNotificationException {

		StatoNotifica statoIniziale = appNotifica.getStato();

		HashMap<StatoNotifica, Integer> countStato = this.getStatoNotificaCount(appNotifica);
		StatoNotifica stato = this.getStatoNotifica(countStato);

		if (!stato.equals(statoIniziale)) {
			modificaStato(appNotifica, stato, false, null);
		}

		return countStato;
	}

	/**
	 * Invio della notifica
	 * 
	 * @param data
	 * @param additionalData
	 * @param deliveryTimeOfDay
	 * @return
	 * @throws ProviderNotificationException
	 */
	private String sendNotification(AppNotifica data, AdditionalDataNotification additionalData,
			String deliveryTimeOfDay) throws ProviderNotificationException {
		/*
		 * -------------------- Invio Notifica al Provider --------------------
		 */
		ProviderNotification provider = ProviderNotificationFactory
				.getProviderNotification(ProviderNotification.LAVORO_PER_TE_PROJECT_ID);
		String sidNotifica = (String) provider.send(data.getTitolo(), data.getSottotitolo(), data.getMessaggio(),
				data.getPfPrincipalDest() != null ? data.getPfPrincipalDest().getEmail() : null, additionalData,
				deliveryTimeOfDay);

		return sidNotifica;
	}

	/**
	 * Recupero degli stati con rispettivi contatori
	 * 
	 * @param sidNotifica
	 * @return
	 * @throws ProviderNotificationException
	 */
	@SuppressWarnings("unchecked")
	private HashMap<StatoNotifica, Integer> getStatoNotificaCount(AppNotifica data)
			throws ProviderNotificationException {
		/*
		 * -------------------- Recupero dello stato notifica --------------
		 */
		HashMap<StatoNotifica, Integer> stato = null;

		if (data.getSidNotifica() != null) {
			ProviderNotification provider = ProviderNotificationFactory
					.getProviderNotification(ProviderNotification.LAVORO_PER_TE_PROJECT_ID);
			stato = (HashMap<StatoNotifica, Integer>) provider.view(data.getSidNotifica());
		}

		return stato;
	}

	private StatoNotifica getStatoNotifica(HashMap<StatoNotifica, Integer> stato) {
		StatoNotifica ret = null;

		if (stato != null) {
			if (stato.containsKey(StatoNotifica.R) && stato.get(StatoNotifica.R) > 0) {
				ret = StatoNotifica.R;
			} else if (stato.containsKey(StatoNotifica.S) && stato.get(StatoNotifica.S) > 0) {
				ret = StatoNotifica.S;
			} else if (stato.containsKey(StatoNotifica.D) && stato.get(StatoNotifica.D) > 0) {
				ret = StatoNotifica.D;
			} else if (stato.containsKey(StatoNotifica.F) && stato.get(StatoNotifica.F) > 0) {
				ret = StatoNotifica.F;
			}
		}

		return ret;
	}

	@Override
	public void persist(AppNotifica data) {
		throw new UnsupportedOperationException(
				"Non è possibile inserire una notifica senza contestuale invio della stessa");
	};

	public void persistAndSend(AppNotifica data, AdditionalDataNotification additionalData, String deliveryTimeOfDay) {
		try {

			/*
			 * Fasi per inserimento notifica: - persist di AppNotifica; - send della notifica attraverso il provider; -
			 * update AppNotifica, attributi sidNotifica e stato post invio notifica;
			 * 
			 * La persist prima dell'invio della notifica (e non viceversa) evita, in caso di eccezione della persist,
			 * di inviare notifiche che poi non verranno salvate. Contemporaneamente, un'eccezione nell'invio della
			 * notifica del provider, viene gestita con il rilancio dell'eccezione EjbException per eseguire il rollback
			 * di tutta la transazione.
			 */
			if (additionalData != null) {
				// Serializzazione JSON dati addizionali
				data.setAdditionalData(additionalData.toJSONObject().toString());
			}
			super.persist(data);

			// Invio notifica attraverso il provider
			String sidNotifica = null;
			try {
				sidNotifica = sendNotification(data, additionalData, deliveryTimeOfDay);
				data.setSidNotifica(sidNotifica);
			} catch (NoRecipientException e) {
				// Nessun destinatario disponibile, si registra comunque la notifica in uno stato specifico.
				data.setSidNotifica(null);
				data.setStato(StatoNotifica.L);
			}

			// Recupero dello stato attraverso il provider
			if (sidNotifica != null) {
				try {
					HashMap<StatoNotifica, Integer> statoCount = this.getStatoNotificaCount(data);
					StatoNotifica stato = this.getStatoNotifica(statoCount);
					data.setStato(stato);
				} catch (ProviderNotificationException e) {
					/*
					 * Un'eventuale eccezione nel recupero dello stato della notifica a seguito di un invio con
					 * successo, non deve mandare in rollback la transazione: la notifica è già stata inviata!
					 */
					log.error("Errore nella chiamata persistAndSend in fase di recupero stato della notifica: " + e.getMessage());
				}
			}

		} catch (Exception e) {
			throw new EJBException("Errore in persistAndSend di AppNotifica: " + e.getMessage());
		}
	}

	public AppNotifica persistAndSend(String titolo, String sottotitolo, String messaggio, String deliveryTimeOfDay,
			Integer idPrincipalDest, TipoNotificaEnum tipoNotifica, Integer idPrincipalIns) {

		PfPrincipal pfPrincipalDest = null;
		DeProvincia deProvinciaRif = null;
		if (idPrincipalDest != null) {
			pfPrincipalDest = pfPrincipalHome.findById(idPrincipalDest);
			deProvinciaRif = AppUtils.getDeProvinciaRiferimento(pfPrincipalDest);
		}

		// Inserimento e invio della notifica
		AppNotifica appNotifica = new AppNotifica(titolo, sottotitolo, messaggio, deProvinciaRif, pfPrincipalDest,
				pfPrincipalHome.findById(idPrincipalIns), null /* sidNotifica */, tipoNotifica /* tipoNotifica */,
				null /* statoNotifica */);

		// Dati addizionali
		AdditionalDataNotification additionalData = new AdditionalDataNotification(appNotifica, null);

		this.persistAndSend(appNotifica, additionalData, deliveryTimeOfDay);

		return appNotifica;
	}

	/* Invia le notifiche ad uno specifico utente che ha effettuato l'accesso all'app */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<AppNotifica> sendNotificationBatch(Integer idPfPrincipalDest, PfPrincipal pfPrincipalIns) {

		List<AppNotifica> ret = new ArrayList<AppNotifica>();

		Class<RvTestata> clazz = (Class<RvTestata>) RvTestata.class;
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<RvTestata> cq = cb.createQuery(clazz);
		Root<RvTestata> r = cq.from(clazz);

		// Fetch
		r.fetch(RvTestata_.rvContrattos, JoinType.LEFT);
		r.fetch(RvTestata_.rvOrarios, JoinType.LEFT);
		r.fetch(RvTestata_.rvPatentes, JoinType.LEFT);
		r.fetch(RvTestata_.rvMansiones, JoinType.LEFT);
		r.fetch(RvTestata_.rvAttivitas, JoinType.LEFT);
		r.fetch(RvTestata_.rvLinguas, JoinType.LEFT);
		r.fetch(RvTestata_.rvTitolos, JoinType.LEFT);

		Predicate p = cb.conjunction();
		p = cb.and(p, cb.equal(r.get(RvTestata_.pfPrincipal).get(PfPrincipal_.idPfPrincipal), idPfPrincipalDest));
		cq.select(r).where(p).orderBy(cb.desc(r.get(RvTestata_.dtmMod)));
		TypedQuery<RvTestata> query = entityManager.createQuery(cq);

		// Eventuale paginazione
		// query.setFirstResult(0);
		// query.setMaxResults(5);

		List<RvTestata> listRvTestata = query.getResultList();

		if (listRvTestata != null && !listRvTestata.isEmpty()) {
			for (RvTestata rvTestata : listRvTestata) {
				Integer numUnreaded = getNumVacancyUnreaded(rvTestata);
				log.debug("Num. vacancies non lette: " + numUnreaded);

				if (numUnreaded != null && numUnreaded > 0) {
					// Recupero destinatario notifiche: lo si recupera solo qui, per evitare query non necessarie
					PfPrincipal pfPrincipalDest = pfPrincipalHome.findById(idPfPrincipalDest);
					// Validazione
					validaPfPrincipalDest(pfPrincipalDest);

					String titolo = "Nuove offerte di lavoro per la ricerca: " + rvTestata.getDescrizione();
					String sottotitolo = "Nuove offerte di lavoro per la ricerca: " + rvTestata.getDescrizione();
					String messaggio = "Per la ricerca salvata " + rvTestata.getDescrizione() + " sono presenti "
							+ numUnreaded + " nuove offerte di lavoro.";

					if (numUnreaded == 1) {
						titolo = titolo.replace("Nuove offerte", "Nuova offerta");
						sottotitolo = sottotitolo.replace("Nuove offerte", "Nuova offerta");
						messaggio = messaggio.replace("sono presenti", "è presente");
						messaggio = messaggio.replace("nuove offerte", "nuova offerta");
					}

					// Inserimento e invio della notifica
					AppNotifica appNotifica = new AppNotifica(titolo, sottotitolo, messaggio,
							AppUtils.getDeProvinciaRiferimento(rvTestata.getPfPrincipal()), rvTestata.getPfPrincipal(),
							pfPrincipalIns, null /* sidNotifica */, TipoNotificaEnum.RIC_SALV/* tipoNotifica */,
							null /* statoNotifica */);

					// Dati addizionali
					AdditionalDataNotification additionalData = new AdditionalDataNotification(appNotifica,
							rvTestata.getIdRvTestata());

					try {
						/*
						 * Nel momento in cui vengono recuperate le nuove vacancy si aggiorna la data di modifica delle
						 * stesse. In questo modo si riescono a determinare le nuove vacancy da inviare come notifica
						 * all'app.
						 */
						rvTestataHome.mergeDtmMod(rvTestata.getIdRvTestata());

						this.persistAndSend(appNotifica, additionalData,
								ConstantsSingleton.App.ONESIGNAL_NOTIFICATION_DELIVERY_TIME_OF_DAY_BATCH /*
																											 * deliveryTimeOfDay
																											 */);
						ret.add(appNotifica);

						/*
						 * La flush è necessaria per scaricare immediatamente l'update della data modifica (quindi
						 * eseguire anche la preUpdate di AbstractEntity) e non posticiparla nel momento della chiusura
						 * della transazione
						 */
						entityManager.flush();

						StringBuffer logMsg = new StringBuffer();
						logMsg.append("Invio notifica all'app. Per l'utente ")
								.append(rvTestata.getPfPrincipal().getUsername()).append(", la ricerca salvata '")
								.append(rvTestata.getDescrizione()).append("' ha ").append(numUnreaded)
								.append(" nuove offerte di lavoro.");

						if (appNotifica.getStato() == StatoNotifica.L) {
							logMsg.append(
									" Non è stato possibile recapitare la notifica poiché l'utente ha eseguito il logout dall'App.");
						}
						log.info(logMsg);

					} catch (Exception e) {
						throw new EJBException("Errore durante il salvataggio della notifica batch per l'utente: "
								+ rvTestata.getPfPrincipal().getUsername() + " per la ricerca salvata "
								+ rvTestata.getDescrizione() + " - " + e.getMessage());
					}
				}
			}
		}
		return ret;
	}

	private Integer getNumVacancyUnreaded(RvTestata rvTestata) {

		Integer ret = null;

		List<String> listaCodMansione = new ArrayList<String>();
		List<String> listaCodContratto = new ArrayList<String>();
		List<String> listaCodOrario = new ArrayList<String>();
		List<String> listaCodSettore = new ArrayList<String>();
		List<String> listaCodLingua = new ArrayList<String>();
		List<String> listaCodTitoloStudio = new ArrayList<String>();
		List<String> listaCodPatente = new ArrayList<String>();

		if (rvTestata.getRvContrattos() != null && !rvTestata.getRvContrattos().isEmpty()) {
			for (RvContratto rvContratto : rvTestata.getRvContrattos()) {
				if (ConstantsSingleton.usaDecodificheSilPerVacancy())
					listaCodContratto.add(rvContratto.getDeContrattoSil().getCodContrattoSil());
				else
					listaCodContratto.add(rvContratto.getDeContratto().getCodContratto());
			}
		}
		if (rvTestata.getRvOrarios() != null && !rvTestata.getRvOrarios().isEmpty()) {
			for (RvOrario rvOrario : rvTestata.getRvOrarios()) {
				if (ConstantsSingleton.usaDecodificheSilPerVacancy())
					listaCodOrario.add(rvOrario.getDeOrarioSil().getCodOrarioSil());
				else
					listaCodOrario.add(rvOrario.getDeOrario().getCodOrario());
			}
		}
		if (rvTestata.getRvPatentes() != null && !rvTestata.getRvPatentes().isEmpty()) {
			for (RvPatente rvPatente : rvTestata.getRvPatentes()) {
				if (ConstantsSingleton.usaDecodificheSilPerVacancy())
					listaCodPatente.add(rvPatente.getDePatenteSil().getCodPatenteSil());
				else
					listaCodPatente.add(rvPatente.getDePatente().getCodPatente());
			}
		}

		// La mansione non è prevista nel caso di usaDecodificheSilPerVacancy a true (in RvMansione non è presente la fk verso DeBpMansione)
		if (!ConstantsSingleton.usaDecodificheSilPerVacancy()) {
			if (rvTestata.getRvMansiones() != null && !rvTestata.getRvMansiones().isEmpty())
				for (RvMansione rvMansione : rvTestata.getRvMansiones())
					listaCodMansione.add(rvMansione.getDeMansione().getCodMansione());
		}
		if (rvTestata.getRvAttivitas() != null && !rvTestata.getRvAttivitas().isEmpty())
			for (RvAttivita rvAttivita : rvTestata.getRvAttivitas())
				listaCodSettore.add(rvAttivita.getDeAttivita().getCodAteco());
		if (rvTestata.getRvLinguas() != null && !rvTestata.getRvLinguas().isEmpty())
			for (RvLingua rvLingua : rvTestata.getRvLinguas())
				listaCodLingua.add(rvLingua.getDeLingua().getCodLingua());
		if (rvTestata.getRvTitolos() != null && !rvTestata.getRvTitolos().isEmpty())
			for (RvTitolo rvTitolo : rvTestata.getRvTitolos())
				listaCodTitoloStudio.add(rvTitolo.getDeTitolo().getCodTitolo());

		SolrQueryCreator creator = SolrQueryCreatorFactory.getSolrQueryCreator(rvTestata.getCosa(), rvTestata.getDove(), false /* getFacet */,
				null /* idVaDatiVacancy */, listaCodMansione, listaCodContratto, listaCodOrario, listaCodSettore,
				listaCodLingua, listaCodTitoloStudio, listaCodPatente, "0" /* start */, "1" /* rows */, null /* dist */,
				null /* lat */, null /* lon */, null /* vacancyValideAllaData */,
				rvTestata.getDtmMod() /* vacancyModificateAllaData */, null /* ordinamento */);

		String result = Utils.stringSOLR(creator.getUrlSolr(), creator.createParamsQuerySolr());

		try {
			JSONObject json = new JSONObject(result);

			if (json.has("response")) {
				json = json.getJSONObject("response");

				if (json.has("numFound")) {
					ret = json.getInt("numFound");
				}
			}
		} catch (JSONException e) {
			// Eccezione in parsing Json
			e.printStackTrace();
		}

		return ret;
	}

	private void validaPfPrincipalDest(PfPrincipal pfPrincipalDest) {
		if (pfPrincipalDest == null) {
			throw new IllegalArgumentException("Il destinatario non è presente");
		}

		// Validazione destinatario notifiche (utente cittadino)
		if (!pfPrincipalDest.isUtente()) {
			throw new IllegalArgumentException("Il destinatario non è un cittadino");
		} else if (pfPrincipalDest.getUtenteInfo() == null
				|| pfPrincipalDest.getUtenteInfo().getDeProvincia() == null) {
			throw new IllegalArgumentException("Il destinatario non ha la provincia di appartenenza");
		}
	}

	public List<AppNotifica> getNotificheBroadcast(List<StatoNotifica> listStato, Integer start, Integer rows) {
		TypedQuery<AppNotifica> query = entityManager.createNamedQuery("appNotifica.getNotificheBroadcast",
				AppNotifica.class);
		query.setParameter("listStato", listStato);

		if (start != null && rows != null) {
			query.setFirstResult(start);
			query.setMaxResults(rows);
		}
		return query.getResultList();
	}

	public Long getCountNotificheBroadcast(List<StatoNotifica> listStato) {
		TypedQuery<Long> query = entityManager.createNamedQuery("appNotifica.getCountNotificheBroadcast", Long.class);
		query.setParameter("listStato", listStato);

		Long ret = query.getSingleResult();

		return ret;
	}

	public List<AppNotifica> getNotifichePersonali(Integer idPfPrincipalDest, List<StatoNotifica> listStato,
			Integer start, Integer rows) {
		TypedQuery<AppNotifica> query = entityManager.createNamedQuery("appNotifica.getNotifichePersonali",
				AppNotifica.class);
		query.setParameter("idPfPrincipalDest", idPfPrincipalDest);
		query.setParameter("listStato", listStato);

		if (start != null && rows != null) {
			query.setFirstResult(start);
			query.setMaxResults(rows);
		}

		return query.getResultList();
	}

	public Long getCountNotifichePersonali(Integer idPfPrincipalDest, List<StatoNotifica> listStato) {
		TypedQuery<Long> query = entityManager.createNamedQuery("appNotifica.getCountNotifichePersonali", Long.class);
		query.setParameter("idPfPrincipalDest", idPfPrincipalDest);
		query.setParameter("listStato", listStato);

		Long ret = query.getSingleResult();

		return ret;
	}

	public Long getCountNuoveNotifichePersonali(Integer idPfPrincipalDest) {
		TypedQuery<Long> query = entityManager.createNamedQuery("appNotifica.getCountNuoveNotifichePersonali",
				Long.class);
		query.setParameter("idPfPrincipalDest", idPfPrincipalDest);
		query.setParameter("listStato", Arrays.asList(StatoNotifica.R, StatoNotifica.S));

		Long ret = query.getSingleResult();

		return ret;
	}

	public List<AppNotificaDTO> getNotificheInviate(String codProvincia) {
		return this.getNotificheInviate(codProvincia, null /* start */, null /* rows */);
	}

	public List<AppNotificaDTO> getNotificheInviate(String codProvincia, Integer start, Integer rows) {

		TypedQuery<AppNotifica> query = entityManager.createNamedQuery("appNotifica.getNotificheInviate",
				AppNotifica.class);
		query.setParameter("codProvincia", codProvincia);

		if (start != null && rows != null) {
			query.setFirstResult(start);
			query.setMaxResults(rows);
		}
		return findDTOByQuery(query);
	}

	public List<AppNotificaDTO> getNotificheInviate(String codProvincia, String email, String codStato,
			boolean escludiNotificheBatch, Date dtaDa, Date dtaA, Integer start, Integer rows) {

		// Costruisco la base della query.
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AppNotifica> query = cb.createQuery(AppNotifica.class);

		// Creo la clausola FROM della query.
		Root<AppNotifica> appNotifica = query.from(AppNotifica.class);
		Join<AppNotifica, PfPrincipal> pfPrincipalDest = appNotifica.join(AppNotifica_.pfPrincipalDest, JoinType.LEFT);
		Join<AppNotifica, DeProvincia> deProvincia = appNotifica.join(AppNotifica_.deProvincia, JoinType.LEFT);

		// Creo la clausola SELECT della query
		query.select(appNotifica);

		// Creo la clausola WHERE della query
		List<Predicate> whereConditions = createWhereConditions(codProvincia, email, codStato, escludiNotificheBatch,
				dtaDa, dtaA, appNotifica, pfPrincipalDest, deProvincia);

		// Order by idAppNotifica.
		query.orderBy(cb.desc(appNotifica.get(AppNotifica_.idAppNotifica)));

		// Costruisco la query.
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		TypedQuery<AppNotifica> tquery = entityManager.createQuery(query);

		if (start != null && rows != null) {
			tquery.setFirstResult(start);
			tquery.setMaxResults(rows);
		}
		return findDTOByQuery(tquery);
	}

	public Long getCountNotificheInviate(String codProvincia) {
		TypedQuery<Long> query = entityManager.createNamedQuery("appNotifica.getCountNotificheInviate", Long.class);
		query.setParameter("codProvincia", codProvincia);

		return query.getSingleResult();
	}

	public Long getCountNotificheInviate(String codProvincia, String email, String codStato,
			boolean escludiNotificheBatch, Date dtaDa, Date dtaA) {
		// Costruisco la base della query.
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);

		// Creo la clausola FROM della query.
		Root<AppNotifica> appNotifica = query.from(AppNotifica.class);
		Join<AppNotifica, PfPrincipal> pfPrincipalDest = appNotifica.join(AppNotifica_.pfPrincipalDest, JoinType.LEFT);
		Join<AppNotifica, DeProvincia> deProvincia = appNotifica.join(AppNotifica_.deProvincia, JoinType.LEFT);

		// Creo la clausola SELECT della query
		query.select(cb.count(appNotifica));

		// Creo la clausola WHERE della query
		List<Predicate> whereConditions = createWhereConditions(codProvincia, email, codStato, escludiNotificheBatch,
				dtaDa, dtaA, appNotifica, pfPrincipalDest, deProvincia);

		// Costruisco la query.
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		TypedQuery<Long> tquery = entityManager.createQuery(query);

		// Eseguo la query e restituisco il risultato
		return tquery.getSingleResult();
	}

	private List<Predicate> createWhereConditions(String codProvincia, String email, String codStato,
			boolean escludiNotificheBatch, Date dtaDa, Date dtaA, Root<AppNotifica> appNotifica,
			Join<AppNotifica, PfPrincipal> pfPrincipalDest, Join<AppNotifica, DeProvincia> deProvincia) {

		List<Predicate> whereConditions = new ArrayList<Predicate>();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		// Notifiche puntuali o batch
		whereConditions.add(cb.or(cb.equal(deProvincia.get(DeProvincia_.codProvincia), codProvincia),
				cb.isNull(deProvincia.get(DeProvincia_.codProvincia))));

		if (email != null) {
			Predicate emailCondizione = cb.equal(cb.upper(pfPrincipalDest.get(PfPrincipal_.email)),
					email.toUpperCase());
			whereConditions.add(emailCondizione);
		}

		if (StringUtils.isNotBlank(codStato)) {
			Predicate statoCondizione = cb.equal(appNotifica.get(AppNotifica_.stato), StatoNotifica.valueOf(codStato));
			whereConditions.add(statoCondizione);
		}

		if (escludiNotificheBatch) {
			Predicate notificheBatchCondizione = cb.notEqual(appNotifica.get(AppNotifica_.pfPrincipalIns), 0);
			whereConditions.add(notificheBatchCondizione);
		}

		// Condizioni sulle date di validita
		if (dtaDa != null) {
			whereConditions.add(cb.greaterThanOrEqualTo(appNotifica.get(AppNotifica_.dtmIns), dtaDa));
		}

		if (dtaA != null) {
			// Incremento di un giorno per la gestione dell'orario
			Calendar cal = Calendar.getInstance();
			cal.setTime(dtaA);
			cal.add(Calendar.DAY_OF_MONTH, 1);

			whereConditions.add(cb.lessThan(appNotifica.get(AppNotifica_.dtmIns), cal.getTime()));
		}

		return whereConditions;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public AppNotifica sendNotificationCVScadenzaBatch(CvDatiPersonali cvDatiPersonali, PfPrincipal pfPrincipalIns) {
		
		String titolo = "Curriculum Vitae in scadenza " + cvDatiPersonali.getDescrizione();
		String sottotitolo = "Curriculum Vitae in scadenza di: "
				+ cvDatiPersonali.getPfPrincipal().getCognome() + " " + cvDatiPersonali.getPfPrincipal().getNome() ; //nome
		String messaggio = "Il curriculum vitae " + cvDatiPersonali.getDescrizione()
				+ " risulta essere in scadenza ";

		// Inserimento e invio della notifica
		AppNotifica appNotifica = new AppNotifica(titolo, sottotitolo, messaggio,
				AppUtils.getDeProvinciaRiferimento(cvDatiPersonali.getPfPrincipal()),
				cvDatiPersonali.getPfPrincipal(), pfPrincipalIns, null /* sidNotifica */,
				TipoNotificaEnum.CV_SCAD/* tipoNotifica */, null /* statoNotifica */);
		
		AdditionalDataNotification additionalData = new AdditionalDataNotification(appNotifica, null); // aggiunge l'additional data in tal modo {"type":"ALTRO"}
		
		try {			
			
			this.persistAndSend(appNotifica, additionalData,ConstantsSingleton.App.ONESIGNAL_NOTIFICATION_DELIVERY_TIME_OF_DAY_BATCH );

			entityManager.flush();

			StringBuffer logMsg = new StringBuffer();
			logMsg.append("Invio notifica all'app. Per l'utente ")
					.append(cvDatiPersonali.getPfPrincipal().getUsername()).append(", il curriculum vitae '")
					.append(cvDatiPersonali.getDescrizione()).append("' e' in scadenza ");

			if (appNotifica.getStato() == StatoNotifica.L) {
				logMsg.append(
						" Non è stato possibile recapitare la notifica poiché l'utente ha eseguito il logout dall'App.");
			}
			log.info(logMsg);

		} catch (Exception e) {
			throw new EJBException("Errore durante il salvataggio della notifica curriculum vitae in scadenza batch per l'utente: "
					+ cvDatiPersonali.getPfPrincipal().getUsername() + " per la ricerca salvata "
					+ cvDatiPersonali.getDescrizione());
		}
		return appNotifica;
	}

}
