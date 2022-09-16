<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  com.engiweb.framework.tracing.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  it.eng.sil.bean.menu.*,
			      javax.xml.transform.*,
				  javax.xml.transform.stream.*
	" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>
<%

    boolean canSave   = true;
 
    String htmlStreamTop = StyleUtils.roundTopTable(canSave);   
    String htmlStreamBottom = StyleUtils.roundBottomTable(canSave);

	String cdnfunzione =  (String) serviceRequest.getAttribute("cdnfunzione");
	
%>

<html>
	<head>
	  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
	   <af:linkScript path="../../js/"/>
</head>	
<body class="gestione" onLoad="rinfresca();">

<br />
   
<%
	Menu menu = (Menu) sessionContainer.getAttribute("menu");
%>

 <%out.print(htmlStreamTop);%>
      <table class="main">
                      <tr>
			            <td> 
				            Clonazione menu: <b><%=menu.getDescrizione()%></b>
			            </td>
			          </tr>
	
	   </table>
	  <%out.print(htmlStreamBottom);%>

	<af:showMessages prefix="ClonaMenu"/>
	<af:showErrors />
 
 
 <table class="main"> <tr><td>
			 <a href="AdapterHTTP?PAGE=DettaglioMenuPage&cdnmenu=<%=menu.getCdnMenu()%>&CDNFUNZIONE=<%=cdnfunzione%>">
              <img id="immIndietro" src="../../img/indietro.gif" alt="Torna alla modifica del menu clonato" />
              Torna alla modifica del menu clonato
              </a>
         </td></tr></table>     
 

<p>&nbsp;</p>

</body>
</html>

