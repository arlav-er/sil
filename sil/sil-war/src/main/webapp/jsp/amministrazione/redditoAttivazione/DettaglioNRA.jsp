<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../../global/noCaching.inc"%>
<%@ include file="../../global/Function_CommonRicercaComune.inc" %>
<%@ include file="../../global/getCommonObjects.inc"%>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  it.eng.sil.module.amministrazione.redditoAttivazione.Decodifica.EsitoElaborazioneDomanda,
                  it.eng.sil.module.amministrazione.redditoAttivazione.Decodifica.StatoDomanda,
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
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%
	String _page = (String) serviceRequest.getAttribute("PAGE");
	ProfileDataFilter filter = new ProfileDataFilter(user,"DettaglioNRAPage");
	PageAttribs attributi = new PageAttribs(user, _page);

	boolean readOnly = false;
	boolean canAggiorna = false;
	boolean canInvia = false;
	boolean canView = filter.canViewLavoratore();
	if (!canView) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	} else {
		canAggiorna = attributi.containsButton("AGGIORNA");
		canInvia = attributi.containsButton("INVIA");
		
 
  	String token = "_TOKEN_" + "ListaNRAPage";
  	String urlDiLista = (String) sessionContainer.getAttribute(token.toUpperCase());
  
	
	
	String img1 = "../../img/chiuso.gif";
	
	String cdnLavoratore = null;
	String prgnuovora = null;
	String codiceFiscale = null;
	String dataNascita = null;
	String idDomandaIntranet = null;
	String idDomandaWeb = null;
	String cognomeNome = null;
	String cognome = null;
	String nome = null;
	String comuneNascita = null;
	String codiceCatastoNascita = null;
	String indirizzoINPS = null;
	String indirizzoSIL = null;
	String codiceComuneINPS = null;
	String codiceComuneSIL = null;
	String descrizioneComuneINPS = null;
	String descrizioneComuneSIL = null;
	String capResidenzaINPS = null;
	String capResidenzaSIL = null;
	String codiceProvinciaSIL = null;
	String codMonotipoDomanda = null;
	String datVariazioneRes = null;
	String cdnUtIns = null;
	String dtmIns = null;
	String cdnUtMod = null;
	String dtmMod = null;
	String NUMKLONUOVORA = null;
	String flgInviataVarResidenza = "";
	
	SourceBean rowResidenzaINPS = null;

	Testata operatoreInfo = null;
	
	//Ottiene le componenti della risposta 
	SourceBean row = (SourceBean) serviceResponse
			.getAttribute("M_GETDETTAGLIONRA.NUOVORA.ROWS.ROW");
	
	SourceBean rowResidenzaSIL = (SourceBean) serviceResponse
			.getAttribute("M_GETDETTAGLIONRA.RESIDENZASIL.ROWS.ROW");
	
	if(serviceResponse.containsAttribute("M_GET_RESIDENZAINPS_NUOVA")){
		rowResidenzaINPS = (SourceBean) serviceResponse
				.getAttribute("M_GET_RESIDENZAINPS_NUOVA.ROWS.ROW");
	}
	if(serviceResponse.containsAttribute("M_GET_RESIDENZAINPS_SUC")){
		rowResidenzaINPS = (SourceBean) serviceResponse
				.getAttribute("M_GET_RESIDENZAINPS_SUC.ROWS.ROW");
	}

	//Valorizza i campi in base al contenuto della risposta
	if (row.containsAttribute("CDNLAVORATORE")) {
		cdnLavoratore = row.getAttribute("CDNLAVORATORE").toString();
		filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	}
	if (row.containsAttribute("PRGNUOVORA")) {
		prgnuovora = row.getAttribute("PRGNUOVORA").toString();
	}
	if (row.containsAttribute("STRCODICEFISCALE")) {
		codiceFiscale = row.getAttribute("STRCODICEFISCALE").toString();
	}
	if (row.containsAttribute("cognome")) {
		cognome = row.getAttribute("cognome").toString();
	}
	if (row.containsAttribute("nome")) {
		nome = row.getAttribute("nome").toString();
	}
	cognomeNome= cognome + " " + nome;
	if (row.containsAttribute("DATANASCITA")) {
		dataNascita = row.getAttribute("DATANASCITA").toString();
	}
	if (row.containsAttribute("CODICECATASTONASCITA")) {
		codiceCatastoNascita = row.getAttribute("CODICECATASTONASCITA").toString();
	}
	if (row.containsAttribute("COMUNENASCITA")) {
		comuneNascita = row.getAttribute("COMUNENASCITA").toString();
	}
	if (row.containsAttribute("IDDOMANDAINTRANET")) {
		idDomandaIntranet = row.getAttribute("IDDOMANDAINTRANET").toString();
	}
	if (row.containsAttribute("IDDOMANDAWEB")) {
		idDomandaWeb = row.getAttribute("IDDOMANDAWEB").toString();
	}
	if (row.containsAttribute("CODMONOTIPODOMANDA")) {
		codMonotipoDomanda = row.getAttribute("CODMONOTIPODOMANDA").toString();
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
	if (row.containsAttribute("NUMKLONUOVORA")) {
		NUMKLONUOVORA = row.getAttribute("NUMKLONUOVORA").toString();
	}
	if (row.containsAttribute("FLGINVIATA")) { 
		readOnly = row.getAttribute("FLGINVIATA").toString().equals("S");
	}
	if (row.containsAttribute("DATVARIAZIONERES")) { 
		datVariazioneRes = row.getAttribute("DATVARIAZIONERES").toString();
	}
	if (row.containsAttribute("FLGINVIATAVARRESIDENZA")) {
		flgInviataVarResidenza = row.getAttribute("FLGINVIATAVARRESIDENZA").toString();
	}
	
	//Ottiene informazioni relative alla residenza prese dal SIL
	if(rowResidenzaSIL!=null){
		if (rowResidenzaSIL.containsAttribute("CODCOMRES")) {
			codiceComuneSIL = rowResidenzaSIL.getAttribute("CODCOMRES").toString();
		}
		if (rowResidenzaSIL.containsAttribute("CODPROVINCIA")) {
			codiceProvinciaSIL = rowResidenzaSIL.getAttribute("CODPROVINCIA").toString();
		}
		if (rowResidenzaSIL.containsAttribute("DESCRIZIONECOMUNE")) {
			descrizioneComuneSIL = rowResidenzaSIL.getAttribute("DESCRIZIONECOMUNE").toString();
		}
		if (rowResidenzaSIL.containsAttribute("STRCAPRES")) {
			capResidenzaSIL = rowResidenzaSIL.getAttribute("STRCAPRES").toString();
		}
		if (rowResidenzaSIL.containsAttribute("STRINDIRIZZORES")) {
			indirizzoSIL = rowResidenzaSIL.getAttribute("STRINDIRIZZORES").toString();
		}
	}
	
	//Ottiene informazioni sulla residenza da INPS
	if(rowResidenzaINPS!=null){
		if (rowResidenzaINPS.containsAttribute("CODICECOMUNE")) {
			codiceComuneINPS = rowResidenzaINPS.getAttribute("CODICECOMUNE").toString();
		}
		if (rowResidenzaINPS.containsAttribute("descrizionecomune")) {
			descrizioneComuneINPS = rowResidenzaINPS.getAttribute("descrizionecomune").toString();
		}
		if (rowResidenzaINPS.containsAttribute("CAP")) {
			capResidenzaINPS = rowResidenzaINPS.getAttribute("CAP").toString();
		}
		if (rowResidenzaINPS.containsAttribute("INDIRIZZO")) {
			indirizzoINPS = rowResidenzaINPS.getAttribute("INDIRIZZO").toString();
		}
	}
	Vector motivoComunicazioneVec = null;
	Vector tipoComunicazioneVec = null;
	Vector tipoEventoVec = null;
	SourceBean s1 = null;
	if(readOnly){
		motivoComunicazioneVec = serviceResponse.getAttributeAsVector("M_MOTIVO_COMUNICAZIONE.ROWS.ROW");
		tipoComunicazioneVec = serviceResponse.getAttributeAsVector("M_TIPO_COMUNICAZIONE.ROWS.ROW");
		tipoEventoVec = serviceResponse.getAttributeAsVector("M_TIPO_EVENTO.ROWS.ROW");
	}
	
	operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
	String cdnFunzione = (String) serviceRequest
			.getAttribute("cdnFunzione");
	
	//Gestione colore sfondo(true=blu false=azzurro)
	String htmlStreamTop = StyleUtils.roundTopTable(true);
	String htmlStreamBottom = StyleUtils.roundBottomTable(true);
%>

<html>
	<head>
		<title>Dettaglio Nuovo reddito di attivazione</title>
		<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
		<af:linkScript path="../../js/" />
	
		<script language="Javascript">
		<%@ include file="../../patto/_sezioneDinamica_script.inc"%>

			var STATO_DOMANDA_DA_ESAMINARE = "<%=StatoDomanda.DA_ESAMINARE%>";
			var ESITO_ELABORAZIONE_NON_VALIDATA = "<%=EsitoElaborazioneDomanda.DOMANDA_NON_VALIDATA%>";
		
			<%//Genera il Javascript che si occuperà  di inserire i links nel footer
			if (cdnLavoratore != null)
						attributi.showHyperLinks(out, requestContainer, responseContainer,
								"cdnLavoratore=" + cdnLavoratore);%>
			
			//Controllo compilazione campi per aggiornamento residenza
		    function checkeAggiornaResidenza()
		    {
		      if( (document.Frm1.datVariazioneRes.value != null) && (document.Frm1.datVariazioneRes.value != "")){
		    	  return true;
		      }
		      alert("Data variazione residenza non compilata."); 
		      return false;
		    }
		    
			//Controllo compilazione campi per aggiornamento dati nuova richiesta
		    function checkeAggiornaN(operazione){
		    	var counter = 0;
		    	var alertString ="Campi obbligatori non compilati:";
		    	if( (document.Frm1.numeroProvvedimento.value != null) && (document.Frm1.numeroProvvedimento.value != "")){
			   		counter++;
			    } else{
			    	alertString = alertString + " Numero Provvedimento";
			    }
		    	if( (document.Frm1.dataProvvedimento.value != null) && (document.Frm1.dataProvvedimento.value != "")){
		    		counter++;
		    	} else{
			    	alertString = alertString + " Data Provvedimento";
			    }
		    	if( (document.Frm1.esitoElaborazione.value != null) && (document.Frm1.esitoElaborazione.value != "")){
					counter++;
		    	} else{
			    	alertString = alertString + " Esito Elaborazione";
			    }
		    	if(document.Frm1.codStatoDomanda.value != ""){
					counter++;
		    	} else{
			    	alertString = alertString + " Stato Domanda";
			    }
		    	if(document.Frm1.flgAutorizzabile.value != ""){
		    		counter++;
		    	} else{
			    	alertString = alertString + " Autorizzabile";
			    }
		    	if(counter == 5){
		    		if( (document.Frm1.esitoElaborazione.value != ESITO_ELABORAZIONE_NON_VALIDATA) && (document.Frm1.codiceReiezione.value != "")) {
			    		alert("Non è possibile indicare il motivo di reiezione in caso di domanda validata");
			    		return false;
		    		}			    	
					if (operazione == "invia") {
						if(document.Frm1.codStatoDomanda.value == STATO_DOMANDA_DA_ESAMINARE) {
							alert("Non è possibile inviare una comunicazione di validazione domanda con stato domanda \"Da Esaminare\"");
							return false;
						}
						else {
							return true;
						}
					} else {
						return true;
					}
		    	} else
		    		alert(alertString);
		    	return false;
		    }
			
		    function checkeAggiornaS(operazione){
		    	var counter = 0;
		    	var alertString ="Campi obbligatori non compilati:";
		    	if( (document.Frm1.numeroProvvedimento.value != null) && (document.Frm1.numeroProvvedimento.value != "")){
			   		counter++;
			    } else{
			    	alertString = alertString + " Numero Provvedimento";
			    }
		    	if( (document.Frm1.dataProvvedimento.value != null) && (document.Frm1.dataProvvedimento.value != "")){
		    		counter++;
		    	} else{
			    	alertString = alertString + " Data Provvedimento";
			    }
		    	if( (document.Frm1.codTipoProv.value != null) && (document.Frm1.codTipoProv.value != "")){
					counter++;
		    	} else{
			    	alertString = alertString + " Tipo Provvedimento";
			    }
		    	if(document.Frm1.codStatoDomanda.value != ""){
					counter++;
		    	} else{
			    	alertString = alertString + " Stato Domanda";
			    }
		    	if(document.Frm1.flgAutorizzabile.value != ""){
		    		counter++;
		    	} else{
			    	alertString = alertString + " Autorizzabile";
			    }
		    	if(counter == 5){
		    		if (operazione == "invia") {
						if(document.Frm1.codStatoDomanda.value == STATO_DOMANDA_DA_ESAMINARE) {
							alert("Non è possibile inviare una comunicazione di validazione evento successivo con stato domanda \"Da Esaminare\"");
							return false;
						}
						else {
							return true;
						}
					}
		    		else {
						return true;
					}
		    	} else
		    		alert(alertString);
		    	return false;
		    }


		    function btFindReiezione_onclick(codice, descrizione, motivo, tipoRicerca, abilitaRicercaSenzaFiltro) {
		        var s= "AdapterHTTP?PAGE=RicercaReiezionePage";
		       	if (tipoRicerca == 'codice') {
		            if (codice.value == "") {
			          if (!abilitaRicercaSenzaFiltro) {
			              descrizione.value = "";
			              motivo.value = "";
			          }
			          else {
			        	  s += "&codiceReiezione=" + codice.value.toUpperCase();
			              s += "&retcod="+codice.name;
			              s +="&retnome="+descrizione.name;
			              s +="&retmotivo="+motivo.name;
			              s +="&tipoRicerca="+tipoRicerca;
			              window.open(s,"REIEZIONE", 'toolbar=0, scrollbars=1');   
			          }    
		            }
		            else {
		            	s += "&codiceReiezione=" + codice.value.toUpperCase();
		              	s += "&retcod="+codice.name;
		              	s +="&retnome="+descrizione.name;
		              	s +="&retmotivo="+motivo.name;
		              	s +="&tipoRicerca="+tipoRicerca;
		              	window.open(s,"REIEZIONE", 'toolbar=0, scrollbars=1');
		          	} 
		    	}
		    }
		    
		</script>
	</head>

	<body class="gestione">

		
			<%if ( (cdnLavoratore != null) && (!cdnLavoratore.equals("")) ) {%>
				<script language="Javascript">
	        	window.top.menu.caricaMenuLav( <%=cdnFunzione%>,   <%=cdnLavoratore%>);
	        	</script>
	      	<%}%>
		

		<%
		if (cdnLavoratore != null){
			InfCorrentiLav testata = new InfCorrentiLav(RequestContainer
					.getRequestContainer().getSessionContainer(),
					cdnLavoratore, user);
			//testata.setSkipLista(true);
			//testata.setPaginaLista("ListaNRAPage");
			testata.show(out);
		}
		%>
		
		<font color="red"><af:showErrors/></font>
		<font color="green">
		 <af:showMessages prefix="M_INVIA_NUOVA_RA"/>
		 <af:showMessages prefix="M_INVIA_SUC_RA"/>
		 <af:showMessages prefix="M_VARIAZIONE_RESIDENZA_RA"/>
		 <af:showMessages prefix="M_AGGIORNA_MSN_RA"/>
		</font>
		
		<af:form method="POST" action="AdapterHTTP" name="Frm1">
		
			<p class="titolo">Nuovo Reddito di Attivazione</p>
			<% out.print(htmlStreamTop); %>
			<br />
			
			
			
			<!-- Tabella principale del form -->
			<table class="main" border="0">
				<!-- Parte iniziare di anagrafica -->
				<tr>
					<td class="etichetta2">ID Domanda Web</td>
					<td colspan="2" class="campo1">
						<af:textBox classNameBase="input"
							name="idDomandaWeb" title="idDomandaWeb" readonly="true"
							value="<%=idDomandaWeb%>" />
					</td>
					<td class="etichetta2">ID Domanda Intranet</td>
					<td colspan="2" class="campo1">
						<af:textBox classNameBase="input"
							name="idDomandaIntranet" title="idDomandaIntranet" readonly="true"
							value="<%=idDomandaIntranet%>" />
					</td>
				</tr>
				<tr><td>&nbsp;</td></tr>
				<tr> <td colspan="6"><div class="sezione2">Dati Richiedente</div></td></tr>	
	
				<tr>
					<td class="etichetta2">Codice Fiscale</td>
					<td colspan="2" class="campo1">
						<af:textBox classNameBase="input"
							name="codiceFiscale" title="codiceFiscale" readonly="true"
							value="<%=codiceFiscale%>" />
					</td>
					<td class="etichetta2">Cognome/Nome</td>
					<td colspan="2" class="campo1">
						<af:textBox classNameBase="input"
							name="cognomeNome" title="cognomeNome" readonly="true"
							value="<%=cognomeNome%>" />
					</td>
				</tr>
				<tr>
					<td class="etichetta">Data Nascita</td>
					<td colspan="2" class="campo1">
						<af:textBox classNameBase="input" type="date"
							name="dataNascita" title="dataNascita" readonly="true"
							value="<%=dataNascita%>" />
					</td>
					<td class="etichetta">Comune Nascita</td>
					<td colspan="2" class="campo1">
						<af:textBox classNameBase="input"
							name="comuneNascita" title="comuneNascita" readonly="true"
							value="<%=comuneNascita%>"/>
					</td>
				</tr>
				<tr><td>&nbsp;</td></tr>
				<tr> <td colspan="6"><div class="sezione2">Residenza Richiedente</div></td></tr>
				
				<tr>
					<td  colspan="2">Dati presenti in SPIL</td>
					<td> </td>
					<td  colspan="2">Dati comunicati dall'INPS</td>
					<td> </td>				
				</tr>
				<tr>
					<td class="etichetta2">Indirizzo</td>
					<td colspan="2" class="campo1"><af:textBox classNameBase="input" readonly="true"
					name="indirizzoSIL" title="indirizzoSIL" 
					value="<%=indirizzoSIL%>" size="45"/>
					</td>
					<td class="etichetta2">Indirizzo</td>
					<td colspan="2" class="campo1"><af:textBox classNameBase="input"
					name="indirizzoINPS" title="indirizzoINPS" readonly="true"
					value="<%=indirizzoINPS%>" size="45"/>
					</td>
				</tr>
				<tr>
					<td class="etichetta2">Comune</td>
					<td colspan="2" class="campo1">
							<af:textBox classNameBase="input" readonly="true"
							name="codiceComuneSIL" title="codiceComuneSIL" 
							value="<%=codiceComuneSIL%>" size="4" maxlength="4"/>  
							<af:textBox classNameBase="input" readonly="true"
							name="descrizioneComuneSIL" title="descrizioneComuneSIL"  
							value="<%=descrizioneComuneSIL%>" />
					</td>
					<td class="etichetta2">Comune</td>
					<td colspan="2" class="campo1">
						<af:textBox classNameBase="input"	title="codiceComuneINPS"
						type="text" name="codiceComuneINPS" value="<%=codiceComuneINPS%>" size="4" maxlength="4" readonly="true"
						/> 
						<af:textBox type="text"	classNameBase="input" title="descrizioneComuneINPS"
						name="descrizioneComuneINPS" value="<%=descrizioneComuneINPS%>" readonly="true"
						/>
					</td>
				</tr>			
				<tr>
					<td class="etichetta2">CAP</td>
					<td colspan="2" class="campo1"><af:textBox classNameBase="input" readonly="true"
					name="capResidenzaSIL" title="capResidenzaSIL"  
					value="<%=capResidenzaSIL%>" />
					</td>
					<td class="etichetta2">CAP</td>
					<td colspan="2" class="campo1"><af:textBox classNameBase="input"
					name="capResidenzaINPS" title="capResidenzaINPS" readonly="true" 
					value="<%=capResidenzaINPS%>" />
					</td>
				</tr>			
				<tr>
					<%if(codiceProvinciaSIL!=null){
						if(!codiceProvinciaSIL.equals("22")){ %>
						<td class="etichetta2">Dati variazione residenza</td>
						<td colspan="2" class="campo1"><af:textBox classNameBase="input" 
						name="datVariazioneRes" title="datVariazioneRes" type="date" size="12"
						value="<%=datVariazioneRes%>" />
						</td>
					<%	
						} 
					  }			
					%>
				</tr>		
				
				<tr>
					<!-- Mostro il pulsante solo in caso la provincia non sia di Trento e se la variazione residenza non è stata già inviata -->
					<%if(codiceProvinciaSIL!=null){
						if(!codiceProvinciaSIL.equals("22") && !flgInviataVarResidenza.equalsIgnoreCase("S")){ %>
						<td colspan="6">
							<input class="pulsante" type="submit" name="variazioneResidenza" value="Invia comunicazione Variazione Residenza" onClick="return checkeAggiornaResidenza()"/>
							<input type="hidden" name="codiceProvinciaSIL" value="<%=codiceProvinciaSIL%>">
						</td>
					<%	
						} 
					  }			
					%>
				</tr>
					<!-- Fine parte anagrafica -->
					<% if(codMonotipoDomanda.equals("N")) { 
						//Se si tratta di una nuova comunicazione
						String dataPresentazioneAsdiNRA = null;
						String dataInizioPrestazioneAsdiNRA = null;
						String dataFinePrestazioneAsdiNRA = null;
						String importoGiornalieroNRA = null;
						String importoComplessivoNRA = null;
						String importoGiornalieroAsdi = null;
						String importoComplessivoAsdi = null;
						String noteDifferenze = null;
						String numeroProvvedimento = null;
						String dataProvvedimento = null;
						String esitoElaborazione = null;
						String codiceReiezione = "";
						String descReiezione = "";
						String motivoReiezione = "";
						String idComunicazioneMinLav = null;
						String codiceIntermediario = null;
						String dataComunicazione = null;
						String tipoPrestazione = null;
						String dataCreazioneComunicazione = null;
						String codiceOperatore = null;
						String identificativoComunicazione = null;
						String statoDomanda = "";
						String flgAutorizzabile = "";
						
						//Ottengo i valori relativi alla sezione mostrata
						if (row.containsAttribute("DATAPRESENTAZIONEASDINRA")) {
							dataPresentazioneAsdiNRA = row.getAttribute("DATAPRESENTAZIONEASDINRA").toString();
						}
						if (row.containsAttribute("DATAINIZIOPRESTAZIONEASDINRA")) {
							dataInizioPrestazioneAsdiNRA = row.getAttribute("DATAINIZIOPRESTAZIONEASDINRA").toString();
						}
						if (row.containsAttribute("DATAFINEPRESTAZIONEASDINRA")) {
							dataFinePrestazioneAsdiNRA = row.getAttribute("DATAFINEPRESTAZIONEASDINRA").toString();
						}
						if (row.containsAttribute("IMPORTOGIORNALIERONRA")) {
							importoGiornalieroNRA = row.getAttribute("IMPORTOGIORNALIERONRA").toString();
						}
						if (row.containsAttribute("IMPORTOCOMPLESSIVONRA")) {
							importoComplessivoNRA = row.getAttribute("IMPORTOCOMPLESSIVONRA").toString();
						}
						if (row.containsAttribute("IMPORTOGIORNALIEROASDI")) {
							importoGiornalieroAsdi = row.getAttribute("IMPORTOGIORNALIEROASDI").toString();
						}
						if (row.containsAttribute("IMPORTOCOMPLESSIVOASDI")) {
							importoComplessivoAsdi = row.getAttribute("IMPORTOCOMPLESSIVOASDI").toString();
						}
						if (row.containsAttribute("NOTEDIFFERENZE")) {
							noteDifferenze = row.getAttribute("NOTEDIFFERENZE").toString();
						}
						if (row.containsAttribute("NUMEROPROVVEDIMENTO")) {
							numeroProvvedimento = row.getAttribute("NUMEROPROVVEDIMENTO").toString();
						}
						if (row.containsAttribute("DATAPROVVEDIMENTO")) {
							dataProvvedimento = row.getAttribute("DATAPROVVEDIMENTO").toString();
						}
						if (row.containsAttribute("ESITOELABORAZIONE")) {
							esitoElaborazione = row.getAttribute("ESITOELABORAZIONE").toString();
						}
						if (row.containsAttribute("CODICEREIEZIONE")) {
							codiceReiezione = row.getAttribute("CODICEREIEZIONE").toString();
						}
						if (row.containsAttribute("descReiezione")) {
							descReiezione = row.getAttribute("descReiezione").toString();
						}
						if (row.containsAttribute("strMotivoDomanda")) {
							motivoReiezione = row.getAttribute("strMotivoDomanda").toString();
						}
						if (row.containsAttribute("IDCOMUNICAZIONEMINLAV")) {
							idComunicazioneMinLav = row.getAttribute("IDCOMUNICAZIONEMINLAV").toString();
						}
						if (row.containsAttribute("CODICEINTERMEDIARIO")) {
							codiceIntermediario = row.getAttribute("CODICEINTERMEDIARIO").toString();
						}
						if (row.containsAttribute("DATACOMUNICAZIONE")) {
							dataComunicazione = row.getAttribute("DATACOMUNICAZIONE").toString();
						}
						if (row.containsAttribute("TIPOPRESTAZIONE")) {
							tipoPrestazione = row.getAttribute("TIPOPRESTAZIONE").toString();
						}
						if (row.containsAttribute("DATACREAZIONECOMUNICAZIONE")) {
							dataCreazioneComunicazione = row.getAttribute("DATACREAZIONECOMUNICAZIONE").toString();
						}
						if (row.containsAttribute("CODICEOPERATORE")) {
							codiceOperatore = row.getAttribute("CODICEOPERATORE").toString();
						}
						if (row.containsAttribute("identificativoComunicazione")) {
							identificativoComunicazione = row.getAttribute("identificativoComunicazione").toString();
						}
						if (row.containsAttribute("codStatoDomanda")) {
							statoDomanda = row.getAttribute("codStatoDomanda").toString();
						}
						if (row.containsAttribute("flgAutorizzabile")) {
							flgAutorizzabile = row.getAttribute("flgAutorizzabile").toString();
						}
					%>
					<tr><td>&nbsp;</td></tr>
					<tr> <td colspan="6"><div class="sezione2">Dati Comunicazione domanda</div></td></tr>
					
					<tr>
						<td class="etichetta2">Data Prestazione Asdi</td> 
						<td class="campo1" nowrap>
							<af:textBox classNameBase="input" type="date" 
							name="dataPresentazioneAsdiNRA" title="dataPresentazioneAsdiNRA" readonly ="<%=String.valueOf(readOnly)%>" 
							value="<%=dataPresentazioneAsdiNRA%>" size="12" />
						</td>
						<td class="etichetta2">Data inizio Prestazione Asdi</td>
						<td class="campo1" nowrap>
							<af:textBox classNameBase="input" type="date" 
							name="dataInizioPrestazioneAsdiNRA" title="dataInizioPrestazioneAsdiNRA" readonly ="<%=String.valueOf(readOnly)%>" 
							value="<%=dataInizioPrestazioneAsdiNRA%>" size="12" />
						</td>
						<td class="etichetta2">Data fine Prestazione Asdi</td>
						<td class="campo1" nowrap>
							<af:textBox classNameBase="input" type="date" 
							name="dataFinePrestazioneAsdiNRA" title="dataFinePrestazioneAsdiNRA" readonly ="<%=String.valueOf(readOnly)%>" 
							value="<%=dataFinePrestazioneAsdiNRA%>" size="12"/>
						</td>
					</tr>
					<tr>	
						<td class="etichetta2">Importo Giornaliero Nra</td> 
						<td colspan="2" class="campo1"><af:textBox classNameBase="input"
							name="importoGiornalieroNRA" title="importoGiornalieroNRA" readonly ="<%=String.valueOf(readOnly)%>" 
							value="<%=importoGiornalieroNRA%>" /></td>
						<td class="etichetta2">Importo Complessivo Nra</td>
						<td colspan="2" class="campo1"><af:textBox classNameBase="input"
							name="importoComplessivoNRA" title="importoComplessivoNRA" readonly ="<%=String.valueOf(readOnly)%>" 
							value="<%=importoComplessivoNRA%>" /></td>
					</tr>			
					<tr>	
						<td class="etichetta2">Importo Giornaliero Asdi</td> 
						<td colspan="2" class="campo1"><af:textBox classNameBase="input"
							name="importoGiornalieroAsdi" title="importoGiornalieroAsdi" readonly ="<%=String.valueOf(readOnly)%>" 
							value="<%=importoGiornalieroAsdi%>" /></td>
						<td class="etichetta2">Importo Complessivo Asdi</td>
						<td colspan="2" class="campo1"><af:textBox classNameBase="input"
							name="importoComplessivoAsdi" title="importoComplessivoAsdi" readonly ="<%=String.valueOf(readOnly)%>" 
							value="<%=importoComplessivoAsdi%>" /></td>
					</tr>
					<tr>
						<td class="etichetta2">Note Differenze</td> 
						<td class="campo1" colspan="5">
							<af:textArea classNameBase="textarea" name="noteDifferenze" value="<%=noteDifferenze%>"
                       		cols="30" rows="4" maxlength="3000"  readonly="<%=String.valueOf(readOnly)%>" />
                       </td>
					</tr>
					
					<tr><td>&nbsp;</td></tr>
					<tr> <td colspan="6"><div class="sezione2">Provvedimento</div></td></tr>
					
					<tr>
						<td class="etichetta2">Numero Provvedimento</td> 
						<td colspan="2" class="campo1"><af:textBox classNameBase="input"
							name="numeroProvvedimento" title="numeroProvvedimento" readonly ="<%=String.valueOf(readOnly)%>" 
							value="<%=numeroProvvedimento%>" maxlength="5"/></td>
						<td class="etichetta2">Data Provvedimento</td>
						<td colspan="2" class="campo1"><af:textBox classNameBase="input" type="date"
							name="dataProvvedimento" title="dataProvvedimento" readonly ="<%=String.valueOf(readOnly)%>"
							value="<%=dataProvvedimento%>" size="12" /></td>
					</tr>
					<tr>
						<td class="etichetta2">Esito Elaborazione</td> 
						<td colspan="2" class="campo1">			       			
			       			<af:comboBox classNameBase="input" name="esitoElaborazione" addBlank="false"  disabled="<%=String.valueOf(readOnly)%>">
					          <OPTION value=""  <%if (esitoElaborazione == null) out.print("SELECTED=\"true\"");%>></OPTION>
					          <OPTION value="Si" <%if (esitoElaborazione != null && esitoElaborazione.equalsIgnoreCase("Si")) out.print("SELECTED=\"true\"");%>>Domanda Validata</OPTION>
					          <OPTION value="No" <%if (esitoElaborazione != null && esitoElaborazione.equalsIgnoreCase("No")) out.print("SELECTED=\"true\"");%>>Domanda non Validata</OPTION>
					        </af:comboBox>
			       		</td>
			       		
			       		
						<td class="etichetta2">Codice Reiezione</td>
						<%if(!readOnly){ %>
							<td colspan="2" class="campo1" nowrap>
								<af:textBox classNameBase="input" title="Codice Reiezione" type="text" 
									 	name="codiceReiezione" size="5" value="<%=codiceReiezione%>" />&nbsp;
								<A HREF="javascript:btFindReiezione_onclick(document.Frm1.codiceReiezione, document.Frm1.strCodiceReiezione, document.Frm1.strMotivoReiezione, 'codice', true);"><IMG name="imageBinocoloReiezione" border="0" src="../../img/binocolo.gif" alt="cerca per codice"></A>&nbsp;
								<af:textBox type="text" classNameBase="input" name="strCodiceReiezione" value="<%=descReiezione%>" size="30" title="Descrizione" readonly="true" />
								<br>
								<af:textBox name="strMotivoReiezione" size="50" title="<%=motivoReiezione%>" readonly="true" classNameBase="input" value="<%=motivoReiezione%>" />
							</td>
						<%} 
						else {%>
							<td colspan="2" class="campo1" nowrap>
				   			<af:textBox classNameBase="input" readonly="true" name="codiceReiezione" title="codiceReiezione" 
									value="<%=codiceReiezione%>" />&nbsp;
							<af:textBox type="text" classNameBase="input" name="strCodiceReiezione" value="<%=descReiezione%>" size="30" title="Reiezione" readonly="true" />
							<br>
							<af:textBox type="text" classNameBase="input" name="strMotivoReiezione" value="<%=motivoReiezione%>" size="50" title="<%=motivoReiezione%>" readonly="true" />
							</td>
				   		<%}%>
					</tr>
					<tr>
						<td class="etichetta2">Stato Domanda</td> 
						<td colspan="2" class="campo1">			       			
			       			<af:comboBox classNameBase="input" name="codStatoDomanda" addBlank="true" title="Stato Domanda"
			       				moduleName="M_STATO_DOMANDA_RA" selectedValue="<%=statoDomanda%>" disabled="<%=String.valueOf(readOnly)%>" />
			       		</td>
						<td class="etichetta2">Autorizzabile</td>
						<td colspan="2" class="campo1">
							<af:comboBox classNameBase="input" name="flgAutorizzabile" addBlank="true" title="Autorizzabile"
								moduleName="M_GenericComboSiNo" selectedValue="<%=flgAutorizzabile%>" disabled="<%=String.valueOf(readOnly)%>" />
					    </td>
					</tr>
					<tr><td>&nbsp;</td></tr>
					<tr> <td colspan="6"><div class="sezione2"><img id='IMG1' src='<%=img1%>' onclick='cambia(this, document.getElementById("TBL1"))'>Sottoscrizione Progetto</div></td></tr>
					
					<!-- Sezioni a scomparsa -->
					<tr>
						<td colspan="6">
							<table class="main" id='TBL1' style='display:none'>
								<script>initSezioni(new Sezione(document.getElementById('TBL1'),document.getElementById('IMG1'),false));</script>
								<tr>
									<td class="etichetta2">Id Comunicazione Ministero Lavoro</td> 
									<td class="campo1"><af:textBox classNameBase="input" readonly="true"
										name="idComunicazioneMinLav" title="idComunicazioneMinLav" 
										value="<%=idComunicazioneMinLav%>" /></td>
									<td class="etichetta2">Codice Intermediario</td>
									<td class="campo1"><af:textBox classNameBase="input" readonly="true"
										name="codiceIntermediario" title="codiceIntermediario" 
										value="<%=codiceIntermediario%>" /></td>
								</tr>
								<tr>
									<td class="etichetta2">Data Comunicazione</td> 
									<td class="campo1"><af:textBox classNameBase="input" readonly="true"
										name="dataComunicazione" title="dataComunicazione"  
										value="<%=dataComunicazione%>" /></td>
									<td class="etichetta2">Tipo Prestazione</td>
									<td class="campo1"><af:textBox classNameBase="input" readonly="true"
										name="tipoPrestazione" title="tipoPrestazione" 
										value="<%=tipoPrestazione%>" /></td>
								</tr>
							</table>
						</td>
					</tr>
					
					<tr><td>&nbsp;</td></tr>
					<tr> <td colspan="6"><div class="sezione2"><img id='IMG2' src='<%=img1%>' onclick='cambia(this, document.getElementById("TBL2"))'>Dati Generici</div></td></tr>
					
					<tr>
						<td colspan="6">
							<table class="main" id='TBL2' style='display:none'>
							<script>initSezioni(new Sezione(document.getElementById('TBL2'),document.getElementById('IMG2'),false));</script>
								<tr>
									<td class="etichetta2">Data Creazione</td> 
									<td class="campo1"><af:textBox classNameBase="input" readonly="true"
										name="dataCreazioneComunicazione" title="dataCreazioneComunicazione" size="12"
										value="<%=dataCreazioneComunicazione%>" /></td>
									<td class="etichetta2">Id Comunicazione</td>
									<td class="campo1"><af:textBox classNameBase="input" readonly="true"
										name="identificativoComunicazione" title="identificativoComunicazione" 
										value="<%=identificativoComunicazione%>" /></td>
									<td class="etichetta2">Codice Operatore</td>
									<td class="campo1"><af:textBox classNameBase="input" readonly="true"
										name="codiceOperatore" title="codiceOperatore" 
										value="<%=codiceOperatore%>" /></td>
								</tr>
							</table>
						</td>
					</tr>
					<!-- Fine sesioni a scomparsa -->
					<!-- Pulsanti -->
					<tr>
						<%	if(canAggiorna && !readOnly){ %>
								<td colspan="3">
									<input class="pulsante" type="submit" name="aggiornaDati_nuova" value="Aggiorna Dati" onClick="return checkeAggiornaN('aggiorna')"/>
								</td>
						<%	}
							if(canInvia && !readOnly){ %>
								<td colspan="3" >
									<input class="pulsante" type="submit" name="invia_nuova" value="Valida ed invia Comunicazione" onClick="return checkeAggiornaN('invia')"/>
								</td>
						<%	} %>
					</tr>
					<tr><td>&nbsp;</td></tr>
					<tr>
						<td colspan="6">
					      <center>
					      	<input class="pulsante" type="button" value="Torna alla lista" onClick="goTo('<%=urlDiLista%>')"/>
					      </center>
						</td>
					</tr>
					
					<% } else if(codMonotipoDomanda.equals("S")) {  
						//Se si tratta di una comunicazione successiva
						String codMotivoComunicazione = null;
						String codiceIntermediario = null;
						String tipoComunicazione = null;
						String codTipoEvento = null;
						String dataEvento = null;
						String codMotivoSanzione = null;
						String idComunicazioneAnnullata = null;
						String importoComplessivoAsdi = null;
						String motivoEvento = null;
						String descrizioneEvento = null;
						String notaEvento = null;
						String importoComplessivoNraDec = null;
						String importoComplessivoAsdiDec = null;
						String numeroProvvedimento = null;
						String dataProvvedimento = null;
						String codTipoProv = null;
						String dataCreazioneComunicazione = null;
						String identificativoComunicazione = null;
						String codiceOperatore = null;
						String idComunicazioneRichiesta = null;
						String statoDomanda = "";
						String flgAutorizzabile = "";
						String descMotivoEvento = "";
						String strMotivoDomandaEvento = "";

						//Ottengo i dati relativi alla sezione mostrata
						if(rowResidenzaINPS!=null){
							if (rowResidenzaINPS.containsAttribute("identificativoComunicazione")) {
								idComunicazioneRichiesta = rowResidenzaINPS.getAttribute("identificativoComunicazione").toString();
							}
						}
						if (row.containsAttribute("CODMOTIVOCOMUNICAZIONE")) {
							codMotivoComunicazione = row.getAttribute("CODMOTIVOCOMUNICAZIONE").toString();
						}
						if (row.containsAttribute("CODICEINTERMEDIARIO")) {
							codiceIntermediario = row.getAttribute("CODICEINTERMEDIARIO").toString();
						}
						if (row.containsAttribute("CODTIPOCOMUNICAZIONE")) {
							tipoComunicazione = row.getAttribute("CODTIPOCOMUNICAZIONE").toString();
							if(readOnly){
								boolean exit=false;
								int i=0;
								while(i<tipoComunicazioneVec.size() && !exit  ){
									SourceBean sb = (SourceBean) tipoComunicazioneVec.get(i);
									String cod = sb.getAttribute("CODICE").toString();
									if(cod.equals(tipoComunicazione)){
										tipoComunicazione = sb.getAttribute("DESCRIZIONE").toString();
										exit = true;
									}
									i++;
								}
							}
						}
						if (row.containsAttribute("CODTIPOEVENTO")) {
							codTipoEvento = row.getAttribute("CODTIPOEVENTO").toString();
							if(readOnly){
								boolean exit=false;
								int i=0;
								while(i<tipoEventoVec.size() && !exit  ){
									SourceBean sb = (SourceBean) tipoEventoVec.get(i);
									String cod = sb.getAttribute("CODICE").toString();
									if(cod.equals(codTipoEvento)){
										codTipoEvento = sb.getAttribute("DESCRIZIONE").toString();
										exit = true;
									}
									i++;
								}
							}
						}
						if (row.containsAttribute("DATAEVENTO")) {
							dataEvento = row.getAttribute("DATAEVENTO").toString();
						}
						if (row.containsAttribute("CODMOTIVOSANZIONE")) {
							codMotivoSanzione = row.getAttribute("CODMOTIVOSANZIONE").toString();
							if(readOnly){
								boolean exit=false;
								int i=0;
								while(i<motivoComunicazioneVec.size() && !exit  ){
									SourceBean sb = (SourceBean) motivoComunicazioneVec.get(i);
									String cod = sb.getAttribute("CODICE").toString();
									if(cod.equals(codMotivoSanzione)){
										codMotivoSanzione = sb.getAttribute("DESCRIZIONE").toString();
										exit = true;
									}
									i++;
								}
							}
						}
						if (row.containsAttribute("IDCOMUNICAZIONEANNULLATA")) {
							idComunicazioneAnnullata = row.getAttribute("IDCOMUNICAZIONEANNULLATA").toString();
						}
						if (row.containsAttribute("IMPORTOCOMPLESSIVOASDI")) {
							importoComplessivoAsdi = row.getAttribute("IMPORTOCOMPLESSIVOASDI").toString();
						} 
						if (row.containsAttribute("DESCRIZIONEEVENTO")) {
							descrizioneEvento = row.getAttribute("DESCRIZIONEEVENTO").toString();
						}
						if (row.containsAttribute("NOTAEVENTO")) {
							notaEvento = row.getAttribute("NOTAEVENTO").toString();
						}
						if (row.containsAttribute("IMPORTOCOMPLESSIVONRADEC")) {
							importoComplessivoNraDec = row.getAttribute("IMPORTOCOMPLESSIVONRADEC").toString();
						}
						if (row.containsAttribute("IMPORTOCOMPLESSIVOASDIDEC")) {
							importoComplessivoAsdiDec = row.getAttribute("IMPORTOCOMPLESSIVOASDIDEC").toString();
						}
						if (row.containsAttribute("NUMEROPROVVEDIMENTO")) {
							numeroProvvedimento = row.getAttribute("NUMEROPROVVEDIMENTO").toString();
						}
						if (row.containsAttribute("DATAPROVVEDIMENTO")) {
							dataProvvedimento = row.getAttribute("DATAPROVVEDIMENTO").toString();
						}
						if (row.containsAttribute("codStatoDomanda")) {
							statoDomanda = row.getAttribute("codStatoDomanda").toString();
						}
						if (row.containsAttribute("flgAutorizzabile")) {
							flgAutorizzabile = row.getAttribute("flgAutorizzabile").toString();
						}
						if (row.containsAttribute("CODTIPOPROV")) {
							codTipoProv = row.getAttribute("CODTIPOPROV").toString();
							if(readOnly){
								boolean exit=false;
								int i=0;
								while(i<tipoEventoVec.size() && !exit  ){
									SourceBean sb = (SourceBean) tipoEventoVec.get(i);
									String cod = sb.getAttribute("CODICE").toString();
									if(cod.equals(codTipoProv)){
										codTipoProv = sb.getAttribute("DESCRIZIONE").toString();
										exit = true;
									}
									i++;
								}
							}
						}  
						if (row.containsAttribute("DATACREAZIONECOMUNICAZIONE")) {
							dataCreazioneComunicazione = row.getAttribute("DATACREAZIONECOMUNICAZIONE").toString();
						}
						if (row.containsAttribute("IDENTIFICATIVOCOMUNICAZIONE")) {
							identificativoComunicazione = row.getAttribute("IDENTIFICATIVOCOMUNICAZIONE").toString();
						}
						if (row.containsAttribute("CODICEOPERATORE")) {
							codiceOperatore = row.getAttribute("CODICEOPERATORE").toString();
						}
						if (row.containsAttribute("MOTIVOEVENTO")) {
							motivoEvento = row.getAttribute("MOTIVOEVENTO").toString();
						}
						if (row.containsAttribute("descMotivoEvento")) {
							descMotivoEvento = row.getAttribute("descMotivoEvento").toString();
						}
						if (row.containsAttribute("strMotivoDomandaEvento")) {
							strMotivoDomandaEvento = row.getAttribute("strMotivoDomandaEvento").toString();
						}
						
						if(codMotivoComunicazione != null && ( codMotivoComunicazione.equals("A") || codMotivoComunicazione.equals("N") ) ){
					%>
					<!-- Comunicazione del Ministero del lavoro -->
					<tr><td>&nbsp;</td></tr>
					<tr> <td colspan="6"><div class="sezione2">Comunicazione Ministero del Lavoro</div></td></tr>
					
					<tr>
						<td class="etichetta2">Codice Intermediario</td> 
						<td class="campo1"><af:textBox classNameBase="input"
							name="codiceIntermediario" title="codiceIntermediario" readonly ="<%=String.valueOf(readOnly)%>"
							value="<%=codiceIntermediario%>" /></td>
						<td class="etichetta2">Motivo Comunicazione</td>
						<td class="campo1">
			       			<af:comboBox classNameBase="input" name="codMotivoComunicazione" addBlank="false"  disabled="<%=String.valueOf(readOnly)%>">
					          <OPTION value=""  <%if (codMotivoComunicazione == null) out.print("SELECTED=\"true\"");%>></OPTION>
					          <OPTION value="A" <%if (codMotivoComunicazione != null && codMotivoComunicazione.equalsIgnoreCase("A")) out.print("SELECTED=\"true\"");%>>Annullata</OPTION>
					          <OPTION value="N" <%if (codMotivoComunicazione != null && codMotivoComunicazione.equalsIgnoreCase("N")) out.print("SELECTED=\"true\"");%>>Nuova</OPTION>
					        </af:comboBox>
					    </td>
						<td class="etichetta2">Tipo Comunicazione</td>
						<td class="campo1">
							<%if(!readOnly){ %>
					    		<af:comboBox name="tipoComunicazione" moduleName="M_TIPO_COMUNICAZIONE" addBlank="true" selectedValue="<%=tipoComunicazione%>" />
					   		<%} else { %>
					   			<af:textBox classNameBase="input" readonly="true" name="tipoComunicazione" title="tipoComunicazione" 
										value="<%=tipoComunicazione%>" />
					   		<%} %>
					    </td>
					</tr>
					<tr>
						<td class="etichetta2">Tipo Evento</td> 
						<td colspan="2" class="campo1">
							<%if(!readOnly){ %>
					    		<af:comboBox name="codTipoEvento" moduleName="M_TIPO_EVENTO" addBlank="true" selectedValue="<%=codTipoEvento%>" />
					   		<%} else { %>
					   			<af:textBox classNameBase="input" readonly="true" name="codTipoEvento" title="codTipoEvento" 
										value="<%=codTipoEvento%>" />
					   		<%} %>
					    </td>
						<td  class="etichetta2">Motivo Sanzione</td>
						<td colspan="2" class="campo1">
							<%if(!readOnly){ %>
					    		<af:comboBox name="codMotivoSanzione" moduleName="M_MOTIVO_COMUNICAZIONE" addBlank="true" selectedValue="<%=codMotivoSanzione%>" />
					   		<%} else { %>
					   			<af:textBox classNameBase="input" readonly="true" name="codMotivoSanzione" title="codMotivoSanzione" 
										value="<%=codMotivoSanzione%>" />
					   		<%} %>
					    </td>
					</tr>
					<tr>
						<td class="etichetta2">Id comuncazione annullata</td> 
						<td colspan="2" class="campo1"><af:textBox classNameBase="input"
							name="idComunicazioneAnnullata" title="idComunicazioneAnnullata" readonly="true"
							value="<%=idComunicazioneAnnullata%>" /></td>
						<td class="etichetta2">Importo Complessivo Asdi</td>
						<td colspan="2" class="campo1"><af:textBox classNameBase="input"
							name="importoComplessivoAsdi" title="importoComplessivoAsdi" readonly ="<%=String.valueOf(readOnly)%>"
							value="<%=importoComplessivoAsdi%>" /></td>
					</tr>
					<!-- Fine comunicazione del ministero del lavoro -->
					<% } else if(motivoEvento != null) { %>
					<!-- Altra comunicazione -->
					<tr> <td colspan="6"><div class="sezione2">Altre Comunicazioni</div></td></tr>
					
					<input type="hidden" name="CODICEINTERMEDIARIO" value="<%=codiceIntermediario%>"/> 
					
					<tr>
						<td class="etichetta2">Tipo Evento</td> 
						<td colspan="2" class="campo1">
					    	<af:comboBox name="codTipoEvento" moduleName="M_TIPO_EVENTO" addBlank="true" selectedValue="<%=codTipoEvento%>" disabled="<%=String.valueOf(readOnly)%>"/>
					    </td>
					    <td class="etichetta2">Motivo Evento</td>
					    <%if(!readOnly){ %>
							<td colspan="2" class="campo1" nowrap>
								<af:textBox classNameBase="input" title="Motivo Evento" type="text" 
									 	name="motivoEvento" size="5" value="<%=motivoEvento%>" />&nbsp;
								<A HREF="javascript:btFindReiezione_onclick(document.Frm1.motivoEvento, document.Frm1.strDescMotivoEvento, document.Frm1.strMotivoEventoReiezione, 'codice', true);"><IMG name="imageBinocoloReiezione" border="0" src="../../img/binocolo.gif" alt="cerca per codice"></A>&nbsp;
								<af:textBox type="text" classNameBase="input" name="strDescMotivoEvento" value="<%=descMotivoEvento%>" size="30" title="Descrizione" readonly="true" />
								<br>
								<af:textBox name="strMotivoEventoReiezione" size="50" title="<%=strMotivoDomandaEvento%>" readonly="true" classNameBase="input" value="<%=strMotivoDomandaEvento%>" />
							</td>
						<%} 
						else {%>
							<td colspan="2" class="campo1" nowrap>
					   			<af:textBox classNameBase="input" readonly="true" name="motivoEvento" title="Motivo Evento" 
										value="<%=motivoEvento%>" />&nbsp;
								<af:textBox type="text" classNameBase="input" name="strDescMotivoEvento" value="<%=descMotivoEvento%>" size="30" title="Descrizione" readonly="true" />
								<br>
								<af:textBox name="strMotivoEventoReiezione" size="50" title="<%=strMotivoDomandaEvento%>" readonly="true" classNameBase="input" value="<%=strMotivoDomandaEvento%>" />
							</td>
				   		<%}%>
					</tr>
					<tr>
						<td class="etichetta2">Descrizione Evento</td> 
						<td colspan="2" class="campo1"><af:textBox classNameBase="input"
							name="descrizioneEvento" title="descrizioneEvento" readonly ="<%=String.valueOf(readOnly)%>"
							value="<%=descrizioneEvento%>" /></td>
						<td class="etichetta2">Nota Evento</td>
						<td colspan="2" class="campo1"><af:textBox classNameBase="input"
							name="notaEvento" title="notaEvento" readonly ="<%=String.valueOf(readOnly)%>"
							value="<%=notaEvento%>" /></td>
					</tr>
					<tr>
						<td class="etichetta2">Data Evento</td>
						<td colspan="2" class="campo1"><af:textBox classNameBase="input" type="date"
							name="dataEvento" title="Data Evento" readonly ="<%=String.valueOf(readOnly)%>"
							value="<%=dataEvento%>" size="12"/></td>
					</tr>
					<!-- Fine altre comunicazioni -->
					<% } %>
					
					<tr><td>&nbsp;</td></tr>
					<tr> <td colspan="6"><div class="sezione2">Importo Complessivo</div></td></tr>
									
					<tr>
						<td class="etichetta2">Importo Nra decurtato</td> 
						<td colspan="2" class="campo1"><af:textBox classNameBase="input"
							name="importoComplessivoNraDec" title="importoComplessivoNraDec" readonly ="<%=String.valueOf(readOnly)%>"
							value="<%=importoComplessivoNraDec%>" /></td>
						<td class="etichetta2">Importo Asdi decurtato</td>
						<td colspan="2" class="campo1"><af:textBox classNameBase="input"
							name="importoComplessivoAsdiDec" title="importoComplessivoAsdiDec" readonly ="<%=String.valueOf(readOnly)%>"
							value="<%=importoComplessivoAsdiDec%>" /></td>
					</tr>
				
					<tr><td>&nbsp;</td></tr>
					<tr> <td colspan="6"><div class="sezione2">Provvedimento</div></td></tr>
					
					<tr>
						<td class="etichetta2">Numero provvedimento</td> 
						<td colspan="2" class="campo1"><af:textBox classNameBase="input"
							name="numeroProvvedimento" title="numeroProvvedimento" readonly ="<%=String.valueOf(readOnly)%>"
							value="<%=numeroProvvedimento%>" /></td>
						<td class="etichetta2">Data provvedimento</td>
						<td colspan="2" class="campo1"><af:textBox classNameBase="input" type="date"
							name="dataProvvedimento" title="dataProvvedimento" readonly ="<%=String.valueOf(readOnly)%>"
							value="<%=dataProvvedimento%>" size="12"/></td>
					</tr>		
					<tr>
						<td class="etichetta2">Tipo provvedimento</td> 
						<td colspan="2" class="campo1">
							<%if(!readOnly){ %>
					    		<af:comboBox name="codTipoProv" moduleName="M_TIPO_EVENTO" addBlank="true" selectedValue="<%=codTipoProv%>" />
					   		<%} else { %>
					   			<af:textBox classNameBase="input" readonly="true" name="codTipoProv" title="codTipoProv" 
										value="<%=codTipoProv%>" />
					   		<%} %>
					    </td>
					</tr>
					<tr>
						<td class="etichetta2">Stato Domanda</td> 
						<td colspan="2" class="campo1">			       			
			       			<af:comboBox classNameBase="input" name="codStatoDomanda" addBlank="true" title="Stato Domanda"
			       				moduleName="M_STATO_DOMANDA_RA" selectedValue="<%=statoDomanda%>" disabled="<%=String.valueOf(readOnly)%>" />
			       		</td>
						<td class="etichetta2">Autorizzabile</td>
						<td colspan="2" class="campo1">
							<af:comboBox classNameBase="input" name="flgAutorizzabile" addBlank="true" title="Autorizzabile"
								moduleName="M_GenericComboSiNo" selectedValue="<%=flgAutorizzabile%>" disabled="<%=String.valueOf(readOnly)%>" />
					    </td>
					</tr>
					
					<tr><td>&nbsp;</td></tr>
					<tr> <td colspan="6"><div class="sezione2"><img id='IMG3' src='<%=img1%>' onclick='cambia(this, document.getElementById("TBL3"))'>Dati Generici</div></td></tr>
					
					<tr>
						<td colspan="6">
							<table class="main" id='TBL3' style='display:none'>
								<script>initSezioni(new Sezione(document.getElementById('TBL3'),document.getElementById('IMG3'),false));</script>
								<tr>
									<td class="etichetta2">Data Creazione</td> 
									<td class="campo1"><af:textBox classNameBase="input" readonly="true"
										name="dataCreazioneComunicazione" title="dataCreazioneComunicazione"
										value="<%=dataCreazioneComunicazione%>" /></td>
									<td class="etichetta2">Id Comunicazione</td>
									<td class="campo1"><af:textBox classNameBase="input" readonly="true"
										name="identificativoComunicazione" title="identificativoComunicazione" 
										value="<%=identificativoComunicazione%>" /></td>
									<td class="etichetta2">Codice Operatore</td>
									<td class="campo1"><af:textBox classNameBase="input" readonly="true"
										name="codiceOperatore" title="codiceOperatore"
										value="<%=codiceOperatore%>" /></td>
								</tr>
								<tr>
									<td class="etichetta2">Identificativo comunicazione Richiesta</td> 
									<td colspan="2" class="campo1"><af:textBox classNameBase="input" readonly="true"
										name="idComunicazioneRichiesta" title="idComunicazioneRichiesta" 
										value="<%=idComunicazioneRichiesta%>" /></td>								
								</tr>
							</table>
						</td>
					</tr>
					

					<tr>
					<!-- Pulsanti -->
					<%	if(canAggiorna && !readOnly){ %>
							<td colspan="3">
								<input class="pulsante" type="submit" name="aggiornaDati_suc" value="Aggiorna Dati" onClick="return checkeAggiornaS('aggiorna')"/>
							</td>
					<%	}
						if(canInvia && !readOnly){%>
							<td colspan="3">
								<input class="pulsante" type="submit" name="invia_suc" value="Valida ed invia Comunicazione evento successivo" onClick="return checkeAggiornaS('invia')"/>
							</td>
							<input type="hidden" name="cdnlavoratore" value="<%=cdnLavoratore%>">
					<%	} %>
					</tr>
					<tr><td>&nbsp;</td></tr>
					<tr>
						<td colspan="6">
					      <center>
					      	<input class="pulsante" type="button" value="Torna alla lista" onClick="goTo('<%= StringUtils.formatValue4Javascript(urlDiLista) %>')"/>
					      </center>
						</td>
					</tr>
				<% } %>
				
			</table>
			<input type="hidden" name="PRGNUOVORA" value="<%=Integer.parseInt(prgnuovora)%>"/>
			<input type="hidden" name="nome" value="<%=nome%>">
			<input type="hidden" name="cognome" value="<%=cognome%>">
			<input type="hidden" name="codiceCatastoNascita" value="<%=codiceCatastoNascita%>">
			<input type="hidden" name="PAGE" value="DettaglioNRAPage">
			<input type="hidden" name="CDNUTMOD" value="<%=user.getCodut()%>">
			<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
			<input type="hidden" name="NUMKLONUOVORA" value="<%=NUMKLONUOVORA%>">
		</af:form>
		<BR />

		<%
			out.print(htmlStreamBottom);
		%>
		<center>
			<% operatoreInfo.showHTML(out); %>
		</center>
	</body>
</html>
<%} %>
