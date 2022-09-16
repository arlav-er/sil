/*
 * Creato il 13-nov-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.anag;

import java.math.BigDecimal;
import java.util.Vector;

/**
 * @author melandri
 *
 * Per modificare il modello associato al commento di questo tipo generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes; //di errore:"dati salvati corrrettamente" "..erroneamente" etc.
import it.eng.afExt.utils.ReportOperationResult; //Servono per per gestire i messaggi
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class SaveRichEsonero extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			if ((getAttributeAsString(request, "updateVariazioni")).equals("1")) {
				BigDecimal numPercEsonero = getAttributeAsBigDecimal(request, "NUMPERCESONERO");

				boolean anomaliaDisabiliCalcEff = false;

				this.setSectionQuerySelect("QUERY_SELECT_VAR_DIS");
				SourceBean rowsVariaz = this.doSelect(request, response);
				Vector rowsVariazVec = rowsVariaz.getAttributeAsVector("ROW");

				for (int i = 0; i < rowsVariazVec.size(); i++) {

					SourceBean rowVar = (SourceBean) rowsVariazVec.get(i);

					BigDecimal prgRichEsonDisabili = getAttributeAsBigDecimal(rowVar, "PRGRICHESONDISABILI");
					BigDecimal numBaseComputo = getAttributeAsBigDecimal(rowVar, "NUMBASECOMPUTO");
					BigDecimal numDisabiliEffett = getAttributeAsBigDecimal(rowVar, "NUMDISABILIEFFETT");

					BigDecimal numDisabiliNew = calcolaNumDisabili(numBaseComputo.doubleValue(),
							numPercEsonero.doubleValue());

					if (numDisabiliEffett.compareTo(numDisabiliNew) > 0) {
						anomaliaDisabiliCalcEff = true;
					}

					request.delAttribute("numDisabiliNew");
					request.delAttribute("prgRichEsonDisabili");
					request.setAttribute("numDisabiliNew", numDisabiliNew);
					request.setAttribute("prgRichEsonDisabili", prgRichEsonDisabili);

					this.setSectionQueryUpdate("QUERY_UPDATE_VAR_DIS");
					ret = doUpdate(request, response);

					if (!ret) {
						throw new Exception("impossibile aggiornare CM_RICH_ESON_DISABILI in transazione");
					}
				}
				if (anomaliaDisabiliCalcEff) {
					reportOperation.reportWarning(
							MessageCodes.CollocamentoMirato.WARNING_NUMDISABEFFETT_MAGGIORE_NUMDISABCALC);
				}
			}

			this.setSectionQueryUpdate("QUERY_UPDATE_RICH_ESON");
			ret = doUpdate(request, response);

			if (!ret) {
				throw new Exception("impossibile aggiornare CM_RICH_ESONERO in transazione");
			}

			transExec.commitTransaction();
			this.setMessageIdSuccess(MessageCodes.General.UPDATE_SUCCESS);
			reportOperation.reportSuccess(idSuccess);
		} catch (Exception e) {
			transExec.rollBackTransaction();
			this.setMessageIdFail(MessageCodes.General.UPDATE_FAIL);
			reportOperation.reportFailure(e, "service()", "aggiornamento in transazione");
		} finally {
		}
	}

	private BigDecimal calcolaNumDisabili(double numBaseComputo, double numPercEsonero) {
		double numDis = 0;

		if (numBaseComputo < 15) {
			numDis = 0;
		}
		if (numBaseComputo >= 15 && numBaseComputo <= 35) {
			numDis = 1;
		}
		if (numBaseComputo >= 36 && numBaseComputo <= 50) {
			numDis = 2;
		}
		if (numBaseComputo > 50) {
			numDis = (numBaseComputo / 100) * 7;
		}

		numDis = (numDis / 100) * numPercEsonero;

		int numDisInt = (new BigDecimal(numDis)).intValue();

		if ((numDis - numDisInt) > 0.5) {
			numDis = numDisInt + 1;
		} else {
			numDis = numDisInt;
		}

		return new BigDecimal(numDis);
	}

	private String getAttributeAsString(SourceBean request, String param) {
		return SourceBeanUtils.getAttrStrNotNull(request, param);
	}

	private BigDecimal getAttributeAsBigDecimal(SourceBean request, String param) {
		String tmp = SourceBeanUtils.getAttrStrNotNull(request, param);
		if (!tmp.equals("")) {
			return new BigDecimal(tmp);
		}
		return null;
	}

}
