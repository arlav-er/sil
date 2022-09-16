<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="
  com.engiweb.framework.base.*,
  it.eng.sil.security.*,
  it.eng.sil.util.*,
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
	String _page = (String) serviceRequest.getAttribute("PAGE");
	String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");
	
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
	SourceBean datiEsperienze = (SourceBean) serviceResponse.getAttribute("M_GetDatiEsperienzaLavAnteprimaCandidatura.ROWS");
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
	SourceBean datiAlbo = (SourceBean) serviceResponse.getAttribute("M_GetDatiAlbiAnteprimaCandidatura.ROWS");
	Vector vectAlbo = null;		
	if (datiAlbo != null){
		vectAlbo = datiAlbo.getAttributeAsVector("ROW");
	}
	
	//Dati Patenti
	SourceBean datiPatenti = (SourceBean) serviceResponse.getAttribute("M_GetDatiPatentiAnteprimaCandidatura.ROWS");
	Vector vectPatenti = null;		
	if (datiPatenti != null){
		vectPatenti = datiPatenti.getAttributeAsVector("ROW");
	}	
	
	//Dati Patentini
	SourceBean datiPatentini = (SourceBean) serviceResponse.getAttribute("M_GetDatiPatentiniAnteprimaCandidatura.ROWS");
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
	
%>

	<html>
	<head>
		<title>Anteprima invio candidatura BLEN</title>
		<link rel="stylesheet" href="../../css/stili.css" type="text/css">
		<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
		<af:linkScript path="../../js/"/>
		<SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>	
										
	</head>		
		
	<body class="gestione" onload="rinfresca()">
		<p class="titolo">Dati Lavoratore</p>
			<% out.print(htmlStreamTop); %>
			<table class="main" border="0">
				<tr valign="bottom">
					<td class="etichetta"><b>Dati Anagrafici</b></td>
  				</tr>
  				<tr valign="top">
  					<td colspan="6" width="100%"><hr></td>
  				</tr>
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
  				<tr valign="bottom">
					<td class="etichetta"><b>Dati Domicilio</b></td>
  				</tr>
  				<tr valign="top">
  					<td colspan="6" width="100%"><hr></td>
  				</tr>
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
  				<tr valign="bottom">
					<td class="etichetta" ><b>Dati Recapito</b></td>
  				</tr>
  				<tr valign="top">
  					<td colspan="6" width="100%"><hr></td>
  				</tr>
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
					String tipoEsperienza = "", tipoEsperienzaDesc = "", qualificaSvolta = "", descrQualificaSvolta = "", principaliMansioni = "", nomeDatore = "";
					String dataInizio = "", dataFine = "", indirizzoDatore = "";
	  				SourceBean rigaEsperienze = null;
	  				
	  				if(datiEsperienze != null){
	  				%>
	  				
					<tr valign="bottom">
						<td class="etichetta"><b>Esperienze Lavorative</b></td>
	  				</tr>
  					<%
  					for(int i=0; i < vectEsperienze.size(); i++){
  						rigaEsperienze = (SourceBean) vectEsperienze.elementAt(i);
  						tipoEsperienza = StringUtils.getAttributeStrNotNull(rigaEsperienze, "tipoEsperienza");
  						tipoEsperienzaDesc = StringUtils.getAttributeStrNotNull(rigaEsperienze, "tipoesperienzadesc");
  						descrQualificaSvolta = StringUtils.getAttributeStrNotNull(rigaEsperienze, "descrQualificaSvolta");	
  						principaliMansioni = StringUtils.getAttributeStrNotNull(rigaEsperienze, "principaliMansioni");
  						nomeDatore = StringUtils.getAttributeStrNotNull(rigaEsperienze, "nomeDatore");	
  						dataInizio = StringUtils.getAttributeStrNotNull(rigaEsperienze, "dataInizio");
  						dataFine = StringUtils.getAttributeStrNotNull(rigaEsperienze, "dataFine");
  						indirizzoDatore = StringUtils.getAttributeStrNotNull(rigaEsperienze, "indirizzoDatore");			  									  						
	  					%>	
		  				<tr valign="top">
		  					<td colspan="6" width="100%"><hr></td>
		  				</tr>
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
					  					<td class="etichetta" valign="top">Data Inizio</td>
					  					<td class="campo2" valign="top"><b><%= dataInizio %></b></td>
					  					<td class="etichetta" valign="top">Data Fine</td>
					  					<td class="campo2" valign="top"><b><%= dataFine %></b></td>
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
	  				
	  				String titoloCorso = "", idSedeCorso = "", durata = "", idTipologiaDurata = "", idAttestazione = "", tipoDurata = "";
	  				SourceBean rigaFormazione = null;
	  				
	  				if(datiFormazione != null){ %>
		  				
		  			<tr valign="bottom">	  				
						<td></br></td>
	  				</tr> 
					<tr valign="bottom">
						<td class="etichetta"><b>Formazione</b></td>
	  				</tr>
	  				<%
  					for(int i=0; i < vectFormazione.size(); i++){
  						rigaFormazione = (SourceBean) vectFormazione.elementAt(i);
  						titoloCorso = StringUtils.getAttributeStrNotNull(rigaFormazione, "titoloCorso");
  						idSedeCorso = StringUtils.getAttributeStrNotNull(rigaFormazione, "idSede");	
  						durata = StringUtils.getAttributeStrNotNull(rigaFormazione, "durata");
  						idTipologiaDurata = StringUtils.getAttributeStrNotNull(rigaFormazione, "idTipologiaDurata");	
  						idAttestazione = StringUtils.getAttributeStrNotNull(rigaFormazione, "idAttestazione");		
  						if("O".equals(idTipologiaDurata)){
  							tipoDurata = "Ore";
  						}else if("M".equals(idTipologiaDurata)){
  							tipoDurata = "Mesi";
  						}else{
  							tipoDurata = "";
  						}
	  					%>	
		  				<tr valign="top">
		  					<td colspan="6" width="100%"><hr></td>
		  				</tr>
		  				<tr>
							<td colspan="6">
								<table width="100%">
						  			<tr>
					  					<td class="etichetta" valign="top">Tipo Corso</td>
					  					<td class="campo2" valign="top"><b><%= titoloCorso %></b></td>
					  				</tr>	
					  				<tr>
					  					<td class="etichetta" valign="top">Sede</td>
					  					<td class="campo2" valign="top"><b><b><af:comboBox classNameBase="input" name="descComu" title="Stato azienda" selectedValue="<%=idSedeCorso%>" disabled="true" moduleName="M_GetListaComuniAnteprimaCandidatura" addBlank="true" /></b></td>
					  				</tr>
					  				<tr>
					  					<td class="etichetta" valign="top">Durata</td>
					  					<td class="campo2" valign="top"><b><%= durata %></b></td>
					  				</tr>
					  				<tr>
					  					<td class="etichetta" valign="top">Tipologia durata</td>
					  					<td class="campo2" valign="top"><b><%= tipoDurata %></b></td>
					  				</tr>
				  				</table>
  							</td>
		  				</tr>	  				
 					<% } 
	  				
	  				}
	  				

	  				String titolostudio = "", descrizioneistruzione = "", votazione = "";
	  				SourceBean rigaIstruzione = null;
	  				
	  				if(datiIstruzione != null){
	  				%>
	  				<tr valign="bottom">	  				
						<td></br></td>
	  				</tr>
	  				<tr valign="bottom">	  				
						<td class="etichetta"><b>Titoli di Studio</b></td>
	  				</tr>
	  				<tr valign="top">
  						<td colspan="6" width="100%"><hr></td>
  					</tr>
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
	  					<%=descrizioneistruzione%>&nbsp;<%=votazione%><%if (i < (vectIstruzione.size() - 1)) { %>, <%}%>
	  				
 					<% } %>
 					</b></td></tr>
 					
	  				<% } 
	  				
	  				String strLingua = "", idLivelloLetto = "", idLivelloScritto = "", idLivelloParlato = "";
	  				SourceBean rigaLingua = null;
	  				
	  				if(datiLingue != null){
	  				%>
	  				<tr valign="bottom">	  				
						<td></br></td>
	  				</tr>
	  				<tr valign="bottom">
						<td class="etichetta"><b>Conoscenze linguistiche</b></td>
	  				</tr>
		  			<tr valign="top">
	  					<td colspan="6" width="100%"><hr></td>
	  				</tr>
	  				<tr>
	  					<td colspan="6">
	  						<table width="100%">
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
		  				<tr valign="bottom">
							<td class="etichetta"><b>Conoscenze Informatiche</b></td>
		  				</tr>
		  				<tr valign="top">
		  					<td colspan="6" width="100%"><hr></td>
		  				</tr>  				
  					<%
	  					for(int i=0; i < vectInformatica.size(); i++){
							rigaInformatica = (SourceBean) vectInformatica.elementAt(i);
							if(i==0){
								tipo_prec = rigaInformatica.getAttribute("tipoconoscenza").toString();
								primaRiga = true;
							}
							tipo = rigaInformatica.getAttribute("tipoconoscenza").toString();
							
							if(tipo.equals(tipo_prec)){
								tipo_prec = tipo;
								tipo = "";
								newLine = false;
							}else{
								tipo_prec = tipo;
								tipo = tipo + ": "; 
								newLine = true;
							}
							if(newLine && primaRiga){ %>
							<tr>
			  					<td class="etichetta">Tipo conoscenza informatica</td>
					  			<td class="campo2" colspan="3"><b><%= desInformatica %></b></td>
			  				</tr>
							<%  desInformatica = "";
								primaRiga = false;
							   } else if(newLine && i!=0) { %>
								<tr>
				  					<td></td>
						  			<td class="campo2" colspan="3"><b><%= desInformatica %></b></td>
				  				</tr>
							<% desInformatica = ""; }
  							
  							
  							if (desInformatica.equals("")){
  								desInformatica = rigaInformatica.getAttribute("tipoconoscenza").toString() + ": " + rigaInformatica.getAttribute("strdescrizione").toString();
  							}else{
  								desInformatica = desInformatica + ", " + tipo + rigaInformatica.getAttribute("strdescrizione").toString();
  							}
  							
  							//Ultima riga
  							if(i==vectInformatica.size()-1){ %>
  								<tr>
				  					<td></td>
						  			<td class="campo2" colspan="3"><b><%= desInformatica %></b></td>
				  				</tr>
  							<% }	  					
	  					}
  					} 
  					
  					
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
	  				<tr valign="bottom">
						<td class="etichetta"><b>Abilitazioni, Patenti</b></td>
	  				</tr>
  					<tr valign="top">
	  					<td colspan="6" width="100%"><hr></td>
	  				</tr> 
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
	  				
	  				<tr valign="bottom">	  				
						<td></br></td>
	  				</tr>
	  				<tr valign="bottom">
						<td class="etichetta"><b>Professioni desiderate</b></td>
	  				</tr>  						  				
	  				<tr valign="top">
	  					<td colspan="6" width="100%"><hr></td>
	  				</tr> 
	  				
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
	  						<tr valign="top">
			  					<td colspan="6" width="100%"><hr></td>
			  				</tr> 
	  						<% } %>
	  						
	  						<%
	  					}	
	  				}
	  				%>
	  				
			</table>	
			<% out.print(htmlStreamBottom); %>  					
	</body>
	</html>