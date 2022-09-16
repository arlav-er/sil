
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../../global/noCaching.inc" %>
<%@ include file="../../global/getCommonObjects.inc" %>

        
<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  java.util.*,
                  it.eng.afExt.utils.StringUtils,
                  it.eng.sil.util.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String CDNFUNZIONE = (String) serviceRequest.getAttribute("CDNFUNZIONE");
	
	String CF 				= StringUtils.getAttributeStrNotNull(serviceRequest,"CF");
	String cognome       				= StringUtils.getAttributeStrNotNull(serviceRequest,"COGNOME");
	String nome          				= StringUtils.getAttributeStrNotNull(serviceRequest,"NOME");
	String dataPrestazioneDa       	= StringUtils.getAttributeStrNotNull(serviceRequest,"dataPrestazioneDa");
	String dataPrestazioneA        	= StringUtils.getAttributeStrNotNull(serviceRequest,"dataPrestazioneA");
	String dataInizioPrestazioneDa     = StringUtils.getAttributeStrNotNull(serviceRequest,"dataInizioPrestazioneDa");
	String dataInizioPrestazioneA      = StringUtils.getAttributeStrNotNull(serviceRequest,"dataInizioPrestazioneA");
	String dataFinePrestazioneDa      	= StringUtils.getAttributeStrNotNull(serviceRequest,"dataFinePrestazioneDa");
	String dataFinePrestazioneA      	= StringUtils.getAttributeStrNotNull(serviceRequest,"dataFinePrestazioneA");
	String dataComunicazioneDa      	= StringUtils.getAttributeStrNotNull(serviceRequest,"dataComunicazioneDa");
	String dataComunicazioneA      	= StringUtils.getAttributeStrNotNull(serviceRequest,"dataComunicazioneA");
	String IDComunicazione      		= StringUtils.getAttributeStrNotNull(serviceRequest,"IDComunicazione");
	String NProvvedimento      		= StringUtils.getAttributeStrNotNull(serviceRequest,"NProvvedimento");
	String dataProvvedimento      		= StringUtils.getAttributeStrNotNull(serviceRequest,"dataProvvedimento");
	String TipoEvento      			= StringUtils.getAttributeStrNotNull(serviceRequest,"TipoEvento");
	String TipoComunicazione      		= StringUtils.getAttributeStrNotNull(serviceRequest,"TipoComunicazione");
	String MotivoComunicazione      	= StringUtils.getAttributeStrNotNull(serviceRequest,"MotivoComunicazione");
	String StatoDomanda      		= StringUtils.getAttributeStrNotNull(serviceRequest,"StatoDomanda");
	String Autorizzabile 			= StringUtils.getAttributeStrNotNull(serviceRequest,"Autorizzabile");
	
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
	<title>Nuovo Reddito Attivazione</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>  
<af:linkScript path="../../js/"/>
<script language="Javascript">
<%@ include file="../../documenti/RicercaCheck.inc" %>
</script>
</head>

<body class="gestione" onload="rinfresca();">
<br>
<p class="titolo">Nuovo Reddito di attivazione</p>
<p align="center">
  <af:form action="AdapterHTTP" method="GET" >
  	<input name="PAGE" type="hidden" value="ListaNRAPage"/>        	
	  <%out.print(htmlStreamTop);%> 
	  
	  <table class="main">
		  <tr>
		    <td class="etichetta">Codice Fiscale</td>
		    <td class="campo">
		      <af:textBox type="text" name="CF" value="<%=CF%>" size="20" maxlength="16"/>
		    </td>
		  </tr>
		  
		  <tr>
		    <td class="etichetta">Cognome</td>
		    <td class="campo">
		      <af:textBox type="text" name="COGNOME" value="<%=cognome%>" size="20" maxlength="50"/>
		    </td>
		  </tr>
		  
		  <tr>
		    <td class="etichetta">Nome</td>
		    <td class="campo">
		      <af:textBox type="text" name="NOME" value="<%=nome%>" size="20" maxlength="50"/>
		    </td>
		  </tr>
		  
		  <tr>
	   		<td class="etichetta">Data presentazione da</td>
	   		<td class="campo">
	   			<af:textBox type="date" title="Data prestazione da" name="dataPrestazioneDa" value="<%=dataPrestazioneDa%>" size="12" maxlength="10" validateOnPost="true"/>
	   			&nbsp;&nbsp;a&nbsp;&nbsp;
	   			<af:textBox type="date" title="Data prestazione a" name="dataPrestazioneA" value="<%=dataPrestazioneA%>" size="12" maxlength="10" validateOnPost="true"/>
	   		</td>
	   	 </tr>
	   	 
	   	<tr>
	   		<td class="etichetta">Data inizio prestazione da</td>
	   		<td class="campo">
	   			<af:textBox type="date" title="Data inizio prestazione da" name="dataInizioPrestazioneDa" value="<%=dataInizioPrestazioneDa%>" size="12" maxlength="10" validateOnPost="true"/>
	   			&nbsp;&nbsp;a&nbsp;&nbsp;
	   			<af:textBox type="date" title="Data inizio prestazione a" name="dataInizioPrestazioneA" value="<%=dataInizioPrestazioneA%>" size="12" maxlength="10" validateOnPost="true"/>
	   		</td>
	   	 </tr>
	   	 
	   	 <tr>
	   		<td class="etichetta">Data fine prestazione da</td>
	   		<td class="campo">
	   			<af:textBox type="date" title="Data fine prestazione da" name="dataFinePrestazioneDa" value="<%=dataFinePrestazioneDa%>" size="12" maxlength="10" validateOnPost="true"/>
	   			&nbsp;&nbsp;a&nbsp;&nbsp;
	   			<af:textBox type="date" title="Data fine prestazione a" name="dataFinePrestazioneA" value="<%=dataFinePrestazioneA%>" size="12" maxlength="10" validateOnPost="true"/>
	   		</td>
	   	 </tr>
	  
	  	 <tr>
	   		<td class="etichetta">Data comunicazione da</td>
	   		<td class="campo">
	   			<af:textBox type="date" title="Data comunicazione da" name="dataComunicazioneDa" value="<%=dataComunicazioneDa%>" size="12" maxlength="10" validateOnPost="true"/>
	   			&nbsp;&nbsp;a&nbsp;&nbsp;
	   			<af:textBox type="date" title="Data comunicazione a" name="dataComunicazioneA" value="<%=dataComunicazioneA%>" size="12" maxlength="10" validateOnPost="true"/>
	   		</td>
	   	 </tr>
	  
	  	  <tr>
		    <td class="etichetta">Identificativo Comunicazione</td>
		    <td class="campo">
		      <af:textBox type="text" name="IDComunicazione" value="<%=IDComunicazione%>" size="20" maxlength="50"/>
		    </td>
		  </tr>
		  
		  <tr>
		    <td class="etichetta">Numero Provvedimento</td>
		    <td class="campo">
		      <af:textBox type="text" name="NProvvedimento" value="<%=NProvvedimento%>" size="20" maxlength="50"/>
		    </td>
		  </tr>
		  
		 <tr>
	   		<td class="etichetta">Data provvedimento</td>
	   		<td class="campo">
	   			<af:textBox type="date" title="Data comunicazione da" name="dataProvvedimento" value="<%=dataProvvedimento%>" size="12" maxlength="10" validateOnPost="true"/>
	   		</td>
	   	 </tr>
	   	 
		  <tr>
		    <td class="etichetta">Tipo evento</td>
		    <td class="campo">
		      <af:comboBox name="TipoEvento" moduleName="M_TIPO_EVENTO" selectedValue="<%=TipoEvento%>" addBlank="true" />
		    </td>
		  <tr>
		  
		  <tr>
		    <td class="etichetta">Tipo comunicazione</td>
		    <td class="campo">
		      <af:comboBox name="TipoComunicazione" moduleName="M_TIPO_COMUNICAZIONE" selectedValue="<%=TipoComunicazione%>" addBlank="true" />
		    </td>
		  <tr>
		  
		  <tr>
		    <td class="etichetta">Motivo comunicazione</td>
		    <td class="campo">
		      <af:comboBox name="MotivoComunicazione" moduleName="M_MOTIVO_COMUNICAZIONE" selectedValue="<%=MotivoComunicazione%>" addBlank="true" />
		    </td>
		  <tr>
		  
		  <tr>
		    <td class="etichetta">Stato domanda</td>
		    <td class="campo">
		      <af:comboBox name="StatoDomanda" moduleName="M_STATO_DOMANDA_RA" selectedValue="<%=StatoDomanda%>" addBlank="true" />
		    </td>
		  <tr>
		  
		  <tr>
		    <td class="etichetta">Autorizzabile</td>
		    <td class="campo">
		      <af:comboBox name="Autorizzabile" moduleName="M_GenericComboSiNo" selectedValue="<%=Autorizzabile%>" addBlank="true" />
		    </td>
		  <tr>
		  
		  <tr> 
		    <td colspan="2" align="center">
		    	<input name="Invia" type="submit" class="pulsanti" value="Cerca">
		    </td>
	 	 </tr>
	  
	  </table>
	  
	  <%out.print(htmlStreamBottom);%> 
	
	  <input name="CDNFUNZIONE" type="hidden" value="<%=CDNFUNZIONE%>"/>
  
  </af:form>
</p>

</body>
</html>
