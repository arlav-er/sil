<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  java.util.*,
                  it.eng.sil.security.ProfileDataFilter,                  
                  it.eng.afExt.utils.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  it.eng.sil.util.*,
                  java.text.*,
                  it.eng.sil.util.InfCorrentiLav" %>
 
       
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<% 
	String _page = (String) serviceRequest.getAttribute("PAGE"); // "InserisciDelegaPage"
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	PageAttribs attributi = new PageAttribs(user, _page);

	boolean canView = filter.canView();
	if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{

	boolean canInsert = attributi.containsButton("INSERISCI_DELEGA");
	// Recupero l'eventuale URL generato dalla LISTA precedente (se ce ne è una)
	String token = "_TOKEN_" + "ListaDeleghePage";
	String goBackListUrl = (String) sessionContainer.getAttribute(token.toUpperCase());
	goBackListUrl = goBackListUrl.replaceAll("CANCELLADELEGA","NONCANCELLADELEGA"); // TODO fix: evita che la lista esegua una cancellazione indesiderata 
	//
	DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    Date currentDate = new Date();
    String strDataCorrente = formatter.format(currentDate);
    // utente
    String _uId = String.valueOf( sessionContainer.getAttribute("_CDUT_") );
    // cdnlavoratore
    String cdnLavoratore = (String) serviceRequest.getAttribute("cdnlavoratore");
    // cdngruppo
    String _cdngruppo = String.valueOf(user.getCdnGruppo());
    // cdndelega
    String _cdnDelega = "";
    SourceBean delegaFromGruppo = (SourceBean) serviceResponse.getAttribute("MODULE_GET_DELEGA_FROM_GRUPPO.ROWS.ROW");
    if ( delegaFromGruppo != null ) {
    	_cdnDelega = String.valueOf( delegaFromGruppo.getAttribute("CDNDELEGA") );
    }
    // classe necessaria per l'intestazione
    InfCorrentiLav infCorrentiLav = new InfCorrentiLav(sessionContainer, cdnLavoratore, user);
    
    boolean inserimentoAvvenuto = serviceRequest.containsAttribute("INSERISCI_NUOVA_DELEGA");
    
 	// prendo il cdn funzione da passare quand navigo in un'altra page 
	String cdnFunzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
		
%>

	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<title>Template di modulistica personalizzata</title>	
	<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
	<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
	<af:linkScript path="../../js/" />
 	<script type="text/javascript">
 	
	 	function invia(bottonePremuto){
	 		 
			 if ( confirm("Sei sicuro di voler inserire la delega per il lavoratore?") ){				 
				var dettForm=document.forms["form1"];				
		 		if(dettForm.onsubmit()){		 				
		 			dettForm.submit();
		 		}		 		
		 		return true;	 		
			 }else{				 
				 return false;
			 }

	 	 }
		
		
		 function goBack() {
				if (isInSubmit()) return;
				
	      		backLocation = "AdapterHTTP?<%= StringUtils.formatValue4Javascript(goBackListUrl) %>";
	      		
	      		setWindowLocation(backLocation);
	    }
		 
		 function inviaForm(){
			 
			 if (isInSubmit()) return;
				
			 	var url = 'AdapterHTTP?PAGE=<%=_page%>';
				url = url + '&cdnfunzione=<%=cdnFunzione%>';
				url = url + '&CDNGRUPPO=<%=_cdngruppo%>';
				url = url + '&CDNLAVORATORE=<%=cdnLavoratore%>';
				url = url + '&CDNUTINS=<%=_uId%>';
				url = url + '&CDNUTMOD=<%=_uId%>';
				url = url + '&INSERISCI_NUOVA_DELEGA=Inserisci';
	      		setWindowLocation(location);
			 
		 }
		
		 
	</script>
</head>
<%
	String htmlStreamTop = StyleUtils.roundTopTable(true);
	String htmlStreamBottom = StyleUtils.roundBottomTable(true); // pagina è solo di insert, mai di pura visualizzazione
%>


<body class="gestione" onload="rinfresca()">
	
	<%
			infCorrentiLav.show(out);
		%>
	
<p class="titolo"> Inserimento Delega</p>

<af:showErrors/>
<af:showMessages prefix="MODULE_INSERT_NEW_DELEGA"/>
 

<%out.print(htmlStreamTop);%>
	</br>
	</br>
		
	
	<af:form name="form1" method="POST" action="AdapterHTTP">
		
	
	<af:textBox type="hidden" name="PAGE" value="<%=_page%>" />	
	<af:textBox type="hidden" name="CDNGRUPPO" value="<%=_cdngruppo%>" />
	<af:textBox type="hidden" name="CDNDELEGA" value="<%=_cdnDelega%>" />
	<af:textBox type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>" />	
	<af:textBox type="hidden" name="CDNUTINS" value="<%=_uId%>" />
	<af:textBox type="hidden" name="CDNUTMOD" value="<%=_uId%>" />
	
	
	<table class="main" border="0">
 
   <tr><td colspan="2"/>&nbsp;</td></tr>
   
   <tr>
     <td class="etichetta">Inizio Validità</td>
     <td class="campo2">
     	<%-- --%>
     	<af:textBox type="date"  size="11" classNameBase="input" readonly="false"  name="dataInizioValid" title="Inizio Validità" validateOnPost="true" value="<%=strDataCorrente%>"  validateOnPost="true" required="true" readonly="true"/> 
     	<%-- --%>
   	 </td>
   </tr>   
   
				
				<input	type="hidden" name="INSERISCI_NUOVA_DELEGA" value="INSERISCI_NUOVA_DELEGA" />
       
   <tr>
      <td colspan="2" align="center">
      <br/>
      <br/>
      </td>
   </tr> 
    
	</table>
	
	<center>
	        
	        <%if (!inserimentoAvvenuto){ %> 
		<input class="pulsanti" type="button" name="_invia" value="Inserisci" onClick="invia(this);"/>
		      <%}%>
	    <input class="pulsanti" type="button"  name="TORNA" value="Torna alla lista" onClick="goBack()"  />
   </center>
	
	
	</af:form>
	
	
<%out.print(htmlStreamBottom);%>
</body>
</html>

<%}%>