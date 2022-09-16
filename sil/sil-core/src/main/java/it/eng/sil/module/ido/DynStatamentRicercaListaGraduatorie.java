package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

public class DynStatamentRicercaListaGraduatorie implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = "select " + " gr.prgGraduatoria, " + " gr.NUMANNO, "
			+ " c.strDescrizione as STATOGRAD, " + " gr.cdnutins, " + " to_char(gr.dtmins, 'dd/mm/yyyy') as dataIns, "
			+ " decode(gr.CODMONOTIPOGRAD,'A', 'Avviamento numerico art.18','D', 'Avviamento numerico art.8') as TIPOGRAD, "
			+ " gr.STRNOTA as STRNOTA, " + " PR.STRDENOMINAZIONE as PROVINCIA_ISCR, "
			+ " (select u.strcognome || ' ' || u.strnome from ts_utente u where cdnut = gr.cdnutins) ||' - '|| TO_CHAR(gr.dtmins, 'DD/MM/YYYY hh24:mi') as utenteins, "
			+ " (select u.strcognome || ' ' || u.strnome from ts_utente u where cdnut = gr.cdnutmod) ||' - '|| TO_CHAR(gr.dtmmod, 'DD/MM/YYYY hh24:mi') as utentemod "
			+ " from CM_GRADUATORIA gr " + " inner join DE_CM_STATO_GRAD c on gr.codStatoGrad = c.codStatoGrad "
			+ " inner join DE_PROVINCIA pr on gr.codprovincia = pr.codprovincia ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		SourceBean req = requestContainer.getServiceRequest();
		String annoGrad = (String) req.getAttribute("annoGrad");
		String statoGrad = (String) req.getAttribute("statoGrad");
		String codMonoTipoGrad = (String) req.getAttribute("codMonoTipoGrad");
		String ambitoTerritoriale = (String) req.getAttribute("PROVINCIA_ISCR");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if ((annoGrad != null) && (!annoGrad.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND");
			}

			buf.append(" gr.numAnno = '" + annoGrad + "' ");
		}

		if ((codMonoTipoGrad != null) && (!codMonoTipoGrad.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND");
			}

			buf.append(" gr.codMonoTipoGrad = '" + codMonoTipoGrad + "' ");
		}

		if ((statoGrad != null) && (!statoGrad.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND");
			}

			buf.append(" gr.codStatoGrad = '" + statoGrad + "' ");
		}

		if ((ambitoTerritoriale != null) && (!ambitoTerritoriale.equals(""))) {
			if (buf.length() == 0) {
				buf.append(" WHERE ");
			} else {
				buf.append(" AND");
			}

			buf.append(" gr.CODPROVINCIA = '" + ambitoTerritoriale + "' ");
		}

		buf.append(" order by gr.numAnno desc, gr.codStatoGrad, gr.CODMONOTIPOGRAD desc, gr.dtmins desc ");

		query_totale.append(buf.toString());
		return query_totale.toString();

	}
}
