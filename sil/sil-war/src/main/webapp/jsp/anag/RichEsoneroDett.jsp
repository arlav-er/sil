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
  String NUMKLOESONERO = null;

  String  STRRAGIONESOCIALE = null;
  String  STRCODICEFISCALE = null;
  String  STRPARTITAIVA = null;
  String  CODSTATORICHIESTA = null;
  String  DATRICHIESTA = null;
  String  DATINIZIOVALIDITA = null;
  String  DATFINE = null;
  String  prgAzienda = null;
  String  prgRichEsonero = null;
  String  NUMBASECOMPUTO = null;
  String  NUMPERCESONERO = null;  
  String  STRMOTIVAZIONE = null;
  String  numDisabili = null;

  String htmlStreamTop2 = "";
  String htmlStreamBottom2 = "";  

  boolean nuovaRichiesta = !(serviceRequest.getAttribute("nuovaRichiesta")==null || 
                            ((String)serviceRequest.getAttribute("nuovaRichiesta")).length()==0);
                            
  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page); 


  PageAttribs attributi = new PageAttribs(user, _page); 

  boolean canModify = false;
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
  
  boolean readOnlyStr = !canModify;
  String fieldReadOnly = canModify ? "false" : "true";
  boolean disableCodRich = false || readOnlyStr;
  String fieldCodRich = disableCodRich ? "true" : "false"; 
  String canModifyRich = StringUtils.getAttributeStrNotNull(serviceRequest,"canModifyRich");
  String fieldNumPercEson = "true";
  String dateReadOnly = "true";
  String variazioniPresenti = "false";
  
  boolean pagamenti = true; 

  String cercaAzienda = StringUtils.getAttributeStrNotNull(serviceRequest,"cercaAzienda");
  String pagam = StringUtils.getAttributeStrNotNull(serviceRequest,"pagam");
  String inserisciPagam = StringUtils.getAttributeStrNotNull(serviceRequest,"inserisciPagam");
  String paginaNuovoPagamento = StringUtils.getAttributeStrNotNull(serviceRequest,"paginaNuovoPagamento");  
  String eliminaPagam = StringUtils.getAttributeStrNotNull(serviceRequest,"eliminaPagam");  
  String calcolaSituaz = StringUtils.getAttributeStrNotNull(serviceRequest,"calcolaSituaz");
  String calcolo = StringUtils.getAttributeStrNotNull(serviceRequest,"calcolo");
  String DATPAGAMENTO = StringUtils.getAttributeStrNotNull(serviceRequest,"DATPAGAMENTO");
  String PROVINCIA_ISCR = StringUtils.getAttributeStrNotNull(serviceRequest,"PROVINCIA_ISCR");

  String fromRicerca = StringUtils.getAttributeStrNotNull(serviceRequest,"fromRicerca");

  prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest,"prgAzienda");

  String prgUnita = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgUnita");
      
  prgRichEsonero = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgRichEsonero");

  String inserisciNewFromRicerca = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "inserisciNewFromRicerca");
  
  String goBackListPage = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "goBackListPage");
    
//----------------------------------------------------------------------------------------------------
  SourceBean rowRich = null;
  SourceBean rowDoc = null;
  Vector rowsDoc      = null;  
  String prAutomatica     = "S";
  String docInOut         = "I";
  String docRif           = "Documentazione L68";
  String docTipo          = "Richiesta di esonero";
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
	  rowDoc = (SourceBean) serviceResponse.getAttribute("M_Load_Doc_RichEson.ROWS.ROW");
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
	  
	  prgAzienda = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgAzienda");
	  STRRAGIONESOCIALE = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"STRRAGIONESOCIALE");
	  STRCODICEFISCALE = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"STRCODICEFISCALE");
	  STRPARTITAIVA = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"STRPARTITAIVA");
	  CODSTATORICHIESTA = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"CODSTATORICHIESTA");
      DATRICHIESTA = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"DATRICHIESTA");
      DATINIZIOVALIDITA = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"DATINIZIOVALIDITA");
      DATFINE = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"DATFINE");
      NUMBASECOMPUTO = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"NUMBASECOMPUTO");	
      NUMPERCESONERO = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"NUMPERCESONERO");
      STRMOTIVAZIONE = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"STRMOTIVAZIONE");
      numDisabili = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"numDisabili");
      PROVINCIA_ISCR = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PROVINCIA_ISCR");

	  cdnUtIns = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
	  cdnUtMod = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
	  
	  if(prgAzienda != null && !prgAzienda.equals("")){
		  SourceBean rowAz = null;
		  Vector rowsAz= serviceResponse.getAttributeAsVector("M_Load_Azienda.ROWS.ROW");
		  // siamo in dettaglio per cui avro' al massimo una riga
		  if (rowsAz.size()==1) {
		        rowAz = (SourceBean)rowsAz.get(0);
				
			    STRRAGIONESOCIALE = Utils.notNull(rowAz.getAttribute("STRRAGIONESOCIALE"));		    
			    STRCODICEFISCALE = Utils.notNull(rowAz.getAttribute("STRCODICEFISCALE")); 
			    STRPARTITAIVA = Utils.notNull(rowAz.getAttribute("STRPARTITAIVA"));  
		  }	  	
	  }
	
  }else if(!cercaAzienda.equals("1")){

	  	  
	  SourceBean rowDis = null;
	  Vector rowsDis= serviceResponse.getAttributeAsVector("M_Get_NumDis.ROWS.ROW");	  
	  // prendo solo il primo record per estrarre l'ultimo numDisabili
	  if (rowsDis.size()>=1) {
		  rowDis = (SourceBean)rowsDis.get(0);
		  numDisabili = Utils.notNull(rowDis.getAttribute("numDisabili")); 
		  NUMBASECOMPUTO = Utils.notNull(rowDis.getAttribute("NUMBASECOMPUTO"));
		  variazioniPresenti = "true";
	  }
	          
	  SourceBean row = null;
	  Vector rows= serviceResponse.getAttributeAsVector("M_Load_RichEsonero.ROWS.ROW");
	  // siamo in dettaglio per cui avro' al massimo una riga
	  if (rows.size()==1) {
	        row = (SourceBean)rows.get(0);
			
			prgRichEsonero = Utils.notNull(row.getAttribute("prgRichEsonero"));
		    prgAzienda = Utils.notNull(row.getAttribute("prgAzienda")); 
		    STRRAGIONESOCIALE = Utils.notNull(row.getAttribute("STRRAGIONESOCIALE"));		    
		    STRCODICEFISCALE = Utils.notNull(row.getAttribute("STRCODICEFISCALE")); 
		    STRPARTITAIVA = Utils.notNull(row.getAttribute("STRPARTITAIVA"));  
		   
		    CODSTATORICHIESTA = Utils.notNull(row.getAttribute("CODSTATORICHIESTA"));  
		    DATRICHIESTA = Utils.notNull(row.getAttribute("DATRICHIESTA"));  
		    DATINIZIOVALIDITA = Utils.notNull(row.getAttribute("DATINIZIOVALIDITA"));  
		    DATFINE = Utils.notNull(row.getAttribute("DATFINE"));  
		    NUMPERCESONERO = Utils.notNull(row.getAttribute("NUMPERCESONERO")); 
		    STRMOTIVAZIONE = Utils.notNull(row.getAttribute("STRMOTIVAZIONE")); 
		    PROVINCIA_ISCR = Utils.notNull(row.getAttribute("PROVINCIA_ISCR"));  


		    
        	NUMKLOESONERO = String.valueOf(((BigDecimal)row.getAttribute("NUMKLOESONERO")).intValue());
	        
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
	  Vector rows2= serviceResponse.getAttributeAsVector("M_Load_RichEsonero.ROWS.ROW");
	  if (rows2.size()==1) {
	        row2 = (SourceBean)rows2.get(0);			
		    statoRichiesta = Utils.notNull(row2.getAttribute("CODSTATORICHIESTA")); 
	  }
	  if(statoRichiesta.equals("AN") || CODSTATOATTO_P.equals("PR")) {
		fieldReadOnly = "true";
	  	if(statoRichiesta.equals("AN")) {		
	  		canModify = false;
	  		canInsert = false;
	  		noButton = "true";
	  		fieldCodRich = "true";
	  	}
	  }
	  if(statoRichiesta.equals("DA") && !readOnlyStr) { 
	  	fieldNumPercEson = "false";
	  	dateReadOnly = "false";
	  }
  } else {
		noButton = "true";
		fieldCodRich = "true";
  }
  
  if(canModifyRich.equals("true")) {
  	pagamenti = false;
  }
      
  //Servono per gestire il layout grafico
  String htmlStreamTop = StyleUtils.roundTopTable(true);
  String htmlStreamBottom = StyleUtils.roundBottomTable(true);

%>

<html>

<head>

   <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
	<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />  
	<SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT> 
    <af:linkScript path="../../js/"/>
   
    
	<script language="JavaScript">
          
          var flagChanged = false;
        
          function fieldChanged() {
           <% if (fieldReadOnly.equals("false") || fieldCodRich.equals("false") || 
        		   fieldNumPercEson.equals("false") || dateReadOnly.equals("false")) {%>
              flagChanged = true;
           <% }%> 
          }

          function conferma() {
			var nuovaRichiesta = "<%=nuovaRichiesta%>";
			if (flagChanged && nuovaRichiesta == "false") {
     			if (!confirm("I dati sono cambiati.\nProcedere lo stesso?")){
         			return false;
     			} else { 
         			return true;
         		}
     		}
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
			  urlpage+="PAGE=CMRichEsoneroRicercaPage&prgAzienda=<%=prgAzienda%>";
			  setWindowLocation(urlpage);
		  }
		  
		  function cercaAzienda(){
			  var f;
 		      f = "AdapterHTTP?PAGE=CMRichEsoneroDettPage&cercaAzienda=1";
 		      f = f + "&CDNFUNZIONE=<%=_funzione%>";
			  f = f + "&prgAzienda=<%=prgAzienda%>";
			  f = f + "&prgRichEsonero=<%=prgRichEsonero%>";
			  f = f + "&ragioneSoc=" + document.Frm1.STRRAGIONESOCIALE.value;
			  f = f + "&CodFisc=" + document.Frm1.STRCODICEFISCALE.value;
			  f = f + "&PIVA=" + document.Frm1.STRPARTITAIVA.value;
			  f = f + "&goBackListPage=<%=goBackListPage%>";
			  
			  var t = "_blank";
			  var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=600,height=500,top=100,left=100";
 		  	  window.open(f, t, feat);
		  }	
		  
		  function pagamentiEson(){
			  if (conferma()) {
			  	var f;
 		      	f = "AdapterHTTP?PAGE=CMRichEsoneroDettPage&pagam=1";
 		      	f = f + "&CDNFUNZIONE=<%=_funzione%>";
			  	f = f + "&prgRichEsonero=<%=prgRichEsonero%>";
			  	f = f + "&DATINIZIOVALIDITA=" + document.Frm1.DATINIZIOVALIDITA.value;
			  	f = f + "&DATFINE=" + document.Frm1.DATFINE.value;
			  	f = f + "&canModifyRich=<%=fieldCodRich%>";
			  	f = f + "&goBackListPage=<%=goBackListPage%>";
			  	var t = "_blank";
			  	var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=600,height=500,top=100,left=100";
 		  	  	window.open(f, t, feat);
 		  	  }		  
		  }
		  
		  function indietroPagamenti(){
			  var f;
 		      f = "AdapterHTTP?PAGE=CMRichEsoneroDettPage&pagam=1";
 		      f = f + "&CDNFUNZIONE=<%=_funzione%>";
			  f = f + "&prgRichEsonero=<%=prgRichEsonero%>";
			  f = f + "&goBackListPage=<%=goBackListPage%>";
			  f = f + "&canModifyRich=<%=fieldCodRich%>";
			  document.location = f;		  	
		  }

		  function calcolaSituazione(){
			  var f;
 		      f = "AdapterHTTP?PAGE=CMRichEsoneroDettPage&calcolaSituaz=1";
 		      f = f + "&CDNFUNZIONE=<%=_funzione%>";
			  f = f + "&prgRichEsonero=<%=prgRichEsonero%>";
			  f = f + "&DATINIZIOVALIDITA=<%=DATINIZIOVALIDITA%>";
			  f = f + "&DATFINE=<%=DATFINE%>";
			  f = f + "&canModifyRich=<%=fieldCodRich%>";
			  f = f + "&goBackListPage=<%=goBackListPage%>";
			  document.location = f;		  	
		  }		  

		  function storicoVariaz(){
			  if (conferma()) {
				  var f;
	 		      f = "AdapterHTTP?PAGE=CMRichEsoneroStoricoVar";
	 		      f = f + "&CDNFUNZIONE=<%=_funzione%>";
				  f = f + "&prgRichEsonero=<%=prgRichEsonero%>";
				  f = f + "&NUMPERCESONERO=<%=NUMPERCESONERO%>";
				  f = f + "&canModifyRich=<%=fieldCodRich%>";
				  f = f + "&NUMBASECOMPUTO=<%=NUMBASECOMPUTO%>";
				  f = f + "&goBackListPage=<%=goBackListPage%>";
				  
				  var t = "_blank";
				  var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=600,height=500,top=100,left=100";
	 		  	  window.open(f, t, feat);			  		  	
		  	  }
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

		  function isNumeric(val){
		  	  return(parseFloat(val,10)==(val*1));
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
		  
		  function controlloDatiInsPagamento(){
		  	  var dataPagam = document.Frm2.DATPAGAMENTO.value;	  	  	  
	  	  	  var importoPagam = document.Frm2.DECIMPORTO.value;
	  	  	  
	  	  	  var dtInComp = document.Frm2.DATINIZIOCOMP.value;
	  	  	  var dtFiComp = document.Frm2.DATFINECOMP.value;
	  	  	  
	  	  	  var dtIn = document.Frm2.DATINIZIOVALIDITA.value;
	  	  	  var dtFin = document.Frm2.DATFINE.value;
			  
		  	  var isOkPeriod = false;
		  	  var period = checkDate(dtInComp,dtFiComp)
		  	  if(period ==1){
		  	  	  isOkPeriod = true;
		  	  }
		  	  		  	  
		  	  var isInPeriod = false;
		  	  var checkInInComp = checkDate(dtIn,dtInComp);
		  	  var checkFiCompFin = checkDate(dtFiComp,dtFin);
		  	  if(dtFin!=""){
			  	  if( (checkInInComp==1 || checkInInComp==0) && (checkFiCompFin==1 || checkFiCompFin==0)){
			  	  	  isInPeriod = true;
			  	  }
		  	  }else{
			  	  if( checkInInComp==1 || checkInInComp==0 ){
			  	  	  isInPeriod = true;
			  	  }				  
		  	  }
		  	  
		  	  var isOkDtPagam = false;
		  	  if(checkDate(dataPagam,dtFiComp)==1){
		  	  	  isOkDtPagam = true;
		  	  }
		  	  
		  	  if(dataPagam==""){
		  	  	  document.Frm2.DATPAGAMENTO.value = dataDiOggi();
			  }
			  
			  if(isNumeric(importoPagam) && isOkPeriod){
				  	if (controllaFixedFloat('DECIMPORTO', 7, 3)) {
				  	  	return true;
				  	} else {
				  		return false;
				  	}			  				
			  }else if(!isNumeric(importoPagam)){
			  	  alert("L'importo deve avere un valore numerico");
			  	  return false;
			  }else if(!isOkPeriod){
			  	  alert("La data inizio del periodo deve essere minore della data fine");
			  	  return false;
			  }else if(!isInPeriod){
			  	  alert("Il periodo di competenza del pagamento deve essere compreso tra la data inizio e fine della validità della richiesta");
			  	  return false;
			  }else if(!isOkDtPagam){
			  	  alert("La data del pagamento deve essere antecedente o essere compresa nel periodo di competenza del pagamento");
			  	  return false;
			  }else{
			  	  return false;
			  }
		  }

		  function Mesi(strData1,strData2){ 
			  //costruisco la data inizio
		  	  var d1giorno=parseInt(strData1.substr(0,2),10);
		  	  var d1mese=parseInt(strData1.substr(3, 2),10)-1; //il conteggio dei mesi parte da zero :P
		      var d1anno=parseInt(strData1.substr(6,4),10);
		  	  var data1=new Date(d1anno, d1mese, d1giorno);
			  
			  //costruisce la data fine
		  	  var d2giorno=parseInt(strData2.substr(0,2),10);
		   	  var d2mese=parseInt(strData2.substr(3,2),10)-1;
		  	  var d2anno=parseInt(strData2.substr(6,4),10);
		      var data2=new Date(d2anno, d2mese, d2giorno);
      
		   	  var mesi;
		   	  var data1time = data1.getTime(); 
		  	  var data2time = data2.getTime(); 
		  	  var difftime = Math.abs(data1time-data2time); 
		      mesi = parseInt(difftime/1000/60/60/24/30);
			  return mesi; 
          } 

		  		  
		  function controllaDati(){
		  	  
		  	  var dataInizioSosp = document.Frm1.DATINIZIOVALIDITA.value;
		  	  var dataFineSosp = document.Frm1.DATFINE.value;
		  	  var statoRich = document.Frm1.CODSTATORICHIESTA.value;
		  	  
	  	  	  var numeroMesi = 0;
			  
	  	  	  var numPercEsonero = document.Frm1.NUMPERCESONERO.value;
	  	  	  var isNumValido = false;

			  if(isNumeric(numPercEsonero) && (numPercEsonero <= 80)){
			  	isNumValido = true;
			  }
			
			  if( statoRich == "AP" && ( dataInizioSosp == "" || dataFineSosp == "" ) ){
			  	alert("Per approvare una richiesta di esonero, la data inizio e la data fine validità devono essere valorizzate");
			  	return false;
			  }
			
			  if ((dataInizioSosp!= "") && (dataFineSosp!= "")) {
			 	var date = checkDate(dataInizioSosp,dataFineSosp);
			 	if (date != 1) {
      				alert("La data inizio validità è maggiore o uguale della data fine validità");
      				return false;
      			} else if(date==1){
					numeroMesi = Mesi(dataInizioSosp, dataFineSosp);
			  		if (numeroMesi > 36){
			  	 		alert("Tra la data di fine validità e la data di inizio validità non devono intercorrere più di 36 mesi");
			  	 		return false;
			  	 	}
			  	}
			  }
			    
			  if(isNumValido){
				  if(numPercEsonero > 60){
				  	 if(confirm("La percentuale di esonero può essere maggiore di 60 se le aziende operano nei settori della sicurezza, della vigilanza e dei trasporti privati. Vuoi continuare?"))
				  	 	return true;
				  	 else
				  	 	return false;
				  }
			  }else if(!isNumValido){
		    	  alert("La percentuale di esonero deve essere un campo numerico e minore o uguale di 80");
				  return false;
			  }
			  
			  return true;
		 }    		   		
		 
		 function calcoloValPagam(){		 	  			  
		      var dtPagam2 = document.Frm3.DATPAGAMENTO.value;
		  
		      if(dtPagam2==""){
		  	  	dtPagam2 = dataDiOggi();
		      }
		      
		      var datFineEsonero = '<%=DATFINE%>';
		      var datUltimaCalcolo = dtPagam2; 
		      if (datFineEsonero != "") {
			      var z = checkDate(datFineEsonero,dtPagam2);
			 	  if (z == 1) {
				  	datUltimaCalcolo = datFineEsonero;	
			  	  }
			  } 
		  	  	
			  f = "AdapterHTTP?PAGE=CMRichEsoneroDettPage&calcolaSituaz=1&calcolo=1";
			  f = f + "&CDNFUNZIONE=<%=_funzione%>";
			  f = f + "&prgRichEsonero=<%=prgRichEsonero%>";
			  f = f + "&DATINIZIOVALIDITA=<%=DATINIZIOVALIDITA%>";
			  f = f + "&DATPAGAMENTO=" + dtPagam2;
			  f = f + "&DATULTIMACALC=" + datUltimaCalcolo;
			  f = f + "&goBackListPage=<%=goBackListPage%>";
			  	  			  
			  document.location = f;
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
		 		
		 		f = "AdapterHTTP?PAGE=CMRichEsoneroDettPage&aggiornaDoc=1";
		 		f = f + "&CDNFUNZIONE=<%=_funzione%>";
				f = f + "&prgRichEsonero=<%=prgRichEsonero%>";
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
				 	f = f + "&NUMKLOESONERO=<%=NUMKLOESONERO%>";
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
		 
		 function aggiornaVariazioni(){
		 	var variazioniPres = "<%=variazioniPresenti%>";
		 	var nuovaRichiesta = "<%=nuovaRichiesta%>";
		 	var numPercEsonOld = "<%=NUMPERCESONERO%>";
		 	var numPercEsonNew = document.Frm1.NUMPERCESONERO.value;

		 	var statoRich = document.Frm1.CODSTATORICHIESTA.value;
		 	if(statoRich == "DA"){
		 		if(variazioniPres == "true" || (nuovaRichiesta == "false" && document.Frm1.NUMBASECOMPUTO.value != "")) {
		 			if (parseInt(numPercEsonNew) != parseInt(numPercEsonOld)) {
						document.Frm1.updateVariazioni.value = 1;
		 			}
		 		}
		 	}
		 	return true;
		 }
		 
		 function goBack() {
			if (conferma()) {
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
	      		if(goBackListPage.equals("CMRichEsoneroListaPage")){
	      			goBackButtonTitle = "Torna alla lista";
	      			%>
	      			setWindowLocation("AdapterHTTP?<%= StringUtils.formatValue4Javascript(goBackListUrl) %>");
	      			<%
	      		}else{
	      			goBackButtonTitle = "Torna alla ricerca";
	      			%>
	      			f = "AdapterHTTP?PAGE=CMRichEsoneroRicercaPage";
			 		f = f + "&CDNFUNZIONE=<%=_funzione%>";
					f = f + "&prgRichEsonero=<%=prgRichEsonero%>";
					<% if (prgAzienda != null && !prgAzienda.equals("")) { %>
					f = f + "&prgAzienda=<%=prgAzienda%>";
					<% } %>      			
	      			document.location = f;
	      			<%
	      		}
				%>	
			}						
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
		 
	</script>

	<script language="Javascript">
	
	<% if(prgUnita.equals("")) {
	   		SourceBean rowPrg = (SourceBean) serviceResponse.getAttribute("M_GetPrgUnita.ROWS.ROW");
 	   		if(rowPrg != null) prgUnita = SourceBeanUtils.getAttrStrNotNull(rowPrg, "prgUnita");
 	   	}
 	   	
 	   	if (prgAzienda!=null && (prgUnita!=null && !prgUnita.equals("")) ) { %>
			if (window.top.menu != undefined){
				window.top.menu.caricaMenuAzienda(<%=_funzione%>, <%=prgAzienda%>, <%=prgUnita%>);
			}		
		<% } %>
	
	</script>

	<script language="Javascript" src="../../js/docAssocia.js"></script>
		
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
    }else if(!pagam.equals("") || !inserisciPagam.equals("") || !eliminaPagam.equals("")){	
    %>
		<br><br>
		
		<af:list moduleName="M_Load_Pagamenti" skipNavigationButton="0" 
					 canDelete="<%= pagamenti ? \"1\" : \"0\" %>" 
             	     canInsert="<%= pagamenti ? \"1\" : \"0\" %>" />		
			    
	    <table align="center">
	    	<tr>
			    <td>
			    	<input type="button" class="pulsanti" value="Chiudi" onClick="window.close()">
			    	&nbsp;&nbsp;
			        <input type="button" class="pulsanti" value="Calcola situazione" onClick="calcolaSituazione()">
			    </td>	
    		</tr>	    		
        </table>	
	<%}else if(!paginaNuovoPagamento.equals("")){%>
		<br><br>

		<table class="main" border="0">
		   	<tr>
			    <td colspan = "2">
			      <p class="titolo">Nuovo pagamento</p>
			    </td>	
	    	</tr>
	    </table>
	    <br>
		<af:form method="POST" action="AdapterHTTP" name="Frm2" onSubmit="controlloDatiInsPagamento()">
			
			<input type="hidden" name="inserisciPagam" value="1">
			<input type="hidden" name="PAGE" value="CMRichEsoneroDettPage" />		
	
			<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
					
			<input type="hidden" name="CDNUTINS" value="<%=cdnUtIns%>">
			<input type="hidden" name="CDNUTMOD" value="<%=cdnUtMod%>">
			<input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"> 
			<input type="hidden" name="prgRichEsonero" value="<%=prgRichEsonero%>">
			<input type="hidden" name="DATFINE" value="<%=DATFINE%>">
			<input type="hidden" name="DATINIZIOVALIDITA" value="<%=DATINIZIOVALIDITA%>">
			<input type="hidden" name="goBackListPage" value="<%=goBackListPage%>" />
			
	    	<%= htmlStreamTop %>

		    <table class="main" border="0">
		    	<tr>
		    		<td colspan="2">&nbsp;</td>
		    	</tr>
		    	<tr>
			        <td colspan="2" align="left">Periodo di competenza del pagamento</td>
	    		</tr>   
		    	<tr>
			        <td class="etichetta">dal</td>
				    <td class="campo">
				      <af:textBox title="Data inizio competenza pagamento" validateOnPost="true" disabled="false" type="date" name="DATINIZIOCOMP" value="" size="12" maxlength="12" readonly="<%=fieldCodRich%>" classNameBase="input" />&nbsp;*
				      <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('DATINIZIOCOMP')"; </SCRIPT>
				      &nbsp;&nbsp;
				      al&nbsp;
				      <af:textBox title="Data fine competenza pagamento" validateOnPost="true" disabled="false" type="date" name="DATFINECOMP" value="" size="12" maxlength="12" readonly="<%=fieldCodRich%>" classNameBase="input" />&nbsp;*
				      <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('DATFINECOMP')"; </SCRIPT>
				    </td>	
	    		</tr>   
		    	<tr>
		    		<td colspan="2">&nbsp;</td>
		    	</tr>
		    	<tr>
			        <td class="etichetta">Data pagamento</td>
				    <td class="campo">
				      <af:textBox title="Data pagamento" validateOnPost="true" disabled="false" type="date" name="DATPAGAMENTO" value="" size="12" maxlength="12" readonly="<%=fieldCodRich%>" classNameBase="input" />&nbsp;*
				    </td>
	    		</tr>   
		    	<tr>
			        <td class="etichetta">Importo</td>
				    <td class="campo">
				      <af:textBox title="Importo pagamento" type="number" value="" classNameBase="input" name="DECIMPORTO" maxlength="11" size="12" readonly="<%=fieldCodRich%>" />&nbsp;&#8364;&nbsp;*
				      <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('DECIMPORTO')"; </SCRIPT>
				    </td>	
	    		</tr>   
		    	<tr>
		    		<td colspan="2">&nbsp;</td>
		    	</tr>
		     </table>
		     <%= htmlStreamBottom %>
		     <table class="main" border="0">	
		    	<tr>
				    <td colspan="2" align="center">
				      <input class="pulsanti" type="submit" name="inserisci" value="Inserisci pagamento" onClick="">
				    </td>	
	    		</tr>
		    	<tr>
				    <td colspan="2" align="center">
				      <input type="button" class="pulsanti" value="Chiudi" onClick="indietroPagamenti();">
				    </td>	
	    		</tr>	    		
	        </table>
	       	         	
		</af:form>
		
	<%}else if(calcolaSituaz.equals("1")){%>
		<br>
		<table class="main" border="0">
		   	<tr>
			    <td colspan = "2">
			      <p class="titolo">Calcolo situazione pagamenti</p>
			    </td>	
	    	</tr>
	    </table>
	    <br>
    	<%= htmlStreamTop %>
	    <af:form name="Frm3" onSubmit="calcoloValPagam()">
	    	<input type="hidden" name="goBackListPage" value="<%=goBackListPage%>" />
		    <table class="main" border="0">
		    	<tr>
			        <td align="center">Data rispetto la quale si vuole calcolare la situazione dei pagamenti&nbsp;</td>
	    		</tr>   
		    	<tr>
				    <td align="center">
				      <af:textBox title="Data pagamento" validateOnPost="true" disabled="false" type="date" name="DATPAGAMENTO" value="<%=DATPAGAMENTO%>" size="12" maxlength="12" readonly="false" classNameBase="input" />
				    </td>	
	    		</tr>   
	        </table>	        	
        <%
        if(!calcolo.equals("")){
      		String calcoloValido = (String) serviceResponse.getAttribute("CMCheckCalcoloSituazioneEsonero.calcoloValido");
      	  	if (calcoloValido.equals("true")) {
        %>
	        <af:list moduleName="ListaSituazioneEsoneroModule" skipNavigationButton="1" />        			    
        <%
            
			RichEsonSituazAzienda situazAz = new RichEsonSituazAzienda(prgRichEsonero, DATINIZIOVALIDITA, DATPAGAMENTO, serviceResponse);
			
			Object  giorniCalcolati = "";
			Object  costoTotale = "";
			Object  costoVersato = "";
   		    Object  isOk = "";
			HashMap calcoli = situazAz.getCalcoloSituazione();
			if (!calcoli.isEmpty()) {
				giorniCalcolati = calcoli.get("giorniCalcolati");
				costoTotale = calcoli.get("costoTotale");
				costoVersato = calcoli.get("costoVersato");
				isOk = calcoli.get("isOk");
			}
			
            %>		       
            <table class="main" border="0">
		   	<tr>
			    <td class="etichetta">Valore calcolato&nbsp;</td>	
			    <td class="campo"><b><%=costoTotale.toString()%>&nbsp;&#8364;</b></td>	
	    	</tr>        
		   	<tr>
			    <td colspan="2">&nbsp;</td>	
	    	</tr>        
		   	<tr>
			    <td class="etichetta">Valore versato&nbsp;</td>	
			    <td class="campo"><b><%=costoVersato.toString()%>&nbsp;&#8364;</b></td>	
	    	</tr>        
		   	<tr>
			    <td colspan="2">&nbsp;</td>	
	    	</tr>       
		   	<tr>
			    <td colspan="2" align="center">
			      <%
			      if(("N").equalsIgnoreCase(isOk.toString())){
			      %>
				      <font color="red"><b>L'azienda non è in regola con i pagamenti</b></font>
			      <%}else{%>
				      <b>L'azienda è in regola con i pagamenti</b>
			      <%}%>
			    </td>	
	    	</tr>
		   	<tr>
			    <td colspan="2">&nbsp;</td>	
	    	</tr>  
	    	</table> 
	    	<% }  else { %>
		    
		    <table class="main" border="0">
		       	<tr>
			        <td></td>
			    </tr>
		    	<tr>
			        <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			        <td align="left">
			        	<font color="red">
			        		<b>Non è possibile effettuare il calcolo:<br></b>
			        		<b>i parametri relativi al costo unitario giornaliero non sono definiti<br></b>
			        		<b>correttamente o, più probabilmente, non coprono tutto il periodo di calcolo.</b>
			        	</font>
			       	</td>
	    		</tr>	    	
	    	</table>
	    		
	    	<% }
        }%>
        <%= htmlStreamBottom %>	
	        <table class="main" border="0">
			   	<tr>
				    <td colspan="2" align="center">
				      <input type="submit" class="pulsanti" value="Calcola">
				    </td>	
		    	</tr>
			   	<tr>
				    <td colspan="2" align="center">
				      <input type="button" class="pulsanti" value="Torna alla lista dei pagamenti" onClick="indietroPagamenti();">
				    </td>	
		    	</tr>
		    </table>
      	</af:form>        	
        	
	<%}else{%>
	
	<%if(nuovaRichiesta){ %>
    	<p class="titolo">Nuova richiesta di esonero</p>
	<%}else{%>
    	<p class="titolo">Richiesta di esonero</p>
	<%}%>

	<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="controllaDati() && aggiornaVariazioni()">

		<input type="hidden" name="PAGE" value="CMRichEsoneroDettPage" />		

		<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
				
		<input type="hidden" name="CDNUTINS" value="<%=cdnUtIns%>">
		<input type="hidden" name="CDNUTMOD" value="<%=cdnUtMod%>">
		<input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"> 
		<input type="hidden" name="prgRichEsonero" value="<%=prgRichEsonero%>">
		<input type="hidden" name="strChiaveTabella" value="<%=prgRichEsonero%>">
		<input type="hidden" name="goBackListPage" value="<%=goBackListPage%>" />
<%
  //Oggetti per l'applicazione dello stile
  htmlStreamTop = StyleUtils.roundTopTable(isInsert);
  htmlStreamBottom = StyleUtils.roundBottomTable(isInsert);
%>
	    <div id="divLookAzi_look" style="display:">
			<span class="sezioneMinimale">
				Azienda&nbsp;
				<% if (prgAzienda == "" || prgAzienda.equals("")) { %>
					<a href="#" onClick="selezionaAzienda();return false"><img src="../../img/binocolo.gif" alt="Cerca"></a>
				<% } %>&nbsp;*
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
			
			<af:showMessages prefix="M_Insert_RichEsonero"/>
			<af:showMessages prefix="M_Save_RichEsonero"/>	
			<af:showMessages prefix="SalvaRichEsonDoc"/>
			<af:showMessages prefix="M_Annullamento_RichEson_E_Doc"/>				
			<af:showErrors />
			
			<%= htmlStreamTop2 %>
			<%@ include file="_protocollazionerichiestaesonero.inc" %>
			
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
		          <td class="etichetta">Ambito Territoriale</td>
		          <td class="campo">
	                  <af:comboBox name="PROVINCIA_ISCR" moduleName="CM_GET_PROVINCIA_ISCR" selectedValue="<%=PROVINCIA_ISCR%>"
        				classNameBase="input" addBlank="true" title="Ambito Territoriale" required="true" disabled="<%=String.valueOf(!nuovaRichiesta)%>" />
		          </td>
		        </tr>		        
	        	        
				<tr>
				    <td class="etichetta">Stato richiesta</td>
				    <td class="campo">
				      <af:comboBox title="Stato richiesta" name="CODSTATORICHIESTA" moduleName="M_COMBO_STATO_RICH_ESON" addBlank="false" selectedValue="<%=CODSTATORICHIESTA%>" disabled="<%= fieldCodRich %>" classNameBase="input" onChange="fieldChanged()"/>&nbsp;*&nbsp;
				      <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('CODSTATORICHIESTA')"; </SCRIPT>
				      &nbsp;&nbsp;&nbsp;&nbsp;   
				      Data richiesta&nbsp;&nbsp;
				      <af:textBox title="Data richiesta" validateOnPost="true" disabled="false" type="date" name="DATRICHIESTA" onKeyUp="fieldChanged();" value="<%=DATRICHIESTA%>" size="12" maxlength="12" readonly="<%=fieldReadOnly%>" classNameBase="input" />&nbsp;*&nbsp;
				      <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('DATRICHIESTA')"; </SCRIPT>
				    </td>
				<tr>
				<%if(!nuovaRichiesta){%>
		        <tr>
		          <td class="etichetta">Base computo</td>
		          <td class="campo">
	                  <af:textBox title="Base computo" value="<%=NUMBASECOMPUTO%>" classNameBase="input" name="NUMBASECOMPUTO" size="10" readonly="true" />
		          </td>
		        </tr>
		        <%}%>
		        <tr>
		          <td class="etichetta">% Esonero</td>
		          <td class="campo">
					  <%if(!nuovaRichiesta){%>
	                  <af:textBox title="Percentuale di esonero" validateOnPost="true" value="<%=NUMPERCESONERO%>" classNameBase="input" size="3" name="NUMPERCESONERO" readonly="<%= fieldNumPercEson %>" onKeyUp="fieldChanged();" />&nbsp;*&nbsp;
	                  <%}else{%>
	                  <af:textBox title="Percentuale di esonero" validateOnPost="true" value="<%=NUMPERCESONERO%>" classNameBase="input" name="NUMPERCESONERO" size="3" readonly="false" onKeyUp="fieldChanged();" />&nbsp;*&nbsp;
	                  <%}%>
	                  <SCRIPT language="JavaScript"> _arrFunz[_arrIndex++]="isRequired('NUMPERCESONERO')"; </SCRIPT>
		          </td>
		        </tr>		        
		        <%if(!nuovaRichiesta){%>
		        <tr>
		          <td class="etichetta">Num disabili</td>
		          <td class="campo">
	                  <af:textBox title="Numero Disabili" value="<%=numDisabili%>" classNameBase="input" name="numDisabili" size="25" readonly="true" />
		          </td>
		        </tr>
		        <%}%>				
				<tr>
				    <td class="etichetta">Data inizio validit&agrave;&nbsp;</td>
				    <td class="campo">
				      <%if(!nuovaRichiesta){%>
				      <af:textBox validateOnPost="true" disabled="false" type="date" name="DATINIZIOVALIDITA" value="<%=DATINIZIOVALIDITA%>" size="12" maxlength="12" readonly="<%=dateReadOnly%>" classNameBase="input" onKeyUp="fieldChanged();" />
				      &nbsp;&nbsp;&nbsp;&nbsp;
				      Data fine validit&agrave;&nbsp;
				      <af:textBox validateOnPost="true" disabled="false" type="date" name="DATFINE" value="<%=DATFINE%>" size="12" maxlength="12" readonly="<%=dateReadOnly%>" classNameBase="input" onKeyUp="fieldChanged();" />
				      <%}else{%>
				      <af:textBox validateOnPost="true" disabled="false" type="date" name="DATINIZIOVALIDITA" value="<%=DATINIZIOVALIDITA%>" size="12" maxlength="12" readonly="false" classNameBase="input" onKeyUp="fieldChanged();" />
				      &nbsp;&nbsp;&nbsp;&nbsp;
				      Data fine validit&agrave;&nbsp;
				      <af:textBox validateOnPost="true" disabled="false" type="date" name="DATFINE" value="<%=DATFINE%>" size="12" maxlength="12" readonly="false" classNameBase="input" onKeyUp="fieldChanged();" />
				      <%}%>
				    </td>
				</tr>
				<tr>
				    <td class="etichetta">Motivazione&nbsp;</td>
				    <td class="campo">
				  		<af:textArea title="Motivazione" cols="80" rows="5" maxlength="1000" readonly="<%=fieldReadOnly%>" classNameBase="textarea"  
	                		name="STRMOTIVAZIONE" 
	                		value="<%=STRMOTIVAZIONE%>" validateOnPost="true" 
	                        required="true" onKeyUp="fieldChanged();"/>&nbsp;
	               </td>
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
				          	<input type="hidden" name="NUMKLOESONERO" value="1">	
			          <%  }
			          	 }if(!nuovaRichiesta){
			          	  	if (!CODSTATOATTO_P.equals("AN")) {%>
			          	 	<input class="pulsanti" type="submit" name="aggiorna" value="Aggiorna" onClick="annullamentoDoc()">
				          	&nbsp;&nbsp;
				          	
				          	<input type="reset" class="pulsanti" value="Annulla"/>
			          	  	&nbsp;&nbsp;
			          	  	<%}%>
							
							<%							  
							if (("AP").equalsIgnoreCase(CODSTATORICHIESTA)) {
							%>
								<input class="pulsanti" type="button" name="variazioni" value="Storico variazioni"  onclick="storicoVariaz()">
							<%
							}
							else {
							%>
								<input class="pulsanti" type="button" name="variazioni" value="Storico variazioni" disabled="true">
							<%
							}															
							%>														
							
		          	  		&nbsp;&nbsp;
							<input class="pulsanti" type="button" name="pagamenti" value="Pagamenti" onclick="pagamentiEson();">
							&nbsp;&nbsp;

			          	  	<input type="hidden" name="aggiornamento" value="1">
			          	  	<input type="hidden" name="NUMKLOESONERO" value="<%= NUMKLOESONERO %>">
			          	  	<input type="hidden" name="annullamento" value="0">
			          	  	<input type="hidden" name="updateVariazioni" value="0">
					   <% } %>
						  <input type="hidden" name="prgUnita" value="<%=prgUnita%>" />
				          <input type="button" class="pulsante" name="ricerca" value="<%=goBackButtonTitle%>" onclick="goBack()" />						  	
			          </td>
			        </tr>				
			      </table>
	              <br><br>

	    <%= htmlStreamBottom2 %> 
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
