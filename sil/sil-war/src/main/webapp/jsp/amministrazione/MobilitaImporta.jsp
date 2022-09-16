<!-- @author: Giovanni Landi - Agosto 2006 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%!  
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger("it.eng.sil._jsp.MobilitaImporta.jsp");
%>

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
  //Attributi della pagina
  PageAttribs attributi = new PageAttribs(user, (String) serviceRequest.getAttribute("PAGE"));
  //Guardo se ho da visualizzare il risultato di una importazione (se ho un attributo IMPORTED nella richiesta)
  boolean imported = serviceRequest.containsAttribute("IMPORTA");
  String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
  //Oggetti per l'applicazione dello stile grafico
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
  
  boolean canImported = attributi.containsButton("IMPORTA");
  boolean canSearch = attributi.containsButton("CERCA");
  
  if (imported) {
	//In questo caso il cdnFunzione lo ripesco dalla response del modulo
  	_funzione = StringUtils.getAttributeStrNotNull(serviceResponse, "M_MOB_IMPORTAMOBILITA.CDNFUNZIONE");
  }
%>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Importa Mobilità</title>
  <script language="Javascript">
  <% 
  	//Genera il Javascript che si occuperà di inserire i links nel footer
  	attributi.showHyperLinks(out, requestContainer,responseContainer,"");
  %>
  </script>
  
  <script language="Javascript">
  var imgChiusa = "../../img/chiuso.gif";
  var imgAperta = "../../img/aperto.gif";
  function inizializza() {
    rinfresca();
    checkError();
  }
  
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
  
  //Link alla pagina di ricerca della mobilità da validare
  function ricercaMobValidazione() {
	var get = "AdapterHTTP?PAGE=MobilitaRicercaValidazionePage&cdnFunzione=<%=_funzione%>";
	setWindowLocation(get);
  }
  
  function controllaDati() {
  	var nomeFile = document.importa.fileOrigine.value;
  	var estensioneFile = "";
  	var tipoFile = document.importa.tipoFile.value;
  	if (nomeFile == "") {
  		alert("Attenzione: scegliere il file da importare.");
  		return false;
  	}
  	if (nomeFile.length > 4) {
  		estensioneFile = nomeFile.substr(nomeFile.length - 4, 4);
  		if (tipoFile == "PROV") {
  			if (estensioneFile.toUpperCase() != ".TXT") {
  				alert("Attenzione: formato file non corretto.");
  				return false;
  			}
  		}
  		else {
  			if (estensioneFile.toUpperCase() != ".DBF") {
  				alert("Attenzione: formato file non corretto.");
  				return false;
  			}
  		}
  	}
  	else {
  		alert("Attenzione: formato file non corretto.");
  		return false;
  	}
  	document.importa.nomeFile.value = nomeFile;
  	return true;
  }
  
  </script>
  
</head>

<body class="gestione" onload="inizializza();">
<af:error/>
  <br>
  <p class="titolo">Importa Mobilità</p>
  <br/>
  <center>
    <%out.print(htmlStreamTop);%>
    <table class="main">
      <af:form name="importa" method="POST" onSubmit="controllaDati()" action="AdapterHTTP?PAGE=MobilitaImportaPage&IMPORTA=true" encType="multipart/form-data">
		<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
		<input type="hidden" name="nomeFile" value=""/>
        <tr>
          <td class="etichetta">Nome del file da importare</td>
          <td class="campo">
            <IMG SRC="../../img/upload.gif" BORDER="0">
            <INPUT TYPE="file" NAME="fileOrigine" title="Nome del File" SIZE="60">
          </td>
        </tr>
        <tr>
          <td class="etichetta">Versione del tracciato</td>
          <td class="campo" >
            <af:comboBox classNameBase="input" required="true" name="version" title="Versione del tracciato">
              <option value="DEFAULT" selected="selected">DEFAULT</option>
            </af:comboBox>
          </td>            
        </tr>
        <tr><td colspan="2">&nbsp;</td></tr>
        <tr>
          <td colspan="2" align="center">
          <% if (canImported) { %>
            	<input class="pulsanti" type="submit" name="azioneImporta" value="Importa"/>&nbsp;&nbsp;
          <% }
          if (canSearch) { %>
            <input class="pulsanti" type="button" onclick="ricercaMobValidazione();" name="azioneRicerca" 
                   value="Cerca mobilità da validare" title="Cerca mobilità da validare"/>
          <% } %>
          </td>
        </tr>            
      </af:form>
    </table>
    <%out.print(htmlStreamBottom);%>
    <br>
    <%
      String elemRisultatiScript[] = null;
      String elemScriptAlert = "";
      String elemScriptConfirm = "";
      if (imported) {
        //Visualizzazione dei risultati di una precedente importazione
        //Genero la sezione di visualizzazione dei risultati a partire dal SourceBean di risposta.
        try {
        	SourceBean result = (SourceBean) serviceResponse.getAttribute("M_Mob_ImportaMobilita.RECORDS");
			out.print(GraficaUtils.showInfoGlobaliImportazione(result));
			out.print(GraficaUtils.showRisultatiMobilita(result, true, null));
			elemScriptAlert = GraficaUtils.showAlert(result);
			elemScriptConfirm = GraficaUtils.showConfirm(result);
        } catch (Exception e) {
          //Segnalo che è impossibile visualizzare i risultati.
	  	  _logger.fatal("Eccezione nella formattazione dei risultati dell'importazione", e);
          out.print("<p class=\"titolo\">Impossibile visualizzare il risultato dell'importazione</p>");
        }
      }%>
	</center>  
	</body>
</html>