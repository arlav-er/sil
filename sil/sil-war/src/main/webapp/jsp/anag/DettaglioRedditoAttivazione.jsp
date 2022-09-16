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
	String _page = (String) serviceRequest.getAttribute("PAGE");
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	
	PageAttribs attributi = new PageAttribs(user, _page);

	boolean rdOnly = true;
	if (!filter.canView()) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	} else {
		rdOnly = !attributi.containsButton("AGGIORNA");
	}
	boolean rdOnlyCodStatoRa = true;
	String visDatRiferimento = "none";
	
	SourceBean rowRA = (SourceBean) serviceResponse.getAttribute("M_GetDettaglioRA.ROWS.ROW");
	
	String cdnLavoratore = SourceBeanUtils.getAttrStrNotNull(rowRA, "CDNLAVORATORE");
	
	String codStatoRa = SourceBeanUtils.getAttrStrNotNull(rowRA, "CODSTATORA");
	if ((codStatoRa.equals("DAV") || codStatoRa.equals("AUT") || codStatoRa.equals("NON")) && !rdOnly) {
		rdOnlyCodStatoRa = false;
	}
	if (codStatoRa.equals("DEC")) {
		rdOnly = true;
		visDatRiferimento = "";
	}
	String nomeFileRa = SourceBeanUtils.getAttrStrNotNull(rowRA, "nomeFileRa");
	String datOraControllo = SourceBeanUtils.getAttrStrNotNull(rowRA, "DATORACONTROLLO");
	String codVerifica = SourceBeanUtils.getAttrStrNotNull(rowRA, "CODVERIFICA");
	String flgEstraiDati = SourceBeanUtils.getAttrStrNotNull(rowRA, "FLGESTRAIDATI");
	String datDomanda = SourceBeanUtils.getAttrStrNotNull(rowRA, "DATDOMANDA");
	String codEnteAutonomo = SourceBeanUtils.getAttrStrNotNull(rowRA, "STRCODENTEAUTONOMO");
	String strEnteAutonomo = SourceBeanUtils.getAttrStrNotNull(rowRA, "STRENTEAUTONOMO");
	String strMatrInps = SourceBeanUtils.getAttrStrNotNull(rowRA, "STRMATRICOLAINPS");
	String strCFAzienda = SourceBeanUtils.getAttrStrNotNull(rowRA, "STRCFAZIENDA");
	String strRagSocAzienda = SourceBeanUtils.getAttrStrNotNull(rowRA, "STRRAGSOCAZIENDA");
	String datCessAttivita = SourceBeanUtils.getAttrStrNotNull(rowRA, "DATACESSATTIVITA");
	String strNumDelibera = SourceBeanUtils.getAttrStrNotNull(rowRA, "STRNUMDELIBERA");
	String numAnnoDelibera = SourceBeanUtils.getAttrStrNotNull(rowRA, "NUMANNODELIBERA");
	String datEsitoDelibera = SourceBeanUtils.getAttrStrNotNull(rowRA, "DATESITODELIBERA");
	String datInizPrestazione = SourceBeanUtils.getAttrStrNotNull(rowRA, "DATINIZIOPRESTAZIONE");
	String datFinePrestazione = SourceBeanUtils.getAttrStrNotNull(rowRA, "DATFINEPRESTAZIONE");
	String numDurataPrestazione = SourceBeanUtils.getAttrStrNotNull(rowRA, "NUMDURATAPRESTAZIONE");
	String strTipoPrestazione = SourceBeanUtils.getAttrStrNotNull(rowRA, "STRTIPOPRESTAZIONE");
	String strCodiceFiscale = SourceBeanUtils.getAttrStrNotNull(rowRA, "STRCODICEFISCALE");
	String strCognome = SourceBeanUtils.getAttrStrNotNull(rowRA, "STRCOGNOME");
	String strNome = SourceBeanUtils.getAttrStrNotNull(rowRA, "STRNOME");
	String datNascita = SourceBeanUtils.getAttrStrNotNull(rowRA, "DATNASCITA");
	String strComNascita = SourceBeanUtils.getAttrStrNotNull(rowRA, "STRCOMNASCITA");
	String strIndirizzo = SourceBeanUtils.getAttrStrNotNull(rowRA, "STRINDIRIZZO");
	String strComune = SourceBeanUtils.getAttrStrNotNull(rowRA, "STRCOMUNE");
	String strCap = SourceBeanUtils.getAttrStrNotNull(rowRA, "STRCAP");
	String strTelBenef = SourceBeanUtils.getAttrStrNotNull(rowRA, "STRTELBENEFICIARIO");
	String strEmailBenef = SourceBeanUtils.getAttrStrNotNull(rowRA, "STREMAILBENEFICIARIO");
	String strIbanNazione = SourceBeanUtils.getAttrStrNotNull(rowRA, "STRIBANNAZIONE");
	String strIbanControllo = SourceBeanUtils.getAttrStrNotNull(rowRA, "STRIBANCONTROLLO");
	String strCinLav = SourceBeanUtils.getAttrStrNotNull(rowRA, "STRCINLAVORATORE");
	String strAbiLav = SourceBeanUtils.getAttrStrNotNull(rowRA, "STRABILAVORATORE");
	String strCabLav = SourceBeanUtils.getAttrStrNotNull(rowRA, "STRCABLAVORATORE");
	String strCCLav = SourceBeanUtils.getAttrStrNotNull(rowRA, "STRCCLAVORATORE");
	String decImpGiornaliero = SourceBeanUtils.getAttrStrNotNull(rowRA, "DECIMPORTOLORDOGIORNALIERO");
	String decImpComplessivo = SourceBeanUtils.getAttrStrNotNull(rowRA, "DECIMPORTOLORDOCOMPLESSIVO");
	String datRiferimento = SourceBeanUtils.getAttrStrNotNull(rowRA, "DATRIFERIMENTO");
	String codProvenienza = SourceBeanUtils.getAttrStrNotNull(rowRA, "CODPROVENIENZA");
	String cdnUtIns = SourceBeanUtils.getAttrStrNotNull(rowRA, "CDNUTINS");
	String dtmIns = SourceBeanUtils.getAttrStrNotNull(rowRA, "DTMINS");
	String cdnUtMod = SourceBeanUtils.getAttrStrNotNull(rowRA, "CDNUTMOD");
	String dtmMod = SourceBeanUtils.getAttrStrNotNull(rowRA, "DTMMOD");	

	Testata operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);

	String cdnFunzione = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnFunzione");
	String prgRedditoAttivazione = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "prgRedditoAttivazione");
	String htmlStreamTop = StyleUtils.roundTopTable(!rdOnly);
	String htmlStreamBottom = StyleUtils.roundBottomTable(!rdOnly);
%>

<html>
<head>
<title>Dettaglio Reddito Attivazione</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>
<af:linkScript path="../../js/" />

<script language="Javascript">
var flagChanged = false; 
var Array_Stati_Con_Motivi = ["DAV","NON","DEC"];

function fieldChanged() {
  <% if (!rdOnly) { out.print("flagChanged = true;");}%>
}
	
<%if(cdnLavoratore.equals("")){%>
function tornaAllaLista()
{   
	<%
	String url = (String )sessionContainer.getAttribute("_BACKURL_");
	%>
    // Solo con CDNLAV nullo, ovvero quando non trovo in anagrafica
    if (isInSubmit()) return;
    url="AdapterHTTP?"+"<%=url%>";
    setWindowLocation(url);
}
<%}%>

function checkSoloLettere(obj) {
    var regexp = /^[a-zA-Z]+$/;
    if (!regexp.test(obj.value))
    {
        alert("Nel campo " + obj.title + " sono consentiti solo caratteri alfabetici");
        return false;
    }
    else return true;
}

function include(arr, obj) {
    for(var i=0; i<arr.length; i++) {
        if (arr[i] == obj) return true;
    }
}

function upperInnerTextcampoSmall(obj) {
	obj.value=obj.value.toUpperCase();
}

function toggleValorizzaFlgEstraiDati(obj) {
	<%if (codStatoRa.equals("DAV")) {%>
	if (obj.value == "AUT") document.Frm1.flgEstraiDati.value = 'S';
	<%}%>
}

function toggleVisMotVerifica(){		  
	var divMotVerifica = document.getElementById("divMotVerifica");
	if (include(Array_Stati_Con_Motivi, document.Frm1.codStatoRa.value)) {
		divMotVerifica.style.display='';
	} else {
		divMotVerifica.style.display='none';
	}
}

function ControllaDati(){

	if (document.Frm1.strIbanNazione.value != '') {
		if (!checkSoloLettere(document.Frm1.strIbanNazione)) {
			return false;
		}
	}

	if (document.Frm1.strCinLav.value != '') {
		if (!checkSoloLettere(document.Frm1.strCinLav)) {
			return false;
		}
	}
	
	if (!controllaFixedFloat('decImpGiornaliero', 20, 3)) {			
		return false;
	} 

	if (!controllaFixedFloat('decImpComplessivo', 20, 3)) {			
		return false;
	}

	if (!include(Array_Stati_Con_Motivi, document.Frm1.codStatoRa.value)) {
		document.Frm1.codVerifica.value = '';
	} 

	return true;
}
</script>
</head>

<body class="gestione" onLoad="toggleVisMotVerifica();">
	<%if(!cdnLavoratore.equals("")){%>
	<script language="Javascript">
		window.top.menu.caricaMenuLav(<%=cdnFunzione%>,<%=cdnLavoratore%>);
	</script>

	<%
		InfCorrentiLav testata = new InfCorrentiLav(RequestContainer
				.getRequestContainer().getSessionContainer(),
				cdnLavoratore, user);
		testata.show(out);
	}
	%>
	<font color="green">
		<af:showMessages prefix="M_UpdDettaglioRA"/>
	</font>
	<font color="red">
	     <af:showErrors/>
	</font>

	<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="ControllaDati()">
		<p class="titolo">Reddito Attivazione</p>
		<% out.print(htmlStreamTop); %>
		<table class="main">
			<tr>
				<td class="etichettaSmall">Stato</td>
				<td class="campoSmall">
					<af:comboBox name="codStatoRa" multiple="false" moduleName="M_GetStatoRA" 
					disabled="<%=String.valueOf(rdOnlyCodStatoRa)%>" onChange="fieldChanged();toggleValorizzaFlgEstraiDati(this);toggleVisMotVerifica();" classNameBase="input" selectedValue="<%=codStatoRa%>"/>
				</td>  
				<td colspan="<%if (visDatRiferimento.equals("")) {%>2<%}else{%>4<%}%>">		
				<div id="divMotVerifica" style="display:none">
					<table>
						<tr>
							<td style="width:14%" class="etichettaSmall" nowrap>Motivo</td>
							<td class="campoSmall">
								<af:comboBox name="codVerifica" multiple="false" moduleName="M_GetMotVerificaRA" title="Motivo Verifica"
								disabled="<%=String.valueOf(rdOnlyCodStatoRa)%>" onChange="fieldChanged()" classNameBase="input" selectedValue="<%=codVerifica%>"/>
							</td>
						</tr>
					</table>
				</div>
				</td>
				<%if (visDatRiferimento.equals("")) {%>
				<td style="width:14%" class="etichettaSmall" nowrap>Data decadenza</td>
				<td class="campoSmall">
					<af:textBox classNameBase="input" type="date" name="datRiferimento" value="<%=datRiferimento%>" readonly="true" size="12" maxlength="10" />
				</td>	
				<%}%>											
			</tr>					
			<tr>
			    <td class="etichettaSmall" nowrap>Nome file</td>
   				<td class="campoSmall">
   					<af:textBox classNameBase="input" type="text" name="nomeFileRa" value="<%=nomeFileRa%>" size="30" maxlength="30" readonly="true"/>
   				</td>
				<td class="etichettaSmall" nowrap>Data e ora controllo</td>
				<td class="campoSmall">
					<af:textBox classNameBase="input" type="date" name="datOraControllo" value="<%=datOraControllo%>" readonly="true" size="18" maxlength="16" />
				</td>	
			    <td class="etichettaSmall" nowrap>Da esportare</td>
			    <td class="campoSmall">
			    	<%
			    	if (rdOnly) {
			    			String txtFlgEst = "";
			    			if ("S".equalsIgnoreCase(flgEstraiDati)) {
			    				txtFlgEst = "S&igrave;";
			    			}
			    			else {
			    				txtFlgEst = "No";
			    			}
			    	%>
			    		<af:textBox classNameBase="input" type="text" name="xx_flgEstraiDati" value="<%=txtFlgEst%>" size="5" maxlength="5" readonly="true"/>
			    	<%
			    	}
			    	else { 
			    	%>
			    	<af:comboBox classNameBase="input" name="flgEstraiDati" onChange="fieldChanged();" >				        
				        <option value="S" <% if ( "S".equalsIgnoreCase(flgEstraiDati) ) { %>SELECTED="true"<% } %> >S&igrave;</option>
				        <option value="N" <% if ( "N".equalsIgnoreCase(flgEstraiDati) ) { %>SELECTED="true"<% } %> >No</option>
			        </af:comboBox>
			        <%
			        }
			        %>
			    </td>
			</tr>
			<tr>
				<td class="etichettaSmall" nowrap>Data domanda</td>
				<td class="campoSmall" colspan="2">
					<af:textBox classNameBase="input" type="date" name="datDomanda" value="<%=datDomanda%>" readonly="true" size="12" maxlength="10" />
				</td>
		    	<td class="etichettaSmall">Provenienza</td>
		      	<td class="campoSmall" colspan="2">
		        	<af:comboBox classNameBase="input" name="codProvenienza" disabled="true">
		            	<OPTION value="F" <%if (codProvenienza.equalsIgnoreCase("F")) out.print("SELECTED=\"true\"");%>>Da File</OPTION>
		            	<OPTION value="M" <%if (codProvenienza.equalsIgnoreCase("M")) out.print("SELECTED=\"true\"");%>>Agg. manuale</OPTION>
		          	</af:comboBox>
		     	</td>			
			</tr>	
			<tr>	
			    <td class="etichettaSmall" nowrap>Codice ente</td>
   				<td class="campoSmall" colspan="2">
   					<af:textBox classNameBase="input" type="text" name="codEnteAutonomo" value="<%=codEnteAutonomo%>" onKeyUp="fieldChanged();" size="10" maxlength="30" readonly="<%= String.valueOf(rdOnly) %>"/>
   				</td>
 			    <td class="etichettaSmall">Ente</td>
   				<td class="campoSmall" colspan="2">
   					<af:textBox classNameBase="input" type="text" name="strEnteAutonomo" value="<%=strEnteAutonomo%>" onKeyUp="fieldChanged();" size="35" maxlength="30" readonly="<%= String.valueOf(rdOnly) %>"/>
   				</td>  				
			</tr>		
			<tr>
				<td colspan="6" align="center">&nbsp;</td>
			</tr>	
			<tr>
				<td colspan="6"><div class="sezione2">Azienda</div></td>
			</tr>
			<tr>
				<td class="etichettaSmall" nowrap>CF Azienda</td>
   				<td class="campoSmall" colspan="2">
   					<af:textBox classNameBase="input" type="text" name="strCFAzienda" value="<%=strCFAzienda%>" size="20" maxlength="16" readonly="true" />
   				</td>
 			    <td class="etichettaSmall" nowrap>Ragione Sociale</td>
   				<td class="campoSmall" colspan="2">
   					<af:textBox classNameBase="input" type="text" name="strRagSocAzienda" value="<%=strRagSocAzienda%>" size="30" maxlength="100" readonly="true" />
   				</td>  				
			</tr>						
			<tr>
				<td class="etichettaSmall" nowrap>Matricola INPS</td>
				<td class="campoSmall" colspan="2">
					<af:textBox classNameBase="input" type="text" name="strMatrInps" value="<%=strMatrInps%>" size="30" maxlength="30" readonly="true" />
				</td>
				<td class="etichettaSmall" nowrap>Data cessazione attivit&agrave;</td>
				<td class="campoSmall" colspan="2">
					<af:textBox classNameBase="input" type="date" name="datCessAttivita" value="<%=datCessAttivita%>" readonly="true" size="12" maxlength="10" />
				</td>	
			</tr>					
			<tr>
				<td colspan="6" align="center">&nbsp;</td>
			</tr>	
			<tr>
				<td colspan="6"><div class="sezione2">Delibera</div></td>
			</tr>
			<tr>
			    <td class="etichettaSmall">Numero</td>
   				<td class="campoSmall">
   					<af:textBox classNameBase="input" type="text" name="strNumDelibera" value="<%=strNumDelibera%>" onKeyUp="fieldChanged();" size="30" maxlength="30" readonly="<%= String.valueOf(rdOnly) %>"/>
   				</td>			
				<td class="etichettaSmall" nowrap>Data</td>
				<td class="campoSmall">
					<af:textBox classNameBase="input" type="date" validateOnPost="true" title="Data" name="datEsitoDelibera" value="<%=datEsitoDelibera%>" onKeyUp="fieldChanged();" size="12" maxlength="10" readonly="<%= String.valueOf(rdOnly) %>"/>
				</td>
 			    <td class="etichettaSmall">Anno</td>
   				<td class="campoSmall">
   					<af:textBox classNameBase="input" type="integer" validateOnPost="true" title="Anno" name="numAnnoDelibera" value="<%=numAnnoDelibera%>" onKeyUp="fieldChanged();" size="6" maxlength="4" readonly="<%= String.valueOf(rdOnly) %>"/>
   				</td>  				
			</tr>			
			<tr>
				<td colspan="6" align="center">&nbsp;</td>
			</tr>
			<tr>
				<td colspan="6"><div class="sezione2">Prestazione</div></td>
			</tr>	
			<tr>
				<td class="etichettaSmall" nowrap>Data inizio</td>
				<td class="campoSmall" colspan="2">
					<af:textBox classNameBase="input" type="date" name="datInizPrestazione" value="<%=datInizPrestazione%>" readonly="true" size="12" maxlength="10" />
				</td>
				<td class="etichettaSmall" nowrap>Data fine</td>
				<td class="campoSmall" colspan="2">
					<af:textBox classNameBase="input" type="date" name="datFinePrestazione" value="<%=datFinePrestazione%>" readonly="true" size="12" maxlength="10" />
				</td>	
			</tr>	
			<tr>							
			    <td class="etichettaSmall">Durata</td>
   				<td class="campoSmall" colspan="2">
   					<af:textBox classNameBase="input" type="text" name="numDurataPrestazione" value="<%=numDurataPrestazione%>" size="6" maxlength="4" readonly="true"/>
   				</td>
 			    <td class="etichettaSmall">Tipologia</td>
   				<td class="campoSmall" colspan="2">
   					<af:textBox classNameBase="input" type="text" name="strTipoPrestazione" value="<%=strTipoPrestazione%>" size="30" maxlength="30" readonly="true"/>
   				</td>  				
			</tr>	
			<tr>
				<td colspan="6" align="center">&nbsp;</td>
			</tr>
			<tr>
				<td colspan="6"><div class="sezione2">Lavoratore</div></td>
			</tr>						
			<tr>
			    <td class="etichettaSmall" nowrap>Codice Fiscale</td>
   				<td class="campoSmall">
   					<af:textBox classNameBase="input" type="text" name="strCodiceFiscale" value="<%=strCodiceFiscale%>" size="20" maxlength="16" readonly="true" />
   				</td>
 			    <td class="etichettaSmall">Cognome</td>
   				<td class="campoSmall">
   					<af:textBox classNameBase="input" type="text" name="strCognome" value="<%=strCognome%>" size="30" maxlength="40" readonly="true" />
   				</td>  
 			    <td class="etichettaSmall">Nome</td>
   				<td class="campoSmall">
   					<af:textBox classNameBase="input" type="text" name="strNome" value="<%=strNome%>" size="30" maxlength="40" readonly="true" />
   				</td>   								
			</tr>	
			<tr>
				<td class="etichettaSmall" nowrap>Data di Nascita</td>
				<td class="campoSmall" colspan="2">
					<af:textBox classNameBase="input" type="date" name="datNascita" value="<%=datNascita%>" readonly="true" size="12" maxlength="10" />
				</td>	
 			    <td class="etichettaSmall" nowrap>Comune di Nascita</td>
   				<td class="campoSmall" colspan="2">
   					<af:textBox classNameBase="input" type="text" name="strComNascita" value="<%=strComNascita%>" size="30" maxlength="50" readonly="true" />
   				</td>   								
			</tr>	
			<tr>
			    <td class="etichettaSmall">Indirizzo</td>
   				<td class="campoSmall">
   					<af:textBox classNameBase="input" type="text" name="strIndirizzo" value="<%=strIndirizzo%>" size="30" maxlength="60" readonly="true" />
   				</td>
 			    <td class="etichettaSmall">Comune</td>
   				<td class="campoSmall">
   					<af:textBox classNameBase="input" type="text" name="strComune" value="<%=strComune%>" size="30" maxlength="50" readonly="true" />
   				</td>  
 			    <td class="etichettaSmall">CAP</td>
   				<td class="campoSmall">
   					<af:textBox classNameBase="input" type="text" name="strCap" value="<%=strCap%>" size="6" maxlength="5" readonly="true" />
   				</td>   								
			</tr>	
			<tr>
 			    <td class="etichettaSmall">Telefono</td>
   				<td class="campoSmall" colspan="2">
   					<af:textBox classNameBase="input" type="text" name="strTelBenef" value="<%=strTelBenef%>" size="30" maxlength="30" readonly="true" />
   				</td>  
 			    <td class="etichettaSmall">Email</td>
   				<td class="campoSmall" colspan="2">
   					<af:textBox classNameBase="input" type="text" name="strEmailBenef" value="<%=strEmailBenef%>" size="30" maxlength="80" readonly="true" />
   				</td>   								
			</tr>	
			<tr>
				<td colspan="6" align="center">&nbsp;</td>
			</tr>
			<tr>
				<td colspan="6"><div class="sezione2">Dati Bancari</div></td>
			</tr>						
			<tr>
			    <td class="etichettaSmall" nowrap>Codice nazione</td>
   				<td class="campoSmall">
   					<af:textBox classNameBase="input" title="Codice nazione" type="text" name="strIbanNazione" value="<%=strIbanNazione%>" onKeyUp="fieldChanged();upperInnerTextcampoSmall(this);" size="3" maxlength="2" readonly="<%= String.valueOf(rdOnly) %>"/>
   				</td>			
			    <td class="etichettaSmall" nowrap>Codice controllo</td>
   				<td class="campoSmall">
   					<af:textBox classNameBase="input" title="Codice controllo" validateOnPost="true" type="integer" name="strIbanControllo" value="<%=strIbanControllo%>" onKeyUp="fieldChanged();" size="3" maxlength="2" readonly="<%= String.valueOf(rdOnly) %>"/>
   				</td>
			    <td class="etichettaSmall">CIN</td>
   				<td class="campoSmall">
   					<af:textBox classNameBase="input" title="CIN" type="text" name="strCinLav" value="<%=strCinLav%>" onKeyUp="fieldChanged();upperInnerTextcampoSmall(this);" size="2" maxlength="1" readonly="<%= String.valueOf(rdOnly) %>"/>
   				</td>   				 				
			</tr>						
			<tr>
			    <td class="etichettaSmall">ABI</td>
   				<td class="campoSmall">
   					<af:textBox classNameBase="input" title="ABI" validateOnPost="true" type="integer" name="strAbiLav" value="<%=strAbiLav%>" onKeyUp="fieldChanged();" size="6" maxlength="5" readonly="<%= String.valueOf(rdOnly) %>"/>
   				</td>			
			    <td class="etichettaSmall">CAB</td>
   				<td class="campoSmall">
   					<af:textBox classNameBase="input" title="CAB" validateOnPost="true" type="integer" name="strCabLav" value="<%=strCabLav%>" onKeyUp="fieldChanged();" size="6" maxlength="5" readonly="<%= String.valueOf(rdOnly) %>"/>
   				</td>
			    <td class="etichettaSmall">CC</td>
   				<td class="campoSmall">
   					<af:textBox classNameBase="input" type="text" name="strCCLav" value="<%=strCCLav%>" onKeyUp="fieldChanged();upperInnerTextcampoSmall(this);" size="15" maxlength="12" readonly="<%= String.valueOf(rdOnly) %>"/>
   				</td>   				 				
			</tr>	
			<tr>
 			    <td class="etichettaSmall" nowrap>Importo giornaliero</td>
   				<td class="campoSmall" colspan="2">
   					<af:textBox classNameBase="input" type="number" name="decImpGiornaliero" title="Importo giornaliero" value="<%=decImpGiornaliero%>" onKeyUp="fieldChanged();" size="30" maxlength="20" readonly="<%= String.valueOf(rdOnly) %>"/>
   				</td>
 			    <td class="etichettaSmall" nowrap>Importo complessivo</td>
   				<td class="campoSmall" colspan="2">
   					<af:textBox classNameBase="input" type="number" name="decImpComplessivo" title="Importo complessivo" value="<%=decImpComplessivo%>" onKeyUp="fieldChanged();" size="30" maxlength="20" readonly="<%= String.valueOf(rdOnly) %>"/>
   				</td>  				
			</tr>
			<tr>
				<td colspan="6" align="center">&nbsp;</td>
			</tr>			
			<tr>
				<td colspan="6" class="campo_read">
					Nota sull'IBAN - Se l'IBAN non viene indicato il pagamento avverr&agrave; a mezzo Bonifico domiciliato; 
					se si vuole l'accredito bancario &egrave; necessario indicare l'iban (anche di un parente prossimo). 
					Si rammenta che, ai sensi della normativa vigente, per importi uguali o superiori a &euro; 1000,00 (mille), 
					si render&agrave; comunque necessario comunicare un codice iban.
				</td>
			</tr>
			<%if(!rdOnly){%>								
			<tr>
				<td colspan="6" align="center">&nbsp;</td>
			</tr>			
			<tr>
				<td colspan="6" align="center">
      				<input class="pulsante" type="submit" name="salva" value="Aggiorna">
      				<input type="hidden" name="PAGE" value="<%=_page%>">
      				<input type="hidden" name="MODULE" value="M_UpdDettaglioRA">
      				<input type="hidden" name="cdnFunzione"	value="<%=cdnFunzione%>">
      				<input type="hidden" name="prgRedditoAttivazione" value="<%=prgRedditoAttivazione%>">
      				<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>">
      				&nbsp;&nbsp;&nbsp;
					<input type="reset" name="reset" class="pulsanti" value="Annulla"/>
				</td>
			</tr>
			<%}%>		
			<%if(cdnLavoratore.equals("")){%>
			<tr width="98%">
				<td colspan="6" align="center">
      				<input class="pulsante" type = "button" name="torna" value="Torna alla lista" onclick="tornaAllaLista()"/>
				</td>
			</tr>			
			<%}%>	
			<tr>
				<td colspan="6" align="center">&nbsp;</td>
			</tr>													
		</table>
		<%
			out.print(htmlStreamBottom);
		%>	
		<center>
			<% operatoreInfo.showHTML(out); %>
		</center>	
	</af:form>

</body>
</html>
