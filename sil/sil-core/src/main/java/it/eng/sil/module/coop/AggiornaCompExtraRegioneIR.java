/*
 * Creato il 20-apr-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.coop;

import java.sql.Connection;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.sql.DataConnection;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.messages.TestataMessageTO;
import it.eng.sil.coop.messages.jmsmessages.AggiornaCompExtraRegioneIRMessage;
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

public class AggiornaCompExtraRegioneIR extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(AggiornaCompExtraRegioneIR.class.getName());

	ReportOperationResult reportOperation;

	public void service(SourceBean request, SourceBean response) {

		String codiceFiscale = null;
		String cognome = null;
		String nome = null;
		String datNasc = null;
		String codComNas = null;

		String cdnGruppo = null;
		String cdnProfilo = null;
		String strMittente = null;
		String poloMittente = null;
		String cdnUtente = null;

		String codCpi = null;
		String codMonoTipoCpi = null;
		String cpiComp = null;
		String datInizio = null;
		String dataValidita = null;
		String retCode = "";

		// prelevo dalla session l'utente
		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);

		// parametri di inserimento
		codiceFiscale = (String) request.getAttribute("strCodiceFiscale");
		cognome = (String) request.getAttribute("strCognome");
		nome = (String) request.getAttribute("strNome");
		datNasc = (String) request.getAttribute("datNasc");
		codComNas = (String) request.getAttribute("codComNas");
		cdnGruppo = "" + user.getCdnGruppo() + "";
		cdnProfilo = "" + user.getCdnProfilo() + "";
		strMittente = user.getNome() + " " + user.getCognome();
		cdnUtente = InfoProvinciaSingleton.getInstance().getCodice();
		poloMittente = InfoProvinciaSingleton.getInstance().getCodice();

		codCpi = (String) request.getAttribute("CODCPIORIG");// VECCHIO CPI
																// COMPETENTE
		// DATA INIZIO VALIDITA' VECCHIO CPI COMPETENTE: usata per il controllo
		// sulle date
		dataValidita = (String) request.getAttribute("DATINIZIO");
		datInizio = (String) request.getAttribute("DATTRASFERIMENTO");// data
																		// inizio
																		// nuova
																		// storia
																		// lavoratore

		cpiComp = (String) request.getAttribute("CODCPI"); // NUOVO CPI
															// COMPETENTE

		codMonoTipoCpi = (String) request.getAttribute("CODMONOTIPOCPI"); // TITOLARITA'
																			// T
																			// VECCHIO
																			// CPI
																			// COMPETENTE

		reportOperation = new ReportOperationResult(this, response);

		boolean esito = true;

		TestataMessageTO testataMessaggio = new TestataMessageTO();
		testataMessaggio.setPoloMittente(poloMittente);
		testataMessaggio.setCdnUtente(cdnUtente);
		testataMessaggio.setCdnGruppo(cdnGruppo);
		testataMessaggio.setCdnProfilo(cdnProfilo);
		testataMessaggio.setStrMittente(strMittente);

		AggiornaCompExtraRegioneIRMessage aggiornaCompExtraRegioneIRMessage = new AggiornaCompExtraRegioneIRMessage();

		aggiornaCompExtraRegioneIRMessage.setTestata(testataMessaggio);

		// imposto i parametri applicativi
		aggiornaCompExtraRegioneIRMessage.setCodCpi(codCpi);
		aggiornaCompExtraRegioneIRMessage.setCodiceFiscale(codiceFiscale);
		aggiornaCompExtraRegioneIRMessage.setCodMonoTipoCpi(codMonoTipoCpi);
		aggiornaCompExtraRegioneIRMessage.setCpiComp(cpiComp);
		aggiornaCompExtraRegioneIRMessage.setDatInizio(datInizio); // verra'
																	// usata
																	// come data
																	// inizio
																	// ins. lav.
		aggiornaCompExtraRegioneIRMessage.setDataValidita(dataValidita); // usata
																			// per
																			// il
																			// controllo
																			// date
		aggiornaCompExtraRegioneIRMessage.setCognome(cognome);
		aggiornaCompExtraRegioneIRMessage.setNome(nome);
		aggiornaCompExtraRegioneIRMessage.setDatNasc(datNasc);
		aggiornaCompExtraRegioneIRMessage.setCodComNas(codComNas);

		DataConnection dataConnection = null;
		Connection dbConnection = null;

		try {
			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();

			aggiornaCompExtraRegioneIRMessage.setDataSourceJndi(dataSourceJndiName);

			OutQ outQ = new OutQ();

			aggiornaCompExtraRegioneIRMessage.send(outQ);
			response.setAttribute("retCode", "0");
			setSuccessMessage(MessageCodes.Coop.INSERIMENTO_IR_SUCCESS);

		} catch (Exception exc) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "AggiornaCompExtraRegioneIR::service()", exc);

			esito = false;
			setFailureMessage(MessageCodes.Coop.ERR_INSERIMENTO_IR);
		} finally {
			try {
				if (dbConnection != null)
					dbConnection.close();
				if (dataConnection != null)
					dataConnection.close();
			} catch (Exception connEx) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "Errore nella chiusura delle connessioni al DB", connEx);

			}

		}
	}

	public void setFailureMessage(int message) {
		reportOperation.reportFailure(message);
	}

	public void setSuccessMessage(int message) {
		reportOperation.reportSuccess(message);
	}

}
