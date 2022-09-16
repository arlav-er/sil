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
       
      function caricadatiLavoratore() {
     

		<%		
		String aggFunz = (String) serviceRequest.getAttribute("AGG_FUNZ");
		String cdnLavoratore  = getMyAttrib4JS(serviceRequest, "cdnLavoratore");
		String strCF          = getMyAttrib4JS(serviceRequest, "STRCODICEFISCALE");
		String strCognome = getMyAttrib4JS(serviceRequest, "STRCOGNOME");
		String strNome = getMyAttrib4JS(serviceRequest, "STRNOME");
		String codMonoTipoRagg = "";
		String flgCfOk = "";
		String fromWhere = (String) serviceRequest.getAttribute("fromWhere");
		if (fromWhere.equals("dettaglio")) {
			codMonoTipoRagg = getMyAttrib4JS(serviceRequest, "CODMONOTIPORAGG");
		%>
		window.opener.<%=aggFunz%>("<%=cdnLavoratore%>","<%=strCF%>","<%=strCognome%>","<%=strNome%>","<%=codMonoTipoRagg%>");	
		<%
		} else if (fromWhere.equals("ricerca")) {
			flgCfOk = getMyAttrib4JS(serviceRequest, "FLGCFOK");;
		%>
		window.opener.<%=aggFunz%>("<%=cdnLavoratore%>","<%=strCF%>","<%=strCognome%>","<%=strNome%>","<%=flgCfOk%>");
		<%
		}
		%>
      }
    </script>
</head>

<body class="gestione" onload="javascript:caricadatiLavoratore();">

</body>
</html>