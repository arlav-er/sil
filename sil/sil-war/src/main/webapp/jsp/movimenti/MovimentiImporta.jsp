<!-- @author: Paolo Roccetti - Gennaio 2004 -->
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
                  java.lang.*,
                  java.text.*,
                  java.sql.*,
                  oracle.sql.*,
                  java.util.*"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%!  
	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger("it.eng.sil._jsp.MovimentiImporta.jsp");
%>


<%
 // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, (String) serviceRequest.getAttribute("PAGE"));
  boolean canValidate = attributi.containsButton("VALIDA");
  /*
  List goToesList = attributi.getGoToes();
  Iterator iter = goToesList.iterator();
  while (iter.hasNext()) {
    GoTo  goTo = (GoTo) iter.next();
    if (goTo.getTargetPage().equalsIgnoreCase("MovRicercaValidazionePage")) {
       canValidate = true;
    }
  }
  */

  String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");

  //Oggetti per l'applicazione dello stile grafico
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);

  //Guardo se ho da visualizzare il risultato di una importazione (se ho un attributo IMPORTED nella richiesta)
  String i = StringUtils.getAttributeStrNotNull(serviceRequest, "IMPORTA");
  boolean imported = false;
  if (i.equalsIgnoreCase("true")) {
  	imported = true;
  	//In questo caso il cdnFunzione lo ripesco dalla response del modulo
  	_funzione = StringUtils.getAttributeStrNotNull(serviceResponse, "M_MOVIMENTIIMPORTAMOVIMENTI.CDNFUNZIONE");
  }
%>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Importa Movimenti</title>
  <script language="Javascript">
  <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
  %>
  
  //Link alla pagina di validazione dei movimenti
  function ricercaValidazione() {
		var get = "AdapterHTTP?PAGE=MovRicercaValidazionePage&cdnFunzione=<%=_funzione%>";
		setWindowLocation(get);
  }

  </script>
  <script language="Javascript">
  function inizializza() {
    rinfresca();
    checkError();
  }
  </script>
  <%@ include file="MovimentiSezioniATendina.inc" %>
</head>

<body class="gestione" onload="inizializza();">
<af:error/>
  <br/>
  <p class="titolo">Importa Movimenti</p>
  <br/>
  <center>
    <%out.print(htmlStreamTop);%>
    <table class="main">
      <af:form name="importa" method="POST" action="AdapterHTTP?PAGE=MovimentiImportaPage&IMPORTA=true" encType="multipart/form-data">

        <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>       
        <tr>
          <td class="etichetta">Nome del file da importare</td>
          <td class="campo">
            <IMG SRC="../../img/upload.gif" BORDER="0">
            <INPUT TYPE="file" NAME="fileOrigine" title="Nome del File" SIZE="60">
            <!--<af:textBox type="text" required="true" name="fileOrigine" title="Nome del File" value="" size="50" maxlength="50"/>-->
          </td>
        </tr>
        <!--
        <tr>
          <td class="etichetta">Path del file da importare</td>
          <td class="campo">
            <af:textBox type="text" name="path" value="" size="100" maxlength="200"/>
          </td>
        </tr>
        -->
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
            <input class="pulsanti" type="submit" name="azione" value="Importa"/>&nbsp;&nbsp;
            <%if (canValidate) {%>
            &nbsp;&nbsp;
            <input class="pulsanti" type="button" onclick="ricercaValidazione();" name="azione" 
            		value="Cerca movimenti da validare" title="Cerca movimenti da validare"/>
          	<%}%>
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
        	SourceBean result = (SourceBean) serviceResponse.getAttribute("M_MOVIMENTIIMPORTAMOVIMENTI.RECORDS");
			out.print(GraficaUtils.showInfoGlobaliImportazione(result));
			out.print(GraficaUtils.showRisultati(result, true, null));
			elemScriptAlert = GraficaUtils.showAlert(result);
			elemScriptConfirm = GraficaUtils.showConfirm(result);
        } catch (Exception e)
        {
          //Segnalo che è impossibile visualizzare i risultati.
	  	  
		_logger.fatal(
	  	  	"Eccezione nella formattazione dei risultati dell'importazione", e);
          out.print("<p class=\"titolo\">Impossibile visualizzare il risultato dell'importazione</p>");
        }
      }%>   
	</center>  
	</body>
  <%@ include file="common/include/GestioneScriptRisultati.inc" %>
</html>

