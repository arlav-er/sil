<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
			it.eng.sil.security.*,
			it.eng.afExt.utils.*,
			it.eng.sil.util.*,
			java.math.*,
			java.util.*"
	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%
 	String  titolo = "Richiesta notifica RDC ad ANPAL";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 
	boolean canModify = true;

	// Lettura parametri dalla REQUEST
	String  cdnfunzioneStr   = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");
	
	// CONTROLLO ACCESSO ALLA PAGINA
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	
	boolean canView = filter.canView();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}

	// CONTROLLO PERMESSI SULLA PAGINA
	PageAttribs attributi = new PageAttribs(user, _page);

	boolean canRichiediNucleo = attributi.containsButton("GETNUCLEORDC");
	
	Object cdnUtCorrente    = sessionContainer.getAttribute("_CDUT_");
	String cdnUtCorrenteStr = StringUtils.getStringValueNotNull(cdnUtCorrente);
	
	String protocolloInps = null;
	String cfBeneficiario = null;
	// Stringhe con HTML per layout tabelle
  	
  	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
  
%>

<html>
<head>
<title><%= titolo %></title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<link rel="stylesheet" type="text/css" href="../../css/stiliTemplate.css"/>

<af:linkScript path="../../js/"/>
<%@ include file="../global/fieldChanged.inc" %>

<%-- INCLUDERE QUI ALTRI JAVASRIPT CON CLAUSOLE DEL TIPO:
<script language="Javascript" src="../../js/xxx.js"></script>
--%>
 
	<af:linkScript path="../../js/" />
	<script type="text/Javascript">
	
	  function tornaRicerca() {
	   		if (isInSubmit()) return;
		    var urlpage="AdapterHTTP?";
		    urlpage+="CDNFUNZIONE=<%=cdnfunzioneStr%>&PAGE=RicercaNotificheRDCPage";
			setWindowLocation(urlpage);
		  }
	 
		function gestisciCruscotto(btn){
			
				var isOk = true;
				var nomePage = btn.name;
				var urlpage="AdapterHTTP?";
				urlpage+="CDNFUNZIONE=<%=cdnfunzioneStr%>&PAGE=EsitoNucleoRDCPage&PROVENIENZA=GENERALE";
				 
				if(new String(nomePage).valueOf() == new String("nucleoCF").valueOf()){
					
					if(document.Frm1.cfBeneficiario.value.trim()== "")
					{
						alert("Il campo "+ document.Frm1.cfBeneficiario.title +" è obbligatorio");
						document.Frm1.cfBeneficiario.focus();
						isOk = false;
					}else if(document.Frm1.cfBeneficiario.value.trim().length != 16 && document.Frm1.cfBeneficiario.value.trim().length != 11) {
						alert("Il campo "+ document.Frm1.cfBeneficiario.title +" deve essere di 11 oppure di 16 caratteri");
						document.Frm1.cfBeneficiario.focus();
						isOk = false;
					}
					document.Frm1.TIPO_RICERCA.value = "CF";
					document.Frm1.PAGE.value = "EsitoNucleoRDCPage";
					urlpage += "&TIPO_RICERCA=CF";
					urlpage += "&cfBeneficiario="+document.Frm1.cfBeneficiario.value ;
				}else if(new String(nomePage).valueOf() == new String("nucleoProtInps").valueOf()){
					if(document.Frm1.codDomandaINPS.value.trim()== "")
					{
						alert("Il campo "+ document.Frm1.codDomandaINPS.title +" è obbligatorio");
						document.Frm1.codDomandaINPS.focus();
						isOk = false;
					}
					document.Frm1.TIPO_RICERCA.value = "INPS";
					document.Frm1.PAGE.value = "EsitoNucleoRDCPage";
					urlpage += "&TIPO_RICERCA=INPS";
					urlpage += "&codDomandaINPS="+document.Frm1.codDomandaINPS.value ;
				}
				if(isOk){
					  window.open(urlpage,'EsitoNucleoRDCPage','toolbar=NO,statusbar=YES,width=700,height=600,top=50,left=100,scrollbars=YES,resizable=YES');	
				}else{
					return false;
				}
			
		}
 
</script>

<style type="text/css">
td.etichetta{
vertical-align: top;
}
input.inputView{
 vertical-align: top;
}
td.campo {
 vertical-align: top;
}
</style>
	

</head>

<body class="gestione" onload="rinfresca()">

 	
<br/>
<p class="titolo"><%= titolo %></p>

	<p>
	 	<font color="green">
	  	</font>
	  	<font color="red"><af:showErrors /></font>
	</p>

<af:form name="Frm1" action="AdapterHTTP">
<input type="hidden" name="PAGE" value=""/>
<input type="hidden" name="TIPO_RICERCA" value=""/>
<af:textBox type="hidden" name="cdnfunzione" value="<%= cdnfunzioneStr %>" />
  
	<%out.print(htmlStreamTop);%>
	<%if(canRichiediNucleo){ %>
	
		<table class="main">
		<tr>
	        <td class="etichetta">
	       		CF Beneficiario
	        </td>
			<td class="campo">
				 <af:textBox name="cfBeneficiario" type="text"
							title="CF Beneficiario"
							value="<%=Utils.notNull(cfBeneficiario) %>"
							size="30"  
							classNameBase="input"
				/> 
			  	<input type="button" onclick="gestisciCruscotto(this)"  name="nucleoCF"
						  class="pulsanti" value="Richiesta per CF" />
	 		</td>
		</tr>
		<tr>
	        <td class="etichetta">
	       		Cod. Domanda INPS
	        </td>
			<td class="campo">
				 <af:textBox name="codDomandaINPS" type="text"
							title="Cod. Domanda INPS"
							value="<%= Utils.notNull(protocolloInps) %>"
							size="30"  
							classNameBase="input"
				/> 
			  	<input type="button" onclick="gestisciCruscotto(this)"  name="nucleoProtInps"
						  class="pulsanti" value="Richiesta per prot. INPS" />
	 		</td>
		</tr>
			    <tr><td colspan="2">&nbsp;</td></tr>
			 <tr>
				<td colspan="2" align="center">
					<input class="pulsante" type = "button" name="torna" value="Torna alla ricerca" onClick="tornaRicerca();"/>
				</td>
			</tr>
		</table>
	<%}%>
	 <%out.print(htmlStreamBottom);%>

</af:form>

</body>
</html>
