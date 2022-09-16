<!-- @author: Stefania Orioli -->
<%@page import="it.eng.sil.action.report.PropertiesReport"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                com.engiweb.framework.security.*,
                it.eng.afExt.utils.*, java.math.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.*,
                it.eng.sil.util.*"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%

ProfileDataFilter filter = new ProfileDataFilter(user, "CMMatchDettGraduatoriaPage");
boolean canView = filter.canView();
if ( !canView ){
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	return;
}  

String _page = (String)serviceRequest.getAttribute("PAGE");
String prgRichiestaAz = serviceRequest.getAttribute("PRGRICHIESTAAZ").toString();
String prgAzienda = serviceRequest.getAttribute("PRGAZIENDA").toString();
String prgUnita = serviceRequest.getAttribute("PRGUNITA").toString();
String _cdnFunzione = serviceRequest.getAttribute("CDNFUNZIONE").toString();
Object numCandidati = serviceRequest.getAttribute("NUM_CANDIDATI");
if (numCandidati == null){
	numCandidati = "";
}
String codCpi = StringUtils.getAttributeStrNotNull(serviceRequest, "codCpi");
String ConcatenaCpi = StringUtils.getAttributeStrNotNull(serviceRequest, "ConcatenaCpi");

String prgRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGROSA");
String codiceRit = "";
codiceRit = "0"; // per tutte le altre operazioni considero il codice di errore 0

SourceBean rowsCandidati = (SourceBean) serviceResponse.getAttribute("CMCandidatiGraduatoria.ROWS");
int numRecords = 0;
if(rowsCandidati != null)  
	numRecords = Integer.parseInt(rowsCandidati.getAttribute("NUM_RECORDS").toString()); 
// inizializzazione delle variabili usate nella testata

String numRichiesta="", numAnno="",prgTipoRosa="", prgTipoIncrocio="",tipoIncrocio="",prgIncrocio="", tipoRosa="",prgAlternativa="", 
	 strAlternativa="",prgOrig="", prgCopia1="",numKloRosa="",utMod="", ultimaModifica="", numRichiestaOrig="",
	 utAttivo="" , ordinamento="", tipoLista="", descrtipoLista="";
String prgRosaFiglia = "";	 
String queryStringBack="";	 
String htmlStreamBottom="", htmlStreamTop = "";  

SourceBean numMaxCandidatiSB = (SourceBean) serviceResponse.getAttribute("CM_NUM_MAX_CANDIDATI.ROWS.ROW");
String numMaxCandidati = ((BigDecimal)numMaxCandidatiSB.getAttribute("maxCandidati")).toString();

	 
SourceBean infoRosa=(SourceBean)serviceResponse.getAttribute("CMDettaglioGraduatoria.row");
prgRosa = ((BigDecimal)infoRosa.getAttribute("PRGROSA")).toString();
numRichiesta = StringUtils.getAttributeStrNotNull(infoRosa, "NUMRICHIESTA");
numRichiestaOrig = StringUtils.getAttributeStrNotNull(infoRosa, "NUMRICHIESTAORIG");
prgRosaFiglia = infoRosa.getAttribute("PRGROSAFIGLIA") == null ? "" : ((BigDecimal)infoRosa.getAttribute("PRGROSAFIGLIA")).toString();
numAnno = StringUtils.getAttributeStrNotNull(infoRosa, "NUMANNO");
prgIncrocio = StringUtils.getAttributeStrNotNull(infoRosa, "PRGINCROCIO");
tipoIncrocio = StringUtils.getAttributeStrNotNull(infoRosa, "TIPOINCROCIO");
prgTipoIncrocio = StringUtils.getAttributeStrNotNull(infoRosa, "PRGTIPOINCROCIO");
tipoRosa = StringUtils.getAttributeStrNotNull(infoRosa, "TIPOROSA");
prgTipoRosa = StringUtils.getAttributeStrNotNull(infoRosa, "PRGTIPOROSA");
BigDecimal checkDataAvv = infoRosa.getAttribute("checkDataAvv") == null ? new BigDecimal(-1) : (BigDecimal) infoRosa.getAttribute("checkDataAvv");
String dataPubb = infoRosa.getAttribute("dataPubb") == null ? "" : (String) infoRosa.getAttribute("dataPubb"); 
String strTipoGrd = "";
if (("2").equals(prgTipoRosa)) {
	strTipoGrd = "Grezza";    
}
else {
	strTipoGrd = "Definitiva";
}
prgAlternativa = StringUtils.getAttributeStrNotNull(infoRosa, "PRGALTERNATIVA");
strAlternativa = "";
if(!prgAlternativa.equals("")) { strAlternativa = "Profilo n. " + prgAlternativa; }
prgOrig = StringUtils.getAttributeStrNotNull(infoRosa, "PRGRICHIESTAORIG");
prgCopia1 = StringUtils.getAttributeStrNotNull(infoRosa, "PRGCOPIA1");
numKloRosa = StringUtils.getAttributeStrNotNull(infoRosa, "NUMKLOROSA");
utMod = StringUtils.getAttributeStrNotNull(infoRosa, "CDNUTMOD");
ultimaModifica = StringUtils.getAttributeStrNotNull(infoRosa, "ULTIMAMODIFICA");
utAttivo = Integer.toString(user.getCodut());
ordinamento = StringUtils.getAttributeStrNotNull(infoRosa, "NUMORDPROVINCIA");

boolean disableChangeProf = false;
Object sbComboProf = serviceResponse.getAttribute("COMBO_ALTERNATIVA.ROWS.ROW");
if (sbComboProf == null) {
	disableChangeProf = true;
}

SourceBean sbStato = (SourceBean) serviceResponse.getAttribute("CMMatchStatoRichOrig.ROWS.ROW");
String cdnStatoRich = StringUtils.getAttributeStrNotNull(sbStato, "CDNSTATORICH");
BigDecimal totGraduatorieArt8 = (BigDecimal) sbStato.getAttribute("tot_graduatorie_art8");  
BigDecimal totGraduatorieArt18 = (BigDecimal) sbStato.getAttribute("tot_graduatorie_art18");  
BigDecimal totGraduatorieArt1 = (BigDecimal) sbStato.getAttribute("tot_graduatorie_art1");  
BigDecimal totGraduatorieDefinitiveArt8 = (BigDecimal) sbStato.getAttribute("tot_graduatorie_def_art8");
BigDecimal totGraduatorieDefinitiveArt18 = (BigDecimal) sbStato.getAttribute("tot_graduatorie_def_art18");
BigDecimal totGraduatorieDefinitiveArt1 = (BigDecimal) sbStato.getAttribute("tot_graduatorie_def_art1");
descrtipoLista = StringUtils.getAttributeStrNotNull(sbStato, "DESCRTIPOLISTA");
tipoLista = StringUtils.getAttributeStrNotNull(sbStato, "CODTIPOLISTA"); 

Vector checkCandidatiAvviati = new Vector();
SourceBean sbCandidatiAvviati = (SourceBean)serviceResponse.getAttribute("CMCheckCandidatiAvviatiGraduatoria.ROWS");
Vector vectCandidatiAvviati = (Vector) sbCandidatiAvviati.getAttributeAsVector("ROW");
if (vectCandidatiAvviati != null) { 
	for (int i=0; i<vectCandidatiAvviati.size(); i++) {
		SourceBean candidatoAvviato = (SourceBean)vectCandidatiAvviati.get(i);
		Object cdnLavoratoreAvviato = candidatoAvviato.getAttribute("CDNLAVORATORE");
		checkCandidatiAvviati.add(cdnLavoratoreAvviato);
	}
}

String queryString = "cdnFunzione="+_cdnFunzione+"&PAGE="+_page+"&MODULE=CMCandidatiGraduatoria"+"&prgRosa="+prgRosa+
	"&prgAzienda="+prgAzienda+"&prgUnita="+prgUnita+"&PRGRICHIESTAAZ="+prgRichiestaAz+"&prgTipoIncrocio="+prgTipoIncrocio+
	"&PRGTIPOROSA="+prgTipoRosa+"&codCpi="+codCpi+"&ConcatenaCpi="+ConcatenaCpi ;

%>

<%
PageAttribs attributiMatch = new PageAttribs(user, "CMGestGraduatoriePage");
boolean gestCopia = attributiMatch.containsButton("GEST_COPIA");
if(!gestCopia) {
  // L'utente non è abilitato alla gestione della copia di lavoro, ma deve utilizzare solo 
  // la richiesta originale (numStorico=0)
  prgCopia1 = prgOrig;
}

PageAttribs attributi = new PageAttribs(user, "CMMatchDettGraduatoriaPage");
boolean aggiungiLavoratore= false;
boolean delMassivaCandidati= false;
boolean CMStampa = false;
boolean avviaPrimi = false;
boolean cambioProfilo = attributi.containsButton("CAMBIO_PROFILO");
boolean avviaSelez = false;
boolean riapri = false;
boolean rendiDef = false;
boolean delFisica = false;
boolean consultaAdes = attributi.containsButton("CONSULTA_ADESIONI");
boolean approvazione = attributi.containsButton("APPROVAZIONE");
boolean punteggio = attributi.containsButton("PUNTEGGIO");
boolean calcPunteggio = false;
boolean posizione = false;
boolean disableAvviamento = false;

if (cdnStatoRich.compareTo("4")!=0 && cdnStatoRich.compareTo("5")!=0){
	avviaPrimi = attributi.containsButton("AVVIA_PRIMI");	
	avviaSelez = attributi.containsButton("AVVIA_SELEZIONE");
	riapri = attributi.containsButton("RIAPRI");
	rendiDef = attributi.containsButton("RENDI_DEFINITIVA");
	delFisica = attributi.containsButton("DELETE_FISICA");	
	calcPunteggio = attributi.containsButton("CALC_PUNTEGGIO");
	posizione = attributi.containsButton("CALC_POSIZIONE");
	CMStampa = attributi.containsButton("STAMPA_INTERNA");
	
	// avvia a selezioene e avvia i primi devono essere attivi se tutte le graduatorie sono definitive
	if (("10".equalsIgnoreCase(prgTipoIncrocio))){
		if (totGraduatorieDefinitiveArt8.compareTo(totGraduatorieArt8) != 0 ) {
			disableAvviamento = true;
		}
	}
	if (("11".equalsIgnoreCase(prgTipoIncrocio))){
		if (totGraduatorieDefinitiveArt18.compareTo(totGraduatorieArt18) != 0 ) {
			disableAvviamento = true;
		}
	} 
	if (("12".equalsIgnoreCase(prgTipoIncrocio))){
		if (totGraduatorieDefinitiveArt1.compareTo(totGraduatorieArt1) != 0 ) {
			disableAvviamento = true;
		}
	}
	    
}  

%>

<%
String token = "";
String urlDiLista = "";
String refListaBtn = "";
if (sessionContainer!=null){
//  token = "_TOKEN_" + "IDOLISTARICHIESTEPAGE";
  token = "_TOKEN_" + "CMGestGraduatoriePage";  
 // urlDiLista = (String) sessionContainer.getAttribute(token.toUpperCase());
  urlDiLista = "PAGE=CMGestGraduatoriePage&MODULE=CMElencoGraduatorieModule&PRGORIG="+prgRichiestaAz+"&PRGAZIENDA="+prgAzienda+"&PRGRICHIESTAAZ="+prgRichiestaAz+"&PRGUNITA="+prgUnita+"&CDNFUNZIONE="+_cdnFunzione+"&MESSAGE=LIST_PAGE&LIST_PAGE=1";
  if (urlDiLista!=null && !urlDiLista.equals("")) {
    refListaBtn ="<a href=\"AdapterHTTP?" + urlDiLista + "\"><img src=\"../../img/rit_lista.gif\" border=\"0\"></a>";
  } else {    
  	urlDiLista = "PAGE=CMGestGraduatoriePage&MODULE=CMElencoGraduatorieModule&PRGORIG="+prgRichiestaAz+"&PRGAZIENDA="+prgAzienda+"&PRGRICHIESTAAZ="+prgRichiestaAz+"&PRGUNITA="+prgUnita+"&CDNFUNZIONE="+_cdnFunzione+"&MESSAGE=LIST_PAGE&LIST_PAGE=1";
  	refListaBtn ="<a href=\"AdapterHTTP?" + urlDiLista + "\"><img src=\"../../img/rit_lista.gif\" border=\"0\"></a>";
  }
}
String htmlStreamTopInfo = StyleUtils.roundTopTableInfoRetLista(urlDiLista);
String htmlStreamBottomInfo = StyleUtils.roundBottomTableInfoNobr();

String messRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE");
String listPageRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE");

//Prendo la conf
String ConfigGraduatoria_cm = serviceResponse.containsAttribute("M_GetConfigGraduatoria_cm.ROWS.ROW.codmonotipogradcm")?serviceResponse.getAttribute("M_GetConfigGraduatoria_cm.ROWS.ROW.codmonotipogradcm").toString():"0";

//Prendo il codice regione
String codRegioneSil_cm = serviceResponse.containsAttribute("M_GetInfoRegione.ROWS.ROW.codregione")?serviceResponse.getAttribute("M_GetInfoRegione.ROWS.ROW.codregione").toString():"";

// codice per l'avvio a selezione

SourceBean candidatiRespose = (SourceBean) serviceResponse.getAttribute("CMCandidatiGraduatoria.ROWS");
Vector rows = candidatiRespose != null ? candidatiRespose.getAttributeAsVector("ROW") : new Vector();

String keySB = prgRichiestaAz+"_"+prgTipoIncrocio;	

Vector rowsCandidatiScelti = (Vector)sessionContainer.getAttribute(keySB);	
if (rowsCandidatiScelti == null) {
	rowsCandidatiScelti = new Vector();
}

%>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <title>Dettaglio Graduatoria</title>
  <%@ include file="../../jsp/documenti/_apriGestioneDoc.inc"%>
  
 <script language="Javascript" src="../../js/utili.js" type="text/javascript"></script>
 <script language="Javascript" src="../../js/lightCustomTL.js" type="text/javascript"></script>
  <script language="Javascript">
  var opened;
  var apriStampa = <%=serviceRequest.containsAttribute("AVVIA_SELEZIONE") %>;
  var arrCandidati = new Array();	   	
        
	function goConfirmGenericCustomTL(url, alertFlag) {   
				
		var urlArray = new Array();
		var message;
		var page;
		var module;
		var prgNominativo;
		var cdnLavoratore;
		var list_page;
		var disableList;
		var old_list_page;
		var evidenze;
		urlArray = url.split("&");
		
		for(i=0; i<urlArray.length; i++) {			
		 	if (urlArray[i].substring(0, 4) == "PAGE") {		 	
				page = urlArray[i];
		 	}
		 	if (urlArray[i].substring(0, 6) == "MODULE") {		 	
				module = urlArray[i];
		 	}		 	
		 	
		 	if (urlArray[i].substring(0, 13) == "PRGNOMINATIVO") {		 	
				prgNominativo = urlArray[i];
		 	}
		 	
		 	if (urlArray[i].substring(0, 13) == "CDNLAVORATORE") {		 	
				cdnLavoratore = urlArray[i];
		 	}
		 	
		 	if (urlArray[i].substring(0, 7) == "MESSAGE") {		 	
				message = urlArray[i];
		 	}						
		 	
		 	if (urlArray[i].substring(0, 9) == "LIST_PAGE") {		 	
				list_page = urlArray[i];
		 	}
		 	
		 	if (urlArray[i].substring(0, 13) == "OLD_LIST_PAGE") {		 	
				old_list_page = urlArray[i];
		 	}
		 	
		 	if (urlArray[i].substring(0, 7) == "APRI_EV") {		 	
				evidenze = urlArray[i];
		 	}		 	
		}

		var s = "AdapterHTTP?";
	  	s += "&PRGROSA=<%=prgRosa%>";  	  	 
	  	s += "&CDNFUNZIONE=<%=_cdnFunzione%>";  
	  	s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
	  	s += "&PRGAZIENDA=<%=prgAzienda%>";
	  	s += "&PRGUNITA=<%=prgUnita%>";
	    s += "&PRGINCROCIO=<%=prgIncrocio%>";  
	  	s += "&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>";
	  	s += "&PRGTIPOROSA=<%=prgTipoRosa%>";
	  	s += "&codCpi=<%=codCpi%>";
	  	s += "&ConcatenaCpi=<%=ConcatenaCpi%>";
		
		if (page != null) {			
			s += "&"+page;			
		}
		if (module != null) {
			s += "&"+module;
		}						
		if (prgNominativo != null) {
			s += "&"+prgNominativo;
		}
		if (cdnLavoratore != null) {
			s += "&"+cdnLavoratore;
		}				
		
		if (message != null) {		    
			s += "&"+message;
		}
		
		if (evidenze != null) {		    
			s += "&"+evidenze;
		}
		
		if (page=="PAGE=CMMatchDettGraduatoriaPage") { 			
			if (list_page != null) {
				s += "&"+list_page;
			}		
		}
		else {
			s += "&"+old_list_page;
		}
				
		//alert(s);		

		document.frm1.action = s;
	   	    
		document.frm1.submit();

	}
  

	function rinfresca(){
		try{
			window.top.footer.rinfresca(); 
		} catch(e) {} 
	}
  
  
    function getCandidatiCheck() {
    	
    	<%
    	// solo se è definitiva
    	if (("3").equalsIgnoreCase(prgTipoRosa)) {
    	
	    	// controllo i checkbox da chekkare
	    	for (int i=0; i<rows.size(); i++) {
	    		SourceBean rowLista = (SourceBean)rows.get(i);
	    		BigDecimal cdnLavoratoreRowLista = (BigDecimal)rowLista.getAttribute("CDNLAVORATORE");
	    		for (int j=0; j<rowsCandidatiScelti.size(); j++) {
	    			String cdnLavoratoreCandidatoScelto = (String)rowsCandidatiScelti.get(j);
	    			BigDecimal prg = new BigDecimal(cdnLavoratoreCandidatoScelto);
	    			if (cdnLavoratoreCandidatoScelto != null) {
		    			if (prg.compareTo(cdnLavoratoreRowLista) == 0) {
		    %>
		    				document.frm1.elements['CK_<%=cdnLavoratoreRowLista%>'].checked = true;		  
			<%	    		 
						}		
					}
	    		} 
	    	}
    	
    	}
    	%>      		    	
	}
    
    
    function getCandidatiDisabled() {
    	
    	<%    	
    	if (("3").equalsIgnoreCase(prgTipoRosa)) {
	    	// controllo i checkbox da chekkare
	    	for (int i=0; i<rows.size(); i++) {
	    		SourceBean rowLista = (SourceBean)rows.get(i);
	    		BigDecimal cdnLavoratoreRowLista = (BigDecimal)rowLista.getAttribute("CDNLAVORATORE");
	    		for (int j=0; j<checkCandidatiAvviati.size(); j++) {
	    			BigDecimal cdnLavoratoreCandidatoAvviato = (BigDecimal)checkCandidatiAvviati.get(j);
	    			if (cdnLavoratoreCandidatoAvviato != null) {
		    			if (cdnLavoratoreCandidatoAvviato.compareTo(cdnLavoratoreRowLista) == 0) {
	    %>
	    					document.frm1.elements['CK_<%=cdnLavoratoreRowLista%>'].checked = false;
	    					document.frm1.elements['CK_<%=cdnLavoratoreRowLista%>'].disabled = true;		  
		<%	    		 
						}		
					}
	    		} 
	    	}
    	}
    	%>      		    	
	}
  
  function selDeselCand()
  {
  	var coll = document.getElementsByName("SD_Candidati");
  	var checkSel = coll[0].checked;  	
  	var i;
  	
  	<%
	// controllo i checkbox da chekkare
	for (int i=0; i<rows.size(); i++) {
		SourceBean rowLista = (SourceBean)rows.get(i);
		BigDecimal cdnLavoratoreRowLista = (BigDecimal)rowLista.getAttribute("CDNLAVORATORE");
		String cdnCanStr =  cdnLavoratoreRowLista.toString();		
    %>
    	if (checkSel) {
    		if (!document.frm1.elements['CK_<%=cdnCanStr%>'].disabled) { 
	    		document.frm1.elements['CK_<%=cdnCanStr%>'].checked = true;		  
	    	}
	    }
	    else {
		    document.frm1.elements['CK_<%=cdnCanStr%>'].checked = false;	
		    
		    arrCandidati.push(<%=cdnCanStr%>);   		
   			var candidati = arrCandidati.join(";");   		   		
   			document.frm1.ARRAY_CDNLAVORATORE.value = candidati;
		    	  
	    }
	<%	    		 			
	}
	%>   	
  }
   
  
  function openRich_PopUP(prgRich) {
     //window.open ("AdapterHTTP?PAGE=IdoDettaglioSinteticoPage&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&CDNFUNZIONE=<%=_cdnFunzione%>&POPUP=1", "DettaglioSintetico", 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES,left=150,top=100');
     window.open ("AdapterHTTP?PAGE=IdoDettaglioSinteticoPage&PRGRICHIESTAAZ=" +prgRich +"&CDNFUNZIONE=<%=_cdnFunzione%>&POPUP=1", "DettaglioSintetico", 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES');
  }

  function stampaInterna(){
 	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;
	
	var url = "AdapterHTTP?PAGE=ASStampeInternePage";
	url += "&prgRichiestaAz="+"<%=prgRichiestaAz%>";
    url += "&prgTipoIncrocio="+"<%=prgTipoIncrocio%>";
    url += "&PRGAZIENDA="+"<%=prgAzienda%>";
    url += "&PRGUNITA="+"<%=prgUnita%>";
    url += "&CDNFUNZIONE="+"<%=_cdnFunzione%>";
    url += "&PRGROSA="+"<%=prgRosa%>";
    url += "&PRGTIPOROSA="+"<%=prgTipoRosa%>";
    url += "&codCpi="+"<%=codCpi%>";
    url += "&ConcatenaCpi="+"<%=ConcatenaCpi%>";
    setWindowLocation(url);
   }


  
   //Ale
   function stampaDefinitiva(){
 	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;
	
	var url = "AdapterHTTP?PAGE=ASStampeDefinitive_GRADUATORIApage";
	url += "&prgRichiestaAz="+"<%=prgRichiestaAz%>";
    url += "&prgTipoIncrocio="+"<%=prgTipoIncrocio%>";
    url += "&PRGAZIENDA="+"<%=prgAzienda%>";
    url += "&PRGUNITA="+"<%=prgUnita%>";
    url += "&CDNFUNZIONE="+"<%=_cdnFunzione%>";
    url += "&PRGROSA="+"<%=prgRosa%>";
    url += "&PRGTIPOROSA="+"<%=prgTipoRosa%>";
    url += "&codCpi="+"<%=codCpi%>";
    url += "&ConcatenaCpi="+"<%=ConcatenaCpi%>";
    setWindowLocation(url);
   }
   
   
    
   function stampa(){
	    apriGestioneDoc('RPT_GRADUATORIA_DEF','&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>&ConcatenaCpi=<%=ConcatenaCpi%>&codCpi=<%=codCpi%>','ALL68O');
   }

   function stampaNominativa(){
	    apriGestioneDoc('RPT_GRADUATORIA_DEF','&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>&ConcatenaCpi=<%=ConcatenaCpi%>&codCpi=<%=codCpi%>&Nominativa=1','ALL68O');
  }

   function stampaAnonima(){
	    apriGestioneDoc('RPT_GRADUATORIA_DEF','&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>&ConcatenaCpi=<%=ConcatenaCpi%>&codCpi=<%=codCpi%>&Nominativa=0','ALL68O');
   }
	
  //fine ale 
  function apriStampa(RPT,paramPerReport,tipoDoc)
  { 
    // Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;
    
    //RPT: attributo name del tag ACTION nel file action.xml relativo al report da visualizzare
    //paramPerReport: parametri necessari a visualizzare il report 
    //tipoDoc: codice relativo al tipo di documento cosi come inserito nel campo CODTIPODOCUMENTO della tab. DE_DOC_TIPO
    
    var urlpage="AdapterHTTP?ACTION_NAME="+RPT;
    urlpage+=paramPerReport; //Quelli che nella calsse sono inseriti nel vettore params
    urlpage+="&tipoDoc="+tipoDoc;

    if(confirm("Vuoi PROTOCOLARE il file pirma di visualizzarlo?\n\nSeleziona\n- \"OK\"        per PROTOCOLLARE prima di visualizzare la stampa\n- \"Annulla\"  per visualizzare SENZA PROTOCOLLARE"))
    { urlpage+="&salvaDB=true";
    }
    else
    { urlpage+="&salvaDB=false";
    }
    
    setWindowLocation(urlpage);
    
   }//apriStampa()

  function Delete(prgRosa, prgNominativo, cdnLavoratore, cdnFunzione, prgRichiestaAz, 
  				 prgAzienda, prgUnita, prgIncrocio, strMessage, strListPage, nomeCognome, prgTipoIncrocio) {
	
	if (strListPage == "") {
		strListPage = "1";
	}
	
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var t="Sicuri di voler rimuovere il nominativo:\n";
    t += nomeCognome.replace(/\^/g, '\'')+ "?";
    
    if ( confirm(t) ) {
  
      var s = "AdapterHTTP?PAGE=CMMatchDettGraduatoriaPage";
      s += "&MODULE=CMDelFisicaLavDaGraduatoria";
      s += "&PRGROSA=" + prgRosa;
      s += "&PRGNOMINATIVO=" + prgNominativo;
      s += "&CDNLAVORATORE=" + cdnLavoratore;
      s += "&CDNFUNZIONE=" + cdnFunzione;
      s += "&PRGRICHIESTAAZ=" + prgRichiestaAz;
      s += "&PRGAZIENDA=" + prgAzienda;
      s += "&PRGUNITA=" + prgUnita;
      s += "&PRGINCROCIO=" + prgIncrocio;
      s += "&PRGTIPOINCROCIO=" + prgTipoIncrocio;
      s += "&codCpi=<%=codCpi%>"; 
      s += "&ConcatenaCpi=<%=ConcatenaCpi%>";
      s += "&MESSAGE=LIST_PAGE";
      s += "&LIST_PAGE=" + strListPage;

      setWindowLocation(s);
    }
  }
  
  function rendiDefinitiva() {
	
	if (isInSubmit()) return;
	  
	var t="Sicuri di voler rendere la graduatoria definitiva?";
    
	if ( confirm(t) ) {

		var s = "AdapterHTTP?PAGE=CMMatchDettGraduatoriaPage";
	  	s += "&MODULE=CMSetDefinitivaGraduatoria";
	  	s += "&PRGROSA=<%=prgRosa%>";   
	  	s += "&CDNFUNZIONE=<%=_cdnFunzione%>";  
	  	s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
	  	s += "&PRGAZIENDA=<%=prgAzienda%>";
	  	s += "&PRGUNITA=<%=prgUnita%>";
	    s += "&PRGINCROCIO=<%=prgIncrocio%>";  
	  	s += "&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>";
	  	s += "&NUMKLOROSA=<%=numKloRosa%>";
	  	s += "&codCpi=<%=codCpi%>";
	  	s += "&ConcatenaCpi=<%=ConcatenaCpi%>";
	
	  	setWindowLocation(s);
    }
	  
  }
  
  
  function ricalcolaPosizione() {
	
	if (isInSubmit()) return;
	  
	var t="Sicuri di voler ricalcolare la posizione?";
    
	if ( confirm(t) ) {

		var s = "AdapterHTTP?PAGE=CMMatchDettGraduatoriaPage";
	  	s += "&MODULE=CMCalcolaPosizioneModule";
	  	s += "&PRGROSA=<%=prgRosa%>";   
	  	s += "&CDNFUNZIONE=<%=_cdnFunzione%>";  
	  	s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
	  	s += "&PRGAZIENDA=<%=prgAzienda%>";
	  	s += "&PRGUNITA=<%=prgUnita%>";
	    s += "&PRGINCROCIO=<%=prgIncrocio%>";  
	  	s += "&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>";
	  	s += "&codCpi=<%=codCpi%>";
	  	s += "&ConcatenaCpi=<%=ConcatenaCpi%>";	  	
	
	  	setWindowLocation(s);
    }
	  
  }
  
  function riapriGraduatoria() {
	
	if (isInSubmit()) return;
	  
	//var t="Confermi l'operazione?";
    
	//if ( confirm(t) ) {

		var s = "AdapterHTTP?PAGE=CMGestRiapriGraduatoriePage";
	  	s += "&MODULE=CMGestRiapriGraduatorieModule";
	  	s += "&PRGROSA=<%=prgRosa%>";   
	  	s += "&CDNFUNZIONE=<%=_cdnFunzione%>";  
	  	s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
	  	s += "&PRGAZIENDA=<%=prgAzienda%>";
 	  	s += "&PRGUNITA=<%=prgUnita%>";
        s += "&PRGINCROCIO=<%=prgIncrocio%>";  
	  	s += "&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>"; 
	  	s += "&PRGTIPOROSA=<%=prgTipoRosa%>";
	  	s += "&codCpi=<%=codCpi%>";
	  	s += "&ConcatenaCpi=<%=ConcatenaCpi%>";  

	  	setWindowLocation(s);
    //}
	  
  }
  
  
  function gestRiapriGraduatoria() {
	
	if (isInSubmit()) return;
	  
	var t="Confermi l'operazione?";
    
	if ( confirm(t) ) {

		var s = "AdapterHTTP?PAGE=CMMatchDettGraduatoriaPage";
	  	s += "&MODULE=CMRiapriGraduatoria";
	  	s += "&PRGROSA=<%=prgRosa%>";   
	  	s += "&CDNFUNZIONE=<%=_cdnFunzione%>";  
	  	s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
	  	s += "&PRGAZIENDA=<%=prgAzienda%>";
 	  	s += "&PRGUNITA=<%=prgUnita%>";
        s += "&PRGINCROCIO=<%=prgIncrocio%>";  
	  	s += "&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>";
	  	s += "&codCpi=<%=codCpi%>";
	  	s += "&ConcatenaCpi=<%=ConcatenaCpi%>"; 

	  	setWindowLocation(s);
    }
	  
  }
  
  
  function approvazione() {
	
	if (isInSubmit()) return;
	  
	//var t="Confermi l'operazione?";
    
	//if ( confirm(t) ) {

		var s = "AdapterHTTP?PAGE=CMGestApprovaGraduatoriePage";
	  	s += "&MODULE=CMGestApprovaGraduatorieModule";
	  	s += "&CDNSTATORICH=<%=cdnStatoRich%>";  
	  	s += "&PRGROSA=<%=prgRosa%>";  
	  	s += "&PRGTIPOROSA=<%=prgTipoRosa%>"; 	 
	  	s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
	  	s += "&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>"; 
	  	s += "&PRGINCROCIO=<%=prgIncrocio%>"; 
	  	s += "&PRGAZIENDA=<%=prgAzienda%>";
 	  	s += "&PRGUNITA=<%=prgUnita%>";         	  	
	  	s += "&CDNFUNZIONE=<%=_cdnFunzione%>";   	
	  	s += "&codCpi=<%=codCpi%>";
	  	s += "&ConcatenaCpi=<%=ConcatenaCpi%>";  

	  	setWindowLocation(s);
    //}
	  
  }
  
  function aggiungiLavoratori() {
	
	if (isInSubmit()) return;
	
	var f = "AdapterHTTP?PAGE=CMMatchLavoratoriArt1Page&CODTIPOLISTA=<%=tipoLista%>";
	f += "&PRGROSA=<%=prgRosa%>";   
	f += "&CDNFUNZIONE=<%=_cdnFunzione%>";  
	f += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
	f += "&PRGAZIENDA=<%=prgAzienda%>";
 	f += "&PRGUNITA=<%=prgUnita%>";
    f += "&PRGINCROCIO=<%=prgIncrocio%>";  
	f += "&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>"; 
	f += "&PRGTIPOROSA=<%=prgTipoRosa%>";
	f += "&codCpi=<%=codCpi%>";
	f += "&ConcatenaCpi=<%=ConcatenaCpi%>";  
  	var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=750,height=650,top=30,left=180";
  	opened = window.open(f, '_blank', feat);  		  
  	
  }
  
  function matchAggiungiLavoratore(cdnLavoratoreLista, prgRosa){
  	//document.frm.CKBOX_ART1.value = cdnLavoratoreLista;
  	//document.frm.PRGROSA.value = prgRosa;  
	var s = "AdapterHTTP?PAGE=CMMatchDettGraduatoriaPage";
  	s += "&MODULE=CMAssociaLavoratoriArt1Module";
  	s += "&CDNSTATORICH=<%=cdnStatoRich%>";  
  	s += "&PRGROSA=<%=prgRosa%>";  
  	s += "&PRGTIPOROSA=<%=prgTipoRosa%>"; 	 
  	s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
  	s += "&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>"; 
  	s += "&PRGINCROCIO=<%=prgIncrocio%>"; 
  	s += "&PRGAZIENDA=<%=prgAzienda%>";
 	s += "&PRGUNITA=<%=prgUnita%>";         	  	
  	s += "&CDNFUNZIONE=<%=_cdnFunzione%>";   	
  	s += "&codCpi=<%=codCpi%>";
  	s += "&ConcatenaCpi=<%=ConcatenaCpi%>";
  	s += "&P_CDNUTENTE=<%=user.getCodut()%>";
  	s += "&CKBOX_ART1="+cdnLavoratoreLista;  

	setWindowLocation(s);

  	opened.close();
  }
  
  function cambiaProfilo() {
		
	if (isInSubmit()) return;
	
	var cambiaProfilo = document.frm1.COMBO_ALTERNATIVA.value;
	
	var t="Confermi l'operazione?";
   
    if (cambiaProfilo != "") {    
		if ( confirm(t) ) {				
		
			var s = "AdapterHTTP?PAGE=CMMatchDettGraduatoriaPage";
		  	s += "&MODULE=CMCandidatiGraduatoria";
		  	s += "&PRGROSA="+cambiaProfilo ;   
		  	s += "&CDNFUNZIONE=<%=_cdnFunzione%>";  
		  	s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
		  	s += "&PRGAZIENDA=<%=prgAzienda%>";
		  	s += "&PRGUNITA=<%=prgUnita%>";
		    s += "&PRGINCROCIO=<%=prgIncrocio%>";  
		  	s += "&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>";
		  	s += "&codCpi=<%=codCpi%>";
		  	s += "&ConcatenaCpi=<%=ConcatenaCpi%>";
		
		  	document.frm1.action = s;
		   	    
			document.frm1.submit();
		}
		  	
    }
	else {
		alert("Attenzione: bisogna selezione un profilo!");
		return false;
	}	
	  		  
  }  
  
  function avviaSelezione() {
	
	if (isInSubmit()) return;
	var check = 0;
	
	<%
	int vectSize = rowsCandidatiScelti.size();
	if (vectSize == 0) {
	%>
		var check = 0;
		<%
		// controllo i checkbox da chekkare
		for (int i=0; i<rows.size(); i++) {
			SourceBean rowLista = (SourceBean)rows.get(i);
			BigDecimal cdnLavoratoreRowLista = (BigDecimal)rowLista.getAttribute("CDNLAVORATORE");
			String cdnCanStr =  cdnLavoratoreRowLista.toString();		
	    %>
	        if(document.frm1.elements['CK_<%=cdnCanStr%>'].checked == true) {
	        	check = 1;
	        }           	
		<%	    		 			
		}
		%>
	<%
	}	
	else {
	%>
		check = 1;
	<%
	}
	%>
	
	if (check == 0) {
		alert("Attenzione: non è stato selezionato nessun candidato da avviare!");
	}
	else {
	
		var t="Confermi l'operazione?";
	
		<%
		if (checkDataAvv.compareTo(new BigDecimal(0)) > 0) {
    	%>
			t = "Attenzione: la data di pubbllicazione <%=dataPubb%> è anteriore a 6 mesi, vuoi procedere?";	
		<%
		}
		%>
   
		if ( confirm(t) ) {
		 	
		// modificare l'url per chiamare il modulo del salvataggio 				 
		//var s = "AdapterHTTP?PAGE=ASAvviaGraduatoriaPage";
	  	//s += "&MODULE=ASAvviaCandidatiGraduatoria";
	  	
	  		var s = "AdapterHTTP?PAGE=CMMatchDettGraduatoriaPage";
     		s += "&MODULE=CMCandidatiGraduatoria";
	  		s += "&PRGROSA=<%=prgRosa%>";  	  	 
	  		s += "&CDNFUNZIONE=<%=_cdnFunzione%>";  
	  		s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
	  		s += "&PRGAZIENDA=<%=prgAzienda%>";
	  		s += "&PRGUNITA=<%=prgUnita%>";
	    	s += "&PRGINCROCIO=<%=prgIncrocio%>";  
	  		s += "&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>";
	  		s += "&PRGTIPOROSA=<%=prgTipoRosa%>";	  	
	  		s += "&AVVIA_SELEZIONE=1";
	  		s += "&codCpi=<%=codCpi%>";
	  		s += "&ConcatenaCpi=<%=ConcatenaCpi%>";		  	
		
			document.frm1.action = s;
	   		    
			document.frm1.submit();
		}
    }
	  
  }
  
  function apriDialogStampa(){
    if (apriStampa) {
		apriGestioneDoc('RPT_AVVIA_GRADUATORIA','&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>&NUM_CANDIDATI=<%=numCandidati%>&codCpi=<%=codCpi%>&ConcatenaCpi=<%=ConcatenaCpi%>','ALL68O','', 'true');           
    }
  }
  
  
  function avviaSelezionePrimi() {
	
	if (isInSubmit()) return;
	
	
	var numCandidati = document.frm1.numCandidati.value;
	if (isNaN(numCandidati)) {
		alert("Attenzione: il numero di candidati non è valido!");
	}
	else if (numCandidati == "") {
		alert("Attenzione: non è stato inserito il numero di candidati da avviare!");
	}
	else if (numCandidati <= Number("0")) {
		alert("Attenzione: il numero di candidati da avviare deve essere maggiore di zero!");
	}
	else if (numCandidati > Number(<%=numMaxCandidati%>)) {
		alert("Attenzione: il massimo numero possibile di candidati da avviare è <%=numMaxCandidati%> ");
	}
	else {
	  
		var t="Confermi l'operazione?";
		<%
		if (checkDataAvv.compareTo(new BigDecimal(0)) >= 0) {
	    %>
			t = "Attenzione: la data di pubbllicazione <%=dataPubb%> è anteriore a 6 mesi, vuoi procedere?";	
		<%
		}
		%>
	    
		if ( confirm(t) ) {
			 	
			//var s = "AdapterHTTP?PAGE=ASAvviaPrimiGraduatoriaPage";
		  	//s += "&MODULE=ASAvviaPrimiCandidatiGraduatoria";		  	
		  	var s = "AdapterHTTP?PAGE=CMMatchDettGraduatoriaPage";
	    	s += "&MODULE=CMCandidatiGraduatoria";
		  	s += "&PRGROSA=<%=prgRosa%>";  	  	 
		  	s += "&CDNFUNZIONE=<%=_cdnFunzione%>";  
		  	s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
		  	s += "&PRGAZIENDA=<%=prgAzienda%>";
		  	s += "&PRGUNITA=<%=prgUnita%>";
		    s += "&PRGINCROCIO=<%=prgIncrocio%>";  
		  	s += "&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>";
		  	s += "&PRGTIPOROSA=<%=prgTipoRosa%>";		  
		  	s += "&AVVIA_SELEZIONE=1";			  		  	
		  	s += "&NUM_CANDIDATI="+numCandidati;
		  	s += "&codCpi=<%=codCpi%>";
		  	s += "&ConcatenaCpi=<%=ConcatenaCpi%>";	
			
			document.frm1.action = s;
		   	    
			document.frm1.submit();
	    }
  	}		  
  }  
  
  function setNominativoSB(ckb, cdnLavoratore) {
  	         	
   	if (!ckb.checked) {
   	    var cdnLav = ckb.value;   	    
   	   		
   		arrCandidati.push(cdnLav);
   		
   		var candidati = arrCandidati.join(";");
   		   		
   		document.frm1.ARRAY_CDNLAVORATORE.value = candidati;	
   	}
   	else {   	
   		var cdnLav = ckb.value; 
   		
   		for (i=0; i<arrCandidati.length;i++) {   		
   			if (arrCandidati[i] == cdnLav) {
   				arrCandidati.splice(i, 1);	   					   				
   			}   			
   		}
   		   
   		var candidati = arrCandidati.join(";");
   		
   		document.frm1.ARRAY_CDNLAVORATORE.value = candidati;
   	}
   	
						  
  }
  
  function Select(cdnLavoratore, cdnFunzione, apriEv) {
  	if (apriEv = 1) {
  		var s ="AdapterHTTP?PAGE=ListaEvidenzePage&SCAD=N&CDNLAVORATORE="+cdnLavoratore+"&CDNFUNZIONE="+cdnFunzione;
  		
  		window.open(s, "", 'toolbar=NO,statusbar=YES,height=500,width=800,scrollbars=YES,resizable=YES,left=150,top=100');		
  	}
  }
  
  function listaCancellati() {
	
	if (isInSubmit()) return;
	var tpRosa = <%=prgTipoRosa%>;	
	var s = "AdapterHTTP?PAGE=CMListaLavCancellatiPage";
	  	s += "&MODULE=CMListaLavCancellatiModule";
		if (tpRosa == "2") {
		  	s += "&grezza=true";  
		}else {
		  	s += "&grezza=false";  
		}
	  	s += "&CDNSTATORICH=<%=cdnStatoRich%>";  
	  	s += "&PRGROSA=<%=prgRosa%>";  
	  	s += "&PRGTIPOROSA=<%=prgTipoRosa%>"; 	 
	  	s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
	  	s += "&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>"; 
	  	s += "&PRGINCROCIO=<%=prgIncrocio%>"; 
	  	s += "&PRGAZIENDA=<%=prgAzienda%>";
 	  	s += "&PRGUNITA=<%=prgUnita%>";         	  	
	  	s += "&CDNFUNZIONE=<%=_cdnFunzione%>";   	
	  	s += "&codCpi=<%=codCpi%>";
	  	s += "&ConcatenaCpi=<%=ConcatenaCpi%>";  

	  	setWindowLocation(s);
	  
  }
  
  </script>
  <script language="Javascript">
    window.top.menu.caricaMenuAzienda(<%=_cdnFunzione%>,<%=prgAzienda%>, <%=prgUnita%>);
  </script>
</head>

<body class="gestione" onload="rinfresca(); getCandidatiCheck(); getCandidatiDisabled(); apriDialogStampa();">
<center>
</center>

<br>
  <%out.print(htmlStreamTopInfo);%>
  <p class="info_lav">
  
  Tipo di Graduatoria <b><%=tipoIncrocio%></b> - Stato della Graduatoria <b><%=tipoRosa%></b> <br> 
  <%
  if ("12".equalsIgnoreCase(prgTipoIncrocio)) {
  %>
  Tipo Lista <b> <%=descrtipoLista%></b>  -  
  <%
  }
  if(!prgAlternativa.equals("")) {
  %>
  Alternativa utilizzata <b><%=strAlternativa%></b> <br>
  <%
  }
  %> 
  <br> 
  <a class="info_lav" href="#" onClick="openRich_PopUP('<%=prgOrig%>')"><img src="../../img/info_soggetto.gif" alt="Dettaglio"/></a>&nbsp;
  Richiesta num. <b><%=numRichiestaOrig%>/<%=numAnno%></b>
  <br/>
 
  </p>
  <%out.print(htmlStreamBottomInfo);%>
  
<font color="red"><af:showErrors/></font>
<font color="green"> 
 <af:showMessages prefix="CMCancLavDaGraduatoria"/>  
 <af:showMessages prefix="CMDelFisicaLavDaGraduatoria"/>
 <af:showMessages prefix="CMSetDefinitivaGraduatoria"/>
 <af:showMessages prefix="CMCalcolaPunteggioModule"/>
 <af:showMessages prefix="CMAssociaLavoratoriArt1Module"/> 
</font>
<br>


<af:form name="frm1" action="" method="POST" dontValidate="true">
<%
if (prgTipoRosa.equals("3")) {
	if (prgRosaFiglia == null || ("").equals(prgRosaFiglia)) {
%>
	<br>
	<table width="96%" align="center" border="0" cellspacing="0" cellpadding="0">
	<tr valign="middle">
		<td class="azzurro_bianco" style="border: #cce4ff;">
			<input type="checkbox" name="SD_Candidati" onClick="selDeselCand();" />&nbsp;Seleziona/Deseleziona tutti &nbsp;			
		</td>			
		<td class="azzurro_bianco" style="border: #cce4ff;"> 
			<af:comboBox name="COMBO_ALTERNATIVA"
	                     size="1"
	                     title="Profilo"
	                     multiple="false"
	                     required="false"
	                     focusOn="false"
	                     moduleName="COMBO_ALTERNATIVA"	                     
	                     addBlank="true"
	                     blankValue=""
	                     selectedValue = "<%=prgRosa%>"					                     
	                     classNameBase="input"					                    
	        />			    
		</td>
		<td class="azzurro_bianco" style="border: #cce4ff;">
		    <input type="image" src="../../img/add.gif" border="0" value="Associa"  <%= (!cambioProfilo || disableChangeProf) ? "disabled='disable'" : "" %>  onclick="return cambiaProfilo();">
			Cambia profilo &nbsp;&nbsp;
		</td>		
		<%if (avviaPrimi) {%>	
			<td class="azzurro_bianco" style="border: #cce4ff;">
				&nbsp; 
				<input type="button" name="avviaSelPrimi" value="Avvia i primi ... " class="pulsante<%=((!disableAvviamento)?"":"Disabled")%>" <%= (disableAvviamento) ? "disabled='disable'" : "" %> onclick="return avviaSelezionePrimi();"> 			
				<af:textBox classNameBase="input" type="number" name="numCandidati" size="5" value="" maxlength="5" />
			</td>	
		<%} else {%>
			<td class="azzurro_bianco" style="border: #cce4ff;" width="35%">		
			</td>
		<%}%>
	</tr>
	</table>
<%
	}
}
%>

<%-- prova recupero array nominativi --%>
<input name="ARRAY_CDNLAVORATORE" type="hidden" value=""/>
<input name="daGraduatoria" type="hidden" value="daGraduatoria"/>		
		
<%
if ("12".equalsIgnoreCase(prgTipoIncrocio)) {
%>		
<af:list moduleName="CMCandidatiGraduatoria" configProviderClass="it.eng.sil.module.ido.CMCandidatiGraduatoriaConfig" jsDelete="Delete" jsSelect="Select"/>
<%
}
else {
%>
<af:list moduleName="CMCandidatiGraduatoria" configProviderClass="it.eng.sil.module.ido.CMCandidatiGraduatoriaConfig" jsDelete="Delete" />
<%
}
%>

</af:form>
	
	
<af:form name="frm" action="AdapterHTTP" method="GET" dontValidate="true" >

<input name="PAGE" type="hidden" value=""/>
<input name="MODULE" type="hidden" value=""/>
<input name="PRGRICHIESTAAZ" type="hidden" value="<%=prgRichiestaAz%>"/>
<input name="PRGALTERNATIVA" type="hidden" value="<%=prgAlternativa%>"/>
<input name="PRGORIG" type="hidden" value="<%=prgOrig%>"/>
<input name="PRGAZIENDA" type="hidden" value="<%=prgAzienda%>"/>
<input name="PRGUNITA" type="hidden" value="<%=prgUnita%>"/>
<input name="PRGROSA" type="hidden" value="<%=prgRosa%>"/>
<input name="NUMKLOROSA" type="hidden" value="<%=numKloRosa%>"/>
<input name="CERCA" type="hidden" value="cerca"/>
<input name="CDNFUNZIONE" type="hidden" value="<%=_cdnFunzione%>"/>
<input name="DAMATCH" type="hidden" value="1"/>
<input name="CPIROSE" type="hidden" value="<%=user.getCodRif()%>"/>
<input name="NUMORDPROVINCIA" type="hidden" value=""/>
<input name="C1" type="hidden" value="<%=prgCopia1%>"/>
<%-- parametri necessari per l'aggiunta di un lavoratore alla rosa nominativa --%>
<input name="v_CDNLAVORATORE" type="hidden" value="">
<input name="AGGIUNGI_LAV" type="hidden" value="false">
<input name="PRGSPICONTATTO" type="hidden" value="">
<input name="PRGTIPOCONTATTO" type="hidden" value="">
<input name="CDNSTATORICHORIG" type="hidden" value="<%=cdnStatoRich%>">
<%-- --%>
<input name="V_PRGNOMINATIVO" type="hidden" value=""/>
<input name="MASSIVA" type="hidden" value=""/>
<input name="PRGTIPOROSA" type="hidden" value="<%=prgTipoRosa%>"/>
<input name="codCpi" type="hidden" value="<%=codCpi%>"/>
<input name="ConcatenaCpi" type="hidden" value="<%=ConcatenaCpi%>"/>

<input name="LIST_PAGE" type="hidden" value="<%=listPageRosa%>" disabled />

<%
if((!prgTipoRosa.equals("3") && (!cdnStatoRich.equals("4") && !cdnStatoRich.equals("5")))
		|| (!prgTipoRosa.equals("3") && prgTipoIncrocio.equals("4") && (!cdnStatoRich.equals("5")))) 
{	
	if (aggiungiLavoratore) {
%>
<!--  aggiungi lavoratore
		<table class="main" align="center">
			<tr>
				<td>
			  		<input type="button" value="Aggiungi lavoratore" class="pulsanti" onclick="ricercaLavoratore()">
		  		</td>
			</tr>
		</table>
-->
	<%
	}
	%>			
<%
}
%>
</af:form>	
<%
// cdnStatoRich --> stato della richiesta 5 = chiusura totale
//   									  4 = chiusura  	

if((!prgTipoRosa.equals("3") && (!cdnStatoRich.equals("4") && !cdnStatoRich.equals("5")))
			|| (!prgTipoRosa.equals("3") && prgTipoIncrocio.equals("4") && (!cdnStatoRich.equals("5")))) 
{
%>	
		<table class="main" align="center" cellspacing="2" cellpadding="2">		
<%
		if ("12".equalsIgnoreCase(prgTipoIncrocio)) {
%>			
			<tr align="center">
				<td align="center">	
					<input type="button" name="aggLavoratore" value=" Aggiungi Lavoratori " class="pulsante" onclick="aggiungiLavoratori();">  			  	
				</td>
			</tr>
<%
		}  
		//
%>			
			<tr align="center">
				<td align="center">				
				<%
				if (rendiDef) {
				%>
			
					<input type="button" name="rendiDefinitiva" value=" Rendi definitiva " class="pulsante" onclick="rendiDefinitiva();">  			  	
				<%
				}
				if (numRecords>0) {
					if (posizione) {
				%>
						<input type="button" name="RicalcolaPosizione" value="Ricalcola posizione" class="pulsante" onclick="ricalcolaPosizione();">  			  	
				<%
					} 
				}
				%>
				<input type="button" name="listaCanc" value="Lista cancellati" class="pulsante" onclick="listaCancellati();">
				</td>  		  									 
			</tr>
			<%
			if (numRecords>0) {
				if (CMStampa) {
			%>			
			<tr align="center">	
				<td align="center">
					<input type="button" name="stampaGrezza" value="Stampa interna" class="pulsante" onClick="stampaInterna();">  
			  	</td> 
			</tr>
			<%
				}
			}
				
		//}
%>	
		</table>
<%	
}

// per le rose definitive il pulsante RIAPRI dipende dalla non presenza del prgRosaFiglia
if (prgTipoRosa.equals("3")) {
	if (prgRosaFiglia == null || ("").equals(prgRosaFiglia)) {
%>
		<table class="main" align="center">		
			<tr align="center">
				<td>
				<%			
				if (numRecords>0) {
					if (riapri) {				
				%>										
					
				
						<input type="button" name="riapriGrad" value=" Riapri grad. " class="pulsante" onclick="riapriGraduatoria();">  
				
								
				<%
					}
				}
				if (avviaSelez) {				
				%>
							

					<input type="button" name="avviaSelezione" value="Avvia a selezione" class="pulsante<%=((!disableAvviamento)?"":"Disabled")%>"  <%= (disableAvviamento) ? "disabled='disable'" : "" %> onclick="avviaSelezione();"> 				
			  	
				
				
			    <%
				}
				if (approvazione) {						
				%>						
			
					<input type="button" name="approvGrad" value="Approvazione" class="pulsante" onclick="approvazione();"> 				
					
				<%
				}
				%>
				<input type="button" name="listaCanc" value="Lista cancellati" class="pulsante" onclick="listaCancellati();"> 
				</td>
			</tr>	
			<%
			if (CMStampa) {
			%>			
				<tr align="center">	
				<td align="center">
				 <% if("3".equals(ConfigGraduatoria_cm) || "4".equals(ConfigGraduatoria_cm)){%> 
					<input type="button" name="stampaDef" value="Stampa" class="pulsante" onClick="stampaDefinitiva();">
				<%}else{ 
					if (codRegioneSil_cm.equals(PropertiesReport.COD_REGIONE_RER)) {%>
						<input type="button" name="stampaDefNom" value="Stampa nominativa" class="pulsante" onClick="stampaNominativa();">&nbsp;
						<input type="button" name="stampaDefAnonima" value="Stampa" class="pulsante" onClick="stampaAnonima();">
					<% } else {%>
 					<input type="button" name="stampaDefNom" value="Stampa" class="pulsante" onClick="stampa();">
 					<%}
 				}%>
				</td> 
				</tr>
			<%
			} 
			%>
		</table>	
	<%
	}	
}
%>
</body>
</html>
