/*
 * Creato il 15-Sep-04
 * Author: rolfini
 * 
 */
package it.eng.sil.module.coop;

import java.sql.Connection;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.sql.DataConnection;

/**
 * @author rolfini
 * 
 */

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.messages.TestataMessageTO;
import it.eng.sil.coop.messages.jmsmessages.AggiornaCompetenzaIRMessage;
import it.eng.sil.coop.queues.OutQ;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.eng.sil.util.InfoProvinciaSingleton;

public class AggiornaCompetenzaIR extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AggiornaCompetenzaIR.class.getName());

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

		String codCpi = null; // cpi master
		String codMonoTipoCpi = null; // tipo di titolarità del master (T, C,
										// X)
		String cpiComp = null; // cpi che ha richiesto il trasferimento
		String datTrasferimento = null; // data del trasferimento

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

		codCpi = (String) request.getAttribute("CODCPI");
		cpiComp = codCpi; // nel trasferimento intraprovinciale il cpi
							// competente il cpi master
		// sono sicuramente sempre lo stesso.
		// cosi' non è per il trasferimento interprovinciale

		// cpiComp=(String) request.getAttribute("CODCPICOMP");

		codMonoTipoCpi = (String) request.getAttribute("CODMONOTIPOCPI");
		datTrasferimento = (String) request.getAttribute("DATTRASFERIMENTO");

		reportOperation = new ReportOperationResult(this, response);

		boolean esito = true;

		// usando l'oggetto WsQueue

		TestataMessageTO testataMessaggio = new TestataMessageTO();
		testataMessaggio.setPoloMittente(poloMittente);
		testataMessaggio.setCdnUtente(cdnUtente);
		testataMessaggio.setCdnGruppo(cdnGruppo);
		testataMessaggio.setCdnProfilo(cdnProfilo);
		testataMessaggio.setStrMittente(strMittente);

		AggiornaCompetenzaIRMessage aggiornaCompetenzaIRMessage = new AggiornaCompetenzaIRMessage();

		aggiornaCompetenzaIRMessage.setTestata(testataMessaggio);

		// imposto i parametri applicativi
		aggiornaCompetenzaIRMessage.setCodCpi(codCpi);
		aggiornaCompetenzaIRMessage.setCodiceFiscale(codiceFiscale);
		aggiornaCompetenzaIRMessage.setCodMonoTipoCpi(codMonoTipoCpi);
		aggiornaCompetenzaIRMessage.setCpiComp(cpiComp);
		aggiornaCompetenzaIRMessage.setdatTrasferimento(datTrasferimento);
		aggiornaCompetenzaIRMessage.setCognome(cognome);
		aggiornaCompetenzaIRMessage.setNome(nome);
		aggiornaCompetenzaIRMessage.setDatNasc(datNasc);
		aggiornaCompetenzaIRMessage.setCodComNas(codComNas);

		DataConnection dataConnection = null;
		Connection dbConnection = null;

		try {
			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();

			aggiornaCompetenzaIRMessage.setDataSourceJndi(dataSourceJndiName);

			OutQ outQ = new OutQ();

			aggiornaCompetenzaIRMessage.send(outQ);
			response.setAttribute("retCode", "0");
			setSuccessMessage(MessageCodes.Coop.INSERIMENTO_IR_SUCCESS);

		} catch (Exception exc) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "AggiornaCompetenzaIR::service()", exc);

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
