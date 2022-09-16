<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
   
  it.eng.sil.util.*, 
  it.eng.afExt.utils.StringUtils,
  java.lang.*,
  java.text.*, 
  java.util.*,
  java.math.*,
  it.eng.sil.security.*"%>

<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
 // NOTE: Attributi della pagina (pulsanti e link) 
//  PageAttribs attributi = new PageAttribs(user, "AnagMainPage");
//  boolean canModify = attributi.containsButton("nuovo");

 //boolean canInsert=attributi.containsButton("INSERISCI");

  Object codiceUtente= sessionContainer.getAttribute("_CDUT_");

  String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
  String prgElencoGiornale=(String) serviceRequest.getAttribute("PRGELENCOGIORNALE");
  String codGiornale=(String) serviceRequest.getAttribute("CODGIORNALE");
  String datInizioSett=(String) serviceRequest.getAttribute("DATINIZIOSETT");
  String datFineSettimana=(String) serviceRequest.getAttribute("DATFINESETTIMANA");  
  PageAttribs attributi = new PageAttribs(user, "IdoPubbRicercaPage");
  boolean canInsert=attributi.containsButton("INSERISCI");
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
  String retRicerca = (String) StringUtils.getAttributeStrNotNull(serviceRequest,"RETRICERCA");
  
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>AssociaPubblicazione.jsp</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>

<%@ include file="../global/Function_CommonRicercaComune.inc"%>
<%@ include file="../anag/Function_CommonRicercaCPI.inc" %>
<script language="Javascript">
     <% 
      //Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
</script>

<script>
function chiudi() {
  	if (isInSubmit()) return;
	var f = "AdapterHTTP?PAGE=IdoRicercaAssociaPubbPage";
 		 f = f + "&cdnFunzione=<%=_funzione%>";
		 f = f + "&CODGIORNALE=<%=codGiornale%>";
		 f = f + "&DATFINESETTIMANA=<%=datFineSettimana%>";
		 f = f + "&DATINIZIOSETT=<%=datInizioSett%>";
		 f = f + "&PRGELENCOGIORNALE=<%=prgElencoGiornale%>";
		 f = f + "&RETRICERCA=<%=retRicerca%>";
		 document.location = f;
	}	


</script>


</head>

<body class="gestione">
<p class="titolo">Ricerca pubblicazioni</p>

<%out.print(htmlStreamTop);%>
<af:form name="Frm1" method="GET" action="AdapterHTTP" >
<input type="hidden" name="PAGE" value="IdoListaPubbGiorListaPage"/>
<input type="hidden" name="cdnFunzione" value="<%=_funzione%>" />
<input type="hidden" name="CDNUT" value="<%= codiceUtente %>"/>
<input type="hidden" name="ASSOCIA" value="S"/>
<input type="hidden" name="PRGELENCOGIORNALE" value="<%=prgElencoGiornale%>"/>
<input type="hidden" name="CODGIORNALE" value="<%=codGiornale%>"/>
<input type="hidden" name="DATINIZIOSETT" value="<%=datInizioSett%>"/>
<input type="hidden" name="DATFINESETTIMANA" value="<%=datFineSettimana%>"/>
<input type="hidden" name="RETRICERCA" value="<%=retRicerca%>"/>


<br/>
<p align="center">
<table class="main"> 

<%
int numCheck=0;
Vector rows = serviceResponse.getAttributeAsVector("M_IDOLISTADAPUBBLICARE.ROWS.ROW");
Enumeration _enum = rows.elements();
if (_enum.hasMoreElements()){
%>
   <table class="LISTA">
    <tr>
      <td align=right colspan="6">
        <center>
          <font color="red">
            <af:showErrors/>
          </font>
        </center>
      </td>
    </tr>
   
      <tr class="LISTA">
        <th class="LISTA">&nbsp</th>
        <th class="LISTA">Numero/Anno</th>
        <th class="LISTA">Ragione sociale</th>
        <th class="LISTA">Data pubblicazione</th>
        <th class="LISTA">Scad Pubblicazione</th>        
     </tr>

<%  int i=0;
    String _stile= null;
    String prgRichiestaAz;
    while ( _enum.hasMoreElements())
    {
      SourceBean row = (SourceBean)_enum.nextElement();
      _stile="LISTA" + (i%2); i++;
      prgRichiestaAz=row.getAttribute("PRGRICHIESTAAZ").toString();
%>
      <tr class="<%=_stile%>">
          <td class="<%=_stile%>">
            <input type="checkbox" name="CHK_PUBB<%=i%>" value="<%=prgRichiestaAz%>">
          </td>
          <td class="<%=_stile%>">
            <%=it.eng.sil.util.Utils.notNull(row.getAttribute("NUMRICHIESTA")) + "/" + it.eng.sil.util.Utils.notNull(row.getAttribute("NUMANNO"))%>
          </td>
          <td class="<%=_stile%>">
            <%=it.eng.sil.util.Utils.notNull(row.getAttribute("STRRAGIONESOCIALE"))%>
          </td>
          <td class="<%=_stile%>">
            <%=it.eng.sil.util.Utils.notNull(row.getAttribute("DATPUBBLICAZIONE")) %>
          </td>
          <td class="<%=_stile%>">
            <%=it.eng.sil.util.Utils.notNull(row.getAttribute("DATSCADENZAPUBBLICAZIONE")) %>
          </td>          
      </tr>
    <%
    } // while
    numCheck=i;
    %>
    </table>
<%} else {%>
  <P class="LISTAINFO"><CENTER><B>Nessuna pubblicazione associabile alla lista selezionata</B></CENTER></P>
<%
} // if - else
%>

<br><br>

<table><tr><td>
<center>
<input class="pulsanti" type="submit" name="Inserisci" value="Aggiungi"/>
<input class="pulsante" type="button" name="back" value="Chiudi" onclick="chiudi();"/>
</center>
</td></tr></table>

<input type="hidden" name="NUMCHK" value="<%=numCheck%>"/>
</af:form>
<%out.print(htmlStreamBottom);%>
</body>
</html>
