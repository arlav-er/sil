package it.eng.sil.module.cig;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.SourceBeanUtils;

public class DynStatementScadenzeCig implements IDynamicStatementProvider {
	private static final int SCADENZA_LAV_PRIMA_PRESA_IN_CARICO_CIG = 1;
	private static final int SCADENZA_MANCATA_PRESENTAZIONE_CIG = 2;
	private static final int SCADENZA_DUE_ESITI_NEGATIVI_STESSA_AZIONE = 3;
	private static final int SCADENZA_ASSENZA_CORSI = 4;

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		SourceBean req = requestContainer.getServiceRequest();
		String SELECT_SQL_BASE = "";
		String SELECT = null;
		String FROM = null;
		String WHERE = null;
		String ORDER = null;

		String strCodScadenza = SourceBeanUtils.getAttrStrNotNull(req, "SCADENZIARIO");
		String codEsito = SourceBeanUtils.getAttrStrNotNull(req, "CODESITO");
		String codCPI = SourceBeanUtils.getAttrStrNotNull(req, "CodCPI");

		int nCodScadenza = 0;
		if (strCodScadenza.compareTo("CIG1") == 0) {
			nCodScadenza = SCADENZA_LAV_PRIMA_PRESA_IN_CARICO_CIG;
		} else if (strCodScadenza.compareTo("CIG2") == 0) {
			nCodScadenza = SCADENZA_MANCATA_PRESENTAZIONE_CIG;
		} else if (strCodScadenza.compareTo("CIG3") == 0) {
			nCodScadenza = SCADENZA_DUE_ESITI_NEGATIVI_STESSA_AZIONE;
		} else if (strCodScadenza.compareTo("CIG4") == 0) {
			nCodScadenza = SCADENZA_ASSENZA_CORSI;
		}

		switch (nCodScadenza) {
		case SCADENZA_LAV_PRIMA_PRESA_IN_CARICO_CIG:
			String dataInizioIscr = SourceBeanUtils.getAttrStrNotNull(req, "DATINIZIOISCR");

			SELECT = " select iscr.PRGALTRAISCR,lav.cdnlavoratore, lav.strcognome, lav.strnome, lav.strcodicefiscale, "
					+ "		  to_char(lav.datnasc,'dd/mm/yyyy') as datnasc, "
					+ "		  to_char(iscr.datinizio,'dd/mm/yyyy') as datinizio, "
					+ "       to_char(iscr.DATFINE,'dd/mm/yyyy') as DATFINE, "
					+ "		  'dal ' || to_char(iscr.datinizio,'dd/mm/yyyy') || ' al ' || to_char(iscr.datfine,'dd/mm/yyyy') || ' az. ' || azi.strragionesociale as iscrizione, "
					+ "		  '<a href=\"#\" onclick=\"appuntamenti('||lav.cdnlavoratore||')\"> <IMG name=\"image\" border=\"0\" src=\"../../img/agendina.gif\" alt=\"Appuntamenti\" title=\"Appuntamenti\" /></a>' as App, "
					+ "       '<a href=\"#\" onclick=\"contatti('||lav.cdnlavoratore||')\"> <IMG name=\"image\" border=\"0\" src=\"../../img/contatti.gif\" alt=\"Contatti\" title=\"Contatti\" /></a>' as Con, "
					+ "        tipoIscr.strdescrizione tipoIscr ";

			FROM = " from am_altra_iscr iscr "
					+ " inner join an_lavoratore lav on (lav.cdnlavoratore = iscr.cdnlavoratore) "
					+ " inner join de_tipo_iscr tipoIscr on (iscr.codtipoiscr = tipoIscr.codtipoiscr) "
					+ " left join an_azienda azi on (azi.prgazienda = iscr.prgazienda) ";

			WHERE = " where iscr.DATCHIUSURAISCR is null " + "       and iscr.CODSTATO is null "
					+ "       and trunc(iscr.DATFINE) > trunc(sysdate) "
					+ "       and iscr.CODTIPOISCR IN ('O','S','M') " + "       and ( not exists "
					+ "				(select 1 " + "				 from or_colloquio "
					+ "				 where prgaltraiscr = iscr.PRGALTRAISCR) " + "		  		 or not exists "
					+ "					(select 1 " + "				 	 from or_colloquio coll "
					+ "				     inner join or_percorso_concordato per on (per.prgcolloquio = coll.prgcolloquio) "
					+ "				     left join de_esito es on (es.codesito = per.codesito) "
					+ "				     where coll.PRGALTRAISCR = iscr.PRGALTRAISCR "
					+ " 					   and per.prgazioni = 151 "
					+ "					       and ( per.codesitorendicont = 'E' or "
					+ "								 (per.codesitorendicont = 'P' and trunc(per.datstimata) > trunc(sysdate)) )  ) ) ";

			if ((dataInizioIscr != null) && (!dataInizioIscr.equals(""))) {
				WHERE = WHERE + " AND iscr.datinizio <= to_date('" + dataInizioIscr + "','dd/mm/yyyy')";
			}
			if ((codCPI != null) && (!codCPI.equals(""))) {
				FROM = FROM + " inner join an_lav_storia_inf inf on (lav.cdnlavoratore = inf.cdnlavoratore) ";
				WHERE = WHERE + " AND inf.datFine is null " + " AND inf.codCpiTit = '" + codCPI + "' "
						+ " AND inf.codMonoTipoCpi = 'C' ";
			}

			SELECT_SQL_BASE = SELECT + FROM + WHERE;

			break;

		case SCADENZA_MANCATA_PRESENTAZIONE_CIG:
			String datStimata = SourceBeanUtils.getAttrStrNotNull(req, "DATSTIMATA");
			if (codEsito.equalsIgnoreCase("NP1")) {
				SELECT = "select lav.cdnlavoratore, lav.strcognome, lav.strnome, lav.strcodicefiscale, "
						+ "		  to_char(lav.datnasc,'dd/mm/yyyy') as datnasc, "
						+ "		  to_char(iscr.datinizio,'dd/mm/yyyy') as datinizio, "
						+ "		  'dal ' || to_char(iscr.datinizio,'dd/mm/yyyy') || ' al ' || to_char(iscr.datfine,'dd/mm/yyyy') || ' az. ' || azi.strragionesociale as iscrizione, "
						+ "       '<a href=\"#\" onclick=\"appuntamenti('||lav.cdnlavoratore||')\"> <IMG name=\"image\" border=\"0\" src=\"../../img/agendina.gif\" alt=\"Appuntamenti\" title=\"Appuntamenti\" /></a>' as App, "
						+ "       '<a href=\"#\" onclick=\"contatti('||lav.cdnlavoratore||')\"> <IMG name=\"image\" border=\"0\" src=\"../../img/contatti.gif\" alt=\"Contatti\" title=\"Contatti\" /></a>' as Con, "
						+ "        tipoIscr.strdescrizione tipoIscr ";

				FROM = "from am_altra_iscr iscr "
						+ "inner join an_lavoratore lav on (lav.cdnlavoratore = iscr.cdnlavoratore) "
						+ "inner join de_tipo_iscr tipoIscr on (iscr.codtipoiscr = tipoIscr.codtipoiscr) "
						+ "left join an_azienda azi on (azi.prgazienda = iscr.prgazienda) "
						+ "inner join or_colloquio coll on (coll.prgaltraiscr = iscr.PRGALTRAISCR) "
						+ "inner join or_percorso_concordato per on (per.prgcolloquio = coll.prgcolloquio and per.PRGAZIONI = 151 and per.codesito = 'NP1') ";
				WHERE = "where iscr.DATCHIUSURAISCR is null " + "		 and iscr.CODSTATO is null "
						+ "		 and trunc(iscr.DATFINE) > trunc(sysdate) "
						+ "      and iscr.CODTIPOISCR IN ('O','S','M') " + "		 and not exists "
						+ "			(select 1 " + "			 from or_colloquio coll1 "
						+ "			 inner join or_percorso_concordato per1 on (per1.prgcolloquio = coll1.prgcolloquio) "
						+ "			 left join de_esito es1 on (es1.codesito = per1.codesito) "
						+ "			 where coll1.PRGALTRAISCR = iscr.PRGALTRAISCR "
						+ "				   and per1.PRGAZIONI = 151 and per1.prgpercorso != per.prgpercorso"
						+ "				   and ( (per1.dateffettiva > nvl(per.dateffettiva,per.datstimata) and ( per1.codesitorendicont = 'E') or per1.codesito = 'NP2') "
						+ "				   		  or trunc(per1.datstimata) >= trunc(sysdate) ) ) ";

				if ((datStimata != null) && (!datStimata.equals(""))) {
					WHERE = WHERE + " AND per.datstimata = to_date('" + datStimata + "','dd/mm/yyyy')";
				}

				if ((codCPI != null) && (!codCPI.equals(""))) {
					FROM = FROM + "  inner join an_lav_storia_inf inf on (lav.cdnlavoratore = inf.cdnlavoratore) ";
					WHERE = WHERE + " AND inf.datFine is null " + " AND inf.codCpiTit = '" + codCPI + "' "
							+ " AND inf.codMonoTipoCpi = 'C' ";
				}
				SELECT_SQL_BASE = SELECT + FROM + WHERE;
			} else {
				if (codEsito.equalsIgnoreCase("NP2")) {
					SELECT = "select lav.cdnlavoratore, lav.strcognome, lav.strnome, lav.strcodicefiscale, "
							+ "		  to_char(lav.datnasc,'dd/mm/yyyy') as datnasc, "
							+ "		  to_char(iscr.datinizio,'dd/mm/yyyy') as datinizio, "
							+ "		  'dal ' || to_char(iscr.datinizio,'dd/mm/yyyy') || ' al ' || to_char(iscr.datfine,'dd/mm/yyyy') || ' az. ' || azi.strragionesociale as iscrizione, "
							+ "       '<a href=\"#\" onclick=\"appuntamenti('||lav.cdnlavoratore||')\"> <IMG name=\"image\" border=\"0\" src=\"../../img/agendina.gif\" alt=\"Appuntamenti\" title=\"Appuntamenti\" /></a>' as App, "
							+ "       '<a href=\"#\" onclick=\"contatti('||lav.cdnlavoratore||')\"> <IMG name=\"image\" border=\"0\" src=\"../../img/contatti.gif\" alt=\"Contatti\" title=\"Contatti\" /></a>' as Con, "
							+ "        tipoIscr.strdescrizione tipoIscr ";
					FROM = "from am_altra_iscr iscr "
							+ "inner join an_lavoratore lav on (lav.cdnlavoratore = iscr.cdnlavoratore) "
							+ "inner join de_tipo_iscr tipoIscr on (iscr.codtipoiscr = tipoIscr.codtipoiscr) "
							+ "left join an_azienda azi on (azi.prgazienda = iscr.prgazienda) "
							+ "inner join or_colloquio coll on (coll.prgaltraiscr = iscr.PRGALTRAISCR) "
							+ "inner join or_percorso_concordato per on (per.prgcolloquio = coll.prgcolloquio and per.prgazioni = 151 and per.codesito = 'NP2') ";
					WHERE = "where iscr.DATCHIUSURAISCR is null " + "		 and iscr.CODSTATO is null "
							+ "		 and trunc(iscr.DATFINE) > trunc(sysdate) "
							+ "      and iscr.CODTIPOISCR IN ('O','S','M') " + "		 and not exists "
							+ "			(select 1 " + "			 from or_colloquio coll1 "
							+ "			 inner join or_percorso_concordato per1 on (per1.prgcolloquio = coll1.prgcolloquio) "
							+ "			 left join de_esito es1 on (es1.codesito = per1.codesito) "
							+ "			 where coll1.PRGALTRAISCR = iscr.PRGALTRAISCR "
							+ "				   and per1.PRGAZIONI = 151 and per1.prgpercorso != per.prgpercorso "
							+ "				   and ( (per1.dateffettiva > nvl(per.dateffettiva,per.datstimata) and per1.codesitorendicont = 'E') "
							+ "                or trunc(per1.datstimata) >= trunc(sysdate) ) )  ";

					if ((datStimata != null) && (!datStimata.equals(""))) {
						WHERE = WHERE + " AND per.datstimata = to_date('" + datStimata + "','dd/mm/yyyy')";
					}
					if ((codCPI != null) && (!codCPI.equals(""))) {
						FROM = FROM + " inner join an_lav_storia_inf inf on (lav.cdnlavoratore = inf.cdnlavoratore) ";
						WHERE = WHERE + " AND inf.datFine is null " + " AND inf.codCpiTit = '" + codCPI + "' "
								+ " AND inf.codMonoTipoCpi = 'C' ";
					}
					SELECT_SQL_BASE = SELECT + FROM + WHERE;
				} else {
					if (codEsito.equalsIgnoreCase("AGA") || codEsito.equalsIgnoreCase("AGD")
							|| codEsito.equalsIgnoreCase("AGS")) {
						SELECT = "select lav.cdnlavoratore, lav.strcognome, lav.strnome, lav.strcodicefiscale, "
								+ "		  to_char(lav.datnasc,'dd/mm/yyyy') as datnasc, "
								+ "		  to_char(iscr.datinizio,'dd/mm/yyyy') as datinizio, "
								+ "		  'dal ' || to_char(iscr.datinizio,'dd/mm/yyyy') || ' al ' || to_char(iscr.datfine,'dd/mm/yyyy') || ' az. ' || azi.strragionesociale as iscrizione,"
								+ "       '<a href=\"#\" onclick=\"appuntamenti('||lav.cdnlavoratore||')\"> <IMG name=\"image\" border=\"0\" src=\"../../img/agendina.gif\" alt=\"Appuntamenti\" title=\"Appuntamenti\" /></a>' as App, "
								+ "       '<a href=\"#\" onclick=\"contatti('||lav.cdnlavoratore||')\"> <IMG name=\"image\" border=\"0\" src=\"../../img/contatti.gif\" alt=\"Contatti\" title=\"Contatti\" /></a>' as Con, "
								+ "        tipoIscr.strdescrizione tipoIscr ";
						FROM = "from am_altra_iscr iscr "
								+ "inner join an_lavoratore lav on (lav.cdnlavoratore = iscr.cdnlavoratore) "
								+ "inner join de_tipo_iscr tipoIscr on (iscr.codtipoiscr = tipoIscr.codtipoiscr) "
								+ "left join an_azienda azi on (azi.prgazienda = iscr.prgazienda) "
								+ "inner join or_colloquio coll on (coll.prgaltraiscr = iscr.PRGALTRAISCR) "
								+ "inner join or_percorso_concordato per on (per.prgcolloquio = coll.prgcolloquio and per.codesito in ('AGA','AGD','AGS')) ";
						WHERE = "where iscr.DATCHIUSURAISCR is null and iscr.CODSTATO is null "
								+ "		 and trunc(iscr.DATFINE) > trunc(sysdate) "
								+ "      and iscr.CODTIPOISCR IN ('O','S','M') " + "      and per.codesito = '"
								+ codEsito.toUpperCase() + "'" + "		 and not exists " + "			( select 1 "
								+ "			  from or_colloquio coll1 "
								+ "			  inner join or_percorso_concordato per1 on (per1.prgcolloquio = coll1.prgcolloquio) "
								+ "			  inner join de_esito es1 on (es1.codesito = per1.codesito) "
								+ "			  where coll1.PRGALTRAISCR = iscr.PRGALTRAISCR "
								+ "					and per1.dateffettiva > nvl(per.dateffettiva,per.datstimata) and per1.codesitorendicont = 'E' ) "
								+ "		and per.prgazioni NOT IN " + "			(select per2.prgazioni "
								+ "			 from or_colloquio coll2 "
								+ "			 inner join or_percorso_concordato per2 on (per2.prgcolloquio = coll2.prgcolloquio) "
								+ "			 where coll2.PRGALTRAISCR = iscr.PRGALTRAISCR and per2.prgpercorso != per.prgpercorso "
								+ "				   and per2.datstimata > per.datstimata and trunc(per2.datstimata) >= trunc(sysdate) ) ";

						if ((datStimata != null) && (!datStimata.equals(""))) {
							WHERE = WHERE + " AND per.datstimata = to_date('" + datStimata + "','dd/mm/yyyy')";
						}
						if ((codCPI != null) && (!codCPI.equals(""))) {
							FROM = FROM
									+ " inner join an_lav_storia_inf inf on (lav.cdnlavoratore = inf.cdnlavoratore) ";
							WHERE = WHERE + " AND inf.datFine is null " + " AND inf.codCpiTit = '" + codCPI + "' "
									+ " AND inf.codMonoTipoCpi = 'C' ";
						}
						SELECT_SQL_BASE = SELECT + FROM + WHERE;
					}
				}
			}

			break;

		case SCADENZA_DUE_ESITI_NEGATIVI_STESSA_AZIONE:
			String codAzione = SourceBeanUtils.getAttrStrNotNull(req, "CODAZIONE");

			SELECT = "select iscr.PRGALTRAISCR,lav.cdnlavoratore, lav.strcognome, lav.strnome, lav.strcodicefiscale, "
					+ "		  to_char(lav.datnasc,'dd/mm/yyyy') as datnasc,"
					+ "		  to_char(iscr.datinizio,'dd/mm/yyyy') as datinizio,"
					+ "       to_char(iscr.DATFINE,'dd/mm/yyyy') as DATFINE,"
					+ "       'dal ' || to_char(iscr.datinizio,'dd/mm/yyyy') || ' al ' || to_char(iscr.datfine,'dd/mm/yyyy') || ' az. ' || azi.strragionesociale as iscrizione,"
					+ "		  '<a href=\"#\" onclick=\"appuntamenti('||lav.cdnlavoratore||')\"> <IMG name=\"image\" border=\"0\" src=\"../../img/agendina.gif\" alt=\"Appuntamenti\" title=\"Appuntamenti\" /></a>' as App, "
					+ "       '<a href=\"#\" onclick=\"contatti('||lav.cdnlavoratore||')\"> <IMG name=\"image\" border=\"0\" src=\"../../img/contatti.gif\" alt=\"Contatti\" title=\"Contatti\" /></a>' as Con, "
					+ "        tipoIscr.strdescrizione tipoIscr ";
			FROM = "from am_altra_iscr iscr "
					+ "inner join an_lavoratore lav on (lav.cdnlavoratore = iscr.cdnlavoratore) "
					+ "inner join de_tipo_iscr tipoIscr on (iscr.codtipoiscr = tipoIscr.codtipoiscr) "
					+ "left join an_azienda azi on (azi.prgazienda = iscr.prgazienda) "
					+ "inner join or_colloquio col on (col.prgaltraiscr = iscr.PRGALTRAISCR) ";
			WHERE = "where iscr.DATCHIUSURAISCR is null and iscr.CODSTATO is null "
					+ "		 and trunc(iscr.DATFINE) > trunc(sysdate) "
					+ "      and iscr.CODTIPOISCR IN ('O','S','M') ";

			if ((codAzione != null) && (!codAzione.equals(""))) {
				WHERE = WHERE + " and PG_GESTIONE_CIG.existsAzioni2pos1neg(col.prgColloquio, '" + codAzione
						+ "' ) = 'true' ";
			}
			if ((codCPI != null) && (!codCPI.equals(""))) {
				FROM = FROM + "  inner join an_lav_storia_inf inf on (lav.cdnlavoratore = inf.cdnlavoratore) ";
				WHERE = WHERE + " AND inf.datFine is null " + " AND inf.codCpiTit = '" + codCPI + "' "
						+ " AND inf.codMonoTipoCpi = 'C' ";
			}
			SELECT_SQL_BASE = SELECT + FROM + WHERE;

			break;

		case SCADENZA_ASSENZA_CORSI:
			if (codEsito.equalsIgnoreCase("2X1") || codEsito.equalsIgnoreCase("7Y1")
					|| codEsito.equalsIgnoreCase("LZ1")) {
				SELECT = "select lav.cdnlavoratore, lav.strcognome, lav.strnome, lav.strcodicefiscale, "
						+ "		  to_char(lav.datnasc,'dd/mm/yyyy') as datnasc, "
						+ "		  to_char(iscr.datinizio,'dd/mm/yyyy') as datinizio,"
						+ "       'dal ' || to_char(iscr.datinizio,'dd/mm/yyyy') || ' al ' || to_char(iscr.datfine,'dd/mm/yyyy') || ' az. ' || azi.strragionesociale as iscrizione,"
						+ "       '<a href=\"#\" onclick=\"appuntamenti('||lav.cdnlavoratore||')\"> <IMG name=\"image\" border=\"0\" src=\"../../img/agendina.gif\" alt=\"Appuntamenti\" title=\"Appuntamenti\" /></a>' as App,"
						+ "       '<a href=\"#\" onclick=\"contatti('||lav.cdnlavoratore||')\"> <IMG name=\"image\" border=\"0\" src=\"../../img/contatti.gif\" alt=\"Contatti\" title=\"Contatti\" /></a>' as Con, "
						+ "        tipoIscr.strdescrizione tipoIscr ";
				FROM = "from am_altra_iscr iscr "
						+ "inner join an_lavoratore lav on (lav.cdnlavoratore = iscr.cdnlavoratore) "
						+ "inner join de_tipo_iscr tipoIscr on (iscr.codtipoiscr = tipoIscr.codtipoiscr) "
						+ "left join an_azienda azi on (azi.prgazienda = iscr.prgazienda) ";
				WHERE = "where iscr.DATCHIUSURAISCR is null and iscr.CODSTATO is null "
						+ "		 and trunc(iscr.DATFINE) > trunc(sysdate) "
						+ "      and iscr.CODTIPOISCR IN ('O','S','M') " + "	     and exists (select 1 "
						+ "		 			 from or_colloquio coll "
						+ "					 inner join or_percorso_concordato per on (per.prgcolloquio = coll.prgcolloquio) "
						+ "					 where coll.PRGALTRAISCR = iscr.PRGALTRAISCR "
						+ "						   and per.codesito in ('2X1','7Y1','LZ1') and per.prgazioni = 152 "
						+ "                        and per.codesito = '" + codEsito.toUpperCase() + "' ) "
						+ "     and not exists (select 1 " + "						from or_colloquio coll "
						+ "						inner join or_percorso_concordato per on (per.prgcolloquio = coll.prgcolloquio)"
						+ "						where coll.PRGALTRAISCR = iscr.PRGALTRAISCR "
						+ "							  and per.codesito in ('2X2','7Y2','LZ2') and per.prgazioni = 152 ) ";
				if ((codCPI != null) && (!codCPI.equals(""))) {
					FROM = FROM + "  inner join an_lav_storia_inf inf on (lav.cdnlavoratore = inf.cdnlavoratore) ";
					WHERE = WHERE + " AND inf.datFine is null " + " AND inf.codCpiTit = '" + codCPI + "' "
							+ " AND inf.codMonoTipoCpi = 'C' ";
				}
				SELECT_SQL_BASE = SELECT + FROM + WHERE;
			} else {
				if (codEsito.equalsIgnoreCase("2X2") || codEsito.equalsIgnoreCase("7Y2")
						|| codEsito.equalsIgnoreCase("LZ2")) {
					SELECT = "select lav.cdnlavoratore, lav.strcognome, lav.strnome, lav.strcodicefiscale, "
							+ "       to_char(lav.datnasc,'dd/mm/yyyy') as datnasc,"
							+ "		  to_char(iscr.datinizio,'dd/mm/yyyy') as datinizio, "
							+ "       'dal ' || to_char(iscr.datinizio,'dd/mm/yyyy') || ' al ' || to_char(iscr.datfine,'dd/mm/yyyy') || ' az. ' || azi.strragionesociale as iscrizione,"
							+ "       '<a href=\"#\" onclick=\"appuntamenti('||lav.cdnlavoratore||')\"> <IMG name=\"image\" border=\"0\" src=\"../../img/agendina.gif\" alt=\"Appuntamenti\" title=\"Appuntamenti\" /></a>' as App,"
							+ "       '<a href=\"#\" onclick=\"contatti('||lav.cdnlavoratore||')\"> <IMG name=\"image\" border=\"0\" src=\"../../img/contatti.gif\" alt=\"Contatti\" title=\"Contatti\" /></a>' as Con, "
							+ "        tipoIscr.strdescrizione tipoIscr ";
					FROM = "from am_altra_iscr iscr "
							+ "inner join an_lavoratore lav on (lav.cdnlavoratore = iscr.cdnlavoratore) "
							+ "inner join de_tipo_iscr tipoIscr on (iscr.codtipoiscr = tipoIscr.codtipoiscr) "
							+ "left join an_azienda azi on (azi.prgazienda = iscr.prgazienda) ";
					WHERE = "where iscr.DATCHIUSURAISCR is null and iscr.CODSTATO is null and trunc(iscr.DATFINE) > trunc(sysdate) "
							+ "      and iscr.CODTIPOISCR IN ('O','S','M') " + "		 and exists (select 1 "
							+ "					 from or_colloquio coll "
							+ "					 inner join or_percorso_concordato per on (per.prgcolloquio = coll.prgcolloquio) "
							+ "					 where coll.PRGALTRAISCR = iscr.PRGALTRAISCR "
							+ "						   and per.codesito in ('2X2','7Y2','LZ2') and per.prgazioni = 152 "
							+ "                        and per.codesito = '" + codEsito.toUpperCase() + "' ) ";
					if ((codCPI != null) && (!codCPI.equals(""))) {
						FROM = FROM + " inner join an_lav_storia_inf inf on (lav.cdnlavoratore = inf.cdnlavoratore) ";
						WHERE = WHERE + " AND inf.datFine is null " + " AND inf.codCpiTit = '" + codCPI + "' "
								+ " AND inf.codMonoTipoCpi = 'C' ";
					}
					SELECT_SQL_BASE = SELECT + FROM + WHERE;
				}
			}

			break;
		}
		return SELECT_SQL_BASE;
	}
}
