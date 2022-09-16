package it.eng.sil.module.anag;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.sil.coop.webservices.myportal.servizicittadino.ServiziCittadino;
import it.eng.sil.coop.webservices.myportal.servizicittadino.ServiziCittadinoException;
import it.eng.sil.module.AbstractSimpleModule;

public class ReinoltraMailAccount extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ReinoltraMailAccount.class.getName());

	private static final long serialVersionUID = -4743023184567517367L;

	public void service(SourceBean request, SourceBean response) {

		String risultato = "";
		String email = (String) request.getAttribute("email");
		String idPfPrincipal = (String) request.getAttribute("idPfPrincipal");
		String destinatarioCC = (String) request.getAttribute("destinatarioCC");

		if (email == null) {

			_logger.error("Reinvio mail fallito l'email di registrazione sul portale è obbligatorio!");
			risultato = "Reinvio mail fallito l'email di registrazione sul portale è obbligatorio!";

		} else if (idPfPrincipal == null) {

			_logger.error("Reinvio mail fallito idPfPrincipal obbligatorio!");
			risultato = "Reinvio mail fallito idPfPrincipal obbligatorio!";

		} else {

			try {

				if (ServiziCittadino.reinvioMail(idPfPrincipal, destinatarioCC)) {
					risultato = "Le credenziali di accesso e il link di attivazione sono stati inviati agli indirizzi indicati.";
				} else {
					risultato = "Reinvio mail fallito";
				}

			} catch (ServiziCittadinoException e) {

				risultato = e.getMessage();
				_logger.error(e.getMessage(), e);

			}

			try {

				response.setAttribute("RISULTATO", risultato);

			} catch (SourceBeanException e) {
				_logger.error("Errore generico", e);
			}

		}

	}

}
