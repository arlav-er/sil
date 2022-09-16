<%@page import="java.math.BigDecimal"%><%@page import="it.eng.afExt.utils.StringUtils"%><%@ page import="java.util.*,com.engiweb.framework.message.*,com.engiweb.framework.base.*, java.io.*,it.eng.sil.bean.*"%><%!public String getContentType(String filename) {

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
			RequestContainer requestContainer = RequestContainer.getRequestContainer();
			SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();			

			ResponseContainer responseContainer = ResponseContainerAccess.getResponseContainer(request);
			SourceBean actionResponseBean = responseContainer.getServiceResponse();
			
			Documento theDocument = null;
			String asAttachment = null;

			//if ( (actionResponseBean.containsAttribute("WARNINGREPORT") || requestContainer.getServiceRequest().containsAttribute("LEGGISESSIONE")) && 
			//	 (sessionContainer.getAttribute("theDocument_report") != null) ) {
			if ( requestContainer.getServiceRequest().containsAttribute("ricarica")){
				//theDocument = (Documento) sessionContainer.getAttribute("theDocument_report");
				//asAttachment = (String) sessionContainer.getAttribute("asAttachment_report");
				
				String prgDocumento = (String) requestContainer.getServiceRequest().getAttribute("prgDocumento");
				if(!StringUtils.isEmpty(prgDocumento)){
					BigDecimal prgD = new BigDecimal(prgDocumento);
					theDocument = new Documento(prgD);
					theDocument.selectExtBlob();
				}
				
				asAttachment = (String) requestContainer.getServiceRequest().getAttribute("asAttachment");
				
				//sessionContainer.delAttribute("theDocument_report");
				//sessionContainer.delAttribute("asAttachment_report");	
			}
			else {
				theDocument = (Documento) actionResponseBean.getAttribute("theDocument");
				asAttachment = (String) actionResponseBean.getAttribute("asAttachment");
			}
				
			java.io.File theFile = theDocument.getTempFile();
			String filename = theDocument.getStrNomeDoc();
			
			if (filename == null) {
				filename = "Documento1.tmp";
			}
			String contentDisposition = "";
			if ((asAttachment != null) && asAttachment.equals("true")) {
				contentDisposition = "attachment;";
			}
			
			if (theDocument.getTempFilePreProtocollo()!=null) {
				if (!theDocument.getTempFilePreProtocollo().delete()) {
					response.setContentType("text/html");
					String htmlStreamTop =    it.eng.sil.util.StyleUtils.roundTopTable(false);
					String htmlStreamBottom = it.eng.sil.util.StyleUtils.roundBottomTable(false);
					out.write("<html><link rel='stylesheet' href='../../css/stili.css' type='text/css'><body class='gestione'><br>");
					out.write(htmlStreamTop);
					out.write("<p>Impossibile cancellare il file temporaneo inviato a DOCAREA. <br>Contattare l'amministratore di sistema.</p>");
					out.write(htmlStreamBottom);
					out.write("</body></html>");
					_logger.fatal( "Impossibile cancellare il file PROTOCOLLATO inviato a DocArea: "+theDocument.getTempFilePreProtocollo().getAbsolutePath());
					_logger.fatal( "DATI REQUEST: "+ actionResponseBean.toXML());
					_logger.fatal( "DATI DOCUMENTO: "+theDocument.toString());
					_logger.fatal( "CONTATTARE L'AMMINISTRATORE DI SISTEMA");
					theFile.delete();
					return;
				}
			}


			ArrayList messageWarning = null;
			if (actionResponseBean.containsAttribute("WARNINGREPORT")) {

				messageWarning = (ArrayList) actionResponseBean.getAttribute("WARNINGREPORT"); 
				String messaggio = "";
				String brTag = "<br/> ";
				for(int i= 0;i<messageWarning.size();i++){
					
					if(i==0){
						messaggio = messaggio + MessageBundle.getMessage(messageWarning.get(i).toString());
					} else{
						messaggio = brTag + messaggio + MessageBundle.getMessage(messageWarning.get(i).toString());
					}
					
				}
				
				//sessionContainer.setAttribute("theDocument_report", theDocument);
				//sessionContainer.setAttribute("asAttachment_report", asAttachment);
				
				out.write("<html><link rel='stylesheet' href='../../css/stili.css' type='text/css'>");
				out.write("<script type='text/javascript'>");
				out.write("parent.frames[0].document.getElementById('ulMessageWarning').style.display='block';");
	            out.write("parent.frames[0].document.getElementById('messageWarning').innerHTML='"+messaggio+"';");
	            
	            out.write("var f;");
	            //out.write("f = 'AdapterHTTP?PAGE=DownloadPageWithWarning&LEGGISESSIONE=1';");
	            out.write("f = 'AdapterHTTP?PAGE=DownloadPageWithWarning&ricarica=1&prgDocumento="+ theDocument.getPrgDocumento().toString() + "&asAttachment=" + asAttachment +"';");
	            out.write("window.location = f;");
	            
	            out.write("</script>");
				out.write("<body class='gestione'><br>");
	            out.write("</body></html>");
			
			} else{
				
				response.setContentType( getContentType(filename) );
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