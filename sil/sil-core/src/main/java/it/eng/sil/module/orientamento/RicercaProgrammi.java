package it.eng.sil.module.orientamento;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.util.Utils;

public class RicercaProgrammi implements IDynamicStatementProvider {
	public RicercaProgrammi() {
	}

	private static final String SELECT_SQL_BASE = " select distinct or_programma_q.prgprogrammaq, " + " strtitolo, "
			+ " or_programma_q.strnote, " + " strcodiceesterno, "
			+ " to_char(or_programma_q.datinizio, 'dd/mm/yyyy') as datinizio, "
			+ " to_char(or_programma_q.datfine, 'dd/mm/yyyy') as datfine, " + " or_programma_q.prgazienda, "
			+ " or_programma_q.prgunita, " + " de_stato_programma.strdescrizione as codstatoprogramma,  "
			+ " STRRAGIONESOCIALE, " + " case   " + " WHEN (select orc.prgcolloquio from or_percorso_concordato opc  "
			+ "                            inner join or_colloquio orc on (orc.prgcolloquio = opc.prgcolloquio)  "
			+ "                            where orc.cdnlavoratore = ";

	private static final String SELECT_SQL_BASE2 = " and opc.prgprogrammaq =   or_programma_q.prgprogrammaq and rownum=1) is not null THEN  "
			+ " '<IMG name=\"image\" border=\"0\" src=\"../../img/warning_trasp.gif\" alt=\"Il programma già è associato\" title=\" \" />' "
			+ " ELSE  " + " ''  " + " END as esiste  " + " from or_programma_q "
			+ " inner join de_stato_programma on or_programma_q.codstatoprogramma =  de_stato_programma.codstatoprogramma "
			+ " left join an_unita_azienda aun on (aun.prgazienda = or_programma_q.prgazienda and aun.prgunita= or_programma_q.prgunita) "
			+ " left join an_azienda az on(az.prgazienda = aun.prgazienda)  ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String strtitolo = null;
		String strcodiceesterno = null;
		String datinizio = null;
		String datfine = null;
		String codstatoprogramma = null;
		String strAzienda = null;

		String notexists = " ";

		String cdnLav = null;
		cdnLav = Utils.notNull(req.getAttribute("cdnLavoratore"));
		strtitolo = Utils.notNull(req.getAttribute("strtitolo"));
		strcodiceesterno = Utils.notNull(req.getAttribute("strcodiceesterno"));
		datinizio = Utils.notNull(req.getAttribute("datinizio"));
		datfine = Utils.notNull(req.getAttribute("datfine"));
		codstatoprogramma = Utils.notNull(req.getAttribute("codstatoprogramma"));

		strAzienda = Utils.notNull(req.getAttribute("strAzienda"));

		// cdn lav nella query
		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer query_totale2 = new StringBuffer(SELECT_SQL_BASE2);
		query_totale.append(cdnLav);
		query_totale.append(query_totale2);

		String buf = "";

		if ((strtitolo != null && !strtitolo.equals("")) || (strAzienda != null && !strAzienda.equals(""))
				|| (datinizio != null && !datinizio.equals("")) || (datfine != null && !datfine.equals(""))
				|| (codstatoprogramma != null && !codstatoprogramma.equals(""))) {
			buf += "where";
		}

		if (strtitolo != null && !strtitolo.equals("")) {
			buf += " upper(strtitolo) like upper('%" + strtitolo + "%')";
		}

		if (strAzienda != null && !strAzienda.equals("")) {
			if (buf.equals("where")) {
				buf += " upper(STRRAGIONESOCIALE) like upper('%" + strAzienda + "%')";
			} else {
				buf += " and upper(STRRAGIONESOCIALE) like upper('%" + strAzienda + "%')";
			}
		}

		if (datinizio != null && !datinizio.equals("")) {
			if (buf.equals("where")) {
				buf += " trunc(or_programma_q.datinizio) >= trunc(to_date('" + datinizio + " ', 'DD/MM/YYYY')) ";
			} else {
				buf += " and trunc(or_programma_q.datinizio) >= trunc(to_date('" + datinizio + " ', 'DD/MM/YYYY')) ";

			}
		}

		//
		if (datfine != null && !datfine.equals("")) {
			if (buf.equals("where")) {
				buf += " trunc(or_programma_q.datfine) <= trunc(to_date('" + datfine + " ', 'DD/MM/YYYY')) ";
			} else {
				buf += " and trunc(or_programma_q.datfine) <= trunc(to_date('" + datfine + " ', 'DD/MM/YYYY')) ";
			}
		}

		if (codstatoprogramma != null && !codstatoprogramma.equals("")) {
			if (buf.equals("where")) {
				buf += " or_programma_q.codstatoprogramma = '" + codstatoprogramma + "'";
			} else {
				buf += " and or_programma_q.codstatoprogramma = '" + codstatoprogramma + "'";
			}
		}

		/*
		 * if (buf.equals("")){ notexists = " "; } else { notexists = " and "; }
		 * 
		 * notexists += "  not exists (select opc.prgprogrammaq  from or_percorso_concordato opc " +
		 * "  inner join or_colloquio orc on orc.prgcolloquio = opc.prgcolloquio " +
		 * "  where opc.prgprogrammaq = or_programma_q.prgprogrammaq and orc.cdnlavoratore =  "+ cdnLav + " ) " ;
		 * 
		 */
		if (!buf.equals("")) {
			notexists += " order by datinizio desc,  strtitolo ";
			query_totale.append(buf);
			query_totale.append(notexists);

		} else {
			query_totale.append(notexists);
			query_totale.append(" order by datinizio desc,  strtitolo ");
		}

		return query_totale.toString();

	}
}
