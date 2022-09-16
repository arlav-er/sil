package it.eng.sil.module.collocamentoMirato;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.configuration.ConfigSingleton;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.bean.Documento;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.LogException;
import it.eng.sil.security.User;

public class CMProtocollaProspDaSareModule extends AbstractSimpleModule {
	private static final Logger _logger = Logger.getLogger(CMProtocollaProspDaSareModule.class.getName());
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

			// verifica che non esiste un altro prospetto con gli stess: anno,
			// provincia e azienda
			SourceBean rows = null;
			Object[] fieldWhere = null;
			rows = (SourceBean) transExec.executeQuery("GET_LISTA_PROSP_DA_SARE", fieldWhere, "SELECT");
			Vector prospetti = rows.getAttributeAsVector("ROW");

			// per ogni prospetto cambiare numero protocollo
			for (int i = 0; i < prospetti.size(); i++) {
				SourceBean prospProto = (SourceBean) prospetti.get(i);

				BigDecimal prgAzienda = (BigDecimal) prospProto.getAttribute("PRGAZIENDA");
				BigDecimal prgUnita = (BigDecimal) prospProto.getAttribute("PRGUNITA");
				Object prgDocumentoProspetto = prospProto.getAttribute("PRGDOCUMENTO");
				BigDecimal prgProspettoInf = (BigDecimal) prospProto.getAttribute("PRGPROSPETTOINF");

				Object[] params = new Object[] { prgProspettoInf, prgProspettoInf };
				SourceBean result = null;

				Object queryRes = transExec.executeQuery("GET_UPDATE_COLL", params, "UPDATE");
				if (queryRes == null || !(queryRes instanceof Boolean && ((Boolean) queryRes).booleanValue() == true)) {
					throw new LogException("Impossibile eseguire update del documento");
				}

				// ricreare documento
				protocollaDocumento(prgAzienda, prgUnita, prgProspettoInf, transExec);

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

	/*
	 * public void insDocumento(SourceBean request, SourceBean rowProsp, TransactionQueryExecutor transqueryexec) throws
	 * Exception { Documento doc = new Documento();
	 * 
	 * doc.setPrgDocumento(null);
	 * 
	 * doc.setCodCpi(getAttributeAsString(request, "codCpi")); doc.setCdnLavoratore(getAttributeAsBigDecimal(request,
	 * "cdnLavoratore")); doc.setPrgAzienda(getAttributeAsBigDecimal(request, "prgAzienda"));
	 * doc.setPrgUnita(getAttributeAsBigDecimal(request, "prgUnita"));
	 * 
	 * doc.setCodTipoDocumento(getAttributeAsString(request, "codTipoDocumento"));
	 * doc.setFlgAutocertificazione(getAttributeAsString(request, "flgAutocertificazione"));
	 * doc.setStrDescrizione(getAttributeAsString(request, "strDescrizione"));
	 * doc.setFlgDocAmm(getAttributeAsString(request, "flgDocAmm")); doc.setFlgDocIdentifP(getAttributeAsString(request,
	 * "flgDocIdentifP")); doc.setDatInizio(getAttributeAsString(request, "DatInizio"));
	 * doc.setStrNumDoc(getAttributeAsString(request, "StrNumDoc"));
	 * doc.setStrEnteRilascio(getAttributeAsString(request, "StrEnteRilascio"));
	 * doc.setCodMonoIO(getAttributeAsString(request, "FlgCodMonoIO")); doc.setDatAcqril(getAttributeAsString(request,
	 * "DatAcqril")); doc.setCodModalitaAcqril(getAttributeAsString(request, "codModalitaAcqril"));
	 * doc.setCodTipoFile(getAttributeAsString(request, "codTipoFile")); doc.setStrNomeDoc(getAttributeAsString(request,
	 * "strNomeDoc")); doc.setDatFine(getAttributeAsString(request, "dataFine"));
	 * doc.setNumAnnoProt(getAttributeAsBigDecimal(request, "numAnnoProt"));
	 * doc.setNumProtocollo(getAttributeAsBigDecimal(request, "numProtocollo"));
	 * doc.setDatProtocollazione(getAttributeAsString(request, "dataOraProt"));
	 * doc.setTipoProt(getAttributeAsString(request, "tipoProt")); doc.setStrNote(getAttributeAsString(request,
	 * "strNote")); doc.setNumKeyLock(getAttributeAsBigDecimal(request, "KLOCKPROT"));
	 * doc.setChiaveTabella(getAttributeAsString(request, "strChiaveTabella"));
	 * doc.setCodMotAnnullamentoAtto(getAttributeAsString(request, "codMotAnnullamentoAtto"));
	 * doc.setCodStatoAtto(getAttributeAsString(request, "CODSTATOATTO"));
	 * 
	 * doc.setCdnUtMod(userid);
	 * 
	 * // 03/04/2007 DOCAREA. ProtocolloDocumentoUtil.putInRequest(doc); doc.insert(transqueryexec);
	 * 
	 * }
	 */

	private Documento protocollaDocumento(BigDecimal prgAzienda, BigDecimal prgUnita,
			BigDecimal prgprospettoinf_storicizzato, TransactionQueryExecutor tex) throws Exception {
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
			doc.setPrgAzienda(prgAzienda);
			doc.setPrgUnita(prgUnita);
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
			doc.setChiaveTabella(prgprospettoinf_storicizzato.toString());

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

	public String getAttributeAsString(SourceBean request, String param) {
		return SourceBeanUtils.getAttrStrNotNull(request, param);
	}

	public BigDecimal getAttributeAsBigDecimal(SourceBean request, String param) {
		String tmp = SourceBeanUtils.getAttrStrNotNull(request, param);
		if (!tmp.equals("")) {
			return new BigDecimal(tmp);
		}
		return null;
	}

}
