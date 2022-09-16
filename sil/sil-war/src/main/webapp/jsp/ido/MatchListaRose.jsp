<!-- @author: Stefania Orioli -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,
                it.eng.afExt.utils.*, java.math.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.*,
                it.eng.sil.util.*"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
String prgRichiestaAz = serviceRequest.getAttribute("PRGRICHIESTAAZ").toString();
String prgOrig = serviceRequest.getAttribute("PRGORIG").toString();
String prgAzienda = serviceRequest.getAttribute("PRGAZIENDA").toString();
String prgUnita = serviceRequest.getAttribute("PRGUNITA").toString();
String prgC1 = serviceRequest.getAttribute("C1").toString();
String _page = serviceRequest.getAttribute("PAGE").toString();
//PageAttribs attributi = new PageAttribs(user, _page);

String p_codCpi = user.getCodRif();


int nroMansioni = 0;
String _cdnFunzione = serviceRequest.getAttribute("CDNFUNZIONE").toString();

//SourceBean contStato = (SourceBean) serviceResponse.getAttribute("MATCHSTATORICHORIG");
SourceBean sbStato = (SourceBean) serviceResponse.getAttribute("MATCHSTATORICHORIG.ROWS.ROW");
String cdnStatoRich = StringUtils.getAttributeStrNotNull(sbStato, "CDNSTATORICH");

// Attributi della pagina GestIncrocioPage
PageAttribs attrIncrocio = new PageAttribs(user, "GestIncrocioPage");
boolean gestCopia = attrIncrocio.containsButton("GEST_COPIA");
PageAttribs attributi = new PageAttribs(user, _page);

boolean nuovaRosaNominativa = attributi.containsButton("NUOVA_RNG");
ProfileDataFilter filter = new ProfileDataFilter(user, _page);
if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
}


boolean viewPar = false;
String prgTipoIncrocio = "";
%>
<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <title>Elenco Rose</title>
  <af:linkScript path="../../js/" />
  <script language="Javascript" src="../../js/utili.js" type="text/javascript"></script>

  <script language="Javascript" src="../../js/docAssocia.js"></script>
  
  <script language="Javascript">
    window.top.menu.caricaMenuAzienda(<%=_cdnFunzione%>,<%=prgAzienda%>, <%=prgUnita%>);
  </script>
</head>     

<body class="gestione" onload="rinfresca()">
<%@ include file="InfoCorrRichiesta.inc" %>
<af:list moduleName="MElencoRose"/>

<af:form name="form_match" action="AdapterHTTP" method="POST" dontValidate="true">
<input name="PAGE" type="hidden" value="GestIncrocioPage"/>

<input name="PRGRICHIESTAAZ" type="hidden" value="<%=prgRichiestaAz%>"/>
<input name="PRGAZIENDA" type="hidden" value="<%=prgAzienda%>"/>
<input name="PRGORIG" type="hidden" value="<%=prgOrig%>"/>
<input name="PRGUNITA" type="hidden" value="<%=prgUnita%>"/>
<input name="CERCA" type="hidden" value="cerca"/>
<input name="CDNFUNZIONE" type="hidden" value="<%=_cdnFunzione%>"/>
<input name="DAMATCH" type="hidden" value="1"/>
<table class="main" align="center">
<%if(!cdnStatoRich.equals("4") && !cdnStatoRich.equals("5")) {%>
	<tr>
	  <td align="center">
	  <input type="submit" name="nuovoMatch" class="pulsanti" value="Nuovo Matching"/>
	  </td>
	</tr>
	<tr><td>&nbsp;</td></tr>
<%}%>
<%if(nuovaRosaNominativa && !cdnStatoRich.equals("5")) {%>
	<%
	String txtGoTo = "PAGE=MatchDettRosaPage&MODULE=CRNG&CRNG_MODULE=Yes";
	txtGoTo += "&PRGRICHIESTAAZ=" + prgOrig;
	txtGoTo += "&PRGORIG=" + prgOrig;
	txtGoTo += "&C1=" + prgC1;
	txtGoTo += "&PRGAZIENDA=" + prgAzienda + "&PRGUNITA=" + prgUnita;
	txtGoTo += "&CDNSTATORICH=" + cdnStatoRich + "&CDNFUNZIONE=" + _cdnFunzione;
	txtGoTo += "&CPIROSE=" + p_codCpi;
	txtGoTo += "&P_CDNUTENTE=" + user.getCodut();
	%>
	
	<tr>
	  <td align="center">
	  <input type="button" name="creaRNG" class="pulsanti" value="Nuova Rosa Nominativa Grezza" onClick="goTo('<%=txtGoTo%>')"/>
	  
	  </td>
	</tr>
	
	<tr><td>&nbsp;</td></tr>
	
<%}%>
<tr>
  <td align="center">
      <input type="button" name="Documenti associati" value="Documenti associati" class="pulsante"
             onClick="docAssociatiAzienda('<%= prgAzienda %>', '<%= prgUnita %>', 'MatchListaRosePage', '<%=_cdnFunzione%>', null, '<%=prgOrig%>')">
  </td>
</tr>



</table>
</af:form>
</body>
</html>
