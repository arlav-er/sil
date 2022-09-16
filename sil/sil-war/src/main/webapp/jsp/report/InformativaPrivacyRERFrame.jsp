<%@ page contentType="text/html;charset=utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.ObjectInputStream"%>
<%@page import="java.awt.Frame"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.io.File"%>
<%@ include file="../global/noCaching.inc" %>

<%@page import="com.engiweb.framework.configuration.ConfigSingleton"%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<SCRIPT language="JavaScript" src="../../js/customTL.js"></SCRIPT>
<title>Informativa Privacy RER</title>
</head>

<FRAMESET rows="*" onload="rinfresca();">
  <FRAME frameborder="0" name="REPORT" noresize src="AdapterHTTP?PAGE=InformativaPrivacyRERPagePub">
</FRAMESET>
</html>
