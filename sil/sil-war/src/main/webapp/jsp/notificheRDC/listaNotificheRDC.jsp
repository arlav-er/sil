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
	String _page = "RicercaNotificheRDCPage";
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
    PageAttribs attributi = new PageAttribs(user, _page);

    boolean canExportCsv = attributi.containsButton("N_RDC_CSV");
 	boolean isRicercaCF = false;
	
	if (!filter.canView()) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	} else {
		String _funzione = (String) serviceRequest.getAttribute("cdnFunzione");
		
		String campoPercorsoRdc =null;
		boolean ricercaDaPercorso = false; 
		if(serviceRequest.containsAttribute("campoPrgRdc")){
			campoPercorsoRdc =StringUtils.getAttributeStrNotNull(serviceRequest, "campoPrgRdc");
			ricercaDaPercorso = StringUtils.isFilledNoBlank(campoPercorsoRdc);
		}
		
		String strCodiceFiscale = StringUtils.getAttributeStrNotNull(serviceRequest, "strCodiceFiscale");
		String strCognome = StringUtils.getAttributeStrNotNull(serviceRequest, "strCognome");
		String strNome = StringUtils.getAttributeStrNotNull(serviceRequest, "strNome");
		String tipoRicerca = StringUtils.getAttributeStrNotNull(serviceRequest, "tipoRicerca");
		String dataInvioDa = StringUtils.getAttributeStrNotNull(serviceRequest, "dataInvioDa");
		String dataInvioA = StringUtils.getAttributeStrNotNull(serviceRequest, "dataInvioA");
		String dataRendicontazioneDa	= StringUtils.getAttributeStrNotNull(serviceRequest, "dataRendicontazioneDa");
		String dataRendicontazioneA = StringUtils.getAttributeStrNotNull(serviceRequest, "dataRendicontazioneA");
		String dataRicezioneSILDa	= StringUtils.getAttributeStrNotNull(serviceRequest, "dataRicezioneSILDa");
		String dataRicezioneSILA = StringUtils.getAttributeStrNotNull(serviceRequest, "dataRicezioneSILA");
		String protocolloInps = StringUtils.getAttributeStrNotNull(serviceRequest, "protocolloInps");
		String finisceCon = StringUtils.getAttributeStrNotNull(serviceRequest, "finisceCon");
		String codComuneRes = StringUtils.getAttributeStrNotNull(serviceRequest, "codComResHid");
		String descrComuneRes = StringUtils.getAttributeStrNotNull(serviceRequest, "strComResHid");
		String codRuolo = StringUtils.getAttributeStrNotNull(serviceRequest, "codRuolo");
		String codStatoDomanda = StringUtils.getAttributeStrNotNull(serviceRequest, "codStatoDomanda");
		String ordDatRendDC = StringUtils.getAttributeStrNotNull(serviceRequest, "ordDatRendDC");
		String ordDatRend = StringUtils.getAttributeStrNotNull(serviceRequest, "ordDatRend");
		String ordNucleoDC = StringUtils.getAttributeStrNotNull(serviceRequest, "ordNucleoDC");
		String ordNucleo = StringUtils.getAttributeStrNotNull(serviceRequest, "ordNucleo");
		String ordDataDomDC = StringUtils.getAttributeStrNotNull(serviceRequest, "ordDataDomDC");
		String ordDataDom = StringUtils.getAttributeStrNotNull(serviceRequest, "ordDataDom");
		String codTipoDomanda =  StringUtils.getAttributeStrNotNull(serviceRequest, "codTipoDomanda");

	/* 	String ordDataRicSILDC = StringUtils.getAttributeStrNotNull(serviceRequest, "ordDataRicSILDC");
		String ordDataRicSIL = StringUtils.getAttributeStrNotNull(serviceRequest, "ordDataRicSIL"); */
		
		String txtOut = "";
		String parameters = "&cdnFunzione="+ _funzione;
		parameters = parameters +  "&isComeBack=true";
		 if(!ordDatRendDC.equals("")) {
			 parameters = parameters + "&ordDatRendDC="+ordDatRendDC;
		 }
		 if(!ordDatRend.equals("")) {
			 parameters = parameters + "&ordDatRend="+ordDatRend;
		 }
		 if(!ordNucleoDC.equals("")) {
			 parameters = parameters + "&ordNucleoDC="+ordNucleoDC;
		 }
		 if(!ordNucleo.equals("")) {
			 parameters = parameters + "&ordNucleo="+ordNucleo;
		 }
		 if(!ordDataDomDC.equals("")) {
			 parameters = parameters + "&ordDataDomDC="+ordDataDomDC;
		 }
		 if(!ordDataDom.equals("")) {
			 parameters = parameters + "&ordDataDom="+ordDataDom;
		 }
		/*  if(!ordDataRicSILDC.equals("")) {
			 parameters = parameters + "&ordDataRicSILDC="+ordDataRicSILDC;
		 }
		 if(!ordDataRicSIL.equals("")) {
			 parameters = parameters + "&ordDataRicSIL="+ordDataRicSIL;
		 } */
		
        if (!strCodiceFiscale.equals("")) {
        	isRicercaCF = true;
        	parameters = parameters + "&strCodiceFiscale="+strCodiceFiscale;
        	txtOut += "Codice Fiscale Lavoratore <strong>"+ strCodiceFiscale +"</strong>; ";
        }
        if (!strCognome.equals("")) {
        	parameters = parameters + "&strCognome="+strCognome;
        	txtOut += "Cognome Lavoratore <strong>"+ strCognome +"</strong>; ";
        }
        if(!strNome.equals("")) {
        	parameters = parameters + "&strNome="+strNome;
        	txtOut += "Nome Lavoratore <strong>"+ strNome +"</strong>; ";
        }
	        
	    parameters = parameters + "&tipoRicerca="+tipoRicerca;
	        
        if (!strCodiceFiscale.equals("") || !strCognome.equals("") || !strNome.equals("")) {
    		String tipoRic = tipoRicerca;
    		if (tipoRic.equalsIgnoreCase("iniziaPer")) tipoRic = "inizia per";
    		txtOut += "tipo di ricerca <strong>" + tipoRic + "</strong>; ";
    	}
        
        if(!codRuolo.equals("")) {
        	parameters = parameters + "&codRuolo="+codRuolo;
        	String descrRuolo = codRuolo.equalsIgnoreCase("M")? "Altro membro nucleo familiare" : "Richiedente";
        	txtOut += "Ruolo <strong>"+ descrRuolo +"</strong>; ";
        }else{
        	parameters = parameters + "&codRuolo=";
        	txtOut += "Ruolo <strong>Tutti</strong>; ";
        }
        if(!codStatoDomanda.equals("")) {
        	parameters = parameters + "&codStatoDomanda="+codStatoDomanda;
        	String descrStatoDomanda =   "";
        	if(codStatoDomanda.equalsIgnoreCase("AC") || codStatoDomanda.equalsIgnoreCase("ACCOLTO")){
        		descrStatoDomanda = "AC - Accolto";
        	}else if(codStatoDomanda.equalsIgnoreCase("OT")){
        		descrStatoDomanda = "Altri stati";
        	}
	   		txtOut += "Stato dom. INPS <strong>"+ descrStatoDomanda +"</strong>; ";      	
        }else{
           	parameters = parameters + "&codStatoDomanda=";
        }
        if(!codTipoDomanda.equals("")) {
        	parameters = parameters + "&codTipoDomanda="+codTipoDomanda;
        	String descrTipoDomanda =   "";
        	if(codTipoDomanda.equalsIgnoreCase("PRD")  ){
        		descrTipoDomanda = "Prima domanda";
        	}else if(codTipoDomanda.equalsIgnoreCase("NUD")){
        		descrTipoDomanda = "Nuova domanda";
        	}else if(codTipoDomanda.equalsIgnoreCase("RIN")){
        		descrTipoDomanda = "Rinnovo";
        	}
	   		txtOut += "Tipo di Domanda <strong>"+ descrTipoDomanda +"</strong>; ";      	
        }else{
           	parameters = parameters + "&codTipoDomanda=";
        }
        if(!protocolloInps.equals("")) {
        	parameters = parameters + "&protocolloInps="+protocolloInps;
        	txtOut +=  "Protocollo Inps ";
        	if(finisceCon.equalsIgnoreCase("si")) {
            	parameters = parameters + "&finisceCon="+finisceCon;
            	txtOut += "finisce con ";
            } else {
            	parameters = parameters + "&finisceCon=no";
            }  
        	txtOut +="<strong>"+  protocolloInps +"</strong>; ";
        }       
        if (!codComuneRes.equals("")) {
        	parameters = parameters + "&codComResHid="+codComuneRes; 	
        }
        if(!descrComuneRes.equals("")) {
        	txtOut += "Comune Residenza <strong>"+ descrComuneRes +"</strong>; ";
        }
         
        if(!dataInvioDa.equals("")) {
        	parameters = parameters + "&dataInvioDa="+dataInvioDa;
        	txtOut += "Data domanda dal <strong>"+ dataInvioDa +"</strong>; ";
        }       
        if(!dataInvioA.equals("")) {
        	parameters = parameters + "&dataInvioA="+dataInvioA;
        	txtOut += "Data domanda al <strong>"+ dataInvioA +"</strong>; ";
        }  
        if(!dataRendicontazioneDa.equals("")) {
        	parameters = parameters + "&dataRendicontazioneDa="+dataRendicontazioneDa;
        	txtOut += "Data rendicontazione dal <strong>"+ dataRendicontazioneDa +"</strong>; ";
        }       
        if(!dataRendicontazioneA.equals("")) {
        	parameters = parameters + "&dataRendicontazioneA="+dataRendicontazioneA;
        	txtOut += "Data rendicontazione al <strong>"+ dataRendicontazioneA +"</strong>; ";
        }  
        if(!dataRicezioneSILDa.equals("")) {
        	parameters = parameters + "&dataRicezioneSILDa="+dataRicezioneSILDa;
        	txtOut += "Data ricezione SIL dal <strong>"+ dataRicezioneSILDa +"</strong>; ";
        }       
        if(!dataRicezioneSILA.equals("")) {
        	parameters = parameters + "&dataRicezioneSILA="+dataRicezioneSILA;
        	txtOut += "Data ricezione SIL al <strong>"+ dataRicezioneSILA +"</strong>; ";
        }  
   Integer numeroRighe = (Integer) responseContainer.getServiceResponse().getAttribute("M_DYNAMICLISTANOTIFICHERDC.ROWS.NUM_RECORDS");
 
%>

<html>
<head>
<title>Notifiche RDC</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />

<script type="text/Javascript">
	if (window.top.menu != undefined){
			window.top.menu.location="AdapterHTTP?PAGE=MenuCompletoPage";
	}

	function tornaAllaRicerca() {
  		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
       	url="AdapterHTTP?PAGE="+"<%=_page%>" + "<%=parameters%>";
		setWindowLocation(url);
	} 	
	function AggiornaForm (PRGRDC, STRPROTOCOLLOINPS) {
       try{
    	   window.opener.document.Frm1.rdcProtocolloINPS.value = STRPROTOCOLLOINPS;
    	   window.opener.document.Frm1.prgRDC.value = PRGRDC;
    	   window.close();
       }
		catch(err) {
			
		}
	  
	}
	
	function esportaCsv() {    
		var doAlert =<%= numeroRighe.intValue()%>;
		if(doAlert<=0){
			alert("Non ci sono notifiche RDC da esportare");
			return false;
		}else{
			url="AdapterHTTP?PAGE=NotificheRDCRisultRicercaCsvPage";
		    url += "<%=parameters%>";             
		    window.location = url;
		    return false;
		}
	}
</script>

</head>
<body class="gestione" onload="rinfresca();">
	<center>
		<font color="red"> 
			<af:showErrors />
		</font>
		
		<%if(txtOut.length() > 0)
          { txtOut = "Filtri di ricerca:<br/> " + txtOut;%>
            <table cellpadding="2" cellspacing="10" border="0" width="100%">
             <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
              <%out.print(txtOut);%>
             </td></tr>
            </table>
        <%}%>
        
	</center>
	<%if(ricercaDaPercorso){ %>
	 	<af:JSButtonList moduleName="M_DynamicListaNotificheRDC" jsSelect="AggiornaForm" configProviderClass="it.eng.sil.module.rdc.DynamicConfigNotificheRDC" 
						 getBack="true"/>
	 <%}else{%>
		 <af:JSButtonList moduleName="M_DynamicListaNotificheRDC"  configProviderClass="it.eng.sil.module.rdc.DynamicConfigNotificheRDC" 
						 getBack="true"/>
	  <%}%>
		<%-- <af:list moduleName="M_DynamicListaNotificheRDC" getBack="true" 
			configProviderClass="it.eng.sil.module.rdc.DynamicConfigNotificheRDC"
		/> --%>
		
		<center>
			<input class="pulsante" type="button" name="torna" value="Torna alla pagina di ricerca" onclick="tornaAllaRicerca()" />
		</center>
		
		 <% if(canExportCsv){%>
		 <br/>
  			<center> 
  				<input type="button" class="pulsanti" value="Esporta in CSV" onClick="esportaCsv()">
  			</center>
  		 <%}%>
			
		
	 
	<br />
	
</body>
</html>
<%}%>
