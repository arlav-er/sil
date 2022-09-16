<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="com.engiweb.framework.base.*, 
                com.engiweb.framework.configuration.ConfigSingleton,
                 java.lang.*,
                java.text.*, java.util.*,it.eng.sil.util.*, java.math.*, it.eng.sil.security.*,
                it.eng.afExt.utils.StringUtils"%>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ include file="../global/Function_CommonRicercaComune.inc" %>
<%@ include file="../anag/Function_CommonRicercaCPI.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
  //variabili di navigazione       
  int _funzione=0;           
      
  String cdnLavoratoreMov = "";
  SourceBean rigaLavMov = null;
  String codComRes="";         
  String strComRes="";             
  String provRes=""; 
  String strIndirizzores="";      
  String strLocalitares="";
  String strCapRes="";
  String codComdom="";
  String strComdom="";
  String provDom="";
  String strIndirizzodom="";   
  String strLocalitadom="";         
  String strCapDom="";
  String codCpiTit="";                          
  String strCpiTit="";
  /*String cdnUtins="";
  String dtmins="";                                
  String cdnUtmod="";
  String dtmmod="";
  String numKloLavoratore="";
  String numKloLavStoriaInf="";*/      
  
  InfCorrentiLav infCorrentiLav= null;
  Testata operatoreInfo = null;
  //Sezione dei parametri dalla pagina precedente
  String strCodiceFiscale="";
  String strCognome="";
  String strNome="";
  String strSesso="";
  String codStatoCivile="";
  String datNasc="";
  String codComNas="";
  String codComNasHid="";
  String strComNas="";
  String strComNasHid="";
  String strNazione="";
  String strNazioneHid="";
  String codCittadinanza="";
  String codCittadinanzaHid="";
  String strCittadinanza="";
  String strCittadinanzaHid="";
  String strNazione2="";
  String strNazione2Hid="";
  String codCittadinanza2="";
  String codCittadinanza2Hid="";
  String strCittadinanza2="";
  String strCittadinanza2Hid="";
  String flgMilite="";
  String strNote="";
  String flgCfOk="";
  //Fine sezione

  String strTelRes="";
  String strTelDom="";
  String strTelAltro="";
  String strCell="";
  String strEmail="";
  String strFax="";
  
  //Davide 05/05/2005 aggiunta variabile per inserire il CPI del lavoratore incaso di inderimeno nuovo movimento
  String codCpiLavXInsMovimento = "";
    
// NOTE: Attributi della pagina (pulsanti e link) 
//  PageAttribs attributi = new PageAttribs(user, "AnagDettaglioPageIndirizziIns");
//  boolean canModify = attributi.containsButton("salva");
// 
// 	L'attributo della pagina e' stato eliminato: se si arriva fin qui vuol dire che tutti i controlli
//  di accessibilita' precedenti sono stati superati (savino 05/04/05)
  boolean canModify = true;
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

  //sono valorizzati quando inserisco un lavoratore dai movimenti
  String contestoChiamata = serviceRequest.containsAttribute("PROVENIENZA")?serviceRequest.getAttribute("PROVENIENZA").toString():"";
  String funzioneAggiornamento = serviceRequest.containsAttribute("AGG_FUNZ")?serviceRequest.getAttribute("AGG_FUNZ").toString():"";
  String prgMovimentoApp = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGMOVIMENTOAPP");
  String prgMobilitaApp = StringUtils.getAttributeStrNotNull(serviceRequest, "prgMobilitaIscrApp");
  
   
   boolean inserimentoFallito=false;
   if (serviceResponse.containsAttribute("M_INSERTLAVORATOREANAGINDIRIZZI.operationResult")) {
   	 inserimentoFallito=(serviceResponse.getAttribute("M_INSERTLAVORATOREANAGINDIRIZZI.operationResult").toString()).equals("ERROR");
   }
   
   //controllo configurazione per la cooperazione
   boolean coopAbilitata=(boolean) ((ConfigSingleton.getInstance()).getAttribute("COOP.ABILITATA")).equals("true");
   
  
%>

<html>
<head>
<title>NEW Anagrafica Indirizzi</title>

 <link rel="stylesheet" href="../../css/stili.css" type="text/css">
 <af:linkScript path="../../js/"/>
 
<SCRIPT TYPE="text/javascript">
<!--
/* Controlla se si naviga tra le due pagine di inseriemento, in modo da controllare 
   il settaggio del codcpi che viene passato tra le due pagine */
var navigazione = false;
  
// Per rilevare la modifica dei dati da parte dell'utente
var flagChanged = false;  

function fieldChanged() {
  flagChanged = true;
}
  
function codCPIUpperCase(inputName){
  var ctrlObj = eval("document.forms[0]." + inputName);
  eval("document.forms[0]."+inputName+".value=document.forms[0]."+inputName+".value.toUpperCase();")
	return true;
}


function checkCap(inputName){
  var capObj = eval("document.forms[0]." + inputName);
  cap=capObj.value;
  ok=true;
  if ((cap.length>0) && (cap.length < 5) ) {
    ok=false;
  }

  if (!ok) {
    alert("Il " + capObj.title + " deve essere di 5 cifre");
    capObj.focus();
  }
  return ok;
}

-->
</SCRIPT>

<script language="javascript">
  function indietro(){
    // Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;
  
    document.Frm1.PAGE.value="AnagDettaglioPageAnagIns";
    doFormSubmit(document.Frm1);
  }
  
  function copia_Dom_Res(){
    var myform = document.Frm1;
    myform.strIndirizzores.value = myform.strIndirizzodom.value;
    myform.strLocalitares.value = myform.strLocalitadom.value;
    myform.codComRes.value = myform.codComdom.value;
    myform.strComRes.value = myform.strComdom.value;
    myform.strCapRes.value = myform.strCapDom.value;
    myform.strTelRes.value = myform.strTelDom.value;
  }

  function SalvaLavoratore() {
  	<%if (contestoChiamata.equals("MOVIMENTI") && ( funzioneAggiornamento.equals("aggiornaLavoratore") 
		  										 || funzioneAggiornamento.equals("aggLav")) ) {%>
	  										
     document.Frm1.PAGE.value = "AnagDettaglioPageIndirizziIns";
   <%}%>
   var esito1 = false;
   var esito2 = false;
               
   esito1 = btFindComuneCAP_onSubmit(document.Frm1.codComdom, 
                            document.Frm1.strComdom, 
                            document.Frm1.strCapDom, 
                            true,
                            document.Frm1.codCPIxConfronto,
                            'compatibilitaComuneCPI()' );    
                            
   if(esito1){                                  
	   esito2 = btFindComuneCAP_onSubmit(document.Frm1.codComRes, 
	                            document.Frm1.strComRes, 
	                            document.Frm1.strCapRes, 
	                            true);
   }
    
    return (esito1 && esito2);
  }

  //Setta il campo codCpi con il valore di CODCPITEXT(viene utilizzato per la navigazione tra 
  //le pagine di inserimento ed in caso di fallito inserimento del lavoratore)
  function setCPI(){
  	var form;
    <% if (serviceRequest.containsAttribute("inserimentoLav") && 
           contestoChiamata.equals("MOVIMENTI") && 
           ( funzioneAggiornamento.equals("aggiornaLavoratore") || funzioneAggiornamento.equals("aggLav")) &&
           !inserimentoFallito) {%>
          form = document.dati;
    <%} else {%>
          form = document.Frm1;
      <%}%>
    if (navigazione){ 
        if ((form.codCPI.value == "") && (form.codCPIText.value != "")){
          form.codCPI.value = form.codCPIText.value;
        }
    } 
  }

function compatibilitaComuneCPI()
{
  var myform = window.document.Frm1;
  <%-- alert("Il comune trovato è\n"+ myform.strComdom.value); --%>
  var codCPItrovato   = myform.codCPIxConfronto.value;
  var codCPIesistente = myform.codCPI.value;

  var doChangeCpi = true;
  
  if (codCPIesistente != null && codCPIesistente != "") {

    if (codCPItrovato != codCPIesistente) {
     
       var warningStr = "Il comune di domicilio trovato non corrisponde con il CPI di competenza:\n\n"+
       					"comune trovato: "+myform.strComdom.value+"\n"+
       					"comune con CPI compentente: "+myform.strCPI.value+"\n\n"+
       					"Cambiare il CPI di compentenza?";
       doChangeCpi = confirm(warningStr);
    }
  }
  <%-- 
  Questo controllo non è più necessario in quanto si è deciso che qualora il codice CPI
  è ugale a NT lo inseriamo comunque (semplicemente non associamo una descrizione)

  else {
     // Se il codCPItrovato = NT significa che non è stato trovato un cod. CPI 
       //   associato a quel comune. Quindi non inseriamo nessun Centro Per l'Impiego.       
     
     doChangeCpi = (codCPItrovato != "NT");
  } 
  --%>
  
  if (doChangeCpi) {
     myform.codCPI.value = codCPItrovato;
     myform.codCPIText.value = codCPItrovato;
     // GG: rimpiazzo: myform.strCPI.value = myform.strComdom.value;  con:
     myform.strCPI.value = "";

	  <%--
	  GG 17/9/2004: poiché qualcuno prima di me ha tolto il bottone di lookup del CPI, occorre eseguire
	                il suo JavaScript per ottenerne la DESCRIZIONE del CPI dato il suo codice numerico.
	  --%>
	  if (myform.codCPIText.value != "NT") {
		  btFindCPI_onclick( myform.codCPI,
		                     myform.codCPIHid,
		                     myform.strCPI,
		                     myform.strCPIHid,
		                     'codice');
	  }
  } //if doChangeCpi

}//end function



</script>
</head>
<body class="gestione" onload="rinfresca()">
<p class="titolo"><b>Inserimento lavoratore - Indirizzi</b></p>
<%
String _page = (String) serviceRequest.getAttribute("PAGE"); 
_funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));

//recupero i dati del lavoratore recuperati dal movimento se sto facendo la validazione
String validaMovimenti = serviceRequest.containsAttribute("VALIDAMOV")?serviceRequest.getAttribute("VALIDAMOV").toString():"";
//parametro = "S" quando la pagina viene chiamata dalla validazione manuale mobilità
String validaMobilita = serviceRequest.containsAttribute("VALIDAMOBILITA")?serviceRequest.getAttribute("VALIDAMOBILITA").toString():"";

String codFiscaleLavMov = "";
String nomeLavMov = "";
String cognomeLavMov = "";
String datNascitaLavMov = "";
String flgCambiamentiDati = "";
String context = "";

if ( validaMovimenti.equals("S") || validaMobilita.equals("S") ) {
  codFiscaleLavMov = serviceRequest.getAttribute("codFiscaleLavMov").toString();
  nomeLavMov = serviceRequest.getAttribute("nomeLavMov").toString();
  cognomeLavMov = serviceRequest.getAttribute("cognomeLavMov").toString();
  datNascitaLavMov = serviceRequest.getAttribute("datNascitaLavMov").toString();
  flgCambiamentiDati  = serviceRequest.getAttribute("flgCambiamentiDati").toString();
  context = serviceRequest.containsAttribute("context")?serviceRequest.getAttribute("context").toString():"";
}

 
//Recupero dati da pagina precedente
  if (serviceRequest.containsAttribute("strCodiceFiscale")){strCodiceFiscale = serviceRequest.getAttribute("strCodiceFiscale").toString();}
  if (serviceRequest.containsAttribute("strCognome")){strCognome = serviceRequest.getAttribute("strCognome").toString();}
  if (serviceRequest.containsAttribute("strNome")){strNome = serviceRequest.getAttribute("strNome").toString();}
  if (serviceRequest.containsAttribute("strSesso")){strSesso = serviceRequest.getAttribute("strSesso").toString();}
  if (serviceRequest.containsAttribute("codStatoCivile")){codStatoCivile = serviceRequest.getAttribute("codStatoCivile").toString();}
  if (serviceRequest.containsAttribute("datNasc")){datNasc = serviceRequest.getAttribute("datNasc").toString();}
  if (serviceRequest.containsAttribute("codComNas")){codComNas = serviceRequest.getAttribute("codComNas").toString();}
  if (serviceRequest.containsAttribute("codComNasHid")){codComNasHid = serviceRequest.getAttribute("codComNasHid").toString();}
  if (serviceRequest.containsAttribute("strComNas")){strComNas = serviceRequest.getAttribute("strComNas").toString();}
  if (serviceRequest.containsAttribute("strComNasHid")){strComNasHid = serviceRequest.getAttribute("strComNasHid").toString();}
  if (serviceRequest.containsAttribute("strNazione")){strNazione = serviceRequest.getAttribute("strNazione").toString();}  
  if (serviceRequest.containsAttribute("strNazioneHid")){strNazioneHid = serviceRequest.getAttribute("strNazioneHid").toString();}
  if (serviceRequest.containsAttribute("codCittadinanza")){codCittadinanza = serviceRequest.getAttribute("codCittadinanza").toString();}
  if (serviceRequest.containsAttribute("codCittadinanzaHid")){codCittadinanzaHid = serviceRequest.getAttribute("codCittadinanzaHid").toString();}
  if (serviceRequest.containsAttribute("strCittadinanza")){strCittadinanza = serviceRequest.getAttribute("strCittadinanza").toString();}
  if (serviceRequest.containsAttribute("strCittadinanzaHid")){strCittadinanzaHid = serviceRequest.getAttribute("strCittadinanzaHid").toString();}
  if (serviceRequest.containsAttribute("strNazione2")){strNazione2 = serviceRequest.getAttribute("strNazione2").toString();}
  if (serviceRequest.containsAttribute("strNazione2Hid")){strNazione2Hid = serviceRequest.getAttribute("strNazione2Hid").toString();}
  if (serviceRequest.containsAttribute("codCittadinanza2")){codCittadinanza2 = serviceRequest.getAttribute("codCittadinanza2").toString();}
  if (serviceRequest.containsAttribute("codCittadinanza2Hid")){codCittadinanza2Hid = serviceRequest.getAttribute("codCittadinanza2Hid").toString();}
  if (serviceRequest.containsAttribute("strCittadinanza2")){strCittadinanza2 = serviceRequest.getAttribute("strCittadinanza2").toString();}
  if (serviceRequest.containsAttribute("strCittadinanza2Hid")){strCittadinanza2Hid = serviceRequest.getAttribute("strCittadinanza2Hid").toString();}
  if (serviceRequest.containsAttribute("flgMilite")){flgMilite = serviceRequest.getAttribute("flgMilite").toString();}
  if (serviceRequest.containsAttribute("strNote")){strNote = serviceRequest.getAttribute("strNote").toString();}
  if (serviceRequest.containsAttribute("flgCfOk")){flgCfOk = serviceRequest.getAttribute("flgCfOk").toString();}  
  
  //Prelievo dati dagli indirizzi
    if (serviceRequest.containsAttribute("codComRes")) {codComRes=serviceRequest.getAttribute("codComRes").toString();}
    if (serviceRequest.containsAttribute("provRes")) {provRes=serviceRequest.getAttribute("provRes").toString();}
    if (serviceRequest.containsAttribute("strComRes")) {
       strComRes=serviceRequest.getAttribute("strComRes").toString()+(!provRes.equals("")?" ("+provRes+")":"");
    }
    if (serviceRequest.containsAttribute("strIndirizzores")) {strIndirizzores=serviceRequest.getAttribute("strIndirizzores").toString();}
    if (serviceRequest.containsAttribute("strLocalitares")) {strLocalitares=serviceRequest.getAttribute("strLocalitares").toString();}
    if (serviceRequest.containsAttribute("strCapRes")) {strCapRes=serviceRequest.getAttribute("strCapRes").toString();}
    if (serviceRequest.containsAttribute("codComdom")) {codComdom=serviceRequest.getAttribute("codComdom").toString();}
    if (serviceRequest.containsAttribute("provDom")) {provDom=serviceRequest.getAttribute("provDom").toString();}
    if (serviceRequest.containsAttribute("strComdom")) {
        strComdom=serviceRequest.getAttribute("strComdom").toString()+(!provDom.equals("")?" ("+provDom+")":"");
    }
    if (serviceRequest.containsAttribute("strIndirizzodom")) {strIndirizzodom=serviceRequest.getAttribute("strIndirizzodom").toString();}
    if (serviceRequest.containsAttribute("strLocalitadom")) {strLocalitadom=serviceRequest.getAttribute("strLocalitadom").toString();}
    if (serviceRequest.containsAttribute("strCapDom")) {strCapDom=serviceRequest.getAttribute("strCapDom").toString();}
    if (serviceRequest.containsAttribute("CODCPITEXT")) {
      codCpiTit=serviceRequest.getAttribute("CODCPITEXT").toString();
      out.write("<script language=\"javascript\"> navigazione = true; </script>");
    }//codCPI
    if (serviceRequest.containsAttribute("strCpi")) {strCpiTit=serviceRequest.getAttribute("strCpi").toString();}

    //Recapiti
    if (serviceRequest.containsAttribute("STRTELRES")) { strTelRes = serviceRequest.getAttribute("STRTELRES").toString();}
    if (serviceRequest.containsAttribute("STRTELDOM")) { strTelDom = serviceRequest.getAttribute("STRTELDOM").toString();}
    if (serviceRequest.containsAttribute("STRTELALTRO")) { strTelAltro = serviceRequest.getAttribute("STRTELALTRO").toString();}
    if (serviceRequest.containsAttribute("STRCELL")) { strCell = serviceRequest.getAttribute("STRCELL").toString();}
    if (serviceRequest.containsAttribute("STREMAIL")) { strEmail = serviceRequest.getAttribute("STREMAIL").toString();}
    if (serviceRequest.containsAttribute("STRFAX")) { strFax = serviceRequest.getAttribute("STRFAX").toString();}
  //*****************************
  
  //Se arrivo dalla validazione per aggiungere il lavoratore i dati li prendo dalla tabella di appoggio
  if (validaMovimenti.equalsIgnoreCase("S") && !prgMovimentoApp.equals("")) {
  	SourceBean dataOrigin = (SourceBean) serviceResponse.getAttribute("M_MovGetDettMovApp.ROWS.ROW");
  	if (dataOrigin != null) {
	  codComdom=StringUtils.getAttributeStrNotNull( dataOrigin, "codComDomLav");
	  provDom=StringUtils.getAttributeStrNotNull( dataOrigin, "codProvDomLav");
	  strComdom=StringUtils.getAttributeStrNotNull( dataOrigin, "strComDomLav")+(!provDom.equals("")?" ("+provDom+")":"");
	  strIndirizzodom=StringUtils.getAttributeStrNotNull( dataOrigin, "strIndirizzoDomLav");
	  strCapDom=StringUtils.getAttributeStrNotNull( dataOrigin, "strCapDomLav");
	  codCpiTit=StringUtils.getAttributeStrNotNull( dataOrigin, "CODCPILAV");	  
	  strCpiTit=StringUtils.getAttributeStrNotNull( dataOrigin, "STRCPILAV");	
  	}
  }
  else {
  	if (validaMobilita.equalsIgnoreCase("S") && !prgMobilitaApp.equals("")) {
  		SourceBean dataOrigin = (SourceBean) serviceResponse.getAttribute("M_MobilitaGetDettaglioMobApp.ROWS.ROW");
	  	if (dataOrigin != null) {
		  codComdom=StringUtils.getAttributeStrNotNull( dataOrigin, "CODCOMDOM");
		  provDom=StringUtils.getAttributeStrNotNull( dataOrigin, "CODPROVDOM");
		  strComdom=StringUtils.getAttributeStrNotNull( dataOrigin, "STRCOMDOM")+(!provDom.equals("")?" ("+provDom+")":"");
		  strIndirizzodom=StringUtils.getAttributeStrNotNull( dataOrigin, "STRINDIRIZZODOM");
		  strCapDom=StringUtils.getAttributeStrNotNull( dataOrigin, "STRCAPDOM");
		  codCpiTit=StringUtils.getAttributeStrNotNull( dataOrigin, "CODCPILAV");	  
		  strCpiTit=StringUtils.getAttributeStrNotNull( dataOrigin, "STRCODCPILAV");
	  	}
	}
  }
  
%>
 
<font color="green">
 <af:showMessages prefix="M_InsertLavoratoreAnagIndirizzi"/>
</font>
<font color="red">
    <af:showErrors/>
</font>

<%
if (serviceRequest.containsAttribute("inserimentoLav") && 
   contestoChiamata.equals("MOVIMENTI") && 
   ( funzioneAggiornamento.equals("aggiornaLavoratore") || funzioneAggiornamento.equals("aggLav")) &&
   !inserimentoFallito) {

  rigaLavMov = (SourceBean) serviceResponse.getAttribute("M_GETPROSSIMOLAVORATORE.ROW");
  cdnLavoratoreMov = rigaLavMov.getAttribute("cdnLavoratore").toString();

  if (!strCodiceFiscale.toUpperCase().equals(codFiscaleLavMov.toUpperCase()) ||
      !strCognome.toUpperCase().equals(cognomeLavMov.toUpperCase()) ||
      !datNasc.equals(datNascitaLavMov) ||
      !strNome.toUpperCase().equals(nomeLavMov.toUpperCase())) {
    flgCambiamentiDati = "L";
  }
  
  %>
  <af:form name="dati" method="POST" action="AdapterHTTP" onSubmit="btFindComuneCAP_onSubmit(document.Frm1.codComdom, 
                                                                                            document.Frm1.strComdom, 
                                                                                            document.Frm1.strCapDom, 
                                                                                            true,
                                                                                            document.Frm1.codCPIxConfronto,
                                                                                            'compatibilitaComuneCPI()' ) && 
                                                                  btFindComuneCAP_onSubmit(document.Frm1.codComRes, 
                                                                                            document.Frm1.strComRes, 
                                                                                            document.Frm1.strCapRes, 
                                                                                            true)"
                                                           dontValidate="true">
  <input type="hidden" name="PAGE" value="MovimentiRicercaRefreshPage">  
  <input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratoreMov%>">
  <input type="hidden" name="strCodiceFiscaleLavoratore" value="<%=strCodiceFiscale%>">
  <input type="hidden" name="FLGCFOK" value="<%=flgCfOk%>">
  <input type="hidden" name="strNome" value="<%=strNome%>">
  <input type="hidden" name="strCognome" value="<%=strCognome%>">
  <input type="hidden" name="datNasc" value="<%=datNasc%>">
  <input type="hidden" name="AGG_FUNZ" value="<%=funzioneAggiornamento%>">
  <input type="hidden" name="flgCambiamentiDati" value="<%=flgCambiamentiDati%>">
  <%if (serviceRequest.containsAttribute("CODCPI")) {
    codCpiLavXInsMovimento = StringUtils.getAttributeStrNotNull(serviceRequest,"CODCPI");%>
  <%}%>
  <input type="hidden" name="CODCPILAV" value="<%=codCpiLavXInsMovimento%>">
  </af:form>
  <script language="Javascript">
    doFormSubmit(document.dati);
  </script>
<%}else {%>
  <af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="SalvaLavoratore()">
  <input type="hidden" name="PAGE" value="AnagDettaglioPageIndirizziGenerale">
  <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
  <%
  if (validaMovimenti.equals("S")) {%>
    <!-- inizio campi da controllare se i dati del lavoratore contenuti nel movimento da validare
    sono cambiati rispetto a quelli contenuti nel DB -->    
    <input type="hidden" name="codFiscaleLavMov" value="<%=codFiscaleLavMov%>">
    <input type="hidden" name="nomeLavMov" value="<%=nomeLavMov%>">
    <input type="hidden" name="cognomeLavMov" value="<%=cognomeLavMov%>">
    <input type="hidden" name="datNascitaLavMov" value="<%=datNascitaLavMov%>">
    <input type="hidden" name="VALIDAMOV" value="<%=validaMovimenti%>">
    <input type="hidden" name="flgCambiamentiDati" value="<%=flgCambiamentiDati%>">
    <input type="hidden" name="CONTEXT" value="<%=context%>">
    
    <input type="hidden" name="prgMovimentoapp" value="<%=prgMovimentoApp%>">
    <!-- fine campi da controllare se i dati del lavoratore contenuti nel movimento da validare
    sono cambiati rispetto a quelli contenuti nel DB -->
  <%}
  else {
  	if (validaMobilita.equals("S")) {%>
  		<!-- inizio campi da controllare se i dati del lavoratore contenuti nella mobilità da validare
	    sono cambiati rispetto a quelli contenuti nel DB -->
	    <input type="hidden" name="codFiscaleLavMov" value="<%=codFiscaleLavMov%>">
	    <input type="hidden" name="nomeLavMov" value="<%=nomeLavMov%>">
	    <input type="hidden" name="cognomeLavMov" value="<%=cognomeLavMov%>">
	    <input type="hidden" name="datNascitaLavMov" value="<%=datNascitaLavMov%>">
	    <input type="hidden" name="VALIDAMOBILITA" value="<%=validaMobilita%>">
	    <input type="hidden" name="flgCambiamentiDati" value="<%=flgCambiamentiDati%>">
  	<%}
  }
  %>
  <input type="hidden" name="strCodiceFiscale" value="<%=strCodiceFiscale%>"/> 
  <input type="hidden" name="strCognome" value="<%=strCognome%>"/>
  <input type="hidden" name="strNome" value="<%=strNome%>"/>
  <input type="hidden" name="strSesso" value="<%=strSesso%>"/>
  <input type="hidden" name="codStatoCivile" value="<%=codStatoCivile%>"/>
  <input type="hidden" name="datNasc" value="<%=datNasc%>"/>
  <input type="hidden" name="codComNas" value="<%=codComNas%>"/>
  <input type="hidden" name="codComNasHid" value="<%=codComNasHid%>"/>
  <input type="hidden" name="strComNas" value="<%=strComNas%>"/>
  <input type="hidden" name="strComNasHid" value="<%=strComNasHid%>"/>
  <input type="hidden" name="strNazione" value="<%=strNazione%>"/>
  <input type="hidden" name="strNazioneHid" value="<%=strNazioneHid%>"/>
  <input type="hidden" name="codCittadinanza" value="<%=codCittadinanza%>"/>
  <input type="hidden" name="codCittadinanzaHid" value="<%=codCittadinanzaHid%>"/>
  <input type="hidden" name="strCittadinanza" value="<%=strCittadinanza%>"/>
  <input type="hidden" name="strCittadinanzaHid" value="<%=strCittadinanzaHid%>"/>
  <input type="hidden" name="strNazione2" value="<%=strNazione2%>"/>
  <input type="hidden" name="strNazione2Hid" value="<%=strNazione2Hid%>"/>
  <input type="hidden" name="codCittadinanza2" value="<%=codCittadinanza2%>"/>
  <input type="hidden" name="codCittadinanza2Hid" value="<%=codCittadinanza2Hid%>"/>
  <input type="hidden" name="strCittadinanza2" value="<%=strCittadinanza2%>"/>
  <input type="hidden" name="strCittadinanza2Hid" value="<%=strCittadinanza2Hid%>"/>
  <input type="hidden" name="flgMilite" value="<%=flgMilite%>"/>
  <input type="hidden" name="strNote" value="<%=strNote%>"/>
  <input type="hidden" name="flgCfOk" value="<%=flgCfOk%>"/>
  <input type="hidden" name="inserimentoLav" value="S"/>
  <!-- ** --> 
  <!-- campi che indicano la provenienza per effettuare delle gestioni 
  particolari (es. MOVIMENTI) 
  -->
  <input type="hidden" name="PROVENIENZA" value="<%=contestoChiamata%>">
  <input type="hidden" name="AGG_FUNZ" value="<%=funzioneAggiornamento%>">
  
  <p align="center">
  <%out.print(htmlStreamTop);%>
  <table  class="main" border="0">
  <!-- RESIDENZA ET DOMICILIO -->
  <tr>
  <td colspan="2" class="titolo"><br/><center><b>Domicilio</b></center></td>
  </tr>
  <tr>
    <td class="etichetta">Indirizzo&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" title="indirizzo domicilio" onKeyUp="fieldChanged();" type="text" name="strIndirizzodom" value="<%=strIndirizzodom%>" size="50" maxlength="50" required="true" readonly="<%= String.valueOf(!canModify) %>" /></td>
    <td class="etichetta" >&nbsp;</td>
    <td class="campo">&nbsp;</td>
  </tr>
  <tr>
    <td class="etichetta">Località&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="strLocalitadom" value="<%=strLocalitadom%>" size="50" maxlength="50" readonly="<%= String.valueOf(!canModify) %>" /></td>
    <td class="etichetta" >&nbsp;</td>
    <td class="campo">&nbsp;</td>
  </tr>
  <tr>
    <td class="etichetta">Comune&nbsp;</td>
    <td class="campo">
        <af:textBox classNameBase="input" type="text" name="codComdom" value="<%=codComdom%>" title="codice comune domicilio"
            onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.codComdom, document.Frm1.codComdomHid, document.Frm1.strComdom, document.Frm1.strComdomHid, document.Frm1.strCapDom, document.Frm1.strCapDomHid, 'codice');"             
            size="4" maxlength="4" validateWithFunction="" readonly="<%= String.valueOf(!canModify) %>"/>&nbsp;
        <% if(canModify) { %>
        <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codComdom, 
                                                    document.Frm1.strComdom, 
                                                    document.Frm1.strCapDom, 
                                                    'codice','',
                                                    document.Frm1.codCPIxConfronto,
                                                    'compatibilitaComuneCPI()',
                                                    'inserisciComDomNonScaduto()');"
         ><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
        <% } %>
        <af:textBox type="hidden" name="codComdomHid" value="<%=codComdom%>" />
        <af:textBox classNameBase="input" type="text"  name="strComdom" value="<%=strComdom%>"
            onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.codComdom, document.Frm1.codComdomHid, document.Frm1.strComdom, document.Frm1.strComdomHid, document.Frm1.strCapDom, document.Frm1.strCapDomHid,'descrizione');"  
            size="50" maxlength="50" required="true" title="comune domicilio" 
            inline="onkeypress=\"if (event.keyCode==13) { event.keyCode=9; this.blur(); }\"" readonly="<%= String.valueOf(!canModify) %>" 
        />&nbsp;
        <% if(canModify) { %>
        <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codComdom, 
                                                    document.Frm1.strComdom, 
                                                    document.Frm1.strCapDom, 
                                                    'descrizione','',
                                                    document.Frm1.codCPIxConfronto,
                                                    'compatibilitaComuneCPI()',
                                                    'inserisciComDomNonScaduto()');"
         ><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
        <% } %>
        <af:textBox type="hidden" name="strComdomHid" value="<%=strComdom%>" />
    <td class="etichetta">Cap&nbsp;</td>
    <td class="campo">
      <af:textBox classNameBase="input" name="strCapDom" value="<%=strCapDom%>" 
            onKeyUp="fieldChanged();;PulisciRicerca(document.Frm1.codComdom, document.Frm1.codComdomHid, document.Frm1.strComdom, document.Frm1.strComdomHid, document.Frm1.strCapDom, document.Frm1.strCapDomHid,'cap');"
            title="Cap del domicilio" type="text"  size="5" maxlength="5" validateWithFunction="" readonly="<%= String.valueOf(!canModify) %>"
        />
      <af:textBox type="hidden" name="strCapDomHid" value="<%=strCapDom%>" />
      <input name="capEsteroDomicilio" type="hidden" value="capEsteroDomicilio"/>
    </td>
  </tr>
  <!--
  <tr>
      <td class="etichetta">Cap&nbsp;</td>
      <td class="campo">
        <af:textBox classNameBase="input" name="strCapDom" value="<%=strCapDom%>" 
              onKeyUp="fieldChanged();;PulisciRicerca(document.Frm1.codComdom, document.Frm1.codComdomHid, document.Frm1.strComdom, document.Frm1.strComdomHid, document.Frm1.strCapDom, document.Frm1.strCapDomHid,'cap');"
              title="Cap del domicilio" type="text"  size="5" maxlength="5" validateWithFunction="codComuneCheck" readonly="<%= String.valueOf(!canModify) %>"
          />
        <af:textBox type="hidden" name="strCapDomHid" value="<%=strCapDom%>" />
      </td>
  </tr>
  -->
  <tr>
      <td class="etichetta">Telefono Domicilio&nbsp;</td>
      <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="strTelDom" value="<%=strTelDom%>" size="15" maxlength="15" readonly="<%= String.valueOf(!canModify) %>"/>
        <input type="button" class="pulsanti" name="copiaDR" value="Copia dati Domicilio in Residenza" onClick="javascript:copia_Dom_Res();">
      </td>      
  </tr>
  <tr>
    <td class="etichetta">Centro per l'impiego ai sensi del D. Lgs 150</td>    
    <td class="campo">
        <af:textBox classNameBase="input" type="text" name="codCPIText" value="<%=codCpiTit%>" 
            onKeyUp="fieldChanged();PulisciRicercaCPI(document.Frm1.codCPI, document.Frm1.codCPIHid, document.Frm1.strCPI, document.Frm1.strCPIHid, 'codice');" 
            size="10" maxlength="9" validateWithFunction="codCPIUpperCase" 
            readonly="true"
        />
        <%-- <% if(canModify) { %>
        <A HREF="javascript:btFindCPI_onclick(  document.Frm1.codCPI, 
                                                document.Frm1.codCPIHid, 
                                                document.Frm1.strCPI, 
                                                document.Frm1.strCPIHid, 
                                                'codice');">
            <IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
        <% } %> --%>
        <af:textBox classNameBase="input" type="hidden" name="codCPIxConfronto" value=""/>
        <af:textBox type="hidden" name="codCPI" value="" />
        <af:textBox type="text" classNameBase="input" name="strCPI" value="<%=strCpiTit%>"
            onKeyUp="fieldChanged();PulisciRicercaCPI(document.Frm1.codCPI, document.Frm1.codCPIHid, document.Frm1.strCPI, document.Frm1.strCPIHid, 'descrizione');" 
            size="30" maxlength="50" 
            inline="onkeypress=\"if (event.keyCode==13) { event.keyCode=9; this.blur(); }\"" 
            readonly="true"
        />&nbsp;
        <%-- <% if(canModify) { %>
        <A HREF="javascript:btFindCPI_onclick(  document.Frm1.codCPI, 
                                                document.Frm1.codCPIHid, 
                                                document.Frm1.strCPI, 
                                                document.Frm1.strCPIHid, 
                                                'descrizione');">
            <IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
        <% } %> --%>
        <af:textBox type="hidden" name="strCPIHid" value="" />
        <%-- campo valorizzato dalla funzione javaScript di FindComune.jsp --%>
        <af:textBox type="hidden" name="codCPIifDOMeqRESHid" value="" />
        <!--
        <input type="button" class="pulsante" name="trasferisci" value="Trasferimento" onClick="javascript:alert('Funzionalità non ancora attivata.');">
        -->        
  </tr>

  <tr>
  <td colspan="4" class="titolo"><center><b>Residenza</b></center></td>
  </tr>
  <tr>
    <td class="etichetta">Indirizzo&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" title="indirizzo residenza" type="text" name="strIndirizzores" value="<%=strIndirizzores%>" size="50" maxlength="50" required="true" readonly="<%= String.valueOf(!canModify) %>" /><br/></td>
    <td class="etichetta" >&nbsp;</td>
    <td class="campo">&nbsp;</td>
  </tr>
  <tr>
    <td class="etichetta">Località&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="strLocalitares" value="<%=strLocalitares%>" size="50" maxlength="50" readonly="<%= String.valueOf(!canModify) %>" /></td>
    <td class="etichetta" >&nbsp;</td>
    <td class="campo">&nbsp;</td>
  </tr>

  <tr>
    <td class="etichetta">Comune&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.codComRes, document.Frm1.codComResHid, document.Frm1.strComRes, document.Frm1.strComResHid, document.Frm1.strCapRes, document.Frm1.strCapResHid, 'codice');" 
                                  type="text" name="codComRes" value="<%=codComRes%>" title="codice comune di residenza"
                                  size="4" maxlength="4" validateWithFunction="" readonly="<%= String.valueOf(!canModify) %>"/>&nbsp;
    <% if(canModify) { %>
    <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codComRes, 
                                                document.Frm1.strComRes, 
                                                document.Frm1.strCapRes, 
                                                'codice','',null,'inserisciComResNonScaduto()');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
    <% } %>                                                
    <af:textBox type="hidden" name="codComResHid" value="<%=codComRes%>"/>
    <af:textBox classNameBase="input" onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.codComRes, document.Frm1.codComResHid, document.Frm1.strComRes, document.Frm1.strComResHid, document.Frm1.strCapRes, document.Frm1.strCapResHid, 'descrizione');"
                type="text" name="strComRes" value="<%=strComRes%>" size="50" maxlength="50" required="true" title="comune di residenza"
                inline="onkeypress=\"if (event.keyCode==13) { event.keyCode=9; this.blur(); }\""
                readonly="<%= String.valueOf(!canModify) %>"/>&nbsp;
    <% if(canModify) { %>
    <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codComRes, 
                                                document.Frm1.strComRes, 
                                                document.Frm1.strCapRes, 
                                                'descrizione','',null,'inserisciComResNonScaduto()');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
    <% } %>
    <af:textBox type="hidden" name="strComResHid" value="<%=strComRes%>" />
    <input name="capEsteroResidenza" type="hidden" value="capEsteroResidenza"/>
    </td>
    <td class="etichetta">Cap&nbsp;</td>
    <td class="campo">
      <af:textBox classNameBase="input" onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.codComRes, document.Frm1.codComResHid, document.Frm1.strComRes, document.Frm1.strComResHid, document.Frm1.strCapRes, document.Frm1.strCapResHid, 'cap');" type="text" title="Cap di residenza" name="strCapRes" value="<%=strCapRes%>" size="5" maxlength="5" validateWithFunction="checkCap" readonly="<%= String.valueOf(!canModify) %>" />
      <af:textBox type="hidden" name="strCapResHid" value="<%=strCapRes%>"/>
    </td>
  </tr>
  <tr>
    <td class="etichetta">Telefono Residenza&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="strTelRes" value="<%=strTelRes%>" size="15" maxlength="15" readonly="<%= String.valueOf(!canModify) %>"/></td>
    <td class="etichetta" >&nbsp;</td>
    <td class="campo">&nbsp;</td>
  </tr>
  <!--
  <tr>
      <td class="etichetta">Cap&nbsp;</td>
      <td class="campo">
        <af:textBox classNameBase="input" onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.codComRes, document.Frm1.codComResHid, document.Frm1.strComRes, document.Frm1.strComResHid, document.Frm1.strCapRes, document.Frm1.strCapResHid, 'cap');" type="text" title="Cap di residenza" name="strCapRes" value="<%=strCapRes%>" size="5" maxlength="5" validateWithFunction="checkCap" readonly="<%= String.valueOf(!canModify) %>" />
        <af:textBox type="hidden" name="strCapResHid" value="<%=strCapRes%>"/>
      </td>
  </tr>
  -->

  <tr><td><br/></td></tr>
  <tr ><td colspan="4" ><hr width="90%"/></td></tr>
  <tr>
    <td class="etichetta">Altro telefono&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="strTelAltro" value="<%=strTelAltro%>" size="15" maxlength="15" readonly="<%= String.valueOf(!canModify) %>"/></td>
    <td class="campo">&nbsp;</td>
  <td class="etichetta" >&nbsp;</td>
  </tr>
  <tr>
    <td class="etichetta">Cellulare&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="strCell" value="<%=strCell%>" size="15" maxlength="15" readonly="<%= String.valueOf(!canModify) %>"/></td>
    <td class="campo">&nbsp;</td>
  <td class="etichetta" >&nbsp;</td>
  </tr>
  <tr>
    <td class="etichetta">Email&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="strEmail" value="<%=strEmail%>" size="80" maxlength="80" readonly="<%= String.valueOf(!canModify) %>"/></td>
    <td class="campo">&nbsp;</td>
    <td class="etichetta" >&nbsp;</td>
  </tr>
  <tr>
    <td class="etichetta">Fax&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="strFax" value="<%=strFax%>" size="15" maxlength="15" readonly="<%= String.valueOf(!canModify) %>"/></td>
    <td class="campo">&nbsp;</td>
    <td class="etichetta" >&nbsp;</td>
  </tr>
  <%--
  <tr>
      <td class="etichetta">Centro per l'impiego ai sensi del D. Lgs 150</td>
      <td class="campo"><af:comboBox classNameBase="input" onChange="fieldChanged();" name="codCpi" selectedValue="<%=codCpiTit%>" disabled="<%= String.valueOf(!canModify) %>" moduleName="M_ElencoCPI" addBlank="true" /></td>
  </tr>
  --%>
  </table>
  <p/>
  <center>
  <table>
    <tr><td align="center">
    <input class="pulsante" type="button" name="bIndietro" value=" << Indietro" onClick="javascript:indietro();">
<%
	if (coopAbilitata) { %>
 	 <input class="pulsante" type="submit" name="insert_lavoratore_coop" value="Inserisci">     
<%} else { %>
     <input class="pulsante" type="submit" name="salva" value="Inserisci">
<%} %>     
<!--      <br/><br/><br/> -->

   </td>
    </tr>
  </table>
  </center>
  <%out.print(htmlStreamBottom);%>
  <br/>
  <br/>
  </af:form>
<%
}
%>
<script language="javascript">
  setCPI();
</script>
</body>
</html>

