<!-- @author: Stefania Orioli -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,it.eng.afExt.utils.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.User,
                it.eng.sil.util.*"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
String giornoDB = "";
String meseDB = "";
String annoDB = "";
String nrosDB = "";
String giorno = "";
String mese = "";
String anno = "";
String cod_vista = "";
int mod = 0;

if(serviceRequest.containsAttribute("giorno")) { giorno = serviceRequest.getAttribute("giorno").toString(); }
if(serviceRequest.containsAttribute("mese")) { mese = serviceRequest.getAttribute("mese").toString(); }
if(serviceRequest.containsAttribute("anno")) { anno = serviceRequest.getAttribute("anno").toString(); }
if(serviceRequest.containsAttribute("nrosDB")) { nrosDB = serviceRequest.getAttribute("nrosDB").toString(); }
if(serviceRequest.containsAttribute("giornoDB")) { giornoDB = serviceRequest.getAttribute("giornoDB").toString(); }
if(serviceRequest.containsAttribute("meseDB")) { meseDB = serviceRequest.getAttribute("meseDB").toString(); }
if(serviceRequest.containsAttribute("annoDB")) { annoDB = serviceRequest.getAttribute("annoDB").toString(); }
if(serviceRequest.containsAttribute("cod_vista")) { cod_vista = serviceRequest.getAttribute("cod_vista").toString(); }
if(serviceRequest.containsAttribute("MOD")) { mod = Integer.parseInt(serviceRequest.getAttribute("MOD").toString()); }
%>

<%
String codCpi = StringUtils.getAttributeStrNotNull(serviceRequest, "CODCPI");
int cdnUt = user.getCodut();
String cdnParUtente = Integer.toString(cdnUt);
%>

<%
String moduleName = "MINS_MASSIVO_SLOT";
SourceBean content = (SourceBean) serviceResponse.getAttribute(moduleName);
SourceBean row = (SourceBean) content.getAttribute("ROW");
int CodiceRit = Integer.parseInt(row.getAttribute("CodiceRit").toString());
if(CodiceRit == -1) { CodiceRit = 6; }

String errMsg[] = { "Inserimento slot avvenuto correttamente",
                    "L'inserimento degli slot non è avvenuto in quanto la data di inizio e la data di fine inserite non sono compatibili con le rispettive date di validità della settimana tipo scelta",
                    "L'inserimento degli slot non è avvenuto in quanto la settimana tipo scelta risulta incompleta o inconsistente. Potrebbe mancare uno dei seguenti elementi: Servizio, Destinatario, Prenotabile, Dalle, Fino alle, Minuti, Quantità oppure i dati: Dalle, Fino alle, Minuti, Quantità potrebbero non essere coerenti fra loro",
                    "L'inserimento degli slot non è avvenuto in quanto nel periodo scelto esistono slot che si sovrappongono per operatore e per servizio a quelli definiti nella settimana tipo",
                    "L'inserimento degli slot non è avvenuto in quanto nel periodo scelto esistono slot che si sovrappongono per operatore a quelli definiti nella settimana tipo",
                    "L'inserimento degli slot non è avvenuto in quanto nel periodo scelto esistono slot che si sovrappongono per servizio a quelli definiti nella settimana tipo",
                    "Errore nell'esecuzione della procedura."
                  };

                  
%>


<html>
<head>
  <title>Inserimento Slot da Settimana Tipo</title>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <script type="text/javascript">
    function procedi()
    {
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;

      doFormSubmit(document.frm);
    }

    function controllaRit(i)
    {
      if(i==0) { procedi(); }
    }
  </script>
</head>
<%
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<body class="gestione" onload="controllaRit(<%=CodiceRit%>)">
<af:form name="frm" action="AdapterHTTP" method="POST" dontValidate="true">
  <input name="PAGE" type="hidden" value="GestSlotPage"/>
  <% if(mod==0) {%>
        <input name="giornoDB" type="hidden" value="<%=giornoDB%>"/>
        <input name="meseDB" type="hidden" value="<%=meseDB%>"/>
        <input name="annoDB" type="hidden" value="<%=annoDB%>"/>
  <% } else { %>
        <% if(mod==1) { %>
            <input name="nrosDB" type="hidden" value="<%=nrosDB%>"/>
            <input name="annoDB" type="hidden" value="<%=annoDB%>"/>
        <% } else {%>
            <%if(mod==2) { // Ricerca precedente%>
              <input name="sel_operatore" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"sel_operatore")%>"/>
              <input name="sel_servizio" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"sel_servizio")%>"/>
              <input name="sel_aula" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"sel_aula")%>"/>
              <input name="dataDal" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"dataDa")%>"/>
              <input name="dataAl" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"dataAl")%>"/>
            <%}%>
        <% } %>
  <% } %>
  <input name="giorno" type="hidden" value="<%=giorno%>"/>
  <input name="mese" type="hidden" value="<%=mese%>"/>
  <input name="anno" type="hidden" value="<%=anno%>"/>
  <input name="MOD" type="hidden" value="<%=mod%>"/>
  <input name="cod_vista" type="hidden" value="<%=cod_vista%>"/>
</af:form>
<%if(CodiceRit!=0){%>
      <br>
      <p class="titolo">Inserimento Slot da Settimana Tipo</p>
      <%out.print(htmlStreamTop);%>
      <table class="main">
      <tr>
        <td align="justify"><%=errMsg[CodiceRit]%></td>
      </tr>
      <tr><td>&nbsp;</td></tr>
      <tr>
        <td align="center">
        <input type="button" class="pulsanti" onClick="procedi()" value="Continua"/>
        </td>
      </tr>
      </table>
      <%out.print(htmlStreamBottom);%>
<%}%>

</body>
</html>
