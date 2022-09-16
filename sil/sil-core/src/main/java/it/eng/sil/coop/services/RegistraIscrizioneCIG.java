package it.eng.sil.coop.services;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFErrorHandler;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.SynchronizedSourceBean;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.cig.bean.CIDomandaCIGBean;
import it.eng.sil.cig.bean.CigConst;
import it.eng.sil.coop.services.logger.DBErrorLogger;

/**
 * 
 * @author Esposito,Rodi
 *
 */
@SuppressWarnings("unchecked")
public class RegistraIscrizioneCIG implements IFaceService {
	private static final Logger _logger = Logger.getLogger(RegistraIscrizioneCIG.class.getName());
	private static final DBErrorLogger _dbLogger = new DBErrorLogger();

	TransactionQueryExecutor tex = null;
	CIDomandaCIGBean domandaCIG = null;
	String _codAccordoLog = "";
	public static final String CDNUTENTE = "190";

	public void elaboraXML(String iscrizioneCIGXML) throws Exception {

		RequestContainer rc = null;
		ResponseContainer rs = null;
		SourceBean iscrizioneCIG = null;
		BigDecimal prgAccordo = null;

		// 19/02/2010 rodi
		// IMPORTANTE!!! SourceBean deve essere costruito in maniera sincronizzata!!
		iscrizioneCIG = SynchronizedSourceBean.fromXMLStringSynch(iscrizioneCIGXML);

		tex = new TransactionQueryExecutor(Values.DB_SIL_DATI);
		tex.initTransaction();

		rc = new RequestContainer();
		RequestContainer.setRequestContainer(rc);
		rs = new ResponseContainer();
		rs.setErrorHandler(new EMFErrorHandler());
		SessionContainer session = new SessionContainer(false);

		session.setAttribute("_CDUT_", new BigDecimal(CDNUTENTE));
		rc.setSessionContainer(session);

		_codAccordoLog = (String) iscrizioneCIG.getCharacters("DATIACCORDO.CODACCORDO");

		_logger.info("-- inizio a processare domanda CIG " + _codAccordoLog + " --");

		// _logger.info(iscrizioneCIGXML);
		// _logger.info(iscrizioneCIG);

		domandaCIG = new CIDomandaCIGBean(iscrizioneCIG, CDNUTENTE, CDNUTENTE, tex);

		if (domandaCIG.getCiAccBean().isEsisteAltroCodAccordo()) {
			_logger.info("esiste un altra domanda con lo stesso codaccordo");

			if (domandaCIG.getCiAccBean().getCodstatoatto().equals(CigConst.ACC_ANNULLATO)) {
				// CASO A: ANNULLAMENTO DOMANDA PRECEDENTE

				domandaCIG.annullaDomanda(CDNUTENTE);

				domandaCIG.annullaLavoratori(CDNUTENTE, CigConst.ISCR_ANNULLAMENTO_PER_ANNULLAMENTO_DOMANDA);

			} else {
				// CASO C: VARIAZIONE DOMANDA PRECEDENTE
				_logger.info("variazione domanda precedentemente acquisita");
				domandaCIG.insertAccordo();
				domandaCIG.variaDomanda(CDNUTENTE, CigConst.ACC_CHIUSO);

				prgAccordo = new BigDecimal(domandaCIG.getCiAccBean().getPrgaccordo());

				// imposto il prgaccordo nell'esame congiunto
				if (domandaCIG.getCiEsameCong() != null) {
					domandaCIG.getCiEsameCong().setPrgaccordo(prgAccordo.toString());

					domandaCIG.insertEsameCong();
				}

				domandaCIG.insertAzienda();

				domandaCIG.insertSindacati(prgAccordo.toString());

				domandaCIG.insertSedi();

				domandaCIG.insertRelAccordi(prgAccordo.toString());

				domandaCIG.rettificaLavoratori(CDNUTENTE, CigConst.ACC_CHIUSO, prgAccordo.toString());

				domandaCIG.annullaLavoratori(CDNUTENTE, CigConst.ISCR_ANNULLAMENTO_PER_NON_APPROVAZIONE);
			}

		} else {
			_logger.info("non esiste un altra domanda con lo stesso codaccordo");
			// se esiste il codice domanda precedente
			if (domandaCIG.getCiAccBean().getCodaccordoorig() != null
					&& !domandaCIG.getCiAccBean().getCodaccordoorig().equals("")
					&& (domandaCIG.getCiAccBean().isEsisteDomandaPrec())) {
				// CASO B: RETTIFICA DOMANDA PRECEDENTE
				_logger.info("rettifica domanda precedemente acquisita " + domandaCIG.getCiAccBean().getCodaccordoorig()
						+ " impostando il codstatoatto ad  AR (Annullata/Rettificata)");
				domandaCIG.insertAccordo();
				domandaCIG.rettificaDomanda(CDNUTENTE, CigConst.ACC_RETTIFICATO);

				prgAccordo = new BigDecimal(domandaCIG.getCiAccBean().getPrgaccordo());

				if (domandaCIG.getCiEsameCong() != null) {
					domandaCIG.getCiEsameCong().setPrgaccordo(prgAccordo.toString());

					domandaCIG.insertEsameCong();
				}

				domandaCIG.insertAzienda();

				domandaCIG.insertSindacati(prgAccordo.toString());

				domandaCIG.insertSedi();

				domandaCIG.insertRelAccordi(prgAccordo.toString());

				domandaCIG.rettificaLavoratori(CDNUTENTE, CigConst.ACC_RETTIFICATO, prgAccordo.toString());

				domandaCIG.annullaLavoratori(CDNUTENTE, CigConst.ISCR_ANNULLAMENTO_PER_RETTIFICA);
			} else { // se non esiste la domanda precedente o se non c'è un codaccordoorig

				if (domandaCIG.getCiAccBean().getCodstatoatto().equals(CigConst.ACC_ANNULLATO)
						|| domandaCIG.getCiAccBean().isEsisteDomandaSucc()) {
					// CASO E: ANNULLAMENTO DOMANDA NON PRESENTE
					_logger.info("annullamento domanda non presente");
					domandaCIG.insertAll(false);
				} else {
					// CASO D: INSERIMENTO NUOVA DOMANDA O RETTIFICA DOMANDA NON PRESENTE
					_logger.info("inserimento nuova domanda o rettifica domanda non esistente");
					domandaCIG.insertAll(true);
				}

			}
		}

		tex.commitTransaction();
		// tex.rollBackTransaction();

	}

	public void send(Message msg) {

		String iscrizioneCIGXML = null;

		try {
			ObjectMessage message = (ObjectMessage) msg;
			Serializable arrObj;
			arrObj = message.getObject();
			List l = (List) arrObj;
			iscrizioneCIGXML = (String) l.get(0);

			elaboraXML(iscrizioneCIGXML);
		} catch (JMSException e) {
			_logger.error("Errore nell'estrazione della comunicazione dal messaggio JMS", e);
		} catch (CigException ce) {
			try {
				if (tex != null)
					tex.rollBackTransaction();
			} catch (EMFInternalError e1) {
				_logger.error("RegistraIscrizioneCIG: problema con la rollback", e1);
			}
			_logger.error("RegistraIscrizioneCIG fallita:\n" + ce.getStrsqlerrm()
					+ "\nMaggiori dettagli sulla tabella ts_ws_err\n", ce);
			try {
				_dbLogger.log(_logger, "CI", domandaCIG.getCiAccBean().getCodaccordo(), iscrizioneCIGXML,
						ce.getStrsqlcode(), ce.getStrsqlerrm());
			} catch (Exception se) {
				_logger.error("RegistraIscrizioneCIG: log su db fallito. \n", se);
			}
		} catch (EMFInternalError error) {
			try {
				if (tex != null)
					tex.rollBackTransaction();
			} catch (EMFInternalError e1) {
				_logger.error("RegistraIscrizioneCIG: problema con la rollback", e1);
			}
			_logger.error("RegistraIscrizioneCIG fallita:\n" + error.getMessage()
					+ "\nMaggiori dettagli sulla tabella ts_ws_err\n");
			try {
				_dbLogger.log(_logger, "CI", domandaCIG.getCiAccBean().getCodaccordo(), iscrizioneCIGXML, "-1",
						error.getMessage());
			} catch (ClassCastException cast) {
				_logger.error("RegistraIscrizioneCIG fallita:\nMaggiori dettagli sulla tabella ts_ws_err\n", error);
			} catch (Exception se) {
				_logger.error("RegistraIscrizioneCIG: log su db fallito. \n", se);
			}
		} catch (Exception e) {
			try {
				if (tex != null)
					tex.rollBackTransaction();
			} catch (EMFInternalError e1) {
				_logger.error("RegistraIscrizioneCIG: problema con la rollback", e1);
			}
			_logger.error("RegistraIscrizioneCIG: fallita. \n" + iscrizioneCIGXML, e);
			try {
				_dbLogger.log(_logger, "CI", _codAccordoLog, iscrizioneCIGXML, "-1", e.getMessage());
			} catch (Exception se) {
				_logger.error("RegistraIscrizioneCIG: log su db fallito. \n", se);
			}
		} finally {
			_logger.info("-- fine domanda CIG " + _codAccordoLog + " --");
		}

	}
}
