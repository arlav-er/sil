<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
//prelevo la funzione che devo chiamare per aggiornare la pagina
String cdnFunzione 		= (String)serviceRequest.getAttribute("CDNFUNZIONE");
String prgOrig 			= StringUtils.getAttributeStrNotNull(serviceRequest, "PRGRICHIESTAORIG");
String prgRichiestaAz 	= StringUtils.getAttributeStrNotNull(serviceRequest, "PRGRICHIESTAAZ");
String prgNominativo 	= StringUtils.getAttributeStrNotNull(serviceRequest, "V_PRGNOMINATIVO");
String numRichiesta 	= StringUtils.getAttributeStrNotNull(serviceRequest, "NUMRICHIESTA");
String numRichiestaOrig = StringUtils.getAttributeStrNotNull(serviceRequest, "NUMRICHIESTAORIG");
if (numRichiestaOrig.equals("")) {
	numRichiestaOrig = numRichiesta;
}
String numAnno 			= StringUtils.getAttributeStrNotNull(serviceRequest, "NUMANNO");
String prgRosa 			= StringUtils.getAttributeStrNotNull(serviceRequest, "PRGROSA");
String prgTipoRosa 		= StringUtils.getAttributeStrNotNull(serviceRequest, "PRGTIPOROSA");
//rappresenta il Cpi dell'utente collegato, è stato rinominato per motivi di navigazione in SCADENZIARIO
SourceBean cpi 			= (SourceBean) serviceResponse.getAttribute("M_CPI_SMS.ROWS.ROW");
String cpirose 			= StringUtils.getAttributeStrNotNull(serviceRequest, "CPIROSE");
String cpidesc 			= StringUtils.getAttributeStrNotNull(cpi, "STRDESCRIZIONE");
String cpitel 			= StringUtils.getAttributeStrNotNull(cpi, "STRTEL");
String rifsms 			= StringUtils.getAttributeStrNotNull(cpi, "STRRIFSMS");
int prgProfiloUtente 	= user.getPrgProfilo();
String operatore		= Utils.notNull(user.getNome()) + " " + Utils.notNull(user.getCognome());

SourceBean testo = (SourceBean) serviceResponse.getAttribute("M_TESTO_SMS.ROWS.ROW");
String testo1 = StringUtils.getAttributeStrNotNull(testo, "STR30MSG1");
String testo2 = StringUtils.getAttributeStrNotNull(testo, "STR30MSG2");
String testo3 = StringUtils.getAttributeStrNotNull(testo, "STR30MSG3");
String testo4 = StringUtils.getAttributeStrNotNull(testo, "STR30MSG4");

//corpo completo del testo dell'SMS
String SMStesto = 	testo1 + " " + cpidesc + "." + testo2 + " " + testo3 + " " + operatore + " " + "telefono " + rifsms +
					" rif " + numRichiestaOrig + "/" + 	numAnno + " " + testo4;

//Oggetti per l'applicazione dello stile grafico
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
<title>Ricerca Lavoratori</title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<af:linkScript path="../../js/" />
<script language="Javascript" src="../../js/utili.js" type="text/javascript"></script>
<script language="Javascript">

function chiudi_invia(){
	doFormSubmit(document.Frm);
}

</script>

</head>
<body class="gestione">
<p align="center">
<p align="center">
<p class="titolo">Testo dell'SMS</p>
<p align="center">
<af:form name="Frm" method="POST" action="AdapterHTTP" >
	<input type="hidden" name="PAGE" value="InvioSMSDaRosaGrezzaPage" />
	<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>" />
	<input type="hidden" name="PRGRICHIESTAORIG" value="<%=prgOrig%>" />
	<input type="hidden" name="PRGRICHIESTAAZ" value="<%=prgRichiestaAz%>" />
	<input type="hidden" name="V_PRGNOMINATIVO" value="<%=prgNominativo%>" />
	<input type="hidden" name="NUMRICHIESTA" value="<%=numRichiesta%>" />
	<input type="hidden" name="NUMRICHIESTAORIG" value="<%=numRichiestaOrig%>" />
	<input type="hidden" name="NUMANNO" value="<%=numAnno%>" />
	<input type="hidden" name="PRGROSA" value="<%=prgRosa%>" />
	<input type="hidden" name="PRGTIPOROSA" value="<%=prgTipoRosa%>" />
	
	<%out.print(htmlStreamTop);%>
	<table class="main">
		<tr>
			<td colspan="2" />&nbsp;</td>
		</tr>		
		<tr>
			<td colspan="2" />
			Per modificare il testo dell'SMS predefinito inserire i caratteri desiderati</td>
		</tr>
		<tr>
			<td colspan="2" />&nbsp;</td>
		</tr>
		<tr>
			<td class="etichetta">Testo dell'SMS</td>
			<td class="campo">
				<af:textArea  validateOnPost="true" classNameBase="textarea" 
				name="testosms" cols="50" value="<%=SMStesto%>" maxlength="160"
				/>
			</td>
		</tr>				
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="2" align="center"><input class="pulsanti" type="button"
				name="avanti" value="Avanti"  onclick="chiudi_invia()"/>&nbsp;&nbsp;<input type="button" class="pulsanti"
				value="Chiudi" onClick="window.close();" /> 
			</td>
		</tr>
		<tr>
			<td colspan="2" align="center"></td>
		</tr>
	</table>
	<%out.print(htmlStreamBottom);%>
</af:form></p>
</body>
</html>
