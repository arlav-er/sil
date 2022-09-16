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
                  it.eng.sil.util.*, it.eng.sil.module.voucher.*
                  " %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
String _page = (String) serviceRequest.getAttribute("PAGE");
ProfileDataFilter filter = new ProfileDataFilter(user, _page);
PageAttribs pageAtts = new PageAttribs(user, _page);
boolean canAnnullaVoucher = false;
boolean canModify = false;

if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
}
else {
	canAnnullaVoucher = pageAtts.containsButton("ANNULLA");
	canModify = canAnnullaVoucher;
}

String cdnLav = serviceRequest.containsAttribute("CDNLAVORATORE")?serviceRequest.getAttribute("CDNLAVORATORE").toString():"";
String cdnFunzione = serviceRequest.containsAttribute("CDNFUNZIONE")?serviceRequest.getAttribute("CDNFUNZIONE").toString():"";
String prgPercorso = serviceRequest.containsAttribute("prgPercorso")?serviceRequest.getAttribute("prgPercorso").toString():"";
String prgColloquio = serviceRequest.containsAttribute("prgColloquio")?serviceRequest.getAttribute("prgColloquio").toString():"";
String prgPattoLavoratore = serviceRequest.containsAttribute("prgPattoLavoratore")?serviceRequest.getAttribute("prgPattoLavoratore").toString():"";

String voucherAttivo = serviceRequest.containsAttribute("vchAttivo")?serviceRequest.getAttribute("vchAttivo").toString():"";
String codiceEnte = "";
String disabilitaCombo = (voucherAttivo.equalsIgnoreCase(StatoEnum.ATTIVATO.getCodice()))? "true" : "false";
String isStorico = serviceRequest.containsAttribute("isStorico")?serviceRequest.getAttribute("isStorico").toString():"";
String messaggioConferma = "Sei sicuro di voler annullare il Titolo di Acquisto?";
if(disabilitaCombo.equalsIgnoreCase("true")){
	codiceEnte ="RICHENT";
	if(!isStorico.equalsIgnoreCase("storico")){
		messaggioConferma = "E' stato richiesto l'annullamento di un Titolo di Acquisto già attivato,\nl'azione deve essere fatta su richiesta esplicita dell'ente attivato: confermi l'operazione?";
	}
}

String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

%>
<html>
<head>
	<%@ include file="../global/fieldChanged.inc" %>
<title>Annulla Voucher</title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 <af:linkScript path="../../js/" />
 
 <script language="JavaScript">

 	function reloadAzioniPatto() {	
		var url = "AdapterHTTP?PAGE=PattoAzioniLinguettaPage";
		url += "&CDNLAVORATORE=<%=cdnLav%>";
		url += "&CDNFUNZIONE=<%=cdnFunzione%>";
		url += "&PRGPERCORSO=<%=prgPercorso%>";
		url += "&PRGCOLLOQUIO=<%=prgColloquio%>";
		url += "&PRGPATTOLAVORATORE=<%=prgPattoLavoratore%>";
		url += "&codAnnullamento=" + document.FrmAnnulla.codAnnullamento.value;
		url += "&ANNULLAVCH=1";
		window.opener.location.replace(url);
		window.close();		
	}
 
 </script>
 
 </head>
 
  <body class="gestione" onload="rinfresca();">
  
  <p class="titolo">Annullamento Titolo di Acquisto</p>

<p>
	<font color="green">
  	</font>
  	<font color="red"><af:showErrors /></font>
</p>

<af:form name="FrmAnnulla" method="POST" action="AdapterHTTP">
<%out.print(htmlStreamTop);%>
<p><%= messaggioConferma %></p>
<table cellpadding="0" cellspacing="0" border="0" width="100%">
 
<tr>
 	<td class="etichetta">Motivo annullamento&nbsp;</td>
 	<td class="campo">
	  <af:comboBox name="codAnnullamento" moduleName="M_GET_MOTIVO_ANNULL_VOUCHER"
        	classNameBase="input" addBlank="true" selectedValue="<%= codiceEnte %>" disabled="<%= disabilitaCombo %>" 
            required="true" title="Motivo annullamento" onChange="fieldChanged();"/>
	</td>
</tr>

</table>
<br>
<center>
<table>
<tr>
<%if (canAnnullaVoucher) {%>
	<td><input class="pulsante" type="button" name="btnProseguiAnnulla" value="Prosegui" onclick="reloadAzioniPatto();"/></td>
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
<input type="hidden" name="prgPercorso" value="<%=prgPercorso%>">  
<input type="hidden" name="prgColloquio" value="<%=prgColloquio%>">
<input type="hidden" name="prgPattoLavoratore" value="<%=prgPattoLavoratore%>"> 
<input type="hidden" name="ANNULLAVCH" value="1"> 

</af:form>
</body>
</html>