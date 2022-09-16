<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>


<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.module.movimenti.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%      
String progressivo = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGMOBILITAISCR");

String htmlStreamTop = StyleUtils.roundTopTable(true);
String htmlStreamBottom = StyleUtils.roundBottomTable(true);

%>

<html>
<head>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<af:linkScript path="../../js/" />

<title></title>
<script language="Javascript">
	
	var flagRicercaPage = "S";

	function ricercaAvanzataMansioni() {
  		window.open("AdapterHTTP?PAGE=RicercaMansioneAvanzataPage&FLGIDO=S", "Mansioni", 'toolbar=0, scrollbars=1, resizable=yes,height=600, width=800');
	}
	
	function selectMansioneOnClick(codMansione, codMansioneHid, descMansione, strTipoMansione) {	
	  	if (codMansione.value==""){
	    	descMansione.value="";
	    	strTipoMansione.value="";      
	  	}
	  	else {
	  		window.open("AdapterHTTP?PAGE=RicercaMansionePage&FLGIDO=S&codMansione="+codMansione.value, "Mansioni", 'toolbar=0, scrollbars=1');     
	  	}
	}
	//Ricerca della mansione per descrizione
	function selectMansionePerDescrizione(desMansione) {
		window.open("AdapterHTTP?PAGE=RicercaMansionePage&FLGIDO=S&desMansione="+desMansione.value+"&flgFrequente=", "Mansioni", 'toolbar=0, scrollbars=1');          
	}
	
	function controlla() {
		if ( (document.Frm1.CODMANSIONE.value != "") && (document.Frm1.CODMANSIONE.value == document.Frm1.codMansioneHid.value) ) {
			document.Frm1.submit();
			document.Frm1.CODMANSIONE.value = "";
			document.Frm1.DESCMANSIONE.value = "";
			document.Frm1.strTipoMansione.value = "";
		}
	}	

</script>

</head>

<body class="gestione">
<p class="titolo">Ricerca disponibilt&agrave;</p>
<br>
<af:form name="Frm1" method="POST" target="DispoMobilitaSuperiore" action="AdapterHTTP">
<%out.print(htmlStreamTop);%>
<table class="main">

<tr>
	<td class="etichetta" nowrap>Qualifica</td>
	<td class="campo" nowrap>
		<af:textBox classNameBase="input" title="Qualifica" name="CODMANSIONE" size="7" maxlength="7"/>
		<af:textBox type="hidden" name="codMansioneHid"/>    
		<a href="javascript:selectMansioneOnClick(document.Frm1.CODMANSIONE, document.Frm1.codMansioneHid, document.Frm1.DESCMANSIONE,  document.Frm1.strTipoMansione);"><img src="../../img/binocolo.gif" alt="Cerca"></A>
        <af:textBox classNameBase="input" type="text" size="50" name="DESCMANSIONE" value=""/>
       	<A href="javascript:selectMansionePerDescrizione(document.Frm1.DESCMANSIONE);">
	   	 	<img src="../../img/binocolo.gif" alt="Cerca per descrizione">
	   	</A>
	</td>
</tr>
<!-- tipo e descrizione mansione -->
<tr>
	<td class="etichetta" nowrap>Tipo mans.:</td>
	<td class="campo" nowrap>
		<af:textBox type="hidden" name="CODTIPOMANSIONE" value=""/>
		<af:textBox classNameBase="input" type="text" size="35" name="strTipoMansione" readonly="true"/>&nbsp;&nbsp;
		<A href="javascript:ricercaAvanzataMansioni();">Ricerca avanzata</A>
		<script language="javascript">
		  if(document.Frm1.CODMANSIONE.value != null && document.Frm1.CODMANSIONE.value != "" &&
		    (document.Frm1.strTipoMansione.value == "")) {
		     selectMansioneOnClick(document.Frm1.CODMANSIONE, document.Frm1.codMansioneHid, document.Frm1.DESCMANSIONE,  document.Frm1.strTipoMansione);
		  }
		</script>
		<af:textBox type="hidden" name="strDesAttivita" value=""/>
	</td>
</tr>
<tr><td></td></tr>

</table>
<%out.print(htmlStreamBottom);%>
<br>
<center>
<table align="center">
<tr>
<td align="center">
<input class="pulsanti" type="button" name="btnInserisci" value="Inserisci disponibilit&agrave;" onclick="controlla();"/>&nbsp;&nbsp;
<input type="reset" class="pulsanti" value="Annulla"/>
</td>
</tr>
</table>
</center>

<input type="hidden" name="PRGMOBILITAISCR" value="<%=progressivo%>">
<input type="hidden" name="PAGE" value="MobListaDisponibilitaPage">
<input type="hidden" name="INSERISCI" value="S">
</af:form>
</body>
</html>
