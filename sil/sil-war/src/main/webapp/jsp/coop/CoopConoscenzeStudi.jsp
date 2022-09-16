
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
   
  it.eng.sil.util.*, 
  it.eng.afExt.utils.StringUtils,
  it.eng.sil.security.ProfileDataFilter,  
  it.eng.sil.module.coop.GetDatiPersonali,
  java.lang.*,
  java.text.*, 
  java.util.*,
  java.math.*,
  it.eng.sil.security.*

"%>

<%
	String cdnLavoratore= "0";//(String )serviceRequest.getAttribute("CDNLAVORATORE");
	String _current_page = (String) serviceRequest.getAttribute("PAGE"); 
    //_current_page="CurrStudiMainPage";
	ProfileDataFilter filter = new ProfileDataFilter(user, _current_page);
	//filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
  	PageAttribs attributi = new PageAttribs(user, _current_page);
	
	boolean canModify = false;
	boolean canDelete = false;
	boolean readOnlyStr = true;

	boolean canView=filter.canView();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}	
    readOnlyStr = !canModify;
%>

<!-- --- NOTE: Gestione Patto
-->
<%@ taglib uri="patto" prefix="pt" %>
<!-- --- -->
<%@ include file="../global/Function_CommonRicercaComune.inc" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
  // --- NOTE: Gestione Patto
  String PRG_TAB_DA_ASSOCIARE = null;
  String COD_LST_TAB = "PR_STU";
  // ---

  //String cdnLavoratore= (String)serviceRequest.getAttribute("CDNLAVORATORE");

  Vector tipiTitoliRows=null;
  tipiTitoliRows= serviceResponse.getAttributeAsVector("M_GETTIPOTITOLI.ROWS.ROW");  

  Vector titoliLavoratoreRows=null;
  titoliLavoratoreRows= serviceResponse.getAttributeAsVector("M_COOP_ConoscenzeStudi_dalla_cache.ROWS.ROW");
  SourceBean row = null;
  SourceBean row_titoliLavoratore= null;
  String prgStudio="";
  String codTitolo="";
  String desTitolo="";
  String codTipoTitolo="";
  String desTipoTitolo="";
  String strSpecifica="";
  String numAnno="";
  String flgPrincipale="";
 
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
  String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE"); 
  /*PageAttribs attributi = new PageAttribs(user, "CurrStudiMainPage");
    boolean canModify = attributi.containsButton("inserisci");
    boolean canDelete = attributi.containsButton("rimuovi");
/////////////////////////
    boolean readOnlyStr = !canModify;*/
  ///////////////////////////
  boolean nuovo = true;
  String apriDiv = "none";
  
%>

<html>
<head>
  <title>Corsi di studio</title>

  <link rel="stylesheet" href="../../css/stiliCoop.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetailCoop.css"/>
  <af:linkScript path="../../js/"/>
  <SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>

  <SCRIPT TYPE="text/javascript">

  // --- NOTE: Gestione Patto
  <%@ include file="../patto/_sezioneDinamica_script.inc"%>

function DettaglioTitolo(prgStudio) {

    var s= "AdapterHTTP?PAGE=CoopConoscenzeStudiDettaglioPage";
    s += "&MODULE=M_GetTitolo";
    s += "&PRGSTUDIO=" + prgStudio;
    s += "&CDNLAVORATORE=<%= cdnLavoratore %>";
    s += "&CDNFUNZIONE=<%= _funzione %>";

    setWindowLocation(s);
  }

</SCRIPT>
</head>
<body class="gestione" onload="rinfresca();">

<%@ include file="_testataLavoratore.inc" %>
<%@ include file="_linguetta.inc" %>



  <af:showErrors />

  <p align="center">
      <af:list moduleName="M_COOP_ConoscenzeStudi_dalla_cache" skipNavigationButton="1"
             canDelete="<%=canDelete ? \"1\" : \"0\"%>" 
             canInsert="<%=canModify ? \"1\" : \"0\"%>" 
             jsSelect="DettaglioTitolo" jsDelete="DeleteTitolo"/>          
  </p>

</body>

</html>
