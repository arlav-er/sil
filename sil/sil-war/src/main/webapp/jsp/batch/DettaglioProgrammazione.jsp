<!-- @author: Giovanni Landi -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                com.engiweb.framework.security.*,java.math.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.User,
                it.eng.sil.security.PageAttribs, it.eng.sil.module.batch.Constants,
                it.eng.sil.util.*,
                it.eng.afExt.utils.*"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
	String _page = (String) serviceRequest.getAttribute("PAGE");
	String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
	String _pageProvenienza = (String) serviceRequest.getAttribute("PAGEPROVENIENZA");
 	String codTipoBatch = "";
 	String codCpiRiferimento ="";
 	String flgAttivo = "";
 	BigDecimal numKloProgrammazione = null;
 	BigDecimal prgProgrammazione = null;
 	String codStatoAppuntamento = "2";
 	String codMotivoContatto = "";
 	String codServizio = "";
 	String strTipoSms = "";
 	String codMotivoFineAttoDid = "";
 	String flgIscrCM = "";
 	BigDecimal prgTipoEvidenza = null;
 	BigDecimal prgMotContatto = null;
 	BigDecimal ggUltimoContatto = null;
 	BigDecimal ggRangeBatch = null;
 	BigDecimal ggSchedulazione = null;
 	BigDecimal prgAzioneBatch = null;
 	BigDecimal ggRangeDataFineDid = null;
 	String strGGUltimoContatto = "";
 	String strGGRangeBatch = "";
 	String strGGSchedulazione = "";
 	String strPrgTipoEvidenza = "";
 	String strPrgAzione = "";
 	String strGGRangeDataFineDid = "";
 	String dataInizioVal = "";
 	String dataFineVal = "";
 	String codEsitoAzione = "";
 	String txtNoteElaborazione = "";
 	PageAttribs attributi = new PageAttribs(user, _pageProvenienza);
	boolean canModify = attributi.containsButton("AGGIORNA");
	boolean canInsert = attributi.containsButton("INSERISCI");
	String btnSalva = "Inserisci";
	String btnAnnulla = "Chiudi senza inserire";
	String displayApp = "display:none";
	String displayAzione = "display:none";
	String displayDid = "display:none";
	String displayPerditaDisocc = "display:none";
	String titlePagina = "Nuova Programmazione";
	String dataOdierna = DateUtils.getNow();
	String dataProssimaSchedulazione = "";
	String prograttivavalidita = "";
	String datUltimaElab = "";
	boolean isNuovaProgrammazione = true;
	
	boolean canOperation = canInsert;
	boolean noEditTipoBatch = false;
	
	String cdnUtins = "";
	String cdnUtmod = "";
	String dtmins = "";
	String dtmmod = "";
	Testata operatoreInfo = null;
	
	Vector listaTipiSms = serviceResponse.getAttributeAsVector("MTipiSmsProgrammazione.ROWS.ROW");
	Vector listaTipiEvidenza = serviceResponse.getAttributeAsVector("MTipiEvidenzeProgrammazione.ROWS.ROW");
	Vector listaCpi = serviceResponse.getAttributeAsVector("M_GetCpiPoloProvincialeAll.ROWS.ROW");
	
	if (serviceResponse.containsAttribute("M_GetDettaglioProgrammazione.ROWS.ROW")) {
		SourceBean rowProgrammazione = (SourceBean)serviceResponse.getAttribute("M_GetDettaglioProgrammazione.ROWS.ROW");
		canOperation = canModify;
		btnSalva = "Aggiorna";
		btnAnnulla = "Chiudi senza aggiornare";
		titlePagina = "Consulta Programmazione";
		noEditTipoBatch = true;
		isNuovaProgrammazione = false;
		datUltimaElab = StringUtils.getAttributeStrNotNull(rowProgrammazione, "datUltimaElab");
		prgProgrammazione = (BigDecimal) rowProgrammazione.getAttribute("prgProgrammaBatch");
		ggUltimoContatto = (BigDecimal) rowProgrammazione.getAttribute("numggprecavviso");
		if (ggUltimoContatto != null) {
			strGGUltimoContatto = ggUltimoContatto.toString();	
		}
		prgMotContatto = (BigDecimal) rowProgrammazione.getAttribute("prgmotcontatto");
		if (prgMotContatto != null) {
			codMotivoContatto = prgMotContatto.toString();	
		}
		ggRangeBatch = (BigDecimal) rowProgrammazione.getAttribute("numggperiododate");
		if (ggRangeBatch != null) {
			strGGRangeBatch = ggRangeBatch.toString();	
		}
		ggSchedulazione = (BigDecimal) rowProgrammazione.getAttribute("numggperiodoprog");
		if (ggSchedulazione != null) {
			strGGSchedulazione = ggSchedulazione.toString();
			if (datUltimaElab.equals("")) {
				dataProssimaSchedulazione = DateUtils.giornoSuccessivo(dataOdierna);
			}
			else {
				dataProssimaSchedulazione = DateUtils.aggiungiNumeroGiorni(datUltimaElab, ggSchedulazione.intValue());	
			}
		}
		ggRangeDataFineDid = (BigDecimal) rowProgrammazione.getAttribute("numggrangefinedid");
		if (ggRangeDataFineDid != null) {
			strGGRangeDataFineDid = ggRangeDataFineDid.toString();
		}
		prgAzioneBatch = (BigDecimal) rowProgrammazione.getAttribute("prgazioni");
		if (prgAzioneBatch != null) {
			strPrgAzione = prgAzioneBatch.toString();	
		}
		prgTipoEvidenza = (BigDecimal) rowProgrammazione.getAttribute("prgtipoevidenza");
		if (prgTipoEvidenza != null) {
			strPrgTipoEvidenza = prgTipoEvidenza.toString();	
		}
		numKloProgrammazione = (BigDecimal) rowProgrammazione.getAttribute("numkloprogrammabatch");
		codTipoBatch = StringUtils.getAttributeStrNotNull(rowProgrammazione, "codTipoBatch");
		flgAttivo = StringUtils.getAttributeStrNotNull(rowProgrammazione, "flgAttivo");
		codStatoAppuntamento = StringUtils.getAttributeStrNotNull(rowProgrammazione, "codstatoappuntamento");
		codServizio = StringUtils.getAttributeStrNotNull(rowProgrammazione, "codServizio");
		dataInizioVal = StringUtils.getAttributeStrNotNull(rowProgrammazione, "datinizioval");
	 	dataFineVal = StringUtils.getAttributeStrNotNull(rowProgrammazione, "datfineval");
	 	prograttivavalidita = StringUtils.getAttributeStrNotNull(rowProgrammazione, "prograttivavalidita");
	 	codEsitoAzione = StringUtils.getAttributeStrNotNull(rowProgrammazione, "codesito");
	 	strTipoSms = StringUtils.getAttributeStrNotNull(rowProgrammazione, "codtiposms");
	 	cdnUtins = StringUtils.getAttributeStrNotNull(rowProgrammazione, "cdnutins");
	 	cdnUtmod = StringUtils.getAttributeStrNotNull(rowProgrammazione, "cdnutmod");
	 	dtmins = StringUtils.getAttributeStrNotNull(rowProgrammazione, "dtmins");
	 	dtmmod = StringUtils.getAttributeStrNotNull(rowProgrammazione, "dtmmod");
	 	txtNoteElaborazione = StringUtils.getAttributeStrNotNull(rowProgrammazione, "strnote");
	 	codMotivoFineAttoDid = StringUtils.getAttributeStrNotNull(rowProgrammazione, "codMotivoFineAttoDid");
	 	flgIscrCM = StringUtils.getAttributeStrNotNull(rowProgrammazione, "flgCmIscr");
	 	codCpiRiferimento = StringUtils.getAttributeStrNotNull(rowProgrammazione, "codCpi");
	 	
	 	operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
	 	
	 	Object chiaveObjBatch = Constants.mapBatch.get(codTipoBatch);
	 	Integer chiaveBatch = new Integer(chiaveObjBatch.toString());
	 	switch (chiaveBatch.intValue()) {
		 	case Constants.APPUNTAMENTI: {
		 		displayApp = "";
		 		break;
		 	}
		 	case Constants.AZIONI_PROGRAMMATE: {
		 		displayAzione = "";
		 		break;	
		 	}
		 	case Constants.CONFERMA_DID: {
		 		displayDid = "";
		 		break;	
		 	}
		 	case Constants.PERDITA_DISOCC: {
		 		displayPerditaDisocc = "";
		 		break;
		 	}
	 	}
	}
	
  	String htmlStreamTop = StyleUtils.roundTopTable(canOperation);
  	String htmlStreamBottom = StyleUtils.roundBottomTable(canOperation);
%>
<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Dettaglio Programmazione</title>
  <script type="text/javascript">
  
	function caricaTipoSmsEvidenza() {
  		var contatoreRigaSmsApp = 0;
  		var contatoreRigaEvidenzaApp = 0;
  		var contatoreRigaSmsAzione = 0;
  		var contatoreRigaSmsPerdDisocc = 0;
  		var contatoreRigaEvidenzaAzione = 0;
  		var contatoreRigaSmsDid = 0;
  		var contatoreRigaEvidenzaDid = 0;
  		var contatoreRigaEvidenzaPerdDisocc = 0;
    	<%for (int i = 0; i < listaTipiSms.size(); i++) {
    		SourceBean rowSms = (SourceBean) listaTipiSms.get(i);
    		String codTipoBatchCurr = Utils.notNull(rowSms.getAttribute("CODTIPOBATCH"));
    		String codiceCurr = Utils.notNull(rowSms.getAttribute("CODICE"));
    		String descCurr = Utils.notNull(rowSms.getAttribute("DESCRIZIONE"));
			if (codTipoBatchCurr.equalsIgnoreCase("APPUNSMS")) {%>
				document.frmProgrammazione.smsApp.options[contatoreRigaSmsApp] = new Option('<%=descCurr%>', '<%=codiceCurr%>', false, false);
				contatoreRigaSmsApp = contatoreRigaSmsApp + 1;
			<%}
			else {
				if (codTipoBatchCurr.equalsIgnoreCase("AZIONSMS")) {%>
					document.frmProgrammazione.smsAzione.options[contatoreRigaSmsAzione] = new Option('<%=descCurr%>', '<%=codiceCurr%>', false, false);
					contatoreRigaSmsAzione = contatoreRigaSmsAzione + 1;
				<%}
				else {
					if (codTipoBatchCurr.equalsIgnoreCase("DIDSMS")) {%>
						document.frmProgrammazione.smsDid.options[contatoreRigaSmsDid] = new Option('<%=descCurr%>', '<%=codiceCurr%>', false, false);
						contatoreRigaSmsDid = contatoreRigaSmsDid + 1;
					<%}
					else {
						if (codTipoBatchCurr.equalsIgnoreCase("PERDISMS")) {%>
							document.frmProgrammazione.smsPerdDisocc.options[contatoreRigaSmsPerdDisocc] = new Option('<%=descCurr%>', '<%=codiceCurr%>', false, false);
							contatoreRigaSmsPerdDisocc = contatoreRigaSmsPerdDisocc + 1;
						<%}
					}
				}
			}
    	}
    	for (int i = 0; i < listaTipiEvidenza.size(); i++) {
    		SourceBean rowEvidenza = (SourceBean) listaTipiEvidenza.get(i);
    		String codTipoBatchCurr = Utils.notNull(rowEvidenza.getAttribute("CODTIPOBATCH"));
    		String codiceCurr = Utils.notNull(rowEvidenza.getAttribute("CODICE"));
    		String descCurr = Utils.notNull(rowEvidenza.getAttribute("DESCRIZIONE"));
			if (codTipoBatchCurr.equalsIgnoreCase("APPUNSMS")) {%>
				document.frmProgrammazione.evidenzaApp.options[contatoreRigaEvidenzaApp] = new Option('<%=descCurr%>', '<%=codiceCurr%>', false, false);
				contatoreRigaEvidenzaApp = contatoreRigaEvidenzaApp + 1;
			<%}
			else {
				if (codTipoBatchCurr.equalsIgnoreCase("AZIONSMS")) {%>
					document.frmProgrammazione.evidenzaAzione.options[contatoreRigaEvidenzaAzione] = new Option('<%=descCurr%>', '<%=codiceCurr%>', false, false);
					contatoreRigaEvidenzaAzione = contatoreRigaEvidenzaAzione + 1;
				<%}
				else {
					if (codTipoBatchCurr.equalsIgnoreCase("DIDSMS")) {%>
						document.frmProgrammazione.evidenzaDid.options[contatoreRigaEvidenzaDid] = new Option('<%=descCurr%>', '<%=codiceCurr%>', false, false);
						contatoreRigaEvidenzaDid = contatoreRigaEvidenzaDid + 1;
					<%}
					else {
						if (codTipoBatchCurr.equalsIgnoreCase("PERDISMS")) {%>
							document.frmProgrammazione.evidenzaPerdDisocc.options[contatoreRigaEvidenzaPerdDisocc] = new Option('<%=descCurr%>', '<%=codiceCurr%>', false, false);
							contatoreRigaEvidenzaPerdDisocc = contatoreRigaEvidenzaPerdDisocc + 1;
						<%}
					}
				}
			}
    	}%>
  	}
  </script>
  
</head>

<body class="gestione" onload="caricaTipoSmsEvidenza();">
<p class="titolo"><%=titlePagina%></p>
<%@ include file="dettaglio_programmazione.inc" %>
</body>
</html>