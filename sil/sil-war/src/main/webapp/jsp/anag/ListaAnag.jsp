<%@ page contentType="text/html;charset=utf-8"%>
 
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

  // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, "AnagRicercaPage");
  boolean canInsert = attributi.containsButton("nuovo");
  boolean canDelete=attributi.containsButton("rimuovi");
  List sezioni = attributi.getSectionList();
  boolean canSAP = sezioni.contains("ACCOUNT_SAP_DA_PORTALE");
  boolean canImportaSAP = false;
  
  //cooperazione applicativa
  boolean listaCoop=serviceResponse.containsAttribute("M_COOP_GetLavoratoreIR");
  boolean troppeRighe=serviceResponse.containsAttribute("M_COOP_GetLavoratoreIR.TROPPI_RISULTATI");
  boolean coopAbilitata= false;
  String coopAttiva = System.getProperty("cooperazione.enabled");
  if (coopAttiva != null && coopAttiva.equals("true")) {
  	coopAbilitata = true;
  }
  //cooperazione applicativa disabilita per il soggetto accreditato
  //verifica esistenza SAP da Portale non viene chiamata per il soggetto accreditato (VerificaEsistenzaSapPortale)
  if (serviceResponse.containsAttribute("M_COOP_GetLavoratoreIR.DISABILITA_COOP_SOGGETTO_ACCRED")) {
	  coopAbilitata = false;  
  }
  
  // seleziono la lista dall'indice regionale solo se e' abilitata la cooperazione
  listaCoop = listaCoop && coopAbilitata;
  
  int numeroLavTrovati = serviceResponse.getAttributeAsVector("M_DynamicRicerca.ROWS.ROW").size();
  if (numeroLavTrovati == 0) {
	numeroLavTrovati = serviceResponse.getAttributeAsVector("M_COOP_GetLavoratoreIR.ROWS.ROW").size();
  }
  
  String numConfigImporta = serviceResponse.containsAttribute("M_CONFIG_IMPORTAZIONE_SAP.ROWS.ROW.NUM")?
			serviceResponse.getAttribute("M_CONFIG_IMPORTAZIONE_SAP.ROWS.ROW.NUM").toString():it.eng.sil.module.movimenti.constant.Properties.DEFAULT_CONFIG;
  if (numConfigImporta.equalsIgnoreCase(it.eng.sil.module.movimenti.constant.Properties.CUSTOM_CONFIG)) {
	canImportaSAP = attributi.containsButton("RICHIESTA_SAP");
  }
  
  String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
 
  String strCodiceFiscale = StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscale");
  String strCognome       = StringUtils.getAttributeStrNotNull(serviceRequest,"strCognome");
  String strNome          = StringUtils.getAttributeStrNotNull(serviceRequest,"strNome");
  String datnasc          = StringUtils.getAttributeStrNotNull(serviceRequest,"datnasc");
  String codComNas        = StringUtils.getAttributeStrNotNull(serviceRequest,"codComNas");
  String strComNas        = StringUtils.getAttributeStrNotNull(serviceRequest,"strComNas");
  String tipoRicerca      = StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRicerca");

%>

<html>
<head>
  <title>Risultati della ricerca</title>
  <%	if (!listaCoop) { %>
				 <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
 				 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
 	<%  } else { %>
				 <link rel="stylesheet" type="text/css" href="../../css/stiliCoop.css"/>
 				 <link rel="stylesheet" type="text/css" href="../../css/listdetailCoop.css"/>
 	<%  } %>
 <af:linkScript path="../../js/"/>

 <script type="text/Javascript">
  function tornaAllaRicerca()
  {  // Se la pagina è già in submit, ignoro questo nuovo invio!
     if (isInSubmit()) return;
  
     url="AdapterHTTP?PAGE=AnagMainPage";
     url += "&CDNFUNZIONE="+"<%=_funzione%>";
     url += "&strCodiceFiscale="+"<%=strCodiceFiscale%>";
     url += "&strCognome="+"<%=strCognome%>";
     url += "&strNome="+"<%=strNome%>";
     url += "&datnasc="+"<%=datnasc%>";
     url += "&codComNas="+"<%=codComNas%>";
     url += "&strComNas="+"<%=strComNas%>";
     url += "&tipoRicerca="+"<%=tipoRicerca%>";
     setWindowLocation(url);
  }

  function caricaDettaglioLavDaOpener(cdnLavoratore)
  {  // Se la pagina è già in submit, ignoro questo nuovo invio!
     if (isInSubmit()) return;
  
     url="AdapterHTTP?PAGE=AnagDettaglioPageAnag";
     url += "&CDNFUNZIONE="+"<%=_funzione%>";
     url += "&MODULE=M_GetLavoratoreAnag";
     url += "&cdnLavoratore=" + cdnLavoratore;
     url += "&APRI_EV=1";
     setWindowLocation(url);
  }  
  
  function selectVisualizzaDatiPersonali(strCodiceFiscale, codProvinciaMaster, strCognome, strNome, comNas, dataNascita, provinciaMaster, tipoMaster) {
	//funzionalità disattivata (per il momento)
	
	// Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;
	url="AdapterHTTP?Page=CoopCaricaDatiPersonaliPage";
  	url+="&strCodiceFiscale="+strCodiceFiscale;
  	url+="&strCognome="+strCognome;
  	url+="&strNome="+strNome;
  	url+="&comNas="+comNas;
  	url+="&dataNascita="+dataNascita;
  	url+="&codProvinciaMaster="+codProvinciaMaster;
  	url+="&provinciaMaster="+provinciaMaster;
  	url+="&tipoMaster="+tipoMaster;
  	url+="&cdnFunzione=140";
  	url+="&CARICA_SCHEDA_DA_POLO_MASTER=1";
  	var w=800; var l=((screen.availWidth)-w)/1.2;
    var h=600; var t=((screen.availHeight)-h)/1.2;
  	var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
  	var titolo = "scheda_lavoratore_pr";
  	var opened = window.open(url, titolo, feat);
  	opened.focus();
  	
  	
  	//alert("Funzionalità non ancora implementata");
  }

  function callImportaSAPWS_Portale(page, idSap, userName) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    url="AdapterHTTP?PAGE=" + page;
    url += "&CDNFUNZIONE="+"<%=_funzione%>";
    url += "&idSap=" + idSap + "&userName="  + userName;
	
	window.open(url,'ImportaSAP_Portale','toolbar=NO,statusbar=YES,width=1200,height=600,top=50,left=100,scrollbars=YES,resizable=YES');
  }

  function callVerificaEsistenzaSAPMinisteriale() {
	    if(!confirm("Sicuro di voler invocare la verifica esistenza SAP?")) {
	        return false;
	    }
	    var urlpage="AdapterHTTP?";
	    urlpage +="CDNFUNZIONE=<%=_funzione%>&PAGE=ImportaSapVerificaEsistenzaPage&MODULE=M_ImportaSapCallVerificaEsistenzaSap&CFLAVORATORE=<%=strCodiceFiscale%>";
	    
	    window.open(urlpage,'VerificaSAP','toolbar=NO,statusbar=YES,width=700,height=600,top=50,left=100,scrollbars=YES,resizable=YES');
	}  
  
 </script>
 
</head>
<!--<body  onload="checkError();" class="gestione" > -->
<body  class="gestione" onload="rinfresca();rinfresca_laterale();">
<p align="center">
	<af:showErrors/>		
</p>

<af:form dontValidate="true">
<%if (!listaCoop) { %>
	<af:list moduleName="M_DynamicRicerca" configProviderClass="it.eng.sil.module.anag.DynRicAnagConfig" canDelete="<%= canDelete ? \"1\" : \"0\" %>" canInsert="<%= canInsert ? \"1\" : \"0\" %>" getBack="true"/>
<%} else { 
		if(!troppeRighe) {

%>
			<af:list moduleName="M_COOP_GetLavoratoreIR" canDelete="<%= canDelete ? \"1\" : \"0\" %>" 
											 canInsert="<%= canInsert ? (canSAP ? \"0\" : \"1\") : \"0\" %>" 
											 skipNavigationButton="1" 
											 jsSelect="selectVisualizzaDatiPersonali"/>
<% 		} //if (!troppeRighe)
		if(canSAP) {%>
			<br/>
			<af:list moduleName="M_SapCallVerificaEsistenzaSapPortale" jsSelect="callImportaSAPWS_Portale" skipNavigationButton="1"/>	
			
<%		}//if (canSap)%>	
<%}//if (!listaCoop)%>

<center><input class="pulsante" type = "button" name="torna" value="Torna alla pagina di ricerca" onclick="tornaAllaRicerca()"/></center>
<%if (!strCodiceFiscale.equals("") && strCodiceFiscale.length() == 16 && canImportaSAP && numeroLavTrovati == 0) {%>
	<p><center><input class="pulsante" type = "button" name="richiestaSAPMin" value="Richiesta SAP Ministeriale" onclick="callVerificaEsistenzaSAPMinisteriale()"/></center></p>
<%}%>
</af:form>
<br/>
</body>
</html>
<%}%>