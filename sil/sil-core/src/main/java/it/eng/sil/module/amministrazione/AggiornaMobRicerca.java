package it.eng.sil.module.amministrazione;

import java.math.BigDecimal;
import java.util.StringTokenizer;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.MultipleTransactionQueryExecutor;

public class AggiornaMobRicerca extends AbstractSimpleModule {

	public void service(SourceBean request, SourceBean response) throws Exception {
		MultipleTransactionQueryExecutor trans = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		String codStatoMob = null;
		String mobDaAggiornare = "";
		String[] parmsPerNumklo = new String[1];
		Object[] parmsPerUpdate = new Object[4];

		BigDecimal cdnUser = (BigDecimal) this.getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
		codStatoMob = (String) request.getAttribute("statoric");

		Vector list = new Vector();

		try {
			mobDaAggiornare = StringUtils.getAttributeStrNotNull(request, "prgmobilita");
			StringTokenizer st = new StringTokenizer(mobDaAggiornare, ",");
			while (st.hasMoreTokens()) {
				list.add(st.nextToken());
			}
		} catch (Exception e) {
			throw new Exception("Errore nei parametri");
		}

		try {
			trans = new MultipleTransactionQueryExecutor((String) this.getConfig().getAttribute("pool"));
			trans.initTransaction();
			this.enableTransactions(trans);

			for (int i = 0; i < list.size(); i++) {

				String prgmob = (String) list.elementAt(i);
				parmsPerNumklo[0] = prgmob;

				SourceBean numKlo = (SourceBean) trans.executeQuery("SELECT_NUMKLO_MOB", parmsPerNumklo, "SELECT");
				BigDecimal numkloiscr = (BigDecimal) numKlo.getAttribute("ROW.NUMKLOMOBISCR");

				parmsPerUpdate[0] = codStatoMob;
				parmsPerUpdate[1] = cdnUser;
				parmsPerUpdate[2] = numkloiscr;
				parmsPerUpdate[3] = prgmob;

				Boolean result = (Boolean) trans.executeQuery("UPDATE_ONLY_STATO_RICH", parmsPerUpdate, "UPDATE");

				if (!result.booleanValue()) {
					if (trans != null)
						trans.rollBackTransaction();
					throw new Exception("Errore nell'aggiornamento dello stato");
				}

			}

			trans.commitTransaction();
			reportOperation.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);

		} catch (Exception e) {
			if (trans != null)
				trans.rollBackTransaction();
			reportOperation.reportFailure(MessageCodes.General.UPDATE_FAIL);
		}

	}

}