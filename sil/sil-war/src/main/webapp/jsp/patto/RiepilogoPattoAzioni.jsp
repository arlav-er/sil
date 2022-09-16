<!-- @author: Giovanni Landi - Settembre 2016 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>


<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
				  com.engiweb.framework.tags.DefaultErrorTag,
                  it.eng.sil.security.*,
                  it.eng.sil.module.movimenti.*,
                  java.math.*,
                  java.io.*,
                  it.eng.sil.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  com.engiweb.framework.util.JavaScript,
                  com.engiweb.framework.dbaccess.sql.TimestampDecorator,
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*,
                  java.text.*,
                  java.sql.*,
                  oracle.sql.*,
                  java.util.*"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	//Attributi della pagina
	String _page = (String) serviceRequest.getAttribute("PAGE");
  	PageAttribs attributi = new PageAttribs(user, _page);
  	boolean canRecuperaAdesione = false;
  	InfCorrentiLav testata = null;
  	String htmlStreamTop = null;
  	String htmlStreamBottom = null;
  	String dataAdesionePA = null;
  	Object mesiAnzianitaAdesioneDid = null;
  	String queryString = "";
  	
	String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
	String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");
	
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	boolean canView = filter.canViewLavoratore();
	
	if (!canView) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}
	
	canRecuperaAdesione = attributi.containsButton("RECUPERA_ADESIONE");
	htmlStreamTop = StyleUtils.roundTopTable(false);
  	htmlStreamBottom = StyleUtils.roundBottomTable(false);
  	dataAdesionePA = (String) serviceResponse.getAttribute("M_RiepilogoPacchettoAdulti.DATAADESIONEPA");
  	mesiAnzianitaAdesioneDid = serviceResponse.getAttribute("M_RiepilogoPacchettoAdulti.MESIANZIANITAADESIONEDID");
%>

<html>
<head>
	<title>Riepilogo Patto/Azioni</title>
	
	<%@ include file="../documenti/_apriGestioneDoc.inc"%>
	
  	<link rel="stylesheet" type="text/css" href="../../css/stili.css">
  	<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
  	<af:linkScript path="../../js/"/>
  
  	<script language="Javascript">	

  	function settaOperazione(operazione) {
  		document.frm1.OPERAZIONE.value = operazione;	
  	}

  	function stampaLista() {
  		// viene aperto nella pagina chiamante: "creiamo il frameset" che permette il ritorno indietro
		var urlpage="AdapterHTTP?PAGE=REPORTFRAMEPAGE&cdnLavoratore=<%=cdnLavoratore%>" +
							"&ACTION_REDIRECT=RPT_RIEPILOGO_PATTO_AZIONI&asAttachment=false&apri=true&QUERY_STRING="+HTTPrequest;
		setWindowLocation(urlpage);
	}
  	
  	</script>
  	
  	<script language="Javascript">

   		window.top.menu.caricaMenuLav(<%=_funzione%>,<%=cdnLavoratore%>);

	</script>
	<script language="Javascript">
  	<%
       //Genera il Javascript che si occuperà di inserire i links nel footer
       attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore);
  	%>
	</script>
  
</head>

<body class="gestione" onload="rinfresca();">
<%
if (canView) {
	if (!cdnLavoratore.equals("")) {
		testata = new InfCorrentiLav(RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
		testata.setSkipLista(true);
		testata.show(out);
	}%>
	
	<p>
	<font color="green">
		<af:showMessages prefix="M_GetAdesionePA"/>
  	</font>
  	<font color="red"><af:showErrors /></font>
	</p>
	
	<p class="titolo">Riepilogo Patto/Azioni</p>
	<br>
	<center>
		<%out.print(htmlStreamTop);%>
		<af:form name="frm1" method="POST" action="AdapterHTTP">
	    	<input type="hidden" name="PAGE" value="<%=_page%>">
			<input type="hidden" name="cdnFunzione" value="<%=_funzione%>">
			<input type="hidden" name="CDNLAVORATORE" value="<%=Utils.notNull(cdnLavoratore)%>">
			<input type="hidden" name="OPERAZIONE" value="">
			
			<table class="main">
				<tr><td colspan="2"><div class="sezione">Pacchetto Adulti</div></td></tr>
				<tr>
				<td class="etichetta">Data Adesione</td>
			    <td class="campo">
			     	<af:textBox type="date" name="DATAADESIONE" classNameBase="input" value="<%=Utils.notNull(dataAdesionePA)%>" size="11" readonly="true" maxlength="10"/>
			   	<%if (canRecuperaAdesione) {%>
			   		<input class="pulsanti" type="submit" name="btnGetAdesione" onclick="settaOperazione('GET_ADESIONE');" value="Recupera data adesione"/>
			   	<%}%>
			   	</td>
				</tr>
				<%if (mesiAnzianitaAdesioneDid != null) {%>
					<tr>
					<td class="etichetta">Mesi DID/Adesione</td>
				    <td class="campo">
				     	<af:textBox type="text" name="mesiDidAdesione" classNameBase="input" value="<%=mesiAnzianitaAdesioneDid.toString()%>" size="5" readonly="true"/>
				   	</td>
					</tr>
				<%}%>
				<tr><td colspan="2"><div class="sezione">Riepilogo Patto/Azioni</div></td></tr>
				<tr><td colspan="2">
					<af:list moduleName="M_RiepilogoPattoAzioni"/>
				</td></tr>
			</table>
			
			<center>
			  <table>
			  <tr><td>
			  <input class="pulsanti" type="button" name="stampa" value="Stampa" onclick="stampaLista();" />
			  </td></tr>
			  </table>
			</center>
			
	    </af:form>
	    <%out.print(htmlStreamBottom);%>
	    <br>
	</center>
<%}%>
</body>
</html>
