package it.eng.sil.module.ido;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

public class DynStatamentRicercaGiorPubb implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = "SELECT EG.PRGELENCOGIORNALE, EG.codgiornale, GI.STRDESCRIZIONE, to_char(EG.DATINIZIOSETT,'dd/mm/yyyy') DATINIZIOSETT, "
			+ "to_char(EG.DATFINESETTIMANA,'dd/mm/yyyy') DATFINESETTIMANA, " + " EG.DATINIZIOSETT as dat_sort "
			+ "FROM do_elencopubb_giornali EG, DE_GIORNALE_PUBB GI " + "WHERE EG.CODGIORNALE=GI.CODGIORNALE ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		/*
		 * String prgAzienda =(String) req.getAttribute("prgAzienda"); String prgUnita =(String)
		 * req.getAttribute("prgUnita"); String flgPubblicata =(String) req.getAttribute("FLGPUBBLICATA"); String
		 * datPubblicazione =(String) req.getAttribute("DATPUBBLICAZIONE"); String datScadenzaPubblicazione =(String)
		 * req.getAttribute("DATSCADENZAPUBBLICAZIONE"); //String prgRichiestaAz =(String)
		 * req.getAttribute("PRGRICHIESTAAZ"); String prgRichiestaAz =(String) req.getAttribute("NUMRICHIESTA"); String
		 * anno =(String) req.getAttribute("ANNO"); String utric =(String) req.getAttribute("UTRIC"); String cdnut
		 * =(String) req.getAttribute("CDNUT");
		 */

		String codGiornale = (String) req.getAttribute("CODGIORNALE");
		String datInizioSett = (String) req.getAttribute("DATINIZIOSETT");
		String datFineSettimana = (String) req.getAttribute("DATFINESETTIMANA");
		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if ((codGiornale != null) && (!codGiornale.equals(""))) {
			buf.append(" AND EG.CODGIORNALE = '" + codGiornale + "' ");
		}

		if (datInizioSett != null && !datInizioSett.equals(""))
			buf.append(" AND EG.DATINIZIOSETT >= to_date('" + datInizioSett + "','dd/mm/yyyy') ");
		if (datFineSettimana != null && !datFineSettimana.equals(""))
			buf.append(" AND EG.DATFINESETTIMANA <= to_date('" + datFineSettimana + "','dd/mm/yyyy') ");
		buf.append(
				" GROUP BY EG.PRGELENCOGIORNALE, EG.codgiornale , GI.STRDESCRIZIONE, EG.DATINIZIOSETT, EG.DATFINESETTIMANA ");
		buf.append(" ORDER BY DAT_SORT DESC");

		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}