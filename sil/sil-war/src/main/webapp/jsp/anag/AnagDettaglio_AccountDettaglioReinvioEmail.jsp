<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="com.engiweb.framework.base.*, 
                com.engiweb.framework.configuration.ConfigSingleton,
                 java.lang.*,
                it.eng.sil.security.ProfileDataFilter, 
                java.text.*, java.util.*,it.eng.sil.util.*, java.math.*,
                it.eng.sil.security.* "%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
	String cdnLavoratore = (String )serviceRequest.getAttribute("CDNLAVORATORE");
    String idPfPrincipal = (String )serviceRequest.getAttribute("idPfPrincipal");
    String emailPortale = (String )serviceRequest.getAttribute("emailPortale");
    String emailSil = (String )serviceRequest.getAttribute("emailSil");
    String risposta = (String )serviceResponse.getAttribute("M_ReinoltraMailAccount.RISULTATO");
	String emailCC = "";
	
	if (emailSil != null && emailPortale != null && (!emailSil.equalsIgnoreCase(emailPortale))) {
		emailCC = emailSil;
	}
	
    boolean isOutput = risposta != null;
    
	String _page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	
	boolean canModify = true;
	
	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
		PageAttribs attributi = new PageAttribs(user, _page);
		//boolean existReinoltraMail = attributi.containsButton("REINOLTRAMAILALTRO?");

		InfCorrentiLav infCorrentiLav = new InfCorrentiLav(requestContainer.getSessionContainer(), cdnLavoratore, user);
	  
	    String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	    String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);	
	    
%>

<html>
<head>
<title>Anagrafica dettaglio</title>

<af:linkScript path="../../js/"/>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<% if (isOutput) { %>
<link rel="stylesheet" type="text/css" href="../../css/listdetailCoop.css">
<% } %>

<style>
td.etichetta_small {
	color: #000066; 
	font-family: Verdana, Arial, Helvetica, Sans-serif; 
	font-size: 11px; 
	text-align: right;
	font-weight: normal;
	text-decoration: none;
	width: 20%;
	padding-right: 12px;
}

td.campo_big {
	color: #000066;
	font-family: Verdana, Arial, Helvetica, Sans-serif; 
	font-size: 11px; 
	text-align: left;
	text-decoration: none;
	width: 80%;
}
</style>

</head>

<body class="gestione">

<%
int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
Linguette l  = new Linguette(user,  _funzione, "AnagDettaglioPageAccount", new BigDecimal(cdnLavoratore));
infCorrentiLav.show(out); 
l.show(out);
%>

<SCRIPT>

	var urlpage="AdapterHTTP?";

	function getURLPageBase() {    
	    urlpage+="CDNFUNZIONE=<%=_funzione%>&";
	    urlpage+="CDNLAVORATORE=<%=cdnLavoratore%>&";
	    return urlpage;
	}

	function tornaIndietroAccount() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) {
			return;	  
		}
		urlpage = getURLPageBase();
		urlpage+="PAGE=AnagDettaglioPageAccount&";
		setWindowLocation(urlpage);
	}

	window.top.menu.caricaMenuLav( <%=_funzione%>,   <%=cdnLavoratore%>);

</SCRIPT>

<font color="red">
	<af:showErrors/>
</font>

<% if (!isOutput) { %>

<af:form method="POST" action="AdapterHTTP" name="Frm1" >

	<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>"/>
	<input type="hidden" name="idPfPrincipal" value="<%=idPfPrincipal%>"/>
	<input type="hidden" name="PAGE" value="AnagDettaglioPageAccountDettaglioReinvioEmail">
	<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"%>
	
<p class="titolo">Reinvio Mail di accreditamento</p>

<%out.print(htmlStreamTop);%>

<br/>
Il reinvio della mail con le credenziali comporterà la modifica della password per il primo accesso al portale, che verrà comunicata nella mail con le credenziali di accesso.
<br/>
<br/>
<table class="main">

	<tr>
		<td class="etichetta_small">A:</td>
	    <td class="campo_big">
			<af:textBox name="email"  value="<%=emailPortale%>" readonly="true"  classNameBase="input" size="80" maxlength="80" required="true"/> <br/>(indirizzo e-mail di registrazione sul portale)
	    </td>
	</tr>
	
	<tr>
	    <td class="etichetta_small">CC:</td>
	    <td class="campo_big">
			<af:textBox name="destinatarioCC"  value="<%=emailCC%>" readonly="false"  classNameBase="input" size="80" maxlength="80"/> <br/>(specificare gli indirizzi separati dalla virgola)
	    </td>
	</tr>
	
	<tr>
		<td colspan="2" align="center">
			<br/>
			<input class="pulsante" type="submit" name="DO_REINOLTRAEMAIL" value="Reinvio mail accreditamento">
			<input type="button" class="pulsanti"  name = "chiudi" value="Chiudi"  onclick="tornaIndietroAccount()">
		</td>
	</tr>
	
</table>

<%out.print(htmlStreamBottom);%>

<br/>
<br/>

</af:form>

<% } else { %>

<p class="titolo">Reinvio Mail di accreditamento</p>

<%out.print(htmlStreamTop);%>

<br/>
<%=risposta%>
<br/>
<br/>

<table class="main">

	<tr>
		<td colspan="2" align="center">
			<br/>
			<input type="button" class="pulsanti"  name = "chiudi" value="Chiudi"  onclick="indietro()">
		</td>
	</tr>
	
</table>

<%out.print(htmlStreamBottom);%>

<br/>
<br/>

<% } %>

	<script>

	function indietro() {
		
		urlpage = getURLPageBase();
		urlpage+="PAGE=AnagDettaglioPageAccount&";
		setWindowLocation(urlpage);
		
	}

	</script>

</body>
</html>

<%}  %>
