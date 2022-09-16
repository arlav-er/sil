<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ page
	contentType="text/html;charset=utf-8"
	import="com.engiweb.framework.base.*,
			it.eng.sil.security.*,
			it.eng.afExt.utils.*,
			it.eng.sil.util.*,
			java.math.*,
			java.util.*"	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%
	ProfileDataFilter filter = new ProfileDataFilter(user, "CMProspListaPage");
	boolean canView = filter.canView();
	if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}
	
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 
		
	//int cdnfunzione   = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
			
%>

<html>
<head>
<title>Lista prospetti da protocollare</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<af:linkScript path="../../js/"/>


</head>

<body class="gestione" onload="rinfresca()">

<af:showErrors />

<af:list moduleName="ListaProspettiDaSareModule" />

<af:form name="Frm1" method="POST" action="AdapterHTTP">
	<input type="hidden" name="PAGE" value="RiProtoProspDaSarePage"/>
	<input type="hidden" name="MODULE" value="CMProtocollaProspDaSareModule"/>   
	<input type="hidden" name="PROTOCOLLA" value="true" />	

	<table class="main"> 	
		<tr>
			<td>
				<input type="submit" class="pulsante" name="inserisci" value="PROTOCOLLA" />
			</td>
		</tr>		
	</table>

</af:form>

<af:form name="Frm1" method="POST" action="AdapterHTTP">
	<input type="hidden" name="PAGE" value="RiProtoProspDaSarePage"/>
	<input type="hidden" name="MODULE" value="CMAggiornaNumProtocollo"/>   	

	<table class="main"> 	
		<tr>
			<td>
				<input type="submit" class="pulsante" name="inserisci" value="AGG_NUMPROTO" />
			</td>
		</tr>		
	</table>

</af:form>

</body>
</html>