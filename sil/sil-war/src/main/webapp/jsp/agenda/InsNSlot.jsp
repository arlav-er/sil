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
String giornoDB = StringUtils.getAttributeStrNotNull(serviceRequest, "giornoDB");
String meseDB = StringUtils.getAttributeStrNotNull(serviceRequest, "meseDB");
String annoDB = StringUtils.getAttributeStrNotNull(serviceRequest, "annoDB");
String nrosDB = StringUtils.getAttributeStrNotNull(serviceRequest, "nrosDB");
String giorno = StringUtils.getAttributeStrNotNull(serviceRequest, "giorno");
String mese = StringUtils.getAttributeStrNotNull(serviceRequest, "mese");
String anno = StringUtils.getAttributeStrNotNull(serviceRequest, "anno");
String cod_vista = StringUtils.getAttributeStrNotNull(serviceRequest, "cod_vista");
int mod = 0;
if(serviceRequest.containsAttribute("MOD")) { mod = Integer.parseInt(serviceRequest.getAttribute("MOD").toString()); }

String codCpi = StringUtils.getAttributeStrNotNull(serviceRequest, "CODCPI");
int cdnUt = user.getCodut();
String cdnParUtente = Integer.toString(cdnUt);
%>

<%
String moduleName = "MInsMultiploSlot";
SourceBean content = (SourceBean) serviceResponse.getAttribute(moduleName);
SourceBean row = (SourceBean) content.getAttribute("ROW");
int CodiceRit = Integer.parseInt(row.getAttribute("CodiceRit").toString());
String errMsg = "";
if(CodiceRit == -1) { errMsg = "Errore nell'esecuzione della procedura."; }
if(CodiceRit == 1) { errMsg = "Non tutti gli inserimenti sono stati portati a termine a causa di sovrapposizioni con slot già presenti."; }                 
String oraErrMsg = StringUtils.getAttributeStrNotNull(row, "strMessOut"); 
%>


<html>
<head>
  <title>Inserimento Multiplo Slot</title>
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
      <p class="titolo">Inserimento Multiplo Slot</p>
      <%out.print(htmlStreamTop);%>
      <table class="main">
      <tr>
        <td align="justify">
        	<%=errMsg%>
        	<br><%=oraErrMsg%>
        </td>
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
