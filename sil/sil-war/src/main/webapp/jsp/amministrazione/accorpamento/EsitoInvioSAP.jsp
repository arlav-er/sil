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
	
	String cdnLavoratore =(String)serviceRequest.getAttribute("cdnLavoratore");
	
	Object cdnUtCorrente    = sessionContainer.getAttribute("_CDUT_");
	String cdnUtCorrenteStr = StringUtils.getStringValueNotNull(cdnUtCorrente);
	String codMinSap = "";
	String statoSAP = "";
	String dtInvioMin = "";
	String dtInizioVal = "";
	String dtFineVal = "";
	
	Vector vectConoInfo = serviceResponse.getAttributeAsVector("M_SapGestioneServiziGetCodMin.ROWS.ROW");
	if ((vectConoInfo != null) && (vectConoInfo.size() > 0)) {
		SourceBean beanLastInsert = (SourceBean) vectConoInfo.get(0);
		codMinSap = StringUtils.getAttributeStrNotNull(beanLastInsert, "CODMINSAP");
		statoSAP = StringUtils.getAttributeStrNotNull(beanLastInsert, "STRDESCRIZIONE");
		dtInvioMin = StringUtils.getAttributeStrNotNull(beanLastInsert, "DATAINVIOMIN");
		dtInizioVal = StringUtils.getAttributeStrNotNull(beanLastInsert, "DATINIZIOVAL");
		dtFineVal = StringUtils.getAttributeStrNotNull(beanLastInsert, "DATFINEVAL");				
	}
	
	// Stringhe con HTML per layout tabelle
	String htmlStreamTop    = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
<title>Esito invio sap a seguito accorpamento lavoratori</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />

<af:linkScript path="../../js/"/>

<script language="Javascript">

	/* Funzione per tornare alla pagina precedente */
	function nuovoAccorpamento() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
		goTo("Page=AccorpaLavRicercaPage&cdnfunzione=<%=cdnFunzione%>");
	}
	
	/* Funzione chiamata al caricamento della pagina */
	function onLoad() {
		rinfresca();
		// altri funzioni da richiamare sulla onLoad...
	}
	
</script>
</head>

<body class="gestione" onload="onLoad()">

<p class="titolo">Esito invio SAP a seguito accorpamento lavoratori</p>

<af:form name="form" action="AdapterHTTP" method="POST">

	<af:textBox type="hidden" name="cdnfunzione" value="<%= cdnFunzioneStr %>" />

	<%= htmlStreamTop %>
	<table class="main">
		<tr>
			<td colspan="2" align="center">
				<table>
					<tr><td><af:showErrors />
							<af:showMessages   prefix="M_GestioneInvioSap" />
					</td><tr>
				</table>
			</td>
		<tr>
			<td colspan="2">
				<input type="button" class="pulsante" name="back" value="Chiudi" onclick="nuovoAccorpamento()" />
			</td>
		</tr>
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
	</table>
	
	<table class="main">
		<tr>
			<td><div class="sezione2"/>Dati SAP</td>
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
	
	<%= htmlStreamBottom %>
</af:form>

</body>
</html>
