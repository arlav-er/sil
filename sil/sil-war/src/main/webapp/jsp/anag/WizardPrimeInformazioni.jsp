<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="com.engiweb.framework.base.*, 
                com.engiweb.framework.configuration.ConfigSingleton,
                 java.lang.*,
                java.text.*, java.util.*,it.eng.sil.util.*, java.math.*, it.eng.sil.security.*"%>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ include file="../global/Function_CommonRicercaComune.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
// NOTE: Attributi della pagina (pulsanti e link) 
PageAttribs attributi = new PageAttribs(user, "AnagWizardPrimeInformazioni");
//boolean canModify = attributi.containsButton("nuovo");
boolean canModify = true;

String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
%>

<%
/*
  NOTA: le pagine di ricerca devono avere lo stile prof_ro -> quindi invece di
  canModify si deve passare il valore false
*/
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
<title>Wizard: passo 1 di 2</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<af:linkScript path="../../js/" />
<script TYPE="text/javascript">
  <%
//Genera il Javascript che si occuperà di inserire i links nel footer
attributi.showHyperLinks(out, requestContainer, responseContainer, "");%>

<!--
// Per rilevare la modifica dei dati da parte dell'utente
var flagChanged = false;  


  function fieldChanged() {
    <%if (canModify) {
	out.print("flagChanged = true;");
	}%>
  }
-->   
    </script>

</head>
<body class="gestione" onload="rinfresca()">
<p class="titolo">Wizard inserimento lavoratori - passo 1 di 4</p>
<%out.print(htmlStreamTop);%>
<af:form method="POST" action="AdapterHTTP" name="Frm1">
	<table class="main">
		<tr>
			<td class="etichetta">Codice fiscale</td>
			<td class="campo"><af:textBox type="text" name="strCodiceFiscale"
				value="" size="20" maxlength="16" required="true" /></td>
		</tr>
		<tr>
			<td class="etichetta">Cognome</td>
			<td class="campo"><af:textBox type="text" name="strCognome" value=""
				size="20" maxlength="50" required="true" /></td>
		</tr>
		<tr>
			<td class="etichetta">Nome</td>
			<td class="campo"><af:textBox type="text" name="strNome" value=""
				size="20" maxlength="50" required="true" /></td>
		</tr>
		<tr>
			<td class="etichetta">Data di nascita</td>
			<td class="campo"><af:textBox type="date" name="datnasc" value=""
				size="10" maxlength="10" required="true" /></td>
		</tr>
		<tr>
			<td class="etichetta">Comune di nascita&nbsp;</td>
			<td class="campo"><af:textBox classNameBase="input"
				title="Comune di nascita"
				onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.codComNas, document.Frm1.codComNasHid, document.Frm1.strComNas, document.Frm1.strComNasHid, null, null, 'codice');"
				type="text" name="codComNas" value="" size="4" maxlength="4"
				validateWithFunction="valComuneNas"
				readonly="<%=String.valueOf(!canModify)%>" />&nbsp; <%if (canModify) {%>
			<A
				HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codComNas, document.Frm1.strComNas, null, 'codice','');"><IMG
				name="image" border="0" src="../../img/binocolo.gif"
				alt="cerca per codice" /></a>&nbsp; <%}%> <af:textBox type="hidden"
				name="codComNasHid" value="" /> <af:textBox type="text"
				classNameBase="input"
				onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.codComNas, document.Frm1.codComNasHid, document.Frm1.strComNas, document.Frm1.strComNasHid, null, null, 'descrizione');"
				name="strComNas" value="" size="30" maxlength="50"
				readonly="<%=String.valueOf(!canModify)%>" />&nbsp;*&nbsp; <%if (canModify) {%>
			<A
				HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codComNas, document.Frm1.strComNas, null, 'descrizione','');"><IMG
				name="image" border="0" src="../../img/binocolo.gif"
				alt="cerca per descrizione" /></a>&nbsp; <%}%> <af:textBox
				type="hidden" name="strComNasHid" value="" /></td>
		</tr>

		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="2" align="center">
			<input class="pulsanti" type="submit"
				name="Avanti" value="Avanti ->" /> &nbsp;&nbsp; <input type="reset"
				class="pulsanti" value="Annulla" /></td>
		</tr>
		<input type="hidden" name="PAGE" value="AnagWizardLista" />
		<input type="hidden" name="cdnFunzione" value="<%=_funzione%>" />
	</table>
</af:form>

<!--
<af:form method="POST" action="AdapterHTTP" dontValidate="true"><span
	class="bottoni"> <%if (canModify) {%> <input class="pulsanti"
	type="submit" name="passo2" value="Inserisci un nuovo lavoratore" />
 <input	type="hidden" name="PAGE" value="AnagDettaglioPageAnag" /> <input
	type="hidden" name="cdnFunzione" value="<%=_funzione%>" /> <input
	type="hidden" name="flag_insert" value="1" /> <%}%> </span></af:form>
-->
<!--/center-->
<%out.print(htmlStreamBottom);%>
</body>
</html>

