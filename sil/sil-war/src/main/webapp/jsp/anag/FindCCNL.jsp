<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                 java.lang.*,
                 java.text.*,
                 java.util.*, 
                 it.eng.afExt.utils.StringUtils,
                 it.eng.sil.security.*"%>
            
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<% 
  String moduleName="";
  boolean isRicerca= (!(boolean)serviceRequest.containsAttribute("apprendistato"));
  
  //Giovanni D'Auria 24/02/2005
  
  String numMesiApprendistato= "";
  
  if((boolean)serviceResponse.containsAttribute("M_MovGetNumDurataApprendist.rows.row.NUMMESIAPPRENDISTATO")){
  	numMesiApprendistato = serviceResponse.getAttribute("M_MovGetNumDurataApprendist.ROWS.ROW.NUMMESIAPPRENDISTATO").toString();  	
  }
	
  //fine
  
  
  
  boolean isCodccnl=(boolean) serviceRequest.containsAttribute("codccnl");

  if (isCodccnl) {
    moduleName="M_RicercaCodCCNL";
  } else {
    moduleName="M_RicercaDesCCNL";
  }
%>



<html>
<head>
<title>Ricerca CCNL</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/" />
<SCRIPT language="Javascript" type="text/javascript">

</SCRIPT>
<script language="Javascript">
     <% 

  
      %>
</script>
<SCRIPT TYPE="text/javascript">
<!--
function AggiornaForm (Codccnl, Strdescrizione, Descpadre, numMesiAppr) {

//	if (isScaduto(Strdescrizione)) {
//		alert("Non è possibile inserire un Tipo CCNL scaduto");
//		window.opener.document.Frm1.codCCNL.value = "";
//	} else {
	  if(window.opener.document.Frm1.strCCNLPadre == undefined ){
	 	  var mesiApprendistato = document.getElementById("mesiApprend").value;
		  var mesi = "";
		  if(mesiApprendistato == "" && mesi == undefined){
		  	mesi = numMesiAppr;
		  }else{
		  	mesi = mesiApprendistato;
		  }
		  if(<%=!isRicerca%>)
			  window.opener.document.Frm1.NUMMESIAPPRENDISTATO.value=mesi;	  
	  }else{
	  	window.opener.document.Frm1.strCCNLPadre.value=Descpadre;
	  }
	  window.opener.document.Frm1.codCCNL.value = Codccnl;
	  window.opener.document.Frm1.codCCNLHid.value=Codccnl;
	  window.opener.document.Frm1.strCCNL.value = Strdescrizione.replace('^', '\'');	  
	  window.close();
	  
//	}
}


function tornaAllaRicerca() {
  window.location="AdapterHTTP?PAGE=RicercaCCNLAvanzataPage";
}


function isScaduto(str) {
	if (opener.flagRicercaPage != "S") { // Se NON provengo da una page di ricerca ammetto solo quelli non scaduti.
		if (str.substring(str.length-9) == "(scaduto)") {
			return true;
		}
	}
	return false;
}

-->
</SCRIPT>
</head>
<body class="gestione">
<%
	Integer currentPage = (Integer) serviceResponse.getAttribute(moduleName+".ROWS.CURRENT_PAGE");
	Integer numPages = (Integer) serviceResponse.getAttribute(moduleName+".ROWS.NUM_PAGES");
    Vector rows = serviceResponse.getAttributeAsVector(moduleName+".ROWS.ROW");
    if (rows.size()==1 && currentPage.intValue()==1 && numPages.intValue()==1) {           
		SourceBean row = (SourceBean)rows.get(0);    
        String codccnl = (String)row.getAttribute("CODCCNL");
        String strdescrizione = (String)StringUtils.getAttributeStrNotNull(row, "STRDESCRIZIONE");
        strdescrizione=strdescrizione.replace('\'', '^');    
        String descpadre = (String) StringUtils.getAttributeStrNotNull(row, "DESCPADRE");            
        descpadre=descpadre.replace('\'', '^');    
        StringBuffer jsCommand = new StringBuffer();
        jsCommand.append("AggiornaForm('");
        jsCommand.append(codccnl);
        jsCommand.append("','");
        jsCommand.append(strdescrizione);        
        jsCommand.append("','");
        jsCommand.append(descpadre);                
        jsCommand.append("','");
        jsCommand.append(numMesiApprendistato); 
        jsCommand.append("');");
                 
%>
	<input type="hidden" name="mesi" value="<%=numMesiApprendistato%>" id="mesiApprend" />
	<script><%=jsCommand.toString()%></script>
	
		
	
<%  } else {%>
<af:list moduleName="<%= moduleName%>" jsSelect="AggiornaForm" />
<br/>
<table width="100%">
<tr>
<td align="center">
	<input type="button" class="pulsante" name="torna" value="Torna alla ricerca" onClick="javascript:tornaAllaRicerca();"/>
	<input type="button" class="pulsante" name="chiudi" value="Chiudi" onClick="javascript:window.close();"/>
	<input type="hidden" name="mesi" value="<%=numMesiApprendistato%>" id="mesiApprend" />
</td>
<tr>
</table>
<%  } %>

</body>
</html>