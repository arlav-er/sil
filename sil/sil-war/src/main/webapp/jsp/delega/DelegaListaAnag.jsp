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
  boolean canInsert = false;
  boolean canDelete=false;
  
  //cooperazione applicativa
  boolean listaCoop=serviceResponse.containsAttribute("M_COOP_GetLavoratoreIR");
  boolean troppeRighe=serviceResponse.containsAttribute("M_COOP_GetLavoratoreIR.TROPPI_RISULTATI");
  boolean coopAbilitata= false;
  String coopAttiva = System.getProperty("cooperazione.enabled");
  if (coopAttiva != null && coopAttiva.equals("true")) {
  	coopAbilitata = true;
  }
  // seleziono la lista dall'indice regionale solo se e' abilitata la cooperazione
  listaCoop = listaCoop && coopAbilitata;
  
  
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
  
     url="AdapterHTTP?PAGE=DelegaAnagMainPage";
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
  
 </script>
 
</head>
<!--<body  onload="checkError();" class="gestione" > -->
<body  class="gestione" onload="rinfresca();rinfresca_laterale();">
<center>
<font color="red">
    <af:showErrors/>
</font>
</center>
<af:form dontValidate="true">
<%	if (!listaCoop) { %>
<af:list moduleName="M_DynamicRicerca" configProviderClass="it.eng.sil.module.delega.DynRicAnagConfig" canDelete="<%= canDelete ? \"1\" : \"0\" %>" canInsert="<%= canInsert ? \"1\" : \"0\" %>" getBack="true"/>
<% } else { 
		if(!troppeRighe) {

%>
<af:list moduleName="M_COOP_GetLavoratoreIR" canDelete="0" 
											 canInsert="0"											  
											 skipNavigationButton="1" 
											 jsSelect="selectVisualizzaDatiPersonali"/>
<% 		} else { %>
			<br/>
			<!--<center><input class="pulsante" name="torna" value="Torna alla pagina di ricerca" onclick="javascript:history.back();"/></center>-->
			<br/>
<%     } //if (!troppeRighe)
}//if (!listaCoop)%>
<%//out.print(serviceResponse.toXML());%>

<center><input class="pulsante" type = "button" name="torna" value="Torna alla pagina di ricerca" onclick="tornaAllaRicerca()"/></center>
</af:form>
<br/>
</body>
</html>
<%}%>