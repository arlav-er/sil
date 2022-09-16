<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                com.engiweb.framework.security.*,it.eng.afExt.utils.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.User,
                it.eng.sil.util.*,
                 java.math.*"
%>

<%@ taglib uri="aftags" prefix="af" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);

String _page = (String)serviceRequest.getAttribute("PAGE");

String pagamenti = StringUtils.getAttributeStrNotNull(serviceRequest,"pagamenti");
String dataRichDa = StringUtils.getAttributeStrNotNull(serviceRequest,"dataRichDa");
String dataRichA = StringUtils.getAttributeStrNotNull(serviceRequest,"dataRichA");
String PROVINCIA_ISCR = StringUtils.getAttributeStrNotNull(serviceRequest,"PROVINCIA_ISCR");

String queryString=null;

%>

<%@ include file="../documenti/_apriGestioneDoc.inc"%>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/"/>
   <SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT> 
  <script type="text/javascript">

function compDate(date1,date2) {
    if (date1==null || date1=="") return -1;    
    if (date2==null || date2=="") return 1;
    if ( toDate(date1).getTime()<toDate(date2).getTime()) return -1;
    if ( toDate(date1).getTime()>toDate(date2).getTime()) return 1;
    //if ( toDate(date1).getTime()==toDate(date2).getTime()) 
    return 0;
}

  	function controllaDate() {
		
		ok=true;
		
		validateDate('dataRichDa');
  		validateDate('dataRichA');
  		
  		if (document.form1.dataRichDa.value == "") {
  			alert("Data richiesta dal è obbligatoria");
  			document.form1.dataRichDa.focus();
			ok=false;
  		}
  		else if (document.form1.dataRichA.value == "") {
  			alert("Data richiesta al è obbligatoria");
  			document.form1.dataRichA.focus();
			ok=false;
  		}
  		else {  				
			if (compareDate(document.form1.dataRichDa.value, document.form1.dataRichA.value) > 0) {
				alert("Data richiesta dal maggiore della Data richiesta al");
				ok=false;
			}
		}
		
		return ok;
   	}
  
  	function stampaEsonero () {
		if (!controllaFunzTL()) return;
		
		var pagamenti;
		
		var dataRichDa = document.form1.dataRichDa.value;
		var dataRichA = document.form1.dataRichA.value;
		var ambitoTerrEs = document.form1.PROVINCIA_ISCR.value;
		
		var calcola=document.form1.pagamenti.length;
	      for (var i = 0 ; i< calcola ; i++)
	        { 
	        	if(document.form1.pagamenti[i].checked) { 
	        		pagamenti=document.form1.pagamenti[i].value; 
	        		break; 
	        	} 
	        }
		
		apriGestioneDoc('RPT_STAMPA_ESONERI','&pagamenti='+pagamenti+'&dataRichDa='+dataRichDa+'&dataRichA='+dataRichA+'&PROVINCIA_ISCR='+ambitoTerrEs,'AZESON');  
		undoSubmit();
	}
 
</script>
 
</head>

<body class="gestione" onload="rinfresca()">
<br>
<p class="titolo">Stampa esoneri</p>

<%out.print(htmlStreamTop);%>  

<af:form action="AdapterHTTP" name="form1" method="GET" onSubmit="controllaDate()">

	<table class="main">
		 <tr>
	          <td class="etichetta">Pagamenti:</td>
	          <td class="campo">
	          <table colspacing="0" colpadding="0" border="0">
	          <tr>
	          <%if (pagamenti.equals("tutti")) {%>
	           <td><input type="radio" name="pagamenti" value="noRegola"/> non in regola&nbsp;&nbsp;&nbsp;&nbsp;</td>
	           <td><input type="radio" name="pagamenti" value="tutti" CHECKED /> tutti</td>
	          <%} else {%>
	           <td><input type="radio" name="pagamenti" value="noRegola" CHECKED /> non in regola&nbsp;&nbsp;&nbsp;&nbsp;</td>
	           <td><input type="radio" name="pagamenti" value="tutti" /> tutti</td>
	          <%}%>
	          </tr>
	          </table>
	          </td>
	        </tr>
	        
	        <tr>
	        	<td class="etichetta">Data richiesta: dal</td>
				<td class="campo">
					<af:textBox type="date" title="Data richiesta dal" name="dataRichDa" value="<%=dataRichDa%>" required="true" size="12" maxlength="10" validateOnPost="true" classNameBase="input" />
				   	&nbsp;&nbsp;al:&nbsp;&nbsp;
				   	<af:textBox type="date" title="Data richiesta al" name="dataRichA" value="<%=dataRichA%>" required="true" size="12" maxlength="10" validateOnPost="true" classNameBase="input" />
				</td>
			</tr>
			
			<tr>
			    <td class="etichetta">Ambito Territoriale</td>
			    <td colspan=3 class="campo">
			    	<af:comboBox name="PROVINCIA_ISCR" moduleName="CM_GET_PROVINCIA_ISCR" selectedValue="<%=PROVINCIA_ISCR%>"
			        	classNameBase="input" addBlank="true" required="true" title="Ambito Territoriale" />
			    </td>
			</tr>
			
		 </table>
	
	<br/>
		<center><input type="button" class="pulsanti" value="Stampa" onclick="controllaDate() && stampaEsonero()" /></center>
	<br/>
</af:form>

<%out.print(htmlStreamBottom);%>

</body>
</html>

