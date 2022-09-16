<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                java.util.*,
                it.eng.afExt.utils.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<% 
  String parameters = "";
  String queryString = null;
  String _funzione=(String) serviceRequest.getAttribute("cdnFunzione");
  String dataInDA = StringUtils.getAttributeStrNotNull(serviceRequest, "datinizioda");
  String dataInA = StringUtils.getAttributeStrNotNull(serviceRequest, "datinizioa");
  String tipoLista = StringUtils.getAttributeStrNotNull(serviceRequest, "codCMTipoIscr");
  String tipoInv = StringUtils.getAttributeStrNotNull(serviceRequest, "codCMTipoInvalidita");
  String statoAtto = StringUtils.getAttributeStrNotNull(serviceRequest, "CODSTATOATTO");
  String percDa = StringUtils.getAttributeStrNotNull(serviceRequest, "numPercInvalilditaDa");
  String percA = StringUtils.getAttributeStrNotNull(serviceRequest, "numPercInvalilditaA");
  String codCittadinanza = StringUtils.getAttributeStrNotNull(serviceRequest, "codCittadinanza");
  String titoloStudio = StringUtils.getAttributeStrNotNull(serviceRequest, "codTitolo");
  String comuneDispo = StringUtils.getAttributeStrNotNull(serviceRequest, "codComune");
  String comuniDispo = StringUtils.getAttributeStrNotNull(serviceRequest, "INSIEMEDICOMUNI");
  String provDispo = StringUtils.getAttributeStrNotNull(serviceRequest, "CODPROVINCIA");
  String codProfilo = StringUtils.getAttributeStrNotNull(serviceRequest, "CODPROFILO");
  String PROVINCIA_ISCR = StringUtils.getAttributeStrNotNull(serviceRequest, "PROVINCIA_ISCR");
  String descProvincia = "";
  String descComune = "";
  String descStatoAtto = "";
  String descTipoLista = "";
  String descTipoInv = "";
  String descCittadinanza = "";
  String descTitoloStudio = "";
  String descZona = "";
  String strProfilo = "";
  if (!comuneDispo.equals("")) {
	 SourceBean rowComune = (SourceBean)serviceResponse.getAttribute("M_GetComune.ROWS.ROW");
	 descComune = StringUtils.getAttributeStrNotNull(rowComune, "descrizione");
  }
  if (!provDispo.equals("")) {
	 SourceBean rowProv = (SourceBean)serviceResponse.getAttribute("M_GetDescProvincia.ROWS.ROW");
	 descProvincia = StringUtils.getAttributeStrNotNull(rowProv, "descrizione");
  }
  if (!statoAtto.equals("")) {
	 SourceBean rowStatoAtto = (SourceBean)serviceResponse.getAttribute("M_GETDESCSTATOATTOISCR.ROWS.ROW");
	 descStatoAtto = StringUtils.getAttributeStrNotNull(rowStatoAtto, "descrizione");
  }
  if (!tipoLista.equals("")) {
	  SourceBean rowTipoLista = (SourceBean)serviceResponse.getAttribute("M_GetDescCMTipoIscr.ROWS.ROW");
	  descTipoLista = StringUtils.getAttributeStrNotNull(rowTipoLista, "descrizione");  
  }
  if (!tipoInv.equals("")) {
	  SourceBean rowTipoInv = (SourceBean)serviceResponse.getAttribute("M_GetDescCMTipoInvalidita.ROWS.ROW");
	  descTipoInv = StringUtils.getAttributeStrNotNull(rowTipoInv, "descrizione");  
  }
  if (!codCittadinanza.equals("")) {
	  SourceBean rowCittadinanza = (SourceBean)serviceResponse.getAttribute("M_GetDescCittadinanza.ROWS.ROW");
	  descCittadinanza = StringUtils.getAttributeStrNotNull(rowCittadinanza, "descrizione");  
  }
  if (!titoloStudio.equals("")) {
	  SourceBean rowTitolo = (SourceBean)serviceResponse.getAttribute("M_GetDescCMTitoloStudio.ROWS.ROW");
	  descTitoloStudio = StringUtils.getAttributeStrNotNull(rowTitolo, "descrizione");  
  }
  if (!comuniDispo.equals("")) {
	  SourceBean rowZona = (SourceBean)serviceResponse.getAttribute("M_GetZonaComune.ROWS.ROW");
	  descZona = StringUtils.getAttributeStrNotNull(rowZona, "descrizione");
  }
  if (!codProfilo.equals("")) {
	 SourceBean rowProfilo = (SourceBean)serviceResponse.getAttribute("M_GetProfiloDiagnosi.ROWS.ROW");
	 strProfilo = StringUtils.getAttributeStrNotNull(rowProfilo, "descrizione");
  }
  
  if (!dataInDA.equals("")) {
	parameters = parameters + "&datinizioda="+dataInDA;
  }
  if (!dataInA.equals("")) {
	parameters = parameters + "&datinizioa="+dataInA;
  }
  if (!tipoLista.equals("")) {
    parameters = parameters + "&codCMTipoIscr="+tipoLista;	
  }
  if (!tipoInv.equals("")) {
	parameters = parameters + "&codCMTipoInvalidita="+tipoInv;
  }
  if (!statoAtto.equals("")) {
	parameters = parameters + "&CODSTATOATTO="+statoAtto;
  }
  if (!percDa.equals("")) {
	parameters = parameters + "&numPercInvalilditaDa="+percDa;
  }
  if (!percA.equals("")) {
	parameters = parameters + "&numPercInvalilditaA="+percA;
  }
  if (!codCittadinanza.equals("")) {
	parameters = parameters + "&codCittadinanza="+codCittadinanza;
  }
  if (!titoloStudio.equals("")) {
	parameters = parameters + "&codTitolo="+titoloStudio;
  }
  if (!comuneDispo.equals("")) {
	parameters = parameters + "&codComune="+comuneDispo;
  }
  if (!comuniDispo.equals("")) {
	  parameters = parameters + "&INSIEMEDICOMUNI="+comuniDispo;
  }
  if (!provDispo.equals("")) {
	parameters = parameters + "&CODPROVINCIA="+provDispo;
  }
  if(!codProfilo.equals("")) {
    parameters = parameters + "&CODPROFILO="+codProfilo;  
  }
  if (!PROVINCIA_ISCR.equals("")) {
	parameters = parameters + "&PROVINCIA_ISCR="+PROVINCIA_ISCR;
  }
%>

<html>
<head>
 <link rel="stylesheet" type="text/css" href=" ../../css/stili.css"/>
 <link rel="stylesheet" type="text/css" href=" ../../css/listdetail.css"/>
 <%@ include file="../documenti/_apriGestioneDoc.inc"%>

<af:linkScript path="../../js/"/>
<script language="JavaScript">
  function openPage(pagina,parametri)
  { //alert(pagina+"\r\n"+parametri);
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;
  setWindowLocation("AdapterHTTP?PAGE="+pagina +parametri+"<%=parameters%>");
  }
  
  function stampaLista() {
	apriGestioneDoc('RPT_LISTA_CM_TN','<%=parameters%>&stampa=stampa','SI68');
  }
  
</script>
</head>

<body onload="checkError();rinfresca()" class="gestione">
<af:error/>

<br/><p class="titolo">Lista Iscrizioni Collocamento Mirato</p>
<%
	String attr   = null;
  	String valore = null;
  	String txtOut = "";
  	boolean flagPercInv = false;
  	
    if(!descTipoLista.equals(""))
    {
    	txtOut += "tipo iscrizione  <strong>"+ descTipoLista +"</strong>; ";
    }
    
    if(!descTipoInv.equals(""))
    {
    	txtOut += "tipo invalidità <strong>"+ descTipoInv +"</strong>; ";
    }
    
    if(!descStatoAtto.equals(""))
    {
    	txtOut += "Stato dell'atto  <strong>"+ descStatoAtto +"</strong>; ";
    }
    
    attr= "numPercInvalilditaDa";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals(""))
    {
    	flagPercInv = true;
    	txtOut += "invalidità da <strong>"+ valore +"%</strong>; ";
    }
    attr= "numPercInvalilditaA";
    valore = (String) serviceRequest.getAttribute(attr);
    if(valore != null && !valore.equals(""))
    {
    	if (!flagPercInv) {
    		txtOut += "invalidità fino a <strong>"+ valore +"%</strong>; ";	
    	}
    	else {
    		txtOut += " fino a <strong>"+ valore +"%</strong>; ";		
    	}
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
    
    if (!descCittadinanza.equals("")) {
    	txtOut += " Cittadinanza <strong>"+ descCittadinanza +"</strong>; ";
    }
    
    if (!descTitoloStudio.equals("")) {
 	    txtOut += " Titolo di studio <strong>"+ descTitoloStudio +"</strong>; ";
    }
    
    if (!strProfilo.equals("")) {
    	txtOut += " Profilo <strong>"+ strProfilo +"</strong>; ";	
    }
    
    if (!descComune.equals("")) {
    	txtOut += " Disponibilità territoriale <strong>"+ descComune +"</strong>; ";
    }
    else {
    	 if (!descZona.equals("")) {
    		 txtOut += " Disponibilità territoriale <strong>"+ descZona +"</strong>; ";   	
    	 }
    	 else {
    		 if (!descProvincia.equals("")) {
     	   		txtOut += " Disponibilità territoriale <strong>"+ descProvincia +"</strong>; ";
     	     }		 
    	 }
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

<af:list moduleName="M_CMListaIscritti"/>

<table class="main">
  <tr><td>&nbsp;</td></tr>
  <tr><td align="center">
  <input type="button" class="pulsanti" value="Torna alla ricerca" 
      onClick="openPage('CMIscrittiRicercaPage','&cdnFunzione=<%=_funzione%>')">&nbsp;&nbsp;
  <input class="pulsante"" type="button" name="stampa" value="Stampa" onclick="stampaLista()" />
  </td></tr>
</table>
</af:form>

</body>
</html>