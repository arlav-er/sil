package it.eng.sil.module.movimenti.processors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.util.amministrazione.impatti.Controlli;

/**
 * Classe per la gestione dei controlli sui dati inseriti nelle pagine dei movimenti.
 */
public class ControllaMovimenti implements RecordProcessor {
	private static final int AVVIAMENTO = 1;
	private static final int CESSAZIONE = 2;
	private static final int PRO_TRA = 3;
	private static final int GENERALE = 4;

	private String className;
	private String prc;
	private TransactionQueryExecutor trans;
	private SourceBean sbInfoGenerali = null;
	/** Identificatore utente */
	private BigDecimal userId;

	/**
	 * 
	 * @param sb
	 *            (contiene la riga della tabella TS_GENERALE, non può essere null)
	 * @param transexec
	 * @throws SourceBeanException
	 */
	public ControllaMovimenti(SourceBean sb, TransactionQueryExecutor transexec, BigDecimal user)
			throws SourceBeanException {
		this.className = this.getClass().getName();
		this.prc = "Controlla Movimenti";
		this.trans = transexec;
		this.sbInfoGenerali = sb;
		this.userId = user;
		this.sbInfoGenerali.setAttribute("CHECKFORZAVALIDAZIONE",
				(ProcessorsUtils.checkForzaValidazione(transexec) == true ? "true" : "false"));
		this.sbInfoGenerali.setAttribute("CHECKFORZAMODIFICHE",
				(ProcessorsUtils.checkForzaModifiche(transexec) == true ? "true" : "false"));
	}

	/**
	 * Metodo che riceve in input un oggetto di tipo Map ed a seconda del tipo di movimento istanzia degli oggetti per i
	 * vari controlli.
	 */
	public SourceBean processRecord(Map record) throws SourceBeanException {
		ArrayList warnings = new ArrayList();
		ArrayList errors = new ArrayList();
		Warning w = null;
		boolean ok = true;
		SourceBean result = null;
		int scelta = 0;

		Object obj = record.get("CODTIPOMOV");
		if (obj.toString().equalsIgnoreCase("TRA") || obj.toString().equalsIgnoreCase("PRO")) {
			scelta = PRO_TRA;
		}
		if (obj.toString().equalsIgnoreCase("AVV")) {
			scelta = AVVIAMENTO;
		}
		if (obj.toString().equalsIgnoreCase("CES")) {
			scelta = CESSAZIONE;
		}

		String data150 = sbInfoGenerali.containsAttribute("DAT150") ? sbInfoGenerali.getAttribute("DAT150").toString()
				: "";
		String datInizioMov = (String) record.get("DATINIZIOMOV");
		String dataFineSospensione150 = null;
		try {
			if (datInizioMov != null && !datInizioMov.equals("") && data150 != null && !data150.equals("")
					&& DateUtils.compare(datInizioMov, data150) >= 0) {
				String dataInizioCalcolo150 = datInizioMov;
				BigDecimal prgMovimentoPrec = (BigDecimal) record.get("PRGMOVIMENTOPREC");
				if (prgMovimentoPrec != null) {
					String selectQueryDataInizio = "select to_char(datiniziomov, 'dd/mm/yyyy') datiniziomov "
							+ " from am_movimento where prgmovimento = RECUPERA_PRGMOV_INIZIALE(" + prgMovimentoPrec
							+ ")";
					SourceBean sb = ProcessorsUtils.executeSelectQuery(selectQueryDataInizio, trans);
					if (sb != null) {
						sb = sb.containsAttribute("ROW") ? (SourceBean) sb.getAttribute("ROW") : sb;
						dataInizioCalcolo150 = (String) sb.getAttribute("datiniziomov");
					}
				}
				if (DateUtils.compare(dataInizioCalcolo150, data150) >= 0) {
					dataFineSospensione150 = Controlli.calcolaFineSospensione(dataInizioCalcolo150,
							Properties.MESI_SOSP_DECRETO150);
					if (dataFineSospensione150 != null && !dataFineSospensione150.equals("")) {
						record.put("DATFINESOSP150", dataFineSospensione150);
					}
				}
			}
		} catch (Exception e) {
			dataFineSospensione150 = null;
		}

		/*
		 * La classe per i controlli generali è sempre istanziata
		 */
		ControllaGen ctrlGen = new ControllaGen(sbInfoGenerali, trans);
		result = ctrlGen.processRecord(record);

		if (result != null && ProcessorsUtils.isError(result)) {
			// Vi sono errori
			return result;
		}

		if (result != null) {
			warnings = ProcessorsUtils.getWarnings(result);
			// warnings.add(ProcessorsUtils.getWarnings(result));
		}

		switch (scelta) {
		case 0:
			// errore nella scelta
			break;
		case AVVIAMENTO:
			if (record.get("FLGASSDACESS") == null || !(record.get("FLGASSDACESS").equals("S"))) {
				ControllaAvv ctrlAvv = new ControllaAvv(sbInfoGenerali, trans);
				// record.put("CONTEXT","VALIDA");
				result = ctrlAvv.processRecord(record);
				if (result != null && ProcessorsUtils.isError(result)) {
					// Vi sono errori
					ProcessorsUtils.getUnicoSourceBean(warnings, result, "WARNING");
					return result;
				}

				if (result != null) {
					// warnings.add(ProcessorsUtils.getWarnings(result));
					ArrayList wrn = ProcessorsUtils.getWarnings(result);
					if ((wrn != null) && (wrn.size() > 0)) {
						for (int i = 0; i < wrn.size(); i++) {
							warnings.add((Warning) wrn.get(i));
						}
					}
				}
			}
			break;
		case CESSAZIONE:
			ControllaCessaz ctrlCes = new ControllaCessaz(sbInfoGenerali, trans, userId);
			result = ctrlCes.processRecord(record);
			if (result != null && ProcessorsUtils.isError(result)) {
				// Vi sono errori
				ProcessorsUtils.getUnicoSourceBean(warnings, result, "WARNING");
				return result;
			}

			if (result != null) {
				if (ProcessorsUtils.isWarningAndSTOP(result)) {
					return result;
				}
				ArrayList wrn = ProcessorsUtils.getWarnings(result);
				if ((wrn != null) && (wrn.size() > 0)) {
					for (int i = 0; i < wrn.size(); i++) {
						warnings.add((Warning) wrn.get(i));
					}
				}
			}
			break;
		case PRO_TRA:
			ControllaTP ctrlTraPro = new ControllaTP(sbInfoGenerali, trans);
			result = ctrlTraPro.processRecord(record);
			if (result != null && ProcessorsUtils.isError(result)) {
				// Vi sono errori
				ProcessorsUtils.getUnicoSourceBean(warnings, result, "WARNING");
				return result;
			}

			if (result != null) {
				// warnings.add(ProcessorsUtils.getWarnings(result));
				ArrayList wrn = ProcessorsUtils.getWarnings(result);
				if ((wrn != null) && (wrn.size() > 0)) {
					for (int i = 0; i < wrn.size(); i++) {
						warnings.add((Warning) wrn.get(i));
					}
				}
			}
			break;
		}

		// 18-01-2016 UNISOMM (AZIENDA INTERINALE E ASSUNZIONE NON PROPRIA, RIMUOVERE IL FLAG FLGLEGGE68, POI ANDRANNO
		// RECUPERATI IN ACCORDO CON CARMELA)
		String flgAssPropria = (String) record.get("FLGASSPROPRIA");
		boolean notAssPropria = "N".equalsIgnoreCase(flgAssPropria);
		String codTipoAzienda = record.get("CODAZTIPOAZIENDA") != null ? record.get("CODAZTIPOAZIENDA").toString() : "";
		if (codTipoAzienda.equalsIgnoreCase("INT") && notAssPropria) {
			if (record.containsKey("FLGLEGGE68")) {
				record.remove("FLGLEGGE68");
			}
		}

		return ProcessorsUtils.createResponse(prc, className, null, "", warnings, errors);
	}
}