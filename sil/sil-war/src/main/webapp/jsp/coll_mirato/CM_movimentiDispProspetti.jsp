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
	ProfileDataFilter filter = new ProfileDataFilter(user, "CMProspLavoratoriPage");
	boolean canView = filter.canView();
	if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}
          
  	String _page = (String) serviceRequest.getAttribute("PAGE"); 
  	String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);
  	    
  	String prgProspettoInf = serviceRequest.getAttribute("prgProspettoInf") == null ? "" : (String)serviceRequest.getAttribute("prgProspettoInf");
  	String prgAzienda = serviceRequest.getAttribute("PRGAZIENDA") == null ? "" : (String)serviceRequest.getAttribute("PRGAZIENDA");
  	String numAnnoRifProspetto = serviceRequest.getAttribute("NUMANNORIFPROSPETTO") == null ? "" : (String)serviceRequest.getAttribute("NUMANNORIFPROSPETTO");
  	String codProvincia = serviceRequest.getAttribute("CODPROVINCIA") == null ? "" : (String)serviceRequest.getAttribute("CODPROVINCIA");
  	String cdnFunzione = (String) serviceRequest.getAttribute("cdnFunzione");
 	String mode = (String) serviceRequest.getAttribute("MODE");
  	
  	PageAttribs attributi = new PageAttribs(user, "CMProspLavoratoriPage");	
	
	boolean canModify 		= 	false;
	boolean readOnlyStr     = 	false; 
			
	canModify =	attributi.containsButton("AGGIORNA");    		
	String fieldReadOnly = canModify ? "false" : "true";  
	
	String listPage = "1";
	if (serviceRequest.containsAttribute("LIST_PAGE")) {
		listPage = (String) serviceRequest.getAttribute("LIST_PAGE");
	}
	
	SourceBean movimentiRespose = (SourceBean) serviceResponse.getAttribute("CMPROSPMOVIMENTIDISPLISTMODULENEW");
	Vector rows = movimentiRespose.getAttributeAsVector("ROWS.ROW"); 
	
	boolean reload = false;
	for (int i=0; i<rows.size(); i++) {
		SourceBean rowLista = (SourceBean)rows.get(i);
		String prgMov = (String)rowLista.getAttribute("PRG_CHECK");		
		Vector result = serviceResponse.getAttributeAsVector("CMProspMovimentiDispSaveModuleNew.ROW");
		if (result.size() > 0) {
			int countSuccess = 0;
			for (int j=0; j<result.size(); j++) {
				SourceBean resultRow = (SourceBean)result.get(j);
				String codiceRit = (String)resultRow.getAttribute("CodiceRit");
				if (codiceRit != null && codiceRit.equals("0")) {
					countSuccess++;
				}
			}
			if (countSuccess == result.size()) {
				reload = true;
			}
		}
	}	
			  	
%>

    
<script>
<!--

function associa() {

<%
	if (rows.size() == 0) {
%>
		alert("Attenzione: non esistono movimenti disponibili!");
		return false;
<%
	} 
%>	
	return true;
}

function disableMovimenti() {	
	
	var reload = <%=reload%>;
	if (reload) {
		var url = "AdapterHTTP?PAGE=CMProspLavoratoriPage";
		url += "&PRGPROSPETTOINF=<%=prgProspettoInf%>";
		url += "&CDNFUNZIONE=<%=cdnFunzione%>";
		window.opener.location.replace(url);			
	}

}

//-->
</script>

<html>
<head>
<title>Movimenti disponibili</title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 
 <af:linkScript path="../../js/" />  
</head>

<body onload="rinfresca();disableMovimenti();">
<br/>
<br/>  
<center>
	<font color="green">
		<af:showMessages prefix="CMProspMovimentiDispSaveModuleNew"/>
    </font>
</center>
<center>
	<font color="red"><af:showErrors /></font>
</center>

<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="associa()">
<input type="hidden" name="PAGE" value="CMProspMovimentiDispPage"/>
<input type="hidden" name="MODULE" value="CMProspMovimentiDispSaveModuleNew"/>
<input type="hidden" name="MESSAGE" value="LIST_PAGE"/>
<input type="hidden" name="LIST_PAGE" value="<%=listPage%>"/>
<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>"/>
<input type="hidden" name="PRGPROSPETTOINF" value="<%=prgProspettoInf%>"/>
<input type="hidden" name="CODPROVINCIA" value="<%=codProvincia%>"/>
<input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"/>
<input type="hidden" name="NUMANNORIFPROSPETTO" value="<%=numAnnoRifProspetto%>"/>
	
<af:list moduleName="CMProspMovimentiDispListModuleNew" configProviderClass="it.eng.sil.module.collocamentoMirato.CMProspMovimentiDispListConfigNew" />  
	
<table class="main">	
	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td>					   			
			
			<input type="submit" class="pulsante" name="inserisci" value="Aggiorna" />			
			&nbsp;&nbsp;
			<input type="button" class="pulsante" name="torna" value="Chiudi" onclick="window.close();" />
		</td>
	</tr>
</table>
</af:form>		
</body>
</html>