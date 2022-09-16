<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,java.lang.*,java.text.*,java.util.*"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>


<%@ page import="
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  com.engiweb.framework.base.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>
                  
                  
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%

	String queryString ="";    


    String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "CDNLAVORATORE");

 

%> 

<html>
<head>
  <title>Lista Movimenti</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>


	<%@ include file="../documenti/_apriGestioneDoc.inc"%>

</head>

<body class="gestione">
<af:error/>
<p align="center">
  <center>
    <af:form dontValidate="true">
	<af:JSButtonList moduleName="CMProspMovLavL68ListModule" configProviderClass="it.eng.sil.module.collocamentoMirato.ButtonsListaMovimentiLavoratoreConfig"/>    
    </af:form>
  </center>
  <br/>

  
  </body>
</html>


