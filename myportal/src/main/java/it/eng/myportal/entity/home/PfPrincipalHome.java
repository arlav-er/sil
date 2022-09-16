package it.eng.myportal.entity.home;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.time.DateUtils;

import it.eng.myportal.beans.amministrazione.pojo.UtentePojo;
import it.eng.myportal.dtos.AbstractUpdatablePkDTO;
import it.eng.myportal.dtos.PfPrincipalDTO;
import it.eng.myportal.dtos.ProvinciaDTO;
import it.eng.myportal.dtos.RegisterAziendaDTO;
import it.eng.myportal.dtos.RegisterDTO;
import it.eng.myportal.dtos.RegisterUtenteDTO;
import it.eng.myportal.dtos.UtenteDTO;
import it.eng.myportal.entity.AziendaInfo;
import it.eng.myportal.entity.AziendaInfo_;
import it.eng.myportal.entity.CertificatoreInfo;
import it.eng.myportal.entity.CertificatoreInfo_;
import it.eng.myportal.entity.ConsulenteInfo;
import it.eng.myportal.entity.CoordinatoreInfo;
import it.eng.myportal.entity.PfAbilitazione;
import it.eng.myportal.entity.PfAbilitazione_;
import it.eng.myportal.entity.PfIdentityProvider;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.PfPrincipal_;
import it.eng.myportal.entity.Provincia;
import it.eng.myportal.entity.Provincia_;
import it.eng.myportal.entity.UtenteInfo;
import it.eng.myportal.entity.UtenteInfo_;
import it.eng.myportal.entity.decodifiche.DeAttivitaPf;
import it.eng.myportal.entity.decodifiche.DeAttivitaPf_;
import it.eng.myportal.entity.decodifiche.DeProvincia;
import it.eng.myportal.entity.decodifiche.DeProvincia_;
import it.eng.myportal.entity.decodifiche.DeRuoloPortale;
import it.eng.myportal.entity.decodifiche.DeRuoloPortale_;
import it.eng.myportal.entity.ejb.UtenteAziendaSARE;
import it.eng.myportal.entity.enums.TipoAbilitazione;
import it.eng.myportal.entity.home.decodifiche.DeRuoloPortaleHome;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.exception.MyPortalNotFoundException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;
import it.eng.sil.base.utils.StringUtils;

/**
 * Classe Home per il pfPrincipal
 * 
 * @see it.eng.myportal.entity.PfPrincipal
 * @author Rodi A.
 */
@Stateless
public class PfPrincipalHome extends AbstractUpdatableHome<PfPrincipal, PfPrincipalDTO> {

	private static final String QUERY_BY_USERNAME = "select p from PfPrincipal p where upper(p.username) = upper(:name)";
	private static final String QUERY_BY_PARTIAL_USERNAME = "select p from PfPrincipal p where upper(p.username) like :name";
	private static final String QUERY_BY_EMAIL = " select p " + " from PfPrincipal p where upper(p.email) = :email";

	@EJB
	PfIdentityProviderHome pfIdentityProviderHome;

	@EJB
	private ProvinciaHome provinciaHome;

	@EJB
	private AziendaInfoHome aziendaInfoHome;

	@EJB
	private UtenteInfoHome utenteInfoHome;

	@EJB
	UtenteAziendaSARE utenteAziendaSARE;

	@EJB
	DeRuoloPortaleHome deRuoloPortaleHome;

	private SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");

	/**
	 * Cerca un pfPrincipal a partire dal suo Username.
	 * 
	 * @param name
	 *            username da ricercare
	 * @return il principal con lo username specificato, null altrimenti
	 */
	public PfPrincipal findByUsername(String name) {
		log.debug("getting pfPrincipal with username: " + name);
		List<PfPrincipal> list = entityManager.createQuery(QUERY_BY_USERNAME, PfPrincipal.class)
				.setParameter("name", name).getResultList();
		if (list.size() != 1)
			return null; // Chiunque sia stato, deve 2 franchi più interessi a Franco.
		return list.get(0);
	}

	/**
	 * Restituisce l'utente con un certo username, SOLO se è abilitato.
	 */
	public PfPrincipal findAbilitatoByUsername(String username) {
		List<PfPrincipal> utentiList = entityManager.createNamedQuery("findAbilitatiByUsername", PfPrincipal.class)
				.setParameter("username", username.trim().toUpperCase()).getResultList();

		if (utentiList == null || utentiList.isEmpty()) {
			String errorMessage = "Errore nella findAbilitatoByUsername: utente " + username + " non trovato.";
			log.error(errorMessage);
			throw new RuntimeException(errorMessage);
		}

		if (utentiList.size() > 1) {
			String errorMessage = "Errore nella findAbilitatoByUsername: trovati " + utentiList.size()
					+ " utenti per username " + username + ". Id degli utenti: ";
			for (PfPrincipal utente : utentiList) {
				errorMessage += utente.getIdPfPrincipal() + ", ";
			}
			log.error(errorMessage);
			throw new RuntimeException(errorMessage);
		}

		return utentiList.get(0);
	}

	/**
	 * Restituisce tutti i pfPrincipal il cui username contiene la stringa passata in input
	 * 
	 * @param name
	 *            username parziale
	 * @return lista di pfPrincipal che contengono la stringa in input nel loro username
	 */
	public List<PfPrincipal> findByPartialUsername(String name) {
		log.debug("getting pfPrincipals with partial username: " + name);

		List<PfPrincipal> list = entityManager.createQuery(QUERY_BY_PARTIAL_USERNAME, PfPrincipal.class)
				.setParameter("name", name.toUpperCase() + "%").getResultList();

		return list;
	}

	public List<PfPrincipal> findEmailByCFAziendaInfo(String cf) {
		log.debug("getting findEmailByCFAziendaInfo: " + cf);
		
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<PfPrincipal> query = qb.createQuery(PfPrincipal.class);
		Root<PfPrincipal> pfPrincipal = query.from(PfPrincipal.class);
		Join<PfPrincipal, AziendaInfo> aziendaInfo = pfPrincipal.join(PfPrincipal_.aziendaInfo);
		query.where(qb.equal(qb.upper(aziendaInfo.get(AziendaInfo_.codiceFiscale)), cf.toUpperCase()));
		
		/* eseguo */
		TypedQuery<PfPrincipal> q = entityManager.createQuery(query).setHint("org.hibernate.cacheable", true);
		/* e prendo i risultati */
		List<PfPrincipal> result = q.getResultList();
		if (result.isEmpty())
			return null;
		return result;
	}
	
	/**
	 * Restituisce tutti i pfPrincipal il cui username contiene la stringa passata in input
	 * 
	 * @param name
	 *            username parziale
	 * @return lista di pfPrincipal che contengono la stringa in input nel loro username
	 */
	public List<PfPrincipalDTO> findDTOByPartialUsername(String name) {
		log.debug("getting pfPrincipals with partial username: " + name);

		List<PfPrincipalDTO> listDTO = new ArrayList<PfPrincipalDTO>();
		List<PfPrincipal> list = findByPartialUsername(name);

		for (PfPrincipal pfPrincipal : list) {
			listDTO.add(toDTO(pfPrincipal));
		}

		return listDTO;
	}

	public PfPrincipalDTO findDTOByUsername(String name) {
		log.debug("getting pfPrincipal with username: " + name);
		List<PfPrincipal> list = entityManager.createQuery(QUERY_BY_USERNAME, PfPrincipal.class)
				.setParameter("name", name).getResultList();
		if (list.size() != 1)
			return null;
		return toDTO(list.get(0));
	}

	public PfPrincipal findById(Integer id) {
		return findById(PfPrincipal.class, id);
	}

	@Override
	public PfPrincipalDTO toDTO(PfPrincipal entity) {
		if (entity == null)
			return null;
		PfPrincipalDTO ret = super.toDTO(entity);
		ret.setId(entity.getIdPfPrincipal());
		ret.setUsername(entity.getUsername());
		ret.setPassWord(entity.getPassWord());

		ret.setDtScadenza(entity.getDtScadenza());
		ret.setFlagAbilitato(entity.getFlagAbilitato());
		ret.setFlagAbilitatoServizi(entity.getFlagAbilitatoServizi());
		ret.setFlagAbilitatoSare(entity.getFlagAbilitatoSare());
		ret.setFlagAbilitaPec(entity.getFlagAbilitaPec());
		ret.setNome(entity.getNome());
		ret.setCognome(entity.getCognome());
		ret.setEmail(entity.getEmail());
		ret.setDomanda(entity.getDomanda());
		ret.setRisposta(entity.getRisposta());
		ret.setStileSelezionato(entity.getStileSelezionato());
		ret.setConfirmationToken(entity.getConfirmationToken());
		ret.setDtmConfirm(entity.getDtmConfirm());
		ret.setRecuperoPasswordToken(entity.getPasswordToken());
		ret.setRegistrazioneForteToken(entity.getRegistrazioneForteToken());
		ret.setRichiestaRegForteToken(entity.getRichiestaRegForteToken());
		ret.setTelefonoUtente(entity.getTelefonoUtente());
		ret.setCodRuoloPortale(entity.getDeRuoloPortale().getCodRuoloPortale());
		return ret;
	}

	@Override
	public PfPrincipal fromDTO(PfPrincipalDTO dto) {
		if (dto == null)
			return null;
		PfPrincipal entity = super.fromDTO(dto);
		entity.setIdPfPrincipal(dto.getId());
		entity.setUsername(dto.getUsername());
		entity.setPassWord(dto.getPassWord());
		entity.setDtScadenza(dto.getDtScadenza());
		entity.setFlagAbilitato(dto.getFlagAbilitato());
		entity.setFlagAbilitatoServizi(dto.getFlagAbilitatoServizi());
		entity.setFlagAbilitatoSare(dto.getFlagAbilitatoSare());
		entity.setFlagAbilitaPec(dto.getFlagAbilitaPec());
		entity.setCognome(dto.getCognome());
		entity.setConfirmationToken(dto.getConfirmationToken());
		entity.setDomanda(dto.getDomanda());
		entity.setDtmConfirm(dto.getDtmConfirm());
		entity.setEmail(dto.getEmail());
		entity.setNome(dto.getNome());
		entity.setPasswordToken(dto.getRecuperoPasswordToken());
		entity.setRegistrazioneForteToken(dto.getRegistrazioneForteToken());
		entity.setRichiestaRegForteToken(dto.getRichiestaRegForteToken());
		entity.setRisposta(dto.getRisposta());
		entity.setStileSelezionato(dto.getStileSelezionato());
		entity.setTelefonoUtente(dto.getTelefonoUtente());
		entity.setDeRuoloPortale(deRuoloPortaleHome.findById(dto.getCodRuoloPortale()));
		return entity;
	}

	/**
	 * 
	 * 
	 * Registra un nuovo principal e lo persiste su DB, partendo dai dati contenuti nella tabella di appoggio
	 * sare_import
	 * 
	 * @deprecated DA NON USARE PER NESSUN MOTIVO!
	 * @param register
	 * @param administrator
	 * @param now
	 * @return
	 */
	@Deprecated
	public PfPrincipal registerFromSareImport(RegisterDTO register, PfPrincipal administrator, Date now) {
		PfPrincipal principal = new PfPrincipal();
		DeRuoloPortale deRuoloPortale;
		String codice = null;

		// se sto registrando da sare import assegnagli il tipo AZIENDA
		deRuoloPortale = deRuoloPortaleHome.findById(ConstantsSingleton.DeRuoloPortale.AZIENDA);

		if (((RegisterAziendaDTO) register).getRichiestaAccessoSARE()) {
			principal.setFlagAbilitatoSare(true);
		} else {
			principal.setFlagAbilitatoSare(false);
		}
		principal.setFlagAbilitato(true); // non è ancora attivo
		principal.setDtScadenza(((RegisterAziendaDTO) register).getDtScadenzaPassword());

		principal.setUsername(register.getUsername());
		principal.setPassWord(register.getPassword());

		principal.setDtmIns(now);
		principal.setDtmMod(now);

		principal.setPfPrincipalIns(administrator);
		principal.setPfPrincipalMod(administrator);
		principal.setFlagAbilitatoServizi(false);
		principal.setDeRuoloPortale(deRuoloPortale);

		pfPrincipalHome.persist(principal);

		principal.setCognome(register.getCognome());
		principal.setNome(register.getNome());
		principal.setEmail(register.getEmail());
		principal.setDomanda(register.getDomanda());
		principal.setRisposta(register.getRisposta());
		principal.setDtFineValidita(DateUtils.addDays(now, 30));
		principal.setStileSelezionato("myportal");
		principal.setConfirmationToken(register.getActivateToken());
		principal.setIndirizzoUtente(register.getIndirizzo());
		principal.setTelefonoUtente(register.getTelefono());
		principal.setPrivacy("Y");
		principal.setNumTentativi(0);
		if (register instanceof RegisterUtenteDTO)
			principal.setRichiestaRegForteToken(codice);

		pfPrincipalHome.persist(principal);
		return principal;
	}

	public PfPrincipal registerCittadino(RegisterDTO register, PfPrincipal administrator, Date now,
			boolean pwdScaduta) {
		DeRuoloPortale deRuoloPortale = deRuoloPortaleHome.findById(ConstantsSingleton.DeRuoloPortale.CITTADINO);
		String codice = ((RegisterUtenteDTO) register).getCodiceRichiestaAutForte();
		Boolean abilitatoServizi = ((RegisterUtenteDTO) register).getAutenticazioneForte();

		return register(register, deRuoloPortale, administrator, now, false, abilitatoServizi, codice, true,
				pwdScaduta);
	}

	public PfPrincipal registerAzienda(RegisterDTO register, PfPrincipal administrator, Date now, boolean pwdScaduta) {
		DeRuoloPortale deRuoloPortale = deRuoloPortaleHome.findById(ConstantsSingleton.DeRuoloPortale.AZIENDA);
		Boolean abilitatoSare;
		if (((RegisterAziendaDTO) register).getRichiestaAccessoSARE()) {
			abilitatoSare = true;
		} else {
			abilitatoSare = false;
		}

		return register(register, deRuoloPortale, administrator, now, abilitatoSare, false, null, true, pwdScaduta);
	}

	public PfPrincipal registerProvincia(RegisterDTO register, PfPrincipal administrator, Date now,
			Boolean abilitatoSare, boolean encrypt) {
		DeRuoloPortale deRuoloPortale = deRuoloPortaleHome.findById(ConstantsSingleton.DeRuoloPortale.PROVINCIA);
		return register(register, deRuoloPortale, administrator, now, abilitatoSare, false, null, encrypt, false);
	}

	public PfPrincipal registerCoordinatore(RegisterDTO register, PfPrincipal administrator, Date now,
			Boolean abilitatoSare, boolean encrypt) {
		DeRuoloPortale deRuoloPortale = deRuoloPortaleHome.findById(ConstantsSingleton.DeRuoloPortale.COORDINA);
		return register(register, deRuoloPortale, administrator, now, abilitatoSare, false, null, encrypt, false);
	}

	public PfPrincipal registerConsulente(RegisterDTO register, PfPrincipal administrator, Date now,
			Boolean abilitatoSare, boolean encrypt) {
		DeRuoloPortale deRuoloPortale = deRuoloPortaleHome.findById(ConstantsSingleton.DeRuoloPortale.CONSULEN);
		return register(register, deRuoloPortale, administrator, now, abilitatoSare, false, null, encrypt, false);
	}

	public PfPrincipal registerCertificatore(RegisterDTO register, PfPrincipal administrator, Date now,
			Boolean abilitatoSare, boolean encrypt) {
		DeRuoloPortale deRuoloPortale = deRuoloPortaleHome.findById(ConstantsSingleton.DeRuoloPortale.CERTIFIC);
		return register(register, deRuoloPortale, administrator, now, abilitatoSare, false, null, encrypt, false);
	}

	public PfPrincipal registerRegione(RegisterDTO register, PfPrincipal administrator, Date now, Boolean abilitatoSare,
			boolean encrypt) {
		DeRuoloPortale deRuoloPortale = deRuoloPortaleHome.findById(ConstantsSingleton.DeRuoloPortale.REGIONE);
		return register(register, deRuoloPortale, administrator, now, abilitatoSare, false, null, encrypt, false);
	}

	public PfPrincipal registerSoggettoPubblico(RegisterDTO register, PfPrincipal administrator, Date now,
			Boolean abilitatoSare, boolean encrypt) {
		DeRuoloPortale deRuoloPortale = deRuoloPortaleHome.findById(ConstantsSingleton.DeRuoloPortale.SOGGETTOPUBBLICO);
		return register(register, deRuoloPortale, administrator, now, abilitatoSare, false, null, encrypt, false);
	}

	/**
	 * 
	 * Registra un nuovo principal e lo persiste su DB
	 */
	private PfPrincipal register(RegisterDTO register, DeRuoloPortale deRuoloPortale, PfPrincipal administrator,
			Date now, boolean abilitatoSare, boolean abilitatoServizi, String codice, boolean encrypt,
			boolean pwdScaduta) {
		PfPrincipal principal = new PfPrincipal();
		principal.setFlagAbilitato(false);
		principal.setFlagAbilitatoSare(abilitatoSare);
		principal.setFlagAbilitatoServizi(abilitatoServizi);
		principal.setRichiestaRegForteToken(codice);
		principal.setUsername(register.getUsername());
		if (encrypt) {
			principal.setPassWord(Utils.SHA1.encrypt(register.getPassword()));
		} else {
			principal.setPassWord(register.getPassword());
		}
		principal.setDtmIns(now);
		principal.setDtmMod(now);
		if (pwdScaduta) {
			principal.setDtScadenza(DateUtils.addDays(now, -1));
		} else {
			principal.setDtScadenza(DateUtils.addDays(now, 90));
		}
		principal.setPfPrincipalIns(administrator);
		principal.setPfPrincipalMod(administrator);

		principal.setCognome(register.getCognome());
		principal.setNome(register.getNome());
		principal.setEmail(register.getEmail());
		principal.setDomanda(register.getDomanda());
		principal.setRisposta(register.getRisposta());
		principal.setDtFineValidita(DateUtils.addDays(now, 30));
		principal.setStileSelezionato("myportal");
		principal.setConfirmationToken(register.getActivateToken());
		principal.setIndirizzoUtente(register.getIndirizzo());
		principal.setTelefonoUtente(register.getTelefono());
		principal.setPrivacy("Y");
		principal.setNumTentativi(0);
		principal.setDeRuoloPortale(deRuoloPortale);

		pfPrincipalHome.persist(principal);
		return principal;
	}

	/**
	 * Restituisce la pfPrincipal dell'amministratore
	 * 
	 * @return l'utente amministratore
	 */
	public PfPrincipal getAdministrator() {
		return findById(0);
	}

	/**
	 * Restituisce la pfPrincipal dell'utente clicLavoro
	 * 
	 * @return l'utente di clicLavoro
	 */
	public PfPrincipal getClicLavoro() {
		return findByUsername(ConstantsSingleton.USERNAME_CLICLAVORO_CANDIDATURE);
	}

	/**
	 * Determina se un utente è abilitato o meno all'esecuzione di una certa attività
	 * 
	 * @param idPfPrincipal
	 *            id dell'utente per il quale si vuole verificare il permesso
	 * @param codAttivita
	 *            codice dell'attività per la quale si vuole verificare il permesso
	 * @param tipoAbilitazione
	 *            tipo di abilitazione richiesta per l'attività
	 * 
	 * @return true se è abilitato all'operazione richiesta
	 * 
	 */
	public boolean checkAbilitazione(Integer idPfPrincipal, String codAttivita, TipoAbilitazione tipoAbilitazione) {
		List<Predicate> whereConditions = new ArrayList<Predicate>();
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Boolean> query = qb.createQuery(Boolean.class);
		Root<PfPrincipal> pfPrincipalRoot = query.from(PfPrincipal.class);
		Join<PfPrincipal, DeRuoloPortale> deRuoloPortaleJoin = pfPrincipalRoot.join(PfPrincipal_.deRuoloPortale);
		Join<DeRuoloPortale, PfAbilitazione> pfAbilitazioneJoin = deRuoloPortaleJoin
				.join(DeRuoloPortale_.abilitazioneList);
		Join<PfAbilitazione, DeAttivitaPf> deAttivitaPfJoin = pfAbilitazioneJoin.join(PfAbilitazione_.deAttivitaPf);

		whereConditions.add(qb.equal(pfPrincipalRoot.get(PfPrincipal_.idPfPrincipal), idPfPrincipal));
		whereConditions.add(qb.equal(deAttivitaPfJoin.get(DeAttivitaPf_.codAttivitaPf), codAttivita));
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		query.select(pfAbilitazioneJoin.get(tipoAbilitazione.getAttribute()));
		TypedQuery<Boolean> q = entityManager.createQuery(query);
		try {
			Boolean result = q.getSingleResult();
			return result;
		} catch (Exception e) {
			// ok! mi sta bene! significa che non è abilitato!
			return false;
		}
	}

	/**
	 * Restituisce true se l'email passata come parametro non compare gia' nel DB o se compare associata
	 * all'idPfPrincipal passato
	 * 
	 * @param idPfPrincipal
	 * @param email
	 * @return
	 */
	public boolean checkUniqueEmail(Integer idPfPrincipal, String email) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
		Root<PfPrincipal> pfPrincipal = query.from(PfPrincipal.class);

		List<Predicate> whereConditions = new ArrayList<>();
		whereConditions.add(cb.equal(cb.upper(pfPrincipal.get(PfPrincipal_.email)), email.toUpperCase()));
		whereConditions.add(cb.notEqual(pfPrincipal.get(PfPrincipal_.idPfPrincipal), idPfPrincipal));

		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		query.select(cb.count(pfPrincipal));

		TypedQuery<Long> q = entityManager.createQuery(query);

		Long result = q.getSingleResult();

		return (result == 0 ? true : false);
	}

	/**
	 * Restituisce true se l'email PEC passata come parametro non compare gia' nel DB o se compare associata
	 * all'idPfPrincipal passato
	 * 
	 * @param idPfPrincipal
	 * @param email
	 * @return
	 */
	public boolean checkUniquePECEmail(Integer idPfPrincipal, String email) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
		Root<PfPrincipal> pfPrincipal = query.from(PfPrincipal.class);
		Join<PfPrincipal, UtenteInfo> utenteInfo = pfPrincipal.join(PfPrincipal_.utenteInfo);
		query.where(cb.equal(cb.upper(utenteInfo.get(UtenteInfo_.emailPEC)), email.toUpperCase()));
		query.select(pfPrincipal.get(PfPrincipal_.idPfPrincipal));
		TypedQuery<Integer> q = entityManager.createQuery(query);

		List<Integer> result = q.getResultList();

		/* se non trovo risultati l'email e' unica */
		if (result.size() == 0) {
			return true;
		}
		/* se nei risultati che trovo c'e' anche l'utente correnteva bene */
		for (Integer id : result) {
			if (id.equals(idPfPrincipal)) {
				return true;
			}
		}
		/* in tutti gli altri casi l'email non e' unica */
		return false;
	}

	/**
	 * Effettua una query per recuperare tutti gli utenti di una certa provincia che abbiano un certo ruolo.
	 * 
	 * @param ruolo
	 *            Il ruolo degli utenti da recuperare.
	 * @param strTarga
	 *            La provicncia degli utenti da recuperare.
	 */
	public List<PfPrincipal> getListaUtentiProvinciaByTarga(String ruolo, String strTarga) {
		String query = "select pp.* " + " from mycas.pf_principal pp  "
				+ " inner join mycas.provincia p on pp.id_pf_principal = p.id_pf_principal "
				+ " inner join mycas.pf_ruolo_principal prp on prp.id_pf_principal = pp.id_pf_principal "
				+ " inner join mycas.Pf_Ruolo pr on pr.id_pf_ruolo = prp.id_pf_ruolo "
				+ " inner join mycas.de_provincia de on de.cod_provincia = p.cod_provincia " + " where pr.ruolo = ? "
				+ " and de.targa = ?";
		Query nativeQuery = entityManager.createNativeQuery(query, PfPrincipal.class);
		nativeQuery.setParameter(1, ruolo);
		nativeQuery.setParameter(2, strTarga);
		List<PfPrincipal> list = nativeQuery.getResultList();
		return list;
	}

	/**
	 * Metodo per cambiare la password di un utente
	 * 
	 * @param username
	 *            username dell'utente a cui si vuole midificare la password
	 * @param passwordNew
	 *            nuova password dell'utente
	 */
	public void cambiaPassword(String username, String passwordNew, String passwordOld) {
		Date now = new Date();
		PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);

		String pwdOldCrypt = Utils.SHA1.encrypt(passwordOld);
		// CONTROLLO se la password vecchia inserita è uguale a quella del DB
		if (pfPrincipal.getPassWord().equals(pwdOldCrypt)) {

			pfPrincipal.setDtScadenza(DateUtils.addDays(now, 90));
			pfPrincipal.setPassWord(Utils.SHA1.encrypt(passwordNew));
			pfPrincipal.setPasswordToken(null);

			pfPrincipalHome.merge(pfPrincipal);

			// modifico la password sul SARE se è un'azienda abilitata
			if (pfPrincipal.isAzienda()) {
				AziendaInfo aziendaInfo = pfPrincipal.getAziendaInfo();

				// verifica accesso al SARE
				Boolean flgAbilitatoSare = pfPrincipal.getFlagAbilitatoSare();
				if (flgAbilitatoSare) {
					if (checkAbilitazione(pfPrincipal.getIdPfPrincipal(), "accediSare", TipoAbilitazione.VISIBILE)) {
						String esitoWs = utenteAziendaSARE.modificaPasswordUtenteSare(toDTO(pfPrincipal),
								aziendaInfoHome.toDTO(aziendaInfo));
						log.info("SARE: modifica password azienda " + pfPrincipal.getUsername() + " con esito="
								+ esitoWs);
					} else {
						throw new EJBException("appSare.pwd.nodisponibile");
					}
				}
			} else if (pfPrincipal.isProvincia()) {
				Boolean flgAbilitatoSare = pfPrincipal.getFlagAbilitatoSare();
				if (flgAbilitatoSare) {
					if (checkAbilitazione(pfPrincipal.getIdPfPrincipal(), "accediSare", TipoAbilitazione.VISIBILE)) {
						ProvinciaDTO provDTO = provinciaHome.findDTOByUserName(pfPrincipal.getUsername());
						String esitoWs = utenteAziendaSARE.modificaPasswordCpiSare(toDTO(pfPrincipal), provDTO);
						log.info("SARE: modifica password azienda " + pfPrincipal.getUsername() + " con esito="
								+ esitoWs);
					} else {
						throw new MyPortalException("appSare.pwd.nodisponibile");
					}
				}
			}
		} else {
			throw new MyPortalException("Password vecchia errata", true);
		}
	}

	public void cambiaPassword(String username, String passwordNew) {
		Date now = new Date();
		PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);

		pfPrincipal.setDtScadenza(DateUtils.addDays(now, 90));
		pfPrincipal.setPassWord(Utils.SHA1.encrypt(passwordNew));
		pfPrincipal.setPasswordToken(null);

		pfPrincipalHome.merge(pfPrincipal);

		// modifico la password sul SARE se è un'azienda abilitata
		if (pfPrincipal.isAzienda()) {
			AziendaInfo aziendaInfo = pfPrincipal.getAziendaInfo();

			// verifica accesso al SARE
			Boolean flgAbilitatoSare = pfPrincipal.getFlagAbilitatoSare();
			if (flgAbilitatoSare) {
				String esitoWs = utenteAziendaSARE.modificaPasswordUtenteSare(toDTO(pfPrincipal),
						aziendaInfoHome.toDTO(aziendaInfo));
				log.info("SARE: modifica password azienda " + pfPrincipal.getUsername() + " con esito=" + esitoWs);
			}
		}

		log.info("L'utente" + username + " ha aggiornato la password che scadrà il "
				+ formatter1.format(pfPrincipal.getDtScadenza()) + ".");

	}

	/**
	 * Determina se uno username è già stato scelto o meno.
	 * 
	 * @param username
	 * @return true se lo username è già presente su DB
	 */
	public boolean exists(String username) {
		return findByUsername(username) != null;
	}

	/**
	 * Determina se il pfPrincipal appartiene ad un utente 'cittadino'
	 * 
	 * @param idPfPrincipal
	 * @return
	 */
	public boolean isUtente(Integer idPfPrincipal) {
		PfPrincipal pfPrincipal = findById(idPfPrincipal);
		return pfPrincipal.isUtente();
	}

	public boolean isCoordinatore(Integer idPfPrincipal) {
		PfPrincipal pfPrincipal = findById(idPfPrincipal);
		return pfPrincipal.isCoordinatore();
	}

	public boolean isCertificatore(Integer idPfPrincipal) {
		PfPrincipal pfPrincipal = findById(idPfPrincipal);
		return pfPrincipal.isCertificatore();
	}

	/**
	 * Determina se il pfPrincipal appartiene ad un utente 'azienda'
	 * 
	 * @param idPfPrincipal
	 * @return
	 * 
	 */
	public boolean isAzienda(Integer idPfPrincipal) {
		PfPrincipal pfPrincipal = findById(idPfPrincipal);
		return pfPrincipal.isAzienda();
	}

	/**
	 * Determina se il pfPrincipal appartiene ad un utente 'provincia'
	 * 
	 * @param idPfPrincipal
	 * @return
	 */
	public boolean isProvincia(Integer idPfPrincipal) {
		PfPrincipal pfPrincipal = findById(idPfPrincipal);
		return pfPrincipal.isProvincia();
	}

	/**
	 * Determina se il pfPrincipal appartiene ad un utente 'regione'
	 * 
	 * @param idPfPrincipal
	 * @return
	 */
	public boolean isRegione(Integer idPfPrincipal) {
		PfPrincipal pfPrincipal = findById(idPfPrincipal);
		return !pfPrincipal.getRegionesForIdPfPrincipal().isEmpty();
	}

	/**
	 * tipo PATRONATO
	 * 
	 * @param idPfPrincipal
	 * @return
	 */
	public boolean isPatronato(Integer idPfPrincipal) {
		PfPrincipal pfPrincipal = findById(idPfPrincipal);
		return !pfPrincipal.getPatronatosForIdPfPrincipal().isEmpty();
	}

	public boolean isSoggettoPubblico(Integer idPfPrincipal) {
		PfPrincipal pfPrincipal = findById(idPfPrincipal);
		return !pfPrincipal.getSoggettoPubbsForIdPfPrincipal().isEmpty();
	}

	/**
	 * Determina se il pfPrincipal appartiene ad un utente 'consulente'
	 * 
	 * @param idPfPrincipal
	 * @return
	 */
	public boolean isConsulente(Integer idPfPrincipal) {
		PfPrincipal pfPrincipal = findById(idPfPrincipal);
		return pfPrincipal.isConsulente();
	}

	public Date getDataScadenza(String username) {
		PfPrincipal pfPrincipal = findAbilitatoByUsername(username);
		return pfPrincipal.getDtScadenza();
	}

	/**
	 * Aggiorna lo stile selezionato dall'utente nelle preferenze
	 * 
	 * @param stileSelezionato
	 *            il nome dello stile selezionato
	 * @param idPrincipal
	 *            principal id dell'utente a cui aggiornare il tema
	 */
	public void updateCSS(String stileSelezionato, Integer idPrincipal) {
		PfPrincipal info = pfPrincipalHome.findById(idPrincipal);
		info.setStileSelezionato(stileSelezionato);

		pfPrincipalHome.merge(info);
	}

	/**
	 * Fa scadere la password dell'utente passato in input settando la data di scadenza a ieri.
	 * 
	 * @param idPfPrincipal
	 *            id dell'utente a cui si vuole fa scadere la password
	 */
	public void invalidaPassword(Integer idPfPrincipal) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		Date ieri = cal.getTime();

		PfPrincipal pfPrincipal = findById(idPfPrincipal);
		pfPrincipal.setDtScadenza(ieri);
		merge(pfPrincipal);
	}

	/**
	 * Resetta la password dell'utente passato come input con la password passata e setta la data di scadenza a ieri.
	 * 
	 * @param idPfPrincipal
	 *            id dell'utente a cui si vuole resettare la password
	 * @param newPassword
	 *            nuova password da settare all'utente
	 */
	public void resetPassword(Integer idPfPrincipal, String newPassword) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		Date ieri = cal.getTime();

		PfPrincipal pfPrincipal = findById(idPfPrincipal);
		pfPrincipal.setPassWord(newPassword);
		pfPrincipal.setDtScadenza(ieri);
		merge(pfPrincipal);
	}

	public boolean isUtente(String username) {
		return findByUsername(username).isUtente();
	}

	public boolean isProvincia(String username) {
		return findByUsername(username).isProvincia();
	}

	public boolean isAzienda(String username) {
		return findByUsername(username).isAzienda();
	}

	/**
	 * Metodo eseguito dalla pagina di conferma della registrazione da parte dell'utente
	 * 
	 * @param user
	 *            dati del lavoratore
	 */
	public PfPrincipalDTO confermaAbilitazioneForte(UtenteDTO user, String token) {

		PfPrincipal principal = findById(user.getId());

		// Se sono io e ho il token giusto
		if (principal.getRegistrazioneForteToken() != null && principal.getRegistrazioneForteToken().equals(token)) {
			principal.setRegistrazioneForteToken(null);
			principal.setFlagAbilitatoServizi(true);
			pfPrincipalHome.merge(principal);
			return toDTO(principal);
		} else {
			return null;
		}
	}

	public List<PfPrincipal> findByEmail(String email) {
		List<PfPrincipal> results = entityManager.createQuery(QUERY_BY_EMAIL, PfPrincipal.class)
				.setHint("org.hibernate.cacheable", true).setParameter("email", email).getResultList();

		return results;

	}

	public List<PfPrincipalDTO> findDTOByEmail(String userOrEmail) {
		List<PfPrincipalDTO> results = new ArrayList<PfPrincipalDTO>();

		List<PfPrincipal> entities = findByEmail(userOrEmail.toUpperCase());

		for (PfPrincipal pfPrincipal : entities) {
			results.add(toDTO(pfPrincipal));
		}

		return results;
	}

	/**
	 * Metodo chiamato dall'operatore CPI quando clicca sul pulsante "Abilita" nella pagina di ricerca utente
	 * 
	 * @param idLavoratore
	 *            id del lavoratore da abilitare
	 * @param token
	 *            token presente nella mail inviata al lavoratore da confrontare nell'ultima fase della registrazione
	 *            forte
	 */
	public void abilita(Integer idLavoratore, String token) {

		PfPrincipal principal = findById(idLavoratore);
		principal.setRichiestaRegForteToken(null);
		principal.setRegistrazioneForteToken(token);
		merge(principal);

	}

	/**
	 * Restituisce la lista di tutti i coordinatori presenti nel portale
	 * 
	 * @return
	 */
	public List<PfPrincipal> findCoordinatori() {
		List<PfPrincipal> lista = new ArrayList<PfPrincipal>();

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<PfPrincipal> query = cb.createQuery(PfPrincipal.class);

		Root<PfPrincipal> root = query.from(PfPrincipal.class);
		Join<PfPrincipal, CoordinatoreInfo> ruoloPrincipal = root.join(PfPrincipal_.coordinatoreInfo);

		lista = entityManager.createQuery(query).getResultList();
		return lista;
	}

	/**
	 * Restituisce la lista di tutti i consulenti rpesenti nel portale
	 * 
	 * @return
	 */
	public List<PfPrincipal> findConsulenti() {
		List<PfPrincipal> lista = new ArrayList<PfPrincipal>();

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<PfPrincipal> query = cb.createQuery(PfPrincipal.class);

		Root<PfPrincipal> root = query.from(PfPrincipal.class);
		Join<PfPrincipal, ConsulenteInfo> ruoloPrincipal = root.join(PfPrincipal_.consulenteInfo);

		lista = entityManager.createQuery(query).getResultList();
		return lista;
	}

	public AbstractUpdatablePkDTO getUserInfo(Integer idPfPrincipal) {

		if (isAzienda(idPfPrincipal)) {
			return aziendaInfoHome.findDTOById(idPfPrincipal);
		} else {
			return utenteInfoHome.findDTOById(idPfPrincipal);
		}

	}

	/*
	 * Richiamato da un web service esposto per il SIL
	 */

	public List<PfPrincipal> findCittadinoByFilter(String nome, String cognome, String email, String cf) {
		Set<PfPrincipal> setCittadini = new HashSet<PfPrincipal>();

		//TICKET https://production.eng.it/jira/browse/ESL4SIL-109 ignoro nome e cognome
		/*if (nome != null && cognome != null && !nome.isEmpty() && !cognome.isEmpty()) {
			setCittadini.addAll(findCittadinoByNomeCognomeFilter(nome, cognome));
		}*/

		if (email != null && !email.isEmpty()) {
			setCittadini.addAll(findCittadinoByEmailFilter(email));
		}

		if (cf != null && !cf.isEmpty()) {
			setCittadini.addAll(findCittadinoByCfFilter(cf));
		}

		List<PfPrincipal> listaCittadini = new ArrayList<PfPrincipal>(setCittadini);
		return listaCittadini;
	}

	private List<PfPrincipal> findCittadinoByNomeCognomeFilter(String nome, String cognome) {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<PfPrincipal> query = qb.createQuery(PfPrincipal.class);
		Root<PfPrincipal> principal = query.from(PfPrincipal.class);

		Predicate nomePred = qb.equal(qb.lower(principal.get(PfPrincipal_.nome)), nome.toLowerCase());
		Predicate cognomePred = qb.equal(qb.lower(principal.get(PfPrincipal_.cognome)), cognome.toLowerCase());

		Predicate emailNoCl = qb.notLike(qb.upper(principal.get(PfPrincipal_.email)), "CL\\_%");
		Predicate emailNoUnderScore = qb.notLike(qb.upper(principal.get(PfPrincipal_.email)), "\\_%");
		Predicate emailNoCANC = qb.notLike(qb.upper(principal.get(PfPrincipal_.email)), "CANC\\_%");

		Predicate finale = qb.and(nomePred, cognomePred);

		Predicate finaleAnd = qb.and(emailNoCl, finale);
		Predicate finaleAnd1 = qb.and(emailNoUnderScore, finaleAnd);
		Predicate finaleAnd2 = qb.and(emailNoCANC, finaleAnd1);

		query.where(finaleAnd2);

		TypedQuery<PfPrincipal> q = entityManager.createQuery(query);
		List<PfPrincipal> instance = q.getResultList();
		return instance;
	}

	private List<PfPrincipal> findCittadinoByEmailFilter(String email) {

		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<PfPrincipal> query = qb.createQuery(PfPrincipal.class);
		Root<PfPrincipal> principal = query.from(PfPrincipal.class);

		Predicate emailNoCl = qb.notLike(qb.upper(principal.get(PfPrincipal_.email)), "CL\\_%");
		Predicate emailNoUnderScore = qb.notLike(qb.upper(principal.get(PfPrincipal_.email)), "\\_%");
		Predicate emailNoCANC = qb.notLike(qb.upper(principal.get(PfPrincipal_.email)), "CANC\\_%");

		Predicate finale = null;
		email = email.toUpperCase();
		if (!email.startsWith("CL_") || !email.startsWith("_") || !email.startsWith("CANC_")) {
			finale = qb.equal(qb.upper(principal.get(PfPrincipal_.email)), email.toUpperCase());
		}

		Predicate finaleAnd = qb.and(emailNoCl, finale);
		Predicate finaleAnd1 = qb.and(emailNoUnderScore, finaleAnd);
		Predicate finaleAnd2 = qb.and(emailNoCANC, finaleAnd1);

		query.where(finaleAnd2);

		TypedQuery<PfPrincipal> q = entityManager.createQuery(query);
		List<PfPrincipal> instance = q.getResultList();
		return instance;
	}

	private List<PfPrincipal> findCittadinoByCfFilter(String cf) {

		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<PfPrincipal> query = qb.createQuery(PfPrincipal.class);
		Root<PfPrincipal> principal = query.from(PfPrincipal.class);
		Join<PfPrincipal, UtenteInfo> utenteInfo = principal.join(PfPrincipal_.utenteInfo);

		Predicate emailNoCl = qb.notLike(qb.upper(principal.get(PfPrincipal_.email)), "CL\\_%");
		Predicate emailNoUnderScore = qb.notLike(qb.upper(principal.get(PfPrincipal_.email)), "\\_%");
		Predicate emailNoCANC = qb.notLike(qb.upper(principal.get(PfPrincipal_.email)), "CANC\\_%");

		Predicate finale = null;

		if (cf != null && !cf.isEmpty()) {
			finale = qb.equal(qb.upper(utenteInfo.get(UtenteInfo_.codiceFiscale)), cf.toUpperCase());
		}

		Predicate finaleAnd = qb.and(emailNoCl, finale);
		Predicate finaleAnd1 = qb.and(emailNoUnderScore, finaleAnd);
		Predicate finaleAnd2 = qb.and(emailNoCANC, finaleAnd1);

		query.where(finaleAnd2);

		TypedQuery<PfPrincipal> q = entityManager.createQuery(query);
		List<PfPrincipal> instance = q.getResultList();
		return instance;
	}

	/**
	 * Metodo utilizzato dalle funzionalità amministrative (specificatamente RESET PASSWORD).
	 */
	public List<UtentePojo> findUtenteByFilter(String username, String nome, String cognome, String ragioneSociale,
			String cf, String email, String tipoRicerca, int startResultsFrom, int maxResults) {

		// Costruisco la query.
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<PfPrincipal> query = qb.createQuery(PfPrincipal.class);
		Root<PfPrincipal> principal = query.from(PfPrincipal.class);

		// Costruisco variabili per potenziali join (serviranno dopo).
		Join<PfPrincipal, UtenteInfo> utenteInfo = null;
		Join<PfPrincipal, AziendaInfo> aziendaInfo = null;
		Join<PfPrincipal, CertificatoreInfo> certificatoreInfo = null;

		// Faccio i join a seconda del tipo di ricerca.
		if (tipoRicerca.equals("CITTADINO")) {
			utenteInfo = principal.join(PfPrincipal_.utenteInfo, JoinType.INNER);
		} else if (tipoRicerca.equals("AZIENDA")) {
			aziendaInfo = principal.join(PfPrincipal_.aziendaInfo, JoinType.INNER);
		} else if (tipoRicerca.equals("PROVINCIA")) {
			principal.join(PfPrincipal_.provinciasForIdPfPrincipal, JoinType.INNER);
		} else if (tipoRicerca.equals("REGIONE")) {
			principal.join(PfPrincipal_.regionesForIdPfPrincipal, JoinType.INNER);
		} else if (tipoRicerca.equals("CONSULENTE")) {
			principal.join(PfPrincipal_.consulenteInfo, JoinType.INNER);
		} else if (tipoRicerca.equals("COORDINATORE")) {
			principal.join(PfPrincipal_.coordinatoreInfo, JoinType.INNER);
		} else if (tipoRicerca.equals("CERTIFICATORE")) {
			certificatoreInfo = principal.join(PfPrincipal_.certificatoreInfo, JoinType.INNER);
		} else if (tipoRicerca.equals("PATRONATO")) {
			principal.join(PfPrincipal_.patronatosForIdPfPrincipal, JoinType.INNER);
		} else if (tipoRicerca.equals("SOGGETTO_PUBBLICO")) {
			principal.join(PfPrincipal_.soggettoPubbsForIdPfPrincipal, JoinType.INNER);
		} else {
			// Se non è uno dei tipi precedenti, assumo che sia di tipo "TUTTI"
			utenteInfo = principal.join(PfPrincipal_.utenteInfo, JoinType.LEFT);
			aziendaInfo = principal.join(PfPrincipal_.aziendaInfo, JoinType.LEFT);
			certificatoreInfo = principal.join(PfPrincipal_.certificatoreInfo, JoinType.LEFT);
		}

		// Creo la clausola WHERE della query.
		List<Predicate> whereConditions = createFindUtenteByFilterWhereConditions(username, nome, cognome,
				ragioneSociale, cf, email, tipoRicerca, principal, utenteInfo, aziendaInfo, certificatoreInfo);

		// Aggiungo le condizioni alla query e la eseguo.
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		query.orderBy(qb.asc(principal.get(PfPrincipal_.idPfPrincipal)));
		TypedQuery<PfPrincipal> q = entityManager.createQuery(query);
		if (startResultsFrom > 0)
			q.setFirstResult(startResultsFrom);
		if (maxResults > 0)
			q.setMaxResults(maxResults);
		List<PfPrincipal> results = q.getResultList();

		List<UtentePojo> listaUtentiPojo = new ArrayList<UtentePojo>();
		for (PfPrincipal princ : results) {
			UtentePojo utentePojo = new UtentePojo();

			utentePojo.setUsername(princ.getUsername());
			utentePojo.setIdPfPrincipal(princ.getIdPfPrincipal());
			utentePojo.setDtScadenza(princ.getDtScadenza());
			utentePojo.setFlagAbilitato(princ.getFlagAbilitato());
			utentePojo.setFlagAbilitatoSare(princ.getFlagAbilitatoSare());
			utentePojo.setFlagAbilitatoServizi(princ.getFlagAbilitatoServizi());
			utentePojo.setEmail(princ.getEmail());
			utentePojo.setConfirmationToken(princ.getConfirmationToken());

			// controllo social network
			List<PfIdentityProvider> result = pfIdentityProviderHome.findByPFPId(princ.getIdPfPrincipal());
			if (result.size() > 0) {
				utentePojo.setSocialNetwork(result.get(0).getCodTipoProvider().toString());
			} else {
				utentePojo.setSocialNetwork("Nessuno");
			}

			// provincia di riferimento
			utentePojo.setProvinciaRiferimento(
					pfPrincipalHome.getDenominazioneProvinciaRiferimento(princ.getIdPfPrincipal()));

			listaUtentiPojo.add(utentePojo);
		}

		return listaUtentiPojo;
	}

	/** Count per paginazione. */
	public Long findUtenteCountByFilter(String username, String nome, String cognome, String ragioneSociale, String cf,
			String email, String tipoRicerca) {
		// Costruisco la query.
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = qb.createQuery(Long.class);
		Root<PfPrincipal> principal = query.from(PfPrincipal.class);

		// Costruisco variabili per potenziali join (serviranno dopo).
		Join<PfPrincipal, UtenteInfo> utenteInfo = null;
		Join<PfPrincipal, AziendaInfo> aziendaInfo = null;
		Join<PfPrincipal, CertificatoreInfo> certificatoreInfo = null;

		// Faccio i join a seconda del tipo di ricerca.
		if (tipoRicerca.equals("CITTADINO")) {
			utenteInfo = principal.join(PfPrincipal_.utenteInfo, JoinType.INNER);
		} else if (tipoRicerca.equals("AZIENDA")) {
			aziendaInfo = principal.join(PfPrincipal_.aziendaInfo, JoinType.INNER);
		} else if (tipoRicerca.equals("PROVINCIA")) {
			principal.join(PfPrincipal_.provinciasForIdPfPrincipal, JoinType.INNER);
		} else if (tipoRicerca.equals("REGIONE")) {
			principal.join(PfPrincipal_.regionesForIdPfPrincipal, JoinType.INNER);
		} else if (tipoRicerca.equals("CONSULENTE")) {
			principal.join(PfPrincipal_.consulenteInfo, JoinType.INNER);
		} else if (tipoRicerca.equals("COORDINATORE")) {
			principal.join(PfPrincipal_.coordinatoreInfo, JoinType.INNER);
		} else if (tipoRicerca.equals("CERTIFICATORE")) {
			certificatoreInfo = principal.join(PfPrincipal_.certificatoreInfo, JoinType.INNER);
		} else if (tipoRicerca.equals("PATRONATO")) {
			principal.join(PfPrincipal_.patronatosForIdPfPrincipal, JoinType.INNER);
		} else if (tipoRicerca.equals("SOGGETTO_PUBBLICO")) {
			principal.join(PfPrincipal_.soggettoPubbsForIdPfPrincipal, JoinType.INNER);
		} else {
			// Se non è uno dei tipi precedenti, assumo che sia di tipo "TUTTI"
			utenteInfo = principal.join(PfPrincipal_.utenteInfo, JoinType.LEFT);
			aziendaInfo = principal.join(PfPrincipal_.aziendaInfo, JoinType.LEFT);
			certificatoreInfo = principal.join(PfPrincipal_.certificatoreInfo, JoinType.LEFT);
		}

		// Creo la clausola WHERE della query.
		List<Predicate> whereConditions = createFindUtenteByFilterWhereConditions(username, nome, cognome,
				ragioneSociale, cf, email, tipoRicerca, principal, utenteInfo, aziendaInfo, certificatoreInfo);

		// Aggiungo le condizioni alla query e la eseguo.
		query.where(whereConditions.toArray(new Predicate[whereConditions.size()]));
		query.select(qb.count(principal.get(PfPrincipal_.idPfPrincipal)));
		TypedQuery<Long> q = entityManager.createQuery(query);
		return q.getSingleResult();
	}

	/**
	 * Crea la clausola WHERE per findUtenteByFilter e findUtenteCountByFilter.
	 */
	public List<Predicate> createFindUtenteByFilterWhereConditions(String username, String nome, String cognome,
			String ragioneSociale, String cf, String email, String tipoRicerca, Root<PfPrincipal> principal,
			Join<PfPrincipal, UtenteInfo> utenteInfo, Join<PfPrincipal, AziendaInfo> aziendaInfo,
			Join<PfPrincipal, CertificatoreInfo> certificatoreInfo) {

		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		List<Predicate> whereConditions = new ArrayList<Predicate>();

		// Parametro 1: username (tutti i tipi di ricerca)
		if (username != null && !username.isEmpty()) {
			Predicate condition1 = qb.like(qb.upper(principal.get(PfPrincipal_.username)),
					username.toUpperCase() + "%");
			whereConditions.add(condition1);
		}

		// Parametro 2: nome (tutti i tipi)
		if (nome != null && !nome.isEmpty()) {
			Predicate condition2 = qb.like(qb.lower(principal.get(PfPrincipal_.nome)), nome.toLowerCase() + "%");
			whereConditions.add(condition2);
		}

		// Parametro 3: cognome (tutti i tipi)
		if (cognome != null && !cognome.isEmpty()) {
			Predicate condition3 = qb.like(qb.lower(principal.get(PfPrincipal_.cognome)), cognome.toLowerCase() + "%");
			whereConditions.add(condition3);
		}

		// Parametro 4: ragione sociale ("tutti", "azienda" o "certificatore")
		if (ragioneSociale != null && !ragioneSociale.isEmpty()) {
			if (aziendaInfo != null) {
				Predicate condition4azienda = qb.like(qb.lower(aziendaInfo.get(AziendaInfo_.ragioneSociale)),
						ragioneSociale.toLowerCase() + "%");
				whereConditions.add(condition4azienda);
			} else if (certificatoreInfo != null) {
				Predicate condition4certificatore = qb.like(
						qb.lower(certificatoreInfo.get(CertificatoreInfo_.ragioneSociale)),
						ragioneSociale.toLowerCase() + "%");
				whereConditions.add(condition4certificatore);
			}
		}

		// Parametro 5: email (tutti i tipi)
		if (email != null && !email.isEmpty()) {
			Predicate condition5 = qb.like(qb.upper(principal.get(PfPrincipal_.email)), email.toUpperCase() + "%");
			whereConditions.add(condition5);
		}

		// Parametro 5: codice fiscale ("tutti", "cittadino" o "azienda")
		if (cf != null && !cf.isEmpty()) {
			if (utenteInfo != null) {
				Predicate condition6cittadino = qb.like(qb.lower(utenteInfo.get(UtenteInfo_.codiceFiscale)),
						cf.toLowerCase() + "%");
				whereConditions.add(condition6cittadino);
			} else if (aziendaInfo != null) {
				Predicate condition6azienda = qb.like(qb.lower(aziendaInfo.get(AziendaInfo_.codiceFiscale)),
						cf.toLowerCase() + "%");
				whereConditions.add(condition6azienda);
			} else if (certificatoreInfo != null) {
				Predicate condition6certificatore = qb.like(
						qb.lower(certificatoreInfo.get(CertificatoreInfo_.codiceFiscale)), cf.toLowerCase() + "%");
				whereConditions.add(condition6certificatore);
			}
		}

		return whereConditions;
	}

	/**
	 * Questa funzione fa una query per recuperare un utente di tipo "cittadino" partendo dal suo idPfPrincipal.
	 */
	public PfPrincipal findCittadinoById(Integer idPfPrincipal) {
		if (idPfPrincipal == null)
			return null;
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<PfPrincipal> query = qb.createQuery(PfPrincipal.class);
		Root<PfPrincipal> principal = query.from(PfPrincipal.class);
		Join<PfPrincipal, UtenteInfo> utenteInfo = principal.join(PfPrincipal_.utenteInfo);

		Predicate idPred = qb.equal(principal.get(PfPrincipal_.idPfPrincipal), idPfPrincipal);

		query.where(idPred);

		TypedQuery<PfPrincipal> q = entityManager.createQuery(query);
		/* e prendo i risultati */
		List<PfPrincipal> instance = q.getResultList();
		if (instance.isEmpty())
			return null;
		return instance.get(0);
	}

	public void abilitaUtente(Integer idPfPrincipal) {
		PfPrincipal pfPrincipal = findById(idPfPrincipal);
		if (pfPrincipal != null) {
			pfPrincipal.setFlagAbilitato(true);
			merge(pfPrincipal);
		}
	}

	/**
	 * Disabilita permanentemente un utente. Cambia l'username, la mail, la data scadenza ed i tre flag "abilitato".
	 * 
	 * @param idPfPrincipal
	 *            L'id dell'utente da disabilitare.
	 */
	public void disabilitaUtente(Integer idPfPrincipal) {
		PfPrincipal pfPrincipal = findById(idPfPrincipal);
		if (pfPrincipal != null) {
			String username = "CANC_" + pfPrincipal.getUsername();
			if (username.length() > 16)
				username = username.substring(0, 16);
			pfPrincipal.setUsername(username);

			String email = "CANC_" + pfPrincipal.getEmail();
			pfPrincipal.setEmail(email);

			Calendar ieri = Calendar.getInstance();
			ieri.add(Calendar.DATE, -1);
			pfPrincipal.setDtScadenza(ieri.getTime());

			pfPrincipal.setFlagAbilitato(false);
			pfPrincipal.setFlagAbilitatoSare(false);
			pfPrincipal.setFlagAbilitatoServizi(false);
			merge(pfPrincipal);
		}
	}

	/**
	 * Determina il tipo di un utente (cittadino, azienda, eccetera).
	 */
	public String getTipoUtente(PfPrincipal utente) {
		if (utente.isAdministrator())
			return "Amministratore";
		else if (utente.isUtente())
			return "Cittadino";
		else if (utente.isAzienda())
			return "Azienda";
		else if (utente.isCertificatore())
			return "Certificatore";
		else if (utente.isConsulente())
			return "Consulente";
		else if (utente.isCoordinatore())
			return "Coordinatore";
		else if (utente.isPatronato())
			return "Patronato";
		else if (utente.isSoggettoPubblico())
			return "SoggettoPubblico";

		// Per scoprire se è una provincia o una regione devo fare delle query
		// (collezione lazy).
		String stringaProvincia = " SELECT COUNT(p.idPfPrincipal) " + " FROM PfPrincipal p "
				+ " INNER JOIN p.provinciasForIdPfPrincipal " + " WHERE p.idPfPrincipal = :id ";
		TypedQuery<Long> queryProvincia = entityManager.createQuery(stringaProvincia, Long.class);
		queryProvincia.setParameter("id", utente.getIdPfPrincipal());
		if (queryProvincia.getSingleResult() > 0)
			return "Provincia";

		String stringaRegione = " SELECT COUNT(p.idPfPrincipal) " + " FROM PfPrincipal p "
				+ " INNER JOIN p.regionesForIdPfPrincipal " + " WHERE p.idPfPrincipal = :id ";
		TypedQuery<Long> queryRegione = entityManager.createQuery(stringaRegione, Long.class);
		queryRegione.setParameter("id", utente.getIdPfPrincipal());
		if (queryRegione.getSingleResult() > 0)
			return "Regione";

		// Se non è di nessuno dei tipi precedenti...
		return "????";
	}

	/**
	 * Recupera la provincia di riferimento di un utente (varia a seconda del tipo). Cittadino: provincia del comune di
	 * domicilio; Azienda non abilitata SARE: provincia del comune della sede operativa; Azienda abilitata SARE:
	 * provincia di riferimento del SARE; Tutti gli altri: "Nessuna".
	 */
	public String getDenominazioneProvinciaRiferimento(Integer idPfPrincipal) {
		PfPrincipal utente = findById(idPfPrincipal);
		if (utente.isUtente()) {
			return utente.getUtenteInfo().getDeComuneDomicilio().getDeProvincia().getDenominazione();
		} else if (utente.isAzienda()) {
			if (utente.getFlagAbilitatoSare()) {
				return utente.getAziendaInfo().getDeProvincia().getDenominazione();
			} else {
				return utente.getAziendaInfo().getDeComuneSede().getDeProvincia().getDenominazione();
			}
		}
		return "Nessuna";
	}

	// per trento ricerco gli utenti creati dallo SPIL ma non abilitati
	// perch era disabilitato ClicLavoro
	@Deprecated
	public List<PfPrincipal> findUserDaSPilNonAbilitati() {
		String query = "select p.* " + " from myportal.cv_dati_personali c "
				+ " inner join mycas.pf_principal p on p.id_pf_principal = c.id_pf_principal_palese "
				+ " inner join mycas.utente_info i on i.id_pf_principal = c.id_pf_principal_palese "
				+ " where c.cod_comunicazione_sil is not null " + " and c.cod_provenienza_curriculum = 'SILTN' "
				+ " and p.flag_abilitato = 'N' "
				+ " and p.id_pf_principal not in (select id_pf_principal from mycas.pf_identity_provider) ";
		Query nativeQuery = entityManager.createNativeQuery(query, PfPrincipal.class);
		List<PfPrincipal> list = nativeQuery.getResultList();
		return list;
	}

	public List<PfPrincipal> findPrincipalsProvinceByCodProvincia(String codProvincia) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<PfPrincipal> criteria = cb.createQuery(PfPrincipal.class);
		Root<Provincia> provinciaRoot = criteria.from(Provincia.class);
		Join<Provincia, PfPrincipal> pfPrincipalJoin = provinciaRoot.join(Provincia_.pfPrincipal);
		Join<Provincia, DeProvincia> deProvinciaJoin = provinciaRoot.join(Provincia_.deProvincia);
		criteria.select(pfPrincipalJoin);
		criteria.where(cb.equal(deProvinciaJoin.get(DeProvincia_.codProvincia), codProvincia));
		TypedQuery<PfPrincipal> query = entityManager.createQuery(criteria);
		return query.getResultList();
	}

	public List<PfPrincipal> findPrincipalsByCodRuoloPortale(String codRuoloPortale) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<PfPrincipal> criteria = cb.createQuery(PfPrincipal.class);
		Root<PfPrincipal> pfPrincipalRoot = criteria.from(PfPrincipal.class);
		Join<PfPrincipal, DeRuoloPortale> deRuoloPortaleJoin = pfPrincipalRoot.join(PfPrincipal_.deRuoloPortale);
		criteria.select(pfPrincipalRoot);
		criteria.where(cb.equal(deRuoloPortaleJoin.get(DeRuoloPortale_.codRuoloPortale), codRuoloPortale));
		TypedQuery<PfPrincipal> query = entityManager.createQuery(criteria);
		return query.getResultList();
	}
	
	public PfPrincipal findByIdVaDatiVacancy(Integer idVaDatiVacancy){
		List<PfPrincipal> utentiList = entityManager.createNamedQuery("findByIdVaDatiVacancy", PfPrincipal.class)
				.setParameter("idVaDatiVacancy", idVaDatiVacancy).getResultList();
		if (utentiList == null || utentiList.isEmpty()) {
			String errorMessage = "Errore nella findByIdVaDatiVacancy: vacancy con id " + idVaDatiVacancy + " non trovato.";
			log.error(errorMessage);
			throw new RuntimeException(errorMessage);
		}
		if (utentiList.size() > 1) {
			String errorMessage = "Errore nella findByIdVaDatiVacancy: trovati " + utentiList.size()
					+ " utenti per idVaDatiVacancy " + idVaDatiVacancy + ". Id degli utenti: ";
			for (PfPrincipal utente : utentiList) {
				errorMessage += utente.getIdPfPrincipal() + ", ";
			}
			log.error(errorMessage);
			throw new RuntimeException(errorMessage);
		}
		return utentiList.get(0);
	}
	
	public List<PfPrincipal> findPaleseByIdVaDatiVacancy(Integer idVaDatiVacancy){
		List<PfPrincipal> utentiList = entityManager.createNamedQuery("findPaleseByIdVaDatiVacancy", PfPrincipal.class)
				.setParameter("idVaDatiVacancy", idVaDatiVacancy).getResultList();
		if (utentiList.size() > 1) {
			String errorMessage = "Errore nella findByIdVaDatiVacancy: trovati " + utentiList.size()
					+ " utenti per idVaDatiVacancy " + idVaDatiVacancy + ". Id degli utenti: ";
			for (PfPrincipal utente : utentiList) {
				errorMessage += utente.getIdPfPrincipal() + ", ";
			}
			log.error(errorMessage);
			throw new RuntimeException(errorMessage);
		}
		return utentiList;
	}
	public PfPrincipal findAbilitatoByCodServiziAmministrativi(String codServiziAmministrativi)
			throws MyPortalNotFoundException {
		// si ignorano eventuali trattini anche a bend
		if (StringUtils.isEmpty(codServiziAmministrativi))
			throw new MyPortalNotFoundException("Codice servizi amministrativi mancante");
		
		codServiziAmministrativi = codServiziAmministrativi.replaceAll("-", "");

		Integer idPfPrincipal = UtenteInfo.codAmmDecode(codServiziAmministrativi).intValue();

		PfPrincipal utentiList = findById(idPfPrincipal);

		if (utentiList == null)
			throw new MyPortalNotFoundException();
		return utentiList;

	}

}
