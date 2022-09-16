<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<!-- @author: Giordano Gritti -->
<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.module.movimenti.constant.Properties, 
                it.eng.sil.security.*, 
                java.math.*"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
		/* recupero request */
		String cpi = (String) serviceRequest.getAttribute("sel_cpi");
		String nome = (String) serviceRequest.getAttribute("strNome");
		String cognome = (String) serviceRequest.getAttribute("strCognome");
		String cf = (String) serviceRequest.getAttribute("strCodiceFiscale");
		String operatore = (String) serviceRequest.getAttribute("sel_operatore");
		String servizio = (String) serviceRequest.getAttribute("sel_servizio");
		String ambiente = (String) serviceRequest.getAttribute("sel_aula");
		//String aula = (String) serviceRequest.getAttribute("sel_aula");
		String esito = (String) serviceRequest.getAttribute("esitoApp");
		String dataDal = (String) serviceRequest.getAttribute("dataDal");
		String dataAl = (String) serviceRequest.getAttribute("dataAl");


        String _page = (String) serviceRequest.getAttribute("PAGE");
		ProfileDataFilter filter = new ProfileDataFilter(user, _page);
		BigDecimal _prgAppunt = ((BigDecimal) serviceResponse.getAttribute("PRGAPPUNTAMENTO"));
		Vector vettRis = serviceResponse.getAttributeAsVector("M_AGENDA_SMS_LISTALAVORATORI.ROWS.ROW");
		
		Vector vettRisFiltr = serviceResponse.getAttributeAsVector("M_AGENDA_SMS_LISTALAVORATORI_FILTRATI.ROWS.ROW"); // mi occorre solo per estrarre il numero totali di lavoratori dalla ricerca -- non posso usare l'altro modulo data la paginazione richiesta
		
		//formattazione pagina jsp
		String htmlStreamTop = StyleUtils.roundTopTable(false);
		String htmlStreamBottom = StyleUtils.roundBottomTable(false);
		
		String codServizioSMSFiltrati = "";
		String invioConfigSMSfiltrati = "";
		SourceBean configSMSFiltrati = (SourceBean) serviceResponse.getAttribute("M_GetCodConfigSMSFiltrati.ROWS.ROW");
		if(configSMSFiltrati != null){
			invioConfigSMSfiltrati  = StringUtils.getAttributeStrNotNull(configSMSFiltrati, "num");	
			codServizioSMSFiltrati  = StringUtils.getAttributeStrNotNull(configSMSFiltrati, "strvalore");
		}
		
%>
 
<html>
<head>
<title>Lista Lavoratori</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />
<script TYPE="text/javascript">

function invioMassivo() {
if (  IsElementiSelezionati('SELEZIONE') && confirm("Creare il contatto per tutti i tipi selezionati?") ) {
	var s= "AdapterHTTP?PAGE=AGENDA_SMS_INVIO_PAGE&INVIOMASSIVO=true";
    var chkboxObjEval = document.getElementsByName("SELEZIONE");
    var chkboxObj=eval(chkboxObjEval);
    var strPrgAppuntamento="";
    for(i=0; i<chkboxObj.length; i++) {
  		if(chkboxObj[i].checked) {
  			if(strPrgAppuntamento.length>0) { strPrgAppuntamento += ","; }
  			strPrgAppuntamento += chkboxObj[i].value;
  			}
  	  	}	
    s +="&SELEZIONE=" + encodeURIComponent(strPrgAppuntamento);    
    setWindowLocation(s);
    }
}

function invioMassivoFiltrati() {

	if (isInSubmit()) return;

    var t="L'invio degli SMS sara' effettuato su tutti gli appuntamenti ottenuti dalla ricerca.\n";
    t += "Confermi l'operazione ?";
    
    if ( confirm(t) ) {

    	var s= "AdapterHTTP?PAGE=AGENDA_SMS_INVIO_PAGE&INVIOMASSIVO=true";
        s +="&SMSFILTRATI=1";
        s +="&sel_cpi=<%=cpi%>";
        s +="&strNome=<%=nome%>";
        s +="&strCognome=<%=cognome%>";
        s +="&strCodiceFiscale=<%=cf%>";
        s +="&sel_aula=<%=ambiente%>";
        s +="&sel_operatore=<%=operatore%>";
        s +="&sel_servizio=<%=servizio%>";
        s +="&esitoApp=<%=esito%>";
        s +="&dataDal=<%=dataDal%>";
        s +="&dataAl=<%=dataAl%>";
        
        setWindowLocation(s);	 
    }	  
}

//controllo sulla selezione degli appuntamnenti
function IsElementiSelezionati(listName) {	
	var elementi= document.getElementsByName(listName);
	msg = "E' necessario scegliere\nalmeno un appuntamento"
	for (var i= 0; i < elementi.length; i++) {
		if ( elementi[i].checked ) {
		return true;
		}	
	}		
	alert(msg);
	return (false);
}  


//gestione checkbox
function selDeselApp(){
	var coll = document.getElementsByName("SEL");
  	var b = coll[0].checked;
  	//alert(b);
  	var i;
  	coll= document.getElementsByName("SELEZIONE");
  	for(i=0; i<coll.length; i++) {
  		coll[i].checked = b;
  	}
}
//torna alla ricerca
function tornaAllaRicerca(){
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;	
  		url="AdapterHTTP?PAGE=AGENDA_SMS_RICERCAAPPUNTAMENTO_PAGE";
    	setWindowLocation(url);
}
</script>

</head>

<body class="gestione" onload="rinfresca()">

<%if((invioConfigSMSfiltrati.equalsIgnoreCase("0")) || ( (invioConfigSMSfiltrati.equalsIgnoreCase("1")) && vettRis.size() < 100)) { // eventualmente nella prima parte della if inserire  && codServizioSMSFiltrati.equalsIgnoreCase("inserire codServizio da abilitare all'invio SMS massivo")%>

<af:form action="AdapterHTTP" name="frmLista" method="GET" >
<input name="PAGE" type="hidden" value="AGENDA_SMS_INVIO_PAGE"/>
<table width="100%" align="center">
	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr valign="middle">
		<td align="left" class="azzurro_bianco"><input type="checkbox"
			name="SEL" onClick="selDeselApp();" />&nbsp;Seleziona/Deseleziona
			tutti &nbsp;&nbsp;</td>
	</tr>
</table>
<af:list moduleName="M_AGENDA_SMS_LISTALAVORATORI" />

<table class="main" align="center">
	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td align="center"><input type="button" class="pulsanti"
			value="Invia SMS" onclick="invioMassivo()">
		<% if(invioConfigSMSfiltrati.equalsIgnoreCase("0")){ // && codServizioSMSFiltrati.equalsIgnoreCase("inserire codServizio da abilitare all'invio SMS massivo")%>	
<!-- 		    <td align="center"> -->
		    <input type="button" class="pulsanti"
			    value="Invia SMS Massivo" onclick="invioMassivoFiltrati()">
		<% } %>	
		</td>	
	</tr>
</table>
</af:form>
<table class="main" align="center">
	<tr>
		<td align="center"><input type="button" class="pulsanti"
			value="Ritorna alla ricerca" onclick="tornaAllaRicerca()">
		</td>
	</tr>
</table>
<%} else {
%>
<p>&nbsp;</p>
<%out.print(htmlStreamTop);%>

<table class="main" align="center">
	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td>Sono stati trovati <%=vettRisFiltr.size()%> record, il numero potrebbe
		caricare eccessivamente l'applicativo: <br>
		è necessario raffinare la ricerca
		</td>
	</tr>
	<tr>
		<td>&nbsp;
		</td>
	</tr>
	<tr>
		<td align="center"><input type="button" class="pulsanti"
			value="Ritorna alla ricerca" onclick="tornaAllaRicerca()">
		</td>
	</tr>
</table>
<%}%>
<%out.print(htmlStreamBottom);%>
</body>
</html>
