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

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af" %>

<%
String cdnLavoratore = (String) serviceRequest.getAttribute("CDNLAVORATORE");
String codCpi = (String) serviceRequest.getAttribute("CODCPI");
String prgAppuntamento = (String) serviceRequest.getAttribute("PRGAPPUNTAMENTO");
String prgLavPattoScelta = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGLAVPATTOSCELTA");
%>

<%
// Dati Lavoratore
String moduleName = "MAGDATILAV";
SourceBean content = null;
content = (SourceBean) serviceResponse.getAttribute(moduleName);
SourceBean row = (SourceBean) content.getAttribute("ROW");
String nomeLav = StringUtils.getAttributeStrNotNull(row, "STRNOME");
String cognomeLav = StringUtils.getAttributeStrNotNull(row, "STRCOGNOME");

// Patti
moduleName = "MAGPATTILAV";
content = (SourceBean) serviceResponse.getAttribute(moduleName);
Vector rows = content.getAttributeAsVector("ROWS.ROW");
int i;

// Parametri di Navigazione nel Calendario
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

String data_cod = StringUtils.getAttributeStrNotNull(serviceRequest,"DATA_COD");
%>

<html>
<head>
  <title>Elenco Patti</title>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/" />
  <script type="text/javascript">
    function insert_patto(prgPatto, dataStipula, dataScadenzaConferma)
    {
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;

      dataAppuntamento = opener.document.frmNuovoAppuntamento.data_app.value;      
      if (compDate(dataAppuntamento, dataStipula)<0){
      	alert("associazione al patto/accordo non possibile: l'appuntamento non rientra nel range di validità del patto/accordo");
      	return false;
      }      
      document.frm.PRG_PATTO_LAVORATORE.value = prgPatto;
      doFormSubmit(document.frm);
    }
    function compDate(date1,date2) {
	    if (date1==null || date1=="") return -1;    
	    if (date2==null || date2=="") return 1;
	    if ( toDate(date1).getTime()<toDate(date2).getTime()) return -1;
	    if ( toDate(date1).getTime()>toDate(date2).getTime()) return 1;	   
	    return 0;
	}
	function toDate(newDate) {
	    var tokens = newDate.split('/');
	    var usDate= tokens[2]+"/"+tokens[1]+"/"+tokens[0];
	    return new Date(usDate);
	}
  </script>
</head>
<body class="gestione">
<af:form name="frm" action="AdapterHTTP" method="POST" dontValidate="true">
<input type="hidden" name="PAGE" value="AgInsertPattoPage">
<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>">
<input type="hidden" name="CODCPI" value="<%=codCpi%>">
<input type="hidden" name="PRGAPPUNTAMENTO" value="<%=prgAppuntamento%>">
<input type="hidden" name="PRGLAVPATTOSCELTA" value="<%=prgLavPattoScelta%>">
<input type="hidden" name="PRG_PATTO_LAVORATORE" value="">
<input type="hidden" name="PRG_AG_LAV" value="<%=(cdnLavoratore+','+codCpi+','+prgAppuntamento)%>">

<input name="MOD" type="hidden" value="<%=mod%>"/>
<input name="cod_vista" type="hidden" value="<%=cod_vista%>"/>
<input name="DATA_COD" type="hidden" value="<%=data_cod%>"/>
<%if(mod.equals("0")) {%>
    <input name="giornoDB" type="hidden" value="<%=giornoDB%>"/>
    <input name="meseDB" type="hidden" value="<%=meseDB%>"/>
    <input name="annoDB" type="hidden" value="<%=annoDB%>"/>
    <input name="giorno" type="hidden" value="<%=giorno%>"/>
    <input name="mese" type="hidden" value="<%=mese%>"/>
    <input name="anno" type="hidden" value="<%=anno%>"/>
<%} else {%>
  <%if(mod.equals("2")) {%>
        <input name="sel_operatore" type="hidden" value="<%=sel_operatore%>"/>
        <input name="sel_servizio" type="hidden" value="<%=sel_servizio%>"/>
        <input name="sel_aula" type="hidden" value="<%=sel_aula%>"/>
        <input name="esitoApp" type="hidden" value="<%=esitoApp%>"/>
        <input name="strRagSoc" type="hidden" value="<%=strRagSoc%>"/>
        <input name="strCodiceFiscale" type="hidden" value="<%=strCodiceFiscale%>"/>
        <input name="strCognome" type="hidden" value="<%=strCognome%>"/>
        <input name="strNome" type="hidden" value="<%=strNome%>"/>
        <input name="mese" type="hidden" value="<%=mese%>"/>
        <input name="anno" type="hidden" value="<%=anno%>"/>
        <input name="dataDal" type="hidden" value="<%=dataDal%>"/>
        <input name="dataAl" type="hidden" value="<%=dataAl%>"/>
        <input name="piva" type="hidden" value="<%=piva%>"/>
        <input name="strCodiceFiscaleAz" type="hidden" value="<%=strCodiceFiscaleAz%>"/>
  <%}%>
<%}%>
<%
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<br>
<h2>Elenco Patti <%=cognomeLav%>&nbsp;<%=nomeLav%></h2>
<%out.print(htmlStreamTop);%>
<table class="lista">
<!--tr>
    <td class="bordato" align="center" colspan="6">
    <b>Elenco Patti <%=cognomeLav%>&nbsp;<%=nomeLav%></b>
    </td>
</tr>
<tr><td colspan="6">&nbsp;</td></tr-->
<%
    if(rows.size()>0) {
%>
<tr>
  <th class="lista">&nbsp;</th>
  <th class="lista"><b>Data Stipula</b></th>
  <th class="lista"><b>Tipologia</b></th>
  <th class="lista"><b>Stato Patto</b></th>
  <th class="lista"><b>Motivo Fine Atto</b></th>
  <th class="lista"><b>Data Fine</b></th>
</tr>
<%}%>
<%
    if(rows.size()==0) {
%>
    <tr valign="middle">
      <td class="def" align="justify" colspan="6">
        <img src="../../img/info.gif" alt="!">&nbsp;
        <b>Attenzione non ci sono patti per questo lavoratore</b>
      </td>
    </tr>
<%
    }
    BigDecimal prgPattoLavoratore = null;
    String dataStipula = "";
    String patto297 = "";
    String tipoPatto = "";
    String motivo = "";
    String dataFine = "";
    String classRow = "";
    String dataScadenzaConferma = "";
    boolean pd = true;
    for(i=0; i < rows.size(); i++) {
      row = (SourceBean) rows.elementAt(i);
      if(pd==true) { pd = false; classRow = "lista_dispari"; }
      else { pd = true; classRow = "lista_pari"; }
      prgPattoLavoratore = (BigDecimal) row.getAttribute("PRGPATTOLAVORATORE");
      dataStipula = StringUtils.getAttributeStrNotNull(row, "DATA_STIPULA");
      if(dataStipula.equals("")) { dataStipula = "&nbsp;"; }
      patto297 = StringUtils.getAttributeStrNotNull(row, "PATTO297");
      if(patto297.equals("")) { patto297 = "&nbsp;"; }
      tipoPatto = StringUtils.getAttributeStrNotNull(row, "TIPO_PATTO");
      if(tipoPatto.equals("")) { tipoPatto = "&nbsp;"; }
      motivo = StringUtils.getAttributeStrNotNull(row, "MOT_FINE_ATTO");
      if(motivo.equals("")) { motivo = "&nbsp;"; }
      dataFine = StringUtils.getAttributeStrNotNull(row, "DATFINE");
      if(dataFine.equals("")) { dataFine = "&nbsp;"; }
      dataScadenzaConferma = StringUtils.getAttributeStrNotNull(row, "DATA_SCADENZA");
%>
    <tr>
      <td class="<%=classRow%>" align="center">
        <a href="#" onClick="insert_patto('<%=prgPattoLavoratore%>', '<%=dataStipula%>', '<%=dataScadenzaConferma%>')">
        <img src="../../img/add.gif" alt="+">
        </a>
      </td>
      <td class="<%=classRow%>" align="center"><%=dataStipula%></td>
      <td class="<%=classRow%>" align="left"><%=patto297%></td>
      <td class="<%=classRow%>" align="justify">
          <%=tipoPatto%>
      </td>
      <td class="<%=classRow%>" align="left"><%=motivo%></td>
      <td class="<%=classRow%>" align="center"><%=dataFine%></td>
    </tr>
<%
    }
%>
<tr><td class="def" colspan="6">&nbsp;</td></tr>
<tr>
    <td class="def" colspan="6" align="center">
    <input type="button" class="pulsanti" value="&nbsp;Chiudi&nbsp;"
     onClick="window.close();">
    </td>
</tr>
</table>
<%out.print(htmlStreamBottom);%>
</af:form>
</body>
</html>
