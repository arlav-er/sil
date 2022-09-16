<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
 
<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
                  com.engiweb.framework.util.*,
                  it.eng.sil.module.movimenti.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.*,
                  java.util.*,
                  java.text.*,
                  java.math.*,
                  java.io.*,
                  it.eng.afExt.utils.*,
                  com.engiweb.framework.security.*" %>
 
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
boolean canModify   = false;

String codTitolo  = StringUtils.getAttributeStrNotNull(serviceRequest,"CODTIPOTITOLOlav");

String modTitolo   = StringUtils.getAttributeStrNotNull(serviceRequest,"MODIFICATITOLO");
if (!modTitolo.equals("")) 
{ if ( modTitolo.equalsIgnoreCase("true") ) canModify = true;
  else canModify = false;
}
boolean readOnlyStr = !canModify;



String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>



<html>
 <head>
    <!-- ../jsp/movimenti/generale/TitoloStudioPerMovimenti.jsp -->
    <title>Titolo di studio</title>     
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>
    
    
<SCRIPT TYPE="text/javascript">
function aggiornaCodTitolo() { 
	window.opener.document.Frm1.CODTIPOTITOLOlav.value = document.Frm1.CODTITOLO.value;
	window.close();
}
</script>

 </head>
 
 <body>
  <br/><br/>
  <af:form name="Frm1" method="POST" action="AdapterHTTP">
    <%out.print(htmlStreamTop);%>
    <br/>
    <p class="titolo">Titolo di studio</p>
    <table class="main" border="0">
              <tr valign="top">
                <td class="campo" nowrap>
            		<af:comboBox classNameBase="input" name="CODTITOLO" moduleName="M_GetDescTitoloStudio" title="Titolo di studio" addBlank="true" selectedValue="<%=codTitolo%>" disabled="<%=String.valueOf(!canModify)%>"/>
          		</td>
          	 </tr>
      <tr><td>&nbsp;</td></tr>
      <tr><td>&nbsp;</td></tr>
      <tr><td colspan="2">
      <%if(!readOnlyStr) {%>
          <input class="pulsante" type="button" name="aggiorna" value="Aggiorna" onClick="javascript:aggiornaCodTitolo()"/>
      <%}%>
          <input class="pulsante" type="button" name="chiudi" value="Chiudi" onClick="javascript:window.close()"/>
          </td>
      </tr>
    </table>
    <%out.print(htmlStreamBottom);%>
  </af:form>
 </body>
</html>