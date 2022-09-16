package it.eng.sil.module.movimenti.processors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.module.movimenti.constant.DeTipoContrattoConstant;
import it.eng.sil.module.movimenti.enumeration.CodContrattoEnum;
import it.eng.sil.module.movimenti.enumeration.CodMonoTempoEnum;
import it.eng.sil.module.movimenti.enumeration.CodTipoOrarioEnum;
import it.eng.sil.module.movimenti.extractor.ManualValidationFieldExtractor;

/**
 * Seleziona il codice del contratto a partire dal codTipoAss del record.
 * <p>
 * 
 * @author Paolo Roccetti
 */
public class SelectCodContratto implements RecordProcessor {

	private List<String> contrattiAmmessiOrarioN = Arrays.asList(new String[] {
			DeTipoContrattoConstant.LAVORO_A_PROGETTO_COLLABORAZIONE_COORDINATA_CONTINUATIVA,
			DeTipoContrattoConstant.LAVORO_OCCASIONALE,
			DeTipoContrattoConstant.LAVORO_COLLABORAZIONE_COORDINATA_CONTINUATIVA, DeTipoContrattoConstant.TIROCINIO,
			DeTipoContrattoConstant.LAVORO_ATTIVITA_SOCIALMENTE_UTILE_LSU_ASU,
			DeTipoContrattoConstant.LAVORO_AUTONOMO_NELLO_SPETTACOLO,
			DeTipoContrattoConstant.ASSOCIAZIONE_IN_PARTECIPAZIONE, DeTipoContrattoConstant.CONTRATTO_DI_AGENZIA,
			DeTipoContrattoConstant.LAVORO_INTERMITTENTE, DeTipoContrattoConstant.LAVORO_A_DOMICILIO });

	/** Dati per la risposta */
	private String name;
	private String classname = this.getClass().getName();
	/** TransactionQueryExecutor da utilizzare per la query */
	private TransactionQueryExecutor trans;

	/**
	 * Costruttore
	 * <p>
	 * 
	 * @param name
	 *            Nome del processore
	 * @param transexec
	 *            TransactionQueryExecutor da utilizzare per le query
	 */
	public SelectCodContratto(String name, TransactionQueryExecutor transexec) {
		this.name = name;
		trans = transexec;
	}

	/**
	 * Processa il record. Cerca la proprieta CodTipoAss e cerca nel DB il CodContratto corrispondente
	 * <p>
	 * 
	 * @exception SourceBeanException
	 *                Se avviene un errore nella creazione del SourceBean di risposta
	 */
	public SourceBean processRecord(Map record) throws SourceBeanException {

		// vettore delle warnings da restituire
		ArrayList warnings = new ArrayList();
		// Vettore dei risultati annidati da restituire
		ArrayList nested = new ArrayList();

		ManualValidationFieldExtractor extractor = new ManualValidationFieldExtractor();

		// Se il record è nullo non lo posso elaborare, ritorno l'errore
		if (record == null) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_NULL_RECORD),
					null, warnings, nested);
		}

		// Cancello un eventuale codice del contratto già esistente
		record.remove("CODCONTRATTO");

		// Controllo che il parametro necessario per la query ci sia
		Object codTipoAvv = record.get("CODTIPOASS");

		if (codTipoAvv == null) {
			warnings.add(new Warning(MessageCodes.ImportMov.WAR_TIPO_ASS_NOVALORIZ, null));
			return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
		}

		String codContratto = null;
		String flgSosp2014 = null;
		// Creo la query e la eseguo
		// String query =
		// "SELECT CODCONTRATTO FROM DE_MV_TIPO_ASS WHERE CODTIPOASS = '" +
		// codTipoAvv + "'";
		String query = "SELECT CODCONTRATTO, CODMONOTIPO, FLGTI, FLGSOSPENSIONE2014 FROM DE_TIPO_CONTRATTO WHERE CODTIPOCONTRATTO = '"
				+ codTipoAvv + "'";
		SourceBean result = null;
		try {
			result = ProcessorsUtils.executeSelectQuery(query, trans);
		} catch (Exception e) {
			warnings.add(new Warning(MessageCodes.ImportMov.WAR_COD_CONTR_NULL, ""));
			return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
		}

		// Esamino il risultato
		codContratto = (String) result.getAttribute("ROW.CODCONTRATTO");
		flgSosp2014 = (String) result.getAttribute("ROW.FLGSOSPENSIONE2014");

		if (flgSosp2014 != null) {
			record.put("FLGSOSPENSIONE2014", flgSosp2014);
		}

		if (codContratto != null) {
			if (CodContrattoEnum.LAVORO_DIPENDENTE_TD_TI.getCodice().equalsIgnoreCase(codContratto)) {
				String codMonoTempo = extractor.estraiCodMonoTempo(record);
				if (CodMonoTempoEnum.DETERMINATO.getCodice().equalsIgnoreCase(codMonoTempo)) {
					record.put("CODCONTRATTO", CodContrattoEnum.LAVORO_DIPENDENTE_TD.getCodice());
				} else {
					record.put("CODCONTRATTO", CodContrattoEnum.LAVORO_DIPENDENTE_TI.getCodice());
				}
			} else {
				// valorizzazione codice contratto
				record.put("CODCONTRATTO", codContratto);
			}

			// CONTROLLI DECRETO GENNAIO 2013:
			try {
				this.checkTipoOrario(record, extractor);
			} catch (ControlliDecretoApplicationException e1) {
				// errore bloccante
				String dettaglioErrore = e1.getErrorDetail() == null ? "" : e1.getErrorDetail();
				return ProcessorsUtils.createResponse(name, classname, e1.getErrorCode(), dettaglioErrore, warnings,
						nested);
			}

			// tutto ok ritorna null
			return null;
		} else {
			warnings.add(new Warning(MessageCodes.ImportMov.WAR_COD_CONTR_NULL, null));
			return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
		}
	}

	/**
	 * Il codice N è ammesso solo nei casi di rapporti: B.01.00, B.02.00, B.03.00 C.01.00, C.03.00, G.03.00, L.02.00,
	 * M.02.00, A.05.02, A.08.02
	 * 
	 * @param record
	 * @param extractor
	 * @throws ControlliDecretoApplicationException
	 */
	private void checkTipoOrario(Map record, ManualValidationFieldExtractor extractor)
			throws ControlliDecretoApplicationException {
		String codTipoOrario = extractor.estraiCodTipoOrario(record);
		String codTipoContratto = extractor.estraiCodTipoContratto(record);
		if (!codTipoOrario.equals("") && codTipoOrario.equals(CodTipoOrarioEnum.NON_DEFINITO.getCodice())
				&& !codTipoContratto.equals("")) {
			if (!contrattiAmmessiOrarioN.contains(codTipoContratto))
				throw new ControlliDecretoApplicationException(MessageCodes.ControlliMovimentiDecreto.ERR_TIPO_ORARIO);
		}
	}
}