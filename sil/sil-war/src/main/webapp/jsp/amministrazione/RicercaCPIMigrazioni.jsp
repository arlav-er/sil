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
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<head>
<title>Ricerca Cpi migrazioni</title>
<link rel="stylesheet" type="text/css" href=" ../../css/stili.css"/>  
<af:linkScript path="../../js/"/>
<script language="Javascript">
<!--
	function selezionaCombo(combo) {
		
		provincia = document.Frm1.CODPROVINCIA;
		regione = document.Frm1.CODREGIONE;
		if (combo.value=="") {
			provincia.disabled=false;
			regione.disabled=false;
		}
		else {			
			if (combo.name=="CODREGIONE" && !provincia.disabled)
				provincia.disabled=true;
			else if (combo.name=="CODPROVINCIA" && !regione.disabled)
				regione.disabled=true;	
		}
	}
	function nascondiCombo(){
		idMostra = "riga_provincia";
		idNascondi = "riga_regione";
		document.getElementById(idNascondi).style.display = "none";
		document.getElementById(idMostra).style.display = "none";
	}
	function mostraCombo(){
		idMostra = "riga_provincia";
		idNascondi = "riga_regione";
		document.getElementById(idNascondi).style.display = "";
		document.getElementById(idMostra).style.display = "";
	}
	
	function riabilitaCombo(){
		document.Frm1.CODREGIONE.disabled=false;
		document.Frm1.CODPROVINCIA.disabled=false;
	}
//-->
</script>
</head>

<% boolean readOnlyStr = false;
   String htmlStreamTop = StyleUtils.roundTopTable(false);
   String htmlStreamBottom = StyleUtils.roundBottomTable(false);
   String _page = (String)serviceRequest.getAttribute("PAGE");
%>

<body class="gestione" onload="rinfresca();">
<br>
<p class="titolo">Ricerca per migrazioni</p>
<p align="center">
  <af:form name="Frm1" action="AdapterHTTP" method="POST" >
  <%out.print(htmlStreamTop);%> 
  <table class="main">    
   <tr><td>&nbsp;</td></tr>
   <tr>
     <td class="etichetta">Territorio</td>
     <td class="campo">
     	<table><tr>
     	<td><input type="radio" name="TERRITORIO" value="tutti" checked /><td>nessuna restrizione&nbsp;&nbsp;&nbsp;&nbsp;
     	<td><input type="radio" name="TERRITORIO" value="fuori_pro" /><td>fuori provincia&nbsp;&nbsp;&nbsp;&nbsp;
     	<td><input type="radio" name="TERRITORIO" value="fuori_reg" /><td>fuori regione
     	</tr></table>
     </td>
   </tr>
   <tr><td colspan="2">&nbsp;</td></tr>
   <tr id="riga_regione">
     <td class="etichetta">Regione </td>
     <td class="campo"><af:comboBox name="CODREGIONE" moduleName="M_GETREGIONIMIG"  onChange="selezionaCombo(this)"
     				classNameBase="input" addBlank="true" disabled="<%=String.valueOf(readOnlyStr)%>"/></td>
   </tr>
   <tr id="riga_provincia">
     <td class="etichetta">Provincia</td>
     <td class="campo"><af:comboBox  name="CODPROVINCIA" moduleName="M_GETPROVINCEMIG" onChange="selezionaCombo(this)"
     			classNameBase="input" addBlank="true" disabled="<%=String.valueOf(readOnlyStr)%>"/></td>
   </tr>
   <tr>
     <td class="etichetta">Gi&agrave; impostati</td>
     <td class="campo"><af:comboBox  name="TIPO_FILE" 
     			classNameBase="input" addBlank="true" disabled="<%=String.valueOf(readOnlyStr)%>">
     				<option value="S">Si</option>
     				<option value="N">No</option>     				
     			</af:comboBox>			
     </td>
	<tr>
		<td colspan="2">
			<div class="sezione2"></div>
		</td>
	</tr>
     <td class="etichetta">Descrizione Cpi</td>
     <td class="campo"><af:textBox type="text" name="DESCRIZIONE" value="" maxlength="100" /></td>
   </tr>
    <tr>
      <td class="etichetta">tipo ricerca</td>
      <td class="campo">
      <table colspacing="0" colpadding="0" border="0">
      <tr>
       <td><input type="radio" name="tipoRicerca" value="esatta" CHECKED/> esatta&nbsp;&nbsp;&nbsp;&nbsp;</td>
       <td><input type="radio" name="tipoRicerca" value="iniziaPer"/> contiene</td>
      </tr>
      </table>
      </td>
    </tr>
   <tr><td>&nbsp;</td></tr>
   <tr><td>&nbsp;</td></tr>
   <tr><td>&nbsp;</td></tr>
   <tr>
    <td colspan="2" align="center">
      <input class="pulsante" type="submit" name="cerca" value="Cerca"/>
      &nbsp;&nbsp;
      <input name="reset" type="reset" class="pulsanti" value="Annulla" onclick="riabilitaCombo()">
    </td>
   </tr>
  </table>
 <%out.print(htmlStreamBottom);%> 
  <input type="hidden" name="CDNFUNZIONE" value="<%=((String) serviceRequest.getAttribute("CDNFUNZIONE"))%>"/>
  <input type="hidden" name="PROVENIENZA" value="<%=_page%>"/>
  <input type="hidden" name="PAGE" value="ListaCpiMigrazioniPage"/>
  </af:form>
</p>

</body>
</html>
