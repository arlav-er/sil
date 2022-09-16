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
    PageAttribs attributi = new PageAttribs(user, "RicercaServiziPage");

    String cdnFunzione = (String)serviceRequest.getAttribute("CDNFUNZIONE");
    String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");
    
    String codServizio = StringUtils.getAttributeStrNotNull(serviceRequest,"codServizio");
    String servizio = StringUtils.getAttributeStrNotNull(serviceRequest,"servizio");
    String codArea  = StringUtils.getAttributeStrNotNull(serviceRequest,"codArea");
    String validi   = StringUtils.getAttributeStrNotNull(serviceRequest,"validi");

    //fs20141007 - start
    String tipoAttivita   = StringUtils.getAttributeStrNotNull(serviceRequest,"tipoAttivita");
    String prestazione   = StringUtils.getAttributeStrNotNull(serviceRequest,"prestazione");
    String polAttiva   = StringUtils.getAttributeStrNotNull(serviceRequest,"polAttiva");
    //fs20141007 - end

    sessionContainer.delAttribute("SERVIZIOCACHE");

    String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);
	
    String labelServizio ="Servizio";
    String labelServizi ="Servizi";
    String umbriaGestAz = "0";
    if(serviceResponse.containsAttribute("M_CONFIG_UMB_NGE_AZ")){
    	umbriaGestAz = Utils.notNull(serviceResponse.getAttribute("M_CONFIG_UMB_NGE_AZ.ROWS.ROW.NUM"));
    }
    if(umbriaGestAz.equals("1")){
    	labelServizio = "Area";
    	labelServizi = "Aree";
    }
    

%>


<html>
<head>
	<title>Ricerca <%=labelServizi %></title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>  
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

function checkCampiObbligatori()
{
   return true;
}


  function valorizzaHidden() {
	  	document.form1.DESCAREA.value = document.form1.codArea.options[document.form1.codArea.selectedIndex].text;

	  	//fs20140710 - start
	  	document.form1.DESCTIPOATTIVITA.value = document.form1.tipoAttivita.options[document.form1.tipoAttivita.selectedIndex].text;
	  	document.form1.DESCPRESTAZIONE.value = document.form1.prestazione.options[document.form1.prestazione.selectedIndex].text;

	  	document.form1.PRGTIPOATTIVITARICERCA.value = document.form1.tipoAttivita.selectedIndex;
	  	document.form1.PRGPRESTAZIONERICERCA.value = document.form1.prestazione.selectedIndex;
	  	//fs20140710 - end
	  	
	  	return true;
  }
  
</script>
</head>

<body class="gestione" onload="rinfresca();">

<br>
<p class="titolo">Ricerca <%=labelServizi %></p>
<p align="center">
<af:form name="form1" action="AdapterHTTP" method="POST" onSubmit="checkCampiObbligatori() && valorizzaHidden()">
<% out.print(htmlStreamTop); %> 
  <table>  
 
  <tr><td colspan="2"/>&nbsp;</td></tr>
  <tr>
    <td class="etichetta">Codice <%=labelServizio %></td>
    <td class="campo"><af:textBox type="text" name="CODSERVIZIO"  value="<%=codServizio%>"  size="10" maxlength="8"/></td>
  </tr>
  <tr>
    <td class="etichetta"><%=labelServizio %></td>
    <td class="campo"><af:textBox type="text" name="SERVIZIO"  value="<%=servizio%>"  size="40" maxlength="100"/></td>
  </tr>
  <tr>
    <td class="etichetta">Area Appartenenza</td>
    <td class="campo"><af:comboBox name="codArea" moduleName="ComboAreaServizio" selectedValue="<%=codArea%>"  addBlank="true"/></td>
  </tr>
  <tr>
    <td class="etichetta">Solo codifiche valide</td>
		<td class="campo">
			<input type="checkbox" name="VALIDI" <%=(validi!=null&&validi.equals("on"))?"checked='checked'":""%> />
		</td>	
  </tr>
  
  <tr>
    <td class="etichetta">Tipo attivit&agrave;</td>
    <td class="campo"><af:comboBox name="tipoAttivita" moduleName="ComboTipoAttivita" selectedValue="<%=tipoAttivita%>"  addBlank="true"/></td>
  </tr>
  
  <tr>
    <td class="etichetta">Prestazione</td>
    <td class="campo"><af:comboBox name="prestazione" moduleName="ComboPrestazione" selectedValue="<%=prestazione%>"  addBlank="true"/></td>
  </tr>

  <tr>
    <td class="etichetta">Flag Pol. Attiva</td>
		<td class="campo">
			<input type="checkbox" name="POLATTIVA" <%=(polAttiva!=null&&polAttiva.equals("on"))?"checked='checked'":""%> />
		</td>	
  </tr>
  
  <tr><td colspan="2"><hr width="90%"/></td></tr>
 
  <tr><td colspan="2">
 	<input  type="hidden" name="DESCAREA" value=""/>
 	<!-- FS20141007 - START -->
 	<input  type="hidden" name="DESCTIPOATTIVITA" value=""/>
 	<input  type="hidden" name="DESCPRESTAZIONE" value=""/>
 	<input  type="hidden" name="PRGTIPOATTIVITARICERCA" value=""/>
 	<input  type="hidden" name="PRGPRESTAZIONERICERCA" value=""/>

 	<!-- FS20141007 - END -->            
    <input  type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
    <input  type="hidden" name="PAGE" value="GestServiziPage">
  	&nbsp;</td>
 </tr>
  <tr>
    <td colspan="2" align="center">
 <input type="submit" class="pulsanti"  name = "Invia" value="Cerca" >
      &nbsp;&nbsp;
      <input name="reset" type="reset" class="pulsanti" value="Annulla">
      &nbsp;&nbsp;      

    </td>
    </tr>
  </table>
  <%out.print(htmlStreamBottom);%> 
  </af:form>
</p>

</body>
</html>
