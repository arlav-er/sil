<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.PageAttribs,
                  it.eng.afExt.utils.DateUtils,
                  it.eng.sil.security.User,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  java.lang.*,
                  java.text.*
                  "   %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%

    BigDecimal prgAzienda         = null;
    String     CODSTATOATTO         = null;
    String     DATSTIPULA           = null;
    String     DATSCADCONFERMA      = null;
    String     STRNOTE              = null;
    BigDecimal PRGPATTOUNITAAZIENDA      = null;


    String     strCognome           = null;
    String     strNome              = null;
    BigDecimal prgDichDispon        = null;
    String     DATFINE              = null;  
    String     CODMOTIVOFINEATTO    = null;

    BigDecimal PRGSTATOOCCUPAZ      = null;
    String     FLGCOMUNICAZESITI    = null;
    String     FLGPATTO297          = null;
    BigDecimal PRGPATTOLAVORATORE   = null;
    BigDecimal NUMKLOPATTOUNITAAZIENDALE= new BigDecimal(-1.0);

//Testata azienda
SourceBean aziendaRow=null;
String strCodiceFiscale="";
String strPartitaIva="";
String strRagioneSociale="";

    BigDecimal cdnUtIns             = null;
    String     dtmIns               = null;
    BigDecimal cdnUtMod             = null;
    String     dtmMod               = null;

    String     codCPI               = null;
    String     descCPI              = null;
    String     CognIns              = null;
    String     NomIns               = null;
    String     CognMod              = null;
    String     NomMod               = null;
    String     DATDICHIARAZIONE     = null;
    String     datInizio            = null;
    String     descStato            = null;
    String     codStatoOcc          = null;
    String     codTipoPatto  = null;
    boolean    flag_insert   = false;
    boolean    buttonAnnulla = false;
    String display1 = "none";
    String img1 = "../../img/chiuso.gif";
    String hasDataUscita = "false";

InfCorrentiAzienda infCorrentiAzienda= null;


    ////////////////////////////////////
    int cdnFunzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
    String prgAziendaStr = (String) serviceRequest.getAttribute("prgAzienda");
    String prgUnita = (String) serviceRequest.getAttribute("prgUnita");
    String queryString = "PAGE=PattoAziendaPage&prgAzienda="+prgAziendaStr+"&PRGUNITA="+prgUnita+"&CDNFUNZIONE="+cdnFunzione;
    
    InfCorrentiLav testata= null;
    Linguette l = null;
    Testata operatoreInfo=null;
    prgAzienda = new BigDecimal(prgAziendaStr);
  
    SourceBean row = null;
    //se abbiamo nella servicerequest il parametro "inserisci" è stato richiesto l'inserimento
    if (serviceRequest.containsAttribute("inserisci")) { 
        flag_insert   = true;
        buttonAnnulla = true;
        DATSTIPULA = DateUtils.getNow();
    }// fine ("inserisci")
    else { 
        row=(SourceBean) serviceResponse.getAttribute("M_GetPattoAzienda.ROWS.ROW");
        if(row != null){



              prgAzienda        =  (BigDecimal) row.getAttribute("prgAzienda");
              PRGPATTOUNITAAZIENDA        =  (BigDecimal) row.getAttribute("PRGPATTOUNITAAZIENDA");
              
              DATSTIPULA           =  (String)     row.getAttribute("DATSTIPULA");      
              CODSTATOATTO         =  (String)     row.getAttribute("CODSTATOATTO");      
              PRGSTATOOCCUPAZ      =  (BigDecimal) row.getAttribute("PRGSTATOOCCUPAZ");
              FLGCOMUNICAZESITI    =  (String)     row.getAttribute("FLGCOMUNICAZESITI");      
              FLGPATTO297          =  (String)     row.getAttribute("FLGPATTO297");            
              PRGPATTOLAVORATORE   =  (BigDecimal) row.getAttribute("PRGPATTOLAVORATORE");
              DATSCADCONFERMA      =  (String)     row.getAttribute("DATSCADCONFERMA");  
              STRNOTE              =  (String)     row.getAttribute("STRNOTE");
              CODMOTIVOFINEATTO    =  (String)     row.getAttribute("CODMOTIVOFINEATTO");
              DATFINE              =  (String)     row.getAttribute("DATFINE");
              cdnUtIns             =  (BigDecimal) row.getAttribute("CDNUTINS");
              dtmIns               =  (String)     row.getAttribute("DTMINS");
              cdnUtMod             =  (BigDecimal) row.getAttribute("CDNUTMOD");
              dtmMod               =  (String)     row.getAttribute("DTMMOD");
              NUMKLOPATTOUNITAAZIENDALE=  (BigDecimal) row.getAttribute("NUMKLOPATTOUNITAAZIENDALE");
              //codCPI               =  (String)     row.getAttribute("CODCPI");            
              descCPI              =  (String)     row.getAttribute("descCPI");            
              codStatoOcc          =  (String)     row.getAttribute("CODICE");
              CognIns              =  (String)     row.getAttribute("CognIns");            
              NomIns               =  (String)     row.getAttribute("NomIns");            
              CognMod              =  (String)     row.getAttribute("CognMod");                  
              NomMod               =  (String)     row.getAttribute("NomMod");            
              DATDICHIARAZIONE     =  (String)     row.getAttribute("DATDICHIARAZIONE");                  
              datInizio            =  (String)     row.getAttribute("DATINIZIO");                   
              descStato            =  (String)      row.getAttribute("DESCRIZIONESTATO");
              codTipoPatto         =  (String) row.getAttribute("CODTIPOPATTO");              
        }//if(row != null)     
        else { 
        //altrimenti il record è vuoto, non ci sono inf. correnti: possiamo solo inserire
              flag_insert = true;        
              DATSTIPULA = DateUtils.getNow();
              Vector rowCpI  = serviceResponse.getAttributeAsVector("M_GETINFCORRPATTO.ROWS.ROW");
              if(rowCpI != null && !rowCpI.isEmpty()) {
                 row  = (SourceBean) rowCpI.elementAt(0);
                 PRGSTATOOCCUPAZ = (BigDecimal) row.getAttribute("PRGSTATOOCCUPAZ");
                 prgDichDispon =  (BigDecimal) row.getAttribute("PRGDICHDISPONIBILITA");      
                 descStato       = (String)     row.getAttribute("DESCRIZIONESTATO");
                 datInizio       = (String)     row.getAttribute("DATINIZIO");
                 descCPI         = (String)     row.getAttribute("CPITITOLARE");
                 codCPI          = (String)     row.getAttribute("CODCPITIT");
                 DATDICHIARAZIONE= (String)     row.getAttribute("DATDICHIARAZIONE");
              }
        }
    } //else
    /////////////////////  protocollazione     /////////////////////////////////
    Vector rows= serviceResponse.getAttributeAsVector("M_PROTOCOLLATOAZIENDA.ROWS.ROW");
    String numProt = null;
    String annoProt = null;
    if(rows != null && !rows.isEmpty())    { 
        row = (SourceBean) rows.elementAt(0);
        numProt  = SourceBeanUtils.getAttrStrNotNull(row,"NUMPROTOCOLLO");
        BigDecimal annoP = (BigDecimal) row.getAttribute("ANNOPROT");
        annoProt = annoP!=null ? annoP.toString() : "";
    }
    ///////////////////////////////////////////////////////////////////////////////////
    operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
    infCorrentiAzienda= new InfCorrentiAzienda(prgAzienda,prgUnita);
    testata= new InfCorrentiLav(RequestContainer.getRequestContainer().getSessionContainer(), prgAzienda, user);
    String _page = (String) serviceRequest.getAttribute("PAGE"); 
    l = new Linguette(user, cdnFunzione, _page, prgAzienda);   
    l.setCodiceItem("prgUnita=" +prgUnita +  "&prgAzienda");
    
    PageAttribs attributi = new PageAttribs(user, "PattoAziendaPage");
    boolean canModify   = attributi.containsButton("aggiorna");
    boolean canInsert   = attributi.containsButton("INSERISCI");
    boolean canPrint    = attributi.containsButton("STAMPA_ACCORDO");
    boolean infStorButt = attributi.containsButton("INFO_STORICHE");
    String readOnlyString = String.valueOf(!canModify);
 if (canModify || canInsert )
       readOnlyString = "false";
    else    readOnlyString = "true";
   // canModify = true;
     //   canInsert = true;
     //       canPrint = true;
         //       infStorButt = true;
                             //  readOnlyString = "false";
%>

<html>
<head>
<title>Dettaglio patto lavoratore</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
<af:linkScript path="../../js/"/>
<script language="Javascript" src="../../js/docAssocia.js"></script>
<%@ include file="../documenti/_apriGestioneDoc.inc"%>

<SCRIPT language="JavaScript">
<%@ include file="_controlloDate_script.inc"%>
<%@ include file="_sezioneDinamica_script.inc"%>
<!--
function sceglipage(Scelta){
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if (Scelta == 1) {
    // torna indietro senza inserire   
        var urlpage="AdapterHTTP?";
        urlpage+="prgAzienda=<%=prgAziendaStr%>&";
        urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&";
        urlpage+="PAGE=PattoAziendaPage";
        setWindowLocation(urlpage);
    }       
}

function apriInfoStoriche(cdnlav) {
    uripopup="AdapterHTTP?PAGE=PattoInformazioniStorichePage&prgAzienda="+cdnlav;
    window.open (uripopup, "InformazioniStoriche", 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES');
}


function mostra(id){
    var div = document.getElementById(id);
    div.style.display="";
    div.style.visibility="visible";
}

function nascondi(id){
    var div = document.getElementById(id);
    div.style.display="none";
    div.style.visibility="hidden";
}



function selezioneAccordoGenerico() {
    nascondi("infoPatto1");                                                      
    mostra  ("infoPatto2");                                                      
    nascondi("infoPatto3");                                                      
    mostra  ("infoPatto4");                                                      
    nascondi("labelVisulizza");                                                  
    mostra  ("labelNascondi");                                                   
    nascondi("titoloIni");                                                       
    nascondi("titolo297");                                                       
    mostra  ("titoloAcc");
    document.Frm1.datInizio.validateOnPost = "false";                      
    document.Frm1.datDichDisponibilita.validateOnPost = "false";           
    document.Frm1.PRGDICHDISPONIBILITA.value = "";                             
}
function selezioneVuota() {
    nascondi("infoPatto1");                                                      
    nascondi("infoPatto2");                                                      
    nascondi("infoPatto3");                                                      
    nascondi("infoPatto4");                                                      
    nascondi("labelVisulizza");                                                  
    nascondi("labelNascondi");                                                   
    mostra  ("titoloIni");                                                       
    nascondi("titolo297");                                                       
    nascondi("titoloAcc");
}
function selezionePatto297() {
    mostra  ("infoPatto1");                                                        
    mostra  ("infoPatto2");                                                        
    mostra  ("infoPatto3");                                                        
    mostra  ("infoPatto4");                                                        
    mostra  ("labelVisulizza");                                                    
    nascondi("labelNascondi");                                                   
    nascondi("titoloIni");                                                       
    mostra  ("titolo297");                                                         
    nascondi("titoloAcc");                                                       
    document.Frm1.datInizio.validateOnPost = "true";                       
    document.Frm1.datDichDisponibilita.validateOnPost = "true";            
    document.Frm1.PRGDICHDISPONIBILITA.value = document.Frm1.PRGDICHDISP.value;
}

function toDichDisp() {
    var urlpage="AdapterHTTP?";
    urlpage+="fromPattoDettaglio=1&";
    urlpage+="cdnFunzione=<%=cdnFunzione%>&";
    urlpage+="prgAzienda=<%=prgAziendaStr%>&"; 
    urlpage+="pageChiamante=PattoAziendaPage&";
    urlpage+="page=DispoDettaglioPage";
	window.open(urlpage,"DichiarazioneDiponibilita", 'toolbar=0, scrollbars=1, resizable=1,height=600,width=800'); 
}
function underConstr() { alert("Funzionaliltà non ancora attivata."); }

        // Rilevazione Modifiche da parte dell'utente
        var flagChanged = false;
        
        function fieldChanged() {
         <% if (!readOnlyString.equalsIgnoreCase("true")){ %> 
            flagChanged = true;
         <%}%> 
        }
/**
* Controllo della presenza della dichiarazione di immediata disponibilita'
* Se non e' stata firmata la dichiarazione di immediata disponibilita' il patto non puo' essere stipulato
*/
function hasDichiarazioneDisponibilita(){
    try {
        var index = document.Frm1.flgPatto297.options.selectedIndex;                 
        var flagPatto297 = document.Frm1.flgPatto297.options[index];                   
    }catch(er) {
        flagPatto297=document.Frm1.flgPatto297;
    }
    if (document.Frm1.PRGDICHDISPONIBILITA.value=="" && flagPatto297.value=="S" ) {
    // e' stato scelto un patto 150 ma il lavoratore non ha fatto la dichiarazione di immediata disponibilita'
        if (confirm("La di chiarazione di immediata disponibilità non e' stata rilasciata: proseguire con la dichiarazione?")) {
            document.Frm1.flgPatto297.options.selectedIndex=0;
            selezioneVuota();
            toDichDisp();
        }
        else {
            // imposto accordo generico
            document.Frm1.flgPatto297.options.selectedIndex=2;
            selezioneAccordoGenerico();
        }
    }
}
/**
* Controlli generici sulle date ed altro prima dell' invio della form
*/
function controlla() {
    dataDich = document.Frm1.datDichDisponibilita.value;    
    dataStipula = document.Frm1.DATSTIPULA.value;    
    dataScadenza = document.Frm1.DATSCADCONFERMA.value;    
    dataFine = document.Frm1.DATFINE.value;
    var codMotivoFineIndex  =0;
    try {
        selInd = document.Frm1.CODMOTIVOFINEATTO.selectedIndex;
        codMotivoFineIndex = selInd;
    }catch(e) {}
    if (compDate(dataDich, dataStipula)>0) {
        alert(Messages.Date.ERR_DATA_PATTO);
        return false;
    }
    if (compDate(dataStipula, dataScadenza)>0) {
        alert(Messages.Date.ERR_DATA_STIP_SCAD);
        return false;
    }
    if (isFuture(dataStipula)) {
        alert(Messages.Date.ERR_DATA_STIP);
        return false;
    }
    if (isOld(dataStipula) && !confirm(Messages.Date.WARNING_STIPULA)) {        
        return false;
    }
    // Controllo sulla chiusura del patto
    if (dataFine!="" && codMotivoFineIndex==0) {
        alert(Messages.Date.ERR_DATA_MOTIVO);
        return false;
    }
    if (dataFine=="" && codMotivoFineIndex>0) {
        alert(Messages.Date.ERR_DATA_MOTIVO2);
        return false;
    }
    if (dataFine!="" && compDate(dataStipula , dataFine)>0){
        alert(Messages.Date.ERR_DATA_STIP_USCITA);
        return false;
    }
    return true;
}

function apriStampa(RPT,paramPerReport,tipoDoc){ 
  //RPT: attributo name del tag ACTION nel file action.xml relativo al report da visualizzare
  //paramPerReport: parametri necessari a visualizzare il report 
  //tipoDoc: codice relativo al tipo di documento cosi come inserito nel campo CODTIPODOCUMENTO della tab. DE_DOC_TIPO

  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;

  var urlpage="AdapterHTTP?ACTION_NAME="+RPT;
  urlpage+=paramPerReport; //Quelli che nella calsse sono inseriti nel vettore params
  urlpage+="&tipoDoc="+tipoDoc;
  urlpage+="&asAttachment=false";
  if(confirm("Vuoi PROTOCOLARE il file pirma di visualizzarlo?\n\nSeleziona\n- \"OK\"        per PROTOCOLLARE prima di visualizzare la stampa\n- \"Annulla\"  per visualizzare SENZA PROTOCOLLARE"))
  { urlpage+="&salvaDB=true";
  }
  else
  { urlpage+="&salvaDB=false";
  }
    
  setWindowLocation(urlpage);
    
}//apriStampa()
  
-->
</SCRIPT>
<script language="Javascript">
<%
    attributi.showHyperLinks(out, requestContainer, responseContainer,"prgAzienda=" + prgAzienda);    
%>
    window.top.menu.caricaMenuAzienda(<%=cdnFunzione%>, <%=prgAzienda%>, <%=prgUnita%>);
</script>
<style >
<%@ include file="_sezioneDinamica_css.inc"%>
</style>
</head>
<%
String htmlStreamTopInfo = StyleUtils.roundTopTableInfo();
String htmlStreamBottomInfo = StyleUtils.roundBottomTableInfo();

String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>
<body class="gestione" onLoad="rinfresca()">





<font color="red"><af:showErrors/></font>
<font color="green">
 <af:showMessages prefix="M_SavePattoAzienda"/>
 <af:showMessages prefix="M_InsertPattoAzienda"/>
</font>


<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="controlla()">

<br>
<%out.print(htmlStreamTopInfo);%>

<%
    if(infCorrentiAzienda != null) infCorrentiAzienda.show(out); 

%>


<%out.print(htmlStreamBottomInfo);%>
<%
	if (l!=null)l.show(out);
%>
<br>
<%out.print(htmlStreamTop);%>
<table class="main" width="96%">
<tr>
  <td colspan="2">
    <div align="center" style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:lightblue">

    <table cellpadding="0" cellspacing="0" border="0" width="100%"><tr>
    <td class="etichetta">
      Stato&nbsp;Patto
    </td>
    <td><af:comboBox classNameBase="input" name="CODSTATOATTO"  moduleName="M_STATOATTOPATTOAZIENDA" selectedValue="<%=CODSTATOATTO%>"
                     addBlank="true" blankValue="" required="true" title="Stato patto"
                     disabled="<%=readOnlyString%>" onChange="fieldChanged();"/>
    </td>
    <td align="right">anno&nbsp;</td>
    <td><af:textBox onKeyUp="fieldChanged();" classNameBase="input" type="text" name="annoProt" value="<%=annoProt!=null ? annoProt : \"\"%>"
                                 readonly="true" title="Anno protocollo" size="5" maxlength="4"/></td>
    <td align="right"> &nbsp;&nbsp;num.&nbsp;</td>
    <td><af:textBox onKeyUp="fieldChanged();" classNameBase="input" type="text" name="numProt" value="<%=numProt%>"
                                 readonly="true" title="Numero protocollo" size="8" maxlength="100"/></td>
    </tr></table>
  </div></td>
</tr>  
<tr><td><br/></td></tr>

<tr><td><br></td></tr>







  <tr>
    <td class="etichetta">Data stipula &nbsp;</td>
    <td>
      <table cellpadding="0" cellspacing="0" border="0" frame="box" width="100%">
        <tr><td width="25%">
            <af:textBox name="DATSTIPULA" value="<%=it.eng.sil.util.Utils.notNull(DATSTIPULA)%>" classNameBase="input" type="date"
                        readonly="<%=readOnlyString%>" onKeyUp="fieldChanged();"  validateOnPost="true"
                        required="true" title="Data stipula" 
                        size="12" maxlength="10"/>
            </td>
            <%--
            <td>Stato&nbsp;patto
            <af:comboBox classNameBase="combobox" name="CODSTATOATTO"  moduleName="M_STATOATTOPATTO" selectedValue="<%=CODSTATOATTO%>"
                         addBlank="true" blankValue="" required="true" title="Stato patto"
                         disabled="<%=readOnlyString%>" onChange="fieldChanged();"/>
            </td>--%>
        </tr>
      </table>
    </td>    
</tr>
  <tr>
    <td class="etichetta">Data scadenza conferma &nbsp;</td>
    <td class="campo">
      <af:textBox name="DATSCADCONFERMA" value="<%=it.eng.sil.util.Utils.notNull(DATSCADCONFERMA)%>" type="date"
                  readonly="<%=readOnlyString%>" onKeyUp="fieldChanged();" classNameBase="input"  validateOnPost="true"                  
                  required="true" title="Data scadenza conferma" size="12" maxlength="10"/>
    </td>
  </tr>
  <!--tr >
    <td class="etichetta"><br/>Impegno di comuncazione esiti</td><td></td>
  </tr-->
  <tr><td><br></td></tr>
<tr>
    <td colspan="4">
        <table class='sezione2' cellspacing=0 cellpadding=0>
            <tr>
                <td  width=18>
                    <img id='IMG1' src='<%=img1%>' onclick='cambia(this, document.getElementById("TBL1"))'></td>
                        <td  class='titolo_sezione'>Chiusura patto</td>    				
                <td align='right' width='30'></td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td colspan=4 align="center">
        <TABLE id='TBL1' style='width:100%;display:<%=display1%>'>     
        <script>initSezioni(new Sezione(document.getElementById('TBL1'),document.getElementById('IMG1'),<%=hasDataUscita%>));</script>
			<td class="etichetta">
                Data fine patto &nbsp;
            </td>
            <td class="campo">
                <af:textBox  name="DATFINE" value="<%=it.eng.sil.util.Utils.notNull(DATFINE)%>" classNameBase="input" type="date" 
                readonly="<%=readOnlyString%>" onKeyUp="fieldChanged();" validateOnPost="true"   size="12" maxlength="10"/>
            </td>
			<tr>
			    <td class="etichetta">Motivo fine patto&nbsp;</td>
                <td class="campo">
                    <af:comboBox name="CODMOTIVOFINEATTO" moduleName="M_MOTFINEATTOPATTOAZIENDA" selectedValue="<%=CODMOTIVOFINEATTO%>" 
                          disabled="<%=readOnlyString%>" onChange="fieldChanged();" classNameBase="input" addBlank="true"/>
                </td>
			</tr>
        </table>
    </td>
</tr>

<tr><td><br></td></tr>



  
  <tr>
    <td class="etichetta">
      Note&nbsp;
    </td>
    <td class="campo">
      <af:textArea name="strNote" value="<%=it.eng.sil.util.Utils.notNull(STRNOTE)%>"
                   cols="60" rows="4" maxlength="100" classNameBase="textarea" 
                   readonly="<%=readOnlyString%>" onKeyUp="fieldChanged();"/>
    </td>
  </tr>


  <%if (!flag_insert) {%>
  <tr>
  <td>
    <input type="hidden" name="NUMKLOPATTOUNITAAZIENDALE2" value="<%=NUMKLOPATTOUNITAAZIENDALE%>" size="10" maxlength="10"  >
    <%NUMKLOPATTOUNITAAZIENDALE= NUMKLOPATTOUNITAAZIENDALE.add(new BigDecimal(1)); %>
    <input type="hidden" name="NUMKLOPATTOUNITAAZIENDALE" value="<%=NUMKLOPATTOUNITAAZIENDALE%>">
    <input type="hidden" name="PRGPATTOUNITAAZIENDA" value="<%=PRGPATTOUNITAAZIENDA%>">
  </td>        
  </tr>
<%}%>
</table>
<BR><center><% operatoreInfo.showHTML(out);%></center>

<BR><% String NomePagina = "";%>

<table class="main">

<% if (!flag_insert) { %>
<tr>
   <td width="33%" align="center">
      <input class="pulsante" type="button" name="Documenti associati" value="Documenti associati"
             onClick="docAssociati('<%=prgAzienda%>','<%=_page%>','<%=cdnFunzione%>')">
   </td>
  <td align="center"><%if(canModify){%><input type="submit" name="Salva" class="pulsanti" value="Aggiorna"><%}%></td>
  <td align="right">
    <%--if(canPrint){
        if(CODSTATOATTO.equalsIgnoreCase("PR") ) {%>
          <input name="Invia" type="button" class="pulsanti" value="Documenti associati" onclick="underConstr()" >
        <%} else {%>
          <input name="Invia" type="button" class="pulsanti" value="Stampa patto"
                 onclick="apriGestioneDoc('Patto/Accordo','RPT_PATTO','&prgAzienda=<%=prgAziendaStr%>','PT297')">        
        <%} //else 
      }--%>
    <input name="Invia" type="button" class="pulsanti" value="Stampa patto"
           onclick="apriGestioneDoc('RPT_PATTO','&prgAzienda=<%=prgAziendaStr%>&pagina=<%=_page%>','PT297')">        
  </td>
</tr>
<tr><td><br/></td></tr>
<tr>
  <td width="33%"></td>
  <td align="center" width="33%">
    <%if(canInsert){%><input type="submit" class="pulsanti" name="Inserisci" value="Inserisci nuovo"><%}%>
  </td>
  <td align="right" width="33%">
    <%if(infStorButt){%>
      <input type="button" name="infSotriche" class="pulsanti" value="Informazioni storiche" onClick="apriInfoStoriche('<%= prgAzienda %>')">
    <%}%>
  </td>
</tr>
<%} else {%>
<!--tr>
  <td></td>controllaPatto()
  <td align="center"><!-- <input type="submit" name="Salva" value="Aggiorna"></td> -->
  <!--td align="right"><!--<input type="submit" name="Cancella" value="cancella il record">-->
    <!--input name="Invia" type="button" class="pulsanti" value="Stampa patto" onclick="sceglipage(2,null)">      
  </td>
</tr-->
<tr><td><br/></td></tr>
<tr>
   <td width="33%" align="center">
      <input class="pulsante" type="button" name="Documenti associati" value="Documenti associati"
             onClick="docAssociati('<%=prgAzienda%>','<%=_page%>','<%=cdnFunzione%>')">
   </td>
   <%if (canInsert){%><td width="33%" align="center"><input type="submit" class="pulsanti" name="insert_pattoazienda" value="Inserisci"></td><%}%>
   <td width="33%" align="right">
     <%if(buttonAnnulla)
     {%><input class="pulsanti" type="button" name="annulla" value="Chiudi senza inserire" onclick="sceglipage(1);"><%}%>
   </td>
</tr>
<tr><td>&nbsp;</td></tr>
<tr>
  <td colspan="3" align="right">
    <%if(infStorButt){%>
      <input type="button" name="infSotriche" class="pulsanti" value="Informazioni storiche" onClick="apriInfoStoriche('<%= prgAzienda %>')">
    <%}%>
  </td>
</tr>
<%} //else%>
</table>
</center>
<input type="hidden" name="PAGE" value="PattoAziendaPage">
<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">

<%--if (PRGPATTOLAVORATORE==null) PRGPATTOLAVORATORE=(BigDecimal)sessionContainer.getAttribute("PRGPATTOLAVORATORE");--%>

<input  type="hidden" name="PRGPATTOLAVORATORE" value="<%=Utils.notNull(PRGPATTOLAVORATORE)%>" size="22" maxlength="16">
<input  type="hidden" name="prgAzienda" value="<%=Utils.notNull(prgAzienda)%>" size="22" maxlength="16">

<!-- variabile di appoggio usata per memorizzare il progresivo della dichiarazione di disp. utilizzata poi nello script showIfSetVar()-->
<input  type="hidden" name="PRGDICHDISP" value="<%=Utils.notNull(prgDichDispon)%>">

<input type="hidden" name="PRGDICHDISPONIBILITA" value="<%=Utils.notNull(prgDichDispon)%>"/>
<input type="hidden" name="REPORT" value="patto/patto.rpt">
<input type="hidden" name="PROMPT0" value="<%= prgAzienda %>">

<%-- sessionContainer.setAttribute("PRGPATTOLAVORATORE",PRGPATTOLAVORATORE); --%>
<%out.print(htmlStreamBottom);%>
</af:form>

</body>
</html>
