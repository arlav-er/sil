<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<% 
  String _funzione=(String) serviceRequest.getAttribute("cdnFunzione");
	String cf = StringUtils.getAttributeStrNotNull(serviceRequest,"CF");
	String cognome       = StringUtils.getAttributeStrNotNull(serviceRequest,"cognome");
	String nome          = StringUtils.getAttributeStrNotNull(serviceRequest,"nome");
	String tipoRicerca          = StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRicerca");
	String dataInizioDa        = StringUtils.getAttributeStrNotNull(serviceRequest,"datinizioda");
	String dataInizioA        = StringUtils.getAttributeStrNotNull(serviceRequest,"datinizioa");
	String codCMTipoIscr      = StringUtils.getAttributeStrNotNull(serviceRequest,"codCMTipoIscr");
	String codStatoAtto      = StringUtils.getAttributeStrNotNull(serviceRequest,"CODSTATOATTO");
	String codCMTipoInvalidita      = StringUtils.getAttributeStrNotNull(serviceRequest,"codCMTipoInvalidita");
	String numPercInvalildita      = StringUtils.getAttributeStrNotNull(serviceRequest,"numPercInvalildita");
	String CodCPI      = StringUtils.getAttributeStrNotNull(serviceRequest,"CodCPI");
	String codGradoOcc      = StringUtils.getAttributeStrNotNull(serviceRequest,"codGradoOcc");
	String PROVINCIA_ISCR = StringUtils.getAttributeStrNotNull(serviceRequest,"PROVINCIA_ISCR");

	String parameters = "";
	
	if (!"".equals(_funzione))
		parameters += "&cdnFunzione="+_funzione;
	
	
	if (!"".equals(cf))
		parameters += "&cf="+cf;
	
	if (!"".equals(cognome))
		parameters += "&cognome="+cognome;
	
	if (!"".equals(nome))
		parameters += "&nome="+nome;
	
	if (!"".equals(tipoRicerca))
		parameters += "&tipoRicerca="+tipoRicerca;
	
	if (!"".equals(dataInizioDa))
		parameters += "&datInizioDa="+dataInizioDa;
	
	if (!"".equals(dataInizioA))
		parameters += "&datInizioA="+dataInizioA;
	
	if (!"".equals(codCMTipoIscr))
		parameters += "&codCMTipoIscr="+codCMTipoIscr;
	
	if (!"".equals(codStatoAtto))
		parameters += "&codStatoAtto="+codStatoAtto;
	
	if (!"".equals(codCMTipoInvalidita))
		parameters += "&codCMTipoInvalidita="+codCMTipoInvalidita;
	
	if (!"".equals(numPercInvalildita))
		parameters += "&numPercInvalildita="+numPercInvalildita;
	
	if (!"".equals(CodCPI))
		parameters += "&CodCPI="+CodCPI;
	
	if (!"".equals(codGradoOcc))
		parameters += "&codGradoOcc="+codGradoOcc;

	if (!"".equals(PROVINCIA_ISCR))
		parameters += "&PROVINCIA_ISCR="+PROVINCIA_ISCR;
%>

<html>
<head>
 <link rel="stylesheet" type="text/css" href=" ../../css/stili.css"/>
 <link rel="stylesheet" type="text/css" href=" ../../css/listdetail.css"/>

<af:linkScript path="../../js/"/>
<script language="JavaScript">
function openPage(pagina,parametri)
{ //alert(pagina+"\r\n"+parametri);
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;

  setWindowLocation("AdapterHTTP?PAGE="+pagina +parametri);
}

function Select(prgCMIscr, CDNLAVORATORE, CODSTATOATTO,PROVINCIA_ISCR) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var s= "AdapterHTTP?";
    <% if (Sottosistema.CM.isOn()) {%>
    s += "PAGE=CMIscrizioniLavoratorePage";
	s += "&MODULE=CM_GET_DETT_ISCR";
	s += "&CODSTATOATTO=" + CODSTATOATTO;
	s += "&PROVINCIA_ISCR=" + PROVINCIA_ISCR;
    <% } else {%>
    s += "PAGE=AmstrListeSpecCMPage";
    <% }%>
    s += "&prgCMIscr=" + prgCMIscr;
    s += "&CDNFUNZIONE=<%= _funzione%>";
    s += "&CDNLAVORATORE=" + CDNLAVORATORE;
    s+= "&APRI_EV=1";

    setWindowLocation(s);
  }
</script>
</head>

<body onload="checkError();rinfresca();rinfresca_laterale()" class="gestione">
<af:error/>

<br/><p class="titolo">Elenco Iscrizioni Collocamento Mirato</p>
<%
	String attr   = null;
  	String valore = null;
  	String txtOut = "";
	attr= "cognome";
    valore = (String) serviceRequest.getAttribute(attr);
   	if(valore != null && !valore.equals(""))
   	{
   		txtOut += "cognome <strong>"+ valore +"</strong>; ";
   	}
    attr= "nome";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals(""))
    {
    	txtOut += "nome <strong>"+ valore +"</strong>; ";
    }
	attr= "CF";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals(""))
    {
    	txtOut += "codice fiscale <strong>"+ valore +"</strong>; ";
    }
    attr= "descrTipoIscrhid";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals(""))
    {
    	txtOut += "tipo iscrizione  <strong>"+ valore +"</strong>; ";
    }
    if (Sottosistema.CM.isOn()) {
	    attr= "descrSTATOATTOhid";
	    valore = (String) serviceRequest.getAttribute(attr);
	    if(valore != null && !valore.equals(""))
	    {
	    	txtOut += "Stato dell'atto  <strong>"+ valore +"</strong>; ";
	    }    
    }
    attr= "descrTipoInvaliditahid";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals(""))
    {
    	txtOut += "tipo invalidità <strong>"+ valore +"</strong>; ";
    }
    attr= "numPercInvalildita";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals(""))
    {
    	txtOut += "invalidità <strong>"+ valore +"%</strong>; ";
    }
    if(!serviceRequest.getAttribute("datinizioda").equals("") || !serviceRequest.getAttribute("datinizioa").equals("")){
   		txtOut += "data inizio";
    } 
    attr= "datinizioda";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals(""))
    {
    	txtOut += " da <strong>"+ valore +"</strong> ";
    }
    attr= "datinizioa";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals(""))
    {
    	txtOut += " fino a <strong>"+ valore +"</strong>; ";
    }
    attr= "descrCPIhid";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals(""))
    {	   
	   	txtOut += "Centro per l'impiego competente <strong>"+ valore +"</strong>; ";
    }
    attr= "codGradoOcc";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals(""))
    {	   
    	String strValore = "";
    	if(valore.equals("SEG")) strValore = "Segnalabile";
    	else if(valore.equals("SOS")) strValore = "In Sospeso";
    		else if(valore.equals("PER")) strValore = "Percorsi di Sostegno";
    			else if(valore.equals("NSE")) strValore = "Attualmente non Segnalabile";
	   	txtOut += "Grado di Occupabilità <strong>"+ strValore +"</strong>; ";
    }
   
    attr= "provinciaTerrit";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals(""))
    {	   
	   	txtOut += "Ambito Territoriale <strong>"+ valore +"</strong>; ";
    }
    
 %>
<p align="center">
<%if(txtOut.length() > 0)
  { txtOut = "Filtri di ricerca:<br/> " + txtOut;%>
    <table cellpadding="2" cellspacing="10" border="0" width="100%">
     <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
      <%out.print(txtOut);%>
     </td></tr>
    </table>
<%}%>

<af:form dontValidate="true">

<af:list moduleName="M_CollMiratoRicerca" configProviderClass="it.eng.sil.module.amministrazione.CollMiratoRicercaListConfig" jsSelect="Select" getBack="true"/>

<table class="main">
  <tr><td>&nbsp;</td></tr>
  <tr><td align="center"><input type="button" class="pulsanti" value="Torna alla ricerca" onClick="openPage('CollMiratoRicercaPage','<%=parameters%>')"></td></tr>
</table>
</af:form>

</body>
</html>