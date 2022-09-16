package it.eng.sil.module.anag.profiloLavoratore.bean;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.std.CompositeDataResult;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.module.anag.profiloLavoratore.Decodifica;
import it.eng.sil.module.voucher.Properties;

@SuppressWarnings("unchecked")
public class ProfiloLavoratore {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ProfiloLavoratore.class.getName());

	private BigDecimal prgLavoratoreProfilo = null;
	private BigDecimal cdnLavoratore = null;
	private String datCreazioneProfilo = null;
	private Float numValoreProfilo = null;
	private String codVchProfiling = null;
	private String codMonoStatoProf = null;
	private String codCpi = null;
	private String flgConoscenzaIta = null;
	private BigDecimal cdnUtIns = null;
	private BigDecimal cdnUtMod = null;
	private BigDecimal numKloLavProfilo = null;
	private TransactionQueryExecutor transactionQueryExecutor;

	public ProfiloLavoratore(TransactionQueryExecutor transExec, BigDecimal progressivo, BigDecimal cdnlavoratore,
			String codiceCpi, BigDecimal cdnut) {
		this.transactionQueryExecutor = transExec;
		this.prgLavoratoreProfilo = progressivo;
		this.cdnLavoratore = cdnlavoratore;
		this.codCpi = codiceCpi;
		this.cdnUtIns = cdnut;
		this.cdnUtMod = cdnut;
	}

	public ProfiloLavoratore(BigDecimal progressivo, BigDecimal cdnlavoratore, String codiceCpi, BigDecimal cdnut) {
		this.prgLavoratoreProfilo = progressivo;
		this.cdnLavoratore = cdnlavoratore;
		this.codCpi = codiceCpi;
		this.cdnUtIns = cdnut;
		this.cdnUtMod = cdnut;
	}

	public ProfiloLavoratore(BigDecimal cdnlavoratore, String codiceCpi, BigDecimal cdnut) {
		this.cdnLavoratore = cdnlavoratore;
		this.codCpi = codiceCpi;
		this.cdnUtIns = cdnut;
		this.cdnUtMod = cdnut;
		this.codMonoStatoProf = Decodifica.StatoProfilo.IN_CORSO;
	}

	public ProfiloLavoratore(TransactionQueryExecutor transExec, BigDecimal cdnlavoratore, String codiceCpi,
			BigDecimal cdnut) {
		this.transactionQueryExecutor = transExec;
		this.cdnLavoratore = cdnlavoratore;
		this.codCpi = codiceCpi;
		this.cdnUtIns = cdnut;
		this.cdnUtMod = cdnut;
		this.codMonoStatoProf = Decodifica.StatoProfilo.IN_CORSO;
	}

	public TransactionQueryExecutor getTransactionQueryExecutor() {
		return transactionQueryExecutor;
	}

	public BigDecimal getPrgLavoratoreProfilo() {
		return prgLavoratoreProfilo;
	}

	public BigDecimal getCdnLavoratore() {
		return cdnLavoratore;
	}

	public String getDatCreazioneProfilo() {
		return datCreazioneProfilo;
	}

	public Float getNumValoreProfilo() {
		return numValoreProfilo;
	}

	public String getCodVchProfiling() {
		return codVchProfiling;
	}

	public String getCodMonoStatoProf() {
		return codMonoStatoProf;
	}

	public String getCodCpi() {
		return codCpi;
	}

	public BigDecimal getCdnUtIns() {
		return cdnUtIns;
	}

	public BigDecimal getCdnUtMod() {
		return cdnUtMod;
	}

	public BigDecimal getNumKloLavProfilo() {
		return numKloLavProfilo;
	}

	public void setPrgLavoratoreProfilo(BigDecimal val) {
		this.prgLavoratoreProfilo = val;
	}

	public void setCdnLavoratore(BigDecimal val) {
		this.cdnLavoratore = val;
	}

	public void setDatCreazioneProfilo(String val) {
		this.datCreazioneProfilo = val;
	}

	public void setNumValoreProfilo(Float val) {
		this.numValoreProfilo = val;
	}

	public void setCodVchProfiling(String val) {
		this.codVchProfiling = val;
	}

	public void setCodMonoStatoProf(String val) {
		this.codMonoStatoProf = val;
	}

	public void setCodCpi(String val) {
		this.codCpi = val;
	}

	public void setCdnUtIns(BigDecimal val) {
		this.cdnUtIns = val;
	}

	public void setCdnUtMod(BigDecimal val) {
		this.cdnUtMod = val;
	}

	public void setNumKloLavProfilo(BigDecimal val) {
		this.numKloLavProfilo = val;
	}

	public void setTransactionQueryExecutor(TransactionQueryExecutor val) {
		this.transactionQueryExecutor = val;
	}

	public Vector<SourceBean> caricaProfilo(int linguetta) throws EMFInternalError {
		Vector<SourceBean> profilo = null;
		Object params[] = new Object[2];
		params[0] = getPrgLavoratoreProfilo();
		params[1] = linguetta;
		if (getTransactionQueryExecutor() != null) {
			SourceBean res = (SourceBean) getTransactionQueryExecutor()
					.executeQuery("GET_PROFILO_DOMANDA_LAVORATORE_LINGUETTA", params, "SELECT");
			if (res != null) {
				profilo = res.getAttributeAsVector("ROW");
			}
			return profilo;
		} else {
			SourceBean res = (SourceBean) QueryExecutor.executeQuery("GET_PROFILO_DOMANDA_LAVORATORE_LINGUETTA", params,
					"SELECT", Values.DB_SIL_DATI);
			if (res != null) {
				profilo = res.getAttributeAsVector("ROW");
			}
			return profilo;
		}
	}

	public SourceBean caricaInfoProfilo() throws EMFInternalError, Exception {
		Object params[] = new Object[1];
		params[0] = getPrgLavoratoreProfilo();
		if (getTransactionQueryExecutor() != null) {
			SourceBean res = (SourceBean) getTransactionQueryExecutor()
					.executeQuery("GET_INFO_DETTAGLIO_PROFILO_LAVORATORE", params, "SELECT");
			if (res == null) {
				throw new Exception("Impossibile leggere il profilo lavoratore");
			}
			res = res.containsAttribute("ROW") ? (SourceBean) res.getAttribute("ROW") : res;
			return res;
		} else {
			SourceBean res = (SourceBean) QueryExecutor.executeQuery("GET_INFO_DETTAGLIO_PROFILO_LAVORATORE", params,
					"SELECT", Values.DB_SIL_DATI);
			if (res == null) {
				throw new Exception("Impossibile leggere il profilo lavoratore");
			}
			res = res.containsAttribute("ROW") ? (SourceBean) res.getAttribute("ROW") : res;
			return res;
		}
	}

	public boolean salvaProfilo(SourceBean origin, int linguetta) {
		DomandeProfilo domande = new DomandeProfilo(linguetta);
		Vector<SourceBean> rowsDomande = domande.caricaDomande();
		Vector<SourceBean> profilo = null;
		if (rowsDomande != null && rowsDomande.size() > 0) {
			boolean gestisciTransazione = false;
			try {
				if (getTransactionQueryExecutor() == null) {
					gestisciTransazione = true;
					setTransactionQueryExecutor(new TransactionQueryExecutor(Values.DB_SIL_DATI));
					getTransactionQueryExecutor().initTransaction();
				}

				if (getPrgLavoratoreProfilo() != null) {
					// gestione aggiornamento profilo
					profilo = caricaProfilo(linguetta);
					if (profilo != null) {
						loadProfilo();
						aggiornaProfilo();
						int numeroRisposteProfilo = profilo.size();
						for (int i = 0; i < numeroRisposteProfilo; i++) {
							SourceBean rowRisposta = (SourceBean) profilo.get(i);
							DomandaRispostaProfilo risposta = new DomandaRispostaProfilo(rowRisposta);
							boolean resultCanc = risposta.cancella(getTransactionQueryExecutor());
							if (!resultCanc) {
								throw new Exception("Impossibile salvare il profilo lavoratore");
							}
						}
						inserisciRisposte(rowsDomande, origin);
					}

					if (gestisciTransazione) {
						getTransactionQueryExecutor().commitTransaction();
					}
					return true;
				} else {
					// gestione inserimento nuovo profilo
					Boolean res = creaProfiloLavoratore();
					if (res != null && res.booleanValue()) {

						inserisciRisposte(rowsDomande, origin);

						if (gestisciTransazione) {
							getTransactionQueryExecutor().commitTransaction();
						}
						return true;
					} else {
						if (gestisciTransazione) {
							getTransactionQueryExecutor().rollBackTransaction();
						}
						return false;
					}
				}
			} catch (EMFInternalError e) {
				it.eng.sil.util.TraceWrapper.debug(_logger,
						"ProfiloLavoratore::salvaProfilo(): Impossibile creare il profilo!", e);
				if (gestisciTransazione && getTransactionQueryExecutor() != null) {
					try {
						getTransactionQueryExecutor().rollBackTransaction();
					} catch (EMFInternalError e1) {
						it.eng.sil.util.TraceWrapper.debug(_logger,
								"ProfiloLavoratore::salvaProfilo(): Impossibile creare il profilo!", e1);
					}
				}
				return false;
			} catch (Exception ex) {
				it.eng.sil.util.TraceWrapper.debug(_logger,
						"ProfiloLavoratore::salvaProfilo(): Impossibile salvare il profilo!", ex);
				if (gestisciTransazione && getTransactionQueryExecutor() != null) {
					try {
						getTransactionQueryExecutor().rollBackTransaction();
					} catch (EMFInternalError e1) {
						it.eng.sil.util.TraceWrapper.debug(_logger,
								"ProfiloLavoratore::salvaProfilo(): Impossibile salvare il profilo!", e1);
					}
				}
				return false;
			}
		} else {
			return false;
		}
	}

	private void inserisciRisposte(Vector<SourceBean> rowsDomande, SourceBean origin)
			throws EMFInternalError, Exception {
		int numDomandeLinguetta = rowsDomande.size();

		for (int i = 0; i < numDomandeLinguetta; i++) {
			String strOsservazione = null;
			String strRisposta = null;
			Object codRispostaDomandaProf = null;
			boolean inserisciRisposta = true;
			SourceBean domanda = (SourceBean) rowsDomande.get(i);

			// gestione domanda D08
			if (domanda.getAttribute("codDomandaProf").toString().equalsIgnoreCase(Decodifica.DomandaProfilo.D08)) {
				String qualifica = origin.containsAttribute("codmansione")
						? origin.getAttribute("codmansione").toString()
						: "";
				if (!qualifica.equals("")) {
					codRispostaDomandaProf = Decodifica.DomandaProfilo.RD08;
					strRisposta = qualifica;
				} else {
					inserisciRisposta = false;
				}
			} else {
				// gestione domanda text area
				if (domanda.containsAttribute("strTipoInput") && domanda.getAttribute("strTipoInput").toString()
						.equalsIgnoreCase(Decodifica.TipoDomanda.TEXT_AREA)) {
					String contenuto = origin.containsAttribute(domanda.getAttribute("codDomandaProf").toString())
							? origin.getAttribute(domanda.getAttribute("codDomandaProf").toString()).toString()
							: "";
					if (!contenuto.equals("")) {
						codRispostaDomandaProf = domanda.getAttribute("codDomandaProf").toString() + "_1";
						strOsservazione = contenuto;
					} else {
						inserisciRisposta = false;
					}
				} else {
					codRispostaDomandaProf = origin.containsAttribute(domanda.getAttribute("codDomandaProf").toString())
							? origin.getAttribute(domanda.getAttribute("codDomandaProf").toString())
							: null;
					if (codRispostaDomandaProf != null) {
						// gestione domanda altro con input text aggiuntivo
						if (domanda.containsAttribute("strTipoInput") && domanda.getAttribute("strTipoInput").toString()
								.equalsIgnoreCase(Decodifica.TipoDomanda.RADIO_ALTRO)) {
							String nomeCampoAltro = codRispostaDomandaProf.toString() + "_altro";
							if (origin.containsAttribute(nomeCampoAltro)) {
								strOsservazione = origin.getAttribute(nomeCampoAltro).toString();
							}
						} else {
							// gestione domanda D44
							if (domanda.getAttribute("codDomandaProf").toString()
									.equalsIgnoreCase(Decodifica.DomandaProfilo.D44_OLD)) {
								inserisciRisposta = false;
								codRispostaDomandaProf = origin.containsAttribute(Decodifica.DomandaProfilo.D44_OLD)
										? origin.getAttribute(Decodifica.DomandaProfilo.D44_OLD)
										: null;
								if (codRispostaDomandaProf != null) {
									String[] risposteD44 = codRispostaDomandaProf.toString().split(",");
									for (int j = 0; j < risposteD44.length; j++) {
										Object paramsRisposteD44[] = new Object[] { getPrgLavoratoreProfilo(), null,
												risposteD44[j], null, getCdnUtIns(), getCdnUtMod() };
										Boolean resRisposta = (Boolean) getTransactionQueryExecutor().executeQuery(
												"INS_RISPOSTA_PROFILO_LAVORATORE", paramsRisposteD44, "INSERT");
										if (!resRisposta.booleanValue())
											throw new Exception(
													"Impossibile inserire la risposta alla domanda per il profilo lavoratore");
									}
								}
							}
						}
					} else {
						inserisciRisposta = false;
					}
				}
			}

			if (inserisciRisposta) {
				Object paramsRisposte[] = new Object[] { getPrgLavoratoreProfilo(), strRisposta, codRispostaDomandaProf,
						strOsservazione, getCdnUtIns(), getCdnUtMod() };
				Boolean resRisposta = (Boolean) getTransactionQueryExecutor()
						.executeQuery("INS_RISPOSTA_PROFILO_LAVORATORE", paramsRisposte, "INSERT");
				if (!resRisposta.booleanValue())
					throw new Exception("Impossibile inserire la risposta alla domanda per il profilo lavoratore");
			}
		}
	}

	private void aggiornaProfilo() throws EMFInternalError, Exception {
		Object params[] = new Object[] { getNumKloLavProfilo(), getCdnUtMod(), getPrgLavoratoreProfilo() };
		Boolean resUpd = (Boolean) getTransactionQueryExecutor().executeQuery("UPD_INFO_PROFILO_LAVORATORE", params,
				"UPDATE");
		if (!resUpd.booleanValue())
			throw new Exception("Impossibile aggiornare il profilo lavoratore");
	}

	@SuppressWarnings("rawtypes")
	public Integer calcolabilitaProfilo(DataConnection conn) {
		DataResult dr = null;
		StoredProcedureCommand command = null;
		try {

			command = (StoredProcedureCommand) conn
					.createStoredProcedureCommand("{ call ? := PG_PROFILO_LAVORATORE.controllaCalcolabilita(?) }");

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(2);

			// Parametro di Ritorno
			parameters.add(conn.createDataField("esito", Types.INTEGER, null));
			command.setAsOutputParameters(paramIndex++);

			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("prgLavoratorepProfiloVar", Types.BIGINT, getPrgLavoratoreProfilo()));
			command.setAsInputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);
			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
			DataField df = pdr.getPunctualDatafield();
			Integer esito = (Integer) df.getObjectValue();

			return esito;
		} catch (Exception ex) {
			return -1;
		} finally {
			Utils.releaseResources(null, command, dr);
		}
	}

	@SuppressWarnings("rawtypes")
	public HashMap<String, String> calcolaValoreProfilo(DataConnection conn, boolean isUpdate) {
		DataResult dr = null;
		StoredProcedureCommand command = null;
		try {

			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(
					"{ call PG_PROFILO_LAVORATORE.calcolaValoreProfilo(?,?,?,?,?,?,?,?,?,?,?,?,?) }");

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(13);

			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("prgLavoratorepProfiloVar", Types.BIGINT, getPrgLavoratoreProfilo()));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("idUtenteVar", Types.BIGINT, getCdnUtIns()));
			command.setAsInputParameters(paramIndex++);

			String eseguiUpdate = isUpdate ? "S" : "N";
			parameters.add(conn.createDataField("eseguiUpdateVar", Types.VARCHAR, eseguiUpdate));
			command.setAsInputParameters(paramIndex++);

			// Parametri di Ritorno
			parameters.add(conn.createDataField("valoreDimensioneVar", Types.FLOAT, null));
			command.setAsOutputParameters(paramIndex++);

			parameters.add(conn.createDataField("valoreDim1Var", Types.FLOAT, null));
			command.setAsOutputParameters(paramIndex++);

			parameters.add(conn.createDataField("valoreDim2Var", Types.FLOAT, null));
			command.setAsOutputParameters(paramIndex++);

			parameters.add(conn.createDataField("valoreDim3Var", Types.FLOAT, null));
			command.setAsOutputParameters(paramIndex++);

			parameters.add(conn.createDataField("valoreDim4Var", Types.FLOAT, null));
			command.setAsOutputParameters(paramIndex++);

			parameters.add(conn.createDataField("valoreDim5Var", Types.FLOAT, null));
			command.setAsOutputParameters(paramIndex++);

			parameters.add(conn.createDataField("valoreDim6Var", Types.FLOAT, null));
			command.setAsOutputParameters(paramIndex++);

			parameters.add(conn.createDataField("valoreDim7Var", Types.FLOAT, null));
			command.setAsOutputParameters(paramIndex++);

			parameters.add(conn.createDataField("indiceDifficoltaVar", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			parameters.add(conn.createDataField("errCodeOut", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);

			CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
			List outputParams = cdr.getContainedDataResult();
			// Recupero i valori di output della stored

			// 9. errCodeOut
			PunctualDataResult pdr = (PunctualDataResult) outputParams.get(9);
			DataField df = pdr.getPunctualDatafield();
			String errCodeOut = df.getStringValue();

			HashMap<String, String> valoriRitorno = new HashMap<String, String>();

			if (errCodeOut.equalsIgnoreCase("CALCOLO_OK")) {

				// 0. valoreDimensioneVar
				pdr = (PunctualDataResult) outputParams.get(0);
				df = pdr.getPunctualDatafield();
				Double valoreDimensione = (Double) df.getObjectValue();
				// 1. valoreDim1
				pdr = (PunctualDataResult) outputParams.get(1);
				df = pdr.getPunctualDatafield();
				Double valoreDim1 = (Double) df.getObjectValue();
				// 2. valoreDim2
				pdr = (PunctualDataResult) outputParams.get(2);
				df = pdr.getPunctualDatafield();
				Double valoreDim2 = (Double) df.getObjectValue();
				// 3. valoreDim3
				pdr = (PunctualDataResult) outputParams.get(3);
				df = pdr.getPunctualDatafield();
				Double valoreDim3 = (Double) df.getObjectValue();
				// 4. valoreDim4
				pdr = (PunctualDataResult) outputParams.get(4);
				df = pdr.getPunctualDatafield();
				Double valoreDim4 = (Double) df.getObjectValue();
				// 5. valoreDim5
				pdr = (PunctualDataResult) outputParams.get(5);
				df = pdr.getPunctualDatafield();
				Double valoreDim5 = (Double) df.getObjectValue();
				// 6. valoreDim6
				pdr = (PunctualDataResult) outputParams.get(6);
				df = pdr.getPunctualDatafield();
				Double valoreDim6 = (Double) df.getObjectValue();
				// 7. valoreDim7
				pdr = (PunctualDataResult) outputParams.get(7);
				df = pdr.getPunctualDatafield();
				Double valoreDim7 = (Double) df.getObjectValue();
				// 8. indiceDifficolta
				pdr = (PunctualDataResult) outputParams.get(8);
				df = pdr.getPunctualDatafield();
				String indiceDifficolta = df.getStringValue();
				indiceDifficolta = StringUtils.isEmptyNoBlank(indiceDifficolta) ? "" : indiceDifficolta;
				valoriRitorno.put("CALCOLO_OK", "CALCOLO_OK");
				valoriRitorno.put("Numdim01", String.valueOf(valoreDim1));
				valoriRitorno.put("Numdim02", String.valueOf(valoreDim2));
				valoriRitorno.put("Numdim03", String.valueOf(valoreDim3));
				valoriRitorno.put("Numdim04", String.valueOf(valoreDim4));
				valoriRitorno.put("Numdim05", String.valueOf(valoreDim5));
				valoriRitorno.put("Numdim06", String.valueOf(valoreDim6));
				valoriRitorno.put("Numdim07", String.valueOf(valoreDim7));
				valoriRitorno.put("indiceProfilo", indiceDifficolta);
				valoriRitorno.put("NUMVALOREPROFILO", String.valueOf(valoreDimensione));
			} else {
				valoriRitorno.put("CALCOLO_KO", "CALCOLO_KO");
			}

			return valoriRitorno;
		} catch (Exception ex) {
			return null;
		} finally {
			Utils.releaseResources(null, command, dr);
		}
	}

	public Integer controlloCoerenzaRisposte(SourceBean origin) {

		String rispostaD20 = origin.containsAttribute(Decodifica.DomandaProfilo.D20)
				? origin.getAttribute(Decodifica.DomandaProfilo.D20).toString()
				: "";
		String rispostaD21 = origin.containsAttribute(Decodifica.DomandaProfilo.D21)
				? origin.getAttribute(Decodifica.DomandaProfilo.D21).toString()
				: "";
		if (StringUtils.isFilledNoBlank(rispostaD20)
				&& rispostaD20.equalsIgnoreCase(Decodifica.DomandaProfilo.D20_1.toString())
				&& StringUtils.isFilledNoBlank(rispostaD21)) {
			return Properties.ERRORE_21;
		}
		String rispostaD45 = origin.containsAttribute(Decodifica.DomandaProfilo.D45)
				? origin.getAttribute(Decodifica.DomandaProfilo.D45).toString()
				: "";
		if (StringUtils.isFilledNoBlank(rispostaD45)
				&& rispostaD45.equalsIgnoreCase(Decodifica.DomandaProfilo.D45_2.toString())) {
			String rispostaD22 = origin.containsAttribute(Decodifica.DomandaProfilo.D22)
					? origin.getAttribute(Decodifica.DomandaProfilo.D22).toString()
					: "";
			String rispostaD23 = origin.containsAttribute(Decodifica.DomandaProfilo.D23)
					? origin.getAttribute(Decodifica.DomandaProfilo.D23).toString()
					: "";
			String rispostaD24 = origin.containsAttribute(Decodifica.DomandaProfilo.D24)
					? origin.getAttribute(Decodifica.DomandaProfilo.D24).toString()
					: "";
			String rispostaD25 = origin.containsAttribute(Decodifica.DomandaProfilo.D25)
					? origin.getAttribute(Decodifica.DomandaProfilo.D25).toString()
					: "";
			String rispostaD26 = origin.containsAttribute(Decodifica.DomandaProfilo.D26)
					? origin.getAttribute(Decodifica.DomandaProfilo.D26).toString()
					: "";
			String rispostaD27 = origin.containsAttribute(Decodifica.DomandaProfilo.D27)
					? origin.getAttribute(Decodifica.DomandaProfilo.D27).toString()
					: "";
			String rispostaD28 = origin.containsAttribute(Decodifica.DomandaProfilo.D28)
					? origin.getAttribute(Decodifica.DomandaProfilo.D28).toString()
					: "";
			String rispostaD29 = origin.containsAttribute(Decodifica.DomandaProfilo.D29)
					? origin.getAttribute(Decodifica.DomandaProfilo.D29).toString()
					: "";
			if (StringUtils.isFilledNoBlank(rispostaD22) || StringUtils.isFilledNoBlank(rispostaD23)
					|| StringUtils.isFilledNoBlank(rispostaD24) || StringUtils.isFilledNoBlank(rispostaD25)
					|| StringUtils.isFilledNoBlank(rispostaD26) || StringUtils.isFilledNoBlank(rispostaD27)
					|| StringUtils.isFilledNoBlank(rispostaD28) || StringUtils.isFilledNoBlank(rispostaD29)) {
				return Properties.ERRORE_45_INV;
			}
		}

		return 0;
	}

	@SuppressWarnings("rawtypes")
	public HashMap<String, String> calcolaScorePersonalita(DataConnection conn) {
		DataResult dr = null;
		StoredProcedureCommand command = null;
		try {

			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(
					"{ call PG_PROFILO_LAVORATORE.calcolaScorePersonalita(?,?,?,?,?,?,?,?) }");

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(8);

			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("prgLavoratorepProfiloVar", Types.BIGINT, getPrgLavoratoreProfilo()));
			command.setAsInputParameters(paramIndex++);

			// Parametri di Ritorno
			parameters.add(conn.createDataField(Decodifica.ScorePersonalita.COMPLETEZZA_PROFILO, Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			parameters.add(conn.createDataField(Decodifica.ScorePersonalita.AMICALITA, Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			parameters.add(conn.createDataField(Decodifica.ScorePersonalita.COSCIENZOSITA, Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			parameters.add(conn.createDataField(Decodifica.ScorePersonalita.STAB_EMOTIVA, Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			parameters.add(conn.createDataField(Decodifica.ScorePersonalita.EXTRAVERSIONE, Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			parameters.add(conn.createDataField(Decodifica.ScorePersonalita.APERTURA, Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			parameters.add(conn.createDataField("errCodeOut", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);

			CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
			List outputParams = cdr.getContainedDataResult();
			// Recupero i valori di output della stored
			// 0. completezzaProfilo
			PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
			DataField df = pdr.getPunctualDatafield();
			String completezzaProfilo = df.getStringValue();
			// 1. scoreAmicalita
			pdr = (PunctualDataResult) outputParams.get(1);
			df = pdr.getPunctualDatafield();
			String scoreAmicalita = df.getStringValue();
			// 2. scoreCoscienzosita
			pdr = (PunctualDataResult) outputParams.get(2);
			df = pdr.getPunctualDatafield();
			String scoreCoscienzosita = df.getStringValue();
			// 3. scoreStabEmotiva
			pdr = (PunctualDataResult) outputParams.get(3);
			df = pdr.getPunctualDatafield();
			String scoreStabEmotiva = df.getStringValue();
			// 4. scoreExtraVersione
			pdr = (PunctualDataResult) outputParams.get(4);
			df = pdr.getPunctualDatafield();
			String scoreExtraVersione = df.getStringValue();
			// 5. scoreApertura
			pdr = (PunctualDataResult) outputParams.get(5);
			df = pdr.getPunctualDatafield();
			String scoreApertura = df.getStringValue();
			// 6. errCodeOut
			pdr = (PunctualDataResult) outputParams.get(6);
			df = pdr.getPunctualDatafield();
			String errCodeOut = df.getStringValue();

			if (errCodeOut.equalsIgnoreCase("00")) {
				HashMap<String, String> valoriRitorno = new HashMap<String, String>();
				valoriRitorno.put("OK_SCORE_PERS", "OK_SCORE_PERS");
				valoriRitorno.put(Decodifica.ScorePersonalita.COMPLETEZZA_PROFILO, completezzaProfilo);
				valoriRitorno.put(Decodifica.ScorePersonalita.AMICALITA, scoreAmicalita);
				valoriRitorno.put(Decodifica.ScorePersonalita.COSCIENZOSITA, scoreCoscienzosita);
				valoriRitorno.put(Decodifica.ScorePersonalita.STAB_EMOTIVA, scoreStabEmotiva);
				valoriRitorno.put(Decodifica.ScorePersonalita.EXTRAVERSIONE, scoreExtraVersione);
				valoriRitorno.put(Decodifica.ScorePersonalita.APERTURA, scoreApertura);
				return valoriRitorno;
			} else if (errCodeOut.equalsIgnoreCase("40")) {
				HashMap<String, String> valoriRitorno = new HashMap<String, String>();
				valoriRitorno.put("NA_SCORE_PERS", "NA_SCORE_PERS");
				return valoriRitorno;
			}

			return null;
		} catch (Exception ex) {
			return null;
		} finally {
			Utils.releaseResources(null, command, dr);
		}
	}

	@SuppressWarnings("rawtypes")
	public Integer getNumeroRisposteDomande(DataConnection conn, String domandeList) {
		DataResult dr = null;
		StoredProcedureCommand command = null;
		try {

			command = (StoredProcedureCommand) conn
					.createStoredProcedureCommand("{ call ? := PG_PROFILO_LAVORATORE.getCountRisposte(?,?) }");

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(3);

			// Parametro di Ritorno
			parameters.add(conn.createDataField("esito", Types.INTEGER, null));
			command.setAsOutputParameters(paramIndex++);

			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("prgLavoratorepProfiloVar", Types.BIGINT, getPrgLavoratoreProfilo()));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("codiciDomande", Types.VARCHAR, domandeList));
			command.setAsInputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);
			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
			DataField df = pdr.getPunctualDatafield();
			Integer esito = (Integer) df.getObjectValue();

			return esito;
		} catch (Exception ex) {
			return -1;
		} finally {
			Utils.releaseResources(null, command, dr);
		}
	}

	public Boolean creaProfiloLavoratore() throws Exception {
		// gestione inserimento nuovo profilo
		Boolean res = null;
		SourceBean rowKey = (SourceBean) getTransactionQueryExecutor().executeQuery("QUERY_NEXTVAL_PROFILO_LAVORATORE",
				null, "SELECT");
		if (rowKey != null) {
			rowKey = (rowKey.containsAttribute("ROW") ? (SourceBean) rowKey.getAttribute("ROW") : rowKey);
			setPrgLavoratoreProfilo((BigDecimal) rowKey.getAttribute("DO_NEXTVAL"));

			Object params[] = new Object[] { getPrgLavoratoreProfilo(), getCdnLavoratore(), getNumValoreProfilo(),
					getCodVchProfiling(), getCodMonoStatoProf(), getCodCpi(), getCdnUtIns(), getCdnUtMod(),
					getFlgConoscenzaIta() };
			res = (Boolean) getTransactionQueryExecutor().executeQuery("INS_NEW_PROFILO_LAVORATORE", params, "INSERT");

			if (!res.booleanValue())
				throw new Exception("Impossibile inserire il nuovo profilo lavoratore");

			return res;
		}
		return res;
	}

	public Boolean aggiornaProfiloFlgLingua() throws EMFInternalError, Exception {
		loadProfilo();
		Boolean resUpd = null;
		Object params[] = new Object[] { getNumKloLavProfilo(), getFlgConoscenzaIta(), getNumValoreProfilo(),
				getCodVchProfiling(), getCdnUtMod(), getPrgLavoratoreProfilo() };
		resUpd = (Boolean) getTransactionQueryExecutor().executeQuery("UPD_INFO_PROFILO_LAVORATORE_FLG_LINGUA", params,
				"UPDATE");
		if (!resUpd.booleanValue())
			throw new Exception("Impossibile aggiornare il profilo lavoratore");
		return resUpd;
	}

	public void loadProfilo() throws EMFInternalError, Exception {
		SourceBean rowProfilo = caricaInfoProfilo();
		String statoCompilazione = (String) rowProfilo.getAttribute("CODMONOSTATOPROF");
		if (StringUtils.isFilledNoBlank(statoCompilazione)
				&& statoCompilazione.equalsIgnoreCase(Decodifica.StatoProfilo.CHIUSO_CALCOLATO)) {
			throw new Exception("Impossibile salvare il profilo lavoratore: stato profilo già calcolato");
		}
		BigDecimal numklolavprofilo = (BigDecimal) rowProfilo.getAttribute("NUMKLOLAVPROFILO");
		setNumKloLavProfilo(numklolavprofilo.add(new BigDecimal(1)));
	}

	public String getFlgConoscenzaIta() {
		return flgConoscenzaIta;
	}

	public void setFlgConoscenzaIta(String flgConoscenzaIta) {
		this.flgConoscenzaIta = flgConoscenzaIta;
	}
}
