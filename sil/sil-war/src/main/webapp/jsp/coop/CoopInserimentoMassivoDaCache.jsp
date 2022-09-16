<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../amministrazione/openPage.inc" %>

<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
   
  it.eng.sil.util.*, 
  it.eng.afExt.utils.StringUtils,
  it.eng.sil.security.ProfileDataFilter,    
  java.lang.*,
  java.text.*, 
  java.util.*,
  it.eng.sil.module.coop.GetDatiPersonali,
  java.math.*,
  it.eng.sil.security.*"%>

<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

	String _current_page = (String) serviceRequest.getAttribute("PAGE"); 
	//int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
	/***
	* ProfileDataFilter filter = new ProfileDataFilter(user, _current_page);
	* boolean canView=filter.canView();
	* if (! canView){
	*	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	* }
	*/
	String cdnFunzione, FWpage, target, cdnLavoratore;
	String canUpdateLav = "";
	String operazione = "INSERIMENTO_SCHEDA_LAVORATORE";
	if (!serviceResponse.containsAttribute("M_COOP_InformazioniAssociate.canUpdateLav")) {
		cdnLavoratore = (String)serviceResponse.getAttribute("M_COOP_INSERIMENTOMASSIVODACACHE.cdnLavoratore");
	} else {
		canUpdateLav = (String)serviceResponse.getAttribute("M_COOP_InformazioniAssociate.canUpdateLav");
		if ("true".equals(canUpdateLav)) {		
			cdnLavoratore = (String)serviceResponse.getAttribute("M_COOP_AGGIORNAMENTOMASSIVODACACHE.cdnLavoratore");
			operazione="AGGIORNAMENTO_SCHEDA_LAVORATORE";
		} else {
			cdnLavoratore = null;
		}
	}
	boolean caricaLavoratore = (cdnLavoratore!=null);
	boolean insOrUpdAvvenuto = responseContainer.getErrorHandler().isOK();
	boolean insOrUpdFallito = !insOrUpdAvvenuto;
	boolean ok = insOrUpdAvvenuto && caricaLavoratore;
	if (!ok) {
		cdnLavoratore = "";
		cdnFunzione = (String) serviceRequest.getAttribute("cdnFunzione");
		FWpage = "CoopAnagDatiPersonaliPage";
		target="scheda_lavoratore_pr";
	}
	else {
		cdnFunzione = "1";
		FWpage = "AnagDettaglioPageAnag";
		target="main";		
	}
	// ---------------------------------------------------------------------------------------------------
	// LA SESSIONE VIENE PULITA	
	if (ok) {
		requestContainer.getSessionContainer().delAttribute(GetDatiPersonali.SCHEDA_LAVORATORE_COOP_ID);
	}
	// ---------------------------------------------------------------------------------------------------
 
%>
<html>

<head>
  <title>Inserimento/Aggiornamento massivo scheda lavoratore in cooperazione</title>

  <link rel="stylesheet" href="../../css/stiliCoop.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetailCoop.css"/>
  
  <af:linkScript path="../../js/"/>
  <script>
  	function caricaLavoratore(){
  		var ok = <%=ok%>;
  		if (ok) {
  			document.Frm1.submit();
  			window.close();
  		}
  	}
  	function chiudi(){
		setWindowLocation("AdapterHTTP?page=CoopChiudiSchedaLavoratorePage&cdnFunzione=<%=cdnFunzione%>");
	}
  </script> 

</head>

<body class="gestione" onload="caricaLavoratore()">
	<%if (!ok) {%>
    <%@ include file="_testataLavoratore.inc"%>
  
    <p align="center">  
		<font color="red">
			<af:showMessages prefix="M_COOP_InformazioniAssociate"/>
		</font>
		<font color="green">
 			<af:showMessages prefix="M_COOP_InserimentoMassivoDaCache"/>
 			<af:showMessages prefix="M_COOP_AggiornamentoMassivoDaCache"/> 			
		</font>	
    </p>
	<%}%>
  	<af:form method="POST" action="AdapterHTTP" name="Frm1" target="<%=target%>">
    	<input type="hidden" name="page"          value="<%=FWpage%>">  
    	<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>">  
    	<input type="hidden" name="cdnFunzione"   value="<%=cdnFunzione%>">
    	<%-- necessari alla pagina di anagrafica per l'attivazione del controllo validita' codice fiscale e
    	 generazione messaggio relativo --%>
    	<input type="hidden" name="PROVENIENZA"   value="SCHEDA_LAVORATORE_COOP">
    	<input type="hidden" name="OPERAZIONE"    value="<%=operazione%>">
    	<%if (ok) {%>
    	<%-- apro le evidenze solo se inserisco la scheda, non la apro se la aggiorno --%>
    		<input type="hidden" name="APRI_EV"       value="<%= operazione.equals("INSERIMENTO_SCHEDA_LAVORATORE")?"1":"0"%>">
    		<input type="hidden" name="MODULE"        value="M_GetLavoratoreAnag">
    	<%}%>
    	<%if (!ok) {%>
    	<center>
    		<%if (insOrUpdFallito) {%>
	  			<input type="submit" class="pulsanti" name="indietro" value="Indietro">
	  		<%}else if (!caricaLavoratore) {%>
	  			<input type="button" class="pulsanti" onClick="chiudi()" value="Chiudi">
	  		<%}%>
	  	</center>
  		<%}%>
  </af:form>
  
</body>

</html>


