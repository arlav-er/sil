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
  // NOTE: Attributi della pagina (pulsanti e link)
  //PageAttribs attributi = new PageAttribs(user, "StatoOccRisultRicercaPage");
  boolean canDelete=true;//attributi.containsButton("aggiorna");
  //E' stato disabilitato il pulsante di cancellazione sulla lista (momentaneamente)
  canDelete=false;
  
  String cognome=serviceRequest.getAttribute("cognome").toString();
  String nome=serviceRequest.getAttribute("nome").toString();
  String CF=serviceRequest.getAttribute("CF").toString();
  String didRilasciata=serviceRequest.getAttribute("didRilasciata").toString();
  String codStatoOcc=serviceRequest.getAttribute("codStatoOcc").toString();
  String viewNonPresentati=serviceRequest.getAttribute("viewNonPresentati").toString();
  String NumVol=serviceRequest.getAttribute("NumVol").toString();
  String dataNP=serviceRequest.getAttribute("dataNP").toString();
  String categoria181=serviceRequest.containsAttribute("categoria181")?serviceRequest.getAttribute("categoria181").toString():"";
  /*   
			Commento aggiunto per il congelamento della versione in fase 3
   		    Cosimo Togna 28/04/2005
  String legge407_90=serviceRequest.getAttribute("legge407_90").toString();
  String lungaDur=serviceRequest.getAttribute("lungaDur").toString();
	
	*/

  String revRic=serviceRequest.getAttribute("revRic").toString();

  String _funzione=serviceRequest.getAttribute("CDNFUNZIONE").toString();
%>

<html>
<head>
 <title>Inf Storiche Stato Occupazionale</title>
 <link rel="stylesheet" type="text/css" href=" ../../css/stili.css"/>
 <link rel="stylesheet" type="text/css" href=" ../../css/listdetail.css"/>

<af:linkScript path="../../js/"/>
<script type="text/JavaScript">
function openPage(pagina,parametri)
{ //alert(pagina+"\r\n"+parametri);
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;

  setWindowLocation("AdapterHTTP?PAGE="+pagina +parametri);
}
</script>

</head>

<body onload="checkError();rinfresca();rinfresca_laterale();" class="gestione">
<af:error/>
<br/><p class="titolo">RISULTATO DELLA RICERCA SULLO STATO OCCUPAZIONALE</p>

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
     <%attr= "descStatoOcc_H";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "stato occupazionale <strong>"+ valore +"</strong>; ";
       }%>
     <%attr= "categoria181";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       {txtOut += "categoria dlg. 181:<strong>"+ valore +"</strong>; ";
       }%>
     <%  /*   
			Commento aggiunto per il congelamento della versione in fase 3
   		    Cosimo Togna 28/04/2005
     
       attr= "legge407_90";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       { if(valore.equalsIgnoreCase("S")){txtOut += " legge 407/90:<strong>Sì</strong>;";}
         else {txtOut += " legge 407/90:<strong>No</strong>;";}
       }
       	*/
       %>
     <%   /*   
			Commento aggiunto per il congelamento della versione in fase 3
   		    Cosimo Togna 28/04/2005
     
       attr= "lungaDur";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       { if(valore.equalsIgnoreCase("S")){txtOut += "disoccupato/inoccupato di lunga durata:<strong>Sì</strong>; ";}
         else {txtOut += " disoccupato/inoccupato di lunga durata:<strong>No</strong>;  ";}
       }
       	*/
       %>
     <%attr= "revRic";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       { if(valore.equalsIgnoreCase("S")){txtOut += " Revisione/Ricorso<strong>Sì</strong>; ";}
         else {txtOut += " Revisione/Ricorso<strong>No</strong>; ";}
       }%>
       
     <%attr= "didRilasciata";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       { if(valore.equalsIgnoreCase("S")){txtOut += " DID rilasciata:<strong>Sì</strong>; ";}
         else {txtOut += " DID rilasciata:<strong>No</strong>; ";}
       }%>
       <%attr= "viewNonPresentati";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       { if(valore.equalsIgnoreCase("S")){txtOut += " Soggetti convocati non presentati:<strong>Sì</strong>; ";}
         else {txtOut += " Soggetti convocati non presentati:<strong>No</strong>; ";}
       }%>
       <%attr= "numVol";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       { txtOut += " Num. volte non presentato:<strong>" + valore + "</strong>; ";
       }%>

       <%attr= "dataNP";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       { txtOut += " Dalla data:<strong>" + valore + "</strong>; ";
       }%>
       <%attr= "descCPI_H";
       valore = (String) serviceRequest.getAttribute(attr);
       if(valore != null && !valore.equals(""))
       { txtOut += " Cpi comp.:<strong>" + valore + "</strong>; ";
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

<af:list moduleName="M_StatoOccRicerca" canDelete="<%= canDelete ? \"1\" : \"0\" %>" getBack="true"/>

<table class="main">
  <tr><td>&nbsp;</td></tr>
  <tr><td align="center"><input type="button" class="pulsanti" value="Torna alla ricerca" onClick="openPage('StatoOccRicercaPage','&CDNFUNZIONE=<%=_funzione%>')"></td></tr>
</table>

</af:form>

</body>
</html>