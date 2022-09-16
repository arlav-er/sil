<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
   
  it.eng.sil.util.*,
  it.eng.afExt.utils.*,
  it.eng.sil.security.ProfileDataFilter, 
  java.lang.*,
  java.text.*, 
  java.util.*,
  java.math.*,
  com.engiweb.framework.security.*,
  it.eng.sil.security.PageAttribs,  
  it.eng.sil.security.User"%>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ include file="../global/Function_CommonRicercaComune.inc" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
	String cdnLavoratore= (String )serviceRequest.getAttribute("CDNLAVORATORE");
	String _current_page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _current_page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
  PageAttribs attributi = new PageAttribs(user, _current_page);
	
	boolean canModify = false;
	boolean canDelete = false;
  boolean readOnlyStr = true;
  boolean readOnly;

	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
      canModify = attributi.containsButton("salva");
      canDelete = attributi.containsButton("RIMUOVI");
    	
    	if(!canModify){
    		canModify=false;
    	}else{
    		canModify=filter.canEditLavoratore();
    	}

    	if(!canDelete){
    		canDelete=false;
    	}else{
    		canDelete=filter.canEditLavoratore();
    	}
      
    }
    readOnlyStr = !canModify;
    readOnly = readOnlyStr;
%>

<%
  // --- NOTE: Gestione Patto
  String PRG_TAB_DA_ASSOCIARE=null;
  String COD_LST_TAB="PR_COR";
  SourceBean row = null;
  // ---

  //String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");
  String strNome       = (String)serviceRequest.getAttribute("LAV_NOME");
  String strCognome    = (String)serviceRequest.getAttribute("LAV_COGNOME");

  Vector vectListaConoscenze = serviceResponse.getAttributeAsVector("M_LISTFORPRO.ROWS.ROW");
  
  Object codiceUtente= sessionContainer.getAttribute("_CDUT_");

  BigDecimal prgCorso=new BigDecimal(0);
  String codCorso="";
  String codTipoCertificato="";
  String descCorso="";  
  String strDescrizione="";
  String strEnte="";
  String flgCompletato="";
  String flgStage="";
  String strContenuto="";
  String codComEnte="";
  String strDescComEnte="";  
  String strLocalitaEnte="";
  String strIndirizzoEnte="";
  BigDecimal numAnno=new BigDecimal(0);
  BigDecimal numMesi=new BigDecimal(0);
  BigDecimal numOre=new BigDecimal(0);
  BigDecimal numOreSpese=new BigDecimal(0);
  String strMotCessazione="";
  String codTipoCorso="";
  BigDecimal cdnAmbitoDisciplinare=new BigDecimal(0);
  String strAmbitoDisciplinare="";
  String strAzienda="";
  String codComAzienda="";
  String strDescComAzienda="";  
  String strLocalitaAzienda="";
  String strIndirizzoAzienda="";
  String strNotaCorso="";
  BigDecimal numOreStage=new BigDecimal(0);
  BigDecimal cdnUtIns=new BigDecimal(0);
  String dtmIns="";
  BigDecimal cdnUtMod=new BigDecimal(0);
  String dtmMod="";

  Vector vectConoInfo= serviceResponse.getAttributeAsVector("M_LOADFORPRO.ROWS.ROW");
  if ( (vectConoInfo != null) && (vectConoInfo.size() > 0) ) {

    SourceBean beanLastInsert = (SourceBean)vectConoInfo.get(0);

    prgCorso           = (BigDecimal)beanLastInsert.getAttribute("PRGCORSO");
    codCorso           = StringUtils.getAttributeStrNotNull(beanLastInsert, "CODCORSO");
    codTipoCertificato = StringUtils.getAttributeStrNotNull(beanLastInsert, "CODTIPOCERTIFICATO");
    descCorso       = StringUtils.getAttributeStrNotNull(beanLastInsert, "STRDESCDECOD");    
    strDescrizione  = StringUtils.getAttributeStrNotNull(beanLastInsert, "STRDESCRIZIONE");
    strEnte         = StringUtils.getAttributeStrNotNull(beanLastInsert, "STRENTE");
    numAnno         = (BigDecimal)beanLastInsert.getAttribute("NUMANNO");
    flgCompletato   = StringUtils.getAttributeStrNotNull(beanLastInsert, "FLGCOMPLETATO");
    flgStage        = StringUtils.getAttributeStrNotNull(beanLastInsert, "FLGSTAGE");
    strContenuto    = StringUtils.getAttributeStrNotNull(beanLastInsert, "STRCONTENUTO");
    codComEnte      = StringUtils.getAttributeStrNotNull(beanLastInsert, "CODCOMENTE");
    strDescComEnte  = StringUtils.getAttributeStrNotNull(beanLastInsert, "STRDESCCOMENTE");
    strLocalitaEnte = StringUtils.getAttributeStrNotNull(beanLastInsert, "STRLOCALITAENTE");
    strIndirizzoEnte= StringUtils.getAttributeStrNotNull(beanLastInsert, "STRINDIRIZZOENTE");
    numAnno         = (BigDecimal)beanLastInsert.getAttribute("NUMANNO");
    numMesi         = (BigDecimal)beanLastInsert.getAttribute("NUMMESI");
    numOre          = (BigDecimal)beanLastInsert.getAttribute("NUMORE");
    numOreSpese     = (BigDecimal)beanLastInsert.getAttribute("NUMORESPESE");
    strMotCessazione= StringUtils.getAttributeStrNotNull(beanLastInsert, "STRMOTCESSAZIONE");
    codTipoCorso    = StringUtils.getAttributeStrNotNull(beanLastInsert, "CODTIPOCORSO");
    cdnAmbitoDisciplinare=(BigDecimal)beanLastInsert.getAttribute("CDNAMBITODISCIPLINARE");
    if (cdnAmbitoDisciplinare != null) {
      strAmbitoDisciplinare = cdnAmbitoDisciplinare.toString();
    }
    strAzienda      = StringUtils.getAttributeStrNotNull(beanLastInsert, "STRAZIENDA");
    codComAzienda   = StringUtils.getAttributeStrNotNull(beanLastInsert, "CODCOMAZIENDA");
    strDescComAzienda  = StringUtils.getAttributeStrNotNull(beanLastInsert, "STRDESCCOMAZIENDA");
    strLocalitaAzienda=StringUtils.getAttributeStrNotNull(beanLastInsert, "STRLOCALITAAZIENDA");
    strIndirizzoAzienda=StringUtils.getAttributeStrNotNull(beanLastInsert, "STRINDIRIZZOAZIENDA");
    strNotaCorso    = StringUtils.getAttributeStrNotNull(beanLastInsert, "STRNOTACORSO");
    numOreStage     = (BigDecimal)beanLastInsert.getAttribute("NUMORESTAGE");
    cdnUtIns        = (BigDecimal)beanLastInsert.getAttribute("CDNUTINS");
    dtmIns          = beanLastInsert.containsAttribute("DTMINS") ? beanLastInsert.getAttribute("DTMINS").toString() : "";
    dtmMod          = beanLastInsert.containsAttribute("DTMMOD") ? beanLastInsert.getAttribute("DTMMOD").toString() : "";
    cdnUtMod        = (BigDecimal)beanLastInsert.getAttribute("CDNUTMOD");
    // --- NOTE: Gestione Patto
    PRG_TAB_DA_ASSOCIARE=prgCorso.toString();
    row = beanLastInsert;
    // ---
  }

  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");

  Testata testata= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
  
  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  Linguette linguette = new Linguette(user, _funzione, _page, new BigDecimal(cdnLavoratore));
  InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);

  /*PageAttribs attributi = new PageAttribs(user, _page);
  boolean canModify= attributi.containsButton("salva");  
  /////////////////////////
    boolean readOnlyStr = !canModify;
  ///////////////////////////*/
  String fieldReadOnly;
  
  if (canModify) {fieldReadOnly="false";}
  else {fieldReadOnly="true";}
  
  /* TODO Commentare 
  it.eng.sil.util.Utils.dumpObject("prgCorso", prgCorso, out);
  if ( (vectConoInfo == null) || (vectConoInfo.size() == 0) )
    out.println("Dati record non presente in request");
  */
%>

<html>

<head>
  <title>Formazione professionale dettaglio</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

  <af:linkScript path="../../js/"/>

  <SCRIPT TYPE="text/javascript">

  // --- NOTE: Gestione Patto
  function getFormObj() {return document.Frm1;}
  <%@ include file="../patto/_associazioneXPatto_scripts.inc" %>
  <%@ include file="../patto/_sezioneDinamica_script.inc"%>
   </SCRIPT>

  <SCRIPT TYPE="text/javascript">
    var flagChanged = false;
  
    function fieldChanged() {
      //alert("field changed !")  
      <% if ( canModify ) { %> 
        flagChanged = true;
      <% } %> 
    }
  
  </SCRIPT>

    <%@ include file="ForPro_CommonScripts.inc" %>

  </head>

  <body class="gestione" onLoad="javascript:DisabilitaCampi_onLoad();">

  <%
    infCorrentiLav.show(out);  
    linguette.show(out);
  %>

  <!-- --- NOTE: Gestione Patto
           aggiungere onSubmit="controllaPatto()" solo se non si ha 
           Salva_onClick(), altrimenti il controllo può essere fatto li 
  -->
  <af:form method="POST" action="AdapterHTTP" name="Frm1" >

    <input type="hidden" name="PAGE" value="ForProPage">
    <input type="hidden" name="MODULE" value="M_UpdateForPro"/>
    <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"%>
    <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>
    <input type="hidden" name="PRGCORSO" value="<%= prgCorso %>"/>
    <!--input type="hidden" name="CODCORSO" value="<%= codCorso %>"/-->
    <input type="hidden" name="CDNUTINS" value="<%= cdnUtIns %>"/>
    <input type="hidden" name="DTMINS" value="<%= dtmIns %>"/>
    <input type="hidden" name="CDNUTMOD" value="<%= codiceUtenteCorrente %>"/>

    <!--<p class="titolo">Formazione professionale</p>-->

    <center>
      <font color="red">
        <af:showErrors/>
      </font>
    </center>

    <p align="center">

    <!--<af:form name="formConoscenzaInfo" method="POST" action="AdapterHTTP">-->
      <table class="main">
        <tr>
          <td/></td>
        </tr>
        <tr>
          <td colspan="2">
            <center>
              <font color="green">
                <af:showMessages prefix="M_UpdateForPro"/>
              </font>
            </center>
          </td>
        </tr>
        <%@ include file="ForPro_Elemento.inc" %>
        <tr>
          <!-- --- NOTE: Gestione Patto
          -->
          <td colspan="2">
            <%@ include file="../patto/_associazioneDettaglioXPatto.inc"%>
          </td>
          <!-- --- -->
        </tr>
      </table>
      <br/>
      <center>
       <% if ( canModify ) { %>
         <input 
          class="pulsante"  
          type="button" 
          name="salva" 
          value="Aggiorna"
          onclick="Salva_onClick();" />
       <%}%>
       
        <input class="pulsante" type="button" name="annulla" 
	value="<%= canModify ? "Chiudi senza aggiornare" : "Chiudi" %>" 
	onclick="javascript:GoToMainPage();">
      </center>
    <!--</af:form>-->
    <center>
      <% testata.showHTML(out);
      %>
    </center>
  </af:form>
</body>

</html>
