<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.security.ProfileDataFilter,
                  it.eng.afExt.utils.*,                  
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  it.eng.sil.util.*
                  " %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String cdnLavoratore= (String )serviceRequest.getAttribute("CDNLAVORATORE");
	String _page = (String) serviceRequest.getAttribute("PAGE"); 
 	int _cdnFunz = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE")); 
 	
 	String numMesiSospEsterni = "";
    String numIscrizione = "";
    String prAutomatica     = "S";
    String docInOut         = "I";
    String docRif           = "Documentazione L68";
    String docTipo          = "ISCRIZIONE LEGGE 68/99";
    BigDecimal numProtV     = null;
    BigDecimal numAnnoProtV = null;
    String dataOraProt      = "";
    String datProtV     = "";
    String oraProtV     = "";
    String CODSTATOATTO = "";
  	
  	
    String prgSpiMod = "";
    String datSospensione="";
    String prgVerbaleAcc = "";
   
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
    PageAttribs attributi = new PageAttribs(user, _page);

  boolean readOnlyStr     = true;
  boolean canInsert       = false;
  boolean infStorButt     = false;
  boolean canModifyNote	  = false;
  boolean canManage		  = false;
  boolean canDelete       = false;

	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
      infStorButt = attributi.containsButton("INF_STOR");
    	canInsert = attributi.containsButton("INSERISCI");
      readOnlyStr = !attributi.containsButton("AGGIORNA");
      canModifyNote= attributi.containsButton("AGGIORNA_NOTE");
      canDelete = attributi.containsButton("CANCELLA");

      if((!canInsert) && (readOnlyStr)){
			//canInsert=false;
		    //rdOnly=true;
			}else{
			    boolean canEdit=filter.canEditLavoratore();
			    if (canInsert){
			      canInsert=canEdit;
			    }
			    if (!readOnlyStr){
			      readOnlyStr=!canEdit;
			    }        
			    
			    if(canModifyNote){
			       canModifyNote=canEdit;
			    }
	      	}
  	  }


    Vector listeSpecRows = serviceResponse.getAttributeAsVector("M_GetDettCollMirato.ROWS.ROW");
    
    SourceBean listeSpec_Row = null;
    //String cdnLavoratore     = null;
    String dataInizio        = null;
    String dataFine          = null;
    String codTipoIscr       = null;
    String codTipoInvalidita = null;
    String strNote           = null;
    String dtmIns            = null;
    String dtmMod            = null;
    String datAccSanitario   = null;
    String codAccSanitario   = null;
    String datAnzianita68	= null;
    String datUltimaIscr	=null;
    
    
    BigDecimal percInval     = null;
    BigDecimal cdnUtMod      = null;
    BigDecimal cdnUtIns      = null;
    BigDecimal keyLock       = null;
    BigDecimal prgCMIscr     = null;
    boolean flag_insert      = false;
    /*boolean readOnlyStr      = true;
    boolean infStorButt      = false;
    boolean canInsert        = false;*/
    boolean buttonAnnulla    = false;
    Testata operatoreInfo    = null;
    String tipoRaggiunto = "";
    String COD_LST_TAB="AM_CM_IS";
    String PRG_TAB_DA_ASSOCIARE=null; 
    SourceBean row = null;

   if(cdnLavoratore != null)
   { 
     InfCorrentiLav testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
     testata.setPaginaLista("CollMiratoRisultRicercaPage");
    
     //"Creo" le linguette --
    
     //String _page = (String) serviceRequest.getAttribute("PAGE"); 
     Linguette l  = new Linguette(user,  _cdnFunz, _page, new BigDecimal(cdnLavoratore));
     
     //----------------------

    String apriDiv = "none";
    

     if(serviceRequest.containsAttribute("newRecord"))
     { flag_insert = true;  buttonAnnulla = true;
     }
     else
     { if(listeSpecRows != null && !listeSpecRows.isEmpty()) 
       { 
         apriDiv = "";
         listeSpec_Row  = (SourceBean) listeSpecRows.elementAt(0);
         dataInizio     = (String)     listeSpec_Row.getAttribute("DATINIZIO"); 
         dataFine       = (String)     listeSpec_Row.getAttribute("DATFINE");
         codTipoIscr    = (String)     listeSpec_Row.getAttribute("CODCMTIPOISCR");
         codTipoInvalidita = (String)  listeSpec_Row.getAttribute("CODTIPOINVALIDITA");
         datAccSanitario= (String)     listeSpec_Row.getAttribute("DATACCERTSANITARIO");
         codAccSanitario= (String)     listeSpec_Row.getAttribute("CODACCERTSANITARIO");
         percInval      = (BigDecimal) listeSpec_Row.getAttribute("NUMPERCINVALIDITA");
         strNote        = (String)     listeSpec_Row.getAttribute("STRNOTE");
         datAnzianita68 = (String)     listeSpec_Row.getAttribute("DATANZIANITA68");
         datUltimaIscr	= (String)	   listeSpec_Row.getAttribute("DATULTIMAISCR");
         dtmIns         = (String)     listeSpec_Row.getAttribute("DTMINS");
         dtmMod         = (String)     listeSpec_Row.getAttribute("DTMMOD");
         cdnUtIns       = (BigDecimal) listeSpec_Row.getAttribute("CDNUTINS");
         cdnUtMod       = (BigDecimal) listeSpec_Row.getAttribute("CDNUTMOD");
         keyLock        = (BigDecimal) listeSpec_Row.getAttribute("NUMKLOCMISCR");
         prgCMIscr      = (BigDecimal) listeSpec_Row.getAttribute("PRGCMISCR");
         numIscrizione = row.getAttribute("NUMISCRIZIONE") == null? "" : ((BigDecimal)row.getAttribute("NUMISCRIZIONE")).toString();
         String codMonoTipoRagg = (String)listeSpec_Row.getAttribute("CODMONOTIPORAGG");
         numMesiSospEsterni = row.getAttribute("NUMMESISOSPESTERNI") == null? "" : ((BigDecimal)row.getAttribute("NUMMESISOSPESTERNI")).toString();
		prgVerbaleAcc = row.getAttribute("verbale") == null? "" : row.getAttribute("verbale").toString();
		datSospensione     = (String)     row.getAttribute("DATSOSPENSIONE");
		prgSpiMod = row.getAttribute("operatore") == null? "" : row.getAttribute("operatore").toString();
		datAccSanitario = (String) row.getAttribute("DATACCERTSANITARIO");
		codAccSanitario = (String) row.getAttribute("codAccSanitario");
		numProtV = (BigDecimal) row.getAttribute("NUMPROTOCOLLO"); 
		dataOraProt = row.getAttribute("DATAORAPROT") == null? "" : (String)row.getAttribute("DATAORAPROT"); 
		numAnnoProtV = (BigDecimal) row.getAttribute("NUMANNOPROT"); 
		
		if (!dataOraProt.equals("")) {
	  		oraProtV = dataOraProt.substring(11,16);
	  		datProtV = dataOraProt.substring(0,10);
  		}  	
  		
  		CODSTATOATTO = row.getAttribute("STATO") == null? "" : (String)row.getAttribute("STATO");
         if (codMonoTipoRagg!=null && codMonoTipoRagg.equals("D"))
             tipoRaggiunto = "Disabili";
         else if (codMonoTipoRagg!=null && codMonoTipoRagg.equals("A"))
             tipoRaggiunto = "Altri";
         PRG_TAB_DA_ASSOCIARE=((BigDecimal)  listeSpec_Row.getAttribute("PRGCMISCR")).toString();
         row = listeSpec_Row;
       }
       else{ flag_insert = true; }
     }
     
     if(flag_insert){  
   		canManage=canInsert;
   	  }else{
   		canManage = (canModifyNote || (!readOnlyStr));
      }
      
    String cpiCompLav = "";
	SourceBean cpiCompSb = (SourceBean) serviceResponse.getAttribute("CM_GET_CODCPI.ROWS.ROW");				
	if (cpiCompSb != null) {	
		cpiCompLav = (String) cpiCompSb.getAttribute("DESCRIZIONE");
	} 
    
   operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);

  // NOTE: Attributi della pagina (pulsanti e link) 
  /*PageAttribs attributi = new PageAttribs(user, "AmstrListeSpecCmPage"); 
   *readOnlyStr = !attributi.containsButton("AGGIORNA");
   *canInsert   =  attributi.containsButton("INSERISCI");
   *infStorButt =  attributi.containsButton("INF_STOR");
   */  
  String htmlStreamTop = StyleUtils.roundTopTable(canInsert);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canInsert);


  //controllo presenza dati storici
 boolean storicoCM = serviceResponse.containsAttribute("M_HasStorCM.ROWS.ROW");
%>

<%
// POPUP EVIDENZE
String strApriEv = StringUtils.getAttributeStrNotNull(serviceRequest, "APRI_EV");
int _fun = 1;
if(_cdnFunz>0) { _fun = _cdnFunz; }
EvidenzePopUp jsEvid = null;
boolean bevid = attributi.containsButton("EVIDENZE");
if(strApriEv.equals("1") && bevid) {
	jsEvid = new EvidenzePopUp(cdnLavoratore, _fun, user.getCdnGruppo(), user.getCdnProfilo());
}	
%> 

<html>
<head>
<title>Amministrazione - Liste Speciali: Colocamento Mirato</title>

<link rel="stylesheet" href=" ../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>

<%if(strApriEv.equals("1") && bevid) { jsEvid.show(out); }%>

<SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
<%@ include file="CommonScript.inc"%>
<script>
    function getFormObj() {return document.form1;}
    <%@ include file="../patto/_sezioneDinamica_script.inc"%>
 <%@ include file="../patto/_associazioneXPatto_scripts.inc" %>

function Select(PRGCMISCR) {
    // Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

    var s= "AdapterHTTP?PAGE=AmstrListeSpecCMPage";
    s += "&PRGCMISCR=" + PRGCMISCR;
    s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
    s += "&CDNFUNZIONE=<%=_cdnFunz%>";
    // --- NOTE: Gestione Patto
    // ---

    setWindowLocation(s);
  }
  function Delete(page, prgCMIscr, cancella, cdnLavoratore, cdnFunzione){
  	if (confirm("Sei sicuro di voler eliminare il colocamento mirato? L'iscrizione potrebbe essere legata ad un patto.")) {
  		var s= "AdapterHTTP?";
	    s+="PAGE="+page;
		s+="&PRGCMISCR="+prgCMIscr;
		s+="&CANCELLA="+cancella;
		s+="&CDNLAVORATORE="+cdnLavoratore;
		s+="&CDNFUNZIONE="+cdnFunzione;
	    setWindowLocation(s);
  	}
  }
 
</script>
<script language="Javascript">
<%
    attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore);    
%>
</script>
</head>
<body class="gestione" onload="rinfresca();<%if(strApriEv.equals("1") && bevid) { %> apriEvidenze() <%}%>">
<script language="Javascript">
     window.top.menu.caricaMenuLav( <%=_cdnFunz%> ,  <%=cdnLavoratore%>);
</script>

<%
    testata.show(out);
    l.show(out);
%>

<font color="red"><af:showErrors/></font>
<font color="green">
 <af:showMessages prefix="M_CancellaCmIscr"/>
 <af:showMessages prefix="M_SaveCmIscr"/>
 <af:showMessages prefix="M_InsCmIscr"/>
 <af:showMessages prefix="UpdateNoteCM"/>
</font> 

<script language="javascript">
  var flgInsert = <%=flag_insert%>;
</script>

  <p align="center">
      <af:list moduleName="M_GETCMISCR" skipNavigationButton="1" jsDelete="Delete"
             jsSelect="Select" canDelete="<%=(canDelete)?\"1\":\"0\"%>"/>          
  </p>




<!-- LAYER -->
<%
String divStreamTop = StyleUtils.roundLayerTop(canManage);
String divStreamBottom = StyleUtils.roundLayerBottom(canManage);
if(serviceResponse.containsAttribute("UpdateNoteCM")){
   	apriDiv="none";
}
%>
  <div id="divLayerDett" name="divLayerDett" class="t_layerDett"
   style="position:absolute; width:80%; left:50; top:200px; z-index:6; display:<%=apriDiv%>;"> 
  <!-- Stondature ELEMENTO TOP -->
  <%out.print(divStreamTop);%>    

    <table width="100%">
      <tr width="100%">
        <td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
        <td height="16" class="azzurro_bianco">
          Collocamento Mirato
        </td>
        <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
      </tr>
    </table>

<af:form name="form1" method="POST" action="AdapterHTTP" onSubmit="controllaPatto() && controllaStatoAtto(flgInsert,this)">
<p align="center">
<%@ include file="CollMiratoCampiLayOut.inc"%>
<br>

<%if(!flag_insert) {%>
  <table class="main">
    <%if(!readOnlyStr){%>
    <tr><td colspan=3><%@ include file="../patto/_associazioneDettaglioXPatto.inc"%></td></tr>
  <tr><td colspan="3" align="center">
             <input class="pulsante" type="submit" name="save" value="Aggiorna">
             <%keyLock= keyLock.add(new BigDecimal(1));%><br/></td>
  </tr>
  <tr><td><br/></td></tr>
    <%}%>
</table>
<%}

else {%>
<table class="main">
    <tr><td colspan=3><%@ include file="../patto/_associazioneDettaglioXPatto.inc"%></td></tr>
<tr>
   <td width="33%"/>
   <td width="33%" align="center">
   </td>
   <td width="33%" align="center">
    <%if(buttonAnnulla) {%>
      <input class="pulsante" type="button" name="annulla" value="Chiudi senza inserire"
             onClick="openPage('AmstrListeSpecCMPage','&cdnLavoratore=<%=cdnLavoratore%>&prgCMIscr=<%=Utils.notNull(prgCMIscr)%>&CDNFUNZIONE=<%=_cdnFunz%>')">
    <%}%>
   </td>
</tr>
</table>
<%} %>
  <br><center><% operatoreInfo.showHTML(out); %></center>
<%
  out.print(divStreamBottom);
%>
  <input type="hidden" name="PAGE" value="AmstrListeSpecCMPage"/>     
  <input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>"/>
  <input type="hidden" name="CDNFUNZIONE" value="<%=_cdnFunz%>"/>
  <input type="hidden" name="cdnUtMod" value="<%=cdnUtMod %>"/>
  <input type="hidden" name="keyLockCMIscr" value="<%=keyLock%>"/>
  <input type="hidden" name="prgCMiscr"     value="<%=Utils.notNull(prgCMIscr)%>"/>
  <input type="hidden" name="soloNote"    value=""/>

  <!-- valori temporanei che andranno poi inseriti nella JSP-->
  <input type="hidden" name="prgDichDisponibilita"     value="0"/>
  <input type="hidden" name="numBaseDiPartenza"        value="0"/>
  <input type="hidden" name="numPuntiAnziznita"        value="0"/>
  <input type="hidden" name="numPuntiFigliInvaCarico"  value="0"/>
  <input type="hidden" name="numPuntiAltrePersACarico" value="0"/>
  <input type="hidden" name="numPuntiReddito"          value="0"/>


</af:form>
</div>
      <center>
      <%if(infStorButt)
        {%><input class="pulsante<%=((storicoCM)?"":"Disabled")%>" type="button" name="infSotriche" value="Informazioni storiche" 
                  onClick="infoStoriche('CollMiratoInfStorPage','&cdnLavoratore=<%=cdnLavoratore%>')"
                  <%=(!storicoCM)?"disabled=\"True\"":""%>>
      <%}%>
      </center>

<%}//end if ("cdnLavoratore") 





else {
    %><h3>L'attributo <i>cdnLavoratore</i> non è presente nella serviceRequest</h3>
      <h4>questo rende impossibile visualizzare o memorizzare i dati!</h4><%
}//else ("cdnLavoratore")
%>



</body>
</html>
