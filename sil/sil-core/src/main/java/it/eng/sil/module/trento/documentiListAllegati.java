package it.eng.sil.module.trento;

import java.util.Collection;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.DynamicStatementUtils;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;

public class documentiListAllegati implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(documentiListAllegati.class.getName());

	private String className = StringUtils.getClassName(documentiListAllegati.class);

	private ReportOperationResult reportOperationResult = null;

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		_logger.debug(className + ".getStatement() INIZIO");

		SourceBean req = requestContainer.getServiceRequest();

		// Campi di Lavoratore
		String cdnLavoratore = (String) req.getAttribute("cdnLavoratore");
		String prgAzienda = (String) req.getAttribute("prgAzienda");
		String prgUnita = (String) req.getAttribute("prgUnita");

		// Campi di protocollo
		String strAnnoProtocolloDa = (String) req.getAttribute("annoProtocollo_da");
		String strAnnoProtocolloA = (String) req.getAttribute("annoProtocollo_a");
		String numProtocolloDa = (String) req.getAttribute("numProtocollo_da");
		String numProtocolloA = (String) req.getAttribute("numProtocollo_a");
		String strDataProtocolloDa = (String) req.getAttribute("dataProtocollo_da");
		String strDataProtocolloA = (String) req.getAttribute("dataProtocollo_a");
		String docIO = (String) req.getAttribute("docIO");
		String ambito = (String) req.getAttribute("ambito");
		String codStatoAtto = (String) req.getAttribute("CODSTATOATTO");

		boolean nascondiSepLavoratoreAzienda = false;

		// Campi di Documento
		Object td = req.getAttribute("tipoDocumento");
		Collection tipoDocumento = null;
		if (td != null) {
			tipoDocumento = AbstractSimpleModule.getArgumentValues(req, "tipoDocumento");
		}

		String autoCertificazione = (String) req.getAttribute("autoCertificazione");
		String docAmministrativo = (String) req.getAttribute("docAmministrativo");
		String docIdentificazione = (String) req.getAttribute("docIdentificazione");
		String datInizioDa = (String) req.getAttribute("DatInizio_da");
		String datInizioA = (String) req.getAttribute("DatInizio_a");
		String datFineDa = (String) req.getAttribute("DatFine_da");
		String datFineA = (String) req.getAttribute("DatFine_a");

		String strNumDoc = (String) req.getAttribute("strNumDoc");
		String strEnteRilascio = (String) req.getAttribute("strEnteRilascio");
		String tipoRicercaEnteRil = (String) req.getAttribute("tipoRicercaEnteRil");
		String codCpi = (String) req.getAttribute("codCpi");

		String strDescrizione = (String) req.getAttribute("strDescrizione");

		String templateStampa = (String) req.getAttribute("TEMPLATESTAMPA");
		String prgTemplateStampa = (String) req.getAttribute("PRGTEMPLATESTAMPA");
		_logger.debug(className + ".getStatement() PARAMETRO TEMPLATESTAMPA=" + templateStampa);
		_logger.debug(className + ".getStatement() PARAMETRO PRGTEMPLATESTAMPA=" + prgTemplateStampa);

		String prgDocumento = (String) req.getAttribute("PRGDOCUMENTO");

		// ORA CREO LA QUERY DINAMICAMENTE
		DynamicStatementUtils dsu = new DynamicStatementUtils();

		dsu.addSelect(" DOC.PRGDOCUMENTO AS PRGDOCUMENTO," + " DOC.CDNLAVORATORE AS CDNLAVORATORE," + " DOC.PRGAZIENDA,"
				+ " DOC.PRGUNITA");

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
				+ " PG_UTILS.TRUNC_DESC(AMB.STRDESCRIZIONE, 20, '...') AS STRDESCRIZIONEAMBITO," + " CASE"
				+ " WHEN MATTO.STRDESCRIZIONE IS NULL THEN PG_UTILS.TRUNC_DESC(TD.STRDESCRIZIONE,  20, '...') || ' - ' || SA.STRDESCRIZIONE "
				+ " ELSE PG_UTILS.TRUNC_DESC(TD.STRDESCRIZIONE,  20, '...') || ' - ' || SA.STRDESCRIZIONE || ' - ' || MATTO.STRDESCRIZIONE "
				+ " END AS STRDESCRIZIONETIPODOC,"
				+ " PG_UTILS.TRUNC_DESC(DOC.STRENTERILASCIO,  40, '...') AS STRENTERILASCIO,"
				+ " DOC.CODTIPOFILE CODTIPOFILE, DOC.DATACQRIL AS DATSORT");

		dsu.addFrom("AM_DOCUMENTO DOC");

		dsu.addFrom("DE_STATO_ATTO SA");
		dsu.addFrom("DE_MOT_ANNULLAMENTO_ATTO MATTO");

		dsu.addFrom("DE_DOC_TIPO TD");
		dsu.addFrom("DE_DOC_AMBITO AMB");
		dsu.addFrom("DE_CPI CPI");
		dsu.addFrom("AN_LAVORATORE LAV");
		dsu.addFrom("AN_AZIENDA AZI");

		if (templateStampa != null && !templateStampa.equalsIgnoreCase("")) {
			templateStampa = templateStampa.substring(("Template: ").length());
			dsu.addFrom("ST_TEMPLATE_STAMPA TEM");
			dsu.addWhere("DOC.STRDESCRIZIONE = '" + templateStampa + "'");
			// dsu.addWhere("DOC.PRGTEMPLATESTAMPA is not null");
			// dsu.addWhere("DOC.STRDESCRIZIONE = 'Template: '||TEM.STRNOME");
			dsu.addWhere("DOC.STRDESCRIZIONE = TEM.STRNOME");
			dsu.addWhere("TEM.DATCANC IS NULL");
		}

		dsu.addWhere("TD.CODTIPODOCUMENTO    = DOC.CODTIPODOCUMENTO");
		dsu.addWhere("AMB.CODAMBITODOC       = TD.CODAMBITODOC");
		dsu.addWhere("CPI.CODCPI          (+)= DOC.CODCPI");
		dsu.addWhere("LAV.CDNLAVORATORE   (+)= DOC.CDNLAVORATORE");
		dsu.addWhere("AZI.PRGAZIENDA      (+)= DOC.PRGAZIENDA");

		dsu.addWhere("SA.CODSTATOATTO     (+)= DOC.CODSTATOATTO");
		dsu.addWhere("MATTO.CODMOTANNULLAMENTOATTO (+)= DOC.CODMOTANNULLAMENTOATTO");

		if (codStatoAtto != null && !codStatoAtto.equals("")) {
			dsu.addWhere(" DOC.CODSTATOATTO = '" + codStatoAtto + "'");
		}

		dsu.addWhereIfFilledNum("DOC.CDNLAVORATORE", cdnLavoratore);
		dsu.addWhereIfFilledNum("DOC.PRGAZIENDA", prgAzienda);
		dsu.addWhereIfFilledNum("DOC.PRGUNITA", prgUnita);
		dsu.addWhereIfFilledNumBetween("DOC.NUMANNOPROT", strAnnoProtocolloDa, strAnnoProtocolloA);

		dsu.addWhereIfFilledNumBetween("DOC.NUMPROTOCOLLO", numProtocolloDa, numProtocolloA);

		dsu.addWhereIfFilledDateBetween("DOC.DATPROTOCOLLO", strDataProtocolloDa, strDataProtocolloA);
		// Nota: NON uso la "BetweenOrNull".

		dsu.addWhereIfFilledStr("DOC.CODMONOIO", docIO);
		dsu.addWhereIfFilledStr("TD.CODAMBITODOC", ambito);

		if (tipoDocumento != null && tipoDocumento.size() > 0) {
			StringBuffer sqlTemp = new StringBuffer("TD.CODTIPODOCUMENTO IN (");
			Object[] tipidoc = tipoDocumento.toArray();
			for (int i = 0; i < tipidoc.length; i++) {
				sqlTemp.append('\'');
				String tipoDocStr = StringUtils.formatValue4Sql(tipidoc[i].toString());
				sqlTemp.append(tipoDocStr);
				sqlTemp.append("',");
			}
			sqlTemp.setCharAt(sqlTemp.length() - 1, ')'); // rimpiazzo ultima virgola con tonda chiusa
			dsu.addWhere(sqlTemp.toString());
		}

		dsu.addWhereIfFilledStr("DOC.FLGDOCAMM", docAmministrativo);
		dsu.addWhereIfFilledStr("DOC.FLGDOCIDENTIFP", docIdentificazione);
		dsu.addWhereIfFilledStr("DOC.FLGAUTOCERTIFICAZIONE", autoCertificazione);

		dsu.addWhereIfFilledDateBetween("DOC.DATINIZIO", datInizioDa, datInizioA);
		dsu.addWhereIfFilledDateBetween("DOC.DATFINE", datFineDa, datFineA);
		dsu.addWhereIfFilledStr("DOC.STRNUMDOC", strNumDoc);

		if ((tipoRicercaEnteRil != null) && tipoRicercaEnteRil.equalsIgnoreCase("esatta")) {
			dsu.addWhereIfFilledStrUpper("DOC.STRENTERILASCIO", strEnteRilascio);
		} else {
			dsu.addWhereIfFilledStrLikeUpper("DOC.STRENTERILASCIO", strEnteRilascio,
					DynamicStatementUtils.DO_LIKE_CONTIENE);
		}

		dsu.addWhereIfFilledStr("DOC.CODCPI", codCpi);

		dsu.addWhereIfFilledStrLikeUpper("DOC.STRDESCRIZIONE", strDescrizione, DynamicStatementUtils.DO_LIKE_CONTIENE);

		// controllo aggiunto per escludere i documenti già allegati
		dsu.addWhere("PRGDOCUMENTO not in ("
				+ "select allegato.PRGDOCUMENTOFIGLIO  from am_documento_allegato allegato  where allegato.PRGDOCUMENTOPADRE = "
				+ prgDocumento + ")");

		// ORDINAMENTO
		dsu.addOrder("STRDESCRIZIONEAMBITO, DATSORT DESC, INFOPROTOCOLLO, STRDESCRIZIONEDOC");

		String query = dsu.getStatement();

		_logger.info(className + ".getStatement() FINE, con query=" + query);

		return query;
	}

}