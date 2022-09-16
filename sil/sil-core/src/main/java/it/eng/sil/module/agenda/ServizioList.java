package it.eng.sil.module.agenda;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.security.User;

/*
 * @author: Stefania Orioli 
 */

public class ServizioList implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ServizioList.class.getName());

	public ServizioList() {
	}

	private static final String SELECT_SQL_BASE = "SELECT DE_SERVIZIO_AREA.STRDESCRIZIONE as Area,"
			+ "DE_SERVIZIO.CODSERVIZIO," + "DE_SERVIZIO.PRGSERVIZIOAREA," + "DE_SERVIZIO.STRDESCRIZIONE as Servizio,"
			+ "TO_CHAR(DE_SERVIZIO.DATINIZIOVAL, 'DD/MM/YYYY') DATINIZIOVAL,"
			+ "TO_CHAR(DE_SERVIZIO.DATFINEVAL, 'DD/MM/YYYY') DATFINEVAL,"
			// + "mn_yg_tipo_attivita.STRDESCRIZIONE as desctipoattivitalista,"
			+ "mn_yg_tipo_attivita.CODTIPOATTIVITA || DECODE(mn_yg_tipo_attivita.CODTIPOATTIVITA, NULL, '',' - ') "
			+ "||  mn_yg_tipo_attivita.STRDESCRIZIONE as desctipoattivitalista, "
			+ "de_prestazione.strdescrizione as descprestazionelista,"
			+ "ma_servizio_prestazione.prgprestazione as PRGPRESTAZIONE,"
			+ "ma_servizio_prestazione.FLGPOLATTIVA as POLATTIVA,"
			+ "ma_servizio_tipoattivita.codtipoattivita as PRGTIPOATTIVITA,  "
			+ "(select nvl( (select to_char(ts_config_loc.num)  from ts_config_loc  "
			+ " where strcodrif=(select ts_generale.codprovinciasil from ts_generale) and codtipoconfig='UMBNGEAZ') , 0) "
			+ " from dual) choiceLabel " + "FROM DE_SERVIZIO "
			+ "inner join DE_SERVIZIO_AREA    on DE_SERVIZIO_AREA.PRGSERVIZIOAREA=DE_SERVIZIO.PRGSERVIZIOAREA "
			+ "left outer join ma_servizio_prestazione  on DE_SERVIZIO.CODSERVIZIO = ma_servizio_prestazione.codservizio "
			+ "left outer join de_prestazione on ma_servizio_prestazione.prgprestazione = de_prestazione.prgprestazione "
			+ "left outer join  ma_servizio_tipoattivita on DE_SERVIZIO.CODSERVIZIO = ma_servizio_tipoattivita.codservizio "
			+ "left outer join mn_yg_tipo_attivita on ma_servizio_tipoattivita.codtipoattivita = mn_yg_tipo_attivita.codtipoattivita ";

	/*
	 * select nvl( (select to_char(ts_config_loc.num) num from ts_config_loc where strcodrif=(select
	 * ts_generale.codprovinciasil from ts_generale) and codtipoconfig=?) , 0) as num, (select
	 * ts_generale.codprovinciasil from ts_generale) as codprovinciasil from dual
	 */

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		int cdnUt = user.getCodut();
		int cdnTipoGruppo = user.getCdnTipoGruppo();
		String query_totale = SELECT_SQL_BASE;

		query_totale += "where 1=1 ";
		query_totale += "AND DE_SERVIZIO.CODMONOPROGRAMMA IS NULL ";

		String CODAREA, VALIDI, CODSERVIZIO, SERVIZIO, POLATTIVA, PRESTAZIONE, TIPOATTIVITA;
		CODAREA = req.getAttribute("CODAREA") != null ? req.getAttribute("CODAREA").toString() : null;
		VALIDI = req.getAttribute("VALIDI") != null ? req.getAttribute("VALIDI").toString() : null;
		CODSERVIZIO = req.getAttribute("CODSERVIZIO") != null ? req.getAttribute("CODSERVIZIO").toString() : null;
		;
		SERVIZIO = req.getAttribute("SERVIZIO") != null ? req.getAttribute("SERVIZIO").toString() : null;
		POLATTIVA = req.getAttribute("POLATTIVA") != null ? req.getAttribute("POLATTIVA").toString() : null;
		PRESTAZIONE = req.getAttribute("PRESTAZIONE") != null ? req.getAttribute("PRESTAZIONE").toString() : null;
		TIPOATTIVITA = req.getAttribute("TIPOATTIVITA") != null ? req.getAttribute("TIPOATTIVITA").toString() : null;

		if (CODAREA != null && !CODAREA.equals(""))
			query_totale += " and DE_SERVIZIO_AREA.PRGSERVIZIOAREA = " + CODAREA;
		if (VALIDI != null && VALIDI.equalsIgnoreCase("on"))
			query_totale += " and sysdate between  DE_SERVIZIO.DATINIZIOVAL and   DE_SERVIZIO.Datfineval ";
		else
			query_totale += " and DE_SERVIZIO.DATINIZIOVAL between  DE_SERVIZIO.DATINIZIOVAL and   DE_SERVIZIO.Datfineval ";
		if (CODSERVIZIO != null && !CODSERVIZIO.equals(""))
			query_totale += " and Upper(DE_SERVIZIO.CODSERVIZIO) LIKE  '%" + CODSERVIZIO.toUpperCase() + "%'";
		if (SERVIZIO != null && !SERVIZIO.equals(""))
			query_totale += " and Upper(DE_SERVIZIO.STRDESCRIZIONE) LIKE  '%" + SERVIZIO.toUpperCase() + "%'";

		if (POLATTIVA != null && (POLATTIVA.equalsIgnoreCase("on") || POLATTIVA.equalsIgnoreCase("S")))
			query_totale += " and ma_servizio_prestazione.flgpolattiva = 'S'";
		// else
		// query_totale += " and ( ma_servizio_prestazione.flgpolattiva = 'N' or ma_servizio_prestazione.flgpolattiva is
		// null)";
		if (PRESTAZIONE != null && !PRESTAZIONE.equals("")) {
			query_totale += " and  DE_PRESTAZIONE.prgprestazione = " + PRESTAZIONE;
		}
		if (TIPOATTIVITA != null && !TIPOATTIVITA.equals("")) {
			query_totale += " and Upper(mn_yg_tipo_attivita.codtipoattivita) = '" + TIPOATTIVITA + "'";
		}
		// Debug

		query_totale += " order by DE_SERVIZIO.DATINIZIOVAL DESC";
		_logger.debug("sil.module.agenda.ServizioList " + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale;

	}

}