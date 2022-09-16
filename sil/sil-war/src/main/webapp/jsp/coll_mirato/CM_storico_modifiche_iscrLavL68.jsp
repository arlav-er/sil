<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ page contentType="text/html;charset=utf-8"
	import="com.engiweb.framework.base.*,
			it.eng.sil.security.*,
			it.eng.afExt.utils.*,
			it.eng.sil.util.*,
			java.math.*,
			java.util.*"
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%@ taglib uri="aftags" prefix="af"%>

<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%	

	String _page = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 
	int cdnfunzione   = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	String contesto = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"CONTEXT");
	 
	String paramCmIsc1 = null;
	SourceBean paramSb = (SourceBean) serviceResponse.getAttribute("M_GetParamCmIsc1.ROWS.ROW");				
	if (paramSb != null) {	
		paramCmIsc1 = paramSb.getAttribute("PARAM_CM_ISC_1").toString();
	} 
	
	
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
	
%>

<html>
<head>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<script language="JavaScript" src="../../js/script_comuni.js"></script>

<af:linkScript path="../../js/" />

<script>

var imgChiusa = "../../img/chiuso.gif";
var imgAperta = "../../img/aperto.gif";	   
function terminaAggiornaIscr() {
	if (isRequired('CODMOTIVOMODIFICA')) {
	  	window.opener.document.Frm1.INSSTORICO.value = 'TRUE';
		window.opener.document.Frm1.CODMONOMOTIVOMODIFICA.value = document.Frm1.CODMOTIVOMODIFICA.value;  
		window.opener.document.Frm1.submit();	
		window.close();	
	}
}

function cambia(immagine, sezione) {
	if (sezione.style.display == 'inline') {
		sezione.style.display = 'none';
		sezione.aperta = false;
		immagine.src = imgChiusa;
		immagine.alt = 'Apri';
	}
	else if (sezione.style.display == "none") {
		sezione.style.display = "inline";
		sezione.aperta = true;
		immagine.src = imgAperta;
		immagine.alt = "Chiudi";
	}
}


var sezioni = new Array();

function Sezione(sezione, img,aperta){    
	this.sezione=sezione;
	this.sezione.aperta=aperta;
	this.img=img;
}

function initSezioni(sezione){
	sezioni.push(sezione);
}

function checkPercentualeInvalidita() {
	if (document.Frm2.codMonoTipoRagg.value == 'A') {
		if (document.Frm2.NUMPERCINVALIDITA.value != '') {
			alert('La percentuale di invalidità non è valorizzabile nel caso di Altre categorie protette.');
			return false;
		}
		return true;
	} 
	else if (document.Frm2.codMonoTipoRagg.value == 'D') {
		if (document.Frm2.CODCMTIPOISCR.value == '01' || document.Frm2.CODCMTIPOISCR.value == '02' || document.Frm2.CODCMTIPOISCR.value == '03') {
			if (parseInt(document.Frm2.NUMPERCINVALIDITA.value) < 1 || parseInt(document.Frm2.NUMPERCINVALIDITA.value) > 8) {
				alert('La percentuale di invalidità in tali casi può assumere valore da 1 a 8.');
				return false;
			}
			return true;
		}	
		if (document.Frm2.CODCMTIPOISCR.value == '04') {
			if (parseInt(document.Frm2.NUMPERCINVALIDITA.value) < 34 || parseInt(document.Frm2.NUMPERCINVALIDITA.value) > 100) {
				alert('La percentuale di invalidità in tali casi può assumere valore da 34 a 100.');
				return false;
			}
			return true;
		}
		if (document.Frm2.CODCMTIPOISCR.value == '08' || 
			document.Frm2.CODCMTIPOISCR.value == '09' || 
			document.Frm2.CODCMTIPOISCR.value == '11' || 
			document.Frm2.CODCMTIPOISCR.value == '12' || 
			document.Frm2.CODCMTIPOISCR.value == '13') {
			if (parseInt(document.Frm2.NUMPERCINVALIDITA.value) < 46 || parseInt(document.Frm2.NUMPERCINVALIDITA.value) > 100) {
				alert('La percentuale di invalidità in tali casi può assumere valore da 46 a 100.');
				return false;
			}
			return true;
		}			
	} 
}

function controllaDati(){ 	
 	if((parseInt(document.Frm2.NUMPERCINVALIDITA.value) > 100 || isNaN(parseInt(document.Frm2.NUMPERCINVALIDITA.value))) 
 		&& document.Frm2.NUMPERCINVALIDITA.value!=""){
		alert("La percentuale del giudizio medico legale deve essere un intero minore o uguale a 100");
		return false;
	}
 	else {  	 	
 	 	return true;
 	}
  }

function chiudi() {
	window.close();	
}



</script>
</head>

<body class="gestione">

	<p align="center">

		<% if (contesto.equals("LISTA")) { %>

		<af:list moduleName="CM_Storico_Mod_IscrL68" skipNavigationButton="1" />
	<table class="main">
		<tr>
			<td align="center"><input type="button" class="pulsanti"
				value="Chiudi" onClick="window.close()"></td>
		</tr>
	</table>

	<% } else if (contesto.equals("DETTAGLIO")) { %>
	<%= htmlStreamTop %>
	<af:form name="Frm1" method="POST" action="AdapterHTTP" >

		<p class="titolo">E' stata modificata almeno una delle seguenti
			informazioni:</p>

		<table class="main" class="azzurro_bianco" border="0">
			<tr>
				<td width="33%"></td>
				<td colspan="2" align="left" width="33%">
					<ul>
						<li>Tipo invalidità
						<li>Percentuale invalidità <% if(paramCmIsc1 != null && ("1").equals(paramCmIsc1)) {%>
						
						<li>Annota per fuori lista <% } %>
						
						<li>Data accertamento sanitario
						<li>Tipo accertamento sanitario
						<li>Verbale accertamento
						<li>Operatore di riferimento
						<li>Note
					</ul></td>
				<td width="33%"></td>
			</tr>
			<tr>
				<td colspan="4" align="center">
					<p class="titolo">La modifica comporta la storicizzazione dei
						precedenti dati e necessita della specifica sul motivo della
						modifica</p></td>
			</tr>
			<tr>
				<td colspan="4">&nbsp;</td>
			</tr>
			<tr>
				<td width="33%"></td>
				<td colspan="2" align="center" width="33%">Motivo
					modifica&nbsp;&nbsp;&nbsp;&nbsp; <af:comboBox
						name="CODMOTIVOMODIFICA" title="Motivo modifica"
						classNameBase="input">
						<option value=""></option>
						<option value="M">Modifica situazione</option>
						<option value="A">Aggiornamento dati</option>
						<option value="E">Errore di digitazione</option>
					</af:comboBox>&nbsp;*</td>
				<td width="33%"></td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td width="33%"></td>
				<td colspan="2" width="33%"><input type="button"
					class="pulsanti" name="Conferma" value="Conferma"
					onclick="terminaAggiornaIscr()" /> &nbsp;&nbsp;&nbsp;&nbsp; <input
					type="button" class="pulsanti" value="Chiudi"
					onClick="window.close()"></td>
				<td width="33%"></td>
			</tr>
		</table>
	</af:form>
	<%= htmlStreamBottom %>

	<% } else if (contesto.equals("DIAGNOSI")) {
		%>
	
	
	<font color="red"><af:showErrors/></font>
	<af:showMessages prefix="CM_SAVE_ISCR"/>
	
	<%
	String datUltimaIscr = "";
	String codMonoTipoRagg = "";
	String codCmTipoIscr = "";
	String datAnzianita68 = "";
	String tipoInvalidita = "";
	String codTipoInvaliditaOld = "";
	String numPercInvalidita = "";
	String accertSanitario = "";
  	String codAccertSanitarioOld = "";
  	String datAccertSanitarioOld = "";
	String dataInizio = "";
	String dataFine = "";
	String motivoFineAtto = "";
	String codStatoAtto = "";
	String strNoteOld = "";
	String prgSpiModOld = "";
	String cdnUtModOld = "";
	String dtmModOld = "";
	String prgVerbaleAccOld = "";
	String codcmannota = "";
	String imgChiuso = "../../img/chiuso.gif";
  	String imgAperto = "../../img/aperto.gif";
  	boolean canUpdate = true;
  	
  	String cdnFunzione = "";
  	String cdnLavoratore = "";
  	String numPercInvaliditaDiagnosi = "";
  	
   	String prgCmIscr = "";
   	String prgVerbaleAcc = "";
  	String numKloCmIscr = "";
  	String codTipoInvalidita = "";
  	String codAccertSanitario = "";
	
	SourceBean listaModule = (SourceBean)serviceResponse.getAttribute("CM_LISTA_ISCRIZIONI_DISABILI_L68");
	Vector listaIscrizioni = listaModule.getAttributeAsVector("ROWS.ROW");
	int numeroIscrizioni = listaIscrizioni.size();
	if (numeroIscrizioni > 0) {
		SourceBean iscrizione = (SourceBean) listaIscrizioni.get(0);
		
		datUltimaIscr = iscrizione.getAttribute("DATULTIMAISCR") == null? "" : (String)iscrizione.getAttribute("DATULTIMAISCR");
		codMonoTipoRagg = iscrizione.getAttribute("codMonoTipoRagg") == null? "" : (String)iscrizione.getAttribute("codMonoTipoRagg");
		codCmTipoIscr = iscrizione.getAttribute("codCmTipoIscr") == null? "" : (String)iscrizione.getAttribute("codCmTipoIscr");
		datAnzianita68 = iscrizione.getAttribute("DATANZIANITA68") == null? "" : (String)iscrizione.getAttribute("DATANZIANITA68");
		tipoInvalidita =iscrizione.getAttribute("TIPOINVALIDITA") == null? "" : (String)iscrizione.getAttribute("TIPOINVALIDITA");
		codTipoInvaliditaOld =iscrizione.getAttribute("CODTIPOINVALIDITA") == null? "" : (String)iscrizione.getAttribute("CODTIPOINVALIDITA");
		numPercInvalidita =iscrizione.getAttribute("PercInvalidita") == null? "" : (String)iscrizione.getAttribute("PercInvalidita").toString();
		accertSanitario =iscrizione.getAttribute("accertSanitario") == null? "" : (String)iscrizione.getAttribute("accertSanitario");
		codAccertSanitarioOld =iscrizione.getAttribute("codAccertSanitario") == null? "" : (String)iscrizione.getAttribute("codAccertSanitario");
		datAccertSanitarioOld =iscrizione.getAttribute("datAccertSanitario") == null? "" : (String)iscrizione.getAttribute("datAccertSanitario");
		strNoteOld =iscrizione.getAttribute("strNote") == null? "" : (String)iscrizione.getAttribute("strNote");
		prgSpiModOld =iscrizione.getAttribute("prgSpiMod") == null? "" : iscrizione.getAttribute("prgSpiMod").toString();
		codcmannota =iscrizione.getAttribute("codcmannota") == null? "" : (String)iscrizione.getAttribute("codcmannota");
		
		dataInizio =iscrizione.getAttribute("datdataInizio") == null? "" : (String)iscrizione.getAttribute("datdataInizio");
		dataFine =iscrizione.getAttribute("datdataFine") == null? "" : (String)iscrizione.getAttribute("datdataFine");
		motivoFineAtto =iscrizione.getAttribute("motivoFineAtto") == null? "" : (String)iscrizione.getAttribute("motivoFineAtto");
		codStatoAtto =iscrizione.getAttribute("codStatoAtto") == null? "" : (String)iscrizione.getAttribute("codStatoAtto");
		cdnUtModOld =iscrizione.getAttribute("cdnUtMod") == null? "" : iscrizione.getAttribute("cdnUtMod").toString();
		dtmModOld =iscrizione.getAttribute("dtmMod") == null? "" : (String)iscrizione.getAttribute("dtmMod");
		prgVerbaleAccOld =iscrizione.getAttribute("prgverbaleacc") == null? "" : iscrizione.getAttribute("prgverbaleacc").toString();
		canUpdate = (dataFine.equals(""));
		numPercInvaliditaDiagnosi = (String) serviceRequest.getAttribute("NUMPERCINVALIDITA");
		cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
		cdnLavoratore = (String) serviceRequest.getAttribute("CDNLAVORATORE");		
		
		
		prgCmIscr = iscrizione.getAttribute("prgCmIscr") == null? "" : iscrizione.getAttribute("prgCmIscr").toString();
		prgVerbaleAcc = iscrizione.getAttribute("prgVerbaleAcc") == null? "" : iscrizione.getAttribute("prgVerbaleAcc").toString();
		numKloCmIscr = iscrizione.getAttribute("numKloCmIscr") == null? "" : iscrizione.getAttribute("numKloCmIscr").toString();
		codTipoInvalidita = serviceRequest.getAttribute("codTipoInvalidita") == null? "" : (String) serviceRequest.getAttribute("codTipoInvalidita");
		codAccertSanitario = serviceRequest.getAttribute("codAccertSanitario") == null? "" : (String) serviceRequest.getAttribute("codAccertSanitario");
		
	}
	
	
	String errori = (String) serviceResponse.getAttribute("CM_SAVE_ISCR.ERROR");
	String aggiorna = (String) serviceRequest.getAttribute("AGGIORNA");
	
	boolean beforeUpdate = !"1".equals(aggiorna);
	String readOnly = "1".equals(aggiorna)?"true":"false"; 

	if (numeroIscrizioni == 0) { 		
	%>
	
	<script>
		window.resizeTo(650,180);
	</script>
	
	<af:form name="Frm2" method="POST" action="AdapterHTTP">
	<%= htmlStreamTop %>
	<p class="titolo">Per questo lavoratore non esiste nessuna iscrizione al Collocamento Mirato come disabile</p>	
	<table class="main">
		<tr>		
			<td>
				<input type="button" class="pulsanti" name="chiudi" value="Chiudi" onclick="window.close()" />
			</td>			
		</tr>
	</table>
	<%= htmlStreamBottom %>
	</af:form>
<!-- Iscrizione presente -->
<%	}
	else { %>
	
	
	<script>
		window.resizeTo(760,560);

		function controllaValori() {
			var invaliditaold = document.Frm2.CODTIPOINVALIDITAOLD.value;
			var sanitarioold = document.Frm2.CODACCERTSANITARIOOLD.value;
			var invalidita = document.Frm2.CODTIPOINVALIDITA.value;
			var sanitario = document.Frm2.CODACCERTSANITARIO.value;
			if (invalidita == "" && sanitario == "" &&
				invaliditaold != "" && sanitarioold != "") {
				return confirm("Attenzione!\nNon sono stati valorizzati i campi 'Tipo Invalidità' e 'Tipo accertamento sanitario'.\nProseguire con le modifiche?");
			}
			else if (invalidita == "" &&
					 invaliditaold != "") {
				return confirm("Attenzione!\nNon è stato valorizzato il campo 'Tipo Invalidità'.\nProseguire con le modifiche?");
			}
			else if (sanitario == "" &&
					 sanitarioold != "") {
				return confirm("Attenzione!\nNon è stato valorizzato il campo 'Tipo accertamento sanitario'.\nProseguire con le modifiche?");
			}
			return true;
		}
		
	</script>
	
	
	<%if (canUpdate && beforeUpdate) { 
		htmlStreamTop = StyleUtils.roundTopTable(true);
		htmlStreamBottom = StyleUtils.roundBottomTable(true);
	
	%>
		<p class="titolo">Modificare la seguente iscrizione adeguandola alla situazione descritta nella diagnosi funzionale</p>
	<%} else if (!canUpdate && beforeUpdate){%>
		<p class="titolo">L'ultima iscrizione al collocamento mirato come disabile del lavoratore è chiusa, dunque non è modificabile</p>
	<%} %>
	<af:form name="Frm2" method="POST" action="AdapterHTTP" onSubmit="checkPercentualeInvalidita() && controllaDati()">
		<%= htmlStreamTop %>
		<table class="main">
			<tr>
		    	<td colspan="3"><br/><div class="sezione2">Dati dell'ultima iscrizione</div></td>
			</tr>
			<tr>
				<td class="etichetta">Data ultima iscrizione/reiscrizione</td>
				<td class="campo"  colspan="2">
				<af:textBox classNameBase="input"
						type="date" title="Data ultima iscrizione/reiscrizione"
						name="DATULTIMAISCR" value="<%=datUltimaIscr%>" readonly="true" /></td>				
			</tr>
			
			
			<tr>
			    <td class="etichetta2">Tipo</td>
				<td class="campo2" colspan="2">
					<af:comboBox name="codMonoTipoRagg" title="Tipo" classNameBase="input" disabled="true">					  
						<option value=""  <%if ("".equalsIgnoreCase(codMonoTipoRagg)) {%>SELECTED="true"<%}%>></option>
					    <option value="A" <%if ("A".equalsIgnoreCase(codMonoTipoRagg)) {%>SELECTED="true"<%}%>>Altre categorie protette</option>
					    <option value="D" <%if ("D".equalsIgnoreCase(codMonoTipoRagg)) {%>SELECTED="true"<%}%>>Disabili</option>
				    </af:comboBox>        
				</td>
			</tr>
			<tr>
			    <td class="etichetta">Categoria </td>
			    <td class="campo2" colspan="2">
			    	<af:comboBox name="CODCMTIPOISCR" moduleName="CM_GET_DE_TIPO_ISCR" 
			    	selectedValue="<%=codCmTipoIscr%>" classNameBase="input" title="Categoria iscrizione" 
			    	addBlank="true" disabled="true"/>
			    </td>
			</tr>
			
			<tr>
			    <td class="etichetta">Data anzianità iscrizione</td>
			    <td class="campo" colspan="2" >			    
			    	<af:textBox classNameBase="input" type="date" name="DATANZIANITA68OLD" title="Data anzianità iscrizione" value="<%=datAnzianita68%>" readonly="true"/>			    	
				</td>	
			</tr>
			<tr>
			    <td class="etichetta">Tipo invalidità</td>
			    <td colspan=2 class="campo">
			    <af:textBox classNameBase="input" type="string" title="Tipo invalidità"	name="CODTIPOINVALIDITAORIG" value="<%=tipoInvalidita%>" readonly="true" /></td>	    	
			    </td>
			</tr>
			<tr>
			    <td class="etichetta">Percentuale invalidità</td>
			    <td colspan=2 class="campo">
			    	<af:textBox classNameBase="input" type="integer" name="STRPERCINVALIDITAOLD" title="Percentuale invalidità" value='<%=numPercInvalidita+"%"%>' readonly="true"/>
			    	<input type="hidden" name="NUMPERCINVALIDITAOLD" value="<%=numPercInvalidita%>" />
			    </td>
			</tr>
			<tr>
			    <td class="etichetta">Tipo accertamento sanitario</td>
			    <td colspan=2 class="campo">
			    <af:textBox classNameBase="input" type="string" name="ACCERTSANITARIOLD" title="Tipo accertamento sanitario" value="<%=accertSanitario%>" size="50" readonly="true"/>
			    <input type="hidden" name="CODTIPOINVALIDITAOLD" value="<%=codTipoInvaliditaOld%>" />
			    <input type="hidden" name="CODACCERTSANITARIOOLD" value="<%=codAccertSanitarioOld%>" />
			    <input type="hidden" name="DATACCERTSANITARIOOLD" value="<%=datAccertSanitarioOld%>" />
			    <input type="hidden" name="STRNOTEOLD" value="<%=strNoteOld%>" />
			    <input type="hidden" name="cdnutmodold" value="<%=cdnUtModOld%>" />
				<input type="hidden" name="dtmmodold" value="<%=dtmModOld%>" />
				<input type="hidden" name="prgverbaleaccold" value="<%=prgVerbaleAccOld%>" />
				<input type="hidden" name="codcmannota" value="<%=codcmannota%>" />
			    </td>
			</tr>

<%if (!canUpdate) { %>
			<tr>
				<td colspan="3">
					<div class="sezione2">
				    	<img id='IMG1' src='<%="".equals(dataFine)?imgChiuso:imgAperto%>' onclick='cambia(this, document.getElementById("TBL1"))'/>Chiusura/cancellazione
				  	</div>
			  	</td>
			</tr>
			<tr>
				<td colspan=3>
			    	<table id='TBL1' style='display:<%="".equals(dataFine)?"none":"inline"%>'>  
			    	 <script>initSezioni(new Sezione(document.getElementById('TBL1'),document.getElementById('IMG1'),"false"));</script>
							<tr>
					    		<td class="etichetta">Data cancellazione</td>
					    		<td class="campo" colspan="3">
			                		<af:textBox classNameBase="input" readonly="true" title="Data cancellazione" type="date" name="DATDATAFINE" value="<%=dataFine%>" size="11" maxlength="10"/>
					            </td>
							</tr>
							<tr>
					    		<td class="etichetta">Motivazione cancellazione</td>
					    		<td class="campo" colspan="3"> 
					    			<af:textBox classNameBase="input" readonly="true" title="Motivo cancellazione" type="string" name="MOTIVOFINEATTO" value="<%=motivoFineAtto%>" size="40"/>
			   					</td>
					    	</tr>
					  </table>
				</td>
			</tr>
	<tr>				
		<td colspan=3>
		<input type="button" class="pulsanti" name="chiudi" value="Chiudi" onclick="window.close()" />
		</td>		
	</tr>
					
<%} else if (beforeUpdate){%>
		<tr>
		    	<td colspan="3"><br/><div class="sezione2">Modifica l'iscrizione in</div></td>
		</tr>
		
		<tr>
		    <td class="etichetta">Tipo invalidità</td>
		    <td colspan=2 class="campo">
		    	<af:comboBox name="CODTIPOINVALIDITA" title="Tipo invalidità" moduleName="CM_GET_DE_TIPO_INVAL" classNameBase="input" addBlank="true" required="false" selectedValue="<%=codTipoInvalidita%>"/>
		    </td>
		</tr>
			<tr>
			    <td class="etichetta">Percentuale invalidità</td>
			    <td colspan=2 class="campo">
			    	<af:textBox classNameBase="input" type="integer" name="NUMPERCINVALIDITA" title="Percentuale invalidità" value="<%=numPercInvaliditaDiagnosi%>" required="true" size="4" maxlength="3"/>
			    	
			    </td>
			</tr>
			<tr>
			    <td class="etichetta">Tipo accertamento sanitario</td>
			    <td colspan=2 class="campo">
			    	<af:comboBox name="CODACCERTSANITARIO" moduleName="CM_GET_DE_ACC_SANIT" classNameBase="input" title="Tipo accertamento sanitario" addBlank="true" required="false" selectedValue="<%=codAccertSanitario%>"/>
			    </td>
			</tr>
		
		
			<tr>
				<td class="etichetta">Motivo modifica</td>
				<td colspan=2 class="campo">
				<af:comboBox
						name="CODMONOMOTIVOMODIFICA" title="Motivo modifica"
						classNameBase="input" required="true">
						<option value=""></option>
						<option value="M" selected="true">Modifica situazione</option>
						<option value="A">Aggiornamento dati</option>
						<option value="E">Errore di digitazione</option>
				</af:comboBox>
				</td>				
			</tr>
			<tr>
				<td colspan="3">
					<input type="submit" class="pulsanti" name="modifica" value="Modifica" onClick="return controllaValori();"/> &nbsp;&nbsp;&nbsp;&nbsp; 
					<input type="button" class="pulsanti" name="annulla" value="Annulla" onClick="window.close()"/>
				</td>				
			</tr>		
			<input type="hidden" name="PAGE" value="CMStoricoModIscrL68Page" />
			<input type="hidden" name="CONTEXT" value="DIAGNOSI" />  
			
			<!-- Elenco parametri per l'aggiornamento dati -->
			<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>" />
			<input type="hidden" name="NUMPERCINVALIDITADIAGNOSI" value="<%=numPercInvaliditaDiagnosi%>" />  
			<input type="hidden" name="AGGIORNA" value="1" />
			<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>" />
			<input type="hidden" name="MESSAGE" value="UPDATE_DIAGNOSI" />
			<input type="hidden" name="CODSTATOATTO" value="<%=codStatoAtto%>" />
			<input type="hidden" name="NUMKLOCMISCR" value="<%=numKloCmIscr%>" />
			<input type="hidden" name="PRGCMISCR" value="<%=prgCmIscr%>" />
			<input type="hidden" name="prgVerbaleAcc" value="<%=prgVerbaleAcc%>" />
			<input type="hidden" name="insstorico" value="TRUE" />
			
				
			
			
	<%}
else if (!beforeUpdate) { %>
	<tr>
	<td colspan="3">
	<input type="button" class="pulsanti" name="chiudi" value="Chiudi" onclick="window.close()" />
	</td>	
</tr>
<%}
	%>
	</table>
<%= htmlStreamBottom %>
	
	</af:form>
<% } %>
<%}%>

</body>
</html>
