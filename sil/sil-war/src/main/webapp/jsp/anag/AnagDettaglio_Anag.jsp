<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="com.engiweb.framework.base.*, 
                com.engiweb.framework.configuration.ConfigSingleton,
                 java.lang.*,
                java.text.*, java.util.*,it.eng.sil.util.*, 
                it.eng.afExt.utils.*, it.eng.sil.module.movimenti.constant.Properties,
                java.math.*, it.eng.sil.security.* "%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
    int _funzione=0;

  //flag booleano che discrimina l'inserimento o la modifica
  boolean flag_insert=false;
  
  //inizializzo i campi
  String cdnLavoratore="";
  String strCodiceFiscale="";
  String strCognome="";
  String strNome="";
  String strSesso="";
  String datNasc="";
  String codComNas="";
  String strComNas="";
  String codCittadinanza="";
  String strCittadinanza="";
  String strNazione="";
  String codCittadinanza2="";  
  String strCittadinanza2="";
  String strNazione2="";
  String codstatoCivile="";
  String numFigli="";
  String strNote="";
  String flgMilite="";
  String cdnUtins="";
  String dtmins="";
  String cdnUtmod="";
  String dtmmod="";
  String numKloLavoratore="";
  String strFlgcfOk="";
  InfCorrentiLav infCorrentiLav= null;
  Testata operatoreInfo = null;
  String codCpi = "";
  String inserimentoLav="";
  String cpicomp= "";
  String codCom="";

  //prelevo SUBITO gli eventuali parametri
  //se è stato richiesto l'inserimento abbiamo nella servicerequest
  //il parametro "inserisci" che corrisponde al pulsante premuto
  //Messo a commento dopo l'implementazione delle nuove page per l'inserimento
  //if (serviceRequest.containsAttribute("inserisci")) flag_insert=true;
  
  //  boolean flag_insert=(boolean) flag_insert_str.(1);

  //Recupero CODCPI
  int cdnUt = user.getCodut();
  int cdnTipoGruppo = user.getCdnTipoGruppo();
  if(cdnTipoGruppo == 1) {
    codCpi =  user.getCodRif();
  }
  
  //---------------
  //mi prelevo in ogni caso gli stati civili
  Vector statiCiviliRows=null;
  statiCiviliRows= serviceResponse.getAttributeAsVector("M_GETSTATICIVILI.ROWS.ROW");
  
  String numConfigCurr = serviceResponse.containsAttribute("M_CONFIG_INVIO_CURRICULUM.ROWS.ROW.NUM")?
			 serviceResponse.getAttribute("M_CONFIG_INVIO_CURRICULUM.ROWS.ROW.NUM").toString():Properties.DEFAULT_CONFIG;
	
  String buttonInviaCV = "Cliclavoro";	 
  if (numConfigCurr.equalsIgnoreCase(Properties.CUSTOM_CONFIG)) {
	  buttonInviaCV = "Invia curriculum";  
  }
  
  _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  //se non sto gestendo l'inserimento
  //devo prelevare la risposta che mi è pervenuta dalla get
  if (!flag_insert) {

    String response_xml="M_GETLAVORATOREANAG.ROWS.ROW";
    
    try{
    
      SourceBean row=(SourceBean) serviceResponse.getAttribute(response_xml);

      if (row.containsAttribute("cdnLavoratore")) {cdnLavoratore=row.getAttribute("cdnLavoratore").toString();}      
      if (row.containsAttribute("strCodiceFiscale")) {strCodiceFiscale=row.getAttribute("strCodiceFiscale").toString();}
      if (row.containsAttribute("strCognome")) {strCognome=row.getAttribute("strCognome").toString();}
      if (row.containsAttribute("strNome")) {strNome=row.getAttribute("strNome").toString();}
      if (row.containsAttribute("strSesso")) {strSesso=row.getAttribute("strSesso").toString();}
      if (row.containsAttribute("datNasc")) {datNasc=row.getAttribute("datNasc").toString(); }
      if (row.containsAttribute("codComNas")) {codComNas=row.getAttribute("codComNas").toString();}
      if (row.containsAttribute("strComNas")) {strComNas=row.getAttribute("strComNas").toString();}
      if (row.containsAttribute("CODCOM")) {codCom=row.getAttribute("CODCOM").toString();}
      if (row.containsAttribute("codCittadinanza")) {codCittadinanza=row.getAttribute("codCittadinanza").toString();}
      if (row.containsAttribute("strCittadinanza")) {strCittadinanza=row.getAttribute("strCittadinanza").toString();}
      if (row.containsAttribute("strNazione")) {strNazione=row.getAttribute("strNazione").toString();}
      if (row.containsAttribute("codCittadinanza2")) {codCittadinanza2=row.getAttribute("codCittadinanza2").toString();}
      if (row.containsAttribute("strCittadinanza2")) {strCittadinanza2=row.getAttribute("strCittadinanza2").toString();}
      if (row.containsAttribute("strNazione2")) {strNazione2=row.getAttribute("strNazione2").toString();}
      if (row.containsAttribute("codstatoCivile")) {codstatoCivile=row.getAttribute("codstatoCivile").toString();}
      if (row.containsAttribute("numFigli")) {numFigli=row.getAttribute("numFigli").toString();}
      if (row.containsAttribute("strNote")) {strNote=row.getAttribute("strNote").toString();}
      if (row.containsAttribute("FLGCFOK")) {strFlgcfOk=row.getAttribute("FLGCFOK").toString();}
      if (row.containsAttribute("cdnUtins")) {cdnUtins=row.getAttribute("cdnUtins").toString();}
      if (row.containsAttribute("dtmins")) {dtmins=row.getAttribute("dtmins").toString();}
      if (row.containsAttribute("cdnUtmod")) {cdnUtmod=row.getAttribute("cdnUtmod").toString();}
      if (row.containsAttribute("dtmmod")) {dtmmod=row.getAttribute("dtmmod").toString();}
      if (row.containsAttribute("numKloLavoratore")) {numKloLavoratore=row.getAttribute("numKloLavoratore").toString();}
      flgMilite=row.containsAttribute("dtmins")?row.getAttribute("flgMilite").toString():"";
    
    }
    catch(Exception ex){
      // non faccio niente
    } 

      
    //Controllo di provenienza dalla pagina di inserimento
      if (serviceRequest.containsAttribute("inserimentoLav")) 
      {
      		inserimentoLav=serviceRequest.getAttribute("inserimentoLav").toString();
      } else {
      		SourceBean rowcpi=(SourceBean) serviceResponse.getAttribute("M_GETCPILAVORATORE.ROWS.ROW");
    
			cpicomp=(String) rowcpi.getAttribute("cpicomp");
      }
    if ( (cdnLavoratore.equals("")) && (inserimentoLav.equals("S")) ){
      out.print("<script language=\"javascript\">history.back();</script>");
    }
    infCorrentiLav= new InfCorrentiLav(sessionContainer, cdnLavoratore, user);
    operatoreInfo= new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
  } else { //stiamo provenendo dalla ricerca di un soggetto richiedendo l'inserimento.
		 //ho sicuramente in request alcuni dati fondamentali con i quali precompilare
		 //i campi codicefiscale, nome, cognome, comune di nascita
	strCodiceFiscale=(String) serviceRequest.getAttribute("strCodiceFiscale");
	strCognome=(String) serviceRequest.getAttribute("strCognome");
	strNome=(String) serviceRequest.getAttribute("strNome");
	datNasc=(String) serviceRequest.getAttribute("datNasc");
	codComNas=(String) serviceRequest.getAttribute("codComNas");
	strComNas=(String) serviceRequest.getAttribute("strComNas");
	System.out.println("strCodiceFiscale: "+  strCodiceFiscale + "\n" +
						"strCognome: " + strCognome + "\n"+
						 "strNome: " + strNome + "\n" + 	
						 "DataNas: " + datNasc + "\n" +
						 "codComNas: " + codComNas + "\n" +
						 "strComNas: " + strComNas + "\n");
    }

   // NOTE: Attributi della pagina (pulsanti e link) 
  /*
  PageAttribs attributi = new PageAttribs(user, "AnagDettaglioPageAnag");
  boolean canModify = attributi.containsButton("salva");
  boolean canDelete = attributi.containsButton("rimuovi");
  boolean canScadenzeLav = attributi.containsButton("LISTA_SCADENZE_LAVORATORE");
  boolean canVerificheLav = attributi.containsButton("LISTA_VERIFICHE_LAVORATORE");
  */
/**
	La jsp e' associata a due page, AnagDettaglioPageAnag e AnagDettaglioPageAnagDettaglioGenerale o qualcosa del 
	genere. Ma quest'ultima page non ha attributi, per cui bisogna in maniera esplicita estrarre gli attributi
	di AnagDettaglioPageAnag.
*/
	String _page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, "AnagDettaglioPageAnag");
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
    PageAttribs attributi = new PageAttribs(user, "AnagDettaglioPageAnag");

	boolean canDelete = false;
	boolean canModify = false;
    boolean canScadenzeLav = false;
    boolean canVerificheLav = false;
    boolean canCaricaSchedaLavCOOP = false;
    boolean canGetInfoServiziSociali = false;
    // aggiunti il 05/07/2006
    boolean canPrivacy = false;
  	boolean canValidaCurr = false;
  	boolean canDocAssociati = false;
  	boolean canAccount = false;
  	boolean canCliclavoro= false;
	boolean canBLEN= false;
  	boolean canView=filter.canViewLavoratore();
	boolean canVerificaSAP_Portale = false;
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
      canScadenzeLav = attributi.containsButton("LISTA_SCADENZE_LAVORATORE");
      canVerificheLav = attributi.containsButton("LISTA_VERIFICHE_LAVORATORE");
      canModify = attributi.containsButton("salva");
      canDelete = attributi.containsButton("rimuovi");
      canCaricaSchedaLavCOOP = attributi.containsButton("SCHEDA_LAVORATORE_COOP");
      canGetInfoServiziSociali = attributi.containsButton("INFO_SERVIZI_SOCIALI");
      canPrivacy = attributi.containsButton("PRIVACY");
      canValidaCurr = attributi.containsButton("VAL_CUR");
      canDocAssociati = attributi.containsButton("DOC_ASS");
      canAccount = attributi.containsButton("ACCOUNT");
      canCliclavoro = attributi.containsButton("LISTA_CAND_CLICLAVORO");
      canBLEN = attributi.containsButton("LISTA_CAND_BLEN");
      canVerificaSAP_Portale = attributi.containsButton("IMPORTA_SAP_DA_PORTALE");

    
    	//rdOnly     = !attributi.containsButton("AGGIORNA");
    	if((!canDelete) && (!canModify)){
    		//canInsert=false;
        //rdOnly=true;
    	}else{
        boolean canEdit=filter.canEditLavoratore();
        if (canDelete){
          canDelete=canEdit;
        }
        if (canModify){
          canModify=canEdit;
        }        
    	}
  }

  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
  
  String accountLavoratore="";
  SourceBean rowLav = (SourceBean) serviceResponse.getAttribute("M_AccountAssociato.ROWS.ROW");
  if (rowLav != null) {
  		accountLavoratore = rowLav.containsAttribute("lav")? rowLav.getAttribute("lav").toString():"";
 }
 // 11/09/2006 Savino: se la cooperazione e' disabilitata il pulsante scheda lavoratore va nascosto
 boolean coopAbilitata= false;
 String coopAttiva = System.getProperty("cooperazione.enabled");
 if (coopAttiva != null && coopAttiva.equals("true")) {
  	coopAbilitata = true;
 }
 
%>
<%
// POPUP EVIDENZE
String strApriEv = StringUtils.getAttributeStrNotNull(serviceRequest, "APRI_EV");
int _fun = 1;
if(_funzione>0) { _fun = _funzione; }
EvidenzePopUp jsEvid = null;
boolean bevid = attributi.containsButton("EVIDENZE");
if(strApriEv.equals("1") && bevid) {
	jsEvid = new EvidenzePopUp(cdnLavoratore, _fun, user.getCdnGruppo(), user.getCdnProfilo());
}	
%> 


<%@page import="com.engiweb.framework.message.MessageBundle"%><html>
<head>
<title>Dati personali</title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>

<%if(strApriEv.equals("1") && bevid) { jsEvid.show(out); }%>

<%@ include file="../global/confrontaData.inc" %>

<script type="text/javascript">
<!--
  var objRifWindow;

  //messaggio d'errore nel caso in cui si provi ad estrarre data di nascita e comune da un codice fiscale non corretto.
  var errorString = 'Il Codice Fiscale non è formalmente corretto.\nNon è possibile completare l\'operazione richiesta.'
	   
  function CaricaDataComune () {
	 //eseguo un controllo sul CF. questo controllo viene replicato anche lato server
	if (checkTempCF('strCodiceFiscale')) {
		alert('Non è possibile estrarre data di nascita e comune da un codice fiscale temporaneo');		
		return;
	}
	 
    if (checkCF('strCodiceFiscale')) {
      var s= "AdapterHTTP?PAGE=CalcolaDataComunePageAnag";
      var strCodice = new String();
      strCodice=escape(document.Frm1.strCodiceFiscale.value);
      strCodice=strCodice.toUpperCase(strCodice);
      var width = 0;
      var height = 0;
      var left = parseInt((screen.availWidth/2) - (width/2));
      var top = parseInt((screen.availHeight/2) - (height/2));
      s += "&strcodicefiscale=" + strCodice;
      var windowFeatures = "toolbar=0, scrollbars=0, height="+height+", width="+width;//,left=" + left + ",top=" + top + "screenX=" + left + ",screenY=" + top;
      objRifWindow = window.open(s,"Calcola", windowFeatures );
    }
    /*else  {
        alert(errorString);
    }*/
  }

  function PrendiValori () {
	var valid = objRifWindow.document.Frm.valid.value;
	//se è fallito il controllo lato server stampo il solito messaggio d'errore
	if (valid == "false") {
		objRifWindow.close();
		alert(errorString);
				
	}
	else {
	    document.Frm1.datNasc.value = objRifWindow.document.Frm.data.value;
	    document.Frm1.strSesso.value = objRifWindow.document.Frm.sesso.value;
	    document.Frm1.strComNas.value = objRifWindow.document.Frm.comune.value;
	    document.Frm1.codComNas.value = objRifWindow.document.Frm.codicecomune.value;
	    document.Frm1.strComNasHid.value = objRifWindow.document.Frm.comune.value;
	    document.Frm1.codComNasHid.value = objRifWindow.document.Frm.codicecomune.value;
	    objRifWindow.close();
	}
  }

  function ApriVerificheLavoratore (cdnLavoratore, codcpi) {
	    window.open("AdapterHTTP?PAGE=VerificheLavoratorePage&CODICEFISCALE=<%=strCodiceFiscale%>&CDNLAVORATORE="+cdnLavoratore+"&CODCPI="+codcpi+"&POPUP=true",'','toolbar=0,scrollbars=1');
	  }
	  
  function ApriXmlClickLavoro (strCodiceFiscale) {
	    window.open("AdapterHTTP?PAGE=CL_testPage&CODICEFISCALE="+strCodiceFiscale+"&TIPOLOGIA=1",'','toolbar=0,scrollbars=1');
	  }

  function invioCandidatura2Cliclavoro() {
		// Apre la finestra
		var url = "AdapterHTTP?PAGE=CL_INVIO_CANDIDATURA&CODICEFISCALE=<%=strCodiceFiscale%>"+
		"&CDNLAVORATORE=<%=cdnLavoratore%>&TIPOLOGIA=1"+"&popUp=true";
		var w=(screen.availWidth)*0.85;  var l=(screen.availHeight)*0.1;
		var h=(screen.availHeight)*0.8;  var t=(screen.availHeight)*0.1;
		w=800, h=550
		var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=YES," +
		  			 "height="+h+",width="+w+",top="+t+",left="+l;
		var opened = window.open(url, "_blank", feat);
		opened.focus();		
	  }
  
  function gestioneCliclavoro() {
		// Apre la finestra
		var url = "AdapterHTTP?PAGE=CL_LIST_INVII_CANDIDATURE_PAGE&CODICEFISCALE=<%=strCodiceFiscale%>"+
		"&CDNLAVORATORE=<%=cdnLavoratore%>&TIPOLOGIA=1"+"&popUp=true";
		var w=(screen.availWidth)*0.85;  var l=(screen.availHeight)*0.1;
		var h=(screen.availHeight)*0.8;  var t=(screen.availHeight)*0.1;
		w=800, h=550
		var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=YES," +
		  			 "height="+h+",width="+w+",top="+t+",left="+l;
		var opened = window.open(url, "_blank", feat);
		opened.focus();		
	  }

  function gestioneBLEN() {
		// Apre la finestra
		var url = "AdapterHTTP?PAGE=BL_LIST_INVII_CANDIDATURE_PAGE&CODICEFISCALE=<%=strCodiceFiscale%>"+
		"&CDNLAVORATORE=<%=cdnLavoratore%>&TIPOLOGIA=1"+"&popUp=true";
		var w=(screen.availWidth)*0.85;  var l=(screen.availHeight)*0.1;
		var h=(screen.availHeight)*0.8;  var t=(screen.availHeight)*0.1;
		w=800, h=550
		var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=YES," +
		  			 "height="+h+",width="+w+",top="+t+",left="+l;
		var opened = window.open(url, "_blank", feat);
		opened.focus();		
	  }
	  
// Per rilevare la modifica dei dati da parte dell'utente
var flagChanged = false;  


  function fieldChanged() {
    <% if ((!flag_insert) && (canModify)) { out.print("flagChanged = true;");}%>
  }
  

function apriCurriculum(cdnLav,cdnFunzione,startPage){
    window.open("AdapterHTTP?PAGE=ValidCurPage&MODULE=M_ListValidCur&CDNLAVORATORE=" + cdnLav + "&CDNFUNZIONE=" + cdnFunzione + "&START_PAGE=" + startPage,'','toolbar=0,scrollbars=1,width=700, height=500, left=10, top=10');
}

function apriPrivacy(cdnLav,cdnFunzione){
    window.open("AdapterHTTP?PAGE=PrivacyDettaglioPage&CDNLAVORATORE=" + cdnLav + "&CDNFUNZIONE=" + cdnFunzione,'','toolbar=0,scrollbars=1,width=700, height=500, left=10, top=10');
}

function btFindCittadinanza_onclick(codcittadinanza, codcittadinanzahid, cittadinanza, cittadinanzahid, nazione, nazionehid) {
	
		if (nazione.value == ""){
			nazionehid.value = "";
			codcittadinanzahid.value = "";
			codcittadinanza.value = "";
 			cittadinanzahid.value = "";
			cittadinanza.value = "";
			} else
		if (nazione.value != nazionehid.value) 
			window.open ("AdapterHTTP?PAGE=RicercaCittadinanzaPage&nazione="+nazione.value.toUpperCase()+"&retcod="+codcittadinanza.name+"&retcittadinanza="+cittadinanza.name+"&retnazione="+nazione.name, "Nazionalità", 'toolbar=0, scrollbars=1');
    
}


function valComuneNas(inputName){344456

  var ctrlObj = eval("document.forms[0]." + inputName);
  eval("document.forms[0]."+inputName+".value=document.forms[0]."+inputName+".value.toUpperCase();")
	if (ctrlObj.value=="") {
		alert("Il campo " + ctrlObj.title + " è obbligatorio");
		ctrlObj.focus();
		return false;
	}
	return true;
} //isRequired


function valCittadinanza(inputName){
  var ctrlObj = eval("document.forms[0]." + inputName);
  eval("document.forms[0]."+inputName+".value=document.forms[0]."+inputName+".value.toUpperCase();")
	if (ctrlObj.value=="") {
		alert("Il campo " + ctrlObj.title + " è obbligatorio");
		ctrlObj.focus();
		return false;
	}
	return true;
} //isRequired

function isDigit(c)
	{   
     return ( (c >= '0') && (c <=  '9') );	
  }

//restituisce true se il codice fiscale è di 11 cifre numeriche.
function checkTempCF(inputName) {
	var cfObj = eval("document.forms[0]." + inputName);
	cfObj.value=cfObj.value.toUpperCase();
	cf=cfObj.value;
	ok=true;

	if (cf.length != 11)
		return false;
	
	
	for (i=0; i<11 && ok; i++) {
	   	c=cf.charAt(i);
	    ok=isDigit(c);	
	}		
	
	
	return ok;
}

function checkCF (inputName)
{
  var cfObj = eval("document.forms[0]." + inputName);
  cfObj.value=cfObj.value.toUpperCase();
  cf=cfObj.value;
  ok=true;
  msg="";

  
  if ((document.Frm1.FLGCFOK.value == "") || (document.Frm1.FLGCFOK.value == "N") /*|| (cf.length < 16)*/) {
	//controllo che sia lungo 16 caaratteri  
	if (cf.length==11) {
		//se di 11 caratteri deve essere composto di soli numeri
		msg="<%= MessageBundle.getMessage(Integer.toString(MessageCodes.CodiceFiscale.ERRPROV_NAN))%>";
		for (i=0; i<11 && ok; i++) {
	    	c=cf.charAt(i);
	        ok=isDigit(c);
		}		
	}
	else if (cf.length==16) {
        //scorro tutti i caratteri
        for (i=0; i<16 && ok; i++)
        {
            c=cf.charAt(i);
            if (i>=0 && i<=5){
                    ok=!isDigit(c);
                    msg="Errore nei primi sei caratteri del codice fiscale";
            } else if  (i==6 || i==7) { 
                    ok=isDigit(c);
                    msg="Errore nel settimo o nell'ottavo carattere del codice fiscale";
            } else if (i==8) {
                    ok=!isDigit(c);
                    msg="Errore nel nono carattere del codice fiscale";
            } else if (i==9 || i==10) {
                    ok=isDigit(c);
                    msg="Erore nel decimo o nell'undicesimo carattere del codice fiscale";
            } else if (i==11) {
                    ok=!isDigit(c);
                    msg="Errore nell'undicesimo carattere del codice fiscale";
            //} else if (i>=12 && i <=14) {
            //     ok=isDigit(c);
            //
            //     msg="Errore nel tredicesimo, nel quattordicesimo o nel penultimo carattere del codice fiscale";
            } else if (i==15) {
                    ok=!isDigit(c);
                    msg="Errore nell'ultimo carattere del codice fiscale: deve essere una lettera";
            }
        }           
    } else {
      ok=false;
      msg="<%= MessageBundle.getMessage(Integer.toString(MessageCodes.CodiceFiscale.ERR_LUNGHEZZA))%>";
    }
    if (!ok) {
        alert(msg);
        cfObj.focus();
    }
  } 
  return ok;
}


function checkAnnoNas(inputName){
  var dataObj = eval("document.forms[0]." + inputName);
  datanas=dataObj.value;

  var anni = calcolaEta(datanas);
  if (anni != -1)
  {
    if(anni > 90) {
       alert("Attenzione: il soggetto ha un\'età maggiore di 90 anni");
       return false;
    }
    else if (anni < 15) {
      if ( confirm("Attenzione: il soggetto ha un\'età inferiore di 15 anni.\nInserirlo ugualmente?") ){
         return true;
      }
      else {
        return false;
      }
    }
  
    return true
  }
  return false;
}

function ApriScadenzeLavoratore (cdnLavoratore) {
  window.open("AdapterHTTP?PAGE=ScadenzeLavoratorePage&CDNLAVORATORE="+cdnLavoratore+"&CODCPI=<%=codCpi%>",'','toolbar=0,scrollbars=1');
}
    
function cfIsChecked()
{ var flg = eval(document.forms[0].FLGCFOK);
  //alert("Is check CF? "+flg.value);
  if((flg.value!=null) && (flg.value=="S"))
  { nascondi("CF_NOcheck");
  }
  else
  { mostra("CF_NOcheck");
  }
}

function mostra(id)
{ var div = document.getElementById(id);
  div.style.display="";
}

function nascondi(id)
{ var div = document.getElementById(id);
  div.style.display="none";
}
 function selectVisualizzaDatiPersonali(strCodiceFiscale) {
	//funzionalità disattivata (per il momento)
	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;
	url="AdapterHTTP?Page=CoopCaricaDatiPersonaliPage";
  	url+="&strCodiceFiscale="+strCodiceFiscale;
  	url+="&cdnFunzione=140";
  	url+="&CARICA_SCHEDA_DA_POLO_MASTER=1";
  	var w=800; var l=((screen.availWidth)-w)/1.2;
    var h=600; var t=((screen.availHeight)-h)/1.2;
  	var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
  	var titolo = "scheda_lavoratore_pr";
  	var opened = window.open(url, titolo, feat);
  	opened.focus();
  }
  
  function selectVisualizzaInfoServiziSociali(strCodiceFiscale, codCom) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;
	url="AdapterHTTP?Page=VisualizzaInfoServiziSocialiPage";
	url+="&strCodiceFiscale="+strCodiceFiscale;
  	url+="&codComune="+codCom;
  	url+="&cdnFunzione=140";
  	var w=800; var l=((screen.availWidth)-w)/1.2;
    var h=600; var t=((screen.availHeight)-h)/1.2;
  	var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
  	var titolo = "info_servizi_sociali";
  	var opened = window.open(url, titolo, feat);
  	opened.focus();
  }
  
  function CreaAccount() {
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;
	  
	  var Codice = document.Frm1.strCodiceFiscale.value;
	  var cognome=document.Frm1.strCognome.value;
      var nome=document.Frm1.strNome.value;
      var dataNascita=document.Frm1.datNasc.value;
      var sesso=document.Frm1.strSesso.value;
      
      var url = "AdapterHTTP?PAGE=ProfNuovoUtentePage";
      	url += "&cdnLavoratore="+"<%=cdnLavoratore%>";
      	url += "&cdnFunzione=32";
        url += "&strCognome=" + document.Frm1.strCognome.value;
        url += "&strNome=" + document.Frm1.strNome.value;
        url += "&datNasc=" + document.Frm1.datNasc.value;
        url += "&strSesso=" + Frm1.strSesso.value;
        url += "&strCodiceFiscale=" + document.Frm1.strCodiceFiscale.value;
        
	  setWindowLocation(url);
    }
  
    function callVerificaEsistenzaSAPWS_Portale() {
	    var urlpage="AdapterHTTP?";
	    urlpage +="CDNFUNZIONE=<%=_funzione%>&PAGE=SapPortaleVerificaEsistenzaPage&MODULE=M_SapCallVerificaEsistenzaSapPortale&CDNLAVORATORE=<%=cdnLavoratore%>";
	    
	    window.open(urlpage,'VerificaSAP_Portale','toolbar=NO,statusbar=YES,width=1200,height=600,top=50,left=100,scrollbars=YES,resizable=YES');
	}

//-->
</script>
<%@ include file="../global/apriControllaCF.inc" %>

<%@ include file="../global/Function_CommonRicercaComune.inc" %>

<script language="Javascript" src="../../js/docAssocia.js"></script>

</head>
<body class="gestione" onload="rinfresca();<%if(strApriEv.equals("1") && bevid) { %> apriEvidenze() <%}%>">
<%
if (!flag_insert) {
  if (cdnLavoratore.compareTo("") != 0) {
    Linguette l = new Linguette(user, _funzione, "AnagDettaglioPageAnag", new BigDecimal(cdnLavoratore));
    infCorrentiLav.show(out);
    l.show(out);  
  }
%>
    <script language="Javascript">
      <%if (!cdnLavoratore.equals("")) {%>
        window.top.menu.caricaMenuLav( <%=_funzione%>,   <%=cdnLavoratore%>);
      <%}%>
    </script>
<%}else {%>
<br/><br/>
<%}
%>
<font color="green">
<%if (!flag_insert) {%>
 <af:showMessages prefix="M_SaveLavoratoreAnag"/>
<%}else{%>
  <af:showMessages prefix="M_InsertLavoratore"/>
<%}%>
<%-- 30/08/2006 Savino: se si proviene dalla pagina della scheda lavoratore (reload ) bisogna mostrare il risultato del 
controllo sul codice fiscale --%>
  <af:showMessages prefix="M_CHECK_CODICEFISCALE"/>
</font>
<font color="red">
     <af:showErrors/>
</font>
<%
if (cdnLavoratore.compareTo("") == 0) {
%>
  <p class="titolo"><b>Inserimento lavoratore - dati personali</b></p>
<%}%>      
<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="btFindComuneCAP_onSubmit(document.Frm1.codComNas, document.Frm1.strComNas, null, true)">
<% if (!flag_insert) {%>
    <input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>"/>
<%}%>

<%out.print(htmlStreamTop);%>
<table  class="main" border="0">
<tr>
    <td class="etichetta">Codice Fiscale&nbsp;</td>
    <td class="campo">
    
     <af:textBox type="hidden" name="strCodiceFiscaleHid" value="<%=strCodiceFiscale%>" />
    <af:textBox classNameBase="input" onKeyUp="fieldChanged();" title="Codice fiscale" required="true" type="text" name="strCodiceFiscale" value="<%=strCodiceFiscale%>" size="21" maxlength="16" validateWithFunction="checkCF" readonly="<%= String.valueOf(!canModify) %>" />&nbsp;
    <%if (canModify) { %>
    <A HREF="javascript:CaricaDataComune();"><IMG name="image" border="0" src="../../img/calc.gif" alt="calcola sesso,data e comune" title="Calcola Sesso, Data di nascita e Comune"/></a>&nbsp;
      <!--<A HREF="javascript:apriControllaCF(true,document.Frm1.strNome, document.Frm1.strCognome, document.Frm1.strSesso, document.Frm1.datNasc,document.Frm1.strComNas,document.Frm1.codComNas,document.Frm1.strCodiceFiscale,document.Frm1.FLGCFOK);">-->
      <IMG name="image" border="0" src="../../img/CheckCF.gif" alt="Verifica correttezza codice fiscale"/><!--</a>-->&nbsp;
     
    <%
    }
    %>
    &nbsp;&nbsp;&nbsp;Validità C.F.&nbsp;
    <af:comboBox 
      name="FLGCFOK"
      classNameBase="input"
      disabled="<%= String.valueOf(!canModify) %>"
      onChange="fieldChanged(); cfIsChecked();">
      <option value=""  <% if ( "".equalsIgnoreCase(strFlgcfOk) )  { %>SELECTED="true"<% } %> ></option>
      <option value="S" <% if ( "S".equalsIgnoreCase(strFlgcfOk) ) { %>SELECTED="true"<% } %> >Sì</option>
      <option value="N" <% if ( "N".equalsIgnoreCase(strFlgcfOk) ) { %>SELECTED="true"<% } %> >No</option>
    </af:comboBox>
    </td>
    <!-- SUBMIT -->
</tr>
<tr><td></td><td>
<div id="CF_NOcheck" style="display:">
  <table colspacing="0" cellspacing="0" border="0" width="100%"><tr><td align="left">Codice fiscale non controllato</td></tr></table>
</div>
</td></tr>
<script language="javascript">cfIsChecked();</script>

<tr>
    <td class="etichetta">Cognome&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" title="Cognome" required="true" type="text" name="strCognome" value="<%=strCognome%>" size="30" maxlength="30" readonly="<%= String.valueOf(!canModify) %>" />
    <%if (canModify) { %>
    
        <!--<A HREF="javascript:apriControllaCF(false,document.Frm1.strNome, document.Frm1.strCognome,document.Frm1.strSesso, document.Frm1.datNasc,document.Frm1.strComNas,document.Frm1.codComNas,document.Frm1.strCodiceFiscale,document.Frm1.FLGCFOK);">-->
          <IMG align="absmiddle" name="image" border="0" src="../../img/CheckAnagrafica.gif" alt="Verifica correttezza dati anagrafici"/><!--</a>-->&nbsp;
          
    <%}%>
    </td>
</tr>
<tr>
    <td class="etichetta">Nome&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" title="Nome" required="true" type="text" name="strNome" value="<%=strNome%>" size="30" maxlength="30" readonly="<%= String.valueOf(!canModify) %>" /></td>
</tr>
<tr>
    <td class="etichetta">Sesso&nbsp;</td>
    <td class="campo"><af:comboBox classNameBase="input" name="strSesso" required="true" title="sesso" onChange="fieldChanged();" disabled="<%= String.valueOf(!canModify) %>" >
          <OPTION value="M" <%if (strSesso.equals("M")) out.print("SELECTED=\"true\"");%>>M</OPTION>
          <OPTION value="F" <%if (strSesso.equals("F")) out.print("SELECTED=\"true\"");%>>F</OPTION>
          <!--<OPTION value="N" >Non so</OPTION>-->
        </af:comboBox>
    </td>
</tr>

<tr>
<td class="etichetta">Stato civile&nbsp;</td>
<td class="campo">
  <af:comboBox name="CODSTATOCIVILE"
        title="Stato civile"
        required="false"
        moduleName="M_GetStatiCivili"
        classNameBase="input"
        disabled="<%= String.valueOf( !canModify ) %>"
        onChange="fieldChanged()"
        selectedValue="<%= codstatoCivile %>" 
        addBlank="true" />
</td>
</tr>

<tr>
    <td class="etichetta">Data di Nascita&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" title="Data di nascita" type="date" required="true" name="datNasc" value="<%=datNasc%>" size="11" maxlength="12" validateOnPost="true" validateWithFunction="checkAnnoNas" readonly="<%= String.valueOf(!canModify) %>" /></td>
</tr>
<tr>
    <td class="etichetta">Comune di nascita&nbsp;</td>
    <td class="campo">
      <af:textBox classNameBase="input" title="Codice comune di nascita" onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.codComNas, document.Frm1.codComNasHid, document.Frm1.strComNas, document.Frm1.strComNasHid, null, null, 'codice');"
                  type="text" name="codComNas" value="<%=codComNas%>" size="4" maxlength="4"
                  readonly="<%= String.valueOf(!canModify) %>"/>&nbsp;
    <%if (canModify) { %>
      <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codComNas, document.Frm1.strComNas, null, 'codice','');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
    <%}%>
    <af:textBox type="hidden" name="codComNasHid" value="<%=codComNas%>"/>
    <af:textBox type="text" classNameBase="input" onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.codComNas, document.Frm1.codComNasHid, document.Frm1.strComNas, document.Frm1.strComNasHid, null, null, 'descrizione');" 
                name="strComNas"  value="<%=strComNas%>" size="30" maxlength="50"
                readonly="<%= String.valueOf(!canModify) %>" title="comune di nascita" />&nbsp;*&nbsp;&nbsp;
    <%if (canModify) { %>   
    <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codComNas, document.Frm1.strComNas, null, 'descrizione','');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>&nbsp;
    <%}%>
    <af:textBox type="hidden"  name="strComNasHid" value="<%=strComNas%>"/>
    </td>
</tr>
<tr>
    <td class="etichetta">Nazione di cittadinanza&nbsp;</td>
    <td class="campo">
        <af:textBox classNameBase="input" onKeyUp="fieldChanged();" title="Cittadinanza" type="text" name="strNazione" value="<%=strNazione%>" 
        size="30" maxlength="40" inline="onkeypress=\"if(event.keyCode==13) { event.keyCode=9; this.blur(); }\" 
        onBlur=\"btFindCittadinanza_onclick(document.Frm1.codCittadinanza, document.Frm1.codCittadinanzaHid, 
                                           document.Frm1.strCittadinanza, document.Frm1.strCittadinanzaHid,
                                           document.Frm1.strNazione, document.Frm1.strNazioneHid)\"" required="true" readonly="<%= String.valueOf(!canModify) %>" />
    <af:textBox type="hidden" name="strNazioneHid" value="<%=strNazione%>"/>
    <af:textBox title="Cittadinanza" classNameBase="input" type="text" onKeyUp="fieldChanged();" name="codCittadinanza" value="<%=codCittadinanza%>" size="5" maxlength="5" readonly="true" validateWithFunction="valCittadinanza" />&nbsp;
    <af:textBox type="hidden" name="codCittadinanzaHid" value="<%=codCittadinanza%>"/>
    <af:textBox type="text" classNameBase="input" name="strCittadinanza" value="<%=strCittadinanza%>" size="20" readonly="true"  />&nbsp;
    <af:textBox type="hidden" name="strCittadinanzaHid" value="<%=strCittadinanza%>"/>

    </td>
</tr>
<tr>
    <td class="etichetta">Seconda Nazione di cittadinanza&nbsp;</td>
    <td class="campo">
        <af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="strNazione2" value="<%=strNazione2%>" size="30" maxlength="40"
        inline="onkeypress=\"if(event.keyCode==13) { event.keyCode=9; this.blur(); }\" 
        onBlur=\"btFindCittadinanza_onclick(document.Frm1.codCittadinanza2, document.Frm1.codCittadinanza2Hid, 
                                           document.Frm1.strCittadinanza2, document.Frm1.strCittadinanza2Hid,
                                           document.Frm1.strNazione2, document.Frm1.strNazione2Hid)\""  required="false" readonly="<%= String.valueOf(!canModify) %>" />
    <af:textBox type="hidden" name="strNazione2Hid" value="<%=strNazione2%>" />
    <af:textBox classNameBase="input" type="text" name="codCittadinanza2" value="<%=codCittadinanza2%>" size="5" maxlength="5" readonly="true" />&nbsp;
    <af:textBox type="hidden" name="codCittadinanza2Hid" value="<%=codCittadinanza2%>"/>
    <af:textBox classNameBase="input" type="text" name="strCittadinanza2" value="<%=strCittadinanza2%>" size="20" readonly="true"  />&nbsp;
    <af:textBox type="hidden" name="strCittadinanza2Hid" value="<%=strCittadinanza2%>" />

    </td>
</tr>

<tr>
   <td class="etichetta">Milite esente/assolto</td>
    <td class="campo">
      <af:comboBox 
        name="flgMilite"
        classNameBase="input"
        disabled="<%= String.valueOf(!canModify) %>"
        onChange="fieldChanged()">
        <option value=""  <% if ( "".equalsIgnoreCase(flgMilite) )  { %>SELECTED="true"<% } %> ></option>
        <option value="S" <% if ( "S".equalsIgnoreCase(flgMilite) ) { %>SELECTED="true"<% } %> >Sì</option>
        <option value="N" <% if ( "N".equalsIgnoreCase(flgMilite) ) { %>SELECTED="true"<% } %> >No</option>
      </af:comboBox>
    </td>
</tr>


<tr>
    <td class="etichetta">Note&nbsp;</td>
    <td class="campo"><af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" cols="50" rows="4" name="strNote" value="<%=strNote%>" maxlength="1000"  readonly="<%= String.valueOf(!canModify) %>"  /></td>
</tr>
<!--
<tr>
  <td class="etichetta">Validità C.F.&nbsp;</td>
  <td class="campo">
    <af:comboBox 
        name="FLGCFOK"
        classNameBase="input"
        disabled="<%= String.valueOf(!canModify) %>"
        onChange="fieldChanged()">
        <option value=""  <% if ( "".equalsIgnoreCase(strFlgcfOk) )  { %>SELECTED="true"<% } %> ></option>
        <option value="S" <% if ( "S".equalsIgnoreCase(strFlgcfOk) ) { %>SELECTED="true"<% } %> >Sì</option>
        <option value="N" <% if ( "N".equalsIgnoreCase(strFlgcfOk) ) { %>SELECTED="true"<% } %> >No</option>
      </af:comboBox>
  </td>  
</tr>
-->
<%if (!flag_insert && cdnLavoratore.compareTo("") != 0) {%>
<input type="hidden" name="numKloLavoratore" value="<%=((int) Integer.parseInt(numKloLavoratore) + 1)%>">
<%}%>

<tr><td colspan="2" align="center">
<% if (!flag_insert && cdnLavoratore.compareTo("") != 0) { %>
    <%if (canModify) { %>
      	<br/>
      	<input class="pulsante" type="submit" name="salva" value="Aggiorna">
      	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      	<%-- Savino 12/5/2006: il pulsante va visto sempre --%>
      	<%--if (codCpi.equals(cpicomp)) { --%>
      		<input type="hidden" name="codCpiLav" value="<%=cpicomp%>">
      		<input class="pulsante" type="submit" name="ribadisci_insert_lav_coop" value="Riprova Inserimento IR">
    	<%--}--%>
    <%}%>
    <%if (canCaricaSchedaLavCOOP && coopAbilitata) {%>
	     <input type="button" value="Carica scheda lavoratore" class="pulsanti" onclick="selectVisualizzaDatiPersonali('<%=strCodiceFiscale%>')">
	<%}%>
	<% if(canVerificaSAP_Portale){ %>				
	     <input class="pulsante" onclick="callVerificaEsistenzaSAPWS_Portale()" type="button" name="verificaPortale" value="Scheda Anagrafica Portale">
	<% } %>
	<%if (canGetInfoServiziSociali) {%>
	     <input type="button" value="Info Servizi Sociali" class="pulsanti" onclick="selectVisualizzaInfoServiziSociali('<%=strCodiceFiscale%>','<%=codCom%>')">
	<%}%>
   <% if (canDelete) { %>
      <!--input class="pulsante" type="submit" name="cancella" value="cancella il record"-->
   <%}%>
<%} else { %>
      <input class="pulsante" type="submit" name="insert_lavoratore" value="Inserisci">
<%}//end if

if (cdnLavoratore.compareTo("") != 0) {
%>
<br/>
  <table width="80%" ><tr align=center>
  <%if (canPrivacy) {%> 
  	<td><input class="pulsante" type="button" name="privacy" value="Privacy" onClick="javascript:apriPrivacy('<%=cdnLavoratore%>', '<%=_funzione%>');"></td>
  <%} 
  if(canValidaCurr) {%>
  	<td><input class="pulsante" type="button" name="validita_curriculum" value="Validità curriculum" onClick="javascript:apriCurriculum('<%=cdnLavoratore%>', '<%=_funzione%>','AnagDettaglioPageAnag')"></td>
   <% } 
  if (canScadenzeLav) {%>  
    <td><input class="pulsante" type="button" name="scadenziario" value="Scadenze" onClick="javascript:ApriScadenzeLavoratore('<%=cdnLavoratore%>')"></td>
  <%}
  if (canVerificheLav) {%>
  	<td><input class="pulsante" type="button" name="verifica" value="Verifiche" onClick="javascript:ApriVerificheLavoratore('<%=cdnLavoratore%>','<%=codCpi%>')"></td>
  <%} 
  if(canDocAssociati) {%>  
  	<td><button class="pulsanti" onclick="docAssociati('<%=cdnLavoratore%>','AnagDettaglioPageAnag','<%=_funzione%>','','<%=cdnLavoratore%>')">Documenti associati</button> </td>
   <%}
   if(canAccount && accountLavoratore.equals("")) {%>    
  	<td><input class="pulsante" type="button" name="Crea account" value="Crea account" onClick=CreaAccount();></td>
  <%}%>
  <%--  --%>
   <%if(canCliclavoro) {%>    
 	<td><button class="pulsanti"  onClick="javascript:gestioneCliclavoro('<%=strCodiceFiscale%>')"  type="button" ><%=buttonInviaCV%></button> </td>
  <%}%>
     <%if(canBLEN) {%>    
 	<td><button class="pulsanti"  onClick="javascript:gestioneBLEN('<%=strCodiceFiscale%>')"  type="button" >BLEN</button> </td>
  <%}%>
  </tr>
  </table>
<%
}
%>
</td></tr>
</table>
<%out.print(htmlStreamBottom);
if (!flag_insert){ 
%>
  <center>
  <table>
  <tr><td align="center">
<%
  operatoreInfo.showHTML(out);
%>
  </td></tr>
  </table></center>
<%
}
%>
<input type="hidden" name="PAGE" value="AnagDettaglioPageAnag">
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"%>
<%-- Savino 20/04/2006 modifica codice fiscale indice regionale --%>
<input type="hidden" name="strCodiceFiscalePrecedente" value="<%=strCodiceFiscale%>">
<input type="hidden" name="COD_TIPO_CONFIG" value="AL_ISCR">

</af:form>


<script language="Javascript">
  <% 
       //Genera il Javascript che si occuperà di inserire i links nel footer
       attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore);
  %>
</script>



</body>
</html>