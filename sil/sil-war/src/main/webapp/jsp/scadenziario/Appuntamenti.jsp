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
boolean fromPattoAzioni = serviceRequest.containsAttribute("PATTO_AZIONI") && serviceRequest.getAttribute("PATTO_AZIONI").equals("true");
//Vector codLstTab = null;
String statoSezioni = "";
String nonFiltrare = "";
if(fromPattoAzioni) {
	//codLstTab = serviceRequest.getAttributeAsVector("COD_LST_TAB");
	statoSezioni = StringUtils.getAttributeStrNotNull(serviceRequest,"statoSezioni");
	nonFiltrare = StringUtils.getAttributeStrNotNull(serviceRequest,"NONFILTRARE");
}
// Savino 19/10/05: modifiche per gestire il ritorno alla pagina di associazione con la presenza oltre della sezione
//      appuntamenti anche quella delle azioni
//if(fromPattoAzioni && codLstTab.equals("")) codLstTab.add("AG_LAV");//In caso di ritorno alla pagina dopo l'inserimento
//if( codLstTab==null) codLstTab = new Vector(0);

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
String datascadenza = serviceRequest.containsAttribute("DATASCADENZA")? serviceRequest.getAttribute("DATASCADENZA").toString():"";
String strDescMotivoContatto = serviceRequest.containsAttribute("MOTIVO_CONTATTO")? serviceRequest.getAttribute("MOTIVO_CONTATTO").toString():"";
String strCodScadenza = serviceRequest.containsAttribute("SCADENZIARIO")? serviceRequest.getAttribute("SCADENZIARIO").toString():"";
String strFiltra = serviceRequest.containsAttribute("FILTRALISTA")? serviceRequest.getAttribute("FILTRALISTA").toString():"";
String strCodTipoVal = serviceRequest.containsAttribute("CODTIPOVALIDITA")? serviceRequest.getAttribute("CODTIPOVALIDITA").toString():"";
String strPatto297 = serviceRequest.containsAttribute("FLGPATTO")? serviceRequest.getAttribute("FLGPATTO").toString():"";
String strStatoValCV = serviceRequest.containsAttribute("statoValCV")? serviceRequest.getAttribute("statoValCV").toString():"";
String OpzioneCV = serviceRequest.containsAttribute("OpzioneCV")? serviceRequest.getAttribute("OpzioneCV").toString():"";

String CodCpiApp = serviceRequest.containsAttribute("CodCpiApp")? serviceRequest.getAttribute("CodCpiApp").toString():"";

//parametri per la gestione delle rose
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

String datInizioIscr = serviceRequest.containsAttribute("DATINIZIOISCR") ? ((String) serviceRequest.getAttribute("DATINIZIOISCR")) : "";
String datStimata = serviceRequest.containsAttribute("DATSTIMATA") ? ((String) serviceRequest.getAttribute("DATSTIMATA")) : "";
String codEsito = serviceRequest.containsAttribute("CODESITO") ? ((String) serviceRequest.getAttribute("CODESITO")) : "";
String codAzione = serviceRequest.containsAttribute("CODAZIONE") ? ((String) serviceRequest.getAttribute("CODAZIONE")) : "";

//Codice CPI dell'utente collegato
String codCpiOperatore = user.getCodRif();

String dataDalSlot   = serviceRequest.containsAttribute("dataDalSlot") ? ((String) serviceRequest.getAttribute("dataDalSlot")) : "";
String codServizio = serviceRequest.containsAttribute("CODSERVIZIO") ? ((String) serviceRequest.getAttribute("CODSERVIZIO")) : "";

String prgSpi = serviceRequest.containsAttribute("PRGSPI") ? ((String) serviceRequest.getAttribute("PRGSPI")) : "";
String prgSpiEff = serviceRequest.containsAttribute("PRGSPIEFF") ? ((String) serviceRequest.getAttribute("PRGSPIEFF")) : "";

InfCorrentiLav infCorrentiLav= null;
InfCorrentiAzienda infCorrentiAzienda= null;
User userCurr = (User) sessionContainer.getAttribute(User.USERID);
PageAttribs attributi = new PageAttribs(userCurr, "ScadAppuntamentoPage");
List listaSezioni = attributi.getSectionList();
if (!listaSezioni.contains("INSERISCI")) {
  if (strCdnLavoratore.compareTo("") != 0) {
    infCorrentiLav= new InfCorrentiLav(sessionContainer, strCdnLavoratore, userCurr);
  }
  else {
    infCorrentiAzienda = new InfCorrentiAzienda(strPrgAzienda,strPrgUnita);
  }
}

String messScad = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE_SCAD");
String messRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE_ROSA");
String listPageRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE_ROSA");
String listPageScad = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE_SCAD");

String token = "";
String urlDiLista = "";

%>
  
<%@ taglib uri="aftags" prefix="af" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title></title>
  <script language="Javascript">

	function setParentWindowLocation(newLocation) {
    	if (isInSubmit()) return;
	    window.parent.frames['ScadSuperiore'].prepareSubmit();
	    prepareSubmit();
		window.parent.location = newLocation;
	}


    function conferma(azione,strPage){
		// Se la pagina è già in submit, ignoro questo nuovo invio!
   	  if (isInSubmit()) return;

      if (azione=="BACK"){
		switch (strPage){
      	case "DispInsRosaPage":
      		document.frmAppuntamenti.MESSAGE.disabled = false;
			document.frmAppuntamenti.LIST_PAGE.disabled = false;
			document.frmAppuntamenti.MESSAGE.value = document.frmAppuntamenti.MESSAGE_ROSA.value;
			document.frmAppuntamenti.LIST_PAGE.value = document.frmAppuntamenti.LIST_PAGE_ROSA.value;
			document.frmAppuntamenti.MESSAGE_ROSA.disabled = true;
			document.frmAppuntamenti.LIST_PAGE_ROSA.disabled = true;
			document.frmAppuntamenti.MESSAGE_SCAD.disabled = true;
			document.frmAppuntamenti.LIST_PAGE_SCAD.disabled = true;
			break;		 
		case "StatoOccRisultRicercaPage":
			<%if (sessionContainer!=null){
			  token = "_TOKEN_" + "STATOOCCRISULTRICERCAPAGE";
			  urlDiLista = "AdapterHTTP?" + (String)sessionContainer.getAttribute(token.toUpperCase());
			 }%>
            setParentWindowLocation("<%=urlDiLista%>");
            return;
		default:
			document.frmAppuntamenti.MESSAGE.disabled = false;
			document.frmAppuntamenti.LIST_PAGE.disabled = false;
			document.frmAppuntamenti.MESSAGE.value = document.frmAppuntamenti.MESSAGE_SCAD.value;
			document.frmAppuntamenti.LIST_PAGE.value = document.frmAppuntamenti.LIST_PAGE_SCAD.value;
			document.frmAppuntamenti.MESSAGE_ROSA.disabled = true;
			document.frmAppuntamenti.LIST_PAGE_ROSA.disabled = true;
			document.frmAppuntamenti.MESSAGE_SCAD.disabled = true;
			document.frmAppuntamenti.LIST_PAGE_SCAD.disabled = true;
			break;
		}

        document.frmAppuntamenti.PAGE.value=strPage;
      } else {
      	document.frmAppuntamenti.MESSAGE.disabled = true;
		document.frmAppuntamenti.LIST_PAGE.disabled = true;
		document.frmAppuntamenti.MESSAGE_ROSA.disabled = false;
		document.frmAppuntamenti.LIST_PAGE_ROSA.disabled = false;
		document.frmAppuntamenti.MESSAGE_SCAD.disabled = false;
		document.frmAppuntamenti.LIST_PAGE_SCAD.disabled = false;
	  }
      doFormSubmit(document.frmAppuntamenti);
    }

    function DettaglioAppuntamento (prgAppuntamento) {
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;

      document.frmAppuntamenti.PRGAPPUNTAMENTO.value = prgAppuntamento;
      document.frmAppuntamenti.PAGE.value = "DettaglioAppuntamentoPage";
      doFormSubmit(document.frmAppuntamenti);
    }

    function ApriStampaPromemoria (page, prgappuntamento, prgazienda, prgunita, cdnlavoratore) {
      var f = "AdapterHTTP?PAGE=" + page + "&PRGAPPUNTAMENTO=" + prgappuntamento;
      if (cdnlavoratore != '') {
        f+= "&CDNLAVORATORE=" + cdnlavoratore;
      }
      else {
        f+= "&PRGAZIENDA=" + prgazienda + "&PRGUNITA=" + prgunita;
      }
      var t = "_blank";
      var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=600,height=300,top=100,left=100"
      window.open(f, t, feat);
    }
    
  </script>
</head>
<body class="gestione" onLoad="rinfresca()">
<%
if (!listaSezioni.contains("INSERISCI")) {
  if (strCdnLavoratore.compareTo("") != 0) {
    infCorrentiLav.setSkipLista(true);
    infCorrentiLav.show(out); 
  }
  else {
    infCorrentiAzienda.show(out); 
  }
}
%>
<font color="red">
  <af:showErrors/>
</font>
<font color="green">
</font>
<br/>
<tr><td colspan="2"><hr width="90%"/></td></tr>
<p class="titolo">Appuntamenti</p>
<af:form name="frmAppuntamenti" action="AdapterHTTP" method="POST" target="_parent">
<input type="hidden" name="CODCPIOPERATORE" value="<%=codCpiOperatore%>">
<input type="hidden" name="CODCPI" value="<%=codCpi%>">
<input type="hidden" name="PRGAPPUNTAMENTO" value="">
<input type="hidden" name="PAGE" value="">
<input type="hidden" name="PAGEPROVENIENZA" value="<%=_pageProvenienza%>">
<input type="hidden" name="PRGUNITA" value="<%=strPrgUnita%>">
<input type="hidden" name="PRGAZIENDA" value="<%=strPrgAzienda%>">
<input type="hidden" name="CDNLAVORATORE" value="<%=strCdnLavoratore%>">
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
<input type="hidden" name="DATAAL" value="<%=data_al%>">
<input type="hidden" name="DATADAL" value="<%=data_dal%>">
<input type="hidden" name="DIREZIONE" value="<%=strDirezione%>">
<input type="hidden" name="MOTIVO_CONTATTO" value="<%=strDescMotivoContatto%>"/>
<input type="hidden" name="DATASCADENZA" value="<%=datascadenza%>">
<input type="hidden" name="FILTRALISTA" value="<%=strFiltra%>">
<input type="hidden" name="SCADENZIARIO" value="<%=strCodScadenza%>"/>
<input type="hidden" name="CODTIPOVALIDITA" value="<%=strCodTipoVal%>">
<input type="hidden" name="FLGPATTO" value="<%=strPatto297%>">
<input type="hidden" name="statoValCV" value="<%=strStatoValCV%>">
<input type="hidden" name="OpzioneCV" value="<%=OpzioneCV%>">
<input type="hidden" name="CodCpiApp" value="<%=CodCpiApp%>">

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
<input type="hidden" name="prgSpi" value="<%=prgSpi%>">
<input type="hidden" name="prgSpiEff" value="<%=prgSpiEff%>">
<%if(fromPattoAzioni){%>
  <input type="hidden" name="PATTO_AZIONI" value="true">
  <input type="hidden" name="pageChiamante" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"PAGECHIAMANTE")%>">
  <input type="hidden" name="statoSezioni" value="<%=statoSezioni%>">
  <input type="hidden" name="NONFILTRARE" value="<%=nonFiltrare%>">
<%}%>
<%--for(int i=0;i< codLstTab.size();i++) {%>
<input type="hidden" name="COD_LST_TAB" value="<%=codLstTab.get(i)%>">
<%}--%>

<%out.print(QueryString.toInputHidden(serviceRequest, "COD_LST_TAB"));%>

<input type="hidden" name="MESSAGE" value="<%=messRosa%>" disabled/>
<input type="hidden" name="LIST_PAGE" value="<%=listPageScad%>" disabled/>
<input type="hidden" name="MESSAGE_ROSA" value="<%=messRosa%>"/>
<input type="hidden" name="LIST_PAGE_ROSA" value="<%=listPageRosa%>"/>
<input type="hidden" name="MESSAGE_SCAD" value="<%=messScad%>"/>
<input type="hidden" name="LIST_PAGE_SCAD" value="<%=listPageScad%>"/>

<input type="hidden" name="DATINIZIOISCR" value="<%=datInizioIscr%>"/>
<input type="hidden" name="DATSTIMATA" value="<%=datStimata%>"/>
<input type="hidden" name="CODESITO" value="<%=codEsito%>"/>
<input type="hidden" name="CODAZIONE" value="<%=codAzione%>"/>

<af:list moduleName="M_GetAppuntamentiLavoratore"  skipNavigationButton="0"
         canDelete="1"
         jsSelect="DettaglioAppuntamento" 
         jsDelete="ApriStampaPromemoria"/>
         
<p class="titolo">
<%if((_pageProvenienza != null) && !_pageProvenienza.equals("") && !_pageProvenienza.equals("null")){%>
    <input type="button" class="pulsanti" name="ANNULLA" value="Chiudi" onClick="javascript:conferma('BACK','<%=_pageProvenienza%>');">
<%}%>
</p>
</af:form>
</body>
</html>