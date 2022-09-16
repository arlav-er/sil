package it.eng.sil.module.flussi.bean;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.util.QueryExecutor;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.coop.webservices.seta.ws.input.Formazione.Patti.ProfilingPatto;

public class PattoTracciato extends SourceBean implements Comparable<PattoTracciato> {

	private static final long serialVersionUID = 1L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(PattoTracciato.class.getName());

	private static String QUERY_INSERT_PROFILING = "INSERT INTO TS_FLUSSO_PATTI (PRGFLUSSO, PRGPATTOLAVORATORE, CODTIPOPATTO, DATSTIPULA, CODSTATOATTO, DATAADESIONEGG, "
			+ " NUMINDICESVANTAGGIO2, DATRIFERIMENTO, NUMPROFILING, DATRIFERIMENTO150, CODSTATOOCCUPAZ, STRIDDOMANDADOTE, NUMPROTOCOLLO, NUMANNOPROT, DATFINE, "
			+ " CODMOTIVOFINEATTO, STRENTECODICEFISCALE) "
			+ " VALUES (?, ?, ?, TO_DATE(?, 'DD/MM/YYYY'), ?, TO_DATE(?, 'DD/MM/YYYY'), ?, TO_DATE(?, 'DD/MM/YYYY'), ?, TO_DATE(?, 'DD/MM/YYYY'), ?, ?, ?, ?, "
			+ " TO_DATE(?, 'DD/MM/YYYY'), ?, ?)";

	private static String QUERY_GET_PROFILING = "select prgflusso, prgpattolavoratore, codtipopatto, to_char(datstipula, 'dd/mm/yyyy') datstipula, "
			+ " codstatoatto, to_char(dataadesionegg, 'dd/mm/yyyy') dataadesionegg, numindicesvantaggio2, to_char(datriferimento, 'dd/mm/yyyy') datriferimento, "
			+ " numprofiling, to_char(datriferimento150, 'dd/mm/yyyy') datriferimento150, codstatooccupaz, striddomandadote, numprotocollo, "
			+ " to_char(datfine, 'dd/mm/yyyy') datfine, codmotivofineatto, strentecodicefiscale "
			+ " from ts_flusso_patti where prgflusso = ?";

	public PattoTracciato(SourceBean sb) throws SourceBeanException {
		super(sb);
	}

	@SuppressWarnings("rawtypes")
	public PattoTracciato(BigDecimal prgFlusso) throws Exception {
		super("ROWS");
		Vector profilingPatti = getFlussoPatti(prgFlusso, null);
		if (profilingPatti != null) {
			for (int iP = 0; iP < profilingPatti.size(); iP++) {
				SourceBean pt = (SourceBean) profilingPatti.get(iP);
				this.setAttribute(pt);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public PattoTracciato(BigDecimal prgFlusso, QueryExecutorObject qExec, DataConnection dc) throws Exception {
		super("ROWS");
		Vector profilingPatti = getFlussoPatti(prgFlusso, qExec, dc);
		if (profilingPatti != null) {
			for (int iP = 0; iP < profilingPatti.size(); iP++) {
				SourceBean pt = (SourceBean) profilingPatti.get(iP);
				this.setAttribute(pt);
			}
		}
	}

	public PattoTracciato(BigDecimal prgFlussoNew, ProfilingPatto patto) throws SourceBeanException {
		super("ROW");
		this.setAttribute("PRGFLUSSO", prgFlussoNew);
		this.setAttribute("PRGPATTOLAVORATORE", patto.getPrgpatto());
		this.setAttribute("CODTIPOPATTO", patto.getTipoMisuraPatto());
		this.setAttribute("DATSTIPULA", DateUtils.formatXMLGregorian(patto.getPattoData()));
		this.setAttribute("CODSTATOATTO", "PR");
		if (patto.getAdesioneGgData() != null) {
			this.setAttribute("DATAADESIONEGG", DateUtils.formatXMLGregorian(patto.getAdesioneGgData()));
		}
		if (patto.getIndiceSvantaggio() != null) {
			this.setAttribute("NUMINDICESVANTAGGIO2", patto.getIndiceSvantaggio());
		}
		if (patto.getIndiceDataRiferimento() != null) {
			this.setAttribute("DATRIFERIMENTO", DateUtils.formatXMLGregorian(patto.getIndiceDataRiferimento()));
		}
		if (patto.getProfiling150() != null) {
			this.setAttribute("NUMPROFILING", patto.getProfiling150());
		}
		if (patto.getDataRiferimento150() != null) {
			this.setAttribute("DATRIFERIMENTO150", DateUtils.formatXMLGregorian(patto.getDataRiferimento150()));
		}
		if (patto.getStatoOccupazionale() != null) {
			this.setAttribute("CODSTATOOCCUPAZ", patto.getStatoOccupazionale());
		}
		if (patto.getIdDomandaDote() != null) {
			this.setAttribute("STRIDDOMANDADOTE", patto.getIdDomandaDote());
		}
		if (patto.getPattoNumeroProtocollo() != null) {
			this.setAttribute("NUMPROTOCOLLO", patto.getPattoNumeroProtocollo());
		}
		if (patto.getDataChiusuraPatto() != null) {
			this.setAttribute("DATFINE", DateUtils.formatXMLGregorian(patto.getDataChiusuraPatto()));
		}
		if (patto.getMotivoChiusuraPatto() != null) {
			this.setAttribute("CODMOTIVOFINEATTO", patto.getMotivoChiusuraPatto());
		}
		if (patto.getCfSoggettoPromotore() != null) {
			this.setAttribute("STRENTECODICEFISCALE", patto.getCfSoggettoPromotore());
		}
	}

	public PattoTracciato(ProfilingPatto patto) throws SourceBeanException {
		super("ROW");
		this.setAttribute("PRGPATTOLAVORATORE", patto.getPrgpatto());
		this.setAttribute("CODTIPOPATTO", patto.getTipoMisuraPatto());
		this.setAttribute("DATSTIPULA", DateUtils.formatXMLGregorian(patto.getPattoData()));
		this.setAttribute("CODSTATOATTO", "PR");
		if (patto.getAdesioneGgData() != null) {
			this.setAttribute("DATAADESIONEGG", DateUtils.formatXMLGregorian(patto.getAdesioneGgData()));
		}
		if (patto.getIndiceSvantaggio() != null) {
			this.setAttribute("NUMINDICESVANTAGGIO2", patto.getIndiceSvantaggio());
		}
		if (patto.getIndiceDataRiferimento() != null) {
			this.setAttribute("DATRIFERIMENTO", DateUtils.formatXMLGregorian(patto.getIndiceDataRiferimento()));
		}
		if (patto.getProfiling150() != null) {
			this.setAttribute("NUMPROFILING", patto.getProfiling150());
		}
		if (patto.getDataRiferimento150() != null) {
			this.setAttribute("DATRIFERIMENTO150", DateUtils.formatXMLGregorian(patto.getDataRiferimento150()));
		}
		if (patto.getStatoOccupazionale() != null) {
			this.setAttribute("CODSTATOOCCUPAZ", patto.getStatoOccupazionale());
		}
		if (patto.getIdDomandaDote() != null) {
			this.setAttribute("STRIDDOMANDADOTE", patto.getIdDomandaDote());
		}
		if (patto.getPattoNumeroProtocollo() != null) {
			this.setAttribute("NUMPROTOCOLLO", patto.getPattoNumeroProtocollo());
		}
		if (patto.getDataChiusuraPatto() != null) {
			this.setAttribute("DATFINE", DateUtils.formatXMLGregorian(patto.getDataChiusuraPatto()));
		}
		if (patto.getMotivoChiusuraPatto() != null) {
			this.setAttribute("CODMOTIVOFINEATTO", patto.getMotivoChiusuraPatto());
		}
		if (patto.getCfSoggettoPromotore() != null) {
			this.setAttribute("STRENTECODICEFISCALE", patto.getCfSoggettoPromotore());
		}
	}

	public SourceBean getSource() {
		try {
			return this;
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	public static Vector getFlussoPatti(BigDecimal prgFlusso, TransactionQueryExecutor transExec) throws Exception {
		SourceBean row = null;
		Vector rows = null;
		Object params[] = new Object[1];
		params[0] = prgFlusso;
		if (transExec != null) {
			row = (SourceBean) transExec.executeQuery("GET_TRACCIATO_FLUSSO_PATTI", params, "SELECT");
		} else {
			row = (SourceBean) QueryExecutor.executeQuery("GET_TRACCIATO_FLUSSO_PATTI", params, "SELECT", "SIL_DATI");
		}

		if (row != null) {
			rows = row.getAttributeAsVector("ROW");
		}

		return rows;
	}

	@SuppressWarnings("rawtypes")
	public static Vector getFlussoPatti(BigDecimal prgFlusso, QueryExecutorObject qExec, DataConnection dc)
			throws Exception {
		Vector rows = null;
		List<DataField> param = new ArrayList<DataField>();
		param.add(dc.createDataField("", Types.BIGINT, prgFlusso));
		qExec.setInputParameters(param);
		qExec.setType(QueryExecutorObject.SELECT);
		qExec.setStatement(QUERY_GET_PROFILING);
		SourceBean row = (SourceBean) qExec.exec();

		if (row == null)
			throw new Exception("impossibile estrarre il flusso profiling");

		rows = row.getAttributeAsVector("ROW");
		return rows;
	}

	public boolean insertProfiling(QueryExecutorObject qExec, DataConnection dc) throws SourceBeanException {
		List<DataField> param = new ArrayList<DataField>();

		param.add(dc.createDataField("", Types.BIGINT, this.getAttribute("PRGFLUSSO")));
		param.add(dc.createDataField("", Types.BIGINT, this.getAttribute("PRGPATTOLAVORATORE")));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("CODTIPOPATTO")));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("DATSTIPULA")));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("CODSTATOATTO")));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("DATAADESIONEGG")));
		param.add(dc.createDataField("", Types.BIGINT, this.getAttribute("NUMINDICESVANTAGGIO2")));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("DATRIFERIMENTO")));
		param.add(dc.createDataField("", Types.BIGINT, this.getAttribute("NUMPROFILING")));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("DATRIFERIMENTO150")));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("CODSTATOOCCUPAZ")));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("STRIDDOMANDADOTE")));
		param.add(dc.createDataField("", Types.BIGINT, this.getAttribute("NUMPROTOCOLLO")));
		param.add(dc.createDataField("", Types.BIGINT, this.getAttribute("NUMANNOPROT")));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("DATFINE")));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("CODMOTIVOFINEATTO")));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("STRENTECODICEFISCALE")));
		qExec.setInputParameters(param);
		qExec.setType(QueryExecutorObject.UPDATE);
		qExec.setStatement(QUERY_INSERT_PROFILING);

		Object queryResUpdate = qExec.exec();
		if (queryResUpdate == null
				|| !(queryResUpdate instanceof Boolean && ((Boolean) queryResUpdate).booleanValue() == true)) {
			return false;
		}

		return true;
	}

	public int compareTo(PattoTracciato obj) {
		String tipoPatto = obj.getAttribute("CODTIPOPATTO") != null ? Flusso.getString(obj.getAttribute("CODTIPOPATTO"))
				: "";
		String tipoPattoCurr = this.getAttribute("CODTIPOPATTO") != null
				? Flusso.getString(this.getAttribute("CODTIPOPATTO"))
				: "";

		String statoPatto = obj.getAttribute("CODSTATOATTO") != null
				? Flusso.getString(obj.getAttribute("CODSTATOATTO"))
				: "";
		String statoPattoCurr = this.getAttribute("CODSTATOATTO") != null
				? Flusso.getString(this.getAttribute("CODSTATOATTO"))
				: "";

		String datStipula = obj.getAttribute("DATSTIPULA") != null ? Flusso.getString(obj.getAttribute("DATSTIPULA"))
				: "";
		String datStipulaCurr = this.getAttribute("DATSTIPULA") != null
				? Flusso.getString(this.getAttribute("DATSTIPULA"))
				: "";

		String dataAdesione = obj.getAttribute("DATAADESIONEGG") != null
				? Flusso.getString(obj.getAttribute("DATAADESIONEGG"))
				: "";
		String dataAdesioneCurr = this.getAttribute("DATAADESIONEGG") != null
				? Flusso.getString(this.getAttribute("DATAADESIONEGG"))
				: "";

		String datFine = obj.getAttribute("DATFINE") != null ? Flusso.getString(obj.getAttribute("DATFINE")) : "";
		String datFineCurr = this.getAttribute("DATFINE") != null ? Flusso.getString(this.getAttribute("DATFINE")) : "";

		String motivoFineAtto = obj.getAttribute("CODMOTIVOFINEATTO") != null
				? Flusso.getString(obj.getAttribute("CODMOTIVOFINEATTO"))
				: "";
		String motivoFineAttoCurr = this.getAttribute("CODMOTIVOFINEATTO") != null
				? Flusso.getString(this.getAttribute("CODMOTIVOFINEATTO"))
				: "";

		BigDecimal numProt = obj.getAttribute("NUMPROTOCOLLO") != null
				? Flusso.getBigDecimal(obj.getAttribute("NUMPROTOCOLLO"))
				: new BigDecimal("-1000");
		BigDecimal numProtCurr = this.getAttribute("NUMPROTOCOLLO") != null
				? Flusso.getBigDecimal(this.getAttribute("NUMPROTOCOLLO"))
				: new BigDecimal("-1000");

		BigDecimal numIndiceSvantaggio2 = obj.getAttribute("NUMINDICESVANTAGGIO2") != null
				? Flusso.getBigDecimal(obj.getAttribute("NUMINDICESVANTAGGIO2"))
				: new BigDecimal("-1000");
		BigDecimal numIndiceSvantaggio2Curr = this.getAttribute("NUMINDICESVANTAGGIO2") != null
				? Flusso.getBigDecimal(this.getAttribute("NUMINDICESVANTAGGIO2"))
				: new BigDecimal("-1000");

		BigDecimal numProfiling = obj.getAttribute("NUMPROFILING") != null
				? Flusso.getBigDecimal(obj.getAttribute("NUMPROFILING"))
				: new BigDecimal("-1000");
		BigDecimal numProfilingCurr = this.getAttribute("NUMPROFILING") != null
				? Flusso.getBigDecimal(this.getAttribute("NUMPROFILING"))
				: new BigDecimal("-1000");

		String datRif = obj.getAttribute("DATRIFERIMENTO") != null
				? Flusso.getString(obj.getAttribute("DATRIFERIMENTO"))
				: "";
		String datRifCurr = this.getAttribute("DATRIFERIMENTO") != null
				? Flusso.getString(this.getAttribute("DATRIFERIMENTO"))
				: "";

		String codStatoOccupaz = obj.getAttribute("CODSTATOOCCUPAZ") != null
				? Flusso.getString(obj.getAttribute("CODSTATOOCCUPAZ"))
				: "";
		String codStatoOccupazCurr = this.getAttribute("CODSTATOOCCUPAZ") != null
				? Flusso.getString(this.getAttribute("CODSTATOOCCUPAZ"))
				: "";

		String datRif150 = obj.getAttribute("DATRIFERIMENTO150") != null
				? Flusso.getString(obj.getAttribute("DATRIFERIMENTO150"))
				: "";
		String datRif150Curr = this.getAttribute("DATRIFERIMENTO150") != null
				? Flusso.getString(this.getAttribute("DATRIFERIMENTO150"))
				: "";

		String idDomandaDote = obj.getAttribute("STRIDDOMANDADOTE") != null
				? Flusso.getString(obj.getAttribute("STRIDDOMANDADOTE"))
				: "";
		String idDomandaDoteCurr = this.getAttribute("STRIDDOMANDADOTE") != null
				? Flusso.getString(this.getAttribute("STRIDDOMANDADOTE"))
				: "";

		String cfEnte = obj.getAttribute("STRENTECODICEFISCALE") != null
				? Flusso.getString(obj.getAttribute("STRENTECODICEFISCALE"))
				: "";
		String cfEnteCurr = this.getAttribute("STRENTECODICEFISCALE") != null
				? Flusso.getString(this.getAttribute("STRENTECODICEFISCALE"))
				: "";

		if (!datStipula.equalsIgnoreCase(datStipulaCurr) || !statoPatto.equalsIgnoreCase(statoPattoCurr)
				|| !dataAdesione.equalsIgnoreCase(dataAdesioneCurr) || !tipoPatto.equalsIgnoreCase(tipoPattoCurr)
				|| !datFine.equalsIgnoreCase(datFineCurr) || !datRif.equalsIgnoreCase(datRifCurr)
				|| !datRif150.equalsIgnoreCase(datRif150Curr) || !motivoFineAtto.equalsIgnoreCase(motivoFineAttoCurr)
				|| !codStatoOccupaz.equalsIgnoreCase(codStatoOccupazCurr)
				|| numProt.intValue() != numProtCurr.intValue()
				|| numIndiceSvantaggio2.intValue() != numIndiceSvantaggio2Curr.intValue()
				|| numProfiling.intValue() != numProfilingCurr.intValue()
				|| !idDomandaDote.equalsIgnoreCase(idDomandaDoteCurr) || !cfEnte.equalsIgnoreCase(cfEnteCurr)) {
			return 1;
		}
		return 0;
	}

}