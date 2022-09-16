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
<%@ include file="../global/getCommonObjects.inc"%>

<%
	boolean isNuovo = true;
	String titolo = "Inserisci Modalità Modello TDA";
	String _page = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE");
	String prgModelloVoucher = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGMODVOUCHER");
	 
	// Lettura parametri dalla REQUEST
	int cdnfunzione = SourceBeanUtils.getAttrInt(serviceRequest,"cdnfunzione");
	String _funzione = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");

	// CONTROLLO ACCESSO ALLA PAGINA
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	// CONTROLLO PERMESSI SULLA PAGINA
	PageAttribs attributi = new PageAttribs(user, _page);
	
	String readonly = "";
	boolean canView = filter.canView();
	boolean canModify = false;
	if (!canView) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	} else {
		canModify = attributi.containsButton("SalvaModModalita");
		readonly = String.valueOf(!canModify);
 	}

	Object cdnUtCorrente = sessionContainer.getAttribute("_CDUT_");
	String cdnUtCorrenteStr = StringUtils.getStringValueNotNull(cdnUtCorrente);

	// Modulo dettaglio: non c'è
	SourceBean dettModule = null; // usato nel file INC dei campi di input

	// Creo i singoli dati riempiti di vuoto (vengono usati nel file INC dei dettagli).
	String strPrgModalita = "";
	String codModalita = "";
	String strTipoDurata = "";
	String strDurataMin = "";
	String strDurataMax = "";
	String strRimborso = "";
	String strValUnit = "";
	String strValTot = "";
	String strPercentuale ="";
	// Stringhe con HTML per layout tabelle
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>

<html>
<head>
<title><%= titolo %></title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />

  <link rel="stylesheet" type="text/css" href="../../css/jqueryui/jquery.selectBoxIt.css">
  <link rel="stylesheet" type="text/css" href="../../css/jqueryui/jqueryui-sil.css">

<af:linkScript path="../../js/"/>

  <script src="../../js/jqueryui/jquery-1.12.4.min.js"></script>
  <script src="../../js/jqueryui/jquery-ui.min.js"></script>
  <script src="../../js/jqueryui/jquery.selectBoxIt.min.js"></script>

<%@ include file="../global/fieldChanged.inc" %>

<%-- INCLUDERE QUI ALTRI JAVASRIPT CON CLAUSOLE DEL TIPO:
<script language="Javascript" src="../../js/xxx.js"></script>
--%>

 <script type="text/javascript">
    $(function() {
    	$("[name='codModalita']").selectBoxIt({
            theme: "default",
            defaultText: "Seleziona una modalità...",
            autoWidth: false
        });
       
    });
    $(function() {
    	$(":reset").click(function() {
    		$("select").data("selectBox-selectBoxIt").selectOption("");
	    });
    });
    </script>
 
<script language="Javascript">
 
function reloadModalita() {	
	 
	
	if(controllaModalitaObb() && checkTipoDurata() && checkDurataMin() && checkDurataMax() && checkPercentuale() && controlliValoriEuro()){
		var url = "AdapterHTTP?PAGE=DettaglioModalitaTdaPage";
		url += "&CDNFUNZIONE=<%=_funzione%>";
		url += "&PRGMODVOUCHER=<%=prgModelloVoucher%>";
	 	url += "&OPERAZIONEMODALITA=INSERISCI";
	 	url += "&codModalita="+ document.frm1.codModalita.value;
		url += "&strTipoDurata="+ document.frm1.strTipoDurata.value;
		url += "&strDurataMin="+ document.frm1.strDurataMin.value;
		url += "&strDurataMax="+ document.frm1.strDurataMax.value;
		url += "&strPercentuale="+ document.frm1.strPercentuale.value;
		url += "&strRimborso="+ document.frm1.strRimborso.value;
		url += "&strValUnit="+ document.frm1.strValUnit.value;
		url += "&strValTot="+ document.frm1.strValTot.value;
		url += "&confermaModificheModello=confermaModificheModello";
		window.location.replace(url);
		//window.close();		
	}
}
var prevValue = "<%=strRimborso%>";
</script>
<script language="Javascript" src="../../js/modellitda/modalita.js"></script>

</head>

<body class="gestione" onload="rinfresca();">
 
<p class="titolo"><%= titolo %></p>

<af:showErrors />
<af:showMessages prefix="M_InsertUpdateModalitaTda"/>

<af:form name="frm1" action="AdapterHTTP" method="POST">

<af:textBox type="hidden" name="PAGE" value="LinguettaModalitaTdaPage" />
<af:textBox type="hidden" name="cdnfunzione" value="<%= _funzione %>" />
<af:textBox type="hidden" name="PRGMODVOUCHER" value="<%= prgModelloVoucher %>" />
<af:textBox type="hidden" name="CDNUTINS" value="<%= cdnUtCorrenteStr %>" />
<af:textBox type="hidden" name="OPERAZIONEMODALITA" value="INSERISCI"/>

 
<%= htmlStreamTop %>
<table class="main">

	<%--
	Tutti i campi di input sono nel file INC poiché sono
	condivisi dalle pagine di dettaglio e di nuovo.
	--%>
	<%-- ***************************************************************************** --%>
	<%@ include file="modalita.inc" %>
	<%-- ***************************************************************************** --%>

</table>
	
	<br>
    <table>
    <%if(canModify){%>
	<tr>
		<td>
			<input type="button" onclick="reloadModalita();" class="pulsanti" name="confermaModificheModello" value="Salva" />
		</td>
		<td>
			<input type="reset" class="pulsanti" value="Annulla" />
		</td>
	</tr>
	<%}%>
	<tr>
			<td colspan="2" align="center">
				<input class="pulsante" type = "button" name="torna" value="Chiudi" onClick="window.close();"/>
			</td>
		</tr>
	<tr>
		<td  colspan="2">&nbsp;</td>
	</tr>

</table>
<%= htmlStreamBottom %>

</af:form>

</body>
</html>