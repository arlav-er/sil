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

String codMonoTempo = StringUtils.getAttributeStrNotNull(serviceRequest,"codMonoTempo");
String codCMTipoIscr = StringUtils.getAttributeStrNotNull(serviceRequest,"codCMTipoIscr");
String dataMovDa = StringUtils.getAttributeStrNotNull(serviceRequest,"dataMovDa");
String dataMovA = StringUtils.getAttributeStrNotNull(serviceRequest,"dataMovA");

String queryString=null;

double calcolaDec = 0;



%>

<%@ include file="../documenti/_apriGestioneDoc.inc"%>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT> 

  <script type="text/javascript">

	  function checkDate (strdata1, strdata2) {
		  annoVar1 = strdata1.substr(6,4);
		  meseVar1 = strdata1.substr(3,2);
		  giornoVar1 = strdata1.substr(0,2);
		  dataVarInt1 = parseInt(annoVar1 + meseVar1 + giornoVar1, 10);

		  annoVar2 = strdata2.substr(6,4);
		  meseVar2 = strdata2.substr(3,2);
		  giornoVar2 = strdata2.substr(0,2);
		  dataVarInt2 = parseInt(annoVar2 + meseVar2 + giornoVar2, 10);
			  
		  if (dataVarInt1 < dataVarInt2) {
		      return 1;
		  }
		  else {
		      if (dataVarInt1 > dataVarInt2) {
		        return 2;
		      }
		      else {
		        return 0;
		      }
		  }
	  }
    
	  function stampaLavoratori() {		
		  var eta;
		
		  var dataMovDa = document.form1.dataMovDa.value;
		  var dataMovA = document.form1.dataMovA.value;
		  var codMonoTempo = document.form1.codMonoTempo.value;
		  var codCMTipoIscr = document.form1.codCMTipoIscr.value;
		  var numGiorni;
		  
		  numGiorni = confrontaDate(dataMovDa, dataMovA);
		  
	      if(dataMovDa!="" && dataMovA!=""){
	      	  if(numGiorni >= 90){
	      	  	  alert("Il periodo di movimentazione deve essere minore di 90 giorni!");
	      	  }else{
		      	  if(checkDate(dataMovDa,dataMovA)==1){
					  apriGestioneDoc('RPT_STAMPA_LAVORATORI_MOV','&codMonoTempo='+codMonoTempo+'&codCMTipoIscr='+codCMTipoIscr+'&dataMovDa='+dataMovDa+'&dataMovA='+dataMovA, 'L68_MOV');
					  undoSubmit();
				  }else{
				  	  alert("Date di movimentazione scorrette");
				  }
			  }
		  }else{
			  alert("Inserire entrambe le date di movimentazione");
		  }
	  }
  
  </script>
 
</head>

<body class="gestione" onload="rinfresca()">
<br>
<p class="titolo">Stampa dei lavoratori che hanno avuto movimentazione</p>

<%out.print(htmlStreamTop);%>  

<af:form action="AdapterHTTP" name="form1" method="GET">
	
	<table class="main">
        <tr>
        	<td class="etichetta">Movimentazione avvenuta dal</td>
			<td class="campo">
				<af:textBox type="date" title="Data movimentazione dal" name="dataMovDa" value="<%=dataMovDa%>" required="true" size="12" maxlength="10" validateOnPost="true"/>
			   			&nbsp;&nbsp;al&nbsp;&nbsp;
			   			<af:textBox type="date" title="Data movimentazione al" name="dataMovA" value="<%=dataMovA%>" required="true" size="12" maxlength="10" validateOnPost="true"/>
			</td>
		</tr>
	    <tr colspan="2">&nbsp;</tr>
	    <tr>
	       <td class="etichetta">Tipo iscrizione</td>
		   <td class="campo">
		      <af:comboBox
			      name="codCMTipoIscr"
			      title="tipoIscr"
			      classNameBase="input"
			      disabled="false"
			      onChange="javaScript: fieldChanged(); ">
			      <option value=""  <% if ( "".equalsIgnoreCase(codCMTipoIscr) )  { %>SELECTED="true"<% } %> ></option>
			      <option value="D" <% if ( "D".equalsIgnoreCase(codCMTipoIscr) ) { %>SELECTED="true"<% } %> >Disabili</option>
			      <option value="A" <% if ( "A".equalsIgnoreCase(codCMTipoIscr) ) { %>SELECTED="true"<% } %> >Altre categorie protette</option>
		      </af:comboBox>
		   </td>
		</tr>		
	    <tr colspan="2">&nbsp;</tr>
	    <tr>
	       <td class="etichetta">Tempo</td>
		   <td class="campo">
		      <af:comboBox
			      name="codMonoTempo"
			      title="tipoIscr"
			      classNameBase="input"
			      disabled="false"
			      onChange="javaScript: fieldChanged(); ">
			      <option value=""  <% if ( "".equalsIgnoreCase(codMonoTempo) )  { %>SELECTED="true"<% } %> ></option>
			      <option value="I" <% if ( "I".equalsIgnoreCase(codMonoTempo) ) { %>SELECTED="true"<% } %> >Indeterminato</option>
			      <option value="D" <% if ( "D".equalsIgnoreCase(codMonoTempo) ) { %>SELECTED="true"<% } %> >Determinato</option>
		      </af:comboBox>
		   </td>
		</tr>		
	</table>
	
	<br/>
	<center><input type="button" class="pulsanti" value="Stampa" onclick="stampaLavoratori()" /></center>
	<br/>
</af:form>

<%out.print(htmlStreamBottom);%>

</body>
</html>


