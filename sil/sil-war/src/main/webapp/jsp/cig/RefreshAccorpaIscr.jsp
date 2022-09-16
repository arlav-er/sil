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
       
      function caricadatiIscr() {

		<%		
		String n   = getMyAttrib4JS(serviceRequest, "numero");
		String aggFunz = (String) serviceRequest.getAttribute("AGG_FUNZ");
		String prgAzienda     = getMyAttrib4JS(serviceRequest, "prgAzienda");
		String prgUnita       = getMyAttrib4JS(serviceRequest, "prgUnita");
		String datInizio          = getMyAttrib4JS(serviceRequest, "datInizio");
		String datFine        = getMyAttrib4JS(serviceRequest, "datFine");
		String strragionesociale      = getMyAttrib4JS(serviceRequest, "ragSoc");
		String cf      = getMyAttrib4JS(serviceRequest, "cf");
		String codAccordo   = getMyAttrib4JS(serviceRequest, "codAccordo");
		String descrStato   = getMyAttrib4JS(serviceRequest, "descrStato");
		String prgaltraiscr   = getMyAttrib4JS(serviceRequest, "prgaltraiscr");
		String tipoIscr   = getMyAttrib4JS(serviceRequest, "tipoIscr");
		
		
		%>
		window.opener.<%=aggFunz%>("<%=n%>","<%=prgAzienda%>","<%=prgUnita%>","<%=datInizio%>","<%=datFine%>","<%=strragionesociale%>","<%=cf%>",
									"<%=tipoIscr%>","<%=codAccordo%>","<%=descrStato%>","<%=prgaltraiscr%>");	

      }
    </script>
</head>

<body class="gestione" onload="javascript:caricadatiIscr();">

</body>
</html>