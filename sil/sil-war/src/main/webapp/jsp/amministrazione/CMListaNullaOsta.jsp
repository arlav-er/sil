<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                 java.lang.*,
                 java.text.*,
                 java.util.*,
                 java.math.*,
                 it.eng.sil.bean.*,
                 java.util.HashSet,
                 it.eng.afExt.utils.StringUtils,
                 it.eng.sil.security.*,
                 it.eng.sil.util.Sottosistema"%>
            
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  ProfileDataFilter filter = new ProfileDataFilter(user, _page);
  PageAttribs attributi = new PageAttribs(user, _page);
  String _funzione =(String) serviceRequest.getAttribute("CDNFUNZIONE");
  
  boolean canNuovo=false;
  
  if (! filter.canView()){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	} else {
		
		canNuovo = attributi.containsButton("NUOVO");
	}
  
  String prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGAZIENDA");
  String prgUnita = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGUNITA");
  String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "CDNLAVORATORE");
  
  String goBackListPage = StringUtils.getAttributeStrNotNull(serviceRequest, "goBackListPage");
  String FLGCFOK = StringUtils.getAttributeStrNotNull(serviceRequest, "FLGCFOK");
  String FLGDATIOK = StringUtils.getAttributeStrNotNull(serviceRequest, "FLGDATIOK");
  String IndirizzoAzienda = StringUtils.getAttributeStrNotNull(serviceRequest, "IndirizzoAzienda");
  String codFiscaleAzienda = StringUtils.getAttributeStrNotNull(serviceRequest, "codFiscaleAzienda");
  String codiceFiscaleLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "codiceFiscaleLavoratore");
  String cognome = StringUtils.getAttributeStrNotNull(serviceRequest, "cognome");
  String nome = StringUtils.getAttributeStrNotNull(serviceRequest, "nome");
  String ragioneSociale = StringUtils.getAttributeStrNotNull(serviceRequest, "ragioneSociale");
  String pIva = StringUtils.getAttributeStrNotNull(serviceRequest, "pIva");
  String infoDatiNOLav = StringUtils.getAttributeStrNotNull(serviceRequest, "infoDatiNOLav");
  String infoDatiNOAzi = StringUtils.getAttributeStrNotNull(serviceRequest, "infoDatiNOAzi");
  












%>

<html>
<head>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>


  
<SCRIPT language="Javascript" type="text/javascript">

function tornaRicerca(){
		var f;
		f = "AdapterHTTP?PAGE=CMRicercaNullaOstaPage";
		f = f + "&CDNFUNZIONE=<%=_funzione%>";
		<%if (!cdnLavoratore.equals("") && infoDatiNOLav.equals("true") ){%>
		f = f + "&cdnLavoratore=<%=cdnLavoratore%>";
		<%}%>
		<%if (!prgAzienda.equals("") && infoDatiNOAzi.equals("true")){%>
		f = f + "&prgAzienda=<%=prgAzienda%>";
		<%}%>
		<%if (!prgUnita.equals("") && infoDatiNOAzi.equals("true")){%>
		f = f + "&prgUnita=<%=prgUnita%>";
		<%}%>
		document.location = f;
      }



// -->
</SCRIPT>
<script language="Javascript">
<% 
       //Genera il Javascript che si occuperà di inserire i links nel footer
       		attributi.showHyperLinks(out, requestContainer,responseContainer,"");
       
 %>
     
</script>

</head>
<body class="gestione" onload="rinfresca();">
<p></p>
<p class="titolo">Lista Nulla Osta</p>

<p align="center">


<af:form name="Frm1" method="POST" action="AdapterHTTP">

<input type="hidden" name="PAGE" value="CMNullaOstaPage"/>
<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
<input type="hidden" name="nuovoNullaOsta" value="1" />
<input type="hidden" name="infoDatiNOLav" value="<%=infoDatiNOLav%>" />
<input type="hidden" name="infoDatiNOAzi" value="<%=infoDatiNOAzi%>" />
<% if (infoDatiNOLav.equals("true") || infoDatiNOAzi.equals("true")) {%>
<input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>" />
<input type="hidden" name="prgAzienda" value="<%=prgAzienda%>" />
<input type="hidden" name="prgUnita" value="<%=prgUnita%>" />
<input type="hidden" name="FLGCFOK" value="<%=FLGCFOK%>" />
<input type="hidden" name="FLGDATIOK" value="<%=FLGDATIOK%>" />
<input type="hidden" name="IndirizzoAzienda" value="<%=IndirizzoAzienda%>" />
<input type="hidden" name="codFiscaleAzienda" value="<%=codFiscaleAzienda%>" />
<input type="hidden" name="codiceFiscaleLavoratore" value="<%=codiceFiscaleLavoratore%>" />
<input type="hidden" name="cognome" value="<%=cognome%>" />
<input type="hidden" name="nome" value="<%=nome%>" />
<input type="hidden" name="ragioneSociale" value="<%=ragioneSociale%>" />
<input type="hidden" name="pIva" value="<%=pIva%>" />
<%}%>


<input type="hidden" name="nuovo" value="true" />
<input type="hidden" name="salva" value="false" />

<af:list moduleName="M_GetListaNullaOsta"/>
	<center><input class="pulsante" type = "button" name="torna" value="Torna alla pagina di ricerca" onClick="tornaRicerca();"/></center>
&nbsp;&nbsp;&nbsp;

  	<%if(canNuovo) {%>












	<table class="main">  
		<tr>
			<td>
      			<center><input type="submit" class="pulsanti" name="inserisci" value="Nuovo Nulla Osta" /></center>
			</td>
		</tr>
	</table>
  	<%}%>


</af:form>


<br/>

<br/>
</body>
</html>