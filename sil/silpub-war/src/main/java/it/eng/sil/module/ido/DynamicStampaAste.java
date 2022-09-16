/*
 * Creato il 25-set-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.ido;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

/**
 * @author Gritti
 * 
 * Seleziona le richieste con modalità di evasione asta da stampare in formato
 * html. I parametri filtro sono : il CODCPI e la DATACHIAMATA
 * 
 */
public class DynamicStampaAste implements IDynamicStatementProvider {

	private static final String SELECT_SQL_BASE = "SELECT  RICH.PRGRICHIESTAAZ," + "		RICH.NUMANNO,"
			+ "		RICH.PRGAZIENDA," + "  RICH.NUMRICHIESTA," + "		PRGUNITA," + " 	RICH.NUMSTORICO, "
			+ "     NVL(RICH.NUMRICHIESTAORIG, RICH.NUMRICHIESTA) NUMRICHIESTAORIG, "
			+ "		to_char(RICH.DATRICHIESTA,'dd/mm/yyyy') DATRICHIESTA," + "		RICH.CODTIPORICHIESTA,"
			+ "		RICH.FLGART16," + "		RICH.STRSESSO," + "		RICH.STRMOTIVSESSO,"
			+ "		to_char(RICH.DATPUBBLICAZIONE,'dd/mm/yyyy') DATPUBBLICAZIONE,"
			+ "		to_char(RICH.DATSCADENZAPUBBLICAZIONE,'dd/mm/yyyy') DATSCADENZAPUBBLICAZIONE,"
			+ "		to_char(RICH.DATSCADENZA,'dd/mm/yyyy') DATSCADENZA," + "		RICH.NUMPROFRICHIESTI,"
			+ "		RICH.STRLOCALITA," + "		RICH.PRGSPI," + "		RICH.STRCOGNOMERIFERIMENTO," + "		RICH.STRNOMERIFERIMENTO,"
			+ "		RICH.STRTELRIFERIMENTO," + "		RICH.STRFAXRIFERIMENTO," + "		RICH.STREMAILRIFERIMENTO,"
			+ "		RICH.FLGAUTOMUNITO," + "		RICH.FLGMILITE," + "		RICH.FLGMOTOMUNITO," + "		RICH.CODTRASFERTA,"
			+ "		RICH.FLGFUORISEDE," + "		RICH.STRMANSIONEPUBB," + "		RICH.TXTFIGURAPROFESSIONALE,"
			+ "		RICH.TXTCARATTERISTFIGPROF," + "		RICH.TXTCONDCONTRATTUALE," + "		RICH.TXTNOTEOPERATORE,"
			+ "		RICH.CODAREA," + "		RICH.CDNUTINS," + "		to_char(RICH.DTMINS,'dd/mm/yyyy') DTMINS,"
			+ "		to_char(RICH.DTMMOD,'dd/mm/yyyy')DTMMOD," + "		RICH.STRCOGNOMERIFPUBB," + "		RICH.STRTELRIFPUBB,"
			+ "		RICH.STRFAXRIFPUBB," + "		RICH.STREMAILRIFPUBB," + "		RICH.STRDATIAZIENDAPUBB,"
			+ "		RICH.STRFORMAZIONEPUBB," + "		RICH.STRCONOSCENZEPUBB," + "		RICH.STRNOTEORARIOPUBB,"
			+ "		RICH.STRRIFCANDIDATURAPUBB," + "		RICH.STRLUOGOLAVORO," + "		RICH.CODQUALIFICA,"
			+ "		RICH.CODMOTGENERE," + "		RICH.CDNGRUPPO," + "		RICH.NUMPOSTOAS," + "		RICH.NUMPOSTOLSU,"
			+ "		RICH.NUMPOSTOMILITARE," + "		RICH.NUMPOSTOMB," + "		RICH.FLGRIUSOGRADUATORIA,"
			+ "		to_char(RICH.DATCHIAMATA,'dd/mm/yyyy') DATCHIAMATA," + "		RICH.STRNOTAAVVISOPUBB,"
			+ "		RICH.STRNOTAGRADDEF," + "		RICH.STRNOTAGRADENTE, " + "		DE_CPI.STRDESCRIZIONE AS STRCPI,"
			+ "		DE_CPI.STRINDIRIZZO," + "		DE_CPI.STRLOCALITA," + "		DE_CPI.STRCAP," + "		DE_CPI.CODCPI,"
			+ "		DE_CPI.STRDESCRIZIONE," + "		DE_CPI.CODCOM," + "		DE_CPI.CODPROVINCIA," + "		DE_CPI.STRTEL,"
			+ "		DE_CPI.STRFAX," + "		DE_CPI.STREMAIL, " + "		DE_PROVINCIA.STRDENOMINAZIONE,"
			+ "		ST_MODELLO_STAMPA.STRNOTE" + " from do_richiesta_az rich "
			+ " inner join do_evasione on (rich.PRGRICHIESTAAZ=do_evasione.PRGRICHIESTAAZ) "
			+ "	inner join de_cpi on (rich.CODCPI=de_cpi.CODCPI) "
			+ "	inner join de_comune on (de_cpi.CODCOM=de_comune.CODCOM) "
			+ "	inner join de_provincia on (de_comune.CODPROVINCIA=de_provincia.CODPROVINCIA) "
			+ "	inner join st_modello_stampa on (rich.CODCPI=st_modello_stampa.CODOGGETTOMODELLO) ";

	private static final String sWhere = " WHERE (rich.NUMSTORICO=0 and rich.FLGPUBBLICATA='S') "
			+ " and (rich.DATPUBBLICAZIONE <= to_date(to_char(SYSDATE, 'DD-MON-YYYY')) and (to_date(to_char(SYSDATE, 'DD-MON-YYYY')) <= rich.DATSCADENZAPUBBLICAZIONE OR rich.DATSCADENZAPUBBLICAZIONE IS NULL)) "
			+ "	and (do_evasione.FLGPUBBWEB = 'S') " + "	and (do_evasione.CDNSTATORICH<>5) "
			+ "	and (do_evasione.CODEVASIONE='AS') " + " and (st_modello_stampa.prgtipomodello = 50)"
			+ "	and (rich.CODCPI=de_cpi.CODCPI) ";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean req = requestContainer.getServiceRequest();

		String codCpi = StringUtils.getAttributeStrNotNull(req, "CODCPI");
		String datChiam = StringUtils.getAttributeStrNotNull(req, "DATCHIAMATA");

		int i = 0;

		StringBuffer query_totale = new StringBuffer(SELECT_SQL_BASE);
		StringBuffer buf = new StringBuffer();

		// DATCHIAMATA -- C'è solo se la richiesta proviene dalla griglia
		// interna o dalla somma della riga
		if (!datChiam.equals("")) {
			buf.append(" AND (RICH.DATCHIAMATA=to_date('" + datChiam + "','dd/mm/yyyy'))");
		}
		// CPI -- C'è solo se la richiesta proviene dalla griglia interna o
		// dalla somma della colonna
		if (!codCpi.equals("")) {
			buf.append(" AND (RICH.CODCPI='" + codCpi + "')");
		}

		buf.append(" ORDER BY RICH.CODCPI ASC, RICH.DATCHIAMATA ASC, RICH.DATPUBBLICAZIONE ASC , NVL(RICH.NUMRICHIESTAORIG, RICH.NUMRICHIESTA) ASC ,RICH.NUMANNO ASC ");
		query_totale.append(sWhere + buf.toString());
		return query_totale.toString();

	}

}
