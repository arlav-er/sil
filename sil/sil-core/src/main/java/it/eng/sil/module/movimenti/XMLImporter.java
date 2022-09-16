package it.eng.sil.module.movimenti;

import java.io.File;
import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;

import it.eng.sil.Values;
import it.eng.sil.module.movimenti.processors.InsertData;
import it.eng.sil.module.movimenti.processors.SelectFieldsMovimentoAppoggio;
import it.eng.sil.module.movimenti.processors.TransformCodTempo;

/**
 * Classe di implementazione per i movimenti in arrivo da Sare
 * <p>
 * 
 * @author Paolo Roccetti
 */
public class XMLImporter {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(XMLImporter.class.getName());

	private String className = this.getClass().getName();
	private String PWD_DA_CONTROLLARE = "TODO";

	/** intestazione per il parsing delle richieste */
	static final String headerDTD = "<?xml version=\"1.0\"?>" + " <!DOCTYPE RECORDS [ "
			+ " <!ELEMENT RECORDS (RECORD*)> " + " <!ELEMENT RECORD (FIELD*)> "
			+ " <!ATTLIST RECORDS recordNumber CDATA #IMPLIED> " + " <!ATTLIST RECORD version CDATA \"DEFAULT\"> "
			+ " <!ATTLIST FIELD name CDATA #REQUIRED " + " format (number|char|date|flag) \"char\" "
			+ " data CDATA #REQUIRED> " + " ]> ";

	/**
	 * Costruttore
	 */
	public XMLImporter() {
	}

	/**
	 * Metodo per l'importazione
	 * <p>
	 * 
	 * @param records
	 *            Una stringa contenente il tracciato XML da importare. Il tracciato indicato verrà validato attraverso
	 *            la DTD seguente: <br>
	 *            <!ELEMENT RECORDS (RECORD*)> <!ELEMENT RECORD (FIELD*)> <!ATTLIST RECORDS recordNumber CDATA #IMPLIED>
	 *            <!ATTLIST RECORD version CDATA "DEFAULT"> <!ATTLIST FIELD name CDATA #REQUIRED format
	 *            (number|char|date|flag) "char" data CDATA #REQUIRED>
	 *            <p>
	 * @return Una stringa XML che indica il risultato dell'elaborazione.
	 */
	public String service(String password, String records) throws SourceBeanException {

		System.out.println(records);

		if (password == null || records == null) {
			return "<RESULT><ERROR>Argomenti nulli!</ERROR></RESULT>";
		}
		// Controllo che la Password corrisponda
		if (!password.equals(PWD_DA_CONTROLLARE)) {
			return "<RESULT><ERROR>Password non valida!</ERROR></RESULT>";
		}

		if (records.equals("")) {
			return "<RESULT><ERROR>Richiesta vuota!</ERROR></RESULT>";
		}

		// Aggiungo alla stringa passata l'intestazione con la DTD
		String completeXMLReq = headerDTD + records;

		// Istanzio l'estrattore (che esegue il parsing della stringa e ne
		// controlla la correttezza formale)
		RecordExtractor extr;
		try {
			extr = new XMLExtractor(completeXMLReq);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"Impossibile istanziare l'estrattore per i record da importare. ", e);

			return "<RESULT><ERROR>Problemi nel parsing dei record inviati. Controllare il formato dei dati inviati. Dettagli: "
					+ e.toString() + "</ERROR></RESULT>";
		}

		// MultipleTransactionQueryExecutor da utilizzare
		MultipleTransactionQueryExecutor trans = null;
		try {
			trans = new MultipleTransactionQueryExecutor(Values.DB_SIL_DATI);
		} catch (Exception error) {
			_logger.debug("Impossibile istanziare il MultipleTransactionQueryExecutor per inserire i dati sul DB.");

			return "<RESULT><ERROR>Impossibile istanziare il MultipleTransactionQueryExecutor per inserire i dati nel DB.</ERROR></RESULT>";
		}

		// creo l'oggetto che si occuperà di effettuare l'importazione vera e
		// propria
		Importer importer = new Importer();
		// Creo il nome del file di configurazione per l'estrattore
		String configbase = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "conf"
				+ File.separator + "import" + File.separator;
		// Configuro i processori necessari e li aggiungo all'importer
		RecordProcessor procCodTempo = new TransformCodTempo("Trasforma_Tempo_Avviamento");
		RecordProcessor selectCorrectFields = new SelectFieldsMovimentoAppoggio("Selezione_Dati_Movimento");
		String configproc = configbase + "processors" + File.separator + "InsertAM_MOVIMENTI_APPOGGIO.xml";
		RecordProcessor insertMovApp;
		try {
			insertMovApp = new InsertData("Inserisci_Movimento_Appoggio", trans, configproc,
					"INSERT_MOVIMENTO_APPOGGIO", new BigDecimal(1));
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"Impossibile istanziare il processore per i record da importare. ", e);

			// Cerco di chiudere la connessione del
			// MultipleTransactionQueryExecutor
			trans.closeConnection();

			return "<RESULT><ERROR>Problemi nella configurazione della logica di importazione. Dettagli: "
					+ e.toString() + "</ERROR></RESULT>";
		}
		importer.addProcessor(procCodTempo);
		importer.addProcessor(selectCorrectFields);
		importer.addProcessor(insertMovApp);

		// Imposto i dati necessari nell'importer
		importer.setIdRecordKey("RECORDNUMBER");
		importer.setRecordExtractor(extr);
		// Processo i records
		SourceBean result = null;
		try {
			result = importer.importRecords(trans);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile importare i record specificati. ", e);

			// Cerco di chiudere la connessione del
			// MultipleTransactionQueryExecutor
			trans.closeConnection();

			return "<RESULT><ERROR>Problemi durante l'importazione dei records. Dettagli: " + e.toString()
					+ "</ERROR></RESULT>";
		}

		// Cerco di chiudere la connessione del MultipleTransactionQueryExecutor
		trans.closeConnection();

		return "<RESULT>" + GraficaUtils.generaXML(result) + "</RESULT>";

	}
}