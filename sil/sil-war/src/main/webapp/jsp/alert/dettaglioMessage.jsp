<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.StringUtils,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);
    	
	SourceBean res = (SourceBean) serviceResponse.getAttribute("DettaglioMessage.rows.row");
	
	String mittente = (String) res.getAttribute("cognome") + " "+ (String) res.getAttribute("nome")+ " "+ (String) res.getAttribute("login");
	String datainserimento = (String) res.getAttribute("datainserimento");
	String oggetto = (String) res.getAttribute("oggetto");
	String corpo = (String) res.getAttribute("corpo");
	String iniziovalidita = (String) res.getAttribute("iniziovalidita");
	String finevalidita = (String) res.getAttribute("finevalidita");
	BigDecimal priorita = (BigDecimal) res.getAttribute("priorita");
	
	String priorità="";
	if(priorita.intValue()==1){ 
		priorità="Bassa";
	}
	else if(priorita.intValue()==2) {
		priorità="Media";
	}
	else if(priorita.intValue()==3) {
		priorità="Alta";
	}	
%>


<html>
<head>
	<title>Dettaglio Messaggio</title>
	<link rel="stylesheet" type="text/css" href=" ../../css/stili.css"/>
	<link rel="stylesheet" type="text/css" href=" ../../css/listdetail.css"/>
	<af:linkScript path="../../js/"/>
</head>

<body onload="checkError();" class="gestione">
	<af:error/>
	<af:showErrors />
	<br/> 
	
	<table class="main">
		<tr>
			<td><p align ="center"><B>Dettaglio messaggio</B></p></td>
		</tr>
	</table>
	
	<af:form action="AdapterHTTP" method="POST" dontValidate="true" name="Frm1">
	
		<%out.print(htmlStreamTop);%>
		<table class="main">		
			<tr>
				<td class="etichetta">Mittente:</td>
				<td class="campo">
					<af:textBox classNameBase="input" name="mittente" type="text" readonly="true" size="40" value="<%=mittente%>"/>
				</td>
			</tr>
			<tr>
				<td class="etichetta">Data inserimento:</td>
				<td class="campo">
					<af:textBox classNameBase="input" name="datainserimento" type="text" readonly="true" size="40" value="<%=datainserimento%>"/>
				</td>
			</tr>
			<tr>
				<td class="etichetta">Oggetto:</td>
				<td class="campo">
					<af:textBox classNameBase="input" name="oggetto" type="text" readonly="true" size="40" value="<%=oggetto%>"/>
				</td>
			</tr>
			<tr>
				<td class="etichetta">Corpo: </td>
				<td class="campo">
					<af:textArea classNameBase="textarea" name="corpo" readonly="true" value="<%=corpo%>" cols="50"/>
				</td>
			</tr>
			<tr>
				<td class="etichetta">Data inizio validità:</td>
				<td class="campo">
					<af:textBox classNameBase="input" name="iniziovalidita" type="text" readonly="true" size="100" value="<%=iniziovalidita%>"/>
				</td>
			</tr>
			<tr>
				<td class="etichetta">Data fine validità:</td>
				<td class="campo">
					<af:textBox classNameBase="input" name="finevalidita" type="text" readonly="true" size="100" value="<%=finevalidita%>"/>
				</td>
			</tr>
			<tr>
				<td class="etichetta">Priorità:</td>
				<td class="campo">
					<af:textBox classNameBase="input" name="priorita" type="text" readonly="true" size="100" value="<%=priorità%>"/>
				</td>
			</tr>
		</table>
		<%out.print(htmlStreamBottom);%>
		
	</af:form>
<%
	String urlDiLista = (String) sessionContainer.getAttribute("_TOKEN_LISTAMESSAGEPAGE");
	if (urlDiLista != null) {
	
			int occorrenzaCancella=urlDiLista.indexOf("ELIMINA");
			if (occorrenzaCancella!=-1) {
				int occorrenzaFineCancella=urlDiLista.indexOf('&', occorrenzaCancella);
				String urlDilistacomodo=urlDiLista.substring(0, occorrenzaCancella);
				String urlDilistacomodo2=urlDiLista.substring(occorrenzaFineCancella);
				urlDiLista=urlDilistacomodo + urlDilistacomodo2;
			}
			

			int occorrenzaInserisci=urlDiLista.indexOf("INSERISCI");
			if (occorrenzaInserisci!=-1) {
				int occorrenzaFineInserisci=urlDiLista.indexOf('&', occorrenzaInserisci);
				String urlDilistacomodo=urlDiLista.substring(0, occorrenzaInserisci);
				String urlDilistacomodo2=urlDiLista.substring(occorrenzaFineInserisci);
				urlDiLista=urlDilistacomodo + urlDilistacomodo2;
			}
		
		out.println("<div align=\"center\"><a href=\"#\" onClick=\"goTo('" + urlDiLista +"')\"><img src=\"../../img/rit_lista.gif\" border=\"0\"></div>");
	}
%>
	
	</body>
</html>
