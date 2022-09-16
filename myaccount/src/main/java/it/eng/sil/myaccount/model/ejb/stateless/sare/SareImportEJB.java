package it.eng.sil.myaccount.model.ejb.stateless.sare;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import it.eng.sil.myaccount.model.ejb.stateless.decodifiche.DeAutorizzazioneSareEJB;
import it.eng.sil.myaccount.model.ejb.stateless.decodifiche.DeTipoUtenteSareEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.AziendaInfoEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.PfPrincipalMyAccountEJB;
import it.eng.sil.myaccount.model.ejb.stateless.profile.ProvinciaEJB;
import it.eng.sil.myaccount.model.entity.migrazione.SareImport;
import it.eng.sil.myaccount.model.entity.migrazione.SareImport_;
import it.eng.sil.myaccount.utils.Utils.SHA1;
import it.eng.sil.mycas.exceptions.MyCasNoResultException;
import it.eng.sil.mycas.model.entity.BaseTabellaDecodificaEntity;
import it.eng.sil.mycas.model.entity.decodifiche.DeProvincia;
import it.eng.sil.mycas.model.entity.profilo.AziendaInfo;
import it.eng.sil.mycas.model.entity.profilo.PfPrincipal;
import it.eng.sil.mycas.model.entity.profilo.Provincia;
import it.eng.sil.mycas.model.manager.AbstractEJB;
import it.eng.sil.mycas.model.manager.AbstractTabellaDecodificaEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeComuneEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeCpiEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeProvinciaEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeTipoAbilitatoEJB;
import it.eng.sil.mycas.model.manager.decodifiche.DeTipoDelegatoEJB;

/**
 * Home object per l'importazione dei dati utente SARE. I dati vengono appoggiati nella tabella sare_import, 69 colonne
 * di disgrazie.
 * 
 * A seconda dei casi, ogni riga della tabella sare_import viene convertita
 * 
 * - in una AziendaInfo - in una Provincia/CPI
 * 
 * Ripreso, rivisto e modificato nel aprile 2017
 * Ripreso, rivisto e modificato nel ottobre 2019
 * 
 * @since 2.4.6
 * 
 * 
 * @author Pegoraro, gcozza
 */
@Stateless
public class SareImportEJB extends AbstractEJB<SareImport, Integer> {
	final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	//static SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

	@EJB
	DeComuneEJB deComuneEJB;

	@EJB
	DeProvinciaEJB deProvinciaEJB;

	@EJB
	DeCpiEJB deCpiEJB;

	@EJB
	AziendaInfoEJB aziendaInfoEJB;

	@EJB
	ProvinciaEJB provinciaEJB;

	@EJB
	DeTipoAbilitatoEJB deTipoAbilitatoEJB;

	@EJB
	DeTipoDelegatoEJB deTipoDelegatoEJB;

	@EJB
	DeAutorizzazioneSareEJB deAutorizzazioneSareEJB;

	@EJB
	DeTipoUtenteSareEJB deTipoUtenteSareEJB;

	@EJB
	PfPrincipalMyAccountEJB pfPrincipalMyAccountEJB;

	/**
	 * Metodo invocato dalla pagina per lanciare l'importazione massiva La gestione della transazione occorre per
	 * persisterre errori e warning nella tabella, lasciando "libera" la transazione a monte e iniziandone una nuova per
	 * riga.
	 * 
	 * Non molto efficiente, ma considerata la natura one-shot di 'sto bagaglio, va bene così.
	 */
	public List<SareImport> findAllSareAccounts(Integer limit) {
		// Tiro fuori tutti i record dalla tabella di appoggio (ogni record
		// diventerà un utente)
		CriteriaBuilder qb = entityMan.getCriteriaBuilder();
		CriteriaQuery<SareImport> criteria = qb.createQuery(SareImport.class);
		Root<SareImport> root = criteria.from(SareImport.class);
		criteria.select(root);

		// TEMP: filtri (sempre flgfatto = 0, poi eventuali altri filtri su tipo utente o cose così)
		List<Predicate> whereConditions = new ArrayList<Predicate>();
		whereConditions.add(qb.equal(root.get(SareImport_.flgFatto), 0));
		// whereConditions.add(qb.equal(root.get(SareImport_.utenteTipoUtente), "R"));
		criteria.where(whereConditions.toArray(new Predicate[whereConditions.size()]));

		TypedQuery<SareImport> query = entityMan.createQuery(criteria);
		if (limit != null && limit > 0)
			query.setMaxResults(limit);

		List<SareImport> sareRowList = query.getResultList();
		return sareRowList;
	}

	private <T extends BaseTabellaDecodificaEntity<K>, E extends AbstractTabellaDecodificaEJB<T, K>, K> T tryToFindById(
			E ejb, K key, SareImport sareRow, boolean obbligatorio, String nomeCampo) {
		try {
			// Se faccio una findById(null), loggo l'errore SOLO se era un campo marcato come obbligatorio.
			if (key == null || "null".equalsIgnoreCase(key.toString())) {
				if (obbligatorio)
					throw new MyCasNoResultException("Primary key nulla");
				else
					return null;
			}

			// Nel caso della DeComune, uso la toUpperCase per evitare problemi coi dati.
			if (ejb instanceof DeComuneEJB && key instanceof String) {
				key = (K) key.toString().toUpperCase();
			}

			return (ejb.findById(key));
		} catch (Exception e) {
			String errorMessage = "Errore durante la findById del campo " + nomeCampo + ", valore: " + key;
			log.warn(errorMessage);
			if (obbligatorio) {
				sareRow.setErroriRegistrazione(sareRow.getErroriRegistrazione() + " / " + errorMessage);
			} else {
				sareRow.setWarnRegistrazione(sareRow.getWarnRegistrazione() + " / " + errorMessage);
			}

			return null;
		}
	}

	private Date tryToParseDate(String data, SareImport sareRow, boolean obbligatorio, String nomeCampo) {
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		try {
			// Se faccio una tryToParseDate(null), loggo l'errore SOLO se era un campo marcato come obbligatorio.
			if (data == null || "null".equalsIgnoreCase(data)) {
				if (obbligatorio)
					throw new MyCasNoResultException("Primary key nulla");
				else
					return null;
			}
			return formatter.parse(data);
		} catch (Exception e) {
			String errorMessage = "Errore durante il parsing della data per il campo " + nomeCampo + ", valore: "
					+ data;
			// log.warn(errorMessage);
			if (obbligatorio) {
				sareRow.setErroriRegistrazione(sareRow.getErroriRegistrazione() + " / " + errorMessage);
			} else {
				sareRow.setWarnRegistrazione(sareRow.getWarnRegistrazione() + " / " + errorMessage);
			}
			return null;
		}
	}

	public void processSingleSareAccount(SareImport sareRow) {
		log.info("---Inizio il porting dell'utente " + sareRow.getUtenteCodFiscUtente() + "...");
		sareRow.setWarnRegistrazione("");
		sareRow.setEsitoRegistrazione("");
		sareRow.setErroriRegistrazione("");

		String tipout = sareRow.getUtenteTipoUtente();
		PfPrincipal newUser = null;
		if (tipout.equals("T")) {
			sareRow.setWarnRegistrazione(sareRow.getWarnRegistrazione() + " / Utente di tipo controller SARE");
			sareRow.setFlgFatto(1);// non occorre ri-processarlo
			sareRow.setEsitoRegistrazione("Non importato");
			sareRow.setErroriRegistrazione("Utente con tipo T");
			entityMan.merge(sareRow);
			return;
		} else if (tipout.equals("M")) {
			sareRow.setWarnRegistrazione(sareRow.getWarnRegistrazione() + "Utente di tipo gestore tirocinio Esterno");
			sareRow.setFlgFatto(1);// non occorre ri-processarlo
			sareRow.setEsitoRegistrazione("Non importato");
			sareRow.setErroriRegistrazione("Utente con tipo M");
			entityMan.merge(sareRow);
			return;
		} else if (tipout.equals("N")) {
			sareRow.setWarnRegistrazione(sareRow.getWarnRegistrazione()
					+ "Utente di tipo  Gestore tirocinio RSA: SALTO");
			sareRow.setFlgFatto(1);// non occorre ri-processarlo
			sareRow.setEsitoRegistrazione("Non importato");
			sareRow.setErroriRegistrazione("Utente con tipo N");
			entityMan.merge(sareRow);
			return;
		} else if (tipout.equals("C") || tipout.equals("P")) {
			// Questi devono essere convertiti in utenti provinciali (sono CPI o
			// superuser)
			sareRow.setWarnRegistrazione(sareRow.getWarnRegistrazione() + " / Utente di tipo " + tipout
					+ " (CPI/superUser) : lo converto in Provincia");
			newUser = createNewProvincia(sareRow);
		} else if (tipout.equals("R")) {
			sareRow.setWarnRegistrazione(sareRow.getWarnRegistrazione() + " / Utente di tipo " + tipout
					+ " (mittente SARE) : lo converto in Azienda");
			// Questi sono mittenti SARE, da convertire in aziende!
			newUser = createNewAzienda(sareRow);
		} else {
			sareRow.setWarnRegistrazione(sareRow.getWarnRegistrazione()
					+ " / Attenzione, utente con tipo diverso da R/P/C/T/M/N : utente "
					+ sareRow.getUserextraStrragionesociale() + " - tipo : " + sareRow.getUtenteTipoUtente());
			sareRow.setFlgFatto(1);
			sareRow.setEsitoRegistrazione("Non importato");
			sareRow.setErroriRegistrazione("Utente con tipo diverso da R/P/C/T/M/N");
			entityMan.merge(sareRow);
			return;
		}

		PfPrincipal operatore = pfPrincipalMyAccountEJB.findByUsername("ut_porting");

		// Faccio la register() dell'utente creato.
		try {
			if (newUser == null) {
				throw new Exception("ERRORE GRAVE, newUser NULLO");
			}

			if (!daSaltare(newUser, sareRow)) {
				String oldEmail = newUser.getEmail();
				if (newUser.getAziendaInfo() != null) {
					AziendaInfo aziendaInfoSganciata = newUser.getAziendaInfo();
					newUser.setAziendaInfo(null);
					aziendaInfoSganciata.setPfPrincipal(null);
					aziendaInfoSganciata = aziendaInfoEJB.registerFromPortingSare(newUser, aziendaInfoSganciata,
							operatore.getIdPfPrincipal());

					// Controllo se durante la registrazione ho dovuto cambiare la email
					if (!aziendaInfoSganciata.getPfPrincipal().getEmail().equals(oldEmail)) {
						sareRow.setFlgEmailCambiata(true);
					}
				} else if (newUser.getProvinciasForIdPfPrincipal() != null) {
					Provincia provinciaSganciata = newUser.getProvinciasForIdPfPrincipal();
					newUser.setProvinciasForIdPfPrincipal(null);
					provinciaSganciata.setPfPrincipal(null);
					provinciaSganciata = provinciaEJB.registerFromPortingSare(newUser, provinciaSganciata,
							operatore.getIdPfPrincipal());

					// Controllo se durante la registrazione ho dovuto cambiare la email
					if (!provinciaSganciata.getPfPrincipal().getEmail().equals(oldEmail)) {
						sareRow.setFlgEmailCambiata(true);
					}
				} else {
					throw new Exception("ERRORE GRAVE: non previsto");
				}

				if (sareRow.isFlgUsernameCambiato()) {
					sareRow.setStrUpdateForSare("UPDATE UTENTE SET cod_fisc_utente='" + newUser.getUsername()
							+ "' WHERE id_utente=" + sareRow.getUtenteIdUtente() + ";");
				}

				sareRow.setIdPfPrincipal(newUser.getIdPfPrincipal());
				sareRow.setStrNewLogin(newUser.getUsername());
				sareRow.setFlgFatto(1);
				sareRow.setEsitoRegistrazione("OK");
			} else {
				// Utente saltato
				log.warn("ATTENZIONE: daSaltare() returned TRUE, salto l'utente");
				sareRow.setFlgFatto(1);
				sareRow.setEsitoRegistrazione("Non importato");
			}
		} catch (Exception e) {
			// finally farà la merge()
			e.printStackTrace();
			log.error("---Errore durante la registrazione "+ e.getClass()+":"+ e.getMessage());
			sareRow.setEsitoRegistrazione("Non importato");
			sareRow.setErroriRegistrazione(sareRow.getErroriRegistrazione() + " / " + e.getMessage());
		} finally {
			// A prescindere dall'esito della registrazione, faccio la merge della riga di SareImport
			// per salvare l'esido ed eventuali warn ed errori.
			entityMan.merge(sareRow);
			log.info("---Fine porting dell'utente " + sareRow.getUtenteCodFiscUtente());
		}
	}

	/**
	 * Riempie la tabella PfPrincipal (dati comuni a tutti i tipi di utente)
	 **/
	private void fillPfPrincipal(PfPrincipal newPfPrincipal, SareImport utenteSare) {

		// Username = codice fiscale utente (eventualmente troncato se più lungo di 16)
		String username = null;
		if (utenteSare.getUtenteCodFiscUtente() != null && utenteSare.getUtenteCodFiscUtente().length() > 16) {
			utenteSare.setWarnRegistrazione(utenteSare.getWarnRegistrazione() + " / Username TRONCATA");
			utenteSare.setFlgUsernameCambiato(true);
			username = utenteSare.getUtenteCodFiscUtente().substring(0, 16);
		} else {
			username = utenteSare.getUtenteCodFiscUtente();
		}

		// Se esiste già un utente con questo username, aggiungo la targa della
		// provincia.
		if (username != null && pfPrincipalMyAccountEJB.findByUsername(username) != null) {

			if (username.length() > 14)
				username = username.substring(0, 14);

			DeProvincia provinciaPerTarga = tryToFindById(deProvinciaEJB, utenteSare.getUtenteProvincia(),
					utenteSare, false, "utenteProvincia");
			if (provinciaPerTarga != null) {
				String targaProvincia = provinciaPerTarga.getTarga();
				username = username + targaProvincia;
			}
			utenteSare.setWarnRegistrazione(utenteSare.getWarnRegistrazione() + " / USERNAME Duplicato, imposto: "
					+ username);
			// Se ANCHE IL SECONDO USERNAME è già esistente, provo ad aggiungerci un 1. Se non riesco, la do su.
			if (username != null && pfPrincipalMyAccountEJB.findByUsername(username) != null) {
				if (username.length() > 15) {
					log.error("GRAVE : ANCHE IL SECONDO USERNAME E' DUPLICATO! RINUNCIO.");
					utenteSare.setErroriRegistrazione(utenteSare.getErroriRegistrazione()
							+ " / USERNAME duplicato che non riesco a risolvere!");
					username = null;
				} else {
					username = username + "1";
				}
			}

			if (username != null) {
				utenteSare.setFlgUsernameCambiato(true);
			}
		}
		newPfPrincipal.setUsername(username);

		// Se non c'è la password, gli metto quella di default (Temporanea123)
		if (utenteSare.getUtenteStrpassword() != null) {
			newPfPrincipal.setPassWord(SHA1.encrypt(utenteSare.getUtenteStrpassword()));
		} else {
			newPfPrincipal.setPassWord(SHA1.encrypt("Temporanea123"));
			utenteSare.setWarnRegistrazione(utenteSare.getWarnRegistrazione() + " / Password resettata: Temporanea123");
		}

		newPfPrincipal.setEmail(utenteSare.getUtenteEmailUtente());
		newPfPrincipal.setNome(utenteSare.getUserextraStrricnome());
		newPfPrincipal.setCognome(utenteSare.getUserextraStrriccognome());
		newPfPrincipal.setDomanda("Qual è il tuo codice fiscale?");
		newPfPrincipal.setRisposta(utenteSare.getMittentiStrcodicefiscale());
		newPfPrincipal.setFlagAbilitato(true);
		newPfPrincipal.setFlagAbilitatoServizi(false);
		newPfPrincipal.setFlagAbilitatoSare(true);
		newPfPrincipal.setPrivacy("1".equalsIgnoreCase(utenteSare.getUtentePrivacy()) ? "Y" : "N");
		newPfPrincipal.setDtFineValidita(tryToParseDate(utenteSare.getUtenteDataFineValidita(), utenteSare, false,
				"dtFineValidita"));
		newPfPrincipal.setIndirizzoUtente(utenteSare.getUserextraStrricindirizzo());
		newPfPrincipal.setMotivoFineValidita(utenteSare.getUtenteMotivoFineValidita());
		newPfPrincipal.setTelefonoUtente(utenteSare.getUtenteTelUtente());
		newPfPrincipal.setDtLastCheckin(tryToParseDate(utenteSare.getUtenteDtlastaccessdate(), utenteSare, false,
				"dtLastCheckin"));
		newPfPrincipal.setNumTentativi(0);
		newPfPrincipal.setStileSelezionato("myportal");

		// La data di scadenza password è tra 6 mesi.
		Calendar now = Calendar.getInstance();
		now.add(Calendar.MONTH, 6);
		newPfPrincipal.setDtScadenza(now.getTime());

		// Abilitiamo solo quelli con status richiesta sare 4 = "ABILITATO"
		// TODO check con Stefy se abilitarne altri
		if (utenteSare.getUtenteStrrequeststatus() != null
				&& "4".equals(utenteSare.getUtenteStrrequeststatus().replaceAll("0", ""))) {
			newPfPrincipal.setFlagAbilitato(true);
		} else {
			newPfPrincipal.setFlagAbilitato(false);
			utenteSare.setWarnRegistrazione(utenteSare.getWarnRegistrazione() + " / Utente NON abilitato, reqStatus= "
					+ utenteSare.getUtenteStrrequeststatus());
		}
	}

	/** Crea un nuovo utente di tipo Azienda **/
	private PfPrincipal createNewAzienda(SareImport utenteSare) {
		PfPrincipal newPfPrincipal = new PfPrincipal();
		AziendaInfo newAziendaInfo = new AziendaInfo();
		fillPfPrincipal(newPfPrincipal, utenteSare);
		newPfPrincipal.setAziendaInfo(newAziendaInfo);
		newAziendaInfo.setPfPrincipal(newPfPrincipal);

		// Se la ragione sociale è troppo lunga, la tronco. Se manca, ci metto il codice fiscale dell'azienda.
		if (utenteSare.getUserextraStrragionesociale() != null
				&& utenteSare.getUserextraStrragionesociale().length() > 100) {
			newAziendaInfo.setRagioneSociale(utenteSare.getUserextraStrragionesociale().substring(0, 97) + "...");
		} else {
			newAziendaInfo.setRagioneSociale(utenteSare.getUserextraStrragionesociale());
		}
		if (newAziendaInfo.getRagioneSociale() == null || newAziendaInfo.getRagioneSociale().isEmpty())
			newAziendaInfo.setRagioneSociale(utenteSare.getUtenteCodFiscUtente());

		// Se il codice fiscale è nullo, metto 11 zeri
		newAziendaInfo.setCodiceFiscale(utenteSare.getUserextraStrcodicefiscale() != null ? utenteSare
				.getUserextraStrcodicefiscale() : "00000000000");
		if (newAziendaInfo.getCodiceFiscale().length() > 16)
			newAziendaInfo.setCodiceFiscale(newAziendaInfo.getCodiceFiscale().substring(0, 15));

		// PROVINCIA DI RIFERIMENTO
		newAziendaInfo.setDeProvincia(tryToFindById(deProvinciaEJB, utenteSare.getUtenteProvincia(),
				utenteSare, true, "deProvincia"));

		// AUTORIZZAZIONE SARE (IN BASE A STRREQUESTSTATUS)
		String codAutorizzazioneSare = null;
		if ("0".equals(utenteSare.getUtenteStrrequeststatus()) || "1".equals(utenteSare.getUtenteStrrequeststatus())) {
			codAutorizzazioneSare = "0";
		} else if ("2".equals(utenteSare.getUtenteStrrequeststatus())
				|| "6".equals(utenteSare.getUtenteStrrequeststatus())) {
			codAutorizzazioneSare = "2";
		} else if ("3".equals(utenteSare.getUtenteStrrequeststatus())) {
			codAutorizzazioneSare = "3";
		} else if ("4".equals(utenteSare.getUtenteStrrequeststatus())
				|| "7".equals(utenteSare.getUtenteStrrequeststatus())) {
			codAutorizzazioneSare = "4";
		}
		newAziendaInfo.setDeAutorizzazioneSare(tryToFindById(deAutorizzazioneSareEJB, codAutorizzazioneSare,
				utenteSare, true, "deAutorizzazioneSare"));

		// ALTRI DATI AZIENDA
		newAziendaInfo.setPartitaIva(utenteSare.getUserextraStrpartitaiva());
		newAziendaInfo.setFlagValida(false);
		if (utenteSare.getMittentiCodiceMittente() != null && utenteSare.getMittentiCodiceMittente().length() > 14)
			utenteSare.setMittentiCodiceMittente(utenteSare.getMittentiCodiceMittente().substring(0, 15));
		newAziendaInfo.setMittenteSare(utenteSare.getMittentiCodiceMittente());
		newAziendaInfo.setDeTipoUtenteSare(tryToFindById(deTipoUtenteSareEJB, utenteSare.getUtenteTipoUtente(),
				utenteSare, true, "deTipoUtenteSare"));
		newAziendaInfo.setDeTipoAbilitato(tryToFindById(deTipoAbilitatoEJB, utenteSare.getUserextraStrtipoabilitato(),
				utenteSare, true, "deTipoAbilitato"));
		newAziendaInfo.setDeTipoDelegato(tryToFindById(deTipoDelegatoEJB, utenteSare.getUserextraStrtipodelegato(),
				utenteSare, false, "deTipoDelegato"));
		newAziendaInfo.setSwCreazioneCo(utenteSare.getUserextraStrsoftwareclient());

		// Dati referente
		newAziendaInfo.setReferenteSare(utenteSare.getUserextraStrreferentesare());
		newAziendaInfo.setEmailReferente(utenteSare.getUserextraStrreferenteemail());
		newAziendaInfo.setTelefonoReferente(utenteSare.getUserextraStrreferentetelefono());

		// NOME RICHIEDENTE
		if (utenteSare.getUserextraStrricnome() != null && !utenteSare.getUserextraStrricnome().isEmpty()) {
			if (utenteSare.getUserextraStrricnome().length() > 30) {
				newAziendaInfo.setNomeRic(utenteSare.getUserextraStrricnome().substring(0, 29));
				utenteSare.setWarnRegistrazione(utenteSare.getWarnRegistrazione() + " / Nome Richiedente troncato");
			} else
				newAziendaInfo.setNomeRic(utenteSare.getUserextraStrricnome());
		} else if (utenteSare.getUtenteCognNomRagUtente() != null && !utenteSare.getUtenteCognNomRagUtente().isEmpty()) {
			if (utenteSare.getUtenteCognNomRagUtente().length() > 30) {
				newAziendaInfo.setNomeRic(utenteSare.getUtenteCognNomRagUtente().substring(0, 29));
				utenteSare.setWarnRegistrazione(utenteSare.getWarnRegistrazione() + " / Nome Richiedente troncato");
			} else {
				newAziendaInfo.setNomeRic(utenteSare.getUtenteCognNomRagUtente());
			}
		} else {
			newAziendaInfo.setNomeRic(utenteSare.getUtenteCodFiscUtente());
			utenteSare.setWarnRegistrazione(utenteSare.getWarnRegistrazione()
					+ " / Nome Richiedente e CognnomeRagUtente NULLI");
		}

		// COGNOME RICHIEDENTE
		if (utenteSare.getUserextraStrriccognome() != null && !utenteSare.getUserextraStrriccognome().isEmpty()) {
			if (utenteSare.getUserextraStrriccognome().length() > 30) {
				newAziendaInfo.setCognomeRic(utenteSare.getUserextraStrriccognome().substring(0, 29));
				utenteSare.setWarnRegistrazione(utenteSare.getWarnRegistrazione() + " / Cognome Richiedente troncato");
			} else
				newAziendaInfo.setCognomeRic(utenteSare.getUserextraStrriccognome());
		} else if (utenteSare.getUtenteCognNomRagUtente() != null && !utenteSare.getUtenteCognNomRagUtente().isEmpty()) {
			if (utenteSare.getUtenteCognNomRagUtente().length() > 30) {
				newAziendaInfo.setCognomeRic(utenteSare.getUtenteCognNomRagUtente().substring(0, 29));
				utenteSare.setWarnRegistrazione(utenteSare.getWarnRegistrazione() + " / Cognome Richiedente troncato");
			} else
				newAziendaInfo.setCognomeRic(utenteSare.getUtenteCognNomRagUtente());
		} else {
			newAziendaInfo.setCognomeRic(utenteSare.getUtenteCodFiscUtente());
			utenteSare.setWarnRegistrazione(utenteSare.getWarnRegistrazione()
					+ " / Cognome Richiedente e CognnomeRagUtente NULLI");
		}

		// ALTRI DATI RICHIEDENTE
		newAziendaInfo.setEmailRic(utenteSare.getUtenteEmailUtente());
		newAziendaInfo.setIndirizzoRic(utenteSare.getUserextraStrricindirizzo() == null ? "-" : utenteSare
				.getUserextraStrricindirizzo());
		newAziendaInfo.setDeComuneRichiedente(tryToFindById(deComuneEJB, utenteSare.getUserextraStrriccomune(),
				utenteSare, false, "deComuneRichiedente"));
		newAziendaInfo.setDeComuneNascitaRic(tryToFindById(deComuneEJB, utenteSare.getUserextraStrricluogonascita(),
				utenteSare, false, "deComuneNascitaRic"));
		newAziendaInfo.setDtDataNascitaRic(tryToParseDate(utenteSare.getUserextraDtricdatanascita(), utenteSare, false,
				"dataNascitaRic"));
		newAziendaInfo.setCapRic(utenteSare.getUserextraStrriccap());

		// SEDE LEGALE
		newAziendaInfo.setIndirizzoSedeLegale(utenteSare.getUserextraStrsedeleindirizzo() == null ? "-" : utenteSare
				.getUserextraStrsedeleindirizzo());
		newAziendaInfo.setDeComuneSedeLegale(tryToFindById(deComuneEJB, utenteSare.getUserextraStrsedelecomune(),
				utenteSare, false, "comuneSedeLegale"));
		newAziendaInfo.setCapSedeLegale(utenteSare.getUserextraStrsedelecap());
		newAziendaInfo.setTelefonoSedeLegale("0"); // NON ESISTE SU SARE, USO 0
		newAziendaInfo.setFaxSedeLegale("0"); // NON ESISTE SU SARE, USO 0

		// SEDE OPERATIVA;
		newAziendaInfo.setDeComuneSede(tryToFindById(deComuneEJB, utenteSare.getUserextraStrsedeopcomune(), utenteSare,
				true, "comuneSede"));
		newAziendaInfo.setIndirizzoSede(utenteSare.getUserextraStrsedeopindirizzo() == null ? "-" : utenteSare
				.getUserextraStrsedeopindirizzo());
		newAziendaInfo.setFaxSede(utenteSare.getUserextraStrsedeopfax());
		newAziendaInfo.setTelefonoSede(utenteSare.getUserextraStrsedeoptelefono());
		newAziendaInfo.setEmailSede(utenteSare.getUserextraStrsedeopemail());
		newAziendaInfo.setCapSede(utenteSare.getUserextraStrsedeopcap() != null ? utenteSare.getUserextraStrsedeopcap()
				: "00000");

		// Se il comune della sede è nullo, ci metto quello di default in base alla provincia.
		if (newAziendaInfo.getDeComuneSede() == null) {
			String codProvincia = utenteSare.getUtenteProvincia();
			if (codProvincia != null && codProvincia.equals("07")) {
				// Provincia 007 -> comune A326 (Aosta)
				newAziendaInfo.setDeComuneSede(tryToFindById(deComuneEJB, "A326", utenteSare, false, "deComuneSede"));
			} else if (codProvincia != null && codProvincia.equals("054")) {
				// Provincia 054 -> comune G478 (Perugia)
				newAziendaInfo.setDeComuneSede(tryToFindById(deComuneEJB, "G478", utenteSare, false, "deComuneSede"));
			} else if (codProvincia != null && codProvincia.equals("055")) {
				// Provincia 055 -> comune L117 (Terni)
				newAziendaInfo.setDeComuneSede(tryToFindById(deComuneEJB, "L117", utenteSare, false, "deComuneSede"));
			} else if (codProvincia != null && codProvincia.equals("22")) {
				// Provincia 22 -> comune L117 (Trento)
				newAziendaInfo.setDeComuneSede(tryToFindById(deComuneEJB, "L378", utenteSare, false, "deComuneSede"));
			} else {
				utenteSare.setErroriRegistrazione(utenteSare.getErroriRegistrazione()
						+ " / codComuneSede è nullo E la provincia non è tra quelle previste");
			}
		}

		// SOGGETTO ABILITATO
		if (utenteSare.getUserextraStrtipoabilitato() != null && "04".equals(utenteSare.getUserextraStrtipoabilitato())) {
			newAziendaInfo.setDtIscrData(tryToParseDate(utenteSare.getUserextraDtiscrdata(), utenteSare, false,
					"dtIscrData"));
			newAziendaInfo.setIscrOrdine(utenteSare.getUserextraStriscrordine());
			newAziendaInfo.setDeComuneIscrizione(tryToFindById(deComuneEJB, utenteSare.getUserextraStriscrluogo(),
					utenteSare, false, "deComuneIscrizione"));

			if (utenteSare.getUserextraStriscrnumero() != null
					&& !utenteSare.getUserextraStriscrnumero().trim().equals("")) {
				if (utenteSare.getUserextraStriscrnumero().trim().length() > 30)
					newAziendaInfo.setIscrNumero(utenteSare.getUserextraStriscrnumero().trim().substring(0, 29));
				else
					newAziendaInfo.setIscrNumero(utenteSare.getUserextraStriscrnumero().trim());
			}
		}

		// CAMPI PER LE AGENZIE DI SOMMINISTRAZIONE (cod_tipo_abilitato = 03)
		// MA LI RIEMPIO IN OGNI CASO, CHE A VOLTE SONO VALORIZZATI
		if (utenteSare.getUserextraStriscrnumero() != null && utenteSare.getUserextraStriscrnumero().length() > 30) {
			newAziendaInfo.setIscrNumero(utenteSare.getUserextraStriscrnumero().substring(0, 29));
			utenteSare.setWarnRegistrazione(utenteSare.getWarnRegistrazione() + " / IscrNumero troncato");
		} else {
			newAziendaInfo.setIscrNumero(utenteSare.getUserextraStriscrnumero());
		}

		newAziendaInfo.setIscrProvvedNumero(utenteSare.getUserextraStriscrprovvednumero());
		newAziendaInfo.setDtIscrProvvedData(tryToParseDate(utenteSare.getUserextraDtiscrprovveddata(), utenteSare,
				false, "dtIscrProvvedData"));
		newAziendaInfo.setDtIscrData(tryToParseDate(utenteSare.getUserextraDtiscrdata(), utenteSare, false,
				"dtIscrData"));
		newAziendaInfo.setDeComuneIscrizione(tryToFindById(deComuneEJB, utenteSare.getUserextraStriscrluogo(),
				utenteSare, false, "deComuneIscrizione"));
		if (utenteSare.getUserextraFlgagenziaestera() != null
				&& !"null".equalsIgnoreCase(utenteSare.getUserextraFlgagenziaestera())) {
			newAziendaInfo.setFlagAgenziaEstera("Y".equalsIgnoreCase(utenteSare.getUserextraFlgagenziaestera()));
		}

		return newPfPrincipal;
	}

	/** Crea un nuovo utente di tipo Provincia **/
	private PfPrincipal createNewProvincia(SareImport sareRow) {
		PfPrincipal newPfPrincipal = new PfPrincipal();
		fillPfPrincipal(newPfPrincipal, sareRow);
		Provincia newProvincia = new Provincia();
		newPfPrincipal.setProvinciasForIdPfPrincipal(newProvincia);
		newProvincia.setPfPrincipal(newPfPrincipal);

		// CAMPI NELLA TABELLA PROVINCIA
		newProvincia.setDeProvincia(tryToFindById(deProvinciaEJB, sareRow.getUtenteProvincia().substring(1), sareRow,
				true, "deProvincia"));
		newProvincia.setFlagTematica(false);
		newProvincia.setFlagLavoro(false);
		newProvincia.setCodTipoUtenteSare(sareRow.getUtenteTipoUtente());
		newProvincia.setFlagTematica(false);
		newProvincia.setFlagLavoro(false);
		newProvincia.setDeCpi(tryToFindById(deCpiEJB, sareRow.getUtenteIdCpi(), sareRow, true, "deCPI"));

		// DE_CPI E CAMPI DEL POLO SARE (INDICATI IN ANALISI, IN BASE ALLA PROVINCIA)
		if ("054".equals(sareRow.getUtenteProvincia())) {
			// PERUGIA
			newProvincia.setCodiceFiscale("00443770540");
			if (newProvincia.getDeCpi() == null)
				newProvincia.setDeCpi(tryToFindById(deCpiEJB, "105800100", sareRow, true, "deCpi"));
			if (newPfPrincipal.getEmail() == null || newPfPrincipal.getEmail().isEmpty())
				newPfPrincipal.setEmail("info.sare@provincia.perugia.it");
		} else if ("055".equals(sareRow.getUtenteProvincia())) {
			// TERNI
			newProvincia.setCodiceFiscale("00179350558");
			if (newProvincia.getDeCpi() == null)
				newProvincia.setDeCpi(tryToFindById(deCpiEJB, "108000600", sareRow, true, "deCpi"));
			if (newPfPrincipal.getEmail() == null || newPfPrincipal.getEmail().isEmpty())
				newPfPrincipal.setEmail("provincia.terni@postacert.umbria.it");
		} else if ("07".equals(sareRow.getUtenteProvincia())) {
			// AOSTA (???)
			if (newProvincia.getDeCpi() == null)
				newProvincia.setDeCpi(tryToFindById(deCpiEJB, "020400100", sareRow, true, "deCpi"));
			if (newPfPrincipal.getEmail() == null || newPfPrincipal.getEmail().isEmpty())
				newPfPrincipal.setEmail("cpi-aosta@regione.vda.it");
			}

		// CAMPI CHE POTREBBERO ESSERE NULLI IN PF_PRINCIPAL PER GLI UTENTI PROVINCIA
		if (newPfPrincipal.getNome() == null || newPfPrincipal.getNome().isEmpty()) {
			newPfPrincipal.setNome(sareRow.getUtenteCognNomRagUtente());
		}

		if (newPfPrincipal.getCognome() == null || newPfPrincipal.getCognome().isEmpty()) {
			newPfPrincipal.setCognome(sareRow.getUtenteCognNomRagUtente());
		}

		if (newPfPrincipal.getDtFineValidita() != null && newPfPrincipal.getDtFineValidita().before(new Date())) {
			newPfPrincipal.setFlagAbilitato(false);
		}

		return newPfPrincipal;
	}

	/**
	 * da lanciare per gli utenti con TIPO UTENTE SARE M N T
	 * 
	 * @param azienda
	 * @return
	 */
	// public PfPrincipal mapAltriUtenteDTO(SareImport azienda, PfPrincipal in) {
	// if (azienda == null)
	// return null;
	//
	// // RegisterAziendaDTO ret = new RegisterAziendaDTO();
	// // contenitore warnings, per errori non gravi
	// // ret.setWarnRegistrazione("");
	// // ret.setErroriImportazione("");
	// SareImport rowId = azienda;
	//
	// in.setFlagAbilitato(true);
	// in.setConfirmationToken(null);
	//
	// in.setFlagAbilitato(true);
	// in.getAziendaInfo().setCapSede("0");
	// in.getAziendaInfo().setCodiceFiscale("00000000000");
	//
	// in.getAziendaInfo().setCognomeRic("");
	//
	// if (azienda.getUtenteCognNomRagUtente().length() > 30)
	// in.getAziendaInfo().setNomeRic(azienda.getUtenteCognNomRagUtente().substring(0, 29));
	// else
	// in.getAziendaInfo().setNomeRic(azienda.getUtenteCognNomRagUtente());
	//
	// try {
	// DeProvincia prov = deProvinciaEJB.findById(rowId.getUtenteProvincia().substring(1));
	// in.getAziendaInfo().setDeProvincia(prov);
	// } catch (MyCasException e) {
	// log.error("GRAVE! Errore durante la findById di provincia: " + e.getMessage());
	// }
	//
	// String codCom = "";
	// String provSare = rowId.getUtenteProvincia();
	//
	// if (("033").equalsIgnoreCase(provSare)) {
	// codCom = "G535";
	// } else if (("034").equalsIgnoreCase(provSare)) {
	// codCom = "G337";
	// } else if (("035").equalsIgnoreCase(provSare)) {
	// codCom = "H223";
	// } else if (("036").equalsIgnoreCase(provSare)) {
	// codCom = "F257";
	// } else if (("037").equalsIgnoreCase(provSare)) {
	// codCom = "A944";
	// } else if (("038").equalsIgnoreCase(provSare)) {
	// codCom = "D548";
	// } else if (("039").equalsIgnoreCase(provSare)) {
	// codCom = "H199";
	// } else if (("040").equalsIgnoreCase(provSare)) {
	// codCom = "D704";
	// // CESENA
	// // codCom = "C537";
	// } else if (("099").equalsIgnoreCase(provSare)) {
	// codCom = "H294";
	// } else if (("054").equalsIgnoreCase(provSare)) {
	// codCom = "G478";
	// } else if (("055").equalsIgnoreCase(provSare)) {
	// codCom = "L117";
	// }
	//
	// // in base alla provincia
	// try {
	// DeComune com = deComuneEJB.findById(codCom);
	// in.getAziendaInfo().setDeComuneNascitaRic(com);
	// } catch (MyCasException e) {
	// log.error("GRAVE! Errore durante la findById di comuneNascitaRic: " + e.getMessage());
	// }
	// in.setDomanda("Qual e' il tuo Codice fiscale?");
	// in.setEmail("-");
	// in.setRisposta(rowId.getUtenteCodFiscUtente());
	//
	// in.setIndirizzoUtente("-");
	// try {
	// in.getAziendaInfo().setDtDataNascitaRic(
	// new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2000-01-01 00:00:00"));
	// } catch (ParseException e) {
	// log.error("GRAVE! Errore durante la parse di dataNascitaRic: " + e.getMessage());
	// }
	//
	// if (rowId.getUtenteStrpassword() != null) {
	// in.setPassWord(rowId.getUtenteStrpassword().toLowerCase());
	// // ret.setPasswordConfirm(rowId.getUtenteStrpassword().toLowerCase());
	// } else {
	// in.setPassWord(SHA1.encrypt("1234"));
	// // ret.setPasswordConfirm(SHA1.encrypt("1234"));
	// azienda.setWarnRegistrazione(azienda.getWarnRegistrazione() + " / Password settata: 1234");
	// }
	//
	// in.getAziendaInfo().setRagioneSociale(rowId.getUtenteCodFiscUtente());
	// in.setRisposta(rowId.getUtenteCodFiscUtente());
	//
	// // SEDE LEGALE
	// // in base alla provincia
	// try {
	// DeComune comLeg = deComuneEJB.findById(codCom);
	// in.getAziendaInfo().setDeComuneSedeLegale(comLeg);
	// in.getAziendaInfo().setCapSedeLegale(comLeg.getCap());
	// } catch (MyCasException e) {
	// log.error("Grave! Errore durante la findById del comune legale: " + e.getMessage());
	// }
	// in.getAziendaInfo().setFaxSedeLegale("0");
	// in.getAziendaInfo().setIndirizzoSedeLegale("-");
	// in.getAziendaInfo().setTelefonoSedeLegale("0");
	//
	// // SEDE OPERATIVA
	// try {
	// DeComune comOp = deComuneEJB.findById(codCom);
	// in.getAziendaInfo().setDeComuneSede(comOp);
	// } catch (MyCasException e) {
	// log.error("Grave! Errore durante la findById del comune sede operativa: " + e.getMessage());
	// }
	// in.getAziendaInfo().setCapSede("0");// era cosi, confrontare con sopra
	// in.getAziendaInfo().setFaxSede("0");
	// in.getAziendaInfo().setIndirizzoSede("-");
	// in.getAziendaInfo().setTelefonoSede("0");
	//
	// // ret.setSoftwareUtilizzato("01");
	// // ret.setSoftwareSAREUtilizzato(SoftwareSAREUtilizzato.SARE_CLIENT_ONLINE);
	// in.setTelefonoUtente("0");
	// // in.set(rowId.getUserextraStrtipoabilitato());
	//
	// if (rowId.getUtenteCodFiscUtente() != null) {
	// // tronco la ragione sociale
	// if (rowId.getUtenteCodFiscUtente().length() > 16) {
	// in.getAziendaInfo().setCodiceFiscale(azienda.getWarnRegistrazione() + " /  TRONCATA");
	// // il duplicato ï¿½ gestito dopo
	// in.getAziendaInfo().setCodiceFiscale(rowId.getUtenteCodFiscUtente().substring(0, 16));
	// } else {
	// in.getAziendaInfo().setCodiceFiscale(rowId.getUtenteCodFiscUtente());
	// }
	// }
	//
	// in.setFlagAbilitato(true);
	//
	// Calendar now = Calendar.getInstance();
	// now.add(Calendar.MONTH, 6);
	// in.setDtScadenza(now.getTime());
	// /*
	// * try { Date dt2 = formatter.parse(rowId.getUtenteDtscadenzapwd()); ret.setDtScadenzaPassword(dt2); } catch
	// * (Exception e) { ret.setWarnRegistrazione(ret.getWarnRegistrazione() + " / errore parse UtenteDtscadenzapwd");
	// * // log.error("Errore parsing data " + e.getMessage()); Calendar now = Calendar.getInstance();
	// * now.add(Calendar.MONTH, 3); ret.setDtScadenzaPassword(now.getTime()); }
	// */
	//
	// // TODO capire dove vada questo
	// // in.set(rowId.getUtenteStrrequeststatus());
	// return in;
	// }

	/**
	 * Controllo se un utente azienda è da NON importare (perchè vecchio, scaduto, o cose del genere
	 **/
	private boolean daSaltare(PfPrincipal it, SareImport sareImport) {
		
		if (it.getEmail() == null) {
			sareImport.setErroriRegistrazione(sareImport.getErroriRegistrazione()
					+ " / Salto il Record, email NULLA tipout = azienda");
			return true;
		}
		
		if (it.getUsername() == null) {
			sareImport.setErroriRegistrazione(sareImport.getErroriRegistrazione()
					+ " / Salto il Record, getUsername NULLA tipout = azienda");
			return true;
		}
		
		final SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		if (it.getAziendaInfo() == null) {
			// Se non è un utente azienda, non c'è bisogno di fare controlli
			return false;
		}
		
		if (it.getAziendaInfo().getPartitaIva()!= null && it.getAziendaInfo().getPartitaIva().length() > 11)
		{
			sareImport.setErroriRegistrazione(sareImport.getErroriRegistrazione()
					+ " / Salto il Record, partita IVA più lunga di 11");
			return true;
		}

		if (it.getNome() == null) {
			sareImport.setErroriRegistrazione(sareImport.getErroriRegistrazione()
					+ " / Salto il Record, nome NULLO tipout = azienda");
			return true;
		}
		
		if (it.getCognome() == null) {
			sareImport.setErroriRegistrazione(sareImport.getErroriRegistrazione()
					+ " / Salto il Record, cognome NULLO tipout = azienda");
			return true;
		}
		
		if (it.getAziendaInfo().getDeAutorizzazioneSare() == null) {
			sareImport.setErroriRegistrazione(sareImport.getErroriRegistrazione()
					+ " / Salto il Record, deAutorizzazioneSare NULLA tipout = azienda");
			return true;
		}


		String tiporic = it.getAziendaInfo().getDeAutorizzazioneSare().getCodAutorizzazioneSare();
		String tipout = sareImport.getUtenteTipoUtente();
		if (tipout == null) {
			return false;
		}

		if (tipout.equals("R")) {
			if (sareImport.getUserextraStrragionesociale() == null) {
				sareImport.setErroriRegistrazione(sareImport.getErroriRegistrazione()
						+ " / Salto il Record, ragione sociale NULLA tipout = azienda");
				return true;
			}
		}

		if (tipout.equals("R") || tipout.equals("C")) {
			if (tipout.equals("R")) {
				if ("N".compareTo(sareImport.getUtenteFlagAbilitato()) == 0) {
					sareImport.setErroriRegistrazione(sareImport.getErroriRegistrazione()
							+ " / Salto il Record, flagAbilitato N con tipout = R");
					return true;
				}
			}
			if (tiporic == null) {
				if (tipout.compareTo("C") == 0 || tipout.compareTo("R") == 0) {
					// lascia andare
					sareImport.setErroriRegistrazione(sareImport.getErroriRegistrazione()
							+ " / Salto il Record, ReqStatus NULLO con tipout = C o R");
					return true;
				}
			}
			// TIPO richiesta
			if ("6".equals(tiporic) || "3".equals(tiporic) || "2".equals(tiporic)) {
				sareImport.setErroriRegistrazione(sareImport.getErroriRegistrazione()
						+ " / Salto il Record, request status = " + tiporic);
				return true;
			} else if ("0".compareTo(tiporic) == 0) {
				try {
					Date dtIscr = formatter.parse(sareImport.getUtenteDtinsertdate());
					Calendar montcheck = Calendar.getInstance();
					montcheck.setTime(dtIscr);
					Calendar now = Calendar.getInstance();
					now.add(Calendar.MONTH, -1);
					if (dtIscr.before(now.getTime())) {
						sareImport.setErroriRegistrazione(sareImport.getErroriRegistrazione()
								+ " / Salto il Record, dtIns piu vecchia di 30 giorni con Request status=0 ");
						return true;
					}
				} catch (Exception e) {
					log.error("Errore parsing FILTER date, should NOT happen, CHECK " + e.getMessage());
				}
			} else if ("1".equals(tiporic)) {
				try {
					Date dtIscr = formatter.parse(sareImport.getUtenteDtemailverification());
					Calendar montcheck = Calendar.getInstance();
					montcheck.setTime(dtIscr);
					Calendar now = Calendar.getInstance();
					now.add(Calendar.MONTH, -1);
					if (dtIscr.before(now.getTime())) {
						sareImport
								.setErroriRegistrazione(sareImport.getErroriRegistrazione()
										+ " / Salto il Record, Dtemailverification piu vecchia di 30 giorni con Request status=1 ");
						return true;
					}
				} catch (NullPointerException e) {
					sareImport.setErroriRegistrazione(sareImport.getErroriRegistrazione()
							+ " / Salto il Record, Dtemailverification nulla con Request status=1 ");

					return true;
				} catch (Exception e) {
					log.error("Errore parsing FILTER password date, should NOT happen, CHECK " + e.getMessage());
				}
			} else if ("7".equals(tiporic)) {
				try {
					Date dtIscr = formatter.parse(sareImport.getUtenteDtlastaccessdate());
					Calendar montcheck = Calendar.getInstance();
					montcheck.setTime(dtIscr);
					Calendar now = Calendar.getInstance();
					now.add(Calendar.MONTH, -1);
					if (dtIscr.before(now.getTime())) {
						sareImport
								.setErroriRegistrazione(sareImport.getErroriRegistrazione()
										+ " / Salto il Record, Dtlastaccessdate piu vecchia di 30 giorni con Request status=7 ");
						return true;
					}
				} catch (NullPointerException e) {
					sareImport.setErroriRegistrazione(sareImport.getErroriRegistrazione()
							+ " / Salto il Record, Dtlastaccessdate nulla con Request status=7 ");
					return true;// se non fa accesso da piu di un mese, scartalo
				} catch (Exception e) {
					log.error("Errore parsing FILTER password date, should NOT happen, CHECK " + e.getMessage());
				}
			}
		} else {
			// tipo utente SARE
			if (tipout.equals("A") || tipout.equals("B") || tipout.equals("O") || tipout.equals("X")
					|| tipout.equals("S") || tipout.equals("G")) {
				// vengono saltati anche M N e T, ma prima del invocazione di questo metodo
				sareImport.setErroriRegistrazione(sareImport.getErroriRegistrazione()
						+ " / Salto il Record, codtipoutenteSare= " + tipout);
				return true;
			}
		}

		return false;
	}

	@Override
	public String getFriendlyName() {
		return "Tabella di appoggio per porting utenti sare";
	}

	@Override
	public Class<SareImport> getEntityClass() {
		return SareImport.class;
	}

}
