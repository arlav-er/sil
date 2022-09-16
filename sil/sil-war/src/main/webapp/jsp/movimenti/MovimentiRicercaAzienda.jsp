<!-- @author: Paolo Roccetti - Gennaio 2004 -->
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
  PageAttribs attributi = new PageAttribs(user, "MovimentiRicercaAziendaPage");
  boolean canInsert =  attributi.containsButton("INSERISCI");
  boolean canTrasfDaAzienda =  attributi.containsButton("TRASFERISCI_RAMO_DA_AZIENDA");
  //Stabilisce da quale menu contestuale si proviene
  String provenienza = "";
  // variabili richieste dalla sezione del lavoratore  (vedi commento su SezioneLavoratore.inc)
  String fDelLav = "";
  String fSelLav = "";
  //
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

  if (provenienza.equalsIgnoreCase("generale")){
    provenienza = "generale";
  } else
        if (!prgAzienda.equals("") && !prgUnita.equals("")){
          provenienza = "azienda";
        }
  
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

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Ricerca Movimenti</title>

  <%@ include file="../presel/Function_CommonRicercaCCNL.inc" %>
  <%@ include file="../movimenti/DynamicRefreshCombo.inc" %>
  <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
  </SCRIPT>

  <%@ include file="MovimentiRicercaSoggetto.inc" %>
  <%@ include file="MovimentiSezioniATendina.inc" %>
  <%@ include file="Common_Function_Mov.inc" %>

<!--Funzioni per l'aggiornamento della form -->
<script type="text/javascript">
<!--

	var cdnLavoratore = '<%=cdnLavoratore%>';
	var prgAziendaUt = '<%=prgAziendaUt%>';
	var prgUnitaUt = '<%=prgUnitaUt%>';
	
	var flagRicercaPage = "S";
	
    function to_upper(text_object){
	  	text_object.value = (text_object.value).toUpperCase();
    }
	
	
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
    }

	//MODIFICA DI GIOVANNI D'AURIA 03_02_05 BEGIN**************************
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
    //MODIFICA DI GIOVANNI D'AURIA 03_02_05 END*****************************  


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
        cdnLavoratore = opened.dati.cdnLavoratore;
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
      window.open("AdapterHTTP?PAGE=RicercaMansioneAvanzataPage", "Mansioni", 'toolbar=0, scrollbars=1,height=600, width=800');
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
	<%if (canInsert) {%>
    //Funzione per inserimento nuovo movimento
    function inserisciNuovo() {
		var get = "AdapterHTTP?PAGE=MovDettaglioGeneraleInserisciPage&cdnFunzione=<%=_funzione%>"
		 + "&CDNLAVORATORE=" + cdnLavoratore + "&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>"
		 + "&PRGAZIENDAUt=" + prgAziendaUt + "&PRGUNITAUt=" + prgUnitaUt + "&PROVENIENZA=<%=provenienza%>"
		 + "&CURRENTCONTEXT=inserisci&collegato=nessuno";
		setWindowLocation(get);    	
    }
	<%} if (canTrasfDaAzienda) {%>    
    //Funzione per trasferimento Ramo Aziendale
    function trasfRamoAzienda() {
		var get = "AdapterHTTP?PAGE=TrasfRamoSceltaAziendePage&cdnFunzione=<%=_funzione%>"
		 + "&PRGAZIENDAPROVENIENZA=<%=prgAzienda%>&PRGUNITAPROVENIENZA=<%=prgUnita%>";
		setWindowLocation(get);    	
    }
	<%}%> 
-->
 </script>
</head>

	<body class="gestione" onload="rinfresca();cambiaCombo(document.Frm1.tipoMovimento);">
  <br/>
  <p class="titolo">Ricerca Movimenti</p>
  <br/>
		<center>
			<af:form name="Frm1" method="POST" action="AdapterHTTP">
        <input type="hidden" name="paginaMansione" value=""/>
        <input type="hidden" name="PAGE" value="MovimentiRisultRicercaPage"/>
        <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>

        <input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"/>
        <input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"/>
        <input type="hidden" name="PRGUNITA" value="<%=prgUnita%>"/>
	    <input type="hidden" name="PRGAZIENDAUt" value="<%=prgAziendaUt%>"/>
	    <input type="hidden" name="PRGUNITAUt" value="<%=prgUnitaUt%>"/>
        <input type="hidden" name="PROVENIENZA" value="<%=provenienza%>"/>			
      <%out.print(htmlStreamTop);%>
      <table class="main" border="0">     
          <tr class="note">
            <td colspan="2">
            <div class="sezione2">
              <img id='tendinaAzienda' alt="Chiudi" src="../../img/aperto.gif" onclick="cambiaTendina(this,'aziendaSez',document.Frm1.pIva);"/>&nbsp;&nbsp;&nbsp;Azienda
            &nbsp;&nbsp;
            <% if (prgAzienda.equals("") && prgUnita.equals("")){%>
              <a href="#" onClick="javascript:apriSelezionaSoggetto('Aziende', 'aggiornaAzienda','','','');"><img src="../../img/binocolo.gif" alt="Cerca"></a>
            <%}%>
            </div>
            </td>
          </tr>
          <tr>
            <td colspan="2">
              <div id="aziendaSez" style="display: none;">
                <table class="main" width="100%" border="0">
                    <tr>
                      <td class="etichetta">Codice Fiscale</td>
                      <td class="campo">
                        <af:textBox classNameBase="input" type="text" name="codFiscaleAzienda" readonly="true" value="<%=codFiscaleAzienda%>" size="30" maxlength="16"/>                       
                      </td>
                    </tr>
                    <tr valign="top">
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
        <!-- sezione lav se si proviene dal menu contestuale del lav. -->
        <%@ include file="SezioneLavoratore.inc" %>
        <!-- sezione mov -->
      <%@ include file="../movimenti/_sezioneMovimentiXRicerca.inc" %>
      <tr><td colspan="2">&nbsp;</td></tr>                                  
      <tr>
        <td colspan="2" align="center">
        <input class="pulsanti" type="submit" name="azione" value="Cerca"/>
        &nbsp;&nbsp;
        <input class="pulsanti" type="reset" name="annulla" value="Annulla"/>
        </td>
      </tr>
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
		<%} if (canTrasfDaAzienda) {%> 
		&nbsp;&nbsp;
		<input class="pulsanti" type="button" onclick="trasfRamoAzienda();" name="azione" value="Trasferimenti - Trasformazioni Aziende" title="Trasferimenti - Trasformazioni Aziende"/>        
		<%}%>           
        </td>
      </tr>
        
      </table>
      <%out.print(htmlStreamBottom);%>
      </af:form>
      <br/> 
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
