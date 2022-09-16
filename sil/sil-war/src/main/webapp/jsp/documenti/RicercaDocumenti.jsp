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
	// NOTE: Attributi della pagina (pulsanti e link) 
	String _page = "DocumentiRicercaPage";
	PageAttribs attributi = new PageAttribs(user, _page);

	int cdnfunzione = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	String  cdnLavoratore  = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnLavoratore");
	String  prgAzienda     = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "prgAzienda");
	String  prgUnita       = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "prgUnita");
	boolean lookLavoratore = SourceBeanUtils.getAttrBoolean(serviceRequest, "lookLavoratore", false);
	boolean lookAzienda    = SourceBeanUtils.getAttrBoolean(serviceRequest, "lookAzienda", false);
	String  contesto       = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "contesto");
	/* NOTA SUI PARAMETRI:
	 * cdnLavoratore o prgAzienda+prgUnita: se fissata, imposta il filtro su quell'entità
	 * lookLavoratore o lookAzienda: se TRUE, abilita la lookup su quell'entità.
	 * contesto: può essere vuoto, "L" o "A" e indica il contesto a cui si fa riferimento (Lav/Azi),
	 *           quello per cui aggiornare l'eventuale menu' di sinistra.
	 * NOTA:
	 * Il parametro di "look" è più prioritario del codice fisso. Per esempio:
	 *    cndLav lookLav
	 * 1.  ""     false --> nè filtrerà sul lavoratore nè mostra il pulsante di lookup per esso
	 * 2.  ""     true  --> mostra il pulsante di lookup lavoratore (per impostare il cndLav)
	 * 3.  "xx"   false --> si filtrerà sul lavoratore con codice COD ma non viene mostrato il
	 *                      pulsante di lookup (non si può cambiare il lavoratore in uso)
	 * 4.  "xx"   true  --> stesso comportamento del caso 2: mostra lookup ma considera il
	 *                      lavoratore come non impostato (come se non fosse stato dato il codice)
	 */

	%>
	<%@ include file="ricercaAutoInitLook.inc" %>
	<%
	
	Vector tipiDoc = serviceResponse.getAttributeAsVector("COMBOTIPODOCUMENTO.ROWS.ROW");
	
	String htmlStreamTop    = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<title>Ricerca Documenti</title>

<af:linkScript path="../../js/" />
<script language="Javascript" src="../../js/documenti/lookEntityDoc.js"></script>
<script language="Javascript" src="../../js/documenti/protocolloDoc.js"></script>
<script language="Javascript" src="../../js/documenti/ricercaDoc.js"></script>

<script language="Javascript">

	// Qui la "flagChanged" serve perché la usano i moduli di "lookup"
	// lavoratore/azienda (che sono presi dalla dettaglioDocumento).
	var flagChanged = false;

	<%
	//Genera il Javascript che si occuperà di inserire i links nel footer
	attributi.showHyperLinks(out, requestContainer, responseContainer, "");
	%>

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

	function dettaglioDocumenti() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
  
		var url = "AdapterHTTP?PAGE=DocumentiAssociatiPage" +
					"&cdnLavoratore=<%=cdnLavoratore%>" +
					"&prgAzienda=<%=prgAzienda%>&prgUnita=<%=prgUnita%>" +
					"&lookLavoratore=<%=lookLavoratore%>" +
					"&lookAzienda=<%=lookAzienda%>" +
					"&contesto=<%=contesto%>" +
					"&cdnfunzione=<%=cdnfunzione%>";
	    setWindowLocation(url);
	}

	function nuovoBot() {
	    // Se la pagina è già in submit, ignoro questo nuovo invio!
	    if (isInSubmit()) return;

		var url = "AdapterHTTP?PAGE=DettagliDocumentoPage&" +
							"NUOVO=true" +
							"&cdnLavoratore=<%=cdnLavoratore%>" +
							"&prgAzienda=<%=prgAzienda%>&prgUnita=<%=prgUnita%>" +
							"&lookLavoratore=<%=lookLavoratore%>" +
							"&lookAzienda=<%=lookAzienda%>" +
							"&contesto=<%=contesto%>" +
							"&cdnfunzione=<%=cdnfunzione%>";
		setWindowLocation(url);
	}


	function onLoad() {
		rinfresca();
		caricaDati();
	}
</script>
</head>

<body class="gestione" onload="onLoad()">

<%
	// TESTATE!
	if (StringUtils.isFilled(cdnLavoratore)) {
		InfCorrentiLav testata = new InfCorrentiLav(cdnLavoratore, user);
		testata.show(out);
	}
	
	if (StringUtils.isFilled(prgAzienda)) {
		InfCorrentiAzienda testata = new InfCorrentiAzienda(prgAzienda, prgUnita);
		testata.show(out);
	}
%>

<p class="titolo">Ricerca Documenti</p>

<af:form name="formina" action="AdapterHTTP" method="GET">
<!-- pregansi lasciare il GET senno' non funzia piu' bene il refresh post POP-UP dello storico -->
<input type="hidden" name="PAGE" value="ListaDocumentiPage" />
<input type="hidden" name="cdnFunzione" value="<%=cdnfunzione%>" />

<%-- Chiavi per le lookup (vedi file INC) --%>
<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>" />
<input type="hidden" name="prgAzienda" value="<%=prgAzienda%>" />
<input type="hidden" name="prgUnita" value="<%=prgUnita%>" />
<input type="hidden" name="lookLavoratore" value="<%=lookLavoratore%>" />
<input type="hidden" name="lookAzienda" value="<%=lookAzienda%>" />
<input type="hidden" name="contesto" value="<%=contesto%>" />

<%= htmlStreamTop %>
<table class="main">

	<% if (lookLavoratore) { %>
		<%-- ***************************************************************************** --%>
		<tr class="note">
			<td colspan="2">
				<div class="sezione2">Lavoratore&nbsp;&nbsp;
					<a href="#" onClick="apriSelezionaLavoratore();return false"><img src="../../img/binocolo.gif" alt="Cerca"></a>
				</div>
			</td>
		</tr>
		<%@ include file="lookLavoratore.inc" %>
		<%-- ***************************************************************************** --%>
	<% } %>

	<% if (lookAzienda) { %>
		<%-- ***************************************************************************** --%>
		<tr class="note">
			<td colspan="2">
				<div class="sezione2">Azienda&nbsp;&nbsp;
					<a href="#" onClick="apriSelezionaAzienda();return false"><img src="../../img/binocolo.gif" alt="Cerca"></a>
				</div>
			</td>
		</tr>
		<%@ include file="lookAzienda.inc" %>
		<%-- ***************************************************************************** --%>
	<% } %>

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
				<input type="submit" name="cerca" value="Cerca" class="pulsanti"
						onClick="return checkCampiRicerca()" />
				&nbsp;&nbsp;
				<input type="reset" name="reset" value="Annulla" class="pulsanti" />
			</span>
		</td>
	</tr>

	<%
	// gestione attributi	
	PageAttribs attributiDettaglio = new PageAttribs(user, "DocumentiAssociatiPage");
	if (attributiDettaglio.containsButton("nuovo")) {
	%>
	<tr>
		<td colspan="2">
			<span class="bottoni">
				<input type="button" name="nuovo" value="Nuovo documento" class="pulsante"
						onClick="nuovoBot()" />
			</span>
		</td>
	</tr>
	<%
	}
	%>

	<%
	if (StringUtils.isFilled(contesto)) {
	// MAH: PRIMA ERA:
	// if (StringUtils.isFilled(cdnLavoratore) || StringUtils.isFilled(prgAzienda))
	%>
	<tr>
		<td colspan="2">
			<span class="bottoni">
				<input type="button" name="cameBack" class="pulsanti" value="Torna alla lista"
						onClick="dettaglioDocumenti()" />
			</span>
		</td>
	</tr>
	<% } %>
	
</table>
<%= htmlStreamBottom %>

</af:form>

</body>
</html>
