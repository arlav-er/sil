<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.*,
  it.eng.afExt.utils.*,
  it.eng.afExt.utils.StringUtils,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af" %>
<%
String _page = (String) serviceRequest.getAttribute("PAGE");
String prgMobilita = (String) serviceRequest.getAttribute("PRGMOBILITAISCR");
boolean canInsertDispo = false;
// NOTE: Attributi della pagina (pulsanti e link) 
PageAttribs attributi = new PageAttribs(user, _page);	 
canInsertDispo = attributi.containsButton("INSERISCI");


String keyLockDispo = "";
SourceBean row = null;

Vector numKloRows = null;
numKloRows = serviceResponse.getAttributeAsVector("M_SELECTNUMKLO.ROWS.ROW");
if(numKloRows.size() == 1){
	row  = (SourceBean) numKloRows.elementAt(0);
	keyLockDispo = Utils.notNull(row.getAttribute("NUMKLOMOBISCR"));
}

%>

<html>
<head>
<af:linkScript path="../../js/" />

<script  language="JavaScript">
	window.opener.document.Frm1.numKloMobIscr.value = "<%=keyLockDispo%>";
	window.opener.document.Frm1.FLGSCHEDA.value = "S";
</script>

</head>



<%if (canInsertDispo) {%>
	<frameset frameborder="YES" rows="60%,40%"  border="0" framespacing="0">
		<frame name="DispoMobilitaSuperiore" title="" src="AdapterHTTP?PAGE=MobListaDisponibilitaPage&PRGMOBILITAISCR=<%=prgMobilita%>" scrolling="auto" frameborder="0">
	  	<frame name="DispoMobilitaInferiore" title="" src="AdapterHTTP?PAGE=MobRicercaDisponibilitaPage&PRGMOBILITAISCR=<%=prgMobilita%>" scrolling="auto" frameborder="0">
	</frameset>
<%}
else {%>
	<frameset frameborder="YES" rows="100%"  border="0" framespacing="0">
		<frame name="DispoMobilitaSuperiore" title="" src="AdapterHTTP?PAGE=MobListaDisponibilitaPage&PRGMOBILITAISCR=<%=prgMobilita%>" scrolling="auto" frameborder="0">
	</frameset>
<%}%>

</html>