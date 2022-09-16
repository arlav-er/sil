package it.eng.sil.module.documenti;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.DynamicStatementUtils;
import it.eng.afExt.utils.StringUtils;

public class documentiListPi3 implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(documentiListPi3.class.getName());

	private String className = StringUtils.getClassName(documentiList.class);

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		_logger.debug(className + ".getStatement() INIZIO");

		SourceBean req = requestContainer.getServiceRequest();

		// Campi di Lavoratore e Azienda+Unita
		String cdnLavoratore = (String) req.getAttribute("cdnLavoratore");
		String prgAzienda = (String) req.getAttribute("prgAzienda");
		String prgUnita = (String) req.getAttribute("prgUnita");
		String prgDocumento = (String) req.getAttribute("prgDocumento");

		// Campi di protocollo
		String codStatoAtto = "PR";
		boolean nascondiSepLavoratoreAzienda = req.containsAttribute("nascondiSepCampoLav");

		// Altri campi dalla QueryString:
		String pagina = (String) req.getAttribute("pagina");
		String strChiaveTabella = (String) req.getAttribute("strChiaveTabella");

		// ORA CREO LA QUERY DINAMICAMENTE (usando un "DynamicStatementUtils"):
		DynamicStatementUtils dsu = new DynamicStatementUtils();

		dsu.addSelect(" ADL.PRGDOCUMENTOFIGLIO AS PRGDOCUMENTO," + " DOC.CDNLAVORATORE AS CDNLAVORATORE,"
				+ " DOC.PRGAZIENDA," + " DOC.PRGUNITA," + " ADL.FLGPRESAVISIONE, ADL.FLGCARICSUCCESSIVO ");

		// commentato perché ora nella tabella AM_DOCUMENTO è stata inserita la
		// colonna CODSTATOATTO
		// e quindi non è più necessario passare per la tabella AM_MOVIMENTO
		// dsu.addSelect(" DOCCOLL.STRCHIAVETABELLA");
		dsu.addSelect(" LAV.STRCOGNOME||' '||LAV.STRNOME "
				+ (nascondiSepLavoratoreAzienda ? ""
						: " || ' / ' || PG_UTILS.TRUNC_DESC(AZI.STRRAGIONESOCIALE, 30, '..')  ")
				+ " AS STRINFRIFAZILAV");
		dsu.addSelect(" LAV.STRCODICEFISCALE," + " DOC.CODCPI AS CODCPI," + " CPI.STRDESCRIZIONE AS CODCPI_DESC,"
				+ " CASE"
				+ " 		WHEN DOC.NUMANNOPROT IS NOT NULL AND DOC.NUMPROTOCOLLO IS NOT NULL THEN DOC.NUMANNOPROT || '/' || DOC.NUMPROTOCOLLO || ' '"
				+ " 		WHEN DOC.NUMANNOPROT IS NOT NULL AND DOC.NUMPROTOCOLLO IS NULL     THEN DOC.NUMANNOPROT || ' '"
				+ " 		WHEN DOC.NUMANNOPROT IS NULL     AND DOC.NUMPROTOCOLLO IS NOT NULL THEN DOC.NUMPROTOCOLLO || ' '"
				+ " END" + " || TO_CHAR(DOC.DATPROTOCOLLO,'DD/MM/YYYY') || ' '" + " || CASE"
				+ " 		WHEN UPPER(DOC.CODMONOIO) = 'I' THEN 'IN'"
				+ " 		WHEN UPPER(DOC.CODMONOIO) = 'O' THEN 'OUT'" + " 	 END" + " AS INFOPROTOCOLLO,"
				+ " PG_UTILS.TRUNC_DESC(DOC.STRDESCRIZIONE, 30, '..') AS STRDESCRIZIONEDOC,"
				+ " DOC.STRNOMEDOC AS STRNOMEDOC," + " DOC.NUMPROTOCOLLO AS NUMPROTOCOLLO,"
				+ " TO_CHAR(DOC.DATACQRIL, 'DD/MM/YYYY') AS DATACQRIL," + " DOC.CODTIPODOCUMENTO AS CODTIPODOCUMENTO,"
				+ " PG_UTILS.TRUNC_DESC(AMB.STRDESCRIZIONE, 20, '...') AS STRDESCRIZIONEAMBITO," +
				// " PG_UTILS.TRUNC_DESC(TD.STRDESCRIZIONE, 20, '...')
				// || DECODE(TD.CODAMBITODOC,'MV', ' - ' ||
				// SA.STRDESCRIZIONE,'') AS STRDESCRIZIONETIPODOC," +
				" CASE"
				+ " WHEN MATTO.STRDESCRIZIONE IS NULL THEN PG_UTILS.TRUNC_DESC(TD.STRDESCRIZIONE,  20, '...') || ' - ' || SA.STRDESCRIZIONE "
				+ " ELSE PG_UTILS.TRUNC_DESC(TD.STRDESCRIZIONE,  20, '...') || ' - ' || SA.STRDESCRIZIONE || ' - ' || MATTO.STRDESCRIZIONE "
				+ " END AS STRDESCRIZIONETIPODOC,"
				+ " PG_UTILS.TRUNC_DESC(DOC.STRENTERILASCIO,  40, '...') AS STRENTERILASCIO," +
				// NON USATO: " TF.STRDESCRIZIONE AS
				// STRDESCRIZIONETIPOFILE,"
				" DOC.CODTIPOFILE CODTIPOFILE, DOC.DATACQRIL AS DATSORT");

		dsu.addFrom("AM_DOCUMENTO DOC");

		dsu.addFrom("DE_STATO_ATTO SA");
		dsu.addFrom("DE_MOT_ANNULLAMENTO_ATTO MATTO");

		dsu.addFrom("DE_DOC_TIPO TD");
		dsu.addFrom("DE_DOC_AMBITO AMB");
		dsu.addFrom("DE_CPI CPI");
		dsu.addFrom("AN_LAVORATORE LAV");
		dsu.addFrom("AN_AZIENDA AZI");

		// PROTOCOLLAZIONE PI3 GESTIONE ALLEGATI
		dsu.addFrom("AM_DOCUMENTO_ALLEGATO ADL");

		// NON USATO: dsu.addWhere("TF.CODTIPOFILE (+)= DOC.CODTIPOFILE");
		dsu.addWhere("TD.CODTIPODOCUMENTO    = DOC.CODTIPODOCUMENTO");
		dsu.addWhere("AMB.CODAMBITODOC       = TD.CODAMBITODOC");
		dsu.addWhere("CPI.CODCPI          (+)= DOC.CODCPI");
		dsu.addWhere("LAV.CDNLAVORATORE   (+)= DOC.CDNLAVORATORE");
		dsu.addWhere("AZI.PRGAZIENDA      (+)= DOC.PRGAZIENDA");

		dsu.addWhere("SA.CODSTATOATTO     (+)= DOC.CODSTATOATTO");
		dsu.addWhere("MATTO.CODMOTANNULLAMENTOATTO (+)= DOC.CODMOTANNULLAMENTOATTO");

		// PROTOCOLLAZIONE PI3 GESTIONE ALLEGATI
		dsu.addWhere("ADL.PRGDOCUMENTOFIGLIO = DOC.PRGDOCUMENTO");
		dsu.addWhere("ADL.PRGDOCUMENTOPADRE = " + prgDocumento);

		if (codStatoAtto != null && !codStatoAtto.equals("")) {
			dsu.addWhere(" DOC.CODSTATOATTO = '" + codStatoAtto + "'");
		}

		/*
		 * if (StringUtils.isFilled(pagina)) {
		 * 
		 * dsu.addSelect("DOCCOL.STRCHIAVETABELLA AS STRCHIAVETABELLA");
		 * 
		 * dsu.addFrom("AM_DOCUMENTO_COLL DOCCOL, TS_COMPONENTE COM");
		 * 
		 * dsu.addWhere("DOCCOL.PRGDOCUMENTO = DOC.PRGDOCUMENTO");
		 * dsu.addWhere("COM.CDNCOMPONENTE   = DOCCOL.CDNCOMPONENTE");
		 * 
		 * dsu.addWhereIfFilledStrUpper("COM.STRPAGE", pagina); dsu.addWhereIfFilledStrUpper("DOCCOL.STRCHIAVETABELLA",
		 * strChiaveTabella); }
		 */

		dsu.addWhereIfFilledNum("DOC.CDNLAVORATORE", cdnLavoratore);
		dsu.addWhereIfFilledNum("DOC.PRGAZIENDA", prgAzienda);

		if (prgUnita != null && !prgUnita.equals("")) {
			dsu.addWhere("nvl(doc.PRGUNITA, 1 ) = " + prgUnita);
		}

		// ORDINAMENTO
		dsu.addOrder("STRDESCRIZIONEAMBITO, DATSORT DESC, INFOPROTOCOLLO, STRDESCRIZIONEDOC");

		String query = dsu.getStatement();

		_logger.debug(className + ".getStatement() FINE, con query=" + query);

		return query;
	}

}