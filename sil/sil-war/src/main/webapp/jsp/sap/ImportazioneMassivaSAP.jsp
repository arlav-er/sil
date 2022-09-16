<!-- @author: Giovanni Landi - Agosto 2006 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>


<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
				  com.engiweb.framework.tags.DefaultErrorTag,
                  it.eng.sil.security.*,
                  it.eng.sil.module.movimenti.*,
                  java.math.*,
                  java.io.*,
                  it.eng.sil.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  com.engiweb.framework.util.JavaScript,
                  com.engiweb.framework.dbaccess.sql.TimestampDecorator,
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*,
                  java.text.*,
                  java.sql.*,
                  oracle.sql.*,
                  java.util.*"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  String _current_page = (String) serviceRequest.getAttribute("PAGE");
  //Attributi della pagina
  PageAttribs attributi = new PageAttribs(user, _current_page);
  //Guardo se ho da visualizzare il risultato di una importazione (se ho un attributo IMPORTED nella richiesta)
  boolean imported = serviceRequest.containsAttribute("IMPORTA");
  boolean visualizzaUltimiRisultati = serviceRequest.containsAttribute("VISUALIZZARISULTATI");
  String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
  //Oggetti per l'applicazione dello stile grafico
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
  
  boolean canImported = false;
  
  String numConfigImporta = serviceResponse.containsAttribute("M_CONFIG_IMPORTAZIONE_SAP.ROWS.ROW.NUM")?
			serviceResponse.getAttribute("M_CONFIG_IMPORTAZIONE_SAP.ROWS.ROW.NUM").toString():it.eng.sil.module.movimenti.constant.Properties.DEFAULT_CONFIG;
  if (numConfigImporta.equalsIgnoreCase(it.eng.sil.module.movimenti.constant.Properties.CUSTOM_CONFIG)) {
	  canImported = true;	
  }
  ProfileDataFilter filter = new ProfileDataFilter(user, _current_page);
	
  if (!filter.canView()) {
  	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  	return;
  }
  
  if (imported) {
	//In questo caso il cdnFunzione lo ripesco dalla response del modulo
  	_funzione = StringUtils.getAttributeStrNotNull(serviceResponse, "M_ImportazioneMassivaSAP.CDNFUNZIONE");
  }
%>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Importazione SAP</title>
  <script language="Javascript">
  <% 
  	//Genera il Javascript che si occuperà di inserire i links nel footer
  	attributi.showHyperLinks(out, requestContainer,responseContainer,"");
  %>
  </script>
  
  <script language="Javascript">
  var imgChiusa = "../../img/chiuso.gif";
  var imgAperta = "../../img/aperto.gif";
  
  function cambia(immagine, sezione) {
	if (sezione.style.display == 'inline') {
		sezione.style.display = 'none';
		sezione.aperta = false;
		immagine.src = imgChiusa;
    	immagine.alt = "Apri";
	}
	else if (sezione.style.display == "none") {
		sezione.style.display = "inline";
		sezione.aperta = true;
		immagine.src = imgAperta;
    	immagine.alt = "Chiudi";
	}
  }
  
  function controllaDati() {
  	var nomeFile = document.importa.fileOrigine.value;
  	var estensioneFile = "";
  	if (nomeFile == "") {
  		alert("Attenzione: scegliere il file da importare.");
  		return false;
  	}
  	if (nomeFile.length > 4) {
  		estensioneFile = nomeFile.substr(nomeFile.length - 4, 4);
		if (estensioneFile.toUpperCase() != ".XML" && estensioneFile.toUpperCase() != ".ZIP") {
			alert("Attenzione: formato file non corretto.");
			return false;
		}
  	}
  	else {
  		alert("Attenzione: formato file non corretto.");
  		return false;
  	}
  	document.importa.nomeFile.value = nomeFile;
  	return true;
  }

  function visulizzaRisultatiImportazione() {
	var get = "AdapterHTTP?PAGE=ImportazioneMassivaSAPPage&cdnFunzione=<%=_funzione%>&VISUALIZZARISULTATI=S";
	setWindowLocation(get);
  }

  function getPage(npage) {
	var get = "AdapterHTTP?PAGE=ImportazioneMassivaSAPPage&cdnFunzione=<%=_funzione%>&VISUALIZZARISULTATI=S&NUMBERPAGECURRENT=" + npage;
	setWindowLocation(get);
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
  
  </script>
  
  <STYLE>
	  li.luceVerde  { list-style-image: url("../../img/luceVerde.gif"); list-style-type: none; clear: both;}
	  li.luceGialla { list-style-image: url("../../img/luceGialla.gif"); list-style-type: none; clear: both;}
	  li.luceRossa  { list-style-image: url("../../img/luceRossa.gif"); list-style-type: none; clear: both;}
  </STYLE>
  
</head>

<body class="gestione" onload="rinfresca();">
<af:error/>
  <br>
  <p class="titolo">Importazione SAP</p>
  <br/>
  <center>
    <%out.print(htmlStreamTop);%>
    <table class="main">
      <af:form name="importa" method="POST" onSubmit="controllaDati()" action="AdapterHTTP?PAGE=ImportazioneMassivaSAPPage&IMPORTA=true" encType="multipart/form-data">
		<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
		<input type="hidden" name="nomeFile" value=""/>
        <tr>
          <td class="etichetta">Nome del file da importare</td>
          <td class="campo">
            <IMG SRC="../../img/upload.gif" BORDER="0">
            <INPUT TYPE="file" NAME="fileOrigine" title="Nome del File" SIZE="60">
          </td>
        </tr>
        <tr><td colspan="2">&nbsp;</td></tr>
        <tr>
          <td colspan="2" align="center">
          <% if (canImported) { %>
            	<input class="pulsanti" type="submit" name="azioneImporta" value="Importa"/>&nbsp;
          <% } %>
          <input class="pulsanti" type="button" onclick="visulizzaRisultatiImportazione();" name="btnRisultatiImp" value="Risultati ultima importazione" 
          		title="Visualizza i risultati dell'ultima importazione eseguita"/>	
          </td>
        </tr>            
      </af:form>
    </table>
    <%out.print(htmlStreamBottom);%>
    <br>
    <%
 	if (imported || visualizzaUltimiRisultati) {%>
 		<center>
    	<p align="center" class="titolo" style="color: purple;">
    		Questa pagina mostra i risultati dell'ultima importazione eseguita.<br/>
    		Se essa è ancora in atto è possibile aggiornare i risultati ottenuti cliccando sull'icona 
    		<img src="../../img/add_trasp.gif" alt="Aggiorna risultati"/> che si trova in mezzo ai pulsanti di navigazione dei risultati.
    	</p>
 		<%
   		String sommario = serviceResponse.containsAttribute("M_GetRisultatiImportazioneSAP.SOMMARIO")?
    				serviceResponse.getAttribute("M_GetRisultatiImportazioneSAP.SOMMARIO").toString():"";
		String pulsantiNavigazione = serviceResponse.containsAttribute("M_GetRisultatiImportazioneSAP.PULSANTINAVIGAZIONE")?
   				serviceResponse.getAttribute("M_GetRisultatiImportazioneSAP.PULSANTINAVIGAZIONE").toString():"";
		String risultato = serviceResponse.containsAttribute("M_GetRisultatiImportazioneSAP.RISULTATO")?
   				serviceResponse.getAttribute("M_GetRisultatiImportazioneSAP.RISULTATO").toString():"";
        if (!sommario.equals("")) {
        	out.print(sommario);	
        }
        if (!pulsantiNavigazione.equals("")) {
        	out.print(pulsantiNavigazione);	
        }
        if (!risultato.equals("")) {
        	out.print(risultato);	
        }
   	}
   	%>
	</center>  
	</body>
</html>