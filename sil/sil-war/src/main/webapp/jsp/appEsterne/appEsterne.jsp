
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
   
  it.eng.sil.util.*, 
  it.eng.afExt.utils.StringUtils,
  it.eng.sil.security.ProfileDataFilter,  
  java.lang.*,
  java.text.*, 
  java.util.*,
  java.math.*,
  it.eng.sil.security.* "
%>

<%
  	String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE"); 
  	String ID = (String) serviceRequest.getAttribute("ID");
%>

<%@ include file="../global/Function_CommonRicercaComune.inc" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%if(ID.equals("1") || ID.equals("2") || ID.equals("3") || ID.equals("4") || ID.equals("5") || ID.equals("6")){ %>
	<%@ include file="./lavoratore.inc" %>
<%}else if(ID.equals("7") || ID.equals("8") || ID.equals("9")){ %>
	<%@ include file="./azienda.inc" %>
<%}else if(ID.equals("10")){%>
	<%@ include file="./aziendaAnag.inc" %>
<%}%>





