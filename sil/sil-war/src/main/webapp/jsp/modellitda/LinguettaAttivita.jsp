<%@ page contentType="text/html;charset=utf-8"%>
 
<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*, java.math.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
	Object cdnUtCorrente = sessionContainer.getAttribute("_CDUT_");
	String cdnUtCorrenteStr = StringUtils.getStringValueNotNull(cdnUtCorrente);
	String _page = (String) serviceRequest.getAttribute("PAGE");
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	//NOTE: Attributi della pagina (pulsanti e link) 
	PageAttribs attributi = new PageAttribs(user, _page);
	boolean canModify = false;

	if (!filter.canView()) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}  
		
	canModify = attributi.containsButton("AggiungiModAttivita");
	 

	String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
	String prgModelloVoucher = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGMODVOUCHER");
	
	String descrizioneObiettivo = "";
	BigDecimal prgAzioni = null;
	String strPrgAzioni = "";
	boolean isAttivo = false;
	SourceBean rowObiettivo = (SourceBean)serviceResponse.getAttribute("M_GetDescrizioneObiettivo.ROWS.ROW");
	if(rowObiettivo != null){
		descrizioneObiettivo = StringUtils.getAttributeStrNotNull(rowObiettivo, "descrizioneObiettivo");
		prgAzioni = (BigDecimal) rowObiettivo.getAttribute("prgazioni");
		strPrgAzioni = prgAzioni.toString();
		String modelloAttivo = StringUtils.getAttributeStrNotNull(rowObiettivo, "Flgattivo");
		if(StringUtils.isFilledNoBlank(modelloAttivo) && modelloAttivo.equalsIgnoreCase("S")){
			isAttivo = true;
		}
	}
	String  titolo = "Dettaglio Modello TDA";
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
 %>

<html>
<head>
  
  <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  
  <link rel="stylesheet" type="text/css" href="../../css/jqueryui/jquery.selectBoxIt.css">
  <link rel="stylesheet" type="text/css" href="../../css/jqueryui/jqueryui-sil.css">
  
  <%@ include file="../global/fieldChanged.inc" %>
  
  <af:linkScript path="../../js/"/>
  <script src="../../js/jqueryui/jquery-1.12.4.min.js"></script>
  <script src="../../js/jqueryui/jquery-ui.min.js"></script>
  <script src="../../js/jqueryui/jquery.selectBoxIt.min.js"></script>
  
 
  <script type="text/javascript">
    $(function() {
    	$("[name='CODVCHATTIVITA']").selectBoxIt({
            theme: "default",
            defaultText: "Seleziona un'attività...",
            autoWidth: false
        });
       
    });
    </script>
 
</head>
<body  class="gestione" onload="rinfresca();">
<p class="titolo"><%= titolo %></p>
<p>
	<af:showErrors />
 	<af:showMessages prefix="M_AssociaAzioneModelloTda"/>
 	<af:showMessages prefix="M_DeleteModelloVoucherAssociabili"/>
</p>
<%
Linguette _linguetta = new Linguette(user, new Integer(_funzione).intValue() , _page, new BigDecimal(prgModelloVoucher)); 
_linguetta.setCodiceItem("PRGMODVOUCHER");
_linguetta.show(out);
%>
<center>
		<table>
		<tr><td colspan="2" align="center" style="font-weight: bold;">Obiettivo Misura:&nbsp;<%=descrizioneObiettivo%></td></tr>
		</table>
</center>
<p>
	<af:list moduleName="M_ListaModAttivitaTda" />
</p>

	<%if(canModify && !isAttivo){ %>
		<%out.print(htmlStreamTop);%>
		<table class="main">			
			<af:form name="form" action="AdapterHTTP" method="POST">

				<af:textBox type="hidden" name="PAGE" value="LinguettaAttivitaTdaPage" />
				<af:textBox type="hidden" name="cdnfunzione" value="<%=_funzione%>" />
				<af:textBox type="hidden" name="CDNUTINS" value="<%=cdnUtCorrenteStr%>" />
				<af:textBox type="hidden" name="PRGMODVOUCHER" value="<%=prgModelloVoucher%>" />
				<af:textBox type="hidden" name="PRGAZIONI" value="<%=strPrgAzioni%>" />
				<tr>
					<td class="etichetta" style="width: unset !important; text-align: center !important;">Attivit&agrave;&nbsp;</td>
					<td class="campo">
 							<af:comboBox classNameBase="input" 
								name="CODVCHATTIVITA" 
								moduleName="M_AzioniModelloVoucherAssociabili"
								addBlank="true" 
								blankValue="" 
								required="false"
								onChange="fieldChanged()"
								title="Attività"
							/>
						
 					</td>
 					<td><input class="pulsante" type="submit" name="confermaAssociazione" value="Inserisci" /></td>
				</tr>
				
			</af:form>
		</table>
	<%= htmlStreamBottom %>
<%}%>
			

</body>
</html>