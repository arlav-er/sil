<!-- @author: Stefania Orioli -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.User,
                it.eng.sil.util.*,
                it.eng.afExt.utils.*"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
// Debug
//String qs = QueryString.GetQueryString((SourceBean) serviceRequest);
%>

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

if(serviceRequest.containsAttribute("solo_miei")) { 
  sessionContainer.setAttribute("slot_FMiei", serviceRequest.getAttribute("solo_miei").toString()); 
} else {
  sessionContainer.setAttribute("slot_FMiei","");
}

if(serviceRequest.containsAttribute("fsel_servizio")) { 
  sessionContainer.setAttribute("slot_FServizio", serviceRequest.getAttribute("fsel_servizio").toString()); 
} else {
  sessionContainer.setAttribute("slot_FServizio","");
}

if(serviceRequest.containsAttribute("fsel_operatore")) { 
  sessionContainer.setAttribute("slot_FOperatore", serviceRequest.getAttribute("fsel_operatore").toString()); 
} else {
  sessionContainer.setAttribute("slot_FOperatore","");
}

if(serviceRequest.containsAttribute("fsel_aula")) { 
  sessionContainer.setAttribute("slot_FAula", serviceRequest.getAttribute("fsel_aula").toString()); 
} else {
  sessionContainer.setAttribute("slot_FAula","");
}
%>


<html>
<head>
  <title>Imposta Filtri Slot</title>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/" />
</head>

<body class="gestione">

<af:form action="AdapterHTTP" name="form" method="POST" dontValidate="true">
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
            <input name="dataDal" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"dataDal")%>"/>
            <input name="dataAl" type="hidden" value="<%=StringUtils.getAttributeStrNotNull(serviceRequest,"dataAl")%>"/>
          <%}%>
      <%}%>
<% } %>
<input name="giorno" type="hidden" value="<%=giorno%>"/>
<input name="mese" type="hidden" value="<%=mese%>"/>
<input name="anno" type="hidden" value="<%=anno%>"/>
<input name="cod_vista" type="hidden" value="<%=cod_vista%>"/>
<input name="MOD" type="hidden" value="<%=mod%>"/>

<p align="center">

</af:form>
<script language="Javascript">
doFormSubmit(document.form);
</script>
</body>
</html>
