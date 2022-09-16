<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                java.math.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%         
  	
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
	
  	String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");  	
  	//String prgGradNominativo = serviceRequest.getAttribute("PRGGRADNOMINATIVO").toString();
    
  	String datAnzianita68 = "";
  	String numMesiAnz = "";
  	String descrTipoIscr = "";
  	String numPercInvalidita = "";
  	String numCaricoFam = "";
  	String numReddito = "";
  	String dataPunteggio = "";
  	String flagPatente = "";
  	String codgradocapacitaloc = "";
  	String codMonoTipoGrad = "";
  	
  	SourceBean punteggioSB = (SourceBean) serviceResponse.getAttribute("CMDettaglioDatiPunteggioGradAnnModule.ROWS.ROW");
	if (punteggioSB != null) {
		codMonoTipoGrad = punteggioSB.getAttribute("CODMONOTIPOGRAD") == null? "" : (String)punteggioSB.getAttribute("CODMONOTIPOGRAD");
		datAnzianita68 = punteggioSB.getAttribute("DATANZIANITA68") == null? "" : (String)punteggioSB.getAttribute("DATANZIANITA68");
	  	numMesiAnz = punteggioSB.getAttribute("NUMMESIANZ") == null? "" : ((BigDecimal)punteggioSB.getAttribute("NUMMESIANZ")).toString();
	  	descrTipoIscr = punteggioSB.getAttribute("DESCRTIPOISCR") == null? "" : (String)punteggioSB.getAttribute("DESCRTIPOISCR");
	  	numPercInvalidita = punteggioSB.getAttribute("NUMPERCINVALIDITA") == null? "" : ((BigDecimal)punteggioSB.getAttribute("NUMPERCINVALIDITA")).toString();
	  	numCaricoFam = punteggioSB.getAttribute("CARICO") == null? "" : (String)punteggioSB.getAttribute("CARICO");
	  	numReddito = punteggioSB.getAttribute("NUMREDDITO") == null? "" : ((BigDecimal)punteggioSB.getAttribute("NUMREDDITO")).toString();
	  	dataPunteggio = punteggioSB.getAttribute("DATAPUNTEGGIO") == null? "" : (String)punteggioSB.getAttribute("DATAPUNTEGGIO");	  	
 		flagPatente = punteggioSB.getAttribute("FLGPATENTE") == null? "" : (String)punteggioSB.getAttribute("FLGPATENTE");
  		codgradocapacitaloc = punteggioSB.getAttribute("CODGRADOCAPACITALOC") == null? "" : (String)punteggioSB.getAttribute("CODGRADOCAPACITALOC");
	}
  	
  	
  	String qs = request.getParameter("QUERY_STRING"); 
%>


<script>  
	
<%if(qs!=null && !qs.equals("")) {%>
	
	function indietro(){
		window.location="AdapterHTTP?<%=qs%>";		
	}
	
<%}%>

</script> 

<html>
<head>
<title></title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 <af:linkScript path="../../js/" />
</head>

<body onload="rinfresca()">


<%     
        InfCorrentiLav _testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
        _testata.setSkipLista(true);
        _testata.show(out);
%>
	
	<af:list moduleName="CMDettaglioPunteggioGradAnnLavoratoreModule" skipNavigationButton="1"/>

	<br>       	
<%out.print(htmlStreamTop);%>		
	<table cellpadding="3" align="center" cellspacing="3" width="400">
		<tr>   
			<td>&nbsp;</td>
			<td class="campo_readFree" valign="top">Punteggio reale <br> (<%=dataPunteggio%>)</td>			
		</tr>
		<tr>
			<td class="etichetta">Data Anzianità</td>
			<td class="campocentrato" ><%=datAnzianita68%></td>
		</tr>
		<tr>
			<td class="etichetta">Mesi anzianità alla data di riferimento</td>
			<td class="campocentrato"><%=numMesiAnz%></td>
		</tr>
		<tr> 
			<td class="etichetta">Tipo Iscrizione</td>
			<td class="campocentrato"><%=descrTipoIscr%></td>			
		</tr>
		<tr>
			<td class="etichetta">Percentuale invalidante/categoria</td>
			<td class="campocentrato"><%=numPercInvalidita%></td>
		</tr>
		<tr>
			<td class="etichetta">Carico familiare e data della dichiarazione</td>
			<td class="campocentrato"><%=numCaricoFam%></td>			
		</tr>
		<tr>
			<td class="etichetta">Reddito CM</td>
			<td class="campocentrato"><%=numReddito%></td>
		</tr>
		<%
		if (("D").equalsIgnoreCase(codMonoTipoGrad)) {
		%>
		<tr>
			<td class="etichetta">Patente</td>
			<td class="campocentrato"><%=flagPatente%></td>				
		</tr>
		<tr>
			<td class="etichetta">Locomozione</td>
			<td class="campocentrato"><%=codgradocapacitaloc%></td>				
		</tr>
		<%
		}
		%>		
	</table>  
<%out.print(htmlStreamBottom);%>


</body>
</html>