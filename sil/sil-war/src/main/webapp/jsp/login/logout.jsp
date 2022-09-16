<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file ="../global/noCaching.inc" %>
<%@ page  import=" 
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.security.*" %>
                  
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html lang="ita">
<head>
     
<%
   session.invalidate();
%>

<script language="Javascript" type="text/javascript">

  function chiudi(){
  	window.top.close();	     
  }
</script>

</head>
<body onload="chiudi();">
</body>
</html>
