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
	
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 
		
	int cdnfunzione   = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");

	//Profilatura ------------------------------------------------
 
	ProfileDataFilter filter = new ProfileDataFilter(user, _page); 

	if (! filter.canView()){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
 	}
 	
  	sessionContainer.delAttribute("COMEFROM");
  	sessionContainer.setAttribute("COMEFROM","A");
  	
  	PageAttribs attributi = new PageAttribs(user, _page);  

	String  prgAzienda    = (String)serviceRequest.getAttribute("prgAzienda");
	String prgUnita = (String)serviceRequest.getAttribute("prgUnita");

%>

<html>
<head>
<title>Lista delle Convenzioni</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<af:linkScript path="../../js/"/>
<script language="javascript">	

	/*
	 * Torna alla pagina di ricerca
	 */
	function goBackRicerca() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

		var url = "AdapterHTTP?PAGE=CMRicercaAssunzioniPage" + "&cdnfunzione=<%=cdnfunzione%>";
		<%if (prgAzienda!=null) {%>
		url+="&prgAzienda=<%=prgAzienda%>";
		url+="&prgUnita=<%=prgUnita%>";
		<%}%>
		setWindowLocation(url);
	}
	<% if (prgAzienda!=null) {
       //Genera il Javascript che si occuperà di inserire i links nel footer
       		attributi.showHyperLinks(out, requestContainer,responseContainer,"");
       }%>
</script>
<script language="Javascript">
		<% if (prgAzienda!=null && prgUnita!=null) { %>
			window.top.menu.caricaMenuAzienda(<%=cdnfunzione%>, <%=prgAzienda%>, <%=prgUnita%>);
		<% } else { %>
			window.top.menu.location="AdapterHTTP?PAGE=MenuCompletoPage";
		<% }%>
	
</script>
</head>

<body class="gestione" onload="rinfresca()">

<af:showErrors />

<%
  	String txtOut = "";
    String valCF = (String) serviceRequest.getAttribute("strCodiceFiscale");
    String valPI = (String) serviceRequest.getAttribute("strPartitaIva");
    String valRS = (String) serviceRequest.getAttribute("strRagioneSociale");
    if((valCF != null && !valCF.equals("")) || (valPI != null && !valPI.equals("")) || (valRS != null && !valRS.equals(""))){
    	txtOut += "Azienda (";
    
		if(valCF != null && !valCF.equals(""))
   		{
   			txtOut += "Codice Fiscale <strong>"+ valCF +"</strong>, ";
   		}
    	if(valPI != null && !valPI.equals(""))
    	{
    		txtOut += "Partita IVA <strong>"+ valPI +"</strong>, ";
    	}
		if(valRS != null && !valRS.equals(""))
    	{
    		txtOut += "Ragione Sociale <strong>"+ valRS +"</strong>";
    	}
    	
    	txtOut += "); ";
    }    
    String valStato = (String) serviceRequest.getAttribute("descrStatoAsshid");
    if(valStato != null && !valStato.equals(""))
    {
    	txtOut += "Stato Assunzione  <strong>"+ valStato +"</strong>; ";
    }
    String valDa = (String) serviceRequest.getAttribute("datPrevista_Da");
    String valA = (String) serviceRequest.getAttribute("datPrevista_A");
    if((valDa != null && !valDa.equals("")) || (valA != null && !valA.equals(""))){
   		txtOut += "Data programmata";
 
    	if(valDa != null && !valDa.equals(""))
    	{
    		txtOut += " da <strong>"+ valDa +"</strong> ";
    	}
    	if(valA != null && !valA.equals(""))
    	{
    		txtOut += " a <strong>"+ valA +"</strong>; ";
    	}
    }
 %>
<p align="center">
<%if(txtOut.length() > 0)
  { txtOut = "Filtri di ricerca:<br/> " + txtOut;%>
    <table cellpadding="2" cellspacing="10" border="0" width="100%">
     <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
      <%out.print(txtOut);%>
     </td></tr>
    </table>
<%}%>

<af:form name="Frm1" method="POST" action="AdapterHTTP">

	<af:list moduleName="CM_LISTA_ASSUNZIONI" />

	<table class="main">  
		<tr>
			<td>
				<input type="button" class="pulsante" name="ricerca" value="Torna alla ricerca" onclick="goBackRicerca()" />
			</td>
		</tr>
	</table>

</af:form>

</body>
</html>