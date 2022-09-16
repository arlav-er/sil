<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
			com.engiweb.framework.util.JavaScript,
			java.text.SimpleDateFormat,
			java.util.*,
			java.io.*,
			java.math.*,
			it.eng.sil.security.*,
			it.eng.sil.util.*,
			it.eng.sil.bean.*,
			it.eng.afExt.utils.*,
			it.eng.sil.Values,
			it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil,
			it.eng.sil.module.movimenti.constant.Properties,
			it.eng.sil.module.trento.Consenso"
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>
	
	<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%
	String asAttachment = "true";
	String contentDisposition = "";
	
	String filename = (String)sessionContainer.getAttribute("FILE_TO_VIEW");
	
	if (filename != null) {
		
		java.io.File theFile = new File(filename);
		
		response.setContentType("application/pdf");
		response.setHeader ("Content-Disposition", contentDisposition + "filename=" + filename+  ";");

		OutputStream outStream = response.getOutputStream();
		BufferedInputStream fif = new BufferedInputStream(new FileInputStream(theFile));
		
		int data;
		while ((data = fif.read()) != -1) {
			outStream.write(data);
		} 
		
		//out.write("<html>ciao mondo!!!</html>");
		outStream.flush();	
		fif.close();
		
		theFile.delete();	
	}
%>