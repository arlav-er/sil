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

<%@ include file="../../global/noCaching.inc" %>
<%@ include file="../../global/getCommonObjects.inc" %>

<%
String _page = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "PAGE");

// Lettura parametri dalla REQUEST
int cdnfunzione = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
String cdnfunzioneStr =
SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");
String _funzione = String.valueOf(cdnfunzione);

// CONTROLLO ACCESSO ALLA PAGINA
ProfileDataFilter filter = new ProfileDataFilter(user, _page);
//	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
boolean canView = filter.canView();
if (/*false */! canView
	) {
	response.sendRedirect(
		"../../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	return;
}

// CONTROLLO PERMESSI SULLA PAGINA
//PageAttribs attributi = new PageAttribs(user, _page);
boolean canManage = true;

Object cdnUtCorrente = sessionContainer.getAttribute("_CDUT_");
String cdnUtCorrenteStr = StringUtils.getStringValueNotNull(cdnUtCorrente);
// Sola lettura: viene usato per tutti i campi di input
String readonly = String.valueOf(!canManage);
/***/
SourceBean datiLav1=null, datiLav2 = null;
String cdnLavoratore1="", cdnLavoratore2="";

it.eng.sil.module.amministrazione.accorpamento.DatiLavoratore dati1 = null;
it.eng.sil.module.amministrazione.accorpamento.DatiLavoratore dati2 = null;

if (serviceRequest.containsAttribute("CARICA_LAVORATORI") ) {
	SourceBean lav1 = (SourceBean)serviceResponse.getAttribute("GET_LAVORATORI.LAV1.ROWS.ROW");
	SourceBean lav2 = (SourceBean)serviceResponse.getAttribute("GET_LAVORATORI.LAV2.ROWS.ROW");
	dati1 = new it.eng.sil.module.amministrazione.accorpamento.DatiLavoratore(lav1);
	dati2 = new it.eng.sil.module.amministrazione.accorpamento.DatiLavoratore(lav2);
	if (dati1.strFlgcfOk.equals("S")) dati1.strFlgcfOk = "Si";
	if (dati1.strFlgcfOk.equals("N") ) dati1.strFlgcfOk = "No";
	if (dati2.strFlgcfOk.equals("S")) dati2.strFlgcfOk = "Si";
	if (dati2.strFlgcfOk.equals("N")) dati2.strFlgcfOk = "No";
	cdnLavoratore1 = (String)serviceRequest.getAttribute("cdnLavoratore1");
	cdnLavoratore2 = (String)serviceRequest.getAttribute("cdnLavoratore2");
}
else {
	dati1 = new it.eng.sil.module.amministrazione.accorpamento.DatiLavoratore(new SourceBean("VUOTO"));
	dati2=dati1;
}

// Stringhe con HTML per layout tabelle
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
<title>Accorpamento Lavoratore</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />

<af:linkScript path="../../js/" />

<script language="Javascript">

	/* Funzione chiamata al caricamento della pagina */
	function onLoad() {
		rinfresca();
		<%	// se si proviene dalla pagina di visualizza e confronta, bisogna mostrare i due lavoratori
			// selezionati e confrontati.
			if (serviceRequest.containsAttribute("CARICA_LAVORATORI") ) { 
		%>
			cambiaTendina(document.getElementById('tendinaLav1'),'lavoratoreSez1', document.Frm1.nome1);
			cambiaTendina(document.getElementById('tendinaLav2'),'lavoratoreSez2', document.Frm1.nome2);
		<%	
			}
		%>
	}
	function azzeraLavoratore1(){
        _azzeraLavoratore(1);
  	}
  	function azzeraLavoratore2() {
  		_azzeraLavoratore(2);
  	}
  	function _azzeraLavoratore(n){
		getItem("codiceFiscaleLavoratore", n).value = "";
		getItem("cognome", n).value = "";
		getItem("nome", n).value = "";
		getItem("CDNLAVORATORE", n).value = "";
		getItem("FLGCFOK", n).value = "";
        var imgV = document.getElementById("tendinaLav"+n);
        cambiaLavMC("lavoratoreSez"+n,"none");
        imgV.src=imgChiusa;
        imgV.alt = "Apri";
	}
	
	function aggiornaLavoratore1() {
		_aggiornaLavoratore(1);
    }
    function aggiornaLavoratore2() {
	    _aggiornaLavoratore(2);
    }
    function getItem(name, n) {
    	o = document.getElementsByName(name+n);
    	return o[0];
    }
    function _aggiornaLavoratore(n) {
    	if (getItem("CDNLAVORATORE", n%2+1).value== opened.dati.cdnLavoratore) {
    		alert("Impossibile scegliere due lavoratori uguali!");
	    	opened.close();
    		return;
    	}
	    getItem("CDNLAVORATORE", n).value = opened.dati.cdnLavoratore;
	    getItem("codiceFiscaleLavoratore", n).value = opened.dati.codiceFiscaleLavoratore;
	    getItem("nome", n).value = opened.dati.nome;
	    getItem("cognome", n).value = opened.dati.cognome;
	    flgCFOK = getItem("FLGCFOK", n);
	    flgCFOK.value = opened.dati.FLGCFOK;
	    if ( flgCFOK.value == "S" ){ 
	        flgCFOK.value = "Si";
	    }else 
	        if ( flgCFOK.value != "" ){
	            flgCFOK.value = "No";
        }	    
	    cambiaLavMC("lavoratoreSez"+n,"inline");
		var imgV = document.getElementById("tendinaLav"+n);
		imgV.src=imgAperta;
        imgV.alt = "Chiudi";
	    opened.close();
    }
    function controllaLav() {
    	if (document.Frm1.CDNLAVORATORE1.value=='' || document.Frm1.CDNLAVORATORE2.value=='') {
    		alert("seleziona due lavoratori");
    		return false;
    	}    	
    	return true;
    }
<%--
// Genera il Javascript che si occuperà di inserire i links nel footer
attributi.showHyperLinks(out, requestContainer, responseContainer, null);
--%>

</script>
<%@ include file="../../movimenti/MovimentiSezioniATendina.inc" %>
<%@ include file="../../movimenti/MovimentiRicercaSoggetto.inc" %>
</head>

<body class="gestione" onload="onLoad()">



<p class="titolo">Accorpamento lavoratori</p>

<af:form name="Frm1" action="AdapterHTTP" method="GET" onSubmit="controllaLav()">

	<af:textBox type="hidden" name="PAGE" value="AccorpaLavDettaglioPage" />
	<af:textBox type="hidden" name="cdnfunzione" value="<%=cdnfunzioneStr%>" />
	<af:textBox type="hidden" name="CDNLAVORATORE1" value="<%=cdnLavoratore1%>"/>
	<af:textBox type="hidden" name="CDNLAVORATORE2" value="<%=cdnLavoratore2%>"/>

	<%=htmlStreamTop%>
	<table class="main">

		<%-- ***************************************************************************** --%>
		<tr class="note">
			<td colspan="2">
			<div class="sezione2"><img id='tendinaLav1' alt='Apri'
				src='../../img/chiuso.gif'
				onclick="cambiaTendina(this,'lavoratoreSez1', document.Frm1.nome1);" />&nbsp;&nbsp;&nbsp;Lavoratore 1
			&nbsp;&nbsp; <%if (canManage) {%><a href="#"
				onClick="javascript:apriSelezionaSoggetto('Lavoratori', 'aggiornaLavoratore1');"><img
				src="../../img/binocolo.gif" alt="Cerca"></a>&nbsp;&nbsp;<a href="#"
				onClick="javascript:azzeraLavoratore1();"><img
				src="../../img/del.gif" alt="Azzera selezione"></a><%}%></div>
			</td>
		</tr>
		<tr>
			<td colspan="2">
			<div id="lavoratoreSez1" style="display: none">
			<table class="main" width="100%" border="0">
				<tr>
					<td class="etichetta">Codice Fiscale</td>
					<td class="campo" valign="bottom"><af:textBox classNameBase="input"
						type="text" name="codiceFiscaleLavoratore1" readonly="true"
						value="<%=dati1.strCodiceFiscale %>" size="30" maxlength="16" /> &nbsp;&nbsp;&nbsp;Validità
					C.F.&nbsp;&nbsp;<af:textBox classNameBase="input" type="text"
						name="FLGCFOK1" readonly="true" value="<%=dati1.strFlgcfOk %>" size="3" maxlength="3" />
					</td>
				</tr>
				<tr>
					<td class="etichetta">Cognome</td>
					<td class="campo"><af:textBox classNameBase="input" type="text"
						name="cognome1" readonly="true" value="<%= dati1.strCognome %>" size="30" maxlength="50" />
					</td>
				</tr>
				<tr>
					<td class="etichetta">Nome</td>
					<td class="campo"><af:textBox classNameBase="input" type="text"
						name="nome1" readonly="true" value="<%=dati1.strNome%>" size="30" maxlength="50" />
					</td>
				</tr>
			</table>
			</div>
			</td>
		</tr>
		<tr class="note">
			<td colspan="2">
			<div class="sezione2"><img id='tendinaLav2' alt='Apri'
				src='../../img/chiuso.gif'
				onclick="cambiaTendina(this,'lavoratoreSez2', document.Frm1.nome2);" />&nbsp;&nbsp;&nbsp;Lavoratore 2
			&nbsp;&nbsp; <%if (canManage) {%><a href="#"
				onClick="javascript:apriSelezionaSoggetto('Lavoratori', 'aggiornaLavoratore2');"><img
				src="../../img/binocolo.gif" alt="Cerca"></a>&nbsp;&nbsp;<a href="#"
				onClick="javascript:azzeraLavoratore2();"><img
				src="../../img/del.gif" alt="Azzera selezione"></a><%}%></div>
			</td>
		</tr>
		<tr>
			<td colspan="2">
			<div id="lavoratoreSez2" style="display: none">
			<table class="main" width="100%" border="0">
				<tr>
					<td class="etichetta">Codice Fiscale</td>
					<td class="campo" valign="bottom"><af:textBox classNameBase="input"
						type="text" name="codiceFiscaleLavoratore2" readonly="true"
						value="<%=dati2.strCodiceFiscale %>" size="30" maxlength="16" /> &nbsp;&nbsp;&nbsp;Validità
					C.F.&nbsp;&nbsp;<af:textBox classNameBase="input" type="text"
						name="FLGCFOK2" readonly="true" value="<%=dati2.strFlgcfOk %>" size="3" maxlength="3" />
					</td>
				</tr>
				<tr>
					<td class="etichetta">Cognome</td>
					<td class="campo"><af:textBox classNameBase="input" type="text"
						name="cognome2" readonly="true" value="<%= dati2.strCognome %>" size="30" maxlength="50" />
					</td>
				</tr>
				<tr>
					<td class="etichetta">Nome</td>
					<td class="campo"><af:textBox classNameBase="input" type="text"
						name="nome2" readonly="true" value="<%=dati2.strNome%>" size="30" maxlength="50" />
					</td>
				</tr>
			</table>
			</div>
			</td>
		</tr>
		<tr>
			<td colspan="2">
			<%if (canManage) {%><input type="submit" class="pulsanti" value="Visualizza e confronta" /><%}%>
			</td>
		</tr>
		
	</table>
	<%=htmlStreamBottom%>
</af:form>
</body>
</html>
