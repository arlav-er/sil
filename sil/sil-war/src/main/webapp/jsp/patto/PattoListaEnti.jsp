<!-- @author: Paolo Roccetti - Gennaio 2004 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
				  com.engiweb.framework.tags.DefaultErrorTag,
                   
                  com.engiweb.framework.util.*,
                  it.eng.sil.module.movimenti.*,
                  it.eng.sil.module.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>
                  
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
  String cdnFunzione = ""; 
  String cdnFunzioneLocal = serviceRequest.containsAttribute("CDNFUNZIONE")?serviceRequest.getAttribute("CDNFUNZIONE").toString():"";
  String funzioneaggiornamento = (String) serviceRequest.getAttribute("AGG_FUNZ");
  String prgColloquioProgramma = (String) serviceRequest.getAttribute("CODPROGRAMMA");
%> 

<html>
<head>
  <title>Lista Soggetti Accreditati (Enti)</title>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/" />

  <script language="JavaScript">
  
  function tornaAllaRicerca() {
  	if (isInSubmit()) return;
  	queryString = "AGG_FUNZ=<%=funzioneaggiornamento%>&CDNFUNZIONE=<%=cdnFunzioneLocal %>&PAGE=SelezionaEnteAccreditatoPage";
  	queryString = queryString + "&CODPROGRAMMA=<%=Utils.notNull(prgColloquioProgramma)%>"
  	window.location = "AdapterHTTP?" + queryString;
  }
  </script>
  
</head>

<body onload="checkError();" class="gestione">
<af:error/>
<p align="center">

<af:list moduleName="M_PattoGetListaEnteAccreditato"/>
 
<table class="main">

<tr>
  <td align="center">
  
   <input type="button" class="pulsanti" value="Ritorna alla pagina di ricerca" onClick="tornaAllaRicerca()">
 
  </td>
</tr>
<tr>
  <td align="center">
	<input type="button" class="pulsanti" value="Chiudi" onClick="window.close()">
  </td>
</tr>
</table>

</body>
</html>