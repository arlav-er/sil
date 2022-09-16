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
	String prgRichiestaAz = (String)serviceRequest.getAttribute("PRGRICHIESTAAZ");
	String prgUnitaAz = (String)serviceRequest.getAttribute("PRGUNITAAZ");	
	
	//Dati Anagrafici
	SourceBean datiAnagrafici = (SourceBean) serviceResponse.getAttribute("M_GetDatiAnagraficiAnteprimaClicLavoro.ROWS.ROW");
	String codFis = "", denominazione = "", settore = "", ampiezza = "", web = "";	
	if (datiAnagrafici != null){
		String codAtecoValido = StringUtils.getAttributeStrNotNull(datiAnagrafici, "codAtecoValido");
		
		codFis = StringUtils.getAttributeStrNotNull(datiAnagrafici, "codicefiscale");
		denominazione = StringUtils.getAttributeStrNotNull(datiAnagrafici, "denominazione");
		settore = StringUtils.getAttributeStrNotNull(datiAnagrafici, "settore");
		ampiezza = ((BigDecimal)datiAnagrafici.getAttribute("ampiezza")).toString();
		web = StringUtils.getAttributeStrNotNull(datiAnagrafici, "web");	
		
		if("NO".equals(codAtecoValido)){
			settore = settore + " (Codifica scaduta)";
		}
	}
	
	//Dati Contatto
	SourceBean datiContatto = null;
	String statoInvioClicLavoro = (String)serviceRequest.getAttribute("STATOINVIOCLICLAVORO");
		if(statoInvioClicLavoro.equals("PA")){
			datiContatto = (SourceBean) serviceResponse.getAttribute("M_GetDatiContattoPreInvioAnteprimaClicLavoro.ROWS.ROW");
			
		}else{
			datiContatto = (SourceBean) serviceResponse.getAttribute("M_GetDatiContattoAnteprimaBlen.ROWS.ROW");
		}	
	String indirizzo = "", comune = "", cap = "", telefono = "", fax = "", email = "";	
	if (datiContatto != null){
		indirizzo = StringUtils.getAttributeStrNotNull(datiContatto, "indirizzo");
		comune = StringUtils.getAttributeStrNotNull(datiContatto, "idcomune");
		cap = StringUtils.getAttributeStrNotNull(datiContatto, "cap");
		telefono = StringUtils.getAttributeStrNotNull(datiContatto, "telefono");
		fax = StringUtils.getAttributeStrNotNull(datiContatto, "fax");		
		email = StringUtils.getAttributeStrNotNull(datiContatto, "email");
	}
	
	//Dati Profilo
	SourceBean datiProfilo = (SourceBean) serviceResponse.getAttribute("M_GetDatiProfiliAnteprimaBlen.ROWS.ROW");
	String numeroLavoratori = "", descrProfessione = "", idProfessione = "", descrizioneRicerca = "", esperienzaRichiesta = "";	
	if (datiProfilo != null){
		numeroLavoratori = ((BigDecimal)datiProfilo.getAttribute("numeroLavoratori")).toString();
		idProfessione = StringUtils.getAttributeStrNotNull(datiProfilo, "idProfessione");
		descrProfessione = StringUtils.getAttributeStrNotNull(datiProfilo, "descrProfessione");
		descrizioneRicerca = StringUtils.getAttributeStrNotNull(datiProfilo, "descrizioneRicerca");
		esperienzaRichiesta = StringUtils.getAttributeStrNotNull(datiProfilo, "esperienzaRichiesta");		
	}	
	
	//Dati Titoli Studio
	SourceBean datiTitoliStudio = (SourceBean) serviceResponse.getAttribute("M_GetDatiTitoliStudioAnteprimaClicLavoro.ROWS");	
	Vector vectTitoliStudio = null;	
	if (datiTitoliStudio != null){
		vectTitoliStudio = datiTitoliStudio.getAttributeAsVector("ROW");
	}
	
	//Dati Condizioni Lavorative
	SourceBean datiCondizioniLavorative = (SourceBean) serviceResponse.getAttribute("M_GetDatiCondizioniLavorativeAnteprimaClicLavoro.ROWS.ROW");
	String idComune = "", strDescrizione = "", idModalitaLavoro = "";	
	if (datiCondizioniLavorative != null){
		idComune = StringUtils.getAttributeStrNotNull(datiCondizioniLavorative, "idComune");
		strDescrizione = StringUtils.getAttributeStrNotNull(datiCondizioniLavorative, "STRDESCRIZIONE"); 		
		idModalitaLavoro = StringUtils.getAttributeStrNotNull(datiCondizioniLavorative, "idModalitaLavoro");
	}
	
	//Dati Durata Richiesta
	SourceBean datiDurataRichiesta = (SourceBean) serviceResponse.getAttribute("M_GetDatiDurataRichiestaAnteprimaClicLavoro.ROWS.ROW");
	String dataPubblicazione = "", dataScadenza = "";	
	if (datiDurataRichiesta != null){
		dataPubblicazione = StringUtils.getAttributeStrNotNull(datiDurataRichiesta, "dataPubblicazione");
		dataScadenza = StringUtils.getAttributeStrNotNull(datiDurataRichiesta, "dataScadenza"); 
	}
	
	//Dati Sistema
	SourceBean datiSistema = (SourceBean) serviceResponse.getAttribute("M_GetDatiSistemaAnteprimaBlen.ROWS.ROW");
	String intermediario = "", codiceFiscaleIntermediario = "", denominazioneIntermediario = "", indirizzoDati = "", idComuneDati = "";
	String capDati = "", telefonoDati = "", faxDati = "", emailDati = "", visibilita = "", tipoOfferta = "", codiceOfferta = "", codiceOffertaIntermediario = "";	
	if (datiSistema != null){
		intermediario = StringUtils.getAttributeStrNotNull(datiSistema, "intermediario");
		codiceFiscaleIntermediario = StringUtils.getAttributeStrNotNull(datiSistema, "codiceFiscaleIntermediario");
		denominazioneIntermediario = StringUtils.getAttributeStrNotNull(datiSistema, "denominazioneIntermediario");
		indirizzoDati = StringUtils.getAttributeStrNotNull(datiSistema, "indirizzo"); 
		idComuneDati = StringUtils.getAttributeStrNotNull(datiSistema, "idComune");
		capDati = StringUtils.getAttributeStrNotNull(datiSistema, "cap"); 
		telefonoDati = StringUtils.getAttributeStrNotNull(datiSistema, "telefono");
		faxDati = StringUtils.getAttributeStrNotNull(datiSistema, "fax"); 
		emailDati = StringUtils.getAttributeStrNotNull(datiSistema, "email");
		visibilita = StringUtils.getAttributeStrNotNull(datiSistema, "visibilita"); 
		tipoOfferta = StringUtils.getAttributeStrNotNull(datiSistema, "tipoOfferta");
		codiceOfferta = StringUtils.getAttributeStrNotNull(datiSistema, "codiceOfferta");
		codiceOffertaIntermediario = StringUtils.getAttributeStrNotNull(datiSistema, "codiceOffertaintermediario");
	}
		
	//Dati Albo
	SourceBean datiAlbo = (SourceBean) serviceResponse.getAttribute("M_GetDatiAlboAnteprimaClicLavoro.ROWS.ROW");
	String desAlbo = "";	
	if (datiAlbo != null){
		desAlbo = StringUtils.getAttributeStrNotNull(datiAlbo, "descrizione");	
	}
		
	//Dati Lingue Conosciute
	SourceBean datiLingue = (SourceBean) serviceResponse.getAttribute("M_GetDatiLingueAnteprimaClicLavoro.ROWS");	
	Vector vectLingue = null;	
	if (datiLingue != null){
		vectLingue = datiLingue.getAttributeAsVector("ROW");
	}
	
	//Dati Conoscenze Informatiche
	SourceBean datiInformatica = (SourceBean) serviceResponse.getAttribute("M_GetDatiInformaticiAnteprimaClicLavoro.ROWS");	
	Vector vectInformatica = null;	
	if (datiInformatica != null){
		vectInformatica = datiInformatica.getAttributeAsVector("ROW");
	}
	
	//Dati Altre Capacita
	SourceBean datiAltreCapacita = (SourceBean) serviceResponse.getAttribute("M_GetDatiAltreCapacitaAnteprimaClicLavoro.ROWS");	
	Vector vectAltreCapacita = null;	
	if (datiAltreCapacita != null){
		vectAltreCapacita = datiAltreCapacita.getAttributeAsVector("ROW");
	}
	
	//Dati Trasferta
	SourceBean datiTrasferta = (SourceBean) serviceResponse.getAttribute("M_GetDatiTrasferteAnteprimaClicLavoro.ROWS.ROW");
	String trasferte = "";	
	if (datiTrasferta != null){
		trasferte = StringUtils.getAttributeStrNotNull(datiTrasferta, "trasferte");
	}
	
	//Dati Patenti
	SourceBean datiPatenti = (SourceBean) serviceResponse.getAttribute("M_GetDatiPatentiAnteprimaClicLavoro.ROWS");
	Vector vectPatenti = null;	
	if (datiPatenti != null){
		vectPatenti = datiPatenti.getAttributeAsVector("ROW");
	}
	
	//Dati Mezzi
	SourceBean datiMezzi = (SourceBean) serviceResponse.getAttribute("M_GetDatiMezziAnteprimaClicLavoro.ROWS.ROW");
	String idmezzitrasporto = "";	
	if (datiMezzi != null){
		idmezzitrasporto = StringUtils.getAttributeStrNotNull(datiMezzi, "idmezzitrasporto");
	}
	
	//Dati Patentini
	SourceBean datiPatentini = (SourceBean) serviceResponse.getAttribute("M_GetDatiPatentiniAnteprimaClicLavoro.ROWS");
	Vector vectPatentini = null;	
	if (datiPatentini != null){
		vectPatentini = datiPatentini.getAttributeAsVector("ROW");
	}
	
	
	
	boolean canModify = false;
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
	
%>

	<html>
	<head>
		<title>Anteprima invio richiesta di personale BLEN</title>
		<link rel="stylesheet" href="../../css/stili.css" type="text/css">
		<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
		<af:linkScript path="../../js/"/>
		<SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>	
										
	</head>		
		
	<body class="gestione" onload="rinfresca()">
		<p class="titolo">Dati Personali del datore di lavoro</p>
			<% out.print(htmlStreamTop); %>
			<table class="main" border="0">
				<tr valign="bottom">
					<td class="etichetta"><b>Dati Anagrafici</b></td>
  				</tr>
  				<tr valign="top">
  					<td colspan="6" width="100%"><hr></td>
  				</tr>
  				<tr>
  					<td class="etichetta2">Codice Fiscale</td>
  					<td class="campo2"><b><%= codFis %></b></td>
  					<td class="etichetta2">Denominazione</td>
  					<td class="campo2"><b><%= denominazione %></b></td>
  				</tr>
  				<tr>
  					<td class="etichetta2">Settore</td>
  					<td class="campo2"><b><%= settore %></b></td>
  					<!--  
  					<td class="etichetta2">Ampiezza</td>
  					<td class="campo2"><b><%= ampiezza %></b></td>
  					-->
  				</tr>
  				<!-- <tr>
  					<td class="etichetta2">Sito Web</td>
  					<td class="campo2"><b><%= web %></b></td>
  				</tr>
  				 -->
  			</table>
			<% out.print(htmlStreamBottom); %>
		
			<% out.print(htmlStreamTop); %>
			<table class="main" border="0" width="100%">
				<tr valign="bottom">
					<td class="etichetta"><b>Dati per il Contatto</b></td>
  				</tr>
  				<tr valign="top">
  					<td colspan="4" width="100%"><hr></td>
  				</tr>
  				<tr>
  					<td class="etichetta2">Indirizzo</td>
  					<td class="campo2"><b><%= indirizzo %></b></td>
  				</tr>
  				<tr>
  					<td class="etichetta2">Comune</td>  					
  					<td class="campo2"><b><af:comboBox classNameBase="input" name="descComu" title="Stato azienda" selectedValue="<%=comune%>" disabled="true" moduleName="M_GetListaComuniAnteprimaClicLavoro" addBlank="true" /></b></td>
  					<td class="etichetta2">CAP</td>
  					<td class="campo2"><b><%= cap %></b></td>
  				</tr>
  				<tr>
  					<td class="etichetta2">Telefono</td>
  					<td class="campo2"><b><%= telefono %></b></td>
  					<td class="etichetta2">Fax</td>
  					<td class="campo2"><b><%= fax %></b></td>
  				</tr>
  				<tr>
  					<td class="etichetta2">Email</td>
  					<td class="campo2"><b><%= email %></b></td>
  				</tr>
  			</table>
			<% out.print(htmlStreamBottom); %>
			
			
			
		<p class="titolo">Dati Relativi alla richiesta di personale</p>
			<% out.print(htmlStreamTop); %>
			<table class="main" border="0">
				<tr valign="bottom">
					<td class="etichetta"><b>Profilo Professionale Ricercato</b></td>
  				</tr>
  				<tr valign="top">
  					<td colspan="6" width="100%"><hr></td>
  				</tr>
  				<tr>
  					<td class="etichetta" valign="top">Numero lavoratori</td>
  					<td class="campo2" valign="top"><b><%= numeroLavoratori %></b></td>
  					<%--
  					<td class="etichetta">Qualifica professionale offerta</td>
  					<td class="campo2"><b><%= idProfessione %></b></td>
  					--%>
  				</tr>
  				<tr>
  					<td class="etichetta" valign="top">Qualifica professionale offerta</td>
  					<td class="campo2" valign="top"><b><%= descrProfessione %></b></td>
  				</tr>
  				<tr>
  					<td class="etichetta" valign="top">Profilo richiesto</td>
  					<td class="campo2" valign="top"><b><%= descrizioneRicerca %></b></td>
  				</tr>
  				<tr>
  					<td class="etichetta" valign="top">Esperienza richiesta</td>
  					<td class="campo2" valign="top"><b><%= esperienzaRichiesta %></b></td>
  				</tr>  				
  				<% 
	  				String idTitoloStudio = "", descrizioneStudio = "";
	  				SourceBean rigaTitolo = null;
	  				
	  				if(datiTitoliStudio != null){
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
  						<td colspan="6">
  							<table width="100%">
  							<%--
			  				<%
			  					for(int i=0; i < vectTitoliStudio.size(); i++){
			  						rigaTitolo = (SourceBean) vectTitoliStudio.elementAt(i);
			  						idTitoloStudio = rigaTitolo.getAttribute("idTitoloStudio").toString();
				  					descrizioneStudio = rigaTitolo.getAttribute("descrizioneStudio").toString();
				  					%>	
					  			<tr>
					  				<td class="etichetta">Titolo di studio</td>
					  				<td class="campo2"><b><%= descrizioneStudio %></b></td>
					  			</tr>	  				
		  						<% } %>
		  					--%>
		  					<tr>
				  				<td class="etichetta" valign="top">Titoli di studio</td>
				  				<td class="campo2" valign="top"><b>
		  					<%
		  					for(int i=0; i < vectTitoliStudio.size(); i++){
		  						rigaTitolo = (SourceBean) vectTitoliStudio.elementAt(i);
		  						idTitoloStudio = rigaTitolo.getAttribute("idTitoloStudio").toString();
			  					descrizioneStudio = rigaTitolo.getAttribute("descrizioneStudio").toString();
			  					%><%=descrizioneStudio%><% if (i < (vectTitoliStudio.size()-1)) { %>, <% } %><% } %>
	  						</b></td></tr>
	  						</table>
	  					</td>
	  				</tr>
	  				<% } %>
	  				
	  				
	  			<%--	
	  			<tr valign="bottom">	  				
					<td></br></td>
  				</tr>
  				<tr valign="bottom">	  				
					<td class="etichetta"><b>Iscrizioni Albi</b></td>
  				</tr>
	  			<tr valign="top">
  					<td colspan="6" width="100%"><hr></td>
  				</tr>
	  			<tr>
	  				<td colspan="6">
	  					<table>
	  						<tr>
				  				<td class="etichetta" valign="top">Iscrizione ad albi e ordini professionali</td> 
					  			<td class="campo2" valign="top"><b><%= desAlbo %></b></td> 
					  		</tr>
			  			</table>
		  			</td>
	  			</tr>
	  			
	  			--%>
  				<% 
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
  								tipo_prec = rigaInformatica.getAttribute("tipo").toString();
  								primaRiga = true;
  							}
  							tipo = rigaInformatica.getAttribute("tipo").toString();
  							
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
  								desInformatica = rigaInformatica.getAttribute("tipo").toString() + ": " + rigaInformatica.getAttribute("descrizione").toString();
  							}else{
  								desInformatica = desInformatica + ", " + tipo + rigaInformatica.getAttribute("descrizione").toString();
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
 
  					String desCapacita = "";
  					SourceBean rigaCapacita = null;
  				
  					if(vectAltreCapacita != null){
  						for(int i=0; i < vectAltreCapacita.size(); i++){
  							rigaCapacita = (SourceBean) vectAltreCapacita.elementAt(i);
  							if (desCapacita.equals("")){
  								desCapacita = rigaCapacita.getAttribute("capacita").toString();
  							}else{
  								desCapacita = desCapacita + ", " + rigaCapacita.getAttribute("capacita").toString();
  							}
  						}
  					}					
  				%>  
  				<tr valign="bottom">	  				
					<td></br></td>
  				</tr>
  				<tr valign="bottom">
					<td class="etichetta"><b>Altre Informazioni</b></td>
  				</tr>
  				<tr valign="top">
  					<td colspan="6" width="100%"><hr></td>
  				</tr>
  				<tr>
	  				<td class="etichetta" valign="top">Albi professionali</td> 
		  			<td class="campo2" valign="top"><b><%= desAlbo %></b></td> 
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
		  			<td class="campo2" colspan="3"><b><%= patenti %></b></td>
  				</tr>
  				<% 
  					String patentini = "";
  					SourceBean rigaPatentini = null;
  				
  					if(vectPatentini != null){
						for(int i=0; i < vectPatentini.size(); i++){
							rigaPatentini = (SourceBean) vectPatentini.elementAt(i);
							if (patentini.equals("")){
								if(rigaPatentini.getAttribute("patentino") != null){
									patentini = rigaPatentini.getAttribute("patentino").toString();
								}else{
									patentini = "Min: nessuna corrispondenza";
								}
								
							}else{
								if(rigaPatentini.getAttribute("patentino") != null){
									patentini = patentini + ", " + rigaPatentini.getAttribute("patentino").toString();
								}else{
									patentini = patentini + ", Min: nessuna corrispondenza";
								}								
							}
						}
  					}
  				%>
  				<tr>
  					<td class="etichetta" valign="top">Patentini</td>
		  			<td class="campo2" colspan="3"><b><%= patentini %></b></td>
  				</tr>
  				<tr>
  					<td class="etichetta" valign="top">Disponibilità alle trasferte</td>
		  			<td class="campo2" colspan="3"><b><%= trasferte %></b></td>
  				</tr>
  				<tr>
  					<td class="etichetta" valign="top">Disponibilità mezzo di trasporto</td>
		  			<td class="campo2" colspan="3"><b><%= idmezzitrasporto %></b></td>
  				</tr>
  				<tr>
  					<td class="etichetta" valign="top">Altre conoscenze e capacità</td>
		  			<td class="campo2" colspan="3"><b><%= desCapacita %></b></td>
  				</tr>
  			</table>
			<% out.print(htmlStreamBottom); %>	
			
			<% out.print(htmlStreamTop); %>
			<table class="main" border="0">
				<tr valign="bottom">
					<td class="etichetta"><b>Durata della richiesta</b></td>
  				</tr>
  				<tr valign="top">
  					<td colspan="6" width="100%"><hr></td>
  				</tr>
  				<tr>
  					<td class="etichetta">Data di pubblicazione</td>
  					<td class="campo2"><b><%= dataPubblicazione %></b></td>
  					<td class="etichetta">Data di scadenza</td>
  					<td class="campo2"><b><%= dataScadenza %></b></td>
  				</tr>
  			</table>
			<% out.print(htmlStreamBottom); %>
			
			<% out.print(htmlStreamTop); %>
			<table class="main" border="0">
				<tr valign="bottom">
					<td class="etichetta"><b>Condizioni Lavorative Offerte</b></td>
  				</tr>
  				<tr valign="top">
  					<td colspan="6" width="100%"><hr></td>
  				</tr>
  				<tr>
  					<td class="etichetta">Sede di lavoro</td>
  					<td class="campo2"><b><af:comboBox classNameBase="input" name="descComu2" title="Stato azienda" selectedValue="<%=idComune%>" disabled="true" moduleName="M_GetListaComuniAnteprimaClicLavoro" addBlank="true" /></b></td>
  				</tr>
  				<tr>
  					<td class="etichetta">Modalità di lavoro</td>
 					<td class="campo2"><b><%= idModalitaLavoro %></b></td>
 				</tr>  
  				<tr>
  					<td class="etichetta">Tipo di rapporto di lavoro offerto</td>
  					<td class="campo2"><b><%= strDescrizione %></b></td>
  				</tr>
  			</table>
			<% out.print(htmlStreamBottom); %>	
					
			<%-- 
			<% out.print(htmlStreamTop); %>
			
			<table class="main" border="0">
				<tr valign="bottom">
					<td class="etichetta"><b>Dati di Sistema</b></td>
  				</tr>
  				<tr valign="top">
  					<td colspan="6" width="100%"><hr></td>
  				</tr>
  				<tr>
  					<td class="etichetta">Intermediario</td>
  					<td class="campo2"><b><%= intermediario %></b></td>
  					<td class="etichetta">Codice Fiscale Intermediario</td>
  					<td class="campo2"><b><%= codiceFiscaleIntermediario %></b></td>
  					<td class="etichetta">Denominazione Intermediario</td>
  					<td class="campo2"><b><%= denominazioneIntermediario %></b></td>
  				</tr>
  				<tr>
  					<td class="etichetta">Indirizzo</td>
  					<td class="campo2" colspan="3"><b><%= indirizzoDati %></b></td>
  					<td class="etichetta">Cap</td>
  					<td class="campo2"><b><%= capDati %></b></td>
  				</tr>
  				<tr>
  					<td class="etichetta">Comune</td>
  					<td class="campo2"><b><af:comboBox classNameBase="input" name="descComu3" title="Stato azienda" selectedValue="<%=idComuneDati%>" disabled="true" moduleName="M_GetListaComuniAnteprimaClicLavoro" addBlank="true" /></b></td>
  				</tr>
  				<tr>
  					<td class="etichetta">Telefono</td>
  					<td class="campo2"><b><%= telefonoDati %></b></td>
  					<td class="etichetta">Fax</td>
  					<td class="campo2"><b><%= faxDati %></b></td>

  				</tr>
  				<tr>
  					<td class="etichetta">E-mail</td>
  					<td class="campo2" colspan="3"><b><%= emailDati %></b></td>
  				</tr>
  				<tr>
  					<td class="etichetta">Visibilità</td>
  					<td class="campo2"><b><%= visibilita %></b></td>
  					<td class="etichetta">Tipo Offerta</td>
  					<td class="campo2"><b><%= tipoOfferta %></b></td>
  				</tr>
  				<tr>
  					<td class="etichetta">Codice Offerta</td>
  					<td class="campo2"><b><%= codiceOfferta %></b></td>
  					<td class="etichetta">Codice Offerta Intermediario</td>
  					<td class="campo2"><b><%= codiceOffertaIntermediario %></b></td>
  					<td></td>
  					<td></td>
  				</tr>
  			</table>
  			
			<% out.print(htmlStreamBottom); %>	
			--%>
	</body>
	</html>