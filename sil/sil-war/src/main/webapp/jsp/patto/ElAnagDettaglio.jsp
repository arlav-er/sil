<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  it.eng.sil.security.User,
                  it.eng.sil.security.ProfileDataFilter,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  java.lang.*,
                  java.text.*
                  "   %>
    
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String cdnLavoratore= (String )serviceRequest.getAttribute("CDNLAVORATORE");
	String _page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
  	PageAttribs attributi = new PageAttribs(user, _page);
  	//data uscita elenco anagrafico può essere solo la data odierna
  	String dataUscita = "";

	boolean infStorButt=false;
	boolean canInsert = false;
	boolean rdOnly=true;
	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
      infStorButt = attributi.containsButton("STORICO");
    	canInsert = attributi.containsButton("INSERISCI");
    	rdOnly     = !attributi.containsButton("AGGIORNA");
    	if((!canInsert) && (rdOnly)){
    		//canInsert=false;
        //rdOnly=true;
    	}else{
        boolean canEdit=filter.canEditLavoratore();
        if (canInsert){
          canInsert=canEdit;
        }
        if (!rdOnly){
          rdOnly=!canEdit;
        }        
    	}
  }
%>

<%
    //flag booleano che discrimina l'inserimento o la modifica
    boolean flag_insert=false;

    //inizializzo i campi  String cdnLavoratore = null; 
    //String cdnLavoratore = null;
    String strCognome= null;
    String strNome= null;  
    String prgElencoAnagrafico = null;
    String datInizio = null;
    String STRNOTE = null;
    String dtmcan = null;
    String cdntipocan = null; 
    String cdnUtIns = null;
    String dtmIns = null;
    String cdnUtMod = null;
    String dtmMod = null;
    String NUMKLOELENCOANAG = null;

    String CognIns = null;
    String NomIns  = null;
    String CognMod = null;
    String NomMod  = null;
    String codCPI  = null;    
    String descCPI = null;
    String codmonotipocpi = null;
    String codCpiUser = null;   
    String PRGINSERTCOLL  = null;
    String dataInizioPrivacy = null;
    String flgAutoriz = null;
    BigDecimal prgPrivacy = null;
    Testata operatoreInfo = null;
    String hasPrivacy = "false";
    String display0 = "none";
    String display1 = "none";
    String img0 = "../../img/chiuso.gif";
    String img1 = "../../img/chiuso.gif";
    String hasDataUscita = "false";
    String numKloPrivacy = null;
    boolean isHistory = false;
    boolean hasStorElAnag = serviceResponse.containsAttribute("M_HasStorElAnag.ROWS.ROW");
    
  //se è stato richiesto l'inserimento abbiamo nella servicerequest
  //il parametro "inserisci" che corrisponde al pulsante premuto
    if (serviceRequest.containsAttribute("inserisci")){
        flag_insert=true;
    } else {
        flag_insert=false;
    }
    
    Vector rowCpI  = serviceResponse.getAttributeAsVector("M_GETCPICORR.ROWS.ROW");
     
    //cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
     
    SourceBean row = (SourceBean) serviceResponse.getAttribute("M_GETELANAG.ROWS.ROW");
    SourceBean elanagrow = row;
    // Autorizzazione al trattamento dei dati personali: puo' accadere che non si conosca, ovvero che il record non esista o
    // ci sia ma sia stato chiuso (datfine is null)
    /*
    SourceBean autPrivacy = (SourceBean)serviceResponse.getAttribute("M_AUTPRIVACY.ROWS.ROW");

    if (autPrivacy!=null) {
        flgAutoriz = Utils.notNull(autPrivacy.getAttribute("flgAutoriz"));
        dataInizioPrivacy = Utils.notNull(autPrivacy.getAttribute("datInizio"));
        prgPrivacy = (BigDecimal)autPrivacy.getAttribute("PRGPRIVACY");
        BigDecimal klo = (BigDecimal)autPrivacy.getAttribute("NUMKLOPRIVACY");
        numKloPrivacy = String.valueOf(klo.intValue()+1);
        hasPrivacy = "true";
        display0= "inline";
        img0 = "../../img/aperto.gif";
    }
    */
      if (row == null)
      { flag_insert=true;
      }

      if(!flag_insert){
      //if (row.containsAttribute("cdnLavoratore")) {cdnLavoratore= row.getAttribute("cdnLavoratore").toString();}
      //if (row.containsAttribute("strCognome")) {strCognome=row.getAttribute("strCognome").toString();}
      //if (row.containsAttribute("strNome")) {strNome=row.getAttribute("strNome").toString();}
      if (row.containsAttribute("prgElencoAnagrafico")) {prgElencoAnagrafico=row.getAttribute("prgElencoAnagrafico").toString();}
      if (row.containsAttribute("datInizio")) {datInizio=row.getAttribute("datInizio").toString();}      
      if (row.containsAttribute("STRNOTE")) {STRNOTE=row.getAttribute("STRNOTE").toString();}
      if (row.containsAttribute("dtmcan")) {dtmcan=row.getAttribute("dtmcan").toString();}

      if (row.containsAttribute("CODTIPOCAN")) {cdntipocan=row.getAttribute("CODTIPOCAN").toString();} 
      
      if (row.containsAttribute("CDNUTINS")) {cdnUtIns=row.getAttribute("CDNUTINS").toString();}
      if (row.containsAttribute("DTMINS")) {dtmIns=row.getAttribute("DTMINS").toString();}
      if (row.containsAttribute("CDNUTMOD")) {cdnUtMod=row.getAttribute("CDNUTMOD").toString();}
      if (row.containsAttribute("DTMMOD")) {dtmMod=row.getAttribute("DTMMOD").toString();}
      if (row.containsAttribute("NUMKLOELENCOANAG")) {NUMKLOELENCOANAG=row.getAttribute("NUMKLOELENCOANAG").toString();}

      if (row.containsAttribute("CognIns")) {CognIns=row.getAttribute("CognIns").toString();}            
      if (row.containsAttribute("NomIns")) {NomIns=row.getAttribute("NomIns").toString();}            
      if (row.containsAttribute("CognMod")) {CognMod=row.getAttribute("CognMod").toString();}                  
      if (row.containsAttribute("NomMod")) {NomMod=row.getAttribute("NomMod").toString();}            
      if (row.containsAttribute("codCPI")) {codCPI=row.getAttribute("codCpi").toString();}            
      if (row.containsAttribute("descCPI")) {descCPI=row.getAttribute("descCPI").toString();}            
      if (row.containsAttribute("PRGINSERTCOLL")) {PRGINSERTCOLL=row.getAttribute("PRGINSERTCOLL").toString();}
      }//if(flag_insert != true)
      else {
       if(rowCpI != null && !rowCpI.isEmpty()) {
         //datInizio = DateUtils.getNow();
         row     = (SourceBean) rowCpI.elementAt(0);
         codCPI  = (String) row.getAttribute("CODCPITIT");
         descCPI = (String) row.getAttribute("CPItitolare");
         codmonotipocpi = Utils.notNull(row.getAttribute("CODMONOTIPOCPI"));
         codCpiUser = Utils.notNull(user.getCodRif());
         if (!codmonotipocpi.equals("C") || !codCPI.equals(codCpiUser)){
			flag_insert = false;
         }
       }//if
      }//else

    
    operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);

/*   PageAttribs attributi = new PageAttribs(user, "ElAnagDettaglioPage");
   boolean rdOnly     = !attributi.containsButton("AGGIORNA");
   boolean canInsert  =  attributi.containsButton("INSERISCI");
   boolean infStorButt = attributi.containsButton("STORICO");
*/

   //  if (sessionContainer.getAttribute("Refresh")== ("Da effettuare")){
   boolean mostraMessaggioStoricizzazione= false;
   if (serviceRequest.containsAttribute("Salva") && elanagrow ==null) 
        mostraMessaggioStoricizzazione = true;
   String cdnFunzione = (String)serviceRequest.getAttribute("cdnFunzione");
   String htmlStreamTop = StyleUtils.roundTopTable(canInsert);
   String htmlStreamBottom = StyleUtils.roundBottomTable(canInsert);
   %>
<%--
///////////// dati di test ///////////////////

flgAutoriz="N";
dataInizioPrivacy = "21/02/2004";
--%>

<%
// TESTER X POPUP EVIDENZE
String strApriEv = StringUtils.getAttributeStrNotNull(serviceRequest, "APRI_EV");
int _fun = 1;
if((cdnFunzione!=null) && !cdnFunzione.equals("")) { _fun = Integer.parseInt(cdnFunzione); }
EvidenzePopUp jsEvid = null;
boolean bevid = attributi.containsButton("EVIDENZE");
if(strApriEv.equals("1") && bevid) {
	jsEvid = new EvidenzePopUp(cdnLavoratore, _fun, user.getCdnGruppo(), user.getCdnProfilo());
}	
%> 
<html>
<head>
<title>Dettaglio dell'elenco anagrafico</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>

<af:linkScript path="../../js/"/>
<%if(strApriEv.equals("1") && bevid) { jsEvid.show(out); }%>

<SCRIPT TYPE="text/javascript">
<%@ include file="_controlloDate_script.inc"%>
<!--
function sceglipage(Scelta){
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

	   if (Scelta == 1) {
        document.Frm1.PAGE.value="AnagDettaglioPageAnag";   
        doFormSubmit(document.Frm1);    
     }
}

function btFindComune_onclick(codcomune, codcomunehid, nomecomune, nomecomunehid) {	
		if (nomecomune.value == ""){
			nomecomunehid.value = "";
			codcomunehid.value = "";
			codcomune.value = "";
			}
		else if (nomecomune != nomecomunehid) 
			window.open ("AdapterHTTP?PAGE=RicercaComunePage&strdenominazione="+nomecomune.value.toUpperCase()+"&retcod="+codcomune.name+"&retnome="+nomecomune.name);
    
}


function btFindCittadinanza_onclick(codcittadinanza, codcittadinanzahid, cittadinanza, cittadinanzahid, nazione, nazionehid) {
	
		if (nazione.value == ""){
			nazionehid.value = "";
			codcittadinanzahid.value = "";
			codcittadinanza.value = "";
 			cittadinanzahid.value = "";
			cittadinanza.value = "";
			} else
		if (nazione != nazionehid) 
			window.open ("AdapterHTTP?PAGE=RicercaCittadinanzaPage&nazione="+nazione.value.toUpperCase()+"&retcod="+codcittadinanza.name+"&retcittadinanza="+cittadinanza.name+"&retnazione="+nazione.name);
    
}

function apriPopUpStoriche (cdnlav) {
    urlPopUp="AdapterHTTP?PAGE=AnagLavInformazioniStorichePage&cdnLavoratore="+cdnlav;
    window.open (urlPopUp, "InformazioniStoriche", 'toolbar=NO,statusbar=YES,height=400,width=800,scrollbars=YES,resizable=YES');
}

function toUsDate(newDate) {
    var tokens = newDate.split('/');
    var usDate= tokens[2]+"/"+tokens[1]+"/"+tokens[0];
    return new Date(usDate);
}
var flagInsert = <%=flag_insert%>;
var controllaInModifica = flagInsert;

function controlla() {
    var dataIns = document.Frm1.datInizio.value;
    /****************var dataPrivacy = document.Frm1.datPrivacy.value;**********************/
    var dataCanc = document.Frm1.dtmcan.value;
    /************var autorizzazione = document.Frm1.flgAutoriz.value;*******/
    var motivoUscitaIndex = 0;
    var motivoUscita = "";
    try {
        selInd = document.Frm1.cdntipocan.selectedIndex;
        motivoIscitaIndex = selInd;
        motivoUscita  = document.Frm1.cdntipocan.options[selInd].value;
    }catch(e){ }
    if (controllaInModifica  && isFuture(dataIns)) {
        alert(Messages.Date.ERR_INS);
        return false;
    }   
    if (controllaInModifica  && isOld(dataIns)) {
        if(!confirm(Messages.Date.WARNING_INS_PREC))
        return false;
    }
    /************************
    if (isFuture(dataPrivacy)){
        alert(Messages.Date.ERR_PRIVACY);
        return false;
    }    
    ****************************/
    if (isFuture(dataCanc)) {
        alert(Messages.Date.ERR_USCITA);
        return false;
    }
    if (dataCanc!="" && compDate(dataIns, dataCanc)>0) {
        alert(Messages.Date.ERR_CANC_INS);
        return false;
    }
    if (dataCanc=="" && motivoIscitaIndex>0) {
        alert(Messages.Date.ERR_DATA_MOTIVO3);
        return false;
    }
    if (dataCanc!="" && motivoIscitaIndex==0) {
        alert(Messages.Date.ERR_DATA_MOTIVO4);
        return false;
    }
    if (flagInsert) {
        /************************
        if (dataPrivacy=="") {
            if (confirm(Messages.Date.WARNING_AUTH_PRIVACY)) {
                // nuovo record  S
                document.Frm1.PRIVACY_OP.value = "NEW_S";
            }
            else {
                // nuovo record N
                document.Frm1.PRIVACY_OP.value = "NEW_N";                
            }            
        }
        if (dataPrivacy!="" && dataPrivacy!=dataIns && autorizzazione!="S") {
            if (confirm (Messages.Date.WARNING_AUTH_PRIVACY)) {
                // nuovo record S
                document.Frm1.PRIVACY_OP.value = "NEW_S";
            }
            else document.Frm1.PRIVACY_OP.value = "NO_OP";
        }
        if (dataPrivacy==dataIns && autorizzazione!="S") {
            if (confirm (Messages.Date.WARNING_AUTH_PRIVACY)) {
                // aggiorna a  S
                document.Frm1.PRIVACY_OP.value = "UPD_S";
            }            
            else document.Frm1.PRIVACY_OP.value = "NO_OP";
        }
        ***********************/
    }
    else { // update ovvero chiusura del record
        if (dataCanc=="") {
            // l'update ha senso soltanto per la chiusura del record
            if (confirm("Vuoi inserire la data di uscita dall'elenco anagrafico?"))
                return false;
            else {
                document.Frm1.PRIVACY_OP.value="NO_OP";
                return true;
            }
        }
        /*************************
        if (dataPrivacy=="") {
                // nuovo record  N
            if (confirm(Messages.Date.WARNING_NO_AUTH_PRIVACY)) {
                document.Frm1.PRIVACY_OP.value = "NEW_N";
            }
            else document.Frm1.PRIVACY_OP.value = "NO_OP";
        }
        if (dataPrivacy!="" && dataPrivacy!=dataIns ) {
            if (autorizzazione=="S" || autorizzazione=="") {
                if (confirm (Messages.Date.WARNING_AUTH_PRIVACY)) {
                    // nuovo record S
                    document.Frm1.PRIVACY_OP.value = "NEW_S";
                }
                else {
                    // nuovo record N
                    document.Frm1.PRIVACY_OP.value = "NEW_N";                
                }
            }            
        }
        if (dataPrivacy!="" && dataPrivacy==dataIns && autorizzazione!="N") {
            if (confirm(Messages.Date.WARNING_NO_AUTH_PRIVACY))
                document.Frm1.PRIVACY_OP.value = "NEW_N";      
            else 
                document.Frm1.PRIVACY_OP.value = "NO_OP";      
        }
        ****************************/
    }
            
    // controllo data di cancellazione dall'elenco anagrafico e motivo di cancellazione
    // la data di cancellazione deve essere >= della data inizio            
    if (motivoUscita=="PU" && document.Frm1.strNote.value==""){
        if (!confirm("Attenzione: uscita per 'Provvedimento di ufficio' ma non e' specificato nelle note qual e' il provvedimento. \nContinuare?"))
        return false;
    }
    return true;
}

// date in formato 'dd/mm/yyyy'
function controllaDateFuture(dateText) {
    if (dateText==null || dateText=="") return true;
    sysdate = new Date();
    return toUsDate(dateText).getTime()<=sysdate.getTime();
}
//-->

</SCRIPT>
<SCRIPT>
<!--
var sezioni = new Array();

function cambia(immagine, sezione) {
	if (sezione.aperta==null) sezione.aperta=true;
	if (sezione.aperta) {
		sezione.style.display="none";
		sezione.aperta=false;
		immagine.src="../../img/chiuso.gif";
	}
	else {
		sezione.style.display="inline";
		sezione.aperta=true;
		immagine.src="../../img/aperto.gif";
	}
}
function Sezione(sezione, img,aperta){    
    this.sezione=sezione;
    this.sezione.aperta=aperta;
    this.img=img;
}

function initSezioni(sezione){
	sezioni.push(sezione);
}
//-->
</SCRIPT>
<script language="Javascript">
  <%
       //Genera il Javascript che si occuperà di inserire i links nel footer
       attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore);
  %>
</script>
   <script language="JavaScript">
        // Rilevazione Modifiche da parte dell'utente
        var flagChanged = false;
        
        function fieldChanged() {
         <% if (!rdOnly){ %> 
            flagChanged = true;
         <%}%> 
        }
    </script>
<style>
table.sezione2 {
	border-collapse:collapse;
	font-family: Verdana,"Times New Roman", Arial, Sans-serif; 
	font-size: 12px;
	font-weight: bold;
	border-bottom-width: 2px; 
	border-bottom-style: solid;
	border-bottom-color: #000080;
	color:#000080; 
	position: relative;
	width: 94%;
	left: 4%;
	text-align: left;
	text-decoration: none;	
}
</STYLE>
</head>

<body  class="gestione" 
	   onload="
	   <%if(strApriEv.equals("1") && bevid) { %>
	   apriEvidenze();
	   <%}%>
	   rinfresca()">

    <script language="Javascript">   
       window.top.menu.caricaMenuLav(<%=cdnFunzione%>,<%=cdnLavoratore%>);
    </script>
   
   <%// }
   InfCorrentiLav testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
   testata.setPaginaLista("ListaElAnagPage");
   testata.show(out);

%>



<font color="red"><af:showErrors/></font>
<font color="green">
 <af:showMessages prefix="M_SaveElAnag"/>
 <af:showMessages prefix="M_InsertElAnag"/>
</font>

 
<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="controlla()">
<p class="titolo">Elenco anagrafico</p>
<%out.print(htmlStreamTop);%>
<%
if (!flag_insert && !rdOnly) {
	dataUscita = DateUtils.getNow();
}
%>
<%@ include file="ElAnagCampiLayOut.inc"%>

<%--if (mostraMessaggioStoricizzazione) {%>
<tr><td colspan=5><br></td></tr>
<tr><td colspan=5 align= center><b>L'informazione verrà storicizzata</b><td></tr>
<%}--%>

<BR><center><% operatoreInfo.showHTML(out); %></center>

<BR>
<table class="main">
<% if (!flag_insert) { %>
<tr>
  <td width="33%"></td>
  <td align="center" width="33%">
    <%if(!rdOnly){%>
      <input class="pulsante" type="submit" name="Salva" value="Aggiorna" class="pulsanti">
      <!-- Non si è ancora deciso se questi pulsanti saranno presenti o meno in questa pagina
           in caso affermativo la loro sistemazione è da rivedere.
           <input class="pulsante" type="submit" name="Inserisci" value="Inserisci nuovo">
           <input class="pulsante" type="submit" name="Cancella" value="cancella il record">
      -->
    <%}%>
  </td>
  <td align="right" width="33%">
    <%if(infStorButt){%>
	  <input class="pulsante<%=((hasStorElAnag)?"":"Disabled")%>" type="button" name="infSotriche" class="pulsanti" value="Informazioni storiche" onClick="apriPopUpStoriche('<%= cdnLavoratore %>')" <%=(!hasStorElAnag)?"disabled=\"True\"":""%>>      
    <%}%>
  </td>
</tr>
<%} else if(canInsert){%>
<tr>
   <td width="33%" align="center"></td>
   <td width="33%" align="center">
     <input class="pulsante" type="submit" class="pulsanti" name="insert_elanag" value="Inserisci">
   </td>
   <td width="33%" align="center">
    <%if(infStorButt){%>
	  <input class="pulsante<%=((hasStorElAnag)?"":"Disabled")%>" type="button" name="infSotriche" class="pulsanti" value="Informazioni storiche" onClick="apriPopUpStoriche('<%= cdnLavoratore %>')" <%=(!hasStorElAnag)?"disabled=\"True\"":""%>>
    <%}%>
   </td>
   
   <%--   
     <td width="33%" align="center">
     /**
     *  per ora non permetto all'utente di ritornare alla pagina precedente dato che bisognerebbe tornare
     *  alla pagina di un'altra funzione di cui non conosco il codice
     */
        <input class="pulsanti" type="button" name="annulla" value="Chiudi senza inserire" onclick="sceglipage(1);">      
     </td>
   --%>
   
</tr>
<%}%>
</table>
<%out.print(htmlStreamBottom);%> 
<%--
   /**
   *    serve per permettere alla pagina di anagrafica-scheda lavoratore di creare la linuetta superiore
   */

<input type="hidden" name="CDNFUNZIONE" value="<%= (String) serviceRequest.getAttribute("CDNFUNZIONE") %>">
<%--<input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>">

--%>
    <input type="hidden" name="PAGE" value="ElAnagDettaglioPage">
    <input type="hidden" name="cdnUtIns" value="<%=Utils.notNull(cdnUtIns)%>"/>
    <input type="hidden" name="dtmIns" value="<%=Utils.notNull(dtmIns)%>"/>
    <input type="hidden" name="cdnUtMod" value="<%=Utils.notNull(cdnUtMod)%>"/>
    <input type="hidden" name="dtmMod" value="<%=Utils.notNull(dtmMod)%>"/>
    <%int kLock = NUMKLOELENCOANAG==null ? -1:Integer.parseInt(NUMKLOELENCOANAG); kLock++;%>
    <input type="hidden" name="NUMKLOELENCOANAG" value="<%=kLock%>"/>
    <input type="hidden" name="PRIVACY_OP" value="NO_OP"/>
    <input type="hidden" name="cdnLavoratore" value="<%=Utils.notNull(cdnLavoratore)%>">
    <input type="hidden" name="cdnFunzione" value="<%=Utils.notNull(cdnFunzione)%>">
    <input type="hidden" name="prgElencoAnagrafico" value="<%=Utils.notNull(prgElencoAnagrafico)%>">
</af:form>

</body>
</html>
