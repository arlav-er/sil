<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ taglib uri="presel" prefix="ps" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>


<%@ page import="com.engiweb.framework.base.*,
		com.engiweb.framework.security.*,
		it.eng.afExt.utils.*,
		it.eng.sil.security.*,
		it.eng.sil.util.*,
        it.eng.sil.module.coop.GetDatiPersonali,		
		java.util.*,
		java.math.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));

	ProfileDataFilter filter = new ProfileDataFilter(user, _page);


	if (! filter.canView()) {
			response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}
	else {

%>

<html>

<head>
	
	<title>Disponibilità geografica per le mansioni</title>
    <af:linkScript path="../../js/"/>
	<link rel="stylesheet" type="text/css" href="../../css/stiliCoop.css" />
	<link rel="stylesheet" type="text/css" href="../../css/listdetailCoop.css" />
<%@ include file="../global/Function_CommonRicercaComune.inc" %>
</head>

<body class="gestione" onload="">
	
	<%@ include file="_testataLavoratore.inc"%>
	<%@ include file="_linguetta.inc" %>

  <p align="center">
	  	<font color="red">
	      <af:showErrors />
	    </font>
  </p>


<table width="96%" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td class="sfondo_lista" valign="top" align="left" width="6" height="6px"><img src="../../img/angoli/bia1.gif" width="6" height="6"></td>
    <td class="sfondo_lista" height="6px">&nbsp;</td>
    <td class="sfondo_lista" valign="top" align="right" width="6" height="6px"><img src="../../img/angoli/bia2.gif" width="6" height="6"></td>
  </tr>
  <tr>
    <td class="sfondo_lista" width="6">&nbsp;</td>

    <td class="sfondo_lista" align="center">
      <TABLE class="lista" align="center">

        <TR>
          <TH class="lista" width="16%">&nbsp;Mansione&nbsp;</TH>
          <TH class="lista" width="24%">&nbsp;Comuni&nbsp;</TH>
          <TH class="lista" width="20%">&nbsp;Province&nbsp;</TH>
          <TH class="lista" width="20%">&nbsp;Regioni&nbsp;</TH>
          <TH class="lista" width="20%">&nbsp;Stati&nbsp;</TH>
        </TR>
        <%
        String strDesMansione= "";
        String strDesComune= "";
        String strDesRegione= "";
        String strDesProvincia= "";
        String strDesStato= "";

        boolean pari = false;
        boolean almenoUnaMans = false;
        String classeLista = pari ? "lista_pari" : "lista_dispari" ;
        SourceBean sbMansione = null;
        Vector vettMansioni = serviceResponse.getAttributeAsVector("M_COOP_ListTerritoriMansioni_dalla_cache.MANSIONI.ROWS.ROW");
        for (int i=0; i< vettMansioni.size(); i++) {
	       	sbMansione = (SourceBean) vettMansioni.get(i);
	       	strDesMansione = StringUtils.getAttributeStrNotNull(sbMansione,"DESCRIZIONE");
	          
	       	// Recupero subito tutti i vettori,
	       	Vector vettComuni   = sbMansione.getAttributeAsVector("COMUNI.ROWS.ROW");
	       	Vector vettProvince = sbMansione.getAttributeAsVector("PROVINCE.ROWS.ROW");
		   		Vector vettRegioni  = sbMansione.getAttributeAsVector("REGIONI.ROWS.ROW");
		   		Vector vettStati    = sbMansione.getAttributeAsVector("STATI.ROWS.ROW");

		   		// NON STAMPO LA RIGA DELLA MANSIONE SE NON HA ALCUNCHE' DA MOSTRARE!
		   		if (vettComuni.isEmpty()  && vettProvince.isEmpty() &&
		       		vettRegioni.isEmpty() && vettStati.isEmpty() ) {
		         // Nulla da fare
		   		}
		   		else {%>
	          <TR class="lista">
	
	            <TD class="<%=classeLista%>">
	            	<%=strDesMansione%>
	            </TD>
	            
	            <TD class="<%=classeLista%>"><TABLE>
		            <%
		            if (vettComuni.isEmpty()) { %> <tr><td>&nbsp;</td></tr> <% }
		            else
			            for (int j=0; j<vettComuni.size(); j++) {
			                SourceBean sbComune = ((SourceBean) vettComuni.get(j));
			                strDesComune = StringUtils.getAttributeStrNotNull(sbComune,"STRDENOMINAZIONE"); %>
			                <TR>                                 
			                  <TD>
			                  	<%=strDesComune.replace('\'', '`')%>
			                  </TD>
			                </TR>
			         <% } %>
	            </TABLE></TD>
	
	            <TD class="<%=classeLista%>"><TABLE>
		            <%
		            if (vettProvince.isEmpty()) { %> <tr><td>&nbsp;</td></tr> <% }
		            else
			            for (int j=0; j<vettProvince.size(); j++) {
			                SourceBean sbProvincia = ((SourceBean) vettProvince.get(j));
			                strDesProvincia = StringUtils.getAttributeStrNotNull(sbProvincia,"STRDENOMINAZIONE");%>
			                <TR>
			                  <TD>
			                  	<%=strDesProvincia.replace('\'', '`')%>
			                  </TD>
			                <TR>
			         <% } %>
	            </TABLE></TD>
	
	            <TD class="<%=classeLista%>"><TABLE>
		            <%
		            if (vettRegioni.isEmpty()) { %> <tr><td>&nbsp;</td></tr> <% }
		            else
			            for (int j=0; j<vettRegioni.size(); j++) {
			                SourceBean sbRegione = ((SourceBean) vettRegioni.get(j));
			                strDesRegione = StringUtils.getAttributeStrNotNull(sbRegione,"STRDENOMINAZIONE");%>
			                <TR>
			                  <TD>
			                  	<%=strDesRegione.replace('\'', '`')%>
			                  </TD>
			                <TR>
			         <% } %>
	            </TABLE></TD>
	
	            <TD class="<%=classeLista%>"><TABLE>
		            <%
		            if (vettStati.isEmpty()) { %> <tr><td>&nbsp;</td></tr> <% }
		            else
			            for (int j=0; j<vettStati.size(); j++) {
			                SourceBean sbStato = ((SourceBean) vettStati.get(j));
			                strDesStato = StringUtils.getAttributeStrNotNull(sbStato,"STRDENOMINAZIONE");%>
			                <TR>
			                  <TD>
			                  	<%=strDesStato.replace('\'', '`')%>
			                  </TD>
			                </TR>
			         <% } %>
	            </TABLE></TD>
	          </TR>
	          <%
	          almenoUnaMans = true;
	          pari = !pari;
	          classeLista = pari ? "lista_pari" : "lista_dispari";
		   		} //else
        } //for i

				// Se non ho stampato neppure una riga, mostro il messaggio di avviso        
        if (! almenoUnaMans) {
        	%>
        	<tr>
        		<td colspan="6"><b>Non &egrave; stato trovato alcun risultato.</b></td>
        	</tr>
        	<%
        }
        %>

      </TABLE>
    </td>
    
    <td class="sfondo_lista" width="6">&nbsp;</td>
  </tr>

  <tr valign="bottom">
    <td class="sfondo_lista" valign="bottom" align="left" width="6" height="6px"><img src="../../img/angoli/bia4.gif" width="6" height="6"/></td>
    <td class="sfondo_lista" height="6px">&nbsp;</td><td class="sfondo_lista" valign="bottom" align="right" width="6" height="6px"><img src="../../img/angoli/bia3.gif" width="6" height="6"/></td>
  </tr>
</table>

</body>

</html>

<% } %>