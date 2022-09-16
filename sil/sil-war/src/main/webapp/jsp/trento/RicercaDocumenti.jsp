<%@ page contentType="text/html;charset=utf-8"%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.security.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  java.util.*,
                  java.math.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
String pageToProfile = "ListaStampeParLavPage";

ProfileDataFilter filter = new ProfileDataFilter(user, pageToProfile);
if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
}
	// NOTE: Attributi della pagina (pulsanti e link) 
	PageAttribs attributi = new PageAttribs(user, pageToProfile);
	
	String _page = "DocumentiRicercaAllegatiStampParamPage";
	

	int cdnfunzione = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	String  cdnLavoratore  = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnLavoratore");
	String  prgAzienda       = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgAzienda");
	String  prgUnita         = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgUnita");
	String  pageBack  = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "PAGEBACK");
	String  codCpi  = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "codCpi");
	String  prgDocumento = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "PRGDOCUMENTO");
	String  prgTemplateStampa  = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "PRGTEMPLATESTAMPA");
	String  dominioDati  = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "DOMINIO");	
	
	String presaVisione = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "presaVisione");
	String caricatoSucc = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "caricatoSucc");
	
	Vector tipiDoc = serviceResponse.getAttributeAsVector("COMBOTIPODOCUMENTO.ROWS.ROW");
	
	String htmlStreamTop    = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<title>Ricerca Documenti</title>

<af:linkScript path="../../js/" />
<script language="Javascript" src="../../js/documenti/protocolloDoc.js"></script>
<script language="Javascript" src="../../js/documenti/ricercaDoc.js"></script>

<script language="Javascript">

    function caricaDati() {
	    <%
	    for (int j = 0; j < tipiDoc.size(); j++) {
			SourceBean rowTipidoc = (SourceBean)tipiDoc.get(j);

			String codAmbito = (String)rowTipidoc.getAttribute("CODAMBITODOC");
			String codTipiDoc = (String)rowTipidoc.getAttribute("CODICE");
			String dscTipiDoc = (String)rowTipidoc.getAttribute("DESCRIZIONE");
			codAmbito = StringUtils.formatValue4Javascript(codAmbito);
			codTipiDoc = StringUtils.formatValue4Javascript(codTipiDoc);
			dscTipiDoc = StringUtils.formatValue4Javascript(dscTipiDoc);
			%>
				vAmbiti[<%=j%>] = new Ambito("<%=codAmbito%>","<%=codTipiDoc%>","<%=dscTipiDoc%>");
			<%
		}
		%>
	}
	
	function onLoad() {
		caricaDati();
	}

	function annulla() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit())
			return;
		
		document.frmRicerca.PAGE.value = "GestAllegatiDocumentoPage";
		doFormSubmit(document.frmRicerca);
	}
	
</script>
</head>

<body class="gestione" onload="onLoad()">

<%
	// TESTATA LAVORATORE
	if (StringUtils.isFilled(cdnLavoratore)) {
		InfCorrentiLav testata = new InfCorrentiLav(cdnLavoratore, user);
		testata.show(out);
	}
		// TESTATA AZIENDA
	if (StringUtils.isFilled(prgAzienda)) {
		InfCorrentiAzienda testata = new InfCorrentiAzienda(prgAzienda, prgUnita);
		
		testata.show(out);
	}
%>

<p class="titolo">Ricerca Documenti</p>

<af:form name="frmRicerca" action="AdapterHTTP" method="POST">

<input type="hidden" name="PAGE" value="" />
<input type="hidden" name="cdnFunzione" value="<%=cdnfunzione%>" />
<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>" />
<input type="hidden" name="prgUnita" value="<%=prgUnita%>" />
<input type="hidden" name="prgAzienda" value="<%=prgAzienda%>" />

<input type="hidden" id="pageBack" name="PAGEBACK" value="<%=pageBack%>">
<input type="hidden" name="codCpi" value="<%= codCpi %>">
<input type="hidden" name="OPERATIONALLEGATI" value="">
<input type="hidden" name="PRGDOCUMENTO" value="<%=prgDocumento%>">
<input type="hidden" name="PRGTEMPLATESTAMPA" value="<%=prgTemplateStampa%>">
<input type="hidden" name="DOMINIO" value="<%=dominioDati%>">
<input type="hidden" name="presaVisione" value="<%=presaVisione%>">
<input type="hidden" name="caricatoSucc" value="<%=caricatoSucc%>">
		
<%= htmlStreamTop %>
<table class="main">
	
	<tr class="note">
		<td colspan="2">
			<div class="sezione2">Protocollo</div>
		</td>
	</tr>
	<%-- ***************************************************************************** --%>
	<%@ include file="ProtocolloDoc_Elemento.inc" %>
	<%-- ***************************************************************************** --%>

	<tr class="note">
		<td colspan="2">
			<div class="sezione2"></div>
		</td>
	</tr>

	<%-- ***************************************************************************** --%>
	<%@ include file="ricercaCampiDoc.inc" %>
	<%-- ***************************************************************************** --%>

	<!--************************************************************************ -->
	<tr>
		<td colspan="2">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="2">
			<span class="bottoni">
				<input type="submit" name="cerca" value="Cerca" class="pulsanti" onClick="return checkCampiRicercaAllegatiStampeParam();" />&nbsp;&nbsp;
				<input type="button" name="btnBack" value="Indietro" class="pulsanti" onClick="annulla();" />
			</span>
		</td>
	</tr>
	
</table>
<%= htmlStreamBottom %>

</af:form>

</body>
</html>
