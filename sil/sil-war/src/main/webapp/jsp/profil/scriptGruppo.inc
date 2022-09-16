//Rilevazione Modifiche da parte dell'utente
var flagChanged = false;

var vTipoGruppoPS = new Array();
var vTipoGruppoID = new Array();
var vTipoGruppoSoggetti = new Array();
var vTipoGruppoIDSoggetti = new Array();
        
function fieldChanged() {
	<% if (!readOnlyStr){ %> 
       	flagChanged = true;
    <%}%> 
}
  	
// va abilitata SOLO per CPI
function abilitaEmail() {
   	var e = document.Frm1.CDNTIPOGRUPPO;
	var tipoGruppo= e.value;
	var emailtextbox = document.Frm1.STREMAILPUBBL;
	var emailLab = document.getElementsByName("emailFields")[0];
	if (tipoGruppo == 1) {
		emailLab.style.display = '';
	} else {
		emailtextbox.value = "";
		emailLab.style.display = 'none';
	}
}
	
function abilitaCodRif() {
	var index = document.Frm1.CDNTIPOGRUPPO.selectedIndex;		
	var selectedID = document.Frm1.CDNTIPOGRUPPO.options[index].value;
	if (selezionatoTipoGruppoPatronatoSogg(selectedID)) {
		document.Frm1.STRCODRIF.disabled = false;
	} else {
		document.Frm1.STRCODRIF.value = "";
		document.Frm1.STRCODRIF.disabled = true;
	}
}
	
function caricaDati() {
	<%
	int contatoreRiga = 0;
    List listaCdnTipoGruppoPatronatoSogg = new ArrayList();
    Vector listaTipoGruppoPatronato = serviceResponse.getAttributeAsVector("M_ListaTipoGruppoPatronato.ROWS.ROW");
    for (int j = 0; j < listaTipoGruppoPatronato.size(); j++) {
		SourceBean rowTipidoc = (SourceBean)listaTipoGruppoPatronato.get(j);

		String denominazione = (String)rowTipidoc.getAttribute("STRDENOMINAZIONE");
		denominazione = StringUtils.formatValue4Javascript(denominazione);
		listaCdnTipoGruppoPatronatoSogg.add((BigDecimal)rowTipidoc.getAttribute("CDNTIPOGRUPPO"));
		%>				
		vTipoGruppoPS[<%=contatoreRiga%>] = '<%=denominazione%>' ;
		vTipoGruppoID[<%=contatoreRiga%>] = <%=(BigDecimal)rowTipidoc.getAttribute("CDNTIPOGRUPPO")%> ;
		<%
		contatoreRiga = contatoreRiga + 1;
	}
    Vector listaTipoGruppoSoggAcc = serviceResponse.getAttributeAsVector("M_ListaTipoGruppoSogAccreditati.ROWS.ROW");
    for (int j = 0; j < listaTipoGruppoSoggAcc.size(); j++) {
		SourceBean rowTipidoc = (SourceBean)listaTipoGruppoSoggAcc.get(j);
			
		String denominazione = (String)rowTipidoc.getAttribute("STRDENOMINAZIONE");
		denominazione = StringUtils.formatValue4Javascript(denominazione);
		listaCdnTipoGruppoPatronatoSogg.add((BigDecimal)rowTipidoc.getAttribute("CDNTIPOGRUPPO"));
		%>				
		vTipoGruppoPS[<%=contatoreRiga%>] = '<%=denominazione%>' ;
		vTipoGruppoID[<%=contatoreRiga%>] = <%=(BigDecimal)rowTipidoc.getAttribute("CDNTIPOGRUPPO")%> ;
		vTipoGruppoSoggetti[<%=j%>] = '<%=denominazione%>' ;
		vTipoGruppoIDSoggetti[<%=j%>] = <%=(BigDecimal)rowTipidoc.getAttribute("CDNTIPOGRUPPO")%> ;
		<%
		contatoreRiga = contatoreRiga + 1;
	}
	%>
}
	
function controllaCampi() {
	var index = document.Frm1.CDNTIPOGRUPPO.selectedIndex;		
	var selectedID = document.Frm1.CDNTIPOGRUPPO.options[index].value;
	if ( selezionatoTipoGruppoPatronatoSogg(selectedID) ) {
		if (document.Frm1.STRCODRIF.value == "") {
			alert("Il campo Codice di riferimento è obbligatorio nel caso in cui Tipo gruppo corrisponda ad un Patronato o Soggetto Accreditato.");
			return false;
		}	
	}
	if ( selezionatoTipoGruppoSoggettoAccreditato(selectedID) ) {
		if (document.Frm1.codComNas.value == "") {
			alert("Il campo Comune di riferimento è obbligatorio nel caso in cui Tipo gruppo corrisponda ad un Soggetto Accreditato.");
			return false;
		}
	}
	if (document.Frm1.codComNas.value != "") {
		if (!btFindComuneCAP_onSubmit(document.Frm1.codComNas, document.Frm1.strComNas, null, true)) {
			return false;
		}	
	}
	return true;
}
	
function selezionatoTipoGruppoPatronatoSogg(valore){
	for ( ix = 0; ix < vTipoGruppoID.length; ix=ix+1){
		if ( vTipoGruppoID[ix] == valore){
			return true;
		}
	}
	return false;
}

function selezionatoTipoGruppoSoggettoAccreditato(valore){
	for ( ix = 0; ix < vTipoGruppoIDSoggetti.length; ix=ix+1){
		if ( vTipoGruppoIDSoggetti[ix] == valore){
			return true;
		}
	}
	return false;
}