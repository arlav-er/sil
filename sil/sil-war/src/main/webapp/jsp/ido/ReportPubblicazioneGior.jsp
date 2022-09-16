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
 String _page = (String) serviceRequest.getAttribute("PAGE"); 
 PageAttribs attributi = new PageAttribs(user, _page);
 boolean canModify= attributi.containsButton("inserisci");
 boolean canDelete= attributi.containsButton("rimuovi");  
  
 cdnLavoratore="1";
 String queryString = null;

 String htmlStreamTop = StyleUtils.roundTopTable(false);
 String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>



<html>
<head>
    <title>ReportPubblicazioneGior.jsp</title>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>
    <script>
    	function controllaDate() {
    		ret = true;
    		var datainizio=new String(document.form1.DATINIZIOSETT.value);
			var datafine=new String(document.form1.DATFINESETTIMANA.value);
			if (datainizio!="" && datafine!="") {
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
					ret = false;
				}
			}
	    	return ret;
    	}
    </script>

    <script>
    	function nuovaLista() {
    		var w = "AdapterHTTP?PAGE=IdoNuovaListaPubbPage&CDNFUNZIONE=<%=cdnFunzione%>&RETRICERCA=S";    		
	    	setWindowLocation(w);
    	}
    </script>
</head>

<body class="gestione" onload="rinfresca()">
<br/>
<%out.print(htmlStreamTop);%>
<p class="titolo">Ricerca pubblicazioni per Giornali</p>
<af:form name="form1" method="POST" action="AdapterHTTP" onSubmit="controllaDate()">

<af:textBox type="hidden" name="PAGE" value="IdoListaPubbGiorPage" />
<af:textBox type="hidden" name="PROMPT0" value="<%=cdnLavoratore%>" />
<af:textBox type="hidden" name="UTENTE" value="<%=codiceUtente%>" />
<af:textBox type="hidden" name="SESSIONID" value="<%=sessionId%>" />
<af:textBox type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>" />

<table class="main">

<tr>
  <td class="etichetta">Dal giorno</td>
  <td class="campo">
    <af:textBox type="date" name="DATINIZIOSETT" title="Data richiesta" value="" size="12" maxlength="10" validateOnPost="true"/>&nbsp;&nbsp;
    <font type="etichetta">Al giorno</font>
    <af:textBox type="date" name="DATFINESETTIMANA" title="Data richiesta" value="" size="12" maxlength="10" validateOnPost="true" />
  </td>
</tr>

<tr>
  <td class="etichetta">Giornale</td>
  <td class="campo">
    <af:comboBox name="CODGIORNALE"
      title="Giornale"
      moduleName="M_IdoListaGiornali"
      classNameBase="input"
      disabled="false"
      addBlank="true" />
  </td>
</tr>

<tr><td colspan="2">&nbsp;</td></tr>

<tr>
  <td colspan="2">
		 <center><input class="pulsante" type="submit" name="btnInvio" value="Ricerca"/></center>
  </td>
<tr>
  <td colspan="2">
		 <center><input class="pulsante" type="button" onClick="nuovaLista();" name="btnNuovaLista" value="Nuova lista"/></center>
  </td>
</tr>
  
</tr>

</table>

</center></p>
</af:form>
<%out.print(htmlStreamBottom);%>
</body>
</html>
