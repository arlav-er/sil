<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="com.engiweb.framework.base.*, 
                com.engiweb.framework.configuration.ConfigSingleton,
                java.text.*, java.util.*,it.eng.sil.util.*,                 
                
                it.eng.sil.Values,
                it.eng.afExt.utils.*,
                java.math.*, 
                it.eng.sil.security.* "%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%!  
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger("it.eng.sil._jsp.CaricaDatiPersonali.jsp");
%>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
    int _funzione=0;

  //flag booleano che discrimina l'inserimento o la modifica
  
  //inizializzo i campi
  String cdnLavoratore="";
  String codiceFiscale="";
  String cognome="";
  String nome="";
  String strSesso="";
  String dataNascita="";
  String comuneNascita="";
  
  String codComNas="";
  String strComNas="";
  String codCittadinanza="";
  String strCittadinanza="";

  InfCorrentiLav infCorrentiLav= null;
  Testata operatoreInfo = null;
  String codCpi = "";
  String inserimentoLav="";
  String cpicomp= "";

	String tipoErrore = "";
	String msgErrore="";

  //////////
  boolean erroreIR            = false;
  boolean erroreCOOP          = false;
  boolean lavoratoreNonSuIR   = false;
  
	_funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
    //se non sto gestendo l'inserimento
    //devo prelevare la risposta che mi è pervenuta dalla get
  
	codiceFiscale  =(String) serviceRequest.getAttribute("strCodiceFiscale");
	cognome        =(String) serviceRequest.getAttribute("strCognome");
	nome           =(String) serviceRequest.getAttribute("strNome");
	dataNascita    =(String) serviceRequest.getAttribute("datNasc");
	codComNas=(String) serviceRequest.getAttribute("codComNas");
	strComNas=(String) serviceRequest.getAttribute("strComNas");
	_logger.debug(  "strCodiceFiscale: "+  codiceFiscale + "\n" +
						"strCognome: " + cognome + "\n"+
						 "strNome: " + nome + "\n" + 	
						 "DataNas: " + dataNascita + "\n" +
						 "codComNas: " + codComNas + "\n" +
						 "strComNas: " + strComNas + "\n");

	String _page = (String) serviceRequest.getAttribute("PAGE"); 
	// uso la profilatura di CoopAnagDatiPersonaliPage
	ProfileDataFilter filter = new ProfileDataFilter(user, "CoopAnagDatiPersonaliPage");
    PageAttribs attributi = new PageAttribs(user, "CoopAnagDatiPersonaliPage");

  
	boolean canView=filter.canView();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}
	SourceBean mod = null;
	SourceBean infoLavoratoreIR  = (SourceBean)serviceResponse.getAttribute("M_COOP_GETLAVORATOREIR");
	SourceBean datiPersonaliCoop = (SourceBean)serviceResponse.getAttribute("M_COOP_GetDatiPersonali");
	// se si arriva a questa pagina passando per la lista lavoratori, il modulo M_COOP_GETLAVORATOREIR non viene chiamato
	if (infoLavoratoreIR!=null && !Utils.notNull(infoLavoratoreIR.getAttribute("IS_OK")).equals("TRUE")) {
		// e' stato chiamato il modulo di richiesta info sul lavoratore all'indice regionale (si proviene dalla 
		// pagina della scheda lavoratore.
		// NON SI DEVE PASSARE ALLA PAGINA DELLA SCHEDA LAVORATORE MA MOSTRARE L'ERRORE.
		erroreIR = true;
		if (Utils.notNull(infoLavoratoreIR.getAttribute("NESSUN_RISULTATO")).equals("Y"))
			msgErrore = "Il lavoratore non e' presente sull'Indice regionale. Impossibile proseguire.";
		else
			msgErrore = "Si e' verificato un errore nel caricamento dei dati del lavoratore dall'Indice Regionale. Impossibile proseguire.";
	}
	else {
		if (datiPersonaliCoop==null) {
			_logger.fatal( "errore grave nella pagina di caricamento dei dati della scheda lavoratore dal polo remoto in coop. Il modulo di chiamata non si trova nella response");
			erroreCOOP = true;
			tipoErrore = "SCONOSCIUTO";
		}
		else {
			if(datiPersonaliCoop.getAttribute("ERRORE_ID")!=null) {
				tipoErrore = (String)datiPersonaliCoop.getAttribute("ERRORE_ID");
				erroreCOOP = true;
			}
		}
		if (erroreCOOP) {
			if (tipoErrore.equals("SCONOSCIUTO")||tipoErrore.equals("GENERICO")) {
				msgErrore = "<p>Si e' verificato un errore. <br>Impossibile proseguire.</p>";
			}
			if (tipoErrore.equals("DATI")) {
				msgErrore = "<p>Si e' verificato un errore nel caricamento dei dati della scheda lavoratore dal polo remoto. <br>I dati ricevuti non sono coerenti.<br>Impossibile proseguire.</p>";
			}
			if (tipoErrore.equals("CHIAMATA")) {
				msgErrore = "<p>Si e' verificato un errore nella chiamata del servizio web della scheda lavoratore. <br>Il polo remoto non e' raggiungibile.<br>Impossibile proseguire.</p>";
			}
			if (tipoErrore.equals("ESECUZIONE_REMOTA")) { // TODO Savino: il messaggio e' troncato (rilascio 1.6.5)
				msgErrore = "<p>Si e' verificato un errore nel caricamento dei dati della scheda lavoratore dal polo remoto.<br>Impossibile proseguire.</p>";
			}
			if (tipoErrore.equals("LAVORATORE_NON_TROVATO")) {
				msgErrore = "<p>Il lavoratore non e' presente nel polo remoto.<br>Impossibile proseguire.</p>";
			}
			if (tipoErrore.equals("ERRORE_VERSIONING")) {
				msgErrore = "<p>Il servizio richiesto è temporaneamente sospeso fino a riallineamento delle versioni tra i due poli coinvolti.<br>Impossibile proseguire.</p>";
			}			
			if (tipoErrore.equals("POLO_LOCALE_COMPETENTE")) {
				msgErrore = "<p>Il polo che ha richiesto i dati e' il cpi master per il lavoratore.<br>Impossibile proseguire.</p>";
			}
		}		
	}
	if (!erroreIR && infoLavoratoreIR!=null ) {
		// Il modulo M_COOP_GETLAVORATOREIR e' stato chiamato e non ha prodotto errori: si prendono i dati del lavoratora presenti sull'indice
		mod = (SourceBean)infoLavoratoreIR.getAttribute("ROWS.ROW");
	}
	else // recupero i dati del lavoratore dalla request (presenti solo se si proviene dalla scheda lavoratore)
		mod = serviceRequest;
	
	// se Indice OK i dati del lavoratore li prendo dall'indice regionale
	codiceFiscale = Utils.notNull(mod.getAttribute("strCodiceFiscale"));
	cognome       = Utils.notNull(mod.getAttribute("strCognome"));
	nome          = Utils.notNull(mod.getAttribute("strNome"));
	dataNascita   = Utils.notNull(mod.getAttribute("dataNascita"));
	comuneNascita = Utils.notNull(mod.getAttribute("comNas"));
	
%>


<html>
<head>
<title>Dati personali scheda lavoratore polo remoto</title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>


<SCRIPT TYPE="text/javascript">

function caricaSchedaLavoratore() {
	document.Frm1.submit();
}
 

</SCRIPT>
</head>
<%-- SE NON CI SONO STATI ERRORI VIENE CARICATA LA SCHEDA LAVORATORE CON UN AUTOSUBMIT --%>
<body class="gestione" onload="<%=(erroreIR || erroreCOOP) ? "":"caricaSchedaLavoratore()"%>">
<%-- 1. PROBLEMA CON L'INDICE REGIONALE --%>
<% 	if (erroreIR) {
		out.print(StyleUtils.roundTopTable(true));
%>
	<p class="info_lav">
		<%--Lavoratore&nbsp;cf:&nbsp;<strong><%=codiceFiscale%></strong>--%>
		<br>
	</p>
<%= msgErrore %>  
<br>
	<input type="button" value="Chiudi" class="pulsanti" onclick="window.close()">
			
<%		out.print(StyleUtils.roundBottomTable(true));
	}
%>
<%-- 2. PROBLEMA CON IL POLO PROVINCIALE --%>
<% 	if (!erroreIR && erroreCOOP) {
		out.print(StyleUtils.roundTopTable(true));
%>
	<p class="info_lav">
		Lavoratore&nbsp;<strong><%=cognome%>&nbsp;<%=nome%></strong>
		codice&nbsp;fiscale&nbsp;<strong><%=codiceFiscale%></strong>
		data&nbsp;di&nbsp;nascita&nbsp;<strong><%=dataNascita%></strong>
		comune&nbsp;di&nbsp;nascita&nbsp;<strong><%=comuneNascita%></strong>
		<br>
	</p>
<%= msgErrore %>  
<br>
	<input type="button" value="Chiudi" class="pulsanti" onclick="window.close()">
			
<%		out.print(StyleUtils.roundBottomTable(true));
	}
%>


<%if (erroreIR ||erroreCOOP) {%>
<font color="red">
     <af:showErrors/>
</font>
<%}%>
<%-- SE NON CI SONO STATI ERRORI VIENE CARICATA LA SCHEDA LAVORATORE CON UN AUTOSUBMIT --%>
<% if (!erroreIR && !erroreCOOP) { %>
<af:form method="POST" action="AdapterHTTP" name="Frm1" >

	<input type="hidden" name="PAGE" value="CoopAnagDatiPersonaliPage"/>
	<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
	<input type="hidden" name="strCodiceFiscale" value="<%=codiceFiscale%>"/>
</af:form>
<%}%>
</body>
</html>
