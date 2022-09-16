<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
   
  it.eng.sil.util.*, 
  it.eng.afExt.utils.StringUtils,
  java.lang.*,
  java.text.*, 
  java.util.*,
  it.eng.sil.module.coop.GetDatiPersonali,
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

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  String cdnLavoratore= "0";//(String)serviceRequest.getAttribute("CDNLAVORATORE");

  /*Vector CCNLRows=null;
  CCNLRows=serviceResponse.getAttributeAsVector("M_GETCCNL.ROWS.ROW");
  */
  SourceBean row_CCNL= null;
  

  //Vector mansioniInseriteRows=null;
  //mansioniInseriteRows=serviceResponse.getAttributeAsVector("M_LISTMANSIONI.ROWS.ROW");
  SourceBean row_MansioniInserite=null;  

  Vector espLavoratoreRows=null;
  //espLavoratoreRows= serviceResponse.getAttributeAsVector("M_GETLAVORATOREESPLAV.ROWS.ROW");
  //SourceBean row_espLavoratore=null;

 Vector contrattiRows=null;
 contrattiRows= serviceResponse.getAttributeAsVector("M_GETTIPICONTRATTO.ROWS.ROW");  
 SourceBean row_Contratti=null;

String codMansione= "",descrizioneMansione="",
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
//Tipi movimento
ArrayList tipiMov = new ArrayList();
String strTipi = "";

boolean nuovaMansione=true;


  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
  String _page = (String) serviceRequest.getAttribute("PAGE");
  /*PageAttribs attributi = new PageAttribs(user, "CurrEspLavMainPage");
  boolean canModify = attributi.containsButton("inserisci");
  boolean canDelete = attributi.containsButton("rimuovi");*/
  // NOTE: Attributi della pagina (pulsanti e link) 

  ProfileDataFilter filter = new ProfileDataFilter(user, _page);
  //filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	
  //boolean canModify = false;
	
  boolean canView=filter.canView();
  if (! canView){
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  }
	// else{
  //PageAttribs attributi = new PageAttribs(user, "CurrEspLavMainPage");
  PageAttribs attributi = new PageAttribs(user, _page);
	
  boolean canInsert = attributi.containsButton("inserisci");
  boolean canDelete = attributi.containsButton("cancella");
  boolean canModify = attributi.containsButton("aggiorna");
  List sezioni = attributi.getSectionList();
  boolean canViewSectionImpresa = sezioni.contains("ESP_IMPRESA");

  boolean nuovo = true;
  if(serviceResponse.containsAttribute("M_COOP_DETTAGLIOESPLAV_DALLE_CACHE")) { nuovo = false; }
  else { nuovo = true; }

  String apriDiv = (String) serviceRequest.getAttribute("APRIDIV");
  if(apriDiv == null) { apriDiv = "none"; }
  else { apriDiv = ""; }
  
  String url_nuovo = "AdapterHTTP?PAGE=CurrEspLavMainPage" +
                     "&CDNLAVORATORE=" + cdnLavoratore +
                     "&CDNFUNZIONE=" + _funzione +
                     "&APRIDIV=1";
  // SourceBean per la gestione dell'associazione al patto
  SourceBean row = null;
  // --- NOTE: Gestione Patto
  //String PRG_TAB_DA_ASSOCIARE = null;
  //String COD_LST_TAB = "PR_ESP_L";
  //
  if(!nuovo) {
    // Sono in modalità "Dettaglio"
   SourceBean row_esperienza= (SourceBean) serviceResponse.getAttribute("M_COOP_DETTAGLIOESPLAV_DALLE_CACHE.ROWS.ROW");
   //
   prgEspLavoro=row_esperienza.containsAttribute("prgEspLavoro") ? row_esperienza.getAttribute("prgEspLavoro").toString() : "";
   prgMansione=row_esperienza.containsAttribute("prgMansione") ? row_esperienza.getAttribute("prgMansione").toString() : "";

   //RECUPERO TIPO MOVIMENTO
   Vector tipo_mov_vec = serviceResponse.getAttributeAsVector("M_COOP_DettaglioEspLavGetTipoMov_dalle_cache.ROWS.ROW");
   if ( (tipo_mov_vec != null) && (tipo_mov_vec.size() > 0) ){
      SourceBean tipo_mov = null;
      for(int i=0;i<tipo_mov_vec.size();i++){
        tipo_mov = (SourceBean)tipo_mov_vec.get(i);
        Object cmmd = tipo_mov.getAttribute("CODMONOMOVDICH");
        if (cmmd != null) {
        	tipiMov.add(cmmd.toString());
        }
      }
   }
  //----
   codContratto         = StringUtils.getAttributeStrNotNull(row_esperienza, "codContratto");
   codAteco             = StringUtils.getAttributeStrNotNull(row_esperienza, "codAteco");
   strDesAteco          = StringUtils.getAttributeStrNotNull(row_esperienza, "strDesAteco");
   tipo_ateco           = StringUtils.getAttributeStrNotNull(row_esperienza, "tipo_ateco");
   tipo_ccnl            = StringUtils.getAttributeStrNotNull(row_esperienza, "tipo_ccnl");
   codCCNL              = StringUtils.getAttributeStrNotNull(row_esperienza, "codCCNL");
   strRifLegge          = StringUtils.getAttributeStrNotNull(row_esperienza, "strRifLegge");
   strDesAttivita       = StringUtils.getAttributeStrNotNull(row_esperienza, "strDesAttivita");
   strLivello           = StringUtils.getAttributeStrNotNull(row_esperienza, "strLivello");
   codArea              = StringUtils.getAttributeStrNotNull(row_esperienza, "codArea");
   codOrario            = StringUtils.getAttributeStrNotNull(row_esperienza, "codOrario");
   strCodFiscaleAzienda = StringUtils.getAttributeStrNotNull(row_esperienza, "strCodFiscaleAzienda");
   strPartitaIvaAzienda = StringUtils.getAttributeStrNotNull(row_esperienza, "strPartitaIvaAzienda");
   strRagSocialeAzienda = StringUtils.getAttributeStrNotNull(row_esperienza, "strRagSocialeAzienda");
   provinciaAzienda     = StringUtils.getAttributeStrNotNull(row_esperienza, "provincia");
   codComAzienda        = StringUtils.getAttributeStrNotNull(row_esperienza, "codComAzienda");
   strComAzienda        = StringUtils.getAttributeStrNotNull(row_esperienza, "strComAzienda");
   strComAzienda        = strComAzienda+(!provinciaAzienda.equals("")?" ("+provinciaAzienda+")":"");
   strIndirizzoAzienda  = StringUtils.getAttributeStrNotNull(row_esperienza, "strIndirizzoAzienda");
   codNatGiuridica      = StringUtils.getAttributeStrNotNull(row_esperienza, "codNatGiuridica");
   strTipoClienti       = StringUtils.getAttributeStrNotNull(row_esperienza, "strTipoClienti");
   flgCompletato        = StringUtils.getAttributeStrNotNull(row_esperienza, "flgCompletato");
   codMvCessazione      = StringUtils.getAttributeStrNotNull(row_esperienza, "codMvCessazione");
   strMotCessazione     = StringUtils.getAttributeStrNotNull(row_esperienza, "strMotCessazione");
   codTipoCertificato   = StringUtils.getAttributeStrNotNull(row_esperienza, "codTipoCertificato");
   numStipendio         = row_esperienza.containsAttribute("numStipendio") ? row_esperienza.getAttribute("numStipendio").toString() : "";
   numMeseInizio        = row_esperienza.containsAttribute("numMeseInizio") ? row_esperienza.getAttribute("numMeseInizio").toString() : "";
   numAnnoInizio        = row_esperienza.containsAttribute("numAnnoInizio") ? row_esperienza.getAttribute("numAnnoInizio").toString() : "";
   numMeseFine          = row_esperienza.containsAttribute("numMeseFine") ? row_esperienza.getAttribute("numMeseFine").toString() : "";
   numAnnoFine          = row_esperienza.containsAttribute("numAnnoFine") ? row_esperienza.getAttribute("numAnnoFine").toString() : "";
   numMesi              = row_esperienza.containsAttribute("numMesi") ? row_esperienza.getAttribute("numMesi").toString() : "";
   numOre               = row_esperienza.containsAttribute("numOre") ? row_esperienza.getAttribute("numOre").toString() : "";
   numOreSett           = row_esperienza.containsAttribute("numOreSett") ? row_esperienza.getAttribute("numOreSett").toString() : "";
   codMansione= StringUtils.getAttributeStrNotNull(row_esperienza, "codMansione");
   descrizioneMansione= StringUtils.getAttributeStrNotNull(row_esperienza, "descrizione_Mansione");
   cdnUtins = row_esperienza.containsAttribute("cdnUtins") ? row_esperienza.getAttribute("cdnUtins").toString() : "";
   dtmins   = row_esperienza.containsAttribute("dtmins") ? row_esperienza.getAttribute("dtmins").toString() : "";
   cdnUtmod = row_esperienza.containsAttribute("cdnUtmod") ? row_esperienza.getAttribute("cdnUtmod").toString() : "";
   dtmmod   = row_esperienza.containsAttribute("dtmmod") ? row_esperienza.getAttribute("dtmmod").toString() : "";
   /////////////////////////
   row = row_esperienza;
   //PRG_TAB_DA_ASSOCIARE = it.eng.sil.util.Utils.notNull(prgEspLavoro);
  }
  
  boolean readOnlyStr = !canModify;
  //Testata operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
  // ---
%>
<%
String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>
<html>

<head>
  <title>Esperienze lavorative</title>

  <link rel="stylesheet" href="../../css/stiliCoop.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetailCoop.css" type="text/css">
    <af:linkScript path="../../js/"/>

<%@ include file="EspLavorative_CommonScripts.inc"%>
<%@ include file="../movimenti/MovimentiRicercaSoggetto.inc" %>
<%@ include file="../presel/_sezioneRicercaAzienda_script.inc" %>
<%@ include file="../global/Function_CommonRicercaComune.inc" %>
<%@ include file="../presel/Function_CommonRicercaCCNL.inc" %>
<SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
<SCRIPT type="text/javascript">
   <%@ include file="../patto/_sezioneDinamica_script.inc"%>
  // --- NOTE: Gestione Patto


  // ---


// Per rilevare la modifica dei dati da parte dell'utente
var flagChanged = false;  


  function fieldChanged() {
      
  }

  



function Select(prgEspLavoro) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var s= "AdapterHTTP?PAGE=CoopEsperienzePage";
    s += "&MODULE=M_GETESPLAV";
    s += "&PRGEspLavoro=" + prgEspLavoro;
    s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
    s += "&CDNFUNZIONE=<%= _funzione %>";
    s += "&APRIDIV=1";
    
    setWindowLocation(s);
  }




function apriMovimenti(page) {
    var urlpage="";
	urlpage+="AdapterHTTP?";
	urlpage+="cdnLavoratore=<%=cdnLavoratore %>&";
	urlpage+="CDNFUNZIONE=<%=_funzione %>&";	
	urlpage+="PAGE="+page;
	var fin = window.open(urlpage,"Associazioni", ' width=980 ,height=450, scrollbars=1, resizable=1'); 
  fin.moveTo (20,100);
}

function controllaMotivazioni() {
	if (document.forms[0].codMvCessazione.value=="")
		document.forms[0].strMotCessazione.value="";
	return true;
}

</SCRIPT>

</head>

<body class="gestione" onload="rinfresca(); toggleVisMotivazioni(document.Frm1.codMvCessazione);">

<%@ include file="_testataLavoratore.inc" %>
<%@ include file="_linguetta.inc" %>
  <%--
    Linguette l = new Linguette( user, _funzione, "CurrEspLavMainPage", new BigDecimal(cdnLavoratore));
    infCorrentiLav.show(out); 
    l.show(out);
  --%>
  <script language="javascript">
    var flgInsert = <%=nuovo%>;
  </script>
  <af:form method="POST" action="AdapterHTTP" name="Frm1" >
    <input type="hidden" name="prgEspLavoro" value="<%=prgEspLavoro%>"/>
    <p align="center">
      <center>
        <font color="red">
          <af:showErrors />
        </font>
      </center>
      <div align="center">          
	      <af:list moduleName="M_COOP_LISTAESPLAV_DALLA_CACHE" skipNavigationButton="1" jsSelect="Select" />
    </div>
   <p/>
    
    
<!-- LAYER -->
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
        <tr>
          <td width="16" height="16" class="azzurro_bianco"><img src="../../img/move_layer.gif" onClick="return false" onMouseDown="engager(event,'divLayerDett');return false"></td>
          <td height="16" class="azzurro_bianco">          
            Esperienza
          </td>
          <td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')" class="azzurro_bianco"><img src="../../img/chiudi_layer.gif" alt="Chiudi"></td>
        </tr>
      </table>
      
      <!-- tipo Movimento --> 
      <% if(!nuovo) {
             if ( tipiMov.size() != 0 ) {
                  strTipi = "Movimento: Non documentato";
                  for(int i=0; i<tipiMov.size();i++)
                  {
                      if (tipiMov.get(i).toString().equalsIgnoreCase("D") ||
                          tipiMov.get(i).toString().equalsIgnoreCase("O"))
                      {
                          strTipi = "Movimento: Documentato";
                          i=tipiMov.size();
                      } 
                  }//for                  
               %>   
             <UL>
               <LI>
            	<font  color="red">
                  <strong>
                    <%= strTipi %>
                  </strong>
            	</font>
               </LI>
            </UL>
         <%}%>
      <%}%>
      <!-- fine -->
      <%//out.print(htmlStreamTop);%>
      <table class="main">      	      	
	        <%@ include file="EspLavorative_Elemento.inc"%>              
      </table>
      <br/>
      <center>
        <input type="hidden" name="PAGE" value="CurrEspLavMainPage">
        <input type="hidden" name="cdnLavoratore" value="<%= cdnLavoratore %>"/>
        <input type="hidden" name="cdnFunzione" value="<%= _funzione %>"/>
                
            <input type="button" 
              class="pulsanti" 
              name="annulla" 
              value="<%= canModify ? "Chiudi senza aggiornare" : "Chiudi" %>" 
              onClick="ChiudiDivLayer('divLayerDett')">        
      </center>
      
      
      <!-- Stondature ELEMENTO BOTTOM -->
    <%out.print(divStreamBottom);%>
    </div>
<!-- LAYER - END --> 
  </af:form>
</body>

</html>

