<%@ page contentType="text/html;charset=utf-8"%>
<%-- /////////////////////////////////// --%>
<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.security.ProfileDataFilter,                  
                  it.eng.sil.util.*,
                  java.util.*,
                  it.eng.afExt.utils.*,
                  com.engiweb.framework.security.*,
                  java.math.*" %>
<%@ taglib uri="aftags" prefix="af" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%
boolean onlyInsert = serviceRequest.getAttribute("ONLY_INSERT") == null ? false : true;
String COD_LST_TAB = "";
if (onlyInsert) {
	COD_LST_TAB = "OR_PER"; //(String)serviceRequest.getAttribute("COD_LST_TAB");
}

String cdnFunzione = (String)serviceRequest.getAttribute("CDNFUNZIONE");
String _page = (String)serviceRequest.getAttribute("PAGE");
///
//
// questi parametri serviranno per tornare indietro alla pagina della lista e permettere di eseguire la stessa query 
// da decidere se eseguire, di ritorno dalla pagina di dettaglio, una query generale o la stessa eseguita dalla pagina di ricerca
// Se mi trovo nello stato di inserisci nuovo l' unico valore certo che avro' sara' il codice cpi
// tutto il resto sara' valorizzato o meno
String codiceFiscale = (String)serviceRequest.getAttribute("CF");
String cognome = (String)serviceRequest.getAttribute("COGNOME");
String nome = (String)serviceRequest.getAttribute("NOME");
String dataInizioColloquio = (String)serviceRequest.getAttribute("DATAINIZIO");
String dataFineColloquio = (String)serviceRequest.getAttribute("DATAFINE");
/* inserisci un nuovo colloquio con il lavoratore */
boolean inserisciNuovo =
	!(serviceRequest.getAttribute("inserisciNuovo") == null
		|| ((String)serviceRequest.getAttribute("inserisciNuovo")).length() == 0);
/////////////////////////////////////
String codCPI = null;
String datColloquio = null;
String aspirazioni = null;
String potenzialita = null;
String vincoliLavoratore = null;
String strNote = null;
String obiettivo = null;
String azioniConcordate = null;
String vincoliMercato = null;
String analisiLavoro = null;
String operatoreSpi = null;
String numklocolloquio = null;
String codServizio = null;
String prgColloquio = null;
String cdnLavoratore = null;
//
BigDecimal cdnUtIns = null;
String dtmIns = null;
BigDecimal cdnUtMod = null;
String dtmMod = null;
Testata operatoreInfo = null;

cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");
if (!inserisciNuovo) { // dettaglio colloquio lavoratore
	SourceBean row = null;
	Vector rows = serviceResponse.getAttributeAsVector("M_LOAD_COLLOQUIO.ROWS.ROW");
	// siamo in dettaglio per cui avro' al massimo una riga
	if (rows.size() == 1) {
		row = (SourceBean)rows.get(0);
		datColloquio = Utils.notNull(row.getAttribute("DATCOLLOQUIO"));
		aspirazioni = Utils.notNull(row.getAttribute("STRDESASPIR"));
		potenzialita = Utils.notNull(row.getAttribute("STRRISPOTENZ"));
		vincoliLavoratore = Utils.notNull(row.getAttribute("STRVINCLAV"));
		strNote = Utils.notNull(row.getAttribute("STRNOTE"));
		obiettivo = Utils.notNull(row.getAttribute("STROBIETTIVO"));
		azioniConcordate = Utils.notNull(row.getAttribute("STRAZIONICON"));
		vincoliMercato = Utils.notNull(row.getAttribute("STRVINCEST"));
		analisiLavoro = Utils.notNull(row.getAttribute("STRANALISILAVORO"));
		operatoreSpi = Utils.notNull(row.getAttribute("PRGSPI"));
		prgColloquio = Utils.notNull(row.getAttribute("PRGCOLLOQUIO"));
		codCPI = Utils.notNull(row.getAttribute("CODCPI"));
		codServizio = Utils.notNull(row.getAttribute("CODSERVIZIO"));
		cdnLavoratore = Utils.notNull(row.getAttribute("CDNLAVORATORE"));
		// aggiornamento del lock ottimistico in aggiornamento    
		numklocolloquio = String.valueOf(((BigDecimal)row.getAttribute("NUMKLOCOLLOQUIO")).intValue() + 1);
		cdnUtIns = (BigDecimal)row.getAttribute("CDNUTINS");
		dtmIns = (String)row.getAttribute("DTMINS");
		cdnUtMod = (BigDecimal)row.getAttribute("CDNUTMOD");
		dtmMod = (String)row.getAttribute("DTMMOD");
		operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
	}
} else {
	// valorizzo gli attributi required
	codCPI = (String)serviceRequest.getAttribute("CODCPI");
	cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");
	datColloquio = it.eng.afExt.utils.DateUtils.getNow();
}
if (codCPI == null || codCPI.equals("")) {
	codCPI = user.getCodRif();
}
Vector percorsi = serviceResponse.getAttributeAsVector("M_GetPercorsiColloquio.rows.row");
String etichettaPercorsi = percorsi.size()>0 ? "Percorsi concordati presenti": "Percorsi concordati assenti";

//Selezione del prgSpi in fase di inserimento, in modo da prevalorizzare la combo
if (inserisciNuovo) {
	SourceBean operatore = (SourceBean)serviceResponse.getAttribute("M_Select_PrgSPI_Da_CDNUT");
	if ((operatore != null) && (operatore.getAttribute("ROWS.ROW.prgSpi") != null)) {
		if (!operatore.getAttribute("ROWS.ROW.prgSpi").equals(""))
			operatoreSpi = operatore.getAttribute("ROWS.ROW.prgSpi").toString();
	}
}

boolean canInsert = false;
boolean canModify = false;

String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
///////////////////////
boolean readOnlyStr = !canModify;
String fieldReadOnly = canModify ? "false" : "true";

String labelServizio = "Programma";
String titleServizio = "Tipo "+labelServizio;
%>



<html>
<head>
<title>Percorso lavoratore: Colloquio</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<af:linkScript path="../../js/" />

<%@ include file="../movimenti/MovimentiSezioniATendina.inc" %>




<SCRIPT TYPE="text/javascript">
<!--

var urlpage="AdapterHTTP?";




function apriPercorsi() {
    if (document.Frm1.PRGCOLLOQUIO.value=="") {
        alert("Inserire un colloquio prima di associare un percorso");
        return ;
    }
    var urlPage = urlpage;
	urlPage+="cdnLavoratore=<%=cdnLavoratore%>&";
	urlPage+="CDNFUNZIONE=<%=cdnFunzione%>&";
    urlPage+="PRGCOLLOQUIO=<%=prgColloquio%>&";
	urlPage+="PAGE=PERCORSIPAGE";
	urlPage+="&statoSezioni=<%=StringUtils.getAttributeStrNotNull(serviceRequest, "statoSezioni")%>";
	urlPage+="&pageChiamante=<%=StringUtils.getAttributeStrNotNull(serviceRequest, "PAGECHIAMANTE")%>";
  <%if (onlyInsert) {%>
    urlPage+="&ONLY_INSERT=1";
  <%}%>
  urlPage+="&COD_LST_TAB=<%=COD_LST_TAB%>";
  setWindowLocation(urlPage);
}


//-->
</script>


<script language="Javascript">
function setSezioneNote() {
	if ((document.Frm1.strDesAspir.value == "") && (document.Frm1.STRRISPOTENZ.value == "") && 
			(document.Frm1.STROBIETTIVO.value == "") && (document.Frm1.STRVINCLAV.value == "") && 
			(document.Frm1.STRVINCEST.value == "") && (document.Frm1.STRAZIONICON.value == "") && (document.Frm1.STRANALISILAVORO.value == "")) {
    	cambia(document.getElementById("tendinaInfo"), document.getElementById("altreInfoSez"));
  	}
}
</script>

<body class="gestione" onload="setSezioneNote()">
<br>
<font color="red"><af:showErrors /></font>

<af:form name="Frm1" method="POST" action="AdapterHTTP">
	<%out.print(htmlStreamTop);%>
	<p class="titolo">Colloquio</p>
	<table class="main">		
		<tr>
			<td class="etichetta"><%=labelServizio %></td>
			<td class="campo" nowrap><af:comboBox name="CODSERVIZIO"
				moduleName="M_DE_LIST_SERVIZI" selectedValue="<%=codServizio%>"
				classNameBase="input" addBlank="true" required="true"
				disabled="<%=fieldReadOnly%>" title="<%=titleServizio %>"
				onChange="fieldChanged();" /></td>
		</tr>
		<tr>
			<td class="etichetta">Operatore SPI</td>
			<td class="campo" nowrap><af:comboBox onChange="fieldChanged();"
				name="PRGSPI" moduleName="COMBO_SPI" classNameBase="input"
				selectedValue="<%=operatoreSpi%>" addBlank="true" required="true"
				disabled="<%=fieldReadOnly%>" title="Operatiore Spi" /></td>
		</tr>

		<tr>
			<td class="etichetta">Data programma</td>
			<td class="campo"><af:textBox classNameBase="input"
				readonly="<%=fieldReadOnly%>" type="date" name="datColloquio"
				value="<%=datColloquio%>" validateOnPost="true" required="true"
				size="12" maxlength="10" 
				title="Data programma" /></td>
		</tr>
		<tr>			
			<td>&nbsp;
			<td class="campo"><af:textBox classNameBase="input"
				readonly="<%=fieldReadOnly%>"  name="percorsi"
				value="<%=etichettaPercorsi%>" 
				size="30" maxlength="30" 
				title="Presenza percorsi" /></td>
		</tr>
		<tr>
			<td class="etichetta">Note colloquio</td>
			<td class="campo"><af:textArea cols="80" rows="5" maxlength="2000"
				readonly="<%=fieldReadOnly%>" classNameBase="textarea"
				name="STRNOTE" value="<%=strNote%>" validateOnPost="true"
				required="false" title="Note"  /></td>
		</tr>
		<tr>
			<td colspan=2><br>
			</td>
		</tr>

		<tr>
			<td class="etichetta" colspan="2">
			<div class='sezione2' id='UlteInfo'><img id='tendinaInfo'
				alt='Chiudi' src='../../img/aperto.gif'
				onclick='cambia(this, document.getElementById("altreInfoSez"));' />
			Ulteriori informazioni&nbsp;</div>
			</td>
		</tr>
		<tr>
			<td colspan="2">
			<div id="altreInfoSez" style="display: inline">
			<table class="main" width="100%" border="0">
				<tr>
					<td class="etichetta">Aspirazioni</td>
					<td class="campo"><af:textArea cols="80" rows="5" maxlength="2000"
						readonly="<%=fieldReadOnly%>" classNameBase="textarea"
						name="strDesAspir" value="<%=aspirazioni%>" validateOnPost="true"
						required="false" title="Aspirazioni"  />
					</td>
				</tr>
				<tr>
					<td class="etichetta">Motivazione</td>
					<td class="campo"><af:textArea cols="80" rows="5" maxlength="2000"
						readonly="<%=fieldReadOnly%>" classNameBase="textarea"
						name="STRRISPOTENZ" value="<%=potenzialita%>"
						validateOnPost="true" required="false" title="Potenzialità"
						 /></td>
				</tr>
				<tr>
					<td class="etichetta">Obiettivo</td>
					<td class="campo"><af:textArea cols="80" rows="5" maxlength="2000"
						readonly="<%=fieldReadOnly%>" classNameBase="textarea"
						name="STROBIETTIVO" value="<%=obiettivo%>" validateOnPost="true"
						required="false" title="Obiettivo"  /></td>
				</tr>
				<tr>
					<td class="etichetta">Vincoli lavoratore</td>
					<td class="campo"><af:textArea cols="80" rows="5" maxlength="2000"
						readonly="<%=fieldReadOnly%>" classNameBase="textarea"
						name="STRVINCLAV" value="<%=vincoliLavoratore%>"
						validateOnPost="true" required="false" title="VincoliLavoratore"
						 /></td>
				</tr>
				<tr>
					<td class="etichetta">Vincoli mercato</td>
					<td class="campo"><af:textArea cols="80" rows="5" maxlength="2000"
						readonly="<%=fieldReadOnly%>" classNameBase="textarea"
						name="STRVINCEST" value="<%=vincoliMercato%>"
						validateOnPost="true" required="false" title="VincoliMercato"
						 /></td>
				</tr>
				<tr>
					<td class="etichetta">Azioni concordate</td>
					<td class="campo"><af:textArea cols="80" rows="5" maxlength="2000"
						readonly="<%=fieldReadOnly%>" classNameBase="textarea"
						name="STRAZIONICON" value="<%=azioniConcordate%>"
						validateOnPost="true" required="false" title="Azioni"
						 /></td>
				</tr>
				<tr>
					<td class="etichetta">Analisi rispetto al lavoro</td>
					<td class="campo"><af:textArea cols="80" rows="5" maxlength="2000"
						readonly="<%=fieldReadOnly%>" classNameBase="textarea"
						name="STRANALISILAVORO" value="<%=analisiLavoro%>"
						validateOnPost="true" required="false" title="Analisi rispetto al lavoro"
						 /></td>
				</tr>
			</table>
			</div>
			</td>
		</tr>
		
	</table>
	<%out.print(htmlStreamBottom);%>
	<%if (!inserisciNuovo) {%>
	<center>
		<%operatoreInfo.showHTML(out);%>
		<br>
		<input type="button" class="pulsanti" value="Chiudi" onclick="window.close()">
	</center>
	<%}%>

</af:form>


</body>
</html>

