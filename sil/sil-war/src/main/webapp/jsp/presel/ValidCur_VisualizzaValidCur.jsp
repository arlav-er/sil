<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
   
  it.eng.sil.util.*,
  it.eng.afExt.utils.*,
  java.lang.*,
  java.text.*, 
  java.util.*,
  java.math.*,
  com.engiweb.framework.security.*,
  com.engiweb.framework.dbaccess.sql.TimestampDecorator,  
  it.eng.sil.security.PageAttribs,   
  it.eng.sil.security.User  
"%>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
  String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");
  
  Object codiceUtente= sessionContainer.getAttribute("_CDUT_");

  String modalita="EDIT";   //Indica se nell'include VaidCur_elemetno.inc si deve bloccare o meno la select list
  String codTipoValidita      = "";
  String datInizioCurr        = "";  
  String datFineCurr          = "";
  //TimestampDecorator datInizioCurrTime ;  
  //TimestampDecorator datFineCurrTime ;

  String strDescValidita      = "";
  //String strStatoCurriculum   = "";
  String codStatoLav     = "";
  BigDecimal cdnUtIns=new BigDecimal(0);
  Object dtmIns="";
  BigDecimal cdnUtMod=new BigDecimal(0);;
  Object dtmMod="";

  Vector vectConoInfo= serviceResponse.getAttributeAsVector("M_LOADVALIDCUR.ROWS.ROW");
  if ( (vectConoInfo != null) && (vectConoInfo.size() > 0) ) {

    SourceBean beanLastInsert = (SourceBean)vectConoInfo.get(0);

    codTipoValidita     = (String)beanLastInsert.getAttribute("CODTIPOVALIDITA");
    datInizioCurr    = (String)beanLastInsert.getAttribute("DATINIZIOCURR");
    datFineCurr      = (String)beanLastInsert.getAttribute("DATFINECURR");
    //strStatoCurriculum  = StringUtils.getAttributeStrNotNull(beanLastInsert, "STRSTATOCURRICULUM");
    codStatoLav     = StringUtils.getAttributeStrNotNull(beanLastInsert, "CODSTATOLAV");
    cdnUtIns        = (BigDecimal)beanLastInsert.getAttribute("CDNUTINS");
    dtmIns          = beanLastInsert.getAttribute("DTMINS");
    cdnUtMod        = (BigDecimal)beanLastInsert.getAttribute("CDNUTMOD");
    dtmMod          = beanLastInsert.getAttribute("DTMMOD");
    strDescValidita = (String)beanLastInsert.getAttribute("STRDESCVALIDITA");
  }
  
  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");

  Testata testata= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);

  String _page = (String) serviceRequest.getAttribute("START_PAGE"); 
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  //Linguette linguette = new Linguette( user, _funzione, _page, new BigDecimal(cdnLavoratore));

  PageAttribs attributi = new PageAttribs(user, _page);
  boolean canModify= attributi.containsButton("salva");
  String fieldReadOnly;
  
  if (canModify) {fieldReadOnly="false";}
  else {fieldReadOnly="true";}

%>

<html>

<head>
  <title>Validit&agrave Curriculum</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">

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

  <%@ include file="ValidCur_CommonScripts.inc" %>

</head>

<body class="gestione">

  <%
    //linguette.show(out);
  %>

  <af:form method="POST" action="AdapterHTTP" name="Frm1">

    <input type="hidden" name="PAGE" value="ValidCurDettPage">
    <input type="hidden" name="MODULE" value="M_UpdateValidCur"/>
    <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"%>
    <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>

    <input type="hidden" name="CDNUTINS" value="<%= cdnUtIns %>"/>
    <input type="hidden" name="DTMINS" value="<%= dtmIns %>"/>
    <input type="hidden" name="CDNUTMOD" value="<%= codiceUtenteCorrente %>"/>
    <input type="hidden" name="START_PAGE" value="<%= _page %>"/>

    <!--<p class="titolo">Conoscenza Informatica</p>-->

    <center>
      <font color="red">
        <af:showErrors/>
      </font>
    </center>

    <p align="center">

      <table class="main">        
        <tr>
          <td colspan="2">
            <center>
              <font color="green">
                <af:showMessages prefix="M_UpdateValidCur"/>
              </font>
            </center>
          </td>
        </tr>
        <%@ include file="ValidCur_Elemento.inc" %>
        
      </table>
      <br/>    
      <center>
      <% if ( canModify ) { %>      
        <input class="pulsante" type="button" name="salva" value="Aggiorna" onClick="javascript:if ( obbligatori() ) {controllaDate();}">
      <%}%>
      <input class="pulsante" type="button" name="annulla" 
        value="<%= canModify ? "Chiudi senza aggiornare" : "Chiudi" %>" 
        onclick="GoToMainPage();">
      </center>
    <center>
      <% testata.showHTML(out); %>
    </center>
  </af:form>
</body>

</html>
