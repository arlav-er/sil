/*
 * Creato il 3-ago-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.ido;

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
public class DynRicercaRichiesteChiamata implements IDynamicStatementProvider {
	public DynRicercaRichiesteChiamata() {
	}

	private static final String SELECT_SQL_BASE = " SELECT " + " do_richiesta_az.prgrichiestaaz,"
			+ " do_incrocio.prgtipoincrocio," + " do_rosa.prgrosa," + " do_richiesta_az.numrichiesta,  "
			+ " do_richiesta_az.numrichiesta ||'/'|| do_richiesta_az.numanno ||'/'|| do_alternativa.prgalternativa  AS richiesta, "
			+ " do_richiesta_az.numanno, " + " do_alternativa.prgalternativa, "
			+ " TO_CHAR(do_richiesta_az.datchiamata,'DD/MM/YYYY') AS dataChiam,"
			// + " (select de_cpi.strdescrizione from de_cpi where de_cpi.codcpi
			// = do_richiesta_az.codcpi) AS cpi,"
			+ " de_cpi.strDescrizione || ' - ' || de_cpi.codcpi as descrizione,"
			+ " de_tipo_incrocio.strdescrizione AS descrInc, " + " de_tipo_rosa.strdescrizione AS descrRosa, "
			+ " do_nominativo.cdnlavoratore, " + " an_lavoratore.strcognome AS cognome, "
			+ " an_lavoratore.strnome AS nome, " + " TO_CHAR(an_lavoratore.datnasc,'DD/MM/YYYY') AS datnasc "
			+ " FROM 	 do_richiesta_az  "
			+ " inner join do_incrocio	on do_incrocio.prgrichiestaaz =do_richiesta_az.prgrichiestaaz   "
			+ " inner join do_rosa on do_rosa.prgincrocio = do_incrocio.prgincrocio "
			+ " inner join do_alternativa on do_alternativa.prgrichiestaaz = do_richiesta_az .prgrichiestaaz and do_alternativa.prgalternativa = do_incrocio.prgalternativa   "
			+ " inner join de_tipo_incrocio on de_tipo_incrocio.prgtipoincrocio = do_incrocio.prgtipoincrocio "
			+ " inner join	de_tipo_rosa on de_tipo_rosa.prgtiporosa = do_rosa.prgtiporosa "
			+ " inner join do_nominativo on do_nominativo.prgrosa = do_rosa.prgrosa "
			+ " inner join an_lavoratore on an_lavoratore.cdnlavoratore = do_nominativo.cdnlavoratore "
			+ " inner join de_cpi on (de_cpi.codcpi = do_richiesta_az.codcpi) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		SourceBean req = requestContainer.getServiceRequest();

		String chiamata = (String) req.getAttribute("chiamata");
		String cpi = (String) req.getAttribute("CodCPI");

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		/*
		 * creo uno StringBuffer dove aggiungo le clausole di WHERE ed AND della QUERY condiziono la query con i
		 * parametri ottenuti dalla request
		 */

		if ((chiamata != null) && (!chiamata.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}
			buf.append(" upper(do_richiesta_az.datchiamata)= TO_DATE('" + chiamata + "','DD/MM/YYYY') ");
		}

		if ((cpi != null) && (!cpi.equals(""))) {
			if (buf.length() == 0) {
				buf.append("WHERE");
			} else {
				buf.append(" AND");
			}

			cpi = StringUtils.replace(cpi, "'", "''");
			buf.append(" upper(do_richiesta_az.codcpi) = '" + cpi.toUpperCase() + "'");
		}

		/*
		 * appendo lo StringBuffer alla QUERY totale nella ServiceResponse sarà ricostruita l'intera QUERY completa
		 * della clausole condizionali
		 */
		buf.append("AND DO_ROSA.PRGROSAFIGLIA IS NULL ORDER BY AN_LAVORATORE.STRCOGNOME,AN_LAVORATORE.STRNOME,"
				+ "AN_LAVORATORE.DATNASC, DO_RICHIESTA_AZ.NUMANNO DESC, DO_RICHIESTA_AZ.NUMRICHIESTA DESC,"
				+ "DO_ALTERNATIVA.PRGALTERNATIVA ASC, DESCRINC ASC");
		query_totale.append(buf.toString());
		return query_totale.toString();

	}
}
