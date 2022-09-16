package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

public class DynamicNullaOsta implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DynamicNullaOsta.class.getName());
	private String className = this.getClass().getName();

	private static final String SELECT_SQL_BASE = " select an_azienda.STRCODICEFISCALE || '<BR/>' || an_azienda.STRPARTITAIVA || '<BR/>' || "
			+ " an_azienda.STRRAGIONESOCIALE as AZIENDA,"
			+ " an_lavoratore.STRCODICEFISCALE || '<BR/>' || an_lavoratore.STRCOGNOME || ' ' ||  an_lavoratore.STRNOME as Lavoratore,"
			+ " an_lavoratore.cdnlavoratore, " + " an_unita_azienda.prgunita, " + " an_azienda.prgAzienda, "
			+ " cm_nulla_osta.CODMONOTIPO, " + " cm_nulla_osta.CODMONOCATEGORIA, "
			+ " cm_nulla_osta.PRGAZIENDAUTILIZ, cm_nulla_osta.PRGUNITAUTILIZ, "
			+ " cm_nulla_osta.FLGCONVENZIONE, cm_nulla_osta.CODMOTIVO,  " + " cm_nulla_osta.CODMONOTEMPO, "
			+ " cm_nulla_osta.CODORARIO, " + " cm_nulla_osta.DECORESETT, cm_nulla_osta.CODMANSIONE, "
			+ " cm_nulla_osta.NUMLIVELLO, cm_nulla_osta.CODCCNL, cm_nulla_osta.DECRETRIBUZIONE,"
			+ " to_char(cm_nulla_osta.DATSPEDIZIONE,'dd/mm/yyyy') as DATSPEDIZIONE, "
			+ " to_char(cm_nulla_osta.DATFINE,'dd/mm/yyyy') as DATFINE, " + " cm_nulla_osta.PRGNULLAOSTA, "
			+ " decode(cm_nulla_osta.CODMONOCATEGORIA,NULL,'','D','Disabile','A','Categoria protetta ex art.18') as CATEGORIA, "
			+ " decode(cm_nulla_osta.CODMONOTIPO,NULL,'','M','Nominativa','R','Numerica') as TIPOLOGIA, "
			+ " to_char(cm_nulla_osta.DATINIZIO,'dd/mm/yyyy') as DATAINIZIO, "
			+ " decode(cm_nulla_osta.CODMONOTEMPO,NULL,'','D','Determinato','I','Indederminato') as TEMPO, "
			+ " de_stato_atto.STRDESCRIZIONE as STATO, " + " de_stato_atto.CODSTATOATTO as CODSTATOATTO, "
			+ " cm_nulla_osta.NUMKLONULLAOSTA, " + " PR.STRDENOMINAZIONE as PROVINCIA_ISCR, "
			+ " cm_nulla_osta.codprovincia as COD_PROVINCIA_ISCR, "
			+ " to_char(am_documento.DATPROTOCOLLO,'dd/mm/yyyy') as dataProt, "
			+ " to_char(am_documento.DATINIZIO,'dd/mm/yyyy') as DatInizio, "
			+ " to_char(am_documento.DATACQRIL,'dd/mm/yyyy') as DatAcqRil, "
			+ " to_char(am_documento.DATPROTOCOLLO,'DD/MM/YYYY hh24:mi') as dataOraProt, "
			+ " am_documento.NUMPROTOCOLLO as NUMPROTOCOLLO, am_documento.NUMANNOPROT as numAnnoProt, "
			+ " de_doc_tipo.strio striodoc, " + " de_doc_tipo.codtipodocumento codtipodoc, "
			+ " de_doc_tipo.strdescrizione strtipodoc, " + " de_doc_tipo.codambitodoc codambitodoc, "
			+ " de_doc_ambito.strdescrizione strambitodoc " + " from cm_nulla_osta  "
			+ " inner join am_documento_coll on (cm_nulla_osta.PRGNULLAOSTA = to_number(am_documento_coll.STRCHIAVETABELLA)) "
			+ " inner join am_documento on (am_documento.PRGDOCUMENTO = am_documento_coll.PRGDOCUMENTO) "
			+ " inner join an_azienda on (an_azienda.PRGAZIENDA = cm_nulla_osta.PRGAZIENDA) "
			+ " inner join an_unita_azienda on (an_unita_azienda.PRGAZIENDA = cm_nulla_osta.PRGAZIENDA "
			+ " 								  and an_unita_azienda.PRGUNITA = cm_nulla_osta.PRGUNITA)"
			+ " inner join an_lavoratore on (an_lavoratore.CDNLAVORATORE = cm_nulla_osta.CDNLAVORATORE)"
			+ " left outer join an_unita_azienda an2 on (an2.PRGAZIENDA = cm_nulla_osta.PRGAZIENDAUTILIZ "
			+ "                                          and an2.PRGUNITA = cm_nulla_osta.PRGUNITAUTILIZ) "
			+ " inner join de_stato_atto on (am_documento.CODSTATOATTO = de_stato_atto.CODSTATOATTO)"
			+ " inner join de_doc_tipo on (de_doc_tipo.codtipodocumento = am_documento.codtipodocumento)"
			+ " INNER JOIN DE_PROVINCIA PR ON (cm_nulla_osta.CODPROVINCIA = PR.CODPROVINCIA) "
			+ " inner join de_doc_ambito on (de_doc_ambito.codambitodoc = de_doc_tipo.codambitodoc)"
			+ " where am_documento.CODTIPODOCUMENTO = 'NULOST'";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		SourceBean req = requestContainer.getServiceRequest();

		String cdnLavoratore = (String) req.getAttribute("CDNLAVORATORE");
		String prgAzienda = (String) req.getAttribute("PRGAZIENDA");
		// String prgUnita=(String)req.getAttribute("PRGUNITA");
		String prgAziendaUt = (String) req.getAttribute("PRGAZIENDAUt");
		// String prgUnitaUt=(String)req.getAttribute("PRGUNITAUt");
		String CODMONOCATEGORIA = (String) req.getAttribute("CODMONOCATEGORIA");
		String datinizio = (String) req.getAttribute("Datinizio");
		String dataFine = (String) req.getAttribute("DataFine");
		String CODSTATOATTO = (String) req.getAttribute("StatoAtto");
		String CODMONOTIPO = (String) req.getAttribute("CODMONOTIPO");
		String PROVINCIA_ISCR = (String) req.getAttribute("PROVINCIA_ISCR");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if ((cdnLavoratore != null) && (!cdnLavoratore.equals(""))) {
			buf.append(" AND cm_nulla_osta.CDNLAVORATORE ='" + cdnLavoratore + "' ");
		}

		if ((prgAzienda != null) && (!prgAzienda.equals(""))) {
			buf.append(" AND cm_nulla_osta.PRGAZIENDA ='" + prgAzienda + "' ");
		}

		/*
		 * if ((prgUnita!=null) && (!prgUnita.equals(""))) { buf.append(" AND cm_nulla_osta.PRGUNITA ='" + prgUnita + "'
		 * "); }
		 */

		if ((prgAziendaUt != null) && (!prgAziendaUt.equals(""))) {
			buf.append(" AND cm_nulla_osta.PRGAZIENDAUTILIZ ='" + prgAziendaUt + "' ");
		}

		if ((prgAziendaUt != null) && (!prgAziendaUt.equals(""))) {
			buf.append(" AND cm_nulla_osta.PRGAZIENDAUTILIZ ='" + prgAziendaUt + "' ");
		}

		/*
		 * if ((prgUnitaUt!=null) && (!prgUnitaUt.equals(""))) { buf.append(" AND cm_nulla_osta.PRGUNITAUTILIZ
		 * ='" + prgUnitaUt + "' "); }
		 */

		if ((datinizio != null) && (!datinizio.equals(""))) {
			buf.append(" AND cm_nulla_osta.DATINIZIO >=to_date('" + datinizio + "', 'DD/MM/YYYY') ");
		}

		if ((dataFine != null) && (!dataFine.equals(""))) {
			buf.append(" AND cm_nulla_osta.DATINIZIO <=to_date('" + dataFine + "', 'DD/MM/YYYY') ");
		}

		if ((CODMONOCATEGORIA != null) && (!CODMONOCATEGORIA.equals(""))) {
			buf.append(" AND cm_nulla_osta.CODMONOCATEGORIA='" + CODMONOCATEGORIA + "' ");
		}

		if ((CODMONOTIPO != null) && (!CODMONOTIPO.equals(""))) {
			buf.append(" AND cm_nulla_osta.CODMONOTIPO ='" + CODMONOTIPO + "' ");
		}

		if ((CODSTATOATTO != null) && (!CODSTATOATTO.equals(""))) {
			buf.append(" AND de_stato_atto.CODSTATOATTO ='" + CODSTATOATTO + "' ");
		}

		if ((PROVINCIA_ISCR != null) && (!PROVINCIA_ISCR.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" (cm_nulla_osta.CODPROVINCIA) = '" + PROVINCIA_ISCR.toUpperCase() + "'");
		}

		query_totale.append(buf.toString());
		_logger.debug(className + "::Stringa di ricerca:" + query_totale.toString());
		return query_totale.toString();

	}
}
