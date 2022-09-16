<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
   
  it.eng.sil.util.*, 
  it.eng.afExt.utils.StringUtils,
  java.lang.*,
  java.text.*, 
  java.util.*,
  java.math.*,
  it.eng.sil.security.*"%>

<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ include file="../global/Function_CommonRicercaComune.inc" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
boolean flag_insert_testata=serviceRequest.containsAttribute("inserisci");
boolean flag_insert_unita=serviceRequest.containsAttribute("insert_unita");
boolean flag_insert_unita_step2=serviceRequest.containsAttribute("insert_unita_step2");

String pagina_back=(String) serviceRequest.getAttribute("ret");
/* paramentro utilizzato quando dai movimenti inserisco una nuova azienda */
String funzioneaggiornamento = serviceRequest.containsAttribute("AGG_FUNZ")?serviceRequest.getAttribute("AGG_FUNZ").toString():"";
/* paramentro utilizzato quando dai movimenti inserisco una nuova sede aziendale */
String funzioneaggiornamentounita = serviceRequest.containsAttribute("AGG_FUNZ_INS_UNITA")?serviceRequest.getAttribute("AGG_FUNZ_INS_UNITA").toString():"";
String _pageSubmit = "IdoUnitaAziendaPage";

SourceBean aziendaRow=null;
SourceBean unitaRow=null;

//Testata azienda
String prgAzienda="";
String strCodiceFiscale="";
String strPartitaIva="";
String strRagioneSociale="";


String prgUnita="";
String strDenominazioneAz="";
String strIndirizzo="";
String strLocalita="";
String strCap="";
String codCom="";
String desComune="";
String desComuneMovimenti="";
String flgMezziPub="";
String codAzStato="";
String codAteco="";
String tipo_ateco="";
String strDesAteco="";
String strDesTipoAzienda="";
String codCCNL="";
String desCCNL="";
String strResponsabile="";
String strReferente="";
String strTel="";
String strFax="";
String strEmail="";
String strPECemail = "";
String datInizio="";
String datFine="";
String strNote="";
String flgSede="";
boolean isSedeLegale = false;
String numREA="";
String strFlgDatiOK="";
String codNatGiuridica="";
String cdnUtins="";
String dtmins="";
String cdnUtmod="";
String dtmmod="";
String prgMov = "";
String codTipoAzienda = "";
String strCodCCNL = "";
String strpatinail = "";
String strDenominazione = "";
String strNota = "";
String strLuogoRif = "";
String codComNas = "";
String strCodRif="";
String strCodRif2="";
String strMailPubbl="";
String flgStandard="";
String cdnTipoGruppo="";


String codCPI = "";

String strnumeroinps = "";
String descNatGiuridica = "";
String strDescCodCCNL = "";

String posInps = "";
String posInps1 = "";
String posInps2 = "";
String strNumRegistroCommitt = "";
String datRegistroCommit = "";
String strRepartoInps = "";
String strRiferimentoSare = "";

String ragSocSommEstera = "";
String cfSommEstera = ""; 

BigDecimal numKloUnitaAzienda=null;

Testata operatoreInfo=null;

boolean esitoInserimentoUnita = true;

int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
prgAzienda=(flag_insert_testata)? serviceResponse.getAttribute("M_INSERTTESTATAAZIENDA.ROWS.ROW.PRGAZIENDA").toString():(String)serviceRequest.getAttribute("prgAzienda");
aziendaRow= (SourceBean) serviceResponse.getAttribute("M_GetTestataAzienda.ROWS.ROW");

/* Gestione di ritorno alla pagina di inserimento unita in caso di fallito inserimento */
if(serviceResponse.containsAttribute("M_INSERTUNITAAZIENDA.INSERT_OK")){
  if(serviceResponse.getAttribute("M_INSERTUNITAAZIENDA.INSERT_OK").equals("FALSE")){
    esitoInserimentoUnita = false;
  }
}
 
if (aziendaRow == null ) {%>
  <%@ include file="GestioneOggettoTestataAz.inc" %> 
<%
  NavigationCache objTestata = null;
  objTestata = (NavigationCache) sessionContainer.getAttribute("AZIENDACORRENTE");
  if (objTestata != null) {
    objTestata.setFieldsFromSourceBean (serviceRequest);
    serviceRequest.setAttribute ((SourceBean)serviceResponse.getAttribute("M_InsertTestataAzienda"));
  }
%>
  <af:form name="TornaIndietro" method="POST" action="AdapterHTTP" dontValidate="true">
  <input type="hidden" name="PAGE" value="IdoTestataAziendaPage"/>
  <input type="hidden" name="cdnFunzione" value="<%= _funzione %>"/> 
  <input type="hidden" name="inserisci" value=""/>  
  </af:form>
  <script language="javascript">
  doFormSubmit(document.TornaIndietro);
  </script>
<%
}
else {  
  strCodiceFiscale= StringUtils.getAttributeStrNotNull(aziendaRow, "strCodiceFiscale");
  strPartitaIva=StringUtils.getAttributeStrNotNull(aziendaRow, "strPartitaIva");
  strRagioneSociale=StringUtils.getAttributeStrNotNull(aziendaRow, "strRagioneSociale");
  strFlgDatiOK=StringUtils.getAttributeStrNotNull(aziendaRow, "FLGDATIOK");
  codNatGiuridica=StringUtils.getAttributeStrNotNull(aziendaRow, "CODNATGIURIDICA");
  descNatGiuridica=StringUtils.getAttributeStrNotNull(aziendaRow, "descNatGiuridica");
  codTipoAzienda=StringUtils.getAttributeStrNotNull(aziendaRow, "CODTIPOAZIENDA");
  strDesTipoAzienda=StringUtils.getAttributeStrNotNull(aziendaRow, "DESCTIPOAZIENDA");
  strCodCCNL=StringUtils.getAttributeStrNotNull(aziendaRow, "CODCCNL");
  strDescCodCCNL=StringUtils.getAttributeStrNotNull(aziendaRow, "DESCODCCNL");
  strpatinail=StringUtils.getAttributeStrNotNull(aziendaRow, "strpatinail");
}

prgMov = serviceRequest.containsAttribute("PRGMOVIMENTO")?serviceRequest.getAttribute("PRGMOVIMENTO").toString():"";
if (!(flag_insert_testata || flag_insert_unita)) {

  if(flag_insert_unita_step2){
    SourceBean insertUnitaAziendaSb = (SourceBean)serviceResponse.getAttribute("M_INSERTUNITAAZIENDA.ROWS.ROW");
    if ((insertUnitaAziendaSb!=null) && insertUnitaAziendaSb.containsAttribute("PRGUNITA")) {
      prgUnita = ((BigDecimal)insertUnitaAziendaSb.getAttribute("PRGUNITA")).toString();
    }
  } else {    
        prgUnita=(String) serviceRequest.getAttribute("prgUnita");
    }
    
  unitaRow=(SourceBean) serviceResponse.getAttribute("M_GetUnitaAzienda.ROWS.ROW");

  if(unitaRow == null){ unitaRow = serviceRequest; }

  strDenominazioneAz=StringUtils.getAttributeStrNotNull(unitaRow, "strDenominazioneAz");
  strIndirizzo=StringUtils.getAttributeStrNotNull(unitaRow, "strIndirizzo");
  strLocalita=StringUtils.getAttributeStrNotNull(unitaRow, "strLocalita");
  codCom=StringUtils.getAttributeStrNotNull(unitaRow, "codCom");
  strCap=StringUtils.getAttributeStrNotNull(unitaRow, "strCap");
  flgMezziPub=StringUtils.getAttributeStrNotNull(unitaRow, "flgMezziPub");

  if(!esitoInserimentoUnita){
  //recupero dati dalla request in seguito al fallimento dell'inserimento dell'unita
      desComune=StringUtils.getAttributeStrNotNull(unitaRow, "desComune");
  } else {
      //recupero dati dal db
      desComune=StringUtils.getAttributeStrNotNull(unitaRow, "strDenominazione");
      desComuneMovimenti=desComune;
      desComune=(!desComune.equals(""))?desComune+" ("+StringUtils.getAttributeStrNotNull(unitaRow, "provincia")+")":"";
    }
  codAzStato=StringUtils.getAttributeStrNotNull(unitaRow, "codAzStato");

  codAteco=StringUtils.getAttributeStrNotNull(unitaRow, "codAteco");
  if(!esitoInserimentoUnita){
    //recupero dati dalla request in seguito al fallimento dell'inserimento dell'unita
      tipo_ateco =StringUtils.getAttributeStrNotNull(unitaRow, "strTipoAteco");  
      strDesAteco=StringUtils.getAttributeStrNotNull(unitaRow, "strAteco");
  } else {
      //recupero dati dal db
      tipo_ateco =StringUtils.getAttributeStrNotNull(unitaRow, "tipo_ateco");  
      strDesAteco=StringUtils.getAttributeStrNotNull(unitaRow, "strDesAteco");
    }  
    
    
  codCCNL=StringUtils.getAttributeStrNotNull(unitaRow, "codCCNL");
  desCCNL=StringUtils.getAttributeStrNotNull(unitaRow, "desCCNL");
  codCPI=StringUtils.getAttributeStrNotNull(unitaRow, "CODCPI");
    
  strResponsabile =StringUtils.getAttributeStrNotNull(unitaRow, "strResponsabile");
  strReferente =StringUtils.getAttributeStrNotNull(unitaRow, "strReferente");
  
  strnumeroinps =StringUtils.getAttributeStrNotNull(unitaRow, "strnumeroinps");
  
  ragSocSommEstera = StringUtils.getAttributeStrNotNull(unitaRow, "RAGSOCAZESTERA");
  cfSommEstera = StringUtils.getAttributeStrNotNull(unitaRow, "CODFISCAZESTERA");
  
  strTel=StringUtils.getAttributeStrNotNull(unitaRow, "strTel");
  strFax=StringUtils.getAttributeStrNotNull(unitaRow, "strFax");
  strEmail=StringUtils.getAttributeStrNotNull(unitaRow, "strEmail");
  strPECemail = StringUtils.getAttributeStrNotNull(unitaRow, "strPECemail");
  
  datInizio=StringUtils.getAttributeStrNotNull(unitaRow, "datInizio");
  datFine=StringUtils.getAttributeStrNotNull(unitaRow, "datFine");

  strNote=StringUtils.getAttributeStrNotNull(unitaRow, "strNote");
  flgSede=StringUtils.getAttributeStrNotNull(unitaRow, "flgSede");
  if (flgSede.equalsIgnoreCase("S")) {
  	isSedeLegale = true;
  }
  numREA=unitaRow.containsAttribute("strREA") ? unitaRow.getAttribute("strREA").toString() : "";
  
  cdnUtins= unitaRow.containsAttribute("cdnUtins") ? unitaRow.getAttribute("cdnUtins").toString() : "";
  dtmins=unitaRow.containsAttribute("dtmins") ? unitaRow.getAttribute("dtmins").toString() : "";
  cdnUtmod=unitaRow.containsAttribute("cdnUtmod") ? unitaRow.getAttribute("cdnUtmod").toString() : "";
  dtmmod=unitaRow.containsAttribute("dtmmod") ? unitaRow.getAttribute("dtmmod").toString() : "";
  
  /* Gestione ritorno alla pagina in caso di errore nell'inserimento */
  if(unitaRow.containsAttribute("numKloUnitaAzienda") && !unitaRow.getAttribute("numKloUnitaAzienda").equals("null") && !unitaRow.getAttribute("numKloUnitaAzienda").equals("")){
    numKloUnitaAzienda = (BigDecimal) unitaRow.getAttribute("numKloUnitaAzienda");
    numKloUnitaAzienda=numKloUnitaAzienda.add(new BigDecimal(1));
  } else {
        numKloUnitaAzienda = null;
    }

  if(!esitoInserimentoUnita){
    //recupero dati dalla request in seguito al fallimento dell'inserimento dell'unita  
    posInps = StringUtils.getAttributeStrNotNull(unitaRow, "strPosInps1") + StringUtils.getAttributeStrNotNull(unitaRow, "strPosInps2");
  } else {
      //recupero dati dal db
      posInps = StringUtils.getAttributeStrNotNull(unitaRow, "strNumeroInps");
    }
  strNumRegistroCommitt = StringUtils.getAttributeStrNotNull(unitaRow, "STRNUMREGISTROCOMMITT"); 
  strRepartoInps = StringUtils.getAttributeStrNotNull(unitaRow, "strRepartoInps"); 
  strRiferimentoSare = StringUtils.getAttributeStrNotNull(unitaRow, "strRiferimentoSare"); 
  datRegistroCommit = StringUtils.getAttributeStrNotNull(unitaRow, "datRegistroCommit"); 
  //Gestione spezzatino posizione Inps
  posInps1 = posInps.substring(0, (posInps.length() >= 2 ? 2 : posInps.length()));
  posInps2 = posInps.substring((posInps.length() >= 2 ? 2 : posInps.length()), (posInps.length() >= 15 ? 15 : posInps.length()));  

  //InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
  operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);

 // NOTE: Attributi della pagina (pulsanti e link) 

 }

	ProfileDataFilter filter = new ProfileDataFilter(user, "IdoUnitaAziendaPage");
  boolean canView = false;

  if ( !(flag_insert_testata || flag_insert_unita || flag_insert_unita_step2) ) {
    filter.setPrgAzienda(new BigDecimal(prgAzienda));
    filter.setPrgUnita(new BigDecimal(prgUnita));
    canView=filter.canViewUnitaAzienda();
  } else {
    canView = true; // sono in inserimento, la visibilità dei dati non ha senso
  }
  
	if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
    
    	PageAttribs attributi = new PageAttribs(user, "IdoUnitaAziendaPage");
		boolean canInsert = attributi.containsButton("inserisci");
    	boolean canModify = attributi.containsButton("aggiorna");
    	boolean canDelete= attributi.containsButton("cancella");
   		boolean canCreaGruppo= attributi.containsButton("GRUPPO");
   		boolean canEsterno = attributi.containsButton("ESTERNO");
    
	if ( !canModify && !canInsert && !canDelete ) {
    } else {
    	
    	boolean canEdit = false;

      if ( !(flag_insert_testata || flag_insert_unita || flag_insert_unita_step2) ) {
        canEdit = filter.canEditUnitaAzienda();
      } else { 
        canEdit = true;   // sono in inserimento, la visibilità dei dati non ha senso
      }

      if ( !canEdit ) {
        canInsert = false;
        canModify = false;
        canDelete = false;
      }
    }

 
 String htmlStreamTop = StyleUtils.roundTopTable(canModify);
 String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
 /* Gestione di ritorno alla pagina di inserimento unita in caso di fallito inserimento */
 if(!esitoInserimentoUnita){
  flag_insert_unita = true;
 }
 
 String mode="";
 String accountAzienda="";
 String accountUnita="";
 String ragSociale = "";
 
 SourceBean rowsAzienda = (SourceBean) serviceResponse.getAttribute("M_GetAccontAzienda.ROWS.ROW");
  	if (rowsAzienda != null) {
  		accountAzienda = StringUtils.getAttributeStrNotNull(rowsAzienda, "strcodrif");
  		accountUnita = StringUtils.getAttributeStrNotNull(rowsAzienda, "strcodrif2");
  		ragSociale = StringUtils.getAttributeStrNotNull(rowsAzienda, "ragSociale");
  	}
%>

<html>

<head>
  <title>Azienda</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  
  <SCRIPT language="JavaScript" src="../../js/Function_CommonRicercaCCNL.js"></SCRIPT>

<SCRIPT TYPE="text/javascript">
<!--
// Per rilevare la modifica dei dati da parte dell'utente
var flagChanged = false;  

function cambia(immagine, sezione) {

	if (sezione.aperta==null) sezione.aperta=false;
	if (sezione.aperta) {
		sezione.style.display="none";
		sezione.aperta=false;
		immagine.src="../../img/chiuso.gif";
	}
	else {
		sezione.style.display="";
		sezione.aperta=true;
		immagine.src="../../img/aperto.gif";
	}
}

function fieldChanged() {
  <% if ( canModify ) { %> 
    flagChanged = true;
  <% } %> 
}
/*
function avanti(){
   if (flagChanged==true){
         if (!confirm("I dati sono cambiati.\r\nProcedere lo stesso ?")){
              return; 
         } 
    } 
}
*/
function go(url, alertFlag) {
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;

  var _url = "AdapterHTTP?" + url;
  if (flagChanged) {
	  if (confirm("I dati sono cambiati.\r\nProcedere lo stesso ?")){
          setWindowLocation(_url);
         } 
  }
  else  {
	  if (alertFlag == 'TRUE') {
	    if (confirm('Confermi operazione')) {
	      setWindowLocation(_url);
	    }
	  } else{
	    setWindowLocation(_url);
	  }
  }
}


function selectATECO_onClick(codAteco, codAtecoHid, strAteco, strTipoAteco) {	
  if (codAteco.value == "") {
    strAteco.value = "";
    strTipoAteco.value = "";
  }
  else {
  	var w=1000; var l=((screen.availWidth)-w)/2;
  	var h=750; var t=((screen.availHeight)-h)/2;
  	var feat = "toolbar=0, scrollbars=1,height="+h+",width="+w+",top="+t+",left="+l;
	var opened=window.open("AdapterHTTP?PAGE=RicercaAtecoPage&codAteco="+codAteco.value, "Attività", feat);
	opened.focus();
    //if (codAteco.value!=codAtecoHid.value) {
    //  window.open("AdapterHTTP?PAGE=RicercaAtecoPage&codAteco="+codAteco.value, "Attività", "toolbar=0, scrollbars=1");
    //}
  }
}

function ricercaAvanzataAteco() {
		var w=1000; var l=((screen.availWidth)-w)/2;
  		var h=750; var t=((screen.availHeight)-h)/2;
  		var feat = "toolbar=0, scrollbars=1,height="+h+",width="+w+",top="+t+",left="+l;
		var opened=window.open("AdapterHTTP?PAGE=RicercaAtecoAvanzataPage", "Attività", feat);
		opened.focus();
  //window.open("AdapterHTTP?PAGE=RicercaAtecoAvanzataPage", "Attività", "toolbar=0, scrollbars=1");
}


// <CCNL>
function ricercaAvanzataCCNL() {
  window.open("AdapterHTTP?PAGE=RicercaCCNLAvanzataPage", "CCNL", 'toolbar=0, scrollbars=1, height=300, width=550');
}

function codCCNLUpperCase(inputName) {
  var ctrlObj = eval("document.forms[0]." + inputName);
  eval("document.forms[0]."+inputName+".value=document.forms[0]."+inputName+".value.toUpperCase();");
  return true;
}
// </CCNL>




function unificaPosInps() {
  // GG 1/12/2004, NO: document.getElementById("STRPOSINPS").value = document.getElementById("STRPOSINPS1").value + document.getElementById("STRPOSINPS2").value;
  document.Frm1.STRPOSINPS.value = document.Frm1.STRPOSINPS1.value + document.Frm1.STRPOSINPS2.value;
}


function controllaValiditaCampi(form, pulsante){

 var isOk = true;
  
  if((form.codCom.value == "") && isOk){
    alert("Non è stato valorizzato il campo comune");
    isOk = false;
  }
  
  if(isOk){
    var pulsanteUnico = "?" + pulsante.name + "=&";
    if (form.action.indexOf(pulsanteUnico) == -1) {
    	form.action = form.action + pulsanteUnico;
    }
    //form.action = form.action + "?" + pulsante.name + "=&";   // CODICE ERRATO!!!
    if(controllaFunzTL()) { doFormSubmit(form); }
  } 
}

function validateInps(inputName) {
  var ctrlObj = eval("document.forms[0]." + inputName);
  if (ctrlObj.value == "")
    return true;    // va bene se non ho inserito nulla
  else {
    var ok = isInteger(ctrlObj.value);

    if (!ok) {
      alert("Numero non corretto nel campo " + ctrlObj.title);
      ctrlObj.focus();
    }
    if( document.getElementById('TBL1').style.display=="none" ){
	    cambia(document.getElementById('tendinaAltreInfo'),document.getElementById('TBL1'));
	}
    return ok;
  }
}

function CreaGruppo(){
 	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;
	
	var url = "AdapterHTTP?PAGE=ProfSalvaGruppoPage";
    	url += "&cdnFunzione=4";
    	url += "&cdnTipoGruppo=20";
    	url += "&strDenominazione="+"<%=StringUtils.formatValue4Javascript(ragSociale)%>";
    	url += "&strNota="+"<%=StringUtils.formatValue4Javascript(strDenominazioneAz)%>";
    	url += "&flgStandard=N"    	
    	url += "&strLuogoRif="+"<%=StringUtils.formatValue4Javascript(strIndirizzo)%>";
    	url += "&codComNas="+"<%=codCom%>";
    	url += "&strCodRif="+"<%=prgAzienda%>";
    	url += "&strCodRif2="+"<%=prgUnita%>";
    	url += "&mode=new";
    	setWindowLocation(url);
}

function accessoEsterno(){
	if (isInSubmit()) return;
	
	var url = "AdapterHTTP?PAGE=AppEsternePage";
    	url += "&cdnFunzione="+"<%=_funzione%>";
    	url += "&prgAzienda="+"<%=prgAzienda%>";
    	url += "&prgUnita="+"<%=prgUnita%>";
    	url += "&ID=10";
   	
   	document.location = url;	
}

-->

</SCRIPT>

<script language="Javascript">
  <%if (funzioneaggiornamento.equals("") && funzioneaggiornamentounita.equals("")) {
    if (!prgUnita.equals("")) {%>
      if (window.top.menu != undefined){
        window.top.menu.caricaMenuAzienda(<%=_funzione%>, <%=prgAzienda%>, <%=prgUnita%>);
      }
<%  }
  }%>
 </script>

  <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
     	if (Utils.notNull(prgAzienda).length()>0 && Utils.notNull(prgUnita).length()>0)
	        attributi.showHyperLinks(out, requestContainer,responseContainer,"prgAzienda="+prgAzienda+"&prgUnita="+prgUnita);
      %>
  </script> 
</head>
<body class="gestione" onload="rinfresca();">
<%
if ((!(flag_insert_testata || flag_insert_unita)) && (!funzioneaggiornamento.equals(""))){
%>
  <af:form name="dati" method="POST" action="AdapterHTTP">
  <af:textBox type="hidden" name="PAGE" value="MovimentiRicercaRefreshPage"/>
  <af:textBox type="hidden" name="strRagioneSociale" value="<%=strRagioneSociale%>"/>
  <af:textBox type="hidden" name="STRPARTITAIVA" value="<%=strPartitaIva%>"/>
  <af:textBox type="hidden" name="STRCODICEFISCALEAZIENDA" value="<%=strCodiceFiscale%>"/>
  <af:textBox type="hidden" name="STRDENOMINAZIONEAZ" value="<%=strDenominazioneAz%>"/>
  <af:textBox type="hidden" name="STRINDIRIZZO" value="<%=strIndirizzo%>"/>
  <af:textBox type="hidden" name="comune_az" value="<%=desComuneMovimenti%>"/>
  <af:textBox type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"/>
  <af:textBox type="hidden" name="PRGUNITA" value="<%=prgUnita%>"/>
  <af:textBox type="hidden" name="codAteco" value="<%=codAteco%>"/>
  <af:textBox type="hidden" name="strDesAtecoUAz" value="<%=strDesAteco%>"/>
  <af:textBox type="hidden" name="codTipoAzienda" value="<%=codTipoAzienda%>"/>
  <af:textBox type="hidden" name="descrTipoAz" value="<%=strDesTipoAzienda%>"/>
  <af:textBox type="hidden" name="strPatInail" value="<%=strpatinail%>"/>
  <af:textBox type="hidden" name="strNumeroInps" value="<%=strnumeroinps%>"/>
  <af:textBox type="hidden" name="CCNLAZ" value="<%=codCCNL%>"/>
  <af:textBox type="hidden" name="STRDESCRIZIONECCNL" value="<%=desCCNL%>"/>
  <af:textBox type="hidden" name="FLGDATIOK" value="<%=strFlgDatiOK%>"/>
  <af:textBox type="hidden" name="AGG_FUNZ" value="<%=funzioneaggiornamento%>"/>
  <af:textBox type="hidden" name="natGiurAz" value="<%=descNatGiuridica%>"/>
  <af:textBox type="hidden" name="STRTEL" value="<%=strTel%>"/>
  <af:textBox type="hidden" name="STRFAX" value="<%=strFax%>"/>
  <af:textBox type="hidden" name="CODCPI" value="<%=codCPI%>"/>  
  </af:form>
  <script language="Javascript">
    doFormSubmit(document.dati);
 </script>
<%
}
else {
  if (!funzioneaggiornamentounita.equals("") && flag_insert_unita_step2) {
  %>
    <af:form name="dati" method="POST" action="AdapterHTTP">
    <af:textBox type="hidden" name="PAGE" value="MovimentiRicercaRefreshPage"/>
    <af:textBox type="hidden" name="strRagioneSociale" value="<%=strRagioneSociale%>"/>
    <af:textBox type="hidden" name="STRPARTITAIVA" value="<%=strPartitaIva%>"/>
    <af:textBox type="hidden" name="STRCODICEFISCALEAZIENDA" value="<%=strCodiceFiscale%>"/>
    <af:textBox type="hidden" name="STRDENOMINAZIONEAZ" value="<%=strDenominazioneAz%>"/>
    <af:textBox type="hidden" name="STRINDIRIZZO" value="<%=strIndirizzo%>"/>
    <af:textBox type="hidden" name="comune_az" value="<%=desComuneMovimenti%>"/>
    <af:textBox type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"/>
    <af:textBox type="hidden" name="PRGUNITA" value="<%=prgUnita%>"/>
    <af:textBox type="hidden" name="codAteco" value="<%=codAteco%>"/>
    <af:textBox type="hidden" name="strDesAtecoUAz" value="<%=strDesAteco%>"/>
    <af:textBox type="hidden" name="codTipoAzienda" value="<%=codTipoAzienda%>"/>
    <af:textBox type="hidden" name="descrTipoAz" value="<%=strDesTipoAzienda%>"/>
    <af:textBox type="hidden" name="strPatInail" value="<%=strpatinail%>"/>
    <af:textBox type="hidden" name="strNumeroInps" value="<%=strnumeroinps%>"/>
    <af:textBox type="hidden" name="CCNLAZ" value="<%=codCCNL%>"/>
    <af:textBox type="hidden" name="STRDESCRIZIONECCNL" value="<%=desCCNL%>"/>
    <af:textBox type="hidden" name="FLGDATIOK" value="<%=strFlgDatiOK%>"/>
    <af:textBox type="hidden" name="AGG_FUNZ" value="<%=funzioneaggiornamentounita%>"/>
    <af:textBox type="hidden" name="natGiurAz" value="<%=descNatGiuridica%>"/>
    <af:textBox type="hidden" name="STRTEL" value="<%=strTel%>"/>
    <af:textBox type="hidden" name="STRFAX" value="<%=strFax%>"/>
  	<af:textBox type="hidden" name="CODCPI" value="<%=codCPI%>"/>
    </af:form>
    <script language="Javascript">
      doFormSubmit(document.dati);
   </script>
  <%
  }
  else {
  %>
    <p align="center">
    <af:form name="Frm1" method="POST" action="AdapterHTTP">
    <input type="hidden" name="AGG_FUNZ_INS_UNITA" value="<%= funzioneaggiornamentounita %>">
      <%out.print(htmlStreamTop);%>
      <table class="main">
        <tr>
          <td colspan="2">
            <font color="green">
              <af:showMessages prefix="M_INSERTTESTATAAZIENDA" />
              <af:showMessages prefix="M_SaveUnitaAzienda" />
              <af:showMessages prefix="M_InsertUnitaAzienda" />
            </font>
            <font color="red">
              <af:showErrors/>
            </font>
          </td>
        </tr>
    <tr>
      <td colspan="2" class="campo">
          Azienda: <b><%=strRagioneSociale%></b><br/>
          Partita Iva: <b><%=strPartitaIva%></b><br/>
          Codice Fiscale: <b><%=strCodiceFiscale%></b><br/>
          <br/>
      </td>
    </tr>

      <tr valign="top">
        <td class="etichetta">Sede legale </td>
        <td class="campo">
          <af:comboBox classNameBase="input" name="flgSede" title="Sede" onChange="fieldChanged();" disabled="<%= String.valueOf(!canModify || (flgSede.equals(\"S\") &&  esitoInserimentoUnita)) %>"> <!--|| flgSede.equals(\"S\")-->
            <OPTION value=""></OPTION>
            <OPTION value="S" <%if (flgSede.equals("S")) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
            <OPTION value="N" <%if (flgSede.equals("N")) out.print("SELECTED=\"true\"");%>>No</OPTION>
          </af:comboBox>
        </td>
      </tr>

      <tr valign="top">
        <td class="etichetta">Denominazione</td>
        <td class="campo">
          <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strDenominazioneAz" size="50" maxlength="100" value="<%=strDenominazioneAz%>" readonly="<%= String.valueOf(!canModify) %>" />
        </td>
      </tr>
      
      <%if (!cfSommEstera.equals("")) {%>
       	<tr valign="top">
	        <td class="etichetta">C.F. Estera</td>
	        <td class="campo">
	          <af:textBox classNameBase="input" name="CODFISCAZESTERA" size="50" maxlength="100" value="<%=cfSommEstera%>" readonly="true" />
	        </td>
      	</tr>
      	<tr valign="top">
	        <td class="etichetta">Rag. Soc. Estera</td>
	        <td class="campo">
	          <af:textBox classNameBase="input" name="RAGSOCAZESTERA" size="50" maxlength="100" value="<%=ragSocSommEstera%>" readonly="true" />
	        </td>
      	</tr>
      <%}%>
      <tr valign="top">
        <td class="etichetta">Numero REA</td>
        <td class="campo">
          <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strREA" title="Numero REA" maxlength="11" size="12" value="<%=numREA%>" readonly="<%= String.valueOf(!canModify) %>" />
        </td>
      </tr>
      
      <tr valign="top">
        <td class="etichetta">Indirizzo </td>
        <td class="campo">
          <af:textBox classNameBase="input" title="Indirizzo unità azienda" onKeyUp="fieldChanged();" name="strIndirizzo" size="50" maxlength="60" value="<%=strIndirizzo%>" readonly="<%= String.valueOf(!canModify) %>" required="true"/>
        </td>
      </tr>

      <tr valign="top">
        <td class="etichetta">Localita</td>
        <td class="campo">
          <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strLocalita" size="50" maxlength="100" value="<%=strLocalita%>" readonly="<%= String.valueOf(!canModify) %>" />
        </td>
      </tr>
    
      <tr>
        <td class="etichetta">Comune</td>
        <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.codCom, document.Frm1.codComHid, document.Frm1.desComune, document.Frm1.desComuneHid, document.Frm1.strCap, document.Frm1.strCapHid , 'codice');" type="text" title="comune unità azienda" name="codCom" value="<%=codCom%>" size="4" maxlength="4" readonly="<%= String.valueOf(!canModify) %>" />&nbsp;
        <% if(canModify){%>
        <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codCom, document.Frm1.desComune, document.Frm1.strCap, 'codice','',null,'inserisciComUANonScaduto()');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
        <%}%>
        <af:textBox type="hidden" name="codComHid" value="<%=codCom%>" />
        <af:textBox classNameBase="input" title="comune unità azienda"  onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.codCom, document.Frm1.codComHid, document.Frm1.desComune, document.Frm1.desComuneHid, document.Frm1.strCap, document.Frm1.strCapHid, 'descrizione');" type="text" name="desComune" value="<%=desComune%>" size="30" maxlength="50" inline="
            onkeypress=\"if(event.keyCode==13) { event.keyCode=9; this.blur(); }\""
            readonly="<%= String.valueOf(!canModify) %>" required="true"/>&nbsp;
        <% if(canModify){%>
        <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codCom, document.Frm1.desComune, document.Frm1.strCap, 'descrizione','',null,'inserisciComUANonScaduto()');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
        <%}%>
        <af:textBox type="hidden" name="desComuneHid" value="<%=desComune%>" />&nbsp;
        </td>                
      </tr>   

      <tr valign="top">
        <td class="etichetta">Cap</td>
        <td class="campo">
          <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strCap" size="5" value="<%= strCap %>" readonly="<%= String.valueOf(!canModify) %>" maxlength="5" />
          <af:textBox type="hidden" name="strCapHid" value="<%= strCap %>" />&nbsp;
        </td>
      </tr>

      <tr valign="top">
        <td class="etichetta">Stato azienda</td>
        <td class="campo">
          <af:comboBox classNameBase="input" onChange="fieldChanged();" title="Stato azienda" name="codAzStato" selectedValue="<%=codAzStato%>" disabled="<%= String.valueOf(!canModify) %>" moduleName="M_GetStatiAzienda" addBlank="true" required="true" />
        </td>
      </tr>

      <tr valign="top">
        <td class="etichetta">Codice Attività</td>
        <td class="campo">
          <af:textBox classNameBase="input" name="codAteco" title="Codice di Attività" size="6" maxlength="6" 
          		value="<%=codAteco%>" validateOnPost="true"
          		readonly="<%= String.valueOf(!canModify) %>" required="true" 
          		onKeyUp="javascript:fieldChanged();
      				PulisciRicercaCCNL(document.Frm1.codAteco, document.Frm1.codAtecoHid, document.Frm1.strTipoAteco, document.Frm1.strAteco, 'codice');"
      		/>
          <af:textBox type="hidden" name="codAtecoHid" value="<%=codAteco%>" />        
          <% if (canModify) { %>
              <a href="javascript:selectATECO_onClick(document.Frm1.codAteco, document.Frm1.codAtecoHid, document.Frm1.strAteco,  document.Frm1.strTipoAteco);"><img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;&nbsp;
              <A href="javascript:ricercaAvanzataAteco();">
                  Ricerca avanzata
              </A>
          <%}%>
        </td>
      </tr> 
      <tr valign="top">
        <td class="etichetta">Tipo</td>
        <td class="campo">
          <af:textBox classNameBase="input" name="strTipoAteco" value="<%=tipo_ateco%>" readonly="true" size="80" maxlength="300"/>
        </td>
      </tr>

      <tr>
        <td class="etichetta">Attività</td>
        <td class="campo">
             <af:textBox classNameBase="input" name="strAteco" size="80" readonly="true" value="<%=strDesAteco%>" maxlength="300"/>
        </td>
      </tr>
      
      <tr>
        <td class="etichetta">Codice CCNL</td>
        <td class="campo" >
          <af:textBox classNameBase="input" title="Codice CCNL" onKeyUp="javascript:
          																	fieldChanged();
          																	PulisciRicercaCCNL(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, document.Frm1.strCCNLHid, 'codice');" 
   					  type="text" name="codCCNL" size="5" maxlength="4" validateWithFunction="codCCNLUpperCase"  value="<%=codCCNL%>"  
          			  readonly="<%= String.valueOf(!canModify) %>"  required="true" />&nbsp;
          <% if(canInsert) {%>
          <A HREF="javascript:btFindCCNL_onclick(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, document.Frm1.strCCNLHid, 'codice');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
          <A href="javascript:ricercaAvanzataCCNL();">Ricerca avanzata</A>
          <%}%>
          <af:textBox type="hidden" name="codCCNLHid" value="<%=codCCNL%>" />
        </td>
	    </tr>
      <tr>
      	<td class="etichetta">Tipo CCNL</td>
      	<td class="campo">
  	          <af:textBox type="text" classNameBase="input" name="strCCNL" value="<%=desCCNL%>" size="48" maxlength="50"
  	           title="CCNL" readonly="true" />
          <af:textBox type="hidden" name="strCCNLHid" value="" />  
          <af:textBox type="hidden" name="strCCNLPadre" value="" />
      	</td>
      </tr>
        
      <tr valign="top">
        <td class="etichetta">Raggiungibile con mezzi pubblici</td>
        <td class="campo">
        <af:comboBox classNameBase="input" name="flgMezziPub" title="Raggiungibile con mezzi pubblici" onChange="fieldChanged();" disabled="<%= String.valueOf(!canModify) %>" >
            <OPTION value=""></OPTION>
            <OPTION value="S" <%if (flgMezziPub.equals("S")) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
            <OPTION value="N" <%if (flgMezziPub.equals("N")) out.print("SELECTED=\"true\"");%>>No</OPTION>
          </af:comboBox>
        </td>
      </tr>
      
      <tr valign="top">
        <td class="etichetta">Telefono</td>
        <td class="campo">
          <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strTel" size="50" value="<%=strTel%>" readonly="<%= String.valueOf(!canModify) %>" maxlength="20" />
        </td>
      </tr>


      <tr valign="top">
        <td class="etichetta">Fax</td>
        <td class="campo">
          <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strFax" size="50" value="<%=strFax%>" readonly="<%= String.valueOf(!canModify) %>" maxlength="20" />
        </td>
      </tr>

      <tr valign="top">
        <td class="etichetta">Email</td>
        <td class="campo">
          <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strEmail" size="50" value="<%=strEmail%>" readonly="<%= String.valueOf(!canModify) %>" maxlength="80" />
        </td>
      </tr>
      
      <tr valign="top">
        <td class="etichetta">PEC</td>
        <td class="campo">
          <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strPECemail" size="50" value="<%=strPECemail%>" readonly="<%= String.valueOf(!canModify) %>" maxlength="80" />
        </td>
      </tr>
      
    </table>


    <table class="main" cellspacing=0 cellpadding=0>
      <tr>
        <td>
          <div class="sezione2">
          <img id='tendinaAltreInfo' alt='Apri' src='../../img/chiuso.gif' onclick="cambia(this,document.getElementById('TBL1'));" />&nbsp;&nbsp;&nbsp;Altre Informazioni
          </div>
        </td>
     </tr>
    </table>

    <table class="main" id='TBL1' style='display:none'>
      <tr valign="top">
        <td colspan="2">
          <table class="main">
            <tr>
              <td class="etichetta" width="5%">INPS</td>
              <td class="etichetta" width="5%">Posizione</td>
              <td class="campo" width="30%">
                <af:textBox classNameBase="input" type="integer" validateWithFunction="validateInps" onKeyUp="fieldChanged();unificaPosInps();" name="STRPOSINPS1" title="Posizione Inps" size="2" maxlength="2" value="<%=posInps1%>" readonly="<%=String.valueOf(!canModify)%>"/> - 
                <af:textBox classNameBase="input" type="integer" validateWithFunction="validateInps" onKeyUp="fieldChanged();unificaPosInps();" name="STRPOSINPS2" title="Posizione Inps" size="15" maxlength="13" value="<%=posInps2%>" readonly="<%=String.valueOf(!canModify)%>"/>
                <input type="hidden" name="STRPOSINPS" value="<%=posInps%>"/>
              </td>
              <td class="etichetta">Reparto</td>
              <td class="campo">
                <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strRepartoInps" title="Reparto INPS" size="30" maxlength="100" value="<%=strRepartoInps%>" readonly="<%=String.valueOf(!canModify)%>"/>
              </td>          
            </tr>

            <tr>
              <td class="etichetta" width="5%">Reg. Committenti</td>
              <td class="etichetta">Iscr.</td>
              <td class="campo">
                <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="STRNUMREGISTROCOMMITT" title="Numero di iscrizione al registro committenti" size="10" maxlength="8" value="<%=strNumRegistroCommitt%>" readonly="<%=String.valueOf(!canModify)%>"/>
              </td>
              <td class="etichetta">Data</td>
              <td class="campo">
                <af:textBox classNameBase="input" type="date" validateOnPost="true" onKeyUp="fieldChanged();" title="Data Iscr. Reg. Committenti" name="datRegistroCommit" size="10" maxlength="10" value="<%=datRegistroCommit%>" readonly="<%= String.valueOf(!canModify) %>" />
              </td>
            </tr>

            <tr>     
              <td></td>
              <td class="etichetta">Riferimento per pratica amm.</td>
              <td class="campo">
                <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strRiferimentoSare" title="Riferimento per pratica amministrativa" size="30" maxlength="100" value="<%=strRiferimentoSare%>" readonly="<%=String.valueOf(!canModify)%>"/>
              </td>
            </tr>
          </table>
        </td>
      </tr>        
    </table>

    <table class="main">
        <tr>
          <td class="etichetta">Data di inizio dell'attività</td>
          <td class="campo">
            <af:textBox classNameBase="input" type="date" validateOnPost="true" onKeyUp="fieldChanged();" name="datInizio" size="10" maxlength="10" value="<%=datInizio%>" readonly="<%= String.valueOf(!canModify) %>" />
            &nbsp;&nbsp;&nbsp;&nbsp;Data di fine dell'attività&nbsp;&nbsp;
            <af:textBox classNameBase="input" type="date" validateOnPost="true" onKeyUp="fieldChanged();" name="datFine" size="10" maxlength="10" value="<%=datFine%>" readonly="<%= String.valueOf(!canModify) %>" />
          </td>
        </tr>
    </table>
    <table class="main">
        <tr valign="top">
          <td class="etichetta">Note</td>
          <td class="campo">
            <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="strNote" cols="50" value="<%=strNote%>" readonly="<%= String.valueOf(!canModify) %>" />
          </td>
        </tr>
      </table>
      <br/>

    <input type="hidden" name="prgAzienda" value="<%= prgAzienda %>"/>
    <input type="hidden" name="prgUnita" value="<%= prgUnita %>"/>
    <input type="hidden" name="numKloUnitaAzienda" value="<%=numKloUnitaAzienda%>">
    <input type="hidden" name="cdnFunzione" value="<%= _funzione %>"/>
    <input type="hidden" name="ret" value="<%=pagina_back%>" />
    <input type="hidden" name="PAGE" value="<%= _pageSubmit %>" />
    
    <% if (!(flag_insert_testata || flag_insert_unita)) { %>
        <%if (canModify) { %>
          <input class="pulsante" type="button" name="salva" value="Aggiorna" onClick="javascript:controllaValiditaCampi(document.Frm1,this);" />   
          <input type="hidden" name="strCodicefiscaleInsUnita" value="<%=strCodiceFiscale%>" />
          <input type="hidden" name="isSedeLegale" value="<%=new Boolean(isSedeLegale).toString()%>" />
        <%}%>
       <% if (canDelete) { 
            if (funzioneaggiornamentounita.equals("")) {%>
              <!--<input class="pulsante" type="button" name="cancella" value="Cancella il record" onClick="alert('funzione non implementata')" />-->
            <%}%> <%}%>
            <input class="pulsante" type="button" name="back" value="Testata azienda" onclick="go('PAGE=IdoTestataAziendaPage&cdnFunzione=<%=_funzione%>&prgAzienda=<%=prgAzienda%>&prgUnita=<%=prgUnita%>&PRGMOVIMENTO=<%=prgMov%>&AGG_FUNZ_INS_UNITA=<%=funzioneaggiornamentounita%>&showMenu=true', 'FALSE')" />
      
    <%} else { %>
          <input class="pulsante" type="button" name="insert_unita_step2" value="Inserisci" onClick="javascript:controllaValiditaCampi(document.Frm1, this);" />
          <input type="hidden" name="strCodicefiscaleInsUnita" value="<%=strCodiceFiscale%>" />
    <%}//end if

    if (!funzioneaggiornamento.equals("")) {%>
      <af:textBox type="hidden" name="AGG_FUNZ" value="<%=funzioneaggiornamento%>"/>
    <%}
    else {
      if (!prgUnita.equals("") && funzioneaggiornamentounita.equals("")) {
      %>
        <input class="pulsante" type="button" name="Referenti" value="Referenti" onclick="go('PAGE=IdoReferentiPage&cdnFunzione=<%=_funzione%>&prgAzienda=<%=prgAzienda%>&prgUnita=<%=prgUnita%>', 'FALSE')" />
    	<% if(canEsterno) { %>
    		<input class="pulsante" type="button" name="accEst" value="Accesso esterno" onClick="accessoEsterno();" />   
    	<% }
      }  
    }
    %>
     
    <% if( (!(flag_insert_testata || flag_insert_unita)) && canCreaGruppo && accountAzienda.equals("") && accountUnita.equals("") ) {%>    
  		<input class="pulsante" type="button" name="Crea gruppo" value="Crea gruppo" onClick="CreaGruppo();">
    <%}%>

    
<%if (prgMov != null && !prgMov.equals("")) {%>
  	<input type="hidden" name="PRGMOVIMENTO" value="<%=prgMov%>"/> 
<%}%>

    </af:form>     
    <br/>
    <p align="center">
    <%if (!(flag_insert_testata || flag_insert_unita)) operatoreInfo.showHTML(out);%>
    </p>
    <%out.print(htmlStreamBottom);%>
    <br/>
  <%
  }
}
%> <%if ( !flgSede.equals("S") && funzioneaggiornamentounita.equals("")) {%>
    <p class="titolo">Sede legale</p>
    
    <af:list moduleName="M_RicercaSedeLegaleAzienda" skipNavigationButton="1"/> 
    <%}%>

<%@ include file="VisualizzaPulsanteChiusuraPopUp.inc"%> 
<% String pagina_lista = "IdoListaAziendePage";%>
<%@ include file="VisualizzaPulsanteIndietroLista.inc"%>
<%String urlDiListaCProsp = (String) sessionContainer.getAttribute("_TOKEN_COPIAPROSPLISTAPAGE");
if (urlDiListaCProsp != null) {%>
	<center>
	<div id="tornaAllaListaCopiaProsp" >
	<table><tr><td>
	<%  
	String htmlToListCopiaProsp = InfCorrentiAzienda.formatBackListCopiaProsp(urlDiListaCProsp); 
	if (htmlToListCopiaProsp != null) out.print(htmlToListCopiaProsp);
	%>
	</td></tr></table>
	</div>
	</center>
	<%if (htmlToListCopiaProsp != null ) {%>
	<script>
		if (window.opener != null) document.getElementById('tornaAllaListaCopiaProsp').style.display='none';
	</script>
	<%}%>
<%}%>
</body> 
</html>

<% } %>
