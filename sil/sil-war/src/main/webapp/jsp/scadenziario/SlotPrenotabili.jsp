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
if(fromPattoAzioni){ 
	//codLstTab = serviceRequest.getAttributeAsVector("COD_LST_TAB");
	statoSezioni = StringUtils.getAttributeStrNotNull(serviceRequest,"statoSezioni");
	nonFiltrare = StringUtils.getAttributeStrNotNull(serviceRequest,"NONFILTRARE");
}
// Savino 19/10/05: modifiche per gestire il ritorno alla pagina di associazione con la presenza oltre della sezione
//      appuntamenti anche quella delle azioni
//if(fromPattoAzioni && codLstTab.size()==0) codLstTab.add("AG_LAV");//In caso di ritorno alla pagina dopo l'inserimento
//if( codLstTab==null) codLstTab = new Vector(0);
int cdnUt = user.getCodut();
String cdnParUtente = Integer.toString(cdnUt);
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
String strTipoContatto = serviceRequest.containsAttribute("SCADENZIARIO")? serviceRequest.getAttribute("SCADENZIARIO").toString():"";
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
String dataDalSlot = serviceRequest.containsAttribute("dataDalSlot") ? ((String) serviceRequest.getAttribute("dataDalSlot")) : "";
String codServizio = serviceRequest.containsAttribute("CODSERVIZIO") ? ((String) serviceRequest.getAttribute("CODSERVIZIO")) : "";
String prgspi = serviceRequest.containsAttribute("PRGSPI") ? ((String) serviceRequest.getAttribute("PRGSPI")) : "";

String datInizioIscr = serviceRequest.containsAttribute("DATINIZIOISCR") ? ((String) serviceRequest.getAttribute("DATINIZIOISCR")) : "";
String datStimata = serviceRequest.containsAttribute("DATSTIMATA") ? ((String) serviceRequest.getAttribute("DATSTIMATA")) : "";
String codEsito = serviceRequest.containsAttribute("CODESITO") ? ((String) serviceRequest.getAttribute("CODESITO")) : "";
String codAzione = serviceRequest.containsAttribute("CODAZIONE") ? ((String) serviceRequest.getAttribute("CODAZIONE")) : "";

User userCurr = (User) sessionContainer.getAttribute(User.USERID);
Calendar today =  Calendar.getInstance();
//String todayDate = today.get(DAY_OF_MONTH+"/"+today.MONTH+"/"+today.YEAR;
String todayDate = "";
if (today.get(Calendar.DAY_OF_MONTH)<10) todayDate +="0";
todayDate += today.get(Calendar.DAY_OF_MONTH)+"/";

if ( (today.get(Calendar.MONTH)+1)<10) todayDate += "0";
todayDate += (today.get(Calendar.MONTH)+1) + "/"+today.get(Calendar.YEAR);

if(dataDalSlot.equals(""))
{ dataDalSlot = todayDate;
}

PageAttribs attributi = new PageAttribs(user, _page);
boolean filtro = true; //attributi.containsButton("FILTRA");


String htmlStreamTop = StyleUtils.roundTopTable(filtro);
String htmlStreamBottom = StyleUtils.roundBottomTable(filtro);

String messScad = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE_SCAD");
String messRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE_ROSA");

String listPageRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE_ROSA");
String listPageScad = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE_SCAD");

String token = "";
String urlDiLista = "";

String tipoGruppo = user.getCodTipo();
boolean operatorePatronato = false;
String moduleNameServizio = "COMBO_SERVIZIO_SCAD";
if (tipoGruppo.equalsIgnoreCase(MessageCodes.Login.COD_TIPO_GRUPPO_PATRONATO)) {
	operatorePatronato = true;
	moduleNameServizio = "COMBO_SERVIZIO_PATRONATO_SCAD";
}

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
  <title>Appuntamenti</title>
  <script language="Javascript">
  <!--

	function setParentWindowLocation(newLocation) {
    	if (isInSubmit()) return;
	    window.parent.frames['ScadInferiore'].prepareSubmit();
	    prepareSubmit();
		window.parent.location = newLocation;
	}


    function conferma(azione,strPage){
    	// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
    
      if (azione=="BACK"){
		switch (strPage) {
	    case "DispInsRosaPage": 
			document.frmNuovoAppuntamento.MESSAGE.disabled = false;
			document.frmNuovoAppuntamento.LIST_PAGE.disabled = false;
			document.frmNuovoAppuntamento.MESSAGE.value = document.frmNuovoAppuntamento.MESSAGE_ROSA.value;
			document.frmNuovoAppuntamento.LIST_PAGE.value = document.frmNuovoAppuntamento.LIST_PAGE_ROSA.value;
			document.frmNuovoAppuntamento.MESSAGE_ROSA.disabled = true;
			document.frmNuovoAppuntamento.LIST_PAGE_ROSA.disabled = true;
			document.frmNuovoAppuntamento.MESSAGE_SCAD.disabled = true;
			document.frmNuovoAppuntamento.LIST_PAGE_SCAD.disabled = true;
			break;
		case "StatoOccRisultRicercaPage":
			<%if (sessionContainer!=null){
			  token = "_TOKEN_" + "STATOOCCRISULTRICERCAPAGE";
			  urlDiLista = "AdapterHTTP?" + (String)sessionContainer.getAttribute(token.toUpperCase());
			 }%>
            setParentWindowLocation("<%=urlDiLista%>");
            return;
		default:
			document.frmNuovoAppuntamento.MESSAGE.disabled = false;
			document.frmNuovoAppuntamento.LIST_PAGE.disabled = false;
			document.frmNuovoAppuntamento.MESSAGE.value = document.frmNuovoAppuntamento.MESSAGE_SCAD.value;
			document.frmNuovoAppuntamento.LIST_PAGE.value = document.frmNuovoAppuntamento.LIST_PAGE_SCAD.value;
			document.frmNuovoAppuntamento.MESSAGE_ROSA.disabled = true;
			document.frmNuovoAppuntamento.LIST_PAGE_ROSA.disabled = true;
			document.frmNuovoAppuntamento.MESSAGE_SCAD.disabled = true;
			document.frmNuovoAppuntamento.LIST_PAGE_SCAD.disabled = true;
	        break;
	    }
        document.frmNuovoAppuntamento.PAGE.value=strPage;
      } else {
      	document.frmNuovoAppuntamento.MESSAGE.disabled = true;
		document.frmNuovoAppuntamento.LIST_PAGE.disabled = true;
		document.frmNuovoAppuntamento.MESSAGE_ROSA.disabled = false;
		document.frmNuovoAppuntamento.LIST_PAGE_ROSA.disabled = false;
		document.frmNuovoAppuntamento.MESSAGE_SCAD.disabled = false;
		document.frmNuovoAppuntamento.LIST_PAGE_SCAD.disabled = false;
      }
      doFormSubmit(document.frmNuovoAppuntamento);
    }

    function SettaAzione(prgSlot,dataslot) {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

      var strCaption1 = "";
      var strCaption2 = "";
      if (document.frmNuovoAppuntamento.LEGA_APP_PATTO != null) {
        strCaption1 = "Salvare appuntamento da slot legandolo al patto corrente ?";
        strCaption2 = "Data appuntamento maggiore della data scadenza, salvare comunque legandolo al patto corrente ?";
      }
      else {
        strCaption1 = "Salvare appuntamento da slot ?";
        strCaption2 = "Data appuntamento maggiore della data scadenza, salvare comunque ?";
      }
      var annoScadenza,meseScadenza,giornoScadenza,dataScadenza;
      var annoSlot = dataslot.substr(6,4);
      var meseSlot = dataslot.substr(3,2);
      var giornoSlot = dataslot.substr(0,2);
      var dataSlot = parseInt(annoSlot + meseSlot + giornoSlot,10);
      if (document.frmNuovoAppuntamento.DATASCADENZA.value != "") {
        annoScadenza = document.frmNuovoAppuntamento.DATASCADENZA.value.substr(6,4);
        meseScadenza = document.frmNuovoAppuntamento.DATASCADENZA.value.substr(3,2);
        giornoScadenza = document.frmNuovoAppuntamento.DATASCADENZA.value.substr(0,2);
        dataScadenza = parseInt(annoScadenza + meseScadenza + giornoScadenza,10);
      }

      if (dataSlot > dataScadenza && '<%=strTipoContatto%>' != 'VER1' && '<%=strTipoContatto%>' != 'VER2') {
        if (confirm(strCaption2)) {
       	  document.frmNuovoAppuntamento.PAGE.value = "ScadSalvaAppuntamentoPage";
          document.frmNuovoAppuntamento.prgParSlot.value = prgSlot;
          doFormSubmit(document.frmNuovoAppuntamento); 
        }
      }
      else {
        if (confirm(strCaption1)) {
       	  document.frmNuovoAppuntamento.PAGE.value = "ScadSalvaAppuntamentoPage";
          document.frmNuovoAppuntamento.prgParSlot.value = prgSlot;
          doFormSubmit(document.frmNuovoAppuntamento);
        }
      }
    }
function toDate(newDate) {
    var tokens = newDate.split('/');
    var usDate= tokens[2]+"/"+tokens[1]+"/"+tokens[0];
    return new Date(usDate);
}

function checkDate(objData1, objData2) {
  ok=true;
  strData1=objData1.value;
  strData2=objData2.value;
	if (toDate(strData2).getTime()<toDate(strData1).getTime()) {
		alert("La "+ objData2.title +" è precedente alla "+ objData1.title);
      objData2.focus();
      ok=false;

	}
	return ok;
}

function filtra()
{ 
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;

  var dataSlot = document.frmNuovoAppuntamento.dataDalSlot;
  if((dataSlot != null) && (dataSlot !=  "")) 
  { 
  	if(!controllaFunzTL()) return;
  /*
    var dataDiOggi = new Date();
    var gg = dataDiOggi.getDate();
    var mm = dataDiOggi.getMonth()+1;
    var yyyy = dataDiOggi.getYear();
    
    var oggi = new Object(gg+"/"+mm+"/"+yyyy);
    */
    dataSlot = document.frmNuovoAppuntamento.dataDalSlot;
    if(!checkDate(document.frmNuovoAppuntamento.dataDiOggi,dataSlot)) {
         undoSubmit();
         return;
    }
  }
  document.frmNuovoAppuntamento.PAGE.value = "ScadAppuntamentoPage";
  doFormSubmit(document.frmNuovoAppuntamento);
}
-->
</script>
</head>
<body class="gestione">
<font color="red">
  <af:showErrors/>
</font>
<font color="green">
</font>
<af:form name="frmNuovoAppuntamento" onSubmit="filtra()" action="AdapterHTTP" method="POST" target="_parent">
<%
if ((strCdnLavoratore.compareTo("") != 0) && (strTipoContatto.equals("AMM4"))) {
%>
  <input type="hidden" name="LEGA_APP_PATTO" value="S">
<%
}
%>
<input type="hidden" name="dataDiOggi" value="<%=todayDate%>" title="data di oggi">
<input type="hidden" name="prgParSlot" value="">
<input type="hidden" name="CODCPI" value="<%=codCpi%>">
<input type="hidden" name="codParCpi" value="<%=codCpi%>">
<input type="hidden" name="PAGE" value="">
<input type="hidden" name="PAGEPROVENIENZA" value="<%=_pageProvenienza%>">
<input type="hidden" name="PRGUNITA" value="<%=strPrgUnita%>">
<input type="hidden" name="PRGAZIENDA" value="<%=strPrgAzienda%>">
<input type="hidden" name="CodCpiApp" value="<%=CodCpiApp%>">

<input type="hidden" name="CDNLAVORATORE" value="<%=strCdnLavoratore%>">
<input type="hidden" name="cdnParUtente" value="<%=cdnParUtente%>">
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
<input type="hidden" name="DATAAL" value="<%=data_al%>">
<input type="hidden" name="DATADAL" value="<%=data_dal%>">
<input type="hidden" name="DIREZIONE" value="<%=strDirezione%>">
<input type="hidden" name="MOTIVO_CONTATTO" value="<%=strDescMotivoContatto%>"/>
<input type="hidden" name="DATASCADENZA" value="<%=datascadenza%>">
<input type="hidden" name="FILTRALISTA" value="<%=strFiltra%>">
<input type="hidden" name="SCADENZIARIO" value="<%=strTipoContatto%>"/>
<input type="hidden" name="CODTIPOVALIDITA" value="<%=strCodTipoVal%>">
<input type="hidden" name="FLGPATTO" value="<%=strPatto297%>">
<input type="hidden" name="statoValCV" value="<%=strStatoValCV%>">
<input type="hidden" name="OpzioneCV" value="<%=OpzioneCV%>">

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
<%if(fromPattoAzioni){%>
  <input type="hidden" name="PATTO_AZIONI" value="true">
  <input type="hidden" name="statoSezioni" value="<%=statoSezioni%>">
  <input type="hidden" name="NONFILTRARE" value="<%=nonFiltrare%>">
  <%if(serviceRequest.getAttribute("PAGECHIAMANTE")==null){%>
    <input type="hidden" name="pageChiamante" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"PAGECHIAMANTE")%>">
  <%} else {%>
    <input type="hidden" name="pageChiamante" value="PattoAzioniLinguettaPage">
    <%}%>
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

<input type="hidden" name="DATINIZIOISCR" value="<%=datInizioIscr%>">
<input type="hidden" name="DATSTIMATA" value="<%=datStimata%>">
<input type="hidden" name="CODESITO" value="<%=codEsito%>">
<input type="hidden" name="CODAZIONE" value="<%=codAzione%>">

<table class="main" border="0">
<tr valign="top">
 <td>
 <p class="titolo">Nuovo Appuntamento</p>
 <%out.print(htmlStreamTop);%>
<table>

<tr>
 <td class="etichetta" nowrap>Dalla data </td>
 	<td class="campo" nowrap><af:textBox type="date" 
 				name="dataDalSlot" 
 				value="<%=dataDalSlot%>" 
 				title=" data di inizio 'Dalla data'" 
 				readonly="<%=String.valueOf(!filtro)%>" 
 				validateOnPost="true" 
 				maxlength="10" 
 				size="11"/></td>
</tr>
<tr> 				
 <td class="etichetta" nowrap ><%=labelServizio %></td>
      <td class="campo"><af:comboBox name="codServizio"
                   moduleName="<%=moduleNameServizio%>"
                   selectedValue="<%=codServizio%>"
                   title="<%=labelServizio %>"
                   classNameBase="input"
                   required="false" addBlank="true"
                   disabled="<%=String.valueOf(!filtro)%>"/>
    
  </td>
 </tr>
<%if(!operatorePatronato) {%>
<tr>
	<td class="etichetta">Operatore</td>
		<td class="campo" ><af:comboBox name="PRGSPI"
                 size="1"
                 title="Scelta Operatore"
                 multiple="false"
                 selectedValue="<%=prgspi%>"
                 required="false"
                 focusOn="false"
                 moduleName="COMBO_SPI_SCAD"
                 addBlank="true"                 
                 blankValue=""
                 disabled="<%=String.valueOf(!filtro)%>"/>
    </td>
</tr>
<%}%>

<%if(filtro) {%>
	<tr>
	<td colspan="2" align="center">   
     	<input type="button" class="pulsanti" name="Filtra" value="Filtra" onClick="filtra();">
    </td>
   	</tr>
<%}%>

</table>
<%out.print(htmlStreamBottom);%>
 
 </td>
 <td>
 <table>
<tr><td>
<af:list moduleName="M_GetSlotPrenotabili" jsSelect="SettaAzione"/>
<%if((_pageProvenienza != null) && !_pageProvenienza.equals("") && !_pageProvenienza.equals("null")){%>
    <center><input type="button" class="pulsanti" name="ANNULLA" value="Chiudi" onClick="javascript:conferma('BACK','<%=_pageProvenienza%>');"></center>
<%}%>
</td>
</tr>
</table>

</td>
</tr>
</table>
    
</af:form>
</body>
</html>