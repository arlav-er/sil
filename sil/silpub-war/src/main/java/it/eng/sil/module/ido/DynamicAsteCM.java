/*
 * Creato il 8-mag-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.ido;

/**
 * @author melandri
 * 
 * Per modificare il modello associato al commento di questo tipo generato,
 * aprire Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e
 * commenti
 */

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

public class DynamicAsteCM implements IDynamicStatementProvider {

	public DynamicAsteCM() {
	}

	private static final String SELECT_SQL_BASE = "SELECT (nvl(rich.numrichiestaorig, rich.numrichiesta) || '/' || RICH.NUMANNO) COD, "
			+ "	(TO_CHAR(rich.DATCHIAMATACM,'dd/mm/yyyy') || ' - ' || TO_CHAR(rich.DATSCADENZA,'dd/mm/yyyy')) dataRichiesta, "
			+ "	TO_CHAR(rich.DATPUBBLICAZIONE,'dd/mm/yyyy') datPubblicaz, " + "	rich.strdatiaziendapubb ente, "
			+ "	rich.strluogolavoro luogolavoro, " + "	to_char(rich.NUMPROFRICHIESTI) nPostiCM, "
			+ "	rich.strmansionepubb mansione, " + "	rich.txtcaratteristfigprof requisiti, "
			+ "	(rich.txtcondcontrattuale || rich.strnoteorariopubb) rapportoLav, "
			+ "	PG_IDO.STR_EVASIONE(rich.codmonotipograd) evasione, " + "	DE_CPI.CODCPI, "
			+ "	to_char(RICH.DATCHIAMATACM,'dd/mm/yyyy') DATCHIAMATA,"
			+ "	to_char(RICH.DATPUBBLICAZIONE,'dd/mm/yyyy') DATPUBBLICAZIONE," + "	RICH.NUMANNO"
			+ " from do_richiesta_az rich "
			+ " inner join do_evasione on (rich.PRGRICHIESTAAZ=do_evasione.PRGRICHIESTAAZ) "
			+ "	inner join de_cpi on (rich.CODCPI=de_cpi.CODCPI) "
			+ "	inner join de_provincia on (DE_CPI.CODPROVINCIA=de_provincia.CODPROVINCIA) "
			+ "	inner join st_modello_stampa on (rich.CODCPI=st_modello_stampa.CODOGGETTOMODELLO) ";

	private static final String sWhere = " WHERE (rich.NUMSTORICO=0 and rich.FLGPUBBLICATA='S') "
			+ " and (rich.DATPUBBLICAZIONE <= to_date(to_char(SYSDATE, 'DD-MON-YYYY')) and (to_date(to_char(SYSDATE, 'DD-MON-YYYY')) <= rich.DATSCADENZAPUBBLICAZIONE OR rich.DATSCADENZAPUBBLICAZIONE IS NULL)) "
			+ "	and (do_evasione.FLGPUBBWEB = 'S') " + "	and (do_evasione.CDNSTATORICH <> 5) "
			+ "	and (do_evasione.CODEVASIONE='CMA') " + " and (st_modello_stampa.prgtipomodello = 53)"
			+ "	and (rich.CODCPI = de_cpi.CODCPI) ";

	public String getStatement(RequestContainer reqCont, SourceBean config) {

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		buf.append(" ORDER BY RICH.CODCPI ASC, RICH.DATCHIAMATACM ASC, RICH.DATPUBBLICAZIONE ASC , nvl(RICH.NUMRICHIESTAORIG, RICH.NUMRICHIESTA) ASC ,RICH.NUMANNO ASC ");
		// buf.append(" ORDER BY RICH.DATCHIAMATACM ASC, RICH.CODCPI ASC,
		// RICH.DATPUBBLICAZIONE ASC , RICH.NUMRICHIESTA ASC ,RICH.NUMANNO ASC
		// ");
		query_totale.append(sWhere + buf.toString());

		return query_totale.toString();
	}

}
