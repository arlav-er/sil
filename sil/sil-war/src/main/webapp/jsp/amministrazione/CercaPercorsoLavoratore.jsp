<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ include file="../amministrazione/openPage.inc" %>

<%@ page import="
  com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.User,
  it.eng.afExt.utils.*,
  it.eng.sil.security.ProfileDataFilter,  
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  it.eng.sil.security.PageAttribs,  
  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%--
	PARAMETRI CARATTERISTICI DELLA PAGINA:
		TIPO_INFO: e' il vettore dei codici del tipo di informazione che l'utente vuole che venga visualizzato
		PROVENIENZA: indica se si arriva a questa pagina dal menu del lavoratore o dalla pagina della lista(tasto indietro)
--%>
<%
String configComboQualifica = StringUtils.getAttributeStrNotNull(serviceResponse,"M_GetConfigFlagQualifica.ROWS.ROW.FLGPLQUALIFICA");
String queryString=null;
String _current_page = (String)serviceRequest.getAttribute("PAGE");
String _page = (String)serviceRequest.getAttribute("PAGE");
// map <codice informazione-visibilita'>
// se l'informazione e' da visualizzare allora il suo codice sara' presente nella map. Se non e'
// da visualizzare sara' assente (al valore 1 non e' associato alcun significato)
HashMap infoSelezionate = null;
String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");
String cdnFunzione = (String)serviceRequest.getAttribute("CDNFUNZIONE");
String provenienza = (String)serviceRequest.getAttribute("PROVENIENZA");
String provenienzaMenu = (String)serviceRequest.getAttribute("PROVENIENZA_MENU");
Vector infoDaVisualizzare = serviceRequest.getAttributeAsVector("TIPO_INFO");

// 22Dec2011 impostazioni di visibilità del check box Liste Speciali
boolean vsPercLavImpostato = false;
Vector impostazioniVS = serviceResponse.getAttributeAsVector("M_GetConfigValue.ROWS.ROW");
if ( impostazioniVS != null && impostazioniVS.size()>0){
	SourceBean row = (SourceBean) impostazioniVS.get(0);
	vsPercLavImpostato = "1".equals(row.getAttribute("NUM"));
}

Vector impostazioni = serviceResponse.getAttributeAsVector("M_GETCONFIGPERCORSI.ROWS.ROW");
infoSelezionate = new HashMap(impostazioni.size());
if (provenienza != null && provenienza.equals("vista")) {
// si proviene dalla lista. Si caricano le info selezionate in partenza dall'utente.
	for (int i = 0; i < infoDaVisualizzare.size(); i++) {
		infoSelezionate.put(infoDaVisualizzare.get(i), "1");
	}
} else {
	// si proviene dal menu contestuale del lavoratore. Si caricano le info configurate nel db.
	for (int i = 0; i < impostazioni.size(); i++) {
		SourceBean row = (SourceBean)impostazioni.get(i);
		String codMonoTipo = (String)row.getAttribute("STRVALORE");
		boolean impostato = ((BigDecimal)row.getAttribute("NUM")).intValue() == 1;
		if (impostato)
			infoSelezionate.put(codMonoTipo, "1");
	}
}
/* e' stato deciso che dal menu generale non si possa arrivare a questa pagina. Se definitivo cancellare

// si puo' arrivare a questa pagina dal menu del lavoratore (LAV) o dalla ricerca lavoratori del menu generale (GEN)
// se il parametro non e' ancora valorizzato vuol dire che si e' arrivati qui dal menu contestuale del lavoratore
if (provenienzaMenu==null) 
	provenienzaMenu = "LAV";
*/
String dataInizio = Utils.notNull(serviceRequest.getAttribute("dataInizio"));
String dataFine = Utils.notNull(serviceRequest.getAttribute("datafine"));
String intestazione = Utils.notNull(serviceRequest.getAttribute("intestazione"));
// testata del lavoratore
InfCorrentiLav testata = new InfCorrentiLav(cdnLavoratore, user);
//
PageAttribs attributi = new PageAttribs(user, _current_page);
ProfileDataFilter filter = new ProfileDataFilter(user, _current_page);
filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
if (! filter.canViewLavoratore())
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
// gli attributi di visibilita' in questa pagina non vengono gestiti (per ora)	
boolean canModify = true;

String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

// verifica se l'utente appartiene al gruppo Patronato 
// e in tal caso vincola il valore di default della combo
// che non sarà visualizzata
String plptmanValue = "S";
boolean isTipoGruppoPatronato = false;
if (user.getCodTipo().equalsIgnoreCase(MessageCodes.Login.COD_TIPO_GRUPPO_PATRONATO)) {
	// riconosciuto utente tipo gruppo patronato
	isTipoGruppoPatronato = true;
	// lettura del valore di default per la combo
	Vector configPerLavPatMan = serviceResponse.getAttributeAsVector("M_GetConfigPerLavPatMan.ROWS.ROW");
	if (configPerLavPatMan != null && configPerLavPatMan.size() == 1) {
		SourceBean row = (SourceBean)configPerLavPatMan.get(0);
		if (row != null) {
			plptmanValue = row.getAttribute("STRVALORE") != null ? (String)row.getAttribute("STRVALORE") : "S";
		}
	}
}

%>

<html>

<head>
<title>Cpi Migrazioni</title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<af:linkScript path="../../js/" />
<SCRIPT>

  	function controllaCheck() {
  		ret = false;
  		infoVista = document.getElementsByName("TIPO_INFO");
  		for (i=0;!ret &&i <infoVista.length;i++) {
  			if (infoVista[i].checked) 
  				ret = true;
  		}
  		if (!ret)
  			alert('Selezionare almeno una informazione da filtrare.');
  		if (ret) {
  			if (!controllaFunzTL()) {
  				dataInizio = document.Frm1.datainizio.value;
  				dataFine = document.Frm1.datafine.value;
  				if (dataInizio!='' && dataFine!='' && compDate(dataInizio, dataFine)>0) {
  					alert("La data inizio (dalla data) deve essere minore o uguale alla data di termine (alla data)");
  					ret = false;
  				}
  			}
  		}  		
  		return ret;
  	}

  	function toDate(newDate) {
	    var tokens = newDate.split('/');
	    var usDate= tokens[2]+"/"+tokens[1]+"/"+tokens[0];
	    return new Date(usDate);
	}
  	function compDate(date1,date2) {
	    if (date1==null || date1=="") return -1;    
	    if (date2==null || date2=="") return 1;
	    if ( toDate(date1).getTime()<toDate(date2).getTime()) return -1;
	    if ( toDate(date1).getTime()>toDate(date2).getTime()) return 1;
    return 0;
}

  </SCRIPT>
  <script language="Javascript">
  <% 
       //Genera il Javascript che si occuperà di inserire i links nel footer
       attributi.showHyperLinks(out, requestContainer,responseContainer,"");
  %>
</script>
</head>

<body class="gestione" onload="rinfresca()">
<%testata.show(out);%>
<center>
<p class="titolo">Impostazioni</p>
<af:form method="GET" action="AdapterHTTP" name="Frm1"	onSubmit="controllaCheck()">

	<input type="hidden" name="PAGE" value="ListPercorsoLavoratorePage">
	<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>" />
	<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>" />
	<input type="hidden" name="PROVENIENZA"	value="CercaPercorsoLavoratorePage" />
	<input type="hidden" name="PROVENIENZA_MENU"	value="<%=provenienzaMenu%>" />
	
	<input type="hidden" name="datafine" value="">

	<%out.print(htmlStreamTop);%>
	<TABLE class="main">
		<tr>
			
			<td class="campo" colspan="2" style="text-align: center">Intestazione &nbsp;&nbsp;
				<af:textBox classNameBase="input"
				readonly="<%=String.valueOf(!canModify)%>" 
				name="intestazione" value="<%=intestazione%>" 
				required="false" size="80" maxlength="100" title="Intestazione stampa" />
		</tr>
		
		<% if (attributi.containsButton("A")) { %>
		<tr>
			<td colspan="2" class="campo" style="text-align: center">&nbsp;</td>
		</tr>
		<tr>
			<td class="etichetta2"><input type="checkbox" name="TIPO_INFO" value="A"
				<%=infoSelezionate.containsKey("A") ? "CHECKED" : ""%>></td>
			<td class="campo">Dichiarazione immediata disponibilit&agrave;</td>
		</tr>
		<% } %>
		<% if (attributi.containsButton("B")) { %>
		<tr>
			<td class="etichetta2"><input type="checkbox" name="TIPO_INFO" value="B"
				<%=infoSelezionate.containsKey("B") ? "CHECKED" : ""%>></td>
			<td class="campo">Movimenti amministrativi da comunicazione obbligatoria</td>
			<td class="campo">
			
				<% if (isTipoGruppoPatronato) { %>
			
					<%-- combo box non visibile e valore fisso sul valore letto dalla ts_config_loc--%>
					<input type="hidden" name="movAmmComObbligatoria" value="<%=plptmanValue%>">
				
				<% } else { %>	
			
					<% if (vsPercLavImpostato)  {%>
						<af:comboBox name="movAmmComObbligatoria" addBlank="false" title="Visualizza qualifica o no">
							<option value="S" <%= (configComboQualifica.equals("")||configComboQualifica.equals("S"))?"selected=\"selected\"":""%>>Visualizzare qualifica</option>
							<option value="N" <%= (configComboQualifica.equals("N"))?"selected=\"selected\"":""%>>Non visualizzare qualifica</option>
						</af:comboBox>
					<%}else{ %>
						<%-- combo box non visibile e valore fisso sul primo valore del combo--%>
						<input type="hidden" name="movAmmComObbligatoria" value="S">
					<%}%>
					
				<%}%>
				
			</td>
		</tr>
		<% } %>
		<% if (attributi.containsButton("O")) { %>
		<tr>
			<td class="etichetta2"><input type="checkbox" name="TIPO_INFO" value="O"
				<%=infoSelezionate.containsKey("O") ? "CHECKED" : ""%>></td>
			<td class="campo">Movimenti amministrativi dichiarati dal lavoratore/documentati</td>
			<td class="campo">
			
				<% if (isTipoGruppoPatronato) { %>
					
					<%-- combo box non visibile e valore fisso sul valore letto dalla ts_config_loc--%>
					<input type="hidden" name="movAmmDichLav" value="<%=plptmanValue%>">
					
				<% } else { %>
					
					<% if (vsPercLavImpostato)  {%>
						<af:comboBox name="movAmmDichLav" addBlank="false" title="Visualizza qualifica o no">
							<option value="S" <%= (configComboQualifica.equals("")||configComboQualifica.equals("S"))?"selected=\"selected\"":""%>>Visualizzare qualifica</option>
							<option value="N" <%= (configComboQualifica.equals("N"))?"selected=\"selected\"":""%>>Non visualizzare qualifica</option>
						</af:comboBox>
					<%}else{ %>
						<%-- combo box non visibile e valore fisso sul primo valore del combo--%>
						<input type="hidden" name="movAmmDichLav" value="S">
					<%}%>
					
				<% } %>
			</td>
		</tr>
		<% } %>
		<% if (attributi.containsButton("C")) { %>
		<tr>
			<td class="etichetta2"><input type="checkbox" name="TIPO_INFO" value="C"
				<%=infoSelezionate.containsKey("C") ? "CHECKED" : ""%>></td>
			<td class="campo">Storico cambio stato occupazionale</td>
		</tr>
		<% } %>
		<% if (attributi.containsButton("D")) { %>
		<tr>
			<td class="etichetta2"><input type="checkbox" name="TIPO_INFO" value="D"
				<%=infoSelezionate.containsKey("D") ? "CHECKED" : ""%>></td>
			<td class="campo">Trasferimento di iscrizione</td>
		</tr>
		<% } %>
		<% if (attributi.containsButton("E")) { %>
		<tr>
			<td class="etichetta2"><input type="checkbox" name="TIPO_INFO" value="E"
				<%=infoSelezionate.containsKey("E") ? "CHECKED" : ""%>></td>
			<td class="campo">Mobilit&agrave;</td>
		</tr>
		<% } %>
		<% if (attributi.containsButton("R")) { %>
		<tr>
			<td class="etichetta2"><input type="checkbox" name="TIPO_INFO" value="R"
				<%=infoSelezionate.containsKey("R") ? "CHECKED" : ""%>></td>
			<td class="campo">Coll. Mirato</td>
		</tr>
		<% } %>
		<% if (attributi.containsButton("F")) { %>
		<tr>
			<td class="etichetta2"><input type="checkbox" name="TIPO_INFO" value="F"
				<%=infoSelezionate.containsKey("F") ? "CHECKED" : ""%>></td>
			<td class="campo">Patto (150)/Accordo generico</td>
		</tr>
		<% } %>
		<% if (attributi.containsButton("G")) { %>
		<tr>
			<td class="etichetta2" ><input type="checkbox" name="TIPO_INFO" value="G"
				<%=infoSelezionate.containsKey("G") ? "CHECKED" : ""%>></td>
			<td class="campo">Appuntamenti/contatti</td>
		</tr>
		<% } %>
		<% if (attributi.containsButton("H")) { %>
		<tr>
			<td class="etichetta2"><input type="checkbox" name="TIPO_INFO" value="H"
				<%=infoSelezionate.containsKey("H") ? "CHECKED" : ""%>></td>
			<td class="campo">Colloqui</td>
		</tr>
		<% } %>
		<% if (attributi.containsButton("I")) { %>
		<tr>
			<td class="etichetta2"><input type="checkbox" name="TIPO_INFO" value="I"
				<%=infoSelezionate.containsKey("I") ? "CHECKED" : ""%>></td>
			<td class="campo">Azioni concordate</td>
		</tr>
		<% } %>
		<% if (attributi.containsButton("L")) { %>
		<tr>
			<td class="etichetta2"><input type="checkbox" name="TIPO_INFO" value="L"
				<%=infoSelezionate.containsKey("L") ? "CHECKED" : ""%>></td>
			<td class="campo">Segnalazione in rose</td>
		</tr>
		<% } %>
		<% if (attributi.containsButton("N")) { %>
		<tr>
			<td class="etichetta2"><input type="checkbox" name="TIPO_INFO" value="N"
				<%=infoSelezionate.containsKey("N") ? "CHECKED" : ""%>></td>
			<td class="campo">Esclusione dalle rose</td>
		</tr>
		<% } %>
		<% if (attributi.containsButton("M")) { %>
		<tr>
			<td class="etichetta2"><input type="checkbox" name="TIPO_INFO" value="M"
				<%=infoSelezionate.containsKey("M") ? "CHECKED" : ""%>></td>
			<td class="campo">Dich. reddito</td>
		</tr>
		<% } %>
		<% if (attributi.containsButton("P")) { %>
		<tr>
			<td class="etichetta2"><input type="checkbox" name="TIPO_INFO" value="P"
				<%=infoSelezionate.containsKey("P") ? "CHECKED" : ""%>></td>
			<td class="campo">Cancellazione da Graduatorie CM/art.1</td>
		</tr>
		<% } %>
		<% if (attributi.containsButton("Q")) { %>
		<tr>
			<td class="etichetta2"><input type="checkbox" name="TIPO_INFO" value="Q"
				<%=infoSelezionate.containsKey("Q") ? "CHECKED" : ""%>></td>
			<td class="campo">Missioni</td>
		</tr>
		<% } %>
		
		<% if (vsPercLavImpostato)  {%>
		<% if (attributi.containsButton("S")) { %>
		<tr>
			<td class="etichetta2"><input type="checkbox" name="TIPO_INFO" value="S"
				<%=infoSelezionate.containsKey("S") ? "CHECKED" : ""%>></td>
			<td class="campo">Liste speciali</td>
		</tr>
		<% } %>
		<%}%>
		<tr>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td colspan="2" style="text-align: center">
				<div class="sezione">&nbsp;</div>
			</td>
		</tr>
		<tr>
			<td class="etichetta">Dalla data
			<td class="campo"><af:textBox classNameBase="input"
				readonly="<%=String.valueOf(!canModify)%>" type="date"
				name="datainizio" value="<%=dataInizio%>" validateOnPost="true"
				required="false" size="12" maxlength="10" title="Data inizio range" />
		</tr>
		<tr>
			<td colspan="2" align="center">
				<input class="pulsanti" type="submit"  name="aggiorna" value="Visualizza" >
				
			</td>
		</tr>
		<tr>
			<td colspan="2">
		<%-- SE DIFINITIVO CANCELLARE 
		if (provenienzaMenu.equals("GEN")) 
          		out.print(InfCorrentiAzienda.formatBackList(sessionContainer, "LISTALAVORATORIPERCORSOPAGE" ));
         --%>
         </tr>
	</table>
<%out.print(htmlStreamBottom);%>
</af:form>
</center>
</body>
</html>
