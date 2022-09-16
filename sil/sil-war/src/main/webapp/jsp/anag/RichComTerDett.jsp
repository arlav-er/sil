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
                  java.text.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*,
                  it.eng.sil.util.GiorniNL,
                  it.eng.sil.module.agenda.*,
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
  String NUMKLOCOMPTERR = null;

  String  STRRAGIONESOCIALE = null;
  String  STRCODICEFISCALE = null;
  String  STRPARTITAIVA = null;
  String  CODSTATORICHIESTA = null;
  String  DATRICHIESTA = null;
  String  DATFINE = null;
  String  prgAzienda = null;
  String  PRGRICHCOMPTERR = null;
  String  STRMOTIVAZIONE = null;
  
  String htmlStreamTop2 = "";
  String htmlStreamBottom2 = "";

  boolean nuovaRichiesta = !(serviceRequest.getAttribute("nuovaRichiesta")==null || 
                            ((String)serviceRequest.getAttribute("nuovaRichiesta")).length()==0);
                            
  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page); 

  PageAttribs attributi = new PageAttribs(user, _page); 

  boolean canModify = false;
  boolean canModifyProv = false;
  boolean canInsert = false; 
  boolean canSalvaStato = false;
  
  if (! filter.canView()){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  } else {
  		canModify = attributi.containsButton("aggiorna");
  		canModifyProv = attributi.containsButton("aggiorna");
  		canInsert = attributi.containsButton("INSERISCI"); 
  		canSalvaStato = attributi.containsButton("SALVA-STATO");
  }

  canInsert = canInsert && canSalvaStato;
  canModify = canModify && canSalvaStato;
  canModifyProv = canModifyProv && canSalvaStato;
  
  boolean readOnlyStr = !canModify;
  boolean readOnlyStrProv = !canModifyProv;
  String fieldReadOnly = canModify ? "false" : "true"; 
  String fieldReadOnlyProv = canModifyProv ? "false" : "true"; 
  boolean disableCodRich = false || readOnlyStr;
  String fieldCodRich = disableCodRich ? "true" : "false";

  String fromRicerca = StringUtils.getAttributeStrNotNull(serviceRequest,"fromRicerca");

  prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest,"prgAzienda");
  String prgUnita = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgUnita");
  PRGRICHCOMPTERR = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PRGRICHCOMPTERR");

  String inserisciNewFromRicerca = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "inserisciNewFromRicerca");

  String listaProv = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "listaProv");
  String newDettProvPage = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "newDettProvPage");
  String inserisciDettProv = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "inserisciDettProv");  
  String eliminaDettProv = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "eliminaDettProv");    
  String dettaglioDettProv = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "dettaglioDettProv");    
  String aggiornamentoDettProv = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "aggiornamentoDettProv");    

  String goBackListPage = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "goBackListPage");
    
//----------------------------------------------------------------------------------------------------
  SourceBean rowRich = null;
  SourceBean rowDoc = null;
  Vector rowsDoc      = null;  
  String prAutomatica     = "S";
  String docInOut         = "I";
  String docRif           = "Documentazione L68";
  String docTipo          = "Richiesta di Compen. Terr.";
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
	  rowDoc = (SourceBean) serviceResponse.getAttribute("M_Load_Doc_ComTer.ROWS.ROW");
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

	  
  if(nuovaRichiesta){ 
	  
	  //prgAzienda = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgAzienda");
	  STRRAGIONESOCIALE = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"STRRAGIONESOCIALE");
	  STRCODICEFISCALE = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"STRCODICEFISCALE");
	  STRPARTITAIVA = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"STRPARTITAIVA");
	  CODSTATORICHIESTA = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"CODSTATORICHIESTA");
      DATRICHIESTA = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"DATRICHIESTA");
      DATFINE = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"DATFINE");
      STRMOTIVAZIONE = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"STRMOTIVAZIONE");

	  cdnUtIns = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
	  cdnUtMod = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
	  
	  if(!prgAzienda.equals("") && prgAzienda != null){
		  SourceBean rowAz = null;
		  Vector rowsAz= serviceResponse.getAttributeAsVector("M_Load_Azienda.ROWS.ROW");
		  if (rowsAz.size()==1) {
		        rowAz = (SourceBean)rowsAz.get(0);
				
			    STRRAGIONESOCIALE = Utils.notNull(rowAz.getAttribute("STRRAGIONESOCIALE"));		    
			    STRCODICEFISCALE = Utils.notNull(rowAz.getAttribute("STRCODICEFISCALE")); 
			    STRPARTITAIVA = Utils.notNull(rowAz.getAttribute("STRPARTITAIVA"));  
		  }	  	
	  }
	
  }else{
	          
	  SourceBean row = null;
	  Vector rows= serviceResponse.getAttributeAsVector("M_Load_RichComTer.ROWS.ROW");
	  // siamo in dettaglio per cui avro' al massimo una riga
	  if (rows.size()==1) {
	        row = (SourceBean)rows.get(0);
			
			PRGRICHCOMPTERR = Utils.notNull(row.getAttribute("PRGRICHCOMPTERR"));
		    prgAzienda = Utils.notNull(row.getAttribute("prgAzienda")); 
		    STRRAGIONESOCIALE = Utils.notNull(row.getAttribute("STRRAGIONESOCIALE"));		    
		    STRCODICEFISCALE = Utils.notNull(row.getAttribute("STRCODICEFISCALE")); 
		    STRPARTITAIVA = Utils.notNull(row.getAttribute("STRPARTITAIVA"));  
		   
		    CODSTATORICHIESTA = Utils.notNull(row.getAttribute("CODSTATORICHIESTA"));  
		    DATRICHIESTA = Utils.notNull(row.getAttribute("DATRICHIESTA"));  
		    DATFINE = Utils.notNull(row.getAttribute("DATFINE"));  
		    STRMOTIVAZIONE = Utils.notNull(row.getAttribute("STRMOTIVAZIONE")); 
	        
        	NUMKLOCOMPTERR = String.valueOf(((BigDecimal)row.getAttribute("NUMKLOCOMPTERR")).intValue());
	        
	        cdnUtIns = (BigDecimal) row.getAttribute("CDNUTINS");
	        dtmIns = (String) row.getAttribute("DTMINS");

		    cdnUtMod = (BigDecimal) row.getAttribute("CDNUTMOD");
		    	    
	        dtmMod = (String) row.getAttribute("DTMMOD");
	        operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
	  }

  }  
  
  /*------------------------ inserire il parametro cdnFunzione ---------------------------*/
  _funzione = Integer.parseInt(StringUtils.getAttributeStrNotNull(serviceRequest,"CDNFUNZIONE"));


  String statoRichiesta = null;
  SourceBean row2 = null;
  if(!nuovaRichiesta){
	  Vector rows2= serviceResponse.getAttributeAsVector("M_Load_RichComTer.ROWS.ROW");
	  if (rows2.size()==1) {
	        row2 = (SourceBean)rows2.get(0);			
		    statoRichiesta = Utils.notNull(row2.getAttribute("CODSTATORICHIESTA")); 
	  }
	  if(statoRichiesta.equals("AN")){
	  		canModify = false;
	  		canModifyProv = false;
	  		fieldReadOnly = "true";
	  		canInsert = false;
	  		noButton = "true";
	  		fieldCodRich = "true";
	  }
  } else {
		noButton = "true";
		fieldCodRich = "true";
  }

	
  if (("PR").equalsIgnoreCase(CODSTATOATTO_P)) {
  		canModify = true;
  		canModifyProv = false;
	  	fieldReadOnly = "true";	
  }

  //Servono per gestire il layout grafico
  String htmlStreamTop = StyleUtils.roundTopTable(true);
  String htmlStreamBottom = StyleUtils.roundBottomTable(true);

%>

<html>

<head>

    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/> 
    <SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>   
    <af:linkScript path="../../js/"/>
    
	<script language="JavaScript">
          
          var flagChanged = false;
        
          function fieldChanged() {
           <% if (!readOnlyStr){ %> 
              flagChanged = true;
           <%}%> 
          }

          <% boolean baseComputoChanged = false; %> 
          function baseComputoChanged() {
              <% baseComputoChanged = true; %>
          }
          
          <% boolean percEsoneroChanged = false; %>
          function percEsoneroChanged() {
              <% percEsoneroChanged = true; %>
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
			  urlpage+="PAGE=CMRichCompTerRicercaPage&prgAzienda=<%=prgAzienda%>";
			  setWindowLocation(urlpage);
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

		  function dataDiOggi(){
	  	  	  var dataDiOggi = new Date();
			  var giornoOggi=dataDiOggi.getDate().toString();
			  var meseOggi=(dataDiOggi.getMonth() +1).toString();			
			  if(giornoOggi.length == 1){
				  giornoOggi = '0' + giornoOggi;
			  }
			  if(meseOggi.length == 1){
				  meseOggi = '0' + meseOggi;	 	
			  }
			  dataDiOggi = giornoOggi + '/' + meseOggi + '/' + dataDiOggi.getFullYear().toString();
			  return dataDiOggi;
		  }
		  
		  function controllaDati(){
		  	var dataInizioSosp = document.Frm1.DATRICHIESTA;
		  	var dataFineSosp = document.Frm1.DATFINE;
 			if(dataInizioSosp.value != "" && dataFineSosp.value!= ""){
    			if (compareDate(dataInizioSosp.value,dataFineSosp.value) >= 0) {
      				alert("La " + dataFineSosp.title + " deve essere maggiore della " + dataInizioSosp.title);
      				return false;
	  			}
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
		 		  		 
		 		f = "AdapterHTTP?PAGE=CMRichCTDettPage&aggiornaDoc=1";
		 		f = f + "&CDNFUNZIONE=<%=_funzione%>";
				f = f + "&PRGRICHCOMPTERR=<%=PRGRICHCOMPTERR%>";
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
				 	f = f + "&NUMKLOCOMPTERR=<%=NUMKLOCOMPTERR%>";
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
	      		if(goBackListPage.equals("CMRichComTerListaPage")){
	      			goBackButtonTitle = "Torna alla lista";
	      			%>
	      			setWindowLocation("AdapterHTTP?<%= StringUtils.formatValue4Javascript(goBackListUrl) %>");
	      			<%
	      		}else{
	      			goBackButtonTitle = "Torna alla ricerca";
	      			%>
	      			f = "AdapterHTTP?PAGE=CMRichCompTerRicercaPage";
			 		f = f + "&CDNFUNZIONE=<%=_funzione%>";
					f = f + "&PRGRICHCOMPTERR=<%=PRGRICHCOMPTERR%>";      			
	      			document.location = f;
	      			<%
	      		}
				%>
							
		 }

		 function selezionaAzienda()
	     {
			var url = "AdapterHTTP?PAGE=IdoSelezionaAziendaPage&AGG_FUNZ=riempiDatiAzienda&cdnFunzione=<%=_funzione%>";
			var feat = "toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes," +
					"width=600,height=500,top=50,left=100";
	    	opened = window.open(url, "_blank", feat);
	    	opened.focus();
	     }
	    
	     function riempiDatiAzienda(prgAzienda, strCodiceFiscale, strPartitaIva, strRagioneSociale) {	
			document.Frm1.PRGAZIENDA.value = prgAzienda;
			document.Frm1.STRCODICEFISCALE.value = strCodiceFiscale;
			document.Frm1.STRPARTITAIVA.value = strPartitaIva;
			document.Frm1.STRRAGIONESOCIALE.value = strRagioneSociale;
			opened.close();
		 }
		 
		 function dettaglioProv(){
			  var f;
 		      f = "AdapterHTTP?PAGE=CMRichCTDettPage&listaProv=1";
 		      f = f + "&CDNFUNZIONE=<%=_funzione%>";
			  f = f + "&PRGRICHCOMPTERR=<%=PRGRICHCOMPTERR%>";
			  f = f + "&prgAzienda=<%=prgAzienda%>";
			  f = f + "&goBackListPage=<%=goBackListPage%>";
			  
			  var t = "_blank";
			  var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=600,height=500,top=100,left=100";
 		  	  window.open(f, t, feat);		  		 	
		 }
		 
		 function indietroListaDettProv(){
			  var f;
 		      f = "AdapterHTTP?PAGE=CMRichCTDettPage&listaProv=1";
 		      f = f + "&CDNFUNZIONE=<%=_funzione%>";
			  f = f + "&PRGRICHCOMPTERR=<%=PRGRICHCOMPTERR%>";
			  f = f + "&prgAzienda=<%=prgAzienda%>";
			  f = f + "&goBackListPage=<%=goBackListPage%>";
			  
		 	  document.location = f;
		 }
		 
	</script>

	<script language="Javascript">
	
		<% if(prgUnita.equals("")) {
	   		SourceBean rowPrg = (SourceBean) serviceResponse.getAttribute("M_GetPrgUnita.ROWS.ROW");
 	   		if(rowPrg != null) prgUnita = SourceBeanUtils.getAttrStrNotNull(rowPrg, "prgUnita");
 	   	}
 	   	
 	   	boolean isInDettProv = listaProv.equals("1") || inserisciDettProv.equals("1") || eliminaDettProv.equals("1") 
 	   					|| newDettProvPage.equals("1") || dettaglioDettProv.equals("1") || aggiornamentoDettProv.equals("1");
 	   	
 	   	if (prgAzienda!=null && (prgUnita!=null && !prgUnita.equals("")) && !isInDettProv) { %>
			window.top.menu.caricaMenuAzienda(<%=_funzione%>, <%=prgAzienda%>, <%=prgUnita%>);
		<% } %>
	
	</script>

	<script language="Javascript" src="../../js/docAssocia.js"></script>
		
</head>


<body class="gestione" onload="rinfresca();">

	<%if(listaProv.equals("1") || inserisciDettProv.equals("1") || eliminaDettProv.equals("1")){ %>
		
		<af:list moduleName="M_List_Dett_Prov" 
		       canDelete="<%= canModifyProv ? \"1\" : \"0\" %>" 
		       canInsert="<%= canModifyProv ? \"1\" : \"0\" %>"   />

		<table align="center">
			<tr>
			   <td align="center">
		          <input type="button" onclick="window.close()" value="Chiudi" class="pulsanti">	         
			   </td>
			</tr>		          		    
		</table>		
	<%}else if( newDettProvPage.equals("1") || dettaglioDettProv.equals("1") || aggiornamentoDettProv.equals("1") ){%>
		<br><br>
		
		<%
		String CODPROVINCIA = "";
		String CODMONOECCDIF = "";
		String NUMINTERESSATI = "";
		String CODMONOCATEGORIA = "";
		String PRGCOMPTERRDETT = "";
		
		if( dettaglioDettProv.equals("1") || aggiornamentoDettProv.equals("1") ){
			  SourceBean rowDettProv = null;
			  Vector rowsDettProv= serviceResponse.getAttributeAsVector("M_Load_DettProv.ROWS.ROW");
			  if (rowsDettProv.size()==1) {
			        rowDettProv = (SourceBean)rowsDettProv.get(0);
					
				    CODPROVINCIA = Utils.notNull(rowDettProv.getAttribute("CODPROVINCIA"));
				    CODMONOECCDIF = Utils.notNull(rowDettProv.getAttribute("CODMONOECCDIF"));
				    NUMINTERESSATI = Utils.notNull(rowDettProv.getAttribute("NUMINTERESSATI"));
				    CODMONOCATEGORIA = Utils.notNull(rowDettProv.getAttribute("CODMONOCATEGORIA"));
				    PRGCOMPTERRDETT = Utils.notNull(rowDettProv.getAttribute("PRGCOMPTERRDETT"));    
			  }			
		%>
		<table class="main" border="0">
		   	<tr>
			    <td colspan = "2">
			      <p class="titolo">Dettaglio provincia</p>
			    </td>	
	    	</tr>
	    </table>
		
		<%}else{%>
		
		<table class="main" border="0">
		   	<tr>
			    <td colspan = "2">
			      <p class="titolo">Nuovo dettaglio provincia</p>
			    </td>	
	    	</tr>
	    </table>
		<%}%>

		
		<af:showMessages prefix="M_Save_DettProv"/>		
		<af:showErrors />
		<af:error />			

		<af:form method="POST" action="AdapterHTTP" name="Frm2">
			
			<input type="hidden" name="PAGE" value="CMRichCTDettPage" />		
			<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
			
			<input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"> 			
			<input type="hidden" name="PRGRICHCOMPTERR" value="<%=PRGRICHCOMPTERR%>">
			<input type="hidden" name="PRGCOMPTERRDETT" value="<%=PRGCOMPTERRDETT%>">
			<input type="hidden" name="goBackListPage" value="<%=goBackListPage%>" />

<%
  			htmlStreamTop = StyleUtils.roundTopTable(canModifyProv);
  			htmlStreamBottom = StyleUtils.roundBottomTable(canModifyProv);
%>
			
	    	<%= htmlStreamTop %>

		    <table class="main" border="0">
		    	<tr>
		    		<td colspan="2">&nbsp;</td>
		    	</tr>
		    	<tr>
			        <td class="etichetta">Provincia</td>
				    <td class="campo">
				      <af:comboBox title="Provincia" name="CODPROVINCIA" moduleName="M_COMBO_PROVINCIA" addBlank="true" selectedValue="<%=CODPROVINCIA%>" disabled="<%= String.valueOf(!canModifyProv) %>" classNameBase="input" onChange="fieldChanged()"/>
					  <%if (canModifyProv) { %>
					  &nbsp;*&nbsp;
					  <%}%>
				      <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('CODPROVINCIA')"; </SCRIPT>
				    </td>	
	    		</tr>   
		    	<tr>
			        <td class="etichetta">Ecc/Rid</td>
				    <td class="campo">
					    <af:comboBox 
					      name="CODMONOECCDIF"
					      title="Ecc/Rid"
					      classNameBase="input"
					      disabled="<%= String.valueOf(!canModifyProv) %>"
					      onChange="javaScript: fieldChanged(); ">
					      <option value=""  <% if ( "".equalsIgnoreCase(CODMONOECCDIF) )  { %>SELECTED="true"<% } %> ></option>
					      <option value="E" <% if ( "E".equalsIgnoreCase(CODMONOECCDIF) ) { %>SELECTED="true"<% } %> >Eccesso</option>
					      <option value="D" <% if ( "D".equalsIgnoreCase(CODMONOECCDIF) ) { %>SELECTED="true"<% } %> >Difetto</option>
					    </af:comboBox>
					    <%if (canModifyProv) { %>
					    &nbsp;*&nbsp;
					    <%}%>
					    <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('CODMONOECCDIF')"; </SCRIPT>   
				    </td>
	    		</tr>   
		    	<tr>
			        <td class="etichetta">Num.</td>
				    <td class="campo">
				      <af:textBox title="Num." value="<%=NUMINTERESSATI%>" classNameBase="input" name="NUMINTERESSATI" maxlength="9" 
				      			size="12" readonly="<%=String.valueOf(!canModifyProv)%>" type="integer" validateOnPost="true" />
					  <%if (canModifyProv) { %>
					  &nbsp;
					  <%}%>
				   </td>	
	    		</tr>   
		    	<tr>
			        <td class="etichetta">D/A</td>
				    <td class="campo">
					    <af:comboBox 
					      name="CODMONOCATEGORIA"
					      title="D/A"
					      classNameBase="input"
					      disabled="<%= String.valueOf(!canModifyProv) %>"
					      onChange="javaScript: fieldChanged(); ">
					      <option value="" <% if ( "".equalsIgnoreCase(CODMONOCATEGORIA) )  { %>SELECTED="true"<% } %> ></option>
					      <option value="D" <% if ( "D".equalsIgnoreCase(CODMONOCATEGORIA) ) { %>SELECTED="true"<% } %> >Disabile</option>
					      <option value="A" <% if ( "A".equalsIgnoreCase(CODMONOCATEGORIA) ) { %>SELECTED="true"<% } %> >Categoria protetta ex art. 18</option>
					    </af:comboBox>
					    <%if (canModifyProv) { %>
					    &nbsp;*&nbsp;
					    <%}%>
					    <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('CODMONOCATEGORIA')"; </SCRIPT>    
				    </td>
	    		</tr>
		    	<tr>
		    		<td colspan="2">&nbsp;</td>
		    	</tr>
		     </table>
		     <%= htmlStreamBottom %>
		     <table class="main" border="0">
		    	<%if (canModifyProv) { %>
		    	<tr>
				    <td colspan="2" align="center">
				      <%if(newDettProvPage.equals("1")){%>
				      	<input class="pulsanti" type="submit" name="inserisci" value="Inserisci" onClick="">
				      	<input type="hidden" name="inserisciDettProv" value="1">
				      <%}else{%>
				      	<input class="pulsanti" type="submit" name="aggiorna" value="Aggiorna" onClick="">
				      	<input type="hidden" name="aggiornamentoDettProv" value="1">
				      <%}%>
		          	  <input type="reset" class="pulsanti" value="Annulla" onClick=""/>
				    </td>
	    		</tr>
	    		<%}%>
		    	<tr>
				    <td colspan="2" align="center">
				      <input type="button" class="pulsanti" value="Torna alla lista" onClick="indietroListaDettProv()">
				    </td>
	    		</tr>	    		
	        </table>	       	         	
		</af:form>

	<%}else{%>

		<%if(nuovaRichiesta){ %>
	    	<p class="titolo">Nuova richiesta di compensazione territoriale</p>
		<%}else{%>
	    	<p class="titolo">Richiesta di compensazione territoriale</p>
		<%}%>
	
		<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="controllaDati()">
	
			<input type="hidden" name="PAGE" value="CMRichCTDettPage" />		
			<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>				
			<input type="hidden" name="CDNUTINS" value="<%=cdnUtIns%>">
			<input type="hidden" name="CDNUTMOD" value="<%=cdnUtMod%>">
			<input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"> 
			<input type="hidden" name="PRGRICHCOMPTERR" value="<%=PRGRICHCOMPTERR%>">
			<input type="hidden" name="strChiaveTabella" value="<%=PRGRICHCOMPTERR%>" />
			<input type="hidden" name="goBackListPage" value="<%=goBackListPage%>" />
	
		    <div id="divLookAzi_look" style="display:">
<%
  //Oggetti per l'applicazione dello stile
  htmlStreamTop = StyleUtils.roundTopTable(isInsert);
  htmlStreamBottom = StyleUtils.roundBottomTable(isInsert);
%>
				<span class="sezioneMinimale">
					Azienda&nbsp;
					<% if (prgAzienda == "" || prgAzienda.equals("")) { %>
					*&nbsp;<a href="#" onClick="selezionaAzienda();return false"><img src="../../img/binocolo.gif" alt="Cerca"></a>
					<% } %>
				</span>
				<%= htmlStreamTop %>
				<table class="main" width="100%">
					<tr valign="top">
						<td class="etichetta">Codice Fiscale</td>
						<td class="campo">
							<input title="Azienda" type="text" name="STRCODICEFISCALE" class="inputView" readonly="true" value="<%=STRCODICEFISCALE%>" size="30" maxlength="16"/>
							<SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('STRCODICEFISCALE')"; </SCRIPT>
						</td>
					</tr>
					<tr valign="top">
						<td class="etichetta">Partita IVA</td>
						<td class="campo">
							<input type="text" name="STRPARTITAIVA" class="inputView" readonly="true" value="<%=STRPARTITAIVA%>" size="30" maxlength="30"/>
						</td>
					</tr>
					<tr valign="top">
						<td class="etichetta">Ragione Sociale</td>
						<td class="campo">
							<input type="text" name="STRRAGIONESOCIALE" class="inputView" readonly="true" value="<%=STRRAGIONESOCIALE%>" size="75" maxlength="120"/>
						</td>
					</tr>
				</table>
				<%= htmlStreamBottom %>
			</div>

<%
  htmlStreamTop2 = StyleUtils.roundTopTable(isInsert || fieldReadOnly.equals("false"));
  htmlStreamBottom2 = StyleUtils.roundBottomTable(isInsert || fieldReadOnly.equals("false"));
%>			

			<af:showMessages prefix="M_Insert_RichComTer"/>
			<af:showMessages prefix="M_Save_RichComTer"/>	
			<af:showMessages prefix="SalvaRichComTerDoc"/>
			<af:showMessages prefix="M_Annullamento_RichCT_E_Doc"/>				
			<af:showErrors />
			<af:error />			
	
			<%= htmlStreamTop2 %>
			<%@ include file="_protocollazionerichiestacompterr.inc" %>
				
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
					      <af:comboBox title="Stato richiesta" name="CODSTATORICHIESTA" moduleName="M_COMBO_STATO_RICH_CT" addBlank="false" selectedValue="<%=CODSTATORICHIESTA%>" disabled="<%= fieldCodRich %>" classNameBase="input" onChange="fieldChanged()"/>&nbsp;*&nbsp;
					      <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('CODSTATORICHIESTA')"; </SCRIPT>
					    </td>
					<tr>
			        <tr><td colspan="2">&nbsp;</td></tr>				
					<tr>
					    <td class="etichetta">Data richiesta&nbsp;</td>
					    <td class="campo">
					      <af:textBox title="Data richiesta" validateOnPost="true" disabled="false" type="date" name="DATRICHIESTA" value="<%=DATRICHIESTA%>" size="12" maxlength="12" readonly="<%=fieldReadOnly%>" classNameBase="input" />&nbsp;*&nbsp;
					      <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('DATRICHIESTA')"; </SCRIPT>
						  &nbsp;&nbsp;&nbsp;&nbsp;
						  Data fine&nbsp;&nbsp;
					      <af:textBox validateOnPost="true" title="Data fine" disabled="false" type="date" name="DATFINE" value="<%=DATFINE%>" size="12" maxlength="12" readonly="<%=fieldReadOnly%>" classNameBase="input" />
					    </td>
					<tr>
					<tr>
					    <td class="etichetta">Motivazione&nbsp;</td>
					    <td class="campo">
				          	<af:textArea title="Motivazione" cols="60" rows="4" maxlength="2000" readonly="<%=fieldReadOnly%>" classNameBase="input"  
		                		name="STRMOTIVAZIONE" 
		                		value="<%=STRMOTIVAZIONE%>" validateOnPost="true" 
		                        required="false" onKeyUp="fieldChanged();"/>
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
				          					          	
				          	<%if(fromRicerca.equals("1")){%>
					          	<input type="hidden" name="inserisciNewFromRicerca" value="1">
				          	<%}%>

				          	<input type="hidden" name="inserisciNew" value="1">
				          	<input type="hidden" name="NUMKLOCOMPTERR" value="1">	
			          <%  }
			          	 }
			          	 if (canModify) { 
			          	  if(!nuovaRichiesta){
			          	  %>
			          	  	<input class="pulsanti" type="submit" name="aggiorna" value="Aggiorna" onClick="annullamentoDoc()">
				          	&nbsp;&nbsp;
				          	<input type="reset" class="pulsanti" value="Annulla"/>
			          	  	&nbsp;&nbsp;
			          	  	
			          	  	<input type="hidden" name="aggiornamento" value="1">
			          	  	<input type="hidden" name="NUMKLOCOMPTERR" value="<%= NUMKLOCOMPTERR %>">
			          	  	<input type="hidden" name="annullamento" value="0">
			          	  	
					  <%  }
					  	 }%>
					  <input type="hidden" name="prgUnita" value="<%=prgUnita%>" />
			          <input type="button" class="pulsante" name="ricerca" value="<%=goBackButtonTitle%>" onclick="goBack()" />
			          <% if(!nuovaRichiesta){ %>			          	  		          	  	
			          &nbsp;&nbsp;
					  <input class="pulsanti" type="button" name="dettProv" value="Dettaglio province" onclick="dettaglioProv()">
					  <% } %>						  	
			          </td>
			        </tr>				
			      </table>
	              <br><br>
	
		    <%= htmlStreamBottom2 %> 
		    <% if(!nuovaRichiesta){ %>    
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
	<% } %>
</body>
</html>
