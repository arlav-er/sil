<%@ page contentType="text/html;charset=utf-8"%>

<%@ page
	import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%@ taglib uri="aftags" prefix="af"%>

<%
	// viene profilata la pagina di ricerca tramite MENU
	String _page = "ElencoAzCondPage";
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
    PageAttribs attributi = new PageAttribs(user, _page);
    String _funzione=(String) serviceRequest.getAttribute("cdnFunzione");
    String  cdnLavoratore    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");

	
	if (!filter.canView()) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	} 
	 String flagCondizionalita= StringUtils.getAttributeStrNotNull(serviceRequest, "CONDIZIONALITA");
	 String flagNegativi= StringUtils.getAttributeStrNotNull(serviceRequest, "NEGATIVI");
	 String descrProgramma	= StringUtils.getAttributeStrNotNull(serviceRequest, "DESCRPROGRAMMA");
 	 String prgColloquio= StringUtils.getAttributeStrNotNull(serviceRequest, "PRGCOLLOQUIO");
	 String htmlStreamTop = StyleUtils.roundTopTable(false);
	 String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
<title>Azioni per eventi di condizionalità</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />

<script type="text/Javascript">
	if (window.top.menu != undefined){
			window.top.menu.location="AdapterHTTP?PAGE=MenuCompletoPage";
	}

 
	function AggiornaForm (prgColl,prgPerc,  dataInizio, dataFine, descrProg, descrAtt, dataAvvio, dataEff, descrEsito) {
       try{
    	   window.opener.document.Frm1.prgColloquio.value = prgColl;
    	   window.opener.document.Frm1.prgPercorso.value = prgPerc;
    	   window.opener.document.Frm1.programma.value = descrProg;
    	   window.opener.document.Frm1.dataInizioProg.value = dataInizio;
    	   window.opener.document.Frm1.dataFineProg.value = dataFine;
    	   window.opener.document.Frm1.attivita.value = descrAtt;
    	   window.opener.document.Frm1.dataAvvio.value = dataAvvio;
    	   window.opener.document.Frm1.dataConclusione.value = dataEff;
    	   window.opener.document.Frm1.esito.value = descrEsito;
    	   window.close();
       }
		catch(err) {
			
		}
	  
	}
	
 
</script>

</head>
<body class="gestione" onload="rinfresca();">
<br>

<p class="titolo">Azioni per eventi di condizionailtà</p>
	
	<af:form action="AdapterHTTP" method="POST"	name="Frm1">
	<%out.print(htmlStreamTop);%>
	<p align="left" style="font-weight: bold;"><%= descrProgramma %></p>
	
	<table class="main" border="0">
			      		<tr>
			       				<td class="etichetta" nowrap>
			       					<input type="checkbox" name="NEGATIVI"
							 		<%=(flagNegativi != null && flagNegativi.equals("on")) ? "checked='checked'" : ""%> /> Solo con esiti negativi&nbsp;&nbsp;
			       				</td>
			       				<td class="etichetta" nowrap>
			       					<input type="checkbox" name="CONDIZIONALITA"
										 <%=(flagCondizionalita != null && flagCondizionalita.equals("on")) ? "checked='checked'" : ""%> /> Solo con eventi di condizionalità
			       				</td>
			        	</tr>
			         
	 <tr><td  colspan="2" align="center">&nbsp;</td></tr>
	    <tr>
	    	<td  colspan="2" align="center">
		    	<input class="pulsanti" type="submit" name="cerca" value="Applica Filtri" />
	        </td>
	    </tr>
	 </table>
	    <input type="hidden" name="PAGE" value="ElencoAzCondPage"/>
	    <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
	    <input type="hidden" name="PRGCOLLOQUIO" value="<%=prgColloquio%>"/>
	    <input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>"/>
</table>
	</af:form>
	<%out.print(htmlStreamBottom);%>	
<br>
	 <center><table class="main">
	 	<af:JSButtonList moduleName="M_ListaAzioniCondizionalita" jsSelect="AggiornaForm" 
	 	configProviderClass="it.eng.sil.module.condizionalita.rdc.DynamicConfigAzioniCondizionalita" 
						 getBack="false"/>
	 
	<br />
	</center>
	<center><button onclick="window.close()" class="pulsanti">Chiudi</button></center>

</body>
</html>

