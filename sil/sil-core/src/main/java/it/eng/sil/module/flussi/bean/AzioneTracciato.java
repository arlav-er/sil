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
import it.eng.sil.coop.webservices.seta.ws.input.Formazione.Patti.ProfilingPatto.PoliticheAttive.PoliticaAttiva;

public class AzioneTracciato extends SourceBean implements Comparable<AzioneTracciato> {

	private static final long serialVersionUID = 1L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AzioneTracciato.class.getName());

	private QueryExecutorObject qExec;
	private DataConnection dc;

	private static String QUERY_INSERT_POLITICA = "INSERT INTO TS_FLUSSO_AZIONI (PRGFLUSSO, PRGPERCORSO, PRGCOLLOQUIO, PRGAZIONI, CODESITO, DATSTIMATA, "
			+ " DATEFFETTIVA, DATAVVIOAZIONE, CODTIPOLOGIADURATA, NUMYGDURATAMIN, NUMYGDURATAMAX, NUMYGDURATAEFF) "
			+ " VALUES (?, ?, ?, ?, ?, TO_DATE(?, 'DD/MM/YYYY'), TO_DATE(?, 'DD/MM/YYYY'), TO_DATE(?, 'DD/MM/YYYY'), ?, ?, ?, ?)";

	private static String QUERY_GET_AZIONE = "select prgazioni from or_percorso_concordato where prgpercorso = ? and prgcolloquio = ?";

	private static String QUERY_GET_AZIONI_FLUSSO = "select prgflusso, prgpercorso, prgcolloquio, prgazioni, codesito, to_char(datstimata, 'dd/mm/yyyy') datstimata, "
			+ " to_char(dateffettiva, 'dd/mm/yyyy') dateffettiva, to_char(datavvioazione, 'dd/mm/yyyy') datavvioazione, "
			+ " codtipologiadurata, numygduratamin, numygduratamax, numygdurataeff "
			+ " from ts_flusso_azioni where prgflusso = ?";

	public AzioneTracciato(BigDecimal prgPercorso, BigDecimal prgColloquio, String esito, String datStimata,
			String datEffettiva, String datAvvio, String tipologiaDurata, BigDecimal durataMin, BigDecimal durataMax,
			BigDecimal durataEff) throws SourceBeanException {
		super("ROW");
		this.setAttribute("PRGPERCORSO", prgPercorso);
		this.setAttribute("PRGCOLLOQUIO", prgColloquio);
		this.setAttribute("CODESITO", esito);
		this.setAttribute("DATSTIMATA", datStimata);
		this.setAttribute("DATEFFETTIVA", datEffettiva);
		this.setAttribute("DATAVVIOAZIONE", datAvvio);
		this.setAttribute("CODTIPOLOGIADURATA", tipologiaDurata);
		this.setAttribute("NUMYGDURATAMIN", durataMin);
		this.setAttribute("NUMYGDURATAMAX", durataMax);
		this.setAttribute("NUMYGDURATAEFF", durataEff);
	}

	public AzioneTracciato(BigDecimal prgFlussoNew, PoliticaAttiva politica) throws SourceBeanException {
		super("ROW");
		this.setAttribute("PRGFLUSSO", prgFlussoNew);
		this.setAttribute("PRGPERCORSO", politica.getPrgPercorso());
		this.setAttribute("PRGCOLLOQUIO", politica.getPrgColloquio());
		if (politica.getEsito() != null) {
			this.setAttribute("CODESITO", politica.getEsito());
		}
		if (politica.getDataStimataFineAttivita() != null) {
			this.setAttribute("DATSTIMATA", DateUtils.formatXMLGregorian(politica.getDataStimataFineAttivita()));
		}
		if (politica.getDataFineAttivita() != null) {
			this.setAttribute("DATEFFETTIVA", DateUtils.formatXMLGregorian(politica.getDataFineAttivita()));
		}
		if (politica.getDataAvvioAttivita() != null) {
			this.setAttribute("DATAVVIOAZIONE", DateUtils.formatXMLGregorian(politica.getDataAvvioAttivita()));
		}

		if (politica.getTipologiaDurata() != null) {
			this.setAttribute("CODTIPOLOGIADURATA", politica.getTipologiaDurata());
		}
		if (politica.getDurataMinima() != null) {
			this.setAttribute("NUMYGDURATAMIN", politica.getDurataMinima());
		}
		if (politica.getDurataMassima() != null) {
			this.setAttribute("NUMYGDURATAMAX", politica.getDurataMassima());
		}
		if (politica.getDurataEffettiva() != null) {
			this.setAttribute("NUMYGDURATAEFF", politica.getDurataEffettiva());
		}
	}

	public AzioneTracciato(PoliticaAttiva politica) throws SourceBeanException {
		super("ROW");
		this.setAttribute("PRGPERCORSO", politica.getPrgPercorso());
		this.setAttribute("PRGCOLLOQUIO", politica.getPrgColloquio());
		if (politica.getEsito() != null) {
			this.setAttribute("CODESITO", politica.getEsito());
		}
		if (politica.getDataStimataFineAttivita() != null) {
			this.setAttribute("DATSTIMATA", DateUtils.formatXMLGregorian(politica.getDataStimataFineAttivita()));
		}
		if (politica.getDataFineAttivita() != null) {
			this.setAttribute("DATEFFETTIVA", DateUtils.formatXMLGregorian(politica.getDataFineAttivita()));
		}
		if (politica.getDataAvvioAttivita() != null) {
			this.setAttribute("DATAVVIOAZIONE", DateUtils.formatXMLGregorian(politica.getDataAvvioAttivita()));
		}

		if (politica.getTipologiaDurata() != null) {
			this.setAttribute("CODTIPOLOGIADURATA", politica.getTipologiaDurata());
		}
		if (politica.getDurataMinima() != null) {
			this.setAttribute("NUMYGDURATAMIN", politica.getDurataMinima());
		}
		if (politica.getDurataMassima() != null) {
			this.setAttribute("NUMYGDURATAMAX", politica.getDurataMassima());
		}
		if (politica.getDurataEffettiva() != null) {
			this.setAttribute("NUMYGDURATAEFF", politica.getDurataEffettiva());
		}
	}

	@SuppressWarnings("rawtypes")
	public AzioneTracciato(BigDecimal prgFlusso) throws Exception {
		super("ROWS");
		Vector azioni = getFlussoAzioni(prgFlusso, null);
		if (azioni != null) {
			for (int i = 0; i < azioni.size(); i++) {
				SourceBean az = (SourceBean) azioni.get(i);
				this.setAttribute(az);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public AzioneTracciato(BigDecimal prgFlusso, QueryExecutorObject qExec, DataConnection dc) throws Exception {
		super("ROWS");
		this.qExec = qExec;
		this.dc = dc;
		Vector azioni = getFlussoAzioni(prgFlusso, qExec, dc);
		if (azioni != null) {
			for (int i = 0; i < azioni.size(); i++) {
				SourceBean az = (SourceBean) azioni.get(i);
				this.setAttribute(az);
			}
		}
	}

	public AzioneTracciato(SourceBean sb) throws SourceBeanException {
		super(sb);
	}

	public SourceBean getSource() {
		try {
			return this;
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	public static Vector getFlussoAzioni(BigDecimal prgFlusso, TransactionQueryExecutor transExec) throws Exception {
		SourceBean row = null;
		Vector rows = null;
		Object params[] = new Object[1];
		params[0] = prgFlusso;
		if (transExec != null) {
			row = (SourceBean) transExec.executeQuery("GET_TRACCIATO_FLUSSO_AZ", params, "SELECT");
		} else {
			row = (SourceBean) QueryExecutor.executeQuery("GET_TRACCIATO_FLUSSO_AZ", params, "SELECT", "SIL_DATI");
		}

		if (row == null)
			throw new Exception("impossibile estrarre il flusso azioni");

		rows = row.getAttributeAsVector("ROW");
		return rows;
	}

	@SuppressWarnings("rawtypes")
	public static Vector getFlussoAzioni(BigDecimal prgFlusso, QueryExecutorObject qExec, DataConnection dc)
			throws Exception {
		Vector rows = null;
		List<DataField> param = new ArrayList<DataField>();
		param.add(dc.createDataField("", Types.BIGINT, prgFlusso));
		qExec.setInputParameters(param);
		qExec.setType(QueryExecutorObject.SELECT);
		qExec.setStatement(QUERY_GET_AZIONI_FLUSSO);
		SourceBean row = (SourceBean) qExec.exec();

		if (row == null)
			throw new Exception("impossibile estrarre il flusso azioni");

		rows = row.getAttributeAsVector("ROW");
		return rows;
	}

	public boolean insertPolitica(QueryExecutorObject qExec, DataConnection dc) throws SourceBeanException {
		List<DataField> paramAz = new ArrayList<DataField>();
		paramAz.add(dc.createDataField("", Types.BIGINT, this.getAttribute("PRGPERCORSO")));
		paramAz.add(dc.createDataField("", Types.BIGINT, this.getAttribute("PRGCOLLOQUIO")));
		qExec.setInputParameters(paramAz);
		qExec.setType(QueryExecutorObject.SELECT);
		qExec.setStatement(QUERY_GET_AZIONE);
		SourceBean esitoSB = (SourceBean) qExec.exec();
		BigDecimal prgAzione = (BigDecimal) esitoSB.getAttribute("ROW.PRGAZIONI");

		List<DataField> param = new ArrayList<DataField>();

		param.add(dc.createDataField("", Types.BIGINT, this.getAttribute("PRGFLUSSO")));
		param.add(dc.createDataField("", Types.BIGINT, this.getAttribute("PRGPERCORSO")));
		param.add(dc.createDataField("", Types.BIGINT, this.getAttribute("PRGCOLLOQUIO")));
		param.add(dc.createDataField("", Types.BIGINT, prgAzione));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("CODESITO")));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("DATSTIMATA")));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("DATEFFETTIVA")));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("DATAVVIOAZIONE")));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("CODTIPOLOGIADURATA")));
		param.add(dc.createDataField("", Types.BIGINT, this.getAttribute("NUMYGDURATAMIN")));
		param.add(dc.createDataField("", Types.BIGINT, this.getAttribute("NUMYGDURATAMAX")));
		param.add(dc.createDataField("", Types.BIGINT, this.getAttribute("NUMYGDURATAEFF")));
		qExec.setInputParameters(param);
		qExec.setType(QueryExecutorObject.UPDATE);
		qExec.setStatement(QUERY_INSERT_POLITICA);

		Object queryResUpdate = qExec.exec();
		if (queryResUpdate == null
				|| !(queryResUpdate instanceof Boolean && ((Boolean) queryResUpdate).booleanValue() == true)) {
			return false;
		}

		return true;
	}

	public int compareTo(AzioneTracciato obj) {

		BigDecimal prgPercorso = obj.getAttribute("PRGPERCORSO") != null
				? Flusso.getBigDecimal(obj.getAttribute("PRGPERCORSO"))
				: new BigDecimal("0");
		BigDecimal prgPercorsoCurr = this.getAttribute("PRGPERCORSO") != null
				? Flusso.getBigDecimal(this.getAttribute("PRGPERCORSO"))
				: new BigDecimal("0");
		BigDecimal prgColloquio = obj.getAttribute("PRGCOLLOQUIO") != null
				? Flusso.getBigDecimal(obj.getAttribute("PRGCOLLOQUIO"))
				: new BigDecimal("0");
		BigDecimal prgColloquioCurr = this.getAttribute("PRGCOLLOQUIO") != null
				? Flusso.getBigDecimal(this.getAttribute("PRGCOLLOQUIO"))
				: new BigDecimal("0");
		BigDecimal prgAzioni = new BigDecimal("0");
		List<DataField> param = new ArrayList<DataField>();
		param.add(dc.createDataField("", Types.BIGINT, prgPercorso));
		param.add(dc.createDataField("", Types.BIGINT, prgColloquio));
		qExec.setInputParameters(param);
		qExec.setType(QueryExecutorObject.SELECT);
		qExec.setStatement(QUERY_GET_AZIONE);
		SourceBean row = (SourceBean) qExec.exec();
		if (row != null) {
			row = row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row;
			// prgAzioni = (BigDecimal)row.getAttribute("PRGAZIONI");
			prgAzioni = row.getAttribute("PRGAZIONI") != null ? Flusso.getBigDecimal(row.getAttribute("PRGAZIONI"))
					: new BigDecimal("0");
		}
		BigDecimal prgAzioniCurr = this.getAttribute("PRGAZIONI") != null
				? Flusso.getBigDecimal(this.getAttribute("PRGAZIONI"))
				: new BigDecimal("0");
		String esito = obj.getAttribute("CODESITO") != null ? Flusso.getString(obj.getAttribute("CODESITO")) : "";
		String esitoCurr = this.getAttribute("CODESITO") != null ? Flusso.getString(this.getAttribute("CODESITO")) : "";
		String datStimata = obj.getAttribute("DATSTIMATA") != null ? Flusso.getString(obj.getAttribute("DATSTIMATA"))
				: "";
		String datStimataCurr = this.getAttribute("DATSTIMATA") != null
				? Flusso.getString(this.getAttribute("DATSTIMATA"))
				: "";
		String datEffettiva = obj.getAttribute("DATEFFETTIVA") != null
				? Flusso.getString(obj.getAttribute("DATEFFETTIVA"))
				: "";
		String datEffettivaCurr = this.getAttribute("DATEFFETTIVA") != null
				? Flusso.getString(this.getAttribute("DATEFFETTIVA"))
				: "";
		String datAvvioAzione = obj.getAttribute("DATAVVIOAZIONE") != null
				? Flusso.getString(obj.getAttribute("DATAVVIOAZIONE"))
				: "";
		String datAvvioAzioneCurr = this.getAttribute("DATAVVIOAZIONE") != null
				? Flusso.getString(this.getAttribute("DATAVVIOAZIONE"))
				: "";
		String tipologiaDurata = obj.getAttribute("CODTIPOLOGIADURATA") != null
				? Flusso.getString(obj.getAttribute("CODTIPOLOGIADURATA"))
				: "";
		String tipologiaDurataCurr = this.getAttribute("CODTIPOLOGIADURATA") != null
				? Flusso.getString(this.getAttribute("CODTIPOLOGIADURATA"))
				: "";
		BigDecimal durataMin = obj.getAttribute("NUMYGDURATAMIN") != null
				? Flusso.getBigDecimal(obj.getAttribute("NUMYGDURATAMIN"))
				: new BigDecimal("0");
		BigDecimal durataMinCurr = this.getAttribute("NUMYGDURATAMIN") != null
				? Flusso.getBigDecimal(this.getAttribute("NUMYGDURATAMIN"))
				: new BigDecimal("0");
		BigDecimal durataMax = obj.getAttribute("NUMYGDURATAMAX") != null
				? Flusso.getBigDecimal(obj.getAttribute("NUMYGDURATAMAX"))
				: new BigDecimal("0");
		BigDecimal durataMaxCurr = this.getAttribute("NUMYGDURATAMAX") != null
				? Flusso.getBigDecimal(this.getAttribute("NUMYGDURATAMAX"))
				: new BigDecimal("0");
		BigDecimal durataEff = obj.getAttribute("NUMYGDURATAEFF") != null
				? Flusso.getBigDecimal(obj.getAttribute("NUMYGDURATAEFF"))
				: new BigDecimal("0");
		BigDecimal durataEffCurr = this.getAttribute("NUMYGDURATAEFF") != null
				? Flusso.getBigDecimal(this.getAttribute("NUMYGDURATAEFF"))
				: new BigDecimal("0");

		if (!esito.equalsIgnoreCase(esitoCurr) || !datStimata.equalsIgnoreCase(datStimataCurr)
				|| !datEffettiva.equalsIgnoreCase(datEffettivaCurr)
				|| !datAvvioAzione.equalsIgnoreCase(datAvvioAzioneCurr)
				|| !tipologiaDurata.equalsIgnoreCase(tipologiaDurataCurr)
				|| prgPercorso.intValue() != prgPercorsoCurr.intValue()
				|| prgColloquio.intValue() != prgColloquioCurr.intValue()
				|| prgAzioni.intValue() != prgAzioniCurr.intValue() || durataMin.intValue() != durataMinCurr.intValue()
				|| durataMax.intValue() != durataMaxCurr.intValue()
				|| durataEff.intValue() != durataEffCurr.intValue()) {
			return 1;
		}
		return 0;
	}

	public QueryExecutorObject getqExec() {
		return qExec;
	}

	public void setqExec(QueryExecutorObject qExec) {
		this.qExec = qExec;
	}

	public DataConnection getDc() {
		return dc;
	}

	public void setDc(DataConnection dc) {
		this.dc = dc;
	}

}