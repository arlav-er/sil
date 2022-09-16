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

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%
	String  titolo = "Retribuzione annuale";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 
	
	// Lettura parametri dalla REQUEST
	String codccnl   	= SourceBeanUtils.getAttrStrNotNull(serviceRequest, "codccnl");
	String numLivello   = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"numLivello");
	String codOrario    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"codOrario");
	String numOreSett   = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"oresett");
	
	// Recupero tutta la RISPOSTA DI UN MODULO
	SourceBean dettModule = (SourceBean) serviceResponse.getAttribute("M_CalcolaRetribuzione");
	String esito 			= SourceBeanUtils.getAttrStrNotNull(dettModule,"ESITO");
	String descrizioneEsito = SourceBeanUtils.getAttrStrNotNull(dettModule,"DESCRIZIONEESITO");
	String retribuzione 	= SourceBeanUtils.getAttrStrNotNull(dettModule,"RETRIBUZIONE");
	String lordoMensile 	= SourceBeanUtils.getAttrStrNotNull(dettModule,"LORDOMENSILE");
	String mensilita 		= SourceBeanUtils.getAttrStrNotNull(dettModule,"MENSILITA");
	String divisoreOrario 	= SourceBeanUtils.getAttrStrNotNull(dettModule,"DIVISOREORARIO");
	String tipoLivello      = SourceBeanUtils.getAttrStrNotNull(dettModule,"TIPOLIVELLO");
	String descrLivello		= SourceBeanUtils.getAttrStrNotNull(dettModule,"DESCRLIVELLO");
	
	numLivello += " - " +descrLivello;
	
	SourceBean dettModuleCCNL = (SourceBean) serviceResponse.getAttribute("M_DescrizioneCCNL");
	String descrizioneCCNL = SourceBeanUtils.getAttrStrNotNull(dettModuleCCNL,"ROWS.ROW.STRDESCRIZIONE");
	
	
	String ccnl = codccnl + " - " + descrizioneCCNL;

	// Stringhe con HTML per layout tabelle
	String htmlStreamTop    = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
<title><%= titolo %></title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />

<af:linkScript path="../../js/"/>

<script language="Javascript">

var retribuzioneCalcolata='<%=retribuzione%>';

function AggiornaForm() {
	if (retribuzioneCalcolata!="0" || window.opener.document.Frm1.codTipoAss.value.toUpperCase()=="C.01.00") {
		window.opener.document.Frm1.decRetribuzioneAnn.value = retribuzioneCalcolata;
	} else {
		window.opener.document.Frm1.decRetribuzioneAnn.value = '';
	}
	
	window.opener.valorizzaCompensoMensile();
	window.close();
}

</script>
</head>

<body class="gestione" onload="onLoad()">


<p class="titolo"><%= titolo %></p>

<af:showErrors />
<af:showMessages prefix="M_CalcolaRetribuzione"/>

<%= htmlStreamTop %>
<table class="main">
	<tr>
	 	<td class="etichetta" nowrap>CCNL</td>
		<td class="campo">
			<af:textBox classNameBase="input" type="text" name="codccnl" size="120" value="<%=ccnl%>" readonly="true"/>
		</td>
	</tr>
	<tr>
		<td class="etichetta" nowrap>Livello</td>
		<td class="campo">
			<af:textBox classNameBase="input" type="text" name="numLivello" size="120" value="<%=numLivello%>" readonly="true"/>
		</td>
	</tr>
	<tr>
		<td class="etichetta" nowrap>Tipo Livello</td>
		<td class="campo">
			<af:textBox classNameBase="input" type="text" name="tipoLivello" size="120" value="<%=tipoLivello%>" readonly="true"/>
		</td>
	</tr>
	<tr>
		<td class="etichetta" nowrap>Lordo mensile</td>
		<td class="campo">
			<af:textBox classNameBase="input" type="text" name="lordoMensile" size="12" value="<%=lordoMensile%>" readonly="true"/>
		</td>
	</tr>
	<tr>
		<td class="etichetta" nowrap>Mensilità</td>
		<td class="campo">
			<af:textBox classNameBase="input" type="text" name="mensilita" size="12" value="<%=mensilita%>" readonly="true"/>
		</td>
	</tr>
	<tr>
	 	<td class="etichetta" nowrap>Orario</td>
	 	<td class="campo">
			<af:comboBox moduleName="ComboTipoOrario" selectedValue="<%=codOrario%>" title="Orario" 
				classNameBase="input" name="codOrario" addBlank="true" disabled="true"/>
		</td>
	</tr>
<% if(numOreSett!="") { %>	
	<tr>
		<td class="etichetta" nowrap>Ore settimanali</td>
		<td class="campo">
			<af:textBox classNameBase="input" type="text" name="numOreSett" size="12" value="<%=numOreSett%>" readonly="true"/>
		</td>
	</tr>
	<tr>
		<td class="etichetta" nowrap>Divisore orario</td>
		<td class="campo">
			<af:textBox classNameBase="input" type="text" name="divisoreOrario" size="12" value="<%=divisoreOrario%>" readonly="true"/>
		</td>
	</tr>
<% } %>	
<tr><td colspan="2">&nbsp;</td></tr>
<% if(!esito.equalsIgnoreCase("KO")) { %>
	<tr>
		<td class="etichetta" nowrap>Minima retribuzione annua lorda</td>
		<td class="campo">
			<af:textBox classNameBase="input" type="text" name="retribuzione" size="12" value="<%=retribuzione%>" readonly="true"/>
		</td>
	</tr>
	
<% } else { %>
	<tr>
		<td class="etichetta" nowrap>Messaggio</td>
		<td class="campo">
			<b><%=descrizioneEsito%></b>
		</td>
	</tr>
	
	
<% } %>
</table>
<%= htmlStreamBottom %>
<center>
<br>
<% if(!esito.equalsIgnoreCase("KO")){ %>
     	<input type="button" class="pulsanti" value="Salva" onclick="AggiornaForm();">
<%}%> 
		<input type="button" class="pulsanti" value="Chiudi" onClick="window.close();"/>
</center>

</body>
</html>
