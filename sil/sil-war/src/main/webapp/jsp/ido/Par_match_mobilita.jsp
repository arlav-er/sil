<!-- @author: Stefania Orioli -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.base.SourceBean,
                 
                com.engiweb.framework.security.*,
                it.eng.afExt.utils.*, java.math.*,
                java.lang.*,java.text.*,java.util.*, it.eng.sil.security.*,
                it.eng.sil.util.*"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
String prgRichiestaAz = serviceRequest.getAttribute("PRGRICHIESTAAZ").toString();
String prgOrig = serviceRequest.getAttribute("PRGRICHIESTAAZ").toString();
String prgC1 = serviceRequest.getAttribute("C1").toString();
String prgAzienda = serviceRequest.getAttribute("PRGAZIENDA").toString();
String prgUnita = serviceRequest.getAttribute("PRGUNITA").toString();

String _page = serviceRequest.getAttribute("PAGE").toString();
PageAttribs attributi = new PageAttribs(user, _page);

String p_codCpi = user.getCodRif();
int nroMansioni = 0;

int nroAlternative = 0;
SourceBean content = (SourceBean) serviceResponse.getAttribute("MALTERNATIVEINCROCIOMOBILITA");
Vector altIncr = content.getAttributeAsVector("ROWS.ROW");
nroAlternative = altIncr.size();

int cdnUtente = user.getCodut();
String _cdnFunzione = serviceRequest.getAttribute("CDNFUNZIONE").toString();

//SourceBean contStato = (SourceBean) serviceResponse.getAttribute("MATCHSTATORICHORIG");
SourceBean sbStato = (SourceBean) serviceResponse.getAttribute("MATCHSTATORICHORIG.ROWS.ROW");
String cdnStatoRich = StringUtils.getAttributeStrNotNull(sbStato, "CDNSTATORICH");
String codcpi = StringUtils.getAttributeStrNotNull(sbStato, "CODCPI");

SourceBean checkIcrNoMansione = (SourceBean) serviceResponse.getAttribute("MatchReportIncrocioMobilita.ROWS.ROW");
BigDecimal numIncMob = (BigDecimal)checkIcrNoMansione.getAttribute("numIncMob");
boolean disableCheck = false;
if (numIncMob.intValue() > 0) {
	disableCheck = true;
}

SourceBean checkIcrNoMansioneXCPI = (SourceBean) serviceResponse.getAttribute("MatchReportIncrocioMobilitaXCPI.ROWS.ROW");
BigDecimal numIncMobXCPI = (BigDecimal)checkIcrNoMansioneXCPI.getAttribute("numIncMob");
boolean disableCheckXCPI = false;
if (numIncMobXCPI.intValue() > 0) {
	disableCheckXCPI = true;
}

boolean disableCheck_cpi = false;
if (("").equalsIgnoreCase(codcpi)) {
	disableCheck_cpi = true;
}

// Attributi della pagina GestIncrocioPage
PageAttribs attrIncrocio = new PageAttribs(user, "GestIncrocioPage");
boolean gestCopia = false;//attrIncrocio.containsButton("GEST_COPIA");

boolean viewPar = false;
String prgTipoIncrocio = "";
%>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <title>Matching Mobilità - Impostazione parametri</title>
  <af:linkScript path="../../js/" />
  <script language="Javascript" src="../../js/utili.js" type="text/javascript"></script>
  <script language="Javascript">
    window.top.menu.caricaMenuAzienda(<%=_cdnFunzione%>,<%=prgAzienda%>, <%=prgUnita%>);
    
    function checkProfilo() {
    	var check = true;
    	//check = controllaFunzTL();
    	if (check) {    		
    		if (document.form_match.flagNoMansione.checked) {
    			document.form_match.PRGALTERNATIVA.value = "";
    		}
    		else if (document.form_match.flagNoMansioneXCpi.checked) {
    			document.form_match.PRGALTERNATIVA.value = "";
    		}
    		else {
     			valueAlt = document.form_match.PRGALTERNATIVA.value;
    			if (valueAlt == null || valueAlt == "") {
    			    check = false;
    				alert("Selezionare un profilo per eseguire il Matching!");
    			}
    		}
    	}
    	
    	return check;
    }
    
    function match_sub(n)
    {
      // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;	  	  
	   
      var pag = "";
      var ok = false;
      var checkFunz = true;
      switch(n) {
        case 1 :        	
          pag = "EseguiMatchMobilitaPage";
          checkFunz = controllaFunzTL(); 
          ok = true;
  
            valueAlt = document.form_match.PRGALTERNATIVA.value;
          	if (document.form_match.flagNoMansione.checked) {          	    
    			if (valueAlt != "") {
    			    ok = false;
    				alert("Selezionare una sola tipologia di Matching!");
    			}
    	  		document.form_match.PRGALTERNATIVA.value = "";
    	  	}
    	  	else if (document.form_match.flagNoMansioneXCpi.checked) {          	    
    			if (valueAlt != "") {
    			    ok = false;
    				alert("Selezionare una sola tipologia di Matching!");
    			}
    	  		document.form_match.PRGALTERNATIVA.value = "";
    	  	}
    	  	else {
    			if (valueAlt == null || valueAlt == "") {
    			    ok = false;
    				alert("Selezionare un profilo per eseguire il Matching!");
    			}
    	  	}         
          
          break;
        case 2 :
          pag = "GestIncrocioPage";
          mod = "";
          ok = true;
          break;
        default :
          pag = "";
          ok = false;
          break;
        }
      if(checkFunz && riportaControlloUtente( ok ) ) {      
        document.form_match.PAGE.value = pag; 
        doFormSubmit(document.form_match);  
      } 
    }    
    
  </script>
</head>

<body class="gestione" onload="rinfresca()">
<%@ include file="InfoCorrRichiesta.inc" %>
<h2>Matching Mobilità - Impostazione dei Parametri</h2>
<af:form name="form_match" action="AdapterHTTP" method="POST" >
<input name="PAGE" type="hidden" value="MatchStoricizzaRichPage"/>
<input name="PRGRICHIESTAAZ" type="hidden" value="<%=prgRichiestaAz%>"/>
<input name="C1" type="hidden" value="<%=prgC1%>"/>
<input name="PRGORIG" type="hidden" value="<%=prgOrig%>"/>
<input name="PRGAZIENDA" type="hidden" value="<%=prgAzienda%>"/>
<input name="PRGUNITA" type="hidden" value="<%=prgUnita%>"/>
<input name="CDNFUNZIONE" type="hidden" value="<%=_cdnFunzione%>"/>
<input name="CERCA" type="hidden" value="cerca"/>
<input type="hidden" name="P_CODCPI" value="<%=p_codCpi%>"/>
<input type="hidden" name="P_CDNUTENTE" value="<%=cdnUtente%>"/>
<input type="hidden" name="EM" value="9"/>
<input type="hidden" name="db" value="3"/>
<%
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<%out.print(htmlStreamTop);%>
<table class="main">
<tr><td colspan="2">&nbsp;</td></tr>
<tr>
  <td colspan="2"><div class="sezione2">Incrocio per profilo</div></td>
</tr>
<%
	if(nroAlternative>0) {
%>
	<tr>
	    <td class="etichetta">Profili:</td>
	    <td class="campo">
	    	<af:comboBox name="PRGALTERNATIVA" size="1" title="Scelta Profili"
	                       multiple="false" disabled="false"
	                       focusOn="false" moduleName="MALTERNATIVEINCROCIOMOBILITA"
	                       selectedValue="" addBlank="true" blankValue=""/> 
	    </td>
	</tr>
	
<%
	}
	else {
%>
		<input type="hidden" name="PRGALTERNATIVA" value=""/>
      	<tr>
      		<td colspan="2" align="left">Nessun profilo</td>
      	</tr>
<%
	}
%>

<tr><td colspan="2">&nbsp;</td></tr>
<tr>
  <td colspan="2"><div class="sezione2">Incrocio slegato dalla mansione</div></td>
</tr>
<tr>
	<td colspan="2" align="left">
		<table width="400">			
			<tr>
			  <td class="etichetta" nowrap="nowrap">Per Provincia</td>
			  <td class="campo"><input type="CHECKBOX" name="flagNoMansione" value="1-0" <%= (disableCheck) ? "disabled='disable'" : "" %>></td>
			  <td colspan="2"></td>	  
			</tr>
			
			<tr>
			  <td class="etichetta" nowrap="nowrap">Per CPI</td>
			  <td class="campo"><input type="CHECKBOX" name="flagNoMansioneXCpi" value="1-1" <%= (disableCheckXCPI) ? "disabled='disable'" : "" %>></td>
			  <td colspan="2"></td>	  
			</tr>
			
		</table>
	</td>
</tr>

<tr><td colspan="2">&nbsp;</td></tr>
<tr>
  <td colspan="2" align="center">  
<%
	if (!disableCheck || !disableCheckXCPI || nroAlternative>0) {
  		if(!cdnStatoRich.equals("4") && !cdnStatoRich.equals("5")) {
%>
  			<input class="pulsanti" type="button" name="sub" value="Esegui Matching" onClick="match_sub(1)" />
  			&nbsp;&nbsp;
<%
		}
	}
%>
  <input class="pulsanti" type="button" name="back" value="Chiudi" onClick="match_sub(2)" />
  </td>
</tr>
<tr><td colspan="2">&nbsp;</td></tr>
</table>
<%out.print(htmlStreamBottom);%>

</af:form>
  
</body>
</html>
