<%@ page contentType="text/xml;charset=utf-8"%>
<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
                   
                  com.engiweb.framework.util.*,
                  it.eng.sil.module.movimenti.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.*,
                  java.util.*,
                  java.text.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

<%
    XMLImporter importer = new XMLImporter();
    String pw = request.getParameter("password");
    String records = request.getParameter("contenuto");
    String result = importer.service(pw, records);
	out.print(result);
%>
	
	