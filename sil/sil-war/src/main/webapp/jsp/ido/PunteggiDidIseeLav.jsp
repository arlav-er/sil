<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ page import="com.engiweb.framework.base.*,
                com.engiweb.framework.security.*,
                java.lang.*,java.text.*,java.util.*,
                it.eng.afExt.utils.*, it.eng.sil.util.*, java.math.BigDecimal,
                it.eng.sil.security.*" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>

<%
	String _page = (String) serviceRequest.getAttribute("PAGE"); 
  	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
      
	if (! filter.canView()){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}

	PageAttribs attributi = new PageAttribs(user,_page);

	String cdnLavoratore = (String) serviceRequest.getAttribute("CDNLAVORATORE");
	String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
	String provenienza = (String) serviceRequest.getAttribute("PROVENIENZA");
	boolean canUpdate = attributi.containsButton("UPDATE_PUNTEGGI");
	boolean isFromLav = false;
	if(StringUtils.isFilledNoBlank(provenienza) && provenienza.equalsIgnoreCase("LAVORATORE")){
		isFromLav = true;
	}
	 
	String codMonoDid = null;
	String codMonoIsee = null;
	String flgFamDec = null;
	String flgProfessionalita = null;
	String numCaricoFam = null;
	BigDecimal prgNominativo = null;
	BigDecimal prgRichiestaAz = null;
	String rosaDefinitiva = null;
	boolean canModify = false;
	SourceBean richiestaRow = (SourceBean) serviceResponse.getAttribute("M_PunteggiDidIseeAdesione.ROWS.ROW");
	if (richiestaRow != null) {
		prgNominativo = (BigDecimal) richiestaRow.getAttribute("prgnominativo");
		prgRichiestaAz = (BigDecimal) richiestaRow.getAttribute("prgrichiestaaz");
		codMonoDid = StringUtils.getAttributeStrNotNull(richiestaRow,"cod_mono_did");
		codMonoIsee = StringUtils.getAttributeStrNotNull(richiestaRow,"cod_mono_isee");
		rosaDefinitiva = StringUtils.getAttributeStrNotNull(richiestaRow, "viewaggiornabtn");
		prgNominativo = (BigDecimal) richiestaRow.getAttribute("prgnominativo");
		prgRichiestaAz = (BigDecimal) richiestaRow.getAttribute("prgrichiestaaz");
		codMonoDid = (String) richiestaRow.getAttribute("cod_mono_did");
		codMonoIsee = (String) richiestaRow.getAttribute("cod_mono_isee");
		flgFamDec = (String) richiestaRow.getAttribute("flgfamdec");
		flgProfessionalita = (String) richiestaRow.getAttribute("flgProfessionalita");
		BigDecimal numCaricoFamBD = (BigDecimal) richiestaRow.getAttribute("numcaricofam");
		if(numCaricoFamBD!=null){
			numCaricoFam = numCaricoFamBD.toPlainString();
		}
		
		rosaDefinitiva = StringUtils.getAttributeStrNotNull(richiestaRow, "viewaggiornabtn");
		if (StringUtils.isFilledNoBlank(rosaDefinitiva) && "1".equalsIgnoreCase(rosaDefinitiva)) {
			canModify = true;
				} else {
			canModify = false;
		}
	}
	if (StringUtils.isEmptyNoBlank(codMonoDid)) {
		codMonoDid = "S";
	}
	if (StringUtils.isEmptyNoBlank(codMonoIsee)) {
		codMonoIsee = "S";
	}
	boolean isConfigAsAttrib = false;
	boolean isConfigAsAttrib_VDA = false;
	boolean isConfigAsAttrib_CAL = false;
	if(serviceResponse.containsAttribute("M_CONFIG_PUNTEGGIDIDISEE.ROWS.ROW")){
		String strValoreConfig = (String) serviceResponse.getAttribute("M_CONFIG_PUNTEGGIDIDISEE.ROWS.ROW.STRVALORECONFIG");
		BigDecimal numValoreConfig = (BigDecimal) serviceResponse.getAttribute("M_CONFIG_PUNTEGGIDIDISEE.ROWS.ROW.NUMVALORECONFIG");
		if(strValoreConfig.equalsIgnoreCase("1") || (numValoreConfig!=null && numValoreConfig.intValue() == 1)){
			isConfigAsAttrib = true;
		}
		if( numValoreConfig!=null && numValoreConfig.intValue() == 2){
			isConfigAsAttrib_VDA = true;
		}
		if( numValoreConfig!=null && numValoreConfig.intValue() == 3){
			isConfigAsAttrib_CAL = true;
		}
	}
	String strErrorCode = "";
	String msgConferma = "";
	boolean confirm = false;
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);

	String p_page = (String) serviceRequest.getAttribute("OLD_PAGE");
	String prgRosa = (String) serviceRequest.getAttribute("prgRosa");
	String prgTipoRosa = (String) serviceRequest.getAttribute("prgTipoRosa");
	String prgTipoIncrocio = (String) serviceRequest.getAttribute("prgTipoIncrocio");

	String prgAzienda = (String) serviceRequest.getAttribute("prgAzienda");
	String prgUnita = (String) serviceRequest.getAttribute("prgUnita");
	String message = (String) serviceRequest.getAttribute("MESSAGE");
	String listPage = (String) serviceRequest.getAttribute("OLD_LIST_PAGE");
	if (("").equals(listPage) || listPage == null) {
		if (("LIST_FIRST").equalsIgnoreCase(message)) {
			listPage = "1";
		} else if (("LIST_LAST").equalsIgnoreCase(message)) {
			listPage = "-1";
		} else {
			listPage = "1";
		}
	}

	String indietro = "";
	if (("ASListaAdesioniPage").equalsIgnoreCase(p_page)) {
		indietro = "AdapterHTTP?PAGE=ASListaAdesioniPage&CDNFUNZIONE="
				+ cdnFunzione + "&cdnLavoratore=" + cdnLavoratore
				+ "&LIST_PAGE=1";
	} else if (("ASStoricoDettGraduatoriaPage").equalsIgnoreCase(p_page)) {
		indietro = "AdapterHTTP?PAGE=ASStoricoDettGraduatoriaPage&MODULE=ASStoricoCandidatiGraduatoria&PRGROSA="
				+ prgRosa
				+ "&PRGTIPOROSA="
				+ prgTipoRosa
				+ "&PRGTIPOINCROCIO="
				+ prgTipoIncrocio
				+ "&PRGRICHIESTAAZ="
				+ prgRichiestaAz
				+ "&PRGAZIENDA="
				+ prgAzienda
				+ "&PRGUNITA="
				+ prgUnita
				+ "&CDNFUNZIONE="
				+ cdnFunzione
				+ "&MESSAGE="
				+ message
				+ "&LIST_PAGE=" + listPage;
	} else {
		indietro = "AdapterHTTP?PAGE=ASMatchDettGraduatoriaPage&MODULE=ASCandidatiGraduatoria&PRGROSA="
				+ prgRosa
				+ "&PRGTIPOROSA="
				+ prgTipoRosa
				+ "&PRGTIPOINCROCIO="
				+ prgTipoIncrocio
				+ "&PRGRICHIESTAAZ="
				+ prgRichiestaAz
				+ "&PRGAZIENDA="
				+ prgAzienda
				+ "&PRGUNITA="
				+ prgUnita
				+ "&CDNFUNZIONE="
				+ cdnFunzione
				+ "&MESSAGE="
				+ message
				+ "&LIST_PAGE=" + listPage;
	}
%>


<script>

function go(url, alertFlag) {      
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;
  
  var _url = "AdapterHTTP?" + url;
  if (alertFlag == 'TRUE' ) {
    if (confirm('Confermi operazione')) {
      setWindowLocation(_url);
    }
  } else {
    setWindowLocation(_url);
  }
}

function checkNumFam(){
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;
	  
	  cambioValore();
		
	  if( document.Frm1.numCaricoFam.value !== "" ) {
    	var strNum = parseInt(document.Frm1.numCaricoFam.value);
    	if(strNum <0 )
    		return false;
    }	  	
    
    return true;	  
}

</script>

<html>
<head>
<title></title>
 <link rel="stylesheet" type="text/css" href="../../css/stili.css" />
 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
 <af:linkScript path="../../js/" />
 <%@ include file="../global/fieldChanged.inc" %>
 <script>
	function goBack() {
		ok = true;
		if (flagChanged) {
			ok = confirm("I dati sono cambiati.\nProcedere lo stesso?");

			// Vogliamo chiudere il layer.
			// Pongo il flag=false per evitare che mi venga riproposto un "confirm"
			// quando poi navigo con le linguette nella pagina principale
			flagChanged = false;
		}
		
		if(ok) {
			url="AdapterHTTP?PAGE=ASListaAdesioniPage";
		    url += "&CDNFUNZIONE="+"<%=cdnFunzione%>";      
		    url += "&cdnLavoratore="+"<%=cdnLavoratore%>";     
		     
		    setWindowLocation(url);		
		}
	}	
	function indietro(){  	
		ok = true;
		if (flagChanged) {
			ok = confirm("I dati sono cambiati.\nProcedere lo stesso?");

			// Vogliamo chiudere il layer.
			// Pongo il flag=false per evitare che mi venga riproposto un "confirm"
			// quando poi navigo con le linguette nella pagina principale
			flagChanged = false;
		}
		if(ok) {
			window.location="<%=indietro%>";     	
		}
		
	}
	
	function cambioValore(){ 
		<%if (canModify) {out.print("flagChanged = true;");}%>
 	}
</script>
 
 
</head>

<body onload="rinfresca()">

<%
    if (cdnLavoratore != null) {         
        InfCorrentiLav _testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
        _testata.setSkipLista(true);
        _testata.show(out);
	}
%>
 
<br/>
<%if(isConfigAsAttrib_VDA || isConfigAsAttrib_CAL){ %>
<p class="titolo">Modalit&agrave; di attribuzione dei punteggi art. 16</p>

<%}else{ %>
<p class="titolo">Modalit&agrave; di attribuzione dei punteggi per DID e ISEE</p>

<%} %>
	<p>
	 	<font color="green">
	 		<%-- <af:showMessages prefix="M_UltimaNotificaRDC"/> --%>
	  	</font>
	  	<font color="red"><af:showErrors /></font>
	</p>
<af:form name="Frm1" method="POST" action="AdapterHTTP">
<%out.print(htmlStreamTop);%>
<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"/>
<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>"/>
<input type="hidden" name="PRGNOMINATIVO" value="<%=prgNominativo%>"/>
<% if (isFromLav) { %>
	<input type="hidden" name="PROV" value="LAV"/>
	<input type="hidden" name="PAGE" value="ASListaAdesioniPage"/>
	<input type="hidden" name="MODULE" value="M_ASUpdatePunteggiRichiestaAdesione"/>
<%}else{ %>
	<input type="hidden" name="PAGE" value="ASMatchDettGraduatoriaPage"/>
	<input type="hidden" name="MODULE_PUNTEGGI" value="M_ASUpdatePunteggiRichiestaAdesione"/>
	<input type="hidden" name="MODULE" value="ASCalcolaPunteggioModule"/>
<%} %>
<input type="hidden" name="PRGRICHIESTAAZ" value="<%=prgRichiestaAz%>">
<input type="hidden" name="PRGROSA" value="<%=prgRosa%>">
<input type="hidden" name="PRGTIPOROSA" value="<%=prgTipoRosa%>">
<input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>">
<input type="hidden" name="PRGTIPOINCROCIO" value="<%=prgTipoIncrocio%>">
<input type="hidden" name="PRGUNITA" value="<%=prgUnita%>">
<input type="hidden" name="OLD_LIST_PAGE" value="<%=listPage%>">
<input type="hidden" name="MESSAGE" value="<%=message%>">
<input type="hidden" name="LIST_PAGE" value="<%=listPage%>">

		<table width="96%" align="center">
			<tr valign="middle">
				<td align="left">
          <%if((isConfigAsAttrib || isConfigAsAttrib_VDA || isConfigAsAttrib_CAL) && canUpdate && canModify){ %>
          		<table width="100%">
                <%if(isConfigAsAttrib){   %>
						<tr>
							<td>Punteggio DID</td>
							<td>
								<table colspacing="0" colpadding="0" border="0">
									
										<%if (codMonoDid.equals("S")) {%>
									<tr>
										<td><input type="radio"  onclick="cambioValore();" name="codMonoDid" value="S"
											CHECKED /> In base a dati in SIL&nbsp;&nbsp;</td>
									</tr>
									<tr>
										<td><input type="radio"  onclick="cambioValore();" name="codMonoDid" value="R" />
											Attribuisci punti DID&nbsp;&nbsp;</td>
									</tr>
									<tr>
										<td><input type="radio"  onclick="cambioValore();" name="codMonoDid" value="E" />
											Escludi punti DID</td>
									</tr>
									<tr><td>&nbsp;</td></tr>
										<%} else if (codMonoDid.equals("R"))  {%>
									<tr>
										<td><input type="radio"  onclick="cambioValore();" name="codMonoDid" value="S"
											CHECKED /> In base a dati in SIL&nbsp;&nbsp;</td>
									</tr>
									<tr>
										<td><input type="radio"  onclick="cambioValore();" name="codMonoDid" value="R" CHECKED />
											Attribuisci punti DID&nbsp;&nbsp;</td>
									</tr>
									<tr>
										<td><input type="radio"  onclick="cambioValore();" name="codMonoDid" value="E" />
											Escludi punti DID</td>
									</tr>
									<tr><td>&nbsp;</td></tr>
										<%} else if (codMonoDid.equals("E"))  {%>
									<tr>
										<td><input type="radio"  onclick="cambioValore();" name="codMonoDid" value="S"
											 /> In base a dati in SIL&nbsp;&nbsp;</td>
									</tr>
									<tr>
										<td><input type="radio"  onclick="cambioValore();" name="codMonoDid" value="R" />
											Attribuisci punti DID&nbsp;&nbsp;</td>
									</tr>
									<tr>
										<td><input type="radio"  onclick="cambioValore();" name="codMonoDid" value="E" CHECKED />
 											Escludi punti DID</td>
									</tr>
									<tr><td>&nbsp;</td></tr>
										<%}%>
								</table>
							</td>
						</tr>
				<%}%>
						<tr>
							<td>Punteggio ISEE</td>
							<td>
								<table colspacing="0" colpadding="0" border="0">
										<%if (codMonoIsee.equals("S")) {%>
										<tr>
										<td><input type="radio"  onclick="cambioValore();" name="codMonoIsee" value="S" 
											CHECKED /> In base a dati in SIL&nbsp;&nbsp;</td>
									</tr>
									<tr>
										<td><input type="radio"  onclick="cambioValore();" name="codMonoIsee" value="E" />
											Escludi ISEE</td>
									</tr>
									<tr><td>&nbsp;</td></tr>
										<%} else if (codMonoIsee.equals("E"))  {%>
										<tr>
										<td><input type="radio"  onclick="cambioValore();" name="codMonoIsee" value="S"
											 /> In base a dati in SIL&nbsp;&nbsp;</td>
									</tr>
									<tr>
										<td><input type="radio"  onclick="cambioValore();" name="codMonoIsee" value="E" CHECKED />
											Escludi ISEE</td>
									</tr>
									<tr><td>&nbsp;</td></tr>
										<%}%>
								</table>
							</td>
						</tr>
					
			<%if(isConfigAsAttrib_VDA){ %>
			 <tr>
				<td>Familiare di lavoratore deceduto per incidente sul lavoro</td>
			    <td colspan="2">
			    	<table colspacing="0" colpadding="0" border="0">
			      		<tr>
			      			<%if (flgFamDec!=null && flgFamDec.equals("S")) {%>
			       				<td>
			       					<input type="radio"  onclick="cambioValore();" name="flgFamDec" value="S" CHECKED /> Sì&nbsp;&nbsp;
			       				</td>
			       				<td>
			       					<input type="radio"  onclick="cambioValore();" name="flgFamDec" value="N" /> No
			       				</td>
			       			<%} else if (flgFamDec!= null && flgFamDec.equals("N"))  {%>
			       				<td>
			       					<input type="radio"  onclick="cambioValore();" name="flgFamDec" value="S"/> Sì&nbsp;&nbsp;
			       				</td>
			       				<td>
			       					<input type="radio" onclick="cambioValore();" name="flgFamDec" value="N" CHECKED /> No
			       				</td>
			      			<%}else {%>
			      				<td>
			       					<input type="radio"  onclick="cambioValore();" name="flgFamDec" value="S"/> Sì&nbsp;&nbsp;
			       				</td>
			       				<td>
			       					<input type="radio"  onclick="cambioValore();" name="flgFamDec" value="N"/> No
			       				</td>
			      			<%}%>
			        	</tr>
			        </table>
			    </td>
			</tr>
			<tr>
				<td>Professionalità acquisita</td>
			    <td colspan="2">
			    	<table colspacing="0" colpadding="0" border="0">
			      		<tr>
			      			<%if (flgProfessionalita!= null && flgProfessionalita.equals("S")) {%>
			       				<td>
			       					<input type="radio"  onclick="cambioValore();" name="flgProfessionalita" value="S" CHECKED /> Sì&nbsp;&nbsp;
			       				</td>
			       				<td>
			       					<input type="radio"  onclick="cambioValore();" name="flgProfessionalita" value="N" /> No
			       				</td>
			       			<%} else if (flgProfessionalita!= null && flgProfessionalita.equals("N"))  {%>
			       				<td>
			       					<input type="radio"  onclick="cambioValore();" name="flgProfessionalita" value="S"/> Sì&nbsp;&nbsp;
			       				</td>
			       				<td>
			       					<input type="radio"  onclick="cambioValore();" name="flgProfessionalita" value="N" CHECKED /> No
			       				</td>
			      			<%}else {%>
			      				<td>
			       					<input type="radio"  onclick="cambioValore();" name="flgProfessionalita" value="S"/> Sì&nbsp;&nbsp;
			       				</td>
			       				<td>
			       					<input type="radio"  onclick="cambioValore();" name="flgProfessionalita" value="N"/> No
			       				</td>
			      			<%}%>
			        	</tr>
			        </table>
			    </td>
			</tr>
			<tr>
			  <td>Carico familiare</td>
			  <td>
			    <af:textBox name="numCaricoFam" 
			                value="<%=numCaricoFam %>"
			                size="4"
			                title="Carico familiare"
			                disabled="false"
			                required="false"
			                type="integer"
			                validateOnPost="true"
			                maxlength="3"
			                onKeyUp="checkNumFam();"
			                classNameBase="input"
			    />
			  </td>
			</tr>
			<%}%>
			<%if(isConfigAsAttrib_CAL){ %>
			 <tr>
			   <td>Carico familiare</td>
			  <td>
			    <af:textBox name="numCaricoFam" 
			                value="<%=numCaricoFam %>"
			                size="4"
			                title="Carico familiare"
			                disabled="false"
			                required="false"
			                type="integer"
			                validateOnPost="true"
			                maxlength="3"
			                onKeyUp="checkNumFam();"
			                classNameBase="input"
			    />&nbsp;Indicare numero figli conviventi privi di occupazione
			  </td>
			</tr>
			 <%}%>
			</table>
		<%} else if( (!isConfigAsAttrib && !isConfigAsAttrib_VDA && !isConfigAsAttrib_CAL) || !canUpdate || !canModify){ %>
			<table width="100%">
			<%if(isConfigAsAttrib){   %>
				<tr>
							<td>Punteggio DID</td>
							<td>
								<table colspacing="0" colpadding="0" border="0">
									
										<%if (codMonoDid.equals("S")) {%>
									<tr>
										<td><input type="radio" name="codMonoDid" value="S"
											CHECKED disabled /> In base a dati in SIL&nbsp;&nbsp;</td>
									</tr>
									<tr>
										<td><input type="radio" name="codMonoDid" value="R" disabled />
											Attribuisci punti DID&nbsp;&nbsp;</td>
									</tr>
									<tr>
										<td><input type="radio" name="codMonoDid" value="E" disabled />
											Escludi punti DID</td>
									</tr>
									<tr><td>&nbsp;</td></tr>
										<%} else if (codMonoDid.equals("R"))  {%>
									<tr>
										<td><input type="radio" name="codMonoDid" value="S" disabled 
											CHECKED /> In base a dati in SIL&nbsp;&nbsp;</td>
									</tr>
									<tr>
										<td><input type="radio" name="codMonoDid" value="R" CHECKED disabled />
											Attribuisci punti DID&nbsp;&nbsp;</td>
									</tr>
									<tr>
										<td><input type="radio" name="codMonoDid" value="E" disabled/>
											Escludi punti DID</td>
									</tr>
									<tr><td>&nbsp;</td></tr>
										<%} else if (codMonoDid.equals("E"))  {%>
									<tr>
										<td><input type="radio" name="codMonoDid" value="S" disabled
											 /> In base a dati in SIL&nbsp;&nbsp;</td>
									</tr>
									<tr>
										<td><input type="radio" name="codMonoDid" value="R" disabled />
											Attribuisci punti DID&nbsp;&nbsp;</td>
									</tr>
									<tr>
										<td><input type="radio" name="codMonoDid" value="E" CHECKED disabled />
											Escludi punti DID</td>
									</tr>
									<tr><td>&nbsp;</td></tr>
										<%}%>
								</table>
							</td>
						</tr>
				<%}%>
						<tr>
							<td>Punteggio ISEE</td>
							<td>
								<table colspacing="0" colpadding="0" border="0">
										<%if (codMonoIsee.equals("S")) {%>
										<tr>
										<td><input type="radio" name="codMonoIsee" value="S" disabled
											CHECKED /> In base a dati in SIL&nbsp;&nbsp;</td>
									</tr>
									<tr>
										<td><input type="radio" name="codMonoIsee" value="E" disabled/>
											Escludi ISEE</td>
									</tr>
									<tr><td>&nbsp;</td></tr>
										<%} else if (codMonoIsee.equals("E"))  {%>
										<tr>
										<td><input type="radio" name="codMonoIsee" value="S" disabled
											 /> In base a dati in SIL&nbsp;&nbsp;</td>
									</tr>
									<tr>
										<td><input type="radio" name="codMonoIsee" value="E" CHECKED disabled />
											Escludi ISEE</td>
									</tr>
									<tr><td>&nbsp;</td></tr>
										<%}%>
									</table>
							</td>
						</tr>
			 <%if(isConfigAsAttrib_VDA){ %>
			 <tr>
				<td>Familiare di lavoratore deceduto per incidente sul lavoro</td>
			    <td colspan="2">
			    	<table colspacing="0" colpadding="0" border="0">
			      		<tr>
			      			<%if (flgFamDec!= null && flgFamDec.equals("S")) {%>
			       				<td>
			       					<input type="radio" disabled onclick="cambioValore();" name="flgFamDec" value="S" CHECKED /> Sì&nbsp;&nbsp;
			       				</td>
			       				<td>
			       					<input type="radio" disabled onclick="cambioValore();" name="flgFamDec" value="N" /> No
			       				</td>
			       			<%} else if (flgFamDec!= null && flgFamDec.equals("N"))  {%>
			       				<td>
			       					<input type="radio" disabled onclick="cambioValore();" name="flgFamDec" value="S"/> Sì&nbsp;&nbsp;
			       				</td>
			       				<td>
			       					<input type="radio" disabled onclick="cambioValore();" name="flgFamDec" value="N" CHECKED /> No
			       				</td>
			      			<%}else {%>
			      				<td>
			       					<input type="radio" disabled onclick="cambioValore();" name="flgFamDec" value="S"/> Sì&nbsp;&nbsp;
			       				</td>
			       				<td>
			       					<input type="radio" disabled onclick="cambioValore();" name="flgFamDec" value="N"/> No
			       				</td>
			      			<%}%>
			        	</tr>
			        </table>
			    </td>
			</tr>
			<tr>
				<td>Professionalità acquisita</td>
			    <td colspan="2">
			    	<table colspacing="0" colpadding="0" border="0">
			      		<tr>
			      			<%if (flgProfessionalita!= null && flgProfessionalita.equals("S")) {%>
			       				<td>
			       					<input type="radio" disabled onclick="cambioValore();" name="flgProfessionalita" value="S" CHECKED /> Sì&nbsp;&nbsp;
			       				</td>
			       				<td>
			       					<input type="radio" disabled onclick="cambioValore();" name="flgProfessionalita" value="N" /> No
			       				</td>
			       			<%} else if (flgProfessionalita!= null && flgProfessionalita.equals("N"))  {%>
			       				<td>
			       					<input type="radio" disabled onclick="cambioValore();" name="flgProfessionalita" value="S"/> Sì&nbsp;&nbsp;
			       				</td>
			       				<td>
			       					<input type="radio" disabled onclick="cambioValore();" name="flgProfessionalita" value="N" CHECKED /> No
			       				</td>
			      			<%}else {%>
			      				<td>
			       					<input type="radio" disabled onclick="cambioValore();" name="flgProfessionalita" value="S"/> Sì&nbsp;&nbsp;
			       				</td>
			       				<td>
			       					<input type="radio" disabled onclick="cambioValore();" name="flgProfessionalita" value="N"/> No
			       				</td>
			      			<%}%>
			        	</tr>
			        </table>
			    </td>
			</tr>
			<tr>
			  <td>Carico familiare</td>
			 <td colspan="2">
			    <af:textBox name="numCaricoFam" 
			                value="<%=numCaricoFam %>"
			                size="4"
			                title="Carico familiare"
			                disabled="true"
			                required="false"
			                type="integer"
			                validateOnPost="true"
			                maxlength="3"
			                onKeyUp="checkNumFam();"
			                classNameBase="input"
			    />
			  </td>
			</tr>
			<%}%>
			<%if(isConfigAsAttrib_CAL){ %>
				<tr>
			  	<td>Carico familiare</td>
			  	<td>
			  	<af:textBox name="numCaricoFam" 
			                value="<%=numCaricoFam %>"
			                size="4"
			                title="Carico familiare"
			                disabled="true"
			                required="false"
			                type="integer"
			                validateOnPost="true"
			                maxlength="3"
			                onKeyUp="checkNumFam();"
			                classNameBase="input"
			    />
			  	</td>
			  	</tr>
			<%}%>
			</table>
		<%}%>
		</td>
		</tr>
		</table>
 <br/> 
		<center>
	<%   if ( (isConfigAsAttrib || isConfigAsAttrib_VDA || isConfigAsAttrib_CAL) && canUpdate && canModify){%>
         <input type="submit" class="pulsanti"  name = "inserisciNuovo" value="Aggiorna" >
          <br/> <br/>
    <%
		}
		if(isFromLav){
	%>
 	  <input class="pulsante" type="button" name="back" value="Torna alla lista" onclick="goBack();"/>
	
	<%   }  %>
	 
    </center>
<br>       

</af:form>
<%out.print(htmlStreamBottom);%>
</body>
</html>