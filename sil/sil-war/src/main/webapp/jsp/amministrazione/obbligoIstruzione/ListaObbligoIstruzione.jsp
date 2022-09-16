<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../../global/noCaching.inc"%>
<%@ include file="../../global/getCommonObjects.inc"%>

<%--
--%>
<%@ page
	import="com.engiweb.framework.base.*,com.engiweb.framework.configuration.ConfigSingleton,it.eng.sil.util.*,it.eng.afExt.utils.StringUtils,it.eng.sil.security.ProfileDataFilter,java.text.*,java.util.*,java.math.*,it.eng.sil.security.*"%>
<%
	String cdnLavoratore = (String) serviceRequest.getAttribute("CDNLAVORATORE");
	String _current_page = (String) serviceRequest.getAttribute("PAGE");
	int _funzione = 0;
	try {
		_funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
	} catch (Exception ex) {
		ex.printStackTrace();
	}
	_current_page = "CurrStudiMainPage";
	ProfileDataFilter filter = new ProfileDataFilter(user, _current_page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	String _page = (String) serviceRequest.getAttribute("PAGE");
	PageAttribs attributi = new PageAttribs(user, _page);

	boolean canModify = attributi.containsButton("AGGIORNA");
	boolean canDelete = attributi.containsButton("CANCELLA");
	boolean canInsert = attributi.containsButton("INSERISCI");

	boolean canView = filter.canViewLavoratore();
	if (!canView) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	} else {
		canModify = attributi.containsButton("inserisci");
		canDelete = attributi.containsButton("rimuovi");

		if (!canModify) {
			canModify = false;
		} else {
			canModify = filter.canEditLavoratore();
		}

		if (!canDelete) {
			canDelete = false;
		} else {
			canDelete = filter.canEditLavoratore();
		}

	}
%>
<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>
<%
	//int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
	InfCorrentiLav infCorrentiLav = new InfCorrentiLav(RequestContainer.getRequestContainer()
			.getSessionContainer(), cdnLavoratore, user);
	String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
	///////////////////////////
	boolean nuovo = true;
	String apriDiv = "none";
%>
<html>
<head>
<title>Obbligo di Istruzione</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<%@ include file="../../global/checkFormatData.inc"%>
<af:linkScript path="../../js/" />
<script language="JavaScript" src="../../js/layers.js"></script>
<script language="JavaScript" type="text/javascript"
	src="../../js/script_comuni.js"></script>

<script language="Javascript" type="text/javascript">
  window.top.menu.caricaMenuLav(<%=cdnFunzione%>,<%=cdnLavoratore%>);
  var flagChanged = false;
function apriMascheraInserimento()
{
	/*
  var collDiv = document.getElementsByName('divLayerDett');
  var objDiv = collDiv.item(0);
  objDiv.style.display = "";
  document.location='#aLayerIns';
  */
  insertForm=document.forms["apriDiv4Insert"];
  insertForm.submit();
}

function ManageRecObbligoIstruzione(prgobbligoistruzione,bottonePremuto){
  //var collDiv = document.getElementsByName('divLayerDett');
  manageForm=document.forms["manageCondizione"];
  manageForm.elements["prgobbligoistruzione"].value=prgobbligoistruzione;
  manageForm.elements["bottonePremuto"].value=bottonePremuto;
  return manageForm;
  
}
function DeleteRecObbligoIstruzione(prgobbligoistruzione)
{
	manageForm=ManageRecObbligoIstruzione(prgobbligoistruzione,"cancella");
	manageForm.submit();
}
function DettaglioRecObbligoIstruzione(prgobbligoistruzione)
{
	manageForm=ManageRecObbligoIstruzione(prgobbligoistruzione,"dettaglio");
  	manageForm.elements["APRIDIV"].value='true';
	manageForm.submit();
}
  </script>
</head>
<body class="gestione">

<%
	Linguette l = new Linguette(user, _funzione, "ObbligoIstruzionePage", new BigDecimal(cdnLavoratore));
	infCorrentiLav.show(out);
	l.show(out);
%>
<%--
<af:showMessages prefix="ListaObbligoIstruzione" />
<af:showMessages prefix="M_COMBO_TIPO_CONDIZIONE" />
<af:showMessages prefix="M_GET_DETTAGLIO_CONDIZIONE" />
<af:showMessages prefix="M_GET_ANLAVSTORIAINF_DA_CDNLAV" />
--%>
<af:showMessages prefix="M_Inserisci_Condizione" />
<af:showMessages prefix="M_AGGIORNA_CONDIZIONE" />
<af:showMessages prefix="M_DELETE_AM_OBBLIGO_ISTRUZIONE" />
<af:showErrors />

<p align="center"><af:list moduleName="ListaObbligoIstruzione"
	skipNavigationButton="1" jsSelect="DettaglioRecObbligoIstruzione"
	jsDelete="DeleteRecObbligoIstruzione" /></p>
<%--
--%>
<%
	if (canInsert) {
%>
<p align="center"><input type="button" class="pulsanti"
	onClick="apriMascheraInserimento()" value="Nuovo Tipo Condizione" /></p>
<%
	}
%>
<!-- LAYER - Start -->
<jsp:include page="DettaglioTipoCondizioneLayer.jsp">
	<jsp:param name="dummy" value="dummy" />
</jsp:include>
<!-- LAYER - END -->
<div style="display: none;">
<af:form name="manageCondizione" method="POST" action="AdapterHTTP">
	<input type="hidden" name="PAGE" value="<%=_page%>">
	<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>">
	<input type="hidden" name="cdnFunzione" value="<%=_funzione%>">
	<input type="hidden" name="bottonePremuto" value="">
	<input type="hidden" name="APRIDIV" value="">
	<input type="hidden" name="prgobbligoistruzione" id="prgobbligoistruzione" value="">
</af:form> 
<af:form name="apriDiv4Insert" method="POST" action="AdapterHTTP">
	<input type="hidden" name="PAGE" value="<%=_page%>">
	<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>">
	<input type="hidden" name="cdnFunzione" value="<%=_funzione%>">
	<input type="hidden" name="APRIDIV" value="true">
</af:form>
</div>


</body>
</html>