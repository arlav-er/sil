/*
 * Creato il 5-giu-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.collocamentoMirato;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.amministrazione.impatti.DBLoad;

/**
 * @author riccardi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class CalcolaMesiAnzLavL68 extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) {
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		int msgCode = MessageCodes.General.OPERATION_FAIL;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		String cdnLavoratoreDecrypt = (String) request.getAttribute("CDNLAVORATORE");
		String datAnz68 = (String) request.getAttribute("DATANZIANITA68");
		String datAnzPregressaOrdinaria = StringUtils.getAttributeStrNotNull(request, "DATANZORDINARIAPREGRESSA");
		int mesiAnz68;

		try {
			String dataOdierna = DateUtils.getNow();
			mesiAnz68 = DBLoad.calcolaAnzianitaCM(cdnLavoratoreDecrypt, datAnz68, "", 0, dataOdierna,
					datAnzPregressaOrdinaria);

			if (mesiAnz68 < 0) {
				reportErrorCalcoloAnzianitaCM(mesiAnz68, reportOperation);
			} else {
				String strMesiAnz68 = Integer.toString(mesiAnz68);
				response.setAttribute("mesiAnz68", strMesiAnz68);
			}
		} catch (Exception e) {
			reportOperation.reportFailure(msgCode, e, "services()",
					"Non è possibile calcolare l'anzianità: Errore generico");
		}
	}

	public void reportErrorCalcoloAnzianitaCM(int mesiAnz68, ReportOperationResult reportOperation) {
		int msgCode;
		if (mesiAnz68 == -1) {
			msgCode = MessageCodes.CollocamentoMirato.ERROR_LAVORATORE_NON_DISOCCUPATO_INOCCUPATO;
			reportOperation.reportFailure(msgCode, null, "services()", "Il lavoratore non è disoccupato o inoccupato");
		} else if (mesiAnz68 == -2) {
			msgCode = MessageCodes.CollocamentoMirato.ERROR_DATANZIANITA_COLL_ORDINARIO_NON_PRESENTE;
			reportOperation.reportFailure(msgCode, null, "services()",
					"La data anzianità del collocamento ordinario non è presente: è necessario ricalcolare gli impatti del lavoratore");
		} else if (mesiAnz68 == -3) {
			msgCode = MessageCodes.CollocamentoMirato.ERROR_DAT_ANZ_CM_MINORE_DAT_ANZ_COLL_ORDINARIO;
			reportOperation.reportFailure(msgCode, null, "services()",
					"La data anzianità del collocamento mirato è minore della data anzianità del collocamento ordinario");
		} else if (mesiAnz68 == -4) {
			msgCode = MessageCodes.CollocamentoMirato.ERROR_DAT_SOSP_CM_MINORE_DAT_ANZ_CM;
			reportOperation.reportFailure(msgCode, null, "services()",
					"La data sospensione del collocamento mirato è minore della data anzianità del collocamento mirato");
		}
	}
}
