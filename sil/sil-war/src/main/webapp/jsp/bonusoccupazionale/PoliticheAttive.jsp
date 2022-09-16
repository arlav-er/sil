<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page
	import="
  com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.User,
  it.eng.sil.security.ProfileDataFilter,  
  it.eng.afExt.utils.*,
  it.eng.sil.pojo.yg.sap.*,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  it.eng.sil.security.PageAttribs,   
  com.engiweb.framework.security.*"%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%
	String _current_page = (String) serviceRequest.getAttribute("PAGE");

	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);

	Vector politicheRows = serviceResponse.getAttributeAsVector("M_GetPoliticheBonusOccupazionale.ROWS.ROW");
%>

<html>

<head>
<title>Politiche Attive</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />
</head>

<body class="gestione">

	<p class="titolo">
		<br>
		<b>Politiche attive notifica Crescere in Digitale</b>
	</p>
	
	<%
		out.print(htmlStreamTop);
	%>
	
	<table class="main">
		<%
		if (politicheRows.size() > 0) {
			for (int j = 0; j < politicheRows.size(); j++) {
				SourceBean item = (SourceBean) politicheRows.get(j);
				
				BigDecimal prgPol = (BigDecimal)item.getAttribute("PRGSPPOLITICANAZIONALE");
				String tipoAttivita = item.getAttribute("CODTIPOATTIVITA")!=null?item.getAttribute("CODTIPOATTIVITA").toString():"";
				String strDenominazione = item.getAttribute("STRDENOMINAZIONE")!=null?item.getAttribute("STRDENOMINAZIONE").toString():"";
				String dataProposta = item.getAttribute("DATPROPOSTA")!=null?item.getAttribute("DATPROPOSTA").toString():"";
				String datInizio = item.getAttribute("DATINIZIO")!=null?item.getAttribute("DATINIZIO").toString():"";
				String datFine = item.getAttribute("DATFINE")!=null?item.getAttribute("DATFINE").toString():"";
				String strDescrizione = item.getAttribute("STRDESCRIZIONE")!=null?item.getAttribute("STRDESCRIZIONE").toString():"";
				String codProgetto = item.getAttribute("CODPROGETTO")!=null?item.getAttribute("CODPROGETTO").toString():"";
				String codEntePromotore = item.getAttribute("STRCODENTEPROMOTORE")!=null?item.getAttribute("STRCODENTEPROMOTORE").toString():"";
				%>
				<tr>
				<td class="etichetta">Attività</td>
				
				<td align="left" colspan="5" class="inputView"><b><%=tipoAttivita%>
	                    - <af:comboBox name="proj_att_<%=prgPol.toString()%>" multiple="false"
	                        moduleName="COMBO_ATTIVITA_POLATT" disabled="true"
	                        classNameBase="input"
	                        selectedValue="<%=tipoAttivita%>" /></b></td>
				</td>
				</tr>
				
				<tr>
					<td class="etichetta">Denominazione</td>
					<td align="left" colspan="5" class="inputView"><b><%=strDenominazione%></b>
					</td>
				</tr>
				
				<tr>
					<td class="etichetta">Data proposta</td>
					<td align="left" colspan="5" class="inputView"><b><%=dataProposta%></b></td>
				</tr>
				
				<tr>
					<td class="etichetta">Data inizio</td>
					<td align="left" colspan="2" class="inputView"><b><%=datInizio%></b>
					</td>
					<td class="etichetta">Data fine</td>
					<td align="left" colspan="2" class="inputView"><b><%=datFine%></b></td>
				</tr>
				
				<tr>
					<td class="etichetta">Descrizione</td>
					<td align="left" colspan="5" class="inputView"><b><%=strDescrizione%></b>
					</td>
				</tr>
				
				<tr>
					<td class="etichetta">Titolo Progetto</td>
					<td align="left" colspan="5" class="inputView"><b><%=codProgetto%>
							- <af:comboBox name="proj_<%=prgPol.toString()%>" multiple="false"
								moduleName="COMBO_TITOLOPROGETTO" disabled="true"
								classNameBase="input"
								selectedValue="<%=codProgetto%>" /></b></td>
				</tr>
				
				<tr>
					<td class="etichetta">Codice Ente Promotore</td>
					<td align="left" colspan="5" class="inputView"><b><%=codEntePromotore%>
							- <af:comboBox name="proj_ente_<%=prgPol.toString()%>" multiple="false"
								moduleName="COMBO_ENTETIT" disabled="true"
								classNameBase="input"
								selectedValue="<%=codEntePromotore%>" /></b></td>			
				</tr>
			<%}
		}
		else {%>
			<tr>
			<td align="center">
			<p class="titolo">
			Non ci sono politiche attive associate
			</p>
			</td>
			</tr>
		<%}%>
	</table>
	<%
	out.print(htmlStreamBottom);
	%>
	
	<center>
		<input class="pulsante" type="button" name="chiudi" value="Chiudi"
			onclick="window.close()" />
	</center>
</body>

</html>
