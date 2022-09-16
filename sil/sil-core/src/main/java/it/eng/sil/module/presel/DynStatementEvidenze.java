package it.eng.sil.module.presel;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.security.User;

/*
 * Crea lo statement per estrarre l'elenco delle evidenze
 * scadute o meno in base al punto di chiamata
 * 
 * @author: Stefania Orioli
 */

public class DynStatementEvidenze implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DynStatementEvidenze.class.getName());

	public DynStatementEvidenze() {
	}

	private String className = this.getClass().getName();

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		String cdnLavoratore = StringUtils.getAttributeStrNotNull(req, "CDNLAVORATORE");
		SessionContainer sexCont = requestContainer.getSessionContainer();
		User user = (User) sexCont.getAttribute("@@USER@@");
		String cdnGruppo = Integer.toString(user.getCdnGruppo());
		String cdnProfilo = Integer.toString(user.getCdnProfilo());

		String fScad = StringUtils.getAttributeStrNotNull(req, "SCAD");

		String SELECT_SQL = "select " + "to_char(PRGEVIDENZA) as PRGEVIDENZA, "
				+ "to_char(an_evidenza.CDNLAVORATORE) as CDNLAVORATORE, "
				+ "to_char(DATDATASCAD,'dd/mm/yyyy') as DATDATASCAD, "
				// + "substr(STREVIDENZA, 1, 200) as STREVIDENZA, "
				+ "PG_UTILS.TRUNC_DESC(STREVIDENZA,200,'...') as STREVIDENZA, "
				+ "de_tipo_evidenza.STRDESCRIZIONE as TIPOEV, "
				+ "InitCap(ts_utente.STRCOGNOME) || ' ' || InitCap(ts_utente.STRNOME) as UT_EV "
				+ ", 1 as ord1, 1 as ord2, trunc(an_evidenza.DATDATASCAD) as ord3 " + "from an_evidenza "
				+ "inner join de_tipo_evidenza on (an_evidenza.PRGTIPOEVIDENZA=de_tipo_evidenza.PRGTIPOEVIDENZA)"
				+ "inner join ts_vis_evidenza vis on (vis.prgTipoEvidenza=de_tipo_evidenza.prgTipoEvidenza and vis.cdnGruppo="
				+ cdnGruppo + " and vis.cdnProfilo=" + cdnProfilo + ") "
				+ "inner join ts_utente on (an_evidenza.CDNUTINS=ts_utente.CDNUT) "
				+ "where an_evidenza.cdnLavoratore = ";

		String SELECT_SQL_UN = "select " + "to_char(PRGEVIDENZA) as PRGEVIDENZA, "
				+ "to_char(an_evidenza.CDNLAVORATORE) as CDNLAVORATORE, "
				+ "to_char(DATDATASCAD,'dd/mm/yyyy') as DATDATASCAD, " + "substr(STREVIDENZA, 1, 30) as STREVIDENZA, "
				+ "de_tipo_evidenza.STRDESCRIZIONE as TIPOEV, "
				+ "InitCap(ts_utente.STRCOGNOME) || ' ' || InitCap(ts_utente.STRNOME) as UT_EV "
				+ ", 2 as ord1, to_number(to_char(datdatascad, 'yyyymmdd')) as ord2, trunc(an_evidenza.DATDATASCAD) as ord3 "
				+ "from an_evidenza "
				+ "inner join de_tipo_evidenza on (an_evidenza.PRGTIPOEVIDENZA=de_tipo_evidenza.PRGTIPOEVIDENZA) "
				+ "inner join ts_vis_evidenza vis on (vis.prgTipoEvidenza=de_tipo_evidenza.prgTipoEvidenza and vis.cdnGruppo="
				+ cdnGruppo + " and vis.cdnProfilo=" + cdnProfilo + ") "
				+ "inner join ts_utente on (an_evidenza.CDNUTINS=ts_utente.CDNUT) "
				+ "where an_evidenza.cdnLavoratore = ";

		String SELECT_SQL_2p = "and (to_char(datDataScad,'yyyymmdd')>=to_char(sysdate,'yyyymmdd')) ";

		String buf = SELECT_SQL + cdnLavoratore;
		// Prima si visualizzano le evidenze non scadute, poi quelle scadute
		buf += " " + SELECT_SQL_2p;

		// Se fScad=N allora la pagina viene aperta come popup quindi non devo
		// vedere
		// le evidenze scadute, altrimenti accodo le evidenze gia' scadute
		if (!fScad.equals("N")) {
			buf += " UNION ";
			buf += SELECT_SQL_UN;
			buf += cdnLavoratore;
			buf += " and (to_char(datDataScad,'yyyymmdd')<to_char(sysdate,'yyyymmdd')) ";
			// buf += "order by AN_EVIDENZA.DATDATASCAD desc ";
		}
		buf += " order by  ord1 asc, ord2 desc, ord3 asc";
		// Debug
		_logger.debug("sil.module.presel.DynStatementEvidenze" + "::Stringa di ricerca:" + buf);

		return (buf);
	}
}