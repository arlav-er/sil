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

%>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <script type="text/javascript">
  
   function creaStringa(){
  		var tipoCond = '';
 		var countSel = 0;
 		for (i=0;i<document.form1.codtipoCondizione.length;i++) {
   			if (document.form1.codtipoCondizione[i].selected) {
   				countSel = countSel + 1;
  				if (tipoCond == '') {
					tipoCond = document.form1.codtipoCondizione[i].value;
				}
				else {
					tipoCond = tipoCond + ',' + document.form1.codtipoCondizione[i].value;
				}
		 	}
		}
		
		document.form1.tipoCondizione.value=tipoCond;
		return true;
	}
  
  
 
</script>
</head>

<body class="gestione" onload="rinfresca()">
<br/>
<p class="titolo">Stampa lavoratori tipo condizione</p>
<br/>
<center> 
<af:form action="AdapterHTTP" name="form1" method="GET" onSubmit="creaStringa()">

<%out.print(htmlStreamTop);%>     

<table class="main">
	<tr>
    	<td class="etichetta">Solo tipi condizione aperte</td>
        <td class="campo">
        	<Input type="checkBox" title="Solo tipi condizione aperte" name="tipoCondAperteCheck" value="on" onclick=""/>
        </td>
    </tr>
    <tr>
		<td class="etichetta">Tipo condizione</td>
        <td class="campo">
        	<input type="hidden" name="tipoCondizione"/>
        	<af:comboBox classNameBase="input" title="Tipo condizione" name="codtipoCondizione" moduleName="M_TipoCondizione" required="true" multiple="true" 
        				 size="12" onChange="">
       	</af:comboBox> 
      </td>
    </tr>
    <tr>
   		<td class="etichetta">Centro per l'Impiego</td> 
		<td class="campo">
			<af:comboBox name="codCpi" title="Centro per l'Impiego" moduleName="M_ElencoCPI" addBlank="true" selectedValue=""/>
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
    <input type="hidden" name="PAGE" value="ListaTipoCondizionePage"/>
    <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
</table>

<%out.print(htmlStreamBottom);%>
</af:form>
</center>        
</body>
</html>