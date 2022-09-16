package it.eng.sil.module.movimenti.processors;

import java.util.ArrayList;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.module.movimenti.constant.Properties;

public class ControlliRettifica implements RecordProcessor {

	private String name;
	private String classname = this.getClass().getName();
	private TransactionQueryExecutor trans;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ControlliRettifica.class.getName());

	public ControlliRettifica(String name, TransactionQueryExecutor transexec) {
		this.name = name;
		trans = transexec;
	}

	public SourceBean processRecord(Map record) throws SourceBeanException {
		ArrayList warnings = new ArrayList();
		ArrayList nested = new ArrayList();
		String selectquery = "";
		boolean exitLav = false;
		boolean exitAz = false;
		SourceBean result = null;
		if (record == null) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
					"Record da elaborare nullo.", warnings, nested);
		}

		String codTipoComunic = (String) record.get("CODTIPOCOMUNIC");
		if (codTipoComunic != null && codTipoComunic.equals(MessageCodes.General.RETTIFICA_COMUNICAZIONE_PREC)) {
			String codcomdom = (String) record.get("CODCOMDOM");
			String codcpilav = (String) record.get("CODCPILAV");

			// Gestione configurazione regionale o provinciale
			UtilsConfig utility = new UtilsConfig("MOV_REG");
			String configRegProv = utility.getConfigurazioneDefault_Custom();
			// configurazione provinciale (controlla comune lavoratore e comune sede lavoro all'interno della provincia)
			// configurazione regionale (controlla comune lavoratore e comune sede lavoro all'interno della regione)
			if (configRegProv.equals(Properties.DEFAULT_CONFIG)) {

				String selectqueryProv = "select de_comune.codprovincia from de_comune inner join ts_generale "
						+ " on (de_comune.codprovincia = ts_generale.codprovinciasil) ";
				String codProv = null;
				String codProvAz = null;

				if (codcomdom == null || codcomdom.equals("")) {
					if (codcpilav != null) {
						selectquery = "SELECT CODCOM FROM DE_CPI WHERE CODCPI='" + codcpilav + "'";
						result = null;
						try {
							result = ProcessorsUtils.executeSelectQuery(selectquery, trans);
							if (result != null) {
								codcomdom = (String) result.getAttribute("ROW.CODCOM");
							}
						} catch (Exception e) {
							warnings.add(new Warning(MessageCodes.ImportMov.WAR_FIND_COMDOMLAV, ""));
							return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
						}
					}
				}

				if (codcomdom != null && !codcomdom.equals("")) {
					codcomdom = codcomdom.toUpperCase();
					selectquery = selectqueryProv + " where de_comune.codcom = '" + codcomdom + "'";
					result = null;
					try {
						result = ProcessorsUtils.executeSelectQuery(selectquery, trans);
						if (result != null) {
							codProv = (String) result.getAttribute("ROW.CODPROVINCIA");
							if (codProv == null) {
								exitLav = true;
							}
						}
					} catch (Exception e) {
						warnings.add(new Warning(MessageCodes.ImportMov.WAR_FIND_COMDOMLAV, ""));
						return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
					}
				}

				if (exitLav) {
					String codComUnitaAz = (String) record.get("CODUACOM");
					if (codComUnitaAz != null && !codComUnitaAz.equals("")) {
						codComUnitaAz = codComUnitaAz.toUpperCase();
						selectquery = selectqueryProv + " where de_comune.codcom = '" + codComUnitaAz + "'";
						result = null;
						try {
							result = ProcessorsUtils.executeSelectQuery(selectquery, trans);
							if (result != null) {
								codProvAz = (String) result.getAttribute("ROW.CODPROVINCIA");
								if (codProvAz == null) {
									exitAz = true;
								}
							}
						} catch (Exception e) {
							warnings.add(new Warning(MessageCodes.ImportMov.ERR_FIND_CODUAZCOM, ""));
							return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
						}
					}
				}
			} else {
				String codRegioneLav = null;
				String codRegioneAz = null;

				String selectqueryRegione = "select de_provincia.codregione from de_comune "
						+ " inner join de_provincia on (de_comune.codprovincia = de_provincia.codprovincia) "
						+ " inner join ts_generale on (de_provincia.codregione = ts_generale.codregionesil) ";

				if (codcomdom == null || codcomdom.equals("")) {
					if (codcpilav != null) {
						selectquery = "SELECT CODCOM FROM DE_CPI WHERE CODCPI='" + codcpilav + "'";
						result = null;
						try {
							result = ProcessorsUtils.executeSelectQuery(selectquery, trans);
							if (result != null) {
								codcomdom = (String) result.getAttribute("ROW.CODCOM");
							}
						} catch (Exception e) {
							warnings.add(new Warning(MessageCodes.ImportMov.WAR_FIND_COMDOMLAV, ""));
							return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
						}
					}
				}

				if (codcomdom != null && !codcomdom.equals("")) {
					codcomdom = codcomdom.toUpperCase();
					selectquery = selectqueryRegione + " where de_comune.codcom = '" + codcomdom + "'";
					result = null;
					try {
						result = ProcessorsUtils.executeSelectQuery(selectquery, trans);
						if (result != null) {
							codRegioneLav = (String) result.getAttribute("ROW.codregione");
							if (codRegioneLav == null) {
								exitLav = true;
							}
						}
					} catch (Exception e) {
						warnings.add(new Warning(MessageCodes.ImportMov.WAR_FIND_COMDOMLAV, ""));
						return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
					}
				}

				if (exitLav) {
					String codComUnitaAz = (String) record.get("CODUACOM");
					if (codComUnitaAz != null && !codComUnitaAz.equals("")) {
						codComUnitaAz = codComUnitaAz.toUpperCase();
						selectquery = selectqueryRegione + " where de_comune.codcom = '" + codComUnitaAz + "'";
						result = null;
						try {
							result = ProcessorsUtils.executeSelectQuery(selectquery, trans);
							if (result != null) {
								codRegioneAz = (String) result.getAttribute("ROW.codregione");
								if (codRegioneAz == null) {
									exitAz = true;
								}
							}
						} catch (Exception e) {
							warnings.add(new Warning(MessageCodes.ImportMov.ERR_FIND_CODUAZCOM, ""));
							return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
						}
					}
				}
			}

			boolean exitRettificaCO = true;
			if (record.get("RETTIFICAEFFETTUATA") != null
					&& record.get("RETTIFICAEFFETTUATA").toString().equalsIgnoreCase("1")) {
				exitRettificaCO = false;
			}
			if (exitLav && exitAz && exitRettificaCO) {
				return cancellaRettifica(record, warnings, nested);
			} else {
				if ((warnings.size() > 0) || (nested.size() > 0)) {
					return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
				} else {
					return null;
				}
			}
		} else {
			return null;
		}
	}

	public SourceBean cancellaRettifica(Map record, ArrayList warnings, ArrayList nested) throws SourceBeanException {
		String contesto = record.get("CONTEXT") != null ? record.get("CONTEXT").toString() : "";
		String nomeTabella = "AM_MOVIMENTO_APPOGGIO";
		String tabellaAgevolazioni = "AM_MOV_AGEV_APP";
		if (contesto.equalsIgnoreCase("validazioneMassiva") || contesto.equalsIgnoreCase("valida")
				|| contesto.equalsIgnoreCase("validaArchivio")) {
			if (contesto.equalsIgnoreCase("validaArchivio")) {
				nomeTabella = "AM_MOV_APP_ARCHIVIO";
				tabellaAgevolazioni = "AM_MOV_AGEV_APP_ARCHIVIO";
			}
			Object resultDel = null;
			Object prgMovApp = record.get("PRGMOVIMENTOAPP");
			Object prgMovAppCVE = record.get("PRGMOVIMENTOAPPCVE");

			try {
				String deletequery = "DELETE FROM " + tabellaAgevolazioni + " WHERE PRGMOVIMENTOAPP = " + prgMovApp;
				resultDel = trans.executeQueryByStringStatement(deletequery, null, TransactionQueryExecutor.DELETE);
				if (!((resultDel instanceof Boolean) && (((Boolean) resultDel).booleanValue() == true))) {
					it.eng.sil.util.TraceWrapper.debug(_logger,
							"ControlloPermessi::processRecord(): query di rimozione del movimento fallita!",
							(Exception) resultDel);
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_DELETE_MOV_APP),
							"Impossibile rimuovere i movimenti di rettifica di cui non si ha competenza.", warnings,
							nested);
				}

				deletequery = "DELETE FROM " + nomeTabella + " WHERE PRGMOVIMENTOAPP =" + prgMovApp;
				resultDel = trans.executeQueryByStringStatement(deletequery, null, TransactionQueryExecutor.DELETE);
				if (!((resultDel instanceof Boolean) && (((Boolean) resultDel).booleanValue() == true))) {
					it.eng.sil.util.TraceWrapper.debug(_logger,
							"ControlloPermessi::processRecord(): query di rimozione del movimento fallita!",
							(Exception) resultDel);
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_DELETE_MOV_APP),
							"Impossibile rimuovere i movimenti di rettifica di cui non si ha competenza.", warnings,
							nested);
				}
				if (prgMovAppCVE != null) {
					deletequery = "DELETE FROM " + tabellaAgevolazioni + " WHERE PRGMOVIMENTOAPP = " + prgMovAppCVE;
					resultDel = trans.executeQueryByStringStatement(deletequery, null, TransactionQueryExecutor.DELETE);
					if (!((resultDel instanceof Boolean) && (((Boolean) resultDel).booleanValue() == true))) {
						it.eng.sil.util.TraceWrapper.debug(_logger,
								"ControlloPermessi::processRecord(): query di rimozione del movimento fallita!",
								(Exception) resultDel);
						return ProcessorsUtils.createResponse(name, classname,
								new Integer(MessageCodes.ImportMov.ERR_DELETE_MOV_APP),
								"Impossibile rimuovere i movimenti di rettifica di cui non si ha competenza.", warnings,
								nested);
					}

					deletequery = "DELETE FROM " + nomeTabella + " WHERE PRGMOVIMENTOAPP = " + prgMovAppCVE;
					resultDel = trans.executeQueryByStringStatement(deletequery, null, TransactionQueryExecutor.DELETE);
					if (!((resultDel instanceof Boolean) && (((Boolean) resultDel).booleanValue() == true))) {
						it.eng.sil.util.TraceWrapper.debug(_logger,
								"ControlloPermessi::processRecord(): query di rimozione del movimento fallita!",
								(Exception) resultDel);
						return ProcessorsUtils.createResponse(name, classname,
								new Integer(MessageCodes.ImportMov.ERR_DELETE_MOV_APP),
								"Impossibile rimuovere i movimenti di rettifica di cui non si ha competenza.", warnings,
								nested);
					}
				}

				warnings.add(new Warning(MessageCodes.ImportMov.WAR_CANC_RETTIFICA_NO_COMPETENZA, ""));

				if (contesto.equalsIgnoreCase("valida") || contesto.equalsIgnoreCase("validaArchivio")) {
					// Dopo aver cancellato la rettifica, visualizziamo il primo movimento rettificato,
					// impostato in ControllaTipoComunicazione
					if (record.containsKey("PRGMOVRETTCOMPETENZA")) {
						record.put("PRGMOVIMENTO", record.get("PRGMOVRETTCOMPETENZA"));
					}
				}
				return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested, true);

			} catch (EMFInternalError error) {
				it.eng.sil.util.TraceWrapper.debug(_logger,
						"ControlloPermessi::processRecord(): query di rimozione del movimento fallita!",
						(Exception) error);
				return ProcessorsUtils.createResponse(name, classname,
						new Integer(MessageCodes.ImportMov.ERR_DELETE_MOV_APP),
						"Impossibile rimuovere i movimenti di rettifica di cui non si ha competenza.", warnings,
						nested);
			}
		} else {
			return null;
		}
	}

}