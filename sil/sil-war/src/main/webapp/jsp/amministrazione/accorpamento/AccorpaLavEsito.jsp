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

<%@ include file="../../global/noCaching.inc" %>
<%@ include file="../../global/getCommonObjects.inc" %>

<%	
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	// Lettura parametri dalla REQUEST
	int     cdnFunzione      = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	String  cdnFunzioneStr   = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");
	
	String cdnLavoratore1 =(String)serviceRequest.getAttribute("cdnLavoratore1");
	String cdnLavoratore2 =(String)serviceRequest.getAttribute("cdnLavoratore2");
	String cdnLavoratoreAccorpante = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnLavInCuiAccorpare");
	String strCodiceFiscale = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "codiceFiscaleTo");
	String esito = (String)serviceResponse.getAttribute("ACCORPA_LAVORATORE.ESITO_ACCORPAMENTO");	
	// CONTROLLO PERMESSI SULLA PAGINA
	/*
	PageAttribs attributi = new PageAttribs(user, _page);
	// gli attributi servirebbere per gli hiperlink. 
	*/
	Object cdnUtCorrente    = sessionContainer.getAttribute("_CDUT_");
	String cdnUtCorrenteStr = StringUtils.getStringValueNotNull(cdnUtCorrente);
	
	String codMinSap = "";
	boolean visualizzaSezioneInvioSAP = false;
	boolean canInviaSAP = false;
	String statoSAP = "";
	String dtInvioMin = "";
	String dtInizioVal = "";
	String dtFineVal = "";
	
	if (esito != null && esito.equalsIgnoreCase("OK")) {
		visualizzaSezioneInvioSAP = true;
		PageAttribs attributiCruscotto = new PageAttribs(user, "CRUSCOTTOADESIONEPAGE");
		canInviaSAP = attributiCruscotto.containsButton("INVIA_SAP");
		Vector vectConoInfo = serviceResponse.getAttributeAsVector("M_AccorpamentoGetUltimoInvioSAPSistema.ROWS.ROW");
		if ((vectConoInfo != null) && (vectConoInfo.size() > 0)) {
			SourceBean beanLastInsert = (SourceBean) vectConoInfo.get(0);
			codMinSap = StringUtils.getAttributeStrNotNull(beanLastInsert, "CODMINSAP");
			statoSAP = StringUtils.getAttributeStrNotNull(beanLastInsert, "STRDESCRIZIONE");
			dtInvioMin = StringUtils.getAttributeStrNotNull(beanLastInsert, "DATAINVIOMIN");
			dtInizioVal = StringUtils.getAttributeStrNotNull(beanLastInsert, "DATINIZIOVAL");
			dtFineVal = StringUtils.getAttributeStrNotNull(beanLastInsert, "DATFINEVAL");				
		}
	}

	// Stringhe con HTML per layout tabelle
	String htmlStreamTop    = StyleUtils.roundTopTable(true);
	String htmlStreamBottom = StyleUtils.roundBottomTable(true);
%>

<html>
<head>
<title>Esito accorpamento lavoratori</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />

<af:linkScript path="../../js/"/>

<script language="Javascript">

	/* Funzione per tornare alla pagina precedente */
	function nuovoAccorpamento() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
		
		goTo("Page=AccorpaLavRicercaPage&cdnfunzione=<%=cdnFunzione%>");
	}
	<%--
	function goBack(){
		if (isInSubmit()) return;
		goTo("Page=AccorpaLavDettaglioPage&cdnfunzione=<%=cdnFunzione%>&cdnLavoratore1=<%=cdnLavoratore1%>&cdnLavoratore2=<%=cdnLavoratore2%>");
	}
	--%>
	
	/* Funzione chiamata al caricamento della pagina */
	function onLoad() {
		rinfresca();
		// altri funzioni da richiamare sulla onLoad...
	}

	function inviaSAP(operazione) {
 	  	if (confirm("Si vuole procedere all'invio della SAP?")) {
  	  		document.form.OPERAZIONE.value = operazione;
	  	  	if (document.getElementById("btnIDInviaSAP") != null) {
				document.getElementById("btnIDInviaSAP").disabled = true;
			}
  	  		return true;
 	  	}
 	  	else {
 	  		return false;
 	  	}
  	}

<%--
	// Genera il Javascript che si occuperà di inserire i links nel footer
	attributi.showHyperLinks(out, requestContainer, responseContainer, null);
--%>

</script>
</head>

<body class="gestione" onload="onLoad()">

<p class="titolo">Esito accorpamento lavoratori</p>

<af:form name="form" action="AdapterHTTP" method="POST">

	<af:textBox type="hidden" name="cdnfunzione" value="<%= cdnFunzioneStr %>" />
	<input type="hidden" name="PAGE" value="AccorpaLavInvioSAPPage">
	<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratoreAccorpante%>">
	<input type="hidden" name="OPERAZIONE" value="">
	<input type="hidden" name="CF" value="<%=strCodiceFiscale%>">

	<%= htmlStreamTop %>
	<table class="main">
		<tr>
			<td colspan="2" align="center">
				<table>
					<tr><td><af:showErrors />
							<af:showMessages   prefix="Accorpa_Lavoratore" />
					</td><tr>
				</table>
			</td>
		<tr>
			<td colspan="2">
				<input type="button" class="pulsante" name="back" value="Chiudi" onclick="nuovoAccorpamento()" />
				<%--if (esito.equals("FAIL")) {%>
				<input type="button" class="pulsante" name="back" value="Indietro" onclick="goBack()" />			
				<%}--%>
			</td>
		</tr>
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
	</table>
	
	<%if (visualizzaSezioneInvioSAP) {%>
	<table class="main">
		<tr>
			<td><div class="sezione2"/>Invia SAP</td>
		</tr>
	</table>
	
	<table class="main">
	<tr>
		<td class="etichetta" align="right">Codice SAP Ministeriale</td>
		<td align="left"><strong><%=codMinSap%></strong></td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td class="etichetta" align="right">Stato</td>
              <td align="left"><strong><%=statoSAP%></strong></td>
	</tr>
	<tr>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr>
      	<td class="etichetta" align="right">Data Invio SAP al Ministero</td>
        <td align="left"><%=dtInvioMin%></td>
        <td>&nbsp;</td>
		<td>&nbsp;</td>
        <td class="etichetta">Data inizio Stato</td>
        <td align="left"><%=dtInizioVal%></td>
  	</tr>
  	</table>
  	
	<%if (canInviaSAP) {%>
  		<table class="main">
		<tr>
     		<td colspan="2" align="center">
     			<input class="pulsante" type="submit" name="btnInviaSAP" value="Invia SAP" onclick="return inviaSAP('INVIA_SAP');" id="btnIDInviaSAP"/>
			</td>
		</tr>
		</table>
	<%}
	}%>
	
	<%= htmlStreamBottom %>
</af:form>

</body>
</html>
