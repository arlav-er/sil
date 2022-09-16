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

import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.coop.webservices.seta.ws.input.Formazione.Partecipante;

public class LavoratoreTracciato extends SourceBean implements Comparable<LavoratoreTracciato> {

	private static final long serialVersionUID = 1L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(LavoratoreTracciato.class.getName());

	private static String QUERY_INSERT_PARTECIPANTE = "INSERT INTO TS_FLUSSO_LAVORATORE (PRGFLUSSO, STRCODICEFISCALELAV, CODCOMDOMLAV, STRINDIRIZZODOMLAV, "
			+ " CODCOMRESLAV, STRINDIRIZZORESLAV, CDNLAVORATORE) VALUES (?, ?, ?, ?, ?, ?,?)";

	private static String QUERY_PARTECIPANTE = "select prgflusso, strcodicefiscalelav, codcomdomlav, strindirizzodomlav, codcomreslav, strindirizzoreslav, cdnlavoratore"
			+ " from ts_flusso_lavoratore where prgflusso = ?";

	public LavoratoreTracciato(String cf, String codComDom, String indirizzoDom, String codComRes, String indirizzoRes,
			String cdnLavoratore) throws SourceBeanException {
		super("ROW");
		this.setAttribute("STRCODICEFISCALELAV", cf);
		this.setAttribute("CODCOMDOMLAV", codComDom);
		this.setAttribute("STRINDIRIZZODOMLAV", indirizzoDom);
		this.setAttribute("CODCOMRESLAV", codComRes);
		this.setAttribute("STRINDIRIZZORESLAV", indirizzoRes);
		this.setAttribute("CDNLAVORATORE", cdnLavoratore);
	}

	public LavoratoreTracciato(BigDecimal prgFlussoNew, Partecipante lavoratore) throws SourceBeanException {
		super("ROW");
		this.setAttribute("PRGFLUSSO", prgFlussoNew);
		this.setAttribute("STRCODICEFISCALELAV", lavoratore.getCodiceFiscale());
		this.setAttribute("CODCOMDOMLAV", lavoratore.getDomicilioCodiceCatastale());
		this.setAttribute("STRINDIRIZZODOMLAV", lavoratore.getDomicilioIndirizzo());
		this.setAttribute("CODCOMRESLAV", lavoratore.getResidenzaCodiceCatastale());
		this.setAttribute("STRINDIRIZZORESLAV", lavoratore.getResidenzaIndirizzo());
		this.setAttribute("CDNLAVORATORE", lavoratore.getCdnlavoratore());
	}

	public LavoratoreTracciato(Partecipante lavoratore) throws SourceBeanException {
		super("ROW");
		this.setAttribute("STRCODICEFISCALELAV", lavoratore.getCodiceFiscale());
		this.setAttribute("CODCOMDOMLAV", lavoratore.getDomicilioCodiceCatastale());
		this.setAttribute("STRINDIRIZZODOMLAV", lavoratore.getDomicilioIndirizzo());
		this.setAttribute("CODCOMRESLAV", lavoratore.getResidenzaCodiceCatastale());
		this.setAttribute("STRINDIRIZZORESLAV", lavoratore.getResidenzaIndirizzo());
		this.setAttribute("CDNLAVORATORE", lavoratore.getCdnlavoratore());
	}

	public LavoratoreTracciato(SourceBean sb) throws SourceBeanException {
		super(sb);
	}

	@SuppressWarnings("rawtypes")
	public LavoratoreTracciato(BigDecimal prgFlusso) throws Exception {
		super("ROWS");
		Vector partecipanti = getFlussoLavoratori(prgFlusso, null);
		if (partecipanti != null) {
			for (int iP = 0; iP < partecipanti.size(); iP++) {
				SourceBean lav = (SourceBean) partecipanti.get(iP);
				this.setAttribute(lav);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public LavoratoreTracciato(BigDecimal prgFlusso, QueryExecutorObject qExec, DataConnection dc) throws Exception {
		super("ROWS");
		Vector partecipanti = getFlussoLavoratori(prgFlusso, qExec, dc);
		if (partecipanti != null) {
			for (int iP = 0; iP < partecipanti.size(); iP++) {
				SourceBean lav = (SourceBean) partecipanti.get(iP);
				this.setAttribute(lav);
			}
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
	public static Vector getFlussoLavoratori(BigDecimal prgFlusso, TransactionQueryExecutor transExec)
			throws Exception {
		SourceBean row = null;
		Vector rows = null;
		Object params[] = new Object[1];
		params[0] = prgFlusso;
		if (transExec != null) {
			row = (SourceBean) transExec.executeQuery("GET_TRACCIATO_FLUSSO_LAV", params, "SELECT");
		} else {
			row = (SourceBean) QueryExecutor.executeQuery("GET_TRACCIATO_FLUSSO_LAV", params, "SELECT", "SIL_DATI");
		}

		if (row == null)
			throw new Exception("impossibile estrarre il flusso lavoratori");

		rows = row.getAttributeAsVector("ROW");
		return rows;
	}

	@SuppressWarnings("rawtypes")
	public static Vector getFlussoLavoratori(BigDecimal prgFlusso, QueryExecutorObject qExec, DataConnection dc)
			throws Exception {
		Vector rows = null;
		List<DataField> param = new ArrayList<DataField>();
		param.add(dc.createDataField("", Types.BIGINT, prgFlusso));
		qExec.setInputParameters(param);
		qExec.setType(QueryExecutorObject.SELECT);
		qExec.setStatement(QUERY_PARTECIPANTE);
		SourceBean row = (SourceBean) qExec.exec();

		if (row == null)
			throw new Exception("impossibile estrarre il flusso lavoratori");

		rows = row.getAttributeAsVector("ROW");
		return rows;
	}

	public boolean insertPartecipante(QueryExecutorObject qExec, DataConnection dc) throws SourceBeanException {
		List<DataField> param = new ArrayList<DataField>();

		param.add(dc.createDataField("", Types.BIGINT, this.getAttribute("PRGFLUSSO")));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("STRCODICEFISCALELAV")));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("CODCOMDOMLAV")));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("STRINDIRIZZODOMLAV")));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("CODCOMRESLAV")));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("STRINDIRIZZORESLAV")));
		param.add(dc.createDataField("", Types.BIGINT, this.getAttribute("CDNLAVORATORE")));

		qExec.setInputParameters(param);
		qExec.setType(QueryExecutorObject.UPDATE);
		qExec.setStatement(QUERY_INSERT_PARTECIPANTE);

		Object queryResUpdate = qExec.exec();
		if (queryResUpdate == null
				|| !(queryResUpdate instanceof Boolean && ((Boolean) queryResUpdate).booleanValue() == true)) {
			return false;
		}

		return true;
	}

	public int compareTo(LavoratoreTracciato obj) {
		String codComDom = obj.getAttribute("CODCOMDOMLAV") != null ? Flusso.getString(obj.getAttribute("CODCOMDOMLAV"))
				: "";
		String codComDomCurr = this.getAttribute("CODCOMDOMLAV") != null
				? Flusso.getString(this.getAttribute("CODCOMDOMLAV"))
				: "";
		String indirizzoDom = obj.getAttribute("STRINDIRIZZODOMLAV") != null
				? Flusso.getString(obj.getAttribute("STRINDIRIZZODOMLAV"))
				: "";
		String indirizzoDomCurr = this.getAttribute("STRINDIRIZZODOMLAV") != null
				? Flusso.getString(this.getAttribute("STRINDIRIZZODOMLAV"))
				: "";
		String codComRes = obj.getAttribute("CODCOMRESLAV") != null ? Flusso.getString(obj.getAttribute("CODCOMRESLAV"))
				: "";
		String codComResCurr = this.getAttribute("CODCOMRESLAV") != null
				? Flusso.getString(this.getAttribute("CODCOMRESLAV"))
				: "";
		String indirizzoRes = obj.getAttribute("STRINDIRIZZORESLAV") != null
				? Flusso.getString(obj.getAttribute("STRINDIRIZZORESLAV"))
				: "";
		String indirizzoResCurr = this.getAttribute("STRINDIRIZZORESLAV") != null
				? Flusso.getString(this.getAttribute("STRINDIRIZZORESLAV"))
				: "";
		String cdnLavoratore = obj.getAttribute("CDNLAVORATORE") != null
				? Flusso.getString(obj.getAttribute("CDNLAVORATORE"))
				: "";
		String cdnLavoratoreCurr = this.getAttribute("CDNLAVORATORE") != null
				? Flusso.getString(this.getAttribute("CDNLAVORATORE"))
				: "";

		if (!codComDom.equalsIgnoreCase(codComDomCurr) || !indirizzoDom.equalsIgnoreCase(indirizzoDomCurr)
				|| !codComRes.equalsIgnoreCase(codComResCurr) || !indirizzoRes.equalsIgnoreCase(indirizzoResCurr)
				|| !cdnLavoratore.equalsIgnoreCase(cdnLavoratoreCurr)) {
			return 1;
		}
		return 0;
	}

}