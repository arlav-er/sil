<%@ page contentType="text/html;charset=utf-8"%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.SourceBean,
					com.engiweb.framework.base.*,
                  it.eng.sil.security.*,java.util.*, 
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  com.engiweb.framework.message.*,
                it.eng.afExt.utils.MessageCodes" %>
      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
String pageToProfile = "ListaStampeParLavPage";

ProfileDataFilter filter = new ProfileDataFilter(user, pageToProfile);
if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
}
	// NOTE: Attributi della pagina (pulsanti e link) 
	PageAttribs attributi = new PageAttribs(user, pageToProfile);
	boolean canSalva = attributi.containsButton("SALVA_ALLEGATO");
	String  _page = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE");
	int cdnfunzione = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	String  cdnLavoratore = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnLavoratore");
	String prgAzienda = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgAzienda");
	String  prgUnita         = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgUnita");
	String  pageBack  = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "PAGEBACK");
	String  codCpi  = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "codCpi");
	String  prgDocumento = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "PRGDOCUMENTO");
	String  prgTemplateStampa  = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "PRGTEMPLATESTAMPA");
	String  dominioDati  = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "DOMINIO");
	String presaVisione = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "presaVisione");
	String caricatoSucc = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "caricatoSucc");
%>

<html>
<head>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<af:linkScript path="../../js/"/>
<script language="javascript">

	var pageRitorno = "<%=pageBack%>";
	var prgTemplateStampa = "<%=prgTemplateStampa%>";
	function tornaRicerca() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
// 		alert(pageRitorno);
// 		alert(prgTemplateStampa);
		//DettaglioDocumentoPadreStampParamPage
		document.formLista.PAGE.value = "GestAllegatiDocumentoPage";
		doFormSubmit(document.formLista);
	}

	//Funzione che viene eseguita ad ogni click delle checkbox relativa al documento da allegare
	function checkboxDocAllegatoClick(checkbox, PRGDOCUMENTOSEL) {
	}

	function confirmSelected() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
		
		//Controllo che sia stato selezionato almeno un movimento	
		var form = document.formLista;
	    var numDocumentiSel = 0;
	    var prgDocSelezionati = '';
	    for(var i = 0; i < form.elements.length ; i ++) {
	        if (form.elements[i].name == "CHECKBOXDOCALLEGATO" && form.elements[i].checked) {
	        	if (prgDocSelezionati == '') {
	        		prgDocSelezionati = prgDocSelezionati + form.elements[i].value;
	        	 }
	        	 else {
	        		 prgDocSelezionati = prgDocSelezionati + '#' + form.elements[i].value;
	        	 }
	        	numDocumentiSel++;
	        }
	    } 
	    if (numDocumentiSel ==0 ){ 
	    	alert ("Selezionare almeno un documento");
	    	return ;
	   	}

		if (confirm('Vuoi allegare i documenti selezionati?')) {
			document.formLista.LISTADOCALLEGATI.value = prgDocSelezionati;
			document.formLista.PAGE.value = pageRitorno;
			doFormSubmit(document.formLista);
		}
	}

	function dettaglioDocumento(PRGDOCUMENTO) {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
		var url = "AdapterHTTP?PAGE=DettaglioRicercaDocAllegatoStampParamPage&prgdocumento="+PRGDOCUMENTO+"&cdnLavoratore=<%= cdnLavoratore %>&prgAzienda=<%= prgAzienda %>&prgUnita=<%= prgUnita %>";
		var t = "_blank";
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=700,height=600,top=100,left=100"
       // alert(url);
        window.open(url, t, feat);
	}
	
</script>

</head>

<body class="gestione">


<%
// 	// TESTATA LAVORATORE
// 	if (StringUtils.isFilled(cdnLavoratore)) {
// 		InfCorrentiLav testata = new InfCorrentiLav(cdnLavoratore, user);
// 		testata.show(out);
// 	}



	// TESTATA LAVORATORE
	if (StringUtils.isFilled(cdnLavoratore)) {
		InfCorrentiLav testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
		testata.setSkipLista(true);
		testata.show(out);
	}
	
	// TESTATA AZIENDA
	if (StringUtils.isFilled(prgAzienda)) {
		InfCorrentiAzienda testata = new InfCorrentiAzienda(prgAzienda, prgUnita);
		testata.setSkipLista(true);
		testata.show(out);
	}

SourceBean docRow = (SourceBean) serviceResponse.getAttribute("M_GetListAllegatiStampParam.ROWS");
Vector vect = docRow.getAttributeAsVector("ROW");
int rows = vect.size();

%>
<af:error/>

<af:form name="formLista" action="AdapterHTTP" method="POST">



<table class="main" width="100%">
	<tr>
		<td>
			<af:list moduleName="M_GetListAllegatiStampParam" jsSelect="dettaglioDocumento"/>
			<%
			if(rows==0)
			{	
			%>
			<center>
	<p align="center">
<!--     <tr><td align="center"> -->
    <b><%=MessageBundle.getMessage(Integer.toString(MessageCodes.Consenso.MSG_STAMPA_PARAMETRICA_TEMPLATE_NODOC))%></b>
<!--     </td></tr> -->
    </p>
    </center>
	<br><br><br>
<%-- 			<p align="center" id="messaggioTemplate"><font color="green"><%=MessageBundle.getMessage(Integer.toString(MessageCodes.Consenso.MSG_STAMPA_PARAMETRICA_TEMPLATE_NODOC))%> --%>
<!-- 		</font></p> -->
<!-- 		<br /><br/> -->
			<%
			} 
			%>
			
		</td>
	</tr>
	<tr>
		<td>
		<%if(canSalva){ %>
			<span class="bottoni">
				<input type="button" class="pulsanti" value="Salva" onClick="confirmSelected();" />&nbsp;&nbsp;
			</span>
			<%} %>
			<span class="bottoni">
				<input type="button" class="pulsanti" value="Torna alla gestione allegati" onClick="tornaRicerca();" />
			</span>
		</td>
	</tr>
</table>

<input type="hidden" name="PAGE" value="" />
<input type="hidden" name="cdnFunzione" value="<%=cdnfunzione%>" />
<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>" />
<input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"/>
<input type="hidden" name="PRGUNITA" value="<%=prgUnita%>"/>
<input type="hidden" id="pageBack" name="PAGEBACK" value="<%=pageBack%>">
<input type="hidden" name="codCpi" value="<%= codCpi %>">
<input type="hidden" name="OPERATIONALLEGATI" value="">
<input type="hidden" name="PRGDOCUMENTO" value="<%=prgDocumento%>">
<input type="hidden" name="PRGTEMPLATESTAMPA" value="<%=prgTemplateStampa%>">
<input type="hidden" name="DOMINIO" value="<%=dominioDati%>">
<input type="hidden" name="LISTADOCALLEGATI" value="">
<input type="hidden" name="presaVisione" value="<%=presaVisione%>">
<input type="hidden" name="caricatoSucc" value="<%=caricatoSucc%>">

</af:form>

</body>
</html>
