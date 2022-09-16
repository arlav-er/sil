/*
 * Creato il 4-mag-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.movimenti.processors;

import java.util.ArrayList;
import java.util.Map;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.messages.TestataMessageTO;
import it.eng.sil.coop.messages.jmsmessages.PutLavoratoreIRMessage;
import it.eng.sil.coop.queues.OutQ;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.security.User;
import it.eng.sil.util.InfoProvinciaSingleton;

/**
 * @author giuliani
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class ComunicaInsLavoratoreToIR implements RecordProcessor {

	ArrayList warnings = new ArrayList();
	// Vettore dei risultati annidati da restituire
	ArrayList nested = new ArrayList();
	TransactionQueryExecutor transex;

	public ComunicaInsLavoratoreToIR(TransactionQueryExecutor transexec) {
		transex = transexec;
	}

	public SourceBean processRecord(Map record) throws SourceBeanException {
		boolean coopAbilitata = ((ConfigSingleton.getInstance()).getAttribute("COOP.ABILITATA")).equals("true");
		if (coopAbilitata && record.containsKey("INSNUOVOLAV")
				&& ((String) record.get("INSNUOVOLAV")).equalsIgnoreCase("TRUE")) {
			try {
				// Savino 15/05/2006: l'inserimento sullo IR deve usare
				// an_lav_storia_inf.datInizio
				Object cdnLavoratore = record.get("CDNLAVORATORE");
				SourceBean infoLav = (SourceBean) transex.executeQuery("SELECT_AN_LAVORATORE_X_IR",
						new Object[] { cdnLavoratore }, "SELECT");
				String dataInizio = (String) infoLav.getAttribute("row.datInizioStoriaAperta");
				// Preparo il messaggio da inviare all'IR per notificare
				// l'inserimento
				SessionContainer sessione = RequestContainer.getRequestContainer().getSessionContainer();
				User utente = (User) sessione.getAttribute(User.USERID);

				String poloMittente = InfoProvinciaSingleton.getInstance().getCodice();
				String strCdnUtente = InfoProvinciaSingleton.getInstance().getCodice();
				String strCdnGruppo = String.valueOf(utente.getCdnGruppo());
				String strCdnProfilo = String.valueOf(utente.getCdnProfilo());
				String strMittente = utente.getNome() + " " + utente.getCognome();

				TestataMessageTO testataMessaggio = new TestataMessageTO();
				testataMessaggio.setPoloMittente(poloMittente);
				testataMessaggio.setCdnGruppo(strCdnGruppo);
				testataMessaggio.setCdnUtente(strCdnUtente);
				testataMessaggio.setCdnProfilo(strCdnProfilo);
				testataMessaggio.setStrMittente(strMittente);

				PutLavoratoreIRMessage putLavoratoreIRMessage = new PutLavoratoreIRMessage();
				putLavoratoreIRMessage.setTestata(testataMessaggio);

				String codfisc = (String) record.get("STRCODICEFISCALE");
				String cognome = (String) record.get("STRCOGNOME");
				String nome = (String) record.get("STRNOME");
				String dataNascita = (String) record.get("DATNASC");
				String comune = (String) record.get("CODCOMNASC");
				String codCpi = utente.getCodRif();
				String cpiComp = (String) record.get("CODCPILAV");
				;
				String codMonoTipoCpi = codCpi.equals(cpiComp) ? "C" : "T";// Se
																			// il
																			// CPI
																			// competente
																			// del
																			// lavoratore
																			// è lo
																			// stesso
																			// che
																			// sta
				// inserendo allora è i competenti altrimenti titolare

				// imposto i parametri applicativi
				putLavoratoreIRMessage.setCodiceFiscale(codfisc);
				putLavoratoreIRMessage.setNome(nome);
				putLavoratoreIRMessage.setCognome(cognome);
				putLavoratoreIRMessage.setDataNascita(dataNascita);
				putLavoratoreIRMessage.setComune(comune);
				putLavoratoreIRMessage.setDataInizio(dataInizio);
				putLavoratoreIRMessage.setCodCpi(codCpi);
				putLavoratoreIRMessage.setCodMonoTipoCpi(codMonoTipoCpi);
				putLavoratoreIRMessage.setCpiComp(cpiComp);

				// prelevo i nomi della coda di output dai file di
				// configurazione
				DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
				String dataSourceJndiName = dataSourceJndi.getJndi();

				OutQ outQ = new OutQ();

				putLavoratoreIRMessage.setDataSourceJndi(dataSourceJndiName);

				// mando il messaggio
				// ...ma non subito perché potrebbero i conrolli successivi e
				// fare rollback fallire dopo l'inserimento

				putLavoratoreIRMessage.send(outQ);
			} catch (Exception e1) {
				warnings.add(new Warning(MessageCodes.ImportMov.ERR_INSERT_DATA,
						"Fallito inserimento nella coda dei messaggi da inviare all'Indice Regionale"));
				return ProcessorsUtils.createResponse("ComunicaInsLavoratoreToIR", this.getClass().getName(), null,
						null, warnings, nested);
			}
		}
		return null;
	}

}
