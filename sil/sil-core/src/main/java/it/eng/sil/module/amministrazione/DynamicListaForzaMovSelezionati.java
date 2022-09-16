package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

public class DynamicListaForzaMovSelezionati implements IDynamicStatementProvider {

	private String SELECT_SQL_BASE_GESTISCI = "SELECT MOV.prgMovimento , " + " MOV.prgMovimentoPrec PRGMOVIMENTOPREC, "
			+ " MOV.prgMovimentoSucc PRGMOVIMENTOSUCC, " + " LAV.cdnLavoratore CDNLAV, " + " AZ.prgAzienda PRGAZ, "
			+ " UAZ.prgUnita PRGUAZ, " + " TO_CHAR(mov.DATFINEMOV,'dd/mm/yyyy') datFineMov, "
			+ " TO_CHAR(MOV.datInizioMov, 'DD/MM/YYYY') DATAMOV, " + " MOV.codTipoMov, "
			+ " MOV.CODTIPOCONTRATTO CODTIPOASS, " + " MOV.CODMONOTIPOFINE CODMONOTIPOFINE, "
			+ " MOV.codMonoTempo CODMONOTEMPO, " + " MOV.CODSTATOATTO, " + " MOV.CODMOTANNULLAMENTO, "
			+ " TO_CHAR(MOV.DATFINEMOVEFFETTIVA, 'DD/MM/YYYY') DATFINEMOVEFFETTIVA, " + " MOV.CODTIPOTRASF, "
			+ " DE_TIPO_TRASF.STRDESCRIZIONE STRTRASFERIMENTO, "
			+ " LAV.strCognome || ' ' || LAV.strNome COGNOMENOMELAV, " + " LAV.strCodiceFiscale CODFISCLAV, "
			+ " CASE WHEN (MOV.DATINIZIOMOV is not null) THEN MOV.DATINIZIOMOV ELSE MOV.DATFINEMOV END DATASORT1, "
			+ " AZ.strRagioneSociale RAGSOCAZ ";

	String SELECT_SQL_BASE_GESTISCI_FROM = " FROM AM_MOVIMENTO MOV,DE_MV_TIPO_MOV TMOV,AN_UNITA_AZIENDA UAZ,"
			+ " AN_AZIENDA AZ, AN_LAVORATORE LAV, DE_COMUNE COM, "
			+ " DE_PROVINCIA PROV , DE_TIPO_CONTRATTO, DE_TIPO_TRASF ";

	String SELECT_SQL_BASE_GESTISCI_WHERE = "WHERE MOV.codTipoMov = TMOV.codTipoMov "
			+ " AND MOV.CODTIPOCONTRATTO = DE_TIPO_CONTRATTO.CODTIPOCONTRATTO (+) "
			+ " AND MOV.CODTIPOTRASF = DE_TIPO_TRASF.CODTIPOTRASF (+) " + " AND MOV.cdnLavoratore = LAV.cdnLavoratore "
			+ " AND MOV.prgAzienda = AZ.prgAzienda "
			+ " AND MOV.prgAzienda = UAZ.prgAzienda AND MOV.prgUnita = UAZ.prgUnita "
			+ " AND UAZ.codCom = COM.codCom AND COM.codProvincia = PROV.codProvincia (+) ";

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

		String query_totaleStr = SELECT_SQL_BASE_GESTISCI + SELECT_SQL_BASE_GESTISCI_FROM
				+ SELECT_SQL_BASE_GESTISCI_WHERE;

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

		buf.append(" ORDER BY DATASORT1 ,  RAGSOCAZ ,  mov.codtipocontratto  ");

		query_totale.append(buf.toString());
		return query_totale.toString();

	}
}
