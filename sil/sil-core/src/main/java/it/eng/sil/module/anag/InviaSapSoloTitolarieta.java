package it.eng.sil.module.anag;

import java.rmi.RemoteException;

import javax.xml.rpc.JAXRPCException;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.sap.BeanSap;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.pojo.yg.sap.due.LavoratoreType;
import it.eng.sil.util.Utils;

public class InviaSapSoloTitolarieta extends AbstractSimpleModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8134045080554899783L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InviaSapSoloTitolarieta.class.getName());

	public void service(SourceBean request, SourceBean response) {

		ReportOperationResult reportOperation = null;
		TransactionQueryExecutor transExec = null;
		try {
			reportOperation = new ReportOperationResult(this, response);
			disableMessageIdSuccess();
			disableMessageIdFail();
			String cdnLavoratore = Utils.notNull(request.getAttribute("CDNLAVORATORE"));
			String codMinSap = Utils.notNull(request.getAttribute("CODMINSAP"));

			if (StringUtils.isFilledNoBlank(codMinSap)) {
				try {
					Object[] params = new Object[1];
					params[0] = cdnLavoratore;
					SourceBean sbCpi = (SourceBean) QueryExecutor.executeQuery("GET_CPI_AN_LAVORATORE", params,
							"SELECT", Values.DB_SIL_DATI);

					SourceBean serviceRequest = getRequestContainer().getServiceRequest();
					serviceRequest.setAttribute("INVIASAPSOLOTITOLARIETA", "S");

					LavoratoreType xmlSapLavoratoreMin = null;
					try {
						xmlSapLavoratoreMin = BeanSap.sendRichiestaSAP(codMinSap);
						if (xmlSapLavoratoreMin != null && xmlSapLavoratoreMin.getDatiinvio() != null) {
							xmlSapLavoratoreMin.getDatiinvio()
									.setDataultimoagg(DateUtils.toXMLGregorianCalendarDate(DateUtils.getNow()));
							xmlSapLavoratoreMin.getDatiinvio()
									.setTipovariazione(GestioneInviaSapYG.VARIAZIONE_AGGIORNAMENTO);
							if (sbCpi != null) {
								String codiceEnteTitMinisteriale = (String) sbCpi.getAttribute("ROW.cpicompmin");
								xmlSapLavoratoreMin.getDatiinvio().setCodiceentetit(codiceEnteTitMinisteriale);
							}
							String xmlSap = BeanSap.convertLavoratoreTypeToString(xmlSapLavoratoreMin);

							int returnInvioSap;
							if (BeanSap.validazioneXml(xmlSap, null)) {
								returnInvioSap = BeanSap.invioSap(xmlSap, cdnLavoratore);
								try {
									transExec = new TransactionQueryExecutor(Values.DB_SIL_DATI);
									transExec.initTransaction();

									Object[] paramsIns = new Object[9];
									paramsIns[0] = BeanSap.getPrgTracciamentoSap(transExec);
									paramsIns[1] = cdnLavoratore;
									paramsIns[2] = xmlSap;
									paramsIns[3] = null;
									paramsIns[4] = codMinSap;
									paramsIns[5] = null;
									paramsIns[6] = null;
									paramsIns[7] = returnInvioSap;
									paramsIns[8] = GestioneInviaSapYG.VARIAZIONE_AGGIORNAMENTO;

									Object queryResUpdTracciatoCodVariazione = transExec
											.executeQuery("INSERT_TS_TRACCIAMENTO_SAP_FOR_BATCH", paramsIns, "INSERT");
									if (queryResUpdTracciatoCodVariazione == null
											|| !(queryResUpdTracciatoCodVariazione instanceof Boolean
													&& ((Boolean) queryResUpdTracciatoCodVariazione)
															.booleanValue() == true)) {
										transExec.rollBackTransaction();
										_logger.debug("Errore inserimento TS_TRACCIAMENTO_SAP");
									} else {
										transExec.commitTransaction();
									}
								} catch (Exception e) {
									if (transExec != null) {
										try {
											transExec.rollBackTransaction();
											_logger.debug(
													"InviaSapSoloTitolarieta:service() - TransactionQueryExecutor rollback...");
										} catch (EMFInternalError emf) {
											_logger.error(emf.getMessage());
										}
									}
								}
								switch (returnInvioSap) {
								case 0:
									response.setAttribute("SAPSOLOTITOLARIETAINVIATA", "S");
									reportOperation
											.reportSuccess(MessageCodes.Trasferimento.SAP_INVIATA_SOLO_TITOLARIETA);
									break;

								default:
									break;
								}
							} else {
								throw new Exception(
										"InviaSapSoloTitolarieta:service(): Errore in validazione input xml sap da reinviare.");
							}
						}
					} catch (JAXRPCException ie) {
						throw new Exception(ie);
					} catch (RemoteException iee) {
						throw new Exception(iee);
					}

				} catch (Exception me) {
					if (!response.containsAttribute("ERRORENOLOG")) {
						it.eng.sil.util.TraceWrapper.error(_logger, "InviaSapSoloTitolarieta:service():errore ", me);
					}
					reportOperation.reportFailure(me, "InviaSapSoloTitolarieta:service()", me.getMessage());
				}
			} else {
				throw new Exception("InviaSapSoloTitolarieta:service(): CODMINSAP NULL");
			}
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "InviaSapSoloTitolarieta:service()", e);
			reportOperation.reportFailure(e, "InviaSapSoloTitolarieta:service()", e.getMessage());
		}
	}

}