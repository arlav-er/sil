package it.eng.myportal.beans.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import it.eng.myportal.dtos.ConferimentoDidDTO;
import it.eng.myportal.dtos.DeCittadinanzaDTO;
import it.eng.myportal.dtos.DeComuneDTO;
import it.eng.myportal.dtos.DeCpiDTO;
import it.eng.myportal.dtos.DeGenereDTO;
import it.eng.myportal.dtos.DeMotivoPermessoDTO;
import it.eng.myportal.dtos.DePosizioneProfessionaleMinDTO;
import it.eng.myportal.dtos.DeTitoloDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.entity.home.ConferimentoDidHome;
import it.eng.myportal.entity.home.DidHome;
import it.eng.myportal.entity.home.UtenteInfoHome;
import it.eng.myportal.entity.home.decodifiche.DeCittadinanzaHome;
import it.eng.myportal.entity.home.decodifiche.DeCittadinanzaMinHome;
import it.eng.myportal.entity.home.decodifiche.DeComuneHome;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;
import it.eng.myportal.entity.home.decodifiche.DePosizioneProfessionaleMinHome;
import it.eng.myportal.entity.home.decodifiche.DeTitoloHome;
import it.eng.myportal.youthGuarantee.bean.RisultatoInvioSap;
import it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.ConferimentoDIDEsitoEnum;
import it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.GestisciDID_Output;

/**
 * Backing Bean per una delle pagine della sezione per la nuova DID nazionale.
 * 
 * In questa pagina l'utente inserisce i dati necessari a fare la richiesta di una nuova DID.
 * 
 * @author gicozza
 */
@ManagedBean
@ViewScoped
public class DidNuovaFormBean extends AbstractServiceBaseBean {

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	DeComuneHome deComuneHome;

	@EJB
	DeCittadinanzaHome deCittadinanzaHome;

	@EJB
	DeCpiHome deCpiHome;

	@EJB
	DeTitoloHome deTitoloHome;

	@EJB
	DidHome didHome;

	@EJB
	ConferimentoDidHome conferimentoDidHome;

	@EJB
	DeCittadinanzaMinHome deCittadinanzaMinHome;

	@EJB
	DePosizioneProfessionaleMinHome dePosizioneProfessionaleMinHome;

	private final static ConferimentoDIDEsitoEnum ESITO_DID_CORRETTO = ConferimentoDIDEsitoEnum.E100;
	private final static String COD_POSIZ_PROFESSIONALE_NON_OCCUPATO = "NO";
	private final static String COD_CITTADINANZA_ITALIANA = "000";
	private final static String COD_CITTADINANZA_MIN_ITALIANA = "A02";
	private final static String COD_CITTADINANZA_MIN_EUROPEA = "A01";
	private final static String COD_CITTADINANZA_MIN_EXTRAUE = "A03";

	private UtenteCompletoDTO utenteCompleto;
	private ConferimentoDidDTO conferimentoDid;
	private DeCpiDTO cpiPromotore;
	private String resultMessage;
	private String codEntePromotoreSap;
	private DePosizioneProfessionaleMinDTO posProfessionaleNonOccupato;

	@PostConstruct
	public void postConstruct() {
		super.postConstruct();

		// Se mi è stato passato un identificativoSap nell'URL, lo prendo.
		if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.containsKey("codEntePromotoreSap")) {
			codEntePromotoreSap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
					.get("codEntePromotoreSap");
			cpiPromotore = deCpiHome.findDTOByCodMin(codEntePromotoreSap);
		}

		// Inizializzo i dati.
		utenteCompleto = utenteInfoHome.findDTOCompletoById(session.getPrincipalId());
		completaProfilo();
		conferimentoDid = new ConferimentoDidDTO();
		conferimentoDid.setIdPfPrincipal(session.getPrincipalId());
		posProfessionaleNonOccupato = dePosizioneProfessionaleMinHome.findDTOById(COD_POSIZ_PROFESSIONALE_NON_OCCUPATO);
	}

	/** Riempie eventuali buchi nei dati dell'utente con valori tampone */
	private void completaProfilo() {
		if (utenteCompleto.getCapDomicilio() == null && utenteCompleto.getComuneDomicilio() != null) {
			utenteCompleto.setCapDomicilio(utenteCompleto.getComuneDomicilio().getCap());
		}

		if (utenteCompleto.getCapResidenza() == null && utenteCompleto.getComuneResidenza() != null) {
			utenteCompleto.setCapResidenza(utenteCompleto.getComuneResidenza().getCap());
		}

		if (utenteCompleto.getComuneResidenza() == null) {
			utenteCompleto.setComuneResidenza(new DeComuneDTO());
		}
		if (utenteCompleto.getGenere() == null) {
			utenteCompleto.setGenere(new DeGenereDTO());
		}

		if (utenteCompleto.getMotivoPermesso() == null) {
			DeMotivoPermessoDTO deMotivoPermessoDTO = new DeMotivoPermessoDTO();
			utenteCompleto.setMotivoPermesso(deMotivoPermessoDTO);
		}

	}

	public List<DeComuneDTO> completeComuneValido(String par) {
		return deComuneHome.findValideBySuggestion(par);
	}

	public List<DeCittadinanzaDTO> completeCittadinanza(String par) {
		return deCittadinanzaHome.findBySuggestion(par);
	}

	public List<DeCpiDTO> getCpiPromotoreList() {
		return deCpiHome.findDTOValidiByProvincia(utenteCompleto.getUtenteInfo().getProvinciaRiferimento().getId());
	}

	public List<DeTitoloDTO> completeDeTitolo(String par) {
		return deTitoloHome.findConferimentoDidBySuggestion(par);
	}

	public void comuneDomicilioSelected() {
		if (utenteCompleto.getUtenteInfo().getComuneDomicilio() != null) {
			utenteCompleto.getUtenteInfo()
					.setCapDomicilio(utenteCompleto.getUtenteInfo().getComuneDomicilio().getCap());
		}
	}

	public void comuneResidenzaSelected() {
		if (utenteCompleto.getUtenteInfo().getComuneResidenza() != null) {
			utenteCompleto.getUtenteInfo()
					.setCapResidenza(utenteCompleto.getUtenteInfo().getComuneResidenza().getCap());
		}
	}

	/**
	 * Se l'utente dice che non ha mai avuto un lavoro, setto a 0 la durata della disoccupazione, e l'ultima posizione
	 * professionale a "non occupato".
	 */
	public void haMaiAvutoLavoroSelected() {
		if (conferimentoDid.getFlgEsperienzaLavoro() == false) {
			conferimentoDid.setDePosizioneProfessionaleMinDTO(posProfessionaleNonOccupato);
			conferimentoDid.setNumMesiRapporto(0);
		} else {
			conferimentoDid.setDePosizioneProfessionaleMinDTO(null);
		}
	}

	/**
	 * Se l'utente ha 1 componente della famiglia, non può avere figli o minori a carico.
	 */
	public void componentiFamigliaSelected() {
		if (conferimentoDid.getNumComponentiFamiglia() != null && conferimentoDid.getNumComponentiFamiglia() < 2) {
			conferimentoDid.setFlgFigliCarico(false);
			conferimentoDid.setFlgFigliMinoriCarico(false);
		}
	}

	/**
	 * Se l'utente non ha figli a carico, non può avere neanche minori a carico.
	 */
	public void figliCaricoSelected() {
		if (conferimentoDid.getFlgFigliCarico() == false) {
			conferimentoDid.setFlgFigliMinoriCarico(false);
		} else {
			conferimentoDid.setFlgFigliMinoriCarico(null);
		}
	}

	/** Non si possono indicare figli a carico, se il numero di componenti famiglia è minore di 2 */
	public boolean isFigliCaricoDisabled() {
		return conferimentoDid.getNumComponentiFamiglia() != null && conferimentoDid.getNumComponentiFamiglia() < 2;
	}

	/** Non si possono indicare minori a carico, se non si hanno figli a carico */
	public boolean isMinoriCaricoDisabled() {
		return isFigliCaricoDisabled()
				|| (conferimentoDid.getFlgFigliCarico() != null && !conferimentoDid.getFlgFigliCarico());
	}

	/**
	 * Fa l'effettiva richiesta della DID. Se necessario, prima fa anche un invio SAP al ministero.
	 */
	public void richiediDid() {
		// Se l'utente è cittadino UE, pulisco i campi relativi al permesso di soggiorno.
		if (utenteCompleto.getUtenteInfo().getCittadinanza().isCittadinoUE()) {
			utenteCompleto.setMotivoPermesso(null);
			//https://production.eng.it/jira/browse/ESL4SIL-985
			//utenteCompleto.setNumeroDocumento(null);
			//utenteCompleto.setDataScadenzaDocumento(null);
			utenteCompleto.setDocumentoSoggiorno(null);
		}

		// Come prima cosa, salvo sul DB eventuali dati del profilo inseriti dall'utente.
		try {
			utenteCompleto = utenteInfoHome.mergeDTOCompleto(utenteCompleto, session.getPrincipalId());
		} catch (Exception e) {
			log.error("Errore durante il salvataggio dei dati del profilo: " + e.toString());
			addCustomErrorMessage("Errore durante il salvataggio dei dati del profilo.");
		}

		// Calcolo il codice cittadinanza ministeriale dell'utente, in base alla cittadinanza indicata.
		if (utenteCompleto.getUtenteInfo().getCittadinanza() != null) {
			if (utenteCompleto.getUtenteInfo().getCittadinanza().getId().equals(COD_CITTADINANZA_ITALIANA)) {
				conferimentoDid.setDeCittadinanzaMinDTO(deCittadinanzaMinHome
						.findDTOById(COD_CITTADINANZA_MIN_ITALIANA));
			} else if (utenteCompleto.getUtenteInfo().getCittadinanza().isCittadinoUE()) {
				conferimentoDid
						.setDeCittadinanzaMinDTO(deCittadinanzaMinHome.findDTOById(COD_CITTADINANZA_MIN_EUROPEA));
			} else {
				conferimentoDid
						.setDeCittadinanzaMinDTO(deCittadinanzaMinHome.findDTOById(COD_CITTADINANZA_MIN_EXTRAUE));
			}
		}

		// Calcolo l'età del cittadino, e controllo che abbia più di 15 anni.
		Calendar now = Calendar.getInstance();
		Calendar nascita = Calendar.getInstance();
		nascita.setTime(utenteCompleto.getUtenteInfo().getDataNascita());
		int etaCittadino = now.get(Calendar.YEAR) - nascita.get(Calendar.YEAR);
		if (nascita.get(Calendar.MONTH) > now.get(Calendar.MONTH)
				|| (nascita.get(Calendar.MONTH) == now.get(Calendar.MONTH) && nascita.get(Calendar.DATE) > now
						.get(Calendar.DATE))) {
			etaCittadino--;
		}

		if (etaCittadino < 15) {
			addCustomErrorMessage("Il cittadino deve avere almeno 15 anni per richiedere una DID.");
			return;
		}

		// Se non ho già l'ente titolare SAP, faccio un invio SAP al ministero.
		if (codEntePromotoreSap == null || codEntePromotoreSap.trim().equals("")) {
			try {
				RisultatoInvioSap sapResult = didHome.inviaSap(utenteCompleto, new Date(), cpiPromotore.getCodCpiMin());
				if (!sapResult.isSuccess()) {
					log.error("L'invio della SAP non è andato a buon fine: " + sapResult.getMessaggioErrore());
					addCustomErrorMessage("Errore durante l'invio della SAP");
					return;
				}
			} catch (Exception e) {
				log.error("Errore non previsto durante l'invio della SAP: " + e.toString());
				addCustomErrorMessage("Errore durante l'invio della SAP.");
				return;
			}
		}

		// Chiamo il nuovo WS per richiedere la DID al ministero.
		try {
			GestisciDID_Output output = didHome.richiediDid(utenteCompleto, conferimentoDid,
					cpiPromotore.getCodCpiMin(), etaCittadino);
			if (output.getEsito() != null && output.getEsito().equals(ESITO_DID_CORRETTO.toString())) {
				fillConferimentoDidResults(output);
			} else {
				ConferimentoDIDEsitoEnum esitoEnum = ConferimentoDIDEsitoEnum.valueOf(output.getEsito());
				log.error("Esito di ConferimentoDID non positivo: " + esitoEnum.toString() + " - "
						+ esitoEnum.getDescrizione());
				addCustomErrorMessage("DID non stipulabile: " + esitoEnum.getDescrizione());
				return;
			}
		} catch (Exception e) {
			log.error("Errore non previsto durante la richiesta della DID: " + e.toString());
			addCustomErrorMessage("Errore non previsto durante la richiesta della DID");
			return;
		}

		// Salvo sul DB la riga di conferimento_did
		try {
			conferimentoDid = conferimentoDidHome.persistDTO(conferimentoDid, session.getPrincipalId());
		} catch (Exception e) {
			log.error("Errore non previsto durante il salvataggio di ConferimentoDid: " + e.toString());
			addCustomErrorMessage("Errore non previsto durante il salvataggio dei dati");
			return;
		}

		this.redirect("/faces/secure/utente/servizi/did_nuova/did_list.xhtml?idConferimentoDid="
				+ conferimentoDid.getId());
	}

	/** Aggiungo all'oggetto ConferimentoDid le informazioni ricevute in output dal ws */
	private void fillConferimentoDidResults(GestisciDID_Output output) {
		conferimentoDid.setOpzTipoEvento(output.getInformazioniDID().getTipoEvento().toString());
		conferimentoDid.setDataEvento(output.getInformazioniDID().getDataEvento().getTime());
		conferimentoDid.setCodEntePromotore(output.getInformazioniDID().getCodiceEntePromotore());
		conferimentoDid.setCodiceFiscale(output.getProfiling().getCodiceFiscale().getCodiceFiscaleClassicoValue());
		conferimentoDid.setDataDid(output.getInformazioniDID().getDataDID().getTime());
		conferimentoDid.setEta(output.getProfiling().getEta());
		conferimentoDid.setOpzGenere(output.getProfiling().getGenere().getValue());
		conferimentoDid.setIdProfiling(output.getProfiling().getIDSProfiling());
		conferimentoDid.setDataInserimento(output.getProfiling().getDataInserimento().getTime());
		conferimentoDid.setProbabilita(output.getProfiling().getProbabilita().doubleValue());
		conferimentoDid.setCondizioneOccupazCalc(output.getProfiling()
				.getCondizioneOccupazionaleAnnoPrecedenteCalcolata());
		conferimentoDid.setDurataDisoccupazCalc(output.getProfiling().getDurataDisoccupazioneCalcolata().toString());
	}

	public UtenteCompletoDTO getUtenteCompleto() {
		return utenteCompleto;
	}

	public void setUtenteCompleto(UtenteCompletoDTO utenteCompleto) {
		this.utenteCompleto = utenteCompleto;
	}

	public ConferimentoDidDTO getConferimentoDid() {
		return conferimentoDid;
	}

	public void setConferimentoDid(ConferimentoDidDTO conferimentoDid) {
		this.conferimentoDid = conferimentoDid;
	}

	public DeCpiDTO getCpiPromotore() {
		return cpiPromotore;
	}

	public void setCpiPromotore(DeCpiDTO cpiPromotore) {
		this.cpiPromotore = cpiPromotore;
	}

	public String getResultMessage() {
		return resultMessage;
	}

	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}

	public String getCodEntePromotoreSap() {
		return codEntePromotoreSap;
	}

	public void setCodEntePromotoreSap(String codEntePromotoreSap) {
		this.codEntePromotoreSap = codEntePromotoreSap;
	}

}
