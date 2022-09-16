<%@ page contentType="text/html;charset=utf-8"%>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<% 

	String _page = (String) serviceRequest.getAttribute("PAGE");
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);

	// TODO: abilitare il filtro in sede di PROFILATURA !!!!!
	if (!filter.canView()) {
	   response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	} else {

	//if (true) {
		
		// NOTE: Attributi della pagina (pulsanti e link)
		PageAttribs attributi = new PageAttribs(user, "ListaPratichePatronatoPage");
		boolean canInsert = false;
		boolean canDelete = false;
		
	 	String _funzione = (String)serviceRequest.getAttribute("CDNFUNZIONE");
		
	 	///////////////////
	 	// DATI DI INPUT //
	 	///////////////////
	 	
		String datInizio           		= StringUtils.getAttributeStrNotNull(serviceRequest,"datInizio");
		String datFine             		= StringUtils.getAttributeStrNotNull(serviceRequest,"datFine");
		String flgDid              		= StringUtils.getAttributeStrNotNull(serviceRequest,"flgDid");
		String flgDomandeMobilita  		= StringUtils.getAttributeStrNotNull(serviceRequest,"flgDomandeMobilita");
		String patronato 				= StringUtils.getAttributeStrNotNull(serviceRequest,"patronato");
	 	String cdnTipoPatronato 		= StringUtils.getAttributeStrNotNull(serviceRequest,"cdnTipoPatronato");
	 	String cdnUfficioPatronato 		= StringUtils.getAttributeStrNotNull(serviceRequest,"cdnUfficioPatronato");
	 	String cdnOperatorePatronato	= StringUtils.getAttributeStrNotNull(serviceRequest,"cdnOperatorePatronato");
	 	String strTipoPatronato 		= StringUtils.getAttributeStrNotNull(serviceRequest,"strTipoPatronato");
	 	String strUfficioPatronato 		= StringUtils.getAttributeStrNotNull(serviceRequest,"strUfficioPatronato");
	 	String strOperatorePatronato	= StringUtils.getAttributeStrNotNull(serviceRequest,"strOperatorePatronato");
	 	String tipoRicerca      		= StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRicerca");

		// esplicita il 'N'
		if (flgDid.equals("")) {
			flgDid = "N";
		}

		if (flgDomandeMobilita.equals("")) {
			flgDomandeMobilita = "N";
		}

		//////////////////////////
		// PARAMETRI PER FILTRO //
		//////////////////////////
		
		StringBuilder filterStringBuilder = new StringBuilder("Filtri di ricerca:<br/>");
		
		if (!datInizio.equals("")) {
			filterStringBuilder.append(" Data inizio: <b>");
			filterStringBuilder.append(datInizio);
			filterStringBuilder.append("</b>");
		}
		
		if (!datFine.equals("")) {
			filterStringBuilder.append(" Data fine: <b>");
			filterStringBuilder.append(datFine);
			filterStringBuilder.append("</b>");
		}
		
		if (flgDid.equals("S")) {
			filterStringBuilder.append(" Tipo pratica DID: <b>Sì</b>");
		}
		
		if (flgDomandeMobilita.equals("S")) {
			filterStringBuilder.append(" Tipo pratica Domande Mobilita: <b>Sì</b>");
		}
		
		if (!strOperatorePatronato.equals("")) {
			filterStringBuilder.append(" Ufficio patronato: <b>");
			filterStringBuilder.append(strOperatorePatronato);
			filterStringBuilder.append("</b>");
		}
		
		if (!strUfficioPatronato.equals("")) {
			filterStringBuilder.append(" Ufficio patronato: <b>");
			filterStringBuilder.append(strUfficioPatronato);
			filterStringBuilder.append("</b>");
		}
		
		if (!strTipoPatronato.equals("")) {
			filterStringBuilder.append(" Tipo patronato: <b>");
			filterStringBuilder.append(strTipoPatronato);
			filterStringBuilder.append("</b>");
		}
		
		String filterString = filterStringBuilder.toString();
		
%>

<html>
<head>

 <title>Risultati della ricerca</title>
  
	<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
 	<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
 	
 <af:linkScript path="../../js/"/>

 <script type="text/Javascript">
  
  function tornaAllaRicerca() { // Se la pagina è già in submit, ignoro questo nuovo invio!
      
	 if (isInSubmit()) return;
	  
     url="AdapterHTTP?PAGE=RicercaPratichePatronatoPage";
     url += "&CDNFUNZIONE="+"<%=_funzione%>";
     url += "&datInizio="+"<%=datInizio%>";
     url += "&datFine="+"<%=datFine%>";
     url += "&flgDid="+"<%=flgDid%>";
     url += "&flgDomandeMobilita="+"<%=flgDomandeMobilita%>";
     url += "&patronato="+"<%=patronato%>";
     url += "&cdnTipoPatronato="+"<%=cdnTipoPatronato%>";
     url += "&cdnUfficioPatronato="+"<%=cdnUfficioPatronato%>";
     url += "&cdnOperatorePatronato="+"<%=cdnOperatorePatronato%>";
     url += "&tipoRicerca="+"<%=tipoRicerca%>";
     
     setWindowLocation(url);
     
  }
  
 </script>
 
</head>

<body  class="gestione" onload="rinfresca();rinfresca_laterale();">
	
	<center>
		<font color="red">
		    <af:showErrors/>
		</font>
	</center>
	
	<%if(filterString.length() > 0) { %>
	<p align="center">
	    <table cellpadding="2" cellspacing="10" border="0" width="100%">
	    	<tr>
	    		<td style="border-width:1px;border-style:solid;text-color:#000080;border-color:#000080;background-color:#dcdcdc;">
	    			<%out.print(filterString);%>
	    		</td>
	    	</tr>
	    </table>
    </p>
	<%}%>
	
	<af:form dontValidate="true">
	
		<af:list 	moduleName="M_LISTA_PRATICHE_PATRONATI" 
					configProviderClass="it.eng.sil.module.delega.ListaPratichePatronatoDynConfig" 
					canDelete="<%= canDelete ? \"1\" : \"0\" %>" 
					canInsert="<%= canInsert ? \"1\" : \"0\" %>" 
					getBack="true"/>
					
		<center>
			<input class="pulsante" type = "button" name="torna" value="Torna alla pagina di ricerca" onclick="tornaAllaRicerca()"/>
		</center>
		
	</af:form>
	
	<br/>
	
</body>

</html>

<%}%>
