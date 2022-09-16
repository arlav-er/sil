<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                 java.lang.*,
                 java.text.*,
                 java.util.*,
                 java.math.*,
                 it.eng.sil.bean.*,
                 java.util.HashSet,
                 it.eng.afExt.utils.StringUtils,
                 it.eng.sil.security.*,
                 it.eng.sil.util.Sottosistema"%>
            
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../../global/noCaching.inc" %>
<%@ include file="../../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page);
  PageAttribs attributi = new PageAttribs(user, _page);
  String _funzione =(String) serviceRequest.getAttribute("CDNFUNZIONE");
  String queryString = null;
  String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "CDNLAVORATORE");
  
  String tipoCondAperteCheck = StringUtils.getAttributeStrNotNull(serviceRequest, "tipoCondAperteCheck");
  String codCpi = StringUtils.getAttributeStrNotNull(serviceRequest, "codCpi");
  String tipoCondizione = StringUtils.getAttributeStrNotNull(serviceRequest, "tipoCondizione");

%>

<html>
<head>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>
	<%@ include file="../../documenti/_apriGestioneDoc.inc"%>


  
<SCRIPT language="Javascript" type="text/javascript">

</SCRIPT>
<script language="Javascript">
<% 
	attributi.showHyperLinks(out, requestContainer,responseContainer,"");
%>

	function tornaRicerca() {  
    	if (isInSubmit()) return;
     	
     	url ="AdapterHTTP?PAGE=StampaLavTipoCondizionePage";
   	 	url += "&CDNFUNZIONE="+"<%=_funzione%>";
     	setWindowLocation(url);
  	}
	
	function Stampa(){
		apriGestioneDoc('RPT_STAMPA_TIPO_CONDIZIONE','&tipoCondAperteCheck=<%=tipoCondAperteCheck%>&codCpi=<%=codCpi%>&tipoCondizione=<%=tipoCondizione%>','NOF');
	}
     
</script>

</head>
<body class="gestione" onload="rinfresca();">
<p></p>
<p class="titolo"></p>

<p align="center">
<af:form name="Frm1" method="POST" action="AdapterHTTP">

<input type="hidden" name="PAGE" value=""/>

<af:list moduleName="M_ListaTipoCondizione" getBack="true"/>
	<center>
		<input class="pulsante" type = "button" name="torna" value="Torna alla ricerca" onClick="tornaRicerca();"/>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="button" name="stampa" class="pulsanti" value="Stampa" onClick="Stampa();"/>
		&nbsp;&nbsp;&nbsp;&nbsp;
	</center>
</af:form>

<br/>
</body>
</html>