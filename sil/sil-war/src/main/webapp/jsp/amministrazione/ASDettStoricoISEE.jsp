<!-- @author: Giordano Gritti -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>


<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.util.*,
                  it.eng.sil.util.amministrazione.impatti.Controlli,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.security.ProfileDataFilter,                     
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean,
                  it.eng.sil.module.movimenti.InfoLavoratore,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%			
			String _page 			= 	(String) serviceRequest.getAttribute("PAGE");		
			String cdnFunzione 		= 	(String) serviceRequest.getAttribute("cdnFunzione");
			String cdnLavoratore 	= 	(String) serviceRequest.getAttribute("cdnLavoratore");
			String datfineval 		= 	"";
			String datinizioval  	= 	"";
			BigDecimal numvaloreisee= 	null;
			BigDecimal numanno		= 	null;
			BigDecimal puntValIsee	=	null;
			String strNota 			= 	"";	
			String strIbanNazione 	= 	"";
			String strIbanControllo = 	"";
			String strCinLav 		= 	"";
			String strAbiLav 		= 	"";
			String strCabLav 		= 	"";
			String strCCLav 		= 	"";			
			
			BigDecimal cdnUtIns 	= 	null;
			String dtmIns 			= 	"";
			BigDecimal cdnUtMod 	= 	null;
			String dtmMod 			= 	"";
			
			BigDecimal prgValore 	= null;
			Vector vett 			= serviceResponse.getAttributeAsVector("M_AS_GetDettStorISEE.ROWS.ROW");			
			SourceBean sbValIsee = (SourceBean)vett.get(0);	
						
			prgValore = (BigDecimal)sbValIsee.getAttribute("prgvaloreisee");
			datfineval = StringUtils.getAttributeStrNotNull(sbValIsee, "DATFINEVAL");				
			datinizioval 	=	StringUtils.getAttributeStrNotNull(sbValIsee, "DATINIZIOVAL");
			numvaloreisee	=	(BigDecimal)sbValIsee.getAttribute("NUMVALOREISEE");
			puntValIsee		= 	(BigDecimal)sbValIsee.getAttribute("NUMPUNTIISEE");
			numanno			=	(BigDecimal)sbValIsee.getAttribute("NUMANNO");
			strNota			=	StringUtils.getAttributeStrNotNull(sbValIsee, "STRNOTA");
			cdnUtIns 	= (BigDecimal) sbValIsee.getAttribute("CDNUTINS"); 
			dtmIns 		= StringUtils.getAttributeStrNotNull(sbValIsee, "DTMINS");
			cdnUtMod	= (BigDecimal) sbValIsee.getAttribute("CDNUTMOD");
			dtmMod 		= StringUtils.getAttributeStrNotNull(sbValIsee, "DTMMOD");
			strIbanNazione = StringUtils.getAttributeStrNotNull(sbValIsee, "STRIBANNAZIONE");
			strIbanControllo = StringUtils.getAttributeStrNotNull(sbValIsee, "STRIBANCONTROLLO");
			strCinLav = StringUtils.getAttributeStrNotNull(sbValIsee, "STRCINLAVORATORE");
			strAbiLav = StringUtils.getAttributeStrNotNull(sbValIsee, "STRABILAVORATORE");
			strCabLav = StringUtils.getAttributeStrNotNull(sbValIsee, "STRCABLAVORATORE");
			strCCLav = StringUtils.getAttributeStrNotNull(sbValIsee, "STRCCLAVORATORE");				
						
			String strPrgValoreIsee =	(prgValore == null)?"":prgValore.toString();
			String strNumAnno 		= 	(numanno == null)?"":numanno.toString();
			String strNumValoreIsee = 	(numvaloreisee == null)?"":numvaloreisee.toString();
			String strPuntValoreIsee= 	(puntValIsee == null)?"":puntValIsee.toString();
						
			
			
			//INFORMAZIONI OPERATORE
			Testata operatoreInfo 	= 	null;
			//LINGUETTE
			Linguette l 			= 	null;
			//INFORMAZIONI TESTATA LAVORATORE
			InfCorrentiLav testata 	= 	null;
			//Creo le inf. riassuntive del lavoratore
			testata 				= 	new InfCorrentiLav(RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
			testata.setPaginaLista(_page);
			//Info sul lavoratore
			InfoLavoratore _lav 	= 	new InfoLavoratore(new BigDecimal(cdnLavoratore));
			
			PageAttribs attributi 	= 	new PageAttribs(user, _page);		
			
			boolean canInsert 		= 	false;			
			canInsert 				=	attributi.containsButton("INSERISCI");			
			boolean updDate 		= 	true;
			boolean	readOnlyStr		=	true;
			
			operatoreInfo   = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);	
			
			String configIBAN = serviceResponse.containsAttribute("M_GetConfigIBAN.ROWS.ROW.NUM")?serviceResponse.getAttribute("M_GetConfigIBAN.ROWS.ROW.NUM").toString():"0";
			
			String htmlStreamTop 	= 	StyleUtils.roundTopTable(false);
			String htmlStreamBottom = 	StyleUtils.roundBottomTable(false);
%>
<html>
<head>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
<%@ include file="CommonScript.inc"%>

<af:linkScript path="../../js/" />
<title>Valore ISEE</title>

<script language="Javascript">
     
</script>
<script language="Javascript">
<%//Genera il Javascript che si occuperà di inserire i links nel footer
attributi.showHyperLinks(out, requestContainer, responseContainer, "cdnLavoratore=" + cdnLavoratore);%>
</script>

</head>
<body class="gestione" onload="rinfresca();">
<%if (testata != null)
	testata.show(out);
if (l != null) {
	if (l.getSize() > 0) {
		l.show(out);
	} else {%>
<p class="titolo">Valore Isee</p>
<%}
}
%>
<br>
<p class="titolo">Dettaglio storico dei Valori ISEE</p>
<%out.print(htmlStreamTop);%> 


<%@ include file="ASValIsee.inc" %> 

<table>
	<tr>
		<td>
			<input class="pulsante" type="button" name="lista" value="Torna alla lista"
       		onClick="checkChange('ASStoricoIseePage','&cdnLavoratore=<%=cdnLavoratore%>')"/>
       	</td>
    </tr>	 
</table>

<%out.print(htmlStreamBottom);%>
	<center>
  		<table>
  			<tr>
  				<td align="center">
	<%  operatoreInfo.showHTML(out);%>
  				</td>
  			</tr>
  		</table>
  	</center>
</body>
</html>