package it.eng.sil.coop.webservices.agenda.appuntamento;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.DataResultInterface;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;
import com.engiweb.framework.dbaccess.sql.result.std.CompositeDataResult;
import com.engiweb.framework.error.EMFInternalError;

public class AgendaUtils {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AgendaUtils.class.getName());

	public static EsitoControlliCoerenza effettuaControlliCoerenza(DataConnection conn, String identificativoProvincia,
			String prgSlot, String codCpi, BigDecimal cdnLavoratore, String codComDomLav, String codComResLav,
			String codComNasLav, String codCittadinanzaLav, String strCell, String strEmail, String flgSMS,
			String flgEmail, String flgInvioSMS, String lavoratoreAzienda, String codiceRichiesta) {

		EsitoControlliCoerenza esito = new EsitoControlliCoerenza();
		DataResult dr = null;
		StoredProcedureCommand command = null;

		// test che si possono fare al volo

		if (flgEmail != null && "S".equalsIgnoreCase(flgEmail)) {
			if (strEmail == null || "".equalsIgnoreCase(strEmail)) {

				_logger.error(Constants.ESITO.DESC_ERRORE_COERENZA_EMAIL);
				esito.setSuccess(false);
				esito.setCodErrore(Constants.ESITO.ERRORE_COERENZA_EMAIL);
				esito.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_COERENZA_EMAIL);
				return esito;

			}
		}

		if (flgSMS != null && "S".equalsIgnoreCase(flgSMS) && flgInvioSMS != null
				&& "S".equalsIgnoreCase(flgInvioSMS)) {
			if (strCell == null || "".equalsIgnoreCase(strCell)) {

				_logger.error(Constants.ESITO.DESC_ERRORE_COERENZA_SMS);
				esito.setSuccess(false);
				esito.setCodErrore(Constants.ESITO.ERRORE_COERENZA_SMS);
				esito.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_COERENZA_SMS);
				return esito;

			}
		}

		// test che richiedono un'interazione con il database
		// sono implementati in una stored procedure

		try {

			int i = 0;
			int numOutParams = 0;
			ArrayList<DataField> parameters = new ArrayList<DataField>();
			String sqlStr = SQLStatements.getStatement("CONTROLLI_COERENZA_LAVORATORE_AGENDA");

			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			// input

			parameters.add(conn.createDataField("codiceRit", Types.VARCHAR, null));
			command.setAsOutputParameters(i++);

			parameters.add(conn.createDataField("in_codProvincia", Types.VARCHAR, identificativoProvincia));
			command.setAsInputParameters(i++);
			parameters.add(conn.createDataField("in_prgSlot", Types.BIGINT, prgSlot));
			command.setAsInputParameters(i++);
			parameters.add(conn.createDataField("in_codCpi", Types.VARCHAR, codCpi));
			command.setAsInputParameters(i++);
			parameters.add(conn.createDataField("in_cdnLavoratore", Types.VARCHAR, cdnLavoratore));
			command.setAsInputParameters(i++);
			parameters.add(conn.createDataField("in_codComDomLav", Types.VARCHAR, codComDomLav));
			command.setAsInputParameters(i++);
			parameters.add(conn.createDataField("in_codComResLav", Types.VARCHAR, codComResLav));
			command.setAsInputParameters(i++);
			parameters.add(conn.createDataField("in_codComNasLav", Types.VARCHAR, codComNasLav));
			command.setAsInputParameters(i++);
			parameters.add(conn.createDataField("in_cittadinanzaLav", Types.VARCHAR, codCittadinanzaLav));
			command.setAsInputParameters(i++);
			parameters.add(conn.createDataField("in_lavoratoreAzienda", Types.VARCHAR, lavoratoreAzienda));
			command.setAsInputParameters(i++);
			parameters.add(conn.createDataField("in_codiceRichiesta", Types.VARCHAR, codiceRichiesta));
			command.setAsInputParameters(i++);

			parameters.add(conn.createDataField("ErrCodeOut", Types.VARCHAR, null));
			command.setAsOutputParameters(i++);
			numOutParams++;

			// esecuzione

			dr = command.execute(parameters);

			// analisi risultati

			i = 0;
			CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
			List outputParams = cdr.getContainedDataResult();

			// output

			PunctualDataResult pdr = (PunctualDataResult) outputParams.get(i++);
			DataField df = pdr.getPunctualDatafield();
			String codiceRit = df.getStringValue();

			if ("0".equalsIgnoreCase(codiceRit)) {

				esito.setSuccess(true);

			} else if ("-1".equalsIgnoreCase(codiceRit)) {

				_logger.error("Errore generico: " + getStringParam(outputParams, numOutParams));
				esito.setSuccess(false);
				esito.setCodErrore(Constants.ESITO.ERRORE_GENERICO);
				esito.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_GENERICO);

			} else if ("-2".equalsIgnoreCase(codiceRit)) {

				_logger.error(Constants.ESITO.DESC_ERRORE_COERENZA_SLOT_CPI);
				esito.setSuccess(false);
				esito.setCodErrore(Constants.ESITO.ERRORE_COERENZA_SLOT_CPI);
				esito.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_COERENZA_SLOT_CPI);

			} else if ("-4".equalsIgnoreCase(codiceRit)) {

				_logger.error(Constants.ESITO.DESC_ERRORE_COERENZA_CPI_PROVINCIA);
				esito.setSuccess(false);
				esito.setCodErrore(Constants.ESITO.ERRORE_COERENZA_CPI_PROVINCIA);
				esito.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_COERENZA_CPI_PROVINCIA);

			} else if ("-6".equalsIgnoreCase(codiceRit)) {

				_logger.error(Constants.ESITO.DESC_ERRORE_COERENZA_COMDOM_PROV_CPI);
				esito.setSuccess(false);
				esito.setCodErrore(Constants.ESITO.ERRORE_COERENZA_COMDOM_PROV_CPI);
				esito.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_COERENZA_COMDOM_PROV_CPI);

			} else if ("-8".equalsIgnoreCase(codiceRit)) {

				_logger.error(Constants.ESITO.DESC_ERRORE_COERENZA_PROVINCIA_XML);
				esito.setSuccess(false);
				esito.setCodErrore(Constants.ESITO.ERRORE_COERENZA_PROVINCIA_XML);
				esito.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_COERENZA_PROVINCIA_XML);

			} else if ("-9".equalsIgnoreCase(codiceRit)) {

				_logger.error(Constants.ESITO.DESC_ERRORE_CODIFICA + "COMUNE DOMICILIO LAVORATORE");
				esito.setSuccess(false);
				esito.setCodErrore(Constants.ESITO.ERRORE_CODIFICA);
				esito.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_CODIFICA + "COMUNE DOMICILIO LAVORATORE");

			} else if ("-10".equalsIgnoreCase(codiceRit)) {

				_logger.error(Constants.ESITO.DESC_ERRORE_CODIFICA + "COMUNE RESIDENZA LAVORATORE");
				esito.setSuccess(false);
				esito.setCodErrore(Constants.ESITO.ERRORE_CODIFICA);
				esito.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_CODIFICA + "COMUNE RESIDENZA LAVORATORE");

			} else if ("-11".equalsIgnoreCase(codiceRit)) {

				_logger.error(Constants.ESITO.DESC_ERRORE_CODIFICA + "COMUNE NASCITA LAVORATORE");
				esito.setSuccess(false);
				esito.setCodErrore(Constants.ESITO.ERRORE_CODIFICA);
				esito.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_CODIFICA + "COMUNE NASCITA LAVORATORE");

			} else if ("-12".equalsIgnoreCase(codiceRit)) {

				_logger.error(Constants.ESITO.DESC_ERRORE_CODIFICA + "CITTADINANZA LAVORATORE");
				esito.setSuccess(false);
				esito.setCodErrore(Constants.ESITO.ERRORE_CODIFICA);
				esito.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_CODIFICA + "CITTADINANZA LAVORATORE");

			} else {

				_logger.error("Errore in esecuzione controlli coerenza, rif: " + codiceRit);
				_logger.error("Descrizione errore: " + getStringParam(outputParams, numOutParams));
				esito.setSuccess(false);
				esito.setCodErrore(Constants.ESITO.ERRORE_GENERICO);
				esito.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_GENERICO);

			}

		} catch (EMFInternalError e) {

			_logger.error("Eccezione durante chiamata alla procedura di ricerca slot", e);

			esito.setSuccess(false);
			esito.setCodErrore(Constants.ESITO.ERRORE_GENERICO);
			esito.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_GENERICO);

		} finally {

			releaseResources(command, dr);

		}

		return esito;

	}

	public static EsitoRicercaSlot cercaSlot(DataConnection conn, String codiceRichiesta,
			String identificativoProvincia, String prgSlot, String codCpi, String dataDa, String dataA,
			String mattinoPomeriggio, String lavoratoreAzienda) {

		EsitoRicercaSlot slot = new EsitoRicercaSlot();
		DataResult dr = null;
		StoredProcedureCommand command = null;

		try {

			int i = 0;
			int numOutParams = 0;
			ArrayList<DataField> parameters = new ArrayList<DataField>();
			String sqlStr = SQLStatements.getStatement("FIND_SLOT_AGENDA");

			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			// input

			parameters.add(conn.createDataField("codiceRit", Types.VARCHAR, null));
			command.setAsOutputParameters(i++);

			parameters.add(conn.createDataField("in_codiceRichiesta", Types.VARCHAR, codiceRichiesta));
			command.setAsInputParameters(i++);
			parameters.add(conn.createDataField("in_codProvincia", Types.VARCHAR, identificativoProvincia));
			command.setAsInputParameters(i++);
			parameters.add(conn.createDataField("in_prgSlot", Types.BIGINT, prgSlot));
			command.setAsInputParameters(i++);
			parameters.add(conn.createDataField("in_codCpi", Types.VARCHAR, codCpi));
			command.setAsInputParameters(i++);
			parameters.add(conn.createDataField("in_dataDa", Types.VARCHAR, dataDa));
			command.setAsInputParameters(i++);
			parameters.add(conn.createDataField("in_dataA", Types.VARCHAR, dataA));
			command.setAsInputParameters(i++);
			parameters.add(conn.createDataField("in_mattinoPomeriggio", Types.VARCHAR, mattinoPomeriggio));
			command.setAsInputParameters(i++);
			parameters.add(conn.createDataField("in_lavoratoreAzienda", Types.VARCHAR, lavoratoreAzienda));
			command.setAsInputParameters(i++);

			parameters.add(conn.createDataField("out_prgSlot", Types.VARCHAR, null));
			command.setAsOutputParameters(i++);
			numOutParams++;
			parameters.add(conn.createDataField("out_strDataApp", Types.VARCHAR, null));
			command.setAsOutputParameters(i++);
			numOutParams++;
			parameters.add(conn.createDataField("out_strOraApp", Types.VARCHAR, null));
			command.setAsOutputParameters(i++);
			numOutParams++;
			parameters.add(conn.createDataField("out_strDurataApp", Types.VARCHAR, null));
			command.setAsOutputParameters(i++);
			numOutParams++;

			parameters.add(conn.createDataField("out_codservizio", Types.VARCHAR, null));
			command.setAsOutputParameters(i++);
			numOutParams++;
			parameters.add(conn.createDataField("out_descservizio", Types.VARCHAR, null));
			command.setAsOutputParameters(i++);
			numOutParams++;

			parameters.add(conn.createDataField("ErrCodeOut", Types.VARCHAR, null));
			command.setAsOutputParameters(i++);
			numOutParams++;

			// esecuzione

			dr = command.execute(parameters);

			// analisi risultati

			i = 0;
			CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
			List outputParams = cdr.getContainedDataResult();

			// output

			PunctualDataResult pdr = (PunctualDataResult) outputParams.get(i++);
			DataField df = pdr.getPunctualDatafield();
			String codiceRit = df.getStringValue();

			if ("0".equalsIgnoreCase(codiceRit)) {

				BigDecimal prgSlotTrovato = new BigDecimal(getStringParam(outputParams, i++));
				String data = getStringParam(outputParams, i++);
				String ora = getStringParam(outputParams, i++);
				String durata = getStringParam(outputParams, i++);
				String codServizio = getStringParam(outputParams, i++);
				String descrizioneServizio = getStringParam(outputParams, i++);

				slot.setTrovato(true);
				slot.setPrgSlot(prgSlotTrovato);
				slot.setData(data);
				slot.setOra(ora);
				slot.setDurata(durata);
				slot.setCodServizio(codServizio);
				slot.setDescrizioneServizio(descrizioneServizio);

			} else if ("-1".equalsIgnoreCase(codiceRit)) {

				_logger.error("Errore generico: " + getStringParam(outputParams, numOutParams));
				slot.setTrovato(false);
				slot.setCodErrore(Constants.ESITO.ERRORE_GENERICO);
				slot.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_GENERICO);

			} else if ("-2".equalsIgnoreCase(codiceRit) || "-8".equalsIgnoreCase(codiceRit)) {

				_logger.error("Errore codservizio non trovato o non corrispondente a un servizio su SIL.");
				slot.setTrovato(false);
				slot.setCodErrore(Constants.ESITO.ERRORE_SERVIZIO_MANCANTE);
				slot.setDescrizioneErrore(Constants.ESITO.DESC_SERVIZIO_MANCANTE);

			} else if ("-5".equalsIgnoreCase(codiceRit)) {

				// da ricerca
				_logger.error("Errore: slot non trovato da ricerca.");
				slot.setTrovato(false);
				slot.setCodErrore(Constants.ESITO.ERRORE_NESSUNO_SLOT_TROVATO);
				slot.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_NESSUNO_SLOT_TROVATO);

			} else if ("-6".equalsIgnoreCase(codiceRit)) {

				// con prgslot
				_logger.error("Errore: slot non trovato con prgslot: " + prgSlot);
				slot.setTrovato(false);
				slot.setCodErrore(Constants.ESITO.ERRORE_INPUT_SLOT_NON_TROVATO);
				slot.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_INPUT_SLOT_NON_TROVATO);

			} else if ("-7".equalsIgnoreCase(codiceRit)) {

				_logger.error("Errore: slot non disponibile: " + prgSlot);
				slot.setTrovato(false);
				slot.setCodErrore(Constants.ESITO.ERRORE_INPUT_SLOT_NON_DISPONIBILE);
				slot.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_INPUT_SLOT_NON_DISPONIBILE);

			} else {

				_logger.error("Errore in fase di ricerca slot, rif: " + codiceRit);
				_logger.error("Descrizione errore: " + getStringParam(outputParams, numOutParams));
				slot.setTrovato(false);
				slot.setCodErrore(Constants.ESITO.ERRORE_GENERICO);
				slot.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_GENERICO);

			}

		} catch (EMFInternalError e) {

			_logger.error("Eccezione durante chiamata alla procedura di ricerca slot", e);

			slot.setTrovato(false);
			slot.setCodErrore(Constants.ESITO.ERRORE_GENERICO);
			slot.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_GENERICO);

		} finally {

			releaseResources(command, dr);

		}

		return slot;

	}

	public static EsitoAppuntamento prenotaAppuntamento(DataConnection conn, String codCpi, BigDecimal prgSlot,
			BigDecimal cdnLavoratore, BigDecimal prgAzienda, BigDecimal prgUnita, String strEmail, String strCell,
			String do_Commit) {

		EsitoAppuntamento esitoAppuntamento = new EsitoAppuntamento();
		DataResult dr = null;
		StoredProcedureCommand command = null;

		try {

			int i = 0;
			int numOutParams = 0;
			ArrayList<DataField> parameters = new ArrayList<DataField>();
			String sqlStr = SQLStatements.getStatement("REGISTRA_APPUNTAMENTO_AGENDA");

			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			// input

			parameters.add(conn.createDataField("codiceRit", Types.VARCHAR, null));
			command.setAsOutputParameters(i++);

			parameters.add(conn.createDataField("in_codCpi", Types.VARCHAR, codCpi));
			command.setAsInputParameters(i++);
			parameters.add(conn.createDataField("in_prgSlot", Types.BIGINT, prgSlot));
			command.setAsInputParameters(i++);
			parameters.add(conn.createDataField("in_cdnlavoratore", Types.BIGINT, cdnLavoratore));
			command.setAsInputParameters(i++);

			parameters.add(conn.createDataField("in_prgAzienda", Types.BIGINT, prgAzienda));
			command.setAsInputParameters(i++);
			parameters.add(conn.createDataField("in_prgUnita", Types.BIGINT, prgUnita));
			command.setAsInputParameters(i++);

			parameters.add(conn.createDataField("in_strEmail", Types.VARCHAR, strEmail));
			command.setAsInputParameters(i++);
			parameters.add(conn.createDataField("in_strCell", Types.VARCHAR, strCell));
			command.setAsInputParameters(i++);
			parameters.add(conn.createDataField("in_doCommit", Types.VARCHAR, do_Commit));
			command.setAsInputParameters(i++);

			parameters.add(conn.createDataField("out_strDescrizioneCpi", Types.VARCHAR, null));
			command.setAsOutputParameters(i++);
			numOutParams++;
			parameters.add(conn.createDataField("out_strIndirizzoCpi", Types.VARCHAR, null));
			command.setAsOutputParameters(i++);
			numOutParams++;
			parameters.add(conn.createDataField("out_strIndirizzoStampaCpi", Types.VARCHAR, null));
			command.setAsOutputParameters(i++);
			numOutParams++;
			parameters.add(conn.createDataField("out_strTelCpi", Types.VARCHAR, null));
			command.setAsOutputParameters(i++);
			numOutParams++;

			parameters.add(conn.createDataField("out_strIsSpiServizioDisp", Types.VARCHAR, null));
			command.setAsOutputParameters(i++);
			numOutParams++;
			parameters.add(conn.createDataField("out_prgSpiServizio", Types.BIGINT, null));
			command.setAsOutputParameters(i++);
			numOutParams++;
			parameters.add(conn.createDataField("out_strNomeSpiServizio", Types.VARCHAR, null));
			command.setAsOutputParameters(i++);
			numOutParams++;
			parameters.add(conn.createDataField("out_strCognomeSpiServizio", Types.VARCHAR, null));
			command.setAsOutputParameters(i++);
			numOutParams++;
			parameters.add(conn.createDataField("out_strTelSpiServizio", Types.VARCHAR, null));
			command.setAsOutputParameters(i++);
			numOutParams++;
			parameters.add(conn.createDataField("out_strSiglaSpiServizio", Types.VARCHAR, null));
			command.setAsOutputParameters(i++);
			numOutParams++;

			parameters.add(conn.createDataField("out_strIsSpiSlotDisp", Types.VARCHAR, null));
			command.setAsOutputParameters(i++);
			numOutParams++;
			parameters.add(conn.createDataField("out_prgSpiSlot", Types.BIGINT, null));
			command.setAsOutputParameters(i++);
			numOutParams++;
			parameters.add(conn.createDataField("out_strNomeSpiSlot", Types.VARCHAR, null));
			command.setAsOutputParameters(i++);
			numOutParams++;
			parameters.add(conn.createDataField("out_strCognomeSpiSlot", Types.VARCHAR, null));
			command.setAsOutputParameters(i++);
			numOutParams++;
			parameters.add(conn.createDataField("out_strTelSpiSlot", Types.VARCHAR, null));
			command.setAsOutputParameters(i++);
			numOutParams++;
			parameters.add(conn.createDataField("out_strSiglaSpiSlot", Types.VARCHAR, null));
			command.setAsOutputParameters(i++);
			numOutParams++;

			parameters.add(conn.createDataField("out_strAmbiente", Types.VARCHAR, null));
			command.setAsOutputParameters(i++);
			numOutParams++;

			parameters.add(conn.createDataField("out_prgAppuntamento", Types.BIGINT, null));
			command.setAsOutputParameters(i++);
			numOutParams++;

			parameters.add(conn.createDataField("ErrCodeOut", Types.VARCHAR, null));
			command.setAsOutputParameters(i++);
			numOutParams++;

			// esecuzione

			dr = command.execute(parameters);

			// analisi risultati

			i = 0;
			CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
			List outputParams = cdr.getContainedDataResult();

			// output

			PunctualDataResult pdr = (PunctualDataResult) outputParams.get(i++);
			DataField df = pdr.getPunctualDatafield();
			String codiceRit = df.getStringValue();

			if ("0".equalsIgnoreCase(codiceRit)) {

				boolean success = true;

				String descrizioneCpi = getStringParam(outputParams, i++);
				String indirizzoCpi = getStringParam(outputParams, i++);
				String indirizzoStampaCpi = getStringParam(outputParams, i++);
				String telefonoCpi = getStringParam(outputParams, i++);

				String isSpiServizioDispString = getStringParam(outputParams, i++);
				boolean isSpiServizioDisp = isSpiServizioDispString != null
						&& "S".equalsIgnoreCase(isSpiServizioDispString);
				BigDecimal prgSpiServizio = getBigDecimalParam(outputParams, i++);
				String nomeSpiServizio = getStringParam(outputParams, i++);
				String cognomeSpiServizio = getStringParam(outputParams, i++);
				String telSpiServizio = getStringParam(outputParams, i++);
				String siglaSpiServizio = getStringParam(outputParams, i++);

				String isSpiSlotDispString = getStringParam(outputParams, i++);
				boolean isSpiSlotDisp = isSpiSlotDispString != null && "S".equalsIgnoreCase(isSpiSlotDispString);
				BigDecimal prgSpiSlot = getBigDecimalParam(outputParams, i++);
				String nomeSpiSlot = getStringParam(outputParams, i++);
				String cognomeSpiSlot = getStringParam(outputParams, i++);
				String telSpiSlot = getStringParam(outputParams, i++);
				String siglaSpiSlot = getStringParam(outputParams, i++);

				String ambiente = getStringParam(outputParams, i++);

				BigDecimal prgAppuntamento = getBigDecimalParam(outputParams, i++);

				esitoAppuntamento.setSuccess(success);
				esitoAppuntamento.setDescrizioneCpi(descrizioneCpi);
				esitoAppuntamento.setIndirizzoCpi(indirizzoCpi);
				esitoAppuntamento.setIndirizzoStampaCpi(indirizzoStampaCpi);
				esitoAppuntamento.setTelefonoCpi(telefonoCpi);
				esitoAppuntamento.setSpiServizioDisp(isSpiServizioDisp);
				esitoAppuntamento.setPrgSpiServizio(prgSpiServizio);
				esitoAppuntamento.setNomeSpiServizio(nomeSpiServizio);
				esitoAppuntamento.setCognomeSpiServizio(cognomeSpiServizio);
				esitoAppuntamento.setTelSpiServizio(telSpiServizio);
				esitoAppuntamento.setSiglaSpiServizio(siglaSpiServizio);
				esitoAppuntamento.setSpiSlotDisp(isSpiSlotDisp);
				esitoAppuntamento.setPrgSpiSlot(prgSpiSlot);
				esitoAppuntamento.setNomeSpiSlot(nomeSpiSlot);
				esitoAppuntamento.setCognomeSpiSlot(cognomeSpiSlot);
				esitoAppuntamento.setTelSpiSlot(telSpiSlot);
				esitoAppuntamento.setSiglaSpiSlot(siglaSpiSlot);
				esitoAppuntamento.setAmbiente(ambiente);
				esitoAppuntamento.setPrgAppuntamento(prgAppuntamento);

			} else if ("-1".equalsIgnoreCase(codiceRit)) {

				_logger.error("Errore generico: " + getStringParam(outputParams, numOutParams));
				esitoAppuntamento.setSuccess(false);
				esitoAppuntamento.setCodErrore(Constants.ESITO.ERRORE_GENERICO);
				esitoAppuntamento.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_GENERICO);

			} else if ("-98".equalsIgnoreCase(codiceRit)) {

				_logger.error(Constants.ESITO.DESC_ERRORE_CONCORRENZA);
				_logger.error("Descrizione errore: " + getStringParam(outputParams, numOutParams));
				esitoAppuntamento.setSuccess(false);
				esitoAppuntamento.setCodErrore(Constants.ESITO.ERRORE_CONCORRENZA);
				esitoAppuntamento.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_CONCORRENZA);

			} else {

				_logger.error("Errore in fase di prenotazione appuntamento, rif: " + codiceRit);
				_logger.error("Descrizione errore: " + getStringParam(outputParams, numOutParams));
				esitoAppuntamento.setSuccess(false);
				esitoAppuntamento.setCodErrore(Constants.ESITO.ERRORE_GENERICO);
				esitoAppuntamento.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_GENERICO);

			}

		} catch (EMFInternalError e) {

			_logger.error("Errore prenota appuntamento ", e);

			boolean success = false;
			String codErrore = Constants.ESITO.ERRORE_GENERICO;
			String descrizioneErrore = Constants.ESITO.DESC_ERRORE_GENERICO;

			esitoAppuntamento.setSuccess(success);
			esitoAppuntamento.setCodErrore(codErrore);
			esitoAppuntamento.setDescrizioneErrore(descrizioneErrore);

		} finally {

			releaseResources(command, dr);

		}

		return esitoAppuntamento;

	}

	public static String aggiornaIdCoapAppuntamento(DataConnection conn, BigDecimal prgAppuntamento, String strIdCoap,
			BigDecimal cdnUtente, String do_Commit) {

		String esitoUpdate = new String();
		DataResult dr = null;
		StoredProcedureCommand command = null;
		try {

			int i = 0;
			int numOutParams = 0;
			ArrayList<DataField> parameters = new ArrayList<DataField>();
			String sqlStr = SQLStatements.getStatement("REGISTRA_APPUNTAMENTO_AGENDA_ID_COAP");

			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlStr);

			// input

			parameters.add(conn.createDataField("codiceRit", Types.VARCHAR, null));
			command.setAsOutputParameters(i++);

			parameters.add(conn.createDataField("in_prgAppuntamento", Types.BIGINT, prgAppuntamento));
			command.setAsInputParameters(i++);
			parameters.add(conn.createDataField("in_strIdCoap", Types.VARCHAR, strIdCoap));
			command.setAsInputParameters(i++);
			parameters.add(conn.createDataField("cdnParUtente", Types.BIGINT, cdnUtente));
			command.setAsInputParameters(i++);
			parameters.add(conn.createDataField("in_doCommit", Types.VARCHAR, do_Commit));
			command.setAsInputParameters(i++);

			parameters.add(conn.createDataField("ErrCodeOut", Types.VARCHAR, null));
			command.setAsOutputParameters(i++);
			numOutParams++;

			// esecuzione

			dr = command.execute(parameters);

			// analisi risultati

			i = 0;
			CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
			List outputParams = cdr.getContainedDataResult();

			// output

			PunctualDataResult pdr = (PunctualDataResult) outputParams.get(i++);
			DataField df = pdr.getPunctualDatafield();
			String codiceRit = df.getStringValue();

			esitoUpdate = "KO";

			if ("0".equalsIgnoreCase(codiceRit)) {

				boolean success = true;

				esitoUpdate = "OK";

			} else if ("-1".equalsIgnoreCase(codiceRit)) {

				_logger.error("Errore generico: " + getStringParam(outputParams, numOutParams));
				/*
				 * esitoUpdate.setSuccess(false); esitoUpdate.setCodErrore(Constants.ESITO.ERRORE_GENERICO);
				 * esitoUpdate.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_GENERICO);
				 */

			} else if ("-98".equalsIgnoreCase(codiceRit)) {

				_logger.error(Constants.ESITO.DESC_ERRORE_CONCORRENZA);
				_logger.error("Descrizione errore: " + getStringParam(outputParams, numOutParams));
				/*
				 * esitoUpdate.setSuccess(false); esitoUpdate.setCodErrore(Constants.ESITO.ERRORE_CONCORRENZA);
				 * esitoUpdate.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_CONCORRENZA);
				 */

			} else {

				_logger.error("Errore in fase di aggiornamento appuntamento, rif: " + codiceRit);
				_logger.error("Descrizione errore: " + getStringParam(outputParams, numOutParams));
				/*
				 * esitoUpdate.setSuccess(false); esitoUpdate.setCodErrore(Constants.ESITO.ERRORE_GENERICO);
				 * esitoUpdate.setDescrizioneErrore(Constants.ESITO.DESC_ERRORE_GENERICO);
				 */

			}

		} catch (EMFInternalError e) {

			_logger.error("Errore aggiorna appuntamento ", e);

			boolean success = false;
			String codErrore = Constants.ESITO.ERRORE_GENERICO;
			String descrizioneErrore = Constants.ESITO.DESC_ERRORE_GENERICO;

		} finally {

			releaseResources(command, dr);

		}

		return esitoUpdate;

	}

	private static void releaseResources(StoredProcedureCommand command, DataResult dataResult) {

		if (command != null) {
			try {
				command.close();
			} catch (EMFInternalError ie) {
				it.eng.sil.util.TraceWrapper.error(_logger, "Utils::releaseResources: command.close()", (Exception) ie);

			}
		}
		if (dataResult != null) {
			DataResultInterface resultInterface = dataResult.getDataObject();
			if ((resultInterface != null) && (resultInterface instanceof ScrollableDataResult)) {
				try {
					((ScrollableDataResult) resultInterface).close();
				} catch (EMFInternalError ie) {
					it.eng.sil.util.TraceWrapper.error(_logger,
							"Utils::releaseResources: ((ScrollableDataResult)resultInterface).close()", (Exception) ie);

				}
			}
		}
	}

	private static String getStringParam(List outputParams, int i) {
		PunctualDataResult pdr = (PunctualDataResult) outputParams.get(i);
		DataField df = pdr.getPunctualDatafield();
		String value = df.getStringValue();
		if (value == null) {
			value = "";
		} else {
			value = value.trim();
		}
		return value;
	}

	private static BigDecimal getBigDecimalParam(List outputParams, int i) {
		PunctualDataResult pdr = (PunctualDataResult) outputParams.get(i);
		DataField df = pdr.getPunctualDatafield();
		String value = df.getStringValue();
		BigDecimal valueBD = null;
		if (value == null) {
			value = "";
		} else {
			value = value.trim();
			valueBD = new BigDecimal(value);
		}
		return valueBD;
	}

}
