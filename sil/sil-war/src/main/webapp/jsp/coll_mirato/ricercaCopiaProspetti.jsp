<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ page contentType="text/html;charset=utf-8"	
	import="com.engiweb.framework.base.*,
			it.eng.afExt.utils.*,
			it.eng.sil.security.*,
			it.eng.sil.security.User,
			it.eng.sil.util.*,
			it.eng.sil.module.collocamentoMirato.constant.*,
			java.math.*,
			java.util.*"
	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%
	ProfileDataFilter filter = new ProfileDataFilter(user, "CopiaProspRicercaPage");
	boolean canView = filter.canView();
	if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}
	
	InfoProvinciaSingleton provincia = InfoProvinciaSingleton.getInstance(); 
	String codiceProv = provincia.getCodice();
	String visualizza_display 	= "none";
	String codCpi="";
	String titolo = "Ricerca prospetti per copie in corso d&#39;anno";
	
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE");
	String cdnfunzione = serviceRequest.getAttribute("CDNFUNZIONE").toString();
	codCpi = user.getCodRif();   	
	int cdnTipoGruppo = user.getCdnTipoGruppo();
	PageAttribs attributi = new PageAttribs(user, "CopiaProspRicercaPage");
	
	String token = "_TOKEN_" + "CopiaProspListaPage";
	String urlDiLista = (String) sessionContainer.getAttribute(token.toUpperCase());
	if (urlDiLista != null) {
		sessionContainer.delAttribute(token.toUpperCase());
	}
	
	String prgUltimaCopiaMassiva = "";
	BigDecimal prgRisultato = null;
	SourceBean risultatoUltimaCopia = (SourceBean) serviceResponse.getAttribute("M_ProspUltimaCopiaUtente.ROWS.ROW");
  	if (risultatoUltimaCopia != null) {
  		prgRisultato = (BigDecimal)risultatoUltimaCopia.getAttribute("PRGPICOPIECORSOANNO");
  		if (prgRisultato != null) {
  			prgUltimaCopiaMassiva = prgRisultato.toString();
  		}
  	}
	
	String htmlStreamTop    = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>
<head>
<title>Ricerca prospetti per copie in corso d&#39;anno</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<af:linkScript path="../../js/"/>  

<SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>
<script language="Javascript">
	var numAnnoRifInitCopia = <%=ProspettiConstant.ANNO_INIZIO_COPIA_IN_CORSO%>;
	function controllaDati() {
		var now = new Date();
		var numAnnoCorrente = now.getFullYear();
		var annoNuovo = document.Frm1.annoNew.value;
		var annoVecchio = document.Frm1.annoOld.value;
		var numAnnoNuovo = parseInt(annoNuovo);
		var numAnnoVecchio = parseInt(annoVecchio);

		if ( (numAnnoNuovo < numAnnoRifInitCopia) || (numAnnoNuovo > numAnnoCorrente) ) {
			alert("Attenzione: l'anno per la nuova copia deve essere un valore numerico \ncompreso tra l'anno " + numAnnoRifInitCopia + 
				  " e l'anno " + numAnnoCorrente + ".");
			return false;	
		}

		if ( (numAnnoVecchio < numAnnoRifInitCopia) || (numAnnoVecchio > (numAnnoCorrente - 1)) ) {
			alert("Attenzione: l'anno di verifica deve essere un valore numerico \ncompreso tra l'anno " + numAnnoRifInitCopia + 
				  " e l'anno " + (numAnnoCorrente - 1) + ".");
			return false;	
		}

		if (numAnnoVecchio >= numAnnoNuovo) {
			alert("Attenzione: l'anno di verifica deve essere minore dell'anno per la nuova copia in corso d'anno.");
			return false;	
		}
		
		return true;
	}

	//funzione di get per la visualizzazione dei risultati
	function visulizzaRisultati() {
		var get = "AdapterHTTP?PAGE=CopiaProspVisualizzaRisultPage&cdnFunzione=<%=cdnfunzione%>&PAGERISULTVALMASSIVA=FIRST&prgUltimaCopiaMassiva=<%=prgUltimaCopiaMassiva%>";
		setWindowLocation(get);
	}
</script>	

</head>

<body class="gestione" onload="rinfresca();">

<af:showErrors/>

<p class="titolo"><%=titolo%></p>

<af:form name="Frm1" action="AdapterHTTP" method="POST" onSubmit="controllaDati()">
<input type="hidden" name="PAGE"          value="CopiaProspListaPage" />
<input type="hidden" name="BACK_PAGE"     value="CopiaProspRicercaPage" />
<input type="hidden" name="cdnFunzione"   value="<%=cdnfunzione%>" />

<%= htmlStreamTop %> 
<table class="main" border="0">    
	<tr>
		<td class="etichetta">Anno per la nuova copia in corso d&#39;anno</td>
		<td class="campo">
			<af:textBox type="integer" title="Anno per la nuova copia in corso d&#39;anno" name="annoNew"	
				value="" size="4" maxlength="4" validateOnPost="true" required="true"/>
		</td>
	</tr>
	<tr>
		<td class="etichetta">Anno di verifica</td>
		<td class="campo">
			<af:textBox type="integer" title="Anno di verifica" name="annoOld"	
				value="" size="4" maxlength="4" validateOnPost="true" required="true"/>
		</td>
	</tr>
	<tr>
	    <td class="etichetta">Categoria azienda</td>
	    <td class="campo">
	    	<af:comboBox name="codMonoCategoria" classNameBase="input">	  
	               <option value="" ></option>
	               <option value="<%=ProspettiConstant.CATEGORIA_NULLA%>">Categoria nulla</option>
	               <option value="A">più di 50 dipendenti</option>
	           	<option value="B">da 36 a 50 dipendenti</option>               
	           	<option value="C">da 15 a 35 dipendenti</option> 
	       	</af:comboBox>  
	    </td>   
	</tr>	
	<tr><td colspan=2>&nbsp;</td></tr>
	<tr>
		<td colspan="2">
			<input type="submit" class="pulsanti" name="cerca" value="Cerca"/>
			&nbsp;&nbsp;
			<input type="reset" class="pulsanti" name="annulla" value="Annulla"/>
		</td>
	</tr>
	<%if (!prgUltimaCopiaMassiva.equals("")) {%>
	<tr>
		<td colspan="2">
			<input class="pulsanti" type="button" onclick="visulizzaRisultati();" name="azioneVisualizzaRis" value="Risultati ultima copia"/>
		</td>
	</tr>
	<%}%>
	<tr><td colspan="2">&nbsp;</td></tr>				
</table>

</af:form>
<%= htmlStreamBottom %>
</body>
</html>