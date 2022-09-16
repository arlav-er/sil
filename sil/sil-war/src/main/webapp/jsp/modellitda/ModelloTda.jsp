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
	String _page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	//NOTE: Attributi della pagina (pulsanti e link) 
	PageAttribs attributi = new PageAttribs(user, _page);
	boolean canModify = false;
	boolean canModifyTipoServizio = false;
	boolean canInsert = true;
	String canModifyFlag = "";
	String canModifyMax = "";
	String readonly = "";
	String isReading = "false";
	String nomeModulo = "M_GetAzioni";
	String nomeModuloObiettivi = "M_ComboObbiettivoMisuraYei";
	Object cdnUtCorrente = sessionContainer.getAttribute("_CDUT_");
	String cdnUtCorrenteStr = StringUtils.getStringValueNotNull(cdnUtCorrente);
  String titolo = "Nuovo Modello Titolo di Acquisto";
  String operazioneModello = "INSERISCI";
  String testoPulsante= "Salva";
  boolean provenienzaListaModelli = false; 
  boolean isNuovo = true;
  provenienzaListaModelli = serviceRequest.containsAttribute("PRGMODVOUCHER");	
  
  String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
  String prgModelloVoucher = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGMODVOUCHER");
  String isInserimento = StringUtils.getAttributeStrNotNull(serviceResponse, "M_INSERTUPDATEMODELLITDA.INS_OK");

  if(provenienzaListaModelli || StringUtils.isFilledNoBlank(isInserimento) ){
	  if(StringUtils.isFilledNoBlank(isInserimento) ){
		  Integer val = (Integer) serviceResponse.getAttribute("M_INSERTUPDATEMODELLITDA.PRGMODVOUCHER");
		  prgModelloVoucher = String.valueOf(val);
	  }
	  titolo = "Dettaglio Modello TDA";
	  isNuovo = false;
	  operazioneModello= "AGGIORNA";
	  testoPulsante = "Aggiorna";
	  isReading = "true";
	  nomeModulo = "M_AzioniModelloInserimento";
	  nomeModuloObiettivi = "ComboObbiettivoMisuraYei";
  } 
  if (!filter.canView()) {
	  	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	  	return;
}

		canInsert = attributi.containsButton("GestisciModello");
		canModify = canInsert;
		if(isNuovo)
			canModifyFlag = String.valueOf(canModify);
		else
  			canModifyFlag = String.valueOf(!canModify);
  canModifyMax =  String.valueOf(!canModify);
  readonly = String.valueOf(!canModify);

  Testata operatoreInfo = null;
  BigDecimal numKlo = null;
  String codServizio = null; 
  String strPrgAzioneRagg ="";
  BigDecimal prgAzioneRagg = null;
  BigDecimal prgAzioni = null;
  String strPrgAzioni = "";
  String strTipoServizio ="";
  String strDescrizioneTipoServizio="";
  String codModalita = "";
  BigDecimal giorniAttivazione  = null;
  String strGiorniErogazione   = "";
  String strGiorniAttivazione  = "";
  BigDecimal giorniErogazione   = null;
  String modelloAttivo = "";
  String tdaCM = "";
  if(isNuovo){
	  modelloAttivo = "N";
	  if(StringUtils.isFilledNoBlank(StringUtils.getAttributeStrNotNull(serviceRequest,"prgazioneragg"))){
		  strPrgAzioneRagg = StringUtils.getAttributeStrNotNull(serviceRequest,"prgazioneragg");
	  }
	  codServizio = StringUtils.getAttributeStrNotNull(serviceRequest,"codServizioSel");
  }
  String strValoreMax  = "";
  BigDecimal valoreMax = null;
  String queryString = null;
  String cdnUtins="";
  String dtmins="";
  String cdnUtmod="";
  String dtmmod="";
  
  String isTipoServizioChanged = StringUtils.getAttributeStrNotNull(serviceResponse, "M_AGGIORNATIPOSERVIZIO.avvisaCampioTipologia");
  boolean avvisaUtente = false;
  if(!isNuovo){
	  
	  if(StringUtils.isFilledNoBlank(isTipoServizioChanged)){
		  avvisaUtente = true;
	  }
	   
	  SourceBean rowModificabilitaTipoServizio = (SourceBean)serviceResponse.getAttribute("M_GetModificabilitaTipoServizio.ROWS.ROW");
	  if(rowModificabilitaTipoServizio !=null){
		  BigDecimal numModAttivi = (BigDecimal) rowModificabilitaTipoServizio.getAttribute("numModAttivi");
		  if(numModAttivi.intValue() > 0){
			  canModifyTipoServizio = false;
		  }else{
			  canModifyTipoServizio = true;
		  }
	  }
	  
	  SourceBean rowModelloTda = (SourceBean)serviceResponse.getAttribute("M_DettaglioModelloTDA.ROWS.ROW");
	  
	  if (rowModelloTda != null) {
		  
		  numKlo =  (BigDecimal) rowModelloTda.getAttribute("NUMKLOMODVOUCHER");
		  
		  if(!SourceBeanUtils.isNull(rowModelloTda, "prgAzioneRagg")){
			  prgAzioneRagg  = SourceBeanUtils.getAttrBigDecimal(rowModelloTda, "prgAzioneRagg");
			  strPrgAzioneRagg = prgAzioneRagg.toString();
		  }
		  if(!SourceBeanUtils.isNull(rowModelloTda, "codServizio")){
			  codServizio  = SourceBeanUtils.getAttrStrNotNull(rowModelloTda, "codServizio");
		  }
		  prgAzioni = (BigDecimal) rowModelloTda.getAttribute("prgAzioni");
		  strPrgAzioni = prgAzioni.toString();
		  strTipoServizio = SourceBeanUtils.getAttrStrNotNull(rowModelloTda,"codTipoServizio");
		  strDescrizioneTipoServizio = SourceBeanUtils.getAttrStrNotNull(rowModelloTda,"descrizioneTipoServizio");
		  if(StringUtils.isEmptyNoBlank(strTipoServizio)){
			  canModifyTipoServizio = true;
		  }
		  codModalita = SourceBeanUtils.getAttrStrNotNull(rowModelloTda,"CODSELEZMODALITA");
		  if(!SourceBeanUtils.isNull(rowModelloTda, "NUMNGMAXATTVCH")){
			  giorniAttivazione  = SourceBeanUtils.getAttrBigDecimal(rowModelloTda, "NUMNGMAXATTVCH");
			  strGiorniAttivazione = giorniAttivazione.toString();
		  }
		  if(!SourceBeanUtils.isNull(rowModelloTda, "NUMNGMAXEROGVCH")){
			  giorniErogazione   = SourceBeanUtils.getAttrBigDecimal(rowModelloTda, "NUMNGMAXEROGVCH");
			  strGiorniErogazione = giorniErogazione.toString();
		  }
		  modelloAttivo   = SourceBeanUtils.getAttrStrNotNull(rowModelloTda, "FLGATTIVO");
		  strValoreMax   =SourceBeanUtils.getAttrStrNotNull(rowModelloTda, "strValoreMax") ;
		  tdaCM   = SourceBeanUtils.getAttrStrNotNull(rowModelloTda, "FLGCM");
		  cdnUtins = SourceBeanUtils.getAttrStrNotNull(rowModelloTda, "cdnUtins");
		  dtmins = SourceBeanUtils.getAttrStrNotNull(rowModelloTda, "dtmins");
		  cdnUtmod = SourceBeanUtils.getAttrStrNotNull(rowModelloTda, "cdnUtmod");
		  dtmmod = SourceBeanUtils.getAttrStrNotNull(rowModelloTda, "dtmmod");
	  }
	  
	  if (modelloAttivo.equalsIgnoreCase("S")) {
		  canModify = false;
		  readonly = "true";
		  canModifyFlag = "false";
		  canModifyMax = "true";
	  }else if(modelloAttivo.equalsIgnoreCase("N")){
		  readonly = "false";
		  canModifyFlag = "false";
		  if(strTipoServizio.equalsIgnoreCase("SR")){
			  canModifyMax = "false";
		  }else{
			  canModifyMax = "true";
		  }
	  }
	  
	  operatoreInfo= new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
  }
  
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>
<html>
<head>
  <title><%= titolo %></title>
  <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  <%@ include file="../global/fieldChanged.inc" %>
  
  <af:linkScript path="../../js/"/>

  <script type="text/Javascript">

  function tornaLista() {
	if (isInSubmit()) return;
	<%
	// Recupero l'eventuale URL generato dalla LISTA precedente
	String token = "_TOKEN_" + "ListaModelliTdaPage";
	String goBackListUrl = (String) sessionContainer.getAttribute(token.toUpperCase());	
	%>
	setWindowLocation("AdapterHTTP?<%= StringUtils.formatValue4Javascript(goBackListUrl) %>");
  }
  
	function reloadDettaglioModelloCodServizio() {	
		fieldChanged();
		var codServizioSel = document.frmModello.codServizio.value;
		var url = "AdapterHTTP?PAGE=ModelloTdaMainPage";
 		url += "&CDNFUNZIONE=<%=_funzione%>";
 		url += "&codServizioSel=" + codServizioSel;
 	 
		window.location.replace(url);
	}

	function reloadDettaglioModello() {	
		fieldChanged();
		var codServizioSel = document.frmModello.codServizio.value;
		var prgAzioneRaggr = document.frmModello.obiettivoMisura.value;
		var url = "AdapterHTTP?PAGE=ModelloTdaMainPage";
 		url += "&CDNFUNZIONE=<%=_funzione%>";
 		url += "&codServizioSel=" + codServizioSel;
 		url += "&prgazioneragg=" + prgAzioneRaggr;
 	 
		window.location.replace(url);
	}

	function controllaCampi(){
		var f = document.frmModello;
	 
		<%if(canModify){%>
		trimObject("giorniAttivazione");
		trimObject("giorniErogazione");
		if (f.giorniAttivazione.value !== "" && f.giorniErogazione.value !== "") {
			if(parseInt(f.giorniAttivazione.value) > parseInt(f.giorniErogazione.value)){
				alert("'"+f.giorniAttivazione.title + "' non può essere maggiore di '"+  f.giorniErogazione.title+"'");
				f.giorniAttivazione.focus();
				return false;
			}
			return true;
		}
	<%}else if(!canModify &&  !(Boolean.valueOf(canModifyFlag).booleanValue())){%>
			f.OPERAZIONEMODELLO.value="DISATTIVA";
			return true;
	<%}%>
	 
		// Se sono qui va tutto bene
		return true;
	}
	
	function apriPopUpModificaTipoServizio() {	
		var isModificabile = <%= canModifyTipoServizio %>;
		if(!isModificabile){
			alert("La tipologia di servizio per questa azione non è modificabile perchè esiste già un modello attivo con la stessa azione");
			return;
		}else{
			if (isInSubmit()) return;
			
 			var url = "AdapterHTTP?PAGE=ModelloTdaTipoServizioPage";
	 		url += "&CDNFUNZIONE=<%=_funzione%>";
	 		url += "&PRGMODVOUCHER=<%=prgModelloVoucher%>";
	 		url += "&PRGAZIONI=<%=strPrgAzioni%>";
			
			var feat = "toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes," +
						"width=600,height=500,top=50,left=100"
		    opened = window.open(url, "TipologiaServizioModello", feat);
		    opened.focus();
			
			
		}
		
	}
	
	function modelloRinfresca(){
		rinfresca();
		var avvisaUtenteModifica = <%= avvisaUtente %>;
		if(avvisaUtenteModifica){
			alert(" Attenzione: tipologia di servizio modificata, il valore totale del modello sarà ricalcolato al prossimo aggiornamento dello stesso");
		}
	}
  </script>
</head>
<body  class="gestione" onload="modelloRinfresca();">
 <p class="titolo"><%= titolo %></p>
<p>
	<af:showErrors />
 	<af:showMessages prefix="M_InsertUpdateModelliTda"/>
 	<af:showMessages prefix="M_AggiornaTipoServizio"/>
</p>

<%
if(!isNuovo){
	Linguette _linguetta = new Linguette(user, new Integer(_funzione).intValue() , _page, new BigDecimal(prgModelloVoucher)); 
	_linguetta.setCodiceItem("PRGMODVOUCHER");
	_linguetta.show(out);
}
%>

<%@page import="it.eng.sil.module.voucher.Properties"%>
<af:form name="frmModello" action="AdapterHTTP" method="POST"  onSubmit="controllaCampi()">
	<input type="hidden" name="PAGE" value="ModelloTdaMainPage">
	<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
	<%if(!isNuovo){ %>
		<input type="hidden" name="PRGMODVOUCHER" value="<%=prgModelloVoucher%>"/>
	<%} %>
	<input type="hidden" name="NUMKLO" value="<%=numKlo%>"/>
	<input type="hidden" name="CDNUTINS" value="<%= cdnUtCorrenteStr %>" />
	<input type="hidden" name="OPERAZIONEMODELLO" value="<%=operazioneModello%>"/>
	<input type="hidden" name="data_cod" value=""/>

	
<%out.print(htmlStreamTop);%>

<p align="center">
	<table class="main">
<%@ include file="modello_titolo_acquisto.inc" %>
</table>
	
	<br>
    <table>

<% if (canModify || !(Boolean.valueOf(canModifyFlag).booleanValue())) { %>
		<tr>
			<td>
				<input type="submit" class="pulsanti" name="confermaModifiche" value="<%=testoPulsante %>" />
			</td>
			<td>
				<input type="reset" class="pulsanti" value="Annulla" />
			</td>
		</tr>
	
	<% } %>

 		<tr>
			<td colspan="2" align="center">
				<input class="pulsante" type = "button" name="torna" value="Torna alla lista" onClick="tornaLista();"/>
			</td>
		</tr>
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
	 
</table>
<%= htmlStreamBottom %>
<% 
if (!isNuovo){ 
%>
  <center>
  <table>
  <tr><td align="center">
<%
  operatoreInfo.showHTML(out);
%>
  </td></tr>
  </table></center>
<%
}
%>

</af:form>
</body>
</html>