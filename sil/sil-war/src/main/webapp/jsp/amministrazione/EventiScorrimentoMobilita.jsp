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
String cdnLav = serviceRequest.containsAttribute("CDNLAVORATORE")?serviceRequest.getAttribute("CDNLAVORATORE").toString():"";
String datInizioMob = serviceRequest.containsAttribute("DATINIZIOMOB")?serviceRequest.getAttribute("DATINIZIOMOB").toString():"";
boolean eseguitoCalcolo = serviceRequest.containsAttribute("EseguiCalcolo")?true:false;
boolean errorOccurred = responseContainer.getErrorHandler().getErrors().size() > 0;
boolean refresh = false;
Vector vettIndisp = null;
Vector vettMov = null;
SourceBean sbError = (SourceBean) serviceResponse.getAttribute("M_Mob_EffettuaScorrimento.RECORD.PROCESSOR.ERROR");
String forzaRicostruzione = "false";
String continuaRicalcolo = "false";
vettIndisp = serviceResponse.getAttributeAsVector("M_Mob_IndispScorrimento.ROWS.ROW");
vettMov = serviceResponse.getAttributeAsVector("M_Mob_EventiScorrimento.ROWS.ROW");
boolean permettiImpatti = true;
ProfileDataFilter filter = new ProfileDataFilter(user, "AMMINISTRLISTESPECPAGE");
filter.setCdnLavoratore(new BigDecimal(cdnLav));
permettiImpatti = filter.canEditLavoratore();
SourceBean valoriParametri = null;
String strErrorCode = "";
boolean confirm = false;
Vector rowsParam = null;
String msgConferma = "";
String str_conf_scorr_agricoltura = serviceResponse.containsAttribute("M_GetConfigValue.ROWS.ROW.NUM")?serviceResponse.getAttribute("M_GetConfigValue.ROWS.ROW.NUM").toString():"0";
if (sbError != null) {
	strErrorCode = sbError.getAttribute("code").toString();
	msgConferma = sbError.getAttribute("messagecode").toString();
	msgConferma = msgConferma + " Vuoi proseguire?";
	rowsParam = serviceResponse.getAttributeAsVector("M_Mob_EffettuaScorrimento.RECORD.PROCESSOR.CONFIRM.PARAM");
	confirm = true;
	if (rowsParam != null && rowsParam.size() > 0) {
		valoriParametri = (SourceBean) rowsParam.get(0);
    	forzaRicostruzione = valoriParametri.getAttribute("value").toString();
    	valoriParametri = (SourceBean) rowsParam.get(1);
    	continuaRicalcolo = valoriParametri.getAttribute("value").toString();
   	}
}

if (eseguitoCalcolo && !errorOccurred && (sbError == null)) {
	refresh = true;
}
%>
<html>
<head>
<title>
</title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 <af:linkScript path="../../js/" />
 
 <script language="JavaScript">
 	var refresh = <%=refresh%>;
 	
 	function refreshOpener() {
 		if(refresh) {
 			var _cdnfunz = window.opener.document.Frm1.CDNFUNZIONE.value;
 			var queryString = "&PAGE=AmministrListeSpecPage&CDNFUNZIONE=" + _cdnfunz + "&CDNLAVORATORE=<%=cdnLav%>";
  			window.opener.location = "AdapterHTTP?" + queryString;
 		}
 	}
 	
 	function ripetiOperazione() {
		document.Frm1.submit();
	}
 </script>
 
 </head>
<body onLoad="refreshOpener()">
<font color="red"><af:showErrors/> </font>
<font color="green">
 <af:showMessages prefix="M_Mob_EffettuaScorrimento"/>
</font>
<%
if (vettMov.size() > 0) {%>
	<p><af:list moduleName="M_Mob_EventiScorrimento" skipNavigationButton="1"/></p>
	<%if (str_conf_scorr_agricoltura.equals("0")) {%>
		<p><span class="etichetta">&nbsp;&nbsp; NB: nel caso dei movimenti in agricoltura si ricorda che lo scorrimento viene calcolato sulla base dei giorni effettivi.</span></p>
		<br>
	<%}
}
if (vettIndisp.size() > 0) {%>
	<p><af:list moduleName="M_Mob_IndispScorrimento" skipNavigationButton="1"/></p>
<%}
if (vettMov.size() == 0 && vettIndisp.size() == 0) {%>
	<center>
	<table class="lista" align="center">
    <tr><td align="center"><b>Non ci sono eventi che possono implicare lo scorrimento della mobilit&agrave;</b></td></tr>
    </table>
    </center>
	<br><br><br>
	<center>
	<table>
	<tr><td><input type="button" class="pulsante" name="chiudi" value="Chiudi" onClick="javascript:window.close();"/></td></tr>
	</table>
	</center>
<%}	
else {%>
	<af:form name="Frm1" method="POST" action="AdapterHTTP">
	<input type="hidden" name="PAGE" value="MobEventiScorrimentoPage"/>
	<input type="hidden" name="cdnLavoratore" value="<%=cdnLav%>"/>
	<input type="hidden" name="datInizioMob" value="<%=datInizioMob%>"/>
	<input type="hidden" name="FORZA_INSERIMENTO" value="<%=forzaRicostruzione%>">
	<input type="hidden" name="CONTINUA_CALCOLO_SOCC" value="<%=continuaRicalcolo%>">
	<br>
	<center>
	<table>
	<tr><td>
	<%if (permettiImpatti) {%>
		<input type="hidden" name="EseguiCalcolo" value="">
		<input class="pulsante" type="submit" name="btnEseguiScorrimento" value="Calcola">&nbsp;&nbsp;
	<%}%>	
	<input type="button" class="pulsante" name="chiudi" value="Chiudi" onClick="javascript:window.close();"/>
	</td></tr>
	</table>
	</center>
	</af:form>
	<%if (confirm) {%>
	<script language="Javascript">
	if (confirm("<%=msgConferma%>")) { 
		ripetiOperazione();
	}
	else {
		document.Frm1.FORZA_INSERIMENTO.value = "false";
		document.Frm1.CONTINUA_CALCOLO_SOCC.value = "false";
	}
	</script>
	<%}
}%>
</body>
</html>