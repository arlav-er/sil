<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

        
<%@ page import=" com.engiweb.framework.base.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
    String cdnFunzione = (String)serviceRequest.getAttribute("CDNFUNZIONE");
    String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>
<head>
	<title>Lista mobilit&agrave; per Comitato/Iscritti con sospesi</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>  
<af:linkScript path="../../js/"/>
<SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>
<script language="JavaScript">
function controllaDateRange(di, df, range){
	
    var ONE_DAY = 1000 * 60 * 60 * 24;
    
	var dataDal_ms = di.getTime();
    var dataAl_ms = df.getTime();
    
    var difference_ms = Math.abs(dataDal_ms - dataAl_ms);
    var delta = Math.round(difference_ms/ONE_DAY); 
      
    //alert(di+" --- "+df+"--- DELTA="+delta);
	if (delta > range) {
		return false;
	}
    return true;
}
function checkCampiObbligatori()
{
	var ok;
	var msg;
	msg = "Parametro obbligatorio:\n"+
	"Data riunione con periodo max 365 gg.\n";
		var dataAl = new Date();//oggi

		if (document.frm1.dataRiunone.value != "") {
		      dataF = document.frm1.dataRiunone.value;
		      var annoDataAl = dataF.substr(6,4);
		      var meseDataAl = dataF.substr(3,2);
		      var giornoDataAl = dataF.substr(0,2);
		    } else {
		    	alert(msg);
		    	return false;
		    }	
		var dataT = new Date(annoDataAl, meseDataAl-1, giornoDataAl);
		//document.frm1.dataRiunone
		ok = controllaDateRange(dataT, dataAl, 365);
		
		if (ok) 
			{
			return (true);}
		else
		{	
			alert(msg);
			return (false);
		}
}

</script>

</head>
<body class="gestione" onload="rinfresca();">
<br>
<p class="titolo">Lista mobilit&agrave; per Comitato/Iscritti con sospesi</p>
<p align="center">
<af:form name="frm1" action="AdapterHTTP" method="GET">
<input  type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
<input  type="hidden" name="PAGE" value="ListaComitatoSilPage">
<% out.print(htmlStreamTop); %> 
  <table>  
  <tr><td colspan="2"/>&nbsp;</td></tr>
  <tr>
    <td class="etichetta" nowrap>Data CPM/Delibera Provinciale</td>
    <td ><af:textBox type="date" name="dataRiunone"  required="true" title="Data CPM" value="" size="12" maxlength="10"  validateOnPost="true"/>
    </td>
  </tr>
  
  <tr> 
     <td class="etichetta">Tipo lista</td>
     <td class="campo">
		<af:comboBox classNameBase="input" name="CodTipoLista" title="Tipo lista" 
			moduleName="M_MO_TIPO_LISTA" multiple="true"/>     
     </td>
  </tr>
  
  <tr> 
     <td class="etichetta">Stato della domanda</td>
     <td class="campo">
		<af:comboBox classNameBase="input" name="codEsito" title="Stato della domanda" moduleName="M_GetStatoMob" 
		addBlank="true" selectedValue=""/>     
     </td>
  </tr>
  
  <tr>
    <td class="etichetta" nowrap>Centro per l'Impiego</td>
    <td class="campo"><af:comboBox title="Centro per l'Impiego" name="CodCPI" moduleName="M_ELENCOCPI" 
    	addBlank="true" selectedValue=""/></td>
  </tr>

  <tr>
  <td colspan="2">&nbsp;</td>
  </tr>
  
  <tr>
    <td colspan="2" align="center">
    <input type="submit" class="pulsanti"  name="Cerca" value="Cerca">
    &nbsp;&nbsp;
    <input name="reset" type="reset" class="pulsanti" value="Annulla">
    &nbsp;&nbsp;      
    </td>
  </tr>
  </table>
  <%out.print(htmlStreamBottom);%>
  </af:form>

</body>
</html>
