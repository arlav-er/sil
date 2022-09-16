<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page
	import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  it.eng.sil.security.User,
                  it.eng.sil.security.ProfileDataFilter,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  java.lang.*,
                  java.text.*"%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%
	String cdnLavoratore = null;
	String _page = (String) serviceRequest.getAttribute("PAGE");
	ProfileDataFilter filter = new ProfileDataFilter(user, "DettaglioBonusPage");
	boolean loadLinguette = serviceRequest.containsAttribute("LOADLINGUETTA");
	
	PageAttribs attributi = new PageAttribs(user, _page);
	boolean canView = filter.canViewLavoratore();
	if (!canView) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}
	
	boolean canModify = attributi.containsButton("AGGIORNA");
	boolean canCallSAP = attributi.containsButton("RICHIESTASAP");
	boolean canVerificaSAP = attributi.containsButton("VERIFICASAP");
	
	String prgNotificaPn = null;
	String datAdesione = null;
	String datRicezione = null;
	String identificativoSap = null;
	String codProvincia = null;
	String flgPresoCarico = null;
	String checkPresaCarico = "";
	Testata operatoreInfo = null;
	String cdnUtIns = "";
	String dtmIns = "";
	String cdnUtMod = "";
	String dtmMod = "";
	String NUMKLONOTIFICA = null;
	String strCodiceFiscale = null;
	String txtNoteBonus = null;

	SourceBean row = (SourceBean) serviceResponse.getAttribute("M_GetBonusOccupazionale.ROWS.ROW");
	
	if (row.containsAttribute("cdnLavoratore")) {
		cdnLavoratore = row.getAttribute("cdnLavoratore").toString();
		filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	}
	if (row.containsAttribute("prgSpNotificaPn")) {
		prgNotificaPn = row.getAttribute("prgspnotificapn").toString();
	}
	if (row.containsAttribute("datAdesione")) {
		datAdesione = row.getAttribute("datAdesione").toString();
	}
	if (row.containsAttribute("datRicezione")) {
		datRicezione = row.getAttribute("datRicezione").toString();
	}
	if (row.containsAttribute("CDNUTINS")) {
		cdnUtIns = row.getAttribute("CDNUTINS").toString();
	}
	if (row.containsAttribute("DTMINS")) {
		dtmIns = row.getAttribute("DTMINS").toString();
	}
	if (row.containsAttribute("CDNUTMOD")) {
		cdnUtMod = row.getAttribute("CDNUTMOD").toString();
	}
	if (row.containsAttribute("DTMMOD")) {
		dtmMod = row.getAttribute("DTMMOD").toString();
	}
	if (row.containsAttribute("NUMKLOSPNOTIFICAPN")) {
		NUMKLONOTIFICA = row.getAttribute("NUMKLOSPNOTIFICAPN").toString();
	}
	if (row.containsAttribute("strCodiceFiscale")) {
		strCodiceFiscale = row.getAttribute("strCodiceFiscale").toString();	
	}
	if (row.containsAttribute("codProvincia")) {
		codProvincia = row.getAttribute("codProvincia").toString();	
	}
	if (row.containsAttribute("identificativoSap")) {
		identificativoSap = row.getAttribute("identificativoSap").toString();	
	}
	if (row.containsAttribute("FLGPRESOINCARICO")) {
		flgPresoCarico = row.getAttribute("FLGPRESOINCARICO").toString();
		if (flgPresoCarico.equalsIgnoreCase("S")) {
			checkPresaCarico = "CHECKED";
		}
	}
	if (row.containsAttribute("strNote")) {
		txtNoteBonus = row.getAttribute("strNote").toString();	
	}
	
	operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);

	String cdnFunzione = (String) serviceRequest.getAttribute("cdnFunzione");
	
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
	
	SourceBean rowVerifica = (SourceBean) serviceResponse.getAttribute("M_VerificaEsistenzaSAP");
	if (rowVerifica != null && rowVerifica.containsAttribute("CODMINSAP")) {
		identificativoSap = rowVerifica.getAttribute("CODMINSAP").toString();
	}
	
	boolean disableRichiestaSap = false;
	if (identificativoSap == null || ("").equalsIgnoreCase(identificativoSap)) {
		disableRichiestaSap = true;
	}
%>

<html>
<head>
<title>Dettaglio Bonus</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />

<af:linkScript path="../../js/" />

<script language="Javascript">
	
<%//Genera il Javascript che si occuperà di inserire i links nel footer
if (cdnLavoratore != null)
	attributi.showHyperLinks(out, requestContainer, responseContainer, "cdnLavoratore=" + cdnLavoratore);%>
			
function tornaAllaLista() {
	<%
	String url = (String )sessionContainer.getAttribute("_BACKURL_");
	%>
    // Solo con CDNLAV nullo, ovvero quando non trovo in anagrafica
    if (isInSubmit()) return;
    url="AdapterHTTP?"+"<%=url%>";
    setWindowLocation(url);
}

function tornaAllaListaLinguettaPN() {
	var urlpage="AdapterHTTP?";
   	urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&PAGE=PoliticheAttiveLavPage&CDNLAVORATORE=<%=cdnLavoratore%>";
    setWindowLocation(urlpage);
}

function controllaCampi() {
	document.Frm1.OPERAZIONEBONUS.value = 'AGGIORNA';
	if (document.Frm1.flgPresaCarico.checked) {
		document.Frm1.PRESOINCARICO.value = 'S';
	}
	else {
		document.Frm1.PRESOINCARICO.value = '';
	}
}

function callVerificaEsistenzaSAPWS() {
    if(!confirm("Sicuro di voler invocare la verifica esistenza SAP?")) {
        return false;
    }
    document.Frm1.OPERAZIONEBONUS.value = 'VERIFICASAP';
    doFormSubmit(document.Frm1);
}

function callWS() {
	if(!confirm("Sicuro di voler invocare la richiesta SAP?")) {
        return false;
    }
   	var urlpage="AdapterHTTP?";
   	urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&PAGE=SapVisualizzaPage&MODULE=M_SapCallRichiestaSap&CODMINSAP=<%=identificativoSap%>&CDNLAVORATORE=<%=cdnLavoratore%>";

  	window.open(urlpage,'RichiestaSAP','toolbar=NO,statusbar=YES,width=700,height=600,top=50,left=100,scrollbars=YES,resizable=YES');
}

function callPoliticheAttive() {
	var urlpage="AdapterHTTP?";
	urlpage+="PAGE=PoliticheAttiveBonusPage&PRGSPNOTIFICAPN=<%=prgNotificaPn%>";
	window.open(urlpage,'Politiche Attive','toolbar=NO,statusbar=YES,width=700,height=600,top=50,left=100,scrollbars=YES,resizable=YES');
}

</script>
</head>

<body class="gestione">
	<%if (!loadLinguette) {%>
		<script language="Javascript">
			window.top.menu.caricaMenuLav(<%=cdnFunzione%>,<%=cdnLavoratore%>);
		</script>
	<%}%>
	<%if (cdnLavoratore != null) {
		InfCorrentiLav testata = new InfCorrentiLav(RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
		testata.show(out);
	}
	if (loadLinguette) {
		Linguette l  = new Linguette(user, Integer.parseInt(cdnFunzione), "PoliticheAttiveLavPage", new BigDecimal(cdnLavoratore));
		l.show(out);	
	}
	%>
	<p>
	  <font color="green">
		<af:showMessages prefix="M_AggiornaBonusOccupazionale" />
		<af:showMessages prefix="M_VerificaEsistenzaSAP" />
	  </font>
	  <font color="red"><af:showErrors /></font>
	</p>
	<font color="red"><af:showErrors /></font>

	<af:form method="POST" action="AdapterHTTP" name="Frm1">
		<p class="titolo">Dettaglio notifica Crescere in Digitale</p>
		<% out.print(htmlStreamTop); %>
		<br>
		<table class="main">
			
			<tr>
				<td class="etichetta2">Data ricezione &nbsp;</td>
				<td class="campo"><af:textBox classNameBase="input"
						type="date" name="datRicezione" title="Data ricezione"
						value="<%=datRicezione%>" readonly="true" size="12" maxlength="10" /></td>
				<td></td>
			</tr>
			
			<tr>
				<td class="etichetta2">Data adesione &nbsp;</td>
				<td class="campo"><af:textBox classNameBase="input"
						type="date" name="datAdesione" title="Data adesione"
						value="<%=datAdesione%>" readonly="true" size="12" maxlength="10" /></td>
				<td></td>
			</tr>
			
			<tr>
				<td class="etichetta2">Codice fiscale</td>
				<td class="campo"><af:textBox classNameBase="input"
						name="strCodiceFiscale" title="Codice fiscale" readonly="true"
						value="<%=strCodiceFiscale%>" /></td>
			</tr>
			
			<tr>
				<td class="etichetta2">Provincia&nbsp;</td>
				<td class="campo">
						<af:comboBox name="codProvincia" multiple="false"
                        moduleName="M_GetProvince" disabled="<%=String.valueOf(!canModify)%>"
                        classNameBase="input" addBlank="true"
                        selectedValue="<%=Utils.notNull(codProvincia)%>"  />
				</td>
			</tr>
			
			<tr>
				<td class="etichetta2">Identificativo SAP</td>
				<td class="campo"><af:textBox classNameBase="input"
						name="identificativoSap" title="Identificativo SAP" readonly="true"
						value="<%=Utils.notNull(identificativoSap)%>" /></td>
			</tr>
			
			<tr>
				<td class="etichetta2">Presa in carico</td>
  			    <td class="campo">  	    
        		<input type="checkbox" name="flgPresaCarico" value="<%=Utils.notNull(flgPresoCarico)%>" <%=checkPresaCarico%> 
        			readonly="<%=String.valueOf(canModify)%>"/>
        		</td>
        	</tr>
        	
        	<tr class="note">
				<td class="etichetta">Note</td>
				<td class="campo"><af:textArea name="noteBonus" cols="60" readonly="<%=String.valueOf(!canModify)%>"
					rows="4" title="Note" classNameBase="input" value="<%=txtNoteBonus%>" /></td>
			</tr>
		
		</table>
		
		<table>
		  <tr>
		  <%
		  if (canModify) {%>
		    <td>
		  	<input class="pulsante" type="submit" name="save" value="Aggiorna" onclick="controllaCampi()">&nbsp;
		  	</td>
		  <%}
		  if (canCallSAP) {%>
		 	<td>			
			<input class="pulsanti<%=((disableRichiestaSap)?"Disabled":"")%>" onclick="callWS()" 
				type="button" name="richiesta" value="Richiesta SAP"  <%=(disableRichiestaSap)?"disabled=\"True\"":""%>>&nbsp;
			</td>
		  <%}
		  if (canVerificaSAP) {%>	
		  	<td>
			<input class="pulsante" onclick="callVerificaEsistenzaSAPWS()" type="button" name="verifica" value="Verifica Esistenza SAP">&nbsp;
			</td>
		  <%}%>
		  <td>
		  <input class="pulsante" onclick="callPoliticheAttive()" type="button" name="politicheAttive" value="Politiche Attive">
		   </td>
		  </tr>
		 </table>
		
		<br>
		<center>
		<% operatoreInfo.showHTML(out); %>
		</center>
		<% out.print(htmlStreamBottom); %>
		
		<input type="hidden" name="PAGE" value="DettaglioBonusPage" />
		<input type="hidden" name="cdnUtIns" value="<%=Utils.notNull(cdnUtIns)%>" />
		<input type="hidden" name="dtmIns" value="<%=Utils.notNull(dtmIns)%>" />
		<input type="hidden" name="cdnUtMod" value="<%=Utils.notNull(cdnUtMod)%>" />
		<input type="hidden" name="dtmMod" value="<%=Utils.notNull(dtmMod)%>" />
		<input type="hidden" name="OPERAZIONEBONUS" value="" />
		<input type="hidden" name="PRESOINCARICO" value="" />
		<%if (cdnLavoratore != null) {%>
		<input type="hidden" name="cdnLavoratore" value="<%=Utils.notNull(cdnLavoratore)%>">
		<%}%>
		<input type="hidden" name="cdnFunzione" value="<%=Utils.notNull(cdnFunzione)%>">
		<input type="hidden" name="prgspnotificapn" value="<%=Utils.notNull(prgNotificaPn)%>">
		<input type="hidden" name="numklospnotificapn" value="<%=Utils.notNull(NUMKLONOTIFICA)%>">
		<center>
		<%if (!loadLinguette) {%>
			<input class="pulsante" type = "button" name="torna" value="Torna alla lista" onclick="tornaAllaLista()"/>
		<%} else {%>
			<input type="hidden" name="LOADLINGUETTA" value="1" />
        	<input class="pulsante" type = "button" name="torna" value="Torna alla lista" onclick="tornaAllaListaLinguettaPN()"/>
        <%}%>
    </center>	
	</af:form>

</body>
</html>
