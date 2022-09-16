<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<!-- @author: Giordano Gritti -->
<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*, 
                java.math.*"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
		String _page = (String) serviceRequest.getAttribute("PAGE");
		ProfileDataFilter filter = new ProfileDataFilter(user, _page);
		
		
	    InfCorrentiLav infCorrentiLav	= null;
	  	Testata operatoreInfo 			= null;
		String cdnLavoratore			= "";
		String cdnFunzione				= "";		

		cdnLavoratore	= StringUtils.getAttributeStrNotNull(serviceRequest,"CDNLAVORATORE");		
		cdnFunzione		= StringUtils.getAttributeStrNotNull(serviceRequest,"cdnFunzione");		
		
		
		SourceBean row  = null;
		//variabili in sola lettura
		String dataAvv	= "";
		String ente		= "";
		String cf		= "";
		String indEnte	= "";
		String rif		= "";
		String prgAvv	= "";
		
		Vector vettRis = serviceResponse.getAttributeAsVector("M_LISTA_AVVIAMENTI.ROWS.ROW");
		for(int i = 0; i<vettRis.size(); i++){
			row = (SourceBean) vettRis.get(i);
			dataAvv		= StringUtils.getAttributeStrNotNull(row,"DATAINIZIO");
			ente		= StringUtils.getAttributeStrNotNull(row,"ENTE");
			cf			= StringUtils.getAttributeStrNotNull(row,"CF_ENTE");
			indEnte		= StringUtils.getAttributeStrNotNull(row,"IND_ENTE");	
			rif			= StringUtils.getAttributeStrNotNull(row,"RIF");
			prgAvv		= StringUtils.getAttributeStrNotNull(row,"PRGAVVSELEZIONE");
		}

             
		//formattazione pagina jsp
		String htmlStreamTop = StyleUtils.roundTopTable(false);
		String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
 
<html>
<head>
<title>Lista Lavoratori</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />
<script TYPE="text/javascript"> 

</script>

</head>

<body class="gestione" onload="rinfresca()">
<%
	// TESTATA
	InfCorrentiLav testata = new InfCorrentiLav(cdnLavoratore, user);
	testata.show(out);
%>
<p align="center">

<af:form action="AdapterHTTP" name="Frm1" method="GET" >

	<input name="PAGE" type="hidden" value="AS_DETTAGLIO_AVVIAMENTI_PAGE"/>
	<af:list moduleName="M_AS_LISTA_AVVIAMENTI_CONTESTUALE" />	

</af:form>
<%out.print(htmlStreamBottom);%>
</body>
</html>

