<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.afExt.utils.StringUtils,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<% 
  // NOTE: Attributi della pagina (pulsanti e link)
  PageAttribs attributi = new PageAttribs(user, "PattoLavDettaglioPage");
  boolean canDelete=attributi.containsButton("aggiorna");
  
  String cognome=StringUtils.getAttributeStrNotNull(serviceRequest,"COGNOME");
  String nome=StringUtils.getAttributeStrNotNull(serviceRequest,"NOME");
  String cf=StringUtils.getAttributeStrNotNull(serviceRequest,"CF");
  String CodCPI=StringUtils.getAttributeStrNotNull(serviceRequest,"CodCPI");
  String flgPatto297=StringUtils.getAttributeStrNotNull(serviceRequest,"flgPatto297");
  String codCodificaPatto=StringUtils.getAttributeStrNotNull(serviceRequest,"codCodificaPatto");
  String codTipoPatto=StringUtils.getAttributeStrNotNull(serviceRequest,"codTipoPatto");
  String AttiAperti=StringUtils.getAttributeStrNotNull(serviceRequest,"AttiAperti");    
  String pattiOnLine=StringUtils.getAttributeStrNotNull(serviceRequest,"PattiOnLine");   
  String codAccOnLine= StringUtils.getAttributeStrNotNull(serviceRequest,"codAccOnLine");   
  String datInizioDa=StringUtils.getAttributeStrNotNull(serviceRequest,"datInizioDa");
  String datInizioA=StringUtils.getAttributeStrNotNull(serviceRequest,"datInizioA");      
  String scadconfda=StringUtils.getAttributeStrNotNull(serviceRequest,"scadconfda");
  String scadconfa=StringUtils.getAttributeStrNotNull(serviceRequest,"scadconfa");      
  String dataFineAttoDa=StringUtils.getAttributeStrNotNull(serviceRequest,"dataFineAttoDa");
  String dataFineAttoA=StringUtils.getAttributeStrNotNull(serviceRequest,"dataFineAttoA");  
  String MotivoFine=StringUtils.getAttributeStrNotNull(serviceRequest,"MotivoFine");    
  String tipoRicerca=StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRicerca");
  String _funzione=serviceRequest.getAttribute("CDNFUNZIONE").toString();
  String titoloRicercaPattoAcc="ESITO RICERCA PATTO/ACCORDO LAVORATORI";
  if (flgPatto297.equalsIgnoreCase("S")) {
	  titoloRicercaPattoAcc = "ESITO RICERCA PATTO LAVORATORI";  
  }
  else {
	  if (flgPatto297.equalsIgnoreCase("N")) {
		  titoloRicercaPattoAcc = "ESITO RICERCA ACCORDO LAVORATORI";  
	  }
  }
%>

<html>
<head>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 <af:linkScript path="../../js/" />
 
<script type="text/JavaScript">

 function TornaAllaRicerca() 
{ // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;
    
	url="AdapterHTTP?PAGE=PattoRicercaPattoLavPage"; 
    url += "&CDNFUNZIONE="+"<%=_funzione%>";
    url += "&COGNOME="+"<%=cognome%>";
    url += "&NOME="+"<%=nome%>";
    url += "&CF="+"<%=cf%>";
    url += "&tipoRicerca="+"<%=tipoRicerca%>"; 
    url += "&AttiAperti="+"<%=AttiAperti%>";
    url += "&PattiOnLine="+"<%=pattiOnLine%>";
    url += "&codAccOnLine="+"<%=codAccOnLine%>";
    url += "&datInizioDa="+"<%=datInizioDa%>";
    url += "&datInizioA="+"<%=datInizioA%>"; 
    url += "&scadconfda="+"<%=scadconfda%>";
    url += "&scadconfa="+"<%=scadconfa%>";
    url += "&dataFineAttoDa="+"<%=dataFineAttoDa%>";
    url += "&dataFineAttoA="+"<%=dataFineAttoA%>";
    url += "&CodCPI="+"<%=CodCPI%>";
    url += "&flgPatto297="+"<%=flgPatto297%>";
    url += "&codCodificaPatto="+"<%=codCodificaPatto%>";
    url += "&codTipoPatto="+"<%=codTipoPatto%>";
    url += "&MotivoFine="+"<%=MotivoFine%>";
    setWindowLocation(url);
} 

</script>
</head>

<body onload="checkError();rinfresca()">
<af:error/>
<br/><p class="titolo"><%=titoloRicercaPattoAcc %></p>
<%String attr   = null;
  String valore = null;
  String txtOut = "";
%>
     <%attr= "cognome";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "cognome <strong>"+ valore +"</strong>; ";
       }%>
     <%attr= "nome";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "nome <strong>"+ valore +"</strong>; ";
       }%>
     <%attr= "CF";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "codice fiscale <strong>"+ valore +"</strong>; ";
       }%>
     <%attr= "descFlgPatto297_H";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "Tipologia <strong>"+ valore +"</strong>; ";
       }%>
     <%attr= "descCodTipoPatto_H"; //obietti del patto
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "Obiettivi del patto <strong>"+ valore +"</strong>; ";
       }%>
       <%attr= "AttiAperti";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += " Atti Aperti <strong>" + valore + "</strong>; ";
       }%>
        <%attr= "PattiOnLine";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += " Patti on line <strong>" + valore + "</strong>; ";
       }%>
        <%attr= "codAccOnLine";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {	String descr = "";
       if(valore.equalsIgnoreCase("A"))
    	   descr= "Accettato";
       if(valore.equalsIgnoreCase("D"))
    	   descr= "Da accettare";
       if(valore.equalsIgnoreCase("S"))
    	   descr= "Scadenza termini accettazione";
    	   txtOut += "Stato accettazione patto on line <strong>" + descr + "</strong>; ";
       }%>
     <%attr= "datinizioda";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "  Data Stipula da <strong>"+ valore +"</strong>; ";
       }%>
     <%attr= "datinizioa";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "Data Stipula a <strong>"+ valore +"</strong>; ";
       }%>
     <%attr= "scadconfda";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "Data Scadenza Conferma da <strong>"+ valore +"</strong>; ";
       }%>
     <%attr= "scadconfa";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "Data Scadenza Conferma a <strong>"+ valore +"</strong>; ";
       }%>
     <%attr= "descCPI_H";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += " Cpi comp.:<strong>" + valore + "</strong>; ";
       }%>
       <%attr= "dataFineAttoDa";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += " Data Fine Atto da <strong>" + valore + "</strong>; ";
       }%>
       <%attr= "dataFineAttoA";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += " Data Fine Atto a <strong>" + valore + "</strong>; ";
       }%>
       <%attr= "motSelected_H";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += " Motivo fine atto <strong>" + valore + "</strong>; ";
       }%>

      
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
	<af:list moduleName="ListaPattoLav" canDelete="<%= canDelete ? \"1\" : \"0\" %>" />
</af:form>
  <!-- <center><input class="pulsante" type = "button" name="torna" value="Torna alla Ricerca" onclick="TornaAllaRicerca()"/></center> -->

<table width="100%">

	   <tr><td class="campo2" colspan=3>N.B.:  Il pulsante di dettaglio permette all'operatore di entrare nel menù contestuale del lavoratore e di arrivare 
	   										   nella videata corrispondente alla visualizzazione/gestione degli atti amministrativi oggetto della ricerca. 
											   La videata mostrata é però sempre quella corrente anche se si parte da un'informazione storicizzata.
		</td></tr>	    
</table>

</body>
</html>
