<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
   
  it.eng.sil.util.*, 
  it.eng.afExt.utils.StringUtils,
  java.lang.*,
  java.text.*, 
  java.util.*,
  java.math.*,
  it.eng.sil.security.*,
  it.eng.sil.module.presel.CostantiPreselezione
"%>

<%@ taglib uri="aftags" prefix="af"%>
<!-- --- NOTE: Gestione Patto
-->
<%@ taglib uri="patto" prefix="pt" %>
<!-- --- -->
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ include file="../global/Function_CommonRicercaComune.inc" %>
<%@ include file="../presel/Function_CommonRicercaCCNL.inc" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  String cdnLavoratore= (String)serviceRequest.getAttribute("CDNLAVORATORE");

  Vector CCNLRows=null;
  CCNLRows=serviceResponse.getAttributeAsVector("M_GETCCNL.ROWS.ROW");
  SourceBean row_CCNL= null;

  Vector mansioniInseriteRows=null;
  mansioniInseriteRows=serviceResponse.getAttributeAsVector("M_LISTMANSIONI.ROWS.ROW");
  SourceBean row_MansioniInserite=null;  

  Vector espLavoratoreRows=null;
  espLavoratoreRows= serviceResponse.getAttributeAsVector("M_GetLavoratoreTirocini.ROWS.ROW");
  SourceBean row_espLavoratore=null;

 Vector contrattiRows=null;
 contrattiRows= serviceResponse.getAttributeAsVector("M_GETTIPICONTRATTOTIROCINIO.ROWS.ROW");  
 SourceBean row_Contratti=null;

String codMansione= "",
         descMansione= "",
         desTipoMansione="",
         flgEsperienza= "",
         flgDisponibile= "",
         flgDispFormazione= "",
         flgEspForm= "",
         flgPianiInsProf= "",
         codMonoTempo= "";
         
String prgEspLavoro="",
       desMansione="",
       desContratto="",
       numAnnoInizio_list="",
       numAnnoFine_list="";

String prgMansione="";
String codContratto="";
String codAteco="";
String tipo_ateco="";
String strDesAteco="";
String tipo_ccnl="";
String codCCNL="";
String strRifLegge="";
String strDesAttivita="";
String strLivello="";
String codArea="";
String codOrario="";
String strPartitaIvaAzienda="";
String strCodFiscaleAzienda="";  
String strRagSocialeAzienda="";
String codComAzienda="";
String strComAzienda="";
String provinciaAzienda="";
String strIndirizzoAzienda="";
String codNatGiuridica="";
String strTipoClienti="";
String flgCompletato="";
String codMvCessazione="";
String strMotCessazione="";
String codTipoCertificato="";

String numStipendio= "";
String numMeseInizio= "";
String numAnnoInizio= "";
String numMeseFine= "";
String numAnnoFine= "";
String numMesi= "";
String numOre= "";
String numOreSett= "";

String cdnUtins="";
String dtmins="";
String cdnUtmod="";
String dtmmod="";

boolean nuovaMansione=true; 


  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  String _page = (String) serviceRequest.getAttribute("PAGE");
  
  //associa mov	
  boolean canAssociaMovimenti=false;

  InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);

  /*PageAttribs attributi = new PageAttribs(user, "CurrTirociniMainPage");
  boolean canModify = attributi.containsButton("inserisci");
  boolean canDelete = attributi.containsButton("rimuovi");*/
  // NOTE: Attributi della pagina (pulsanti e link) 

	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	
	boolean canView=filter.canViewLavoratore();
	if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
    //PageAttribs attributi = new PageAttribs(user, "CurrEspLavMainPage");
    PageAttribs attributi = new PageAttribs(user, _page);

    boolean canInsert = attributi.containsButton("inserisci");
    boolean canDelete = attributi.containsButton("cancella");
    boolean canModify = attributi.containsButton("aggiorna");
  	//associa mov
	canAssociaMovimenti = attributi.containsButton("ASS_MOV");
    List sezioni = attributi.getSectionList();
    boolean canViewSectionImpresa = sezioni.contains("ESP_IMPRESA");  	
	
    if ( !canModify && !canInsert && !canDelete ) {
      
    } else {
      boolean canEdit = filter.canEditLavoratore();
      if ( !canEdit ) {
        canModify = false;
        canInsert = false;
        canDelete = false;
      }
      
    }

  boolean nuovo = true;
  if(serviceResponse.containsAttribute("M_GETESPLAV")) { nuovo = false; }
  else { nuovo = true; }

  String apriDiv = (String) serviceRequest.getAttribute("APRIDIV");
  if(apriDiv == null) { apriDiv = "none"; }
  else { apriDiv = ""; }
  
  String url_nuovo = "AdapterHTTP?PAGE=CurrTirociniMainPage" + 
                     "&CDNLAVORATORE=" + cdnLavoratore +
                     "&CDNFUNZIONE=" + _funzione +
                     "&APRIDIV=1";
  // SourceBean per la gestione dell'associazione al patto
  SourceBean row = null;
  // --- NOTE: Gestione Patto
  String PRG_TAB_DA_ASSOCIARE = null;
  String COD_LST_TAB = "PR_ESP_L";
  //
  if(!nuovo) {
    // Sono in modalità "Dettaglio"
   SourceBean row_esperienza= (SourceBean) serviceResponse.getAttribute("M_GETESPLAV.ROWS.ROW");
   //
   prgEspLavoro=row_esperienza.containsAttribute("prgEspLavoro") ? row_esperienza.getAttribute("prgEspLavoro").toString() : "";
   prgMansione=row_esperienza.containsAttribute("prgMansione") ? row_esperienza.getAttribute("prgMansione").toString() : "";

  codContratto=StringUtils.getAttributeStrNotNull(row_esperienza, "codContratto");
   codAteco=StringUtils.getAttributeStrNotNull(row_esperienza, "codAteco");
   strDesAteco=StringUtils.getAttributeStrNotNull(row_esperienza, "strDesAteco");
   tipo_ateco=StringUtils.getAttributeStrNotNull(row_esperienza, "tipo_ateco");
   tipo_ccnl=StringUtils.getAttributeStrNotNull(row_esperienza, "tipo_ccnl");
   codCCNL=StringUtils.getAttributeStrNotNull(row_esperienza, "codCCNL");
   strRifLegge=StringUtils.getAttributeStrNotNull(row_esperienza, "strRifLegge");
   strDesAttivita=StringUtils.getAttributeStrNotNull(row_esperienza, "strDesAttivita");
   strLivello=StringUtils.getAttributeStrNotNull(row_esperienza, "strLivello");
   codArea=StringUtils.getAttributeStrNotNull(row_esperienza, "codArea");
   codOrario=StringUtils.getAttributeStrNotNull(row_esperienza, "codOrario");
   strCodFiscaleAzienda=StringUtils.getAttributeStrNotNull(row_esperienza, "strCodFiscaleAzienda");
   strPartitaIvaAzienda=StringUtils.getAttributeStrNotNull(row_esperienza, "strPartitaIvaAzienda");
   strRagSocialeAzienda=StringUtils.getAttributeStrNotNull(row_esperienza, "strRagSocialeAzienda");
   provinciaAzienda=StringUtils.getAttributeStrNotNull(row_esperienza, "provincia");
   codComAzienda=StringUtils.getAttributeStrNotNull(row_esperienza, "codComAzienda");
   strComAzienda=StringUtils.getAttributeStrNotNull(row_esperienza, "strComAzienda");
   strComAzienda=strComAzienda+(!provinciaAzienda.equals("")?" ("+provinciaAzienda+")":"");
   strIndirizzoAzienda=StringUtils.getAttributeStrNotNull(row_esperienza, "strIndirizzoAzienda");
   codNatGiuridica=StringUtils.getAttributeStrNotNull(row_esperienza, "codNatGiuridica");
   strTipoClienti=StringUtils.getAttributeStrNotNull(row_esperienza, "strTipoClienti");
   flgCompletato=StringUtils.getAttributeStrNotNull(row_esperienza, "flgCompletato");
   codMvCessazione=StringUtils.getAttributeStrNotNull(row_esperienza, "codMvCessazione");
   strMotCessazione=StringUtils.getAttributeStrNotNull(row_esperienza, "strMotCessazione");
   codTipoCertificato=StringUtils.getAttributeStrNotNull(row_esperienza, "codTipoCertificato");
    numStipendio= row_esperienza.containsAttribute("numStipendio") ? row_esperienza.getAttribute("numStipendio").toString() : "";
   numMeseInizio= row_esperienza.containsAttribute("numMeseInizio") ? row_esperienza.getAttribute("numMeseInizio").toString() : "";
   numAnnoInizio= row_esperienza.containsAttribute("numAnnoInizio") ? row_esperienza.getAttribute("numAnnoInizio").toString() : "";
   numMeseFine= row_esperienza.containsAttribute("numMeseFine") ? row_esperienza.getAttribute("numMeseFine").toString() : "";
   numAnnoFine= row_esperienza.containsAttribute("numAnnoFine") ? row_esperienza.getAttribute("numAnnoFine").toString() : "";
   numMesi= row_esperienza.containsAttribute("numMesi") ? row_esperienza.getAttribute("numMesi").toString() : "";
   numOre= row_esperienza.containsAttribute("numOre") ? row_esperienza.getAttribute("numOre").toString() : "";
   numOreSett= row_esperienza.containsAttribute("numOreSett") ? row_esperienza.getAttribute("numOreSett").toString() : "";

   cdnUtins=row_esperienza.containsAttribute("cdnUtins") ? row_esperienza.getAttribute("cdnUtins").toString() : "";
   dtmins=row_esperienza.containsAttribute("dtmins") ? row_esperienza.getAttribute("dtmins").toString() : "";
   cdnUtmod=row_esperienza.containsAttribute("cdnUtmod") ? row_esperienza.getAttribute("cdnUtmod").toString() : "";
   dtmmod=row_esperienza.containsAttribute("dtmmod") ? row_esperienza.getAttribute("dtmmod").toString() : "";
   /////////////////////////
   row = row_esperienza;
   PRG_TAB_DA_ASSOCIARE = it.eng.sil.util.Utils.notNull(prgEspLavoro);
  }
  
  String ControllaCdn="";
  Vector rowDispo = serviceResponse.getAttributeAsVector("M_GETLAVCM.ROWS.ROW");
  if (rowDispo.size()>0) {
  	SourceBean sb1 = (SourceBean)rowDispo.get(0);
	ControllaCdn = sb1.containsAttribute("CONTROLLACDN")? sb1.getAttribute("CONTROLLACDN").toString():"";
  }
  
  boolean readOnlyStr = !canModify;
  Testata operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
  // ---
  
 //Tipi movimento
  ArrayList tipiMov = new ArrayList();
  
%>
<%
String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>
<html>

<head>
  <title>Esperienze lavorative</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
    <af:linkScript path="../../js/"/>

<%@ include file="Tirocini_CommonScripts.inc"%>
<SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
<SCRIPT type="text/javascript">

  // --- NOTE: Gestione Patto

  function getFormObj() {return document.Frm1;}
  // ---


// Per rilevare la modifica dei dati da parte dell'utente
var flagChanged = false;  


  function fieldChanged() {
      flagChanged = true;  
  }

  



function Select(prgEspLavoro) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var s= "AdapterHTTP?PAGE=CurrTirociniMainPage";
    s += "&MODULE=M_GETESPLAV";
    s += "&PRGEspLavoro=" + prgEspLavoro;
    s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
    s += "&CDNFUNZIONE=<%= _funzione %>";
    s += "&APRIDIV=1";
    
    setWindowLocation(s);
  }




 function Delete(prgEspLavoro, tipo, prgLavPattoScelta) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var s="Sicuri di voler rimuovere l'esperienza:\'" + tipo.replace('^','\'');
    s += "\' ?";
    if ( confirm(s) ) {

      var s= "AdapterHTTP?PAGE=CurrTirociniMainPage";
      s += "&MODULE=M_DeleteEspLav";
      s += "&PRGESPLAVORO=" + prgEspLavoro;

      s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
      s += "&CDNFUNZIONE=<%=_funzione%>";
      s += "&" + getParametersForPatto(prgLavPattoScelta);
      setWindowLocation(s);
    }
  }  


function controlliForm() {
	if (document.Frm1.FLGDISPONIBILE.value == 'L' && document.Frm1.ControllaCdn.value==""){
    	alert("Il lavoratore non è in collocamento mirato!");
		return false;
   	}
	return true;
}


function apriMovimentiTir(page) {
    var urlpage="";
	urlpage+="AdapterHTTP?";
	urlpage+="cdnLavoratore=<%=cdnLavoratore %>&";
	urlpage+="CDNFUNZIONE=<%=_funzione %>&";
	urlpage+="AssMov_EspNonLav=1&";	
	urlpage+="PAGE="+page;
	var fin = window.open(urlpage,"Associazioni", ' width=980 ,height=450, scrollbars=1, resizable=1'); 
  fin.moveTo (20,100);
}
  
  <%@ include file="../patto/_associazioneXPatto_scripts.inc" %>
  <%@ include file="../patto/_sezioneDinamica_script.inc" %>
       window.top.menu.caricaMenuLav( <%=_funzione%>,   <%=cdnLavoratore%>);
</SCRIPT>
  <%@ include file="../movimenti/MovimentiRicercaSoggetto.inc" %>
  <%@ include file="../presel/_sezioneRicercaAzienda_script.inc" %>
  <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
    attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore);  
      %>
</script>
</head>

<!--body class="gestione" onLoad="<%= canModify ? "toggleVisContratto(Frm1.codContratto);toggleVisMotivazioni(Frm1.codMvCessazione);":""%>"-->
<body class="gestione" onload="rinfresca(); toggleVisMotivazioni(Frm1.codMvCessazione);">

  <%
    Linguette l = new Linguette( user, _funzione, _page, new BigDecimal(cdnLavoratore));
    infCorrentiLav.show(out); 
    l.show(out);
  %>
  <script language="javascript">
    var flgInsert = <%=nuovo%>;
  </script>
  <af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="controllaPatto() && controllaStatoAtto(flgInsert,this) && controlliForm()">
    <input type="hidden" name="prgEspLavoro" value="<%=prgEspLavoro%>"/>
    <p align="center">
      <center>
        <font color="green">
          <af:showMessages prefix="M_InsertEspLav"/>
          <af:showMessages prefix="M_DeleteEspLav"/>
          <af:showMessages prefix="M_SaveEspLav"/>
        </font>
        <font color="red">
          <af:showErrors />
        </font>
      </center>
      <div align="center">
          
      <af:list moduleName="M_GetLavoratoreTirocini" skipNavigationButton="1"
               canDelete="<%=canDelete ? \"1\" : \"0\"%>" 
               canInsert="<%=canInsert ? \"1\" : \"0\"%>" 
               jsSelect="Select" jsDelete="Delete"
      />
    
    
    <%if(canInsert) {%>
          <br>
          <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>');document.location='#aLayerIns';" value="Nuova esperienza"/>
          <%if(canAssociaMovimenti) {%>
          	 <input type="button" class="pulsanti" onClick="apriMovimentiTir('AssMov_EspLavPage')" value="Associa movimenti"/>        
   		 <%}%>	            
    <%}%>
    
    
    
<%
String divStreamTop = StyleUtils.roundLayerTop(canModify);
String divStreamBottom = StyleUtils.roundLayerBottom(canModify);
%>    
    <div id="divLayerDett" name="divLayerDett" class="t_layerDett"
     style="position:absolute; width:80%; left:50; top:200px; z-index:6; display:<%=apriDiv%>;">
	<a name="aLayerIns"></a>
	
    <!-- Stondature ELEMENTO TOP -->
    <%out.print(divStreamTop);%>

      <table width="100%">
        <tr width="100%">
          <td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
          <td height="16" class="azzurro_bianco">
             <%if(nuovo){%>
                Nuova esperienza
             <%} else {%>
                Esperienza
             <%}%>
          </td>
          <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
        </tr>
      </table>
   
      <%//out.print(htmlStreamTop);%>
      <table class="main" width="80%" cellspacing="2">
        <%@ include file="tir_elemento.inc" %>  
        <tr>
          <td colspan=2> 
            <%@ include file="../patto/_associazioneDettaglioXPatto.inc" %>
          </td>
        </tr>
      </table>
      <%//out.print(htmlStreamBottom);%> 
      <br/>
      <center>
        <input type="hidden" name="PAGE" value="CurrTirociniMainPage">
        <input type="hidden" name="cdnLavoratore" value="<%= cdnLavoratore %>"/>
        <input type="hidden" name="cdnFunzione" value="<%= _funzione %>"/>
        <input type="hidden" name="ControllaCdn" value="<%=ControllaCdn%>">
        <%if(nuovo) {%>
          <input class="pulsante" type="submit" name="inserisci" value="Inserisci" />
          <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="ChiudiDivLayer('divLayerDett')">
        <% } %>
        <%if(!nuovo && canModify) {%>
            <input type="submit" class="pulsanti" name="salva" value="Aggiorna" >
            <input type="button" 
              class="pulsanti" 
              name="annulla" 
              value="<%= canModify ? "Chiudi senza aggiornare" : "Chiudi" %>" 
              onClick="ChiudiDivLayer('divLayerDett')">
        <%}%>
      </center>
      <%operatoreInfo.showHTML(out);%>
      <!-- Stondature ELEMENTO BOTTOM -->
    <%out.print(divStreamBottom);%>
    </div>
  </af:form>
</body>

</html>

<% } %>
