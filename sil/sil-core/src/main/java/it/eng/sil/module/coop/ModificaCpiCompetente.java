/*
 *
 */
package it.eng.sil.module.coop;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

/**
 * Modicica cpi competente tramite la funzione "Modifica CPI competente" della
 * pagina indirizzi (1.2) Azione sull'indice regionale
 * 
 * @author savino
 */

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.messages.TestataMessageTO;
import it.eng.sil.coop.messages.jmsmessages.ModificaCPICompIRMessage;
import it.eng.sil.coop.queues.OutQ;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.eng.sil.util.InfoProvinciaSingleton;

public class ModificaCpiCompetente extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ModificaCpiCompetente.class.getName());

	public void service(SourceBean request, SourceBean response) {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		// 1. bisogna recuperare il codice fiscale del lavoratore
		// i dati del lavoratore prima dell'aggiornamento del cpi
		try {
			SourceBean lavPrecUpdate = (SourceBean) request.getAttribute("SELECT_AN_LAVORATORE_X_IR.ROWS.ROW");

			if (lavPrecUpdate == null) {
				_logger.debug("ModificaCpiCompetente::service()");

				reportOperation.reportFailure(MessageCodes.Coop.ERR_MODIFICA_CPI_IR);
				return;
			}

			String codiceFiscale = (String) lavPrecUpdate.getAttribute("strcodicefiscale");
			String nuovoCpiComp = (String) request.getAttribute("newCodCpi");

			String cdnGruppo = null, cdnProfilo = null, strMittente = null, poloMittente = null, cdnUtente = null;
			String cognome = null, nome = null, dataRichiesta = null, dataNascita = null, codComNascita = null;
			String codCpiPrec = null, codProvinciaOp = null;

			dataNascita = (String) lavPrecUpdate.getAttribute("datnasc");
			cognome = (String) lavPrecUpdate.getAttribute("strcognome");
			nome = (String) lavPrecUpdate.getAttribute("strnome");
			codCpiPrec = (String) lavPrecUpdate.getAttribute("CODCPITIT");
			codComNascita = (String) lavPrecUpdate.getAttribute("codcomnas");

			dataRichiesta = DateUtils.getNow();
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
			// il polo mitttente e' il De_provincia.codProvincia dell'operatore
			codProvinciaOp = poloMittente;
			// controllo che la provincia del polo e la provincia del nuovo cpi
			// non siano della stessa regione ma appartenenti a provincie
			// diverse
			request.setAttribute("codCpiRiferimento", nuovoCpiComp);
			request.setAttribute("codProvinciaRiferimento", poloMittente);
			SourceBean row = (SourceBean) doSelect(request, response, false);
			if (row == null) {
				_logger.debug("ModificaCpiCompetente::service()");

				reportOperation.reportFailure(MessageCodes.Coop.ERR_MODIFICA_CPI_IR);
				return;
			}
			String controllo = (String) row.getAttribute("row.controllo");
			if (controllo.equals("2")) {
				// caso di provincie diverse ma appartenenti alla stessa
				// regione.
				_logger.debug(
						"Il servizio di forzatura cambio cpi non viene chiamato perche' la provincia del nuovo cpi e' diversa di quella del polo ma appartenente alla stessa regione");

				return;
			}

			TestataMessageTO testata = new TestataMessageTO();
			testata.setPoloMittente(poloMittente);
			testata.setCdnUtente(cdnUtente);
			testata.setCdnGruppo(cdnGruppo);
			testata.setCdnProfilo(cdnProfilo);
			testata.setStrMittente(strMittente);

			ModificaCPICompIRMessage messaggio = new ModificaCPICompIRMessage();
			// imposto i parametri applicativi
			messaggio.setCodCpiNuovo(nuovoCpiComp);
			messaggio.setCodiceFiscale(codiceFiscale);
			messaggio.setCodProvinciaOp(codProvinciaOp); // si potrebbe
															// omettere dato che
															// e' il
															// poloMittente
			messaggio.setCognome(cognome);
			messaggio.setCodComNascita(codComNascita);
			messaggio.setNome(nome);
			messaggio.setDataNascita(dataNascita);
			messaggio.setDataRichiesta(dataRichiesta);
			messaggio.setCodCpiPrec(codCpiPrec); // codcpitit
													// indifferentemente dal
													// mono tipo

			messaggio.setTestata(testata);

			DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
			String dataSourceJndiName = dataSourceJndi.getJndi();

			messaggio.setDataSourceJndi(dataSourceJndiName);

			OutQ outQ = new OutQ();

			messaggio.send(outQ);
			response.setAttribute("retCode", "0");
			reportOperation.reportSuccess(MessageCodes.Coop.INSERIMENTO_IR_SUCCESS);

		} catch (Exception exc) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "ModificaCpiCompetente::service()", exc);

			reportOperation.reportFailure(MessageCodes.Coop.ERR_MODIFICA_CPI_IR);
		}
	}

}
