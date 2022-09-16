<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ page import="
  com.engiweb.framework.base.*,
  java.lang.*,
  java.text.*,
  java.util.*, 
  it.eng.sil.util.*,
  it.eng.afExt.utils.StringUtils,
  it.eng.sil.security.*" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
String strTitolo = "";
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
int _funzione=Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
String strScadenziario = (String) serviceRequest.getAttribute("SCADENZIARIO");
int nCodScadenza = 0;
if (strScadenziario.compareTo("ORG1") == 0) {
  nCodScadenza = 1;
}
else {
  if (strScadenziario.compareTo("ORG2") == 0) {
    nCodScadenza = 2;
  }
  else {
    if (strScadenziario.compareTo("ORG3") == 0) {
      nCodScadenza = 3;
    }
    else {  
      if (strScadenziario.compareTo("ORG4") == 0) {
        nCodScadenza = 4;
      }
      else {
        if (strScadenziario.compareTo("AMM1") == 0) {
          nCodScadenza = 101;
        }
        else {
          if (strScadenziario.compareTo("AMM2") == 0) {
            nCodScadenza = 102;
          }
          else {
            if (strScadenziario.compareTo("AMM3") == 0) {
              nCodScadenza = 103;
            }
            else {
              if (strScadenziario.compareTo("AMM4") == 0) {
                nCodScadenza = 104; 
              }
            }
          }
        }
      }
    }
  }
}


//String codCpi = serviceRequest.containsAttribute("CODCPI")? serviceRequest.getAttribute("CODCPI").toString():"";
/* 
 * Se CODCPI è presente nella request, prendo il valore dalla request,
 * altrimenti prendo il valore che è in sessione
*/	

String codCpi = StringUtils.getAttributeStrNotNull(serviceRequest, "CODCPI");
int cdnUt = user.getCodut();
if(codCpi.equals("")) {
	codCpi =  user.getCodRif();	
}

switch (nCodScadenza) {
  case 1:
 	strTitolo = "Lavoratori da Ricontattare";
    break;
  case 2:
    strTitolo = "Aziende da Ricontattare";
    break;
  case 3:
    strTitolo = "Scadenze validità della scheda lavoratori";
    break;
  case 4:
    strTitolo = "Scadenze dei permessi di soggiorno";
    break;
  case 101:
    strTitolo = "Scadenze relative alle date stimate nei percorsi concordati con il lavoratore";
    break;
  case 102:
    strTitolo = "Scadenze relative al colloquio di primo orientamento";
    break;
  case 103:
    strTitolo = "Scadenze relative al patto che il cpi deve stipulare";
    break;
  case 104:
    strTitolo = "Scadenze validità del patto/accordo";
    break;
}

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title></title>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<af:linkScript path="../../js/"/>
<script language="Javascript">
  function ControllaData() {
    var bReturnOK = false;
    var dataDalCerca = "";
    var dataAlCerca = "";
    var dataDalCercaInt = 0;
    var dataAlCercaInt = 0;

    if (document.Frm1.DATADAL.value != "") {
      dataDalCerca = new String(document.Frm1.DATADAL.value);
      annoDataDal = dataDalCerca.substr(6,4);
      meseDataDal = dataDalCerca.substr(3,2);
      giornoDataDal = dataDalCerca.substr(0,2);
      dataDalCercaInt = parseInt(annoDataDal + meseDataDal + giornoDataDal,10);
    }

    if (document.Frm1.DATAAL.value != "") {
      dataAlCerca = new String(document.Frm1.DATAAL.value);
      annoDataAl = dataAlCerca.substr(6,4);
      meseDataAl = dataAlCerca.substr(3,2);
      giornoDataAl = dataAlCerca.substr(0,2);
      dataAlCercaInt = parseInt(annoDataAl + meseDataAl + giornoDataAl,10);            
    }
    
    if (<%=nCodScadenza%> == 101 || <%=nCodScadenza%> == 102 || <%=nCodScadenza%> == 103 || <%=nCodScadenza%> == 104) {
      if (document.Frm1.DATADAL.value != "") {
        var d = new Date();
        annoOdierno = d.getYear().toString();
        meseOdierno = (d.getMonth() + 1).toString();
        if (meseOdierno.length == 1) {
          meseOdierno = '0' + meseOdierno;
        }
        giornoOdierno = d.getDate().toString();
        if (giornoOdierno.length == 1) {
          giornoOdierno = '0' + giornoOdierno;
        }
        var dataOdierna = parseInt(annoOdierno + meseOdierno + giornoOdierno,10);

        if (dataDalCercaInt < dataOdierna) {
          alert ("Data Dal non può essere precedente alla data corrente");
          bReturnOK = false;
        }    
        else {
          if (document.Frm1.DATAAL.value != "") {
            if (dataDalCercaInt > dataAlCercaInt) {
              alert ("Data Dal deve essere minore o uguale a Data Al");
              bReturnOK = false;
            }
            else {
              bReturnOK = true;
            }
          }
          else {
            bReturnOK = true;
          }
        }
      }
      else {
        bReturnOK = true;
      }
    }
    else {
      if ((document.Frm1.DATADAL.value != "") && (document.Frm1.DATAAL.value != "")) { 
        if (dataDalCercaInt > dataAlCercaInt) {
          alert ("Data Dal deve essere minore o uguale a Data Al");
          bReturnOK = false;
        }
        else {
          bReturnOK = true;
        }
      } 
      else {
        bReturnOK = true;
      }
    }
    return bReturnOK;
  }
  
  function validaCodCpi(){
   	var tmp = document.Frm1.CODCPI.value;
   	document.Frm1.codCpiApp.value=tmp;
   }
  
</script>
</head>

<body class="gestione">
<af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="ControllaData()">
<p class="titolo"><%=strTitolo%></p>
<%out.print(htmlStreamTop);%>
<p align="center">
<table class="main"> 
<%
switch (nCodScadenza) {

  case 1:
  %>
    <tr>
    <td class="etichetta">Dal giorno</td>
    <td class="campo">
      <af:textBox type="date" name="DATADAL" title="Dal giorno" value="" size="12" maxlength="10" validateOnPost="true" required="false"/>&nbsp;&nbsp;
      <font class="etichetta">Al giorno</font>
      <af:textBox type="date" name="DATAAL" title="Al giorno" value="" size="12" maxlength="10" validateOnPost="true" required="false"/>
    </td>
    </tr>
    <tr>
    <td class="etichetta">Motivo</td>
    <td class="campo">
    <af:comboBox name="MOTIVO_CONTATTO" size="1" title="Motivo contatto"
                 multiple="false" disabled="false" required="false"
                 focusOn="false" moduleName="COMBO_MOTIVO_CONTATTO_AG"
                 addBlank="true" blankValue=""/>
    </td>
    </tr>
    <input type="hidden" name="DIREZIONE" value="O">
    <input type="hidden" name="CODCPI" value="<%=codCpi%>"/> 
    <!-- inizio campi nascosti
    <tr>
    <td class="etichetta">Operatore</td>
    <td class="campo">
    <af:comboBox name="PRGSPI" size="1" title="Operatore"
                 multiple="false" disabled="false" required="false"
                 focusOn="false" moduleName="COMBO_SPI_AG"
                 addBlank="true" blankValue=""/>&nbsp;&nbsp;
    <font type="etichetta">Servizio</font>
    <af:comboBox name="CODSERVIZIO" size="1" title="Servizio"
                 multiple="false" disabled="false" required="false"
                 focusOn="false" moduleName="COMBO_SERVIZIO"
                 addBlank="true" blankValue=""/>
    </td>
    </tr>
    <tr>
    <td class="etichetta">Tipo</td>
    <td class="campo">
    <af:comboBox name="TIPO" size="1" title="Tipo contatto"
                 multiple="false" disabled="false" required="false"
                 focusOn="false" moduleName="COMBO_TIPO_CONTATTO_AG"
                 addBlank="true" blankValue=""/>&nbsp;&nbsp;

    <af:comboBox name="DIREZIONE"
                 size="1" 
                 title="Tipo di Contatto"
                 multiple="false" 
                 disabled="false" 
                 required="false"
                 focusOn="false"
                 addBlank="true"
                 blankValue="">
      <option value="O">Dal CpI</option>
      <option value="I">Al CpI</option>
    </af:comboBox>
    </td>
    </tr>
    <tr>
    <td class="etichetta">Motivo</td>
    <td class="campo">
    <af:comboBox name="MOTIVO" size="1" title="Motivo contatto"
                 multiple="false" disabled="false" required="false"
                 focusOn="false" moduleName="COMBO_MOTIVO_CONTATTO_AG"
                 addBlank="true" blankValue=""/>
    </td>
    </tr>
    fine campi nascosti -->
  <%
    break;
    
  case 2:
    %>
    <tr>
    <td class="etichetta">Dal giorno</td>
    <td class="campo">
      <af:textBox type="date" name="DATADAL" title="Dal giorno" value="" size="12" maxlength="10" validateOnPost="true" required="false"/>&nbsp;&nbsp;
      <font class="etichetta">Al giorno</font>
      <af:textBox type="date" name="DATAAL" title="Al giorno" value="" size="12" maxlength="10" validateOnPost="true" required="false"/>
    </td>
    </tr>
    <tr>
    <td class="etichetta">Motivo</td>
    <td class="campo">
    <af:comboBox name="MOTIVO_CONTATTO" size="1" title="Motivo contatto"
                 multiple="false" disabled="false" required="false"
                 focusOn="false" moduleName="COMBO_MOTIVO_CONTATTO_AG"
                 addBlank="true" blankValue=""/>
    </td>
    </tr>
    <input type="hidden" name="DIREZIONE" value="O">
    <input type="hidden" name="CODCPI" value="<%=codCpi%>"/> 
    <!-- inizio campi nascosti
    <tr>
    <td class="etichetta">Operatore</td>
    <td class="campo">
    <af:comboBox name="PRGSPI" size="1" title="Operatore"
                 multiple="false" disabled="false" required="false"
                 focusOn="false" moduleName="COMBO_SPI_AG"
                 addBlank="true" blankValue=""/>&nbsp;&nbsp;
    <font type="etichetta">Servizio</font>
    <af:comboBox name="CODSERVIZIO" size="1" title="Servizio"
                 multiple="false" disabled="false" required="false"
                 focusOn="false" moduleName="COMBO_SERVIZIO"
                 addBlank="true" blankValue=""/>
    </td>
    </tr>
    <tr>
    <td class="etichetta">Tipo</td>
    <td class="campo">
    <af:comboBox name="TIPO" size="1" title="Tipo contatto"
                 multiple="false" disabled="false" required="false"
                 focusOn="false" moduleName="COMBO_TIPO_CONTATTO_AG"
                 addBlank="true" blankValue=""/>&nbsp;&nbsp;

    <af:comboBox name="DIREZIONE"
                 size="1" 
                 title="Tipo di Contatto"
                 multiple="false" 
                 disabled="false" 
                 required="false"
                 focusOn="false"
                 addBlank="true"
                 blankValue="">
      <option value="O">Dal CpI</option>
      <option value="I">Al CpI</option>
    </af:comboBox>
    </td>
    </tr>
    <tr>
    <td class="etichetta">Motivo</td>
    <td class="campo">
    <af:comboBox name="MOTIVO" size="1" title="Motivo contatto"
                 multiple="false" disabled="false" required="false"
                 focusOn="false" moduleName="COMBO_MOTIVO_CONTATTO_AG"
                 addBlank="true" blankValue=""/>
    </td>
    </tr>
    fine campi nascosti -->
  <%
    break;
    
  case 3:
  %>
    <tr>
    <td class="etichetta">Dal giorno</td>
    <td class="campo">
    <af:textBox type="date" name="DATADAL" title="Dal giorno" size="12" maxlength="10" validateOnPost="true" required="false"/>&nbsp;&nbsp;
    <font class="etichetta">Al giorno</font>
    <af:textBox type="date" name="DATAAL" title="Al giorno" size="12" maxlength="10" validateOnPost="true" required="false"/>
    </td>
    </tr>
    <tr>
    <td class="etichetta">Validità</td>
    <td class="campo">
    <af:comboBox name="CODTIPOVALIDITA" size="1" title="Tipo Validità"
                 multiple="false" disabled="false" required="true"
                 focusOn="false" moduleName="COMBO_VALIDITA_SCAD"
                 addBlank="true" blankValue=""/>
               
     </td>
    </tr>
    <tr>
    	<td class="etichetta" nowrap>
			Stato di validità del CV
  	   </td> 
  	  <td class="campo">
	 	<af:comboBox name="statoValCV" moduleName="M_LISTSTATOLAV" addBlank="true" selectedValue="" required="false"/>
  	  </td>
  	</tr>
    
    <tr><td colspan=2><hr></hr></td></tr>
    <tr valign="top">
		<td class="etichetta">Centro per l'impiego</td>
		<td class="campo">
			<af:comboBox name="CODCPI" title="Centro per l'impiego" moduleName="M_GetCpiPoloProvinciale" disabled="false"
			 focusOn="false" blankValue="" addBlank="true"  onChange="validaCodCpi();"/>
	</td>
	</tr>
		<input type="hidden" name="codCpiApp" value=""/>
	
	<!-- inizio campi nascosti
    <input type="hidden" name="CODSERVIZIO" value="">
    fine campi nascosti -->
  <%
    break;
    
  case 4:
  %>
    <tr>
    <td class="etichetta">Dal giorno</td>
    <td class="campo">
    <af:textBox type="date" name="DATADAL" title="Dal giorno" value="" size="12" maxlength="10" validateOnPost="true" required="false"/>&nbsp;&nbsp;
    <font class="etichetta">Al giorno</font>
    <af:textBox type="date" name="DATAAL" title="Al giorno" value="" size="12" maxlength="10" validateOnPost="true" required="false"/>
    </td>
    </tr>
    </table>
    <input type="hidden" name="CODCPI" value="<%=codCpi%>"/>
    <!-- inizio campi nascosti
    <input type="hidden" name="CODSERVIZIO" value="">
    fine campi nascosti -->
  <%
    break;

  case 101:
  %>
    <tr>
    <td class="etichetta">Dal giorno</td>
    <td class="campo">
    <af:textBox type="date" name="DATADAL" title="Dal giorno" value="" size="12" maxlength="10" validateOnPost="true" required="false"/>&nbsp;&nbsp;
    <font class="etichetta">Al giorno</font>
    <af:textBox type="date" name="DATAAL" title="Al giorno" value="" size="12" maxlength="10" validateOnPost="true" required="false"/>
    </td>
    </tr>
    
    <tr>
    <td class="etichetta">Tipologia Patto</td>
    <td class="campo">
    	<af:comboBox name="tipologiaPatto" moduleName="M_GetCodificaTipoPatto" addBlank="true" selectedValue="" 
    		required="false" classNameBase="input" disabled="false" title="Tipologia Patto"/>
    </td>
    </tr>
    <input type="hidden" name="CODCPI" value="<%=codCpi%>"/> 
    <!-- inizio campi nascosti
    <input type="hidden" name="CODSERVIZIO" value="">
    fine campi nascosti -->
  <%
    break;

  case 102:
  %>
    <tr>
    <td class="etichetta">Dal giorno</td>
    <td class="campo">
    <af:textBox type="date" name="DATADAL" title="Dal giorno" value="" size="12" maxlength="10" validateOnPost="true" required="false"/>&nbsp;&nbsp;
    <font class="etichetta">Al giorno</font>
    <af:textBox type="date" name="DATAAL" title="Al giorno" value="" size="12" maxlength="10" validateOnPost="true" required="false"/>
    </td>
    </tr>
    <input type="hidden" name="CODCPI" value="<%=codCpi%>"/> 
  <%
    break;

  case 103:
  %>
    <tr>
    <td class="etichetta">Dal giorno</td>
    <td class="campo">
    <af:textBox type="date" name="DATADAL" title="Dal giorno" value="" size="12" maxlength="10" validateOnPost="true" required="false"/>&nbsp;&nbsp;
    <font class="etichetta">Al giorno</font>
    <af:textBox type="date" name="DATAAL" title="Al giorno" value="" size="12" maxlength="10" validateOnPost="true" required="false"/>
    </td>
    </tr>
    <input type="hidden" name="CODCPI" value="<%=codCpi%>"/> 
  <%
    break;

  case 104:
  %>
    <tr>
    <td class="etichetta">Dal giorno</td>
    <td class="campo">
    <af:textBox type="date" name="DATADAL" title="Dal giorno" value="" size="12" maxlength="10" validateOnPost="true" required="false"/>&nbsp;&nbsp;
    <font class="etichetta">Al giorno</font>
    <af:textBox type="date" name="DATAAL" title="Al giorno" value="" size="12" maxlength="10" validateOnPost="true" required="false"/>
    </td>
    </tr>
    <tr>
    <td class="etichetta">Tipologia Patto</td>
    <td class="campo">
    	<af:comboBox name="tipologiaPatto" moduleName="M_GetCodificaTipoPatto" addBlank="true" selectedValue="" 
    		required="false" classNameBase="input" disabled="false" title="Tipologia Patto"/>
    </td>
    </tr>
    <input type="hidden" name="CODCPI" value="<%=codCpi%>"/> 
  <%
    break;
    
}
%>
</table>
<br>
<center>
<table align="center">
<tr>
<td align="center">
<input class="pulsanti" type="submit" name="cerca" value="Cerca"/>&nbsp;&nbsp;
<input type="reset" class="pulsanti" value="Annulla"/>
</td>
</tr>
</table>
</center>
<%out.print(htmlStreamBottom);%>
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
<input type="hidden" name="PAGE" value="ScadListaPage"/>
<input type="hidden" name="PAGEPROVENIENZA" value="ScadListaPage">
<input type="hidden" name="FILTRALISTA" value="0"/>
<input type="hidden" name="SCADENZIARIO" value="<%=strScadenziario%>"/>
</af:form>
</body>
</html>
