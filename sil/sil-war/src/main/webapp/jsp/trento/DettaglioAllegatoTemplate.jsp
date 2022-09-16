<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page
	import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,java.math.*,
                java.lang.*,java.text.*,java.util.*, 
                it.eng.sil.security.*,
                it.eng.sil.util.*, it.eng.sil.module.agenda.ShowApp,
                it.eng.afExt.utils.*"%>


<%@ taglib uri="aftags" prefix="af"%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>
<%

String pageToProfile = "RicercaTemplatePage";

ProfileDataFilter filter = new ProfileDataFilter(user, pageToProfile);
if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
}
PageAttribs attributi = new PageAttribs(user, pageToProfile);
boolean canModify = attributi.containsButton("SALVA_TEMPLATE");
//da modificare
//canModify = true;
	Testata operatoreInfo = null;
	String _page = (String) serviceRequest.getAttribute("PAGE");
	
	
	String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");


	String cdnUtins = "";
	String cdnUtmod = "";
	String dtmins = "";
	String dtmmod = "";
	
	String prgConfigProt = serviceRequest.containsAttribute("PRGCONFIGPROT")
			? serviceRequest.getAttribute("PRGCONFIGPROT").toString() : "";
	String prgTemplate = serviceRequest.containsAttribute("PRGTEMPLATESTAMPA")
			? serviceRequest.getAttribute("PRGTEMPLATESTAMPA").toString() : "";
	String CODTIPODOMINIO = serviceRequest.containsAttribute("CODTIPODOMINIO")
			? serviceRequest.getAttribute("CODTIPODOMINIO").toString() : "";		
	String prgConfigProtDocTipo = serviceRequest.containsAttribute("PRGCONFIGPROTDOCTIPO")
			? serviceRequest.getAttribute("PRGCONFIGPROTDOCTIPO").toString() : "";
	String codAmbitoTem = "";
	String strDescrizione = "";
	BigDecimal numKloConfProtDoc = null;
	String flgObbl = "";
	String tipoAllegatoTemplate = "";
	BigDecimal prgAllegatoTemplateStampa = null;
	String strPrgAllegatoTemplate = "";
	
	if (serviceResponse.containsAttribute("MDettaglioAllegatoTemplate.ROWS.ROW")) {
		BigDecimal prgConfigProtDocTipoDB = (BigDecimal)serviceResponse.getAttribute("MDettaglioAllegatoTemplate.ROWS.ROW.PRGCONFIGPROTDOCTIPO");
		BigDecimal prgConfigProtDB = (BigDecimal)serviceResponse.getAttribute("MDettaglioAllegatoTemplate.ROWS.ROW.PRGCONFIGPROT");
		prgConfigProtDocTipo = prgConfigProtDocTipoDB.toString();
		prgConfigProt = prgConfigProtDB.toString();
		codAmbitoTem = serviceResponse.getAttribute("MDettaglioAllegatoTemplate.ROWS.ROW.CODTIPODOCUMENTO")!=null ? 
				serviceResponse.getAttribute("MDettaglioAllegatoTemplate.ROWS.ROW.CODTIPODOCUMENTO").toString():"";
		strDescrizione = serviceResponse.getAttribute("MDettaglioAllegatoTemplate.ROWS.ROW.STRDESCRIZIONE")!=null ? 
				serviceResponse.getAttribute("MDettaglioAllegatoTemplate.ROWS.ROW.STRDESCRIZIONE").toString():"";
		numKloConfProtDoc = (BigDecimal)serviceResponse.getAttribute("MDettaglioAllegatoTemplate.ROWS.ROW.NUMKLOCONFPROTDOC");
		flgObbl = serviceResponse.getAttribute("MDettaglioAllegatoTemplate.ROWS.ROW.FLGOBBL")!=null ? 
				serviceResponse.getAttribute("MDettaglioAllegatoTemplate.ROWS.ROW.FLGOBBL").toString():"";
		prgAllegatoTemplateStampa = (BigDecimal)serviceResponse.getAttribute("MDettaglioAllegatoTemplate.ROWS.ROW.PRGTEMPLATESTAMPA");
		if (prgAllegatoTemplateStampa != null) {
			tipoAllegatoTemplate = "1";
			strPrgAllegatoTemplate = prgAllegatoTemplateStampa.toString();
		}
	}
	
	String btnAnnulla = "Torna alla lista";

	operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
	
	
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>

<head>
  <%@ include file="../global/fieldChanged.inc" %>
  
  <script type="text/javascript">

	var prgConfigProtDocTipo = "<%=prgConfigProtDocTipo%>";
  
	function annulla() {
		if (flagChanged == true) {
			if (confirm("I dati sono cambiati.\nProcedere lo stesso ?")) {
				window.close();
			}
		} else {
			window.close();
		}
	}

	function visualizzaTipoAllegato() {
		var sezioneDoc = document.getElementById("sezInfoTipoDoc");
		var sezioneTemplate = document.getElementById("sezInfoTipoTemplate");
		if (document.frmAllegatoTemplate.tipoAllegatoTemplate.checked) {
			sezioneTemplate.style.display="inline";
			sezioneDoc.style.display="none";
		}
		else {
			sezioneTemplate.style.display="none";
			sezioneDoc.style.display="inline";
		}
	}

	function checkCampiObbligatori() {
		if (document.frmAllegatoTemplate.tipoAllegatoTemplate.checked) {
			if (document.frmAllegatoTemplate.PRGTEMPLATEALLEGATO.value == "") {
				alert("Il campo Nome template è obbligatorio");
				return false;
			}
		}
		else {
			if (document.frmAllegatoTemplate.CODTIPODOCUMENTO.value == "") {
				alert("Il campo Tipo documento è obbligatorio");
				return false;
			}
			if (document.frmAllegatoTemplate.STRDESCRIZIONE.value == "") {
				alert("Il campo Descrizione è obbligatorio");
				return false;
			}
		}

		window.opener.document.frmTemplate.PAGE.value = "DettaglioTemplatePage";
		if (prgConfigProtDocTipo == '') {
			window.opener.document.frmTemplate.MODULE.value = "MInsAllegatoConfig";
		}
		else {
			window.opener.document.frmTemplate.MODULE.value = "MAggiornaAllegatoConfig";
			window.opener.document.frmTemplate.NUMKLOCONFPROTDOC.value = "<%=Utils.notNull(numKloConfProtDoc)%>";
			window.opener.document.frmTemplate.PRGCONFIGPROTDOCTIPO.value = prgConfigProtDocTipo;
		}
		if (document.frmAllegatoTemplate.tipoAllegatoTemplate.checked) {
			window.opener.document.frmTemplate.tipoAllegatoTemplate.value = "1";
		}
		window.opener.document.frmTemplate.CODTIPODOCUMENTO.value = document.frmAllegatoTemplate.CODTIPODOCUMENTO.value;
		window.opener.document.frmTemplate.STRDESCRIZIONE.value = document.frmAllegatoTemplate.STRDESCRIZIONE.value;
		window.opener.document.frmTemplate.PRGTEMPLATEALLEGATO.value = document.frmAllegatoTemplate.PRGTEMPLATEALLEGATO.value;
		if (document.frmAllegatoTemplate.FLGOBBL.checked) {
			window.opener.document.frmTemplate.FLGOBBL.value = document.frmAllegatoTemplate.FLGOBBL.value;
		}	
		window.opener.document.frmTemplate.submit();
		window.close();
	}
  </script>
  
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Allegati Template</title>
</head>
<body>
	
	<p class="titolo">Associazione allegati alla configurazione Template</p>
	
	<%
	out.print(htmlStreamTop);
	%>
	
	<af:form name="frmAllegatoTemplate" action="AdapterHTTP" method="POST">
		<input type="hidden" id="page" name="PAGE" value="<%=_page%>">
		<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
		<input type="hidden" name="PRGCONFIGPROTDOCTIPO" value="<%=prgConfigProtDocTipo%>">
		<input type="hidden" name="PRGCONFIGPROT" value="<%=prgConfigProt%>">
		<input type="hidden" name="PRGTEMPLATESTAMPA" value="<%=prgTemplate%>">
		<input type="hidden" name="CODTIPODOMINIO" value="<%=CODTIPODOMINIO%>">
		<input type="hidden" name="NUMKLOCONFPROTDOC" value="<%=Utils.notNull(numKloConfProtDoc)%>">
		
		<p align="center">
		<table class="main">
			
			<tr>
				<td class="etichetta">Template</td>
				<td class="campo">
					<input type="checkbox" name="tipoAllegatoTemplate" value="1" onclick="visualizzaTipoAllegato();"
						<% if(tipoAllegatoTemplate.equals("1")) { out.print("CHECKED"); }%>>
  				</td>
			</tr>
			
			<tr>
			<td colspan="2">
			<%if(tipoAllegatoTemplate.equals("1")) {%>
			<div id="sezInfoTipoDoc" style="display: none">
			<%} else {%>
			<div id="sezInfoTipoDoc" style="display:inline">
			<%}%>
			<table class="main" width="100%" border="0">
				<tr>
					<td class="etichetta">Tipo documento</td>
					<td class="campo"><af:comboBox name="CODTIPODOCUMENTO"
							size="1" title="Tipo documento" multiple="false"
							required="false" focusOn="false" moduleName="ComboTipoDocumento"
							classNameBase="input" disabled="<%=String.valueOf(!canModify)%>"
							onChange="fieldChanged();"
							selectedValue="<%=codAmbitoTem%>" addBlank="true"
							blankValue="" />
					</td>
				</tr>
				<tr>
					<td class="etichetta">Descrizione</td>
					<td class="campo"><af:textBox type="text" 
							name="STRDESCRIZIONE"
							title="Descrizione" required="false" size="50" value="<%=strDescrizione%>"
							onKeyUp="fieldChanged();" classNameBase="input"
							readonly="<%=String.valueOf(!canModify)%>" maxlength="100" />
					</td>
				</tr>
			</table>
			</div>
			</td>
			</tr>
			
			<tr>
			<td colspan="2">
			<%if(tipoAllegatoTemplate.equals("1")) {%>
			<div id="sezInfoTipoTemplate" style="display:inline">
			<%} else {%>
			<div id="sezInfoTipoTemplate" style="display:none">
			<%}%>
			<table class="main" width="100%" border="0">
				<tr>
					<td class="etichetta">Nome template</td>
					<td class="campo"><af:comboBox name="PRGTEMPLATEALLEGATO"
							size="1" title="Nome template" multiple="false"
							required="false" focusOn="false" moduleName="MAllegatoIsTemplate"
							classNameBase="input" disabled="<%=String.valueOf(!canModify)%>"
							onChange="fieldChanged();"
							selectedValue="<%=strPrgAllegatoTemplate%>" addBlank="true"
							blankValue="" />
					</td>
				</tr>
			</table>
			</div>
			</td>
			</tr>
			
			<tr>
				<td class="etichetta">Obbligatorio</td>
				<td class="campo">
					<input type="checkbox" name="FLGOBBL" value="1" <%if(flgObbl.equals("1")) { out.print("CHECKED"); }%>>
  				</td>
			</tr>
			
		</table>
		
		<table>
		<tr>
		<%if (canModify) {
			if (prgConfigProtDocTipo.equals("")) {%>
				<td align="center">
				<input type="button" class="pulsanti" name="btnSalva" value="Salva" onClick="checkCampiObbligatori();">
				</td>
			<%} else {%>
				<td align="center">
				<input type="button" class="pulsanti" name="btnAggiorna" value="Aggiorna" onClick="checkCampiObbligatori();">
				</td>
			<%}
		}%>
		<td align="center">
		<input type="button" class="pulsanti" name="ANNULLA" value="Chiudi" onClick="annulla();">
		</td>
		</tr>
		</table>
		
		</p>
	</af:form>
	<div align="center">
		<%
			operatoreInfo.showHTML(out);
		%>
	</div>
	<%
		out.print(htmlStreamBottom);
	%>
</body>
</html>