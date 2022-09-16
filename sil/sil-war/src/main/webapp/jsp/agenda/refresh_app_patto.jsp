<!-- @author: Stefania Orioli - Jan 2004 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,java.math.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.User,
                it.eng.sil.util.*, it.eng.sil.module.agenda.ShowApp,
                it.eng.afExt.utils.*"
%>

<%@ taglib uri="aftags" prefix="af"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%  
    String CODCPI=(String) serviceRequest.getAttribute("CODCPI");
    String PRGAPPUNTAMENTO=(String) serviceRequest.getAttribute("PRGAPPUNTAMENTO");  
    String data_cod = StringUtils.getAttributeStrNotNull(serviceRequest,"DATA_COD");  
%>

<%
    String cod_vista = StringUtils.getAttributeStrNotNull(serviceRequest,"cod_vista");
    String mod = StringUtils.getAttributeStrNotNull(serviceRequest,"MOD");
    if(mod.equals("")) { mod = "0"; }
    
    String giorno = StringUtils.getAttributeStrNotNull(serviceRequest,"giorno");
    String mese = StringUtils.getAttributeStrNotNull(serviceRequest,"mese");
    String anno = StringUtils.getAttributeStrNotNull(serviceRequest,"anno");
    String giornoDB = StringUtils.getAttributeStrNotNull(serviceRequest,"giornoDB");
    String meseDB = StringUtils.getAttributeStrNotNull(serviceRequest,"meseDB");
    String annoDB = StringUtils.getAttributeStrNotNull(serviceRequest,"annoDB");
    
    
    String sel_operatore = StringUtils.getAttributeStrNotNull(serviceRequest,"sel_operatore");
    String sel_servizio = StringUtils.getAttributeStrNotNull(serviceRequest,"sel_servizio");
    String sel_aula = StringUtils.getAttributeStrNotNull(serviceRequest,"sel_aula");
    String esitoApp = StringUtils.getAttributeStrNotNull(serviceRequest,"esitoApp");
    String strRagSoc = StringUtils.getAttributeStrNotNull(serviceRequest,"strRagSoc");
    String strCodiceFiscale = StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscale");
    String strCognome = StringUtils.getAttributeStrNotNull(serviceRequest,"strCognome");
    String strNome = StringUtils.getAttributeStrNotNull(serviceRequest,"strNome");
    String dataDal = StringUtils.getAttributeStrNotNull(serviceRequest, "dataDal");
    String dataAl = StringUtils.getAttributeStrNotNull(serviceRequest, "dataAl");
    String piva = StringUtils.getAttributeStrNotNull(serviceRequest, "piva");
	String strCodiceFiscaleAz= StringUtils.getAttributeStrNotNull(serviceRequest, "strCodiceFiscaleAz");
	
    String linkDett = "&MOD=" + mod + "&cod_vista=" + cod_vista + "&DATA_COD=" + data_cod;
    if(mod.equals("0")) {
      linkDett += "&giornoDB=" + giornoDB + "&meseDB=" + meseDB + "&annoDB=" + annoDB;
      linkDett += "&giorno=" + giorno + "&mese=" + mese + "&anno=" + anno;
    }
    if(mod.equals("2")) {
      linkDett += "&sel_operatore=" + sel_operatore;
      linkDett += "&sel_servizio=" + sel_servizio;
      linkDett += "&sel_aula=" + sel_aula;
      linkDett += "&esitoApp=" + esitoApp;     
      linkDett += "&strRagSoc=" + strRagSoc;
      linkDett += "&strCodiceFiscale=" + strCodiceFiscale;
      linkDett += "&strCognome=" + strCognome;
      linkDett += "&strNome=" + strNome;
      linkDett += "&dataDal=" + dataDal;
      linkDett += "&dataAl=" + dataAl;
      linkDett += "&mese=" + mese;
      linkDett += "&anno=" + anno;
      linkDett += "&piva=" + piva;
      linkDett += "&strCodiceFiscaleAz=" + strCodiceFiscaleAz;      
    }
 
%>



<html>
<HEAD>
  <af:linkScript path="../../js/" />
  <script type="text/javascript">
    function aggiorna(codcpi,prgappuntamento, linkDett)
    {
      var url = "AdapterHTTP?PAGE=SELECT_AGENDA_PAGE&CODCPI=" + codcpi + "&PRGAPPUNTAMENTO=" + prgappuntamento + linkDett;
      setOpenerWindowLocation(url);
      window.close();
    }
  </script>
</head>

<body class="gestione" onload="aggiorna('<%=CODCPI%>','<%=PRGAPPUNTAMENTO%>','<%=linkDett%>');">

<af:error/>
<p align="center">
  <font color="green"><af:showMessages /></font>
  <font color="red"><af:showErrors /></font>
</p>

</body>
</html>