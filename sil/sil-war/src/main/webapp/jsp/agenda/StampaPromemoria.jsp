<!-- @author: Giovanni Landi -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.afExt.utils.MessageCodes,
                  it.eng.sil.security.User,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
String queryString = null;
String strPrgAppuntamento = serviceRequest.containsAttribute("PRGAPPUNTAMENTO")? serviceRequest.getAttribute("PRGAPPUNTAMENTO").toString():"";
String strPrgAzienda = serviceRequest.containsAttribute("PRGAZIENDA")? serviceRequest.getAttribute("PRGAZIENDA").toString():"";
String strPrgUnita = serviceRequest.containsAttribute("PRGUNITA")? serviceRequest.getAttribute("PRGUNITA").toString():"";
String strCdnLavoratore = serviceRequest.containsAttribute("CDNLAVORATORE")? serviceRequest.getAttribute("CDNLAVORATORE").toString():"";

String codCpi = serviceRequest.containsAttribute("CODCPI")? serviceRequest.getAttribute("CODCPI").toString():"";

int cdnUt = user.getCodut();
int cdnTipoGruppo = user.getCdnTipoGruppo();
String tipoGruppo = user.getCodTipo();
//String codCpi="";
if(codCpi.equals("")){
	if(cdnTipoGruppo == 1 || tipoGruppo.equalsIgnoreCase(MessageCodes.Login.COD_TIPO_GRUPPO_PATRONATO)) {
	  codCpi =  user.getCodRif();
	}
}

//per il layout grafico
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>
<head>
<title>Stampa Promemoria</title>
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

function EffettuaStampaPromemoria () {
  var visOperatore = 1;
  if (document.frmStampa.visualizzaOperatore.checked) {
    visOperatore = 0;
  }
  apriGestioneDoc('RPT_PROMEMORIA_APP','&operatore=' + visOperatore + '&prgAppuntamento=<%=strPrgAppuntamento%>&codcpi=<%=codCpi%>&cdnLavoratore=<%=strCdnLavoratore%>&prgAzienda=<%=strPrgAzienda%>&prgUnita=<%=strPrgUnita%>&','PRM');
}

function EffettuaStampaConvocazione () {
  if (document.frmStampa.MODELLOSTAMPA.value == '') {
    alert ("Scegliere modello di stampa");
  }
  else {
    apriGestioneDoc('RPT_AGENDA_STAMPA_GIOR','&prgmodellostampa=' + document.frmStampa.MODELLOSTAMPA.value + '&prgAppuntamento=<%=strPrgAppuntamento%>&codcpi=<%=codCpi%>&cdnLavoratore=<%=strCdnLavoratore%>&prgAzienda=<%=strPrgAzienda%>&prgUnita=<%=strPrgUnita%>&','LTC');
  }
}

</script>
  
</head>
<body class="gestione">
<af:form name="frmStampa" action="AdapterHTTP" method="POST">
<input type="hidden" name="PAGE" value="">
<input type="hidden" name="prgappuntamento" value="<%=strPrgAppuntamento%>">
<input type="hidden" name="cdnlavoratore" value="<%=strCdnLavoratore%>">
<input type="hidden" name="prgazienda" value="<%=strPrgAzienda%>">
<input type="hidden" name="prgunita" value="<%=strPrgUnita%>">
<br>
<%out.print(htmlStreamTop);%>
<table id="sezione_promemoria" width="100%" border="0">
<tr><td width="100%">
<font class="titoloSezioni">Promemoria</font>
</td></tr>
<tr><td>
<font class="titoloSezioni"><hr></font>
</td></tr>
<tr><td>

<table>
<tr valign="top"><td class="etichetta2">
Esplicita riferimento operatore &nbsp;<input type="checkbox" name="visualizzaOperatore" checked>
</td>
<td class="etichetta2">
<input class="pulsante" type="button" name="btnStampaProm" value="Stampa" onclick="javascript:EffettuaStampaPromemoria();"/>
</td>
</tr>
</table>

</td></tr>
</table>
<%
if (!strCdnLavoratore.equals("")) {
%>
  <br>
  <table id="sezione_convocazione" width="100%" border="0">
  <tr><td width="100%">
  <font class="titoloSezioni">Convocazione</font>
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
                                    focusOn="false" moduleName="ComboStModelloStampa"
                                    addBlank="true" blankValue=""/>
  </td>
  <td class="etichetta2">
  <input class="pulsante" type="button" name="btnStampaConv" value="Stampa" onclick="javascript:EffettuaStampaConvocazione();"/>
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