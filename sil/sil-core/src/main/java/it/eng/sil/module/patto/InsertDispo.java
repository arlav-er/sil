package it.eng.sil.module.patto;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.QueryStrategyException;
import it.eng.sil.util.ExceptionUtils;
import it.eng.sil.util.amministrazione.impatti.Controlli;
import it.eng.sil.util.amministrazione.impatti.ControlliException;
import it.eng.sil.util.amministrazione.impatti.DBLoad;
import it.eng.sil.util.amministrazione.impatti.DIDManager;
import it.eng.sil.util.amministrazione.impatti.SituazioneAmministrativa;

/**
 * NOTE: 1) nel caso sia presente la dichiarazione non viene fatto alcun controllo sel flag (S o N) 2) se non ci sono
 * movimenti non faccio niente con lo stato occupazionale 3) problema: se e' presente uno stato occupazionale precedente
 * cosa faccio? Lo aggiorno o lo chiudo?
 */
public class InsertDispo extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		// avvia transazione
		boolean ret = false;
		Object res = null;
		int errorCode = MessageCodes.General.INSERT_FAIL;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		TransactionQueryExecutor transExec = null;
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		boolean canUpdate = false;
		SituazioneAmministrativa sitAmm = null;

		try {
			transExec = new TransactionQueryExecutor(getPool(), this);
			enableTransactions(transExec);
			transExec.initTransaction();
			//
			if (!esisteInEA(request)) {
				insertInEA(request, response);
			}
			String cdnLavoratore = StringUtils.getAttributeStrNotNull(request, "cdnLavoratore");
			String dataInizio = StringUtils.getAttributeStrNotNull(request, "datDichiarazione");
			Vector statiOccupazionali = DBLoad.getStatiOccupazionali(cdnLavoratore, transExec);
			Vector dids = DBLoad.getDichiarazioniDisponibilitaNonProtocollate(cdnLavoratore, dataInizio, transExec);
			Vector patti = DBLoad.getPattiStoricizzati(cdnLavoratore, dataInizio, transExec);
			Vector movimentiAperti = DBLoad.getMovimentiLavoratore(cdnLavoratore, transExec);
			movimentiAperti = Controlli.togliMovNonProtocollati(movimentiAperti);
			movimentiAperti = Controlli.togliMovimentoInDataFutura(movimentiAperti);
			request.setAttribute("FORZA_CHIUSURA_MOBILITA", "true");
			sitAmm = new SituazioneAmministrativa(movimentiAperti, statiOccupazionali, patti, dids, dataInizio,
					transExec, RequestContainer.getRequestContainer());

			boolean codControllo = DIDManager.inserimentoDID(request, getRequestContainer(), response, sitAmm,
					transExec);

			if (!codControllo) {
				throw new ControlliException(MessageCodes.DID.CONTROLLI_INSERIMENTO_NON_SUPERATI);
			}

			/*********************************************/
			this.setSectionQuerySelect("QUERY_SELECT_DID_PA_APERTA");
			SourceBean rowDidPA_Aperta = doSelect(request, response, false);
			if (rowDidPA_Aperta == null) {
				throw new Exception("impossibile leggere da am_dich_disponibilita");
			}
			Vector vettDidPA_Aperte = rowDidPA_Aperta.getAttributeAsVector("ROW");
			if (vettDidPA_Aperte.size() > 0) {
				throw new Exception(
						"Impossibile rilasciare la dichiarazine di immediata disponibilità: il lavoratore ha già una did aperta in attesa di essere protocollata");
			}
			this.setSectionQuerySelect("QUERY_NEXTVAL");
			SourceBean row = doSelect(request, response, false);
			if (row.containsAttribute("ROW.DO_NEXTVAL")) {
				Object o = row.getAttribute("ROW.DO_NEXTVAL");
				request.updAttribute("PRGDICHDISPONIBILITA", o);
			} else
				throw new Exception("Impossibile leggere il progressivo per la did");
			setSectionQueryInsert("QUERY_INSERT");
			// old call: res = this.doInsert(request, response, true);
			/*
			 * 15/10/2004 In inserimento DID (non protocollata) lo stato occupazionale attribuito sarà null in modo da
			 * risolvere un problema di dereferenzazione in fase di ricostruzione storia per gli impatti amministrativi.
			 */
			if (request.containsAttribute("PRGSTATOOCCUPAZ"))
				request.delAttribute("PRGSTATOOCCUPAZ");

			/* Calcolo se il lavoratore è in 181 */
			String descCat181 = Controlli.getCat181(StringUtils.getAttributeStrNotNull(request, "annoNascita"),
					StringUtils.getAttributeStrNotNull(request, "datDichiarazione"),
					StringUtils.getAttributeStrNotNull(request, "flgObbSco"),
					StringUtils.getAttributeStrNotNull(request, "flgLaurea"));
			String cat181 = "";
			if (descCat181 != null) {
				cat181 = descCat181.substring(0, 1);
				request.updAttribute("cat181", cat181);
			}

			res = this.doInsert(request, response, true);
			ExceptionUtils.controlloDateRecordPrecedente(res,
					MessageCodes.General.ERR_INTERSEZIONE_DATA_REC_STORICIZZATO);
			ret = ((Boolean) res).booleanValue();

			if (!ret) {
				throw new Exception("impossibile inserire il record nella tabella am_dich_sisponibilita");
			}

			// Se esiste il collegamento alla page della DID, allora si esegue un'update, altrimenti
			// una insert col significato di associare il doc. di identif. alla pagina.
			this.setSectionQuerySelect("QUERY_SELECT_AM_DOC_COLL");
			SourceBean coll = doSelect(request, response, false);
			if (coll.containsAttribute("ROW.PRGDOCUMENTOCOLL")) {
				Object obj = coll.getAttribute("ROW.PRGDOCUMENTOCOLL");
				if (obj != null) {
					canUpdate = true;
				}
			}
			if (canUpdate) {
				// Aggiornamento am_documento_coll con il prgdichdisponibilita appena inserito nel campo
				// strchiavetabella
				this.setSectionQueryUpdate("QUERY_UPDATE");
				boolean risUpadate = this.doUpdate(request, response);
				if (!risUpadate) {
					reportOperation.reportFailure(MessageCodes.General.ERR_AGGIORNAMETO_AMDOCCOLL);
					throw new Exception("impossibile aggiornare il record nella tabella am_documento_coll");
				}
			} else {
				// Inserimento nell'am_doc_coll dell'associazione al doc. di ident.
				this.setSectionQueryInsert("QUERY_INSERT_AM_DOC_COLL");
				boolean risInsert = this.doInsert(request, response);
				if (!risInsert) {
					reportOperation.reportFailure(MessageCodes.General.ERR_AGGIORNAMETO_AMDOCCOLL);
					throw new Exception("impossibile inserire il record nella tabella am_documento_coll");
				}
			}
			transExec.commitTransaction();
			reportOperation.reportSuccess(MessageCodes.General.INSERT_SUCCESS);
		} catch (QueryStrategyException qse) {
			reportOperation.reportFailure(MessageCodes.General.ERR_INTERSEZIONE_DATA_REC_STORICIZZATO);

			if (transExec != null) {
				transExec.rollBackTransaction();
			}
		} catch (ControlliException ce) {
			// rollback
			int codeError = ce.getCode();
			if (codeError == MessageCodes.DID.DATA_DID_PRECEDENTE_AL_30012003) {
				Vector paramV = new Vector(1);
				paramV.add(sitAmm.getDataPrec297());
				reportOperation.reportFailure(MessageCodes.DID.DATA_DID_PRECEDENTE_NORMATIVA_297,
						"InsertDispo:execute()", "controlli per inserimento did non superati", paramV);
			} else {
				reportOperation.reportFailure(ce, "InsertDispo:execute()",
						"controlli per inserimento did non superati");
			}

			if (transExec != null) {
				transExec.rollBackTransaction();
			}
		} catch (Exception e) {
			// rollback
			reportOperation.reportFailure(errorCode, e, "execute()",
					"inserimento dichiarazione disponibilità in transazione");

			if (transExec != null) {
				transExec.rollBackTransaction();
			}
		}
	}

	private boolean esisteInEA(SourceBean request) throws Exception {
		String prgEA = (String) request.getAttribute("prgElencoAnagrafico");

		return ((prgEA != null) && (prgEA.length() > 0));
	}

	private void insertInEA(SourceBean request, SourceBean response) throws ControlliException, Exception {
		/*
		 * leggi privacy se esiste la autorizzazione non faccio niente se non esiste la creo
		 *
		 * crea elenco anagrafico
		 *
		 */
		SourceBean row = null;
		setSectionQuerySelect("QUERY_SELECT_PRIVACY");
		row = doSelect(request, response);

		if (row == null) {
			throw new Exception("errore nella lettura della privacy");
		}
		// leggi il prg
		setSectionQuerySelect("QUERY_NEXT_AM_EA");
		row = doSelect(request, response);

		if (row == null) {
			throw new Exception("impossibile leggere il nextval di am_elenco_anagrafico");
		}

		BigDecimal nextVal = (BigDecimal) row.getAttribute("ROW.NEXTVAL");
		request.delAttribute("prgElencoAnagrafico");
		request.setAttribute("prgElencoAnagrafico", nextVal);

		// inserisci il record nell'elenco anagrafico
		setSectionQueryInsert("QUERY_INSERT_EA");
		request.delAttribute("datInizio");
		String dataInizioEA = (String) request.getAttribute("datDichiarazione");
		request.setAttribute("datInizio", dataInizioEA);

		if (!doInsert(request, response)) {
			throw new ControlliException(MessageCodes.ElencoAnagrafico.INSERT_FAIL);
		}
	}

}
