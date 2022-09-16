<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ page contentType="text/html;charset=utf-8"	
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
	ProfileDataFilter filter = new ProfileDataFilter(user, "CMProspPostiDispPage");
	boolean canView = filter.canView();
	if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}

	String _page = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 
	String prgProspettoInf = "";
	String prgAzienda = "";
	String prgUnita = "";
	String codProvincia = "";
	String codRegioneProspetto = "";
	String codMonoStatoProspetto = "";
	String flgCompetenza = "S";
	
	InfCorrentiAzienda infCorrentiAzienda= null;	
	int cdnfunzione   = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");	 
	Testata operatoreInfo 	= 	null;  
	Linguette l = null;
	
	String codStatoAtto = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"codStatoAtto");
	
	SourceBean prospetto = (SourceBean) serviceResponse.getAttribute("CMProspDettModule.ROWS.ROW");		
	if (prospetto != null) {	
		prgProspettoInf = prospetto.getAttribute("prgProspettoInf") == null? "" : ((BigDecimal)prospetto.getAttribute("prgProspettoInf")).toString();
		prgAzienda = prospetto.getAttribute("prgAzienda") == null? "" : ((BigDecimal)prospetto.getAttribute("prgAzienda")).toString();
		prgUnita = prospetto.getAttribute("prgUnita") == null? "" : ((BigDecimal)prospetto.getAttribute("prgUnita")).toString();
		codProvincia = (prospetto.getAttribute("codprovincia")).toString();
		codMonoStatoProspetto = (String)prospetto.getAttribute("codMonoStatoProspetto");
		flgCompetenza = StringUtils.getAttributeStrNotNull(prospetto,"flgCompetenza");
		codRegioneProspetto = StringUtils.getAttributeStrNotNull(prospetto,"codregione");
	}		
		
	//LINGUETTE		
	l = new Linguette( user,  cdnfunzione, _page, new BigDecimal(prgProspettoInf), codStatoAtto);
	l.setCodiceItem("PRGPROSPETTOINF");
		
	//info dell'unità aziendale	
	if( prgAzienda != null && prgUnita!=null && !prgAzienda.equals("") && !prgUnita.equals("") ) {	
  		infCorrentiAzienda= new InfCorrentiAzienda(sessionContainer, prgAzienda, prgUnita);   	
  		infCorrentiAzienda.setPaginaLista("CMProspListaPage");   	
  	}  		  	
		     	    	
	PageAttribs attributi = new PageAttribs(user, "CMProspPostiDispPage");	
	boolean canModify 		= 	false;
	boolean readOnlyStr     = 	false; 
			
	canModify =	attributi.containsButton("AGGIORNA");    		
	if (("N").equalsIgnoreCase(codMonoStatoProspetto) || ("S").equalsIgnoreCase(codMonoStatoProspetto) 
		|| ("V").equalsIgnoreCase(codMonoStatoProspetto) || ("U").equalsIgnoreCase(codMonoStatoProspetto)
		|| ("N").equalsIgnoreCase(flgCompetenza)) {
    	canModify = false;
    } 
    
	//nel caso di provincia esterna si disabilita tutto
	InfoProvinciaSingleton provincia = InfoProvinciaSingleton.getInstance(); 
	String codiceProv = provincia.getCodice();
	String flgPoloReg = provincia.getFlgPoloReg();
	String codiceReg = provincia.getCodiceRegione();
	if (flgPoloReg == null || flgPoloReg.equals("") || flgPoloReg.equalsIgnoreCase("N")) {
		if (!(codiceProv).equalsIgnoreCase(codProvincia)) {
			canModify = false;
		}	
	}
	else {
		if (!(codiceReg).equalsIgnoreCase(codRegioneProspetto)) {
			canModify = false;
		}	
	}	
	
	String fieldReadOnly = canModify ? "false" : "true";  
	
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
<title>Dettaglio Prospetto</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<af:linkScript path="../../js/"/>

<script>
	   
function nuovo() {
	
	var url = "AdapterHTTP?PAGE=CMProspPostiDispPage";		
	url += "&MODULE=CMProspPostiDettModule";
	url += "&PRGPROSPETTOINF=<%=prgProspettoInf%>";
	url += "&CDNFUNZIONE=<%=cdnfunzione%>";
	url += "&codStatoAtto=<%=codStatoAtto%>";
	url += "&MESSAGE=INSERT";
		 
	setWindowLocation(url);    
}	
 
function movimentiDisponibili() {

}
 		
</script>
<SCRIPT TYPE="text/javascript">
 
  window.top.menu.caricaMenuAzienda(<%=cdnfunzione%>, <%=prgAzienda%>, <%=prgUnita%>);
  
</SCRIPT> 
</head>

<body class="gestione" onload="rinfresca();">
<%
if(infCorrentiAzienda != null) {
%>
	<div id="infoCorrAz" style="display:"><%infCorrentiAzienda.show(out); %></div>
<%
}

l.show(out);
%>

<center>
	<font color="green">
		<af:showMessages prefix="CMProspPostiSaveModule"/>
		<af:showMessages prefix="CMProspPostiDeleteModule"/>
    </font>
</center>
<center>
	<font color="red"><af:showErrors /></font>
</center>
  
<af:list moduleName="CMProspPostiListModule" canDelete="<%= canModify ? \"1\" : \"0\" %>" />

<% 
if (canModify) {
%>
	<table class="main">  
		<tr>
			<td>
				<input type="button" class="pulsante" name="nuovo" value="Nuovo" onclick="nuovo()"/>		    
			</td>
		</tr>
	</table>
<%
}
%>
<br/>

</body>
</html>