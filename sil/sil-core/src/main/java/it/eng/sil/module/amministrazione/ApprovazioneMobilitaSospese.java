package it.eng.sil.module.amministrazione;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class ApprovazioneMobilitaSospese extends AbstractSimpleModule {
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(ApprovazioneMobilitaSospese.class.getName());

	private int erroreControlli = 0;

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {
		Vector vettIscr = serviceRequest.getAttributeAsVector("checkboxmob");
		int sizeIscr = vettIscr.size();
		String dataApp = StringUtils.getAttributeStrNotNull(serviceRequest, "datApprovazione");
		String numApp = StringUtils.getAttributeStrNotNull(serviceRequest, "numCRTApprovazione");
		String regApp = StringUtils.getAttributeStrNotNull(serviceRequest, "regioneCRTApprov");
		String provApp = StringUtils.getAttributeStrNotNull(serviceRequest, "provCRTApprov");
		String statoApp = StringUtils.getAttributeStrNotNull(serviceRequest, "statoApprov");
		TransactionQueryExecutor trans = null;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		Object[] parmsPerNumklo = new Object[1];
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);

		try {
			if (sizeIscr > 0) {
				// controllo obbligatorietà dei campi relativi all'approvazione
				if ((dataApp.equals("")) || (numApp.equals("")) || (statoApp.equals(""))) {
					erroreControlli = MessageCodes.ApprovazioneMobilita.ERROR_CAMPI_OBBLIGATORI;
					throw new Exception("Data approvazione, numero approvazione e stato sono obbligatori.");
				}

				if ((provApp.equals("")) && (regApp.equals(""))) {
					erroreControlli = MessageCodes.ApprovazioneMobilita.ERROR_ENTE_APPROVAZIONE_OBBLIGATORIO;
					throw new Exception("Bisogna indicare l'ente, regione o provincia.");
				}

				if ((!provApp.equals("")) && (!regApp.equals(""))) {
					erroreControlli = MessageCodes.ApprovazioneMobilita.ERROR_ENTE_APPROVAZIONE_REG_PROV;
					throw new Exception("Bisogna indicare come ente la regione o la provincia.");
				}

				trans = new TransactionQueryExecutor(getPool(), this);
				enableTransactions(trans);
				trans.initTransaction();

				for (int i = 0; i < sizeIscr; i++) {
					Object prgMob = vettIscr.elementAt(i);
					if (!prgMob.equals("")) {
						parmsPerNumklo[0] = prgMob;
						SourceBean numKlo = (SourceBean) trans.executeQuery("SELECT_NUMKLO_MOB", parmsPerNumklo,
								"SELECT");
						BigDecimal numkloiscr = (BigDecimal) numKlo.getAttribute("ROW.NUMKLOMOBISCR");
						numkloiscr = numkloiscr.add(new BigDecimal("1"));

						String sqlUpdate = "UPDATE AM_MOBILITA_ISCR SET NUMKLOMOBISCR = " + numkloiscr;

						if (!dataApp.equals("")) {
							sqlUpdate = sqlUpdate + ", DATCRT = TO_DATE('" + dataApp + "', 'DD/MM/YYYY') ";
						}

						if (!numApp.equals("")) {
							sqlUpdate = sqlUpdate + ", STRNUMATTO = '" + numApp + "' ";
						}

						if (!regApp.equals("")) {
							sqlUpdate = sqlUpdate + ", CODREGIONE = '" + regApp + "' ";
						}

						if (!provApp.equals("")) {
							sqlUpdate = sqlUpdate + ", CODPROVINCIA = '" + provApp + "' ";
						}

						if (!statoApp.equals("")) {
							sqlUpdate = sqlUpdate + ", CDNMBSTATORICH = '" + statoApp + "' ";
						}

						sqlUpdate = sqlUpdate
								+ ", CODTIPOMOB = (case when codtipomob = 'S4' then 'LA' when codtipomob = 'S2' then 'LC' when codtipomob = 'S9' then 'LT' end) ";
						sqlUpdate = sqlUpdate + " WHERE PRGMOBILITAISCR = " + prgMob;

						//
						Object result = trans.executeQueryByStringStatement(sqlUpdate, null, "UPDATE");
						if (result instanceof Exception) {
							_logger.debug(result.toString());
							throw new Exception("aggiornamento iscrizioni da approvare fallito");
						} else if (result instanceof Boolean && ((Boolean) result).booleanValue() == false) {
							throw new Exception("aggiornamento iscrizioni da approvare fallito");
						}
					}
				}
				//
				trans.commitTransaction();
				this.setMessageIdSuccess(MessageCodes.General.UPDATE_SUCCESS);
				reportOperation.reportSuccess(idSuccess);
			}
		} catch (Exception e) {
			if (trans != null) {
				trans.rollBackTransaction();
			}
			if (erroreControlli > 0) {
				reportOperation.reportFailure(erroreControlli);
			} else {
				reportOperation.reportFailure(MessageCodes.General.UPDATE_FAIL);
			}
		}
	}

}
