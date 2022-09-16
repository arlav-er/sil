<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/Function_CommonRicercaComune.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>

<%@ page
	import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.StringUtils,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*"%>


<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%
	String _funzione=(String) serviceRequest.getAttribute("cdnFunzione");
	String _page = (String) serviceRequest.getAttribute("PAGE"); 
	
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	PageAttribs attributi = new PageAttribs(user, _page);
	boolean canSearch = false;
	if (!filter.canView()) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	} else {
			 canSearch = attributi.containsButton("CERCA_CORSI_SIFER");
			 String strTitoloCorso = StringUtils.getAttributeStrNotNull(serviceRequest,"strTitoloCorso");
			 String strCodiceCorso = StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceCorso");
			 String annoCorso = StringUtils.getAttributeStrNotNull(serviceRequest,"annoCorso");
			 String dataInizioCorso = StringUtils.getAttributeStrNotNull(serviceRequest,"dataInizioCorso");
			 String dataFineCorso = StringUtils.getAttributeStrNotNull(serviceRequest, "dataFineCorso");
			 String tipoCertificazione = StringUtils.getAttributeStrNotNull(serviceRequest,"tipoCertificazione"); 
			 String strEnte	= StringUtils.getAttributeStrNotNull(serviceRequest, "strEnte");
			 String codComEnte = StringUtils.getAttributeStrNotNull(serviceRequest, "codComEnte");
	

%>

<%
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>
<head>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
<af:linkScript path="../../js/" />
<SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>

<script language="Javascript">
     
     	function controllaCampi() {

			var dataInvioDa = document.Frm1.dataInizioCorso.value;
			var dataInvioA = document.Frm1.dataFineCorso.value;
			
				
			var checkDate =  confrontaDate(dataInvioDa, dataInvioA);
			if(checkDate<0){
				alert("Data Inizio Corso maggiore della Data Fine Corso"); 
			    return false;
			}

			
			return true;
   		}
		  
     	function controllaAnno(inputName) {
     		  var dataObj = eval("document.forms[0]." + inputName);
     		  anno=dataObj.value;     		  
     	 
     	 	  var ok=true;
     	 	if (anno=='') {
     	 		return ok; 
     	 	}
     		  if (anno!=null && anno.toString().length != 4) {
     		    ok=false;
     		    alert("Il campo Anno deve essere un numero intero di 4 cifre");
     		  }  

     		  if (ok) {
     		  	return ok; 
     		  }  
     		}

     	  function valorizzaHidden() {
     		  	document.Frm1.descComune_H.value = document.Frm1.codComEnte.options[document.Frm1.codComEnte.selectedIndex].text;
     		  	document.Frm1.descTipoCert_H.value = document.Frm1.tipoCertificazione.options[document.Frm1.tipoCertificazione.selectedIndex].text;
     		  	return true;
     	 }
</script>
</head>
<body class="gestione" onload="rinfresca()">
	<br>
	<p class="titolo">Ricerca Corsi Formazione</p>
	<p align="center">
		<af:form action="AdapterHTTP" method="POST" name="Frm1"
			onSubmit="controllaCampi() && valorizzaHidden()"> 
			<%out.print(htmlStreamTop);%>

			<table class="main">
				<tr>
					<td colspan="2" />&nbsp;
					</td></tr>
				<tr>
					<td class="etichetta">Titolo Corso</td>
					<td class="campo"><af:textBox type="text"
							name="strTitoloCorso" value="<%=strTitoloCorso%>" size="60"
							maxlength="50" /></td>
				</tr>
				<tr>
					<td class="etichetta">Codice Corso</td>
					<td class="campo"><af:textBox type="text" title="Codice Corso"
							name="strCodiceCorso" value="<%=strCodiceCorso%>" size="20"
							maxlength="20" /></td>
				</tr>
				<tr>
			        <td class="etichetta">Anno Corso</td>
			        <td class="campo">
			                  <af:textBox name="annoCorso" type="integer" 
			                  classNameBase="input" title="Anno Corso" value="<%= Utils.notNull(annoCorso) %>" 
			                  onKeyUp="fieldChanged()" 
			                  validateOnPost="true" maxlength="4" size="4" validateWithFunction="controllaAnno"/>
			        </td>
			      </tr>
				<tr>
					<td class="etichetta">Data Inizio Corso</td>
					<td class="campo"><af:textBox validateOnPost="true"
							type="date" name="dataInizioCorso" title="Data Inizio Corso"
							value="<%=dataInizioCorso%>" size="10" maxlength="10" /></td>
				</tr>
				<tr>
					<td class="etichetta">Data Fine Corso</td>
					<td class="campo"><af:textBox validateOnPost="true"
							type="date" name="dataFineCorso" title="Data Fine Corso"
							value="<%=dataFineCorso%>" size="10" maxlength="10" /></td>
				</tr>
				<tr valign="top">
					<td class="etichetta">Tipo Certificazione</td>
					<td class="campo"><af:comboBox name="tipoCertificazione"
							title="Tipo Certificazione"
							moduleName="M_ElencoTipoCertificazione" disabled="false"
							focusOn="false" blankValue="" selectedValue="<%=tipoCertificazione%>" addBlank="true" /></td>
				</tr>
				<tr>
					<td class="etichetta">Descrizione Ente</td>
					<td class="campo"><af:textBox type="text" name="strEnte"
							value="<%=strEnte%>" size="20" maxlength="20" /></td>
				</tr>

				<tr>
					<td class="etichetta">Comune Ente&nbsp;</td>
					<td class="campo"><af:comboBox name="codComEnte"
							title="Comune Ente"
							moduleName="M_ElencoComuniProvincia" disabled="false"
							focusOn="false" blankValue="" selectedValue="<%=codComEnte%>" addBlank="true" /></td>
				</tr>

				<tr>
					<td colspan="2">&nbsp;</td>
				</tr>
				<tr>
					<td colspan="2" align="center">
					<% if (canSearch)  {%>
						<input class="pulsanti"	type="submit" name="cerca" value="Cerca" /> &nbsp;&nbsp; 
						<input	type="reset" class="pulsanti" value="Annulla" /></td>					
					<%}%>
				</tr>
				<input type="hidden" name="PAGE" value="ListaCorsiFormazionePage" />
				<input type="hidden" name="cdnFunzione" value="<%=_funzione%>" />
				<input type="hidden" name="descComune_H" value="" />
				<input type="hidden" name="descTipoCert_H" value="" />
			</table>
		</af:form>

		<%out.print(htmlStreamBottom);%>
	
</body>
</html>
<%}%>
