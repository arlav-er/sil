<!-- @author: Stefania Orioli -->
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
String _page = (String)serviceRequest.getAttribute("PAGE");
String prgRichiestaAz = serviceRequest.getAttribute("PRGRICHIESTAAZ").toString();
String prgAzienda = serviceRequest.getAttribute("PRGAZIENDA").toString();
String prgUnita = serviceRequest.getAttribute("PRGUNITA").toString();
String _cdnFunzione = serviceRequest.getAttribute("CDNFUNZIONE").toString();
    
String prgRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGROSA");
String codiceRit = "";
codiceRit = "0"; // per tutte le altre operazioni considero il codice di errore 0

SourceBean rowsCandidati = (SourceBean) serviceResponse.getAttribute("CMStoricoCandidatiGraduatoria.ROWS");
int numRecords = 0;
if(rowsCandidati != null)  
	numRecords = Integer.parseInt(rowsCandidati.getAttribute("NUM_RECORDS").toString()); 
// inizializzazione delle variabili usate nella testata

String numRichiesta="", numAnno="",prgTipoRosa="", prgTipoIncrocio="",tipoIncrocio="",prgIncrocio="", tipoRosa="",prgAlternativa="", 
	 strAlternativa="",prgOrig="", prgCopia1="",numKloRosa="",utMod="", ultimaModifica="", numRichiestaOrig="",
	 utAttivo="" , ordinamento="";
String prgRosaFiglia = "";	 
String queryStringBack="";	 
String htmlStreamBottom="", htmlStreamTop = "";  

	 
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

String queryString = "cdnFunzione="+_cdnFunzione+"&PAGE="+_page+"&prgRosa="+prgRosa+
	"&prgAzienda="+prgAzienda+"&prgUnita="+prgUnita+"&PRGRICHIESTAAZ="+prgRichiestaAz+"&CPIROSE="+user.getCodRif();


%>

<%
PageAttribs attributiMatch = new PageAttribs(user, "CMStoricoDettGraduatoriaPage");
boolean gestCopia = true; //attributiMatch.containsButton("GEST_COPIA");
if(!gestCopia) {
  // L'utente non è abilitato alla gestione della copia di lavoro, ma deve utilizzare solo 
  // la richiesta originale (numStorico=0)
  prgCopia1 = prgOrig;
}
PageAttribs attributi = new PageAttribs(user, "CMStoricoDettGraduatoriaPage");
boolean riapri = attributi.containsButton("RIAPRI");

%>

<%
String token = "";
String urlDiLista = "";
String refListaBtn = "";
if (sessionContainer!=null){
//  token = "_TOKEN_" + "IDOLISTARICHIESTEPAGE";
  token = "_TOKEN_" + "CMStoricoGraduatoriePage";  
  urlDiLista = (String) sessionContainer.getAttribute(token.toUpperCase());
  if (urlDiLista!=null && !urlDiLista.equals("")) {
    refListaBtn ="<a href=\"AdapterHTTP?" + urlDiLista + "\"><img src=\"../../img/rit_lista.gif\" border=\"0\"></a>";
  } else {
  	urlDiLista = "";
  }
}
String htmlStreamTopInfo = StyleUtils.roundTopTableInfoRetLista(urlDiLista);
String htmlStreamBottomInfo = StyleUtils.roundBottomTableInfoNobr();

String messRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE");
String listPageRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE");

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
  
  var arrCandidati = new Array();	   	
      
	function goConfirmGenericCustomTL(url, alertFlag) {   
				
		var urlArray = new Array();
		var message;
		var page;
		var module;
		var prgNominativo;
		var cdnLavoratore;
		var list_page;
		var old_list_page;
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
	  	s += "&OLD_PAGE=CMStoricoDettGraduatoriaPage";
		
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
		if (page=="PAGE=CMStoricoDettGraduatoriaPage") { 			
			if (list_page != null) {
				s += "&"+list_page;
			}		
		}
		else {
			s += "&"+old_list_page;
		}
		
				
		document.frm1.action = s;
	   	    
		document.frm1.submit();

	}
  

	function rinfresca(){
		try{
			window.top.footer.rinfresca(); 
		} catch(e) {} 
	}
  
  
  function openRich_PopUP(prgRich) {
     //window.open ("AdapterHTTP?PAGE=IdoDettaglioSinteticoPage&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&CDNFUNZIONE=<%=_cdnFunzione%>&POPUP=1", "DettaglioSintetico", 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES,left=150,top=100');
     window.open ("AdapterHTTP?PAGE=IdoDettaglioSinteticoPage&PRGRICHIESTAAZ=" +prgRich +"&CDNFUNZIONE=<%=_cdnFunzione%>&POPUP=1", "DettaglioSintetico", 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES');
  }
  
  function riapriGraduatoria() {
	
	if (isInSubmit()) return;	  

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
	  	s += "&OLD_PAGE=CMStoricoDettGraduatoriaPage";

	  	setWindowLocation(s);

  }
   
  function approvazione() {
	
	if (isInSubmit()) return;
	  
		var s = "AdapterHTTP?PAGE=CMGestApprovaGraduatoriePage";
	  	s += "&MODULE=CMGestApprovaGraduatorieModule";
	  	s += "&PRGROSA=<%=prgRosa%>";   
	  	s += "&CDNFUNZIONE=<%=_cdnFunzione%>";  
	  	s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
	  	s += "&PRGAZIENDA=<%=prgAzienda%>";
 	  	s += "&PRGUNITA=<%=prgUnita%>";
        s += "&PRGINCROCIO=<%=prgIncrocio%>";  
	  	s += "&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>"; 
	  	s += "&PRGTIPOROSA=<%=prgTipoRosa%>";  
	  	s += "&OLD_PAGE=CMStoricoDettGraduatoriaPage";

	  	setWindowLocation(s);
	  
  } 
  
  function listaCancellati() {
	
	if (isInSubmit()) return;	  	

		var s = "AdapterHTTP?PAGE=CMListaLavCancellatiPage";
	  	s += "&MODULE=CMListaLavCancellatiModule";
	  	s += "&CDNSTATORICH=<%=cdnStatoRich%>";  
	  	s += "&PRGROSA=<%=prgRosa%>";  
	  	s += "&PRGTIPOROSA=<%=prgTipoRosa%>"; 	 
	  	s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
	  	s += "&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>"; 
	  	s += "&PRGINCROCIO=<%=prgIncrocio%>"; 
	  	s += "&PRGAZIENDA=<%=prgAzienda%>";
 	  	s += "&PRGUNITA=<%=prgUnita%>";         	  	
	  	s += "&CDNFUNZIONE=<%=_cdnFunzione%>";   		  	
	  	s += "&OLD_PAGE=CMStoricoDettGraduatoriaPage";

	  	setWindowLocation(s);
	  
  }
  
  </script>
  <script language="Javascript">
    window.top.menu.caricaMenuAzienda(<%=_cdnFunzione%>,<%=prgAzienda%>, <%=prgUnita%>);
  </script>
</head>

<body class="gestione" onload="rinfresca();">
<center>
</center>

<br>
  <%out.print(htmlStreamTopInfo);%>
  <p class="info_lav">
  
  Tipo di Graduatoria <b><%=tipoIncrocio%></b> - Stato della Graduatoria <b><%=tipoRosa%></b> <br>  
  <%
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
<br>

<af:form name="frm1" action="" method="POST" dontValidate="true">
		
<af:list moduleName="CMStoricoCandidatiGraduatoria" configProviderClass="it.eng.sil.module.ido.CMStoricoCandidatiGraduatoriaConfig"/>

</af:form>

<table class="main" align="center">			
	<tr>
		<td align="center">
			<input type="button" name="riapriGrad" value=" Motivo riapertura " class="pulsante" onclick="riapriGraduatoria();">  
		  	&nbsp;&nbsp;
			<input type="button" name="approvGrad" value="  Approvazione  " class="pulsante" onclick="approvazione();"> 				
		  	&nbsp;&nbsp;
	  		<input type="button" name="listaCanc" value="Lista cancellati" class="pulsante" onclick="listaCancellati();"> 
	  	</td>
	</tr>
</table>
</body>
</html>
