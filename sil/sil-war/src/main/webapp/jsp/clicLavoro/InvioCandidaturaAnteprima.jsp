<%@ page contentType="text/html;charset=utf-8"%>
<%@page import="it.eng.sil.action.report.UtilsConfig"%>
<%@ page import="
  com.engiweb.framework.base.*,
  it.eng.sil.security.*,
  it.eng.sil.util.*, it.eng.sil.module.movimenti.constant.Properties,
  it.eng.afExt.utils.StringUtils,
  java.util.*,
  java.lang.*,
  java.math.*
" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af" %>
<%@ taglib uri="presel" prefix="ps" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>



<%
	UtilsConfig utility = new UtilsConfig("CUR_CLIC");
	String configCurClic = utility.getConfigurazioneDefault_Custom();
	boolean flgNoteCurriculum = false;
	if (configCurClic.equals(Properties.CUSTOM_CONFIG)) {
		flgNoteCurriculum = true;
	}

    String _page = (String) serviceRequest.getAttribute("PAGE");
	String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");
	String numConfigCurr = serviceResponse.containsAttribute("M_CONFIG_INVIO_CURRICULUM.ROWS.ROW.NUM")?
			 serviceResponse.getAttribute("M_CONFIG_INVIO_CURRICULUM.ROWS.ROW.NUM").toString():Properties.DEFAULT_CONFIG;

	//Dati Anagrafici
	SourceBean datiAnagrafici = (SourceBean) serviceResponse.getAttribute("M_GetDatiAnagraficiAnteprimaCandidatura.ROWS.ROW");
	String codicefiscale = "", cognome = "", nome = "", sesso = "", datanascita = "", idcittadinanza = "", cittadinanza = "", idcomune = "";
	if (datiAnagrafici != null){		
		codicefiscale = StringUtils.getAttributeStrNotNull(datiAnagrafici, "codicefiscale");
		cognome = StringUtils.getAttributeStrNotNull(datiAnagrafici, "cognome");
		nome = StringUtils.getAttributeStrNotNull(datiAnagrafici, "nome");
		sesso = StringUtils.getAttributeStrNotNull(datiAnagrafici, "sesso");
		datanascita = StringUtils.getAttributeStrNotNull(datiAnagrafici, "datanascita");	
		idcittadinanza = StringUtils.getAttributeStrNotNull(datiAnagrafici, "idcittadinanza");
		cittadinanza = StringUtils.getAttributeStrNotNull(datiAnagrafici, "cittadinanza");
		idcomune = StringUtils.getAttributeStrNotNull(datiAnagrafici, "idcomune");
	}
	
	//Dati domicilio	
	SourceBean datiDomicilio = (SourceBean) serviceResponse.getAttribute("M_GetDatiDomicilioAnteprimaCandidatura.ROWS.ROW");
	String idcomuneDom = "", cap = "";
	if (datiDomicilio != null){		
		idcomuneDom = StringUtils.getAttributeStrNotNull(datiDomicilio, "idcomune");
		cap = StringUtils.getAttributeStrNotNull(datiDomicilio, "cap");	
	}
	
	//Dati recapito
	SourceBean datiRecapito = (SourceBean) serviceResponse.getAttribute("M_GetDatiRecapitiAnteprimaCandidatura.ROWS.ROW");
	String indirizzo = "", telefono = "", cellulare = "", fax = "", email = "";
	if (datiRecapito != null){		
		indirizzo = StringUtils.getAttributeStrNotNull(datiRecapito, "indirizzo");
		telefono = StringUtils.getAttributeStrNotNull(datiRecapito, "telefono");	
		cellulare = StringUtils.getAttributeStrNotNull(datiRecapito, "cellulare");
		fax = StringUtils.getAttributeStrNotNull(datiRecapito, "fax");	
		email = StringUtils.getAttributeStrNotNull(datiRecapito, "email");
	}
	
	//Esperienza lavorative
	SourceBean datiEsperienze = null;
	if (numConfigCurr.equalsIgnoreCase(Properties.DEFAULT_CONFIG)) {
		datiEsperienze = (SourceBean) serviceResponse.getAttribute("M_GetDatiEsperienzaLavAnteprimaCandidaturaDefault.ROWS");
	}else{
		datiEsperienze = (SourceBean) serviceResponse.getAttribute("M_GetDatiEsperienzaLavAnteprimaCandidatura.ROWS");
	}
	Vector vectEsperienze = null;	
	if (datiEsperienze != null){		
		vectEsperienze = datiEsperienze.getAttributeAsVector("ROW");	
	}
	
	//Istruzione
	SourceBean datiIstruzione = (SourceBean) serviceResponse.getAttribute("M_GetDatiIstruzioneAnteprimaCandidatura.ROWS");
	Vector vectIstruzione = null;	
	if (datiIstruzione != null){		
		vectIstruzione = datiIstruzione.getAttributeAsVector("ROW");	
	}
	
	//Formazione
	SourceBean datiFormazione = (SourceBean) serviceResponse.getAttribute("M_GetDatiFormazioneAnteprimaCandidatura.ROWS");
	Vector vectFormazione = null;	
	if (datiFormazione != null){		
		vectFormazione = datiFormazione.getAttributeAsVector("ROW");	
	}
	
	//Dati Lingue Conosciute
	SourceBean datiLingue = (SourceBean) serviceResponse.getAttribute("M_GetDatiLingueAnteprimaCandidatura.ROWS");	
	Vector vectLingue = null;	
	if (datiLingue != null){
		vectLingue = datiLingue.getAttributeAsVector("ROW");
	}
	
	//Dati Conoscenze Informatiche
	SourceBean datiInformatica = (SourceBean) serviceResponse.getAttribute("M_GetDatiConoscenzeInformaticheAnteprimaCandidatura.ROWS");	
	Vector vectInformatica = null;	
	if (datiInformatica != null){
		vectInformatica = datiInformatica.getAttributeAsVector("ROW");
	}
	
	//Dati Albo
	SourceBean datiAlbo = null;
	if (numConfigCurr.equalsIgnoreCase(Properties.DEFAULT_CONFIG)) {
		datiAlbo = (SourceBean) serviceResponse.getAttribute("M_GetDatiAlbiAnteprimaCandidaturaDefault.ROWS");
	}else{
		datiAlbo = (SourceBean) serviceResponse.getAttribute("M_GetDatiAlbiAnteprimaCandidatura.ROWS");
	}
	Vector vectAlbo = null;		
	if (datiAlbo != null){
		vectAlbo = datiAlbo.getAttributeAsVector("ROW");
	}
	
	//Dati Patenti
	SourceBean datiPatenti = null;
	if (numConfigCurr.equalsIgnoreCase(Properties.DEFAULT_CONFIG)) {
		datiPatenti = (SourceBean) serviceResponse.getAttribute("M_GetDatiPatentiAnteprimaCandidaturaDefault.ROWS");
	}else{
		datiPatenti = (SourceBean) serviceResponse.getAttribute("M_GetDatiPatentiAnteprimaCandidatura.ROWS");
	}
	Vector vectPatenti = null;		
	if (datiPatenti != null){
		vectPatenti = datiPatenti.getAttributeAsVector("ROW");
	}	
	
	//Dati Patentini
	SourceBean datiPatentini = null; 
	if (numConfigCurr.equalsIgnoreCase(Properties.DEFAULT_CONFIG)) {
		datiPatentini = (SourceBean) serviceResponse.getAttribute("M_GetDatiPatentiniAnteprimaCandidaturaDefault.ROWS");
	}else{
		datiPatentini = (SourceBean) serviceResponse.getAttribute("M_GetDatiPatentiniAnteprimaCandidatura.ROWS");
	}
	Vector vectPatentini = null;		
	if (datiPatentini != null){
		vectPatentini = datiPatentini.getAttributeAsVector("ROW");
	}
	
	String mansioni = StringUtils.getAttributeStrNotNull(serviceResponse,"M_GetDatiMansioniAnteprimaCandidatura.MANSIONI");
	String profDesiderate = StringUtils.getAttributeStrNotNull(serviceResponse,"M_GetDatiMansioniAnteprimaCandidatura.PROFDESIDERATE");
	String orari = StringUtils.getAttributeStrNotNull(serviceResponse,"M_GetDatiMansioniAnteprimaCandidatura.ORARI");
	String contratti = StringUtils.getAttributeStrNotNull(serviceResponse,"M_GetDatiMansioniAnteprimaCandidatura.CONTRATTI");
	String mezzi = StringUtils.getAttributeStrNotNull(serviceResponse,"M_GetDatiMansioniAnteprimaCandidatura.MEZZI");
	String territorio = StringUtils.getAttributeStrNotNull(serviceResponse,"M_GetDatiMansioniAnteprimaCandidatura.TERRITORIO");
	
	boolean canModify = false;
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
	
	// Professioni desiderate
	SourceBean professioniDesiderateSB = (SourceBean) serviceResponse.getAttribute("M_GetDatiMansioniAnteprimaCandidatura.PROFESSIONIDESIDERATE");
	Vector professioniDesiderate = null;		
	if (professioniDesiderateSB != null){
		professioniDesiderate = professioniDesiderateSB.getAttributeAsVector("PROFESSIONEDESIDERATA");
	}
	
	//Annotazioni
	SourceBean annotazioni =  (SourceBean) serviceResponse.getAttribute("M_GetAnnotazioniAnteprimaCandidatura.ROWS.ROW");
	
%>

	<html>
	<head>
		<title>Anteprima invio candidatura ClicLavoro</title>
		<link rel="stylesheet" href="../../css/stili.css" type="text/css">
		<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
		<af:linkScript path="../../js/"/>
		<SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>	
										
	</head>		
		
	<body class="gestione" onload="rinfresca()">
		<p class="titolo">Dati Lavoratore</p>
			<% out.print(htmlStreamTop); %>
			<table class="main">
				<tr><td colspan="6"><div class="sezione">Dati Anagrafici</div></td></tr>

  				<tr>
  					<td class="etichetta2" valign="top">Codice Fiscale</td>
  					<td class="campo2" valign="top"><b><%= codicefiscale %></b></td>
  				</tr>
  				<tr>
  					<td class="etichetta2" valign="top">Cognome</td>
  					<td class="campo2" valign="top"><b><%= cognome %></b></td>
  					<td class="etichetta2" valign="top">Nome</td>
  					<td class="campo2" valign="top"><b><%= nome %></b></td>
  					<td class="etichetta2" valign="top">Sesso</td>
  					<td class="campo2" valign="top"><b><%= sesso %></b></td>
  				</tr>
  				<tr>
  					<td class="etichetta2" valign="top">Data di nascita</td>
  					<td class="campo2" valign="top"><b><%= datanascita %></b></td>
  					<td class="etichetta2" valign="top">Cittadinanza</td>
  					<td class="campo2" valign="top"><b><%= cittadinanza %></b></td>
  					<td class="etichetta2" valign="top">Comune</td>
  					<td class="campo2" valign="top"><b><af:comboBox classNameBase="input" name="descComu" title="Stato azienda" selectedValue="<%=idcomune%>" disabled="true" moduleName="M_GetListaComuniAnteprimaCandidatura" addBlank="true" /></b></td>
  				</tr>
  				<tr>
					<td></br></td>
  				</tr>
  				<tr><td colspan="6"><div class="sezione">Dati Domicilio</div></td></tr>
  				<tr>
  					<td class="etichetta2" valign="top">Indirizzo</td>
  					<td class="campo2" colspan="5" valign="top"><b><%= indirizzo %></b></td>
  				</tr>
  				<tr>
  					<td class="etichetta2" valign="top">Comune</td>
  					<td class="campo2" valign="top"><b><af:comboBox classNameBase="input" name="descComu" title="Stato azienda" selectedValue="<%=idcomuneDom%>" disabled="true" moduleName="M_GetListaComuniAnteprimaCandidatura" addBlank="true" /></b></td>
  					<td class="etichetta2" valign="top">CAP</td>
  					<td class="campo2" valign="top"><b><%= cap %></b></td>
  				</tr>
  				<tr>
					<td></br></td>
  				</tr>
  				<tr><td colspan="6"><div class="sezione">Dati Recapito</div></td></tr>
  				<tr>
  					<td class="etichetta2" valign="top">Telefono</td>
  					<td class="campo2" valign="top"><b><%= telefono %></b></td>
  					<td class="etichetta2" valign="top">Cellulare</td>
  					<td class="campo2" valign="top"><b><%= cellulare %></b></td>
  				</tr>
  				<tr>
  					<td class="etichetta2" valign="top">Fax</td>
  					<td class="campo2" valign="top"><b><%= fax %></b></td>
  					<td class="etichetta2" valign="top">Email</td>
  					<td class="campo2" colspan="3" valign="top"><b><%= email %></b></td>
  				</tr>
  			</table>
			<% out.print(htmlStreamBottom); %>		
			
		<p class="titolo">Dati Curriculari</p>	
			<% out.print(htmlStreamTop); %>
			<table class="main" border="0">
				<% 
					String tipoEsperienzaDesc = "", qualificaSvolta = "", descrQualificaSvolta = "", principaliMansioni = "", nomeDatore = "";
					String dataInizio = "", dataFine = "", indirizzoDatore = "";
					String codiceAteco = "", descrAteco = "", cfAzienda = "", tipoRapporto = "", areaLavoro = "", motivoCessazione ="", altroMotivoCessazione = "";
					String flgTirocinioEsp = "";
	  				SourceBean rigaEsperienze = null;
	  				
	  				if(datiEsperienze != null){
	  				%>
	  				
					<tr><td colspan="6"><div class="sezione">Esperienze Lavorative</div></td></tr>
  					<%
  					if (numConfigCurr.equalsIgnoreCase(Properties.DEFAULT_CONFIG)) {
  						for(int i=0; i < vectEsperienze.size(); i++){
  	  						rigaEsperienze = (SourceBean) vectEsperienze.elementAt(i);
  	  						tipoEsperienzaDesc = StringUtils.getAttributeStrNotNull(rigaEsperienze, "tipoesperienzadesc");
  	  						descrQualificaSvolta = StringUtils.getAttributeStrNotNull(rigaEsperienze, "descrQualificaSvolta");	
  	  						principaliMansioni = StringUtils.getAttributeStrNotNull(rigaEsperienze, "principaliMansioni");
  	  						nomeDatore = StringUtils.getAttributeStrNotNull(rigaEsperienze, "nomeDatore");	
  	  						dataInizio = StringUtils.getAttributeStrNotNull(rigaEsperienze, "dataInizio");
  	  						dataFine = StringUtils.getAttributeStrNotNull(rigaEsperienze, "dataFine");
  	  						if(StringUtils.isFilledNoBlank(dataFine)){
	  	  						dataInizio = dataInizio + " - " + dataFine;
	  						}
  	  						indirizzoDatore = StringUtils.getAttributeStrNotNull(rigaEsperienze, "indirizzoDatore");		
  		  					%>
  		  					 
			  				<tr>
								<td colspan="6">
									<table width="100%">
							  			<tr>
						  					<td class="etichetta" valign="top">Tipo di contratto</td>
						  					<td class="campo2" valign="top" colspan="3"><b><%= tipoEsperienzaDesc %></b></td>
						  				</tr>	
						  				<tr>
						  					<td class="etichetta" valign="top">Qualifica svolta</td>
						  					<td class="campo2" valign="top" colspan="3"><b><%= descrQualificaSvolta %></b></td>
						  				</tr>
						  				<tr>
						  					<td class="etichetta" valign="top">Descrizione attività</td>
						  					<td class="campo2" valign="top" colspan="3"><b><%= principaliMansioni %></b></td>
						  				</tr>
						  				<tr>
						  					<td class="etichetta" valign="top">Data Inizio - Data Fine</td>
						  					<td class="campo2" valign="top"><b><%= dataInizio %></b></td>
						  				</tr>
						  				<tr>
						  					<td class="etichetta" valign="top">Nome Datore di Lavoro</td>
						  					<td class="campo2" valign="top"><b><%= nomeDatore %></b></td>
						  				</tr>
						  				<tr>
						  					<td class="etichetta" valign="top">Indirizzo</td>
						  					<td class="campo2" valign="top"><b><%= indirizzoDatore %></b></td>
						  				</tr>
					  				</table>
	  							</td>
		  					</tr>	  				
  	 					<% }
  					}
  					else {
  						for(int i=0; i < vectEsperienze.size(); i++){
  	  						rigaEsperienze = (SourceBean) vectEsperienze.elementAt(i);
  	  						tipoEsperienzaDesc = StringUtils.getAttributeStrNotNull(rigaEsperienze, "tipoesperienzadesc");
  	  						descrQualificaSvolta = StringUtils.getAttributeStrNotNull(rigaEsperienze, "descrQualificaSvolta");	
  	  						principaliMansioni = StringUtils.getAttributeStrNotNull(rigaEsperienze, "principaliMansioni");
  	  						nomeDatore = StringUtils.getAttributeStrNotNull(rigaEsperienze, "nomeDatore");	
  	  						dataInizio = StringUtils.getAttributeStrNotNull(rigaEsperienze, "dataInizio");
  	  						dataFine = StringUtils.getAttributeStrNotNull(rigaEsperienze, "dataFine");
  	  						if(StringUtils.isFilledNoBlank(dataFine)){
	  	  						dataInizio = dataInizio + " - " + dataFine;
	  						}
  	  						indirizzoDatore = StringUtils.getAttributeStrNotNull(rigaEsperienze, "indirizzoDatore");	
  	  						cfAzienda = StringUtils.getAttributeStrNotNull(rigaEsperienze, "Strcodfiscaleazienda");	
  	  						areaLavoro = StringUtils.getAttributeStrNotNull(rigaEsperienze, "areaLavoro");	
  	  						tipoRapporto = StringUtils.getAttributeStrNotNull(rigaEsperienze, "tipoRapporto");	
  	  						codiceAteco = StringUtils.getAttributeStrNotNull(rigaEsperienze, "Codatecodot");	
  	  						descrAteco = StringUtils.getAttributeStrNotNull(rigaEsperienze, "descrAteco");	
  	  						if(StringUtils.isFilledNoBlank(codiceAteco) && StringUtils.isFilledNoBlank(descrAteco)){
  	  							descrAteco = codiceAteco + " - " + descrAteco;
  	  						}
  	  						motivoCessazione = StringUtils.getAttributeStrNotNull(rigaEsperienze, "motivocessazione");	
  	  						altroMotivoCessazione = StringUtils.getAttributeStrNotNull(rigaEsperienze, "Strmotcessazione");	
  	  						flgTirocinioEsp = StringUtils.getAttributeStrNotNull(rigaEsperienze, "flgTirocini");
  	  						if (flgTirocinioEsp.equalsIgnoreCase("N")) {
	  		  					%>	
	  			  				
	  			  				<tr>
	  								<td colspan="6">
	  									<table width="100%">
	  							  			<tr>
	  						  					<td class="etichetta" valign="top">Tipo di contratto</td>
	  						  					<td class="campo2" valign="top" colspan="3"><b><%= tipoEsperienzaDesc %></b></td>
	  						  				</tr>	
	  						  				<tr>
	  						  					<td class="etichetta" valign="top">Qualifica svolta</td>
	  						  					<td class="campo2" valign="top" colspan="3"><b><%= descrQualificaSvolta %></b></td>
	  						  				</tr>
	  						  				<tr>
	  						  					<td class="etichetta" valign="top">Descrizione attività</td>
	  						  					<td class="campo2" valign="top" colspan="3"><b><%= principaliMansioni %></b></td>
	  						  				</tr>
	  						  				<tr>
	  						  					<td class="etichetta" valign="top">Area lavoro</td>
	  						  					<td class="campo2" valign="top" colspan="3"><b><%= areaLavoro %></b></td>
	  						  				</tr>
	  						  				<tr>
	  						  					<td class="etichetta" valign="top">Tipo di rapporto</td>
	  						  					<td class="campo2" valign="top" colspan="3"><b><%= tipoRapporto %></b></td>
	  						  				</tr>
	  						  				<tr>
	  						  					<td class="etichetta" valign="top">Data Inizio - Data Fine</td>
	  						  					<td class="campo2" valign="top"><b><%= dataInizio %></b></td>
	  						  				</tr>
	  						  				<tr>
	  						  					<td class="etichetta" valign="top">Nome Datore di Lavoro</td>
	  						  					<td class="campo2" valign="top"><b><%= nomeDatore %></b></td>
	  						  				</tr>
	  						  				<tr>
	  						  					<td class="etichetta" valign="top">Codice e descrizione ATECO</td>
	  						  					<td class="campo2" valign="top"><b><%= descrAteco %></b></td>
	  						  				</tr>
	  						  				<tr>
	  						  					<td class="etichetta" valign="top">Codice Fiscale Azienda</td>
	  						  					<td class="campo2" valign="top"><b><%= cfAzienda %></b></td>
	  						  				</tr>
	  						  				<tr>
	  						  					<td class="etichetta" valign="top">Indirizzo</td>
	  						  					<td class="campo2" valign="top"><b><%= indirizzoDatore %></b></td>
	  						  				</tr>
	  						  				<tr>
	  						  					<td class="etichetta" valign="top">Motivo cessazione rapporto</td>
	  						  					<td class="campo2" valign="top"><b><%= motivoCessazione %></b></td>
	  						  				</tr>
	  						  				<tr>
	  						  					<td class="etichetta" valign="top">Altre eventuali motivazioni</td>
	  						  					<td class="campo2" valign="top"><b><%= altroMotivoCessazione %></b></td>
	  						  				</tr>
	  					  				</table>
	  	  							</td>
	  			  				</tr>	 
	  			  				<%if(i != vectEsperienze.size()-1){ %>
	  			  				<tr><td colspan="6"><div class="sezione"></div></td></tr>
	  			  			<%
	  			  				}
	  			  				}
  							}
  						%>
  						<tr valign="bottom">	  				
							<td></br></td>
	  					</tr>
  						<tr><td colspan="6"><div class="sezione">Esperienze Non Lavorative</div></td></tr>
	  					<%
  						for(int i=0; i < vectEsperienze.size(); i++){
  	  						rigaEsperienze = (SourceBean) vectEsperienze.elementAt(i);
  	  						descrQualificaSvolta = StringUtils.getAttributeStrNotNull(rigaEsperienze, "descrQualificaSvolta");	
  	  						principaliMansioni = StringUtils.getAttributeStrNotNull(rigaEsperienze, "principaliMansioni");
  	  						nomeDatore = StringUtils.getAttributeStrNotNull(rigaEsperienze, "nomeDatore");	
  	  						dataInizio = StringUtils.getAttributeStrNotNull(rigaEsperienze, "dataInizio");
  	  						dataFine = StringUtils.getAttributeStrNotNull(rigaEsperienze, "dataFine");
  	  						if(StringUtils.isFilledNoBlank(dataFine)){
  	  	  						dataInizio = dataInizio + " - " + dataFine;
  	  						}
  	  						indirizzoDatore = StringUtils.getAttributeStrNotNull(rigaEsperienze, "indirizzoDatore");	
  	  						cfAzienda = StringUtils.getAttributeStrNotNull(rigaEsperienze, "Strcodfiscaleazienda");	
  	  						areaLavoro = StringUtils.getAttributeStrNotNull(rigaEsperienze, "areaLavoro");	
  	  						tipoRapporto = StringUtils.getAttributeStrNotNull(rigaEsperienze, "tipoRapporto");	
  	  						codiceAteco = StringUtils.getAttributeStrNotNull(rigaEsperienze, "Codatecodot");	
  	  						descrAteco = StringUtils.getAttributeStrNotNull(rigaEsperienze, "descrAteco");	
  	  						if(StringUtils.isFilledNoBlank(codiceAteco) && StringUtils.isFilledNoBlank(descrAteco)){
  	  							descrAteco = codiceAteco + " - " + descrAteco;
  	  						}
  	  						motivoCessazione = StringUtils.getAttributeStrNotNull(rigaEsperienze, "motivocessazione");	
  	  						altroMotivoCessazione = StringUtils.getAttributeStrNotNull(rigaEsperienze, "Strmotcessazione");	
  	  						flgTirocinioEsp = StringUtils.getAttributeStrNotNull(rigaEsperienze, "flgTirocini");
  	  						int found = 0;
  	  						if (flgTirocinioEsp.equalsIgnoreCase("S")) {
  	  							
  	  							if(found > 0 && i != vectEsperienze.size()-1){ %>
	  			  					<tr><td colspan="6"><div class="sezione"></div></td></tr>
	  		  					<%} 
	  	  						found += 1;
	  		  					
	  		  					%>	
	  			  				 
	  			  				<tr>
	  								<td colspan="6">
	  									<table width="100%">	
	  						  				<tr>
	  						  					<td class="etichetta" valign="top">Qualifica svolta</td>
	  						  					<td class="campo2" valign="top" colspan="3"><b><%= descrQualificaSvolta %></b></td>
	  						  				</tr>
	  						  				<tr>
	  						  					<td class="etichetta" valign="top">Descrizione attività</td>
	  						  					<td class="campo2" valign="top" colspan="3"><b><%= principaliMansioni %></b></td>
	  						  				</tr>
	  						  				<tr>
	  						  					<td class="etichetta" valign="top">Area lavoro</td>
	  						  					<td class="campo2" valign="top" colspan="3"><b><%= areaLavoro %></b></td>
	  						  				</tr>
	  						  				<tr>
	  						  					<td class="etichetta" valign="top">Tipo di rapporto</td>
	  						  					<td class="campo2" valign="top" colspan="3"><b><%= tipoRapporto %></b></td>
	  						  				</tr>
	  						  				<tr>
	  						  					<td class="etichetta" valign="top">Data Inizio - Data Fine</td>
	  						  					<td class="campo2" valign="top"><b><%= dataInizio %></b></td>
	  						  				</tr>
	  						  				<tr>
	  						  					<td class="etichetta" valign="top">Nome Datore di Lavoro</td>
	  						  					<td class="campo2" valign="top"><b><%= nomeDatore %></b></td>
	  						  				</tr>
	  						  				<tr>
	  						  					<td class="etichetta" valign="top">Codice e descrizione ATECO</td>
	  						  					<td class="campo2" valign="top"><b><%= descrAteco %></b></td>
	  						  				</tr>
	  						  				<tr>
	  						  					<td class="etichetta" valign="top">Codice Fiscale Azienda</td>
	  						  					<td class="campo2" valign="top"><b><%= cfAzienda %></b></td>
	  						  				</tr>
	  						  				<tr>
	  						  					<td class="etichetta" valign="top">Indirizzo</td>
	  						  					<td class="campo2" valign="top"><b><%= indirizzoDatore %></b></td>
	  						  				</tr>
	  						  				<tr>
	  						  					<td class="etichetta" valign="top">Motivo cessazione rapporto</td>
	  						  					<td class="campo2" valign="top"><b><%= motivoCessazione %></b></td>
	  						  				</tr>
	  						  				<tr>
	  						  					<td class="etichetta" valign="top">Altre eventuali motivazioni</td>
	  						  					<td class="campo2" valign="top"><b><%= altroMotivoCessazione %></b></td>
	  						  				</tr>
	  					  				</table>
	  	  							</td>
	  			  				</tr>	 
	  			  				
	  			  			<%
	  			  			 }
  							}
  						}
	  				}
	  				
	  				String titoloCorso = "", idSedeCorso = "", durata = "", idTipologiaDurata = "", idAttestazione = "", tipoDurata = "";
	  				String codiceCorso ="", annoForm ="", descrCors ="", contenCorso="", corsoCompletato ="", idAttestazioneSil = "", ambitoDisc ="";
	  				SourceBean rigaFormazione = null;
	  				
	  				if(datiFormazione != null){ %>
		  				
		  			<tr valign="bottom">	  				
						<td></br></td>
	  				</tr> 
					<tr><td colspan="6"><div class="sezione">Formazione</div></td></tr>
	  				<%
  					for(int i=0; i < vectFormazione.size(); i++){
  						rigaFormazione = (SourceBean) vectFormazione.elementAt(i);
  						codiceCorso = StringUtils.getAttributeStrNotNull(rigaFormazione, "codice");
  						titoloCorso = StringUtils.getAttributeStrNotNull(rigaFormazione, "titoloCorso");
  						annoForm = StringUtils.getAttributeStrNotNull(rigaFormazione, "annoform");
  						descrCors = StringUtils.getAttributeStrNotNull(rigaFormazione, "descrizione");
  						contenCorso = StringUtils.getAttributeStrNotNull(rigaFormazione, "contenuto");
  						idSedeCorso = StringUtils.getAttributeStrNotNull(rigaFormazione, "idSede");	
  						durata = StringUtils.getAttributeStrNotNull(rigaFormazione, "durata");
  						idTipologiaDurata = StringUtils.getAttributeStrNotNull(rigaFormazione, "idTipologiaDurata");	
  						idAttestazione = StringUtils.getAttributeStrNotNull(rigaFormazione, "idAttestazione");		
  						corsoCompletato  = StringUtils.getAttributeStrNotNull(rigaFormazione, "completato");		
  						idAttestazioneSil  = StringUtils.getAttributeStrNotNull(rigaFormazione, "idattestazionesil");	
  						ambitoDisc  = StringUtils.getAttributeStrNotNull(rigaFormazione, "cdnambitodisciplinare");	
  						if("O".equals(idTipologiaDurata)){
  							tipoDurata = "Ore";
  						}else if("M".equals(idTipologiaDurata)){
  							tipoDurata = "Mesi";
  						}else{
  							tipoDurata = "";
  						}
	  					%>	
		  			 
		  				<tr>
							<td colspan="6">
								<table width="100%">
									<tr>
					  					<td class="etichetta" valign="top">Codice</td>
					  					<td class="campo2" valign="top"><b><%= codiceCorso %></b></td>
					  				</tr>
						  			<tr>
					  					<td class="etichetta" valign="top">Tipo Corso</td>
					  					<td class="campo2" valign="top"><b><%= titoloCorso %></b></td>
					  				</tr>	
					  				<tr>
					  					<td class="etichetta" valign="top">Anno</td>
					  					<td class="campo2" valign="top"><b><%= annoForm %></b></td>
					  				</tr>	
					  				<tr>
					  					<td class="etichetta" valign="top">Descrizione</td>
					  					<td class="campo2" valign="top"><b><%= descrCors %></b></td>
					  				</tr>	
					  				<tr>
					  					<td class="etichetta" valign="top">Contenuto</td>
					  					<td class="campo2" valign="top"><b><%= contenCorso %></b></td>
					  				</tr>	
					  				<tr>
					  					<td class="etichetta" valign="top">Sede</td>
					  					<td class="campo2" valign="top"><b><af:comboBox classNameBase="input" name="descComu<%=i%>" title="Stato azienda" selectedValue="<%=idSedeCorso%>" disabled="true" moduleName="M_GetListaComuniAnteprimaCandidatura" addBlank="true" /></b></td>
					  				</tr>
					  				<tr>
					  					<td class="etichetta" valign="top">Completato</td>
					  					<td class="campo2" valign="top"><b><%= corsoCompletato %></b></td>
					  				</tr>
					  				<tr>
					  					<td class="etichetta" valign="top">Tipo Certificazione</td>
					  					<td class="campo2" valign="top"><b><af:comboBox classNameBase="input" name="descCerti<%=i%>" title="Tipo Certificazione" selectedValue="<%=idAttestazioneSil%>" disabled="true" moduleName="M_ListForProTipoCorso" addBlank="true" /></b></td>
					  				</tr>
					  				<tr>
					  					<td class="etichetta" valign="top">Ambito Disciplinare</td>
					  					<td class="campo2" valign="top"><b><af:comboBox classNameBase="input" name="ambitoD<%=i%>" title="Ambito Disciplinare" selectedValue="<%=ambitoDisc%>" disabled="true" moduleName="M_ListForProAmbDiscip" addBlank="true" /></b></td>
					  				</tr>
					  			
				  				</table>
  							</td>
		  				</tr>	  				
		  					<%if(i != vectFormazione.size()-1){ %>
	  			  				<tr><td colspan="6"><div class="sezione"></div></td></tr>
 					<% } 
  					} 
	  				}
	  				

	  				String titolostudio = "", descrizioneistruzione = "", votazione = "";
	  				//nuovi campi gennaio 2018 Borriello
	  				String specificaStudio = "", statoStudio = "", annoStudio = "", nomeIstituto="", codComuneIstituto = "", comuneIstituto ="";
	  				SourceBean rigaIstruzione = null;
	  				
	  				if(datiIstruzione != null){
	  					if(numConfigCurr.equalsIgnoreCase(Properties.DEFAULT_CONFIG)){
	  		  				%>
	  		  				<tr valign="bottom">	  				
	  							<td></br></td>
	  		  				</tr>
	  		  				<tr><td colspan="6"><div class="sezione">Titoli di Studio</div></td></tr>
	  	  					<tr>
	  	  					<td class="etichetta" valign="top">Titoli di studio</td>
	  	  					<td class="campo2" valign="top"><b>
	  	  					<%
	  	  					for(int i=0; i < vectIstruzione.size(); i++){
	  	  						rigaIstruzione = (SourceBean) vectIstruzione.elementAt(i);
	  	  						titolostudio = StringUtils.getAttributeStrNotNull(rigaIstruzione, "titolostudio");
	  	  						descrizioneistruzione = StringUtils.getAttributeStrNotNull(rigaIstruzione, "descrizioneistruzione");
	  	  						votazione = StringUtils.getAttributeStrNotNull(rigaIstruzione, "votazione");			  									  						
	  		  					%>
	  		  					<%=descrizioneistruzione%> <%=votazione%><%if (i < (vectIstruzione.size() - 1)) { %>, <%}%>
	  				  			 		
	  	 					<% } %>
	  	 					</b></td></tr>
	  	 					
	  		  				<% } else {%>
	  						<tr valign="bottom">	  				
								<td></br></td>
			  				</tr>
			  				<tr><td colspan="6"><div class="sezione">Titoli di Studio</div></td></tr>
		  					<tr>
							<td colspan="6">
								<table width="100%">
	  						<%
		  					for(int i=0; i < vectIstruzione.size(); i++){
		  						rigaIstruzione = (SourceBean) vectIstruzione.elementAt(i);
		  						titolostudio = StringUtils.getAttributeStrNotNull(rigaIstruzione, "titolostudio");
		  						descrizioneistruzione = StringUtils.getAttributeStrNotNull(rigaIstruzione, "descrizioneistruzione");
		  						votazione = StringUtils.getAttributeStrNotNull(rigaIstruzione, "votazione");			
		  						specificaStudio = StringUtils.getAttributeStrNotNull(rigaIstruzione, "specifica");		
		  						statoStudio = StringUtils.getAttributeStrNotNull(rigaIstruzione, "descrStato");		
		  						BigDecimal annoCons =(BigDecimal) rigaIstruzione.getAttribute("annoistr");
		  						if(annoCons!=null){
		  							annoStudio = annoCons.toPlainString();		
		  						}
		  						nomeIstituto = StringUtils.getAttributeStrNotNull(rigaIstruzione, "nomeistituto");		
		  						codComuneIstituto = StringUtils.getAttributeStrNotNull(rigaIstruzione, "codcomistituto");		
		  						comuneIstituto = StringUtils.getAttributeStrNotNull(rigaIstruzione, "comistituto");
			  					%>
			  					 
			  					<tr>
				  					<td class="etichetta" valign="top">Titolo di studio</td>
				  					<td class="campo2" valign="top"><b><%= descrizioneistruzione %></b></td>
				  				</tr>
					  			<tr>
				  					<td class="etichetta" valign="top">Votazione</td>
				  					<td class="campo2" valign="top"><b><%= votazione %></b></td>
				  				</tr>
				  				<tr>
				  					<td class="etichetta" valign="top">Specifica</td>
				  					<td class="campo2" valign="top"><b><%= specificaStudio %></b></td>
				  				</tr>
					  			<tr>
				  					<td class="etichetta" valign="top">Stato</td>
				  					<td class="campo2" valign="top"><b><%= statoStudio %></b></td>
				  				</tr>
				  				<tr>
				  					<td class="etichetta" valign="top">Anno di conseguimento</td>
				  					<td class="campo2" valign="top"><b><%= annoStudio %></b></td>
				  				</tr>
					  			<tr>
				  					<td class="etichetta" valign="top">Istituto scolastico e localit&agrave;</td>
				  					<td class="campo2" valign="top"><b><%= nomeIstituto %>&nbsp;(<%=comuneIstituto %>)</b></td>
				  				</tr>  	
				  				<%if(i != vectIstruzione.size()-1){ %>
	  			  				<tr><td colspan="6"><div class="sezione"></div></td></tr>
				  							
		 					<% } 
		 					}%>
		 					</table>
	 						</td></tr>
		  				<% } 
	  				}
	  				
	  				String strLingua = "", idLivelloLetto = "", idLivelloScritto = "", idLivelloParlato = "";
	  				String strLivelloLetto = "", strLivelloScritto = "", strLivelloParlato = "";
	  				String certificatoLingua = "", modalitaLingua = "", altraModalitaLingua = "";
	  				SourceBean rigaLingua = null;
	  				
	  				if(datiLingue != null){
	  				%>
	  				<tr valign="bottom">	  				
						<td></br></td>
	  				</tr>
	  				<tr><td colspan="6"><div class="sezione">Conoscenze Linguistiche</div></td></tr>
	  				<tr>
	  					<td colspan="6">
	  						<table width="100%">
	  							<%if (numConfigCurr.equalsIgnoreCase(Properties.DEFAULT_CONFIG)) {%>
	  								<tr>
						  				<td align="right" class="etichetta">Lingua</td>
						  				<td align="center">Livello Letto</td>
						  				<td align="center">Livello Scritto</td>
						  				<td align="center">Livello Parlato</td>	
						  			</tr>  				
					  				<%
				  					for(int i=0; i < vectLingue.size(); i++){
				  						rigaLingua = (SourceBean) vectLingue.elementAt(i);
				  						strLingua = rigaLingua.getAttribute("strLingua").toString();
				  						idLivelloLetto = rigaLingua.getAttribute("idLivelloLetto").toString();
				  						idLivelloScritto = rigaLingua.getAttribute("idLivelloScritto").toString();
				  						idLivelloParlato = rigaLingua.getAttribute("idLivelloParlato").toString(); 
					  				%>	
						  			<tr>
						  				<td align="right" class="etichetta"><b><%= strLingua %></b></td>
						  				<td align="center"><b><%= idLivelloLetto %></b></td>
						  				<td align="center"><b><%= idLivelloScritto %></b></td>
						  				<td align="center"><b><%= idLivelloParlato %></b></td>
						  			</tr>
				 					<% 	} %>
	  							<% } else {%>
	  								<tr>
						  				<td align="right" class="etichetta">Lingua</td>
						  				<td align="center">Livello Letto</td>
						  				<td align="center">Livello Scritto</td>
						  				<td align="center">Livello Parlato</td>	
						  				<td align="center">Conoscenza Certificata</td>	
						  				<td align="center">Modalit&agrave;</td>	
						  				<td align="center">Altra modalit&agrave;</td>	
						  			</tr>  				
					  				<%
				  					for(int i=0; i < vectLingue.size(); i++){
				  						rigaLingua = (SourceBean) vectLingue.elementAt(i);
				  						strLingua = rigaLingua.getAttribute("strLingua").toString();
				  						strLivelloLetto = rigaLingua.getAttribute("silGradoLetto").toString();
				  						strLivelloParlato = rigaLingua.getAttribute("silGradoparlato").toString();
				  						strLivelloScritto = rigaLingua.getAttribute("silGradoscritto").toString();
				  						certificatoLingua =StringUtils.getAttributeStrNotNull(rigaLingua, "CERTIFICATO"); 
				  						modalitaLingua = StringUtils.getAttributeStrNotNull(rigaLingua, "MODALITA");
				  						altraModalitaLingua = StringUtils.getAttributeStrNotNull(rigaLingua, "ALTRAMODALITA"); 
					  				%>	
						  			<tr>
						  				<td align="right" class="etichetta"><b><%= strLingua %></b></td>
						  				<td align="center"><b><%= strLivelloLetto %></b></td>
						  				<td align="center"><b><%= strLivelloScritto %></b></td>
						  				<td align="center"><b><%= strLivelloParlato %></b></td>
						  				<td align="center"><b><%= certificatoLingua %></b></td>
						  				<td align="center"><b><%= modalitaLingua %></b></td>
						  				<td align="center"><b><%= altraModalitaLingua %></b></td>
						  			</tr>
				 					<% 	} %>
	  							<% } %>
 							</table>
	  					</td>
	  				</tr>
  					<% } 

  			 
  				String desInformatica = "";
				String tipo = "", tipo_prec = "";
				boolean newLine = false, primaRiga = false;
				SourceBean rigaInformatica = null;
			
				if(vectInformatica != null){ %>
					<tr valign="bottom">	  				
						<td></br></td>
	  				</tr>
	  				<tr><td colspan="6"><div class="sezione">Conoscenze Informatiche</div></td></tr>			
				<%
				if(vectInformatica.size()>0){
					rigaInformatica = (SourceBean) vectInformatica.elementAt(0);
					tipo_prec = rigaInformatica.getAttribute("tipoconoscenza").toString();
					desInformatica = rigaInformatica.getAttribute("tipoconoscenza").toString() + ": " + rigaInformatica.getAttribute("strdescrizione").toString();
					%>
					<tr>
		  					<td class="etichetta">Tipo conoscenza informatica</td>
		  			<%
		  				if(vectInformatica.size()==1){
		  			%>
		  				<td class="campo2" colspan="3"><b><%= desInformatica %></b></td>
		  				</tr>		
					<%
					}
				}
					for(int i=1; i < vectInformatica.size(); i++){
						
						rigaInformatica = (SourceBean) vectInformatica.elementAt(i);
						 
						tipo = rigaInformatica.getAttribute("tipoconoscenza").toString();

						if(tipo.equals(tipo_prec)){
 							newLine = false;
							desInformatica = desInformatica + ", " + rigaInformatica.getAttribute("strdescrizione").toString();
						}else{
 							newLine = true;
						}
						tipo_prec = tipo;

						if(newLine && i != vectInformatica.size()-1){ %>
						
				  			<td class="campo2" colspan="3"><b><%= desInformatica %></b></td>
		  				</tr>
		  				<tr>
		  					<td class="etichetta">Tipo conoscenza informatica</td>
						<% 
						//aggiorno descrizione
						desInformatica = rigaInformatica.getAttribute("tipoconoscenza").toString() + ": " + rigaInformatica.getAttribute("strdescrizione").toString();
						
						}else if(i== vectInformatica.size()-1) { %>
								<td class="campo2" colspan="3"><b><%= desInformatica %></b></td>
		  				</tr>
					  			
						<%
						//aggiorno descrizione
						desInformatica = rigaInformatica.getAttribute("tipoconoscenza").toString() + ": " + rigaInformatica.getAttribute("strdescrizione").toString();

						}
						 	  					
					}
				} %>
	  				<% if (numConfigCurr.equalsIgnoreCase(Properties.DEFAULT_CONFIG)) {
	  				
	  					String albi = "";
		  				SourceBean rigaAlbo = null;
		  				
		  				if(datiAlbo != null){
	  				%>
	  					<tr valign="bottom">	  				
						<td></br></td>
	  				</tr>
  					<%
	  					for(int i=0; i < vectAlbo.size(); i++){
	  						rigaAlbo = (SourceBean) vectAlbo.elementAt(i);
	  						if (albi.equals("")){
	  							albi = rigaAlbo.getAttribute("descrizione").toString();
							}else{
								albi = albi + ", " + rigaAlbo.getAttribute("descrizione").toString();
							}		  									  						
		  				} 
	  				} %>
	  				
					<tr><td colspan="6"><div class="sezione">Abilitazioni, Patenti</div></td></tr>			

	  				<tr>
	  					<td class="etichetta" valign="top">Albi Professionali</td>
			  			<td class="campo2" colspan="3" valign="top"><b><%= albi %></b></td>
	  				</tr>
	  				
	  				<% 				
  					String patenti = "";
  					SourceBean rigaPatenti = null;
  				
  					if(vectPatenti != null){
						for(int i=0; i < vectPatenti.size(); i++){
							rigaPatenti = (SourceBean) vectPatenti.elementAt(i);
							if (patenti.equals("")){
								patenti = rigaPatenti.getAttribute("descrizione").toString();
							}else{
								patenti = patenti + ", " + rigaPatenti.getAttribute("descrizione").toString();
							}
						}
  					}
	  				%>
	  				 
	  				<tr>
	  					<td class="etichetta" valign="top">Patenti di guida</td>
			  			<td class="campo2" colspan="3" valign="top"><b><%= patenti %></b></td>
	  				</tr>
	  				<%   						  				
  					String patentini = "";
  					SourceBean rigaPatentini = null;
  				
  					if(vectPatentini != null){
						for(int i=0; i < vectPatentini.size(); i++){
							rigaPatentini = (SourceBean) vectPatentini.elementAt(i);
							if (patentini.equals("")){
								patentini = rigaPatentini.getAttribute("descrizione").toString();
							}else{
								patentini = patentini + ", " + rigaPatentini.getAttribute("descrizione").toString();
							}
						}
  					}
	  				%>
	  				 
	  				<tr>
	  					<td class="etichetta" valign="top">Patentini</td>
			  			<td class="campo2" colspan="3" valign="top"><b><%= patentini %></b></td>
	  				</tr>
	  				
	  				<%}else{
	  					if(datiAlbo != null || datiPatenti!= null || datiPatentini!= null){
	  				
	  				%>
	  				
	  				<tr valign="bottom">	  				
						<td></br></td>
	  				</tr>
	  				<tr><td colspan="6"><div class="sezione">Abilitazioni, Patenti</div></td></tr>			

	  				
	  				<%
	  				if(vectAlbo !=null){
	  				%>
	  				<tr>
							<td colspan="6">
								<table width="100%">
	  						<%
		  					String descrAlbo = "", noteAlbo = "";
							SourceBean rigaAlbo = null;

								for(int i=0; i < vectAlbo.size(); i++){
									rigaAlbo = (SourceBean) vectAlbo.elementAt(i);
									descrAlbo = StringUtils.getAttributeStrNotNull(rigaAlbo, "descrizione");
									noteAlbo = StringUtils.getAttributeStrNotNull(rigaAlbo, "strnote");
			  					%>
			  					 
			  					<tr>
				  					<td class="etichetta" valign="top">Albo Professionale</td>
				  					<td class="campo2" valign="top"><b><%= descrAlbo %></b></td>
				  				</tr>
					  			<tr>
				  					<td class="etichetta" valign="top">Note</td>
				  					<td class="campo2" valign="top"><b><%= noteAlbo %></b></td>
				  				</tr> 				
		 					<% } %>
		 					</table>
	 						</td></tr>
	  				<% } %>
	  				
	  				<%
	  				if(vectPatenti !=null){
	  				%>
	  				<tr>
							<td colspan="6">
								<table width="100%">
	  						<%
		  						String descrPatente = "", notePatente = "";
	  							SourceBean rigaPatenti = null;
	  				
		  					for(int i=0; i < vectPatenti.size(); i++){
		  						rigaPatenti = (SourceBean) vectPatenti.elementAt(i);
		  						descrPatente = StringUtils.getAttributeStrNotNull(rigaPatenti, "descrizione");
		  						notePatente = StringUtils.getAttributeStrNotNull(rigaPatenti, "strnote");
	  						
			  					%>
			  					 
			  					<tr>
				  					<td class="etichetta" valign="top">Patente</td>
				  					<td class="campo2" valign="top"><b><%= descrPatente %></b></td>
				  				</tr>
					  			<tr>
				  					<td class="etichetta" valign="top">Note</td>
				  					<td class="campo2" valign="top"><b><%= notePatente %></b></td>
				  				</tr> 				
				  				<%if(i != vectPatenti.size()-1){ %>
	  			  				<tr><td colspan="6"><div class="sezione"></div></td></tr>
		 					<% } 
		 					} %>
		 					</table>
	 						</td></tr>
	  				<% } %>
	  				
	  			 	<%
	  				if(vectPatentini !=null){
	  				%>
	  				<tr>
							<td colspan="6">
								<table width="100%">
	  						<%
	  						String descrPatentino = "", notePatentino = "";
		  					SourceBean rigaPatentini = null;
	  				
	  					for(int i=0; i < vectPatentini.size(); i++){
	  						rigaPatentini = (SourceBean) vectPatentini.elementAt(i);
	  						descrPatentino = StringUtils.getAttributeStrNotNull(rigaPatentini, "descrizione");
	  						notePatentino = StringUtils.getAttributeStrNotNull(rigaPatentini, "strnote");
	  						
	  						
			  					%>
			  					 
			  					<tr>
				  					<td class="etichetta" valign="top">Patentino</td>
				  					<td class="campo2" valign="top"><b><%= descrPatentino %></b></td>
				  				</tr>
					  			<tr>
				  					<td class="etichetta" valign="top">Note</td>
				  					<td class="campo2" valign="top"><b><%= notePatentino %></b></td>
				  				</tr> 	
				  				<%if(i != vectPatentini.size()-1){ %>
	  			  				<tr><td colspan="6"><div class="sezione"></div></td></tr>			
		 					<% }
				  			}%>
		 					</table>
	 						</td></tr>
	  				<% 	}
		  			  }
	  			 	}%>
	  			 
	  			  <%if (numConfigCurr.equalsIgnoreCase(Properties.DEFAULT_CONFIG)) { %>
	  			  	 <tr valign="bottom">	  				
						<td></br></td>
	  				</tr>
	  				<tr><td colspan="6"><div class="sezione">Professioni Desiderate</div></td></tr>		
	  				
	  				<%
	  				if (professioniDesiderate != null) {
	  					for (int i=0;i<professioniDesiderate.size();i++) {
	  						SourceBean professioneDesiderata = (SourceBean) professioniDesiderate.get(i);
	  						%>
	  						<tr>
			  					<td class="etichetta" valign="top">Mansione</td>
					  			<td class="campo2" colspan="3" valign="top"><b><%= StringUtils.getAttributeStrNotNull(professioneDesiderata, "MANSIONE") %></b></td>
			  				</tr>
			  				<tr>
			  					<td class="etichetta" valign="top">Descrizione professione</td>
					  			<td class="campo2" colspan="3" valign="top"><b><%= StringUtils.getAttributeStrNotNull(professioneDesiderata, "DESCRIZIONEPROFESSIONE") %></b></td>
			  				</tr>
	  						<tr>
			  					<td class="etichetta" valign="top">Breve descrizione e durata esperienza</td>
					  			<td class="campo2" colspan="3" valign="top"><b><%= StringUtils.getAttributeStrNotNull(professioneDesiderata, "DESCRIZIONEESPERIENZA") %></b></td>
			  				</tr>
	  						<tr>
			  					<td class="etichetta" valign="top">Disponibilità utilizzo mezzo proprio</td>
					  			<td class="campo2" colspan="3" valign="top"><b><%= StringUtils.getAttributeStrNotNull(professioneDesiderata, "MEZZI") %></b></td>
			  				</tr>
	  						<tr>
			  					<td class="etichetta" valign="top">Preferenze su modalità lavoro</td>
					  			<td class="campo2" colspan="3" valign="top"><b><%= StringUtils.getAttributeStrNotNull(professioneDesiderata, "ORARIO") %></b></td>
			  				</tr>
	  						
	  						<% if (i < (professioniDesiderate.size()-1)) { %>
 	  			  				<tr><td colspan="6"><div class="sezione"></div></td></tr>			
	  						<% } %>
	  						
	  						<%
	  					}	
	  				}
	  				%>
	  			  
	  			  <%}else{ %>
	  				<tr valign="bottom">	  				
						<td></br></td>
	  				</tr>
	  				<tr><td colspan="6"><div class="sezione">Professioni Desiderate</div></td></tr>		
	  				
	  				<%
	  				if (professioniDesiderate != null) {
	  					for (int i=0;i<professioniDesiderate.size();i++) {
	  						SourceBean professioneDesiderata = (SourceBean) professioniDesiderate.get(i);
	  						HashMap comuniNote = (HashMap) professioneDesiderata.getAttribute("COMUNI");
	  						%>
	  						<tr>
			  					<td class="etichetta" valign="top">Mansione</td>
					  			<td class="campo2" colspan="3" valign="top"><b><%= StringUtils.getAttributeStrNotNull(professioneDesiderata, "MANSIONE") %></b></td>
			  				</tr>
			  				<tr>
			  					<td class="etichetta" valign="top">Descrizione professione</td>
					  			<td class="campo2" colspan="3" valign="top"><b><%= StringUtils.getAttributeStrNotNull(professioneDesiderata, "DESCRIZIONEPROFESSIONE") %></b></td>
			  				</tr>
			  				<tr>
			  					<td class="etichetta" valign="top">Esperienza</td>
					  			<td class="campo2" colspan="3" valign="top"><b><%= StringUtils.getAttributeStrNotNull(professioneDesiderata, "ESPERIENZA") %></b></td>
			  				</tr>
	  						<tr>
			  					<td class="etichetta" valign="top">Breve descrizione e durata esperienza</td>
					  			<td class="campo2" colspan="3" valign="top"><b><%= StringUtils.getAttributeStrNotNull(professioneDesiderata, "DESCRIZIONEESPERIENZA") %></b></td>
			  				</tr>
	  						 
			  				<%
			  				 
			  				SourceBean mobilitaDes = (SourceBean)professioneDesiderata.getAttribute("MOBILITA");
			  				Vector vecMobilita = mobilitaDes.getAttributeAsVector("ROWS.ROW");			  				
			  				String dispauto = "", dispmoto = "", dispmezzipubblici = "", pendolare = "", percorrenza = "", mobsettimanale ="", tipotrasferta = "", notedisponibilita ="";
							if (vecMobilita.size() != 0) {
							%>
							<tr>
			  					<td class="etichetta">Mobilit&agrave; geografica</td>
 			  				</tr>
 			  				<%
								for (int j = 0; j < vecMobilita.size(); j++) {
									SourceBean mobilita = (SourceBean)vecMobilita.get(j);
									dispauto = StringUtils.getAttributeStrNotNull(mobilita, "dispauto"); 
									dispmoto = StringUtils.getAttributeStrNotNull(mobilita, "dispmoto"); 
									dispmezzipubblici = StringUtils.getAttributeStrNotNull(mobilita, "dispmezzipubblici"); 
									pendolare = StringUtils.getAttributeStrNotNull(mobilita, "pendolare"); 
									percorrenza = StringUtils.getAttributeStrNotNull(mobilita, "percorrenza"); 
									mobsettimanale = StringUtils.getAttributeStrNotNull(mobilita, "mobsettimanale"); 
									tipotrasferta = StringUtils.getAttributeStrNotNull(mobilita, "tipotrasferta"); 
									notedisponibilita = StringUtils.getAttributeStrNotNull(mobilita, "notedisponibilita");
							%>
							     <tr class="note">
							        <td class="etichetta">Disponibilit&agrave; utilizzo<br>Automobile</td>
							        <td class="campo">
							          <table>
							          <tr>
							           <td class="campo2" colspan="3" valign="top"><b><%= dispauto %></b></td>
							                
							            </td>
							            <td class="etichetta2">&nbsp;</td>
							            <td class="etichetta2">Disponibilit&agrave; utilizzo<br>Motoveicolo</td>
							           <td class="campo2" colspan="3" valign="top"><b><%= dispmoto %></b></td>
							          </tr>
							          </table>
							        </td>
							     </tr>
							      <tr>
							            <td class="etichetta2">Disponibilit&agrave; utilizzo<br>Mezzi pubblici</td>
							            <td class="campo2" colspan="3" valign="top"><b><%= dispmezzipubblici %></b></td>
							    </tr>
							     <tr class="note">
							        <td class="etichetta">Pendolarismo<br>Giornaliero</td>
							        <td class="campo">
							        <table>
							        <tr>
							           <td class="campo2" colspan="3" valign="top"><b><%= pendolare %></b></td>
							            <td class="etichetta2">&nbsp;</td>
							            <td class="etichetta2">Durata di Percorrenza Max. in minuti</td>
							            <td class="campo2" colspan="3" valign="top"><b><%= percorrenza %></b></td>
							        </tr>
							        </table>
							        </td>
							     </tr>
							     <tr>
							        <td class="etichetta">Mobilit&agrave;<br>Settimanale</td>
							       <td class="campo2" colspan="3" valign="top"><b><%= mobsettimanale %></b></td>
							      </tr>
							      <tr>
							        <td class="etichetta">Tipo di Trasferta</td>
							        <td class="campo2" colspan="3" valign="top"><b><%= tipotrasferta %></b></td>
							      </tr>
							      <tr class="note">
							        <td class="etichetta">Note</td>
							      <td class="campo2" colspan="3" valign="top"><b><%= notedisponibilita %></b></td>
							      </tr>
							      
							<%
								}
								
							}
			  				%>
	  						<tr>
			  					<td class="etichetta" valign="top">Preferenze su modalità lavoro</td>
					  			<td class="campo2" colspan="3" valign="top"><b><%= StringUtils.getAttributeStrNotNull(professioneDesiderata, "ORARIO") %></b></td>
			  				</tr>
			  				<tr>
			  					<td class="etichetta" valign="top">Preferenze tipologie contrattuali</td>
					  			<td class="campo2" colspan="3" valign="top"><b><%= StringUtils.getAttributeStrNotNull(professioneDesiderata, "TIPIRAPPORTO") %></b></td>
			  				</tr>
			  				<tr>
			  					<td class="etichetta" valign="top">Preferenze turni</td>
					  			<td class="campo2" colspan="3" valign="top"><b><%= StringUtils.getAttributeStrNotNull(professioneDesiderata, "TURNI") %></b></td>
			  				</tr>
			  				<tr>
			  					<td class="etichetta">Preferenze comuni</td>
 			  				</tr>
			  				<%if(comuniNote!= null && !comuniNote.isEmpty()){
			  					 Iterator it = comuniNote.entrySet().iterator();
			  					 while (it.hasNext()) {
			  					    // Utilizza il nuovo elemento (coppia chiave-valore)
			  					    // dell'hashmap
			  					    Map.Entry entry = (Map.Entry)it.next();
			  					    String comune = (String) entry.getKey();
			  					    String nota = (String) entry.getValue();
			  				%>
	  						<tr>
	  							<td class="etichetta" valign="top">&nbsp;</td>
					  			<td class="campo2" valign="top"><b><%= Utils.notNull(comune) %></b></td>
					  			<td class="etichetta" valign="top">Nota</td>
					  			<td class="campo2" colspan="5" valign="top" nowrap><b><%= Utils.notNull(nota) %></b></td>
 			  				</tr>
			  				
			  				<%
			  					 }
			  				}%>
			  				<tr>
			  					<td class="etichetta" valign="top">Preferenze province</td>
					  			<td class="campo2" colspan="3" valign="top"><b><%= StringUtils.getAttributeStrNotNull(professioneDesiderata, "PROVINCE") %></b></td>
			  				</tr>
			  				<tr>
			  					<td class="etichetta" valign="top">Preferenze regioni</td>
					  			<td class="campo2" colspan="3" valign="top"><b><%= StringUtils.getAttributeStrNotNull(professioneDesiderata, "REGIONI") %></b></td>
			  				</tr>
			  				<tr>
			  					<td class="etichetta" valign="top">Preferenze stati</td>
					  			<td class="campo2" colspan="3" valign="top"><b><%= StringUtils.getAttributeStrNotNull(professioneDesiderata, "STATI") %></b></td>
			  				</tr>
	  						
	  						<% if (i < (professioniDesiderate.size()-1)) { %>
	  							<tr><td colspan="6"><div class="sezione"></div></td></tr>	
	  						<% } %>
	  						
	  						<%
	  					}	
	  				}
	  			  }
	  			%>
	  				
	  			<%if (numConfigCurr.equalsIgnoreCase(Properties.CUSTOM_CONFIG)){%>	
	  			<tr valign="bottom">	  				
						<td></br></td>
	  			</tr>	
	  			<tr><td colspan="6"><div class="sezione">Annotazioni</div></td></tr>		
  				<%if(annotazioni != null){ 
  					
  					String limitazioniDispo = StringUtils.getAttributeStrNotNull(annotazioni, "limitazionicv");
  					String noteCV = StringUtils.getAttributeStrNotNull(annotazioni, "notecv");
  					
  				%>
  				<tr>
  					<td class="etichetta2" valign="top">Limitazioni delle disponibilit&agrave;</td>
  					<td class="campo2" valign="top" colspan="5"><b><%=  limitazioniDispo%></b></td>
  				</tr>	
  				</tr>
  					<td class="etichetta2" valign="top">Note Curriculum Vitae</td>
  					<td class="campo2" valign="top" colspan="5"><b><%= noteCV %></b></td>
  				</tr>
  				<% if(flgNoteCurriculum){
  					String competenze = StringUtils.getAttributeStrNotNull(annotazioni, "competenze");
  				%>
  				   </tr>
  					<td class="etichetta2" valign="top">Note Competenze</td>
  					<td class="campo2" valign="top" colspan="5"><b><%= competenze %></b></td>
  				   </tr>  					
  				<% } %>
  				
  				
	  			<%} 
	  			}%>
	  				 
			</table>	
			<% out.print(htmlStreamBottom); %> 
			<center>
				<input class="pulsante" type="button" name="btnchiudi" value="Chiudi" onclick="window.close()" />
			</center>				
	</body>
	</html>