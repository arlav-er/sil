package it.eng.sil.module.anag;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.Utils;

public class CheckStatoAdesione extends AbstractSimpleModule {

	private static final long serialVersionUID = -1533726811526662058L;
	private String className = this.getClass().getName();
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CheckStatoAdesione.class.getName());

	public void service(SourceBean request, SourceBean response) throws Exception {
		String cdnLavoratore = "";
		String statoInput = "";
		String dataAdesioneMin = "";
		String dataAdesioneSil = "";
		String operazione = "";
		BigDecimal cdnLav = null;
		VerificaPoliticheAttive verificaPolAttive = null;

		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		try {
			cdnLavoratore = Utils.notNull(request.getAttribute("CDNLAVORATORE"));
			operazione = Utils.notNull(request.getAttribute("OPERAZIONE"));
			if (operazione.equals("SET_STATO") || operazione.equals("VERIFICA_STATO")) {
				statoInput = Utils.notNull(request.getAttribute("nuovoStato"));
			} else {
				if (operazione.equals("INVIA_SAP")) {
					statoInput = Utils.notNull(request.getAttribute("currStato"));
				}
			}

			dataAdesioneMin = Utils.notNull(request.getAttribute("datAdesioneGG"));
			dataAdesioneSil = Utils.notNull(request.getAttribute("datAdesioneSIL"));
			cdnLav = new BigDecimal(cdnLavoratore);

			verificaPolAttive = new VerificaPoliticheAttive(cdnLav, dataAdesioneMin, dataAdesioneSil, statoInput);

			int checkVerificaEsistenzaAdesione = verificaPolAttive.verificaEsistenzaAdesione();
			int checkPoliticheAttiveCollegateAdesione = verificaPolAttive.politicheAttiveCollegateAdesione();
			int checkCoerenzaPoliticheAttiveStatoAdesione = verificaPolAttive.coerenzaPoliticheAttiveStatoAdesione();

			SourceBean errors = new SourceBean("ERRORS");
			String descErrore = "";

			if (checkVerificaEsistenzaAdesione < 0) {
				descErrore = verificaPolAttive.getDescrizioneEsistenzaAdesione();
				SourceBean error = new SourceBean("ERROR");
				error.setAttribute("DESCRIZIONE", descErrore);
				errors.setAttribute(error);
			}

			if (checkPoliticheAttiveCollegateAdesione < 0) {
				descErrore = verificaPolAttive.getDescrizionePolAttiveCollAdesione();
				SourceBean error = new SourceBean("ERROR");
				error.setAttribute("DESCRIZIONE", descErrore);
				errors.setAttribute(error);
			}

			if (checkCoerenzaPoliticheAttiveStatoAdesione < 0) {
				descErrore = verificaPolAttive.getDescrizionePolAttiveStatoAdesione();
				SourceBean error = new SourceBean("ERROR");
				error.setAttribute("DESCRIZIONE", descErrore);
				errors.setAttribute(error);
			}

			if (checkVerificaEsistenzaAdesione < 0 || checkPoliticheAttiveCollegateAdesione < 0
					|| checkCoerenzaPoliticheAttiveStatoAdesione < 0) {
				response.setAttribute("ESITO_CHECK", "KO");
				if (operazione.equals("SET_STATO") || operazione.equals("INVIA_SAP")) {
					reportOperation.reportSuccess(MessageCodes.YG.CONFIRM_OPERAZIONE_CRUSCOTTO_ADESIONE_YG);
				}
				response.setAttribute(errors);
			} else {
				response.setAttribute("ESITO_CHECK", "OK");
			}

			if (operazione.equals("VERIFICA_STATO")) {
				reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
			}

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, className + "::service", e);
			response.setAttribute("ESITO_CHECK", "ERROR");
			if (operazione.equals("VERIFICA_STATO")) {
				reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
			}
		}
	}
}
