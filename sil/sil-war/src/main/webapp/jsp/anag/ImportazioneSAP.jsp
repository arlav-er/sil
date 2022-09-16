<%@ page contentType="text/html;charset=utf-8"%>
 
<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.math.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<% 

String _page = (String) serviceRequest.getAttribute("PAGE"); 
ProfileDataFilter filter = new ProfileDataFilter(user, _page);
//NOTE: Attributi della pagina (pulsanti e link) 
PageAttribs attributi = new PageAttribs(user, _page);

String numEsperienzeSAP = "0";
String numEspImportate = "0";
String numEspNonImportate = "0";
String numPoliticheComp = "0";
String numColloquiIns = "0";
String numPoliticheImportate = "0";
String numPoliticheNonMappate = "0";
String numPoliticheNonImportate = "0";
String codErroreSezioneAnag = "";
String codErroreSezioneDid = "";
String codErroreSezioneEsp = "";
String codErroreSezionePolitiche = "";
String esitoDatiAnagrafici = "";
String esitoDatiAmministrativi = "";
String cdnLavoratoreImportato = "";

boolean canModify = false;
String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

String valoreConfImpSap = serviceResponse.containsAttribute("M_CONFIG_IMPORTAZIONE_SAP.ROWS.ROW.STRVALORE")?
		serviceResponse.getAttribute("M_CONFIG_IMPORTAZIONE_SAP.ROWS.ROW.STRVALORE").toString():"";

String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
if(serviceResponse.containsAttribute("M_ImportaSap")){
	codErroreSezioneAnag = serviceResponse.getAttribute("M_ImportaSap.CODICEERROREANAG")!=null?
			serviceResponse.getAttribute("M_ImportaSap.CODICEERROREANAG").toString():"";
			
	codErroreSezioneDid = serviceResponse.getAttribute("M_ImportaSap.CODICEERROREDID")!=null?
			serviceResponse.getAttribute("M_ImportaSap.CODICEERROREDID").toString():"";
			
	codErroreSezioneEsp = serviceResponse.getAttribute("M_ImportaSap.CODICEERROREESPLAV")!=null?
			serviceResponse.getAttribute("M_ImportaSap.CODICEERROREESPLAV").toString():"";
			
	codErroreSezionePolitiche = serviceResponse.getAttribute("M_ImportaSap.CODICEERROREPOLITICHE")!=null?
			serviceResponse.getAttribute("M_ImportaSap.CODICEERROREPOLITICHE").toString():"";
			
	numEsperienzeSAP = serviceResponse.getAttribute("M_ImportaSap.ESPLAVORATIVESAP")!=null?
			serviceResponse.getAttribute("M_ImportaSap.ESPLAVORATIVESAP").toString():"0";
			
	numEspImportate = serviceResponse.getAttribute("M_ImportaSap.ESPLAVORATIVEIMPORTATE")!=null?
			serviceResponse.getAttribute("M_ImportaSap.ESPLAVORATIVEIMPORTATE").toString():"0";
	
	numPoliticheComp = serviceResponse.getAttribute("M_ImportaSap.POLITICHECOMPETENZA")!=null?
			serviceResponse.getAttribute("M_ImportaSap.POLITICHECOMPETENZA").toString():"0";
		
	numColloquiIns = serviceResponse.getAttribute("M_ImportaSap.COLLOQUIINSERITI")!=null?
			serviceResponse.getAttribute("M_ImportaSap.COLLOQUIINSERITI").toString():"0";
			
	numPoliticheImportate = serviceResponse.getAttribute("M_ImportaSap.POLITICHEIMPORTATE")!=null?
			serviceResponse.getAttribute("M_ImportaSap.POLITICHEIMPORTATE").toString():"0";
		
	numPoliticheNonImportate = serviceResponse.getAttribute("M_ImportaSap.POLITICHENONIMPORTATE")!=null?
			serviceResponse.getAttribute("M_ImportaSap.POLITICHENONIMPORTATE").toString():"0";
	
	numPoliticheNonMappate = serviceResponse.getAttribute("M_ImportaSap.POLITICHENONMAPPATE")!=null?
			serviceResponse.getAttribute("M_ImportaSap.POLITICHENONMAPPATE").toString():"0";
			
	cdnLavoratoreImportato = serviceResponse.getAttribute("M_ImportaSap.CDNLAVORATOREIMPORTATO")!=null?
			serviceResponse.getAttribute("M_ImportaSap.CDNLAVORATOREIMPORTATO").toString():"";
			
	BigDecimal appoggio = (new BigDecimal(numEsperienzeSAP)).subtract(new BigDecimal(numEspImportate));
	numEspNonImportate = appoggio.toString();
	
	esitoDatiAnagrafici = serviceResponse.getAttribute("M_ImportaSap.ESITODATIANAGRAFICI")!=null?
			serviceResponse.getAttribute("M_ImportaSap.ESITODATIANAGRAFICI").toString():"";
			
	esitoDatiAmministrativi = serviceResponse.getAttribute("M_ImportaSap.ESITODATIAMMINISTRATIVI")!=null?
			serviceResponse.getAttribute("M_ImportaSap.ESITODATIAMMINISTRATIVI").toString():"";
}
%>

<html>
<head>
  <title>Risultati importazione SAP</title>
  <link rel="stylesheet" type="text/css" href="../../css/stiliCoop.css"/>
  <link rel="stylesheet" type="text/css" href="../../css/listdetailCoop.css"/>
  
  <af:linkScript path="../../js/"/>
  
  <script language="JavaScript">

 	function caricaScheda() {
 		var url;
	    url = "AdapterHTTP?PAGE=SapGestioneServiziPage";
	    url = url + "&CDNFUNZIONE=1";
	    url = url + "&CDNLAVORATORE=<%=cdnLavoratoreImportato%>";
	    window.opener.document.location = url;
		window.close();
 	}

  </script>
 
</head>
<body  class="gestione" onload="rinfresca();">
<p>
	<font color="green">
		<af:showMessages/>
  	</font>
  	<font color="red"><af:showErrors /></font>
</p>

<p class="titolo">Esito Importazione</p>
<% out.print(htmlStreamTop); %>
<table class="main" border="0">
<tr valign="top"><td width="100%"><div class="sezione2">Sezione 1 Dati Anagrafici</div></td></tr>
<tr valign="top">
<td class="campo2"><b>&nbsp;<%= Utils.notNull(esitoDatiAnagrafici) %></b></td>
</tr>
<tr valign="top"><td>&nbsp;</td></tr>
<%if (codErroreSezioneAnag.equals("00")) {%>
	<tr valign="top"><td width="100%"><div class="sezione2">Sezione 2 Dati Amministrativi</div></td></tr>
	<tr valign="top">
	<td class="campo2"><b>&nbsp;<%= Utils.notNull(esitoDatiAmministrativi) %></b></td>
	</tr>
	<tr valign="top"><td>&nbsp;</td></tr>
	<tr valign="top"><td width="100%"><div class="sezione2">Sezione 4 Esperienze di lavoro</div></td></tr>
	<tr valign="top">
	<td class="campo2"><b>&nbsp;Esperienze presenti nella SAP:&nbsp;<%= Utils.notNull(numEsperienzeSAP) %></b></td>
	</tr>
	<tr valign="top">
	<td class="campo2"><b>&nbsp;Esperienze importate:&nbsp;<%= Utils.notNull(numEspImportate) %></b></td>
	</tr>
	<tr valign="top">
	<td class="campo2"><b>&nbsp;Esperienze non importate:&nbsp;<%= Utils.notNull(numEspNonImportate) %></b></td>
	</tr>
	<%if (!valoreConfImpSap.equalsIgnoreCase("POLITICHE_OFF")) {%>
		<tr valign="top"><td>&nbsp;</td></tr>
		<tr valign="top"><td width="100%"><div class="sezione2">Sezione 6 Politiche attive</div></td></tr>
		<tr valign="top">
		<td class="campo2"><b>&nbsp;Politiche attive di competenza:&nbsp;<%= Utils.notNull(numPoliticheComp) %></b></td>
		</tr>
		<tr valign="top">
		<td class="campo2"><b>&nbsp;Colloqui importati:&nbsp;<%= Utils.notNull(numColloquiIns) %></b></td>
		</tr>
		<tr valign="top">
		<td class="campo2"><b>&nbsp;Politiche attive inserite:&nbsp;<%= Utils.notNull(numPoliticheImportate) %></b></td>
		</tr>
		<tr valign="top">
		<td class="campo2"><b>&nbsp;Politiche attive non importate:&nbsp;<%= Utils.notNull(numPoliticheNonImportate) %></b></td>
		</tr>
		<tr valign="top">
		<td class="campo2"><b>&nbsp;Politiche attive non mappate:&nbsp;<%= Utils.notNull(numPoliticheNonMappate) %></b></td>
		</tr>
	<%}%>
	<tr valign="top"><td>&nbsp;</td></tr>
<%}%>
</table>
<% out.print(htmlStreamBottom); %>

<%if (codErroreSezioneAnag.equals("00") && !cdnLavoratoreImportato.equals("")) {%>
	<br>
	<center>
	<table class="main">
	<tr>
	<td>
	<input class="pulsante" type="button" name="chiudi" value="Chiudi e vai alla scheda" onClick="caricaScheda()" />
	</td>
	</tr>
	</table>
	</center>
<%} else {%>
	<br>
	<center>
	<table class="main">
	<tr>
	<td>
	<input class="pulsante" type="button" name="chiudi" value="Chiudi" onclick="window.close()" />
	</td>
	</tr>
	</table>
	</center>
<%}%>
 
</body>
</html>