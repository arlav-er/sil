<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %> 
<%@ include file="../global/getCommonObjects.inc" %>


<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.security.ProfileDataFilter,                     
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  java.text.*,
                  it.eng.afExt.utils.*"
%> 


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<% 
	String  titolo = "Storico modifiche ISEE";
    String _page   = (String) serviceRequest.getAttribute("PAGE");
	String cdnLavoratore 	= 	(String) serviceRequest.getAttribute("cdnLavoratore");
	String prgValoreIsee 	= 	(String) serviceRequest.getAttribute("prgValoreIsee");
	//System.out.println("mld prgValoreIsee:"+prgValoreIsee);
	
	// CONTROLLO ACCESSO ALLA PAGINA
	//ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	ProfileDataFilter filter = new ProfileDataFilter(user, "AsValoreIseePage");
	
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}	
    
%>

<html>
<head>
	<title><%= titolo %></title>
	<link rel="stylesheet" type="text/css" href=" ../../css/stili.css"/>
	<link rel="stylesheet" type="text/css" href=" ../../css/listdetail.css"/>
 	<af:linkScript path="../../js/" />
</head>

<body class="gestione">

<af:showErrors />
<af:showMessages prefix="M_AsValoreISEEStorico"/>


<af:form dontValidate="true">

<p class="titolo"><%= titolo %></p>


<table class="main">
    
    <%-- STAMPA LA LISTA --%>
	<af:list moduleName="M_AsValoreISEEStorico" />

</table>

<%-- BOTTONI --%>
<table class="main">
	<tr>
  		<td>&nbsp;
  		</td>
  	</tr>
  	<tr>
  		<td align="center"><input type="button" class="pulsanti" value="Chiudi" onClick="window.close()">
  		</td>
  	</tr>
</table>



</af:form>
</body>
</html>