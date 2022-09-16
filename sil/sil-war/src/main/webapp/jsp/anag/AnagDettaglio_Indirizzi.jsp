<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="com.engiweb.framework.base.*, 
                com.engiweb.framework.configuration.ConfigSingleton,
                 java.lang.*,
                it.eng.sil.security.ProfileDataFilter,
                java.text.*, java.util.*,it.eng.sil.util.*, java.math.*,
                it.eng.sil.security.*"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ include file="../global/Function_CommonRicercaComune.inc" %>
<%@ include file="../anag/Function_CommonRicercaCPI.inc" %>

<%@ taglib uri="aftags" prefix="af"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	final int STAMPA_TRASF_DA_PROTOCOLLARE        = 0; // la stampa trasf. e' fallita, bisogna stamparla
	final int STAMPA_RICH_DOC_LAV_DA_PROTOCOLLARE = 1; // stampa trasferimento protocollata, bisogna stampare il documento lavoratore
	final int STAMPE_PROTOCOLLATE                 = 2; // operazione completata (in questo stato non dovrebbe mai entrare)
	final int STAMPA_RICH_DOC_OP_DA_PROTOCOLLARE  = 4; // operazione completata (in questo stato non dovrebbe mai entrare)
	int statoTrasf = -1; 
	//
	String _page = (String) serviceRequest.getAttribute("PAGE"); 
    int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
	String cdnLavoratore= (String )serviceRequest.getAttribute("CDNLAVORATORE");
	String _current_page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _current_page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
    PageAttribs attributi = new PageAttribs(user, _current_page);
	boolean canModify = false;
	boolean canAggiornaCpi = false;
	boolean canTrasf_canPresa = false;
	boolean canStoricoTrasf = false;
	boolean canVerMasterLav = false;
	
	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
    	boolean existsSalva = attributi.containsButton("salva");
    	canAggiornaCpi = attributi.containsButton("AGGIORNACPI");
    	canTrasf_canPresa = attributi.containsButton("TRASF-PRESA");
    	canStoricoTrasf = attributi.containsButton("STOR_TRASF");
    	canVerMasterLav = attributi.containsButton("VER_MASTER_LAV");
    	
    	if(!existsSalva){
    		canModify=false;
    	}else{
    		canModify=filter.canEditLavoratore();    	
    	}
    }
    // se in precedenza il lavoratore e' stato trasferito ma la stampa del trasferimento, per qualche motivo,
    // non e' stata fatta allora potra' essere stampata successivamente da questa jsp.
    boolean stampaTrasferimento = false; 

    //Controllo se in sessione ho il CodCpi di destinazione (cdnTipoGruppo dell'utente == 1)
	int cdnTipoGruppo = user.getCdnTipoGruppo();
	String codCpiUser = user.getCodRif();
	boolean codCpiUserNotFound = false;
	if(cdnTipoGruppo != 1) {
		codCpiUserNotFound = true;
	}
	
	String queryStringBack = "page=" + _page + "&CDNLAVORATORE=" + cdnLavoratore + "&cdnFunzione=" + _funzione;
%>

<%
  //variabili di navigazione


  
  //inizializzo i campi
  //String cdnLavoratore="";
  String queryString = null;
  String strNome="";
  String strCognome="";
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
  String cdnUtins="";
  String dtmins="";
  String cdnUtmod="";
  String dtmmod="";
  String numKloLavoratore="";
  String codCpiTit="";
  String strCpiTit="";
  String codMonoTipoCpi = "";
  String codCpiOrig = "";
  String strCpiOrig = "";
  String strdescMontanaR = "";
  String strdescMontanaD = "";
  
  String strTelRes="";
  String strTelDom="";
  String strTelAltro="";
  String strCell="";
  String strEmail="";
  String strFax="";
  String flgStampaTrasf = null;
  String flgStampaDoc = null;
  String numKloLavStoriaInf="";
  InfCorrentiLav infCorrentiLav= null;
  Testata operatoreInfo = null;
  //invio SMS
  String fglInvioSMS = "";
  String datInvioSMS = "";
  //Controlla se il lavoratore ha uno storico dei trasferimenti e visualizza l'eventale pulsante.
  boolean storicoTrasf = serviceResponse.containsAttribute("M_TrasferimentiStoricoElenco.ROWS.ROW");
  
  // 10/08/2006 Savino: presa atto in cooperazione
  // se si proviene dalla lista delle richieste presa atto, nel caso si apra la pagina di presa atto
  // bisogna passare il parametro del prgPresaAtto da utilizzare per la prevalorizzazione del cpi etc.
  String provenienza = (String)serviceRequest.getAttribute("PROVENIENZA");
  
  SourceBean rowConfigTrasfLav = (SourceBean) serviceResponse.getAttribute("M_ConfigStampaTrasferimentoLavoratore.ROWS.ROW");
  String configTrasfLav = it.eng.sil.module.movimenti.constant.Properties.DEFAULT_CONFIG;
  if (rowConfigTrasfLav != null){
      configTrasfLav = rowConfigTrasfLav.getAttribute("num").toString();
  }
  
  String response_xml="M_GETLAVORATOREINDIRIZZI.ROW";
  
  try {

    SourceBean row=(SourceBean) serviceResponse.getAttribute(response_xml);
  
    //cdnLavoratore= serviceRequest.getAttribute("CDNLAVORATORE").toString();
    //Recapiti
    if (row.containsAttribute("STRTELRES")) { strTelRes = row.getAttribute("STRTELRES").toString();}
    if (row.containsAttribute("STRTELDOM")) { strTelDom = row.getAttribute("STRTELDOM").toString();}
    if (row.containsAttribute("STRTELALTRO")) { strTelAltro = row.getAttribute("STRTELALTRO").toString();}
    if (row.containsAttribute("STRCELL")) { strCell = row.getAttribute("STRCELL").toString();}
    if (row.containsAttribute("STREMAIL")) { strEmail = row.getAttribute("STREMAIL").toString();}
    if (row.containsAttribute("STRFAX")) { strFax = row.getAttribute("STRFAX").toString();}
    //SMS
    if (row.containsAttribute("FLGINVIOSMS")) { fglInvioSMS = row.getAttribute("FLGINVIOSMS").toString();}
    if (row.containsAttribute("DATINVIOSMS")) { datInvioSMS = row.getAttribute("DATINVIOSMS").toString();}
      
    if (row.containsAttribute("codComRes")) {codComRes=row.getAttribute("codComRes").toString();}
    if (row.containsAttribute("provRes")) {provRes=row.getAttribute("provRes").toString();}
    if (row.containsAttribute("strComRes")) {
       strComRes=row.getAttribute("strComRes").toString()+(!provRes.equals("")?" ("+provRes+")":"");
    }
    if (row.containsAttribute("strIndirizzores")) {strIndirizzores=row.getAttribute("strIndirizzores").toString();}
    if (row.containsAttribute("strLocalitares")) {strLocalitares=row.getAttribute("strLocalitares").toString();}
    if (row.containsAttribute("strCapRes")) {strCapRes=row.getAttribute("strCapRes").toString();}
    if (row.containsAttribute("codComdom")) {codComdom=row.getAttribute("codComdom").toString();}
    if (row.containsAttribute("provDom")) {provDom=row.getAttribute("provDom").toString();}
    if (row.containsAttribute("strComdom")) {
        strComdom=row.getAttribute("strComdom").toString()+(!provDom.equals("")?" ("+provDom+")":"");
    }
    if (row.containsAttribute("strIndirizzodom")) {strIndirizzodom=row.getAttribute("strIndirizzodom").toString();}
    if (row.containsAttribute("strLocalitadom")) {strLocalitadom=row.getAttribute("strLocalitadom").toString();}
    if (row.containsAttribute("strCapDom")) {strCapDom=row.getAttribute("strCapDom").toString();}
    if (row.containsAttribute("cdnUtins")) {cdnUtins=row.getAttribute("cdnUtins").toString();}
    if (row.containsAttribute("dtmins")) {dtmins=row.getAttribute("dtmins").toString();}
    if (row.containsAttribute("cdnUtmod")) {cdnUtmod=row.getAttribute("cdnUtmod").toString();}
    if (row.containsAttribute("dtmmod")) {dtmmod=row.getAttribute("dtmmod").toString();}
    if (row.containsAttribute("numKloLavoratore")) {numKloLavoratore=row.getAttribute("numKloLavoratore").toString();}
    if (row.containsAttribute("CODCPITIT")) {codCpiTit=row.getAttribute("CODCPITIT").toString();}
    if (row.containsAttribute("STRDESCRIZIONE")) {strCpiTit=row.getAttribute("STRDESCRIZIONE").toString();}
    if (row.containsAttribute("CODMONOTIPOCPI")) {codMonoTipoCpi = (String)row.getAttribute("CODMONOTIPOCPI");}  
    if (row.containsAttribute("CODCPIORIG")) {codCpiOrig=row.getAttribute("CODCPIORIG").toString();}
    if (row.containsAttribute("STRDESCRIZIONEORIG")) {strCpiOrig=row.getAttribute("STRDESCRIZIONEORIG").toString();}
    if (row.containsAttribute("descMontanaR")) {strdescMontanaR=row.getAttribute("descMontanaR").toString();}
    if (row.containsAttribute("descMontanaD")) {strdescMontanaD=row.getAttribute("descMontanaD").toString();}
    
    if (row.containsAttribute("NumKloLavStoriaInf")) {numKloLavStoriaInf=row.getAttribute("NumKloLavStoriaInf").toString();}
    // recupero le informazioni sul trasferimento del lavoratore/presa atto
    // e sulla relativa stampa avvenuta con successo o meno
    flgStampaTrasf = (String)row.getAttribute("FLGSTAMPATRASF");  
    flgStampaDoc = (String)row.getAttribute("FLGSTAMPADOC");  
    
  }
  catch(Exception ex){
    // non faccio niente
  }
  // se il flag e' null vuol dire che il lavoratore non si e' trasferito
  // se 'S' si e' trasferito e la stampa e' gia' stata fatta e protocollata
  // se 'N' si e' trasferito e la stampa e' fallita -> bisogna stampare e protocollare
//  stampaTrasferimento = flgStampaTrasf!=null && flgStampaTrasf.equals("N");

// nuovo codice: si commenta da solo o quasi. valori possibili: null|N|S
  if (flgStampaTrasf==null && flgStampaDoc==null)
  	statoTrasf = -1;
  	else if (flgStampaTrasf==null && flgStampaDoc.equals("N"))
  		statoTrasf = STAMPA_RICH_DOC_OP_DA_PROTOCOLLARE;
	  else if (flgStampaTrasf !=null && flgStampaTrasf.equals("N"))
  		statoTrasf = STAMPA_TRASF_DA_PROTOCOLLARE;
  			else if (flgStampaDoc!=null && flgStampaDoc.equals("N"))
	  		statoTrasf = STAMPA_RICH_DOC_LAV_DA_PROTOCOLLARE;
  				else statoTrasf = STAMPE_PROTOCOLLATE;
  //
  infCorrentiLav= new InfCorrentiLav(requestContainer.getSessionContainer(), cdnLavoratore, user);
  operatoreInfo= new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);

  //Individuo il CODCPI competente per il confronto corretto (Roccetti: 27/09/2004)
  String codCpiComp = codCpiTit;
  String strCpiComp = strCpiTit;
  if (codMonoTipoCpi.equalsIgnoreCase("T")) {
  	codCpiComp = codCpiOrig;
  	strCpiComp = strCpiOrig;
  }

  boolean presaAttoTrasf=false;

  //Controllo cosa è possibile fare. i casi sono tre:
  //1) solo presa d'atto (se ho la competenza amministrativa) 
  //2) solo trasferimento (in tutti gli altri casi)
  //3) nessuna operazione se bisogna ancora stampare e protocollare un trasferimento/presa atto
  if (!codCpiUserNotFound && codMonoTipoCpi.equalsIgnoreCase("C") && codCpiUser.equals(codCpiTit)) {
	presaAttoTrasf=true;		
  }


	
   // NOTE: Attributi della pagina (pulsanti e link) 
  /*PageAttribs attributi = new PageAttribs(user, "AnagDettaglioPageIndirizzi");
  boolean canModify = attributi.containsButton("salva");*/
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
  
  //
  queryString = "cdnLavoratore="+cdnLavoratore+"&cdnFunzione="+_funzione + "&page="+_page;
  
  //controllo configurazione per la cooperazione
  boolean coopAbilitata= false;
  String coopAttiva = System.getProperty("cooperazione.enabled");
  if (coopAttiva != null && coopAttiva.equals("true")) {
  		coopAbilitata = true;
  }
%>

<html>
<head>
<title>Anagrafica dettaglio</title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">
 <af:linkScript path="../../js/"/>
<%@ include file="../documenti/_apriGestioneDoc.inc"%>
<%@ include file="../documenti/parametriche/_apriGestioneDocParam.inc"%>


<script language="Javascript" src="../../js/docAssocia.js"></script>

<script language="Javascript">
<!--Contiene il javascript che si occupa di aggiornare i link del footer-->
  <% 
       //Genera il Javascript che si occuperà di inserire i links nel footer
       attributi.showHyperLinks(out, requestContainer,responseContainer,"cdnLavoratore=" + cdnLavoratore);
  %>
</script>

 
<SCRIPT TYPE="text/javascript">
// Per rilevare la modifica dei dati da parte dell'utente
var flagChanged = false;  


  function fieldChanged() {
    <%if (canModify) {out.print("flagChanged = true;");}%>
  }

function checkIndirizzo(inputName){
	var addressObj = eval("document.forms[0]." + inputName);
	address = addressObj.value;
	ok = false;
	if (address.length>3){
		ok = true;
	}
	if (!ok)
	{
		var resOdom;
		if (inputName=="strIndirizzodom"){
			resOdom = "domicilio";
		}else{
			resOdom = "residenza";
		}
		alert("L'indirizzo di "+resOdom+" deve essere almeno di 4 caratteri.");	
	}
	return ok;
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
  if ((cap.length>0) && (cap.length<5)) {
    ok=false;
  }

  if (!ok) {
    alert("Il " + capObj.title + " deve essere di 5 cifre");
    capObj.focus();
  }
  return ok;
}
       window.top.menu.caricaMenuLav( <%=_funzione%>,   <%=cdnLavoratore%>);

function copia_Dom_Res(){
  // Se la pagina è già in submit, ignoro quest'azione!
  if (isInSubmit()) return;

  document.Frm1.strIndirizzores.value = document.Frm1.strIndirizzodom.value;
  document.Frm1.strLocalitares.value = document.Frm1.strLocalitadom.value;
  document.Frm1.codComRes.value = document.Frm1.codComdom.value;
  document.Frm1.strComRes.value = document.Frm1.strComdom.value;
  document.Frm1.strCapRes.value = document.Frm1.strCapDom.value;
  document.Frm1.strTelRes.value = document.Frm1.strTelDom.value;
}

function apriPopUpTrasferimento(cdnLav) {

	var f=null;
	<% if (presaAttoTrasf) { 		
		//se posso solo prendere atto, apro la popup di presa d'atto %>
     	f = "AdapterHTTP?PAGE=PresaAttoTrasferimentoPage&CDNLAVORATORE=" + cdnLav + "&trasferisci=false&pageDocAssociata=<%=_page%>&CODCPIORIG=<%=codCpiOrig%>&CODCPIUTENTE=<%=codCpiUser%>";
        <%
     	// 10/08/2006 savino: presa atto in cooperazione
		  if ("ListaRichestePresaAttoPage".equalsIgnoreCase(provenienza)) {
		    	String prgPresaAtto = (String)serviceRequest.getAttribute("prgPresaAtto");
	    %>
		f+="&PROVENIENZA=<%=provenienza%>&PRGPRESAATTO=<%=prgPresaAtto%>";
		<%}%>
	<% } else {
		//altrimenti apro la poppasopra di trasferimento	%>
        f = "AdapterHTTP?PAGE=TrasferimentoPage&CDNLAVORATORE=" + cdnLav + "&trasferisci=false&pageDocAssociata=<%=_page%>&cdnFunzione=<%=_funzione%>&CODCPIORIG=<%=codCpiComp%>&CODCPIUTENTE=<%=codCpiUser%>";
   <% } %>     
    var t = "trasfPresaAtto";
    var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=600,top=75,left=100";
	window.open(f, t, feat);	
        
}

function apriStoricoTrasferimenti()
{   var url = "AdapterHTTP?PAGE=TrasferimentiStoricoElencoPage&CDNLAVORATORE=<%=cdnLavoratore%>&CODCPIUTENTE=<%=codCpiUser%>";
    var tit = "StoricoTrasferimenti";
    var w=800; var l=((screen.availWidth)-w)/2;
    var h=500; var t=((screen.availHeight)-h)/2;
    //var feat = "status=YES,location=YES,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
    var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=NO,height="+h+",width="+w+",top="+t+",left="+l;
	window.open(url, tit, feat);	
} 


function compatibilitaComuneCPI()
{ //alert("Il comune trovato è\n"+ document.Frm1.strComdom.value);
  var codCPItrovato = document.Frm1.codCPIxConfronto.value;
  var codCPIesistente = document.Frm1.codCpiComp.value;
  if (codCPItrovato != codCPIesistente)
  { 
     alert("Il comune di domicilio trovato non corrisponde con il CPI di competenza:\n\ncomune trovato: "+document.Frm1.strComdom.value+"\ncomune con CPI compentente: "+document.Frm1.strCpiComp.value);
  }
}

//effettua il refresh della pagina quando chiamata 
//(Attenzione, con questa chiamata si perdono eventuali modifiche non salvate dall'utente)
function aggiorna() {
	document.location.href = "AdapterHTTP?"
	+ "PAGE=AnagDettaglioPageIndirizzi&cdnFunzione=<%=_funzione%>&cdnLavoratore=<%=cdnLavoratore%>";
}

function apriPopUpAggiornaCpi(cdnLav) {
		
		var f = "AdapterHTTP?PAGE=AggiornaCpiPage&CDNLAVORATORE=" + cdnLav + "&pageDocAssociata=<%=_page%>&CODCPIORIG=<%=codCpiOrig%>&STRCPIORIG=<%=strCpiOrig%>&CODCPIUTENTE=<%=codCpiUser%>";
	    var t = "AggionrnaCpi";
	    var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=600,top=75,left=100";
		window.open(f, t, feat);	
	
}

function aggiornaCpiComp(param) {

	document.location.href = "AdapterHTTP?"
	+ "PAGE=AnagDettaglioPageIndirizzi&cdnFunzione=<%=_funzione%>&cdnLavoratore=<%=cdnLavoratore%>&"+param;
}

function apriCheckMaster(cdnLav) {
		var f = "AdapterHTTP?PAGE=CheckMasterIRPage&CDNLAVORATORE=" + cdnLav;
	    var t = "CPIMaster";	    
	    var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=300,top=75,left=100";
		window.open(f, t, feat);	
	

}

function startsWith(str, word) {
    return str.lastIndexOf(word, 0) === 0;
}

</SCRIPT>


</head>
<body class="gestione" onload="rinfresca()"> 
<%

 
 Linguette l  = new Linguette(user,  _funzione, _page, new BigDecimal(cdnLavoratore));
 infCorrentiLav.show(out); 
 l.show(out); 
%>

<SCRIPT>
   window.top.menu.caricaMenuLav( <%=_funzione%>,   <%=cdnLavoratore%>);
</SCRIPT>
 
<font color="green">
 <af:showMessages prefix="M_SaveLavoratoreIndirizzi"/>
</font>
<af:showMessages prefix="M_AggiornaCpi"/>

 <font color="red">
     <af:showErrors/>
 </font>
 
<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="btFindComuneCAP_onSubmit( document.Frm1.codComdom, 
                                                                                            document.Frm1.strComdom, 
                                                                                            document.Frm1.strCapDom, 
                                                                                            true,
                                                                                            document.Frm1.codCPIxConfronto,
                                                                                            'compatibilitaComuneCPI()' ) && 
                                                                  btFindComuneCAP_onSubmit( document.Frm1.codComRes, 
                                                                                            document.Frm1.strComRes, 
                                                                                            document.Frm1.strCapRes, 
                                                                                            true )">
<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>"/>

<p align="center">
<%out.print(htmlStreamTop);%>
<table  class="main" border="0">
<!-- RESIDENZA ET DOMICILIO -->
<tr>
<td colspan="4" class="titolo"><br/><center><b>Domicilio</b></center></td>
</tr>
<tr>
    <td class="etichetta">Indirizzo&nbsp;</td>
    <td class="campo"><af:textBox validateWithFunction="checkIndirizzo" classNameBase="input"  title="indirizzo domicilio" onKeyUp="fieldChanged();" type="text" name="strIndirizzodom" value="<%=strIndirizzodom%>" size="50" maxlength="50" required="true" readonly="<%= String.valueOf(!canModify) %>" /></td>
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
            size="4" maxlength="4" validateWithFunction="" readonly="<%= String.valueOf(!canModify) %>"
        />&nbsp;
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
            size="50" maxlength="50" title="comune di domicilio" 
            inline="onkeypress=\"if (event.keyCode==13) { event.keyCode=9; this.blur(); }\"" readonly="<%= String.valueOf(!canModify) %>" 
        />&nbsp;*&nbsp;&nbsp;
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
        <input name="capEsteroDomicilio" type="hidden" value="capEsteroDomicilio"/>
        <af:textBox type="hidden" name="strComdomHid" value="<%=strComdom%>" />
  <td class="etichetta">Cap&nbsp;</td>
  <td class="campo">
    <af:textBox classNameBase="input" name="strCapDom" value="<%=strCapDom%>" 
          onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.codComdom, document.Frm1.codComdomHid, document.Frm1.strComdom, document.Frm1.strComdomHid, document.Frm1.strCapDom, document.Frm1.strCapDomHid,'cap');"
          title="Cap del domicilio" type="text"  size="5" maxlength="5" validateWithFunction="" readonly="<%= String.valueOf(!canModify) %>"
      />
    <af:textBox type="hidden" name="strCapDomHid" value="<%=strCapDom%>" />
  </td>
</tr>

<tr>
    <td class="etichetta">Comunità Montana&nbsp;</td>
    <td class="campo" colspan="3"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="strdescMontana" value="<%=strdescMontanaD%>" size="50" maxlength="50"  readonly="true"/>
    
    </td>
</tr>



<tr>
    <td class="etichetta">Telefono Domicilio&nbsp;</td>
    <td class="campo" colspan="3"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="strTelDom" value="<%=strTelDom%>" size="15" maxlength="15" readonly="<%= String.valueOf(!canModify) %>"/>
      <% if (canModify) { %>
      <input type="button" class="pulsanti" name="copiaDR" value="Copia dati Domicilio in Residenza" onClick="javascript:copia_Dom_Res();">
     <% }%>
    </td>
</tr>

<tr>
    <td class="etichetta">
      <% if(codMonoTipoCpi.equals("C")){ %>
        Centro per l'impiego ai sensi del D. Lgs 150
      <%} else { 
              if(codMonoTipoCpi.equals("T")){ %>
                Centro per l'impiego titolare
        <%    }
          }%>
    </td>
    <td class="campo">
        <af:textBox classNameBase="input" type="hidden" name="codCpiComp" value="<%=codCpiComp%>"/>
        <af:textBox classNameBase="input" type="hidden" name="strCpiComp" value="<%=strCpiComp%>"/>    
        <af:textBox classNameBase="input" type="hidden" name="codCPIxConfronto" value=""/>
        <af:textBox classNameBase="input" type="text" name="codCPI" value="<%=codCpiTit%>" 
            onKeyUp="fieldChanged();PulisciRicercaCPI(document.Frm1.codCPI, document.Frm1.codCPIHid, document.Frm1.strCPI, document.Frm1.strCPIHid, 'codice');" 
            size="10" maxlength="9" validateWithFunction="codCPIUpperCase" 
            readonly="true"
        />&nbsp;
        <!-- <% if(canModify) { %>
        <A HREF="javascript:btFindCPI_onclick(  document.Frm1.codCPI, 
                                                document.Frm1.codCPIHid, 
                                                document.Frm1.strCPI, 
                                                document.Frm1.strCPIHid, 
                                                'codice');">
            <IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
        <% } %>-->
        <af:textBox type="hidden" name="codCPIHid" value="<%=codCpiTit%>" />
        <af:textBox type="text" classNameBase="input" name="strCPI" value="<%=strCpiTit%>"
            onKeyUp="fieldChanged();PulisciRicercaCPI(document.Frm1.codCPI, document.Frm1.codCPIHid, document.Frm1.strCPI, document.Frm1.strCPIHid, 'descrizione');" 
            size="30" maxlength="50" 
            inline="onkeypress=\"if (event.keyCode==13) { event.keyCode=9; this.blur(); }\"" 
            readonly="true"
        />&nbsp;
        <!-- <% if(canModify) { %>
        <A HREF="javascript:btFindCPI_onclick(  document.Frm1.codCPI, 
                                                document.Frm1.codCPIHid, 
                                                document.Frm1.strCPI, 
                                                document.Frm1.strCPIHid, 
                                                'descrizione');">
            <IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
        <% } %>-->
        <af:textBox type="hidden" name="strCPIHid" value="<%=strCpiTit%>" />
        <%-- campo valorizzato dalla funzione javaScript di FindComune.jsp --%>
        <af:textBox type="hidden" name="codCPIifDOMeqRESHid" value="" />
			<% if(codMonoTipoCpi.equals("T") && !codCpiOrig.equals("")){ %>
				<br/><STRONG>(CPI Competente:&nbsp;&nbsp; <%=codCpiOrig%>&nbsp;<%=strCpiOrig%>)</STRONG>
			<%}%>                
  </td>
  <td colspan="2">		
  	<%   if (canTrasf_canPresa && (statoTrasf<0 || statoTrasf==STAMPE_PROTOCOLLATE)) { // se non ci sono stampe di trasf/presa atto da fare
  	%>
    	<input type="button" class="pulsante" name="trasferisci" value="<%=(presaAttoTrasf )?"Presa d'atto\ntrasferimento":"Trasferimento"%>" onClick="apriPopUpTrasferimento('<%=cdnLavoratore%>');">
    <% } %>
    <% if (canAggiornaCpi) { %>
    	<p>
    	<input type="button" class="pulsante" name="aggiornaCpi" value="Modifica CPI competente" onClick="apriPopUpAggiornaCpi('<%=cdnLavoratore%>')">
    	</p>
    <% } %>
  </td>
</tr>

<tr>
<td colspan="4" class="titolo"><center><b>Residenza</b></center></td>
</tr>
<tr>
    <td class="etichetta" >Indirizzo&nbsp;</td>
    <td class="campo" ><af:textBox validateWithFunction="checkIndirizzo" classNameBase="input" title="indirizzo residenza" onKeyUp="fieldChanged();" type="text" name="strIndirizzores" value="<%=strIndirizzores%>" size="50" maxlength="50" required="true" readonly="<%= String.valueOf(!canModify) %>" /><br/></td>
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
                type="text" name="strComRes" value="<%=strComRes%>" size="50" maxlength="50" title="comune di residenza"
                inline="onkeypress=\"if (event.keyCode==13) { event.keyCode=9; this.blur(); }\""
                readonly="<%= String.valueOf(!canModify) %>"/>&nbsp;*&nbsp;&nbsp;
    <% if(canModify) { %>
    <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codComRes, 
                                                document.Frm1.strComRes, 
                                                document.Frm1.strCapRes, 
                                                'descrizione','',null,'inserisciComResNonScaduto()');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
    <% } %>
    <af:textBox type="hidden" name="strComResHid" value="<%=strComRes%>" />
    </td>
    <!-- *********** -->
    <td class="etichetta">Cap&nbsp;</td>
    <td class="campo">
      <af:textBox classNameBase="input" onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.codComRes, document.Frm1.codComResHid, document.Frm1.strComRes, document.Frm1.strComResHid, document.Frm1.strCapRes, document.Frm1.strCapResHid, 'cap');" type="text" title="Cap di residenza" name="strCapRes" value="<%=strCapRes%>" size="5" maxlength="5" validateWithFunction="checkCap" readonly="<%= String.valueOf(!canModify) %>" />
      <af:textBox type="hidden" name="strCapResHid" value="<%=strCapRes%>"/>
      <input name="capEsteroResidenza" type="hidden" value="capEsteroResidenza"/>
    </td>
</tr>
<tr>
    <td class="etichetta">Comunità Montana&nbsp;</td>
    <td class="campo" colspan="3"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="strdescMontanaD" value="<%=strdescMontanaR%>" size="50" maxlength="50" readonly="true" />
    
    </td>
</tr>
<!--
<tr>
    <td class="etichetta">Cap&nbsp;</td>
    <td class="campo">
      <af:textBox classNameBase="input" onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.codComRes, document.Frm1.codComResHid, document.Frm1.strComRes, document.Frm1.strComResHid, document.Frm1.strCapRes, document.Frm1.strCapResHid, 'cap');" type="text" title="Cap di residenza" name="strCapRes" value="<%=strCapRes%>" size="5" maxlength="5" validateWithFunction="checkCap" readonly="<%= String.valueOf(!canModify) %>" />
      <af:textBox type="hidden" name="strCapResHid" value="<%=strCapRes%>"/>
    </td>
    <td class="etichetta" >&nbsp;</td>
    <td class="campo">&nbsp;</td>
</tr>
-->
<tr>
    <td class="etichetta">Telefono Residenza&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="strTelRes" value="<%=strTelRes%>" size="15" maxlength="15" readonly="<%= String.valueOf(!canModify) %>"/></td>
    <td class="etichetta" >&nbsp;</td>
    <td class="campo">&nbsp;</td>
</tr>
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
</tr>
<tr>  
    <!-- label per l'invio degli SMS -->
    <td class="etichetta" >Invio SMS</td>
  	<td class="campo" >
  		<af:comboBox classNameBase="input" name="fglInvioSMS" required="false" title="flag" onChange="fieldChanged();" disabled="<%= String.valueOf(!canModify) %>" >
    		<OPTION value="" <%if (fglInvioSMS.equals("")) out.print("SELECTED=\"true\"");%>></OPTION>
    		<OPTION value="S" <%if (fglInvioSMS.equals("S")) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
        	<OPTION value="N" <%if (fglInvioSMS.equals("N")) out.print("SELECTED=\"true\"");%>>No</OPTION>        
    	</af:comboBox> 
    </td> 	
  	<td class="etichetta" align="left" >fino al</td>
  	<td class="campo" align="left" nowrap><af:textBox validateOnPost="true" classNameBase="input" onKeyUp="fieldChanged();" type="date" name="datInvioSMS" value="<%=datInvioSMS%>" size="10" maxlength="15" readonly="<%= String.valueOf(!canModify) %>"/></td>
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
<tr>
<td colspan="4">
<input type="hidden" name="numKloLavoratore" value="<%=( Integer.parseInt(numKloLavoratore) + 1) %>">
<input type="hidden" name="numKloLavStoriaInf" value="<%=( Integer.parseInt(numKloLavStoriaInf) + 1) %>">
</td>
</tr>
</table>
<p/>
<center>
<% 
if (canModify) {%>
  <table>
  <tr><td align="center">
  <input class="pulsante" type="submit" name="salva" value="Aggiorna">
  </td>
  <% switch (statoTrasf) {
  		case STAMPA_TRASF_DA_PROTOCOLLARE: {
  			%><td><input class="pulsante" type="button"  value="Stampa report trasf. lav."  
  			onclick="apriGestioneDoc('RPT_SOLO_STAMPA_TRASFERIMENTO',
  									'&cdnLavoratore=<%=cdnLavoratore%>&pagina=<%=_page%>&stampa_report=true',
									'TRCPI',null, true);"></td>
		<% break;}
		case STAMPA_RICH_DOC_LAV_DA_PROTOCOLLARE: {%>
			<td><input class="pulsante" type="button"  value="Stampa richiesta doc. trasferimento" 
			<%if (configTrasfLav.equals(it.eng.sil.module.movimenti.constant.Properties.DEFAULT_CONFIG)){%>	
			
  				onclick="apriGestioneDoc('RPT_STAMPA_RICHIESTA_DOC',
  									'&cdnLavoratore=<%=cdnLavoratore%>&pagina=<%=_page%>&isPresaAtto=false',
									'TRDOC',null, true);">
			<%} else {%>	
			
				onclick="apriGestioneProtocollazioneParam('&cdnLavoratore=<%=cdnLavoratore%>&pagina=<%=_page%>&isPresaAtto=false',
				                     'TRDOC', 'AnagDettaglioPageIndirizzi');">
				                     
				                 
			<%}	%>						
									
									</td> 	
		<% break;}
		case STAMPA_RICH_DOC_OP_DA_PROTOCOLLARE: %>
			<td><input class="pulsante" type="button"  value="Stampa richiesta doc. presa atto" 
  			onclick="apriGestioneDoc('RPT_STAMPA_RICHIESTA_DOC',
  									'&cdnLavoratore=<%=cdnLavoratore%>&pagina=<%=_page%>&isPresaAtto=true',
									'TRDOC',null, true);"></td>
		<% break;
		default: // non ci sono operazioni da eseguire 
	}%>
  <td><input class="pulsante" type="button" name="Documenti associati" value="Documenti associati"
             onClick="docAssociati('<%=cdnLavoratore%>','<%=_page%>','<%=_funzione%>','','')"></td>
  </tr>
  </table>
<% }%>
  
  <%if(canStoricoTrasf) {%>
    <table border="0" width="100%">
    <tr><td align="center"><input class="pulsante<%=((storicoTrasf)?"":"Disabled")%>" type="button" name="StoricoTrasf" value="Storico Trasferimenti"
                 onClick="apriStoricoTrasferimenti()"
                 <%=(!storicoTrasf)?"disabled=\"True\"":""%>>
       </td>
   </tr>
   </table>
  <%}%>

<%if (coopAbilitata && canVerMasterLav) {%>
<br/>
<input class="pulsante" type="button" name="checkMasterIR" value="Verifica Master per il lavoratore" onclick="apriCheckMaster('<%=cdnLavoratore%>')" />
</center>
<%} %>
<%out.print(htmlStreamBottom);%>
<br/>
<p align="center">
<%operatoreInfo.showHTML(out);%>
</p>
<br/>
<input type="hidden" name="PAGE" value="AnagDettaglioPageIndirizzi">
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">


</af:form>

<!-- NOTA da passare poi a campi hidden -->
<af:form name="frmTemplate" method="POST" action="AdapterHTTP">
	<input type="hidden" name="PAGE" value="ElaboraStampaParametricaPage" />
	<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>" />
	<input type="hidden" name="CODCPI" value="<%=codCpiUser%>" />
	<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>" />
	<input type="hidden" name="PRGTEMPLATESTAMPA" value="" />
	<input type="hidden" name="GENERASTAMPA" value="" />
	<input type="hidden" name="PROTOCOL" value="" />
	<input type="hidden" name="HOST" value="" />
	<input type="hidden" name="PORT" value="" />
	<input type="hidden" name="TIPODOC" value="" />
	<input type="hidden" name="NOMETEMPLATE" value="" />
	<input type="hidden" name="PAGEBACK" value="<%=queryStringBack%>">
</af:form>
  
</body>
</html>
