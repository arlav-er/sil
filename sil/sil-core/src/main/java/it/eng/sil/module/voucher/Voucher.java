package it.eng.sil.module.voucher;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.dbaccess.sql.result.std.CompositeDataResult;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;

public class Voucher {

	static Logger logger = Logger.getLogger(Voucher.class.getName());

	public static final String TIPO_TDA_2Ce = "2Ce";

	private BigDecimal prgVoucher = null;
	private BigDecimal prgPercorso = null;
	private BigDecimal prgColloquio = null;
	private BigDecimal prgPattoLavoratore = null;
	private String codiceAttivazione = null;
	private String codSelezioneModalita = null;
	private double decValTot;
	private double decValEffettivo;
	private String codStato = null;
	private String codTipoServizio = null;
	private double residuo;
	private String azione = null;
	private BigDecimal numKeyLock;
	private BigDecimal numCodice;
	private int numAnnoCodice;
	private String codProvincia = null;

	public Voucher() {
		// TODO Auto-generated constructor stub
	}

	public Voucher(BigDecimal prgVoucherkey) {
		this.prgVoucher = prgVoucherkey;
	}

	public Voucher(BigDecimal prgPercorsoVal, BigDecimal prgColloquioVal, String codiceAttivazioneVal) {
		this.prgPercorso = prgPercorsoVal;
		this.prgColloquio = prgColloquioVal;
		this.codiceAttivazione = codiceAttivazioneVal;
	}

	public Voucher(BigDecimal prgVoucherVal, BigDecimal prgPercorsoVal, BigDecimal prgColloquioVal) {
		this.prgPercorso = prgPercorsoVal;
		this.prgColloquio = prgColloquioVal;
		this.prgVoucher = prgVoucherVal;
	}

	public Voucher(BigDecimal prgPercorsoVal, BigDecimal prgColloquioVal) {
		this.prgPercorso = prgPercorsoVal;
		this.prgColloquio = prgColloquioVal;
	}

	public boolean verificaDoteAssegnazione() {
		double doteResidua = getResiduo() - getDecValTot();
		if (doteResidua > 0)
			return true;
		else
			return false;
	}

	public boolean verificaBugdetCPIAssegnazione(double residuoCPI) {
		double doteResidua = residuoCPI - getDecValTot();
		if (doteResidua > 0)
			return true;
		else
			return false;
	}

	public void setPrgPattoLavoratore(BigDecimal val) {
		this.prgPattoLavoratore = val;
	}

	public void setPrgPercorso(BigDecimal val) {
		this.prgPercorso = val;
	}

	public void setPrgColloquio(BigDecimal val) {
		this.prgColloquio = val;
	}

	public void setCodSelezioneModalita(String val) {
		this.codSelezioneModalita = val;
	}

	public void setCodiceAttivazione(String val) {
		this.codiceAttivazione = val;
	}

	public void setDecValTot(double val) {
		this.decValTot = val;
	}

	public void setCodStato(String val) {
		this.codStato = val;
	}

	public void setCodTipoServizio(String val) {
		this.codTipoServizio = val;
	}

	public void setResiduo(double val) {
		this.residuo = val;
	}

	public void setDecValEffettivo(double val) {
		this.decValEffettivo = val;
	}

	public void setAzione(String val) {
		this.azione = val;
	}

	public BigDecimal getPrgPattoLavoratore() {
		return this.prgPattoLavoratore;
	}

	public BigDecimal getPrgVoucher() {
		return this.prgVoucher;
	}

	public BigDecimal getPrgPercorso() {
		return this.prgPercorso;
	}

	public BigDecimal getPrgColloquio() {
		return this.prgColloquio;
	}

	public String getCodSelezioneModalita() {
		return this.codSelezioneModalita;
	}

	public String getCodiceAttivazione() {
		return this.codiceAttivazione;
	}

	public double getDecValTot() {
		return this.decValTot;
	}

	public String getCodStato() {
		return this.codStato;
	}

	public String getCodTipoServizio() {
		return this.codTipoServizio;
	}

	public double getResiduo() {
		return this.residuo;
	}

	public double getDecValEffettivo() {
		return this.decValEffettivo;
	}

	public String getAzione() {
		return this.azione;
	}

	public String getCodProvincia() {
		return this.codProvincia;
	}

	public int getNumAnnoCodice() {
		return this.numAnnoCodice;
	}

	public BigDecimal getNumCodice() {
		return this.numCodice;
	}

	public BigDecimal getKeyNumLock() {
		return this.numKeyLock;
	}

	public String toString() {
		return "VOUCHER ASSOCIATO ALL'AZIONE";
	}

	public Integer assegnaAllaData(DataConnection conn, BigDecimal cdnParUtente, String dataInput) {
		DataResult dr = null;
		StoredProcedureCommand command = null;
		try {

			command = (StoredProcedureCommand) conn
					.createStoredProcedureCommand("{ call ? := PG_VOUCHER.assegnazioneAllaData(?,?,?,?,?) }");

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(6);

			// Parametro di Ritorno
			parameters.add(conn.createDataField("esito", Types.INTEGER, null));
			command.setAsOutputParameters(paramIndex++);

			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("prgColloquio", Types.BIGINT, getPrgColloquio()));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("prgPercorso", Types.BIGINT, getPrgPercorso()));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("codiceAttivazione", Types.VARCHAR, getCodiceAttivazione()));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("cdnParUtente", Types.BIGINT, cdnParUtente));
			command.setAsInputParameters(paramIndex++);

			parameters
					.add(conn.createDataField("datAssegnazione", Types.DATE, DateUtils.convertStringToDate(dataInput)));
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

	public Integer annulla(DataConnection conn, BigDecimal cdnParUtente, String codificaMotivoAnnullamento) {

		logger.info("annulla(...) chiamato con parametri CDNUTENTE=" + cdnParUtente.toString()
				+ " codificaMotivoAnnullamento=" + codificaMotivoAnnullamento);

		DataResult dr = null;
		StoredProcedureCommand command = null;
		try {

			command = (StoredProcedureCommand) conn
					.createStoredProcedureCommand("{ call ? := PG_VOUCHER.annulla(?,?,?,?) }");

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(4);

			// Parametro di Ritorno
			parameters.add(conn.createDataField("esito", Types.INTEGER, null));
			command.setAsOutputParameters(paramIndex++);

			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("prgColloquio", Types.BIGINT, getPrgColloquio()));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("prgPercorso", Types.BIGINT, getPrgPercorso()));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("cdnParUtente", Types.BIGINT, cdnParUtente));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("codVchMotivoAnnullamento", Types.VARCHAR, codificaMotivoAnnullamento));
			command.setAsInputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);
			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
			DataField df = pdr.getPunctualDatafield();
			Integer esito = (Integer) df.getObjectValue();

			return esito;
		} catch (Throwable ex) {
			logger.error("Errore nel lancio della SP", ex);
			return -1;
		} finally {
			Utils.releaseResources(null, command, dr);
		}
	}

	public Integer proroga(DataConnection conn, Integer ggProrogaAttivazione, Integer ggProrogaChiusura,
			BigDecimal cdnParUtente, boolean skipCheckProroga) {
		DataResult dr = null;
		StoredProcedureCommand command = null;
		try {

			command = (StoredProcedureCommand) conn
					.createStoredProcedureCommand("{ call ? := PG_VOUCHER.proroga(?,?,?,?,?) }");

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(6);

			// Parametro di Ritorno
			parameters.add(conn.createDataField("esito", Types.INTEGER, null));
			command.setAsOutputParameters(paramIndex++);

			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("prgVoucher", Types.BIGINT, getPrgVoucher()));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("ggProrogaAttivazione", Types.INTEGER, ggProrogaAttivazione));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("ggProrogaChiusura", Types.INTEGER, ggProrogaChiusura));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("cdnParUtente", Types.BIGINT, cdnParUtente));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("skipCheckProroga", Types.INTEGER, skipCheckProroga ? 0 : 1));
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

	public String attivaAllaData(DataConnection conn, String cfEnte, String sedeEnte, String cfLavoratore,
			String codiceAttivazione, BigDecimal cdnParUtente, String dataInput) {
		DataResult dr = null;
		StoredProcedureCommand command = null;
		try {

			command = (StoredProcedureCommand) conn
					.createStoredProcedureCommand("{ call PG_VOUCHER.attivazioneAllaData(?,?,?,?,?,?,?,?) }");

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(8);

			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("cfEnte", Types.VARCHAR, cfEnte));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("sedeEnte", Types.VARCHAR, sedeEnte));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("cfLavoratore", Types.VARCHAR, cfLavoratore));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("codiceAttivazione", Types.VARCHAR, codiceAttivazione));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("cdnParUtente", Types.BIGINT, cdnParUtente));
			command.setAsInputParameters(paramIndex++);

			parameters
					.add(conn.createDataField("datAttivazione", Types.DATE, DateUtils.convertStringToDate(dataInput)));
			command.setAsInputParameters(paramIndex++);

			// Parametro di output
			parameters.add(conn.createDataField("errCodeOut", Types.INTEGER, null));
			command.setAsOutputParameters(paramIndex++);

			parameters.add(conn.createDataField("messaggioErr", Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);
			CompositeDataResult cdr = (CompositeDataResult) dr.getDataObject();
			List outputParams = cdr.getContainedDataResult();
			// Recupero i valori di output della stored
			// 0. errCodeOut
			PunctualDataResult pdr = (PunctualDataResult) outputParams.get(0);
			DataField df = pdr.getPunctualDatafield();
			Object errCodeOut = df.getObjectValue();

			String esito = errCodeOut.toString();

			return esito;
		} catch (Exception ex) {
			return Properties.ERRORE_GENERICO_WS;
		} finally {
			Utils.releaseResources(null, command, dr);
		}
	}

	public Integer aggiornaModalitaDurata(DataConnection conn, BigDecimal prgModalita, BigDecimal durataEffettiva,
			BigDecimal cdnParUtente) {
		DataResult dr = null;
		StoredProcedureCommand command = null;
		try {

			command = (StoredProcedureCommand) conn
					.createStoredProcedureCommand("{ call ? := PG_VOUCHER.aggiornaModalitaDurata(?,?,?,?) }");

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(5);

			// Parametro di Ritorno
			parameters.add(conn.createDataField("esito", Types.INTEGER, null));
			command.setAsOutputParameters(paramIndex++);

			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("prgVoucher", Types.BIGINT, getPrgVoucher()));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("prgModalita", Types.BIGINT, prgModalita));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("durataEffettiva", Types.NUMERIC, durataEffettiva));
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

	public Integer cancellaModalitaDurata(DataConnection conn, BigDecimal prgModalita, BigDecimal cdnParUtente) {
		DataResult dr = null;
		StoredProcedureCommand command = null;
		try {

			command = (StoredProcedureCommand) conn
					.createStoredProcedureCommand("{ call ? := PG_VOUCHER.cancellaModalitaDurata(?,?,?) }");

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(4);

			// Parametro di Ritorno
			parameters.add(conn.createDataField("esito", Types.INTEGER, null));
			command.setAsOutputParameters(paramIndex++);

			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("prgVoucher", Types.BIGINT, getPrgVoucher()));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("prgModalita", Types.BIGINT, prgModalita));
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

	public Integer chiusuraTDA(DataConnection conn, String dataChiusura, String codTipoRisultatoVoucher,
			String descRisultato, BigDecimal cdnParUtente) {
		DataResult dr = null;
		StoredProcedureCommand command = null;
		try {

			command = (StoredProcedureCommand) conn
					.createStoredProcedureCommand("{ call ? := PG_VOUCHER.chiusuraTDA(?,?,?,?,?) }");

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(6);

			// Parametro di Ritorno
			parameters.add(conn.createDataField("esito", Types.INTEGER, null));
			command.setAsOutputParameters(paramIndex++);

			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("prgVoucher", Types.BIGINT, getPrgVoucher()));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("dataChiusura", Types.VARCHAR, dataChiusura));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("codTipoRisultatoVoucher", Types.VARCHAR, codTipoRisultatoVoucher));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("descRisultato", Types.VARCHAR, descRisultato));
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

	public Integer aggiornaPagamenti(DataConnection conn, String codVchStatoPagamento, Float importoPagato,
			BigDecimal cdnParUtente) {
		DataResult dr = null;
		StoredProcedureCommand command = null;
		try {

			command = (StoredProcedureCommand) conn
					.createStoredProcedureCommand("{ call ? := PG_VOUCHER.aggiornaPagamentoTDA(?,?,?,?) }");

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(5);

			// Parametro di Ritorno
			parameters.add(conn.createDataField("esito", Types.INTEGER, null));
			command.setAsOutputParameters(paramIndex++);

			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("prgVoucher", Types.BIGINT, getPrgVoucher()));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("codVchStatoPagamento", Types.VARCHAR, codVchStatoPagamento));
			command.setAsInputParameters(paramIndex++);

			parameters.add(conn.createDataField("importoPagato", Types.FLOAT, importoPagato));
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

	public Integer riapri(DataConnection conn, BigDecimal cdnParUtente) {
		DataResult dr = null;
		StoredProcedureCommand command = null;
		try {

			command = (StoredProcedureCommand) conn
					.createStoredProcedureCommand("{ call ? := PG_VOUCHER.riapriTDA(?,?) }");

			int paramIndex = 0;
			ArrayList parameters = new ArrayList(3);

			// Parametro di Ritorno
			parameters.add(conn.createDataField("esito", Types.INTEGER, null));
			command.setAsOutputParameters(paramIndex++);

			// preparazione dei Parametri di Input
			parameters.add(conn.createDataField("prgVoucher", Types.BIGINT, getPrgVoucher()));
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

	public void lockNewNumCodiceAttivazione(TransactionQueryExecutor txExecutor) throws EMFInternalError, Exception {

		this.numAnnoCodice = DateUtils.getAnno(DateUtils.getNow());

		SourceBean rowsGenerale = (SourceBean) txExecutor.executeQuery("GET_INFO_TS_GENERALE", null, "SELECT");
		this.codProvincia = SourceBeanUtils.getAttrStrNotNull(rowsGenerale, "ROW.CODPROVINCIASIL");

		Object[] objS = new Object[1];
		objS[0] = this.numAnnoCodice;
		SourceBean rowsS = (SourceBean) txExecutor.executeQuery("GET_KEYLOCK_NUMCODICEATTIVAZIONE", objS, "SELECT");

		if (rowsS != null && rowsS.getAttribute("ROW.NUMCODICEVCH") != null) {
			// Se trovato, recupero num codice. corrente
			this.numKeyLock = SourceBeanUtils.getAttrBigDecimal(rowsS, "ROW.NUMKLOVCHCODICE");
			this.numCodice = SourceBeanUtils.getAttrBigDecimal(rowsS, "ROW.NUMCODICEVCH");
		} else {
			// Non esiste alcun record associato a quell'anno
			// inseriamo il nuovo record con numero "1".
			Object[] objI = new Object[2];

			this.numCodice = new BigDecimal(1D);

			objI[0] = this.numCodice;
			objI[1] = this.numAnnoCodice;
			Boolean resultI = (Boolean) txExecutor.executeQuery("INSERT_AM_NUMCODICEATTIVAZIONE", objI, "INSERT");
			if (!resultI.booleanValue()) {
				throw new EMFInternalError(EMFErrorSeverity.BLOCKING,
						"inserimento INSERT_AM_NUMCODICEATTIVAZIONE non possibile");
			}

			objS = new Object[1];
			objS[0] = this.numAnnoCodice;
			rowsS = (SourceBean) txExecutor.executeQuery("GET_KEYLOCK_NUMANNO_AM_VCH_CODICE", objS, "SELECT");

			if (rowsS != null && rowsS.getAttribute("ROW.NUMKLOVCHCODICE") != null) {
				this.numKeyLock = (BigDecimal) rowsS.getAttribute("ROW.NUMKLOVCHCODICE");
			} else {
				throw new EMFInternalError(EMFErrorSeverity.BLOCKING,
						"inserimento INSERT_AM_NUMCODICEATTIVAZIONE non possibile");
			}

		}
	}

	public void unlockNewNumCodiceAttivazione(TransactionQueryExecutor txExecutor) throws EMFInternalError {
		// Aggiornamento in AM_VCH_CODICE_ATTIVAZIONE del num di codice
		// Questa operazione DEVE essere eseguita per ultima, per evitare problemi in caso di concorrenza
		Object[] obj = new Object[3];

		obj[0] = this.numCodice;
		obj[1] = this.numKeyLock.add(new BigDecimal(1D));
		obj[2] = this.numAnnoCodice;

		Boolean result = (Boolean) txExecutor.executeQuery("UPDATE_AM_NUMCODICEATTIVAZIONE", obj, "UPDATE");
		if (!result.booleanValue()) {
			throw new EMFInternalError(EMFErrorSeverity.BLOCKING,
					"Impossibile aggiornare AM_VCH_CODICE_ATTIVAZIONE, probabile concorrenza.");
		}
	}

}
