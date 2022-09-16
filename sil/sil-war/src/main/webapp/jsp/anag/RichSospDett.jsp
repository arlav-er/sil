<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  java.text.SimpleDateFormat,
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  it.eng.sil.security.User,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%
 
  int _funzione=0;
  InfCorrentiLav infCorrentiLav= null;

  BigDecimal cdnUtIns = null;
  String dtmIns = null;
  BigDecimal cdnUtMod = null;
  String dtmMod = null;
  Testata operatoreInfo = null;
  String NUMKLOSOSPENSIONE = null;

  String  STRRAGIONESOCIALE = "";
  String  STRCODICEFISCALE = "";
  String  STRPARTITAIVA = "";
  String  CODSTATORICHIESTA = "";
  String  CODMOTIVOSOSP = "";
  String  DATRICHIESTA = "";
  String  FLGPROVVEDIMENTO = "";
  String  FLGPROROGA = "";
  String  DATINIZIOSOSP = "";
  String  DATFINESOSP = "";
  String  DATFINETEMPORANEA = "";
  String  prgRichSospensione = "";

  String htmlStreamTop2 = "";
  String htmlStreamBottom2 = "";

  String goBackListPage = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "goBackListPage");

  boolean nuovaRichiesta = !(serviceRequest.getAttribute("nuovaRichiesta")==null || 
                            ((String)serviceRequest.getAttribute("nuovaRichiesta")).length()==0);
                            
  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page); 


  PageAttribs attributi = new PageAttribs(user, _page); 

  boolean canModify = false;
  boolean canModifySedi = true;
  boolean canInsert = false; 
  boolean canSalvaStato = false;
  
  if (! filter.canView()){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  } else {
  		canModify = attributi.containsButton("aggiorna");
  		canInsert = attributi.containsButton("INSERISCI"); 
  		canSalvaStato = attributi.containsButton("SALVA-STATO");
  }

  canInsert = canInsert && canSalvaStato;
  canModify = canModify && canSalvaStato;
  canModifySedi = canModifySedi && canSalvaStato;
  
  boolean readOnlyStr = !canModify;
  String fieldReadOnly = canModify ? "false" : "true"; 
  boolean disableCodRich = false || readOnlyStr;
  String fieldCodRich = disableCodRich ? "true" : "false";
  
  String prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest,"prgAzienda");
  String prgUnita = StringUtils.getAttributeStrNotNull(serviceRequest,"prgUnita");

  String cercaAzienda = StringUtils.getAttributeStrNotNull(serviceRequest,"cercaAzienda");
  String sediCoinvilte = StringUtils.getAttributeStrNotNull(serviceRequest,"sediCoinvilte");
  String inserisciSede = StringUtils.getAttributeStrNotNull(serviceRequest,"inserisciSede");
  String loadSedi = StringUtils.getAttributeStrNotNull(serviceRequest,"loadSedi"); 
  String eliminaSede = StringUtils.getAttributeStrNotNull(serviceRequest,"eliminaSede");  
  
  SourceBean rowAz = null;
  
  String fromRicerca = StringUtils.getAttributeStrNotNull(serviceRequest,"fromRicerca");

  //String prgAzienda = null;
  String prgAziendaApp = StringUtils.getAttributeStrNotNull(serviceRequest,"prgAzienda");
  
  prgRichSospensione = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "PRGRICHSOSPENSIONE");
  
  if (prgRichSospensione.equals("")){
  		SourceBean rowRich = (SourceBean) serviceResponse.getAttribute("M_INSERT_RICHSOSP");
  		prgRichSospensione = SourceBeanUtils.getAttrStrNotNull(rowRich, "PRGRICHSOSPENSIONE");
  }
  /*--------------------------------------------------------------------------------------------------------------*/    

  if (prgAzienda != null && !prgAzienda.equals("") ) {
      rowAz = (SourceBean) serviceResponse.getAttribute("M_Info_Azienda.ROWS.ROW");
   	  STRRAGIONESOCIALE = Utils.notNull(rowAz.getAttribute("RAGIONESOCIALE"));		    
	  STRCODICEFISCALE = Utils.notNull(rowAz.getAttribute("CODICEFISCALE")); 
	  STRPARTITAIVA = Utils.notNull(rowAz.getAttribute("PIVA"));  
  }
	
  	
  if(nuovaRichiesta){ 
	
	CODSTATORICHIESTA = "DA";
	cdnUtIns = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
	cdnUtMod = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer().getAttribute("_CDUT_");

  }else if(!cercaAzienda.equals("1")){
	if(!cercaAzienda.equals("1")){
  
	  SourceBean row = null;
	  Vector rows= serviceResponse.getAttributeAsVector("M_Load_RichSosp.ROWS.ROW");
	  // siamo in dettaglio per cui avro' al massimo una riga
	  if (rows.size()==1) {
	        row = (SourceBean)rows.get(0);
			
		    prgRichSospensione = Utils.notNull(row.getAttribute("prgRichSospensione"));		
		    prgAzienda = Utils.notNull(row.getAttribute("prgAzienda")); 
		    STRRAGIONESOCIALE = Utils.notNull(row.getAttribute("STRRAGIONESOCIALE"));		    
		    STRCODICEFISCALE = Utils.notNull(row.getAttribute("STRCODICEFISCALE")); 
		    STRPARTITAIVA = Utils.notNull(row.getAttribute("STRPARTITAIVA"));  
		   
		    CODSTATORICHIESTA = Utils.notNull(row.getAttribute("CODSTATORICHIESTA"));  
		    CODMOTIVOSOSP = Utils.notNull(row.getAttribute("CODMOTIVOSOSP"));  
		    DATRICHIESTA = Utils.notNull(row.getAttribute("DATRICHIESTA"));  
		    FLGPROVVEDIMENTO = Utils.notNull(row.getAttribute("FLGPROVVEDIMENTO"));  
		    FLGPROROGA = Utils.notNull(row.getAttribute("FLGPROROGA"));  
		    DATINIZIOSOSP = Utils.notNull(row.getAttribute("DATINIZIOSOSP"));  
		    DATFINESOSP = Utils.notNull(row.getAttribute("DATFINESOSP"));  
		    DATFINETEMPORANEA = Utils.notNull(row.getAttribute("DATFINETEMPORANEA"));  
	        
	        // aggiornamento del lock ottimistico in aggiornamento
        	NUMKLOSOSPENSIONE = String.valueOf(((BigDecimal)row.getAttribute("NUMKLOSOSPENSIONE")).intValue());
	        
	        cdnUtIns = (BigDecimal) row.getAttribute("CDNUTINS");
	        dtmIns = (String) row.getAttribute("DTMINS");

		    cdnUtMod = (BigDecimal) row.getAttribute("CDNUTMOD");
	    	//cdnUtMod = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
		    	    
	        dtmMod = (String) row.getAttribute("DTMMOD");
	        operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
	        //infCorrentiAzienda= new InfCorrentiAzienda(sessionContainer, prgAzienda,prgUnita,prgRichiestaAz);     
	  }
    }
  }  
  
  /*------------------------ inserire il parametro cdnFunzione ---------------------------*/
  //_funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  _funzione = Integer.parseInt(StringUtils.getAttributeStrNotNull(serviceRequest,"CDNFUNZIONE"));



//----------------------------------------------------------------------------------------------------
  //SourceBean rowRich = null;
  SourceBean rowDoc = null;
  Vector rowsDoc      = null;  
  String prAutomatica     = "S";
  String docInOut         = "I";
  String docRif           = "Documentazione L68";
  String docTipo          = "Richiesta di sospensione";
  BigDecimal numProtV     = null;
  BigDecimal numAnnoProtV = null;
  String dataOraProt      = "";
  String noButton = "false"; 
  String datProtV     = "";
  String oraProtV     = "";
  Vector rowsProt     = null;
  SourceBean rowProt  = null;

  Vector rowsDoc1     = null;
  SourceBean rowDoc1  = null;

  String codStatoAttoV = "";
  String CODSTATOATTO_P = null;

  Calendar oggi = Calendar.getInstance();
  String giornoDB = Integer.toString(oggi.get(5));
  if (giornoDB.length() == 1){
	giornoDB = '0' + giornoDB;
  }
  String meseDB = Integer.toString(oggi.get(2)+1);
  if (meseDB.length() == 1){
  	meseDB = '0' + meseDB;
  }
  String annoDB = Integer.toString(oggi.get(1));
  String dataOdierna = giornoDB + "/" + meseDB + "/" + annoDB; 

  boolean isInsert = (StringUtils.getAttributeStrNotNull(serviceRequest,"nuovo")).equals("true") ? true : false;

  if(!nuovaRichiesta){
	  rowDoc = (SourceBean) serviceResponse.getAttribute("M_Load_Doc_RichSosp.ROWS.ROW");
	  if (rowDoc != null) {
		numAnnoProtV = SourceBeanUtils.getAttrBigDecimal(rowDoc, "numAnnoProt", null);
	 	numProtV = SourceBeanUtils.getAttrBigDecimal(rowDoc, "numProtocollo", null);
	 	dataOraProt = SourceBeanUtils.getAttrStrNotNull(rowDoc, "datProtocollo");
	 	CODSTATOATTO_P = SourceBeanUtils.getAttrStrNotNull(rowDoc, "CODSTATOATTO");
		if (!dataOraProt.equals("")) {
	  		oraProtV = dataOraProt.substring(11,16);
	  		datProtV = dataOraProt.substring(0,10);
  		} 
	  }
  }else{
	  CODSTATOATTO_P = "NP";
  }

//----------------------------------------------------------------------------------------------------	  

  String statoRichiesta = null;
  SourceBean row2 = null;
  if(!nuovaRichiesta){
	  Vector rows2= serviceResponse.getAttributeAsVector("M_Load_RichSosp.ROWS.ROW");
	  if (rows2.size()==1) {
	        row2 = (SourceBean)rows2.get(0);			
		    statoRichiesta = Utils.notNull(row2.getAttribute("CODSTATORICHIESTA")); 
	  }
	  if(statoRichiesta.equals("AN") || CODSTATOATTO_P.equals("PR")) {
	  	fieldReadOnly = "true";
	  	canModifySedi = false;
	  	if(statoRichiesta.equals("AN")) {
	  		canModify = false;
	  		canInsert = false;
	  		noButton = "true";
	  		fieldCodRich = "true";
	  	}
	  }
  } else {
		noButton = "true";
		fieldCodRich = "true";
  }

  //Servono per gestire il layout grafico
  String htmlStreamTop = StyleUtils.roundTopTable(true);
  String htmlStreamBottom = StyleUtils.roundBottomTable(true);

%>

<html>

<head>

    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>    
    <af:linkScript path="../../js/"/>
    
    <SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT> 

	<%-- include lo script che permette di aprire la PopUp che gestisce i documenti (salvataggio/protocollazione) --%>
	<%-- String queryString = null; --%>
	<%--@ include file="../documenti/_apriGestioneDoc.inc"--%>
		
	<script language="JavaScript">
          
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
		  
		  function AggiornaForm (PRGAZIENDA, STRRAGIONESOCIALE, STRCODICEFISCALE, STRPARTITAIVA) {
			  window.opener.document.Frm1.STRRAGIONESOCIALE.value = STRRAGIONESOCIALE;
			  window.opener.document.Frm1.STRCODICEFISCALE.value = STRCODICEFISCALE;
			  window.opener.document.Frm1.STRPARTITAIVA.value = STRPARTITAIVA;
			  window.opener.document.Frm1.PRGAZIENDA.value = PRGAZIENDA;
			  
			  window.close();
		  }


		  var urlpage="AdapterHTTP?";	

		  function getURLPageBase() {    
		    urlpage+="CDNFUNZIONE=<%=_funzione%>&";
		    return urlpage;
 		  }

		  function indietroRicerca() {
			  if (isInSubmit()) return;
			  if(flagChanged)
			  { if(!confirm("I dati sono cambiati.\nProcedere lo stesso?"))
			    { return false;
			    }
			  }
			  urlpage = getURLPageBase();
			  urlpage+="PAGE=CMRichSospRicercaPage&";
			  urlpage = urlpage + "&prgAzienda=<%=prgAzienda%>";
			  setWindowLocation(urlpage);
		  }
		  

		  function cercaAzienda(){
			  var f;
 		      f = "AdapterHTTP?PAGE=CMRichSospDettPage&cercaAzienda=1";
 		      f = f + "&CDNFUNZIONE=<%=_funzione%>";
			  f = f + "&prgAzienda=<%=prgAzienda%>";
			  f = f + "&prgRichSospensione=<%=prgRichSospensione%>";
			  f = f + "&ragioneSoc=" + document.Frm1.STRRAGIONESOCIALE.value;
			  f = f + "&CodFisc=" + document.Frm1.STRCODICEFISCALE.value;
			  f = f + "&PIVA=" + document.Frm1.STRPARTITAIVA.value;
			  f = f + "&goBackListPage=<%=goBackListPage%>";
			  
			  var t = "_blank";
			  var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=600,height=500,top=100,left=100";
 		  	  window.open(f, t, feat);
		  }	
		  
		  function SediCoinv(){
			  var f;
 		      f = "AdapterHTTP?PAGE=CMRichSospDettPage&sediCoinvilte=1";
 		      f = f + "&CDNFUNZIONE=<%=_funzione%>";
			  f = f + "&prgAzienda=<%=prgAzienda%>";
			  f = f + "&prgRichSospensione=<%=prgRichSospensione%>";
			  f = f + "&goBackListPage=<%=goBackListPage%>";
			  
			  var t = "_blank";
			  var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=600,height=500,top=100,left=100";
 		  	  window.open(f, t, feat);		  
		  }
		  
		  function goBack() {
				var f;
				<%
				String goBackButtonTitle = "";
				boolean goBackIsInList = false;
				%>
				// Se la pagina è già in submit, ignoro questo nuovo invio!
				if (isInSubmit()) return;
				<%
				// Recupero l'eventuale URL generato dalla LISTA precedente
				String token = "_TOKEN_" + goBackListPage;
				String goBackListUrl = (String) sessionContainer.getAttribute(token.toUpperCase());				
	      		if(goBackListPage.equals("CMRichSospListaPage")){
	      			goBackButtonTitle = "Torna alla lista";
	      			%>
	      			setWindowLocation("AdapterHTTP?<%= StringUtils.formatValue4Javascript(goBackListUrl) %>");
	      			<%
	      		}else{
	      			goBackButtonTitle = "Torna alla ricerca";
	      			%>
	      			f = "AdapterHTTP?PAGE=CMRichSospRicercaPage";
			 		f = f + "&CDNFUNZIONE=<%=_funzione%>";
					f = f + "&prgRichSospensione=<%=prgRichSospensione%>";
	      			document.location = f;
	      			<%
	      		}
				%>							
		  }
	   
 		  function indietroSediCoinv(){
			  var f;
 		      f = "AdapterHTTP?PAGE=CMRichSospDettPage&sediCoinvilte=1";
 		      f = f + "&CDNFUNZIONE=<%=_funzione%>";
			  f = f + "&prgAzienda=<%=prgAzienda%>";
			  f = f + "&prgRichSospensione=<%=prgRichSospensione%>";
			  f = f + "&goBackListPage=<%=goBackListPage%>";
			  
			  document.location = f;		  	
		  }

		  function checkDate (strdata1, strdata2) {
			  annoVar1 = strdata1.substr(6,4);
			  meseVar1 = strdata1.substr(3,2);
			  giornoVar1 = strdata1.substr(0,2);
			  dataVarInt1 = parseInt(annoVar1 + meseVar1 + giornoVar1, 10);

			  annoVar2 = strdata2.substr(6,4);
			  meseVar2 = strdata2.substr(3,2);
			  giornoVar2 = strdata2.substr(0,2);
			  dataVarInt2 = parseInt(annoVar2 + meseVar2 + giornoVar2, 10);
			  
			  if (dataVarInt1 < dataVarInt2) {
			      return 1;
			  }
			  else {
			      if (dataVarInt1 > dataVarInt2) {
			        return 2;
			      }
			      else {
			        return 0;
			      }
			  }
		  }
		  
		  function controllaDati()
		  	{
		  	    var dataInizioSosp = document.Frm1.DATINIZIOSOSP.value;
		  	    var dataFineSosp = document.Frm1.DATFINESOSP.value;
		  	    ok=true;
				if (dataInizioSosp != "" && dataFineSosp != ""){
  					if (compareDate(dataInizioSosp, dataFineSosp)>0) {
						alert("La data inizio sospensione deve essere minore della data fine sospensione");
						ok=false;
					} 
					var statoRich = document.Frm1.CODSTATORICHIESTA.value;
			 		if(statoRich == "AP"){
			 			document.Frm1.aggProsp.value = 1;
			 		}
				}								
				return ok;
   			}

			var opened = "";
			function selezionaAzienda()
				 {
					var url = "AdapterHTTP?PAGE=IdoSelezionaAziendaPage&AGG_FUNZ=riempiDatiAzienda&cdnFunzione=<%=_funzione%>";
					var feat = "toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes," +
							   "width=600,height=500,top=50,left=100";
					opened = window.open(url, "_blank", feat);
					opened.focus();
				 }
		
			function riempiDatiAzienda(prgAzienda, aziCodFiscale, aziPIva, aziRagSociale) 
			{
				document.Frm1.prgAzienda.value = prgAzienda;
				document.Frm1.aziCodFiscale.value = aziCodFiscale;
				document.Frm1.aziPIva.value = aziPIva;
				document.Frm1.aziRagSociale.value = aziRagSociale;
				opened.close();
			}
				
			function controllaDatiAzienda() { 
				//if (document.Frm1.aziCodFiscale.value == "" && document.Frm1.aziPIva.value == "" && document.Frm1.aziRagSociale.value == "" ) 
				if (document.Frm1.prgAzienda.value == "") 
					{ 
					alert("Caricare i dati relativi all'azienda!");
					return false;
				}
				if (document.Frm1.CODSTATORICHIESTA.value == "") { 
					alert("Il campo Stato richiesta è obbligatorio");
					return false;
				}
				if (document.Frm1.DATRICHIESTA.value == "") { 
					alert("Il campo Data richiesta è obbligatorio");
					return false;
				}
				if (document.Frm1.CODMOTIVOSOSP.value == "") { 
					alert("Il campo Motivo della sospensione è obbligatorio");
					return false;
				}
				return true;
		
			} 

			function aggiornaDocumento(){
				var f;
				
				var codStatoAtto = document.Frm1.CODSTATOATTO_P.value;
		 		var codStatoAttoInReq = "<%=CODSTATOATTO_P%>";
	
		 		if (codStatoAttoInReq == codStatoAtto) {
			 		alert("Stato atto non modificato");
					return false;
			 	}
		 	
		 		else {
		 			var numAnnoProt = document.Frm1.numAnnoProt.value;
		 			var numProtocollo = document.Frm1.numProtocollo.value;
		 			var dataOraProt = document.Frm1.dataOraProt.value;
					var codTipoDocumento = document.Frm1.codTipoDocumento.value;
		 			var tipoProt = document.Frm1.tipoProt.value;
		 			var FlgcodMonoIO = document.Frm1.FlgcodMonoIO.value;
		 			var codAmbito = document.Frm1.codAmbito.value;
			 		 
		 		  		 
		 			f = "AdapterHTTP?PAGE=CMRichSospDettPage&aggiornaDoc=1";
		 			f = f + "&CDNFUNZIONE=<%=_funzione%>";
					f = f + "&prgRichSospensione=<%=prgRichSospensione%>";
					f = f + "&CODSTATOATTO_P=" + codStatoAtto;
					f = f + "&numAnnoProt=" + numAnnoProt;		  			  
					f = f + "&numProtocollo=" + numProtocollo;		  			  
					f = f + "&dataOraProt=" + dataOraProt;
					f = f + "&CODSTATORICHIESTA=<%=CODSTATORICHIESTA%>";
					f = f + "&goBackListPage=<%=goBackListPage%>";
					f = f + "&prgAzienda=<%=prgAzienda%>";
					f = f + "&codTipoDocumento=" + codTipoDocumento;
					f = f + "&tipoProt=" + tipoProt;
					f = f + "&FlgcodMonoIO=" + FlgcodMonoIO;
					f = f + "&codAmbito=" + codAmbito;
					 
					if(codStatoAtto == "AN"){
					 	f = f + "&annullamento=1";
					 	f = f + "&NUMKLOSOSPENSIONE=<%=NUMKLOSOSPENSIONE%>";
					} else {
						f = f + "&annullamento=0";
					}
					 		  			  
					document.location = f;
				}
			}

			function annullamentoDoc(){
			 	var codStatoAtto = "<%=CODSTATOATTO_P%>";
			 	document.Frm1.CODSTATOATTO_P.value = codStatoAtto;
	
			 	var statoRich = document.Frm1.CODSTATORICHIESTA.value;
			 	if(statoRich == "AN"){
			 		document.Frm1.annullamento.value = 1;
			 	}
			}
					     		    
	</script>
	
	<script language="Javascript" src="../../js/docAssocia.js"></script>
	
	<script language="Javascript">
	
	<% if(prgUnita.equals("")) {
	   		SourceBean rowPrg = (SourceBean) serviceResponse.getAttribute("M_GetPrgUnita.ROWS.ROW");
 	   		if(rowPrg != null) prgUnita = SourceBeanUtils.getAttrStrNotNull(rowPrg, "prgUnita");
 	   	}

	 	if(sediCoinvilte.equals("") && loadSedi.equals("") && cercaAzienda.equals("") && inserisciSede.equals("") && eliminaSede.equals("")) {
 	   		if (prgAzienda!=null && (prgUnita!=null && !prgUnita.equals("")) ) { 
 	%>
				window.top.menu.caricaMenuAzienda(<%=_funzione%>, <%=prgAzienda%>, <%=prgUnita%>);
	<% 
	    	} 
		}
	%>
	
	</script> 
</head>

<body class="gestione" onload="rinfresca();">

    <%if(!cercaAzienda.equals("")){%>
		<br><br>
		<af:list moduleName="M_CercaAzienda" skipNavigationButton="0" jsSelect="AggiornaForm"
	             canInsert="<%= canInsert ? \"1\" : \"0\" %>"   />		

	    <table align="center">
	    	<tr>
			    <td>
			      <input type="button" class="pulsanti" value="Chiudi" onClick="window.close()">
			    </td>	
    		</tr>    
        </table>
    <%
    }else if(!sediCoinvilte.equals("") || !inserisciSede.equals("") || !eliminaSede.equals("")){	
    %>
		<br><br>
		<af:list moduleName="M_Load_SediCoinvolte" skipNavigationButton="0" 
					 canDelete="<%= canModifySedi ? \"1\" : \"0\" %>" 
             	     canInsert="<%= canModifySedi ? \"1\" : \"0\" %>" />		
		
	    <table align="center">
	    	<tr>
			    <td>
			      <input type="button" class="pulsanti" value="Chiudi" onClick="window.close()">
			    </td>	
    		</tr>    
        </table>	
	<%}else if(!loadSedi.equals("")){%>
		<br><br>
		<af:list moduleName="M_Load_Sedi" skipNavigationButton="0"/>
		
	    <table align="center">
	    	<tr>
			    <td>
			      <input type="button" class="pulsanti" value="Chiudi" onClick="indietroSediCoinv();">
			    </td>	
    		</tr>    
        </table>	

	<%}else{%>
	
	<%if(nuovaRichiesta){ %>
    	<p class="titolo">Nuova richiesta di sospensione</p>
	<%}else{%>
		<p class="titolo">Richiesta di sospensione</p>	
	<%}%>

	<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="controllaDati() && controllaDatiAzienda()">
	
	<input type="hidden" name="PAGE" value="CMRichSospDettPage" />		
	<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
	<input type="hidden" name="CDNUTINS" value="<%=cdnUtIns%>">
	<input type="hidden" name="CDNUTMOD" value="<%=cdnUtMod%>">
	<input type="hidden" name="prgAzienda" value="<%=prgAzienda%>"> 
	<input type="hidden" name="prgRichSospensione" value="<%=prgRichSospensione%>">
	<input type="hidden" name="strChiaveTabella" value="<%=prgRichSospensione%>" />
	<input type="hidden" name="goBackListPage" value="<%=goBackListPage%>" />
<%
  //Oggetti per l'applicazione dello stile
  htmlStreamTop = StyleUtils.roundTopTable(isInsert);
  htmlStreamBottom = StyleUtils.roundBottomTable(isInsert);
%>
		<%if(!nuovaRichiesta){ %>
			<span class="sezioneMinimale">
				Azienda&nbsp;
			</span>			
			<%= htmlStreamTop %>
			  <table class="main" border="0">
		        <tr>
		          <td class="etichetta">Ragione sociale</td>
		          <td class="campo">
	                  <af:textBox title="Ragione sociale azienda" value="<%=STRRAGIONESOCIALE%>" classNameBase="input" name="STRRAGIONESOCIALE" size="45" readonly="true" />
	                  <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('STRRAGIONESOCIALE')"; </SCRIPT>
		          </td>
		        </tr>
		        <tr>
		          <td class="etichetta">Codice Fiscale</td>
		          <td class="campo">
	                  <af:textBox title="Codice fiscale azienda" value="<%=STRCODICEFISCALE%>" classNameBase="input" name="STRCODICEFISCALE" size="15" readonly="true" /> 
	                  <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('STRCODICEFISCALE')"; </SCRIPT>
		          </td>
		        </tr>
		        <tr>
		          <td class="etichetta">Partita IVA</td>
		          <td class="campo">
	                  <af:textBox title="Partita IVA azienda" value="<%=STRPARTITAIVA%>" classNameBase="input" name="STRPARTITAIVA" size="20" readonly="true" />&nbsp;
		          </td>
		        </tr>
		      </table>			
			<%= htmlStreamBottom %>

			
			<af:showMessages prefix="M_Insert_RichSosp"/>
			<af:showMessages prefix="M_Save_RichSosp"/>	
			<af:showMessages prefix="SalvaRichSospDoc"/>
			<af:showMessages prefix="M_Annullamento_RichSosp_E_Doc"/>					
			<af:showErrors />		
					
		<%}%>

	<%if(nuovaRichiesta){ %>
		<div id="divLookAzi_look" style="display:">
		<span class="sezioneMinimale">Azienda&nbsp;&nbsp;
			<% if (prgAzienda == null || prgAzienda.equals("")) { %>
			<a href="#" onClick="selezionaAzienda();return false"><img src="../../img/binocolo.gif" alt="Cerca"></a>
			<%}%>
		</span>
		<%= htmlStreamTop %>
		<table class="main" width="100%">
		<tr valign="top">
			<td class="etichetta">Codice Fiscale</td>
			<td class="campo">
				<input type="text" name="aziCodFiscale" class="inputView" readonly="true" value="<%=STRCODICEFISCALE%>" size="30" maxlength="16"/>
			</td>
		</tr>
	<tr valign="top">
		<td class="etichetta">Partita IVA</td>
		<td class="campo">
			<input type="text" name="aziPIva" class="inputView" readonly="true" value="<%=STRPARTITAIVA%>" size="30" maxlength="30"/>
		</td>
	</tr>

	<tr valign="top">
		<td class="etichetta">Ragione Sociale</td>
		<td class="campo">
			<input type="text" name="aziRagSociale" class="inputView" readonly="true" value="<%=STRRAGIONESOCIALE%>" size="75" maxlength="120"/>
		</td>
	</tr>
	</table>
			<%= htmlStreamBottom %>
		</div>

		<% } %>

<%
  htmlStreamTop2 = StyleUtils.roundTopTable(isInsert || fieldReadOnly.equals("false"));
  htmlStreamBottom2 = StyleUtils.roundBottomTable(isInsert || fieldReadOnly.equals("false"));
%>	
		<%= htmlStreamTop2 %>
		<%@ include file="_protocollazionerichiestasospensione.inc" %>
				
			<script>	
			 
			 function gestione_Protocollazione() {
		 		<%-- DOCAREA: basta questo controllo. I valori della protocollazione vengono impostati dalla classe Documento stessa. --%>
				var protocolloLocale = <%=it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil.protocollazioneLocale() %>;
				if (!protocolloLocale) return;
				
				
				<% if(!nuovaRichiesta && CODSTATOATTO_P.equals("NP")) { %>
					if (document.Frm1.CODSTATOATTO_P.value == "PR"){
					<%  
					rowsProt = serviceResponse.getAttributeAsVector("M_GetProtocollazione.ROWS.ROW");
			  		if(rowsProt != null && !rowsProt.isEmpty()) { 
						rowProt = (SourceBean) rowsProt.elementAt(0);
				   		numProtV = SourceBeanUtils.getAttrBigDecimal(rowProt, "NUMPROTOCOLLO", null);
			   			numAnnoProtV = (BigDecimal) rowProt.getAttribute("NUMANNOPROT");
	   					dataOraProt = (String) rowProt.getAttribute("DATAORAPROT");
	  					oraProtV = dataOraProt.substring(11,16);
	  					datProtV = dataOraProt.substring(0,10);
	  				}
			  		%>
			  		document.Frm1.oraProt.value = "<%=oraProtV%>";
					document.Frm1.dataProt.value= "<%=datProtV%>"; 
					document.Frm1.numProtocollo.value="<%=numProtV%>";  
					document.Frm1.numAnnoProt.value= "<%=numAnnoProtV%>";
					document.Frm1.dataOraProt.value="<%=dataOraProt%>"; 
			  	}
			  	if (document.Frm1.CODSTATOATTO_P.value == "AN" || document.Frm1.CODSTATOATTO_P.value == "NP") {
					document.Frm1.numAnnoProt.value = "";
			  		document.Frm1.numProtocollo.value = "";
			  		document.Frm1.dataProt.value = "";
			  		document.Frm1.oraProt.value = "";
			  		document.Frm1.dataOraProt.value = "";
				}
				<%}%>		 	
			 }
			 
		    </script>
		    		
		<table class="main" border="0">
		      <tr><td colspan="2">&nbsp;</td></tr>	        	        	        
				<tr>
				    <td class="etichetta">Stato richiesta</td>
				    <td class="campo">
				      <af:comboBox title="Stato richiesta" name="CODSTATORICHIESTA" moduleName="M_COMBO_STATO_RICH_SOSP" addBlank="false" selectedValue="<%=CODSTATORICHIESTA%>" disabled="<%= fieldCodRich %>" classNameBase="input" onChange="fieldChanged()"/>&nbsp;*&nbsp;
				      &nbsp;&nbsp;&nbsp;&nbsp;   
				      Data richiesta&nbsp;&nbsp;
				      <af:textBox title="Data richiesta" validateOnPost="true" disabled="false" type="date" name="DATRICHIESTA" value="<%=DATRICHIESTA%>" size="12" maxlength="12" readonly="<%=fieldReadOnly%>" classNameBase="input" />&nbsp;*&nbsp;
				      
				    </td>
				<tr>
		    	<tr >
		    		<td class="etichetta">Motivo della sospensione</td>
				    <td class="campo">
				      <af:comboBox title="Motivo di sospensione" name="CODMOTIVOSOSP" moduleName="M_ComboMotivi" addBlank="true" selectedValue="<%=CODMOTIVOSOSP%>" disabled="<%= fieldReadOnly %>" classNameBase="input" onChange="fieldChanged()"/>&nbsp;*&nbsp;
				      
				    </td>	
	    		</tr>				        
		        <tr>
		          <td class="etichetta">Azienda in possesso del provvedimento&nbsp;</td>	
				  <td class="campo">
					    <af:comboBox 
					      name="FLGPROVVEDIMENTO"
					      classNameBase="input"
					      disabled="<%= fieldReadOnly %>"
					      onChange="javaScript:fieldChanged(); ">
					      <option value=""  <% if ( "".equalsIgnoreCase(FLGPROVVEDIMENTO) )  { %>SELECTED="true"<% } %> ></option>
					      <option value="S" <% if ( "S".equalsIgnoreCase(FLGPROVVEDIMENTO) ) { %>SELECTED="true"<% } %> >Sì</option>
					      <option value="N" <% if ( "N".equalsIgnoreCase(FLGPROVVEDIMENTO) ) { %>SELECTED="true"<% } %> >No</option>
					    </af:comboBox>
				  </td>
		        </tr>
		        <tr>
		          <td class="etichetta">Proroga&nbsp;</td>	
				  <td class="campo">
					    <af:comboBox 
					      name="FLGPROROGA"
					      classNameBase="input"
					      disabled="<%= fieldReadOnly %>"
					      onChange="javaScript:fieldChanged(); ">
					      <option value=""  <% if ( "".equalsIgnoreCase(FLGPROROGA) )  { %>SELECTED="true"<% } %> ></option>
					      <option value="S" <% if ( "S".equalsIgnoreCase(FLGPROROGA) ) { %>SELECTED="true"<% } %> >Sì</option>
					      <option value="N" <% if ( "N".equalsIgnoreCase(FLGPROROGA) ) { %>SELECTED="true"<% } %> >No</option>
					    </af:comboBox>
				  </td>
		        </tr>
				<tr>
				    <td class="etichetta">Data inizio sosp.&nbsp;</td>
				    <td class="campo">
				      <af:textBox validateOnPost="true" disabled="false" type="date" name="DATINIZIOSOSP" value="<%=DATINIZIOSOSP%>" size="12" maxlength="12" readonly="<%=fieldReadOnly%>" classNameBase="input" />
				      &nbsp;&nbsp;&nbsp;&nbsp;
				      Data fine sosp.&nbsp;
				      <af:textBox validateOnPost="true" disabled="false" type="date" name="DATFINESOSP" value="<%=DATFINESOSP%>" size="12" maxlength="12" readonly="<%=fieldReadOnly%>" classNameBase="input" />
				    </td>
				<tr>
				<tr>
				    <td class="etichetta">Data fine temporanea&nbsp;</td>
				    <td class="campo">
				      <af:textBox validateOnPost="true" disabled="false" type="date" name="DATFINETEMPORANEA" value="<%=DATFINETEMPORANEA%>" size="12" maxlength="12" readonly="<%=fieldReadOnly%>" classNameBase="input" />
				    </td>
				<tr>
		        <tr>
		          <td colspan="2">&nbsp;</td>
		        </tr>  				
		        <tr>
		          <td colspan="2">&nbsp;</td>
		        </tr>  				
		        <tr>
		          <td colspan="2" align="center">
			          <% if (canInsert) {
			          	  if(nuovaRichiesta){
			          	  %>
				          	<input class="pulsanti" type="submit" name="inserisci" value="Inserisci">
				          	&nbsp;&nbsp;
				          	<input type="reset" class="pulsanti" value="Annulla"/>
				          	&nbsp;&nbsp;			          	

				          	<input type="hidden" name="inserisciNew" value="1">
				          	<input type="hidden" name="NUMKLOCOMPTERR" value="1">	
			          <%  }
			          	 }
			          	 if (canModify) { 
			          	  if(!nuovaRichiesta && !CODSTATOATTO_P.equals("AN")) {%>
			          	  	<input class="pulsanti" type="submit" name="aggiorna" value="Aggiorna" onClick="annullamentoDoc()">
				          	&nbsp;&nbsp;
				          	<input type="reset" class="pulsanti" value="Annulla"/>
			          	  	&nbsp;&nbsp;
							<input class="pulsanti" type="button" name="sedi" value="Sedi coinvolte" onclick="SediCoinv();">
			          	  	&nbsp;&nbsp;
			          	  	
			          	  	<input type="hidden" name="aggiornamento" value="1">
			          	  	<input type="hidden" name="NUMKLOSOSPENSIONE" value="<%= NUMKLOSOSPENSIONE %>">
			          	  	<input type="hidden" name="annullamento" value="0">
			          	  <%}
					  	 }%>  
					  <input type="hidden" name="aggProsp" value="0" />
					  <input type="hidden" name="prgUnita" value="<%=prgUnita%>" />	 
			          <input type="button" class="pulsante" name="ricerca" value="<%=goBackButtonTitle%>" onclick="goBack()" />						  	
			       </td>
			     </tr>				
			   </table>
	           <br><br>

	    <%= htmlStreamBottom2%> 
	    <%
	    if(!nuovaRichiesta){%>    
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
