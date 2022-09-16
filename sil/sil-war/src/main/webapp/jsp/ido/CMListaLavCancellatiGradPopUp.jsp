<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page contentType="text/html;charset=utf-8"
	import="com.engiweb.framework.base.*,com.engiweb.framework.error.*,it.eng.sil.security.*,it.eng.afExt.utils.*,it.eng.sil.util.*,java.math.*,com.engiweb.framework.message.*,it.eng.afExt.utils.MessageCodes,java.util.*"
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ taglib uri="aftags" prefix="af"%>
   
<% 
         	 	
ProfileDataFilter filter = new ProfileDataFilter(user, "CMListaLavCancellatiPage");
boolean canView = filter.canView();
boolean canStampaCancellati = false;
boolean canReintegraCancellati = false;
if (!canView) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
}
	PageAttribs attributi = new PageAttribs(user,
	"CMListaLavCancellatiPage");

	canStampaCancellati = attributi.containsButton("STAMPA");
	canReintegraCancellati = attributi.containsButton("STAMPA-REINTEGRA");

	String queryString = null;
 	String prgRichiestaAz = serviceRequest.getAttribute("PRGRICHIESTAAZ").toString();
	String prgAzienda = serviceRequest.getAttribute("PRGAZIENDA").toString();
	String prgRosa = serviceRequest.getAttribute("PRGROSA").toString();
	String prgTipoRosa = serviceRequest.getAttribute("PRGTIPOROSA").toString();
	String prgIncrocio = serviceRequest.getAttribute("PRGINCROCIO").toString();
	String prgTipoIncrocio = serviceRequest.getAttribute("PRGTIPOINCROCIO").toString();
	String prgOrig = prgRichiestaAz;
	String prgUnita = serviceRequest.getAttribute("PRGUNITA").toString();
	String _page = serviceRequest.getAttribute("PAGE").toString();
	String p_page = (String) serviceRequest.getAttribute("OLD_PAGE");
	String grezza = (String) serviceRequest.getAttribute("GREZZA");
	
	// Recupero il numero di records della tabella
	SourceBean row = (SourceBean) serviceResponse.getAttribute("CMLISTALAVCANCELLATIMODULE.ROWS");
	Integer records = (Integer)row.getAttribute("NUM_RECORDS");
	
	String codCpiApp = (String) serviceRequest.getAttribute("codCpi");
	String ConcatenaCpi = (String) serviceRequest.getAttribute("ConcatenaCpi");
	
	int nroMansioni = 0;   
	String prgC1 = "";
	boolean viewPar = false;
	
	Testata operatoreInfo 	= 	null; 
	BigDecimal cdnUtIns	= null;
	String dtmIns = "";
	BigDecimal cdnUtMod = null;
	String dtmMod = "";
	
	String p_codCpi = user.getCodRif();
	String _cdnFunzione = serviceRequest.getAttribute("cdnFunzione").toString();
  	String listPage = (String) serviceRequest.getAttribute("LIST_PAGE");
  	if (("").equals(listPage) || listPage==null) {
  		listPage = "1";
  	} 
  	     	    	  	  	  	  
    boolean canModify = true;  	
    boolean gestCopia = false;
    
    if (("CMStoricoDettGraduatoriaPage").equalsIgnoreCase(p_page)) {
    	canModify = false;
    }    
  	
  	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
    String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
  	
%>

<html>
<head>
<title>Lista Lavoratori L68 Cancellati</title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 <%@ include file="../../jsp/documenti/_apriGestioneDoc.inc"%>
 <af:linkScript path="../../js/" />
<script language="javascript">
		
	function stampaCancellati() {
		var vPRGROSA = <%=prgRosa%>;
	    apriGestioneDoc('RPT_STAMPA_CANCELLATI','&PRGROSA='+vPRGROSA,'PROSP');
	}
	
	function fieldChanged() {
    	<%if (canModify) {out.print("flagChanged = true;");}%>
	}	
			
	function tornaIndietro(){   
		<%
	    if (("CMStoricoDettGraduatoriaPage").equalsIgnoreCase(p_page)) {
	    %>
			window.location="AdapterHTTP?PAGE=CMStoricoDettGraduatoriaPage&MODULE=CMStoricoCandidatiGraduatoria&PRGROSA=<%=prgRosa%>&PRGTIPOROSA=<%=prgTipoRosa%>&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>&CDNFUNZIONE=<%=_cdnFunzione%>&codCpi=<%=codCpiApp%>&ConcatenaCpi=<%=ConcatenaCpi%>&LIST_PAGE=<%=listPage%>"; 	
	    <%
  		}
  		else {
	    %>    	 	   
			window.location="AdapterHTTP?PAGE=CMMatchDettGraduatoriaPage&MODULE=CMCandidatiGraduatoria&PRGROSA=<%=prgRosa%>&PRGTIPOROSA=<%=prgTipoRosa%>&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>&CDNFUNZIONE=<%=_cdnFunzione%>&codCpi=<%=codCpiApp%>&ConcatenaCpi=<%=ConcatenaCpi%>&LIST_PAGE=<%=listPage%>"; 	     	
		<%
		}
		%>
	}
		
	  function reintegra(cdnlavoratore) {
		  // Se la pagina è già in submit, ignoro questo nuovo invio!
		  if (isInSubmit()) return;
			
		  if(!confirm("l'operazione richiesta reintegrerà il lavoratore nella rosa grezza, vuoi continuare?")){
			return false;
		  }
	      var s= "AdapterHTTP?PAGE=CMListaLavCancellatiPage";
	      s += "&OLD_PAGE=CMListaLavCancellatiPage";
	      s += "&REINTEGRA=true";
	      s += "&GREZZA=true";
	      s += "&_ENCRYPTER_KEY_=<%=sessionContainer.getAttribute("_ENCRYPTER_KEY_")%>";
	      s += "&CDNLAVORATORE="+cdnlavoratore;
	      s += "&prgSpi=";
	      s += "&P_CDNUTENTE=<%=user.getCodut()%>";
	      s += "&P_CDNGRUPPO=<%=user.getCdnGruppo()%>";
	      s += "&PRGROSA=<%=prgRosa%>&PRGTIPOROSA=<%=prgTipoRosa%>&PRGINCROCIO=<%=prgIncrocio%>&PRGTIPOINCROCIO=<%=prgTipoIncrocio%>&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>&CDNFUNZIONE=<%=_cdnFunzione%>&codCpi=<%=codCpiApp%>&ConcatenaCpi=<%=ConcatenaCpi%>&LIST_PAGE=<%=listPage%>";
	      setWindowLocation(s);
	      return true;	  
	  }
	
</script>
</head>

<body onload="rinfresca()" >
<af:form name="Frm1" method="POST" action="AdapterHTTP">
<%@ include file="InfoCorrRichiesta.inc" %>


<!-- messaggi di esito delle operazioni applicative -->
<font color="red"><af:showErrors/></font>
<font color="green"> 
 <af:showMessages prefix="M_CMReintegraInGraduatoria"/>
</font>

<br>


<%
if (canReintegraCancellati && grezza.equals("true")) {
%>
	<!-- Tale lista viene visualizzata nel caso in cui lo stato della rosa è "Grezza", offrendo quindi la possibilità di reintegro dei lavoratori -->      
	<af:list moduleName="CMListaLavCancellatiReintegraModule" jsDelete="reintegra" />       
<%
}else{
%>
	<af:list moduleName="CMListaLavCancellatiModule" /> 
<%
}
%>

<table align="center">
<tr>			
	<td align="center">					
		<input type="button" class="pulsanti" value="Chiudi" onclick="tornaIndietro()"/>
		<%
		if (canStampaCancellati && records.intValue() > 0) {
		%>
			<input type="button" class="pulsanti" name="stampaCanc" value="Stampa Cancellati" onclick="stampaCancellati()" />
		<%
	 	}
		%>
	</td>			
</tr>

</table>	
<br/>
</af:form>
</body>
</html>