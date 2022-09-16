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
	String _page = "SAPRicercaNotifichePage";
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
 	boolean isRicercaCF = false;
	
	if (!filter.canView()) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}
	
	String _funzione = (String) serviceRequest.getAttribute("cdnFunzione");
	
	String strCodiceFiscale = StringUtils.getAttributeStrNotNull(serviceRequest, "strCodiceFiscale");
	String strCognome = StringUtils.getAttributeStrNotNull(serviceRequest, "strCognome");
	String strNome = StringUtils.getAttributeStrNotNull(serviceRequest, "strNome");
	String tipoRicerca = StringUtils.getAttributeStrNotNull(serviceRequest, "tipoRicerca");
	String dataNotificaDa = StringUtils.getAttributeStrNotNull(serviceRequest, "dataNotificaDa");
	String dataNotificaA = StringUtils.getAttributeStrNotNull(serviceRequest, "dataNotificaA");
	String codMinSap = StringUtils.getAttributeStrNotNull(serviceRequest, "codMinSap");
	String codMotivoModifica = StringUtils.getAttributeStrNotNull(serviceRequest, "codMotivoModifica");
	String ricercaDidAttiva = StringUtils.getAttributeStrNotNull(serviceRequest, "ricercaDidAttiva");
	String ricercaPattoAttivo = StringUtils.getAttributeStrNotNull(serviceRequest, "ricercaPattoAttivo");

	String txtOut = "";
	String parameters = "&cdnFunzione="+ _funzione;

   	if (!strCodiceFiscale.equals("")) {
       	isRicercaCF = true;
       	parameters = parameters + "&strCodiceFiscale="+strCodiceFiscale;
       	parameters = parameters + "&cercaNellaLista=";
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
       
    if(!codMinSap.equals("")) {
       	parameters = parameters + "&codMinSap="+codMinSap;
       	txtOut += "Codice Ministeriale SAP <strong>"+ codMinSap +"</strong>; ";
    }       
    if (!codMotivoModifica.equals("")) {
       	parameters = parameters + "&codMotivoModifica="+codMotivoModifica;
       	String descNotifica = serviceResponse.containsAttribute("M_GetMotivoNotifica.ROWS.ROW.STRDESCRIZIONE")?
       			serviceResponse.getAttribute("M_GetMotivoNotifica.ROWS.ROW.STRDESCRIZIONE").toString():"";
       	txtOut += "Motivo Modifica <strong>"+ descNotifica +"</strong>; ";
    }       
    if(!ricercaDidAttiva.equals("")) {
       	parameters = parameters + "&ricercaDidAttiva="+ricercaDidAttiva;
       	txtOut += "<strong>DID attiva</strong>; ";
    }
    if(!ricercaPattoAttivo.equals("")) {
       	parameters = parameters + "&ricercaPattoAttivo="+ricercaPattoAttivo;
       	txtOut += "<strong>Patto attivo</strong>; ";
    }        
    if(!dataNotificaDa.equals("")) {
       	parameters = parameters + "&dataNotificaDa="+dataNotificaDa;
       	txtOut += "Data notifica dal <strong>"+ dataNotificaDa +"</strong>; ";
    }       
    if(!dataNotificaA.equals("")) {
       	parameters = parameters + "&dataNotificaA="+dataNotificaA;
       	txtOut += "Data notifica al <strong>"+ dataNotificaA +"</strong>; ";
    }
%>

<html>
<head>
<title>Lista Notifiche SAP</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />

<script type="text/Javascript">
	if (window.top.menu != undefined){
			window.top.menu.location="AdapterHTTP?PAGE=MenuCompletoPage";
	}
	
	function visualizzaErroriSap(page, cdnFunzione, cdnLavoratore) {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

        var url = 'AdapterHTTP?' + 
        	'PAGE=' + page + "&" +
        	'cdnFunzione=' + cdnFunzione + "&" +
        	'cdnLavoratore=' + cdnLavoratore;
			  	
	  	var feat = "status=NO,toolbar=NO,scrollbars=YES,resizable=YES,height=400,width=800,left=400";
	  	var titolo = "Errori SAP";
	  	window.open(url, "_blank", feat);
	}	
	
	function visualizzaNotificheAssociate(page, cdnFunzione, codMinSap) {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

        var url = 'AdapterHTTP?' + 
        	'PAGE=' + page + "&" +
        	'cdnFunzione=' + cdnFunzione + "&" +
        	'codMinSap=' + codMinSap;
			  	
	  	var feat = "status=NO,toolbar=NO,scrollbars=NO,resizable=NO,height=400,width=800,left=400";
	  	var titolo = "Lista Notifiche SAP";
	  	window.open(url, "_blank", feat);
	}	

	function tornaAllaRicerca() {
  		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
       	url="AdapterHTTP?PAGE="+"<%=_page%>" + "<%=parameters%>";
		setWindowLocation(url);
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
 	
	<af:form dontValidate="true">
	
		<af:JSButtonList moduleName="M_DynamicListaNotificheSAP" jsCaptions="visualizzaNotificheAssociate;visualizzaErroriSap" getBack="true"/>
		
		<center>
			<input class="pulsante" type="button" name="torna" value="Torna alla pagina di ricerca" onclick="tornaAllaRicerca()" />
		</center>
	</af:form>
	<br />
	
</body>
</html>