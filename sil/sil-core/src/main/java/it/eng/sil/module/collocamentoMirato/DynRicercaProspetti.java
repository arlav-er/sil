package it.eng.sil.module.collocamentoMirato;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.collocamentoMirato.constant.ProspettiConstant;

public class DynRicercaProspetti implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = " select p.PRGPROSPETTOINF, p.PRGAZIENDA, p.PRGUNITA, p.PRGAZREFERENTE, "
			+ "    az.strcodicefiscale as codicefiscale, " + " 	az.strpartitaiva as piva, "
			+ "	az.strragionesociale as ragionesociale, " + " 	p.numannorifprospetto as anno, "
			+ " 	to_char(p.datprospetto, 'DD/MM/YYYY') as datprospetto, "
			+ " 	to_char(p.datConsegnaProspetto, 'DD/MM/YYYY') as datConsegnaProspetto, "
			+ " 	( decode(to_char(p.datprospetto, 'DD/MM/YYYY'),NULL,'<BR/>',to_char(p.datprospetto, 'DD/MM/YYYY')) || ' ' || to_char(p.datConsegnaProspetto, 'DD/MM/YYYY') ) as datprospCons, "
			+ "	p.codmonostatoprospetto, " + "    CASE " + "    WHEN p.codmonostatoprospetto = 'A'  "
			+ "	     THEN 'In corso d&#39;anno' " + "    WHEN p.codmonostatoprospetto = 'S'     "
			+ "      THEN 'Storicizzato' " + "    WHEN p.codmonostatoprospetto = 'V' "
			+ "      THEN 'SARE: storicizzato' " + "    WHEN p.codmonostatoprospetto = 'N' " + "      THEN 'Annullato' "
			+ "    WHEN p.codmonostatoprospetto = 'U' " + "	     THEN 'Storicizzato: uscita dall&#39;obbligo' "
			+ "    END as stato," + "    CASE " + "    WHEN p.codmonocategoria = 'A'  "
			+ "	     THEN 'più di 50 dipendenti' " + "    WHEN p.codmonocategoria = 'B'     "
			+ "      THEN 'da 36 a 50 dipendenti' " + "    WHEN p.codmonocategoria = 'C' "
			+ "      THEN 'da 15 a 35 dipendenti' " + "	ELSE '&nbsp;' " + "    END as fascia," + "    CASE "
			+ "    WHEN pg_coll_mirato_2.checkscopertura(p.prgprospettoinf, 'N') = 1  " + "	     THEN 'Sì' "
			+ "    WHEN pg_coll_mirato_2.checkscopertura(p.prgprospettoinf, 'N') = 0    " + "      THEN 'No' "
			+ "    END as scoperture," +

			"	p.codprovincia as prov, " + "    dp.strdenominazione as provincia, " + " 	doc.codStatoAtto, "
			+ "    stato.STRDESCRIZIONE as statoAtto, " + " 	to_char(doc.DATPROTOCOLLO,'dd/mm/yyyy') as dataProt, "
			+ " 	to_char(doc.DATINIZIO,'dd/mm/yyyy') as DatInizio, "
			+ " 	to_char(doc.DATACQRIL,'dd/mm/yyyy') as DatAcqRil, "
			+ " 	to_char(doc.DATPROTOCOLLO,'DD/MM/YYYY hh24:mi') as dataOraProt, "
			+ " 	doc.NUMPROTOCOLLO as NUMPROTOCOLLO, doc.NUMANNOPROT as numAnnoProt, "
			+ "	de_doc_tipo.strio striodoc, de_doc_tipo.codtipodocumento codtipodoc, "
			+ "	de_doc_tipo.strdescrizione strtipodoc, de_doc_tipo.codambitodoc codambitodoc, "
			+ "	de_doc_ambito.strdescrizione strambitodoc " + "    from cm_prospetto_inf p "
			+ "	inner join an_azienda az on az.prgazienda = p.prgazienda "
			+ "	inner join AN_UNITA_AZIENDA auz on az.prgAzienda=auz.prgAzienda "
			+ "									and auz.prgunita = p.prgunita "
			+ "    inner join de_provincia dp on dp.codprovincia = p.codprovincia "
			+ "    inner join AM_DOCUMENTO_COLL coll ON (p.PRGPROSPETTOINF = to_number(coll.STRCHIAVETABELLA)) "
			+ "    inner join AM_DOCUMENTO doc ON (doc.PRGDOCUMENTO = coll.PRGDOCUMENTO) "
			+ "    inner join de_stato_atto stato on (doc.CODSTATOATTO = stato.CODSTATOATTO) "
			+ "    inner join de_doc_tipo on (de_doc_tipo.codtipodocumento = doc.codtipodocumento) "
			+ "    inner join de_doc_ambito on (de_doc_ambito.codambitodoc = de_doc_tipo.codambitodoc) "
			+ "    where 1=1 and doc.CODTIPODOCUMENTO = 'PINF' ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		SourceBean req = requestContainer.getServiceRequest();
		String prgAziendaApp = (String) req.getAttribute("prgAziendaApp");
		String flgVerOperatore = (String) req.getAttribute("flgVerOperatore");
		String codMonoCategoria = (String) req.getAttribute("codMonoCategoria");
		String anno = (String) req.getAttribute("anno");
		String datConsDal = (String) req.getAttribute("datConsDal");
		String datConsAl = (String) req.getAttribute("datConsAl");
		String datInsDal = (String) req.getAttribute("datInsDal");
		String datInsAl = (String) req.getAttribute("datInsAl");
		String codMonoStatoProspetto = (String) req.getAttribute("codMonoStatoProspetto");
		String codMonoProv = (String) req.getAttribute("codMonoProv");
		String flgScopertura = (String) req.getAttribute("flgScopertura");
		String flgConvenzione = (String) req.getAttribute("flgConvenzione");
		String codProvincia = (String) req.getAttribute("codProvincia");
		String CODSTATOATTO = (String) req.getAttribute("StatoAtto");
		String codComunicazione = (String) req.getAttribute("codComunicazione");
		String flgSospNazionale = (String) req.getAttribute("flgSospensioneMob");
		String flgCapoGruppo = (String) req.getAttribute("flgCapoGruppo");
		String strCodFiscAzCapogruppo = (String) req.getAttribute("strCFAZCapogruppo");
		String flgCompetenza = (String) req.getAttribute("flgCompetenzaProsp");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if ((prgAziendaApp != null) && (!prgAziendaApp.equals(""))) {
			prgAziendaApp = StringUtils.replace(prgAziendaApp, "'", "''");
			buf.append(" and  az.prgAzienda = '" + prgAziendaApp + "' ");
		}

		if ((flgVerOperatore != null) && (!flgVerOperatore.equals(""))) {
			flgVerOperatore = StringUtils.replace(flgVerOperatore, "'", "''");
			buf.append("and nvl(flgVerOperatore,'N') = '" + flgVerOperatore + "' ");
		}

		if ((codMonoCategoria != null) && (!codMonoCategoria.equals(""))) {
			if (codMonoCategoria.equals(ProspettiConstant.CATEGORIA_NULLA)) {
				buf.append("and p.codmonocategoria is null ");
			} else {
				codMonoCategoria = StringUtils.replace(codMonoCategoria, "'", "''");
				buf.append("and p.codmonocategoria = '" + codMonoCategoria + "' ");
			}
		}

		if ((anno != null) && (!anno.equals(""))) {
			anno = StringUtils.replace(anno, "'", "''");
			buf.append("and p.numannorifprospetto = " + anno + " ");
		}

		if ((datConsDal != null) && (!datConsDal.equals(""))) {
			datConsDal = StringUtils.replace(datConsDal, "'", "''");
			buf.append(" AND trunc(p.datConsegnaProspetto) >= to_date('" + datConsDal + "', 'DD/MM/YYYY') ");
		}

		if ((datConsAl != null) && (!datConsAl.equals(""))) {
			datConsAl = StringUtils.replace(datConsAl, "'", "''");
			buf.append(" AND trunc(p.datConsegnaProspetto) <= to_date('" + datConsAl + "', 'DD/MM/YYYY') ");
		}

		if ((datInsDal != null) && (!datInsDal.equals(""))) {
			datInsDal = StringUtils.replace(datInsDal, "'", "''");
			buf.append(" AND trunc(p.dtmins) >= to_date('" + datInsDal + "', 'DD/MM/YYYY') ");
		}

		if ((datInsAl != null) && (!datInsAl.equals(""))) {
			datInsAl = StringUtils.replace(datInsAl, "'", "''");
			buf.append(" AND trunc(p.dtmins) <= to_date('" + datInsAl + "', 'DD/MM/YYYY') ");
		}

		if ((codMonoStatoProspetto != null) && (!codMonoStatoProspetto.equals(""))) {
			codMonoStatoProspetto = StringUtils.replace(codMonoStatoProspetto, "'", "''");
			buf.append("and p.codMonoStatoProspetto = '" + codMonoStatoProspetto + "' ");
		}

		if ((codMonoProv != null) && (!codMonoProv.equals(""))) {
			codMonoProv = StringUtils.replace(codMonoProv, "'", "''");
			buf.append("and p.codMonoProv = '" + codMonoProv + "' ");
		}

		if ((flgSospNazionale != null) && (!flgSospNazionale.equals(""))) {
			buf.append("and p.flgsospensionemob = '" + flgSospNazionale + "' ");
		}

		if ((flgCapoGruppo != null) && (!flgCapoGruppo.equals(""))) {
			buf.append("and p.flgcapogruppo = '" + flgCapoGruppo + "' ");
		}

		if ((strCodFiscAzCapogruppo != null) && (!strCodFiscAzCapogruppo.equals(""))) {
			buf.append("and p.strcfazcapogruppo = '" + strCodFiscAzCapogruppo + "' ");
		}

		if ((flgCompetenza != null) && (!flgCompetenza.equals(""))) {
			buf.append("and p.flgcompetenza = '" + flgCompetenza + "' ");
		}

		if ((flgScopertura != null) && (!flgScopertura.equals(""))) {
			flgScopertura = StringUtils.replace(flgScopertura, "'", "''");
			if ((flgConvenzione != null) && (!flgConvenzione.equals(""))) {
				flgConvenzione = StringUtils.replace(flgConvenzione, "'", "''");
				buf.append("and pg_coll_mirato_2.checkscopertura(p.prgprospettoinf,'" + flgConvenzione + "') = "
						+ flgScopertura + " ");
			} else {
				buf.append("and pg_coll_mirato_2.checkscopertura(p.prgprospettoinf,'N') = " + flgScopertura + " ");
			}
		}

		if ((codProvincia != null) && (!codProvincia.equals(""))) {
			codProvincia = StringUtils.replace(codProvincia, "'", "''");
			buf.append("and p.codProvincia = '" + codProvincia + "' ");
		}

		if ((CODSTATOATTO != null) && (!CODSTATOATTO.equals(""))) {
			CODSTATOATTO = StringUtils.replace(CODSTATOATTO, "'", "''");
			buf.append(" and stato.CODSTATOATTO ='" + CODSTATOATTO + "' ");
		}

		if ((codComunicazione != null) && (!codComunicazione.equals(""))) {
			codComunicazione = StringUtils.replace(codComunicazione, "'", "''");
			// buf.append(" and p.codcomunicazione ='" + codComunicazione + "' ");
			buf.append(" and (p.codcomunicazione like '%" + codComunicazione + "' or p.codcomunicazioneorig like '%"
					+ codComunicazione + "' " + " or p.codcomunicazioneann like '%" + codComunicazione + "') ");
		}

		/*
		 * Ordine in base allo stato: in corso d'anno storicizzato (varie) annullato
		 */
		buf.append(" ORDER BY az.strragionesociale, " + "         p.numannorifprospetto DESC, " + "         CASE "
				+ "                  WHEN p.codmonostatoprospetto = 'A' THEN 0 "
				+ "                  WHEN p.codmonostatoprospetto = 'S' THEN 1 "
				+ "                  WHEN p.codmonostatoprospetto = 'V' THEN 2 "
				+ "                  WHEN p.codmonostatoprospetto = 'N' THEN 4 "
				+ "                  WHEN p.codmonostatoprospetto = 'U' THEN 3 " + "         END");
		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}