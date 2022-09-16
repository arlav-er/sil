<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule, 
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.afExt.utils.StringUtils,                  
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  it.eng.sil.security.PageAttribs,
                  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%
 //String sessionId= requestContainer.getAttribute("HTTP_SESSION_ID").toString();
 String sessionId= request.getSession().getId(); 
 String codiceUtente= sessionContainer.getAttribute("_CDUT_").toString();
 String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
 String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
 PageAttribs attributi = new PageAttribs(user, "IdoPubbGiorReportPage");
 boolean canModify= attributi.containsButton("inserisci");
 boolean canDelete= attributi.containsButton("rimuovi");  
 cdnLavoratore="1";
 String queryString = null;
 String htmlStreamTop = StyleUtils.roundTopTable(false);
 String htmlStreamBottom = StyleUtils.roundBottomTable(false);
 String strListPage = "1";
 String strMessage = "LIST_PAGE";
 String strDatInizioSett = "";
 String strDatFineSettimana = "";
 String strCodGiornale = "";
 String retRicerca = (String) StringUtils.getAttributeStrNotNull(serviceRequest, "RETRICERCA");

 String tok = "_TOKEN_" + "IdoListaPubbGiorPage";
 String url = (String) sessionContainer.getAttribute(tok.toUpperCase());
 if (url != null && !url.equals("")){
	 int i = 0;
	 int j = 0;
	 i = url.indexOf("LIST_PAGE=") + "LIST_PAGE=".length();
	 j = url.indexOf("&",i);
	 if (j==-1) { j=url.length(); }
	 strListPage = url.substring(i,j);
	
	 i = url.indexOf("MESSAGE=") + "MESSAGE=".length();;
	 j = url.indexOf("&",i);
	 if (j==-1) { j=url.length(); }
	 strMessage = url.substring(i,j);
	
	 i = url.indexOf("DATINIZIOSETT=") + "DATINIZIOSETT=".length();;
	 j = url.indexOf("&",i);
	 strDatInizioSett = url.substring(i,j);
	
	 i = url.indexOf("DATFINESETTIMANA=") + "DATFINESETTIMANA=".length();;
	 j = url.indexOf("&",i);
	 strDatFineSettimana = url.substring(i,j);
	
	 i = url.indexOf("CODGIORNALE=") + "CODGIORNALE=".length();;
	 j = url.indexOf("&",i);
	 strCodGiornale = url.substring(i,j);
 } 
%>


<html>
<head>
    <title>NuovaListaPubblicazione.jsp</title>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>

<script type="text/javascript">

  function isDate(dateStr) {
    var datePat = /^(\d{1,2})(\/|-)(\d{1,2})(\/|-)(\d{4})$/;
    var matchArray = dateStr.match(datePat); // is the format ok?

    if (matchArray == null) {
      return false;
    }

    month = matchArray[3]; // p@rse date into variables
    day = matchArray[1];
    year = matchArray[5];

    if (month < 1 || month > 12) { // check month range
      return false;
    }

    if (day < 1 || day > 31) {
      return false;
    }

    if ((month==4 || month==6 || month==9 || month==11) && day==31) {
      return false;
    }

    if (month == 2) { // check for february 29th
      var isleap = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
      if (day > 29 || (day==29 && !isleap)) {
        return false;
      }
    }

    return true; // date is valid
  }

	function invio(){

	// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
		if (!controllaFunzTL())
			return ;
		var datainizio=new String(document.form1.DATINIZIOSETT_INS.value);
		var datafine=new String(document.form1.DATFINESETTIMANA_INS.value);
  		var giornale = "";
  		if(document.form1.CODGIORNALE_INS.selectedIndex != -1){
  			giornale = document.form1.CODGIORNALE_INS.options[document.form1.CODGIORNALE_INS.selectedIndex].text;
  		}
  		
		if (!isDate(datainizio)){
			alert("Data inizio settimana non valida");
			undoSubmit();
      		return;
		}
    	if (!isDate(datafine)){
			alert("Data fine settimana non valida");    
			undoSubmit();
	        return;
    	}
    	
    	if (giornale == "" ){
			alert("Il campo giornale è obbligatorio");    
			undoSubmit();
	        return;
    	}

	    meseinizio=datainizio.substring(3,5);
	    giornoinizio=datainizio.substring(0,2);
	    annoinizio=datainizio.substring(6,10);
	    var data1inizio=new Date(meseinizio+"/"+giornoinizio+"/"+annoinizio);
	
	    mesefine=datafine.substring(3,5);
	    giornofine=datafine.substring(0,2);
	    annofine=datafine.substring(6,10);
	    var data1fine=new Date(mesefine+"/"+giornofine+"/"+annofine);
	      
	    if (data1inizio>data1fine){
	      alert("La data di inizio deve precedere la data fine");
	      undoSubmit();
	      return;
	    }
    /* i giorni della settimana non vengono piu' considerati
    
    if (data1inizio.getDay()!=1){
      alert("Il primo giorno della settimana deve essere un lunedì");
      return;
    }
    if (data1fine.getDay()!=5){
      alert("L'ultimo giorno della settimana deve essere un venerdì");
      return;
    }
	*/
	//
	document.form1.DATINIZIOSETT.value = document.form1.DATINIZIOSETT_INS.value;
	document.form1.DATFINESETTIMANA.value = document.form1.DATFINESETTIMANA_INS.value;
	document.form1.CODGIORNALE.value = document.form1.CODGIORNALE_INS.value;
	//
    doFormSubmit(document.form1);
	}
<%--
function goBackLista() {
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

	<%
	String token = "_TOKEN_" + "IdoListaPubbGiorPage";
	String urlDiLista = (String) sessionContainer.getAttribute(token.toUpperCase());
	if (urlDiLista != null) {
		%>
		setWindowLocation("AdapterHTTP?<%= urlDiLista %>");
		<%
	}
	%>
}		

--%>
</script>

<SCRIPT>
function goBack() {
	setWindowLocation("AdapterHTTP?PAGE=IdoPubbGiorReportPage&CDNFUNZIONE=<%=cdnFunzione%>");
}		
</SCRIPT>



</head>

<body class="gestione" onload="rinfresca()">
<br/>
<%out.print(htmlStreamTop);%>
<p class="titolo">Inserimento Lista Pubblicazioni</p>
<af:form name="form1" method="POST" action="AdapterHTTP" >

<af:textBox type="hidden" name="PAGE" value="IdoListaPubbGiorListaPage" />
<af:textBox type="hidden" name="MODULE" value="INSERISCI" />
<af:textBox type="hidden" name="PROMPT0" value="<%=cdnLavoratore%>" />
<af:textBox type="hidden" name="UTENTE" value="<%=codiceUtente%>" />
<af:textBox type="hidden" name="SESSIONID" value="<%=sessionId%>" />
<af:textBox type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>" />
<input type="hidden" name="CODGIORNALE" value="<%=strCodGiornale%>"/>
<input type="hidden" name="DATINIZIOSETT" value="<%=strDatInizioSett%>"/>
<input type="hidden" name="DATFINESETTIMANA" value="<%=strDatFineSettimana%>"/>
<input type="hidden" name="LIST_PAGE" value="<%=strListPage%>"/>
<input type="hidden" name="MESSAGE" value="<%=strMessage%>"/>
<input type="hidden" name="RETRICERCA" value="<%=retRicerca%>"/>


<table class="main">

<tr>
  <td class="etichetta">Dal giorno</td>
  <td class="campo">
    <af:textBox type="date" name="DATINIZIOSETT_INS" title="Data richiesta" value="" size="12" maxlength="10" validateOnPost="true"/>&nbsp;&nbsp;
    <font class="etichetta">Al giorno</font>
    <af:textBox type="date" name="DATFINESETTIMANA_INS" title="Data richiesta" value="" size="12" maxlength="10" validateOnPost="true" />
  </td>
</tr>

<tr>
  <td class="etichetta">Giornale</td>
  <td class="campo">
    <af:comboBox name="CODGIORNALE_INS"
      title="Giornale"
      moduleName="M_IdoListaGiornali"
      classNameBase="input"
      disabled="false"
      addBlank="false" />
  </td>
</tr>
</table>
<table>
<tr><td colspan="2">&nbsp;</td></tr>
<tr>	
  <td colspan="2" align="center">
  	<input class="pulsante" type="button" name="btnInserisci" value="Inserisci" onClick="invio();"/>
  </td>
</tr>
<tr>
  <td  colspan="2">
  	<%if ("".equals(retRicerca)) {
  		out.print(InfCorrentiAzienda.formatBackList(sessionContainer, "IdoListaPubbGiorPage"));
  	  } else {%>
  	  	<input class="pulsante" type="button" name="btnIndietro" value="Torna alla pagina di ricerca" onClick="goBack();"/>
  	<%}%>
  </td>
</tr>
</table>

</af:form>
<%out.print(htmlStreamBottom);%>
</body>
</html>
