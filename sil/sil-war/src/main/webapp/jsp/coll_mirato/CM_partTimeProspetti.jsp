<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,java.math.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%         
	ProfileDataFilter filter = new ProfileDataFilter(user, "CMProspDettPage");
	boolean canView = filter.canView();
	if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}

  	String _page = (String) serviceRequest.getAttribute("PAGE");   
  	    
  	String prgProspettoInf = serviceRequest.getAttribute("prgProspettoInf") == null ? "" : (String)serviceRequest.getAttribute("prgProspettoInf");
  	String prgPartTimeProspetto = serviceRequest.getAttribute("prgPartTimeProspetto") == null ? "" : (String)serviceRequest.getAttribute("prgPartTimeProspetto");
   	String message = serviceRequest.getAttribute("message") == null ? "INSERT" : (String)serviceRequest.getAttribute("message");
  	String cdnFunzione = (String) serviceRequest.getAttribute("cdnFunzione");
 	String mode = (String) serviceRequest.getAttribute("MODE");
 	String message_esclusione = (String) serviceRequest.getAttribute("MESSAGE_ESCLUSIONE");
  	  	
  	String numLavoratori = "";
  	String decOreLavorate = "";
  	String decOreTotali = "";
  	String numPartTime = "";
  	
  	String codMonoStatoProspetto = "";
  	SourceBean prospetto = (SourceBean) serviceResponse.getAttribute("CMProspDettModule.ROWS.ROW");		
	if (prospetto != null) {	
		codMonoStatoProspetto = (String)prospetto.getAttribute("codMonoStatoProspetto");
	}
  	
  	SourceBean dett = (SourceBean) serviceResponse.getAttribute("CMProspPartTimeDettModule.ROWS.ROW");				
	if (dett != null) {	
		message = "UPDATE";					
		numLavoratori = dett.getAttribute("numLavoratori") == null ? "" : ((BigDecimal)dett.getAttribute("numLavoratori")).toString();
		//numPartTime = dett.getAttribute("numPartTime") == null ? "" : ((BigDecimal)dett.getAttribute("numPartTime")).toString();
		decOreLavorate = dett.getAttribute("decOreLavorate") == null ? "" : ((BigDecimal)dett.getAttribute("decOreLavorate")).toString();
		decOreTotali = dett.getAttribute("decOreTotali") == null ? "" : ((BigDecimal)dett.getAttribute("decOreTotali")).toString();
	}
	else {
		message = "INSERT";			
	}
	
	String token = "_TOKEN_" + _page.toUpperCase();
	String urlDiLista = (String)sessionContainer.getAttribute(token);
  	
  	PageAttribs attributi = new PageAttribs(user, "CMProspEsclusioniPage");	
	
	boolean canModify 		= 	false;
	boolean readOnlyStr     = 	false; 
			
	canModify     			=	attributi.containsButton("AGGIORNA");    		
	if (("N").equalsIgnoreCase(codMonoStatoProspetto) || ("S").equalsIgnoreCase(codMonoStatoProspetto) 
		|| ("V").equalsIgnoreCase(codMonoStatoProspetto) || ("U").equalsIgnoreCase(codMonoStatoProspetto)) {
    	canModify = false;    
    }
	String fieldReadOnly = canModify ? "false" : "true";  				
	
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>

<html>
<head>
<title>Part-time</title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

 <af:linkScript path="../../js/" />

<script language="javascript">

    function controllaCampi() {		
		
		if (!controllaFixedFloat('decOreLavorate', 7, 3)) {			
			return false;
		}
		
		if (!controllaFixedFloat('decOreTotali', 7, 3)) {			
			return false;
		}	
									
		return true;      
	}
	
	function tornaAllaLista() {
		var f;
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
		
		setWindowLocation("AdapterHTTP?<%= StringUtils.formatValue4Javascript(urlDiLista) %>");							
    }	

</script>

</head>

<body onload="rinfresca();">
<br/>


<%out.print(htmlStreamTop);%>

<p class="titolo">Dettaglio Part-Time</p>

<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="controllaCampi()">
<input type="hidden" name="PAGE" value="CMProspPartTimePage"/>
<input type="hidden" name="MODULE" value="CMProspPartTimeSaveModule"/>
<input type="hidden" name="MESSAGE" value="<%=message%>"/>
<input type="hidden" name="MODE" value="LISTA"/> 
<input type="hidden" name="MESSAGE_ESCLUSIONE" value="<%=message_esclusione%>"/> 
<input type="hidden" name="prgPartTimeProspetto" value="<%=prgPartTimeProspetto%>"/>
<input type="hidden" name="prgProspettoInf" value="<%=prgProspettoInf%>"/>
<input type="hidden" name="cdnfunzione" value="<%=cdnFunzione%>"/>  
<table class="main">				
	<tr>
		<td class="etichetta2" width="50%">Numero Lav.</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="integer" name="numLavoratori" validateOnPost="true" 
						size="6" value="<%=numLavoratori%>" maxlength="6" readonly="<%=fieldReadOnly%>" 
						required="true" title="Numero Lav."/>
	    </td> 
	</tr>
	<tr>
		<td class="etichetta2" width="50%">Ore lavorate</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="number" name="decOreLavorate" size="5" maxlength="11"
						value="<%=decOreLavorate%>" readonly="<%=fieldReadOnly%>" required="true" title="Ore lavorate"/>
	    </td> 
	</tr>
	<tr>
		<td class="etichetta2" width="50%">Ore Totali</td>
		<td class="campo2">
			<af:textBox classNameBase="input" type="number" name="decOreTotali" size="5" maxlength="11" 
						value="<%=decOreTotali%>" readonly="<%=fieldReadOnly%>" required="true" title="Ore Totali"/>
	    </td> 
	</tr>		
	<tr><td colspan="2">&nbsp;</td></tr>
	<tr>
		<td colspan="2">				   		
			<%
			if (canModify) {
				if (("UPDATE").equalsIgnoreCase(message)) {
			%>
					<input type="submit" class="pulsante" name="aggiorna" value="Aggiorna" />	
			<%
				}
				else {
			%>		
					<input type="submit" class="pulsante" name="inserisci" value="Inserisci" />			
			<%
				}
			}
			%>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="button" class="pulsante" name="torna" value="Torna alla lista" onclick="tornaAllaLista();" />
		</td>
	</tr>
</table> 
<%out.print(htmlStreamBottom);%>
</af:form>
</body>
</html>