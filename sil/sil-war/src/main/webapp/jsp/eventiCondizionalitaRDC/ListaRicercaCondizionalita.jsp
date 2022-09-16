<%@ page contentType="text/html;charset=utf-8"%>
 
<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                java.math.*,
                 it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
	String _page = (String) serviceRequest.getAttribute("PAGE");
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);

	boolean canView = filter.canView();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}
	
    PageAttribs attributi = new PageAttribs(user, _page);

    boolean canExportCsv = attributi.containsButton("ESPORTA_CONDIZIONALITA_CSV");

	int     cdnfunzione      = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	String  _funzione   = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");
 	String  cdnLavoratore  = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnLavoratore");

	 String strCodiceFiscaleLav = StringUtils.getAttributeStrNotNull(serviceRequest,"codiceFiscaleLavoratore");
	 String strCognomeLav = StringUtils.getAttributeStrNotNull(serviceRequest,"cognome");
	 String strNomeLav = StringUtils.getAttributeStrNotNull(serviceRequest,"nome");
	 String dataEventoDa	= StringUtils.getAttributeStrNotNull(serviceRequest, "dataEventoDa");
	 String dataEventoA = StringUtils.getAttributeStrNotNull(serviceRequest, "dataEventoA");
	 String statoInvio = StringUtils.getAttributeStrNotNull(serviceRequest, "statoInvio");
	 String tipoDomanda = StringUtils.getAttributeStrNotNull(serviceRequest, "tipoDomanda");
	 String codStatoDomanda = StringUtils.getAttributeStrNotNull(serviceRequest, "codStatoDomanda");
	 String tipoEvento = StringUtils.getAttributeStrNotNull(serviceRequest, "tipoEvento");

	 String ordDataEventoDC = StringUtils.getAttributeStrNotNull(serviceRequest, "ordDataEventoDC");
	 String ordDataEvento = StringUtils.getAttributeStrNotNull(serviceRequest, "ordDataEvento");
	 String ordINPSDC = StringUtils.getAttributeStrNotNull(serviceRequest, "ordINPSDC");
	 String ordINPS = StringUtils.getAttributeStrNotNull(serviceRequest, "ordINPS");
	 String ordDataInvioDC = StringUtils.getAttributeStrNotNull(serviceRequest, "ordDataInvioDC");
	 String ordDataInvio = StringUtils.getAttributeStrNotNull(serviceRequest, "ordDataInvio");
	 String ordCF = StringUtils.getAttributeStrNotNull(serviceRequest, "ordCF");
	 
	 String isComeBack = StringUtils.getAttributeStrNotNull(serviceRequest, "isComeBack");
	 
	 Integer numeroRighe = (Integer) responseContainer.getServiceResponse().getAttribute("M_ListaRicercaCondizionalita.ROWS.NUM_RECORDS");

	String txtOut = "";
	String parameters = "&cdnFunzione="+ _funzione;
	parameters = parameters +  "&isComeBack=true";
	 
	if (StringUtils.isFilledNoBlank(strCognomeLav)) {
	//	parameters = parameters + "&cognome=" + strCognomeLav;
		txtOut += "Cognome <strong>" + strCognomeLav + "</strong>; ";
	}
	if (StringUtils.isFilledNoBlank(strNomeLav)) {
	//		parameters = parameters + "&nome=" + strNomeLav;
		txtOut += "Nome <strong>" + strNomeLav + "</strong>; ";
	}
	if (StringUtils.isFilledNoBlank(strCodiceFiscaleLav)) {
	//		parameters = parameters + "&codiceFiscaleLavoratore=" + strCodiceFiscaleLav;
	//		parameters = parameters + "&cdnLavoratore=" + cdnLavoratore;
		txtOut += "Codice fiscale <strong>" + strCodiceFiscaleLav + "</strong>; ";
	}
	if (StringUtils.isFilledNoBlank(statoInvio)) {
	//		parameters = parameters + "&statoInvio=" + strCodiceFiscaleLav;
		String valoreTemp ="";
		if (statoInvio.equals("I"))
			valoreTemp = "Inviati";
		if (statoInvio.equals("D"))
			valoreTemp = "Da inviare";
		txtOut += "Stato invio <strong>" + valoreTemp + "</strong>; ";
	}
	if (StringUtils.isFilledNoBlank(tipoDomanda)) {
	//		parameters = parameters + "&tipoDomanda=" + tipoDomanda;
		txtOut += "Tipo domanda <strong>" + tipoDomanda + "</strong>; ";
	}
	if (StringUtils.isFilledNoBlank(dataEventoDa)) {
	//		parameters = parameters + "&dataEventoDa=" + dataEventoDa;
		txtOut += "  Data evento da <strong>" + dataEventoDa + "</strong>; ";
	}
	if (StringUtils.isFilledNoBlank(dataEventoA)) {
	//		parameters = parameters + "&dataEventoA=" + dataEventoA;
		txtOut += "Data evento a <strong>" + dataEventoA + "</strong>; ";
	}
	if (StringUtils.isFilledNoBlank(tipoEvento)) {
	//		parameters = parameters + "&tipoEvento=" + tipoEvento;
		txtOut += "Tipo evento <strong>" + tipoEvento + "</strong>; ";
	}
	/* if (StringUtils.isFilledNoBlank(codStatoDomanda)) {
		parameters = parameters + "&codStatoDomanda=" + codStatoDomanda;
	}
	if (StringUtils.isFilledNoBlank(ordDataEventoDC)) {
		parameters = parameters + "&ordDataEventoDC=" + ordDataEventoDC;
	}
	if (StringUtils.isFilledNoBlank(ordDataEvento)) {
		parameters = parameters + "&ordDataEvento=" + ordDataEvento;
	}
	if (StringUtils.isFilledNoBlank(ordINPSDC)) {
		parameters = parameters + "&ordINPSDC=" + ordINPSDC;
	}
	if (StringUtils.isFilledNoBlank(ordINPS)) {
		parameters = parameters + "&ordINPS=" + ordINPS;
	}
	if (StringUtils.isFilledNoBlank(ordDataInvioDC)) {
		parameters = parameters + "&ordDataInvioDC=" + ordDataInvioDC;
	}
	if (StringUtils.isFilledNoBlank(ordDataInvio)) {
		parameters = parameters + "&ordDataInvio=" + ordDataInvio;
	}
	if (StringUtils.isFilledNoBlank(ordCF)) {
		parameters = parameters + "&ordCF=" + ordCF;
	} */
%>
 
<html>
<head>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/> 
 <af:linkScript path="../../js/"/>
   
 <script type="text/Javascript">
 
	function tornaAllaRicerca() {
  		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
       	url="AdapterHTTP?PAGE=RicercaCondizionalitaPage" + "<%=parameters%>";
		setWindowLocation(url);
	} 
 
 
	function esportaCsv() {    
		var doAlert =<%= numeroRighe.intValue()%>;
		if(doAlert<=0){
			alert("Non ci sono eventi da esportare");
			return false;
		}else{
			url="AdapterHTTP?PAGE=EventiCondRisultRicercaCsvPage&CSV=CSV";
		    url += "<%=parameters%>";             
		    window.location = url;
		    return false;
		}
	}
</script>
</head>
<body  class="gestione">
<br>
	<p class="titolo">Elenco eventi condizionalit&agrave;</p>

	<p align="center">
		<%
			if (txtOut.length() > 0) {
				txtOut = "Filtri di ricerca:<br/> " + txtOut;
		%>
	
	<table cellpadding="2" cellspacing="10" border="0" width="100%">
		<tr>
			<td
				style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color: #dcdcdc">
				<%
					out.print(txtOut);
				%>
			</td>
		</tr>
	</table>
	<%
		}
	%>
<af:list moduleName="M_ListaRicercaCondizionalita"/>
<table align="center">
	<tr>
		<td colspan="2" align="center">
			<input class="pulsante" type="button" name="torna" value="Torna alla ricerca" onclick="tornaAllaRicerca()"/>
		</td>
	</tr>
	<% if(canExportCsv){%>
	<tr>
		<td colspan="2" align="center">
			<input class="pulsante" type="button" value="Esporta in CSV" onClick="esportaCsv()"/>
		</td>
	</tr>
  	<%}%>
</table>

</body>
</html>