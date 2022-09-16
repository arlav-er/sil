<!-- @author: Giovanni Landi - Ottobre 2014 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../../global/noCaching.inc" %>
<%@ include file="../../global/getCommonObjects.inc" %>

<%! 
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger("it.eng.sil._jsp.ImportaFileRA.jsp");
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
  	String nomeFileImportato = "";
  	
  	if (imported) {
		//In questo caso il cdnFunzione lo ripesco dalla response del modulo
  		_funzione = StringUtils.getAttributeStrNotNull(serviceResponse, "M_IMPORTAFILERA.CDNFUNZIONE");
  		nomeFileImportato = StringUtils.getAttributeStrNotNull(serviceResponse, "M_IMPORTAFILERA.NOME_FILE");
  	}
%>

<html>
<head>
  	<link rel="stylesheet" type="text/css" href="../../css/stili.css">
  	<af:linkScript path="../../js/"/>
  	<title>Importazione Reddito Attivazione</title>
  
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
  		var tipoFile = document.importa.tipoFile.value;
  		if (nomeFile == "") {
  			alert("Attenzione: scegliere il file da importare.");
  			return false;
  		}
  		if (nomeFile.length > 4) {
  			estensioneFile = nomeFile.substr(nomeFile.length - 4, 4);
  			if (estensioneFile.toUpperCase() != ".XML") {
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
  
  	</script>
  
</head>

<body class="gestione">
<af:error/>
<br>
<p class="titolo">Importa File Reddito Attivazione</p>
<br/>
<center>
	<%out.print(htmlStreamTop);%>
    <table class="main">
    	<af:form name="importa" method="POST" onSubmit="controllaDati()" action="AdapterHTTP?PAGE=ImportaRedditiAttivazionePage&IMPORTA=true" encType="multipart/form-data">
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
          <%if (canImported) { %>
            	<input class="pulsanti" type="submit" name="azioneImporta" value="Importa"/>
          <%}%>
          </td>
        </tr>            
      </af:form>
    </table>
    <%out.print(htmlStreamBottom);%>
    <br>
    <%
      if (imported) {
        //Genero la sezione di visualizzazione dei risultati a partire dal SourceBean di risposta.
        try {
        	Vector risultati = serviceResponse.getAttributeAsVector("M_IMPORTAFILERA.ROWS.ROW");
        	if (risultati != null) {
        		int processed = risultati.size();
        		int numRecord = processed;
        		int correctProcessed = 0;
        		int errorProcessed = 0;
        		
        		String prgRisultato = "";
        		String stile = "style=\"background-color:#99FFFF\"";
        		String colorePunto = "luceVerde";
        		String righe = "";
        		
        		for (int iCont=0;iCont<numRecord;iCont++) {
        			SourceBean rowCurr = (SourceBean)risultati.get(iCont);
        			if (rowCurr.containsAttribute("RESULT") && rowCurr.getAttribute("RESULT").toString().equalsIgnoreCase("OK")) {
        				correctProcessed++;	
        			}
        			else {
        				errorProcessed++;
        			}
        		}
        		
        		String sommario = "<p class=\"titolo\">Numero di record processati: " + processed + " (di " + numRecord + ")<br>\n"
        				+ "Record importati: " + correctProcessed + "<br/>\n" + "Record non importati: " + errorProcessed;
        				
        		if (nomeFileImportato.equals("")) {
        			sommario = sommario + "</p>\n";
        		}
        		else {
        			sommario = sommario + "<br>\n" + "Nome file: " + nomeFileImportato + "</p>\n";	
        		}
        				
        		out.print(sommario);
        		
        		StringBuffer risultato = new StringBuffer();
    			risultato.append("<ul>\n");
        		
        		for (int iCont=0;iCont<numRecord;iCont++) {
        			int rigaCurr = iCont + 1;
        			SourceBean rowCurr = (SourceBean)risultati.get(iCont);
        			boolean aperta = false;
        			
	        		if (rowCurr.containsAttribute("RESULT") && rowCurr.getAttribute("RESULT").toString().equalsIgnoreCase("ERROR")) {
	        			String cognomeLav = StringUtils.getAttributeStrNotNull(rowCurr, "COGNOME");
		        		String nomeLav = StringUtils.getAttributeStrNotNull(rowCurr, "NOME");
		        		String cFLav = StringUtils.getAttributeStrNotNull(rowCurr, "CODFISC");
	        			colorePunto = "luceRossa";
	        			String erroreCurr = rowCurr.containsAttribute("DETTAGLIOERRORE")?rowCurr.getAttribute("DETTAGLIOERRORE").toString():"";
	        			aperta = true;
	        			
	        			String tendinaRecord = "<a name=\"RITORNA_PRGRISULTATO_" + prgRisultato
								+ "\"></a><div class='sezione2' " + stile + " id='sezioneRisultato" + rigaCurr + "'>\n"
								+ " <img id='tendinaRisultato" + rigaCurr + "' alt='" + (aperta ? "Chiudi" : "Apri") + "' src='"
								+ (aperta ? "../../img/aperto.gif" : "../../img/chiuso.gif")
								+ "' onclick='cambia(this, document.getElementById(\"listaRisultato" + rigaCurr + "\"));'/>\n";
								
		        		
						tendinaRecord += "Lavoratore: " + cognomeLav  + " " +  nomeLav + " " + cFLav;
						tendinaRecord = tendinaRecord + "<br>\n </div>\n";
						String listaRecord = "<div style='display: inline;' id='listaRisultato"
								+ rigaCurr + "'><ul style=\"list-style-image: none\">" + erroreCurr + "</ul></div>\n";
							
						righe = "<li>" + tendinaRecord + listaRecord + "</li>\n";
						risultato.append(righe);
						risultato.append("<script language=\"javascript\">document.getElementById(\"listaRisultato" + rigaCurr
								+ "\").aperta = " + (aperta ? "true" : "false") + "</script>");
	        		}
        		}
        		
        		out.print(risultato.toString());
        		
        	}
        } catch (Exception e) {
          //Segnalo che è impossibile visualizzare i risultati.
	  	  _logger.fatal("Eccezione nella formattazione dei risultati dell'importazione", e);
          out.print("<p class=\"titolo\">Impossibile visualizzare il risultato dell'importazione</p>");
        }
      }%>
</center>  
</body>
</html>