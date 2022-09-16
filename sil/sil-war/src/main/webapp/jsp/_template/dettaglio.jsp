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
	String  titolo = "xxx";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	// Lettura parametri dalla REQUEST
	int     cdnfunzione      = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	String  cdnfunzioneStr   = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");
	String  cdnLavoratore    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
	String  prgAzienda       = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgAzienda");
	String  prgUnita         = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgUnita");


	// CONTROLLO ACCESSO ALLA PAGINA
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	
	boolean canView = filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}

	// CONTROLLO PERMESSI SULLA PAGINA
	PageAttribs attributi = new PageAttribs(user, _page);

	boolean canModify = attributi.containsButton("aggiorna");
	boolean canDelete = attributi.containsButton("rimuovi");
	
	Object cdnUtCorrente    = sessionContainer.getAttribute("_CDUT_");
	String cdnUtCorrenteStr = StringUtils.getStringValueNotNull(cdnUtCorrente);



	// Recupero la ROW contenuta nella RISPOSTA DI UN MODULO
	SourceBean row = (SourceBean) serviceResponse.getAttribute("DettXxxModule.ROWS.ROW");
	// Recupero tutta la RISPOSTA DI UN MODULO
	SourceBean dettModule = (SourceBean) serviceResponse.getAttribute("DettXxxModule");


	// Leggo i singoli dati della risposta (dalla ROW o da altro SourceBean)
	BigDecimal prgXxx = SourceBeanUtils.getAttrBigDecimal(row, "prgXxx", null);
	String  strXxx = SourceBeanUtils.getAttrStrNotNull(row, "strXxx");
	String  strYyy = SourceBeanUtils.getAttrStr(row, "strXxx", "default");
	int     cdnXxx = SourceBeanUtils.getAttrInt(row, "cdnXxx", -1);
	boolean flgXxx = SourceBeanUtils.getAttrBoolean(row, "flgXxx", false);

	// Recupero (da ROW) il numKlo (per controllo concorrenza)
	String numKlo = SourceBeanUtils.getAttrStrNotNull(row, "numKlo");
	
	// Recupero (da ROW) l'operatore di creazione e ultima modifica
	String cdnUtins = SourceBeanUtils.getAttrStrNotNull(row, "cdnUtins");
	String dtmins   = SourceBeanUtils.getAttrStrNotNull(row, "dtmins");
	String cdnUtmod = SourceBeanUtils.getAttrStrNotNull(row, "cdnUtmod");
	String dtmmod   = SourceBeanUtils.getAttrStrNotNull(row, "dtmmod");
	Testata operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
	
	// Recupero URL della LISTA da cui sono venuto al dettaglio (se esiste)
	String token = "_TOKEN_" + "ListaXxxPage";
	String goBackInf = "Torna alla lista";
	String goBackUrl = (String) sessionContainer.getAttribute(token.toUpperCase());
	if (StringUtils.isEmpty(goBackUrl)) {
		goBackInf = "Torna alla ricerca";
		goBackUrl = "PAGE=RicercaXxxPage&cdnfunzione=" + cdnfunzioneStr;
	}

	// Sola lettura: viene usato per tutti i campi di input
	String readonly = String.valueOf( ! canModify );
	
	// Stringhe con HTML per layout tabelle
	String htmlStreamTop    = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>

<html>
<head>
<title><%= titolo %></title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />

<af:linkScript path="../../js/"/>
<%@ include file="../global/fieldChanged.inc" %>

<%-- INCLUDERE QUI ALTRI JAVASRIPT CON CLAUSOLE DEL TIPO:
<script language="Javascript" src="../../js/xxx.js"></script>
--%>

<script language="Javascript">

	/* Funzione per tornare alla pagina precedente */
	function goBack() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
		
		goTo("<%= goBackUrl %>");
	}
	
	
	/* Funzione chiamata al caricamento della pagina */
	function onLoad() {
		rinfresca();
		// altri funzioni da richiamare sulla onLoad...
	}

<%
	// Genera il Javascript che si occuperà di inserire i links nel footer
	attributi.showHyperLinks(out, requestContainer, responseContainer, "cdnLavoratore="+cdnLavoratore);
%>

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
	
	// LINGUETTE LAVORATORE
	if (StringUtils.isFilled(cdnLavoratore)) {
		Linguette linguette = new Linguette(user, cdnfunzione, _page, new BigDecimal(cdnLavoratore));
		linguette.show(out);
	}
%>


<p class="titolo"><%= titolo %></p>

<af:showErrors />
<af:showMessages prefix="SalvaXxxModule"/>
<af:showMessages prefix="DettXxxModule"/>

<af:form name="form" action="AdapterHTTP" method="POST">

<af:textBox type="hidden" name="PAGE" value="SalvaXxxPage" />
<af:textBox type="hidden" name="cdnfunzione" value="<%= cdnfunzioneStr %>" />
<af:textBox type="hidden" name="numKlo" value="<%= numKlo %>" />
<af:textBox type="hidden" name="CDNUTMOD" value="<%= cdnUtCorrenteStr %>" />


<%= htmlStreamTop %>
<table class="main">

	<%--
	Tutti i campi di input sono nel file INC poiché sono
	condivisi dalle pagine di dettaglio e di nuovo.
	--%>
	<%-- ***************************************************************************** --%>
	<%@ include file="dettaglioCampi.inc" %>
	<%-- ***************************************************************************** --%>


	<% if (canModify) { %>
		<tr>
			<td colspan="2">
				<input type="submit" class="pulsanti" value="Aggiorna" />
	
				<input type="reset" class="pulsanti" value="Annulla" />
			</td>
		</tr>
		<tr>
			<td  colspan="2">&nbsp;</td>
		</tr>
	<% } %>

	<% if (StringUtils.isFilled(goBackUrl)) { %>
		<tr>
			<td colspan="2">
				<input type="button" class="pulsante" name="back" value="<%= goBackInf %>"
						onclick="goBack()" />
			</td>
		</tr>
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
	<% } %>

</table>
<%= htmlStreamBottom %>

<% operatoreInfo.showHTML(out); %>

</af:form>

</body>
</html>
