/*
 * Created on Mar 23, 2007
 */
package it.eng.sil.bean.protocollo;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Vector;

import javax.activation.DataSource;
import javax.activation.FileDataSource;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.bean.Documento;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.endpoints.EndPoint;
import it.eng.sil.coop.wsClient.docareaProto.v161.DMPantaReiLocator;
import it.eng.sil.coop.wsClient.docareaProto.v161.DMPantaReiSoap;
import it.eng.sil.coop.wsClient.docareaProto.v161.Import;
import it.eng.sil.coop.wsClient.docareaProto.v161.ImportResponse;
import it.eng.sil.coop.wsClient.docareaProto.v161.ImportRet;
import it.eng.sil.coop.wsClient.docareaProto.v161.Login;
import it.eng.sil.coop.wsClient.docareaProto.v161.LoginResponse;
import it.eng.sil.coop.wsClient.docareaProto.v161.LoginRet;

/**
 * Protocollazione DOCAREA standard Ver. 1.6.1<br>
 * <ul>
 * <ol>
 * <li>creazione report in formato pdf senza le informazioni di protocollo TODO e se si tratta di una jpeg? <br>
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
public class RDS_ProtocolloDocAreaPantarei161 implements RegistraDocumentoStrategy {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(RDS_ProtocolloDocAreaPantarei161.class.getName());
	/**
	 * Il nome del file che verra' specificato nella profilatura xml.
	 */
	private String nomeDocumento;

	private File fileVuoto;
	/**
	 * E' il file che verra' spedito al ws. Vedere i metodi setFile();
	 */
	private File fileDaInviare;

	/**
	 * 
	 */
	public void registra(Documento doc, TransactionQueryExecutor tex) throws Exception {
		boolean creaReport = ((doc.getTempFile() == null) && (doc.getCrystalClearRelativeReportFile() != null));
		if (creaReport) {
			doc.creaReportXDOCAREA(tex);
			setFile(doc.getTempFilePreProtocollo(), doc);
		} else {
			// si sta inviando un file ricevuto dal client o nulla (documento
			// vuoto)
			setFile(doc);
		}
		inviaDocumento(doc);
		try {
			doc.rinominaFilePreProtocollo(this.fileDaInviare.getParent() + "\\" + doc.getNumProtInserito() + ".rpt");
			if (doc.inInserimento())
				doc.inserisciDocumento(tex);
			else
				// oppure aggiornamento della sola tabella AM_DOCUMENTO
				doc.aggiornaDocumento(tex);
			// il report 2 e' il report che contiene le informazioni di
			// protocollo
			if (creaReport)
				doc.creaEinserisciReport_2(tex);
			// il secondo report o il file inviato dal client viene registrato
			// in AM_DOCUMENTO_BLOB
			// N.B. Potrebbe non esserci nessun blob da inserire (caso documento
			// vuoto: empty_blob() inserito di default. Vedere procedure pl-sql
			// di inserimento del documento).
			doc.inserisciBlob(tex);
		} catch (Exception e) {
			_logger.fatal("IMPOSSIBILE INSERIRE/AGGIORNARE IL LOCALE IL DOCUMENTO PROTOCOLLATO IN DOCAREA.", e);
			_logger.fatal("Documento: " + doc.toString());
			throw new EMFUserError(EMFErrorSeverity.BLOCKING,
					MessageCodes.Protocollazione.PANTAREI_161_ERRORE_DOPO_PROTOCOLLO);
		}
	}

	/**
	 * Invia il documento a docarea. Aggiorna il numero di protocollo nel Documento. Aggiorna anno e data di protocollo
	 * nel Documento (si usa la data di sistema)
	 * 
	 * @throws Exception
	 */
	private void inviaDocumento(Documento doc) throws Exception {

		// E' il nome della libreria in cui è presente il documento oggetto
		// dell'operazione di cancellazione.
		// I valori ammissibili sono memorizzati nella tabella REMOTE_LIBRARIES
		String libraryName = "DOCAREAprotoER";
		// E' il nome utente relativo a chi sta effettuando la sessione di
		// lavoro.
		// I valori ammissibili sono nella tabella PEOPLE
		String username = "";
		// E' la password dell'utente che sta effettuando la sessione di lavoro.
		String password = "";

		String n = null;
		String urlWS = null;
		DMPantaReiLocator locator = null;
		// -- Passo 0. Creazione del WS client
		DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
		String dataSourceJndiName = dataSourceJndi.getJndi();
		locator = new DMPantaReiLocator();
		// si recupera l'url del web service da invocare
		EndPoint endPoint = new EndPoint();
		endPoint.init(dataSourceJndiName, "DOCAREA_V161Protocollazione"); // lancia
		// una
		// java.lang.Exception
		urlWS = endPoint.getUrl();
		_logger.debug("url Web Service DMPantarei: " + urlWS);
		if (urlWS == null)
			throw new Exception("url di chiamata al web service di protocolloazione DMPantarei non valido: " + urlWS);
		locator.setDMPantaReiSoapEndpointAddress(urlWS);
		// Si ottiene lo stub
		DMPantaReiSoap docAreaProtocollo = locator.getDMPantaReiSoap();
		// -- Passo 1. Login
		Login login = new Login(libraryName, username, password);
		LoginResponse loginRes = docAreaProtocollo.login(login);
		LoginRet loginRet = loginRes.getLoginResult();
		_logger.debug("Web Service Login invocato. Risultato: " + loginRet);
		if (loginRet.getLngErrNumber() != 0) {
			Vector v = new Vector();
			String msg = loginRet.getStrErrString();
			if (msg == null)
				msg = "Non ritornato";
			v.add(msg);
			throw new DOCAREAProtocolloDocumentoError(EMFErrorSeverity.BLOCKING,
					MessageCodes.Protocollazione.PANTAREI_161_ERRORE_WS, loginRet.getLngErrNumber(), v);
		}
		// -- Passo 2. Inserimento
		DataSource dsDocumento = new FileDataSource(fileDaInviare);
		DOCAREAProfiloIFace profilo = creaProfilo();
		DataSource dsProfiloXML = new ByteArrayDataSource(profilo.toString().getBytes(), "text/xml", "profilo.xml");
		Import ins = new Import(libraryName, username, loginRet.getStrDST());
		// ATTENZIONE: E' IMPORTANTE LA SEQUENZA CON CUI PASSIAMO I DUE
		// DATA-SOURCE. IL PRIMO DEVE SEMPRE ESSERE IL PROFILO XML.
		ImportResponse ir = docAreaProtocollo._import(ins, new DataSource[] { dsProfiloXML, dsDocumento });
		ImportRet insRet = ir.getImportResult();
		_logger.debug("Web Service Import invocato. Risultato: " + insRet);
		if (insRet.getLngErrNumber() != 0) {
			Vector v = new Vector();
			String msg = insRet.getStrErrString();
			if (msg == null)
				msg = "Non ritornato";
			v.add(msg);
			throw new DOCAREAProtocolloDocumentoError(EMFErrorSeverity.BLOCKING,
					MessageCodes.Protocollazione.PANTAREI_161_ERRORE_WS, insRet.getLngErrNumber(), v);
		}
		// -- Passo 3. aggiornamento dati Documento
		doc.setNumProtInserito(new BigDecimal(insRet.getLngDocID()));
		// Anno e data del sistema
		java.text.SimpleDateFormat sdf = DateUtils.getSimpleDateFormatFixBugMem("dd/MM/yyyy HH:mm");
		String dataSistema = sdf.format(new java.util.Date());
		// Anche se ho usato il metodo che fissa il problema della memoria
		// sporca nella chiamata della data di sistama, e' meglio richiamare il
		// garbage collector
		// System.gc();
		doc.setDatProtocollazione(dataSistema);
		doc.setNumAnnoProt(new BigDecimal(DateUtils.getAnno()));

	}

	/**
	 * 2 casi:<br>
	 * - si deve inviare un allegato ricevuto dal client. Si usa il file temporaneo creato dal sistema - si deve inviare
	 * un file vuoto. In questo caso bisogna creare un file temporaneo che rappresenti un documento vuoto, e lo si deve
	 * assegnare nel Documento alla proprieta' tempFilePreProtocollo. Dopo l'invia quindi verra' rinominato.
	 * 
	 * Viene impostato anche il nome del documento.
	 */
	private void setFile(Documento doc) throws Exception {
		if (doc.getTempFile() != null) {
			this.fileDaInviare = doc.getTempFile();
			this.nomeDocumento = doc.getStrNomeDoc();
			doc.setTempFilePreProtocollo(this.fileDaInviare);
		} else {
			// creo un file vuoto
			InputStream is = null;
			OutputStream os = null;
			try {
				is = new FileInputStream(this.fileVuoto);
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
				this.nomeDocumento = "Documento_Vuoto.rtf";
			} finally {
				if (os != null)
					os.close();
				if (is != null)
					is.close();
			}
		}
	}

	/**
	 * Il file da inviare e' un report(si tratta del primo report, quello senza le informazioni di protocollo).<br>
	 * Il riferimento si trova in Documento.tempFilePreProtocollo<br>
	 * Viene impostato anche il nome del documento.
	 * 
	 * @param fileReport
	 */
	private void setFile(File fileReport, Documento doc) {
		this.fileDaInviare = fileReport;
		this.nomeDocumento = doc.getStrNomeDoc();
	}

	/**
	 * Creazione del profilo xml di inserimento (operazione Import del web service DMPantarei)
	 * 
	 * @return
	 * @throws SourceBeanException
	 */
	private DOCAREAProfiloIFace creaProfilo() throws SourceBeanException {
		DMPantareiProfiloXML __profilo = null;
		// Nome del file compresa estensione
		String docName = null;
		// Ente di appartenenza del documento e dell'utente autenticato; sono
		// dei valori enumerati nella tabella GROUPS.
		String codEnte = null;
		// Tipologia del documento; sono dei valori enumerati nella tabella
		// "DOCUMENTTYPES". Deve essere fornito il valore "documento".
		String typeID = null;
		// Valore dello Stato Pantarei; sono dei valori enumerati nella tabella
		// "STATI_PANTAREI"
		// 4 indica documento protocollato
		String statoPantarei = "4";
		// Valore della Area Organizzativa Omogenea; sono dei valori enumerati
		// nella tabella "AOO". In docarea 1.2 si tratta dell'identificativo
		// dell'applicazione.
		String codAOO = null;
		SourceBean sb;
		__profilo = new DMPantareiProfiloXML();
		sb = (SourceBean) __profilo.getFilteredSourceBeanAttribute("DOC_DESCR.PROFILE_INFOS.PROFILE_INFO", "name",
				"DOCNAME");
		sb.setAttribute("value", this.nomeDocumento);
		// "DOC_DESCR.PROFILE_INFOS.PROFILE_INFO" "COD_ENTE" sara' sempre fisso
		// "DOC_DESCR.PROFILE_INFOS.PROFILE_INFO" "TYPE_ID" valore fisso
		// documento
		sb = (SourceBean) __profilo.getFilteredSourceBeanAttribute("DOC_DESCR.PROFILE_INFOS.PROFILE_INFO", "name",
				"STATO_PANTAREI");
		sb.setAttribute("value", statoPantarei);

		// "DOC_DESCR.PROFILE_INFOS.PROFILE_INFO" "COD_AOO" sara' sempre lo
		// stesso (?)

		return __profilo;
	}

	/**
	 * Classe che rappresenta il profilo xml di un documento da inviare a DOCAREA. Implementa DOCAREAProfiloIFace che ha
	 * un solo metodo (posseduto anche dalla classe SourceBean), toXML().<br>
	 * Viene caricato il file <b>WEB-INF/conf/DMPantarei161ImportProfile.xml</b>
	 */
	public static class DMPantareiProfiloXML extends SourceBean implements DOCAREAProfiloIFace {

		public DMPantareiProfiloXML() throws SourceBeanException {
			super("DOC_INFO");
			SourceBean doc = SourceBean.fromXMLFile("WEB-INF/conf/DMPantarei161ImportProfile.xml");
			setBean(doc);
		}

		public void setAttribute(String key, String value) {
		}

		public String toXML() {
			return super.toXML();
		}
	}

}
