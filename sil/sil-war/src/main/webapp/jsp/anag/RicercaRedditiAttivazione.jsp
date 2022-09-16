<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/Function_CommonRicercaComune.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page
	import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.StringUtils,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*"%>


<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%
	String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");

	String CF = StringUtils.getAttributeStrNotNull(serviceRequest, "CF");
	String COGNOME = StringUtils.getAttributeStrNotNull(serviceRequest, "COGNOME");
	String NOME = StringUtils.getAttributeStrNotNull(serviceRequest, "NOME");
	
	String codProvenienza = StringUtils.getAttributeStrNotNull(serviceRequest, "codProvenienza");
	String datprestazioneda = StringUtils.getAttributeStrNotNull(serviceRequest, "datprestazioneda");
	String datprestazionea = StringUtils.getAttributeStrNotNull(serviceRequest, "datprestazionea");
	String stato = StringUtils.getAttributeStrNotNull(serviceRequest, "stato");
	String datcaricamentoda = StringUtils.getAttributeStrNotNull(serviceRequest, "datcaricamentoda");
	String datcaricamentoa = StringUtils.getAttributeStrNotNull(serviceRequest, "datcaricamentoa");	
	String tipoRicerca = StringUtils.getAttributeStrNotNull(serviceRequest, "tipoRicerca");
	String nomeFile = StringUtils.getAttributeStrNotNull(serviceRequest, "nomeFile");

  	String htmlStreamTop = StyleUtils.roundTopTable(false);
  	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
	<title>Ricerca Redditi di Attivazione</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>  
<af:linkScript path="../../js/"/>
<script language="Javascript">
	function valorizzaHidden() {
		document.formRA.statoSelected_H.value = document.formRA.stato.options[document.formRA.stato.selectedIndex].text;
		document.formRA.provenienzaSelected_H.value = document.formRA.codProvenienza.options[document.formRA.codProvenienza.selectedIndex].text;
		document.formRA.fileSelected_H.value = document.formRA.nomeFile.options[document.formRA.nomeFile.selectedIndex].text;
		return true;
	}
	
</script>
</head>

<body class="gestione" onload="rinfresca();">
<br>
<p class="titolo">Ricerca Redditi di Attivazione</p>
<p align="center">
  <af:form name="formRA" action="AdapterHTTP" method="GET" onSubmit="valorizzaHidden()">        	
  <%out.print(htmlStreamTop);%> 
  <table class="main">
  
  <tr><td colspan="2"/>&nbsp;</td></tr>
  <tr>
    <td class="etichetta">Codice Fiscale</td>
    <td class="campo">
      <af:textBox type="text" name="CF" value="<%=CF%>" size="20" maxlength="16"/>
    </td>
  </tr>
  <tr>
    <td class="etichetta">Cognome</td>
    <td class="campo"><af:textBox type="text" name="COGNOME" value="<%=COGNOME%>" size="20" maxlength="50"/></td>
  </tr>
  <tr>
    <td class="etichetta">Nome</td>
    <td class="campo"><af:textBox type="text" name="NOME" value="<%=NOME%>" size="20" maxlength="50"/></td>
  </tr>
  <tr>
	<td class="etichetta">tipo ricerca</td>
	<td class="campo">
		<table colspacing="0" colpadding="0" border="0">
			<tr>
			<%if (tipoRicerca.equals("iniziaPer")) {%>
 				<td><input type="radio" name="tipoRicerca" value="esatta"/> esatta&nbsp;&nbsp;&nbsp;&nbsp;</td>
 				<td><input type="radio" name="tipoRicerca" value="iniziaPer" CHECKED /> inizia per</td>
			<%} else {%>
 				<td><input type="radio" name="tipoRicerca" value="esatta" CHECKED /> esatta&nbsp;&nbsp;&nbsp;&nbsp;</td>
 				<td><input type="radio" name="tipoRicerca" value="iniziaPer" /> inizia per</td>
			<%}%>
		</tr>			
		</table>
	</td>
  </tr>   
  <tr><td colspan="2"><hr width="90%"/></td></tr>
  <tr>
  	<td class="etichetta">Provenienza</td>
    <td class="campo">
      	<af:comboBox classNameBase="input" name="codProvenienza">
      		<option value="" <% if ( "".equalsIgnoreCase(codProvenienza) ) { %>SELECTED="true"<% } %> ></option>
      		<option value="F" <% if ( "F".equalsIgnoreCase(codProvenienza) ) { %>SELECTED="true"<% } %> >Da File</option>
      		<option value="M" <% if ( "M".equalsIgnoreCase(codProvenienza) ) { %>SELECTED="true"<% } %> >Agg. manuale</option>
      	</af:comboBox>
   	</td>  
  </tr>		     	
  <tr>
	<td class="etichetta" nowrap>Data prestazione da</td>
	<td class="campo"><af:textBox type="date" name="datprestazioneda"
			title="Data prestazione da" validateOnPost="true"
			value="<%=datprestazioneda%>" size="10" maxlength="10" />
		&nbsp;&nbsp;a&nbsp;&nbsp; <af:textBox type="date"
			name="datprestazionea" title="Data prestazione a" validateOnPost="true"
			value="<%=datprestazionea%>" size="10" maxlength="10" /></td>
  </tr>
  <tr>
    <td class="etichetta">Stato</td>
    <td class="campo">
      <af:comboBox name="stato" moduleName="M_GetStatoRA" addBlank="true" selectedValue="<%=stato%>"/>
    </td>
  <tr>  
  <tr>
	<td class="etichetta" nowrap>Data caricamento da</td>
	<td class="campo"><af:textBox type="date" name="datcaricamentoda"
			title="Data caricamento da" validateOnPost="true"
			value="<%=datcaricamentoda%>" size="10" maxlength="10" />
		&nbsp;&nbsp;a&nbsp;&nbsp; <af:textBox type="date"
			name="datcaricamentoa" title="Data caricamento a" validateOnPost="true"
			value="<%=datcaricamentoa%>" size="10" maxlength="10" /></td>
  </tr> 
  <tr>
	<td class="etichetta" nowrap>Nome file</td>
   	<td class="campo">
    	<af:comboBox name="nomeFile" moduleName="M_GetFileDaAutorizzare" addBlank="true" selectedValue="<%=nomeFile%>"/>
    </td>
  </tr>    
  <tr><td colspan="2">&nbsp;</td></tr>
  <tr> 
    <td colspan="2" align="center">
      <input name="Cerca" type="submit" class="pulsanti" value="Cerca">
      &nbsp;&nbsp;
      <input name="reset" type="reset" class="pulsanti" value="Annulla">
    </td>
    <input type="hidden" name="PAGE" value="ListaRedditiAttivazionePage" />
	<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
	<input type="hidden" name="statoSelected_H" value=""/>
	<input type="hidden" name="provenienzaSelected_H" value=""/>
	<input type="hidden" name="fileSelected_H" value=""/>
  </tr>

  </table>
  <%out.print(htmlStreamBottom);%> 
  </af:form>
</p>

</body>
</html>
