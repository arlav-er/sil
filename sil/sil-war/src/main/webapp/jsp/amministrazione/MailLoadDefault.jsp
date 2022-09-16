<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  it.eng.sil.util.*,
                  it.eng.sil.security.User,
                  it.eng.afExt.utils.*
				" %>



<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
    
	SourceBean row = (SourceBean) serviceResponse.getAttribute("MailLoadDefault.rows.row");
	
	// Gestione delle linguette
	String cdnfunzione = (String) serviceRequest.getAttribute("cdnfunzione");
	String _page = (String) serviceRequest.getAttribute("PAGE");
	
	String htmlStreamTop = StyleUtils.roundTopTable(true);
	String htmlStreamBottom = StyleUtils.roundBottomTable(true);
	
	// Inizio della pagina vera e propria
	String corpoMail = (String) row.getAttribute("STRCORPOEMAIL");
	String mittente = (String) row.getAttribute("STREMAILMITTENTE");
	String oggetto = (String) row.getAttribute("STROGGETTO");

%>



<html>
<head>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
<af:linkScript path="../../js/" />

 <script language="JavaScript">
        // Rilevazione Modifiche da parte dell'utente
        var flagChanged = false;
        
        function fieldChanged() {
            flagChanged = true;
         
        }

</script>

</head>
<body class="gestione" onload="rinfresca()">

<p>&nbsp;</p>
<p class="titolo">Invio migrazioni per posta elettronica</p>

<af:form method="POST" action="AdapterHTTP" name="Frm1">

	<af:textBox type="hidden" name="PAGE" value="MailInviaExpPage" />
	<af:textBox name="cdnfunzione" type="hidden" value="<%=cdnfunzione%>" />
	<input type="hidden" name="<%= StepByStepConst.PARAM_COMANDO %>" value="<%= StepByStepConst.COMANDO_INIZIO %>" />



	<%out.print(htmlStreamTop);%>
	<table class="main">



		<tbody>
			<tr>
				<td class="etichetta">Mittente</td>
				<td class="campo"><af:textBox name="mittente"
					value="<%=Utils.notNull(mittente)%>" title="Mittente"
					required="true" size="50" onKeyUp="fieldChanged()"
					classNameBase="input" readonly="false" /></td>
			</tr>



			<tr>
				<td class="etichetta">Oggetto</td>
				<td class="campo"><af:textBox name="oggetto"
					value="<%=Utils.notNull(oggetto)%>" title="Mittente"
					required="true" size="50" onKeyUp="fieldChanged()"
					classNameBase="input" readonly="false" /></td>
			</tr>



			<tr>
				<td class="etichetta">Corpo</td>
				<td class="campo"><af:textArea name="corpoMail"
					value="<%=Utils.notNull(corpoMail)%>" title="Mittente"
					required="true" onKeyUp="fieldChanged()" rows="5" cols="50"
					classNameBase="input" readonly="false" /></td>
			</tr>



			<tr>
				<td>&nbsp;</td>
			</tr>

			<tr>
				<td>&nbsp;</td>

				<td nowrap class="campo"><input class="pulsante" type="submit"
					name="INVIA" value="INVIA" /> <input class="pulsante" type="reset"
					value="Annulla" /></td>
			</tr>



		</tbody>
	</table>

	<%out.print(htmlStreamBottom);%>

</af:form>
</body>
</html>

