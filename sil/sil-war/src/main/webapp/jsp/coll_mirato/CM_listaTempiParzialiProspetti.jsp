<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*, it.eng.sil.module.collocamentoMirato.constant.*,
                java.math.*, it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%  
	String _pageOpener = (String) serviceRequest.getAttribute("PAGEOPENER");
	ProfileDataFilter filter = new ProfileDataFilter(user, _pageOpener);
	boolean canView = filter.canView();
	if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}
          
  	String _page = (String) serviceRequest.getAttribute("PAGE"); 
    String _module = (String) serviceRequest.getAttribute("MODULE");
    boolean aggiornaOpener = false;
    if (!"CMProspTempiParzialiListModule".equals(_module)) {
    	aggiornaOpener = true; 
    }
  	String prgProspettoInf = serviceRequest.getAttribute("prgProspettoInf") == null ? "" : (String)serviceRequest.getAttribute("prgProspettoInf");
  	String codTipoPTDisabile = serviceRequest.getAttribute("codTipoPTDisabile") == null ? "" : (String)serviceRequest.getAttribute("codTipoPTDisabile");
  	String cdnFunzione = (String) serviceRequest.getAttribute("cdnFunzione");
 	String mode = (String) serviceRequest.getAttribute("MODE");
 	
  	String codMonoStatoProspetto = "";
  	String flgCompetenza = "";
  	SourceBean prospetto = (SourceBean) serviceResponse.getAttribute("CMProspDettModule.ROWS.ROW");		
	if (prospetto != null) {	
		codMonoStatoProspetto = (String)prospetto.getAttribute("codMonoStatoProspetto");
		flgCompetenza = (String)prospetto.getAttribute("flgCompetenza");
	}
	
	SourceBean tipoDisabile = (SourceBean) serviceResponse.getAttribute("CMProspDescrTipoDisabile.ROWS.ROW");
	String descrTipoDisabile = "";
	if (tipoDisabile != null) {	
		String codiceDisabile = (String)tipoDisabile.getAttribute("codice");
		if (codiceDisabile != null && codiceDisabile.equalsIgnoreCase(ProspettiConstant.CATEGORIA_DISABILE_TELELAVORO)) {
			descrTipoDisabile = "Telelavoro per quota parte dell'orario contrattuale";	
		}
		else {
			descrTipoDisabile = (String)tipoDisabile.getAttribute("descrizione");
			descrTipoDisabile = descrTipoDisabile + " a tempo parziale";
		}
	}	
	
	PageAttribs attributi = new PageAttribs(user, _pageOpener);	
	
	boolean canModify =	attributi.containsButton("AGGIORNA");    	
	if (("N").equalsIgnoreCase(codMonoStatoProspetto) || ("S").equalsIgnoreCase(codMonoStatoProspetto) 
		|| ("V").equalsIgnoreCase(codMonoStatoProspetto) || ("U").equalsIgnoreCase(codMonoStatoProspetto)
		|| ("N").equalsIgnoreCase(flgCompetenza)) {
    	canModify = false;
    }	
  	
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);  
  	
%>


<script>
	   
function nuovoTempoParziale() {
	
	var url = "AdapterHTTP?PAGE=CMProspTempiParzialiPage";		
	url += "&MODULE=CMProspTempiParzialiDettModule";
	url += "&PRGPROSPETTOINF=<%=prgProspettoInf%>";
	url += "&CDNFUNZIONE=<%=cdnFunzione%>"; 
	url += "&CODTIPOPTDISABILE=<%=codTipoPTDisabile%>";
	url += "&PAGEOPENER=<%=_pageOpener%>";
		 
	setWindowLocation(url);    
}
	
</script>

<html>
<head>
<title><%=descrTipoDisabile%></title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 
 <af:linkScript path="../../js/" />  

</head>

<body onload="rinfresca();<%if (aggiornaOpener) {%>window.opener.reloadMe()<%} %>">

<center>
	<font color="green">
		<af:showMessages prefix="CMProspTempiParzialiDeleteModule"/>
		<af:showMessages prefix="CMProspTempiParzialiSaveModule"/>
	</font>
    <font color="red">
		<af:showErrors/>
  	</font>
</center>

	<p class="titolo"><%=descrTipoDisabile%></p>
	
	<af:list moduleName="CMProspTempiParzialiListModule" canDelete="<%= canModify ? \"1\" : \"0\" %>" />  
		
		<table class="main">  
			<tr>
				<td>
					<% 
					if (canModify) {
					%>	
						<input type="button" class="pulsante" name="nuovo" value="Nuovo" onclick="nuovoTempoParziale()"/>
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