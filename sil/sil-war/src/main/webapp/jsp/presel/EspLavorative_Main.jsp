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
  espLavoratoreRows= serviceResponse.getAttributeAsVector("M_GETLAVORATOREESPLAV.ROWS.ROW");
  SourceBean row_espLavoratore=null;

 Vector contrattiRows=null;
 contrattiRows= serviceResponse.getAttributeAsVector("M_GETTIPICONTRATTO.ROWS.ROW");  
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
String codtipocontratto="";
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
  filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));

  boolean canAssociaMovimenti=false;

  //boolean canModify = false;

  boolean canView=filter.canViewLavoratore();
  if (! canView){
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  }else{
    
  //PageAttribs attributi = new PageAttribs(user, "CurrEspLavMainPage");
   
  PageAttribs attributi = new PageAttribs(user, _page);
  canAssociaMovimenti = attributi.containsButton("ASS_MOV");
  boolean canInsert = attributi.containsButton("inserisci");
  boolean canDelete = attributi.containsButton("cancella");
  boolean canModify = attributi.containsButton("aggiorna");
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
  
  String url_nuovo = "AdapterHTTP?PAGE=CurrEspLavMainPage" +
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

   //RECUPERO TIPO MOVIMENTO
   Vector tipo_mov_vec = serviceResponse.getAttributeAsVector("M_GetTipoMov.ROWS.ROW");
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
   //contiene codraplav da DE_RAPLAV, per Cliclavoro
   codtipocontratto     = StringUtils.getAttributeStrNotNull(row_esperienza, "codrapportolav");
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

   cdnUtins = row_esperienza.containsAttribute("cdnUtins") ? row_esperienza.getAttribute("cdnUtins").toString() : "";
   dtmins   = row_esperienza.containsAttribute("dtmins") ? row_esperienza.getAttribute("dtmins").toString() : "";
   cdnUtmod = row_esperienza.containsAttribute("cdnUtmod") ? row_esperienza.getAttribute("cdnUtmod").toString() : "";
   dtmmod   = row_esperienza.containsAttribute("dtmmod") ? row_esperienza.getAttribute("dtmmod").toString() : "";
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

<%@ include file="EspLavorative_CommonScripts.inc"%>
<%@ include file="../movimenti/MovimentiRicercaSoggetto.inc" %>
<%@ include file="../presel/_sezioneRicercaAzienda_script.inc" %>
<SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
<script type="text/javascript" src="../../js/ComboPair.js"></script>
<SCRIPT type="text/javascript">
   <%@ include file="../patto/_sezioneDinamica_script.inc"%>
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

    var s= "AdapterHTTP?PAGE=CurrEspLavMainPage";
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

      var s= "AdapterHTTP?PAGE=CurrEspLavMainPage";
      s += "&MODULE=M_DeleteEspLav";
      s += "&PRGESPLAVORO=" + prgEspLavoro;

      s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
      s += "&CDNFUNZIONE=<%=_funzione%>";
      s += "&" + getParametersForPatto(prgLavPattoScelta);
      setWindowLocation(s);
    }
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

function controlliForm() {
	if (document.forms[0].codMvCessazione.value=="")
		document.forms[0].strMotCessazione.value="";
	
 	if (document.Frm1.FLGDISPONIBILE.value == 'L' && document.Frm1.ControllaCdn.value==""){
    	alert("Il lavoratore non è in collocamento mirato!");
		return false;
   	}
	return true;
}


  window.top.menu.caricaMenuLav( <%=_funzione%>,   <%=cdnLavoratore%>);
  <%@ include file="../patto/_associazioneXPatto_scripts.inc" %>
</SCRIPT>
  <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
 attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore);  
      %>
</script>
</head>

<!--<body class="gestione" onload="rinfresca(); <%= canModify ? "toggleVisContratto(Frm1.codContratto);toggleVisMotivazioni(Frm1.codMvCessazione);":""%>">-->
<body class="gestione" onload="rinfresca(); toggleVisMotivazioni(document.Frm1.codMvCessazione);">

  <%
    Linguette l = new Linguette( user, _funzione, "CurrEspLavMainPage", new BigDecimal(cdnLavoratore));
    infCorrentiLav.show(out); 
    l.show(out);
  %>
  <script language="javascript">
    var flgInsert = <%=nuovo%>;
  </script>
  <af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="controllaPatto() && controllaDate() && controllaStatoAtto(flgInsert,this) && controlliForm()">
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
          
      <af:list moduleName="M_GETLAVORATOREESPLAV" skipNavigationButton="1"
               canDelete="<%=canDelete ? \"1\" : \"0\"%>" 
               canInsert="<%=canInsert ? \"1\" : \"0\"%>" 
               jsSelect="Select" jsDelete="Delete"
      />
    
    
    <%if(canInsert) {%>
          <br>
          <input type="button" class="pulsanti" onClick="apriNuovoDivLayer(<%=nuovo%>,'divLayerDett','<%=url_nuovo%>');document.location='#aLayerIns';" value="Nuova esperienza"/> 
          <%if(canAssociaMovimenti) {%>
          	 <input type="button" class="pulsanti" onClick="apriMovimenti('AssMov_EspLavPage')" value="Associa movimenti"/>        
   		 <%}%>	  
    <%}%>
    <p/>
    </div>
    
    
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
          <%if(nuovo){%>
            Nuova esperienza
          <%} else {%>
            Esperienza
          <%}%>
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
                  /*for(int i=0;i<tipiMov.size();i++){
                      if (tipiMov.get(i).toString().equalsIgnoreCase("D")){
                          strTipi += "Documentato dal lavoratore";
                      } else
                            if (tipiMov.get(i).toString().equalsIgnoreCase("O")){
                            strTipi += "Da comunicazione obbligatoria";
                            } else
                                strTipi += "Dichiarato";
                      if ((tipiMov.size() > 1) && (i != (tipiMov.size()-1))){
                         strTipi += "/";
                      }
                  }*/
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
        
        <tr>
          <td colspan=2>
            <%@ include file="../patto/_associazioneDettaglioXPatto.inc" %>
          </td>
        </tr>
      </table>
      <%//out.print(htmlStreamBottom);%>
      <br/>
      <center>
        <input type="hidden" name="PAGE" value="CurrEspLavMainPage">
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
<!-- LAYER - END --> 
  </af:form>
</body>

</html>

<% } %>
