
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

        
<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.StringUtils,
                  java.util.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<% 
    String cdnFunzione = (String)serviceRequest.getAttribute("CDNFUNZIONE");

    String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);
    
    String strCodiceFiscale=StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscale");
  	String strCognomeNome=StringUtils.getAttributeStrNotNull(serviceRequest,"strCognomeNome");
  	String CodCPI=StringUtils.getAttributeStrNotNull(serviceRequest,"CodCPI");
  	String CodProvincia=StringUtils.getAttributeStrNotNull(serviceRequest,"CodProvincia");
  	
  	String strAzRagioneSociale=StringUtils.getAttributeStrNotNull(serviceRequest,"strAzRagioneSociale");
  	
  	String CodMbTipo=StringUtils.getAttributeStrNotNull(serviceRequest,"CodMbTipo");      
  	String datInizioDa=StringUtils.getAttributeStrNotNull(serviceRequest,"datInizioDa");
  	String datInizioA=StringUtils.getAttributeStrNotNull(serviceRequest,"datInizioA");
  	String datFineDa=StringUtils.getAttributeStrNotNull(serviceRequest,"datFineDa"); 
  	String datFineA=StringUtils.getAttributeStrNotNull(serviceRequest,"datFineA");    
  	String strNumAtto=StringUtils.getAttributeStrNotNull(serviceRequest,"strNumAtto");
  	String datCRT=StringUtils.getAttributeStrNotNull(serviceRequest,"datCRT");  
  	String codEnteDetermina=StringUtils.getAttributeStrNotNull(serviceRequest,"codEnteDetermina");    
%>

<html>
<head>
	<title>Consulta Mobilità da File Regionale</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>  
<af:linkScript path="../../js/"/>
<SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>
<script language="Javascript">

  function controlloDate(){
  		var objDataDa = document.form1.datInizioDa;
    	var objDataA = document.form1.datInizioA;    
		if ((objDataDa.value != "") && (objDataA.value != "")) {
      		if (compareDate(objDataDa.value,objDataA.value) > 0) {
      			alert(objDataDa.title + " maggiore di " + objDataA.title);
      			objDataDa.focus();
	    		return false;
	  		}	
		}
		objDataDa = document.form1.datFineDa;
    	objDataA = document.form1.datFineA;    
		if ((objDataDa.value != "") && (objDataA.value != "")) {
      		if (compareDate(objDataDa.value,objDataA.value) > 0) {
      			alert(objDataDa.title + " maggiore di " + objDataA.title);
      			objDataDa.focus();
	    		return false;
	  		}	
		}
    	return true;
  } 

  function valorizzaHidden() {  	
  	document.form1.descCPI_H.value = document.form1.CodCPI.options[document.form1.CodCPI.selectedIndex].text;
  	document.form1.descProvincia_H.value = document.form1.CodProvincia.options[document.form1.CodProvincia.selectedIndex].text;
  	document.form1.descTipoMob_H.value = document.form1.CodMbTipo.options[document.form1.CodMbTipo.selectedIndex].text;
  	return true;
  } 
	
</script>

</head>

<body class="gestione" onload="rinfresca();">
<center>
<font color="red">
	<af:showErrors/> 
</font>
<font color="green">
	<af:showMessages prefix="MobilitaDeleteListaFileReg"/>
</font>
</center>
<br>
<p class="titolo">Ricerca Mobilità da File Regionale</p>
<p align="center">
  <af:form name="form1" action="AdapterHTTP" method="POST" onSubmit="controlloDate() && valorizzaHidden()">        	
  <%out.print(htmlStreamTop);%>
  <table class="main">  
  <tr><td colspan="2"/>&nbsp;</td></tr>  
  <tr><td colspan="2"><div class="sezione2"/>Lavoratore</td></tr>
  <tr>
    <td class="etichetta">Codice Fiscale</td>
    <td class="campo"><af:textBox type="text" title="Codice Fiscale" name="strCodiceFiscale" value="<%=strCodiceFiscale%>" size="20" maxlength="16"/></td>
  </tr>
  <tr>
    <td class="etichetta">Cognome e Nome</td>
    <td class="campo"><af:textBox type="text" title="Lavoratore" name="strCognomeNome" value="<%=strCognomeNome%>" size="30" maxlength="50" /></td>
  </tr>
  <tr>
    <td class="etichetta">Centro per l'Impiego</td>
    <td class="campo"><af:comboBox name="CodCPI" title="Centro per l'Impiego competente" addBlank="true" moduleName="M_ELENCOCPI" selectedValue="<%=CodCPI%>"/></td>
  </tr>
  <tr>
    <td class="etichetta">Provincia</td>
    <td class="campo"><af:comboBox name="CodProvincia" title="Provincia" addBlank="true" moduleName="M_ELENCOPROVINCE" selectedValue="<%=CodProvincia%>"/></td>
  </tr>
  <tr><td colspan="2"/>&nbsp;</td></tr>  
  <tr><td colspan="2"><div class="sezione2"/>Azienda</td></tr>
  <tr>
    <td class="etichetta">Ragione Sociale</td>
    <td class="campo"><af:textBox type="text" title="Ragione Sociale" name="strAzRagioneSociale" value="<%=strAzRagioneSociale%>" size="30" maxlength="50" /></td>
  </tr>  
  <tr><td colspan="2"/>&nbsp;</td></tr>  
  <tr><td colspan="2"><div class="sezione2"/>Mobilità</td></tr>
  <tr>
    <td class="etichetta">Tipo</td>
    <td class="campo"><af:comboBox name="CodMbTipo" title="Tipo di Mobilità" addBlank="true" moduleName="M_ELENCOTIPOMOB" selectedValue="<%=CodMbTipo%>"/></td>
  </tr>  
  <tr>
    <td class="etichetta">Data Inizio da</td>
    <td class="campo">
      <af:textBox type="date" name="datInizioDa" title="Data Inizio da" value="<%=datInizioDa%>" size="12" maxlength="10" validateOnPost="true"/>
      &nbsp;&nbsp;&nbsp;a&nbsp;&nbsp;<af:textBox type="date" name="datInizioA" title="Data Inizio a" value="<%=datInizioA%>" size="12" maxlength="10" validateOnPost="true"/>
    </td>
  </tr> 
  <tr>
    <td class="etichetta">Data Fine da</td>
    <td class="campo">
      <af:textBox type="date" name="datFineDa" title="Data Fine da" value="<%=datFineDa%>" size="12" maxlength="10" validateOnPost="true"/>
      &nbsp;&nbsp;&nbsp;a&nbsp;&nbsp;<af:textBox type="date" name="datFineA" title="Data Fine a" value="<%=datFineA%>" size="12" maxlength="10" validateOnPost="true"/>
    </td>
  </tr>
  <tr>
    <td class="etichetta">Numero di approvazione</td>
    <td class="campo"><af:textBox type="text" title="Numero di approvazione" name="strNumAtto" value="<%=strNumAtto%>" size="20" maxlength="15"/></td>
  </tr>
  <tr>
    <td class="etichetta">Data di approvazione</td>
    <td class="campo">
      <af:textBox type="date" name="datCRT" title="Data di approvazione" value="<%=datCRT%>" size="12" maxlength="10"/>
	</td>
  </tr>
  <tr>
    <td class="etichetta">Sigla Ente di approvazione</td>
    <td class="campo"><af:textBox type="text" title="Sigla Ente di approvazione" name="codEnteDetermina" value="<%=codEnteDetermina%>" size="20" maxlength="10" /></td>
  </tr>   
    
  <input type="hidden" name="PAGE" value="MobilitaListaFileRegPage"/>
  <input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
  <input  type="hidden" name="descCPI_H" value=""/> 
  <input  type="hidden" name="descProvincia_H" value=""/>
  <input  type="hidden" name="descTipoMob_H" value=""/>
  
  <tr><td colspan="2">&nbsp;</td></tr>
  <tr>
    <td colspan="2" align="center">
      <input name="Cerca" type="submit" class="pulsanti" value="Cerca">
      &nbsp;&nbsp;
      <input name="reset" type="reset" class="pulsanti" value="Annulla">
    </td>
    </tr>
  </table>
  <%out.print(htmlStreamBottom);%>  
  </af:form>
</p>

</body>
</html>
