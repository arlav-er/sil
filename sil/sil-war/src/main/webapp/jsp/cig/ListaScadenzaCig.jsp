<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ page contentType="text/html;charset=utf-8"	
	import="com.engiweb.framework.base.*,
			it.eng.sil.security.*,
			it.eng.afExt.utils.*,
			it.eng.sil.util.*,
			java.math.*,
			java.util.*"
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>

<%	
	String _page = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 
	int _funzione           = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
	String _pageProvenienza = serviceRequest.containsAttribute("PAGEPROVENIENZA")? serviceRequest.getAttribute("PAGEPROVENIENZA").toString():"";
	String strScadenziarioCig = serviceRequest.containsAttribute("SCADENZIARIO")? serviceRequest.getAttribute("SCADENZIARIO").toString():"";
	
	String dataInizioIscr = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"DATINIZIOISCR");
	String codCPI = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"CodCPI");
	String datStimata = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"DATSTIMATA");
	String codEsito = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"CODESITO");
	String codAzione = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"CODAZIONE");
	String descrizione = "";
	String descCPI =  SourceBeanUtils.getAttrStrNotNull(serviceResponse,"M_DescCPI.ROWS.ROW.STRDESCRIZIONE");	
	int nCodScadenza = 0;
	if (strScadenziarioCig.compareTo("CIG1") == 0) {
	  nCodScadenza = 1;
	}
	else {
	  if (strScadenziarioCig.compareTo("CIG2") == 0) {
	    nCodScadenza = 2;
	  }
	  else {
	    if (strScadenziarioCig.compareTo("CIG3") == 0) {
	      nCodScadenza = 3;
	    }
	    else {  
	      if (strScadenziarioCig.compareTo("CIG4") == 0) {
	        nCodScadenza = 4;
	      }
	   	}
	  }
	}
	
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
<title>Scadenze CIG/MB in Deroga</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<af:linkScript path="../../js/"/>

<script>
	   


function appuntamenti(cdnLavoratore) {
	document.Frm1.PAGE.value = "ScadAppuntamentoPage";  
    document.Frm1.CDNFUNZIONE.value = "<%=_funzione%>";
    document.Frm1.CDNLAVORATORE.value = cdnLavoratore;
    document.Frm1.DATINIZIOISCR.value = "<%=dataInizioIscr%>";
    document.Frm1.CODCPI.value = "<%=codCPI%>";
    document.Frm1.DATSTIMATA.value = "<%=datStimata%>";
    document.Frm1.CODESITO.value = "<%=codEsito%>";
    document.Frm1.CODAZIONE.value = "<%=codAzione%>";
    document.Frm1.LIST_PAGE_SCAD.value = <%=serviceResponse.getAttribute("M_ListScadenzeCig.ROWS.CURRENT_PAGE")%>;
    document.Frm1.MESSAGE_SCAD.value = "LIST_PAGE";
    doFormSubmit(document.Frm1);
}

function contatti(cdnLavoratore) {
	document.Frm1.PAGE.value = "ScadContattoPage";
    document.Frm1.CDNLAVORATORE.value = cdnLavoratore;
    document.Frm1.CDNFUNZIONE.value = "<%=_funzione%>";
    document.Frm1.DATINIZIOISCR.value = "<%=dataInizioIscr%>";
    document.Frm1.CODCPI.value = "<%=codCPI%>";
    document.Frm1.DATSTIMATA.value = "<%=datStimata%>";
    document.Frm1.CODESITO.value = "<%=codEsito%>";
    document.Frm1.CODAZIONE.value = "<%=codAzione%>";
    document.Frm1.RECUPERAINFO.value = "LAVORATORE";   
    document.Frm1.LIST_PAGE_SCAD.value = <%=serviceResponse.getAttribute("M_ListScadenzeCig.ROWS.CURRENT_PAGE")%>;
    document.Frm1.MESSAGE_SCAD.value = "LIST_PAGE";
    doFormSubmit(document.Frm1);
}

function TornaAllaRicerca() {
      document.Frm1.PAGE.value = "CercaScadenzeCigPage";
      document.Frm1.CDNFUNZIONE.value = "<%=_funzione%>";
      doFormSubmit(document.Frm1);  
}

function ApriSchedaLavoratore (cdnLavoratore) {
	  var s= "AdapterHTTP?PAGE=AmstrInfoCorrentiPage";
      s += "&cdnLavoratore=" + cdnLavoratore;
      s += "&APRI_EV=1";
      window.open (s,"InfoCorrenti", "toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES");
    }

</script>
<SCRIPT TYPE="text/javascript">
 
 
  
</SCRIPT> 
</head>

<body class="gestione" onload="rinfresca();">


<%
String txtOut = "";
SourceBean row = null;

switch(nCodScadenza) {
case 1: 
	if(dataInizioIscr != null && !dataInizioIscr.equals(""))
    {
    	txtOut += "<br/>Data iscrizione prima del  <strong>"+ dataInizioIscr +"</strong>; ";
    }
	if(descCPI != null && !"".equals(descCPI)) {
		txtOut += "<br/>Centro per l'impiego competente: <strong>" + descCPI + "</strong>;";
	}
	
%>
<p align="center">
<%if(txtOut.length() > 0)
  { txtOut = "Filtri di ricerca: " + txtOut;%>
    <table cellpadding="2" cellspacing="10" border="0" width="100%">
     <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
      <%out.print(txtOut);%>
     </td></tr>
    </table>
<%} break; 

case 2: 
	if(datStimata != null && !datStimata.equals(""))
    {
    	txtOut += "<br/>Data di mancata presentazione  <strong>"+ datStimata +"</strong>; ";
    }
	
	row = (SourceBean) serviceResponse.getAttribute("M_EsitoCig.ROWS.ROW");
		if (row != null) {
			descrizione = (String) row.getAttribute("DESCRIZIONE");
	    }
	    
		if (descrizione != null && !descrizione.equals("")) {
	        txtOut += "<br/>Esito <strong>"+ descrizione +"</strong>; ";
	    }
%>
<p align="center">
<%if(txtOut.length() > 0)
  { txtOut = "Filtri di ricerca: " + txtOut;%>
    <table cellpadding="2" cellspacing="10" border="0" width="100%">
     <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
      <%out.print(txtOut);%>
     </td></tr>
    </table>
<%} break; 

case 3: 
	row = (SourceBean) serviceResponse.getAttribute("M_AzioniCig.ROWS.ROW");
	if (row != null) {
		descrizione = (String) row.getAttribute("DESCRIZIONE");
    }
    
	if (descrizione != null && !descrizione.equals("")) {
        txtOut += "<br/>Azione <strong>"+ descrizione +"</strong>; ";
    }
%>
<p align="center">
<%if(txtOut.length() > 0)
  { txtOut = "Filtri di ricerca: " + txtOut;%>
    <table cellpadding="2" cellspacing="10" border="0" width="100%">
     <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
      <%out.print(txtOut);%>
     </td></tr>
    </table>
<%} break; 


case 4: 
	row = (SourceBean) serviceResponse.getAttribute("M_EsitoCig.ROWS.ROW");
	if (row != null) {
		codEsito = (String) row.getAttribute("CODICE");
		descrizione = (String) row.getAttribute("DESCRIZIONE");
    }
    
	if (descrizione != null && !descrizione.equals("")) {
        txtOut += "<br/>Esito <strong>"+ descrizione +"</strong>; ";
    }
%>
<p align="center">
<%if(txtOut.length() > 0)
  { txtOut = "Filtri di ricerca: " + txtOut;%>
    <table cellpadding="2" cellspacing="10" border="0" width="100%">
     <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
      <%out.print(txtOut);%>
     </td></tr>
    </table>
<%} break; 

}%>

<af:form name="Frm1" action="AdapterHTTP" method="POST">
<input type="hidden" name="PAGE" value="">
<input type="hidden" name="LIST_PAGE_SCAD" value=""/>
<input type="hidden" name="MESSAGE_SCAD" value=""/>
<input type="hidden" name="CDNLAVORATORE" value="">
<input type="hidden" name="CDNFUNZIONE" value=""/>
<input type="hidden" name="PAGEPROVENIENZA" value="<%=_pageProvenienza%>">
<input type="hidden" name="SCADENZIARIO" value="<%=strScadenziarioCig%>">
<input type="hidden" name="RECUPERAINFO" value="">
<input type="hidden" name="DATINIZIOISCR" value="">
<input type="hidden" name="CODCPI" value="">
<input type="hidden" name="DATSTIMATA" value="">
<input type="hidden" name="CODESITO" value="">
<input type="hidden" name="CODAZIONE" value="">

<af:list moduleName="M_ListScadenzeCig" configProviderClass="it.eng.sil.module.cig.ListaScadenzeCigConfig" jsSelect="ApriSchedaLavoratore"/>

<center>
	<table>
  		<tr>
  			<td>
  				<input class="pulsanti" type="button" name="btnBack" value="Torna alla pagina di ricerca" onclick="javascript:TornaAllaRicerca();"/>
  			</td>
  		</tr>
  </table>
</center>


<br/>
</af:form>
</body>
</html>