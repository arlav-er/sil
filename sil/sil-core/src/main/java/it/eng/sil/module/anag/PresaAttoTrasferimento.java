/*
 * Creato il 1-lug-04
 */
package it.eng.sil.module.anag;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.messages.TestataMessageTO;
import it.eng.sil.coop.messages.jmsmessages.NotificaLavoratoreSILMessage;
import it.eng.sil.coop.queues.OutQ;
import it.eng.sil.coop.services.EseguiPresaAttoAutomatica;
import it.eng.sil.coop.utils.CoopMessageCodes;
import it.eng.sil.coop.utils.MessageBundle;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.eng.sil.util.InfoProvinciaSingleton;
import it.eng.sil.util.Utils;
import it.eng.sil.util.amministrazione.impatti.DBLoad;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean;

/**
 * @author rolfini featuring roccetti (TraIntraProvinciale)
 * 
 *         Prende atto di un trasferimento avvenuto: Aggiorna il domicilio del lavoratore su AN_LAVORATORE Chiude
 *         AN_LAV_STORIA_INF, chiude AM_ELENCO_ANAGRAFICO, Chiude l'eventuale patto aperto per il lavoratore su
 *         AM_PATTO_LAVORATORE
 */
public class PresaAttoTrasferimento extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(PresaAttoTrasferimento.class.getName());

	/**
	 * @see com.engiweb.framework.dispatching.module.AbstractModule#service(com.engiweb.framework.base.SourceBean,
	 *      com.engiweb.framework.base.SourceBean)
	 */
	public void service(SourceBean request, SourceBean response) throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		StatoOccupazionaleBean so = null;
		// Segnalazione soli errori/problemi
		disableMessageIdSuccess();
		// 16/08/2006 savino: presa atto in cooperazione
		// controllo se il cpi associato al prgpresaatto corrisponde al cpi
		// iviato (codcpi)
		// se si devo chiamare la presa atto automatica altrimenti passo al
		// codice di questo modulo (gestione precedente)
		boolean coopAbilitata = false;
		String coopAttiva = System.getProperty("cooperazione.enabled");
		if (coopAttiva != null && coopAttiva.equals("true")) {
			coopAbilitata = true;
		}
		if (coopAbilitata && vediSePresaAttoAutomatica(request, response)) {
			boolean result = presaAttoAutomatica(request, response);
			if (!result) {
				response.setAttribute("trasferito", "false");
				// se non e' stato inserito nessun errore nell'error handler lo
				// inserisco adesso
				if (getErrorHandler().isOK())
					reportOperation.reportFailure(MessageCodes.Trasferimento.ERR_PRESA_ATTO_AUTOMATICA);
			} else {
				response.setAttribute("trasferito", "true");
				reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
			}
			return;
		}
		// fine modifica 11/08/2006
		boolean isPattoAperto = false;
		// Controllo se ho un patto da chiudere
		String prgPattoLav = StringUtils.getAttributeStrNotNull(request, "PRGPATTOLAVORATORE");
		if (!prgPattoLav.equals("") && !prgPattoLav.equalsIgnoreCase("null")) {
			isPattoAperto = true;
		}
		// Transazione per AN_LAVORATORE, AN_LAV_STORIA_INF,
		// AM_ELENCO_ANAGRAFICO, AM_PATTO_LAVORATORE

		TransactionQueryExecutor trans = null;

		try {
			trans = new TransactionQueryExecutor(getPool());
			this.enableTransactions(trans);
			trans.initTransaction();
			// end presa atto in cooperazione

			// Dopo ogni operazione raccoglie il risultato
			boolean result = true;
			setSectionQuerySelect("GET_DID_VALIDA");
			SourceBean did = doSelect(request, response, false); // sarà da
																	// chiudere!
			// aggiorno domicilio lavoratore
			setSectionQueryUpdate("QUERY_UPDATE_DOM_LAV");
			result = doUpdate(request, response);

			if (!result) {
				trans.rollBackTransaction();
				response.setAttribute("trasferito", "false");
				reportOperation.reportFailure(MessageCodes.Trasferimento.ERR_UPDATE_DOM_LAV);
				return;
			}
			// Esecuzione della query su AN_LAV_STORIA_INF (Il record precedente
			// viene chiuso con una stored procedure)
			setSectionQueryInsert("QUERY_INSERT_AN_LAV_S");
			result = doInsert(request, response);

			if (!result) {
				trans.rollBackTransaction();
				response.setAttribute("trasferito", "false");
				reportOperation.reportFailure(MessageCodes.Trasferimento.ERR_CLOSE_AN_LAV_S);
				return;
			}
			// Chiudo il record precedente su AM_ELENCO_ANAGRAFICO
			setSectionQueryUpdate("QUERY_UPDATE_AM_EL_ANAG");
			result = doUpdate(request, response);

			if (!result) {
				trans.rollBackTransaction();
				response.setAttribute("trasferito", "false");
				reportOperation.reportFailure(MessageCodes.Trasferimento.ERR_CLOSE_AM_EL_ANAG);
				return;
			}
			// Chiudo la DID
			if (did != null && did.containsAttribute("row")) {
				setSectionQueryUpdate("CLOSE_DICH_DISP");
				request.updAttribute("prgdichdisponibilita", did.getAttribute("row.prgdichdisponibilita"));
				BigDecimal numKlo = (BigDecimal) did.getAttribute("row.numklodichdisp");
				request.updAttribute("numklodichdisp", numKlo.add(new BigDecimal(1)));
				boolean ret = doUpdate(request, response);
				if (!ret) {
					trans.rollBackTransaction();
					response.setAttribute("trasferito", "false");
					reportOperation.reportFailure(MessageCodes.Trasferimento.ERR_UPD_AM_DICH_DISP);
					return;
				}
			}

			if (isPattoAperto) {
				// Chiudo il record precedente su AM_PATTO_LAVORATORE
				setSectionQueryUpdate("QUERY_UPDATE_AM_PATTO_LAV");
				result = doUpdate(request, response);
				if (!result) {
					trans.rollBackTransaction();
					response.setAttribute("trasferito", "false");
					reportOperation.reportFailure(MessageCodes.Trasferimento.ERR_CLOSE_AM_PATTO_LAV);
					return;
				}
			}
			// 4/7/2005 - Correzione anomalia che permetteva di avere due stati occ.
			// alla stessa data
			Vector vectSo = DBLoad.getStatiOccupazionali(request.getAttribute("cdnLavoratore"), trans);
			so = vectSo.size() > 0 ? new StatoOccupazionaleBean((SourceBean) vectSo.get(vectSo.size() - 1)) : null;
			if (so != null && (DateUtils.compare(so.getDataInizio(),
					(String) request.getAttribute("DATTRASFERIMENTO")) == 0)) {
				// Aggiorno lo stato occupazionale
				BigDecimal numklostatooccupaz = so.getNumKlo().add(new BigDecimal(1));
				BigDecimal prgStatoOcc = so.getPrgStatoOccupaz();
				request.setAttribute("numklostatooccupaz", numklostatooccupaz);
				request.setAttribute("prgStatoOccupaz", prgStatoOcc);
				setSectionQueryUpdate("QUERY_UPDATE_STATO_OCC");
				request.updAttribute("codStatoOcc", "C");
				result = doUpdate(request, response);
			} else {
				// Inserisco un nuovo stato occupazionale
				setSectionQueryInsert("INSERT_NEW_STATO_OCC");
				request.updAttribute("codStatoOcc", "C");
				result = doInsert(request, response);
			}
			// 4/7/2005 - Fine correzione anomalia
			if (!result) {
				trans.rollBackTransaction();
				response.setAttribute("trasferito", "false");
				reportOperation.reportFailure(MessageCodes.StatoOccupazionale.ERRORE_INS_STATO_OCC);
				return;
			}
			if (!chiudiCollocamentoMirato(request, trans)) {
				trans.rollBackTransaction();
				response.setAttribute("trasferito", "false");
				// TODO correggere messaggio chiusura collocamento mirato
				reportOperation.reportFailure(CoopMessageCodes.Trasferimento.ERR_CLOSE_CM_ISCR);
				return;
			}
			// 11/08/2006 savino: presa atto in cooperazione
			// chiusura delle richieste attive
			if (!chiudiRichiesteAttive(trans, request, response)) {
				trans.rollBackTransaction();
				response.setAttribute("trasferito", "false");
				reportOperation.reportFailure(MessageCodes.Trasferimento.ERR_PRESA_ATTO_CHIUSURA_RICHIESTE);
				return;
			} // fine modifica 11/08/2006
			else {
				trans.commitTransaction();
				response.setAttribute("trasferito", "true");
				reportOperation.reportSuccess(MessageCodes.General.OPERATION_SUCCESS);
			}
		} catch (Exception e) {
			if (trans != null) {
				trans.rollBackTransaction();
			}
			it.eng.sil.util.TraceWrapper.debug(_logger, "Errore durante il trasferimento del lavoratore", e);
			response.setAttribute("trasferito", "false");
			reportOperation.reportFailure(MessageCodes.General.OPERATION_FAIL);
			return;
		}
	}

	private boolean chiudiCollocamentoMirato(SourceBean request, TransactionQueryExecutor trans) {
		String cdnLavoratore = (String) request.getAttribute("cdnLavoratore");
		try {
			String dataTrasferimento = Utils.notNull(request.getAttribute("DATTRASFERIMENTO"));
			User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);
			String cdnUt = String.valueOf(user.getCodut());

			String keyCifratura = System.getProperty("_ENCRYPTER_KEY_");

			String sql = "update am_cm_iscr set datdatafine = TO_DATE('" + dataTrasferimento + "','DD/MM/YYYY')-1, "
					+ "NUMKLOCMISCR = NUMKLOCMISCR + 1, CODMOTIVOFINEATTO = 'TD', " + "CDNUTMOD = " + cdnUt
					+ ", DTMMOD = SYSDATE " + "WHERE am_cm_iscr.prgcmiscr in ("
					+ "select c.prgcmiscr FROM am_cm_iscr c, DE_CM_TIPO_ISCR , am_documento d, am_documento_coll coll where "
					+ cdnLavoratore + " = decrypt (c.cdnlavoratore, '" + keyCifratura
					+ "') and DE_CM_TIPO_ISCR.CODCMTIPOISCR = c.CODCMTIPOISCR"
					+ " and d.PRGDOCUMENTO = coll.PRGDOCUMENTO and coll.STRCHIAVETABELLA = c.PRGCMISCR "
					+ " and c.DATDATAFINE is null and d.CODSTATOATTO = 'PR'" + " and d.CDNLAVORATORE =  "
					+ cdnLavoratore + ")";
			Connection dataConnection = trans.getDataConnection().getInternalConnection();
			Statement st = dataConnection.createStatement();
			int n = st.executeUpdate(sql);
			st.close();
			return true;
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"Errore in fase di chiusura del collocamento mirato per il lavoratore  " + cdnLavoratore, e);

			return false;
		}
	}

	/**
	 * Se il sevizio di presa atto automatica lancia una eccezione EMFUserError allora questa viene inserita nell'error
	 * handler della risposta. Nella pagina verra' stampato il messaggio associato all'errore, in modo equivalente
	 * all'operazione di presa atto non automatica.
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private boolean presaAttoAutomatica(SourceBean request, SourceBean response) {

		User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);

		String cdnGruppo = String.valueOf(user.getCdnGruppo());
		String cdnProfilo = String.valueOf(user.getCdnProfilo());
		String cdnUt = String.valueOf(user.getCodut());
		String strMittente = InfoProvinciaSingleton.getInstance().getNome();
		String cdnUtente = InfoProvinciaSingleton.getInstance().getCodice();
		String poloMittente = InfoProvinciaSingleton.getInstance().getCodice();

		// purtroppo bisogna trasportare i dati in un nuovo oggetto
		// serviceRequest con chiavi diverse
		String codiceFiscale = Utils.notNull(request.getAttribute("STRCODICEFISCALE"));
		String dataTrasferimento = Utils.notNull(request.getAttribute("DATTRASFERIMENTO"));
		String codComDomNuovo = Utils.notNull(request.getAttribute("codComdom"));
		String indirizzoDomNuovo = Utils.notNull(request.getAttribute("STRINDIRIZZODOM"));
		String cognomeNuovo = Utils.notNull(request.getAttribute("strCognome"));
		String nomeNuovo = Utils.notNull(request.getAttribute("strNome"));
		String comuneNascNuovo = Utils.notNull(request.getAttribute("strComdom"));
		String dataNascNuovo = Utils.notNull(request.getAttribute("datNasc"));
		String prgPresaAtto = Utils.notNull(request.getAttribute("prgPresaAtto"));
		// il nuovo cpi competente del lavoratore, quello al quale e' stato
		// trasferito
		String codCpiDestinazione = (String) request.getAttribute("codcpi");
		String codCpiOrig = (String) request.getAttribute("codcpiOrig");

		_logger.debug("Presa atto: chiamata la procedura automatica per il lavoratore " + codiceFiscale);

		// leggi provincia di appartenenza del cpi a cui il lavoratore e' stato
		// trasferito

		/*
		 * setSectionQuerySelect("INFO_CPI_LOCALE"); row = doSelect(request, response, false); codCpiLocale =
		 * (String)row.getAttribute("row.codcpicapoluogo"); descCpiLocale =
		 * (String)row.getAttribute("row.strdenominazione");
		 */

		TransactionQueryExecutor tex = null;
		boolean ret = false;
		try {
			setSectionQuerySelect("COD_PROVINCIA_CPI");
			request.updAttribute("codcpi_rif", codCpiDestinazione);
			SourceBean row = doSelect(request, response, false);
			String codProvinciaDestinazione = (String) row.getAttribute("row.codProvincia");
			String strProvinciaDestinazione = (String) row.getAttribute("row.provincia");
			if (codProvinciaDestinazione == null) {
				throw new Exception("Impossibile leggere il codProvincia di destinazione del servizio invio dati");
			}
			setSectionQuerySelect("INFO_DE_CPI");
			request.updAttribute("codcpi_rif", codCpiOrig);
			row = doSelect(request, response, false);
			// String codCpiOrig = (String)row.getAttribute("row.codcpi");
			String desCpiOrig = (String) row.getAttribute("row.strdescrizione");
			request.updAttribute("codcpi_rif", codCpiDestinazione);
			row = doSelect(request, response, false);
			// String codCpiOrig = (String)row.getAttribute("row.codcpi");
			String desCpiDestinazione = (String) row.getAttribute("row.strdescrizione");
			SourceBean _request = new SourceBean("SERVICE_REQUEST");
			/*******************************************************************
			 * _request.setAttribute("cdnutente", cdnUtente); _request.setAttribute("cdnGruppo", cdnGruppo);
			 * _request.setAttribute("cdnProfilo", cdnProfilo); _request.setAttribute("strMittente", strMittente);
			 ******************************************************************/
			/*
			 * _request.setAttribute("PoloMittente", poloMittente); _request.setAttribute("Destinazione",
			 * codProvinciaDestinazione);
			 */
			// e' necessario questo 'scambio' perche' il servizio di invio dati
			// si attende come polo mittente
			// il polo da cui ha ricevuto la chiamata del servizio della presa
			// atto
			/*******************************************************************
			 * _request.setAttribute("PoloMittente", codProvinciaDestinazione); _request.setAttribute("Destinazione",
			 * poloMittente);
			 ******************************************************************/
			// al posto di queste informazioni costruisco un oggetto
			// TestataMessageTO
			TestataMessageTO testataPoloPresaAtto = new TestataMessageTO();
			testataPoloPresaAtto.setCdnGruppo(cdnGruppo);
			testataPoloPresaAtto.setCdnProfilo(cdnProfilo);
			testataPoloPresaAtto.setCdnUtente(cdnUtente);
			testataPoloPresaAtto.setDestinazione(null);
			testataPoloPresaAtto.setPoloMittente(cdnUtente);
			testataPoloPresaAtto.setStrMittente(strMittente);
			testataPoloPresaAtto.setServizio(null);
			// reperisco le informazioni dal db
			TestataMessageTO testataPoloTrasferimento = new TestataMessageTO();
			testataPoloTrasferimento.setCdnUtente(codProvinciaDestinazione);
			testataPoloTrasferimento.setPoloMittente(codProvinciaDestinazione);
			testataPoloTrasferimento.setStrMittente(strProvinciaDestinazione);

			_request.setAttribute("POLO_PRESA_ATTO", testataPoloPresaAtto);
			_request.setAttribute("POLO_TRASFERIMENTO", testataPoloTrasferimento);

			_request.setAttribute("CPI_PRESA_ATTO.codcpi", codCpiOrig);
			_request.setAttribute("CPI_PRESA_ATTO.descrizione", desCpiOrig);
			_request.setAttribute("CPI_TRASFERIMENTO.codcpi", codCpiDestinazione);
			_request.setAttribute("CPI_TRASFERIMENTO.descrizione", desCpiDestinazione);

			_request.setAttribute("codCpiTitNuovo", codCpiDestinazione);
			_request.setAttribute("cdnut", cdnUt);

			_request.setAttribute("CODICEFISCALE", codiceFiscale);
			_request.setAttribute("DATATRASF", dataTrasferimento);
			_request.setAttribute("CODCOMUNEDOM", codComDomNuovo);
			_request.setAttribute("INDIRIZZODOM", indirizzoDomNuovo);
			_request.setAttribute("cognome", cognomeNuovo);
			_request.setAttribute("nome", nomeNuovo);
			_request.setAttribute("comunenascita", comuneNascNuovo);
			_request.setAttribute("datanascita", dataNascNuovo);
			_request.setAttribute("prgPresaAtto", prgPresaAtto);
			// _request.setAttribute("codCpiLocale", codCpiLocale );
			// _request.setAttribute("descCpiLocale", descCpiLocale );

			tex = new TransactionQueryExecutor(getPool());
			enableTransactions(tex);
			tex.initTransaction();
			Connection connection = tex.getDataConnection().getInternalConnection();

			EseguiPresaAttoAutomatica eseguiPAA = new EseguiPresaAttoAutomatica();
			eseguiPAA.setConnection(connection);
			eseguiPAA.setDataSourceJNDI(new DataSourceJNDI().getJndi());
			// in caso di errore viene lanciata una eccezione (nella response
			// non viene inserito niente)
			eseguiPAA.service(_request, response);
			// se ci sono richieste attive aperte le chiudo
			if (!chiudiRichiesteAttive(tex, request, response)) {
				throw new Exception("Impossibile chiudere le richieste presa atto attive");
			}
			tex.commitTransaction();
			ret = true;
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"Errore in presa atto automatica per il lavoratore " + codiceFiscale, e);

			ret = false;
			if (tex != null)
				try {
					tex.rollBackTransaction();
				} catch (EMFInternalError e1) {
					// e1.printStackTrace();
					it.eng.sil.util.TraceWrapper.debug(_logger,
							"Errore in presa atto automatica per il lavoratore " + codiceFiscale, (Exception) e1);

				}
			// gestione errore con messaggio identico alla presa atto non
			// automatica
			if (e instanceof EMFUserError) {
				getErrorHandler().addError((EMFUserError) e);
			}
		}

		return ret;
	}

	/**
	 * se esistono richieste presa atto nello stato di attive per il lavoratore allora le chiudo (->stato = CH) In
	 * questo caso invio una notifica al cpi che aveva fatto la richiesta (savino 11/08/2006)
	 * 
	 * @return false se si verifica un errore, true se non viene fatta alcuna operazione o se la chiusura e' eseguita
	 *         con successo
	 */
	private boolean chiudiRichiesteAttive(TransactionQueryExecutor trans, SourceBean request, SourceBean response) {
		boolean ret = false;
		// leggo la lista delle richieste del lavoratore
		try {
			setSectionQuerySelect("LISTA_RICHIESTE_PRESA_ATTO_LAV");
			Vector listaRichieste = doSelect(request, response, false).getAttributeAsVector("row");
			String provinciaPolo = InfoProvinciaSingleton.getInstance().getNome();
			for (int i = 0; i < listaRichieste.size(); i++) {
				// per ogni richiesta: la chiudo ed invio una notifica
				SourceBean richiesta = (SourceBean) listaRichieste.get(i);
				String prgPresaAtto = (String) richiesta.getAttribute("prgPresaAtto");

				String codiceFiscale = (String) richiesta.getAttribute("STRCODICEFISCALE");
				String dataTrasferimento = (String) richiesta.getAttribute("DATTRASFERIMENTO");
				String codCpiRich = (String) richiesta.getAttribute("codCpiRich");
				Object numklopresaatto = richiesta.getAttribute("numklopresaatto");
				String cognomeNuovo = (String) richiesta.getAttribute("strCognome");
				String nomeNuovo = (String) richiesta.getAttribute("strNome");
				String dataNascNuovo = (String) richiesta.getAttribute("datNasc");

				Object numKloPresaAtto = richiesta.getAttribute("numKloPresaAtto");
				request.updAttribute("prgPresaAttoDaChiudere", prgPresaAtto);
				request.updAttribute("numKloPresaAttoDaChiudere",
						String.valueOf(Integer.parseInt(numKloPresaAtto.toString()) + 1));
				setSectionQueryUpdate("ANNULLA_RICHIESTA_PRESA_ATTO");
				if (!doUpdate(request, response))
					throw new Exception("Impossibile chiudere una richiesta presa atto ca_presa_atto per il lavoratore "
							+ codiceFiscale);
				String codProvinciaDestinazione = (String) richiesta.getAttribute("codProvincia");
				/*
				 * String messaggio = "La richiesta di presa atto pervenuta al centro per l'impiego "+codCpiLocale+" di
				 * "+descCpiLocale+ " relativa al lavoratore "+cognomeNuovo+" "+ nomeNuovo + " nato il
				 * " + dataNascNuovo+ " in data (trasferimento) " + dataTrasferimento + " " + " e' stata chiusa con
				 * successo il giorno "+DateUtils.getNow() +" in quanto e' stata portata a termine" + " l'operazione di
				 * presa d'atto del trasferimento.";
				 */
				Vector v = new Vector(5);
				v.add(provinciaPolo);
				v.add(cognomeNuovo + " " + nomeNuovo);
				v.add(dataNascNuovo);
				v.add(dataTrasferimento);
				v.add(DateUtils.getNow());
				String messaggioNotifica = MessageBundle.getMessage(MessageCodes.Coop.PRESA_ATTO_CHIUSA_RICHIESTA, v);
				String dataSourceJNDI = new DataSourceJNDI().getJndi();
				// ho bisogno del codprovincia del cpi richiesta per inviare la
				// notifica
				TestataMessageTO testataMessaggio = new TestataMessageTO();
				testataMessaggio.setDestinazione(codProvinciaDestinazione);
				testataMessaggio.setPoloMittente(InfoProvinciaSingleton.getInstance().getCodice());
				NotificaLavoratoreSILMessage notificaLavoratoreSILMessage = new NotificaLavoratoreSILMessage();
				notificaLavoratoreSILMessage.setTestata(testataMessaggio);
				notificaLavoratoreSILMessage.setDataSourceJndi(dataSourceJNDI);
				notificaLavoratoreSILMessage.setCodiceFiscale(codiceFiscale);
				notificaLavoratoreSILMessage.setContenutoMessaggio(messaggioNotifica);
				OutQ outQ = new OutQ();
				notificaLavoratoreSILMessage.send(outQ);
			}
			ret = true;
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Errore nella chiusura delle richiesta presa atto ", e);

			ret = false;
		}
		return ret;
	}

	public boolean vediSePresaAttoAutomatica(SourceBean request, SourceBean response) {
		boolean ret = false;

		String datTrasferimento = (String) request.getAttribute("dattrasferimento");
		String indirizzoDom = (String) request.getAttribute("STRINDIRIZZODOM");
		String codComDom = (String) request.getAttribute("codComdom");
		// String strComDom = (String)request.getAttribute("strComdom");
		String codCpi = (String) request.getAttribute("codCpi");

		setSectionQuerySelect("RICHIESTA_PRESA_ATTO");
		SourceBean richiestaPresaAtto = doSelect(request, response, false);
		String codCpiRich = (String) richiestaPresaAtto.getAttribute("row.codCpiRich");// puo'
																						// essere
																						// null
		if (codCpiRich != null) {
			String datTrasferimentoPresaAtto = (String) richiestaPresaAtto.getAttribute("row.datTrasferimento");
			String indirizzoDomPresaAtto = (String) richiestaPresaAtto.getAttribute("row.strIndirizzoDom");
			String codComDomPresaAtto = (String) richiestaPresaAtto.getAttribute("row.codComDom");
			if (codCpi.equals(codCpiRich) && datTrasferimento.equals(datTrasferimentoPresaAtto)
					&& indirizzoDom.equals(indirizzoDomPresaAtto) && codComDom.equals(codComDomPresaAtto))
				ret = true;
		}
		return ret;
	}

}