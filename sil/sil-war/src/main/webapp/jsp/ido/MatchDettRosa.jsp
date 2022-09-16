<!-- @author: Stefania Orioli -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,
                it.eng.afExt.utils.*, java.math.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.*,
                it.eng.sil.util.*"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
String _page = (String)serviceRequest.getAttribute("PAGE");
String prgRichiestaAz = serviceRequest.getAttribute("PRGRICHIESTAAZ").toString();
String prgAzienda = serviceRequest.getAttribute("PRGAZIENDA").toString();
String prgUnita = serviceRequest.getAttribute("PRGUNITA").toString();
String _cdnFunzione = serviceRequest.getAttribute("CDNFUNZIONE").toString();

String flgincrociomirato = serviceRequest.getAttribute("flgIncMir")  == null ? "0" : (String)serviceRequest.getAttribute("flgIncMir");
 
//necessari per il corretto refresh della pagina dopo l'invio dwegli SMS
String cdnStatoRichOrig = StringUtils.getAttributeStrNotNull(serviceRequest, "CDNSTATORICHORIG");
String cpiRose = StringUtils.getAttributeStrNotNull(serviceRequest, "CPIROSE");

String prgRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGROSA");
String codiceRit = "";
String moduleName = StringUtils.getAttributeStrNotNull(serviceRequest, "MODULE");
// se l'operazione eseguita e' stata la creazione di una nuova rosa nominativa bisogna gestire l'eventuale 
// errore in modo particolare, nascondendo una parte della pagina, in particolare la lista dei candidati. 
// (Andrea 21/02/05)
// 02/05/05 Andrea : tolta la condizione "&& prgRosa.length()=0"
// il prgRosa e' valorizzato se esiste una anteprima
if(moduleName.equalsIgnoreCase("CRNG")) {
	SourceBean creaRosa = (SourceBean) serviceResponse.getAttribute("MCREAROSANOMGREZZA");
	prgRosa = StringUtils.getAttributeStrNotNull(creaRosa, "MATCH_PRGROSA"); 
	codiceRit=StringUtils.getAttributeStrNotNull(creaRosa, "row.codiceRit"); 
}
else codiceRit = "0"; // per tutte le altre operazioni considero il codice di errore 0

// *************gestine di variabili obsoleta**********
//String cdnStatoRichOrig = StringUtils.getAttributeStrNotNull(serviceRequest, "CDNSTATORICHORIG");
//String prgRosa = serviceRequest.getAttribute("PRGROSA").toString();
//String prgIncrocio = serviceRequest.getAttribute("PRGINCROCIO").toString();

SourceBean rowsCandidati = (SourceBean) serviceResponse.getAttribute("MCANDIDATIROSA.ROWS");
int numRecords = 1;
if(rowsCandidati != null)  
	numRecords = Integer.parseInt(rowsCandidati.getAttribute("NUM_RECORDS").toString()); 
// inizializzazione delle variabili usate nella testata
String numRichiesta="", numAnno="",prgTipoRosa="", prgTipoIncrocio="",tipoIncrocio="",prgIncrocio="", tipoRosa="",prgAlternativa="", 
	 strAlternativa="",prgOrig="", prgCopia1="",numKloRosa="",utMod="", ultimaModifica="", numRichiestaOrig="", 
	 utAttivo="" , ordinamento="" ;
String queryStringBack="";	 
String htmlStreamBottom="", htmlStreamTop = "";
	 

if (codiceRit.equals("0"))  {
	SourceBean infoRosa=(SourceBean)serviceResponse.getAttribute("MDETTAGLIOROSA.row");
	numRichiesta = StringUtils.getAttributeStrNotNull(infoRosa, "NUMRICHIESTA");
	numRichiestaOrig = StringUtils.getAttributeStrNotNull(infoRosa, "NUMRICHIESTAORIG");
	numAnno = StringUtils.getAttributeStrNotNull(infoRosa, "NUMANNO");
	prgIncrocio = StringUtils.getAttributeStrNotNull(infoRosa, "PRGINCROCIO");
	tipoIncrocio = StringUtils.getAttributeStrNotNull(infoRosa, "TIPOINCROCIO");
	prgTipoIncrocio = StringUtils.getAttributeStrNotNull(infoRosa, "PRGTIPOINCROCIO");
	tipoRosa = StringUtils.getAttributeStrNotNull(infoRosa, "TIPOROSA");
	prgTipoRosa = StringUtils.getAttributeStrNotNull(infoRosa, "PRGTIPOROSA");
	prgAlternativa = StringUtils.getAttributeStrNotNull(infoRosa, "PRGALTERNATIVA");
	strAlternativa = "";
	if(!prgAlternativa.equals("")) { strAlternativa = "Profilo n. " + prgAlternativa; }
	prgOrig = StringUtils.getAttributeStrNotNull(infoRosa, "PRGRICHIESTAORIG");
	prgCopia1 = StringUtils.getAttributeStrNotNull(infoRosa, "PRGCOPIA1");
	numKloRosa = StringUtils.getAttributeStrNotNull(infoRosa, "NUMKLOROSA");
	utMod = StringUtils.getAttributeStrNotNull(infoRosa, "CDNUTMOD");
	ultimaModifica = StringUtils.getAttributeStrNotNull(infoRosa, "ULTIMAMODIFICA");
	utAttivo = Integer.toString(user.getCodut());
	ordinamento = StringUtils.getAttributeStrNotNull(infoRosa, "NUMORDPROVINCIA");
	flgincrociomirato = StringUtils.getAttributeStrNotNull(infoRosa, "FLGINCROCIOMIRATO").equals("S")?"1":"0";
}
else {// l'operazione e' quella di cnrm e non e' andata a buon fine
    SourceBean infoRich=(SourceBean)serviceResponse.getAttribute("MInfoRichiesta.rows.row");
    
	numRichiesta = (String)serviceResponse.getAttribute("MInfoRichiesta.rows.row.numRichiesta");
	numRichiestaOrig = (String)serviceResponse.getAttribute("MInfoRichiesta.rows.row.numRichiestaOrig");
	numAnno = (String)serviceResponse.getAttribute("MInfoRichiesta.rows.row.numAnno");	
	// impostazione 'poco ortodossa' delle informazioni della testata
	prgTipoIncrocio = "4"; //Autocandidatura
	// andrea 02/05/05 : si deve tornare indietro utilizzando la copia di lavoro (se si hanno i permessi)
	prgCopia1 = (String)serviceRequest.getAttribute("C1");
	// andrea 02/05/05 : problema dovuto all'assenza dei permessi per utilizzare la copia di lavoro
	prgOrig = (String)serviceRequest.getAttribute("prgOrig");
	tipoRosa = "Rosa Grezza";
	tipoIncrocio = "Autocandidatura";
	htmlStreamTop = StyleUtils.roundTopTable(true);
	htmlStreamBottom = StyleUtils.roundBottomTable(true);
}


// Andrea 02/05/05  : PRGRICHIESTAAZ="+prgOrig -> PRGRICHIESTAAZ="+prgRichiestaAz
// il prgRichiestaAz punto al prg a cui e' stata associata la rosa (vedi do_incrocio), per cui quando 
// di torna indietro dalla stampa non si deve riprendere il prgOriginale
String queryString = "cdnFunzione="+_cdnFunzione+"&PAGE="+_page+"&prgRosa="+prgRosa+
	"&prgAzienda="+prgAzienda+"&prgUnita="+prgUnita+"&PRGRICHIESTAAZ="+prgRichiestaAz+"&CPIROSE="+user.getCodRif();


%>

<%
PageAttribs attributiMatch = new PageAttribs(user, "GestIncrocioPage");
boolean gestCopia = attributiMatch.containsButton("GEST_COPIA");
if(!gestCopia) {
  // L'utente non è abilitato alla gestione della copia di lavoro, ma deve utilizzare solo 
  // la richiesta originale (numStorico=0)
  prgCopia1 = prgOrig;
}
PageAttribs attributi = new PageAttribs(user, "MatchDettRosaPage");

boolean ordina= attributi.containsButton("ORDINAMENTO");
boolean stampeRosaDef= attributi.containsButton("STAMPE_ROSA_DEF");
boolean stampaRosaGrezza= attributi.containsButton("STAMPA_ROSA_GREZZA");
boolean filtraRosa= attributi.containsButton("FILTRA_ROSA");
boolean filtraCM= attributi.containsButton("FILTRI_CM");
boolean delMassivaCandidati= attributi.containsButton("DEL_LOGICA_MASSIVA");
boolean aggiungiLavoratore= attributi.containsButton("ADD_LAV");
boolean invioSMS= attributi.containsButton("INVIOSMS");
%>

<%
String token = "";
String urlDiLista = "";
String refListaBtn = "";
if (sessionContainer!=null){
  token = "_TOKEN_" + "IDOLISTARICHIESTEPAGE";
  urlDiLista = (String) sessionContainer.getAttribute(token.toUpperCase());
  if (urlDiLista!=null && !urlDiLista.equals("")) {
    refListaBtn ="<a href=\"AdapterHTTP?" + urlDiLista + "\"><img src=\"../../img/rit_lista.gif\" border=\"0\"></a>";
  } else {
  	urlDiLista = "";
  }
}

String htmlStreamTopInfo = StyleUtils.roundTopTableInfoRetLista(urlDiLista);
String htmlStreamBottomInfo = StyleUtils.roundBottomTableInfoNobr();

//SourceBean contStato = (SourceBean) serviceResponse.getAttribute("MATCHSTATORICHORIG");
SourceBean sbStato = (SourceBean) serviceResponse.getAttribute("MATCHSTATORICHORIG.ROWS.ROW");
String cdnStatoRich = StringUtils.getAttributeStrNotNull(sbStato, "CDNSTATORICH");
String flagCresco = StringUtils.getAttributeStrNotNull(sbStato, "Flgpubbcresco");
String dataInizioRich = StringUtils.getAttributeStrNotNull(sbStato, "dataInizio");
String dataFineRich = StringUtils.getAttributeStrNotNull(sbStato, "dataFine");

String messRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE");
String listPageRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE");

// MESSAGGIO ORDINAMENTO
String msgOrd = "";
if(ordinamento.equals("") || ordinamento.equals("0")) {
	if(prgTipoIncrocio.equals("1")) { msgOrd = "Utente 297"; }
	if(prgTipoIncrocio.equals("2")) { msgOrd = "Prossimit&agrave;, Utente 297"; }
} else {
	if(prgTipoIncrocio.equals("1")) { msgOrd = "Lav. della Provincia, Utente 297"; }
	if(prgTipoIncrocio.equals("2")) { msgOrd = "Lav. della Provincia, Prossimit&agrave;, Utente 297"; }
}
// query di ritorno alla pagina gestione generale in caso di errore CRNG
if (!codiceRit.equals("0")) {
	// andrea 02/05/05 : problema dovuto all'assenza dei permessi per utilizzare la copia di lavoro
	// prgOrig = (String)serviceRequest.getAttribute("PRGORIG");
	queryStringBack = "PAGE=GestIncrocioPage&LIST_PAGE="+listPageRosa+"&PRGAZIENDA="+prgAzienda+
		"&PRGORIG="+prgOrig+"&PRGRICHIESTAAZ="+prgCopia1+"&PRGUNITA="+prgUnita+"&cdnFunzione="+_cdnFunzione;	
}

//gestione degli errori nell'invio degli SMS
String error = StringUtils.getAttributeStrNotNull(serviceRequest, "ERROR");

%>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <title>Dettaglio Rosa</title>
  <%@ include file="../../jsp/documenti/_apriGestioneDoc.inc"%>
  <af:linkScript path="../../js/" />
  <script language="Javascript" src="../../js/utili.js" type="text/javascript"></script>
  <script language="Javascript">
   var opened;
    
    

    function frm_sub(n)
  {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var pag = "";
    var mod = "";
    var ord = "";
    var ok = false;
    switch(n) {
      case 1 :
        pag = "MatchListaRosePage";
        mod = "";
        ord = "";
        document.frm.PRGRICHIESTAAZ.value = "<%=prgCopia1%>";
        ok = true;
        break;
      case 2 :
        pag = "MatchDettRosaPage";
        mod = "SRD";
        ord = "";
        document.frm.PRGRICHIESTAAZ.value = "<%=prgRichiestaAz%>";
        ok = true;
        break;
      case 3 :
        pag = "GestIncrocioPage";
        mod = "";
        ord = "";
        document.frm.PRGRICHIESTAAZ.value = "<%=prgCopia1%>";
        ok = true;
        break;
      case 4 :
        pag = "FiltriPerRosaIDOPage";
        mod = "";
        ord = "";
        document.frm.PRGRICHIESTAAZ.value = "<%=prgRichiestaAz%>";
        ok = true;
        break;
      case 5 :
      	// Esegue l'ordinamento per comp. amm. della rosa
        pag = "MatchDettRosaPage";
        mod = "ORD";
        ord = "1";
        document.frm.PRGRICHIESTAAZ.value = "<%=prgRichiestaAz%>";
        ok = true;
        break;
      case 6 :
      	// Toglie l'ordinamento per comp. amm. della rosa
        pag = "MatchDettRosaPage";
        mod = "ORD";
        ord = "0";
        document.frm.PRGRICHIESTAAZ.value = "<%=prgRichiestaAz%>";
        ok = true;
        break;
      case 7:
      	// Esclusione Massiva
      	pag = "EscludiDaRosaPage";
      	mod = "";
      	setVPrgNominativo();
      	document.frm.MASSIVA.value="1";
      	ok = true;
      	break;
      case 8:
      	// aggiunta lavoratore alla rosa nominativa
      	pag = "<%=_page%>";
      	mod="RNG_AGG_LAV";
      	ord="0";
      	if (document.frm.LIST_PAGE.value!='')
	      	document.frm.LIST_PAGE.disabled=false;
	    else 
	    	document.frm.LIST_PAGE.disabled=true;
      	ok = true;
      	break;
      case 9 :
        pag = "FiltriCMPerRosaIDOPage";
        mod = "";
        ord = "";
        document.frm.PRGRICHIESTAAZ.value = "<%=prgRichiestaAz%>";
        ok = true;
        break;
      /*case 9:
      	// Invio SMS
      	pag = "InvioSMSDaRosaGrezzaPage";
      	mod = "M_IDO_SMS_INVIO";
      	setVPrgNominativo();
      	document.frm.MASSIVA.value="1";
      	ok = true;
      	break;
      */
      	
      default :
        pag = "";
        mod = "";
        document.frm.PRGRICHIESTAAZ.value = "<%=prgRichiestaAz%>";
        ok = false;
        break;
      }
    if(ok) {      
      document.frm.PAGE.value = pag; 
      document.frm.MODULE.value = mod;
      document.frm.NUMORDPROVINCIA.value = ord;
      doFormSubmit(document.frm);
    } else {
      avviso();
    }
  }
	  
  
  function stampe_rosa()
  {
    var url = "AdapterHTTP?PAGE=StampeRosaCandidatiIDOPage"
    url += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
    url += "&PRGORIG=<%=prgOrig%>";
    url += "&PRGROSA=<%=prgRosa%>";
    url += "&PRGAZIENDA=<%=prgAzienda%>";
    url += "&PRGUNITA=<%=prgUnita%>";
    url += "&CDNFUNZIONE=<%=_cdnFunzione%>";
    url += "&CPIROSE=<%=user.getCodRif()%>";
    window.top.main.location=url;
  }
  
  
  function openPar_Pesato_PopUP() {
     window.open ("AdapterHTTP?PAGE=MatchViewParPesatoPage&PRGINCROCIO=<%=prgIncrocio%>", "Parametri", 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES,left=150,top=100');
  }
  
  function openPar_Esatto_PopUP() {
     window.open ("AdapterHTTP?PAGE=MatchViewParEsattoPage&PRGINCROCIO=<%=prgIncrocio%>", "Parametri", 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES,left=150,top=100');
  }
  
  function openRich_PopUP(prgRich) {
     //window.open ("AdapterHTTP?PAGE=IdoDettaglioSinteticoPage&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&CDNFUNZIONE=<%=_cdnFunzione%>&POPUP=1", "DettaglioSintetico", 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES,left=150,top=100');
     window.open ("AdapterHTTP?PAGE=IdoDettaglioSinteticoPage&PRGRICHIESTAAZ=" +prgRich +"&CDNFUNZIONE=<%=_cdnFunzione%>&POPUP=1", "DettaglioSintetico", 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES');
  }
  
  function selDeselCand()
  {
  	var coll = document.getElementsByName("SD_Candidati");
  	var b = coll[0].checked;
  	//alert(b);
  	var i;
  	coll= document.getElementsByName("CK_ESCL");
  	for(i=0; i<coll.length; i++) {
  		coll[i].checked = b;
  	}
  }
  
  function setVPrgNominativo()
  {
  	var i;
  	var coll = document.getElementsByName("CK_ESCL");
  	var strPrgNominativo = "";
  	for(i=0; i<coll.length; i++) {
  		b = coll[i].checked;
  		if(b) {
  			if(strPrgNominativo.length>0) { strPrgNominativo += ","; }
  			strPrgNominativo += coll[i].value;
  		}
  	}
  	document.frm.V_PRGNOMINATIVO.value = strPrgNominativo;
  	
  	//alert(strPrgNominativo);
  }
  
	function IsElementiSelezionati() {	
	var elementi= document.getElementsByName("CK_ESCL");
	msg = "E' necessario selezionare almeno un nominativo dalla lista per inviare l'SMS"
		for (var i= 0; i < elementi.length; i++) {
			if ( elementi[i].checked ) {
				return  testoSMS();							
			}	
		}		
		alert(msg);
		return (false);
	}
  
  //effettua il refresh della pagina quando chiamata 
	//(Attenzione, con questa chiamata si perdono eventuali modifiche non salvate dall'utente)
	
	function aggiorna() {
		var err = document.frm.ERROR.value; 
		document.location.href = "AdapterHTTP?"
		+ "PAGE=MatchDettRosaPage&CDNSTATORICHORIG=<%=cdnStatoRichOrig%>&CPIROSE=<%=cpiRose%>&PRGROSA=<%=prgRosa%>&CDNFUNZIONE=<%=_cdnFunzione%>&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>&ERROR=" + err;
	}
  

  
  function ricercaLavoratore() {
  	var f = "AdapterHTTP?PAGE=MatchRicercaLavPage&CDNFUNZIONE=<%=_cdnFunzione%>";
  	var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=750,height=650,top=30,left=180";
  	opened = window.open(f, '_blank', feat);
  }
  
  function testoSMS() {
  	  var nom 	= document.frm.V_PRGNOMINATIVO.value
	  var f 	= "AdapterHTTP?PAGE=TestoSMSPage&CDNFUNZIONE=<%=_cdnFunzione%>&PRGRICHIESTAORIG=<%=prgOrig%>&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&NUMRICHIESTA=<%=numRichiesta%>&NUMRICHIESTAORIG=<%=numRichiestaOrig%>&NUMANNO=<%=numAnno%>&PRGTIPOROSA=<%=prgTipoRosa%>&CPIROSE=<%=user.getCodRif()%>&PRGROSA=<%=prgRosa%>&V_PRGNOMINATIVO=" + nom;
	  var feat 	= "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=750,height=300,top=30,left=180";
	  opened 	= window.open(f, '_blank', feat);
  }
  
  function aggiungiLavoratore(cdnLavoratore, prgSpiContatto, prgTipoContatto){
  	document.frm.CDNLAVORATORE.value = cdnLavoratore;
  	document.frm.PRGTIPOCONTATTO.value = prgTipoContatto;
  	document.frm.PRGSPICONTATTO.value = prgSpiContatto;
  	frm_sub(8);
  	opened.close();
  }
  
  function controllaCM(){
  	
  	<%  
  	    String strStampa = StringUtils.getAttributeStrNotNull(serviceRequest, "STAMPA");
  		if(strStampa != null && !strStampa.equals("")){
  		String diagnosi = "";
		SourceBean rowsCM = (SourceBean) serviceResponse.getAttribute("M_GetDiagnosiCM.ROWS.ROW"); 
			if (rowsCM != null && !rowsCM.equals("")) {
				diagnosi = ((BigDecimal)rowsCM.getAttribute("PRGDIAGNOSI")).toString();
			} 
			if (diagnosi.equals("") ) {%>
  	   			alert("Non esiste la diagnosi funzionale per il lavoratore selezionato!");
 			 <%} else { 
 			 	String parametri = "&prgDiagnosiFunzionale="+diagnosi+"&strChiaveTabella="+diagnosi;
 			 	if ((queryString==null) || (queryString.length() == 0))
					queryString = (String)requestContainer.getAttribute("HTTP_REQUEST_QUERY_STRING");
 				%> 
					var HTTPrequest = "<%=getQueryString(queryString)%>";  	
      				var urlDoc = "AdapterHTTP?";
  					urlDoc += "PAGE=REPORTFRAMEPAGE";
  					urlDoc += "&ACTION_REDIRECT=RPT_DIAGNOSI_FUNZ";
  					urlDoc += "&apri=true";
  					urlDoc += "&asAttachment=false";
					urlDoc += "&mostraPerLavoratore=true";
					urlDoc += "&docInOut=I";
					urlDoc += "&tipoDoc=VS03";
					urlDoc += "&tipoFile=PDF";
					urlDoc += "&rptAction=RPT_DIAGNOSI_FUNZ";
					urlDoc += "&showNoteCPI=true";
  					urlDoc += '<%=parametri%>';
  					urlDoc += "&QUERY_STRING="+HTTPrequest;
  					document.location=urlDoc;
      		<%}	
 			} %>	
	}

</script>
  <script language="Javascript">
    window.top.menu.caricaMenuAzienda(<%=_cdnFunzione%>,<%=prgAzienda%>, <%=prgUnita%>);
  </script>
</head>

<body class="gestione" onload="rinfresca(); controllaCM();">
<center>
</center>

<br>
  <%out.print(htmlStreamTopInfo);%>
  <p class="info_lav">
  Identificativo Rosa <b><%=prgRosa%></b> - 
  Tipo di Rosa <b><%=tipoRosa%></b> -  Tipo di Incrocio <b><%=tipoIncrocio%></b><br>
    <!--  Copia Richiesta num <b><%=prgRichiestaAz%></b>--><br>
  <a class="info_lav" href="#" onClick="openRich_PopUP('<%=prgOrig%>')"><img src="../../img/info_soggetto.gif" alt="Dettaglio"/></a>&nbsp;
  Richiesta num. <b><%=numRichiestaOrig%>/<%=numAnno%></b>
  <%if(!prgTipoIncrocio.equals("4")) {%>
   - <a class="info_lav" href="#" onClick="openRich_PopUP('<%=prgRichiestaAz%>')"><img src="../../img/copiarich.gif" alt="Inf. Copia di Lavoro"/></a>&nbsp;Copia utilizzata
   - Alternativa utilizzata <b><%=strAlternativa%></b> - 
  <%}%>
  <%if(prgTipoIncrocio.equals("1")) {%>
    <a href="#" onClick="openPar_Esatto_PopUP()"><img src="../../img/match_par.gif"></a>&nbsp;Parametri utilizzati
  <%} else {%>
    <%if(prgTipoIncrocio.equals("2")) {%>
    <a href="#" onClick="openPar_Pesato_PopUP()"><img src="../../img/match_par.gif"></a>&nbsp;Parametri utilizzati
    <%}%>
  <%}%>
  <%if(!utAttivo.equals(utMod)){%>
    <br>Ultima Modifica <b><%=ultimaModifica%></b>
  <%}%>
  <br/>
  <%if (codiceRit.equals("0")&& ordina) {%>
  	Ordinamento:&nbsp;<b>
  	<%if(ordinamento.equals("") || ordinamento.equals("0")) {%>
  		<%=msgOrd%> <input type="button" class="pulsanti" onClick="frm_sub(5);" value="Ordina per Lav. Provincia"/>
  	<%} else {%>
  		<%=msgOrd%> <input type="button" class="pulsanti" onClick="frm_sub(6);" value="Elimina Ordinamento"/>
  	<%}%>
  	</b>
  	<%}%>
  </p>
  <%out.print(htmlStreamBottomInfo);%>
<%if(codiceRit.equals("0")) {%>

		<af:showErrors />
		

<!-- Se è una rosa grezza e la richiesta non è chiusa ... -->
<%if(
		(!prgTipoRosa.equals("3") && numRecords>0 && (!cdnStatoRich.equals("4") && !cdnStatoRich.equals("5")))
		|| (!prgTipoRosa.equals("3") && prgTipoIncrocio.equals("4") && (!cdnStatoRich.equals("5")))
	
	) {%>
		<br>
		<table width="96%" align="center" border="0" cellspacing="0" cellpadding="0">
		<tr valign="middle">
			<td class="azzurro_bianco" style="border: #cce4ff;">
			<input type="checkbox" name="SD_Candidati" onClick="selDeselCand();" <%=(invioSMS || delMassivaCandidati)?"":"disabled"%>/>&nbsp;Seleziona/Deseleziona tutti
			&nbsp;&nbsp;
			<%if (delMassivaCandidati) {%>
			<button type="button" onClick="frm_sub(7)" class="ListButtonChangePage">
			<img src="../../img/del.gif" alt="">&nbsp;Escludi selezionati
			</button>		
			<%} else {%>
				&nbsp;
			<%}%>
			</td>
			<td align="center" class="azzurro_bianco" style="border: #cce4ff;; text-align:center">
			<%if (aggiungiLavoratore && prgTipoIncrocio.equals("4") && !prgTipoRosa.equals("3") && !cdnStatoRich.equals("5")) {%>		
				&nbsp;<input type="button" value="Aggiungi lavoratore" class="pulsanti" onclick="ricercaLavoratore()">
			<%}else {%>
			&nbsp;
			<%}%>			
			</td>
		</tr>
		</table>
<%}%>

<!-- gestione invio SMS per la Rosa Grezza -->
		<%if(invioSMS && prgTipoRosa.equals("2") && numRecords>0 && !prgTipoIncrocio.equals("4") && !cdnStatoRich.equals("4") && !cdnStatoRich.equals("5")) {
		%>
		<table width="96%" align="center" border="0" cellspacing="0" cellpadding="0">
			<tr>				
				<td class="azzurro_bianco" style="border: #cce4ff;">			
					<button type="button" onClick="setVPrgNominativo();IsElementiSelezionati()" class="ListButtonChangePage">
						<img src="../../img/cellulare.gif" alt="">&nbsp;Invia SMS				
					</button>								
				</td>
			</tr>
		</table>	
		<%} else {%>
			&nbsp;
		<%}%>
		<br>
		<table width="96%" align="center" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
					<%if (error != null && error.equals("160004")) {%>
						Operazione fallita: L'utente non ha un operatore associato, è impossibile proseguire l'invio del SMS	
					<%} else if (error != null && error.equals("160007")) {%>
						Operazione fallita: non è stato possibile contattare il server di posta
					<%} else {%>
						<b></b>
					<%}%>					
				</td>
			</tr>									
		</table>				

<af:list moduleName="MCANDIDATIROSA" configProviderClass="it.eng.sil.module.ido.MatchCandidatiRosaConfig"/>

<af:form name="frm" action="AdapterHTTP" method="GET" dontValidate="true">
<input name="PAGE" type="hidden" value=""/>
<input name="MODULE" type="hidden" value=""/>
<input name="PRGRICHIESTAAZ" type="hidden" value="<%=prgRichiestaAz%>"/>
<input name="PRGALTERNATIVA" type="hidden" value="<%=prgAlternativa%>"/>
<input name="PRGORIG" type="hidden" value="<%=prgOrig%>"/>
<input name="PRGAZIENDA" type="hidden" value="<%=prgAzienda%>"/>
<input name="PRGUNITA" type="hidden" value="<%=prgUnita%>"/>
<input name="PRGROSA" type="hidden" value="<%=prgRosa%>"/>
<input name="NUMKLOROSA" type="hidden" value="<%=numKloRosa%>"/>
<input name="CERCA" type="hidden" value="cerca"/>
<input name="CDNFUNZIONE" type="hidden" value="<%=_cdnFunzione%>"/>
<input name="DAMATCH" type="hidden" value="1"/>
<input name="CPIROSE" type="hidden" value="<%=user.getCodRif()%>"/>
<input name="NUMORDPROVINCIA" type="hidden" value=""/>
<input name="C1" type="hidden" value="<%=prgCopia1%>"/>
<%-- parametri necessari per l'aggiunta di un lavoratore alla rosa nominativa --%>
<input name="CDNLAVORATORE" type="hidden" value="">
<input name="AGGIUNGI_LAV" type="hidden" value="false">
<input name="PRGSPICONTATTO" type="hidden" value="">
<input name="PRGTIPOCONTATTO" type="hidden" value="">
<input name="CDNSTATORICHORIG" type="hidden" value="<%=cdnStatoRich%>">
<%-- --%>
<input name="V_PRGNOMINATIVO" type="hidden" value=""/>
<input name="MASSIVA" type="hidden" value=""/>
<input name="PRGTIPOROSA" type="hidden" value="<%=prgTipoRosa%>"/>
<input name="ERROR" type="hidden" value=""/>

<%if(!messRosa.equals("")) {%>
	<input type="hidden" name="MESSAGE_ROSA" value="<%=messRosa%>"/>
	<%if(!listPageRosa.equals("")) {%>
		<input type="hidden" name="LIST_PAGE_ROSA" value="<%=listPageRosa%>"/>
	<%}%>
<%}%>
<input name="LIST_PAGE" type="hidden" value="<%=listPageRosa%>" disabled />

<table class="main" align="center">
  <%if (aggiungiLavoratore && prgTipoIncrocio.equals("4") && !prgTipoRosa.equals("3") && !cdnStatoRich.equals("5")) {%>
  <tr><td>
  <input type="button" value="Aggiungi lavoratore" class="pulsanti" onclick="ricercaLavoratore()">
  </td></tr>
  <%}%>

<tr>
  <td align="center">
  	      &nbsp;&nbsp;
  <input type="button" name="elencoRose" class="pulsanti" value="Elenco Rose" onClick="frm_sub(1)"/>
  <!-- Se è una rosa grezza e la richiesta non è chiusa ... -->
  <%
	// cdnStatoRichiesta: de_stato_ev_rich = ins|elab.|elab. match|CHIUSURA|CHIUSURA TOTALE|sospensione
	// prgTipoRosa: de_tipo_rosa = anteprima|grezza|definitiva
	// prgTipoIncrocio: de_tipo_incrocio = esatto|pesato|incrementale|autocandidatura
	if ((!cdnStatoRich.equals("5")&&!prgTipoRosa.equals("3")) && // se definitiva o chiusa totale
	    (
		  (!cdnStatoRich.equals("4") && !prgTipoIncrocio.equals("4")) || // !chiusura e !autocand.
		  (!cdnStatoRich.equals("5") && prgTipoIncrocio.equals("4")) // !chiusura totale e autocand.
		)
	 )
	{%>
	
	   <%if(numRecords>0) {%>
    &nbsp;&nbsp;
    <input type="button" name="definitivo" class="pulsanti" value="Salva Rosa Definitiva" onClick="frm_sub(2)"/>
       <%}%>
       <%if (stampaRosaGrezza){%>
    &nbsp;&nbsp;
    <input name="stampa_rosa" type="button" class="pulsanti" value="Stampa rosa" onclick="apriGestioneDoc('RPT_STAMPA_ROSA','&CPIROSE=<%=user.getCodRif()%>&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>&PRGORIG=<%=prgOrig%>&PRGROSA=<%=prgRosa%>','RGR')" />
    	<%}%>
  <%}%>
  <!-- Se la richiesta non è chiusa -->
  <%if(!cdnStatoRich.equals("4") && !cdnStatoRich.equals("5")) {%>
	  &nbsp;&nbsp;
	  <input type="button" name="nuovoMatch" class="pulsanti" value="Nuovo Matching" onClick="frm_sub(3)"/>
  <%}%>
  <!-- Se è una rosa definitiva -->
  <%if(prgTipoRosa.equals("3") && stampeRosaDef) {%>
    &nbsp;&nbsp;
    <input type="button" name="stampe" class="pulsanti" value="Stampe" onClick="stampe_rosa()"/>
  <%}%>
  <!-- Se posso filtrare la rosa e la richiesta non è chiusa ... -->
  <%if(filtraRosa && !prgTipoRosa.equals("3") && numRecords>0 && (!cdnStatoRich.equals("4") && !cdnStatoRich.equals("5"))&& !prgTipoIncrocio.equals("4")) {%>
    &nbsp;&nbsp;
    <input type="button" name="filtri" class="pulsanti" value="Filtra Rosa" onClick="frm_sub(4)"/>
  <%}%>
<%
// INIT-PARTE-TEMP
if (Sottosistema.CM.isOff()) {	
// END-PARTE-TEMP
%>  
<%	
// INIT-PARTE-TEMP
} else {
// END-PARTE-TEMP
%>
  <!-- Se posso filtrare la rosa CM e la richiesta non è chiusa ... -->
  <%if(filtraCM && ("1").equalsIgnoreCase(flgincrociomirato) && !prgTipoRosa.equals("3") && numRecords>0 && (!cdnStatoRich.equals("4") && !cdnStatoRich.equals("5"))&& !prgTipoIncrocio.equals("4")) {%>
    &nbsp;&nbsp;    
    <input type="button" name="filtri" class="pulsanti" value="Filtra CM" onClick="frm_sub(9)"/>
  <%}%>
<%
// INIT-PARTE-TEMP
}
// END-PARTE-TEMP
 %>    
  </td>
</tr>
</table>
<br>&nbsp;
</af:form>
<table width="100%">
<%if("S".equals(flagCresco)){ %>
	<tr>
	  <td style="campo2">N.B.:</td>
	  <td style="campo2">
	  	Nella lista viene indicato se il lavoratore &egrave; stato Disoccupato(Disocc.), se ha avuto un contratto di lavoro intermittente(Interm.), se ha aderito al programma Garanzia Giovani e/o Pacchetto Adulti.
	  </td>
	</tr>
	<tr>
	  <td style="campo2">&nbsp;</td>
	  <td style="campo2">
	  	I requisiti sono stati verificati nell&apos;intervallo temporale che va dalla data richiesta alla data scadenza pubblicazione: <b><%=dataInizioRich%> - <%=dataFineRich%></b>.
	  </td>
	</tr>	
<%} %>
</table>
<%} else { // gestione errore in fase di CREAZIONIONE ROSA NOMINATIVA GREZZA %>
<br/>
<H2>Rosa Grezza dei Candidati</H2>
<%
	out.print(htmlStreamTop);
%>
	<p class="info_lav"><b>Si e' verificato un errore</b></p>	
	<af:showErrors />
<%
	out.print(htmlStreamBottom);
%>
	<center><input type="button" value="Nuovo Matching" class="pulsanti" onclick="goTo('<%= queryStringBack%>')"></center>
<%}%>
</body>
</html>
