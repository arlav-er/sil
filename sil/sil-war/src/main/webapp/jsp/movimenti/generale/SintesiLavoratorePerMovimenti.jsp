<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../../global/noCaching.inc" %>
<%@ include file="../../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.security.ProfileDataFilter,                  
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>




      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
boolean canModify   = false;

//SourceBean row = (SourceBean)serviceResponse.getAttribute("M_GetDescTitoloStudio.ROWS");

//String desTitolo     = StringUtils.getAttributeStrNotNull(row,"ROW.DESCRIZIONE");
//String desTipoTitolo = StringUtils.getAttributeStrNotNull(row,"ROW.TIPOTITOLO");

String cdnLavoratore  = StringUtils.getAttributeStrNotNull(serviceRequest,"CDNLAVORATORElav");
String modTitolo   = StringUtils.getAttributeStrNotNull(serviceRequest,"MODIFICATITOLO");
if (!modTitolo.equals("")) 
{ if ( modTitolo.equalsIgnoreCase("true") ) canModify = true;
  else canModify = false;
}
boolean readOnlyStr = !canModify;

InfCorrentiLav infCorrentiLav= new InfCorrentiLav(sessionContainer, cdnLavoratore, user);
infCorrentiLav.setSkipLista(true);

String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>



<html>
 <head>
    <title>Sintesi lavoratore</title>     
    <%@ include file="../../global/fieldChanged.inc" %>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>
    
    
<SCRIPT TYPE="text/javascript">

</script>

 </head>
 
 <body>
  <br/>
  <p class="titolo">Sintesi lavoratore</p>
<%
  if (cdnLavoratore.compareTo("") != 0) {
    infCorrentiLav.show(out); 
  }
%>
  <br/>
    <table class="main" border="0">
      <tr><td>&nbsp;</td></tr>
      <tr><td>&nbsp;</td></tr>
      <tr><td colspan="2">
          <input class="pulsante" type="button" name="chiudi" value="Chiudi" onClick="javascript:window.close()"/>
          </td>
      </tr>
    </table>
 </body>
</html>