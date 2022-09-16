<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>


<%@ page
	import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.Linguette,
                  java.math.BigDecimal,
                  com.engiweb.framework.security.*,
                  it.eng.sil.util.InfCorrentiLav"%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>
<% 
String pageToProfile = "RicercaTemplatePage";

ProfileDataFilter filter = new ProfileDataFilter(user, pageToProfile);
if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
}
// NOTE: Attributi della pagina (pulsanti e link) 
PageAttribs attributi = new PageAttribs(user, pageToProfile);
    String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");

    //String cdnLavoratore = (String) serviceRequest.getAttribute("CDNLAVORATORE");
    
	String STRNOME = StringUtils.getAttributeStrNotNullStr(serviceRequest,"STRNOME");
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
	String codAmbitoTem = StringUtils.getAttributeStrNotNull(serviceRequest, "codAmbitoTem");
	String codTipoDominio = StringUtils.getAttributeStrNotNull(serviceRequest, "codTipoDominio");
	String dataInizio = StringUtils.getAttributeStrNotNullStr(serviceRequest, "DATINIZIOVAL");
	String dataFine = StringUtils.getAttributeStrNotNullStr(serviceRequest, "DATFINEVAL");
	sessionContainer.delAttribute("TEMPLATECACHE");
	
 	Vector rowsClassificazioni = serviceResponse.getAttributeAsVector("ComboClassificazioni.ROWS.ROW");
 	SourceBean row = null;
 	java.math.BigDecimal prgClassif = new java.math.BigDecimal(0);
 	java.math.BigDecimal prgTemplateStampa = new java.math.BigDecimal(0);
 	java.math.BigDecimal prgConfigProt = new java.math.BigDecimal(0);
 	String nomeClassif = "";
 	String nomeStampa = "";
 	String actualClassif = "";
 	String codTipoDominioLista = "";
 	
 	if (StringUtils.isEmpty(codTipoDominio)){
	    if(serviceResponse.containsAttribute("ComboPrgTipoDominio")){
	    	Vector listeDominioDati = serviceResponse.getAttributeAsVector("ComboPrgTipoDominio.ROWS.ROW");
	    	SourceBean dominioDato  = (SourceBean) listeDominioDati.elementAt(0);
	        String codiceDato = (String) dominioDato.getAttribute("CODICE");
	        codTipoDominio = codiceDato;
	    }
    }
%>
	
<html>
<head>
<title>Ricerca Template</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<af:linkScript path="../../js/" />
<script language="Javascript">
	
<%@ include file="../documenti/RicercaCheck.inc" %>
	function go(url, alertFlag) {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit())
			return;

		var _url = "AdapterHTTP?" + url;
		if (alertFlag == 'TRUE') {
			if (confirm('Confermi operazione')) {
				setWindowLocation(_url);
			}
		} else {
			setWindowLocation(_url);
		}
	}

	function checkCampiObbligatori() {
		return true;
	}

	function valorizzaHidden() {
		document.form1.DESCCODAMBITOTEM.value = 
			document.form1.codAmbitoTem.options[document.form1.codAmbitoTem.selectedIndex].text;
		document.form1.DESCDOMINIO.value = 
			document.form1.codTipoDominio.options[document.form1.codTipoDominio.selectedIndex].text;

		return true;
	}
</script>

<script language="JavaScript" type="text/javascript">
	<!--
		
		function openSelect(){
			var viewClassificazioniObj = document.getElementById('viewClassificazioni');
			var viewClassificazioniObj2 = document.getElementById('lista');
			
			
			if (viewClassificazioniObj.style.display == 'none'){
				viewClassificazioniObj.style.display = 'block';
				viewClassificazioniObj2.style.display = 'table-row';
			} else{
				viewClassificazioniObj.style.display = 'none';
				viewClassificazioniObj2.style.display = 'none';
			}
		}
		
		
		function sizeTbl(t) {
		  var tbl = document.getElementById('template_'+t);

		  var imgFolderClose = document.getElementById('folder_close_'+t);
		  var imgFolderOpen = document.getElementById('folder_open_'+t);
		  
		  if (tbl.style.display == 'none'){
			  tbl.style.display='block';
			  imgFolderClose.style.display='none';
			  imgFolderOpen.style.display='block';
		  } else{ 
			  tbl.style.display = 'none';
			  imgFolderClose.style.display='block';
			  imgFolderOpen.style.display='none';
		  }
		}
		
		
		function valorizeClassif(value, text){
			var doClassificazioneObj = document.getElementById('PRGCLASSIF');
			
			//remove all options from list
			doClassificazioneObj.innerHTML = "";
			
			//add new option
			doClassificazioneObj.options[doClassificazioneObj.options.length] = new Option(text, value);
			
			var viewClassificazioniObj = document.getElementById('viewClassificazioni');
			var viewClassificazioniObj2 = document.getElementById('lista');
			viewClassificazioniObj.style.display = 'none';
			viewClassificazioniObj2.style.display = 'none';
		}
				
	// -->
</script> 


</head>
<body class="gestione" onload="doLoad();">
	<br>
	<p class="titolo">Ricerca Template</p>
	<p align="center">

		<af:form name="form1" action="AdapterHTTP" method="POST"
			onSubmit="checkCampiObbligatori() && valorizzaHidden()">
			<%
				out.print(htmlStreamTop);
			%>
			<table style="border-collapse: collapse;">
				<tr><td colspan="2" />&nbsp;</td></tr>
				<tr>
					<td class="etichetta">Nome</td>
					<td class="campo"><af:textBox type="text" name="STRNOME"
							value="<%=STRNOME%>" size="40" maxlength="100" /></td>
				</tr>
				
				
				
				<tr style='background-color: #e8f3ff;'>
			   		<td class="etichetta">
						Lista per Classificazione
					</td>
					<td class="campo" >
						<SELECT name="PRGCLASSIF" id="PRGCLASSIF" style="width:650px;" onClick="javascript:openSelect();" />
					</td>
			   </tr>
			   
			   <tr style='background-color: #e8f3ff;display: none;top: -2px;position: relative;' id="lista">
			   		<td class="etichetta">
						&nbsp;
					</td>
					<td class="campo">
					
					<table class='master' border=0 id='viewClassificazioni' style='display:none;border-left: 1px solid #e2e3ea;border-right: 1px solid #e2e3ea;border-bottom: 1px solid #e2e3ea;width:650px;border-collapse: collapse;'>
				
						<tr style='background-color: #e8f3ff;'>
							<td width='650px' style='background-color: white;'>
							
								<%
								if (rowsClassificazioni.size() > 0) {
								      for(int i=0; i<rowsClassificazioni.size(); i++)  { 
								        row = (SourceBean) rowsClassificazioni.elementAt(i);
								        
								        prgClassif = (java.math.BigDecimal) row.getAttribute("PRGCLASSIF");
								        prgTemplateStampa = (java.math.BigDecimal) row.getAttribute("PRGTEMPLATESTAMPA");
								        prgConfigProt = (java.math.BigDecimal) row.getAttribute("PRGCONFIGPROT");
								        
								        codTipoDominioLista = (String) row.getAttribute("codTipoDominio");
								        
								        nomeClassif = (String) row.getAttribute("nomeClassif");
								        nomeStampa = (String) row.getAttribute("nomeStampa");
								        
								        if (!actualClassif.equalsIgnoreCase(nomeClassif)){
								        	actualClassif = nomeClassif;
						
											if(i>0){%>
														</div>
													</td>
												</tr>
											</table>
											<table>
												<tr>
													<td style="vertical-align:top;">
														<a href="javascript:sizeTbl('<%=i%>')" style="color: black;">
															<img id='folder_close_<%=i%>' src='../../img/folder_0.png' style='display:block;' />
															<img id='folder_open_<%=i%>' src='../../img/folder_1.png' style='display:none;' />
														</a>
													</td>
													
													<td>
														<!-- <a href="javascript:sizeTbl('<%=i%>')" style="color: black;"> -->
														<a href="javascript:valorizeClassif('<%=prgClassif%>', '<%=nomeClassif%>')" style="color: black;">
														<!-- <a href="javascript:go('PAGE=GestTemplatePage&MODULE=MListaTemplate&PRGCLASSIF=<%=prgClassif%>&nomeClassif=<%=actualClassif%>&cdnFunzione=<%=cdnFunzione%>&codTipoDominio=<%=codTipoDominioLista%>&backfrom=ricerca', 'false');"  title="Ricerca i Template associati a questa Classificazione"> -->
															<%=actualClassif%>
														</a>
													
														<div id='template_<%=i%>' style='display:none;'>
															<p>
																<span width='5%'>- </span>
																<!-- <a href="javascript:go('PAGE=DettaglioTemplatePage&MODULE=MDettaglioTemplate&MOSTRATEMPLATE=S&PRGTEMPLATESTAMPA=<%=prgTemplateStampa%>&PRGCONFIGPROT=<%=prgConfigProt%>&cdnFunzione=<%=cdnFunzione%>&codTipoDominio=<%=codTipoDominioLista%>&backfrom=ricerca', 'false');"  title="vai alla pagina di Dettaglio del Template"> -->
																	<span width='95%' style='text-decoration: none;'> <%=nomeStampa%></span>
																<!-- </a> -->
															</p>
														
											<%} else{ %>
											<table>
												<tr>
													<td style="vertical-align:top;">
														<a href="javascript:sizeTbl('<%=i%>')" style="color: black;">
															<img id='folder_close_<%=i%>' src='../../img/folder_0.png' style='display:block;' />
															<img id='folder_open_<%=i%>' src='../../img/folder_1.png' style='display:none;' />
														</a>
													</td>
													
													<td>
														<!-- <a href="javascript:sizeTbl('<%=i%>')" style="color: black;"> -->
														<a href="javascript:valorizeClassif('<%=prgClassif%>', '<%=nomeClassif%>')" style="color: black;">
														<!-- <a href="javascript:go('PAGE=GestTemplatePage&MODULE=MListaTemplate&PRGCLASSIF=<%=prgClassif%>&nomeClassif=<%=actualClassif%>&cdnFunzione=<%=cdnFunzione%>&codTipoDominio=<%=codTipoDominioLista%>&backfrom=ricerca', 'false');"  title="Ricerca i Template associati a questa Classificazione"> -->
															<%=actualClassif%>
														</a>
													
														<div id='template_<%=i%>' style='display:none;'>
															<p>
																<span width='5%'>- </span>
																<!-- <a href="javascript:go('PAGE=DettaglioTemplatePage&MODULE=MDettaglioTemplate&MOSTRATEMPLATE=S&PRGTEMPLATESTAMPA=<%=prgTemplateStampa%>&PRGCONFIGPROT=<%=prgConfigProt%>&cdnFunzione=<%=cdnFunzione%>&codTipoDominio=<%=codTipoDominioLista%>&backfrom=ricerca', 'false');" title="vai alla pagina di Dettaglio del Template"> -->
																	<span width='95%' style='text-decoration: none;'> <%=nomeStampa%></span>
																<!-- </a> -->
															</p>
														
											<%} //if >0 %>
										
										<% } else{ %>
										
															<p>
																<span width='5%'>- </span>
																<!-- <a href="javascript:go('PAGE=DettaglioTemplatePage&MODULE=MDettaglioTemplate&MOSTRATEMPLATE=S&PRGTEMPLATESTAMPA=<%=prgTemplateStampa%>&PRGCONFIGPROT=<%=prgConfigProt%>&cdnFunzione=<%=cdnFunzione%>&codTipoDominio=<%=codTipoDominioLista%>&backfrom=ricerca', 'false');" title="vai alla pagina di Dettaglio del Template"> -->
																	<span width='95%' style='text-decoration: none;'> <%=nomeStampa%></span>
																<!-- </a> -->
															</p>
										
										<% } // else %>
									
									<% } //for %>
													</div>
												</td>
											</tr>
										</table>
								
								<% } //if size > 0 %>
								
								</td>
							</tr>
						</table>
					</td>
				</tr>
				
				
				
				<tr>
					<td class="etichetta">Tipo documento</td>
					<td class="campo"><af:comboBox name="codAmbitoTem"
							moduleName="ComboTipoDocumento" selectedValue="<%=codAmbitoTem%>"
							addBlank="true" /></td>
				</tr>

				<tr>
					<td class="etichetta">Dominio dati</td>
					<td class="campo"><af:comboBox name="codTipoDominio"
							moduleName="ComboPrgTipoDominio" required="true" title="Dominio dati"
							selectedValue="<%=codTipoDominio%>" /></td>
				</tr>

				<tr>
					<td class="etichetta">Data validita' da</td>
					<td><af:textBox type="date" name="DATINIZIOVAL"
							title="Data validita' da" value="<%=dataInizio%>" size="12" maxlength="10"
							validateOnPost="true" /> &nbsp;&nbsp;&nbsp;&nbsp;a &nbsp;&nbsp;
						<af:textBox
							type="date" name="DATFINEVAL" value="<%=dataFine%>" size="12" maxlength="10"
							title="Data a" validateOnPost="true" /></td>
				</tr>

				<tr>
						<td colspan="2">
							<input type="hidden" name="DESCCODAMBITOTEM" value="" />
							<input type="hidden" name="DESCDOMINIO" value="" />
							<input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
							<input type="hidden" name="MODULE" value="MListaTemplate">&nbsp;
							<input type="hidden" name="PAGE" value="GestTemplatePage">&nbsp;
						</td>
				</tr>
				<tr>
					<td colspan="2" align="center"><input type="submit"
						class="pulsanti" name="Invia" value="Cerca"> &nbsp;&nbsp;
						<input name="reset" type="reset" class="pulsanti" value="Annulla">
						&nbsp;&nbsp;</td>
				</tr>

			</table>
			<%out.print(htmlStreamBottom);%> 
		</af:form>
</body>
</html>