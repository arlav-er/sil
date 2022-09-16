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
 PageAttribs attributi = new PageAttribs(user, "RicercaPratichePatronatoPage");
 
 String _funzione = (String)serviceRequest.getAttribute("CDNFUNZIONE");
 
 String datInizio           	= StringUtils.getAttributeStrNotNull(serviceRequest,"datInizio");
 String datFine             	= StringUtils.getAttributeStrNotNull(serviceRequest,"datFine");
 String flgDid              	= StringUtils.getAttributeStrNotNull(serviceRequest,"flgDid");
 String flgDomandeMobilita  	= StringUtils.getAttributeStrNotNull(serviceRequest,"flgDomandeMobilita");
 String patronato 				= StringUtils.getAttributeStrNotNull(serviceRequest,"patronato");
 String cdnTipoPatronato 		= StringUtils.getAttributeStrNotNull(serviceRequest,"cdnTipoPatronato");
 String cdnUfficioPatronato 	= StringUtils.getAttributeStrNotNull(serviceRequest,"cdnUfficioPatronato");
 String cdnOperatorePatronato 	= StringUtils.getAttributeStrNotNull(serviceRequest,"cdnOperatorePatronato");
 String tipoRicerca      		= StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRicerca");

 // se nella pagina risultato della ricerca è assente
 // il valore flgDid o flgDomandeMobilita allora bisogna
 // forzare il valore 'N', altrimenti, essendo il check true
 // di default non si riesce a capire se deve essere false
 if (!flgDid.equalsIgnoreCase("N")) {
	 flgDid = "S";
 }
 
 if (!flgDomandeMobilita.equalsIgnoreCase("N")) {
	 flgDomandeMobilita = "S";
 }
 
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
<title>Ricerca sull'anagrafica lavoratori</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<af:linkScript path="../../js/" />
<script language="JavaScript" src="../../js/script_comuni.js"></script>

<script language="Javascript">
	<% 
		//Genera il Javascript che si occuperà di inserire i links nel footer
		attributi.showHyperLinks(out, requestContainer,responseContainer,"");
    %>

    // controlla se la data di fine è successiva alla data inizio
    function dateInizioFineOk() {
	
		var dataInizioObj = document.Frm1['datInizio'];
		var dataFineObj = document.Frm1['datFine'];
		var dataInizio = new String(dataInizioObj.value);
		var dataFine = new String(dataFineObj.value);
		
		if (dataFine === "") {
			return true;
		}
		
		if (compareDate(dataInizio,dataFine) > 0) {
			return false;
		}
		
		return true;
		
    }

	function checkDate (dataObj) {
		if (dataObj.value === "") {
			return true;
		}
		return checkFormatDate(dataObj);	
	}
	
	function checkCampiObbligatori() {
				
		var errorMsg = "";
		
		if (!checkDate(document.Frm1['datInizio'])) {
			errorMsg += "\n - Data inizio non corretta";	
		}

		if (!checkDate(document.Frm1['datFine'])) {
			errorMsg += "\n - Data fine non corretta";	
		}

		if (!document.Frm1['flgDid'].checked && !document.Frm1['flgDomandeMobilita'].checked) {
			errorMsg += "\n - Selezionare almeno un tipo di pratica da filtrare";
		}
		
		if (!dateInizioFineOk()) {
			errorMsg += "\n - Data fine non può essere precedente a data inizio";
		}		

		if (errorMsg !== "") {
			alert("Attenzione:\n" + errorMsg);
			return false;
		} 

		return true;

	}

	function resetPatronato() {
        
		document.Frm1['cdnOperatorePatronato'].value = "";
		document.Frm1['cdnUfficioPatronato'].value = "";
		document.Frm1['cdnTipoPatronato'].value = "";
    	
	}

	function resetPatronatoRadio () {

  		<% if (cdnOperatorePatronato.equalsIgnoreCase("")) { %>
		document.getElementById('flgOperatorePatronato').checked = false;
		<% } %>
		
		<% if (cdnUfficioPatronato.equalsIgnoreCase("")) { %>
		document.getElementById('flgUfficioPatronato').checked = false;
		<% } %>

		<% if (cdnTipoPatronato.equalsIgnoreCase("")) { %>
		document.getElementById('flgTipoPatronato').checked = false;
		<% } %>
    	  
	}

	function updatePatronato (selectedCombo,radioButtonToSelect) {

		if (selectedCombo !== 'cdnOperatorePatronato') {
			document.Frm1['cdnOperatorePatronato'].value = "";
		}

		if (selectedCombo !== 'cdnUfficioPatronato') {
			document.Frm1['cdnUfficioPatronato'].value = "";
		}

		if (selectedCombo !== 'cdnTipoPatronato') {
			document.Frm1['cdnTipoPatronato'].value = "";
		}

		document.getElementById(radioButtonToSelect).checked = true;
	
	}

	function aggiornaDescrizioni() {

		var operatorePatronatoCombo = document.Frm1['cdnOperatorePatronato'];
		var operatorePatronatoHidden = document.Frm1['strOperatorePatronato'];
		if (operatorePatronatoCombo.value != "") {
			operatorePatronatoHidden.value = operatorePatronatoCombo.options[operatorePatronatoCombo.selectedIndex].text;	
		}

		var tipoPatronatoCombo = document.Frm1['cdnTipoPatronato'];
		var tipoPatronatoHidden = document.Frm1['strTipoPatronato'];
		if (tipoPatronatoCombo.value != "") {
			tipoPatronatoHidden.value = tipoPatronatoCombo.options[tipoPatronatoCombo.selectedIndex].text;	
		}

		var ufficioPatronatoCombo = document.Frm1['cdnUfficioPatronato'];
		var ufficioPatronatoHidden = document.Frm1['strUfficioPatronato'];
		if (ufficioPatronatoCombo.value != "") {
			ufficioPatronatoHidden.value = ufficioPatronatoCombo.options[ufficioPatronatoCombo.selectedIndex].text;	
		}

		return true;

	}

</script>

</head>
<body class="gestione" onload="rinfresca()">
	<p class="titolo">Ricerca pratiche Patronati</p>
	<%out.print(htmlStreamTop);%>
	<af:form 	method="POST" 
				action="AdapterHTTP" 
				name="Frm1"
				onSubmit="checkCampiObbligatori() && aggiornaDescrizioni()" >
				
		<table class="main">

			<tr>
				<td colspan="2" /><br/></td>
			</tr>

			<tr>
				<td class="etichetta">Data inizio</td>
				<td class="campo">
					<af:textBox 	validateOnPost="true" 
									type="date"
									name="datInizio" 
									value="<%=datInizio%>"
									title="Data inizio" 
									size="10" 
									maxlength="10" />
				</td>
			</tr>

			<tr>
				<td class="etichetta">Data fine</td>
				<td class="campo">
					<af:textBox 	validateOnPost="true" 
									type="date"
									name="datFine" 
									value="<%=datFine%>"
									title="Data fine" 
									size="10" 
									maxlength="10" />
				</td>
			</tr>

			<tr>
				<td colspan="2" />&nbsp;</td>
			</tr>

			<tr>
				<td class="etichetta" nowrap valign="top">Tipo pratica</td>
				<td class="campo">
					
					<input 	type="checkbox" 
							name="flgDid"
							value="S" <% if (flgDid.equals("S")) {%> CHECKED <%}%> /> DID 
							
					<br />
					
					<input 	type="checkbox" 
							name="flgDomandeMobilita" 
							value="S"
							<% if (flgDomandeMobilita.equals("S")) {%> CHECKED <%}%> /> Domande di mobilità</td>
			</tr>

			<tr>
				<td colspan="2" />&nbsp;</td>
			</tr>

			<tr>
				<td class="etichetta" nowrap valign="top">Patronato</td>
				<td class="campo">
					
					<table>
					
						<tr>
							<td>
							
							<input 	id="flgOperatorePatronato"
							type="radio" 
							name="patronato"
							value="flgOperatorePatronato" <%if(patronato.equalsIgnoreCase("flgOperatorePatronato")){%>CHECKED<%}%> 
							onchange="resetPatronato();" />
					
							Operatore&nbsp;&nbsp;&nbsp;
							
							</td>
							<td>
							<af:comboBox 	classNameBase="input" 
									name="cdnOperatorePatronato" 
									selectedValue="<%=cdnOperatorePatronato%>"
									title="Tipo patronato" 
									moduleName="M_LISTA_OPERATORE_PATRONATO"
									addBlank="true" 
									onChange="updatePatronato('cdnOperatorePatronato','flgOperatorePatronato');" />
					
							<input 	type="hidden"
							name="strOperatorePatronato"
							value="" />
							</td>
						</tr>
						
						<tr>
					<td>
				
				
					<input 	id="flgUfficioPatronato"
							type="radio" 
							name="patronato"
							value="flgUfficioPatronato" <%if(patronato.equalsIgnoreCase("flgUfficioPatronato")){%>CHECKED<%}%> 
							onchange="resetPatronato();" /> 
					
					Ufficio
					</td>
					<td>
					
					<af:comboBox	classNameBase="input" 
									name="cdnUfficioPatronato" 
									selectedValue="<%=cdnUfficioPatronato%>"
									title="Tipo patronato" 
									moduleName="M_LISTA_UFFICIO_PATRONATO"
									addBlank="true" 
									onChange="updatePatronato('cdnUfficioPatronato','flgUfficioPatronato');" />
					
					<input 	type="hidden"
							name="strUfficioPatronato"
							value="" />
					
				</td>
			</tr>

			<tr>
				
				<td>
					
					<input 	id="flgTipoPatronato"
							type="radio" 
							name="patronato"
							value="flgTipoPatronato" <%if(patronato.equalsIgnoreCase("flgTipoPatronato")){%>CHECKED<%}%> 
							onchange="resetPatronato();" />
					
					Tipo
				
				</td>
				<td>
					<af:comboBox	classNameBase="input" 
									name="cdnTipoPatronato"
									selectedValue="<%=cdnTipoPatronato%>" 
									title="Tipo patronato"
									moduleName="M_LISTA_TIPO_PATRONATO" 
									addBlank="true" 
									onChange="updatePatronato('cdnTipoPatronato','flgTipoPatronato');" />
					
					<input 	type="hidden"
							name="strTipoPatronato"
							value="" />
					
				</td>
			</tr>
						
					</table>
					
					
					
					
					
				</td>
			</tr>

			

			<tr>
				<td colspan="2" />&nbsp;
				</td></tr>

			<tr>
				<td class="etichetta" valign="top">Tipo ricerca</td>
				<td class="campo">
					<table colspacing="0" colpadding="0" border="0">
						<tr>
							<td>
								<input 	type="radio" 
										name="tipoRicerca" 
										value="conteggio"
										<%if(!tipoRicerca.equalsIgnoreCase("elenco")){%>CHECKED<%}%> /> Conteggio numerico <br /> 
								
								<input 	type="radio"
										name="tipoRicerca" 
										value="elenco"
										<%if(tipoRicerca.equalsIgnoreCase("elenco")){%>CHECKED<%}%> /> Elenco pratiche</td>
						</tr>
					</table>
				</td>
			</tr>

			<tr>
				<td colspan="2">&nbsp;</td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					
					<input 	class="pulsanti" 
							type="submit" 
							name="cerca" 
							value="Cerca" /> &nbsp;&nbsp; 
					
					<input 	type="button" 
							class="pulsanti" 
							value="Annulla" 
							onclick = "this.form.reset();resetPatronatoRadio();"/>
					
				</td>
			</tr>
			<input type="hidden" name="PAGE" value="ListaPratichePatronatoPage" />
			<input type="hidden" name="cdnFunzione" value="<%=_funzione%>" />

		</table>
	</af:form>

	<%out.print(htmlStreamBottom);%>
</body>
</html>
