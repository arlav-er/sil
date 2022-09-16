<!-- @author: Stefania Orioli -->
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
    com.engiweb.framework.base.*,
    com.engiweb.framework.configuration.ConfigSingleton,
    
    com.engiweb.framework.error.EMFErrorHandler,
    com.engiweb.framework.util.JavaScript,
    it.eng.afExt.utils.*,
    it.eng.sil.security.User,
    it.eng.sil.util.*,
    java.lang.*,
    java.text.*,
    java.math.*,
    java.sql.*,
    oracle.sql.*,
    java.util.*"%>
    
<%@ taglib uri="aftags" prefix="af"%>
<%
    String PROV =  serviceRequest.getAttribute("PROV").toString();
    String prg = "";
    
    String prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGAZIENDA");
    String prgUnita = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGUNITA");
    String strRagioneSociale = StringUtils.getAttributeStrNotNull(serviceRequest, "STRRAGIONESOCIALE");
    String strPartitaIva = StringUtils.getAttributeStrNotNull(serviceRequest, "STRPARTITAIVA");
    String strIndirizzo = StringUtils.getAttributeStrNotNull(serviceRequest, "STRINDIRIZZO");
    if(strIndirizzo.equals("STRINDIRIZZO")) { strIndirizzo = ""; }
    String comuneAz = StringUtils.getAttributeStrNotNull(serviceRequest, "COMUNE_AZ");
    if(comuneAz.equals("COMUNE_AZ")) { comuneAz = ""; }
    String strTel = StringUtils.getAttributeStrNotNull(serviceRequest, "STRTEL");
    if(strTel.equals("STRTEL")) { strTel = ""; }
    
    int tipo = 0;
    if(PROV.equals("CONTATTI")) { tipo = 2; }
    else { tipo = 1; }


%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<HEAD>
  <af:linkScript path="../../js/" />
  <script type="text/javascript">
    //function aggiorna(prgAzienda, prgUnita, strRagioneSociale, strPIva, strInd, com, tel){
    function aggiorna(n) {
      var txt;
      if(n==1) {
            window.opener.document.frmNuovoAppuntamento.PRGAZIENDA.value = <%=prgAzienda%>;
            window.opener.document.frmNuovoAppuntamento.PRGUNITA.value = <%=prgUnita%>;
            window.opener.document.frmNuovoAppuntamento.Ragione.value = "<%=JavaScript.escapeText(strRagioneSociale)%>";
            window.opener.document.frmNuovoAppuntamento.Indirizzo.value = "<%=JavaScript.escapeText(strIndirizzo)%>";
            window.opener.document.frmNuovoAppuntamento.Comune.value = "<%=JavaScript.escapeText(comuneAz)%>";
            window.opener.document.frmNuovoAppuntamento.PartIva.value = "<%=JavaScript.escapeText(strPartitaIva)%>";
            window.opener.document.frmNuovoAppuntamento.strTel.value = "<%=JavaScript.escapeText(strTel)%>";
      } else {
            window.opener.document.frmNuovoContatto.PRGAZIENDA.value = <%=prgAzienda%>;
            window.opener.document.frmNuovoContatto.PRGUNITA.value = <%=prgUnita%>;
            window.opener.document.frmNuovoContatto.CDNLAVORATORE.value = "";
            window.opener.document.frmNuovoContatto.TContatto.src = "../../img/azienda.gif";
            txt = "<%=JavaScript.escapeText(strRagioneSociale)%>\n" + "<%=JavaScript.escapeText(strIndirizzo)%>\n";
            txt += "<%=JavaScript.escapeText(comuneAz)%>\n" + "<%=JavaScript.escapeText(strPartitaIva)%>\n";
            txt += "<%=JavaScript.escapeText(strTel)%>\n";
            window.opener.document.frmNuovoContatto.TXTDATICONTATTO.value = txt;
      }
      window.close();
    }
  </script>
</head>
<!--onload="aggiorna('<%=prgAzienda%>','<%=prgUnita%>','<%=strRagioneSociale%>','<%=strPartitaIva%>','<%=strIndirizzo%>','<%=comuneAz%>','<%=strTel%>');">-->
<body class="gestione" onload="aggiorna(<%=tipo%>)">
<!--body class="gestione"-->
<!-- DEBUG -->
<%//qs%>
<!--input type="button" value= "OK" class="pulsanti" onClick="aggiorna('<%=prgAzienda%>','<%=prgUnita%>','<%=strRagioneSociale%>','<%=strPartitaIva%>','<%=strIndirizzo%>','<%=comuneAz%>','<%=strTel%>');"-->

</body>
</html>