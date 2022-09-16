package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.sil.coop.webservices.myportal.servizicittadino.ServiziCittadino;
import it.eng.sil.coop.webservices.myportal.servizicittadino.ServiziCittadinoException;
import it.eng.sil.module.AbstractSimpleModule;

public class InsertAccountPortale extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InsertAccountPortale.class.getName());
	private String className = this.getClass().getName();

	public void service(SourceBean request, SourceBean response) {

		try {
			response.setAttribute("TITOLO", "Risultato Inserimento Account sul Portale");

			ServiziCittadino.putAccountCittadino(null, (String) request.getAttribute("USERNAME_PORT"),
					(String) request.getAttribute("COGNOME_SIL"), (String) request.getAttribute("NOME_SIL"),
					(String) request.getAttribute("MAIL_SIL"), (String) request.getAttribute("COD_COMUNENAS_SIL"),
					(String) request.getAttribute("COD_COMUNDOM_SIL"),
					(String) request.getAttribute("INDIRIZZODOM_SIL"),
					(String) request.getAttribute("CODICEFISCALE_SIL"),
					(String) request.getAttribute("DATANASCITA_SIL"),
					(String) request.getAttribute("CODCITTADINANZA_SIL"),
					(String) request.getAttribute("CODICEPROVINCIASIL"), (String) request.getAttribute("DOCUMENTO_SIL"),
					(String) request.getAttribute("NUMDOC_SIL"), (String) request.getAttribute("DATASCADDOC_SIL"));

			response.setAttribute("RISULTATO",
					"Dati aggiornati correttamente.<br/>Le credenziali di accesso e il link di attivazione sono stati inviati all'indirizzo "
							+ request.getAttribute("MAIL_SIL"));

		} catch (ServiziCittadinoException e) {
			try {
				_logger.error(e.getMessage(), e);
				response.setAttribute("RISULTATO", e.getMessage());
			} catch (SourceBeanException e1) {
				_logger.error(e.getMessage(), e);
			}

		} catch (Exception e) {
			try {
				_logger.error(e.getMessage(), e);
				response.setAttribute("RISULTATO", "Errore generico.");
			} catch (SourceBeanException e1) {
				_logger.error(e.getMessage(), e);
			}
		}
	}
}