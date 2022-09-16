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

public class ModalitaModelloTda {

	public ModalitaModelloTda(BigDecimal prgModalita) {
		super();
		this.prgModalita = prgModalita;
	}

	public ModalitaModelloTda() {
		super();
	}

	public ModalitaModelloTda(BigDecimal prgModalita, BigDecimal numKeyLock, String codiceModalita,
			BigDecimal prgModello) {
		super();
		this.prgModalita = prgModalita;
		this.numKeyLock = numKeyLock;
		this.codiceModalita = codiceModalita;
		this.prgModello = prgModello;
	}

	public ModalitaModelloTda(String codiceModalita, BigDecimal prgModello) {
		super();
		this.codiceModalita = codiceModalita;
		this.prgModello = prgModello;
	}

	private BigDecimal prgModello = null;
	private BigDecimal prgModalita = null;
	private BigDecimal numKeyLock = null;
	private String codiceModalita = null;
	private String tipoDurata = null;
	private BigDecimal durataMinima = null;
	private BigDecimal durataMassima = null;
	private BigDecimal percentuale = null;
	private String tipoRimborso = null;
	private double valoreUnitario;
	private double valoreTotale;

	public BigDecimal getPrgModello() {
		return prgModello;
	}

	public void setPrgModello(BigDecimal prgModello) {
		this.prgModello = prgModello;
	}

	public BigDecimal getPrgModalita() {
		return prgModalita;
	}

	public void setPrgModalita(BigDecimal prgModalita) {
		this.prgModalita = prgModalita;
	}

	public BigDecimal getNumKeyLock() {
		return numKeyLock;
	}

	public void setNumKeyLock(BigDecimal numKeyLock) {
		this.numKeyLock = numKeyLock;
	}

	public String getCodiceModalita() {
		return codiceModalita;
	}

	public void setCodiceModalita(String codiceModalita) {
		this.codiceModalita = codiceModalita;
	}

	public String getTipoDurata() {
		return tipoDurata;
	}

	public void setTipoDurata(String tipoDurata) {
		this.tipoDurata = tipoDurata;
	}

	public BigDecimal getDurataMinima() {
		return durataMinima;
	}

	public void setDurataMinima(BigDecimal durataMinima) {
		this.durataMinima = durataMinima;
	}

	public BigDecimal getDurataMassima() {
		return durataMassima;
	}

	public void setDurataMassima(BigDecimal durataMassima) {
		this.durataMassima = durataMassima;
	}

	public BigDecimal getPercentuale() {
		return percentuale;
	}

	public void setPercentuale(BigDecimal percentuale) {
		this.percentuale = percentuale;
	}

	public String getTipoRimborso() {
		return tipoRimborso;
	}

	public void setTipoRimborso(String tipoRimborso) {
		this.tipoRimborso = tipoRimborso;
	}

	public double getValoreUnitario() {
		return valoreUnitario;
	}

	public void setValoreUnitario(double valoreUnitario) {
		this.valoreUnitario = valoreUnitario;
	}

	public double getValoreTotale() {
		return valoreTotale;
	}

	public void setValoreTotale(double valoreTotale) {
		this.valoreTotale = valoreTotale;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public BigDecimal gestisciModalita(DataConnection conn, boolean isInsert, String tipoDurata,
			BigDecimal durataMinima, BigDecimal durataMassima, BigDecimal percentuale, String tipoRimborso,
			Double valoreUnitario, Double valoreTotale, BigDecimal cdnParUtente) {

		DataResult dr = null;
		StoredProcedureCommand command = null;
		try {

			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(
					"{ call ? := PG_GESTIONE_VOUCHER.gestioneModalitaModTda(?,?,?,?,?,?,?,?,?,?,?,?,?) }");

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(14);

			// Parametro di Ritorno
			parameters.add(conn.createDataField("esito", Types.BIGINT, null));
			command.setAsOutputParameters(paramIndex++);

			// preparazione dei Parametri di Input
			String operazione = isInsert ? "INSERISCI" : "AGGIORNA";
			parameters.add(conn.createDataField("tipoOperazioneVar", Types.VARCHAR, operazione));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("prgModelloTdaVar", Types.BIGINT, getPrgModello()));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("prgModalitaTdaVar", Types.BIGINT, getPrgModalita()));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("numKLockVar", Types.BIGINT, getNumKeyLock()));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("codModalitaErogVar", Types.VARCHAR, getCodiceModalita()));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("tipoDurataVar", Types.VARCHAR, tipoDurata));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("durataMinvar", Types.INTEGER, durataMinima));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("durataMaxvar", Types.INTEGER, durataMassima));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("percentualeVar", Types.INTEGER, percentuale));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("tipoRimborsoVar", Types.VARCHAR, tipoRimborso));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("valUnitVar", Types.DOUBLE, valoreUnitario));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("valTotVar", Types.DOUBLE, valoreTotale));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("cdnParUtente", Types.BIGINT, cdnParUtente));
			command.setAsInputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);
			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
			DataField df = pdr.getPunctualDatafield();
			Long esitoL = (Long) df.getObjectValue();
			BigDecimal esito = BigDecimal.valueOf(esitoL);
			return esito;
		} catch (Exception ex) {
			return new BigDecimal(-1);
		} finally {
			Utils.releaseResources(null, command, dr);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Integer eliminaModalitaModelloTDA(DataConnection conn, BigDecimal cdnParUtente) {
		DataResult dr = null;
		StoredProcedureCommand command = null;
		try {

			command = (StoredProcedureCommand) conn
					.createStoredProcedureCommand("{ call ? := PG_GESTIONE_VOUCHER.deleteModalitaModelloTDA(?,?) }");

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(3);

			// Parametro di Ritorno
			parameters.add(conn.createDataField("esito", Types.INTEGER, null));
			command.setAsOutputParameters(paramIndex++);

			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("prgModalitaTdaVar", Types.BIGINT, getPrgModalita()));
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

}
