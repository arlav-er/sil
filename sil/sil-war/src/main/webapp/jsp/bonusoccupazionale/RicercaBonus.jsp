<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/Function_CommonRicercaComune.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page
	import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.StringUtils,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*"%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%
	// NOTE: Attributi della pagina (pulsanti e link) 
	PageAttribs attributi = new PageAttribs(user, "RicercaBonusPage");
	boolean canModify = false;
	
	String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
	String strCodiceFiscale = StringUtils.getAttributeStrNotNull(serviceRequest, "strCodiceFiscale");
	String tipoRicerca = StringUtils.getAttributeStrNotNull(serviceRequest, "tipoRicerca");
	String datadesioneda = StringUtils.getAttributeStrNotNull(serviceRequest, "datadesioneda");
	String datadesionea = StringUtils.getAttributeStrNotNull(serviceRequest, "datadesionea");
	String codProv = StringUtils.getAttributeStrNotNull(serviceRequest, "codProv");
	String flgPresaCarico = StringUtils.getAttributeStrNotNull(serviceRequest, "flgPresaCarico");
	
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>
<head>
<title>Crescere in Digitale</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<af:linkScript path="../../js/" />

<script language="Javascript">
	
	<%
	//Genera il Javascript che si occuperà di inserire i links nel footer
	attributi.showHyperLinks(out, requestContainer, responseContainer, "");
	%>

	function checkCampiObbligatori() {
		if (document.Frm1.presaInCarico.checked) {
			document.Frm1.flgPresaCarico.value = "S";
		}
		else {
			document.Frm1.flgPresaCarico.value = "N";	
		}
		return true;
	}

</script>

</head>
<body class="gestione" onload="rinfresca()">
	<p class="titolo">Crescere in Digitale</p>
	<%
		out.print(htmlStreamTop);
	%>
	<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="checkCampiObbligatori()">
		<table class="main">
			
			<tr>
				<td colspan="2">&nbsp;</td>
			</tr>
			
			<tr>
			<td class="etichetta">Codice fiscale</td>
			<td class="campo">
				<af:textBox type="text" name="strCodiceFiscale" value="<%=strCodiceFiscale%>" size="20" maxlength="16" />
			</td>
			</tr>
			
			<tr>
				<td class="etichetta">tipo ricerca</td>
				<td class="campo">
					<table colspacing="0" colpadding="0" border="0">
						<tr>
							<%if (tipoRicerca.equals("iniziaPer")) {%>
							<td><input type="radio" name="tipoRicerca" value="esatta" />esatta&nbsp;&nbsp;&nbsp;&nbsp;</td>
							<td><input type="radio" name="tipoRicerca" value="iniziaPer" CHECKED />inizia per</td>
							<% } else {%>
							<td><input type="radio" name="tipoRicerca" value="esatta" CHECKED />esatta&nbsp;&nbsp;&nbsp;&nbsp;</td>
							<td><input type="radio" name="tipoRicerca" value="iniziaPer" />inizia per</td>
							<%}%>
						</tr>
					</table>
				</td>
			</tr>
			
			<tr>
				<td colspan="2"><hr /></td>

			</tr>
			
			<tr>
				<td class="etichetta" nowrap>Data adesione da</td>
				<td class="campo"><af:textBox type="date" name="datadesioneda" title="Data adesione da" validateOnPost="true"
						value="<%=datadesioneda%>" size="10" maxlength="10" />
					&nbsp;&nbsp;a&nbsp;&nbsp; <af:textBox type="date" name="datadesionea" title="Data adesione a" validateOnPost="true"
						value="<%=datadesionea%>" size="10" maxlength="10" /></td>
			</tr>
			
			<tr>
			<td class="etichetta" nowrap>Provincia</td>
			<td class="campo">
				<af:comboBox name="codProv" moduleName="M_GetProvince" addBlank="true" selectedValue="<%=codProv%>" />
			</td>
			</tr>
			
			<tr>
			  <td class="etichetta">Presa in carico</td>
			  <td class="campo">
			  <input type="checkbox" name="presaInCarico" value="1" <% if(flgPresaCarico.equals("S")) { out.print("CHECKED"); }%>>
			  </td>
			</tr>
			
			<tr>
				<td colspan="2">&nbsp;</td>
			</tr>
			<tr>
				<td colspan="2" align="center"><input class="pulsanti" type="submit" name="cerca" value="Cerca" /> &nbsp;&nbsp; 
				<input type="reset" class="pulsanti" value="Annulla" /></td>
			</tr>
			
			<input type="hidden" name="PAGE" value="ListaBonusPage" />
			<input type="hidden" name="cdnFunzione" value="<%=_funzione%>" />
			<input type="hidden" name="flgPresaCarico" value="" />
			
		</table>
	</af:form>
	<%
		out.print(htmlStreamBottom);
	%>
</body>
</html>

