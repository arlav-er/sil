package it.eng.sil.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.DataRow;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.DataResultInterface;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.inet.report.Area;
import com.inet.report.Element;
import com.inet.report.Engine;
import com.inet.report.Fields;
import com.inet.report.PromptField;
import com.inet.report.RDC;
import com.inet.report.ReportException;
import com.inet.report.Section;
import com.inet.report.Subreport;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import it.eng.afExt.utils.FileUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.action.report.ReportResultSet;
import it.eng.sil.bean.protocollo.RegistraDocumentoFactory;
import it.eng.sil.bean.protocollo.RegistraDocumentoStrategy;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.util.ConnessionePerReport;
import it.eng.sil.util.DBAccess;
import oracle.jdbc.OracleTypes;
import oracle.sql.BLOB;

/**
 * Contiene i dati di un documento e permette la stampa dei report. - GG gen.05: aggiunta sostituzione dinamica delle
 * immagini. - GG mar.05: modificata protocollazione: fattorizzata in metodi e usati anche in update!
 */
public class Documento implements Serializable {

	private static final long serialVersionUID = 1L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(Documento.class.getName());

	public static final String thisClassName = StringUtils.getClassName(Documento.class);

	// Costanti usate per la sostituzione dinamica dell'immagine segnaposto:
	/**
	 * Percorso (relativo) in cui sono memorizzate le immagini. ("WEB-INF/report/img")
	 */
	private static final String DYN_IMG_PATH = "WEB-INF" + File.separator + "report" + File.separator + "img"
			+ File.separator;

	/** Elenco delle estensioni delle immagini usate. */
	private static final String[] IMAGE_EXTENSION = { "gif", "jpg", "bmp", "png" };

	/** Elenco degli ID delle aree dei report. */
	private static final String[] REPORT_AREA_ID = { "RH", "PH", "D", "PF", "RF" };

	private static final String CC_REPORT_REL_PATH = "/WEB-INF/report/";

	private Vector crystalClearPrompts = null;
	private String crystalClearRelativeReportFile = null;
	private String reportFile = null;
	private File tempFile;
	private File tempFilePreProtocollo;
	// private String fileType = Engine.EXPORT_PDF; //default

	// Tabella AM_DOCUMENTO
	private BigDecimal prgDocumento;
	// progressivo di identificazione del record

	private String codCpi;
	private String descCpi;
	private BigDecimal cdnLavoratore;
	// lavoratore a cui si riferisce il documento

	private BigDecimal prgAzienda;
	// unità aziendale a cui si riferisce il documento

	private BigDecimal prgUnita;
	// unità aziendale a cui si riferisce il documento

	private String codTipoDocumento;
	// chiave esterna verso la tabella di decodifica del tipo di documento:
	// certificato medico, autocertificazione stato di disoccupazione,
	// comunicazione di avviamento, carta di identità, patente, altro, ecc.

	private String flgAutocertificazione;
	// campi flag con la possibilità di inserire S, N o Null Il flag indica se
	// il documento è un'autocertificazione

	private String strDescrizione;
	// descrizione del documento

	private String flgDocAmm;

	private String flgDocIdentifP;
	// campi flag con la possibilità di inserire S, N o Null Indica qual'è il
	// documento di identificazione del soggetto principale: usato nella stampa
	// della dichiarazione immediata disponibilità, ..

	private String datInizio;
	// data inizio validità (rilascio) del documento

	private String strNumDoc;
	// è la stringa relativa al numero del documento (ad esempio della carta di
	// identità); il campo può essere nullo perché può non avere significato per
	// tutti i doc

	private String strEnteRilascio;
	// eventuale ente che ha rilasciato il documento: per la c.i. il comune di
	// residenza, il cpi stesso per i doc. di uscita, ....

	private String codMonoIO;
	// indica se il documento è di input o di output: I, O

	private String datAcqril;
	// data di acquisizione/rilascio del documento

	private String codModalitaAcqril;
	// chiave esterna verso la modalità di acquisizione/rilascio: fax, via Web,
	// visione, ecc..

	private String codTipoFile;
	// chiave esterna verso il tipo di file: fotografia, ..

	private String strNomeDoc;
	// nome del documento: se esiste come file, se scannerizzato, ..

	private String datFine;
	// data di fine validità (scadenza) del documento

	private BigDecimal numAnnoProt;
	// anno dell'eventuale protocollazione

	private BigDecimal numProtocollo;
	// eventuale numero di protocollo

	private String strNote;
	// note legate al documento acquisito

	private BigDecimal numProtInserito = null;
	// num di protocollo realmente inserito (può essere differente da quello
	// passato in caso di concorrenza)

	private boolean protAutomatica = false;
	// indica se si sta usando la protocollazione automatica o meno (vedi
	// "lockNewNumProt()")

	private BigDecimal cdnUtIns;

	private String dtmIns;
	// data inserimento del record

	private BigDecimal cdnUtMod;

	private String dtmMod;
	// data ultima modifica del record

	private String datProtocollazione;
	// data di protocollazione

	private BigDecimal numKloDocumento;

	// Tabella AM_DOCUMENTO_BLOB
	private BigDecimal prgDocumentoBlob;

	// se true sta ad indicare che nella tabella am_documento_blob
	// esiste realmente un file
	private boolean existBlob;

	private String tipoProt = "";
	// identifica quale tipo di protocollazione si vuole effettuare:
	// - manuale
	// - automatica

	// Davide ==============================================
	// Tabella AM_DOCUMENTO_COLL

	// progressivo del documento associato ad una data camponente
	private BigDecimal prgDocumentoColl;

	// PAGE a cui associare il docuemento
	private String pagina;

	// Codice che identifica PAGE a cui associare il docuemento (tab
	// TS_COMPONENTE)
	private BigDecimal cdnComponente;

	// valore che identifica il movimento
	private String chiaveTabella;

	// descrizione dello stato movimento nel caso di documenti collegati a
	// movimenti amministrativi.
	private String strTipoDoc;

	private BigDecimal numKeyLock;
	private Engine engine;
	// Fine Davide ==========================================

	private Map promptFields = null;
	// Parametri usati nella costruzione del report con Crystal.
	// Sono qui definiti come *non* posizionali ma come coppie chiave-valore.
	// Primariamente si usa il vettore "crystalClearPrompts"

	private static boolean checkPromptsEnabled = false;

	private boolean skipSaving = false;
	// se true non salva il documento nel db

	// Stato atto
	private String CodStatoAtto = "";

	// Savino 15/09/05: campo aggiunto di recente
	private String flgAnnullamentoDocumento;
	private String codMotAnnullamentoAtto;

	// am_documento_coll.strChiaveTabella
	// chiave dell'atto collegato al documento: se non collegato il valore e'
	// '0'
	// -> non sara' mai nullo

	// COMUNQUE ANCHE SE VALE 'O' LO CONSIDERO COLLEGATO
	// n.b. parrebbe un duplicato del parametro chiaveTabella ma questo viene
	// usato con compiti diversi nella fase
	// di protocollazione
	private String strChiaveTabella = "";

	private BigDecimal prgTemplateStampa;

	private boolean inserimento;
	/**
	 * Vale true se il documento e' gia' stato protocollato su un altro sistema: quindi in fase di regitrazione il
	 * documento deve saltare la fase di lettura e regitrazione del protocollo (vale solo per la protocollazione docarea
	 * o cumunque esterna)
	 */
	private boolean protocollatoEsternamente;

	private String servizioOnLine = "";

	public Documento() {
	}

	public Documento(BigDecimal prgDocumento) throws EMFInternalError {
		setPrgDocumento(prgDocumento);
		this.select();
	}

	/**
	 * Sarebbe bello avere il codStatoAtto gia' valorizzato (sul db e' un campo not null), ma purtroppo cosi' non
	 * e'.<br>
	 * Per cui se per esempio si vuole salvare sul db un file/report accade che questa proprieta' sara' nulla. La si
	 * imposta per default a "NP". Vedi metodo lockNewNumProt().
	 * 
	 * @throws Exception
	 */
	public void checkCodStatoAtto() throws Exception {
		// Forse un giorno arriveremo qui con il CodStatoAtto valorizzato.
		// Allora se null si trattera' di un errore.
		/*
		 * if (CodStatoAtto==null || CodStatoAtto.equals("")) { throw new
		 * RuntimeException("il codstatoatto e' null o e' vuoto"); //CodStatoAtto ="NP"; }
		 */
		if (CodStatoAtto == null || CodStatoAtto.equalsIgnoreCase("")) {
			if (datProtocollazione == null || datProtocollazione.equals("")) {
				setCodStatoAtto("NP");
			} else {
				setCodStatoAtto("PR");
			}
		}
	}

	/**
	 * Recupera un nuovo numero di protocollo dalla tabella AM_DOCUMENTO, che viene usata come deposito di "sequence"
	 * relativi a ogni anno. Side effect su: - numProtInserito, - numKeyLock, - protAutomatica. Per la connessione, si
	 * chiedono i servizi del TransactionQueryExecutor dato.
	 */
	public void leggiNewNumProt(TransactionQueryExecutor txExecutor)
			throws EMFInternalError, ReportException, ProtocollaException, SQLException {

		// Imposta a true se si farà una protocollazione automatica e quindi se
		// si è preso un progressivo dalla tabella AM_PROTOCOLLO.
		// (serve per ricordarsi se la si è fatta al RITORNO dal metodo)

		this.protAutomatica = false;

		// Se è già stato assegnato un numero di protocollo (numProtInserito)
		// e questo è proprio uguale a quello ricevuto via request
		// (numProtocollo)
		// NON procedo ad assegnarne uno nuovo (nè automatico, nè manuale).
		// (nota: il "numProtInserito" viene impostato dalla "load" del
		// documento).
		if ((numProtInserito != null) && (numProtocollo != null) && numProtInserito.equals(numProtocollo)) {

			// nulla da fare, tengo la protocollazione che ho già

		} else {
			// ALTRIMENTI procedo con la normale protocollazione
			// (automatica/manuale)

			// ------------------------- PROTOCOLLAZIONE
			// ('LOCK')------------------------------------------
			// Se sono valorizzati "num.protocollo" e "anno protocollo"
			if ((this.numProtocollo != null) && (this.numAnnoProt != null)) {

				// Se si vuole fare una protocollazione automatica
				if (this.tipoProt.equalsIgnoreCase("S")) {

					this.protAutomatica = true; // Si!

					// In caso di protocollazione automatica, le seguenti
					// operazioni vanno eseguite prima di ogni altra,
					// in modo da poter gestire la concorrenza. In questo modo
					// infatti, usiamo il LOCK ottimistico per
					// "bloccare" l'aggiornamento della tabella AM_PROTOCOLLO.
					// Evitiamo così che due documenti
					// abbiano lo stesso numero di protocollo.
					Object[] objS = new Object[1];
					objS[0] = this.numAnnoProt;
					SourceBean rowsS = (SourceBean) txExecutor.executeQuery("GET_KEYLOCK_NUMPR", objS, "SELECT");

					if (rowsS != null) {
						// Se trovato, recupero num prot. corrente
						this.numKeyLock = SourceBeanUtils.getAttrBigDecimal(rowsS, "ROW.NUMKLOPROTOCOLLO");
						this.numProtInserito = SourceBeanUtils.getAttrBigDecimal(rowsS, "ROW.NUMPROTOCOLLO");
						// DOCAREA: aggiunta assegnazione
						this.numProtocollo = this.numProtInserito;
					} else {
						// Non esiste alcun record associato a quell'anno
						// E' il primo documento che protocolliamo in
						// quell'anno:
						// inseriamo il nuovo record con protocollo "1".
						Object[] objI = new Object[3];

						this.numKeyLock = new BigDecimal(1D);
						this.numProtInserito = new BigDecimal(1D);

						objI[0] = this.numKeyLock;
						objI[1] = this.numProtInserito;
						objI[2] = this.numAnnoProt;
						Boolean resultI = (Boolean) txExecutor.executeQuery("INSERT_AM_PROTOCOLLO", objI, "INSERT");
						if (!resultI.booleanValue()) {
							throw new EMFInternalError(EMFErrorSeverity.BLOCKING,
									"inserimento INSERT_AM_PROTOCOLLO non possibile");
						}
					}

					// Si aggiorna il numero di protocollo nel parametro del
					// report in caso
					// di passaggio per nome se di e' scelto il passaggio per
					// vettore e'
					// impossibile sapere in quale posizione si trovi per cui
					// nella stampa
					// risultera' il numero di protocollo visualizzato nella
					// maschera di stampa.

					/**
					 * ---------- non dovrebbe piu' servire -------------------------
					 * 
					 * 
					 * if (promptFields != null) { promptFields.put("numProt", this.numProtInserito.toString()); } else
					 * if (engine != null) { // (vedansi "apipatto") try { checkPromptFieldsName("numProt", engine);
					 * engine.setPrompt("numProt", this.numProtInserito.toString()); } catch (IllegalArgumentException
					 * e) { } // ignoro se non presente }
					 * -----------------------------------------------------------------
					 */
				} // if protAutomatica
				else {
					// Protocollazione MANUALE
					// viene controllato il numero di protocollo passato
					// dall'utente. Se non va bene viene lanciata una eccezione.
					numProtInserito = protocollaDoc(txExecutor.getDataConnection(), numProtocollo, numAnnoProt,
							datProtocollazione, tipoProt);
				}

			} // if valorizzati num e anno protocollo

		}

		// Quando si effettua l'inserimento o l'update di un documento e si
		// sceglie di protocollarlo
		// (protocollazione manuale o automatica) bisogna SEMPRE settare il
		// codstatoatto = PR,
		// altrimenti bisogna settare il codstatoatto = NP
		/**
		 * non dovrebbe piu' servire if(CodStatoAtto == null || CodStatoAtto.equalsIgnoreCase("") ) {
		 * if(datProtocollazione == null ||datProtocollazione.equals("")){ setCodStatoAtto("NP"); }else{
		 * setCodStatoAtto("PR"); } }
		 */
	}

	/**
	 * Rende definitiva la protocollazione del documento. Viene aggiornato il numero di protocollo in AM_PROTOCOLLO.
	 * 
	 * Per la connessione, si usa il TransactionQueryExecutor passato.
	 */
	public void unlockNewNumProt(TransactionQueryExecutor txExecutor) throws EMFInternalError {

		// ------------------------- PROTOCOLLAZIOE
		// ('UNLOCK')------------------------------------------
		// Se prima (nella "lock") si è fatta una protocollazione automatica,
		if (this.protAutomatica) {

			// Aggiornamento in AM_PROTOCOLLO del num di protocolo
			// Questa operazione DEVE essere eseguita per ultima, per evitare
			// problemi in caso di concorrenza
			Object[] obj = new Object[3];

			obj[0] = this.numProtInserito;
			obj[1] = this.numKeyLock.add(new BigDecimal(1D)); // aggiunge "+1"
			obj[2] = this.numAnnoProt;

			Boolean result = (Boolean) txExecutor.executeQuery("UPDATE_AM_PROTOCOLLO", obj, "UPDATE");
			if (!result.booleanValue()) {
				throw new EMFInternalError(EMFErrorSeverity.BLOCKING,
						"Impossibile aggiornare AM_PROTOCOLLO, probabile concorrenza.");
			}
		}
	}

	// ================================== PROTOCOLLAZIONE
	// =======================================
	/**
	 * Protocollazione manuale. Controlla che il numero di protocollo passato dall'utente sia ammissibile per il
	 * sistema. La procedura chiamata fa' un aggiornamento della tabella am_protocollo ma solo in caso di
	 * protocollazione automatica: ma questo metodo viene chiamato solo nel caso di protocollazione manuale. Il nome del
	 * metodo andrebbe cambiato.
	 * 
	 * @throws ProtocollaException
	 *             se il numero di protocollo non e' utilizzabile.
	 */
	public BigDecimal protocollaDoc(DataConnection conn, BigDecimal numPR, BigDecimal numAnnoPR, String datProt,
			String tipoPR) throws ProtocollaException, SQLException {

		Connection connection = conn.getInternalConnection();

		System.out.println(conn.toString());

		CallableStatement stmt = connection.prepareCall("{? = call pg_ido.protocolla (?, ?, ?, ?) }");

		stmt.registerOutParameter(1, OracleTypes.NUMBER);
		stmt.setString(2, numPR.toString());
		stmt.setString(3, numAnnoPR.toString());
		stmt.setString(4, datProt);
		stmt.setString(5, tipoPR);
		stmt.execute();

		BigDecimal numProtIns = stmt.getBigDecimal(1);

		int returnSPvalue = numProtIns.intValue();
		if (returnSPvalue < 0) {
			_logger.fatal(
					"Impossibile protocollare il documento: numProt=" + numPR + ", anno=" + numAnnoPR + ", datProt="
							+ datProt + ", tipo=" + tipoPR + "\n\r codice restituito dalla procedure:" + returnSPvalue);

			throw new ProtocollaException(returnSPvalue);
		}

		return numProtIns;
	}

	// =========================================================================================

	/**
	 * Metodo di test per report generati tramite api RDC
	 */
	public void insert(Engine engine) throws Exception {
		_logger.debug(thisClassName + "::insert() CALLED...");

		setEngine(engine);
		insert();
	}

	public void insert() throws Exception {
		_logger.debug(thisClassName + "::insert() CALLED...");

		TransactionQueryExecutor txExecutor = null;
		try {
			txExecutor = new TransactionQueryExecutor(Values.DB_SIL_DATI);
			txExecutor.initTransaction();
			insert(txExecutor);
			txExecutor.commitTransaction();
		} catch (Exception e) {
			txExecutor.rollBackTransaction();
			throw e;
		}
	}

	public void insert(TransactionQueryExecutor txExecutor) throws Exception {
		_logger.debug(thisClassName + "::insert() CALLED...");

		Monitor monitor = null;
		monitor = MonitorFactory.start("ProtocolloDocumento: [ insert ]");
		this.inserimento = true;
		// TODO Quando verra' impostato il codstatoatto in tutti i reports?
		checkCodStatoAtto();
		try {
			RegistraDocumentoStrategy numeroProtocollo = RegistraDocumentoFactory.getStrategy(this, txExecutor);
			numeroProtocollo.registra(this, txExecutor);
		} // Gestisco gli errori emersi in tutte le fasi di inserimento
		catch (Exception emf) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					thisClassName + "::insert() Errore durante la transazione di INSERT", emf);

			throw emf;
		} finally {
			monitor.stop();
		}
	}

	/**
	 * Se esiste un report lo genera. Se esiste un file lo registra nella am_documento_blob
	 * 
	 * @param tex
	 * @throws Exception
	 */
	public void creaEinserisciReport(TransactionQueryExecutor tex) throws Exception {

		if ((this.tempFile == null) && (this.crystalClearRelativeReportFile != null)) {
			// Esiste un report. Va generato
			// Bisogna prima aggiornare le informazioni di protocollo (numero
			// anno e data)
			aggiornaNumProtocolloReport(engine);
			createReportTempFile(tex);
		}
		if (this.tempFile != null) {
			// Patch per posticipare il lock protocollo
			if (!isProtPosticipated()) {
				saveBLOB(tex.getDataConnection());
			}
		}
	}

	/**
	 * Comprendi se la protocollazione è da posticipare. Patch Franco Vuoto e Alessandro Pegoraro del 6/11/07
	 * 
	 * @return true se la protocollazione del documento è da posticipare
	 */
	public boolean isProtPosticipated() {
		/* se il documento ancora non esiste, inutile continuare */
		if (this.getStrNomeDoc() == null)
			return false;
		/* Solo pdf */
		if (this.getFileExtension().compareToIgnoreCase("pdf") != 0)
			return false;
		/* Solo protocollazione INTERNA, altrimenti non ha senso */
		if (this.protocollatoEsternamente())
			return false;

		/* Nel caso delle Stampe Parametriche la Protocollazione non deve essere posticipata */
		if (this.getPrgTemplateStampa() != null) {
			return false;
		}

		/* Lista stampe modificate */
		if (this.getCodTipoDocumento().equalsIgnoreCase("SAP"))
			return true;
		if (this.getCodTipoDocumento().equalsIgnoreCase("IM"))
			return true;
		if (this.getCodTipoDocumento().equalsIgnoreCase("LTC"))
			return true;
		if (this.getCodTipoDocumento().equalsIgnoreCase("SSL"))
			return true;
		if (this.getCodTipoDocumento().equalsIgnoreCase("SSLP"))
			return true;
		if (this.getCodTipoDocumento().equalsIgnoreCase("TRDOC"))
			return true;
		if (this.getCodTipoDocumento().equalsIgnoreCase("TRCPI"))
			return true;
		if (this.getCodTipoDocumento().equalsIgnoreCase(Properties.DEFAULT_DOCUMENTO_PATTO)
				|| this.getCodTipoDocumento().equalsIgnoreCase(Properties.DOCUMENTO_PATTO_GENERICO))
			return true;
		if (this.getCodTipoDocumento().equalsIgnoreCase("CUL"))
			return true;
		if (this.getCodTipoDocumento().equalsIgnoreCase("CUE"))
			return true;
		if (this.getCodTipoDocumento().equalsIgnoreCase("CUA"))
			return true;
		if (this.getCodTipoDocumento().equalsIgnoreCase("DICH"))
			return true;
		if (this.getCodTipoDocumento().equalsIgnoreCase("IMDICANN"))
			return true;

		return false; // default
	}

	/**
	 * Vengono inseriti un record in AM_DOCUMENTO ,uno in AM_DOCUMENTO_COLL e uno in AM_DOCUMENTO_BLOB (si usa un
	 * empty_blob) Si aggiornano le chiavi del documento (prgDocumento, prgDocumentoColl, prgDocumentoBlob)
	 * 
	 * @param tex
	 * @throws Exception
	 */
	public void inserisciDocumento(TransactionQueryExecutor tex) throws Exception {
		BigDecimal prgDocOut = null;
		BigDecimal prgDocBlobOut = null;
		BigDecimal prgDocCollOut = null;
		BigDecimal ris = null;
		// TODO DOCAREA controlla se questa impostazione copiata dal vecchio
		// codice e' corretta.
		if (this.chiaveTabella == null || this.chiaveTabella.equals("")) {
			this.chiaveTabella = "0";
		}
		Connection connection = tex.getDataConnection().getInternalConnection();
		CallableStatement stmt = connection.prepareCall(
				"{? = call pg_gestamm.insertDocumento (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }");
		stmt.registerOutParameter(1, OracleTypes.NUMBER);
		stmt.registerOutParameter(2, OracleTypes.NUMBER);
		stmt.setString(3, codCpi);
		stmt.setBigDecimal(4, cdnLavoratore);
		stmt.setBigDecimal(5, prgAzienda);
		stmt.setBigDecimal(6, prgUnita);
		stmt.setString(7, codTipoDocumento);
		stmt.setString(8, flgAutocertificazione);
		stmt.setString(9, strDescrizione);
		stmt.setString(10, flgDocAmm);
		stmt.setString(11, flgDocIdentifP);
		stmt.setString(12, datInizio);
		stmt.setString(13, strNumDoc);
		stmt.setString(14, strEnteRilascio);
		stmt.setString(15, codMonoIO);
		stmt.setString(16, datAcqril);
		stmt.setString(17, codModalitaAcqril);
		stmt.setString(18, codTipoFile);
		stmt.setString(19, strNomeDoc);
		stmt.setString(20, datFine);
		// TODO savino DOCAREAProto
		stmt.setBigDecimal(21, numAnnoProt);
		stmt.setBigDecimal(22, numProtInserito);
		stmt.setString(23, strNote);
		stmt.setBigDecimal(24, cdnUtIns);
		stmt.setBigDecimal(25, cdnUtMod);
		stmt.setString(26, datProtocollazione);
		stmt.setString(27, CodStatoAtto);
		stmt.registerOutParameter(28, OracleTypes.NUMBER);
		stmt.setString(29, this.pagina);
		stmt.registerOutParameter(30, OracleTypes.NUMBER);
		stmt.setString(31, this.chiaveTabella);

		stmt.execute();
		// la risposta della stored e' sempre 0. Se si verifica un errore viene
		// lanciata una eccezione.
		ris = stmt.getBigDecimal(1);
		prgDocOut = stmt.getBigDecimal(2);
		prgDocBlobOut = stmt.getBigDecimal(28);

		setPrgDocumento(prgDocOut);
		setPrgDocumentoBlob(prgDocBlobOut);

		if (this.pagina != null && this.pagina.length() > 0) {
			prgDocCollOut = stmt.getBigDecimal(30);
			setPrgDocumentoColl(prgDocCollOut);
		}

	}

	private void aggiornaNumProtocolloReport(Engine e) throws ReportException {
		// TODO Andrea: DOCAREA: l'aggiornamento del n. prot. nel report deve
		// avvenire solo se si sta protocollando
		if ("PR".equals(CodStatoAtto)) {

			String lNumProtInserito = (this.numProtInserito == null) ? "" : String.valueOf(this.numProtInserito);
			String lNumAnnoProt = (this.numAnnoProt == null) ? "" : String.valueOf(this.numAnnoProt);

			if (promptFields != null) { // TODO Docarea: aggiorno nel report
										// anche l'anno di protocollo
				promptFields.put("numProt", lNumProtInserito);
				promptFields.put("numAnnoProt", lNumAnnoProt);
				promptFields.put("dataProt", this.datProtocollazione);
			} else if (engine != null) { // (vedansi "apipatto")
				try {
					checkPromptFieldsName("numProt", e);
					e.setPrompt("numProt", lNumProtInserito);
					e.setPrompt("numAnnoProt", lNumAnnoProt);
					// promptFields.put("dataProt", this.datProtocollazione);
				} catch (IllegalArgumentException iae) {
				} // ignoro se non presente
			}
		}
	}

	/**
	 * Prende come parametro un'oggetto TransactionQueryExecutor, in modo da poter utilizzare il metodo update
	 * all'interno di una transazione.
	 */
	public void update(TransactionQueryExecutor texec) throws Exception {

		try {

			this.inserimento = false;
			// TODO Quando verra' impostato il codstatoatto in tutti i reports?
			checkCodStatoAtto();
			RegistraDocumentoStrategy storeDocumento = RegistraDocumentoFactory.getStrategy(this, texec);
			storeDocumento.registra(this, texec);

			/*
			 * // 1) Recupero e imposto i campi relativi alla protocollazione. // TODO DOCAREA bisogna istanziare la
			 * classe che nel caso id docarea non deve fare niente //lockNewNumProt(texec); ProtocolloDocumentoIFace
			 * numeroProtocollo = ProtocolloDocumentoFactory.crea(this, texec); numeroProtocollo.init(this, texec);
			 * setNumProtocollo(numeroProtocollo.getNumero()); // 2a) Faccio l'UPDATE del record col documento Object[]
			 * objD = new Object[27];
			 * 
			 * objD[0] = codCpi; objD[1] = cdnLavoratore; objD[2] = prgAzienda; objD[3] = prgUnita; objD[4] =
			 * codTipoDocumento; objD[5] = flgAutocertificazione; objD[6] = strDescrizione;
			 * 
			 * objD[7] = flgDocAmm; objD[8] = flgDocIdentifP; objD[9] = datInizio; objD[10] = strNumDoc; objD[11] =
			 * strEnteRilascio; objD[12] = codMonoIO; objD[13] = datAcqril; objD[14] = codModalitaAcqril; objD[15] =
			 * codTipoFile;
			 * 
			 * objD[16] = strNomeDoc; objD[17] = datFine; objD[18] = numAnnoProt; objD[19] = numProtInserito; // NB!
			 * objD[20] = strNote; objD[21] = cdnUtMod; objD[22] = datProtocollazione; objD[23] = numKloDocumento;
			 * objD[24] = CodStatoAtto; // Savino 15/09/05 aggiunto campo codMotAnnullamentoAtto (in fase di inserimento
			 * non e' necessario) objD[25] = codMotAnnullamentoAtto; objD[26] = prgDocumento;
			 * 
			 * texec.executeQuery("UPDATE_AM_DOCUMENTO", objD, "UPDATE"); // 2b) Creazione report in un file temporaneo
			 * e salvataggio come BLOB if (this.tempFile != null) { saveBLOB(texec.getDataConnection()); } // 3) Parte
			 * finale della protocollazione (aggiorno chiave su DB) // TODO vedi sopra //unlockNewNumProt(texec);
			 * numeroProtocollo.registra();
			 */
		} catch (Exception emf) {
			// Gestisco gli errori emersi in tutte le fasi di inserimento
			_logger.fatal(thisClassName + "::update() Errore durante la transazione di UPDATE" + emf.toString());

			throw emf;
		}
	}

	public void aggiornaDocumento(TransactionQueryExecutor tex) throws Exception {
		Object[] objD = new Object[27];

		objD[0] = codCpi;
		objD[1] = cdnLavoratore;
		objD[2] = prgAzienda;
		objD[3] = prgUnita;
		objD[4] = codTipoDocumento;
		objD[5] = flgAutocertificazione;
		objD[6] = strDescrizione;

		objD[7] = flgDocAmm;
		objD[8] = flgDocIdentifP;
		objD[9] = datInizio;
		objD[10] = strNumDoc;
		objD[11] = strEnteRilascio;
		objD[12] = codMonoIO;
		objD[13] = datAcqril;
		objD[14] = codModalitaAcqril;
		objD[15] = codTipoFile;

		objD[16] = strNomeDoc;
		objD[17] = datFine;
		objD[18] = numAnnoProt;
		objD[19] = numProtInserito; // NB!
		objD[20] = strNote;
		objD[21] = cdnUtMod;
		objD[22] = datProtocollazione;
		objD[23] = numKloDocumento;
		objD[24] = CodStatoAtto;
		// Savino 15/09/05 aggiunto campo codMotAnnullamentoAtto (in fase di
		// inserimento non e' necessario)
		objD[25] = codMotAnnullamentoAtto;
		objD[26] = prgDocumento;

		tex.executeQuery("UPDATE_AM_DOCUMENTO", objD, "UPDATE");
	}

	/**
	 * Metodo che assegna all'attributo indirizzoDetail il valore del sourceBean risultato della query di dettaglio del
	 * sito ed inoltre valorizza tutti gli attributi del sito
	 */
	public void select() throws EMFInternalError {
		_logger.debug(thisClassName + "::select() CALLED...");

		DataConnection conn = null;
		try {
			DBAccess dbaccess = new DBAccess();
			conn = dbaccess.getConnection(Values.DB_SIL_DATI);

			loadDocument(conn);
		} catch (EMFInternalError xe) {
			throw xe;
		} finally {
			Utils.releaseResources(conn, null, null);
		}
	}

	public void selectStampaParam() throws EMFInternalError {
		_logger.debug(thisClassName + "::select() CALLED...");

		DataConnection conn = null;
		try {
			DBAccess dbaccess = new DBAccess();
			conn = dbaccess.getConnection(Values.DB_SIL_DATI);

			loadDocumentStampaParam(conn);
		} catch (EMFInternalError xe) {
			throw xe;
		} finally {
			Utils.releaseResources(conn, null, null);
		}
	}
	
	public void selectStampaParam(DataConnection conn) throws EMFInternalError {
		_logger.debug(thisClassName + "::select() CALLED...");
		try {
			loadDocumentStampaParam(conn);
		} catch (EMFInternalError xe) {
			throw xe;
		}
	}

	public void selectExtBlob() throws EMFInternalError {
		_logger.debug(thisClassName + "::select() CALLED...");

		DataConnection conn = null;
		try {
			DBAccess dbaccess = new DBAccess();
			conn = dbaccess.getConnection(Values.DB_SIL_DATI);

			loadDocument(conn);
			readBLOB(conn);
		} catch (EMFInternalError xe) {
			throw xe;
		} catch (IOException ex) {
			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, ex);
		} catch (Exception ex) {
			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, ex);
		} finally {
			Utils.releaseResources(conn, null, null);
		}
	}

	private void loadDocument(DataConnection conn /*
													 * , SQLCommand selectCommand, DataResult dr
													 */
	) throws EMFInternalError {
		String stmt = SQLStatements.getStatement("AM_DOCUMENTO_SELECT");
		SQLCommand selectCommand = conn.createSelectCommand(stmt);

		ArrayList inputParameters = new ArrayList(1);
		inputParameters.add(conn.createDataField("prgDocumento", Types.BIGINT, prgDocumento));

		DataResult dr = (DataResult) selectCommand.execute(inputParameters);

		// Verifica che il risultato sia un resultset
		if (dr.getDataResultType().equals(DataResultInterface.SCROLLABLE_DATA_RESULT)) {
			ScrollableDataResult sdr = (ScrollableDataResult) dr.getDataObject();

			DataRow drow = sdr.getDataRow();

			this.setPrgDocumento((BigDecimal) drow.getColumn("prgDocumento").getObjectValue());

			this.setCodCpi((String) drow.getColumn("codcpi").getObjectValue());
			this.setCdnLavoratore((BigDecimal) drow.getColumn("cdnlavoratore").getObjectValue());
			this.setPrgAzienda((BigDecimal) drow.getColumn("prgazienda").getObjectValue());
			this.setPrgUnita((BigDecimal) drow.getColumn("prgunita").getObjectValue());
			this.setCodTipoDocumento((String) drow.getColumn("codtipodocumento").getObjectValue());
			this.setFlgAutocertificazione((String) drow.getColumn("flgautocertificazione").getObjectValue());
			this.setStrDescrizione((String) drow.getColumn("strdescrizione").getObjectValue());
			this.setFlgDocAmm((String) drow.getColumn("flgdocamm").getObjectValue());
			this.setFlgDocIdentifP((String) drow.getColumn("flgdocidentifp").getObjectValue());
			this.setDatInizio((String) drow.getColumn("datinizio").getObjectValue());
			this.setStrNumDoc((String) drow.getColumn("strnumdoc").getObjectValue());
			this.setStrEnteRilascio((String) drow.getColumn("strenterilascio").getObjectValue());
			this.setCodMonoIO((String) drow.getColumn("codmonoio").getObjectValue());
			this.setDatAcqril((String) drow.getColumn("datacqril").getObjectValue());
			this.setCodModalitaAcqril((String) drow.getColumn("codmodalitaacqril").getObjectValue());
			this.setCodTipoFile((String) drow.getColumn("codtipofile").getObjectValue());
			this.setStrNomeDoc((String) drow.getColumn("strnomedoc").getObjectValue());
			this.setDatFine((String) drow.getColumn("datfine").getObjectValue());
			this.setNumAnnoProt((BigDecimal) drow.getColumn("numannoprot").getObjectValue());
			this.setNumProtocollo((BigDecimal) drow.getColumn("numprotocollo").getObjectValue());

			// GG 9-3-05: in caso di caricamento, ricopio il NUMPROTOCOLLO anche
			// in "numProtInserito"
			this.numProtInserito = (BigDecimal) drow.getColumn("numprotocollo").getObjectValue();

			this.setStrNote((String) drow.getColumn("strnote").getObjectValue());
			this.setCdnUtIns((BigDecimal) drow.getColumn("cdnutins").getObjectValue());
			this.setDtmIns((String) drow.getColumn("dtmins").getObjectValue());
			this.setCdnUtMod((BigDecimal) drow.getColumn("cdnutmod").getObjectValue());
			this.setDtmMod((String) drow.getColumn("dtmmod").getObjectValue());
			this.setNumKloDocumento((BigDecimal) drow.getColumn("numklodocumento").getObjectValue());
			this.setPrgDocumentoBlob((BigDecimal) drow.getColumn("prgdocumentoblob").getObjectValue());
			this.setExistBlob((BigDecimal) drow.getColumn("existBlob").getObjectValue());
			this.setDatProtocollazione((String) drow.getColumn("DATPROTOCOLLO").getObjectValue());
			this.setStrTipoDoc((String) drow.getColumn("STRTIPODOC").getObjectValue());
			this.setCodStatoAtto((String) drow.getColumn("CODSTATOATTO").getObjectValue());
			// Savino 15/09/05: aggiunto campo codMotAnnullamentoAtto
			// (am_documento)
			this.setCodMotAnnullamentoAtto((String) drow.getColumn("codMotAnnullamentoAtto").getObjectValue());
			// Savino 19/09/05: aggiunto campo flgAnnullamentoDocumento
			// (de_doc_tipo)
			this.flgAnnullamentoDocumento = (String) drow.getColumn("flgAnnullDaDocumento").getObjectValue();
			// Savino 15/09/05: riprendo il valore di
			// am_documento_coll.strChiaveTabella
			this.setStrChiaveTabella((String) drow.getColumn("strChiaveTabella").getObjectValue());
			sdr.close();
		} else {
			// Il risultato non è un resultset
			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "Il result set della query '" + stmt
					+ "' non è di tipo " + DataResultInterface.SCROLLABLE_DATA_RESULT);
		}

		Utils.releaseResources(null, selectCommand, dr);
	}

	private void loadDocumentStampaParam(DataConnection conn) throws EMFInternalError {
		String stmt = SQLStatements.getStatement("AM_DOCUMENTO_SELECT_STAMPA_PARAM");
		SQLCommand selectCommand = conn.createSelectCommand(stmt);

		ArrayList inputParameters = new ArrayList(1);
		inputParameters.add(conn.createDataField("prgDocumento", Types.BIGINT, prgDocumento));

		DataResult dr = (DataResult) selectCommand.execute(inputParameters);

		// Verifica che il risultato sia un resultset
		if (dr.getDataResultType().equals(DataResultInterface.SCROLLABLE_DATA_RESULT)) {
			ScrollableDataResult sdr = (ScrollableDataResult) dr.getDataObject();
			
			DataRow drow = null;
			int nroRows = sdr.getRowsNumber(); // imposto il numero delle righe da prelevare
			int i = 1; // ciclo da uno al numero di righe
			
			drow = sdr.getDataRow(i);
			
			this.setPrgDocumento((BigDecimal) drow.getColumn("prgDocumento").getObjectValue());

			this.setCodCpi((String) drow.getColumn("codcpi").getObjectValue());
			this.setDescCpi((String) drow.getColumn("desccpi").getObjectValue());
			this.setCdnLavoratore((BigDecimal) drow.getColumn("cdnlavoratore").getObjectValue());
			this.setPrgAzienda((BigDecimal) drow.getColumn("prgazienda").getObjectValue());
			this.setPrgUnita((BigDecimal) drow.getColumn("prgunita").getObjectValue());
			this.setCodTipoDocumento((String) drow.getColumn("codtipodocumento").getObjectValue());
			this.setFlgAutocertificazione((String) drow.getColumn("flgautocertificazione").getObjectValue());
			this.setStrDescrizione((String) drow.getColumn("strdescrizione").getObjectValue());
			this.setFlgDocAmm((String) drow.getColumn("flgdocamm").getObjectValue());
			this.setFlgDocIdentifP((String) drow.getColumn("flgdocidentifp").getObjectValue());
			this.setDatInizio((String) drow.getColumn("datinizio").getObjectValue());
			this.setStrNumDoc((String) drow.getColumn("strnumdoc").getObjectValue());
			this.setStrEnteRilascio((String) drow.getColumn("strenterilascio").getObjectValue());
			this.setCodMonoIO((String) drow.getColumn("codmonoio").getObjectValue());
			this.setDatAcqril((String) drow.getColumn("datacqril").getObjectValue());
			this.setCodModalitaAcqril((String) drow.getColumn("codmodalitaacqril").getObjectValue());
			this.setCodTipoFile((String) drow.getColumn("codtipofile").getObjectValue());
			this.setStrNomeDoc((String) drow.getColumn("strnomedoc").getObjectValue());
			this.setDatFine((String) drow.getColumn("datfine").getObjectValue());
			this.setNumAnnoProt((BigDecimal) drow.getColumn("numannoprot").getObjectValue());
			this.setNumProtocollo((BigDecimal) drow.getColumn("numprotocollo").getObjectValue());

			// GG 9-3-05: in caso di caricamento, ricopio il NUMPROTOCOLLO anche
			// in "numProtInserito"
			this.numProtInserito = (BigDecimal) drow.getColumn("numprotocollo").getObjectValue();

			this.setStrNote((String) drow.getColumn("strnote").getObjectValue());
			this.setCdnUtIns((BigDecimal) drow.getColumn("cdnutins").getObjectValue());
			this.setDtmIns((String) drow.getColumn("dtmins").getObjectValue());
			this.setCdnUtMod((BigDecimal) drow.getColumn("cdnutmod").getObjectValue());
			this.setDtmMod((String) drow.getColumn("dtmmod").getObjectValue());
			this.setNumKloDocumento((BigDecimal) drow.getColumn("numklodocumento").getObjectValue());
			this.setExistBlob((BigDecimal) drow.getColumn("existBlob").getObjectValue());
			this.setDatProtocollazione((String) drow.getColumn("DATPROTOCOLLO").getObjectValue());
			this.setStrTipoDoc((String) drow.getColumn("STRTIPODOC").getObjectValue());
			this.setCodStatoAtto((String) drow.getColumn("CODSTATOATTO").getObjectValue());
			// Savino 15/09/05: aggiunto campo codMotAnnullamentoAtto
			// (am_documento)
			this.setCodMotAnnullamentoAtto((String) drow.getColumn("codMotAnnullamentoAtto").getObjectValue());
			// Savino 19/09/05: aggiunto campo flgAnnullamentoDocumento
			// (de_doc_tipo)
			this.flgAnnullamentoDocumento = (String) drow.getColumn("flgAnnullDaDocumento").getObjectValue();
			this.setPrgTemplateStampa((BigDecimal) drow.getColumn("prgtemplatestampa").getObjectValue());
			this.setStrChiaveTabella((String) drow.getColumn("strChiaveTabella").getObjectValue());
			
			sdr.close();
		} else {
			// Il risultato non è un resultset
			throw new EMFInternalError(EMFErrorSeverity.BLOCKING, "Il result set della query '" + stmt
					+ "' non è di tipo " + DataResultInterface.SCROLLABLE_DATA_RESULT);
		}

		Utils.releaseResources(null, selectCommand, dr);
	}

	private void saveBLOB(DataConnection conn) throws Exception {

		SQLCommand selectCommand = null;
		DataResult dr = null;
		OutputStream outStream = null;
		InputStream inStream = null;
		try {
			String stmt = SQLStatements.getStatement("AM_DOCUMENTO_BLOB_UPLOAD");
			selectCommand = conn.createSelectCommand(stmt);

			ArrayList inputParameters = new ArrayList(1);
			inputParameters.add(conn.createDataField("prgDocumento", Types.BIGINT, this.prgDocumento));

			dr = selectCommand.execute(inputParameters);

			ScrollableDataResult sdr = (ScrollableDataResult) dr.getDataObject();

			DataField df = sdr.getDataRow().getColumn("BLBFILE");
			BLOB resultBlob = (BLOB) df.getObjectValue();

			outStream = resultBlob.getBinaryOutputStream();

			// Buffer to hold chunks of data to being written to the Blob.
			int chunk = resultBlob.getChunkSize();
			byte[] buffer = new byte[chunk];

			// Read a chunk of data from the sample file input stream,
			// and write the chunk to the Blob column output stream.
			// Repeat till file has been fully read.
			int nread = 0; // Number of bytes read
			inStream = new FileInputStream(this.tempFile);

			while ((nread = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, nread); // Write to Blob
			}
			outStream.flush();
		} catch (Exception e) {
			throw e; // Rilancia qualsiasi eccezione
		} finally {
			// Rilascia eventuali risorse allocate
			if (inStream != null) {
				try {
					inStream.close();
				} catch (Exception e) {
				}
			}
			if (outStream != null) {
				try {
					outStream.close();
				} catch (Exception e) {
				}
			}
			Utils.releaseResources(null, selectCommand, dr);
		}
	}

	public void readBLOB(DataConnection conn) throws Exception {

		// lettura da DB
		SQLCommand selectCommand = null;
		DataResult dr = null;
		OutputStream outStream = null;
		InputStream inStream = null;
		try {
			String stmt = SQLStatements.getStatement("DOCUMENTI_DOWNLOAD");
			selectCommand = conn.createSelectCommand(stmt);

			ArrayList inputParameters = new ArrayList(1);
			inputParameters.add(conn.createDataField("prgDocumento", Types.BIGINT, this.prgDocumento));

			dr = selectCommand.execute(inputParameters);

			ScrollableDataResult sdr = (ScrollableDataResult) dr.getDataObject();

			DataField df = sdr.getDataRow().getColumn("BLOBDOCUMENTO");
			BLOB resultBlob = (BLOB) df.getObjectValue();

			inStream = resultBlob.getBinaryStream();

			int chunk = resultBlob.getChunkSize();
			byte[] buffer = new byte[chunk];
			int length;

			this.tempFile = File.createTempFile("~rpt", ".out");

			outStream = new FileOutputStream(tempFile);

			// Fetch data
			while ((length = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, length);
			}
		} catch (Exception e) {
			throw e; // Rilancia qualsiasi eccezione
		} finally {
			// Rilascia eventuali risorse allocate
			if (inStream != null) {
				try {
					inStream.close();
				} catch (Exception e) {
				}
			}
			if (outStream != null) {
				try {
					outStream.close();
				} catch (Exception e) {
				}
			}
			Utils.releaseResources(null, selectCommand, dr);
		}
	}

	public BigDecimal getCdnLavoratore() {
		return cdnLavoratore;
	}

	public BigDecimal getCdnUtIns() {
		return cdnUtIns;
	}

	public BigDecimal getCdnUtMod() {
		return cdnUtMod;
	}

	public String getCodCpi() {
		return codCpi;
	}

	public String getDescCpi() {
		return descCpi;
	}

	public String getCodModalitaAcqril() {
		return codModalitaAcqril;
	}

	public String getCodMonoIO() {
		return codMonoIO;
	}

	public String getCodTipoDocumento() {
		return codTipoDocumento;
	}

	public String getCodTipoFile() {
		return codTipoFile;
	}

	public String getDatAcqril() {
		return datAcqril;
	}

	public String getDatFine() {
		return datFine;
	}

	public String getDatInizio() {
		return datInizio;
	}

	public String getDtmIns() {
		return dtmIns;
	}

	public String getDtmMod() {
		return dtmMod;
	}

	public String getFlgAutocertificazione() {
		return flgAutocertificazione;
	}

	public String getFlgDocAmm() {
		return flgDocAmm;
	}

	public String getFlgDocIdentifP() {
		return flgDocIdentifP;
	}

	public BigDecimal getNumAnnoProt() {
		return numAnnoProt;
	}

	public BigDecimal getNumKloDocumento() {
		return numKloDocumento;
	}

	public BigDecimal getPrgAzienda() {
		return prgAzienda;
	}

	public BigDecimal getPrgUnita() {
		return prgUnita;
	}

	public String getStrDescrizione() {
		return strDescrizione;
	}

	public String getStrEnteRilascio() {
		return strEnteRilascio;
	}

	public String getStrNomeDoc() {
		return strNomeDoc;
	}

	public String getStrNote() {
		return strNote;
	}

	public String getStrNumDoc() {
		return strNumDoc;
	}

	/**
	 * Numero di protocollo PROPOSTO, che potrebbe essere diverso da quello effettivamente inserito nella AM_DOCUMENTO.
	 * 
	 * @see setNumProtInserito
	 */
	public BigDecimal getNumProtocollo() {
		return numProtocollo;
	}

	public BigDecimal getNumProtInserito() {
		return this.numProtInserito;
	}

	public BigDecimal getPrgTemplateStampa() {
		return this.prgTemplateStampa;
	}

	/**
	 * @param numProtocollo
	 *            il numero di protocollo con cui e' stato (o sara') effettivamente registrato il documento.<br>
	 *            Il metodo setNumProtocollo() invece memorizza il numero di protocollo <b>probabile</b>
	 */
	public void setNumProtInserito(BigDecimal numProtocollo) {
		this.numProtInserito = numProtocollo;
	}

	public void setCdnLavoratore(BigDecimal long1) {
		cdnLavoratore = long1;
	}

	public void setCdnUtIns(BigDecimal bigDecimal1) {
		cdnUtIns = bigDecimal1;
	}

	public void setCdnUtMod(BigDecimal bigDecimal1) {
		cdnUtMod = bigDecimal1;
	}

	public void setCodCpi(String string) {
		codCpi = string;
	}

	public void setDescCpi(String string) {
		descCpi = string;
	}

	public void setCodModalitaAcqril(String string) {
		codModalitaAcqril = string;
	}

	public void setCodMonoIO(String string) {
		codMonoIO = string;
	}

	public void setCodTipoDocumento(String string) {
		codTipoDocumento = string;
	}

	public void setCodTipoFile(String string) {
		codTipoFile = string;
	}

	public void setDatAcqril(String string) {
		datAcqril = string;
	}

	public void setDatFine(String string) {
		datFine = string;
	}

	public void setDatInizio(String string) {
		datInizio = string;
	}

	public void setDtmIns(String string) {
		dtmIns = string;
	}

	public void setDtmMod(String string) {
		dtmMod = string;
	}

	public void setFlgAutocertificazione(String string) {
		flgAutocertificazione = string;
	}

	public void setFlgDocAmm(String string) {
		flgDocAmm = string;
	}

	public void setFlgDocIdentifP(String string) {
		flgDocIdentifP = string;
	}

	public void setNumAnnoProt(BigDecimal bigDecimal1) {
		numAnnoProt = bigDecimal1;
	}

	public void setNumKloDocumento(BigDecimal bigDecimal1) {
		numKloDocumento = bigDecimal1;
	}

	public void setPrgAzienda(BigDecimal bigDecimal1) {
		prgAzienda = bigDecimal1;
	}

	public void setPrgDocumento(BigDecimal bigDecimal1) {
		prgDocumento = bigDecimal1;
	}

	public void setPrgUnita(BigDecimal bigDecimal1) {
		prgUnita = bigDecimal1;
	}

	public void setStrDescrizione(String string) {
		strDescrizione = string;
	}

	public void setStrEnteRilascio(String string) {
		strEnteRilascio = string;
	}

	public void setStrNomeDoc(String string) {
		strNomeDoc = string;
	}

	public void setStrNote(String string) {
		strNote = string;
	}

	public void setStrNumDoc(String string) {
		strNumDoc = string;
	}

	/**
	 * Numero di protocollo PROPOSTO, che potrebbe essere diverso da quello effettivamente inserito nella AM_DOCUMENTO.
	 * 
	 * @see setNumProtInserito
	 */
	public void setNumProtocollo(BigDecimal numProtocollo) {
		this.numProtocollo = numProtocollo;
	}

	public void setPrgTemplateStampa(BigDecimal prgTemplate) {
		this.prgTemplateStampa = prgTemplate;
	}

	/**
	 * Restituisce il vettore dei parametri da passare all'engine di CrystalClear
	 */
	public Vector getCrystalClearPrompts() {
		return crystalClearPrompts;
	}

	/**
	 * Serve a settare i vari argomenti passati a CrystalClear PROMPT0,PROMPT1, ....
	 * 
	 * @param vector
	 */
	public void setCrystalClearPrompts(Vector vector) {
		crystalClearPrompts = vector;
	}

	public void setCrystalClearPromptFields(Map promptFields) {
		this.promptFields = promptFields;
	}

	public String getCrystalClearRelativeReportFile() {
		return crystalClearRelativeReportFile;
	}

	/**
	 * Serve ad indicare a CrystalClear il template da utilizzare. Bisogna indicare il path relativo a /WEB-INF/report/.
	 * 
	 * @param string
	 */
	public void setCrystalClearRelativeReportFile(String ccRelReportFile) {
		if (ccRelReportFile == null) {
			_logger.warn(thisClassName + "::setCrystalClearRelativeReportFile(null) Assegnato valore nullo");

			return;
		}

		crystalClearRelativeReportFile = ccRelReportFile;
		reportFile = "file:" + ConfigSingleton.getRootPath() + CC_REPORT_REL_PATH + ccRelReportFile;
	}

	public BigDecimal getPrgDocumento() {
		return prgDocumento;
	}

	/**
	 * metodo di test per RDC
	 */
	public void createReportTempFile(Engine e, TransactionQueryExecutor txEcex)
			throws EMFInternalError, IOException, ReportException, SQLException {
		setEngine(e);
		createReportTempFile(txEcex);
	}

	public void createReportTempFile(Engine e) throws EMFInternalError, IOException, ReportException, SQLException {
		createReportTempFile(e, null);
	}

	public void createReportTempFile() throws EMFInternalError, IOException, ReportException, SQLException {
		createReportTempFile((TransactionQueryExecutor) null);
	}

	/**
	 * Crea il report utilizzando l'engine passata o, se null, creandone una.
	 * 
	 * @param txConn
	 *            se null viene estratta una connessione dal pool del framework
	 * 
	 * @throws IOException
	 * @throws ReportException
	 * @throws SQLException
	 * @throws EMFInternalError
	 */
	public void createReportTempFile(TransactionQueryExecutor txConn)
			throws IOException, ReportException, SQLException, EMFInternalError {
		_logger.warn(thisClassName + "::createReportTempFile() - Report invocato: " + this.reportFile);
		Connection conn = null;
		OutputStream fout = null;

		try {
			// se l'engine non e' stato passato allora si provvede a crearlo dal
			// file .rpt
			// L'engine potrebbe essere sia un report creato con le api sia un
			// report creato col designer e gia' istanziato per usi particolari
			// (vedi passaggio di ResultSet )
			if (engine == null) {
				engine = new Engine(convertFileType(getFileExtension(this.strNomeDoc)));
				engine.setReportFile(this.reportFile);
			}
			// Anche se si tratta di un report scritto con le api di crystal
			// clear o di un engine comunque gia' istanziato aggiorno i prompts
			if (crystalClearPrompts != null)
				setPromptsByVector(engine);
			else {
				if (promptFields != null)
					setPromptsByName(engine);
			}

			// Se viene passato il TransactionQueryExecutor si utilizza la sua
			// connessione, altrimenti ne si estrae
			// una dal pool

			if (txConn == null) {
				conn = DataConnectionManager.getInstance().getConnection().getInternalConnection();
				_logger.debug(getClass().getName() + ": "
						+ "La connessione che verra' utilizzata dall'engine di crystal clear e' stata estratta dal pool");

			} else {
				conn = txConn.getDataConnection().getInternalConnection();
				_logger.debug(getClass().getName() + ": "
						+ "La connessione che verra' utilizzata dall'engine di crystal clear e' stata aperta in transazione "
						+ "e non verra' chiusa");

			}
			setEngineConnections(engine, conn);

			sostituisciImmaginiSegnaposto(engine);

			engine.execute();

			// Scrittura del file....
			tempFile = File.createTempFile("~rpt", ".~" + getFileExtension(this.strNomeDoc));

			fout = new FileOutputStream(tempFile);

			int numPag = engine.getPageCount();

			for (int i = 1; i <= numPag; i++) {
				fout.write(engine.getPageData(i));
			}

		} catch (ReportException re) {
			// TODO FV 29/7/2020
			/*
			 * java.util.Properties engineProperties = RDC.getConfigurationProperties(); java.net.URL enginePath =
			 * RDC.getCrystalClearPropertyPath(); _logger.fatal("Crystal clear properties : " + engineProperties);
			 * _logger.fatal("Crystal clear properties PATH: " + enginePath);
			 */

			_logger.debug("ERRORE NELL'ENGINE DI CRYSTAL CLEAR");

			throw re;
		} finally {
			if (txConn == null && conn != null) {
				conn.close();
			}
			if (fout != null) {
				fout.close();
			}
		}

	}

	/**
	 * DOCAREA: Viene genrato il report che verra' inviato a docarea. Al momento ancora non si conosce il numero di
	 * protocollo. Il file creato viene assegnato a tempFilePreProtocollo.<br>
	 * N.B. E' qui che viene istanziato il secondo Engine di Crystal Clear che verra' eseguito in seguito.
	 * 
	 * @param tex
	 * @throws Exception
	 */
	public void creaReportXDOCAREA(TransactionQueryExecutor tex) throws Exception {
		_logger.warn(thisClassName + "::createReportTempFile() - Report invocato: " + this.reportFile);

		boolean CCrpt = false;
		try {
			// se l'engine non e' stato passato allora si provvede a crearlo dal
			// file .rpt
			if (engine == null) {
				engine = new Engine(convertFileType(getFileExtension(this.strNomeDoc)));
				engine.setReportFile(this.reportFile);
				CCrpt = true;
			}
			if (crystalClearPrompts != null)
				setPromptsByVector(engine);
			else {
				if (promptFields != null)
					setPromptsByName(engine);
			}

			Connection conn = null;
			conn = tex.getDataConnection().getInternalConnection();
			_logger.debug(getClass().getName() + ": "
					+ "La connessione che verra' utilizzata dall'engine di crystal clear e' stata aperta in transazione "
					+ "e non verra' chiusa");

			setEngineConnections(engine, conn);

			sostituisciImmaginiSegnaposto(engine);
			// salvo l'engine
			Engine engine2 = null;
			if (!CCrpt) {
				File fe = File.createTempFile("rpt-api", ".~rpt");
				RDC.saveEngine(fe, engine);
				engine2 = RDC.loadEngine(fe, convertFileType(getFileExtension(this.strNomeDoc)));
				fe.delete(); // anche se non viene cancellato non importa

				/*
				 * se volessimo utilizzare un buffer anzicche' un file .....
				 * 
				 * ByteArrayOutputStream engineBytes = new ByteArrayOutputStream(); PrintWriter pw = new
				 * PrintWriter(engineBytes); RDC.saveEngine(pw, engine); BufferedReader br = new BufferedReader(new
				 * InputStreamReader(new ByteArrayInputStream(engineBytes.toByteArray()))); engine2 = RDC.loadEngine(new
				 * File("c:/pluto.pippo"), br, convertFileType(getFileExtension(this.strNomeDoc)));
				 */

			}
			engine.execute();

			// Scrittura del file....
			// ATTENZIONE all'estensione del file temporaneo. Potrebbe essere
			// tra quelle che attivano l'antivirus.....
			tempFilePreProtocollo = File.createTempFile("~rpt", ".~" + getFileExtension(this.strNomeDoc));
			OutputStream fout = new FileOutputStream(tempFilePreProtocollo);

			int numPag = engine.getPageCount();

			for (int i = 1; i <= numPag; i++) {
				fout.write(engine.getPageData(i));
			}

			// Chiusura dello stream
			fout.flush();
			fout.close();

			if (CCrpt) {
				engine2 = new Engine(convertFileType(getFileExtension(this.strNomeDoc)));
				engine2.setReportFile(this.reportFile);
			}
			Hashtable userData = engine.getUserData();
			if (userData == null) {
				userData = new Hashtable();
				engine.setUserData(userData);
			}
			engine.getUserData().put("engine_2", engine2);

		} catch (ReportException re) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Crystal clear Error: ", re);

			throw new EMFUserError(EMFErrorSeverity.BLOCKING,
					MessageCodes.Protocollazione.PANTAREI_161_ERRORE_REPORT_DA_INVIARE);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "Errore nella creazione del report da inviare a DOCAREA: ", e);

			throw new EMFUserError(EMFErrorSeverity.BLOCKING,
					MessageCodes.Protocollazione.PANTAREI_161_ERRORE_REPORT_DA_INVIARE);
		}

	}

	/**
	 * DOCAREA: Il file pre-protocollo e' il file che viene creato prima che si conosca il numero di protocollo. Questo
	 * file viene inviato a docarea. Il web service di inserimento restituira' il numero di protocollo che verra'
	 * utilizzato per la protocollazione in locale.<br>
	 * Questo file viene rinominato (nome=<n. di protocollo>) in modo da permettere di risalire al documento, che in
	 * caso di errore successivo all'invio e protocollazione in docarea, non avra' un corrispondente in locale.<br>
	 * In caso non si riesca a rinominare il file si tenta di farne una copia.
	 * 
	 * @param fileName
	 * @throws Exception
	 */
	public void rinominaFilePreProtocollo(String fileName) throws Exception {
		FileOutputStream fTo = null;
		FileInputStream fFrom = null;
		try {
			File fNew = new File(fileName);
			File fOld = new File(tempFilePreProtocollo.getAbsolutePath());
			if (fNew.exists()) {
				_logger.fatal("---------- ERRORE DOCAREA ---- il nuovo file esiste: " + fNew.getAbsolutePath());

				throw new Exception(
						"---------- ERRORE DOCAREA ---- Il nome file in cui rinominare il file temporaneo inviato a docarea esiste gia'");
			}

			_logger.debug("---- ERRORE DOCAREA -------- tento di aprire il file ");

			// test di apertura del file che non e' stato possibile rinominare
			try {
				FileOutputStream fTest = new FileOutputStream(fOld.getAbsolutePath(), true);
				_logger.debug("---- ERRORE DOCAREA -------- apertura del file " + fOld.getAbsolutePath()
						+ " riuscita !!!!! ");

				fTest.close();
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.fatal(_logger,
						"---- ERRORE DOCAREA -------- apertura del file NON riuscita ", e);

				throw e;
			}
			// avvio della fase di copia
			try {
				_logger.debug("---- ERRORE DOCAREA -------- inizio copia .....");

				fTo = new FileOutputStream(fNew);
				fFrom = new FileInputStream(fOld);
				int len = 0, maxlen = 1024;
				byte buf[] = new byte[maxlen];
				while ((len = fFrom.read(buf)) > 0)
					fTo.write(buf, 0, len);
				fTo.flush();
				fTo.close();
				_logger.debug("---- ERRORE DOCAREA -------- Copia terminata correttamente in  " + fileName);

			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.fatal(_logger, "---- ERRORE DOCAREA -------- Copia di "
						+ fOld.getAbsolutePath() + " in " + fNew.getAbsolutePath() + " fallita ! ", e);

				throw e;
			} finally {
				if (fTo != null)
					try {
						fTo.close();
					} catch (Exception e) {
						it.eng.sil.util.TraceWrapper.fatal(_logger,
								"---- ERRORE DOCAREA -------- Impossibile chiudere il file di copia "
										+ fNew.getAbsolutePath(),
								e);

						throw e;
					}
				if (fFrom != null)
					fFrom.close();
			}
			// se non ci sono stati errori si associa il nuovo file a
			// "tempFilePreProtocollo"
			tempFilePreProtocollo = fNew;
		} catch (Exception e) {
			_logger.fatal("-------------------- ERRORE DOCAREA -------------------");

			_logger.fatal(
					"Impossibile rinominare il file inviato a DOCAREA. PROTOCOLLO DOCUMENTO = " + this.numProtInserito);

			_logger.fatal("-------------------- ERRORE DOCAREA -------------------");

			throw e;
		}
	}

	/**
	 * DOCAREA: Creazione del secondo report che stampera' il numero di protocollo. Questo report verra' inserito nel db
	 * locale.
	 * 
	 * @param tex
	 * @throws Exception
	 */
	public void creaEinserisciReport_2(TransactionQueryExecutor tex) throws Exception {
		Engine engine2;
		OutputStream fout;

		if ((this.tempFile == null) && (this.crystalClearRelativeReportFile != null)) {
			Hashtable userData = engine.getUserData();
			engine2 = (Engine) userData.get("engine_2");
			if (engine2 == null)
				throw new Exception(
						"Il secondo engine Crystal Clear non e' stato trovato. Impossibile creare il secondo report");

			Connection conn = tex.getDataConnection().getInternalConnection();

			setEngineConnections(engine2, conn);
			sostituisciImmaginiSegnaposto(engine2);
			aggiornaNumProtocolloReport(engine2);
			if (crystalClearPrompts != null)
				setPromptsByVector(engine2);
			else {
				if (promptFields != null)
					setPromptsByName(engine2);
			}
			ReportResultSet impostaResultSet = (ReportResultSet) userData.get("ReportResultSet");
			if (impostaResultSet != null)
				impostaResultSet.setDataTo(engine2);
			engine2.execute();

			// Scrittura del file....
			tempFile = File.createTempFile("~rpt", ".~" + getFileExtension(this.strNomeDoc));

			fout = new FileOutputStream(tempFile);

			int numPag = engine2.getPageCount();

			for (int i = 1; i <= numPag; i++) {
				fout.write(engine2.getPageData(i));
			}
			// Chiusura dello stream
			fout.close();
		}
	}

	/**
	 * Nel caso in cui sia stato creato un report, o se il client ha inviato un allegato, allora bisogna registrare il
	 * file temporaneo. Nel caso in cui non ci siano allegati (per esempio protocollazione di un documento a seguito di
	 * un inserimento di un movimento, allora non e' necessario inserire un blob vuoto dato che in fase di creazione del
	 * record gia' si provvede a cio'. Vedere la procedura pg_gestam.insertDocument())
	 * 
	 * @param tex
	 * @throws Exception
	 */
	public void inserisciBlob(TransactionQueryExecutor tex) throws Exception {
		if (this.tempFile != null) {
			saveBLOB(tex.getDataConnection());
		}
	}

	/**
	 * Imposta per vettore i parametri dell'engine. Se impostata a true la variabile static checkPromptsEnabled viene
	 * fatto un controllo sul numero dei parametri passati e quelli richiesti. Se il controllo fallisce viene lanciata
	 * un'eccezione.
	 * 
	 * @param engine
	 * @throws ReportException
	 *             se fallisce l'impostazione dei parametri nell'engine.
	 * @throws IllegalArgumentException
	 *             se il controllo sul numero dei parametri (se abilitato) fallisce.
	 * @author Savino
	 */
	private void setPromptsByVector(Engine engine) throws ReportException, IllegalArgumentException {
		checkPromptVector(engine);
		engine.setPrompts(this.crystalClearPrompts);
	}

	/**
	 * Imposta i parametri (prompt) dell'engine per nome e non per vettore. Se impostata a true la variabile static
	 * checkPromptsEnabled viene fatto anche un controllo sulla presenza o meno dei parametri nell' engine.
	 * 
	 * @param engine
	 * @throws ReportException
	 *             se fallisce l'impostazione dei parametri nell'engine del report.
	 * @throws IllegalArgumentException
	 *             se la Map promptFields e' null o il controllo sul nome dei parametri (se abilitato) fallisce.
	 * @author Savino
	 */
	private void setPromptsByName(Engine engine) throws ReportException, IllegalArgumentException {
		if (promptFields == null)
			throw new IllegalArgumentException("la map dei parametri di ingresso del report e' assente");
		Iterator fieldsName = promptFields.keySet().iterator();
		while (fieldsName.hasNext()) {
			String fieldName = (String) fieldsName.next();
			String fieldValue = (String) promptFields.get(fieldName);
			checkPromptFieldsName(fieldName, engine);
			engine.setPrompt(fieldName, fieldValue);
		}

//		// FV serve a metter un valore tappo ai paramentri non valorizzati
//		Fields fields = engine.getFields();
//		int promptsCount = fields.getPromptFieldsCount();
//		PromptField promptField = null;
//		for (int i = 0; i < promptsCount; i++) {
//			promptField = (PromptField) fields.getPromptField(i);
//			String nome = promptField.getName();
//			Object valore = promptField.getValue();
//			if (valore == null) {
//				engine.setPrompt(nome, "");
//			}
//		}
		
//		Fields fields = engine.getFields();
//		int promptsCount = fields.getPromptFieldsCount();
//		PromptField promptField = null;
//		for (int i = 0; i < promptsCount; i++) {
//			promptField = (PromptField) fields.getPromptField(i);
//			String nome = promptField.getName();
//			Object valore = promptField.getValue();
//			System.out.println("nome="+ nome + " valore:" + valore);
//		}

		
	}

	/**
	 * Testa se il numero dei parametri del vettore che si vuole passare all'engine e' diverso dal numero di parametri
	 * (prompt) presenti nell'engine stesso N.B. il test viene eseguito solo se e' stato impostata la variabile statica
	 * checkPromptsEnabled a true (vedi setCheckPromptsEnabled(boolean b))
	 * 
	 * @param e
	 * @throws ReportException
	 *             se l'accesso ai campi dell' engine fallisce
	 * @throws IllegalArgumentException
	 *             se il numero dei parametri del vettore che si vuole passare all'engine e' diverso dal numero dei
	 *             parametri (prompt) presenti nell'engine stesso
	 * @author Savino
	 */
	private void checkPromptVector(Engine e) throws ReportException, IllegalArgumentException {
		if (!checkPromptsEnabled)
			return;
		Fields fields = e.getFields();
		int nPrompt = e.getFields().getPromptFieldsCount();
		int i = 0;
		// il numero di parametri esterni. In Crystal Clear vengono mostrati
		// anche i parametri interni,
		// ovvero quelli usati per il passaggio ai subreport (subreport link).
		// Naturalmente nel conteggio questi parametri vanno esclusi. (iniziano
		// per "Pm-?")
		int nPromptEx = 0;
		for (; i < nPrompt; i++) {
			PromptField pf = fields.getPromptField(i);
			String text = pf.getName();
			if (!fields.getPromptField(i).getName().startsWith("Pm-?"))
				nPromptEx++;
		}
		if (nPromptEx != crystalClearPrompts.size())
			throw new IllegalArgumentException(
					"Il numero dei parametri passati all'engine Crystal Clear e' diverso dal numero di parametri richiesti");
	}

	/**
	 * Testa se il parametro che si vuole impostare e' presente tra i parametri dell'engine (passaggio dei parametri per
	 * nome) N.B. il test viene eseguito solo se e' stato impostata la variabile statica checkPromptsEnabled a true
	 * (vedi setCheckPromptsEnabled(boolean b))
	 * 
	 * @param promptName
	 *            nome parametro prompt dell'engine
	 * @param e
	 * @throws IllegalArgumentException
	 *             se il promptName che si vuole impostare non esiste nell'engine (di fatto l'engine ignora i parametri
	 *             che si cerca di valorizzare se questi non sono presenti. Pero' si tratta di un errore applicativo.
	 *             Es: vedi javadoc delle api di crystal clear Engine.setPrompt(fieldName, fieldValue); )
	 * @throws ReportException
	 *             se l'accesso ai campi dell' engine fallisce
	 * @author Savino
	 */
	private void checkPromptFieldsName(String promptName, Engine e) throws IllegalArgumentException, ReportException {
		if (!checkPromptsEnabled)
			return;
		Fields fields = e.getFields();
		int nPrompt = fields.getPromptFieldsCount();
		int i = 0;
		for (; i < nPrompt; i++) {
			com.inet.report.PromptField pf = fields.getPromptField(i);
			String text = pf.getName();
			if (fields.getPromptField(i).getName().equals(promptName))
				break;
		}
		if (i == nPrompt)
			throw new IllegalArgumentException("il parametro " + promptName
					+ " non e' presente tra i parametri del report " + engine.getReportTitle());
	}

	public BigDecimal getPrgDocumentoBlob() {
		return prgDocumentoBlob;
	}

	public void setPrgDocumentoBlob(BigDecimal bigDecimal1) {
		prgDocumentoBlob = bigDecimal1;
	}

	public File getTempFile() {
		return tempFile;
	}

	public void setTempFile(File file) {
		tempFile = file;
	}

	public File getTempFilePreProtocollo() {
		return tempFilePreProtocollo;
	}

	public void setTempFilePreProtocollo(File newFile) {
		this.tempFilePreProtocollo = newFile;
	}

	/**
	 * @param newSkipSaving
	 *            se true il documento non viene salvato nel db. Di default false
	 */
	public void setSkipSaving(boolean newSkipSaving) {
		this.skipSaving = newSkipSaving;
	}

	public void setExistBlob(BigDecimal newExistBlob) {
		this.existBlob = (newExistBlob.compareTo(new BigDecimal("0")) == 0) ? false : true;
	}

	public boolean getExistBlob() {
		return this.existBlob;
	}

	public String getDatProtocollazione() {
		return this.datProtocollazione;
	}

	public void setDatProtocollazione(String newDatProtocollazione) {
		this.datProtocollazione = newDatProtocollazione;
	}

	public String getTipoProt() {
		return this.tipoProt;
	}

	public void setTipoProt(String newTipoProt) {
		this.tipoProt = newTipoProt;
	}

	// DAVIDE ==================================================================
	public void setPrgDocumentoColl(BigDecimal newPrgDocumentoColl) {
		this.prgDocumentoColl = newPrgDocumentoColl;
	}

	public BigDecimal getPrgDocumentoColl() {
		return this.prgDocumentoColl;
	}

	public void setPagina(String newPagina) {
		this.pagina = newPagina;
	}

	public String getPagina() {
		return this.pagina;
	}

	public void setCdnComponente(BigDecimal newCdnComponente) {
		this.cdnComponente = newCdnComponente;
	}

	public BigDecimal getCdnComponente() {
		return this.cdnComponente;
	}

	public void setNumKeyLock(BigDecimal newNumKeyLock) {
		this.numKeyLock = newNumKeyLock;
	}

	public BigDecimal getNumKeyLock() {
		return this.numKeyLock;
	}

	public String getChiaveTabella() {
		return this.chiaveTabella;
	}

	public void setChiaveTabella(String key) {
		this.chiaveTabella = key;
	}

	public boolean inInserimento() {
		return this.inserimento;
	}

	public boolean protAutomatica() {
		return this.protAutomatica;
	}

	public String convertFileType(String newFileType) {
		if (newFileType.equalsIgnoreCase("PDF"))
			return Engine.EXPORT_PDF;
		if (newFileType.equalsIgnoreCase("RTF"))
			return Engine.EXPORT_RTF;
		if (newFileType.equalsIgnoreCase("XLS"))
			return Engine.EXPORT_XLS;
		if (newFileType.equalsIgnoreCase("CVS"))
			return Engine.EXPORT_CSV;
		if (newFileType.equalsIgnoreCase("HTML"))
			return Engine.EXPORT_HTML;

		return Engine.EXPORT_PDF; // default
	}

	/**
	 * @deprecated utilizzare getFileExtension()
	 */
	public static String getFileExtension(String filename) {
		if (filename == null)
			return null;
		String suffix = null;
		int puntoIdx = filename.lastIndexOf(".");
		if (puntoIdx > 0)
			suffix = filename.substring(puntoIdx + 1).toLowerCase();
		return suffix;
	}

	/**
	 * @return L'estensione del file
	 * 
	 */
	public String getFileExtension() {
		String suffix = Documento.getFileExtension(this.strNomeDoc);

		if (suffix == null)
			suffix = "";
		return suffix;

	}

	/**
	 * Imposta la connessione ottenuta dal pool all'engine di Crystal Clear.
	 * 
	 * @param engine
	 * @param conn
	 * @throws Exception
	 */
	private void setEngineConnections(Engine engine, Connection conn) throws SQLException, ReportException {
		String catalog = null;

		catalog = conn.getCatalog();

		ConnessionePerReport miaConn = new ConnessionePerReport(conn);
		engine.setConnection(miaConn);
		engine.setCatalog(catalog);

		for (int i = 0; i < engine.getSubReportCount(); i++) {
			engine.getSubReport(i).setConnection(miaConn);
			engine.setCatalog(catalog);
		}

	}

	// END DAVIDE
	// ===============================================================

	public Engine getEngine() {
		return engine;
	}

	public void setEngine(Engine engine) {
		this.engine = engine;
	}

	/**
	 * Tramite l'engine [engiin] spazzola il report in cerca delle immagini "segnaposto" da rimpiazzare con quelle
	 * opportune. Vengono esaminate TUTTE le sezioni di TUTTE le aree del report. Vengono sostituite TUTTE le immagini
	 * trovate e che hanno l'attributo di SUPPRESS attivato (ossia che sono delle immagini soppresse).
	 * 
	 * @author Luigi Antenucci
	 */
	public static final void sostituisciImmaginiSegnaposto(Engine e) throws ReportException {

		long prima = System.currentTimeMillis();

		// 1) Per tutt le aree del report,
		for (int numArea = 0; numArea < REPORT_AREA_ID.length; numArea++) {

			Area area = e.getArea(REPORT_AREA_ID[numArea]);
			if (area != null) {

				// 2) Per tutte le sezioni dell'area corrente,
				int maxSez = area.getSectionCount();
				for (int numSez = 0; numSez < maxSez; numSez++) {

					Section sezione = area.getSection(numSez);

					// 3) Per tutti gli elementi della sezione corrente,
					Vector elements = sezione.getElementsV(); // all elements
																// in the
																// section
					for (int numElem = 0; numElem < elements.size(); numElem++) {
						Element element = (Element) elements.elementAt(numElem);
						if (element instanceof com.inet.report.Subreport) {
							sostituisciImmaginiSegnaposto(((Subreport) element).getEngine());
						}
						if (element instanceof com.inet.report.Picture) {

							// Se l'immagine è "SOPPRESSA", allora è l'immagine
							// da sostituire!
							boolean suppress = element.isSuppress();
							if (suppress) {

								// Recupero la posizione e la dimensione
								// dell'immagine originale
								int x = element.getX();
								int y = element.getY();
								int w = element.getWidth();
								int h = element.getHeight();

								String logoFileName = getNomeFileNuovaImmagine(e, w, h);

								// Rimuovo l'immagine e la sostituisco con
								// quella nuova
								sezione.remove(element);
								if (logoFileName != null) {
									sezione.addPicture(x, y, w, h, logoFileName);
								}

								// NB: *non* Interrompo la ricerca ma continuo
								// con le altre possibili immagini
							}
						}
					}
				}
			} // for numSez
		} // for numArea

		long dopo = System.currentTimeMillis();
		_logger.debug(thisClassName + ".sostituisciImmaginiSegnaposto() - " + "tempo di esecuzione: " + (dopo - prima)
				+ " ms");

	}

	/**
	 * Rende il nome del file contenente l'immagine del LOGO da usare nei report. Viene recuperato dal DB il
	 * CODPROVINCIASIL dalla TS_GENERALE e usato per generare il nome del file. Rende null in caso o che non si riesca a
	 * reperire il CODPROVINCIASIL o che non esista il nome del file generato.
	 * 
	 * In base a WIDTH e HEIGHT passati, so quale CATEGORIA di immagine devo usare.
	 * 
	 * @author Luigi Antenucci
	 */
	private static final String getNomeFileNuovaImmagine(Engine e, int wPir, int hPir) {

		// Fattore di conversione: 100 pixel = 1550 piripicchi. :)
		int FATTORE_X = 1500 / 100;
		int w = wPir / FATTORE_X;
		int h = hPir / FATTORE_X;

		// CREAZIONE DEL NOME DEL FILE (SENZA ESTENSIONE)
		String name = getNomeFilePerCategoria(e, w, h);
		if (name == null)
			return null; // ESCO senza alcun nome!

		// COSTRUISCO IL PERCORSO COMPLETO PER RAGGIUNGERE IL FILE.
		String pathName = FileUtils.appendFileSeparator(ConfigSingleton.getRootPath()) + DYN_IMG_PATH;
		pathName = FileUtils.appendFileSeparator(pathName);
		File pathNameFile = new File(pathName);
		if (!pathNameFile.isDirectory()) { // rende FALSE se non esiste o non è
											// una directory
			_logger.fatal(thisClassName + ".getNomeFileNuovaImmagine() - " + "Percorso non trovato [" + pathName + "]");

			return null; // ESCO senza alcun nome!
		}
		pathName += name + ".";

		// RECUPERO DEL FILE (PROVANDO LE POSSIBILI ESTENSIONI IN SEQUENZA)
		// Provo a crecare un file immagine con varie estensioni (in ordine di
		// qualità)
		for (int numExt = 0; numExt < IMAGE_EXTENSION.length; numExt++) {

			String pathNameExt = pathName + IMAGE_EXTENSION[numExt];
			File file = new File(pathNameExt);
			if (file.exists()) { // Se il file con l'immagine esiste, lo
									// rendo.
				_logger.debug(thisClassName + ".getNomeFileNuovaImmagine() - " + "Uso l'immagine di logo ["
						+ pathNameExt + "]");

				return pathNameExt; // ESCO e rendo il nome del file
			}
		}
		_logger.fatal(thisClassName + ".getNomeFileNuovaImmagine() - " + "Non esiste il file [" + pathName + "*]");

		return null; // ESCO senza alcun nome!
	}

	/**
	 * Rende il nome del file di immagine da usare. Il NOME DEL FILE D'IMMAGINE varia a seconda della CATEGORIA;
	 * quest'ultima è definita univocamente dalla coppia (WIDTH,HEIGHT) passata come parametro (che è la DIMENSIONE, in
	 * pixel, dell'immagine originale presente nel report). NB: il nome del file reso è SENZA percorso e SENZA
	 * ESTENSIONE! per es. "prov32" oppure "prov37_90x100".
	 * 
	 * @author Luigi Antenucci
	 */
	private static final String getNomeFilePerCategoria(Engine e, int w, int h) {

		// La coppia (w,h) identifica univocamente la categoria dell'immagine
		// segnaposto.
		// PS: si possono usare le funzioni di utilità getCodProvSil() e
		// getReportFileName(e).

		// LOGO DELLA PROVINCIA 90x100
		if ((w == 90) && (h == 100)) {
			String codProvSil = getCodProvSil();
			if (codProvSil != null) {
				// TODO CITTA METROPOLITANA BOLOGNA
				// if ("37".equalsIgnoreCase(codProvSil)){
				// return null;
				// }
				return "prov" + codProvSil + "_90x100";
			}
		}

		// LOGO DELLA PROVINCIA 81x90
		if ((w == 81) && (h == 90)) {
			String codProvSil = getCodProvSil();
			if (codProvSil != null) {
				// TODO CITTA METROPOLITANA BOLOGNA
				// if ("37".equalsIgnoreCase(codProvSil)){
				// return null;
				// }
				return "prov" + codProvSil + "_81x90";
			}
		}
		// LOGO DELLA PROVINCIA 125x75
		// logo centri per l'impiego emilia romagna
		if ((w == 125) && (h == 75)) {
			String codProvSil = getCodProvSil();
			if (codProvSil != null) {
				// TODO CITTA METROPOLITANA BOLOGNA
				// if ("37".equalsIgnoreCase(codProvSil)){
				// return null;
				// }
				return "prov" + codProvSil + "_125x75";
			}
		}
		// per ora viene utilizzata solo nella stampa dichiarazioni/attestazioni
		if ((w == 222) && (h == 72)) {
			String codProvSil = getCodProvSil();
			if (codProvSil != null) {
				// TODO CITTA METROPOLITANA BOLOGNA
				// if ("37".equalsIgnoreCase(codProvSil)){
				// return null;
				// }
				return "prov" + codProvSil + "_222x72";
			}
		}
		// LOGO DELLA PROVINCIA 53x58
		if ((w == 53) && (h == 58)) {
			String codProvSil = getCodProvSil();
			if (codProvSil != null) {
				// TODO CITTA METROPOLITANA BOLOGNA
				// if ("37".equalsIgnoreCase(codProvSil)){
				// return null;
				// }
				return "prov" + codProvSil + "_53x58";
			}
		}

		// SE SONO ANCORA QUA, NON HO NESSUNA IMMAGINE DA RIMPIAZZARE!
		_logger.warn(
				thisClassName + ".getPrefissoPerCategoria() - " + "Categoria w=" + w + " e h=" + h + " non definita!");

		return null;
	}

	/**
	 * Funzione di utilità per la "getNomeFilePerCategoria", ma usabile in altri contesti (è stata resa pubblica). Rende
	 * il CodProvinciaSil memorizzato nella TS_GENERALE nel DB.
	 * 
	 * @author Luigi Antenucci
	 */
	public static final String getCodProvSil() {
		String codProvSil = null;

		// Recupero dal DB il CODPROVINCIASIL dalla TS_GENERALE
		DataConnection connection = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;
		try {
			SourceBean row;

			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			connection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);
			String sqlStatoSelect = SQLStatements.getStatement("GET_CODPROVINCIASIL_PER_LOGO");
			sqlCommand = connection.createSelectCommand(sqlStatoSelect);

			dataResult = sqlCommand.execute();

			ScrollableDataResult sdr = (ScrollableDataResult) dataResult.getDataObject();

			if (sdr.hasRows()) {
				row = sdr.getDataRow().getSourceBean();

				codProvSil = (String) row.getAttribute("CODPROVINCIASIL");
			}
		} catch (Exception ex) {
			// Non fo nulla
		} finally {
			Utils.releaseResources(connection, sqlCommand, dataResult);
		}

		if (codProvSil == null) {
			_logger.fatal(
					thisClassName + ".getCodProvSil() - " + "Non sono riuscito a recuperare il CODPROVINCIASIL dal DB");

		}
		return codProvSil;
	}

	/**
	 * Funzione di utilità per la "getNomeFilePerCategoria", ma usabile in altri contesti (è stata resa pubblica). Rende
	 * il nome del file di report, senza percorso. PS: per ottenere l'URL completo si usi: engine.getReportFile().
	 * 
	 * Rende null se non riesce a recuperare il nome del file.
	 * 
	 * @author Luigi Antenucci
	 */
	public static final String getReportFileName(Engine e) {
		String reportFileName = null;
		;

		try {
			String temp = e.getReportFile().getFile();
			int lastBack = temp.lastIndexOf(File.separatorChar);
			if (lastBack == -1)
				lastBack = temp.lastIndexOf('/');
			if (lastBack == -1)
				lastBack = temp.lastIndexOf('\\');
			if (lastBack != -1) {
				reportFileName = temp.substring(lastBack + 1);
			}
		} catch (Exception ex) {
		}

		_logger.debug(thisClassName + ".getReportFileName() - " + "rende [" + reportFileName + "]");

		return reportFileName;
	}

	public String getStrTipoDoc() {
		return this.strTipoDoc;
	}

	public void setStrTipoDoc(String strTipoDoc) {
		this.strTipoDoc = strTipoDoc;
	}

	public void setCodStatoAtto(String CodStatoAtto) {
		this.CodStatoAtto = CodStatoAtto;
	}

	public String getCodStatoAtto() {
		return this.CodStatoAtto;
	}

	/**
	 * Se si vuole testare il corretto passaggio dei parametri al report (sia che avvenga per vettore che per
	 * nome-parametro) bisogna chiamare questo metodo statico (per esempio nella classe di test con junit..) impostamdo
	 * ovviemente la relativa variabile a true; Vedi i metodi checkPromptVector(Engine e) e checkPromptFieldsName(String
	 * promptName, Engine e).
	 * 
	 * @param enable
	 * @author Savino
	 */
	public static void setCheckPromptsEnabled(boolean enable) {
		checkPromptsEnabled = enable;
	}

	/**
	 * @return
	 */
	public String getStrChiaveTabella() {
		return strChiaveTabella;
	}

	/**
	 * @param string
	 */
	public void setStrChiaveTabella(String string) {
		strChiaveTabella = string;
	}

	/**
	 * @return
	 */
	public String getCodMotAnnullamentoAtto() {
		return codMotAnnullamentoAtto;
	}

	/**
	 * @param string
	 */
	public void setCodMotAnnullamentoAtto(String string) {
		codMotAnnullamentoAtto = string;
	}

	/**
	 * @return
	 */
	public String getFlgAnnullamentoDocumento() {
		return flgAnnullamentoDocumento;
	}

	public String toString() {
		return new StringBuffer().append("it.eng.sil.bean.Documento ..... ").append("prgDocumento=")
				.append(this.prgDocumento).append(", codTipoDocumento=").append(this.codTipoDocumento)
				.append(", codStatoAtto=").append(this.CodStatoAtto).append(", numProtocollo=")
				.append(this.numProtocollo).append(", numProtInserito=").append(this.numProtInserito)
				.append(", cdnLavoratore=").append(this.cdnLavoratore).append(", datProtocollazione=")
				.append(this.datProtocollazione).append(", strChiaveTabella=").append(this.strChiaveTabella).toString();

	}

	/**
	 * @param string
	 */
	public void setServizioOnLine(String string) {
		servizioOnLine = string;
	}

	/**
	 * @return
	 */
	public String getServizioOnLine() {
		return servizioOnLine;
	}

	/**
	 * Ritorna true se il documento e' gia' stato protocollato su un altro sistema: quindi in fase di regitrazione il
	 * documento deve saltare la fase di lettura e regitrazione del protocollo (vale solo per la protocollazione docarea
	 * o cumunque esterna)
	 */
	public boolean protocollatoEsternamente() {
		return protocollatoEsternamente;
	}

	/**
	 * I movimenti da validare pervenuti da sare potrebbero gia' essere stati protocollati in un sistema centralizzato
	 * (come accade per il sare di bologna). In questo caso non bisogna riprotollarli in docarea. <br>
	 * N.B. A bologna i movimenti validati gia' protocollati vengono registrati nel db con il protocollo locale che
	 * pero' NON e' un protocollo vero e proprio, ma un semplice numero progressivo interno.
	 * 
	 * @param giaProtocollato
	 */
	public void setProtocollatoEsternamente(boolean giaProtocollato) {
		protocollatoEsternamente = giaProtocollato;
	}

	public void aggiornaSoloNumProt(TransactionQueryExecutor tex) throws Exception {
		Object[] objD = new Object[2];

		objD[0] = numProtInserito; // NB!
		objD[1] = prgDocumento;

		tex.executeQuery("UPDATE_ONLY_NUM_PROT", objD, "UPDATE");
	}

	public void aggiornaSoloNomeDoc(TransactionQueryExecutor tex) throws Exception {
		Object[] objD = new Object[2];

		objD[0] = strNomeDoc; // NB!
		objD[1] = prgDocumento;

		tex.executeQuery("UPDATE_ONLY_NOME_DOC", objD, "UPDATE");
	}

	public boolean emptyBLOBDoc(TransactionQueryExecutor txExecutor, BigDecimal prgDocumento) throws Exception {

		// Svuota il campo BLOB della tabella AM_DOCUMENTO_BLOB

		Object[] obj = new Object[1];

		obj[0] = prgDocumento;

		Boolean result = (Boolean) txExecutor.executeQuery("EMPTY_BLOB_AM_DOCUMENTO", obj, "UPDATE");
		if (!result.booleanValue()) {
			return false;
		}
		return true;
	}

}
