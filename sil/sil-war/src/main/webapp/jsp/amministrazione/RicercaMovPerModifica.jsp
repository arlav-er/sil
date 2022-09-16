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
  PageAttribs attributi = new PageAttribs(user, "ForzaModMovPage");
   // variabili richieste dalla sezione del lavoratore  (vedi commento su SezioneLavoratore.inc)
  String fSelLav = "";
  // quando si cancella il lavoratore selezionato bisogna resettare il campo PROVENIENZA
  String fDelLav = "setProvenienza('ListaMov')";
  //
  //Stabilisce da quale menu contestuale si proviene
  String provenienza = "";
  
  String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
  provenienza = StringUtils.getAttributeStrNotNull(serviceRequest,"PROVENIENZA");
  
  
  SourceBean data = (SourceBean) serviceResponse.getAttribute("M_GetForzaturaAbil.ROWS");
  BigDecimal conf = (BigDecimal)data.getAttribute("ROW.NUM");
  boolean canView = (conf.intValue()==0)?false:true;
  if ( !canView ){
      response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  }
  
  //Oggetti per l'applicazione dello stile grafico
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);

  //Genero informazioni sul lavoratore se sono in un menu contestuale
  String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "CDNLAVORATORE");
  String strCodiceFiscaleLav = "";
  String strNomeLav = "";
  String strCognomeLav = "";
  String dataNascitaLav ="";
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
    dataNascitaLav = datiLav.getDataNasc();
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
	
	
    function setMovimentoDescr(combo){
    	var itemValue = combo.options[combo.selectedIndex].text;
    	document.Frm1.movimentoDescr.value=itemValue
    }
    
    function setStatoDescr(combo){
    	var itemValue = combo.options[combo.selectedIndex].text;
    	document.Frm1.statoDescr.value=itemValue
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
        document.Frm1.datnasc.value = opened.dati.datNasc;
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
 

    function fieldChanged() {
      //non faccio niente!
    }
 
    
    //Ricerca della mansione per descrizione
	function selectMansionePerDescrizione(desMansione) {
		window.open("AdapterHTTP?PAGE=RicercaMansionePage&desMansione="+desMansione.value+"&flgFrequente=", "Mansioni", 'toolbar=0, scrollbars=1');          
	}
 
    
    function setProvenienza(newValue) {
    	document.Frm1.PROVENIENZA.value = newValue;
    	provenienza = newValue;
    }	
  	function verificaCatene(){
  		var msg;
		if (isInSubmit()) return;
		 
		var cdnLav = document.Frm1.CDNLAVORATORE.value;
		 
		var esito = false;

		if((cdnLav == "") ){
			 alert("Attenzione: è necessario selezionare un lavoratore.")
			return;
		}else{
			var f = "AdapterHTTP?PAGE=VerificaCateneMovLavPage" + 
			"&CFLAVORATORE=" +  document.Frm1.codiceFiscaleLavoratore.value 
			 ;
		 
	    var t = "_blank";
	    var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=1000,height=400,top=100,left=100"
	    window.open(f, t, feat);
		}
		
  	}
	function cerca(){
		var msg;
		if (isInSubmit()) return;
		var datiOk = controllaFunzTL();
		 
		var cdnLav = document.Frm1.CDNLAVORATORE.value;
		var prgAz = document.Frm1.PRGAZIENDA.value;
		var prgAzUt = document.Frm1.PRGAZIENDAUt.value;
		var datMovDa = document.Frm1.datmovimentoda.value;
		var datMovA = document.Frm1.datmovimentoa.value;
		 
		var esito = false;

		if((cdnLav == "") ){
			 alert("Il lavoratore è obbligatorio.")
			 undoSubmit();
			return;
		}else{
			esito = true;
		}
		
		if(datMovDa!="" && datMovA!="" && (confrontaDate(datMovDa, datMovA)) > 0 ){
			 esito = true;
		}else	if(datMovDa!="" && datMovA!="" && (confrontaDate(datMovDa, datMovA)) <= 0 )	{
			alert("Data mov. da deve essere inferiore a Data mov. a");
			undoSubmit();
			return;
			
		}
					
		if (datiOk && esito) { 
				doFormSubmit(document.Frm1);				
		}else{
			undoSubmit();
		}
		
	}
 
-->
 </script>
</head>

	<body class="gestione" onload="rinfresca();">
  <br/>
  <p class="titolo">Ricerca Movimenti per Modifiche Forzate</p>
  <br/>
	<center>
	<af:form name="Frm1" method="POST" action="AdapterHTTP" >

    <input type="hidden" name="PAGE" value="ListaForzaModMovPage"/>
    <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
 
	<input type="hidden" name="movimentoDescr" value=""/>
	<input type="hidden" name="statoDescr" value=""/>
	<input type="hidden" name="datnasc" value="<%=dataNascitaLav%>"/>
    <input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"/>
    <input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"/>
    <input type="hidden" name="PRGUNITA" value="<%=prgUnita%>"/>
    <input type="hidden" name="PRGAZIENDAUt" value="<%=prgAziendaUt%>"/>
    <input type="hidden" name="PRGUNITAUt" value="<%=prgUnitaUt%>"/>
    <input type="hidden" name="PROVENIENZA" value="<%=provenienza%>"/>
    <input type="hidden" name="RICERCA_DA" value="ForzaModMovPage"/>
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

	  
<tr class="note">
      <td colspan="2">
      <div class="sezione2">
        <img id='tendinaMov' alt="Chiudi" src="../../img/aperto.gif" onclick="cambiaTendina(this,'movimentoSez',document.Frm1.datcomunicazioneda);"/>&nbsp;&nbsp;&nbsp;
        Dati Movimento
      </div>
      </td>
    </tr>
    <tr>
      <td colspan="2">
        <div id="movimentoSez" style="display: none;">
          <table class="main" width="100%" border="0" cellpadding="2" celspacing="0">

          
            <tr>
              <td class="etichetta" nowrap>Data mov. da</td>
              <td class="campo" >
                      <af:textBox type="date" name="datmovimentoda" validateOnPost="true" value="" size="10" maxlength="10"/>
                      a&nbsp;&nbsp;<af:textBox type="date" name="datmovimentoa" validateOnPost="true" value="" size="10" maxlength="10"/>
              </td>            
            </tr>
			<tr>
			  <td class="etichetta">Tipo movimento</td>
			    <td>
			      <table border="0" cellpadding="0" cellspacing="0">
			        <tr>
			          <td class="campo" nowrap>
			            <af:comboBox classNameBase="input" name="tipoMovimento" moduleName="ComboTipoMovimento" addBlank="true" onChange="javascript:setMovimentoDescr(this);" />
			          </td>
			       </tr>
			    </table>
			  </td>
			</tr>
          

            <tr>
              <td class="etichetta">Stato movimento</td>
			  <td> 
			  	<table border="0" cellpadding="0" cellspacing="0">
			  	<tr>             
	              <td class="campo" nowrap>
					<af:comboBox classNameBase="input" name="CODSTATOATTO" moduleName="ComboStatoAtto" title="Stato Atto" addBlank="true"  onChange="javascript:setStatoDescr(this);"  />	              
	              </td>
				  
              	</tr>
              	</table>
              </td>
            </tr>
            </table border="0" cellpadding="0" cellspacing="0">
          </div>
        </td>
      </tr>
      <tr><td colspan="2">&nbsp;</td></tr>
     
      <tr>
		<td colspan="2" align="center">
		<!--<input class="pulsanti" type="submit" id="pulsanteRicerca" name="azione" value="Cerca"/>-->
		<input class="pulsanti" type="button" onclick="cerca();" name="azione" value="Cerca"/>		
		&nbsp;&nbsp;
		<input class="pulsanti" type="reset" name="azione" value="Annulla"/>
		</td>
	 </tr>
	  <tr>
		<td colspan="2" align="center">
		<input class="pulsanti" type="button" onclick="verificaCatene();" name="azione" value="Verifica Catene Movimenti"/>	
		</td>
	  </tr>
      <tr>
		<td colspan="2" align="center">
		&nbsp;
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
