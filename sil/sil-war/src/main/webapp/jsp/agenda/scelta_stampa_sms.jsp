<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ taglib uri="cal" prefix="cal" %>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
    com.engiweb.framework.base.*,
    com.engiweb.framework.configuration.ConfigSingleton,
    
    com.engiweb.framework.error.EMFErrorHandler,
    it.eng.afExt.utils.DateUtils,
    it.eng.sil.security.User,
    it.eng.afExt.utils.*,it.eng.sil.util.*,
    java.lang.*,
    java.text.*,
    java.math.*,
    java.sql.*,
    oracle.sql.*,
    java.util.*"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
String streamDescrCpi = "";
SourceBean content = (SourceBean) serviceResponse.getAttribute("MDESCRCPI");
if(content!=null) {
  SourceBean row = (SourceBean) content.getAttribute("ROWS.ROW");
  if(row!=null) { streamDescrCpi = (String) row.getAttribute("STRDESCRIZIONE"); }
}
String codCpi = (String) serviceRequest.getAttribute("CODCPI");
String giorno = (String) serviceRequest.getAttribute("giorno");
if (giorno.length() == 1) { 
  giorno = "0" + giorno;
}
String mese = (String) serviceRequest.getAttribute("mese");
if (mese.length() == 1) { 
  mese = "0" + mese;
}
String anno = (String) serviceRequest.getAttribute("anno");
String dataDefault = giorno + "/" + mese + "/" + anno;

String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);

String queryString = null;

String labelServizio = "Servizio";
String umbriaGestAz = "0";
if(serviceResponse.containsAttribute("M_CONFIG_UMB_NGE_AZ")){
	umbriaGestAz = Utils.notNull(serviceResponse.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM"));
}
if(umbriaGestAz.equalsIgnoreCase("1")){
	labelServizio = "Area";
}
%>
<html>
<head>
  
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <!-- include lo script che permette di aprire la PopUp che gestisce i documenti (salvataggio/protocollazione) -->
  <%@ include file="../documenti/_apriGestioneDoc.inc"%>
  <%@ include file="../global/checkFormatData.inc"%>
  <script language="Javascript">
  
  function avviaStampa() { 
    if (document.Frm1.DATARICERCA.value == "") {
      alert ("Il campo data è obbligatorio");
    }
    else {    
      if (checkAndFormatDate(document.Frm1.DATARICERCA)) {
        var servizio = document.Frm1.CODSERVIZIO.value;
        var dataRicerca = document.Frm1.DATARICERCA.value;
        var codCpi = document.Frm1.CODCPI.value;
        apriGestioneDoc('RPT_AGENDA_APP_SMS','&CODSERVIZIO='+servizio + '&DATARICERCA='+dataRicerca + '&CODCPI='+codCpi,'RAP');
      }
    }
  }
  
  </script>
</head>

<body class="gestione">
<br/><br/>
<p class="titolo">Stampa appuntamenti per invio SMS</p>
<p>
<%out.print(htmlStreamTop);%>
<af:form name="Frm1" action="AdapterHTTP" method="POST">
<input type="hidden" name="CODCPI" value="<%=codCpi%>">

<table class="main" align="center">
<tr>
  <td class="etichetta">Data</td>
  <td class="campo">
    <af:textBox type="date" name="DATARICERCA" title="Data" value="<%=dataDefault%>" size="12" maxlength="10" validateOnPost="true"/>&nbsp;*
  </td>
</tr>

<tr>
  <td class="etichetta"><%=labelServizio %></td>
  <td class="campo">
      <af:comboBox name="CODSERVIZIO" size="1" title="<%=labelServizio %>"
                     multiple="false" disabled="false" required="false"
                     focusOn="false" moduleName="COMBO_SERVIZIO"
                     selectedValue="" addBlank="true" blankValue=""/>
  </td>
</tr>
</table>
<br>
<table align="center">
<tr>
  <td align="center"><input class="pulsante" type="button" name="btnChiudi" value="Annulla" onClick="javascript:window.close();"/></td>
	<td align="center"><input class="pulsante" type="button" name="btnInvio" value="Stampa" onClick="avviaStampa()"/></td>
</tr>
</table>

</af:form>
<%out.print(htmlStreamBottom);%>
</body>
</html>