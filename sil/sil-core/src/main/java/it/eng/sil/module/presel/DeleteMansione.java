package it.eng.sil.module.presel;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.patto.PattoManager;

public class DeleteMansione extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		TransactionQueryExecutor transExec = null;
		boolean ret = false;
		boolean withPatto = false;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();

		// if (PattoManager.withPatto(request)) {
		try {
			transExec = new TransactionQueryExecutor(getPool());
			PattoManager patto = new PattoManager(this, transExec);
			transExec.initTransaction();
			// controllo che il patto non sia protocollato. In questo caso la
			// cancellazione non deve essere
			// permessa
			withPatto = PattoManager.withPatto(request);
			if (withPatto) {
				this.setSectionQuerySelect("GET_PATTO");
				SourceBean row = doSelect(request, response, false);
				String codStatoAtto = (String) row.getAttribute("ROW.codStatoAtto");
				if (codStatoAtto != null && codStatoAtto.equals("PR"))
					throw new EMFUserError(EMFErrorSeverity.ERROR,
							MessageCodes.Mansioni.MANSIONE_ASS_PATTO_PROTOCOLLATO);
				ret = patto.execute(request, response);
				if (!ret)
					throw new Exception("");
			}
			//

			Vector sbVec = (Vector) getServiceResponse().getAttributeAsVector("M_GetLavoratoreEspLav.ROWS.ROW");
			Vector sbVec2 = (Vector) getServiceResponse().getAttributeAsVector("M_GETLAVORATORETIROCINI.ROWS.ROW");
			BigDecimal prgMansione = new BigDecimal((String) request.getAttribute("PRGMANSIONE"));
			request.setAttribute("PRGESPLAVORO", "");
			for (int i = 0; i < sbVec.size(); i++) {
				SourceBean sbRequest = (SourceBean) sbVec.elementAt(i);
				if (!sbRequest.getAttribute("PRGMANSIONE").equals(prgMansione))
					continue;
				String flgPatto297 = StringUtils.getAttributeStrNotNull(sbRequest, "FLGPATTO297");
				String codStatoPattoLav = StringUtils.getAttributeStrNotNull(sbRequest, "CODSTATOPATTOLAVORATORE");
				if (!flgPatto297.equals("") && flgPatto297.equalsIgnoreCase("S")) {
					/*
					 * this.setSectionQuerySelect("GET_PATTO"); SourceBean row = doSelect(request, response, false);
					 * String codStatoAtto = (String)row.getAttribute("ROW.codStatoAtto");
					 */
					if (!codStatoPattoLav.equals("") && codStatoPattoLav.equals("PR"))
						throw new EMFUserError(EMFErrorSeverity.ERROR,
								MessageCodes.Mansioni.MANSIONE_CON_ESPERIENZA_ASS_PATTO_PROTOCOLLATO);

					if (request.containsAttribute("PRG_LAV_PATTO_SCELTA"))
						request.updAttribute("PRG_LAV_PATTO_SCELTA", sbRequest.getAttribute("PRGLAVPATTOSCELTA"));
					else
						request.setAttribute("PRG_LAV_PATTO_SCELTA", sbRequest.getAttribute("PRGLAVPATTOSCELTA"));
					if (request.containsAttribute("operazioneColPatto"))
						request.updAttribute("operazioneColPatto", "1");
					else
						request.setAttribute("operazioneColPatto", "1");

					ret = patto.execute(request, response);
					if (!ret)
						throw new Exception("");
				}
				request.updAttribute("PRGESPLAVORO", sbRequest.getAttribute("PRGESPLAVORO"));
				this.setSectionQueryDelete("DELETE_ESPLAV");
				ret = this.doDelete(request, response);
				if (!ret)
					throw new Exception("");
			}
			// request.delAttribute("PRGESPLAVORO");

			// M_GETLAVORATORETIROCINI
			// request.setAttribute("PRGESPLAVORO","");
			for (int i = 0; i < sbVec2.size(); i++) {
				SourceBean sbRequest = (SourceBean) sbVec2.elementAt(i);
				if (!sbRequest.getAttribute("PRGMANSIONE").equals(prgMansione))
					continue;
				String flgPatto297 = StringUtils.getAttributeStrNotNull(sbRequest, "FLGPATTO297");
				String codStatoPattoLav = StringUtils.getAttributeStrNotNull(sbRequest, "CODSTATOPATTOLAVORATORE");
				if (!flgPatto297.equals("") && flgPatto297.equalsIgnoreCase("S")) {
					if (!codStatoPattoLav.equals("") && codStatoPattoLav.equals("PR"))
						throw new EMFUserError(EMFErrorSeverity.ERROR,
								MessageCodes.Mansioni.MANSIONE_CON_ESPERIENZA_ASS_PATTO_PROTOCOLLATO);

					if (request.containsAttribute("PRG_LAV_PATTO_SCELTA"))
						request.updAttribute("PRG_LAV_PATTO_SCELTA", sbRequest.getAttribute("PRGLAVPATTOSCELTA"));
					else
						request.setAttribute("PRG_LAV_PATTO_SCELTA", sbRequest.getAttribute("PRGLAVPATTOSCELTA"));
					if (request.containsAttribute("operazioneColPatto"))
						request.updAttribute("operazioneColPatto", "1");
					else
						request.setAttribute("operazioneColPatto", "1");

					ret = patto.execute(request, response);
					if (!ret)
						throw new Exception("");
				}
				request.updAttribute("PRGESPLAVORO", sbRequest.getAttribute("PRGESPLAVORO"));
				this.setSectionQueryDelete("DELETE_ESPLAV");
				ret = this.doDelete(request, response);
				if (!ret)
					throw new Exception("");
			}
			request.delAttribute("PRGESPLAVORO");
			/*
			 * this.setSectionQuerySelect("GET_ESP_LAVORO"); SourceBean sb = this.doSelect(request,response,false);
			 * Vector sbVec = (Vector) sb.getAttributeAsVector("ROW"); BigDecimal prgMansione = new
			 * BigDecimal((String)request.getAttribute("PRGMANSIONE")); request.setAttribute("PRGESPLAVORO","");
			 * 
			 * for (int i = 0; i < sbVec.size(); i++){ SourceBean sbRequest = (SourceBean) sbVec.elementAt(i); if
			 * (!sbRequest.getAttribute("PRGMANSIONE").equals(prgMansione)) continue;
			 * request.updAttribute("prgEspLavoro", sbRequest.getAttribute("prgEspLavoro"));
			 * this.setSectionQueryDelete("DELETE_ESPLAV"); ret = this.doDelete(request, response); if (!ret) throw new
			 * Exception(""); } request.delAttribute("PRGESPLAVORO");
			 */

			this.setSectionQueryDelete("DELETE_MANSIONE");
			ret = this.doDelete(request, response);

			/*
			 * if (withPatto) { if (ret){ ret = patto.execute(request, response); } else { throw new Exception(""); } }
			 */

			//
			if (ret) {
				transExec.commitTransaction();
				this.setMessageIdSuccess(idSuccess);
				this.setMessageIdFail(idFail);
				reportOperation.reportSuccess(idSuccess);
			} else {
				throw new Exception("");
			}
		} catch (EMFUserError ue) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			if (ue.getCode() == MessageCodes.Mansioni.MANSIONE_ASS_PATTO_PROTOCOLLATO
					|| ue.getCode() == MessageCodes.Mansioni.MANSIONE_CON_ESPERIENZA_ASS_PATTO_PROTOCOLLATO)
				reportOperation.reportFailure(ue.getCode());
			else
				reportOperation.reportFailure(MessageCodes.General.DELETE_FAIL);
		} catch (Exception e) {
			// this.setMessageIdFail(MessageCodes.General.DELETE_FAIL);
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			reportOperation.reportFailure(MessageCodes.General.DELETE_FAIL);
		}
	}
}
