<%@ page import="com.engiweb.framework.base.*, java.io.*,it.eng.sil.bean.*,it.eng.sil.security.User"
%><%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" 
%><%@ include file="../global/noCaching.inc" %><%@ include file="../global/getCommonObjects.inc" %>
			
<%			
			String asAttachment = "true";
			
			java.io.File theFile = (java.io.File) sessionContainer.getAttribute(MobilitaExpThread.THE_ZIP_FILE);
			String filename = (String) sessionContainer.getAttribute(MobilitaExpThread.THE_ZIP_FILE_NAME);
			
			String contentDisposition = "";
			if ((asAttachment != null) && asAttachment.equals("true")) {
				contentDisposition = "attachment;";
			}
    		    		
    		response.reset();
    		response.setContentType("application/zip");
    			
    		response.setHeader("Expires", "0");	
    		response.setHeader("Cache-Control","PUBLIC");
    		//response.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
    		response.setHeader("Pragma","public"); 
    		
			response.setHeader ("Content-Disposition", contentDisposition + "filename=" + filename+  ";");

			BufferedInputStream fif = new BufferedInputStream(new FileInputStream(theFile));
			
			int data;
			while ((data = fif.read()) != -1) {
				out.write(data);
			}
			
			
			out.flush();	
			fif.close();
			
			theFile.delete();
			
			sessionContainer.delAttribute(MobilitaExpThread.THE_ZIP_FILE);
%>
