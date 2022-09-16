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
	String _page = "SAPRicercaPage";
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
 	boolean isRicercaCF = false;
	
	if (!filter.canView()) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	} else {
		String _funzione = (String) serviceRequest.getAttribute("cdnFunzione");
		
		String strCodiceFiscale = StringUtils.getAttributeStrNotNull(serviceRequest, "strCodiceFiscale");
		String strCodiceFiscaleLista = StringUtils.getAttributeStrNotNull(serviceRequest, "strCodiceFiscaleLista");
		String ricercaNellaLista = StringUtils.getAttributeStrNotNull(serviceRequest, "cercaNellaLista");
		String strCognome = StringUtils.getAttributeStrNotNull(serviceRequest, "strCognome");
		String strNome = StringUtils.getAttributeStrNotNull(serviceRequest, "strNome");
		String tipoRicerca = StringUtils.getAttributeStrNotNull(serviceRequest, "tipoRicerca");
		String dataInvioMinDa = StringUtils.getAttributeStrNotNull(serviceRequest, "dataInvioMinDa");
		String dataInvioMinA = StringUtils.getAttributeStrNotNull(serviceRequest, "dataInvioMinA");
		String dtmModDa = StringUtils.getAttributeStrNotNull(serviceRequest, "dtmModDa");
		String dtmModA = StringUtils.getAttributeStrNotNull(serviceRequest, "dtmModA");
		String codMinSap = StringUtils.getAttributeStrNotNull(serviceRequest, "codMinSap");
		String codStatoSap = StringUtils.getAttributeStrNotNull(serviceRequest, "codStatoSap");
		String descrCodStatoSaphid = StringUtils.getAttributeStrNotNull(serviceRequest, "descrCodStatoSaphid");
		String ricercaSoloUltimoStato = StringUtils.getAttributeStrNotNull(serviceRequest, "ricercaSoloUltimoStato");
		String ricercaSoloConNotificheMin = StringUtils.getAttributeStrNotNull(serviceRequest, "ricercaSoloConNotificheMin");
		String ricercaSoloMieiLavoratori = StringUtils.getAttributeStrNotNull(serviceRequest, "ricercaSoloMieiLavoratori");
		String ricercaDidAttiva = StringUtils.getAttributeStrNotNull(serviceRequest, "ricercaDidAttiva");
		String ricercaPattoAttivo = StringUtils.getAttributeStrNotNull(serviceRequest, "ricercaPattoAttivo");
	    String ricercaAutoSAP = StringUtils.getAttributeStrNotNull(serviceRequest, "ricercaAutoSAP");
		String ricercaErroreSAP = StringUtils.getAttributeStrNotNull(serviceRequest, "ricercaErroreSAP");
 
		String txtOut = "";
		String parameters = "&cdnFunzione="+ _funzione;

        if (!strCodiceFiscale.equals("")) {
        	isRicercaCF = true;
        	parameters = parameters + "&strCodiceFiscale="+strCodiceFiscale;
        	parameters = parameters + "&cercaNellaLista=";
        	txtOut += "Codice Fiscale Lavoratore <strong>"+ strCodiceFiscale +"</strong>; ";
        }
        if (!strCodiceFiscaleLista.equals("")) {
         	parameters = parameters + "&strCodiceFiscaleLista="+strCodiceFiscaleLista;
         	parameters = parameters + "&cercaNellaLista=si";
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
        }       
        if (!codStatoSap.equals("")) {
        	parameters = parameters + "&codStatoSap="+codStatoSap; 	
        }
        if(!descrCodStatoSaphid.equals("")) {
        	txtOut += "Stato SAP <strong>"+ descrCodStatoSaphid +"</strong>; ";
        }
        if(!ricercaSoloUltimoStato.equals("")) {
        	parameters = parameters + "&ricercaSoloUltimoStato="+ricercaSoloUltimoStato;
        	txtOut += "<strong>Solo ultimo Stato</strong>; ";
        } else {
        	parameters = parameters + "&ricercaSoloUltimoStato=no";
        }
        if(!ricercaSoloConNotificheMin.equals("")) {
        	parameters = parameters + "&ricercaSoloConNotificheMin="+ricercaSoloConNotificheMin;
        	txtOut += "<strong>Solo con notifiche Ministeriali</strong>; ";
        } else {
        	parameters = parameters + "&ricercaSoloConNotificheMin=no";
        }       
        if(!ricercaSoloMieiLavoratori.equals("")) {
        	parameters = parameters + "&ricercaSoloMieiLavoratori="+ricercaSoloMieiLavoratori;
        	txtOut += "<strong>Solo miei lavoratori</strong>; ";
        } else {
        	parameters = parameters + "&ricercaSoloMieiLavoratori=no";
        }       
        if(!ricercaDidAttiva.equals("")) {
        	parameters = parameters + "&ricercaDidAttiva="+ricercaDidAttiva;
        	txtOut += "<strong>DID attiva</strong>; ";
        }
        if(!ricercaPattoAttivo.equals("")) {
        	parameters = parameters + "&ricercaPattoAttivo="+ricercaPattoAttivo;
        	txtOut += "<strong>Patto attivo</strong>; ";
        }        
        if(!dataInvioMinDa.equals("")) {
        	parameters = parameters + "&dataInvioMinDa="+dataInvioMinDa;
        	txtOut += "Data invio Ministeriale dal <strong>"+ dataInvioMinDa +"</strong>; ";
        }       
        if(!dataInvioMinA.equals("")) {
        	parameters = parameters + "&dataInvioMinA="+dataInvioMinA;
        	txtOut += "Data invio Ministeriale al <strong>"+ dataInvioMinA +"</strong>; ";
        }        
        if(!dtmModDa.equals("")) {
        	parameters = parameters + "&dtmModDa="+dtmModDa;
        	txtOut += "Data modifica Stato dal <strong>"+ dtmModDa +"</strong>; ";
        }       
        if(!dtmModA.equals("")) {
        	parameters = parameters + "&dtmModA="+dtmModA;
        	txtOut += "Data modifica Stato al <strong>"+ dtmModA +"</strong>; ";
        }     
        if(!ricercaErroreSAP.equals("")) {
        	parameters = parameters + "&ricercaErroreSAP="+ricercaErroreSAP;
        	txtOut += "<strong>SAP in errore</strong>; ";
        }  
        if(!ricercaAutoSAP.equals("")) {
        	parameters = parameters + "&ricercaAutoSAP="+ricercaAutoSAP;
        	txtOut += "<strong>Invio automatico SAP</strong>; ";
        }   
        //La ricerca per uno specifico codice ministeriale esclude gli altri criteri.
        if(!codMinSap.equals("")) {
        	txtOut = "Codice Ministeriale SAP <strong>"+ codMinSap +"</strong>; ";
        }
%>

<html>
<head>
<title>Lista SAP</title>
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
	<h2>Lista SAP</h2>
	<af:form action="AdapterHTTP" method="POST"	name="Frm2">
  
			<table class="main"  style="width: 33% !important; margin: 0px 30% !important;">
				<tr><td colspan="2"/>&nbsp;</td></tr>
			   	<tr>
			    	<td class="etichetta"  style="width: 56% !important; padding-right: 0px !important">Codice Fiscale Lavoratore</td>
			        <td class="campo">
			        	<af:textBox type="text" name="strCodiceFiscaleLista" value="<%=strCodiceFiscaleLista%>" size="20" maxlength="16" />
			        </td>
			        <td>
				    	<input class="pulsante<%=((!isRicercaCF)?"":"Disabled")%>"  type="submit" name="cerca" value="Cerca" onClick=""/>
			        </td>
			   	</tr>
			   	 <input type="hidden" name="cercaNellaLista" value="si"/>
				 <input type="hidden" name="strCognome" value="<%=strCognome%>"/>
				 <input type="hidden" name="strNome" value="<%=strNome%>"/>
				 <input type="hidden" name="tipoRicerca" value="<%=tipoRicerca%>"/>
				 <input type="hidden" name="dataInvioMinDa" value="<%=dataInvioMinDa%>"/>
				 <input type="hidden" name="dataInvioMinA" value="<%=dataInvioMinA%>"/>
				 <input type="hidden" name="dtmModDa" value="<%=dtmModDa%>"/>
				 <input type="hidden" name="dtmModA" value="<%=dtmModA%>"/>	 
			     <input type="hidden" name="codMinSap" value="<%=codMinSap%>"/>
			     <input type="hidden" name="codStatoSap" value="<%=codStatoSap%>"/>
			     <input type="hidden" name="descrCodStatoSaphid" value="<%=descrCodStatoSaphid%>"/>
				 <input type="hidden" name="ricercaSoloUltimoStato" value="<%=ricercaSoloUltimoStato%>"/>	 
				 <input type="hidden" name="ricercaSoloConNotificheMin" value="<%=ricercaSoloConNotificheMin%>"/>	 
				 <input type="hidden" name="ricercaDidAttiva" value="<%=ricercaDidAttiva%>"/>
				 <input type="hidden" name="ricercaSoloMieiLavoratori" value="<%=ricercaSoloMieiLavoratori%>"/>
				 <input type="hidden" name="ricercaPattoAttivo" value="<%=ricercaPattoAttivo%>"/>
				 <input type="hidden" name="ricercaAutoSAP" value="<%=ricercaAutoSAP%>"/>
				 <input type="hidden" name="ricercaErroreSAP" value="<%=ricercaErroreSAP%>"/>  
			     <input type="hidden" name="PAGE" value="SAPListaPage"/>
			     <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
			</table>
	</af:form>
 	
	<af:form dontValidate="true">
	
		<af:JSButtonList moduleName="M_DynamicListaSAP"	configProviderClass="it.eng.sil.module.amministrazione.DynamicListaSAPConfig" 
						jsCaptions="visualizzaNotificheAssociate;visualizzaErroriSap" getBack="true"/>
		
		<center>
			<input class="pulsante" type="button" name="torna" value="Torna alla pagina di ricerca" onclick="tornaAllaRicerca()" />
		</center>
	</af:form>
	<br />
	
</body>
</html>
<%
	}
%>