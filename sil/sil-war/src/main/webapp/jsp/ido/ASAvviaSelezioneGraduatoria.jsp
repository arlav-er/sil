<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,
                it.eng.afExt.utils.*, java.math.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.*,
                it.eng.sil.util.*"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
    
String prgRichiestaAz = serviceRequest.getAttribute("PRGRICHIESTAAZ").toString();
String prgAzienda = serviceRequest.getAttribute("PRGAZIENDA").toString();
String prgUnita = serviceRequest.getAttribute("PRGUNITA").toString();
String _cdnFunzione = serviceRequest.getAttribute("cdnFunzione").toString();
String _page = serviceRequest.getAttribute("PAGE").toString();
String prgRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGROSA");



// inizializzazione delle variabili usate nella testata
String numRichiesta="", numAnno="",prgTipoRosa="", prgTipoIncrocio="",tipoIncrocio="",prgIncrocio="", tipoRosa="",prgAlternativa="", 
	 strAlternativa="",prgOrig="", prgCopia1="",numKloRosa="",utMod="", ultimaModifica="", numRichiestaOrig="",
	 utAttivo="" , ordinamento="";

SourceBean infoRosa=(SourceBean)serviceResponse.getAttribute("ASDettaglioGraduatoria.row");
numRichiesta = StringUtils.getAttributeStrNotNull(infoRosa, "NUMRICHIESTA");
numRichiestaOrig = StringUtils.getAttributeStrNotNull(infoRosa, "NUMRICHIESTAORIG");
numAnno = StringUtils.getAttributeStrNotNull(infoRosa, "NUMANNO");
prgIncrocio = StringUtils.getAttributeStrNotNull(infoRosa, "PRGINCROCIO");
tipoIncrocio = StringUtils.getAttributeStrNotNull(infoRosa, "TIPOINCROCIO");
prgTipoIncrocio = StringUtils.getAttributeStrNotNull(infoRosa, "PRGTIPOINCROCIO");
tipoRosa = StringUtils.getAttributeStrNotNull(infoRosa, "TIPOROSA");
prgTipoRosa = StringUtils.getAttributeStrNotNull(infoRosa, "PRGTIPOROSA");
String strTipoGrd = "";
if (("2").equals(prgTipoRosa)) {
	strTipoGrd = "Grezza";    
}
else {
	strTipoGrd = "Definitiva";
}
prgAlternativa = StringUtils.getAttributeStrNotNull(infoRosa, "PRGALTERNATIVA");
strAlternativa = "";
if(!prgAlternativa.equals("")) { strAlternativa = "Profilo n. " + prgAlternativa; }
prgOrig = StringUtils.getAttributeStrNotNull(infoRosa, "PRGRICHIESTAORIG");
prgCopia1 = StringUtils.getAttributeStrNotNull(infoRosa, "PRGCOPIA1");
numKloRosa = StringUtils.getAttributeStrNotNull(infoRosa, "NUMKLOROSA");
utMod = StringUtils.getAttributeStrNotNull(infoRosa, "CDNUTMOD");
ultimaModifica = StringUtils.getAttributeStrNotNull(infoRosa, "ULTIMAMODIFICA");
utAttivo = Integer.toString(user.getCodut());
ordinamento = StringUtils.getAttributeStrNotNull(infoRosa, "NUMORDPROVINCIA");
 
String token = "";
String urlDiLista = "";
String refListaBtn = "";
if (sessionContainer!=null){
//  token = "_TOKEN_" + "IDOLISTARICHIESTEPAGE";
  token = "_TOKEN_" + "ASGestGraduatoriePage";  
  urlDiLista = (String) sessionContainer.getAttribute(token.toUpperCase());
  if (urlDiLista!=null && !urlDiLista.equals("")) {
    refListaBtn ="<a href=\"AdapterHTTP?" + urlDiLista + "\"><img src=\"../../img/rit_lista.gif\" border=\"0\"></a>";
  } else {
  	urlDiLista = "";
  }
}
String htmlStreamTopInfo = StyleUtils.roundTopTableInfoRetLista(urlDiLista);
String htmlStreamBottomInfo = StyleUtils.roundBottomTableInfoNobr();   
%>
<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <title>Avvia Selezione Graduatoria</title>
  <af:linkScript path="../../js/" />
  <script language="Javascript" src="../../js/utili.js" type="text/javascript"></script>

  <script language="Javascript" src="../../js/docAssocia.js"></script>
  
  <script language="Javascript">
    window.top.menu.caricaMenuAzienda(<%=_cdnFunzione%>,<%=prgAzienda%>, <%=prgUnita%>);
  </script>
</head>     

<body class="gestione" onload="rinfresca()">

<center>
</center>

<br>
  <%out.print(htmlStreamTopInfo);%>
  <p class="info_lav">
  
  Tipo di Graduatoria <b><%=tipoIncrocio%></b> - Stato della Graduatoria <b><%=tipoRosa%></b> <br>  
  <%
  if(!prgAlternativa.equals("")) {
  %>
  Alternativa utilizzata <b><%=strAlternativa%></b> <br>
  <%
  }
  %> 
  <br> 
  <a class="info_lav" href="#" onClick="openRich_PopUP('<%=prgOrig%>')"><img src="../../img/info_soggetto.gif" alt="Dettaglio"/></a>&nbsp;
  Richiesta num. <b><%=numRichiestaOrig%>/<%=numAnno%></b>
  <br/>
 
  </p>
  <%out.print(htmlStreamBottomInfo);%>
  
<font color="red"><af:showErrors/></font>
<font color="green"> 
 <af:showMessages prefix="ASAvviaCandidatiGraduatoria"/>
 <af:showMessages prefix="ASAvviaPrimiCandidatiGraduatoria"/>
</font>
<br>

</body>
</html>
