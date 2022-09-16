<%@ page contentType="text/xml;charset=utf-8"%><%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
   
  it.eng.sil.util.*, 
  it.eng.afExt.utils.StringUtils,
  java.lang.*,
  java.text.*, 
  java.util.*,
  java.math.*,
  it.eng.sil.security.*"%><%@ include file="../global/noCaching.inc" %><%@ include file="../global/getCommonObjects.inc"%><%@ page 
  extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %><%@ taglib uri="aftags" prefix="af"%><%
	boolean esito = false;
    SourceBean row = (SourceBean) serviceResponse.getAttribute("ListaAvviamentoSelettiva.ROWS.ROW");
    if (row != null) {
    	esito = true;
    	String xml = row.toXML(false);
    }
%><%=(row != null ? row.toXML(false) : "<?xml version='1.0' encoding='utf-8'?><error/>")%>