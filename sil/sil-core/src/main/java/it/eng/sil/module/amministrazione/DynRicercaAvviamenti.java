/*
 * Creato il 13-giu-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.amministrazione;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

/**
 * @author gritti
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class DynRicercaAvviamenti implements IDynamicStatementProvider {
	public DynRicercaAvviamenti() {
	}

	private static final String SELECT_SQL_BASE = " SELECT "
			+ " (AN_LAVORATORE.STRCODICEFISCALE || '\n' || AN_LAVORATORE.STRCOGNOME || '\n' || AN_LAVORATORE.STRNOME) AS datiLav , "
			+ " AN_LAVORATORE.CDNLAVORATORE, " + "	AN_LAVORATORE.STRCODICEFISCALE, " + "	AN_LAVORATORE.STRCOGNOME , "
			+ "	AN_LAVORATORE.STRNOME, " + "	AN_AZIENDA.STRRAGIONESOCIALE AS ente, "
			+ "	AN_UNITA_AZIENDA.STRINDIRIZZO AS ind_Ente, " + "	AN_AZIENDA.STRCODICEFISCALE AS cf_Ente, "
			+ "	(DO_RICHIESTA_AZ.NUMRICHIESTA || '/\n' || DO_RICHIESTA_AZ.NUMANNO) AS rif, "
			+ "	DO_RICHIESTA_AZ.PRGRICHIESTAAZ, " + "	DO_RICHIESTA_AZ.NUMRICHIESTA AS numRich, "
			+ "	DO_RICHIESTA_AZ.NUMANNO AS numAnno, " + "	DO_RICHIESTA_AZ.PRGAZIENDA , "
			+ "	TO_CHAR(AS_AVV_SELEZIONE.DATAVVIAMENTO,'DD/MM/YY') AS DATAINIZIO, "
			+ "	AS_AVV_SELEZIONE.PRGAVVSELEZIONE, " + "	DE_ESITO_AVV_SEL.CODESITOAVVSEL, "
			+ "	DE_ESITO_AVV_SEL.STRDESCRIZIONE AS STATOAVV, " + "	AM_STATO_OCCUPAZ.CODSTATOOCCUPAZ, "
			+ "	(de_stato_occupaz_ragg.strdescrizione || ': ' || de_stato_occupaz.strdescrizione)  AS STATOOCCUPAZ, "
			+ "	DECODE (an_lav_storia_inf.codmonotipocpi, " + "            'C', cpi.strdescrizione, "
			+ "            'E', cpi.strdescrizione, " + "            'T', cpio.strdescrizione "
			+ "           ) AS cpi, " + "(case  " + "	   when AM_DICH_DISPONIBILITA.PRGDICHDISPONIBILITA IS NULL "
			+ "	   then 'N' " + "	   else 'S' " + "	   end) AS DID, " + "(case "
			+ "	   when AM_MOBILITA_ISCR.CDNLAVORATORE IS NULL " + "	   then 'N' " + "	   else 'S' "
			+ "	   end) AS MOB, " + "(case  " + "	  when AN_EVIDENZA.CDNLAVORATORE is null " + "	  then 'N' "
			+ "	  else 'S' " + "	  end) AS EV " + "	FROM AS_AVV_SELEZIONE  "
			+ "	INNER JOIN AN_LAVORATORE ON (AS_AVV_SELEZIONE.CDNLAVORATORE = AN_LAVORATORE.CDNLAVORATORE) "
			+ "	INNER JOIN DO_RICHIESTA_AZ on (AS_AVV_SELEZIONE.PRGRICHIESTAAZ=DO_RICHIESTA_AZ.PRGRICHIESTAAZ) "
			+ " LEFT OUTER JOIN DE_ESITO_AVV_SEL on (AS_AVV_SELEZIONE.CODESITOAVVSEL = DE_ESITO_AVV_SEL.CODESITOAVVSEL) "
			+ " INNER JOIN AM_STATO_OCCUPAZ on (AS_AVV_SELEZIONE.CDNLAVORATORE = AM_STATO_OCCUPAZ.CDNLAVORATORE) "
			+ "	INNER JOIN DE_STATO_OCCUPAZ on (AM_STATO_OCCUPAZ.CODSTATOOCCUPAZ = DE_STATO_OCCUPAZ.CODSTATOOCCUPAZ) "
			+ " INNER JOIN de_stato_occupaz_ragg ON (de_stato_occupaz.CODSTATOOCCUPAZRAGG = de_stato_occupaz_ragg.CODSTATOOCCUPAZRAGG) "
			+ "	INNER JOIN AN_AZIENDA on (DO_RICHIESTA_AZ.PRGAZIENDA = AN_AZIENDA.PRGAZIENDA) "
			+ "	INNER JOIN AN_UNITA_AZIENDA on (DO_RICHIESTA_AZ.PRGAZIENDA = AN_UNITA_AZIENDA.PRGAZIENDA "
			+ " AND do_richiesta_az.PRGUNITA = an_unita_azienda.prgunita)"
			// + " INNER JOIN AM_DOCUMENTO on (AS_AVV_SELEZIONE.CDNLAVORATORE =
			// AM_DOCUMENTO.CDNLAVORATORE) "
			+ "	INNER JOIN an_lav_storia_inf ON (as_avv_selezione.cdnlavoratore = an_lav_storia_inf.CDNLAVORATORE "
			+ " AND an_lav_storia_inf.datfine IS NULL) "
			+ " LEFT JOIN de_cpi cpi ON (an_lav_storia_inf.codcpitit = cpi.codcpi) "
			+ " LEFT JOIN de_cpi cpio ON (an_lav_storia_inf.codcpiorig = cpio.codcpi) "
			+ " LEFT JOIN AM_ELENCO_ANAGRAFICO on (AN_LAVORATORE.CDNLAVORATORE = AM_ELENCO_ANAGRAFICO.CDNLAVORATORE) "
			+ " LEFT OUTER JOIN AM_DICH_DISPONIBILITA on (AM_ELENCO_ANAGRAFICO.PRGELENCOANAGRAFICO = AM_DICH_DISPONIBILITA.PRGELENCOANAGRAFICO "
			+ "  AND AM_DICH_DISPONIBILITA.DATDICHIARAZIONE <= SYSDATE "
			+ "  AND nvl(am_dich_disponibilita.DATFINE,TO_DATE('01/01/2100','DD/MM/YYYY')) >= sysdate "
			+ "  AND AM_DICH_DISPONIBILITA.CODSTATOATTO = 'PR' )"
			+ " LEFT OUTER JOIN AM_MOBILITA_ISCR on (AS_AVV_SELEZIONE.CDNLAVORATORE = AM_MOBILITA_ISCR.CDNLAVORATORE "
			+ "  AND AM_MOBILITA_ISCR.DATINIZIO <= SYSDATE " + "  AND AM_MOBILITA_ISCR.DATFINE >= SYSDATE )"
			+ " LEFT OUTER JOIN AN_EVIDENZA on (AS_AVV_SELEZIONE.CDNLAVORATORE = AN_EVIDENZA.CDNLAVORATORE "
			+ "  AND AN_EVIDENZA.DATDATASCAD >= SYSDATE)";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String cdnLavoratore = (String) req.getAttribute("cdnLavoratore");
		String prgAzienda = (String) req.getAttribute("prgAzienda");
		String prgUnita = (String) req.getAttribute("prgUnita");
		String numRich = (String) req.getAttribute("prgRichiestaAz");
		String numAnno = (String) req.getAttribute("anno");
		String datAvvDa = (String) req.getAttribute("datAvvDal");
		String datAvvA = (String) req.getAttribute("datAvvAl");
		String statoAvv = (String) req.getAttribute("statoAvv");
		String flgAvvNoStato = (String) req.getAttribute("flgAvvNoStato");
		String cpi = (String) req.getAttribute("sel_cpi");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if ((cpi != null) && (!cpi.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			cpi = StringUtils.replace(cpi, "'", "''");
			// modifica savino 24/01/2007: il filtro si riferisca al cpi della
			// richesta e non del lavoratore
			buf.append(" (do_richiesta_az.codcpi) = '" + cpi.toUpperCase() + "'");
		}

		if ((cdnLavoratore != null) && (!cdnLavoratore.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			cdnLavoratore = StringUtils.replace(cdnLavoratore, "'", "''");
			buf.append(" (as_avv_selezione.cdnlavoratore) = '" + cdnLavoratore.toUpperCase() + "'");
		}

		if ((numRich != null) && (!numRich.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			numRich = StringUtils.replace(numRich, "'", "''");
			buf.append(" (do_richiesta_az.numrichiesta) = '" + numRich.toUpperCase() + "'");
		}

		if ((prgAzienda != null) && (!prgAzienda.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			prgAzienda = StringUtils.replace(prgAzienda, "'", "''");
			buf.append(" (do_richiesta_az.prgazienda) = '" + prgAzienda.toUpperCase() + "'");
		}

		if ((prgUnita != null) && (!prgUnita.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			prgUnita = StringUtils.replace(prgUnita, "'", "''");
			buf.append(" (do_richiesta_az.prgUnita) = '" + prgUnita.toUpperCase() + "'");
		}

		if ((numAnno != null) && (!numAnno.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" (do_richiesta_az.numanno) = '" + numAnno.toUpperCase() + "'");
		}

		if ((datAvvDa != null) && (!datAvvDa.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(
					" ((as_avv_selezione.DATAVVIAMENTO) >= TO_DATE('" + datAvvDa.toUpperCase() + "','DD/MM/YYYY')) ");

		}

		if ((datAvvA != null) && (!datAvvA.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append("((as_avv_selezione.DATAVVIAMENTO) <= TO_DATE('" + datAvvA.toUpperCase() + "','DD/MM/YYYY')) ");
		}

		if ((statoAvv != null) && (!statoAvv.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" (as_avv_selezione.codEsitoAvvSel) = '" + statoAvv.toUpperCase() + "'");
		}

		if ((flgAvvNoStato != null) && (!flgAvvNoStato.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" (as_avv_selezione.codesitoavvsel) is null ");

		}

		if (buf.length() == 0) {
			buf.append("WHERE");
		} else {
			buf.append(" AND");
		}

		buf.append(" NVL(am_stato_occupaz.DATFINE,TO_DATE('2100/01/01','yyyy/mm/dd')) >= sysdate "
				+ " AND NVL(an_lav_storia_inf.DATFINE,TO_DATE('2100/01/01','yyyy/mm/dd')) >= sysdate");

		buf.append(" GROUP BY an_lavoratore.cdnlavoratore," + " an_lavoratore.strcodicefiscale, "
				+ " an_lavoratore.strcognome, " + "	an_lavoratore.strnome," + " an_azienda.strragionesociale, "
				+ " an_unita_azienda.strindirizzo, " + " an_azienda.strcodicefiscale, "
				+ "	do_richiesta_az.prgrichiestaaz, " + " do_richiesta_az.numanno, " + " do_richiesta_az.numrichiesta, "
				+ " do_richiesta_az.prgazienda," + "	as_avv_selezione.datavviamento, "
				+ "	as_avv_selezione.prgavvselezione, " + " de_esito_avv_sel.codesitoavvsel, "
				+ " de_esito_avv_sel.strdescrizione, " + " am_stato_occupaz.codstatooccupaz, "
				+ " de_stato_occupaz_ragg.strdescrizione, " + " de_stato_occupaz.strdescrizione, "
				+ "	cpi.strdescrizione, " + " cpio.strdescrizione, " + " an_lav_storia_inf.codmonotipocpi, "
				+ "	am_dich_disponibilita.prgdichdisponibilita, " + "	am_mobilita_iscr.cdnlavoratore, "
				+ "	an_evidenza.cdnlavoratore "
				+ " ORDER BY CPI, DATAINIZIO DESC, AN_LAVORATORE.STRCOGNOME, AN_LAVORATORE.STRNOME ");
		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}