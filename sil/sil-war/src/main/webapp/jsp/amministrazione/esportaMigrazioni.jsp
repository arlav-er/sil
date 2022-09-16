<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.error.*,
                  it.eng.sil.security.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%!
	private String getAttrStrNotNull(SourceBean sb, String attr) {
		return SourceBeanUtils.getAttrStrNotNull(sb, attr);
	}
%>

<%
	String mainPageName = "EsportaMigrazioniPage";
	String moduleName   = "M_EsportaMigrazioni";
	SourceBean moduleResponse = (SourceBean) serviceResponse.getAttribute(moduleName);

	// Comando appena eseguito dal modulo (immediato passato).
	String comandoP = getAttrStrNotNull(serviceRequest, StepByStepConst.PARAM_COMANDO);
	
	// Comando che si dovrà eseguire nell'immediato futuro.
	String comandoF = getAttrStrNotNull(moduleResponse, StepByStepConst.PARAM_COMANDO);

	boolean thereWasAnError = ! responseContainer.getErrorHandler().isOK();
	boolean lastPageEsporta = ((String)serviceRequest.getAttribute("PAGE")).equals(mainPageName);

	boolean isNeverDoneMigr = SourceBeanUtils.getAttrBoolean(moduleResponse, "isNeverDoneMigr", false);

	PageAttribs attributi   = new PageAttribs(user, mainPageName);
	boolean canEnter        = true;
	boolean canDoMigra      = attributi.containsButton("MIGRAZIONE_NUOVA");
	boolean canDoSetInvio   = attributi.containsButton("MIGRAZIONE_SETINVIO");
	boolean canDoStorico    = attributi.containsButton("MIGRAZIONE_STORICO");
	boolean canDoInvioMail  = attributi.containsButton("MIGRAZIONE_INVIOMAIL");
	boolean canDoAnnulla    = attributi.containsButton("MIGRAZIONE_ANNULLAERRATA");

	String htmlStreamTop    = StyleUtils.roundTopTable(true);
	String htmlStreamBottom = StyleUtils.roundBottomTable(true);
%>
<html>
<head>
<title>Esportazione Migrazioni</title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<style type="text/css">
	td.emCantRun  { color:#FF0000; font-size:14px; font-weight:bold; }
	div.attendere { color:#29A360; font-size:12px; font-weight:bold; font-style:italic; }
</style>

<af:linkScript path="../../js/"/>
<script language="JavaScript" src="../../js/lookup.js"></script>

<script language="Javascript">
<!--

	function infoBut() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
		
		document.formina.<%= StepByStepConst.PARAM_COMANDO %>.value = "<%= StepByStepConst.COMANDO_INFO %>";
		doFormSubmit(document.formina);
	}


	function inviaMailBut(){
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
	
		document.formina.PAGE.value = "MailLoadDefaultPage";
		doFormSubmit(document.formina);
	}


	function migraBut() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
		if (!controllaFunzTL()) return;
		if (! confirm("Attenzione: l'esportazione potrebbe durare anche parecchi minuti\n" +
		              "e NON dovrà essere interrotta per nessun motivo!\n\n" +
		              "Premere [OK] per cominciare l'esportazione.")){ 
		   undoSubmit();           
		   return;
		}

		attendereMostra();		
		doFormSubmit(document.formina);
	}


	function setinvioBut() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

		document.formina.PAGE.value = "EsportaMigrSetInvioPage";
		document.formina.<%= StepByStepConst.PARAM_COMANDO %>.value = "<%= StepByStepConst.COMANDO_INFO %>";
		doFormSubmit(document.formina);
	}


	function annullaBut() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

		if (! confirm("Attenzione: questa funzionalità permette di ripristinare la storia " +
					"delle elaborazioni eliminando l'ultima estrazione fatta ma erroneamente " +
					"interrotta prima del suo completamento.\n" +
					"E' possibile eliminarla solo se si trova in stato di 'in corso' ed è " +
					"trascorso un certo periodo di tempo dal suo avvio.\n\n" +
					"Confermare solo se si è veramente sicuri.")) return;
		
		document.formina.PAGE.value = "EsportaMigrAnnullaErrataPage";
		document.formina.<%= StepByStepConst.PARAM_COMANDO %>.value = "<%= StepByStepConst.COMANDO_INFO %>";
		doFormSubmit(document.formina);
	}


	function storicoBut() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
		
		apriLookup("EsportaMigrVisStoricoPage", "");
	}


	function attendereMostra() {
		var attendere = document.getElementById("attendere");
		if ((attendere != null) && (attendere != undefined)) {
			attendere.style.display  = ""; //visibile
		}
	}
	
	function onLoad() {
		<%
		if (canEnter) {
			if (StringUtils.isEmpty(comandoP) ||
				comandoF.equalsIgnoreCase(StepByStepConst.COMANDO_SUCC)) {
					%>
					doFormSubmit(document.formina);
					<%
			}
		}
		%>
	}
	// gestione sezione dinamica
	function cambia(immagine, sezione) {
		if (sezione.style.display=="none") {
			sezione.style.display="";
			immagine.src="../../img/aperto.gif";
		}
		else {
			sezione.style.display="none";
			immagine.src="../../img/chiuso.gif";
		}
	}
		
//-->
</script>

</head>

<body class="gestione" onLoad="onLoad()">
<br/>
<p class="titolo">Esportazione Migrazioni</p>

<af:showErrors/>
<af:showMessages prefix="<%= moduleName %>"/>
<af:showMessages prefix="M_EsportaMigrSetInvio"/>
<af:showMessages prefix="M_EsportaMigrAnnullaErrata"/>
<%-- Savino 30/01/2006  dontValidate="false" onSubmit="false" perche' se si imposta la data fine migrazione
e si da invio la form parte senza che siano fatti i controlli js, attivati incece cliccando sui pulsanti (input button).
In questo modo se ci si posiziona nel campo data e si pigia sul tasto enter non accadra' nulla
--%>
<af:form name="formina" method="POST" action="AdapterHTTP" dontValidate="false" onSubmit="false">
<input type="hidden" name="PAGE" value="<%= mainPageName %>" />
<input type="hidden" name="cdnFunzione" value="<%=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione")%>" />
<input type="hidden" name="<%= StepByStepConst.PARAM_COMANDO %>" value="<%= comandoF %>" />

<%= htmlStreamTop %>
<table class="main">
<tr><td valign="top" height="300">

<table class="main">
	<tr>
		<td class="etichetta2" width="55%">&nbsp;</td>
		<td class="campo_read" width="45%">&nbsp;</td>
	</tr>

<%
/*
 * La pagina JSP deve leggere:
 *  - comandoPrecedente (letto da request)
 *  - comandoFuturo (letto da response del modulo)
 * e decidere in che stato ci troviamo: 1, 2, 3a o 3b.
 */
/* --------------------------------------------------------------------------------
 * 0- controllo permessi di accesso alla pagina
 * --------------------------------------------------------------------------------
 */
if (! canEnter) {
	%>
	<tr>
		<td colspan="2" class="emCantRun">
			Non si dispone dei privilegi per eseguire l'esportazione.
		</td>
	</tr>
	<%
}
/* --------------------------------------------------------------------------------
 * 1- se comandoPrecedente è nullo/vuoto: 
 *      + mostro pagina di attesa inizializzazione (o blocca se non ha i permessi)
 * --------------------------------------------------------------------------------
 */
else if (StringUtils.isEmpty(comandoP)) {
	%>
	<tr>
		<td colspan="2">
			<p class="titolo">Inizializzazione in corso...</p>
		</td>
	</tr>
	<%
}
/* --------------------------------------------------------------------------------
 *  2- altrimenti se comandoPrecedente è "INFO":
 *      + mostro info vecchie con tasto di inizio nuova
 * --------------------------------------------------------------------------------
 */
else if (comandoP.equalsIgnoreCase(StepByStepConst.COMANDO_INFO)) {
	%>
	<tr>
		<td colspan="2">
			<p class="titolo">Informazioni sull'Ultima Esportazione Fatta:</p>
		</td>
	</tr>
	<tr><td colspan="2">&nbsp;</td></tr>
	<tr>
		<td class="etichetta2">Provincia SIL</td>
		<td class="campo_read"><%= getAttrStrNotNull(moduleResponse, "descProvinciaSil") %></td>
	</tr>
	<tr>
		<td class="etichetta2">Data e ora di lancio</td>
		<td class="campo_read"><%= getAttrStrNotNull(moduleResponse, "dataLancio") %></td>
	</tr>
	<tr>
		<td class="etichetta2">Data e ora ultima migrazione</td>
		<td class="campo_read"><%= getAttrStrNotNull(moduleResponse, "dataUltimaMigrazione") %></td>
	</tr>
	<tr>
		<td class="etichetta2">Tempo impiegato (ore:min:sec)</td>
		<td class="campo_read"><%= getAttrStrNotNull(moduleResponse, "strPrecNumSecElaborazione") %></td>
	</tr>
	<tr>
		<td class="etichetta2">Effettuata da</td>
		<td class="campo_read"><%= getAttrStrNotNull(moduleResponse, "precFattaDaUtente") %></td>
	</tr>
	<tr>
		<td class="etichetta2">Numero di CPI</td>
		<td class="campo_read"><%= getAttrStrNotNull(moduleResponse, "precNumCpiElab") %></td>
	</tr>
	<tr>
		<td class="etichetta2">Numero di Movimenti</td>
		<td class="campo_read"><%= getAttrStrNotNull(moduleResponse, "precNumMovimentiElab") %></td>
	</tr>

	<tr>
		<td class="etichetta2">Esportazione in corso</td>
		<td class="campo_read">
			<%
			boolean flgInCorso = SourceBeanUtils.getAttrBoolean(moduleResponse, "flgInCorso", false);
			%>
			<%= (flgInCorso ? "S&igrave;" : "No") %>
		</td>
	</tr>
	
	<tr>
		<td class="etichetta2">Inviato</td>
		<td class="campo_read">
			<%
			boolean flgInvio = SourceBeanUtils.getAttrBoolean(moduleResponse, "flgInvio", false);
			if (isNeverDoneMigr || !canDoSetInvio) {
				%><%= (flgInvio?"S&igrave;":"No") %><%
			}
			else {
				%>
				<select name="flgInvio">
					<option value="N" <%= (flgInvio?"":"selected='true'") %> >No</option>
					<option value="S" <%= (flgInvio?"selected='true'":"") %> >S&igrave;</option>
				</select>
				<%
			}
			%>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<table class='sezione' cellspacing=0 cellpadding=0>
				<tr>
					<td  width="18">
	    	           	<img id='I_SEZ_ANTICIPA_DF' src='../../img/chiuso.gif' onclick='cambia(this, document.getElementById("T_SEZ_ANTICIPA_DF"))'>
	    	        </td>
	    	        <td class="sezione_titolo">Nuova migrazione: anticipa data fine</td>
    	        </tr>
			</table>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<table id='T_SEZ_ANTICIPA_DF' style='width:100%;display:none'>   
				<tr>
					<td class="etichetta2" width="55%">Data fine ultima migrazione</td>
					<td class="campo" width="45%" style="padding-left: 12px;">
					<%-- Se la migrazione non e' permessa allora il campo deve essere in sola lettura --%>
						<af:textBox type="date" name="dataFineUltimaMigrazione" value="" 
							validateOnPost="true" readonly="<%=String.valueOf(!canDoMigra)%>"  
							classNameBase="input" size="11" maxlength="10"/>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr><td colspan="2">&nbsp;</td></tr>
	<%
	if (thereWasAnError && lastPageEsporta) {
		%>
		<tr>
			<td colspan="2" class="campocentrato">
				Si &egrave; verificato un errore: non &egrave; possibile esportare.
			</td>
		</tr>
		<%
	}
	else {
		if (canDoMigra) {
		%>
			<tr>
				<td colspan="2" class="campocentrato">
					<b>Attenzione:</b> l'esportazione potrebbe richiedere anche parecchi minuti per poter essere completata.<br/>
					Si prega di <b>non interrompere</b> l'esportazione una volta cominciata!
				</td>
			</tr>
		<%
		} //if canDoMigra
		%>
		<tr><td colspan="2">&nbsp;</td></tr>
		<tr>
			<td colspan="2">
				<span class="bottoni">
				<%
				if (canDoMigra) {
					%>
					<input type="button" class="pulsante" name="migra"
					       value="Nuova Migrazione" onClick="migraBut()" />&nbsp;
					<%
				}
				%>				
				</span>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<span class="bottoni">
				<%				
					if (canDoSetInvio) {
						%>
						<input type="button" class="pulsante" name="setinvio"
						       <%= isNeverDoneMigr?"disabled=\"true\"":"" %>
						       value="Imposta Inviato" onClick="setinvioBut()" />&nbsp;
						<%
					}
					if (canDoStorico) {
						%>
						<input type="button" class="pulsante" name="storico"  
						       value="Visualizza Storico" onClick="storicoBut()" />&nbsp;
						<%
					}			
					if (canDoInvioMail) {
						%>
						<input type="button" class="pulsante" name="Invia_exp_x_mail"
							   <%= isNeverDoneMigr?"disabled=\"true\"":"" %>
						       value="Invia Migrazioni per E-Mail" onClick="inviaMailBut()" />&nbsp;
						<%
					}
					if (canDoAnnulla) {
						%>
						<input type="button" class="pulsante" name="annulla"
							   <%= isNeverDoneMigr?"disabled=\"true\"":"" %>
						       value="Ripristina" onClick="annullaBut()" />&nbsp;
						<%
					}
				%>
				</span>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				
			</td>
		</tr>
		<tr>
			<td colspan="2" class="campocentrato">
				<div id="attendere" class="attendere" style="display:none">
					Inizializzazione in corso, attendere prego...
				</div>
			</td>
		</tr>
		<%
	}
}
/* --------------------------------------------------------------------------------
 *  3- altrimenti (comandoPrecedente è "INIZIO" o "SUCC") guardo il comandoFuturo
 *      3a- se comandoFuturo è "SUCC":
 *            + mostra info elaborazione e barra di progresso
 *      3b- altrimenti (comandoFuturo è "INFO"):
 *            + mostra totali di fine operazione.
 * --------------------------------------------------------------------------------
 */
else {
	%>
	<tr>
		<td colspan="2" class="campocentrato">
			<%= StepByStepUtils.getProgressBar(moduleResponse) %>
		</td>
	</tr>
	<tr><td colspan="2">&nbsp;</td></tr>
	<%
	// Non distinguo tra "in corso" e "finita" poiché stampo
	// sempre i dati di elaborazione (eventualmente parziali).
	// NOTA: LA "SUBMIT" VIENE FATTA IN AUTOMATICO SULL'onLoad QUANDO SERVE.
	String messElab;	// messaggio da mostrare
	if (comandoF.equalsIgnoreCase(StepByStepConst.COMANDO_SUCC)) {
		messElab = "Attendere, prego...";
	}
	// Comando Futuro = STOP: FINE ITERAZIONE --------------------------------------------------
	else {   // ho per forza: comandoF uguale COMANDO_INFO
		if (thereWasAnError) {
			messElab = "Esportazione interrotta!";
		}
		else {
			messElab = "Esportazione completata!";
		}
	} // if comandoF
	%>

	<tr>
		<td colspan="2">
			<p class="titolo"><%= messElab %></p>
		</td>
	</tr>
	<tr><td colspan="2">&nbsp;</td></tr>
	<%
	String infoCpiSucc = getAttrStrNotNull(moduleResponse, "infoCpiSucc");
	boolean ce = StringUtils.isFilled(infoCpiSucc);
	%>
	<tr>
		<td class="etichetta2"><%= ce ? "codice CPI in esportazione:" : "&nbsp;" %></td>
		<td class="campo_read"><%= ce ? infoCpiSucc : "&nbsp;" %></td>
	</tr>
	<tr>
		<td class="etichetta2">Data e ora di lancio</td>
		<td class="campo_read"><%= getAttrStrNotNull(moduleResponse, "dataQuestoLancio") %></td>
	</tr>
	<tr>
		<td class="etichetta2">Data e ora migrazione</td>
		<td class="campo_read"><%= getAttrStrNotNull(moduleResponse, "dataQuestaMigrazione") %></td>
	</tr>
	<tr>
		<td class="etichetta2">Tempo impiegato (ore:min:sec)</td>
		<td class="campo_read"><%= getAttrStrNotNull(moduleResponse, "strNumSecElaborazione") %></td>
	</tr>
	<tr>
		<td class="etichetta2">Numero di CPI</td>
		<td class="campo_read"><%= getAttrStrNotNull(moduleResponse, "numCpiElab") %></td>
	</tr>
	<tr>
		<td class="etichetta2">Numero di Movimenti</td>
		<td class="campo_read"><%= getAttrStrNotNull(moduleResponse, "numMovimentiElab") %></td>
	</tr>
	<%	
	if (comandoF.equalsIgnoreCase(StepByStepConst.COMANDO_INFO)) {
		%>
		<tr><td colspan="2">&nbsp;</td></tr>
		<tr>
			<td colspan="2">
				<span class="bottoni">
					<input type="button" class="pulsante" name="ok"
					       value="  OK  " onClick="infoBut()" />
				</span>
			</td>
		</tr>
		<%
	}

} // else-if comandoP
%>

</table>
</td></tr>
</table>
<%= htmlStreamBottom %>

</af:form>

</body>
</html>
