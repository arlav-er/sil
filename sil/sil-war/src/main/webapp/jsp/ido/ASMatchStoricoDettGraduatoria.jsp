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

SourceBean rowsCandidati = (SourceBean) serviceResponse.getAttribute("ASStoricoCandidatiGraduatoria.ROWS");
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

	 
SourceBean infoRosa=(SourceBean)serviceResponse.getAttribute("ASDettaglioGraduatoria.row");
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

SourceBean sbStato = (SourceBean) serviceResponse.getAttribute("ASMatchStatoRichOrig.ROWS.ROW");
String cdnStatoRich = StringUtils.getAttributeStrNotNull(sbStato, "CDNSTATORICH");

	//Aste art 16 on line
	boolean flgaAsOnline = false;
	String dataIstanzaOnLine = null;

	boolean canASOnline = false;
	boolean scaricoIstanzeInCorso = false;
	boolean istanzeElaborate = false;
	String prgIstanza = null;
	SourceBean configAsOnline = (SourceBean) serviceResponse.getAttribute("M_Config_AsOnline.ROWS.ROW");
	if (configAsOnline != null) {
		int configOnline = Integer.parseInt(configAsOnline.getAttribute("NUMVALORECONFIG").toString());
		if (configOnline == 1) {
			canASOnline = true;
		}
	}

	if (canASOnline) {
		String flgTemp = StringUtils.getAttributeStrNotNull(infoRosa,"FLGASONLINE");
		if (flgTemp != null && StringUtils.isFilledNoBlank(flgTemp) && flgTemp.equalsIgnoreCase("S")) {
			flgaAsOnline = true;
			dataIstanzaOnLine = StringUtils.getAttributeStrNotNull( infoRosa, "strDatAsOnline");
		}
	}

	String queryString = "cdnFunzione=" + _cdnFunzione + "&PAGE="
			+ _page + "&prgRosa=" + prgRosa + "&prgAzienda="
			+ prgAzienda + "&prgUnita=" + prgUnita + "&PRGRICHIESTAAZ="
			+ prgRichiestaAz + "&CPIROSE=" + user.getCodRif();
%>

<%
PageAttribs attributiMatch = new PageAttribs(user, "ASStoricoDettGraduatoriaPage");
boolean gestCopia = true; //attributiMatch.containsButton("GEST_COPIA");
if(!gestCopia) {
  // L'utente non è abilitato alla gestione della copia di lavoro, ma deve utilizzare solo 
  // la richiesta originale (numStorico=0)
  prgCopia1 = prgOrig;
}
PageAttribs attributi = new PageAttribs(user, "ASStoricoDettGraduatoriaPage");
boolean riapri = attributi.containsButton("RIAPRI");

%>

<%
String token = "";
String urlDiLista = "";
String refListaBtn = "";
if (sessionContainer!=null){
//  token = "_TOKEN_" + "IDOLISTARICHIESTEPAGE";
  token = "_TOKEN_" + "ASStoricoGraduatoriePage";  
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
String codMonoTipoGradCm = StringUtils.getAttributeStrNotNull(serviceRequest, "CODMONOTIPOGRADCM");
%>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
    <style type="text/css"> 
.pulsanteInfoLav {
   border: #dddddd 1px outset;
   font-weight: bold;
   font-size: 10px;
   color: #ffffff;
   font-style: normal;
   font-family: Arial, Helvetica, sans-serif;
   background-color: #999999;
	position: relative;
	
	left: 2%;
	text-align: left;
	margin: 0;
}
.pulsanteInfoLavDisabled {
   border: #ffffff 1px solid;
   font-weight: bold;
   font-size: 11px;
   color: #ffffff;
   font-style: normal;
   font-family: Arial, Helvetica, sans-serif;
   background-color: #E2E2E2;
   	position: relative;
	
	left: 2%;
	text-align: left;
	margin: 0;
}
</style>
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
	  	s += "&OLD_PAGE=ASStoricoDettGraduatoriaPage";
		
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
		if (page=="PAGE=ASStoricoDettGraduatoriaPage") { 			
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

		var s = "AdapterHTTP?PAGE=ASGestRiapriGraduatoriePage";
	  	s += "&MODULE=ASGestRiapriGraduatorieModule";
	  	s += "&PRGROSA=<%=prgRosa%>";   
	  	s += "&CDNFUNZIONE=<%=_cdnFunzione%>";  
	  	s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
	  	s += "&PRGAZIENDA=<%=prgAzienda%>";
 	  	s += "&PRGUNITA=<%=prgUnita%>";
        s += "&PRGINCROCIO=<%=prgIncrocio%>";  
	  	s += "&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>"; 
	  	s += "&PRGTIPOROSA=<%=prgTipoRosa%>"; 
	  	s += "&OLD_PAGE=ASStoricoDettGraduatoriaPage";

	  	setWindowLocation(s);

  }
   
  function approvazione() {
	
	if (isInSubmit()) return;
	  
		var s = "AdapterHTTP?PAGE=ASGestApprovaGraduatoriePage";
	  	s += "&MODULE=ASGestApprovaGraduatorieModule";
	  	s += "&PRGROSA=<%=prgRosa%>";   
	  	s += "&CDNFUNZIONE=<%=_cdnFunzione%>";  
	  	s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
	  	s += "&PRGAZIENDA=<%=prgAzienda%>";
 	  	s += "&PRGUNITA=<%=prgUnita%>";
        s += "&PRGINCROCIO=<%=prgIncrocio%>";  
	  	s += "&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>"; 
	  	s += "&PRGTIPOROSA=<%=prgTipoRosa%>";  
	  	s += "&OLD_PAGE=ASStoricoDettGraduatoriaPage";

	  	setWindowLocation(s);
	  
  } 
  function visualizzaRisultatiIstanze(){
		var s = "AdapterHTTP?PAGE=ASScaricoIstanzePage";
	  	s += "&PRGROSA=<%=prgRosa%>";   
	  	s += "&CDNSTATORICH=<%=cdnStatoRich%>";   
	  	s += "&PRGTIPOROSA=<%=prgTipoRosa%>";	
	  	s += "&CDNFUNZIONE=<%=_cdnFunzione%>";  
	  	s += "&PRGRICHIESTAAZ=<%=prgRichiestaAz%>";
	  	s += "&PRGAZIENDA=<%=prgAzienda%>";
	  	s += "&PRGUNITA=<%=prgUnita%>";
        s += "&PRGINCROCIO=<%=prgIncrocio%>";  
	  	s += "&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>";
	  	s += "&codCpi=";
	  	s += "&ConcatenaCpi="; 
	  	s += "&numrichiesta=<%=numRichiestaOrig%>";
	  	s += "&numanno=<%=numAnno%>"; 
		s += "&TIPO=VISUALIZZA"; 
		s += "&PAGE_STORICO=<%= _page%>"; 
		s += "&MODULE_STORICO=ASStoricoCandidatiGraduatoria";
		s += "&codmonotipogradcm=<%=codMonoTipoGradCm%>";

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
  <%if(canASOnline && flgaAsOnline){ %>
	  <br> 
	  <b>Graduatorie con istanze presentate online su sistema esterno</b>	
  
  	<%if(StringUtils.isFilledNoBlank(dataIstanzaOnLine)){ %>
	  		<br>Ultimo aggiornamento istanze <b><%= dataIstanzaOnLine %></b>	
  	<%  }  %> 

  		<br><af:form name="frm2" action="" method="POST" dontValidate="true">
 			<input type="button" name="visualizzaRisultatiAsOnline" 
  			class="pulsanteInfoLav"   			
  			 value="Visualizza risultati" onclick="visualizzaRisultatiIstanze();" /> 
		</af:form>
  	<%  }  %> 
  </p>
  <%out.print(htmlStreamBottomInfo);%>
  
<font color="red"><af:showErrors/></font>
<br>

<af:form name="frm1" action="" method="POST" dontValidate="true">
		
<af:list moduleName="ASStoricoCandidatiGraduatoria" configProviderClass="it.eng.sil.module.ido.ASStoricoCandidatiGraduatoriaConfig"/>

</af:form>

<table class="main" align="center">			
	<tr>
		<td align="right">
			<input type="button" name="riapriGrad" value=" Motivo riapertura " class="pulsante" onclick="riapriGraduatoria();">  
	  	</td>  
	  	<td>&nbsp;</td>
		<td align="left">
			<input type="button" name="approvGrad" value="  Approvazione  " class="pulsante" onclick="approvazione();"> 				
	  	</td>  
	</tr>
</table>
</body>
</html>
