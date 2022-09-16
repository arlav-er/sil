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
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
   Vector rows = null;
  
   rows = serviceResponse.getAttributeAsVector("M_RicercaComuneStato.ROWS.ROW"); 

   String codComune  = ""; 
   String nomeComune = ""; 
   String provincia  = ""; 
   String capComune  = "";
   String esito      = null;
   String codcpi     = "";
   
   if(rows.size() == 1)
   { 
   	 SourceBean row = (SourceBean) rows.get(0);
     codComune  = (String) row.getAttribute("codcom");
     nomeComune = (String) row.getAttribute("strdenominazione");
     provincia  = (String) row.getAttribute("stristat");
     capComune  = (String) row.getAttribute("strcap");
     codcpi     = StringUtils.getAttributeStrNotNull(row,"CODCPI");
     esito      = "success";
   }
   else {
     esito = "error";
   }
%>

<?xml version="1.0" encoding="utf-8"?>
<comune esito="<%=esito%>">
  <nome><%=nomeComune%></nome>
  <codice><%=codComune%></codice>
  <cap><%=capComune%></cap>
  <provincia><%=provincia%></provincia>
  <codcpi><%=codcpi%></codcpi>
</comune>
