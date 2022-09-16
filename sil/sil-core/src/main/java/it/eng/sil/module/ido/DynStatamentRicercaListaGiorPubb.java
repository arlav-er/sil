package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

public class DynStatamentRicercaListaGiorPubb implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = "SELECT EG.PRGELENCOGIORNALE, RI.NUMRICHIESTA, RI.PRGRICHIESTAAZ, RI.NUMANNO AS ANNO, AZ.STRRAGIONESOCIALE, UAZ.STRINDIRIZZO, "
			+ " TO_CHAR(DATPUBBLICAZIONE,'dd/mm/yyyy') DATPUBBLICAZIONE, TO_CHAR(DATSCADENZAPUBBLICAZIONE,'dd/mm/yyyy') DATSCADENZAPUBBLICAZIONE, "
			+ "DG.numPriorita, RI.NUMRICHIESTA || '/' ||  RI.NUMANNO as rich_anno "
			+ "FROM do_elencopubb_giornali EG, do_richiesta_az RI, an_azienda AZ, DO_DETTAGLIOPUB_GIORNALI DG, AN_UNITA_AZIENDA UAZ "
			+ "WHERE EG.PRGELENCOGIORNALE=DG.PRGELENCOGIORNALE  " + "AND RI.NUMSTORICO=0 "
			+ "AND DG.PRGRICHIESTAAZ=RI.PRGRICHIESTAAZ " + "AND RI.PRGAZIENDA=AZ.PRGAZIENDA "
			+ "AND RI.PRGAZIENDA=UAZ.PRGAZIENDA " + "AND RI.PRGUNITA=UAZ.PRGUNITA ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String codGiornale = (String) req.getAttribute("CODGIORNALE");
		String datInizioSett = (String) req.getAttribute("DATINIZIOSETT");
		String datFineSett = (String) req.getAttribute("DATFINESETTIMANA");
		/*
		 * if ("INSERISCI".equals(req.getAttribute("module"))) { codGiornale =
		 * (String)req.getAttribute("CODGIORNALE_INS"); datInizioSett = (String)req.getAttribute("DATINIZIOSETT_INS");
		 * datFineSett = (String)req.getAttribute("DATFINESETTIMANA_INS"); }
		 */
		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if ((codGiornale != null) && (!codGiornale.equals(""))) {
			buf.append(" AND EG.CODGIORNALE = '" + codGiornale + "' ");
		}

		if ((datInizioSett != null) && (!datInizioSett.equals("")) && (datFineSett != null)
				&& (!datFineSett.equals(""))) {
			buf.append(" AND EG.DATINIZIOSETT = to_date('" + datInizioSett + "','dd/mm/yyyy') ");
			buf.append(" AND EG.DATFINESETTIMANA = to_date('" + datFineSett + "','dd/mm/yyyy') ");
		}
		buf.append(" ORDER BY  numPriorita");
		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}