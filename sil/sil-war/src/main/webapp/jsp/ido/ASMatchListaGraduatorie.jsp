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
String prgAzienda = serviceRequest.getAttribute("PRGAZIENDA").toString();
String prgOrig = serviceRequest.getAttribute("PRGORIG").toString();
String prgUnita = serviceRequest.getAttribute("PRGUNITA").toString();
String _page = serviceRequest.getAttribute("PAGE").toString();

int nroMansioni = 0;   
String prgC1 = "";
boolean viewPar = false;
String prgTipoIncrocio = "";

String p_codCpi = user.getCodRif();
String _cdnFunzione = serviceRequest.getAttribute("cdnFunzione").toString();

SourceBean sbStato = (SourceBean) serviceResponse.getAttribute("ASMatchStatoRichOrig.ROWS.ROW");
BigDecimal checkStorico = (BigDecimal) sbStato.getAttribute("checkStorico");  

boolean disableStorico = false;
if (checkStorico.compareTo(new BigDecimal(0)) == 0 ) {   
	disableStorico = true;
}

PageAttribs attrGraduatorie = new PageAttribs(user, "ASGestGraduatoriePage");
boolean gestCopia = attrGraduatorie.containsButton("GEST_COPIA");
boolean docAssociati = attrGraduatorie.containsButton("DOC_ASSOCIATI");
boolean infoStoriche = true;//attrGraduatorie.containsButton("INFO_STORICHE");



// per ora imposto la page a quella della richiesta
_page = "IdoTestataRichiestaPage";
PageAttribs attributi = new PageAttribs(user, _page);

ProfileDataFilter filter = new ProfileDataFilter(user, _page);
if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
}

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
<af:list moduleName="ASElencoGraduatorieModule"/>

<af:form name="form_match" action="AdapterHTTP" method="POST" dontValidate="true">
<input name="PAGE" type="hidden" value="ASGestGraduatoriePage"/>
<input name="PRGRICHIESTAAZ" type="hidden" value="<%=prgRichiestaAz%>"/>
<input name="PRGAZIENDA" type="hidden" value="<%=prgAzienda%>"/>
<input name="PRGUNITA" type="hidden" value="<%=prgUnita%>"/>
<input name="CDNFUNZIONE" type="hidden" value="<%=_cdnFunzione%>"/>
<input name="DAMATCH" type="hidden" value="1"/>
<table class="main" align="center">

<tr>
  <td align="center">
  	<% String txtReturn = "PAGE=IdoTestataRichiestaPage&PRGRICHIESTAAZ="+prgRichiestaAz+"&CDNFUNZIONE="+_cdnFunzione+"&PRGAZIENDA="+prgAzienda+"&PRGUNITA="+prgUnita+"&RET=IdoListaRichiestePage" ;%>
	<input type="button" name="tornaRichiesta" value="Torna alla richiesta" class="pulsante"
            onClick="goTo('<%=txtReturn%>')">  
  </td>
</tr>
<!--
documenti associati
-->
<tr>
	<td align="center" width="100%">	
		<table class="main" align="center" width="100%">
			<tr>
<%
if (docAssociati) {  
%>

			   	
			  <td align="right">
			      <input type="button" name="Documenti associati" value="Documenti associati" class="pulsante"
			             onClick="docAssociatiAzienda('<%= prgAzienda %>', '<%= prgUnita %>', 'ASGestGraduatoriePage', '<%=_cdnFunzione%>', null, '<%=prgOrig%>')">
			  </td>	
<%
}
%>
<%
if (infoStoriche) {
%>
			  <td align="right" width="39%">
			  	  <% 
			  	  String txtInfoStoriche = "PAGE=ASStoricoGraduatoriePage&cdnFunzione="+_cdnFunzione+"&prgAzienda="+prgAzienda+"&prgRichiestaAZ="+prgRichiestaAz+"&prgUnita="+prgUnita+"&PRGORIG="+prgRichiestaAz;
			  	  %>
			  	  <input type="button" name="InfoStoriche" value="Informazioni storiche" class="pulsante<%=((!disableStorico)?"":"Disabled")%>" <%= disableStorico ? "disabled='disable'" : "" %> onClick="goTo('<%=txtInfoStoriche%>')">
			  </td>

<%
}
%>
			</tr>
		</table>
	</td>
</tr>
</table>
</af:form>
</body>
</html>
