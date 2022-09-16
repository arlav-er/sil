<%@ page contentType="text/html;charset=utf-8"%>
 
<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.module.voucher.*, it.eng.sil.util.*,
                it.eng.sil.security.*,
                java.math.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<% 

  boolean canModify = true;
  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page);
 
  String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
  String prgModelloVoucher = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGMODVOUCHER");
  String strPrgAzioni = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGAZIONI");
  
 
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>
<html>
<head>
  <title>Modifica Tipologia Servizio</title>
  <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  <%@ include file="../global/fieldChanged.inc" %>
  
  <af:linkScript path="../../js/"/>
	
<script language="Javascript">

function aggiornaModificaTipologiaServizio() {	
 	if(isRequired("CODTIPOSERVIZIO")){
 		var url = "AdapterHTTP?PAGE=ModelloTdaMainPage";
		url += "&CDNFUNZIONE=<%=_funzione%>";
		url += "&PRGMODVOUCHER=<%=prgModelloVoucher%>";
	 	url += "&PRGAZIONI=<%=strPrgAzioni%>";
	 	url += "&CODTIPOSERVIZIO="+ document.frm1.CODTIPOSERVIZIO.value;
		url += "&confermaCambioTipologia=confermaCambioTipologia";
		window.opener.location.replace(url);
		window.close();		
 	}
	 
}

</script>
 </head>
<body  class="gestione" onload="rinfresca();">
<p>
	<font color="green">
  	</font>
  	<font color="red"><af:showErrors /></font>
</p>
<p class="titolo">Modifica Tipologia Servizio</p>

<%@page import="it.eng.sil.module.voucher.Properties"%>
<af:form name="frm1" action="AdapterHTTP" method="POST">

<%out.print(htmlStreamTop);%>
<af:showErrors />
<af:showMessages prefix="M_AggiornaTipoServizio"/>
<p align="center">
	<table class="main">
	<tr>
  		<td class="etichetta">Tipologia Servizio&nbsp;</td>
  		<td class="campo">
  	 	  <af:comboBox classNameBase="input"
							name="CODTIPOSERVIZIO" 
							moduleName="M_ComboTipoServizio"
							addBlank="true" 
							blankValue="" 
							required="false"
							onChange="fieldChanged()"
							title="Tipologia Servizio"
						/>
		 
		</td>
		<td>
				<input class="pulsante" type="button"  name="confermaCambioTipologia" value="Salva" onclick="aggiornaModificaTipologiaServizio();"/>
			</td>
	</tr>

</af:form>

</table>
	
	<br>
    <table>
		<tr>
			<td colspan="2" align="center">
				<input class="pulsante" type = "button" name="torna" value="Chiudi" onClick="window.close();"/>
			</td>
		</tr>
 		
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
	 
</table>
<%= htmlStreamBottom %>


</body>
</html>