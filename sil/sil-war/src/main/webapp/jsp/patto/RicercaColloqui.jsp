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
    //per gestire il pulsante di ritorna 16/06/2004 in basso a sinistra
    PageAttribs attributi = new PageAttribs(user, "RicercaColloquiPage");

    String cdnFunzione = (String)serviceRequest.getAttribute("CDNFUNZIONE");
    String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");    

    String canCIG = (String) serviceResponse.getAttribute("M_Configurazione_CIG.ROWS.ROW.canCIG");

    String labelServizio = "Programma";
    
    String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);

%>


<html>
<head>
	<title>Ricerca Programmi</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>  
<link rel="stylesheet" type="text/css" href="../../css/jqueryui/jquery.selectBoxIt.css">
<link rel="stylesheet" type="text/css" href="../../css/jqueryui/jqueryui-sil.css">
<af:linkScript path="../../js/"/>
<script language="Javascript">
<%@ include file="../documenti/RicercaCheck.inc" %>

  function go(url, alertFlag) {
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;
  
  var _url = "AdapterHTTP?" + url;
  if (alertFlag == 'TRUE' ) {
    if (confirm('Confermi operazione')) {
      setWindowLocation(_url);
    }
  }
  else {
    setWindowLocation(_url);
  }
}

</script>
<script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer (per il ritorna 16/06/2004)
   attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore);  
      %>
</script>
<SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>
  <script src="../../js/jqueryui/jquery-1.12.4.min.js"></script>
  <script src="../../js/jqueryui/jquery-ui.min.js"></script>
  <script src="../../js/jqueryui/jquery.selectBoxIt.min.js"></script>	
  <script type="text/javascript">
    $(function() {
    	$("[name='codServizio']").selectBoxIt({
            theme: "default",
            autoWidth: false
        });
       
    });
    </script>
<script language="Javascript">
function checkCampiObbligatori()
{
  <%
    if (cdnLavoratore!=null) {
  		out.print( "return true" );
  	}
  %>
 
  if ((document.form1.dataInizioDa.value != "") || (document.form1.dataInizioA.value != "")) 
       return true;
    alert("E' necessario inserire la data inizio ('da' o 'a')");
    return false;
}

  function controlloDate(){
    var objDataDa = document.form1.dataInizioDa;
    var objDataA = document.form1.dataInizioA;    
	if ((objDataDa.value != "") && (objDataA.value != "")) {
      if (compareDate(objDataDa.value,objDataA.value) > 0) {
      	alert(objDataDa.title + " maggiore di " + objDataA.title);
      	objDataDa.focus();
	    return false;
	  }	
	}
	 var objDataFDa = document.form1.dataFineDa;
	    var objDataFA = document.form1.dataFineA;    
		if ((objDataFDa.value != "") && (objDataFA.value != "")) {
	      if (compareDate(objDataFDa.value,objDataFA.value) > 0) {
	      	alert(objDataFDa.title + " maggiore di " + objDataFA.title);
	      	objDataFDa.focus();
		    return false;
		  }	
		}
    return true;
  }

  function valorizzaHidden() {
  	document.form1.descCodServizio_H.value = document.form1.codServizio.options[document.form1.codServizio.selectedIndex].text;  	
  	document.form1.descCPI_H.value = document.form1.codCPI.options[document.form1.codCPI.selectedIndex].text;
  	return true;
  }
  
</script>
</head>

<body class="gestione" onload="rinfresca();">
<% if (cdnLavoratore != null) { 
        InfCorrentiLav _testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
        _testata.show(out);
        String _page = (String)serviceRequest.getAttribute("PAGE");
        Linguette _linguetta = new Linguette(user, Integer.parseInt(cdnFunzione), "LISTACOLLOQUIPAGE", new BigDecimal(cdnLavoratore)); 
        _linguetta.show(out);
   } %>
<br>
<p class="titolo">Ricerca Programmi</p>
<p align="center">
<af:form name="form1" action="AdapterHTTP" method="POST" onSubmit="checkCampiObbligatori() && controlloDate() && valorizzaHidden()">
<% out.print(htmlStreamTop); %> 
  <table>  
  <%--  nel caso in cui la pagina sia stata richesta nell' ambito di un lavoratore allora nella maschera di ricerca non dovra' 
        comparire la sezione relativa ai dati del lavoratore e dovra' comparire in alto la sezione delle inf correnti --%>
<% if (cdnLavoratore==null)  { %>

    <input type="hidden" name="ricerca_generale" value="true">


  <tr><td colspan="2"/>&nbsp;</td></tr>
  <tr>
    <td class="etichetta">Codice Fiscale</td>
    <td class="campo"><af:textBox type="text" name="CF" value="" size="20" maxlength="16"/></td>
  </tr>
  <tr>
    <td class="etichetta">Cognome</td>
    <td><af:textBox type="text" name="COGNOME" value="" size="20" maxlength="50" /></td>
  </tr>
  <tr>
    <td class="etichetta">Nome</td>
    <td class="campo"><af:textBox type="text" name="NOME" value="" size="20" maxlength="50" /></td>
  </tr>
  <tr>
    <td class="etichetta">tipo ricerca</td>
    <td class="campo">
    <table colspacing="0" colpadding="0" border="0">
    <tr>
     <td><input type="radio" name="tipoRicerca" value="esatta" CHECKED/> esatta&nbsp;&nbsp;&nbsp;&nbsp;</td>
     <td><input type="radio" name="tipoRicerca" value="iniziaPer"/> inizia per</td>
    </tr>
    </table>
    </td>
  </tr>
  
  <tr><td colspan="2"><hr width="90%"/></td></tr>
<%  } else { // passo il cdnLavoratore alla query dinamica %>
    <tr><td colspan="2"><input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"></td></tr>
<%  } %>
  <tr>
    <td class="etichetta">Programmi aperti</td>
    <td class="campo"><af:comboBox name="progrAperti" moduleName="M_ComboSiNo" addBlank="true"/></td>
  </tr>
 
  <tr>
    <td class="etichetta"><%=labelServizio %></td>
    <td class="campo"><af:comboBox name="codServizio" moduleName="M_DE_LIST_SERVIZI" addBlank="true"/></td>
  </tr>

   <tr>
    <td class="etichetta">Data Inizio da</td>
    <td ><af:textBox type="date" name="dataInizioDa" title="Data Inizio da" value="" size="12" maxlength="10"  validateOnPost="true"/>
    &nbsp;&nbsp;&nbsp;&nbsp;a &nbsp;&nbsp;<af:textBox type="date" name="dataInizioA" value="" size="12" maxlength="10" title="Data Inizio a" validateOnPost="true" />
    </td>
  </tr>
  <tr>
    <td class="etichetta">Data Fine da</td>
    <td ><af:textBox type="date" name="dataFineDa" title="Data Fine da" value="" size="12" maxlength="10"  validateOnPost="true"/>
        &nbsp;&nbsp;&nbsp;&nbsp;a &nbsp;&nbsp;<af:textBox type="date" name="dataFineA" value="" size="12" maxlength="10" title="Data Fine a" validateOnPost="true" />
    </td>
  </tr>
  <%if (canCIG.equals("true")) {%>
	<tr><td colspan="2"><hr width="90%"/></td></tr>
	<tr>
		<td class="etichetta">Legato ad una <br>iscrizione CIG</td>
		<td class="campo">
			<input type="checkbox" name="conCig" />
		</td>				    
	</tr>
  <%} %>
  	<tr><td colspan="2"><hr width="90%"/></td></tr>
	<tr>
		<td class="etichetta">Sottoposti a condizionalità</td>
		<td class="campo">
			<input type="checkbox" name="progCond" />
		</td>				    
	</tr>
	<tr>
		<td class="etichetta">Attività con esiti negativi</td>
		<td class="campo">
			<input type="checkbox" name="azioniNegativo" />
		</td>				    
	</tr>
	<tr>
		<td class="etichetta">Con eventi di condizionalità</td>
		<td class="campo">
			<input type="checkbox" name="azioniCond" />
		</td>				    
	</tr>
    <tr><td colspan="2"><hr width="90%"/></td></tr>
  <tr>
    <td class="etichetta">Centro per l'Impiego</td>
    <% String requiredCpi="false";
       if (cdnLavoratore==null)  {
       	 requiredCpi = "true";
       } %>
    <td class="campo"><af:comboBox name="codCPI" title="Centro per l'Impiego" addBlank="true" moduleName="M_ELENCOCPI" selectedValue="" required="<%=requiredCpi%>"/></td>
	</tr>
   
    </table>
    </td>
  </tr>
  <tr><td colspan="2">
	<input  type="hidden" name="descCodServizio_H" value=""/>
	<input  type="hidden" name="descCPI_H" value=""/>            
    <input  type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
    <input  type="hidden" name="PAGE" value="ListaColloquiPage">
  	&nbsp;</td>
 </tr>
  <tr>
    <td colspan="2" align="center">
 <input type="submit" class="pulsanti"  name = "Invia" value="Cerca" >
      &nbsp;&nbsp;
      <input name="reset" type="reset" class="pulsanti" value="Annulla">
      &nbsp;&nbsp;      
  <% if (cdnLavoratore != null && !cdnLavoratore.equalsIgnoreCase("")) { %>
      <input type="button" onclick="javascript:go('PAGE=ListaColloquiPage&cdnFunzione=<%=cdnFunzione%>&cdnLavoratore=<%=cdnLavoratore%>', 'FALSE');" value = "Torna alla lista" class="pulsanti">
      <br/><br/>
      <input type="button" class="pulsanti"  name = "inserisciNuovo" value="nuovo programma" onclick= "go('PAGE=COLLOQUIOPAGE&cdnFunzione=<%=cdnFunzione%>&cdnLavoratore=<%=cdnLavoratore%>&inserisciNuovo=1&data_cod=&prgspi=', 'FALSE')">
    <% } %>
    </td>
    </tr>
  </table>
  <%out.print(htmlStreamBottom);%> 
  </af:form>
</p>
<table>
<tr><td class="campo2">N.B.: Se il campo "Programmi aperti" é nullo vengono filtrati sia i programmi aperti che quelli chiusi.</td></tr>
</table>
</body>
</html>
