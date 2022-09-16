package it.eng.sil.module.documenti;

import java.util.Collection;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.dbaccess.sql.IDynamicStatementProvider;
import it.eng.afExt.utils.DynamicStatementUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;

public class documentiList implements IDynamicStatementProvider {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(documentiList.class.getName());

	private String className = StringUtils.getClassName(documentiList.class);

	public String getStatement(RequestContainer requestContainer, SourceBean config) {

		_logger.debug(className + ".getStatement() INIZIO");

		SourceBean req = requestContainer.getServiceRequest();

		// Campi di Lavoratore e Azienda+Unita
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
		// parametro che determina se visualizzare o meno la barra di
		// separazione nel campi del nome lavoratore
		// viene utilizzato nella lista dichiarazioni/attestazioni dove non
		// bisogna riportare il nome dell'azienda
		// Savino 01/04/05
		boolean nascondiSepLavoratoreAzienda = req.containsAttribute("nascondiSepCampoLav");

		// Campi di Documento
		// (tipoDocumento: questi dati mi servono se arrivo dalla pagina di
		// ricerca)
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

		// Altri campi dalla QueryString:
		String pagina = (String) req.getAttribute("pagina");
		// se c'è il parametro pagina significa che sto cercando i documenti
		// associati a quella data pagina (PAGE) per lo specifico lavoratore

		String infStoriche = (String) req.getAttribute("infStoriche");

		String strChiaveTabella = (String) req.getAttribute("strChiaveTabella");

		// ORA CREO LA QUERY DINAMICAMENTE (usando un "DynamicStatementUtils"):
		DynamicStatementUtils dsu = new DynamicStatementUtils();

		dsu.addSelect(" DOC.PRGDOCUMENTO AS PRGDOCUMENTO," + " DOC.CDNLAVORATORE AS CDNLAVORATORE," + " DOC.PRGAZIENDA,"
				+ " DOC.PRGUNITA");

		// commentato perché ora nella tabella AM_DOCUMENTO è stata inserita la
		// colonna CODSTATOATTO
		// e quindi non è più necessario passare per la tabella AM_MOVIMENTO
		// dsu.addSelect(" DOCCOLL.STRCHIAVETABELLA");
		dsu.addSelect(" LAV.STRCOGNOME||' '||LAV.STRNOME "
				+ (nascondiSepLavoratoreAzienda ? ""
						: " || ' / ' || PG_UTILS.TRUNC_DESC(AZI.STRRAGIONESOCIALE, 30, '..')  ")
				+ " AS STRINFRIFAZILAV");
		dsu.addSelect(" LAV.STRCODICEFISCALE," + " DOC.CODCPI AS CODCPI," + " CPI.STRDESCRIZIONE AS CODCPI_DESC,"
				+ " DOC.NUMANNOPROT || '/' || DOC.NUMPROTOCOLLO as annoprotNum," + " CASE"
				+ " 		WHEN DOC.NUMANNOPROT IS NOT NULL AND DOC.NUMPROTOCOLLO IS NOT NULL THEN DOC.NUMANNOPROT || '/' || DOC.NUMPROTOCOLLO || ' '"
				+ " 		WHEN DOC.NUMANNOPROT IS NOT NULL AND DOC.NUMPROTOCOLLO IS NULL     THEN DOC.NUMANNOPROT || ' '"
				+ " 		WHEN DOC.NUMANNOPROT IS NULL     AND DOC.NUMPROTOCOLLO IS NOT NULL THEN DOC.NUMPROTOCOLLO || ' '"
				+ " END" + " || TO_CHAR(DOC.DATPROTOCOLLO,'DD/MM/YYYY') || ' '" + " || CASE"
				+ " 		WHEN UPPER(DOC.CODMONOIO) = 'I' THEN 'IN'"
				+ " 		WHEN UPPER(DOC.CODMONOIO) = 'O' THEN 'OUT'" + " 	 END" + " AS INFOPROTOCOLLO,"
				+ " PG_UTILS.TRUNC_DESC(DOC.STRDESCRIZIONE, 30, '..') AS STRDESCRIZIONEDOC,"
				+ " DOC.STRNOMEDOC AS STRNOMEDOC," + " DOC.NUMPROTOCOLLO AS NUMPROTOCOLLO,"
				+ " TO_CHAR(DOC.DATACQRIL, 'DD/MM/YYYY') AS DATACQRIL,"
				+ " DOC.CODTIPODOCUMENTO AS CODTIPODOCUMENTO, DOC.CODSTATOATTO AS CODSTATOATTO, "
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
		// NON USATO: dsu.addFrom("DE_DOC_TIPO_FILE TF");

		// commentato perché ora nella tabella AM_DOCUMENTO è stata inserita la
		// colonna CODSTATOATTO
		// e quindi non è più necessario passare per la tabella AM_MOVIMENTO
		// dsu.addFrom("AM_DOCUMENTO_COLL DOCCOLL");
		// dsu.addFrom("AM_MOVIMENTO MOV");
		dsu.addFrom("DE_STATO_ATTO SA");
		dsu.addFrom("DE_MOT_ANNULLAMENTO_ATTO MATTO");

		dsu.addFrom("DE_DOC_TIPO TD");
		dsu.addFrom("DE_DOC_AMBITO AMB");
		dsu.addFrom("DE_CPI CPI");
		dsu.addFrom("AN_LAVORATORE LAV");
		dsu.addFrom("AN_AZIENDA AZI");

		// NON USATO: dsu.addWhere("TF.CODTIPOFILE (+)= DOC.CODTIPOFILE");
		dsu.addWhere("TD.CODTIPODOCUMENTO    = DOC.CODTIPODOCUMENTO");
		dsu.addWhere("AMB.CODAMBITODOC       = TD.CODAMBITODOC");
		dsu.addWhere("CPI.CODCPI          (+)= DOC.CODCPI");
		dsu.addWhere("LAV.CDNLAVORATORE   (+)= DOC.CDNLAVORATORE");
		dsu.addWhere("AZI.PRGAZIENDA      (+)= DOC.PRGAZIENDA");

		/*
		 * if(prgAzienda != null && !prgAzienda.equals("")){ dsu.addWhere("nvl(doc.PRGAZIENDA," + prgAzienda + ") = " +
		 * prgAzienda); }
		 */

		// commentato perché ora nella tabella AM_DOCUMENTO è stata inserita la
		// colonna CODSTATOATTO
		// e quindi non è più necessario passare per la tabella AM_MOVIMENTO
		// dsu.addWhere("DOCCOLL.PRGDOCUMENTO(+)= DOC.PRGDOCUMENTO");
		// dsu.addWhere("MOV.PRGMOVIMENTO (+)= DOCCOLL.STRCHIAVETABELLA");
		// dsu.addWhere("SA.CODSTATOATTO (+)= MOV.CODSTATOATTO");
		dsu.addWhere("SA.CODSTATOATTO     (+)= DOC.CODSTATOATTO");
		dsu.addWhere("MATTO.CODMOTANNULLAMENTOATTO (+)= DOC.CODMOTANNULLAMENTOATTO");

		if (codStatoAtto != null && !codStatoAtto.equals("")) {
			dsu.addWhere(" DOC.CODSTATOATTO = '" + codStatoAtto + "'");
		}

		if (StringUtils.isFilled(pagina)) {

			dsu.addSelect("DOCCOL.STRCHIAVETABELLA AS STRCHIAVETABELLA");

			dsu.addFrom("AM_DOCUMENTO_COLL DOCCOL, TS_COMPONENTE COM");

			dsu.addWhere("DOCCOL.PRGDOCUMENTO = DOC.PRGDOCUMENTO");
			dsu.addWhere("COM.CDNCOMPONENTE   = DOCCOL.CDNCOMPONENTE");

			dsu.addWhereIfFilledStrUpper("COM.STRPAGE", pagina);
			dsu.addWhereIfFilledStrUpper("DOCCOL.STRCHIAVETABELLA", strChiaveTabella);
		}

		dsu.addWhereIfFilledNum("DOC.CDNLAVORATORE", cdnLavoratore);
		dsu.addWhereIfFilledNum("DOC.PRGAZIENDA", prgAzienda);

		if (prgUnita != null && !prgUnita.equals("")) {
			dsu.addWhere("nvl(doc.PRGUNITA, 1 ) = " + prgUnita);
		}

		// dsu.addWhereIfFilledNum("DOC.PRGUNITA", prgUnita);

		// GG 21-2-05 Aggiunto "da-a" per le date, anno e num.protocollo
		// dsu.addWhereIfFilledNum("DOC.NUMANNOPROT", strAnnoProtocollo);
		dsu.addWhereIfFilledNumBetween("DOC.NUMANNOPROT", strAnnoProtocolloDa, strAnnoProtocolloA);

		// GG 1-3-05 - gestione num.protocollo come NUMERO e non come STRINGA
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
			sqlTemp.setCharAt(sqlTemp.length() - 1, ')'); // rimpiazzo ultima
															// virgola con tonda
															// chiusa
			dsu.addWhere(sqlTemp.toString());
		}

		dsu.addWhereIfFilledStr("DOC.FLGDOCAMM", docAmministrativo);
		dsu.addWhereIfFilledStr("DOC.FLGDOCIDENTIFP", docIdentificazione);
		dsu.addWhereIfFilledStr("DOC.FLGAUTOCERTIFICAZIONE", autoCertificazione);

		// Nel dettaglio documento la data-inizio è obbligatoria e quindi sempre
		// valorizzata.
		// Non devo distinguere se "datInizioA" è nulla per recuperare anche
		// quelli con data nulla.
		dsu.addWhereIfFilledDateBetween("DOC.DATINIZIO", datInizioDa, datInizioA);

		// NOTA BENE:
		// Se "data fine validità" *NON* è valorizzata (nè DA nè A),
		if (StringUtils.isEmpty(datFineDa) && StringUtils.isEmpty(datFineA)) {

			if ((infStoriche != null) && infStoriche.equalsIgnoreCase("true")) {
				// Se sto cercando info "storiche",
				// considero solo quelli con data fine valorizzata e minore
				// della data odierna.
				// ORIGINALE: dsu.addWhere("NVL(DOC.DATFINE, SYSDATE) <
				// SYSDATE");
				// OTTIMIZZATA (con indici):
				dsu.addWhere("( DECODE(DOC.DATFINE, NULL, 'S','N') = 'N'  AND  DOC.DATFINE < TRUNC(SYSDATE) )");

			} else {
				// Altrimenti (sempre però per "data fine validità" non è
				// valorizzata),
				// considero solo quelli "non scaduti", cioè con data fine nulla
				// o maggiore o uguale della data odierna.
				// ORIGINALE: dsu.addWhere("NVL(DOC.DATFINE, SYSDATE) >=
				// SYSDATE");
				// OTTIMIZZATA (con indici):
				dsu.addWhere("( DECODE(DOC.DATFINE, NULL, 'S','N') = 'S'  OR  DOC.DATFINE >= TRUNC(SYSDATE) )");
			}
		} else {
			dsu.addWhereIfFilledDateBetweenOrNull("DOC.DATFINE", datFineDa, datFineA);
			// Nota: uso "OrNull" per recuperare anche quelli con dataFine
			// nulla.
		}

		dsu.addWhereIfFilledStr("DOC.STRNUMDOC", strNumDoc);

		if ((tipoRicercaEnteRil != null) && tipoRicercaEnteRil.equalsIgnoreCase("esatta")) {
			dsu.addWhereIfFilledStrUpper("DOC.STRENTERILASCIO", strEnteRilascio);
		} else {
			dsu.addWhereIfFilledStrLikeUpper("DOC.STRENTERILASCIO", strEnteRilascio,
					DynamicStatementUtils.DO_LIKE_CONTIENE);
		}

		dsu.addWhereIfFilledStr("DOC.CODCPI", codCpi);

		dsu.addWhereIfFilledStrLikeUpper("DOC.STRDESCRIZIONE", strDescrizione, DynamicStatementUtils.DO_LIKE_CONTIENE);

		// ORDINAMENTO
		dsu.addOrder("STRDESCRIZIONEAMBITO, DATSORT DESC, INFOPROTOCOLLO, STRDESCRIZIONEDOC");

		String query = dsu.getStatement();

		_logger.debug(className + ".getStatement() FINE, con query=" + query);

		return query;
	}

	/*
	 * VERSIONE CON SDOPPIAMENTO DELLE RIGHE PER LAVORATORE E AZIENDA:
	 * 
	 * public String getStatement(RequestContainer requestContainer, SourceBean config) {
	 * 
	 * _logger.debug( className+".getStatement() INIZIO");
	 * 
	 * 
	 * SourceBean req = requestContainer.getServiceRequest(); // Campi di Lavoratore e Azienda+Unita String
	 * cdnLavoratore = (String)req.getAttribute("cdnLavoratore"); String prgAzienda =
	 * (String)req.getAttribute("prgAzienda"); String prgUnita = (String)req.getAttribute("prgUnita"); // Campi di
	 * protocollo String strAnnoProtocollo = (String)req.getAttribute("annoProtocollo"); String numProtocollo =
	 * (String)req.getAttribute("numProtocollo"); String strDataProtocollo = (String)req.getAttribute("dataProtocollo");
	 * String docIO = (String)req.getAttribute("docIO"); String ambito = (String)req.getAttribute("ambito"); // Campi di
	 * Documento // (tipoDocumento: questi dati mi servono se arrivo dalla pagina di ricerca) Object td =
	 * req.getAttribute("tipoDocumento"); Collection tipoDocumento = null; if (td != null) { tipoDocumento =
	 * AbstractSimpleModule.getArgumentValues(req, "tipoDocumento"); }
	 * 
	 * String autoCertificazione = (String)req.getAttribute("autoCertificazione"); String docAmministrativo =
	 * (String)req.getAttribute("docAmministrativo"); String docIdentificazione =
	 * (String)req.getAttribute("docIdentificazione"); String datInizio = (String)req.getAttribute("DatInizio"); String
	 * datFine = (String)req.getAttribute("DatFine");
	 * 
	 * String strNumDoc = (String)req.getAttribute("strNumDoc"); String strEnteRilascio =
	 * (String)req.getAttribute("strEnteRilascio"); String tipoRicercaEnteRil =
	 * (String)req.getAttribute("tipoRicercaEnteRil"); String codCpi = (String)req.getAttribute("codCpi");
	 * 
	 * String strDescrizione = (String)req.getAttribute("strDescrizione"); // Altri campi dalla QueryString: String
	 * pagina = (String)req.getAttribute("pagina"); // se c'è il parametro pagina significa che sto cercando i documenti
	 * // associati a quella data pagina (PAGE) per lo specifico lavoratore
	 * 
	 * String infStoriche = (String)req.getAttribute("infStoriche");
	 * 
	 * String strChiaveTabella = (String)req.getAttribute("strChiaveTabella");
	 * 
	 * // ORA CREO LA QUERY DINAMICAMENTE (usando un "DynamicStatementUtils"): DynamicStatementUtils dsu = new
	 * DynamicStatementUtils();
	 * 
	 * dsu.addSelect( " DOC.PRGDOCUMENTO AS PRGDOCUMENTO," + " DOC.CDNLAVORATORE AS CDNLAVORATORE," + "
	 * DOC.PRGAZIENDA," + " DOC.PRGUNITA," + " DOC.CODCPI AS CODCPI," + " CPI.STRDESCRIZIONE AS CODCPI_DESC," + "
	 * CASE" + " WHEN DOC.NUMANNOPROT IS NOT NULL AND DOC.NUMPROTOCOLLO IS NOT NULL THEN DOC.NUMANNOPROT || '/' ||
	 * DOC.NUMPROTOCOLLO || ' '" + " WHEN DOC.NUMANNOPROT IS NOT NULL AND DOC.NUMPROTOCOLLO IS NULL THEN DOC.NUMANNOPROT
	 * || ' '" + " WHEN DOC.NUMANNOPROT IS NULL AND DOC.NUMPROTOCOLLO IS NOT NULL THEN DOC.NUMPROTOCOLLO || ' '" + "
	 * END" + " || TO_CHAR(DOC.DATPROTOCOLLO,'DD/MM/YYYY') || ' '" + " || CASE" + " WHEN UPPER(DOC.CODMONOIO) = 'I' THEN
	 * 'IN'" + " WHEN UPPER(DOC.CODMONOIO) = 'O' THEN 'OUT'" + " END" + " AS INFOPROTOCOLLO," + "
	 * PG_UTILS.TRUNC_DESC(DOC.STRDESCRIZIONE, 30, '..') AS STRDESCRIZIONEDOC," + " DOC.STRNOMEDOC AS STRNOMEDOC," + "
	 * DOC.NUMPROTOCOLLO AS NUMPROTOCOLLO," + " TO_CHAR(DOC.DATACQRIL, 'DD/MM/YYYY') AS DATACQRIL," + "
	 * DOC.CODTIPODOCUMENTO AS CODTIPODOCUMENTO," + " PG_UTILS.TRUNC_DESC(AMB.STRDESCRIZIONE, 20, '...') AS
	 * STRDESCRIZIONEAMBITO," + " PG_UTILS.TRUNC_DESC(TD.STRDESCRIZIONE, 20, '...') AS
	 * STRDESCRIZIONETIPODOC," + // NON USATO: " TF.STRDESCRIZIONE AS STRDESCRIZIONETIPOFILE," " DOC.CODTIPOFILE
	 * CODTIPOFILE, DOC.DATACQRIL AS DATSORT" );
	 * 
	 * dsu.addFrom("AM_DOCUMENTO DOC"); // NON USATO: dsu.addFrom("DE_DOC_TIPO_FILE TF"); dsu.addFrom("DE_DOC_TIPO TD");
	 * dsu.addFrom("DE_DOC_AMBITO AMB"); dsu.addFrom("DE_CPI CPI"); // NON USATO:
	 * dsu.addWhere("TF.CODTIPOFILE (+)= DOC.CODTIPOFILE"); dsu.addWhere("TD.CODTIPODOCUMENTO = DOC.CODTIPODOCUMENTO");
	 * dsu.addWhere("AMB.CODAMBITODOC = TD.CODAMBITODOC"); dsu.addWhere("CPI.CODCPI (+)= DOC.CODCPI");
	 * 
	 * if (StringUtils.isFilled(pagina)) {
	 * 
	 * dsu.addSelect("DOCCOL.STRCHIAVETABELLA AS STRCHIAVETABELLA");
	 * 
	 * dsu.addFrom("AM_DOCUMENTO_COLL DOCCOL, TS_COMPONENTE COM");
	 * 
	 * dsu.addWhere("DOCCOL.PRGDOCUMENTO = DOC.PRGDOCUMENTO"); dsu.addWhere("COM.CDNCOMPONENTE = DOCCOL.CDNCOMPONENTE");
	 * dsu.addWhereIfFilledStrUpper("COM.STRPAGE", pagina); dsu.addWhereIfFilledStrUpper("DOCCOL.STRCHIAVETABELLA",
	 * strChiaveTabella); }
	 * 
	 * dsu.addWhereIfFilledNum("DOC.CDNLAVORATORE", cdnLavoratore); dsu.addWhereIfFilledNum("DOC.PRGAZIENDA",
	 * prgAzienda); dsu.addWhereIfFilledNum("DOC.PRGUNITA", prgUnita);
	 * 
	 * dsu.addWhereIfFilledNum("DOC.NUMANNOPROT", strAnnoProtocollo); dsu.addWhereIfFilledStr("DOC.NUMPROTOCOLLO",
	 * numProtocollo); if (StringUtils.isFilled(strDataProtocollo)) {
	 * dsu.addWhere("TRUNC(DOC.DATPROTOCOLLO) = TO_DATE('" + strDataProtocollo + "','DD/MM/YYYY')"); }
	 * dsu.addWhereIfFilledStr("DOC.CODMONOIO", docIO); dsu.addWhereIfFilledStr("TD.CODAMBITODOC", ambito);
	 * 
	 * if (tipoDocumento != null && tipoDocumento.size() > 0) { StringBuffer sqlTemp = new
	 * StringBuffer("TD.CODTIPODOCUMENTO IN ("); Object[] tipidoc = tipoDocumento.toArray(); for (int i = 0; i <
	 * tipidoc.length; i++) { sqlTemp.append('\''); String tipoDocStr =
	 * StringUtils.formatValue4Sql(tipidoc[i].toString()); sqlTemp.append(tipoDocStr); sqlTemp.append("',"); }
	 * sqlTemp.setCharAt(sqlTemp.length()-1, ')'); // rimpiazzo ultima virgola con tonda chiusa
	 * dsu.addWhere(sqlTemp.toString()); }
	 * 
	 * dsu.addWhereIfFilledStr("DOC.FLGDOCAMM", docAmministrativo); dsu.addWhereIfFilledStr("DOC.FLGDOCIDENTIFP",
	 * docIdentificazione); dsu.addWhereIfFilledStr("DOC.FLGAUTOCERTIFICAZIONE", autoCertificazione);
	 * 
	 * if (StringUtils.isFilled(datInizio)) { dsu.addWhere("TRUNC(DOC.DATINIZIO) =
	 * TO_DATE('" + datInizio + "','DD/MM/YYYY')"); }
	 * 
	 * if (StringUtils.isFilled(datFine)) { dsu.addWhere("TRUNC(DOC.DATFINE) =
	 * TO_DATE('" + datFine + "','DD/MM/YYYY')"); } else { if ((infStoriche != null) &&
	 * infStoriche.equalsIgnoreCase("true")) { // Se "data fine
	 * validità" non è valorizzata, ma sto cercando info "storiche", // considero solo quelli con data fine valorizzata
	 * e minore della data odierna. // ORIGINALE: dsu.addWhere("NVL(DOC.DATFINE, SYSDATE) <
	 * SYSDATE"); // OTTIMIZZATA (con indici): dsu.addWhere("( DECODE(DOC.DATFINE, NULL, 'S','N') = 'N' AND DOC.DATFINE
	 * < TRUNC(SYSDATE) )"); } else { // Se "data fine validità" non è valorizzata, // considero solo quelli
	 * "non scaduti", cioè con data fine nulla o maggiore o uguale della data odierna. // ORIGINALE:
	 * dsu.addWhere("NVL(DOC.DATFINE, SYSDATE) >= SYSDATE"); // OTTIMIZZATA (con indici): dsu.addWhere("(
	 * DECODE(DOC.DATFINE, NULL, 'S','N') = 'S' OR DOC.DATFINE >= TRUNC(SYSDATE) )"); } }
	 * 
	 * dsu.addWhereIfFilledStr("DOC.STRNUMDOC", strNumDoc);
	 * 
	 * if ((tipoRicercaEnteRil!=null) && tipoRicercaEnteRil.equalsIgnoreCase("esatta")) {
	 * dsu.addWhereIfFilledStrUpper("DOC.STRENTERILASCIO", strEnteRilascio); } else {
	 * dsu.addWhereIfFilledStrLikeUpper("DOC.STRENTERILASCIO", strEnteRilascio, DynamicStatementUtils.DO_LIKE_CONTIENE);
	 * }
	 * 
	 * dsu.addWhereIfFilledStr("DOC.CODCPI", codCpi);
	 * 
	 * dsu.addWhereIfFilledStrLikeUpper("DOC.STRDESCRIZIONE", strDescrizione, DynamicStatementUtils.DO_LIKE_CONTIENE); /
	 * * ORA HO LA QUERY DI BASE. MA DEVO PERFEZIONARLA AFFINCHE' UNA RIGA CHE FA RIFERIMENTO SIA A UN LAVORATORE SIA A
	 * UN'AZIENDA VENGA PRESA DUE VOLTE (una volta per il LAV e una per l'AZI)! Ecco i casi possibili: 1. xxx / / 2. xxx
	 * L1 / 3. xxx / A2 4. xxx L3 A3 DEVE DIVENTARE: (documento) 1. xxx / / 2. xxx L1 / 3. xxx / A2 4. xxx L3 /
	 * \__record raddoppiato! 5. xxx / A3 / Faccio la "UNION" delle 3 "Mappe di Karnaugh": a) i due null "/" in
	 * orizzontale b) la coppia L1+L3 c) la coppia A2+A3 Quindi farà: SELECT ... AND DOC.CDNLAVORATORE IS NULL AND
	 * DOC.PRGAZIENDA IS NULL UNION ALL SELECT ... FROM ..., AN_LAVORATORE LAV ... AND LAV.CDNLAVORATORE =
	 * DOC.CDNLAVORATORE UNION ALL SELECT ... FROM ..., AN_UNITA_AZIENDA AZI ... AND AZI.PRGAZIENDA = DOC.PRGAZIENDA AND
	 * AZI.PRGUNITA = DOC.PRGUNITA Ossia, prendo la QUERY DI BASE e ne creo tre versioni "modificate" che combino in
	 * UNION per ottenere la query finale. /
	 * 
	 * DynamicStatementUtils dsuNessuno = dsu; DynamicStatementUtils dsuLavorat = (DynamicStatementUtils) dsu.clone();
	 * DynamicStatementUtils dsuAziende = (DynamicStatementUtils) dsu.clone(); // a) i due null "/" in orizzontale
	 * dsuNessuno.addSelect("NULL AS STRINFRIFAZILAV"); dsuNessuno.addSelect("NULL AS STRINFRIFAZILAV_CF"); // VERSIONE
	 * ORIGINALE: // dsuNessuno.addWhere ("DOC.PRGAZIENDA IS NULL"); // dsuNessuno.addWhere ("DOC.CDNLAVORATORE IS
	 * NULL"); // VERSIONE OTTIMIZZATA (CON USO DI INDICI): dsuNessuno.addWhere ("NVL(DOC.PRGAZIENDA, -9999) = -9999");
	 * dsuNessuno.addWhere ("NVL(DOC.CDNLAVORATORE, -9999) = -9999"); // b) la coppia L1+L3
	 * dsuLavorat.addSelect("LAV.STRCOGNOME||' '||LAV.STRNOME AS STRINFRIFAZILAV");
	 * dsuLavorat.addSelect("LAV.STRCODICEFISCALE AS STRINFRIFAZILAV_CF");
	 * 
	 * dsuLavorat.addFrom ("AN_LAVORATORE LAV");
	 * 
	 * dsuLavorat.addWhere ("LAV.CDNLAVORATORE = DOC.CDNLAVORATORE"); // c) la coppia A2+A3
	 * dsuAziende.addSelect("PG_UTILS.TRUNC_DESC(AZI.STRRAGIONESOCIALE, 30, '..') AS
	 * STRINFRIFAZILAV"); dsuAziende.addSelect("AZI.STRCODICEFISCALE AS STRINFRIFAZILAV_CF");
	 * 
	 * dsuAziende.addFrom ("AN_AZIENDA AZI");
	 * 
	 * dsuAziende.addWhere ("AZI.PRGAZIENDA = DOC.PRGAZIENDA"); // Compongo le tre query con "UNION" e faccio l'ORDERBY.
	 * String query = dsuNessuno.getStatement() + " UNION ALL " + dsuLavorat.getStatement() + " UNION ALL
	 * " + dsuAziende.getStatement() + / * ordinamento * / " ORDER BY STRDESCRIZIONEAMBITO, DATSORT DESC,
	 * INFOPROTOCOLLO";
	 * 
	 * _logger.debug( className+".getStatement() FINE, con query=" + query);
	 * 
	 * 
	 * return query; }
	 */

}