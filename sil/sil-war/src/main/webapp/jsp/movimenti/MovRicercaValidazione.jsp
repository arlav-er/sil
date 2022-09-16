<!-- @author: Paolo Roccetti - Gennaio 2004 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  java.text.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, (String) serviceRequest.getAttribute("PAGE"));
  boolean canValidaTutti = attributi.containsButton("VALIDATUTTI");
  
  String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");

  String canConsultArchivioLav = "false";
  Object canConsultArchivio = serviceResponse.getAttribute("M_CheckConsultArchivioMov.ArchivioConsultabile");
  if (canConsultArchivio != null) {
  	canConsultArchivioLav = (String) canConsultArchivio;
  }
  
  //Oggetti per l'applicazione dello stile grafico
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
  //Variabili per la gestione della protocollazione ================
  String prAutomatica     = null; 
  String estReportDefautl = null;
  BigDecimal numProtV     = null;
  BigDecimal numAnnoProtV = null;
  String     datProtV     = (new SimpleDateFormat("dd/MM/yyyy")).format(new Date());
  String     oraProtV     = (new SimpleDateFormat("HH:mm")).format(new Date());
  String     docInOut     = "";
  String     docRif       = "";
  boolean numProtEditable = false;
  Vector rowsPR           = null;
  SourceBean rowPR        = null;

   rowsPR = serviceResponse.getAttributeAsVector("M_GetProtocollazione.ROWS.ROW");
   if(rowsPR != null && !rowsPR.isEmpty()) {
     rowPR = (SourceBean) rowsPR.elementAt(0);
     prAutomatica     = (String) rowPR.getAttribute("FLGPROTOCOLLOAUT");
     if ( prAutomatica.equalsIgnoreCase("N") ) {
     	numProtEditable = true; 
     }
     else {
     	numProtV        = (BigDecimal) rowPR.getAttribute("NUMPROTOCOLLO");
     }
     numAnnoProtV       = new BigDecimal(datProtV.substring(6,10));
     estReportDefautl   = (String) rowPR.getAttribute("CODTIPOFILEESTREPORT");
   }
  
  //========================================================================
%>
<%@ include file="GestioneValidazioneMassiva.inc" %>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Ricerca Movimenti da Validare</title>

  <%@ include file="../presel/Function_CommonRicercaCCNL.inc" %>
  <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
  </SCRIPT>
  
</head>

	<body class="gestione" onload="rinfresca();">
  <br/>
  <p class="titolo">Ricerca Movimenti da Validare</p>
  <br/>

<%@ include file="MovimentiRicercaSoggetto.inc" %>
<%@ include file="MovimentiSezioniATendina.inc" %>
<%@ include file="../movimenti/DynamicRefreshCombo.inc" %>
<%@ include file="Common_Function_Mov.inc" %>

<!--Funzioni per l'aggiornamento della form -->
<script type="text/javascript">

	var flagRicercaPage = "S";
	
    function aggiornaAzienda(){
        document.Frm1.ragioneSociale.value = opened.dati.ragioneSociale;
        document.Frm1.pIva.value = opened.dati.partitaIva;
        document.Frm1.codFiscaleAzienda.value = opened.dati.codiceFiscaleAzienda;
        document.Frm1.codTipoAzienda.value = opened.dati.codTipoAz;
		if (document.Frm1.descrTipoAz != null) {
        	document.Frm1.descrTipoAz.value = opened.dati.descrTipoAz;
        }
        document.Frm1.FLGDATIOK.value = opened.dati.FLGDATIOK;
        if ( document.Frm1.FLGDATIOK.value == "S" ){ 
                    document.Frm1.FLGDATIOK.value = "Si";
        }else 
              if ( document.Frm1.FLGDATIOK.value != "" ){
                document.Frm1.FLGDATIOK.value = "No";
              }
        if (document.Frm1.CODNATGIURIDICA != null) {
        	document.Frm1.CODNATGIURIDICA.value = opened.dati.CODNATGIURIDICA;
        }
        opened.close();
    }
    
    //Funzione aggiunta il 09/05/2005 da D'Auria Giovanni 
	function visualizzaTipoDescrMansione(stato){ 
   /*
      var dvar = document.getElementById("descrMans");
      dvar.style.display = stato;
      dvar = document.getElementById("tipoMans");
      dvar.style.display = stato;
     */  
    }

    function aggiornaLavoratore(){
        document.Frm1.codiceFiscaleLavoratore.value = opened.dati.codiceFiscaleLavoratore;
        document.Frm1.cognome.value = opened.dati.cognome;
        document.Frm1.nome.value = opened.dati.nome;

        document.Frm1.FLGCFOK.value = opened.dati.FLGCFOK;
        if ( document.Frm1.FLGCFOK.value == "S" ){ 
                    document.Frm1.FLGCFOK.value = "Si";
        }else 
              if ( document.Frm1.FLGCFOK.value != "" ){
                document.Frm1.FLGCFOK.value = "No";
              }
        opened.close();  
    }

    function selectMansione_onClick(codMansione, codMansioneHid, descMansione, strTipoMansione) {	

        if (codMansione.value==""){

          descMansione.value="";
          strTipoMansione.value="";
          //visualizzaTpoDescrMansione("none");
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
    	var f = "AdapterHTTP?PAGE=SelezionaContrattiSelettivaPage&CODTIPOAZIENDA=" + document.Frm1.codTipoAzienda.value + "&codMonoTempo=" + document.Frm1.codMonoTempo.value;
        f = f + "&CRITERIO=" + criterio;
        f = f + "&codTipoAss=" + document.Frm1.codTipoAss.value;
        descr = document.Frm1.descrTipoAss.value;
  		descr = descr.replace("%","&amp;");
 		f = f + "&descrTipoAss=" + descr;
        f = f + "&updateFunctionName=aggiornaTipoContratto";
        var t = "_blank";
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=500,height=400,top=100,left=100";
        window.open(f, t, feat);
    }
    
    //Aggiorna il tipo di assunzione e le combo collegate
    function aggiornaTipoContratto(codice, descrizione, codmonotipo, codContratto) {
      document.Frm1.codTipoAss.value = codice;
      document.Frm1.descrTipoAss.value = descrizione;
    }

    function visualizzaTpoDescrMansione(stato){
      var dvar = document.getElementById("descrMans");
      dvar.style.display = stato;
      dvar = document.getElementById("tipoMans");
      dvar.style.display = stato;
    }

// =============================================================================
//     Sono le stesse funzioni js usate in GestisciStatoDoc.jsp
//                                                                by Roccetti
// =============================================================================
function checkAndFormatTime(oraObj)
{
  var strTime = oraObj.value;
  var strHours = "";
  var strMin   = "";
  var separator = ":";
  var strTimeArray;

  var titleObj = "ora"; 
  if(oraObj.title != null)
  { titleObj = oraObj.title;
  }
  
   if (strTime.indexOf(separator) != -1) {
      strTimeArray = strTime.split(separator);
      if (strTimeArray.length != 2) {
         alert("Il campo "+titleObj+" non ha un orario scritto nel formato corretto:\nscrivere HH"+separator+"MM   es: 08"+separator+"12")
         return false;
      }
      else {
       strHours = strTimeArray[0];
       strMin   = strTimeArray[1];
      }
   }
   else if(strTime.length == 4)
   { //Non c'è il separatore, probabilmente è stata inserita un'orario nel formato 1215 -> 12:15
     //che comunque reputiamo valido...
     strHours = strTime.substr(0,2);
     strMin   = strTime.substr(2,4);
   }
   else
   { alert("Il campo "+titleObj+" non ha un orario scritto nel formato corretto:\nscrivere HH"+separator+"MM   es: 08"+separator+"12");
     return false;
   }

   var hours = parseInt(strHours, 10);
   var min   = parseInt(strMin, 10);
   
   if(isNaN(hours))
   { alert("L'ora inserita nel campo "+titleObj+" non è un numero:\nscrivere HH"+separator+"MM   es: 08"+separator+"12");
     return false;
   }
   if(isNaN(min))
   { alert("I minuti inseriti nel campo "+titleObj+" non sono un numero:\nscrivere HH"+separator+"MM   es: 08"+separator+"12");
     return false;
   }

   hours = parseInt(strHours, 10);
   min   = parseInt(strMin, 10);
   
   if( hours<0 || hours > 23 )
   { alert("L'ora inserita nel campo "+titleObj+" non è orario valido.\nInserire un'ora compresa fra 0 e 23");
     return false;
   }
   if( min<0 || min > 59 )
   { alert("I minuti inseriti nel campo "+titleObj+" non sono corretti.\nInserire un numero compreso fra 0 e 59");
     return false;
   }

	oraObj.value = (hours<10?"0":"") + hours + separator + (min<10?"0":"") + min;
	return true;	  
   
}//end function


function cambiAnnoProt(dataPRObj,annoProtObj)
{
  var dataProt = dataPRObj.value;
  var lun = dataProt.length;

  //Stiamo modificando la data di protocollazione. Quindi cambia anche l'anno di protocollazione
  annoProtObj.value = ""; 
  
  if (lun > 5) {
    var tmpDate = new Object();
    tmpDate.value = dataProt;
    if ( checkFormatDate(tmpDate) ) {
       annoProtObj.value = tmpDate.value.substr(6,10);      
    }
    else if (lun==8 || lun==10) {
      alert("La data di protocollazione non è corretta");
    }
  }
  
}//end function
// =============================================================================

//funzione di get per il pulsante valida tutti
function validaTutti() {
	if (confirm('Vuoi validare tutti i movimenti presenti nel sistema?')) {
		var get = "AdapterHTTP?PAGE=MovValidazioneMassivaPage&cdnFunzione=<%=_funzione%>" 
			+ "&AZIONE=validaTutti&numAnnoProt=<%= Utils.notNull(numAnnoProtV) %>"
			+ "&numProt=<%= Utils.notNull(numProtV) %>&dataProt=<%=datProtV%>&oraProt=<%=oraProtV%>"
			+ "&tipoProt=<%=prAutomatica%>";
		setWindowLocation(get);
	}
}

//funzione di get per l'arresto della validazione massiva
function arrestaValidazione() {
	if (confirm('Vuoi arrestare la validazione massiva corrente?')) {
		var get = "AdapterHTTP?PAGE=MovValidazioneMassivaPage&cdnFunzione=<%=_funzione%>&AZIONE=arresta";
		setWindowLocation(get);
	}
}

//funzione di get per la visualizzazione dei risultati della validazione massiva
function visulizzaRisultati() {
		var get = "AdapterHTTP?PAGE=MovRisultValidazionePage&cdnFunzione=<%=_funzione%>&PAGERISULTVALMASSIVA=FIRST";
		setWindowLocation(get);
}

function to_upper(text_object) {
	text_object.value = (text_object.value).toUpperCase();
}

 </script>
 
		<center>
			<af:form name="Frm1" method="GET" action="AdapterHTTP">
         <%out.print(htmlStreamTop);%>
         <table class="main">
      <tr class="note">
        <td colspan="2">
        <div class="sezione2">
          <img id='tendinaLav' alt='Chiudi' src='../../img/aperto.gif' onclick="cambia(this, document.getElementById('lavoratoreSez'));" />&nbsp;&nbsp;&nbsp;
          Lavoratore&nbsp;&nbsp;
        <a href="#" onClick="javascript:apriSelezionaSoggetto('Lavoratori', 'aggiornaLavoratore','','','');"><img src="../../img/binocolo.gif" alt="Cerca"></a>
        &nbsp;<a href="#" onClick="javascript:azzeraLavoratore();"><img src="../../img/del.gif" alt="Azzera selezione"></a>
        </div>
        </td>
      </tr>
    <tr>
      <td colspan="2">
        <div id="lavoratoreSez" style="display: inline;">
          <table class="main" width="100%" border="0">
              <tr>
                <td class="etichetta">Codice Fiscale</td>
                <td class="campo">
                  <af:textBox classNameBase="input" type="text" name="codiceFiscaleLavoratore" value="" size="30" maxlength="16"/>
                  &nbsp;&nbsp;&nbsp;Validità C.F.&nbsp;&nbsp;<af:textBox classNameBase="input" type="text" name="FLGCFOK" readonly="true" value="" size="3" maxlength="3"/>
                </td>
              </tr>
              <tr>
                <td class="etichetta">Cognome</td>
                <td class="campo">
                  <af:textBox classNameBase="input" type="text" name="cognome" value="" size="30" maxlength="50"/>
                </td>
              </tr>
              <tr>
                <td class="etichetta">Nome</td>
                <td class="campo">
                  <af:textBox classNameBase="input" type="text" name="nome" value="" size="30" maxlength="50"/>
                </td>
              </tr>
            </table>
          </div>
        </td>
      </tr>
      
      <tr class="note">
        <td colspan="2">
        <div class="sezione2">
          <img id='tendinaAzienda' alt='Chiudi' src='../../img/aperto.gif' onclick="cambia(this, document.getElementById('aziendaSez'));" />&nbsp;&nbsp;&nbsp;
          Azienda&nbsp;&nbsp;
          <a href="#" onClick="javascript:apriSelezionaSoggetto('Aziende', 'aggiornaAzienda','','','');"><img src="../../img/binocolo.gif" alt="Cerca"></a>
          &nbsp;<a href="#" onClick="javascript:azzeraAzienda();"><img src="../../img/del.gif" alt="Azzera selezione"></a>
        </div>
        </td>
      </tr>
      <tr>
        <td colspan="2">
          <div id="aziendaSez" style="display: inline;">
            <table class="main" width="100%" border="0">
                <tr>
                  <td class="etichetta">Codice Fiscale</td>
                  <td class="campo">
                    <af:textBox classNameBase="input" type="text" name="codFiscaleAzienda" value="" size="30" maxlength="16"/>
                  </td>
                </tr>
                <tr>
                  <td class="etichetta">Partita IVA</td>
                  <td class="campo">
                    <af:textBox classNameBase="input" type="text" name="pIva" value="" size="30" maxlength="11"/>
                    &nbsp;&nbsp;&nbsp;&nbsp;Validità C.F. / P. IVA&nbsp;&nbsp;<af:textBox classNameBase="input" type="text" name="FLGDATIOK" readonly="true" value="" size="3" maxlength="3"/>
                  </td>
                </tr>
                <tr>
                  <td class="etichetta">Ragione Sociale</td>
                  <td class="campo">
                    <af:textBox classNameBase="input" type="text" name="ragioneSociale" value="" size="60" maxlength="100"/>
                  </td>
                </tr>
                <tr>
				  <td class="etichetta">Tipo Azienda</td>
				  <td class="campo">
				    <af:comboBox classNameBase="input" title="Tipo Azienda" name="codTipoAzienda" moduleName="M_GetTipiAzienda" addBlank="true" />
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
          
    <tr class="note">
      <td colspan="2">
      <div class="sezione2">
        <img id='tendinaMov' alt='Chiudi' src='../../img/aperto.gif' onclick="cambia(this, document.getElementById('movimentoSez'));" />&nbsp;&nbsp;&nbsp;
        Dati Movimento</div>
      </td>
    </tr>
    
    <tr>
      <td colspan="2">
        <div id="movimentoSez" style="display: inline;">
          <table class="main" width="100%" border="0">
          	<tr>
				<td class="etichetta" nowrap>Rif. Per pratica amm.</td>
				<td class="campo">
					<af:textBox type="text" name="referente" value="" size="42" maxlength="100" onKeyUp="to_upper(this);"/>
				</td>
			</tr>
			
			 <tr>
				<td class="etichetta">Codice comunicazione</td>	
				<td colspan="5" class="campo2">
					<table style="border-collapse:collapse">
						<tr>
							<td class="campo2">
								<af:textBox type="text" name="codComunicazione" value="" size="22" maxlength="20" onKeyUp="to_upper(this);"/>
							</td>
							<td>&nbsp;&nbsp;&nbsp;</td>
							<td class="etichetta2">Codice comunicazione precedente</td>
							<td class="campo2">
								<af:textBox type="text" name="codComunicazionePrec" value="" size="22" maxlength="20" onKeyUp="to_upper(this);"/>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			
			<!--
            <tr>
              <td class="etichetta">Movimenti in ritardo</td>
              <td class="campo">
                <af:comboBox classNameBase="input" name="numGgTraMovComunicaz" >
                  <option value="" selected ></option>
                  <option value="S" >Si</option>
                  <option value="N" >No</option>              
                </af:comboBox>                
              </td>
            </tr>
            -->
            <tr>
              <td class="etichetta" nowrap>Data Comunicazione da</td>
              <td class="campo" >
                      <af:textBox type="date" name="datcomunicazioneda" validateOnPost="true" value="" size="10" maxlength="10"/>
                      a&nbsp;&nbsp;<af:textBox type="date" name="datcomunicazionea" validateOnPost="true" value="" size="10" maxlength="10"/>
              </td>            
            </tr>
            <tr>
              <td class="etichetta" nowrap>Data Movimento da</td>
              <td class="campo" >
                      <af:textBox type="date" name="datmovimentoda" validateOnPost="true" value="" size="10" maxlength="10"/>
                      a&nbsp;&nbsp;<af:textBox type="date" name="datmovimentoa" validateOnPost="true" value="" size="10" maxlength="10"/>
              </td>            
            </tr>

            <tr>
              <td class="etichetta" nowrap>Tipo Comunicazione</td>
              <td class="campo" >
                      <af:comboBox classNameBase="input" name="codTipoComunic" moduleName="ComboCodTipoComunic" addBlank="true" />
              </td>      
            </tr>

            <tr>
              <td class="etichetta" nowrap>Tipo Movimento</td>
              <td class="campo" >
                      <af:comboBox classNameBase="input" name="tipoMovimento" moduleName="ComboTipoMovimento" addBlank="true" />
              </td>            
            </tr>
            <!--
            <tr>
              <td class="etichetta" nowrap>Tipo Avviamento</td>
              <td class="campo" >
                      <af:comboBox classNameBase="input" name="tipoAvviamento" moduleName="ComboTipoAvviamento" addBlank="true" multiple="true" />
              </td>            
            </tr>
            -->
            <tr>
              <td class="etichetta">Tipo di Contratto</td>
              <td class="campo" colspan="3">
                  <af:textBox title="Codice del tipo di contratto" value="" classNameBase="input" name="codTipoAss" size="8" maxlength="7" onKeyUp="fieldChanged();"/> 
                  &nbsp;<a href="javascript:cercaTipoContratto('codice');"><img src="../../img/binocolo.gif" alt="Cerca per codice"></a>&nbsp;
                  <af:textBox title="Descrizione del tipo di contratto" value="" classNameBase="input" name="descrTipoAss" size="50" onKeyUp="fieldChanged();"/>&nbsp;
                  <a href="javascript:cercaTipoContratto('descrizione');"><img src="../../img/binocolo.gif" alt="Cerca per descrizione"></a>              
              </td>
            </tr>
 			 <tr>
              <td class="etichetta" nowrap>Mansione a tempo</td>
              <td class="campo" >
                      <af:comboBox classNameBase="input" name="codMonoTempo" moduleName=""  addBlank="true" onChange="javascript:aggiornaTipoContratto('', '', '', '');" >
                        <option value="D">Determinato</option>
                        <option value="I">Indeterminato</option>                
                      </af:comboBox>
              </td>            
            </tr>
            <tr>
              <td class="etichetta" nowrap>Tipo Rapporto</td>
              <td class="campo" >
                      <af:comboBox classNameBase="input" name="tipoRapporto" moduleName="ComboTipiContratto"  addBlank="true" />
              </td>            
            </tr>    
            <tr>
              <td class="etichetta" nowrap>Qualifica</td>
            <td class="campo">
              <af:textBox classNameBase="input" name="CODMANSIONE" size="7" maxlength="7" />
      		  <af:textBox type="hidden" name="codMansioneHid"/>
      		  <a href="javascript:selectMansione_onClick(Frm1.CODMANSIONE, Frm1.codMansioneHid, Frm1.DESCMANSIONE,  Frm1.strTipoMansione);"><img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;&nbsp;
              <A href="javascript:ricercaAvanzataMansioni();">Ricerca avanzata</A>
              <af:textBox type="hidden" name="CODTIPOMANSIONE" value="" />
              <!--<af:textBox type="hidden" name="strTipoMansione" value="" />
              <af:textBox type="hidden" name="DESCMANSIONE" value="" />-->

            </tr>

            <tr id="tipoMans" ><!--style="display: none;" -->
              <TD class="etichetta">
              	Tipo mans.:
              </TD>
              <td class="campo"  >
                <af:textBox classNameBase="input" size="70" type="text" name="strTipoMansione" value="" readonly="true" />
              </td>
              
             </tr>
             <tr id="descrMans" ><!--style="display: none;" -->
              <TD class="etichetta">
              	Mans.:
              </TD>               
              <td class="campo" >
				<af:textBox classNameBase="input" type="text" size="70" name="DESCMANSIONE" value="" readonly="true" />
              </td>
             </tr>
      
             <tr>
                  <td class="etichetta">CCNL</td>
                  <td class="campo" >
                    <af:textBox classNameBase="input" onKeyUp="fieldChanged();PulisciRicercaCCNL(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, document.Frm1.strCCNLHid, 'codice');" type="text" name="codCCNL" value="" size="10" maxlength="9" validateWithFunction="codCCNLUpperCase"  />&nbsp;
                    <A HREF="javascript:btFindCCNL_onclick(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, document.Frm1.strCCNLHid, 'codice');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per codice"/></a>&nbsp;
                    <af:textBox type="hidden" name="codCCNLHid" value="" />
                    <af:textBox type="text" classNameBase="input" onKeyUp="fieldChanged();PulisciRicercaCCNL(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, document.Frm1.strCCNLHid, 'descrizione');" name="strCCNL" value="" size="25" maxlength="50" inline="
                        onkeypress=\"if (event.keyCode==13) { event.keyCode=9; this.blur(); }\""/>&nbsp;
                    <A HREF="javascript:btFindCCNL_onclick(document.Frm1.codCCNL, document.Frm1.codCCNLHid, document.Frm1.strCCNL, document.Frm1.strCCNLHid, 'descrizione');"><IMG name="image" border="0" src="../../img/binocolo.gif" alt="cerca per descrizione"/></a>
                    <af:textBox type="hidden" name="strCCNLHid" value="" />

                    <A href="javascript:ricercaAvanzataCCNL();">Ricerca avanzata</A>
                  </td>
              </tr>
              <tr>
                <td class="etichetta">Agevolazioni</td>
                <td class="campo">
                  <af:comboBox moduleName="ComboTipoAgevolazioni" classNameBase="input" name="codAgevolazione" addBlank="true" />
                </td>
              </tr>
              <tr>
                <td class="etichetta">L.68/99</td>
                <td class="campo">
                  <af:comboBox name="FLGLEGGE68" classNameBase="input">					  
						<option value="T" selected>tutti i movimenti</option>
					    <option value="L">solo movimenti legge 68/99</option>
					    <option value="N">escludi movimenti legge 68/99</option>
				    </af:comboBox>
                </td>
              </tr>
              <!-- Inizio modifica D'Auria Giovanni del 09/05/2005-->
              <tr>
                <td class="etichetta">CPI competente per l'azienda</td>
                <td class="campo">
			      <af:comboBox name="CodCPI" title="Centro per l'Impiego competente per l'azienda" moduleName="ElencoCPI_VALIDAZIONE" addBlank="true" selectedValue="" />
			    </td>
              </tr>
             <!--Fine modifica D'Auria Giovanni del 09/05/2005-->
             <tr><td colspan="2">&nbsp;</td></tr>
             <tr class="note">
             	<td colspan="2">
             	<div class="sezione2"></div>
        		</td>
             </tr>
             <tr>
                <td class="etichetta">CPI competente per il lavoratore</td>
                <td class="campo">
			      <af:comboBox name="CodCPILav" title="Centro per l'Impiego competente per il lavoratore" moduleName="ElencoCPI_VALIDAZIONE" addBlank="true" selectedValue="" />
			    </td>
             </tr>
             <%if (canConsultArchivioLav.equalsIgnoreCase("true")) { %>
			 <tr>
			    <td class="etichetta2">Cerca in</td>
				<td class="campo2">
					<af:comboBox name="CONTEXT" classNameBase="input">					  
						<option value="valida"></option>
					    <option value="validaArchivio">Archivio</option>
					    <option value="valida">Importati</option>
				    </af:comboBox>        
				</td>
			 </tr>              
             <%} else { %>
             	<!--il parametro seguente serve per eseguire la ricerca sulla tabella AM_MOVIMENTO_APPOGGIO invece che sulla AM_MOVIMENTO-->
      			<input type="hidden" name="CONTEXT" value="valida"/> 
             <%}%>  
            </table>
          </div>
        </td>
      </tr>

      <tr><td colspan="2">&nbsp;</td></tr>
      <tr>
        <td colspan="2" align="center">
        <input class="pulsanti" type="submit" name="azione" value="Cerca" title="Cerca i movimenti da validare"/>
        &nbsp;&nbsp;
        <input class="pulsanti" type="reset" name="reset" value="Annulla" title="Cancella i campi della ricerca"/>
        &nbsp;&nbsp;
        <%if (validazioneInCorso) {%> 
        	<input class="pulsanti" type="button" onclick="arrestaValidazione();" name="reset" value="Arresta Validazione" title="Ferma la validazione massiva attualmente in esecuzione"/>        
        <%}%>       
        </td>
      </tr>
      <tr>
        <td colspan="2" align="center">
        <%if (risultatiInSessione) {%>
        <input class="pulsanti" type="button" onclick="visulizzaRisultati();" name="azione" value="Risultati ultima validazione" title="Visualizza i risultati dell'ultima validazione massiva eseguita"/>
        <%}%>
        </td>
      </tr>      
      </table>
      <%out.print(htmlStreamBottom);%>
      <input type="hidden" name="paginaMansione" value=""/>
      <input type="hidden" name="ROOT_PAGE" value="MovRicercaValidazionePage"/>
      <input type="hidden" name="PAGE" value="MovListaValidazionePage"/>
      <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
      <!--il parametro seguente serve per la visualizzazione paginata dei movimenti-->
      <input type="hidden" name="MESSAGE" value="LIST_FIRST"/>
      <input type="hidden" name="LIST_PAGE" value="1"/>
      <!-- di supporto -->
      <input type="hidden" name="CDNLAVORATORE" value=""/>
      <input type="hidden" name="PRGAZIENDA" value=""/>
      <input type="hidden" name="PRGUNITA" value=""/>
      </af:form>       
      <br/>
		</center>  

	</body>
</html>

