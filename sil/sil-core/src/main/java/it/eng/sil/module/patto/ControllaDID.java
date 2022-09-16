package it.eng.sil.module.patto;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dispatching.module.AbstractModule;

import it.eng.afExt.utils.DateUtils;
import it.eng.sil.security.User;

public class ControllaDID extends AbstractModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ControllaDID.class.getName());
	private String className = this.getClass().getName();
	private ConfrontaDatiDid confronta = null;

	public void service(SourceBean request, SourceBean response) {
		User usr = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);
		SourceBean attuale = null;
		SourceBean memorizzata = null;
		try {
			BigDecimal cdnLavoratore = new BigDecimal(request.getAttribute("CDNLAVORATORE").toString());
			confronta = new ConfrontaDatiDid(cdnLavoratore);
			attuale = confronta.getDidAttuale();
			memorizzata = confronta.getDidMemorizzata(null);

			if (!confronta.confrontaDati(memorizzata, attuale)) { // Se campi
																	// diversi
				Object params[] = new Object[5];
				params[0] = DateUtils.getNow();
				params[1] = "CC";
				params[2] = new Integer(usr.getCodut());// cdnUtMod
				params[3] = memorizzata.getAttribute("ROW.NUMKLODICHDISP");
				params[4] = memorizzata.getAttribute("ROW.PRGDICHDISPONIBILITA");

				if (!confronta.chiudiDid(params)) {
					throw new Exception();
				}

				if (request.containsAttribute("PRGDICHDISPONIBILITA")
						&& !request.getAttribute("PRGDICHDISPONIBILITA").equals("")) {
					response.setAttribute("NonEseguireGet", "true");
					// TracerSingleton.log("ControllaDid:
					// ",TracerSingleton.DEBUG,response.toString());
				}
				response.setAttribute("CHIUSO", "true");

			}

		} catch (Exception ex) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, className + "::update: ST_CHIUDI_DID", ex);

		} // catch
	}// end service
}