<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ page import="com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.*,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  com.engiweb.framework.security.*" %>

<%
boolean fromPattoAzioni = serviceRequest.containsAttribute("PATTO_AZIONI") && serviceRequest.getAttribute("PATTO_AZIONI").equals("true");
//Vector codLstTab = null;
String statoSezioni = "";
String nonFiltrare = "";
if(fromPattoAzioni) {
	//codLstTab = serviceRequest.getAttributeAsVector("COD_LST_TAB");
	statoSezioni = StringUtils.getAttributeStrNotNull(serviceRequest,"statoSezioni");
	nonFiltrare = StringUtils.getAttributeStrNotNull(serviceRequest,"NONFILTRARE");
}
// Savino 19/10/05: modifiche per gestire il ritorno alla pagina di associazione con la presenza oltre della sezione
//      appuntamenti anche quella delle azioni
//if(fromPattoAzioni && codLstTab.equals("")) codLstTab.add("AG_LAV");//In caso di ritorno alla pagina dopo l'inserimento
//if( codLstTab==null) codLstTab = new Vector(0);

String _page = (String) serviceRequest.getAttribute("PAGE");
String _pageProvenienza = (String) serviceRequest.getAttribute("PAGEPROVENIENZA");
int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
String strPrgAzienda = serviceRequest.containsAttribute("PRGAZIENDA")? serviceRequest.getAttribute("PRGAZIENDA").toString():"";
String strPrgUnita = serviceRequest.containsAttribute("PRGUNITA")? serviceRequest.getAttribute("PRGUNITA").toString():"";
String strCdnLavoratore = serviceRequest.containsAttribute("CDNLAVORATORE")? serviceRequest.getAttribute("CDNLAVORATORE").toString():"";
String codCpi = serviceRequest.containsAttribute("CODCPI")? serviceRequest.getAttribute("CODCPI").toString():"";
String codice = serviceRequest.containsAttribute("CODICE")? serviceRequest.getAttribute("CODICE").toString():"";

String data_al = serviceRequest.containsAttribute("DATAAL")? serviceRequest.getAttribute("DATAAL").toString():"";
String data_dal = serviceRequest.containsAttribute("DATADAL")? serviceRequest.getAttribute("DATADAL").toString():"";
String strDirezione = serviceRequest.containsAttribute("DIREZIONE")? serviceRequest.getAttribute("DIREZIONE").toString():"";
String dataDalSlot   = serviceRequest.containsAttribute("dataDalSlot") ? ((String) serviceRequest.getAttribute("dataDalSlot")) : "";
String codServizio = serviceRequest.containsAttribute("CODSERVIZIO") ? ((String) serviceRequest.getAttribute("CODSERVIZIO")) : "";

InfCorrentiLav infCorrentiLav= null;
InfCorrentiAzienda infCorrentiAzienda= null;
User userCurr = (User) sessionContainer.getAttribute(User.USERID);

PageAttribs attributi = new PageAttribs(userCurr, "SOScadAppuntamentoPage");
List listaSezioni = attributi.getSectionList();

if (strCdnLavoratore.compareTo("") != 0) {
    infCorrentiLav= new InfCorrentiLav(sessionContainer, strCdnLavoratore, userCurr);
    
}
else {
  	infCorrentiAzienda = new InfCorrentiAzienda(strPrgAzienda,strPrgUnita);
  	
}

String token = "";
String urlDiLista = "";
boolean abilita = false;
%>
  
<%@ taglib uri="aftags" prefix="af" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title></title>
  <script language="Javascript">

	function setParentWindowLocation(newLocation) {
    	if (isInSubmit()) return;
	    window.parent.frames['ScadSuperiore'].prepareSubmit();
	    prepareSubmit();
		window.parent.location = newLocation;
	}
	
	function DettaglioAppuntamento (prgAppuntamento,CODCPI) {
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;
	
	  document.frmAppuntamenti.CODCPICONTATTO.value = document.frmAppuntamenti.CODCPI.value;
	  document.frmAppuntamenti.PRGAPPUNTAMENTO.value = prgAppuntamento;
	  document.frmAppuntamenti.CODCPI.value = CODCPI;
	  document.frmAppuntamenti.PAGE.value = "SODettaglioAppuntamentoPage";
      doFormSubmit(document.frmAppuntamenti);
    }
    
</script>
</head>
<body class="gestione" onLoad="rinfresca()">

<font color="red">
  <af:showErrors/>
</font>
<font color="green">
</font>
<br/>
<tr><td colspan="2"><hr width="90%"/></td></tr>
<p class="titolo">Appuntamenti presi</p>
<af:form name="frmAppuntamenti" action="AdapterHTTP" method="POST" target="_parent">
<input type="hidden" name="CODCPICONTATTO" value="">
<input type="hidden" name="PRGAPPUNTAMENTO" value="">
<input type="hidden" name="CODCPI" value="<%=codCpi%>">
<input type="hidden" name="prova" value="">
<input type="hidden" name="PAGE" value="">
<input type="hidden" name="PAGEPROVENIENZA" value="<%=_pageProvenienza%>">
<input type="hidden" name="PRGUNITA" value="<%=strPrgUnita%>">
<input type="hidden" name="PRGAZIENDA" value="<%=strPrgAzienda%>">
<input type="hidden" name="CDNLAVORATORE" value="<%=strCdnLavoratore%>">
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
<input type="hidden" name="DATAAL" value="<%=data_al%>">
<input type="hidden" name="DATADAL" value="<%=data_dal%>">
<input type="hidden" name="dataDalSlot" value="<%=dataDalSlot%>">
<input type="hidden" name="codServizio" value="<%=codServizio%>">

<af:list moduleName="M_SOGetAppuntamentiLavoratore"  skipNavigationButton="0"
         jsSelect="DettaglioAppuntamento" />
         
<p class="titolo">
<%if((_pageProvenienza != null) && !_pageProvenienza.equals("") && !_pageProvenienza.equals("null")){%>
    <input type="button" class="pulsanti" name="ANNULLA" value="Chiudi">
<%}%>
</p>
</af:form>
</body>
</html>