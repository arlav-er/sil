<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.error.*,
                  it.eng.sil.security.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*,
                  java.util.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	SourceBean moduleResponse = null;
	// jsFunct... = Nomi delle funzioni JS chiamabili nella pagina "opener".
	// (sono impostate dalla pagina JSP chiamante)
	String jsFunctForXxx = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "jsFunctForXxx");
	String jsFunctForCom = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "jsFunctForCom");

	// Spazzolo le risposte di tutti i moduli eseguiti,
	// in cerca del primo che ha nome che comincia con "M_SelezComuniIn" (del tipo "M_SelezComuniInXxx")
	Vector modules = serviceResponse.getContainedSourceBeanAttributes();
	for (int m=0; m<modules.size(); m++) {
		SourceBean module = (SourceBean) ((SourceBeanAttribute)modules.elementAt(m)).getValue();
		String name = module.getName();		// nome del modulo

		if (name.startsWith("M_SelezComuniIn")) {

			moduleResponse = module;
			break;	// Stop ricerca
		}
	}
	if (moduleResponse == null) moduleResponse = new SourceBean("dummy");
	
	String htmlStreamTop    = StyleUtils.roundTopTable(true);
	String htmlStreamBottom = StyleUtils.roundBottomTable(true);
%>
<html>
<head>
<title>Selezione comuni</title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">

<af:linkScript path="../../js/"/>

<script language="Javascript">

	function checkXxxOnClick() {
		// Se seleziono la radice, seleziono anche tutti i comuni
		// Se tolgo la selezione alla radice, la tolgo anche a tutti i comuni
		// Quindi, lo stato della radice influenza in modo analogo lo stato di tutti i comuni.
		checkComDefineAll(document.form.checkXxx.checked);
	}


	function checkComOnClick(checkClicked) {
		
		// Se tolgo la selezione a un comune, la tolgo anche all'elemento radice
		if (! checkClicked.checked) {
			document.form.checkXxx.checked = false;
		}

		// Se ora sono selezionati tutti i comuni, si seleziona anche la radice
		if (tuttiCheckSelezionati(document.form.checkCom)) {
			document.form.checkXxx.checked = true;
		}
	}

	function checkSelectAllOnClick() {
		// A seconda dello stato del "checkSelectAll" imposta
		// in modo uguale tutti i checkbox dei comuni e anche quello radice.
		var checked = document.form.checkSelectAll.checked;
		checkComDefineAll(checked);
		document.form.checkXxx.checked = checked;
	}

	// Imposta a checked S/N tutti i checkbox dei comuni
	function checkComDefineAll(checked) {
	
		var checkCom = document.form.checkCom;
		if (checkCom.length == undefined) {
			checkCom.checked = checked;
		} else {
			for (var i=0; i < checkCom.length; i++) {
				checkCom[i].checked = checked;
			}
		}
	}


	/*
	 * Rende true se almeno uno dei checkbox passati è selezionato
	 * Es.:  almenoUno = almenoUnCheckSelezionato(document.form.checkCom)
	 */
	function almenoUnCheckSelezionato(arrCheck) {
		var almenoUnoSel = false;
		if (arrCheck != undefined) {
			if (arrCheck.length == undefined) {
				almenoUnoSel = arrCheck.checked;
			} else {
				var i = 0;
				while ((i < arrCheck.length) && !almenoUnoSel){
					almenoUnoSel = arrCheck[i].checked;
					i++;
				}
			}
		}
		return almenoUnoSel;
	}


	/*
	 * Rende true se tutti i checkbox passati sono selezionati
	 * Es.:  almenoUno = tuttiCheckSelezionati(document.form.checkCom)
	 */
	function tuttiCheckSelezionati(arrCheck) {
		var tuttiSel = false;
		if (arrCheck != undefined) {
			if (arrCheck.length == undefined) {
				tuttiSel = arrCheck.checked;
			} else {
				var i = 0;
				tuttiSel = true;
				while ((i < arrCheck.length) && tuttiSel){
					tuttiSel = tuttiSel && arrCheck[i].checked;
					i++;
				}
			}
		}
		return tuttiSel;
	}
	


	// Chiamata sul bottone di "inserisci".
	function inserisciOnClick() {

		var checkXxx = document.form.checkXxx;
		var checkCom = document.form.checkCom;	
		<%--
			NOTA: Se jsFunctForXxx è definita, posso eventualmente salvare l'elemento radice
			come entità a sè stante; altrimenti potrò solo salvare i singoli comuni scelti.
		--%>
		<% if (StringUtils.isFilled(jsFunctForXxx)) { %>
			<%--
				Se il chekbox di root è selezionato
				oppure se sono stati selezionati tutti i comuni,
				eseguo la funzione per salvare la selezione della sola entità di radice
				(che è definita dalla variabile Java "jsFunctForXxx").
			--%>
			if (checkXxx.checked || tuttiCheckSelezionati(checkCom)) {
				<%-- CHIAMO LA FUNZIONE JAVASCRIPT (per l'entità di radice) NELLA FINESTRA GENITORE --%>
				window.opener.<%= jsFunctForXxx %>(checkXxx.value);
				window.close();
				return;
			}
			
		<% } %>
		
		<%--
			In ogni altro caso, recupero tutti i codici dei checkbox dei comuni selezionati,
			li uso per costruire una "separated stringa" e invoco la funzione JavaScript che
			provvederà a salvare uno a uno i singoli comuni scelti.
		--%>
		var rootSelez = checkXxx.checked;  // Se ho selez. la radice, recupero tutti i comuni
		if (rootSelez || almenoUnCheckSelezionato(checkCom)) {
		
			<%-- Costruisco stringa con i codici dei comuni separati da opportuno separatore. --%>
			var codComSepStr = "";
			if (checkCom.length == undefined) {
				if (checkCom.checked || rootSelez) {
					codComSepStr = checkCom.value;
				}
			} else {
				for (var i=0; i < checkCom.length; i++) {
					// inserisco nella stringa solo i comuni selezionati
					if (checkCom[i].checked || rootSelez) {
						
						if (codComSepStr.length == 0)
							codComSepStr = checkCom[i].value;
						else
							codComSepStr += "<%= CodSepStrUtils.SEPARATOR %>" + checkCom[i].value;
					}
				}
			}
		
			<%-- CHIAMO LA FUNZIONE JAVASCRIPT (per i comuni) NELLA FINESTRA GENITORE --%>
			window.opener.<%= jsFunctForCom %>(codComSepStr);
			window.close();
			return;
		}
		
		<%-- Se sono qui, non ho potuto fare alcuna azione di salvataggio. --%>
		alert("Nessun comune selezionato.");
		return;
	}
	
</script>

</head>

<body>

<af:showErrors/>
<af:showMessages prefix="<%= moduleResponse.getName() %>"/>

<br/>

<af:form name="form" method="POST" action="AdapterHTTP" dontValidate="true">
<input type="hidden" name="PAGE" value="(viaJavaScript)" />
<input type="hidden" name="cdnFunzione" value="<%=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione")%>" />


<%= htmlStreamTop %>
<table class="main">
	<tr>
		<td colspan="2">

			<%
			// Recupero tutte le righe della lista dei comuni
			Vector rows = moduleResponse.getAttributeAsVector("ROWS.ROW");
			if (rows != null && !rows.isEmpty()) {
				%>
				<table cellpadding="0" cellspacing="0">
				<%
				boolean toShowXxx = true;
				Iterator iter = rows.iterator();
				while (iter.hasNext()) {
					SourceBean row = (SourceBean) iter.next();
					
					if (toShowXxx) {
						%>
						<tr>
							<td colspan="2" class="azzurro_bianco">
								<input type="checkbox" name="checkSelectAll"
										onClick="checkSelectAllOnClick()" />
								Seleziona/deseleziona tutti i comuni&nbsp;&nbsp;
							</td>
						</tr>
						<tr>
							<td colspan="2">&nbsp;</td>
						</tr>
						
						<% if (rows.size() > 16) { %>
						<tr>
							<td colspan="2">
								<a href="#fondo"><img src="../../img/giu.gif" align="right" alt="vai in fondo" /></a>
							</td>
						</tr>
						<% } %>
						
						<tr>
							<td colspan="2" nowrap="nowrap">
								<%
								String codXxx  = SourceBeanUtils.getAttrStrNotNull(row, "COD_XXX");
								String descXxx = SourceBeanUtils.getAttrStrNotNull(row, "DESC_XXX");
								%>
								<input type="checkbox" name="checkXxx"
										checked="checked"
										value="<%= codXxx %>"
										onClick="checkXxxOnClick()" />
								<%= descXxx %>
							</td>
						</tr>
						<%
						toShowXxx = false;
					}
					%>
					<tr>
						<td align="right" width="13%"><img src="../../img/lines/line2.gif" border="0" /><img src="../../img/lines/line4.gif" border="0" /></td>
						<td nowrap="nowrap">
							<%
							String codCom  = SourceBeanUtils.getAttrStrNotNull(row, "COD_COMUNE");
							String descCom = SourceBeanUtils.getAttrStrNotNull(row, "DESC_COMUNE");
							%>
							<input type="checkbox" name="checkCom"
								checked="checked"
								value="<%= codCom %>"
								onClick="checkComOnClick(this)" />
						<%= descCom %>
						</td>
					</tr>
					<%
				}
				%>
				</table>
				<%
			}
			%>
		</td>
	</tr>


	<tr><td colspan="2">&nbsp;</td></tr>
	<tr>
		<td colspan="2">
			<a name="fondo"></a>
			<span class="bottoni">
				<input type="button" class="pulsante" name="indietro"
				       value=" &lt;&lt; Indietro " onClick="window.close();" />
				&nbsp;&nbsp;
				<input type="button" class="pulsante" name="inserisci"
				       value="  Inserisci  " onClick="inserisciOnClick();" />
			</span>
		</td>
	</tr>
	<tr><td colspan="2">&nbsp;</td></tr>

</table>
<%= htmlStreamBottom %>

</af:form>

</body>
</html>
