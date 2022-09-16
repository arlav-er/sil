<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
                  com.engiweb.framework.tracing.*,
                  com.engiweb.framework.util.*,
                  it.eng.sil.module.movimenti.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.bean.*,
                  it.eng.sil.module.anag.*,
                  it.eng.sil.*,
                  java.util.*,
                  java.text.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>
                  
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af" %>
<%
	String _page = (String) serviceRequest.getAttribute("PAGE"); 
 	int _cdnFunz = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
	String cdnLavoratoreDecrypt= (String)serviceRequest.getAttribute("CDNLAVORATORE");	
	String datAnz68= (String)serviceRequest.getAttribute("DATANZIANITA68");
	String mesiAnz68 = "";
			
	Object mesiAnzObj = serviceResponse.getAttribute("CM_CALCOLA_MESI_ANZ.mesiAnz68");
	if (mesiAnzObj != null) {
		mesiAnz68 = (String) mesiAnzObj;
	} 	
  	
  	String htmlStreamTop = StyleUtils.roundTopTable(false);
  	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
  <head>
  	<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
  </head>
  <body class="gestione">
  <%=htmlStreamTop%>
  <center>
	<font color="green">
		<af:showErrors/>
    </font>
  </center>
  <% if (mesiAnzObj != null) {%>
  <br/><p class="titolo">Calcolo di Anzianità di Iscrizione al Collocamento Mirato</p>
  <af:form name="Frm1" method="POST" action="AdapterHTTP">
	<table class="main">
		<tr align="left">
		    <td>Numero Mesi d'Anzianità maturati ad oggi</td>
		    <td>
				<input type="text" class="inputView" name="NUMEMESIANZIANITA68" value="<%=mesiAnz68%>" size="4" maxlength="3" />
		    </td>
		</tr>
		<tr>
		    <td>&nbsp;</td>
		</tr>
		<tr align="left">
		    <td>Calcolati in base alla seguente data:</td>
		    <td>
		    	<input type="text" class="inputView" name="DATANZIANITA68" value="<%=datAnz68%>" size="11" maxlength="10" />
		    </td>
		</tr>
	</table>
  </af:form>
  <% }%>
  <p align="center">
	<input type="button" class="pulsanti" value="Chiudi" onClick="window.close()">
  </p>
  <%=htmlStreamBottom%>

  </body>
</html>
