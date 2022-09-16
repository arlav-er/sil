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
PageAttribs attributi = new PageAttribs(user, "ScadAmministrativePage");
boolean canScadDatePercLav = attributi.containsButton("SCAD_DATE_PERCORSI_LAV");
boolean canScadCollLavPor = attributi.containsButton("SCAD_COLLOQUIO_LAV_POR");
boolean canScadStipulaPatto = attributi.containsButton("SCAD_STIPULA_PATTO_LAV");
boolean canScadPatto = attributi.containsButton("SCAD_VALIDITA_PATTO");
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);

int cdnUt = user.getCodut();
int cdnTipoGruppo = user.getCdnTipoGruppo();
String codCpi="";
if(cdnTipoGruppo == 1) {
  codCpi =  user.getCodRif();
}
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Scadenziario</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>
<script language="javascript">
  function SettaScadenziario(nTipoScadenze) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    switch(nTipoScadenze) {
      case 1:
        document.Frm1.SCADENZIARIO.value = "AMM1";
        break;

      case 2:
        document.Frm1.SCADENZIARIO.value = "AMM2";
        break;

      case 3:
        document.Frm1.SCADENZIARIO.value = "AMM3";
        break;

      case 4:
        document.Frm1.SCADENZIARIO.value = "AMM4";
        break;
        
    }
    doFormSubmit(document.Frm1);
  }
</script>
</head>
<body class="gestione" onload="rinfresca();">
<af:form name="Frm1" method="POST" action="AdapterHTTP">
<p class="titolo">Scadenze Amministrative</p>
<%out.print(htmlStreamTop);%>
<p align="center">
<table class="main" align="center">
<%
if (canScadDatePercLav) {
%>
  <tr>
  <td class="etichetta" nowrap>
  Scadenze relative alle date stimate nei percorsi concordati con il lavoratore
  </td>
  <td class="campo">
  <A href="javascript:SettaScadenziario(1);"><img src="../../img/scadenze.gif" border="0"></img>
  </A>
  </td>
  </tr>
<%
}
if (canScadCollLavPor) {
%>
  <tr>
  <td class="etichetta" nowrap>
  Scadenze relative al colloquio di primo orientamento
  </td>
  <td class="campo">
  <A href="javascript:SettaScadenziario(2);"><img src="../../img/scadenze.gif" border="0"></img>
  </A>
  </td>
  </tr>
<%
}
if (canScadStipulaPatto) {
  %>
  <tr>
  <td class="etichetta" nowrap>
  Scadenze relative al patto che il cpi deve stipulare
  </td>
  <td class="campo">
  <A href="javascript:SettaScadenziario(3);"><img src="../../img/scadenze.gif" border="0"></img>
  </A>
  </td>
  </tr>
<%
}
if (canScadPatto) {
  %>
  <tr>
  <td class="etichetta" nowrap>
  Scadenze validità del patto/accordo
  </td>
  <td class="campo">
  <A href="javascript:SettaScadenziario(4);"><img src="../../img/scadenze.gif" border="0"></img>
  </A>
  </td>
  </tr>
<%
}
%>
</table>
</p>
<%out.print(htmlStreamBottom);%>
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
<input type="hidden" name="PAGE" value="ScadRicercaPage"/>
<input type="hidden" name="SCADENZIARIO" value=""/>
<input type="hidden" name="CODCPI" value="<%=codCpi%>"/>
</af:form>
</body>
</html>
