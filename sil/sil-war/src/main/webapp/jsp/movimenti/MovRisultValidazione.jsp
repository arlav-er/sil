<!-- @author: Paolo Roccetti - Gennaio 2004 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
                  
                  com.engiweb.framework.util.*,
                  it.eng.sil.module.movimenti.*,
                  it.eng.sil.security.*,
                  it.eng.sil.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%!  
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger("it.eng.sil._jsp.MovRisultValidazione.jsp");
%>

<%
  // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, (String) serviceRequest.getAttribute("PAGE"));

  String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
  String prgValidazioneMassiva = null;
  String numMovimentiValidati = null; 
  String ritornaPrgRisultato = "";

  //Risultati della validazione
  SourceBean result = (SourceBean) serviceResponse.getAttribute("M_MOVSELEZIONAPAGINARISULTATI.RECORDS");
  prgValidazioneMassiva = result.getAttribute("PRGVALIDAZIONEMASSIVA").toString();
  numMovimentiValidati  = result.getAttribute("CORRECTPROCESSED").toString();
  if (serviceRequest.containsAttribute("RITORNAPRGRISULTATO")) {
     ritornaPrgRisultato   = serviceRequest.getAttribute("RITORNAPRGRISULTATO").toString();
  }
  String elemScriptAlert = GraficaUtils.showAlert(result);
  String elemScriptConfirm = GraficaUtils.showConfirm(result);  
%>
<%@ include file="GestioneValidazioneMassiva.inc" %>

<html>
  <head>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>
	<%-- include lo script che permette di aprire la PopUp che gestisce i documenti (salvataggio/protocollazione) --%>
	<% String queryString = ""; %>
	<%@ include file="../documenti/_apriGestioneDoc.inc"%>
    <title>Risultati della Validazione</title>
    <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
	//funzione di get per il ritorno alla ricerca
	function altraRicerca() {
		var get = "AdapterHTTP?PAGE=MovRicercaValidazionePage&cdnFunzione=<%=_funzione%>"
		setWindowLocation(get);		
	}
	
	//funzione di get per l'arresto della validazione massiva
	function arrestaValidazione() {
		if (confirm('Vuoi arrestare la validazione massiva corrente?')) {
			var get = "AdapterHTTP?PAGE=MovValidazioneMassivaPage&cdnFunzione=<%=_funzione%>&AZIONE=arresta";
			setWindowLocation(get);
		}
	}
	
	//funzione di get per l'aggironamento dei dati della validazione massiva
	function getPage(page) {
		var get = "AdapterHTTP?PAGE=MovRisultValidazionePage&cdnFunzione=<%=_funzione%>&PAGERISULTVALMASSIVA=" + page + 
		          "&RITORNAPRGRISULTATO=<%=ritornaPrgRisultato%>";
		setWindowLocation(get);
	}
	//funzione di stampa dei movimenti validati
	function stampaMov() {
        <% if (numMovimentiValidati.equalsIgnoreCase("0")){ %>
        	alert ("Non ci sono movimenti validati");
        	return false;
		<% } else { %>
			apriGestioneDoc('RPT_MOVIMENTI','&TIPOSTAMPA=VALIDATI&PRGVALIDAZIONEMASSIVA=<%=prgValidazioneMassiva%>','STMOV');
		<% } %>
	} 
    
    
    <%-- Faccio il preload delle immagini che verranno usate nella lista per dare un'evidenza cromatica aol risultato --%>
	if (document.images) 
	{
	   luceVerde  = new Image();
	   luceGialla = new Image();
	   luceRossa  = new Image();
	   luceVerde.src  = "../../img/luceVerde.gif";
	   luceGialla.src = "../../img/luceGialla.gif";
	   luceRossa.src  = "../../img/luceRossa.gif";
	}
    </SCRIPT>
    <%@ include file="MovimentiSezioniATendina.inc" %>    

  <STYLE>
	  li.luceVerde  { list-style-image: url("../../img/luceVerde.gif"); list-style-type: none; clear: both;}
	  li.luceGialla { list-style-image: url("../../img/luceGialla.gif"); list-style-type: none; clear: both;}
	  li.luceRossa  { list-style-image: url("../../img/luceRossa.gif"); list-style-type: none; clear: both;}
  </STYLE>
  </head>

  <body class="gestione" onload="rinfresca();">
    <br/>
    <p class="titolo">Risultati della Validazione Massiva</p>
    <br/>
    <center>
    	<p align="center" class="titolo" style="color: purple;">
    		Questa pagina mostra i risultati dell'ultima validazione massiva eseguita dall'utente.<br/>
    		Se essa è ancora in atto è possibile aggiornare i risultati ottenuti cliccando sull'icona 
    		<img src="../../img/add_trasp.gif" alt="Aggiorna risultati"/> che si trova in mezzo ai pulsanti di navigazione dei risultati.<br>
    		Se la validazione è ancora in atto è anche possibile arrestarla premendo il pulsante con etichetta "Arresta validazione" posizionato in fondo alla pagina.</p>
	  	<%try { 
	  		out.print(GraficaUtils.showInfoGlobali(result));
	  		out.print(GraficaUtils.showPulsantiNavigazione(result));
	  		out.print(GraficaUtils.showRisultati(result, true, null, ritornaPrgRisultato, true));
	  		//out.print(GraficaUtils.showRisultati(result, true, null)); 
	  		%>
	  <%} catch (Exception e) {
		  _logger.fatal( 
	  			"Eccezione nella formattazione dei risultati della validazione", e);
	    	out.print("<p class='titolo'>Impossibile visualizzare il risultato della validazione.</p>");
	  	}%>
      	<br>
		<af:form>
			<div>
			<%if(result.getAttributeAsVector("RECORD").size() != 0){
				try { 
			  		out.print(GraficaUtils.showPulsantiNavigazione(result));
			  		} catch (Exception e) {
			  			_logger.fatal( 
			  			"Eccezione nella formattazione dei risultati della validazione", e);
			    	out.print("<p class='titolo'>Impossibile visualizzare il risultato della validazione.</p>");
		  		}
		  	}%>
	  		</div>
	  	
	  		<%if (validazioneInCorso) {%>
	  		<input class="pulsanti" type="button" onclick="arrestaValidazione();" name="reset" value="Arresta Validazione" title="Ferma la validazione massiva attualmente in esecuzione"/>        
	  		<%}%>           
      		&nbsp;&nbsp;
	  		<input class="pulsanti" type="button" onclick="stampaMov();" name="stampa" value="Stampa movimenti validati massivamente" title="Stampa movimenti validati"/>        
      		<input class="pulsanti" type="button" onclick="altraRicerca();" name="cerca" value="Altra ricerca"/>
      		<br/><br/>
		</af:form>
    </center>    
  </body>
  <%@ include file="common/include/GestioneScriptRisultati.inc" %>
</html>
