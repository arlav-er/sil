package it.eng.sil.module.collocamentoMirato;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.engiweb.framework.base.RequestContainer;

import it.eng.sil.Values;
import it.eng.sil.module.collocamentoMirato.processors.AggiornaDatiProspetto;
import it.eng.sil.module.collocamentoMirato.processors.CopiaProspetto;
import it.eng.sil.module.collocamentoMirato.processors.RicalcolaRiepilogoProspetto;
import it.eng.sil.module.collocamentoMirato.processors.VerificaProspetto;
import it.eng.sil.module.movimenti.MultipleTransactionQueryExecutor;
import it.eng.sil.module.movimenti.ResultLogger;

public class BackGroundCopiaProsp implements Runnable {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(BackGroundCopiaProsp.class.getName());
	private MultipleTransactionQueryExecutor trans = null;
	private RequestContainer reqCont = null;
	private ArrayList<BigDecimal> prgProspettiArray = new ArrayList();
	private String annoProspetto = null;
	private ValidatorCopia validator = null;
	private ResultLogger logger = null;

	public BackGroundCopiaProsp(RequestContainer reqContainer, ValidatorCopia validator, ResultLogger logger,
			ArrayList<BigDecimal> prgProspettiArray, String annoXCopia) {
		super();
		this.prgProspettiArray = prgProspettiArray;
		this.reqCont = reqContainer;
		this.validator = validator;
		this.logger = logger;
		this.annoProspetto = annoXCopia;
	}

	public void run() {
		RequestContainer.setRequestContainer(reqCont);
		try {
			trans = new MultipleTransactionQueryExecutor(Values.DB_SIL_DATI);
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"BackGroundCopiaProsp::run(): Eccezione nella creazione del MultipleTransactionQueryExecutor()", e);
			return;
		}

		try {
			BigDecimal user = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer()
					.getAttribute("_CDUT_");

			validator.addProcessor(new AggiornaDatiProspetto(trans, user));
			validator.addProcessor(new RicalcolaRiepilogoProspetto(trans, user));
			validator.addProcessor(new VerificaProspetto(trans, user));
			validator.addProcessor(new CopiaProspetto(trans, annoProspetto, user));

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"Impossibile configurare i processori per la copia dei prospetti.", e);
			trans.closeConnection();
			return;
		}

		// Setto i dati per il validator che effettua la copia massiva
		validator.setIdRecordKey("PRGPROSPETTOINF");
		validator.setIdRecordNewKey("PRGPROSPETTONEW");
		validator.setRecordExtractor(prgProspettiArray);
		try {
			// AVVIO DELLA COPIA DEI PROSPETTI
			validator.importRecords(logger, trans);
		} catch (Exception e) {
			trans.closeConnection();
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"BackGroundCopiaProsp::run(): Eccezione nella copia dei prospetti", e);
		}
		// chiusura della connessione
		trans.closeConnection();
		_logger.debug("BackGroundCopiaProsp::run(): Connessione chiusa al termine della copia dei prospetti");

		// rimozione dalla sessione dei riferimenti
		RequestContainer.getRequestContainer().getSessionContainer().delAttribute("COPIAPROSPETTOCORRENTE");
		RequestContainer.getRequestContainer().getSessionContainer().delAttribute("PROCESSOCOPIAPROSPETTOCORRENTE");
		_logger.debug("BackGroundCopiaProsp::run(): Oggetti per la copia dei prospetti eliminati dalla sessione");

	}

	protected void finalize() throws Throwable {
		super.finalize();
		trans.closeConnection();
		_logger.debug("BackGroundCopiaProsp::run(): Connessione chiusa dal Garbage Collector!!!");

	}

}