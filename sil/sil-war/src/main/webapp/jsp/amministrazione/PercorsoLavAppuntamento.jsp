<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.*,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  com.engiweb.framework.security.*" %>

<%
//Quando si proviene dal patto
boolean fromPattoAzioni = serviceRequest.containsAttribute("PATTO_AZIONI");
String codLstTab = "";
String statoSezioni = "";
if(fromPattoAzioni) {
	codLstTab = (String)serviceRequest.getAttribute("COD_LST_TAB");
	statoSezioni = StringUtils.getAttributeStrNotNull(serviceRequest,"statoSezioni");
}

Testata operatoreInfo = null;
String _page = (String) serviceRequest.getAttribute("PAGE");
String _pageProvenienza = (String) serviceRequest.getAttribute("PAGEPROVENIENZA");
int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
String strPrgAzienda = serviceRequest.containsAttribute("PRGAZIENDA")? serviceRequest.getAttribute("PRGAZIENDA").toString():"";
String strPrgUnita = serviceRequest.containsAttribute("PRGUNITA")? serviceRequest.getAttribute("PRGUNITA").toString():"";
String strCdnLavoratore = serviceRequest.containsAttribute("CDNLAVORATORE")? serviceRequest.getAttribute("CDNLAVORATORE").toString():"";
String codCpi = serviceRequest.containsAttribute("CODCPI")? serviceRequest.getAttribute("CODCPI").toString():"";
String data_al = serviceRequest.containsAttribute("DATAAL")? serviceRequest.getAttribute("DATAAL").toString():"";
String data_dal = serviceRequest.containsAttribute("DATADAL")? serviceRequest.getAttribute("DATADAL").toString():"";
String strDirezione = serviceRequest.containsAttribute("DIREZIONE")? serviceRequest.getAttribute("DIREZIONE").toString():"";
String strDescMotivoContatto = serviceRequest.containsAttribute("MOTIVO_CONTATTO")? serviceRequest.getAttribute("MOTIVO_CONTATTO").toString():"";
String datascadenza = serviceRequest.containsAttribute("DATASCADENZA")? serviceRequest.getAttribute("DATASCADENZA").toString():"";
String strCodScadenza = serviceRequest.containsAttribute("SCADENZIARIO")? serviceRequest.getAttribute("SCADENZIARIO").toString():"";
String strFiltra = serviceRequest.containsAttribute("FILTRALISTA")? serviceRequest.getAttribute("FILTRALISTA").toString():"";
String strCodTipoVal = serviceRequest.containsAttribute("CODTIPOVALIDITA")? serviceRequest.getAttribute("CODTIPOVALIDITA").toString():"";
String strPatto297 = serviceRequest.containsAttribute("FLGPATTO")? serviceRequest.getAttribute("FLGPATTO").toString():"";
String strPrgRosa = serviceRequest.containsAttribute("PRGROSA")? serviceRequest.getAttribute("PRGROSA").toString():"";
String strCpiRose = serviceRequest.containsAttribute("CPIROSE")? serviceRequest.getAttribute("CPIROSE").toString():"";
String strPrgRichiestaAz = serviceRequest.containsAttribute("PRGRICHIESTAAZ")? serviceRequest.getAttribute("PRGRICHIESTAAZ").toString():"";
String strCognome = serviceRequest.containsAttribute("cognome")? serviceRequest.getAttribute("cognome").toString():"";
String strNome = serviceRequest.containsAttribute("nome")? serviceRequest.getAttribute("nome").toString():"";
String strCF = serviceRequest.containsAttribute("CF")? serviceRequest.getAttribute("CF").toString():"";
String strDidRilasciata = serviceRequest.containsAttribute("didRilasciata")? serviceRequest.getAttribute("didRilasciata").toString():"";
String strCodStataOcc = serviceRequest.containsAttribute("codStatoOcc")? serviceRequest.getAttribute("codStatoOcc").toString():"";
String strViewNonPresentati = serviceRequest.containsAttribute("viewNonPresentati")? serviceRequest.getAttribute("viewNonPresentati").toString():"";
String strNumVol = serviceRequest.containsAttribute("NumVol")? serviceRequest.getAttribute("NumVol").toString():"";
String strDataNP = serviceRequest.containsAttribute("dataNP")? serviceRequest.getAttribute("dataNP").toString():"";
String strCategoria181 = serviceRequest.containsAttribute("categoria181")? serviceRequest.getAttribute("categoria181").toString():"";
String strLegge407_90 = serviceRequest.containsAttribute("legge407_90")? serviceRequest.getAttribute("legge407_90").toString():"";
String strLungaDur = serviceRequest.containsAttribute("lungaDur")? serviceRequest.getAttribute("lungaDur").toString():"";
String strRevRic = serviceRequest.containsAttribute("revRic")? serviceRequest.getAttribute("revRic").toString():"";
String dataDalSlot   = serviceRequest.containsAttribute("dataDalSlot") ? ((String) serviceRequest.getAttribute("dataDalSlot")) : "";
String codServizio = serviceRequest.containsAttribute("CODSERVIZIO") ? ((String) serviceRequest.getAttribute("CODSERVIZIO")) : "";

User userCurr = (User) sessionContainer.getAttribute(User.USERID);
InfCorrentiLav infCorrentiLav= null;
InfCorrentiAzienda infCorrentiAzienda= null;
if (strCdnLavoratore.compareTo("") != 0) {
  infCorrentiLav = new InfCorrentiLav(sessionContainer, strCdnLavoratore, userCurr);
}
else {
  infCorrentiAzienda = new InfCorrentiAzienda(strPrgAzienda,strPrgUnita);
}

String cdnUtins="";
String dtmins="";
String cdnUtmod="";
String dtmmod="";
  
SourceBean cont = (SourceBean) serviceResponse.getAttribute("SELECT_DETTAGLIO_AGENDA_MOD");
SourceBean row = (SourceBean) cont.getAttribute("ROWS.ROW");
cdnUtins= row.containsAttribute("cdnUtins") ? row.getAttribute("cdnUtins").toString() : "";
dtmins=row.containsAttribute("dtmins") ? row.getAttribute("dtmins").toString() : "";
cdnUtmod=row.containsAttribute("cdnUtmod") ? row.getAttribute("cdnUtmod").toString() : "";
dtmmod=row.containsAttribute("dtmmod") ? row.getAttribute("dtmmod").toString() : "";
operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
String datApp = row.containsAttribute("DATA") ? row.getAttribute("DATA").toString() : "";
String strOraApp = row.containsAttribute("ORARIO") ? row.getAttribute("ORARIO").toString() : "";
BigDecimal prgSpiApp = null, prgSpiAppEff=null;;
prgSpiApp = (BigDecimal) row.getAttribute("PRGSPI");
prgSpiAppEff = (BigDecimal) row.getAttribute("PRGSPIEFF");
String strSpiApp = "", strSpiAppEff="";
if (prgSpiApp != null) {
  strSpiApp = prgSpiApp.toString();
}
if (prgSpiAppEff != null) {
  strSpiAppEff = prgSpiAppEff.toString();
}
String codServizioApp = row.containsAttribute("CODSERVIZIO") ? row.getAttribute("CODSERVIZIO").toString() : "";
BigDecimal prgAmbiente = null;
prgAmbiente = (BigDecimal) row.getAttribute("PRGAMBIENTE");
String strAmbiente = "";
if (prgAmbiente != null) {
  strAmbiente = prgAmbiente.toString();
}

BigDecimal nDurataMinuti = null;
nDurataMinuti = (BigDecimal) row.getAttribute("NUMMINUTI");
String strDurata = "";
if (nDurataMinuti != null) {
  strDurata = nDurataMinuti.toString();
}
String txtNoteApp = row.containsAttribute("TXTNOTE") ? row.getAttribute("TXTNOTE").toString() : "";
String CODEFFETTOAPPUNT = StringUtils.getAttributeStrNotNull(row,"CODEFFETTOAPPUNT");
String CODESITOAPPUNT = StringUtils.getAttributeStrNotNull(row,"CODESITOAPPUNT");
String CODSTATOAPPUNTAMENTO = StringUtils.getAttributeStrNotNull(row, "CODSTATOAPPUNTAMENTO");
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);


String mess = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE");
String listPage = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE");
String messRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE_ROSA");
String listPageRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE_ROSA");
String messScad = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE_SCAD");
String listPageScad = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE_SCAD");

String labelServizio = "Servizio";
String umbriaGestAz = "0";
if(serviceResponse.containsAttribute("M_CONFIG_UMB_NGE_AZ")){
	umbriaGestAz = Utils.notNull(serviceResponse.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM"));
}
if(umbriaGestAz.equalsIgnoreCase("1")){
	labelServizio = "Area";
}
%>
  
<%@ taglib uri="aftags" prefix="af" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Pecorso lavoratore: Appuntamento</title>
  <script type="text/javascript">
    function chiudi () { 
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;

      document.frmAppuntamento.PAGE.value = "ScadAppuntamentoPage";
      doFormSubmit(document.frmAppuntamento);
    }
  </script> 
  
</head>
<body class="gestione">
<%--
if (strCdnLavoratore.compareTo("") != 0) {
  infCorrentiLav.show(out); 
}
else {
  infCorrentiAzienda.show(out); 
}
--%>
<af:form name="frmAppuntamento" action="AdapterHTTP" method="POST">
<input type="hidden" name="CODCPICONTATTO" value="<%=codCpi%>">
<input type="hidden" name="CODCPI" value="<%=codCpi%>">
<input type="hidden" name="PAGE" value="ScadSalvaContattoPage">
<input type="hidden" name="PAGEPROVENIENZA" value="<%=_pageProvenienza%>">
<input type="hidden" name="PRGUNITA" value="<%=strPrgUnita%>">
<input type="hidden" name="PRGAZIENDA" value="<%=strPrgAzienda%>">
<input type="hidden" name="CDNLAVORATORE" value="<%=strCdnLavoratore%>">
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
<input type="hidden" name="DATASCADENZA" value="<%=datascadenza%>">
<input type="hidden" name="PRGCONTATTO" value="">
<input type="hidden" name="DATAAL" value="<%=data_al%>">
<input type="hidden" name="DATADAL" value="<%=data_dal%>">
<input type="hidden" name="DIREZIONE" value="<%=strDirezione%>">
<input type="hidden" name="MOTIVO_CONTATTO" value="<%=strDescMotivoContatto%>"/>
<input type="hidden" name="FILTRALISTA" value="<%=strFiltra%>">
<input type="hidden" name="SCADENZIARIO" value="<%=strCodScadenza%>"/>
<input type="hidden" name="CODTIPOVALIDITA" value="<%=strCodTipoVal%>">
<input type="hidden" name="FLGPATTO" value="<%=strPatto297%>">
<input type="hidden" name="PRGROSA" value="<%=strPrgRosa%>">
<input type="hidden" name="CPIROSE" value="<%=strCpiRose%>">
<input type="hidden" name="PRGRICHIESTAAZ" value="<%=strPrgRichiestaAz%>">
<input type="hidden" name="cognome" value="<%=strCognome%>">
<input type="hidden" name="nome" value="<%=strNome%>">
<input type="hidden" name="CF" value="<%=strCF%>">
<input type="hidden" name="didRilasciata" value="<%=strDidRilasciata%>">
<input type="hidden" name="codStatoOcc" value="<%=strCodStataOcc%>">
<input type="hidden" name="viewNonPresentati" value="<%=strViewNonPresentati%>">
<input type="hidden" name="NumVol" value="<%=strNumVol%>">
<input type="hidden" name="dataNP" value="<%=strDataNP%>">
<input type="hidden" name="categoria181" value="<%=strCategoria181%>">
<input type="hidden" name="legge407_90" value="<%=strLegge407_90%>">
<input type="hidden" name="lungaDur" value="<%=strLungaDur%>">
<input type="hidden" name="revRic" value="<%=strRevRic%>">
<input type="hidden" name="dataDalSlot" value="<%=dataDalSlot%>">
<input type="hidden" name="codServizio" value="<%=codServizio%>">
<%if(fromPattoAzioni){%>
  <input type="hidden" name="PATTO_AZIONI" value="true">
  <input type="hidden" name="statoSezioni" value="<%=statoSezioni%>">
  <input type="hidden" name="pageChiamante" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"PAGECHIAMANTE")%>">
<%}%>
<input type="hidden" name="COD_LST_TAB" value="<%=codLstTab%>">

<input type="hidden" name="MESSAGE" value="<%=messRosa%>" disabled/>
<input type="hidden" name="LIST_PAGE" value="<%=listPageScad%>" disabled/>
<input type="hidden" name="MESSAGE_ROSA" value="<%=messRosa%>"/>
<input type="hidden" name="LIST_PAGE_ROSA" value="<%=listPageRosa%>"/>
<input type="hidden" name="MESSAGE_SCAD" value="<%=messScad%>"/>
<input type="hidden" name="LIST_PAGE_SCAD" value="<%=listPageScad%>"/>



<p align="center">
<%out.print(htmlStreamTop);%>
<table class="main">
<tr><td colspan="2"><p class="titolo">Dettaglio Appuntamento</p></tr>
<tr><td colspan="2">&nbsp;
<tr>
  <td class="etichetta">Data</td>
  <td class="campo">
  <af:textBox name="DATAAPP"
              size="11"
              maxlength="10"
              type="date"
              readonly="true"
              classNameBase="input"
              value="<%=datApp%>"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Orario</td>
  <td class="campo">
  <af:textBox name="ORARIOAPP"
              size="5"
              maxlength="5"
              type="time"
              title="Orario"
              readonly="true"
              classNameBase="input"
              value="<%=strOraApp%>"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Durata</td>
  <td class="campo">
  <af:textBox name="DurataApp"
              size="5"
              maxlength="5"
              type="text"
              title="Durata"
              readonly="true"
              classNameBase="input"
              value="<%=strDurata%>"/>
  </td>
</tr>
<tr>
  <td class="etichetta"><%=labelServizio %></td>
    <td class="campo">
      <af:comboBox name="SERVIZIOAPP" size="1" title="<%=labelServizio %>"
                     multiple="false" disabled="true" classNameBase="input"
                     focusOn="false" moduleName="COMBO_SERVIZIO"
                     selectedValue="<%=codServizioApp%>" addBlank="true" blankValue=""/>
    </td>
</tr>
<tr>
  <td class="etichetta">Operatore</td>
    <td class="campo">
      <af:comboBox name="PRGSPIAPP" size="1" title="Operatore"
                     multiple="false" disabled="true" classNameBase="input"
                     focusOn="false" moduleName="COMBO_SPI_SCAD"
                     selectedValue="<%=strSpiApp%>" addBlank="true" blankValue=""/>
    </td>
</tr>
<tr>
  <td class="etichetta">Operatore effettivo</td>
    <td class="campo">
      <af:comboBox name="PRGSPIAPPEFF" size="1" title="Operatore effettivo"
                     multiple="false" disabled="true" classNameBase="input"
                     focusOn="false" moduleName="COMBO_SPI_SCAD_EFF"
                     selectedValue="<%=strSpiAppEff%>" addBlank="true" blankValue=""/>
    </td>
</tr>
<tr>
  <td class="etichetta">Ambiente</td>
    <td class="campo">
      <af:comboBox name="AMBIENTEAPP" size="1" title="Ambiente"
                     multiple="false" disabled="true" classNameBase="input"
                     focusOn="false" moduleName="COMBO_AMBIENTE"
                     selectedValue="<%=strAmbiente%>" addBlank="true" blankValue=""/>
    </td>
</tr>
<tr class="note">
  <td class="etichetta">Note</td>
  <td class="campo">
    <af:textArea name="TXTCONTATTOAPP" 
                 cols="60" 
                 rows="4" 
                 title="Note"
                 readonly="true"
                 classNameBase="textarea"
                 value="<%=txtNoteApp%>"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Effetto Appuntamento</td>
  <td class="campo">
      <af:comboBox name="CODEFFETTOAPPUNT" size="1" title="Effetto appuntamento"
                     multiple="false" required="false"
                     focusOn="false" moduleName="COMBO_EFFETTO_APPUNTAMENTO"
                     selectedValue="<%=CODEFFETTOAPPUNT%>" addBlank="true" blankValue=""
                     classNameBase="input"
                     disabled="true"/>    
    </td>
</tr>
<tr>
  <td class="etichetta">Esito appuntamento</td>
  <td class="campo">
      <af:comboBox name="CODESITOAPPUNT" size="1" title="Effetto appuntamento"
                     multiple="false" required="false"
                     focusOn="false" moduleName="COMBO_ESITO_APPUNTAMENTO"
                     selectedValue="<%=CODESITOAPPUNT%>" addBlank="true" blankValue=""
                     classNameBase="input"
                     disabled="true"/>        
  </td>
</tr>
<tr>
  <td class="etichetta">Stato appuntamento</td>
  <td class="campo">
      <af:comboBox name="CODSTATOAPPUNTAMENTO" size="1" title="Stato appuntamento"
                     multiple="false" required="true"
                     focusOn="false" moduleName="COMBO_STATO_APPUNTAMENTO"
                     selectedValue="<%=CODSTATOAPPUNTAMENTO%>" addBlank="true" blankValue=""
                     classNameBase="input"
                     disabled="true"/>        
  </td>
</tr>

</table>
<%out.print(htmlStreamBottom);%>
<br>
<p class="titolo">Lavoratori</p>
<af:list moduleName="LISTA_LAVORATORI_APPUNTAMENTO_SCAD_PERCORSO" skipNavigationButton="1" />

</af:form>
<center>
	<% operatoreInfo.showHTML(out);%>	
	<br>
	<input type="button" class="pulsanti" value="Chiudi" onclick="window.close()">
</center>
<br>
</body>
</html>