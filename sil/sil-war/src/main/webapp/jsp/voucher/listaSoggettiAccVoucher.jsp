<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
			it.eng.sil.security.User,
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

	

	// Lettura parametri dalla REQUEST
	//int     cdnfunzione      = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	//int cdnfunzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));

	String  cdnfunzioneStr   = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");
	String  cdnLavoratore    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
	String  prgAzienda       = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgAzienda");
	String  prgUnita         = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"prgUnita");
	
	
	String token = "_TOKEN_" + "ListaPage";
	String goBackInf = "Torna alla pagina di ricerca";
	String goBackUrl = (String) sessionContainer.getAttribute(token.toUpperCase());
	if (StringUtils.isEmpty(goBackUrl)) {
		goBackInf = "Torna alla pagina di ricerca";
		goBackUrl = "PAGE=CercaSoggettiAccreditatiVoucherPage&cdnfunzione=" + cdnfunzioneStr;
	}
	
	String  titolo = "Lista Soggetti Accreditati per i Voucher";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	

	// CONTROLLO ACCESSO ALLA PAGINA
	/* Commentato per test
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	boolean canView = filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}
	*/

	// CONTROLLO PERMESSI SULLA PAGINA
	PageAttribs attributi = new PageAttribs(user, _page);

	boolean canModify = attributi.containsButton("aggiorna");
	boolean canDelete = attributi.containsButton("rimuovi");
	
	Object cdnUtCorrente    = sessionContainer.getAttribute("_CDUT_");
	String cdnUtCorrenteStr = StringUtils.getStringValueNotNull(cdnUtCorrente);
	
	// Sola lettura: viene usato per tutti i campi di input
	String readonly = String.valueOf( ! canModify );
	
	// Stringhe con HTML per layout tabelle
	String htmlStreamTop    = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
	
	
	
	int cdnUt = user.getCodut();
	int cdnTipoGruppo = user.getCdnTipoGruppo();
	String strCodCpi;
	strCodCpi =  user.getCodRif();
	//Solamente per testare ricordare di eliminare la riga
	canModify=true;
	
	
		String cfSel=" ";
	 String descComuniSel=" ";
	 String denominazioneSel="";
	 String codComune="";
	 String tipoRicerca="";
	 
	   cfSel  = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cfSel");
	   if (StringUtils.isEmpty(cfSel)){
		   cfSel= SourceBeanUtils.getAttrStrNotNull(serviceResponse, "cfSel");
	   }
	   //alert(cpi);
	   descComuniSel=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "descComuniSel");
	   if (StringUtils.isEmpty(descComuniSel)){
		   descComuniSel= SourceBeanUtils.getAttrStrNotNull(serviceResponse, "descComuniSel");
	   }
  		
	   denominazioneSel=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "denominazioneSel");
	   if (StringUtils.isEmpty(denominazioneSel)){
		   denominazioneSel= SourceBeanUtils.getAttrStrNotNull(serviceResponse, "denominazioneSel");
	   }
  		
	   codComune=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "codComuneSel");
	   if (StringUtils.isEmpty(codComune)){
		   codComune= SourceBeanUtils.getAttrStrNotNull(serviceResponse, "codComuneSel");
	   }
	   
	   tipoRicerca=SourceBeanUtils.getAttrStrNotNull(serviceRequest, "tipoRicerca");
	   if (StringUtils.isEmpty(tipoRicerca)){
		   tipoRicerca= SourceBeanUtils.getAttrStrNotNull(serviceResponse, "tipoRicerca");
	   }
	   
	   sessionContainer.delAttribute("cfRicerca");
	   sessionContainer.delAttribute("descComuniRicerca");  
	   sessionContainer.delAttribute("denominazioneRicerca");	 
	   sessionContainer.delAttribute("codComuneRicerca");
	   sessionContainer.delAttribute("tipoRicercaRicerca");
	
%>
<html>
<head>
<title><%= titolo %></title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

<af:linkScript path="../../js/"/>
<%@ include file="../global/fieldChanged.inc" %>



<script language="Javascript">

	/* Funzione per tornare alla pagina precedente */
	function goBack() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
	
		goTo("<%= goBackUrl %>");
	}

	/* Funzione chiamata al caricamento della pagina */
	function onLoad() {
		rinfresca();
		// altri funzioni da richiamare sulla onLoad...
	}

	function chiamaPaginaInserimentoAcc(){


		  doFormSubmit(document.frmListaSogAcc);
		 
	}

<%
	// Genera il Javascript che si occuperà di inserire i links nel footer
	attributi.showHyperLinks(out, requestContainer, responseContainer, "cdnLavoratore="+cdnLavoratore);
%>

</script>
</head>

<body class="gestione" onload="onLoad()">



<p class="titolo"><%= titolo %></p>
<font color="red">
<af:showErrors />
</font>
<font color="green">
<af:showMessages />
</font>



	<af:form name="frmListaSogAcc" action="AdapterHTTP" method="POST">

	<input type="hidden" name="PAGE" value="chiamaInserimentoAccPage"/>
	<input type="hidden" name="cdnfunzione" value="<%= cdnfunzioneStr %>"/>
	<input type="hidden" name="cfSel" value="<%= cfSel %>"/>
	<input type="hidden" name="descComuniSel" value="<%= descComuniSel %>"/>
	<input type="hidden" name="denominazioneSel" value="<%= denominazioneSel %>"/>
	<input type="hidden" name="codComuneSel" value="<%= codComune %>"/>
	<input type="hidden" name="tipoRicerca" value="<%= tipoRicerca %>"/>
	
	<%= htmlStreamTop %>
	<table class="main">
	


	<%--
		Campi  della Totali Budget non presenti
	--%>
	<%-- ***************************************************************************** --%>
	<p align="center">
	<tr>
			
			<td class="campo">
					<af:list moduleName="M_ListaSogAccVoucher"  />
			</td>
	</tr>
	<tr>
		<td class="campo">&nbsp;</td>
	</tr>
	</p>
	</table>
	
	
	
	<table  align="center" >
	

	
		<tr>
			<td colspan="2">
			
				<input type="button" class="pulsante" name="back" value="<%= goBackInf %>"
						onclick="goBack()" />
				&nbsp;
				<input type="button" class="pulsante" name="nuovoSogg" value="Nuovo Soggetto"
						onclick="chiamaPaginaInserimentoAcc()" />
			</td>
		</tr>
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
	

	</table>
	<%= htmlStreamBottom %>
	
</af:form>
</body>
</html>	



