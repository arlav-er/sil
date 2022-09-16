package it.eng.sil.module.patto;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.util.Utils;

public class ListaSoggettoAccProgrammi implements IDynamicStatementProvider {
	private String className = this.getClass().getName();

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(ListaSoggettoAccProgrammi.class.getName());

	private static final String SELECT_SQL_BASE = "SELECT am_programma_ente.strentecodicefiscale strcodicefiscale, ente.strindirizzo, ente.strtel, ente.strdenominazione, "
			+ " de_comune.strdenominazione comune, patto.prgpattolavoratore, patto.cdnlavoratore, am_programma_ente.prgcolloquio, am_programma_ente.codsede, "
			+ " de_servizio.strdescrizione descprogramma, am_programma_ente.strnotaente, to_char(coll.datcolloquio, 'dd/mm/yyyy') dataprogramma "
			+ " from am_programma_ente "
			+ " inner join am_patto_lavoratore patto on (am_programma_ente.prgpattolavoratore = patto.prgpattolavoratore) "
			+ " inner join an_vch_ente ente on (am_programma_ente.strentecodicefiscale = ente.strcodicefiscale and am_programma_ente.codsede = ente.codsede) "
			+ " inner join de_comune on (ente.codcom = de_comune.codcom) "
			+ " inner join or_colloquio coll on (am_programma_ente.prgcolloquio = coll.prgcolloquio) "
			+ " inner join de_servizio on (coll.codservizio = de_servizio.codservizio) " + " where ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();
		String prgPattoLavoratore = Utils.notNull((String) req.getAttribute("prgPattoLavoratore"));
		String cdnLavoratore = (String) req.getAttribute("cdnLavoratore");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		if (!prgPattoLavoratore.equals("")) {
			query_totale.append(" patto.prgpattolavoratore = " + prgPattoLavoratore);
		} else {
			query_totale.append(" patto.cdnlavoratore = " + cdnLavoratore);
			query_totale.append(" and patto.datfine is null ");
		}

		query_totale.append(" order by am_programma_ente.prgcolloquio desc ");

		return query_totale.toString();
	}
}
