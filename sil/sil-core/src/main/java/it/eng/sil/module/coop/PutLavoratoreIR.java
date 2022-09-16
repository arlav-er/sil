/*
 * Creato il 07-Apr-04
 * Author: rolfini
 * 
 */

/**
 * @author rolfini
 *
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

public class PutLavoratoreIR extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(PutLavoratoreIR.class.getName());

	public void service(SourceBean request, SourceBean response) {

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

		String retCode = "";
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		boolean esito = true;
		try {
			/*
			 * SourceBean row = doSelect(request, response, false); // SE si verifica un errore evito il null pointer e
			 * proseguo.... if (row==null) dataInizio=DateUtils.getNow(); else
			 * dataInizio=(String)row.getAttribute("row.datInizioStoriaAperta");
			 */
			StoriaInfLavoratore infLav = new StoriaInfLavoratore(request.getAttribute("cdnLavoratore"));
			dataInizio = infLav.getDatInizio();
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

			cdnGruppo = String.valueOf(user.getCdnGruppo());
			cdnProfilo = String.valueOf(user.getCdnProfilo());
			strMittente = user.getNome() + " " + user.getCognome();
			cdnUtente = InfoProvinciaSingleton.getInstance().getCodice();
			poloMittente = InfoProvinciaSingleton.getInstance().getCodice();

			if (((String) request.getAttribute("CODCOMDOM")).equals("")
					&& ((String) request.getAttribute("CODCPI")).equals("")) {
				cpiComp = (String) request.getAttribute("codCPIifDOMeqRESHid"); // domicilio
																				// vuoto
																				// -
																				// recuperiamo
																				// il
																				// cpiComp
																				// dalla
																				// residenza
			} else {
				cpiComp = (String) request.getAttribute("CODCPI"); // domicilio
																	// asserito:
																	// recuperiamo
																	// il cpi
																	// dall'apposita
																	// casella
			}

			codCpi = user.getCodRif(); // cpi del centro per l'impiego che sta
										// inserendo i dati

			if (codCpi.equals(cpiComp)) {
				codMonoTipoCpi = "C"; // il cpi di domicilio e il cpi di
										// inserimento coincidono: il cpi ha la
										// competenza amministrativa
			} else {
				// -------*test*-------*test*-------
				codMonoTipoCpi = "T";// settaggio di test - bisogna ancora
										// controllare se la residenza sia in
										// regione (credo). Da verificare
										// ulteriormente
				// -------*test*-------*test*-------
			}

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
			putLavoratoreIRMessage.setCodCpi(codCpi);
			putLavoratoreIRMessage.setCodMonoTipoCpi(codMonoTipoCpi);
			putLavoratoreIRMessage.setCpiComp(cpiComp);

			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();

			OutQ outQ = new OutQ();

			putLavoratoreIRMessage.setDataSourceJndi(dataSourceJndiName);

			// mando il messaggio
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