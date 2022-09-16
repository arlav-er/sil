package it.eng.myportal.entity.home;

import java.rmi.RemoteException;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import it.eng.myportal.dtos.DeCpiDTO;
import it.eng.myportal.dtos.UtenteCompletoDTO;
import it.eng.myportal.dtos.YgAdesioneDTO;
import it.eng.myportal.entity.UtenteInfo;
import it.eng.myportal.entity.decodifiche.DeCpi;
import it.eng.myportal.entity.ejb.youthGuarantee.YouthGuaranteeSapEjb;
import it.eng.myportal.entity.home.decodifiche.DeCpiHome;
import it.eng.myportal.exception.MyPortalException;
import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.Utils;
import it.eng.myportal.utils.yg.YgConstants;
import it.eng.myportal.utils.yg.YgDebugConstants;
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

@Stateless
public class YgSapHome {

	protected final Log log = LogFactory.getLog(this.getClass());

	@EJB
	UtenteInfoHome utenteInfoHome;

	@EJB
	YouthGuaranteeSapEjb youthGuaranteeSapEjb;

	@EJB
	YgAdesioneHome ygAdesioneHome;

	@EJB
	DeCpiHome deCpiHome;

	public boolean isExtraUE(UtenteCompletoDTO utente) {
		if (utente.getCittadinanza() != null && utente.getCittadinanza().getFlgCee() != null
				&& "S".equalsIgnoreCase(utente.getCittadinanza().getFlgCee())) {
			return false;
		}
		return true;
	}

	public String getTelefonoUtente(UtenteCompletoDTO utente) {

		String telefonoUtente = utente.getTelCasa();

		// if (telefonoUtente == null) {
		// telefonoUtente = utente.getUtenteDTO().getPfPrincipalDTO().getTelefonoUtente();
		// }

		return telefonoUtente;

	}

	private String getInputXmlForRichiestaSap(String identificativoSap) throws JAXBException, SAXException {

		String inputXml = null;

		IDSAP idSap = new IDSAP();
		idSap.setIdentificativoSap(identificativoSap);
		inputXml = youthGuaranteeSapEjb.convertRichiestaSapToString(idSap);

		return inputXml;

	}
	
	private String getInputXmlForVerificaEsistenzaSap(String codiceFiscale) throws JAXBException, SAXException {

		String inputXml = null;

		VerificaSAP verificaSap = new VerificaSAP();
		verificaSap.setCodiceFiscale(codiceFiscale);
		inputXml = youthGuaranteeSapEjb.convertVerificaSapToString(verificaSap);

		return inputXml;

	}

	public String getInputXmlForVerificaEsistenzaSap(UtenteCompletoDTO utente) throws JAXBException, SAXException {

		String inputXml = null;

		VerificaSAP verificaSap = new VerificaSAP();
		verificaSap.setCodiceFiscale(utente.getCodiceFiscale());
		inputXml = youthGuaranteeSapEjb.convertVerificaSapToString(verificaSap);

		return inputXml;

	}

	/**
	 * Anche detta MINI-SAP
	 * 
	 * @param utente
	 * @param dataAdesione
	 * @param deCpiAdesioneDTO
	 * @return
	 * @throws Exception
	 */
	public String getInputXmlForInvioSap(UtenteCompletoDTO utente, Date dataAdesione, DeCpiDTO deCpiAdesioneDTO)
			throws Exception {

		String inputXml = null;

		String codCpiMin = null;
		UtenteInfo utenteInfo = utenteInfoHome.findById(utente.getId());
		if (utenteInfo != null && utenteInfo.getDeComuneDomicilio() != null
				&& utenteInfo.getDeComuneDomicilio().getDeCpi() != null) {
			codCpiMin = utenteInfo.getDeComuneDomicilio().getDeCpi().getCodCpiMin();
		}

		// se l'utente è fuori regione allora
		// il campo cpi min deve essere compilato
		// utilizzando il campo yg_adesione.cod_cpi_adesione
		// valorizzato dall'utente all'atto di adesione

		if (utenteInfo.getDeComuneDomicilio() != null
				&& utenteInfo.getDeComuneDomicilio().getDeProvincia() != null
				&& utenteInfo.getDeComuneDomicilio().getDeProvincia().getDeRegione() != null
				&& !ConstantsSingleton.COD_REGIONE.toString().equalsIgnoreCase(
						utenteInfo.getDeComuneDomicilio().getDeProvincia().getDeRegione().getCodRegione())) {
			DeCpi deCpi = deCpiHome.findById(deCpiAdesioneDTO.getId());
			if (deCpi != null) {
				codCpiMin = deCpi.getCodCpiMin();
			}
		}

		LavoratoreType lavoratoreType = new LavoratoreType();
		Datiinvio datiinvio = new Datiinvio();
		lavoratoreType.setDatiinvio(datiinvio);

		datiinvio.setDataultimoagg(Utils.dateToGregorianDate(dataAdesione));
		datiinvio.setCodiceentetit(codCpiMin);
		datiinvio.setTipovariazione("01");
		datiinvio.setDatadinascita(Utils.dateToGregorianDate(utente.getDataNascita()));

		Datianagrafici datianagrafici = new Datianagrafici();
		lavoratoreType.setDatianagrafici(datianagrafici);

		// dati personali

		Datipersonali datipersonali = new Datipersonali();
		datianagrafici.setDatipersonali(datipersonali);

		datipersonali.setCodicefiscale(utente.getCodiceFiscale()==null?null:utente.getCodiceFiscale().toUpperCase());
		datipersonali.setCognome(utente.getCognome()==null?null:utente.getCognome().toUpperCase());
		datipersonali.setNome(utente.getNome()==null?null:utente.getNome().toUpperCase());
		
		datipersonali.setSesso(utente.getGenere().getId());
		datipersonali.setDatanascita(Utils.dateToGregorianDate(utente.getDataNascita()));
		datipersonali.setCodcomune(utente.getComuneNascita().getId());
		datipersonali.setCodcittadinanza(utente.getCittadinanza().getId());

		// dati stranieri

		Datistranieri datistranieri = new Datistranieri();
		datianagrafici.setDatistranieri(datistranieri);

		if (isExtraUE(utente)) {
			if (utente.getDocumentoSoggiorno() != null) {
				datistranieri.setCodtipodocumento(utente.getDocumentoSoggiorno().getId());
			}
			datistranieri.setNumero(utente.getNumeroDocumento());
			if (utente.getMotivoPermesso() != null) {
				datistranieri.setMotivo(utente.getMotivoPermesso().getId());
			}
			datistranieri.setValidoal(Utils.dateToGregorianDate(utente.getDataScadenzaDocumento()));
		}

		// domicilio

		Domicilio domicilio = new Domicilio();
		datianagrafici.setDomicilio(domicilio);

		if (utente.getComuneDomicilio() != null) {
			domicilio.setCodcomune(utente.getComuneDomicilio().getId());
		}
		domicilio.setCap(utente.getCapDomicilio());

		String indirizzoDomicilio = utente.getIndirizzoDomicilio();
		if (indirizzoDomicilio != null && indirizzoDomicilio.length() > 100) {
			// troncamento indirizzo domicilio per mini-sap, max 100 caratteri
			indirizzoDomicilio = indirizzoDomicilio.substring(0, 100);
		}
		domicilio.setIndirizzo(indirizzoDomicilio);

		// residenza

		Residenza residenza = new Residenza();
		datianagrafici.setResidenza(residenza);

		if (utente.getComuneResidenza() != null) {
			residenza.setCodcomune(utente.getComuneResidenza().getId());
		}
		residenza.setCap(utente.getCapResidenza());

		String indirizzoResidenza = utente.getIndirizzoResidenza();
		if (indirizzoResidenza != null && indirizzoResidenza.length() > 100) {
			// troncamento indirizzo residenza per mini-sap, max 100 caratteri
			indirizzoResidenza = indirizzoResidenza.substring(0, 100);
		}
		residenza.setIndirizzo(indirizzoResidenza);

		// recapiti

		Recapiti recapiti = new Recapiti();
		datianagrafici.setRecapiti(recapiti);

		String numeroTelefono = getTelefonoUtente(utente);
		if (numeroTelefono != null && numeroTelefono.length() > 15) {
			// troncamento per mini-sap, max 15 caratteri
			numeroTelefono = numeroTelefono.substring(0, 15);
		}
		recapiti.setTelefono(numeroTelefono);

		String cellulare = utente.getCellulare();
		if (cellulare != null && cellulare.length() > 15) {
			// troncamento per mini-sap, max 15 caratteri
			cellulare = cellulare.substring(0, 15);
		}
		recapiti.setCellulare(cellulare);

		recapiti.setEmail(utente.getEmail());

		// controlla la presenza di almeno uno tra i campi dei recapiti

		if (getTelefonoUtente(utente) == null && utente.getCellulare() == null && utente.getEmail() == null) {
			throw new Exception("Nessun recapito indicato (telefono, email, cellulare)");
		}

		// altri campi necessari da xsd

		Datiamministrativi datiamministrativi = new Datiamministrativi();
		lavoratoreType.setDatiamministrativi(datiamministrativi);

		EsperienzelavoroLst esperienzelavoroLst = new EsperienzelavoroLst();
		lavoratoreType.setEsperienzelavoroLst(esperienzelavoroLst);

		LivelliistruzioneLst livelliistruzioneLst = new LivelliistruzioneLst();
		lavoratoreType.setLivelliistruzioneLst(livelliistruzioneLst);

		Allegato allegato = new Allegato();
		lavoratoreType.setAllegato(allegato);

		// conversione oggetto stringa

		inputXml = youthGuaranteeSapEjb.convertSapToString(lavoratoreType);

		return inputXml;

	}

	public boolean recuperaIdentificativoSap(Date currentDate, UtenteCompletoDTO utente, DeCpiDTO deCpiAdesioneDTO) {

		boolean esisteSap = false;

		try {

			boolean isSapInviataAndAdesioneNonInviata = isSapInviataAndAdesioneNonInviata(utente);

			// "pezzotto" per risolvere problema verificaEsistenzaSap nel seguente caso:
			// - verifica esistenza: KO
			// - invio sap: OK
			// - invio adesione: KO
			// inizio anomalie:
			// - verifica esistenza: KO (non ancora processato) -> viene resettato l'id sap
			// - invio sap: KO (sembra che il secondo invio non funzioni)
			// - invio adesione: KO
			// --> in questi casi in cui si ha già inviato la sap non la si reinvia
			if (!isSapInviataAndAdesioneNonInviata) {

				esisteSap = verificaEsistenzaSap(currentDate, utente);

				if (!esisteSap) {
					esisteSap = invioSap(currentDate, utente, deCpiAdesioneDTO);
				}

			} else {

				esisteSap = true;

			}

		} catch (Exception e) {
			// in caso di errore durante le
			// elaborazioni sap non bloccare
			// l'invio dell'adesione
			log.error("Errore Generico verifica esistenza SAP o invio SAP: " + e.getMessage());
		}

		return esisteSap;

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean isSapInviataAndAdesioneNonInviata(UtenteCompletoDTO utente) {

		boolean esisteIdentificativoSap = false;

		Integer idPfPrincipalUtente = utente.getUtenteDTO().getPfPrincipalDTO().getId();
		YgAdesioneDTO oldYgAdesioneDTO = ygAdesioneHome.findLatestDTOAttivaByIdPfPrincipal(idPfPrincipalUtente);

		if (oldYgAdesioneDTO != null && oldYgAdesioneDTO.getIdentificativoSap() != null
				&& !"".equalsIgnoreCase(oldYgAdesioneDTO.getIdentificativoSap())
				&& !"0".equalsIgnoreCase(oldYgAdesioneDTO.getIdentificativoSap())
				&& (oldYgAdesioneDTO.getFlgAdesione() == null || !oldYgAdesioneDTO.getFlgAdesione())) {

			esisteIdentificativoSap = true;

		}

		return esisteIdentificativoSap;

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean esisteIdentificativoSap(UtenteCompletoDTO utente) {

		boolean esisteIdentificativoSap = false;

		Integer idPfPrincipalUtente = utente.getUtenteDTO().getPfPrincipalDTO().getId();
		YgAdesioneDTO oldYgAdesioneDTO = ygAdesioneHome.findLatestDTOAttivaByIdPfPrincipal(idPfPrincipalUtente);

		if (oldYgAdesioneDTO != null && oldYgAdesioneDTO.getIdentificativoSap() != null
				&& !"".equalsIgnoreCase(oldYgAdesioneDTO.getIdentificativoSap())
				&& !"0".equalsIgnoreCase(oldYgAdesioneDTO.getIdentificativoSap())) {

			esisteIdentificativoSap = true;

		}

		return esisteIdentificativoSap;

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean richiestaSap(Date currentDate, UtenteCompletoDTO utente) throws JAXBException, SAXException,
			RemoteException {

		boolean success = false;
		String identificativoSap = null;
		LavoratoreType sap = null;

		// verifica esistenza adesione precedente

		Integer idPfPrincipalUtente = utente.getUtenteDTO().getPfPrincipalDTO().getId();
		YgAdesioneDTO oldYgAdesioneDTO = ygAdesioneHome.findLatestDTOAttivaByIdPfPrincipal(idPfPrincipalUtente);

		if (oldYgAdesioneDTO != null) {

			identificativoSap = oldYgAdesioneDTO.getIdentificativoSap();

			// chiamata al ws

			String inputXml = getInputXmlForRichiestaSap(identificativoSap);

			if (YgDebugConstants.IS_DEBUG) {
				identificativoSap = YgDebugConstants.DEBUG_IDENTIFICATIVO_SAP_VERIFICA;
			} else {
				sap = youthGuaranteeSapEjb.richiestaSap(inputXml);
			}

		}

		// TODO eventuali verifiche da effettuare sulla SAP
		// TODO eventuali verifiche da effettuare sulla SAP
		// TODO eventuali verifiche da effettuare sulla SAP

		return success;

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public String verificaEsistenzaSap(String codiceFiscale) throws JAXBException, SAXException, RemoteException {
		
		String identificativoSap = null;
		String inputXml = null;
		
		inputXml = getInputXmlForVerificaEsistenzaSap(codiceFiscale);
		identificativoSap = youthGuaranteeSapEjb.verificaEsistenzaSap(inputXml);
				
		return identificativoSap;
		
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean verificaEsistenzaSap(Date currentDate, UtenteCompletoDTO utente) throws JAXBException, SAXException,
			RemoteException {

		String identificativoSap = null;
		boolean esisteSap = false;
		String codiceFiscale = utente.getCodiceFiscale();

		// verifica esistenza adesione precedente

		Integer idPfPrincipalUtente = utente.getUtenteDTO().getPfPrincipalDTO().getId();
		YgAdesioneDTO oldYgAdesioneDTO = ygAdesioneHome.findLatestDTOAttivaByIdPfPrincipal(idPfPrincipalUtente);

		// chiamata al ws

		String inputXml = null;

		inputXml = getInputXmlForVerificaEsistenzaSap(utente);

		if (YgDebugConstants.IS_DEBUG) {
			identificativoSap = YgDebugConstants.DEBUG_IDENTIFICATIVO_SAP_VERIFICA;
		} else {
			identificativoSap = youthGuaranteeSapEjb.verificaEsistenzaSap(inputXml);
		}

		if (identificativoSap != null && !"0".equalsIgnoreCase(identificativoSap)) {
			esisteSap = true;
		}

		// salvataggio su db in caso risposta sia ok

		if (esisteSap) {

			YgAdesioneDTO newYgAdesioneDTO = new YgAdesioneDTO();
			newYgAdesioneDTO.setCodiceFiscale(codiceFiscale);
			newYgAdesioneDTO.setDtAdesione(null);
			newYgAdesioneDTO.setIdentificativoSap(identificativoSap);
			newYgAdesioneDTO.setPfPrincipal(utente.getUtenteDTO().getPfPrincipalDTO());
			newYgAdesioneDTO.setCodMonoProv("V");
			newYgAdesioneDTO.setFlgAdesione(null);
			newYgAdesioneDTO.setStrMessWsAdesione(null);
			newYgAdesioneDTO.setFlgSap(null);
			newYgAdesioneDTO.setStrMessWsInvioSap(null);
			newYgAdesioneDTO.setDtmIns(currentDate);
			newYgAdesioneDTO.setDtmMod(currentDate);
			newYgAdesioneDTO.setIdPrincipalIns(utente.getId());
			newYgAdesioneDTO.setIdPrincipalMod(utente.getId());
			newYgAdesioneDTO.setStrMessWsNotifica(null);

			if (oldYgAdesioneDTO != null) {

				newYgAdesioneDTO.setId(oldYgAdesioneDTO.getId());
				newYgAdesioneDTO.setDtAdesione(oldYgAdesioneDTO.getDtAdesione());
				newYgAdesioneDTO.setCodMonoProv(oldYgAdesioneDTO.getCodMonoProv());
				newYgAdesioneDTO.setFlgAdesione(oldYgAdesioneDTO.getFlgAdesione());
				newYgAdesioneDTO.setStrMessWsAdesione(oldYgAdesioneDTO.getStrMessWsAdesione());
				newYgAdesioneDTO.setFlgSap(oldYgAdesioneDTO.getFlgSap());
				newYgAdesioneDTO.setStrMessWsInvioSap(oldYgAdesioneDTO.getStrMessWsInvioSap());
				newYgAdesioneDTO.setIdPrincipalIns(oldYgAdesioneDTO.getIdPrincipalIns());
				newYgAdesioneDTO.setDtmIns(oldYgAdesioneDTO.getDtmIns());
				newYgAdesioneDTO.setStrMessWsNotifica(oldYgAdesioneDTO.getStrMessWsNotifica());

				newYgAdesioneDTO.setFlgPresoInCarico(oldYgAdesioneDTO.getFlgPresoInCarico());
				newYgAdesioneDTO.setPfPrincipalPic(oldYgAdesioneDTO.getPfPrincipalPic());
				newYgAdesioneDTO.setDtPresaInCarico(oldYgAdesioneDTO.getDtPresaInCarico());

				ygAdesioneHome.mergeDTO(newYgAdesioneDTO, utente.getId());
			} else {
				ygAdesioneHome.persistDTO(newYgAdesioneDTO, utente.getId());
			}
		}

		return esisteSap;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean invioSap(Date currentDate, UtenteCompletoDTO utente, DeCpiDTO deCpiAdesioneDTO) throws Exception {

		RisultatoInvioSap risultatoInvioSap = new RisultatoInvioSap();
		String identificativoSap = null;
		String messaggioErrore = null;
		boolean success = false;
		String codiceFiscale = utente.getCodiceFiscale();

		// verifica esistenza adesione precedente

		Integer idPfPrincipalUtente = utente.getUtenteDTO().getPfPrincipalDTO().getId();
		YgAdesioneDTO oldYgAdesioneDTO = ygAdesioneHome.findLatestDTOAttivaByIdPfPrincipal(idPfPrincipalUtente);

		// chiamata al ws

		String inputXml = null;

		inputXml = getInputXmlForInvioSap(utente, currentDate, deCpiAdesioneDTO);

		if (YgDebugConstants.IS_DEBUG) {
			identificativoSap = YgDebugConstants.DEBUG_IDENTIFICATIVO_SAP_INVIO;
			if (YgDebugConstants.DEBUG_ECCEZIONE_SERVIZIO_NON_DISPONIBILE_INVIO_SAP) {
				throw new MyPortalException(YgConstants.COD_ERRORE_SERVIZIO_NON_DISPONIBILE,
						YgConstants.DESCRIZIONE_ERRORE_SERVIZIO_NON_DISPONIBILE);
			}
			if (YgDebugConstants.DEBUG_ECCEZIONE_ERRORE_GENERICO_INVIO_SAP) {
				throw new MyPortalException(YgConstants.COD_ERRORE_GENERICO, YgConstants.DESCRIZIONE_ERRORE_GENERICO);
			}
			risultatoInvioSap = new RisultatoInvioSap();
			risultatoInvioSap.setSuccess(!"0".equalsIgnoreCase(identificativoSap));
		} else {

			// invio sap e elaborazione risultato
			risultatoInvioSap = youthGuaranteeSapEjb.inviaSap(inputXml);
			if (risultatoInvioSap != null) {
				identificativoSap = risultatoInvioSap.getCodiceSAP();
				if (!risultatoInvioSap.isSuccess()) {
					messaggioErrore = risultatoInvioSap.getMessaggioErrore();
				}
			}

		}

		if (identificativoSap != null && !"0".equalsIgnoreCase(identificativoSap)
				&& !"".equalsIgnoreCase(identificativoSap) && risultatoInvioSap != null
				&& risultatoInvioSap.isSuccess()) {
			success = true;
		}

		// salvataggio su db in caso risposta sia ok

		YgAdesioneDTO newYgAdesioneDTO = new YgAdesioneDTO();
		newYgAdesioneDTO.setCodiceFiscale(codiceFiscale);
		newYgAdesioneDTO.setDtAdesione(null);
		newYgAdesioneDTO.setPfPrincipal(utente.getUtenteDTO().getPfPrincipalDTO());
		newYgAdesioneDTO.setCodMonoProv("V");
		newYgAdesioneDTO.setFlgAdesione(null);
		newYgAdesioneDTO.setStrMessWsAdesione(null);
		newYgAdesioneDTO.setStrMessWsInvioSap(null);
		newYgAdesioneDTO.setDtmIns(currentDate);
		newYgAdesioneDTO.setDtmMod(currentDate);
		newYgAdesioneDTO.setIdPrincipalIns(utente.getId());
		newYgAdesioneDTO.setIdPrincipalMod(utente.getId());
		newYgAdesioneDTO.setStrMessWsNotifica(null);

		if (success) {
			newYgAdesioneDTO.setIdentificativoSap(identificativoSap);
			newYgAdesioneDTO.setFlgSap(true);
		} else {
			newYgAdesioneDTO.setFlgSap(false);
			newYgAdesioneDTO.setIdentificativoSap(null);
			newYgAdesioneDTO.setStrMessWsInvioSap(messaggioErrore);
		}

		if (oldYgAdesioneDTO != null) {

			newYgAdesioneDTO.setDtmIns(oldYgAdesioneDTO.getDtmIns());
			newYgAdesioneDTO.setIdPrincipalIns(oldYgAdesioneDTO.getIdPrincipalIns());
			newYgAdesioneDTO.setId(oldYgAdesioneDTO.getId());
			newYgAdesioneDTO.setDtAdesione(oldYgAdesioneDTO.getDtAdesione());
			newYgAdesioneDTO.setCodMonoProv(oldYgAdesioneDTO.getCodMonoProv());
			newYgAdesioneDTO.setFlgAdesione(oldYgAdesioneDTO.getFlgAdesione());
			newYgAdesioneDTO.setStrMessWsAdesione(oldYgAdesioneDTO.getStrMessWsAdesione());
			newYgAdesioneDTO.setStrMessWsNotifica(oldYgAdesioneDTO.getStrMessWsNotifica());

			newYgAdesioneDTO.setFlgPresoInCarico(oldYgAdesioneDTO.getFlgPresoInCarico());
			newYgAdesioneDTO.setPfPrincipalPic(oldYgAdesioneDTO.getPfPrincipalPic());
			newYgAdesioneDTO.setDtPresaInCarico(oldYgAdesioneDTO.getDtPresaInCarico());

			ygAdesioneHome.mergeDTO(newYgAdesioneDTO, utente.getId());
		} else {
			ygAdesioneHome.persistDTO(newYgAdesioneDTO, utente.getId());
		}

		return success;
	}
}
