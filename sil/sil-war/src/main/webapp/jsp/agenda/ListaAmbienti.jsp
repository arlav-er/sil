<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ taglib uri="aftags" prefix="af" %>

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


<%
  BigDecimal prgAmbiente=null;
  String _page = (String) serviceRequest.getAttribute("PAGE");
  SourceBean ambientiRows=(SourceBean) serviceResponse.getAttribute("MLISTAAMBIENTI");
  int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  SourceBean riga = null;
  String strDescrizione="";
  String strDescrizioneVisualizza="";
  String strDataInizio="";
  String strDataFine="";
  BigDecimal numCapienza=null;
  BigDecimal numCapacita=null;
  String btnSalva="Inserisci";
  String btnAnnulla="";
  int cdnUt = user.getCodut();
  int cdnTipoGruppo = user.getCdnTipoGruppo();
  String strCodCpi;
  strCodCpi =  user.getCodRif();
%>

<html>
<head>
  <title>Lista Ambienti</title>
  <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  <af:linkScript path="../../js/"/>
</head>
<body class="gestione">

<font color="red">
  <af:showErrors/>
</font>
<font color="green">
  <af:showMessages prefix="MDeleteAmbiente"/>
  <af:showMessages prefix="MSalvaAmbiente"/>
  <af:showMessages prefix="MAggiornaAmbiente"/>
</font>
<af:list moduleName="MListaAmbienti" />
<center>
<af:form method="POST" action="AdapterHTTP" dontValidate="true">
<input type="hidden" name="PAGE" value="InsAmbientePage"/>
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
<input type="hidden" name="CODCPI" value="<%=strCodCpi%>"/>
<input type="hidden" name="MODULE" value="MInserisciAmbiente"/>
<input class="pulsanti" type="submit" name="inserisci" value="Nuovo ambiente"/>
</af:form>
</center>
</body>
</html>
