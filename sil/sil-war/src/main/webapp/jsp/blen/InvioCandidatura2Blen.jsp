<%@page import="it.eng.sil.module.cigs.bean.BeanUtils"%>
<%@page import="com.engiweb.framework.error.EMFErrorSeverity"%>
<%@page import="com.engiweb.framework.error.EMFUserError"%>
<%@page import="com.engiweb.framework.error.EMFErrorHandler"%>
<%@page import="org.apache.commons.lang.time.FastDateFormat"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page
	import="com.engiweb.framework.base.*,com.engiweb.framework.dispatching.module.AbstractModule,com.engiweb.framework.util.QueryExecutor,com.engiweb.framework.security.*,com.engiweb.framework.configuration.ConfigSingleton,it.eng.sil.security.User,it.eng.sil.security.PageAttribs,it.eng.sil.security.ProfileDataFilter,it.eng.sil.util.*,it.eng.afExt.utils.*,java.util.*,java.math.*,java.io.*,java.lang.*,java.text.*"%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>
<%

String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
String pageName = Utils.notNull(serviceRequest.getAttribute("PAGE"));
ProfileDataFilter filter = new ProfileDataFilter(user, pageName);
filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
boolean canViewByProfile=filter.canViewLavoratore();
if (! canViewByProfile){
	response.sendRedirect( request.getContextPath()+ "/servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
}else{
	
String codCpi = null;
	int cdnUt = user.getCodut();
	int cdnTipoGruppo = user.getCdnTipoGruppo();
	if (cdnTipoGruppo == 1) {
		codCpi = user.getCodRif();
	}
%>
<%
Object candidaturaObj = serviceResponse.getAttribute("M_CL_GET_CANDIDATURA.ROWS.ROW");
SourceBean candidaturaRow  = null;
String prgcandidaturaObj  = "";
String codcandidatura  = "N/A";
String desc_stato_invio  = "IN ATTESA DI PRIMO INVIO";
String codstatoinviocl  = "PA";
FastDateFormat fdf = FastDateFormat.getInstance("dd/MM/yyyy");
String data_invio = fdf.format(new Date());
String data_invio_old = data_invio;
String is_insert = "";
String data_scad_cand = Utils.notNull(serviceRequest.getAttribute("DATA_SCAD_CAND"));
String dataOdierna = DateUtils.getNow();

if(candidaturaObj==null){
	prgcandidaturaObj="";
	is_insert="1";
	
	if(data_scad_cand==null || data_scad_cand.equals("")){
		data_scad_cand = DateUtils.aggiungiNumeroGiorni(dataOdierna, 60);
	}	
	
}else {
	 candidaturaRow= (SourceBean) serviceResponse.getAttribute("M_CL_GET_CANDIDATURA.ROWS.ROW");
	 BeanUtils bu = new BeanUtils(candidaturaRow);
	 prgcandidaturaObj =  bu.getObjectToString( "prgcandidatura");
	 codcandidatura =  bu.getObjectToString( "CODCANDIDATURA");
	 codstatoinviocl =  bu.getObjectToString( "CODSTATOINVIOCL");
	 data_invio_old =  bu.getObjectToString( "DATA_INVIO");
	 data_scad_cand =  bu.getObjectToString( "DATA_SCAD_CAND");
	 desc_stato_invio =  bu.getObjectToString( "DESC_STATO_INVIO");
	 codCpi =  bu.getObjectToString( "codCpi");

}

boolean isCandidaturaNonValida = false; 
if (DateUtils.compare(new Date(), DateUtils.getDate(data_scad_cand)) > 0) {
	isCandidaturaNonValida = true;
}




	String codiceFiscale = (String) serviceRequest.getAttribute("CODICEFISCALE");
	InfCorrentiLav infCorrentiLav = new InfCorrentiLav(
			sessionContainer, cdnLavoratore, user);
	String cdnFunzione = (String) serviceRequest.getAttribute("cdnFunzione");
	
	String dataFine = null;
	String numKloPrivacy = null;

	
	BigDecimal cdnUtins = null;
	String dtmins = null;
	BigDecimal cdnUtmod = null;
	String dtmmod = null;
	Testata operatoreInfo = null;

	/*
	ProfileDataFilter filter = new ProfileDataFilter(user, pageName);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	boolean canView = filter.canViewLavoratore();
	*/

	SourceBean esisteCvValidoSb = (SourceBean) serviceResponse
			.getAttribute("M_CL_ESISTE_CV_VALIDO.ROWS.ROW");
	boolean esisteCvValido = !(esisteCvValidoSb == null || esisteCvValidoSb
			.getContainedAttributes().isEmpty());
	
	// check cittadinanza valida
	SourceBean esisteCittadinanzaValidaSb = (SourceBean) serviceResponse.getAttribute("M_CL_CHECK_CITTADINANZA_VALIDA.ROWS.ROW");
	boolean esisteCittadinanzaValida = !(esisteCittadinanzaValidaSb == null || esisteCittadinanzaValidaSb.getContainedAttributes().isEmpty());
	
	// se non vi sono esperienze
	SourceBean checkTipoEsperienzaSb = (SourceBean) serviceResponse.getAttribute("M_CL_CHECK_TIPO_ESPERIENZA");
	boolean existTipoEsperienza = checkTipoEsperienzaSb.containsAttribute("ROWS.ROW");
	
	// se non vi sono esperienze con contratto
	SourceBean checkTipoEsperienzaContrattiSb = (SourceBean) serviceResponse.getAttribute("M_CL_CHECK_TIPO_ESPERIENZA_CONTRATTO");
	boolean existTipoEsperienzaContratti = checkTipoEsperienzaContrattiSb==null?false:checkTipoEsperienzaContrattiSb.containsAttribute("ROWS.ROW");
		 
	PageAttribs attributi = new PageAttribs(user, pageName);
	boolean canSincronizza = attributi.containsButton("INVIO");
	boolean canModify = attributi.containsButton("INVIO") && esisteCvValido && !isCandidaturaNonValida && esisteCittadinanzaValida;	
	boolean canAnteprima = attributi.containsButton("ANTEPRIMA");

	String readOnlyString = String.valueOf(!canModify);

	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);


	String codCpiCapoluogo = "";
	try {
		SourceBean cpiSB = (SourceBean)serviceResponse.getAttribute("M_CL_GET_CPI_CAPOLUOGO.ROWS.ROW");
		codCpiCapoluogo = cpiSB.getAttribute("CODCPICAPOLUOGO").toString();
	} catch (Exception e) { }
	
%>
<html>
<head>
<title>Sincronizza il curriculum con Cliclavoro</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

<af:linkScript path="../../js/"/>

<script type="text/javascript"  src="../../js/layers.js" ></script>

<script type="text/javascript" language="JavaScript">
	// Rilevazione Modifiche da parte dell'utente
	var flagChanged = false;
	function fieldChanged() {
 	<%if (!readOnlyString.equalsIgnoreCase("true")) {%> 
    	flagChanged = true;
 	<%}%> 
	}
	
	 function gestioneCliclavoro() {	 
			// Apre la finestra
			var url = "AdapterHTTP?PAGE=BL_LIST_INVII_CANDIDATURE_PAGE&CODICEFISCALE=<%=codiceFiscale%>"+
			"&CDNLAVORATORE=<%=cdnLavoratore%>&TIPOLOGIA=1"+"&popUp=true";
			 window.location.assign(url);
		  }
	 

</script>


<script type="text/javascript" language="Javascript">    


	function checkTipoEsperienza() {
		
		var tipoEsperienzaContratti = document.forms["frm"].existTipoEsperienzaContratti.value;		
		var tipoEsperienza = document.forms["frm"].existTipoEsperienza.value;		

		if (tipoEsperienza == "true") {
		
			var msg1 = "Esistono esperienze di lavoro la cui tipologia di contratto non dispone di una codifica ministeriale valida e necessaria per l'invio ";
			var msg2 = "dell'esperienza a Cliclavoro.\nProseguire comunque con l'invio della candidatura?";
			var msg = msg1 + msg2;
			
			//if (tipoEsperienzaContratti == "false") {
			//	return confirm(msg);
			//}
		}
		return true;	
	}	

	
	function checkDataScadenza() {
		var dataScad  = document.forms["frm"].elements["DATA_SCAD_CAND"].value;
		var dataInvio = document.forms["frm"].elements["DATA_INVIO_OLD"].value;

		if (dataScad == null || dataScad === "") {
			alert("La data di scadenza è obbligatoria.");
			return false;
		}

		var arr1 = dataScad.split("/");
		var arr2 = dataInvio.split("/");

		var d_Scad  = new Date(arr1[2],arr1[1]-1,arr1[0]);
		var d_Invio = new Date(arr2[2],arr2[1]-1,arr2[0]);

		var r_Scad  = d_Scad.getTime();
		var r_Invio = d_Invio.getTime();
	
		if (r_Scad <= r_Invio){
			alert("La data di scadenza dev'essere successiva alla data d'invio.");
			return false;
		}

		return true;	
	}	


	function apriAnteprimaInvio() {
		//var prgRichiestaAz = document.Frm1.prgRichiestaAz.value;
		var cdnLavoratore = document.frm.cdnLavoratore.value;
		var codCpi = document.frm.codCpiHidden.value;
		var dataInvio = document.frm.DATA_INVIO.value;
		
    	var opened;
        var f = "AdapterHTTP?PAGE=CL_INVIO_CANDIDATURA_ANTEPRIMA&cdnLavoratore="+cdnLavoratore+"&codCpi="+codCpi+"&dataInvio="+dataInvio;
        var t = "_blank";
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=650,top=30,left=180";
        opened = window.open(f, t, feat);
    }
	
	
</script>
</head>
<body class="gestione">
	<%
		InfCorrentiLav testata = new InfCorrentiLav(RequestContainer
				.getRequestContainer().getSessionContainer(),
				cdnLavoratore, user);
		testata.setSkipLista(true);
		testata.show(out);
	%>
	<%
		if (!esisteCvValido) {
			EMFErrorHandler engErrorHandler = responseContainer
					.getErrorHandler();
			EMFUserError userError = new EMFUserError(
					EMFErrorSeverity.WARNING,
					MessageCodes.ClicLavoro.NON_ESISTE_CV_VALIDO,
					null,
					"Errore. Per il lavoratore di cf:"
							+ codiceFiscale
							+ " non esiste un curriculum valido alla data attuale");
			engErrorHandler.addError(userError);
		}
		if (!esisteCittadinanzaValida) {
			EMFErrorHandler engErrorHandler = responseContainer
					.getErrorHandler();
			EMFUserError userError = new EMFUserError(
					EMFErrorSeverity.WARNING,
					MessageCodes.ClicLavoro.CITTADINANZA_NON_VALIDA,
					null,
					"Attezione: la cittadinanza indicata per il lavoratore risulta essere una codifica scaduta");
			engErrorHandler.addError(userError);
		}
	%>
	<%--
	<af:showMessages prefix="M_CLCANDIDATURA"/> 
	 --%>
	<font color="red"><af:showErrors showUserError="true" /> </font>
	<af:showMessages prefix="M_CLCANDIDATURA"/> 
	<center>
		<p class="titolo">Sincronizza il curriculum con BLEN</p>
	</center>
	<center>
		<af:form name="frm" action="AdapterHTTP" method="post" onSubmit="checkDataScadenza() && checkTipoEsperienza() ">
			<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>">
			<input type="hidden" name="prgcandidatura" value="<%=prgcandidaturaObj%>">
			<input type="hidden" name="CODICEFISCALE" value="<%=codiceFiscale%>">
			<input type="hidden" name="cdnFunzione" value="<%=cdnFunzione%>">
			<input type="hidden" name="codCpiHidden" value="<%=codCpiCapoluogo%>">
			<input type="hidden" name="TIPOLOGIA" value="1">
			<input type="hidden" name="EXEC_SEND" value="YES">
			<%
			if("1".equalsIgnoreCase(is_insert)){
			%>
			<input type="hidden" name="is_insert" value="<%=is_insert%>">
			<%
			}
			%>
			<input type="hidden" name="PAGE" value="<%=pageName%>">
			<input type="hidden" name="codtipocomunicazionecl" value="01_BLEN">
			<input type="hidden" name="AMBITO_DIFFUSIONE" value="01">
			 
			<input type="hidden" name="DATA_INVIO" value="<%=data_invio%>">
			<input type="hidden" name="codstatoinviocl" value="<%=codstatoinviocl%>">
			<input type="hidden" name="existTipoEsperienza" value="<%=existTipoEsperienza%>"/>
			<input type="hidden" name="existTipoEsperienzaContratti" value="<%=existTipoEsperienzaContratti%>"/>
			<!-- solo x il nuovo -->
			
			<%
				out.print(htmlStreamTop);
			%>
			<table class="main">
				<!--
				<tr>
					<td class="etichetta">Codice Candidatura</td>
			
					<td class="campo"><af:textBox 
							classNameBase="input"  name="codcandidatura"
							value="<%=codcandidatura%>" validateOnPost="true"
							readonly="true" size="30" />
					</td>
				</tr>
				-->
				<input type="hidden" name="codcandidatura" value="<%=codcandidatura%>" />
				<tr>
					<td class="etichetta">Data Invio</td>
					<td class="campo">
						<% if (esisteCvValido && esisteCittadinanzaValida) { %>
							<af:textBox 
							classNameBase="input" type="date" name="DATA_INVIO_OLD"
							value="<%=data_invio_old%>" validateOnPost="true" 
							readonly="true" size="11" maxlength="11" />
						<% } %>
					</td>
				</tr>
				
				  
				<tr>
					<td class="etichetta">Data scadenza</td>
					<td class="campo">		<af:textBox onKeyUp="fieldChanged();"
							classNameBase="input" type="date" name="DATA_SCAD_CAND"
							value="<%=data_scad_cand%>"
							validateOnPost="true" size="11" maxlength="11"
							readonly="<%=readOnlyString%>" />
					</td>
				</tr>

				 <input type="hidden" name="codCpi" value="<%=codCpiCapoluogo%>" />
			</table>
			<table>							
				<tr>	
					<% if(canAnteprima) { %>
						
						<td><input type="button" class="pulsante" name="anteprima" value="Anteprima Invio" onclick="apriAnteprimaInvio();"/></td>
						
					<%	} %>
					<% if (canSincronizza) {			
					String bName="Sincronizza"; //var
					
						if (canModify && ( 
								(prgcandidaturaObj == null || "".equalsIgnoreCase(prgcandidaturaObj)) ||
								"PI".equalsIgnoreCase(codstatoinviocl) ||
								"VI".equalsIgnoreCase(codstatoinviocl)) 
						) {
							//if (esisteCvValido) {
								//if(	candidaturaObj==null || "PA".equals(codstatoinviocl)){
								//	bName="Sincronizza"; // inv
								//}						
						%>
							<td><input type="submit" class="pulsanti" name="Invio" value="<%=bName%>"> 
						<% } else { %>
							<td><input type="submit" class="pulsanteDisabled" name="Invio" value="<%=bName%>" disabled="disabled">
						<% } %>
					</td>
					<% } %>
					<td colspan="2" align="center"><input type="button"
						class="pulsanti" value="Chiudi" onClick="window.close();">
					</td> 
					<td colspan="2" align="center"><input type="button"
						class="pulsanti" value="Torna a Lista" onClick="gestioneCliclavoro();">
					</td>
				</tr>
			</table>
			<%
				out.print(htmlStreamBottom);
					if (operatoreInfo != null) {
			%>
			<table>
				<tr>
					<td align="center">
						<%
							operatoreInfo.showHTML(out);
						%>
					</td>
				</tr>
			</table>
			<%
				} //if (operatoreInfo != null)
			%>
		</af:form>
	</center>
</body>
</html>
<% } %>
<%--
--%>