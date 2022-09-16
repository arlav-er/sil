package it.eng.sil.module.scadenziario;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

public class ScadenzeLavoratore extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws SourceBeanException {

		String nomeModulo = (String) request.getAttribute("AF_MODULE_NAME");

		// Per il modulo M_ListaVerAppStatoOccVariato sono richiesti due
		// parametri il cdnlavoratore ed il codcpi
		// il primo è sempre presente, mentre il codcpi puo non essere
		// valorizzato
		// quindi controllo se l'utente collegato è un cpi, in tal caso prendo
		// il codcpi dell'utente
		// dalla sessione, altrimenti non eseguo la query
		if (nomeModulo.equalsIgnoreCase("M_ListaVerAppStatoOccVariato")) {
			SessionContainer sessione = RequestContainer.getRequestContainer().getSessionContainer();
			User user = (User) sessione.getAttribute(User.USERID);

			// Ricavo il tipo gruppo per verificare che sia un centro per
			// l'impiego
			int tipoGruppo = user.getCdnTipoGruppo();

			if (tipoGruppo == 1) {

				// Ricavo il codcpi dell'utente collegato
				String cpi = user.getCodRif();

				if (!request.containsAttribute("CODCPI"))
					request.setAttribute("CODCPI", cpi);

				doSelect(request, response);
			}

		} else {
			// Per gli altri moduli è richiesto , come parametro, il solo
			// cdnLavoratore
			doSelect(request, response);
		}
	}
}
