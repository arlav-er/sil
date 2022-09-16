<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ page import="
  com.engiweb.framework.base.*,
  java.lang.*,
  java.text.*,
  java.util.*, 
  it.eng.afExt.utils.StringUtils,
  it.eng.sil.security.*,
  it.eng.sil.util.*,
  it.eng.sil.module.movimenti.GraficaUtils,
  it.eng.afExt.utils.StringUtils" %>

<%@ include file="../global/getCommonObjects.inc"%>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
  String htmlStreamTop   = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
  SourceBean resultValidazione = (SourceBean) serviceResponse.getAttribute("MovGetRisultatiUltimaValidazione.LOG");
  SourceBean row = (SourceBean) serviceResponse.getAttribute("MovGetRisultatiUltimaValidazione.ROW");
  String dataUltimaValidazione = "";
  String utenteIns = "";
  String risultatiHTML = "";
  
  if (resultValidazione.containsAttribute("ERROR")) {
    risultatiHTML  = "<b>ERRORE</b> nella ricerca delle informazioni relative all'ultima validazione:<BR/>";
    risultatiHTML += StringUtils.getAttributeStrNotNull(resultValidazione,"ERROR");
  } else {
    dataUltimaValidazione = StringUtils.getAttributeStrNotNull(row,"DTMINS");
    utenteIns = StringUtils.getAttributeStrNotNull(row,"COGNOME_UT_INS") + " " +
                StringUtils.getAttributeStrNotNull(row,"NOME_UT_INS");
    risultatiHTML = GraficaUtils.showRisultati(resultValidazione,false,null);
  }
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>Mostra i risultati di una validazione</title>
<%-- Mostra i risultati di una validazione a partire dalla lista dei movimenti da validare --%>

<!-- ..jsp/movimenti/MovMostraRisultatiValidazione.jsp -->

<script language="Javascript">
var imgChiusa = "../../img/chiuso.gif";
var imgAperta = "../../img/aperto.gif";

//funzione che cambia lo stato di una sezione da aperta a chiusa o viceversa
function cambia(immagine, sezione) {
	if (sezione.style.display == 'inline') {
		sezione.style.display = 'none';
		sezione.aperta = false;
		immagine.src = imgChiusa;
    	immagine.alt = 'Apri';
	}
	else if (sezione.style.display == "none") {
		sezione.style.display = "inline";
		sezione.aperta = true;
		immagine.src = imgAperta;
    	immagine.alt = "Chiudi";
	}
}
</script>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
</head>

<body class="gestione" >
<p class="titolo"><br/>Risultati ultima validazione</p>
<%out.print(htmlStreamTop);%>
<af:form name="Frm1" method="POST" action="AdapterHTTP" >

<% if (risultatiHTML != null) { 
      if (resultValidazione.containsAttribute("RECORD")) {%>
      <table class="main" cellspacing="0" cellpadding="0">
        <tr><td>Ultima validazione</td>
            <td align="right">data&nbsp;</td>
            <td align="left"><strong>&nbsp;<%=dataUltimaValidazione%></strong></td>
            <td align="right">utente&nbsp;</td>
            <td align="left"><strong>&nbsp;<%=utenteIns%></strong></td>
        </tr>
      </table>
      <%}
      out.print(risultatiHTML);
   } else {
%>   ERRORE nel reperimento delle informazioni relative all&apos; ultima validazione
<% } %>

</af:form>
<%out.print(htmlStreamBottom);%>

<table class="main"><tr><td align="center"><input class="pulsanti" type="button" onclick="window.close();" name="Chiudi" value="chiudi"/></td></tr></table>


</body>
</html>
