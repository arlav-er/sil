<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="com.engiweb.framework.base.*, 
                com.engiweb.framework.configuration.ConfigSingleton,
                 java.lang.*,
                it.eng.sil.security.ProfileDataFilter, 
                java.text.*, java.util.*,it.eng.sil.util.*, java.math.*,
                it.eng.sil.security.* "%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
	String cdnLavoratore= (String )serviceRequest.getAttribute("CDNLAVORATORE");
	String _page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	
	boolean canModify = false;
	
	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
		PageAttribs attributi = new PageAttribs(user, _page);
		boolean existInserisci = attributi.containsButton("INSERISCI");
	
	    InfCorrentiLav infCorrentiLav= new InfCorrentiLav(requestContainer.getSessionContainer(), cdnLavoratore, user);
	
	  
	    String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	    String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);	
	    int _funzione =0;
%>

<html>
<head>
<title>Anagrafica dettaglio</title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetailCoop.css" />
 <af:linkScript path="../../js/"/>


</head>
<body>
<%
 _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
 Linguette l  = new Linguette(user,  _funzione, _page, new BigDecimal(cdnLavoratore));
 infCorrentiLav.show(out); 
 l.show(out);
 boolean isErrore = false;
 String response_xml="M_GetLavoratoreAccount";
 SourceBean row_account=(SourceBean) serviceResponse.getAttribute(response_xml);
 isErrore = row_account.containsAttribute("ERRORE"); 
 String attenzioneMsg ="ATTENZIONE: la ricerca del cittadino avviene unicamente per corrispondenza sul CODICE FISCALE oppure dell'indirizzo E-MAIL.";

 %>


<SCRIPT>
   window.top.menu.caricaMenuLav( <%=_funzione%>,   <%=cdnLavoratore%>);

   function go(url, alertFlag) {
	   // Se la pagina è già in submit, ignoro questo nuovo invio!
	   if (isInSubmit()) return;
	   
	   var _url = "AdapterHTTP?" + url;
	   if (alertFlag == 'TRUE' ) {
	     if (confirm('Confermi operazione?')) {
	       setWindowLocation(_url);
	     }
	   }
	   else {
	     setWindowLocation(_url);
	   }
	 }
</SCRIPT>


 <font color="red">
     <af:showErrors/>
     <af:showMessages prefix="M_GetLavoratoreAccount"/>
 </font>
<af:form method="POST" action="AdapterHTTP" name="Frm1" >
<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>"/>
<% 
if (!isErrore) { %>
		<center>
		<table>
			<tr>
				<td align="center"><af:textBox classNameBase="input"
						type="text" name="TESTOATT" value="<%=attenzioneMsg%>"
						readonly="true" title="Anno protocollo" size="130" /></td>
			</tr>
		</table>
		</center>
		<af:list moduleName="M_GetLavoratoreAccount" skipNavigationButton="1"   />

<center>
<% 
if (existInserisci) { %>
  <table>
  <tr><td align="center">
  <input type="button" class="pulsanti"  name = "inserisciNuovo" value="Inserisci Nuovo Account" onclick= "go('PAGE=AnagDettaglioPageAccountDettaglio&cdnFunzione=<%=_funzione%>&CDNLAVORATORE=<%=cdnLavoratore%>&inserisciNuovo=1', 'FALSE')">
  </td></tr>
  </table>
<% } %>
</center>
<% }%>
<br/>
<p align="center">
</p>
<br/>
<input type="hidden" name="PAGE" value="AnagDettaglioPageAccount">
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"%>
</af:form>

</body>
</html>

<%}  %>
