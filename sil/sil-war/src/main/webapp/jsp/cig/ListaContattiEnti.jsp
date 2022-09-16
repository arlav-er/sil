<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page import="
  com.engiweb.framework.base.*,
  
  it.eng.sil.security.User,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  it.eng.sil.security.PageAttribs,
  java.math.*" %>
  
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %> 
<%      
    SourceBean row = null;    
	BigDecimal numIscrAperte = null;
	BigDecimal prgUnicaIscr = null;
	BigDecimal prgUnicoPercorso = null;
	BigDecimal prgUnicoColloquio = null;
	BigDecimal numPercorsi = null;
	BigDecimal numPresaIncarico = null;
	BigDecimal cdnUtIns=null;
	BigDecimal cdnUtMod=null;
	SourceBean dataOrigin = null;
	
	BigDecimal operatore = null;
	
	boolean canModify = false;
	
	
	Testata operatori_info=null;
	String dtmIns="";
	String dtmMod="";
	
	PageAttribs attributi = new PageAttribs(user, "ListaContattiEntiPage");
	
	String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");
    String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
    int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
 
 %>

<html>
<head>
  <title>Lista Contatti</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css"/> 
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
  <af:linkScript path="../../js/"/>
  
  <SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>
  <SCRIPT TYPE="text/javascript">
  </SCRIPT>
</head>

<body class="gestione" onload="rinfresca();">

	<%  
   		InfCorrentiLav _testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
        _testata.setSkipLista(true);
        _testata.show(out);
        Linguette _linguetta = new Linguette(user, 1 , "ListaContattiEntiPage", new BigDecimal(cdnLavoratore)); 
        _linguetta.show(out);
    %>
	<h2>Contatti e-mail agli Enti</h2>
	<af:list moduleName="ListaContattiEntiModule"  skipNavigationButton="1"/> 
	
	<%
  		String divStreamTop = StyleUtils.roundLayerTop(!canModify);
  		String divStreamBottom = StyleUtils.roundLayerBottom(!canModify);  
    %>
   



 
<script language="Javascript">
  <% 
	attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore);
  %>
</script>
</body>
</html>
	
