<%@page import="it.eng.afExt.utils.DateUtils"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.afExt.utils.StringUtils,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.security.ProfileDataFilter,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  it.eng.sil.util.*
                  " %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
String _page = (String) serviceRequest.getAttribute("PAGE");
ProfileDataFilter filter = new ProfileDataFilter(user, _page);
PageAttribs pageAtts = new PageAttribs(user, _page);
boolean canAssegnaVoucher = false;
boolean canModify = false;
boolean canViewDataAssegnazione = false;

if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	return;
}
String dataOdierna = DateUtils.getNow();
canAssegnaVoucher = pageAtts.containsButton("ASSEGNA");
List sezioni = pageAtts.getSectionList();
canViewDataAssegnazione = sezioni.contains("DATA_ASSEGNAZIONE");
canModify = canAssegnaVoucher;
String cdnLav = serviceRequest.containsAttribute("CDNLAVORATORE")?serviceRequest.getAttribute("CDNLAVORATORE").toString():"";
String cdnFunzione = serviceRequest.containsAttribute("CDNFUNZIONE")?serviceRequest.getAttribute("CDNFUNZIONE").toString():"";
String prgLavPattoScelta = serviceRequest.containsAttribute("prgLavPattoScelta")?serviceRequest.getAttribute("prgLavPattoScelta").toString():"";
String prgPercorso = serviceRequest.containsAttribute("prgPercorso")?serviceRequest.getAttribute("prgPercorso").toString():"";
String prgColloquio = serviceRequest.containsAttribute("prgColloquio")?serviceRequest.getAttribute("prgColloquio").toString():"";
String prgAzione = serviceRequest.containsAttribute("prgazione")?serviceRequest.getAttribute("prgazione").toString():"";

String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

%>
<html>
<head>
	<%@ include file="../global/fieldChanged.inc" %>
<title>Assegna Voucher</title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 <af:linkScript path="../../js/" />
 
 <script language="JavaScript">
 	<%@ include file="_controlloDate_script.inc"%>

 	function reloadAzioniPatto() {
		var dataAssegnazioneInput = document.FrmAssegna.dtAssegnazione.value;
		if (dataAssegnazioneInput == "") {
			alert("La data assegnazione è obbligatoria");
		}
		else {
			if (!isDateParam(dataAssegnazioneInput)) {
				alert("La data assegnazione non è corretta");
			}
			else {
				if (isFuture(dataAssegnazioneInput)) {
		 	 		alert("La data assegnazione è successiva alla data odierna");
		 		}
		 		else {
		 	
					var url = "AdapterHTTP?PAGE=PattoAzioniLinguettaPage";
					url += "&CDNLAVORATORE=<%=cdnLav%>";
					url += "&CDNFUNZIONE=<%=cdnFunzione%>";
					url += "&PRGPERCORSO=<%=prgPercorso%>";
					url += "&PRGCOLLOQUIO=<%=prgColloquio%>";
					url += "&codAttivazione=" + document.FrmAssegna.codAttivazione.value;
					url += "&dtAssegnazione=" + dataAssegnazioneInput;
					url += "&ASSEGNAVCH=1";
					window.opener.location.replace(url);
					window.close();
		 		}
			}
		}
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
 
  <body class="gestione">
  
  <p class="titolo">Assegnazione Voucher</p>

<p>
	<font color="green">
  	</font>
  	<font color="red"><af:showErrors /></font>
</p>

<af:form name="FrmAssegna" method="POST" action="AdapterHTTP">
<%out.print(htmlStreamTop);%>
<table cellpadding="0" cellspacing="0" border="0" width="100%">

<tr>
 	<td class="etichetta">Codice attivazione&nbsp;</td>
 	<td class="campo">
	  <af:comboBox name="codAttivazione" moduleName="M_GetCodiceAttivazioneVoucher"
        	classNameBase="input" addBlank="true" blankValue="" disabled="<%=String.valueOf(!canAssegnaVoucher)%>"
            required="false" title="Codice attivazione" onChange="fieldChanged();"/>
	</td>
</tr>
<tr>
	<td class="etichetta">Data assegnazione&nbsp;</td>
	<td class="campo">
	<af:textBox name="dtAssegnazione" type="date" disabled="<%=String.valueOf(!(canAssegnaVoucher && canViewDataAssegnazione))%>"
						value="<%=dataOdierna%>" classNameBase="input"
						size="11" maxlength="10"
						required="true" />
	</td>
</tr>

</table>
<br>
<center>
<table>
<tr>
<%if (canAssegnaVoucher) {%>
	<td><input class="pulsante" type="button" name="btnProseguiAssegna" value="Prosegui" onClick="javascript:reloadAzioniPatto();"/></td>
	<td>&nbsp;</td>
<%}%>
<td><input type="button" class="pulsante" name="chiudi" value="Chiudi" onClick="javascript:window.close();"/></td>
</tr>
</table>
</center>
<%out.print(htmlStreamBottom);%>

<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLav%>">
<input type="hidden" name="PAGE" value="PattoAzioniLinguettaPage">
<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
<input type="hidden" name="prgLavPattoScelta" value="<%=prgLavPattoScelta%>">
<input type="hidden" name="prgPercorso" value="<%=prgPercorso%>">  
<input type="hidden" name="prgColloquio" value="<%=prgColloquio%>">
<input type="hidden" name="prgAzione" value="<%=prgAzione%>">
<input type="hidden" name="ASSEGNAVCH" value="1"> 

</af:form>
</body>
</html>