<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                java.math.BigDecimal,
                it.eng.sil.security.*" %>
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
  //Sezione degli indirizzi
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
  
  String strTelRes="";
  String strTelDom="";
  String strTelAltro="";
  String strCell="";
  String strEmail="";
  String strFax="";
  //***************
  InfCorrentiLav infCorrentiLav= null;
  Testata operatoreInfo = null;

  //prelevo SUBITO gli eventuali parametri
  //se è stato richiesto l'inserimento abbiamo nella servicerequest
  //il parametro "inserisci" che corrisponde al pulsante premuto
  if (serviceRequest.containsAttribute("inserisci")) flag_insert=true;
  
  //  boolean flag_insert=(boolean) flag_insert_str.(1);

  //mi prelevo in ogni caso gli stati civili
  Vector statiCiviliRows=null;
  statiCiviliRows= serviceResponse.getAttributeAsVector("M_GETSTATICIVILI.ROWS.ROW");
  String _page = (String)serviceRequest.getAttribute("PAGE");
  cdnLavoratore = (String)serviceRequest.getAttribute("cdnLavoratore");
  _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  String contestoChiamata = serviceRequest.containsAttribute("PROVENIENZA")?serviceRequest.getAttribute("PROVENIENZA").toString():"";
  String funzioneAggiornamento = serviceRequest.containsAttribute("AGG_FUNZ")?serviceRequest.getAttribute("AGG_FUNZ").toString():"";
  String validaMovimenti = serviceRequest.containsAttribute("VALIDAMOV")?serviceRequest.getAttribute("VALIDAMOV").toString():"";
  String context = "";
  //parametro = "S" quando la pagina viene chiamata dalla validazione manuale mobilità
  String validaMobilita = serviceRequest.containsAttribute("VALIDAMOBILITA")?serviceRequest.getAttribute("VALIDAMOBILITA").toString():"";
  String flgCambiamentiDati  = serviceRequest.containsAttribute("flgCambiamentiDati")?serviceRequest.getAttribute("flgCambiamentiDati").toString():"";   
  String prgMovimentoApp = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGMOVIMENTOAPP");
  if (validaMovimenti.equalsIgnoreCase("S") && !prgMovimentoApp.equals("")) {
  	context = serviceRequest.containsAttribute("context")?serviceRequest.getAttribute("context").toString():"";
  }  
  String prgMobilitaApp = StringUtils.getAttributeStrNotNull(serviceRequest, "prgMobilitaIscrApp");   
  if (serviceRequest.containsAttribute("strCodiceFiscale")) {strCodiceFiscale=serviceRequest.getAttribute("strCodiceFiscale").toString();}
  if (serviceRequest.containsAttribute("strCognome")) {strCognome=serviceRequest.getAttribute("strCognome").toString();}
  if (serviceRequest.containsAttribute("strNome")) {strNome=serviceRequest.getAttribute("strNome").toString();}
  if (serviceRequest.containsAttribute("strSesso")) {strSesso=serviceRequest.getAttribute("strSesso").toString();}
  if (serviceRequest.containsAttribute("datNasc")) {datNasc=serviceRequest.getAttribute("datNasc").toString(); }
  if (serviceRequest.containsAttribute("codComNas")) {codComNas=serviceRequest.getAttribute("codComNas").toString();}
  if (serviceRequest.containsAttribute("strComNas")) {strComNas=serviceRequest.getAttribute("strComNas").toString();}
  if (serviceRequest.containsAttribute("codCittadinanza")) {codCittadinanza=serviceRequest.getAttribute("codCittadinanza").toString();}
  if (serviceRequest.containsAttribute("strCittadinanza")) {strCittadinanza=serviceRequest.getAttribute("strCittadinanza").toString();}
  if (serviceRequest.containsAttribute("strNazione")) {strNazione=serviceRequest.getAttribute("strNazione").toString();}
  if (serviceRequest.containsAttribute("codCittadinanza2")) {codCittadinanza2=serviceRequest.getAttribute("codCittadinanza2").toString();}
  if (serviceRequest.containsAttribute("strCittadinanza2")) {strCittadinanza2=serviceRequest.getAttribute("strCittadinanza2").toString();}
  if (serviceRequest.containsAttribute("strNazione2")) {strNazione2=serviceRequest.getAttribute("strNazione2").toString();}
  if (serviceRequest.containsAttribute("codstatoCivile")) {codstatoCivile=serviceRequest.getAttribute("codstatoCivile").toString();}
  if (serviceRequest.containsAttribute("numFigli")) {numFigli=serviceRequest.getAttribute("numFigli").toString();}
  if (serviceRequest.containsAttribute("strNote")) {strNote=serviceRequest.getAttribute("strNote").toString();}
  if (serviceRequest.containsAttribute("FLGCFOK")) {strFlgcfOk=serviceRequest.getAttribute("FLGCFOK").toString();}
  if (serviceRequest.containsAttribute("cdnUtins")) {cdnUtins=serviceRequest.getAttribute("cdnUtins").toString();}
  if (serviceRequest.containsAttribute("dtmins")) {dtmins=serviceRequest.getAttribute("dtmins").toString();}
  if (serviceRequest.containsAttribute("cdnUtmod")) {cdnUtmod=serviceRequest.getAttribute("cdnUtmod").toString();}
  if (serviceRequest.containsAttribute("dtmmod")) {dtmmod=serviceRequest.getAttribute("dtmmod").toString();}
  if (serviceRequest.containsAttribute("numKloLavoratore")) {numKloLavoratore=serviceRequest.getAttribute("numKloLavoratore").toString();}
  //Bugfix Alessandro Pegoraro
  //flgMilite=serviceRequest.containsAttribute("dtmins")?serviceRequest.getAttribute("flgMilite").toString():"";
  if (serviceRequest.containsAttribute("flgMilite")) {flgMilite=serviceRequest.getAttribute("flgMilite").toString();}
 
  //Se arrivo dalla validazione mobilità per aggiungere il lavoratore i dati li prendo dalla tabella di appoggio
  if (validaMobilita.equalsIgnoreCase("S") && !prgMobilitaApp.equals("")) {
  	SourceBean dataOrigin = (SourceBean) serviceResponse.getAttribute("M_MobilitaGetDettaglioMobApp.ROWS.ROW");
  	if (dataOrigin != null) {
	  strCodiceFiscale=StringUtils.getAttributeStrNotNull( dataOrigin, "STRCODICEFISCALE");
	  strCognome=StringUtils.getAttributeStrNotNull( dataOrigin, "STRCOGNOME");
	  strNome=StringUtils.getAttributeStrNotNull( dataOrigin, "STRNOME");
	  strSesso=StringUtils.getAttributeStrNotNull( dataOrigin, "STRSESSO");
	  datNasc=StringUtils.getAttributeStrNotNull( dataOrigin, "DATNASC");
	  codComNas=StringUtils.getAttributeStrNotNull( dataOrigin, "CODCOMNASC");
	  strComNas=StringUtils.getAttributeStrNotNull( dataOrigin, "STRCOMNASC");
	  codCittadinanza=StringUtils.getAttributeStrNotNull( dataOrigin, "CODCITTADINANZA");
	  strCittadinanza=StringUtils.getAttributeStrNotNull( dataOrigin, "STRCITTADINANZA");
	  strNazione=StringUtils.getAttributeStrNotNull( dataOrigin, "STRNAZIONE");	  
  	}
  }
  else {
	  //Se arrivo dalla validazione per aggiungere il lavoratore i dati li prendo dalla tabella di appoggio
	  if (validaMovimenti.equalsIgnoreCase("S") && !prgMovimentoApp.equals("")) {
	  	SourceBean dataOrigin = (SourceBean) serviceResponse.getAttribute("M_MovGetDettMovApp.ROWS.ROW");
	  	if (dataOrigin != null) {
		  strCodiceFiscale=StringUtils.getAttributeStrNotNull( dataOrigin, "strCodiceFiscaleLav");
		  strCognome=StringUtils.getAttributeStrNotNull( dataOrigin, "strCognomeLav");
		  strNome=StringUtils.getAttributeStrNotNull( dataOrigin, "strNomeLav");
		  strSesso=StringUtils.getAttributeStrNotNull( dataOrigin, "STRSESSOLAV");
		  datNasc=StringUtils.getAttributeStrNotNull( dataOrigin, "datNascLav");
		  codComNas=StringUtils.getAttributeStrNotNull( dataOrigin, "codComNascLav");
		  strComNas=StringUtils.getAttributeStrNotNull( dataOrigin, "strComNascLav");
		  codCittadinanza=StringUtils.getAttributeStrNotNull( dataOrigin, "codCittadinanzaLav");
		  strCittadinanza=StringUtils.getAttributeStrNotNull( dataOrigin, "strCittadinanzaLav");
		  strNazione=StringUtils.getAttributeStrNotNull( dataOrigin, "strNazioneLav");	  
	  	}
	  }
	}

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
    if (serviceRequest.containsAttribute("codCPIText")) {codCpiTit=serviceRequest.getAttribute("codCPIText").toString();}//codCPI
    if (serviceRequest.containsAttribute("strCpi")) {strCpiTit=serviceRequest.getAttribute("strCpi").toString();}

    //Recapiti
    if (serviceRequest.containsAttribute("STRTELRES")) { strTelRes = serviceRequest.getAttribute("STRTELRES").toString();}
    if (serviceRequest.containsAttribute("STRTELDOM")) { strTelDom = serviceRequest.getAttribute("STRTELDOM").toString();}
    if (serviceRequest.containsAttribute("STRTELALTRO")) { strTelAltro = serviceRequest.getAttribute("STRTELALTRO").toString();}
    if (serviceRequest.containsAttribute("STRCELL")) { strCell = serviceRequest.getAttribute("STRCELL").toString();}
    if (serviceRequest.containsAttribute("STREMAIL")) { strEmail = serviceRequest.getAttribute("STREMAIL").toString();}
    if (serviceRequest.containsAttribute("STRFAX")) { strFax = serviceRequest.getAttribute("STRFAX").toString();}
  //*****************************

    String insertNewLav = StringUtils.getAttributeStrNotNull(serviceResponse,"M_INSERTLAVORATOREANAGINDIRIZZI.operationResult");
    String errorMesg = "";
    if (insertNewLav.equalsIgnoreCase("ERROR"))
    { errorMesg = (String) serviceResponse.getAttribute("M_INSERTLAVORATOREANAGINDIRIZZI.errorMesg");
    }
    // N.B. (savino 04/04/05)
	// la pagina fa riferimento agli attributi di visibilita' di AnagDettaglioPageAnag, in particolare di "inserisci"
    PageAttribs attributi = new PageAttribs(user, "AnagDettaglioPageAnag"); 
    ProfileDataFilter filter = new ProfileDataFilter(user, "AnagDettaglioPageAnag");
  	boolean canModify = false;
  	boolean canView=filter.canView();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
		canModify = attributi.containsButton("INSERISCI");
	}  		
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>
<%@page import="com.engiweb.framework.message.MessageBundle"%>
<html>
<head>
<title>Dati personali</title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>
<%@ include file="../global/confrontaData.inc" %>

<SCRIPT TYPE="text/javascript">
  var objRifWindow;
  function CaricaDataComune () {
    if (checkCF('strCodiceFiscale')) {
      var s= "AdapterHTTP?PAGE=CalcolaDataComunePageAnag";
      var strCodice = new String();
      strCodice=escape(document.Frm1.strCodiceFiscale.value);
      strCodice=strCodice.toUpperCase(strCodice);
      s += "&strcodicefiscale=" + strCodice;
      objRifWindow = window.open(s,"Calcola", 'toolbar=0, scrollbars=0, height=0, width=0');
    }
  }

  function PrendiValori () {
    document.Frm1.datNasc.value = objRifWindow.document.Frm.data.value;
    document.Frm1.strSesso.value = objRifWindow.document.Frm.sesso.value;
    document.Frm1.strComNas.value = objRifWindow.document.Frm.comune.value;
    document.Frm1.codComNas.value = objRifWindow.document.Frm.codicecomune.value;
    document.Frm1.strComNasHid.value = objRifWindow.document.Frm.comune.value;
    document.Frm1.codComNasHid.value = objRifWindow.document.Frm.codicecomune.value;
    objRifWindow.close();
  }
  

<!--
// Per rilevare la modifica dei dati da parte dell'utente
var flagChanged = false;  


  function fieldChanged() {
    <% if ((!flag_insert) && (canModify)) { out.print("flagChanged = true;");}%>
  }
  

function apriCurriculum(cdnLav,cdnFunzione,startPage){
    window.open("AdapterHTTP?PAGE=ValidCurPage&MODULE=M_ListValidCur&CDNLAVORATORE=" + cdnLav + "&CDNFUNZIONE=" + cdnFunzione + "&START_PAGE=" + startPage,'','toolbar=0,scrollbars=1,width=700, height=500, left=10, top=10');
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


function valComuneNas(inputName){

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
     return ( (c >= "0") && (c <=  '9') );	
  }

function checkCF (inputName)
{
  var cfObj = eval("document.forms[0]." + inputName);
  cfObj.value=cfObj.value.toUpperCase();
  cf=cfObj.value;
  ok=true;
  msg="";
  
  if ((document.Frm1.FLGCFOK.value == "") || (document.Frm1.FLGCFOK.value == "N") || (cf.length < 16)) {
	  if (cf.length==11) {
	        //se di 11 caratteri deve essere composto di soli numeri
	        msg="<%= MessageBundle.getMessage(Integer.toString(MessageCodes.CodiceFiscale.ERRPROV_NAN))%>";
	        for (i=0; i<11 && ok; i++) {
	            c=cf.charAt(i);
	            ok=isDigit(c);
	        }       
	  }
	  else if (cf.length==16) {
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
            } else if (i==15) {
                    ok=!isDigit(c);
                    msg="Errore nell'ultimo carattere del codice fiscale: deve essere una lettera";
            }
        }
    } else {
      ok=false;
      msg="Il codice fiscale deve essere di 16 caratteri";
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

//-->
</SCRIPT>
<%@ include file="../global/apriControllaCF.inc" %>

<%@ include file="../global/Function_CommonRicercaComune.inc" %>
<script language="Javascript" src="../../js/docAssocia.js"></script>

</head>
<body class="gestione" onload="rinfresca()"> 
<font color="green">
  <af:showMessages prefix="M_SaveLavoratoreAnag"/>
  <af:showMessages prefix="M_InsertLavoratore"/>
</font>
<font color="red">
     <af:showErrors/>
</font>
<% if( errorMesg != null && !errorMesg.equals("")) {%>
   <ul><li><%=errorMesg%></li></ul>
<% }%>
<p class="titolo"><b>Inserimento lavoratore - dati personali</b></p>
<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="btFindComuneCAP_onSubmit(document.Frm1.codComNas, document.Frm1.strComNas, null, true)">

  <input type="hidden" name="PAGE" value="AnagDettaglioPageIndirizziIns">
  <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
  <!-- Sezione per gli indirizzi -->
  <input type="hidden" name="codComRes" value="<%=codComRes%>">
  <input type="hidden" name="provRes" value="<%=provRes%>">
  <input type="hidden" name="strComRes" value="<%=strComRes%>">
  <input type="hidden" name="strIndirizzores" value="<%=strIndirizzores%>">
  <input type="hidden" name="strLocalitares" value="<%=strLocalitares%>">
  <input type="hidden" name="strCapRes" value="<%=strCapRes%>">
  <input type="hidden" name="codComdom" value="<%=codComdom%>">
  <input type="hidden" name="provDom" value="<%=provDom%>">
  <input type="hidden" name="strComdom" value="<%=strComdom%>">
  <input type="hidden" name="strIndirizzodom" value="<%=strIndirizzodom%>">
  <input type="hidden" name="strLocalitadom" value="<%=strLocalitadom%>">
  <input type="hidden" name="strCapDom" value="<%=strCapDom%>">
  <input type="hidden" name="codCPIText" value="<%=codCpiTit%>"><!--codCPI-->
  <input type="hidden" name="strCpi" value="<%=strCpiTit%>">

  <input type="hidden" name="strTelRes" value="<%=strTelRes%>">
  <input type="hidden" name="strTelDom" value="<%=strTelDom%>">
  <input type="hidden" name="strTelAltro" value="<%=strTelAltro%>">
  <input type="hidden" name="strCell" value="<%=strCell%>">
  <input type="hidden" name="strEmail" value="<%=strEmail%>">
  <input type="hidden" name="strFax" value="<%=strFax%>">
  <!-- campi che indicano la provenienza per effettuare delle gestioni 
  particolari (es. MOVIMENTI) 
  -->
  <input type="hidden" name="PROVENIENZA" value="<%=contestoChiamata%>">
  <input type="hidden" name="AGG_FUNZ" value="<%=funzioneAggiornamento%>">
  
<%out.print(htmlStreamTop);%>
<table  class="main">
<tr>
    <td class="etichetta">Codice Fiscale&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" title="Codice fiscale" required="true" type="text" name="strCodiceFiscale" value="<%=strCodiceFiscale%>" size="21" maxlength="16" validateWithFunction="checkCF" readonly="<%= String.valueOf(!canModify) %>" />&nbsp;
    <%if (canModify) { %>
    <A HREF="javascript:CaricaDataComune();"><IMG name="image" border="0" src="../../img/calc.gif" alt="calcola sesso,data e comune"/></a>&nbsp;
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
      <option value=""  <% if ( "".equalsIgnoreCase(strFlgcfOk) )  { %>SELECTED="selected"<% } %> ></option>
      <option value="S" <% if ( "S".equalsIgnoreCase(strFlgcfOk) ) { %>SELECTED="selected"<% } %> >Sì</option>
      <option value="N" <% if ( "N".equalsIgnoreCase(strFlgcfOk) ) { %>SELECTED="selected"<% } %> >No</option>
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
          <OPTION value="">&nbsp;</OPTION>
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
                  readonly="<%= String.valueOf(!canModify) %>"/>&nbsp;*&nbsp;&nbsp;
    <%if (canModify) { %>
      <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codComNas, document.Frm1.strComNas, null, 'codice','');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
    <%
    }
    %>
    <af:textBox type="hidden" name="codComNasHid" value="<%=codComNas%>"/>
    <af:textBox type="text" classNameBase="input" onKeyUp="fieldChanged();PulisciRicerca(document.Frm1.codComNas, document.Frm1.codComNasHid, document.Frm1.strComNas, document.Frm1.strComNasHid, null, null, 'descrizione');" 
                name="strComNas"  value="<%=strComNas%>" size="30" maxlength="50"
                readonly="<%= String.valueOf(!canModify) %>" title="comune di nascita" />&nbsp;
    <%if (canModify) { %>   
    <A HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codComNas, document.Frm1.strComNas, null, 'descrizione','');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>&nbsp;
    <%
    }
    %>
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
        <option value=""  <% if ( "".equalsIgnoreCase(flgMilite) )  { %>SELECTED="selected"<% } %> ></option>
        <option value="S" <% if ( "S".equalsIgnoreCase(flgMilite) ) { %>SELECTED="selected"<% } %> >Sì</option>
        <option value="N" <% if ( "N".equalsIgnoreCase(flgMilite) ) { %>SELECTED="selected"<% } %> >No</option>
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
<tr><td colspan="2" align="center">
<%if (canModify) {%>
<input class="pulsante" type="submit" name="avanti" value="Avanti >> ">
<%} else {%>
&nbsp;
<%}%>
<!--
<br/><br/><br/>
<input class="pulsante" type="submit" name="insert_lavoratore_coop" value="Inserisci in cooperazione">
-->
</td></tr>
</table>
<%out.print(htmlStreamBottom);%>
<%if (validaMobilita.equals("S")) {%>
	<input type="hidden" name="codFiscaleLavMov" value="">
  	<input type="hidden" name="nomeLavMov" value="">
  	<input type="hidden" name="cognomeLavMov" value="">
  	<input type="hidden" name="datNascitaLavMov" value="">
  	<input type="hidden" name="VALIDAMOBILITA" value="<%=validaMobilita%>">
  	<input type="hidden" name="flgCambiamentiDati" value="<%=flgCambiamentiDati%>">
  	<input type="hidden" name="prgMobilitaIscrApp" value="<%=prgMobilitaApp%>">
<%}
else {
	if (validaMovimenti.equals("S")) {%>
  		<input type="hidden" name="codFiscaleLavMov" value="">
  		<input type="hidden" name="nomeLavMov" value="">
  		<input type="hidden" name="cognomeLavMov" value="">
  		<input type="hidden" name="datNascitaLavMov" value="">
  		<input type="hidden" name="VALIDAMOV" value="<%=validaMovimenti%>">
  		<input type="hidden" name="CONTEXT" value="<%=context%>">
  		<input type="hidden" name="flgCambiamentiDati" value="<%=flgCambiamentiDati%>">
  		<input type="hidden" name="PRGMOVIMENTOAPP" value="<%=prgMovimentoApp%>">  
	<%}
}%>
</af:form>
<script language="Javascript">
<% 
  //Genera il Javascript che si occuperà di inserire i links nel footer
  //attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore);
  if ( validaMovimenti.equals("S") || validaMobilita.equals("S") ) {%>
    if (document.Frm1.strCodiceFiscale.value == "") {
      document.Frm1.strCodiceFiscale.value = window.opener.document.frmLav.codFiscaleLavMov.value;
    }
    if (document.Frm1.strNome.value == "") {
      document.Frm1.strNome.value = window.opener.document.frmLav.nomeLavMov.value;
    }
    if (document.Frm1.strCognome.value == "") {
      document.Frm1.strCognome.value = window.opener.document.frmLav.cognomeLavMov.value;
    }
    if (document.Frm1.datNasc.value == "") {
      document.Frm1.datNasc.value = window.opener.document.frmLav.datNascitaLavMov.value;
    }

    document.Frm1.codFiscaleLavMov.value = window.opener.document.frmLav.codFiscaleLavMov.value;
    document.Frm1.nomeLavMov.value = window.opener.document.frmLav.nomeLavMov.value;
    document.Frm1.cognomeLavMov.value = window.opener.document.frmLav.cognomeLavMov.value;
    document.Frm1.datNascitaLavMov.value = window.opener.document.frmLav.datNascitaLavMov.value;
  <%}%>
</script>
</body>
</html>