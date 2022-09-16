<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  com.engiweb.framework.security.*,
                  it.eng.sil.security.PageAttribs,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  it.eng.sil.security.User,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  java.lang.*,
                  java.text.*"%>
      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>



<%//flag booleano che discrimina l'inserimento o la modifica
    boolean flag_insert=false;

    //inizializzo i campi
    String cdnLavoratore = null;
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
    String NomIns = null;
    String CognMod = null;
    String NomMod = null;
    String codCPI = null;    
    String descCPI = null;   
    String PRGINSERTCOLL = null;
    String flgAutoriz = null;
    BigDecimal prgPrivacy = null;
    String dataInizioPrivacy = null;
    Testata operatoreInfo = null;
    boolean isHistory = true;
    boolean rdOnly    = false;
    boolean canInsert = false;
    // 
    String hasPrivacy = "false";
    String hasDataUscita = "true";
    String display0 = "none";
    String img0 = "../../img/chiuso.gif";
    String display1 = "inline";
    String img1 = "../../img/aperto.gif";
    
    String dataUscita = "";
%>
<%  // Vector rowCpI  = serviceResponse.getAttributeAsVector("M_GETCPICORR.ROWS.ROW");
     
    cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
     
    
    SourceBean autPrivacy = (SourceBean) serviceResponse.getAttribute("M_AUTPRIVACY.ROWS.ROW");
    if (autPrivacy!=null) {
        flgAutoriz = Utils.notNull(autPrivacy.getAttribute("flgAutoriz"));
        dataInizioPrivacy = Utils.notNull(autPrivacy.getAttribute("datInizio"));
        prgPrivacy = (BigDecimal)autPrivacy.getAttribute("PRGPRIVACY");
        hasPrivacy = "true";
        display0= "inline";
        img0 = "../../img/aperto.gif";
    }
    SourceBean row = (SourceBean) serviceResponse.getAttribute("M_ELANAGDETTAGLIOSTORICO.ROWS.ROW");
    if (row == null) flag_insert=true;
    if(flag_insert != true){
    if (row.containsAttribute("cdnLavoratore")) {cdnLavoratore= row.getAttribute("cdnLavoratore").toString();}
    if (row.containsAttribute("strCognome")) {strCognome=row.getAttribute("strCognome").toString();}
    if (row.containsAttribute("strNome")) {strNome=row.getAttribute("strNome").toString();}
    if (row.containsAttribute("prgElencoAnagrafico")) {prgElencoAnagrafico=row.getAttribute("prgElencoAnagrafico").toString();}
    if (row.containsAttribute("datInizio")) {datInizio=row.getAttribute("datInizio").toString();}      
    if (row.containsAttribute("STRNOTE")) {STRNOTE=row.getAttribute("STRNOTE").toString();}
    if (row.containsAttribute("dtmcan")) {
    	dtmcan=row.getAttribute("dtmcan").toString();
    	dataUscita = dtmcan;
    }

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
    
    operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
    //Setto gli attributi della pagina (pulsanti e link)
    PageAttribs attributi = new PageAttribs(user, "ElAnagDettaglioPage");
    rdOnly    = true; //!attributi.containsButton("AGGIORNA");
    canInsert =  false; //attributi.containsButton("INSERISCI");

//}

%>
<html>
<head>
<title>Dettaglio dell'elenco anagrafico</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>

<af:linkScript path="../../js/"/>

<SCRIPT TYPE="text/javascript">
<!--
function sceglipage(Scelta){
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

	   if (Scelta == 1) {
        document.Frm1.PAGE.value="AnagLavInformazioniStorichePage";   
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
   <script language="JavaScript">
        // Rilevazione Modifiche da parte dell'utente
        var flagChanged = false;
        
        function fieldChanged() {
         <% if (rdOnly){ %> 
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
<body  class="gestione">
   
<%
   InfCorrentiLav testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
   testata.setSkipLista(true);
   testata.show(out);
%>


<!-- <%@ include file="InfLavoratore.inc" %> -->
<font color="red">
   <af:showErrors/>
</font>

<font color="green">
 <af:showMessages prefix="M_SaveElAnag"/>
 <af:showMessages prefix="M_InsertElAnag"/>
</font>


<af:form method="POST" action="AdapterHTTP" name="Frm1">
<p align="center">
<table class="main"><td colspan="5"><p class="titolo">Elenco anagrafico</p></td></table>

<%@ include file="ElAnagCampiLayOut.inc"%>

</p>

<BR>

<center>
  <% operatoreInfo.showHTML(out); %>
</center>


<BR>
<table class="main">
<tr>
  <%//if(!rdOnly)
    if(false){%>
  <td width="33%"></td>
  <td align="center" width="33%">
          <!--   <input type="submit" name="Inserisci" value="Inserisci nuovo"> -->
          <input class="pulsanti" type="submit" name="Salva" value="Aggiorna" class="pulsanti">
          <!--<input type="submit" name="Cancella" value="cancella il record">-->
  </td>
  <td align="right" width="33%"><%} else {%>
  <td align="center"><%}%>
      <input class="pulsanti" type="button" name="infSotriche" value="Torna alla lista" onClick="sceglipage(1)">
  </td>
</tr>

<input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>">

</table>

<input type="hidden" name="PAGE" value="AnagLavDettaglioInformazioniStorichePage">
<input type="hidden" name="cdnUtIns" value="<%=Utils.notNull(cdnUtIns)%>"/>
<input type="hidden" name="dtmIns" value="<%=Utils.notNull(dtmIns)%>"/>
<input type="hidden" name="cdnUtMod" value="<%=Utils.notNull(cdnUtMod)%>"/>
<input type="hidden" name="dtmMod" value="<%=Utils.notNull(dtmMod)%>"/>
</af:form>

</body>
</html>
