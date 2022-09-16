package it.eng.sil.module.amministrazione;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;

import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.ManualExtractor;
import it.eng.sil.module.movimenti.MultipleTransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordExtractor;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.module.movimenti.ResultLogger;
import it.eng.sil.module.movimenti.Validator;
import it.eng.sil.module.movimenti.processors.ControllaCampiValidazioneMobilita;
import it.eng.sil.module.movimenti.processors.ControllaEsistenzaMobilita;
import it.eng.sil.module.movimenti.processors.GestioneMovimentoXValidazioneMobilita;
import it.eng.sil.module.movimenti.processors.InsertAzienda;
import it.eng.sil.module.movimenti.processors.InsertLavoratore;
import it.eng.sil.module.movimenti.processors.InsertMobilitaXValidazioneMass;
import it.eng.sil.util.amministrazione.impatti.DBLoad;

public class M_MobValidaMobilita extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(M_MobValidaMobilita.class.getName());

	/** Informazioni di connessione col DB */
	String pool = null;

	public M_MobValidaMobilita() {
	}

	/**
	 * Inserimento del movimento. Esegue prima i controlli necessari e poi esegue l'inserimento. Se l'inserimento va a
	 * buon fine viene chiamato il publisher per la consultazione, altrimenti ritorna alla pagina di inserimento.
	 */
	public void service(SourceBean request, SourceBean response) throws SourceBeanException {

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		RequestContainer req = RequestContainer.getRequestContainer();
		SessionContainer ses = req.getSessionContainer();
		ArrayList prgMobAppArray = new ArrayList();
		Validator validator = null;
		ResultLogger logger = null;
		prgMobAppArray.add(new BigDecimal(request.getAttribute("prgmobilitaiscrapp").toString()));

		SourceBean sbGenerale = null;
		try {
			sbGenerale = DBLoad.getInfoGenerali();
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile leggere dalla ts_generale. ", e);
			return;
		}

		SourceBean sbApp = null;
		SourceBean sbConfigLoc = null;
		Vector vettConfigLoc = getServiceResponse().getAttributeAsVector("M_MOB_GETCONFIGLOC.ROWS.ROW");
		if (vettConfigLoc != null) {
			for (int i = 0; i < vettConfigLoc.size(); i++) {
				sbApp = (SourceBean) vettConfigLoc.get(i);
				if (sbApp.containsAttribute("STRVALORE")
						&& sbApp.getAttribute("STRVALORE").toString().equalsIgnoreCase("VALMAN")) {
					sbConfigLoc = new SourceBean("CONFIG");
					BigDecimal num = (BigDecimal) sbApp.getAttribute("NUM");
					sbConfigLoc.setAttribute("NUMCONFIGLOC", num);
					break;
				}
			}
		}
		String configbase = "";
		String configFileName = "";
		RecordExtractor extr;
		try {
			configbase = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "conf"
					+ File.separator + "import" + File.separator;
			configFileName = configbase + "ManualValidationMobFieldsMapping.xml";
			SourceBean sessionOrigin = null;
			extr = new ManualExtractor(request,
					(SourceBean) request.getAttribute("M_MobilitaGetDettaglioMobApp.ROWS.ROW"), sessionOrigin,
					configFileName);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile istanziare l'estrattore per i record. ", e);
			return;
		}
		validator = new Validator();
		// Path dei file di configurazione
		String processorbase = configbase + "processors" + File.separator;
		MultipleTransactionQueryExecutor trans = null;
		SourceBean result = null;

		try {
			trans = new MultipleTransactionQueryExecutor(Values.DB_SIL_DATI);

			BigDecimal user = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer()
					.getAttribute("_CDUT_");
			// Inserimento Lavoratore
			validator.addProcessor(new ControllaCampiValidazioneMobilita("Controlli campi", "MANUALE", trans));
			RecordProcessor insLav = new InsertLavoratore("Inserimento Lavoratore nel DB", trans, user,
					processorbase + "insertLavoratore.xml", sbGenerale);
			validator.addProcessor(insLav);
			// Inserimento Azienda
			RecordProcessor insAz = new InsertAzienda("Inserimento Azienda nel DB", trans, user,
					processorbase + "insertAzienda.xml", sbGenerale);
			validator.addProcessor(insAz);
			// Processor che controlla l'esistenza o meno della mobilità nel db
			RecordProcessor esistenzaMob = new ControllaEsistenzaMobilita("Controllo esistenza mobilità nel DB", trans);
			validator.addProcessor(esistenzaMob);
			// Processor che gestisce il movimento che ha portato il lavoratore
			// in mobilità
			RecordProcessor gestioneMovimento = new GestioneMovimentoXValidazioneMobilita(
					"Inserimento movimento nel DB", sbConfigLoc, trans, request, user, sbGenerale, logger);
			validator.addProcessor(gestioneMovimento);
			// Processor che inserisce la mobilità
			RecordProcessor insMobilita = new InsertMobilitaXValidazioneMass("Inserimento Mobilità nel DB", trans, user,
					req, sbGenerale);
			validator.addProcessor(insMobilita);

			// Imposto i dati necessari nell'importer
			validator.setIdRecordKey("PRGMOBILITAISCRAPP");
			validator.setRecordExtractor(extr);

			result = validator.importRecords(null, trans);

			if (!validator.getErrorValidator()) {
				result.setAttribute("VALIDAZIONE_MANUALE", "OK");
				Map record = ((ManualExtractor) extr).getData();
				String prgAzienda = record.containsKey("PRGAZIENDA") ? record.get("PRGAZIENDA").toString() : "";
				String prgUnita = record.containsKey("PRGUNITAPRODUTTIVA") ? record.get("PRGUNITAPRODUTTIVA").toString()
						: "";
				String cdnlavoratore = record.containsKey("CDNLAVORATORE") ? record.get("CDNLAVORATORE").toString()
						: "";
				result.setAttribute("PRGAZIENDA_VALIDAZIONEMOB", prgAzienda);
				result.setAttribute("PRGUNITA_VALIDAZIONEMOB", prgUnita);
				result.setAttribute("CDNLAVORATORE_VALIDAZIONEMOB", cdnlavoratore);

			}
			// inserimento dei dati nella response del modulo
			response.setAttribute(result);

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Impossibile configurare i "
					+ "processori per la validazione delle mobilità, controllare i file XML di configurazione. ", e);
			return;
		}

		finally {
			if (trans != null) {
				// Chiudo la connessione del MultipleTransactionQueryExecutor
				trans.closeConnection();
			}
		}
	}
}