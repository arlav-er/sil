package it.eng.myportal.entity.home;

import it.eng.myportal.dtos.ConferimentoDidDTO;
import it.eng.myportal.dtos.DeRegioneDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.entity.ejb.youthGuarantee.YouthGuaranteeSapEjb;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;
import it.eng.myportal.entity.home.decodifiche.DeRegioneHome;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;
import it.eng.myportal.youthGuarantee.bean.RisultatoInvioSap;
import it.eng.myportal.youthGuarantee.richiestaSAP.IDSAP;
import it.eng.myportal.youthGuarantee.sap.Allegato;
import it.eng.myportal.youthGuarantee.sap.Datiamministrativi;
import it.eng.myportal.youthGuarantee.sap.Datianagrafici;
import it.eng.myportal.youthGuarantee.sap.Datiinvio;
import it.eng.myportal.youthGuarantee.sap.Datipersonali;
import it.eng.myportal.youthGuarantee.sap.Datistranieri;
import it.eng.myportal.youthGuarantee.sap.Domicilio;
import it.eng.myportal.youthGuarantee.sap.EsperienzelavoroLst;
import it.eng.myportal.youthGuarantee.sap.LavoratoreType;
import it.eng.myportal.youthGuarantee.sap.LivelliistruzioneLst;
import it.eng.myportal.youthGuarantee.sap.Recapiti;
import it.eng.myportal.youthGuarantee.sap.Residenza;
import it.eng.myportal.youthGuarantee.verificaSAP.VerificaSAP;
import it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.Applicazione;
import it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.CodiceFiscale;
import it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.Genere;
import it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.GestisciDID_Input;
import it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.GestisciDID_Output;
import it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.InformazioniDID;
import it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.TipoEvento;
import it.gov.anpal.DataModels.InformationDelivery.ConferimentoDID._1_0.VariabiliDiProfiling;
import it.gov.anpal.Services.InformationDelivery.ConferimentoDID._1_0.ConferimentoDID_PortTypeProxy;

import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Questo EJB contiene sia i metodi (che d'ora in poi saranno legacy) per la vecchia DID effettuata tramite il SIL, sia
 * i metodi per le chiamate ai WS ministeriali per la nuova DID nazionale.
 * 
 * @author gicozza
 */
@Stateless
public class DidHome {
	protected final Log log = LogFactory.getLog(this.getClass());

	@EJB
	WsEndpointHome wsEndpointHome;

	@EJB
	YouthGuaranteeSapEjb youthGuaranteeSapEjb;

	@EJB
	DeCpiHome deCpiHome;

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	DeRegioneHome deRegioneHome;

	/** VECCHIO: Genera la stampa PDF per la DID di un utente */
	@Deprecated
	public InputStream createStampaDidPdfFile(Integer idPfPrincipal) {
		InputStream result = null;
		String promemoriaHtmlFile = getStampaDidHtmlFile(idPfPrincipal);
		result = Utils.PDF.htmlToPDF(promemoriaHtmlFile, "did/modulo_did.xhtml");
		return result;
	}

	/** VECCHIO: Genera la stampa PDF per la DID di un utente */
	@Deprecated
	private String getStampaDidHtmlFile(Integer idPfPrincipal) {
		String result = null;
		WebClient webClient = new WebClient();
		webClient.getOptions().setJavaScriptEnabled(false);
		HtmlPage requestedPage = null;

		try {
			requestedPage = webClient.getPage(ConstantsSingleton.BASE_URL
					+ "/faces/public/print/did/modulo_did.xhtml" + "?idPfPrincipal=" + idPfPrincipal);
		} catch (Exception e) {
			String errMsg = "Errore durante il recupero del documento XML: did/modulo_did.xhtml. idPfPrincipal = "
					+ idPfPrincipal;
			log.error(errMsg);
			log.error("Eccezione originale: " + e.getClass() + " - " + e.getLocalizedMessage());
			throw new MyPortalException(errMsg, e, true);
		}
		if (requestedPage != null) {
			result = requestedPage.asXml();
		}
		return result;
	}

	/**
	 * Chiama il WS del ministero per verificare l'esistenza di una SAP per un certo cittadino.
	 */
	public String verificaEsistenzaSap(String codiceFiscale) throws JAXBException, SAXException, RemoteException {
		VerificaSAP verificaSap = new VerificaSAP();
		verificaSap.setCodiceFiscale(codiceFiscale);
		String inputXml = youthGuaranteeSapEjb.convertVerificaSapToString(verificaSap);
		String identificativoSap = youthGuaranteeSapEjb.verificaEsistenzaSap(inputXml);

		return identificativoSap;
	}

	/**
	 * Chiama il WS del ministero per scaricare una SAP già esistente.
	 */
	public LavoratoreType richiestaSap(String identificativoSap) throws JAXBException, SAXException, RemoteException {
		IDSAP idSap = new IDSAP();
		idSap.setIdentificativoSap(identificativoSap);
		String inputXml = youthGuaranteeSapEjb.convertRichiestaSapToString(idSap);
		LavoratoreType lavoratoreType = youthGuaranteeSapEjb.richiestaSap(inputXml);

		return lavoratoreType;
	}

	/**
	 * Chiama il WS del ministero per effettuare una SAP per un certo cittadino.
	 */
	public RisultatoInvioSap inviaSap(UtenteCompletoDTO utenteCompleto, Date dataAdesione, String codCpiAdesione)
			throws Exception {
		LavoratoreType lavoratoreSap = createLavoratorePerInvioSap(utenteCompleto, dataAdesione, codCpiAdesione);
		String inputXml = youthGuaranteeSapEjb.convertSapToString(lavoratoreSap);
		RisultatoInvioSap risultatoInvio = youthGuaranteeSapEjb.inviaSap(inputXml);
		return risultatoInvio;
	}

	/**
	 * Chiama il WS del ministero per richiedere una DID per un certo cittadino.
	 * 
	 * @return
	 */
	public GestisciDID_Output richiediDid(UtenteCompletoDTO utente, ConferimentoDidDTO did, String codCpiPromotore,
			Integer etaCittadino) throws RemoteException {
		// Trovo il GUID (ovvero cod_min della regione), e il cod_min della provincia di residenza.
		DeRegioneDTO regioneGUID = deRegioneHome.findDTOById(ConstantsSingleton.COD_REGIONE.toString());
		String codProvinciaResidenza = utente.getUtenteInfo().getComuneResidenza().getIdProvincia();
		while (codProvinciaResidenza.length() < 3) {
			codProvinciaResidenza = "0" + codProvinciaResidenza;
		}

		// Configuro la chiamata al servizio
		String conferimentoDidAddress = wsEndpointHome.getConferimentoDidAddress();
		ConferimentoDID_PortTypeProxy proxyDidMin = new ConferimentoDID_PortTypeProxy(conferimentoDidAddress);
		GestisciDID_Input input = new GestisciDID_Input();
		input.setApplicazione(Applicazione.NCN);
		input.setCodiceFiscaleOperatore(null);
		input.setGUIDOperatore(regioneGUID.getCodMin());

		// Dati dell'utente e della richiesta
		VariabiliDiProfiling var = new VariabiliDiProfiling();
		var.setAttualmenteIscrittoScuolaUniversitaOCorsoFormazione(did.getDeIscrizioneCorsoMinDTO().getId());
		var.setCittadinanza(did.getDeCittadinanzaMinDTO().getId());
		var.setDaQuantiMesiConclusoUltimoRappLavoro(did.getNumMesiRapporto());
		var.setDaQuantiMesiStaCercandoLavoro(did.getNumMesiRicercaLavoro());
		var.setDurataPresenzaInItalia(did.getDePresenzaItaliaMinDTO().getId());
		var.setEta(etaCittadino);
		var.setGenere(utente.getUtenteInfo().getGenere().getId().equals("M") ? Genere.M : Genere.F);
		var.setHaMaiAvutoUnLavoro(did.getFlgEsperienzaLavoro());
		var.setNumeroComponentiFamiglia(did.getNumComponentiFamiglia());
		var.setPosizioneProfessioneUltimaOccupazione(did.getDePosizioneProfessionaleMinDTO().getId());
		var.setPresenzaFigliACarico(did.getFlgFigliCarico());
		var.setPresenzaFigliACaricoMeno18Anni(did.getFlgFigliMinoriCarico());
		var.setProvinciaDiResidenza(codProvinciaResidenza);
		var.setTitoloDiStudio(did.getDeTitoloDTO().getId());
		var.setCondizioneOccupazionaleUnAnnoPrima(did.getDeCondizioneOccupazMinDTO().getId());
		input.setVariabiliDiProfiling(var);

		// Dati sulla chiamata al servizio
		InformazioniDID info = new InformazioniDID();
		info.setCodiceEntePromotore(codCpiPromotore);
		info.setCodiceFiscale(new CodiceFiscale(utente.getUtenteInfo().getCodiceFiscale()));
		info.setDataDID(Calendar.getInstance());
		info.setDataEvento(Calendar.getInstance());
		info.setTipoEvento(TipoEvento.I); // I : Inserimento
		input.setInformazioniDID(info);

		GestisciDID_Output output = proxyDidMin.gestisciDID(input);
		return output;
	}

	/**
	 * Crea l'input di una richiesta di invio SAP, a partire dai dati dell'utente.
	 */
	private LavoratoreType createLavoratorePerInvioSap(UtenteCompletoDTO utenteCompleto, Date dataAdesione,
			String codCpiAdesione) throws Exception {
		LavoratoreType lavoratore = new LavoratoreType();

		// Dati invio
		Datiinvio datiinvio = new Datiinvio();
		datiinvio.setDataultimoagg(Utils.dateToGregorianDate(dataAdesione));
		datiinvio.setCodiceentetit(codCpiAdesione);
		datiinvio.setTipovariazione("01");
		datiinvio.setDatadinascita(Utils.dateToGregorianDate(utenteCompleto.getDataNascita()));
		lavoratore.setDatiinvio(datiinvio);

		// Dati anagrafici
		Datianagrafici datianagrafici = new Datianagrafici();
		lavoratore.setDatianagrafici(datianagrafici);

		// Dati anagrafici -> personali
		Datipersonali datipersonali = new Datipersonali();
		datipersonali.setCodicefiscale(utenteCompleto.getCodiceFiscale());
		datipersonali.setCognome(utenteCompleto.getCognome());
		datipersonali.setNome(utenteCompleto.getNome());
		datipersonali.setSesso(utenteCompleto.getGenere().getId());
		datipersonali.setDatanascita(Utils.dateToGregorianDate(utenteCompleto.getDataNascita()));
		datipersonali.setCodcomune(utenteCompleto.getComuneNascita().getId());
		datipersonali.setCodcittadinanza(utenteCompleto.getCittadinanza().getId());
		datianagrafici.setDatipersonali(datipersonali);

		// Dati anagrafici -> stranieri
		Datistranieri datistranieri = new Datistranieri();
		if (!utenteCompleto.getCittadinanza().isCittadinoUE()) {
			if (utenteCompleto.getDocumentoSoggiorno() != null) {
				datistranieri.setCodtipodocumento(utenteCompleto.getDocumentoSoggiorno().getId());
			}
			datistranieri.setNumero(utenteCompleto.getNumeroDocumento());
			if (utenteCompleto.getMotivoPermesso() != null) {
				datistranieri.setMotivo(utenteCompleto.getMotivoPermesso().getId());
			}
			datistranieri.setValidoal(Utils.dateToGregorianDate(utenteCompleto.getDataScadenzaDocumento()));
		}
		datianagrafici.setDatistranieri(datistranieri);

		// Dati anagrafici -> Domicilio
		Domicilio domicilio = new Domicilio();
		if (utenteCompleto.getComuneDomicilio() != null) {
			domicilio.setCodcomune(utenteCompleto.getComuneDomicilio().getId());
		}
		domicilio.setCap(utenteCompleto.getCapDomicilio());

		String indirizzoDomicilio = utenteCompleto.getIndirizzoDomicilio();
		if (indirizzoDomicilio != null && indirizzoDomicilio.length() > 100) {
			// troncamento indirizzo domicilio per mini-sap, max 100 caratteri
			indirizzoDomicilio = indirizzoDomicilio.substring(0, 100);
		}
		domicilio.setIndirizzo(indirizzoDomicilio);
		datianagrafici.setDomicilio(domicilio);

		// Dati anagrafici -> Residenza
		Residenza residenza = new Residenza();
		if (utenteCompleto.getComuneResidenza() != null) {
			residenza.setCodcomune(utenteCompleto.getComuneResidenza().getId());
		}
		residenza.setCap(utenteCompleto.getCapResidenza());

		String indirizzoResidenza = utenteCompleto.getIndirizzoResidenza();
		if (indirizzoResidenza != null && indirizzoResidenza.length() > 100) {
			// troncamento indirizzo residenza per mini-sap, max 100 caratteri
			indirizzoResidenza = indirizzoResidenza.substring(0, 100);
		}
		residenza.setIndirizzo(indirizzoResidenza);
		datianagrafici.setResidenza(residenza);

		// Dati anagrafici -> Recapiti
		Recapiti recapiti = new Recapiti();

		String numeroTelefono = utenteCompleto.getTelCasa();
		if (numeroTelefono != null && numeroTelefono.length() > 15) {
			// troncamento per mini-sap, max 15 caratteri
			numeroTelefono = numeroTelefono.substring(0, 15);
		}
		recapiti.setTelefono(numeroTelefono);

		String cellulare = utenteCompleto.getCellulare();
		if (cellulare != null && cellulare.length() > 15) {
			// troncamento per mini-sap, max 15 caratteri
			cellulare = cellulare.substring(0, 15);
		}
		recapiti.setCellulare(cellulare);

		recapiti.setEmail(utenteCompleto.getEmail());

		// controlla la presenza di almeno uno tra i campi dei recapiti
		if (utenteCompleto.getTelCasa() == null && utenteCompleto.getCellulare() == null
				&& utenteCompleto.getEmail() == null) {
			throw new Exception("Nessun recapito indicato (telefono, email, cellulare)");
		}

		datianagrafici.setRecapiti(recapiti);

		// Altri campi necessari da xsd
		Datiamministrativi datiamministrativi = new Datiamministrativi();
		lavoratore.setDatiamministrativi(datiamministrativi);

		EsperienzelavoroLst esperienzelavoroLst = new EsperienzelavoroLst();
		lavoratore.setEsperienzelavoroLst(esperienzelavoroLst);

		LivelliistruzioneLst livelliistruzioneLst = new LivelliistruzioneLst();
		lavoratore.setLivelliistruzioneLst(livelliistruzioneLst);

		Allegato allegato = new Allegato();
		lavoratore.setAllegato(allegato);

		return lavoratore;
	}
}
