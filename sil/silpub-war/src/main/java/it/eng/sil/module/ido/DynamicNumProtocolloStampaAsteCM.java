/*
 * Creato il 15-giu-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.ido;

/**
 * @author melandri
 *
 * Per modificare il modello associato al commento di questo tipo generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */


import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.StringUtils;
import java.util.StringTokenizer;
/**
 * @author Gritti 
 * 
 * Questa query vieni reiterata più volte a seconda del CODCPI e DATACHIAMATA delle richieste da stampare
 * il risultato che si ottiene è la lista di tutte le richieste da protocollare con relativo NUM PROTOCOLLO
 */
public class DynamicNumProtocolloStampaAsteCM implements IDynamicStatementProvider {
			

		public String getStatement(RequestContainer requestContainer, SourceBean config) {
			SourceBean req = requestContainer.getServiceRequest();

			String codCpi 		= StringUtils.getAttributeStrNotNull(req, "CODCPI");
			String datChiam 	= StringUtils.getAttributeStrNotNull(req, "DATCHIAMATACM");
			// savino 24/01/2007 numProt non viene usato
			//String numProt		= StringUtils.getAttributeStrNotNull(req, "NUMPROT");
			String moreToken	= "";
			String codCpiSt		= "";
			String datChiamSt	= "";
			StringBuffer query_totale = null;
			String SELECT_SQL_BASE = "";
	
	
			SELECT_SQL_BASE =  
				" SELECT tab2.prgrichiestaaz, tab2.numanno, tab2.prgazienda, "
				+"		 tab2.numrichiesta, tab2.NUMPROTOCOLLO, to_char(tab3.maxdata,'dd/mm/yyyy hh24:mi') as maxdata from " 
				+"(SELECT   rich.prgrichiestaaz, rich.numanno, rich.prgazienda, "
				+"		 nvl(rich.numrichiestaorig, rich.numrichiesta) numrichiesta, AM_DOCUMENTO.NUMPROTOCOLLO, am_documento.datprotocollo "
				+"	FROM do_richiesta_az rich INNER JOIN do_evasione ON (rich.prgrichiestaaz = "
				+"															do_evasione.prgrichiestaaz "
				+"														) "
				+"		 INNER JOIN de_cpi ON (rich.codcpi = de_cpi.codcpi) "
				+"		 INNER JOIN AS_REL_RICH_DOC ON (rich.PRGRICHIESTAAZ = AS_REL_RICH_DOC.PRGRICHIESTAAZ) "
				+"		 INNER JOIN AM_DOCUMENTO ON (AS_REL_RICH_DOC.PRGDOCUMENTO = AM_DOCUMENTO.PRGDOCUMENTO) "
				// blocco modificato da savino 24/01/2007. Quello originario e' ancora presente nella query formattata commentata piu' su.
				+"		 inner join ( "
				+"		  SELECT   count(1)numrichxprot, AS_REL_RICH_DOC.PRGDOCUMENTO "
				+"		  FROM AS_REL_RICH_DOC " 
				+"		       INNER JOIN AM_DOCUMENTO ON (AS_REL_RICH_DOC.PRGDOCUMENTO = AM_DOCUMENTO.PRGDOCUMENTO) "
				+"		  WHERE "
				+"   AM_DOCUMENTO.CODSTATOATTO = 'PR' "
				+"	 AND AM_DOCUMENTO.NUMPROTOCOLLO IS NOT NULL "
				+"	 AND AM_DOCUMENTO.STRNOMEDOC LIKE 'AvvisoPubblicoWeb.%' "
				+"	 AND AM_DOCUMENTO.CODCPI = '" + codCpi + "'"
				+"	 AND AM_DOCUMENTO.DATACQRIL = TO_DATE ('"+ datChiam +"', 'dd/mm/yyyy') "
				+"	 group by AS_REL_RICH_DOC.PRGDOCUMENTO) contarichxdoc "
				+"	on  AS_REL_RICH_DOC.PRGDOCUMENTO=contarichxdoc.PRGDOCUMENTO "
				// fine blocco modificato
				+"	inner join ( "
				+"		 SELECT   count(1) numrichieste "
				+"		 FROM do_richiesta_az rich INNER JOIN do_evasione ON (rich.prgrichiestaaz = do_evasione.prgrichiestaaz) "
				+"		 INNER JOIN de_cpi ON (rich.codcpi = de_cpi.codcpi) "
				+"		 WHERE (rich.numstorico = 0 AND rich.flgpubblicata = 'S') "
				+"		 AND (rich.datpubblicazione <= SYSDATE  "
				+"		 AND (SYSDATE <= rich.datscadenzapubblicazione OR rich.datscadenzapubblicazione IS NULL)) "
				+"	 AND (do_evasione.flgpubbweb = 'S') "
				+"	 AND (do_evasione.cdnstatorich <> 5) "
				+"	 AND (do_evasione.codevasione = 'CMA') "
				+"	 AND (rich.codcpi = de_cpi.codcpi) "
				+"	 AND (rich.DATCHIAMATACM = TO_DATE ('"+ datChiam +"', 'dd/mm/yyyy')) "
				+"	 AND (rich.codcpi = '"+ codCpi +"'))contarichpubb "
				+"	on 	 contarichxdoc.numrichxprot=contarichpubb.numrichieste "
				+"	 WHERE (rich.numstorico = 0 AND rich.flgpubblicata = 'S') "
				+"	 AND (    rich.datpubblicazione <= SYSDATE "
				+"		  AND ( SYSDATE <= rich.datscadenzapubblicazione OR rich.datscadenzapubblicazione IS NULL) "
				+"		 ) "
				+"	 AND (do_evasione.flgpubbweb = 'S') "
				+"	 AND (do_evasione.cdnstatorich <> 5) "
				+"	 AND (do_evasione.codevasione = 'CMA')"
				+"	 AND (rich.codcpi = de_cpi.codcpi) "
				+"	 AND (rich.DATCHIAMATACM = TO_DATE ('"+ datChiam +"', 'dd/mm/yyyy')) "
				+"	 AND (rich.codcpi = '"+ codCpi +"') "
				+"	 AND AM_DOCUMENTO.CODSTATOATTO = 'PR' "
				+"	 AND AM_DOCUMENTO.NUMPROTOCOLLO IS NOT NULL "
				+"	 AND AM_DOCUMENTO.STRNOMEDOC LIKE 'AvvisoPubblicoWeb.%' "
				+"	 AND AM_DOCUMENTO.CODCPI = '" + codCpi + "'"
				+"	 AND AM_DOCUMENTO.DATACQRIL = TO_DATE ('"+ datChiam +"', 'dd/mm/yyyy'))tab2 "
				+"	 inner join (  "
				+"select max(tab1.datprotocollo)maxdata from  "
				+"(  "  
				+"SELECT   rich.prgrichiestaaz, rich.numanno, rich.prgazienda, "
				+"		 nvl(rich.numrichiestaorig, rich.numrichiesta) numrichiesta, AM_DOCUMENTO.NUMPROTOCOLLO, am_documento.datprotocollo "
				+"	FROM do_richiesta_az rich INNER JOIN do_evasione ON (rich.prgrichiestaaz = "
				+"															do_evasione.prgrichiestaaz "
				+"														) "
				+"		 INNER JOIN de_cpi ON (rich.codcpi = de_cpi.codcpi) "
				+"		 INNER JOIN AS_REL_RICH_DOC ON (rich.PRGRICHIESTAAZ = AS_REL_RICH_DOC.PRGRICHIESTAAZ) "
				+"		 INNER JOIN AM_DOCUMENTO ON (AS_REL_RICH_DOC.PRGDOCUMENTO = AM_DOCUMENTO.PRGDOCUMENTO) "
				+"		 inner join ( "
				+"		  SELECT   count(1)numrichxprot, AS_REL_RICH_DOC.PRGDOCUMENTO "
				+"		  FROM do_richiesta_az rich INNER JOIN do_evasione ON (rich.prgrichiestaaz = do_evasione.prgrichiestaaz) "
				+"		 INNER JOIN de_cpi ON (rich.codcpi = de_cpi.codcpi) "
				+"		 INNER JOIN AS_REL_RICH_DOC ON (rich.PRGRICHIESTAAZ = AS_REL_RICH_DOC.PRGRICHIESTAAZ) "
				+"		 INNER JOIN AM_DOCUMENTO ON (AS_REL_RICH_DOC.PRGDOCUMENTO = AM_DOCUMENTO.PRGDOCUMENTO) "
				+"		  WHERE (rich.numstorico = 0 AND rich.flgpubblicata = 'S') "
				+"	 AND (    rich.datpubblicazione <= SYSDATE "
				+"		  AND (SYSDATE <= rich.datscadenzapubblicazione OR rich.datscadenzapubblicazione IS NULL) "
				+"		 ) "
				+"	 AND (do_evasione.flgpubbweb = 'S') "
				+"	 AND (do_evasione.cdnstatorich <> 5) "
				+"	 AND (do_evasione.codevasione = 'CMA') "
				+"	 AND (rich.codcpi = de_cpi.codcpi) "
				+"	 AND (rich.DATCHIAMATACM = TO_DATE ('"+ datChiam +"', 'dd/mm/yyyy')) "
				+"	 AND (rich.codcpi = '"+ codCpi +"') "
				+"	 AND AM_DOCUMENTO.CODSTATOATTO = 'PR' "
				+"	 AND AM_DOCUMENTO.NUMPROTOCOLLO IS NOT NULL "
				+"	 AND AM_DOCUMENTO.STRNOMEDOC LIKE 'AvvisoPubblicoWeb.%' "
				+"	 AND AM_DOCUMENTO.CODCPI = '" + codCpi + "'"
				+"	 AND AM_DOCUMENTO.DATACQRIL = TO_DATE ('"+ datChiam +"', 'dd/mm/yyyy') "
				+"	 group by AS_REL_RICH_DOC.PRGDOCUMENTO) contarichxdoc "
				+"	on  AS_REL_RICH_DOC.PRGDOCUMENTO=contarichxdoc.PRGDOCUMENTO "
				+"	inner join ( "
				+"		 SELECT   count(1) numrichieste "
				+"		 FROM do_richiesta_az rich INNER JOIN do_evasione ON (rich.prgrichiestaaz = do_evasione.prgrichiestaaz) "
				+"		 INNER JOIN de_cpi ON (rich.codcpi = de_cpi.codcpi) "
				+"		 WHERE (rich.numstorico = 0 AND rich.flgpubblicata = 'S') "
				+"		 AND (rich.datpubblicazione <= SYSDATE  "
				+"		 AND (SYSDATE <= rich.datscadenzapubblicazione OR rich.datscadenzapubblicazione IS NULL)) "
				+"	 AND (do_evasione.flgpubbweb = 'S') "
				+"	 AND (do_evasione.cdnstatorich <> 5) "
				+"	 AND (do_evasione.codevasione = 'CMA') "
				+"	 AND (rich.codcpi = de_cpi.codcpi) "
				+"	 AND (rich.DATCHIAMATACM = TO_DATE ('"+ datChiam +"', 'dd/mm/yyyy')) "
				+"	 AND (rich.codcpi = '"+ codCpi +"'))contarichpubb "
				+"	on 	 contarichxdoc.numrichxprot=contarichpubb.numrichieste "
				+"	 WHERE (rich.numstorico = 0 AND rich.flgpubblicata = 'S') "
				+"	 AND (    rich.datpubblicazione <= SYSDATE "
				+"		  AND (SYSDATE <= rich.datscadenzapubblicazione OR rich.datscadenzapubblicazione IS NULL) "
				+"		 ) "
				+"	 AND (do_evasione.flgpubbweb = 'S') "
				+"	 AND (do_evasione.cdnstatorich <> 5) "
				+"	 AND (do_evasione.codevasione = 'CMA') "
				+"	 AND (rich.codcpi = de_cpi.codcpi) "
				+"	 AND (rich.DATCHIAMATACM = TO_DATE ('"+ datChiam +"', 'dd/mm/yyyy')) "
				+"	 AND (rich.codcpi = '"+ codCpi +"') "
				+"	 AND AM_DOCUMENTO.CODSTATOATTO = 'PR' "
				+"	 AND AM_DOCUMENTO.NUMPROTOCOLLO IS NOT NULL "
				+"	 AND AM_DOCUMENTO.STRNOMEDOC LIKE 'AvvisoPubblicoWeb.%' "
				+"	 AND AM_DOCUMENTO.CODCPI = '" + codCpi + "'"
				+"	 AND AM_DOCUMENTO.DATACQRIL = TO_DATE ('"+ datChiam +"', 'dd/mm/yyyy') "
				+"	 )tab1)tab3 "
				+"	 on tab2.datprotocollo=tab3.maxdata	 ";
			
			query_totale = new StringBuffer(SELECT_SQL_BASE);						
			return query_totale.toString();
	}
	/**
	 * metodo di test 
	 */
	public static void main(String []a) throws Exception {
		DynamicNumProtocolloStampaAste test = new DynamicNumProtocolloStampaAste();
		SourceBean req = new SourceBean("SERVICE_REQUEST");
		req.setAttribute("CODCPI", "085603000");
		req.setAttribute("DATCHIAMATACM", "28/02/2007");
		RequestContainer rc = new RequestContainer();
		RequestContainer.setRequestContainer(rc);
		rc.setServiceRequest(req);
		
		System.out.println(test.getStatement(rc, null));
	}
}
