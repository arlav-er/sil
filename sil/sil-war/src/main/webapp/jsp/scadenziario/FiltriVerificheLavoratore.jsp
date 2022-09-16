<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ page import="
  com.engiweb.framework.base.*,
  java.lang.*,
  java.text.*,
  java.util.*, 
  it.eng.sil.util.*,
  it.eng.afExt.utils.StringUtils,
  it.eng.sil.security.*" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);

int cdnUt = user.getCodut();
int cdnTipoGruppo = user.getCdnTipoGruppo();
String codCpi="";
if(cdnTipoGruppo == 1) {
  codCpi =  user.getCodRif();
}
boolean canExecuteQuery = true;

int verificaScelta=Integer.parseInt((String) serviceRequest.getAttribute("VERIFICA"));
if(verificaScelta == 1){
	if(codCpi.equals(""))
		canExecuteQuery = false;
}
//Titoli finestra
String title=null;
switch(verificaScelta) {
	case 1: 
		title="Soggetti con appuntamenti e stato occupazionale variato";
		break;
	case 2:
		title="Soggetti senza disponibilità territoriale";
		break;
	case 3:
		title="Soggetti esclusi dalla rosa dopo la DID";
		break;
	case 4:
		title="Soggetti pronti all'incrocio senza mansioni";
		break;
	case 5:
		title="Candidature inviate a Cliclavoro";
		break;

}


%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Ricerca Verifiche Lavoratore</title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>
<script language="javascript">
  
  
   function TornaAlleVerifiche() {
      var s= "AdapterHTTP?PAGE=VerLavoratoriPage";
	      s += "&CDNFUNZIONE=<%= _funzione %>";
	  setWindowLocation(s);
    }
    
    function toggleVisValCV() {

    	var divVisCV = document.getElementById("valCV");
    	 
    	 vis_valCV=(document.forms[0].OpzioneCV[0].checked);
         divVisCV.style.display=(vis_valCV)?"":"none";
    
    }
    
    function compDate(data1,data2) {
	// Metodo che permette il confronto fra due date
		
		if (data1!="" && data2!="") {
		   var y1,y2,m1,m2,d1,d2;
		   var tmpdata1,tmpdata2;
		     d1=data1.substring(0,2);
		     d2=data2.substring(0,2);
		     m1=data1.substring(3,5);
		     m2=data2.substring(3,5);
		     y1=data1.substring(6);
		     y2=data2.substring(6);
		  
		   tmpdata1=y1+m1+d1;
		   tmpdata2=y2+m2+d2;
		  
		   if(tmpdata1>tmpdata2)
		      return 1;
			// Ritorna 1 se la data1 e maggiore di data 2      
		   if(tmpdata2>tmpdata1)   
		      return 2;
		      // Ritorna 2 se la data2 e maggiore di data 1
		   if(tmpdata1==tmpdata2)   
		      return 0;
		      // Ritorna 0 se la data1 e uguale a data 2
		}
		return 0;
	}
    
    function checkDate(inputName){ 
    	  var datDalObj = eval("document.forms[0].DATADAL");
    	  var datAlObj = eval("document.forms[0].DATAAL");    	  
    	  datDal=datDalObj.value;
      	  datAl=datAlObj.value;
		  if (compDate(datDal, datAl)==1) { //DAL maggiore di AL
		  	alert("Data 'Dal' magiore della data 'Al'");
		  	return false;
		  }
	return true;
    
    }
             
    function checkDateInvioClicLavoro(inputName){
		var datInvioDalObj = eval("document.forms[0].DATAINVIO_DAL");
		var datInvioAlObj  = eval("document.forms[0].DATAINVIO_AL");
		var datInvioDal = datInvioDalObj.value;
		var datInvioAl  = datInvioAlObj.value;
		
		  if (compDate(datInvioDal, datInvioAl)==1) { //DAL maggiore di AL
		  	alert("Data 'Dal' magiore della data 'Al'");
		  	return false;
		  }
	return true;
	}

    function checkDateScadenzaClicLavoro(inputName){
		var datScadenzaDalObj = eval("document.forms[0].DATASCADENZA_DAL");
		var datScadenzaAlObj  = eval("document.forms[0].DATASCADENZA_AL");
		var datScadenzaDal = datScadenzaDalObj.value;
		var datScadenzaAl  = datScadenzaAlObj.value;
		
		  if (compDate(datScadenzaDal, datScadenzaAl)==1) { //DAL maggiore di AL
		  	alert("Data 'Dal' magiore della data 'Al'");
		  	return false;
		  }
	return true;
	}

	function valorizzaStrCPI(){
		var codCpiObj = eval("document.forms[0].CODCPI");
		var strCpiObj = eval("document.forms[0].STRCPI");
		strCpiObj.value = codCpiObj[codCpiObj.selectedIndex].text;
	}

	function valorizzaStrAmbitoDiffusione(){
		var codAmbitoObj = eval("document.forms[0].COD_AMBITO_DIFFUSIONE");
		var strAmbitoObj = eval("document.forms[0].STR_AMBITO_DIFFUSIONE");
		strAmbitoObj.value = codAmbitoObj[codAmbitoObj.selectedIndex].text;
	}

</script>
</head>
<body class="gestione" onload="rinfresca();">
<af:form name="Frm1" method="POST" action="AdapterHTTP">

<p class="titolo"><%=title%></p>

<%out.print(htmlStreamTop);%>
<p align="center">

<table class="main" align="center">

<% switch (verificaScelta) { 
	case 1:
%>

  <tr>
  <td class="etichetta" nowrap>
  Data appuntamento
  </td> 
  <td class="campo">
	  dal:&nbsp;<af:textBox type="date" name="DATADAL" value="" size="10" maxlength="10" validateOnPost="true" />&nbsp;&nbsp;
	  al:&nbsp;<af:textBox type="date" name="DATAAL" value="" size="10" maxlength="10" validateWithFunction="checkDate" validateOnPost="true" />
  </td>
  </tr>
  <tr>
  <input type="hidden" name="CODCPI" value="<%=codCpi%>"/>
<!--
  <td class="etichetta" nowrap>
	CPI Titolare:
  </td> 
  <td class="campo">
	 <af:comboBox name="CodCPItit" moduleName="M_ELENCOCPI" addBlank="true" selectedValue="" required="false"/>
  </td>
  </tr>
-->  
  <input type="hidden" name="SCADENZIARIO" value="VER1"/>
<% 	break;

	case 2:
%>
  
  <tr>
  <td class="etichetta" nowrap>
	CPI Titolare/Competente :
  </td> 
  <td class="campo">
	 <af:comboBox name="CodCPI" moduleName="M_ELENCOCPI" addBlank="true" selectedValue="" required="false"/>
  </td>
  </tr>
  <tr>
  <td class="etichetta" nowrap>
	Opzione CV:
  </td> 
  <td class="campo">
  	<table>
	  	<tr>
	  	<td>con validit&agrave;</td>
	  	<td><input type="radio" name="OpzioneCV" value="1" onclick="toggleVisValCV();" checked /></td>
	  	</tr>
	  	<tr>
	  	<td>senza validit&agrave;</td>
	  	<td><input type="radio" name="OpzioneCV" value="0" onclick="toggleVisValCV();" /></td>
	  	</tr>
    </table>
  </td>
  </tr>


  <tr id="valCV" style="">
  <td class="etichetta" nowrap>
	Stato di validità del CV:
  </td> 
  <td class="campo">
	 <af:comboBox name="statoValCV" moduleName="M_LISTSTATOLAV" addBlank="true" selectedValue="" required="false"/>
  </td>
  </tr>
  
  
  <input type="hidden" name="SCADENZIARIO" value="VER2"/>

<%

	break;
	case 3:
%>
<tr>
  <td class="etichetta" nowrap>
  Intervallo data D.I.D.
  </td> 
  <td class="campo">
	  dal:&nbsp;<af:textBox type="date" name="DATADAL" value="" size="10" maxlength="10" validateOnPost="true" />&nbsp;&nbsp;
	  al:&nbsp;<af:textBox type="date" name="DATAAL" value="" size="10" maxlength="10" validateWithFunction="checkDate" validateOnPost="true" />
  </td>
  </tr>
<tr>
  <td class="etichetta" nowrap>
	CPI Competente:
  </td> 
  <td class="campo">
	 <af:comboBox name="CodCPI" moduleName="M_ELENCOCPI" addBlank="true" selectedValue="" required="false"/>
  </td>
  </tr>  
  <input type="hidden" name="SCADENZIARIO" value="VER3"/>

<%
	break;
	case 4:

%>
<tr>
  <td class="etichetta" nowrap>
	CPI Titolare:
  </td> 
  <td class="campo">
	 <af:comboBox name="CodCPI" moduleName="M_ELENCOCPI" addBlank="true" title="CPI Titolare" selectedValue="" required="false"/>
  </td>
  </tr>  
  <input type="hidden" name="SCADENZIARIO" value="VER4"/>

<%
	break;
	case 5:
%>
	<tr>
  <td class="etichetta" nowrap>
	CPI intermediario:
  </td> 
  <td class="campo">
	 <af:comboBox onChange="valorizzaStrCPI()" name="CODCPI" moduleName="M_ELENCOCPI" addBlank="true" title="CPI Intermediario" selectedValue="" required="false"/>
	 <input name="STRCPI" value="" type="hidden"/>
  </td>
  </tr>  
  <tr>
  <td class="etichetta" nowrap>
  Data invio a Cliclavoro
  </td> 
  <td class="campo">
	  dal:&nbsp;<af:textBox type="date" name="DATAINVIO_DAL" value="" size="10" maxlength="10" validateOnPost="true" />&nbsp;&nbsp;
	  al:&nbsp;<af:textBox type="date" name="DATAINVIO_AL" value="" size="10" maxlength="10" validateWithFunction="checkDateInvioClicLavoro" validateOnPost="true" />
  </td>
  </tr>
  <tr>
  <td class="etichetta" nowrap>
  Data scadenza su Cliclavoro
  </td> 
  <td class="campo">
	  dal:&nbsp;<af:textBox type="date" name="DATASCADENZA_DAL" value="" size="10" maxlength="10" validateOnPost="true" />&nbsp;&nbsp;
	  al:&nbsp;<af:textBox type="date" name="DATASCADENZA_AL" value="" size="10" maxlength="10" validateWithFunction="checkDateScadenzaClicLavoro" validateOnPost="true" />
  </td>
  </tr>
  <tr>
  <td class="etichetta" nowrap>
	Ambito di diffusione:
  </td> 
  <td class="campo">
	 <af:comboBox onChange="valorizzaStrAmbitoDiffusione()" name="COD_AMBITO_DIFFUSIONE" moduleName="M_CL_COMBO_AMBITO_DIFFUSIONE" addBlank="true" title="Ambito di diffusione" selectedValue="" required="false"/>
	 <input name="STR_AMBITO_DIFFUSIONE" value="" type="hidden"/>
	 <input type="hidden" name="SCADENZIARIO" value="VER5"/>
  </td>
  </tr>  
	
<%	break;
}//end switch%>

<tr>
<td colspan="2">
  <br/>
  <input class="pulsanti" type="submit" name="cerca" value="Cerca" />
  <%
  if(canExecuteQuery){
  %>
  	<input type="hidden" name="ISCPI" value="" />
  <%
  }
  %>
  <br/>
</td>
</tr>
</table>
  <center>
  <table>
  <tr><td>
  <input class="pulsanti" type="button" name="btnBack" value="Torna alle lista delle verifiche" onclick="javascript:TornaAlleVerifiche();"/>
  </td></tr>
  </table>
  </center>
<p/>
<%out.print(htmlStreamBottom);%>
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
<input type="hidden" name="PAGE" value="SCADVerificheListaPage"/>
</af:form>
</body>
</html>
