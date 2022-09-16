<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                java.io.*,com.engiweb.framework.error.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*, it.eng.sil.bean.*, it.eng.sil.action.report.amministrazione.*" %><%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %><%@ include file="../global/noCaching.inc" %><%@ include file="../global/getCommonObjects.inc" %><%!public String getContentType(String filename) {

	String suffix = null;
	int puntoIdx = filename.lastIndexOf(".");
	if (puntoIdx > 0) {
		suffix = filename.substring(puntoIdx + 1).toLowerCase();
	}

	if (suffix == null) {
		return "application/octet-stream";
	} else if (suffix.equals("txt")) {
		return "text/plain";
	} else if (suffix.equals("pdf")) {
		return "application/pdf";
	} else if (suffix.equals("xml")) {
		return "text/xml";
	} else if (suffix.equals("html")) {
		return "text/html";
	} else if (suffix.equals("htm")) {
		return "text/html";
	} else if (suffix.equals("gif")) {
		return "image/gif";
	} else if (suffix.equals("jpg")) {
		return "image/jpeg";
	} else if (suffix.equals("jpeg")) {
		return "image/jpeg";
	} else if (suffix.equals("doc") || suffix.equals("rtf")) {
		return "application/vnd.ms-word";
	} else if (suffix.equals("xls") || suffix.equals("cvs")) {
		return "application/vnd.ms-excel";
	} else {
		return "application/octet-stream";
	}

}

static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger("it.eng.sil._jsp.download.jsp");

%><%

	String asAttachment = "true";
	ResponseContainer responseCont = ResponseContainerAccess.getResponseContainer(request);
	SourceBean responseBean = responseCont.getServiceResponse();
	String filename = (responseBean != null && responseBean.getAttribute("M_MOBILITARICERCASAVECSV.fileExportIscrMobCsv") != null) ? 
			responseBean.getAttribute("M_MOBILITARICERCASAVECSV.fileExportIscrMobCsv").toString() : 
			"";
	String todayString = (new SimpleDateFormat("yyyyMMdd")).format(new Date());
	String outputFilename = "ListaMobilita_" + todayString + ".csv";
	
	int numErrors = responseContainer.getErrorHandler().getErrors().size();
	
	if (filename != null && numErrors == 0) {
		
		java.io.File theFile = new File(filename);  
		
		String contentDisposition = "";
		if ((asAttachment != null) && asAttachment.equals("true")) {
			contentDisposition = "attachment;";
		}
				
		response.reset();
		//response.setContentType("application/zip");
			
		response.setHeader("Expires", "0");	
		response.setHeader("Cache-Control","PUBLIC");
		//response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
		response.setHeader("Pragma","public"); 
		
		response.setContentType(getContentType(theFile.getAbsolutePath()));
		response.setHeader("Content-Disposition", contentDisposition + "filename=" + outputFilename+  ";");
		
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