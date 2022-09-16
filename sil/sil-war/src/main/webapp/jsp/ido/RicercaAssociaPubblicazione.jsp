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
 String prgElencoGiornale=(String) serviceRequest.getAttribute("PRGELENCOGIORNALE"); 
 String codGiornale=(String) serviceRequest.getAttribute("CODGIORNALE");
 String datInizioSett=(String) serviceRequest.getAttribute("DATINIZIOSETT");
 String datFineSettimana=(String) serviceRequest.getAttribute("DATFINESETTIMANA"); 
 PageAttribs attributi = new PageAttribs(user, "IdoPubbGiorReportPage");
 String nomeGiornale=(String) serviceResponse.getAttribute("M_DECODIFICAGIORNALE.ROWS.ROW.STRDESCRIZIONE");  
 
 boolean canModify= attributi.containsButton("inserisci");
 boolean canDelete= attributi.containsButton("rimuovi");  
  
 cdnLavoratore="1";
 String queryString = null;

 String htmlStreamTop = StyleUtils.roundTopTable(false);
 String htmlStreamBottom = StyleUtils.roundBottomTable(false);
 String retRicerca = (String) StringUtils.getAttributeStrNotNull(serviceRequest, "RETRICERCA");
%>

<SCRIPT>
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
</SCRIPT>



<html>
<head>
    <title>RicercaAssociaPubblicazione.jsp</title>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>
</head>

<body class="gestione" onload="rinfresca()">
<br/>
<%out.print(htmlStreamTop);%>
<p class="titolo">Ricerca pubblicazioni da associare alla lista del <%=nomeGiornale%></p>
<af:form name="form1" method="POST" action="AdapterHTTP" >

<af:textBox type="hidden" name="PAGE" value="IdoAssociaPubbPage" />
<af:textBox type="hidden" name="PROMPT0" value="<%=cdnLavoratore%>" />
<af:textBox type="hidden" name="UTENTE" value="<%=codiceUtente%>" />
<af:textBox type="hidden" name="SESSIONID" value="<%=sessionId%>" />
<af:textBox type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>" />
<af:textBox type="hidden" name="PRGELENCOGIORNALE" value="<%=prgElencoGiornale%>" />
<af:textBox type="hidden" name="CODGIORNALE" value="<%=codGiornale%>"/>
<af:textBox type="hidden" name="DATINIZIOSETT" value="<%=datInizioSett%>"/>
<af:textBox type="hidden" name="DATFINESETTIMANA" value="<%=datFineSettimana%>"/>
<af:textBox type="hidden" name="RETRICERCA" value="<%=retRicerca%>"/>


<table class="main">

<tr>
  <td class="etichetta">Dal giorno</td>
  <td class="campo">
    <af:textBox type="date" name="DATPUBBLICAZIONEDAL" title="Data richiesta" value="" size="12" maxlength="10" validateOnPost="true"/>&nbsp;&nbsp;
    <font type="etichetta">Al giorno</font>
    <af:textBox type="date" name="DATPUBBLICAZIONEAL" title="Data richiesta" value="" size="12" maxlength="10" validateOnPost="true" />
  </td>
</tr>

  <tr ><td colspan="4" ><hr width="90%"/></td></tr>      

  <tr>
    <td class="etichetta" valign="top">Modalità pubblicazione</td>
    <td class="campo">
      <af:comboBox multiple="true" title="Modalità Pubblicazione" name="modPubblicazione">
        <OPTION value="1">Web      </OPTION>
        <OPTION value="2">Giornali </OPTION>
        <OPTION value="3">Bacheca  </OPTION>
      </af:comboBox>
    </td>
  </tr>
<br/>
<tr ><td colspan="2" >Utente di inserimento</td></tr>      
<tr ><td colspan="2" >
  <input type="radio" name="UTRIC" value="MIE" checked> Mie
  <input type="radio" name="UTRIC" value="GRUP"> Mio gruppo
  <input type="radio" name="UTRIC" value="TUTTE"> Tutte
</td></tr>      

<tr>
  <td colspan="2">
	 <center>
		 <input class="pulsante" type="submit" name="btnInvio" value="Ricerca"/>
		 <input class="pulsante" type="button" name="back" value="Chiudi" onclick="javascript:history.back();"/>
    </center>
  </td>
</tr>

</table>

</center></p>
</af:form>
<%out.print(htmlStreamBottom);%>
</body>
</html>
