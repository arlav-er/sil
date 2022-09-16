<%@page import="it.eng.sil.pojo.yg.sap.LavoratoreType"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page
	import="com.engiweb.framework.base.*,com.engiweb.framework.dispatching.module.AbstractModule,com.engiweb.framework.util.QueryExecutor,it.eng.sil.security.User,it.eng.sil.security.ProfileDataFilter,it.eng.afExt.utils.*,it.eng.sil.pojo.yg.sap.*,it.eng.sil.util.*,java.util.*,java.math.*,java.io.*,it.eng.sil.security.PageAttribs,com.engiweb.framework.security.*"%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%
	String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
	String _current_page = (String) serviceRequest.getAttribute("PAGE");
	ProfileDataFilter filter = new ProfileDataFilter(user, _current_page);
	PageAttribs attributi = new PageAttribs(user, _current_page);
	
	PageAttribs attributiSAP = new PageAttribs(user, "SapGestioneServiziPage");
	boolean canCallSAP = attributiSAP.containsButton("RICHIESTA_SAP");
	
    String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);
    
	String codMinSap = (String) serviceResponse.getAttribute("M_ImportaSapCallVerificaEsistenzaSap.CODMINSAP");
	if (codMinSap == null) {
		codMinSap = "0";
	}
%>

<html>

<head>
    <title>Verifica esistenza SAP</title>
    <link rel="stylesheet" type="text/css" href="../../css/stiliCoop.css"/>
    <af:linkScript path="../../js/" />
    
    <SCRIPT TYPE="text/javascript">

    function callSAPMinisteriale() {
       var urlpage="AdapterHTTP?";
       urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&PAGE=SapVisualizzaPage&MODULE=M_SapCallRichiestaSap&CODMINSAP=<%=codMinSap%>";
      
       setWindowLocation(urlpage);
   	}
	
    </SCRIPT>
    
    
</head>

<body class="gestione" onload="rinfresca();reloadSap();">
    <p class="titolo"><br><b>Verifica esistenza SAP</b></p>
	
	<%out.print(htmlStreamTop);%>
    <center>
        <font color="red">
            <af:showErrors/>
        </font>
        <font color="green">
            <af:showMessages prefix="M_ImportaSapCallVerificaEsistenzaSap"/>
        </font>
    </center>
    <%out.print(htmlStreamBottom);%>
    
    <%if (!codMinSap.equals("0")) {
    	if(canCallSAP){ %>				
			<p><center><input class="pulsanti" onclick="callSAPMinisteriale()" type="button" name="richiesta" value="Richiesta SAP"></center></p>
		<%}	
	}%>
	
	<center><input class="pulsante" type = "button" name="chiudi" value="Chiudi" onclick="window.close()"/></center>
</body>

</html>
