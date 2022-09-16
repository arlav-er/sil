package it.eng.sil.module.presel;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.PattoManager;

public class InsertMansione extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;
		String InserimentoMansione = "";
		boolean flgInserimentoMansione = false;
		String strOperazioneColPatto = "";

		InserimentoMansione = (String) request.getAttribute("InserimentoMansione");

		if (StringUtils.isFilled(InserimentoMansione)) {
			if (InserimentoMansione.equals("1") && PattoManager.withPatto(request)) {
				strOperazioneColPatto = (String) request.getAttribute("operazioneColPatto");
				request.updAttribute("operazioneColPatto", "0");
			}
		}
		// InsertMansione insMansione = new InsertMansione();
		// insMansione.setParameterForInsertMansione(request, response);
		setParameterForInsertMansione(request, response);
		// this.setSectionQueryUpdate();
		if (!strOperazioneColPatto.equals("")) {
			request.updAttribute("operazioneColPatto", strOperazioneColPatto);
		}
	}

	public void service2(SourceBean request, SourceBean response) throws Exception {
		boolean ret = false;

		if (PattoManager.withPatto(request)) {
			ReportOperationResult reportOperation = new ReportOperationResult(this, response);
			TransactionQueryExecutor transExec = null;
			int idSuccess = this.disableMessageIdSuccess();
			int idFail = this.disableMessageIdFail();

			try {
				transExec = new TransactionQueryExecutor(getPool());

				PattoManager patto = new PattoManager(this, transExec);
				transExec.initTransaction();

				BigDecimal prgMansione = getPrgMansione(request, response);
				setParameterForMansione(prgMansione, request);

				// this.setSectionQueryInsert("INSERT_IND_T");
				this.setSectionQuerySelect("QUERY_SELECT");
				this.setSectionQueryInsert("QUERY_INSERT");
				ret = this.doInsertNoDuplicate(request, response);

				if (ret) {
					if (request.getAttribute("PRG_TAB") != null) {
						request.delAttribute("PRG_TAB");
					}

					request.setAttribute("PRG_TAB", prgMansione);
					ret = patto.execute(request, response);
				} else {
					throw new Exception("");
				}

				//
				if (ret) {
					transExec.commitTransaction();
					this.setMessageIdSuccess(idSuccess);
					this.setMessageIdFail(idFail);
					reportOperation.reportSuccess(idSuccess);
				} else {
					throw new Exception();
				}
			} catch (Exception e) {
				// this.setMessageIdFail(MessageCodes.General.INSERT_FAIL);
				reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL);

				if (transExec != null) {
					transExec.rollBackTransaction();
				}
			}
		} else {
			int idSuccess = this.disableMessageIdSuccess();
			BigDecimal prgMansione = getPrgMansione(request, response);

			if (prgMansione != null) {
				setParameterForMansione(prgMansione, request);
				this.setMessageIdSuccess(idSuccess);
				this.setSectionQuerySelect("QUERY_SELECT");
				doInsertNoDuplicate(request, response);
			}
		}

	}

	private BigDecimal getPrgMansione(SourceBean request, SourceBean response) throws Exception {
		this.setSectionQuerySelect("QUERY_SEQUENCE");

		SourceBean beanPrgMansione = (SourceBean) doSelect(request, response);

		return (BigDecimal) beanPrgMansione.getAttribute("ROW.PRGMANSIONE");
	}

	private void setParameterForMansione(BigDecimal prgMansione, SourceBean request) throws Exception {
		if (request.getAttribute("PRGMANSIONE") != null) {
			request.delAttribute("PRGMANSIONE");
		}

		request.setAttribute("PRGMANSIONE", prgMansione);
	}

	private void setParameterForInsertMansione(SourceBean request, SourceBean response) throws Exception {

		String StringSize = ((String) request.getAttribute("SIZE"));

		if ((StringSize != null && !StringSize.equals(""))) {
			int SIZE = new Integer(StringSize).intValue();
			for (int i = 0; i < SIZE; i++) {
				if (request.getAttribute("CODMANSIONE_" + i) != null) {
					request.delAttribute("CODMANSIONE");
					request.setAttribute("CODMANSIONE", request.getAttribute("CODMANSIONE_" + i));
					// request.getAttribute("NUMANNOINIZIO" + i);
					request.delAttribute("NUMANNOINIZIO");
					request.setAttribute("NUMANNOINIZIO", request.getAttribute("NUMANNOINIZIO_" + i));
					// request.getAttribute("NUMANNOFINE" + i);
					request.delAttribute("NUMANNOFINE");
					request.setAttribute("NUMANNOFINE", request.getAttribute("NUMANNOFINE_" + i));
					// request.getAttribute("FLGDISPONIBILE" + i);
					request.delAttribute("FLGDISPONIBILE");
					request.setAttribute("FLGDISPONIBILE", request.getAttribute("FLGDISPONIBILE_" + i));

					request.delAttribute("STRNOTE");
					request.setAttribute("STRNOTE", request.getAttribute("STRNOTE_" + i));

					service2(request, response);
				}
			}

		} else {
			setSectionQuerySelect("QUERY_PATTO_APERTO");
			SourceBean row = doSelect(request, response, false);
			if (row.containsAttribute("row.codStatoatto")
					&& ((String) row.getAttribute("row.codStatoAtto")).equals("PR")) {
				String flgLavoro = (String) request.getAttribute("flgDisponibile");
				String flgFormazione = (String) request.getAttribute("flgDispFormazione");
				if ((flgLavoro != null && flgLavoro.equals("S")) || (flgLavoro != null && flgLavoro.equals("S"))) {
					// debbo creare l' associazione al patto protocollato
					request.updAttribute("operazioneColPatto", "-1");
					request.updAttribute("PRG_PATTO_LAVORATORE", row.getAttribute("row.prgPattoLavoratore"));
					request.delAttribute("COD_LST_TAB");
					request.setAttribute("COD_LST_TAB", "PR_MAN");
				}
			}
			service2(request, response);
		}

	}
}