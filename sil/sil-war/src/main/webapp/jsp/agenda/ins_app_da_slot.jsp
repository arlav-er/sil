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


String data_cod = StringUtils.getAttributeStrNotNull(serviceRequest,"DATA_COD");
%>

<%
String codCpi = StringUtils.getAttributeStrNotNull(serviceRequest, "codParCpi");
int cdnUt = user.getCodut();
String cdnParUtente = Integer.toString(cdnUt);
%>

<%
String moduleName = "MIns_AppDaSlot";
SourceBean content = (SourceBean) serviceResponse.getAttribute(moduleName);
SourceBean row = (SourceBean) content.getAttribute("ROW");
int CodiceRit = Integer.parseInt(row.getAttribute("CodiceRit").toString());
if(CodiceRit == -1) { CodiceRit = 7; }
String prgAppuntamento = StringUtils.getAttributeStrNotNull(row, "PRGAPPUNTAMENTO");
String codCpiAppuntamento = StringUtils.getAttributeStrNotNull(row, "CODCPIAPPUNTAMENTO");

String errMsg[] = { "Inserimento appuntamento da slot avvenuto correttamente",
                    "L'inserimento non è avvenuto in quanto per lo slot non sono stati prenotati azienda e/o lavoratori.",
                    "Si è verificato un errore durante l'accesso allo slot.",
                    "L'inserimento non è avvenuto in quanto lo slot si trova in uno stato non ammissibile per la prenotazione.",
                    "Si è verificato un errore durante l'accesso allo slot.",
                    "L'inserimento non è avvenuto in quanto è stato raggiunto il numero massimo di prenotazioni per le aziende",
                    "L'inserimento non è avvenuto in quanto è stato raggiunto il numero massimo di prenotazioni per i lavoratori",
                    "Errore nell'esecuzione della procedura."
                  };

                  
%>
<%
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
  <title>Inserimento Appuntamento da Slot</title>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <script type="text/javascript">
    function procedi(page)
    {
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;

      document.frm.PAGE.value = page;
      doFormSubmit(document.frm);
    }

    function controllaRit(i)
    {
      if(i==0) { procedi("SELECT_AGENDA_PAGE"); }
      //else { procedi("PCalendario"); }
    }
  </script>
</head>

<body class="gestione" onload="controllaRit(<%=CodiceRit%>)">
<af:form name="frm" action="AdapterHTTP" method="POST" dontValidate="true">
  <input name="PAGE" type="hidden" value=""/>
  <input name="PRGAPPUNTAMENTO" type="hidden" value="<%=prgAppuntamento%>"/>
  <input name="CODCPI" type="hidden" value="<%=codCpiAppuntamento%>"/>
  <!-- 21/06/2004 - Stefy - campi aggiunti a causa mancato allineamento -->
  <input type="hidden" name="ALLINEA_SLOT" value="1">
  <input type="hidden" name="OLDSTATO" value="5">
  <!-- // end 21/06/2004-->
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
  <input name="DATA_COD" type="hidden" value="<%=data_cod%>"/>
</af:form>
<%if(CodiceRit!=0){%>
      <br>
      <p class="titolo">Inserimento Appuntamento da Slot</p>
      <%out.print(htmlStreamTop);%>
      <table class="main">
      <tr>
        <td align="justify"><%=errMsg[CodiceRit]%></td>
      </tr>
      <tr><td>&nbsp;</td></tr>
      <tr>
        <td align="center">
        <input type="button" class="pulsanti" onClick="procedi('PCalendario')" value="Continua"/>
        </td>
      </tr>
      </table>
      <%out.print(htmlStreamBottom);%>
<%} //else {%>

        <!--input type="button" class="pulsanti" onClick="procedi('SELECT_AGENDA_PAGE')" value="Continua"/-->
<%//}%>
</body>
</html>
