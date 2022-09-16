<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.StringUtils,
                  java.util.*,
                  java.text.*,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.security.ProfileDataFilter,
                  java.math.* " %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%//	@author rolfini
//	due parole, che è meglio:
//
//	questa pagina è il publisher per tre tipologie d'uso (tra parentesi, rispettivamente:
// 	il flag proveniente dalla response/request ed il flag booleano che permettono di
//	riconoscere la tipologia - ove applicabile):
//	 1) inserimento (flag_insert -> isInsert)
//	 2) consultazione/modifica
//	 3) consultazione storico (storico -> isStorico)
//
// Inoltre, abbiamo 2 possibili contesti, che possono presentarsi in combinazione:
// a) inserimento (flag_insert -> isInsert)
// b) contestualizzazione (se è già stato selezionato il lavoratore dal menu principale: contestuale -> isContestuale)
//
// nb: si contestualizza *SEMPRE*
//
// ricapitoliamo:
// flag_insert -> isInsert : indica che siamo in un contesto di inserimento
// contestuale -> isContestuale : indica che abbiamo selezionato un lavoratore dal menu; siamo in un ambiente contestualizzato
//								  (ed infatti compare la testata lavoratore)
// storico -> isStorico : indica che stiamo consultando uno storico, e quindi tutto deve essere READ ONLY
//
// altra cosa: PAGE_LISTA
// visto che la cancellazione avviene sulla lista e abbiamo DUE liste che, selezionando il dettaglio, 
// richiamano questa jsp (lista contestuale e lista non contestuale), 
// abbiamo bisogno di sapere quale lista ha chiamato il dettaglio, in modo
// da impostare la cancellazione su quella lista.
// Introduciamo quindi il parametro PAGE_LISTA, impostato come parametro sul modulo di lista di ognuna
// delle due liste (lista contestuale e lista non contestuale).
// Cosi' sappiamo sempre da quale lista siamo arrivati e a quale lista dobbiamo tornare in caso
// di cancellazione. Chiaro, vero? Eh? Eh? Eh?
//
// per ulteriori informazioni contattatemi (Rolfini)


  //flag che indica stiamo consultando o abbiamo richiesto un inserimento
 boolean isInsert=serviceRequest.containsAttribute("flag_insert");
 
  //flag che indica se siamo in ambito contestuale o menu generale
  boolean isContestuale= (serviceRequest.containsAttribute("contestuale") && serviceRequest.getAttribute("contestuale").equals("true"));


 //flag che indica se dobbiamo visualizzare dati storici
 boolean isStorico = (serviceRequest.containsAttribute("storico") && serviceRequest.getAttribute("storico").equals("true"));

 //flag che indica se il lavoratore selezionato *ha* uno storico
  boolean hasStorico = serviceResponse.containsAttribute("M_AMMHASDICHSOSPSTORICO.ROWS.ROW");	
	String queryString = null;
	// 
	boolean protocollato = false;
	String prgDichSospensione="";
	String strCodiceFiscaleLav="";
	String strCognomeLav="";
	String strNomeLav="";
	String cdnLavoratore="";
	String codCpiComp="";
	String descCpiComp="";
	
 	String strCodiceFiscaleAz="";
 	String strPartitaIvaAz="";
 	String strRagSocAz="";
 	String strIndirizzoAz="";
 	String prgAzienda="";
 	String prgUnita="";

 	String datDichiarazione="";
 	String datFine="";
 
 	String numKloDichSospensione="";
 	
	String cdnUtins="";
	String dtmIns="";
	String cdnUtmod="";
	String dtmMod="";
 
 
 	protocollato = serviceResponse.getAttributeAsVector("M_GETDOCPROTOCOLLATO.rows.row").size()>0;
    SourceBean dichSospRow= (SourceBean) serviceResponse.getAttribute("M_AmmGetDichSosp.ROWS.ROW");

    if (dichSospRow==null) {  //se abbiamo avuto un problema di inserimento
    	isInsert=true; 		  //ritorniamo al layout dell'inserimento.    	
    	//preleviamo i parametri dalla request
    }
 
  
 String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
 String pageLista=StringUtils.getAttributeStrNotNull(serviceRequest, "PAGE_LISTA");
 
 

 //recuperiamo i dati dalla response
  if (isInsert && isContestuale) { // preleviamo solo i dati del lavoratore se siamo in insert contestuale
    SourceBean datiLav= (SourceBean) serviceResponse.getAttribute("M_AmmGetDatiLavoratore.ROWS.ROW");
 	strCodiceFiscaleLav=StringUtils.getAttributeStrNotNull(datiLav, "strCodiceFiscale");
	strCognomeLav=StringUtils.getAttributeStrNotNull(datiLav, "strCognome");
	strNomeLav=StringUtils.getAttributeStrNotNull(datiLav, "strNome");
	codCpiComp=StringUtils.getAttributeStrNotNull(datiLav, "cpiCompetente");
	cdnLavoratore=datiLav.containsAttribute("cdnLavoratore") ? datiLav.getAttribute("cdnLavoratore").toString() : "";	
 
  }

 if (!isInsert && (dichSospRow!=null)) { //se siamo in sola visualizzazione/storico, recuperiamo tutto
 	prgDichSospensione=dichSospRow.containsAttribute("prgDichSospensione")? dichSospRow.getAttribute("prgDichSospensione").toString() :"";
  
    strCodiceFiscaleLav=StringUtils.getAttributeStrNotNull(dichSospRow, "strCodiceFiscaleLav");
	strCognomeLav=StringUtils.getAttributeStrNotNull(dichSospRow, "strCognomeLav");
	strNomeLav=StringUtils.getAttributeStrNotNull(dichSospRow, "strNomeLav");
	cdnLavoratore=dichSospRow.containsAttribute("cdnLavoratore") ? dichSospRow.getAttribute("cdnLavoratore").toString() : "";
	codCpiComp=StringUtils.getAttributeStrNotNull(dichSospRow, "cpiCompetente");
	descCpiComp=StringUtils.getAttributeStrNotNull(dichSospRow, "cpiCompetenteDesc");
	
  
	strCodiceFiscaleAz=StringUtils.getAttributeStrNotNull(dichSospRow, "strCodiceFiscaleAz");
	strPartitaIvaAz=StringUtils.getAttributeStrNotNull(dichSospRow, "strPartitaIvaAz");
	strRagSocAz=StringUtils.getAttributeStrNotNull(dichSospRow, "strRagSocAz");
	strIndirizzoAz=StringUtils.getAttributeStrNotNull(dichSospRow, "strIndirizzoAz");
	prgAzienda=dichSospRow.containsAttribute("prgAzienda") ? dichSospRow.getAttribute("prgAzienda").toString() : "";
	prgUnita=dichSospRow.containsAttribute("prgUnita") ? dichSospRow.getAttribute("prgUnita").toString() : "";

	datDichiarazione=StringUtils.getAttributeStrNotNull(dichSospRow, "datDichiarazione");
	datFine=StringUtils.getAttributeStrNotNull(dichSospRow, "datFine");

	numKloDichSospensione = dichSospRow.containsAttribute("NUMKLODICHSOSPENSIONE")? dichSospRow.getAttribute("NUMKLODICHSOSPENSIONE").toString() :"";
	

  cdnUtins= dichSospRow.containsAttribute("cdnUtins") ? dichSospRow.getAttribute("cdnUtins").toString() : "";
  dtmIns=dichSospRow.containsAttribute("dtmins") ? dichSospRow.getAttribute("dtmins").toString() : "";
  cdnUtmod=dichSospRow.containsAttribute("cdnUtmod") ? dichSospRow.getAttribute("cdnUtmod").toString() : "";
  dtmMod=dichSospRow.containsAttribute("dtmmod") ? dichSospRow.getAttribute("dtmmod").toString() : "";			
 }
 
 
  Testata operatoreInfo = null;
  operatoreInfo = new Testata(cdnUtins, dtmIns, cdnUtmod, dtmMod);

  
  //se sono in contestuale, prelevo anche la testata del lavoratore
  InfCorrentiLav infCorrentiLav= null;
  if (isContestuale) { 
      infCorrentiLav= new InfCorrentiLav(sessionContainer, cdnLavoratore, user);
  }
 
 
	 //mi prendo la data di oggi (verrà usata nei controlli javascript)
     DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
     Date currentDate = new Date();
     String strDataCorrente = formatter.format(currentDate);
  
  	String cpiUtente=(String) user.getCodRif();
  	
  	
  	//FILTRI VISUALIZZAZIONE e pulsanti  	
	String _page = (String) serviceRequest.getAttribute("PAGE"); 
	PageAttribs attributi = new PageAttribs(user, _page);
	boolean readOnlyStr= false;
  	
	if (!isInsert) {		
		ProfileDataFilter filter = new ProfileDataFilter(user, _page);
		filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
		boolean canView=filter.canViewLavoratore();
		boolean canInsert = true;
		if (! canView){
			response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		}else{
		      readOnlyStr = !attributi.containsButton("AGGIORNA");
		      canInsert   =  attributi.containsButton("INSERISCI");
		      if((!canInsert) && (readOnlyStr)){
		    		canInsert=false;
	    		    readOnlyStr=true;
	      	  }else{
	          boolean canEdit=filter.canEditLavoratore();
    	      if (canInsert){
        	    canInsert=canEdit;
	          }
    	      if (!readOnlyStr){
        	   readOnlyStr=!canInsert;
        	 }        
    	}
  	}
  }

  	
 queryString="contestuale="+isContestuale+"&storico="+isStorico+"&PAGE_LISTA="+pageLista+
	"&cdnLavoratore="+cdnLavoratore+"&page="+_page+"&cdnFunzione="+_funzione+"&prgDichSospensione="+prgDichSospensione;
	if (isInsert)
		queryString+="&flag_insert=true";
  	
  	
  	
  	
  	

%>

<%
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>
	<head>
		<title><%=(isInsert)?"Inserimento di una nuova richiesta di sospensione":"Dettaglio richiesta di sospensione"%></title>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/" />
	 <script type="text/javascript" src="../../js/CommonXMLHTTPRequest.js" language="JavaScript"></script>
     <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
      <!--
      
      // NOTE: Rilevazione Modifiche da parte dell'utente
  var flagChanged = false;
  
  function fieldChanged() {
    
    // NOTE: field-check solo se posso scrivere
    <% if ( !readOnlyStr ) { %> 
      flagChanged = true;
    <% } %> 
  }
      
      
      function checkCorrettezza()
      { 
        //prelevo gli oggetti
        var dataDichObj=eval("document.forms[0].datDichiarazione");
        var dataFineValObj=eval("document.forms[0].datFine");
        
        var cdnLavObj= eval("document.forms[0].cdnLavoratore");
        var cpiCompObj=eval("document.forms[0].codCpiComp");
        
        dataDichstr=dataDichObj.value;
        dataFinestr=dataFineValObj.value;
        dataCorrentestr="<%=strDataCorrente%>";

		cdnLavoratore=cdnLavObj.value;
        cpiComp=cpiCompObj.value;

		//controllo se è presente il lavoratore
		if (cdnLavoratore=="") {
			alert("E' necessario selezionare un lavoratore");
			return false;
		}

      	
      	//formattazione delle date  
    	dataDich = new Date(dataDichstr.substr(6,10), parseInt(dataDichstr.substr(3,5), 10) - 1, dataDichstr.substr(0,2));
    	dataCorrente=new Date(dataCorrentestr.substr(6,10), parseInt(dataCorrentestr.substr(3,5), 10) - 1, dataCorrentestr.substr(0,2));


        //controllo se la data dichiarazione è futura
		if ( dataDich > dataCorrente ) {  
			alert("La data di dichiarazione non può essere una data futura");
			dataDichObj.focus();
			return false;
		}

    	
        if (dataFinestr!="") {
  	    	dataFine = new Date(dataFinestr.substr(6,10), parseInt(dataFinestr.substr(3,5), 10) - 1, dataFinestr.substr(0,2));
	        //controllo se la data di fine validità è minore della data di dichiarazione
    	    if (dataFine < dataDich ) {
        		alert("La data di fine sospensione non può essere antecedente alla data di inizio sospensione");
        		dataFineValObj.focus();
	        	return false;
    	    }
		}
		
		
		//controllo se il cpi competente del lavoratore è all'interno dei CPI
		//del polo provinciale al quale appartiene l'utente.
		//Il confronto avviene sui primi 4 caratteri del codice: se essi coincidono,
		//il CPI del lavoratore è all'interno del polo provinciale dell'utente (fonte: Tiziana)
		
		if (cpiComp.substr(0,4) !="<%=cpiUtente.substring(0,4)%>") {
			alert("Il CPI del lavoratore scelto non appartiene a questo polo provinciale.\n Impossibile proseguire.");
			return false;
		}
		

        return true;
      }
      
      
      
      
var imgChiusa = "../../img/chiuso.gif";
var imgAperta = "../../img/aperto.gif";



//-------------------------------------------------------------------------      
      
    function apriCercaLavoratore(funzionediaggiornamento) {
        var f = "AdapterHTTP?PAGE=CommonCercaLavoratorePage&AGG_FUNZ="+funzionediaggiornamento;
        var t = "_blank";
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=750,height=650,top=30,left=180";
        window.open(f, t, feat);
    }

    function apriCercaAzienda(funzionediaggiornamento) {
        var f = "AdapterHTTP?PAGE=CommonCercaUnitaAziendaPage&AGG_FUNZ="+funzionediaggiornamento;
        var t = "_blank";
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=750,height=650,top=30,left=180";
        window.open(f, t, feat);
    }



      function getDatiLavoratore(cdnLavoratore) {
		
		//se siamo qui abbiamo sicuramente selezionato o cambiato il lavoratore
		fieldChanged();
		
      	//Query per recupero dati
		var request = "AdapterHTTP?PAGE=AmmGetLavoratoreDichSospPage&cdnLavoratore=" + cdnLavoratore;
	
		//Controllo sul server
		var result = syncXMLHTTPGETRequest(request);
		if (result == null || result.responseXML.documentElement == null ) {
			alert("Impossibile reperire i dati del lavoratore selezionato \n");
		} else {
			//Prelevo la riga restituita dal modulo di reperimento dei dati
			row=getXMLHTTPRow(result, "M_AMMGETDATILAVORATORE");
			if (row==null) {
				alert("impossibile reperire i dati del lavoratore selezionato \n");
			} else {
	
				//prelevo dalla riga i dati
				document.Frm1.cdnLavoratore.value = row.getAttribute("CDNLAVORATORE");
				document.Frm1.codiceFiscaleLavoratore.value = row.getAttribute("STRCODICEFISCALE");		
				document.Frm1.cognome.value = row.getAttribute("STRCOGNOME");
				document.Frm1.nome.value = row.getAttribute("STRNOME");
				document.Frm1.cpiComp.value=row.getAttribute("CPICOMPETENTEDESC");
				document.Frm1.codCpiComp.value=row.getAttribute("CPICOMPETENTE");
				//visualizzo i dati
				apriSezioneLavoratore();
		
		      	return true;
		    }
      }
	}
	
	function getDatiUnitaAzienda(prgAzienda, prgUnita) {
		
		//se siamo qui abbiamo sicuramente selezionato o cambiato l'azienda
		fieldChanged();
		
		//Query per recupero dati
		var request = "AdapterHTTP?PAGE=AmmGetUnitaAziendaDichSospPage&prgAzienda=" + prgAzienda+"&prgUnita="+prgUnita;
	
		//Controllo sul server
		var result = syncXMLHTTPGETRequest(request);
		if (result == null || result.responseXML.documentElement == null ) {
			alert("Impossibile reperire i dati del lavoratore selezionato \n");
		} else {
			//Prelevo la riga restituita dal modulo di reperimento dei dati
			row=getXMLHTTPRow(result, "M_AMMGETDATIUNITAAZIENDA");
			if (row==null) {
				alert("impossibile reperire i dati dell'unit&agrave; aziendale selezionata \n");
			} else {
	
				//prelevo dalla riga i dati
				
				document.Frm1.prgAzienda.value = prgAzienda;
				document.Frm1.prgUnita.value=prgUnita
				document.Frm1.codiceFiscaleAz.value = row.getAttribute("STRCODICEFISCALE");		
				document.Frm1.partitaIvaAz.value = row.getAttribute("STRPARTITAIVA");
				document.Frm1.ragSocAz.value = row.getAttribute("STRRAGIONESOCIALE");
				document.Frm1.indirizzoAz.value = row.getAttribute("STRINDIRIZZO");
				
				//visualizzo i dati
				apriSezioneUnitaAzienda();
		
		      	return true;
		    }
      }
	}
	
	
	
	function apriSezioneLavoratore(){
		document.Frm1.tendinaLav.src=imgAperta;
	    var divDatiLav = document.getElementById("datiLav");
	    divDatiLav.style.display="";	
	}
	function chiudiSezioneLavoratore(){
		document.Frm1.tendinaLav.src=imgChiusa;
	    var divDatiLav = document.getElementById("datiLav");
	    divDatiLav.style.display="none";	
	}
	function apriSezioneUnitaAzienda(){
		document.Frm1.tendinaAz.src=imgAperta;
	    var divDatiAz = document.getElementById("datiAz");
	    divDatiAz.style.display="";	
	}
	function chiudiSezioneUnitaAzienda(){
		document.Frm1.tendinaAz.src=imgChiusa;
	    var divDatiAz = document.getElementById("datiAz");
	    divDatiAz.style.display="none";	
	}
	

	
	function azzeraLavoratore() {
		document.Frm1.cdnLavoratore.value="";
		<%if (!isContestuale){%>
    	document.Frm1.codiceFiscaleLavoratore.value="";
    	document.Frm1.cognome.value="";
    	document.Frm1.nome.value="";
    	chiudiSezioneLavoratore();  
    	<%}%>  	
	}

	function azzeraAzienda() {
		document.Frm1.prgAzienda.value="";
		document.Frm1.prgUnita.value="";		
    	document.Frm1.codiceFiscaleAz.value="";
    	document.Frm1.partitaIvaAz.value="";
    	document.Frm1.ragSocAz.value="";
    	document.Frm1.indirizzoAz.value="";    	
    	chiudiSezioneUnitaAzienda();    	
	}


	function annulla(){
		azzeraLavoratore();
		azzeraAzienda();
		
		document.Frm1.datDichiarazione.value="";
		document.Frm1.datFine.value="";
		
	
	}


function cancellaDichSosp(){
		var cpiCompObj=eval("document.forms[0].codCpiComp");
		cpiComp = cpiCompObj.value;
		if (cpiComp.substr(0,4) !="<%=cpiUtente.substring(0,4)%>") {
			alert("Il CPI del lavoratore scelto non appartiene a questo polo provinciale.\n Impossibile proseguire.");
			return false;
		}
		if (confirm("Sei proprio sicuro di cancellare?")) {
			// Se la pagina è già in submit, ignoro questo nuovo invio!
		    if (isInSubmit()) return;
		    var urlDiLista="<%	    
			if (sessionContainer!=null){
	 			String token= "_TOKEN_";
			 	token += pageLista;				 				 
	    		 String urlDiLista = (String) sessionContainer.getAttribute(token.toUpperCase());
	    		 if (urlDiLista!=null){	     
			     	//cancello il parametro di cancellazione dalla stringa di ritorno
			     	//in modo che non possa più essere ripetuta.
			     	int occorrenzaCancella=urlDiLista.indexOf("PRGDICHSOSPENSIONECANC");
					if (occorrenzaCancella!=-1) {
						int occorrenzaFineCancella=urlDiLista.indexOf('&', occorrenzaCancella);
						String urlDilistacomodo=urlDiLista.substring(0, occorrenzaCancella);
						String urlDilistacomodo2=urlDiLista.substring(occorrenzaFineCancella);
						urlDiLista=urlDilistacomodo + urlDilistacomodo2;
					}
	    		 out.print(urlDiLista);
	    		}
			  } 		    
		    %>";		    
			var f = "AdapterHTTP?" + urlDiLista + "&prgDichSospensioneCanc=<%=prgDichSospensione%>";
	        setWindowLocation(f);
		}
}

function apriInfoStoriche() {
     var f = "AdapterHTTP?PAGE=AmmListaStoricoDichSospPage&cdnLavoratore=<%=cdnLavoratore%>"
     var t = "_blank";
     var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=750,height=650,top=30,left=180";
     window.open(f, t, feat);

}
function stampa() {
	var cpiCompObj=eval("document.forms[0].codCpiComp");
	cpiComp = cpiCompObj.value;
	if (cpiComp.substr(0,4) !="<%=cpiUtente.substring(0,4)%>") {
		alert("Il CPI del lavoratore scelto non appartiene a questo polo provinciale.\n Impossibile proseguire.");
		return false;
	}
	apriGestioneDoc('RPT_STAMPA_DICH_SOSPENSIONE',
		'&cdnLavoratore=<%=cdnLavoratore%>&pagina=<%=_page%>&strChiaveTabella=<%=prgDichSospensione%>',
		'SOSP','','true');
	
}
 <%if (!cdnLavoratore.equals("") && !isStorico) {%>
        window.top.menu.caricaMenuLav( <%=_funzione%>,   <%=cdnLavoratore%>);
      <%}%>


      -->



    </script>
<script language="Javascript" src="../../js/docAssocia.js"></script>
<%@ include file="../documenti/_apriGestioneDoc.inc"%>    
    
    
	</head>
	<body class="gestione" onload="rinfresca()">
<% if (isContestuale) { 
    infCorrentiLav.show(out); 
 }
%>

<font color="green">
  <af:showMessages prefix="M_AmmUpdateDichSosp"/>
  <af:showMessages prefix="M_AmmInsertDichSosp"/>
</font>
<font color="red">
     <af:showErrors/>
</font>
	
  <p class="titolo"><%=(isInsert)?"Inserimento dichiarazione di sospensione per contrazione d'attività":"Dettaglio dichiarazione di sospensione per contrazione d'attività"%></p>
    <%out.print(htmlStreamTop);%>

  <af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="checkCorrettezza()">
      <table class="main">        
  	<%if (!isContestuale) {%>
        <tr><td colspan="2"/>&nbsp;</td></tr>
	
		<tr class="note">		
		    <td colspan="2">
		    <div class="sezione2">
             <img id='tendinaLav' alt='Chiudi' src='<%=((isInsert && (!isContestuale)) ?"../../img/chiuso.gif":"../../img/aperto.gif")%>' onclick="" />&nbsp;&nbsp;&nbsp;Lavoratore
		      &nbsp;&nbsp;
		      <% if (isInsert &&(!isContestuale) && !readOnlyStr) { %>
		        <a href="#" onClick="javascript:apriCercaLavoratore('getDatiLavoratore');"><img src="../../img/binocolo.gif" alt="Cerca"></a>
		        		        &nbsp;<a href="#" onClick="javascript:azzeraLavoratore();"><img src="../../img/del.gif" alt="Azzera selezione"></a>
		      <%}%>
		   </div>
		    </td>
		</tr>
		<tr>
			<td colspan="2">
		<div id="datiLav" style="<%=((isInsert && (!isContestuale)) ?"display:none":"")%>"> 
			<table class="main" width="100%" border="0">
				<tr>				
					<td class="etichetta">Codice Fiscale</td>
					<td class="campo" valign="bottom"><af:textBox classNameBase="input"
						type="text" name="codiceFiscaleLavoratore" readonly="true"
						value="<%=strCodiceFiscaleLav%>" size="30" maxlength="16" />
						
						<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>"/>
  					    <input type="hidden" name="prgDichSospensione" value="<%=prgDichSospensione%>"/>
						<% if (!isInsert) { %>
							<input type="hidden" name="numKloDichSospensione" value="<%=((int) Integer.parseInt(numKloDichSospensione) + 1)%>"/>
						<% } else { %>
							<input type="hidden" name="numKloDichSospensione" value=""/>
						<% } %>
					</td>
				</tr>
				<tr>
					<td class="etichetta">Cognome</td>
					<td class="campo"><af:textBox classNameBase="input" type="text"
						name="cognome" readonly="true" value="<%=strCognomeLav%>"
						size="30" maxlength="50" /></td>
				</tr>
				<tr>
					<td class="etichetta">Nome</td>
					<td class="campo"><af:textBox classNameBase="input" type="text"
						name="nome" readonly="true" value="<%=strNomeLav%>" size="30"
						maxlength="50" /></td>
				</tr>
				<tr>
					<td class="etichetta">CPI Competente</td>
					<td class="campo">
						<af:textBox classNameBase="input" type="text"
						name="cpiComp" readonly="true" value="<%=descCpiComp%>" size="30"
						maxlength="50" />
						<input type="hidden" name="codCpiComp" value="<%=codCpiComp%>"/>						
					</td>
						
				</tr>
				
      		</table>
    	</div>
  		</td>
		</tr>
<% } //if (!isContestuale) 	
	else { %>

		<tr>
					<td colspan="2"> 
						<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>"/>
  					    <input type="hidden" name="prgDichSospensione" value="<%=prgDichSospensione%>"/>
						<input type="hidden" name="codCpiComp" value="<%=codCpiComp%>"/>	
						<% if (!isInsert) { %>
							<input type="hidden" name="numKloDichSospensione" value="<%=((int) Integer.parseInt(numKloDichSospensione) + 1)%>"/>
						<% } else { %>
							<input type="hidden" name="numKloDichSospensione" value=""/>
						<% } %>	
					</td>
		</tr>
	
	
<%} %>
			
		        <tr><td colspan="2"/>&nbsp;</td></tr>
		
		
		<tr class="note">		
		    <td colspan="2">
		    <div class="sezione2">
             <img id='tendinaAz' alt='Chiudi' src='<%=((isInsert||prgAzienda.equals(""))?"../../img/chiuso.gif":"../../img/aperto.gif")%>' onclick="" />&nbsp;&nbsp;&nbsp;Azienda
		      &nbsp;&nbsp;
		      <% if (!isStorico && !readOnlyStr) { %>
		        <a href="#" onClick="javascript:apriCercaAzienda('getDatiUnitaAzienda');"><img src="../../img/binocolo.gif" alt="Cerca"></a>
		        &nbsp;<a href="#" onClick="fieldChanged();azzeraAzienda();"><img src="../../img/del.gif" alt="Azzera selezione"></a>
		      <% } %>
		   </div>
		    </td>
		</tr>
		<tr>
			<td colspan="2">
		<div id="datiAz" style="<%=( (isInsert||prgAzienda.equals(""))?"display:none":"")%>"> 
			<table class="main" width="100%" border="0">
				<tr>
					<td class="etichetta">Codice Fiscale</td>
					<td class="campo" valign="bottom"><af:textBox classNameBase="input"
						type="text" name="codiceFiscaleAz" readonly="true"
						value="<%=strCodiceFiscaleAz%>" size="30" maxlength="16" />
						
						<input type="hidden" name="prgAzienda" value="<%=prgAzienda%>"/>
						<input type="hidden" name="prgUnita" value="<%=prgUnita%>"/>						
					</td>
				</tr>
				<tr>
					<td class="etichetta">Partita Iva</td>
					<td class="campo"><af:textBox classNameBase="input" type="text"
						name="partitaIvaAz" readonly="true" value="<%=strPartitaIvaAz%>"
						size="30" maxlength="50" /></td>
				</tr>
				<tr>
					<td class="etichetta">Ragione Sociale</td>
					<td class="campo"><af:textBox classNameBase="input" type="text"
						name="ragSocAz" readonly="true" value="<%=strRagSocAz%>" size="70"
						maxlength="50" /></td>
				</tr>
				<tr>
					<td class="etichetta">Indirizzo</td>
					<td class="campo"><af:textBox classNameBase="input" type="text"
						name="indirizzoAz" readonly="true" value="<%=strIndirizzoAz%>" size="70"
						maxlength="50" /></td>
				</tr>				
      		</table>
    	</div>
  		</td>
		</tr>

        <tr><td colspan="2"/>&nbsp;</td></tr>
        <tr><td colspan="2"/>&nbsp;</td></tr>

				<tr>
					<td class="etichetta">Data inizio sospensione</td>
					<td class="campo"><af:textBox classNameBase="input" title="Data inizio sospensione" type="date" name="datDichiarazione"	
										value="<%=datDichiarazione%>" size="12" maxlength="10" readonly="<%= String.valueOf( ((readOnlyStr) || (isStorico)) )  %>" required="true" 
										onKeyUp="fieldChanged();" validateOnPost="true"/>
					</td>
				</tr>
				<tr>
					<td class="etichetta">Data fine sospensione</td>
					<td class="campo"><af:textBox classNameBase="input" title="Data fine sospensione" type="date" name="datFine" value="<%=datFine%>"
						size="12" maxlength="10" readonly="<%= String.valueOf( ((readOnlyStr) || (isStorico)) )%>" onKeyUp="fieldChanged();" validateOnPost="true" /></td>
				</tr>

				<tr>
					<td colspan="2">&nbsp;</td>
				</tr>
				<tr>
					<td colspan="2">&nbsp;</td>
				</tr>
		</table>
	
		<table width="100%">
				<tr>
					<td colspan="3" align="center">						
				<% if (!isStorico) { %>
						<%if (isInsert) { %> 
						<input class="pulsanti" type="submit" name="Inserisci" value="Inserisci" />&nbsp;&nbsp; <input type="button"
							class="pulsanti" value="Annulla" onclick="annulla()"/>
						<% } else if (!readOnlyStr) {%> 
							<input class="pulsanti" type="submit" name="Salva" value="Aggiorna" /> 
						<%}
					} else { %>
							<input class="pulsanti" type="button" name="Chiudi" value="Chiudi" onclick="window.close();" /> 
				 <% } %>							
					</td>
				</tr>

				<tr>
				  <!--td colspan="3">&nbsp;</td-->
<!--				  <td width="33%" align="center">				  -->
					<td colspan="3" align="center">
				<% if (!isInsert && !isStorico) { %>
					<input class="pulsante<%=((hasStorico)?"":"Disabled")%>" type="button" name="InfoStoriche" 
				   value="Informazioni storiche" onclick="apriInfoStoriche();"
				   <%=(!hasStorico)?"disabled=\"True\"":""%>/>
				   
				   <input class="pulsante" type="button" name="Documenti associati" value="Documenti associati"
	    	         onClick="docAssociati('<%=cdnLavoratore%>','<%=_page%>','<%=_funzione%>','','')">
	    	          <%if (!protocollato) {%>
	    	       <input class="pulsante" type="button"  value="Stampa" 
  					onclick="stampa()">
					  <%}%>
				 <% } else { %>
					     &nbsp;
				<% } %>				
					</td>
				</tr>  
				<tr>
				  <td colspan="3" align="center">
				  
				<% if (!isInsert && !isStorico && !readOnlyStr&!protocollato) { %>
					<input class="pulsanti" type="button" name="cancella" value="Cancella la dichiarazione" onclick="cancellaDichSosp()"/>
				 <% } else { %>
					     &nbsp;
				<% } %>				
					</td>
				</tr>  
				<tr>
					<td colspan="3">&nbsp;
						<input type="hidden" name="PAGE" value="AmmDettDichSospPage" />
						<input type="hidden" name="cdnFunzione" value="<%=_funzione%>" />
				        <input type="hidden" name="contestuale" value="true"/>
	       		        <input type="hidden" name="PAGE_LISTA" value="<%=pageLista%>"/>  
				     </td>
				</tr>
			</table>

			</af:form>
      
<%
//torno alla lista	 

	 if (sessionContainer!=null){
	 		String token= "_TOKEN_";
		if (pageLista!=null && !isStorico) { 
		 	token += pageLista;
		 } else if (isStorico) { //storico!
			token += "AmmListaStoricoDichSospPage";		 	 
		 }
		 
	     String urlDiLista = (String) sessionContainer.getAttribute(token.toUpperCase());
	     if (urlDiLista!=null){
	     
	     	//cancello il parametro di cancellazione dalla stringa di ritorno
	     	//in modo che non possa più essere ripetuta.
	     	int occorrenzaCancella=urlDiLista.indexOf("PRGDICHSOSPENSIONECANC");
			if (occorrenzaCancella!=-1) {
				int occorrenzaFineCancella=urlDiLista.indexOf('&', occorrenzaCancella);
				String urlDilistacomodo=urlDiLista.substring(0, occorrenzaCancella);
				String urlDilistacomodo2=urlDiLista.substring(occorrenzaFineCancella);
				urlDiLista=urlDilistacomodo + urlDilistacomodo2;
			}
	     	out.println("<a href=\"javascript:goTo(\'" + urlDiLista
	                      + "\');\"><img src=\"../../img/rit_lista.gif\" border=\"0\"></a>");
	         }
	  } 
%>


	<%if (!isInsert) {
		operatoreInfo.showHTML(out); 
	  } %>
<%out.print(htmlStreamBottom);%>
	</body>
</html>

