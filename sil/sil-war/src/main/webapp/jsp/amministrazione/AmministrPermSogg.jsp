<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  java.util.*,
                  it.eng.sil.security.ProfileDataFilter,                  
                  it.eng.afExt.utils.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  it.eng.sil.util.*" %>
 
       
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%  
	String cdnLavoratore = (String )serviceRequest.getAttribute("CDNLAVORATORE");
	String _page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
    PageAttribs attributi = new PageAttribs(user, _page);

    boolean readOnlyStr     = true; 
    boolean canInsert       = false;
    boolean infStorButt     = false;
    boolean canNuovoInserimento = false;
    boolean canDelete = false;
	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
        infStorButt = attributi.containsButton("INF_STOR");
    	canInsert = attributi.containsButton("INSERISCI");
    	canNuovoInserimento = attributi.containsButton("NUOVO");
        readOnlyStr = !attributi.containsButton("AGGIORNA");
        canDelete = attributi.containsButton("CANCELLA");
    	if((!canInsert) && (readOnlyStr) && !canNuovoInserimento){
    		//canInsert=false;
        //rdOnly=true;
    	}else{
	        boolean canEdit=filter.canEditLavoratore();
	        if (canInsert){
	          canInsert=canEdit;
	        }
	        if (canDelete)
	        	canDelete = canEdit;
	        if (canNuovoInserimento)
	        	canNuovoInserimento=canEdit;
	        if (!readOnlyStr){
	          readOnlyStr=!canEdit;
	        }        
    	}
    }
%>

<% 
    Vector permSoggiornoRows = serviceResponse.getAttributeAsVector("M_GETEXPERMSOGGIORNO.ROWS.ROW");
	SourceBean row_exPermSogg  = null;
    //String cdnLavoratore   = null;
    String datScad         = null;
    String dataFine        = null;
    String datRichiesta    = null;
    String codMotivoRil    = null;
    String codStatus	   = null;
    String codStatoRic     = null;
    String notePermSogg    = null;
    String strNumDocumento = null;
    String dtmIns          = null;
    String dtmMod          = null;
    BigDecimal cdnUtIns    = null;
    BigDecimal cdnUtMod    = null;
    BigDecimal keyLock     = null;
    BigDecimal progres     = null;
    boolean flag_insert    = false;
    boolean buttonAnnulla  = false;

    Linguette l =null;
    InfCorrentiLav testata = null;
    ///////////////////////////////////
    String COD_LST_TAB="AM_EX_PS";
    String PRG_TAB_DA_ASSOCIARE=null; 
    SourceBean row = null;
    ///////////////////////////////////
    Testata operatoreInfo = null;   

     //cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
   
     testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
     testata.setMaxLenStatoOcc(50);
     
    
     //"Creo" le linguette --
     int _cdnFunz = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE")); 
     //String _page = (String) serviceRequest.getAttribute("PAGE"); 
     l  = new Linguette(user,  _cdnFunz, _page, new BigDecimal(cdnLavoratore));
     
     //----------------------

     if(serviceRequest.containsAttribute("newRecord")) {
        //si vuole inserire un nuovo record
      flag_insert = true; buttonAnnulla = true;
      progres     = new BigDecimal((String) serviceRequest.getAttribute("prgPermSogg"));
     }
     else     { 
        if( permSoggiornoRows != null && !permSoggiornoRows.isEmpty())        { 
        row_exPermSogg  = (SourceBean) permSoggiornoRows.elementAt(0);
         progres         = (BigDecimal) row_exPermSogg.getAttribute("PRGPERMSOGG");
         datScad         = (String)     row_exPermSogg.getAttribute("DATSCADENZA");
         datRichiesta    = (String)     row_exPermSogg.getAttribute("DATRICHIESTA");
         codMotivoRil    = (String)     row_exPermSogg.getAttribute("CODMOTIVORIL");
         codStatus		 = (String)     row_exPermSogg.getAttribute("CODSTATUS");
         codStatoRic     = (String)     row_exPermSogg.getAttribute("CODSTATORICHIESTA");
         notePermSogg    = (String)     row_exPermSogg.getAttribute("STRNOTE");
         dtmIns          = (String)     row_exPermSogg.getAttribute("DTMINS");
         dtmMod          = (String)     row_exPermSogg.getAttribute("DTMMOD");
         cdnUtIns        = (BigDecimal) row_exPermSogg.getAttribute("CDNUTINS");
         cdnUtMod        = (BigDecimal) row_exPermSogg.getAttribute("CDNUTMOD");
         keyLock         = (BigDecimal) row_exPermSogg.getAttribute("NUMKLOPERMSOGG");
         strNumDocumento = (String) row_exPermSogg.getAttribute("strNumDocumento");
         row = row_exPermSogg;
         PRG_TAB_DA_ASSOCIARE=((BigDecimal)  row.getAttribute("PRGPERMSOGG")).toString();
       }//if
       else   {
        //non ci sono dati inerenti il lavoratore: possiamo solo inserire un nuovo record
        flag_insert = true;
       }
     }//else
    
    String display0 = (datScad!=null && datScad.length()>0) ? "inline":"none";
    String img0 = (datScad!=null && datScad.length()>0) ? "../../img/aperto.gif": "../../img/chiuso.gif";
    operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
    
    // NOTE: Attributi della pagina (pulsanti e link) 
    Vector cittadinoEuropeo = serviceResponse.getAttributeAsVector("M_CittadinoCee.ROWS.ROW");
    if (cittadinoEuropeo.size()>0) {
    // il soggetto non e' un extracomunitario per cui non puo' avere il permesso di soggiorno
        readOnlyStr = true;
        canInsert = false;
        canNuovoInserimento=false;
        //infStorButt = false;    
    }
    String display1 = "none";
    String htmlStreamTop = StyleUtils.roundTopTable(!readOnlyStr);
    String htmlStreamBottom = StyleUtils.roundBottomTable(!readOnlyStr);
    
    //controllo presenza di dati storici
    boolean storicoPermSogg = serviceResponse.containsAttribute("M_HasStorPermSogg.ROWS.ROW");
%>

<html>
<head>
<title>Amministrazione Cittadini Stranieri</title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">

<af:linkScript path="../../js/"/>

<script language="JavaScript">
<%@ include file="../patto/_controlloDate_script.inc"%>
<%@ include file="../patto/_sezioneDinamica_script.inc"%>

function cambiaTitoloScadenza() {
	var IsExtraCom = <%=(cittadinoEuropeo!=null&&cittadinoEuropeo.size()==0)%>
	
	if(!IsExtraCom) return;
	
	var index = document.form1.codStatus.options.selectedIndex;                 
  	var opzioneSel = document.form1.codStatus.options[index];
  
    var obj = document.getElementById("TITOLO_SCADENZA");
    var objDivDataFine = document.getElementById("tableDataFine");
    if( opzioneSel.value != "2" && opzioneSel.value != "5" ){
        objDivDataFine.style.display = "inline";
        obj.innerHTML="Scadenza Documento";
    } else if (opzioneSel.value == "2" || opzioneSel.value == "5") {
        objDivDataFine.style.display = "none";
        obj.innerHTML="Revoca Carta";
    }
}
function getFormObj() {return document.form1;}

function controlla(){
    dataRichiesta = document.form1.dataRichiesta.value;
    dataScadenza =  document.form1.dataScadenza.value;
    codStatoRichiesta = document.form1.codStatoRichiesta.value;
    codMotRilascio = document.form1.codMotRilascio.value;
  	
    if (dataRichiesta != "" && codStatoRichiesta == "") {
      alert ("Inserire lo Stato richiesta");
      return false;
    }

    if (dataRichiesta == "" && codStatoRichiesta != "") {
      alert ("Inserire data richiesta");
      return false;
    }

    if ((dataRichiesta != "" || codStatoRichiesta != "") && (codMotRilascio == "")) {
      if (!confirm ('Attenzione, selezionare motivo!')) {
        return false;
      }
    }
    
    cartaSoggIndex  = 0;
    cartaSogg="";
    var status = "";
    try {
        var index = document.form1.codStatus.options.selectedIndex;
        statusIndex = index;
        var opzioneSel = document.form1.codStatus.options[statusIndex];
        status = opzioneSel.value;
    }catch(e) {}
    if (status!="2" && status!="5" && dataScadenza=="") {
        alert(Messages.Date.ERR_PERMESSO_SOGGIORNO_SCAD);
        return false;
    }
    if (status=="2" || status=="5" ) {
      document.form1.dataFine.value = dataScadenza;
    }
    if (isFuture(dataRichiesta)) {
        alert(Messages.Date.ERR_INS);
        return false;
    }
    if (dataScadenza!="" && compDate(dataRichiesta, dataScadenza)>0) {
        alert(Messages.Date.ERR_PERMESSO_SOGGIORNO_STIP_SCAD);
        return false;
    }
    return true;
}

function visualizzaData(oggetto){
    
	
  if (oggetto.value != "2" && oggetto.value != "5"){
    document.getElementById("IMG0").src="../../img/aperto.gif";
    document.getElementById("TBL0").style.display="inline";
  } 
}
function cancella_permesso() {
	tipo = "<%=(codStatus!=null && (codStatus.equals("2") || codStatus.equals("5"))) ? "la carta di soggiorno":"il documento"%>";
	if (confirm("Sei sicuro di voler cancellare " + tipo + "?")) {
		if (!isInSubmit() ) {
			op = _prgPatto>0 ? "1":"0"; // se il legame col patto e' presente bisogna cancellarlo
			pattoForm = getFormObj();
			setParameterForPatto(pattoForm, op);
			document.form1.cancella.disabled=false;
			doFormSubmit();	
		}
	}
}
//-->
<%@ include file="../patto/_associazioneXPatto_scripts.inc" %>
<%@ include file="../patto/_sezioneDinamica_script.inc" %>
</script>
<script language="Javascript">
<%
    attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore);    
%>
   window.top.menu.caricaMenuLav( <%=_cdnFunz%>,   <%=cdnLavoratore%>);
</script>

<%@ include file="CommonScript.inc"%>

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
	width: 98%;
	left: 1%;
	text-align: left;
	text-decoration: none;	
}
</STYLE>
<script language="javascript">
  function mostra_citComEur(id)
  { 
    var div = document.getElementById(id);
    <% 
    	if (cittadinoEuropeo.size()>0) { %>
       		div.style.display="inline";
    <%}%>
  }  
</script>
</head>
<body class="gestione" onLoad="javascript:mostra_citComEur('citComEur');rinfresca();cambiaTitoloScadenza()">

<%
    if (testata!=null)testata.show(out);
    if (l!=null) l.show(out);
%>

<font color="red"><af:showErrors/></font>
<font color="green">
 <af:showMessages prefix="M_SavePermSoggiorno"/>
 <af:showMessages prefix="M_InsertPermSoggiorno"/>
 <af:showMessages prefix="M_DeletePermSoggiorno"/>
</font>

<div align="left" id="citComEur" style="display:none"><font  color="red">
 <UL>
   <LI><strong>Si tratta di un cittadino con cittadinanza U.E.</strong></LI>
</UL></font></div>
<script language="javascript">
  var flgInsert = <%=flag_insert%>;
</script>
<af:form name="form1" method="POST" action="AdapterHTTP" onSubmit="controllaPatto()&&controlla()&&controllaStatoAtto(flgInsert,this)">
<p align="center">
<!-- <table class="main"><tr><td colspan="2"><center><b>Notizie sui Cittadini Stranieri</b></center></td></tr></table> -->
<%
out.print(htmlStreamTop);
%>
<%@ include file="PermSoggCampiLayOut.inc"%>
<br>
<%@ include file="../patto/_associazioneDettaglioXPatto.inc"%>
<!--
<%
out.print(htmlStreamBottom);
%>
<br>
<%
out.print(htmlStreamTop);
%>
-->
<%
if(!flag_insert) {%>
  <table class="main">
  <%if(!readOnlyStr){%>
  <tr><td colspan="3" align="center">
         <input class="pulsante" type="submit" name="save" value="Aggiorna">
         <%keyLock= keyLock.add(new BigDecimal(1));%>      
      <%if (canDelete) {%>
      <input class="pulsante" type="button" value="Cancella" onclick="cancella_permesso()">
      <input type="hidden" name="cancella" value="true" disabled>
      <%}%>
  </tr>
  <tr><td colspan=3><br/></td></tr>
  <%}%>  
  <tr>
      <td width="33%"></td>
      <td align="center" width="33%">
       <%if(!readOnlyStr && canNuovoInserimento) {%>
          <input class="pulsante" type="submit" name="newRecord" value="Inserisci nuove notizie">
       <%}%>
      </td>
      <td align="right" width="33%">
       <%if(infStorButt)
         {%><input class="pulsante<%=((storicoPermSogg)?"":"Disabled")%>" type="button" name="infSotriche" value="Informazioni storiche" 
                   onClick="infoStoriche('PermSoggInfStorPage','&cdnLavoratore=<%=cdnLavoratore%>&CDNFUNZIONE=<%=_cdnFunz%>')"
                   <%=(!storicoPermSogg)?"disabled=\"True\"":""%>>
       <%}%>
      </td>
  </tr>
  </table>
<%}

else if(canInsert){%>
<table class="main">
<tr>
   <td width="33%"/>
   <td width="33%" align="center"><input class="pulsante" type="submit" name="insert" value="Inserisci"></td>
   <td width="33%" align="center">
    <%if(buttonAnnulla){%>
      <input class="pulsante" type="button" name="annulla" value="Chiudi senza inserire"
             onClick="openPage('AmministrPermSoggPage','&cdnLavoratore=<%=cdnLavoratore%>&prgPermSogg=<%=Utils.notNull(progres)%>&CDNFUNZIONE=<%=_cdnFunz%>')">
    <%}%>
   </td>
</tr>
<tr>
  <td colspan="3" align="right">
   <%if(infStorButt)
     {%><input class="pulsante<%=((storicoPermSogg)?"":"Disabled")%>" type="button" name="infSotriche" value="Informazioni storiche" 
               onClick="infoStoriche('PermSoggInfStorPage','&cdnLavoratore=<%=cdnLavoratore%>&CDNFUNZIONE=<%=_cdnFunz%>')"
               <%=(!storicoPermSogg)?"disabled=\"True\"":""%>>
   <%}%>
  </td>
</tr>
</table>
<%
} else { 
	if(infStorButt)
	{%>
		<table class="main">
		<tr>
		  <td colspan="3" align="right">
			<input class="pulsante<%=((storicoPermSogg)?"":"Disabled")%>" type="button" name="infSotriche" value="Informazioni storiche" 
		    	   onClick="infoStoriche('PermSoggInfStorPage','&cdnLavoratore=<%=cdnLavoratore%>&CDNFUNZIONE=<%=_cdnFunz%>')"
		    	   <%=(!storicoPermSogg)?"disabled=\"True\"":""%>>
		  </td>
		</tr>
		</table>
  <%}
  }  
  out.print(htmlStreamBottom);
%>
  <center>
  <table><tr><td align="center">
  <% operatoreInfo.showHTML(out); %>
  </td></tr></table>
  </center>

  <input type="hidden" name="PAGE" value="AmministrPermSoggPage"/>
  <input type="hidden" name="CDNFUNZIONE" value="<%=_cdnFunz%>"/>
  <input type="hidden" name="cdnLavoratore" value="<%=Utils.notNull(cdnLavoratore)%>"/>
  <input type="hidden" name="numKloPermSogg" value="<%=Utils.notNull(keyLock)%>"/>
  <input type="hidden" name="prgPermSogg" value="<%=Utils.notNull(progres)%>"/>
   
</af:form>


</body>
</html>
