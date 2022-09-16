<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ page import="
  com.engiweb.framework.base.*,
  java.lang.*,
  java.text.*,
  java.util.*, 
  it.eng.sil.util.*,
  it.eng.afExt.utils.StringUtils,
  it.eng.sil.security.*" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
PageAttribs attributi = new PageAttribs(user, "ScadenzeCigPage");

boolean canLavPresaCarico = attributi.containsButton("LAVPRESACARICO");
boolean canPresentazioneCig = attributi.containsButton("NOPRESENTAZIONECIG");
boolean canEsitiNeg = attributi.containsButton("ESITINEG");
boolean canNoCorsi = attributi.containsButton("NOCORSI");

String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);

String _pageProvenienza = serviceRequest.containsAttribute("PAGEPROVENIENZA")? serviceRequest.getAttribute("PAGEPROVENIENZA").toString():"";

%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Scadenziario Cig</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>
<script language="javascript">
  function SettaScadenziarioCig(nTipoScadenze) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    switch(nTipoScadenze) {
      case 1:
        document.Frm1.SCADENZIARIO.value = "CIG1";
        break;

      case 2:
        document.Frm1.SCADENZIARIO.value = "CIG2";
        break;

      case 3:
        document.Frm1.SCADENZIARIO.value = "CIG3";
        break;

      case 4:
        document.Frm1.SCADENZIARIO.value = "CIG4";
        break;
        
    }
    doFormSubmit(document.Frm1);
  }
</script>
</head>
<body class="gestione" onload="rinfresca();">
<af:form name="Frm1" method="POST" action="AdapterHTTP">
<p class="titolo">Scadenze CIG/MB in Deroga</p>
<%out.print(htmlStreamTop);%>
<p align="center">
<table class="main" align="center">
<%
if (canLavPresaCarico) {
%>
  <tr>
  <td class="etichetta" nowrap>
  	Lavoratori prima presa in carico CIG/MB in Deroga
  </td>
  <td class="campo">
  <A href="javascript:SettaScadenziarioCig(1);"><img src="../../img/scadenze.gif" border="0"></img>
  </A>
  </td>
  </tr>
<%
}
if (canPresentazioneCig) {
%>
  <tr>
  <td class="etichetta" nowrap>
  	Mancata presentazione CIG/MB in Deroga
  </td>
  <td class="campo">
  <A href="javascript:SettaScadenziarioCig(2);"><img src="../../img/scadenze.gif" border="0"></img>
  </A>
  </td>
  </tr>
<%
}
if (canEsitiNeg) {
  %>
  <tr>
  <td class="etichetta" nowrap>
  	Due esiti negativi per la stessa azione CIG/MB in Deroga
  </td>
  <td class="campo">
  <A href="javascript:SettaScadenziarioCig(3);"><img src="../../img/scadenze.gif" border="0"></img>
  </A>
  </td>
  </tr>
<%
}
if (canNoCorsi) {
  %>
  <tr>
  <td class="etichetta" nowrap>
  	Assenza corsi
  </td>
  <td class="campo">
  <A href="javascript:SettaScadenziarioCig(4);"><img src="../../img/scadenze.gif" border="0"></img>
  </A>
  </td>
  </tr>
<%
}
%>
</table>

<%out.print(htmlStreamBottom);%>
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
<input type="hidden" name="PAGE" value="CercaScadenzeCigPage"/>
<input type="hidden" name="SCADENZIARIO" value=""/>
<input type="hidden" name="PAGEPROVENIENZA" value="<%=_pageProvenienza%>">
</af:form>
</body>
</html>
