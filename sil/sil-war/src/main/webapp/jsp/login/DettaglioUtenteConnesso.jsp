<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

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
	String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);
    	
	SourceBean res = (SourceBean) serviceResponse.getAttribute("DettaglioUtenteConnesso.rows.row");
	String login = (String) res.getAttribute("strlogin");
	String nome = (String) res.getAttribute("strnome");
	String cognome = (String) res.getAttribute("strcognome");
	String browser = (String) res.getAttribute("strbrowser");
	String dataInizioSessione = (String) res.getAttribute("dtminiziosessione");
	String telefono = (String) res.getAttribute("strtelefono");
	String fax= (String) res.getAttribute("strfax");
	String email = (String) res.getAttribute("stremail");
	String luogoRif = (String) res.getAttribute("strluogorif");
	String nota = (String) res.getAttribute("strnota");
	String ip = (String) res.getAttribute("strip");
%>


<html>
<head>
	<title>Dettaglio Utente Connesso</title>
	<link rel="stylesheet" type="text/css" href=" ../../css/stili.css"/>
	<link rel="stylesheet" type="text/css" href=" ../../css/listdetail.css"/>
	<af:linkScript path="../../js/" />
</head>

<body onload="checkError();" class="gestione">
	<af:error/>
	<af:showErrors />
	<br> &nbsp;
	
	<table class="main">
		<tr>
			<td><p align ="center"><B>Dettaglio utente connesso</B></p></td>
		</tr>
	</table>
	
	<af:form method="POST" action="AdapterHTTP" name="form">
	
		<%out.print(htmlStreamTop);%>
		<table class="main">		
			<tr>
				<td class="etichetta">Login:</td>
				<td class="campo">
					<af:textBox classNameBase="input" name="login" type="text" readonly="true" size="40" value="<%=login%>"/>
				</td>
			</tr>
			<tr>
				<td class="etichetta">Nome:</td>
				<td class="campo">
					<af:textBox classNameBase="input" name="nome" type="text" readonly="true" size="40" value="<%=nome%>"/>
				</td>
			</tr>
			<tr>
				<td class="etichetta">Cognome:</td>
				<td class="campo">
					<af:textBox classNameBase="input" name="cognome" type="text" readonly="true" size="40" value="<%=cognome%>"/>
				</td>
			</tr>
			<tr>
				<td class="etichetta">Data inizio sessione:</td>
				<td class="campo">
					<af:textBox classNameBase="input" name="dataInizioSessione" type="text" readonly="true" size="100" value="<%=dataInizioSessione%>"/>
				</td>
			</tr>
			<tr>
				<td class="etichetta">Telefono:</td>
				<td class="campo">
					<af:textBox classNameBase="input" name="telefono" type="text" readonly="true" size="100" value="<%=telefono%>"/>
				</td>
			</tr>
			<tr>
				<td class="etichetta">Fax:</td>
				<td class="campo">
					<af:textBox classNameBase="input" name="fax" type="text" readonly="true" size="100" value="<%=fax%>"/>
				</td>
			</tr>
			<tr>
				<td class="etichetta">Email:</td>
				<td class="campo">
					<af:textBox classNameBase="input" name="email" type="text" readonly="true" size="100" value="<%=email%>"/>
				</td>
			</tr>
			<tr>
				<td class="etichetta">Browser:</td>
				<td class="campo">
					<af:textBox classNameBase="input" name="browser" type="text" readonly="true" size="100" value="<%=browser%>"/>
				</td>
			</tr>
			<tr>
				<td class="etichetta">Ip / macchina:</td>
				<td class="campo">
					<af:textBox classNameBase="input" name="ip" type="text" readonly="true" size="100" value="<%=ip%>"/>
				</td>
			</tr>
			<tr>
				<td class="etichetta">Nota:</td>
				<td class="campo" valign="baseline">
					<af:textArea classNameBase="textarea" name="nota" readonly="true" value="<%=nota%>" cols="50"/>
					
				</td>
			</tr>
		</table>
		<%out.print(htmlStreamBottom);%>
		
	</af:form>
	
<%
	String urlDiLista = (String) sessionContainer.getAttribute("_TOKEN_UTENTICONNESSIPAGE");
	if (urlDiLista != null) {
		out.println("<div align=\"center\"><a href=\"#\" onClick=\"goTo('" + urlDiLista +"')\"><img src=\"../../img/rit_lista.gif\" border=\"0\"></div>");
	}
%>
	
	</body>
</html>
