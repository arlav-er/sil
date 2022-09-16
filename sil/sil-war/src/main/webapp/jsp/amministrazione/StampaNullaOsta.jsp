<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  it.eng.afExt.utils.*,
                  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
String queryString = null;
String strPrgNullaOsta = serviceRequest.containsAttribute("PRGNULLAOSTA")? serviceRequest.getAttribute("PRGNULLAOSTA").toString():"";
String strPrgAzienda = serviceRequest.containsAttribute("PRGAZIENDA")? serviceRequest.getAttribute("PRGAZIENDA").toString():"";
String strPrgUnita = serviceRequest.containsAttribute("PRGUNITA")? serviceRequest.getAttribute("PRGUNITA").toString():"";
String strCdnLavoratore = serviceRequest.containsAttribute("CDNLAVORATORE")? serviceRequest.getAttribute("CDNLAVORATORE").toString():"";
String cdnLavoratoreEncrypt = serviceRequest.containsAttribute("cdnLavoratoreEncrypt")? serviceRequest.getAttribute("cdnLavoratoreEncrypt").toString():"";
BigDecimal numProt = SourceBeanUtils.getAttrBigDecimal(serviceRequest, "numProtocollo", null);

BigDecimal annoProt = it.eng.afExt.utils.SourceBeanUtils.getAttrBigDecimal(serviceRequest, "numAnnoProt", null); 
String dataOraProt = StringUtils.getAttributeStrNotNull(serviceRequest,"dataOraProt");


//per il layout grafico
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>
<head>
<title>Stampa Nulla Osta</title>
<style type="text/css"> 
  .titoloSezioni {
    text-decoration: none;
    border: 0px;
    font-family: Verdana, Arial, Helvetica, Sans-serif; 
    font-size: 11px;
    font-weight: bold;
    color: #000066;
    text-align: center;
}
</style>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
<af:linkScript path="../../js/"/>
<!-- include lo script che permette di aprire la PopUp che gestisce i documenti (salvataggio/protocollazione) -->
<%@ include file="../documenti/_apriGestioneDoc.inc"%>

<script language="javascript">

function EffettuaStampaNullaOsta () {
   var prgmodellostampa = document.frmStampa.MODELLOSTAMPA.value;
  if (document.frmStampa.MODELLOSTAMPA.value == '') {
    alert ("Scegliere modello di stampa");
  }
  else {
  	<% if(numProt != null && annoProt != null){%>
		apriGestioneDoc('RPT_STAMPA_NULLAOSTA','&prgmodellostampa=' + document.frmStampa.MODELLOSTAMPA.value +  '&cdnLavoratoreEncrypt=<%=cdnLavoratoreEncrypt%>&prgNullaOsta=<%=strPrgNullaOsta%>&numProt=<%=numProt%>&annoProt=<%=annoProt%>&dataOraProt=<%=dataOraProt%>','NULOST');
  	<%} else { %>
  		apriGestioneDoc('RPT_STAMPA_NULLAOSTA','&prgmodellostampa=' + document.frmStampa.MODELLOSTAMPA.value + '&cdnLavoratoreEncrypt=<%=cdnLavoratoreEncrypt%>&prgNullaOsta=<%=strPrgNullaOsta%>','NULOST');
  	<%}%>
  }
}

</script>
  
</head>
<body class="gestione">
<af:form name="frmStampa" action="AdapterHTTP" method="POST">
<input type="hidden" name="PAGE" value="">
<input type="hidden" name="prgNullaOsta" value="<%=strPrgNullaOsta%>">
<input type="hidden" name="cdnlavoratore" value="<%=strCdnLavoratore%>">
<input type="hidden" name="prgazienda" value="<%=strPrgAzienda%>">
<input type="hidden" name="prgunita" value="<%=strPrgUnita%>">
<br>
<%out.print(htmlStreamTop);%>
<table id="sezione_promemoria" width="100%" border="0">
</table>
<%
if (!strCdnLavoratore.equals("")) {
%>
  <br>
  <table id="sezione_convocazione" width="100%" border="0">
  <tr><td width="100%">
  <font class="titoloSezioni">Nulla Osta</font>
  </td></tr>
  <tr><td>
  <font class="titoloSezioni"><hr></font>
  </td></tr>
  <tr><td>
            
  <table>
  <tr valign="top">
  <td class="etichetta2">
  Modello stampa &nbsp;<af:comboBox name="MODELLOSTAMPA" size="1" title="Modello stampa"
                                    multiple="false" disabled="false" required="true"
                                    focusOn="false" moduleName="M_ST_MODELLO_NULLAOSTA"
                                    addBlank="true" blankValue=""/>
  </td>
  <td class="etichetta2">
  <input class="pulsante" type="button" name="btnStampaConv" value="Stampa" onclick="javascript:EffettuaStampaNullaOsta();"/>
  </td>
  </tr>
  </table>

  </td></tr>
  </table>
<%
}
%>
</af:form>
<%out.print(htmlStreamBottom);%>
</body>
</html>