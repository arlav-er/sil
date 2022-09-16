package it.eng.sil.module.collocamentoMirato;

import java.math.BigDecimal;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;

public class CMProspCompensazioneSave extends AbstractSimpleModule {

	private static final long serialVersionUID = 4089896767492706737L;

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) throws Exception {

		// RequestContainer requestContainer = getRequestContainer();
		// SessionContainer sessionContainer = requestContainer.getSessionContainer();

		boolean check = true;
		int idSuccess = this.disableMessageIdSuccess();
		// int idFail = this.disableMessageIdFail();
		int msgCode = MessageCodes.General.INSERT_FAIL;
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			String strPrgProspettoInf = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGPROSPETTOINF");
			strPrgProspettoInf = "".equals(strPrgProspettoInf) ? "-1" : strPrgProspettoInf;
			BigDecimal prgProspettoInf = new BigDecimal(strPrgProspettoInf);
			// String prgCompensaz = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGCOMPENSAZ");

			String codMonoCategoria = StringUtils.getAttributeStrNotNull(serviceRequest, "CODMONOCATEGORIA");
			String codMonoEccDiff = StringUtils.getAttributeStrNotNull(serviceRequest, "CODMONOECCDIFF");
			String strCFAZCapoGruppo = StringUtils.getAttributeStrNotNull(serviceRequest, "STRCFAZCAPOGRUPPO");
			String codProvincia = StringUtils.getAttributeStrNotNull(serviceRequest, "CODPROVINCIA");
			String codProvinciaProspetto = StringUtils.getAttributeStrNotNull(serviceRequest, "CODPROVINCIAPROSPETTO");
			String flgEsonero = StringUtils.getAttributeStrNotNull(serviceRequest, "FLGESONERO");
			String codTipoAzienda = StringUtils.getAttributeStrNotNull(serviceRequest, "CODTIPOAZIENDA");
			// String codMonoEccDiffOpposto = "";

			////////////////////////////
			// CONTROLLI MINISTERIALI //
			////////////////////////////

			boolean existsOpposto = false;
			String codMonoEccDiffOpposta = "E".equalsIgnoreCase(codMonoEccDiff) ? "D" : "E";

			// L'autocompensazione non può essere riferita alla stessa provincia del prospetto

			if ("".equalsIgnoreCase(strCFAZCapoGruppo) && codProvincia.equalsIgnoreCase(codProvinciaProspetto)) {
				serviceResponse.setAttribute("ERROR", MessageCodes.ControlliMovimentiDecreto.ERR_STESSA_PROVINCIA);
				reportOperation.reportFailure(MessageCodes.ControlliMovimentiDecreto.ERR_STESSA_PROVINCIA, "", "",
						null);
				return;
			}

			// Per la Pubblica Amministrazione la compensazione deve essere riferita a una provincia nella stessa
			// regione di appartenenza della provincia del prospetto

			if ("PA".equalsIgnoreCase(codTipoAzienda)) {
				String codRegioneCompensazione = getCodRegione(transExec, codProvincia);
				String codRegioneProspetto = getCodRegione(transExec, codProvinciaProspetto);
				if (!codRegioneCompensazione.equalsIgnoreCase(codRegioneProspetto)) {
					serviceResponse.setAttribute("ERROR", MessageCodes.ControlliMovimentiDecreto.ERR_STESSA_REGIONE);
					reportOperation.reportFailure(MessageCodes.ControlliMovimentiDecreto.ERR_STESSA_REGIONE, "", "",
							null);
					return;
				}
			}

			// Per la stessa categoria soggetto (Disabili/Altre categorie protette) non è possibile indicare
			// sia compensazioni in eccedenza che compensazioni in riduzione. Per poter avere una compensazione in
			// eccedenza e una in
			// riduzione per la stessa categoria, è necessario che una di queste deve essere intergruppo
			// (deve avere il Codice fiscale azienda appartenente al gruppo valorizzato)

			if ("".equalsIgnoreCase(strCFAZCapoGruppo)) {
				existsOpposto = existsCompensazione(transExec, prgProspettoInf, codMonoCategoria,
						codMonoEccDiffOpposta);
				if (existsOpposto) {
					serviceResponse.setAttribute("ERROR",
							MessageCodes.ControlliMovimentiDecreto.ERR_COMPENSAZIONI_ECCEDENZA_O_RIDUZIONE_SENZA_CF);
					reportOperation.reportFailure(
							MessageCodes.ControlliMovimentiDecreto.ERR_COMPENSAZIONI_ECCEDENZA_O_RIDUZIONE_SENZA_CF, "",
							"", null);
					return;
				}
			} else {
				existsOpposto = existsCompensazioneByEccDiff(transExec, prgProspettoInf, codMonoCategoria,
						codMonoEccDiffOpposta);
				if (existsOpposto) {
					serviceResponse.setAttribute("ERROR",
							MessageCodes.ControlliMovimentiDecreto.ERR_COMPENSAZIONI_ECCEDENZA_O_RIDUZIONE_CON_CF);
					reportOperation.reportFailure(
							MessageCodes.ControlliMovimentiDecreto.ERR_COMPENSAZIONI_ECCEDENZA_O_RIDUZIONE_CON_CF, "",
							"", null);
					return;
				}
			}

			if ("S".equalsIgnoreCase(flgEsonero) && "E".equalsIgnoreCase(codMonoEccDiff)
					&& codProvinciaProspetto.equalsIgnoreCase(codProvincia)) {

				serviceResponse.setAttribute("ERROR",
						MessageCodes.ControlliMovimentiDecreto.ERR_COMPENSAZIONI_ECCEDENZA_CON_ESONERO);
				reportOperation.reportFailure(
						MessageCodes.ControlliMovimentiDecreto.ERR_COMPENSAZIONI_ECCEDENZA_CON_ESONERO, "", "", null);
				return;

			}

			/////////////////
			// INSERIMENTO //
			/////////////////

			this.setSectionQueryInsert("QUERY_INSERT");
			check = doInsert(serviceRequest, serviceResponse);

			if (!check) {
				throw new Exception("Errore durante l'aggiornamento della compensazione. Operazione interrotta");
			}

			transExec.commitTransaction();

			reportOperation.reportSuccess(idSuccess);

		} catch (Exception e) {
			transExec.rollBackTransaction();
			reportOperation.reportFailure(msgCode, e, "services()", "insert in transazione");

		} finally {
		}

	}

	private boolean existsCompensazioneByEccDiff(TransactionQueryExecutor transExec, BigDecimal prgProspettoInf,
			String codMonoCategoria, String codMonoEccDiffOpposta) throws EMFInternalError {

		Object[] inputParameters = new Object[3];
		inputParameters[0] = codMonoCategoria;
		inputParameters[1] = codMonoEccDiffOpposta;
		inputParameters[2] = prgProspettoInf;

		BigDecimal esiste = null;
		SourceBean sb = (SourceBean) transExec.executeQuery("ExistsCompensazioneByCategoriaByEccDiffAndCf",
				inputParameters, "SELECT");

		if (sb != null && sb.containsAttribute("ROW")) {
			esiste = (BigDecimal) sb.getAttribute("ROW.ESISTE");
			if (esiste != null && esiste.compareTo(new BigDecimal(1)) == 0) {
				return true;
			}
		}

		return false;

	}

	private boolean existsCompensazione(TransactionQueryExecutor transExec, BigDecimal prgProspettoInf,
			String codMonoCategoria, String codMonoEccDiffOpposta) throws EMFInternalError {

		Object[] inputParameters = new Object[3];
		inputParameters[0] = codMonoCategoria;
		inputParameters[1] = codMonoEccDiffOpposta;
		inputParameters[2] = prgProspettoInf;

		BigDecimal esiste = null;
		SourceBean sb = (SourceBean) transExec.executeQuery("ExistsCompensazioneByCategoriaNoCf", inputParameters,
				"SELECT");

		if (sb != null && sb.containsAttribute("ROW")) {
			esiste = (BigDecimal) sb.getAttribute("ROW.ESISTE");
			if (esiste != null && esiste.compareTo(new BigDecimal(1)) == 0) {
				return true;
			}
		}

		return false;

	}

	private String getCodRegione(TransactionQueryExecutor transExec, String codProvincia) throws EMFInternalError {

		Object[] inputParameters = new Object[1];
		inputParameters[0] = codProvincia;

		String codRegione = "";
		SourceBean sb = (SourceBean) transExec.executeQuery("GetCodRegioneByCodProvincia", inputParameters, "SELECT");

		if (sb != null && sb.containsAttribute("ROW")) {
			codRegione = sb.getAttribute("ROW.CODREGIONE") != null ? sb.getAttribute("ROW.CODREGIONE").toString() : "";
		}

		return codRegione;

	}

}
