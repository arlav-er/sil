<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*,
                java.math.*
                " %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<% 
	String nomeLav    	= "";
  	String cognomeLav 	= "";
  	String codFis		= "";
  	BigDecimal cdnLav	= null;
  	BigDecimal prgValIsee	= null;
	
	String htmlStreamTop 	= 	StyleUtils.roundTopTable(true);
	String htmlStreamBottom = 	StyleUtils.roundBottomTable(true);
  
  	Vector rows = serviceResponse.getAttributeAsVector("M_ASStoricoIsee.ROWS.ROW"); 
	  	for (int i = 0;i<rows.size();i++) {
			SourceBean rowVector = (SourceBean)rows.get(i); 
	  	
	  		nomeLav 	= (String)rowVector.getAttribute("NOME");
	  		cognomeLav 	= (String)rowVector.getAttribute("COGNOME");
	  		codFis 		= (String)rowVector.getAttribute("CF");
	  		cdnLav		= (BigDecimal)rowVector.getAttribute("CDNLAVORATORE");
	  		prgValIsee	= (BigDecimal)rowVector.getAttribute("PRGVALOREISEE");
	  	}
  

	  // NOTE: Attributi della pagina (pulsanti e link)
	  PageAttribs attributi = new PageAttribs(user, "StatoOccupazionalePage");
	  boolean canDelete=attributi.containsButton("aggiorna");
	  //E' stato disabilitato il pulsante di cancellazione sulla lista (momentaneamente)
	  canDelete=false;
%>

<html>
<head>
	<title>Inf Storiche Valore ISEE</title>
	<link rel="stylesheet" type="text/css" href=" ../../css/stili.css"/>
	<link rel="stylesheet" type="text/css" href=" ../../css/listdetail.css"/>
 	<af:linkScript path="../../js/" />
</head>

<body onload="checkError();" class="gestione">
<af:error/>
<p align="center">

<table class="main">
	<tr>
		<td><br/>
		</td>
	</tr>
	<tr>
		<td>
			<div style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
	  		&nbsp;&nbsp;Lavoratore: <b><%=cognomeLav%>&nbsp;<%=nomeLav%></b> &nbsp;&nbsp;codice&nbsp;fiscale: <b><%=codFis%></b>
	  		</div>
		</td>
	</tr>
</table>

<af:list moduleName="M_ASStoricoIsee" />

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
<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLav%>" />
<input type="hidden" name="PRGVALOREISEE" value="<%=prgValIsee%>" />

</body>
</html>