<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
  com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.User,
  it.eng.afExt.utils.*,
  it.eng.sil.security.ProfileDataFilter,  
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  it.eng.sil.security.PageAttribs,  
  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

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
      canModify = attributi.containsButton("inserisci");
    	
    	if(!canModify){
    		canModify=false;
    	}else{
    		canModify=filter.canEditLavoratore();
    	}      
    }
%>

<%  
  //String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");

  BigDecimal prgCredito=new BigDecimal(0);
  String strSpecifica= "";
  BigDecimal cdnUtIns=new BigDecimal(0);
  Object dtmIns="";
  BigDecimal cdnUtMod=new BigDecimal(0);;
  Object dtmMod="";

  Vector vectConoInfo= serviceResponse.getAttributeAsVector("M_LOADALTCRED.ROWS.ROW");
  if ( (vectConoInfo != null) && (vectConoInfo.size() > 0) ) {

    SourceBean beanLastInsert = (SourceBean)vectConoInfo.get(0);

    prgCredito        = (BigDecimal) beanLastInsert.getAttribute("PRGCREDITO");
    strSpecifica      = StringUtils.getAttributeStrNotNull(beanLastInsert, "STRSPECIFICA");
    cdnUtIns        = (BigDecimal)beanLastInsert.getAttribute("CDNUTINS");
    dtmIns          = beanLastInsert.getAttribute("DTMINS");
    cdnUtMod        = (BigDecimal)beanLastInsert.getAttribute("CDNUTMOD");
    dtmMod          = beanLastInsert.getAttribute("DTMMOD");    
  }
  
  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");

  InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
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
  <title>Altri Crediti</title>

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

  <%@ include file="AltCred_CommonScripts.inc" %>

</head>

<body class="gestione" onload="rinfresca()">

  <%
    infCorrentiLav.show(out); 
    linguette.show(out);
  %>

  <af:form method="POST" action="AdapterHTTP" name="MainForm" id="MainForm" dontValidate="true">

    <input type="hidden" name="PAGE" value="AltCredPage">
    <input type="hidden" name="MODULE" value="M_UpdateAltCred"/>
    <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"%>
    <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>
    <input type="hidden" name="PRGCREDITO" value="<%= prgCredito %>"/>

    <input type="hidden" name="CDNUTINS" value="<%= cdnUtIns %>"/>
    <input type="hidden" name="DTMINS" value="<%= dtmIns %>"/>
    <input type="hidden" name="CDNUTMOD" value="<%= codiceUtenteCorrente %>"/>

    <!--<p class="titolo">Altri Crediti</p>-->

    <center>
      <font color="red">
        <af:showErrors/>
      </font>
    </center>

    <p align="center">

    <table class="main">
      <tr>
        <td/></td>
      </tr>
      <tr>
        <td colspan="2">
          <center>
            <font color="green">
              <af:showMessages prefix="M_UpdateAltCred"/>
            </font>
          </center>
        </td>
      </tr>
      <%@ include file="AltCred_Elemento.inc" %>
    </table>
    <br/>
    <center>
      <% if ( canModify ) { %>    
        <input class="pulsante" type="submit" name="salva" value="Aggiorna">
      <%}%>
      <input class="pulsante" type="button" name="annulla" 
        value="<%= canModify ? "Chiudi senza aggiornare" : "Chiudi" %>"
        onclick="javascript:GoToMainPage();">
    </center>
    <br/>
    <center>
      <% testata.showHTML(out); %>
    </center>
  </af:form>
</body>

</html>
