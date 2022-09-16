<%@ page contentType="text/html;charset=utf-8"%>
<%-- /////////////////////////////////// --%>
<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  it.eng.sil.module.movimenti.constant.Properties,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.security.ProfileDataFilter,                  
                  it.eng.sil.util.*,
                  java.util.*,
                  it.eng.afExt.utils.*,
                  com.engiweb.framework.security.*,
                  java.math.*" %>
<%-- /////////////////////////////////// --%>
<%@ taglib uri="aftags" prefix="af" %>
<%-- /////////////////////////////////// --%>        
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%-- /////////////////////////////////// --%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%-- /////////////////////////////////// --%>
<%
boolean onlyInsert = serviceRequest.getAttribute("ONLY_INSERT")==null?false:true;
String COD_LST_TAB = "";
if(onlyInsert){ 
  COD_LST_TAB = "OR_PER";//(String)serviceRequest.getAttribute("COD_LST_TAB");
}

String cdnFunzione= (String)serviceRequest.getAttribute("CDNFUNZIONE");
String _page = (String) serviceRequest.getAttribute("PAGE"); 
String nonFiltrare = Utils.notNull(serviceRequest.getAttribute("NONFILTRARE"));

///
//
// questi parametri serviranno per tornare indietro alla pagina della lista e permettere di eseguire la stessa query 
// da decidere se eseguire, di ritorno dalla pagina di dettaglio, una query generale o la stessa eseguita dalla pagina di ricerca
// Se mi trovo nello stato di inserisci nuovo l' unico valore certo che avro' sara' il codice cpi
// tutto il resto sara' valorizzato o meno
String codiceFiscale = (String)serviceRequest.getAttribute("CF");
String cognome = (String)serviceRequest.getAttribute("COGNOME");
String nome = (String)serviceRequest.getAttribute("NOME");
String dataInizioColloquio = (String)serviceRequest.getAttribute("DATAINIZIO");
String dataFineColloquio = (String)serviceRequest.getAttribute("DATAFINE");


String ricercaGenerale = (String) serviceRequest.getAttribute("ricerca_generale");
ricercaGenerale = ricercaGenerale == null ? "" : ricercaGenerale;
boolean mostraCondizionalita = false;
String numAzioniEsitoNegativo = null;
String flgCondizionalita = null;
String numEventiCondizionalita = null;

if(StringUtils.isEmptyNoBlank(ricercaGenerale)){
	mostraCondizionalita = true;
}

/* inserisci un nuovo colloquio con il lavoratore */
boolean inserisciNuovo = !(serviceRequest.getAttribute("inserisciNuovo")==null || 
                            ((String)serviceRequest.getAttribute("inserisciNuovo")).length()==0);
/////////////////////////////////////
if (serviceResponse.containsAttribute("M_Insert_Colloquio.CHECK_KO")){
	inserisciNuovo = true;	
}
String codCPI = null;
String descrcpi = "";
String datColloquio = null;
String datFineProgramma = "";
String aspirazioni = null;
String potenzialita = null;
String vincoliLavoratore = null;
String strNote= null;
String obiettivo = null;
String azioniConcordate = null;
String vincoliMercato = null;
String analisiLavoro = null;
String operatoreSpi = null;
String numklocolloquio=null;
String codServizio = null; 
String codMonoProgramma = null;
String prgColloquio = null;
String cdnLavoratore = null;
//
BigDecimal cdnUtIns = null;
String dtmIns = null;
BigDecimal cdnUtMod = null;
String dtmMod = null;
Testata operatoreInfo = null;
String prgAltraIscr = null;
String datChiusuraPer = null;
String codMotivoChiusuraPer = null;
String codEsitoColl = null;
String codLineaAz = null;
String PRGACCORDO = "";
String urlPdfAccSin = "";
String tipoDichiarazione = null;
String datAdesione = null;
boolean canViewAdesione = false;

String labelServizio = "Programma";
cdnLavoratore= (String)serviceRequest.getAttribute("CDNLAVORATORE");
if (!inserisciNuovo && 
		(serviceResponse.containsAttribute("M_Insert_Colloquio.numCigColl") ||
		serviceResponse.containsAttribute("M_Save_Colloquio.numCigColl"))){ 
    aspirazioni = Utils.notNull(serviceRequest.getAttribute("STRDESASPIR"));
    potenzialita = Utils.notNull(serviceRequest.getAttribute("STRRISPOTENZ"));
    vincoliLavoratore = Utils.notNull(serviceRequest.getAttribute("STRVINCLAV"));
    strNote = Utils.notNull(serviceRequest.getAttribute("STRNOTE"));
    obiettivo = Utils.notNull(serviceRequest.getAttribute("STROBIETTIVO"));
    azioniConcordate = Utils.notNull(serviceRequest.getAttribute("STRAZIONICON"));
    vincoliMercato = Utils.notNull(serviceRequest.getAttribute("STRVINCEST"));
    analisiLavoro = Utils.notNull(serviceRequest.getAttribute("STRANALISILAVORO"));
    operatoreSpi = Utils.notNull(serviceRequest.getAttribute("PRGSPI"));
    codServizio = Utils.notNull(serviceRequest.getAttribute("CODSERVIZIO"));
    prgAltraIscr = Utils.notNull(serviceRequest.getAttribute("PRGALTRAISCR"));
    datChiusuraPer = Utils.notNull(serviceRequest.getAttribute("DATCHIUSURAPER"));
	codMotivoChiusuraPer = Utils.notNull(serviceRequest.getAttribute("CODMOTIVOCHIUSURAPER"));
	codEsitoColl = Utils.notNull(serviceRequest.getAttribute("CODESITOCOLL")); 
	codLineaAz = Utils.notNull(serviceRequest.getAttribute("CODLINEAAZ"));
	PRGACCORDO = Utils.notNull(serviceRequest.getAttribute("PRGACCORDO"));
	if (serviceResponse.containsAttribute("M_Insert_Colloquio.numCigColl")) {
		inserisciNuovo = true;
	}	
	if (serviceResponse.containsAttribute("M_Save_Colloquio.numCigColl")) {
		datColloquio = Utils.notNull(serviceRequest.getAttribute("DATCOLLOQUIO"));
	}
}
if (!inserisciNuovo) { // dettaglio colloquio lavoratore
	SourceBean row = null;
    Vector rows= serviceResponse.getAttributeAsVector("M_LOAD_COLLOQUIO.ROWS.ROW");
    // siamo in dettaglio per cui avro' al massimo una riga
    if (rows.size()==1) {
        row = (SourceBean)rows.get(0);
        if (!serviceResponse.containsAttribute("M_Save_Colloquio.numCigColl")) {
        	descrcpi = Utils.notNull(row.getAttribute("descrcpi"));
        	datColloquio = Utils.notNull(row.getAttribute("DATCOLLOQUIO"));
        	aspirazioni = Utils.notNull(row.getAttribute("STRDESASPIR"));
        	potenzialita = Utils.notNull(row.getAttribute("STRRISPOTENZ"));
        	vincoliLavoratore = Utils.notNull(row.getAttribute("STRVINCLAV"));
        	strNote = Utils.notNull(row.getAttribute("STRNOTE"));
        	obiettivo = Utils.notNull(row.getAttribute("STROBIETTIVO"));
        	azioniConcordate = Utils.notNull(row.getAttribute("STRAZIONICON"));
        	vincoliMercato = Utils.notNull(row.getAttribute("STRVINCEST"));
        	analisiLavoro = Utils.notNull(row.getAttribute("STRANALISILAVORO"));
        	operatoreSpi = Utils.notNull(row.getAttribute("PRGSPI"));
        	codServizio = Utils.notNull(row.getAttribute("CODSERVIZIO"));
        	codMonoProgramma = Utils.notNull(row.getAttribute("codmonoprogramma"));
            prgAltraIscr = Utils.notNull(row.getAttribute("PRGALTRAISCR"));
            PRGACCORDO = Utils.notNull(row.getAttribute("PRGACCORDO"));
            datChiusuraPer = Utils.notNull(row.getAttribute("DATCHIUSURAPER"));
    		codMotivoChiusuraPer = Utils.notNull(row.getAttribute("CODMOTIVOCHIUSURAPER"));
    		codEsitoColl = Utils.notNull(row.getAttribute("CODESITOCOLL")); 
    		codLineaAz = Utils.notNull(row.getAttribute("CODLINEAAZ"));
        }
        
        tipoDichiarazione = Utils.notNull(row.getAttribute("TIPODICHIARAZIONE"));
        datAdesione = Utils.notNull(row.getAttribute("datAdesione"));
        if (!"".equals(datAdesione)) canViewAdesione = true;

        prgColloquio = Utils.notNull(row.getAttribute("PRGCOLLOQUIO"));
        codCPI = Utils.notNull(row.getAttribute("CODCPI"));
        cdnLavoratore = Utils.notNull(row.getAttribute("CDNLAVORATORE"));
        datFineProgramma = Utils.notNull(row.getAttribute("DATAFINEPROGRAMMA"));
        // aggiornamento del lock ottimistico in aggiornamento    
        numklocolloquio = String.valueOf(((BigDecimal)row.getAttribute("NUMKLOCOLLOQUIO")).intValue()+1);
        cdnUtIns = (BigDecimal) row.getAttribute("CDNUTINS");
        dtmIns = (String) row.getAttribute("DTMINS");
        cdnUtMod = (BigDecimal) row.getAttribute("CDNUTMOD");
        dtmMod = (String) row.getAttribute("DTMMOD");
        operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);   
        flgCondizionalita = Utils.notNull(row.getAttribute("flgCondizionalita"));
		numEventiCondizionalita = Utils.notNull(row.getAttribute("numEventiCondizionalita"));
		numAzioniEsitoNegativo = Utils.notNull(row.getAttribute("numAttivitaNegativi"));
    }
} else {
    // valorizzo gli attributi required
    codCPI = (String)serviceRequest.getAttribute("CODCPI");
    cdnLavoratore= (String)serviceRequest.getAttribute("CDNLAVORATORE");
    datColloquio = it.eng.afExt.utils.DateUtils.getNow();   
}
if (codCPI==null || codCPI.equals("")) {
    codCPI = user.getCodRif();
}

//Selezione del prgSpi in fase di inserimento, in modo da prevalorizzare la combo
if (inserisciNuovo && !serviceResponse.containsAttribute("M_Insert_Colloquio.numCigColl")) {
  SourceBean operatore = (SourceBean)serviceResponse.getAttribute("M_Select_PrgSPI_Da_CDNUT");
  if ((operatore != null) && (operatore.getAttribute("ROWS.ROW.prgSpi")!=null)){
    if(!operatore.getAttribute("ROWS.ROW.prgSpi").equals(""))
      operatoreSpi = operatore.getAttribute("ROWS.ROW.prgSpi").toString();
  }
}

String iscrCigPresenti = "false";
String canCIG = (String) serviceResponse.getAttribute("M_Configurazione_CIG.ROWS.ROW.canCIG");
if (canCIG.equals("true")) {
	iscrCigPresenti = (String) serviceResponse.getAttribute("M_Lav_Iscrizioni_Cig.iscrCigPresenti");
}
	
boolean disableComboCig = false;
Vector rowsPercorsi = serviceResponse.getAttributeAsVector("M_ListPercorsi.ROWS.ROW");
if (!inserisciNuovo && iscrCigPresenti.equals("true")) {
	if (rowsPercorsi.size() > 0) {
		disableComboCig = true;
	}
	if (!PRGACCORDO.equals("")) {
		if (serviceResponse.containsAttribute("M_PdfAccordoSindacale.ROWS.ROW")) {
		    String token = StringUtils.getAttributeStrNotNull(serviceResponse,"M_PdfAccordoSindacale.ROWS.ROW.token");
		    String strUrl = StringUtils.getAttributeStrNotNull(serviceResponse,"M_PdfAccordoSindacale.ROWS.ROW.strvalore");
		    urlPdfAccSin = strUrl + "?t=" + token;	
		}
	}
}

ProfileDataFilter filter = new ProfileDataFilter(user, _page);
boolean canView = false;

if ( !inserisciNuovo ) {
  filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
  canView=filter.canViewLavoratore();
} else {
  canView = true; // sono in inserimento, la visibilità dei dati non ha senso
}

if ( !canView ){
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	return;
}else{

PageAttribs attributi = new PageAttribs(user, _page);
boolean canInsert = attributi.containsButton("inserisci");
boolean canModify = attributi.containsButton("aggiorna");

if ( !canInsert && !canModify) {
    
} else {
	boolean canEdit = false;
    if (!inserisciNuovo) {
    	canEdit = filter.canEditLavoratore();
    } else { 
      	canEdit = true;   // sono in inserimento, la visibilità dei dati non ha senso
    }
    
    if (!canEdit) {
    	canInsert = false;
      	canModify = false;
    }
}

//IL PROGRAMMA IN MODIFICA NON PUò ESSERE MAI MODIFICATO
String fieldReadOnlyProg ="false";
if (!inserisciNuovo) {
	fieldReadOnlyProg ="true";
}

if (Utils.notNull(codMonoProgramma).equalsIgnoreCase(it.eng.sil.util.amministrazione.impatti.PattoBean.DB_MISURE_L14)) {
	canModify = false;
}
if (Utils.notNull(codMonoProgramma).equalsIgnoreCase(it.eng.sil.util.amministrazione.impatti.PattoBean.DB_MISURE_DOTE) ||
	Utils.notNull(codMonoProgramma).equalsIgnoreCase(it.eng.sil.util.amministrazione.impatti.PattoBean.DB_MISURE_DOTE_IA)) {
	canModify = false;	
}

boolean readOnlyStr = !canModify;
String fieldReadOnly = canModify?"false":"true";

String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>

<%
// POPUP EVIDENZE
String strApriEv = StringUtils.getAttributeStrNotNull(serviceRequest, "APRI_EV");
int _fun = 1;
if((cdnFunzione!=null) && !cdnFunzione.equals("")) { _fun = Integer.parseInt(cdnFunzione); }
EvidenzePopUp jsEvid = null;
boolean bevid = attributi.containsButton("EVIDENZE");
if(strApriEv.equals("1") && bevid) {
	jsEvid = new EvidenzePopUp(cdnLavoratore, _fun, user.getCdnGruppo(), user.getCdnProfilo());
	jsEvid.show(out);
}	
%> 

<html>
<head>
<title>Colloqui dettaglio</title>

<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
<link rel="stylesheet" type="text/css" href="../../css/jqueryui/jquery.selectBoxIt.css">
<link rel="stylesheet" type="text/css" href="../../css/jqueryui/jqueryui-sil.css">
<script src="../../js/jqueryui/jquery-1.12.4.min.js"></script>
<script src="../../js/jqueryui/jquery-ui.min.js"></script>

 <script type="text/javascript">
    $(function() {
    	$("[name='CODSERVIZIO']").selectBoxIt({
            theme: "default",
            autoWidth: false
        }); 
	});	    
    </script>   


<script src="../../js/jqueryui/jquery.selectBoxIt.min.js"></script>

<af:linkScript path="../../js/"/>

<%@ include file="../movimenti/MovimentiSezioniATendina.inc" %>

   <script language="JavaScript">
	   	// Rilevazione Modifiche da parte dell'utente
	   	var flagChanged = false;
	   	var serviziToCheck = "<%=MessageCodes.General.SERVIZIO_L14%>-<%=MessageCodes.General.SERVIZIO_DOTE%>-<%=MessageCodes.General.SERVIZIO_DOTE_IA%>";
	   
	   	function fieldChanged() {
	    	<%if (!readOnlyStr){ %> 
	       		flagChanged = true;
	    	<%}%> 
	   	}
	   	
	   	<%if (canCIG.equals("true")) {%>
	   		
	   		function azzeraAltroCIG() {   
			  	document.Frm1.DATCHIUSURAPER.value="";
				document.Frm1.CODMOTIVOCHIUSURAPER.value="";
				document.Frm1.CODESITOCOLL.value="";
				document.Frm1.CODLINEAAZ.value="";   				
				altroCigSez = document.getElementById("altroCigSez");
				altroCigSez.style.display = "none";
			}
	   		
	   		var altroCigSez;
			function abilitaAltroCig() {
		   		if (document.Frm1.PRGALTRAISCR.value != "") {
		    		altroCigSez = document.getElementById("altroCigSez");
		    		altroCigSez.style.display = "";
		    	} else {
		    		<%if (!inserisciNuovo && prgAltraIscr != null && !prgAltraIscr.equals("")) {%>
		    			if (confirm("Le informazioni relative alla Data Chiusura, Motivo Chiusura, Esito complessivo percorso e \nLinea di Azione saranno azzerrate. Continuare?")) {
		    				azzeraAltroCIG();
		    			} else {
		    				document.Frm1.PRGALTRAISCR.value = '<%=prgAltraIscr%>';
		    			}
		    		<%} else {%>
		    			azzeraAltroCIG();	   
		    		<%}%>     
		    	}
		   	}
		   	
			 function VisualizzaPdfAccordoSindacale() {
				<%if (!urlPdfAccSin.equals("")) {%>
					var f = "<%=urlPdfAccSin%>";
			     	var t = "_blank";
			        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=750,height=650,top=30,left=180";
			        window.open(f, t, feat);
		        <%}%>		   	
			 }	   	
		   	
		<%}%>

		var urlpage="AdapterHTTP?";

		function getURLPageBase() {    
		    urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&";
		    urlpage+="CDNLAVORATORE=<%=cdnLavoratore%>&";
		    return urlpage;
		}
			
		function checkServizio() {
			if (serviziToCheck.indexOf(document.Frm1.CODSERVIZIO.value) > -1) {
				alert("Servizio non consentito");
		      	document.Frm1.CODSERVIZIO.options.selectedIndex=0;
			}
		}

		function indietro() {
		  // Se la pagina è già in submit, ignoro questo nuovo invio!
		  if (isInSubmit()) return;
		
		 if(flagChanged)
		 { if(!confirm("I dati sono cambiati.\nProcedere lo stesso?"))
		   { return false;
		   }
		 }
		    urlpage = getURLPageBase();
		    urlpage+="PAGE=LISTACOLLOQUIPAGE&";
		    <%if(onlyInsert){%>
		      urlpage+="COD_LST_TAB=<%=COD_LST_TAB%>&";
		      urlpage+="ONLY_INSERT=&";
		    <%}%>
		    urlpage+="NONFILTRARE=<%=nonFiltrare%>&"
		    setWindowLocation(urlpage);
		}
	
		function fai_submit(operazione) {
			if (serviziToCheck.indexOf(document.Frm1.CODSERVIZIO.value) > -1) {
				alert("Servizio non consentito");
		      	return;
			}

			if ( controllaFunzTL() ) { // e' il controllo che di default fa il tag af:form bypassato dalla funzione js
				if (operazione == "inserisci") {
					document.Frm1.MODULE.value="M_INSERT_COLLOQUIO";
				} else if (operazione == "aggiorna") {
					<%if(!disableComboCig && canCIG.equals("true")){%>
					if (document.Frm1.PRGALTRAISCR.value != "") {
						alert("Il mancato inserimento dell\'azione \"Presa in carico CIG\" nei Percorsi concordati \nnon consentirà la corretta rilevazione delle politiche attive su questo lavoratore. \nSi invita l\'operatore ad inserire la suddetta azione.");
					}
					<%}%>
					document.Frm1.MODULE.value="M_SAVE_COLLOQUIO";				
				}
		        document.Frm1.action+="?pageChiamante=<%=StringUtils.getAttributeStrNotNull(serviceRequest,"PAGECHIAMANTE")%>";//Per il ritorno dall'associa
		        doFormSubmit(document.Frm1);
			}
			else return;
		}
	
		function inserisciNuovo(){
		    // Se la pagina è già in submit, ignoro questo nuovo invio!
		    if (isInSubmit()) return;
		
		    urlpage = getURLPageBase();
		    urlpage+="PAGE=COLLOQUIOPAGE&"; 
		    urlpage+="inserisciNuovo=1&";
		    setWindowLocation(urlpage);
		}

		function apriPercorsi() {
			// Se la pagina è già in submit, ignoro questo nuovo invio!
		  	if (isInSubmit()) return;
		
		 	if(flagChanged) { 
		 		if(!confirm("I dati sono cambiati.\nProcedere lo stesso?")) { 
		 			return false;
		   		}
		 	}
			if (document.Frm1.PRGCOLLOQUIO.value=="") {
			    alert("Inserire un colloquio prima di associare un percorso");
			    return ;
			}
			var urlPage = urlpage;
			urlPage+="cdnLavoratore=<%=cdnLavoratore %>&";
			urlPage+="CDNFUNZIONE=<%=cdnFunzione %>&";
			urlPage+="PRGCOLLOQUIO=<%=prgColloquio%>&";
			urlPage+="PRGSPI=<%=operatoreSpi%>&";
			urlPage+="PAGE=PERCORSIPAGE&";
			urlPage+="CODSERVIZIO=<%=codServizio %>";
			urlPage+="&CODMONOPROGRAMMA=<%=Utils.notNull(codMonoProgramma)%>";
			urlPage+="&statoSezioni=<%=StringUtils.getAttributeStrNotNull(serviceRequest,"statoSezioni")%>";
			urlPage+="&pageChiamante=<%=StringUtils.getAttributeStrNotNull(serviceRequest,"PAGECHIAMANTE")%>";
			<%if(onlyInsert){%>
			  urlPage+="&ONLY_INSERT=1";
			<%}%>
			<%if (serviceRequest.getAttribute("DA_PATTO_COD_LST_TAB")==null)
					out.print("urlPage+='&"+QueryString.toURLParameter(serviceRequest, "COD_LST_TAB","DA_PATTO_COD_LST_TAB")+"';");
			  else 
					out.print("urlPage+='&"+QueryString.toURLParameter(serviceRequest, "DA_PATTO_COD_LST_TAB")+"';");
			%>
			
			//urlPage+="&COD_LST_TAB=<%=COD_LST_TAB%>";
			urlPage+="&NONFILTRARE=<%=nonFiltrare%>";
			setWindowLocation(urlPage);
		}
		
		<%if (onlyInsert) {
		//questa funzione js potra' essere chiamata soltanto quando la pagina viene chiamata dal template di associazione al patto
		  %>
		    function indietroPopUpAssociazione() {
		        var urlpage="AdapterHTTP?";
		        urlpage+="cdnLavoratore=<%=cdnLavoratore %>&";
		        urlpage+="CDNFUNZIONE=<%=cdnFunzione %>&";
		        <%
		        List codLstTab = null;
		        if (serviceRequest.getAttribute("DA_PATTO_COD_LST_TAB")==null)
		        	codLstTab = serviceRequest.getAttributeAsVector("COD_LST_TAB");
		        else 
		        	codLstTab = serviceRequest.getAttributeAsVector("DA_PATTO_COD_LST_TAB");
		        for (int i=0;i<codLstTab.size();i++) {
		        %>
		            urlpage+="COD_LST_TAB=<%=(String)codLstTab.get(i)%>&";
		        <%}%>
		        urlpage+="statoSezioni=<%=StringUtils.getAttributeStrNotNull(serviceRequest,"statoSezioni")%>&";
		        urlpage+="PAGE=AssociazioneAlPattoTemplatePage&";
		        urlpage+="pageChiamante=<%=StringUtils.getAttributeStrNotNull(serviceRequest,"PAGECHIAMANTE")%>&";
		        urlpage+="&NONFILTRARE=<%=nonFiltrare%>";
		        //window.open(urlpage,"_self");
		        setWindowLocation(urlpage);
		    }
		<%}%>
</script>

<script language="Javascript">
    if(window.top.menu != undefined)
       window.top.menu.caricaMenuLav(<%=cdnFunzione%>,<%=cdnLavoratore%>);
</script>
<script language="Javascript">
<%	if (!inserisciNuovo)
       attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore);
%>
</script>
<body class="gestione" onload="rinfresca();<%if(strApriEv.equals("1") && bevid) { %> apriEvidenze() <%}%>">

<%  
    if(!onlyInsert){
        InfCorrentiLav _testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
        _testata.setPaginaLista("LISTACOLLOQUIPAGE");
        _testata.show(out);
        Linguette _linguetta = new Linguette(user, Integer.parseInt(cdnFunzione), "LISTACOLLOQUIPAGE", new BigDecimal(cdnLavoratore)); 
        _linguetta.show(out);
    }
%>

<font color="red">
  <af:showErrors/>
</font>
<font color="green">
  <af:showMessages prefix="M_INSERT_COLLOQUIO"/>
  <af:showMessages prefix="M_SAVE_COLLOQUIO"/>
  <af:showMessages prefix="M_DELETE_COLLOQUIO"/>
</font>

<p class="titolo">Programma</p>
<af:form name="Frm1" method="POST" action="AdapterHTTP">
     <%out.print(htmlStreamTop);%> 
    <table class="main">
        <%--  /////////// PER IL MOMENTO NON SI MOSTRA QUESTO CAMPO ////////////////////////////
        <tr><td class="etichetta">CPI</td>
            <td class="campo">
                <af:comboBox disabled="<%=fieldReadOnly%>"  name="codCPI"  moduleName="M_ELENCOCPI" selectedValue="<%=codCPI%>"
                      addBlank="false" required="true" title="Cpi di riferimento"  />
            </td>
        </tr>
        --%>
        

        
        <tr><td class="etichetta">CPI</td>
            <td class="campo"><%=descrcpi%></td>
        </tr>
        
        <tr>
        <%String titoloServ = "Tipo " + labelServizio; %>
            <td class="etichetta"><%=labelServizio %></td>
            <td class="campo" nowrap>
                <af:comboBox classNameBase="input" name="CODSERVIZIO"  moduleName="COMBO_SERVIZIO_SCAD_COLLOQUI" selectedValue="<%=codServizio%>"
                      addBlank="true" required="true" disabled="<%= fieldReadOnlyProg%>" title="<%= titoloServ %>" onChange="checkServizio();fieldChanged();"/>
            </td>
        </tr>
        <%if(mostraCondizionalita){ %>
			<tr>
				<td class="etichetta">Sottoposta a condizionalità</td>
				<td class="campo"><af:textBox classNameBase="input"
					readonly="true" type="text" name="flgCondizionalita"
					value="<%=flgCondizionalita%>"  
					size="12"  
					title="Sottoposta a condizionalità" /></td>
			</tr>
			<tr>
				<td class="etichetta">Num. Attività con esiti negativi</td>
				<td class="campo"><af:textBox classNameBase="input"
					readonly="true" type="text" name="numAzioniEsitoNegativo"
					value="<%=numAzioniEsitoNegativo%>"  
					size="12"  
					title="Num. Attività con esiti negativi" /></td>
			</tr>
			<tr>
				<td class="etichetta">Num. Eventi con condizionalità</td>
				<td class="campo"><af:textBox classNameBase="input"
					readonly="true" type="text" name="numEventiCondizionalita"
					value="<%=numEventiCondizionalita%>"  
					size="12"  
					title="Num. Eventi con condizionalità" /></td>
			</tr>
		<%} %>
        <tr>
            <td class="etichetta">Operatore SPI</td>
            <td class="campo" nowrap>
               	<af:comboBox 
                	classNameBase="input" 
                	onChange="fieldChanged();" 
                	name="PRGSPI" 
                	moduleName="COMBO_SPI_SCAD" 
                	required="true" 
                	selectedValue="<%=operatoreSpi%>" 
                	addBlank="true" 
                	disabled="<%= fieldReadOnly%>"  
                	title="Operatore Spi"
                />
            </td>
        </tr>
    
        <tr><td class="etichetta">Data programma</td>
            <td class="campo">
                <af:textBox classNameBase="input" readonly="<%=fieldReadOnly%>"  type="date"
                      name="datColloquio" value="<%=datColloquio%>" validateOnPost="true" required="true"
                      size="12" maxlength="10" onKeyUp="fieldChanged();" title="Data programma"/>
            </td>
        </tr>
        <tr><td class="etichetta">Note</td>
            <td class="campo">
                 <af:textArea cols="80" rows="5" maxlength="2000" readonly="<%=fieldReadOnly%>" classNameBase="textarea"  
                		name="STRNOTE" value="<%=strNote%>" validateOnPost="true" 
                      	required="false" title="Note" onKeyUp="fieldChanged();"/>
            </td>
        </tr>
        
        <tr><td class="etichetta">Data fine programma</td>
            <td class="campo">
                <af:textBox classNameBase="input" readonly="true" type="date"
                      name="datFineProgramma" value="<%=datFineProgramma%>"
                      size="12" maxlength="10" title="Data fine programma"/>
            </td>
        </tr>
        
        <%if (canViewAdesione) {%>
        <tr>
        	<td class="etichetta">Tipo Dichiarazione</td>
            <td class="campo">
     			<af:textBox name="tipoDichiarazione" value="<%=tipoDichiarazione%>" classNameBase="input" type="text" 
           			title="Tipo Dichiarazione" readonly="true" size="12" maxlength="10"/>
    		&nbsp;&nbsp;Data Adesione&nbsp;
                <af:textBox classNameBase="input" readonly="true" type="date" name="datAdesione" value="<%=datAdesione%>"
                      size="12" maxlength="10" title="Data Adesione"/>
            </td>
        </tr>      
        <%}%>
        
        <%if (canCIG.equals("true")) {%>
	        <tr>
	            <td class="etichetta">Iscrizione</td>
	            <td class="campo" nowrap>
	                <af:comboBox classNameBase="input" onChange="fieldChanged();abilitaAltroCig()" name="PRGALTRAISCR"  
	                		moduleName="M_Lav_Iscrizioni_Cig" addBlank="true" disabled='<%=(readOnlyStr || disableComboCig)? "true" : "false"%>' 
	                		title="Iscrizione CIG" selectedValue="<%=prgAltraIscr%>"/>
	            </td>
	        </tr>
	        <% if (iscrCigPresenti.equals("true")) {%>
	        <tr>
	        	<td colspan="2">
	          		<div id="altroCigSez" style="display:<%if (prgAltraIscr == null || prgAltraIscr.equals("")) {%>none<%}%>">
	            		<table class="main" width="100%" border="0">        
					        <tr>
					        	<td class="etichetta">Data Chiusura</td>
					            <td class="campo">
					                <af:textBox classNameBase="input" readonly="<%=fieldReadOnly%>"  type="date"
					                      name="DATCHIUSURAPER" value="<%=datChiusuraPer%>" validateOnPost="true" 
					                      size="12" maxlength="10" onKeyUp="fieldChanged();" title="Data Chiusura"/>
					            </td>
					        </tr>
					        <tr>
					            <td class="etichetta">Motivo Chiusura</td>
					            <td class="campo" nowrap>
					                <af:comboBox  name="CODMOTIVOCHIUSURAPER" classNameBase="input"  moduleName="M_Motivo_Chiusura" 
					                		selectedValue="<%=codMotivoChiusuraPer%>" addBlank="true" disabled="<%= fieldReadOnly%>" 
					                		title="Motivo Chiusura" onChange="fieldChanged();"/>
					            </td>
					        </tr>	
					        <tr>
					            <td class="etichetta">Esito complessivo percorso</td>
					            <td class="campo" nowrap>
					                <af:comboBox  name="CODESITOCOLL"  classNameBase="input" moduleName="M_Esito_Coll" 
					                		selectedValue="<%=codEsitoColl%>" addBlank="true" disabled="<%= fieldReadOnly%>" 
					                		title="Esito complessivo percorso" onChange="fieldChanged();"/>
					            </td>
					        </tr>
					        <tr>
					            <td class="etichetta">Linea di Azione</td>
					            <td class="campo" nowrap>
					                <af:comboBox  name="CODLINEAAZ"  classNameBase="input" moduleName="M_De_Linea_Azione" 
					                		selectedValue="<%=codLineaAz%>" addBlank="true" disabled="<%= fieldReadOnly%>" 
					                		title="Linea di Azione" onChange="fieldChanged();"/>
					            </td>
					        </tr>
					        <%if (!PRGACCORDO.equals("")) {%>
					        <tr>
			  					<td class="etichetta"></td>
					            <td class="campo" nowrap>
			  						<input type="button" name="pdfAccSin" class="pulsanti" value="Visualizza Accordo Sindacale" onClick="VisualizzaPdfAccordoSindacale()">
			  		 			</td>
			  		 			<input type="hidden" name="PRGACCORDO" value="<%=PRGACCORDO %>">
							</tr>
							<%}%>
				        </table>
		           	</div>
		        </td>
	        </tr>
	        <%} 
	    }%>       
        <tr><td colspan=2><br></td></tr>
        
        <tr>
          <td class="etichetta" colspan="2">
            <div class='sezione2' id='UlteInfo'>
                <img id='tendinaInfo' alt='Chiudi' src='../../img/aperto.gif' onclick='cambia(this, document.getElementById("altreInfoSez"));'/>
                Ulteriori informazioni&nbsp;  
            </div>
          </td>
        </tr>
        <tr>
          <td colspan="2">
            <div id="altreInfoSez" style="display:inline">
              <table class="main" width="100%" border="0">
                  <tr>
                    <td class="etichetta">Aspirazioni</td>
                      <td class="campo">
                          <af:textArea cols="80" rows="5" maxlength="2000" readonly="<%=fieldReadOnly%>" classNameBase="textarea"  
                          		name="strDesAspir" value="<%=aspirazioni%>" validateOnPost="true" 
                                required="false" title="Aspirazioni" onKeyUp="fieldChanged();"/>
                      </td>
                  </tr>
                  <tr><td class="etichetta">Motivazione</td>
                      <td class="campo">
                           <af:textArea cols="80" rows="5" maxlength="2000" readonly="<%=fieldReadOnly%>" classNameBase="textarea"  
                          		name="STRRISPOTENZ" value="<%=potenzialita%>" validateOnPost="true" 
                                required="false" title="Potenzialità" onKeyUp="fieldChanged();"/>
                      </td>
                  </tr>    
                  <tr><td class="etichetta">Obiettivo</td>
                      <td class="campo">
                           <af:textArea cols="80" rows="5" maxlength="2000" readonly="<%=fieldReadOnly%>" classNameBase="textarea"  
                          		name="STROBIETTIVO" value="<%=obiettivo%>" validateOnPost="true" 
                                required="false" title="Obiettivo" onKeyUp="fieldChanged();"/>
                      </td>
                  </tr>
                  <tr><td class="etichetta">Vincoli lavoratore</td>
                      <td class="campo">
                           <af:textArea cols="80" rows="5" maxlength="2000" readonly="<%=fieldReadOnly%>" classNameBase="textarea"  
                          		name="STRVINCLAV" value="<%=vincoliLavoratore%>" validateOnPost="true" 
                                required="false" title="VincoliLavoratore" onKeyUp="fieldChanged();"/>
                      </td>
                  </tr>   
                  <tr><td class="etichetta">Vincoli mercato</td>
                      <td class="campo">
                           <af:textArea cols="80" rows="5" maxlength="2000" readonly="<%=fieldReadOnly%>" classNameBase="textarea"  
                          		name="STRVINCEST" value="<%=vincoliMercato%>" validateOnPost="true" 
                                required="false" title="VincoliMercato" onKeyUp="fieldChanged();"/>
                      </td>
                  </tr>
                  <tr><td class="etichetta">Azioni concordate</td>
                      <td class="campo">
                           <af:textArea cols="80" rows="5" maxlength="2000" readonly="<%=fieldReadOnly%>" classNameBase="textarea"  
                          		name="STRAZIONICON" value="<%=azioniConcordate%>" validateOnPost="true" 
                                required="false" title="Azioni" onKeyUp="fieldChanged();"/>
                      </td>
                  </tr>
                  <tr><td class="etichetta">Analisi rispetto al lavoro</td>
                      <td class="campo">
                           <af:textArea cols="80" rows="5" maxlength="2000" readonly="<%=fieldReadOnly%>" classNameBase="textarea"  
                          		name="STRANALISILAVORO" value="<%=analisiLavoro%>" validateOnPost="true" 
                                required="false" title="Analisi rispetto al lavoro" onKeyUp="fieldChanged();"/>
                      </td>
                  </tr>
               </table>
             </div>
           </td>
        </tr>
        <tr>
            <td colspan="3" align=center>
                <table border="0" >
                    <tr align="center">          
                    	<%if(canModify) {
                    		if (!inserisciNuovo) {%>
                                <td><input type="button" onclick="fai_submit('aggiorna')" value="Aggiorna" class="pulsanti"></td>
                                <%if(!onlyInsert) {%>
                                    <td><input type="button" onclick="indietro()" value="Chiudi senza aggiornare" class="pulsanti"></td>
                                <%}%>
                            <%} else {%>
                                <td><input type="button"  onclick="fai_submit('inserisci')" value="Inserisci" class="pulsanti"></td>
                                <%if(!onlyInsert) {%>
                                    <td><input type="button" onclick="indietro()" value="Chiudi senza inserire" class="pulsanti"></td>
                                <%}%>
                            <%}%>
                            <%if(onlyInsert) {%>
                            	<td>
                                   <button onclick="indietroPopUpAssociazione()" class="pulsanti">Indietro</button>
                                </td>
                            <%}%>                            
                        <%} else {%>
                            <td><input type="button" onclick="indietro()" value = "Chiudi" class="pulsanti"></td>
                        <%}%>
                    </tr>
                    <tr align="center">                  
                        <td colspan="3"><input type="button" onclick="apriPercorsi()" value="Percorsi concordati" class="pulsanti"></td>                    
                    </tr>
                </table>
                
                
                
                
            </td>
        </tr>        
        <tr>
            <td class="etichetta">
                <input type="hidden" name="MODULE" value=""><%-- campo valorizzato dalla funzione js specifica della azione richiesta --%>
                <input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore %>">
                <input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione %>">
                <input type="hidden" name="PRGCOLLOQUIO" value="<%=Utils.notNull(prgColloquio) %>">
                <input type="hidden" name="NUMKLOCOLLOQUIO" value="<%=numklocolloquio %>">
                <input type="hidden" name="CODCPI" value="<%=codCPI%>">
                <input type="hidden" name="NONFILTRARE" value="<%=nonFiltrare%>">
                <input type="hidden" name="CODMONOPROGRAMMA" value="<%=Utils.notNull(codMonoProgramma)%>">
                <%-- ATTENZIONE SE REINSERISCI LA COMBO BOX ALLORA TOGLI QUESTO --%>
                <input type="hidden" name="PAGE" VALUE="COLLOQUIOPAGE"><%-- valore eventualmente cambiato dalla funzione js handler --%>
                <%if(onlyInsert){%>
                  <input type = "hidden" name="ONLY_INSERT" value="1">          
                  <input type = "hidden" name="statoSezioni" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"statoSezioni")%>">
                <%}%>
                <%if (serviceRequest.getAttribute("DA_PATTO_COD_LST_TAB")==null){%>
                	<%=QueryString.toInputHidden(serviceRequest, "COD_LST_TAB", "DA_PATTO_COD_LST_TAB")%>
                <%}else {%>
                	<%=QueryString.toInputHidden(serviceRequest, "DA_PATTO_COD_LST_TAB")%>
                <%}%>
                <%--input type = "hidden" name="COD_LST_TAB" value="<%=COD_LST_TAB%>"--%>
            </td>
            <td class="campo"></td>
        </tr>
    </table>
    <%out.print(htmlStreamBottom);%> 
    <%if (!inserisciNuovo && operatoreInfo != null) {%>    
      <center><table><tr><td align="center">
      <% operatoreInfo.showHTML(out); %>
      </td></tr></table></center>
    <%}%>
    
</af:form>
<script language="javascript">
  if ((document.Frm1.strDesAspir.value == "") && (document.Frm1.STRRISPOTENZ.value == "") && (document.Frm1.STROBIETTIVO.value == "") && 
	  (document.Frm1.STRVINCLAV.value == "") && (document.Frm1.STRVINCEST.value == "") && (document.Frm1.STRAZIONICON.value == "") && 
	  (document.Frm1.STRANALISILAVORO.value == "")){
    cambia(document.getElementById("tendinaInfo"), document.getElementById("altreInfoSez"));
  }
</script>

</body>
</html>

<% } %>