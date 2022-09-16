<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
			it.eng.afExt.utils.*,
			it.eng.sil.coop.webservices.clicLavoro.candidatura.*,
			it.eng.sil.security.*,
			it.eng.sil.util.*,
			java.math.*,
			java.util.*,java.text.*,org.apache.commons.lang.time.DateUtils"
	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%
	String  titolo = "Invio Massivo Curriculum Vitae";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	int cdnfunzione = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	
	// CONTROLLO ACCESSO ALLA PAGINA
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	boolean canView = filter.canView();
	if (!canView) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}

	String codCpiCapoluogo = "";
	try {
		SourceBean cpiSB = (SourceBean)serviceResponse.getAttribute("M_CL_GET_CPI_CAPOLUOGO.ROWS.ROW");
		codCpiCapoluogo = cpiSB.getAttribute("CODCPICAPOLUOGO").toString();
	} catch (Exception e) { }
	
	String codcandidatura  = "N/A";
	String codstatoinviocl  = "PA";
	
	SourceBean countCV = (SourceBean) serviceResponse.getAttribute("M_Num_Tmp_Massivo.ROWS.ROW");
	String numTotCV = StringUtils.getAttributeStrNotNull(countCV, "NUMTOT");
	
	SourceBean countIncorso = (SourceBean) serviceResponse.getAttribute("M_Check_Invio_InCorso.ROWS.ROW");
	String numInCorso = countIncorso.getAttribute("NUM").toString();
	
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>


<html>
<head>
<title><%= titolo %></title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />

 <af:linkScript path="../../js/"/>
 <script language="Javascript">

   function invioMassivoCV() {
	//if (isInSubmit()) return;
     var  numCVDaElaborare = document.forms["form"].elements["numCVDaCaricare"].value;	    
     var t="Verranno inviati " + numCVDaElaborare + " curriculum vitae dal SIL al Portale LavoroXTe.\n";
     t += "Confermi l'operazione ?";
    
     if ( confirm(t) ) {
    	return true; 
     }else{ 
        return false;
      }	  
   } 

</script>
</head>

<body class="gestione">

<af:showErrors />

<p class="titolo"><%= titolo %></p>

<af:form name="form" action="AdapterHTTP" method="POST" onSubmit="invioMassivoCV()">
<input type="hidden" name="PAGE" value="INVIO_MASSIVO_CV_PAGE" />
<input type="hidden" name="cdnFunzione" value="<%=cdnfunzione%>" />

<%= htmlStreamTop %>
<table class="main">
	<tr>
		<td colspan="2">
			<div class="sezione2">Curriculum Vitae da trasferire </div>
		</td>
	</tr>

	<tr>
		<td class="etichetta">Numero Curriculum Vitae da trasferire in totale</td>
		<td class="campo">
			<af:textBox name="numCV" type="text"
						value="<%= numTotCV %>"
						size="40" maxlength="101"
						required="false" classNameBase="input" readonly="true" />										
		</td>
	</tr>
    <tr>
		<td class="etichetta">Numero Curriculum Vitae da caricare</td>
		<td class="campo">
		<af:textBox name="numCVDaCaricare" type="integer" title="Numero di CV da inviare"
						value=""
						size="5" 
						required="true" validateOnPost="true" classNameBase="input"/>		
		</td>
	</tr>
	<tr>
		<td colspan="2">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="2">
			<span class="bottoni">
			<% if((new Integer(numInCorso)).intValue()  == 0 ){%>
				<input type="submit" class="pulsanti" name="invio" value="Invio Massivo" />
			<% }else{%>
			    <input type="submit" class="pulsantiDisabled" name="invio" value="Invio Massivo"  disabled="true"/>
			<%  } %>
			</span>
		</td>
	</tr>
</table>
<%= htmlStreamBottom %>
<input type="hidden" name="codCpi" value="<%=codCpiCapoluogo%>" />
<input type="hidden" name="codtipocomunicazionecl" value="01"/>
<input type="hidden" name="codcandidatura" value="<%=codcandidatura%>" />
<input type="hidden" name="codstatoinviocl" value="<%=codstatoinviocl%>">
</af:form>
</body>
</html>
