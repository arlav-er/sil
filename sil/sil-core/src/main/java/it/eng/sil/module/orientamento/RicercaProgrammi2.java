package it.eng.sil.module.orientamento;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.sil.util.Utils;

public class RicercaProgrammi2 implements IDynamicStatementProvider {
	public RicercaProgrammi2() {
	}

	private static final String SELECT_SQL_BASE = " select distinct or_programma_q.prgprogrammaq, " + " strtitolo, "
			+ " or_programma_q.strnote, " + " strcodiceesterno, "
			+ " to_char(or_programma_q.datinizio, 'dd/mm/yyyy') as datinizio, "
			+ " to_char(or_programma_q.datfine, 'dd/mm/yyyy') as datfine, " + " or_programma_q.prgazienda, "
			+ " or_programma_q.prgunita, " + " de_stato_programma.strdescrizione as codstatoprogramma,  "
			+ " az.STRRAGIONESOCIALE as ente  " +
			// " ,STRRAGIONESOCIALE "+
			" from or_programma_q "
			+ " inner join de_stato_programma on or_programma_q.codstatoprogramma =  de_stato_programma.codstatoprogramma "
			+ " left join an_unita_azienda aun on (aun.prgazienda = or_programma_q.prgazienda and aun.prgunita= or_programma_q.prgunita) "
			+ " left join an_azienda az on(az.prgazienda = aun.prgazienda) ";
	/*
	 * + " inner join or_percorso_concordato opc on or_programma_q.prgprogrammaq = opc.prgprogrammaq "+
	 * " inner join or_colloquio orc on orc.prgcolloquio = opc.prgcolloquio "+
	 * " inner join an_lavoratore an on an.cdnlavoratore = orc.cdnlavoratore ";
	 */

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String strtitolo = null;
		String datinizio = null;
		String datfine = null;
		String codstatoprogramma = null;
		String strAzienda = null;

		String notexists = null;

		String strcodiceFiscale = null;
		strcodiceFiscale = Utils.notNull(req.getAttribute("strcodiceFiscale"));

		strtitolo = Utils.notNull(req.getAttribute("strtitolo"));
		datinizio = Utils.notNull(req.getAttribute("datinizio"));
		datfine = Utils.notNull(req.getAttribute("datfine"));
		codstatoprogramma = Utils.notNull(req.getAttribute("codstatoprogramma"));

		strAzienda = Utils.notNull(req.getAttribute("strAzienda"));

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);

		String buf = " where ";

		if (strtitolo != null && !strtitolo.equals("")) {
			buf += " upper(strtitolo) like upper('%" + strtitolo + "%')";
		}

		if (strAzienda != null && !strAzienda.equals("")) {
			if (buf.equals(" where ")) {
				buf += " upper(STRRAGIONESOCIALE) like upper('%" + strAzienda + "%')";
			} else {
				buf += " and upper(STRRAGIONESOCIALE) like upper('%" + strAzienda + "%')";
			}
		}

		if (strcodiceFiscale != null && !strcodiceFiscale.equals("")) {
			if (buf.equals(" where ")) {
				buf += " upper(an.strcodicefiscale) like upper('%" + strcodiceFiscale + "%')";
			} else {
				buf += " and upper(an.strcodicefiscale) like upper('%" + strcodiceFiscale + "%')";
			}
		}

		if (datinizio != null && !datinizio.equals("")) {
			if (buf.equals(" where ")) {
				buf += " trunc(or_programma_q.datinizio) >= trunc(to_date('" + datinizio + "','DD/MM/YYYY')) ";
			} else {
				buf += " and trunc(or_programma_q.datinizio) >= trunc(to_date('" + datinizio + "','DD/MM/YYYY')) ";

			}
		}

		//
		if (datfine != null && !datfine.equals("")) {
			if (buf.equals(" where ")) {
				buf += " trunc(or_programma_q.datfine) <= trunc(to_date('" + datfine + "','DD/MM/YYYY')) ";
			} else {
				buf += " and trunc(or_programma_q.datfine) <= trunc(to_date('" + datfine + "','DD/MM/YYYY')) ";
			}
		}

		if (codstatoprogramma != null && !codstatoprogramma.equals("")) {
			if (buf.equals(" where ")) {
				buf += " or_programma_q.codstatoprogramma = '" + codstatoprogramma + "'";
			} else {
				buf += " and or_programma_q.codstatoprogramma = '" + codstatoprogramma + "'";
			}
		}

		if (!buf.equals(" where ")) {
			notexists += " order by datinizio desc,  strtitolo ";
			query_totale.append(buf);
		} else {
			query_totale.append(" order by datinizio desc,  strtitolo ");
		}

		return query_totale.toString();

	}
}
