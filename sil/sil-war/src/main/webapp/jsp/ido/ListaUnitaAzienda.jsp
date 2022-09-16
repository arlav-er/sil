<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                 java.lang.*,
                 java.text.*,
                 java.util.*, 
                 it.eng.afExt.utils.StringUtils,
                 it.eng.sil.security.*"%>
                 
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<% 

  String prgAzienda=(String) serviceRequest.getAttribute("prgAzienda");
  String pagina_back=StringUtils.getAttributeStrNotNull(serviceRequest,"ret");

  SourceBean aziendaRow= (SourceBean) serviceResponse.getAttribute("M_GetTestataAzienda.ROWS.ROW");

  String strCodiceFiscale= StringUtils.getAttributeStrNotNull(aziendaRow, "strCodiceFiscale");
  String strPartitaIva=StringUtils.getAttributeStrNotNull(aziendaRow, "strPartitaIva");
  String strRagioneSociale=StringUtils.getAttributeStrNotNull(aziendaRow, "strRagioneSociale");

  PageAttribs attributi = new PageAttribs(user, "IdoListaUnitaAziendaPage");
  boolean canInsert = attributi.containsButton("INSERISCI");

  int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
%>
<html>
<head>
<title>Lista Unità Aziende</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>
<SCRIPT language="Javascript" type="text/javascript">
<!--
function go(url, alertFlag) {
// Se la pagina è già in submit, ignoro questo nuovo invio!
if (isInSubmit()) return;

var _url = "AdapterHTTP?" + url;
if (alertFlag == 'TRUE' ) {
if (confirm('Confermi operazione'))
setWindowLocation(_url);
}
else
setWindowLocation(_url);
}
// -->

</SCRIPT>
<script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
</script>

</head>
<p class="titolo">LISTA UNITA' AZIENDE</p>
<br/><br/><br/>
<body class="gestione">

<af:form dontValidate="true">

<TABLE class="lista" align="center">
<tr>
  <td colspan="6" class="campo">
      Azienda: <b><%=strRagioneSociale%></b><br/>
      Partita Iva: <b><%=strPartitaIva%></b><br/>
      Codice Fiscale: <b><%=strCodiceFiscale%></b><br/>
      <br/>
  </td>
</tr>
</table>

<af:list moduleName="M_RicercaUnitaAzienda" configProviderClass="it.eng.sil.module.ido.DynamicRicUnitaAziendaConfig" />

</af:form>

</body>
</html>
