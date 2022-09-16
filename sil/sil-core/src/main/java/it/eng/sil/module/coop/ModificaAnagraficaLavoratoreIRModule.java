package it.eng.sil.module.coop;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

/**
 * 
 * @author savino
 */

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.bean.StoriaInfLavoratore;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.messages.TestataMessageTO;
import it.eng.sil.coop.messages.jmsmessages.ModificaAnagraficaLavoratoreIRMessage;
import it.eng.sil.coop.queues.OutQ;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.eng.sil.util.InfoProvinciaSingleton;

public class ModificaAnagraficaLavoratoreIRModule extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(ModificaAnagraficaLavoratoreIRModule.class.getName());

	public void service(SourceBean request, SourceBean response) {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		try {
			String codiceFiscale = null, cognome = null, nome = null, dataNascita = null, codComNascita = null;
			String codProvinciaPolo = null, codCpi = null, codMonoTipoCpi = null, dataInizio = null;
			String cognomeNuovo = null, nomeNuovo = null, dataNascitaNuovo = null, codComNascitaNuovo = null;
			String cdnGruppo = null, cdnProfilo = null, strMittente = null, poloMittente = null, cdnUtente = null;
			String cdnLavoratore;
			codiceFiscale = (String) request.getAttribute("strCodiceFiscale!!");
			cognomeNuovo = (String) request.getAttribute("strCognome");
			nomeNuovo = (String) request.getAttribute("strNome");
			dataNascitaNuovo = (String) request.getAttribute("datNasc");
			codComNascitaNuovo = (String) request.getAttribute("codComNas");
			cdnLavoratore = (String) request.getAttribute("cdnLavoratore");
			StoriaInfLavoratore infLav = new StoriaInfLavoratore(cdnLavoratore);
			dataInizio = infLav.getDatInizio();
			codMonoTipoCpi = infLav.getCodMonoTipoCpi();
			codCpi = infLav.getCodCpiTit();

			// recupero tutto il modulo M_GetLavoratoreAnag (leggi commento
			// nello xml della page)
			SourceBean lavoratore = (SourceBean) request.getAttribute("M_GetLavoratoreAnag.ROWS.ROW");
			codiceFiscale = (String) lavoratore.getAttribute("strCodiceFiscale");
			codComNascita = (String) lavoratore.getAttribute("codComNas");
			cognome = (String) lavoratore.getAttribute("strCognome");
			nome = (String) lavoratore.getAttribute("strNome");
			dataNascita = (String) lavoratore.getAttribute("datNasc");
			// solo se uno di questi valori e' cambiato lancio il servizio
			if (cognome.equalsIgnoreCase(cognomeNuovo) && nome.equalsIgnoreCase(nomeNuovo)
					&& dataNascita.equalsIgnoreCase(dataNascitaNuovo)
					&& codComNascita.equalsIgnoreCase(codComNascitaNuovo)) {
				// i dati critici non sono cambiati. Sull'indice non c'e' nulla
				// da aggiornare
				return;
			}
			// prelevo dalla session l'utente
			RequestContainer requestContainer = getRequestContainer();
			SessionContainer sessionContainer = requestContainer.getSessionContainer();
			User user = (User) sessionContainer.getAttribute(User.USERID);

			// parametri di inserimento

			cdnGruppo = String.valueOf(user.getCdnGruppo());
			cdnProfilo = String.valueOf(user.getCdnProfilo());
			strMittente = user.getNome() + " " + user.getCognome();
			cdnUtente = InfoProvinciaSingleton.getInstance().getCodice();
			poloMittente = InfoProvinciaSingleton.getInstance().getCodice();
			// codCpiOperatore = user.getCodRif();
			codProvinciaPolo = poloMittente;

			TestataMessageTO testata = new TestataMessageTO();
			testata.setPoloMittente(poloMittente);
			testata.setCdnUtente(cdnUtente);
			testata.setCdnGruppo(cdnGruppo);
			testata.setCdnProfilo(cdnProfilo);
			testata.setStrMittente(strMittente);

			ModificaAnagraficaLavoratoreIRMessage msg = new ModificaAnagraficaLavoratoreIRMessage();
			msg.setTestata(testata);

			msg.setCodiceFiscale(codiceFiscale);
			msg.setCognome(cognomeNuovo);
			msg.setNome(nomeNuovo);
			msg.setDataNascita(dataNascitaNuovo);
			msg.setCodComNascita(codComNascitaNuovo);
			msg.setCodCpi(codCpi);
			msg.setCodProvinciaPolo(codProvinciaPolo);
			msg.setDataInizio(dataInizio);
			msg.setCodMonoTipoCpi(codMonoTipoCpi);

			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();

			msg.setDataSourceJndi(dataSourceJndiName);

			OutQ outQ = new OutQ();

			msg.send(outQ);
			response.setAttribute("retCode", "0");
			reportOperation.reportSuccess(MessageCodes.Coop.INSERIMENTO_IR_SUCCESS);

		} catch (Exception exc) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "ModificaAnagraficaLavoratoreIRModule::service()", exc);

			reportOperation.reportFailure(MessageCodes.Coop.ERR_AGGIORNA_ANAGRAFICA_LAV_IR);
		}

	}

}
