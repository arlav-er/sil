<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
			it.eng.sil.security.*,
			it.eng.afExt.utils.*,
			it.eng.sil.util.*,
			java.math.*,
			java.util.*"
	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%
	String  titolo = "Inserimento dichiarazione/attestazione";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	// Lettura parametri dalla REQUEST
	int     cdnFunzione      = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	String  cdnfunzioneStr   = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");
	String  cdnLavoratore    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
	String prgTipoModello ="", prgModelloStampa="", moduloTipoModello="", tipoDoc="", dataInizio="", 
		dataFine="", note="", prgStDichAtt=""; 	
	Vector modelliDaVisualizzare = serviceRequest.getAttributeAsVector("ModelliDaVisualizzare");
	boolean apriStampa = false;
	if (serviceRequest.containsAttribute("inserisci") ) {
		apriStampa = ResponseContainerAccess.getResponseContainer(request).getErrorHandler().getErrors().size()==0;
	}
	if (serviceRequest.containsAttribute("inserisci")) {
		prgTipoModello = (String)serviceRequest.getAttribute("prgTipoModello");
		prgModelloStampa = (String)serviceRequest.getAttribute("prgModelloStampa");
		note = (String) serviceRequest.getAttribute("strNoteSpecifiche");
		dataInizio = (String) serviceRequest.getAttribute("datInizio");
		dataFine = Utils.notNull(serviceRequest.getAttribute("datFine"));
		tipoDoc = (String)serviceResponse.getAttribute("GetDeRelMod.rows.row.codTipoDocumento");
		prgStDichAtt = Utils.notNull(serviceResponse.getAttribute("STAMPADICHATTNEXTVAL.rows.row.nextVal"));
		moduloTipoModello = "ComboStModelloStampa";
	}
	String queryString=null;
	String urlDiLista = (String)sessionContainer.getAttribute("_TOKEN_LISTADICHIARAZIONIPAGE");
	queryString = urlDiLista;
	//
    InfCorrentiLav testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
    testata.setPaginaLista("ListaDichiarazioniPage");

    //
   	String htmlStreamTop    = StyleUtils.roundTopTable(true);
	String htmlStreamBottom = StyleUtils.roundBottomTable(true);
%>

<html>
<head>
<title><%= titolo %></title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<af:linkScript path="../../js/"/>
<%@ include file="../documenti/_apriGestioneDoc.inc"%>
<script language="javascript">
	var flagChanged = false;  
	function fieldChanged() {
		flagChanged = true;
  	}
	function nuovo() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
	
		var url = "AdapterHTTP?PAGE=InsDichiarazionePage" +		
		          		"&cdnLavoratore=<%=cdnLavoratore%>" + 
						"&cdnfunzione=<%=cdnFunzione%>";
		setWindowLocation(url);
	}

	function popolaComboModello(codiceModello) {
		if (codiceModello=='') {
			while (document.Frm1.prgModelloStampa.options.length>0) {
		        document.Frm1.prgModelloStampa.options[0]=null;
		    }
			return;
		}
		request = "AdapterHTTP?PAGE=GetModelloStampaPage&prgTipoModello=" + codiceModello;
	
		//Controllo sul server
		var result = syncXMLHTTPGETRequest(request);
		if (result == null || result.responseXML.documentElement == null ) {
			alert("Modelli non trovati \n");
		} else {
			xmlresult = result.responseXML.documentElement; 
			if (xmlresult==null) {
				alert ("Impossibile leggere i dati dal server");
				return;
			}
			datiElements=xmlresult.getElementsByTagName("COMBOSTMODELLOSTAMPA");
			rows=datiElements[0].getElementsByTagName("ROW");
			while (document.Frm1.prgModelloStampa.options.length>0)
				document.Frm1.prgModelloStampa.options[0]=null;			
			document.Frm1.prgModelloStampa.options[0]=new Option("", "", true);			
			for (i=0;i<rows.length;i++) {
				row = rows[i];
				codice = row.getAttribute("CODICE");
				descrizione = row.getAttribute("DESCRIZIONE")
//				alert(row.getAttribute("CODICE") + " - " + descrizione);
				document.Frm1.prgModelloStampa.options[i+1]=new Option(descrizione, codice);				
			}
		}		
	}
	function annulla() {
		document.Frm1.prgTipoModello.value='';
		while (document.Frm1.prgModelloStampa.options.length>0)
			document.Frm1.prgModelloStampa.options[0]=null;	
		document.Frm1.datInizio.value='';
		document.Frm1.datFine.value='';
		document.Frm1.strNoteSpecifiche.value='';
		
	}
			
	function apriStampa() {
		tipoDoc = '<%=tipoDoc%>';
		params = '&cdnLavoratore=<%=cdnLavoratore%>&pagina=ListaDichiarazioniPage&prgStDichAtt=<%=prgStDichAtt%>';
		
		apriGestioneDoc('RPT_MOD_DICH_ATT',params,tipoDoc);
	}
</script>
<script type="text/javascript" src="../../js/CommonXMLHTTPRequest.js" language="JavaScript"></script>

</head>

<body class="gestione" onload="<%=apriStampa?"apriStampa()":""%>">

<%testata.show(out);%>
<af:showErrors />



<af:form name="Frm1" dontValidate="false" method="POST"  action="AdapterHTTP">
	<input type="hidden" name="PAGE" value="InsDichiarazionePage"/>
	<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>"/>
	<input type="hidden" name="cdnFunzione" value="<%=cdnFunzione%>"/>
	<%for (int i=0;i<modelliDaVisualizzare.size();i++) {%>
		<input type="hidden" name="modelliDaVisualizzare" value="<%=modelliDaVisualizzare.get(i)%>"/>
	<%}%>
<p class="titolo"><%= titolo %></p>



<%= htmlStreamTop %>
<table class="main">
	<tr>
		<td class="etichetta">Tipo modello</td>
		<td class="campo">
				<af:comboBox name="prgTipoModello"
						title="Tipo Modello"
						moduleName="ComboTipoModello" 
						selectedValue="<%=prgTipoModello%>"
						addBlank="true"
						required="true"
						classNameBase="input"						
						onChange="fieldChanged();popolaComboModello(this.value)" />
		</td>
	</tr>
	<tr>
		<td class="etichetta">Modello</td>
		<td class="campo">
			<af:comboBox name="prgModelloStampa"
						title="Modello"
						moduleName="<%=moduloTipoModello%>"
						selectedValue="<%=prgModelloStampa%>"
						addBlank="true"
						required="true"
						classNameBase="input"/>
		</td>
	</tr>
	<tr>
		<td class="etichetta">Data inizio</td>
		<td class="campo">
			<af:textBox name="datInizio" type="date"
						title="Data inizio"
						validateOnPost="true"
						size="11" maxlength="10"
						required="true"
						value="<%=dataInizio%>"
						classNameBase="input" />
		</td>
	</tr>
	<tr>
		<td class="etichetta">Data fine</td>
		<td class="campo">
			<af:textBox name="datFine" type="date"
						title="Data fine"
						validateOnPost="true"
						size="11" maxlength="10"
						required="false"
						value="<%=dataFine%>"
						classNameBase="input" />
		</td>
	</tr>
	<tr>
		<td class="etichetta">Note specifiche</td>
		<td class="campo">
			<af:textArea name="strNoteSpecifiche" classNameBase="textarea" maxlength="3000" value="<%=note%>"/>
		</td>
	</tr>
	
	<tr>
		<td colspan=2>
			<input type="submit" class="pulsante" name="inserisci" value="Stampa"/>&nbsp;&nbsp;
			<input type="button" class="pulsante"  value="Annulla" onclick="annulla()"/>
		</td>
	</tr>
	<tr><td>&nbsp;</td></tr>

	
</table>
<%= htmlStreamBottom %>
</af:form>
</body>
</html>
