package it.eng.sil.module.flussi.bean;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.util.QueryExecutor;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.utils.TransactionQueryExecutor;

public class Flusso extends SourceBean implements Comparable<Flusso> {

	private static final long serialVersionUID = 1L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(Flusso.class.getName());

	private Vector<LavoratoreTracciato> infoLavoratore = null;
	private Vector<PattoTracciato> infoPatto = null;
	private Vector<AzioneTracciato> infoAzione = null;
	private Vector<MovimentoTracciato> infoMovimento = null;

	private static String QUERY_NEXTVAL = "SELECT S_TS_FLUSSI.nextval AS KEYFLUSSO FROM dual";

	private static String QUERY_INSERT_FLUSSO = "INSERT INTO TS_FLUSSI (PRGFLUSSO, STRTIPOFLUSSO, DATINVIO, NUMPATTI, NUMMOVIMENTI, NUMAZIONI, CDNUTINS, DTMINS) "
			+ " VALUES (?, ?, TRUNC(SYSDATE), ?, ?, ?, ?, SYSDATE)";

	private static String QUERY_DELETE_FLUSSO_LAV = "DELETE FROM TS_FLUSSO_LAVORATORE WHERE PRGFLUSSO = ?";

	private static String QUERY_DELETE_FLUSSO_PATTI = "DELETE FROM TS_FLUSSO_PATTI WHERE PRGFLUSSO = ?";

	private static String QUERY_DELETE_FLUSSO_AZIONI = "DELETE FROM TS_FLUSSO_AZIONI WHERE PRGFLUSSO = ?";

	private static String QUERY_DELETE_FLUSSO_MOVIMENTI = "DELETE FROM TS_FLUSSO_MOVIMENTI WHERE PRGFLUSSO = ?";

	private static String QUERY_DELETE_FLUSSO = "DELETE FROM TS_FLUSSI WHERE PRGFLUSSO = ?";

	private static String QUERY_GET_ULTIMO_FLUSSO = "select prgflusso, strtipoflusso, numpatti, numazioni, nummovimenti, to_char(datinvio, 'dd/mm/yyyy') datinvio "
			+ " from ts_flussi where prgflusso = (select max(flusso.prgflusso) from ts_flusso_lavoratore "
			+ " inner join ts_flussi flusso on (ts_flusso_lavoratore.prgflusso = flusso.prgflusso) "
			+ " where upper(strcodicefiscalelav) = upper(?) and upper(flusso.strtipoflusso) = upper(?))";

	public Flusso(String codFiscale, String tipoFlusso) throws Exception {
		super("ROW");
		SourceBean rowFlusso = getUltimoFlussoLav(codFiscale, tipoFlusso, null);
		if (rowFlusso != null && rowFlusso.containsAttribute("PRGFLUSSO")) {
			this.setAttribute("CODICEFISCALELAV", codFiscale);
			this.setAttribute("PRGFLUSSO", (BigDecimal) rowFlusso.getAttribute("PRGFLUSSO"));
			this.setAttribute("STRTIPOFLUSSO", (String) rowFlusso.getAttribute("STRTIPOFLUSSO"));
			this.setAttribute("NUMPATTI", (BigDecimal) rowFlusso.getAttribute("NUMPATTI"));
			this.setAttribute("NUMAZIONI", (BigDecimal) rowFlusso.getAttribute("NUMAZIONI"));
			this.setAttribute("NUMMOVIMENTI", (BigDecimal) rowFlusso.getAttribute("NUMMOVIMENTI"));
			this.infoLavoratore = new Vector<LavoratoreTracciato>();
			this.infoPatto = new Vector<PattoTracciato>();
			this.infoAzione = new Vector<AzioneTracciato>();
			this.infoMovimento = new Vector<MovimentoTracciato>();
		}
	}

	public Flusso(String codFiscale, String tipoFlusso, QueryExecutorObject qExec, DataConnection dc) throws Exception {
		super("ROW");
		SourceBean rowFlusso = getUltimoFlussoLav(codFiscale, tipoFlusso, qExec, dc);
		if (rowFlusso != null && rowFlusso.containsAttribute("PRGFLUSSO")) {
			this.setAttribute("CODICEFISCALELAV", codFiscale);
			this.setAttribute("PRGFLUSSO", (BigDecimal) rowFlusso.getAttribute("PRGFLUSSO"));
			this.setAttribute("STRTIPOFLUSSO", (String) rowFlusso.getAttribute("STRTIPOFLUSSO"));
			this.setAttribute("NUMPATTI", (BigDecimal) rowFlusso.getAttribute("NUMPATTI"));
			this.setAttribute("NUMAZIONI", (BigDecimal) rowFlusso.getAttribute("NUMAZIONI"));
			this.setAttribute("NUMMOVIMENTI", (BigDecimal) rowFlusso.getAttribute("NUMMOVIMENTI"));
			this.infoLavoratore = new Vector<LavoratoreTracciato>();
			this.infoPatto = new Vector<PattoTracciato>();
			this.infoAzione = new Vector<AzioneTracciato>();
			this.infoMovimento = new Vector<MovimentoTracciato>();
		}
	}

	public Flusso(BigDecimal prgFlussoNew, String tipoFlusso, BigDecimal numPattiNew, BigDecimal numAzioniNew,
			BigDecimal numMovimentiNew) throws SourceBeanException {
		super("ROW");
		this.setAttribute("PRGFLUSSO", prgFlussoNew);
		this.setAttribute("STRTIPOFLUSSO", tipoFlusso);
		this.setAttribute("NUMPATTI", numPattiNew);
		this.setAttribute("NUMAZIONI", numAzioniNew);
		this.setAttribute("NUMMOVIMENTI", numMovimentiNew);
		this.infoLavoratore = new Vector<LavoratoreTracciato>();
		this.infoPatto = new Vector<PattoTracciato>();
		this.infoAzione = new Vector<AzioneTracciato>();
		this.infoMovimento = new Vector<MovimentoTracciato>();
	}

	public Flusso(String codFiscale, String tipoFlusso, int numPattiNew, int numAzioniNew, int numMovimentiNew)
			throws SourceBeanException {
		super("ROW");
		this.setAttribute("CODICEFISCALELAV", codFiscale);
		this.setAttribute("STRTIPOFLUSSO", tipoFlusso);
		this.setAttribute("NUMPATTI", numPattiNew);
		this.setAttribute("NUMAZIONI", numAzioniNew);
		this.setAttribute("NUMMOVIMENTI", numMovimentiNew);
		this.infoLavoratore = new Vector<LavoratoreTracciato>();
		this.infoPatto = new Vector<PattoTracciato>();
		this.infoAzione = new Vector<AzioneTracciato>();
		this.infoMovimento = new Vector<MovimentoTracciato>();
	}

	public Flusso(SourceBean sb) throws SourceBeanException {
		super(sb);
		this.infoLavoratore = new Vector<LavoratoreTracciato>();
		this.infoPatto = new Vector<PattoTracciato>();
		this.infoAzione = new Vector<AzioneTracciato>();
		this.infoMovimento = new Vector<MovimentoTracciato>();
	}

	public SourceBean getSource() {
		try {
			return this;
		} catch (Exception e) {
			return null;
		}
	}

	public void setInfoLavoratore(LavoratoreTracciato lav) {
		infoLavoratore.add(lav);
	}

	public void setInfoPatto(PattoTracciato patto) {
		infoPatto.add(patto);
	}

	public void setInfoAzione(AzioneTracciato azione) {
		infoAzione.add(azione);
	}

	public void setInfoMovimento(MovimentoTracciato mov) {
		infoMovimento.add(mov);
	}

	public Vector<PattoTracciato> getInfoPatto() {
		return infoPatto;
	}

	public Vector<MovimentoTracciato> getInfoMovimento() {
		return infoMovimento;
	}

	public Vector<LavoratoreTracciato> getInfoLavoratore() {
		return infoLavoratore;
	}

	public Vector<AzioneTracciato> getInfoAzione() {
		return infoAzione;
	}

	public static SourceBean getFlusso(BigDecimal prgFlusso, TransactionQueryExecutor transExec) throws Exception {
		SourceBean row = null;
		Object params[] = new Object[1];
		params[0] = prgFlusso;
		if (transExec != null) {
			row = (SourceBean) transExec.executeQuery("GET_TRACCIATO_FLUSSO", params, "SELECT");
		} else {
			row = (SourceBean) QueryExecutor.executeQuery("GET_TRACCIATO_FLUSSO", params, "SELECT", "SIL_DATI");
		}

		if (row == null)
			throw new Exception("impossibile estrarre il flusso");
		return row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row;
	}

	public SourceBean getUltimoFlussoLav(String codFiscale, String tipoFlusso, TransactionQueryExecutor transExec)
			throws Exception {
		SourceBean row = null;
		Object params[] = new Object[2];
		params[0] = codFiscale;
		params[1] = tipoFlusso;
		if (transExec != null) {
			row = (SourceBean) transExec.executeQuery("GET_ULTIMO_TRACCIATO_FLUSSO_LAVORATORE", params, "SELECT");
		} else {
			row = (SourceBean) QueryExecutor.executeQuery("GET_ULTIMO_TRACCIATO_FLUSSO_LAVORATORE", params, "SELECT",
					"SIL_DATI");
		}

		if (row == null)
			throw new Exception("impossibile estrarre l'ultimo flusso lavoratore");
		return row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row;
	}

	public SourceBean getUltimoFlussoLav(String codFiscale, String tipoFlusso, QueryExecutorObject qExec,
			DataConnection dc) throws Exception {
		List<DataField> param = new ArrayList<DataField>();
		param.add(dc.createDataField("", Types.VARCHAR, codFiscale));
		param.add(dc.createDataField("", Types.VARCHAR, tipoFlusso));
		qExec.setInputParameters(param);
		qExec.setType(QueryExecutorObject.SELECT);
		qExec.setStatement(QUERY_GET_ULTIMO_FLUSSO);
		SourceBean row = (SourceBean) qExec.exec();
		if (row == null)
			throw new Exception("impossibile estrarre l'ultimo flusso lavoratore");
		return row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row;
	}

	public boolean esisteUltimoFlusso() {
		return this.containsAttribute("PRGFLUSSO");
	}

	public boolean insertFlusso(QueryExecutorObject qExec, DataConnection dc) throws SourceBeanException {
		BigDecimal prgNewFlusso = null;
		List<DataField> paramsVoid = new ArrayList<DataField>();
		qExec.setInputParameters(paramsVoid);
		qExec.setType(QueryExecutorObject.SELECT);
		qExec.setStatement(QUERY_NEXTVAL);
		SourceBean row = (SourceBean) qExec.exec();
		if (row != null) {
			row = row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row;
			prgNewFlusso = (BigDecimal) row.getAttribute("KEYFLUSSO");
		}
		this.setAttribute("PRGFLUSSO", prgNewFlusso);
		List<DataField> param = new ArrayList<DataField>();
		param.add(dc.createDataField("", Types.BIGINT, prgNewFlusso));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("STRTIPOFLUSSO")));
		param.add(dc.createDataField("", Types.BIGINT, this.getAttribute("NUMPATTI")));
		param.add(dc.createDataField("", Types.BIGINT, this.getAttribute("NUMMOVIMENTI")));
		param.add(dc.createDataField("", Types.BIGINT, this.getAttribute("NUMAZIONI")));
		param.add(dc.createDataField("", Types.BIGINT, it.eng.sil.coop.webservices.utils.Utils.USER_BATCH));
		qExec.setInputParameters(param);
		qExec.setType(QueryExecutorObject.UPDATE);
		qExec.setStatement(QUERY_INSERT_FLUSSO);

		Object queryResUpdate = qExec.exec();
		if (queryResUpdate == null
				|| !(queryResUpdate instanceof Boolean && ((Boolean) queryResUpdate).booleanValue() == true)) {
			return false;
		}

		return true;
	}

	public boolean deleteFlusso(BigDecimal prgFlusso, QueryExecutorObject qExec, DataConnection dc) {
		List<DataField> param = new ArrayList<DataField>();
		param = new ArrayList<DataField>();
		param.add(dc.createDataField("", Types.BIGINT, prgFlusso));
		qExec.setInputParameters(param);
		qExec.setType(QueryExecutorObject.DELETE);
		qExec.setStatement(QUERY_DELETE_FLUSSO_LAV);

		Object queryResDel = qExec.exec();
		if (queryResDel == null
				|| !(queryResDel instanceof Boolean && ((Boolean) queryResDel).booleanValue() == true)) {
			return false;
		}

		qExec.setStatement(QUERY_DELETE_FLUSSO_PATTI);

		queryResDel = qExec.exec();
		if (queryResDel == null
				|| !(queryResDel instanceof Boolean && ((Boolean) queryResDel).booleanValue() == true)) {
			return false;
		}

		qExec.setStatement(QUERY_DELETE_FLUSSO_AZIONI);

		queryResDel = qExec.exec();
		if (queryResDel == null
				|| !(queryResDel instanceof Boolean && ((Boolean) queryResDel).booleanValue() == true)) {
			return false;
		}

		qExec.setStatement(QUERY_DELETE_FLUSSO_MOVIMENTI);

		queryResDel = qExec.exec();
		if (queryResDel == null
				|| !(queryResDel instanceof Boolean && ((Boolean) queryResDel).booleanValue() == true)) {
			return false;
		}

		qExec.setStatement(QUERY_DELETE_FLUSSO);

		queryResDel = qExec.exec();
		if (queryResDel == null
				|| !(queryResDel instanceof Boolean && ((Boolean) queryResDel).booleanValue() == true)) {
			return false;
		}

		return true;
	}

	public int compareTo(Flusso obj) {
		String tipoFlusso = obj.getAttribute("STRTIPOFLUSSO") != null ? (String) obj.getAttribute("STRTIPOFLUSSO") : "";
		String tipoFlussoCurr = this.getAttribute("STRTIPOFLUSSO") != null ? (String) this.getAttribute("STRTIPOFLUSSO")
				: "";
		BigDecimal numPatti = obj.getAttribute("NUMPATTI") != null ? Flusso.getBigDecimal(obj.getAttribute("NUMPATTI"))
				: new BigDecimal(0);
		BigDecimal numPattiCurr = this.getAttribute("NUMPATTI") != null
				? Flusso.getBigDecimal(this.getAttribute("NUMPATTI"))
				: new BigDecimal(0);
		BigDecimal numAz = obj.getAttribute("NUMAZIONI") != null ? Flusso.getBigDecimal(obj.getAttribute("NUMAZIONI"))
				: new BigDecimal(0);
		BigDecimal numAzCurr = this.getAttribute("NUMAZIONI") != null
				? Flusso.getBigDecimal(this.getAttribute("NUMAZIONI"))
				: new BigDecimal(0);
		BigDecimal numMov = obj.getAttribute("NUMMOVIMENTI") != null
				? Flusso.getBigDecimal(obj.getAttribute("NUMMOVIMENTI"))
				: new BigDecimal(0);
		BigDecimal numMovCurr = this.getAttribute("NUMMOVIMENTI") != null
				? Flusso.getBigDecimal(this.getAttribute("NUMMOVIMENTI"))
				: new BigDecimal(0);

		if (!tipoFlusso.equalsIgnoreCase(tipoFlussoCurr) || numPatti.intValue() != numPattiCurr.intValue()
				|| numAz.intValue() != numAzCurr.intValue() || numMov.intValue() != numMovCurr.intValue()) {
			return 1;
		}
		return 0;
	}

	public static BigDecimal getBigDecimal(Object value) {
		BigDecimal ret = null;
		if (value != null) {
			if (value instanceof BigDecimal) {
				ret = (BigDecimal) value;
			} else if (value instanceof String) {
				ret = new BigDecimal((String) value);
			} else if (value instanceof Number) {
				ret = new BigDecimal(((Number) value).intValue());
			} else {
				throw new ClassCastException("Not possible to coerce [" + value + "] from class " + value.getClass()
						+ " into a BigDecimal.");
			}
		}
		return ret;
	}

	public static String getString(Object value) {
		String ret = null;
		if (value != null) {
			if (value instanceof String) {
				ret = (String) value;
			} else if (value instanceof Integer) {
				ret = (Integer) value + "";
			} else if (value instanceof BigInteger) {
				ret = (BigInteger) value + "";
			} else if (value instanceof BigDecimal) {
				ret = String.valueOf(((BigDecimal) value).intValue());
			} else if (value instanceof Date) {
				DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				ret = df.format((Date) value);
			} else {
				throw new ClassCastException(
						"Not possible to coerce [" + value + "] from class " + value.getClass() + " into a String.");
			}
		}
		return ret;
	}

}