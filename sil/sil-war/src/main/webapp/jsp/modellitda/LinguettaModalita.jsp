<%@ page contentType="text/html;charset=utf-8"%>
 
<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*,
                it.eng.sil.security.*, java.math.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
	String _page = (String) serviceRequest.getAttribute("PAGE"); 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page);
  //NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, _page);
  boolean canInsert = false;
  
  
  if (!filter.canView()) {
  	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
  }else{
	  canInsert = attributi.containsButton("NuovoModModalita");
	  SourceBean rowModelloAttivo = (SourceBean)serviceResponse.getAttribute("M_GetModelloAttivo.ROWS.ROW");
	  String strModelloAttivo = StringUtils.getAttributeStrNotNull(rowModelloAttivo, "FLGATTIVO");
	  if(StringUtils.isFilledNoBlank(strModelloAttivo) && strModelloAttivo.equalsIgnoreCase("S")){
		  canInsert = false;
	  }
  }

  
  String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
  String prgModelloVoucher = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGMODVOUCHER");
  String  titolo = "Dettaglio Modello TDA";
%>

<html>
<head>
  
  <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
  
  <af:linkScript path="../../js/"/>

  <script type="text/Javascript">
 
  
  function nuovaModalita(){	
		 
		var url = 'AdapterHTTP?PAGE=InserisciModalitaTdaPage';
		url = url + '&cdnfunzione=<%=_funzione%>';
		url += "&PRGMODVOUCHER="+<%=prgModelloVoucher %>;
	 
	 	var w=900; 
        var l=((screen.availWidth)-w)/2;
        var h=520;   
        var t=((screen.availHeight)-h)/2;
        var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
        var tit = '_blank';
        
        window.open(url,tit,feat);
		
 	 }
  
	function apriDettaglio(page,CDNFUNZIONE,PRGMODMODALITA){
    	var w=900; 
        var l=((screen.availWidth)-w)/2;
        var h=520;   
        var t=((screen.availHeight)-h)/2;
        var feat = "status=NO,location=NO,toolbar=NO,scrollbars=YES,resizable=YES,height="+h+",width="+w+",top="+t+",left="+l;
        var tit = '_blank';
        var url = "AdapterHTTP?PAGE=DettaglioModalitaTdaPage";
        url += "&PRGMODMODALITA="+PRGMODMODALITA;
        url += "&CDNFUNZIONE=" + <%=_funzione%> ;
        url += "&PRGMODVOUCHER="+<%=prgModelloVoucher %>;
    	 
        window.open(url,tit,feat);
    }
  </script>
 
</head>
<body  class="gestione" onload="rinfresca();">
<p class="titolo"><%= titolo %></p>
<p>
<af:showErrors />
<af:showMessages prefix="M_InsertUpdateModalitaTda"/>
</p>
<%
Linguette _linguetta = new Linguette(user, new Integer(_funzione).intValue() , _page, new BigDecimal(prgModelloVoucher)); 
_linguetta.setCodiceItem("PRGMODVOUCHER");
_linguetta.show(out);
%>

<af:list moduleName="M_ListaModModalitaTda" jsSelect="apriDettaglio"/>
	 
<center>
		<table class="main">
		<%if(canInsert){ %>
		 <tr>
		<td>
		<input class="pulsante" type="button" name="inserisci" value="Nuova Modalità" onclick="nuovaModalita()"/>
		</td>
		</tr>
		<%}%>
</table>

</center>

</body>
</html>