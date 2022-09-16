<%@ page contentType="text/html;charset=utf-8"%>
 
<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,
                java.text.*,
                java.util.*,
                java.math.*,
                it.eng.afExt.utils.*,
                it.eng.sil.util.*,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ taglib uri="aftags" prefix="af"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>


<%

	String strCodiceFiscale="";
	String strCognome="";
	String strNome="";
	String datNasc="";
	String strComNas=""; 
	String codCpi="";
	String desCpi="";
	String provCpi="";
	String tipoCompetenza="";
	String datInizio="";

	SourceBean cpiMaster = (SourceBean) serviceResponse.getAttribute("M_COOP_GETCPIMASTERIR.ROWS.ROW");
	SourceBean descrCodCom = (SourceBean) serviceResponse.getAttribute("M_COMMONGETDESCRCODCOM.ROWS.ROW");
	SourceBean descrCpi = (SourceBean) serviceResponse.getAttribute("M_COMMONGETDESCRCPI.ROWS.ROW");
	if (cpiMaster!=null) {		
		strCodiceFiscale=StringUtils.getAttributeStrNotNull(cpiMaster, "codiceFiscale");
		strCognome=StringUtils.getAttributeStrNotNull(cpiMaster, "cognome");
		strNome=StringUtils.getAttributeStrNotNull(cpiMaster, "nome");
		datNasc=StringUtils.getAttributeStrNotNull(cpiMaster, "datNasc");
		codCpi=StringUtils.getAttributeStrNotNull(cpiMaster, "codCpiMaster");
		tipoCompetenza=StringUtils.getAttributeStrNotNull(cpiMaster, "codTipoMaster");
		datInizio=StringUtils.getAttributeStrNotNull(cpiMaster,"dataMaster");
	}
	
	if (descrCodCom!=null) {
		strComNas=StringUtils.getAttributeStrNotNull(descrCodCom, "STRCOM");	
	}
	
	if (descrCpi!=null) {
		desCpi=StringUtils.getAttributeStrNotNull(descrCpi, "DESCRCPI");	
		provCpi=StringUtils.getAttributeStrNotNull(descrCpi, "SIGLAPROVCPI");	
	}



   //PageAttribs attributi = new PageAttribs(user, "CoopDatiPersonaliPage");
   //int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));

  // NOTE: Attributi della pagina (pulsanti e link) 
  //boolean canModify = attributi.containsButton("aggiorna");
	boolean canModify=false;

  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>


<html>
<head>
 <title>Controllo del master</title>
 <!-- ../jsp/anag/TrasferimentiStoricoElenco.jsp -->
 <link rel="stylesheet" type="text/css" href="../../css/stiliCoop.css"/>
 <af:linkScript path="../../js/"/>

 
</head>
<!--<body  onload="checkError();" class="gestione" > -->
<body  class="gestione" onload="rinfresca()">
<center>
<font color="red">
    <af:showErrors/>
</font>
</center>

  <p class="titolo"><b>Verifica Master per il lavoratore</b></p>
  
<af:form name="Frm1" method="POST" action="AdapterHTTP" >
<%out.print(htmlStreamTop);%>
<table  class="main">
<tr>
    <td class="etichetta">Codice Fiscale&nbsp;</td>	
    <td class="campo"><% if (!strCodiceFiscale.equals("")) {    							
        					out.print(strCodiceFiscale);        					
        			  	 } else {
        			  	 	out.print("<I>Non pervenuto</I>");
        			  	 } %>   
    </td>
</tr>
<tr>
    <td class="etichetta">Cognome&nbsp;</td>	
    <td class="campo"><% if (!strCognome.equals("")) {    							
        					out.print(strCognome);        					
        			  	 } else {
        			  	 	out.print("<I>Non pervenuto</I>");
        			  	 } %>   
    </td>
</tr>
<tr>
    <td class="etichetta">Nome&nbsp;</td>	
    <td class="campo"><% if (!strNome.equals("")) {    							
        					out.print(strNome);        					
        			  	 } else {
        			  	 	out.print("<I>Non pervenuto</I>");
        			  	 } %>   
    </td>
</tr>
<tr>
    <td class="etichetta">Data di Nascita&nbsp;</td>	
    <td class="campo"><% if (!datNasc.equals("")) {    							
        					out.print(datNasc);        					
        			  	 } else {
        			  	 	out.print("<I>Non pervenuto</I>");
        			  	 } %>   
    </td>
</tr>
<tr>
    <td class="etichetta">Comune di Nascita&nbsp;</td>	
    <td class="campo"><% if (!strComNas.equals("")) {    							
        					out.print(strComNas);        					
        			  	 } else {
        			  	 	out.print("<I>Non pervenuto</I>");
        			  	 } %>   
    </td>
</tr>
<tr>
	<td colspan="2"></td>
</tr>
<tr>
    <td class="etichetta">Centro per l'Impiego Master&nbsp;</td>
    <td class="campo"><% if (!codCpi.equals("")) {    							
        					out.print(codCpi+" - "+desCpi + " (" + provCpi+")");
        			  	 } else {
        			  	 	out.print("<I>Non pervenuto</I>");
        			  	 } %>        	
    </td>
</tr>
<tr>
    <td class="etichetta">Tipo di Master&nbsp;</td>
    <td class="campo"><% if (!tipoCompetenza.equals("")) {    							
        					out.print(tipoCompetenza.equals("C")?"Competente amministrativo":"Titolare");        					
        			  	 } else {
        			  	 	out.print("<I>Non pervenuto</I>");
        			  	 } %>   
    </td>
</tr>
<tr>
    <td class="etichetta">Data Master&nbsp;</td>
    <td class="campo"><% if (!datInizio.equals("")) {    							
        					out.print(datInizio);        					
        			  	 } else {
        			  	 	out.print("<I>Non pervenuto</I>");
        			  	 } %>   
    </td>
</tr>

</table>
<%out.print(htmlStreamBottom);%>
</af:form>

<center><input class="pulsante" type = "button" name="chiudi" value="Chiudi" onclick="window.close()"/></center>
</body>
</html>
