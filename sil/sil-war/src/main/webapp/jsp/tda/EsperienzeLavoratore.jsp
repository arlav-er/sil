<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
  com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
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
<%--
	PARAMETRI CARATTERISTICI DELLA PAGINA:
		TIPO_INFO: e' il vettore dei codici del tipo di informazione che l'utente vuole che venga visualizzato
		PROVENIENZA: indica se si arriva a questa pagina dal menu del lavoratore o dalla pagina della lista(tasto indietro)
--%>
<%
 
String _current_page = (String)serviceRequest.getAttribute("PAGE");
String _page = (String)serviceRequest.getAttribute("PAGE");
// map <codice informazione-visibilita'>
// se l'informazione e' da visualizzare allora il suo codice sara' presente nella map. Se non e'
// da visualizzare sara' assente (al valore 1 non e' associato alcun significato)
String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");
String cdnFunzione = (String)serviceRequest.getAttribute("CDNFUNZIONE");
 
// testata del lavoratore
InfCorrentiLav testata = new InfCorrentiLav(cdnLavoratore, user);
//
ProfileDataFilter filter = new ProfileDataFilter(user, _current_page);
filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
if (! filter.canView()){
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
}
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);

%>

<html>

<head>
<title>Esperienze lavoratore</title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<af:linkScript path="../../js/" />
 
</head>

<body class="gestione" onload="rinfresca()">
<%testata.show(out);%>
<center>
<p class="titolo">Esperienze lavoratore</p>
 

<%out.print(htmlStreamTop);%>

 <af:list moduleName="M_GetEsperienzeLavoratore" />

<%out.print(htmlStreamBottom);%>
 
</center>
</body>
</html>