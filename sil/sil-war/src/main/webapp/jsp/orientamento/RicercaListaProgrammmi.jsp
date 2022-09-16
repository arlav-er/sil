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
 
  String soggetto = "Programmi";
  String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
    

%> 


<html>
<head>
  <title>Lista <%=soggetto%></title>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/" />




  <script language="JavaScript">
  function inserisciProgrammi () {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;
	
		doFormSubmit(document.FrmInserisciAz);
  }
  
  
  function tornaAllaRicerca() {
  	if (isInSubmit()) return;
  	queryString = "PAGE=RicercaProgrammaPage&cdnLavoratore=<%=cdnLavoratore%>";
  	window.location = "AdapterHTTP?" + queryString;
  }
  </script>
  
</head>

<body onload="rinfresca();" class="gestione">
<af:error/>
<p align="center">
<af:list moduleName="M_GetListaProgrammi" />

<table class="main">
<tr><td>&nbsp;</td></tr>
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
<tr>
  <td class="cal_legenda">&nbsp;
          <img src="../../img/warning_trasp.gif">&nbsp;Programma già utilizzato per questo lavoratore.&nbsp;&nbsp;
        
          </td>
</tr>
</table>

</body>
</html>