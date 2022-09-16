<%@ page contentType="text/html;charset=utf-8"%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*" %>
      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String  _page          = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	int     cdnfunzione    = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	String  cdnLavoratore  = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnLavoratore");
	String  prgAzienda     = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "prgAzienda");
	String  prgUnita       = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "prgUnita");
	boolean lookLavoratore = SourceBeanUtils.getAttrBoolean(serviceRequest, "lookLavoratore", false);
	boolean lookAzienda    = SourceBeanUtils.getAttrBoolean(serviceRequest, "lookAzienda", false);
	String  contesto       = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "contesto");
	String  goBackListPage = _page;
%>

<html>
<head>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<af:linkScript path="../../js/"/>
<script language="javascript">

	function tornaRicerca() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
		
		var url = "AdapterHTTP?PAGE=DocumentiRicercaPage" +
							"&cdnLavoratore=<%=cdnLavoratore%>" +
							"&prgAzienda=<%=prgAzienda%>&prgUnita=<%=prgUnita%>" +
							"&lookLavoratore=<%=lookLavoratore%>" +
							"&lookAzienda=<%=lookAzienda%>" +
							"&contesto=<%=contesto%>" +
							"&cdnfunzione=<%=cdnfunzione%>";
		setWindowLocation(url);
	}


	function nuovo() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
	
		var url = "AdapterHTTP?PAGE=DettagliDocumentoPage" +
							"&NUOVO=true" +
							"&cdnLavoratore=<%=cdnLavoratore%>" +
							"&prgAzienda=<%=prgAzienda%>&prgUnita=<%=prgUnita%>" +
							"&lookLavoratore=<%=lookLavoratore%>" +
							"&lookAzienda=<%=lookAzienda%>" +
							"&contesto=<%=contesto%>" +
							"&goBackListPage=<%= goBackListPage %>" +
							"&cdnfunzione=<%=cdnfunzione%>";
		setWindowLocation(url);
	}

	
	function onLoad() {
		checkError();
		rinfresca();
	}
	
</script>

</head>

<body class="gestione" onload="onLoad()">

<af:error/>

<%--
StringBuffer txtOut = new StringBuffer();
RicercaUtils.addUsedFilter(txtOut, "lavoratore", serviceRequest, "codiceFiscaleLavoratore");
RicercaUtils.addUsedFilter(txtOut, "azienda", serviceRequest, "aziRagione");
RicercaUtils.addUsedFilter(txtOut, "anno protocollazione", serviceRequest, "annoProtocollo");
RicercaUtils.addUsedFilter(txtOut, "numero protocollazione", serviceRequest, "numeroProtocollo");
RicercaUtils.addUsedFilter(txtOut, "data protocollazione", serviceRequest, "dataProtocollo");
RicercaUtils.addUsedFilter(txtOut, "documento di Input/Output", serviceRequest, "docIO");
RicercaUtils.addUsedFilter(txtOut, "riferimento (codice)", serviceRequest, "ambito");
RicercaUtils.addUsedFilter(txtOut, "tipo documenti (codici)", serviceRequest, "tipoDocumento");
RicercaUtils.addUsedFilter(txtOut, "autocertificazione", serviceRequest, "autoCertificazione");
RicercaUtils.addUsedFilter(txtOut, "documento di identificazione principale", serviceRequest, "docIdentificazione");
RicercaUtils.addUsedFilter(txtOut, "data inizio validit&agrave;", serviceRequest, "DatInizio");
RicercaUtils.addUsedFilter(txtOut, "data fine validit&agrave;", serviceRequest, "DatFine");
RicercaUtils.addUsedFilter(txtOut, "numero di protocollo esterno", serviceRequest, "strNumDoc");
RicercaUtils.addUsedFilter(txtOut, "ente che ha rilasciato", serviceRequest, "strEnteRilascio");
if (StringUtils.isFilled(SourceBeanUtils.getAttrStr(serviceRequest, "strEnteRilascio")))
{ RicercaUtils.addUsedFilter(txtOut, "", serviceRequest, "tipoRicercaEnteRil", " (", " ", "); "); }
RicercaUtils.addUsedFilter(txtOut, "codice CPI di riferimento", serviceRequest, "codCpi");
%>
<%= RicercaUtils.getTableWithData(txtOut) %>
--%>
<af:form name="formina" dontValidate="true">

<table class="main" width="100%">
	<tr>
		<td>
			<af:list moduleName="M_GetListDocumenti" />
		</td>
	</tr>
	<tr>
		<td>
			<span class="bottoni">
				<input type="button" class="pulsanti" value="Torna alla ricerca"
				       onClick="tornaRicerca()" />
			</span>
		</td>
	</tr>


	<%
	// gestione attributi	
	PageAttribs attributiDettaglio = new PageAttribs(user, "DocumentiAssociatiPage");
	if (attributiDettaglio.containsButton("nuovo")) {
	%>
	<tr>
		<td>
			<span class="bottoni">
				<input type="button" class="pulsante" onClick="nuovo()"
				       value="Nuovo documento" />
			</span>
		</td>
	</tr>
	<%
	}
	%>

</table>

</af:form>

</body>
</html>
