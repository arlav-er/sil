<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  it.eng.afExt.utils.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af" %>

<%
  String cdnFunzione = (String) serviceRequest.getAttribute("cdnfunzione");
  //String denominazione = (String) serviceRequest.getAttribute("STRDENOMINAZIONERIC");
  String moduleName = (String )(serviceResponse.containsAttribute("M_PROFNUOVOGRUPPO")? "M_PROFNUOVOGRUPPO":"M_PROFMODIFICAGRUPPO");
  String cdnGruppo = (String) Utils.notNull(serviceResponse.getAttribute(moduleName + ".cdnGruppo"));
//  String flagStandard   = (String) serviceRequest.getAttribute("FLGSTANDARDRIC");  

  //Se l'inserimento è andato bene vado nella pagina della modifica, altrimenti ritorno in quella dell'inserimento
  String mode = StringUtils.getAttributeStrNotNull(serviceResponse, "MODE");
  String inserimentoRiuscito = StringUtils.getAttributeStrNotNull(serviceResponse, "M_ProfNuovoGruppo.INSERT_OK");
  String pageDest = "ProfNuovoGruppoPage";
  if ("new".equalsIgnoreCase(mode) && !"true".equalsIgnoreCase(inserimentoRiuscito)) {
  	pageDest = "ProfInserisciGruppoPage";
  }
%>

<html>
<head>
	<af:linkScript path="../../js/" />
</head>

<body onload="javascript:doFormSubmit(document.Frm1);">
<af:form action="AdapterHTTP?PAGE=<%=pageDest%>" method="POST" name="Frm1" dontValidate="true">
<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>"/>
<input type="hidden" name="CDNGRUPPO" value="<%=cdnGruppo%>"/>
<input type="hidden" name="MODE" value="VIEW"/>
</af:form>
</body>
</html>