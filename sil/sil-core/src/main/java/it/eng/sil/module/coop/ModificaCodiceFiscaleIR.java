package it.eng.sil.module.coop;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

/**
 * 
 * @author savino
 */

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.bean.StoriaInfLavoratore;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.messages.TestataMessageTO;
import it.eng.sil.coop.messages.jmsmessages.ModificaCodiceFiscaleIRMessage;
import it.eng.sil.coop.queues.OutQ;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.eng.sil.util.InfoProvinciaSingleton;

public class ModificaCodiceFiscaleIR extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ModificaCodiceFiscaleIR.class.getName());

	public void service(SourceBean request, SourceBean response) throws SourceBeanException {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		try {
			// 1. bisogna recuperare le informazioni del lavoratore:
			String codMonoTipoCpi = null, codiceFiscale = null, codiceFiscaleNuovo = null, cognome = null, nome = null,
					dataNascita = null, codComNascita = null, dataInizio = null;
			String codCpiTit = null, codProvinciaOp = null;
			String cdnGruppo = null, cdnProfilo = null, strMittente = null, poloMittente = null, cdnUtente = null;
			codiceFiscale = (String) request.getAttribute("strCodiceFiscalePrecedente");
			codiceFiscaleNuovo = (String) request.getAttribute("strCodiceFiscale");
			if (codiceFiscale.equals(codiceFiscaleNuovo)) {
				response.setAttribute("SERVIZIO_CHIAMATO", "FALSE"); // necessario
																		// per
																		// la
																		// chiamata
																		// di un
																		// modulo
																		// successivo
				return;
			}
			// solo se la modifica riguarda i codici fiscali proseguiamo......

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
			codProvinciaOp = poloMittente;
			Object cdnLavoratore = (String) request.getAttribute("cdnLavoratore");
			StoriaInfLavoratore infLav = new StoriaInfLavoratore(cdnLavoratore);
			dataInizio = infLav.getDatInizio();
			// recupero tutto il modulo M_GETCPILAVORATORE (leggi commento nello
			// xml della page)
			SourceBean cpiLavoratore = (SourceBean) request.getAttribute("M_GETCPILAVORATORE.ROWS.ROW");
			// codCpiComp = (String) cpiLavoratore.getAttribute("CPICOMP");
			codCpiTit = (String) cpiLavoratore.getAttribute("CODCPITIT");
			codMonoTipoCpi = (String) cpiLavoratore.getAttribute("CODMONOTIPOCPI");
			codComNascita = (String) request.getAttribute("codComNas");
			cognome = (String) request.getAttribute("strCognome");
			nome = (String) request.getAttribute("strNome");
			dataNascita = (String) request.getAttribute("datNasc");
			// L'indice gestisce solo due codici mono tipo: 'C' e 'T'. Invece
			// per il polo si usa anche 'E'
			codMonoTipoCpi = codMonoTipoCpi.equals("C") ? "C" : "T";

			TestataMessageTO testata = new TestataMessageTO();
			testata.setPoloMittente(poloMittente);
			testata.setCdnUtente(cdnUtente);
			testata.setCdnGruppo(cdnGruppo);
			testata.setCdnProfilo(cdnProfilo);
			testata.setStrMittente(strMittente);
			// nuovo costruttore

			ModificaCodiceFiscaleIRMessage messaggio = new ModificaCodiceFiscaleIRMessage();

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
			response.setAttribute("SERVIZIO_CHIAMATO", "TRUE");// necessario
																// per la
																// chiamata di
																// un modulo
																// successivo
		} catch (Exception exc) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "ModificaCodiceFiscaleIR::service()", exc);

			reportOperation.reportFailure(MessageCodes.Coop.ERR_MODIFICA_CPI_IR);
		}

	}

}
