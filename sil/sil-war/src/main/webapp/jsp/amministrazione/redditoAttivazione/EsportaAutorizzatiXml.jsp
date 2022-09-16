<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                java.io.*,com.engiweb.framework.error.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*, it.eng.sil.bean.*, it.eng.sil.module.amministrazione.redditoAttivazione.*" %>
 
<%@ taglib uri="aftags" prefix="af" %> 

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ include file="../../global/noCaching.inc" %><%@ include file="../../global/getCommonObjects.inc" %>

<%
String asAttachment = "true";
ResponseContainer responseCont = ResponseContainerAccess.getResponseContainer(request);
SourceBean actionResponseBean = responseCont.getServiceResponse();
int numErrors = responseCont.getErrorHandler().getErrors().size();

if (numErrors > 0) {
	%>
	<html>
	<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link rel="stylesheet" href="../../css/stili.css" type="text/css">
		<af:showErrors />
	</head>
	</html>
	<% 
} 
else if (actionResponseBean.getAttribute("fileListaAutorizzati") != null) {

	java.io.File theFile = new File((String)actionResponseBean.getAttribute("fileListaAutorizzati"));  
	
	String contentDisposition = "";
	if ((asAttachment != null) && asAttachment.equals("true")) {
		contentDisposition = "attachment;";
	}

	response.reset();
	response.setContentType("application/zip");
		
	response.setHeader("Expires", "0");	
	response.setHeader("Cache-Control","PUBLIC");
	response.setHeader("Pragma","public"); 
	response.setHeader ("Content-Disposition", contentDisposition + "filename=" + theFile.getName()+  ";");
	
	BufferedInputStream fif = new BufferedInputStream(new FileInputStream(theFile));
	
	int data;
	while ((data = fif.read()) != -1) {
		
		out.write(data);
	}
	
	
	out.flush();	
	fif.close();
	
	theFile.delete();
}
%>     
	
	