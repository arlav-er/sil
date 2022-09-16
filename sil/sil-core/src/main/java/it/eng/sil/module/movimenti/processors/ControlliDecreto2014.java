package it.eng.sil.module.movimenti.processors;

import java.util.ArrayList;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.module.movimenti.constant.DeTipoContrattoConstant;
import it.eng.sil.module.movimenti.enumeration.CodMonoTipoEnum;
import it.eng.sil.util.amministrazione.impatti.Controlli;

public class ControlliDecreto2014 implements RecordProcessor {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ControlliDecreto2014.class.getName());

	private String className;
	private String prc;

	private TransactionQueryExecutor trans;

	public ControlliDecreto2014(TransactionQueryExecutor trans) {
		super();
		this.trans = trans;
		className = this.getClass().getName();
		prc = "Controlli Decreto 2014";

	}

	@Override
	public SourceBean processRecord(Map record) throws SourceBeanException {

		ArrayList<Warning> warnings = new ArrayList<Warning>();
		ArrayList errors = new ArrayList();
		Integer codiceErrore = null;
		String dettaglioErrore = "";
		Integer numMesiLavoro = null;

		// ManualValidationFieldExtractor extractor = new ManualValidationFieldExtractor();
		String codCategoriaTir = record.get("CODCATEGORIATIR") != null ? record.get("CODCATEGORIATIR").toString() : "";
		String codTipologiaTir = record.get("CODTIPOLOGIATIR") != null ? record.get("CODTIPOLOGIATIR").toString() : "";
		// String datInizioAvv = (String) record.get("DATAINIZIOAVV") != null ? record.get("DATAINIZIOAVV").toString() :
		// "";
		String datInizioMov = (String) record.get("DATINIZIOMOV") != null ? record.get("DATINIZIOMOV").toString() : "";
		String datFineMov = (String) record.get("DATFINEMOV") != null ? record.get("DATFINEMOV").toString() : "";
		String strCodFiscPromotoreTir = (String) record.get("STRCODFISCPROMOTORETIR") != null
				? record.get("STRCODFISCPROMOTORETIR").toString()
				: "";
		String flgLavoroStagionale = (String) record.get("FLGLAVOROSTAGIONALE") != null
				? record.get("FLGLAVOROSTAGIONALE").toString()
				: "";
		String codTipoAss = (String) record.get("CODTIPOASS") != null ? record.get("CODTIPOASS").toString() : "";
		// String numLivello = (String) record.get("NUMLIVELLO") != null ? record.get("NUMLIVELLO").toString() : "";
		// String decRetribuzioneMen = (String) record.get("DECRETRIBUZIONEMEN") != null ?
		// record.get("DECRETRIBUZIONEMEN").toString() : "";
		// String codCCNL = (String) record.get("CODCCNL") != null ? record.get("CODCCNL").toString() : "";
		// String codTipoMov = (String) record.get("CODTIPOMOV") != null ? record.get("CODTIPOMOV").toString() : "";
		String strDenominazioneTir = (String) record.get("STRDENOMINAZIONETIR") != null
				? record.get("STRDENOMINAZIONETIR").toString()
				: "";
		String codSoggPromotoreMin = (String) record.get("CODSOGGPROMOTOREMIN") != null
				? record.get("CODSOGGPROMOTOREMIN").toString()
				: "";
		// String codMonoTempo = (String) record.get("CODMONOTEMPO") != null ? record.get("CODMONOTEMPO").toString() :
		// "";
		String codMonoTipo = "";

		try {
			SourceBean tipoContrattoRow = ProcessorsUtils.cercaTipoContratto(codTipoAss, trans);
			codMonoTipo = (String) tipoContrattoRow.getAttribute("ROW.CODMONOTIPO");
		} catch (Exception e) {
			_logger.error("Errore nella lettura del codmonotipo del contratto " + codTipoAss, e);
			codMonoTipo = "";
		}

		// codice fiscale tirocinio obbligatorio

		if (CodMonoTipoEnum.TIROCINIO.getCodice().equals(codMonoTipo)) {
			if ("C.01.00".equalsIgnoreCase(codTipoAss)) {
				if ("".equals(strCodFiscPromotoreTir)) {
					// In caso di Tirocinio il codice fiscale del soggetto promotore tirocinio è obbligatorio
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ControlliMovimentiDecreto.ERR_CF_PROMOTORE_OBBLIGATORIO), "",
							warnings, null);
				}
				// In caso di Tirocinio la denominazione del soggetto promotore tirocinio è obbligatorio
				if ("".equals(strDenominazioneTir)) {
					// In caso di Tirocinio il codice fiscale del soggetto promotore tirocinio è obbligatorio
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ControlliMovimentiDecreto.ERR_CF_PROMOTORE_OBBLIGATORIO), "",
							warnings, null);
				}
				// In caso di Tirocinio il tipo soggetto promotore è obbligatorio
				if ("".equals(codSoggPromotoreMin)) {
					// In caso di Tirocinio il codice fiscale del soggetto promotore tirocinio è obbligatorio
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ControlliMovimentiDecreto.ERR_CF_PROMOTORE_OBBLIGATORIO), "",
							warnings, null);
				}
				// In caso di Tirocinio la categoria tirocinante è obbligatoria
				if ("".equals(codCategoriaTir)) {
					// In caso di Tirocinio il codice fiscale del soggetto promotore tirocinio è obbligatorio
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ControlliMovimentiDecreto.ERR_CF_PROMOTORE_OBBLIGATORIO), "",
							warnings, null);
				}
				// In caso di Tirocinio la tipologia tirocinio è obbligatoria
				if ("".equals(codTipologiaTir)) {
					// In caso di Tirocinio il codice fiscale del soggetto promotore tirocinio è obbligatorio
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ControlliMovimentiDecreto.ERR_CF_PROMOTORE_OBBLIGATORIO), "",
							warnings, null);
				}
			}
		}

		// categoria tirocinio & tipologia tirocinio o entrambe valorizzate o entrambe nulle

		// if ("".equals(codCategoriaTir) && !"".equals(codTipologiaTir)) {
		// Se la categoria di tirocinio è valorizzata inserire la tipologia di tirocinio
		// return ProcessorsUtils.createResponse(prc, className, new
		// Integer(MessageCodes.ControlliMovimentiDecreto.ERR_IF_CATEGORIA_TIR_THEN_TIPOLOGIA_TIR), "", warnings, null);
		// }

		// if (!"".equals(codCategoriaTir) && "".equals(codTipologiaTir)) {
		// Se la tipologia di tirocinio è valorizzata inserire la categoria di tirocinio
		// return ProcessorsUtils.createResponse(prc, className, new
		// Integer(MessageCodes.ControlliMovimentiDecreto.ERR_IF_TIPOLOGIA_TIR_THEN_CATEGORIA_TIR), "", warnings, null);
		// }

		// tipologia tirocinio coerente con categoria tirocinio

		boolean isValoreCoerente = false;
		ArrayList<String> valori = new ArrayList<String>();

		if ("A".equalsIgnoreCase(codTipologiaTir)) {

			valori.clear(); // valori ammessi
			valori.add("01");
			valori.add("02");
			valori.add("04");
			valori.add("05");
			valori.add("06");
			valori.add("07");

			isValoreCoerente = false;

			for (int i = 0; i < valori.size(); i++) {
				if (valori.get(i).equalsIgnoreCase(codCategoriaTir)) {
					isValoreCoerente = true;
					break;
				}
			}

			if (!isValoreCoerente) {
				// La tipologia di tirocinio deve essere valorizzata coerentemente con la categoria del tirocinante
				return ProcessorsUtils.createResponse(prc, className,
						new Integer(MessageCodes.ControlliMovimentiDecreto.ERR_TIPOLOGIA_E_CATEGORIA_NON_COERENTI), "",
						warnings, null);
			}

		}

		if ("B".equalsIgnoreCase(codTipologiaTir)) {

			valori.clear(); // valori ammessi
			valori.add("01");
			valori.add("02");
			valori.add("03");
			valori.add("08");

			isValoreCoerente = false;

			for (int i = 0; i < valori.size(); i++) {
				if (valori.get(i).equalsIgnoreCase(codCategoriaTir)) {
					isValoreCoerente = true;
					break;
				}
			}

			if (!isValoreCoerente) {
				// La tipologia di tirocinio deve essere valorizzata coerentemente con la categoria del tirocinante
				return ProcessorsUtils.createResponse(prc, className,
						new Integer(MessageCodes.ControlliMovimentiDecreto.ERR_TIPOLOGIA_E_CATEGORIA_NON_COERENTI), "",
						warnings, null);
			}

		}

		if ("C".equalsIgnoreCase(codTipologiaTir)) {

			valori.clear(); // valori ammessi
			valori.add("09");

			isValoreCoerente = false;

			for (int i = 0; i < valori.size(); i++) {
				if (valori.get(i).equalsIgnoreCase(codCategoriaTir)) {
					isValoreCoerente = true;
					break;
				}
			}

			if (!isValoreCoerente) {
				// La tipologia di tirocinio deve essere valorizzata coerentemente con la categoria del tirocinante
				return ProcessorsUtils.createResponse(prc, className,
						new Integer(MessageCodes.ControlliMovimentiDecreto.ERR_TIPOLOGIA_E_CATEGORIA_NON_COERENTI), "",
						warnings, null);
			}

		}

		// controlli flag stagionale

		if ("S".equals(flgLavoroStagionale)) {

			// solo per le seguenti tipologie contrattuali

			valori.clear(); // valori ammessi
			valori.add("A.02.00");
			valori.add("A.02.01");
			valori.add("B.01.00");
			valori.add("B.02.00");
			valori.add(DeTipoContrattoConstant.LAVORO_COLLABORAZIONE_COORDINATA_CONTINUATIVA);
			valori.add("C.03.00");
			valori.add("A.03.08");
			valori.add("A.03.09");
			valori.add("A.03.10");
			valori.add("A.04.02");
			valori.add("A.08.02");
			valori.add("A.07.02");
			valori.add("A.05.02");
			valori.add("H.02.00");

			isValoreCoerente = false;

			for (int i = 0; i < valori.size(); i++) {
				if (valori.get(i).equalsIgnoreCase(codTipoAss)) {
					isValoreCoerente = true;
					break;
				}
			}

			if (!isValoreCoerente) {
				// Per la tipologia contrattuale selezionata non è possibile specificare lavoro stagionale
				return ProcessorsUtils.createResponse(prc, className,
						new Integer(MessageCodes.ControlliMovimentiDecreto.ERR_TIPOLOGIA_CONTRATTUALE_NON_STAGIONALE),
						"", warnings, null);
			}

			// data fine movimento valorizzata

			// if ("".equals(datFineMov) && !"CES".equalsIgnoreCase(codTipoMov)) {
			// Il campo data fine movimento è obbligatorio in caso di rapporto stagionale
			// return ProcessorsUtils.createResponse(prc, className, new
			// Integer(MessageCodes.ControlliMovimentiDecreto.ERR_DATA_FINE_MOV_OBBL_SE_STAGIONALE), "", warnings,
			// null);
			// }

		}

		if (!"".equals(codCategoriaTir) && "C.01.00".equalsIgnoreCase(codTipoAss) && !"".equals(datFineMov)) {

			// Calcolo la durata di mesi di lavoro per il movimento corrente

			try {
				numMesiLavoro = new Integer(Controlli.numeroMesiDiLavoro(datInizioMov, datFineMov));
			} catch (Exception e) {
				_logger.error(className + "::processRecord(): Fallito calcolo mesi di lavoro!", e);
				return ProcessorsUtils.createResponse(prc, className,
						new Integer(MessageCodes.ImportMov.ERR_CALCOLO_MESI_LAVORO), null, warnings, null);
			}

			// Se la categoria tirocinante è valorizzata con uno dei codici 04-05-06-07,
			// la durata massima del rapporto deve essere di 6 mesi

			valori.clear(); // valori per 6 mesi
			valori.add("04");
			valori.add("05");
			valori.add("06");
			valori.add("07");

			if (valori.contains(codCategoriaTir) && numMesiLavoro > 6) {
				// Per la categoria tirocinante selezionata la durata massima del rapporto è di sei mesi
				warnings.add(new Warning(MessageCodes.ControlliMovimentiDecreto.WRN_CATEGORIA_TIR_6_MESI, ""));
			}

			// Se la categoria tirocinante è valorizzata con uno dei codici 01-03-08,
			// la durata massima del rapporto deve essere di 12 mesi

			valori.clear(); // valori per 12 mesi
			valori.add("01");
			valori.add("03");
			valori.add("08");

			if (valori.contains(codCategoriaTir) && numMesiLavoro > 12) {
				// Per la categoria tirocinante selezionata la durata massima del rapporto è di 12 mesi
				warnings.add(new Warning(MessageCodes.ControlliMovimentiDecreto.WRN_CATEGORIA_TIR_12_MESI, ""));
			}

			// Se la categoria tirocinante è valorizzata con il codice 02,
			// la durata massima del rapporto deve essere di 24 mesi

			valori.clear(); // valori per 24 mesi
			valori.add("02");
			valori.add("09");

			if (valori.contains(codCategoriaTir) && numMesiLavoro > 24) {
				// Per la categoria tirocinante selezionata la durata massima del rapporto è di 24 mesi
				warnings.add(new Warning(MessageCodes.ControlliMovimentiDecreto.WRN_CATEGORIA_TIR_24_MESI, ""));
			}

		}

		// apprendistati, aggiunti al file Controlli.java in quanto devono scattare prima di altri l' definiti

		/*
		 * if ("A.03.08".equalsIgnoreCase(codTipoAss) || "A.03.09".equalsIgnoreCase(codTipoAss) ||
		 * "A.03.10".equalsIgnoreCase(codTipoAss)) {
		 * 
		 * // Se si inserisce/rettifica/valida un movimento di apprendistato a tempo determinato senza specificare
		 * lavoro stagionale
		 * 
		 * if ("D".equalsIgnoreCase(codMonoTempo) && !"S".equals(flgLavoroStagionale)) { // Per la tipologia di
		 * contratto selezionata è necessario indicare tempo indeterminato return ProcessorsUtils.createResponse(prc,
		 * className, new Integer(MessageCodes.ControlliMovimentiDecreto.ERR_APPRENDISTATO_INDETERMINATO), "", warnings,
		 * null); }
		 * 
		 * // Se si inserisce/rettifica/valida un movimento di apprendistato a tempo indeterminato per un lavoro
		 * stagionale e viene valorizzata la data fine rapporto
		 * 
		 * if ("I".equalsIgnoreCase(codMonoTempo) && "S".equals(flgLavoroStagionale) && !"".equals(datFineMov)) { // Per
		 * la tipologia di contratto selezionata è necessario indicare tempo determinato return
		 * ProcessorsUtils.createResponse(prc, className, new
		 * Integer(MessageCodes.ControlliMovimentiDecreto.ERR_APPRENDISTATO_DETERMINATO), "", warnings, null); }
		 * 
		 * }
		 */
		return ProcessorsUtils.createResponse(prc, className, codiceErrore, dettaglioErrore, warnings, errors);

	}

}
