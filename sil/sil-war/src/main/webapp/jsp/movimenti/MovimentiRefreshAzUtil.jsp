<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
    com.engiweb.framework.base.*,
    com.engiweb.framework.configuration.ConfigSingleton,
    
    com.engiweb.framework.error.EMFErrorHandler,
    com.engiweb.framework.util.JavaScript,
    it.eng.afExt.utils.*,
    it.eng.sil.security.User,
    it.eng.sil.util.*,
    java.lang.*,
    java.text.*,
    java.math.*,
    java.sql.*,
    oracle.sql.*,
    java.util.*"%>
    
<%@ taglib uri="aftags" prefix="af"%>
<%  
  String prgAziendaUtil = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGAZIENDAUTIL"); 
  String prgUnitaUtil = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGUNITAUTIL"); 
  String strRagioneSociale = "";
  String strIndirizzoAziendaUtil="";
  String strComuneAziendaUtil="";
  strRagioneSociale = serviceRequest.containsAttribute("strRagioneSocialeAzUtil")?serviceRequest.getAttribute("strRagioneSocialeAzUtil").toString():"";
  strIndirizzoAziendaUtil = serviceRequest.containsAttribute("strIndirizzoAziendaUtil")?serviceRequest.getAttribute("strIndirizzoAziendaUtil").toString():"";
  strComuneAziendaUtil = serviceRequest.containsAttribute("strComuneAziendaUtil")?serviceRequest.getAttribute("strComuneAziendaUtil").toString():"";
  String numContratto = serviceRequest.containsAttribute("numContratto")?serviceRequest.getAttribute("numContratto").toString():"";
  String dataInizio = serviceRequest.containsAttribute("dataInizio")?serviceRequest.getAttribute("dataInizio").toString():"";
  String dataFine = serviceRequest.containsAttribute("dataFine")?serviceRequest.getAttribute("dataFine").toString():"";
  String legaleRapp = serviceRequest.containsAttribute("legaleRapp")?serviceRequest.getAttribute("legaleRapp").toString():"";
  String numSoggetti = serviceRequest.containsAttribute("numSoggetti")?serviceRequest.getAttribute("numSoggetti").toString():"";
  String classeDip = serviceRequest.containsAttribute("classeDip")?serviceRequest.getAttribute("classeDip").toString():"";
  String funzione = serviceRequest.containsAttribute("FUNZ_AGG")?serviceRequest.getAttribute("FUNZ_AGG").toString():"";    
%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<HEAD>
  <af:linkScript path="../../js/" />
  
  <script type="text/javascript">
  
  var datacontainer = new Object();
  window.dati = datacontainer;
  datacontainer.prgAziendaUtil = "<%=prgAziendaUtil%>";
  datacontainer.prgUnitaUtil = "<%=prgUnitaUtil%>";
  datacontainer.strRagioneSociale = "<%=StringUtils.replace(strRagioneSociale,"\"","\\\"")%>";
  datacontainer.strIndirizzoAzienda = "<%=strIndirizzoAziendaUtil%>";
  datacontainer.comuneAzienda = "<%=strComuneAziendaUtil%>";
  datacontainer.numContratto = "<%=numContratto%>";
  datacontainer.dataInizio = "<%=dataInizio%>";
  datacontainer.dataFine = "<%=dataFine%>";
  datacontainer.legaleRapp = "<%=legaleRapp%>";
  datacontainer.numSoggetti = "<%=numSoggetti%>";
  datacontainer.classeDip = "<%=classeDip%>";
  
  </script>
</head>

<body class="gestione" onload="javascript:window.opener.<%=funzione%>();">

</body>
</html>