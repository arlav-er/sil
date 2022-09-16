<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page import="
  com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.User,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  it.eng.sil.security.PageAttribs,
  com.engiweb.framework.security.*" %>
  
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %> 

<%      
    SourceBean row = null;     
    
    String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");
    String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
    String prgAltraIscr = StringUtils.getAttributeStrNotNull(serviceRequest,"prgAltraIscr");
    
    String configurazione = serviceResponse.containsAttribute("M_GetConfigAltreIscr.CONFIGURAZIONE")?serviceResponse.getAttribute("M_GetConfigAltreIscr.CONFIGURAZIONE").toString():"0";
    
    //C'è questa configurazione perchè Con Domenico si è deciso di utilizzare un nuovo parametro per tutte le etichette personalizzate.
    String configuraDataInizio=(String)Utils.getConfigValue("LABEL").getAttribute("ROW.NUM"); 	//La Valle d'Aosta vuole la data inizio prevalorizzata a oggi e la data fine + 1 anno nell'inserimento
    boolean prevalorizzaDataInizio = false;						//di una nuova iscrizione
    
    String datInizio = "";
    String datIniziOld = "";
    String datFine = "";
    String codtipoiscr = "";
    String prgAzienda = "";
    String prgUnita = "";
    String datCompetenza = "";
    String codMotChiusuraIscr = "";
    String datChiusuraIscr = "";
    String dataLicenziamento = "";
    String prgaccordo = "";
    String codStato = "";
    String descrStato = "";
    String codComCompetenza = "";
    String codMonoTipoCompetenza = "M";
    String numKloAltraIscr = "";
    BigDecimal cdnUtins = null;
    BigDecimal cdnUtmod = null;
	String dtmins = "";
    String dtmmod = "";
	String strNote = "";
	String ragioneSociale = "";
	String indirizzo = "";
	String comune = "";
	String tipoIscr = "";
	String motChiusura = "";
	String competenza = "";
	String codAccordo = "";
	String strCodFiscale = "";
	String flgSl = "";
  	String valDisplay="none";
  	String displayStatoNoFlag = "";
  	String displayStatoFlag = "none";
  	String queryString = null;
    String urlPdfAccSin = "";
    String flgModifDate = "";
    String flgDirittoDo = "";
    String codMotivoNoDo = "";
	
    Testata operatoreInfo = null;
    
    String _page = (String) serviceRequest.getAttribute("PAGE"); 
    int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
    codComCompetenza = (String)serviceResponse.getAttribute("M_CodComDomLav.ROWS.ROW.codComCompetenza");

    PageAttribs attributi = new PageAttribs(user, _page);
    
    boolean canInsert = attributi.containsButton("INSERISCI");   
    boolean canModify = attributi.containsButton("AGGIORNA");
    boolean canDelete = attributi.containsButton("CANCELLA");
    boolean canPrint  = attributi.containsButton("STAMPA");
    
    boolean flagChanged = false;
    
    String fieldReadOnly = canModify?"false":"true";
    String fieldReadOnlyDatInizioFine = canModify?"false":"true";
    String fieldReadOnlyInfoMobDeroga = canModify?"false":"true";
    String fleldsLegatiAccordo = fieldReadOnly;
    String fleldsChangeStato = fieldReadOnly;
    
    boolean nuovo = true;
    String moduleNameGet = "";
    if(configurazione.equals("0") || configurazione.equals("2")) {
    	if(serviceResponse.containsAttribute("M_IscrizioniCIG"))  { 
    		 nuovo = false;
    		 moduleNameGet = "M_IscrizioniCIG";
    	}
    	else 
    	{ 
    		nuovo = true; 
    	}	
    }
    else {
    	if(serviceResponse.containsAttribute("M_GetAltraIscrizione"))  { 
	   		nuovo = false;
	   		moduleNameGet = "M_GetAltraIscrizione";
	   	}
	   	else 
	   	{ 
	   		nuovo = true; 
	   	}	
    } 
    												
    if (configuraDataInizio.equals("1")) 	//La Valle d'Aosta vuole la data inizio prevalorizzata a oggi nell'inserimento
    	prevalorizzaDataInizio = true;				//di una nuova iscrizione
    	
    
    String apriDiv = (serviceRequest.containsAttribute("APRIDIV"))?"":"none";
    String url_nuovo = "AdapterHTTP?PAGE=CigLavListaPage" +
    					"&cdnLavoratore=" + cdnLavoratore +
    					"&cdnFunzione=" + cdnFunzione +
    					"&prgAltraIscr=" + prgAltraIscr +
    					"&APRIDIV=1";

    if(!nuovo) {
		row = (SourceBean)serviceResponse.getAttribute(moduleNameGet+".ROWS.ROW");
		
		prgAzienda = Utils.notNull(row.getAttribute("prgazienda"));
		prgUnita = Utils.notNull(row.getAttribute("prgunita"));
		ragioneSociale = Utils.notNull(row.getAttribute("strragionesociale"));      
		indirizzo = Utils.notNull(row.getAttribute("strindirizzo"));    
		comune = Utils.notNull(row.getAttribute("comune"));
		strCodFiscale = Utils.notNull(row.getAttribute("strcodicefiscale"));
		datInizio = Utils.notNull(row.getAttribute("datInizio"));        
		datFine = Utils.notNull(row.getAttribute("datFine"));
		codtipoiscr = Utils.notNull(row.getAttribute("codtipoiscr"));
		tipoIscr = Utils.notNull(row.getAttribute("tipoIscr"));
		datCompetenza = Utils.notNull(row.getAttribute("datCompetenza"));
		codMotChiusuraIscr = Utils.notNull(row.getAttribute("codMotChiusuraIscr"));
		motChiusura = Utils.notNull(row.getAttribute("motChiusura"));
		datChiusuraIscr = Utils.notNull(row.getAttribute("datChiusuraIscr"));
		codStato = Utils.notNull(row.getAttribute("codStato"));
		descrStato = Utils.notNull(row.getAttribute("descrStato"));
		codMonoTipoCompetenza = Utils.notNull(row.getAttribute("codMonoTipoCompetenza"));
		competenza = Utils.notNull(row.getAttribute("Competenza"));
		strNote = Utils.notNull(row.getAttribute("strnota"));
		flgSl = Utils.notNull(row.getAttribute("flgSl"));
		prgaccordo = Utils.notNull(row.getAttribute("prgaccordo"));
		codAccordo = Utils.notNull(row.getAttribute("codaccordo"));
		flgModifDate = Utils.notNull(row.getAttribute("flgModDate"));
		dataLicenziamento = Utils.notNull(row.getAttribute("datLicenz"));
		flgDirittoDo = Utils.notNull(row.getAttribute("flgdirittodo"));
	    codMotivoNoDo = Utils.notNull(row.getAttribute("codmotivonotdo"));
	    
	    datIniziOld = datInizio;
	    
		if(!prgaccordo.equals("")) {
			fieldReadOnly = "true";
			fleldsLegatiAccordo = "false";
			fleldsChangeStato = "false";
			if (flgModifDate.equalsIgnoreCase("N")) {
				fieldReadOnlyDatInizioFine = "true";		
			}
			if (serviceResponse.containsAttribute("M_PdfAccordoSindacale.ROWS.ROW")) {
				String token = StringUtils.getAttributeStrNotNull(serviceResponse,"M_PdfAccordoSindacale.ROWS.ROW.token");
			    String strUrl = StringUtils.getAttributeStrNotNull(serviceResponse,"M_PdfAccordoSindacale.ROWS.ROW.strvalore");
			    urlPdfAccSin = strUrl + "?t=" + token;
			}
		} 
		
		if(flgSl.equals("S") || (!codMotChiusuraIscr.equals("") && !datChiusuraIscr.equals(""))) {
			canModify = false; 
			fieldReadOnly="true";
			fieldReadOnlyDatInizioFine = "true";
			fleldsLegatiAccordo = "true";
			fleldsChangeStato = "true";
			fieldReadOnlyInfoMobDeroga = "true";
			if (flgSl.equals("S")) {
				displayStatoNoFlag = "none";
			  	displayStatoFlag = "";
			}
		}
		
		if (codStato.equals("AA")) {
			fleldsChangeStato = "false";
			fieldReadOnly="true";
			fieldReadOnlyDatInizioFine = "true";
			fleldsLegatiAccordo = "true";
			fieldReadOnlyInfoMobDeroga = "true";
		}
		
		numKloAltraIscr = StringUtils.getAttributeStrNotNull(row, "NUMKLOALTRAISCR");  
		cdnUtins = (BigDecimal) row.getAttribute("cdnUtins");
     	dtmins = StringUtils.getAttributeStrNotNull(row, "dtmins");
	 	cdnUtmod = (BigDecimal) row.getAttribute("cdnUtmod");
	 	dtmmod = StringUtils.getAttributeStrNotNull(row, "dtmmod");
	 	operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
	 }
    
     Vector tipoIscrizioni = serviceResponse.getAttributeAsVector("CI_TIPO_ISCR.ROWS.ROW");
     boolean provenienzaDaLista = serviceRequest.containsAttribute("LISTAALTREISCRIZIONI");
     
     
     String dataOdierna = DateUtils.getNow();
     String dataOdiernaPlusAnno = DateUtils.aggiungiNumeroGiorni(dataOdierna, 365);
     
%>

<html>
<head>
  <title>Domande Cig</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css"/> 
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
  <af:linkScript path="../../js/"/>
  <%@ include file="../../jsp/documenti/_apriGestioneDoc.inc"%>
  
  <%@ include file="RicercaSoggettoCIG.inc" %>
  <SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>
  <SCRIPT TYPE="text/javascript">
  
  var flagChanged = false;
  
  function fieldChanged() {
  	<% if (!fieldReadOnly.equalsIgnoreCase("true") || !fleldsLegatiAccordo.equals("true")){ %> 
		flagChanged = true;
	 <%}%> 
  }

  //31-03-10 Rodi - questa funzione non serve più! Per ora non la elimino ma in futuro si potrebbe fare se non sono nati problemi.
  function indietro_old() {
  	
  	if (isInSubmit()) return;
		
	if(flagChanged){ 
		if(!confirm("I dati sono cambiati.\nProcedere lo stesso?")) { 
			return false; 
		}
 	}
	var urlpage="AdapterHTTP?";
    	urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&";
    	urlpage+="CDNLAVORATORE=<%=cdnLavoratore%>&";
    	urlpage+="PAGE=CigLavListaPage&";
    	urlpage+="prgAltraIscr=<%=prgAltraIscr%>&";
    	setWindowLocation(urlpage);
  }
  	
  function dettaglio(prgAltraIscr) {
  	if (isInSubmit()) return;
    	
    	var s= "AdapterHTTP?"
        s += "PAGE=CigLavListaPage&";
        s += "prgAltraIscr=" + prgAltraIscr+"&";
        s += "CDNLAVORATORE=<%= cdnLavoratore %>&";
        s += "CDNFUNZIONE=<%= cdnFunzione %>&";
        s += "DETTAGLIO=TRUE&";
        s += "APRIDIV=1&";
       setWindowLocation(s);
    }
    
    function cancella(prgAltraIscr) {
	  if (isInSubmit()) return;
		var s="Eliminare l'iscrizione?";
      	if ( confirm(s) ) {
			var s= "AdapterHTTP?";
        		s += "PAGE=CigLavListaPage&";
        		s += "MODULE=M_DeleteIscrCig&";
        		s += "prgAltraIscr=" + prgAltraIscr + "&";
        		s += "CDNLAVORATORE=<%= cdnLavoratore %>&";
        		s += "CANCELLA=TRUE&";
        		s += "CDNFUNZIONE=<%=cdnFunzione%>";
        		setWindowLocation(s);
      }
      return;
    }
    
    function aggiornaAzienda(){
        document.Frm1.ragioneSociale.value = opened.dati.ragioneSociale;
        document.Frm1.codiceFiscale.value = opened.dati.codiceFiscaleAzienda;
        document.Frm1.strCodFiscale.value = opened.dati.codiceFiscaleAzienda;
        document.Frm1.indirizzo.value = opened.dati.strIndirizzoAzienda;
        document.Frm1.comune.value = opened.dati.comuneAzienda
        document.Frm1.prgAzienda.value = opened.dati.prgAzienda;
        document.Frm1.prgUnita.value = opened.dati.prgUnita;
        opened.close();
        var imgV = document.getElementById("tendinaAzienda");
        cambiaLavMC("aziendaSez","");
        imgV.src=imgAperta;
    }
    
    function azzeraAzienda(){
      	document.Frm1.ragioneSociale.value = "";
      	document.Frm1.codiceFiscale.value = "";
        document.Frm1.strCodFiscale.value = "";
        document.Frm1.indirizzo.value = "";
        document.Frm1.comune.value = "";
        document.Frm1.prgAzienda.value = "";
        document.Frm1.prgUnita.value = "";
        var imgV = document.getElementById("tendinaAzienda");
        cambiaLavMC("aziendaSez","none");
        imgV.src=imgChiusa;
    }
    
	var motiviChiusura=new Array();
	var motiviChiusura_TipoIscr=new Array();
	<%if (!fleldsLegatiAccordo.equals("true")) {			
		Vector motChiusRows=serviceResponse.getAttributeAsVector("CI_MOTIVO_CHIUSURA.ROWS.ROW");
		String descmotChius="";
		String codmotChius="";
		Iterator record=motChiusRows.iterator();
		while(record.hasNext()) {
			row= (SourceBean) record.next();
			codmotChius=StringUtils.getAttributeStrNotNull(row, "CODICE");
			int indice = codmotChius.indexOf("-");
			String codTipoIscrMot = codmotChius.substring(0,indice);
			descmotChius=StringUtils.getAttributeStrNotNull(row, "DESCRIZIONE");
			descmotChius=(descmotChius.length()>60)?descmotChius.substring(0,60) + "..." : descmotChius;
			out.print("motiviChiusura[\""+codmotChius+"\"]=\""+descmotChius+"\";\n");
			out.print("motiviChiusura_TipoIscr[\""+codmotChius+"\"]=\""+codTipoIscrMot+"\";\n");
		}
	 }%>    

	//...senza parole... Donisi
    function gestisciMotChiusura(orig){

		<% if (configurazione.equals("2")) {%>
			var j = 0;
			for(var i=1; i<document.Frm1.codtipoiscr.options.length;i++){
				if(document.Frm1.codtipoiscr.options[i].selected){
					j++;
				}	
			}	
					
			if (j > 1) {
				while (document.forms[0].codMotChiusuraIscr.options.length>0) {
					document.forms[0].codMotChiusuraIscr.options[0]=null;
				}
				return;
			} else {
				// recupero valore selezionato
				var codtipoiscr;
				var codMotChiusIscr = "";
				for(var i=1; i<document.Frm1.codtipoiscr.options.length;i++){
					if(document.Frm1.codtipoiscr.options[i].selected){
						codtipoiscr = document.Frm1.codtipoiscr.options[i].value;
					}
				}		
				
				//svuoto 
				while (document.forms[0].codMotChiusuraIscr.options.length>0) {
					document.forms[0].codMotChiusuraIscr.options[0]=null;
				}		
				//riempio 
	    		j=1;
	     		for (prop in motiviChiusura_TipoIscr) {
	     			if (motiviChiusura_TipoIscr[prop]==codtipoiscr) {
	     				var indiceIscr = prop.indexOf("-");
	    		     	document.Frm1.codMotChiusuraIscr.options[j]=new Option(motiviChiusura[prop], prop.substring(indiceIscr + 1,prop.length), false, false);      			
	    		     	if (document.Frm1.codMotChiusuraIscr.options[j].value == codMotChiusIscr) {
	    		     		document.Frm1.codMotChiusuraIscr.options[j].selected = "true";
	    		     	}
	    		     	j++;
	    			}
	     		}
			}	
			
		<% } else {%>
		 	
	    	var codtipoiscr = document.Frm1.codtipoiscr.value;
			var codMotChiusIscr = "";
			if (orig=='onLoad') {
				<%if(prevalorizzaDataInizio){ //Sono in Valle d'Aosta%>
					setDateInizioFine();
				<%}%>
				codMotChiusIscr = "<%=codMotChiusuraIscr%>";
			} 
			
		
			if (codtipoiscr!="") {
				//svuoto 
				while (document.forms[0].codMotChiusuraIscr.options.length>0) {
					document.forms[0].codMotChiusuraIscr.options[0]=null;
				}		
				//riempio 
	    		j=1;
	     		for (prop in motiviChiusura_TipoIscr) {
	     			if (motiviChiusura_TipoIscr[prop]==codtipoiscr) {
	     				var indiceIscr = prop.indexOf("-");
	    		     	document.Frm1.codMotChiusuraIscr.options[j]=new Option(motiviChiusura[prop], prop.substring(indiceIscr + 1,prop.length), false, false);      			
	    		     	if (document.Frm1.codMotChiusuraIscr.options[j].value == codMotChiusIscr) {
	    		     		document.Frm1.codMotChiusuraIscr.options[j].selected = "true";
	    		     	}
	    		     	j++;
	    			}
	     		}
			} else {
				//svuoto 
				while (document.forms[0].codMotChiusuraIscr.options.length>0) {
					document.forms[0].codMotChiusuraIscr.options[0]=null;
				}
			}
		<%}%>	
	}
	
	function controlloDate(){
		
	    var datInizio = document.Frm1.datInizio;
	    var datFine = document.Frm1.datFine;    
		if ((datInizio.value != "") && (datFine.value != "")) {
	      if (compareDate(datInizio.value,datFine.value) > 0) {
	      	alert(datInizio.title + " maggiore di " + datFine.title);
	      	datInizio.focus();	      	
		    return false;
		  }	
		}
		<% if (prevalorizzaDataInizio){ //Controllo fatto solo per la Valle d'Aosta, quando il campo data inizio è prevalorizzato%>
		var datOdierna = '<%=dataOdierna%>';	
		if ((datInizio.value != "") && (datOdierna != "")){
			if (compareDate(datInizio.value,datOdierna) > 0){
				alert(datInizio.title + " non deve essere maggiore della data odierna");
				datInizio.focus();	  			
 	  			<%if (nuovo){%>
 	  				document.Frm1.datInizio.value = '<%=dataOdierna%>'; 
 	  			<%}else{%>
 	  				document.Frm1.datInizio.value = '<%=datIniziOld%>';  	  			
 	  			<%}%> 	  			
 				return false;
			}
			
		}
		<%}%>		
	   	return true;
	}
  
	function controllaCampi(){
		
		var azienda = document.Frm1.strCodFiscale.value;
		var checKAzienda = false;
		
		<% if (configurazione.equals("2")) {%>
			var codtipoiscr;
			for(var i=1; i<document.Frm1.codtipoiscr.options.length;i++){
				if(document.Frm1.codtipoiscr.options[i].selected){
					codtipoiscr = document.Frm1.codtipoiscr.options[i].value;
					<%
					for(int k=0;k<tipoIscrizioni.size();k++) {
				  		SourceBean rowIscr = (SourceBean)tipoIscrizioni.get(k);
				  	%>
						if (codtipoiscr == '<%=rowIscr.getAttribute("CODICE").toString()%>') {
				  			var obbligoAz = '<%=Utils.notNull(rowIscr.getAttribute("FLGOBBAZ"))%>'
				  			if (obbligoAz == 'S') {
				  				checKAzienda = true;
				  				break;
				  			}
				  		}
				  <%}%>	
				}
			}	
	  	<% } else { %>
		  	var codtipoiscr = document.Frm1.codtipoiscr.value;
		  	
		  	<%
		  	for(int k=0;k<tipoIscrizioni.size();k++) {
		  		SourceBean rowIscr = (SourceBean)tipoIscrizioni.get(k);
		  		%>
		  		if (codtipoiscr == '<%=rowIscr.getAttribute("CODICE").toString()%>') {
		  			var obbligoAz = '<%=Utils.notNull(rowIscr.getAttribute("FLGOBBAZ"))%>'
		  			if (obbligoAz == 'S') {
		  				checKAzienda = true;
		  			}
		  		}
		  		<%
		  	}
	  	} %>
	  	  
	  	var motChiusura = document.Frm1.codMotChiusuraIscr.value;
	  	var dataChiusura = document.Frm1.datChiusuraIscr.value;
	  	
	  	if(checKAzienda && azienda == "") {
	  		alert("Inserire i dati dell'azienda");
	  		return false;
	  	}
	  	
	  	if(motChiusura != "" && dataChiusura == ""){
	  		alert("il campo Data chiusura iscr. è obbligatorio");
	  		return false;
	  	}
	  	if(motChiusura == "" && dataChiusura != ""){
	  		if (document.Frm1.codMotChiusuraIscr.length > 1) {
	  			alert("il campo Motivo chiusura iscr. è obbligatorio");
	  			return false;
	  		}
	  	}
	  	
	  	return true;
	}
 
	function Stampa(){
	 	<% String parametri = "&prgaccordo="+prgaccordo;
	 		if ((queryString==null) || (queryString.length() == 0))
				queryString = (String)requestContainer.getAttribute("HTTP_REQUEST_QUERY_STRING");
	 	%> 
		var HTTPrequest = "<%=getQueryString(queryString)%>";  	
	    var urlDoc = "AdapterHTTP?";
		urlDoc += "PAGE=REPORTFRAMEPAGE";
		urlDoc += "&ACTION_REDIRECT=RPT_DOMANDA_CIG";
		urlDoc += "&apri=true";
		urlDoc += "&asAttachment=false";
		urlDoc += "&mostraPerLavoratore=true";
		urlDoc += "&docInOut=I";
		urlDoc += "&tipoDoc=CIGDOM";
		urlDoc += "&tipoFile=PDF";
		urlDoc += "&rptAction=RPT_DOMANDA_CIG";
		urlDoc += "&showNoteCPI=true";
		urlDoc += '<%=parametri%>';
		urlDoc += "&QUERY_STRING="+HTTPrequest;
		document.location=urlDoc;
	 }
 
	 function settaStato(){
	 	document.Frm1.codStato.value = document.Frm1.codStatoNoFlag.value;
	 }
	 
	 function VisualizzaPdfAccordoSindacale() {
		<%if (!urlPdfAccSin.equals("")) {%>
			var f = "<%=urlPdfAccSin%>";
	     	var t = "_blank";
	        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=850,height=800,top=30,left=180";
	        window.open(f, t, feat);
        <%}%>		   	
	 }
	 
     function setDateInizioFine() {
		document.Frm1.setDateInizioFine.value = 'true';     
		<%if (nuovo && prevalorizzaDataInizio){   //in inserimento nuova iscrizione 	
			datInizio=dataOdierna;
			datFine=dataOdiernaPlusAnno;    		
      	}%>
      		
	}
     
     function dataInizioPlus365(){       
   		<% if(prevalorizzaDataInizio){%> //la data fine diventa data inizio + 1 anno solo se sono in Valle d'Aosta. %>
 			if (validateDate('datInizio')){ <%//data inizio deve essere nel formato corretto%>
 				<%if (nuovo){//in inserimento nuova iscrizione %> 						
 	 				if(document.Frm1.datInizio.value!=""){	 	 				 
 	 	 				document.Frm1.datFine.value = aggiungiGiorni(document.Frm1.datInizio.value,364);
 						if (checkBisestile()){ 				
 	  						document.Frm1.datFine.value = aggiungiGiorni(document.Frm1.datFine.value,1);
 	  					} 	
 	 				}else {
                        alert('La Data Inizio non può essere vuota.');
                        return false;
 	 				}	
 	 	 				   				
 	  			<%}%> 	  					  			
 	 		}
 		<%}%>
 			
   	}

 	function checkBisestile(){
		var inizioBisestile = false;
		var fineBisestile   = false;
		var dataInizio = document.Frm1.datInizio.value;
		var dataFine = document.Frm1.datFine.value;
 		if (isAnnoBisestile(dataInizio)){
 			inizioBisestile=true;
 	 	}
 		if (isAnnoBisestile(dataFine)){
 			fineBisestile=true;
 	 	}

 	 	if (inizioBisestile && fineBisestile){
			return true;
 	 	}

 	 	if (inizioBisestile && (confrontaDate(dataInizio,'29/02/'+dataInizio.substr(dataInizio.length-4)) >= 0)){
			return true;
 	 	}

 	 	if (fineBisestile &&   (confrontaDate(dataFine,'28/02/'+dataFine.substr(dataFine.length-4)) <= 0)){
			return true;
 	 	}

 	 	return false;
 	}

</SCRIPT>
    
<script language="Javascript">
    if(window.top.menu != undefined)
       window.top.menu.caricaMenuLav(<%=cdnFunzione%>,<%=cdnLavoratore%>);
</script>
<script language="Javascript">
    function prevalDataInizio(){       
        <%if(prevalorizzaDataInizio){ %>//Sono in Valle d'Aosta%>
			setDateInizioFine();
		<%}%>		   	
    }  
 
</script>
<script language="Javascript">
    function callBackDateControl(){       
        <%if(prevalorizzaDataInizio){ //Sono in Valle d'Aosta%>
        	var t = controlloDate();
        	if(t=true) dataInizioPlus365();
		<%}%>		   	
    }  
 
</script>






<script language="Javascript">
<%	
       attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore+"&prgAltraIscr="+prgAltraIscr);
%>
</script>

</head>

<body class="gestione" onload="rinfresca();<%if (!fleldsLegatiAccordo.equals("true")) {%>gestisciMotChiusura('onLoad');<%}%>">

	<%  
   		InfCorrentiLav _testata= new InfCorrentiLav( sessionContainer, cdnLavoratore, user);
        
        _testata.show(out);
        Linguette _linguetta = new Linguette(user, 1 , "CigLavListaPage", new BigDecimal(cdnLavoratore)); 
        _linguetta.show(out);
    
  %>

  <af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="controlloDate() && controllaCampi()">
	  
	  <input type="hidden" name="datInizioCompare" value="<%=datInizio%>"/>
	  <input type="hidden" name="PAGE" value="CigLavListaPage">
      <input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>"%>
      <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>
      <input type="hidden" name="strCodFiscale" value="<%=strCodFiscale%>"/>
      <input type="hidden" name="prgAzienda" value="<%=prgAzienda%>"/>
      <input type="hidden" name="prgUnita" value="<%=prgUnita%>"/>
      <input type="hidden" name="prgaccordo" value="<%=prgaccordo%>"/>
      <input type="hidden" name="codComCompetenza" value="<%=codComCompetenza%>"/>
      <input type="hidden" name="LISTA" value="1"/>
      <input type="hidden" name="configurazione" value="<%=configurazione %>"/>
      
      
      <% if (!nuovo) {%>
         <input type="hidden" name="prgAltraIscr" value="<%=prgAltraIscr%>">   
      <% } %>
       
      <center>
       
        <%if (configurazione.equals("0") || configurazione.equals("2")) {%>
        	<af:showMessages prefix="M_DeleteIscrCig"/>
            <af:showMessages prefix="M_InsertDomandaCig"/>
	        <af:showMessages prefix="M_UpdateIscrCIG"/>
	    <%} else {%>
            <af:showMessages prefix="M_InsertAltraIscrizione"/>
	        <af:showMessages prefix="M_UpdateAltraIscrizione"/>	    
	    <%} %>
        <af:showErrors/>
      </center>  
      <div align="center">
	  <%if (configurazione.equals("0") || configurazione.equals("2")) {
	  %>
      <af:list moduleName="M_ListaLavDomandeCig" skipNavigationButton="0"
               canDelete="<%=canDelete ? \"1\" : \"0\"%>"  
               jsSelect="dettaglio" jsDelete="cancella"/> 
	  <%}
	  else {%>
	  <af:list moduleName="M_ListaAltreIscrizioni" skipNavigationButton="0" />
	  <%}
      if(canInsert) {%>
      		<input type="button" class="pulsanti" onClick="prevalDataInizio();apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>');" value="Nuova Iscrizione"/>        
      <%}%>
      
     
	</div>
  <%
  String divStreamTop = StyleUtils.roundLayerTop(canModify || canInsert);
  String divStreamBottom = StyleUtils.roundLayerBottom(canModify || canInsert);  
  %>

      <div id="divLayerDett" name="divLayerDett" class="t_layerDett"
       style="position:absolute; width:90%; left:30; top:150px; z-index:6; display:<%=apriDiv%>;">

    <%out.print(divStreamTop);%>
     <table width="100%">
     	<tr>
        	<td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
            <td height="16" class="azzurro_bianco">
            	<%if(nuovo){%>
					Nuova Iscrizione
            	<%}else{%>
            		Dettaglio Iscrizione
            	<%}%>	   
            </td>
            <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
        </tr>
      </table>
	  
	  <div class='sezione2' id='SedeAzienda'>
	  	<img id='tendinaAzienda' alt="Chiudi" src="../../img/aperto.gif" onclick='cambia(this, document.getElementById("aziendaSez"));'/>&nbsp;&nbsp;&nbsp;Azienda&nbsp;&nbsp;
      		<% if(!fieldReadOnly.equalsIgnoreCase("true")) {%> 
      		<a href="#" onClick="javascript:apriSelezionaSoggetto('Aziende', 'aggiornaAzienda','','','');"><img src="../../img/binocolo.gif" alt="Cerca"></a>
      		&nbsp;<a href="#" onClick="javascript:azzeraAzienda();"><img src="../../img/del.gif" alt="Azzera selezione"></a>
      		<% } %> 
      	</div>
	 
	 <div id="aziendaSez" style="display:none">
     	<table class="main">
     		<tr>
            	<td class="etichetta" nowrap>Codice fiscale</td>
                <td class="campo">
                	<af:textBox classNameBase="input" type="text" name="codiceFiscale" readonly="true" value="<%=strCodFiscale%>" size="50" maxlength="16"/>
                </td>
            </tr>
        	<tr>
            	<td class="etichetta" nowrap>Ragione Sociale</td>
                <td class="campo">
                	<af:textBox classNameBase="input" type="text" name="ragioneSociale" readonly="true" value="<%=ragioneSociale%>" size="50" maxlength="100"/>
                </td>
            </tr>
            <tr>
            	<td class="etichetta">Indirizzo</td>
                <td class="campo">
                	<af:textBox classNameBase="input" type="text" name="indirizzo" readonly="true" value="<%=indirizzo%>" size="40" maxlength="100"/>
                </td>
                	<td class="etichetta">Comune</td>
                    <td class="campo">
                    	<af:textBox classNameBase="input" type="text" name="comune" readonly="true" value="<%=comune%>" size="30" maxlength="100"/>
                    </td>
             </tr>
        </table>
     </div>
     <table class="main" width="100%">  	
     	<tr>
     	<!--Campo prevalorizzato per la regione VDA alla data odierna-->
     	<td class="etichetta">Data Inizio</td>
        	<td class="campo">
        	    
        		<af:textBox onKeyUp="fieldChanged();" title="Data inizio iscrizione" classNameBase="input" 
                			readonly="<%=fieldReadOnlyDatInizioFine%>" required="true" 
                			onBlur="controlloDate();dataInizioPlus365();" type="date"                			
                    		name="datInizio" validateOnPost="true" callBackDateFunction="callBackDateControl();" 
                    		value="<%=datInizio%>" 
                    		size="12" maxlength="10"/>
        	</td>
		</tr>
        <tr>
       		<td class="etichetta">Data Fine</td>
        	<td class="campo">
        		<af:textBox onKeyUp="fieldChanged();" title="Data fine" classNameBase="input" 
                			readonly="<%=fieldReadOnlyDatInizioFine%>" required = "true" type="date"
                    		name="datFine" validateOnPost="true" value="<%=datFine%>" 
                    		size="12" maxlength="10"/>
        	</td>
		</tr>
		<tr>
			<td class="etichetta">Tipo Iscrizione</td>
            <td class="campo">
            	<% if (configurazione.equalsIgnoreCase("2") && nuovo) { %>                        	
            		<af:comboBox multiple="true" disabled="<%=fieldReadOnly%>"  name="codtipoiscr" classNameBase="input" 
	                    		 moduleName="CI_TIPO_ISCR" addBlank="true" required="true" title="Tipo Iscrizione"
	                        	 onChange="fieldChanged();gestisciMotChiusura('Combo');"/>
            	<% } else { %>
	            	<af:comboBox disabled="<%=fieldReadOnly%>"  name="codtipoiscr" classNameBase="input" 
	                    		 moduleName="CI_TIPO_ISCR" selectedValue="<%= codtipoiscr %>"
	                        	 addBlank="true" required="true" title="Tipo Iscrizione"
	                        	 onChange="fieldChanged();gestisciMotChiusura('Combo');" />
                <% } %>        	 
            </td>
        </tr>
        <% if ((prgaccordo != null) && (!"".equals(prgaccordo))) { %>
        <tr>
			<td class="etichetta">Codice domanda</td>
            <td class="campo">
            	<af:textBox classNameBase="input" type="text" name="codcig" readonly="true" value="<%=codAccordo%>" size="30" maxlength="100"/>                   
            </td>
        </tr>
        <%} %>
        <tr>
            <td class="etichetta">Data competenza</td>
            <td class="campo">
                <af:textBox onKeyUp="fieldChanged();" title="Data competenza" classNameBase="input" 
                			readonly="<%=fleldsLegatiAccordo%>"  type="date"
                    		name="datCompetenza" validateOnPost="true" value="<%=datCompetenza%>" 
                    		size="12" maxlength="10"/>
            </td>
        </tr>
        <tr>
            <td class="etichetta">Data chiusura iscr.</td>
            <td class="campo">
                <af:textBox onKeyUp="fieldChanged();" title="Data chiusura iscr." classNameBase="input" 
                			readonly="<%=fleldsLegatiAccordo%>"  type="date"
                    		name="datChiusuraIscr"  validateOnPost="true"
                    		value="<%=datChiusuraIscr%>" size="12" maxlength="10"/>
            </td>
          </tr>
          <tr>
        	<td class="etichetta">Motivo chiusura iscr.</td>
            <td class="campo">
            	<%if (fleldsLegatiAccordo.equals("true")) {%>			
					<af:textBox classNameBase="input" size="50" name="codMotChiusuraIscr" readonly="true" title="Motivo chiusura iscr." value="<%=motChiusura%>"/>
				<%} else {%> 
            		<af:comboBox classNameBase="input" name="codMotChiusuraIscr" title="Motivo chiusura iscr." onChange="fieldChanged()" addBlank="true"/>            
          		<%}%> 
          	</td>
          </tr>
          <tr>
	    	<td class="etichetta">Note</td>
	 		<td class="campo">			
	 			<af:textArea classNameBase="input"  name="strNote" 
	 						 value="<%=strNote%>" cols="60" rows="4" maxlength="2000"
                    		 onKeyUp="fieldChanged();" readonly="<%=fleldsLegatiAccordo%>"  />
	    	</td>			
		  </tr>
		  <tr>
			<td class="etichetta">Stato</td>
            <td class="campo">
            	<div id="StatoNoFlag" style="display:<%=displayStatoNoFlag%>">
    				<af:comboBox name="codStatoNoFlag" moduleName="CI_STATO_ALTRA_ISCR_NO_FLAG" 
    							 selectedValue="<%=codStato%>" classNameBase="input" 
    							 title="Stato" onChange="fieldChanged();settaStato();"
    							 addBlank="true" disabled="<%=fleldsChangeStato%>"/>
    			</div>
    			<div id="StatoFlag" style="display:<%=displayStatoFlag%>">
    				<af:comboBox name="codStatoFlag" moduleName="CI_STATO_ALTRA_ISCR_FLAG" 
    							 selectedValue="<%=codStato%>" classNameBase="input" 
    							 title="Stato" 
    							 addBlank="true" disabled="<%=fleldsChangeStato%>"/>
    			</div>
    			<input type="hidden" name="codStato" title="Stato" value="<%=codStato%>" />
            </td>
         </tr>
		  <tr>
		  	<td class="etichetta">Tipo di Competenza</td>
   		    <td class="campo">		
				<af:comboBox classNameBase="input" name="codMonoTipoCompetenza" selectedValue="<%=codMonoTipoCompetenza%>" title="Tipo di Competenza" onChange="fieldChanged()" disabled="true">
					<option value="M" <% if (codMonoTipoCompetenza.equals("M")) {%>selected="true" <%}%>>Inserimento manuale</option>
					<option value="A" <% if (codMonoTipoCompetenza.equals("A")) {%>selected="true" <%}%>>Competenza amministrativa</option>
					<option value="D" <% if (codMonoTipoCompetenza.equals("D")) {%>selected="true" <%}%>>Competenza sul domicilio indicato nella domanda</option>
					<option value="R" <% if (codMonoTipoCompetenza.equals("R")) {%>selected="true" <%}%>>Competenza sulla residenza indicata nella domanda</option>
					<option value="S" <% if (codMonoTipoCompetenza.equals("S")) {%>selected="true" <%}%>>Competenza in base al comune della sede</option>
				</af:comboBox>&nbsp;
		  	</td>
		 </tr>
		 
		 <%if ((configurazione.equalsIgnoreCase("0") || configurazione.equals("2")) && codtipoiscr.equalsIgnoreCase("M")) {%>
        	<tr>
				<td class="etichetta">Data licenziamento</td>
            	<td class="campo">
            		<af:textBox onKeyUp="fieldChanged();" title="Data licenziamento" classNameBase="input" 
                			readonly="<%=fieldReadOnlyInfoMobDeroga%>" type="date"
                    		name="datLicenziamento" validateOnPost="true"
                    		value="<%=dataLicenziamento%>" size="12" maxlength="10"/>                   
            	</td>
        	</tr>
        	<tr>
             	<td class="etichetta">Diritto alla disoccupazione ordinaria</td>
             	<td class="campo">
               	<af:comboBox classNameBase="input" name="flgDirittoDO" disabled="<%=fieldReadOnlyInfoMobDeroga%>">
                 	<option value="" <% if (flgDirittoDo.equals("")) {%>selected="true" <%}%>></option>
                 	<option value="S" <% if (flgDirittoDo.equals("S")) {%>selected="true" <%}%>>Si</option>
                 	<option value="N" <% if (flgDirittoDo.equals("N")) {%>selected="true" <%}%>>No</option>              
               	</af:comboBox>                
             	</td>
             	</tr>
       		<tr>
				<td class="etichetta">Motivazione mancato diritto alla D.O.</td>
	            <td class="campo">
	            	<af:comboBox disabled="<%=fieldReadOnlyInfoMobDeroga%>" name="codMotivoNotDO" classNameBase="input" 
	                    		 moduleName="CI_MOTIVO_NOT_DO" selectedValue="<%= codMotivoNoDo %>"
	                        	 addBlank="true" title="Motivazione mancato diritto alla D.O."
	                        	 onChange="fieldChanged();" />
	            </td>
	        </tr>
		 	<%} else {%>
		 		<input type="hidden" name="datLicenziamento" value=""/>
		 		<input type="hidden" name="flgDirittoDO" value=""/>
		 		<input type="hidden" name="codMotivoNotDO" value=""/>
		 		<input type="hidden" name="setDateInizioFine" value="false"/>
		 	<%}%> 
         <tr><td colspan="2"></td></tr>
        </table>  
		&nbsp;&nbsp;&nbsp;&nbsp;
		<table> 
	        <%if(nuovo) {%>
			<tr>
	        	<td colspan="2" align="center">
	            	<%if(canInsert) {%>
		            	<input type="submit" class="pulsanti" name="inserisci" value="Inserisci">
		            	<input type="hidden" name="inserisci" value="1"/>
	            	<%}%>
	            	<input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="ChiudiDivLayer('divLayerDett')">
	            </td>
			</tr>
	        <%} else if(!nuovo) {%>
			<tr>
	            <td colspan="2" align="center">
	            	<% if (canModify) { %>
	                	<input type="submit" class="pulsanti" name="salva" value="Aggiorna">
	                	<input type="hidden" name="aggiorna" value="1"/>
	              	<% } %>
	                <input type="button" class="pulsanti" name="annulla" value="<%= canModify ? "Chiudi senza aggiornare" : "Chiudi" %>" 
	                  onClick="ChiudiDivLayer('divLayerDett')">
	            </td>
			</tr>
			<%	if(!prgaccordo.equals("")) {
					if(canPrint) { %> 
						<tr>
				  			<td colspan="2" align="center">
				  				<input type="button" class="pulsanti" value="Visualizza Domanda" onClick="Stampa()"> 
				  			</td>
						</tr>
					<%} %>
					<tr>
			  			<td colspan="2" align="center">
			  				<input type="button" name="pdfAccSin" class="pulsanti" value="Visualizza Accordo" onClick="VisualizzaPdfAccordoSindacale()">
			  		 	</td>
					</tr>  	
			<%	} %>	
			<tr><td colspan="2"></td></tr> 
			<tr>
			  	<td colspan="2" align="center">
					<%if (operatoreInfo!=null) operatoreInfo.showHTML(out);%>
				</td>
			</tr>
	        <%}%>
	    </table>
	      
	 	<%out.print(divStreamBottom);%>              
    </af:form>
    <script language="javascript">
  	
  	var imgV = document.getElementById("tendinaAzienda");
  		<% if (!strCodFiscale.equals("")){%>
    		cambiaLavMC("aziendaSez","");
    		imgV.src = imgAperta;
    		imgV.alt="Chiudi";
  		<%} else {%>
  			cambiaLavMC("aziendaSez","none");
  			imgV.src = imgChiusa;
    		imgV.alt="Apri";
    	<%}%>
	
	</script> 
  </body>
</html>

