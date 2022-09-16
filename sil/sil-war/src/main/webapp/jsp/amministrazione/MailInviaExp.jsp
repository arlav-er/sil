<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.error.*,
                  it.eng.sil.security.*,
                  it.eng.sil.module.amministrazione.*,
                  java.util.*,
                  java.io.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%!private String getAttrStrNotNull(SourceBean sb, String attr) {
	return SourceBeanUtils.getAttrStrNotNull(sb, attr);
}
%>

<%String mainPageName = "MailInviaExpPage";
String moduleName = "MailInviaExpModule";
SourceBean moduleResponse = (SourceBean) serviceResponse.getAttribute(moduleName);

List bagList = (List) moduleResponse.getAttribute("BagList");

// Comando appena eseguito dal modulo (immediato passato).
String comandoP = getAttrStrNotNull(serviceRequest, StepByStepConst.PARAM_COMANDO);

// Comando che si dovrà eseguire nell'immediato futuro.
String comandoF = getAttrStrNotNull(moduleResponse, StepByStepConst.PARAM_COMANDO);

boolean thereWasAnError = !responseContainer.getErrorHandler().isOK();
boolean lastPageEsporta = ((String) serviceRequest.getAttribute("PAGE")).equals(mainPageName);

String htmlStreamTop = StyleUtils.roundTopTable(true);
String htmlStreamBottom = StyleUtils.roundBottomTable(true);
%>
<html>
<head>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" href="../../css/listdetail.css" type="text/css">


<af:linkScript path="../../js/" />

<script language="Javascript">
<!--

	function onLoad() {
		<%if (StringUtils.isEmpty(comandoP) || comandoF.equalsIgnoreCase(StepByStepConst.COMANDO_SUCC)) {%>
				//	alert("passo....")
					doFormSubmit(document.formina);
					<%}%>
	}
	
	function infoBut() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
	
		var url = "AdapterHTTP?PAGE=EsportaMigrazioniPage&<%=StepByStepConst.PARAM_COMANDO%>=<%=StepByStepConst.COMANDO_INFO%>";
		setWindowLocation(url);	
	}
	
	
	
//-->
</script>

</head>

<body class="gestione" onload="onLoad()">
<br />
<p class="titolo">Invio mail delle Migrazioni</p>

<p><af:showErrors /> <af:showMessages prefix="<%=moduleName%>" /></p>

<af:form name="formina" method="POST" action="AdapterHTTP"
	dontValidate="true">
	<input type="hidden" name="PAGE" value="<%=mainPageName%>" />
	<input type="hidden" name="cdnFunzione"
		value='<%=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione")%>' />
	<input type="hidden" name="<%=StepByStepConst.PARAM_COMANDO%>"
		value="<%=comandoF%>" />

	<%=htmlStreamTop%>
	<table class="main">
		<tbody>
			<tr>
				<td>

				<table class="main">
					<tbody>
						<tr>
							<td class="etichetta2" width="55%">&nbsp;</td>
							<td class="campo2" width="45%">&nbsp;</td>
						</tr>

						<%
/*
 * La pagina JSP deve leggere:
 *  - comandoPrecedente (letto da request)
 *  - comandoFuturo (letto da response del modulo)
 * e decidere in che stato ci troviamo: 1, 2, 3a o 3b.
 */

/* --------------------------------------------------------------------------------
 *  3- altrimenti (comandoPrecedente è "INIZIO" o "SUCC") guardo il comandoFuturo
 *      3a- se comandoFuturo è "SUCC":
 *            + mostra info elaborazione e barra di progresso
 *      3b- altrimenti (comandoFuturo è "INFO"):
 *            + mostra totali di fine operazione.
 * --------------------------------------------------------------------------------
 */
//else {%>
						<tr>
							<td colspan="2" class="campocentrato"><%=StepByStepUtils.getProgressBar(moduleResponse)%>
							</td>
						</tr>
						<tr>
							<td colspan="2">&nbsp;</td>
						</tr>
						<%
// Non distinguo tra "in corso" e "finita" poiché stampo
// sempre i dati di elaborazione (eventualmente parziali).
// NOTA: LA "SUBMIT" VIENE FATTA IN AUTOMATICO SULL'onLoad QUANDO SERVE.
String messElab; // messaggio da mostrare
if (comandoF.equalsIgnoreCase(StepByStepConst.COMANDO_SUCC)) {
	messElab = "Attendere, prego...";
}
// Comando Futuro = STOP: FINE ITERAZIONE --------------------------------------------------
else { // ho per forza: comandoF uguale COMANDO_INFO
	if (thereWasAnError) {
		messElab = "Invio interrotto!";
	} else {
		messElab = "Invio completato!";
	}
} // if comandoF%>

						<tr>
							<td colspan="2"><b><%=messElab%></b></td>
						</tr>
					</tbody>
				</table>

				</td>
			</tr>

			<tr>
				<td colspan="2">

				<table class="main">

					<tbody>
						<tr>
							<th class="lista">Stato</th>
							<th class="lista">Destinatario</th>
							<th class="lista">Provincia</th>
							<th class="lista">Cod. CPI</th>
							<th class="lista">File inviati</th>
						</tr>
						<%
						if (bagList != null) {
							Iterator iter = bagList.iterator();
							boolean pari = true;
							while (iter.hasNext()) {
								MailStatusBag bag = (MailStatusBag) iter.next();
							
								String stile = pari ? "lista_pari" : "lista_dispari";
								pari = !pari;%>
							
													<tr>
														<td class="<%=stile%>">
														<%
															if (bag.isOK())
																out.print("<img src=\"../../img/invio_OK.gif\" border=\"0\">");
															else{
																out.print("<img src=\"../../img/invio_fallito.gif\" border=\"0\"");
																out.print(" title=\"" + bag.getMsg() + "\"");
																out.print(" alt=\"" + bag.getMsg() + "\"");																
																 out.print(" >");
															}
																	
														%>
														</td>
														<td class="<%=stile%>">&nbsp;<%=bag.getEmail()%></td>
														<td class="<%=stile%>">&nbsp;<%=bag.getProvincia()%></td>
														<td class="<%=stile%>">&nbsp;<%=bag.getCpi()%></td>
														<td class="<%=stile%>">&nbsp;
														<%
														List fileList = bag.getFileList();
														
														if ((fileList != null) && (fileList.size()>0)){%>
															<table class="main">
															<%
																for (int i = 0; i < fileList.size(); i++) {
																	File file= (File) fileList.get(i);	
																	String nomefile =file.getName();%>
																
																	<tr><td><%=nomefile%></td></tr>
									
																<%} //for %>
															</table>
														<%
														} //if	
														
														%></td>
														
													</tr>
							
							
													<%}%>
					  <% } %>
					</tbody>
				</table>
				</td>
			</tr>




			<%if (comandoF.equalsIgnoreCase(StepByStepConst.COMANDO_INFO)) {%>
			<tr>
				<td colspan="2">&nbsp;</td>
			</tr>
			<tr>
				<td colspan="2"><span class="bottoni"> <input type="button"
					class="pulsante" name="ok" value="  OK  " onclick="infoBut()" /></span></td>
			</tr>
			<%}

//} // else-if comandoP
%>
		</tbody>
	</table>
	<%=htmlStreamBottom%>

</af:form>

</body>
</html>
