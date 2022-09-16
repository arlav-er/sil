<!-- @author: Paolo Roccetti - Gennaio 2004 -->
<!-- mod. 28/10/2004 (Andrea) 
	Modificato valore del campo PROVENIENZA:
		Se la ricerca avviene per lavoratore e/azienda questo campo prende il valore dell'ultima ricerca
		(quindi se si seleziona prima il lavoratore e poi l'azienda il valore del campo sara' "azienda")
	Aggiunto campo RICERCA_DA: 
		indica la pagina specifica di ricerca da cui si proviene, davo che il campo
		PROVENIENZA  non individua piu' con esattezza da quale pagine si proviene. Questo campo e' utilizzato nella
		pagina di lista.
	Modificate le funzioni JS di impostazione dei dati selezionati: 
		modificano i campi PROVENIENZA delle due form (di ricerca e di inserimento movimento)
-->
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
  // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, "MovimentiRicercaPage");
  boolean canInsert =  attributi.containsButton("INSERISCI");
  boolean canImport = attributi.containsButton("IMPORTA");
  boolean canTrasf = attributi.containsButton("TRASFERISCI_RAMO");  
  // variabili richieste dalla sezione del lavoratore  (vedi commento su SezioneLavoratore.inc)
  String fSelLav = "";
  // quando si cancella il lavoratore selezionato bisogna resettare il campo PROVENIENZA
  String fDelLav = "setProvenienza('ListaMov')";
  //
  //Stabilisce da quale menu contestuale si proviene
  String provenienza = "";
  
  String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
  provenienza = StringUtils.getAttributeStrNotNull(serviceRequest,"PROVENIENZA");
  
  //Oggetti per l'applicazione dello stile grafico
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);

  //Genero informazioni sul lavoratore se sono in un menu contestuale
  String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "CDNLAVORATORE");
  String strCodiceFiscaleLav = "";
  String strNomeLav = "";
  String strCognomeLav = "";

  //Dati per l'azienda
  String prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGAZIENDA");
  String prgUnita = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGUNITA");
  String ragioneSociale = "";
  String pIva = "";
  String codFiscaleAzienda = "";
  String IndirizzoAzienda = "";
  String descrTipoAz = "";
  String codTipoAz = "";
  String codnatGiurAz = "";
  String strFlgCfOk = "";
  String strFlgDatiOk = "";

  //Dati per l'azienda utilizzatrice
  String prgAziendaUt = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGAZIENDAUT");
  String prgUnitaUt = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGUNITAUT");
  String ragioneSocialeUt = "";
  String pIvaUt = "";
  String codFiscaleAziendaUt = "";
  String IndirizzoAziendaUt = "";
  String descrTipoAzUt = "";
  String codTipoAzUt = "";
  String codnatGiurAzUt = "";
  String strFlgCfOkUt = "";
  String strFlgDatiOkUt = "";

  /*if (provenienza.equalsIgnoreCase("generale")){
    provenienza = "generale";
  }*/ 
  provenienza = "ListaMov";
  ////////////////
  // ritornando alla lista dalla ricerca si parte dalla pagina vuota
    prgAzienda = "";
    prgUnita = "";
    cdnLavoratore = "";
    prgAziendaUt = "";
    prgUnitaUt = "";
  ///////////////
  if (!cdnLavoratore.equals("")) {
    //Oggetto per la generazione delle informazioni sul lavoratore
    InfoLavoratore datiLav = new InfoLavoratore(new BigDecimal(cdnLavoratore));
    strCodiceFiscaleLav = datiLav.getCodFisc();
    strNomeLav = datiLav.getNome();
    strCognomeLav = datiLav.getCognome();
    strFlgCfOk = datiLav.getFlgCfOk();
    if (strFlgCfOk!=null){
      if (strFlgCfOk.equalsIgnoreCase("S")){
        strFlgCfOk = "Si";
      }else
          if (strFlgCfOk.equalsIgnoreCase("N")){
            strFlgCfOk = "No";
          }
    }
  }
  if (!prgAzienda.equals("") && !prgUnita.equals("")){
    InfCorrentiAzienda currAz = new InfCorrentiAzienda(prgAzienda,prgUnita);
    ragioneSociale = currAz.getRagioneSociale();
    pIva = currAz.getPIva();
    codFiscaleAzienda = currAz.getCodiceFiscale();
    IndirizzoAzienda = currAz.getIndirizzo();
    descrTipoAz = currAz.getDescrTipoAz();
    codTipoAz = currAz.getTipoAz();
    codnatGiurAz = currAz.getCodNatGiurAz();
    strFlgDatiOk = currAz.getFlgDatiOk();
    if (strFlgDatiOk!=null) {
        if (strFlgDatiOk.equalsIgnoreCase("S")){
          strFlgDatiOk = "Si";
        }else
            if (strFlgDatiOk.equalsIgnoreCase("N")){
              strFlgDatiOk = "No";
            }
    }
  }
%>
<%

%>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/"/>
  
  <title>Ricerca Movimenti</title>
  <%@ include file="../movimenti/DynamicRefreshCombo.inc" %>
   <SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>
  <script language="Javascript">
  	

     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
  </SCRIPT>

  <%@ include file="MovimentiRicercaSoggetto.inc" %>
  <%@ include file="MovimentiSezioniATendina.inc" %>
  <%@ include file="Common_Function_Mov.inc" %>
  <%@ include file="../movimenti/DynamicRefreshCombo.inc" %> 

  <SCRIPT language="JavaScript" src="../../js/Function_CommonRicercaCCNL.js"></SCRIPT>

<script type="text/javascript">
<!--
	//Variabili per la memorizzazione dell'azienda e lavoratore corrente per le GET
	var prgAzienda = '<%=prgAzienda%>';
	var prgUnita = '<%=prgUnita%>';
	var cdnLavoratore = '<%=cdnLavoratore%>';
	var prgAziendaUt = '<%=prgAziendaUt%>';
	var prgUnitaUt = '<%=prgUnitaUt%>';
	var provenienza = '<%=provenienza%>';
	
	var flagRicercaPage = "S";
	
    function aggiornaAzienda(){
        document.Frm1.ragioneSociale.value = opened.dati.ragioneSociale;
        document.Frm1.pIva.value = opened.dati.partitaIva;
        document.Frm1.codFiscaleAzienda.value = opened.dati.codiceFiscaleAzienda;
        document.Frm1.IndirizzoAzienda.value = opened.dati.strIndirizzoAzienda + " (" + opened.dati.comuneAzienda + ")";
        document.Frm1.PRGAZIENDA.value = opened.dati.prgAzienda;
        document.Frm1.PRGUNITA.value = opened.dati.prgUnita;
        document.Frm1.codTipoAz.value = opened.dati.codTipoAz;
        document.Frm1.descrTipoAz.value = opened.dati.descrTipoAz;
        document.Frm1.FLGDATIOK.value = opened.dati.FLGDATIOK;
        prgAzienda = opened.dati.prgAzienda;
        prgUnita = opened.dati.prgUnita;
        if ( document.Frm1.FLGDATIOK.value == "S" ){ 
                    document.Frm1.FLGDATIOK.value = "Si";
        }else 
              if ( document.Frm1.FLGDATIOK.value != "" ){
                document.Frm1.FLGDATIOK.value = "No";
              }
        document.Frm1.CODNATGIURIDICA.value = opened.dati.codNatGiurAz;
        opened.close();
        var imgV = document.getElementById("tendinaAzienda");
        cambiaLavMC("aziendaSez","inline");
        imgV.src=imgAperta;
        aggiornaTipoContratto('', '', '', '');
        provenienza = "azienda";
        document.Frm1.PROVENIENZA.value="azienda";
    }
    
   

    function aggiornaAziendaUt(){
        document.Frm1.ragioneSocialeUt.value = opened.dati.ragioneSociale;
        document.Frm1.pIvaUt.value = opened.dati.partitaIva;
        document.Frm1.codFiscaleAziendaUt.value = opened.dati.codiceFiscaleAzienda;
        document.Frm1.IndirizzoAziendaUt.value = opened.dati.strIndirizzoAzienda + " (" + opened.dati.comuneAzienda + ")";
        document.Frm1.PRGAZIENDAUt.value = opened.dati.prgAzienda;
        document.Frm1.PRGUNITAUt.value = opened.dati.prgUnita;        
        document.Frm1.codTipoAzUt.value = opened.dati.codTipoAz;
        document.Frm1.descrTipoAzUt.value = opened.dati.descrTipoAz;
        document.Frm1.FLGDATIOKUt.value = opened.dati.FLGDATIOK;
        prgAziendaUt = opened.dati.prgAzienda;
        prgUnitaUt = opened.dati.prgUnita;
        if ( document.Frm1.FLGDATIOKUt.value == "S" ){ 
                    document.Frm1.FLGDATIOKUt.value = "Si";
        }else 
              if ( document.Frm1.FLGDATIOKUt.value != "" ){
                document.Frm1.FLGDATIOKUt.value = "No";
              }
        document.Frm1.CODNATGIURIDICAUt.value = opened.dati.codNatGiurAz;
        opened.close();
        var imgV = document.getElementById("tendinaAziendaUt");
        cambiaLavMC("aziendaSezUt","inline");
        imgV.src=imgAperta;
    }

    function aggiornaLavoratore(){
        document.Frm1.codiceFiscaleLavoratore.value = opened.dati.codiceFiscaleLavoratore;
        document.Frm1.cognome.value = opened.dati.cognome;
        document.Frm1.nome.value = opened.dati.nome;
        document.Frm1.CDNLAVORATORE.value = opened.dati.cdnLavoratore;
        document.Frm1.FLGCFOK.value = opened.dati.FLGCFOK;
        if ( document.Frm1.FLGCFOK.value == "S" ){ 
                    document.Frm1.FLGCFOK.value = "Si";
        }else 
              if ( document.Frm1.FLGCFOK.value != "" ){
                document.Frm1.FLGCFOK.value = "No";
              }
        opened.close();
        var imgV = document.getElementById("tendinaLav");
        cambiaLavMC("lavoratoreSez","inline");
        imgV.src=imgAperta;
        cdnLavoratore = opened.dati.cdnLavoratore; 
        provenienza = "lavoratore";
        document.Frm1.PROVENIENZA.value="lavoratore";
    }

    function selectMansione_onClick(codMansione, codMansioneHid, descMansione, strTipoMansione) {	
	    if (codMansione.value==""){
	      descMansione.value="";
          strTipoMansione.value="";
          visualizzaTipoDescrMansione("none");
        }
        else if (codMansione.value!=codMansioneHid.value){
        window.open("AdapterHTTP?PAGE=RicercaMansionePage&codMansione="+codMansione.value, "Mansioni", 'toolbar=0, scrollbars=1');     
        }
      }

    function ricercaAvanzataMansioni() {
      window.open("AdapterHTTP?PAGE=RicercaMansioneAvanzataPage", "Mansioni", 'toolbar=0, scrollbars=1, height=600, width=800');
    }

    function ricercaAvanzataCCNL() {
      window.open("AdapterHTTP?PAGE=RicercaCCNLAvanzataPage", "CCNL", 'toolbar=0, scrollbars=1, height=300, width=550');
    }

    function fieldChanged() {
      //non faccio niente!
    }
    function codCCNLUpperCase(inputName){

      var ctrlObj = eval("document.forms[0]." + inputName);
      eval("document.forms[0]."+inputName+".value=document.forms[0]."+inputName+".value.toUpperCase();");
      return true;
    }

    //Cerca il tipo di assunzione
    function cercaTipoContratto(criterio){
    var descr;
        if (document.Frm1.avvFiltrato.value == "DL"){
          var f = "AdapterHTTP?PAGE=SelezionaContrattiSelettivaPage&CODTIPOAZIENDA=" + document.Frm1.codTipoAz.value + "&codMonoTempo=" + document.Frm1.codMonoTempo.value;
        }else{
            //Se non filtrato include le opzioni scadute
            var f = "AdapterHTTP?PAGE=SelezionaContrattiSelettivaPage&CODTIPOAZIENDA=" + document.Frm1.codTipoAz.value + "&codMonoTempo=&filtrato=DL";
         }            
         f = f + "&CRITERIO=" + criterio;
  		 f = f + "&codTipoAss=" + document.Frm1.codTipoAss.value;
  	     //se la descrizione ha caratteri speciali la ricerca potrebbe non funzionare
  		 descr = document.Frm1.descrTipoAss.value;
  		 descr = descr.replace("%","&amp;");
  		 f = f + "&descrTipoAss=" + descr;
  	 	 f = f + "&updateFunctionName=aggiornaTipoContratto";
  	  	 var t = "_blank";
         var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=500,height=400,top=100,left=100";
         window.open(f, t, feat);
    } 
 	
 	//Aggiorna il tipo di assunzione e le combo collegate
    function aggiornaTipoContratto(codice, descrizione, codMonoTipo, codContratto) {
      document.Frm1.codTipoAss.value = codice;
      document.Frm1.descrTipoAss.value = descrizione;
      //refreshComboCollegate();
    }

    //Aggiorna le combo collegate quando avviene qualche cambiamento nelle combo da cui dipendono
    function refreshComboCollegate() {   
      args = "&CODTIPOAZIENDA=" + document.Frm1.codTipoAz.value;
      args = args + '&CODMONOTEMPO='+ document.Frm1.codMonoTempo.value;
      args = args + '&CODTIPOASS='+ document.Frm1.codTipoAss.value;      
      args = args + '&qFilter=S';
     refreshCombo('ComboAgevolazioneSelettiva', 'Frm1.codAgevolazione', args);      
    }
    
    
    
 	function visualizzaTipoDescrMansione(stato){
      /*
      var dvar = document.getElementById("descrMans");
      dvar.style.display = stato;
      dvar = document.getElementById("tipoMans");
      dvar.style.display = stato;
      */
    }

    /* Funzione per includere in normativa e benefici anche le opzioni scadute 
     */
    function selezioneCombo(combo,valore){
      args = "&CODTIPOAZIENDA=" + document.Frm1.codTipoAz.value + "&CODNATGIURIDICA=" + document.Frm1.CODNATGIURIDICA.value;
      args = args + '&CODMONOTEMPO='+ document.Frm1.codMonoTempo.value;
      args = args + '&CODTIPOASS='+ document.Frm1.codTipoAss.value;
      var flt = '&filtrato=' + document.Frm1.normFiltrato.value;
      var flt2 = '&filtrato=' + document.Frm1.benefFiltrato.value;
      if (valore == "norm"){
        args = args + flt;
        clearCombo('Frm1.normativa');
        refreshCombo('ComboNormativaSelettiva', 'Frm1.normativa', args);
      }else {
        args = args + flt2;
        clearCombo('Frm1.codAgevolazione');
        refreshCombo('ComboAgevolazioneSelettiva', 'Frm1.codAgevolazione', args);
      }
    }
    
    
    
    //MODIFICA DI GIOVANNI D'AURIA 03_02_05 BEGIN
    function cambiaCombo(combo){
    	var item=combo.value;
    	if(item == "CES"){
    		document.Frm1.motivoCessazione.disabled=false;
    	}else{
    		document.Frm1.motivoCessazione.value="";
    		document.Frm1.motivoCessazione.disabled=true;
    	}
    	
    }
    
    //Ricerca della mansione per descrizione
	function selectMansionePerDescrizione(desMansione) {
		window.open("AdapterHTTP?PAGE=RicercaMansionePage&desMansione="+desMansione.value+"&flgFrequente=", "Mansioni", 'toolbar=0, scrollbars=1');          
	}
    //MODIFICA DI GIOVANNI D'AURIA 03_02_05 END
    
    //funzione che inibisce la scelta dello stato atto 'In attesa di essere protocollato' 
    function inibisciScelta(combo, scelta){
		var comboValue= combo[combo.selectedIndex].value;
		if(comboValue == scelta){
			alert("Scelta non valida");
			for(i=0; i< combo.options.length; i++){
				if(combo[i].value == 'PR'){	
					break;				
				}
			}
			combo[i].selected=true;
		}
	}
    
    
    function setProvenienza(newValue) {
    	document.Frm1.PROVENIENZA.value = newValue;
    	provenienza = newValue;
    }
     
	<%if (canInsert) {%>
    //Funzione per inserimento nuovo movimento
    function inserisciNuovo() {
    	if (isInSubmit()) return;
	
		var get = "AdapterHTTP?PAGE=MovDettaglioGeneraleInserisciPage&cdnFunzione=<%=_funzione%>"
		 + "&CDNLAVORATORE=" + cdnLavoratore + "&PRGAZIENDA=" + prgAzienda + "&PRGUNITA=" + prgUnita
		 + "&PRGAZIENDAUt=" + prgAziendaUt + "&PRGUNITAUt=" + prgUnitaUt + "&PROVENIENZA=" + provenienza
		 + "&CURRENTCONTEXT=inserisci&collegato=nessuno";
		setWindowLocation(get);    	
    }
	<%} if (canImport) {%>    
    //Funzione per importazione movimenti
    function importaMovimenti() {
		if (isInSubmit()) return;
	
		var get = "AdapterHTTP?PAGE=MovimentiImportaPage&cdnFunzione=<%=_funzione%>";
		setWindowLocation(get);    	
    }
	<%} if (canTrasf) {%>    
    //Funzione per trasferimento Ramo Aziendale
    function trasfRamoAzienda() {
    	if (isInSubmit()) return;
			var get = "AdapterHTTP?PAGE=TrasfRamoSceltaAziendePage&cdnFunzione=<%=_funzione%>";
		setWindowLocation(get);    	
    }
	<%}%> 
	
	
	function to_upper(text_object){text_object.value = (text_object.value).toUpperCase();}
	
	function cerca(){
		var msg;
		if (isInSubmit()) return;
		var datiOk = controllaFunzTL();
		var tipoAvv = document.Frm1.codTipoAss.value;
		var cdnLav = document.Frm1.CDNLAVORATORE.value;
		var prgAz = document.Frm1.PRGAZIENDA.value;
		var prgAzUt = document.Frm1.PRGAZIENDAUt.value;
		var datMovDa = document.Frm1.datmovimentoda.value;
		var datMovA = document.Frm1.datmovimentoa.value;
		var codComunic = document.Frm1.codComunicazione.value;
		var codComunicPrec = document.Frm1.codComunicazionePrec.value;
		var dataDiOggi = new Date();
		var giornoOggi=dataDiOggi.getDate().toString();
		var meseOggi=(dataDiOggi.getMonth() +1).toString();
		
		if(giornoOggi.length == 1){
			giornoOggi = '0' + giornoOggi;
		}
		if(meseOggi.length == 1){
			meseOggi = '0' + meseOggi;	 	
		}
		dataDiOggi = giornoOggi + '/' + meseOggi + '/' + dataDiOggi.getFullYear().toString();
		var periodoDataMov = confrontaDate(datMovDa, datMovA) + 1;
		var esito = false;
		var GG_MAX_MOV = 90;
		var GG_MAX = 30;
		if (document.Frm1.ricXArt13[1].checked || document.Frm1.ricXArt13[2].checked) {
			GG_MAX = 180;
			GG_MAX_MOV = 180;
		}
		if(((datMovDa != "") && (datMovA != ""))|| (tipoAvv != "")){
			if(tipoAvv != "" ){
				if(tipoAvv == 'NO0' ){
					if(periodoDataMov <= GG_MAX){
						esito = true;
					}
				}else{
					if(periodoDataMov <= GG_MAX_MOV){
						 esito = true;
					}
				}	
			}
			if(periodoDataMov <= GG_MAX ){
				 esito = true;
			}
		}
		if((confrontaDate(datMovDa, dataDiOggi)) < 0 ){
			 esito = true;
		}		
		if((cdnLav != "") || (prgAz != "") || (prgAzUt != "")){
			 esito = true;	
		}
		
		if(codComunic != ""){ esito = true; }
		
		if(codComunicPrec != ""){ esito = true; }
		
		if (datiOk && esito) { 
				doFormSubmit(document.Frm1);				
		}else{
			msg = "Parametri generici.\n" + 
			"OPZIONE 1 - Uno dei seguenti valori: Lavoratore / Azienda / Azienda utilizzatrice.\n" + 
			"OPZIONE 2 - Periodo data movimento da... a... con periodo max 30 gg.\n" + 
			"OPZIONE 3 - Tutti i seguenti valori : Tipo avviamento / Periodo data movimento da... a... \n"+
			"            con periodo max  90 gg  (se movimento = NO0 si applica OPZIONE 2).\n" + 
			"OPZIONE 4 - Data movimento da...  maggiore della data odierna.\n" +
			"OPZIONE 5 - Codice comunicazione.\n" +
 			"OPZIONE 6 - Codice comunicazione precedente.\n" +
			"OPZIONE 7 - Se selezionato il filtro sull'Art.13: periodo data movimento da... a... con periodo max 180 gg";			
		
			alert (msg);
			undoSubmit();
		}
		
	}
	
	function gestioneArt13(abilitaTipoMov) {
		if (document.Frm1.ricXArt13[0].checked) {
			if (document.Frm1.tipoMovimento.disabled) {
				document.Frm1.tipoMovimento.value="";
				document.Frm1.tipoMovimento.disabled=false;


			}
		} else {
			if (abilitaTipoMov) {




				document.Frm1.tipoMovimento.value="AVV";
				document.Frm1.tipoMovimento.disabled=true;
			} else {


				document.Frm1.tipoMovimento.value="AVV";
				document.Frm1.tipoMovimento.disabled=true;
			}
		}
	}
-->
 </script>
</head>

	<body class="gestione" onload="rinfresca();cambiaCombo(document.Frm1.tipoMovimento);">
  <br/>
  <p class="titolo">Ricerca Movimenti</p>
  <br/>
	<center>
	<af:form name="Frm1" method="POST" action="AdapterHTTP" >

    <input type="hidden" name="paginaMansione" value=""/>
    <input type="hidden" name="PAGE" value="MovimentiRisultRicercaPage"/>
    <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>

    <input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"/>
    <input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"/>
    <input type="hidden" name="PRGUNITA" value="<%=prgUnita%>"/>
    <input type="hidden" name="PRGAZIENDAUt" value="<%=prgAziendaUt%>"/>
    <input type="hidden" name="PRGUNITAUt" value="<%=prgUnitaUt%>"/>
    <input type="hidden" name="PROVENIENZA" value="<%=provenienza%>"/>
    <input type="hidden" name="RICERCA_DA" value="MovimentiRicercaPage"/>
      <%out.print(htmlStreamTop);%>     
      <table class="main" border="0">
        <!-- sezione lav se si proviene dal menu contestuale del lav. -->
        <%@ include file="SezioneLavoratore.inc" %>
        <!-- sezione azienda -->
        <tr class="note">
          <td colspan="2">
          <div class="sezione2">
            <img id='tendinaAzienda' alt="Chiudi" src="../../img/aperto.gif" onclick="cambiaTendina(this,'aziendaSez',document.Frm1.pIva);"/>&nbsp;&nbsp;&nbsp;Azienda
          &nbsp;&nbsp;
          <% if (prgAzienda.equals("") && prgUnita.equals("")){%>
            <a href="#" onClick="javascript:apriSelezionaSoggetto('Aziende', 'aggiornaAzienda','','','');"><img src="../../img/binocolo.gif" alt="Cerca"></a>
          <%}%>
          &nbsp;<a href="#" onClick="javascript:azzeraAzienda();aggiornaTipoContratto('', '', '', '');setProvenienza('ListaMov');"><img src="../../img/del.gif" alt="Azzera selezione"></a>
          </div>
          </td>
        <tr>
          <td colspan="2">
            <div id="aziendaSez" style="display: none;">
              <table class="main" width="100%" border="0">
                  <tr>
                    <td class="etichetta">Codice Fiscale</td>
                    <td class="campo">
                      <af:textBox classNameBase="input" type="text" name="codFiscaleAzienda" readonly="true" value="<%=codFiscaleAzienda%>" size="30" maxlength="16"  />                       
                    </td>
                  </tr>
                  <tr>
                    <td class="etichetta">Partita IVA</td>
                    <td class="campo">
                      <af:textBox classNameBase="input" type="text" name="pIva" readonly="true" value="<%=pIva%>" size="30" maxlength="11"/>
                      &nbsp;&nbsp;&nbsp;Validità C.F./P. IVA&nbsp;&nbsp;<af:textBox classNameBase="input" type="text" name="FLGDATIOK" readonly="true" value="<%=strFlgDatiOk%>" size="3" maxlength="3"/>
                    </td>
                  </tr>
                  <tr>
                    <td class="etichetta">Ragione Sociale</td>
                    <td class="campo">
                      <af:textBox classNameBase="input" type="text" name="ragioneSociale" readonly="true" value="<%=ragioneSociale%>" size="60" maxlength="100"/>
                    </td>
                  </tr>
                  <tr>
                    <td class="etichetta">Indirizzo (Comune)</td>
                    <td class="campo">
                      <af:textBox classNameBase="input" type="text" name="IndirizzoAzienda" readonly="true" value="<%=IndirizzoAzienda%>" size="60" maxlength="100"/>
                    </td>
                  </tr>
                  <tr>
                    <td class="etichetta">Tipo Azienda</td>
                    <td class="campo">
                      <af:textBox classNameBase="input" type="text" name="descrTipoAz" readonly="true" value="<%=descrTipoAz%>" size="30" maxlength="30"/>
                      <af:textBox classNameBase="input" type="hidden" name="codTipoAz" readonly="true" value="<%=codTipoAz%>" size="10" maxlength="10"/>
                      <af:textBox classNameBase="input" type="hidden" name="CODNATGIURIDICA" readonly="true" value="<%=codnatGiurAz%>" size="10" maxlength="10"/>
                    </td>
                  </tr>
                </table>
            </div>
        </td>
      </tr>
    <tr class="note">
      <td colspan="2">
      <div class="sezione2">
        <img id='tendinaAziendaUt' alt="Chiudi" src="../../img/aperto.gif" onclick="cambiaTendina(this,'aziendaSezUt',document.Frm1.pIvaUt);"/>&nbsp;&nbsp;&nbsp;Azienda utilizzatrice / Ente promotore
      &nbsp;&nbsp;
      <% if (prgAziendaUt.equals("") && prgUnitaUt.equals("")){%>
        <a href="#" onClick="javascript:apriSelezionaSoggetto('Aziende', 'aggiornaAziendaUt','','','');"><img src="../../img/binocolo.gif" alt="Cerca"></a>
      <%}%>
      &nbsp;<a href="#" onClick="javascript:azzeraAziendaUt();"><img src="../../img/del.gif" alt="Azzera selezione"></a>
      </div>
      </td>
	    <tr>
	      <td colspan="2">
	        <div id="aziendaSezUt" style="display: none;">
	          <table class="main" width="100%" border="0">
	              <tr>
	                <td class="etichetta">Codice Fiscale</td>
	                <td class="campo">
	                  <af:textBox classNameBase="input" type="text" name="codFiscaleAziendaUt" readonly="true" value="<%=codFiscaleAziendaUt%>" size="30" maxlength="16"/>                       
	                </td>
	              </tr>
	              <tr>
	                <td class="etichetta">Partita IVA</td>
	                <td class="campo">
	                  <af:textBox classNameBase="input" type="text" name="pIvaUt" readonly="true" value="<%=pIvaUt%>" size="30" maxlength="11"/>
	                  &nbsp;&nbsp;&nbsp;Validità C.F./P. IVA&nbsp;&nbsp;<af:textBox classNameBase="input" type="text" name="FLGDATIOKUt" readonly="true" value="<%=strFlgDatiOkUt%>" size="3" maxlength="3"/>
	                </td>
	              </tr>
	              <tr>
	                <td class="etichetta">Ragione Sociale</td>
	                <td class="campo">
	                  <af:textBox classNameBase="input" type="text" name="ragioneSocialeUt" readonly="true" value="<%=ragioneSocialeUt%>" size="60" maxlength="100"/>
	                </td>
	              </tr>
	              <tr>
	                <td class="etichetta">Indirizzo (Comune)</td>
	                <td class="campo">
	                  <af:textBox classNameBase="input" type="text" name="IndirizzoAziendaUt" readonly="true" value="<%=IndirizzoAziendaUt%>" size="60" maxlength="100"/>
	                </td>
	              </tr>
	              <tr>
	                <td class="etichetta">Tipo Azienda</td>
	                <td class="campo">
	                  <af:textBox classNameBase="input" type="text" name="descrTipoAzUt" readonly="true" value="<%=descrTipoAzUt%>" size="30" maxlength="30"/>
	                  <af:textBox classNameBase="input" type="hidden" name="codTipoAzUt" readonly="true" value="<%=codTipoAzUt%>" size="10" maxlength="10"/>
	                  <af:textBox classNameBase="input" type="hidden" name="CODNATGIURIDICAUt" readonly="true" value="<%=codnatGiurAzUt%>" size="10" maxlength="10"/>
	                </td>
	              </tr>
	            </table>
	        </div>
	    </td>
	  </tr>
	  
	<%-- [START ]Agenzia di somministrazione --%>
	<tr class="note">
      <td colspan="2">
      <div class="sezione2">
        <img id='tendinaMov' alt='Chiudi' src='../../img/aperto.gif' onclick="cambia(this, document.getElementById('agenziaDiSomministrazioneSez'));" />&nbsp;&nbsp;&nbsp;
        Agenzia di somministrazione</div>
      </td>
    </tr>
    
    <tr>
      <td colspan="2">
        <div id="agenziaDiSomministrazioneSez" style="display: inline;">
          <table class="main" width="100%" border="0">
          	<tr>
				<td class="etichetta" nowrap>Agenzia estera</td>
				<td class="campo">
					<af:comboBox name="flgAzEstera" classNameBase="input" addBlank="false" required="false" 
				 	     title="Agenzia estera"> 
					        <OPTION value=""></OPTION>
					        <OPTION value="S" <%--if (flgCasoDubbio != null && flgCasoDubbio.equalsIgnoreCase("S")) out.print("SELECTED=\"true\"");--%>>Sì</OPTION>
					        <OPTION value="N" <%--if (flgCasoDubbio != null && flgCasoDubbio.equalsIgnoreCase("N")) out.print("SELECTED=\"true\"");--%>>No</OPTION>
					    </af:comboBox>
				</td>
			</tr>
			
			<tr>
				<td class="etichetta" nowrap>Codice fiscale</td>
				<td class="campo">
					<af:textBox type="text" name="strCfAzEstera" value="" size="30" maxlength="99" onKeyUp="to_upper(this);"/>
				</td>
			</tr>
			
			<tr>
				<td class="etichetta" nowrap>Ragione sociale</td>
				<td class="campo">
					<af:textBox type="text" name="strRagSocAzEstera" value="" size="60" maxlength="100"/>
				</td>
			</tr>
			
			</table>
			</div>
			</td>
			</tr>         
    <%-- [END ]Agenzia di somministrazione --%>
	  
      <%@ include file="./_sezioneMovimentiXRicerca.inc" %>
      <tr><td colspan="2">&nbsp;</td></tr>
      <tr class="note">
          <td colspan="2">
          <div class="sezione2">
            <img id='art13' alt="Chiudi" src="../../img/aperto.gif" onclick="cambiaTendina(this,'art13Sez','');"/>&nbsp;&nbsp;&nbsp;Art.13
          </div>
          </td>
        <tr>
          <td colspan="2">
            <div id="art13Sez" style="display: ">
              <table class="main" width="100%" border="0">
                  <tr>
                    <td class="etichetta"><input type="radio" name="ricXArt13" value="NIET" onclick="gestioneArt13(true)" checked></td>
                    <td class="campo">Nessuna condizione sull'Art.13</td>
                  </tr>
                  <tr>
                    <td class="etichetta"><input type="radio" name="ricXArt13" value="VALIDI" onclick="gestioneArt13(false)"></td>
                    <td class="campo">Seleziona i movimenti che hanno nell'agevolazione l'indicazione relativa alla L.68/9 Art. 13 e data Art13 non valorizzata.</td>
		  		 </tr>
                  <tr>
                    <td class="etichetta"><input type="radio" name="ricXArt13" value="CESSATI" onclick="gestioneArt13(true)"></td>
                    <td class="campo">Gli Art.13 cessati in anticipo. I movimenti con la data Art.13 che sono stati cessati in anticipo rispetto a quella data.</td>
                  </tr>
                </table>
            </div>
        </td>
      </tr>       
      <tr>
		<td colspan="2" align="center">
		<!--<input class="pulsanti" type="submit" id="pulsanteRicerca" name="azione" value="Cerca"/>-->
		<input class="pulsanti" type="button" onclick="cerca();" name="azione" value="Cerca"/>		
		&nbsp;&nbsp;
		<input class="pulsanti" type="reset" name="azione" value="Annulla"/>
      <tr>
		<td colspan="2" align="center">
		&nbsp;
		</td>
	  </tr>
      <tr>
	    <td colspan="2" align="center">	  		
		<%if (canInsert) {%>		
		&nbsp;&nbsp;
		<input class="pulsanti" type="button" onclick="inserisciNuovo();" name="azione" value="Nuovo movimento" title="Inserisci un nuovo movimento"/>
		<%} if ((canImport) && (provenienza.equalsIgnoreCase("generale") || 
								provenienza.equalsIgnoreCase("ListaMov") || 
								provenienza.equals("") )) {%>		
		&nbsp;&nbsp;       
		<input class="pulsanti" type="button" onclick="importaMovimenti();" name="azione" value="Importa movimenti" title="Importa movimenti da un file"/>        
		<%} if (canTrasf) {%> 
		&nbsp;&nbsp;
		<input class="pulsanti" type="button" onclick="trasfRamoAzienda();" name="azione" value="Trasferimenti - Trasformazioni Aziende" title="Trasferimenti - Trasformazioni Aziende"/>        
		<%}%>           
        </td>     
      </tr>  
      </table>
      <%out.print(htmlStreamBottom);%>
      </af:form>
	</center>
<script language="javascript">
  var imgV = document.getElementById("tendinaLav");
  <% if (!strNomeLav.equals("")){%>
    cambiaLavMC("lavoratoreSez","inline");
    imgV.src = imgAperta;
    imgV.alt="Chiudi";
  <%} else {%>
    cambiaTendina(imgV,"lavoratoreSez",document.Frm1.nome);
  <%}%>
        
  imgV = document.getElementById("tendinaAzienda");
  <% if (!pIva.equals("")){%>
    cambiaLavMC("aziendaSez","inline");
    imgV.src = imgAperta;
    imgV.alt="Chiudi";
  <%} else {%>
    cambiaTendina(imgV,"aziendaSez",document.Frm1.pIva)
  <%}%>

  imgV = document.getElementById("tendinaAziendaUt");
  <% if (!pIvaUt.equals("")){%>
    cambiaLavMC("aziendaSezUt","inline");
    imgV.src = imgAperta;
    imgV.alt="Chiudi";
  <%} else {%>
    cambiaTendina(imgV,"aziendaSezUt",document.Frm1.pIvaUt)
  <%}%>

  imgV = document.getElementById("tendinaMov");
  cambiaLavMC("movimentoSez","inline");
  imgV.src = imgAperta;
  imgV.alt="Chiudi";
</script> 
	</body>
</html>
