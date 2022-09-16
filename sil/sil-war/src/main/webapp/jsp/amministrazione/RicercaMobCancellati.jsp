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
	<title>Lista cancellati</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>  
<af:linkScript path="../../js/"/>
<SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>

<script language="JavaScript">
function controllaDateRange(diraw, dfraw, range){
	var dataI = "";
    var dataF = "";
    var ONE_DAY = 1000 * 60 * 60 * 24;

	var di = diraw.value;
	var df = dfraw.value;
      
    
    if (di != "") {
      dataI = new String(di);
      var annoDataDal = dataI.substr(6,4);
      var meseDataDal = dataI.substr(3,2);
      var giornoDataDal = dataI.substr(0,2);
    } else {
    	return false;
    }

    if (df != "") {
      dataF = new String(df);
      var annoDataAl = dataF.substr(6,4);
      var meseDataAl = dataF.substr(3,2);
      var giornoDataAl = dataF.substr(0,2);
    } else {
    	return false;
    }

    var dataDal = new Date(annoDataDal, meseDataDal-1, giornoDataDal);
	var dataAl = new Date(annoDataAl, meseDataAl-1, giornoDataAl);

	var dataDal_ms = dataDal.getTime();
    var dataAl_ms = dataAl.getTime();
    var difference_ms = Math.abs(dataDal_ms - dataAl_ms);
    var delta = 1 + Math.round(difference_ms/ONE_DAY);   

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
	"Data comitato da... a... con periodo max 365 gg.\n";
		//Controlla che i campi non siano vuoti
		ok = controllaDateRange(document.frm1.dataInDA, document.frm1.dataInA, 365);
		if (ok) 
			{//controllo coerenza temporale
			if ((document.frm1.dataInDA.value != "") && (document.frm1.dataInA != "")) {
			      if (compareDate(document.frm1.dataInDA.value,document.frm1.dataInA.value) > 0) {
			      	alert(document.frm1.dataInDA.title + " maggiore di " + document.frm1.dataInA.title);
				    return false;
				  }	
				}
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
<p class="titolo">Lista cancellati</p>
<p align="center">
<af:form name="frm1" action="AdapterHTTP" onSubmit="checkCampiObbligatori()" method="GET">
<input  type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
<input  type="hidden" name="PAGE" value="ListaMobCancellatiPage">
<% out.print(htmlStreamTop); %> 
  <table>  
  <tr><td colspan="2"/>&nbsp;</td></tr>
  
  <tr>
	 <td class="etichetta" nowrap>Data comitato da</td>
	 <td class="campo" >
	    <af:textBox type="date" name="dataInDA" title="Data comitato da" validateOnPost="true" value="" size="10" maxlength="10" required="true"/>
	    a&nbsp;&nbsp;<af:textBox type="date" name="dataInA" title="Data comitato a" validateOnPost="true" value="" size="10" maxlength="10" required="true"/>
	 </td>            
  </tr>
  
  <tr> 
     <td class="etichetta">Tipo lista</td>
     <td class="campo">
		<af:comboBox classNameBase="input" name="CodTipoLista" title="Tipo lista" moduleName="M_MO_TIPO_LISTA" 
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
