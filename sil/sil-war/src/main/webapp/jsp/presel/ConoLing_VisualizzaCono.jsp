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
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
	String cdnLavoratore= (String )serviceRequest.getAttribute("CDNLAVORATORE");
	String _current_page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _current_page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
  PageAttribs attributi = new PageAttribs(user, _current_page);
	
	boolean canModify = false;

	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
      canModify = attributi.containsButton("salva");
    	
    	if(!canModify){
    		canModify=false;
    	}else{
    		canModify=filter.canEditLavoratore();
    	}
      
    }
%>

<%
/*
	RequestContainer rc = getRequestContainer(request);	
	SessionContainer sessionContainer;
	
	if ( rc != null ) {
		sessionContainer = rc.getSessionContainer();
	} else {
		rc = (RequestContainer)session.getAttribute("REQUEST_CONTAINER");
		sessionContainer = rc.getSessionContainer();
	}

  //prelevo la response e la request
  ResponseContainer responseContainer=ResponseContainerAccess.getResponseContainer(request);
  SourceBean serviceResponse = responseContainer.getServiceResponse();

  RequestContainer requestContainer = RequestContainerAccess.getRequestContainer(request);
  SourceBean serviceRequest= requestContainer.getServiceRequest();
*/
  //String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");
  String strNome       = (String)serviceRequest.getAttribute("LAV_NOME");
  String strCognome    = (String)serviceRequest.getAttribute("LAV_COGNOME");

  Vector vectListaConoscenze = serviceResponse.getAttributeAsVector("M_LISTCONOSCENZELING.ROWS.ROW");
  
  Object codiceUtente= sessionContainer.getAttribute("_CDUT_");

  BigDecimal prgLingua=new BigDecimal(0);;
  String codLingua= "";
  BigDecimal cdnGradoLetto=new BigDecimal(0);
  String strGradoLetto="";
  BigDecimal cdnGradoScritto=new BigDecimal(0);
  String strGradoScritto="";
  BigDecimal cdnGradoParlato=new BigDecimal(0);
  String strGradoParlato="";
  String flgCertificato="";
  String flgPrimaLingua="";  
  String codModLingua="";
  String strModLingua="";  
  BigDecimal cdnUtIns=new BigDecimal(0);
  String dtmIns="";
  BigDecimal cdnUtMod=new BigDecimal(0);;
  String dtmMod="";
  String newCdnGradoLetto="";
  String newCdnGradoScritto="";
  String newCdnGradoParlato="";
  Vector vectConoInfo= serviceResponse.getAttributeAsVector("M_LOADCONOSCENZALING.ROWS.ROW");
  if ( (vectConoInfo != null) && (vectConoInfo.size() > 0) ) {

    SourceBean beanLastInsert = (SourceBean)vectConoInfo.get(0);

    prgLingua       = (BigDecimal)beanLastInsert.getAttribute("PRGLINGUA");
    codLingua       = (String)beanLastInsert.getAttribute("CODLINGUA");
    cdnGradoLetto   = (BigDecimal)beanLastInsert.getAttribute("CDNGRADOLETTO");
    cdnGradoScritto = (BigDecimal)beanLastInsert.getAttribute("CDNGRADOSCRITTO");
    cdnGradoParlato = (BigDecimal)beanLastInsert.getAttribute("CDNGRADOPARLATO");
    flgCertificato  = StringUtils.getAttributeStrNotNull(beanLastInsert, "FLGCERTIFICATO");
    flgPrimaLingua  = StringUtils.getAttributeStrNotNull(beanLastInsert, "FLGPRIMALINGUA");    
    codModLingua    = StringUtils.getAttributeStrNotNull(beanLastInsert, "CODMODLINGUA");
    strModLingua    = StringUtils.getAttributeStrNotNull(beanLastInsert, "STRMODLINGUA");
    cdnUtIns        = (BigDecimal)beanLastInsert.getAttribute("CDNUTINS");
    dtmIns          = (String)beanLastInsert.getAttribute("DTMINS");
    cdnUtMod        = (BigDecimal)beanLastInsert.getAttribute("CDNUTMOD");
    dtmMod          = (String)beanLastInsert.getAttribute("DTMMOD");
  }

  if (cdnGradoLetto != null) {
    strGradoLetto = cdnGradoLetto.toString();
  }

  if (cdnGradoScritto != null) {
    strGradoScritto = cdnGradoScritto.toString();
  }

  if (cdnGradoParlato != null) {
    strGradoParlato = cdnGradoParlato.toString();
  }
  
  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");

  Testata testata= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);

  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  Linguette linguette = new Linguette( user, _funzione, _page, new BigDecimal(cdnLavoratore));

  /*
  PageAttribs attributi = new PageAttribs(user, _page);
  boolean canModify= attributi.containsButton("salva");
  */

  String fieldReadOnly;
  
  if (canModify) {fieldReadOnly="false";}
  else {fieldReadOnly="true";}

%>

<html>

<head>
  <title>Conoscenza Informatica</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

  <af:linkScript path="../../js/"/>

  <SCRIPT TYPE="text/javascript">
    var flagChanged = false;
  
    function fieldChanged() {
      //alert("field changed !")      
      <% if ( canModify ) { %> 
        flagChanged = true;
      <% } %> 
    }
  </SCRIPT>

  <%@ include file="ConoLing_CommonScripts.inc" %>

</head>

<body class="gestione">

  <%
    linguette.show(out);
  %>

  <af:form method="POST" action="AdapterHTTP" name="MainForm" id="MainForm" dontValidate="true">

    <input type="hidden" name="PAGE" value="ConoscenzeLingPage">
    <input type="hidden" name="MODULE" value="M_UpdateConoscenzaLing"/>
    <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"%>
    <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>
    <input type="hidden" name="PRGLINGUA" value="<%= prgLingua %>"/>

    <input type="hidden" name="CDNUTINS" value="<%= cdnUtIns %>"/>
    <input type="hidden" name="DTMINS" value="<%= dtmIns %>"/>
    <input type="hidden" name="CDNUTMOD" value="<%= codiceUtenteCorrente %>"/>

    <!--<p class="titolo">Conoscenza Informatica</p>-->

    <center>
      <font color="red">
        <af:showErrors/>
      </font>
    </center>

    <p align="center">

    <af:form name="formConoscenzaInfo" method="POST" action="AdapterHTTP">
      <table class="main">
        <tr>
          <td/></td>
        </tr>
        <tr>
          <td colspan="2">
            <center>
              <font color="green">
                <af:showMessages prefix="M_UpdateConoscenzaLing"/>
              </font>
            </center>
          </td>
        </tr>
        <%@ include file="ConoLing_Elemento.inc" %>
      </table>
      <br/>
      <center>
      <% if ( canModify ) { %>      
        <input class="pulsante" type="submit" name="salva" value="Aggiorna">
      <%}%>
      <input class="pulsante" type="button" name="annulla" 
        value="<%= canModify ? "Chiudi senza aggiornare" : "Chiudi" %>" 
        onclick="GoToMainPage();">
      </center>
    </af:form>
    <center>
      <% testata.showHTML(out); %>
    </center>
  </af:form>
</body>

</html>
