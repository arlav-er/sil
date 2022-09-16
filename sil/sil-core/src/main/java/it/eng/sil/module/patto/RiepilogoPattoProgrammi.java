package it.eng.sil.module.patto;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.util.Utils;

public class RiepilogoPattoProgrammi implements IDynamicStatementProvider {
	private String className = this.getClass().getName();

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RiepilogoPattoProgrammi.class.getName());

	private static final String SELECT_SQL_BASE = "SELECT coll.prgcolloquio, to_char(coll.datcolloquio, 'dd/mm/yyyy') dataProgramma, "
			+ " (case when patto.datfine is null then patto.cdnlavoratore else null end) cdnlavoratore, "
			+ " de_servizio.strdescrizione descProgramma, to_char(coll.datfineprogramma, 'dd/mm/yyyy') dataFineProgramma, "
			+ " patto.prgpattolavoratore " + " from am_patto_lavoratore patto "
			+ " inner join or_colloquio coll on (patto.cdnlavoratore = coll.cdnlavoratore) "
			+ " inner join de_servizio on (coll.codservizio = de_servizio.codservizio) ";
	// + " where (nvl(de_servizio.flgprogramma, 'N') = 'S' or nvl(de_servizio.codmonoprogramma, 'N') = 'L14') ";

	// + " inner join or_percorso_concordato on (coll.prgcolloquio = or_percorso_concordato.prgcolloquio) "
	// + " inner join de_azione az on (or_percorso_concordato.prgazioni = az.prgazioni) "
	// + " inner join de_esito on (or_percorso_concordato.codesito = de_esito.codesito) "
	// + " inner join am_lav_patto_scelta on (patto.prgpattolavoratore = am_lav_patto_scelta.prgpattolavoratore "
	// + " and to_number(am_lav_patto_scelta.strchiavetabella) = or_percorso_concordato.prgpercorso and
	// am_lav_patto_scelta.codlsttab = 'OR_PER') "
	// + " where or_percorso_concordato.codesito in ('PRO', 'CC', 'AVV', 'FC', 'INT', 'RIF') ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		String prgPattoLavoratore = Utils.notNull((String) req.getAttribute("prgPattoLavoratore"));
		String cdnLavoratore = (String) req.getAttribute("cdnLavoratore");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		if (!prgPattoLavoratore.equals("")) {
			query_totale.append(" where patto.prgpattolavoratore = " + prgPattoLavoratore);
		} else {
			query_totale.append(" where patto.cdnlavoratore = " + cdnLavoratore);
			query_totale.append(" and patto.datfine is null ");
		}

		query_totale.append(" and de_servizio.codmonoprogramma is not null ");

		query_totale.append(" and coll.prgcolloquio in (select per.prgcolloquio from am_lav_patto_scelta scelta "
				+ " inner join or_percorso_concordato per "
				+ " on (to_number(scelta.strchiavetabella) = per.prgpercorso and scelta.codlsttab = 'OR_PER') "
				+ " where scelta.prgpattolavoratore = patto.prgpattolavoratore) ");

		query_totale.append(
				" order by decode(coll.datfineprogramma, null, 1, 0) desc, coll.datcolloquio desc, coll.prgcolloquio desc ");

		return query_totale.toString();
	}
}
