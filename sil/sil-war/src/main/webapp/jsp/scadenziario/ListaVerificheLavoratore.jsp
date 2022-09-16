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
  it.eng.sil.security.User,
  it.eng.sil.security.ProfileDataFilter,
  com.engiweb.framework.security.*" %>
  
<%@ taglib uri="aftags" prefix="af" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
String strCdnLavoratore = serviceRequest.getAttribute("CDNLAVORATORE").toString();
String codiceFiscale = StringUtils.getAttributeStrNotNull(serviceRequest,"codiceFiscale");
//Ricavo il tipoGruppo per verificare se l'utente appartiene ad un centro per l'impiego se non lo è
//avviso l'operatore con un messaggio specifico
int tipoGruppo = user.getCdnTipoGruppo();
String codcpi="";
if(tipoGruppo == 1){
	//Ricavo il codcpi dell'utente collegato
	codcpi = user.getCodRif();

}				

boolean hasCodCpi =  !codcpi.equals("");

String strPopup = StringUtils.getAttributeStrNotNull(serviceRequest, "POPUP");
InfCorrentiLav infCorrentiLav= null;
infCorrentiLav = new InfCorrentiLav(sessionContainer, strCdnLavoratore, user);
infCorrentiLav.setSkipLista(true);

String _page = (String) serviceRequest.getAttribute("PAGE");
ProfileDataFilter filter = new ProfileDataFilter(user, _page);
PageAttribs attributi = new PageAttribs(user, "VerLavoratoriPage");
filter.setCdnLavoratore(new BigDecimal(strCdnLavoratore));
boolean canView=filter.canViewLavoratore();
if (! canView){
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
}else{

%>

<html>
<head>
<title>Verifiche Lavoratore</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/" />
<script language="Javascript">
function dettaglioInvioCliclavoro(prg) {
	// Apre la finestra
	var url = "AdapterHTTP?PAGE=CL_INVIO_CANDIDATURA&CODICEFISCALE=<%=codiceFiscale%>"+
	"&CDNLAVORATORE=<%=strCdnLavoratore%>&TIPOLOGIA=1&prgcandidatura="+prg+"&popUp=true";
	window.location.assign(url);
  }
<!--Contiene il javascript che si occupa di aggiornare i link del footer-->
<% 
if (strPopup != null && strPopup.equals("")) {
  attributi.showHyperLinks(out,requestContainer,responseContainer,"cdnLavoratore=" + strCdnLavoratore);
}
%>
</script>
</head>
<% if (strPopup!=null && strPopup.equalsIgnoreCase("true")) {%>
<body>
<%} else {%>
<body class="gestione" onload="rinfresca();">
<%}
infCorrentiLav.show(out); 

//PageAttribs attributi = new PageAttribs(user, "VerLavoratoriPage");
boolean canVerStatoOcc = attributi.containsButton("CAMBIO_STATO_OCCUPAZ_LAV");
boolean canVerMansDispTerr = attributi.containsButton("DISPONIBILITA_TERRITORIO_LAV");
boolean canVerEsclusiRosa = attributi.containsButton("ESCLUSI_ROSA_LAV");
boolean canVerNoMansPronti = attributi.containsButton("PRONTI_INC_NO_MANSIONI");
boolean canVerCandidatureClicLavoro=attributi.containsButton("CANDIDATURE_INVIATE");

boolean bVerificaOk = false;
boolean bCanVerifica = false;

String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);

SourceBean contVer = null;
Vector rows_VectorVer= null;



if (canVerStatoOcc) {

  bCanVerifica = true;
  contVer = (SourceBean) serviceResponse.getAttribute("M_ListaVerAppStatoOccVariato");
  rows_VectorVer = contVer.getAttributeAsVector("ROWS.ROW");
  if (rows_VectorVer.size() > 0) {
		  //**************************
		if (!hasCodCpi) { 
		%>
		  <br>
		  
		  <table class="main" align="center">
		  <tr><td><B>Appuntamenti e stato occupazionale variato </B></td></tr>  
		  </TABLE>
		 
		  <br>
		  <%out.print(htmlStreamTop);%>
		  <p align="center">
		  <table class="lista" align="center">
		  <tr><td align="center"><b>Impossibile trovare le eventuali verifiche poichè l'utente non possiede un codice CPI associato</b></td></tr>
		  </table><p/>
		<%
		  out.print(htmlStreamBottom);
		}else{
		//**************************
    bVerificaOk = true;
  %>
    <af:list moduleName="M_ListaVerAppStatoOccVariato" skipNavigationButton="1"/>
  <%
  }
  }

}









contVer = null;
rows_VectorVer= null;

if (canVerMansDispTerr) {
  bCanVerifica = true;
  contVer = (SourceBean) serviceResponse.getAttribute("M_ListaVerNonDispTerritorio");
  rows_VectorVer = contVer.getAttributeAsVector("ROWS.ROW");
  if (rows_VectorVer.size() > 0) {
    bVerificaOk = true;
  %>
    <af:list moduleName="M_ListaVerNonDispTerritorio" skipNavigationButton="1"/>
  <%
  }
}

contVer = null;
rows_VectorVer= null;

if (canVerEsclusiRosa) {
  bCanVerifica = true;
  contVer = (SourceBean) serviceResponse.getAttribute("M_ListaVerEsclusiRosa");
  rows_VectorVer = contVer.getAttributeAsVector("ROWS.ROW");
  if (rows_VectorVer.size() > 0) {
    bVerificaOk = true;
  %>
    <af:list moduleName="M_ListaVerEsclusiRosa" skipNavigationButton="1"/>
  <%
  }
}

contVer = null;
rows_VectorVer= null;

if (canVerEsclusiRosa) {
  bCanVerifica = true;
  contVer = (SourceBean) serviceResponse.getAttribute("M_ListaVerProntoIncNoMans");
  rows_VectorVer = contVer.getAttributeAsVector("ROWS.ROW");
  if (rows_VectorVer.size() > 0) {
    bVerificaOk = true;
  %>
    <af:list moduleName="M_ListaVerProntoIncNoMans" skipNavigationButton="1"/>
  <%
  }
}

if (canVerCandidatureClicLavoro){
	bCanVerifica = true;
	contVer = (SourceBean) serviceResponse.getAttribute("M_CL_LIST_INVII_CANDIDATURE");
	  rows_VectorVer = contVer.getAttributeAsVector("ROWS.ROW");
	  if (rows_VectorVer.size() > 0) {
	    bVerificaOk = true;
	  %>
	  	<center>
		<p class="titolo">Candidature a Cliclavoro</p>
		</center>
	    <af:list moduleName="M_CL_LIST_INVII_CANDIDATURE" skipNavigationButton="1" jsSelect="dettaglioInvioCliclavoro" />
	  <%
	  }
	
}

contVer = null;
rows_VectorVer = null;

if (!bCanVerifica) {
%>
  <br>
  <%out.print(htmlStreamTop);%>
  <p align="center">
  <table class="lista" align="center">
  <tr><td align="center"><b>Operatore non abilitato a visualizzare le verifiche del lavoratore</b></td></tr>
  </table><p/>
<%
  out.print(htmlStreamBottom);
}
else {
  if (!bVerificaOk) {
  %>
    <br>
    <%out.print(htmlStreamTop);%>
    <p align="center">
    <table class="lista" align="center">
    <tr><td align="center"><b>Non ci sono verifiche per il lavoratore</b></td></tr>
    </table><p/>
  <%
    out.print(htmlStreamBottom);
  }
}



if (strPopup!=null && strPopup.equalsIgnoreCase("true")) {%>
<center>
<table><tr><td align="center">
<input type="button" class="pulsanti" name="buttChiudi" value="Chiudi" onClick="javascript:window.close();">
</td></tr></table>
</center>
<%}%>
</body>
</html>

<% } %>