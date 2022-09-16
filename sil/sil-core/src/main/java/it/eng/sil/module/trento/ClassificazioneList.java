package it.eng.sil.module.trento;

import org.apache.commons.lang.StringUtils;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.module.agenda.ServizioList;
import it.eng.sil.util.NavigationCache;

public class ClassificazioneList implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ServizioList.class.getName());

	public ClassificazioneList() {
	}

	private static final String SELECT_SQL_BASE = "SELECT * FROM ST_TEMPLATE_CLASSIF ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String query_totale = SELECT_SQL_BASE;

		String STRNOME, prgTipoDominio;
		NavigationCache cache = null;
		if (requestContainer.getSessionContainer().getAttribute("CLASSIFICAZIONECACHE") != null) {
			cache = (NavigationCache) requestContainer.getSessionContainer().getAttribute("CLASSIFICAZIONECACHE");
			STRNOME = cache.getField("STRNOME") != null ? cache.getField("STRNOME").toString() : null;
			prgTipoDominio = cache.getField("prgTipoDominio") != null ? cache.getField("prgTipoDominio").toString()
					: null;
		} else {
			STRNOME = req.getAttribute("STRNOME") != null ? req.getAttribute("STRNOME").toString() : null;
			;
			prgTipoDominio = req.getAttribute("prgTipoDominio") != null ? req.getAttribute("prgTipoDominio").toString()
					: null;
		}

		if (!StringUtils.isEmpty(STRNOME)) {
			query_totale += " WHERE STRNOME LIKE '%" + STRNOME + "%' ";

			if (!StringUtils.isEmpty(prgTipoDominio)) {
				query_totale += " AND CODTIPODOMINIO = '" + prgTipoDominio + "'";
			}

			query_totale += " AND DATCANC IS NULL ";

		} else {
			if (!StringUtils.isEmpty(prgTipoDominio)) {
				query_totale += " WHERE CODTIPODOMINIO = '" + prgTipoDominio + "'";
			}

			query_totale += " AND DATCANC IS NULL ";
		}

		query_totale += " order by st_template_classif.ordinamento ASC, st_template_classif.DTMINS DESC";

		_logger.debug("sil.module.trento.ClassificazioneList " + "::Stringa di ricerca:" + query_totale.toString());

		return query_totale;
	}

}
