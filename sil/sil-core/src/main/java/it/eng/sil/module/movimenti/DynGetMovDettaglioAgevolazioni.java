package it.eng.sil.module.movimenti;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.util.Utils;

public class DynGetMovDettaglioAgevolazioni implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DynGetMovDettaglio.class.getName());

	private String SELECT_PROLOGO = "select agev_app.prgmovimentoapp, agev_app.codagevolazione, "
			+ "de_agevolazione.strdescrizione ";

	public DynGetMovDettaglioAgevolazioni() {
	}

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		String prgMovimentoApp = Utils.notNull(req.getAttribute("prgMovimentoApp"));
		String context = "";

		if (req.containsAttribute("CONTEXT")) {
			context = StringUtils.getAttributeStrNotNull(req, "CONTEXT");
		}
		if (req.containsAttribute("CURRENTCONTEXT")) {
			context = StringUtils.getAttributeStrNotNull(req, "CURRENTCONTEXT");
		}

		// Creo la parte dinamica della query e la ritorno
		String query = SELECT_PROLOGO;

		if (context.equals("validaArchivio")) {
			query += " from am_mov_agev_app_archivio agev_app ";
		} else {
			query += " from am_mov_agev_app agev_app ";
		}

		query += " inner join  de_agevolazione on (agev_app.codagevolazione = de_agevolazione.codagevolazione) ";
		query += " where agev_app.prgMovimentoApp = " + prgMovimentoApp;

		if ("".equals(prgMovimentoApp)) {
			_logger.error("prgMovimentoApp vuoto");
			_logger.error(req.toXML());
		}
		return query;
	}
}
