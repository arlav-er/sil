<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.StringUtils,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
	String cdnFunzione = (String) serviceRequest.getAttribute("cdnfunzione");
%>

<html>
<head>
	<title>Ricerca Alert</title>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css"/>
    <af:linkScript path="../../js/" />
    <script language="javascript">
      function nuovo(){
      	// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
      
        var url = "AdapterHTTP?PAGE=InsertMessageFormPage&cdnfunzione=<%=cdnFunzione%>";
		setWindowLocation(url);
      }
    </script>
</head>
<body>
<br/>
<p class="titolo">Ricerca Alert</p>
	
    <%out.print(htmlStreamTop);%>
    <p align="center">
	<af:form method="POST" action="AdapterHTTP" name="Frm1">
		<input type="hidden" name="PAGE" value="ListaMessagePage"/>
		<table class="main">
			<tr>
          		<td align="center">Tipo &nbsp;&nbsp;
          			<af:comboBox classNameBase="input" title="Tipo" name="tiporicerca" required="true" disabled="">
	                	<option value="validi" selected="selected">Non scaduti</option>
	                	<option value="nonvalidi">Scaduti</option>
	                	<option value="tutti">Tutti</option>
	 	            </af:comboBox>
          		</td>
			</tr>
			
			<tr>

				<td align="center">
					<br/>&nbsp;
					<input class="pulsante" type="submit" name="salva" value="Cerca"/>
					&nbsp;&nbsp;
			        <input class="pulsante" type="reset"  value="Annulla"/>
				</td>
			</tr>
			<tr>
				<td align="center">
					<br>&nbsp;
					<input type="button" class="pulsante" VALUE="Nuovo alert" onClick="nuovo()"/>
				</td>
			</tr>
				
		</table>

	</af:form>
	</p>
	<%out.print(htmlStreamBottom);%>
	
</body>
</html>
