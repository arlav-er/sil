<!-- @author: Paolo Roccetti - Gennaio 2004 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
				  com.engiweb.framework.tags.DefaultErrorTag,
                   
                  com.engiweb.framework.util.*,
                  it.eng.sil.module.movimenti.*,
                  it.eng.sil.module.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>
                  
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
  String strPageSalto = "";
  String cdnFunzione = ""; 
  String cdnFunzioneLocal = serviceRequest.containsAttribute("CDNFUNZIONE")?serviceRequest.getAttribute("CDNFUNZIONE").toString():"";
  //prelevo il soggetto della ricerca
  String soggetto = (String) serviceRequest.getAttribute("MOV_SOGG");
  String funzioneaggiornamento = (String) serviceRequest.getAttribute("AGG_FUNZ");   
  SourceBean goInserisciAz = null;
  SourceBean goInserisciLav = null;
  
  String codMvTrasformazione = StringUtils.getAttributeStrNotNull(serviceRequest, "codMvTrasformazione");
  
  //Controllo se ho una sola azienda nella lista, in questo caso la seleziono direttamente
  BigDecimal prgAzienda = null;
  BigDecimal prgUnita = null;
  String ragioneSociale = null;
  String partitaIva = null;
  String codiceFiscaleAzienda = null;
  String strIndirizzoAzienda = null;
  String comuneAzienda = null;
  String provAzienda = null;
  String strTelAzienda = null;
  String strFaxAzienda = null;
  String strCapAzienda = null;
  String codTipoAz = null;
  String natGiurAz = null;
  String descrTipoAz = null;
  String numalbointerinali = null;
  String numregcommitt = null;
  String strdescrizioneccnl = null;
  String ccnlaz = null;
  String codAteco = null;
  String strDesAtecoUAz = null;
  String strPatInail = null;
  String strNumeroInps = null;
  String codNatGiurAz = null;
  String strReferente = null;  
  String strFlgDatiOK = null;
  String codCpi = null;
  String strCfEstera = "";
  String strRagSocEstera = "";
  
  boolean unicaAzienda = false;
  
  if ("Aziende".equalsIgnoreCase(soggetto)) {
  	Vector rows = serviceResponse.getAttributeAsVector("M_MovimentiGetListaAziende.ROWS.ROW");
  	/*if (rows.size() == 1) {
  		SourceBean row = (SourceBean) rows.get(0);
  		unicaAzienda = true;
		prgAzienda = (BigDecimal) row.getAttribute("prgAzienda");
		prgUnita = (BigDecimal) row.getAttribute("prgUnita");
		ragioneSociale = StringUtils.getAttributeStrNotNull(row, "strRagioneSociale");
		partitaIva = StringUtils.getAttributeStrNotNull(row, "STRPARTITAIVA");
		codiceFiscaleAzienda = StringUtils.getAttributeStrNotNull(row, "STRCODICEFISCALE");
		strIndirizzoAzienda = StringUtils.getAttributeStrNotNull(row, "STRINDIRIZZO");
		comuneAzienda = StringUtils.getAttributeStrNotNull(row, "comune_az");
		provAzienda = StringUtils.getAttributeStrNotNull(row, "prov_az");
		strTelAzienda = StringUtils.getAttributeStrNotNull(row, "STRTEL");
		strFaxAzienda = StringUtils.getAttributeStrNotNull(row, "STRFAX");
		strCapAzienda = StringUtils.getAttributeStrNotNull(row, "STRCAP");
		codTipoAz = StringUtils.getAttributeStrNotNull(row, "codTipoAzienda");
		natGiurAz = StringUtils.getAttributeStrNotNull(row, "natGiurAz");
		descrTipoAz = StringUtils.getAttributeStrNotNull(row, "descrTipoAz");
		numalbointerinali = StringUtils.getAttributeStrNotNull(row, "STRNUMALBOINTERINALI");
		numregcommitt = StringUtils.getAttributeStrNotNull(row, "STRNUMREGCOMM");
		strdescrizioneccnl = StringUtils.getAttributeStrNotNull(row, "STRDESCRIZIONECCNL");
		ccnlaz = StringUtils.getAttributeStrNotNull(row, "CCNLAZ");  
		codAteco = StringUtils.getAttributeStrNotNull(row, "codAteco");
		strDesAtecoUAz = StringUtils.getAttributeStrNotNull(row, "strDesAtecoUAz");
		strPatInail = StringUtils.getAttributeStrNotNull(row, "strPatInail");
		strNumeroInps = StringUtils.getAttributeStrNotNull(row, "strNumeroInps");
		codNatGiurAz = StringUtils.getAttributeStrNotNull(row, "CODNATGIURIDICA");
		strReferente = StringUtils.getAttributeStrNotNull(row, "STRREFERENTE");		
		strFlgDatiOK = StringUtils.getAttributeStrNotNull(row, "FLGDATIOK");
		codCpi = StringUtils.getAttributeStrNotNull(row, "CODCPI");		
  	}*/
  }

  if ("Testate".equalsIgnoreCase(soggetto)) {
  	Vector rows = serviceResponse.getAttributeAsVector("M_MovimentiGetListaTestate.ROWS.ROW");
  	if (rows.size() == 1) {
  		SourceBean row = (SourceBean) rows.get(0);
  		unicaAzienda = true;
		prgAzienda = (BigDecimal) row.getAttribute("prgAzienda");
		prgUnita = (BigDecimal) row.getAttribute("prgUnita");
		ragioneSociale = StringUtils.getAttributeStrNotNull(row, "strRagioneSociale");
		partitaIva = StringUtils.getAttributeStrNotNull(row, "STRPARTITAIVA");
		codiceFiscaleAzienda = StringUtils.getAttributeStrNotNull(row, "STRCODICEFISCALE");
		strIndirizzoAzienda = StringUtils.getAttributeStrNotNull(row, "STRINDIRIZZO");
		comuneAzienda = StringUtils.getAttributeStrNotNull(row, "comune_az");
		provAzienda = StringUtils.getAttributeStrNotNull(row, "prov_az");
		strTelAzienda = StringUtils.getAttributeStrNotNull(row, "STRTEL");
		strFaxAzienda = StringUtils.getAttributeStrNotNull(row, "STRFAX");
		strCapAzienda = StringUtils.getAttributeStrNotNull(row, "STRCAP");
		codTipoAz = StringUtils.getAttributeStrNotNull(row, "codTipoAzienda");
		natGiurAz = StringUtils.getAttributeStrNotNull(row, "natGiurAz");
		descrTipoAz = StringUtils.getAttributeStrNotNull(row, "descrTipoAz");
		numalbointerinali = StringUtils.getAttributeStrNotNull(row, "STRNUMALBOINTERINALI");
		numregcommitt = StringUtils.getAttributeStrNotNull(row, "STRNUMREGCOMM");
		//strdescrizioneccnl = StringUtils.getAttributeStrNotNull(row, "STRDESCRIZIONECCNL");
		//ccnlaz = StringUtils.getAttributeStrNotNull(row, "CCNLAZ"); 
		codAteco = StringUtils.getAttributeStrNotNull(row, "codAteco");
		strDesAtecoUAz = StringUtils.getAttributeStrNotNull(row, "strDesAtecoUAz");
		strPatInail = StringUtils.getAttributeStrNotNull(row, "strPatInail");
		strNumeroInps = StringUtils.getAttributeStrNotNull(row, "strNumeroInps");
		codNatGiurAz = StringUtils.getAttributeStrNotNull(row, "CODNATGIURIDICA");
		strReferente = StringUtils.getAttributeStrNotNull(row, "STRREFERENTE");		
		strFlgDatiOK = StringUtils.getAttributeStrNotNull(row, "FLGDATIOK");
		codCpi = StringUtils.getAttributeStrNotNull(row, "CODCPI");
		strCfEstera = StringUtils.getAttributeStrNotNull(row, "CODFISCAZESTERA");
		strRagSocEstera = StringUtils.getAttributeStrNotNull(row, "RAGSOCAZESTERA");
  	}
  }

%> 


<html>
<head>
  <title>Lista <%=soggetto%></title>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/" />

<% if (unicaAzienda) {%>
    <script type="text/javascript">
    var datacontainer = new Object();
    window.dati = datacontainer;
    datacontainer.soggetto = "<%=soggetto%>";
    datacontainer.funzioneaggiornamento = "<%=funzioneaggiornamento%>";

    datacontainer.prgAzienda = "<%=prgAzienda%>";
    datacontainer.prgUnita = "<%=prgUnita%>";
    datacontainer.ragioneSociale = "<%=StringUtils.replace(ragioneSociale,"\"","\\\"")%>";
    datacontainer.partitaIva = "<%=partitaIva%>";
    datacontainer.codiceFiscaleAzienda = "<%=codiceFiscaleAzienda%>";
    datacontainer.FLGDATIOK = "<%=strFlgDatiOK%>";
    datacontainer.strIndirizzoAzienda = "<%=strIndirizzoAzienda%>";
    datacontainer.comuneAzienda = "<%=comuneAzienda%>";
    datacontainer.strTelAzienda = "<%=strTelAzienda%>";
    datacontainer.strFaxAzienda = "<%=strFaxAzienda%>";
    datacontainer.strCapAzienda = "<%=strCapAzienda%>";
    datacontainer.provAzienda = "<%=provAzienda%>";
    datacontainer.codTipoAz = "<%=codTipoAz%>";
    datacontainer.natGiurAz = "<%=natGiurAz%>";
    datacontainer.codNatGiurAz = "<%=codNatGiurAz%>";
    datacontainer.descrTipoAz = "<%=descrTipoAz%>";
    datacontainer.numAlboInterinali = "<%=numalbointerinali%>";
    datacontainer.numRegCommitt = "<%=numregcommitt%>";
    datacontainer.descrCCNLAz = "<%=strdescrizioneccnl%>";
    datacontainer.CCNLAz = "<%=ccnlaz%>";    
    datacontainer.codAteco = "<%=codAteco%>";  
    datacontainer.strDesAtecoUAz = "<%=strDesAtecoUAz%>";  
    datacontainer.strPatInail = "<%=strPatInail%>";
    datacontainer.strNumeroInps = "<%=strNumeroInps%>";
    datacontainer.strReferente = "<%=strReferente%>"; 
    datacontainer.codCpi = "<%=codCpi%>";
    datacontainer.CODFISCAZESTERA = "<%=strCfEstera%>";
    datacontainer.RAGSOCAZESTERA = "<%=strRagSocEstera%>";
  </script>
<%}%>


  <script language="JavaScript">
  function inserisciAzienda () {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;
	
		doFormSubmit(document.FrmInserisciAz);
  }
  
  function inserisciLavoratore () {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;
	
		doFormSubmit(document.FrmInserisciLav);
  }
  function tornaAllaRicerca() {
  	if (isInSubmit()) return;
  	queryString = "AGG_FUNZ=<%=funzioneaggiornamento%>&CDNFUNZIONE=<%=cdnFunzioneLocal %>&MOV_SOGG=<%=soggetto%>&PAGE=MovimentiSelezionaSoggettoPage";
  	window.location = "AdapterHTTP?" + queryString;
  }
  </script>
  
</head>

<body onload="checkError();<%if (unicaAzienda) {%>window.opener.<%=funzioneaggiornamento%>();<%}%>" class="gestione">
<af:error/>
<p align="center">
<!--Visualizzo i risultati a seconda del tipo di ricerca-->
<% 
if (soggetto.equals("Aziende") ) {
  goInserisciAz = (SourceBean) serviceResponse.getAttribute("M_MovimentiGoInserisciAzienda.ROW");
  if (goInserisciAz != null) {
    strPageSalto = goInserisciAz.getAttribute("GOPAGE").toString();
    cdnFunzione = goInserisciAz.getAttribute("GOCDNFUNZ").toString();
  }
%>
<af:list moduleName="M_MovimentiGetListaAziende" configProviderClass="it.eng.sil.module.movimenti.DynamicRicAziendeConfig"/>
<%} else if (soggetto.equals("Lavoratori")) {
  goInserisciLav = (SourceBean) serviceResponse.getAttribute("M_MovimentiGoInserisciLavoratore.ROW");
  if (goInserisciLav != null) {
    strPageSalto = goInserisciLav.getAttribute("GOPAGE").toString();
    cdnFunzione = goInserisciLav.getAttribute("GOCDNFUNZ").toString();
  }
%>
<af:list moduleName="M_MovimentiGetListaLavoratori"/>
<%} else if (soggetto.equals("LavoratoriNew")) {
  goInserisciLav = (SourceBean) serviceResponse.getAttribute("M_MovimentiGoInserisciLavoratore.ROW");
  if (goInserisciLav != null) {
    strPageSalto = goInserisciLav.getAttribute("GOPAGE").toString();
    cdnFunzione = goInserisciLav.getAttribute("GOCDNFUNZ").toString();
  }
%>
<af:list moduleName="M_MovimentiGetListaLavoratoriNew"/>
<%} else if (soggetto.equals("Mansioni")) {%>
<af:list moduleName="M_MovimentiGetListaMansioni"/>
<%} else if (soggetto.equals("Testate")) {%>
<af:list moduleName="M_MovimentiGetListaTestate" configProviderClass="it.eng.sil.module.movimenti.DynamicRicTestateConfig"/>
<% } %>

<table class="main">
<tr><td>&nbsp;</td></tr>
<% if (soggetto.equals("Aziende") && cdnFunzioneLocal.equals("52")  && (!funzioneaggiornamento.equals("aggiornaAziendaUt"))) {
    if (goInserisciAz != null) {%>
      <tr>
        <td align="center">
          <input type="button" class="pulsanti" value="Nuova azienda" onClick="javascript:inserisciAzienda();">
        </td>
      </tr>
    <%}
  }
  else {
    if ((soggetto.equals("Lavoratori") || soggetto.equals("LavoratoriNew")) && cdnFunzioneLocal.equals("52")) {
      if (goInserisciLav != null) {%>
        <tr>
          <td align="center">
            <input type="button" class="pulsanti" value="Nuovo lavoratore" onClick="javascript:inserisciLavoratore();">
          </td>
        </tr>
      <%
      }
    }
  }
  %>
<tr>
  <td align="center">
  <%if(!codMvTrasformazione.equals("TL")) {%>
    <input type="button" class="pulsanti" value="Ritorna alla pagina di ricerca" onClick="tornaAllaRicerca()">
  <%}%>
  </td>
</tr>
<tr>
  <td align="center">
	<input type="button" class="pulsanti" value="Chiudi" onClick="window.close()">
  </td>
</tr>
</table>
<%
if (goInserisciAz != null) {%>
  <af:form name="FrmInserisciAz" method="POST" action="AdapterHTTP" dontValidate="true">
  <input type="hidden" name="PAGE" value="<%=strPageSalto%>"/>
  <input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>"/>
  <input type="hidden" name="inserisci" value=""/>
  <input type="hidden" name="CONTESTOPROVENIENZA" value="MOVIMENTI"/>
  <input type="hidden" name="AGG_FUNZ" value="<%=funzioneaggiornamento%>"/>
  </af:form>
<%}
else {
  if (goInserisciLav != null) {%>
    <af:form name="FrmInserisciLav" method="POST" action="AdapterHTTP" dontValidate="true">
    <input type="hidden" name="PAGE" value="<%=strPageSalto%>"/>
    <input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>"/>
    <input type="hidden" name="AGG_FUNZ" value="<%=funzioneaggiornamento%>"/>
    <input type="hidden" name="PROVENIENZA" value="MOVIMENTI"/>
    </af:form>
  <%}
}
%>
</body>
</html>