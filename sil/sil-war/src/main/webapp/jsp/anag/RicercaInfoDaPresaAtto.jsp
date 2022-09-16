<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
			it.eng.afExt.utils.*,
			it.eng.sil.security.*,
			it.eng.sil.util.*,
			java.math.*,
			java.util.*"
	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%
// La presenza del cdnLavoratore mi serve per capire se provengo dal menu generale o dal menu contestuale del lavoratore.	
	String cdnLavoratore = null;
	String strCodiceFiscale = "", strNome="", strCognome="", codCPIComp="";
	String dataTrasferimentoDa="", dataTrasferimentoA="";
	
	String titolo = "Ricerca informazioni da presa d'atto";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	PageAttribs attributi = null;
	
	cdnLavoratore = (String)serviceRequest.getAttribute("cdnLavoratore");
	if (cdnLavoratore!=null) {
		attributi = new PageAttribs(user, _page);	
		// bisogna nascondere la sezione di ricerca del lavoratore		
	}
	else {
		// se provengo dalla lista avro' nella request i dati ricercati (per ora no)
		strCodiceFiscale     = Utils.notNull(serviceRequest.getAttribute("strCodiceFiscale"));
		strCognome           = Utils.notNull(serviceRequest.getAttribute("strCognome"));
		strNome              = Utils.notNull(serviceRequest.getAttribute("strNome"));
	}
	int cdnfunzione = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");

	// CONTROLLO PERMESSI SULLA PAGINA
	// PageAttribs attributi = new PageAttribs(user, _page);

	String htmlStreamTop    = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>
<head>
<title><%= titolo %></title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />

<af:linkScript path="../../js/"/>
<script>
	
    <% if (cdnLavoratore!=null) {
       //Genera il Javascript che si occuperà di inserire i links nel footer
       		attributi.showHyperLinks(out, requestContainer,responseContainer,"");
       }%>
</script>
</head>

<body class="gestione" onload="rinfresca()">

<af:showErrors />

<% 
	if (cdnLavoratore!=null ) { // se sono nel contesto del lavoratore mostro la testata
   		InfCorrentiLav testata = new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
   		testata.setSkipLista(true);
		testata.show(out);
	  }
%>

<p class="titolo"><%= titolo %></p>

<af:form name="Frm1" action="AdapterHTTP" method="POST">

<input type="hidden" name="PAGE"                 value="ListaInfoDaPresaAttoPage" />
<input type="hidden" name="cdnFunzione"          value="<%=cdnfunzione%>" />
<% if (cdnLavoratore!=null) { %>
<input type="hidden" name="cdnLavoratore"        value="<%=cdnLavoratore%>" />
<% } %>

<%= htmlStreamTop %>
	<table class="main">
		<%if (cdnLavoratore==null) {%>
		<tr><td colspan="2"/>&nbsp;</td></tr>
	    <tr>
	    	<td class="etichetta">Codice fiscale</td>
	    	<td class="campo"><af:textBox type="text" name="strCodiceFiscale" value="" maxlength="16" /></td>
	    </tr>
	    <tr>
		    <td class="etichetta">Cognome </td>
		    <td class="campo"><af:textBox type="text" name="strCognome" value="" maxlength="100"/></td>
	    </tr>   
	    <tr>
		    <td class="etichetta">Nome</td>
		    <td class="campo"><af:textBox type="text" name="strNome" value=""    maxlength="100" /></td>
	    </tr>
	    <tr>
		    <td class="etichetta">tipo ricerca</td>
		    <td class="campo">
			    <table cellpadding='0' cellspacing='0' border="0">
				    <tr>
					    <td><input type="radio" name="tipoRicerca" value="esatta" CHECKED/> esatta&nbsp;&nbsp;&nbsp;&nbsp;</td>
					    <td><input type="radio" name="tipoRicerca" value="iniziaPer"/> inizia per</td>
				    </tr>
		      	</table>
	    	</td>
	    </tr>
	    <tr><td>&nbsp;</td><tr>
	    <tr><td colspan="2" ><div class="sezione2"/></td></tr>	    	
		<%}%>				
		<tr>
			<td class="etichetta">Data di avvenuto trasferimento da</td>
			<td class="campo">
				<af:textBox type="date" title="Data di ricezione da" name="dattrasferimento_Da" 
				value="<%=dataTrasferimentoDa%>" size="12" maxlength="10" validateOnPost="true" />
				&nbsp;&nbsp;a&nbsp;&nbsp;
				<af:textBox type="date" title="di ricezione a" name="dattrasferimento_A" 
				value="<%=dataTrasferimentoA%>" size="12" maxlength="10" validateOnPost="true" />
		    </td>
		</tr>
		<tr>
		    <td class="etichetta">Centro per l'Impiego competente</td>
		    <td class="campo">
		      <af:comboBox name="codCPIComp" moduleName="M_DE_CPI" addBlank="true" selectedValue="<%=codCPIComp%>"/>
		    </td>
		<tr>
		<tr><td colspan=2>&nbsp;</td></tr>
		<tr>
			<td colspan="2">
				<span class="bottoni">
					<input type="submit" class="pulsanti" name="cerca" value="Cerca" />
					&nbsp;&nbsp;
					<input type="reset" class="pulsanti" name="annulla" value="Annulla"/>
				</span>
			</td>
		</tr>

	</table>
<%= htmlStreamBottom %>

</af:form>

</body>
</html>