<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="
  com.engiweb.framework.base.*,
  it.eng.sil.security.*,
  it.eng.sil.util.*,
  it.eng.afExt.utils.StringUtils,
  it.eng.afExt.utils.DateUtils,
  java.util.*,
  java.lang.*,
  java.math.*,
  it.eng.sil.module.ido.vacancy.*
" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af" %>
<%@ taglib uri="presel" prefix="ps" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>


 <%
   	String _page = (String) serviceRequest.getAttribute("PAGE");
	PageAttribs attributi = new PageAttribs(user, "PubbRichiestePage");
 
 	boolean canView = attributi.containsButton("ANTFLVAC");
 	if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}
 	String prgRichiestaAz = (String)serviceRequest.getAttribute("PRGRICHIESTAAZ");
 	String prgAzienda = null;
 	String prgUnita = null;
 	
 	Vacancy datiAnteprima = null;
 	if(serviceResponse.containsAttribute("M_ANTEPRIMA_INVIO_VACANCY_SIL.ANTEPRIMA")){
 		datiAnteprima = (Vacancy) serviceResponse.getAttribute("M_ANTEPRIMA_INVIO_VACANCY_SIL.ANTEPRIMA");
 	}
	//DatoreLavoro
 	String codFis = "", denominazione = "", codAteco = "", codComuneLavoro = "", indLavoro = "", web = "";	
 	String cognomeRef = "", nomeRef = "", telefono = "", fax = "", email = "";	
 	String cognomeRefAlt = "", nomeRefAlt = "", telefonoAlt = "", faxAlt = "", emailAlt = "";	
	if (datiAnteprima!=null && datiAnteprima.getDatoreLavoro() != null){
		DatoreLavoro datoreLavoro = datiAnteprima.getDatoreLavoro();
		//dati anagrafici
		DatiAnagrafici datiAnagrafici = datoreLavoro.getDatiAnagrafici();
		if(datiAnagrafici!=null){			
			codFis = datiAnagrafici.getCodicefiscale();
			denominazione = datiAnagrafici.getDenominazione();
			codAteco = datiAnagrafici.getCodateco();
			codComuneLavoro = datiAnagrafici.getCodcomunelavoro();
 			indLavoro = datiAnagrafici.getIndirizzolavoro();
 			web = datiAnagrafici.getWeb();
		}
		///dati contatto
		DatiContatto contattoLav = datoreLavoro.getDatiContatto();
		if(contattoLav!=null){
			cognomeRef =Utils.notNull(contattoLav.getCognomereferente());
			nomeRef= Utils.notNull(contattoLav.getNomereferente());
			telefono = contattoLav.getTelefono();
			fax= contattoLav.getFax();
			email =contattoLav.getEmail();
		}
		///dati contatto alternativi
		if( datoreLavoro.getDatiContattoAlternativo()!=null){
			DatiContattoAlternativo contattoLavAlt = datoreLavoro.getDatiContattoAlternativo();
			cognomeRefAlt =Utils.notNull(contattoLavAlt.getCognomereferente());
			nomeRefAlt= Utils.notNull(contattoLavAlt.getNomereferente());
			telefonoAlt = contattoLavAlt.getTelefono();
			faxAlt= contattoLavAlt.getFax();
			emailAlt =contattoLavAlt.getEmail();
		}
	}

	//Dati Richiesta
	String numerolavoratori = "",attivitaprincipale = "", codprofessione = "",descrprofessione = "",descrizionericerca= "", descrizioneesperienza= "", esperienzarichiesta= "";
    String conoscenzeinformatiche = "",  trasferte = "", codmezzitrasporto = "";
    String codcomune= "", codtipologiacontratto= "",retribuzioneannualorda= "";
    String dataPubblicazione = "", dataScadenzaPubblicazione = "";
    String strProfiloRichiesto = "";
    String strEsperienzaRichiesta = "";
    List orari = new ArrayList();
    List titoliStudio = new ArrayList();;
    List lingue= new ArrayList();
    
	if(datiAnteprima!=null && datiAnteprima.getRichiesta()!=null){
		Richiesta richiesta = datiAnteprima.getRichiesta();
		ProfiloRichiesto prof = richiesta.getProfiloRichiesto();
		if(prof!=null){
			numerolavoratori = prof.getNumerolavoratori();
			attivitaprincipale = prof.getAttivitaprincipale();
			strProfiloRichiesto = attivitaprincipale;
			codprofessione = prof.getCodprofessione();
			descrprofessione = prof.getDescrprofessione();
			descrizionericerca = prof.getDescrizionericerca();
			descrizioneesperienza = prof.getDescrizioneesperienza();
			if(prof.getEsperienzarichiesta()!=null){
				esperienzarichiesta = prof.getEsperienzarichiesta().value();
				if(esperienzarichiesta.equals("S"))
					esperienzarichiesta = "SI";
				else
					esperienzarichiesta = "NO";
			}
		}
		IstruzioneFormazione istrForm = richiesta.getIstruzioneFormazione();
		if(istrForm!=null){
			conoscenzeinformatiche = istrForm.getConoscenzeinformatiche();
			if(istrForm.getTrasferte()!=null){
				trasferte = istrForm.getTrasferte().value();
				if(trasferte.equals("S"))
					trasferte = "SI";
				else
					trasferte = "NO";
			}
			if(istrForm.getCodmezzitrasporto()!=null){
				codmezzitrasporto = istrForm.getCodmezzitrasporto().value();
				if(codmezzitrasporto.equals("S"))
					codmezzitrasporto = "SI";
				else
					codmezzitrasporto = "NO";
			}
			titoliStudio = istrForm.getTitolostudio();
			lingue = istrForm.getLingua();
			orari = istrForm.getCodorario();
		}
		CondizioniOfferte cond = richiesta.getCondizioniOfferte();
		if(cond!=null){
			codcomune= cond.getCodcomune();
			codtipologiacontratto= cond.getCodtipologiacontratto();
			retribuzioneannualorda= cond.getRetribuzioneannualorda();
		}
		DurataRichiesta dur = richiesta.getDurataRichiesta();
		if(dur!=null){
			if(dur.getDatapubblicazione()!=null){
				dataPubblicazione = DateUtils.formatXMLGregorian(dur.getDatapubblicazione());
			}
			if(dur.getDatascadenzapubblicazione()!=null){
				dataScadenzaPubblicazione = DateUtils.formatXMLGregorian(dur.getDatascadenzapubblicazione());
			}
		}
	}
	
	//Registrazione Azienda
	String cognomeRichiedente = "", nomeRichiedente = "", emailRichiedente = "";
	String codicefiscale="", ragionesociale="",  indirizzosedeoperativa="",   capsedeoperativa="",  codcomunesedeoperativa="",  telefonosedeoperativa="",  faxsedeoperativa="";	 
	if(datiAnteprima!=null &&  datiAnteprima.getRegistrazioneAzienda()!=null){
		RegistrazioneAzienda regAz = datiAnteprima.getRegistrazioneAzienda();
		//Dati richiedente
		if(regAz.getDatiRichiedente()!=null){
			DatiRichiedente datiRich = regAz.getDatiRichiedente();
			cognomeRichiedente = Utils.notNull(datiRich.getCognome());
			nomeRichiedente = Utils.notNull(datiRich.getNome());
			emailRichiedente = datiRich.getEmailregistrazione();
		}
		//DatiAzienda
		if(regAz.getDatiAzienda()!=null){
			DatiAzienda datiAz = regAz.getDatiAzienda();
			codicefiscale = datiAz.getCodicefiscale();
			ragionesociale = datiAz.getRagionesociale();
			indirizzosedeoperativa = datiAz.getIndirizzosedeoperativa();
			capsedeoperativa = datiAz.getCapsedeoperativa();
			codcomunesedeoperativa = datiAz.getCodcomunesedeoperativa();
			telefonosedeoperativa = datiAz.getTelefonosedeoperativa();
			faxsedeoperativa = datiAz.getFaxsedeoperativa();
		}
	}
	//Dati Sistema
	String numeroofferta="", annoofferta="", provenienza="",  cpi="";
	if(datiAnteprima!=null && datiAnteprima.getDatiSistema()!=null){
		DatiSistema datiSis = datiAnteprima.getDatiSistema();
		numeroofferta = datiSis.getNumeroofferta();
		annoofferta = datiSis.getAnnoofferta();
		provenienza = datiSis.getProvenienza();
		cpi = datiSis.getCpi();
	}
	//Dati Aggiuntivi 
	String opzTipoDecodifiche="",opzInvioClicl="",fuorisede="",automunito="",motomunito="",milite="",sesso="",
		    motivosesso="",notamotivosesso="",codarea="",localita="",trasferta="",alloggio="",noteoperatore="",nomeoperatore="",cognomeoperatore="";
	List codturni = new ArrayList();
	String numda="", numa="", codmoteta="", notamotivoeta="", esperienza="", anniesperienza="", formazioneprof="", notaesperienza="";
	String rangeEta="";
	List agevolazioni  = new ArrayList();
	List codcontratti  = new ArrayList();
	List albi  = new ArrayList();
	List patenti = new ArrayList();
	List patentini = new ArrayList();
	String datiAziendaPub="", mansionePub="", contenutoPub="", luogoPub="", formazionePub="", contrattoPub="", conoscenzePub="", caratteristichePub="", orarioPub="", candidaturaPub="";
	if(datiAnteprima!=null && datiAnteprima.getDatiAggiuntivi()!=null){
		DatiAggiuntivi datiAggiuntivi = datiAnteprima.getDatiAggiuntivi();
		DatiGenerali datiGen = datiAggiuntivi.getDatiGenerali();
		opzTipoDecodifiche = datiGen.getOpzTipoDecodifiche().value();
		opzInvioClicl = datiGen.getOpzInvioClicl().value();
		if(opzInvioClicl.equals("Y"))
			opzInvioClicl = "SI";
		else
			opzInvioClicl = "NO";
		if(datiGen.getFuorisede()!= null){
			fuorisede = datiGen.getFuorisede().value();
			if(fuorisede.equals("Y"))
				fuorisede = "SI";
			else
				fuorisede = "NO";
		}
		if(datiGen.getAutomunito()!= null){
			automunito = datiGen.getAutomunito().value();
			if(automunito.equals("Y"))
				automunito = "SI";
			else if(automunito.equals("P"))
				automunito = "PREFERIBILE";
			else
				automunito = "NO";
		}
		if(datiGen.getMotomunito()!= null){
			motomunito = datiGen.getMotomunito().value();
			if(motomunito.equals("Y"))
				motomunito = "SI";
			else if(motomunito.equals("P"))
				motomunito = "PREFERIBILE";
			else
				motomunito = "NO";
		}
		if(datiGen.getMilite()!= null){
			milite = datiGen.getMilite().value();
			if(milite.equals("Y"))
				milite = "SI";
			else if(milite.equals("P"))
				milite = "PREFERIBILE";
			else
				milite = "NO";
		}
		if(datiGen.getSesso()!= null){
			sesso = datiGen.getSesso().value();
			if(sesso.equals("F"))
				sesso = "Femminile";
			else
				sesso = "Maschile";
		}
		if(datiGen.getMotivosesso()!= null)
			motivosesso =  datiGen.getMotivosesso();
		if(datiGen.getNotamotivosesso()!= null)
			notamotivosesso =  datiGen.getNotamotivosesso();
		if(datiGen.getCodarea()!= null)
			codarea = datiGen.getCodarea();
		if(datiGen.getLocalita()!= null)
			localita = datiGen.getLocalita();
		if(datiGen.getTrasferta()!= null)
			trasferta = datiGen.getTrasferta();
		if(datiGen.getAlloggio()!= null){
			alloggio = datiGen.getAlloggio().value();
			if(alloggio.equals("Y"))
				alloggio = "SI";
			else
				alloggio = "NO";
		}
		if(datiGen.getNoteoperatore()!= null)
			noteoperatore = datiGen.getNoteoperatore();
		
		if(datiGen.getNomeOperatore()!= null)
			nomeoperatore = datiGen.getNomeOperatore();
		
		if(datiGen.getCognomeOperatore()!= null)
			cognomeoperatore = datiGen.getCognomeOperatore();
		//turni
		if(datiAggiuntivi.getTurni()!=null){
			codturni = datiAggiuntivi.getTurni().getCodturno();
		}
		if(datiAggiuntivi.getEsperienze()!=null){
			Esperienze esp = datiAggiuntivi.getEsperienze();
			numda= Utils.notNull(esp.getNumda()); 
			numa= Utils.notNull(esp.getNuma()); 
			if(StringUtils.isEmptyNoBlank(numda)){
				rangeEta ="da " + numda;
			}
			if(StringUtils.isEmptyNoBlank(numa)){
				rangeEta +=" a " + numa;
			}
			codmoteta= esp.getCodmoteta(); 
			notamotivoeta= esp.getNotamotivoeta(); 
			if(esp.getEsperienza()!=null){
				esperienza= esp.getEsperienza().value();
				if(esperienza.equals("Y"))
					esperienza = "SI";
				else if(esperienza.equals("P"))
					esperienza = "PREFERIBILE";
				else
					esperienza = "NO";
			}
			strEsperienzaRichiesta = esperienza;
			anniesperienza= esp.getAnniesperienza();
			if(esp.getFormazioneprof()!=null){
				formazioneprof= esp.getFormazioneprof().value(); 
				if(formazioneprof.equals("Y"))
					formazioneprof = "SI";
				else if(formazioneprof.equals("P"))
					formazioneprof = "PREFERIBILE";
				else
					formazioneprof = "NO";
			}
			notaesperienza= esp.getNotaesperienza();
		}
		agevolazioni = datiAggiuntivi.getAgevolazioni();
		if(datiAggiuntivi.getTipoRapporto()!=null)
			codcontratti = datiAggiuntivi.getTipoRapporto().getCodcontratto();
		if(datiAggiuntivi.getAbilitazioni()!=null){
			Abilitazioni abil = datiAggiuntivi.getAbilitazioni();
			albi = abil.getAlbi();
			patenti = abil.getPatenti();
			patentini = abil.getPatentini();
		}
		if(datiAggiuntivi.getPubblicazione()!=null){
			Pubblicazione pubb = datiAggiuntivi.getPubblicazione();
			datiAziendaPub= pubb.getDatiAziendaPub(); 
			mansionePub= pubb.getMansionePub(); 
			contenutoPub=  Utils.notNull(pubb.getContenutoPub());
			if(StringUtils.isEmptyNoBlank(contenutoPub)){
				try{
					contenutoPub = datiAnteprima.getRichiesta().getProfiloRichiesto().getDescrizionericerca();
				}catch(NullPointerException eee){
					contenutoPub = "";
				}
			}
			luogoPub= pubb.getLuogoPub(); 
			formazionePub= pubb.getFormazionePub(); 
			contrattoPub= pubb.getContrattoPub(); 
			conoscenzePub= pubb.getConoscenzePub(); 
			caratteristichePub= pubb.getCaratteristichePub(); 
			orarioPub= pubb.getOrarioPub(); 
			candidaturaPub= pubb.getCandidaturaPub();
		}
	}
 
	boolean canModify = false;
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
	
%>
    <%@ include file="_infCorrentiAzienda.inc" %>  
	<html>
	<head>
		<title>Anteprima invio Vacancy da SIL a Portale</title>
		<link rel="stylesheet" href="../../css/stili.css" type="text/css">
		<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
		<af:linkScript path="../../js/"/>
		<SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>	
										
	</head>		
		
	<body class="gestione" onload="rinfresca()">
	<center>
      <font color="green">
        <af:showMessages prefix="M_ANTEPRIMA_INVIO_VACANCY_SIL" />
      </font>
      <font color="red">
        <af:showErrors/>
      </font>
    </center>
			<p class="titolo">Dati Personali del datore di lavoro</p>
			<% out.print(htmlStreamTop); %>
			<table class="main" border="0">
  				<tr valign="top"><td colspan="4" width="100%"><div class="sezione3">Dati Anagrafici</div></td></tr>
  				<tr valign="top">
  					<td class="etichetta2">Codice Fiscale</td>
  					<td class="campo2" valign="top" ><af:textBox type="text" classNameBase="input" name="cf" readonly="true" value="<%= Utils.notNull(codFis) %>"/></td>
  					<td class="etichetta2">Denominazione</td>
  					<td class="campo2" valign="top" ><af:textBox type="text" classNameBase="input" name="den" readonly="true" size="70"  value="<%= Utils.notNull(denominazione) %>"/></td>
  				</tr>
  				<tr valign="top">
  					<td class="etichetta2">Indirizzo</td>
  					<td class="campo2" valign="top"  colspan="3"><af:textBox type="text" classNameBase="input" name="indl"  size="70" readonly="true" value="<%= Utils.notNull(indLavoro) %>"/>
  				</tr>
  				<tr valign="top">
  					<td class="etichetta2">Comune</td>  					
  					<td class="campo2" valign="top"  colspan="3"><af:comboBox classNameBase="input" name="descComu" title="Comune" selectedValue="<%=codComuneLavoro%>" disabled="true" moduleName="M_GetListaComuniAnteprimaVacancy" addBlank="true" /></td>
  				</tr>
  				<tr valign="top">
  					<td class="etichetta2">Settore</td>
  					<td class="campo2" valign="top"  colspan="3"><af:comboBox classNameBase="input" name="codAteco" title="codAteco" selectedValue="<%=codAteco%>" disabled="true" moduleName="M_GetSettoreAnteprimaVacancy" addBlank="true" />
  					</td>
  				</tr>
  				 <tr valign="top">
  					<td class="etichetta2">Sito Web</td>
  					<td class="campo2" valign="top" colspan="3"><af:textBox type="text" classNameBase="input" name="sito" size="70"  readonly="true" value="<%= Utils.notNull(web) %>"/></td>
  				</tr>
  				<tr valign="top"><td colspan="4">&nbsp;</tr>
				 
  				<tr valign="top"><td colspan="4" width="100%"><div class="sezione3">Dati contatto</div></td></tr>
  				
  				<tr valign="top">
  					<td class="etichetta2">Referente</td>
  					<td class="campo2" valign="top"  colspan="3"><af:textBox type="text" classNameBase="input" name="referente" readonly="true" size="70" value='<%= Utils.notNull(cognomeRef + " "+ nomeRef) %>'/></td>
  				</tr>
  				<tr valign="top">
  					<td class="etichetta2">Telefono</td>
  					<td class="campo2" valign="top" ><af:textBox type="text" classNameBase="input" name="tel" readonly="true" value="<%= Utils.notNull(telefono) %>"/></td>
  					<td class="etichetta2">Fax</td>
  					<td class="campo2" valign="top" ><af:textBox type="text" classNameBase="input" name="fax" readonly="true" value="<%= Utils.notNull(fax) %>"/></td>
  				</tr>
  				<tr valign="top">
  					<td class="etichetta2">Email</td>
  					<td class="campo2" valign="top"  colspan="3"><af:textBox type="text" classNameBase="input" name="email"  size="70"  readonly="true" value="<%= Utils.notNull(email) %>"/></td>
  				</tr>
  				<tr valign="top"><td colspan="4">&nbsp;</tr>
	 
  				<tr valign="top"><td colspan="4" width="100%"><div class="sezione3">Dati alternativi contatto</div></td></tr>
  				 
 				<tr valign="top">
  					<td class="etichetta2">Referente</td>
  					<td class="campo2" valign="top"  colspan="3"><af:textBox type="text" classNameBase="input" name="refalt"  size="70" readonly="true" value='<%= Utils.notNull(cognomeRefAlt+ " " + nomeRefAlt) %>'/></td>
  				</tr>
  				<tr valign="top">
  					<td class="etichetta2">Telefono</td>
  					<td class="campo2" valign="top" ><af:textBox type="text" classNameBase="input" name="refalt" readonly="true" value="<%= Utils.notNull(telefonoAlt) %>"/></td>
  					<td class="etichetta2">Fax</td>
  					<td class="campo2" valign="top" ><af:textBox type="text" classNameBase="input" name="faxalt" readonly="true" value="<%= Utils.notNull(faxAlt) %>"/></td>
  				</tr>
  				<tr valign="top">
  					<td class="etichetta2">Email</td>
  					<td class="campo2" valign="top"  colspan="3"><af:textBox type="text" classNameBase="input" name="mailalt"  size="70" readonly="true" value="<%= Utils.notNull(emailAlt) %>"/></td>
  				</tr>
  			</table>
			<% out.print(htmlStreamBottom); %>
			<p class="titolo">Dati Relativi alla richiesta di personale</p>
			<% out.print(htmlStreamTop); %>
			<table class="main" border="0">
				<tr valign="top"><td colspan="4" width="100%"><div class="sezione3">Profilo Professionale Ricercato</div></td></tr>
				
  				<tr valign="top">
  					<td class="etichetta2">Numero lavoratori</td>
  					<td class="campo2" valign="top"  colspan="3"><af:textBox type="text" classNameBase="input" name="numlav" readonly="true" value="<%= Utils.notNull(numerolavoratori) %>"/></td>
  				</tr>
  				<tr valign="top">
  					<td class="etichetta2">Qualifica professionale offerta</td>
  					<td class="campo2" valign="top"  colspan="3"><af:textBox type="text" classNameBase="input" name="profstr" size="70"  readonly="true" value="<%= Utils.notNull(descrprofessione) %>"/></td>
  				</tr>
  				<tr valign="top">
  					<td class="etichetta2">Profilo richiesto</td>
  					<td class="campo2" valign="top"  colspan="3"><af:textBox type="text" classNameBase="input" name="profrich"  size="70"  readonly="true" value="<%= Utils.notNull(strProfiloRichiesto) %>"/></td>
  				</tr> 
				<tr> 
  					<td class="etichetta2">Esperienza richiesta</td>
  					<td class="campo2" valign="top"  colspan="3"><af:textBox type="text" classNameBase="input" name="esprstr"  size="70" readonly="true" value="<%= Utils.notNull(strEsperienzaRichiesta) %>"/></td>
  				</tr>  
  				<tr valign="top"><td colspan="4">&nbsp;</tr>
  				<tr valign="top"><td colspan="4" width="100%"><div class="sezione3">Titoli di studio</div></td></tr>
 				
  				<% 
	  				String idTitoloStudio = "", descrizioneStudio = "", speficifaTitolo = "",conseguito ="", titIndisp="";
	  				Titolostudio rigaTitolo = null;
	  				
	  				if(titoliStudio != null){
	  				%>
	  				
  					<tr valign="top">
  						<td colspan="4">
  							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr valign="top">
					  				<td align="center" class="campo2">Titolo</td>
					  				<td align="center" class="campo2">Specifica</td>
					  				<td align="center" class="campo2">Conseguito</td>
					  				<td align="center" class="campo2">Indispensabile</td>	
					  			</tr>  			
   							<%
		  					for(int i=0; i < titoliStudio.size(); i++){
		  						rigaTitolo =  (Titolostudio) titoliStudio.get(i);
		  						idTitoloStudio = rigaTitolo.getCodtitolostudio();
			  					descrizioneStudio = rigaTitolo.getDescrizionestudio();
						        speficifaTitolo = rigaTitolo.getSpecifica();
								if(rigaTitolo.getConseguito()!=null){
									conseguito = rigaTitolo.getConseguito().value();
									if(conseguito.equals("Y"))
										conseguito = "SI";
									else
										conseguito = "NO";
								}
								
								if(rigaTitolo.getTitoloindisp()!=null){
						       		titIndisp = rigaTitolo.getTitoloindisp().value();
									if(titIndisp.equals("Y"))
										titIndisp = "SI";
									else
										titIndisp = "NO";
								}
			  				%>	
								<tr valign="top">
					  				<td align="center" class="campo2"><b><%= Utils.notNull(descrizioneStudio) %></b></td>
					  				<td align="center" class="campo2"><b><%= Utils.notNull(speficifaTitolo) %></b></td>
					  				<td align="center" class="campo2"><b><%= Utils.notNull(conseguito) %></b></td>
					  				<td align="center" class="campo2"><b><%= Utils.notNull(titIndisp) %></b></td>
					  			</tr>  		
							<% } %>
	  						
	  						</table>
	  					</td>
	  				</tr>
	  				<% } %>
				 	<tr valign="top"><td colspan="4">&nbsp;</tr>
	  				<tr valign="top"><td colspan="4" width="100%"><div class="sezione3">Conoscenze linguistiche</div></td></tr>
	  			 
  				<% 
	  				String strLingua = "", idLivelloLetto = "", idLivelloScritto = "", idLivelloParlato = "", linguaIndisp="";
	  				Lingua rigaLingua = null;
	  				
	  				if(lingue != null){
	  				%>
	  				
	  				<tr valign="top">
	  					<td colspan="4">
	  						<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr valign="top">
					  				<td align="center" class="campo2">Lingua</td>
					  				<td align="center" class="campo2">Livello Letto</td>
					  				<td align="center" class="campo2">Livello Scritto</td>
					  				<td align="center" class="campo2">Livello Parlato</td>
					  				<td align="center" class="campo2">Indispensabile</td>	
					  			</tr>  	
	  						 				
				  				<%
			  					for(int i=0; i < lingue.size(); i++){
			  						rigaLingua = (Lingua) lingue.get(i);
			  						strLingua = rigaLingua.getCodlingua();
			  						idLivelloLetto = rigaLingua.getCodlivelloletto();
			  						idLivelloScritto = rigaLingua.getCodlivelloscritto();
			  						idLivelloParlato = rigaLingua.getCodlivelloparlato();
									if(rigaLingua.getLinguaindisp()!=null){
										linguaIndisp = rigaLingua.getLinguaindisp().value();
										if(linguaIndisp.equals("Y"))
										   linguaIndisp = "SI";
										else
										   linguaIndisp = "NO";
									}
										
				  				%>	
					  			<tr valign="top">
					  				<td align="center" class="campo2"><af:comboBox classNameBase="input" name="descLingua" title="lingua" selectedValue="<%=strLingua%>" disabled="true" moduleName="M_GetDatiLingueAnteprimaVacancy" addBlank="true" /></td>
					  				<td align="center" class="campo2"><af:comboBox classNameBase="input" name="livletto" title="livletto" selectedValue="<%=idLivelloLetto%>" disabled="true" moduleName="M_GetDatiLivelloLingueAnteprimaVacancy" addBlank="true" /></td>
					  				<td align="center" class="campo2"><af:comboBox classNameBase="input" name="livscritto" title="livscritto" selectedValue="<%=idLivelloScritto%>" disabled="true" moduleName="M_GetDatiLivelloLingueAnteprimaVacancy" addBlank="true" /></td>
									<td align="center" class="campo2"><af:comboBox classNameBase="input" name="livparlato" title="livparlato" selectedValue="<%=idLivelloParlato%>" disabled="true" moduleName="M_GetDatiLivelloLingueAnteprimaVacancy" addBlank="true" /></td>
									<td align="center" class="campo2"><af:textBox type="text" classNameBase="input" name="lingindisp" readonly="true" value="<%= Utils.notNull(linguaIndisp) %>"/></td>
					  			</tr>	  				
			 					<% 	} %>
 							</table>
	  					</td>
	  				</tr>
  					<% }  %>
	  			<tr valign="top"><td colspan="4">&nbsp;</tr>
  				<tr valign="top"><td colspan="4" width="100%"><div class="sezione3">Altre Informazioni</div></td></tr>
  				<tr valign="top">
	  				<td class="etichetta2">Conoscenze informatiche</td> 
		  			<td class="campo2" valign="top"  colspan="3"><af:textBox type="text"  size="70"  classNameBase="input" name="inform" readonly="true" value="<%= Utils.notNull(conoscenzeinformatiche) %>"/></td>
		  		</tr>				
  				<tr valign="top">
  					<td class="etichetta2">Disponibilità alle trasferte</td>
		  			<td class="campo2" valign="top"  colspan="3"><af:textBox type="text" classNameBase="input" name="trasferte" readonly="true" value="<%= Utils.notNull(trasferte) %>"/></td>
  				</tr>
  				<tr valign="top">
  					<td class="etichetta2">Disponibilità mezzo di trasporto</td>
		  			<td class="campo2" valign="top"  colspan="3"><af:textBox type="text" classNameBase="input" name="codmezzi" readonly="true" value="<%= Utils.notNull(codmezzitrasporto) %>"/></td>
  				</tr>
  		 		<tr valign="top"><td colspan="4">&nbsp;</tr>
  				<tr valign="top"><td colspan="4" width="100%"><div class="sezione3">Orari di lavoro</div></td></tr>
  				 
  				<% 
  				 	if(orari != null){
						for(int i=0; i < orari.size(); i++){
							String codOra = (String) orari.get(i);
							if(i==0){
							%>
								 <tr valign="top">
	  								<td class="etichetta2">Orario</td>
			  						<td class="campo2" valign="top"  colspan="3"><af:comboBox classNameBase="input" name="orario" title="orario" selectedValue="<%=codOra%>" disabled="true" moduleName="M_GetDatiOrarioAnteprimaVacancy" addBlank="true" /></td>
	  							</tr>
						<%	}else{%>
								<tr valign="top">
	  								<td class="etichetta2">&nbsp;</td>
			  						<td class="campo2" valign="top"  colspan="3"><af:comboBox classNameBase="input" name="orario" title="orario" selectedValue="<%=codOra%>" disabled="true" moduleName="M_GetDatiOrarioAnteprimaVacancy" addBlank="true" /></td>
	  							</tr>

						<%	}
						}
  					}
  				%>
  				</table>
			<% out.print(htmlStreamBottom); %>	
			
			<% out.print(htmlStreamTop); %>
			<table class="main" border="0">
				<tr valign="top"><td colspan="2" width="100%"><div class="sezione3">Condizioni Lavorative Offerte</div></td></tr>
				 
  				<tr valign="top">
  					<td class="etichetta2">Sede di lavoro</td>
  					<td class="campo2" valign="top" ><af:comboBox classNameBase="input" name="descComu2" title="sede di lavoro" selectedValue="<%=codcomune%>" disabled="true" moduleName="M_GetListaComuniAnteprimaVacancy" addBlank="true" /></td>
  				</tr> 
  				<tr valign="top">
  					<td class="etichetta2">Tipo di rapporto di lavoro offerto</td>
  					<td class="campo2" valign="top" ><af:comboBox classNameBase="input" name="tipocontratto" title="tipocontratto" selectedValue="<%=codtipologiacontratto%>" disabled="true" moduleName="M_GetDatiContrattoAnteprimaVacancy" addBlank="true" /></td>
  				</tr>
  				<tr valign="top">
  					<td class="etichetta2">Retribuzione annua lorda</td>
  					<td class="campo2" valign="top" ><af:textBox type="text" classNameBase="input" name="lordoannuo" readonly="true" value="<%= Utils.notNull(retribuzioneannualorda) %>"/></td>
  				</tr>
  			</table>
			<% out.print(htmlStreamBottom); %>	
			
			<% out.print(htmlStreamTop); %>
			<table class="main" border="0">
				<tr valign="top"><td colspan="4" width="100%"><div class="sezione3">Durata della richiesta</div></td></tr>
				 
  				<tr valign="top">
  					<td class="etichetta2">Data di pubblicazione</td>
  					<td class="campo2" valign="top" ><af:textBox type="text" classNameBase="input" name="datapubb" readonly="true" value="<%= Utils.notNull(dataPubblicazione) %>"/></td>
  					<td class="etichetta2">Data di scadenza</td>
  					<td class="campo2" valign="top" ><af:textBox type="text" classNameBase="input" name="datascade" readonly="true" value="<%= Utils.notNull(dataScadenzaPubblicazione) %>"/></td>
  				</tr>
  			</table>
			<% out.print(htmlStreamBottom); %>
	
			<p class="titolo">Dati registrazione azienda</p>
			<% out.print(htmlStreamTop); %>
			<table class="main" border="0">
				<tr valign="top"><td colspan="4" width="100%"><div class="sezione3">Dati richiedente</div></td></tr>
				 
  				<tr valign="top">
  					<td class="etichetta2">Cognome e nome</td>
  					<td class="campo2" valign="top"  colspan="3"><af:textBox type="text" classNameBase="input" size="70"  name="richiedente" readonly="true" value='<%= cognomeRichiedente + " "+ nomeRichiedente %>'/></td>
  				</tr> 
  				<tr valign="top">
  					<td class="etichetta2">Email</td>
  					<td class="campo2" valign="top"  colspan="3"><af:textBox type="text" classNameBase="input"  size="70" name="mailrich" readonly="true" value="<%= Utils.notNull(emailRichiedente) %>"/></td>
  				</tr>
			 	<tr valign="top"><td colspan="4">&nbsp;</tr>
				<tr valign="top"><td colspan="4" width="100%"><div class="sezione3">Dati azienda (sede operativa)</div></td></tr>
	  			 
				<tr valign="top">
  					<td class="etichetta2">Codice fiscale</td>
  					<td class="campo2" valign="top"  colspan="3"><af:textBox type="text" classNameBase="input" name="cfaz" readonly="true" value="<%= Utils.notNull(codicefiscale) %>"/></td>
  				</tr>
  				<tr valign="top">
  					<td class="etichetta2">Ragione sociale</td>
  					<td class="campo2" valign="top"  colspan="3"><af:textBox type="text" classNameBase="input"  size="70" name="ragsoc" readonly="true" value="<%= Utils.notNull(ragionesociale) %>"/></td>
  				</tr>
				<tr valign="top">
  					<td class="etichetta2">Comune</td>
  					<td class="campo2" valign="top" ><af:comboBox classNameBase="input" name="descComu3" title="sedeoperativa" selectedValue="<%=codcomunesedeoperativa%>" disabled="true" moduleName="M_GetListaComuniAnteprimaVacancy" addBlank="true" /></td>
					<td class="etichetta2">CAP</td>
  					<td class="campo2" valign="top" ><af:textBox type="text" classNameBase="input" name="capsedop" readonly="true" value="<%= Utils.notNull(capsedeoperativa) %>"/></td>
  				</tr>
				<tr valign="top">
  					<td class="etichetta2">Indirizzo</td>
  					<td class="campo2" valign="top"  colspan="3"><af:textBox type="text" classNameBase="input"  size="70" name="indsedop" readonly="true" value="<%= Utils.notNull(indirizzosedeoperativa) %>"/></td>
  				</tr>
  				<tr valign="top">
  					<td class="etichetta2">Telefono</td>
  					<td class="campo2" valign="top" ><af:textBox type="text" classNameBase="input" name="telsedop" readonly="true" value="<%= Utils.notNull(telefonosedeoperativa) %>"/></td>
					<td class="etichetta2">Fax</td>
  					<td class="campo2" valign="top" ><af:textBox type="text" classNameBase="input" name="faxsedop" readonly="true" value="<%= Utils.notNull(faxsedeoperativa) %>"/></td>
  				</tr>
  			</table>
			<% out.print(htmlStreamBottom); %>	
			
			<% out.print(htmlStreamTop); %>
			<table class="main" border="0">
  				<tr valign="top"><td colspan="2" width="100%"><div class="sezione3">Dati sistema</div></td></tr>
  				<tr valign="top">
  					<td class="etichetta2">Numero offerta</td>
  					<td class="campo2" valign="top" ><af:textBox type="text" classNameBase="input" name="numoff" readonly="true" value="<%= Utils.notNull(numeroofferta) %>"/></td>
  				</tr> 
  				<tr valign="top">
  					<td class="etichetta2">Anno offerta</td>
  					<td class="campo2" valign="top" ><af:textBox type="text" classNameBase="input" name="annoOff" readonly="true" value="<%= Utils.notNull(annoofferta) %>"/></td>
  				</tr>
				<tr valign="top">
  					<td class="etichetta2">Provenienza</td>
  					<td class="campo2" valign="top" ><af:textBox type="text" classNameBase="input" name="provenienza" readonly="true" value="<%= Utils.notNull(provenienza) %>"/></td>
  				</tr>
  				<tr valign="top">
  					<td class="etichetta2">CPI</td>
  					<td class="campo2" valign="top" ><af:comboBox classNameBase="input" name="cpi" title="cpi" selectedValue="<%=cpi%>" disabled="true" moduleName="M_GetListaCPIAnteprimaVacancy" addBlank="true" /></td>
  				</tr>
  			</table>
			<% out.print(htmlStreamBottom); %>	
			
			
 			<p class="titolo">Dati Aggiuntivi</p>
			<% out.print(htmlStreamTop); %>
			<table class="main" border="0">
				<tr valign="top"><td colspan="2" width="100%"><div class="sezione3">Dati generali</div></td></tr>
				 
  				<tr valign="top">
  					<td class="etichetta2">Fuorisede</td>
  					<td class="campo2" valign="top" ><af:textBox type="text" classNameBase="input" name="fuorisede" readonly="true" value="<%= Utils.notNull(fuorisede) %>"/></td>
  				</tr>
				<tr valign="top">
  					<td class="etichetta2">Automunito</td>
  					<td class="campo2" valign="top" ><af:textBox type="text" classNameBase="input" name="automunito" readonly="true" value="<%= Utils.notNull(automunito) %>"/></td>
  				</tr>
  				<tr valign="top"> 		
  					<td class="etichetta2" valign="top">Motomunito</td>
  					<td class="campo2" valign="top" ><af:textBox type="text" classNameBase="input" name="motomunito" readonly="true" value="<%= Utils.notNull(motomunito) %>"/></td>
  				</tr>
  				<tr valign="top">
  					<td class="etichetta2">Milite esente</td>
  					<td class="campo2" valign="top" ><af:textBox type="text" classNameBase="input" name="milite" readonly="true" value="<%= Utils.notNull(milite) %>"/></td>
  				</tr>
				<tr valign="top">
  					<td class="etichetta2">Sesso</td>
  					<td class="campo2" valign="top" ><af:textBox type="text" classNameBase="input" name="sesso" readonly="true" value="<%= Utils.notNull(sesso) %>"/></td>
  				</tr>
  				<tr valign="top">
  					<td class="etichetta2">Motivazione sesso</td>
  					<td class="campo2" valign="top" ><af:textBox type="text" classNameBase="input"  size="70" name="motsess" readonly="true" value="<%= Utils.notNull(motivosesso) %>"/></td>
  				</tr>
  				<tr valign="top">
  					<td class="etichetta2">Nota motivazione sesso</td>
  					<td class="campo2" valign="top" ><af:textArea
									classNameBase="textarea" name="notamotivosesso" cols="50"
									value="<%=Utils.notNull(notamotivosesso) %>" maxlength="2000"
									readonly="<%=String.valueOf(!canModify)%>" /></td>
  				</tr>
				<tr valign="top">
  					<td class="etichetta2">Area</td>
  					<td class="campo2" valign="top" ><af:comboBox classNameBase="input" name="area" title="area" selectedValue="<%=codarea%>" disabled="true" moduleName="M_GetAreaAnteprimaVacancy" addBlank="true" /></td>
  				</tr>
  				<tr valign="top">  
  					<td class="etichetta2">Localit&agrave;</td>
  					<td class="campo2" valign="top" ><af:textBox type="text" classNameBase="input"  size="70" name="localita" readonly="true" value="<%= Utils.notNull(localita) %>"/></td>
  				</tr>
  				<tr valign="top">
  					<td class="etichetta2">Trasferta</td>
  					<td class="campo2" valign="top"  ><af:comboBox classNameBase="input" name="trasferta" title="trasferta" selectedValue="<%=trasferta%>" disabled="true" moduleName="M_GetTrasfertaAnteprimaVacancy" addBlank="true" /></td>
  				</tr>
				<tr valign="top">
  					<td class="etichetta2">Alloggio</td>
  					<td class="campo2" valign="top" ><af:textBox type="text" classNameBase="input" name="alloggio" readonly="true" value="<%= Utils.notNull(alloggio) %>"/></td>
  				</tr>
				<tr valign="top">
  					<td class="etichetta2">Nome operatore</td>
					<td class="campo2" valign="top" ><af:textBox type="text" classNameBase="input" name="nomeoperatore" readonly="true" value="<%= Utils.notNull(nomeoperatore) %>"/></td>  				</tr>

				<tr valign="top">
  					<td class="etichetta2">Cognome operatore</td>
  					<td class="campo2" valign="top" ><af:textBox type="text" classNameBase="input" name="cognomeoperatore" readonly="true" value="<%= Utils.notNull(cognomeoperatore) %>"/></td>
  				</tr>

				<tr valign="top">
  					<td class="etichetta2">Note operatore</td>
  							<td class="campo2" valign="top" ><af:textArea
									classNameBase="textarea" name="noteoperatore" cols="50"
									value="<%=Utils.notNull(noteoperatore)%>" maxlength="2000"
									readonly="<%=String.valueOf(!canModify)%>" /></td>
  				</tr>
  				
  				<%
                     if(infCorrentiAzienda != null) {%> 
                        <%infCorrentiAzienda.showMinimalInfo(out); %>
               <%
                     } %>
  				
	 			<tr valign="top"><td colspan="2">&nbsp;</td></tr>
	 			<tr valign="top"><td colspan="2" width="100%"><div class="sezione3">Contratti e turni lavorativi</div></td></tr> 
  						 
  				<% 
  				 	if(codcontratti != null){
						for(int i=0; i < codcontratti.size(); i++){
							String contratto = (String) codcontratti.get(i);
							 if(i==0){
							%>
							<tr valign="top">
  								<td class="etichetta2">Contratto</td>
		  						<td class="campo2" valign="top" >
		  						<af:comboBox classNameBase="input" 
		  						name="contratto" title="contratto"
		  						 selectedValue="<%=contratto%>" 
		  						 disabled="true" moduleName="M_GetDatiContrattoAnteprimaVacancy" 
		  						 addBlank="true" /></td>
  							</tr>
						<%	}else{%>
								 <tr valign="top">
  								<td class="etichetta2">&nbsp;</td>
		  						<td class="campo2" valign="top" >
		  						<af:comboBox classNameBase="input" 
		  						name="contratto" title="contratto"
		  						 selectedValue="<%=contratto%>" 
		  						 disabled="true" moduleName="M_GetDatiContrattoAnteprimaVacancy" 
		  						 addBlank="true" /></td>
  							</tr>

						<%	}
							
					 	}
  					}
  				
  				 	if(codturni != null){
						for(int i=0; i < codturni.size(); i++){
							String turno = (String) codturni.get(i);
						 if(i==0){
							%>
						 <tr valign="top">
  								<td class="etichetta2">Turno</td>
		  						<td class="campo2" valign="top" >
		  						<af:comboBox classNameBase="input" name="turno" 
		  						title="turno" selectedValue="<%=turno%>" 
		  						disabled="true" moduleName="M_GetDatiTurniAnteprimaVacancy" 
		  						addBlank="true" /></td>
  							</tr>
						<%	}else{%>
							 <tr valign="top">
  								<td class="etichetta2">&nbsp;</td>
		  						<td class="campo2" valign="top" >
		  						<af:comboBox classNameBase="input" name="turno" 
		  						title="turno" selectedValue="<%=turno%>" 
		  						disabled="true" moduleName="M_GetDatiTurniAnteprimaVacancy" 
		  						addBlank="true" /></td>
  							</tr>

						<%	}
							
					 	}
			 
  					}
  				%>
  				
				<tr valign="top"><td colspan="2">&nbsp;</td></tr>
	 			<tr valign="top"><td colspan="2" width="100%"><div class="sezione3">Esperienze</div></td></tr> 
  				 
				<tr valign="top">
  					<td class="etichetta2">Et&agrave;</td>
  					<td class="campo2" valign="top" ><af:textBox type="text" classNameBase="input" name="eta" readonly="true" value="<%=Utils.notNull(rangeEta) %>"/></td>
  				</tr>
				<tr valign="top">
  					<td class="etichetta2">Motivazione et&agrave;</td>
  					<td class="campo2" valign="top" >
  					<af:comboBox classNameBase="input" name="moteta" title="moteta" 
  					selectedValue="<%=codmoteta%>" disabled="true"
  					 moduleName="M_GetMotEtaAnteprimaVacancy" addBlank="true" /></td>
  				</tr>
				<tr valign="top">
  					<td class="etichetta2">Note motivazione et&agrave;</td>
  							<td class="campo2" valign="top" ><af:textArea
									classNameBase="textarea" name="notamotivoeta" cols="50"
									value="<%=Utils.notNull(notamotivoeta)%>" maxlength="2000"
									readonly="<%=String.valueOf(!canModify)%>" /></td>
  				</tr>
  				<tr valign="top">
  					<td class="etichetta2">Esperienza</td>
  					<td class="campo2" valign="top" ><af:textBox type="text" classNameBase="input" name="espagg" readonly="true" value="<%=Utils.notNull(esperienza) %>"/></td>
  				</tr>
  				<tr valign="top">
					<td class="etichetta2">Numero anni di esperienza</td>
  					<td class="campo2" valign="top" ><af:textBox type="text" classNameBase="input" name="anniesp" readonly="true" value="<%=Utils.notNull(anniesperienza) %>"/></td>
  				</tr>
				<tr valign="top">
  					<td class="etichetta2">Formazione professionale</td>
  					<td class="campo2" valign="top" ><af:textBox type="text" classNameBase="input" name="formp" readonly="true" value="<%=Utils.notNull(formazioneprof) %>"/></td>
  				</tr>
				<tr valign="top">
  					<td class="etichetta2">Note esperienza</td>
  					<td class="campo2" valign="top" ><af:textArea
									classNameBase="textarea" name="notaesperienza" cols="50"
									value="<%=Utils.notNull(notaesperienza) %>" maxlength="2000"
									readonly="<%=String.valueOf(!canModify)%>" /></td>
  				</tr>
  				<tr valign="top"><td colspan="2">&nbsp;</td></tr>
	 			<tr valign="top"><td colspan="2" width="100%"><div class="sezione3">Agevolazioni</div></td></tr> 
  				 
  				 
  				<% 
  				String agevolazione = "", agevolIndisp="";
  				Agevolazioni rigaAgevolazioni = null;
  				if(agevolazioni != null){
	  				%>
	  		 
	  					<tr valign="top">
	  					<td colspan="2">
	  						<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr valign="top">
					  				<td align="center" class="campo2">Agevolazione</td>
					  				<td align="center" class="campo2">Indispensabile</td>	
					  			</tr>  	
						  		 
					  			  
					  		 		
				  				<%
			  					for(int i=0; i < agevolazioni.size(); i++){
			  						rigaAgevolazioni = (Agevolazioni) agevolazioni.get(i);
			  						agevolazione = rigaAgevolazioni.getCodagevolazione();
									if(rigaAgevolazioni.getAgevolazioneindisp()!=null){
										agevolIndisp = rigaAgevolazioni.getAgevolazioneindisp().value();
										if(agevolIndisp.equals("Y"))
										   agevolIndisp = "SI";
										else
										   agevolIndisp = "NO";
									}
				  				%>	
				  				   	
				  				 <tr valign="top">
						  				<td align="center" class="campo2"><af:comboBox classNameBase="input" name="agevolazione" title="agevolazione" selectedValue="<%=agevolazione%>" disabled="true" moduleName="M_GetDatiAgevolazioneAnteprimaVacancy" addBlank="true" /></td>
						  				<td align="center" class="campo2""><af:textBox type="text" name="indispAg" classNameBase="input" value="<%= agevolIndisp %>" readonly="true"/></td>
						  			</tr>
					  		 			
			 					<% 	} %>
 					 </table>
 					 </td>
 					  </tr>
  					<% }  %>
  					</table>
  				<% out.print(htmlStreamBottom); %>
				<p class="titolo">Abilitazioni</p>
				<% out.print(htmlStreamTop); %>
				<table class="main" border="0">
 	 			<tr valign="top"><td colspan="2" width="100%"><div class="sezione3">Iscrizione ad albi e ordini professionali</div></td></tr> 
  				
					 
  				<% 
	  				String descrAlbo = "", alboIndisp="";
	  				Albi rigaAlbi = null;
	  				
	  				if(albi != null){
	  				%>
	  				
	  				<tr valign="top">
	  					<td colspan="2">
	  						<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr valign="top">
					  				<td align="center" class="campo2">Albo</td>
					  				<td align="center" class="campo2">Indispensabile</td>	
					  			</tr>  			
				  				<%
			  					for(int i=0; i < albi.size(); i++){
			  						rigaAlbi = (Albi) albi.get(i);
			  						descrAlbo = rigaAlbi.getCodalbo();
									if(rigaAlbi.getAlboindisp()!=null){
										alboIndisp = rigaAlbi.getAlboindisp().value();
										if(alboIndisp.equals("Y"))
										   alboIndisp = "SI";
										else
										   alboIndisp = "NO";
									}
										
				  				%>	
					  			<tr valign="top">
					  				<td align="center" class="campo2""><af:comboBox classNameBase="input" name="descrAlbo" title="descrAlbo" selectedValue="<%=descrAlbo%>" disabled="true" moduleName="M_GetDatiAlboAnteprimaVacancy" addBlank="true" /></td>
									<td align="center" class="campo2"><af:textBox type="text" name="alboindisp" classNameBase="input" value="<%= alboIndisp %>" readonly="true"/></td>
					  			</tr>	  				
			 					<% 	} %>
 							</table>
	  					</td>
	  				</tr>
  					<% }  %>
					<tr valign="top"><td colspan="2">&nbsp;</td></tr>
					<tr valign="top"><td colspan="2" width="100%"><div class="sezione3">Patenti</div></td></tr> 

  				<% 
	  				String descrPatente = "",patenteIndisp="";
	  				Patenti rigaPat = null;
	  				
	  				if(patenti != null){
	  				%>
	  				
	  				<tr valign="top">
	  					<td colspan="2">
	  						<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr valign="top">
					  				<td align="center" class="campo2">Patente</td>
					  				<td align="center" class="campo2">Indispensabile</td>	
					  			</tr>  					
				  				<%
			  					for(int i=0; i < patenti.size(); i++){
			  						rigaPat = (Patenti) patenti.get(i);
			  						descrPatente = rigaPat.getCodpatenteguida();
									if(rigaPat.getPatguidaindisp()!=null){
										patenteIndisp = rigaPat.getPatguidaindisp().value();
										if(patenteIndisp.equals("Y"))
										   patenteIndisp = "SI";
										else
										   patenteIndisp = "NO";
									}
										
				  				%>	
					  			<tr valign="top">
					  				<td align="center" class="campo2"><af:comboBox classNameBase="input" name="descrPatente" title="descrPatente" selectedValue="<%=descrPatente%>" disabled="true" moduleName="M_GetDatiPatentiAnteprimaVacancy" addBlank="true" /></td>
									<td align="center" class="campo2"><af:textBox type="text" name="patenteindisp" classNameBase="input" value="<%= patenteIndisp %>" readonly="true"/></td>
					  			</tr>	  				
			 					<% 	} %>
 							</table>
	  					</td>
	  				</tr>
  					<% }  %>
	  				<tr valign="top"><td colspan="2">&nbsp;</td></tr>
					<tr valign="top"><td colspan="2" width="100%"><div class="sezione3">Patentini</div></td></tr> 

  				<% 
	  				String descrPatentino = "",patentinoIndisp="";
	  				Patentini rigaPatentini = null;
	  				
	  				if(patentini != null){
	  				%>
	  				
	  				<tr valign="top">
	  					<td colspan="2">
	  						<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr valign="top">
					  				<td align="center" class="campo2">Patentino</td>
					  				<td align="center" class="campo2">Indispensabile</td>	
					  			</tr>  				
				  				<%
			  					for(int i=0; i < patentini.size(); i++){
			  						rigaPatentini = (Patentini) patentini.get(i);
			  						descrPatentino = rigaPatentini.getCodpatentino();
									if(rigaPatentini.getPatentinoindisp()!=null){
										patentinoIndisp = rigaPatentini.getPatentinoindisp().value();
										if(patentinoIndisp.equals("Y"))
										   patentinoIndisp = "SI";
										else
										   patentinoIndisp = "NO";
									}
										
				  				%>	
					  			<tr valign="top">
					  				<td align="center" class="campo2"><af:comboBox classNameBase="input" name="descrPatentino" title="descrPatentino" selectedValue="<%=descrPatentino%>" disabled="true" moduleName="M_GetDatiPatentiniAnteprimaVacancy" addBlank="true" /></td>
									<td align="center" class="campo2"><af:textBox type="text" name="patentinoindisp" classNameBase="input" value="<%= patentinoIndisp %>" readonly="true"/></td>
					  			</tr>	  				
			 					<% 	} %>
 							</table>
	  					</td>
	  				</tr>
  					<% }  %>
					</table>
					
		<% out.print(htmlStreamBottom); %>
		<p class="titolo">Pubblicazione</p>
		<% out.print(htmlStreamTop); %>
		<table class="main" border="0">
		
			<tr valign="top">
				<td width="100%"><hr></td>
			</tr>
			<tr valign="top">
				<td>
					<table width="100%">
						<tr valign="top">
							<td class="etichetta2">Dati azienda</td>
							<td class="campo2" valign="top" ><af:textArea
									classNameBase="textarea" name="strDatiAziendaPubb" cols="50"
									value="<%=Utils.notNull(datiAziendaPub) %>" maxlength="2000"
									readonly="<%= String.valueOf(!canModify)%>" /></td>
						</tr>
						<tr valign="top">
							<td class="etichetta2">Mansione</td>
							<td class="campo2" valign="top" ><af:textArea
									classNameBase="textarea" name="strMansionePubb" cols="50"
									value="<%=mansionePub%>" maxlength="2000"
									readonly="<%= String.valueOf(!canModify)%>" /></td>
						</tr>
		
						<tr valign="top">
							<td class="etichetta2">Contenuto e contesto del lavoro</td>
							<td class="campo2" valign="top" ><af:textArea
									classNameBase="textarea" name="txtFiguraProfessionale" rows="6" cols="50"
									value="<%=Utils.notNull(contenutoPub) %>" maxlength="4000"
									readonly="<%= String.valueOf(!canModify)%>" /></td>
						</tr>
						<tr valign="top">
							<td class="etichetta2">Luogo di lavoro</td>
							<td class="campo2" valign="top" ><af:textArea
									classNameBase="textarea" name="strLuogoLavoro" cols="50"
									value="<%=Utils.notNull(luogoPub) %>" maxlength="2000"
									readonly="<%= String.valueOf(!canModify)%>" /></td>
						</tr>
						<tr valign="top">
							<td class="etichetta2">Formazione</td>
							<td class="campo2" valign="top" ><af:textArea
									classNameBase="textarea" name="strFormazionePubb" cols="50"
									value="<%=Utils.notNull(formazionePub) %>" maxlength="2000"
									readonly="<%= String.valueOf(!canModify)%>" /></td>
						</tr>
						<tr valign="top">
							<td class="etichetta2">Contratto</td>
							<td class="campo2" valign="top" ><af:textArea
									classNameBase="textarea" name="txtCondContrattuale" cols="50"
									value="<%=Utils.notNull(contrattoPub) %>" maxlength="2000"
									readonly="<%=String.valueOf(!canModify)%>" /></td>
						</tr>
						<tr valign="top">
							<td class="etichetta2">Conoscenze</td>
							<td class="campo2" valign="top" ><af:textArea
									classNameBase="textarea" name="strConoscenzePubb" cols="50"
									value="<%=Utils.notNull(conoscenzePub) %>" maxlength="2000"
									readonly="<%=String.valueOf(!canModify)%>" /></td>
						</tr>
						<tr valign="top">
							<td class="etichetta2">Caratteristiche del candidato</td>
							<td class="campo2" valign="top" ><af:textArea
									classNameBase="textarea" name="txtCaratteristFigProf" cols="50"
									value="<%=Utils.notNull(caratteristichePub)%>" maxlength="2000"
									readonly="<%=String.valueOf(!canModify)%>" /></td>
						</tr>
						<tr valign="top">
							<td class="etichetta2">Orario</td>
							<td class="campo2" valign="top" ><af:textArea
									classNameBase="textarea" name="strNoteOrarioPubb" cols="50"
									value="<%=Utils.notNull(orarioPub) %>" maxlength="2000"
									readonly="<%=String.valueOf(!canModify)%>" /></td>
						</tr>
						<tr valign="top">
							<td class="etichetta2">Per candidarsi</td>
							<td class="campo2" valign="top" ><af:textArea
									classNameBase="textarea" name="strRifCandidaturaPubb" cols="50"
									value="<%=Utils.notNull(candidaturaPub)%>" maxlength="2000"
									readonly="<%=String.valueOf(!canModify)%>" /></td>
						</tr>
					</table>
					</td>
				</tr>
				</table>
  				<% out.print(htmlStreamBottom); %>
  				<center>
					<input class="pulsante" type="button" name="btnchiudi" value="Chiudi" onclick="window.close()" />
			    </center>	
	
	</body>
	</html>