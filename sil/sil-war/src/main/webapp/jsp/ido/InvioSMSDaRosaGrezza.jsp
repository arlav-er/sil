<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>
<HEAD>
<%@ page 
language="java"
contentType="text/html; charset=utf-8"
pageEncoding="utf-8"
%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>


<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.security.ProfileDataFilter,                     
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  java.text.*,
                  it.eng.afExt.utils.*"
%> 

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%	
	String error = (String) serviceResponse.getAttribute("M_IDO_SMS_INVIO.SMS_NONINVIATI.ERROR");	
%>


<META http-equiv="Content-Type" content="text/html; charset=utf-8">
<META name="GENERATOR" content="IBM WebSphere Studio">
<TITLE>InvioSMSDaRosaGrezza</TITLE>
<SCRIPT LANGUAGE="JavaScript"><!--
	//Aggiorna la pagina sottostante alla pop-up, ma solo se contiene la funzione aggiorna
	function updateMain() {
		if (window.opener != null && window.opener.aggiorna != null) {
			var err = "<%=error%>";			
			window.opener.document.frm.ERROR.value = err;
			window.opener.aggiorna();
			self.close();
		}
	}
//-->
</SCRIPT>
</HEAD>
<BODY onload="updateMain()">
<P>pagina di risposta all'invio degli SMS </P>

</BODY>
</HTML>
