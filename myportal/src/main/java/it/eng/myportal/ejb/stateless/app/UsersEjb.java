package it.eng.myportal.ejb.stateless.app;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import it.eng.myportal.entity.PfIdentityDevice;
import it.eng.myportal.entity.PfPrincipal;
import it.eng.myportal.entity.UtenteInfo;
import it.eng.myportal.entity.home.PfIdentityDeviceHome;
import it.eng.myportal.entity.home.PfPrincipalHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.rest.app.exception.AppEjbException;
import it.eng.myportal.rest.app.exception.BlockedLoginException;
import it.eng.myportal.rest.app.exception.EmailNotUniqueException;
import it.eng.myportal.rest.app.exception.ExpiredPasswordException;
import it.eng.myportal.rest.app.exception.GenericException;
import it.eng.myportal.rest.app.exception.UserNotActiveException;
import it.eng.myportal.rest.app.exception.UserNotEnabledException;
import it.eng.myportal.rest.app.exception.UserNotFoundException;
import it.eng.myportal.rest.app.exception.WrongPasswordException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;
import it.eng.sil.base.utils.StringUtils;

@Stateless
public class UsersEjb {

	private static final String NOME = "nome";
	private static final String COGNOME = "cognome";
	private static final String DATA_NASCITA = "data_nascita";
	private static final String COMUNE_DOMICILIO = "comune_domicilio";
	private static final String PROVINCIA_DOMICILIO = "provincia_domicilio";
	private static final String INDIRIZZO_DOMICILIO = "indirizzo_domicilio";
	private static final String EMAIL = "email";
	private static final String GENERE = "genere";
	private static final String FOTO = "foto";

	private static final String SERVIZI_AMMINISTRATIVI_ABILITATI = "serviziAmministrativiAbilitati";
	private static final String ACCETTATA_INFORMATIVA_PERCORSO_LAVORATORE = "accettataInformativaPercorsoLavoratore";
	private static final String ACCETTATA_INFORMATIVA_DID = "accettataInformativaDid";

	private final static String OK = "ok";
	private final static String STATUS = "status";
	private final static String TOKEN = "token";

	protected static Log log = LogFactory.getLog(UsersEjb.class);

	@PersistenceContext
	protected EntityManager entityManager;

	@EJB
	PfPrincipalHome pfPrincipalHome;

	@EJB
	PfIdentityDeviceHome pfIdentityDeviceHome;

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	UsersEjb usersEjb;

	public String login(String username, String password) throws AppEjbException {

		String ret = null;

		PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);

		try {
			if (pfPrincipal != null) {
				if (pfPrincipal.isUtente()) {
					Long waitSecond = usersEjb.isLocked(username);
					if (waitSecond <= 0) {
						if (pfPrincipal.getPassWord().equals(password)) {
							if (pfPrincipal.getFlagAbilitato()) {
								if (new Date().before(pfPrincipal.getDtScadenza())) {
									ret = getUserInfo(pfPrincipal);
									// All'atto del login si registra l'utente in PfIdentityDevice, se non già presente
									List<PfIdentityDevice> listPfIdentityDevice = pfIdentityDeviceHome
											.findDevice(pfPrincipal.getIdPfPrincipal());
									if (listPfIdentityDevice == null || listPfIdentityDevice.isEmpty()) {
										pfIdentityDeviceHome.create(pfPrincipal.getIdPfPrincipal());
									}

									// Vengono resettati in PfPrincipal il numero tentativi e data ultimo tentativo
									usersEjb.resetCounter(username);

								} else {
									// Password scaduta
									throw new ExpiredPasswordException(username);
								}
							} else {
								// Utente non attivo
								throw new UserNotActiveException(username);
							}
						} else {
							// Password errata

							// Viene registrato in PfPrincipal un tentativo di accesso errato al sistema
							usersEjb.incTentativi(username);

							throw new WrongPasswordException(username);
						}
					} else {
						// Superato il numero di tentativi di accesso senza attendere l'intervallo di tempo necessario
						throw new BlockedLoginException();
					}
				} else {
					// Utente non abilitato - non cittadino
					throw new UserNotEnabledException(username);
				}
			} else {
				// Utente non trovato
				throw new UserNotFoundException(username);
			}
		} catch (JSONException e) {
			throw new GenericException("Errore durante la costruzione dell'oggetto");
		} catch (AppEjbException e) {
			throw e;
		} catch (Exception e) {
			throw new GenericException("Errore durante il processo di login");
		}

		log.info("L'utente " + pfPrincipal.getUsername() + "(" + pfPrincipal.getIdPfPrincipal()
				+ ") ha effettuato il login al sistema con successo.");

		return ret;
	}

	public String getUserInfo(String username) throws AppEjbException {

		String ret = null;

		try {
			PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);
			if (pfPrincipal != null) {

				ret = getUserInfo(pfPrincipal);

			} else {
				throw new UserNotFoundException(username);
			}
		} catch (JSONException e) {
			throw new GenericException("Errore durante la costruzione dell'oggetto");
		} catch (AppEjbException e) {
			throw e;
		} catch (Exception e) {
			throw new GenericException("Errore durante il processo di recupero informazioni utente");
		}
		return ret;
	}

	public String updateUser(String username, String nome, String cognome, String dtNascita, String email)
			throws AppEjbException {

		String ret = null;

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

		try {
			PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);

			if (pfPrincipal != null && pfPrincipal.getUtenteInfo() != null) {

				// Validazione email, non è accettato un altro utente con la stessa email
				boolean unique = true;
				if (!StringUtils.isEmptyNoBlank(email))
					unique = pfPrincipalHome.checkUniqueEmail(pfPrincipal.getIdPfPrincipal(), email);

				if (unique) {
					// Detach dell'entity dal contesto
					entityManager.detach(pfPrincipal);

					// Set dei campi modificati
					if (!StringUtils.isEmptyNoBlank(nome))
						pfPrincipal.setNome(nome);
					if (!StringUtils.isEmptyNoBlank(cognome))
						pfPrincipal.setCognome(cognome);

					if (!StringUtils.isEmptyNoBlank(dtNascita))
						pfPrincipal.getUtenteInfo().setDtNascita(simpleDateFormat.parse(dtNascita));

					if (!StringUtils.isEmptyNoBlank(email))
						pfPrincipal.setEmail(email);

					// Set dtmMod, idPrincipalMod sia di PfPrincipal che UtenteInfo
					Date dtmMod = new Date();
					pfPrincipal.setDtmMod(dtmMod);
					pfPrincipal.getUtenteInfo().setDtmMod(dtmMod);
					pfPrincipal.setPfPrincipalMod(pfPrincipal);
					pfPrincipal.getUtenteInfo().setPfPrincipalMod(pfPrincipal);

					// merge delle informazioni
					pfPrincipal = pfPrincipalHome.merge(pfPrincipal);

					// Ritorno del json utente aggiornato
					ret = getUserInfo(pfPrincipal);
				} else {
					// Email già presente
					throw new EmailNotUniqueException(email);
				}

			} else {
				throw new UserNotFoundException(username);
			}
		} catch (ParseException e) {
			throw new GenericException(
					"Errore durante il processo di aggiornamento delle informazioni utente: formato della data non corretto");
		} catch (JSONException e) {
			throw new GenericException("Errore durante la costruzione dell'oggetto");
		} catch (AppEjbException e) {
			throw e;
		} catch (Exception e) {
			throw new GenericException("Errore durante il processo di aggiornamento delle informazioni utente");
		}
		return ret;
	}

	/*
	 * Verifica se l'utente può accedere. Restituisce 0 se l'utente può accedere. Restituisce un numero > 0, secondi che
	 * l'utente deve attendere prima di poter effettuare nuovamente l'accesso se è bloccato.
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Long isLocked(String username) {

		PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);

		if (pfPrincipal.getNumTentativi() >= ConstantsSingleton.MAX_NUM_TENTATIVI.intValue()
				&& pfPrincipal.getDtLastCheckin() != null) {
			Date now = new java.util.Date();

			// conta quanti secondi sono passati dall'ultimo tentativo fallito.
			long secondsSinceLastLogin = (now.getTime() - pfPrincipal.getDtLastCheckin().getTime()) / 1000;
			// se ha aspettato a sufficienza
			if (secondsSinceLastLogin >= ConstantsSingleton.WAIT_SECONDS) {
				// resetta il contatore
				resetCounter(username);

				return 0L;
			}
			// altrimenti restituisci il numero di secondi che deve aspettare
			else {
				return ConstantsSingleton.WAIT_SECONDS - secondsSinceLastLogin;
			}
		}
		// può ancora provare il login
		else {
			return 0L;
		}
	}

	/*
	 * Azzera il numero di tentatiti e la data di ultimo accesso in caso di accesso con successo.
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void resetCounter(String username) {
		PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);
		pfPrincipal.setNumTentativi(0);
		pfPrincipal.setDtLastCheckin(new Date());
		entityManager.merge(pfPrincipal);
	}

	/*
	 * Memorizza un tentativo di accesso errato al sistema
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void incTentativi(String username) {
		PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);
		pfPrincipal.setNumTentativi(pfPrincipal.getNumTentativi() + 1);
		entityManager.merge(pfPrincipal);
	}

	private String getUserInfo(PfPrincipal pfPrincipal) throws JSONException {

		JSONObject obj = new JSONObject();

		UtenteInfo utenteInfo = pfPrincipal.getUtenteInfo();

		obj.put(STATUS, OK);
		obj.put(TOKEN, Utils.getTokenUtente(pfPrincipal.getUsername()));
		obj.put(NOME, pfPrincipal.getNome());
		obj.put(COGNOME, pfPrincipal.getCognome());
		obj.put(DATA_NASCITA, utenteInfo.getDtNascita());
		obj.put(EMAIL, pfPrincipal.getEmail());
		if (utenteInfo.getDeGenere() != null)
			obj.put(GENERE, utenteInfo.getDeGenere().getCodGenere());

		// Gestione indirizzo/comune/provincia domicilio/residenza con prioritò sul primo
		if (utenteInfo.getDeComuneDomicilio() != null) {
			obj.put(INDIRIZZO_DOMICILIO, utenteInfo.getIndirizzoDomicilio());
			obj.put(COMUNE_DOMICILIO, utenteInfo.getDeComuneDomicilio().getDenominazione());
			if (utenteInfo.getDeComuneDomicilio().getDeProvincia() != null)
				obj.put(PROVINCIA_DOMICILIO, utenteInfo.getDeComuneDomicilio().getDeProvincia().getTarga());
		} else if (utenteInfo.getDeComuneResidenza() != null) {
			obj.put(INDIRIZZO_DOMICILIO, utenteInfo.getIndirizzoResidenza());
			obj.put(COMUNE_DOMICILIO, utenteInfo.getDeComuneResidenza().getDenominazione());
			if (utenteInfo.getDeComuneResidenza().getDeProvincia() != null)
				obj.put(PROVINCIA_DOMICILIO, utenteInfo.getDeComuneResidenza().getDeProvincia().getTarga());
		}

		if (utenteInfo.getFoto() != null && utenteInfo.getFoto().length > 0) {
			obj.put(FOTO, new String(Base64.encodeBase64(utenteInfo.getFoto())));
		}
		obj.put(SERVIZI_AMMINISTRATIVI_ABILITATI, pfPrincipal.getFlagAbilitatoServizi());
		obj.put(ACCETTATA_INFORMATIVA_PERCORSO_LAVORATORE, utenteInfo.getFlgAcceptedInformativaPercLav());
		obj.put(ACCETTATA_INFORMATIVA_DID, utenteInfo.getFlgAcceptedInformativaDid());

		return obj.toString();
	}

	public String updateFoto(String username, byte[] data) throws AppEjbException {

		String ret = null;

		try {
			PfPrincipal pfPrincipal = pfPrincipalHome.findByUsername(username);

			if (pfPrincipal != null) {
				pfPrincipal.getUtenteInfo().setFoto(data);

				// Set dtmMod, idPrincipalMod sia di PfPrincipal che UtenteInfo
				Date dtmMod = new Date();
				pfPrincipal.setDtmMod(dtmMod);
				pfPrincipal.getUtenteInfo().setDtmMod(dtmMod);
				pfPrincipal.setPfPrincipalMod(pfPrincipal);
				pfPrincipal.getUtenteInfo().setPfPrincipalMod(pfPrincipal);

				// merge delle informazioni
				pfPrincipal = pfPrincipalHome.merge(pfPrincipal);

				// Json Ritorno
				JSONObject obj = new JSONObject();
				obj.put(STATUS, OK);
				ret = obj.toString();

			} else {
				throw new UserNotFoundException(username);
			}
		} catch (JSONException e) {
			throw new GenericException("Errore durante la costruzione dell'oggetto");
		} catch (AppEjbException e) {
			throw e;
		} catch (Exception e) {
			throw new GenericException("Errore durante il processo aggiornamento della foto del profilo");
		}
		return ret;
	}

}
