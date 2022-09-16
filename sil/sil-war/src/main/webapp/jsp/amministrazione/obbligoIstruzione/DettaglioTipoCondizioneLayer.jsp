<%@ taglib uri="aftags" prefix="af"%>
<%@ page
	import="com.engiweb.framework.base.*,com.engiweb.framework.configuration.ConfigSingleton,it.eng.sil.util.*,it.eng.afExt.utils.StringUtils,it.eng.sil.security.ProfileDataFilter,java.lang.*,java.text.*,java.util.*,java.math.*,it.eng.sil.security.*"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>
<%@ include file="../../global/getCommonObjects.inc"%>
<%
	int _funzione = 0;
	try {
		_funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
	} catch (Exception ex) {
		ex.printStackTrace();
	}
	String _page = (String) serviceRequest.getAttribute("PAGE");
	PageAttribs attributi = new PageAttribs(user, _page);
	boolean canModify = attributi.containsButton("AGGIORNA");
	boolean canDelete = attributi.containsButton("CANCELLA");
	boolean canInsert = attributi.containsButton("INSERISCI");

	String apriDiv = "none";
	if ("true".equals(serviceRequest.getAttribute("APRIDIV"))) {
		apriDiv = "";
	}
	SourceBean dettCondizione, dettCondizioneSB;
	String prgobbligoistruzione = "";
	String closeBtnText = "";
	String cdnlavoratore = "";
	String codtipocondizione = "";
	String prglavstoriainf = "";
	String datainizioobbligo = "";
	String datafineobbligo = "";
	String strnote = "";
	String cdnUtins = "";
	String dtmins = "";
	String cdnUtmod = "";
	String dtmmod = "";
	String numkloobbligoistr = "1";
	BigDecimal numLock = BigDecimal.ONE;
	BigDecimal numLock4upd = BigDecimal.ONE;
	boolean nuovo = true;
	Testata operatoreInfo = null;
	if (serviceResponse.containsAttribute("M_GET_DETTAGLIO_CONDIZIONE")) {
		nuovo = false;
		dettCondizioneSB = (SourceBean) serviceResponse.getAttribute("M_GET_DETTAGLIO_CONDIZIONE");
		dettCondizione = (SourceBean) dettCondizioneSB.getAttribute("ROWS.ROW");
		prgobbligoistruzione = dettCondizione.getAttribute("prgobbligoistruzione").toString();
		cdnlavoratore = dettCondizione.getAttribute("cdnlavoratore").toString();
		codtipocondizione = dettCondizione.getAttribute("codtipocondizione").toString();
		prglavstoriainf = dettCondizione.getAttribute("prglavstoriainf").toString();
		datainizioobbligo = (String) dettCondizione.getAttribute("datainizioobbligo");
		datafineobbligo = (String) dettCondizione.getAttribute("datafineobbligo");
		strnote = (String) dettCondizione.getAttribute("strnote");
		numkloobbligoistr = dettCondizione.getAttribute("numkloobbligoistr").toString();
		numLock = new BigDecimal(numkloobbligoistr);
		numLock4upd = numLock.add(BigDecimal.ONE);
		cdnUtins = dettCondizione.getAttribute("cdnutins").toString();
		dtmins = dettCondizione.getAttribute("dtmins").toString();
		cdnUtmod = dettCondizione.getAttribute("cdnutmod").toString();
		dtmmod = dettCondizione.getAttribute("dtmmod").toString();
		operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
	} else {
		cdnlavoratore = (String) serviceRequest.getAttribute("CDNLAVORATORE");
	}
	String divStreamTop = StyleUtils.roundLayerTop(canModify);
	String divStreamBottom = StyleUtils.roundLayerBottom(canModify);
%>
<div id="divLayerDett" name="divLayerDett" class="t_layerDett"
	style="position: absolute; width: 80%; left: 50; top: 100px; z-index: 6; display: <%=apriDiv%>;">
<!-- Stondature ELEMENTO TOP --> <a name="aLayerIns"></a> <%
 	out.print(divStreamTop);
 %> <script language="Javascript" type="text/javascript">
  _arrFunz[_arrIndex++]="controllaForm()";
 
 function invia(bottonePremuto){
	dettForm=document.forms["dettCondizione"];
	dettForm.elements["bottonePremuto"].value=bottonePremuto.name;
	if(dettForm.onsubmit()){
		dettForm.submit();
	}
	return false;
 }
 //Controlla se la data di fine è successiva alla data inizio
function dateInizioFineOk(dettForm) {
	//dettForm=document.forms["dettCondizione"];
	var dataInizioObj=dettForm.elements["datainizioobbligo"];
	var dataFineObj=dettForm.elements["datafineobbligo"];
	
	var dataInizio = new String(dataInizioObj.value);
	var dataFine = new String(dataFineObj.value);
	
	if (dataFine=="") return true;
	if (compareDate(dataInizio,dataFine) > 0) {
		return false;
		}
	return true;
}
function controllaForm() {
	var errorMsg="";
	dettForm=document.forms["dettCondizione"];
	var objSelTC= dettForm.elements["codtipocondizione"];
	var optSel=objSelTC.options[objSelTC.selectedIndex]
	if(optSel.value==""){
	errorMsg+="Selezionare un tipo Condizione\n"
	}
	var objDataInizio= dettForm.elements["datainizioobbligo"];
	if(objDataInizio.value==""){
	errorMsg+="Inserire una data di inizio\n"
	}
	var dataInizioOk=checkFormatDate(objDataInizio);
	if(!dataInizioOk){
	errorMsg+="Data non corretta nel campo " + objDataInizio.title+"\n";
	objDataInizio.focus();
		}
	
	var dataFineObj=dettForm.elements["datafineobbligo"];
	var dataFineOk=checkFormatDate(dataFineObj);
	if(!dataFineOk){
	errorMsg+="Data non corretta nel campo " + dataFineObj.title+"\n";
	dataFineObj.focus();
		}

	//controllo le 2 date se entrambe son corrette
	if(dataInizioOk && dataFineOk)
	if(!dateInizioFineOk(dettForm)){
	errorMsg+="La data di inizio deve essere precedente alla data di Fine\n"
	}
	
	if(errorMsg!=""){
	alert(errorMsg);
	return false;
	}
	return true;
}

 </script>
<table width="100%">
	<tr>
		<td width="16" height="16" class="azzurro_bianco"><img
			src="../../img/move_layer.gif" onClick="return false"
			onMouseDown="engager(event,'divLayerDett');return false"></td>
		<td height="16" class="azzurro_bianco">
		<%
			if (nuovo) {
		%> Nuovo Tipo Condizione <%
			} else {
		%> Tipo Condizione <%
			}
		%>
		</td>
		<td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')"
			class="azzurro_bianco"><img src="../../img/chiudi_layer.gif"
			alt="Chiudi"></td>
	</tr>
</table>
<af:form name="dettCondizione" method="POST" action="AdapterHTTP">
	<table align="center" width="100%" border="0">
		<tr valign="top">
			<td class="etichetta">Tipo Condizione</td>
			<td class="campo"><af:comboBox name="codtipocondizione"
				moduleName="M_COMBO_TIPO_CONDIZIONE" title="Tipo Condizione"
				classNameBase="input" required="true" addBlank="true"
				selectedValue="<%=codtipocondizione%>" /></td>

		</tr>
		<tr>
			<td class="etichetta">Data Inizio</td>
			<td class="campo"><af:textBox type="date" validateOnPost="true"
				title="Data Inizio" name="datainizioobbligo"
				value="<%=datainizioobbligo%>" size="10" maxlength="10"
				required="true" /></td>
		</tr>
		<tr>
			<td class="etichetta">Data Fine</td>
			<td class="campo"><af:textBox type="date" name="datafineobbligo"
				validateOnPost="true" title="Data Fine" value="<%=datafineobbligo%>"
				size="10" maxlength="10" required="false" /></td>
		</tr>
		<tr>
			<td class="etichetta">Note</td>
			<td class="campo"><af:textArea cols="50" rows="4"
				classNameBase="textarea" name="strnote" validateOnPost="true"
				value="<%=strnote%>" /></td>
		</tr>
		<tr>
			<td colspan="4" align="center"><input type="hidden" name="PAGE"
				value="<%=_page%>"> <input type="hidden"
				name="cdnLavoratore" value="<%=cdnlavoratore%>" /> <input
				type="hidden" name="cdnFunzione" value="<%=_funzione%>" /> <input
				type="hidden" name="numLock4upd" value="<%=numLock4upd.toString()%>" />
			<input type="hidden" name="numLock" value="<%=numLock.toString()%>" />
			<input type="hidden" name="prgobbligoistruzione"
				value="<%=prgobbligoistruzione%>" /> <input type="hidden"
				name="prglavstoriainf" value="<%=prglavstoriainf%>" /> <input
				type="hidden" name="bottonePremuto" value="" /> <%
 	if (canModify && !nuovo) {
 			closeBtnText = "Chiudi senza aggiornare";
 %> <input type="button" name="aggiorna" value="Aggiorna"
				class="pulsante" onclick="invia(this)" /> <%
 	}
 %> <%--
 	if (canDelete && !nuovo) {
 %> <input type="button" name="cancella" value="Rimuovi"
				class="pulsante" onclick="invia(this)" /> <%
 	}
 --%> <%
 	if (canInsert && nuovo) {
 			closeBtnText = "Chiudi senza inserire";
 %> <input type="button" name="inserisci" value="Inserisci"
				class="pulsante" onclick="invia(this)" /> <%
 	}
 %> <input type="button" class="pulsanti" name="chiudi"
				value="<%=closeBtnText%>" onClick="ChiudiDivLayer('divLayerDett')">
			</td>
		</tr>
	</table>
</af:form> <!-- Stondature ELEMENTO BOTTOM --> <%
 	if (!nuovo) {
 %>
<p align="center">
<%
	operatoreInfo.showHTML(out);
%>
</p>
<%
	}
%> <%
 	out.print(divStreamBottom);
 %>
</div>