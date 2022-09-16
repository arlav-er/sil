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
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
String _page = (String) serviceRequest.getAttribute("PAGE");
String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
ProfileDataFilter filter = new ProfileDataFilter(user, _page);
if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
}
else {
	PageAttribs attributi = new PageAttribs(user, _page);
	boolean canInsert = attributi.containsButton("INSERISCI");
	boolean canDelete = attributi.containsButton("CANCELLA");
	//Servono per gestire il layout grafico
	String htmlStreamTop = StyleUtils.roundTopTable(true);
	String htmlStreamBottom = StyleUtils.roundBottomTable(true);
%>
	<html>
	<head>
    <title>Programmazioni Batch</title>
	<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
	<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
	<af:linkScript path="../../js/" />
	
	<script language="JavaScript">

		function conferma_simulazione(url, alertFlag) {
			var msg = "Simulazione lancio elaborazione: La simulazione fornisce la lista dei lavoratori che rispondono in data corrente ai parametri fissati nella programmazione. Vuoi proseguire?";
			if (confirm(msg)) {
				var _url = "AdapterHTTP?" + url;
				setWindowLocation(_url);
			}
		}
	
	</script>
	</head>

	<body class="gestione">
		<font color="red">
			<af:showErrors />
		</font>
		<font color="green">
			<af:showMessages prefix="M_DeleteProgrammazioneBatch" />
			<af:showMessages prefix="M_InserisciProgrammazioneBatch" />
			<af:showMessages prefix="M_AggiornaProgrammazioneBatch" />
		</font>
		
		<af:list moduleName="M_GetProgrammazioniBatch" 
             canDelete="<%= canDelete ? \"1\" : \"0\" %>" />
        
        <%if (canInsert) {%>  
	        <center>
			<af:form method="POST" action="AdapterHTTP" dontValidate="true">
			<input type="hidden" name="PAGE" value="GestioneProgrammazionePage"/>
			<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
			<input type="hidden" name="OPERAZIONEPROGR" value="INSERISCI"/>
			<input type="hidden" name="PAGEPROVENIENZA" value="ListaProgrammazioniPage"/>
			<input class="pulsanti" type="submit" name="inserisci" value="Nuova programmazione"/>
			</af:form>
			</center>
		<%}%>
	</body>
	</html>
<%}%>