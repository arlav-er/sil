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
	String titoloPage = "Numero dei lavoratori attualmente in forza all'azienda nel territorio selezionato";
	String titoloNote = "Il conteggio è stato effettuato sui movimenti presenti nel sistema e senza tener conto degli eventuali part-time o delle possibili esclusioni.";
	String numLavOrgTotali="";
	String strDataCalcolo = StringUtils.getAttributeStrNotNull(serviceRequest, "dataCalcolo");
	SourceBean calcoloOrganico = (SourceBean) serviceResponse.getAttribute("M_Calcolo_Organico.ROWS.ROW");
	if (calcoloOrganico!=null) {
		if (strDataCalcolo.equals("")) {
			numLavOrgTotali = ((BigDecimal)calcoloOrganico.getAttribute("numLavInProv")).toString();	
		}
		else {
			titoloPage = "Numero dei lavoratori attualmente in forza all'azienda";
			titoloNote = "Il conteggio è stato effettuato sui movimenti presenti nel sistema.";
			String strNumLavOrg = ((BigDecimal)calcoloOrganico.getAttribute("numLavMovimenti")).toString();
			String strNumDipendenti = StringUtils.getAttributeStrNotNull(serviceRequest, "numDipendenti");
			BigDecimal numDipendentiAz = null;
			BigDecimal numDipendentiTotal = new BigDecimal(strNumLavOrg);
			if (!strNumDipendenti.equals("")) {
				try {
					numDipendentiAz = new BigDecimal(strNumDipendenti);
					numDipendentiTotal = numDipendentiTotal.add(numDipendentiAz);
				}
				catch (Exception e) {
					//numDipendentiTotal non cambia
				}
			}
			numLavOrgTotali = numDipendentiTotal.toString();
		}
	}
	boolean canModify=false;

  	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>


<html>
<head>
 <title>Calcolo Organico Aziendale</title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
 <af:linkScript path="../../js/"/>
</head>

<body  class="gestione" onload="rinfresca()">
<center>
<font color="red">
    <af:showErrors/>
</font>
</center>

  <p class="titolo">
  	<b><%=titoloPage %></b>
  </p>
  <p class="titolo">
  	<i><%=titoloNote %></i>
  </p>
  
<af:form name="Frm1" method="POST" action="AdapterHTTP" >
<%out.print(htmlStreamTop);%>
<table  class="main">

<tr>
    <td class="etichetta">   </td>
    <td class="etichetta">Numero lavoratori:&nbsp;</td>	
    <td class="campo"><% if (!numLavOrgTotali.equals("")) {    							
        					out.print(numLavOrgTotali);        					
        			  	 } else {
        			  	 	out.print("<i>Dato non calcolabile</i>");
        			  	 } %>   
    </td>
</tr>

</table>
<%out.print(htmlStreamBottom);%>
</af:form>

<center><input class="pulsante" type = "button" name="chiudi" value="Chiudi" onclick="window.close()"/></center>
</body>
</html>
