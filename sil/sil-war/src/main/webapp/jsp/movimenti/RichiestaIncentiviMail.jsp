<%@page import="it.eng.sil.module.cigs.bean.BeanUtils"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,com.engiweb.framework.dispatching.module.AbstractModule,it.eng.sil.util.*,it.eng.sil.security.User,it.eng.afExt.utils.*" %>



<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	SourceBean row = (SourceBean) serviceResponse
			.getAttribute("M_GET_RICHIESTA_INCENTIVI_MAIL.rows.row");
	String xmlGenerato = (String) serviceResponse
			.getAttribute("M_RichiestaIncentivi_InviaMail.xmlGenerato");
	// Gestione delle linguette
	String cdnfunzione = (String) serviceRequest
			.getAttribute("cdnfunzione");
	String prgMovimento = (String) serviceRequest
			.getAttribute("PRGMOVIMENTO");
	String _page = (String) serviceRequest.getAttribute("PAGE");

	String htmlStreamTop = StyleUtils.roundTopTable(true);
	String htmlStreamBottom = StyleUtils.roundBottomTable(true);
	BeanUtils bu = new BeanUtils(serviceRequest);

	// Inizio della pagina vera e propria
	String corpoMailDB = (String) row.getAttribute("STRCORPOEMAIL");
	String mittente = (String) row.getAttribute("STREMAILMITTENTE");
	String oggetto = (String) row.getAttribute("STROGGETTO");
	String strdestcc = (String) row.getAttribute("strdestcc");
	String strdestccn = (String) row.getAttribute("strdestccn");
	String strdestinatari = (String) row.getAttribute("strdestinatari");
	
	String strCognomeLav=bu.getObjectToString("strCognomeLav");
	String strNomeLav=bu.getObjectToString("strNomeLav");
	String strCodiceFiscaleLav=bu.getObjectToString("strCodiceFiscaleLav");
	String strRagioneSocialeAz=bu.getObjectToString("strRagioneSocialeAz");
	String strCodiceFiscaleAz=bu.getObjectToString("strCodiceFiscaleAz");
	
	corpoMailDB += "\nLavoratore:"+strCognomeLav +" "+ strNomeLav +" CodiceFiscale:" + strCodiceFiscaleLav+"\n\n";
	corpoMailDB += "Azienda:"+strRagioneSocialeAz +" CodiceFiscale:" + strCodiceFiscaleAz+"";
	
	//hanno la precedenza i valori postati
	String corpoMail=bu.getObjectToString("corpoMail", corpoMailDB);
	mittente=bu.getObjectToString("mittente", mittente);
	oggetto=bu.getObjectToString("oggetto", oggetto);
	strdestcc=bu.getObjectToString("strdestcc", strdestcc);
	strdestccn=bu.getObjectToString("strdestccn", strdestccn);
	strdestinatari=bu.getObjectToString("strdestinatari", strdestinatari);
%>
<html>
<head>
<title>Invio dati per domanda incentivi</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
<af:linkScript path="../../js/" />

 <script language="JavaScript" type="text/javascript" >
        // Rilevazione Modifiche da parte dell'utente
        var flagChanged = false;
        
        function fieldChanged() {
            flagChanged = true;
        }
</script>

</head>
<body class="gestione" onload="rinfresca();">

<p>&nbsp;</p>
<p class="titolo">Invio dati per domanda incentivi</p>
    <af:showMessages prefix="M_RichiestaIncentivi_InviaMail"/>
    <af:showErrors/>
<af:form method="POST" action="AdapterHTTP" name="Frm1">

	<af:textBox type="hidden" name="PAGE" value="RichiestaIncentiviLoadMail" />
	<af:textBox name="cdnfunzione" type="hidden" value="<%=cdnfunzione%>" />
	<af:textBox name="PRGMOVIMENTO" type="hidden" value="<%=prgMovimento%>" />
	<af:textBox name="strdestcc" type="hidden" value="<%=strdestcc%>" />
	<af:textBox name="strdestccn" type="hidden" value="<%=strdestccn%>" />
	<af:textBox name="strCognomeLav" type="hidden" value="<%=strCognomeLav%>" />
	<af:textBox name="strNomeLav" type="hidden" value="<%=strNomeLav%>" />
	<af:textBox name="MODULE" type="hidden" value="M_RichiestaIncentivi_InviaMail" />

	<%
		out.print(htmlStreamTop);
	%>
	<table class="main">

		<tbody>
				<tr>
					<td class="etichetta">Lavoratore</td>
					<td class="campo"><af:textBox name="Lavoratore"
							value='<%=strCognomeLav +" " + strNomeLav%>' title="Lavoratore"
							classNameBase="input" readonly="true" /></td>
				</tr>
				<tr>
				<td class="etichetta">CodiceFiscale</td>
				<td class="campo"><af:textBox name="strCodiceFiscaleLav"
							value="<%=Utils.notNull(strCodiceFiscaleLav)%>"
							title="CodiceFiscale Lavoratore"
							classNameBase="input" readonly="true" /></td>
			</tr>
			<tr>
				<td class="etichetta">Azienda</td>
				<td class="campo"><af:textBox name="strRagioneSocialeAz"
							value="<%=Utils.notNull(strRagioneSocialeAz)%>"
							title="Azienda" size="50" 
							classNameBase="input" readonly="true" /></td>
			</tr>
			<tr>
				<td class="etichetta">CodiceFiscale</td>
				<td class="campo"><af:textBox name="strCodiceFiscaleAz"
							value="<%=Utils.notNull(strCodiceFiscaleAz)%>"
							title="CodiceFiscale Azienda" size="50" 
							classNameBase="input" readonly="true" /></td>
			</tr>
			<tr>
				<td class="etichetta">Mittente</td>
				<td class="campo"><af:textBox name="mittente"
					value="<%=Utils.notNull(mittente)%>" title="Mittente"
					required="true" size="50" onKeyUp="fieldChanged()"
					classNameBase="input" readonly="false" /></td>
			</tr>
			<tr>
				<td class="etichetta">Destinatari</td>
				<td class="campo"><af:textBox name="strdestinatari"
					value="<%=Utils.notNull(strdestinatari)%>" title="Destinatari"
					required="true" size="50" onKeyUp="fieldChanged()"
					classNameBase="input" readonly="false" /></td>
			</tr>
			<tr>
				<td class="etichetta">Oggetto</td>
				<td class="campo"><af:textBox name="oggetto"
					value="<%=Utils.notNull(oggetto)%>" title="Oggetto"
					required="true" size="50" onKeyUp="fieldChanged()"
					classNameBase="input" readonly="false" /></td>
			</tr>
			<tr>
				<td class="etichetta">Corpo</td>
				<td class="campo"><af:textArea name="corpoMail"
					value="<%=Utils.notNull(corpoMail)%>" title="Mittente"
					required="true" onKeyUp="fieldChanged()" rows="15" cols="50"
					classNameBase="input" readonly="false" /></td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td nowrap class="campo"><input class="pulsante" type="submit"
					name="INVIA" value="INVIA" />
					<input class="pulsante" type="reset" onclick="window.close()"	value="Annulla" /></td>
			</tr>
		</tbody>
	</table>
	<%
		out.print(htmlStreamBottom);
	%>
</af:form>
</body>
</html>

