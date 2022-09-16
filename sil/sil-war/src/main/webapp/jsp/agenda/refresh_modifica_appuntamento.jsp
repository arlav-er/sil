<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
    com.engiweb.framework.base.*,
    com.engiweb.framework.configuration.ConfigSingleton,
    
    com.engiweb.framework.error.EMFErrorHandler,
    com.engiweb.framework.util.JavaScript,
    it.eng.afExt.utils.*,
    it.eng.sil.util.*,
    it.eng.sil.security.*,
    java.lang.*,
    java.text.*,
    java.math.*,
    java.sql.*,
    oracle.sql.*,
    java.util.*"%>

<%@ taglib uri="aftags" prefix="af"%>

<%  
    String CODCPI=(String) serviceRequest.getAttribute("CODCPI");
    String PRGAPPUNTAMENTO=(String) serviceRequest.getAttribute("PRGAPPUNTAMENTO");
    String OLDSTATO = (String) serviceRequest.getAttribute("OLDSTATO");
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
    String CODSERVIZIO = StringUtils.getAttributeStrNotNull(serviceRequest,"CODSERVIZIO");
    String prgSpi = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGSPI");
    String prgSpiEff = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGSPIEFF");
     
    String linkDett = "&MOD=" + mod + "&cod_vista=" + cod_vista + "&DATA_COD=" + data_cod;
    linkDett += "&PRGSPI=" +  prgSpi;
    linkDett += "&PRGSPIEFF=" +  prgSpiEff;
    linkDett += "&CODSERVIZIO=" +  CODSERVIZIO;
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

    String PROV =  StringUtils.getAttributeStrNotNull(serviceRequest, "PROV");
    int tipo = 0;
   
    String strNomeLav = "";
    String strCognomeLav = "";
    String strCFLav = "";
    String comuneNasc = "";
    String dataNasc = "";
    String comuneDom = "";
    String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "CDNLAVORATORE");
    if(PROV.equals("CONTATTI")) { 
      tipo = 2; 
      SourceBean content = (SourceBean) serviceResponse.getAttribute("MSEL_AG_LAVORATORE");
      SourceBean row = (SourceBean) content.getAttribute("ROWS.ROW");
      if(row!=null) {
        strNomeLav = StringUtils.getAttributeStrNotNull(row, "STRNOME");
        strCognomeLav = StringUtils.getAttributeStrNotNull(row, "STRCOGNOME");
        strCFLav = StringUtils.getAttributeStrNotNull(row, "STRCODICEFISCALE");
        comuneNasc = StringUtils.getAttributeStrNotNull(row, "COMUNE_NASC");
        dataNasc = StringUtils.getAttributeStrNotNull(row, "DATNASC");
        comuneDom = StringUtils.getAttributeStrNotNull(row, "COMUNE_DOM");
      }
    } else { tipo = 1; }
    
%>

<%
// POPUP EVIDENZE
String strApriEv = StringUtils.getAttributeStrNotNull(serviceRequest, "APRI_EV");
int _fun = 1;
String cdnFunzione = StringUtils.getAttributeStrNotNull(serviceRequest, "CDNFUNZIONE");
if((cdnFunzione!=null) && !cdnFunzione.equals("")) { _fun = Integer.parseInt(cdnFunzione); }
EvidenzePopUp jsEvid = null;
String _page = StringUtils.getAttributeStrNotNull(serviceRequest, "PAGE");
PageAttribs attributi = new PageAttribs(user, _page);
boolean bevid = attributi.containsButton("EVIDENZE");
if(strApriEv.equals("1") && bevid) {
	jsEvid = new EvidenzePopUp(cdnLavoratore, _fun, user.getCdnGruppo(), user.getCdnProfilo());
}	
%> 

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<HEAD>
  <af:linkScript path="../../js/" />
  
  <%if(strApriEv.equals("1") && bevid) { jsEvid.show(out); }%>
  
  <script type="text/javascript">
    function aggiorna(n,codcpi,prgappuntamento, linkDett){
      var txt;
      var url;
      if(n==1) {
        //url="AdapterHTTP?PAGE=SELECT_AGENDA_PAGE&CODCPI=" + codcpi + "&PRGAPPUNTAMENTO=" + prgappuntamento + "&ALLINEA_SLOT=1" + linkDett;
        url="AdapterHTTP?PAGE=SELECT_AGENDA_PAGE&CODCPI=" + codcpi;
        url += "&PRGAPPUNTAMENTO=" + prgappuntamento; 
        url += "&ALLINEA_SLOT=1" + linkDett;
        url += "&OLDSTATO=" + "<%=OLDSTATO%>";
        setOpenerWindowLocation(url);
      } else {
        window.opener.document.frmNuovoContatto.PRGAZIENDA.value = "";
        window.opener.document.frmNuovoContatto.PRGUNITA.value = "";
        window.opener.document.frmNuovoContatto.CDNLAVORATORE.value = "<%=cdnLavoratore%>";
        window.opener.document.frmNuovoContatto.TContatto.src = "../../img/omino.gif";
        txt = "<%=JavaScript.escapeText(strCognomeLav)%>"+ " " + "<%=JavaScript.escapeText(strNomeLav)%>\n";
        txt += "<%=JavaScript.escapeText(strCFLav)%>\n";
        txt += "<%=JavaScript.escapeText(comuneNasc)%>" + " " + "<%=dataNasc%>\n";
        txt += "<%=JavaScript.escapeText(comuneDom)%>";
        window.opener.document.frmNuovoContatto.TXTDATICONTATTO.value = txt;
      }
      window.close();
    }
  </script>
</head>

<body class="gestione" 
onload="aggiorna(<%=tipo%>,'<%=CODCPI%>','<%=PRGAPPUNTAMENTO%>','<%=linkDett%>');<%if(strApriEv.equals("1") && bevid) { %> apriEvidenze() <%}%>"
>

<af:error/>
<p align="center">
  <font color="green"><af:showMessages /></font>
  <font color="red"><af:showErrors /></font>
</p>

</body>
</html>