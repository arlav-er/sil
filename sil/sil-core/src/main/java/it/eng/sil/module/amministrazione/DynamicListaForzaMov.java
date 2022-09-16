package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

public class DynamicListaForzaMov implements IDynamicStatementProvider {

	private String SELECT_SQL_BASE_GESTISCI = "SELECT MOV.prgMovimento , "
			+ " TO_CHAR(mov.DATFINEMOV,'dd/mm/yyyy') datFineMov, "
			+ " TO_CHAR(MOV.datInizioMov, 'DD/MM/YYYY') datamov, " + " MOV.codTipoMov, "
			+ " MOV.CODTIPOCONTRATTO CODTIPOASS, " + " MOV.CODMONOTIPOFINE, " + " MOV.codMonoTempo, "
			+ " MOV.CODSTATOATTO, " + " TO_CHAR(MOV.DATFINEMOVEFFETTIVA, 'DD/MM/YYYY') datfinemoveffettiva, "
			+ " MOV.DATINIZIOMOV, " + " AZ.strRagioneSociale RAGSOCAZ " + " FROM AM_MOVIMENTO MOV, AN_AZIENDA AZ "
			+ " WHERE MOV.prgAzienda = AZ.prgAzienda ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String dataMovDa = (String) req.getAttribute("datmovimentoda");
		String dataMovA = (String) req.getAttribute("datmovimentoa");
		String tipoMovimento = (String) req.getAttribute("tipoMovimento");
		String statoMovimento = (String) req.getAttribute("CODSTATOATTO");

		String cdnLavoratore = (String) req.getAttribute("CDNLAVORATORE");
		String prgAzienda = (String) req.getAttribute("PRGAZIENDA");
		String prgUnita = (String) req.getAttribute("PRGUNITA");

		String prgAziendaUt = (String) req.getAttribute("PRGAZIENDAUT");
		String prgUnitaUt = (String) req.getAttribute("PRGUNITAUT");

		String query_totaleStr = SELECT_SQL_BASE_GESTISCI;

		StringBuffer query_totale = new StringBuffer(query_totaleStr);

		StringBuffer buf = new StringBuffer();

		if ((cdnLavoratore != null) && (!cdnLavoratore.equals(""))) {
			buf.append(" AND MOV.cdnLavoratore = " + cdnLavoratore);
		}

		if ((prgAzienda != null) && (!prgAzienda.equals("")) && (prgUnita != null) && (!prgUnita.equals(""))) {
			buf.append(" AND MOV.prgAzienda = " + prgAzienda + " AND MOV.prgUnita = " + prgUnita);
		}

		if ((prgAziendaUt != null) && (!prgAziendaUt.equals("")) && (prgUnitaUt != null) && (!prgUnitaUt.equals(""))) {
			buf.append(" AND MOV.PRGAZIENDAUTILIZ = " + prgAziendaUt + " AND MOV.PRGUNITAUTILIZ = " + prgUnitaUt);
		}

		// date movimento
		if ((dataMovDa != null) && (!dataMovDa.equals(""))) {
			buf.append(" AND MOV.datInizioMov >= to_date('" + dataMovDa + "', 'DD/MM/YYYY') ");
		}
		if ((dataMovA != null) && (!dataMovA.equals(""))) {
			buf.append(" AND MOV.datInizioMov <= to_date('" + dataMovA + "', 'DD/MM/YYYY') ");
		}

		// tipoMovimento
		if ((tipoMovimento != null) && (!tipoMovimento.equals(""))) {
			buf.append(" AND MOV.codTipoMov = '" + tipoMovimento + "' ");
		}

		// statoMovimento
		if ((statoMovimento != null) && (!statoMovimento.equals(""))) {
			buf.append(" AND MOV.codStatoAtto = '" + statoMovimento + "'");
		}

		buf.append(" ORDER BY DATINIZIOMOV asc  ");

		query_totale.append(buf.toString());
		return query_totale.toString();

	}
}
