<%@page import="it.eng.afExt.utils.StringUtils"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ taglib uri="patto" prefix="pt" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.ProfileDataFilter,                   
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  java.lang.*,
                  java.text.*,it.eng.sil.security.PageAttribs"%>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String cdnLavoratore = null;
	String prgPattoLavoratore = Utils.notNull((String)serviceRequest.getAttribute("prgPattoLavoratore"));
	SourceBean infoPatto = (SourceBean)serviceResponse.getAttribute("NotePattoStoricizzato.ROWS.ROW");
	if (infoPatto != null) {
		cdnLavoratore = (String)infoPatto.getAttribute("CDNLAVORATORE");
	}
	String _current_page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _current_page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
 	PageAttribs pageAtts = new PageAttribs(user, _current_page);
	
	boolean canModify = false;
	boolean canView=filter.canViewLavoratore();
	if (!canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}
	
    String _funzione = (String)serviceRequest.getAttribute("CDNFUNZIONE");
    String htmlStreamTop = StyleUtils.roundTopTable(canModify);
    String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
    %>
<html>
<head>
<title>Soggetti Accreditati Programmi</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/"/>

<SCRIPT language="JavaScript">
</SCRIPT>
</head>
<body class="gestione" onload="rinfresca()">
<%
InfCorrentiLav _testata= new InfCorrentiLav(RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
Linguette _linguetta = new Linguette(user, Integer.parseInt(_funzione), _current_page, new BigDecimal(prgPattoLavoratore));
_linguetta.setCodiceItem("prgPattoLavoratore");
_testata.setPaginaLista("PattoInformazioniStorichePage");
_testata.show(out);
_linguetta.show(out);
%>
<br>
<center>
<%out.print(htmlStreamTop);%>
<table class="main">
<tr><td colspan="2">
	<af:list moduleName="M_ListaSoggettoAccProgrammi" canDelete="0"/>
</td></tr>
</table>
<%out.print(htmlStreamBottom);%>
</center>
</body>
</html>
