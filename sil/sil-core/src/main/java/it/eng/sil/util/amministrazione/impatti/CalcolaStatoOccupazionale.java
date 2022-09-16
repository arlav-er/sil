package it.eng.sil.util.amministrazione.impatti;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.processors.ProcessorsUtils;
import it.eng.sil.security.ProfileDataFilter;
import it.eng.sil.security.User;

public class CalcolaStatoOccupazionale extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(CalcolaStatoOccupazionale.class.getName());

	public void service(SourceBean request, SourceBean response) throws Exception {
		String cdnLavoratore = request.getAttribute("cdnLavoratore").toString();
		String dataCalcolo = request.getAttribute("dataInizioCalcolo").toString();
		String operazione = request.getAttribute("CALCOLA").toString();
		String CALCOLA_COMMIT = "1";
		String dataPrec297 = getResponseContainer().getServiceResponse()
				.containsAttribute("M_CONFIG_DATA_NORMATIVA_297.ROWS.ROW.STRVALORE")
						? getResponseContainer().getServiceResponse()
								.getAttribute("M_CONFIG_DATA_NORMATIVA_297.ROWS.ROW.STRVALORE").toString()
						: EventoAmministrativo.DATA_NORMATIVA_DEFAULT;

		TransactionQueryExecutor transExec = null;
		SourceBean rowWarning = null;
		try {
			boolean canEditLav = false;
			RequestContainer requestContainer = RequestContainer.getRequestContainer();
			SessionContainer sessionContainer = requestContainer.getSessionContainer();
			User user = (User) sessionContainer.getAttribute(User.USERID);
			String _pageDaValutare = "";
			_pageDaValutare = "MovDettaglioAvviamentoInserisciPage";
			ProfileDataFilter filter = new ProfileDataFilter(user, _pageDaValutare);
			filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
			canEditLav = filter.canEditLavoratore();

			if (canEditLav) {
				if (DateUtils.compare(dataCalcolo, dataPrec297) >= 0) {
					if (DateUtils.compare(DateUtils.getNow(), dataCalcolo) >= 0) {
						transExec = new TransactionQueryExecutor(getPool());
						transExec.initTransaction();
						StatoOccupazionaleBean statoOccFinale = SituazioneAmministrativaFactory
								.newInstance(cdnLavoratore, dataCalcolo, transExec).calcolaImpatti();
						SourceBean row = new SourceBean("ROW");
						row.setAttribute("Descrizione", statoOccFinale.getDescrizioneCompleta());
						response.setAttribute((SourceBean) row);
						if (operazione.equals(CALCOLA_COMMIT)) {
							transExec.commitTransaction();
						} else {
							transExec.rollBackTransaction();
						}
					} else {
						rowWarning = new SourceBean("ROW");
						rowWarning.setAttribute("Warning",
								"Data futura: lo stato occupazionale del lavoratore non è cambiato");
						response.setAttribute((SourceBean) rowWarning);
					}
				} else {
					rowWarning = new SourceBean("ROW");
					// rowWarning.setAttribute("Warning", "Data precedente al 150: la storia non viene ricostruita");
					rowWarning.setAttribute("Warning", "Data precedente al 297: la storia non viene ricostruita");
					response.setAttribute((SourceBean) rowWarning);
				}
			} else {
				rowWarning = new SourceBean("ROW");
				rowWarning.setAttribute("Warning",
						"Gli impatti amministrativi non sono scattati in quanto l'utente non ha i diritti sul lavoratore");
				response.setAttribute((SourceBean) rowWarning);
			}
		}

		catch (MobilitaException me) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			it.eng.sil.util.TraceWrapper.error(_logger, "Errore in chiusura did.", (Exception) me);

			addConfirm(request, response, me.getCode());
		}

		catch (ControlliException ce) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			it.eng.sil.util.TraceWrapper.error(_logger, "Errore in chiusura did.", (Exception) ce);

			addConfirm(request, response, ce.getCode());
		}

		catch (ProTrasfoException proTrasfEx) {
			it.eng.sil.util.TraceWrapper.error(_logger, "Errore nella ricostruzione della storia.",
					(Exception) proTrasfEx);

			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			rowWarning = new SourceBean("ROW");
			int code = proTrasfEx.getCode();
			if (code == MessageCodes.CollegaMov.ERR_PRO_NON_COLLEGATA) {
				rowWarning.setAttribute("Error", "Movimento di proroga non collegato a nessun movimento precedente.");
			} else {
				rowWarning.setAttribute("Error",
						"Movimento di trasformazione a TD non collegato a nessun movimento precedente.");
			}
			response.setAttribute((SourceBean) rowWarning);
		}

		catch (Exception e) {
			if (transExec != null) {
				transExec.rollBackTransaction();
			}
			it.eng.sil.util.TraceWrapper.error(_logger, "Errore in calcola stato occupazionale.", (Exception) e);

			rowWarning = new SourceBean("ROW");
			rowWarning.setAttribute("Error", "Stato occupazionale non ricalcolato.");
			response.setAttribute((SourceBean) rowWarning);
		}
	}

	private void addConfirm(SourceBean request, SourceBean response, int code) throws Exception {
		String forzaRicostruzione = "";
		String continuaRicalcoloSOccManuale = "";
		ArrayList warnings = new ArrayList();
		ArrayList nested = new ArrayList();
		switch (code) {
		case MessageCodes.StatoOccupazionale.STATO_OCCUPAZIONALE_MANUALE:
		case MessageCodes.StatoOccupazionale.CONSERVA_STATO_OCCUPAZIONALE_MANUALE:
		case MessageCodes.StatoOccupazionale.STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI:
		case MessageCodes.StatoOccupazionale.STATO_OCC_CON_DATA_ANZIANITA_ERRATA:
			continuaRicalcoloSOccManuale = "true";
			if (request.containsAttribute("FORZA_INSERIMENTO")) {
				forzaRicostruzione = request.getAttribute("FORZA_INSERIMENTO").toString();
			}
			break;
		default:
			forzaRicostruzione = "true";
			if (request.containsAttribute("CONTINUA_CALCOLO_SOCC")) {
				continuaRicalcoloSOccManuale = request.getAttribute("CONTINUA_CALCOLO_SOCC").toString();
			}

		}
		SourceBean puResult = ProcessorsUtils.createResponse("", "CalcolaStatoOccupazionale", new Integer(code),
				"Situazione di incongruenza", warnings, nested);
		ProcessorsUtils.addConfirm(puResult, "Vuoi forzare l' operazione?", "ripetiOperazione",
				new String[] { forzaRicostruzione, continuaRicalcoloSOccManuale }, true);
		SourceBean sb = new SourceBean("RECORD");
		sb.setAttribute("RESULT", "ERROR");
		sb.setAttribute(puResult);
		response.setAttribute(sb);
	}

}