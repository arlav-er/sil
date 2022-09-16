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
	String  titolo = "Informazioni da presa d'atto";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	// Lettura parametri dalla REQUEST
	int     cdnfunzione      = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	String  cdnfunzioneStr   = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");
	String  cdnLavoratore    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
	String  cdnLavoratoreRicerca    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratoreRicerca");



	// CONTROLLO ACCESSO ALLA PAGINA
	ProfileDataFilter filter = new ProfileDataFilter(user, "RicercaInfoDaPresaAttoPage");
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratoreRicerca));
	boolean canView = filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}

	// CONTROLLO PERMESSI SULLA PAGINA
	PageAttribs attributi = new PageAttribs(user, _page);

	//boolean canModify = attributi.containsButton("aggiorna");
	//boolean canDelete = attributi.containsButton("rimuovi");
	
	Object cdnUtCorrente    = sessionContainer.getAttribute("_CDUT_");
	String cdnUtCorrenteStr = StringUtils.getStringValueNotNull(cdnUtCorrente);



	// Recupero la ROW contenuta nella RISPOSTA DI UN MODULO
	SourceBean row = (SourceBean) serviceResponse.getAttribute("M_INFO_DA_PRESA_ATTO.ROWS.ROW");
	// Recupero tutta la RISPOSTA DI UN MODULO
	//SourceBean dettModule = (SourceBean) serviceResponse.getAttribute("DettXxxModule");


	// Leggo i singoli dati della risposta (dalla ROW o da altro SourceBean)
	BigDecimal prgInfo = SourceBeanUtils.getAttrBigDecimal(row, "prgInfoTrasferimento");
	String datTrasferimento = SourceBeanUtils.getAttrStrNotNull(row, "dattrasferimento");
	String cognome = SourceBeanUtils.getAttrStr(row, "strCognome");
	String nome = SourceBeanUtils.getAttrStr(row, "strNome");
	String codiceFiscale = SourceBeanUtils.getAttrStr(row, "strcodicefiscale");
	String statoOccupazionale = SourceBeanUtils.getAttrStrNotNull(row, "statoOccupazionale");
	String dataAnzianita = SourceBeanUtils.getAttrStrNotNull(row, "datAnzianitaDisoc");
	String mesiAnzianita = SourceBeanUtils.getAttrStrNotNull(row, "numMesiAnzianita");
	String mesiSospensione = SourceBeanUtils.getAttrStrNotNull(row, "numMesiSosp");
	
	String cmTipoIscr_1 = SourceBeanUtils.getAttrStrNotNull(row, "cmTipoIscr_1");
	String cmTipoIscr_2 = SourceBeanUtils.getAttrStrNotNull(row, "cmTipoIscr_2");
	String datDataInizio68_1 = SourceBeanUtils.getAttrStrNotNull(row, "datDataInizio68_1");
	String datDataInizio68_2 = SourceBeanUtils.getAttrStrNotNull(row, "datDataInizio68_2");
	String datAnzianita68_1  = SourceBeanUtils.getAttrStrNotNull(row, "datAnzianita68_1");
	String datAnzianita68_2  = SourceBeanUtils.getAttrStrNotNull(row, "datAnzianita68_2");
	
	String myJsS = "AdapterHTTP?ACTION_NAME=DOWNLOAD_GENERICA&PRGINFO=" + prgInfo + "&asAttachment=true";

	// Recupero (da ROW) il numKlo (per controllo concorrenza)
	//String numKlo = SourceBeanUtils.getAttrStrNotNull(row, "numKlo");
	
	// Recupero (da ROW) l'operatore di creazione e ultima modifica
	String cdnUtins = SourceBeanUtils.getAttrStrNotNull(row, "cdnUtins");
	String dtmins   = SourceBeanUtils.getAttrStrNotNull(row, "dtmins");
	String cdnUtmod = SourceBeanUtils.getAttrStrNotNull(row, "cdnUtmod");
	String dtmmod   = SourceBeanUtils.getAttrStrNotNull(row, "dtmmod");
	
	Testata operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
	
	// Recupero URL della LISTA da cui sono venuto al dettaglio (se esiste)
	String token = "_TOKEN_" + "ListaInfoDaPresaAttoPage";
	String goBackInf = "Torna alla lista";
	String goBackUrl = (String) sessionContainer.getAttribute(token.toUpperCase());
	// Sola lettura: viene usato per tutti i campi di input
	String readonly = "true"; // String.valueOf( ! canModify );
	boolean  canModify = false;
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
	if (StringUtils.isFilled(cdnLavoratore)) {
	// Genera il Javascript che si occuperà di inserire i links nel footer
		attributi.showHyperLinks(out, requestContainer, responseContainer, "cdnLavoratore="+cdnLavoratore);
	}
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
%>


<p class="titolo"><%= titolo %></p>

<af:showErrors />

<af:form name="form" action="AdapterHTTP" method="POST">

<af:textBox type="hidden" name="cdnfunzione" value="<%= cdnfunzioneStr %>" />


<%= htmlStreamTop %>
<table class="main">
<% if (!StringUtils.isFilled(cdnLavoratore)) { %>
	<tr>
        <td class="etichetta">
       		Codice fiscale
        </td>
		<td class="campo">
		
			<%-- Input di un campo di testo --%>
			<af:textBox name="codiceFiscale" type="text"
						title="Codice fiscale"
						value="<%= codiceFiscale %>"
						size="40" maxlength="101"
						required="false"
						classNameBase="input"
						readonly="<%= readonly %>"
						onKeyUp="fieldChanged()"
						/>
	<tr>
        <td class="etichetta">
       		Cognome
        </td>
		<td class="campo">
		
			<%-- Input di un campo di testo --%>
			<af:textBox name="strcognome" type="text"
						title="Cognome"
						value="<%= cognome %>"
						size="40" maxlength="101"
						required="false"
						classNameBase="input"
						readonly="<%= readonly %>"
						onKeyUp="fieldChanged()"
						/>
	<tr>
        <td class="etichetta">
       		Nome
        </td>
		<td class="campo">
		
			<%-- Input di un campo di testo --%>
			<af:textBox name="strnome" type="text"
						title="Nome"
						value="<%= nome %>"
						size="40" maxlength="101"
						required="false"
						classNameBase="input"
						readonly="<%= readonly %>"
						onKeyUp="fieldChanged()"
						/>												
<% } %>
	<tr>
        <td class="etichetta">
       		Stato occupazionale
        </td>
		<td class="campo">
		
			<%-- Input di un campo di testo --%>
			<af:textBox name="statoOccupazionale" type="text"
						title="Stato occupazionale"
						value="<%= statoOccupazionale %>"
						size="40" maxlength="101"
						required="false"
						classNameBase="input"
						readonly="<%= readonly %>"
						onKeyUp="fieldChanged()"
						/>
	<tr>
		<td class="etichetta">
       		Data anzianità disoc.
        </td>
		<td class="campo">

			<%-- Input di una data --%>
			<af:textBox name="datAnzianitaDisoc" type="date"
						title="Data anzianita' disoccupazione"
						value="<%= dataAnzianita %>"
						validateOnPost="true"
						size="11" maxlength="10"
						required="false"
						classNameBase="input"
						readonly="<%= readonly %>"
						onKeyUp="fieldChanged()" />
	<tr>
		<td class="etichetta">
       		Mesi anzianità disoc.
        </td>
		<td class="campo">
			<af:textBox name="mesiAnzianita" type="text"
						title="Mesi anzianita' disoccupazione"
						value="<%= mesiAnzianita %>"
						validateOnPost="true"
						size="11" maxlength="10"
						required="false"
						classNameBase="input"
						readonly="<%= readonly %>"
						onKeyUp="fieldChanged()" />
	<tr>
		<td class="etichetta">
       		Mesi sospensione
        </td>
		<td class="campo">
			<af:textBox name="mesiSospensione" type="text"
						title="Mesi sospensione"
						value="<%= mesiSospensione %>"
						validateOnPost="true"
						size="11" maxlength="10"
						required="false"
						classNameBase="input"
						readonly="<%= readonly %>"
						onKeyUp="fieldChanged()" />

		</td>
	<tr>
		<td class="etichetta">
       		Tipo di iscrizione (D)
        </td>
		<td class="campo">
			<af:textBox name="cmTipoIscr_1" type="text"
						title="Tipo di iscrizione"
						value="<%= cmTipoIscr_1 %>"
						validateOnPost="false"
						size="30" maxlength="50"
						required="false"
						classNameBase="input"
						readonly="<%= readonly %>"
					/>
		</td>
	<tr>
		<td class="etichetta">
       		Data Iscrizione C.M.
        </td>
		<td class="campo">
			<af:textBox name="datDataInizio68_1" type="date"
						title="Data Iscrizione"
						value="<%= datDataInizio68_1 %>"
						validateOnPost="false"
						size="11" maxlength="10"
						required="false"
						classNameBase="input"
						readonly="<%= readonly %>"
						onKeyUp="" />
	<tr>
		<td class="etichetta">
       		Data Anzianita' L.68
        </td>
		<td class="campo">
			<af:textBox name="datAnzianita68_1" type="date"
						title="Data Anzianita'"
						value="<%= datAnzianita68_1 %>"
						validateOnPost="false"
						size="11" maxlength="10"
						required="false"
						classNameBase="input"
						readonly="<%= readonly %>"
						onKeyUp="" />
						
	<tr>
		<td class="etichetta">
       		Tipo di iscrizione (A)
        </td>
		<td class="campo">
			<af:textBox name="cmTipoIscr_2" type="text"
						title="Tipo di iscrizione"
						value="<%= cmTipoIscr_2 %>"
						validateOnPost="false"
						size="30" maxlength="50"
						required="false"
						classNameBase="input"
						readonly="<%= readonly %>"
					/>
		</td>
	<tr>
		<td class="etichetta">
       		Data Iscrizione C.M.
        </td>
		<td class="campo">
			<af:textBox name="datDataInizio68_2" type="date"
						title="Data Iscrizione"
						value="<%= datDataInizio68_2 %>"
						validateOnPost="false"
						size="11" maxlength="10"
						required="false"
						classNameBase="input"
						readonly="<%= readonly %>"
						onKeyUp="" />
	<tr>
		<td class="etichetta">
       		Data Anzianita' L.68
        </td>
		<td class="campo">
			<af:textBox name="datAnzianita68_2" type="date"
						title="Data Anzianita'"
						value="<%= datAnzianita68_2 %>"
						validateOnPost="false"
						size="11" maxlength="10"
						required="false"
						classNameBase="input"
						readonly="<%= readonly %>"
						onKeyUp="" />
												
	<tr>
		<td class="etichetta">
       		PercorsoLavoratore.pdf
        </td>
		<td class="campo"><a href="<%= myJsS %>"><img src="../../img/download.gif" border="0" alt="Salva il documento" /></a></td>
	</tr>

	<%-- Linea di separazione dei dati --%>
	<tr>
		<td colspan="4">
			<div class="sezione2"></div>
		</td>
	</tr>

	<%-- ***************************************************************************** --%>


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
<center>
<% operatoreInfo.showHTML(out); %>
</center>

</af:form>

</body>
</html>
