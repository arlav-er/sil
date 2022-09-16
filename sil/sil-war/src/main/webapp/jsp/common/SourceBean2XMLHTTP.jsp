<%@ page contentType="text/xml;charset=utf-8"%><%@ page import="
  com.engiweb.framework.base.*, it.eng.sil.security.*"%><%@ include file="../global/noCaching.inc" %><%@ include file="../global/getCommonObjects.inc"%><%@ page 
  extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %><%@ taglib uri="aftags" prefix="af"%><%
	boolean esito = false;
	//SourceBean  resp = (SourceBean) serviceResponse;
    if (serviceResponse != null) {
    	esito = true;
    }
%><%=(serviceResponse != null ? serviceResponse.toXML(false) : "<?xml version='1.0' encoding='utf-8'?><error/>")%>
