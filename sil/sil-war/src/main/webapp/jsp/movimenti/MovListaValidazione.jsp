<!-- @author: Paolo Roccetti - Gennaio 2004 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
                   
                  com.engiweb.framework.util.*,
                  it.eng.sil.module.movimenti.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  java.text.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, (String) serviceRequest.getAttribute("PAGE"));
  boolean canValidaTutti = attributi.containsButton("VALIDATUTTI");
  
  String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
	
  //Ottengo il vettore con tutti i movimenti ricercati
  Vector movimentiFiltrati= serviceResponse.getAttributeAsVector("M_MovGetListaAppoggio.ROWS.ROW");    	
  boolean permettiValidazione = (movimentiFiltrati.size()>=1)? true : false;

  String root_page = StringUtils.getAttributeStrNotNull(serviceRequest,"ROOT_PAGE");
  
  //Variabili per la gestione della protocollazione ================
  String prAutomatica     = null; 
  String estReportDefautl = null;
  BigDecimal numProtV     = null;
  BigDecimal numAnnoProtV = null;
  String     datProtV     = (new SimpleDateFormat("dd/MM/yyyy")).format(new Date());
  String     oraProtV     = (new SimpleDateFormat("HH:mm")).format(new Date());
  String     docInOut     = "";
  String     docRif       = "";
  boolean numProtEditable = false;
  Vector rowsPR           = null;
  SourceBean rowPR        = null;

   rowsPR = serviceResponse.getAttributeAsVector("M_GetProtocollazione.ROWS.ROW");
   if(rowsPR != null && !rowsPR.isEmpty())
   { rowPR = (SourceBean) rowsPR.elementAt(0);
     prAutomatica     = (String) rowPR.getAttribute("FLGPROTOCOLLOAUT");
     if ( prAutomatica.equalsIgnoreCase("N") )
     { numProtEditable = true; 
     }
     else
     { numProtV          = (BigDecimal) rowPR.getAttribute("NUMPROTOCOLLO");
     }
     numAnnoProtV      = new BigDecimal(datProtV.substring(6,10));
     estReportDefautl = (String) rowPR.getAttribute("CODTIPOFILEESTREPORT");
   }
  
  //========================================================================

	String numeroPagine = "";
	String numeroRecords = "";

  //Lo stile della lista
  String stile = "LISTA";

  //Oggetti per l'applicazione dello stile grafico
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
  
  //Creazione del token per il pulsante di ritorno alla lista,
  // devo farlo a manina visto che non uso il tag af:list
  Integer numpage = (Integer) serviceResponse.getAttribute("M_MOVGETLISTAAPPOGGIO.ROWS.CURRENT_PAGE");
  String upperPageName = ((String) serviceRequest.getAttribute("PAGE")).toUpperCase();
  
  String providerURL = "PAGE=" + upperPageName + "&MODULE=M_MOVGETLISTAAPPOGGIO&";
  String strReferente = serviceRequest.containsAttribute("referente")?serviceRequest.getAttribute("referente").toString():"";
  
  StringBuffer queryStringBuffer = new StringBuffer();
  Vector queryParameters = serviceRequest.getContainedAttributes();
  for (int i = 0; i < queryParameters.size(); i++) {
	SourceBeanAttribute parameter = (SourceBeanAttribute) queryParameters.get(i);
	String parameterKey = parameter.getKey();
	if ( parameterKey.equalsIgnoreCase("PAGE")
		|| parameterKey.equalsIgnoreCase("MODULE")
		|| parameterKey.equalsIgnoreCase("MESSAGE")
		|| parameterKey.equalsIgnoreCase("LIST_PAGE")
		|| parameterKey.equalsIgnoreCase("LIST_NOCACHE"))
		continue;
	String parameterValue = parameter.getValue().toString();
	queryStringBuffer.append(JavaScript.escape(parameterKey.toUpperCase()));
	queryStringBuffer.append("=");
	queryStringBuffer.append(JavaScript.escape(parameterValue));
	queryStringBuffer.append("&");
  } 
//  String queryString = queryStringBuffer.toString();
  
  sessionContainer.setAttribute("_TOKEN_" + upperPageName, 
  	providerURL + queryStringBuffer.toString() + "MESSAGE=LIST_PAGE&LIST_PAGE=" + numpage);
  //Fine Creazione del token per il pulsante di ritorno alla lista
  
  String context = StringUtils.getAttributeStrNotNull(serviceRequest,"CONTEXT");
  
%>

<%-- include lo script che permette di aprire la PopUp che gestisce i documenti (salvataggio/protocollazione) --%>
<% String queryString = ""; %>
<%@ include file="../documenti/_apriGestioneDoc.inc"%>
<%@ include file="GestioneValidazioneMassiva.inc" %>
<html>
  <head>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>
    <%if (context.equals("validaArchivio")) {%>
    <title>Lista Movimenti in Archivio</title>
    <%} else {%>
    <title>Lista Movimenti da Validare</title>
    <%}%>
    <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
<!--      
    var numRighe = 15;
    
    function selectPage() {
    	var form = document.FormListaValidazione;
        for(var i = 0; i < form.elements.length ; i ++) {
            if(form.elements[i].name == "ckeckboxMovimenti" && 
               !form.elements[i].checked) {
                form.elements[i].click;
                form.elements[i].checked = true;
            }
        } 
    }
    
    var totalePagine = <%=serviceResponse.getAttribute("M_MOVGETLISTAAPPOGGIO.ROWS.NUM_PAGES")%>;
    var posizioneAttuale = <%=serviceResponse.getAttribute("M_MOVGETLISTAAPPOGGIO.ROWS.CURRENT_PAGE")%>;

    function calcolaPrecedente() {
      if (posizioneAttuale <= 1 ) {return 1;}
      else return posizioneAttuale - 1;
    }

    function calcolaSuccessiva() {
     if (totalePagine==-1)  
	       return posizioneAttuale + 1;
	            
      if (posizioneAttuale >= totalePagine ) {
      	return totalePagine;
      }      
    }
    
    //Effettua la navigazione tra le pagine e la cancellazione dei movimenti (con deleteAction == true e prgMovApp indicato)
    function naviga(listpage, message, deleteAction, prgMovApp) {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

    	go = true;
    	if (deleteAction) {
    		go = confirm('Eliminare il movimento?');
    	}
    	if (go) {
	      document.FormListaNavigazione.LIST_PAGE.value = listpage;
	      document.FormListaNavigazione.MESSAGE.value = message;
	      if (deleteAction) {
	        document.FormListaNavigazione.DELETEMOV.value = "true";
	        document.FormListaNavigazione.PRGMOVAPP.value = prgMovApp;
	
	        //Se cancello l'ultimo movimento nella pagina invece di visualizzare la stessa visualizzo quella precedente,
	        //ma solo se non ho un'unica pagina!
	        if ((numRighe == 1) && (listpage > 1)) {
	          document.FormListaNavigazione.LIST_PAGE.value = listpage - 1;
	        }
	      }
	      doFormSubmit(document.FormListaNavigazione);
	   }
    }
  
  	function confirmValidateExtracted(){
  		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
		
		if(<%=!permettiValidazione%>){
			alert("Non ci sono movimenti da validare");
             	return;
		}
		
  		if(confirm('Vuoi validare i movimenti filtrati?')){
  			if (document.FormListaValidazione.numProt.value == null ||
            		document.FormListaValidazione.numProt.value == "" )
            { 
            	alert("Il numero di protocollo è obbligatorio");
             	return;
            }
           
        	if ( !checkFormatDate(document.FormListaValidazione.dataProt) )
            {
            	alert("La data di protocolazione non è valida");
             	return;
            }
        	else if ( !checkAndFormatTime(document.FormListaValidazione.oraProt) )
          	{ 
          		alert("L'ora di protocolazione non è valida");
          		return;
          	}
			
			document.FormListaValidazione.AZIONE.value="validaFiltrati";
			doFormSubmit(document.FormListaValidazione);
			
  		}
  	}
  
  	function confirmDeleteSelected(listpage) {
  		//Controllo che sia stato selezionato almeno un movimento	
		var form = document.FormListaValidazione;
        var numMovimentiSel=0;
        var strPrgMovApp = '';
        for(var i = 0; i < form.elements.length ; i ++) {
            if(form.elements[i].name == "ckeckboxMovimenti" && form.elements[i].checked) {
            	 if (strPrgMovApp == '') {
            	 	strPrgMovApp = strPrgMovApp + form.elements[i].value;
            	 }
            	 else {
            	 	strPrgMovApp = strPrgMovApp + '#' + form.elements[i].value;
            	 }
            	 numMovimentiSel++;
            }
        } 
	    if (numMovimentiSel==0){ 
	   		alert ("Selezionare almeno un movimento");
	    	return ;
	   	}
	   	if (confirm('Vuoi cancellare i movimenti selezionati?')) {
	   		//Se cancello tutti i movimenti nella pagina invece di visualizzare la stessa 
	   		//visualizzo quella precedente, ma solo se non ho un'unica pagina
	        if ((numMovimentiSel == numRighe) && (listpage > 1)) {
	          document.FormListaNavigazione.LIST_PAGE.value = listpage - 1;
	        }
	   		else {
	   			document.FormListaNavigazione.LIST_PAGE.value = listpage;
	   		}
	   	    document.FormListaNavigazione.DELETEMOV.value = "true";
	        document.FormListaNavigazione.PRGLISTAMOVAPP.value = strPrgMovApp;
	        doFormSubmit(document.FormListaNavigazione);
	   	}
      	
  	}
  
    function confirmValidateSelected() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
		
		//Controllo se la ricerca non ha prodotto alcun risultato  
		if(<%=!permettiValidazione%>){
			alert("Non ci sono movimenti da validare");
             	return;
		}	
		
		//Controllo che sia stato selezionato almeno un movimento	
		var form = document.FormListaValidazione;
        var numMovimentiSel=0;
        for(var i = 0; i < form.elements.length ; i ++) {
            if(form.elements[i].name == "ckeckboxMovimenti" && form.elements[i].checked) {
               	 numMovimentiSel++;
            }
        } 
	    if (numMovimentiSel==0){ 
	    	alert ("Selezionare almeno un movimento");
	    	return ;
	    	}

    	if (confirm('Vuoi validare i movimenti selezionati?')) {
      
        if (document.FormListaValidazione.numProt.value == null ||
            document.FormListaValidazione.numProt.value == "" )
           { alert("Il numero di protocollo è obbligatorio");
             return;
           }
           
        if ( !checkFormatDate(document.FormListaValidazione.dataProt) )
           { alert("La data di protocolazione non è valida");
             return;
           }
        else if ( !checkAndFormatTime(document.FormListaValidazione.oraProt) )
          { return;
          }
          
    		doFormSubmit(document.FormListaValidazione);
        
    	}//if_confirm
      
    }//end_function


// =============================================================================
//     Sono le stesse funzioni js usate in GestisciStatoDoc.jsp
//                                                                by Davide
// =============================================================================
function checkAndFormatTime(oraObj)
{
  var strTime = oraObj.value;
  var strHours = "";
  var strMin   = "";
  var separator = ":";
  var strTimeArray;

  var titleObj = "ora"; 
  if(oraObj.title != null)
  { titleObj = oraObj.title;
  }
  
   if (strTime.indexOf(separator) != -1) {
      strTimeArray = strTime.split(separator);
      if (strTimeArray.length != 2) {
         alert("Il campo "+titleObj+" non ha un orario scritto nel formato corretto:\nscrivere HH"+separator+"MM   es: 08"+separator+"12")
         return false;
      }
      else {
       strHours = strTimeArray[0];
       strMin   = strTimeArray[1];
      }
   }
   else if(strTime.length == 4)
   { //Non c'è il separatore, probabilmente è stata inserita un'orario nel formato 1215 -> 12:15
     //che comunque reputiamo valido...
     strHours = strTime.substr(0,2);
     strMin   = strTime.substr(2,4);
   }
   else
   { alert("Il campo "+titleObj+" non ha un orario scritto nel formato corretto:\nscrivere HH"+separator+"MM   es: 08"+separator+"12");
     return false;
   }

   var hours = parseInt(strHours, 10);
   var min   = parseInt(strMin, 10);
   
   if(isNaN(hours))
   { alert("L'ora inserita nel campo "+titleObj+" non è un numero:\nscrivere HH"+separator+"MM   es: 08"+separator+"12");
     return false;
   }
   if(isNaN(min))
   { alert("I minuti inseriti nel campo "+titleObj+" non sono un numero:\nscrivere HH"+separator+"MM   es: 08"+separator+"12");
     return false;
   }

   hours = parseInt(strHours, 10);
   min   = parseInt(strMin, 10);
   
   if( hours<0 || hours > 23 )
   { alert("L'ora inserita nel campo "+titleObj+" non è orario valido.\nInserire un'ora compresa fra 0 e 23");
     return false;
   }
   if( min<0 || min > 59 )
   { alert("I minuti inseriti nel campo "+titleObj+" non sono corretti.\nInserire un numero compreso fra 0 e 59");
     return false;
   }

	oraObj.value = (hours<10?"0":"") + hours + separator + (min<10?"0":"") + min;
	return true;	  
   
}//end function


function cambiAnnoProt(dataPRObj,annoProtObj)
{
  var dataProt = dataPRObj.value;
  var lun = dataProt.length;

  //Stiamo modificando la data di protocollazione. Quindi cambia anche l'anno di protocollazione
  annoProtObj.value = ""; 
  
  if (lun > 5) {
    var tmpDate = new Object();
    tmpDate.value = dataProt;
    if ( checkFormatDate(tmpDate) ) {
       annoProtObj.value = tmpDate.value.substr(6,10);      
    }
    else if (lun==8 || lun==10) {
      alert("La data di protocollazione non è corretta");
    }
  }
  
}//end function
// =============================================================================

	//funzione di get per il pulsante valida tutti
	function validaTutti() {
		if (confirm('Vuoi validare tutti i movimenti presenti nel sistema?')) {
			var get = "AdapterHTTP?PAGE=MovValidazioneMassivaPage&cdnFunzione=<%=_funzione%>" 
				+ "&AZIONE=validaTutti&numAnnoProt=<%= Utils.notNull(numAnnoProtV) %>"
				+ "&numProt=<%= Utils.notNull(numProtV) %>&dataProt=<%=datProtV%>&oraProt=<%=oraProtV%>"
				+ "&tipoProt=<%=prAutomatica%>";
			setWindowLocation(get);
		}
	}
	
	//funzione di get per il ritorno alla ricerca
	function altraRicerca() {
		var get = "AdapterHTTP?PAGE=MovRicercaValidazionePage&cdnFunzione=<%=_funzione%>"
		setWindowLocation(get);		
	}
	
//funzione di get per l'arresto della validazione massiva
function arrestaValidazione() {
	if (confirm('Vuoi arrestare la validazione massiva corrente?')) {
		var get = "AdapterHTTP?PAGE=MovValidazioneMassivaPage&cdnFunzione=<%=_funzione%>&AZIONE=arresta";
		setWindowLocation(get);
	}
}

//funzione di get per la visualizzazione dei risultati della validazione massiva
function visulizzaRisultati() {
		var get = "AdapterHTTP?PAGE=MovRisultValidazionePage&cdnFunzione=<%=_funzione%>&PAGERISULTVALMASSIVA=FIRST";
		setWindowLocation(get);
}

<%-- Apre la pop-up che visualizza i risultati dell'ultima validazione effettuata per il movimento selezionato --%>
function risultatiValidazioneSingoloMov(prgMovimentoApp) {
   var urlo  = "AdapterHTTP?PAGE=MovMostraRisultatiValidazionePAGE";
       urlo += "&CDNFUNZIONE="+<%=_funzione%>;
       urlo += "&PRGMOVIMENTOAPP="+prgMovimentoApp;
   var w=800; var l=((screen.availWidth)-w)/2;
   var h=330; var t=((screen.availHeight)-h)/2;
   <%-- var feat = "status=YES,location=YES,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l; --%>
   var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
   var opened = window.open(urlo, "RisValidazione", feat);
   opened.focus();
}

// Conta i movimenti selezionati
    function stampaMovimenti() {
    	var form = document.FormListaValidazione;
        var numMovimentiSel=0;
        var stringaMovimenti = '';
        for(var i = 0; i < form.elements.length ; i ++) {
            if(form.elements[i].name == "ckeckboxMovimenti" && 
               form.elements[i].checked) {
               	 stringaMovimenti+="&prgMovimento="+form.elements[i].value;
               	 numMovimentiSel++;
            }
        } 
	    if (numMovimentiSel==0){ 
	    	alert ("Selezionare almeno un movimento");
	    	return false;
	    	}
		else 
			apriGestioneDoc('RPT_MOVIMENTI_DA_VALIDARE','&TIPOSTAMPA=DAVALIDARE&'+stringaMovimenti,'STMOV');
			return true;
    }

-->
    </SCRIPT>
  </head>

  <body class="gestione" onload="rinfresca();">
    <br/>
    <%if (context.equals("validaArchivio")) {%>
    <p class="titolo">Lista Movimenti in Archivio</p>
    <%} else {%>
    <p class="titolo">Lista Movimenti da Validare</p>
    <%}%>
    <br/>
    <center>   
    <%
    	Vector rows = serviceResponse.getAttributeAsVector("M_MOVGETLISTAAPPOGGIO.ROWS.ROW");
		Enumeration _enum = rows.elements();
        //Creo i pulsanti solo se ho almeno un risultato
        if (_enum.hasMoreElements()){
        
  			int currentpage = ((Integer) serviceResponse.getAttribute("M_MOVGETLISTAAPPOGGIO.ROWS.CURRENT_PAGE")).intValue();
  			int numpages = ((Integer) serviceResponse.getAttribute("M_MOVGETLISTAAPPOGGIO.ROWS.NUM_PAGES")).intValue();
  			int numrecords = ((Integer) serviceResponse.getAttribute("M_MOVGETLISTAAPPOGGIO.ROWS.NUM_RECORDS")).intValue();
  			int rowsxpage = ((Integer) serviceResponse.getAttribute("M_MOVGETLISTAAPPOGGIO.ROWS.ROWS_X_PAGE")).intValue();
  			int numeroda = ((currentpage - 1) * rowsxpage) +1;
  			int numeroa = (currentpage == numpages ? numrecords : currentpage * rowsxpage);   
    %>
    <af:form name="FormListaNavigazione" method="GET" action="AdapterHTTP">
      <!--Salvo i parametri per la prossima ricerca sulla navigazione delle pagine-->
      <input type="hidden" name="CODMANSIONE" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"CODMANSIONE")%>"/>
      <input type="hidden" name="CODTIPOMANSIONE" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"CODTIPOMANSIONE")%>"/>
      <input type="hidden" name="CONTEXT" value="<%=context%>"/>
      <input type="hidden" name="DESCMANSIONE" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"DESCMANSIONE")%>"/>
      <input type="hidden" name="LIST_PAGE" value=""/>
      <input type="hidden" name="MESSAGE" value=""/>
      <input type="hidden" name="PAGE" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"PAGE")%>"/>
      <input type="hidden" name="cdnFunzione" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"cdnFunzione")%>"/>
      <input type="hidden" name="cerca" value="<%=serviceRequest.getAttribute("cerca")%>"/>
      <input type="hidden" name="codCCNL" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codCCNL")%>"/>
      <input type="hidden" name="codCCNLHid" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codCCNLHid")%>"/>
      <input type="hidden" name="codFiscaleAzienda" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codFiscaleAzienda")%>"/>
      <input type="hidden" name="codMansioneHid" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codMansioneHid")%>"/>
      <input type="hidden" name="codMonoTempo" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codMonoTempo")%>"/>
      <input type="hidden" name="codiceFiscaleLavoratore" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codiceFiscaleLavoratore")%>"/>
      <input type="hidden" name="cognome" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"cognome")%>"/>
      <input type="hidden" name="datcomunicazionea" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"datcomunicazionea")%>"/>
      <input type="hidden" name="datcomunicazioneda" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"datcomunicazioneda")%>"/>
      <input type="hidden" name="datmovimentoa" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"datmovimentoa")%>"/>
      <input type="hidden" name="datmovimentoda" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"datmovimentoda")%>"/>
      <input type="hidden" name="nome" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"nome")%>"/>
      <input type="hidden" name="normativa" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"normativa")%>"/>
      <input type="hidden" name="pIva" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"pIva")%>"/>
      <input type="hidden" name="ragioneSociale" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"ragioneSociale")%>"/>
      <input type="hidden" name="codTipoAzienda" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codTipoAzienda")%>"/>      
      <input type="hidden" name="strCCNL" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strCCNL")%>"/>
      <input type="hidden" name="strCCNLHid" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strCCNLHid")%>"/>
      <input type="hidden" name="strTipoMansione" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"strTipoMansione")%>"/>
      <input type="hidden" name="codTipoAss" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codTipoAss")%>"/>
      <input type="hidden" name="tipoMovimento" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"tipoMovimento")%>"/>
      <input type="hidden" name="CODCPI" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"CODCPI")%>"/>
      <input type="hidden" name="CODCPILAV" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"CODCPILAV")%>"/>
      <input type="hidden" name="tipoRapporto" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRapporto")%>"/>
      <input type="hidden" name="DELETEMOV" value="false"/>
      <input type="hidden" name="PRGMOVAPP" value=""/>
      <input type="hidden" name="PRGLISTAMOVAPP" value=""/>
      <input type="hidden" name="referente" value="<%= strReferente %>"/>
      <input type="hidden" name="CODCOMUNICAZIONE" value="<%= StringUtils.getAttributeStrNotNull(serviceRequest,"CODCOMUNICAZIONE") %>"/>
      <input type="hidden" name="codTipoComunic" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codTipoComunic")%>"/>
      <%if (context.equals("validaArchivio")) {%>
      <input type="hidden" name="ROOT_PAGE" value="<%=root_page%>">
      <%}%>
         
      <!--pulsanti di navigazione-->
      
    <% 
		if(numpages != -1){
			numeroPagine= " di " + numpages;
			numeroRecords=" di " + numrecords;
		}
    
    	if (numpages != 1) {
    %> 
    
      <img src="../../img/list_first.gif" alt="Prima pagina" onclick="naviga(1, 'LIST_PAGE', false, null);"/>
      &nbsp;
      <img src="../../img/list_prev.gif" alt="Pagina precedente" onclick="naviga(calcolaPrecedente(), 'LIST_PAGE', false, null);" />
      &nbsp;
      <img src="../../img/list_next.gif" alt="Pagina successiva" onclick="naviga(calcolaSuccessiva(), 'LIST_PAGE', false, null);" />
      &nbsp;
      <img src="../../img/list_last.gif" alt="Ultima pagina" onclick="naviga(<%=serviceResponse.getAttribute("M_MOVGETLISTAAPPOGGIO.ROWS.NUM_PAGES")%>, 'LIST_PAGE', false, null);" />

    <% } else { %>
      
      <img src="../../img/list_first_gri.gif"/>
      &nbsp;
      <img src="../../img/list_prev_gri.gif" />
      &nbsp;
      <img src="../../img/list_next_gri.gif"/>
      &nbsp;
      <img src="../../img/list_last_gri.gif"/>
    
    <%  } %> 
    
     <br>  
       
    </af:form>

<p class="titolo"><b>Pag. <%=currentpage%><%=numeroPagine%> (da <%=numeroda%> a <%=numeroa%><%=numeroRecords%>)</b></p>
	<%}%>
	<af:form name="FormListaValidazione" method="POST" action="AdapterHTTP">
    
      <input type="hidden" name="PAGE" value="MovValidazioneMassivaPage"/>
      <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
      <input type="hidden" name="CONTEXT" value="<%=context%>"/>
      <input type="hidden" name="AZIONE" value="validaSelezionati"/>

      <!--PARAMETRI USATI NELLA RICERCA PER FILTRARE I MOVIMENTI -->
      <input type="hidden" name="datmovimentoda" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"datmovimentoda")%>"/>
      <input type="hidden" name="datmovimentoa" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"datmovimentoa")%>"/>
      <input type="hidden" name="datcomunicazionea" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"datcomunicazionea")%>"/>
      <input type="hidden" name="datcomunicazioneda" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"datcomunicazioneda")%>"/>
      <input type="hidden" name="tipoMovimento" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"tipoMovimento")%>"/>
      <input type="hidden" name="codTipoAss" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codTipoAss")%>"/>
      <input type="hidden" name="codMonoTempo" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codMonoTempo")%>"/>
      <input type="hidden" name="tipoRapporto" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRapporto")%>"/>
      <input type="hidden" name="normativa" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"normativa")%>"/>
      <input type="hidden" name="CODMANSIONE" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"CODMANSIONE")%>"/>
      <input type="hidden" name="codCCNL" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codCCNL")%>"/>
      <input type="hidden" name="nome" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"nome")%>"/>
      <input type="hidden" name="cognome" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"cognome")%>"/>
      <input type="hidden" name="codiceFiscaleLavoratore" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codiceFiscaleLavoratore")%>"/>
      <input type="hidden" name="pIva" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"pIva")%>"/>
      <input type="hidden" name="ragioneSociale" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"ragioneSociale")%>"/>
      <input type="hidden" name="codFiscaleAzienda" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codFiscaleAzienda")%>"/>
      <input type="hidden" name="codTipoAzienda" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"codTipoAzienda")%>"/>
      <input type="hidden" name="CODCPI" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"CODCPI")%>"/>
      <input type="hidden" name="CODCPILAV" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"CODCPILAV")%>"/>
      <input type="hidden" name="referente" value="<%= strReferente %>"/>
      <input type="hidden" name="numGgTraMovComunicaz" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"numGgTraMovComunicaz")%>"/>
      <input type="hidden" name="cdnLavoratore" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"cdnLavoratore")%>"/>
      <input type="hidden" name="prgAzienda" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"prgAzienda")%>"/>
      <input type="hidden" name="prgUnita" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"prgUnita")%>"/>
            
      <%out.print(htmlStreamTop);%>      
      <table class="<%=stile%>" border="0">
      <%  
      	//Creo l'intestazione solo se ho almeno un risultato
        if (_enum.hasMoreElements()){
      %>
        <tr class="<%=stile%>">
          <th class="<%=stile%>">&nbsp;</th>
          <%if (!context.equals("validaArchivio")) {%>
          <th class="<%=stile%>">&nbsp;</th>
          <th class="<%=stile%>">&nbsp;</th>
          <%}%>
          <th class="<%=stile%>">Data Inizio</th>
          <th class="<%=stile%>">Tipo</th>
          <th class="<%=stile%>">Cod. Comunicazione</th>
          <th class="<%=stile%>">Tipo Comunicazione</th>
          <th class="<%=stile%>">Tipo Contr. in Origine</th>
          <th class="<%=stile%>">Tempo</th>
          <th class="<%=stile%>">Lavoratore</th>
          <th class="<%=stile%>">Rag. Sociale Azienda</th>
          <th class="<%=stile%>">Indirizzo Azienda</th>
          <th class="<%=stile%>">&nbsp;</th>        
        </tr>
      <%} else { //Indico che non ho movimenti%>
      	<tr><td colspan="2"><p align="center" class="titolo">Nessun movimento nella lista</p></td></tr>
      <%} 
      	int i = 0;
        while (_enum.hasMoreElements()) {
	        //finchè ho righe le visualizzo
	        SourceBean row = (SourceBean) _enum.nextElement();
	        i++;
      %>
        <tr class="<%=stile%>">
          <!--Pulsanti per dettaglio-->
          <td class="<%=stile%>" valign="middle">
           <a href="AdapterHTTP?PAGE=MovValidaDettaglioGeneralePage&CDNFUNZIONE=<%=_funzione%>&PROVENIENZA=validazione&destinazione=MovValidaDettaglioGeneralePage&PAGERITORNOLISTA=MovListaValidazionePage&CONTEXT=<%=context%>&PRGMOVIMENTOAPP=<%=it.eng.sil.util.Utils.notNull(row.getAttribute("PRGMOVAPP"))%>">
              <img border="0" src="../../img/detail.gif" alt="Dettaglio"/>
           </a>
          </td>
          <%if (!context.equals("validaArchivio")) {%>
          <td class="<%=stile%>" valign="middle">
           <a href="#" onClick="risultatiValidazioneSingoloMov(<%=it.eng.sil.util.Utils.notNull(row.getAttribute("PRGMOVAPP"))%>)">
              <img border="0" src="../../img/validazione.gif" alt="Risultati validazione"/>
           </a>
          </td>
          <!--checkbox della riga-->
          <td class="<%=stile%>">
            <input type="checkbox" name="ckeckboxMovimenti" value="<%=row.getAttribute("PRGMOVAPP")%>"/>
          </td>
    	  <%}%>
          <!--elementi della riga-->
          <td class="<%=stile%>"><%=it.eng.sil.util.Utils.notNull(row.getAttribute("DATAMOV")) %></td>
          <td class="<%=stile%>"><%=it.eng.sil.util.Utils.notNull(row.getAttribute("CODTIPOMOV")) %></td>
          <td class="<%=stile%>"><%=it.eng.sil.util.Utils.notNull(row.getAttribute("CODCOMUNICAZIONE")) %></td>
          <td class="<%=stile%>"><%=it.eng.sil.util.Utils.notNull(row.getAttribute("DESCRTIPOCOMUNICAZ")) %></td>
          <td class="<%=stile%>"><%=it.eng.sil.util.Utils.notNull(row.getAttribute("CODTIPOASS")) %></td>          
          <td class="<%=stile%>"><%=it.eng.sil.util.Utils.notNull(row.getAttribute("CODMONOTEMPO")) %></td>
          <td class="<%=stile%>"><%=it.eng.sil.util.Utils.notNull(row.getAttribute("COGNOMELAV")) + " " + it.eng.sil.util.Utils.notNull(row.getAttribute("NOMELAV"))%></td>
          <td class="<%=stile%>"><%=it.eng.sil.util.Utils.notNull(row.getAttribute("RAGSOCAZ")) %></td>
          <td class="<%=stile%>"><%=it.eng.sil.util.Utils.notNull(row.getAttribute("IndirAzienda")) %></td>
          <!--Pulsanti per cancellazione-->
          <td class="<%=stile%>">
            <a href="#" onclick="naviga(<%=serviceResponse.getAttribute("M_MOVGETLISTAAPPOGGIO.ROWS.CURRENT_PAGE")%>, 'LIST_PAGE', true, <%=it.eng.sil.util.Utils.notNull(row.getAttribute("PRGMOVAPP"))%>)">
              <img border="0" src="../../img/del.gif" alt="Cancella"/>
            </a>
          </td>
        </tr>
        <% } //while 
          out.print("<script language=\"Javascript\"> numRighe = " + i + "</SCRIPT>");
 		%> 		
 		<tr><td>&nbsp;</td></tr>
        <tr><td>&nbsp;</td></tr>
        <tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>
        <%if (!context.equals("validaArchivio")) {
        	if (it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil.protocollazioneLocale()) { %>
        <td colspan="8" align="left" class="azzurro_bianco">        
        <table class="main" cellpadding="0" cellspacing="0" border="0" width="100%">
         <tr><td colspan="8" align="center"><div class="sezione2">Protocollazione</div></td></tr>
         <tr>
           <td class="etichetta2">Anno</td>
           <td class="campo2">
             <af:textBox name="numAnnoProt"
                       validateOnPost="true" 
                       title="Anno di protocollazione"
                       value="<%= Utils.notNull(numAnnoProtV) %>"
                       classNameBase="input"
                       size="4"
                       maxlength="4"
                       required="true"
                       readonly="true" />
           </td>            
           <td class="etichetta2">Num&nbsp;</td>
           <td class="campo2" nowrap>
             <af:textBox name="numProt"
                       title="Numero di protocollo"
                       value="<%= Utils.notNull(numProtV) %>"
                       classNameBase="input"
                       size="6"
                       maxlength="38"
                       required="true"
                       readonly="<%=String.valueOf(!numProtEditable)%>" />
           </td>
           <td class="etichetta2">data</td>
           <td>
              <af:textBox name="dataProt" 
                          type="date" 
                          value="<%=datProtV%>" 
                          size="11" 
                          maxlength="10"
                          title="data di protocollazione"  
                          classNameBase="input" 
                          readonly="<%=String.valueOf(!numProtEditable)%>" 
                          validateOnPost="true" 
                          required="false" 
                          trim ="false" 
                          onKeyUp="cambiAnnoProt(this,numAnnoProt)" 
                          onBlur="checkFormatDate(this)"
               />&nbsp;*</td>
           <td class="etichetta2">ora</td>
           <td>
              <af:textBox name="oraProt"
                          type="date"
                          value="<%=oraProtV%>"
                          size="6" 
                          maxlength="5"
                          title="ora"  
                          classNameBase="input" 
                          readonly="<%=String.valueOf(!numProtEditable)%>"
                          validateOnPost="false" 
                          required="false" 
                          trim ="false"
                          onBlur="checkAndFormatTime(this)"
               />&nbsp;*
           <input name="tipoProt" type="hidden" value="<%=prAutomatica%>" />
           </td>
         </tr>
      </table>
         <%} else {%>
				<input type="hidden" name="numAnnoProt" value="<%= Utils.notNull(numAnnoProtV) %>">
				<input type="hidden" name="numProt" value="0">
				<input type="hidden" name="dataProt" value="<%= datProtV %>">
				<input type="hidden" name="oraProt" value="00:00">
         <%}%>
      </td>
      <%}%>
      </tr>
      <tr><td>&nbsp;</td></tr>
	  <%if (!context.equals("validaArchivio")) { %>
	  <tr>
	  	<td colspan="10" align="center">
	      <input class="pulsanti" type="button" onclick="selectPage();" name="selezionaPagina" value="Seleziona pagina"/>   
	      &nbsp;&nbsp;      
	      <!-- input type="hidden" name="CONTEXT" value="valida"/ -->        
	      <input class="pulsanti" type="button" onclick="confirmValidateSelected();" name="valida" value="Valida selezionati" />
	      &nbsp;&nbsp;
	      <input class="pulsanti" type="button" onclick="confirmDeleteSelected(<%=serviceResponse.getAttribute("M_MOVGETLISTAAPPOGGIO.ROWS.CURRENT_PAGE")%>);" name="cancellaSelezionati" value="Cancella selezionati" />
          &nbsp;&nbsp;
	      <input class="pulsanti" type="button" onclick="stampaMovimenti();" name="stampa" value="Stampa selezionati"/>
	      &nbsp;&nbsp;
		  <%if (validazioneInCorso) {%>
		  	<input class="pulsanti" type="button" onclick="arrestaValidazione();" name="reset" value="Arresta validazione" title="Ferma la validazione massiva attualmente in esecuzione"/>        
		  <%}%>           
	      &nbsp;&nbsp;
	      <input class="pulsanti" type="button" onclick="altraRicerca();" name="cerca" value="Altra ricerca"/>	  		
	  	</td>
	  </tr>
      <tr>
        <td colspan="10" align="center">
        <%if (risultatiInSessione) {%>
        <input class="pulsanti" type="button" onclick="visulizzaRisultati();" name="azione" value="Risultati ultima validazione" title="Visualizza i risultati dell'ultima validazione massiva eseguita"/>
        <%}%>
        </td>
      </tr> 
      <%} else { 
      		if (root_page.equals("MovRicercaValidazionePage")) {%>
	      		<tr><td colspan="10" align="center"><input class="pulsanti" type="button" onclick="altraRicerca();" name="cerca" value="Altra ricerca"/></td></tr>	  		
      <%	} else {%>
     			<tr><td colspan="10" align="center"><input type="button" class="pulsanti" value="Chiudi" onClick="window.close()"></td></tr>       	
      <%	}
      	}
      %>			
      </table>
      <%out.print(htmlStreamBottom);%>
    </af:form>    
    <!--Bottone di prova per la validazione interattiva   
	<af:form name="ValidazioneInterattiva" method="POST" action="AdapterHTTP">
      <input type="hidden" name="PAGE" value="MovValidaInterattivaPage"/>
      <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
      <input class="pulsanti" type="submit" name="cerca" value="Validazione Interattiva"/>      
    </af:form> -->
    </center>
  </body>
</html>