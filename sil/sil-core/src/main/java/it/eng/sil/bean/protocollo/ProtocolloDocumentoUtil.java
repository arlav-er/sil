/*
 * Created on Mar 8, 2007
 */
package it.eng.sil.bean.protocollo;

import java.io.File;
import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.Values;
import it.eng.sil.bean.Documento;

/**
 * savino
 */
public class ProtocolloDocumentoUtil {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ProtocolloDocumentoUtil.class.getName());

	public static final int TIPO_DOCAREA_SARE = 0;
	public static final int TIPO_DOCAREA_VER_161 = 1;
	public static final int TIPO_SIL_LOCALE_AUTOMATICA = 2;
	public static final int TIPO_SIL_LOCALE_MANUALE = 3;

	/**
	 * E' il tipo di protocollo in atto. Vedere in questa classe i fields TIPO_XXX.<br>
	 * Puo' essere registrato nel file di configurazione o nel db in TS_CONFIG_LOC<br>
	 * Viene inizializzata dal blocco static in fondo alla classe....
	 */
	private static int tipo = -1;
	/**
	 * username e password del codEnte. Verranno utilizzati per la fase di login con il web service docarea.<br>
	 * Possono essere registrati nel file di configurazione o nel db in TS_UTENTE<br>
	 * Vengono inizializzate dal blocco static in fondo alla classe....
	 */
	private static String username;
	private static String password;
	/**
	 * Vale true se i movimenti importati da sare sono gia' stati protocollati in docarea.<br>
	 * Viene inizializzata dal blocco static in fondo alla classe....
	 */
	private static boolean importDocProtocollati;

	/**
	 * se il file di configurazione va sempre riletto allora leggiSempre deve valere true.<br>
	 * Viene inizializzata dal blocco static in fondo alla classe....
	 */
	private static boolean leggiSempre;

	private static int leggiTipoDalDB() throws Exception {
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_PROTOCOLLAZIONE", new Object[] {}, "SELECT",
				Values.DB_SIL_DATI);
		if (row == null)
			throw new Exception("impossibile leggere il tipo di protocollo dal db. SourceBean vuoto");
		BigDecimal tipoProtocollo = (BigDecimal) row.getAttribute("row.tipo_registrazione_doc");
		if (tipoProtocollo == null)
			throw new Exception("impossibile leggere il tipo di protocollo dal db. tipoProtocollo null");
		return tipoProtocollo.intValue();
	}

	private static String leggiUsernameEnteDalDB() throws Exception {
		String codente = getCodEnte();
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_UTENTE_DOCAREA", new Object[] { codente },
				"SELECT", Values.DB_SIL_DATI);
		if (row == null)
			throw new Exception("impossibile leggere lo username dell'utente docarea dal db. SourceBean vuoto");
		String _username = (String) row.getAttribute("row.strlogin");
		if (_username == null)
			throw new Exception("impossibile leggere lo username dal db. username null");
		return _username;
	}

	private static String leggiPasswordEnteDalDB() throws Exception {
		String codente = getCodEnte();
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_UTENTE_DOCAREA", new Object[] { codente },
				"SELECT", Values.DB_SIL_DATI);
		if (row == null)
			throw new Exception("impossibile leggere la password dell'utente docarea dal db. SourceBean vuoto");
		String _password = (String) row.getAttribute("row.strpassword");
		if (_password == null)
			throw new Exception("impossibile leggere la password dell'utente docarea dal db. username null");
		return _password;
	}

	private static String leggiImportDocProtocollatiDalDB() throws Exception {
		SourceBean row = (SourceBean) QueryExecutor.executeQuery("GET_PROTOCOLLAZIONE", new Object[] {}, "SELECT",
				Values.DB_SIL_DATI);
		if (row == null)
			throw new Exception("impossibile leggere il flag imp_doc_prot dal db. SourceBean vuoto");
		String impDocProt = (String) row.getAttribute("row.imp_doc_prot");
		if (impDocProt == null)
			throw new Exception("impossibile leggere il flag imp_doc_prot dal db. imp_doc_prot null");
		return impDocProt;
	}

	/**
	 * se il file di configurazione va letto sempre allora apre il file e lo restituisce, altrimenti restituisce il
	 * SourceBean letto al caricamemnto della classe
	 * 
	 * @return
	 * @throws Exception
	 */
	private static SourceBean leggiConfigurazione() throws Exception {
		return (SourceBean) ConfigSingleton.getInstance().getAttribute("PROTOCOLLO");
	}

	/**
	 * Restituisce il tipo di protocollo.<br>
	 * Se bisogna leggerlo ad ogni chiamata e dal db allora esegue la select, altrimenti riporta il valore registrato
	 * nella variabile statica tipo (unica lettura) o il valore registrato nel file di configurazione.
	 * 
	 * @return
	 * @throws Exception
	 */
	public static int tipoProtocollo() throws Exception {
		if (leggiSempre) {
			SourceBean sb = leggiConfigurazione();
			if ("S".equals(sb.getAttribute("leggi_da_db"))) {
				tipo = leggiTipoDalDB();
			} else
				tipo = Integer.parseInt((String) sb.getAttribute("tipo"));
		}
		return tipo;
	}

	/**
	 * Il tipo di attachment e' configurabile tramite il file ProtocolloDocumento.xml con l'attributo "attachment". <br>
	 * Se l'attributo e' assente si assume come defalut il tipo MIME.
	 * 
	 * @return true se e' necessario utilizzare il tipo DIME, false se si deve usare il tipo MIME.
	 * 
	 * @throws Exception
	 */
	public static boolean isDIMEAttachment() throws Exception {
		boolean ret;
		SourceBean sb = leggiConfigurazione();
		String tipoAtt = (String) sb.getAttribute("docarea.ws.attachment");
		return "DIME".equalsIgnoreCase(tipoAtt);
	}

	/**
	 * Restituisce il path assoluto del file che rappresenta un documento vuoto. L'attributo "documento_vuoto" e'
	 * obbligatorio.
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String pathDocumentoVuoto() throws Exception {
		SourceBean sb = leggiConfigurazione();
		String pathRelativo = (String) sb.getAttribute("docarea.file.documento_vuoto");
		if (pathRelativo == null || pathRelativo.equals(""))
			throw new Exception("attributo documento_vuoto obbligatorio");
		String path = ConfigSingleton.getRootPath() + pathRelativo;
		return path;
	}

	/**
	 * Restituisce la descrizione del file che rappresenta un documento vuoto. L'attributo "descrizione_documento_vuoto"
	 * e' obbligatorio.
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String descrizioneDocumentoVuoto() throws Exception {
		SourceBean sb = leggiConfigurazione();
		String descrizione = (String) sb.getAttribute("docarea.file.descrizione_documento_vuoto");
		if (descrizione == null || descrizione.equals(""))
			throw new Exception("attributo descrizione_documento_vuoto obbligatorio");
		return descrizione;
	}

	/**
	 * Restituisce il nome del file che rappresenta un documento vuoto. L'attributo "nome_documento_vuoto" e'
	 * obbligatorio.
	 * 
	 * @return
	 */
	public static String nomeDocumentoVuoto() throws Exception {
		SourceBean sb = leggiConfigurazione();
		String nome = (String) sb.getAttribute("docarea.file.nome_documento_vuoto");
		if (nome == null || nome.equals(""))
			throw new Exception("attributo nome_descrizione_documento_vuoto obbligatorio");
		return nome;
	}

	/**
	 * Legge dal file di configurazione ProtocolloDocumento.xml (attributo "SOAP_version" ) la versione di soap da
	 * utilizzare per le chiamate dei web services.<br>
	 * Sono possibili due valori: "1.1" che e' il valore di defalut, e "1.2"
	 * 
	 * @return
	 * @throws Exception
	 */
	public static boolean isSOAP12() throws Exception {
		SourceBean sb = leggiConfigurazione();
		String versione = (String) sb.getAttribute("docarea.ws.SOAP_version");
		return "1.2".equals(versione);
	}

	public static String descrizioneProtocollo() throws Exception {
		String s = "";
		int n = 1;
		switch (n) {
		case TIPO_DOCAREA_SARE:
			s = "Protocollazione automatica centrallizzata DOCAREA utilizzata dal sistema SARE";
			break;
		case TIPO_DOCAREA_VER_161:
			s = "Protocollazione automatica centrallizzata DOCAREA versione 1.6.1";
			break;
		case TIPO_SIL_LOCALE_AUTOMATICA:
			s = "Protocollazione automatica locale al sistema SIL";
			break;
		case TIPO_SIL_LOCALE_MANUALE:
			s = "Protocollazione manuale locale al sistema SIL";
			break;
		default:
			s = "Tipo di protocollazione non gestito";
		}
		return s;
	}

	public static boolean protocollazioneLocale() throws Exception {
		return (tipoProtocollo() == ProtocolloDocumentoUtil.TIPO_SIL_LOCALE_AUTOMATICA
				|| tipoProtocollo() == ProtocolloDocumentoUtil.TIPO_SIL_LOCALE_MANUALE);
	}

	/**
	 * controlla se la protocollazione e' locale o docarea. In caso sia docarea cancella il file inviato se esistente.
	 * 
	 * @param doc
	 * @throws Exception
	 *             se non si riesce a cancellarlo
	 */
	public static void cancellaFileDocarea(Documento doc) throws Exception {
		if (!protocollazioneLocale()) {
			if (doc.getTempFilePreProtocollo() != null) {
				if (!doc.getTempFilePreProtocollo().delete()) {
					_logger.debug("Impossibile cancellare il file PROTOCOLLATO inviato a DocArea: "
							+ doc.getTempFilePreProtocollo().getAbsolutePath());

					_logger.debug("DATI DOCUMENTO: " + doc.toString());

					_logger.debug("CONTATTARE L'AMMINISTRATORE DI SISTEMA");

					throw new EMFUserError(EMFErrorSeverity.BLOCKING,
							MessageCodes.Protocollazione.PANTAREI_161_ERRORE_RENAME_FILE);
				}
			}
		}
	}

	/**
	 * Controlla se la protocollazione e' locale o docarea. In caso sia docarea cancella tutti i files inviati,<br>
	 * e cancella dalla serviceRequest il/i documenti.<br>
	 * Il file inviato e' referenziato dalla proprieta' tempFilePreProtocollo della classe Documento.<br>
	 * Il documento viene ripreso dalla ServiceRequest (nel caso dell'inserimento di un movimento possono esserci piu'
	 * documenti).
	 * 
	 * @param doc
	 * @throws Exception
	 *             se non si riesce a cancellarlo
	 */
	public static void cancellaFileDocarea() throws Exception {
		if (!protocollazioneLocale()) {
			SourceBean serviceRequest = RequestContainer.getRequestContainer().getServiceRequest();
			if (serviceRequest == null) {
				_logger.debug("La serviceRequest non e' presente nel RequestContainer");

				throw new Exception("La serviceRequest non e' presente nel RequestContainer");
			}
			Vector v = serviceRequest.getAttributeAsVector("theDocument");
			for (int i = 0; i < v.size(); i++) {
				Documento doc = (Documento) v.get(i);
				cancellaFileDocarea(doc);
			}
			serviceRequest.delAttribute("theDocument");
		}
	}

	/**
	 * Cancella dalla serviceRequest tutti i documenti inviati a docarea.
	 * 
	 * @throws Exception
	 */
	public static void clearRequest() throws Exception {
		if (!protocollazioneLocale()) {
			SourceBean serviceRequest = RequestContainer.getRequestContainer().getServiceRequest();
			if (serviceRequest == null) {
				_logger.debug("La serviceRequest non e' presente nel RequestContainer");

				throw new Exception("La serviceRequest non e' presente nel RequestContainer");
			}
			serviceRequest.delAttribute("theDocument");
		}
	}

	/**
	 * Inserisce nella ServiceRequest il riferimento al Documento. Se l'operazione di protocollazione in docarea avra'
	 * successo, dovra' essere chiamato il metodo cancellaFileDocarea() ( il riferimento verra' recuperato ed il file
	 * temporaneo inviato a docarea vera' cancellato).<br>
	 * N.B. se e' gia' presente un documento nella request, questo non viene sovrascritto ma il nuovo documento viene
	 * aggiunto, <b>purche'</b> sia diverso.
	 * 
	 * @param doc
	 * @throws Exception
	 */
	public static void putInRequest(Documento doc) throws Exception {
		SourceBean serviceRequest = RequestContainer.getRequestContainer().getServiceRequest();
		if (serviceRequest == null) {
			_logger.debug("La serviceRequest non e' presente nel RequestContainer");

			throw new Exception("La serviceRequest non e' presente nel RequestContainer");
		}
		Vector v = serviceRequest.getAttributeAsVector("theDocument");
		boolean esiste = false;
		for (int i = 0; i < v.size(); i++) {
			Documento d = (Documento) v.get(i);
			if (d.equals(doc)) {
				esiste = true;
				break;
			}
		}
		if (!esiste)
			serviceRequest.setAttribute("theDocument", doc);
	}

	/**
	 * Il codEnte viene registrato nel file di configurazione. Deve matchare con il cognome dell'utente docarea
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getCodEnte() throws Exception {
		SourceBean sb = leggiConfigurazione();
		return (String) sb.getAttribute("docarea.utente.codente");
	}

	/**
	 * Puo' essere letto dal db o dal file di configurazione.
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getUsernameEnte() throws Exception {
		if (leggiSempre && (tipoProtocollo() == ProtocolloDocumentoUtil.TIPO_DOCAREA_SARE)) {
			SourceBean sb = leggiConfigurazione();
			if ("S".equals(sb.getAttribute("leggi_da_db"))) {
				username = leggiUsernameEnteDalDB();
			} else {
				username = (String) leggiConfigurazione().getAttribute("docarea.utente.username");
			}
		}
		return username;
	}

	/**
	 * Puo' essere letto dal db o dal file di configurazione.
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getPasswordEnte() throws Exception {
		if (leggiSempre && (tipoProtocollo() == ProtocolloDocumentoUtil.TIPO_DOCAREA_SARE)) {
			SourceBean sb = leggiConfigurazione();
			if ("S".equals(sb.getAttribute("leggi_da_db"))) {
				password = leggiPasswordEnteDalDB();
			} else
				password = (String) sb.getAttribute("docarea.utente.password");
		}
		return password;
	}

	/**
	 * Si possono avere due tipi di invio: completo o parziale.<br>
	 * Nel caso <b>completo</b> il file inviato a docarea e' lo stesso che si registra in locale (nel caso di report
	 * manchera' il numero di protocollo).<br>
	 * Nel caso <b>parziale</b> si inviera' sempre un file vuoto. Bisogna fare questo controllo nei reports cosiddetti
	 * misti, ovvero creati con il designer ma a cui vengono passati i ResultSet direttamente nell'engine. (Vedere il
	 * report della diagnosi funzionale)
	 * 
	 * @return
	 * @throws Exception
	 */
	public static boolean invioCompleto() throws Exception {
		SourceBean sb = leggiConfigurazione();
		return "S".equals(sb.getAttribute("docarea.invio_completo"));
	}

	/**
	 * Possono esistere documenti gia' protocollati. Se true in fase di registrazione del documento bisogna saltare la
	 * fase di lettura e aggiornamento del protocollo (dato che e' possibile solo per la protocollazione estrerna, non
	 * bisogna chiamare il web service). Puo' essere configurato nella TS_CONFIG_LOC o nel file di configurazine
	 * nell'attributo DOCAREA.IMPORTAZIONE_DOC_PROTOCOLLATI
	 */
	public static boolean importDocProtocollati() throws Exception {
		if (leggiSempre) {
			SourceBean sb = leggiConfigurazione();
			if ("S".equals(sb.getAttribute("leggi_da_db"))) {
				importDocProtocollati = "S".equals(leggiImportDocProtocollatiDalDB());
			} else
				importDocProtocollati = "S".equals(sb.getAttribute("docarea.importazione_doc_protocollati"));
		}
		return importDocProtocollati;
	}

	/**
	 * Restituisce il path dove copiare i file inviati e protocollati in docarea.<br>
	 * Una volta completata l'operazione di protocollazione, se conclusa con successo il file verra' cancellato,
	 * altrimenti restera' nella directory. La presenza di un file indica quindi l'esistenza di un errore a cui porre
	 * rimedio con un inserimento nella tabella am_documento di un record fittizio in modo da riallineare il db locale
	 * del sil con quello di docarea in cui risulta esistere un documento protocollato con numero di protocollo uguale
	 * al nome del file.
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getPathFileInviato() throws Exception {
		SourceBean sb = leggiConfigurazione();
		String s = (String) sb.getAttribute("docarea.file.path_file_inviato");
		String path = ConfigSingleton.getRootPath() + File.separator + s;
		return path;
	}

	// --------------------------------------------------------
	// --------------------------------------------------------
	// INIZIALIZZAZIONE DELLE PROPRIETA' STATICHE DELLA CLASSE.
	static {
		SourceBean sb = null;
		try {
			sb = leggiConfigurazione();
			if (sb == null)
				throw new SourceBeanException("Configurazione del protocollo non trovata");
			// forzo la lettura dei dati
			leggiSempre = true;
			// metodi che valorizzano le proprieta' della classe
			tipoProtocollo();
			importDocProtocollati();
			getPasswordEnte();
			getUsernameEnte();
			// per ultima inizializzo "leggiSempre"
			int n = Integer.parseInt((String) sb.getAttribute("caricamento"));
			leggiSempre = (n == 0);
		} catch (Exception e) {
			leggiSempre = true;
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					"IMPOSSIBILE INIZIALIZZARE LA CONFIGURAZIONE DELLA PROTOCOLLAZIONE DOCAREA", e);

			_logger.fatal("CONFIGURATORE: " + sb + ", tipoProtocollo = " + tipo + ", importDocProtocollati = "
					+ importDocProtocollati + ", PasswordEnte = " + password + ", UsernameEnt = " + username);

			e.printStackTrace();
		}
	}

}