<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
  it.eng.sil.util.*, 
  it.eng.afExt.utils.StringUtils,
  it.eng.afExt.utils.DateUtils,
  java.lang.*,
  java.text.*, 
  java.util.*,
  java.math.*,
  it.eng.sil.security.*"%>

<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
boolean flag_insert=serviceRequest.containsAttribute("inserisci");
String pagina_back=StringUtils.getAttributeStrNotNull(serviceRequest,"ret");
String strFlgDatiOk="";
String strFlgObbligoL68="";
String accorpa = StringUtils.getAttributeStrNotNull(serviceRequest, "ACCORPAMENTO");

/*Vector CCNLRows=null;
CCNLRows=serviceResponse.getAttributeAsVector("M_GETCCNL.ROWS.ROW");
SourceBean row_CCNL= null;*/


SourceBean aziendaRow=null;
SourceBean unitaRows=null;

String prgUnita="";
String prgAzienda="";
String strCodiceFiscale="";
String strPartitaIva="";
String strRagioneSociale="";
String strHistory="";
String strNote="";
String codNatGiuridica="";
String codTipoAzienda="";
String strSitoInternet="";
//String codAteco="";
String strDesAteco="";
String tipo_ateco="";
String strDescAttivita="";
String tipo_ccnl="";
String codCCNL="";
String desCCNL="";
String numSoci="";
String numDipendenti="";
String numCollaboratori="";
String numAltraPosizione="";
//String numREA="";
String datInizio="";
String datAggInformazione="";
String datFine="";
String cdnUtins="";
String dtmins="";
String cdnUtmod="";
String dtmmod="";
String prgMov="";
String strNumAlboInterinali="";
String strNumAgSomministrazione="";
String strRepartoInail="";
String patInail="";
String patInail1 = "";
String patInail2 = "";  
String datCambioRagSoc = "";
BigDecimal numKloAzienda=null;

Testata operatoreInfo=null;
prgMov = serviceRequest.containsAttribute("PRGMOVIMENTO")?serviceRequest.getAttribute("PRGMOVIMENTO").toString():"";
String configOrganico = serviceResponse.containsAttribute("M_GetConfigCalcolaOrganico.ROWS.ROW.NUM")?serviceResponse.getAttribute("M_GetConfigCalcolaOrganico.ROWS.ROW.NUM").toString():"0";

if (!flag_insert) {

  prgAzienda= (String)serviceRequest.getAttribute("prgAzienda");
  prgUnita  = StringUtils.notNull( (String)serviceRequest.getAttribute("prgUnita") );

  aziendaRow= (SourceBean) serviceResponse.getAttribute("M_GetTestataAzienda.ROWS.ROW");
  unitaRows=(SourceBean) serviceResponse.getAttribute("M_RicercaUnitaAzienda");
  
  strCodiceFiscale= StringUtils.getAttributeStrNotNull(aziendaRow, "strCodiceFiscale");
  strPartitaIva=StringUtils.getAttributeStrNotNull(aziendaRow, "strPartitaIva");
  strRagioneSociale=StringUtils.getAttributeStrNotNull(aziendaRow, "strRagioneSociale");
  strHistory=StringUtils.getAttributeStrNotNull(aziendaRow, "strHistory");
  strNote=StringUtils.getAttributeStrNotNull(aziendaRow, "strNote");
  codNatGiuridica=StringUtils.getAttributeStrNotNull(aziendaRow, "codNatGiuridica");
  codTipoAzienda=StringUtils.getAttributeStrNotNull(aziendaRow, "codTipoAzienda");
  strSitoInternet=StringUtils.getAttributeStrNotNull(aziendaRow, "strSitoInternet");
  //codAteco=StringUtils.getAttributeStrNotNull(aziendaRow, "codAteco");
  strDesAteco =StringUtils.getAttributeStrNotNull(aziendaRow, "strDesAteco");
  tipo_ateco =StringUtils.getAttributeStrNotNull(aziendaRow, "tipo_ateco");
  strDescAttivita=StringUtils.getAttributeStrNotNull(aziendaRow, "strDescAttivita");
  tipo_ccnl=StringUtils.getAttributeStrNotNull(aziendaRow, "tipoCCNL");
  codCCNL=StringUtils.getAttributeStrNotNull(aziendaRow, "codCCNL");
  desCCNL=StringUtils.getAttributeStrNotNull(aziendaRow, "desCCNL");
  numSoci=aziendaRow.containsAttribute("numSoci") ? aziendaRow.getAttribute("numSoci").toString() : "";
  numDipendenti=aziendaRow.containsAttribute("numDipendenti") ? aziendaRow.getAttribute("numDipendenti").toString() : "";
  numCollaboratori=aziendaRow.containsAttribute("numCollaboratori") ? aziendaRow.getAttribute("numCollaboratori").toString() : "";
  numAltraPosizione=aziendaRow.containsAttribute("numAltraPosizione") ? aziendaRow.getAttribute("numAltraPosizione").toString() : "";
  //numREA=aziendaRow.containsAttribute("numREA") ? aziendaRow.getAttribute("numREA").toString() : "";
  datInizio=StringUtils.getAttributeStrNotNull(aziendaRow, "datInizio");
  datAggInformazione=StringUtils.getAttributeStrNotNull(aziendaRow, "datAggInformazione");  
  datFine=StringUtils.getAttributeStrNotNull(aziendaRow, "datFine");
  strFlgDatiOk=aziendaRow.containsAttribute("FLGDATIOK") ? aziendaRow.getAttribute("FLGDATIOK").toString() : "";
  strFlgObbligoL68=aziendaRow.containsAttribute("FLGOBBLIGOL68") ? aziendaRow.getAttribute("FLGOBBLIGOL68").toString() : "";
  cdnUtins= aziendaRow.containsAttribute("cdnUtins") ? aziendaRow.getAttribute("cdnUtins").toString() : "";
  dtmins=aziendaRow.containsAttribute("dtmins") ? aziendaRow.getAttribute("dtmins").toString() : "";
  cdnUtmod=aziendaRow.containsAttribute("cdnUtmod") ? aziendaRow.getAttribute("cdnUtmod").toString() : "";
  dtmmod=aziendaRow.containsAttribute("dtmmod") ? aziendaRow.getAttribute("dtmmod").toString() : "";
  numKloAzienda=aziendaRow.containsAttribute("numKloAzienda") ? (BigDecimal) aziendaRow.getAttribute("numKloAzienda") : null;
  numKloAzienda=numKloAzienda.add(new BigDecimal(1));
  strNumAlboInterinali=aziendaRow.containsAttribute("strNumAlboInterinali") ? aziendaRow.getAttribute("strNumAlboInterinali").toString() : "";
  strNumAgSomministrazione=aziendaRow.containsAttribute("strNumAgSomministrazione") ? aziendaRow.getAttribute("strNumAgSomministrazione").toString() : "";
  
  strRepartoInail=aziendaRow.containsAttribute("strRepartoInail") ? aziendaRow.getAttribute("strRepartoInail").toString() : "";
  patInail=aziendaRow.containsAttribute("strPatInail") ? aziendaRow.getAttribute("strPatInail").toString() : "";
 
  //DONA
  datCambioRagSoc = aziendaRow.containsAttribute("datCambioRagSoc") ? aziendaRow.getAttribute("datCambioRagSoc").toString() : "";
   
  //Gestione spezzatino Pat Inail
  patInail1 = patInail.substring(0, (patInail.length() >= 8 ? 8 : patInail.length()));
  patInail2 = patInail.substring((patInail.length() >= 8 ? 8 : patInail.length()), (patInail.length() >= 10 ? 10 : patInail.length()));

  //InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
  operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
  
 }
 else {
    //oggetto che contiene le informazioni dell'azienda che si sta per inserire
    //vengono utilizzate quando l'inserimento della testata va in errore
    //(tiene traccia dei dati che si volevano inserire)
    //NavigationCache objTestataAz = null;
    //objTestataAz = (NavigationCache) sessionContainer.getAttribute("AZIENDACORRENTE");
    //SourceBean objTestataAz = (SourceBean) serviceRequest.getAttribute("AZIENDACORRENTE");
    //if (objTestataAz != null) {
      //objTestataAz.enable();

      strCodiceFiscale = serviceRequest.getAttribute("strCodiceFiscale") != null ? serviceRequest.getAttribute("strCodiceFiscale").toString() : "";
      strRagioneSociale = serviceRequest.getAttribute("strRagioneSociale") != null ? serviceRequest.getAttribute("strRagioneSociale").toString() : "";
      strPartitaIva = serviceRequest.getAttribute("strPartitaIva") != null ? serviceRequest.getAttribute("strPartitaIva").toString() : "";
      strFlgDatiOk = serviceRequest.getAttribute("FLGDATIOK") != null ? serviceRequest.getAttribute("FLGDATIOK").toString() : "";
	  strFlgObbligoL68= serviceRequest.getAttribute("FLGOBBLIGOL68") != null ? serviceRequest.getAttribute("FLGOBBLIGOL68").toString() : "";
      codNatGiuridica = serviceRequest.getAttribute("codNatGiuridica") != null ? serviceRequest.getAttribute("codNatGiuridica").toString() : "";
      codTipoAzienda = serviceRequest.getAttribute("codTipoAzienda") != null ? serviceRequest.getAttribute("codTipoAzienda").toString() : "";
      strSitoInternet = serviceRequest.getAttribute("strSitoInternet") != null ? serviceRequest.getAttribute("strSitoInternet").toString() : "";
      strDescAttivita = serviceRequest.getAttribute("strDescAttivita") != null ? serviceRequest.getAttribute("strDescAttivita").toString() : "";
      codCCNL = serviceRequest.getAttribute("codCCNL") != null ? serviceRequest.getAttribute("codCCNL").toString() : "";
      desCCNL = serviceRequest.getAttribute("strCCNL") != null ? serviceRequest.getAttribute("strCCNL").toString() : "";
      numSoci = serviceRequest.getAttribute("numSoci") != null ? serviceRequest.getAttribute("numSoci").toString() : "";
      numDipendenti = serviceRequest.getAttribute("numDipendenti") != null ? serviceRequest.getAttribute("numDipendenti").toString() : "";
      numCollaboratori = serviceRequest.getAttribute("numCollaboratori") != null ? serviceRequest.getAttribute("numCollaboratori").toString() : "";
      numAltraPosizione = serviceRequest.getAttribute("numAltraPosizione") != null ? serviceRequest.getAttribute("numAltraPosizione").toString() : "";
      patInail1 = serviceRequest.getAttribute("STRPATINAIL1") != null ? serviceRequest.getAttribute("STRPATINAIL1").toString() : "";
      patInail2 = serviceRequest.getAttribute("STRPATINAIL2") != null ? serviceRequest.getAttribute("STRPATINAIL2").toString() : "";
      patInail = serviceRequest.getAttribute("STRPATINAIL") != null ? serviceRequest.getAttribute("STRPATINAIL").toString() : "";
      strRepartoInail = serviceRequest.getAttribute("strRepartoInail") != null ? serviceRequest.getAttribute("strRepartoInail").toString() : "";
      strNumAlboInterinali = serviceRequest.getAttribute("strNumAlboInterinali") != null ? serviceRequest.getAttribute("strNumAlboInterinali").toString() : "";
      strNumAgSomministrazione = serviceRequest.getAttribute("strNumAgSomministrazione") != null ? serviceRequest.getAttribute("strNumAgSomministrazione").toString() : "";
      datInizio = serviceRequest.getAttribute("datInizio") != null ? serviceRequest.getAttribute("datInizio").toString() : "";
      datFine = serviceRequest.getAttribute("datFine") != null ? serviceRequest.getAttribute("datFine").toString() : "";
      strHistory = serviceRequest.getAttribute("strHistory") != null ? serviceRequest.getAttribute("strHistory").toString() : "";      
      strNote = serviceRequest.getAttribute("strNote") != null ? serviceRequest.getAttribute("strNote").toString() : "";
      //DONA
      datCambioRagSoc = serviceRequest.getAttribute("datCambioRagSoc") != null ? serviceRequest.getAttribute("datCambioRagSoc").toString() : "";
      //objTestataAz.disable();
      //sessionContainer.delAttribute("AZIENDACORRENTE");
    //} 
 }
  
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  /* paramentro utilizzato quando dai movimenti inserisco una nuova azienda */
  String funzioneaggiornamento = "";
  String strContesto = serviceRequest.containsAttribute("CONTESTOPROVENIENZA")?serviceRequest.getAttribute("CONTESTOPROVENIENZA").toString():"";
  //if (strContesto.equals("MOVIMENTI")) {
    funzioneaggiornamento = serviceRequest.containsAttribute("AGG_FUNZ")?serviceRequest.getAttribute("AGG_FUNZ").toString():"";
  //}
  /* paramentro utilizzato quando dai movimenti inserisco una nuova sede aziendale */
  String funzioneaggiornamentounita = serviceRequest.containsAttribute("AGG_FUNZ_INS_UNITA")?serviceRequest.getAttribute("AGG_FUNZ_INS_UNITA").toString():"";

  // NOTE: Attributi della pagina (pulsanti e link) 
	ProfileDataFilter filter = new ProfileDataFilter(user, "IdoTestataAziendaPage");
  boolean canView = false;
  if ( !flag_insert && !prgUnita.equals("")) {
    filter.setPrgAzienda(new BigDecimal(prgAzienda));
    filter.setPrgUnita(new BigDecimal(prgUnita));
    canView=filter.canViewUnitaAzienda();
  } else {
    canView = true; // sono in inserimento, la visibilità dei dati non ha senso
  }
  
	if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
    PageAttribs attributi = new PageAttribs(user, "IdoTestataAziendaPage");
  
    boolean canModify          = attributi.containsButton("aggiorna");
    boolean canDoAccorpa       = attributi.containsButton("accorpa");
    boolean canInsert_Azienda  = attributi.containsButton("INSERISCI_AZIENDA");
    boolean canInsert_Unita    = attributi.containsButton("INSERISCI_UNITA");
    boolean canViewCalcoloOrganico = attributi.containsButton("CALCOLA-ORGANICO");   
    
    boolean canDelete=true;

    if ( !canModify && !canInsert_Azienda && !canInsert_Unita && !canDelete ) {
      
    } else {
      boolean canEdit = false;
      if ( !flag_insert && !prgUnita.equals("")) {
        canEdit = filter.canEditUnitaAzienda();
      } else { 
        canEdit = true;   // sono in inserimento, la visibilità dei dati non ha senso
        canModify = canInsert_Azienda;
      }
      
      if ( !canEdit ) {
        canInsert_Azienda = false;
        canInsert_Unita = false;
        canModify = false;
        canDelete = false;
      }
    }
     
    
  //Visualizzo il menu dell'azienda se provengo da unitaazienda  
  String showMenu = StringUtils.getAttributeStrNotNull(serviceRequest ,"showMenu" );
  
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>
<%@page import="it.eng.afExt.utils.DateUtils"%>
<html>

<head>
  <title>Testata Azienda</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
    <af:linkScript path="../../js/"/>
    
<SCRIPT TYPE="text/javascript">

var flagChanged = false;  
function fieldChanged() {
  if (<%=canModify%>) {
    flagChanged = true;
  }
}


aUnitaSel = new Array(100);
numUnitaSel = 0;
prgUnita1=0;
prgUnita2=0;

function CheckExSelezione() {
	<%
	String exSelezione1 = (String)serviceRequest.getAttribute("exSelezione1");
	String exSelezione2 = (String)serviceRequest.getAttribute("exSelezione2");
	if (StringUtils.isFilled(exSelezione1)) { %>
		SelUnita(<%=exSelezione1%>);
	<% }
	if (StringUtils.isFilled(exSelezione2)) { %>
		SelUnita(<%=exSelezione2%>);
	<% } %>

    <% if (!accorpa.equals("")) { %>
		SelUnita(<%=prgUnita%>);
	<% } %>
	return true;
}

function CheckUnitaSel() {
  <% if (!accorpa.equals("")) { %>
    var check= eval("document.forms[0].Seleziona"+<%=prgUnita%>);
    if (check != null) {
      check.checked = true;
      aUnitaSel[0]=<%=prgUnita%>;
      numUnitaSel++;
    }
  <% } %>
  return (true);
}

function SelUnita(prgUnita) {
	var check = eval("document.forms[0].Seleziona"+prgUnita);
	if (check != undefined) {
		check.checked = true;
		aUnitaSel[numUnitaSel++] = prgUnita;
	}
}

function SelUnitaClick(prgUnita) {
  var check = eval("document.forms[0].Seleziona"+prgUnita);
  if (check == undefined)
    return;
  
  if (check.checked) {
	  if(numUnitaSel == 1){
		  var posiz = 0;
		  for(u = 0; u < document.forms[0].elements.length; u++){
			  elem = document.forms[0].elements[u];
			  if(elem.type == "checkbox" && elem.checked){
			      aUnitaSel[posiz] = elem.name.substring("Seleziona".length);
			      posiz++;
			  }
			  if(posiz == 2){
				  break;
			  }
		  }
	  }else{
	      aUnitaSel[numUnitaSel] = prgUnita;
	  }
      numUnitaSel++;
  }
  else {
    var i=0;
    while (i<numUnitaSel && aUnitaSel[i]!=prgUnita) {
      i++;
    }
    numUnitaSel--;
    if (numUnitaSel > 0)
      aUnitaSel[i] = aUnitaSel[numUnitaSel];
  }
}

function botAccorpa() {
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;

  if (numUnitaSel == 2){
    prgUnita1 = aUnitaSel[0];
    prgUnita2 = aUnitaSel[1];
    var url = "AdapterHTTP?PAGE=AccorpamentoUnitaPage" +
			    "&CDNFUNZIONE=<%=_funzione%>" +
			    "&PRGUNITA1="+prgUnita1 +
			    "&PRGUNITA2="+prgUnita2 +
			    "&PRGAZIENDA=<%=prgAzienda%>";
    setWindowLocation(url);
  }
  else {
    alert ("Selezionare due unità");
    return (false);
  }
}

function disableAccorpa() {
	var form = document.forms[0];

	for (var e=0; e < form.length; e++) {   // Per tutti gli elementi della form
		var elem = form.elements[e];
		if (elem.type) {
			if ((elem.type == "checkbox") && (elem.name.indexOf("Seleziona") == 0)) {
				elem.disabled = true;
			}
		}
	}
}


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


function unificaPatInail() {
  // GG 1/12/2004, NO: document.getElementById("STRPATINAIL").value = document.getElementById("STRPATINAIL1").value + document.getElementById("STRPATINAIL2").value;
  document.Frm1.STRPATINAIL.value = document.Frm1.STRPATINAIL1.value + document.Frm1.STRPATINAIL2.value;
  return true;
}


function go(url, alertFlag) {
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;

  var _url = "AdapterHTTP?" + url;
  if (alertFlag == 'TRUE' ) {
    if (confirm('Confermi operazione')) {
      setWindowLocation(_url);
    }
  }
  else {
    setWindowLocation(_url);
  }
}

function goBackLista() {
  	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

	<%
	String token = "_TOKEN_" + "IdoListaTestateAziendePage";
	String urlDiLista = (String) sessionContainer.getAttribute(token.toUpperCase());
	if (urlDiLista != null) {
		%>
		setWindowLocation("AdapterHTTP?<%= urlDiLista %>");
		<%
	}
	%>
}		


function selectATECO_onClick(codAteco, codAtecoHid, strAteco, strTipoAteco) {	
  if (codAteco.value==""){
    strAteco.value="";
    strTipoAteco.value=""      
  }
  else { 
    if (codAteco.value!=codAtecoHid.value) {
      window.open("AdapterHTTP?PAGE=RicercaAtecoPage&codAteco="+codAteco.value, "Attività", 'toolbar=0, scrollbars=1'); 
    }
  }
}

function ricercaAvanzataAteco() {
  window.open("AdapterHTTP?PAGE=RicercaAtecoAvanzataPage", "Attività", 'toolbar=0, scrollbars=1');
}

function isDateParam(dateStr) {
	var datePat = /^(\d{1,2})(\/|-)(\d{1,2})(\/|-)(\d{4})$/;
    var matchArray = dateStr.match(datePat); // is the format ok?

    if (matchArray == null) {
      return false;
    }

    month = matchArray[3]; // p@rse date into variables
    day = matchArray[1];
    year = matchArray[5];
   	
    if (month < 1 || month > 12) { // check month range
      return false;
    }

    if (day < 1 || day > 31) {
      return false;
    }

    if ((month==4 || month==6 || month==9 || month==11) && day==31) {
      return false;
    }

    if (month == 2) { // check for february 29th
      var isleap = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
      if (day > 29 || (day==29 && !isleap)) {
        return false;
      }
    }
    return true; // date is valid
}

function checkDataCalcoloOrganico() {
	var objDataCalcolo=eval("document.forms[0].datCalcoloOrganico");
    var strDataCalcolo=objDataCalcolo.value;
    var strDataAggiornamento="<%= datAggInformazione%>";
    var dataOdierna = "<%= DateUtils.getNow() %>";
    var ok=true;
    if (strDataCalcolo == "" || strDataAggiornamento == "") {
    	alert("Inserire Data calcolo organico e data di aggiornamento");
    	ok=false;
    }
    else {
    	var dCalcologiorno=parseInt(strDataCalcolo.substr(0,2),10);
	    var dCalcolomese=parseInt(strDataCalcolo.substr(3,2),10)-1;
	    var dCalcoloanno=parseInt(strDataCalcolo.substr(6,4),10);
	    var dataCalcolo=new Date(dCalcoloanno, dCalcolomese, dCalcologiorno);
    	if (!checkFormatDate(document.forms[0].datCalcoloOrganico)) {
    		alert('Data non corretta nel campo ' + document.forms[0].datCalcoloOrganico.title);
    		ok=false;
    	}
		if ( (ok) && (!isDateParam(strDataCalcolo)) ) {
			alert("Attenzione! Data calcolo organico non valida. Formato deve essere gg/mm/yyyy.");
			ok=false;
		}
    	if (ok) {
		    var dOggigiorno=parseInt(dataOdierna.substr(0,2),10);
		    var dOggimese=parseInt(dataOdierna.substr(3,2),10)-1;
		    var dOggianno=parseInt(dataOdierna.substr(6,4),10);
		    var dataOggi=new Date(dOggianno, dOggimese, dOggigiorno);
		    if (dataCalcolo > dataOggi) {
		    	alert("La data calcolo organico deve essere minore o uguale alla data odierna");
		    	ok=false;	
		    }
    	}
    	if (ok) {
    		var dAgggiorno=parseInt(strDataAggiornamento.substr(0,2),10);
      		var dAggmese=parseInt(strDataAggiornamento.substr(3, 2),10)-1;
			var dAgganno=parseInt(strDataAggiornamento.substr(6,4),10);
		    var dataAgg=new Date(dAgganno, dAggmese, dAgggiorno);
		    if (dataAgg >= dataCalcolo) {
		    	alert("La data calcolo organico deve essere successiva alla data di aggiornamento");
		    	ok=false;
    		}
    	}
    }
    if (ok) {
    	var f = "AdapterHTTP?PAGE=CalcoloOrganicoPage&cdnFunzione=<%=_funzione%>&prgAzienda=<%=prgAzienda%>&dataAggiornamento=<%=datAggInformazione%>&numDipendenti=<%=numDipendenti%>&dataCalcolo=" + strDataCalcolo;
		var t = "CalcoloOrganico";
		var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=500,height=200,top=250,left=300";
		window.open(f, t, feat);
    }   
}

function checkDataInzioFine(inputName) {
  var objDataInizio=  eval("document.forms[0].datInizio");
  var objDataFine=  eval("document.forms[0].datFine");
  strDataInizio=objDataInizio.value;
  strDataFine=objDataFine.value;
  ok=true;

  if (strDataInizio != "" && strDataFine != "") {
      //costruisco la data di inizio
      dIgiorno=parseInt(strDataInizio.substr(0,2),10);
      dImese=parseInt(strDataInizio.substr(3, 2),10)-1; //il conteggio dei mesi parte da zero :P
      dIanno=parseInt(strDataInizio.substr(6,4),10);
      dataInizio=new Date(dIanno, dImese, dIgiorno);

      //costruisce la data di fine
      dFgiorno=parseInt(strDataFine.substr(0,2),10);
      dFmese=parseInt(strDataFine.substr(3,2),10)-1;
      dFanno=parseInt(strDataFine.substr(6,4),10);
      dataFine=new Date(dFanno, dFmese, dFgiorno);
  
      ok=true;
      if (dataFine<dataInizio) {
          alert("La data di inizio attività è successiva alla data di fine attività");
          document.forms[0].datFine.focus();
          ok=false;
      }
  } else if (strDataInizio=="" && strDataFine!="") {
      alert("E' stata specificata la data di fine attività ma non quella di inizio");
      document.forms[0].datInizio.focus();
      ok=false;
  }
  
  return ok;
}

function modificaRagioneSociale(){
  var url = "AdapterHTTP?PAGE=RagSocialeDettaglioPage&cdnFunzione=<%=_funzione%>&prgAzienda=<%=prgAzienda%>&prgUnita=<%=prgUnita%>";
  window.open(url, "RagSociale", 'toolbar=0, scrollbars=1');
}

<%
    // INIT-PARTE-TEMP
	if (Sottosistema.CM.isOff()) {	
	// END-PARTE-TEMP
	
	// INIT-PARTE-TEMP
	} else {
	// END-PARTE-TEMP 
%>

function calcolaOrganico(){
	var f = "AdapterHTTP?PAGE=CalcoloOrganicoPage&cdnFunzione=<%=_funzione%>&prgAzienda=<%=prgAzienda%>";
	if ( document.forms[0].CODPROVORGANICO != null ) {
		f = f + "&CODPROVORGANICO=" + document.forms[0].CODPROVORGANICO.value;
	}
	var t = "CalcoloOrganico";
	var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=500,height=200,top=250,left=300";
	window.open(f, t, feat);
}

<%   
  	// INIT-PARTE-TEMP
	}
	// END-PARTE-TEMP
%>

<%//esigenza: il menu contestuale non deve vedersi mai guardando la testata di una azienda.
  //quindi effettuo la modifica commentando le righe seguenti. (risoluzione bug #324, Alessio Rolfini)

//	 if (prgUnita.equals("")) { *qui c'era la chiusura del java%>
//   Però il controllo che il menu ci sia lo faccio comunque, visto che questa pagina viene chiamata anche
//	 all'interno delle pop-up (Roccetti)
//      if (window.top.menu != undefined){
//  		window.top.menu.location="AdapterHTTP?PAGE=MenuCompletoPage";
//      }  
<%//}
//else
//{
//if (funzioneaggiornamento.equals("") && funzioneaggiornamentounita.equals("")) {
//    if (!flag_insert){
 

//questo era javascript - sopra vi era la chiusura del java
//      if (window.top.menu != undefined){
//      window.top.menu.caricaMenuAzienda(<%//=_funzione,   <%//=prgAzienda, <%//= prgUnita ); //Aggiunto prgUnita
//      }
// <%// }
//}}%>


<%
	if ( showMenu.equalsIgnoreCase("true") ) {
    	if (!prgUnita.equals("")) {
      		out.println( "if (window.top.menu != undefined){" );
        		out.println( " window.top.menu.caricaMenuAzienda("+_funzione+","+prgAzienda+","+prgUnita+"); ");
      		out.println( " } ");
  		}
  	}

%>

function checkPIVA(inputName){
  var pivaObj = eval("document.forms[0]." + inputName);
  pivaObj.value=pivaObj.value.toUpperCase();
  pIVA=pivaObj.value;
  var ok = true;
  var i = document.forms[0].strPartitaIva.value.length;  
  if ((i==11)){
     var regEx = /^[0-9]{11}/;
     if (pIVA.search(regEx)==-1)
     { alert ("La partita IVA deve essere solo numerica");
       ok=false;
     }
  }
  else{
  	if (i>0) {
	    alert ("La Partita IVA deve avere una lunghezza pari a 11 caratteri");
	    ok = false;
    }
  }
  return (ok);
}

function checkCodiceFiscale(inputName){
/*
  var ok = true;
  var i = document.forms[0].strCodiceFiscale.value.length;
  if ((i==11) || (i==16)){
    ok = true;
  }
  else{
    alert ("Il codice fiscale deve avere una lunghezza pari a 11 oppure a 16 caratteri");
    ok = false;
  }
  return (ok);
*/
  //Davide 22/09/2004
  var cfObj = eval("document.forms[0]." + inputName);
  cfObj.value=cfObj.value.toUpperCase();
  cf=cfObj.value;
  ok=true;
  msg="";
  	if (cf.length==16 ){
  		if(document.Frm1.FLGDATIOK.value != "S"){
        for (i=0; i<16 && ok; i++)
        {
           c=cf.charAt(i);
            if (i>=0 && i<=5){
                    ok=!isDigit(c);
                    msg="Errore nei primi sei caratteri del codice fiscale:\ndevono essere delle lettere";
            	} else if  (i==6 || i==7) { 
                    ok=isDigit(c);
                    msg="Errore nel settimo o nell'ottavo carattere del codice fiscale:\ndevono essere numeri";
            	} else if (i==8) {
                    ok=!isDigit(c);
                    msg="Errore nel nono carattere del codice fiscale:\ndeve essere una lettera";
            	} else if (i==9 || i==10) {
                    ok=isDigit(c);
                    msg="Erore nel decimo o nell'undicesimo carattere del codice fiscale";
            	} else if (i==11) {
                    ok=!isDigit(c);
                    msg="Errore nell'undicesimo carattere del codice fiscale:\ndeve essere una lettera";
            	} else if (i>=12 && i <=14) {
                    ok=isDigit(c);
                    msg="Errore nel tredicesimo, nel quattordicesimo o nel penultimo carattere del codice fiscale:\ndevono essere dei numeri";
            	} else if (i==15) {
                    ok=!isDigit(c);
                    msg="Errore nell'ultimo carattere del codice fiscale:\ndeve essere una lettera";
            	}
        	}
        }
       else ok=true;
    } 
    else if (cf.length==11) {
    	if (document.Frm1.FLGDATIOK.value != "S"){
          	var regEx = /^[0-9]{11}/;
           	if (cf.search(regEx)==-1) { 
           		msg="Se di 11 cifre il codice fiscale deve essere solo numerico";
              	ok=false;
           	}
    	}
    	else ok=true;
    }
    else {
      	ok=false;
      	msg="Il codice fiscale deve essere di 11 o di 16 caratteri";
    }   
    if (!ok) {
        alert(msg);
        cfObj.focus();
    }
  return ok;
}


function checkAlbo(){
  var ok = true;
  var tipoAz = document.forms[0].codTipoAzienda.value;
  var numAlbo = document.forms[0].strNumAlboInterinali.value;
  if (tipoAz!="INT"){
    ok = true;
  }
  else{
    if ((numAlbo==null) || (numAlbo=='')){
      alert ("Il numero di iscrizione all'albo imprese interinali è obbligatorio");
      ok = false;
    }
    else{
      ok = true;
    }
  }
  return (ok);
}

function onLoad() {
	<% if (! canDoAccorpa) { %>
		disableAccorpa();
	<% } %>
	CheckExSelezione();
	rinfresca();
}

</SCRIPT>
<script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
</script>
</head>

<body class="gestione" onload="onLoad();">

<h2>Testata Azienda</h2>

<p>
  <font color="green"><af:showMessages prefix="M_InsertTestataAzienda" /></font>
  <font color="green"><af:showMessages prefix="M_SaveTestataAzienda" /></font>
  <font color="red"><af:showErrors /></font>
</p>
<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="unificaPatInail()">
<%= htmlStreamTop %>
<table class="main">
    <tr valign="top">
      <td class="etichetta">Codice fiscale</td>
      <td class="campo">
        <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strCodiceFiscale" title="Codice Fiscale" size="20" validateWithFunction="checkCodiceFiscale"  value="<%=strCodiceFiscale%>" readonly="<%= String.valueOf(!canModify) %>" maxlength="16" required="true" validateOnPost="true" />
      </td>
    </tr>

    <tr valign="top">
      <td class="etichetta">Partita IVA </td>
      <td class="campo">
        <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strPartitaIva" title="Partita Iva"  size="13" validateWithFunction="checkPIVA" value="<%=strPartitaIva%>" readonly="<%= String.valueOf(!canModify) %>" maxlength="11" validateOnPost="true" />
        &nbsp;&nbsp
        Validità C.F. / P. IVA&nbsp;
        <af:comboBox 
          name="FLGDATIOK"
          classNameBase="input"
          disabled="<%= String.valueOf(!canModify) %>"
          onChange="fieldChanged()">
          <option value=""  <% if ( "".equalsIgnoreCase(strFlgDatiOk) )  { %>SELECTED="true"<% } %> ></option>
          <option value="S" <% if ( "S".equalsIgnoreCase(strFlgDatiOk) ) { %>SELECTED="true"<% } %> >Sì</option>
          <option value="N" <% if ( "N".equalsIgnoreCase(strFlgDatiOk) ) { %>SELECTED="true"<% } %> >No</option>
        </af:comboBox>
      </td>
    </tr>

     <%-- <tr valign="top">
      <td class="etichetta">Numero REA</td>
      <td class="campo">
        <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strREA" title="Numero REA" size="12" value="<%=numREA%>" readonly="<%= String.valueOf(!canModify) %>" />
      </td>
    </tr> --%>

    <tr valign="top">
      <td class="etichetta">Ragione sociale</td>
      <td class="campo">
        <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strRagioneSociale" title="Ragione Sociale" size="50" value="<%=strRagioneSociale%>" readonly="<%= String.valueOf(!(canModify && flag_insert)) %>" required="true" />         
        <%if (canModify && !flag_insert) {%>
        <input class="pulsante" type="button" name="cambiaRagioneSociale" value="Modifica Ragione Sociale" onclick="modificaRagioneSociale();" />
        <%}%>
      </td>
    </tr>
    
	<%
	//DONA
	//if (canCambioRagSoc) {
	%>
	<tr valign="top">
      <td class="etichetta">Data cambio ragione sociale</td>
      <td class="campo">
        <af:textBox classNameBase="input" name="datCambioRagSoc" title="Data cambio ragione sociale" size="12" value="<%=datCambioRagSoc%>" readonly="true" />                 
      </td>
    </tr>
	<%
	//}
	%>
	
    <tr valign="top">
      <td class="etichetta">Natura azienda</td>
      <td class="campo">
        <af:comboBox classNameBase="input" onChange="fieldChanged();" name="codNatGiuridica" title="Natura Giuridica" selectedValue="<%=codNatGiuridica%>" disabled="<%= String.valueOf(!canModify) %>" moduleName="M_GETTIPINATGIURIDICA" addBlank="true" />
      </td>
    </tr>

    <tr valign="top">
      <td class="etichetta">Tipo di azienda</td>
      <td class="campo">
        <af:comboBox classNameBase="input" onChange="fieldChanged();" name="codTipoAzienda" title="Tipo di Azienda" selectedValue="<%=codTipoAzienda%>" disabled="<%= String.valueOf(!canModify) %>" moduleName="M_GETTIPIAZIENDA" addBlank="true" required="true" />
      </td>
    </tr>

    <tr valign="top">
      <td class="etichetta">Sito internet</td>
      <td class="campo">
        <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strSitoInternet" title="Sito Internet" size="50"  maxlength="100" 
                    value="<%=strSitoInternet%>" readonly="<%= String.valueOf(!canModify) %>" />
      </td>
    </tr>

    <%--<tr>
       <td class="etichetta">Codice Attività</td>
      <td class="campo">
        <af:textBox classNameBase="input" name="codAteco" title="Codice di Attività" size="6" maxlength="5" value="<%=codAteco%>" readonly="<%= String.valueOf(!canModify) %>" required="true" />
        <af:textBox type="hidden" name="codAtecoHid" value="<%=codAteco%>" />
        <% if (canModify) { %>
            <a href="javascript:selectATECO_onClick(Frm1.codAteco, Frm1.codAtecoHid, Frm1.strAteco,  Frm1.strTipoAteco);"><img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;&nbsp;
            <A href="javascript:ricercaAvanzataAteco();">
                Ricerca avanzata
            </A>
        <%}%>
      </td>
    </tr> --%>       
    <%--<tr valign="top">
      <td class="etichetta">Tipo</td>
      <td class="campo">
        <af:textBox classNameBase="input" name="strTipoAteco" value="<%=tipo_ateco%>" readonly="true" size="48" />
      </td>
    </tr>

    <tr>
      <td class="etichetta">Attività</td>
      <td class="campo">
           <af:textBox classNameBase="input" name="strAteco" size="48" readonly="true" value="<%=strDesAteco%>" />
      </td>
    </tr> --%>

    <tr valign="top">
      <td class="etichetta">Descrizione dell'attività</td>
      <td class="campo">
        <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="strDescAttivita" title="Descirizone dell'Attività" cols="50" value="<%=strDescAttivita%>" readonly="<%= String.valueOf(!canModify) %>"  maxlength="100"/>
      </td>
    </tr>

<%
    // INIT-PARTE-TEMP
	if (Sottosistema.CM.isOff()) {	
	// END-PARTE-TEMP
	
	// INIT-PARTE-TEMP
	} else {
	// END-PARTE-TEMP 
%>
		
    <tr>
    <td colspan="2">
        <table class="main" cellspacing=0 cellpadding=0>
          <tr>
            <td>
              <div class="sezione2">
              <img id='tendinaInfoL68' alt='Apri' src='../../img/chiuso.gif' onclick="cambia(this,document.getElementById('TBL0'));" />&nbsp;&nbsp;&nbsp;Informazioni L68
            </td>
         </tr>
        </table>
    </td>
    </tr>
    
  	<tr>
    	<td colspan="2">
    	<table align="center" id='TBL0' style='display:none'>
      		<tr valign="top">
      			<% if (configOrganico.equals("0")) { %>
      				<td colspan="2">
      				<table>
      				<tr>
	      			<td nowrap class="etichetta">Obbligo L.68</td>
	      			<td nowrap class="campo">
	        			<af:comboBox 
	          			name="FLGOBBL68"
	          			classNameBase="input"
	          			disabled="<%= String.valueOf(!canModify) %>"
	          			onChange="fieldChanged()">
	          			<option value=""  <% if ( "".equalsIgnoreCase(strFlgObbligoL68) )  { %>SELECTED="true"<% } %> ></option>
	          			<option value="S" <% if ( "S".equalsIgnoreCase(strFlgObbligoL68) ) { %>SELECTED="true"<% } %> >Sì</option>
	          			<option value="N" <% if ( "N".equalsIgnoreCase(strFlgObbligoL68) ) { %>SELECTED="true"<% } %> >No</option>
	        			</af:comboBox>
	        		</td>
	      			<td nowrap class="etichetta">Provincia</td>	
	      			<td nowrap class="campo">	      			        			
		      			<af:comboBox 
		      			    addBlank="true" blankValue=""
		      			    name="CODPROVORGANICO"
		        			moduleName="CM_GET_AMB_TERR"
		        			classNameBase="input"
		        			disabled="<%= String.valueOf(!canModify) %>" >
		      			</af:comboBox>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;			      	      			
	        			<% if (canViewCalcoloOrganico) { %>	
        				<input class="pulsante" type="button" name="calculateOrganico" value="Calcola Organico" onclick="calcolaOrganico();"/>
      					<% } %>
      				</td>
      				</tr>
      				</table>
      				</td>
	        		<% } else { %>
	        			<td class="etichetta">Obbligo L.68</td>
	      				<td class="campo">
	        			<af:comboBox 
	          			name="FLGOBBL68"
	          			classNameBase="input"
	          			disabled="<%= String.valueOf(!canModify) %>"
	          			onChange="fieldChanged()">
	          			<option value=""  <% if ( "".equalsIgnoreCase(strFlgObbligoL68) )  { %>SELECTED="true"<% } %> ></option>
	          			<option value="S" <% if ( "S".equalsIgnoreCase(strFlgObbligoL68) ) { %>SELECTED="true"<% } %> >Sì</option>
	          			<option value="N" <% if ( "N".equalsIgnoreCase(strFlgObbligoL68) ) { %>SELECTED="true"<% } %> >No</option>
	        			</af:comboBox>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	        			<% if (canViewCalcoloOrganico) { %>	
        				<input class="pulsante" type="button" name="calculateOrganico" value="Calcola Organico" onclick="calcolaOrganico();"/>
      					<% } %>
      					</td>
	        			</tr>
	        			<tr valign="top">
	        			<td class="etichetta">Data calcolo</td>
	        			<td class="campo">
	        			<af:textBox classNameBase="input" type="date" title="Data calcolo organico" 
	        				name="datCalcoloOrganico" size="12" maxlength="10" value=""/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        				<input class="pulsante" type="button" name="calculateOrganico" value="Calcola Organico" onclick="checkDataCalcoloOrganico();"/>
      					</td>
	        		<% } %>
      		</tr>   
    	</table>
    	</td>
  	</tr>    

<%	    
  	// INIT-PARTE-TEMP
	}
	// END-PARTE-TEMP
%>

    <tr>
    <td colspan="2">
        <table class="main" cellspacing=0 cellpadding=0>
          <tr>
            <td>
              <div class="sezione2">
              <img id='tendinaOrganico' alt='Apri' src='../../img/chiuso.gif' onclick="cambia(this,document.getElementById('TBL1'));" />&nbsp;&nbsp;&nbsp;Organico
            </td>
         </tr>
        </table>
    </td>
    </tr>

    <tr>
      <td colspan="2">
      <table class="main" id='TBL1' style='display:none'>
        <tr valign="top">
          <td class="etichetta">Numero di soci</td>
          <td>
          <af:textBox classNameBase="input" type="integer" validateOnPost="true" onKeyUp="fieldChanged();" name="numSoci" title="Numero di Soci" value="<%=numSoci%>" readonly="<%= String.valueOf(!canModify) %>" size="5" maxlength="38" />
          </td>
          <td class="etichetta">Numero di dipendenti</td>
          <td>
            <af:textBox classNameBase="input" type="integer" validateOnPost="true" onKeyUp="fieldChanged();" name="numDipendenti" title="Numero di Dipendenti" size="5" value="<%=numDipendenti%>" readonly="<%= String.valueOf(!canModify) %>" maxlength="38"/>
          </td>
        </tr>
        <tr valign="top">
          <td class="etichetta">Numero di collaboratori</td>
          <td>
            <af:textBox classNameBase="input" type="integer" validateOnPost="true" onKeyUp="fieldChanged();" name="numCollaboratori" title="Numero di collaboratori" size="5" value="<%=numCollaboratori%>" readonly="<%= String.valueOf(!canModify) %>" maxlength="38"/>
          </td>
          <td class="etichetta">Numero di personale in altre posizioni</td>
          <td>
            <af:textBox classNameBase="input" type="integer" validateOnPost="true" onKeyUp="fieldChanged();" name="numAltraPosizione" title="Numero di personale in altre posizioni" size="5" value="<%=numAltraPosizione%>" readonly="<%= String.valueOf(!canModify) %>" maxlength="38"/>
          </td>
        </tr>
        <tr>
          <td class="etichetta">Data di aggiornamento</td>
          <td>
            <af:textBox classNameBase="input" type="date" onKeyUp="fieldChanged();" title="Data di aggiornamento" name="DATAGGINFORMAZIONE" size="11" maxlength="10" value="<%=datAggInformazione%>" readonly="<%= String.valueOf(!canModify) %>" validateOnPost="true" />
          </td>
        </tr>        
    </table>
    </td>
  </tr>


  <tr>
  <td colspan="2">
      <table class="main" cellspacing=0 cellpadding=0>
        <tr>
          <td>
            <div class="sezione2">
            <img id='tendinaAltreInfo' alt='Apri' src='../../img/chiuso.gif' onclick="cambia(this,document.getElementById('TBL2'));" />&nbsp;&nbsp;&nbsp;Altre Informazioni
          </td>
       </tr>
      </table>
  </td>
  </tr>


  <tr>
    <td colspan="2">
    <table class="main" id='TBL2' style='display:none'>
      <tr valign="top">
        <td class="etichetta">PAT INAIL</td>
        <td nowrap>
          <af:textBox classNameBase="input" type="integer" validateOnPost="true" onKeyUp="fieldChanged();" name="STRPATINAIL1" title="Pat INAIL" size="10" maxlength="8" value="<%=patInail1%>" readonly="<%=String.valueOf(!canModify)%>"/> - 
          <af:textBox classNameBase="input" type="integer" validateOnPost="true" onKeyUp="fieldChanged();" name="STRPATINAIL2" title="Pat INAIL" size="3" maxlength="2" value="<%=patInail2%>" readonly="<%=String.valueOf(!canModify)%>"/>
          <input type="hidden" name="STRPATINAIL" value="<%=patInail%>"/>                    
        </td>
        <td></td>   
        <td class="etichetta">Reparto</td>
        <td class="campo">
          <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strRepartoInail" title="Reparto INAIL" size="30" maxlength="100" value="<%=strRepartoInail%>" readonly="<%= String.valueOf(!canModify) %>" />
        </td>
      </tr>
   	</table>
   </td>
 </tr>
  <tr>
  	<td colspan="2">
  	<table class="main" cellspacing=0 cellpadding=0>
     	<tr>
          <td>
            <div class="sezione2">
            <img id='tendinaAltreInfo' alt='Apri' src='../../img/chiuso.gif' onclick="cambia(this,document.getElementById('TBL3'));" />&nbsp;&nbsp;&nbsp;Somministrazione
          </td>
       </tr>
      </table>
  </td>
  </tr>
  <tr valign="top">
    <td colspan="2">
    <table class="main" id='TBL3' style='display:none'>
      <tr> 
      	<td class="etichetta">Numero albo agenzie somministrazione</td>
      	<td align="left">
          <af:textBox classNameBase="input" onKeyUp="fieldChanged();" validateWithFunction="checkAlbo" name="strNumAlboInterinali" title="Numero albo agenzie somministrazione" size="60" maxlength="100" value="<%=strNumAlboInterinali%>" readonly="<%= String.valueOf(!canModify) %>" />
        </td>
        <td></td>
        <td class="etichetta">Numero agenzia somministrazione</td>
        <td class="campo">
          <af:textBox classNameBase="input" onKeyUp="fieldChanged();" name="strNumAgSomministrazione" title="Numero agenzia somministrazione" size="14" maxlength="11" value="<%=strNumAgSomministrazione%>" readonly="<%= String.valueOf(!canModify) %>" />
        </td>
      </tr>    
    </table>
    </td>
  </tr>

  <tr valign="top">
    <td colspan="2">
    <table class="main">
      <tr>
        <td class="etichetta">Data di inizio dell'attività</td>
        <td>
          <af:textBox classNameBase="input" type="date" onKeyUp="fieldChanged();" title="Data di inizio dell'attività" name="datInizio" size="11" maxlength="10" value="<%=datInizio%>" readonly="<%= String.valueOf(!canModify) %>" validateOnPost="true" />
        </td>
        <td class="etichetta">Data di fine dell'attività</td>
        <td>
          <af:textBox classNameBase="input" type="date" onKeyUp="fieldChanged();" title="Data di fine dell'attività" name="datFine" size="11" maxlength="10" value="<%=datFine%>" readonly="<%= String.valueOf(!canModify) %>" validateWithFunction="checkDataInzioFine" validateOnPost="true" />
        </td>
      </tr>
    </table>
    </td>
  </tr>
  <tr valign="top">
    <td class="etichetta">History</td>
    <td class="campo">
       <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="strHistory" cols="70" value="<%=strHistory%>" maxlength="2000" readonly="<%= String.valueOf(!canModify) %>" />
    </td>
  </tr>  
  <tr valign="top">
    <td class="etichetta">Note</td>
    <td class="campo">
       <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="strNote" cols="70" value="<%=strNote%>" readonly="<%= String.valueOf(!canModify) %>" />
    </td>
  </tr>

<input type="hidden" name="prgAzienda" value="<%= prgAzienda %>"/>
<input type="hidden" name="prgUnita" value="<%= prgUnita %>"/>
<input type="hidden" name="numKloAzienda" value="<%=numKloAzienda%>"/>
<input type="hidden" name="cdnFunzione" value="<%= _funzione %>"/>
<tr><td colspan="2">&nbsp;</td></tr>
<tr>
  <td colspan="2" align="center">
  <%if (!flag_insert) operatoreInfo.showHTML(out);%>
  </td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
<tr><td colspan="2" align="center">
<% if (!flag_insert) { %>
    <input type="hidden" name="PAGE" value="IdoTestataAziendaPage"/>
    <%if (canModify) { %>
      <input class="pulsante" type="submit" name="salva" value="Aggiorna"/>      
    <%}%>
   <%// if (canDelete) { 
      if (pagina_back.equals("IdoListaAziendePage")) { %>
<!--        <input class="pulsante" type="button" name="back" value="Torna alla lista"
        		onclick="goBackLista()" />-->
    <%} else {
          if (prgMov.equals("")){
              if (funzioneaggiornamentounita.equals("")) {%> 
                <input class="pulsante" type="button" name="back" value="Torna alla pagina di ricerca"
                	onclick="go('PAGE=IdoAziendaRicercaPage&cdnFunzione=<%=_funzione%>','FALSE')" />
            <%}
          } else {%>
              <input class="pulsante" type="button" name="back" value="Indietro"
              		onclick="javascript:history.back();" />
          <%}%>
    <%}%>
    <%
    	String pagina_lista = null;
	    if (pagina_back.equals("IdoListaAziendePage")) 
    		pagina_lista = "IdoListaTestateAziendePage";
    	else pagina_lista = "IdoListaAziendePage";
    %>
<%@ include file="VisualizzaPulsanteIndietroLista.inc"%>  
     <!-- <input class="pulsante" type="submit" name="cancella" value="cancella il record"> -->
   <%//}%>
<%} else { %>
      <% if (canInsert_Azienda) { %>
          <input class="pulsante" type="submit" name="inserisci" value="Inserisci"/>
      <% }%>
      <input type="hidden" name="PAGE" value="IdoUnitaAziendaPage"/>
      <input type="hidden" name="ret" value="IdoTestataAziendaPage"/>
<%}//end if%>
</td></tr>
</table>
<%= htmlStreamBottom %>
<% if (!flag_insert) { %>
	<a name="unitalocali"></a>
	<p class="titolo">Unit&agrave; locali</p>
    
    <af:list moduleName="M_RicercaUnitaAzienda" configProviderClass="it.eng.sil.module.ido.DynamicRicUnitaAziendaConfig" skipNavigationButton="1" />
<%} //if !flag_insert %>

<%if (!funzioneaggiornamento.equals("")) {%>
  <af:textBox type="hidden" name="AGG_FUNZ" value="<%=funzioneaggiornamento%>"/>
<%}%>


<%if (!flag_insert && canDoAccorpa) {%>
  <center>
   <input type="button" name="Accorpa" class="pulsante" value="Accorpa"
   			onclick="botAccorpa()" />
  </center>
<%}%>
<%@ include file="VisualizzaPulsanteChiusuraPopUp.inc"%>	
</af:form>     
<br>&nbsp;
<!--p align="center">
<%//if (!flag_insert) operatoreInfo.showHTML(out);%>
</p-->
</body>

</html>

<% } %>