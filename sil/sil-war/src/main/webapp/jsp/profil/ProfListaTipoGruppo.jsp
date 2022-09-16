<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
com.engiweb.framework.dispatching.module.AbstractModule,com.engiweb.framework.util.QueryExecutor,it.eng.sil.security.User,it.eng.sil.security.PageAttribs,java.util.*,it.eng.sil.security.ProfileDataFilter,it.eng.afExt.utils.*,java.math.*,java.io.*,com.engiweb.framework.security.*,it.eng.sil.util.*,it.eng.sil.util.InfCorrentiLav" %> 
       
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>



<%
	
	String _page = (String) serviceRequest.getAttribute("PAGE");	
	PageAttribs attributi = new PageAttribs(user, _page);
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	

	 //if (! filter.canView()){
	if (false){	 
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
		boolean canDelete = attributi.containsButton("RIMUOVI");
		boolean canAdd = attributi.containsButton("NUOVO");		
		
		// crea la url per tornare alla lista
		String token = "_TOKEN_" + _page;		
		String chiudiUrl = (String) sessionContainer.getAttribute(token.toUpperCase());
		// prendo il cdn funzione da passare quand navigo in un'altra page 
		String cdnFunzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
	%>
		
	
<html>
<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>Lista tipi gruppo</title>
		
		 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
		 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
		 <af:linkScript path="../../js/" />
		 
		 <script type="text/javascript">
		 
		 
		function apriDettaglio(selectedId){
				var url = 'AdapterHTTP?PAGE=ProfDettaglioTipoGruppoPage';
				url = url + '&cdnfunzione=<%=cdnFunzione%>';
				url = url + '&CDNTIPOGRUPPO='+selectedId;
				url = url + '&NOME_MODULO=PROF_GET_SELECTED_TIPO_GRUPPO_MOD';
				setWindowLocation(url);
		}
		 
			
		 function nuovoTipoGruppo(){
			 var url = 'AdapterHTTP?PAGE=ProfDettaglioTipoGruppoPage';
			url = url + '&cdnfunzione=<%=cdnFunzione%>';
			url = url + '&nuovo_tipo_gruppo=true';
			setWindowLocation(url);
		 }
		 
		 function cancellaElemento(IDTIPOGRUPPO, ISSTANDARD, ISUSED, STRDENOMINAZIONE) {
			 
			 // Se la pagina è già in submit, ignoro questo nuovo invio!
			if (isInSubmit()) return;
			
			if ( '1' == ISSTANDARD ){
				alert('Non è possibile rimuovere un tipo gruppo standard.');
				return;
			}
			
			if ( '1' == ISUSED ){
				alert('Non è possibile rimuovere un tipo gruppo in uso nel sistema.');
				return;
			}
			
			if ( confirm('Sei sicuro di voler cancellare il tipo gruppo ' + STRDENOMINAZIONE + '?')){
				var url = 'AdapterHTTP?PAGE=ProfListaTipoGruppoPage';
				url = url + '&cdnfunzione=<%=cdnFunzione%>';
				url = url + '&CANCELLACDNTIPOGRUPPO='+IDTIPOGRUPPO;				
				
				setWindowLocation(url);
			}
		 } 
		 
			 
		</script>
	
</head>

<body class="gestione" onload="rinfresca()">
		
	
		<af:showErrors/>
		
		<af:list moduleName="ProfListaTipoGruppoMod"			
			jsSelect="apriDettaglio"
			canDelete="<%= String.valueOf(canDelete ? 1 : 0) %>" 
			jsDelete="cancellaElemento"  /> 
						 
			<center>
				<%if ( canAdd ){%><input class="pulsante" type="button" value="Nuovo tipo gruppo" onclick="nuovoTipoGruppo()"/><%}%>					
			</center>
			
</body>
</html>


<%}%>