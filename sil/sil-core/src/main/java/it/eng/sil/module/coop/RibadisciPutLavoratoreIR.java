/*
 * Creato il 13-apr-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.coop;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.bean.StoriaInfLavoratore;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.messages.TestataMessageTO;
import it.eng.sil.coop.messages.jmsmessages.PutLavoratoreIRMessage;
import it.eng.sil.coop.queues.OutQ;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.eng.sil.util.InfoProvinciaSingleton;

/**
 * @author riccardi
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class RibadisciPutLavoratoreIR extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(RibadisciPutLavoratoreIR.class.getName());

	public void service(SourceBean request, SourceBean response) {
		boolean esito = true;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		String codiceFiscale = null;
		String nome = null;
		String cognome = null;
		String dataNascita = null;
		String comune = null;
		String dataInizio = null;
		String cdnGruppo = null;
		String cdnProfilo = null;
		String strMittente = null;
		String poloMittente = null;
		String cdnUtente = null;
		String codCpi = null;
		String codMonoTipoCpi = null;
		String cpiComp = null;

		try {
			String retCode = "";
			/*
			 * SourceBean row = doSelect(request, response, false); // SE si verifica un errore evito il null pointer e
			 * proseguo.... if (row==null) dataInizio=DateUtils.getNow(); else
			 * dataInizio=(String)row.getAttribute("row.datInizioStoriaAperta");
			 */

			// prelevo dalla session l'utente
			RequestContainer requestContainer = getRequestContainer();
			SessionContainer sessionContainer = requestContainer.getSessionContainer();
			User user = (User) sessionContainer.getAttribute(User.USERID);

			// parametri di inserimento
			codiceFiscale = (String) request.getAttribute("strCodiceFiscale");
			nome = (String) request.getAttribute("strNome");
			cognome = (String) request.getAttribute("strCognome");
			dataNascita = (String) request.getAttribute("datnasc");
			comune = (String) request.getAttribute("codComNas");

			// cpi del lavoratore
			StoriaInfLavoratore infLav = new StoriaInfLavoratore(request.getAttribute("cdnLavoratore"));
			String codCpiTit = infLav.getCodCpiTit();
			codMonoTipoCpi = infLav.getCodMonoTipoCpi();
			dataInizio = infLav.getDatInizio();
			// cpiComp= (String) request.getAttribute("codCpiLav");

			cdnGruppo = String.valueOf(user.getCdnGruppo());
			cdnProfilo = String.valueOf(user.getCdnProfilo());
			strMittente = user.getNome() + " " + user.getCognome();
			cdnUtente = InfoProvinciaSingleton.getInstance().getCodice();
			poloMittente = InfoProvinciaSingleton.getInstance().getCodice();

			codCpi = user.getCodRif(); // cpi del centro per l'impiego che sta
										// inserendo i dati

			// codMonoTipoCpi="C"; //il cpi del lavoratore e il cpi di
			// inserimento coincidono sempre in tal caso: il cpi ha la
			// competenza amministrativa

			TestataMessageTO testataMessaggio = new TestataMessageTO();

			testataMessaggio.setPoloMittente(poloMittente);
			testataMessaggio.setCdnUtente(cdnUtente);
			testataMessaggio.setCdnGruppo(cdnGruppo);
			testataMessaggio.setCdnProfilo(cdnProfilo);
			testataMessaggio.setStrMittente(strMittente);

			PutLavoratoreIRMessage putLavoratoreIRMessage = new PutLavoratoreIRMessage();
			putLavoratoreIRMessage.setTestata(testataMessaggio);

			// imposto i parametri applicativi
			putLavoratoreIRMessage.setCodiceFiscale(codiceFiscale);
			putLavoratoreIRMessage.setNome(nome);
			putLavoratoreIRMessage.setCognome(cognome);
			putLavoratoreIRMessage.setDataNascita(dataNascita);
			putLavoratoreIRMessage.setComune(comune);
			putLavoratoreIRMessage.setDataInizio(dataInizio);
			putLavoratoreIRMessage.setCodCpi(codCpiTit);
			putLavoratoreIRMessage.setCodMonoTipoCpi(codMonoTipoCpi);
			// il codcpicomp di ir_lav_storia_mappatura verra' cancellato
			putLavoratoreIRMessage.setCpiComp(codCpi);

			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();

			OutQ outQ = new OutQ();

			// mando il messaggio
			putLavoratoreIRMessage.setDataSourceJndi(dataSourceJndiName);
			putLavoratoreIRMessage.send(outQ);
			response.setAttribute("retCode", "0");
			reportOperation.reportSuccess(MessageCodes.Coop.INSERIMENTO_IR_SUCCESS);
		} catch (Exception exc) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "PutLavoratoreIR::service()", exc);

			esito = false;
			reportOperation.reportFailure(MessageCodes.Coop.ERR_INSERIMENTO_IR);
		}

	}

}