<%@page import="java.io.*"%><%@page import="java.io.ObjectInputStream"%><%@page import="java.awt.Frame"%><%@page import="java.io.InputStream"%><%@page import="java.io.File"%><%@page import="java.io.BufferedInputStream"%><%@ include file="../global/noCaching.inc" %><%@page import="com.engiweb.framework.configuration.ConfigSingleton"%><%
	
	String stFileName = ConfigSingleton.getRootPath() + File.separator + "WEB-INF" + File.separator + "report" + 
	File.separator + "Statici" + File.separator + "Informativa_SIL_PROV.pdf" + File.separator;
	File pdfFile;
	

	try {
		pdfFile = new File( stFileName );

	} catch(Throwable t) {

		throw new ServletException("PdfGet: open pdf stream: " + t.toString());

	}

// prepare the response

//
	response.reset();

	response.setContentType( "application/pdf" );
	
	response.setHeader("Content-Disposition", "filename=InformativaPrivacy.pdf");
	
	BufferedInputStream fif = new BufferedInputStream(new FileInputStream(pdfFile));
	OutputStream outStream = response.getOutputStream();
		
		int data;
		while ((data = fif.read()) != -1) {
			outStream.write(data);
		}
		outStream.close();
		fif.close();
	
	%>
	

