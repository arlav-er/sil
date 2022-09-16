<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ taglib uri="presel" prefix="ps" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
  com.engiweb.framework.base.*,
  it.eng.sil.security.*,
  it.eng.sil.util.*,
  java.util.*,
  java.lang.*,
  java.math.*
" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
String prgRichiestaAz = (String)serviceRequest.getAttribute("PRGRICHIESTAAZ");
String _page = (String) serviceRequest.getAttribute("PAGE");
int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
BigDecimal prgAzienda=null;
BigDecimal prgUnita=null;
String strPrgAziendaMenu="";
String strPrgUnitaMenu="";
String strCodiciEsiti = "";
SourceBean rigaTestata = null;
String cdnStatoRich = "";
SourceBean contTestata = (SourceBean) serviceResponse.getAttribute("M_GETTESTATARICHIESTA");
Vector rows_VectorTestata = null;
rows_VectorTestata = contTestata.getAttributeAsVector("ROWS.ROW");
if (rows_VectorTestata.size()!=0) {
  	rigaTestata=(SourceBean) rows_VectorTestata.elementAt(0);
  	cdnStatoRich = rigaTestata.getAttribute("cdnStatoRich").toString();
  	prgAzienda = (BigDecimal) rigaTestata.getAttribute("PRGAZIENDA");
  	if (prgAzienda != null) {  
    	strPrgAziendaMenu = prgAzienda.toString();
  	}
  	prgUnita = (BigDecimal) rigaTestata.getAttribute("PRGUNITA");
  	if (prgUnita != null) {
    	strPrgUnitaMenu = prgUnita.toString();
  	}
}
boolean btnInserisci = false;
//lettura eventuali esiti della richiesta
SourceBean esitiRichiesta = (SourceBean) serviceResponse.getAttribute("M_GetEsitiRichiesta");
Vector rows_EsitiRichiesta = esitiRichiesta.getAttributeAsVector("ROWS.ROW");
int nSizeEsiti = rows_EsitiRichiesta.size();
//lettura degli esiti possibili
SourceBean esitiOfferta = (SourceBean) serviceResponse.getAttribute("M_GetEsitiOfferta");
Vector rows_EsitiOfferta = esitiOfferta.getAttributeAsVector("ROWS.ROW");
int nSizeOfferta = rows_EsitiOfferta.size();
boolean canInsert= false;
boolean canUpdate= false;
boolean canModify= true;
PageAttribs attributi = new PageAttribs(user, _page);

//NOTE: Attributi della pagina (pulsanti e link) 
ProfileDataFilter filter = new ProfileDataFilter(user, _page);
filter.setPrgAzienda(prgAzienda);
filter.setPrgUnita(prgUnita);
boolean canView=filter.canViewUnitaAzienda();
if ( !canView ) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
}
else {
	if (cdnStatoRich.compareTo("4")!=0 && cdnStatoRich.compareTo("5")!=0) {
		canInsert = attributi.containsButton("INSERISCI");
		canUpdate = attributi.containsButton("AGGIORNA");
	}
	if ( !canInsert && !canUpdate ) {
		canModify = false;      
    } else {
 		boolean canEdit = filter.canEditUnitaAzienda();
      	if ( !canEdit ) {
        	canModify = false;
        	canInsert = false;
        	canUpdate = false;
      	}
    }
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
	%>
	<%@ include file="_infCorrentiAzienda.inc" %> 
	<html>
	<head>
	<title>Esiti</title>
	<link rel="stylesheet" href="../../css/stili.css" type="text/css">
	<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
	<af:linkScript path="../../js/"/>
	<SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
	<SCRIPT TYPE="text/javascript">
		var flagChanged = false;
	  	window.top.menu.caricaMenuAzienda(<%=_funzione%>, <%=strPrgAziendaMenu%>, <%=strPrgUnitaMenu%>);  
	    <% 
	    //Genera il Javascript che si occuperà di inserire i links nel footer
	    attributi.showHyperLinks(out, requestContainer,responseContainer,"prgAzienda="+prgAzienda+"&prgUnita="+prgUnita);
	    %>
	</SCRIPT>
	</head>
	
	<body class="gestione" onload="rinfresca()">
	<%
	if (infCorrentiAzienda != null) {
		infCorrentiAzienda.show(out); 	
	}
	if(prgRichiestaAz != null && !prgRichiestaAz.equals("") ) {
		Linguette l = new Linguette( user,  _funzione, _page, new BigDecimal(prgRichiestaAz));
	    l.setCodiceItem("prgRichiestaAz");    
	    l.show(out);
	}
	%> 
	<p align="center">
	<af:form name="Frm1" method="POST" action="AdapterHTTP">
	<center>
	<font color="green">
	  <af:showMessages prefix="M_InsertEsitiOfferta"/>
	  <af:showMessages prefix="M_UpdateEsitiOfferta"/>
	</font>
	</center>
	<center>
	<font color="red">
	  <af:showErrors/>
	</font>
	</center>
	<%out.print(htmlStreamTop);%>
	<table class="main">
	<%
	int sommaValEsiti = 0;
	if (nSizeEsiti > 0) {
		for (int i=0;i<nSizeEsiti;i++) {
			SourceBean esitoBean = 	(SourceBean)rows_EsitiRichiesta.get(i);
			String descEsito = esitoBean.containsAttribute("descrizione")?esitoBean.getAttribute("descrizione").toString():"";
			String codEsito = esitoBean.getAttribute("codice").toString();
			String valore = esitoBean.containsAttribute("valore")?esitoBean.getAttribute("valore").toString():"";
			if (!valore.equals("")) {
				Integer valEsito = new Integer(valore);
				sommaValEsiti = sommaValEsiti + valEsito.intValue();
			}
			String nameCampo = "valoreEsito_" + codEsito;
			if (i==0) {
				strCodiciEsiti = codEsito;	
			}
			else {
				strCodiciEsiti = strCodiciEsiti + "#" + codEsito;	
			}
			%>
			<tr>
			<td class="etichetta"><%=descEsito %></td>
	  		<td class="campo">
	  		<af:textBox classNameBase="input" type="integer" 
	  			name="<%=nameCampo %>" value="<%=valore%>" validateOnPost="true"/>
			</td>
			</tr>
			<%
		}
		Integer totaleEsitiOff = new Integer(sommaValEsiti);
		%>
		<tr>
		<td class="etichetta">totale:</td>
  		<td class="campo">
  		<af:textBox classNameBase="input" type="integer" readonly="true"
  			name="totaleEsitiOff" value="<%=totaleEsitiOff.toString()%>" />
		</td>
		</tr>
		<%
	} else {
		btnInserisci = true;
		for (int i=0;i<nSizeOfferta;i++) {
			SourceBean esitoBean = 	(SourceBean)rows_EsitiOfferta.get(i);
			String descEsito = esitoBean.containsAttribute("descrizione")?esitoBean.getAttribute("descrizione").toString():"";
			String codEsito = esitoBean.getAttribute("codice").toString();
			String valore = "";
			String nameCampo = "valoreEsito_" + codEsito;
			if (i==0) {
				strCodiciEsiti = codEsito;	
			}
			else {
				strCodiciEsiti = strCodiciEsiti + "#" + codEsito;	
			}
			String titleCampo = "Numero posti per " + descEsito;
			%>
			<tr>
			<td class="etichetta"><%=descEsito %></td>
	  		<td class="campo">
	  		<af:textBox classNameBase="input" type="integer" title="<%=titleCampo %>" 
	  			name="<%=nameCampo %>" value="<%=valore%>" validateOnPost="true"/>
			</td>
			</tr>
			<%
		}
	}
	%>
	</table>
	<input type="hidden" name="PAGE" value="<%= _page %>"/>
	<input type="hidden" name="CDNFUNZIONE" value="<%= _funzione %>"/>
	<input type="hidden" name="PRGRICHIESTAAZ" value="<%= prgRichiestaAz %>"/>
	<input type="hidden" name="CODICIESITI" value="<%= strCodiciEsiti %>"/>
	<%
	if (canInsert && btnInserisci) {
	%>
	<table>
	<tr><td align="center">
	<input class="pulsante" type="submit" name="BTNINSERT" value="Inserisci">
	</td></tr>
	</table>
	<%	
	}
	else {
		if (canUpdate) {
		%>
		<table>
		<tr><td align="center">
		<input class="pulsante" type="submit" name="BTNUPDATE" value="Aggiorna">
		</td></tr>
		</table>
		<%}
	}%>
	<%out.print(htmlStreamBottom);%>
	</af:form>
	</p>
	</body>
	</html>
<% } %>