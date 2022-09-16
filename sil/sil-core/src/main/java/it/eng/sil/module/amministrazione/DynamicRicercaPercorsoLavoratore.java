/*
 * Creato il Dec 21, 2004
 *
 */
package it.eng.sil.module.amministrazione;

import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;

/**
 * @author savino
 *
 */
public class DynamicRicercaPercorsoLavoratore implements IDynamicStatementProvider {
	private static final String STM = " select " + "	CASE  " + "		WHEN lav.STRPOSDATA = 'STATUS'"
			+ "	       THEN to_char(lav.DATDATAINIZIO, 'dd/mm/yyyy') || DECODE(lav.DATDATAFINE,NULL, '','-&gt;'|| to_char(lav.DATDATAFINE, 'dd/mm/yyyy'))"
			+ "		WHEN lav.STRPOSDATA <> 'STATUS'" + "   	    THEN null" + "   END as status,          "
			+ "   CASE   " + "   	WHEN lav.STRPOSDATA = 'SERVIZI'"
			+ "       	THEN to_char(lav.DATDATAINIZIO, 'dd/mm/yyyy') || DECODE(lav.DATDATAFINE,NULL, '','-&gt;'|| to_char(lav.DATDATAFINE, 'dd/mm/yyyy'))"
			+ "		WHEN lav.STRPOSDATA <> 'SERVIZI'" + "       	THEN null" + "   END as servizi	,       "
			+ "   CASE  " + "   	WHEN lav.STRPOSDATA = 'MOVIMENTI'"
			+ "       	THEN to_char(lav.DATDATAINIZIO, 'dd/mm/yyyy') || DECODE(lav.DATDATAFINE,NULL, '','-&gt;'|| to_char(lav.DATDATAFINE, 'dd/mm/yyyy'))"
			+ "		WHEN lav.STRPOSDATA = 'MISSIONI'"
			+ "       	THEN to_char(lav.DATDATAINIZIO, 'dd/mm/yyyy') || DECODE(lav.DATDATAFINE,NULL, '','-&gt;'|| to_char(lav.DATDATAFINE, 'dd/mm/yyyy'))"
			+ "		ELSE null" + "   END as movimenti,        " + "	CASE	"
			+ "   	WHEN lav.STRPOSDATA = 'MISSIONI'"
			+ "       	THEN to_char(lav.DATDATAINIZIO, 'dd/mm/yyyy') || DECODE(lav.DATDATAFINE,NULL, '','-&gt;'|| to_char(lav.DATDATAFINE, 'dd/mm/yyyy'))"
			+ "		WHEN lav.STRPOSDATA <> 'MISSIONI'" + "			THEN null" + "	END as missioni," + "   CASE   "
			+ "   	WHEN lav.codmonotipoinf = 'A'"
			+ "			THEN 'PAGE=PercorsoDispoLavDettaglioInformazioniStorichePage&PRGDICHDISPONIBILITA='|| lav.CHIAVEDETTAGLIO "
			+ "		WHEN lav.codmonotipoinf = 'B'"
			+ "           THEN 'PAGE=PercorsoMovimentiCollegatiPage&PrgMovimentoColl='  || lav.CHIAVEDETTAGLIO "
			+ "	    WHEN lav.codmonotipoinf = 'C'"
			+ "           THEN 'PAGE=PercorsoStatoOccInfoStorDettPage&prgStatoOccupaz=' || lav.CHIAVEDETTAGLIO "
			+ "	   	WHEN lav.codmonotipoinf = 'D'"
			+ "           THEN 'PAGE=PercorsoTrasferimentiStoricoDettaglioPage&PRGLAVSTORIAINF=' || lav.CHIAVEDETTAGLIO "
			+ "	   	WHEN lav.codmonotipoinf = 'E'" + "			THEN " + "				CASE"
			+ "					WHEN lav.prgRichiestaAz=0 " + // collocamento mirato
			"		           		THEN 'PAGE=PercorsoCollMiratoInfStorDettPage&prgCMIscr='      || lav.CHIAVEDETTAGLIO "
			+ "					WHEN lav.prgRichiestaAz=1 " + // mobilita'
			"		           		THEN 'PAGE=PercorsoMobilitaInfoStorDettPage&prgMobilitaIscr=' || lav.CHIAVEDETTAGLIO "
			+ "				END " + "	   	WHEN lav.codmonotipoinf = 'F'"
			+ "           THEN 'PAGE=PercorsoPattoLavDettaglioInformazioniStorichePage&PRGPATTOLAVORATORE=' || lav.CHIAVEDETTAGLIO "
			+ "	   	WHEN lav.codmonotipoinf = 'G'" + "			THEN " + "				CASE"
			+ "					WHEN lav.prgRichiestaAz=0 " + // appuntamenti
			"           			THEN 'PAGE=PercorsoDettaglioAppuntamentoPage&PRGAPPUNTAMENTO=' || lav.CHIAVEDETTAGLIO "
			+ "					WHEN lav.prgRichiestaAz=1 " + // contatti
			"		           		THEN 'PAGE=PercorsoDettaglioContattoPage&PRGCONTATTO=' || lav.CHIAVEDETTAGLIO || '&codCpiContatto=' || lav.codCpi "
			+ "				END " + "	   	WHEN lav.codmonotipoinf = 'H'" +
			// ora si prendono tutti i colloqui, ma continuo a riportare l'eventuale prgPercorsoConcordato
			// perché serve alla jsp di dettaglio
			"           THEN 'PAGE=PercorsoColloquioPage&prgColloquio=' || lav.CHIAVEDETTAGLIO "
			+ "	   	WHEN lav.codmonotipoinf = 'I'" +
			// il prgPattoLavoratore, riportato in prgRichiestaAz, serve per caricare un modulo della pagina dei
			// percorsi
			// NON ELIMINARLO DALLA VIEW
			"           THEN 'PAGE=PercorsoPercorsiConcordatiPage&prgPercorso=' || lav.CHIAVEDETTAGLIO || decode(lav.prgRichiestaAz,null,'','&prgPattoLavoratore=') || lav.prgRichiestaAz"
			+ "	   	WHEN lav.codmonotipoinf = 'L'"
			+ "           THEN 'PAGE=IdoDettaglioSinteticoPage&POPUP=1&PRGRICHIESTAAZ=' || lav.PRGRICHIESTAAZ "
			+ "		WHEN lav.codmonotipoinf = 'M'"
			+ "           THEN 'PAGE=DichRedDettaglioPage&POPUP=1&PRGDICHLAV=' || lav.CHIAVEDETTAGLIO "
			+ "		WHEN lav.codmonotipoinf = 'N'"
			+ "           THEN 'PAGE=IdoDettaglioSinteticoPage&POPUP=1&PRGRICHIESTAAZ=' || lav.PRGRICHIESTAAZ "
			+ "		WHEN lav.codmonotipoinf = 'O'"
			+ "           THEN 'PAGE=PercorsoMovimentiCollegatiPage&PrgMovimentoColl='  || lav.CHIAVEDETTAGLIO "
			+ "		WHEN lav.codmonotipoinf = 'P'"
			+ "           THEN 'PAGE=IdoDettaglioSinteticoPage&POPUP=1&PRGRICHIESTAAZ=' || lav.PRGRICHIESTAAZ "
			+ "		WHEN lav.codmonotipoinf = 'Q'"
			+ "           THEN 'PAGE=ConsultaDatiMissionePage&POPUP=1&PRGMISSIONE=' || lav.CHIAVEDETTAGLIO "
			+ "   END as URL,              " + "   lav.CDNLAVORATORE,       " + "   lav.DESCRIZIONEPERCORSO, "
			+ "   lav.CHIAVEDETTAGLIO,     " + "   lav.CODMONOTIPOINF,       " + "	de_cpi.strDescrizione as CpiRif "
			+ " from vw_percorso_lav lav, de_cpi " + " where lav.codCpi = de_cpi.codCpi (+)";

	public String getStatement(RequestContainer requestContainer, SourceBean config) {
		SourceBean request = requestContainer.getServiceRequest();
		String cdnLavoratore = (String) request.getAttribute("cdnlavoratore");
		Object tipoInfo = request.getAttribute("TIPO_INFO");
		String dataInizio = (String) request.getAttribute("datainizio");
		String dataFine = (String) request.getAttribute("datafine");

		SessionContainer session = requestContainer.getSessionContainer();
		String encryptKey = (String) session.getAttribute("_ENCRYPTER_KEY_");

		StringBuffer stm = new StringBuffer(STM);
		if (tipoInfo != null) {
			Vector infoDaVisualizzare = null;
			infoDaVisualizzare = request.getAttributeAsVector("TIPO_INFO");
			stm.append(" and lav.codmonotipoinf in (");
			for (int i = 0; i < infoDaVisualizzare.size(); i++) {
				stm.append("'");
				stm.append(infoDaVisualizzare.get(i));
				stm.append("'");
				if (i < infoDaVisualizzare.size() - 1)
					stm.append(", ");
			}
			stm.append(") ");
		}

		// aggiunto rif. al cdnlavoratore cryptato
		stm.append(" and (lav.cdnlavoratore = '");
		stm.append(cdnLavoratore);
		stm.append("'");
		stm.append(" OR lav.CDNLAVORATORE_CRYPT = encrypt('");
		stm.append(cdnLavoratore);
		stm.append("', '");
		stm.append(encryptKey);
		stm.append("') )");

		if (dataInizio != null && !dataInizio.equals("")) {
			stm.append(" and datdatainizio >= to_date('");
			stm.append(dataInizio);
			stm.append("','dd/mm/yyyy') ");
		}
		if (dataFine != null && !dataFine.equals("")) {
			stm.append(" and datdatainizio <= to_date('");
			stm.append(dataFine);
			stm.append("','dd/mm/yyyy') ");
		}
		stm.append(" order by DATDATAINIZIO desc, dataSort2 desc");
		return stm.toString();
	}
}
