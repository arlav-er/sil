<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
  	String _page = (String) serviceRequest.getAttribute("PAGE"); 
  	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
  
	if (! filter.canView()){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{


    PageAttribs attributi = new PageAttribs(user, "ListaDichRedPage");

	String cdnLavoratore= (String )serviceRequest.getAttribute("CDNLAVORATORE");
    String cdnFunzione=(String)serviceRequest.getAttribute("CDNFUNZIONE");
	String prgDichLav = serviceRequest.containsAttribute("prgDichLav")?serviceRequest.getAttribute("prgDichLav").toString():"";
	String codStatoAtto = serviceRequest.containsAttribute("codStatoAtto")?serviceRequest.getAttribute("codStatoAtto").toString():"";
	String motivazione = serviceRequest.containsAttribute("motivazione")?serviceRequest.getAttribute("motivazione").toString():"";
	boolean canInsert = false;
	boolean flag_competenza = true;
	
    Vector rowCpIComp  = serviceResponse.getAttributeAsVector("M_GETCPICORR.ROWS.ROW");
    if(rowCpIComp != null && !rowCpIComp.isEmpty()) {
      	SourceBean firstrow = (SourceBean) rowCpIComp.elementAt(0);
        String codCPItit  = (String) firstrow.getAttribute("CODCPITIT");
        String codmonotipocpi = Utils.notNull(firstrow.getAttribute("CODMONOTIPOCPI"));
        String codCpiUser = Utils.notNull(user.getCodRif());
        if (!codmonotipocpi.equals("C") || !codCPItit.equals(codCpiUser)){
			flag_competenza = false;
        }
    }
    
    canInsert=attributi.containsButton("inserisci") && flag_competenza;
    String strErrorCode = "";
    String msgConferma = "";
    boolean confirm = false;
    SourceBean sbError = (SourceBean) serviceResponse.getAttribute("AnnullaDichiarazione.RECORD.PROCESSOR.ERROR");
    String forzaRicostruzione = "false";
    String continuaRicalcolo = "false";
    Vector rowsParam = null;
    SourceBean valoriParametri = null;
    if (sbError != null) {
    	strErrorCode = sbError.getAttribute("code").toString();
    	msgConferma = sbError.getAttribute("messagecode").toString();
    	msgConferma = msgConferma + " Vuoi proseguire?";
    	rowsParam = serviceResponse.getAttributeAsVector("AnnullaDichiarazione.RECORD.PROCESSOR.CONFIRM.PARAM");
    	confirm = true;
    	if (rowsParam != null && rowsParam.size() > 0) {
    		valoriParametri = (SourceBean) rowsParam.get(0);
	    	forzaRicostruzione = valoriParametri.getAttribute("value").toString();
	    	valoriParametri = (SourceBean) rowsParam.get(1);
	    	continuaRicalcolo = valoriParametri.getAttribute("value").toString();
	   	}
    }
%>


<script>
 function ripetiOperazione() {
	document.Frm1.MODULE.value = "AnnullaDichiarazione";
	doFormSubmit(document.Frm1);
 }

  function go(url, alertFlag) {
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;
  
  var _url = "AdapterHTTP?" + url;
  if (alertFlag == 'TRUE' ) {
    if (confirm('Confermi operazione')) {
      setWindowLocation(_url);
    }
  }
  else {
    setWindowLocation(_url);
  }
}
</script>

<html>
<head>
<title></title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 <af:linkScript path="../../js/" />
</head>

<body onload="rinfresca()">
<af:form name="Frm1" method="POST" action="AdapterHTTP">
<input type="hidden" name="FORZA_INSERIMENTO" value="<%=forzaRicostruzione%>">
<input type="hidden" name="CONTINUA_CALCOLO_SOCC" value="<%=continuaRicalcolo%>">
<input type="hidden" name="MODULE" value=""/>
<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"/>
<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>"/>
<input type="hidden" name="PAGE" value="ListaDichRedPage"/>
<input type="hidden" name="prgDichLav" value="<%=prgDichLav%>"/>
<input type="hidden" name="codStatoAtto" value="<%=codStatoAtto%>"/>
<input type="hidden" name="motivazione" value="<%=motivazione%>"/>
<%
    if (cdnLavoratore != null) {         
        InfCorrentiLav _testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
        _testata.setSkipLista(true);
        _testata.show(out);
	}
%>
<af:list moduleName="M_ListaDichRed" />
    <center>
	<%if (canInsert && cdnLavoratore !=null){%>
         <input type="button" class="pulsanti"  name = "inserisciNuovo" value="Nuova dichiarazione" onclick="go('PAGE=SanareSituazionePrecPage&cdnFunzione=<%=cdnFunzione%>&cdnLavoratore=<%=cdnLavoratore%>', 'FALSE')">
	<%}%>
	<%if(cdnLavoratore==null) {%>
      <input type="button" class="pulsanti"  name = "cerca" value="Torna alla ricerca" onclick="go('PAGE=RicercaDichRedPage&cdnFunzione=<%=cdnFunzione%>', 'FALSE')">
    <%}%>
    </center>
<br>
<%
String descError = "";
String msg = "";
SourceBean row_Dich = (SourceBean)serviceResponse.getAttribute("AnnullaDichiarazione.ROW");
if (row_Dich != null) {
 	//Errori
 	if (row_Dich.containsAttribute("Error")) {
		descError = StringUtils.getAttributeStrNotNull(row_Dich, "Error");
	}
}
if (!descError.equals("")) {
	msg = "Operazione fallita.\\n\\r";
	msg += descError;
	%>
	<script language="javascript">
		alert ("<%=msg%>");
	</script>
<%}%>
</af:form>
<%if (confirm) {%>
	<script language="Javascript">
		if (confirm("<%=msgConferma%>")) { 
			ripetiOperazione();
		}
		else {
			document.Frm1.FORZA_INSERIMENTO.value = "false";
			document.Frm1.CONTINUA_CALCOLO_SOCC.value = "false";
		}
	</script>
<%}%>
</body>
</html>
<%}%>