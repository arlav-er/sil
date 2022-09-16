<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                java.math.*, it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%    
	ProfileDataFilter filter = new ProfileDataFilter(user, "CMProspEsclusioniPage");
	boolean canView = filter.canView();
	if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}
          
  	String _page = (String) serviceRequest.getAttribute("PAGE"); 
  	String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);
  	    
  	String prgProspettoInf = serviceRequest.getAttribute("prgProspettoInf") == null ? "" : (String)serviceRequest.getAttribute("prgProspettoInf");
  	String cdnFunzione = (String) serviceRequest.getAttribute("cdnFunzione");
 	String mode = (String) serviceRequest.getAttribute("MODE");
 	String message_esclusione = (String) serviceRequest.getAttribute("MESSAGE_ESCLUSIONE");
 	
  	String codMonoStatoProspetto = "";
  	SourceBean prospetto = (SourceBean) serviceResponse.getAttribute("CMProspDettModule.ROWS.ROW");		
	if (prospetto != null) {	
		codMonoStatoProspetto = (String)prospetto.getAttribute("codMonoStatoProspetto");
	}
  	
  	boolean notEmptyIntermittentiList = false;
  	Vector intermittentiList = serviceResponse.getAttributeAsVector("CMProspIntermittentiListModule.ROWS.ROW");		
	if (intermittentiList != null && intermittentiList.size() > 0 ) {	
		notEmptyIntermittentiList = true;
	}

	String numIntermittenti = "0";  	
  	SourceBean dett = (SourceBean) serviceResponse.getAttribute("CMProspEsclusioniDettModule.ROWS.ROW");				
	if (dett != null) {	
		message_esclusione = "UPDATE";				
	}
	
	SourceBean calcolaInterm = (SourceBean) serviceResponse.getAttribute("CMProspCalcolaIntermittenti.ROWS.ROW");
	if (calcolaInterm != null) {	
		BigDecimal nIntermittenti = (BigDecimal)calcolaInterm.getAttribute("checkIntermittenti");
		if (nIntermittenti.compareTo(new BigDecimal(0)) > 0) {		
			numIntermittenti = calcolaInterm.getAttribute("numinterm") == null ? "" : ((BigDecimal)calcolaInterm.getAttribute("numinterm")).toString();
		}				
	}
  	
  	PageAttribs attributi = new PageAttribs(user, "CMProspEsclusioniPage");	
	
	boolean canModify 		= 	false;
	boolean readOnlyStr     = 	false; 
			
	canModify =	attributi.containsButton("AGGIORNA");   
	if (("N").equalsIgnoreCase(codMonoStatoProspetto) || ("S").equalsIgnoreCase(codMonoStatoProspetto) 
		|| ("V").equalsIgnoreCase(codMonoStatoProspetto) || ("U").equalsIgnoreCase(codMonoStatoProspetto)) {
    	canModify = false;
    } 		
	String fieldReadOnly = canModify ? "false" : "true";  
  	
%>


<script>
	   
function nuovoDettIntermittente() {
	
	var url = "AdapterHTTP?PAGE=CMProspIntermittentiPage";		
	url += "&MODULE=CMProspIntermittentiDettModule";
	url += "&PRGPROSPETTOINF=<%=prgProspettoInf%>";
	url += "&CDNFUNZIONE=<%=cdnFunzione%>"; 
	url += "&MESSAGE_ESCLUSIONE=<%=message_esclusione%>";
	url += "&MESSAGE=INSERT";
		 
	setWindowLocation(url);    
}
	
function aggiornaForm() {  
	var num = '<%=numIntermittenti%>';
	<%if (notEmptyIntermittentiList) { %>
		window.opener.document.Frm1.numIntermittenti.value = num;
		window.opener.document.Frm1.numIntermittentiCalc.value = num;
		<% 
		if (dett != null) { 
		%>
			window.opener.document.Frm1.MESSAGE.value = "UPDATE";
		<% 
		}
	  }	
	else {
	%>	
		window.opener.document.Frm1.numIntermittenti.value = 0;
		window.opener.document.Frm1.numIntermittentiCalc.value = 0;
	<%	
	}
	%>
} 	
 		
</script>

<html>
<head>
<title>Lista Lavoratori Intermittenti</title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 
 <af:linkScript path="../../js/" />  

</head>

<body onload="rinfresca();aggiornaForm()">

<center>
	<font color="green">
		<af:showMessages prefix="CMProspIntermittentiDeleteModule"/>
		<af:showMessages prefix="CMProspIntermittentiSaveModule"/>
    </font>
    <font color="red">
		<af:showErrors/>
  	</font>
</center>
	
	<af:list moduleName="CMProspIntermittentiListModule" canDelete="<%= canModify ? \"1\" : \"0\" %>" />  
		
		<table class="main">  
			<tr>
				<td>
					<% 
					if (canModify) {
					%>	
						<input type="button" class="pulsante" name="nuovo" value="Nuovo" onclick="nuovoDettIntermittente()"/>
				    	&nbsp;&nbsp;&nbsp;&nbsp;
				    <%
					}
					%>
					<input type="button" class="pulsante" name="chiudi" value="Chiudi" onclick="window.close()" />
				</td>
			</tr>
		</table>
</body>
</html>