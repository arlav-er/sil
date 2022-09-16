package it.eng.sil.module.movimenti.processors;

import java.util.ArrayList;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.module.movimenti.constant.DeTipoContrattoConstant;
import it.eng.sil.module.movimenti.enumeration.CodMonoTempoEnum;
import it.eng.sil.module.movimenti.enumeration.TipoMovimentoEnum;
import it.eng.sil.module.movimenti.enumeration.TipoTrasfEnum;

public abstract class AbstractSelectMovimentoPrecedente implements RecordProcessor {

	/** Dati per la risposta */
	private String name;
	private String classname = this.getClassName();
	/** TransactionQueryExecutor da utilizzare per la query di select */
	private TransactionQueryExecutor trans;
	/**
	 * Processor per la gestione dell'assunzione da cessazione, viene impostato nel modulo di inserimento/validazione
	 */
	private RecordProcessor gestoreCVE = null;

	/**
	 * Costruttore
	 * <p>
	 * 
	 * @param name
	 *            Nome del processore
	 * @param transexec
	 *            TransactionQueryExecutor da utilizzare per le query
	 */
	public AbstractSelectMovimentoPrecedente(String name, TransactionQueryExecutor transexec,
			RecordProcessor gestoreCVE) {
		this.name = name;
		trans = transexec;
		this.gestoreCVE = gestoreCVE;
	}

	public abstract String getClassName();

	@Override
	public final SourceBean processRecord(Map record) throws SourceBeanException {
		// vettore delle warnings da restituire
		ArrayList warnings = new ArrayList();
		// Vettore dei risultati annidati da restituire
		ArrayList nested = new ArrayList();

		// Se il record è nullo non lo posso elaborare, ritorno l'errore
		if (record == null) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
					"Record da elaborare nullo.", warnings, nested);
		}

		return this.customProcessRecord(record, warnings, nested);
	}

	public abstract SourceBean customProcessRecord(Map record, ArrayList warnings, ArrayList nested)
			throws SourceBeanException;

	public SourceBean controlliDecreto(String codTipoMov, String codTipoTrasf, String codTipoContratto,
			String codMonoTempoMovPrec, ArrayList warnings, ArrayList nested) throws SourceBeanException {
		// CONTROLLI DECRETO GENNAIO 2013: se il movimento corrente è
		// trasformazione ZZ e il movimento precedente è a tempo determinato =>
		// blocco
		if (codTipoMov.equalsIgnoreCase(TipoMovimentoEnum.TRASFORMAZIONE.getCodice())
				&& TipoTrasfEnum.PROSECUZIONE_PERIODO_FORMATIVO.getCodice().equalsIgnoreCase(codTipoTrasf)
				&& DeTipoContrattoConstant.APPRENDISTATO_PROFESSIONALIZZANTE_O_CONTRATTO_DI_MESTIERE
						.equalsIgnoreCase(codTipoContratto)) {
			if (codMonoTempoMovPrec.equalsIgnoreCase(CodMonoTempoEnum.DETERMINATO.getCodice()))
				return ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.ControlliMovimentiDecreto.ERR_TRASFORMAZIONE_SU_TD), "", warnings,
						nested);

		}
		// FINE CONTROLLI DECRETO GENNAIO 2013
		return null;
	}

	public String getName() {
		return name;
	}

	public TransactionQueryExecutor getTrans() {
		return trans;
	}

	public RecordProcessor getGestoreCVE() {
		return gestoreCVE;
	}

}
