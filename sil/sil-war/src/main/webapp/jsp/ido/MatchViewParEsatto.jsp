<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  it.eng.afExt.utils.*, java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  it.eng.sil.util.*" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<head>
<title>GestisciStatoDoc.jsp</title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">

<af:linkScript path="../../js/"/>

<% 
	Vector rows = null;
	
	rows = serviceResponse.getAttributeAsVector("M_GetParOpzionaliIncrocio.ROWS.ROW");
	
	
	String datstatolav     = "";  
	String flgpreferibile = "";
	String flgnonindispensabile = "";
	String flgsolodisoccupato     = ""; 
	String prgalternativa = "";
	String numrichiesta = "";
	String numrichiestaVis = "";
	String numanno     = ""; 
	String flgMansione = "";
	String flgIncrocioMirato = "";
	String codMonoCMCategoria = "";
	String flgGaranziaGiovani = "";
	BigDecimal numProt      = null;
	BigDecimal numAnnoProt  = null;
	BigDecimal kLockProt    = null;
	String rosaFiltro = "";
	String strCategoriaCM = "";
	//La linea di codice successiva è provvisoria in attesa che venga creata la tabella in cui reperire quali sono 
	//i documenti che devono obbligatoriamente protocollati
 
   
	if(rows != null && !rows.isEmpty()) { 
		SourceBean row = (SourceBean) rows.elementAt(0);
		datstatolav = StringUtils.getAttributeStrNotNull (row,"datstatolav");
		flgpreferibile = StringUtils.getAttributeStrNotNull(row,"flgpreferibile").equals("S")?"Si":"No";
		flgnonindispensabile = StringUtils.getAttributeStrNotNull(row,"flgnonindispensabile").equals("S")?"Si":"No"; 
		flgsolodisoccupato = StringUtils.getAttributeStrNotNull(row,"flgsolodisoccupato").equals("S")?"Si":"No";
		prgalternativa = it.eng.sil.util.Utils.notNull(row.getAttribute("prgalternativa"));
		numrichiesta = it.eng.sil.util.Utils.notNull(row.getAttribute("numrichiesta"));
		numrichiestaVis = it.eng.sil.util.Utils.notNull(row.getAttribute("numrichiestavis"));
		numanno = it.eng.sil.util.Utils.notNull(row.getAttribute("numanno"));
		flgMansione = StringUtils.getAttributeStrNotNull(row, "flgNoMansione").equals("S")?"Si":"No";
		flgIncrocioMirato = StringUtils.getAttributeStrNotNull(row, "flgIncrocioMirato").equals("S")?"Si":"No";
		codMonoCMCategoria = StringUtils.getAttributeStrNotNull(row, "codMonoCMCategoria");
		rosaFiltro = StringUtils.getAttributeStrNotNull(row, "FLGFILTRICMAATTIVATI").equals("S")?"Si":"No";
		flgGaranziaGiovani = StringUtils.getAttributeStrNotNull (row,"flgGaranziaGiovani").equals("S")?"Si":"No"; 
	}

	if (("D").equalsIgnoreCase(codMonoCMCategoria)) {
    	strCategoriaCM = "Disabili";
  	} 
 	else if (("A").equalsIgnoreCase(codMonoCMCategoria)) {
  		strCategoriaCM = "Categoria protetta ex. Art. 18";
  	}
  	else if (("E").equalsIgnoreCase(codMonoCMCategoria)) {
  		strCategoriaCM = "Entrambi";
  	}
  	else {
 		strCategoriaCM = "Non Usato";
  	}

 //Servono per gestire il layout grafico
    String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>






</head>
<body class="gestione">
<!--<body bgcolor="gold">-->

<af:form name="form1" method="POST" action="AdapterHTTP">
<br/>
<p align="center">
<%out.print(htmlStreamTop);%>
<table class="main" >

<tr><td><br/></td></tr>
<tr><td class="azzurro_bianco" colspan="2" align="center">
        <b>Parametri Utilizzati nell'incrocio</b>
    </td>
</tr>

<tr><td><br/></td></tr>

<tr>
    <td colspan="2"><br/><div class="sezione2">Parametri opzionali  </div>
</td></tr>
<%
// INIT-PARTE-TEMP
if (Sottosistema.CM.isOff()) {
// END-PARTE-TEMP
%>
	<tr>
		<td class="sezione" colspan="2" align=left> Data validità CV <b> <%=datstatolav%> </b></td>
	</tr>
<%
// INIT-PARTE-TEMP
} else {
// END-PARTE-TEMP
%>
<%
	if ("No".equalsIgnoreCase(flgIncrocioMirato)) {
%>
		<tr>
			<td class="sezione" colspan="2" align=left> Data validità CV <b> <%=datstatolav%> </b></td>
		</tr>
<%
	}
%>
<%
// INIT-PARTE-TEMP
}
// END-PARTE-TEMP
%>
<tr><td class="sezione" colspan="2" align=left>Uso informazioni preferibili <b> <%=flgpreferibile%> </b></td>
</tr>

<tr><td class="sezioni" colspan="2" align=left>Uso informazioni Non indispensabili <b> <%=flgnonindispensabile%> </b></td>
</tr>

<tr><td class="sezioni" colspan="2" align=left>Solo disoccupati/inoccupati <b> <%=flgsolodisoccupato%> </b></td>
</tr>

<tr><td class="sezioni" colspan="2" align=left>Garanzia Giovani <b> <%=flgGaranziaGiovani%> </b></td>
</tr>

<tr>
	<td class="sezioni" colspan="2" align=left>Incrocio slegato dalla mansione <b> <%=flgMansione%> </b></td>
</tr>
<%
// INIT-PARTE-TEMP
if (Sottosistema.CM.isOff()) {
// END-PARTE-TEMP
%>

<%
// INIT-PARTE-TEMP
} else {
// END-PARTE-TEMP
%>
	<tr>
		<td class="sezione" colspan="2" align=left>Uso incrocio mirato <b> <%=flgIncrocioMirato%> </b></td>
	</tr>
	<tr>
		<td class="sezione" colspan="2" align=left>Categoria CM <b> <%=strCategoriaCM%> </b></td>
	</tr>
	<tr>
		<td class="sezione" colspan="2" align=left>Utilizzo filtri CM <b> <%=rosaFiltro%> </b></td>
	</tr>
<%
// INIT-PARTE-TEMP
}
// END-PARTE-TEMP
%>
<tr>
    <td colspan="2"><br/><div class="sezione2">Richiesta utilizzata</div>
</td></tr>

<tr><td class="sezioni" colspan="2" align=left>Numero Richiesta <b> <%=numrichiestaVis+"/"+numanno%> </b></td>
</tr>

<tr><td class="sezioni" colspan="2" align=left>Profilo n. <b> <%=prgalternativa%> </b></td>
</tr>
  <tr>
    <td colspan="2" align="center">
      <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onClick="javascript: window.close();">
    </td>
</tr>  



</table>

<%out.print(htmlStreamBottom);%>
</af:form>
</body>
</html>


