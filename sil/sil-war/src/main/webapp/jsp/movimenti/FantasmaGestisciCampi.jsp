<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ taglib uri="aftags" prefix="af"%>

<%@ page import="com.engiweb.framework.base.*,
				it.eng.afExt.utils.*,
				it.eng.sil.security.User,
				java.lang.*"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%!
	private String getMyAttrib4JS(SourceBean request, String attrName) {
		String value = SourceBeanUtils.getAttrStrNotNull(request, attrName);
		value = StringUtils.formatValue4Javascript(value);
		return value;
	}
%>

<html>
<head>
	<af:linkScript path="../../js/" />

    <!--Crea e popola l'oggetto che consente di recuperare i dati della ricerca-->
    <script type="text/javascript">
       
      function caricaTitolo() {
		<% String aggFunz = (String) serviceRequest.getAttribute("AGG_FUNZ"); 
		   String codTipoMov = StringUtils.getAttributeStrNotNull(serviceRequest, "CODTIPOMOV");
		   String titolo = "";
		   String sezTitolo = "";
		   if(codTipoMov.equals("AVV")) { 
		   		titolo = "AVVIAMENTO";
		   		sezTitolo = "";
		   }
		   else titolo = "DATI RAPPORTO";
		   if(codTipoMov.equals("CES")) sezTitolo = "CESSAZIONE";
		   if(codTipoMov.equals("TRA")) sezTitolo = "TRASFORMAZIONE";
		   if(codTipoMov.equals("PRO")) sezTitolo = "PROROGA";
	       
	       if (!titolo.equals("")) {%>
		         window.opener.<%=aggFunz%>("<%=titolo%>","<%=sezTitolo%>");
		         window.close();
		   <%}%>	
	  }
    </script>
</head>

<body class="gestione" onload="javascript:caricaTitolo();">

</body>
</html>
