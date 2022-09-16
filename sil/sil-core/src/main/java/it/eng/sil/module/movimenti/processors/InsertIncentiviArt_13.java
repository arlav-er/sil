/*
 * Created on Feb 7, 2007
 */
package it.eng.sil.module.movimenti.processors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;

/**
 * @author savino
 */
public class InsertIncentiviArt_13 implements RecordProcessor {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InsertIncentiviArt_13.class.getName());
	private String className;
	private String prc;

	private String keyTable;

	private TransactionQueryExecutor trans;

	public InsertIncentiviArt_13(TransactionQueryExecutor txExecutor) {
		className = this.getClass().getName();
		this.trans = txExecutor;
		prc = "Inserimento incentivi art. 13";
	}

	public SourceBean processRecord(Map record) throws SourceBeanException {
		ArrayList warnings = new ArrayList();
		String dataFineSgravio = (String) record.get("DATFINESGRAVIO");
		String importoConcesso = (String) record.get("DECIMPORTOCONCESSO");
		BigDecimal prgMovimento = (BigDecimal) record.get("PRGMOVIMENTO");
		boolean inserisci = (!(importoConcesso == null || importoConcesso.equals(""))
				|| !(dataFineSgravio == null || dataFineSgravio.equals("")));
		if (inserisci && record.get("CONTEXT") != null
				&& (record.get("CONTEXT").toString().equalsIgnoreCase("Inserisci"))) {
			/*
			 * || record.get("CONTEXT").toString().equalsIgnoreCase("valida") ||
			 * record.get("CONTEXT").toString().equalsIgnoreCase("validazioneMassiva"))) {
			 */
			try {
				// Creo la query
				String colonne = "", valori = "";
				if (prgMovimento != null) {
					colonne += "PRGMOVIMENTO";
					valori += prgMovimento.toString();
				}
				if (dataFineSgravio != null) {
					colonne += ", DATFINESGRAVIO";
					valori += ", to_date('" + dataFineSgravio + "','dd/mm/yyyy')";
				}
				if (importoConcesso != null) {
					colonne += ", DECIMPORTOCONCESSO";
					valori += ", " + importoConcesso;
				}
				String statement = "INSERT INTO CM_MOV_L68_ART13 (" + colonne + ") VALUES (" + valori + ")";

				// Ne faccio il tracing
				_logger.debug(className + "::processRecord(): " + statement);

				// Inserisco il record
				Object result = null;
				try {
					result = trans.executeQueryByStringStatement(statement, null, TransactionQueryExecutor.INSERT);
				} catch (EMFInternalError emfie) {
					// Se ho avuto problemi nella query lo segnalo
					_logger.debug(emfie.getMessage());

					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_INCENTIVI_ART_13),
							"Impossibile inserire le informazioni sugli incentivi.", warnings, null);
				}

				// Se ho un'eccezione nel risultato lo segnalo
				if (result instanceof Exception) {
					// Se ho avuto problemi nella query lo segnalo
					_logger.debug(result.toString());

					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_INCENTIVI_ART_13),
							"Impossibile inserire le informazioni sugli incentivi.", warnings, null);
				} else if (result instanceof Boolean && ((Boolean) result).booleanValue() == true) {
					// Controllo se ho avuto warnings e le riporto
					if (warnings.size() > 0) {
						return ProcessorsUtils.createResponse(prc, className, null, null, warnings, null);
					} else {
						return null;
					}
				} else {
					// Se ho avuto problemi nella query lo segnalo
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_INCENTIVI_ART_13),
							"Impossibile inserire le informazioni sugli incentivi.", warnings, null);
				}
			} catch (Exception e) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "InsertIncentiviArt_13", e);

			}
		}
		return null;
	}

}