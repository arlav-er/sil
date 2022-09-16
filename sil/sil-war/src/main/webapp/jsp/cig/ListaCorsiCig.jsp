<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page import="
  com.engiweb.framework.base.*,
  
  it.eng.sil.security.User,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  java.util.Date,
  java.text.DateFormat,
  java.text.SimpleDateFormat,
  it.eng.sil.security.PageAttribs,
  java.math.*" %>
  
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %> 
<%      
    SourceBean row = null;    
	BigDecimal numIscrAperte = null;
	BigDecimal prgUnicaIscr = null;
	BigDecimal prgUnicoPercorso = null;
	BigDecimal prgUnicoColloquio = null;
	BigDecimal numPercorsi = null;
	BigDecimal numPresaIncarico = null;
	BigDecimal cdnUtIns=null;
	BigDecimal cdnUtMod=null;
	SourceBean dataOrigin = null;
	
	BigDecimal operatore = null;
	
	boolean canModify = false;
	
	boolean canModifyCatalogo = false;
	
	String numidproposta = "";
	String numrecid = "";
	String strambitoprovinciale ="";
	String strsede = "";
	String strcodiceproposta="";
	
	String strnominativo="";
	String stremail="";
	String strdescrizionearea="";
	String strtitoloqualifica="";
	String strragionesocialeente="";
	String strNoteCatalogo="";
	String flgItalianoStranieri="";
	
	String datcolloquioCat = "";
	String datstimataCat = "";
	String esitoCat = "";
	String dateffettivaCat = "";
	String mostraAlert = "";
	Testata operatori_info=null;
	String dtmIns="";
	String dtmMod="";
	
	String datInizioIscrCat = ""; 
    String datFineIscrCat = ""; 
    String tipoIscrCat = ""; 
    String statoIscrCat = ""; 
    String codAccordoCat = "";
    
	PageAttribs attributi = new PageAttribs(user, "CigLavCorsiPage");
	
	String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");
    String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
    int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
    
    mostraAlert = StringUtils.getAttributeStrNotNull(serviceRequest,"mostraAlert");
    
    String tipoCorso = StringUtils.getAttributeStrNotNull(serviceRequest,"tipoCorso"); 
    String prgAltraIscr = StringUtils.getAttributeStrNotNull(serviceRequest,"prgAltraIscr");
    String codTipoIscr = StringUtils.getAttributeStrNotNull(serviceRequest,"codTipoIscr"); 
    String prgPercorso = StringUtils.getAttributeStrNotNull(serviceRequest,"prgPercorso");
    String prgColloquio = StringUtils.getAttributeStrNotNull(serviceRequest,"prgColloquio");
    
    String token = StringUtils.getAttributeStrNotNull(serviceResponse,"M_RecuperaInfoPerOrienter.ROWS.ROW.token");
    String cf = StringUtils.getAttributeStrNotNull(serviceResponse,"M_RecuperaInfoPerOrienter.ROWS.ROW.cf");
    String codAcc = StringUtils.getAttributeStrNotNull(serviceResponse,"M_RecuperaInfoPerOrienter.ROWS.ROW.codAcc");
    String nome = StringUtils.getAttributeStrNotNull(serviceResponse,"M_RecuperaInfoPerOrienter.ROWS.ROW.nome");
    String cogn = StringUtils.getAttributeStrNotNull(serviceResponse,"M_RecuperaInfoPerOrienter.ROWS.ROW.cogn");
    String strUrl = StringUtils.getAttributeStrNotNull(serviceResponse,"M_RecuperaInfoPerOrienter.ROWS.ROW.strvalore");
    String tel = StringUtils.getAttributeStrNotNull(serviceResponse,"M_RecuperaInfoPerOrienter.ROWS.ROW.tel");
    String strSede = StringUtils.getAttributeStrNotNull(serviceRequest,"strSede");  
   
    SourceBean row_cdn  = (SourceBean) serviceResponse.getAttribute("M_DettaglioCorsoOrienter.ROWS.ROW");
    if (row_cdn != null) {
	    cdnUtIns  = (BigDecimal)row_cdn.getAttribute("CDNUTINS");
	    cdnUtMod  = (BigDecimal)row_cdn.getAttribute("CDNUTMOD");
	    dtmIns= StringUtils.getAttributeStrNotNull(serviceResponse,"M_DettaglioCorsoOrienter.ROWS.ROW.dtmins");
	    dtmMod = StringUtils.getAttributeStrNotNull(serviceResponse,"M_DettaglioCorsoOrienter.ROWS.ROW.dtmmod");
	    operatori_info= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
	       
    }
    
   SourceBean row_cdnCat  = (SourceBean) serviceResponse.getAttribute("M_GetCatalogo.ROWS.ROW");
    if (row_cdnCat != null) {
	    cdnUtIns  = (BigDecimal)row_cdnCat.getAttribute("CDNUTINS");
	    cdnUtMod  = (BigDecimal)row_cdnCat.getAttribute("CDNUTMOD");
	    dtmIns= StringUtils.getAttributeStrNotNull(serviceResponse,"M_GetCatalogo.ROWS.ROW.dtmins");
	    dtmMod = StringUtils.getAttributeStrNotNull(serviceResponse,"M_GetCatalogo.ROWS.ROW.dtmmod");
	    operatori_info= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
	       
    }
    
    SourceBean row_Operatore  = (SourceBean) serviceResponse.getAttribute("MSPI_UTENTE.ROWS.ROW");
    if (row_Operatore != null) {
    
    	operatore  = (BigDecimal)row_Operatore.getAttribute("PRGSPI");
    }
       
    SourceBean bloccaPulsanti = null;
	bloccaPulsanti = (SourceBean) serviceResponse.getAttribute("M_BloccaPulsanti.ROWS.ROW");
	String disableCorsiCigInizio = null;
	disableCorsiCigInizio = (String) bloccaPulsanti.getAttribute("inizio");
	String disableCorsiCigFine = (String) bloccaPulsanti.getAttribute("fine");
	String oggi = (String) bloccaPulsanti.getAttribute("oggi");
	
	DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); 
	Date disableCorsiCigInizioDate = (Date)formatter.parse(disableCorsiCigInizio); 
	Date disableCorsiCigFineDate = (Date)formatter.parse(disableCorsiCigFine);
	Date oggiDate = (Date)formatter.parse(oggi);
	
//	(oggiDate.after(inizioDate) || oggiDate.)
	
	boolean disableCorsiCigBlocco = (oggiDate.compareTo(disableCorsiCigInizioDate) >= 0) && (oggiDate.compareTo(disableCorsiCigFineDate) < 0);
	
    String prgcorsoci = serviceResponse.getAttribute("M_DettaglioCorsoOrienter.ROWS.ROW.prgcorsoci") == null ? "" : ((BigDecimal)serviceResponse.getAttribute("M_DettaglioCorsoOrienter.ROWS.ROW.prgcorsoci")).toString();    	
    String ragSoc = StringUtils.getAttributeStrNotNull(serviceResponse,"M_DettaglioCorsoOrienter.ROWS.ROW.strragionesociale");
    String indirizzo = StringUtils.getAttributeStrNotNull(serviceResponse,"M_DettaglioCorsoOrienter.ROWS.ROW.strindirizzo");
    String comune = StringUtils.getAttributeStrNotNull(serviceResponse,"M_DettaglioCorsoOrienter.ROWS.ROW.strdenominazione");
    String cognReferente = StringUtils.getAttributeStrNotNull(serviceResponse,"M_DettaglioCorsoOrienter.ROWS.ROW.strreferentecognome");
    String nomeReferente = StringUtils.getAttributeStrNotNull(serviceResponse,"M_DettaglioCorsoOrienter.ROWS.ROW.strreferentenome");
    cognReferente = cognReferente + " " + nomeReferente;
    String telReferente = StringUtils.getAttributeStrNotNull(serviceResponse,"M_DettaglioCorsoOrienter.ROWS.ROW.strreferentetel");
    String codSede = StringUtils.getAttributeStrNotNull(serviceResponse,"M_DettaglioCorsoOrienter.ROWS.ROW.codsede");
	if (codSede != null && !codSede.equals("")) {
    	codSede = "(" + codSede + ")";    
	}
    String comSede = StringUtils.getAttributeStrNotNull(serviceResponse,"M_DettaglioCorsoOrienter.ROWS.ROW.comsede");
    String codRifPA = StringUtils.getAttributeStrNotNull(serviceResponse,"M_DettaglioCorsoOrienter.ROWS.ROW.codrifpa");
    String descrizioneRifPA = StringUtils.getAttributeStrNotNull(serviceResponse,"M_DettaglioCorsoOrienter.ROWS.ROW.strdescrizionerifpa");
    String strNote = StringUtils.getAttributeStrNotNull(serviceResponse,"M_DettaglioCorsoOrienter.ROWS.ROW.note");    
    String datInizioPrev = StringUtils.getAttributeStrNotNull(serviceResponse,"M_DettaglioCorsoOrienter.ROWS.ROW.datinizioprev");
    String datFinePrev = StringUtils.getAttributeStrNotNull(serviceResponse,"M_DettaglioCorsoOrienter.ROWS.ROW.datfineprev");    
    String datInizio = StringUtils.getAttributeStrNotNull(serviceResponse,"M_DettaglioCorsoOrienter.ROWS.ROW.datinizio");
    String datFine = StringUtils.getAttributeStrNotNull(serviceResponse,"M_DettaglioCorsoOrienter.ROWS.ROW.datfine");
    String datPresaInCarico = StringUtils.getAttributeStrNotNull(serviceResponse,"M_DettaglioCorsoOrienter.ROWS.ROW.datpresaincarico");
    String datRitiro = StringUtils.getAttributeStrNotNull(serviceResponse,"M_DettaglioCorsoOrienter.ROWS.ROW.datritiro");
    String motivoRitiro = StringUtils.getAttributeStrNotNull(serviceResponse,"M_DettaglioCorsoOrienter.ROWS.ROW.strmotivoritiro");
    
    String datColloquio = StringUtils.getAttributeStrNotNull(serviceResponse,"M_DettaglioCorsoOrienter.ROWS.ROW.datcolloquio");
    String datStimata = StringUtils.getAttributeStrNotNull(serviceResponse,"M_DettaglioCorsoOrienter.ROWS.ROW.datstimata");
    String esito = StringUtils.getAttributeStrNotNull(serviceResponse,"M_DettaglioCorsoOrienter.ROWS.ROW.esito");
    String datEffettiva = StringUtils.getAttributeStrNotNull(serviceResponse,"M_DettaglioCorsoOrienter.ROWS.ROW.dateffettiva");
    
    String datInizioIscr = StringUtils.getAttributeStrNotNull(serviceResponse,"M_DettaglioCorsoOrienter.ROWS.ROW.datInizioIscr");
    String datFineIscr = StringUtils.getAttributeStrNotNull(serviceResponse,"M_DettaglioCorsoOrienter.ROWS.ROW.datFineIscr");
    String tipoIscr = StringUtils.getAttributeStrNotNull(serviceResponse,"M_DettaglioCorsoOrienter.ROWS.ROW.tipoIscr");
    String statoIscr = StringUtils.getAttributeStrNotNull(serviceResponse,"M_DettaglioCorsoOrienter.ROWS.ROW.statoIscr");
    String codAccordo = StringUtils.getAttributeStrNotNull(serviceResponse,"M_DettaglioCorsoOrienter.ROWS.ROW.codAccordo");
    
    
   
    if(serviceResponse.containsAttribute("M_ContaIscrAperte.ROWS.ROW.numIscrAperte")){ 
    	numIscrAperte = (BigDecimal)serviceResponse.getAttribute("M_ContaIscrAperte.ROWS.ROW.numIscrAperte"); 
    
    	if(numIscrAperte.compareTo(new BigDecimal("1"))==0){ 
    		prgUnicaIscr = (BigDecimal)serviceResponse.getAttribute("M_ListaIscrAperteCorsiCig.ROWS.ROW.prgAltraIscr");
    		prgAltraIscr = prgUnicaIscr.toString();
    		codTipoIscr = (String)serviceResponse.getAttribute("M_ListaIscrAperteCorsiCig.ROWS.ROW.codTipoIscr");
    	}
    }
    
    if(serviceResponse.containsAttribute("M_ContaOrPercorsi.ROWS.ROW.numPercorsi")){ 
    	numPercorsi = (BigDecimal)serviceResponse.getAttribute("M_ContaOrPercorsi.ROWS.ROW.numPercorsi"); 
    
    	if(numPercorsi.compareTo(new BigDecimal("1"))==0){ 
    		prgUnicoPercorso = (BigDecimal)serviceResponse.getAttribute("M_ListaOrPercConcordato.ROWS.ROW.prgPercorso");
    		prgUnicoColloquio = (BigDecimal)serviceResponse.getAttribute("M_ListaOrPercConcordato.ROWS.ROW.prgColloquio");
    		prgPercorso = prgUnicoPercorso.toString(); 
    		prgColloquio = prgUnicoColloquio.toString();
    	}
    }
    
    if(serviceResponse.containsAttribute("M_ContaPresaIncarico.ROWS.ROW.numPresaIncarico")){ 
    	numPresaIncarico = (BigDecimal)serviceResponse.getAttribute("M_ContaPresaIncarico.ROWS.ROW.numPresaIncarico");    	
    }
    
    SourceBean rowCatalogo = (SourceBean)serviceResponse.getAttribute("M_GetCatalogo.ROWS.ROW");
    if(rowCatalogo != null) {
    	
    	prgcorsoci = rowCatalogo.getAttribute("prgcorsoci") == null ? "" : ((BigDecimal)rowCatalogo.getAttribute("prgcorsoci")).toString();
    	datInizioPrev = Utils.notNull(rowCatalogo.getAttribute("datinizioprev"));
        datFinePrev = Utils.notNull(rowCatalogo.getAttribute("datfineprev"));   
        datInizio = Utils.notNull(rowCatalogo.getAttribute("datinizio"));
        datFine = Utils.notNull(rowCatalogo.getAttribute("datfine"));
        datPresaInCarico = Utils.notNull(rowCatalogo.getAttribute("datpresaincarico"));
        datRitiro = Utils.notNull(rowCatalogo.getAttribute("datritiro"));
        motivoRitiro = Utils.notNull(rowCatalogo.getAttribute("strmotivoritiro"));
    	
    	strambitoprovinciale = Utils.notNull(rowCatalogo.getAttribute("strambitoprovinciale"));
    	strcodiceproposta = Utils.notNull(rowCatalogo.getAttribute("strcodiceproposta"));
    	strsede = Utils.notNull(rowCatalogo.getAttribute("strsede"));
    	strnominativo = Utils.notNull(rowCatalogo.getAttribute("STRNOMINATIVO"));
    	stremail = Utils.notNull(rowCatalogo.getAttribute("STREMAIL"));
    	strdescrizionearea = Utils.notNull(rowCatalogo.getAttribute("STRDESCRIZIONEAREA"));
    	strtitoloqualifica = Utils.notNull(rowCatalogo.getAttribute("STRTITOLOQUALIFICA"));
    	strragionesocialeente = Utils.notNull(rowCatalogo.getAttribute("STRRAGIONESOCIALEENTE"));
    	strNoteCatalogo = Utils.notNull(rowCatalogo.getAttribute("STRNOTE"));
    	flgItalianoStranieri = Utils.notNull(rowCatalogo.getAttribute("flgItalianoStranieri"));
    	
    	datcolloquioCat = Utils.notNull(rowCatalogo.getAttribute("datcolloquio"));
    	datstimataCat = Utils.notNull(rowCatalogo.getAttribute("datstimata"));
    	esitoCat = Utils.notNull(rowCatalogo.getAttribute("esito"));
    	dateffettivaCat = Utils.notNull(rowCatalogo.getAttribute("dateffettiva"));
    	
    	datInizioIscrCat = Utils.notNull(rowCatalogo.getAttribute("datInizioIscr")); 
        datFineIscrCat = Utils.notNull(rowCatalogo.getAttribute("datFineIscr")); 
        tipoIscrCat = Utils.notNull(rowCatalogo.getAttribute("tipoIscr")); 
        statoIscrCat = Utils.notNull(rowCatalogo.getAttribute("statoIscr")); 
        codAccordoCat = Utils.notNull(rowCatalogo.getAttribute("codAccordo")); 
    	    	    	
    	
    	canModify = true;
    }
    
    
    String apriDivListaIscr = (serviceRequest.containsAttribute("APRIDIVLISTAISCR"))?"":"none";
    String apriDivDettCorsoOrienter = (serviceRequest.containsAttribute("APRIDIVDETTORIENTER"))?"":"none";
    
    String apriDivOrPercConcordato = "none";
    String apriDivOrienter = "none";
    String apriDivCorsi = "none";
    
    if(prgUnicoPercorso!=null){
    	if(tipoCorso.equals("orienter")) { apriDivOrienter = ""; }
    	if(tipoCorso.equals("catalogo")) { apriDivCorsi = ""; }
    	apriDivOrPercConcordato = "none";
    }else{
    	apriDivOrienter = (serviceRequest.containsAttribute("APRIDIVORIENTER"))?"":"none";
    	apriDivOrPercConcordato = (serviceRequest.containsAttribute("APRIDIVPERCCONCORDATO"))?"":"none";
    	apriDivCorsi = (serviceRequest.containsAttribute("apriDivCorsi"))?"":"none";
    }
    
    String url_listaIscr = "AdapterHTTP?PAGE=CigLavCorsiPage" +
	  					  "&cdnLavoratore=" + cdnLavoratore +
	  					  "&cdnFunzione=" + cdnFunzione +
	  					  "&APRIDIVLISTAISCR=1";
    
    String url_nuovo = "AdapterHTTP?PAGE=CigLavCorsiPage" +
						"&cdnLavoratore=" + cdnLavoratore +
						"&cdnFunzione=" + cdnFunzione +
						"&apriDivCorsi=1";

%>

<html>
<head>
  <title>Domande Cig</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css"/> 
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
  <af:linkScript path="../../js/"/>
  <%@ include file="RicercaSoggettoCIG.inc" %>
  <SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>
  <SCRIPT TYPE="text/javascript">
  <%
  	String codCPI = user.getCodRif();
  	String cdnUt = ""+user.getCodut();
  	//String url_orienter = strUrl + "&token="+token+"&cf="+cf+"&codAcc="+codAcc+"&codCPI="+codCPI+"&nome="+nome+"&cogn="+cogn+"&codPerc="+prgPercorso+"&codColl="+prgColloquio+"&codOp="+cdnUt+"&tel="+tel;
  	String url_orienter = strUrl + "&token="+token+"&cf="+cf+"&codAcc="+codAcc+"&tipo="+codTipoIscr+"&codCPI="+codCPI+"&nome="+nome+"&cogn="+cogn+"&codPerc="+prgPercorso+"&codColl="+prgColloquio+"&codOp="+cdnUt+"&tel="+tel;
  %>
  var flagChanged = false;
  
  function scegli_iscrizione(prgAltraIscr, codTipoIscr) {
	  	if (isInSubmit()) return;
	    var tipoCorso = ""+"<%=tipoCorso%>";
	    
	    var s= "AdapterHTTP?"
	    s += "PAGE=CigLavCorsiPage&";
	    s += "prgAltraIscr=" + prgAltraIscr+"&";
	    s += "codTipoIscr=" + codTipoIscr+"&";
	    s += "CDNLAVORATORE=<%= cdnLavoratore %>&";
	    s += "CDNFUNZIONE=<%= cdnFunzione %>&";
	    s += "tipoCorso=" + tipoCorso+"&";
	    s += "APRIDIVPERCCONCORDATO=1";
	    setWindowLocation(s);
	  }
  
  function scegli_unica_iscrizione(prgAltraIscr, tipoCorso, codTipoIscr) {
	  	if (isInSubmit()) return;
	  	<%String control = (String) serviceResponse.getAttribute("M_ControlCorsiCig.CONTROLCORSO");
		if ("OK".equals(control)) {%>
				var s= "AdapterHTTP?"
				s += "PAGE=CigLavCorsiPage&";
				s += "prgAltraIscr=" + prgAltraIscr+"&";
				s += "codTipoIscr=" + codTipoIscr+"&";
				s += "CDNLAVORATORE=<%=cdnLavoratore%>&";
				s += "CDNFUNZIONE=<%=cdnFunzione%>&";
				s += "tipoCorso=" + tipoCorso+"&";
				s += "APRIDIVPERCCONCORDATO=1";
				setWindowLocation(s);
		<%} else if("KL".equals(control)){%>
	    			alert('Il lavoratore ha raggiunto il numero massimo di corsi.');
	   		  <%}else{%>
	   				 alert('Iscrizione disabilitata.');	
	    <%}%>
	  }
  
  function scegli_percorso(prgPercorso, prgColloquio) {
  	if (isInSubmit()) return;
    	
	var s= "AdapterHTTP?"
    s += "PAGE=CigLavCorsiPage&";
    s += "prgPercorso=" + prgPercorso+"&";
    s += "prgColloquio=" + prgColloquio +"&";
    s += "prgAltraIscr=<%= prgAltraIscr %>&";
    s += "codTipoIscr=<%= codTipoIscr %>&";
    s += "CDNLAVORATORE=<%= cdnLavoratore %>&";
    s += "CDNFUNZIONE=<%= cdnFunzione %>&";
    <% if(tipoCorso.equals("orienter")) { %>
    	s += "APRIDIVORIENTER=1";
    <% } if(tipoCorso.equals("catalogo")) { %>
    	s += "apriDivCorsi=1";
    <% } %>
    setWindowLocation(s);
  }
  
  function dettaglioCorso(prgCorso, orienter) {
  	if (isInSubmit()) return;
    	
   	var s= "AdapterHTTP?"
    s += "PAGE=CigLavCorsiPage&";
    s += "CDNFUNZIONE=<%= cdnFunzione %>&";
    s += "PRGCORSO=" + prgCorso + "&";
    s += "CDNLAVORATORE=<%= cdnLavoratore %>&";
    s += "PRGCORSOCI=" + prgCorso + "&";
    if(orienter=="0") s += "APRIDIVDETTORIENTER=1";
    else s += "apriDivCorsi=1";
    setWindowLocation(s);
  }
  
  var flagChanged = false;
  
  function fieldChanged() {
  	flagChanged = true; 
  }
  
  function deleteCorso(prgCorso){
 	  if (isInSubmit()) return;
	  
	  var s="Eliminare il corso?";
      if ( confirm(s) ) {
		var s= "AdapterHTTP?";
	    s += "PAGE=CigLavCorsiPage&";
	    s += "MODULE=M_DeleteCorso&";
	    s += "prgCorsoCi=" + prgCorso + "&";
	    s += "CDNLAVORATORE=<%= cdnLavoratore %>&";
	    s += "CDNFUNZIONE=<%=cdnFunzione%>";
	    setWindowLocation(s);
      }
      return;
  }
  
  
function apriSezioneCatalogo(strcodiceproposta) {
  		var f = "AdapterHTTP?PAGE=ApriListaCatalogoPage&strcodiceproposta=" + strcodiceproposta + "&CDNFUNZIONE=<%= cdnFunzione %>";
        var t = "_blank"; 
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=750,height=650,top=30,left=180";
        window.open(f, t, feat);
  }
  
  function PulisciRicercaCorsi(strcodiceproposta, strcodicepropostahid,strsede) {
  	if (strcodiceproposta.value != strcodicepropostahid.value) {
        strsede.value="";
    }
  }

  function UpdateNoteCorso(strnote) {
	  if (isInSubmit()) return;
  		
		var s= "AdapterHTTP?"
	    s += "PAGE=CigLavCorsiPage&";
		s += "MODULE=M_UPDATENOTECORSO&";
		s += "strNote="+strnote.value+"&";
	    s += "prgcorsoci=<%= prgcorsoci%>&";	  
	    s += "prgAltraIscr=<%= prgAltraIscr %>&";
	    s += "CDNLAVORATORE=<%= cdnLavoratore %>&";
	    s += "updateNoteCorso=1&";	    
	    s += "CDNFUNZIONE=<%= cdnFunzione %>";
	    <% if(tipoCorso.equals("orienter")) { %>
    	s += "APRIDIVORIENTER=1";
    	<% } if(tipoCorso.equals("catalogo")) { %>
    		s += "apriDivCorsi=1";
   	 	<% } %>
   	 	
	    setWindowLocation(s);
  }

  function ricaricaPagina() {
  	if (isInSubmit()) return;

  	mostraMsg();
	var s= "AdapterHTTP?"
		s += "PAGE=CigLavCorsiPage&";
		s += "CDNLAVORATORE=<%= cdnLavoratore %>&";
		s += "CDNFUNZIONE=<%= cdnFunzione %>&";
		s += "mostraAlert=S";
		setWindowLocation(s);
  }

  function mostraMsg(){
	//var msg = '<%=mostraAlert%>';
	//if(msg == 'S') {
		alert("L'elenco dei corsi potrebbe non essere allineato in tempo reale, in quanto intervengono comunicazioni con sistemi esterni al SIL.");	
	//}
	return true;
  }


  function controllaOperatore(){
	  alert("Attenzione: per poter inserire un corso del catalogo è necessario associare un operatore all'utente in corso.");
  }

  

  
</SCRIPT>
</head>

<body class="gestione" onload="rinfresca();">

	<%  
   		InfCorrentiLav _testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
        
        _testata.show(out);
        Linguette _linguetta = new Linguette(user, 1 , "CigLavCorsiPage", new BigDecimal(cdnLavoratore)); 
        _linguetta.show(out);
    %>
	<center>   
		<af:showMessages prefix="M_InsertCorsoOrienter"/>
		<af:showMessages prefix="M_DeleteCorso"/>
		<af:showMessages prefix="M_InsertCorso"/>
		<af:showMessages prefix="M_UPDATENOTECORSO"/>  
		<af:showErrors/>
	</center>
	  
	<div align="center">
	
	<af:list moduleName="M_ListaLavCorsiCig" configProviderClass="it.eng.sil.module.cig.DynamicListaCorsiCigConfig" jsSelect="dettaglioCorso" jsDelete="deleteCorso" skipNavigationButton="1" getBack="true"/> 
	
	<input onClick="<% if (disableCorsiCigBlocco) { %>
            				alert('Questa procedura sarà sospesa fino al <%=disableCorsiCigFine%>.');
            	    <%} else {%>
	
						<%if(prgUnicaIscr!=null){%>
												scegli_unica_iscrizione(<%=prgUnicaIscr.toString()%>,'orienter','<%=codTipoIscr.toString() %>')
										<%}else{%>
						  						apriNuovoDivLayer(false,'divLayerListaIscr','<%=url_listaIscr%>'+'&tipoCorso=orienter')		
						  				<%}
						}%>"
	
	type="button" class="pulsanti"  value="Gestione Corsi Orienter"/>
	
	<input onClick="
		<% if (disableCorsiCigBlocco) { %>
        	alert('Questa procedura sarà sospesa fino al <%=disableCorsiCigFine%>.');
        <%} else {%>
			<%if (operatore != null) { %>
		 		<%if(prgUnicaIscr!=null){%>
					scegli_unica_iscrizione(<%=prgUnicaIscr.toString()%>,'catalogo','<%=codTipoIscr.toString() %>')
				<%}else{%>
	  				apriNuovoDivLayer(false,'divLayerListaIscr','<%=url_listaIscr%>'+'&tipoCorso=catalogo')	  			
	  			<%} %>		
	  		<%} else  {%>	  				
	  					controllaOperatore();
			<%}
			}%>"	
	
	type="button" class="pulsanti" value="Nuovo Corso Catalogo"/>         
	
	</div>
	<%
  		String divStreamTop = StyleUtils.roundLayerTop(!canModify);
  		String divStreamBottom = StyleUtils.roundLayerBottom(!canModify); 
  		
  		String divStreamTopCatalogo = StyleUtils.roundLayerTop(!canModifyCatalogo);
  		String divStreamBottomCatalogo = StyleUtils.roundLayerBottom(!canModifyCatalogo);  
  	%>
    <%if(!apriDivOrienter.equals("none")){ %>
	<div id="divLayerOrienter" name="divLayerOrienter" class="t_layerDett"
       style="position:absolute; width:100%;left:0; top:0px; z-index:6; display:<%=apriDivOrienter%>;">
       <%out.print(divStreamTop);%>
       <table width="100%">
     	<tr>
        	<td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerOrienter');return false"></td>
            <td height="16" class="azzurro_bianco">
            	Gestione Corsi Orienter   
            </td>
            <td width="16" height="16" onClick="ChiudiDivLayer('divLayerOrienter');ricaricaPagina()" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
        </tr>
      </table>
       <table width="100%" class="main">      	
      	<tr>
<!--
      		<td>
      		    <af:form name="frmInsertCorsoOrienter" action="AdapterHTTP" method="POST">
    				<input type="hidden" name="PAGE" value="CigLavCorsiPage">
      				<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
    				<input type="hidden" name="PRGALTRAISCR" value="<%=prgAltraIscr%>">
    				<input type="hidden" name="PRGCOLLOQUIO" value="<%=prgColloquio %>">
    				<input type="hidden" name="PRGPERCORSO" value="<%=prgPercorso %>">
    				<input type="hidden" name="INSERTCORSOORIENTER" value="">
    				<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>">
    				
    				<table class="main">
						<tr>
						  <td class="etichetta">Rif. PA</td>
						  <td class="campo">
						  	<af:textBox name="CODRIFPA"
						  				  title="Rif. PA"	
							              size="20"
							              maxlength="20"
							              type="text"
							              readonly="false"
							              classNameBase="input"
							              value=""
							              required="true"
							              onKeyUp="fieldChanged()"/>
						  </td>
						</tr>
						<tr>
						  <td class="etichetta">Descrizione</td>
						  <td class="campo">
						  	<af:textArea name="STRDESCRIZIONERIFPA" 
						  			 cols="40" 
					                 rows="4" 
					                 title="Descrizione"
					                 readonly="false"
					                 classNameBase="input"
					                 value=""
					                 required="true"
					                 onKeyUp="fieldChanged()"
					                 maxlength="400"/>
						  </td>
						</tr>
						<tr>
						  <td class="etichetta">Codice sede</td>
						  <td class="campo">
						  	<af:textBox name="codsede"
						  				  title="Codice sede"	
							              size="20"
							              maxlength="20"
							              type="text"
							              readonly="false"
							              classNameBase="input"
							              value=""
							              required="true"
							              onKeyUp="fieldChanged()"/>
						  </td>
						</tr>
						<tr>
						  <td class="etichetta">Note</td>
						  <td class="campo">
						  	<af:textArea name="strnote" 
					                 cols="40" 
					                 rows="4" 
					                 title="Note"
					                 readonly="false"
					                 classNameBase="input"
					                 value=""
					                 onKeyUp="fieldChanged()"
					                 maxlength="2000"/>
						  </td>
						</tr>
						<tr>
						  <td class="etichetta" nowrap>Data inizio prev.</td>
						  <td class="campo">
						  	<af:textBox name="datInizioPrev"
							              size="12"
							              maxlength="10"
							              type="date"
							              readonly="false"
							              classNameBase="input"
							              value=""
							              onKeyUp="fieldChanged()"
							              title="Data inizio prev."
							              validateOnPost="true"/>
						  </td>
						</tr>
						<tr>
						  <td class="etichetta" nowrap>Data fine prev.</td>
						  <td class="campo">
						  	<af:textBox name="datFinePrev"
							              size="12"
							              maxlength="10"
							              type="date"
							              readonly="false"
							              classNameBase="input"
							              value=""
							              onKeyUp="fieldChanged()"
							              title="Data fine prev."
							              validateOnPost="true"/>
						  </td>
						</tr>
						<tr>
							<td>
							</td>
							<td>
								<input type="submit" class="pulsanti" name="inserisci" value="Inserisci">
							</td>
						</tr>
	      			</table>
      			</af:form>
      		</td>
      		 -->
        	<td width="100%">
        		<iframe src="<%=url_orienter%>" height="500" width="100%"></iframe>
        	</td>
        </tr>
      </table>
      <%out.print(divStreamBottom);%>
    </div>
    <%} %>
    <div id="divLayerListaIscr" name="divLayerListaIscr" class="t_layerDett"
       style="position:absolute; width:90%; left:30; top:150px; z-index:6; display:<%=apriDivListaIscr%>;">
       <%out.print(divStreamTop);%>
       <table width="100%">
     	<tr>
        	<td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerListaIscr');return false"></td>
            <td height="16" class="azzurro_bianco">
            	Lista Iscrizioni Aperte   
            </td>
            <td width="16" height="16" onClick="ChiudiDivLayer('divLayerListaIscr')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
        </tr>
      </table>
      	
      <af:list moduleName="M_ListaIscrAperteCorsiCig" skipNavigationButton="1" jsSelect="scegli_iscrizione"/> 
      
      <%if(!apriDivListaIscr.equals("none")&&numIscrAperte!=null&&numIscrAperte.equals(new BigDecimal("0"))){ %>
      	<script type="text/javascript">
      		alert("Per effettuare l'iscrizione ad un corso il lavoratore deve avere un'iscrizione CIG o un altro tipo di iscrizione compatibile aperta.");
      		ChiudiDivLayer('divLayerListaIscr');
      	</script>
      <%} %>
      
      <%out.print(divStreamBottom);%>
    </div>
    
    <div id="divLayerOrPercConcordato" name="divLayerOrPercConcordato" class="t_layerDett"
       style="position:absolute; width:90%; left:30; top:150px; z-index:6; display:<%=apriDivOrPercConcordato%>;">
       <%if (numPresaIncarico != null && numPresaIncarico.equals(new BigDecimal("0"))) { %>
       		<script type="text/javascript">
	       		alert("Per iscrivere un lavoratore al corso è necessaria un'azione di presa in carico CIG in stato presentato o concluso nel percorso concordato.");
	  			ChiudiDivLayer('divLayerOrPercConcordato');
  			</script>
       <%} else { %>
       <%out.print(divStreamTop);%>
       <table width="100%">
     	<tr>
        	<td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerOrPercConcordato');return false"></td>
            <td height="16" class="azzurro_bianco">
            	Lista Percorsi Concordati   
            </td>
            <td width="16" height="16" onClick="ChiudiDivLayer('divLayerOrPercConcordato')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
        </tr>
      </table>
      <af:list moduleName="M_ListaOrPercConcordato" skipNavigationButton="1" jsSelect="scegli_percorso"/>
		    <%if (numPresaIncarico == null || !numPresaIncarico.equals(new BigDecimal("0"))) {%>
		        <%if(!apriDivOrPercConcordato.equals("none")&&numPercorsi!=null&&numPercorsi.equals(new BigDecimal("0"))){ %>			  
				    <input type="button" class="pulsanti" onClick="scegli_percorso('','')" value="Continua"/>
			    <%}out.print(divStreamBottom);%>
			    <%if(!apriDivOrPercConcordato.equals("none")&&numPercorsi!=null&&numPercorsi.equals(new BigDecimal("0"))){ %>
			      	<script type="text/javascript">
			      		alert("Prima di effettuare l'iscrizione ad un corso sarebbe opportuno inserire un'apposita azione nel percorso concordato. E' comunque possibile proseguire senza associare il corso a nessuna azione facendo click su continua.");
			      	</script>
			    <%} %>
		    <%} %>			  		      	      
		<%} %>    
    </div>
    
    <%if(!apriDivDettCorsoOrienter.equals("none")){ %>
    
    <div id="divLayerDettCorsoOrienter" name="divLayerDettCorsoOrienter" class="t_layerDett"
       style="position:absolute; width:90%; left:30; top:150px; z-index:6; display:<%=apriDivDettCorsoOrienter%>;">
       <%out.print(divStreamTop);%>
       <table width="100%">
     	<tr>
        	<td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDettCorsoOrienter');return false"></td>
            <td height="16" class="azzurro_bianco">
            	Dettaglio Corso Orienter   
            </td>
            <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDettCorsoOrienter')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
        </tr>
      </table>
      <div class='sezione2' id='SedeAzienda'>
	  	<img id='tendinaAzienda' alt="Chiudi" src="../../img/aperto.gif" onclick='cambia(this, document.getElementById("aziendaSez"));'/>&nbsp;&nbsp;&nbsp;Ente&nbsp;&nbsp;
      </div>
	 
	  <div id="aziendaSez" style="display:1">
     	<table class="main">
       		<tr>
           		<td class="etichetta" nowrap>Ragione Sociale</td>
                <td class="campo">
               		<af:textBox classNameBase="input" type="text" name="ragioneSociale" readonly="true" value="<%=ragSoc %>" size="40" maxlength="100"/>
               	</td>
           </tr>
           <tr>
          		<td class="etichetta">Indirizzo
          		</td>
               	<td class="campo">
              		<af:textBox classNameBase="input" type="text" name="indirizzo" readonly="true" value="<%=indirizzo %>" size="40" maxlength="100"/>
               	</td>
               	<td class="etichetta">Comune
               	</td>
               	<td class="campo">
                	<af:textBox classNameBase="input" type="text" name="comune" readonly="true" value="<%=comune %>" size="40" maxlength="100"/>
              	</td>
           </tr>
           <tr>
           		<td class="etichetta">Referente
               	</td>
               	<td class="campo" colspan="3">
                	<af:textBox classNameBase="input" type="text" name="cognReferente" readonly="true" value="<%=cognReferente %>" size="30" maxlength="100"/>
              	</td>
           </tr>
           <%-- 
		   <tr>
           		<td class="etichetta">Cognome Referente
               	</td>
               	<td class="campo">
                	<af:textBox classNameBase="input" type="text" name="cognReferente" readonly="true" value="<%=cognReferente %>" size="30" maxlength="100"/>
              	</td>
              	<td class="etichetta">Nome Referente
               	</td>
               	<td class="campo">
                	<af:textBox classNameBase="input" type="text" name="nomeReferente" readonly="true" value="<%=nomeReferente %>" size="30" maxlength="100"/>
              	</td>
           --%>
           </tr>
           
           <tr>
           		<td class="etichetta">Telefono Referente
               	</td>
               	<td class="campo">
                	<af:textBox classNameBase="input" type="text" name="telReferente" readonly="true" value="<%=telReferente %>" size="30" maxlength="100"/>
              	</td>
               	<td class="etichetta">Codice Sede
               	</td>
               	<td class="campo">
                	<b><%=comSede %></b>&nbsp;<af:textBox classNameBase="input" type="text" name="codSede" readonly="true" value="<%=codSede %>" size="30" maxlength="100"/>
              	</td>
           </tr>
       </table>
     </div>
     <div class='sezione2' id='dettCorso'>
	  	<img id='tendinaAzienda' alt="Chiudi" src="../../img/aperto.gif" onclick='cambia(this, document.getElementById("corsoSez"));'/>&nbsp;&nbsp;&nbsp;Corso&nbsp;&nbsp;
     </div>
     <div id="corsoSez" style="display:1">
       <af:form name="Frm2" action="AdapterHTTP" method="POST">
	   <input type="hidden" name="PAGE" value="CigLavCorsiPage">
	   <input type="hidden" name="MODULE" value="M_UPDATENOTECORSO">
	   <input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">	  
	   <input type="hidden" name="updateNoteCorso" value="1">
	   
     	<table class="main">
     		<tr>
           		<td class="etichetta">Codice corso
               	</td>
               	<td class="campo">
                	<af:textBox classNameBase="input" type="text" name="codRifPA" readonly="true" value="<%=codRifPA %>" size="30" maxlength="100"/>
              	</td>
            </tr>
            <tr>
              	<td class="etichetta">Descrizione
               	</td>
               	<td class="campo" colspan="3">
               		<af:textArea name="descrizioneRifPA" 
						  			 cols="40" 
					                 rows="4" 
					                 title="Descrizione"
					                 readonly="true"
					                 classNameBase="input"					                 
					                 required="false"
					                 maxlength="400"
					                 value="<%=descrizioneRifPA %>"/>
              	</td>
           </tr>                      	    			
           <tr>
              	<td class="etichetta">Note
               	</td>
               	<td class="campo" colspan="3">
               		<af:textArea name="note" 
						  			 cols="40" 
					                 rows="4" 
					                 title="Note"					                 
					                 classNameBase="input"					                 
					                 required="true"
					                 maxlength="2000"
					                 value="<%=strNote %>"/>
              	</td>
           </tr>
           <tr>
           		<td class="etichetta" nowrap>Data inizio prevista
               	</td>
               	<td class="campo">
                	<af:textBox classNameBase="input" type="text" name="datInizioPrev" readonly="true" value="<%=datInizioPrev %>" size="15" maxlength="100"/>
              	</td>
              	<td class="etichetta" nowrap>Data fine prevista
               	</td>
               	<td class="campo">
                	<af:textBox classNameBase="input" type="text" name="datFinePrev" readonly="true" value="<%=datFinePrev %>" size="15" maxlength="100"/>
              	</td>
           </tr>
           <tr>
           		<td class="etichetta" nowrap>Data inizio effettiva
               	</td>
               	<td class="campo">
                	<af:textBox classNameBase="input" type="text" name="datInizio" readonly="true" value="<%=datInizio %>" size="15" maxlength="100"/>
              	</td>
              	<td class="etichetta" nowrap>Data fine effettiva
               	</td>
               	<td class="campo">
                	<af:textBox classNameBase="input" type="text" name="datFine" readonly="true" value="<%=datFine %>" size="15" maxlength="100"/>
              	</td>
           </tr>
           <tr>
           		<td class="etichetta" nowrap>Data presa in carico ente
               	</td>
               	<td class="campo">
                	<af:textBox classNameBase="input" type="text" name="datPresaInCarico" readonly="true" value="<%=datPresaInCarico %>" size="15" maxlength="100"/>
              	</td>
           </tr>
           <tr>
           		<td class="etichetta" nowrap>Data ritiro corso
               	</td>
               	<td class="campo">
                	<af:textBox classNameBase="input" type="text" name="datRitiro" readonly="true" value="<%=datRitiro %>" size="15" maxlength="100"/>
              	</td>
              	<td class="etichetta">Motivo ritiro
               	</td>
               	<td class="campo">
                	<af:textBox classNameBase="input" type="text" name="motivoRitiro" readonly="true" value="<%=motivoRitiro %>" size="40" maxlength="100"/>
              	</td>
           </tr>
           <tr>
           		<td colspan="4">
           			&nbsp;
               	</td>           
           </tr> 
       </table>
     </af:form>
     </div>
     
     <div class='sezione2' id='Iscrizione'>
	  	<img id='tendinaIscrizione' alt="Chiudi" src="../../img/chiuso.gif" onclick='cambia(this, document.getElementById("IscrizioneSez"));'/>&nbsp;&nbsp;&nbsp;Iscrizione&nbsp;&nbsp;
     </div>
     
     <div id="IscrizioneSez" style="display:none">
     	<table class="main">
     		<tr>
           		<td class="etichetta">Data Inizio
               	</td>
               	<td class="campo">
                	<af:textBox classNameBase="input" type="text" name="datInizioIscr" readonly="true" value="<%=datInizioIscr %>" size="15" maxlength="100"/>
              	</td>
              	<td class="etichetta" nowrap>Data Fine
               	</td>
               	<td class="campo">
                	<af:textBox classNameBase="input" type="text" name="datFineIscr" readonly="true" value="<%=datFineIscr %>" size="15" maxlength="100"/>
              	</td>
           </tr>
           <tr>
           		<td class="etichetta">Tipo Iscrizione
               	</td>
               	<td class="campo">
                	<af:textBox classNameBase="input" type="text" name="tipoIscr" readonly="true" value="<%=tipoIscr %>" size="50" maxlength="100"/>
              	</td>
              	<td class="etichetta" nowrap>Codice domanda
               	</td>
               	<td class="campo">
                	<af:textBox classNameBase="input" type="text" name="codAccordo" readonly="true" value="<%=codAccordo %>" size="30" maxlength="100"/>
              	</td>
           </tr>
            <tr>
           		<td class="etichetta" nowrap>Stato
               	</td>
               	<td class="campo">
                	<af:textBox classNameBase="input" type="text" name="statoIscr" readonly="true" value="<%=statoIscr %>" size="50" maxlength="100"/>
              	</td>
            </tr>
        </table>
     </div>
     
    <table><tr><td colspan="4">&nbsp;</td></tr></table>   
     
     <div class='sezione2' id='azioneConcordata'>
	  	<img id='tendinaPercorso' alt="Chiudi" src="../../img/chiuso.gif" onclick='cambia(this, document.getElementById("azioneSez"));'/>&nbsp;&nbsp;&nbsp;Azione Collegata&nbsp;&nbsp;
     </div>
    
     <div id="azioneSez" style="display:none">
     	<table class="main">
     		<tr>
           		<td class="etichetta">Data colloquio
               	</td>
               	<td class="campo">
                	<af:textBox classNameBase="input" type="text" name="datColloquio" readonly="true" value="<%=datColloquio %>" size="15" maxlength="100"/>
              	</td>
              	<td class="etichetta" nowrap>Data stimata
               	</td>
               	<td class="campo">
                	<af:textBox classNameBase="input" type="text" name="datStimata" readonly="true" value="<%=datStimata %>" size="15" maxlength="100"/>
              	</td>
           </tr>
           <tr>
           		<td class="etichetta">Esito
               	</td>
               	<td class="campo">
                	<af:textBox classNameBase="input" type="text" name="esito" readonly="true" value="<%=esito %>" size="30" maxlength="100"/>
              	</td>
              	<td class="etichetta" nowrap>Data effettiva
               	</td>
               	<td class="campo">
                	<af:textBox classNameBase="input" type="text" name="datEffettiva" readonly="true" value="<%=datEffettiva %>" size="15" maxlength="100"/>
              	</td>
           </tr>
          
     	</table>
     </div>
      <table><tr>
           		<td colspan="4">
           			<input type="button" class="pulsanti" name="Salva" value="Aggiorna" onClick="UpdateNoteCorso(document.Frm2.note)">
           			<input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="ChiudiDivLayer('divLayerDettCorsoOrienter')">
               	</td>           
           </tr></table>
     <p align="center">
	<%operatori_info.showHTML(out);%>
</p>
    
     <%out.print(divStreamBottom);%>
     
    </div>
   
    <%} %>
 
    <%if(!apriDivCorsi.equals("none") && (numPresaIncarico == null || !numPresaIncarico.equals(new BigDecimal("0")))){ %>
     <div id="divLayerDett" name="divLayerDett" class="t_layerDett"
         style="position:absolute; width:90%; left:30; top:150px; z-index:6; display:<%=apriDivCorsi%>;">

    <%out.print(divStreamTopCatalogo);%>
     
     <table width="100%" class="main">
      <tr>
      	<td>
      		<af:form name="Frm1" action="AdapterHTTP" method="POST">
      			<input type="hidden" name="PAGE" value="CigLavCorsiPage">
      				<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
    				<input type="hidden" name="PRGALTRAISCR" value="<%=prgAltraIscr%>">
    				<input type="hidden" name="PRGCOLLOQUIO" value="<%=prgColloquio %>">
    				<input type="hidden" name="PRGPERCORSO" value="<%=prgPercorso %>">
    				<input type="hidden" name="inserisciCorsoCatalogo" value="1">
    				<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>">
    				<input type="hidden" name="numidproposta" value="<%=numidproposta%>" />
          			<input type="hidden" name="numrecid" value="<%=numrecid%>" />
     			
    			
     <table width="100%">
     	<tr>
        	<td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
            <td height="16" class="azzurro_bianco">Catalogo</td>
            <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
        </tr>
      </table>
      <table class="main" width="100%"> 
	  <tr>
      	<td class="etichetta">Codice proposta</td>
        <td class="campo" colspan="3">
        <af:textBox classNameBase="input" onKeyUp="fieldChanged();PulisciRicercaCorsi(document.Frm1.strcodiceproposta,document.Frm1.strcodicepropostaHid,document.Frm1.strsede);" 
        			title="Codice proposta" name="strcodiceproposta" value="<%=strcodiceproposta%>" size="38" readonly="<%=String.valueOf(canModify)%>"/>&nbsp;
       				<a href="#" onClick="javascript:apriSezioneCatalogo(document.Frm1.strcodiceproposta.value);">
       					<% if(!canModify) { %><img src="../../img/binocolo.gif" alt="Cerca"><% } %> 
       				</a>
        	<af:textBox type="hidden" name="strcodicepropostaHid" value="<%=strcodiceproposta%>"/>
        </td>                
      </tr>  
      <tr>
        <td class="etichetta">Sede</td>
        <td class="campo" colspan="3">
          <af:textBox title="Sede" required="true" classNameBase="input" type="text" name="strsede" readonly="true" value="<%=strsede%>" size="60" maxlength="100"/>
       </td>
      </tr>
      <% if(canModify) { %> 
      <tr>
      	<td class="etichetta">Ambito provinciale</td>
      	<td class="campo" colspan="3">
      		<af:textBox classNameBase="input" type="text" name="strambitoprovinciale" readonly="true" value="<%=strambitoprovinciale%>" size="50" maxlength="100"/>
      	</td>
      </tr>
      <tr>
      	<td class="etichetta">Referente</td>
      	<td class="campo" colspan="3">
      		<af:textBox classNameBase="input" type="text" name="strnominativo" readonly="true" value="<%=strnominativo%>" size="50" maxlength="100"/>
      	</td>
      </tr>
      <tr>
      	<td class="etichetta">Email</td>
      	<td class="campo" colspan="3">
      		<af:textBox classNameBase="input" type="text" name="stremail" readonly="true" value="<%=stremail%>" size="50" maxlength="100"/>
      	</td>
      </tr>
      <tr>
      	<td class="etichetta">Area</td>
      	<td class="campo" colspan="3">
      		<af:textBox classNameBase="input" type="text" name="strdescrizionearea" readonly="true" value="<%=strdescrizionearea%>" size="50" maxlength="100"/>
      	</td>
      </tr>
      <tr>
      	<td class="etichetta">Qualifica</td>
      	<td class="campo" colspan="3">
      		<af:textBox classNameBase="input" type="text" name="strtitoloqualifica" readonly="true" value="<%=strtitoloqualifica%>" size="50" maxlength="100"/>
      	</td>
      </tr>
      <tr>
      	<td class="etichetta">Ente</td>
      	<td class="campo" colspan="3">
      		<af:textBox classNameBase="input" type="text" name="strragionesocialeente" readonly="true" value="<%=strragionesocialeente%>" size="70" maxlength="100"/>
      	</td>
      </tr>
      <% } %>
      <tr>
      	<td class="etichetta">Corso ITA per stranieri</td>
    	<td class="campo" colspan="3">
        	<af:comboBox onChange="fieldChanged()" classNameBase="input" name="flgItalianoStranieri" disabled="<%=String.valueOf(canModify)%>" selectedValue="<%=flgItalianoStranieri%>" addBlank="true"> 
            	<option value="S" <% if (flgItalianoStranieri.equals("S")) {%>selected="true" <%}%>>Sì</option>
            	<option value="N" <% if (flgItalianoStranieri.equals("N")) {%>selected="true" <%}%>>No</option>
            </af:comboBox>
    	</td>
  	  </tr>	
      <tr>
		<td class="etichetta">Note</td>
    	<td class="campo" colspan="3">
    		<af:textArea onKeyUp="fieldChanged()" classNameBase="input" name="strNoteCatalogo" value="<%=strNoteCatalogo%>" title="Note"
                	 	cols="60" rows="4" maxlength="2000" />
  		</td>
  	  </tr>  	  	 
  	  <tr><td colspan="4"></td></tr>
	  	  <tr>
	      		<td class="etichetta" nowrap>Data inizio prevista
	          	</td>
	          	<td class="campo">
	           	<af:textBox classNameBase="input" type="text" name="datInizioPrev" readonly="true" value="<%=datInizioPrev %>" size="15" maxlength="100"/>
	         	</td>
	         	<td class="etichetta" nowrap>Data fine prevista
	          	</td>
	          	<td class="campo">
	           	<af:textBox classNameBase="input" type="text" name="datFinePrev" readonly="true" value="<%=datFinePrev %>" size="15" maxlength="100"/>
	         	</td>
	      </tr>
	      <tr>
	      		<td class="etichetta" nowrap>Data inizio effettiva
	          	</td>
	          	<td class="campo">
	           	<af:textBox classNameBase="input" type="text" name="datInizio" readonly="true" value="<%=datInizio %>" size="15" maxlength="100"/>
	         	</td>
	         	<td class="etichetta" nowrap>Data fine effettiva
	          	</td>
	          	<td class="campo">
	           	<af:textBox classNameBase="input" type="text" name="datFine" readonly="true" value="<%=datFine %>" size="15" maxlength="100"/>
	         	</td>
	      </tr>
	      <tr>
	      		<td class="etichetta" nowrap>Data presa in carico ente
	          	</td>
	          	<td class="campo">
	           	<af:textBox classNameBase="input" type="text" name="datPresaInCarico" readonly="true" value="<%=datPresaInCarico %>" size="15" maxlength="100"/>
	         	</td>
	      </tr>
	      <tr>
	      		<td class="etichetta" nowrap>Data ritiro corso
	          	</td>
	          	<td class="campo">
	           	<af:textBox classNameBase="input" type="text" name="datRitiro" readonly="true" value="<%=datRitiro %>" size="15" maxlength="100"/>
	         	</td>
	         	<td class="etichetta">Motivo ritiro
	          	</td>
	          	<td class="campo">
	           	<af:textBox classNameBase="input" type="text" name="motivoRitiro" readonly="true" value="<%=motivoRitiro %>" size="40" maxlength="100"/>
	         	</td>
	      </tr>    
  	<tr><td colspan="4"></td></tr>
    </table>  
    <% if(canModify) { %>
    
    <div class='sezione2' id='IscrizioneCat'>
	  	<img id='tendinaIscrizione' alt="Chiudi" src="../../img/chiuso.gif" onclick='cambia(this, document.getElementById("IscrizioneSezCat"));'/>&nbsp;&nbsp;&nbsp;Iscrizione&nbsp;&nbsp;
     </div>
     
     <div id="IscrizioneSezCat" style="display:none">
     	<table class="main">
     		<tr>
           		<td class="etichetta">Data Inizio
               	</td>
               	<td class="campo">
                	<af:textBox classNameBase="input" type="text" name="datInizioIscrCat" readonly="true" value="<%=datInizioIscrCat %>" size="15" maxlength="100"/>
              	</td>
              	<td class="etichetta" nowrap>Data Fine
               	</td>
               	<td class="campo">
                	<af:textBox classNameBase="input" type="text" name="datFineIscrCat" readonly="true" value="<%=datFineIscrCat %>" size="15" maxlength="100"/>
              	</td>
           </tr>
           <tr>
           		<td class="etichetta">Tipo Iscrizione
               	</td>
               	<td class="campo">
                	<af:textBox classNameBase="input" type="text" name="tipoIscrCat" readonly="true" value="<%=tipoIscrCat %>" size="50" maxlength="100"/>
              	</td>
              	<td class="etichetta" nowrap>Codice domanda
               	</td>
               	<td class="campo">
                	<af:textBox classNameBase="input" type="text" name="codAccordoCat" readonly="true" value="<%=codAccordoCat %>" size="30" maxlength="100"/>
              	</td>
           </tr>
            <tr>
           		<td class="etichetta" nowrap>Stato
               	</td>
               	<td class="campo">
                	<af:textBox classNameBase="input" type="text" name="statoIscrCat" readonly="true" value="<%=statoIscrCat %>" size="50" maxlength="100"/>
              	</td>
            </tr>
        </table>
     </div>
     
    <table><tr><td colspan="4">&nbsp;</td></tr></table>   
    
    <div class='sezione2' id='azioConcCat'>
	  	<img id='tendPercCat' alt="Chiudi" src="../../img/chiuso.gif" onclick='cambia(this, document.getElementById("aziSezCat"));'/>&nbsp;&nbsp;&nbsp;Azione Collegata&nbsp;&nbsp;
     </div>
     <div id="aziSezCat" style="display:none">
     	<table class="main">
     		<tr>
           		<td class="etichetta">Data colloquio
               	</td>
               	<td class="campo">
                	<af:textBox classNameBase="input" type="text" name="datcolloquioCat" readonly="true" value="<%=datcolloquioCat %>" size="15" maxlength="100"/>
              	</td>
              	<td class="etichetta" nowrap>Data stimata
               	</td>
               	<td class="campo">
                	<af:textBox classNameBase="input" type="text" name="datstimataCat" readonly="true" value="<%=datstimataCat %>" size="15" maxlength="100"/>
              	</td>
           </tr>
           <tr>
           		<td class="etichetta">Esito
               	</td>
               	<td class="campo">
                	<af:textBox classNameBase="input" type="text" name="esitoCat" readonly="true" value="<%=esitoCat %>" size="30" maxlength="100"/>
              	</td>
              	<td class="etichetta" nowrap>Data effettiva
               	</td>
               	<td class="campo">
                	<af:textBox classNameBase="input" type="text" name="dateffettivaCat" readonly="true" value="<%=dateffettivaCat %>" size="15" maxlength="100"/>
              	</td>
           </tr>
     	</table>
     </div>
     <% } %>
 
	&nbsp;&nbsp;&nbsp;&nbsp;
	<table class="main">
    	<tr>
          	<td align="center">
          	<% if(!canModify) { %> 
            	<input type="submit" class="pulsanti" name="inserisci" value="Inserisci">
            <% } else { %> 	
            <% 		if(prgcorsoci != null) { %>
             		<input type="button" class="pulsanti" name="Salva" value="Aggiorna" onClick="UpdateNoteCorso(document.Frm1.strNoteCatalogo)">
            <% 		} 
               }%>
            	<input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="ChiudiDivLayer('divLayerDett')">
            </td>
          </tr>
  	 </table>
  	 </af:form>
  	</td>
  	</tr>
  </table>
     <p align="center">
     <%if(row_cdn!=null || row_cdnCat!=null){
	operatori_info.showHTML(out);
	} %>
</p>
   <%out.print(divStreamBottomCatalogo);%>
</div>
    
   
    <%} %>
  

 
<script language="Javascript">
  <% 
	attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore);
  %>
</script>
</html>
	
</body>