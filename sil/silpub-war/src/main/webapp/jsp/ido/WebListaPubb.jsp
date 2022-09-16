<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                 java.lang.*,
                 java.text.*,
                 java.util.*, 
                 it.eng.afExt.utils.StringUtils,
                 it.eng.sil.security.*"%>
            
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<% 
 // int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
 String cdnUt = StringUtils.getAttributeStrNotNull(serviceRequest,"CDNUT");
 
 String strCollocamentoMirato = StringUtils.getAttributeStrNotNull(serviceRequest,"FLAGCM");
 boolean flagCM = strCollocamentoMirato.equals("true")?true:false;
 
 String _page = "";
 String _btnTxt = "";
 String prov = StringUtils.getAttributeStrNotNull(serviceRequest,"PROV");
 if(prov.equals("G")) { 
 	_page = "WebGrigliaProvPage"; 
 	_btnTxt = "Torna alla griglia delle offerte";
 } else { 
 	_page = "WebRicercaPubbPage"; 
 	_btnTxt = "Torna alla pagina di ricerca";
 }
 
%>
<html>
<head>
  <title>Pubblicazioni</title>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  <af:linkScript path="../../js/"/>
  <script>
  	function doSelect(page, prgRichiestaAz, prgAzienda, prgUnita, cdnUt){
  		feat="toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,top=100,left=100, width=700, height=500";
 		window.open("AdapterHTTP?PAGE="+page+"&prgRichiestaAz="+prgRichiestaAz+"&prgAzienda="+prgAzienda+"&prgUnita="+prgUnita+"&cdnut="+cdnUt+"&flagCM="+"<%=String.valueOf(flagCM)%>",
 		'_blank',feat);
 	}
  </script>
</head>
<body class="gestione">
<% if( flagCM ){%>
	<af:list moduleName="WebListaPubbCM"  jsSelect="doSelect"/>
<%}else{%>
	<af:list moduleName="WebListaPubb"  jsSelect="doSelect"/>
<%}%>
<center>
<!--input class="pulsante" type = "button" name="torna" value="Torna alla pagina di ricerca" disabled="true"/-->
<af:form name="Frm1" method="POST" action="AdapterHTTP">
<input type="hidden" name="PAGE" value="<%=_page%>"/>
<input type="hidden" name="CDNUT" value="<%=cdnUt%>">
<input type="hidden" name="FLAGCM" value="<%=String.valueOf(flagCM)%>">
<input type="submit" class="pulsanti" name="sub" value="<%=_btnTxt%>">
</af:form>
<br>
</center>
<%@ include file="/jsp/MIT.inc" %>
</body>
</html>
