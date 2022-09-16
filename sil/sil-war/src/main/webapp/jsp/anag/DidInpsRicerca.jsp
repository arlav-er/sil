<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/Function_CommonRicercaComune.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page
	import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.StringUtils,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*"%>


<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%
	// NOTE: Attributi della pagina (pulsanti e link) 
	PageAttribs attributi = new PageAttribs(user, "listaDidInpsPage");
	boolean canModify = attributi.containsButton("nuovo");

	String _funzione = (String) serviceRequest
			.getAttribute("CDNFUNZIONE");

	String strCodiceFiscale = StringUtils.getAttributeStrNotNull(
			serviceRequest, "strCodiceFiscale");
	String datstipulada = StringUtils.getAttributeStrNotNull(
			serviceRequest, "datstipulada");
	String datstipulaa = StringUtils.getAttributeStrNotNull(
			serviceRequest, "datstipulaa");
	String codCpi = StringUtils.getAttributeStrNotNull(
            serviceRequest, "codCpi");
	String tipoRicerca = StringUtils.getAttributeStrNotNull(
			serviceRequest, "tipoRicerca");
	String codDID = StringUtils.getAttributeStrNotNull(
            serviceRequest, "codDID");
%>

<%
	/*
	 NOTA: le pagine di ricerca devono avere lo stile prof_ro -> quindi invece di
	 canModify si deve passare il valore false
	 */
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>
<head>
<title>Ricerca DID INPS</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<af:linkScript path="../../js/" />

<script language="Javascript">
	
<%//Genera il Javascript che si occuperà di inserire i links nel footer
			attributi.showHyperLinks(out, requestContainer, responseContainer,
					"");%>
	function checkCampiObbligatori() {
		if (((document.Frm1.strCodiceFiscale.value == '') || (document.Frm1.strCodiceFiscale.value.length >= 6))) {
			return true;
		} else {
			alert("Inserire almeno sei caratteri del codice fiscale");
			return false;
		}
	}
</script>



</head>
<body class="gestione" onload="rinfresca()">
	<p class="titolo">Ricerca DID INPS</p>
	<%
		out.print(htmlStreamTop);
	%>
	<af:form method="POST" action="AdapterHTTP" name="Frm1"
		onSubmit="checkCampiObbligatori()">
		<table class="main">
			<tr>
				<td colspan="2" />
				<br />Per effettuare una ricerca inserire almeno i primi sei
				caratteri del codice fiscale
				</td>
			</tr>
			<tr>
				<td colspan="2">&nbsp;</td>
			</tr>
			<tr>
			<td class="etichetta">Codice fiscale</td>
			<td class="campo"><af:textBox type="text"
					name="strCodiceFiscale" value="<%=strCodiceFiscale%>" size="20"
					maxlength="16" /></td>
			</tr>

			<tr>
				<td colspan="2">&nbsp;</td>
			</tr>
			<tr>
				<td class="etichetta">tipo ricerca</td>
				<td class="campo">
					<table colspacing="0" colpadding="0" border="0">
						<tr>
							<%
								if (tipoRicerca.equals("iniziaPer")) {
							%>
							<td><input type="radio" name="tipoRicerca" value="esatta" />
								esatta&nbsp;&nbsp;&nbsp;&nbsp;</td>
							<td><input type="radio" name="tipoRicerca" value="iniziaPer"
								CHECKED /> inizia per</td>
							<%
								} else {
							%>
							<td><input type="radio" name="tipoRicerca" value="esatta"
								CHECKED /> esatta&nbsp;&nbsp;&nbsp;&nbsp;</td>
							<td><input type="radio" name="tipoRicerca" value="iniziaPer" />
								inizia per</td>
							<%
								}
							%>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td colspan="2"><hr /></td>

			</tr>
			<TR>
				<td class="etichetta" nowrap>Data stipula da</td>
				<td class="campo"><af:textBox type="date" name="datstipulada"
						title="Data inizio da" validateOnPost="true"
						value="<%=datstipulada%>" size="10" maxlength="10" />
					&nbsp;&nbsp;a&nbsp;&nbsp; <af:textBox type="date"
						name="datstipulaa" title="Data inizio a" validateOnPost="true"
						value="<%=datstipulaa%>" size="10" maxlength="10" /></td>
			</TR>
			
			<tr>
				<td colspan="2"><hr /></td>

			</tr>
			
			</tr>
				<td class="etichetta">Lavoratori</td>
				<td class="campo">
					<af:comboBox 	name="CodDID" 
									classNameBase="input"
                 					title = "DID">
						<option value="T" <% if ( "T".equalsIgnoreCase(codDID) )  { %>SELECTED="true"<% } %>>Tutti</option>
	     				<option value="D"<% if ( "D".equalsIgnoreCase(codDID) )  { %>SELECTED="true"<% } %>>Con DID</option>
	     				<option value="S"<% if ( "S".equalsIgnoreCase(codDID) )  { %>SELECTED="true"<% } %>>Senza DID</option>
     					<option value="N"<% if ( "N".equalsIgnoreCase(codDID) )  { %>SELECTED="true"<% } %>>Non a Sistema</option>
     				</af:comboBox>
				</td>
			</tr>
			
			<tr>
				<td colspan="2"><hr /></td>
			</tr>
			
			<tr>
				<td class="etichetta">Centro per l'Impiego competente</td>
				<td class="campo"><af:comboBox name="CodCPI"
						moduleName="M_ELENCOCPI" addBlank="true"
						selectedValue="<%=codCpi%>" /></td>
			</tr>

			<tr>
				<td colspan="2">&nbsp;</td>
			</tr>
			<tr>
				<td colspan="2" align="center"><input class="pulsanti"
					type="submit" name="cerca" value="Cerca" /> &nbsp;&nbsp; <input
					type="reset" class="pulsanti" value="Annulla" /></td>
			</tr>
			<input type="hidden" name="PAGE" value="DidInpsRicercaPage" />
			<input type="hidden" name="cdnFunzione" value="<%=_funzione%>" />
		</table>
	</af:form>




	<%
		out.print(htmlStreamBottom);
	%>
</body>
</html>

