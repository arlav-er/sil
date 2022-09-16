<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

        
<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  java.util.*, java.math.*,
                  com.engiweb.framework.security.*,it.eng.sil.module.consenso.GConstants" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String DATE_FORMAT_NOW = "dd/MM/yyyy";
	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT_NOW);

	String _page = (String) serviceRequest.getAttribute("PAGE"); 
	String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
	String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");

	boolean canVerificaConsenso = false;
	boolean isRevocato= false;
	boolean canInsert= false;
	boolean isUnavailable= false; 
	boolean isPresent=false;
	//ProfileDataFilter filter = new ProfileDataFilter(user, _page); //per ogni pagina da profilare
	/** Per qualsiasi pagina da profilare appartenente alla 'sezione' Gestione Consenso */ 
	ProfileDataFilter filter = new ProfileDataFilter(user, "HomeConsensoPage");
	
	//PageAttribs attributi = new PageAttribs(user, _page); //per ogni pagina da profilare
	/** Per ogni attributo della pagina da profilare appartenente alla 'sezione' Gestione Consenso */
	PageAttribs attributi = new PageAttribs(user, "HomeConsensoPage");
	
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	
	if (!filter.canView()) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
		// TODO: da mettere il pulsante 'VERIFICA'...
		//canVerificaConsenso = attributi.containsButton("VERIFICA");
		canVerificaConsenso = true;
	} 
	
	InfCorrentiLav infCorrentiLav= null;

	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
  	String htmlStreamTopCoop = StyleUtils.roundTopTable("prof_ro_coop");
  	String htmlStreamBottomCoop = StyleUtils.roundBottomTable("prof_ro_coop");

	String code = (String) serviceResponse.getAttribute("M_VerificaConsenso.AzioneConsenso");
	String statoConsenso="";
	String cognome=(String) serviceResponse.getAttribute("MSEL_AG_LAVORATORE.ROWS.ROW.STRCOGNOME");;
	String nome=(String) serviceResponse.getAttribute("MSEL_AG_LAVORATORE.ROWS.ROW.STRNOME");
	String cf=(String) serviceResponse.getAttribute("MSEL_AG_LAVORATORE.ROWS.ROW.STRCODICEFISCALE");
	
	java.util.Date dateInsDate = (java.util.Date) serviceResponse.getAttribute("M_VerificaConsenso.DataRegistrazione");
	java.util.Date dateRevocaDate = (java.util.Date) serviceResponse.getAttribute("M_VerificaConsenso.DataRevoca");
	
	String dataIns = "";
	if(dateInsDate != null){
		dataIns = sdf.format(dateInsDate);
	}
	
	String dataRevoca = "";
	if(dateRevocaDate != null){
		dataRevoca = sdf.format(dateRevocaDate);
	}
	
	String dataRaccoltaOggi = sdf.format(new Date());
	
	if (code.equalsIgnoreCase(GConstants.CONSENSO_ASSENTE_CODICE)){
		canInsert=true;
		statoConsenso="NON PRESENTE";
	} else if (code.equalsIgnoreCase(GConstants.CONSENSO_ATTIVO_CODICE)){
		isPresent=true;
		statoConsenso="ATTIVO";
	} else if (code.equalsIgnoreCase(GConstants.CONSENSO_NON_DISPONIBILE_CODICE)){
		isUnavailable=true;
		statoConsenso="NON DISPONIBILE";
	} else if (code.equalsIgnoreCase(GConstants.CONSENSO_REVOCATO_CODICE)){
		isRevocato=true;
		statoConsenso="REVOCATO";
	}
	
	//SourceBean lavoratore = (SourceBean) serviceResponse.getAttribute("M_GetInfoLavAdesioneGG.ROWS.ROW");
	//String codfisclav = lavoratore.getAttribute("strcodicefiscale").toString();
	
	//String queryString = "PAGE="+_page+"&cdnLavoratore="+cdnLavoratore+"&trasferisci=true&stampato=true";
	String queryString = "PAGE="+_page+"&cdnLavoratore="+cdnLavoratore+"&CDNFUNZIONE="+_funzione+"&BTNVERIFICA=OK";
	//String queryString = "";
%>
 
<html>
<head>
<title>Gestione Consenso</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css">
  	<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  	<link rel="stylesheet" type="text/css" href="../../css/stiliTemplate.css"/>
<af:linkScript path="../../js/"/>

<script language="JavaScript">

<%//Genera il Javascript che si occuperà di inserire i links nel footer
if (!cdnLavoratore.equals(""))
	attributi.showHyperLinks(out, requestContainer, responseContainer, "cdnLavoratore=" + cdnLavoratore);
%>

</script>
<%@ include file="../documenti/_apriGestioneDoc.inc"%>

</head>
<body class="gestione" onload="rinfresca();">
	
	<%
		infCorrentiLav= new InfCorrentiLav(sessionContainer, cdnLavoratore, user);
		infCorrentiLav.setSkipLista(true);
		infCorrentiLav.show(out);
	%>
	<p align="center">
	  <font color="green"><af:showMessages prefix="M_VerificaConsenso" /></font>
	  <font color="red"><af:showErrors /></font>
	</p>
	
	<p class="titolo">Consenso</p>
	<af:form name="form1" action="AdapterHTTP" method="POST">
	
	<% out.print(htmlStreamTopCoop); %>

  
	<br>
	<table class="maincoop">
		<tr><td colspan="6"><div class="sezione2"/>Stato attuale del consenso sul Sistema di Gestione dei Conensi</td></tr>
		<tr>
			 <th class="etichettacoop">Cognome</th>
			 <th class="etichettacoop">Nome</th>
			 <th class="etichettacoop">Codice Fiscale</th>
			 <th class="etichettacoop">Stato Consenso</th>
			 <th class="etichettacoop">Data registrazione</th>
			 <th class="etichettacoop">Data revoca</th>
		</tr>
		<tr>
			<td class="infocoop"><%=cognome%></td>
			<td class="infocoop"><%=nome%></td>
			<td class="infocoop"><%=cf%></td>
			<td class="infocoop"><%=statoConsenso%></td>
			<td class="infocoop"><%=dataIns%></td>
			<td class="infocoop"><%=dataRevoca%></td>
		</tr>
		</table>
		
		<br>
		
			
			<center>
		  <table class="maincoop" >
 	  	 
 		       		<tr>
 		       			<!--  PULSANTE REVOCA -->
						<!--
 	 				  <td>
		           		  <input class="pulsanticoop <=((isPresent)?"":"Disabled")%>" type="submit" name="BTNREVOCA" value="Revoca Consenso" <=((isPresent)?"":"disabled")%>>
		              &nbsp;
		              -->
		             		<input type="submit" class="pulsanticoop" name="BTNVERIFICA" value="Verifica Consenso">
		             		
		           		</td>
		         		 
		         	</tr>
		         	</table>
 	         	</center>
	   
	<% out.print(htmlStreamBottomCoop); %>
 <br>
  	
	  <%out.print(htmlStreamTop);%> 
	  <table class="main" >
	  	<tr><td colspan="2"><div class="sezione2"/>Raccolta consenso (inserisce il consenso inviando notifica al Sistema di Gestione dei Consensi)</td></tr>
	  	<tr>
			 <th class="etichettacoop">Codice Fiscale</th>
			 <th class="etichettacoop"><%=cf%></th>
		</tr>
		
		<!--
		<tr>
			<th class="etichettacoop">Data raccolta consenso</td>
			<th class="etichettacoop"><af:textBox disabled="false" name="dataRaccoltaOggi" type="date" validateOnPost="true" required="true" value="" maxlength="10" size="10" readonly="readonly" /></td>
		</tr>
		-->
	  <tr>
		 <td class="etichetta" nowrap></td>      
	   </tr>
	    
	    <!--
	  	<tr>
		 <td nowrap>Scarica modulo per raccogliere il consenso 
		  
				<a href="#"><img src="../../img/download.gif" border="0" alt="Scarica Modulo" /></a> 
		 </td>          
	   </tr>
	   -->
	   
	   </table>
	    <br>
	   <center>
	  		<table class="main" >
 	  	 
	       		<tr>
 				  <td>
	             		<input class="pulsante<%=((canInsert)?"":"Disabled")%>" type="button" name="BTNINSERISCI" value="Inserisci Consenso" <%=((canInsert)?"":"disabled")%>
										onclick="apriGestioneDoc('RPT_CONSENSO',
  										'&cdnLavoratore=<%=cdnLavoratore%>&pagina=<%=_page%>',
										'DICH','REPORTFRAMEPAGE', true);">
           		  </td>
           		 
         	</tr>
         	</table>	  
 
 	         	</center>
	  
		<% out.print(htmlStreamBottom);%>
		
	  <input type="hidden" name="PAGE" value="VerificaConsensoPage">
	  <input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>">
	  <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
	  </af:form>
	  
</body>
</html>
