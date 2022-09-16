package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageAppender;
import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.Values;
import it.eng.sil.coop.webservices.myportal.servizicittadino.ServiziCittadino;
import it.eng.sil.coop.webservices.myportal.servizicittadino.ServiziCittadinoException;
import it.eng.sil.module.AbstractSimpleModule;

public class GetLavoratoreAccountDettaglio extends AbstractSimpleModule {

	private static final long serialVersionUID = -2809140125834976390L;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(GetLavoratoreAccountDettaglio.class.getName());

	public void service(SourceBean request, SourceBean response) {

		boolean isErrore = false;
		SourceBean dettaglioSourceBean = null;
		String idPfPrincipal = (String) request.getAttribute("idPfPrincipal");

		// recupera dettaglio

		try {

			dettaglioSourceBean = ServiziCittadino.getDettaglioCittadino(idPfPrincipal);

		} catch (ServiziCittadinoException e) {

			_logger.error(e.getMessage(), e);
			MessageAppender.appendMessage(response, e.getMessage(), null);
			isErrore = true;

		} catch (Exception e) {

			_logger.error(e.getMessage(), e);
			MessageAppender.appendMessage(response, MessageCodes.General.OPERATION_FAIL, null);
			isErrore = true;

		}

		// lettura dettaglio

		if (dettaglioSourceBean != null && !isErrore) {

			String strComune = getDenominazioneComune((String) dettaglioSourceBean.getAttribute("COMUNENASCITA"));
			String strComuneDomicilio = getDenominazioneComune(
					(String) dettaglioSourceBean.getAttribute("COMUNEDOMICILIO"));
			String strCittadinanza = getDenominazioneCittadinanza(
					(String) dettaglioSourceBean.getAttribute("cittadinanza"));

			try {

				if (strComune != null)
					dettaglioSourceBean.setAttribute("descComunmeNascita", strComune);
				if (strComuneDomicilio != null)
					dettaglioSourceBean.setAttribute("descComunmeDomicilio", strComuneDomicilio);
				if (strCittadinanza != null)
					dettaglioSourceBean.setAttribute("descCittadinanza", strCittadinanza);

				response.setAttribute(dettaglioSourceBean);

			} catch (SourceBeanException e) {

				_logger.error(e.getMessage(), e);
				MessageAppender.appendMessage(response, MessageCodes.General.OPERATION_FAIL, null);
				isErrore = true;

			}

		}

		// gestione eventuale errore

		if (isErrore) {

			try {
				response.setAttribute("ERRORE", "true");
			} catch (SourceBeanException e1) {
				_logger.error(e1.getMessage(), e1);
			}

		}

	}

	private String getDenominazioneComune(String codComune) {
		String decodifica = null;
		if (codComune != null) {
			SourceBean row = null;
			String[] parameter = new String[1];
			parameter[0] = codComune;
			row = (SourceBean) com.engiweb.framework.util.QueryExecutor
					.executeQuery("GET_DENOMINAZIONE_COMUNE_BY_CODICE", parameter, "SELECT", Values.DB_SIL_DATI);

			if (row != null) {
				decodifica = row.getAttribute("ROW.STRDENOMINAZIONE") != null
						? row.getAttribute("ROW.STRDENOMINAZIONE").toString()
						: null;
			}
		}
		return decodifica;
	}

	private String getDenominazioneCittadinanza(String codCittadinanza) {
		String decodifica = null;
		if (codCittadinanza != null) {
			SourceBean row = null;
			String[] parameter = new String[1];
			parameter[0] = codCittadinanza;
			row = (SourceBean) com.engiweb.framework.util.QueryExecutor
					.executeQuery("GET_DENOMINAZIONE_CITTADINANZA_BY_CODICE", parameter, "SELECT", Values.DB_SIL_DATI);

			if (row != null) {
				decodifica = row.getAttribute("ROW.strdescrizione") != null
						? row.getAttribute("ROW.strdescrizione").toString()
						: null;
			}
		}
		return decodifica;
	}
}