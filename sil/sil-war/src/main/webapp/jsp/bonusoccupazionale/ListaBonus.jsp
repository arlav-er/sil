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
ProfileDataFilter filter = new ProfileDataFilter(user, _page);

if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
} else {
	// NOTE: Attributi della pagina (pulsanti e link) 
	PageAttribs attributi = new PageAttribs(user, _page);

	String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
	// Visualizzazione parametri ricerca
	String txtOut = "";
	
	String strCodiceFiscale = StringUtils.getAttributeStrNotNull(serviceRequest, "strCodiceFiscale");
	String datinda = StringUtils.getAttributeStrNotNull(serviceRequest, "datadesioneda");
	String datina = StringUtils.getAttributeStrNotNull(serviceRequest, "datadesionea");
	String codProvincia = StringUtils.getAttributeStrNotNull(serviceRequest, "codProv");
	String tipoRicerca = StringUtils.getAttributeStrNotNull(serviceRequest, "tipoRicerca");
	String flgPresaCarico = StringUtils.getAttributeStrNotNull(serviceRequest, "flgPresaCarico");
	String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "cdnLavoratore");
	
	if(strCodiceFiscale != null && !strCodiceFiscale.equals(""))
    {txtOut += "Codice Fiscale Lavoratore <strong>"+ strCodiceFiscale +"</strong>; ";
    }
    if(datinda != null && !datinda.equals(""))
    {txtOut += "Data Adesione da <strong>"+ datinda +"</strong>; ";
    }
    if(datina != null && !datina.equals(""))
    {txtOut += "Data Adesione a <strong>"+ datina +"</strong>; ";
    }
    Vector provRows = serviceResponse.getAttributeAsVector("M_GetProvince.ROWS.ROW");
	if (!codProvincia.equals("")) {
		for (int k=0;k<provRows.size();k++) {
			SourceBean provRow = (SourceBean)provRows.get(k);
			String codProvCurr = (String)provRow.getAttribute("codice");
			if (codProvCurr.equalsIgnoreCase(codProvincia)) {
				txtOut += "Provincia <strong>"+ StringUtils.getAttributeStrNotNull(provRow, "descrizione") +"</strong>; ";	
			}
		}
	}
	if(flgPresaCarico != null && flgPresaCarico.equals("S"))
    {txtOut += "Presa in carico <strong>Sì</strong>; ";
    }
	
	
%>

<html>
<head>
<title>Risultati della ricerca</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />

<script type="text/Javascript">
	<%//Genera il Javascript che si occuperà di inserire i links nel footer
	if (!cdnLavoratore.equals(""))
		attributi.showHyperLinks(out, requestContainer, responseContainer, "cdnLavoratore=" + cdnLavoratore);%>

	function tornaRicerca() {  
	 	// Se la pagina è già in submit, ignoro questo nuovo invio!
	 	if (isInSubmit()) return;

	 	var url="AdapterHTTP?PAGE=RicercaBonusPage";
	    url += "&CDNFUNZIONE="+"<%=_funzione%>";
	    url += "&strCodiceFiscale="+"<%=strCodiceFiscale%>";
	    url += "&datadesioneda="+"<%=datinda%>";
	    url += "&datadesionea="+"<%=datina%>";
	    url += "&codProv="+"<%=codProvincia%>";
	    url += "&flgPresaCarico="+"<%=flgPresaCarico%>";
	    url += "&tipoRicerca="+"<%=tipoRicerca%>";
		
		setWindowLocation(url);
	}
</script>

</head>

<%if (!cdnLavoratore.equals("")) {%>
<body class="gestione">
<%} else {%>
<body class="gestione" onload="rinfresca();rinfresca_laterale();">
<%}%>
	<script language="Javascript">
		window.top.menu.caricaMenuLav(<%=_funzione%>,<%=cdnLavoratore%>);
	</script>

	<%if (!cdnLavoratore.equals("")) {
		InfCorrentiLav testata = new InfCorrentiLav(RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
		testata.show(out);
	}%>


	<center>
		<font color="red"> <af:showErrors />
		</font>
		
		<%if(cdnLavoratore.equals("") && txtOut.length() > 0)
          { txtOut = "Filtri di ricerca:<br/> " + txtOut;%>
            <table cellpadding="2" cellspacing="10" border="0" width="100%">
             <tr><td style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:#dcdcdc">
              <%out.print(txtOut);%>
             </td></tr>
            </table>
        <%}%>
        
	</center>
	<af:form name="Frm1" dontValidate="true">
		
		<af:list moduleName="M_DynamicRicercaBonus" getBack="true"/>
		<%if(cdnLavoratore.equals("")) {%>
		<center>
			<input type="button" class="pulsanti" value="Torna alla pagina di ricerca"
				       onClick="tornaRicerca()" />
		</center>
		<%}%>
	</af:form>
	<br />
</body>
</html>
<%
}
%>