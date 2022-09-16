<%@ page contentType="text/html;charset=utf-8"%>
 
<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                java.math.*,
                 it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
	String _page = (String) serviceRequest.getAttribute("PAGE"); 

  
	 // da togliere ovviamente
 	PageAttribs attributi = new PageAttribs(user, _page);
	int     cdnfunzione      = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	String  _funzione   = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");
  
 
 
 	String cdnLavoratore= (String) StringUtils.getAttributeStrNotNull(serviceRequest, "CDNLAVORATORE");
 
 	String cpiCodRif = user.getCodRif();
  
	//CONTROLLO ACCESSO ALLA PAGINA
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));

	boolean canView = filter.canViewLavoratore();
	if (!canView) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}

	boolean canAdd = attributi.containsButton("EVENT_COND");
%>
 
<html>
<head>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/> 
 <af:linkScript path="../../js/"/>
 

<script type="text/Javascript">
	<%//Genera il Javascript che si occuperà di inserire i links nel footer
	if (!cdnLavoratore.equals(""))
		attributi.showHyperLinks(out, requestContainer, responseContainer, "cdnLavoratore=" + cdnLavoratore);
	%>
</script>

<script type="text/javascript">
	function nuovoEventoPage() {

  	var urlpage="AdapterHTTP?";
    urlpage+="CDNFUNZIONE=<%=_funzione%>&";
    urlpage+="CDNLAVORATORE=<%=cdnLavoratore%>&";
    urlpage+="CPICODRIF=<%=cpiCodRif%>&";
    urlpage+="PAGE=EventoCondizionalitaPage&"; 
  	 
	window.open(urlpage,"","toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,height=600,width=700" );

}

</script> 
</head>
<body  class="gestione">
			
	 
<%
InfCorrentiLav testata = new InfCorrentiLav(RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
testata.show(out);
Linguette l  = new Linguette(user, Integer.parseInt(_funzione), _page, new BigDecimal(cdnLavoratore));
l.show(out);
%>
<af:list moduleName="M_ListaEventiCondizionalita"/>

<%if(canAdd){ %>
	<af:form name="Frm1" action="AdapterHTTP" method="POST" >
	<input type="hidden" name="PAGE" value="EventoCondizionalitaPage"/>
 	<af:textBox type="hidden" name="cdnlavoratore" value="<%= cdnLavoratore %>" />
	<af:textBox type="hidden" name="cdnfunzione" value="<%= _funzione %>" />
	<af:textBox type="hidden" name="CPICODRIF" value="<%= cpiCodRif %>" />
	<center>
		<input type="submit" class="pulsanti" name="addEvento" value="Nuovo Evento Condizionalità"   />
 	</center>
 </af:form>	 
 	
<%}%> 


</body>
</html>