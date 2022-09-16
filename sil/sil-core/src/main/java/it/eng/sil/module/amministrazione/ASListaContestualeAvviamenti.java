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
public class ASListaContestualeAvviamenti implements IDynamicStatementProvider {
	public ASListaContestualeAvviamenti() {
	}

	private static final String SELECT_SQL_BASE = " SELECT " + " AN_LAVORATORE.CDNLAVORATORE, "
			+ "	AN_LAVORATORE.STRCODICEFISCALE, " + "	AN_LAVORATORE.STRCOGNOME , " + "	AN_LAVORATORE.STRNOME, "
			+ "	AN_AZIENDA.STRRAGIONESOCIALE AS ente, " + "	AN_UNITA_AZIENDA.STRINDIRIZZO AS ind_Ente, "
			+ "	AN_AZIENDA.STRCODICEFISCALE AS cf_Ente, "
			+ "	(DO_RICHIESTA_AZ.NUMRICHIESTA || '/\n' || DO_RICHIESTA_AZ.NUMANNO) AS rif, "
			+ "	DO_RICHIESTA_AZ.PRGRICHIESTAAZ, " + "	DO_RICHIESTA_AZ.NUMRICHIESTA AS numRich, "
			+ "	DO_RICHIESTA_AZ.NUMANNO AS numAnno, " + "	DO_RICHIESTA_AZ.PRGAZIENDA , "
			+ "	TO_CHAR(AS_AVV_SELEZIONE.DATAVVIAMENTO,'DD/MM/YYYY') AS DATAINIZIO, "
			+ "	AS_AVV_SELEZIONE.PRGAVVSELEZIONE, " + "	DE_ESITO_AVV_SEL.CODESITOAVVSEL, "
			+ "	DE_ESITO_AVV_SEL.STRDESCRIZIONE AS STATOAVV " + "	FROM AS_AVV_SELEZIONE  "
			+ "	INNER JOIN AN_LAVORATORE ON (AS_AVV_SELEZIONE.CDNLAVORATORE = AN_LAVORATORE.CDNLAVORATORE) "
			+ "	INNER JOIN DO_RICHIESTA_AZ on (AS_AVV_SELEZIONE.PRGRICHIESTAAZ=DO_RICHIESTA_AZ.PRGRICHIESTAAZ) "
			+ " LEFT OUTER JOIN DE_ESITO_AVV_SEL on (AS_AVV_SELEZIONE.CODESITOAVVSEL = DE_ESITO_AVV_SEL.CODESITOAVVSEL) "
			+ "	LEFT OUTER JOIN AN_AZIENDA on (DO_RICHIESTA_AZ.PRGAZIENDA = AN_AZIENDA.PRGAZIENDA) "
			+ "	LEFT OUTER JOIN AN_UNITA_AZIENDA on (AN_AZIENDA.PRGAZIENDA = AN_UNITA_AZIENDA.PRGAZIENDA and DO_RICHIESTA_AZ.PRGUNITA = AN_UNITA_AZIENDA.PRGUNITA) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String cdnLavoratore = (String) req.getAttribute("cdnLavoratore");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		if ((cdnLavoratore != null) && (!cdnLavoratore.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			cdnLavoratore = StringUtils.replace(cdnLavoratore, "'", "''");
			buf.append(" (as_avv_selezione.cdnlavoratore) = '" + cdnLavoratore.toUpperCase() + "'");
		}

		buf.append(" GROUP BY an_lavoratore.cdnlavoratore,an_lavoratore.strcodicefiscale, an_lavoratore.strcognome, "
				+ "	an_lavoratore.strnome, an_azienda.strragionesociale, an_unita_azienda.strindirizzo, an_azienda.strcodicefiscale, "
				+ "	do_richiesta_az.prgrichiestaaz, do_richiesta_az.numanno, do_richiesta_az.numrichiesta, "
				+ " do_richiesta_az.prgazienda,as_avv_selezione.datavviamento, "
				+ "	as_avv_selezione.prgavvselezione, de_esito_avv_sel.codesitoavvsel, "
				+ " de_esito_avv_sel.strdescrizione " + " ORDER BY DATAINIZIO DESC ");
		query_totale.append(buf.toString());
		return query_totale.toString();

	}

}