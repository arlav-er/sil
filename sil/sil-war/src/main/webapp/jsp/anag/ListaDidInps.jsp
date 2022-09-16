<%@ page contentType="text/html;charset=utf-8"%>

<%@ page
	import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%@ taglib uri="aftags" prefix="af"%>

<%
	String _page = (String) serviceRequest.getAttribute("PAGE");
	ProfileDataFilter filter = new ProfileDataFilter(user, "listaDidInpsPage");

	if (!filter.canView()) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	} else {
		// NOTE: Attributi della pagina (pulsanti e link) 
		PageAttribs attributi = new PageAttribs(user,
				"DidInpsRicercaPage");

		//cooperazione applicativa
		boolean listaCoop = serviceResponse
				.containsAttribute("M_COOP_GetLavoratoreIR");
		boolean troppeRighe = serviceResponse
				.containsAttribute("M_COOP_GetLavoratoreIR.TROPPI_RISULTATI");
		// seleziono la lista dall'indice regionale solo se e' abilitata la cooperazione

		String _funzione = (String) serviceRequest
				.getAttribute("CDNFUNZIONE");

		String strCodiceFiscale = StringUtils.getAttributeStrNotNull(
				serviceRequest, "strCodiceFiscale");
		String datstipulada = StringUtils.getAttributeStrNotNull(
				serviceRequest, "datstipulada");
		String datstipulaa = StringUtils.getAttributeStrNotNull(
                serviceRequest, "datstipulaa");
		String codCpi = StringUtils.getAttributeStrNotNull(
				serviceRequest, "codCpi");
		String tipoRicerca = StringUtils.getAttributeStrNotNull(
				serviceRequest, "tipoRicerca");
		String codDID = StringUtils.getAttributeStrNotNull(
				serviceRequest, "codDID");
		
        String txtOut = "";
        
        if(strCodiceFiscale != null && !strCodiceFiscale.equals(""))
        {txtOut += "Codice Fiscale Lavoratore <strong>"+ strCodiceFiscale +"</strong>; ";
        }
        if(datstipulada != null && !datstipulada.equals(""))
        {txtOut += "Data Stipula da <strong>"+ datstipulada +"</strong>; ";
        }
        if(datstipulaa != null && !datstipulaa.equals(""))
        {txtOut += "Data Stipula a <strong>"+ datstipulaa +"</strong>; ";
        } 
        if(codCpi != null && !codCpi.equals(""))
        {txtOut += "CPI <strong>"+ codCpi +"</strong>; ";
        } 
        if(codDID != null && !codDID.equals("T"))
        {
        	if(codDID.equals("D"))
        		txtOut += "Lavoratori <strong> Con DID </strong>; ";
        	if(codDID.equals("S"))
        		txtOut += "Lavoratori <strong> Senza DID </strong>; ";	
        } 
%>

<html>
<head>
<title>Risultati della ricerca</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />

<script type="text/Javascript">
  function tornaAllaRicerca()
  {  // Se la pagina è già in submit, ignoro questo nuovo invio!
	 if (isInSubmit()) return;
  
     url="AdapterHTTP?PAGE=listaDidInpsPage";
     url += "&CDNFUNZIONE="+"<%=_funzione%>";
     url += "&strCodiceFiscale="+"<%=strCodiceFiscale%>";
     url += "&datstipulada="+"<%=datstipulada%>";
     url += "&datstipulaa="+"<%=datstipulaa%>";
     url += "&codCpi="+"<%=codCpi%>";
     url += "&tipoRicerca="+"<%=tipoRicerca%>";
     url += "&codDID="+"<%=codDID%>";
	
		setWindowLocation(url);
	}
</script>

</head>
<!--<body  onload="checkError();" class="gestione" > -->
<body class="gestione" onload="rinfresca();rinfresca_laterale();">
	<center>
		<font color="red"> <af:showErrors />
		</font>
		
		<%if(txtOut.length() > 0)
          { txtOut = "Filtri di ricerca:<br/> " + txtOut;%>
            <table cellpadding="2" cellspacing="10" border="0" width="100%">
             <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
              <%out.print(txtOut);%>
             </td></tr>
            </table>
        <%}%>
        
	</center>
	<af:form dontValidate="true" onSubmit="tornaAllaRicerca()">
	
		<af:list moduleName="M_DynamicRicercaDidInps"
			configProviderClass="it.eng.sil.module.anag.DynRicDidInpsConfig"
			 getBack="true" />
		

		<center>
			<input class="pulsante" type="button" name="torna"
				value="Torna alla pagina di ricerca" onclick="tornaAllaRicerca()" />
		</center>
	</af:form>
	<br />
</body>
</html>
<%
	}
%>