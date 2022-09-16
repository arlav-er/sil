<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
			it.eng.sil.security.*,
			it.eng.afExt.utils.*,
			it.eng.sil.util.*,
			it.eng.sil.module.trento.*,
			java.math.*,
			java.util.*"
	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%
String pageToProfile = "ListaStampeParLavPage";

ProfileDataFilter filter = new ProfileDataFilter(user, pageToProfile);
if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
}

//Gestione Attributi
PageAttribs attributi = new PageAttribs(user, pageToProfile);	

	String  titolo = "Lista stampe parametriche";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	// Lettura parametri dalla REQUEST
	int     cdnfunzione      = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	String  cdnfunzioneStr   = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");
	String  cdnLavoratore    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
	String  prgAzienda       = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgAzienda");
	String  prgUnita         = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgUnita");
	/*
	Commento le parti riguardanti i controlli sulla presenza del consenso.
	Tali controlli andranno ripresi e rivisti quando si lavorerà sull'integrazione
	*/
	
// 	Consenso consensoLav = (Consenso)sessionContainer.getAttribute("CONSENSO_" + cdnLavoratore);
// 	String msgConsenso = "";
// 	String codStatoConsenso = null;
	
// 	if (consensoLav != null) {
// 		codStatoConsenso = consensoLav.getCodice();
// 		if (codStatoConsenso != null) {
// 			if (codStatoConsenso.equalsIgnoreCase(Consenso.ATTIVO)) {
// 				msgConsenso = "Consenso Attivo rilasciato in data " + consensoLav.getDataRegistrazione();
// 			}
// 			else {
// 				if (codStatoConsenso.equalsIgnoreCase(Consenso.ASSENTE)) {
// 					msgConsenso = "Consenso non presente. Si rammenta che per alcuni tipi di documento che richiedono l'uso della firma grafometrica, " +
// 							"&egrave; obbligatorio acquisire preliminarmente il consenso";
// 				}
// 				else {
// 					if (codStatoConsenso.equalsIgnoreCase(Consenso.REVOCATO)) {
// 						msgConsenso = "Consenso revocato. &Egrave; necessario, dove previsto, procedere con la firma autografa dei documenti";
// 					}
// 					else {
// 						if (codStatoConsenso.equalsIgnoreCase(Consenso.NON_DISPONIBILE)) {
// 							msgConsenso = "Il consenso non &egrave; disponibile. Se si desidera continuare, per i documenti che richiedono l'uso della " +
// 								"firma grafometrica, sarà necessario procedere con la firma autografa";
// 						}
// 					}
// 				}
// 			}
// 		}
// 	}
// 	else {
// 		msgConsenso = "Il consenso non &egrave; disponibile. Se si desidera continuare, per i documenti che richiedono l'uso della " +
// 				"firma grafometrica, sarà necessario procedere con la firma autografa";	
// 	}

	//

	
//     InfCorrentiLav testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
    //testata.setSkipLista(true);
	boolean canInsert = attributi.containsButton("NUOVA_STAMPA");
	//boolean canInsert = true;
	
// 	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
// 	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
// 	boolean canView=filter.canViewLavoratore();
// 	if (!canView) {
// 		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
// 	}
%>

<html>
<head>
<title><%= titolo %></title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<af:linkScript path="../../js/"/>
<script language="javascript">

	function nuovo() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
		var url = "";
		<%
		if (StringUtils.isFilled(prgAzienda)) {
		%>

		url = "AdapterHTTP?PAGE=InsStampaParamPage" +		
		          		"&prgAzienda=<%=prgAzienda%>&prgUnita=<%=prgUnita%>&dominio=DA" + 
						"&cdnfunzione=<%=cdnfunzione%>";
		<%
		}else if(StringUtils.isFilled(cdnLavoratore)) {
		%>		
		url = "AdapterHTTP?PAGE=InsStampaParamPage" +		
  		"&cdnLavoratore=<%=cdnLavoratore%>&dominio=DL" + 
		"&cdnfunzione=<%=cdnfunzione%>&_ENCRYPTER_KEY_=<%=sessionContainer.getAttribute("_ENCRYPTER_KEY_")%>";

		<%
		}
		%>
						
		setWindowLocation(url);
	}

	

</script>
<script language="Javascript">
<!--Contiene il javascript che si occupa di aggiornare i link del footer-->
  <% 
       //Genera il Javascript che si occuperà di inserire i links nel footer
       attributi.showHyperLinks(out, requestContainer,responseContainer,"");
  %>
</script>
</head>

<body class="gestione" onload="rinfresca()">



<%-- <%testata.show(out);%> --%>


<%
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
	

%>

<af:showErrors />



<af:form dontValidate="true" action="AdapterHTTP">

<p class="titolo"><%= titolo %></p>


<table class="main">
    
    <%-- STAMPA LA LISTA --%>
	<af:list moduleName="MListaStampeParLav" getBack="true" />

</table>


<%-- BOTTONI --%>
<table class="main">

  <% if (canInsert) { %>
	<tr>
		<td>
			<input type="button" class="pulsante" value="Nuova Stampa Parametrica"
					onClick="nuovo()" />
		</td>
	</tr>
	<tr><td>&nbsp;</td></tr>
  <% } %>
	
</table>

</af:form>
<%--

Commento le parti riguardanti i controlli sulla presenza del consenso.
Tali controlli andranno ripresi e rivisti quando si lavorerà sull'integrazione

<%if (!msgConsenso.equals("")) { %>
<table width="100%">
    <tr><td style="campo2"><%=msgConsenso%></td></tr>
</table>
<% } %>
 --%>
</body>
</html>
