<!-- 

	NON SERVE PIU!!!!  
	Eliminata in data 16/04/2004 
	RIESUMATA IN DATA 10/10/2004 :))))	

-->
<!--
REFRESH_MAIN : se presente e true bisognera' ricaricare la pagina main che ha aperto la pop-up di stampa
-->



<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="java.util.*,
				javax.xml.datatype.XMLGregorianCalendar,
				java.math.*,
				it.eng.afExt.utils.*,
				it.eng.sil.security.*,
				it.gov.lavoro.servizi.servizicoap.richiestasap.*,
				com.engiweb.framework.base.*,
				it.eng.sil.util.*"
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>
				
<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/getCommonObjects.inc" %>

<%boolean aggiornaMain = false;
boolean visualizzaInfoSAP = false;
boolean isSapAssente = false;
String codMinSapCurrent = null;
String refreshMain = request.getParameter("REFRESH_MAIN"); 
String inibisciIndietro  = (String) request.getParameter("INIBISCI_INDIETRO");
boolean flgInibisciIndietro = (inibisciIndietro!=null && !inibisciIndietro.equalsIgnoreCase(""));
aggiornaMain = refreshMain !=null && refreshMain.equals("true");
String datTrasfCompetenza = request.getParameter("DATATRASFCOMP");
String msgTrasferimento = "Data di presa in carico per trasferimento " + datTrasfCompetenza;
String cdnLavoratoreRequest = null;
String queryStringApp = null;
boolean isPageTrasferimentoIn = false;
boolean isOperationTrasferimentoOK = false;
boolean integraTrasfDIDSAP = false;
String provenienzaChiamata = "";
boolean provenienzaVerificaSAP = false;
if (request.getParameter("TRASFVERIFICASAP") != null) {
	provenienzaVerificaSAP = true;
	cdnLavoratoreRequest = (String)request.getParameter("CDNLAVORATORE");
}
if (request.getParameter("PROVENIENZA") != null && request.getParameter("PROVENIENZA").toString().equalsIgnoreCase("STORICOTRASF")) {
	provenienzaChiamata = "STORICOTRASF";
	isPageTrasferimentoIn = true;	
	isOperationTrasferimentoOK = true;
	cdnLavoratoreRequest = (String)request.getParameter("CDNLAVORATORE");
}
else {
	queryStringApp = request.getParameter("QUERY_STRING");
	if (queryStringApp != null) {
		StringTokenizer stApp = new StringTokenizer(queryStringApp, "&");
		while (stApp.hasMoreTokens()) {
		    String parApp = (String)stApp.nextToken();
		    int iApp = parApp.indexOf("=");
		    if (iApp < 0) continue;
		    String valoreParApp = parApp.substring(0,iApp);
		    if (valoreParApp.equalsIgnoreCase("PAGE")) {
		    	String nomePage = parApp.substring(iApp+1);
		    	if (nomePage.equalsIgnoreCase("TrasferimentoPage")) {
		    		isPageTrasferimentoIn = true;
		    	}
		    }
		    else {
		    	if (valoreParApp.equalsIgnoreCase("trasferisci")) {
		    		String esitoOperazione = parApp.substring(iApp+1);
		    		if (esitoOperazione.equalsIgnoreCase("true")) {
		    			isOperationTrasferimentoOK = true;
		        	}
		    	}
		    	else {
		    		if (valoreParApp.equalsIgnoreCase("CDNLAVORATORE")) {
		    			cdnLavoratoreRequest = parApp.substring(iApp+1);
		    		}
		    	}	
		    }
		}
	}
}
integraTrasfDIDSAP = (isPageTrasferimentoIn && isOperationTrasferimentoOK);
//LavoratoreType lavT =null;
ListaDIDType lavT1 = null; 
if (request.getParameter("INVIASAPTITOLARIETA") != null || request.getParameter("INVIASAPSOLOTITOLARIETA") != null) { 
	boolean prendiSapTit = serviceResponse.containsAttribute("M_InviaSapPrendiTitolarieta.SAPTITOLARIETAINVIATA");
	boolean inviaSoloTit  = serviceResponse.containsAttribute("M_InviaSapSoloTitolarieta.SAPSOLOTITOLARIETAINVIATA");
	if(!prendiSapTit && !inviaSoloTit){
		codMinSapCurrent = (String) request.getParameter("CODMINSAP");
		integraTrasfDIDSAP = false;
   		visualizzaInfoSAP = true;
	}
	else if(prendiSapTit || inviaSoloTit){
		codMinSapCurrent = (String) request.getParameter("CODMINSAP");
		integraTrasfDIDSAP = false;
		visualizzaInfoSAP = false;
		isSapAssente = false;
	}
}
else {
	if (request.getParameter("TRASFVERIFICASAP") != null) {
		if(serviceResponse.containsAttribute("M_CCD_GetSituazioneSap.CODMINSAPWS")){
			codMinSapCurrent = (String) serviceResponse.getAttribute("M_CCD_GetSituazioneSap.CODMINSAPWS");
	   		 
	   	}
		if(serviceResponse.containsAttribute("M_CCD_GetSituazioneSap.SAPWS") && serviceResponse.getAttribute("M_CCD_GetSituazioneSap.SAPWS")!=null){
	  		//lavT =  (LavoratoreType) serviceResponse.getAttribute("M_CCD_GetSituazioneSap.SAPWS");
			lavT1 =  (ListaDIDType) serviceResponse.getAttribute("M_CCD_GetSituazioneSap.SAPWS");
	    }
	   	if(serviceResponse.containsAttribute("M_CCD_GetSituazioneSap.ESITO_SAP")){
	   		integraTrasfDIDSAP = false;
	   		visualizzaInfoSAP = true;
	   	}
	   	else {
	   		integraTrasfDIDSAP = true;	
	   	}
	   	//if (lavT == null) {
	   	//	isSapAssente = true;	
	   	//}
	   	if (lavT1 == null) {
	   		isSapAssente = true;	
	   	}
	}
}
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);

String htmlStreamTopCoop = StyleUtils.roundTopTable("prof_ro_coop");
String htmlStreamBottomCoop = StyleUtils.roundBottomTable("prof_ro_coop");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
<link rel="stylesheet" href="../../css/stiliTemplate.css" type="text/css">

<script language="JavaScript">
	var provPaginaChiamante = "<%=provenienzaChiamata%>";
	var provenienzaVerificaSAP = "<%=provenienzaVerificaSAP%>";
	function indietro(){		
		window.parent.location="AdapterHTTP?<%= getParameters(request)%>";	
	}
	
	function customFieldEnabledDisabled(objCampo, enabled) {
		// Cambio lo stile dell'oggetto
		var className = objCampo.className;
		var pos = className.indexOf("Disabled");
		if (enabled) {
			if (pos >= 0) {
				// Rimuovo il "Disabled" (se c'\u00E8)
				className = className.substring(0,pos);
			}
		}
		else {
			if (pos == -1) {
				// Aggiungo il "DIsabled" (se non c'\u00E8 gi\u00E0)
				className += "Disabled";
			}
		}
		objCampo.className = className;
	}
	
	function callVerificaSAP(){
		var f;
      	f = "AdapterHTTP?PAGE=ReportTopPage";
      	if (provPaginaChiamante != "") {
      		f = f + "&PROVENIENZA=" + provPaginaChiamante + "&CDNLAVORATORE=<%=cdnLavoratoreRequest%>&trasferisci=true";
      	}
      	else {
      		f = f + "&QUERY_STRING=<%=queryStringApp%>";
      	}
      	f = f + "&REFRESH_MAIN=<%=refreshMain%>";
      	f = f + "&INIBISCI_INDIETRO=<%=inibisciIndietro%>";
      	f = f + "&DATATRASFCOMP=<%=datTrasfCompetenza%>";
      	f = f + "&TRASFVERIFICASAP=true";

      	if (document.getElementById("btnIDVerificaSap") != null) {
      		customFieldEnabledDisabled(document.getElementById("btnIDVerificaSap"), false);
      	}
	  	document.location = f;
	}

	function callInviaSAPTitolarieta(){
		var f;
      	f = "AdapterHTTP?PAGE=ReportTopPage";
      	if (provPaginaChiamante != "" || provenienzaVerificaSAP) {
      		f = f + "&CDNLAVORATORE=<%=cdnLavoratoreRequest%>&trasferisci=true";
      		if (provPaginaChiamante != "") {
      			f = f + "&PROVENIENZA=" + provPaginaChiamante;	
      		}	
      	}
      	else {
      		f = f + "&QUERY_STRING=<%=queryStringApp%>";
      	}
      	f = f + "&REFRESH_MAIN=<%=refreshMain%>";
      	f = f + "&INIBISCI_INDIETRO=<%=inibisciIndietro%>";
      	f = f + "&DATATRASFCOMP=<%=datTrasfCompetenza%>";
      	f = f + "&CODMINSAP=<%=codMinSapCurrent%>";
      	f = f + "&INVIASAPSOLOTITOLARIETA=true";

      	if (document.getElementById("btnIDTitolarieta") != null) {
      		customFieldEnabledDisabled(document.getElementById("btnIDTitolarieta"), false);
      	}
      	if (document.getElementById("btnIDTitolarietaDid") != null) {
      		customFieldEnabledDisabled(document.getElementById("btnIDTitolarietaDid"), false);
      	}
	  	document.location = f;
	}

	function callInviaSAPTitPresaCarico(dataDidMin){
		var f;
      	f = "AdapterHTTP?PAGE=ReportTopPage";
      	if (provPaginaChiamante != "" || provenienzaVerificaSAP) {
      		f = f + "&CDNLAVORATORE=<%=cdnLavoratoreRequest%>&trasferisci=true";
      		if (provPaginaChiamante != "") {
      			f = f + "&PROVENIENZA=" + provPaginaChiamante;	
      		}	
      	}
      	else {
      		f = f + "&QUERY_STRING=<%=queryStringApp%>";
      	}
      	f = f + "&REFRESH_MAIN=<%=refreshMain%>";
      	f = f + "&INIBISCI_INDIETRO=<%=inibisciIndietro%>";
      	f = f + "&INVIASAPTITOLARIETA=true";
      	f = f + "&DATATRASFCOMP=<%=datTrasfCompetenza%>";
      	f = f + "&DATADID=" + dataDidMin;

      	if (document.getElementById("btnIDTitolarieta") != null) {
      		customFieldEnabledDisabled(document.getElementById("btnIDTitolarieta"), false);
      	}
      	if (document.getElementById("btnIDTitolarietaDid") != null) {
      		customFieldEnabledDisabled(document.getElementById("btnIDTitolarietaDid"), false);
      	}
	  	document.location = f;
	}	

	
</script>
</head>
<body class="gestione" <%= aggiornaMain ? "onunload='parent.window.opener.top.main.location.reload()'":""%>>
<p>
	<font color="green">
		<af:showMessages prefix="M_InviaSapPrendiTitolarieta"/>
		<af:showMessages prefix="M_InviaSapSoloTitolarieta"/>
  	</font>
  	<font color="red"><af:showErrors /></font>

</p>
<p>
  	<font color="navy" >
		<ul id="ulMessageWarning" style="display:none"><li id="messageWarning"></li></ul>
	</font>
</p>
<%if (integraTrasfDIDSAP) {%>
	<%out.print(htmlStreamTop);%>
	<table class="main">
  	<tr>
	<td class="titolo" colspan="2">ATTENZIONE, per completare l'operazione e' necessario acquisire la titolarita' della SAP verificando la situazione attuale.
	</td>
   	</tr>
   	<tr>
	<td align="center">
		<input class="pulsanti" type="button" name="btnVerificaSap" value="Verifica SAP" onClick="callVerificaSAP();" id="btnIDVerificaSap"/>&nbsp;&nbsp;
	</td>
	</tr>
	</table>
	<%out.print(htmlStreamBottom);%>
<%} else {
	if (visualizzaInfoSAP) {%>
		<%out.print(htmlStreamTopCoop);%>
		<table class="maincoop">
		<%if(lavT1!=null){ %>
		 	<tr>
				<td class="etichettacoop" nowrap>Ente Titolare SAP</td>
				<td class="campocoop">
					<b><%=lavT1.getCodiceentetit()%>&nbsp;-&nbsp;</b>
					<af:comboBox name="descrEnteSap" classNameBase="input"
								moduleName="COMBO_ENTETIT" 
								selectedValue="<%=lavT1.getCodiceentetit()%>" 
								addBlank="true"
								disabled ="true"
								required="false" />
				</td>
			</tr>
	 		<% 
	 		XMLGregorianCalendar dataDichSap = null;
	 		XMLGregorianCalendar dataEventoDidSap = null;
	 		String indiceProfilingSta = null;
			//Statoinanagrafe statoAn = null;
			
			dataDichSap = lavT1.getDisponibilita();
			dataEventoDidSap = lavT1.getDataevento();
			if (lavT1.getIndiceprofiling() != null) {
				indiceProfilingSta = lavT1.getIndiceprofiling().setScale(2, BigDecimal.ROUND_DOWN).toString();	
			}
	 		//if(lavT.getDatiamministrativi()!=null && lavT.getDatiamministrativi().getStatoinanagrafe()!=null){
	 		//	statoAn= lavT.getDatiamministrativi().getStatoinanagrafe();
	 		//	dataDichSap = 	statoAn.getDisponibilita();
	 		//	dataEventoDidSap = statoAn.getDataevento();
	 		//	if (statoAn.getIndiceprofiling() != null) {
			//		indiceProfilingSta = statoAn.getIndiceprofiling().setScale(2, BigDecimal.ROUND_DOWN).toString();	
			//	}
	 		//}
	 		%>
			<tr>
				<td class="etichettacoop" nowrap>Data dichiarazione</td>
				<td class="campocoop">
					<af:textBox name="dataDicSap" type="date" classNameBase="input"
								value="<%=DateUtils.formatXMLGregorian(dataDichSap)%>"
								size="12" maxlength="10"
								validateOnPost="true"
								readonly="true" 
								required="false" />
				</td>
			</tr>
			<tr>
				<td class="etichettacoop" nowrap>Indice profiling</td>
				<td class="campocoop">
					<af:textBox name="indiceProfDatiAmm" type="text" classNameBase="input"
								value="<%=Utils.notNull(indiceProfilingSta)%>"
								size="40" maxlength="101"
								readonly="true" 
								required="false" />
	
				</td>
			</tr>
			<tr>
			<td class="etichettacoop" nowrap>Data Ultimo Evento DID</td>
			<td class="campocoop">
	 			<af:textBox name="datEvDidSap" type="date" classNameBase="input"
							value="<%=DateUtils.formatXMLGregorian(dataEventoDidSap)%>"
							size="12" maxlength="10"
							validateOnPost="true"
							readonly="true" 
							required="false" />
			</td>
			</tr>
			<%if (serviceResponse.containsAttribute("M_CheckPrendiTitolarieta.PRENDIINCARICO") || 
				  serviceResponse.containsAttribute("M_CheckPrendiTitolarieta.PRENDITITOLARIETA")) {%>
				<%if (dataDichSap != null) {%>
					<tr>
						<td class="etichettacoop">Attività</td>
						<td class="campocoop"><b><%=Utils.notNull(serviceResponse.getAttribute("M_CheckPrendiTitolarieta.ATTIVITA"))%>
			                    - <af:comboBox name="attivitaInCarico" multiple="false"
			                        moduleName="COMBO_ATTIVITA_POLATT" disabled="true"
			                        classNameBase="input"
			                        selectedValue='<%=Utils.notNull(serviceResponse.getAttribute("M_CheckPrendiTitolarieta.ATTIVITA"))%>' /></b>
			          	</td>
					</tr>
					<tr>
						<td class="etichettacoop">Denominazione</td>
						<td class="campocoop"><b><%=Utils.notNull(serviceResponse.getAttribute("M_CheckPrendiTitolarieta.DENOMINAZIONE"))%></b>
						</td>
					</tr>
					<tr>
						<td class="etichettacoop">Data proposta</td>
						<td class="campocoop"><b><%=Utils.notNull(serviceResponse.getAttribute("M_CheckPrendiTitolarieta.DATAPROPOSTA"))%></b></td>
					</tr>
					<tr>
						<td class="etichettacoop">Data inizio</td>
						<td class="campocoop"><b><%=Utils.notNull(serviceResponse.getAttribute("M_CheckPrendiTitolarieta.DATAINIZIO"))%></b>
						</td>
					</tr>
					<tr>
						<td class="etichettacoop">Data fine</td>
						<td class="campocoop"><b><%=Utils.notNull(serviceResponse.getAttribute("M_CheckPrendiTitolarieta.DATAFINE"))%></b></td>
					</tr>
					<tr>
						<td class="etichettacoop">Descrizione</td>
						<td class="campocoop"><b><%=Utils.notNull(serviceResponse.getAttribute("M_CheckPrendiTitolarieta.DESCRIZIONE"))%></b>
						</td>
					</tr>
					<tr>
						<td class="etichettacoop">Titolo Progetto</td>
						<td class="campocoop"><b><%=Utils.notNull(serviceResponse.getAttribute("M_CheckPrendiTitolarieta.TITOLOPROGETTO"))%>
								- <af:comboBox name="progettoInCarico" multiple="false"
									moduleName="COMBO_TITOLOPROGETTO" disabled="true"
									classNameBase="input"
									selectedValue='<%=Utils.notNull(serviceResponse.getAttribute("M_CheckPrendiTitolarieta.TITOLOPROGETTO"))%>' /></b></td>
					</tr>
					<tr>
						<td class="etichettacoop">Codice Ente Promotore</td>
						<td class="campocoop"><b><%=Utils.notNull(serviceResponse.getAttribute("M_CheckPrendiTitolarieta.ENTEPROMOTORE"))%></b></td>			
					</tr>
				<%}%>
			<%}%>
			<tr>
			<td colspan="2">
			<%if (serviceResponse.containsAttribute("M_CheckPrendiTitolarieta.PRENDITITOLARIETA")) {%>	
				<input type="button" class="pulsanti" name="btnTitolarita" value="Prendi solo titolarità SAP" 
					onClick="callInviaSAPTitolarieta();" id="btnIDTitolarieta"/>&nbsp;&nbsp;
			<%}%>
			<%if (dataDichSap != null && serviceResponse.containsAttribute("M_CheckPrendiTitolarieta.PRENDIINCARICO")) {%>
				<input type="button" class="pulsanti" name="btnTitolaritaDid" value="Prendi in carico per trasferimento" 
					onClick="callInviaSAPTitPresaCarico('<%=DateUtils.formatXMLGregorian(dataDichSap)%>');" id="btnIDTitolarietaDid"/>
			<%}%>
			</td>
			</tr>
		<%}%>
		</table>
		<%out.print(htmlStreamBottomCoop);%>
	<%} else {
		if (isSapAssente) {
			out.print(htmlStreamTop);%>
			<table class="main">
		  	<tr>
			<td class="titolo" colspan="2">SAP Nazionale inesistente.
			</td>
			</tr>
			</table>
		   	<%out.print(htmlStreamBottom);%>
		<%}%>
	<%}%>
<%}%>

<table class="main">

<%if (request.getParameter("INVIASAPTITOLARIETA") != null) {%>
	<tr>
	<td class="campo"><%=msgTrasferimento%>
	</td>
	</tr>
<% } %>

<tr><td align="center">
<%if (!flgInibisciIndietro) { %>
	<button type="button" class="ListButtonChangePage" name="btnIndietro" 
	          onClick="indietro()"><span align="center"><img id="immIndietro" src="../../img/indietro.gif" alt="Indietro" align="absmiddle" /> Indietro</span></button>
<% } else { %>
	<input class="pulsanti" type="button"  name="btnChiudi"
			value="Chiudi" onClick="javascript:window.parent.close();"/>
<% } %>
</td></tr>
</table>

</body>
</html>

<%!
String getParameters(HttpServletRequest request) {
    String qs = request.getParameter("QUERY_STRING");
    /*
    StringTokenizer st = new StringTokenizer(qs, "&26");
    StringBuffer sb = new StringBuffer();
    while (st.hasMoreTokens()) {
        String par = (String)st.nextToken();
        int i = par.indexOf("%3D");
        if (i<0)continue;
        sb.append(par.substring(0,i));
        sb.append("=");
        if (par.length()>i+3)
            sb.append(par.substring(i+3));
        sb.append("&");
    }
    return sb.toString();
    */
    return qs;
}
%>