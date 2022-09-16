package it.eng.sil.module.modelli.tda;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;

import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;

public class ModelloTda {

	public ModelloTda() {
		super();
	}

	public ModelloTda(BigDecimal prgModello) {
		super();
		this.prgModello = prgModello;
	}

	public ModelloTda(BigDecimal prgModello, BigDecimal numKeyLock) {
		super();
		this.prgModello = prgModello;
		this.numKeyLock = numKeyLock;
	}

	public ModelloTda(BigDecimal prgModello, BigDecimal prgAzione, String flgAttivo) {
		super();
		this.prgModello = prgModello;
		this.prgAzione = prgAzione;
		this.flgAttivo = flgAttivo;
	}

	private BigDecimal prgModello = null;
	private BigDecimal prgAzione = null;
	private BigDecimal numGiorniAttivazione = null;
	private BigDecimal numGiorniErogazione = null;
	private double decValTot;
	private String codModalita = null;
	private String flgAttivo = null;
	private BigDecimal numKeyLock = null;
	private BigDecimal cndUt = null;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Integer inserisciModello(DataConnection conn, String flagCM, String strCodModalita, BigDecimal giorniAtt,
			BigDecimal giorniErog, Double valoreMax, BigDecimal cdnParUtente) {
		DataResult dr = null;
		StoredProcedureCommand command = null;
		try {

			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(
					"{ call ? := PG_GESTIONE_VOUCHER.inserisciModelloTda(?,?,?,?,?,?,?) }");

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(8);

			// Parametro di Ritorno
			parameters.add(conn.createDataField("esito", Types.INTEGER, null));
			command.setAsOutputParameters(paramIndex++);

			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("prgAzioni", Types.BIGINT, getPrgAzione()));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("strFlagCM", Types.VARCHAR, flagCM));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("strCodModalita", Types.VARCHAR, strCodModalita));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("ggAttivazione", Types.INTEGER, giorniAtt));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("ggErogazione", Types.INTEGER, giorniErog));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("valoreMax", Types.DOUBLE, valoreMax));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("cdnParUtente", Types.BIGINT, cdnParUtente));
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Integer aggiornaModello(DataConnection conn, String flagCM, String strCodModalita, BigDecimal giorniAtt,
			BigDecimal giorniErog, Double valoreMax, BigDecimal cdnParUtente) {
		DataResult dr = null;
		StoredProcedureCommand command = null;
		try {

			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(
					"{ call ? := PG_GESTIONE_VOUCHER.aggiornaModelloTda(?,?,?,?,?,?,?,?) }");

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(9);

			// Parametro di Ritorno
			parameters.add(conn.createDataField("esito", Types.INTEGER, null));
			command.setAsOutputParameters(paramIndex++);

			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("prgModelloTdaVar", Types.BIGINT, getPrgModello()));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("numKLockVar", Types.BIGINT, getNumKeyLock()));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("strFlagCM", Types.VARCHAR, flagCM));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("strCodModalita", Types.VARCHAR, strCodModalita));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("ggAttivazione", Types.INTEGER, giorniAtt));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("ggErogazione", Types.INTEGER, giorniErog));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("valoreMax", Types.DOUBLE, valoreMax));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("cdnParUtente", Types.BIGINT, cdnParUtente));
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Integer calcolaValoreTotaleModelloTDA(DataConnection conn, BigDecimal cdnParUtente) {
		DataResult dr = null;
		StoredProcedureCommand command = null;
		try {

			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(
					"{ call ? := PG_GESTIONE_VOUCHER.aggiornaValoreTotaleModelloTDA(?,?) }");

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(3);

			// Parametro di Ritorno
			parameters.add(conn.createDataField("esito", Types.INTEGER, null));
			command.setAsOutputParameters(paramIndex++);

			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("prgModelloTdaVar", Types.BIGINT, getPrgModello()));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("cdnParUtente", Types.BIGINT, cdnParUtente));
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Integer controllaCoerenzaModello(DataConnection conn) {
		DataResult dr = null;
		StoredProcedureCommand command = null;
		try {

			command = (StoredProcedureCommand) conn
					.createStoredProcedureCommand("{ call ? := PG_GESTIONE_VOUCHER.controllaCoerenzaModello(?) }");

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(2);

			// Parametro di Ritorno
			parameters.add(conn.createDataField("esito", Types.INTEGER, null));
			command.setAsOutputParameters(paramIndex++);

			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("prgModelloTdaVar", Types.BIGINT, getPrgModello()));
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Integer attivaDisattivaModello(DataConnection conn, BigDecimal cdnParUtente, String flgAttivo) {
		DataResult dr = null;
		StoredProcedureCommand command = null;
		try {

			command = (StoredProcedureCommand) conn
					.createStoredProcedureCommand("{ call ? := PG_GESTIONE_VOUCHER.attivaDisattivaModello(?,?,?) }");

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(4);

			// Parametro di Ritorno
			parameters.add(conn.createDataField("esito", Types.INTEGER, null));
			command.setAsOutputParameters(paramIndex++);

			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("prgModelloTdaVar", Types.BIGINT, getPrgModello()));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("cdnParUtente", Types.BIGINT, cdnParUtente));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("flgAttivoVar", Types.CHAR, flgAttivo));
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

	public BigDecimal getPrgModello() {
		return prgModello;
	}

	public void setPrgModello(BigDecimal prgModello) {
		this.prgModello = prgModello;
	}

	public BigDecimal getPrgAzione() {
		return prgAzione;
	}

	public void setPrgAzione(BigDecimal prgAzione) {
		this.prgAzione = prgAzione;
	}

	public BigDecimal getNumGiorniAttivazione() {
		return numGiorniAttivazione;
	}

	public void setNumGiorniAttivazione(BigDecimal numGiorniAttivazione) {
		this.numGiorniAttivazione = numGiorniAttivazione;
	}

	public BigDecimal getNumGiorniErogazione() {
		return numGiorniErogazione;
	}

	public void setNumGiorniErogazione(BigDecimal numGiorniErogazione) {
		this.numGiorniErogazione = numGiorniErogazione;
	}

	public double getDecValTot() {
		return decValTot;
	}

	public void setDecValTot(double decValTot) {
		this.decValTot = decValTot;
	}

	public String getCodModalita() {
		return codModalita;
	}

	public void setCodModalita(String codModalita) {
		this.codModalita = codModalita;
	}

	public String getFlgAttivo() {
		return flgAttivo;
	}

	public void setFlgAttivo(String flgAttivo) {
		this.flgAttivo = flgAttivo;
	}

	public BigDecimal getNumKeyLock() {
		return numKeyLock;
	}

	public void setNumKeyLock(BigDecimal numKeyLock) {
		this.numKeyLock = numKeyLock;
	}

	public BigDecimal getCndUt() {
		return cndUt;
	}

	public void setCndUt(BigDecimal cndUt) {
		this.cndUt = cndUt;
	}

}
