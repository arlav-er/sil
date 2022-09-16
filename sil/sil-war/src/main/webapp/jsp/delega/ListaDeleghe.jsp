<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,com.engiweb.framework.dispatching.module.AbstractModule,com.engiweb.framework.util.QueryExecutor,it.eng.sil.security.User,it.eng.sil.security.PageAttribs,java.util.*,it.eng.sil.security.ProfileDataFilter,it.eng.afExt.utils.*,java.math.*,java.io.*,com.engiweb.framework.security.*,it.eng.sil.util.*,it.eng.sil.util.InfCorrentiLav" %> 
       
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>



<%
	
	String _page = (String) serviceRequest.getAttribute("PAGE");	
	PageAttribs attributi = new PageAttribs(user, _page);
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	
	// cdnlavoratore
	String cdnLavoratore = (String)requestContainer.getServiceRequest().getAttribute("cdnlavoratore");
	// cdngruppo
    String _cdngruppo = String.valueOf(user.getCdnGruppo());
	// cdnutente
	String _cdnutente = String.valueOf(user.getCodut());
	
	InfCorrentiLav infCorrentiLav = new InfCorrentiLav(sessionContainer, cdnLavoratore, user);

	 if (! filter.canView()){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
		boolean canDelete = attributi.containsButton("rimuovi");
		boolean canAdd = attributi.containsButton("NUOVA_DELEGA");
		// crea la url per tornare alla lista
		String token = "_TOKEN_" + "DelegaAnagRicercaPage";		
		String chiudiUrl = (String) sessionContainer.getAttribute(token.toUpperCase());
		// prendo il cdn funzione da passare quand navigo in un'altra page 
		String cdnFunzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
		// verifica se esiste già, o meno, 
		BigDecimal checkDelega = (BigDecimal) serviceResponse.getAttribute("MODULE_CHECK_DELEGA_ATTIVA.ROWS.ROW.CHECK_DELEGA");
		boolean esisteDelegaAttivaAltroTipoGruppo = checkDelega.intValue() > 0;
		BigDecimal checkDelegaNoAttive = (BigDecimal) serviceResponse.getAttribute("MODULE_CHECK_NOT_DELEGA_ATTIVA.ROWS.ROW.CHECK_DELEGA");
		boolean esisteDelegaAttive = checkDelegaNoAttive.intValue() > 0;
	%>
		
	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>Lista delle deleghe</title>
		
		 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
		 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
		 <af:linkScript path="../../js/" />
		 
		 <script type="text/javascript">
			
		 function nuovaDelega(selectedId, pageName){	
			 
			 
			var url = 'AdapterHTTP?PAGE=InserisciDelegaPage';
			url = url + '&cdnfunzione=<%=cdnFunzione%>';
			url = url + '&cdnlavoratore=<%=cdnLavoratore%>';
			url = url + '&CDNGRUPPO=<%=_cdngruppo%>';
			
			
			setWindowLocation(url);
		 }
		 
		 function cancellaElemento(IDDELEGA, ISDATAFINENULL) {
			// Se la pagina è già in submit, ignoro questo nuovo invio!
			if (isInSubmit()) return;
			
			if ( '0' == ISDATAFINENULL ){
				alert('Non è possibile rimuovere questa delega.');
				return;
			}
			
			if ( confirm('Sei sicuro di voler cancellare la delega?')){
				var url = 'AdapterHTTP?PAGE=ListaDeleghePage';
				url = url + '&cdnfunzione=<%=cdnFunzione%>';
				url = url + '&cdnlavoratore=<%=cdnLavoratore%>';
				url = url + '&CDNGRUPPO=<%=_cdngruppo%>'
				url = url + '&CDNUTMOD=<%=_cdnutente%>'
				url = url + '&CANCELLADELEGA='+IDDELEGA;				
				
				setWindowLocation(url);
			}
		 } 
		 
		 
		 function tornaAllaListaLavoratori() {			 
			// Se la pagina è già in submit, ignoro questo nuovo invio!
			if (isInSubmit()) return;
			var url = 'AdapterHTTP?'+'<%=chiudiUrl%>';
			setWindowLocation(url);
		}
			 
		</script>
	
</head>

<body class="gestione" onload="rinfresca()">
		
		<%
			infCorrentiLav.show(out);
		%>
		<af:showErrors/>
		
		<af:list moduleName="MODULE_LISTA_DELEGHE_PER_LAVORATORE"			
			jsSelect="selectLavoratore"
			canDelete="<%= String.valueOf(canDelete ? 1 : 0) %>" 
			jsDelete="cancellaElemento"  /> 
						 
			<center>
				<%if (canAdd) {
					if (esisteDelegaAttivaAltroTipoGruppo) {%>
						<input class="pulsante" type="button" name="inserisci" value="Nuova delega" onclick="nuovaDelega()"/>
					<%}
					else {
						if (!esisteDelegaAttive) {%>
							<input class="pulsante" type="button" name="inserisci" value="Nuova delega" onclick="nuovaDelega()"/>
						<%}
					}
				}%>
				<input class="pulsante" type="button" name="chiudi" value="Chiudi" onclick="tornaAllaListaLavoratori()"/>	
			</center>					
			
			
</body>
</html>


<%}%>

