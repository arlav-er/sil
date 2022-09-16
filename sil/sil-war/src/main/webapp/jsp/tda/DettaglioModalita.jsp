<%@ page contentType="text/html;charset=utf-8"%>

<%@ page
	import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  it.eng.sil.module.voucher.*,
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

<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%@ taglib uri="aftags" prefix="af"%>

<%
String _page = (String) serviceRequest.getAttribute("PAGE");
String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
String codStatoVch = StringUtils.getAttributeStrNotNull(serviceRequest,"CODSTATOVOUCHER");
ProfileDataFilter filter = new ProfileDataFilter(user, _page);
PageAttribs attributi = new PageAttribs(user, _page);
boolean canModify = false;
String btnChiudi = "Chiudi senza aggiornare";

if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
} else {
	// NOTE: Attributi della pagina (pulsanti e link)
	canModify = attributi.containsButton("SALVA");
	if (canModify) {
		if (!codStatoVch.equalsIgnoreCase(StatoEnum.ATTIVATO.getCodice())) {
			canModify = false;
			btnChiudi = "Torna alla lista";
	  	}
	}
	else {
		btnChiudi = "Torna alla lista";
	}
}
String titlePagina = "Modalità e Durata";
Testata operatoreInfo = null;
String cdnUtIns = "";
String dtmIns = "";
String cdnUtMod = "";
String dtmMod = "";
BigDecimal prgModModalita = null;
BigDecimal prgVoucher = null;
BigDecimal numklomodalita = null;
String sPrgVoucher = "";
String sPrgModModalita = "";
String sNumkloModalita = "";
String codTipologiaDurata = "";
String codModalitaErogazione = "";
String strValUnitario = "";
String strValTotale = "";
String strSpesaEffettiva = "";
BigDecimal durataMinima = null;
BigDecimal durataMassima = null;
BigDecimal durataEffettiva = null;
String sDurataMinima = "";
String sDurataMassima = "";
String sDurataEffettiva = "";
String tipoRimborso = "";
BigDecimal percentualeMinima = null;
String sPercMinima = "";
String descModalitaErog = "";

SourceBean rowModalita = (SourceBean)serviceResponse.getAttribute("M_DettaglioModalitaTDA.ROWS.ROW");
if (rowModalita != null) {
	prgModModalita = (BigDecimal) rowModalita.getAttribute("PRGMODMODALITA");
	prgVoucher = (BigDecimal) rowModalita.getAttribute("PRGMODVOUCHER");
	durataMinima = (BigDecimal) rowModalita.getAttribute("NUMDURATAMIN");
	durataMassima = (BigDecimal) rowModalita.getAttribute("NUMDURATAMAX");
	durataEffettiva = (BigDecimal) rowModalita.getAttribute("NUMDURATAEFFETTIVA");
	percentualeMinima = (BigDecimal) rowModalita.getAttribute("NUMPERCPARCOMPLET");
	codTipologiaDurata = SourceBeanUtils.getAttrStrNotNull(rowModalita, "CODTIPOLOGIADURATA");
	codModalitaErogazione = SourceBeanUtils.getAttrStrNotNull(rowModalita, "CODVCHMODEROG");
	strValUnitario = SourceBeanUtils.getAttrStrNotNull(rowModalita, "STRVALUNITARIO");
	strValTotale = SourceBeanUtils.getAttrStrNotNull(rowModalita, "STRVALTOTALE");
	strSpesaEffettiva = SourceBeanUtils.getAttrStrNotNull(rowModalita, "STRSPESAEFFETTIVA");
	tipoRimborso = SourceBeanUtils.getAttrStrNotNull(rowModalita, "STRTIPORIMBORSO");
	descModalitaErog = SourceBeanUtils.getAttrStrNotNull(rowModalita, "descModalitaErog");
	cdnUtIns = rowModalita.getAttribute("cdnutins").toString();
	dtmIns = rowModalita.getAttribute("dtmins").toString();
	cdnUtMod = rowModalita.getAttribute("cdnutmod").toString();
	dtmMod = rowModalita.getAttribute("dtmmod").toString();
	operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
	numklomodalita = (BigDecimal) rowModalita.getAttribute("NUMKLOVCHMODALITA");
	numklomodalita = numklomodalita.add(new BigDecimal(1));
	sPrgVoucher = prgVoucher.toString();
	sPrgModModalita = prgModModalita.toString();
	sNumkloModalita = numklomodalita.toString();
	if (durataMinima != null) {
		sDurataMinima = durataMinima.toString();
	}
	if (durataMassima != null) {
		sDurataMassima = durataMassima.toString();
	}
	if (durataEffettiva != null) {
		sDurataEffettiva = durataEffettiva.toString();
	}
	if (percentualeMinima != null) {
		sPercMinima = percentualeMinima.toString();
	}
}

if (serviceResponse.containsAttribute("M_CHECK_SELEZIONE_MODALITA_TDA.MODALITAGIAATTIVA")) {
	canModify = false;
	btnChiudi = "Torna alla lista";	
}

String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>

<html>
<head>
<title>Modalità e Durata</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />

<script type="text/Javascript">
	var isEditing = <%=canModify%>;

	function settaOperazione(operazione) {
		document.Frm1.OPERAZIONEMODALITA.value = operazione;
		if (operazione == 'CHIUDI') {
			doFormSubmit(document.Frm1);
		}
	}

	function checkDurata() {
		return true;
	}
	
	function controllaCampi() {

		if(!isEditing){
			return true;
		}
		
        if (!controllaFixedFloat('durataEffettiva', 7, 2)) {
            return false;
        }

        return true;
    } 
	
</script>

</head>

<body class="gestione" onload="rinfresca();">

<p>
 	<font color="green">
  	</font>
  	<font color="red"><af:showErrors /></font>
</p>

<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="controllaCampi() && checkDurata()">
	<p class="titolo"><%=titlePagina%></p>
	<% out.print(htmlStreamTop); %>
	<br>
	<table class="main">
		<tr>
			<td>
				<table class="main">
					<tr> 
						<td class="etichettaLeft" nowrap>Modalità&nbsp;</td>
						<td class="campoBigger">
							<af:textBox type="text" name="descModalitaErog" title="Modalità"
						                        value="<%=descModalitaErog%>" classNameBase="inputMB"   readonly="true" size="80" />
							<input type="hidden" name="codModalitaErog" value="<%=codModalitaErogazione%>">
			            </td>
					</tr>
		    	</table>
			</td>
		<tr>
		</table>
		<table class="main">
		<!-- 	<td>
				<table class="main">
				<tr> -->
			  		<td class="etichettaLeft" nowrap>Tipo Durata&nbsp;</td>
			  		<td class="campoSmaller">
					  <af:comboBox disabled="true" name="codTipoDurata"  
                 	selectedValue="<%=codTipologiaDurata%>" moduleName="M_COMBO_MN_YG_TIPOLOGIA_DURATA" classNameBase="input" title="Tipo Durata"/>
					</td>
				<!-- </tr>
				</table>
			</td>
			<td>
				<table class="main">
				<tr> -->
			  		<td class="etichettaLeftSmall" nowrap>Durata Minima&nbsp;</td>
			  		<td class="campoSmaller">
					  <af:textBox type="integer" name="durataMin" title="Durata Minima"
						  	value="<%=sDurataMinima%>" classNameBase="input" readonly="true" size="10"/>
					</td>
				<!-- </tr>
				</table>
			</td>
			<td>
				<table class="main">
				<tr> -->
					<td class="etichettaLeftSmall" nowrap>Durata Massima&nbsp;</td>
				  	<td class="campoSmaller">
						<af:textBox type="integer" name="durataMax" title="Durata Massima"
						  	value="<%=sDurataMassima%>" classNameBase="input" readonly="true" size="10"/>
					</td>
				<!-- </tr>
				</table>
			</td> -->
		
		</tr>
		
		<tr>
			<td class="etichettaLeft" nowrap>Valore per unità di durata EURO&nbsp;</td>
			<td class="campoBigger">
			<af:textBox type="text" name="valoreUnita" title="Valore per unità di durata Euro"
				value="<%=strValUnitario%>" classNameBase="input" readonly="true" size="10"/>
			</td>
		</tr>
		
		<tr>
			<td class="etichettaLeft" nowrap>Valore Totale/Massimo EURO&nbsp;</td>
			<td class="campoBigger">
			<af:textBox type="text" name="valoreTotale" title="Valore Totale/Massimo Euro"
				value="<%=strValTotale%>" classNameBase="input" readonly="true" size="10"/>
			</td>
		</tr>
		
		<tr>
			<!-- <td>
				<table class="main">
				<tr> -->
			  		<td class="etichettaLeft" nowrap>Importo Erogabile EURO&nbsp;</td>
			  		<td class="campoBigger">
					  <af:textBox type="text" name="importErogabile" title="Importo Erogabile Euro"
						  	value="<%=strSpesaEffettiva%>" classNameBase="input" readonly="true" size="10"/>
					</td>
				<!-- </tr>
				</table>
			</td>
			<td>
				<table class="main">
				<tr> -->
					<td class="etichettaLeft" nowrap>Tipologia Rimborso&nbsp;</td>
				  	<td class="campoBigger">
						<af:textBox type="text" name="tipologiaRimborso" title="Tipologia Rimborso"
						  	value="<%=tipoRimborso%>" classNameBase="input" readonly="true" size="10"/>
					</td>
				<!-- </tr>
				</table>
			</td> -->
		</tr>
		
		<tr>
			<!-- <td>
				<table class="main">
				<tr> -->
					<td class="etichettaLeft" nowrap>Durata Effettiva&nbsp;</td>
				  	<td class="campoBigger">
						<af:textBox type="number" name="durataEffettiva" title="Durata Effettiva" validateOnPost="true"
						  	value="<%=sDurataEffettiva%>" classNameBase="input" readonly="<%=String.valueOf(!canModify)%>" size="5"/>
					</td>
				<!-- </tr>
				</table>
			</td>
			<td>
				<table class="main">
				<tr> -->
					<td class="etichettaLeft" nowrap>Perc. minima %&nbsp;</td>
					<td class="campoBigger">
					<af:textBox type="text" name="percMinima" title="Perc. minima %"
						value="<%=sPercMinima%>" classNameBase="input" readonly="true" size="10"/>
					</td>
				<!-- </tr>
				</table>
			</td> -->
		</tr>
	
	</table>
	
	<br>
    <table>
    <tr>
    <td align="center">
    <%
    if (canModify) {%>
    	<input type="submit" class="pulsanti" name="AGGIORNAMODALITA" value="Aggiorna" onclick="settaOperazione('AGGIORNA');">
    <%}%>
    </td>
    <td align="center">
    	<input type="button" class="pulsanti" name="CHIUDIMODALITA" value="<%=btnChiudi%>" onclick="settaOperazione('CHIUDI');">
    </td>
    </tr>
    </table>
    
	<%if (operatoreInfo != null) {%>
  		<br>
		<center>
		<% 
		operatoreInfo.showHTML(out);
		%>
		</center>
	<%}
	out.print(htmlStreamBottom); 
	%>
	<input type="hidden" name="PAGE" value="TDAModalitaPage">
	<input type="hidden" name="cdnFunzione" value="<%=Utils.notNull(_funzione)%>">
	<input type="hidden" name="PRGVOUCHER" value="<%=sPrgVoucher%>">
	<input type="hidden" name="PRGMODMODALITA" value="<%=sPrgModModalita%>">
	<input type="hidden" name="NUMKLOVCHMODALITA" value="<%=sNumkloModalita%>">
	<input type="hidden" name="OPERAZIONEMODALITA" value="">
</af:form>

</body>
</html>