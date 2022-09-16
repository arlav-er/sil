<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>


<%@ page
	import="com.engiweb.framework.base.*,com.engiweb.framework.dispatching.module.AbstractModule,com.engiweb.framework.util.QueryExecutor,it.eng.sil.security.*,it.eng.sil.util.*,java.util.*,it.eng.afExt.utils.*,it.eng.sil.util.Linguette,java.math.BigDecimal,com.engiweb.framework.security.*,it.eng.sil.util.InfCorrentiLav"%>


<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%
	PageAttribs attributi = new PageAttribs(user, "RicercaOperatoriPage");

	String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
	String cdnLavoratore = (String) serviceRequest.getAttribute("CDNLAVORATORE");
	
	String validi   = StringUtils.getAttributeStrNotNull(serviceRequest,"validi");
	 
	String fcognome = StringUtils.getAttributeStrNotNull(serviceRequest,"FSTRCOGNOME");
	String fnome = StringUtils.getAttributeStrNotNull(serviceRequest,"FSTRNOME");
	String fcf = StringUtils.getAttributeStrNotNull(serviceRequest,"FSTRCODICEFISCALE");
	String fsiglaOp = StringUtils.getAttributeStrNotNull(serviceRequest,"FSTRSIGLAOPERATORE");
	String fdataNascita = StringUtils.getAttributeStrNotNull(serviceRequest,"FDATNASC");
	 
	 

	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>


<html>
<head>
<title>Ricerca Operatori</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<af:linkScript path="../../js/" />
<script language="Javascript">
<%@ include file="../documenti/RicercaCheck.inc" %>

  function go(url, alertFlag) {
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;
  
  var _url = "AdapterHTTP?" + url;
  if (alertFlag == 'TRUE' ) {
    if (confirm('Confermi operazione')) {
      setWindowLocation(_url);
    }
  }
  else {
    setWindowLocation(_url);
  }
}


function checkCampiObbligatori()
{
   return true;
}


  function valorizzaHidden() {
	  	return true;
  }
  
</script>
</head>

<body class="gestione" onload="rinfresca();">

	<br>
	<p class="titolo">Ricerca Operatori</p>
	<p align="center">
		<af:form name="form1" action="AdapterHTTP" method="POST"
			onSubmit="checkCampiObbligatori() && valorizzaHidden()">
			<%
				out.print(htmlStreamTop);
			%>
			<table>

				<tr>
					<td colspan="2" />&nbsp;
					</td>
			    </tr>
				<tr>
					<td class="etichetta">Codice Fiscale</td>
					<td class="campo"><af:textBox type="text"
							name="FSTRCODICEFISCALE" value="<%=fcf%>" size="40" maxlength="16" />(inizia con)</td> 
				</tr>
				<tr>
					<td class="etichetta">Cognome</td>
					<td class="campo"><af:textBox type="text" name="FSTRCOGNOME"
							value="<%=fcognome%>" size="40" maxlength="40" />(inizia con)</td>
				</tr>
				<tr>
					<td class="etichetta">Nome</td>
					<td class="campo"><af:textBox type="text" name="FSTRNOME"
							value="<%=fnome%>" size="40" maxlength="40" />(inizia con)</td>
				</tr>
				<tr>
					<td class="etichetta">Sigla Operatore</td>
					<td class="campo"><af:textBox type="text"
							name="FSTRSIGLAOPERATORE" value="<%=fsiglaOp%>" size="40" maxlength="80" />(inizia con)</td>
				</tr>
				<tr>
					<td class="etichetta">Data Nascita</td>
					<td class="campo"><af:textBox type="date" name="FDATNASC"
							 value="<%=fdataNascita%>" size="11" maxlength="10" validateOnPost="true"/></td>
				</tr>
				<tr>
					<td class="etichetta">Solo operatori validi</td>
					<td class="campo"><input type="checkbox" name="VALIDI" <%=(validi!=null&&validi.equals("on"))?"checked='checked'":""%> /></td>
				</tr>
				<tr>
					<td colspan="2"><hr width="90%" /></td>
				</tr>

				<tr>
					<td colspan="2"><input type="hidden" name="DESCAREA" value="" />
						<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
						<input type="hidden" name="PAGE" value="GestOperatoriPage">
						&nbsp;</td>
				</tr>
				<tr>
					<td colspan="2" align="center"><input type="submit"
						class="pulsanti" name="Invia" value="Cerca"> &nbsp;&nbsp;
						<input name="reset" type="reset" class="pulsanti" value="Annulla">
						&nbsp;&nbsp;</td>
				</tr>
			</table>
			<%
				out.print(htmlStreamBottom);
			%>
		</af:form>
	</p>

</body>
</html>
