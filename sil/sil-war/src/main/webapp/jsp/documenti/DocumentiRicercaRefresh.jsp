<!-- @author: Paolo Roccetti / Luigi Antenucci -->
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
       
      function caricadati() {

		<%  
		// funzione di aggiornamento
		String aggFunz = (String) serviceRequest.getAttribute("AGG_FUNZ");
			
		// Chiavi possibili
		String cdnLavoratore  = getMyAttrib4JS(serviceRequest, "cdnLavoratore");
		String prgAzienda     = getMyAttrib4JS(serviceRequest, "PrgAzienda");
		String prgUnita       = getMyAttrib4JS(serviceRequest, "prgUnita");
		%>
		
		window.opener.<%=aggFunz%>( <%
			if (StringUtils.isFilled(cdnLavoratore)) {
				%>
								"<%=cdnLavoratore%>",
								"<%=getMyAttrib4JS(serviceRequest, "strCodFiscLav")%>",
								"<%=getMyAttrib4JS(serviceRequest, "strCognome")%>",
								"<%=getMyAttrib4JS(serviceRequest, "strNome")%>");
				<%
			} else if (StringUtils.isFilled(prgAzienda)) {
				%>
								"<%=prgAzienda%>",
								"<%=prgUnita%>",
								"<%=getMyAttrib4JS(serviceRequest, "strRagioneSociale")%>",
								"<%=getMyAttrib4JS(serviceRequest, "strIndirizzo")%>",
								"<%=getMyAttrib4JS(serviceRequest, "comuneAzi")%>",
								"<%=getMyAttrib4JS(serviceRequest, "strPartitaIva")%>",
								"<%=getMyAttrib4JS(serviceRequest, "strTel")%>");
				<%
			}
			%>
      }
    </script>
</head>

<body class="gestione" onload="javascript:caricadati();">

</body>
</html>