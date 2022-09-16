<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.*,
  it.eng.afExt.utils.*,
  it.eng.afExt.utils.StringUtils,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  com.engiweb.framework.security.*" %>

<%@ taglib uri="aftags" prefix="af" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
String messScad = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE_SCAD");
String listPage = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE_SCAD");
String messRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE_ROSA");
String listPageRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE_ROSA");
%>

<html>
<head>
<af:linkScript path="../../js/" />
<script language="javascript">
function CaricaForm() {
  document.Frm1.PAGEPROVENIENZA.value = parent.localPageProvenienza;
  document.Frm1.CDNFUNZIONE.value = parent.localFunzione;
  document.Frm1.PRGAZIENDA.value = parent.strPrgAzienda;
  document.Frm1.PRGUNITA.value = parent.strPrgUnita;
  document.Frm1.CDNLAVORATORE.value = parent.strCdnLavoratore;
  document.Frm1.CODCPI.value = parent.codCpi;
  document.Frm1.DATADAL.value = parent.data_dal;
  document.Frm1.DATAAL.value = parent.data_al;
  document.Frm1.DIREZIONE.value = parent.strDirezioneLocal;
  document.Frm1.MOTIVO_CONTATTO.value = parent.strMotivoContatto;
  document.Frm1.DATASCADENZA.value = parent.datascadenza;
  document.Frm1.SCADENZIARIO.value = parent.strCodScadenza;
  document.Frm1.FILTRALISTA.value = parent.strFiltra;
  document.Frm1.CODTIPOVALIDITA.value = parent.strCodTipoVal;
  document.Frm1.FLGPATTO.value = parent.strPatto297;
  document.Frm1.statoValCV.value = parent.strStatoValCV;
  document.Frm1.OpzioneCV.value = parent.OpzioneCV;
  
  document.Frm1.CodCpiApp.value = parent.CodCpiApp;
  document.Frm1.PRGROSA.value = parent.localPrgRosa;
  document.Frm1.CPIROSE.value = parent.localCodCpiRose;
  document.Frm1.PRGRICHIESTAAZ.value = parent.localPrgRichiestaAz;
  document.Frm1.cognome.value = parent.strCognomeLocal;
  document.Frm1.nome.value = parent.strNomeLocal;
  document.Frm1.CF.value = parent.strCFLocal;
  document.Frm1.didRilasciata.value = parent.strDidRilasciataLocal;
  document.Frm1.codStatoOcc.value = parent.strCodStataOccLocal;
  document.Frm1.viewNonPresentati.value = parent.strViewNonPresLocal;
  document.Frm1.NumVol.value = parent.strNumVolLocal;
  document.Frm1.dataNP.value = parent.strDataNPLocal;
  document.Frm1.categoria181.value = parent.strCategoria181Local;
  document.Frm1.legge407_90.value = parent.strLegge407_90Local;
  document.Frm1.lungaDur.value = parent.strLungaDurLocal;
  document.Frm1.revRic.value = parent.strRevRicLocal;
  document.Frm1.dataDalSlot.value = parent.dataDalSlotLocal;
  document.Frm1.codServizio.value = parent.codServizioLocal;
  document.Frm1.PATTO_AZIONI.value = parent.fromPattoAzioni;
  // AndS 19/10/05: necessario per gestire piu' codLstTab
  if (parent.codLstTab!=null) {
    document.Frm1.COD_LST_TAB.options[0]=null;
  	for(i=0;i<parent.codLstTab.length;i++)
  		document.Frm1.COD_LST_TAB.options[i]=new Option(parent.codLstTab[i], parent.codLstTab[i], true, true);
  }
  document.Frm1.pageChiamante.value = parent.pageChiamante;
  document.Frm1.statoSezioni.value = parent.statoSezioni;
  document.Frm1.NONFILTRARE.value = parent.nonFiltrare;
  
  document.Frm1.MESSAGE_ROSA.value = parent.strmessRosa;
  document.Frm1.MESSAGE_SCAD.value = parent.strmessScad;
  document.Frm1.LIST_PAGE_ROSA.value = parent.strlistPageRosa;
  document.Frm1.LIST_PAGE_SCAD.value = parent.strlistPageScad;
  document.Frm1.PRGSPI.value = parent.operatore;
  
  document.Frm1.DATINIZIOISCR.value = parent.datInizioIscr;
  document.Frm1.DATSTIMATA.value = parent.datStimata;
  document.Frm1.CODESITO.value = parent.codEsito;
  document.Frm1.CODAZIONE.value = parent.codAzione;
  
  doFormSubmit(document.Frm1);
}
</script>
</head>
<body>
<af:form name="Frm1" action="AdapterHTTP?PAGE=ScadSlotPrenotabiliPage" method="POST" dontValidate="true">
<input type="hidden" name="PAGEPROVENIENZA" value="">
<input type="hidden" name="CDNFUNZIONE" value="">
<input type="hidden" name="PRGAZIENDA" value="">
<input type="hidden" name="PRGUNITA" value="">
<input type="hidden" name="CDNLAVORATORE" value="">
<input type="hidden" name="CODCPI" value="">
<input type="hidden" name="DATAAL" value="">
<input type="hidden" name="DATADAL" value="">
<input type="hidden" name="DIREZIONE" value="">
<input type="hidden" name="MOTIVO_CONTATTO" value="">
<input type="hidden" name="DATASCADENZA" value="">
<input type="hidden" name="SCADENZIARIO" value="">
<input type="hidden" name="FILTRALISTA" value="">
<input type="hidden" name="CODTIPOVALIDITA" value="">
<input type="hidden" name="FLGPATTO" value="">
<input type="hidden" name="statoValCV" value="">
<input type="hidden" name="OpzioneCV" value="">
<input type="hidden" name="CodCpiApp" value="">


<!-- parametri per la gestione delle rose -->
<input type="hidden" name="PRGROSA" value="">
<input type="hidden" name="CPIROSE" value="">
<input type="hidden" name="PRGRICHIESTAAZ" value="">
<input type="hidden" name="cognome" value="">
<input type="hidden" name="nome" value="">
<input type="hidden" name="CF" value="">
<input type="hidden" name="didRilasciata" value="">
<input type="hidden" name="codStatoOcc" value="">
<input type="hidden" name="viewNonPresentati" value="">
<input type="hidden" name="NumVol" value="">
<input type="hidden" name="dataNP" value="">
<input type="hidden" name="categoria181" value="">
<input type="hidden" name="legge407_90" value="">
<input type="hidden" name="lungaDur" value="">
<input type="hidden" name="revRic" value="">
<input type="hidden" name="dataDalSlot" value="">
<input type="hidden" name="codServizio" value="">

<input type="hidden" name="PATTO_AZIONI" value="">
<select name="COD_LST_TAB" multiple="multiple" style="display:none"></select>
<input type="hidden" name="pageChiamante" value="">
<input type="hidden" name="statoSezioni" value="">
<input type="hidden" name="NONFILTRARE" value="">

<input type="hidden" name="MESSAGE_SCAD" value=""/>
<input type="hidden" name="LIST_PAGE_SCAD" value=""/>
<input type="hidden" name="MESSAGE_ROSA" value=""/>
<input type="hidden" name="LIST_PAGE_ROSA" value=""/>

<input type="hidden" name="PRGSPI" value="">

<input type="hidden" name="DATINIZIOISCR" value="">
<input type="hidden" name="DATSTIMATA" value="">
<input type="hidden" name="CODESITO" value="">
<input type="hidden" name="CODAZIONE" value="">

</af:form>

<script language="javascript">
  CaricaForm();
</script>

</body>
</html>