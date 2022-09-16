package it.eng.sil.module.trento;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.DynamicStatementUtils;
import it.eng.afExt.utils.StringUtils;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import it.eng.sil.security.User;

public class DocumentiStampeLavList implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DocumentiStampeLavList.class.getName());

	private String className = StringUtils.getClassName(DocumentiStampeLavList.class);

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		_logger.debug(className + ".getStatement() INIZIO");

		SourceBean req = requestContainer.getServiceRequest();

		// Campi di Lavoratore
		String cdnLavoratore = (String) req.getAttribute("cdnLavoratore");
		String prgAzienda = (String) req.getAttribute("prgAzienda");
		String codStatoAtto = "";
		// parametro nascondiSepLavoratoreAzienda = true (non riporta il nome dell'azienda)
		boolean nascondiSepLavoratoreAzienda = req.containsAttribute("nascondiSepCampoLav");

		
		int cdnProfilo = 0;
		User user = (User) requestContainer.getSessionContainer().getAttribute(User.USERID);
		cdnProfilo = user.getCdnProfilo();
		
		
		
		// Altri campi dalla QueryString:
		String pagina = (String) req.getAttribute("pagina");
		// se c'è il parametro pagina significa che sto cercando i documenti associati a quella data pagina (PAGE) per lo specifico lavoratore

		// ORA CREO LA QUERY DINAMICAMENTE (usando un "DynamicStatementUtils"):
		DynamicStatementUtils dsu = new DynamicStatementUtils();

		dsu.addSelect(" DOC.PRGDOCUMENTO AS PRGDOCUMENTO," + " DOC.CDNLAVORATORE AS CDNLAVORATORE,"
				+ " DOC.PRGAZIENDA," + " DOC.PRGUNITA");
		
		dsu.addSelect(" LAV.STRCOGNOME||' '||LAV.STRNOME "
				+ ((cdnLavoratore!=null&&!cdnLavoratore.equals("")) ? ""
						: " || ' / ' || PG_UTILS.TRUNC_DESC(AZI.STRRAGIONESOCIALE, 30, '..')  ")
				+ " AS STRINFRIFAZILAV ");
		dsu
				.addSelect(" LAV.STRCODICEFISCALE "
						+ (cdnLavoratore!=null&&!cdnLavoratore.equals("") ? "" : " || AZI.STRCODICEFISCALE ")
						+ " AS STRCODICEFISCALE,"
						+ " DOC.CODCPI AS CODCPI,"
						+ " CPI.STRDESCRIZIONE AS CODCPI_DESC,"
						+ " CASE"
						+ " 		WHEN DOC.NUMANNOPROT IS NOT NULL AND DOC.NUMPROTOCOLLO IS NOT NULL THEN DOC.NUMANNOPROT || '/' || DOC.NUMPROTOCOLLO || ' '"
						+ " 		WHEN DOC.NUMANNOPROT IS NOT NULL AND DOC.NUMPROTOCOLLO IS NULL     THEN DOC.NUMANNOPROT || ' '"
						+ " 		WHEN DOC.NUMANNOPROT IS NULL     AND DOC.NUMPROTOCOLLO IS NOT NULL THEN DOC.NUMPROTOCOLLO || ' '"
						+ " END" + " || TO_CHAR(DOC.DATPROTOCOLLO,'DD/MM/YYYY') || ' '" + " || CASE"
						+ " 		WHEN UPPER(DOC.CODMONOIO) = 'I' THEN 'IN'"
						+ " 		WHEN UPPER(DOC.CODMONOIO) = 'O' THEN 'OUT'" + " 	 END" + " AS INFOPROTOCOLLO,"
						+ " pg_utils.trunc_desc(doc.strdescrizione, 20, '..') ||" 
						//+ " DECODE(SYSDATE, GREATEST(SYSDATE, ST.DATFINEVAL),' (scaduto)',"
						//+ " LEAST(SYSDATE, ST.DATINIZIOVAL), ' (scaduto)'," 
						+ " ( case when TRUNC(sysdate) > trunc(ST.DATFINEVAL) then '(scaduto)'  else ' ' end     "
						+ " ) AS STRDESCRIZIONEDOC,"
						+ " DOC.STRNOMEDOC AS STRNOMEDOC," + " DOC.NUMPROTOCOLLO AS NUMPROTOCOLLO,"
						+ " TO_CHAR(DOC.DATACQRIL, 'DD/MM/YYYY') AS DATACQRIL,"
						+ " DOC.CODTIPODOCUMENTO AS CODTIPODOCUMENTO,"
						+ " PG_UTILS.TRUNC_DESC(AMB.STRDESCRIZIONE, 20, '...') AS STRDESCRIZIONEAMBITO, "
						+ " CASE"
						+ " WHEN MATTO.STRDESCRIZIONE IS NULL THEN PG_UTILS.TRUNC_DESC(TD.STRDESCRIZIONE,  20, '...') || ' - ' || SA.STRDESCRIZIONE "
						+ " ELSE PG_UTILS.TRUNC_DESC(TD.STRDESCRIZIONE,  20, '...') || ' - ' || SA.STRDESCRIZIONE || ' - ' || MATTO.STRDESCRIZIONE " 
						+ " END AS STRDESCRIZIONETIPODOC, "
						+ " PG_UTILS.TRUNC_DESC(DOC.STRENTERILASCIO,  40, '...') AS STRENTERILASCIO, " +
						// NON USATO: " TF.STRDESCRIZIONE AS
						// STRDESCRIZIONETIPOFILE,"
						" DOC.CODTIPOFILE CODTIPOFILE, DOC.PRGTEMPLATESTAMPA, DOC.DATACQRIL AS DATSORT");

		dsu.addFrom("AM_DOCUMENTO DOC");
		dsu.addFrom("DE_STATO_ATTO SA");
		dsu.addFrom("DE_MOT_ANNULLAMENTO_ATTO MATTO");

		dsu.addFrom("DE_DOC_TIPO TD");
		dsu.addFrom("DE_DOC_AMBITO AMB");
		dsu.addFrom("DE_CPI CPI");
		dsu.addFrom("AN_LAVORATORE LAV");
		dsu.addFrom("AN_AZIENDA AZI");
		dsu.addFrom("ST_TEMPLATE_STAMPA ST");
		
		

		
		dsu.addWhere("TD.CODTIPODOCUMENTO    = DOC.CODTIPODOCUMENTO");
		dsu.addWhere("AMB.CODAMBITODOC       = TD.CODAMBITODOC");
		dsu.addWhere("CPI.CODCPI          (+)= DOC.CODCPI");
		dsu.addWhere("LAV.CDNLAVORATORE   (+)= DOC.CDNLAVORATORE");
		dsu.addWhere("AZI.PRGAZIENDA      (+)= DOC.PRGAZIENDA");
		dsu.addWhere("SA.CODSTATOATTO     (+)= DOC.CODSTATOATTO");
		dsu.addWhere("MATTO.CODMOTANNULLAMENTOATTO (+)= DOC.CODMOTANNULLAMENTOATTO");
		dsu.addWhere("ST.PRGTEMPLATESTAMPA(+) = DOC.PRGTEMPLATESTAMPA");
		
		if(cdnProfilo==9){
			dsu.addWhere("ST.FLG_PATRONATO = 1");
		}

		if (codStatoAtto != null && !codStatoAtto.equals("")) {
			dsu.addWhere(" DOC.CODSTATOATTO = '" + codStatoAtto + "'");
		}

		if (StringUtils.isFilled(pagina)) {
			dsu.addFrom("TS_COMPONENTE COM");
			dsu.addWhereIfFilledStrUpper("COM.STRPAGE", pagina);
		}
		
		dsu.addWhere("DOC.PRGTEMPLATESTAMPA IS NOT NULL");
		
		dsu.addWhereIfFilledNum("DOC.CDNLAVORATORE", cdnLavoratore);
		dsu.addWhereIfFilledNum("DOC.PRGAZIENDA", prgAzienda);

		// ORDINAMENTO
		//dsu.addOrder("STRDESCRIZIONEAMBITO, DATSORT DESC, INFOPROTOCOLLO, STRDESCRIZIONEDOC");
		dsu.addOrder("DOC.DATACQRIL DESC,STRDESCRIZIONEAMBITO, DATSORT DESC, INFOPROTOCOLLO, STRDESCRIZIONEDOC");

		String query = dsu.getStatement();

		_logger.info(className + ".getStatement() FINE, con query=" + query);
		return query;
	}
	
}