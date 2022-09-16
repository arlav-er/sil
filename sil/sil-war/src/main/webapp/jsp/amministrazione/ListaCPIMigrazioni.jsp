<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ include file="../amministrazione/openPage.inc" %>

<%@ page import="
  com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.User,
  it.eng.afExt.utils.*,
  it.eng.sil.security.ProfileDataFilter,  
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  it.eng.sil.security.PageAttribs,  
  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

	String _current_page = (String) serviceRequest.getAttribute("PAGE"); 
    PageAttribs attributi = new PageAttribs(user, _current_page);
	BigDecimal cdnUtIns = null;
	BigDecimal cdnUtMod = null;
	boolean canModify = false;
	boolean canDelete = false;
	String dtmIns = null;
	String dtmMod = null;
	
      //canModify = attributi.containsButton("aggiorna");
    	

%>

<%  

  
  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");
  String strCodiceUtenteCorrente=codiceUtenteCorrente.toString();


  String _page = (String) serviceRequest.getAttribute("PAGE"); 

  String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE"); 
    

  
  
  
  // *************
  
%>

<html>

<head>
  <title>Cpi Migrazioni</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

  <af:linkScript path="../../js/"/>

<script language="Javascript">
<!--Contiene il javascript che si occupa di aggiornare i link del footer-->
  <% 
       //Genera il Javascript che si occuperà di inserire i links nel footer
       attributi.showHyperLinks(out, requestContainer,responseContainer,"");
  %>
</script>

  <SCRIPT TYPE="text/javascript">

    function Select(prg) {
      
      var s= "AdapterHTTP?PAGE=CpiMigrazioniPage";
      s += "&CDNFUNZIONE=<%= cdnFunzione %>";
      
      setWindowLocation(s);
    }

    function tornaAllaRicerca( ){
		var s= "AdapterHTTP?PAGE=RicercaCpiMigrazioniPage";
		s += "&CDNFUNZIONE=<%= cdnFunzione %>";
    	  
      	setWindowLocation(s);
    }
      
  
  </SCRIPT>

</head>

<body class="gestione" onload="rinfresca()">

  
  <center>
    <font color="red">
      <af:showErrors />
    </font>
  </center>
  
      <p align="center">
          <af:list moduleName="M_ListCpiMigrazioni" skipNavigationButton="0" />          
          <center>
          	<input class="pulsanti" type="button" value="Nuova ricerca" onclick="tornaAllaRicerca()"/>
          </center>
      </p>      
</body>
</html>