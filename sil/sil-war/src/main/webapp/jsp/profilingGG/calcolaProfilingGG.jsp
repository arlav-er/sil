<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
			it.eng.sil.security.*,
			it.eng.afExt.utils.*,
			it.eng.sil.util.*,
			java.math.*,
			java.util.*"
	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%
	String  titolo = "Calcola Nuovo Profiling GG";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	// Lettura parametri dalla REQUEST
	int     cdnfunzione      = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	String  cdnfunzioneStr   = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");
	String  cdnLavoratore    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
	

	// CONTROLLO ACCESSO ALLA PAGINA
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	
	boolean canView = filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}

	// CONTROLLO PERMESSI SULLA PAGINA
	PageAttribs attributi = new PageAttribs(user, _page);

	boolean canModify = true;
	
	Object cdnUtCorrente    = sessionContainer.getAttribute("_CDUT_");
	String cdnUtCorrenteStr = StringUtils.getStringValueNotNull(cdnUtCorrente);
 
	
	BigDecimal prgProfilingGG =null;
	String descrTipoProfiling= null; 
	String strCodiceFiscale= null;
	String codiceProvincia = null;
	String strDataCalcolo = null;
	String codiceDurataPrIt= null;
	String codiceCondOccupaz= null;
	String codTitoloStudio = null;
	String descrTitolo=null;
	String strSesso= null;
	String strEta= null;
	String strIndice2= null;
	String descrIndiceProf2= null;
	String codiceProvinciaInvio= null;
	
	SourceBean infoLav  = (SourceBean) serviceResponse.getAttribute("M_GetInfoLavProfilingGG.ROWS.ROW");
	codiceProvincia = StringUtils.getAttributeStrNotNull(infoLav, "codProvCpi");	
	codiceProvinciaInvio = StringUtils.getAttributeStrNotNull(infoLav, "codProvCpiInvio");	
	strCodiceFiscale = StringUtils.getAttributeStrNotNull(infoLav, "STRCODICEFISCALE");	
	if (StringUtils.getAttributeStrNotNull(infoLav, "CODCITTADINANZA").equals("000")) {
		codiceDurataPrIt = "A01";
	} else {
		String nascitaEstero = StringUtils.getAttributeStrNotNull(infoLav, "codiceEstero");
		if (!nascitaEstero.equals("99")) {
			codiceDurataPrIt = "A02";
		}
	}
	String codiceUltimoTitolo = StringUtils.getAttributeStrNotNull(infoLav, "CODTITOLO");
	if (StringUtils.isFilledNoBlank(codiceUltimoTitolo)) {
		if (StringUtils.getAttributeStrNotNull(infoLav, "FLGPRINCIPALE").equals("S") && StringUtils.getAttributeStrNotNull(infoLav, "FLGPFGG").equals("S")) {
			codTitoloStudio = StringUtils.getAttributeStrNotNull(infoLav, "codtitolo");
			descrTitolo = StringUtils.getAttributeStrNotNull(infoLav, "strTitoloStudio");
		}
	}

	boolean isFromWs = false;
	
	// Sola lettura: viene usato per tutti i campi di input
	String readonly = String.valueOf(!canModify);
	String required = String.valueOf( canModify );
	String readonlyAlways = String.valueOf(true);

	// Stringhe con HTML per layout tabelle
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>

<html>
<head>
<title><%= titolo %></title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/jqueryui/jquery-ui.min.css">
<link rel="stylesheet" type="text/css" href="../../css/jqueryui/jqueryui-sil.css">  


<%@ include file="../global/fieldChanged.inc" %>
  
  <af:linkScript path="../../js/"/>
  <script src="../../js/jqueryui/jquery-1.12.4.min.js"></script>
  <script src="../../js/jqueryui/jquery-ui.min.js"></script>
 
<script language="Javascript">
var contextPath = "<%=request.getContextPath()%>";
$(function() {
$( "[name='strTitolo']" ).autocomplete({
	//width: 300,
    max: 10,
    delay: 50,
    minLength: 3,
    autoFocus: true,
    cacheLength: 1,
    scroll: true,
    highlight: true,
    
	source: function(request, response) {
		
        $.ajax({
            url: contextPath + "/services/autocompleteServletComponent?prefixQueryName=TITOLOSTUDIOGG",
            dataType: "json",
            data: request,
            success: function( data, textStatus, jqXHR) {
              //  console.log( data);
             var items = data.matchingItems;
             if(items.length <=0){
            	 items = new Array();
            	 var noResult ={id: "", value:""};
            	 items[0]=noResult;
            	 $( "[name='codTitolo']" ).val(null);
            	 
             }
                response(items);
                
             },
            error: function(jqXHR, textStatus, errorThrown){
                 console.log( textStatus);
            }
    
        });
    },
    select: function(event, ui) {
      
    	$( "[name='codTitolo']" ).val(ui.item.id);
    }
   
  });
	$( "[name='strTitolo']" ).on( "keydown", function( event ) {
	  switch( event.keyCode ) {
	    case $.ui.keyCode.BACKSPACE:
	      $( "[name='codTitolo']" ).val(null);
	      break;
	    case $.ui.keyCode.DELETE:
	      $( "[name='codTitolo']" ).val(null);
	      break;
	    case $.ui.keyCode.SPACE:
		  $( "[name='codTitolo']" ).val(null);
		  break;
	  }
	});
  
});
 
	/* Funzione chiamata al caricamento della pagina */
	function onLoad() {
		rinfresca();
		// altri funzioni da richiamare sulla onLoad...
	}

<%
	// Genera il Javascript che si occuperà di inserire i links nel footer
	attributi.showHyperLinks(out, requestContainer, responseContainer, "cdnLavoratore="+cdnLavoratore);
%>

</script>
</head>

<body class="gestione" onload="onLoad()">

<br/>
<p class="titolo"><%= titolo %></p>

	<p>
	 	<font color="green">
			<af:showMessages prefix="M_GestioneProfilingGG"/>
	  	</font>
	  	<font color="red"><af:showErrors /></font>
	</p>

<af:form name="Frm1" action="AdapterHTTP" method="POST">

<input type="hidden" name="PAGE" value="DettaglioProfilingGGPage"/>
<input type="hidden" name="OPERAZIONE_GG" value="CALCOLO"/>
<af:textBox type="hidden" name="cdnfunzione" value="<%= cdnfunzioneStr %>" />
<af:textBox type="hidden" name="CDNUTMOD" value="<%= cdnUtCorrenteStr %>" />
<af:textBox type="hidden" name="cdnlavoratore" value="<%= cdnLavoratore %>" />
<af:textBox type="hidden" name="codiceProvinciaInvio" value="<%= codiceProvinciaInvio %>" />
 
<%= htmlStreamTop %>
<table class="main">

	<%--
	Tutti i campi di input sono nel file INC poiché sono
	condivisi dalle pagine di dettaglio e di nuovo.
	--%>
	<%-- ***************************************************************************** --%>
	<%@ include file="dettaglioCampi.inc" %> 
	<%-- ***************************************************************************** --%>

	<% if (canModify) { %>
		<tr>
			<td colspan="2">
				<input type="submit" name="invioProfilingGG"  class="pulsanti" value="Invia dati profiling" />
			</td>
		</tr>
		<tr>
			<td colspan="2">	
				<input type="button" onClick="window.close();" class="pulsanti" value="Chiudi" >
			</td>
		</tr>
		<tr>
			<td  colspan="2">&nbsp;</td>
		</tr>
	<% } %>


</table>
<%= htmlStreamBottom %>


</af:form>

</body>
</html>
