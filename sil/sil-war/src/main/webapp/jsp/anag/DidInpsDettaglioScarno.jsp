<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page
	import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  it.eng.sil.security.User,
                  it.eng.sil.security.ProfileDataFilter,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  java.lang.*,
                  java.text.*
                  "%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%
String cdnLavoratore = null;
	String _page = (String) serviceRequest.getAttribute("PAGE");
	ProfileDataFilter filter = new ProfileDataFilter(user,
			"listaDidInpsPage");
	
	PageAttribs attributi = new PageAttribs(user, _page);

	boolean rdOnly = true;
	boolean canView = filter.canViewLavoratore();
	if (!canView) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	} else {
		rdOnly = !attributi.containsButton("AGGIORNA");

	}
	String prgDidInps = null;
	String datDichiarazione = null;
	String datInizioAttSub = null;
	String datInizioDisocupaz = null;
	String datInvio = null;
	String datRicezione = null;
	String CodMonoTipoOp = null;
	String DecRedditoPrevisto = null;
	String DecRedditoLavoro = null;
	String cdnUtIns = null;
	String dtmIns = null;
	String cdnUtMod = null;
	String dtmMod = null;
	String NUMKLODIDINPS = null;
	String CODUNIVOCO = null;
	String strAzCodFiscale = null;
	String strCellulare = null;
	String strCodFiscLav = null;
	String codCPI = null;
	String descCPI = null;
	String codmonotipocpi = null;

	String codCpiUser = null;
	String strComunicazione = null;
	String strDenominazione = null;
	String strEmail = null;
	String strProtocolloInps = null;
	String strUltimaQualifica = null;
	String strTelefono = null;

	Testata operatoreInfo = null;

	SourceBean row = (SourceBean) serviceResponse
			.getAttribute("M_GetDidInps.ROWS.ROW");
	SourceBean elanagrow = row;
    
	if (row.containsAttribute("cdnLavoratore")) {
		cdnLavoratore = row.getAttribute("cdnLavoratore").toString();
		filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	}
	if (row.containsAttribute("prgDidInps")) {
		prgDidInps = row.getAttribute("prgDidInps").toString();
	}
	if (row.containsAttribute("DATDICHIARAZIONE")) {
		datDichiarazione = row.getAttribute("DATDICHIARAZIONE")
				.toString();
	}
	if (row.containsAttribute("DATINIZIOATTSUB")) {
		datInizioAttSub = row.getAttribute("DATINIZIOATTSUB")
				.toString();
	}
	if (row.containsAttribute("DATINIZIODISOCUPAZ")) {
		datInizioDisocupaz = row.getAttribute("DATINIZIODISOCUPAZ")
				.toString();
	}
	if (row.containsAttribute("DATINVIO")) {
		datInvio = row.getAttribute("DATINVIO").toString();
	}
	if (row.containsAttribute("DATRICEZIONE")) {
		datRicezione = row.getAttribute("DATRICEZIONE").toString();
	}
	if (row.containsAttribute("CODUNIVOCO")) {
		CODUNIVOCO = row.getAttribute("CODUNIVOCO").toString();
	}
	if (row.containsAttribute("CODMONOTIPOOPERAZIONE")) {
		CodMonoTipoOp = row.getAttribute("CODMONOTIPOOPERAZIONE")
				.toString();
	}
	if (row.containsAttribute("DECREDDITOLAVORO")) {
		DecRedditoLavoro = row.getAttribute("DECREDDITOLAVORO")
				.toString();
	}
	if (row.containsAttribute("DECREDDITOPREVISTO")) {
		DecRedditoPrevisto = row.getAttribute("DECREDDITOPREVISTO")
				.toString();
	}
	if (row.containsAttribute("CDNUTINS")) {
		cdnUtIns = row.getAttribute("CDNUTINS").toString();
	}
	if (row.containsAttribute("DTMINS")) {
		dtmIns = row.getAttribute("DTMINS").toString();
	}
	if (row.containsAttribute("CDNUTMOD")) {
		cdnUtMod = row.getAttribute("CDNUTMOD").toString();
	}
	if (row.containsAttribute("DTMMOD")) {
		dtmMod = row.getAttribute("DTMMOD").toString();
	}
	if (row.containsAttribute("NUMKLODIDINPS")) {
		NUMKLODIDINPS = row.getAttribute("NUMKLODIDINPS").toString();
	}
	if (row.containsAttribute("STRAZCODICEFISCALE")) {
		strAzCodFiscale = row.getAttribute("STRAZCODICEFISCALE")
				.toString();
	}
	if (row.containsAttribute("STRCELLULARE")) {
		strCellulare = row.getAttribute("STRCELLULARE").toString();
	}
	if (row.containsAttribute("STRCODICEFISCALELAV")) {
		strCodFiscLav = row.getAttribute("STRCODICEFISCALELAV")
				.toString();
	}

	if (row.containsAttribute("CODUNIVOCO")) {
		codCPI = row.getAttribute("CODUNIVOCO").toString();
	}
	if (row.containsAttribute("DENOMINAZIONECPI")) {
		descCPI = row.getAttribute("DENOMINAZIONECPI").toString();
	}
	if (row.containsAttribute("STRCOMUNICAZIONE")) {
		strComunicazione = row.getAttribute("STRCOMUNICAZIONE")
				.toString();
	}
	if (row.containsAttribute("STRDENOMINAZIONE")) {
		strDenominazione = row.getAttribute("STRDENOMINAZIONE")
				.toString();
	}
	if (row.containsAttribute("STREMAIL")) {
		strEmail = row.getAttribute("STREMAIL").toString();
	}
	if (row.containsAttribute("STRPROTOCOLLOINPS")) {
		strProtocolloInps = row.getAttribute("STRPROTOCOLLOINPS")
				.toString();
	}
	if (row.containsAttribute("STRTELEFONO")) {
		strTelefono = row.getAttribute("STRTELEFONO").toString();
	}
	if (row.containsAttribute("STRULTIMAQUALIFICA")) {
		strUltimaQualifica = row.getAttribute("STRULTIMAQUALIFICA")
				.toString();
	}

	operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);

	String cdnFunzione = (String) serviceRequest
			.getAttribute("cdnFunzione");
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
<title>Dettaglio D.I.D Inps</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />

<af:linkScript path="../../js/" />

<script language="Javascript">
	
<%//Genera il Javascript che si occuperà di inserire i links nel footer
if (cdnLavoratore != null)
			attributi.showHyperLinks(out, requestContainer, responseContainer,
					"cdnLavoratore=" + cdnLavoratore);%>
</script>
</head>

<body class="gestione">

	<font color="red"><af:showErrors /></font>

	<af:form method="POST" action="AdapterHTTP" name="Frm1"
		onSubmit="controlla()">
		<p class="titolo">DID INPS</p>
		<% out.print(htmlStreamTop); %>
		<br />
		<table class="main">
			<tr>
				<td colspan="4"><br />
					<div class="sezione">Informazioni Amministrative collegate</div></td>
			</tr>
			<tr>
				<td class="etichetta2">Cpi competentee&nbsp;</td>
				<td class="campo" colspan="2">
					<%
						String strCpI = "";
							if (codCPI != null ) 
								strCpI += codCPI;
							if (descCPI != null)
								strCpI += " - " + descCPI; %> 
						<af:comboBox name="codCPI" multiple="false"
                        moduleName="COMBO_ENTETIT" disabled="true"
                        classNameBase="input"
                        selectedValue="<%=Utils.notNull(codCPI)%>"  />
				</td>
				<td></td>
			</tr>
			<tr>
				<td class="etichetta2">Data Invio &nbsp;</td>
				<td class="campo" colspan="2"><af:textBox classNameBase="input"
						type="date" name="datDichiarazione" title="Data disoccupazione"
						value="<%=datInvio%>" readonly="true"  size="12" maxlength="10" /></td>
				<td></td>
			</tr>

			<tr>
				<td colspan="4"><div class="sezione"></div></td>
			</tr>
			<tr>
				<td class="etichetta2">Data dichiarazione &nbsp;</td>
				<td class="campo" colspan="2"><af:textBox classNameBase="input"
						type="date" name="datDichiarazione"
						title="Data inserimento nell'elenco anagrafico"
						value="<%=datDichiarazione%>" 
						validateOnPost="true" readonly="true" size="12" maxlength="10" /></td>
				<td></td>
			</tr>

			<tr>
				<td class="etichetta2">Tipo Operazione &nbsp;</td>
				<td class="campo" colspan="3"><af:textBox classNameBase="input"
						name="tipoOp" title="prg" readonly="true" 
						value="<%=CodMonoTipoOp%>" /></td>
			</tr>

			<tr>
				<td class="etichetta2">Data disoccupazione &nbsp;</td>
				<td class="campo" colspan="2"><af:textBox classNameBase="input"
						type="date" name="datDichiarazione" title="Data disoccupazione"
						value="<%=datInizioDisocupaz%>" readonly="true" size="12"
						maxlength="10" /></td>
				<td></td>
			</tr>
			<tr>
				<td class="etichetta2">Data inizio Att. Sub. &nbsp;</td>
				<td class="campo" colspan="2"><af:textBox classNameBase="input"
						type="date" name="datDichiarazione" title="Data disoccupazione"
						value="<%=datInizioAttSub%>" readonly="true" size="12"
						maxlength="10" /></td>
				<td></td>
			</tr>
			<tr>
				<td class="etichetta2">Data ricezione &nbsp;</td>
				<td class="campo" colspan="2"><af:textBox classNameBase="input"
						type="date" name="datDichiarazione" title="Data ricezione"
						value="<%=datRicezione%>" readonly="true"  size="12" maxlength="10" /></td>
				<td></td>
			</tr>
			<tr>
				<td class="etichetta2">Reddito lavoro</td>
				<td class="campo" colspan="3"><af:textBox classNameBase="input"
						name="tipoOp" title="prg" readonly="true"
						value="<%=DecRedditoLavoro%>" /></td>
			</tr>
			<tr>
				<td class="etichetta2">Reddito previsto</td>
				<td class="campo" colspan="3"><af:textBox classNameBase="input"
						name="tipoOp" title="prg" readonly="true"
						value="<%=DecRedditoPrevisto%>" /></td>
			</tr>
			<tr>
				<td class="etichetta2">Codice fiscale azienda</td>
				<td class="campo" colspan="3"><af:textBox classNameBase="input"
						name="tipoOp" title="prg" readonly="true"
						value="<%=strAzCodFiscale%>" /></td>
			</tr>
			<tr>
				<td class="etichetta2">Denominazione</td>
				<td class="campo" colspan="3"><af:textBox classNameBase="input"
						name="tipoOp" size="60" maxlength="20" title="prg" readonly="true"
						value="<%=strDenominazione%>" /></td>
			</tr>
			<tr>
				<td class="etichetta2">Email</td>
				<td class="campo" colspan="3"><af:textBox classNameBase="input" size="60" maxlength="30" 
						name="tipoOp" title="prg" readonly="true" value="<%=strEmail%>" /></td>
			</tr>
			<tr>
				<td class="etichetta2">Telefono</td>
				<td class="campo" colspan="3"><af:textBox classNameBase="input" size="60" maxlength="30" 
						name="tipoOp" title="prg" readonly="true" value="<%=strTelefono%>" /></td>
			</tr>
			<tr>
				<td class="etichetta2">Cellulare</td>
				<td class="campo" colspan="3"><af:textBox classNameBase="input"
						name="tipoOp" title="prg" readonly="true"
						value="<%=strCellulare%>" /></td>
			</tr>
			<tr>
				<td class="etichetta2">Codice fiscale lavoratore</td>
				<td class="campo" colspan="3"><af:textBox classNameBase="input"
						name="tipoOp" title="prg" readonly="true" 
						value="<%=strCodFiscLav%>" /></td>
			</tr>
			<tr>
				<td class="etichetta2">Ultima Qualifica</td>
				<td class="campo" colspan="3"><af:textBox classNameBase="input"
						name="tipoOp" title="prg" readonly="true"
						value="<%=strUltimaQualifica%>" /></td>
			</tr>
			<tr>
				<td colspan="4"><div class="sezione"></div></td>
			</tr>
			<tr>
				<td class="etichetta2">Prot. INPS</td>
				<td class="campo" colspan="3">
				<af:textBox classNameBase="input"
						name="protInps" title="Protocollo INPS" readonly="true"
						 size="60" maxlength="30" 
						value="<%=strProtocolloInps%>" /></td>
			</tr>
			<tr>
				<td class="etichetta2">Comunicazione</td>
				<td class="campo" colspan="3">
				<af:textBox classNameBase="input" 
						size="60" maxlength="20" name="strcom" 
						readonly="true"
						value="<%=strComunicazione%>" /></td>
			</tr>
		</table>
		<BR />
		<center>
			<% operatoreInfo.showHTML(out); %>
		</center>
		<%
			out.print(htmlStreamBottom);
		%>
		 <center>
        <input class="pulsante" type = "button" name="Chiudi" value="Chiudi finestra" onclick="self.close()"/>
    </center>	
	</af:form>

</body>
</html>
