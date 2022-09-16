<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file ="../global/noCaching.inc" %>
<%@ page  import="java.util.*, 
                  java.text.*, 
                   
                  com.engiweb.framework.base.*,
                  it.eng.sil.util.*,
                  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

 
<%

    String onLoadJs = "";
    String esitoLogin = null;
    String esitoLoginMsg = "";
    String username = "";
    
    boolean checkConvenzione = false;
    String flgLoginConvenzione = "N";
    
    SourceBean serviceResponse = null;
    ResponseContainer responseContainer = ResponseContainerAccess.getResponseContainer(request);

    if (responseContainer != null){
      serviceResponse = responseContainer.getServiceResponse();
      esitoLogin = (String) serviceResponse.getAttribute("eseguiLogin.esito_login");
      esitoLoginMsg = serviceResponse.getAttribute("eseguiLogin.esito_login_msg") == null ? "" : (String) serviceResponse.getAttribute("eseguiLogin.esito_login_msg");
      
      serviceResponse = responseContainer.getServiceResponse();
	  flgLoginConvenzione = (String) serviceResponse.getAttribute("eseguiLogin.FLG_LOGIN_CONVENZIONE");  
      
     }
 
   	 if ("S".equalsIgnoreCase(flgLoginConvenzione)) {
   		 checkConvenzione = true;
   	 }
    
    
    if (esitoLogin == null){
            // Prima volta
            %><%@ include file ="login.inc" %><% 

    }else if ((esitoLogin != null)  && (esitoLogin.equalsIgnoreCase("KO")) ){
          // Autenticazione fallita
          username = (String) serviceResponse.getAttribute("eseguiLogin.req_username");
          onLoadJs = "apriLookupErrAut('KO', '"+esitoLoginMsg+"');";
           %><%@ include file ="login.inc" %><% 
    }else if ((esitoLogin != null)  && (esitoLogin.equalsIgnoreCase("BLOCCATO")) ){
          // Account bloccato
          username = "";
          onLoadJs = "apriLookupErrAut('BLOCCATO', '');";
           %><%@ include file ="login.inc" %><% 
    }else if ((esitoLogin != null)  && (esitoLogin.equalsIgnoreCase("NON_ANCORA_VALIDO")) ){
          // Account non ancora valido
          username = "";
          onLoadJs = "apriLookupErrAut('NON_ANCORA_VALIDO', '');";
           %><%@ include file ="login.inc" %><%    
    }else if ((esitoLogin != null)  && (esitoLogin.equalsIgnoreCase("SCADUTO")) ){
          // Account scaduto
          username = "";
          onLoadJs = "apriLookupErrAut('SCADUTO', '');";
           %><%@ include file ="login.inc" %><%    
    }else if ((esitoLogin != null)  && (esitoLogin.equalsIgnoreCase("PWD_SCADUTA")) ){
          // password scaduta
          
           onLoadJs = "apriAppl('cambioPwdPAGE');";
           %><%@ include file ="login.inc" %><%
     } else {
          // Tutto OK
          // Redirezione in corso...
          onLoadJs = "apriAppl('framesetPAGE');";
           %><%@ include file ="login.inc" %><%
   }
%>

