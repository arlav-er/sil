<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
    com.engiweb.framework.base.*,
    com.engiweb.framework.configuration.ConfigSingleton,
    
    com.engiweb.framework.error.EMFErrorHandler,
    it.eng.afExt.utils.DateUtils,
    it.eng.sil.security.User,
    it.eng.afExt.utils.*,it.eng.sil.util.*,
    java.lang.*,
    java.text.*,
    java.math.*,
    java.sql.*,
    oracle.sql.*,
    java.util.*"%>
    
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af" %>

<%
  String CODCPI;
  String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
  SourceBean assegnazioniRows=(SourceBean) serviceResponse.getAttribute("MListaAssegnazione");
  int cdnUt = user.getCodut();
  int cdnTipoGruppo = user.getCdnTipoGruppo();
    
  if(cdnTipoGruppo==1) { 
    CODCPI =  user.getCodRif(); 
  }
  else { 
    CODCPI = requestContainer.getAttribute("agenda_codCpi").toString(); 
  }
%>

<html>
<head>
  <title>Lista Assegnazione</title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
 <af:linkScript path="../../js/" />
</head>
<body class="gestione">
<font color="red">
  <af:showErrors/>
</font>
<font color="green">
  <af:showMessages prefix="MSalvaAssegnazione"/>
  <af:showMessages prefix="MAggiornaAssegnazione"/>
  <af:showMessages prefix="MDeleteAssegnazione"/>
</font>
<af:list moduleName="MListaAssegnazione"/>
<center>
<af:form method="POST" action="AdapterHTTP" dontValidate="true">
<span class="bottoni">
<input class="pulsanti" type="submit" name="inserisci" value="Nuova assegnazione"/>
<input type="hidden" name="PAGE" value="NUOVOASSEGNAZIONEPAGE"/>
<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
<input type="hidden" name="CODCPI" value="<%=CODCPI%>"/>
</span>
</af:form>
</center>
</body>
</html>
