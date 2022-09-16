<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="com.engiweb.framework.base.*,
				java.util.*,
				it.eng.sil.security.*,
				it.eng.sil.util.*,
				it.eng.afExt.utils.*" %>
				
				
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%
	String prgRichiestaAz= (String) serviceRequest.getAttribute("prgRichiestaAz");
    String prgTipoIncrocio=(String) serviceRequest.getAttribute("prgTipoIncrocio");
    String descCpi= (String) serviceRequest.getAttribute("descCpi");
	String _page = (String) serviceRequest.getAttribute("PAGE"); 
	String _cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
	String prgAzienda = (String) serviceRequest.getAttribute("PRGAZIENDA");
	String prgUnita = (String) serviceRequest.getAttribute("PRGUNITA");
	
	String prgRosa = (String) serviceRequest.getAttribute("PRGROSA");
	String prgTipoRosa = (String) serviceRequest.getAttribute("PRGTIPOROSA");
	
	String codCpi = (String) serviceRequest.getAttribute("codCpi");
	String ConcatenaCpi = (String) serviceRequest.getAttribute("ConcatenaCpi");
	
	String pageLink = "ASMatchDettGraduatoriaPage";
	String moduleLink = "ASCandidatiGraduatoria";
	String tipoStampa = "ALSEVO";
	if (("10").equalsIgnoreCase(prgTipoIncrocio) || ("11").equalsIgnoreCase(prgTipoIncrocio) || ("12").equalsIgnoreCase(prgTipoIncrocio)) {
		pageLink = "CMMatchDettGraduatoriaPage";
		moduleLink = "CMCandidatiGraduatoria";
		tipoStampa = "ALL68O";
	}
	
	PageAttribs attributi = new PageAttribs(user,_page);
    
	//Ho messo true per i test
    //boolean stampaPos = false;
	//boolean stampaAlf = false;
	boolean stampaPos = true;
	boolean stampaAlf = true;
	
	//stampaPos = attributi.containsButton("STAMPA_INTERNA_POS");
    //stampaAlf = attributi.containsButton("STAMPA_INTERNA_ALF");	
	
	String posizione="";
	
	String htmlStreamTop = StyleUtils.roundTopTable(true);
	String htmlStreamBottom = StyleUtils.roundBottomTable(true);
	
%>

<html>
<head>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<af:linkScript path="../../js/"/>

<% String queryString = null; %>
<%@ include file="../documenti/_apriGestioneDoc.inc"%>
    
<script language="JavaScript">

function Stampa(posizione,tipo){
    if (tipo=="list")
		apriGestioneDoc('RPT_GRADUATORIA_DEF', '&tipo=list&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>&codCpi=<%=codCpi%>&ConcatenaCpi=<%=ConcatenaCpi%>','ALL68O')
	else
		apriGestioneDoc('RPT_GRADUATORIA_DEF', '&tipo=grad&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>&codCpi=<%=codCpi%>&ConcatenaCpi=<%=ConcatenaCpi%>','ALL68O')
}

function stampa(){
		apriGestioneDoc('RPT_GRADUATORIA_DEF','&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>&ConcatenaCpi=<%=ConcatenaCpi%>&codCpi=<%=codCpi%>','ALL68O')
	}




function indietro() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

     url="AdapterHTTP?PAGE="+"<%=pageLink%>";  
     url += "&MODULE="+"<%=moduleLink%>";
     url += "&PRGRICHIESTAAZ="+"<%=prgRichiestaAz%>";
     url += "&PRGTIPOINCROCIO="+"<%=prgTipoIncrocio%>";
     url += "&CDNFUNZIONE="+"<%=_cdnFunzione%>";
     url += "&PRGAZIENDA="+"<%=prgAzienda%>";
     url += "&PRGUNITA="+"<%=prgUnita%>";
     url += "&PRGROSA="+"<%=prgRosa%>";
     url += "&PRGTIPOROSA="+"<%=prgTipoRosa%>";
      url += "&codCpi="+"<%=codCpi%>";
     url += "&ConcatenaCpi="+"<%=ConcatenaCpi%>";
     
     setWindowLocation(url);
 }

</script>
</head>
<body class="gestione" onLoad="rinfresca();">
<br/>

<af:form name="form1" method="POST" action="AdapterHTTP">
<%= htmlStreamTop %>



<table class="main" cellpadding="2" cellspacing="2" width="100%">
	<tr>
      <td style="text-align: center;" colspan="2" rowspan="1"><p class="titolo">Elenco stampe definitive</p></td>
    </tr>

    
	
	<tr>
      <td><p class="titolo"> Graduatoria</p></td>
      <td><p class="titolo"> Lista Fuori Graduatoria</p></td>
    </tr>
    
	<tr>
      <td align="left">	<a href="#" onClick="Stampa('pos');">	
			<img name="dettImg" src="../../img/text.gif" alt="Stampa Definitiva Graduatoria" /></a>&nbsp;
		<a href="#" onClick="Stampa('pos','grad');">Stampa Graduatoria</a>
	  </td>
      
      
      <td align="left"> 	<a href="#" onClick="Stampa('pos');">	
			<img name="dettImg" src="../../img/text.gif" alt="Stampa Definitiva Lista Fuori Graduatoria" /></a>&nbsp;
		<a href="#" onClick="Stampa('pos','list');">Stampa Lista</a>
	  </td>
	  
    </tr>
	
	
	
	
	
	
</table>
<%= htmlStreamBottom %>

</af:form>

</body>
</html>
