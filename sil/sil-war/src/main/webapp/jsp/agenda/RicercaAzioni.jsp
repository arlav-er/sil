<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>


<%@ page import=" com.engiweb.framework.base.*,com.engiweb.framework.dispatching.module.AbstractModule,com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,it.eng.sil.util.*,java.util.*,it.eng.afExt.utils.*,it.eng.sil.util.Linguette,
                  java.math.BigDecimal,com.engiweb.framework.security.*, it.eng.sil.util.InfCorrentiLav"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%
	PageAttribs attributi = new PageAttribs(user, "RicercaAzioniPage");

	String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
	String cdnLavoratore = (String) serviceRequest.getAttribute("CDNLAVORATORE");

	String selComboObbiettivoMisuraYei = StringUtils.getAttributeStrNotNull(serviceRequest, "comboObbiettivoMisuraYei");
	String selComboTipoAttivita = StringUtils.getAttributeStrNotNull(serviceRequest, "comboTipoAttivita");
	String selComboPrestazione = StringUtils.getAttributeStrNotNull(serviceRequest, "comboPrestazione");
	String azione = StringUtils.getAttributeStrNotNull(serviceRequest, "azione");
	String flagMisuraYei = StringUtils.getAttributeStrNotNull(serviceRequest, "flagMisuraYei");
	String flagPolAttiva = StringUtils.getAttributeStrNotNull(serviceRequest, "flagPolAttiva");

	sessionContainer.delAttribute("AZIONECACHE");

	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
	
    String labelObiettivoMisura = "Obbiettivo/Misura YEI";
    String labelAzione = "Azione";
    String umbriaGestAz = "0";
    if(serviceResponse.containsAttribute("M_CONFIG_UMB_NGE_AZ")){
    	umbriaGestAz = Utils.notNull(serviceResponse.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM"));
    }
    if(umbriaGestAz.equalsIgnoreCase("1")){
    	labelObiettivoMisura = "Servizio";
    	labelAzione = "Misura";
    }
%>


<html>
	<head>
	<title>Ricerca Azioni</title>
	<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
	<af:linkScript path="../../js/" />
	<script language="Javascript">
		function go(url, alertFlag) {
			// Se la pagina è già in submit, ignoro questo nuovo invio!
			if (isInSubmit())
				return;
	
			var _url = "AdapterHTTP?" + url;
			if (alertFlag == 'TRUE') {
				if (confirm('Confermi operazione')) {
					setWindowLocation(_url);
				}
			} else {
				setWindowLocation(_url);
			}
		}
	
		function checkCampiObbligatori() {
			return true;
		}
	
		function valorizzaHidden() {
			document.form1.DESCOBBIETTIVOMISURAYEI.value = document.form1.comboObbiettivoMisuraYei.options[document.form1.comboObbiettivoMisuraYei.selectedIndex].text;
			document.form1.DESCTIPOATTIVITA.value = document.form1.comboTipoAttivita.options[document.form1.comboTipoAttivita.selectedIndex].text;
			document.form1.DESCPRESTAZIONE.value = document.form1.comboPrestazione.options[document.form1.comboPrestazione.selectedIndex].text;
			return true;
		}
	</script>
	</head>
	
	<body class="gestione" onload="rinfresca();">
		<br>
		<p class="titolo">Ricerca Azioni</p>
		<p align="center">
			<af:form name="form1" action="AdapterHTTP" method="POST"
				onSubmit="checkCampiObbligatori() && valorizzaHidden()">
				<% out.print(htmlStreamTop); %>
				<table>
					<tr><td colspan="2" />&nbsp;</td></tr>
					<tr>
						<td class="etichetta"><%=labelObiettivoMisura %></td>
						<td class="campo"><af:comboBox name="comboObbiettivoMisuraYei"
								moduleName="ComboObbiettivoMisuraYei" 
								selectedValue="<%=selComboObbiettivoMisuraYei%>"
								addBlank="true" /></td>
					</tr>

					<tr>
						<td class="etichetta"><%=labelAzione %></td>
						<td class="campo"><af:textBox type="text" name="AZIONE"
								value="<%=azione%>" size="40" maxlength="100" /></td>
					</tr>

  <tr>
    <td class="etichetta">Tipo attivit&agrave;</td>
    <td class="campo"><af:comboBox name="comboTipoAttivita" moduleName="ComboTipoAttivita" 
    	selectedValue="<%=selComboTipoAttivita%>"  addBlank="true"/></td>
  </tr>
  
  <tr>
    <td class="etichetta">Prestazione</td>
    <td class="campo"><af:comboBox name="comboPrestazione" moduleName="ComboPrestazione" selectedValue="<%=selComboPrestazione%>"  addBlank="true"/></td>
  </tr>

	
					<tr>
						<td class="etichetta">Flag misura YEI</td>
						<td class="campo">
							<input type="checkbox" name="FLAGMISURAYEI"
							 <%=(flagMisuraYei != null && flagMisuraYei.equals("on")) ? "checked='checked'" : ""%> />
						</td>
					</tr>

					<tr>
						<td class="etichetta">Flag politica attiva</td>
						<td class="campo">
							<input type="checkbox" name="FLAGPOLATTIVA"
							 <%=(flagPolAttiva != null && flagPolAttiva.equals("on")) ? "checked='checked'" : ""%> />
						</td>
					</tr>
	
					<tr>
						<td colspan="2"><hr width="90%" /></td>
					</tr>
	
					<tr>
						<td colspan="2">
							<input type="hidden" name="DESCOBBIETTIVOMISURAYEI" value="" />
							<input type="hidden" name="DESCTIPOATTIVITA" value="" />
							<input type="hidden" name="DESCPRESTAZIONE" value="" />
							<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
							<input type="hidden" name="MODULE" value="MListaAzioni">&nbsp;
							<input type="hidden" name="PAGE" value="GestAzioniPage">&nbsp;
						</td>
					</tr>

					<tr>
						<td colspan="2" align="center"><input type="submit"
							class="pulsanti" name="Invia" value="Cerca"> &nbsp;&nbsp;
							<input name="reset" type="reset" class="pulsanti" value="Annulla">
							&nbsp;&nbsp;</td>
					</tr>
				</table>
				<% 	out.print(htmlStreamBottom); %>
			</af:form>
		</p>
	</body>
</html>
