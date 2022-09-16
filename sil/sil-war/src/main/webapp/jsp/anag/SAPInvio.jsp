<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page
	import="
  com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.User,
  it.eng.sil.security.ProfileDataFilter,  
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  it.eng.sil.security.PageAttribs,   
  com.engiweb.framework.security.*"%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%
	String cdnLavoratore = (String) serviceRequest.getAttribute("CDNLAVORATORE");
	String _current_page = (String) serviceRequest.getAttribute("PAGE");
	ProfileDataFilter filter = new ProfileDataFilter(user, _current_page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	PageAttribs attributi = new PageAttribs(user, _current_page);
	
	boolean canAnnullaSAP = attributi.containsButton("ANNULLA_SAP");
	boolean canCallSAP = attributi.containsButton("RICHIESTA_SAP");
	boolean canInvioSAP = attributi.containsButton("INVIO_SAP");
	boolean canVerificaSAP = attributi.containsButton("VERIFICA_ESISTENZA_SAP");
	boolean canVerificaSAP_Portale = attributi.containsButton("VERIFICA_ESISTENZA_SAP_PORTALE");
	boolean canRegistroSAP = attributi.containsButton("REGISTRO_INVII_SAP");
	
	boolean canView = filter.canViewLavoratore();
	if (!canView) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}
	//String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");

	String codMinSap = "";
	String txtNotaCV = "";
	String codstatoSAP = "";
	String statoSAP = "";
	String dtInvioMin = "";
	String dtInizioVal = "";
	String dtFineVal = "";
	
	Testata operatoreInfo = null;
	
	BigDecimal cdnUtIns = new BigDecimal(0);
	String dtmIns = "";
	BigDecimal cdnUtMod = new BigDecimal(0);;
	String dtmMod = "";
		
	String codMonoTipoCpi = "";
	SourceBean rowCompetenza = (SourceBean) serviceResponse.getAttribute("M_GetCpiLavoratore.ROWS.ROW");
	if (rowCompetenza != null) {
		codMonoTipoCpi = StringUtils.getAttributeStrNotNull(rowCompetenza, "CODMONOTIPOCPI");		
	}	
		
	Vector vectConoInfo = serviceResponse.getAttributeAsVector("M_SapGestioneServiziGetCodMin.ROWS.ROW");
	if ((vectConoInfo != null) && (vectConoInfo.size() > 0)) {

		SourceBean beanLastInsert = (SourceBean) vectConoInfo.get(0);

		codMinSap = StringUtils.getAttributeStrNotNull(beanLastInsert, "CODMINSAP");
		
		codstatoSAP = StringUtils.getAttributeStrNotNull(beanLastInsert, "CODSTATO");
		
		statoSAP = StringUtils.getAttributeStrNotNull(beanLastInsert, "STRDESCRIZIONE");
		
		dtInvioMin = StringUtils.getAttributeStrNotNull(beanLastInsert, "DATAINVIOMIN");
		
		dtInizioVal = StringUtils.getAttributeStrNotNull(beanLastInsert, "DATINIZIOVAL");
		
		dtFineVal = StringUtils.getAttributeStrNotNull(beanLastInsert, "DATFINEVAL");				
		
		
		cdnUtIns = (BigDecimal) beanLastInsert.getAttribute("CDNUTINS");
	    dtmIns = StringUtils.getAttributeStrNotNull(beanLastInsert, "DTMINS");
	    cdnUtMod = (BigDecimal) beanLastInsert.getAttribute("CDNUTMOD");	   	   
	    dtmMod = StringUtils.getAttributeStrNotNull(beanLastInsert, "DTMMOD");
	}

	Object codiceUtenteCorrente = sessionContainer.getAttribute("_CDUT_");

	InfCorrentiLav infCorrentiLav = new InfCorrentiLav(sessionContainer, cdnLavoratore, user);
	Testata testata = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);

	String _page = (String) serviceRequest.getAttribute("PAGE");
	int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
	Linguette linguette = new Linguette(user, _funzione, _page, new BigDecimal(cdnLavoratore));

	String htmlStreamTop = StyleUtils.roundTopTable(canCallSAP);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canCallSAP);
	
	
	boolean disableRichiestaSap = false;
	if (("").equalsIgnoreCase(codMinSap)) {
		disableRichiestaSap = true;
	}
	
	boolean disableAnnullaSap = false;
	if (("").equalsIgnoreCase(codMinSap) || ("03").equalsIgnoreCase(codstatoSAP)) {
		disableAnnullaSap = true;
	}	
	else {
		if (!("C").equalsIgnoreCase(codMonoTipoCpi)) {
			disableAnnullaSap = true;
		}
	}
	
	operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
	
%>

<html>

<head>
<title>Invio SAP</title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />

<af:linkScript path="../../js/" />

<SCRIPT TYPE="text/javascript">
var urlpage="AdapterHTTP?";    

function getURLPageBase() {    
  urlpage+="CDNFUNZIONE=<%=_funzione%>&";
  return urlpage;
}
function callAnnullaSapWS() {
    if(!confirm("Sicuro di voler annullare la SAP?")) {
        return false;
    }

	var urlpage="AdapterHTTP?";
    urlpage+="CODMINSAP=<%=codMinSap%>&";
    urlpage+="cdnLavoratore=<%=cdnLavoratore%>&";    
    urlpage+="PAGE=SapGestioneServiziPage&";
    urlpage+="MODULE=M_SapCallAnnullaSap&";
    urlpage+="CDNFUNZIONE=<%=_funzione%>";
    disableBtn();
    setWindowLocation(urlpage);    
}
function callWS() {
     if(!confirm("Sicuro di voler invocare la richiesta SAP?"))
      { return false;
      }
    
    var urlpage="AdapterHTTP?";
    urlpage+="CDNFUNZIONE=<%=_funzione%>&PAGE=SapVisualizzaPage&MODULE=M_SapCallRichiestaSap&CODMINSAP=<%=codMinSap%>&CDNLAVORATORE=<%=cdnLavoratore%>";
   
    window.open(urlpage,'RichiestaSAP','toolbar=NO,statusbar=YES,width=700,height=600,top=50,left=100,scrollbars=YES,resizable=YES');
    // setWindowLocation(urlpage);
}
function callVerificaEsistenzaSAPWS() {
    if(!confirm("Sicuro di voler invocare la verifica esistenza SAP?")) {
        return false;
    }
    var urlpage="AdapterHTTP?";
    urlpage +="CDNFUNZIONE=<%=_funzione%>&PAGE=SapVerificaEsistenzaPage&MODULE=M_SapCallVerificaEsistenzaSap&CDNLAVORATORE=<%=cdnLavoratore%>";
    
    window.open(urlpage,'VerificaSAP','toolbar=NO,statusbar=YES,width=700,height=200,top=50,left=100,scrollbars=YES,resizable=YES');

}

function callVerificaEsistenzaSAPWS_Portale() {
    if(!confirm("Sicuro di voler invocare la verifica esistenza SAP al Portale?")) {
        return false;
    }
    var urlpage="AdapterHTTP?";
    urlpage +="CDNFUNZIONE=<%=_funzione%>&PAGE=SapPortaleVerificaEsistenzaPage&MODULE=M_SapCallVerificaEsistenzaSapPortale&CDNLAVORATORE=<%=cdnLavoratore%>";
    
    window.open(urlpage,'VerificaSAP_Portale','toolbar=NO,statusbar=YES,width=700,height=400,top=50,left=100,scrollbars=YES,resizable=YES');

}

function callInviaSapWS() {
    if(!confirm("Sicuro di voler inviare la SAP?")) {
        return false;
    }
    
    var urlpage="AdapterHTTP?";
    urlpage+="cdnLavoratore=<%=cdnLavoratore%>&";    
    urlpage+="PAGE=SapGestioneServiziPage&";
    urlpage+="MODULE=M_GestioneInvioSap&";
    urlpage+="CDNFUNZIONE=<%=_funzione%>";
    disableBtn();
    setWindowLocation(urlpage);  
}

function disableBtn() {
	if (document.getElementById("btnInviaSap") != null) {
		document.getElementById("btnInviaSap").disabled = true;
	}
	if (document.getElementById("btnRichiestaSap") != null) {
		document.getElementById("btnRichiestaSap").disabled = true;
	}
	if (document.getElementById("btnVerificaSap") != null) {
		document.getElementById("btnVerificaSap").disabled = true;
	}
	if (document.getElementById("btnAnnullaSap") != null) {
		document.getElementById("btnAnnullaSap").disabled = true;
	}
	if (document.getElementById("btnRegistroInvii") != null) {
		document.getElementById("btnRegistroInvii").disabled = true;
	}
 }


function callRegistroInvii() {
   
   var urlpage="AdapterHTTP?";
   urlpage+="CDNFUNZIONE=<%=_funzione%>&PAGE=RegistroInviiSapPage&CDNLAVORATORE=<%=cdnLavoratore%>";
  
   window.open(urlpage,'RegistroInviiSAP','toolbar=NO,statusbar=YES,width=700,height=600,top=50,left=100,scrollbars=YES,resizable=YES');
}

</SCRIPT>
<script language="Javascript">
<!--Contiene il javascript che si occupa di aggiornare i link del footer-->
	
<%//Genera il Javascript che si occuperà di inserire i links nel footer
			attributi.showHyperLinks(out, requestContainer, responseContainer,
					"cdnLavoratore=" + cdnLavoratore);%>
	
</script>
</head>

<body class="gestione" onload="rinfresca()">

	<%
		infCorrentiLav.show(out);
			linguette.show(out);
	%>
    <script language="Javascript">
      	if(window.top.menu != undefined){
    	    window.top.menu.caricaMenuLav( <%=_funzione%>,   <%=cdnLavoratore%>);
    	}
    </script>	

<font color="red">
	<af:showErrors/>
</font>
<font color="green">
 	<af:showMessages prefix="M_GestioneInvioSap"/>
</font>
<font color="green">
 	<af:showMessages prefix="M_SapCallAnnullaSap"/>
</font>

	<af:form method="POST" action="AdapterHTTP" name="MainForm"
		id="MainForm" dontValidate="true">

		<input type="hidden" name="PAGE" value="SapGestioneServiziPage">
		<input type="hidden" name="MODULE" value="M_InsertUpdateNote" />
		<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"%>
		<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>" />

		<input type="hidden" name="CDNUTINS" value="<%=codiceUtenteCorrente%>" />
		<input type="hidden" name="CDNUTMOD" value="<%=codiceUtenteCorrente%>" />

		<p align="center">
			<%
				out.print(htmlStreamTop);
			%>
		
		<table class="main">
			<tr>
				<td class="etichetta" align="right">Codice SAP Ministeriale</td>
				<td align="left"><strong><%=codMinSap%></strong></td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td class="etichetta" align="right">Stato</td>
                <td align="left"><strong><%=statoSAP%></strong></td>
			</tr>
			<tr>
				<td colspan="6">&nbsp;</td>
			</tr>
			<tr>
                <td class="etichetta" align="right">Data Invio SAP al Ministero</td>
                <td align="left"><%=dtInvioMin%></td>
                <td>&nbsp;</td>
				<td>&nbsp;</td>
                <td class="etichetta">Data inizio Stato</td>
                <td align="left"><%=dtInizioVal%></td>
            </tr>
            <!-- 
            <tr>
                <td class="etichetta" align="right">Data Modifica</td>
                <td align="left"><%=dtmMod%></td>
                <td>&nbsp;</td>
				<td>&nbsp;</td>
                <td class="etichetta" align="right">Data fine validit&agrave;</td>
                <td align="left"><%=dtFineVal%></td>
            </tr>
            -->
            <tr>
                <td colspan="6">&nbsp;</td>
            </tr>
            <tr>
                <td colspan="6"><hr/></td>
            </tr>
			<tr>	
				<td>			
					<% if(canAnnullaSAP){ %>				
					<input class="pulsanti<%=((disableAnnullaSap)?"Disabled":"")%>" onclick="callAnnullaSapWS()" id="btnAnnullaSap"
						type="button" name="annulla" value="Annulla SAP" <%=(disableAnnullaSap)?"disabled=\"True\"":""%>>
					<% } %>
				</td>
				<td>
					<% if(canCallSAP){ %>				
					<input class="pulsanti<%=((disableRichiestaSap)?"Disabled":"")%>" onclick="callWS()" id="btnRichiestaSap"
						type="button" name="richiesta" value="Richiesta SAP"  <%=(disableRichiestaSap)?"disabled=\"True\"":""%>>
					<% } %>
				</td>	
				<td>
					<% if(canInvioSAP){ %>	
					<input class="pulsante" onclick="callInviaSapWS()" type="button" name="invio" value="Invio SAP" id="btnInviaSap">
					<% } %>
				</td>				
				<td>
					<% if(canVerificaSAP){ %>	
					<input class="pulsante" onclick="callVerificaEsistenzaSAPWS()" type="button" name="verifica" value="Verifica Esistenza SAP" id="btnVerificaSap">
					<% } %>
				</td>
			</tr>
			
			<% if(canRegistroSAP){ %>
			<tr>
				<td colspan="6" align="center">
					<input class="pulsante" onclick="callRegistroInvii();" type="button" name="registroInvii" value="Registro Invii SAP" id="btnRegistroInvii">
				</td>
			</tr>
			<%}%>
			
		</table>
		<br />

		<%
			out.print(htmlStreamBottom);
				testata.showHTML(out);      
		%>		
		
	</af:form>
</body>

</html>
