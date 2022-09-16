<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,it.eng.afExt.utils.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.User,
                it.eng.sil.util.*,
                it.eng.sil.security.PageAttribs,
                 java.math.*"
%>

<%@ taglib uri="aftags" prefix="af" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);

String chiamata =StringUtils.getAttributeStrNotNull(serviceRequest,"chiamata");
String CodCPI =StringUtils.getAttributeStrNotNull(serviceRequest,"CodCPI");

String queryString = null;

String _page = (String) serviceRequest.getAttribute("PAGE");
PageAttribs attributi = new PageAttribs(user, _page);

%>

<%@ include file="../documenti/_apriGestioneDoc.inc"%>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <script type="text/javascript">
  
  function stampaAdesioni () {
	if (!controllaFunzTL()) return;
	
	document.form1.descrizione.value = document.form1.CodCPI.options[document.form1.CodCPI.selectedIndex].text;
	var descrizione = document.form1.descrizione.value;
  	var CodCPI = document.form1.CodCPI.value;
	var chiamata = document.form1.chiamata.value;
	var modPubblicazione = document.form1.modPubblicazione.value;
	var tipoAvvPubb = document.form1.tipoAvvPubb.value;
	var tipoFile;
	  if(tipoAvvPubb == "AS"){ 
	  	tipoFile = "ALSEAP";
	   } else { 
	   	tipoFile = "CMAP"; 
	   }
	   apriGestioneDoc('RPT_AVVISO_PUBBLICO','&CodCPI='+CodCPI +'&descrizione='+descrizione+'&chiamata='+chiamata +'&modPubblicazione='+modPubblicazione +'&tipoAvvPubb='+tipoAvvPubb, tipoFile);
	   undoSubmit();
	}

</script>
 
</head>

<body class="gestione" onload="rinfresca()">
<br>
<p class="titolo">Stampa avviso pubblico</p>

<%out.print(htmlStreamTop);%>  

<af:form action="AdapterHTTP" name="form1" method="GET">

<table class="main">
	<tr>
		<td class="etichetta">Tipo Avviso Pubblico</td>
		<td class="campo">
			<af:comboBox name="tipoAvvPubb" title="Tipo Avviso Pubblico"
	                 	 multiple="false" required="true" focusOn="false"
	                 	 moduleName="M_GetTipoAvvPubb" addBlank="true"
	                 	 blankValue="" selectedValue=""/>
	    </td>
	</tr>
	<tr>
		<td class="etichetta">Data di chiamata</td>
		<td class="campo">
			<af:textBox name="chiamata" title="Data di chiamata"
		                type="date"
		    	        size="10"
		                maxlength="10"
		                required="true"
		                validateOnPost="true"
		                disabled="false"
		                value=""/>
		</td>
	</tr>
	<tr>
		<td class="etichetta">Centro per l'Impiego</td>
		<td class="campo">
			<af:comboBox name="CodCPI"
	        	         title="Centro per l'Impiego"
	            	     multiple="false"
	              	     required="true"
	                 	 focusOn="false"
	                 	 moduleName="M_GetCpiPoloProvinciale"
	                 	 addBlank="true"
	                 	 blankValue=""
	                 	 selectedValue=""/>
	  	</td>
	</tr>
	<input  type="hidden" name="descrizione" value=""/> 
	<tr>
    	<td class="etichetta" valign="top">Modalità pubblicazione</td>
    	<td class="campo">
      		<af:comboBox title="Modalità Pubblicazione" name="modPubblicazione" addBlank="true" required="true">
        		<OPTION value="1">Web      </OPTION>
        		<OPTION value="2">Bacheca  </OPTION>
      		</af:comboBox>
    	</td>
  </tr>
</table>

<br/>
	<center>
		<input type="button" class="pulsanti" value="Stampa" onclick="stampaAdesioni()" />
	</center>
<br/>
</af:form>

<%out.print(htmlStreamBottom);%>

</body>
</html>

