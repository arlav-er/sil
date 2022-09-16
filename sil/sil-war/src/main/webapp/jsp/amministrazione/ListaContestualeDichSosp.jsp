<%@ page contentType="text/html;charset=utf-8"%>
 
<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*,
                it.eng.sil.security.PageAttribs,
                it.eng.sil.security.ProfileDataFilter,
                java.math.*" %>
                
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<% 

  //flag che indica se il lavoratore ha uno storico
  boolean hasStorico = serviceResponse.containsAttribute("M_AMMHASDICHSOSPSTORICO.ROWS.ROW");	  
	  
    String _page = (String) serviceRequest.getAttribute("PAGE");

	String cdnLavoratore=(String) serviceRequest.getAttribute("cdnLavoratore");
	
	//prelevo i parametri di ricerca
	String strCodiceFiscale= StringUtils.getAttributeStrNotNull(serviceRequest, "strCodiceFiscale");
    String strCognome= StringUtils.getAttributeStrNotNull(serviceRequest, "strCognome");
    String strNome= StringUtils.getAttributeStrNotNull(serviceRequest, "strNome");
    String tipoRicerca=StringUtils.getAttributeStrNotNull(serviceRequest, "tipoRicerca");
	String codCPI=StringUtils.getAttributeStrNotNull(serviceRequest, "codCPI");


   
 String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
 
 
 //se sono in contestuale, prelevo anche la testata del lavoratore
  InfCorrentiLav infCorrentiLav= null;
  infCorrentiLav= new InfCorrentiLav(sessionContainer, cdnLavoratore, user);

	//FILTRI VISUALIZZAZIONE e pulsanti  	
	PageAttribs attributi = new PageAttribs(user, _page);
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	boolean canView=filter.canViewLavoratore();
	boolean readOnlyStr = true;
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
	      boolean canInsert   =  attributi.containsButton("INSERISCI");
          if (canInsert){
       	   readOnlyStr=!canInsert;
       	 }        
   	}
	





    
%>

<html>
<head>
  <title>Lista Dichiarazioni di Sospensione per contrazione d'attività</title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
 <af:linkScript path="../../js/"/>

 <script type="text/Javascript">
  function tornaAllaRicerca()
  {  // Se la pagina è già in submit, ignoro questo nuovo invio!
     if (isInSubmit()) return;
  
     url="AdapterHTTP?PAGE=AmmRicercaDichSospPage";
     url += "&CDNFUNZIONE="+"<%=_funzione%>";
     url += "&strCodiceFiscale="+"<%=strCodiceFiscale%>";
     url += "&strCognome="+"<%=strCognome%>";
     url += "&strNome="+"<%=strNome%>";
	 url += "&tipoRicerca="+"<%=tipoRicerca%>";         
	 url += "&codCPI="+"<%=codCPI%>";
     setWindowLocation(url);
  }
  
   function apriInfoStoriche() {
     var f = "AdapterHTTP?PAGE=AmmListaStoricoDichSospPage&cdnLavoratore=<%=cdnLavoratore%>"
     var t = "_blank";
     var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=750,height=650,top=30,left=180";
     window.open(f, t, feat);

	}
   
   
 </script>
 
</head>
<!--<body  onload="checkError();" class="gestione" > -->
<body  class="gestione" onload="rinfresca()">
<%    infCorrentiLav.show(out);  %>

<center>
<font color="green">
  <af:showMessages prefix="M_AmmDelDichSosp"/>
</font>
<font color="red">
     <af:showErrors/>
</font></center>

<af:list moduleName="M_Amm_cercaDichSospContestuale" canDelete="<%= !readOnlyStr ? \"1\" : \"0\" %>" canInsert="<%= !readOnlyStr ? \"1\" : \"0\" %>" jsDelete="cancellaDichSosp" />
<af:form dontValidate="true">
<table border="0" width="100%">
	<tr>
		<td align="center" colspan="3">
			<%if (!readOnlyStr) { %>
		        <input class="pulsanti" type="submit" name="inserisci_nuova" value="Inserisci dichiarazione"/>
		        <input type="hidden" name="PAGE" value="AmmDettDichSospPage"/>
		        <input type="hidden" name="flag_insert" value="1"/>
		        <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
		        <input type="hidden" name="contestuale" value="true"/>
		        <input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>"/>
		        <input type="hidden" name="PAGE_LISTA" value="<%=_page%>"/>  		        
			<%}%>
		</td>
	</tr>
	<tr>
		<td colspan="2">&nbsp;</td>
		<td align="center" width="33%">	
			<input class="pulsante<%=((hasStorico)?"":"Disabled")%>" type="button" name="InfoStoriche" 
				   value="Informazioni storiche" onclick="apriInfoStoriche();"
				   <%=(!hasStorico)?"disabled=\"True\"":""%>/>
		</td>
	</tr>
</table>
</af:form>
<br/>
</body>

<script>

   function cancellaDichSosp(page, prgDichSospensioneCanc, cdnFunzione){
   if (confirm("Sei proprio sicuro di cancellare?")) {
	   	var urlDiLista="<%if (sessionContainer!=null){
	 			String token= "_TOKEN_";
			 	token += "AmmListaContestualeDichSospPage";

				 				 
	    		 String urlDiLista = (String) sessionContainer.getAttribute(token.toUpperCase());
	    		 if (urlDiLista!=null){	     
			     	//cancello il parametro di cancellazione dalla stringa di ritorno
			     	//in modo che non possa più essere ripetuta.
			     	int occorrenzaCancella=urlDiLista.indexOf("PRGDICHSOSPENSIONECANC");
					if (occorrenzaCancella!=-1) {
						int occorrenzaFineCancella=urlDiLista.indexOf('&', occorrenzaCancella);
						String urlDilistacomodo=urlDiLista.substring(0, occorrenzaCancella);
						String urlDilistacomodo2=urlDiLista.substring(occorrenzaFineCancella);
						urlDiLista=urlDilistacomodo + urlDilistacomodo2;
					}
	    		 out.print(urlDiLista);
	    		}
			  } 		    
		    %>";		    
			var f = "AdapterHTTP?" + urlDiLista + "&prgDichSospensioneCanc="+prgDichSospensioneCanc;
	       setWindowLocation(f);
    	}  
   }


</script>

</html>
