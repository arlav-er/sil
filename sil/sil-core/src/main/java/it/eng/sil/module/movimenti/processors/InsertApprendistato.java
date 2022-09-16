package it.eng.sil.module.movimenti.processors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;

public class InsertApprendistato implements RecordProcessor {
	private String className;
	private String prc;
	// PrgMovimento appena inserito
	private String keyTable;
	private BigDecimal userId;
	private TransactionQueryExecutor trans;
	private ArrayList warnings = null;
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InsertApprendistato.class.getName());

	public InsertApprendistato(BigDecimal user, TransactionQueryExecutor transexec) {
		className = this.getClass().getName();
		prc = "Inserisci apprendistato";
		userId = user;
		trans = transexec;
	}

	public SourceBean processRecord(Map record) throws SourceBeanException {
		String columns = "";
		String values = "";
		warnings = new ArrayList();

		// Controllo se si tratta di un'apprendistato
		String codMonoTipo = record.get("CODMONOTIPO") != null ? (String) record.get("CODMONOTIPO") : "";
		if (codMonoTipo.equalsIgnoreCase("A")) {
			keyTable = ((BigDecimal) record.get("PRGMOVIMENTO")).toString();
			if (keyTable != null) {

				// ***************
				// Se il record è nullo non lo posso elaborare, ritorno l'errore
				if (record == null) {
					return ProcessorsUtils.createResponse(prc, className,
							new Integer(MessageCodes.ImportMov.ERR_NULL_RECORD), null, warnings, null);
				}

				try {

					columns = getColonne(record);

					// Costruzione dei valori da inserire nel DB
					values = "(" + keyTable + ",";
					values = values + getValori(record);
				} catch (Exception e) {
					if (e.getMessage().equals("NUMMESI")) {
						return ProcessorsUtils.createResponse(prc, className,
								new Integer(MessageCodes.ImportMov.ERR_NO_NUMMESI_APP), null, warnings, null);
					}
				}

				try {
					// Creo la query
					String statement = "INSERT INTO AM_MOVIMENTO_APPRENDIST " + columns + " VALUES " + values;

					// Ne faccio il tracing
					_logger.debug("::processRecord(): " + statement);

					// Inserisco il record
					Object result = null;
					try {
						result = trans.executeQueryByStringStatement(statement, null, TransactionQueryExecutor.INSERT);
					} catch (EMFInternalError emfie) {
						// Se ho avuto problemi nella query lo segnalo
						it.eng.sil.util.TraceWrapper.fatal(_logger, "", emfie.getMessage());
						return ProcessorsUtils.createResponse(prc, className,
								new Integer(MessageCodes.ImportMov.ERR_INSERT_APPRENDISTATO),
								"Impossibile inserire il record in: AM_MOVIMENTO_APPRENDIST.", warnings, null);
					}

					// Se ho un'eccezione nel risultato lo segnalo
					if (result instanceof Exception) {
						// Se ho avuto problemi nella query lo segnalo
						it.eng.sil.util.TraceWrapper.fatal(_logger, "", result.toString());
						return ProcessorsUtils.createResponse(prc, className,
								new Integer(MessageCodes.ImportMov.ERR_INSERT_APPRENDISTATO),
								"Impossibile inserire il record in: AM_MOVIMENTO_APPRENDIST.", warnings, null);
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
								new Integer(MessageCodes.ImportMov.ERR_INSERT_APPRENDISTATO),
								"Impossibile inserire il record in: AM_MOVIMENTO_APPRENDIST. ", warnings, null);
					}
				} catch (Exception e) {
					it.eng.sil.util.TraceWrapper.debug(_logger, "InsertApprendistato", e);
				}
				// *****************
			}
		}
		return ProcessorsUtils.createResponse(prc, className, null, null, warnings, null);
	}

	private String getValori(Map rec) throws Exception {
		String values = "";
		Warning w = null;

		// il munero di mesi dell'apprendistato non sono più utilizzati

		/*
		 * if (controlla("NUMMESIAPPRENDISTATO",rec)){ String numMesiApprendistato= rec.get("NUMMESIAPPRENDISTATO") !=
		 * null ? ((BigDecimal)rec.get("NUMMESIAPPRENDISTATO")).toString():""; if (numMesiApprendistato!=null &&
		 * !numMesiApprendistato.equals("")){ values = numMesiApprendistato + ","; } } else if(
		 * rec.get("CODTIPOMOV").toString().equalsIgnoreCase("AVV") ) {
		 * if(rec.get("CONTEXT").toString().equalsIgnoreCase("valida") ||
		 * rec.get("CONTEXT").toString().equalsIgnoreCase("validaArchivio") ||
		 * rec.get("CONTEXT").toString().equalsIgnoreCase("validazioneMassiva") ) { w = new
		 * Warning(MessageCodes.ImportMov.ERR_NO_NUMMESI_APP, ""); warnings.add(w); } else { throw new
		 * Exception("NUMMESI"); } }
		 */

		if (controlla("STRCOGNOMETUTORE", rec)) {
			String strCognomeTutore = (String) rec.get("STRCOGNOMETUTORE");
			if (strCognomeTutore != null && !strCognomeTutore.equals("")) {
				values += "'" + StringUtils.formatValue4Sql(strCognomeTutore) + "',";
			}
		} else {
			// throw new Exception("COGNOME");
			w = new Warning(MessageCodes.ImportMov.ERR_NO_COGNOME_APP, "");
			warnings.add(w);
		}
		if (controlla("STRNOMETUTORE", rec)) {
			String strNomeTutore = (String) rec.get("STRNOMETUTORE");
			if (strNomeTutore != null && !strNomeTutore.equals("")) {
				values += "'" + StringUtils.formatValue4Sql(strNomeTutore) + "',";
			}
		} else {
			// throw new Exception("NOME");
			w = new Warning(MessageCodes.ImportMov.ERR_NO_NOME_APP, "");
			warnings.add(w);
		}
		if (controlla("STRCODICEFISCALETUTORE", rec)) {
			String strCodiceFiscaleTutore = (String) rec.get("STRCODICEFISCALETUTORE");
			if (strCodiceFiscaleTutore != null && !strCodiceFiscaleTutore.equals("")) {
				values += "'" + strCodiceFiscaleTutore + "',";
			}
		} else {
			// throw new Exception("CODFISCALE");
			w = new Warning(MessageCodes.ImportMov.ERR_NO_CF_APP, "");
			warnings.add(w);
		}
		if (controlla("CODQUALIFICASRQ", rec)) {
			String codQualificaSrq = (String) rec.get("CODQUALIFICASRQ");
			if (codQualificaSrq != null && !codQualificaSrq.equals("")) {
				values += "'" + codQualificaSrq + "',";
			}
		} // else {
			// throw new Exception("CODQUALIFICASRQ");
			// w = new Warning(MessageCodes.ImportMov.ERR_NO_QUALIFICASRQ_APP, "");
			// warnings.add(w);
			// }
		if (controlla("FLGTITOLARETUTORE", rec)) {
			String flgTitolareTutore = (String) rec.get("FLGTITOLARETUTORE");
			if (flgTitolareTutore != null && !flgTitolareTutore.equals("")) {
				values += "'" + flgTitolareTutore + "',";
			}
		}
		if (controlla("CODMANSIONETUTORE", rec)) {
			String codMansione = (String) rec.get("CODMANSIONETUTORE");
			if (codMansione != null && !codMansione.equals("")) {
				values += "'" + codMansione + "',";
			}
		}
		if (controlla("STRLIVELLOTUTORE", rec)) {
			String strLivelloTutore = (String) rec.get("STRLIVELLOTUTORE");
			if (strLivelloTutore != null && !strLivelloTutore.equals("")) {
				values += "'" + StringUtils.formatValue4Sql(strLivelloTutore) + "',";
			}
		}
		if (controlla("NUMANNIESPTUTORE", rec)) {
			String numAnniEspTutore = ((BigDecimal) rec.get("NUMANNIESPTUTORE")).toString();
			if (numAnniEspTutore != null && !numAnniEspTutore.equals("")) {
				values += numAnniEspTutore + ",";
			}
		}
		if (controlla("FLGARTIGIANA", rec)) {
			String flgArtigiana = (String) rec.get("FLGARTIGIANA");
			if (flgArtigiana != null && !flgArtigiana.equals("")) {
				values += "'" + flgArtigiana + "',";
			}
		}
		if (controlla("STRNOTE", rec)) {
			String strNote = (String) rec.get("STRNOTE");
			if (strNote != null && !strNote.equals("")) {
				values += "'" + StringUtils.formatValue4Sql(strNote) + "',";
			}
		}
		values += userId + ", SYSDATE, " + userId + " ,SYSDATE";
		values += ")";

		return values;
	}

	private String getColonne(Map rec) {
		String columns = "(PRGMOVIMENTO";

		// il munero di mesi dell'apprendistato non sono più utilizzati
		/*
		 * if (controlla("NUMMESIAPPRENDISTATO",rec)){ columns += ",NUMMESIAPPRENDISTATO"; }
		 */
		if (controlla("STRCOGNOMETUTORE", rec)) {
			columns += ",STRCOGNOMETUTORE";
		}
		if (controlla("STRNOMETUTORE", rec)) {
			columns += ",STRNOMETUTORE";
		}
		if (controlla("STRCODICEFISCALETUTORE", rec)) {
			columns += ",STRCODICEFISCALETUTORE";
		}
		// il campo qualifica srq deve far parte della sezione dell'apprendistato
		if (controlla("CODQUALIFICASRQ", rec)) {
			columns += ",CODQUALIFICASRQ";
		}
		if (controlla("FLGTITOLARETUTORE", rec)) {
			columns += ",FLGTITOLARETUTORE";
		}
		if (controlla("CODMANSIONETUTORE", rec)) {
			columns += ",CODMANSIONETUTORE";
		}
		if (controlla("STRLIVELLOTUTORE", rec)) {
			columns += ",STRLIVELLOTUTORE";
		}
		if (controlla("NUMANNIESPTUTORE", rec)) {
			columns += ",NUMANNIESPTUTORE";
		}
		if (controlla("FLGARTIGIANA", rec)) {
			columns += ",FLGARTIGIANA";
		}
		if (controlla("STRNOTE", rec)) {
			columns += ",STRNOTE";
		}
		columns += ",CDNUTINS, DTMINS, CDNUTMOD, DTMMOD)";

		return columns;
	}

	private boolean controlla(String campo, Map rec) {
		boolean v = false;
		if ((rec.get(campo) != null) && (!"".equals(rec.get(campo))))
			v = true;
		return v;
	}
}