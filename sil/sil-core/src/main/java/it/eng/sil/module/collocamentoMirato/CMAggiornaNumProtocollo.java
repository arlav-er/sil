package it.eng.sil.module.collocamentoMirato;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.bean.Documento;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

public class CMAggiornaNumProtocollo extends AbstractSimpleModule {
	private static final Logger _logger = Logger.getLogger(CMAggiornaNumProtocollo.class.getName());
	BigDecimal userid = new BigDecimal("200");
	TransactionQueryExecutor transExec = null;
	BigDecimal prgDocumento = null;
	String numkloDocumento = "";

	/*
	 * (non Javadoc)
	 * 
	 * @see com.engiweb.framework.dispatching.service.ServiceIFace#service(com.engiweb.framework.base.SourceBean,
	 * com.engiweb.framework.base.SourceBean)
	 */
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		userid = new BigDecimal(user.getCodut());
		boolean check = true;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		int msgCode = MessageCodes.General.INSERT_FAIL;

		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);
			transExec.initTransaction();

			// per ogni prospetto cambiare numero protocollo
			for (int i = 0; i < 82000; i++) {

				String prosp = "" + i;
				// ricreare documento
				protocollaDocumento(null, null, prosp, transExec);

			}

			requestContainer.setServiceRequest(serviceRequest);

			transExec.commitTransaction();

			reportOperation.reportSuccess(idSuccess);

		} catch (Exception e) {
			transExec.rollBackTransaction();
			reportOperation.reportFailure(msgCode, e, "services()", "insert in transazione");

		} finally {
		}
	}

	private Documento protocollaDocumento(BigDecimal prgAzienda, BigDecimal prgUnita,
			String prgprospettoinf_storicizzato, TransactionQueryExecutor tex) throws Exception {
		Documento doc = null;
		try {
			doc = new Documento();
			String dataOdierna = DateUtils.getNow();
			SimpleDateFormat fd = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			String dataEOraProtocollo = fd.format(new Date());
			String annoProtocollo = dataOdierna.substring(6, 10);

			String utenteCoordinamento = "200";

			doc.setPrgDocumento(null); // nuovo documento
			doc.setCdnLavoratore(null);
			doc.setPrgAzienda(null);
			doc.setPrgUnita(null);
			doc.setCodTipoDocumento("PINF");
			doc.setStrDescrizione("Prospetto informativo " + prgprospettoinf_storicizzato);
			doc.setDatInizio(dataOdierna);
			doc.setCodMonoIO("I");
			doc.setTipoProt("S");
			doc.setDatAcqril(dataOdierna);
			// valori generici necessari per la protocollazione
			doc.setDatProtocollazione(dataEOraProtocollo);
			doc.setNumAnnoProt(new BigDecimal(annoProtocollo));
			doc.setNumProtocollo(new BigDecimal(0));
			doc.setCdnUtIns(new BigDecimal(utenteCoordinamento));
			doc.setCdnUtMod(new BigDecimal(utenteCoordinamento));
			doc.setCodStatoAtto("PR");
			doc.setPagina("CMProspRiepilogoPage");
			doc.setChiaveTabella(prgprospettoinf_storicizzato);

			SourceBean proto = (SourceBean) ConfigSingleton.getInstance().getAttribute("PROTOCOLLO");
			_logger.fatal("configurazione protocollo: " + proto);
			doc.insert(tex);
			return doc;

		} catch (Exception e) {
			StringBuffer msg = new StringBuffer(100);
			msg.append("protocollaDocumento() fallito. Servizio registra prospetto informativo da NCR. ");
			msg.append("prgAzienda=");
			msg.append(prgAzienda);
			msg.append(", prgUnita=");
			msg.append(prgUnita);

			if (_logger.isEnabledFor(Level.DEBUG))
				_logger.debug(msg.toString(), e);
			throw e;
		}
	}

}
