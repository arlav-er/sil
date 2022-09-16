<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file ="../global/noCaching.inc" %>
<%@ page  import=" 
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.security.*" %>
                  
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
 String username = "";
 String numPratica = "";
 String  onLoadJs = "";
 
 boolean checkConvenzione = false;
 String flgLoginConvenzione = "N";
 SourceBean serviceResponse = null;
 ResponseContainer responseContainer = ResponseContainerAccess.getResponseContainer(request);

 if (responseContainer != null){
   serviceResponse = responseContainer.getServiceResponse();
   flgLoginConvenzione = (String) serviceResponse.getAttribute("loginConvenzione.FLG_LOGIN_CONVENZIONE");  
 }
 
 if ("S".equalsIgnoreCase(flgLoginConvenzione)) {
	 checkConvenzione = true;
 }
 
 %>

 
<%@ include file ="loginConvenzione.inc" %>

