<%@ page contentType="text/html;charset=utf-8"%>
 
<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.module.voucher.*, it.eng.sil.util.*,
                it.eng.sil.security.*,
                java.math.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<% 
  boolean isNuovo = false;
  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page);
  //NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, _page);
 
  boolean canModify = false;
   String readonly = "";
  
  if (!filter.canView()) {
  	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  }
  else {
	canModify = attributi.containsButton("AggiornaModModalita");
 	readonly = String.valueOf(!canModify);
  }
  
  String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
  String prgModelloVoucher = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGMODVOUCHER");
  String strPrgModalita = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGMODMODALITA");
  BigDecimal prgModalita = null;
  if(StringUtils.isEmptyNoBlank(strPrgModalita) && serviceResponse.containsAttribute("M_InsertUpdateModalitaTda.PRGMODMODALITA")){
	  prgModalita = (BigDecimal) serviceResponse.getAttribute("M_InsertUpdateModalitaTda.PRGMODMODALITA");
	  strPrgModalita = prgModalita.toString();
  }
  
  String dtmIns = null; 
  String dtmMod = null;
  BigDecimal cdnUtIns = null; 
  BigDecimal cdnUtMod = null;
  BigDecimal numKlo = null; 
  String codModalita ="";
  String strTipoDurata   = "";
  String strDurataMin     = "";
  String strDurataMax  = "";
  String strRimborso   = "";
  String strValUnit   	  ="";
  String strValTot    = "";
  String strPercentuale ="";
  String modelloAttivo = "";
  String queryString = null;
  Testata operatoreInfo = null;
  
  SourceBean rowModelloTda = (SourceBean)serviceResponse.getAttribute("M_DettaglioMod_ModalitaTDA.ROWS.ROW");
  
  if (rowModelloTda != null) {
 
	  numKlo =  (BigDecimal) rowModelloTda.getAttribute("NUMKLOCK");
	  codModalita = StringUtils.getAttributeStrNotNull(rowModelloTda,"strModalita");
	  strTipoDurata   = StringUtils.getAttributeStrNotNull(rowModelloTda,"strTipoDurata");
      cdnUtIns = (BigDecimal) rowModelloTda.getAttribute("CDNUTINS");
      cdnUtMod = (BigDecimal) rowModelloTda.getAttribute("CDNUTMOD");
      dtmIns = (String) rowModelloTda.getAttribute("DTMINS");
      dtmMod = (String) rowModelloTda.getAttribute("DTMMOD");
	  strDurataMin     = StringUtils.getAttributeStrNotNull(rowModelloTda,"strDurataMin");
	  strDurataMax  = StringUtils.getAttributeStrNotNull(rowModelloTda,"strDurataMax");
	  strPercentuale   = StringUtils.getAttributeStrNotNull(rowModelloTda,"strPercentuale");
	  strRimborso   = StringUtils.getAttributeStrNotNull(rowModelloTda,"strRimborso");
	  strValUnit   	  = StringUtils.getAttributeStrNotNull(rowModelloTda,"strValUnit");
	  strValTot    = StringUtils.getAttributeStrNotNull(rowModelloTda,"strValTot");
	  modelloAttivo = StringUtils.getAttributeStrNotNull(rowModelloTda,"flgAttivo");
	  if(modelloAttivo.equalsIgnoreCase("S")){
		  canModify = false;
		  readonly = String.valueOf(!canModify);
	  }
  }
  
  operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
  
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>
<html>
<head>
  <title>Dettaglio Modalità Modello TDA</title>
  <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
    <link rel="stylesheet" type="text/css" href="../../css/jqueryui/jquery.selectBoxIt.css">
  <link rel="stylesheet" type="text/css" href="../../css/jqueryui/jqueryui-sil.css">
  <%@ include file="../global/fieldChanged.inc" %>
  
  <af:linkScript path="../../js/"/>
  <script src="../../js/jqueryui/jquery-1.12.4.min.js"></script>
  <script src="../../js/jqueryui/jquery-ui.min.js"></script>
  <script src="../../js/jqueryui/jquery.selectBoxIt.min.js"></script>	
  <script type="text/javascript">
    $(function() {
    	$("[name='codModalita']").selectBoxIt({
            theme: "default",
            defaultText: "Seleziona una modalità...",
            autoWidth: false
        });
       
    });
    $(function() {
    	$(":reset").click(function() {
    		$("select").data("selectBox-selectBoxIt").selectOption("<%=codModalita%>");
	    });
    });
    </script>
   <script type="text/Javascript">
  
  function reloadModalita() {	
	  if(controllaModalitaObb() && checkTipoDurata() && checkDurataMin() && checkDurataMax() && checkPercentuale() && controlliValoriEuro()){
	 		var url = "AdapterHTTP?PAGE=DettaglioModalitaTdaPage";
	 		url += "&CDNFUNZIONE=<%=_funzione%>";
			url += "&PRGMODVOUCHER=<%=prgModelloVoucher%>";
			url += "&PRGMODMODALITA=<%=strPrgModalita%>";
			url += "&OPERAZIONEMODALITA=AGGIORNA";
			url += "&NUMKLO=<%=numKlo%>";
			url += "&codModalita="+ document.frm1.codModalita.value;
			url += "&strTipoDurata="+ document.frm1.strTipoDurata.value;
			url += "&strDurataMin="+ document.frm1.strDurataMin.value;
			url += "&strDurataMax="+ document.frm1.strDurataMax.value;
			url += "&strPercentuale="+ document.frm1.strPercentuale.value;
			url += "&strRimborso="+ document.frm1.strRimborso.value;
			url += "&strValUnit="+ document.frm1.strValUnit.value;
			url += "&strValTot="+ document.frm1.strValTot.value;
	 		url += "&confermaModificheModello=confermaModificheModello";
 			window.location.replace(url);
			//window.close();		
	  }
	}
  function reloadModalitaLista() {	
 
	 		var url = "AdapterHTTP?PAGE=LinguettaModalitaTdaPage";
	 		url += "&CDNFUNZIONE=<%=_funzione%>";
			url += "&PRGMODVOUCHER=<%=prgModelloVoucher%>";
			url += "&PRGMODMODALITA=<%=strPrgModalita%>";
			 
	 		window.opener.location.replace(url);
			window.close();				
	  
	}
  var prevValue = "<%=strRimborso%>";
  </script>
 <script language="Javascript" src="../../js/modellitda/modalita.js"></script>
</head>
<body  class="gestione" onload="rinfresca();">
 
 
<p class="titolo">Dettaglio Modalità Modello TDA</p>

<%@page import="it.eng.sil.module.voucher.Properties"%>
<af:form name="frm1" action="AdapterHTTP" method="POST">
	<input type="hidden" name="PAGE" value="LinguettaModalitaTdaPage">
	<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
	<input type="hidden" name="PRGMODVOUCHER" value="<%=prgModelloVoucher%>"/>
 	<input type="hidden" name="PRGMODMODALITA" value="<%=strPrgModalita%>"/>
 	<input type="hidden" name="NUMKLO" value="<%=numKlo%>"/>
 	<input type="hidden" name="OPERAZIONEMODALITA" value="AGGIORNA"/>
 	
<%out.print(htmlStreamTop);%>
<af:showErrors />
<af:showMessages prefix="M_InsertUpdateModalitaTda"/>
<p align="center">
	<table class="main">
<%@ include file="modalita.inc" %>
</table>
	
	<br>
    <table>
 
<% if (canModify) { %>
		<tr>
			<td>
				<input type="button" onclick="reloadModalita();" class="pulsanti" name="confermaModificheModello" value="Aggiorna" />
			</td>
			<td>
				<input type="reset" class="pulsanti" value="Annulla" />
			</td>
		</tr>
	
	<% } %>
		<tr>
			<td colspan="2" align="center">
				<input class="pulsante" type = "button" name="torna" value="Chiudi" onClick="reloadModalitaLista();"/>
			</td>
		</tr>
 		
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
	 
</table>
<%= htmlStreamBottom %>

<center><table><tr><td align="center">
<% operatoreInfo.showHTML(out); %>
</td></tr></table></center>

</af:form>
</body>
</html>