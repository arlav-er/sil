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
import it.eng.sil.coop.webservices.seta.ws.input.Formazione.ComunicazioniObbligatorie.ComunicazioneObbligatoria;

public class MovimentoTracciato extends SourceBean implements Comparable<MovimentoTracciato> {

	private static final long serialVersionUID = 1L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MovimentoTracciato.class.getName());

	private static String QUERY_INSERT_CO = "INSERT INTO TS_FLUSSO_MOVIMENTI (PRGFLUSSO, PRGMOVIMENTO, CODTIPOMOV, DATINIZIO, DATFINE, CODSTATOATTO, "
			+ " CODCOMUNICAZIONE, PRGMOVIMENTORETT, DATORECODICEFISCALE, UTILIZZATORECODICEFISCALE, CODMANSIONEDOT, CODTIPOCONTRATTO, CODCOMLAVORO, CODORARIO) "
			+ " VALUES (?, ?, ?, TO_DATE(?, 'DD/MM/YYYY'), TO_DATE(?, 'DD/MM/YYYY'), ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static String QUERY_MOVIMENTI = "select prgflusso, prgmovimento, codtipomov, to_char(datinizio, 'dd/mm/yyyy') datinizio, "
			+ " to_char(datfine, 'dd/mm/yyyy') datfine, codstatoatto, codcomunicazione, prgmovimentorett, datorecodicefiscale, utilizzatorecodicefiscale, "
			+ " codmansionedot, codtipocontratto, codcomlavoro, codorario "
			+ " from ts_flusso_movimenti where prgflusso = ?";

	public MovimentoTracciato(BigDecimal prgMovimento, String tipoMov, String datInizio, String datFine,
			String statoAtto, String codiceComunicazione) throws SourceBeanException {
		super("ROW");
		this.setAttribute("PRGMOVIMENTO", prgMovimento);
		this.setAttribute("CODTIPOMOV", tipoMov);
		this.setAttribute("DATINIZIO", datInizio);
		this.setAttribute("DATFINE", datFine);
		this.setAttribute("CODSTATOATTO", statoAtto);
		this.setAttribute("CODCOMUNICAZIONE", codiceComunicazione);
	}

	public MovimentoTracciato(BigDecimal prgFlussoNew, ComunicazioneObbligatoria movimento) throws SourceBeanException {
		super("ROW");
		this.setAttribute("PRGFLUSSO", prgFlussoNew);
		this.setAttribute("PRGMOVIMENTO", movimento.getPrgMovimentoSil());
		this.setAttribute("CODTIPOMOV", movimento.getTipoMovimento());
		this.setAttribute("DATINIZIO", DateUtils.formatXMLGregorian(movimento.getDataInizio()));
		if (movimento.getDataFine() != null) {
			this.setAttribute("DATFINE", DateUtils.formatXMLGregorian(movimento.getDataFine()));
		}
		if (movimento.getStatoMovimento() != null) {
			this.setAttribute("CODSTATOATTO", movimento.getStatoMovimento());
		}
		if (movimento.getCodiceComunicazioneAvviamento() != null) {
			this.setAttribute("CODCOMUNICAZIONE", movimento.getCodiceComunicazioneAvviamento());
		}
		if (movimento.getPrgMovimentoSilRett() != null) {
			this.setAttribute("PRGMOVIMENTORETT", movimento.getPrgMovimentoSilRett());
		}
		if (movimento.getDatoreLavoroCodiceFiscale() != null) {
			this.setAttribute("DATORECODICEFISCALE", movimento.getDatoreLavoroCodiceFiscale());
		}
		if (movimento.getUtilizzatoreCodiceFiscale() != null) {
			this.setAttribute("UTILIZZATORECODICEFISCALE", movimento.getUtilizzatoreCodiceFiscale());
		}
		if (movimento.getQualificaProfessionale() != null) {
			this.setAttribute("CODMANSIONEDOT", movimento.getQualificaProfessionale());
		}
		if (movimento.getTipoContratto() != null) {
			this.setAttribute("CODTIPOCONTRATTO", movimento.getTipoContratto());
		}
		if (movimento.getSedeLavoroCodiceCatastale() != null) {
			this.setAttribute("CODCOMLAVORO", movimento.getSedeLavoroCodiceCatastale());
		}
		if (movimento.getModalitaLavoro() != null) {
			this.setAttribute("CODORARIO", movimento.getModalitaLavoro());
		}
	}

	public MovimentoTracciato(ComunicazioneObbligatoria movimento) throws SourceBeanException {
		super("ROW");
		this.setAttribute("PRGMOVIMENTO", movimento.getPrgMovimentoSil());
		this.setAttribute("CODTIPOMOV", movimento.getTipoMovimento());
		this.setAttribute("DATINIZIO", DateUtils.formatXMLGregorian(movimento.getDataInizio()));
		if (movimento.getDataFine() != null) {
			this.setAttribute("DATFINE", DateUtils.formatXMLGregorian(movimento.getDataFine()));
		}
		if (movimento.getStatoMovimento() != null) {
			this.setAttribute("CODSTATOATTO", movimento.getStatoMovimento());
		}
		if (movimento.getCodiceComunicazioneAvviamento() != null) {
			this.setAttribute("CODCOMUNICAZIONE", movimento.getCodiceComunicazioneAvviamento());
		}
		if (movimento.getPrgMovimentoSilRett() != null) {
			this.setAttribute("PRGMOVIMENTORETT", movimento.getPrgMovimentoSilRett());
		}
		if (movimento.getDatoreLavoroCodiceFiscale() != null) {
			this.setAttribute("DATORECODICEFISCALE", movimento.getDatoreLavoroCodiceFiscale());
		}
		if (movimento.getUtilizzatoreCodiceFiscale() != null) {
			this.setAttribute("UTILIZZATORECODICEFISCALE", movimento.getUtilizzatoreCodiceFiscale());
		}
		if (movimento.getQualificaProfessionale() != null) {
			this.setAttribute("CODMANSIONEDOT", movimento.getQualificaProfessionale());
		}
		if (movimento.getTipoContratto() != null) {
			this.setAttribute("CODTIPOCONTRATTO", movimento.getTipoContratto());
		}
		if (movimento.getSedeLavoroCodiceCatastale() != null) {
			this.setAttribute("CODCOMLAVORO", movimento.getSedeLavoroCodiceCatastale());
		}
		if (movimento.getModalitaLavoro() != null) {
			this.setAttribute("CODORARIO", movimento.getModalitaLavoro());
		}
	}

	@SuppressWarnings("rawtypes")
	public MovimentoTracciato(BigDecimal prgFlusso) throws Exception {
		super("ROWS");
		Vector movimenti = getFlussoMovimenti(prgFlusso, null);
		if (movimenti != null) {
			for (int i = 0; i < movimenti.size(); i++) {
				SourceBean mov = (SourceBean) movimenti.get(i);
				this.setAttribute(mov);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public MovimentoTracciato(BigDecimal prgFlusso, QueryExecutorObject qExec, DataConnection dc) throws Exception {
		super("ROWS");
		Vector movimenti = getFlussoMovimenti(prgFlusso, qExec, dc);
		if (movimenti != null) {
			for (int i = 0; i < movimenti.size(); i++) {
				SourceBean mov = (SourceBean) movimenti.get(i);
				this.setAttribute(mov);
			}
		}
	}

	public MovimentoTracciato(SourceBean sb) throws SourceBeanException {
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
	public static Vector getFlussoMovimenti(BigDecimal prgFlusso, TransactionQueryExecutor transExec) throws Exception {
		SourceBean row = null;
		Vector rows = null;
		Object params[] = new Object[1];
		params[0] = prgFlusso;
		if (transExec != null) {
			row = (SourceBean) transExec.executeQuery("GET_TRACCIATO_FLUSSO_MOV", params, "SELECT");
		} else {
			row = (SourceBean) QueryExecutor.executeQuery("GET_TRACCIATO_FLUSSO_MOV", params, "SELECT", "SIL_DATI");
		}

		if (row == null)
			throw new Exception("impossibile estrarre il flusso movimenti");

		rows = row.getAttributeAsVector("ROW");
		return rows;
	}

	@SuppressWarnings("rawtypes")
	public static Vector getFlussoMovimenti(BigDecimal prgFlusso, QueryExecutorObject qExec, DataConnection dc)
			throws Exception {
		Vector rows = null;
		List<DataField> param = new ArrayList<DataField>();
		param.add(dc.createDataField("", Types.BIGINT, prgFlusso));
		qExec.setInputParameters(param);
		qExec.setType(QueryExecutorObject.SELECT);
		qExec.setStatement(QUERY_MOVIMENTI);
		SourceBean row = (SourceBean) qExec.exec();

		if (row == null)
			throw new Exception("impossibile estrarre il flusso movimenti");

		rows = row.getAttributeAsVector("ROW");
		return rows;
	}

	public boolean insertCO(QueryExecutorObject qExec, DataConnection dc) throws SourceBeanException {
		List<DataField> param = new ArrayList<DataField>();

		param.add(dc.createDataField("", Types.BIGINT, this.getAttribute("PRGFLUSSO")));
		param.add(dc.createDataField("", Types.BIGINT, this.getAttribute("PRGMOVIMENTO")));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("CODTIPOMOV")));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("DATINIZIO")));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("DATFINE")));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("CODSTATOATTO")));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("CODCOMUNICAZIONE")));
		param.add(dc.createDataField("", Types.BIGINT, this.getAttribute("PRGMOVIMENTORETT")));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("DATORECODICEFISCALE")));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("UTILIZZATORECODICEFISCALE")));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("CODMANSIONEDOT")));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("CODTIPOCONTRATTO")));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("CODCOMLAVORO")));
		param.add(dc.createDataField("", Types.VARCHAR, this.getAttribute("CODORARIO")));
		qExec.setInputParameters(param);
		qExec.setType(QueryExecutorObject.UPDATE);
		qExec.setStatement(QUERY_INSERT_CO);

		Object queryResUpdate = qExec.exec();
		if (queryResUpdate == null
				|| !(queryResUpdate instanceof Boolean && ((Boolean) queryResUpdate).booleanValue() == true)) {
			return false;
		}

		return true;
	}

	public int compareTo(MovimentoTracciato obj) {
		BigDecimal prgMovimento = obj.getAttribute("PRGMOVIMENTO") != null
				? Flusso.getBigDecimal(obj.getAttribute("PRGMOVIMENTO"))
				: new BigDecimal("-1");
		BigDecimal prgMovimentoCurr = this.getAttribute("PRGMOVIMENTO") != null
				? Flusso.getBigDecimal(this.getAttribute("PRGMOVIMENTO"))
				: new BigDecimal("-1");
		BigDecimal prgMovimentoRett = obj.getAttribute("PRGMOVIMENTORETT") != null
				? Flusso.getBigDecimal(obj.getAttribute("PRGMOVIMENTORETT"))
				: new BigDecimal("-1");
		BigDecimal prgMovimentoRettCurr = this.getAttribute("PRGMOVIMENTORETT") != null
				? Flusso.getBigDecimal(this.getAttribute("PRGMOVIMENTORETT"))
				: new BigDecimal("-1");
		String codTipoMov = obj.getAttribute("CODTIPOMOV") != null ? Flusso.getString(obj.getAttribute("CODTIPOMOV"))
				: "";
		String codTipoMovCurr = this.getAttribute("CODTIPOMOV") != null
				? Flusso.getString(this.getAttribute("CODTIPOMOV"))
				: "";
		String datInizio = obj.getAttribute("DATINIZIO") != null ? Flusso.getString(obj.getAttribute("DATINIZIO")) : "";
		String datInizioCurr = this.getAttribute("DATINIZIO") != null ? Flusso.getString(this.getAttribute("DATINIZIO"))
				: "";
		String datFine = obj.getAttribute("DATFINE") != null ? Flusso.getString(obj.getAttribute("DATFINE")) : "";
		String datFineCurr = this.getAttribute("DATFINE") != null ? Flusso.getString(this.getAttribute("DATFINE")) : "";
		String statoAtto = obj.getAttribute("CODSTATOATTO") != null ? Flusso.getString(obj.getAttribute("CODSTATOATTO"))
				: "";
		String statoAttoCurr = this.getAttribute("CODSTATOATTO") != null
				? Flusso.getString(this.getAttribute("CODSTATOATTO"))
				: "";
		String codiceComunicazione = obj.getAttribute("CODCOMUNICAZIONE") != null
				? Flusso.getString(obj.getAttribute("CODCOMUNICAZIONE"))
				: "";
		String codiceComunicazioneCurr = this.getAttribute("CODCOMUNICAZIONE") != null
				? Flusso.getString(this.getAttribute("CODCOMUNICAZIONE"))
				: "";
		String datoreLavoro = obj.getAttribute("DATORECODICEFISCALE") != null
				? Flusso.getString(obj.getAttribute("DATORECODICEFISCALE"))
				: "";
		String datoreLavoroCurr = this.getAttribute("DATORECODICEFISCALE") != null
				? Flusso.getString(this.getAttribute("DATORECODICEFISCALE"))
				: "";
		String utilizzatore = obj.getAttribute("UTILIZZATORECODICEFISCALE") != null
				? Flusso.getString(obj.getAttribute("UTILIZZATORECODICEFISCALE"))
				: "";
		String utilizzatoreCurr = this.getAttribute("UTILIZZATORECODICEFISCALE") != null
				? Flusso.getString(this.getAttribute("UTILIZZATORECODICEFISCALE"))
				: "";
		String contratto = obj.getAttribute("CODTIPOCONTRATTO") != null
				? Flusso.getString(obj.getAttribute("CODTIPOCONTRATTO"))
				: "";
		String contrattoCurr = this.getAttribute("CODTIPOCONTRATTO") != null
				? Flusso.getString(this.getAttribute("CODTIPOCONTRATTO"))
				: "";
		String qualifica = obj.getAttribute("CODMANSIONEDOT") != null
				? Flusso.getString(obj.getAttribute("CODMANSIONEDOT"))
				: "";
		String qualificaCurr = this.getAttribute("CODMANSIONEDOT") != null
				? Flusso.getString(this.getAttribute("CODMANSIONEDOT"))
				: "";
		String comuneLavoro = obj.getAttribute("CODCOMLAVORO") != null
				? Flusso.getString(obj.getAttribute("CODCOMLAVORO"))
				: "";
		String comuneLavoroCurr = this.getAttribute("CODCOMLAVORO") != null
				? Flusso.getString(this.getAttribute("CODCOMLAVORO"))
				: "";
		String orario = obj.getAttribute("CODORARIO") != null ? Flusso.getString(obj.getAttribute("CODORARIO")) : "";
		String orarioCurr = this.getAttribute("CODORARIO") != null ? Flusso.getString(this.getAttribute("CODORARIO"))
				: "";

		if (prgMovimento.intValue() != prgMovimentoCurr.intValue()
				|| prgMovimentoRett.intValue() != prgMovimentoRettCurr.intValue()
				|| !codTipoMov.equalsIgnoreCase(codTipoMovCurr) || !datInizio.equalsIgnoreCase(datInizioCurr)
				|| !datFine.equalsIgnoreCase(datFineCurr) || !statoAtto.equalsIgnoreCase(statoAttoCurr)
				|| !datoreLavoro.equalsIgnoreCase(datoreLavoroCurr) || !utilizzatore.equalsIgnoreCase(utilizzatoreCurr)
				|| !contratto.equalsIgnoreCase(contrattoCurr) || !qualifica.equalsIgnoreCase(qualificaCurr)
				|| !comuneLavoro.equalsIgnoreCase(comuneLavoroCurr) || !orario.equalsIgnoreCase(orarioCurr)
				|| !codiceComunicazione.equalsIgnoreCase(codiceComunicazioneCurr)) {
			return 1;
		}
		return 0;
	}

}