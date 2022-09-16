<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

        
<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.Linguette,
                  java.math.BigDecimal,
                  com.engiweb.framework.security.*,
                  it.eng.sil.util.InfCorrentiLav" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<% 

	String pageToProfile = "RicercaClassificazionePage";
	
	ProfileDataFilter filter = new ProfileDataFilter(user, pageToProfile);
	if (!filter.canView()) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}

	PageAttribs attributi = new PageAttribs(user, pageToProfile);
    String cdnFunzione = (String)serviceRequest.getAttribute("CDNFUNZIONE");
    
    String strnome = StringUtils.getAttributeStrNotNull(serviceRequest,"strnome");
    String prgTipoDominio  = StringUtils.getAttributeStrNotNull(serviceRequest,"prgTipoDominio");
    
    String codiceDato = "";
    if (StringUtils.isEmpty(prgTipoDominio)){
	    if(serviceResponse.containsAttribute("ComboPrgTipoDominio")){
	    	Vector listeDominioDati = serviceResponse.getAttributeAsVector("ComboPrgTipoDominio.ROWS.ROW");
	    	SourceBean dominioDato  = (SourceBean) listeDominioDati.elementAt(0);
	        codiceDato = (String) dominioDato.getAttribute("CODICE");
	        prgTipoDominio = codiceDato;
	    }
    }
    
    sessionContainer.delAttribute("CLASSIFICAZIONECACHE");

    String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);

%>


<html>
<head>
	<title>Ricerca Classificazione</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>  
<script type="text/javascript" src="../../js/tree.js"></script>
<script type="text/javascript" src="../../js/tree_codifiche.js"></script>
<script src="../../js/jqueryui/jquery-1.12.4.min.js"></script>
<script src="../../js/jqueryui/jquery-ui.min.js"></script>
<af:linkScript path="../../js/"/>
<script language="Javascript">
<%@ include file="../documenti/RicercaCheck.inc" %>



function checkCampiObbligatori(){
   return true;
}

function valorizzaHidden() {
  	document.form1.DESCDOMINIO.value = document.form1.prgTipoDominio.options[document.form1.prgTipoDominio.selectedIndex].text;
  	
  	return true;
}
  
</script>

<script type="text/javascript">

	function clearFields(){
		var strnome = eval('document.form1.strnome');
		strnome.value = '';
		
		var prgTipoDominio = eval('document.form1.prgTipoDominio');
		prgTipoDominio.selectedIndex =0;
		
		return;
	}
        
</script>
        
</head>

<body class="gestione" onload="doLoad();objLocalTree.toggleExpand('root_0');">

<br>
<p class="titolo">Ricerca Classificazione</p>
<p align="center">
<af:form name="form1" action="AdapterHTTP" method="POST" onSubmit="checkCampiObbligatori() && valorizzaHidden()">
<% out.print(htmlStreamTop); %> 
  <table>  
 
  <tr><td colspan="2"/>&nbsp;</td></tr>
  <tr>
    <td class="etichetta">Nome</td>
    <td class="campo"><af:textBox type="text" name="strnome"  value="<%=strnome%>"  size="40" maxlength="100"/></td>
  </tr>
  
  <tr style='background-color: #e8f3ff;'>
    <td class="etichetta">Dominio Dati</td>
    <td class="campo"><af:comboBox name="prgTipoDominio" moduleName="ComboPrgTipoDominio" selectedValue="<%=prgTipoDominio%>" required="true"  title="Dominio dati" /></td>
  </tr>
  
  <tr style='background-color: #e8f3ff;'><td colspan="2">&nbsp;&nbsp;</td></tr>
 
   <tr><td colspan="2">
 	<input type="hidden" name="DESCDOMINIO" value="" />
 	<input type="hidden" name="MODULE" value="MListaClassificazione">&nbsp;
 	
    <input  type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
    <input  type="hidden" name="PAGE" value="GestClassificazionePage">
  	&nbsp;</td>
 </tr>

  <tr>
    <td colspan="2" align="center">
 <input type="submit" class="pulsanti"  name = "Invia" value="Cerca" >
      &nbsp;&nbsp;
      <input name="reset" type="button" class="pulsanti" value="Annulla" onclick="javascript:clearFields();">
      &nbsp;&nbsp;      

    </td>
    </tr>
  </table>
  <%out.print(htmlStreamBottom);%> 
  </af:form>
</p>

</body>
</html>
