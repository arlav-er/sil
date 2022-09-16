package it.eng.sil.module.movimenti.processors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.RecordProcessor;

public class InsertTirocinio implements RecordProcessor {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InsertTirocinio.class.getName());
	private String className;
	private String prc;
	// PrgMovimento appena inserito
	private String keyTable;
	private BigDecimal userId;
	private TransactionQueryExecutor trans;
	private ArrayList warnings = null;
	private String dataTirocinio = "";
	private SourceBean sbInfoGenerale = null;

	public InsertTirocinio(BigDecimal user, TransactionQueryExecutor transexec, SourceBean sbGenerale) {
		className = this.getClass().getName();
		prc = "Inserisci tirocinio";
		userId = user;
		trans = transexec;
		sbInfoGenerale = sbGenerale;
	}

	public SourceBean processRecord(Map record) throws SourceBeanException {
		String columns = "";
		String values = "";
		warnings = new ArrayList();
		if (record == null) {
			return ProcessorsUtils.createResponse(prc, className, new Integer(MessageCodes.ImportMov.ERR_NULL_RECORD),
					null, warnings, null);
		}

		dataTirocinio = sbInfoGenerale.getAttribute("DATTIROCINIO") != null
				? sbInfoGenerale.getAttribute("DATTIROCINIO").toString()
				: "";
		String codTipoAss = record.get("CODTIPOASS") != null ? record.get("CODTIPOASS").toString() : "";
		// Controllo se si tratta di un tirocinio
		try {
			if (record.get("CODMONOTIPO") != null && record.get("CODMONOTIPO").toString().equalsIgnoreCase("T")
					&& (!dataTirocinio.equals("")) && (DateUtils.compare(DateUtils.getNow(), dataTirocinio) >= 0)
					&& (!codTipoAss.equalsIgnoreCase("NB5")) && (!codTipoAss.equalsIgnoreCase("C.03.00"))
					&& (!codTipoAss.equalsIgnoreCase("C.04.00"))) {
				keyTable = ((BigDecimal) record.get("PRGMOVIMENTO")).toString();
				if (keyTable != null) {
					try {
						columns = getColonne(record);
						// Costruzione dei valori da inserire nel DB
						values = "(" + keyTable + ",";
						values = values + getValori(record);
					} catch (Exception e) {
						return ProcessorsUtils.createResponse(prc, className,
								new Integer(MessageCodes.ImportMov.ERR_INSERT_TIROCINIO), null, warnings, null);
					}

					try {
						// Creo la query
						String statement = "INSERT INTO AM_MOVIMENTO_APPRENDIST " + columns + " VALUES " + values;
						// Ne faccio il tracing
						_logger.debug(className + "::processRecord(): " + statement);

						// Inserisco il record
						Object result = null;
						try {
							result = trans.executeQueryByStringStatement(statement, null,
									TransactionQueryExecutor.INSERT);
						} catch (EMFInternalError emfie) {
							// Se ho avuto problemi nella query lo segnalo
							_logger.debug(emfie.getMessage());

							return ProcessorsUtils.createResponse(prc, className,
									new Integer(MessageCodes.ImportMov.ERR_INSERT_TIROCINIO),
									"Impossibile inserire il record in: AM_MOVIMENTO_APPRENDIST.", warnings, null);
						}

						// Se ho un'eccezione nel risultato lo segnalo
						if (result instanceof Exception) {
							// Se ho avuto problemi nella query lo segnalo
							_logger.debug(result.toString());

							return ProcessorsUtils.createResponse(prc, className,
									new Integer(MessageCodes.ImportMov.ERR_INSERT_TIROCINIO),
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
									new Integer(MessageCodes.ImportMov.ERR_INSERT_TIROCINIO),
									"Impossibile inserire il record in: AM_MOVIMENTO_APPRENDIST. ", warnings, null);
						}
					} catch (Exception e) {
						it.eng.sil.util.TraceWrapper.debug(_logger, "InsertTirocinio", e);

					}
				}
			}

			else {
				record.remove("CODTIPOENTEPROMOTORE");
			}

		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "InsertTirocinio", e);

		}
		return ProcessorsUtils.createResponse(prc, className, null, null, warnings, null);
	}

	private String getValori(Map rec) throws Exception {
		String values = "";
		Warning w = null;

		// Decreto 2014: gestione C.04.00 come tirocinio, ma senza i controlli del tirocinio
		String codTipoContratto = (String) rec.get("CODTIPOASS");
		if (codTipoContratto != null && !"C.01.00".equalsIgnoreCase(codTipoContratto)) {
			values += userId + ", SYSDATE, " + userId + " ,SYSDATE";
			values += ")";
			return values;
		}

		if (controlla("STRCOGNOMETUTORE", rec)) {
			String strCognomeTutore = (String) rec.get("STRCOGNOMETUTORE");
			if (strCognomeTutore != null && !strCognomeTutore.equals("")) {
				values += "'" + StringUtils.formatValue4Sql(strCognomeTutore) + "',";
			}
		} else {
			w = new Warning(MessageCodes.ImportMov.ERR_NO_COGNOME_APP, "");
			warnings.add(w);
		}
		if (controlla("STRNOMETUTORE", rec)) {
			String strNomeTutore = (String) rec.get("STRNOMETUTORE");
			if (strNomeTutore != null && !strNomeTutore.equals("")) {
				values += "'" + StringUtils.formatValue4Sql(strNomeTutore) + "',";
			}
		} else {
			w = new Warning(MessageCodes.ImportMov.ERR_NO_NOME_APP, "");
			warnings.add(w);
		}
		if (controlla("STRCODICEFISCALETUTORE", rec)) {
			String strCodiceFiscaleTutore = (String) rec.get("STRCODICEFISCALETUTORE");
			if (strCodiceFiscaleTutore != null && !strCodiceFiscaleTutore.equals("")) {
				values += "'" + strCodiceFiscaleTutore + "',";
			}
		} else {
			w = new Warning(MessageCodes.ImportMov.ERR_NO_CF_APP, "");
			warnings.add(w);
		}

		if (controlla("CODQUALIFICASRQ", rec)) {
			String codQualificaSrq = (String) rec.get("CODQUALIFICASRQ");
			if (codQualificaSrq != null && !codQualificaSrq.equals("")) {
				values += "'" + codQualificaSrq + "',";
			}
		} else {

			// il warning è da visualizzare solo in RER
			if (isRER()) {
				w = new Warning(MessageCodes.ImportMov.ERR_CODQUALIFICASRQ_ASSENTE, "");
				warnings.add(w);
			}
		}

		if (controlla("STRCODFISCPROMOTORETIR", rec)) {
			String strCodFiscPromotoreTir = (String) rec.get("STRCODFISCPROMOTORETIR");
			if (strCodFiscPromotoreTir != null && !strCodFiscPromotoreTir.equals("")) {
				values += "'" + strCodFiscPromotoreTir + "',";
			}
		} else {
			w = new Warning(MessageCodes.ImportMov.WAR_STRCODFISCPROMOTORETIR_ASSENTE, "");
			warnings.add(w);
		}

		// decreto 2014 STRDENOMINAZIONETIR CODCATEGORIATIR CODTIPOLOGIATIR

		if (controlla("STRDENOMINAZIONETIR", rec)) {
			String strDenominazioneTir = (String) rec.get("STRDENOMINAZIONETIR");
			if (strDenominazioneTir != null && !strDenominazioneTir.equals("")) {
				values += "'" + StringUtils.formatValue4Sql(strDenominazioneTir) + "',";
			}
		} else {
			// nessun warning giusto
		}

		if (controlla("CODCATEGORIATIR", rec)) {
			String codCategoriaTir = (String) rec.get("CODCATEGORIATIR");
			if (codCategoriaTir != null && !codCategoriaTir.equals("")) {
				values += "'" + StringUtils.formatValue4Sql(codCategoriaTir) + "',";
			}
		} else {
			// nessun warning giusto
		}

		if (controlla("CODTIPOLOGIATIR", rec)) {
			String codTipologiaTir = (String) rec.get("CODTIPOLOGIATIR");
			if (codTipologiaTir != null && !codTipologiaTir.equals("")) {
				values += "'" + StringUtils.formatValue4Sql(codTipologiaTir) + "',";
			}
		} else {
			// nessun warning giusto
		}

		values += userId + ", SYSDATE, " + userId + " ,SYSDATE";
		values += ")";
		return values;
	}

	private String getColonne(Map rec) {

		String columns = "(PRGMOVIMENTO";

		// Decreto 2014: gestione C.04.00 come tirocinio, ma senza i controlli del tirocinio
		String codTipoContratto = (String) rec.get("CODTIPOASS");
		if (codTipoContratto != null && !"C.01.00".equalsIgnoreCase(codTipoContratto)) {
			columns += ",CDNUTINS, DTMINS, CDNUTMOD, DTMMOD";
			columns += ")";
			return columns;
		}

		if (controlla("STRCOGNOMETUTORE", rec)) {
			columns += ",STRCOGNOMETUTORE";
		}
		if (controlla("STRNOMETUTORE", rec)) {
			columns += ",STRNOMETUTORE";
		}
		if (controlla("STRCODICEFISCALETUTORE", rec)) {
			columns += ",STRCODICEFISCALETUTORE";
		}
		if (controlla("CODQUALIFICASRQ", rec)) {
			columns += ",CODQUALIFICASRQ";
		}
		if (controlla("STRCODFISCPROMOTORETIR", rec)) {
			columns += ",STRCODFISCPROMOTORETIR";
		}
		// decreto 2014
		if (controlla("STRDENOMINAZIONETIR", rec)) {
			columns += ",STRDENOMINAZIONETIR";
		}
		if (controlla("CODCATEGORIATIR", rec)) {
			columns += ",CODCATEGORIATIR";
		}
		if (controlla("CODTIPOLOGIATIR", rec)) {
			columns += ",CODTIPOLOGIATIR";
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

	// introdotto il 17/12/2013 su indicazione di Anna Paola
	private boolean isRER() {

		boolean isRER = false;

		try {
			SourceBean row = (SourceBean) trans.executeQuery("ST_GET_CONFIG_TIROCINIO", null, "SELECT");
			if (row != null && row.containsAttribute("ROW.NUM")) {
				String config = row.getAttribute("ROW.NUM").toString();
				if (config != null && "1".equalsIgnoreCase(config)) {
					isRER = true;
				}
			}
		} catch (EMFInternalError e) {
			_logger.error(e);
			isRER = false;
		}

		return isRER;

	}
}