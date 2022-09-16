<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%!  
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger("it.eng.sil._jsp.risultOperazioneCopiaProspetti.jsp");
%>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
                  com.engiweb.framework.util.*,
                  it.eng.sil.module.movimenti.*,
                  it.eng.sil.security.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>
    
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%@ taglib uri="aftags" prefix="af" %>

<%
  // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, (String) serviceRequest.getAttribute("PAGE"));
  String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
  //Oggetti per l'applicazione dello stile grafico
  String azione = serviceRequest.containsAttribute("azioneScelta")?serviceRequest.getAttribute("azioneScelta").toString():"";
  String prgValidazioneMassiva = null;
  String numProspSCopiati = null;
  String numProspACopiati = null;
  String ritornaPrgRisultato = "";
  SourceBean result = null;
  //Risultati della validazione
  result = (SourceBean) serviceResponse.getAttribute("M_Prosp_SelezionaRisultatiCopia.RECORDS");
  prgValidazioneMassiva = result.getAttribute("PRGVALIDAZIONEMASSIVA").toString();
  numProspSCopiati  = result.getAttribute("CORRECTPROCESSEDSC").toString();
  numProspACopiati  = result.getAttribute("CORRECTPROCESSEDAC").toString();
  
  if (serviceRequest.containsAttribute("RITORNAPRGRISULTATO")) {
   	ritornaPrgRisultato   = serviceRequest.getAttribute("RITORNAPRGRISULTATO").toString();
  }
  %>
  <html>
  
  	<head>
  	<link rel="stylesheet" href="../../css/stili.css" type="text/css">
  	<af:linkScript path="../../js/"/>
  	<title>Risultati storicizzazione e copia</title>
  	<script language="Javascript">
  	<% 
  		//Genera il Javascript che si occuperà di inserire i links nel footer
  		attributi.showHyperLinks(out, requestContainer,responseContainer,"");
  	%>
	//funzione di get per il ritorno alla ricerca
  	function altraRicerca() {
  		var get = "AdapterHTTP?PAGE=CopiaProspRicercaPage&cdnFunzione=<%=_funzione%>"
		setWindowLocation(get);		
  	}
  
  	<%-- Carico le immagini che verranno usate nella lista per dare un'evidenza cromatica al risultato --%>
  	if (document.images) {
    	luceVerde  = new Image();
    	luceGialla = new Image();
    	luceRossa  = new Image();
    	luceVerde.src  = "../../img/luceVerde.gif";
    	luceGialla.src = "../../img/luceGialla.gif";
    	luceRossa.src  = "../../img/luceRossa.gif";
 	}
  
  	//funzione di get per l'aggiornamento dei dati della validazione massiva
	function getPage(page) {
		var get = "AdapterHTTP?PAGE=CopiaProspVisualizzaRisultPage&cdnFunzione=<%=_funzione%>&PAGERISULTVALMASSIVA=" + page + 
		          "&RITORNAPRGRISULTATO=<%=ritornaPrgRisultato%>";
		setWindowLocation(get);
	}
  
  	</script>
  	<script type="text/javascript" src="../../js/movimenti/common/MovimentiSezioniATendina.js" language="JavaScript"></script>
  	<STYLE>
	  li.luceVerde  { list-style-image: url("../../img/luceVerde.gif"); list-style-type: none; clear: both;}
	  li.luceGialla { list-style-image: url("../../img/luceGialla.gif"); list-style-type: none; clear: both;}
	  li.luceRossa  { list-style-image: url("../../img/luceRossa.gif"); list-style-type: none; clear: both;}
  	</STYLE>
	</head>

	<body class="gestione" onload="rinfresca();">
    <br/>
    <p class="titolo">Risultati storicizzazione e copia</p>
    <br/>
    <center>
    	<p align="center" class="titolo" style="color: purple;">
    		Questa pagina mostra i risultati dell&#39;ultima operazione di generazione copie in corso d&#39;anno eseguita dall'utente.<br/>
    		Se essa è ancora in atto è possibile aggiornare i risultati ottenuti cliccando sull'icona 
    		<img src="../../img/add_trasp.gif" alt="Aggiorna risultati"/> che si trova in mezzo ai pulsanti di navigazione dei risultati.<br>
    	</p>
	  	<%try { 
	  		out.print(GraficaUtils.showInfoGlobaliCopiaProsp(result));
	  		out.print(GraficaUtils.showPulsantiNavigazioneCopiaProsp(result));
	  		out.print(GraficaUtils.showRisultatiCopiaProspetti(result, true, null, ritornaPrgRisultato, true));
	  		%>
	  <%} catch (Exception e) {
	  		_logger.fatal( 
	  			"Eccezione nella formattazione dei risultati della copia prospetti", e);
	    	out.print("<p class='titolo'>Impossibile visualizzare il risultato dell&#39;operazione di generazione copie in corso d&#39;anno.</p>");
	  	}%>
      	<br>
		<af:form>
			<div>
			<%if(result.getAttributeAsVector("RECORD").size() != 0) {
				try { 
			  		out.print(GraficaUtils.showPulsantiNavigazioneCopiaProsp(result));
			  	} 
			  	catch (Exception e) {
			  		_logger.fatal( 
			  			"Eccezione nella formattazione dei risultati della copia prospetti", e);
			    	out.print("<p class='titolo'>Impossibile visualizzare il risultato dell&#39;operazione di generazione copie in corso d&#39;anno.</p>");
		  		}
		  	}%>
	  		</div>
	  		<input class="pulsanti" type="button" onclick="altraRicerca();" name="cerca" value="Altra ricerca"/>
      		<br/><br/>
		</af:form>
    </center>    
  	</body>
  </html>



