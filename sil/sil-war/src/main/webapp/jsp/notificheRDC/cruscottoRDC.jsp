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
 	String  titolo = "Cruscotto RDC";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 
	boolean canModify = false;

	// Lettura parametri dalla REQUEST
	String  cdnfunzioneStr   = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");
	String  cdnLavoratore    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
	
	// CONTROLLO ACCESSO ALLA PAGINA
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	
	boolean canView = filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}

	// CONTROLLO PERMESSI SULLA PAGINA
	PageAttribs attributi = new PageAttribs(user, _page);

	boolean canDettaglio = attributi.containsButton("DETTAGLIO_NOTIFICA_RDC");
	boolean canRichiediCF = attributi.containsButton("WS_NUCLEO_CF");
	boolean canRichiediProtInps = attributi.containsButton("WS_NUCLEO_PROT_INPS");
	
	Object cdnUtCorrente    = sessionContainer.getAttribute("_CDUT_");
	String cdnUtCorrenteStr = StringUtils.getStringValueNotNull(cdnUtCorrente);
	
	BigDecimal prgAmRDC =null;
	String strPrgAmRDC = null;
	String protocolloInps = null;
	String statoDomInps = null;
	String strDataDomanda = null;
	String strCodiceFiscaleRich = null;
	String codRuolo = null;
	String codRuoloDescr = null;
	String cfBeneficiario = null;
	
	boolean isUltimaNotifica = false;
 
	SourceBean row = null;
	//
	 if(serviceResponse.containsAttribute("M_UltimaNotificaRDC")){
		
		row= (SourceBean) serviceResponse.getAttribute("M_UltimaNotificaRDC.ROWS.ROW");
		if(row!=null){
			isUltimaNotifica = true;
			prgAmRDC = SourceBeanUtils.getAttrBigDecimal(row, "PRGRDC", null);
			strPrgAmRDC = prgAmRDC.toString();
			protocolloInps= SourceBeanUtils.getAttrStrNotNull(row, "STRPROTOCOLLOINPS");
			statoDomInps= SourceBeanUtils.getAttrStrNotNull(row, "CODSTATOINPS");
			strDataDomanda = SourceBeanUtils.getAttrStrNotNull(row, "STRDATDOMANDA");
			strCodiceFiscaleRich = SourceBeanUtils.getAttrStrNotNull(row, "STRCFRICHIEDENTE");
			codRuolo = SourceBeanUtils.getAttrStrNotNull(row, "CODMONORUOLO");
			codRuoloDescr = SourceBeanUtils.getAttrStrNotNull(row, "DESCRCODMONORUOLO");
			cfBeneficiario = SourceBeanUtils.getAttrStrNotNull(row, "STRCODICEFISCALE");
		}
	} 
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
	<script type="text/javascript"  src="../../js/script_comuni.js"></script>
	<script type="text/Javascript">
	 
		function gestisciCruscotto(btn){
			
			var nomePage = btn.name;
			if(new String(nomePage).valueOf() == new String("dettaglioNotifica").valueOf()){
				document.Frm1.PAGE.value = "NotificaRDCPage";
				document.Frm1.TIPO_RICERCA.value = "CRUSCOTTO";
				document.Frm1.submit();
			}else {
				var isOk = true;
				var urlpage="AdapterHTTP?";
				urlpage+="CDNFUNZIONE=<%=cdnfunzioneStr%>&PAGE=EsitoNucleoRDCPage&CDNLAVORATORE=<%=cdnLavoratore%>";
				 
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
			
			
		}

	/* Funzione chiamata al caricamento della pagina */
	function onLoad() {
		rinfresca();
		//rinfrescaLaterale();
		// altri funzioni da richiamare sulla onLoad...
	}
	
 
<%
	// Genera il Javascript che si occuperà di inserire i links nel footer
	attributi.showHyperLinks(out, requestContainer, responseContainer, "cdnLavoratore="+cdnLavoratore);
%>

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

<body class="gestione" onload="onLoad()">

<%
	// TESTATA LAVORATORE
	if (StringUtils.isFilled(cdnLavoratore)) {
		InfCorrentiLav testata = new InfCorrentiLav(cdnLavoratore, user);
		testata.show(out);
	}

%>
 
    <script language="Javascript">
      	if(window.top.menu != undefined){
    	    window.top.menu.caricaMenuLav( <%=cdnfunzioneStr%>,   <%=cdnLavoratore%>);
    	}
    </script>	
<br/>
<p class="titolo"><%= titolo %></p>

	<p>
	 	<font color="green">
	 		<%-- <af:showMessages prefix="M_UltimaNotificaRDC"/> --%>
	  	</font>
	  	<font color="red"><af:showErrors /></font>
	</p>

<af:form name="Frm1" action="AdapterHTTP">
<input type="hidden" name="PAGE" value=""/>
<input type="hidden" name="TIPO_RICERCA" value=""/>
<af:textBox type="hidden" name="cdnlavoratore" value="<%= cdnLavoratore %>" />
<af:textBox type="hidden" name="cdnfunzione" value="<%= cdnfunzioneStr %>" />
<af:textBox type="hidden" name="PRGRDC" value="<%= strPrgAmRDC %>" />
 
	<%out.print(htmlStreamTop);%>
	<div align="center">
 	<table class="main">
 			<tr>
				<td><br /></td>
			</tr>
			<tr>
				<td colspan="4"><p class="titolo">Ultima Notifica RDC</p></td>
			</tr>
			<tr ><td colspan="4" ><hr width="90%"/></td></tr>
		</table>
	</div>	
	
	
	<table class="main">
		<tr>
	        <td class="etichetta">
	       		Data Domanda
	        </td>
			<td class="campo">
			 <af:textBox name="dataDomanda" type="text"
							title="data Domanda INPS"
							value="<%= Utils.notNull(strDataDomanda) %>"
							size="15" maxlength="101"
							classNameBase="input"
							readonly="true"
				/>
	 		</td>
		</tr>
		<tr>
	        <td class="etichetta">
	       		Stato Domanda
	        </td>
			<td class="campo">
			 <af:textBox name="domandaInps" type="text"
							title="Stato Domanda INPS"
							value="<%= Utils.notNull(statoDomInps) %>"
							size="30" maxlength="101"
							classNameBase="input"
							readonly="true"
				/>
	 		</td>
		</tr>
		<tr>
	        <td class="etichetta">
	       		Protocollo INPS
	        </td>
			<td class="campo">
			 <af:textBox name="protInps" type="text"
							title="Protocollo INPS"
							value="<%= Utils.notNull(protocolloInps) %>"
							size="45" maxlength="101"
							classNameBase="input"
							readonly="true"
				/>
	 		</td>
		</tr>
		<tr>
	        <td class="etichetta">
	       		CF Richiedente
	        </td>
			<td class="campo">
			 <af:textBox name="cfRich" type="text"
							title="cf richiedente"
							value="<%= Utils.notNull(strCodiceFiscaleRich) %>"
							size="20" maxlength="101"
							classNameBase="input"
							readonly="true"
				/>
	 		</td>
		</tr>
		<tr>
	        <td class="etichetta">
	       		Ruolo
	        </td>
			<td class="campo">
				 <af:textBox name="codRuolo" type="text"
							title="codRuolo"
							value="<%= Utils.notNull(codRuolo) %>"
							size="3"  
							classNameBase="input"
							readonly="true"
				/>&nbsp;-&nbsp;
			  	<af:textBox name="descrCodRuolo" type="text"
							title="descrCodRuolo"
							value="<%= Utils.notNull(codRuoloDescr) %>"
							size="50"  
							classNameBase="input"
							readonly="true"
				/>
	 		</td>
		</tr>
		<%if(canDettaglio){ %>
		<tr>
				<td colspan="2" align="center">
				<%if(isUltimaNotifica){ %>
					<input type="button" onclick="return gestisciCruscotto(this)"  name="dettaglioNotifica"
						  class="pulsanti" value="Dettaglio" />
				<%}else{ %>
				<input type="button" onclick="return gestisciCruscotto(this)"  name="dettaglioNotifica" disabled
						  class="pulsanti" value="Dettaglio" />
				<%} %>
				</td>
		</tr>
		<%} %>
	</table>
	<%if(canRichiediCF || canRichiediProtInps){ %>
		<div align="center">
	 	<table class="main">
	 			<tr>
					<td><br /></td>
				</tr>
				<tr>
					<td colspan="4"><p class="titolo">Richieste dati Nucleo Familiare</p></td>
				</tr>
				<tr ><td colspan="4" ><hr width="90%"/></td></tr>
			</table>
		</div>	
	
		<table class="main">
			 <%if(canRichiediCF){ %>
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
			<%}%>
			 <%if(canRichiediProtInps){ %>
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
			<%}%>
		</table>
	<%}%>
	 <%out.print(htmlStreamBottom);%>

</af:form>

</body>
</html>
