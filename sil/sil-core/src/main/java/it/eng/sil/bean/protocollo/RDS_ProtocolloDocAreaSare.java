package it.eng.sil.bean.protocollo;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Vector;

import javax.activation.DataSource;
import javax.activation.FileDataSource;

import org.apache.axis.AxisFault;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.bean.Documento;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPoint;
import it.eng.sil.coop.wsClient.docareaProto.DOCAREAProtoLocator;
import it.eng.sil.coop.wsClient.docareaProto.DOCAREAProtoSoap;
import it.eng.sil.coop.wsClient.docareaProto.DOCAREAProtoSoapStub;
import it.eng.sil.coop.wsClient.docareaProto.Inserimento;
import it.eng.sil.coop.wsClient.docareaProto.InserimentoResponse;
import it.eng.sil.coop.wsClient.docareaProto.InserimentoRet;
import it.eng.sil.coop.wsClient.docareaProto.Login;
import it.eng.sil.coop.wsClient.docareaProto.LoginResponse;
import it.eng.sil.coop.wsClient.docareaProto.LoginRet;
import it.eng.sil.coop.wsClient.docareaProto.Protocollazione;
import it.eng.sil.coop.wsClient.docareaProto.ProtocollazioneResponse;
import it.eng.sil.coop.wsClient.docareaProto.ProtocollazioneRet;
import it.eng.sil.security.User;

/**
 * Protocollazione DOCAREA<br>
 * <ul>
 * <ol>
 * <li>creazione report in formato pdf senza le informazioni di protocollo <br>
 * se non si deve creare il report ma si deve protocollare un documento vuoto allora bisogna creare un file temporaneo
 * vuoto. <br>
 * se invece si deve inviare un allegato ricevuto dal client si usa direttamente il file temporaneo creato dal sistema.
 * <li>invio del report/file e lettura info protocollo tramite web service
 * <li>il file temporaneo inviato viene rinomitato utilizzando il n. di protocollo restituito.
 * <li>inserimento documento con il numero di protocollo ottenuto dal web service. Anno e data del sistema locale.
 * <li>creazione secondo report (se esiste) con le informazioni di protocollazione
 * <li>inserimento file (se esiste)
 * </ol>
 * </ul>
 * 
 * @author Savino
 */
public class RDS_ProtocolloDocAreaSare implements RegistraDocumentoStrategy {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(RDS_ProtocolloDocAreaSare.class.getName());

	/**
	 * Il nome del file/documento che verra' specificato nella profilatura xml.
	 */
	private String nomeDocumento;
	/**
	 * Descrizione file/documento che verra' specificato nella profilatura xml.
	 */
	private String descrizioneDocumento;
	/**
	 * E' il file che verra' spedito al ws. Vedere i metodi setFile();
	 */
	private File fileDaInviare;

	private String descrizioneCpiMittente;

	/**
	 * 
	 */
	public void registra(Documento doc, TransactionQueryExecutor tex) throws Exception {
		boolean creaReport = ((doc.getTempFile() == null) && (doc.getCrystalClearRelativeReportFile() != null));
		boolean invioCompleto = ProtocolloDocumentoUtil.invioCompleto();
		if (invioCompleto) {
			if (creaReport) {
				doc.creaReportXDOCAREA(tex);
				setFileReport(doc.getTempFilePreProtocollo(), doc);
			} else {
				// si sta inviando un file ricevuto dal client o nulla
				// (documento vuoto)
				setFile(doc);
			}
		} else {
			setFileVuoto(doc);
		}
		leggiDescrizioneTipoDoc(doc, tex);
		leggiDescrizioneCPI(tex);

		inviaDocumento(doc, tex);
		try {
			rinominaFilePreProtocollo(doc);
			if (doc.inInserimento())
				doc.inserisciDocumento(tex);
			else
				// oppure aggiornamento della sola tabella AM_DOCUMENTO
				doc.aggiornaDocumento(tex);
			if (creaReport) {
				if (invioCompleto)
					// il report 2 e' il report che contiene le informazioni di
					// protocollo
					doc.creaEinserisciReport_2(tex);
				else
					doc.creaEinserisciReport(tex);
			}
			// il secondo report o il file inviato dal client viene registrato
			// in AM_DOCUMENTO_BLOB
			// N.B. Potrebbe non esserci nessun blob da inserire (caso documento
			// vuoto: empty_blob() inserito di default. Vedere procedure pl-sql
			// di inserimento del documento).
			doc.inserisciBlob(tex);
		}
		/*
		 * catch(EMFUserError e ) { it.eng.sil.util.TraceWrapper.debug( _logger, "IMPOSSIBILE INSERIRE/AGGIORNARE IL
		 * LOCALE IL DOCUMENTO PROTOCOLLATO IN DOCAREA.", (Exception)e);
		 * 
		 * _logger.debug( "Documento: "+doc.toString());
		 * 
		 * throw e; }
		 */
		catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"IMPOSSIBILE INSERIRE/AGGIORNARE IL LOCALE IL DOCUMENTO PROTOCOLLATO IN DOCAREA.", e);

			_logger.debug("Documento: " + doc.toString());

			it.eng.sil.util.TraceWrapper.debug(_logger, "ERRORE DOCAREA", RequestContainer.getRequestContainer());

			it.eng.sil.util.TraceWrapper.debug(_logger, "ERRORE DOCAREA",
					RequestContainer.getRequestContainer().getSessionContainer());

			Vector v = new Vector();
			v.add(String.valueOf(doc.getNumAnnoProt()));
			v.add(String.valueOf(doc.getNumProtInserito()));
			throw new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.Protocollazione.DOCAREA_ERRORE_RENAME_FILE,
					v);
		}
	}

	/**
	 * La descrizione del tipo documento viene utilizzata nel profilo xml
	 * 
	 * @param doc
	 * @param tex
	 */
	private void leggiDescrizioneTipoDoc(Documento doc, TransactionQueryExecutor tex) throws Exception {
		SourceBean row = (SourceBean) tex.executeQuery("GET_TIPODOC", new Object[] { doc.getCodTipoDocumento() },
				"SELECT");
		String desc = (String) row.getAttribute("row.descrizione");
		if (desc == null)
			throw new Exception("impossibile leggere la descrizione del tipo documento da protocollare");
		doc.setStrTipoDoc(desc);
	}

	private void leggiDescrizioneCPI(TransactionQueryExecutor tex) throws Exception {
		User user = null;
		if (RequestContainer.getRequestContainer() != null
				&& RequestContainer.getRequestContainer().getSessionContainer() != null) {
			if (RequestContainer.getRequestContainer().getSessionContainer()
					.getAttribute(User.USERID) instanceof User) {
				user = (User) RequestContainer.getRequestContainer().getSessionContainer().getAttribute(User.USERID);
			}
		}
		String codCpi = null;
		if (user != null) {
			codCpi = user.getCodRif();
		}

		if (codCpi == null || ("").equalsIgnoreCase(codCpi)) {
			codCpi = "105800100";
		}

		SourceBean row = (SourceBean) tex.executeQuery("GET_CPI_DOCUMENTO", new Object[] { codCpi }, "SELECT");
		String desc = (String) row.getAttribute("row.strdescrizione");
		if (desc == null)
			throw new Exception("impossibile leggere la descrizione del cpi del mittente");
		descrizioneCpiMittente = "CPI di " + desc;
	}

	/**
	 * 
	 * @param cdnLav
	 * @param tex
	 * @return SourceBean contenente le info sul lavoratore
	 * @throws Exception
	 */
	private SourceBean leggiInfoLavoratore(BigDecimal cdnLav, TransactionQueryExecutor tex) throws Exception {
		SourceBean row = (SourceBean) tex.executeQuery("GET_DATI_LAV", new Object[] { cdnLav }, "SELECT");
		if (row == null) {
			throw new Exception("impossibile leggere le info del lavoratore");
		}
		return getRowAttribute(row);
	}

	/**
	 * 
	 * @param prgAzienda
	 * @param prgUnita
	 * @param tex
	 * @return SourceBean contenente le info sull'azienda
	 * @throws Exception
	 */
	private SourceBean leggiInfoAzienda(BigDecimal prgAzienda, BigDecimal prgUnita, TransactionQueryExecutor tex)
			throws Exception {
		SourceBean row = (SourceBean) tex.executeQuery("CM_INFO_UNITA_AZIENDA", new Object[] { prgAzienda, prgUnita },
				"SELECT");
		if (row == null) {
			throw new Exception("impossibile leggere le info azienda");
		}
		return getRowAttribute(row);
	}

	private SourceBean getRowAttribute(SourceBean row) {
		return row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row;
	}

	/**
	 * Copia e rinomina il file inviato a docarea con successo.<br>
	 * Viene rinominato cosi': codTipoDoc_nProt.~rpt<br>
	 * Viene copiato nella directory indicata nel file di configurazione protocollo_documento.xml nell'attributo
	 * "file.path_file_inviato"
	 * 
	 * @param doc
	 * @throws Exception
	 */
	private void rinominaFilePreProtocollo(Documento doc) throws Exception {
		String nomeFile = doc.getCodTipoDocumento() + "_" + doc.getNumProtInserito() + ".~rpt";
		String nuovoFile = ProtocolloDocumentoUtil.getPathFileInviato() + File.separator + nomeFile;
		doc.rinominaFilePreProtocollo(nuovoFile);
	}

	/**
	 * Invia il documento a docarea. Aggiorna il numero di protocollo nel Documento. Aggiorna anno e data di protocollo
	 * nel Documento (la data viene restituita dal ws in formato dd/mm/aaaa)
	 * 
	 * @throws Exception
	 */
	private void inviaDocumento(Documento doc, TransactionQueryExecutor tex) throws Exception {
		try {
			String n = null;
			String urlWS = null;

			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();
			DOCAREAProtoLocator locator = null;
			// è l'identificativo dell'ente che espone il servizio di
			// protocollazione che viene invocato (pag. 7)
			String codEnte = ProtocolloDocumentoUtil.getCodEnte();
			// è il nome utente relativo a chi sta effettuando la sessione di
			// lavoro
			String username = ProtocolloDocumentoUtil.getUsernameEnte();
			// è la password dell'utente che sta effettuando la sessione di
			// lavoro.
			String password = ProtocolloDocumentoUtil.getPasswordEnte();
			// si recupera l'url del web service da invocare
			locator = new DOCAREAProtoLocator();
			EndPoint endPoint = new EndPoint();
			/*
			 * String codProvincia = it.eng.sil.util.InfoProvinciaSingleton.getInstance().getCodice();
			 * endPoint.init(dataSourceJndiName, "DOCAREAProtocollazione_"+codProvincia);
			 */
			endPoint.init(dataSourceJndiName, "DOCAREAProtocollazione"); // lancia
																			// una
																			// java.lang.Exception
			urlWS = endPoint.getUrl();
			_logger.debug("url Web Service DOCAREAProtocollazione: " + urlWS);

			if (urlWS == null)
				throw new EMFUserError(EMFErrorSeverity.BLOCKING,
						MessageCodes.Protocollazione.DOCAREA_ERRORE_CONNESSIONE_CONFIGURA_URL);
			long t1 = System.currentTimeMillis();
			_logger.debug("Tempo t1: " + t1 + " ms");

			locator.setDOCAREAProtoSoapEndpointAddress(urlWS);
			DOCAREAProtoSoap docAreaProtocollo = locator.getDOCAREAProtoSoap();
			// imposto le opzioni di invio del messaggio soap
			((DOCAREAProtoSoapStub) docAreaProtocollo).setUsaDIME(ProtocolloDocumentoUtil.isDIMEAttachment());
			((DOCAREAProtoSoapStub) docAreaProtocollo).setSOAP12(ProtocolloDocumentoUtil.isSOAP12());
			// Passo 1. Login
			Login login = new Login(codEnte, username, password);
			LoginResponse loginRes = docAreaProtocollo.login(login);
			if (loginRes == null) {
				_logger.debug("Risposta del servizio di Login nulla");

				throw new EMFUserError(EMFErrorSeverity.BLOCKING,
						MessageCodes.Protocollazione.DOCAREA_ERRORE_RISPOSTA_ASSENTE);
			}
			LoginRet loginRet = loginRes.getLoginResult();
			_logger.debug("Web Service Login invocato. Risultato: " + loginRet);

			if (loginRet.getLngErrNumber() != 0) {
				Vector v = new Vector();
				String msg = loginRet.getStrErrString();
				if (msg == null)
					msg = "Non ritornato";
				v.add(msg);
				throw new DOCAREAProtocolloDocumentoError(EMFErrorSeverity.BLOCKING,
						MessageCodes.Protocollazione.DOCAREA_ERRORE_OPERAZIONE, loginRet.getLngErrNumber(), v);
			}
			// Passo 2. Inserimento
			DataSource dsDocumento = new FileDataSource(fileDaInviare);
			Inserimento ins = new Inserimento(username, loginRet.getStrDST());
			InserimentoResponse ir = docAreaProtocollo.inserimento(ins, dsDocumento);
			if (ir == null) {
				_logger.debug("Risposta del servizio di Inserimento nulla");

				throw new EMFUserError(EMFErrorSeverity.BLOCKING,
						MessageCodes.Protocollazione.DOCAREA_ERRORE_RISPOSTA_ASSENTE);
			}
			InserimentoRet insRet = ir.getInserimentoResult();
			_logger.debug("Web Service Inserimento invocato. Risultato: " + insRet);

			if (insRet.getLngErrNumber() != 0) {
				Vector v = new Vector();
				String msg = insRet.getStrErrString();
				if (msg == null)
					msg = "Non ritornato";
				v.add(msg);
				throw new DOCAREAProtocolloDocumentoError(EMFErrorSeverity.BLOCKING,
						MessageCodes.Protocollazione.DOCAREA_ERRORE_OPERAZIONE, insRet.getLngErrNumber(), v);
			}
			// Passo 3. Protocollazione
			// Creazione del profilo xml
			String idDocumento = String.valueOf(insRet.getLngDocID());
			DOCAREAProfiloIFace profilo = creaProfilo(idDocumento, doc, tex);
			DataSource dsProfiloXML = new ByteArrayDataSource(profilo.toString().getBytes(), "text/xml", "profilo.xml");
			//
			Protocollazione prot = new Protocollazione(username, loginRet.getStrDST());
			ProtocollazioneResponse protocolloRes = docAreaProtocollo.protocollazione(prot, dsProfiloXML);
			if (protocolloRes == null) {
				_logger.debug("Risposta del servizio di Protocollazione nulla");

				throw new EMFUserError(EMFErrorSeverity.BLOCKING,
						MessageCodes.Protocollazione.DOCAREA_ERRORE_RISPOSTA_ASSENTE);
			}
			ProtocollazioneRet protocolloRet = protocolloRes.getProtocollazioneResult();
			_logger.debug("Web Service Protocollazione invocato. Risultato: " + protocolloRet);

			if (protocolloRet.getLngErrNumber() != 0) {
				Vector v = new Vector();
				String msg = protocolloRet.getStrErrString();
				if (msg == null)
					msg = "Non ritornato";
				v.add(msg);
				throw new DOCAREAProtocolloDocumentoError(EMFErrorSeverity.BLOCKING,
						MessageCodes.Protocollazione.DOCAREA_ERRORE_OPERAZIONE, protocolloRet.getLngErrNumber(), v);
			}
			long t2 = System.currentTimeMillis();
			_logger.debug("Tempo t2: " + t2 + " ms");

			_logger.debug("Risultato: " + Long.toString(t2 - t1) + " ms");

			String dataProtocollo = protocolloRet.getStrDataPG();
			long annoProtocollo = protocolloRet.getLngAnnoPG();
			long numeroProtocollo = protocolloRet.getLngNumPG();

			// aggiorno il Documento
			doc.setDatProtocollazione(dataProtocollo);
			doc.setNumAnnoProt(new BigDecimal(annoProtocollo));
			doc.setNumProtInserito(new BigDecimal(numeroProtocollo));
		} catch (AxisFault af) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Errore nella chiamata del web service di protocollazione", af);

			if (af.detail != null && af.detail.getClass().equals(java.net.UnknownHostException.class))
				throw new EMFUserError(EMFErrorSeverity.BLOCKING,
						MessageCodes.Protocollazione.DOCAREA_ERRORE_CONNESSIONE_HOST_SCONOSCIUTO);
			if (af.detail != null && af.detail.getClass().equals(java.net.ConnectException.class)) {
				String s = af.detail.getMessage();
				if (s == null)
					s = af.detail.getLocalizedMessage();
				if (s == null)
					s = "Sconosciuto";
				Vector v = new Vector();
				v.add(s);
				throw new EMFUserError(EMFErrorSeverity.BLOCKING,
						MessageCodes.Protocollazione.DOCAREA_ERRORE_CONNESSIONE, v);
			}
			if (af.detail != null && (af.detail.getClass().equals(org.xml.sax.SAXException.class)
					|| af.detail.getClass().equals(org.xml.sax.SAXParseException.class))) {
				throw new EMFUserError(EMFErrorSeverity.BLOCKING,
						MessageCodes.Protocollazione.DOCAREA_ERRORE_RISPOSTA_XML);
			}
			if (af.getFaultString() != null) {
				String s = af.getFaultString();
				Vector v = new Vector();
				v.add(s);
				throw new EMFUserError(EMFErrorSeverity.BLOCKING,
						MessageCodes.Protocollazione.DOCAREA_ERRORE_CONNESSIONE, v);
			}
			throw new EMFUserError(EMFErrorSeverity.BLOCKING,
					MessageCodes.Protocollazione.DOCAREA_ERRORE_CONNESSIONE_GENERICO);

		} catch (DOCAREAProtocolloDocumentoError pe) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Errore nella chiamata del web service di protocollazione",
					(Exception) pe);

			throw pe;
		} catch (EMFUserError e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Errore nella chiamata del web service di protocollazione",
					(Exception) e);

			throw e;
		} catch (javax.xml.rpc.ServiceException se) {
			String s = se.getMessage();

			if (s == null)
				s = se.getLocalizedMessage();
			if (s == null)
				s = se.getLinkedCause().getLocalizedMessage();
			if (s == null)
				s = se.toString();
			Vector v = new Vector();
			v.add(s);
			it.eng.sil.util.TraceWrapper.debug(_logger, "Errore nella chiamata del web service di protocollazione", se);

			throw new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.Protocollazione.DOCAREA_ERRORE_CONNESSIONE,
					v);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Errore nella chiamata del web service di protocollazione", e);

			throw new EMFUserError(EMFErrorSeverity.BLOCKING, MessageCodes.Protocollazione.DOCAREA_ERRORE);
		}
	}

	/**
	 * 2 casi:<br>
	 * - si deve inviare un allegato ricevuto dal client. Si usa il file temporaneo creato dal sistema - si deve inviare
	 * un file vuoto. In questo caso bisogna creare un file temporaneo che rappresenti un documento vuoto, e lo si deve
	 * assegnare nel Documento alla proprieta' tempFilePreProtocollo. Dopo l'invio quindi verra' rinominato.
	 * 
	 * Viene impostato anche il nome del documento.
	 */
	private void setFile(Documento doc) throws Exception {
		if (doc.getTempFile() != null) {
			this.fileDaInviare = doc.getTempFile();
			this.nomeDocumento = doc.getStrNomeDoc();
			this.descrizioneDocumento = doc.getStrDescrizione();
			doc.setTempFilePreProtocollo(this.fileDaInviare);
		} else {
			setFileVuoto(doc);
		}
	}

	/**
	 * Il file da inviare e' un report(si tratta del primo report, quello senza le informazioni di protocollo).<br>
	 * Il riferimento si trova in Documento.tempFilePreProtocollo<br>
	 * Viene impostato anche il nome del documento.
	 * 
	 * @param fileReport
	 */
	private void setFileReport(File fileReport, Documento doc) {
		this.fileDaInviare = fileReport;
		this.nomeDocumento = doc.getStrNomeDoc();
		this.descrizioneDocumento = doc.getStrDescrizione();
	}

	private void setFileVuoto(Documento doc) throws Exception {
		// creo un file vuoto
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(ProtocolloDocumentoUtil.pathDocumentoVuoto());
			File fTempVuoto = File.createTempFile("~rpt", ".out");
			os = new java.io.FileOutputStream(fTempVuoto);
			int nread = 0;
			byte buffer[] = new byte[1024];
			while ((nread = is.read(buffer)) != -1) {
				os.write(buffer, 0, nread);
			}
			os.flush();
			doc.setTempFilePreProtocollo(fTempVuoto);
			this.fileDaInviare = fTempVuoto;
			this.nomeDocumento = ProtocolloDocumentoUtil.nomeDocumentoVuoto();
			this.descrizioneDocumento = ProtocolloDocumentoUtil.descrizioneDocumentoVuoto();
		} finally {
			if (os != null)
				os.close();
			if (is != null)
				is.close();
		}
	}

	/**
	 * Crea un oggetto che rappresenta il documento XML del profilo da inviare CON I SOLI DATI NECESSARI PER LA
	 * PROTOCOLLAZIONE ER. Per la regione Umbria probabilmente il formato del documento sara' diverso.
	 * 
	 * @return
	 */
	private DOCAREAProfiloIFace creaProfilo(String idDocumento, Documento doc, TransactionQueryExecutor tex)
			throws Exception {
		User user = null;
		if (RequestContainer.getRequestContainer() != null
				&& RequestContainer.getRequestContainer().getSessionContainer() != null) {
			user = (User) RequestContainer.getRequestContainer().getSessionContainer().getAttribute(User.USERID);
		}
		DOCAREAProfiloER __profilo;
		__profilo = new DOCAREAProfiloER();
		// Informazioni Intestazione
		// Descrizione oggetto
		String descrizioneOggetto = doc.getStrTipoDoc();
		__profilo.setOggetto(descrizioneOggetto);
		// IDENTIFICATORE
		// vedere pagina 16: codice dell'ente che gestisce l'applicazione
		// chiamante il WS
		String codiceAmministrazione = "";
		if (user != null) {
			codiceAmministrazione = user.getCodRif();
		}

		if (codiceAmministrazione == null || ("").equalsIgnoreCase(codiceAmministrazione)) {
			codiceAmministrazione = "105800100";
		}
		// vedere pagina 16: P_X Dove X è il nome dell'applicazione chiamante
		String codiceAOO = "Prov_PG";
		// pag 16/17 per il caso d) ed e) vale sempre 0
		String dataRegistrazione = "0";
		// pag 16/17 flusso per il caso d) ed e) vale sempre "U"
		// Modifica 24/03/2014
		// documento Output (O) --> Flusso = Uscita (U)
		// documento Input (I) --> Flusso = Entrata (E)
		String codIO = doc.getCodMonoIO();
		String flusso = "";
		if (codIO.equalsIgnoreCase("I")) {
			flusso = "E";
		} else {
			flusso = "U";
		}

		// pag 16/17 numeroRegistrazione per il caso d) ed e) vale sempre 0
		String numeroRegistrazione = "0";

		__profilo.setIdentificatore("CPI di PG", codiceAOO, dataRegistrazione, flusso, numeroRegistrazione);

		if (flusso.equals("U")) {
			// il CPI DIVENTA MITTENTE

			// codiceAmministrazione codice dell'ente che gestisce l'applicazione
			// chiamante il WS
			// e' gia' stato recuperato
			// denominazione denominazione dell'ente che gestisce l'applicazione
			// chiamante il WS
			String denominazione = this.descrizioneCpiMittente;
			// email indirizzo e-mail dell'ente che gestisce l'applicazione
			// chiamante il WS
			String email = "siul@provincia.perugia.it";
			// unitaOrgID Un elemento UnitaOrganizzativa rappresenta un elemento nel
			// percorso che costituisce della descrizione di un indirizzo.!!!!
			// Di che si tratta?
			String unitaOrgID = "5";
			// codiceAOO P_X Dove X e' il nome dell'applicazione chiamante (pag 16 e
			// pag 19 elemento AOO)

			__profilo.setMittente(codiceAmministrazione, denominazione, email, unitaOrgID, codiceAOO);

			boolean existDestinatario = false;
			String denominazioneDestinatario = "";
			String codiceAmministrazioneDestinatario = "";
			String emailDestinatario = "";
			String unitaOrgDest = "0";

			if (doc.getPrgAzienda() != null && doc.getPrgUnita() != null) {
				existDestinatario = true;
				SourceBean az = leggiInfoAzienda(doc.getPrgAzienda(), doc.getPrgUnita(), tex);
				String codiceFiscale = (String) az.getAttribute("strCodiceFiscale");
				String ragSociale = (String) az.getAttribute("strRagioneSociale");
				codiceAmministrazioneDestinatario = codiceFiscale;
				// CF e Ragione sociale
				denominazioneDestinatario = codiceFiscale + " " + ragSociale;
				// indirizzo email azienda
				if (az.getAttribute("strEmail") != null && !az.getAttribute("strEmail").toString().equals("")) {
					emailDestinatario = (String) az.getAttribute("strEmail");
				}
			}

			if (doc.getCdnLavoratore() != null) {
				existDestinatario = true;
				SourceBean lav = leggiInfoLavoratore(doc.getCdnLavoratore(), tex);
				String codiceFiscale = (String) lav.getAttribute("STRCODICEFISCALE");
				String cognome = (String) lav.getAttribute("STRCOGNOME");
				String nome = (String) lav.getAttribute("STRNOME");
				// destinatario
				if (codiceAmministrazioneDestinatario.equals("")) {
					codiceAmministrazioneDestinatario = "0";
				} else {
					codiceAmministrazioneDestinatario = codiceAmministrazioneDestinatario + " - " + "0";
				}
				// CF e Nome Cognome lavoratore
				if (denominazioneDestinatario.equals("")) {
					denominazioneDestinatario = codiceFiscale + " " + cognome + " " + nome;
				} else {
					denominazioneDestinatario = denominazioneDestinatario + " - " + codiceFiscale + " " + cognome + " "
							+ nome;
				}
				// indirizzo email lavoratore
				if (lav.getAttribute("STREMAIL") != null && !lav.getAttribute("STREMAIL").toString().equals("")) {
					if (emailDestinatario.equals("")) {
						emailDestinatario = (String) lav.getAttribute("STREMAIL");
					} else {
						emailDestinatario = emailDestinatario + " - " + (String) lav.getAttribute("STREMAIL");
					}
				}
			}

			if (!existDestinatario) {
				__profilo.setDestinatario(codiceAmministrazione, denominazione, email, unitaOrgID);
			} else {
				__profilo.setDestinatario(codiceAmministrazioneDestinatario, denominazioneDestinatario,
						emailDestinatario, unitaOrgDest);
			}
		} else {
			// // il CPI DIVENTA DESTINATARIO
			boolean existMittente = false;
			String denominazioneMittente = "";
			String codiceAmministrazioneMittente = "";
			String emailMittente = "";
			String unitaOrgMittente = "0";
			String codiceAOOMittente = "0";

			String denominazione = this.descrizioneCpiMittente;
			String email = "siul@provincia.perugia.it";
			String unitaOrgID = "5";

			__profilo.setDestinatario(codiceAmministrazione, denominazione, email, unitaOrgID);

			if (doc.getPrgAzienda() != null && doc.getPrgUnita() != null) {
				existMittente = true;
				SourceBean az = leggiInfoAzienda(doc.getPrgAzienda(), doc.getPrgUnita(), tex);
				String codiceFiscale = (String) az.getAttribute("strCodiceFiscale");
				String ragSociale = (String) az.getAttribute("strRagioneSociale");
				codiceAmministrazioneMittente = codiceFiscale;
				// CF e Ragione sociale
				denominazioneMittente = codiceFiscale + " " + ragSociale;
				// indirizzo email azienda
				if (az.getAttribute("strEmail") != null && !az.getAttribute("strEmail").toString().equals("")) {
					emailMittente = (String) az.getAttribute("strEmail");
				}
			}

			String tipoDoc = doc.getCodTipoDocumento();

			if ((tipoDoc == null) || (!tipoDoc.equalsIgnoreCase("MVAVV") && !tipoDoc.equalsIgnoreCase("MVCES")
					&& !tipoDoc.equalsIgnoreCase("MVPRO") && !tipoDoc.equalsIgnoreCase("MVTRA"))) {

				if (doc.getCdnLavoratore() != null) {
					existMittente = true;
					SourceBean lav = leggiInfoLavoratore(doc.getCdnLavoratore(), tex);
					String codiceFiscale = (String) lav.getAttribute("STRCODICEFISCALE");
					String cognome = (String) lav.getAttribute("STRCOGNOME");
					String nome = (String) lav.getAttribute("STRNOME");
					// destinatario
					if (codiceAmministrazioneMittente.equals("")) {
						codiceAmministrazioneMittente = "0";
					} else {
						codiceAmministrazioneMittente = codiceAmministrazioneMittente + " - " + "0";
					}
					// CF e Nome Cognome lavoratore
					if (denominazioneMittente.equals("")) {
						denominazioneMittente = codiceFiscale + " " + cognome + " " + nome;
					} else {
						denominazioneMittente = denominazioneMittente + " - " + codiceFiscale + " " + cognome + " "
								+ nome;
					}
					// indirizzo email lavoratore
					if (lav.getAttribute("STREMAIL") != null && !lav.getAttribute("STREMAIL").toString().equals("")) {
						if (emailMittente.equals("")) {
							emailMittente = (String) lav.getAttribute("STREMAIL");
						} else {
							emailMittente = emailMittente + " - " + (String) lav.getAttribute("STREMAIL");
						}
					}
				}
			}

			if (!existMittente) {
				__profilo.setMittente(codiceAmministrazione, denominazione, email, unitaOrgID, codiceAOO);
			} else {
				__profilo.setMittente(codiceAmministrazioneMittente, denominazioneMittente, emailMittente,
						unitaOrgMittente, codiceAOOMittente);
			}
		}

		__profilo.setDocumento(idDocumento, nomeDocumento, descrizioneDocumento);
		return __profilo;
	}
}