<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  it.eng.sil.security.User,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%
  String configDiagnFunz = serviceResponse.containsAttribute("M_GetConfigDiagnosiFunz.ROWS.ROW.NUM")?serviceResponse.getAttribute("M_GetConfigDiagnosiFunz.ROWS.ROW.NUM").toString():"0";
  String labelDataRedazDiagn = "Data redazione diagnosi";
  boolean labelMentale = false;
  if (configDiagnFunz.equals("1")) {
	 labelDataRedazDiagn = "Data redazione diagnosi/profilo";  
  }else if (configDiagnFunz.equals("2")) {//Siamo in Valle d'Aosta, enjoy skiing!
	 labelDataRedazDiagn = "Data accertamento capacità lav.";
	 labelMentale=true;
  }
  
  int _funzione=0;
  InfCorrentiLav infCorrentiLav= null;

  BigDecimal cdnUtIns = null;
  String dtmIns = null;
  BigDecimal cdnUtMod = null;
  String dtmMod = null;
  Testata operatoreInfo = null;
  String numklodiagnosifunzionale=null;

  String  prgDiagnosiFunzionale = null;
  String  cdnLavoratore = null;
		
  String  CODAZIENDAASL = null;
  String  FLGREVACCERTAMENTO = null;
  String  DATREVISIONEACCERT = null;
  String  FLGINVALIDFISICA = null;
  String  FLGINVALIDPSICHICA = null;
  String  FLGINVALIDNONDETERMINATA = null;
  String  FLGINVALIDINTELLETTIVA = null;
  String  FLGINVALIDSENSORIALE = null;
  String  STRGIUDIZIODIAGNOSTICO = null;
  String  CODGIUDIZIO = null;
  String  DATRICONOSCIMENTO = null;
  String  FLGPERMANENZASTATO = null;
  String  FLGSTATOGRAVITA104 = null;
  String  DATDIAGNOSI = null;
  String  DATFINE = null;
  String  DATRICHREVDIAGNOSI = null;
  String  FLGREVDIAGNOSIPERVENUTA = null;
  String  DATARRIVOREVDIAGNOSI = null;
  String  descrASL = null;
  String  descrGiudizioML = null;
  String  parametro = null;
  String  numPercInval = null;
  
  boolean datFineVal = false;

  parametro = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"parametro");
  
  boolean inserisciNuovo = !(serviceRequest.getAttribute("inserisciNuovo")==null || 
                            ((String)serviceRequest.getAttribute("inserisciNuovo")).length()==0);
                            
  boolean inserisciNew = !(serviceRequest.getAttribute("inserisciNew")==null || 
                            ((String)serviceRequest.getAttribute("inserisciNew")).length()==0);
    
  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page); 

  String cercaASL = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cercaASL");
  
  String aggiornamento = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"aggiornamento");
  String showIscrizioneUpdate = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"showIscrizioneUpdate");
  
  //String fromLinguette = (String) RequestContainer.getRequestContainer().getSessionContainer().getAttribute("fromLinguette");
  
  //Profilatura ------------------------------------------------
  //PageAttribs attributi = new PageAttribs(user, "ListaTabDecodPage");
  //String _page = (String) serviceRequest.getAttribute("PAGE");
  PageAttribs attributi = new PageAttribs(user, _page); 
  boolean canPrint= attributi.containsButton("STAMPA");
  boolean canModify= attributi.containsButton("aggiorna");
  boolean canDelete= attributi.containsButton("RIMUOVI");
  boolean canInsert= attributi.containsButton("INSERISCI");
  //boolean canDocAssociati = attributi.containsButton("DOC_ASS");
  
  boolean readOnlyStr = !canModify;
  String fieldReadOnly = canModify?"false":"true"; 
  
  
  cdnLavoratore = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
  
  String cdnLavoratoreDecrypt = EncryptDecryptUtils.decrypt(cdnLavoratore);

  prgDiagnosiFunzionale = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgDiagnosiFunzionale");

  //String goBackListPage = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "goBackListPage");
  
  /*--------------------------------------------------------------------------------------------------------------*/    


  if(inserisciNuovo){ 
	  prgDiagnosiFunzionale = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgDiagnosiFunzionale");
	  //cdnLavoratore = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
	
	  CODAZIENDAASL = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"CODAZIENDAASL");  
	  FLGREVACCERTAMENTO = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"FLGREVACCERTAMENTO");
	  DATREVISIONEACCERT = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"DATREVISIONEACCERT"); 
	  FLGINVALIDFISICA = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"FLGINVALIDFISICA");  
	  FLGINVALIDPSICHICA = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"FLGINVALIDPSICHICA");
	  FLGINVALIDNONDETERMINATA = Utils.notNull(serviceRequest.getAttribute("FLGINVALIDNONDETERMINATA"));
	  FLGINVALIDINTELLETTIVA = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"FLGINVALIDINTELLETTIVA");  
	  FLGINVALIDSENSORIALE = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"FLGINVALIDSENSORIALE");  
	  STRGIUDIZIODIAGNOSTICO = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"STRGIUDIZIODIAGNOSTICO");  
	  CODGIUDIZIO = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"CODGIUDIZIO");
	  DATRICONOSCIMENTO = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"DATRICONOSCIMENTO");  
	  FLGPERMANENZASTATO = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"FLGPERMANENZASTATO"); 
	  FLGSTATOGRAVITA104 = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"FLGSTATOGRAVITA104");  
	  DATDIAGNOSI = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"DATDIAGNOSI"); 
	  DATFINE = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"DATFINE");  
	  DATRICHREVDIAGNOSI = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"DATRICHREVDIAGNOSI");  
	  FLGREVDIAGNOSIPERVENUTA = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"FLGREVDIAGNOSIPERVENUTA");  
	  DATARRIVOREVDIAGNOSI = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"DATARRIVOREVDIAGNOSI");
	  descrASL = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"descrASL");
	  numPercInval = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"numPercInval");

	  cdnUtIns = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
	  cdnUtMod = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer().getAttribute("_CDUT_");

  }else if(parametro.equals("")){

      SourceBean row = null;
      Vector rows = serviceResponse.getAttributeAsVector("M_Load_Diagnosi_Funz.ROWS.ROW");
      // siamo in dettaglio per cui avro' al massimo una riga
      if (rows.size()==1) {
	        row = (SourceBean)rows.get(0);

		    prgDiagnosiFunzionale = Utils.notNull(row.getAttribute("prgDiagnosiFunzionale"));
		    //cdnLavoratore = Utils.notNull(row.getAttribute("cdnLavoratore"));
		
		    CODAZIENDAASL = Utils.notNull(row.getAttribute("CODAZIENDAASL"));  
		    FLGREVACCERTAMENTO = Utils.notNull(row.getAttribute("FLGREVACCERTAMENTO"));
		    DATREVISIONEACCERT = Utils.notNull(row.getAttribute("DATREVISIONEACCERT")); 
		    FLGINVALIDFISICA = Utils.notNull(row.getAttribute("FLGINVALIDFISICA"));  
		    FLGINVALIDPSICHICA = Utils.notNull(row.getAttribute("FLGINVALIDPSICHICA"));
		    FLGINVALIDNONDETERMINATA = Utils.notNull(row.getAttribute("FLGINVALIDNONDETERMINATA"));
		    FLGINVALIDINTELLETTIVA = Utils.notNull(row.getAttribute("FLGINVALIDINTELLETTIVA"));  
		    FLGINVALIDSENSORIALE = Utils.notNull(row.getAttribute("FLGINVALIDSENSORIALE"));  
		    STRGIUDIZIODIAGNOSTICO = Utils.notNull(row.getAttribute("STRGIUDIZIODIAGNOSTICO"));  
		    CODGIUDIZIO = Utils.notNull(row.getAttribute("CODGIUDIZIO"));
		    DATRICONOSCIMENTO = Utils.notNull(row.getAttribute("DATRICONOSCIMENTO"));  
		    FLGPERMANENZASTATO = Utils.notNull(row.getAttribute("FLGPERMANENZASTATO"));
		    FLGSTATOGRAVITA104 = Utils.notNull(row.getAttribute("FLGSTATOGRAVITA104"));
		    DATDIAGNOSI = Utils.notNull(row.getAttribute("DATDIAGNOSI"));  
		    DATFINE = Utils.notNull(row.getAttribute("DATFINE"));
		    if(!DATFINE.equals("")) {datFineVal = true;}
		    DATRICHREVDIAGNOSI = Utils.notNull(row.getAttribute("DATRICHREVDIAGNOSI"));  
		    FLGREVDIAGNOSIPERVENUTA = Utils.notNull(row.getAttribute("FLGREVDIAGNOSIPERVENUTA"));  
		    DATARRIVOREVDIAGNOSI = Utils.notNull(row.getAttribute("DATARRIVOREVDIAGNOSI"));
		    descrASL = Utils.notNull(row.getAttribute("descrASL"));
		    descrGiudizioML = Utils.notNull(row.getAttribute("descrGiudizioML"));
	        numPercInval = Utils.notNull(row.getAttribute("numPercInval"));
	        
	        // aggiornamento del lock ottimistico in aggiornamento    
	        numklodiagnosifunzionale = String.valueOf(((BigDecimal)row.getAttribute("NUMKLODIAGNOSIFUNZIONALE")).intValue());
	        cdnUtIns = (BigDecimal) row.getAttribute("CDNUTINS");
	        dtmIns = (String) row.getAttribute("DTMINS");
	        cdnUtMod = (BigDecimal) row.getAttribute("CDNUTMOD");
	        dtmMod = (String) row.getAttribute("DTMMOD");
	        operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);        
  	  }  
	    	  
  }else if(!cercaASL.equals("") || !parametro.equals("")){
      SourceBean row = null;
      Vector rows= serviceResponse.getAttributeAsVector("M_Load_Diagnosi_Funz_linguette.ROWS.ROW");
      // siamo in dettaglio per cui avro' al massimo una riga
      if (rows.size()==1) {
	        row = (SourceBean)rows.get(0);

		    prgDiagnosiFunzionale = Utils.notNull(row.getAttribute("prgDiagnosiFunzionale"));
		    //cdnLavoratore = Utils.notNull(row.getAttribute("cdnLavoratore"));
		
		    CODAZIENDAASL = Utils.notNull(row.getAttribute("CODAZIENDAASL"));  
		    FLGREVACCERTAMENTO = Utils.notNull(row.getAttribute("FLGREVACCERTAMENTO"));
		    DATREVISIONEACCERT = Utils.notNull(row.getAttribute("DATREVISIONEACCERT")); 
		    FLGINVALIDFISICA = Utils.notNull(row.getAttribute("FLGINVALIDFISICA"));  
		    FLGINVALIDPSICHICA = Utils.notNull(row.getAttribute("FLGINVALIDPSICHICA"));
		    FLGINVALIDNONDETERMINATA = Utils.notNull(row.getAttribute("FLGINVALIDNONDETERMINATA"));
		    FLGINVALIDINTELLETTIVA = Utils.notNull(row.getAttribute("FLGINVALIDINTELLETTIVA"));  
		    FLGINVALIDSENSORIALE = Utils.notNull(row.getAttribute("FLGINVALIDSENSORIALE"));  
		    STRGIUDIZIODIAGNOSTICO = Utils.notNull(row.getAttribute("STRGIUDIZIODIAGNOSTICO"));  
		    CODGIUDIZIO = Utils.notNull(row.getAttribute("CODGIUDIZIO"));
		    DATRICONOSCIMENTO = Utils.notNull(row.getAttribute("DATRICONOSCIMENTO"));  
		    FLGPERMANENZASTATO = Utils.notNull(row.getAttribute("FLGPERMANENZASTATO")); 
		    FLGSTATOGRAVITA104 = Utils.notNull(row.getAttribute("FLGSTATOGRAVITA104")); 
		    DATDIAGNOSI = Utils.notNull(row.getAttribute("DATDIAGNOSI"));  
		    DATFINE = Utils.notNull(row.getAttribute("DATFINE"));  
		    DATRICHREVDIAGNOSI = Utils.notNull(row.getAttribute("DATRICHREVDIAGNOSI"));  
		    FLGREVDIAGNOSIPERVENUTA = Utils.notNull(row.getAttribute("FLGREVDIAGNOSIPERVENUTA"));  
		    DATARRIVOREVDIAGNOSI = Utils.notNull(row.getAttribute("DATARRIVOREVDIAGNOSI"));
		    descrASL = Utils.notNull(row.getAttribute("descrASL"));
		    descrGiudizioML = Utils.notNull(row.getAttribute("descrGiudizioML"));
	        numPercInval = Utils.notNull(row.getAttribute("numPercInval"));
	        
	        // aggiornamento del lock ottimistico in aggiornamento    
	        numklodiagnosifunzionale = String.valueOf(((BigDecimal)row.getAttribute("NUMKLODIAGNOSIFUNZIONALE")).intValue());
	        cdnUtIns = (BigDecimal) row.getAttribute("CDNUTINS");
	        dtmIns = (String) row.getAttribute("DTMINS");
	        cdnUtMod = (BigDecimal) row.getAttribute("CDNUTMOD");
	        dtmMod = (String) row.getAttribute("DTMMOD");
	        operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);        
  	  }  
	    	  
  }

  /*------------------------ inserire il parametro cdnFunzione ---------------------------*/
  _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  
  /*------------------------ metto in sessione prgDiafnosiFunzionale ---------------------------*/
  RequestContainer.getRequestContainer().getSessionContainer().setAttribute("prgDiagnosiFunzionale", prgDiagnosiFunzionale.toString());
  
  //Servono per gestire il layout grafico
  String htmlStreamTop = StyleUtils.roundTopTable(true);
  String htmlStreamBottom = StyleUtils.roundBottomTable(true);
  
%>

<html>

<head>
    <title>Diagnosi Funzionale</title>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>   
    <af:linkScript path="../../js/"/>
    <SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT> 

	<script language="Javascript">
	<!--Contiene il javascript che si occupa di aggiornare i link del footer-->
	  <% 
	       //Genera il Javascript che si occuperà di inserire i links nel footer
	       attributi.showHyperLinks(out, requestContainer,responseContainer,"cdnLavoratore=" + cdnLavoratoreDecrypt);
	  %>
	//se arrivo in seguito ad un aggiornamento mostro una popup per l'aggiornamento dell'iscrizione alla L68
  
  function onLoadOperation() {
  	<%
	  String errori = (String) serviceResponse.getAttribute("INSERISCIDIAGNOSIFUNZ.ERROR");
	  if (showIscrizioneUpdate != null && "1".equals(showIscrizioneUpdate) && !"TRUE".equals(errori)) {%>
	  		var url = "AdapterHTTP?PAGE=CMStoricoModIscrL68Page";		
				url += "&CONTEXT=DIAGNOSI";
				url += "&CDNFUNZIONE=<%=_funzione%>";
				url += "&cdnLavoratore=<%=cdnLavoratore%>";
				url += "&NUMPERCINVALIDITA=<%=numPercInval%>";
				var w = 650;
				var h = 350;
				var left = (screen.width/2)-(w/2);
				var top = (screen.height/2)-(h/2);			
				window.open(url, '_blank', 'toolbar=no,statusbar=yes,width='+w+',height='+h+',top='+top+',left='+left+',scrollbars=yes,resizable=yes');
	<%} %>//recion
	}
	</script>
	
	
	

	<%-- include lo script che permette di aprire la PopUp che gestisce i documenti (salvataggio/protocollazione) --%>
	<% String queryString = "cdnLavoratore="+cdnLavoratore+"&prgDiagnosiFunzionale="+prgDiagnosiFunzionale+"&PAGE=CMDiagnosiMinorPage"+"&cdnFunzione="+_funzione; %>
	<%@ include file="../documenti/_apriGestioneDoc.inc"%>
		
	<script language="JavaScript">
          var configurazioneDiagnosi = "<%=configDiagnFunz%>";
          var flagChanged = false;
        
          function fieldChanged() {
           <% if (!readOnlyStr){ %> 
              flagChanged = true;
           <%}%> 
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

		  
		  //Cerca azienda ASL competente
		  function cercaASLCompetente(criterio){
			  var f;
 		      f = "AdapterHTTP?PAGE=CMDiagnosiMinorPage&cercaASL=1";
 		      f = f + "&CDNFUNZIONE=<%=_funzione%>";
 		      f = f + "&prgDiagnosiFunzionale=<%=prgDiagnosiFunzionale%>";
 		      f = f + "&cdnLavoratore=<%=cdnLavoratore%>";
			  f = f + "&CRITERIO=" + criterio;
			  f = f + "&cod=" + document.Frm1.CODAZIENDAASL.value;
			  f = f + "&descr=" + document.Frm1.descrASL.value;
			  
			  var t = "_blank";
			  var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=500,height=400,top=100,left=100";
			  if(criterio=="codice" && document.Frm1.CODAZIENDAASL.value!="")
			  	window.open(f, t, feat);
			  if(criterio=="descrizione" && document.Frm1.descrASL.value!="")
			  	window.open(f, t, feat);
		  }
		   
		  function AggiornaForm (codice, descrizione) {
			  window.opener.document.Frm1.CODAZIENDAASL.value = codice;
			  window.opener.document.Frm1.descrASL.value = descrizione;
			  window.close();
		  }

		  //FLGREVACCERTAMENTO
		  function cfIsChecked(){
		      var flg = eval(document.forms[0].FLGREVACCERTAMENTO);
			  //alert("Is check CF? "+flg.value);
			  if((flg.value!=null) && (flg.value=="S"))
			  { nascondi("CF_NOcheck");
			  }
			  else
			  { mostra("CF_NOcheck");
			  }
		  }
		  function mostra(id)
		  {   
		  	  var div = document.getElementById(id);
			  div.style.display="";
		  }
		  
		  function nascondi(id)
		  {   var div = document.getElementById(id);
			  div.style.display="none";
		  }		  


		  function setFlags() {
		  		  
			  if(document.Frm1.FLGINVALIDFISICA.checked) document.Frm1.FLGINVALIDFISICA.value = "S";
			  else document.Frm1.FLGINVALIDFISICA.value = "N";

			  if(document.Frm1.FLGINVALIDPSICHICA.checked) document.Frm1.FLGINVALIDPSICHICA.value = "S";
			  else document.Frm1.FLGINVALIDPSICHICA.value = "N";
			  
			  if (configurazioneDiagnosi == "1") {
				  if(document.Frm1.FLGINVALIDNONDETERMINATA.checked) document.Frm1.FLGINVALIDNONDETERMINATA.value = "S";
				  else document.Frm1.FLGINVALIDNONDETERMINATA.value = "N";
			  }
			  else {
				  if(document.Frm1.FLGINVALIDINTELLETTIVA.checked) document.Frm1.FLGINVALIDINTELLETTIVA.value = "S";
				  else document.Frm1.FLGINVALIDINTELLETTIVA.value = "N";
					
				  if(document.Frm1.FLGINVALIDSENSORIALE.checked) document.Frm1.FLGINVALIDSENSORIALE.value = "S";
				  else document.Frm1.FLGINVALIDSENSORIALE.value = "N";
			  }
			  checkIfIscrizioneToChange();
			
		  }

		  var urlpage="AdapterHTTP?";	

		  function getURLPageBase() {    
		    urlpage+="CDNFUNZIONE=<%=_funzione%>&";
		    urlpage+="CDNLAVORATORE=<%=cdnLavoratore%>&";
		    return urlpage;
 		  }
		  
		  function aggiornaDiagnosiFunz(){		  	  		  	  
		  	  if(!flagChanged){
					alert("Nessuna modifica effettuata");
					return false;
		  	  }
		  	  else{
				  setFlags();				  
				  return true;	 
		  	  }
		  }

		  //controlla se sono stati aggiornati i valori di percentuale invalidità, tipo invalidità o tipo accertamento.
		function checkIfIscrizioneToChange() {
			if ((document.Frm1.numPercInval.value != document.Frm1.numPercInvalOld.value) ||
				(document.Frm1.CODGIUDIZIO.value != document.Frm1.codGiudizioOld.value))
			document.Frm1.showIscrizioneUpdate.value = "1";		
		}
		  
		  function abilitaDataRevisione(){
		  	if(document.Frm1.FLGREVACCERTAMENTO.value == "S"){
				  document.Frm1.DATREVISIONEACCERT.disabled = false;
			}
			else{
				  document.Frm1.DATREVISIONEACCERT.value = "";
				  document.Frm1.DATREVISIONEACCERT.disabled = true;
			}
		  }

		  function abilitaDataRevPerv(){
		  	if(document.Frm1.FLGREVDIAGNOSIPERVENUTA.value == "S"){
				  document.Frm1.DATARRIVOREVDIAGNOSI.disabled = false;
			}
			else{
				  document.Frm1.DATARRIVOREVDIAGNOSI.value = "";
				  document.Frm1.DATARRIVOREVDIAGNOSI.disabled = true;
			}
		  }
		  
		  function controlloReset(){
		  	<%if ( "S".equalsIgnoreCase(FLGREVACCERTAMENTO) ) {%>
				  document.Frm1.DATREVISIONEACCERT.disabled = false;
			<%}else{%>
				  document.Frm1.DATREVISIONEACCERT.disabled = true;
			<%}
		  	  if ( "S".equalsIgnoreCase(FLGREVDIAGNOSIPERVENUTA) ) {%>
				  document.Frm1.DATARRIVOREVDIAGNOSI.disabled = false;
			<%}else{%>
				  document.Frm1.DATARRIVOREVDIAGNOSI.disabled = true;
			<%}%>
		  }

		  function checkDateWithNowDate (strdata) {
		
			annoVar = strdata.substr(6,4);
			meseVar = strdata.substr(3,2);
			giornoVar = strdata.substr(0,2);
			dataVarInt = parseInt(annoVar + meseVar + giornoVar, 10);
			<% Calendar oggi = Calendar.getInstance();
			   String giornoDB = Integer.toString(oggi.get(5));
			   if (giornoDB.length() == 1){
					giornoDB = '0' + giornoDB;
				}
				
				String meseDB = Integer.toString(oggi.get(2)+1);
				if (meseDB.length() == 1){
					meseDB = '0' + meseDB;
				}
				
				String annoDB = Integer.toString(oggi.get(1));
				String dataOdierna = annoDB + meseDB + giornoDB;
			%> 
			
			varOggi = document.Frm1.dataOdierna.value;
			annoVarOggi = varOggi.substr(9,2);
			meseVarOggi = varOggi.substr(6,2);
			
			if (dataVarInt < varOggi) {
				return 1;
			}
			else {
				if (dataVarInt > varOggi) {
					return 2;
				}else {
					return 0;
				}
		    }
		  }

		  
		  function controllaDati(){
		 	
		 	var data = checkDateWithNowDate(document.Frm1.DATDIAGNOSI.value);
		  	  if((parseInt(document.Frm1.numPercInval.value) > 100 || isNaN(parseInt(document.Frm1.numPercInval.value))) && document.Frm1.numPercInval.value!=""){
					alert("La percentuale del giudizio medico legale deve essere un intero minore o uguale a 100");
					return false;
	  	  	  }else if(data == 2){
	  	  	  		alert("La data di redazione diagnosi deve essere inferiore o uguale alla data di oggi");
	  	  	  		return false;
	  	  	  }else 
	  	  	  		return true;
		  }
		  
		  function controllaDate() {
	
				ok=true;
				var dataInizio = document.Frm1.DATDIAGNOSI.value;
				var dataFine =  document.Frm1.DATFINE.value;
				if (dataInizio != "" && dataFine != ""){
  					if (compareDate(dataInizio, dataFine)>0) {
						alert("Data redazione diagnosi maggiore della Data fine diagnosi");
						ok=false;
					}
				}	
				return ok;
   		  }
   			
   		  function controllaDataFine (){
   			<% if (datFineVal == true) {
   				SourceBean rowCount = null;
	    			rowCount = (SourceBean) serviceResponse.getAttribute("M_CountDataFineCrypt.ROWS.ROW");
  					int conta = Integer.valueOf(""+rowCount.getAttribute("conta")).intValue();
  					if(conta > 0){ %>
  						if (document.Frm1.DATFINE.value == ""){
							alert("Esiste già una diagnosi attiva!");
							return false;
						}
		  			<%}
		  	}%>
		    return true;
   		  }

		  function goBack() {
				// Se la pagina è già in submit, ignoro questo nuovo invio!
				if (isInSubmit()) return;
 			    if(flagChanged){ 
 			    	if(!confirm("I dati sono cambiati.\nProcedere lo stesso?")){ 
 			    		return false;
			    	}
			    }
				<%
				//Recupero l'eventuale URL generato dalla LISTA precedente
				String token = "_TOKEN_" + "CMDiagnosiListaPage";
				String goBackListUrl = (String) sessionContainer.getAttribute(token.toUpperCase());				
      			%>
      			setWindowLocation("AdapterHTTP?<%= StringUtils.formatValue4Javascript(goBackListUrl) %>");
		  }
		  
</script>

<script language="Javascript">	
	<%if(!cdnLavoratore.equals("") && cdnLavoratore != null){ %>
		  if (window.top.menu != undefined){
	      	  window.top.menu.caricaMenuLav(<%=_funzione%>, <%=cdnLavoratoreDecrypt%>);
	      }
    <%}%>
</script>

<script language="Javascript" src="../../js/docAssocia.js"></script>
</head>

<body class="gestione" onload="onLoadOperation();rinfresca();">

	<%if(!cercaASL.equals("")){%>
		<br><br>
		<af:list moduleName="CercaASLCompetente"  skipNavigationButton="1" jsSelect="AggiornaForm"
	             canDelete="<%= canDelete ? \"1\" : \"0\" %>" 
	             canInsert="<%= canInsert ? \"1\" : \"0\" %>"   />

	    <table align="center">
	    	<tr>
			    <td>
			      <input type="button" class="pulsanti" value="Chiudi" onClick="window.close()">
			    </td>	
    		</tr>    
	    </table>
	<%
	}else{
	
		InfCorrentiLav testata = new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user, true);
  		testata.setSkipLista(true);
		testata.show(out);
		
		// utilizzo il costruttore per il recupero delle linguette del lavoratore cryptato
	    //Linguette l = new Linguette(user, _funzione, "CMDiagnosiMinorPage", cdnLavoratore, true);
	    LinguetteConfigurazioneRegione l = new LinguetteConfigurazioneRegione (user, _funzione, "CMDiagnosiMinorPage" , cdnLavoratore , "1", true , "LNDGNFNZ");

	    if(!inserisciNuovo){
	    	l.show(out);
	    }
	   
    %>

	<font color="red"><af:showErrors/></font>
	<af:showMessages prefix="InserisciDiagnosiFunz"/>
	<af:showMessages prefix="M_Save_Diagnosi_Funz"/>

	<af:showErrors />
	
	<af:error />
	
	<p class="titolo">Diagnosi Funzionale</p>

	<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="controllaDati() && controllaDate() && controllaDataFine()">

		<input type="hidden" name="PAGE" value="CMDiagnosiMinorPage" />		

		<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>" />
		<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
		<input type="hidden" name="prgDiagnosiFunzionale" value="<%=prgDiagnosiFunzionale%>">
		<input type="hidden" name="dataOdierna" value="<%=dataOdierna%>">
		
		<input type="hidden" name="CDNUTINS" value="<%=cdnUtIns%>">
		<input type="hidden" name="CDNUTMOD" value="<%=cdnUtMod%>">

		<%= htmlStreamTop %>
		      <table class="main" border="0">
		        <tr><td colspan="2"/>&nbsp;</td></tr>
		        <tr>
					<td class="etichetta">Azienda A.S.L. Competente&nbsp;</td>
					<td class="campo">
					
		                  <af:textBox title="Codice del tipo di Azienda A.S.L. Competente" value="<%=CODAZIENDAASL%>" classNameBase="input" name="CODAZIENDAASL" size="6" onKeyUp="fieldChanged();" readonly="<%=fieldReadOnly%>"/> 
		                  &nbsp;
		                  <%if (canModify) { %>
		                  <a href="javascript:cercaASLCompetente('codice');">
		                  	<img src="../../img/binocolo.gif" alt="Cerca per codice"></a>&nbsp;&nbsp;&nbsp;
		                  <%}%>
		                  <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('CODAZIENDAASL')"; </SCRIPT>
		                  
		                  <af:textBox title="Descrizione del tipo di Azienda A.S.L. Competente" value="<%=descrASL%>" classNameBase="input" name="descrASL" size="30" onKeyUp="fieldChanged();" readonly="<%=fieldReadOnly%>"/>
		                  <%if (canModify) { %>
		                  &nbsp;*&nbsp;
		                  <a href="javascript:cercaASLCompetente('descrizione');">
		                  	<img src="../../img/binocolo.gif" alt="Cerca per descrizione">
		                  </a>
		                  <%}%>  
		                  <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('descrASL')"; </SCRIPT>

				    </td>
		        </tr>
		        <tr>
		          <td class="etichetta">Revisione accert. prevista&nbsp;</td>	
				  <td class="campo">
					    <af:comboBox 
					      name="FLGREVACCERTAMENTO"
					      classNameBase="input"
					      disabled="<%= String.valueOf(!canModify) %>"
					      onChange="javaScript:abilitaDataRevisione(); fieldChanged(); ">
					      <option value=""  <% if ( "".equalsIgnoreCase(FLGREVACCERTAMENTO) )  { %>SELECTED="true"<% } %> ></option>
					      <option value="S" <% if ( "S".equalsIgnoreCase(FLGREVACCERTAMENTO) ) { %>SELECTED="true"<% } %> >Sì</option>
					      <option value="N" <% if ( "N".equalsIgnoreCase(FLGREVACCERTAMENTO) ) { %>SELECTED="true"<% } %> >No</option>
					    </af:comboBox>&nbsp;&nbsp;&nbsp;     
				        entro il&nbsp;&nbsp;
				        <af:textBox validateOnPost="true" disabled="false" type="date" name="DATREVISIONEACCERT" value="<%=DATREVISIONEACCERT%>" size="12" maxlength="12" readonly="<%=fieldReadOnly%>" classNameBase="input" />
				       
				        <%if ( !("S".equalsIgnoreCase(FLGREVACCERTAMENTO)) ) {%> 
							<SCRIPT language="JavaScript">
								document.Frm1.DATREVISIONEACCERT.disabled = true;
							</SCRIPT>
						<%}%>
				  </td>
		        </tr>
		      </table>
           
  			  <%String checkFis = "";	
  			  if(FLGINVALIDFISICA.equals("S")) checkFis="CHECKED";
  			  else checkFis="";%>

  			  
		      <table border="0" align="left" width="100%">
		        <tr>
		          <td colspan="3"><br/><div class="sezione2">Giudizio diagnostico</div></td>
		        </tr>
		        <tr>
		          <td><input type="checkbox" name="FLGINVALIDFISICA" onChange="fieldChanged();" value="<%=FLGINVALIDFISICA%>" <%=checkFis%>/>Fisica&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
				  <td>&nbsp;</td>
		          <td>
		          	Diagnosi&nbsp;&nbsp;
				  </td>
		        </tr>
		        
	  			<%String checkPsi = "";	
	  			if(FLGINVALIDPSICHICA.equals("S")) checkPsi="CHECKED";
	  			else checkPsi="";%>
		        <tr>
		          <td colspan="2"><input type="checkbox" name="FLGINVALIDPSICHICA" onChange="fieldChanged();" value="<%=FLGINVALIDPSICHICA%>" <%=checkPsi%>/><%=labelMentale?"Mentale":"Psichica" %>&nbsp;&nbsp;&nbsp;&nbsp;</td>
		          <td rowspan = "3">
		          	<af:textArea cols="60" rows="5" maxlength="2000" readonly="<%=fieldReadOnly%>" classNameBase="input"  
                		name="STRGIUDIZIODIAGNOSTICO" 
                		value="<%=STRGIUDIZIODIAGNOSTICO%>" validateOnPost="true" 
                        required="false" title="Note" onKeyUp="fieldChanged();"/>
				  </td>
		        </tr>
				
				<%if (configDiagnFunz.equals("1")) {
					String checkNonDeterminato = "";
					if(FLGINVALIDNONDETERMINATA.equals("S")) checkNonDeterminato="CHECKED";
	  				else checkNonDeterminato="";%>
	  				<tr>
			          <td colspan="2"><input type="checkbox" name="FLGINVALIDNONDETERMINATA" onChange="fieldChanged();" value="<%=FLGINVALIDNONDETERMINATA%>" <%=checkNonDeterminato%>/>&nbsp;Non determinato&nbsp;&nbsp;&nbsp;&nbsp;</td>
			        </tr>
				<%} else {
		  			String checkInt = "";	
		  			if(FLGINVALIDINTELLETTIVA.equals("S")) checkInt="CHECKED";
		  			else checkInt="";%>		        
			        <tr>
			          <td colspan="2"><input type="checkbox" name="FLGINVALIDINTELLETTIVA" onChange="fieldChanged();" value="<%=FLGINVALIDINTELLETTIVA%>" <%=checkInt%>/>Intellettiva&nbsp;&nbsp;&nbsp;&nbsp;</td>
			        </tr>
	
		  			<%String checkSens = "";	
		  			if(FLGINVALIDSENSORIALE.equals("S")) checkSens="CHECKED";
		  			else checkSens="";%>		        		        
			        <tr>
			          <td colspan="2"><input type="checkbox" name="FLGINVALIDSENSORIALE" onChange="fieldChanged();" value="<%=FLGINVALIDSENSORIALE%>" <%=checkSens%>/>Sensoriale&nbsp;&nbsp;&nbsp;&nbsp;</td>
			        </tr>
			     <%}%>
			        <tr>
			          <td>&nbsp;</td>
			        </tr>
			        <tr>
			          <td colspan="3"><br/><div class="sezione2">Giudizio medico-legale</div></td>
			        </tr>
		        <tr>
					<td class="campo" colspan="3">					
		                  <af:comboBox name="CODGIUDIZIO"
						        title="Giudizio medico-legale"
						        selectedValue="<%=CODGIUDIZIO%>"
						        required="false"
						        moduleName="M_GetGiudizioML"
						        disabled="<%= String.valueOf(!canModify) %>"
						        classNameBase="input"
						        onChange="fieldChanged()"
						        addBlank="true"/> 
						        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						  %&nbsp;
						  <af:textBox size="5" type="text" name="numPercInval" value="<%=numPercInval%>" maxlength="4" onKeyUp="fieldChanged();" readonly="<%=fieldReadOnly%>" classNameBase="input"/>
						  <input type="hidden" name="numPercInvalOld" value="<%=numPercInval%>">
						  <input type="hidden" name="codGiudizioOld" value="<%=CODGIUDIZIO%>">
					</td>
										
		        </tr>
		        <tr>
		          <td>&nbsp;</td>
		          <td>&nbsp;</td>
		          <td>&nbsp;</td>
		        </tr>	
		        <tr>
		          <td class="campo" colspan="6">Data ricon. ex L. 104/92&nbsp;
		          <af:textBox validateOnPost="true" type="date" name="DATRICONOSCIMENTO" onKeyUp="fieldChanged();" value="<%=DATRICONOSCIMENTO%>" size="12" maxlength="12" readonly="<%=fieldReadOnly%>" classNameBase="input"/>
		          &nbsp;&nbsp;Stato di gravità ex L.104/92&nbsp;&nbsp;
					    <af:comboBox 
					      name="FLGSTATOGRAVITA104"
					      classNameBase="input"
					      disabled="<%= String.valueOf(!canModify) %>"
					      onChange="fieldChanged();">
					      <option value=""  <% if ( "".equalsIgnoreCase(FLGSTATOGRAVITA104) )  { %>SELECTED="true"<% } %> ></option>
					      <option value="S" <% if ( "S".equalsIgnoreCase(FLGSTATOGRAVITA104) ) { %>SELECTED="true"<% } %> >Sì</option>
					      <option value="N" <% if ( "N".equalsIgnoreCase(FLGSTATOGRAVITA104) ) { %>SELECTED="true"<% } %> >No</option>
					    </af:comboBox>
		          &nbsp;&nbsp;Permanenza&nbsp;stato&nbsp;invalidante&nbsp;&nbsp;
					    <af:comboBox 
					      name="FLGPERMANENZASTATO"
					      classNameBase="input"
					      disabled="<%= String.valueOf(!canModify) %>"
					      onChange="fieldChanged();">
					      <option value=""  <% if ( "".equalsIgnoreCase(FLGPERMANENZASTATO) )  { %>SELECTED="true"<% } %> ></option>
					      <option value="S" <% if ( "S".equalsIgnoreCase(FLGPERMANENZASTATO) ) { %>SELECTED="true"<% } %> >Sì</option>
					      <option value="N" <% if ( "N".equalsIgnoreCase(FLGPERMANENZASTATO) ) { %>SELECTED="true"<% } %> >No</option>
					    </af:comboBox>
		          </td>
		        </tr>
		        <tr><td>&nbsp;</td></tr>
		        <tr>
		        	<td class="campo" colspan="6"><%=labelDataRedazDiagn %>&nbsp;
		        	<af:textBox title="<%=labelDataRedazDiagn %>" validateOnPost="true" onKeyUp="fieldChanged();" type="date" name="DATDIAGNOSI" value="<%=DATDIAGNOSI%>" required="true" size="12" maxlength="12" readonly="<%=fieldReadOnly%>" classNameBase="input"/>&nbsp;&nbsp;&nbsp;
				    Data fine diagnosi&nbsp;&nbsp;
				    <af:textBox title="Data fine diagnosi" validateOnPost="true" onKeyUp="fieldChanged();" type="date" name="DATFINE" value="<%=DATFINE%>" size="12" maxlength="12" classNameBase="input"/>
					</td>
				</tr>
				<tr>
		          <td colspan="3"><br/><div class="sezione2">Revisione</div></td>
		        </tr>
		        <tr>
		          <td class="etichetta">Richiesta in data&nbsp;&nbsp;</td>
		          <td class="campo"><af:textBox validateOnPost="true" type="date" name="DATRICHREVDIAGNOSI" value="<%=DATRICHREVDIAGNOSI%>" size="12" maxlength="12" onKeyUp="fieldChanged();" readonly="<%=fieldReadOnly%>" classNameBase="input"/></td>
		          <td>&nbsp;</td>
    		    </tr>
		        <tr>
				  <td class="etichetta">Pervenuta&nbsp;&nbsp;</td>
   				  <td class="campo">
   				      <af:comboBox 
					      name="FLGREVDIAGNOSIPERVENUTA"
					      classNameBase="input"
					      disabled="<%= String.valueOf(!canModify) %>"
					      onChange="fieldChanged(); abilitaDataRevPerv();">
					      <option value=""  <% if ( "".equalsIgnoreCase(FLGREVDIAGNOSIPERVENUTA) )  { %>SELECTED="true"<% } %> ></option>
					      <option value="S" <% if ( "S".equalsIgnoreCase(FLGREVDIAGNOSIPERVENUTA) ) { %>SELECTED="true"<% } %> >Sì</option>
					      <option value="N" <% if ( "N".equalsIgnoreCase(FLGREVDIAGNOSIPERVENUTA) ) { %>SELECTED="true"<% } %> >No</option>
					    </af:comboBox>				        
				  </td>
		          <td>in data&nbsp;&nbsp;
		          	<af:textBox validateOnPost="true" type="date" name="DATARRIVOREVDIAGNOSI" value="<%=DATARRIVOREVDIAGNOSI%>" size="12" maxlength="12" onKeyUp="fieldChanged();" readonly="<%=fieldReadOnly%>" classNameBase="input"/>
		          </td>
		          <%if ( !("S".equalsIgnoreCase(FLGREVDIAGNOSIPERVENUTA)) ) {%> 
						<SCRIPT language="JavaScript">
							document.Frm1.DATARRIVOREVDIAGNOSI.disabled = true;
						</SCRIPT>
				  <%}%>
		          
		        </tr>
		        <tr>
		          <td colspan="3">
			          &nbsp;
		          </td>
		        </tr>		          		    
		        <tr>
		          <td colspan="3">
			          &nbsp;
		          </td>
		        </tr>		          		    
		        <tr>
		          <td colspan="3" align="center">
		          	  <%
		          	  if(!inserisciNuovo){
		          	  %>	
		          	    <%if (canModify) { %>
		          	  	<input class="pulsanti" type="submit" name="aggiorna" value="Aggiorna" onClick="return aggiornaDiagnosiFunz()">
			          	&nbsp;&nbsp;
			          	<input type="reset" class="pulsanti" value="Annulla" onClick="controlloReset();"/>
		          	  	<%}%>
		          	  	&nbsp;&nbsp;
		          	  	<input type="button" onclick="goBack()" value = "Torna alla lista" class="pulsanti">
		          	  	&nbsp;&nbsp;

						<%
						String onClick = "apriGestioneDoc('RPT_DIAGNOSI_FUNZ','&prgDiagnosiFunzionale=" + prgDiagnosiFunzionale + "&strChiaveTabella=" + prgDiagnosiFunzionale + "&pagina=CMDiagnosiMinorPage&cdnLavoratore="+cdnLavoratoreDecrypt+"','VS03')";
						
		          	    if (canPrint) { %>
		          	  	<input type="button" onclick="<%=onClick%>" value = "Stampa" class="pulsanti">
		          	  	<%}%>
		          	  	<input type="hidden" name="aggiornamento" value="1">
		          	  	<input type="hidden" name="NUMKLODIAGNOSIFUNZIONALE" value="<%= numklodiagnosifunzionale %>">
		          	  	<input class="pulsanti" type="button" value="Documenti associati" onclick="docAssociati('<%=cdnLavoratoreDecrypt%>','CMDiagnosiMinorPage','<%=_funzione%>','','<%=prgDiagnosiFunzionale%>')"/>
		          	  <%
		          	  }else{
		          	  %>
			          	<input class="pulsanti" type="submit" name="inserisci" value="Inserisci" onClick="setFlags();">
			          	&nbsp;&nbsp;
			          	<input type="reset" class="pulsanti" value="Annulla" onClick="controlloReset();"/>
			          	&nbsp;&nbsp;
			          	<input type="button" onclick="goBack()" value = "Torna alla lista" class="pulsanti">
			          	<input type="hidden" name="inserisciNew" value="1">
			          	<input type="hidden" name="NUMKLODIAGNOSIFUNZIONALE" value="1">
					  <%}%>

		          	  <input type="hidden" name="showIscrizioneUpdate" value="0"> <!-- Viene valorizzato ad 1 se è necessario mostrare la finestra per l'aggiornamento dell'iscrizione al collocamento mirato -->	
					  	
		          </td>
		        </tr>		          		    
			  </table>
			  <br><br>

	    <%out.print(htmlStreamBottom);%> 
	    <%
	    if(!inserisciNuovo){%>    
	      <center>
	      	<table>
	      		<tr>
	      			<td align="center">
	      				<% operatoreInfo.showHTML(out); %>
	      			</td>
	      		</tr>
	      	</table>
	      </center>
	    <%}%>	
	</af:form>
	<%} //fine else%>
	
</body>
</html>
