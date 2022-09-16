<!-- @author: Giovanni Landi - Ottobre 2014 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../../global/noCaching.inc" %>
<%@ include file="../../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
				  com.engiweb.framework.tags.DefaultErrorTag,
                  it.eng.sil.security.*,
                  it.eng.sil.module.movimenti.*,
                  java.math.*,
                  java.io.*,
                  it.eng.sil.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  com.engiweb.framework.util.JavaScript,
                  com.engiweb.framework.dbaccess.sql.TimestampDecorator,
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*,
                  java.text.*,
                  java.sql.*,
                  oracle.sql.*,
                  java.util.*"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	//Attributi della pagina
  	PageAttribs attributi = new PageAttribs(user, (String) serviceRequest.getAttribute("PAGE"));
	String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
	
	boolean canEsportaAutorizzati = attributi.containsButton("ESPORTAXML");
	boolean canCalcolaDecaduti = attributi.containsButton("CALCESPORTAXLS");
	boolean canEsportaDecaduti = attributi.containsButton("ESPORTAXLS");
	boolean canAssociaDelibera = attributi.containsButton("ASSOCIADEL");
	canAssociaDelibera = true;

  	String htmlStreamTop = StyleUtils.roundTopTable(false);
  	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
  	
%>

<html>
<head>
  	<link rel="stylesheet" type="text/css" href="../../css/stili.css">
  	<af:linkScript path="../../js/"/>
  	<title>Esportazione</title>
  	
  	<script language="Javascript">

  	function esportaAutorizzati() {
  	  	if (document.frmEsporta.nomeFile.value == "") {
  	  	  	alert("Il campo File da esportare è obbligatorio");
  	  	}
  	  	else {
  	  		document.frmEsporta.ACTION_NAME.value = "RPT_ESPORTA_AUTORIZZATI_XML";
  	  		document.frmEsporta.submit();
  	  	}
  	}
  	
  	function esportaDecaduti() {
  		if (document.frmEsporta.datRifDecadenza.value == "") {
  	  	  	alert("Il campo Data decadenza è obbligatorio");
  	  	}
  		else {
  	  		document.frmEsporta.ACTION_NAME.value = "RPT_ESPORTA_DECADUTI_EXCEL";
  	  		document.frmEsporta.submit();
  		}
  	}
  	
  	function estraiDecaduti() {
  	  	document.frmEsporta.ACTION_NAME.value = "RPT_ESPORTA_LAVORATORI_STATO_DECADUTO_EXCEL";
  	  	document.frmEsporta.submit();
  	}

	function associaDeliberaAutorizzati() {
		if (document.frmEsporta.nomeFile.value == "") {
  	  	  	alert("Il campo File da associare è obbligatorio");
  	  	}
		else {
			if (document.frmEsporta.numeroDelibera.value == "") {
	  	  	  	alert("Il campo Numero è obbligatorio");
	  	  	}
			else {
				if (document.frmEsporta.datDelibera.value == "") {
		  	  	  	alert("Il campo Data è obbligatorio");
		  	  	}
				else {
					document.frmAssocia.numeroDelibera.value = document.frmEsporta.numeroDelibera.value;
					document.frmAssocia.datDelibera.value = document.frmEsporta.datDelibera.value;
					document.frmAssocia.nomeFile.value = document.frmEsporta.nomeFile.value;
					document.frmAssocia.submit();
				}
			}
		}
	}
  	
  	</script>
  	
</head>

<body class="gestione">
<br>
<p class="titolo">Esportazione</p>
<br/>

<font color="green">
	<af:showMessages prefix="M_AssociaDeliberaAFile"/>
</font>
<font color="red">
     <af:showErrors/>
</font>

<center>
	<%out.print(htmlStreamTop);%>
    
    	<table class="main">
			<tr>
				<td><div class="sezione2"/>Autorizzati</td>
			</tr>
		</table>
		
		
    	<af:form name="frmEsporta" method="POST" action="AdapterHTTP">
    	
		<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
		<input type="hidden" name="ACTION_NAME" value="" />
		
		<table class="main">
		<tr>
			<td class="etichetta" nowrap>Associa numero delibera</td>
			<td class="campo">
				<af:textBox classNameBase="input" type="text" name="numeroDelibera" value="" size="10" title="Numero Delibera"/>
			</td>
		</tr>
		
		<tr>
			<td class="etichetta" nowrap>Associa data delibera</td>
			<td class="campo">
				<af:textBox type="date" classNameBase="input" name="datDelibera" title="Data Delibera" validateOnPost="true" value="" size="11" maxlength="10"/>
			</td>
		</tr>
		
	    <tr>
      		<td class="etichetta" nowrap>Nome file</td>
	    	<td class="campo">
		    <af:comboBox name="nomeFile" size="1" title="File da esportare"
		                 multiple="false" disabled="false" classNameBase="input"
		                 moduleName="M_GetFileDaAutorizzare"
		                 addBlank="true" blankValue=""/>&nbsp;*
		    </td>
      	</tr>
      	
      	<tr><td colspan="2" align="center">&nbsp;</td></tr>
      	
      	<tr>
      		<td colspan="2" align="center">
      		<%if (canEsportaAutorizzati) {%>
	      		<input class="pulsante" type="button" name="EsportaXml" value="Esporta XML" onclick="return esportaAutorizzati()"/>
	      	<%}%>
	      	<%if (canAssociaDelibera) {%>
	      		<input class="pulsante" type="button" name="associaDelibera" value="Associa" onclick="return associaDeliberaAutorizzati()">
	      	<%}%>
	      	</td>
	    </tr>
      	</table>
      	
      	<table class="main">
			<tr>
				<td><div class="sezione2"/>Decaduti</td>
			</tr>
		</table>
		
		<table class="main">
		<tr>
      		<td class="etichetta" nowrap>Alla data</td>
	    	<td class="campo">
		    	<af:textBox type="date" classNameBase="input" name="datRifDecadenza" title="Data decadenza" validateOnPost="true" value="" size="11" maxlength="10"/>
		    </td>
      	</tr>
      	
      	<tr><td colspan="2" align="center">&nbsp;</td></tr>
      	
      	<tr>
      		<td colspan="2" align="center">
      			<%if (canCalcolaDecaduti) { %>
	      			<input class="pulsante" type="button" name="CalcoloEsportaExcel" value="Calcola/Esporta EXCEL" onclick="return esportaDecaduti()"/>&nbsp;&nbsp;&nbsp;
	      		<%}%>
	      		<%if (canEsportaDecaduti) { %>
	      			<input class="pulsante" type="button" name="EsportaExcel" value="Esporta EXCEL" onclick="return estraiDecaduti()"/>
	      		<%}%>
	      	</td>
   		 </tr>
	      	
		</table>
		
		</af:form>
		
		<af:form name="frmAssocia" method="POST" action="AdapterHTTP">
			<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
			<input type="hidden" name="PAGE" value="EsportaAutorizzatiPage" />
			<input type="hidden" name="OPERAZIONE" value="ASSOCIA" />
			<input type="hidden" name="numeroDelibera" value=""/>
			<input type="hidden" name="datDelibera" value=""/>
			<input type="hidden" name="nomeFile" value=""/>
		</af:form>
		
    <%out.print(htmlStreamBottom);%>
    <br>
</center>  
</body>
</html>