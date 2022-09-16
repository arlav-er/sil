<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../../global/getCommonObjects.inc"%>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.module.movimenti.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);

String _page=StringUtils.getAttributeStrNotNull(serviceRequest,"page");
PageAttribs attributi = new PageAttribs(user, _page);

String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
String cdnLavoratore=StringUtils.getAttributeStrNotNull(serviceRequest,"cdnLavoratore");
String flgObbligoFormativo = "";
String flgObbligoScolastico = "";
%>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <script type="text/javascript">
  
   function controllaCampi(){
   	var flgFormativo = document.form1.flgObbligoFormativo.value;
   	var flgScolastico = document.form1.flgObbligoScolastico.value;
   	
   	if(flgFormativo == '' && flgScolastico == '') {
   		alert("E' necessario selezionare almeno un Obbligo di formazione e/o un Obbligo di istruzione");
   		return false;
   	}
   	return true;
  }
  
  
 
</script>
</head>

<body class="gestione" onload="rinfresca()">
<br/>
<p class="titolo">Stampa lavoratori con obbligo di istruzione</p>
<br/>
<center> 
<af:form action="AdapterHTTP" name="form1" method="GET" onSubmit="controllaCampi()">

<%out.print(htmlStreamTop);%>     

<table class="main">
	<tr>
    	<td class="etichetta">Obbligo di formazione</td> 
		<td class="campo">
			<af:comboBox title="Obbligo di formazione" classNameBase="input" name="flgObbligoFormativo">
        		<OPTION value="" ></OPTION>
        		<OPTION value="S">Assolto</OPTION>
        		<OPTION value="N">Non assolto</OPTION>
       		</af:comboBox>
		</td>
    </tr>
    <tr>
		<td class="etichetta">Obbligo di istruzione</td> 
		<td class="campo"> 
			<af:comboBox title="Obbligo di istruzione" classNameBase="input" name="flgObbligoScolastico">
        		<OPTION value="" ></OPTION>
        		<OPTION value="S">Assolto</OPTION>
        		<OPTION value="N">Non assolto</OPTION>
       		</af:comboBox>
		</td>
    </tr>
    <tr>
   		<td class="etichetta">Centro per l'Impiego</td> 
		<td class="campo">
			<af:comboBox name="codCpi" title="Centro per l'Impiego" moduleName="M_ElencoCPI" addBlank="true" selectedValue="" />
		</td>
	</tr> 
	<tr><td colspan="2">&nbsp;</td></tr> 
	<tr>
    	<td colspan="2" align="center">
        	<input type="submit" class="pulsanti"  name="cerca" value="Cerca" />
          	&nbsp;&nbsp;
          	<input type="reset" class="pulsanti" value="Annulla" />
          	&nbsp;&nbsp;
	    </td>
    </tr>
    <input type="hidden" name="PAGE" value="ListaLavOIPage"/>
    <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
</table>

<%out.print(htmlStreamBottom);%>
</af:form>
</center>        
</body>
</html>