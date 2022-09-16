<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.security.ProfileDataFilter,
                  it.eng.afExt.utils.*,                  
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  it.eng.sil.util.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String cdnLavoratore= (String )serviceRequest.getAttribute("CDNLAVORATORE");
  
	String cdnLavoratoreEncrypt = EncryptDecryptUtils.encrypt(cdnLavoratore);
	
	String _page = (String) serviceRequest.getAttribute("PAGE"); 
 	int _cdnFunz = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE")); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
  	PageAttribs attributi = new PageAttribs(user, _page);

  	boolean canInsert       = false;

	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
		canInsert = attributi.containsButton("INSERISCI");
	}
   
   	if(cdnLavoratore != null) { 
    	InfCorrentiLav testata= new InfCorrentiLav(RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
     	//testata.setPaginaLista("CollMiratoRisultRicercaPage");
    
    	Linguette l  = new Linguette(user,  _cdnFunz, _page, cdnLavoratore, false);

		String selTutte = StringUtils.getAttributeStrNotNull(serviceRequest,"selTutte");

		String iscrizionePossibile = "false";
		Object iscrPoss = serviceResponse.getAttribute("CM_CHECK_CAN_ISCR.IscrizionePossibile");
		if (iscrPoss != null) {
			iscrizionePossibile = (String) iscrPoss;
		}

%>

<%
		// POPUP EVIDENZE
		String strApriEv = StringUtils.getAttributeStrNotNull(serviceRequest, "APRI_EV");
		int _fun = 1;
		if(_cdnFunz>0) { _fun = _cdnFunz; }
		EvidenzePopUp jsEvid = null;
		boolean bevid = attributi.containsButton("EVIDENZE");
		if(strApriEv.equals("1") && bevid) {
			jsEvid = new EvidenzePopUp(cdnLavoratore, _fun, user.getCdnGruppo(), user.getCdnProfilo());
		}	
%> 

<html>
<head>
<title>Collocamento Mirato</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>

<%	if(strApriEv.equals("1") && bevid) { jsEvid.show(out); }%>

<script>
	   
function nuovaIscr() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;
	
	var url = "AdapterHTTP?PAGE=CMIscrizioniLavoratorePage";		
	url += "&MODULE=CM_GET_DETT_ISCR";
	url += "&cdnLavoratore=<%=cdnLavoratoreEncrypt%>";
	url += "&CDNLAVINCHIARO=<%=cdnLavoratore%>";
	url += "&CDNFUNZIONE=<%=_cdnFunz%>";
	url += "&selTutte=<%=selTutte%>";
		 
	setWindowLocation(url);    
}	

function selAllIscr() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var url = "AdapterHTTP?PAGE=CMIscrizioniLavoratorePage";
    url += "&cdnLavoratore=<%=cdnLavoratore%>";
	url += "&CDNFUNZIONE=<%=_cdnFunz%>";

	<%if (selTutte.equalsIgnoreCase("S")) { %>
		url += "&selTutte=N";
	<%} else {%>
		url += "&selTutte=S";
	<%} %>
 
	setWindowLocation(url);
}
 		
</script>

<script language="Javascript">
<%
    attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore);    
%>
</script>
</head>
<body class="gestione" onload="rinfresca();<%if(strApriEv.equals("1") && bevid) { %> apriEvidenze() <%}%>">
<script language="Javascript">
     window.top.menu.caricaMenuLav( <%=_cdnFunz%> ,  <%=cdnLavoratore%>);
</script>

<%
    testata.show(out);
    l.show(out);
%>

	<table cellpadding="2" cellspacing="10" border="0" width="100%">
		<tr>
			<td class="azzurro_bianco">
				<button type="button" onClick="selAllIscr()" class="ListButtonChangePage">
					<%if (selTutte.equalsIgnoreCase("S")) {%>
						<img src="../../img/filtro.gif" alt="">&nbsp;Iscrizioni non annullate
					<%} else {%>
						<img src="../../img/togli_filtro.gif" alt="">&nbsp;Tutte le iscrizioni
					<%}%>
				</button>
			</td>
		</tr>
	</table>

	<font color="red"><af:showErrors/></font>
	<font color="green">
	 <af:showMessages prefix="CM_SAVE_ISCR"/>
	</font>

	<af:list moduleName="CM_LOAD_ISCRIZIONI" skipNavigationButton="1" />          

  	<%if(iscrizionePossibile.equalsIgnoreCase("false")) {%>
	<center>
    	<table>		   	  
   	  		<tr>
   				<td align="center">
   					<p class="titolo">
						Il lavoratore non risulta nella condizione occupazionale prevista per l'iscrizione !
					</p> 
    			</td>
      		</tr>
    	</table>
  	</center>
  	<%} else if(iscrizionePossibile.equalsIgnoreCase("true") && canInsert) {%>
	<table class="main">  
		<tr>
			<td>
      			<input type="button" class="pulsante" name="Nuovo" value="Nuova iscrizione" onclick="nuovaIscr()"/>
			</td>
		</tr>
	</table>
  	<%}%>

<%}//end if ("cdnLavoratore") 

else {
    %><h3>L'attributo <i>cdnLavoratore</i> non è presente nella serviceRequest</h3>
      <h4>questo rende impossibile visualizzare o memorizzare i dati!</h4><%
}//else ("cdnLavoratore")
%>

</body>
</html>

