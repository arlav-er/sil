package it.eng.myportal.beans.yg.over;

import it.eng.myportal.beans.AbstractBaseBean;
import it.eng.myportal.dtos.DeCittadinanzaDTO;
import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.DeCpiDTO;
import it.eng.myportal.dtos.DeGenereDTO;
import it.eng.myportal.dtos.DeMotivoPermessoDTO;
import it.eng.myportal.dtos.DeProvinciaDTO;
import it.eng.myportal.dtos.DeTitoloSoggiornoDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.dtos.YgGaranziaOverDTO;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.YgGaranziaOverHome;
import it.eng.myportal.entity.home.decodifiche.DeCittadinanzaHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;
import it.eng.myportal.entity.home.decodifiche.DeGenereHome;
import it.eng.myportal.entity.home.decodifiche.DeMotivoPermessoHome;
import it.eng.myportal.entity.home.decodifiche.DeProvinciaHome;
import it.eng.myportal.entity.home.decodifiche.DeTitoloSoggiornoHome;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.CfUtils;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.ws.garanziaover.WsCheckAmmissibilitaOverSilEJB;
import it.eng.sil.coop.webservices.requisitiAdesione.VerificaRequisitiGaranziaOverResponse;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.xml.datatype.DatatypeConfigurationException;

@ManagedBean
@ViewScoped
public class YgOverAdesioneBean extends AbstractBaseBean {

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	YgGaranziaOverHome ygGaranziaOverHome;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	DeProvinciaHome deProvinciaHome;

	@EJB
	DeCpiHome deCpiHome;

	@EJB
	DeCittadinanzaHome deCittadinanzaHome;

	@EJB
	DeTitoloSoggiornoHome deTitoloSoggiornoHome;

	@EJB
	DeMotivoPermessoHome deMotivoPermessoHome;

	@EJB
	DeGenereHome deGenereHome;

	@EJB
	WsCheckAmmissibilitaOverSilEJB wsCheckAmmissibilitaOverSilEJB;

	private YgGaranziaOverDTO adesione;
	private UtenteCompletoDTO utente;

	private Boolean newAdesioneAmmortizzatori = false;
	private DeCpiDTO newAdesioneCpi;

	private List<DeTitoloSoggiornoDTO> titoloSoggiornoList;
	private List<DeMotivoPermessoDTO> motiviPermessoList;
	private List<DeProvinciaDTO> provinciaRiferimentoList;
	private List<DeCpiDTO> cpiRiferimentoList;

	private static final String MSG_ADESIONE_SUCCESSO = "La tua iscrizione a Umbriattiva Adulti e' avvenuta in modo corretto. A seguito della tua adesione, sar� cura di  ARPAL Umbria effettuare ulteriori verifiche del possesso dei requisiti  necessari per beneficiare degli strumenti previsti da Umbriattiva. Ricordati di prendere un appuntamento col CpI.";
	private static final String MSG_ADESIONE_NON_DISPONIBILE = "Servizio temporaneamente non disponibile. Si invita a riprovare in un momento successivo.";
	private static final String MSG_ADESIONE_NO_REQUISITI = "Siamo spiacenti ma l'iscrizione al programma Umbriattiva Adulti non può avvenire per mancanza di requisiti. Ti invitiamo a recarti presso il CpI per verificare la tua posizione.";
	private static final String MSG_ADESIONE_ERRORE_14 = "Il lavoratore non risulta avere una DID valida da almeno 12 mesi.";
	private static final String MSG_ADESIONE_ERRORE_15 = "Il lavoratore non risulta avere una DID valida da almeno 6 mesi.";
	private static final String MSG_ADESIONE_ERRORE_16 = "Il lavoratore non risulta avere una DID valida da almeno 4 mesi.";

	/**
	 * Alla creazione del bean controllo se l'utente collegato ha già una richiesta di adesione e/o un appuntamento, e
	 * recupero dal DB le informazioni necessarie.
	 */
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		if (!session.isUtente()) {
			log.warn("Tentativo di accedere alla sezione Garanzia Giovani Over da utente non cittadino.");
			redirectHome();
			return;
		}

		// Recupero i dati utente e l'adesione.
		utente = utenteInfoHome.findDTOCompletoByUsername(session.getUsername());
		adesione = ygGaranziaOverHome.findByPfPrincipal(session.getPrincipalId());
		inizializzaUtenteCampiMancanti();

		// Inizializzo eventuali liste per selectOneMenu
		titoloSoggiornoList = deTitoloSoggiornoHome.findAllDTO();
		motiviPermessoList = deMotivoPermessoHome.findAllDTO();
		provinciaRiferimentoList = deProvinciaHome.findByRegione(ConstantsSingleton.COD_REGIONE.toString());
		aggiornaCpiRiferimentoList();
		newAdesioneCpi = new DeCpiDTO();
	}

	/**
	 * Questo metodo fa una redirect alla homepage se l'utente non può accedere alla sezione YG-Over 30.
	 */
	public void checkViewPage() {
		if (!session.isUtente()) {
			log.warn("Tentativo di accedere alla sezione Umbriattiva Adulti da utente non cittadino.");
			redirectHome();
			return;
		}

		// Questa sezione è abilitata solo per il portale Umbria.
		if (ConstantsSingleton.COD_REGIONE != ConstantsSingleton.COD_REGIONE_UMBRIA) {
			log.warn("Tentativo di accedere alla sezione Umbriattiva Adulti su un Portale non Umbria.");
			redirectHome();
			return;
		}

		// Se l'adesione è già stata approvata, rimando alla home.
		if (adesione != null && adesione.getDtAdesione() != null) {
			log.warn("Tentativo di accedere alla pagina di adesione a Umbriattiva Adulti avendo già un'adesione.");
			redirectHome();
			return;
		}
	}

	/**
	 * Provo ad inizializzare eventuali campi mancanti che potrebbero essere dedotti da informazioni che già ho
	 * sull'utente.
	 */
	private void inizializzaUtenteCampiMancanti() {
		// Se il genere è mancante, provo ad inizializzarlo basandomi sul codice fiscale.
		if (utente.getUtenteInfo().getGenere() == null) {
			if (utente.getUtenteInfo().getCodiceFiscale() != null) {
				utente.getUtenteInfo().setGenere(
						deGenereHome.findDTOById(CfUtils.getSesso(utente.getUtenteInfo().getCodiceFiscale())));
			} else {
				utente.getUtenteInfo().setGenere(new DeGenereDTO());
			}
		}

		// Se i CAP domicilio e/o residenza mancano, posso inizializzarli basandomi sui rispettivi comuni.
		if (utente.getUtenteInfo().getCapDomicilio() == null && utente.getUtenteInfo().getComuneDomicilio() != null) {
			utente.getUtenteInfo().setCapDomicilio(utente.getUtenteInfo().getComuneDomicilio().getCap());
		}

		if (utente.getUtenteInfo().getCapResidenza() == null && utente.getUtenteInfo().getComuneResidenza() != null) {
			utente.getUtenteInfo().setCapResidenza(utente.getUtenteInfo().getComuneResidenza().getCap());
		}

		// Se esiste già un'adesione, inizializzo il flag percettore ammortizzatori
		if (adesione != null) {
//			newAdesioneAmmortizzatori = adesione.getFlgPercettoreAmmortizzatori().equals("Y");
			newAdesioneCpi = adesione.getDeCpiAdesione();
		}
	}

	/**
	 * Restituisce TRUE se l'utente ha fatto almeno un tentativo di adesione a Garanzia Over.
	 */
	public boolean isAdesionePresente() {
		return (adesione != null);
	}

	/**
	 * Restituisce TRUE se l'utente ha aderito a Garanzia Over con successo (ed è quindi stato preso in carico dal CPI).
	 */
	public boolean isAdesioneValida() {
		return (isAdesionePresente() && adesione.getDtAdesione() != null);
	}

	/**
	 * Restituisce TRUE se l'utente ha aderito a Garanzia Over ed ha preso un appuntamento presso il CPI.
	 */
	public boolean isAdesionePresaInCarico() {
		return isAdesioneValida() && adesione.getFlgPresoInCarico();
	}

	/**
	 * Autocomplete per il comune di nascita.
	 */
	public List<DeComuneDTO> completeComuneNascita(String par) {
		return deComuneHome.findValideBySuggestion(par);
	}

	/**
	 * Autocomplete per il comune di domicilio.
	 */
	public List<DeComuneDTO> completeComuneDomicilio(String par) {
		return deComuneHome.findValideBySuggestion(par);
	}

	/**
	 * Autocomplete per la cittadinanza.
	 */
	public List<DeCittadinanzaDTO> completeCittadinanza(String par) {
		return deCittadinanzaHome.findBySuggestion(par);
	}

	/**
	 * Aggiorna la lista dei CPI disponibili in base alla provincia di riferimento.
	 */
	public void aggiornaCpiRiferimentoList() {
		if (utente.getUtenteInfo().getProvinciaRiferimento() != null) {
			cpiRiferimentoList = deCpiHome.findDTOValidiByProvincia(utente.getUtenteInfo().getProvinciaRiferimento()
					.getId());
		}
	}

	/**
	 * Copia i dati del domicilio in residenza.
	 */
	public void copiaDatiDomicilio() {
		utente.getUtenteInfo().setComuneResidenza(utente.getUtenteInfo().getComuneDomicilio());
		utente.getUtenteInfo().setCapResidenza(utente.getUtenteInfo().getCapDomicilio());

		// Per qualche ragione, l'indirizzo di domicilio può andare fino a 512 caratteri e quello di residenza solo 100.
		String indirizzoDomicilio = utente.getUtenteInfo().getIndirizzoDomicilio();
		if (indirizzoDomicilio != null && indirizzoDomicilio.length() > 100)
			indirizzoDomicilio = indirizzoDomicilio.substring(0, 100);
		utente.getUtenteInfo().setIndirizzoResidenza(indirizzoDomicilio);
	}

	/**
	 * Restituisce la data che avrà la nuova adesione a Garanzia Over (ovvero la data attuale).
	 */
	public Date getDataNewAdesione() {
		return new Date();
	}

	/**
	 * Restituisce TRUE se l'utente ha selezionato una cittadinanza che non fa parte dell'Unione Europea.
	 */
	public boolean isUtenteExtraUe() {
		if (utente.getCittadinanza().getId() == null)
			return false;

		if (utente.getCittadinanza().getFlgCee() != null && "S".equalsIgnoreCase(utente.getCittadinanza().getFlgCee())) {
			return false;
		}
		return true;
	}

	/**
	 * Restituisce TRUE se l'utente ha selezionato un comune di domicilio fuori dalla regione del Portale.
	 */
	public boolean isDomicilioFuoriRegione() {
		if (utente.getUtenteInfo().getComuneDomicilio() == null)
			return false;

		DeProvinciaDTO provinciaDomicilio = deProvinciaHome.findDTOById(utente.getUtenteInfo().getComuneDomicilio()
				.getIdProvincia());
		if (provinciaDomicilio.getIdRegione().equalsIgnoreCase(ConstantsSingleton.COD_REGIONE.toString())) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Metodo chiamato per confermare la richiesta di adesione a Garanzia Over una volta riempito il form. Come prima
	 * cosa, salva eventuali dati inseriti o cambiati nel profilo dell'utente. Poi crea un oggetto YgGaranziaOver per
	 * memorizzare il tentativo di adesione. Infine chiama il WS del sil per effettuare l'adesione vera e propria, e
	 * salva il risultato nell'oggetto YgGaranziaOver appena creato.
	 *
	 */
	public String creaAdesione() {
		try {
			// Salvo eventuali campi del profilo utente inseriti/modificati tramite il form.
			utenteInfoHome.mergeDTOCompletoWithNewTransaction(utente, session.getPrincipalId());
	
			// Se il domicilio non è fuori regione, setto il CPI di riferimento a quello del comune di domicilio.
			if (!isDomicilioFuoriRegione()) {
				newAdesioneCpi = deCpiHome.findDTOByCodComune(utente.getUtenteInfo().getComuneDomicilio().getId());
			}
	
			// Se non ho mai effettuato un tentativo di adesione, creo un nuovo oggetto.
			if (adesione == null) {
				try {
					adesione = ygGaranziaOverHome.createNewAdesione(utente, newAdesioneAmmortizzatori,
							newAdesioneCpi.getId(), session.getPrincipalId());
				} catch (MyPortalException e) {
					// Dato che non ho fatto richiesta di adesione, rimango alla stessa pagina.
					addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Richiesta già effettuata:",
							"Esiste già una richiesta di adesione a Umbriattiva Adulti per questo codice fiscale."));
					return "";
				}
			} else {
				// Se esiste già un tentativo di adesione, aggiorno il flag degli ammortizzatori ed il codice fiscale.
				adesione.setFlgPercettoreAmmortizzatori(newAdesioneAmmortizzatori ? "Y" : "N");
				adesione.setCodiceFiscale(utente.getCodiceFiscale());
				ygGaranziaOverHome.mergeDTO(adesione, session.getPrincipalId());
			}
	
			// Se esiste già un'adesione con lo stesso codice fiscale, interrompo tutto.
			if (ygGaranziaOverHome.isAdesioneGiaPresente(utente.getCodiceFiscale())) {
				addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Richiesta già effettuata:",
						"Esiste già una richiesta di adesione a Umbriattiva Adulti per questo codice fiscale."));
				return "";
			}
	
			// Chiamo il WS del SIL per effettuare l'effettiva adesione.
			VerificaRequisitiGaranziaOverResponse risultatoWS = new VerificaRequisitiGaranziaOverResponse();
			try {
				risultatoWS = wsCheckAmmissibilitaOverSilEJB.checkAmmissibilita(adesione, utente.getUtenteInfo()
						.getCodiceFiscale(), new Date(), newAdesioneAmmortizzatori, utente.getUtenteInfo()
						.getProvinciaRiferimento().getId());
			} catch (DatatypeConfigurationException e) {
				log.error("Errore durante la chiamata ws a verificaRequisitiGaranziaOver: " + e);
				risultatoWS.setCodice(Byte.parseByte("99"));
				risultatoWS.setDescrizione("Errore durante la chiamata al webservice");
			}
	
			// Aggiorno l'adesione in base al risultato del WS.
			adesione = ygGaranziaOverHome.findByPfPrincipal(session.getPrincipalId());
			if (risultatoWS.getCodice() == 0) {
				// Se il WS ha restituito OK, valorizzo dtAdesione per mostrare che l'adesione è
				// effettivamente avvenuta.
				adesione.setDtAdesione(new Date());
				adesione.setStrMessWsAdesione(risultatoWS.getDescrizione());
				adesione.setStrMessInterfaccia(MSG_ADESIONE_SUCCESSO);
				ygGaranziaOverHome.mergeDTO(adesione, session.getPrincipalId());
	
				// Dato che l'adesione è andata a buon fine, mando alla pagina di prenotazione appuntamento.
				return "appuntamento?faces-redirect=true";
			} else {
				// Se il WS ha restituito errore, salvo il messaggio di errore.
				// Se l'errore è una mancanza di requisti, salvo una versione più breve del messaggio.
				if (risultatoWS.getCodice() == 14) {
					adesione.setStrMessWsAdesione(MSG_ADESIONE_ERRORE_14);
				} else if (risultatoWS.getCodice() == 15) {
					adesione.setStrMessWsAdesione(MSG_ADESIONE_ERRORE_15);
				} else if (risultatoWS.getCodice() == 16) {
					adesione.setStrMessWsAdesione(MSG_ADESIONE_ERRORE_16);
				} else {
					adesione.setStrMessWsAdesione(risultatoWS.getDescrizione());
				}
	
				// Salvo il messaggio che spiega il tipo di errore (servizio non disponibile o mancanza di requisiti)
				if (risultatoWS.getCodice() == 1 || risultatoWS.getCodice() == 2 || risultatoWS.getCodice() == 98) {
					adesione.setStrMessInterfaccia(MSG_ADESIONE_NON_DISPONIBILE);
				} else {
					adesione.setStrMessInterfaccia(MSG_ADESIONE_NO_REQUISITI);
				}
	
				ygGaranziaOverHome.mergeDTO(adesione, session.getPrincipalId());
	
				// Dato che l'adesione è fallita, rimando all'informativa.
				return "informativa?faces-redirect=true";
			}
		}catch(Exception e) {
			addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Errore: ",
					"Errore interno del sistema. Servizio non disponibile."));
			return "";
		
		}

	}

	public UtenteCompletoDTO getUtente() {
		return utente;
	}

	public void setUtente(UtenteCompletoDTO utente) {
		this.utente = utente;
	}

	public YgGaranziaOverDTO getAdesione() {
		return adesione;
	}

	public void setAdesione(YgGaranziaOverDTO adesione) {
		this.adesione = adesione;
	}

	public Boolean getNewAdesioneAmmortizzatori() {
		return newAdesioneAmmortizzatori;
	}

	public void setNewAdesioneAmmortizzatori(Boolean newAdesioneAmmortizzatori) {
		this.newAdesioneAmmortizzatori = newAdesioneAmmortizzatori;
	}

	public List<DeTitoloSoggiornoDTO> getTitoloSoggiornoList() {
		return titoloSoggiornoList;
	}

	public void setTitoloSoggiornoList(List<DeTitoloSoggiornoDTO> titoloSoggiornoList) {
		this.titoloSoggiornoList = titoloSoggiornoList;
	}

	public List<DeMotivoPermessoDTO> getMotiviPermessoList() {
		return motiviPermessoList;
	}

	public void setMotiviPermessoList(List<DeMotivoPermessoDTO> motiviPermessoList) {
		this.motiviPermessoList = motiviPermessoList;
	}

	public List<DeProvinciaDTO> getProvinciaRiferimentoList() {
		return provinciaRiferimentoList;
	}

	public void setProvinciaRiferimentoList(List<DeProvinciaDTO> provinciaRiferimentoList) {
		this.provinciaRiferimentoList = provinciaRiferimentoList;
	}

	public List<DeCpiDTO> getCpiRiferimentoList() {
		return cpiRiferimentoList;
	}

	public void setCpiRiferimentoList(List<DeCpiDTO> cpiRiferimentoList) {
		this.cpiRiferimentoList = cpiRiferimentoList;
	}

	public DeCpiDTO getNewAdesioneCpi() {
		return newAdesioneCpi;
	}

	public void setNewAdesioneCpi(DeCpiDTO newAdesioneCpi) {
		this.newAdesioneCpi = newAdesioneCpi;
	}
}
