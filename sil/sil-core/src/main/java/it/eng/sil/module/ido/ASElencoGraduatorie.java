package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dispatching.module.impl.ListModule;

public class ASElencoGraduatorie extends ListModule {

	public void service(SourceBean request, SourceBean response) {

		RequestContainer requestContainer = getRequestContainer();
		SessionContainer session = requestContainer.getSessionContainer();

		String numRichiesta = (String) request.getAttribute("PRGRICHIESTAAZ");
		// possibili chiavi che identificano i nominativi selezionati
		String key1 = numRichiesta + "_5";
		String key2 = numRichiesta + "_6";
		String key3 = numRichiesta + "_7";
		String key4 = numRichiesta + "_8";
		String key5 = numRichiesta + "_9";

		// elimino dalla sessione il SB dei candidati selezionati
		session.delAttribute(key1);
		session.delAttribute(key2);
		session.delAttribute(key3);
		session.delAttribute(key4);
		session.delAttribute(key5);

		super.service(request, response);

	}

}