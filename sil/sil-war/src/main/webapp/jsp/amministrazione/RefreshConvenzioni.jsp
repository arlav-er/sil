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
       
      function caricadatiAzienda() {

		<%		
		String aggFunz = (String) serviceRequest.getAttribute("AGG_FUNZ");
		String prgConv     = getMyAttrib4JS(serviceRequest, "PRGCONV");
		%>
		window.opener.<%=aggFunz%>("<%=prgConv%>");	

      }
    </script>
</head>

<body class="gestione" onload="javascript:caricadatiAzienda();">

</body>
</html>
