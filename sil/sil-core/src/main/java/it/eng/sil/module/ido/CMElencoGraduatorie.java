package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dispatching.module.impl.ListModule;

public class CMElencoGraduatorie extends ListModule {

	public void service(SourceBean request, SourceBean response) {

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer session = requestContainer.getSessionContainer();

		String numRichiesta = (String) request.getAttribute("PRGRICHIESTAAZ");
		// possibili chiavi che identificano i nominativi selezionati
		String key1 = numRichiesta + "_10";
		String key2 = numRichiesta + "_11";
		String key3 = numRichiesta + "_12";

		// elimino dalla sessione il SB dei candidati selezionati
		session.delAttribute(key1);
		session.delAttribute(key2);
		session.delAttribute(key3);

		super.service(request, response);

	}

}