<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                com.engiweb.framework.base.*,
                com.engiweb.framework.dispatching.module.AbstractModule,
                
                com.engiweb.framework.util.QueryExecutor,
                it.eng.sil.security.*,
                it.eng.afExt.utils.*,
                it.eng.sil.util.*,
                java.util.*,
                java.math.*,
                java.io.*,
                com.engiweb.framework.security.*"%>
      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
  String prgAziendaUtil = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGAZIENDAUTIL"); 
  String prgUnitaUtil = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGUNITAUTIL"); 
  String strRagioneSociale = serviceRequest.containsAttribute("RAGSOCUTIL")?serviceRequest.getAttribute("RAGSOCUTIL").toString():"";
  String numContratto = serviceRequest.containsAttribute("numContratto")?serviceRequest.getAttribute("numContratto").toString():"";
  String dataInizio = serviceRequest.containsAttribute("dataInizio")?serviceRequest.getAttribute("dataInizio").toString():"";
  String dataFine = serviceRequest.containsAttribute("dataFine")?serviceRequest.getAttribute("dataFine").toString():"";
  String legaleRapp = serviceRequest.containsAttribute("legaleRapp")?serviceRequest.getAttribute("legaleRapp").toString():"";
  String numSoggetti = serviceRequest.containsAttribute("numSoggetti")?serviceRequest.getAttribute("numSoggetti").toString():"";
  String classeDip = serviceRequest.containsAttribute("classeDip")?serviceRequest.getAttribute("classeDip").toString():"";
  String funzione = serviceRequest.containsAttribute("FUNZ_AGG")?serviceRequest.getAttribute("FUNZ_AGG").toString():"";
  String codTipoTrasf = serviceRequest.containsAttribute("CODTIPOTRASF")?serviceRequest.getAttribute("CODTIPOTRASF").toString():"";
  //Oggetti per l'applicazione dello stile grafico
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
  <title>Ricerca per Aziende</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/"/>    
  <script language="Javascript">
  function chiudiRicerca() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;
	
	document.Form1.PAGE.value = "MovCercaAziendaUtilPage";
    doFormSubmit(document.Form1);
  }
  </script>
</head>
<body class="gestione">
<p class="titolo">Ricerca per Aziende</p>
<p align="center">
<af:form name="Form1" method="POST" action="AdapterHTTP">
<input type="hidden" name="PAGE" value="MovimentiListaAziendaUtilPage"/>
<!--Passo l'informazione sui dati che dovrò recuperare e sulla funzione
da chiamare al ritorno per selezionare l'azienda utilizzatrice-->
<input type="hidden" name="FUNZ_AGG" value="<%=funzione%>">
<input type="hidden" name="RAGSOCUTIL" value="<%=strRagioneSociale%>">
<input type="hidden" name="numContratto" value="<%=numContratto%>">
<input type="hidden" name="dataInizio" value="<%=dataInizio%>">
<input type="hidden" name="dataFine" value="<%=dataFine%>">
<input type="hidden" name="legaleRapp" value="<%=legaleRapp%>">
<input type="hidden" name="numSoggetti" value="<%=numSoggetti%>">
<input type="hidden" name="classeDip" value="<%=classeDip%>">
<input type="hidden" name="PRGAZIENDAUTIL" value="<%=prgAziendaUtil%>">
<input type="hidden" name="PRGUNITAUTIL" value="<%=prgUnitaUtil%>">
<input type="hidden" name="CODTIPOTRASF" value="<%=codTipoTrasf%>">
<%out.print(htmlStreamTop);%>
<table class="main">
    
<tr valign="top">
  <td class="etichetta">Tipo azienda</td>
  <td class="campo">
    <af:comboBox classNameBase="input" name="codTipoAzienda" title="Tipo Azienda" moduleName="M_GETTIPIAZIENDA" addBlank="true"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Partita IVA</td>
  <td class="campo">
    <af:textBox type="text" name="piva" validateOnPost="true" value="" size="30" maxlength="11"/>
  </td>
</tr>
<tr>
  <td class="etichetta">Codice Fiscale</td>
  <td class="campo">
    <af:textBox type="text" name="cf" validateOnPost="true" value="" size="30" maxlength="16"/>
  </td>
</tr>            
<tr>
  <td class="etichetta">Ragione Sociale</td>
  <td class="campo">
    <af:textBox type="text" name="ragioneSociale" validateOnPost="true" value="" size="30" maxlength="100"/>
  </td>
</tr>   
<!--Inserisco i pulsanti necessari-->
<tr><td colspan="2">&nbsp;</td></tr>
<tr>
  <td colspan="2" align="center">
  <input class="pulsanti" type="submit" name="cerca" value="Cerca"/>
  &nbsp;&nbsp;
  <input class="pulsanti" type="reset" name="reset" value="Annulla"/>
  </td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
<tr>
  <td colspan="2" align="center">
  <input type="button" class="pulsanti" value="Chiudi" onClick="javascript:chiudiRicerca();"/>
  </td>
</tr>
</table>
<%out.print(htmlStreamBottom);%>
</af:form>
</p>
</body>
</html>
