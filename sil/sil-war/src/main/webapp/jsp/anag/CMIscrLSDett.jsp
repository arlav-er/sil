<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
                  com.engiweb.framework.tracing.*,
                  com.engiweb.framework.util.*,
                  it.eng.sil.module.movimenti.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.bean.*,
                  it.eng.sil.module.anag.*,
                  it.eng.sil.*,
                  java.util.*,
                  java.text.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>
                  
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
//Servono per gestire il layout grafico
  
  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page);
  String _funzione =(String) serviceRequest.getAttribute("CDNFUNZIONE");
  boolean isInsert = true;
  SourceBean rowRich = null;
  SourceBean rowDoc = null;
  Vector rowsDoc      = null;  
  String  prgAzienda = null;
  String  cdnLavoratore = null;
  String  prgUnita = null;
  
  PageAttribs attributi = new PageAttribs(user, _page);
  
  boolean canInsert=false;
  boolean canUpdate=false;
  boolean canDocAss=false;
  boolean canSalvaStato=false;

   boolean canModify = true;
  
  if (! filter.canView()){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	} else {
		
		canInsert = attributi.containsButton("INSERISCI");
		canUpdate = attributi.containsButton("AGGIORNA");
		canSalvaStato = attributi.containsButton("SALVA-STATO");
	}
	
  boolean readOnlyStr = !canUpdate;
  
  boolean nuovaIscrizione = (!(serviceRequest.getAttribute("inserisci")==null || 
                            ((String)serviceRequest.getAttribute("inserisci")).length()==0) && 
                            !(serviceResponse.containsAttribute("M_INSERTISCRLISTE.PRGISCRART1")) );
  
  if (nuovaIscrizione){
  	if (!canInsert){
  		canSalvaStato = canInsert;
  	}
  	else if (!canSalvaStato){
  		canInsert = canSalvaStato;
  	} 
  }
  
  String prAutomatica     = "S";
  String docInOut         = "I";
  String docRif           = "Documentazione L68";
  String docTipo          = "ISCRIZIONE LISTE SPECIALI ART. 1";
  BigDecimal numProtV     = null;
  BigDecimal numAnnoProtV = null;
  String dataOraProt      = "";
  boolean noButton = false; 
  String datProtV     = "";
  String oraProtV     = "";
  Vector rowsProt     = null;
  SourceBean rowProt  = null;
  String CODSTATOATTO = "NP";
  BigDecimal numAnnoProt = null;
  BigDecimal numProtocollo = null;
  String dataProt = "";
  String DatAcqRil = "";
  String DatInizio = "";
  String oraProt = "";
	String PROVINCIA_ISCR = serviceRequest
			.getAttribute("PROVINCIA_ISCR") == null ? ""
			: (String) serviceRequest
			.getAttribute("PROVINCIA_ISCR");
	String COD_PROVINCIA_ISCR = serviceRequest
			.getAttribute("COD_PROVINCIA_ISCR") == null ? ""
			: (String) serviceRequest
			.getAttribute("COD_PROVINCIA_ISCR");
  
  cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "cdnLavoratore");
  String prgIscrArt1 = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGISCRART1");
  //CODSTATOATTO = StringUtils.getAttributeStrNotNull(serviceRequest, "CODSTATOATTO");
  
  String valDisplay="inline";
 
  
  String CODTIPOLISTA = "";
  String STRNUMALBO = "";
  String DATISCRALBO = "";
  String DATISCRLISTAPROV = "";
  BigDecimal NUMMESISOSP = null;
  String numMesiSospStr = null;
 
  String NumPunteggioStr = null;
  BigDecimal NUMANNOPUNTEGGIO = null;
  String NumAnnoPunteggioStr = null;
  String DATMODIFPUNTEGGIO = "";
  String CDNUTPUNTEGGIO = "";
  String CdnUtPunteggioStr = null;
  
  String dtmIns = "";
  String cdnUtMod = "";
  String dtmMod = "";
  String cdnUtIns = "";
  
  BigDecimal PRGSPI=null;
  String PrgSpiStr=null;
  
  String DATFINE="";
  String CODMOTIVOFINEATTO="";
  String numPercInv = "";
  
  Testata operatoreInfo = null;
  boolean read = false; 
  
  String dataOdierna = "";
  
  
  String display1 = "none";
  String img0 = "../../img/chiuso.gif";
  String img1 = "../../img/aperto.gif";
  String hasDataFine = "false";
  
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
	dataOdierna = giornoDB + "/" + meseDB + "/" + annoDB;  
 
 if (nuovaIscrizione) {
 	PrgSpiStr = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "PRGSPI");
 	if(PrgSpiStr.equals("")){
  		SourceBean operatore = (SourceBean)serviceResponse.getAttribute("M_Select_PrgSPI_Da_CDNUT");
  			if ((operatore != null) && (operatore.getAttribute("ROWS.ROW.prgSpi")!=null)){
    			if(!operatore.getAttribute("ROWS.ROW.prgSpi").equals(""))
      				PrgSpiStr = operatore.getAttribute("ROWS.ROW.prgSpi").toString();
  			}
  	}
  }
  
  	Vector infoDati = serviceResponse.getAttributeAsVector("M_LoadListeSpec.ROWS.ROW");
		if(infoDati != null && !infoDati.isEmpty()) { 
			SourceBean rowInfoDati = (SourceBean) infoDati.firstElement();
			dtmIns = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "DTMINS");
			cdnUtMod = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "CDNUTMOD");
			dtmMod = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "DTMMOD");
			cdnUtIns = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "CDNUTINS");
			prgIscrArt1 = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "PRGISCRART1");
			STRNUMALBO = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "STRNUMALBO");
			DATISCRALBO = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "DATISCRALBO");
			CODTIPOLISTA = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "CODTIPOLISTA");
			DATISCRLISTAPROV = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "DATISCRLISTAPROV");
			NumPunteggioStr = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "NUMPUNTEGGIO");
			NumAnnoPunteggioStr = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "NUMANNOPUNTEGGIO");
			DATFINE = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "DATFINE");
			PrgSpiStr = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "PRGSPI");
			CDNUTPUNTEGGIO = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "CDNUTPUNTEGGIO");
			DATMODIFPUNTEGGIO = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "DATMODIFPUNTEGGIO");
			CODMOTIVOFINEATTO = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "CODMOTIVOFINEATTO");
			
			//numPercInv = rowInfoDati.getAttribute("NUMPERCINV") == null ? "" : ((BigDecimal)rowInfoDati.getAttribute("NUMPERCINV")).toString();
			numPercInv = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "NUMPERCINV");
			COD_PROVINCIA_ISCR = SourceBeanUtils.getAttrStrNotNull(rowInfoDati, "PROVINCIA_ISCR");
		} else {
			prgIscrArt1 = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "PRGISCRART1");
			STRNUMALBO = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "STRNUMALBO");
			DATISCRALBO = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "DATISCRALBO");
			CODTIPOLISTA = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "CODTIPOLISTA");
			DATISCRLISTAPROV = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "DATISCRLISTAPROV");
			NumPunteggioStr = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "NUMPUNTEGGIO");
			NumAnnoPunteggioStr = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "NUMANNOPUNTEGGIO");
			DATFINE = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "DATFINE");
			CDNUTPUNTEGGIO = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "CDNUTPUNTEGGIO");
			DATMODIFPUNTEGGIO = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "DATMODIFPUNTEGGIO");
			CODMOTIVOFINEATTO = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "CODMOTIVOFINEATTO");
			//numPercInv = serviceRequest.getAttribute("NUMPERCINV") == null ? "" : ((BigDecimal)serviceRequest.getAttribute("NUMPERCINV")).toString();
			numPercInv = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "NUMPERCINV");
			dtmIns = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "DTMINS");
			cdnUtMod = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "CDNUTMOD");
			dtmMod = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "DTMMOD");
			cdnUtIns = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "CDNUTINS");
		}
		
		if(!dtmIns.equals("") && !cdnUtMod.equals("") && !dtmMod.equals("") && !cdnUtIns.equals("")) {
			operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
		}
		
	SourceBean beanProt = null;
  		
  	DatAcqRil = StringUtils.getAttributeStrNotNull(serviceRequest,"DatAcqRil");
    DatInizio = StringUtils.getAttributeStrNotNull(serviceRequest,"DatInizio");
  
 	if(!nuovaIscrizione) {
  		CODSTATOATTO = StringUtils.getAttributeStrNotNull(serviceRequest,"CODSTATOATTO");
  			if(!DATFINE.equals("") && !CODMOTIVOFINEATTO.equals("")){
  					read = true;
  			}
  			if(CODSTATOATTO.equals("PR") || CODSTATOATTO.equals("AN")){
  				canModify = false;
  				if(CODSTATOATTO.equals("AN")){
  					read = true;
  					noButton = true;
  				}
  			}
  			
  			if ((CODSTATOATTO.equals("PR") || CODSTATOATTO.equals("AN")) ) {
  				numProtV = SourceBeanUtils.getAttrBigDecimal(serviceRequest, "NUMPROTOCOLLO", null);
	   			numAnnoProtV = SourceBeanUtils.getAttrBigDecimal(serviceRequest, "numAnnoProt", null); 
	   			dataOraProt = StringUtils.getAttributeStrNotNull(serviceRequest,"dataOraProt");
	 			if (!dataOraProt.equals("")) {
	 				oraProtV = dataOraProt.substring(11,16);
	 				datProtV = dataOraProt.substring(0,10);
	 			}
	   		}
  	}
  	
String htmlStreamTop    = StyleUtils.roundTopTable(!noButton);
String htmlStreamBottom = StyleUtils.roundBottomTable(!noButton);

%>

<html>
  <head>
  	<title> ISCRIZIONE LISTE SPECIALI ART. 1 </title> 
  	<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
	<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
	<SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>

	<af:linkScript path="../../js/"/>
	
   <script language="Javascript">
   
    var imgChiusa = "../../img/chiuso.gif";
	var imgAperta = "../../img/aperto.gif";
	var sezioni = new Array();

	function Sezione(sezione, img,aperta){    
    	this.sezione=sezione;
    	this.sezione.aperta=aperta;
    	this.img=img;
	}
	
	function cambia(immagine, sezione) {
		if (sezione.style.display == 'inline') {
			sezione.style.display = 'none';
			sezione.aperta = false;
			immagine.src = imgChiusa;
			immagine.alt = 'Apri';
		}
		else if (sezione.style.display == "none") {
			sezione.style.display = "inline";
			sezione.aperta = true;
			immagine.src = imgAperta;
    		immagine.alt = "Chiudi";
		}
	}
	
	function initSezioni(sezione){
		sezioni.push(sezione);
	}
	
	function selLavIscrCM(context, funzSetLav)
	{
		var url = "AdapterHTTP?PAGE=SelLavNOPage&fromWhere=" + context + "&AGG_FUNZ=" + funzSetLav + "&cdnFunzione=<%=_funzione%>";
		var feat = "toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes," +
		     	   "width=600,height=500,top=50,left=100";
    	opened = window.open(url, "_blank", feat);
    	opened.focus();
	}
	
	function riempiDatiLavIscrCM(cdnLavoratore, strCodiceFiscaleLav, strCognome, strNome, codMonoTipoRagg) {
	  	document.Frm1.cdnLavoratore.value = cdnLavoratore;
	  	document.Frm1.strCodiceFiscaleLav.value = strCodiceFiscaleLav;
	  	document.Frm1.strNomeCognomeLav.value = strCognome + " " + strNome;
	  	document.Frm1.CODMONOCATEGORIA.value = codMonoTipoRagg;
	  	opened.close();
	}
	
	function azzeraLavIscrCM(){
      	document.Frm1.cdnLavoratore.value = "";
      	document.Frm1.strCodiceFiscaleLav.value = "";
        document.Frm1.strNomeCognomeLav.value = "";
        document.Frm1.CODMONOCATEGORIA.value = "";
    }
	
	function tornaLista() {
		if (isInSubmit()) return;
		<%
		// Recupero l'eventuale URL generato dalla LISTA precedente
		String token = "_TOKEN_" + "CMIscrLSListaPage";
		String goBackListUrl = (String) sessionContainer.getAttribute(token.toUpperCase());	
		%>
			setWindowLocation("AdapterHTTP?<%= StringUtils.formatValue4Javascript(goBackListUrl) %>");
		}
		
	function ControllaDati(){
 		
 		if (!controllaFixedFloat('NUMPUNTEGGIO', 7, 3)) {			
			return false;
		} 		
 		
 		var dataiscrAlbo = document.Frm1.DATISCRALBO;
 		var dataIscrLista = document.Frm1.DATISCRLISTAPROV;
    	var dataOdierna = '<%=dataOdierna%>';
    	if(dataiscrAlbo.value != "" && dataIscrLista.value!= ""){
    		if (compareDate(dataiscrAlbo.value,dataIscrLista.value) >= 0) {
      			alert("La " + dataIscrLista.title + " deve essere maggiore della " + dataiscrAlbo.title);
      			dataiscrAlbo.focus();
	    		return false;
	  		}
	  	}
	  	
	  	if (compareDate(dataIscrLista.value,dataOdierna) > 0) {
      			alert("La " + dataIscrLista.title + " deve essere minore o uguale della data odierna ");
      			dataiscrAlbo.focus();
	    		return false;
	  	}
	  
	  <% if(!nuovaIscrizione && !CODSTATOATTO.equals("AN")) {%>
	  		var dataMotivoFine = document.Frm1.DATFINE;
	  		var motivoFine = document.Frm1.CODMOTIVOFINEATTO;
	  		if(dataIscrLista.value !="" && dataMotivoFine.value !=""){
	  			if (compareDate(dataIscrLista.value,dataMotivoFine.value) > 0) {
	  				alert("La " + dataMotivoFine.title + " non può essere precedente alla " + dataIscrLista.title);
      				dataMotivoFine.focus();
	    			return false;
	  			}
	  		}
	  		
	  		if(dataMotivoFine.value != "" && motivoFine.value == ""){
	  	 		alert("Il campo " + motivoFine.title + " è obbligatorio");
      			return false;
      		}
      		if(dataMotivoFine.value == "" && motivoFine.value != ""){
	  	 		alert("Il campo " + dataMotivoFine.title + " è obbligatorio");
      			return false;
      		}
      		
      	
     <%}%>
	  return true;
	}
		
		var flagChanged = false;
        
          function fieldChanged() {
           <% if (!readOnlyStr){ %> 
              flagChanged = true;
           <%}%> 
          }
		
		function calcolaPunteggio() {
			var punteggio;
			if(flagChanged) { 
				alert("I dati sono cambiati.\nSalvare i nuovi dati per poter utilizzare il calcolo punteggio automatico del sistema.");
				return false;
			}
			punteggio = "AdapterHTTP?PAGE=CMIscrLSPunteggioPage";
 			punteggio = punteggio + "&cdnLavoratore=<%=cdnLavoratore%>";
 			punteggio = punteggio + "&codtipolista=<%=CODTIPOLISTA%>";
 			punteggio = punteggio + "&PRGISCRART1=<%=prgIscrArt1%>";
  			var t = "_blank";
			var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=400,height=350,top=300,left=300";
 		  	window.open(punteggio, t, feat);		  
		  }
	
	function controllaPercentualeInvalidita(){
		if( document.Frm1.NUMPERCINV.value > 100 ){
			alert('La percentuale di invalidità non può superare il 100%');
			document.Frm1.NUMPERCINV.focus();
			return false;
		}
		return true;
	}
	
</script>

<style>
table.sezione2 {
	border-collapse:collapse;
	font-family: Verdana,"Times New Roman", Arial, Sans-serif; 
	font-size: 12px;
	font-weight: bold;
	border-bottom-width: 2px; 
	border-bottom-style: solid;
	border-bottom-color: #000080;
	color:#000080; 
	position: relative;
	width: 94%;
	text-align: left;
	text-decoration: none;	
}
</STYLE>
</head>

<body class="gestione">

 <script language="Javascript">
	   	   if((window.top != null) && (window.top.menu != null))
		       window.top.menu.caricaMenuLav(<%=_funzione%>,<%=cdnLavoratore%>);
 </script>

<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="ControllaDati()">

<input type="hidden" name="PAGE" value="CMIscrLSDettPage"/>
<input type="hidden" name="cdnFunzione" value="<%=_funzione%>" />
<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>"/> 

	<%if(!cdnLavoratore.equals("")){
  		InfCorrentiLav testata= new InfCorrentiLav(RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
  		testata.setSkipLista(true);
  		testata.show(out);
  	 }%>
  	 <br/><p class="titolo">Iscrizione Liste Speciali</p>
  	 
  	 <af:showErrors/>
	 
	 <center>
	 	<af:showMessages prefix="M_insertIscrListe"/>
		<af:showMessages prefix="M_Save_IscrListeSpec"/>
		<af:showMessages prefix="SalvaIscrListeSpecDoc"/> 
	</center>

<%= htmlStreamBottom %>

<%= htmlStreamTop %>

<%@ include file="_protocollazioneIscrListeSpeciali.inc" %>

<% if(!DATMODIFPUNTEGGIO.equals("") && !CDNUTPUNTEGGIO.equals("")) {%>
<input type="hidden" name="DATMODIFPUNTEGGIO" value="<%=DATMODIFPUNTEGGIO%>"/>
<input type="hidden" name="CDNUTPUNTEGGIO" value="<%=CDNUTPUNTEGGIO%>"/>
<%}%>
<script>

function gestisci_Protocollazione(){
		<%-- DOCAREA: basta questo controllo. I valori della protocollazione vengono impostati dalla classe Documento stessa. --%>
		var protocolloLocale = <%=it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil.protocollazioneLocale() %>;
		if (!protocolloLocale) return;
	 
		<% if(CODSTATOATTO.equals("NP")) { %>
		if (document.Frm1.CODSTATOATTO.value == "PR"){
			<%  rowsProt = serviceResponse.getAttributeAsVector("M_GetProtocollazione.ROWS.ROW");
	  			if(rowsProt != null && !rowsProt.isEmpty()) { 
					rowProt = (SourceBean) rowsProt.elementAt(0);
		   			numProtV = SourceBeanUtils.getAttrBigDecimal(rowProt, "NUMPROTOCOLLO", null);
	   				numAnnoProtV = (BigDecimal) rowProt.getAttribute("NUMANNOPROT");
	   				dataOraProt = (String) rowProt.getAttribute("DATAORAPROT");
	  			}
	  			oraProtV = dataOraProt.substring(11,16);
	  			datProtV = dataOraProt.substring(0,10);
	  		%>
	  		document.Frm1.oraProt.value = "<%=oraProtV%>";
			document.Frm1.dataProt.value= "<%=datProtV%>"; 
			document.Frm1.numProtocollo.value="<%=numProtV%>";  
			document.Frm1.numAnnoProt.value= "<%=numAnnoProtV%>";
			document.Frm1.dataOraProt.value="<%=dataOraProt%>"; 
	  	}
	  	if (document.Frm1.CODSTATOATTO.value == "AN" || document.Frm1.CODSTATOATTO.value == "NP") {
			document.Frm1.numAnnoProt.value = "";
	  		document.Frm1.numProtocollo.value = "";
	  		document.Frm1.dataProt.value = "";
	  		document.Frm1.oraProt.value = "";
	  		document.Frm1.dataOraProt.value = "";
		}
		<%}%>
	}		

	function controllaDoc(){
	 	var codStatoAtto = "<%=CODSTATOATTO%>";
	 	document.Frm1.CODSTATOATTO.value = codStatoAtto;
		if (document.Frm1.CODSTATOATTO.value == "NP"){
				document.Frm1.numAnnoProt.value = "";
	  			document.Frm1.numProtocollo.value = "";
	  			document.Frm1.dataProt.value = "";
	  			document.Frm1.oraProt.value = "";
	  			document.Frm1.dataOraProt.value = "";
    	}
    }	

	function aggiornaDocumento(){
		var f;
 		var codStatoAtto = "<%=CODSTATOATTO%>";

 		if (document.Frm1.CODSTATOATTO.value == codStatoAtto){
	 		alert("Stato atto non modificato");
			return false;
	 	}
	 	
	 	else {
	 	
 		 <% if (nuovaIscrizione) { %>
 		 		controllaDatARilascio();
 		 <%}%>
 		 f = "AdapterHTTP?PAGE=CMIscrLSDettPage&aggiornaDoc=1";
 		 f = f + "&CDNFUNZIONE=<%=_funzione%>";
 		 f = f + "&PRGISCRART1=<%=prgIscrArt1%>";
 		 f = f + "&cdnLavoratore=<%=cdnLavoratore%>";
		 f = f + "&oraProt=" +document.Frm1.oraProt.value;
	     f = f + "&dataProt="+document.Frm1.dataProt.value;
	     f = f + "&CODSTATOATTO=" + document.Frm1.CODSTATOATTO.value;
	     f = f + "&numProtocollo="+document.Frm1.numProtocollo.value;
	     f = f + "&numAnnoProt="+document.Frm1.numAnnoProt.value;
	     f = f + "&dataOraProt="+document.Frm1.dataOraProt.value;
	     f = f + "&codTipoDocumento=ILS";
	     f = f + "&DatAcqRil="+document.Frm1.DatAcqRil.value;
	     f = f + "&DatInizio="+document.Frm1.DatInizio.value;
	     f = f + "&tipoProt=S";
	     f = f + "&codAmbito=L68";
	    <% if(!DATMODIFPUNTEGGIO.equals("") && !CDNUTPUNTEGGIO.equals("")) {%>
	    	f = f + "&DATMODIFPUNTEGGIO=<%=DATMODIFPUNTEGGIO%>";
	    	f = f + "&CDNUTPUNTEGGIO=<%=CDNUTPUNTEGGIO%>";
		<%}%>
	     f = f + "&FlgCodMonoIO=<%=docInOut%>";
	    
	     document.location = f;
		}
	 }
	 
	 
	function controllaDatARilascio(){
		if (document.Frm1.CODSTATOATTO.value == "NP"){
			document.Frm1.DatAcqRil.value = '<%=dataOdierna%>';
			document.Frm1.DatInizio.value = '<%=dataOdierna%>';
		}
		else {
			document.Frm1.DatAcqRil.value = "<%=datProtV%>";
			document.Frm1.DatInizio.value = "<%=datProtV%>";
		}
	}
	
	function check_campi_inserimento(){
		if( controllaPercentualeInvalidita() == false ) return false;
			
		controllaDatARilascio();
		return true;
	}
	
	function check_campi_modifica(){
		if( controllaPercentualeInvalidita() == false ) return false;
			
		controllaDoc()
		return true;
	}
	
</script>

<input type="hidden" name="DatAcqRil" value="<%=DatAcqRil%>" />
<input type="hidden" name="DatInizio" value="<%=DatInizio%>" />
<input type="hidden" name="PRGISCRART1" value="<%=prgIscrArt1%>"/>
<input type="hidden" name="FlgCodMonoIO" value="<%=docInOut%>"/>

<table style="border-collapse:collapse">
<%
if (nuovaIscrizione) {
%>
	    <tr>
          <td class="etichetta">Ambito Territoriale</td>
          <td class="campo">
    	  	<af:comboBox name="PROVINCIA_ISCR" moduleName="CM_GET_PROVINCIA_ISCR" selectedValue="<%=COD_PROVINCIA_ISCR%>"
        	classNameBase="input" title="Ambito Territoriale" required="true" addBlank="true" />
       	  </td>
       	</tr>
<%}else {%>

	    <tr>
          <td class="etichetta">Ambito Territoriale</td>
          <td class="campo">
    	  	<af:textBox classNameBase="input" name="PROVINCIA_ISCR" value="<%=COD_PROVINCIA_ISCR%>"  
        	readonly="true" size="20" />
       	  </td>
       	</tr>

<%} %>
	<tr>
		<td class="etichetta2">Numero  iscr. albo nazionale</td>
	    <td class="campo2" colspan="4">
	    	<af:textBox classNameBase="input" validateOnPost="true" name="STRNUMALBO" title="Numero iscr. albo nazionale" size="20" onKeyUp="fieldChanged();" value="<%=STRNUMALBO%>" readonly="<%=String.valueOf(!canModify)%>"/>	
	 		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Data iscr. albo nazionale
			<af:textBox classNameBase="input" validateOnPost="true" type="date" name="DATISCRALBO" title="Data iscr. albo nazionale" onKeyUp="fieldChanged();" value="<%=DATISCRALBO%>" size="12" maxlength="10" readonly="<%=String.valueOf(!canModify)%>"/>
		 </td>
	</tr>
	<tr>
		<td class="etichetta2">Albo</td>
	    <td class="campo2">
	    	<af:comboBox classNameBase="input" title="Albo" name="CODTIPOLISTA" moduleName="M_TipoListSpec" onChange="fieldChanged()" addBlank="true" selectedValue="<%=CODTIPOLISTA%>"  required="true" disabled="<%=String.valueOf(!canModify)%>">
       		</af:comboBox>	
	   	</td>
	 </tr>
	 <tr>
	 	<td class="etichetta2">Data iscr. lista provinciale</td>
	    <td class="campo2">	
			<af:textBox classNameBase="input" validateOnPost="true" type="date" name="DATISCRLISTAPROV" title="Data iscr. lista provinciale" onKeyUp="fieldChanged();" value="<%=DATISCRLISTAPROV%>" size="12" maxlength="10" required="true" readonly="<%=String.valueOf(!canModify)%>"/>
		</td>
	</tr>
	<tr>
	    <td class="etichetta">Percentuale invalidità</td>
    	<td class="campo2">
    		<af:textBox classNameBase="input" type="integer" name="NUMPERCINV" title="Percentuale invalidità" 
    			value="<%=numPercInv%>" readonly="<%=String.valueOf(!canModify)%>" 
    			onKeyUp="fieldChanged();" validateOnPost="true" size="4" maxlength="3" />%
    	</td>
	</tr>
	<tr>
		<td class="etichetta">Anno riferimento punteggio</td>	
		<td colspan="5" class="campo2">
			<table style="border-collapse:collapse">
				<tr>
					<td class="campo2">
						<af:textBox classNameBase="input" type="integer" title="Anno riferimento punteggio" onKeyUp="fieldChanged();" name="NUMANNOPUNTEGGIO" value="<%=NumAnnoPunteggioStr%>" validateOnPost="true" size="6" maxlength="4" readonly="<%=String.valueOf(noButton)%>"/>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Numero punteggio&nbsp;&nbsp;	
					<td class="campo2">
						<af:textBox classNameBase="input" type="number" title="Numero punteggio" name="NUMPUNTEGGIO" value="<%=NumPunteggioStr%>" size="12" maxlength="11" readonly="<%=String.valueOf(noButton)%>"/>
       					<% if (!nuovaIscrizione && !CODSTATOATTO.equals("AN")){ %>
       						<a href="#" onClick="javascript:calcolaPunteggio();"><IMG name="image" border="0" src="../../img/calc.gif" alt="calcola punteggio"/></a>&nbsp;(calcola punteggio)
      			   		<%}%>
      			   </td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td class="etichetta2">Operatore di riferimento</td>
        <td class="campo2" nowrap>
        	<af:comboBox classNameBase="input" name="PRGSPI"  moduleName="COMBO_SPI_SCAD" onChange="fieldChanged()" selectedValue="<%=PrgSpiStr%>"
                          addBlank="true" title="Operatiore Spi" disabled="<%=String.valueOf(!canModify)%>"/>
           </td>
	</tr>
	<tr><td><br></td></tr>

 <% if(!nuovaIscrizione) {%>	
	<tr>
		<td colspan="4">
			<div class="sezione2">
		    	<img id='IMG1' src='<%=DATFINE.equals("") && CODMOTIVOFINEATTO.equals("")?img0:img1%>' onclick='cambia(this, document.getElementById("TBL1"))'/>Chiusura/cancellazione
		  	</div>
	  	</td>
	</tr>
	<tr>
    	<td colspan=4 align="center">
        	<table id='TBL1' style='display:<%=DATFINE.equals("") && CODMOTIVOFINEATTO.equals("")?"none":"inline"%>'>  
        	 <script>initSezioni(new Sezione(document.getElementById('TBL1'),document.getElementById('IMG1'),<%=hasDataFine%>));</script>
					<tr>
			    		<td class="etichetta">Data cancellazione</td>
			    		<td  class="campo"> 
                    		<af:textBox classNameBase="input" validateOnPost="true" onKeyUp="fieldChanged();" title="Data cancellazione" type="date" name="DATFINE" value="<%=DATFINE%>" size="12" readonly="<%=String.valueOf(read)%>" maxlength="10"/>
			            </td>
					</tr>
					<tr>
			    		<td class="etichetta">Motivazione cancellazione</td>
			    		<td  class="campo"> 
			    			<af:comboBox classNameBase="input" title="Motivo cancellazione" onChange="fieldChanged()" name="CODMOTIVOFINEATTO" moduleName="M_Motivo_Fine_Atto" addBlank="true" disabled="<%=String.valueOf(read)%>" selectedValue="<%=CODMOTIVOFINEATTO%>">
       						</af:comboBox>
       					</td>
			    	</tr>
			  </table>
    	</td>
	</tr>
	<tr><td colspan=4><br></td></tr>
<%}%>
</table>
 

<table>
  <%if(nuovaIscrizione && canInsert){%>
		<tr><td colspan="2">&nbsp;</td></tr><tr><td>&nbsp;</td></tr>  
		<tr>
			<td colspan="2" align="center">
				<input type="submit" name="inserisci" class="pulsanti" value="Inserisci" onClick="return check_campi_inserimento();"/>
				<input type="hidden" name="inserisciDoc" value="1"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
				<input type="reset" name="reset" class="pulsanti" value="Annulla"/>
	
   <%} if(canUpdate && !nuovaIscrizione && !CODSTATOATTO.equals("AN") ) {%>
		<tr><td colspan="2">&nbsp;</td></tr><tr><td></td></tr>    
			<tr>
				<td colspan="2" align="center">
					<input type="submit" name="Aggiorna" class="pulsanti" value="Aggiorna" onClick="return check_campi_modifica();"/>
					<input type="hidden" name="aggiornamento" value="1">
					<input type="hidden" name="NUMKLOCMISCRART1" value="">
					<input type="hidden" name="dtmIns" value="<%=dtmIns%>">
					<input type="hidden" name="cdnUtMod" value="<%=cdnUtMod%>">
					<input type="hidden" name="dtmMod" value="<%=dtmMod%>">
					<input type="hidden" name="cdnUtIns" value="<%=cdnUtIns%>">
					&nbsp;&nbsp;&nbsp;&nbsp; 
					<input type="reset" name="reset" class="pulsanti" value="Annulla"/>
	   <%} %>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<input class="pulsante" type = "button" name="torna" value="Torna alla lista" onClick="tornaLista();"/>
   		&nbsp;&nbsp;&nbsp;&nbsp;
 	</td>
   	</tr>

</table> 
 
<%= htmlStreamBottom %>

<% if (!nuovaIscrizione){ %>
	
 <!-- La data modifica punteggio nel db viene salvata con il formato dd/MM/yyyy HH:mm:ss 
						 ma deve essere visualizzata come dd/MM/yyyy -->
					 
<center>
	<table>
		<tr>
			<td align="center">
				<%operatoreInfo.showHTML(out);%>
			</td>
		</tr>
		<% 
		   String cognome="";
		   String nome = "";
		   String concatenaNome = "";
		   if (!DATMODIFPUNTEGGIO.equals("") && !CDNUTPUNTEGGIO.equals("")) {
				SourceBean utPunt = (SourceBean)serviceResponse.getAttribute("CMCARICA_UT_PUNTEGGIO");
				if ((utPunt != null) && (utPunt.getAttribute("ROWS.ROW")!=null)){
  					cognome = (String) utPunt.getAttribute("ROWS.ROW.COGNOME");
  					nome = utPunt.getAttribute("ROWS.ROW.NOME").toString();
  					concatenaNome = " " + cognome + " " + nome;
  				} 
  				
  		   %>
		<tr class="note">
			<td align="center" class="info_mod">Modifica punteggio&nbsp;<b><%=concatenaNome%></b><b> - <%=DATMODIFPUNTEGGIO%></b></td>
		</tr>
		<%}%>
	</table>
</center>
<%}%>

</af:form>  
</body>
</html>