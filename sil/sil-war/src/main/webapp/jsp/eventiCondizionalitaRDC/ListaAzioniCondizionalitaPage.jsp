<%@ page contentType="text/html;charset=utf-8"%>
 
<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                java.math.*,
                 it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
	String _page = "PercorsiPage"; 

  
 // da togliere ovviamente
 PageAttribs attributi = new PageAttribs(user, _page);
	int     cdnfunzione      = SourceBeanUtils.getAttrInt(serviceRequest, "CDNFUNZIONE");
	String  _funzione   = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "CDNFUNZIONE");
  
 String cdnLavoratore= (String) StringUtils.getAttributeStrNotNull(serviceRequest, "CDNLAVORATORE");
  
	//CONTROLLO ACCESSO ALLA PAGINA
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));

	boolean canView = filter.canViewLavoratore();
	if (!canView) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}

%>
 
<html>
<head>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/> 
 <af:linkScript path="../../js/"/>

<script type="text/Javascript">
	<%//Genera il Javascript che si occuperà di inserire i links nel footer
	if (!cdnLavoratore.equals(""))
		attributi.showHyperLinks(out, requestContainer, responseContainer, "cdnLavoratore=" + cdnLavoratore);
	%>
</script>

</head>
<body  class="gestione">
<p class="titolo">Eventi di condizionalità</p>			
 
<af:list moduleName="M_ListaAzioniCondizionalitaFromPerc"/>
 
  <center>
      <input type="button" class="pulsanti" value="Chiudi" onclick="window.close()"/>			
   </center>

</body>
</html>