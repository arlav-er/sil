<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ page import="
                com.engiweb.framework.base.*,
                com.engiweb.framework.dispatching.module.*,
				com.engiweb.framework.tags.DefaultErrorTag,
                 
                com.engiweb.framework.util.*,
                it.eng.sil.module.movimenti.*,
                it.eng.sil.module.*,
                it.eng.afExt.utils.*,
                it.eng.sil.security.*,
                it.eng.sil.util.*,
                it.eng.sil.*,
                java.util.*,
                java.math.*,
                java.io.*,
                com.engiweb.framework.security.*"%>
                  
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
  String prgAziendaUtil = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGAZIENDAUTIL");  
  String prgUnitaUtil = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGUNITAUTIL");
  String strRagioneSociale = serviceRequest.containsAttribute("strRagioneSocialeAzUtil")?serviceRequest.getAttribute("strRagioneSocialeAzUtil").toString():"";
  String numContratto = serviceRequest.containsAttribute("numContratto")?serviceRequest.getAttribute("numContratto").toString():"";
  String dataInizio = serviceRequest.containsAttribute("dataInizio")?serviceRequest.getAttribute("dataInizio").toString():"";
  String dataFine = serviceRequest.containsAttribute("dataFine")?serviceRequest.getAttribute("dataFine").toString():"";
  String legaleRapp = serviceRequest.containsAttribute("legaleRapp")?serviceRequest.getAttribute("legaleRapp").toString():"";
  String numSoggetti = serviceRequest.containsAttribute("numSoggetti")?serviceRequest.getAttribute("numSoggetti").toString():"";
  String classeDip = serviceRequest.containsAttribute("classeDip")?serviceRequest.getAttribute("classeDip").toString():"";
  String codTipoTrasf = serviceRequest.containsAttribute("CODTIPOTRASF")?serviceRequest.getAttribute("CODTIPOTRASF").toString():"";
  String funzione = serviceRequest.containsAttribute("FUNZ_AGG")?serviceRequest.getAttribute("FUNZ_AGG").toString():"";
%>

<html>
<head>
  <title>Lista Aziende</title>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/" />
  
  <script language="JavaScript">
  function chiudiRicerca() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;
	document.Form1.PAGE.value = "MovCercaAziendaUtilPage";
    doFormSubmit(document.Form1);
  }
	function altraRicerca() {
		if (isInSubmit()) return;
		
		document.Form1.PAGE.value = "MovimentiSelezionaAziendaUtil";
		doFormSubmit(document.Form1);
	}
  function SelezionaAzienda(prgAzienda, prgUnita, strRagioneSociale, strIndirizzo, strComune) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;
	
   	document.Form1.PRGAZIENDAUTIL.value = prgAzienda;
   	document.Form1.PRGUNITAUTIL.value = prgUnita;
   	document.Form1.PAGE.value = "MovCercaAziendaUtilPage";
    document.Form1.strRagioneSocialeAzUtil.value = strRagioneSociale;
    document.Form1.strIndirizzoAziendaUtil.value = strIndirizzo;
    document.Form1.strComuneAziendaUtil.value = strComune;
    doFormSubmit(document.Form1);
  }
  
  </script>
</head>
<body class="gestione">
<af:error/>
<p align="center">
<af:form name="Form1" method="POST" action="AdapterHTTP">
<!--Passo l'informazione sui dati che dovrò recuperare e sulla funzione
da chiamare al ritorno per selezionare l'azienda utilizzatrice-->
<input type="hidden" name="PAGE" value="">
<input type="hidden" name="FUNZ_AGG" value="<%=funzione%>">
<input type="hidden" name="strRagioneSocialeAzUtil" value="<%=strRagioneSociale%>">
<input type="hidden" name="numContratto" value="<%=numContratto%>">
<input type="hidden" name="dataInizio" value="<%=dataInizio%>">
<input type="hidden" name="dataFine" value="<%=dataFine%>">
<input type="hidden" name="legaleRapp" value="<%=legaleRapp%>">
<input type="hidden" name="numSoggetti" value="<%=numSoggetti%>">
<input type="hidden" name="classeDip" value="<%=classeDip%>">
<input type="hidden" name="PRGAZIENDAUTIL" value="<%=prgAziendaUtil%>">
<input type="hidden" name="PRGUNITAUTIL" value="<%=prgUnitaUtil%>">
<input type="hidden" name="strIndirizzoAziendaUtil" value="">
<input type="hidden" name="strComuneAziendaUtil" value="">
<input type="hidden" name="CODTIPOTRASF" value="<%=codTipoTrasf%>">
<!--Visualizzo i risultati della ricerca-->
<af:list moduleName="M_MovimentiGetListaAziende" jsSelect="SelezionaAzienda" configProviderClass="it.eng.sil.module.movimenti.DynamicRicAziendeUtilConfig"/>
<center>
<table>
<tr><td align="center">
<input type="button" class="pulsanti" value="Chiudi" onClick="javascript:chiudiRicerca();"/>
<input type="button" class="pulsanti" value="Ritorna alla pagina di ricerca" onClick="javascript:altraRicerca();"/>
</td></tr>
</table>
</center>
</af:form>
</p>
</body>
</html>