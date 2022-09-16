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
import it.eng.sil.coop.messages.jmsmessages.AccorpaLavoratoriIRMessage;
import it.eng.sil.coop.queues.OutQ;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.eng.sil.util.InfoProvinciaSingleton;

public class AccorpaLavoratoriIR extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AccorpaLavoratoriIR.class.getName());

	public void service(SourceBean request, SourceBean response) {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		try {
			String codiceFiscale1 = (String) request.getAttribute("codicefiscale1");
			String codiceFiscale2 = (String) request.getAttribute("codicefiscale2"); // sull'indice
																						// verra'
																						// cancellato

			String cdnGruppo = null, cdnProfilo = null, strMittente = null, poloMittente = null, cdnUtente = null;
			String codMonoTipoCpi = null, codiceFiscale = null, codiceFiscaleNuovo = null, cognome = null, nome = null,
					dataNascita = null, codComNascita = null;
			String codCpiTit = null, codProvinciaOp = null;

			// codiceFiscaleNuovo sara' inserito se inesistente: equivale al
			// lavoratore a cui si accorpa
			codiceFiscaleNuovo = codiceFiscale1;
			// codiceFiscale sara' cancellato: equivale al codiceFiscale2
			codiceFiscale = codiceFiscale2;
			// prelevo dalla session l'utente
			RequestContainer requestContainer = getRequestContainer();
			SessionContainer sessionContainer = requestContainer.getSessionContainer();
			User user = (User) sessionContainer.getAttribute(User.USERID);

			// parametri di inserimento
			String cdnLavInCuiAccorpare = (String) request.getAttribute("cdnLavInCuiAccorpare");
			cdnGruppo = String.valueOf(user.getCdnGruppo());
			cdnProfilo = String.valueOf(user.getCdnProfilo());
			strMittente = user.getNome() + " " + user.getCognome();
			cdnUtente = InfoProvinciaSingleton.getInstance().getCodice();
			poloMittente = InfoProvinciaSingleton.getInstance().getCodice();
			codProvinciaOp = poloMittente;
			StoriaInfLavoratore infLav = new StoriaInfLavoratore(cdnLavInCuiAccorpare);
			String dataInizio = infLav.getDatInizio();
			// recupero tutto il modulo M_GETCPILAVORATORE (leggi commento nello
			// xml della page)
			SourceBean cpiLavoratore = (SourceBean) request.getAttribute("M_GETCPILAVORATORE.ROWS.ROW");
			codCpiTit = (String) cpiLavoratore.getAttribute("CODCPITIT");
			codMonoTipoCpi = (String) cpiLavoratore.getAttribute("CODMONOTIPOCPI");
			SourceBean anagLavoratore = (SourceBean) request.getAttribute("M_GETLAVORATOREANAG.ROWS.ROW");
			codComNascita = (String) anagLavoratore.getAttribute("codComNas");
			cognome = (String) anagLavoratore.getAttribute("strCognome");
			nome = (String) anagLavoratore.getAttribute("strNome");
			dataNascita = (String) anagLavoratore.getAttribute("datNasc");
			// L'indice gestisce solo due codici mono tipo: 'C' e 'T'. Invece
			// per il polo si usa anche 'E'
			codMonoTipoCpi = codMonoTipoCpi.equals("C") ? "C" : "T";

			TestataMessageTO testata = new TestataMessageTO();
			testata.setPoloMittente(poloMittente);
			testata.setCdnUtente(cdnUtente);
			testata.setCdnGruppo(cdnGruppo);
			testata.setCdnProfilo(cdnProfilo);
			testata.setStrMittente(strMittente);

			AccorpaLavoratoriIRMessage messaggio = new AccorpaLavoratoriIRMessage();

			messaggio.setCodCpi(codCpiTit);
			messaggio.setCodComNascita(codComNascita);
			messaggio.setCodiceFiscaleNuovo(codiceFiscaleNuovo);
			messaggio.setCodMonoTipoCpi(codMonoTipoCpi);
			messaggio.setCognome(cognome);
			messaggio.setDataNascita(dataNascita);
			messaggio.setNome(nome);
			messaggio.setCodiceFiscale(codiceFiscale);
			messaggio.setCodProvinciaOp(codProvinciaOp);
			messaggio.setDataInizio(dataInizio);
			messaggio.setTestata(testata);

			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();

			messaggio.setDataSourceJndi(dataSourceJndiName);

			OutQ outQ = new OutQ();

			messaggio.send(outQ);
			response.setAttribute("retCode", "0");
			reportOperation.reportSuccess(MessageCodes.Coop.INSERIMENTO_IR_SUCCESS);

		} catch (Exception exc) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "AccorpaLavoratoriIR::service()", exc);

			reportOperation.reportFailure(MessageCodes.Coop.ERR_CONNESSIONE_IR);
		}
	}

}
