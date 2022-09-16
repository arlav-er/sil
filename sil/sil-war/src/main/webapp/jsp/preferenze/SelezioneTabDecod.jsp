<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  String cdnFunzione = (String) serviceRequest.getAttribute("cdnfunzione");
%>


<html>
	<head>
		<title>Mini TOAD via Web</title>

    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>

	</head>
	<body class="gestione" onload="rinfresca()">
		<p class="titolo">Mini TOAD via Web</p>
	<br>

	<af:form action="AdapterHTTP">
		<af:textBox type="hidden" name="PAGE" value="ListaTabDecodPage" />
		<af:textBox type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>" />
		
 <p align="center">
      <table class="main">


        <tr>
          <td class="etichetta">
	          PAGE
          </td>
          <td class="campo">
	          ListaTabDecodPage
          </td>
        </tr>


        <tr>
          <td class="etichetta">
	          CDNFUNZIONE
          </td>
          <td class="campo">
	          Preso dalla request..
          </td>
        </tr>
      
        <tr>
          <td class="etichetta">
	          TABLE_NAME
          </td>
          <td class="campo">
	          <af:textBox  name="TABLE_NAME" value="" />
          </td>
        </tr>
    


        <tr>
          <td class="etichetta">
	          SKIP_COMMENTS
          </td>
          <td class="campo">
	          <input type="checkbox" name="SKIP_COMMENTS" value="true">
          </td>
        </tr>

        <tr>
          <td class="etichetta">
	          SHOW_KEYS
          </td>
          <td class="campo">
	          <input type="checkbox" name="SHOW_KEYS" value="true">
          </td>
        </tr>




	    <tr>
            <td>
             &nbsp;
            </td>
          <td nowrap>

    		<input type="submit" value="OK">
	      </td>
        </tr>



        
      </table>


		
		
	</af:form>


	</body>
</html>

