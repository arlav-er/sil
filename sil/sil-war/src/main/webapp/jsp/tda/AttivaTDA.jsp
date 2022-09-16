<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ include file="CommonScripts.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.DateUtils,
                  it.eng.afExt.utils.StringUtils,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
String _page = (String) serviceRequest.getAttribute("PAGE");
String _funzione = (String) serviceRequest.getAttribute("cdnFunzione");
// NOTE: Attributi della pagina (pulsanti e link) 
PageAttribs attributi = new PageAttribs(user, _page);
boolean canAttiva = false;
boolean canModify = false;
boolean canViewDataAttivazione = false;

ProfileDataFilter filter = new ProfileDataFilter(user, _page);
if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	return;
}
canAttiva = attributi.containsButton("ATTIVA");
canModify = canAttiva;
List sezioni = attributi.getSectionList();
canViewDataAttivazione = sezioni.contains("DATA_ATTIVAZIONE");
String dataOdierna 		= DateUtils.getNow();
// inizio parametri della ricerca tda
String strCodiceFiscale = StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscale");
String strCognome       = StringUtils.getAttributeStrNotNull(serviceRequest,"strCognome");
String strNome          = StringUtils.getAttributeStrNotNull(serviceRequest,"strNome");
String statoTDA         = StringUtils.getAttributeStrNotNull(serviceRequest,"statoTDA");
String azioneTDA        = StringUtils.getAttributeStrNotNull(serviceRequest,"azioneTDA");
String codCPI 		  	= StringUtils.getAttributeStrNotNull(serviceRequest,"codCPI");
String tipoRicerca      = StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRicerca");
String strCodAttivazione  = StringUtils.getAttributeStrNotNull(serviceRequest,"strCodAttivazione");
String dataStatoDa		= StringUtils.getAttributeStrNotNull(serviceRequest, "dataStatoDa");  
String dataStatoA		    = StringUtils.getAttributeStrNotNull(serviceRequest, "dataStatoA"); 
String codAnnullTDA		= StringUtils.getAttributeStrNotNull(serviceRequest,"codAnnullTDA");
String statoPagamentoTDA  = StringUtils.getAttributeStrNotNull(serviceRequest,"statoPagamentoTDA");
String assegnatiScaduti	= StringUtils.getAttributeStrNotNull(serviceRequest,"assegnatiScaduti");
String attivatiScaduti	= StringUtils.getAttributeStrNotNull(serviceRequest,"attivatiScaduti");
String cfEnteCollegato    = StringUtils.getAttributeStrNotNull(serviceRequest,"cfEnte");
String sedeEnteCollegato  = StringUtils.getAttributeStrNotNull(serviceRequest,"sedeEnteAtt");
//fine parametri della ricerca tda
String cfEnteCollegatoUser = "";
String tipoGruppoCollegato = user.getCodTipo();
if (tipoGruppoCollegato.equalsIgnoreCase(it.eng.sil.module.voucher.Properties.SOGGETTO_ACCREDITATO)) {
	cfEnteCollegatoUser = user.getCfUtenteCollegato();		  
}
String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>
<html>
<head>
	<title>Attiva TDA</title>
   	<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 	<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
   	<af:linkScript path="../../js/" />

  	<script language="Javascript">
   	<% 
    	//Genera il Javascript che si occuperà di inserire i links nel footer
   		attributi.showHyperLinks(out, requestContainer,responseContainer,"");
  	%>
  	<%@ include file="../patto/_controlloDate_script.inc"%>

  	function tornaLista() {
        
  	 if (isInSubmit()) return;
    
       url="AdapterHTTP?PAGE=ListaTDAPage";
       url += "&CDNFUNZIONE="+"<%=_funzione%>";
       url += "&strCodAttivazione="+"<%=strCodAttivazione%>";
       url += "&strCodiceFiscale="+"<%=strCodiceFiscale%>";
       url += "&strCognome="+"<%=strCognome%>";
       url += "&strNome="+"<%=strNome%>";
       url += "&tipoRicerca="+"<%=tipoRicerca%>";
       url += "&statoTDA="+"<%=statoTDA%>";
       url += "&dataStatoDa="+"<%=dataStatoDa%>";
       url += "&dataStatoA="+"<%=dataStatoA%>";
       url += "&assegnatiScaduti="+"<%=assegnatiScaduti%>";
       url += "&attivatiScaduti="+"<%=attivatiScaduti%>";
       url += "&statoPagamentoTDA="+"<%=statoPagamentoTDA%>";
       url += "&codAnnullTDA="+"<%=codAnnullTDA%>";
       url += "&azioneTDA="+"<%=azioneTDA%>";
       url += "&cfEnteAtt="+"<%=cfEnteCollegato%>";
       url += "&sedeEnteAtt="+"<%=sedeEnteCollegato%>";
       url += "&codCPI="+"<%=codCPI%>";
       
       setWindowLocation(url);
    }
    
  	function controllaDataAttivazione() {
		var dataAttivazioneInput = document.Frm1.dtAttivazione.value;
		if (dataAttivazioneInput == "") {
			alert("La Data Attivazione è obbligatoria");
			return false;
		}
		if (!isDateParam(dataAttivazioneInput)) {
			alert("La Data Attivazione non è corretta");
			return false;
		}
		if (isFuture(dataAttivazioneInput)) {
 	 		alert("La Data Attivazione è successiva alla data odierna");
 	 		return false;
 		}
 		return true;
	}

 	function isDateParam(dateStr) {
 		var datePat = /^(\d{1,2})(\/|-)(\d{1,2})(\/|-)(\d{4})$/;
 	    var matchArray = dateStr.match(datePat); // is the format ok?

 	    if (matchArray == null) {
 	      return false;
 	    }

 	    month = matchArray[3]; // p@rse date into variables
 	    day = matchArray[1];
 	    year = matchArray[5];
 	   	
 	    if (month < 1 || month > 12) { // check month range
 	      return false;
 	    }

 	    if (day < 1 || day > 31) {
 	      return false;
 	    }

 	    if ((month==4 || month==6 || month==9 || month==11) && day==31) {
 	      return false;
 	    }

 	    if (month == 2) { // check for february 29th
 	      var isleap = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
 	      if (day > 29 || (day==29 && !isleap)) {
 	        return false;
 	      }
 	    }
 	    return true; // date is valid
 	}
  	
   </script>
</head>
    
<body class="gestione" onload="rinfresca();">
<p class="titolo">Attiva Titolo</p>
<%out.print(htmlStreamTop);%>
<af:form method="POST" action="AdapterHTTP" name="Frm1">
	<table class="main">
	<%if (tipoGruppoCollegato.equalsIgnoreCase(it.eng.sil.module.voucher.Properties.SOGGETTO_ACCREDITATO)) {%>
	    <tr>
	    	<td class="etichetta">CF Soggetto Accreditato</td>
	       	<td class="campo">
	        	<af:textBox type="text" classNameBase="input" name="enteAtt" value="<%=cfEnteCollegatoUser%>" size="20" maxlength="16" 
	        		title="CF Soggetto Accreditato" readonly="true"/>
	        </td>
	    </tr>
	<%} else {%>
		<input type="hidden" name="enteAtt" value=""/>
	<%}%>
    <tr>
    	<td class="etichetta">Sede Soggetto Accreditato</td>
       	<td class="campo">
       		<%if (tipoGruppoCollegato.equalsIgnoreCase(it.eng.sil.module.voucher.Properties.SOGGETTO_ACCREDITATO)) {%>
	        	<af:comboBox classNameBase="input" title="Sede Soggetto Accreditato" name="sedeEnteAttivazione" moduleName="M_SediEnteCollegato" 
	            	required="true" disabled="<%=String.valueOf(!canAttiva)%>"/>
	        <%} else {%>
	        	<af:comboBox classNameBase="input" title="Sede Soggetto Accreditato" name="sedeEnteAttivazione" moduleName="M_SediEntiVoucher" 
	            	required="true" disabled="<%=String.valueOf(!canAttiva)%>" addBlank="true"/>
	        <%}%>
        </td>
    </tr>
    <tr>
    	<td class="etichetta">Codice Fiscale Cittadino</td>
       	<td class="campo">
        	<af:textBox type="text" classNameBase="input" name="strCodiceFiscaleCittadino" value="" required="true" size="20" maxlength="16" 
        		validateWithFunction="checkCF" title="Codice Fiscale Cittadino" readonly="<%=String.valueOf(!canAttiva)%>"/>
        </td>
   	</tr>
   	<tr>
    	<td class="etichetta">Codice di Attivazione</td>
       	<td class="campo">
        	<af:textBox type="text" classNameBase="input" name="strCodiceTitolo" value="" readonly="<%=String.valueOf(!canAttiva)%>"
        	required="true" size="21" maxlength="20" title="Codice Titolo"/>
        </td>
   	</tr>
   	<tr>
		<td class="etichetta">Data Attivazione&nbsp;</td>
		<td class="campo">
		<af:textBox name="dtAttivazione" type="date" validateWithFunction="controllaDataAttivazione"
							value="<%=dataOdierna%>" classNameBase="input"
							size="11" maxlength="10" disabled="<%=String.valueOf(!(canAttiva && canViewDataAttivazione))%>"
							required="true" title="Data Attivazione"/>
		</td>
	</tr>
   	</table>
   	
	<br>
	<center>
	<table>
	<tr><td colspan="2">&nbsp;</td></tr>
	 <%if (canAttiva) {%>
	<tr>
	<td>
		<input class="pulsante" type="submit" name="btnAttivaTitolo" value="Attiva"/>&nbsp;
		<input type="reset" class="pulsanti" value="Annulla"/>
	</td>  
	</tr>
	<%}%>
	<tr>
	<td align="center">
		<input class="pulsante" type = "button" name="torna" value="Torna alla lista" onClick="tornaLista();"/>
	</td>
	</tr>
	</table>
	</center>
	
	<input type="hidden" name="PAGE" value="ListaTDAPage"/>
	<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
	<input type="hidden" name="ATTIVATDA" value="S"/>
	<input type="hidden" name="strCodiceFiscale" value="<%=strCodiceFiscale%>"/>
	<input type="hidden" name="strCognome" value="<%=strCognome%>"/>
	<input type="hidden" name="strNome" value="<%=strNome%>"/>
	<input type="hidden" name="statoTDA" value="<%=statoTDA%>"/>
	<input type="hidden" name="azioneTDA" value="<%=azioneTDA%>"/>
	<input type="hidden" name="codCPI" value="<%=codCPI%>"/>
	<input type="hidden" name="tipoRicerca" value="<%=tipoRicerca%>"/>
	<input type="hidden" name="strCodAttivazione" value="<%=strCodAttivazione%>"/>
	<input type="hidden" name="dataStatoDa" value="<%=dataStatoDa%>"/>
	<input type="hidden" name="dataStatoA" value="<%=dataStatoA%>"/>
	<input type="hidden" name="assegnatiScaduti" value="<%=assegnatiScaduti%>"/>
	<input type="hidden" name="attivatiScaduti" value="<%=attivatiScaduti%>"/>
	<input type="hidden" name="statoPagamentoTDA" value="<%=statoPagamentoTDA%>"/>
	<input type="hidden" name="codAnnullTDA" value="<%=codAnnullTDA%>"/>
	<input type="hidden" name="cfEnteAtt" value="<%=cfEnteCollegato%>"/>
	<input type="hidden" name="sedeEnteAtt" value="<%=sedeEnteCollegato%>"/>
	
</af:form>
<%out.print(htmlStreamBottom);%>
</body>
</html>