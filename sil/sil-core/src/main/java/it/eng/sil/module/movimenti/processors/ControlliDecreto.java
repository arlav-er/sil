package it.eng.sil.module.movimenti.processors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.PIVA_utils;
import it.eng.afExt.utils.PivaException;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.coop.webservices.utils.Utils;
import it.eng.sil.module.amministrazione.CalcoloRetribuzione;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.module.movimenti.constant.DeTipoContrattoConstant;
import it.eng.sil.module.movimenti.constant.MotivoCesazioneConstant;
import it.eng.sil.module.movimenti.enumeration.CodMonoTempoEnum;
import it.eng.sil.module.movimenti.enumeration.CodMonoTipoEnum;
import it.eng.sil.module.movimenti.enumeration.TipoMovimentoEnum;
import it.eng.sil.module.movimenti.enumeration.TipoTrasfEnum;
import it.eng.sil.module.movimenti.extractor.ManualValidationFieldExtractor;
import it.eng.sil.util.amministrazione.impatti.MovimentoBean;

/**
 * controlli decreto gennaio 2013
 * 
 * @author pironi
 * 
 */
public class ControlliDecreto implements RecordProcessor {

	private String className;
	private String prc;

	private TransactionQueryExecutor trans;

	public ControlliDecreto(TransactionQueryExecutor trans) {
		super();
		this.trans = trans;
		className = this.getClass().getName();
		prc = "Controlla Decreto";
	}

	@Override
	public SourceBean processRecord(Map record) throws SourceBeanException {
		ArrayList<Warning> warnings = new ArrayList<Warning>();
		ArrayList errors = new ArrayList();
		Integer codiceErrore = null;
		String dettaglioErrore = "";

		ManualValidationFieldExtractor extractor = new ManualValidationFieldExtractor();
		String codTipoMov = extractor.estraiCodTipoMov(record);
		String codMonoTempo = extractor.estraiCodMonoTempo(record);
		String codVariazione = extractor.estraiCodVariazione(record);
		String codTipoContratto = extractor.estraiCodTipoContratto(record);
		String dataFinePeriodoFormativo = extractor.estraiDataFinePeriodoFormativo(record);
		String strCodFiscPromotoreTir = extractor.estraiStrCodFiscPromotoreTir(record);
		String dataInizio = extractor.estraiDataInizio(record);

		/* Decreto 05/11/2019 */
		String versioneTracciato = null;
		String tipoTracciato = null;
		String flgAssPropria = extractor.estraiAssunzionePropria(record);
		// BigDecimal compensoRetribuzioneAnno = extractor.estraiCompensoRetribuzioneAnno(record);
		Double compensoRetribuzioneAnno = extractor.estraiCompensoRetribuzioneAnno(record);
		try {
			boolean checkDataFinePF = false;
			SourceBean tipoContrattoRow = ProcessorsUtils.cercaTipoContratto(codTipoContratto, trans);
			String codMonoTipo = (String) tipoContrattoRow.getAttribute("ROW.CODMONOTIPO");
			String flgTI = (String) tipoContrattoRow.getAttribute("ROW.FLGTI");
			if (codMonoTipo != null && CodMonoTipoEnum.APPRENDISTATO.getCodice().equalsIgnoreCase(codMonoTipo)
					&& flgTI != null && flgTI.equalsIgnoreCase("S")) {
				checkDataFinePF = true;
			} else {
				if (record.containsKey("DATFINEPF")) {
					record.remove("DATFINEPF");
				}
			}

			// b)CONTROLLO BLOCCANTE SUL VALORE CODVARIAZIONE A SECONDA DEL TIPO
			// MOVIMENTO
			if (codVariazione != null && !codVariazione.equals("")) {
				List<String> codVariazioneCompatibili = ProcessorsUtils.cercaCodVariazioneValidi(codTipoMov, trans);
				if (!codVariazioneCompatibili.contains(codVariazione))
					throw new ControlliDecretoApplicationException(
							MessageCodes.ControlliMovimentiDecreto.ERR_COD_VARIAZIONE);
			}

			// CF promotore tirocinio obbligatorio per contratti di tirocinio
			// (anche quelli scaduti: controllare il codmonotipo)
			if (CodMonoTipoEnum.TIROCINIO.getCodice().equals(codMonoTipo)) {
				if (strCodFiscPromotoreTir == null || strCodFiscPromotoreTir.equals("")) {
					if ("C.01.00".equalsIgnoreCase(codTipoContratto)) {
						throw new ControlliDecretoApplicationException(
								MessageCodes.ControlliMovimentiDecreto.ERR_CF_PROMOTORE_TIROCINIO);
					}
				} else {
					// controllo sintassi codice fiscale
					boolean valido = Utils.checkCodiceFiscale(strCodFiscPromotoreTir);
					if (!valido) {
						// controllo se e' una partita iva
						try {
							// controllo SINTASSI BLOCCANTE
							PIVA_utils.verifyPartitaIvaRegEx(strCodFiscPromotoreTir);
						} catch (PivaException e) {
							throw new ControlliDecretoApplicationException(
									MessageCodes.ControlliMovimentiDecreto.ERR_CF_PROMOTORE_TIROCINIO_NON_VALIDO);
						}
						// controlli NON bloccanti sulla partita iva
						try {
							PIVA_utils.verifyPartitaIva(strCodFiscPromotoreTir);

						} catch (PivaException e) {
							warnings.add(new Warning(e.getMessageIdFail(), ""));
						}
					}

				}
			}

			// Decreto 2014: apprendistati
			this.checkApprendistatoStagionale(record, extractor);
			// Decreto 2014: contratti X
			this.checkContrattiXStagionale(record, extractor);

			switch (TipoMovimentoEnum.getValueByCodice(codTipoMov)) {
			case AVVIAMENTO:
				if (checkDataFinePF) {
					this.checkDataFinePeriodoFormativo(dataFinePeriodoFormativo, dataInizio, codTipoMov);
				}
				this.checkDataFineMovimento(codMonoTempo, record, extractor);
				if (checkDataFinePF) {
					this.checkDataFineRapportoApprendistatoStagionale(record, extractor);
				}
				break;
			case PROROGA:
				if (CodMonoTempoEnum.INDETERMINATO.getCodice().equals(codMonoTempo))
					throw new ControlliDecretoApplicationException(
							MessageCodes.ControlliMovimentiDecreto.ERR_PROROGA_SU_I);
				break;
			case TRASFORMAZIONE:
				this.checkDataFineMovimentoTra(codMonoTempo, record, extractor);
				break;
			case CESSAZIONE:
				if (checkDataFinePF) {
					this.checkDataFinePeriodoFormativo(dataFinePeriodoFormativo, dataInizio, codTipoMov);
				}
				this.checkDataFineMovimentoCes(codMonoTempo, record, extractor);
				if (checkDataFinePF) {
					this.checkDataFineRapportoApprendistatoStagionale(record, extractor);
				}
				break;
			default:
				break;
			}

			// Decreto 05/11/2019 :::::::::::::::::::::::::::::::::::::::::::::::::..INIZIO
			boolean checkDecreto2019 = false;
			String codOrario = null;
			String oreSett = null;
			String codCcnl = null;
			String numLivello = null;

			SourceBean response = new SourceBean("SERVICE_RESPONSE");
			codOrario = record.get("CODORARIO") != null ? record.get("CODORARIO").toString() : null;
			oreSett = record.get("NUMORESETT") != null ? record.get("NUMORESETT").toString() : null;
			codCcnl = record.get("CODCCNL") != null ? record.get("CODCCNL").toString() : null;
			numLivello = record.get("NUMLIVELLO") != null ? record.get("NUMLIVELLO").toString() : null;

			CalcoloRetribuzione calcRet = new CalcoloRetribuzione(codOrario, oreSett, codCcnl, numLivello);

			if (record.containsKey("CONTEXT") && record.get("CONTEXT").toString().equalsIgnoreCase("inserisci")) {

				/******************* CONTROLLO PARAMETRI INPUT DALLA SERVICE REQUEST ****************/
				if (codCcnl == null || codCcnl.isEmpty() || numLivello == null || numLivello.isEmpty()
						|| codOrario == null || codOrario.isEmpty() || compensoRetribuzioneAnno == null) {
					codiceErrore = MessageCodes.ControlliMovimentiDecreto.ERR_PARAMETRI_INPUT_MANCANTI_DECRETO_2019;
				}
			} else {
				// validazione manuale o massiva

				if (record.containsKey("STRVERSIONETRACCIATO") && record.get("STRVERSIONETRACCIATO") != null) {
					versioneTracciato = record.get("STRVERSIONETRACCIATO").toString();
					// da inserire in un metodo a parte
					StringTokenizer sToken = new StringTokenizer(versioneTracciato, ",");
					String tk = "-1";
					if (sToken.hasMoreTokens()) {
						tk = sToken.nextToken();
					}
					StringTokenizer sTokenDefault = new StringTokenizer(MovimentoBean.VERSIONE_TRACCIATO_05_11, ",");
					String tkDefault = "-1";
					if (sTokenDefault.hasMoreTokens()) {
						tkDefault = sTokenDefault.nextToken();
					}
					if (Integer.parseInt(tk) >= Integer.parseInt(tkDefault)) {
						// CHECK NUOVI CONTROLLI PER DECRETO 05/11/2019 il controllo non deve scattare il controllo per
						// vardatori e unisomm assunzione propria
						// --- estraiAssunzionePropria con flgassunzionepropria = S non deve fare il controllo
						// SE MANCA STRTRACCIAOTO QUINDI LA CO E ARRIVATA PRIMA DEL 5/11 QUINDI TRACCIATO VECCHIO DEVE
						// FARE I NUOVI CONTROLLI O NO ?????

						if (record.containsKey("STRTRACCIATO") && record.get("STRTRACCIATO") != null) {
							tipoTracciato = record.get("STRTRACCIATO").toString();
							if (!tipoTracciato.isEmpty() && ((tipoTracciato.equalsIgnoreCase(MovimentoBean.UNILAV))
									|| (tipoTracciato.equals(MovimentoBean.UNISOMM)
											&& flgAssPropria.equalsIgnoreCase("N")))) {
								// In validazione manuale l'obbligatorieta dei campi di cui sopra non scattano se si
								// tratta di vardatori o se si tratta di unisomm con assunzione propria.
								// quindi in validazione manuale i controlli sui campi scattano se il tracciato e' nuovo
								// && (si tratta di vardatori o se si tratta di unisomm) con assunzione propria
								// flgAssPropria = N

								/******************* CONTROLLO PARAMETRI INPUT DALLA SERVICE REQUEST ****************/
								if (codCcnl == null || codCcnl.isEmpty() || numLivello == null || numLivello.isEmpty()
										|| codOrario == null || codOrario.isEmpty()
										|| compensoRetribuzioneAnno == null) {
									codiceErrore = MessageCodes.ControlliMovimentiDecreto.ERR_PARAMETRI_INPUT_MANCANTI_DECRETO_2019;
								}
							}
						}
						if (codiceErrore == null) {
							if ("C.01.00".equalsIgnoreCase(codTipoContratto)
									&& !"ND".equalsIgnoreCase(codCcnl.toUpperCase())) {
								codiceErrore = MessageCodes.ControlliMovimentiDecreto.ERR_CCNL_NON_COMPATIBILE;
							}
						}

						if (codiceErrore == null) {
							// se il tracciato e' nuovo ed ha superato tutti i controlli esegui checkCalcoloRetribuzione
							checkDecreto2019 = calcRet.checkCalcoloRetribuzione();
						}
					}
				}
			}

			if (checkDecreto2019) {

				calcRet.calcoloCompensoRetribuzione(response);
				String esito = null;
				String retribuzione = null;
				Double retribuzioneNumber = null;

				if (response.getAttribute("ESITO") != null) {

					esito = response.getAttribute("ESITO").toString();

					if (esito.equalsIgnoreCase("KO")) {
						codiceErrore = ((Integer) response.getAttribute("CODICEERRORE")).intValue();
					} else { // se OK
						if (response.getAttribute("RETRIBUZIONE") != null
								&& !(response.getAttribute("RETRIBUZIONE").toString()).isEmpty()) {
							retribuzione = response.getAttribute("RETRIBUZIONE").toString();
							retribuzioneNumber = new Double(retribuzione);
							if (compensoRetribuzioneAnno != null) { // valore inserito nel campo della form FE
								if (compensoRetribuzioneAnno.compareTo(new Double("1")) < 0
										&& !"C.01.00".equalsIgnoreCase(codTipoContratto)) {
									codiceErrore = MessageCodes.ControlliMovimentiDecreto.ERR_COMPENSO_ANNUALE_NON_VALIDO;
								}

								if (compensoRetribuzioneAnno < retribuzioneNumber) {
									warnings.add(new Warning(
											MessageCodes.ControlliMovimentiDecreto.ERR_COMPENSO_RETRIBUZIONE, ""));
								}
							} else { // compensoRetribuzioneAnno impostato a null
								codiceErrore = MessageCodes.ControlliMovimentiDecreto.ERR_PARAMETRI_INPUT_MANCANTI;
							}
						}
					}
				} else {
					codiceErrore = MessageCodes.ControlliMovimentiDecreto.ERRORE_GENERICO;
				}
			}

			// Decreto 05/11/2019 :::::::::::::::::::::::::::::::::::::::::::::::::..FINE

		} catch (ControlliDecretoApplicationException e) {
			codiceErrore = e.getErrorCode();
			dettaglioErrore = e.getErrorDetail() == null ? "" : e.getErrorDetail();
		} catch (Exception e) {
			codiceErrore = MessageCodes.ControlliMovimentiDecreto.ERRORE_GENERICO;
			dettaglioErrore = e.getMessage();
		}

		return ProcessorsUtils.createResponse(prc, className, codiceErrore, "", warnings, errors);
	}

	private void checkDataFinePeriodoFormativo(String dataFinePeriodoFormativo, String dataInizio, String codTipoMov)
			throws ControlliDecretoApplicationException {
		// c) per contratti di apprendistato la data fine periodo formativo deve
		// essere valorizzata (07/01/2013: SOLO AVV E CESS)
		if (dataFinePeriodoFormativo == null || dataFinePeriodoFormativo.equals(""))
			throw new ControlliDecretoApplicationException(
					MessageCodes.ControlliMovimentiDecreto.ERR_DAT_FINE_PF_NON_VALORIZZATA);
		// La data fine periodo formativo deve essere maggiore della data inizio

		if (TipoMovimentoEnum.getValueByCodice(codTipoMov) == TipoMovimentoEnum.AVVIAMENTO) {
			try {
				if (DateUtils.compare(dataFinePeriodoFormativo, dataInizio) <= 0) {
					throw new ControlliDecretoApplicationException(
							MessageCodes.ControlliMovimentiDecreto.ERR_DATA_FINE_PF_MINORE_DATA_INIZIO);
				}
			} catch (Exception e) {
				throw new ControlliDecretoApplicationException(
						MessageCodes.ControlliMovimentiDecreto.ERR_DATA_FINE_PF_MINORE_DATA_INIZIO);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void checkDataFineMovimento(String codMonoTempo, Map record, ManualValidationFieldExtractor extractor)
			throws ControlliDecretoApplicationException {
		String dataFineMovimento = extractor.estraiDataFineMovimento(record);
		String codTipoContratto = extractor.estraiCodTipoContratto(record);
		if (CodMonoTempoEnum.INDETERMINATO.getCodice().equalsIgnoreCase(codMonoTempo)
				&& !dataFineMovimento.equals("")) {

			if (DeTipoContrattoConstant.APPRENDISTATO_PROFESSIONALIZZANTE_O_CONTRATTO_DI_MESTIERE
					.equalsIgnoreCase(codTipoContratto)) {
				record.remove("DATFINEMOVFITTIZZIA");
				record.put("DATFINEMOVFITTIZZIA", dataFineMovimento);
				record.remove("DATFINEMOV");
			} else
				throw new ControlliDecretoApplicationException(
						MessageCodes.ControlliMovimentiDecreto.ERR_INDETERMINATO_DATA_FINE);
		}
	}

	private void checkDataFineMovimentoTra(String codMonoTempo, Map record, ManualValidationFieldExtractor extractor)
			throws ControlliDecretoApplicationException {
		String dataFineMovimento = extractor.estraiDataFineMovimento(record);
		String codTipoTrasf = extractor.estraiTipoTrasformazione(record);
		if (CodMonoTempoEnum.INDETERMINATO.getCodice().equalsIgnoreCase(codMonoTempo)
				&& !dataFineMovimento.equals("")) {
			if (TipoTrasfEnum.PROSECUZIONE_PERIODO_FORMATIVO.getCodice().equalsIgnoreCase(codTipoTrasf)) {
				record.remove("DATFINEMOVFITTIZZIA");
				record.put("DATFINEMOVFITTIZZIA", dataFineMovimento);
				record.remove("DATFINEMOV");
			}
		}
	}

	private void checkDataFineMovimentoCes(String codMonoTempo, Map record, ManualValidationFieldExtractor extractor)
			throws ControlliDecretoApplicationException {
		String dataFineMovimento = extractor.estraiDataFineMovimento(record);
		String codMvCessazione = extractor.estraiCodMotivoCessazione(record);
		if (CodMonoTempoEnum.INDETERMINATO.getCodice().equalsIgnoreCase(codMonoTempo)
				&& !dataFineMovimento.equals("")) {
			if (!MotivoCesazioneConstant.SOSPESO_DAL_LAVORO.equalsIgnoreCase(codMvCessazione)) {
				throw new ControlliDecretoApplicationException(
						MessageCodes.ControlliMovimentiDecreto.ERR_INDETERMINATO_DATA_FINE);
			}
		}
	}

	private void checkDataFineRapportoApprendistatoStagionale(Map record, ManualValidationFieldExtractor extractor)
			throws ControlliDecretoApplicationException {
		String flgLavoroStagionale = extractor.estraiFlgLavoroStagionale(record);
		if (flgLavoroStagionale != null && ProcessorsUtils.flagToBooleanConverter(flgLavoroStagionale)) {
			String dataFinePeriodoFormativo = extractor.estraiDataFinePeriodoFormativo(record);
			String dataFineMovimento = extractor.estraiDataFineMovimento(record);
			String codTipoMov = extractor.estraiCodTipoMov(record);
			if (dataFineMovimento.equals("")
					&& TipoMovimentoEnum.getValueByCodice(codTipoMov) == TipoMovimentoEnum.CESSAZIONE) {
				dataFineMovimento = extractor.estraiDataFineMovimentoPrecedente(record);
			}
			if (!dataFineMovimento.equals(dataFinePeriodoFormativo))
				throw new ControlliDecretoApplicationException(
						MessageCodes.ControlliMovimentiDecreto.ERR_APPRENDISTATO_DATA_FINE_PERIODO_FORM);
		}
	}

	private void checkApprendistatoStagionale(Map record, ManualValidationFieldExtractor extractor)
			throws ControlliDecretoApplicationException {

		String flgLavoroStagionale = extractor.estraiFlgLavoroStagionale(record);
		String codTipoContratto = extractor.estraiCodTipoContratto(record);
		String datFineMov = extractor.estraiDataFineMovimento(record);
		String codMonoTempo = extractor.estraiCodMonoTempo(record);

		flgLavoroStagionale = flgLavoroStagionale != null ? flgLavoroStagionale : "";
		codTipoContratto = codTipoContratto != null ? codTipoContratto : "";
		datFineMov = datFineMov != null ? datFineMov : "";
		codMonoTempo = codMonoTempo != null ? codMonoTempo : "";

		if ("A.03.08".equalsIgnoreCase(codTipoContratto) || "A.03.09".equalsIgnoreCase(codTipoContratto)
				|| "A.03.10".equalsIgnoreCase(codTipoContratto)) {

			// Se si inserisce/rettifica/valida un movimento di apprendistato a tempo determinato senza specificare
			// lavoro stagionale

			// if ("D".equalsIgnoreCase(codMonoTempo) && !"S".equals(flgLavoroStagionale)) {
			// Per la tipologia di contratto selezionata e' necessario indicare tempo indeterminato
			// throw new
			// ControlliDecretoApplicationException(MessageCodes.ControlliMovimentiDecreto.ERR_APPRENDISTATO_INDETERMINATO);
			// }

			// Se si inserisce/rettifica/valida un movimento di apprendistato a tempo indeterminato per un lavoro
			// stagionale e viene valorizzata la data fine rapporto

			if ("I".equalsIgnoreCase(codMonoTempo) && "S".equals(flgLavoroStagionale) && !"".equals(datFineMov)) {
				// Per la tipologia di contratto selezionata e' necessario indicare tempo determinato
				throw new ControlliDecretoApplicationException(
						MessageCodes.ControlliMovimentiDecreto.ERR_APPRENDISTATO_DETERMINATO);
			}

		}

	}

	private void checkContrattiXStagionale(Map record, ManualValidationFieldExtractor extractor)
			throws ControlliDecretoApplicationException {

		String flgLavoroStagionale = extractor.estraiFlgLavoroStagionale(record);
		String codTipoContratto = extractor.estraiCodTipoContratto(record);
		String datFineMov = extractor.estraiDataFineMovimento(record);
		String codMonoTempo = extractor.estraiCodMonoTempo(record);

		flgLavoroStagionale = flgLavoroStagionale != null ? flgLavoroStagionale : "";
		codTipoContratto = codTipoContratto != null ? codTipoContratto : "";
		datFineMov = datFineMov != null ? datFineMov : "";
		codMonoTempo = codMonoTempo != null ? codMonoTempo : "";

		if ("A.04.02".equalsIgnoreCase(codTipoContratto) || "A.08.02".equalsIgnoreCase(codTipoContratto)
				|| "A.07.02".equalsIgnoreCase(codTipoContratto) || "A.05.02".equalsIgnoreCase(codTipoContratto)) {

			if ("I".equalsIgnoreCase(codMonoTempo) && "S".equals(flgLavoroStagionale)) {
				// Per la tipologia di contratto selezionata e' necessario indicare tempo determinato
				throw new ControlliDecretoApplicationException(
						MessageCodes.ControlliMovimentiDecreto.ERR_CONTRATTO_X_DETERMINATO);
			}

		}

	}

}
