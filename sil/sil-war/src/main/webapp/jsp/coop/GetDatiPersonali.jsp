<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
   
  it.eng.sil.util.*, 
  it.eng.afExt.utils.StringUtils,
  java.lang.*,
  java.text.*, 
  java.util.*,
  java.math.*,
  it.eng.sil.security.*
"%>

<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

	String strCodiceFiscale="";
	String strCognome="";
	String strNome="";
	String strSesso="";
	String datNasc="";
	String strComNas="";
	String strNazione="";
	String strNazione2="";
	String flgMilite="";
	
	
	String strIndirizzores="";
	String strLocalitares="";
	String strComRes="";
	String strCapRes="";
	String strIndirizzodom="";
	String strLocalitadom="";
	String strComdom="";
	String strCapDom="";

	SourceBean datiPersonali = (SourceBean) serviceResponse.getAttribute("M_COOP_GETDATIPERSONALI.ROWS.ROW");
	if (datiPersonali!=null) {
		strCodiceFiscale=StringUtils.getAttributeStrNotNull(datiPersonali, "strCodiceFiscale");
		strCognome=StringUtils.getAttributeStrNotNull(datiPersonali, "strCognome");
		strNome=StringUtils.getAttributeStrNotNull(datiPersonali, "strNome");	
		strSesso=StringUtils.getAttributeStrNotNull(datiPersonali, "strSesso");		
		datNasc=StringUtils.getAttributeStrNotNull(datiPersonali, "dataNascita");		
		strComNas=StringUtils.getAttributeStrNotNull(datiPersonali, "comNas");
		strNazione=StringUtils.getAttributeStrNotNull(datiPersonali, "strNazione");
		strNazione2=StringUtils.getAttributeStrNotNull(datiPersonali, "strNazione2");
		flgMilite=StringUtils.getAttributeStrNotNull(datiPersonali, "flgMilite");							
		
		strIndirizzores=StringUtils.getAttributeStrNotNull(datiPersonali, "strIndirizzoRes");							
		strLocalitares=StringUtils.getAttributeStrNotNull(datiPersonali, "strLocalitaRes");							
		strComRes=StringUtils.getAttributeStrNotNull(datiPersonali, "strComRes");							
		strCapRes=StringUtils.getAttributeStrNotNull(datiPersonali, "strCapRes");							
		
		strIndirizzodom=StringUtils.getAttributeStrNotNull(datiPersonali, "strIndirizzoDom");							
		strLocalitadom=StringUtils.getAttributeStrNotNull(datiPersonali, "strLocalitadom");							
		strComdom=StringUtils.getAttributeStrNotNull(datiPersonali, "strComDom");							
		strCapDom=StringUtils.getAttributeStrNotNull(datiPersonali, "strCapDom");							
	}



   //PageAttribs attributi = new PageAttribs(user, "CoopDatiPersonaliPage");
   //int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));

  // NOTE: Attributi della pagina (pulsanti e link) 
  //boolean canModify = attributi.containsButton("aggiorna");
  boolean canModify=false;


%>
<html>

<head>
  <title>Dati Personali</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
    <af:linkScript path="../../js/"/>
   
<SCRIPT TYPE="text/javascript">
var flagChanged = false;  
function fieldChanged() {
  if (<%=canModify%>) {
    flagChanged = true;
  }
}




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




</SCRIPT>
<script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        //attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
</script>
</head>

<body class="gestione">

<p class="titolo">Dati personali</p>

<p>
  <font color="green"><af:showMessages prefix="M_COOP_GETDATIPERSONALI" /></font>
  <font color="red"><af:showErrors /></font>
</p>

<af:form name="Frm1" method="POST" action="AdapterHTTP" >
<%//out.print(htmlStreamTop);%>
<table  class="main">
<tr>
	<td class="etichetta">
	Dati provenienti dalla provincia di:</td>
	<td class="campo"> <B><%=serviceRequest.getAttribute("provMaster")%></B>
	</td>
</tr>
<tr>
	<td colspan="2"></td>
</tr>
<tr>
    <td class="etichetta">Codice Fiscale&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" title="Codice fiscale" required="false" type="text" name="strCodiceFiscale" value="<%=strCodiceFiscale%>" size="21" maxlength="16" validateWithFunction="checkCF" readonly="true" />&nbsp;
    </td>
</tr>
<tr>
    <td class="etichetta">Cognome&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" title="Cognome" required="false" type="text" name="strCognome" value="<%=strCognome%>" size="30" maxlength="30" readonly="true" /></td>
</tr>
<tr>
    <td class="etichetta">Nome&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" title="Nome" required="false" type="text" name="strNome" value="<%=strNome%>" size="30" maxlength="30" readonly="true" /></td>
</tr>
<tr>
    <td class="etichetta">Sesso&nbsp;</td>
    <td class="campo"><af:comboBox classNameBase="input" name="strSesso" required="false" title="sesso" onChange="fieldChanged();" disabled="true" >
          <OPTION value="M" <%if (strSesso.equals("M")) out.print("SELECTED=\"true\"");%>>M</OPTION>
          <OPTION value="F" <%if (strSesso.equals("F")) out.print("SELECTED=\"true\"");%>>F</OPTION>
          <!--<OPTION value="N" >Non so</OPTION>-->
        </af:comboBox>
    </td>
</tr>



<tr>
    <td class="etichetta">Data di Nascita&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" title="Data di nascita" type="date" required="false" name="datNasc" value="<%=datNasc%>" size="11" maxlength="12" validateOnPost="true" validateWithFunction="checkAnnoNas" readonly="true" /></td>
</tr>
<tr>
    <td class="etichetta">Comune di nascita&nbsp;</td>
    <td class="campo">
    <af:textBox type="text" classNameBase="input" onKeyUp="fieldChanged();" 
                name="strComNas"  value="<%=strComNas%>" size="30" maxlength="50"
                readonly="true" title="comune di nascita" />&nbsp;
    </td>
</tr>
<tr>
    <td class="etichetta">Nazione di cittadinanza&nbsp;</td>
    <td class="campo">
        <af:textBox classNameBase="input" onKeyUp="fieldChanged();" title="Cittadinanza" type="text" name="strNazione" value="<%=strNazione%>" 
        size="30" maxlength="40"  required="false" readonly="true" />
    </td>
</tr>
<tr>
    <td class="etichetta">Seconda Nazione di cittadinanza&nbsp;</td>
    <td class="campo">
        <af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="strNazione2" value="<%=strNazione2%>" size="30" maxlength="40"
         required="false" readonly="true" />
    </td>
</tr>

<tr>
   <td class="etichetta">Milite esente/assolto</td>
    <td class="campo">
      <af:comboBox 
        name="flgMilite"
        classNameBase="input"
        disabled="true"
        onChange="fieldChanged()">
        <option value=""  <% if ( "".equalsIgnoreCase(flgMilite) )  { %>SELECTED="true"<% } %> ></option>
        <option value="S" <% if ( "S".equalsIgnoreCase(flgMilite) ) { %>SELECTED="true"<% } %> >Sì</option>
        <option value="N" <% if ( "N".equalsIgnoreCase(flgMilite) ) { %>SELECTED="true"<% } %> >No</option>
      </af:comboBox>
    </td>
</tr>



<tr>
<td colspan="2" class="titolo"><center><b>Residenza</b></center></td>
</tr>
<tr>
    <td class="etichetta">Indirizzo&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="strIndirizzores" value="<%=strIndirizzores%>" size="50" maxlength="50" required="false" readonly="true" /><br/></td>
</tr>
<tr>
    <td class="etichetta">Località&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="strLocalitares" value="<%=strLocalitares%>" size="30" maxlength="30" readonly="true" /></td>
</tr>

<tr>
    <td class="etichetta">Comune&nbsp;</td>
	<td class="campo">
    <af:textBox classNameBase="input" onKeyUp="fieldChanged();"
                type="text" name="strComRes" value="<%=strComRes%>" size="30" maxlength="50" required="false" title="comune di residenza"
                readonly="true"/>
    </td>
</tr>
<tr>
    <td class="etichetta">Cap&nbsp;</td>
    <td class="campo">
      <af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" title="Cap di residenza" name="strCapRes" value="<%=strCapRes%>" size="5" maxlength="5" readonly="true" />
    </td>
</tr>


<tr>
<td colspan="2" class="titolo"><br/><center><b>Domicilio</b></center></td>
</tr>
<tr>
    <td class="etichetta">Indirizzo&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="strIndirizzodom" value="<%=strIndirizzodom%>" size="30" maxlength="30" required="false" readonly="true" /></td>
</tr>
<tr>
    <td class="etichetta">Località&nbsp;</td>
    <td class="campo"><af:textBox classNameBase="input" onKeyUp="fieldChanged();" type="text" name="strLocalitadom" value="<%=strLocalitadom%>" size="30" maxlength="30" readonly="true" /></td>
</tr>
<tr>
    <td class="etichetta">Comune&nbsp;</td>
    <td class="campo">
        <af:textBox classNameBase="input" type="text"  name="strComdom" value="<%=strComdom%>"
            onKeyUp="fieldChanged();"  
            size="30" maxlength="50" required="false" title="comune domicilio" readonly="true"
        />&nbsp;
</tr>
<tr>
    <td class="etichetta">Cap&nbsp;</td>
    <td class="campo">
      <af:textBox classNameBase="input" name="strCapDom" value="<%=strCapDom%>" 
            onKeyUp="fieldChanged();"
            title="Cap del domicilio" type="text"  size="5" maxlength="5" readonly="true"
        />
    </td>
</tr>
</table>
<%//out.print(htmlStreamBottom);%>
</p>
<center>
<table>
  <tr>
  <td align="center">
	  <input class="pulsante" type="submit" name="scaricaScheda" value="Scarica scheda">&nbsp;&nbsp;
	  <input class="pulsante" type="submit" name="trasferimento" value="Richiesta di trasferimento">
   </td>
  </tr>
  <tr>
  <td align="center"><br/>
   <input class="pulsante" type="button" name="bIndietro" value=" << Indietro" onClick="javascript:history.back();">
  </td>
  </tr>
</table>
</center>




</af:form>     
<br>&nbsp;
<!--p align="center">
<%//if (!flag_insert) operatoreInfo.showHTML(out);%>
</p-->


</body>

</html>