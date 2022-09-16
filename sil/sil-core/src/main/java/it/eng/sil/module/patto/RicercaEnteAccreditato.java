package it.eng.sil.module.patto;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

public class RicercaEnteAccreditato implements IDynamicStatementProvider {
	public RicercaEnteAccreditato() {
	}

	private static final String SELECT_SQL_BASE = "select " + "an_vch_ente.strcodicefiscale, " + "an_vch_ente.codsede, "
			+ "InitCap(an_vch_ente.strdenominazione) as strdenominazione, " + "an_vch_ente.STRINDIRIZZO, "
			+ "De_Comune.Strcap, " + "De_Provincia.Strtarga, " + "an_vch_ente.strTel, "
			+ "InitCap(de_comune.STRDENOMINAZIONE) as comune " + "FROM an_vch_ente  "
			+ "inner join de_comune on (an_vch_ente.codcom = de_comune.codcom) "
			+ "inner join de_provincia on (de_comune.Codprovincia = de_provincia.Codprovincia) "
			+ "inner join an_rel_ente_programma on (an_vch_ente.strcodicefiscale = an_rel_ente_programma.strcodicefiscale and "
			+ "an_vch_ente.codsede = an_rel_ente_programma.codsede) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String cf = (String) req.getAttribute("cf");
		String ragioneSociale = (String) req.getAttribute("ragioneSociale");
		String prgColloquioProgramma = (String) req.getAttribute("CODPROGRAMMA");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);

		String buf = "";

		if (cf != null && !cf.equals("")) {
			buf += " WHERE upper(an_vch_ente.STRCODICEFISCALE) = '" + cf.toUpperCase() + "'";
		}
		if (ragioneSociale != null && !ragioneSociale.equals("")) {
			if (!buf.equals("")) {
				buf += " AND ";
			} else {
				buf += " WHERE ";
			}
			buf += "upper(an_vch_ente.strdenominazione) LIKE '%" + ragioneSociale.toUpperCase() + "%'";
		}

		if (prgColloquioProgramma != null && !prgColloquioProgramma.equals("")) {
			if (!buf.equals("")) {
				buf += " AND ";
			} else {
				buf += " WHERE ";
			}
			buf += "an_rel_ente_programma.codservizio = (select coll.codservizio from or_colloquio coll where coll.prgcolloquio = "
					+ prgColloquioProgramma + ") ";
		}

		buf += " order by an_vch_ente.strdenominazione";
		query_totale.append(buf);

		return query_totale.toString();

	}
}
