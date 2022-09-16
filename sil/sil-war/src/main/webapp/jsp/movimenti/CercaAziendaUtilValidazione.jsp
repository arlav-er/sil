<!-- @author: Paolo Roccetti - Luglio 2004 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.module.movimenti.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  //Definizione variabili
  String prgAziendaUtil = "";
  String prgUnitaUtil = "";
  String strCodiceFiscale = "";
  String strRagioneSociale = "";
  String numContratto = "";
  String dataInizio = "";
  String dataFine = "";
  String legaleRapp = "";
  String numSoggetti = "";
  String classeDip = "";
  String funzione = "";
  String luogoDiLavoro = "";

  //Oggetti per l'applicazione dello stile grafico
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);	
  
  //Recupero dati dalla request
  String prgMovimentoApp = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGMOVIMENTOAPP");
  String funz_agg = StringUtils.getAttributeStrNotNull(serviceRequest, "FUNZ_AGGIORNAMENTO");  
  String _funzione = StringUtils.getAttributeStrNotNull(serviceRequest, "CDNFUNZIONE");
  String action = StringUtils.getAttributeStrNotNull(serviceRequest, "ACTION");
  boolean isRefreshing = "refresh".equalsIgnoreCase(action);
  boolean isSaving = "salva".equalsIgnoreCase(action);
  String flagAziEstera = StringUtils.getAttributeStrNotNull(serviceRequest, "FLGDISTAZESTERA");
  
  String codTipoTrasf = StringUtils.getAttributeStrNotNull(serviceRequest, "CODTIPOTRASF");

  //Recupero oggetto movimento in sessione
  NavigationCache mov = (NavigationCache) sessionContainer.getAttribute("MOVIMENTOCORRENTE");
  
  //Se l'utente ha premuto il pulsante di salva lo aggiorno con i dati provenienti dalla request
  //(per salvare i dati nella cache non c'è bisogno di abilitarla)
  boolean flgDatiSalvati = false;
  if (isSaving) {
  	mov.setFieldsFromSourceBean(serviceRequest);
  	mov.setField("FlgDatiAzIntValidi", "S");
  	flgDatiSalvati = true;
  }
  
  //Guardo se l'oggetto in sessione era abilitato
  boolean wasMovEnabled = mov.isEnabled();
  mov.enable();
  
  //Guardo se l'oggetto in sessione contiene dati validi
  boolean datiInSessioneValidi = "S".equalsIgnoreCase((String) mov.getField("FlgDatiAzIntValidi"));
  
  //Setto l'origine dei dati generali da recuperare
  SourceBean dataOrigin = (SourceBean) serviceResponse.getAttribute("M_MovGetDettMovApp.ROWS.ROW"); 

  //Dati azienda da confrontare (sempre dal movimento sul DB, per capire quali pulsanti devo mostrare)
  String strCodiceFiscaleAzUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "strCodiceFiscaleAzUtil");  
  String strPartitaIvaAzUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "strPartitaIvaAzUtil");
  String strRagioneSocialeAzUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "strRagioneSocialeAzUtil");
  String strIndirizzoUAzUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "strIndirizzoUAzUtil");   
  String strCapUAzUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "STRUAINTCAP");   
  String codAtecoUAzUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "CODAZINTATECO");
  String descrComUAzUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "strComuneUAzUtil");

  //Per gli altri campi, se i dati in sessione sono validi, li prelevo da lì, altrimenti sempre dal DB
  if (datiInSessioneValidi) {dataOrigin = mov.getFieldsAsSourceBean();}
  
  //Se invece sto facendo un refresh li prendo dalla serviceRequest
  if (isRefreshing) {dataOrigin = serviceRequest;}
  
  //Mi dice se l'az Utilizzatrice l'ha scelta l'utente a mano o era presente nel movimento
  boolean isSceltaUtente = false;
  
  //Recupero dati
  if (dataOrigin != null) {
	  prgAziendaUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGAZIENDAUTILIZ");
	  prgUnitaUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "PRGUNITAUTILIZ");
	  if (strCodiceFiscaleAzUtil.equals("")) {
	  	//Se il movimento non ha l'azienda utilizzatrice guardo se l'ho in sessione per scelte precedenti
	  	//dell'utente
	  	strRagioneSocialeAzUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "strRagioneSocialeAzUtil");

        strIndirizzoUAzUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "strIndirizzoUAzUtil");   
        descrComUAzUtil = StringUtils.getAttributeStrNotNull(dataOrigin, "strComuneUAzUtil");

	  	isSceltaUtente = true;
	  }  		
	  numContratto = StringUtils.getAttributeStrNotNull(dataOrigin, "STRAZINTNUMCONTRATTO");
	  dataInizio = StringUtils.getAttributeStrNotNull(dataOrigin, "DATAZINTINIZIOCONTRATTO");
	  dataFine = StringUtils.getAttributeStrNotNull(dataOrigin, "DATAZINTFINECONTRATTO");
	  legaleRapp = StringUtils.getAttributeStrNotNull(dataOrigin, "STRAZINTRAP");
	  numSoggetti = StringUtils.getAttributeStrNotNull(dataOrigin, "NUMAZINTSOGGETTI");
	  classeDip = StringUtils.getAttributeStrNotNull(dataOrigin, "NUMAZINTDIPENDENTI");
	  luogoDiLavoro = StringUtils.getAttributeStrNotNull(dataOrigin, "STRLUOGODILAVORO");	  
  }
  
  //Mi dice se i dati dell'azienda sul DB sono aggiornati o da aggiornare rispetto a quelli del movimento
  boolean datiAziendaAggiornati = true;
  //Mi dice quante unita ho trovato nel comune indicato
  int numUnitaConIndirizzoDiverso = 0;
  //Mi dice quante unita ho trovato con lo stesso indirizzo nel comune indicato
  int numUnitaConIndirizzoUguale = 0;
  
  //Guardo se ho identificato l'azienda e/o l'unita aziendale, in questo caso mostro i 
  //dati del DB e li confronto per sapere se sono aggiornati
  boolean unitaDaSessione = false;
  SourceBean rigaUAzValMov = null;
    
  Vector vectRicercaAz = serviceResponse.getAttributeAsVector("M_MovRicercaAziendaCF.ROWS.ROW");
  if (vectRicercaAz.size() == 1) {
	//Azienda trovata, recupero i dati dell'azienda contenuti nel DB
	SourceBean rigaAzValMov = (SourceBean) vectRicercaAz.elementAt(0);
	prgAziendaUtil = rigaAzValMov.getAttribute("PRGAZIENDA").toString();
	String ragSocDB = StringUtils.getAttributeStrNotNull(rigaAzValMov, "strragionesociale");
	String pIvaDB = StringUtils.getAttributeStrNotNull(rigaAzValMov, "strpartitaiva");
	    
	//Confronto i dati per vedere se ci sono differenze
	if ((!strPartitaIvaAzUtil.equals("") && !strPartitaIvaAzUtil.equals(pIvaDB)) ||   
     (!strRagioneSocialeAzUtil.equals("") && !strRagioneSocialeAzUtil.equalsIgnoreCase(ragSocDB))) {
      datiAziendaAggiornati = false;
	} 
		    
	//Visualizzo i dati dell'azienda contenuti sul DB invece di quelli del movimento
	strRagioneSocialeAzUtil = ragSocDB;	    	    
		      
	Vector vectRicercaComuneUnitaAz = serviceResponse.getAttributeAsVector("M_MovRicercaUnitaAziendaCF.ROWS.ROW");
		    
	//Estraggo dalla ricerca le unita che hanno lo stesso indirizzo riportato nel movimento
	Vector vectorRicercaIndirizzoUnitaAz = new Vector();
	for (int i = 0; i < vectRicercaComuneUnitaAz.size(); i++) {
		SourceBean unita = (SourceBean) vectRicercaComuneUnitaAz.get(i);
		String indUnita = StringUtils.getAttributeStrNotNull(unita, "strindirizzo");
		if (indUnita.equalsIgnoreCase(strIndirizzoUAzUtil)) {
			vectorRicercaIndirizzoUnitaAz.add(unita);
		}
	}
		    
	numUnitaConIndirizzoDiverso = vectRicercaComuneUnitaAz.size();
	numUnitaConIndirizzoUguale = vectorRicercaIndirizzoUnitaAz.size();

	if (numUnitaConIndirizzoUguale == 1) {
		//Unita trovata sul DB	      
		rigaUAzValMov = (SourceBean) vectorRicercaIndirizzoUnitaAz.elementAt(0);
	} else {
		//Guardo se in sessione ho una scelta dell'utente in merito all'unita aziendale
		NavigationCache sceltaUnitaAzienda = (NavigationCache) sessionContainer.getAttribute("SCELTAUNITAAZIENDA");
		if (sceltaUnitaAzienda != null) {
			//Controllo se si riferisce al movimento corrente o se è precedente
			if (prgMovimentoApp.equals(sceltaUnitaAzienda.getField("PRGMOVIMENTOAPP").toString())) {
				prgUnitaUtil = (String) sceltaUnitaAzienda.getField("PRGUNITAUTIL");
				if (prgUnitaUtil != null) {
					//Cerco nel vettore delle unita quella con progressivo unita coincidente
					for (int j = 0; j < vectRicercaComuneUnitaAz.size(); j++) {
						SourceBean unitaCorrente = (SourceBean) vectRicercaComuneUnitaAz.get(j);
						String prgUnitaCorrente = unitaCorrente.getAttribute("PRGUNITA").toString();
						if (prgUnitaCorrente.equals(prgUnitaUtil)) {
							rigaUAzValMov = unitaCorrente;
							unitaDaSessione = true;
						}
					}
				} else prgUnitaUtil = "";
			} else {
				//Se il prgMovimentoApp non coincide cancello l'oggetto perchè è vecchio
				sessionContainer.delAttribute("SCELTAUNITAAZIENDA");
			}
		}
	}
		    
	//Unita trovata (dal DB o dalla sessione), recupero i dati dell'azienda contenuti nel DB
	if (rigaUAzValMov != null) {
		prgUnitaUtil = rigaUAzValMov.getAttribute("PRGUNITA").toString();
		String codAtecoDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "codateco");
		String strIndirizzoDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "strindirizzo");
		String capDB = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "strcap");
	      
		//Confronto i dati per vedere se ci sono differenze
		if ((!strIndirizzoUAzUtil.equals("") && !strIndirizzoUAzUtil.equalsIgnoreCase(strIndirizzoDB)) ||     
		   (!codAtecoUAzUtil.equals("") && !codAtecoUAzUtil.equals(codAtecoDB)) ||
		   (!strCapUAzUtil.equals("") && !strCapUAzUtil.equals(capDB))) {
		   datiAziendaAggiornati = false;
		}
		
		//Visualizzo i dati dell'unita contenuti sul DB invece di quelli del movimento
		strIndirizzoUAzUtil = strIndirizzoDB;	  		  	      
		descrComUAzUtil = StringUtils.getAttributeStrNotNull(rigaUAzValMov, "strDescrComune");
	} 
  }
  
  //Creo la stringa del luogo di lavoro se è la prima volta che apro la pop-up e ho un CF 
  //dell'azienda utilizzatrice nel movimento sulla tabella di appoggio
  //oppure dopo aver aggiornato i dati dell'azienda sul DB
  if ((!isRefreshing && !isSaving && !datiInSessioneValidi && !strCodiceFiscaleAzUtil.equals("")) ||
  	  (isRefreshing && (rigaUAzValMov != null))) {
  	luogoDiLavoro = strRagioneSocialeAzUtil + " - " + strIndirizzoUAzUtil + " (" + descrComUAzUtil + ")";
  }

  String currentcontext = "valida";
  String context = "";
  
  if (serviceRequest.containsAttribute("CONTEXT")) {
  	context = StringUtils.getAttributeStrNotNull(serviceRequest,"CONTEXT");
  }
  if (serviceRequest.containsAttribute("CURRENTCONTEXT")) {
  	context = StringUtils.getAttributeStrNotNull(serviceRequest,"CURRENTCONTEXT");
  }  
  if (context.equals("validaArchivio")) {
  	currentcontext = context;
  }
      
  //Guardo se devo eseguire il refresh dei dati sottostanti prima di chiudere la pagina (se ho appena salvato)
  String funz_chiusura = (!funz_agg.equals("") ? "window.opener." + funz_agg + "(document.Frm1.STRLUOGODILAVORO.value,document.Frm1.PRGAZIENDAUTILIZ.value,document.Frm1.PRGUNITAUTILIZ.value);" : "") + 
  	"window.close();";

  //Ripristino l'abilitazione dell'oggetto in sessione
  if (!wasMovEnabled) {mov.disable();}
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Cerca azienda Utilizzatrice</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css"/>
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>
<%@ include file="../global/confrontaData.inc"%>
	
	<script language="Javascript">
	
	var flagRicercaPage = "S";
	
	var _funzione = '<%=_funzione%>';
	
	//Aggiorna la pagina salvando i dati contenuti
	function aggiornaCercaAzUtil() {
		var datiOk = controllaFunzTL();
		if (datiOk) { doFormSubmit(document.Frm1); }
	}
	
	//Funzione per l'inserimento di una nuova testata aziendale dell'azienda utilizzatrice
	function apriInserisciAziendaUtil() {
		var f = "AdapterHTTP?PAGE=MovimentiTestataAziendaPage&CONTESTO=AZUTIL" + 
			"&PRGMOVIMENTOAPP=" + document.Frm1.PRGMOVIMENTOAPP.value + "&FUNZ_AGGIORNAMENTO=aggiornaCercaAzUtil";

	  	if (document.Frm1.CURRENTCONTEXT != null) {
  			f = f + "&context=" +  document.Frm1.CURRENTCONTEXT.value;
  	  	}

		var t = "_blank";
		var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=600,top=0,left=100";
		window.open(f, t, feat);	
	}
	
	//Funzione per l'inserimento di una nuova unita aziendale dell'azienda utilizzatrice
	function apriInserisciUnitaAziendaUtil(){
		var f = "AdapterHTTP?PAGE=MovimentiUnitaAziendaPage&CONTESTO=AZUTIL" + 
			"&PRGMOVIMENTOAPP=" + document.Frm1.PRGMOVIMENTOAPP.value + "&PRGAZIENDA=" + 
			document.Frm1.PRGAZIENDAUTILIZ.value + "&FUNZ_AGGIORNAMENTO=aggiornaCercaAzUtil";

	  	if (document.Frm1.CURRENTCONTEXT != null) {
  			f = f + "&context=" +  document.Frm1.CURRENTCONTEXT.value;
  	  	}

		var t = "_blank";	
		var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=600,top=0,left=100";
		window.open(f, t, feat);	
	}
	
	//apre la pop-up di scelta dell'unita aziendale tra quelle trovate
	function apriScegliUnitaAziendaUtil(prgAzienda, cdnFunzione, prgMovimentoApp) {
	  var f = "AdapterHTTP?PAGE=ListaUnitaAziendaPage&CONTESTO=AZUTIL&PRGAZ=" + prgAzienda + 
	  "&CDNFUNZIONE=" + cdnFunzione + "&PRGMOVIMENTOAPP=" + prgMovimentoApp + "&FUNZ_AGGIORNAMENTO=aggiornaCercaAzUtil" + 
	  "&CODTIPOTRASF=" + document.Frm1.CODTIPOTRASF.value;

	  if (document.Frm1.CURRENTCONTEXT != null) {
  		f = f + "&context=" +  document.Frm1.CURRENTCONTEXT.value;
  	  }

	  var t = "_blank";
	  var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=600,top=0,left=100";
	  window.open(f, t, feat);	
	}
	
	//apre la pop-up per l'aggiornamento dei dati dell'azienda
	function apriAggiornaAzienda(prgAzienda, prgUnita, cdnFunzione, prgMovimentoApp) {	  	 	  
	  var f = "AdapterHTTP?PAGE=AggiornaDatiAziendaPage&PRGAZIENDA=" + prgAzienda + "&PRGUNITA=" + prgUnita + 
	  	"&CDNFUNZIONE=" + cdnFunzione + "&PRGMOVIMENTOAPP=" + prgMovimentoApp + 
	  	"&CONTESTO=AZUTIL&FUNZ_AGGIORNAMENTO=aggiornaCercaAzUtil";    
	  
	  if (document.Frm1.CURRENTCONTEXT != null) {
  		f = f + "&context=" +  document.Frm1.CURRENTCONTEXT.value;
  	  }
	  
	  var t = "_blank";
	  var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=600,top=0,left=100";
	  window.open(f, t, feat);
	}
	
	
	//Controlla che la data di inizio contratto sia precedente a quella di fine se entrambe sono specificate
	function controlDate() {
		var datainizio = new String(document.Frm1.DATAZINTINIZIOCONTRATTO.value);
		var datafine = new String(document.Frm1.DATAZINTFINECONTRATTO.value);
		var checkData = 0;
		if (datainizio != null && datainizio != '' && datafine != null && datafine != '') {
			checkData = compareDate(datainizio, datafine);	
		}
		if (checkData > 0) {
			alert("Data di inizio contratto successiva alla data di fine contratto");
			return false;
		} else return true;
	}
	
	//Funzione che esegue il salvataggio dei dati nell'oggetto in sessione
	function salvaInfoAzUtil() {
		document.Frm1.ACTION.value = "salva";
		aggiornaCercaAzUtil();
	}
	
	//Funzione di aggiornamento dell'azienda quando scelta direttamente dal DB (nel movimento non era presente)
	function aggiornaAzienda() {
  		document.Frm1.PRGAZIENDAUTILIZ.value = opened.dati.prgAzienda;
  		document.Frm1.PRGUNITAUTILIZ.value = opened.dati.prgUnita;
  		document.Frm1.strRagioneSocialeAzUtil.value = opened.dati.ragioneSociale;

  		document.Frm1.strIndirizzoUAzUtil.value = opened.dati.strIndirizzoAzienda;
  		document.Frm1.strComuneUAzUtil.value = opened.dati.comuneAzienda;
  		document.Frm1.STRLUOGODILAVORO.value = opened.dati.ragioneSociale + " - " + 
  		opened.dati.strIndirizzoAzienda + " (" + opened.dati.comuneAzienda + ")";
  		opened.close();  				
	}
	
	//Azzera i dati dell'azienda utilizzatrice e salva le modifiche in sessione
	function azzeraAziendaUtil() {
		if (confirm("Si desidera procedere con lo scollegamento dell'azienda utilizzatrice?")) {
	  		document.Frm1.PRGAZIENDAUTILIZ.value = "";
	  		document.Frm1.PRGUNITAUTILIZ.value = "";
	  		document.Frm1.strRagioneSocialeAzUtil.value = "";
	  		document.Frm1.STRAZINTNUMCONTRATTO.value = "";
	  		document.Frm1.DATAZINTINIZIOCONTRATTO.value = "";
	  		document.Frm1.DATAZINTFINECONTRATTO.value = "";
	  		document.Frm1.STRAZINTRAP.value = "";
	  		document.Frm1.NUMAZINTSOGGETTI.value = "";
	  		document.Frm1.NUMAZINTDIPENDENTI.value = "";
	  		document.Frm1.STRLUOGODILAVORO.value = "";

	  		document.Frm1.strIndirizzoUAzUtil.value = "";
	  		document.Frm1.strComuneUAzUtil.value = "";

	  		salvaInfoAzUtil();	
	  	}	
	}
	
	</script>
	<script type="text/javascript" src="../../js/movimenti/common/MovimentiRicercaSoggetto.js" language="JavaScript"></script>
</head>

<body class="gestione" onload="rinfresca();<%=(flgDatiSalvati ? funz_chiusura : "")%>">
<% if (codTipoTrasf.equalsIgnoreCase("DL") && !flagAziEstera.equals("S")) {%>
	<p class="titolo">Ricerca Azienda Distaccataria</p>
<%} else {%>
	<p class="titolo">Ricerca Azienda Utilizzatrice</p>
<%}%>	
<af:form name="Frm1" method="POST" action="AdapterHTTP" >
<%out.print(htmlStreamTop);%>
	<table>  
	<tr>
	  <td class="etichetta">Ragione Sociale</td>
	  <td class="campo">                         
	    <af:textBox classNameBase="input" name="strRagioneSocialeAzUtil" title="Ragione Sociale" size="50" value="<%=strRagioneSocialeAzUtil%>" readonly="true"/>
	    <%if (isSceltaUtente) { //Devo scegliere l'azienda perchè nel movimento non c'è o l'utente l'ha cancellata%>
          <a id='ricercaAzienda' href="#" onClick="javascript:apriSelezionaSoggetto('Aziende', 'aggiornaAzienda', '', '', '');"><img src="../../img/binocolo.gif" alt="Cerca azienda nel DB"></a>
	 	  <a href="#" title="cancella dati" onClick="javascript:azzeraAziendaUtil();"><img src="../../img/del.gif" alt="Scollega azienda utilizzatrice"></a>            	    
	    <%} else if (prgAziendaUtil.equals("")) { //Devo inserire l'azienda%>
	      <a href="#" onClick="javascript:apriInserisciAziendaUtil();"><img src="../../img/add2.gif" alt="nuova azienda"></a>
	    <%} else if (prgUnitaUtil.equals("") && numUnitaConIndirizzoDiverso == 0) { //Devo inserire l'unita%>
	      <a href="#" onClick="javascript:apriInserisciUnitaAziendaUtil();"><img src="../../img/add2.gif" alt="nuova unità"></a>  
	    <%} else if ((prgUnitaUtil.equals("") && numUnitaConIndirizzoDiverso > 0)) { //Devo scegliere l'unita%>
	      <a href="#" title="Scegli unita aziendale" onClick="javascript:apriScegliUnitaAziendaUtil(document.Frm1.PRGAZIENDAUTILIZ.value, <%=_funzione%>, document.Frm1.PRGMOVIMENTOAPP.value);"><img src="../../img/binocolo.gif" alt="scegli unità tra quelle trovate"></a>      	
	    <%} else if (!prgAziendaUtil.equals("") && !prgUnitaUtil.equals("")) { //Ho trovato anche l'unita%>
	      <a href="#" title="Dettaglio azienda utilizzatrice" onClick="javascript:apriUnitaAziendale(document.Frm1.PRGAZIENDAUTILIZ.value, document.Frm1.PRGUNITAUTILIZ.value, <%=_funzione%>, '0');"><img src="../../img/detail.gif" alt="Dettaglio azienda"></a>
		  <% if (unitaDaSessione) {%>&nbsp;     
			<a href="#" title="Scegli unita aziendale" onClick="javascript:apriScegliUnitaAziendaUtil(document.Frm1.PRGAZIENDAUTILIZ.value, <%=_funzione%>, document.Frm1.PRGMOVIMENTOAPP.value);"><img src="../../img/binocolo.gif" alt="scegli unità tra quelle trovate"></a>      
	   	  <%}%>
	 	  <% if (!datiAziendaAggiornati) {%>&nbsp;
			<a href="#" title="Aggiorna dati azienda utilizzatrice" onClick="javascript:apriAggiornaAzienda(document.Frm1.PRGAZIENDAUTILIZ.value, document.Frm1.PRGUNITAUTILIZ.value,<%=_funzione%>,document.Frm1.PRGMOVIMENTOAPP.value);"><img src="../../img/DB_img.gif" title="Aggiorna dati azienda" alt="Aggiorna dati azienda"></a>  
	      <%}%>
	 	<%}%>
	  </td>
	</tr>
	<tr>
	  <td class="etichetta">Indirizzo</td>
	  <td class="campo">
	    <af:textBox classNameBase="input" name="strIndirizzoUAzUtil" title="Indirizzo" size="60" value="<%=strIndirizzoUAzUtil%>" readonly="true"/>
	  </td>
	</tr>
	<tr>
	  <td class="etichetta">Comune</td>
	  <td class="campo">
	    <af:textBox classNameBase="input" name="strComuneUAzUtil" title="Comune" size="60" value="<%=descrComUAzUtil%>" readonly="true"/>
	  </td>
	</tr>
	</table>

<p align="center">
<% if (codTipoTrasf.equalsIgnoreCase("DL")) {%>
    <input type="hidden" name="STRAZINTNUMCONTRATTO" value="">
	<input type="hidden" name="DATAZINTINIZIOCONTRATTO" value="">
	<input type="hidden" name="DATAZINTFINECONTRATTO" value="">
<%} else {%>
<table>
<tr><td>

  <table>
    <tr>
    <td class="etichetta" nowrap="nowrap">Num. Convenzione/Contratto</td>
    <td class="campo" nowrap="nowrap">
      <af:textBox classNameBase="input" name="STRAZINTNUMCONTRATTO" title="Numero Contratto" size="12" value="<%=numContratto%>" maxlength="11"/>
    </td>
    <td class="etichetta" nowrap="nowrap">&nbsp;Data Convenzione/Inizio Contratto</td>
    <td class="campo" nowrap="nowrap">
      <af:textBox classNameBase="input" name="DATAZINTINIZIOCONTRATTO" type="date" validateOnPost="true" title="Data Inizio Contratto" size="12" value="<%=dataInizio%>" maxlength="10"/>
    </td>
    <td class="etichetta" nowrap="nowrap">&nbsp;Data Fine</td>
    <td class="campo" nowrap="nowrap">
      <af:textBox classNameBase="input" name="DATAZINTFINECONTRATTO" type="date" validateOnPost="true" title="Data Fine Contratto" size="12" value="<%=dataFine%>" validateWithFunction="controlDate" maxlength="10"/>
    </td>
    </tr>
  </table>

</td></tr>
</table>
<%}%>
</p>
<br>
<center>
<table>
	<tr>
	  	<td align="center">
	  	<input type="button" class="pulsante" name="confermaSelAzUtil" value="Salva" onclick="javascript:salvaInfoAzUtil();">
	  	</td>
		<td align="center">
		<input type="button" class="pulsante" name="chiudiSelAzUtil" value="Chiudi" onclick="javascript:window.close();">
		</td>
	</tr>
</table>
<input type="hidden" name="PAGE" value="MovCercaAziendaUtilValidazionePage">
<input type="hidden" name="PRGAZIENDAUTILIZ" value="<%=prgAziendaUtil%>">
<input type="hidden" name="PRGUNITAUTILIZ" value="<%=prgUnitaUtil%>">
<input type="hidden" name="STRLUOGODILAVORO" value="<%=luogoDiLavoro%>">
<input type="hidden" name="PRGMOVIMENTOAPP" value="<%=prgMovimentoApp%>">
<input type="hidden" name="ACTION" value="refresh">
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
<input type="hidden" name="FUNZ_AGGIORNAMENTO" value="<%=funz_agg%>">
<input type="hidden" name="CURRENTCONTEXT" value="<%=currentcontext%>">
<input type="hidden" name="STRAZINTRAP" value="<%=legaleRapp%>">
<input type="hidden" name="NUMAZINTSOGGETTI" value="<%=numSoggetti%>">
<input type="hidden" name="NUMAZINTDIPENDENTI" value="<%=classeDip%>">
<input type="hidden" name="CODTIPOTRASF" value="<%=codTipoTrasf%>">
<input type="hidden" name="FLGDISTAZESTERA" value="<%=flagAziEstera%>">
</center>
</af:form>
<%out.print(htmlStreamBottom);%>
</body>
</html>

