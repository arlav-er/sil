<%@ page contentType="text/html;charset=utf-8"%>

<%@ page
	import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  it.eng.sil.security.User,
                  it.eng.sil.security.ProfileDataFilter,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.module.anag.profiloLavoratore.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  java.lang.*,
                  java.text.*"%>

<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>


<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%@ taglib uri="aftags" prefix="af"%>

<%
String _page = (String) serviceRequest.getAttribute("PAGE");

String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
PageAttribs attributi = new PageAttribs(user,_page);

ProfileDataFilter filter = new ProfileDataFilter(user, _page);
 
if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	return;
} 
%>
<%
	boolean canSave = attributi.containsButton("SALVA");
	boolean canModify = canSave;
	boolean readOnlyStr = !canModify;
 	boolean canCalcolaProfilo = attributi.containsButton("CALCOLA");
 	String tipoOperazione = "";
	String titlePagina = "Informazioni Generali Profilo Lavoratore";
 	String dataRiferimento =DateUtils.getNow();
 	String flgLinguaIta = null;
 	String statoCompilazione =""; 
 	String statoCompilazioneDescr ="Nuovo profilo (da compilare)";
	String prgLavoratoreProfilo = "";
	String valoreCalcolato = null;
	String dimensione1 = null;
	String dimensione2 = null;
	String dimensione3 = null;
	String dimensione4 = null;
	String dimensione5 = null;
	String dimensione6 = null;
	String dimensione7 = null;
	boolean bdimensione1 = true;
	boolean bdimensione2 = true;
	boolean bdimensione3 = true;
	boolean bdimensione4 = true;
	boolean bdimensione5 = true;
	boolean bdimensione6 = true;
	boolean bdimensione7 = true;
	String strVchProfiling = null;
 	int numRisPers = -1;
	
	Vector dimensioniNonAttive = (Vector) serviceResponse.getAttributeAsVector("M_GetDimensioniNonAttive.ROWS.ROW");
	if(dimensioniNonAttive != null && dimensioniNonAttive.size() > 0){
		
		if(dimensioniNonAttive.size() == 8){
			canCalcolaProfilo = false;
		}
		for (int dim=0;dim<dimensioniNonAttive.size() ;dim++) {
			
			SourceBean dimensioneBean = (SourceBean)dimensioniNonAttive.get(dim);
			
			String iesimaDim = dimensioneBean.getAttribute("Coddomandaprofragg").toString();
			if(iesimaDim.endsWith("1")){
				dimensione1 = "NON ATTIVA: Non Calcolato";
				bdimensione1 = false;
			}
			if(iesimaDim.endsWith("2")){
				dimensione2 = "NON ATTIVA: Non Calcolato";
				bdimensione2 = false;
			}
			if(iesimaDim.endsWith("3")){
				dimensione3 = "NON ATTIVA: Non Calcolato";
				bdimensione3 = false;
			}
			if(iesimaDim.endsWith("4")){
				dimensione4 = "NON ATTIVA: Non Calcolato";
				bdimensione4 = false;
			}
			if(iesimaDim.endsWith("5")){
				dimensione5 = "NON ATTIVA: Non Calcolato";
				bdimensione5 = false;
			}
			if(iesimaDim.endsWith("6")){
				dimensione6 = "NON ATTIVA: Non Calcolato";
				bdimensione6 = false;
			}
			if(iesimaDim.endsWith("7")){
				dimensione7 = "NON ATTIVA: Non Calcolato";
				bdimensione7 = false;
			}
		}
	}
	boolean consultaProfilo = serviceRequest.containsAttribute("PRGLAVORATOREPROFILO") || serviceResponse.containsAttribute("M_SALVAFLGLINGUAPROFILO.PRGLAVORATOREPROFILO");
	
 	SourceBean infoProfilo = null;
 	boolean skipDb = false;
	if (consultaProfilo) {
		prgLavoratoreProfilo = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGLAVORATOREPROFILO") ;
		if(StringUtils.isEmptyNoBlank(prgLavoratoreProfilo)){
			BigDecimal prgLP =(BigDecimal) serviceResponse.getAttribute("M_SALVAFLGLINGUAPROFILO.PRGLAVORATOREPROFILO");
			prgLavoratoreProfilo = prgLP.toPlainString() ;
		}
 		infoProfilo =(SourceBean) serviceResponse.getAttribute("M_GetInfoGeneraliProfilo.ROWS.ROW");
		if(infoProfilo != null){
			statoCompilazione =(String) infoProfilo.getAttribute("codmonostatoprof");
			if(StringUtils.isFilledNoBlank(statoCompilazione) && statoCompilazione.equalsIgnoreCase(Decodifica.StatoProfilo.CHIUSO_CALCOLATO)){
				canModify = false;
				readOnlyStr = !canModify;
				statoCompilazioneDescr = "Chiuso/Calcolato";
			}else if(StringUtils.isFilledNoBlank(statoCompilazione) && statoCompilazione.equalsIgnoreCase(Decodifica.StatoProfilo.IN_CORSO)){
 				canModify = true;
				statoCompilazioneDescr = "In compilazione";
				if(serviceResponse.containsAttribute("M_CalcolaProfilo.VALORE_CALCOLO")){
					skipDb = true;
					valoreCalcolato = serviceResponse.getAttribute("M_CalcolaProfilo.VALORE_CALCOLO").toString();
					if(bdimensione1){
						dimensione1 =  serviceResponse.getAttribute("M_CalcolaProfilo.Numdim01").toString();
					}if(bdimensione2){
						dimensione2 =  serviceResponse.getAttribute("M_CalcolaProfilo.Numdim02").toString();
					}if(bdimensione3){
						dimensione3 =  serviceResponse.getAttribute("M_CalcolaProfilo.Numdim03").toString();
					}if(bdimensione4){
						dimensione4 =  serviceResponse.getAttribute("M_CalcolaProfilo.Numdim04").toString();
					}if(bdimensione5){
						dimensione5 =  serviceResponse.getAttribute("M_CalcolaProfilo.Numdim05").toString();
					}if(bdimensione6){
						dimensione6 =  serviceResponse.getAttribute("M_CalcolaProfilo.Numdim06").toString();
					}if(bdimensione7){
						dimensione7 =  serviceResponse.getAttribute("M_CalcolaProfilo.Numdim07").toString();
					} 
					strVchProfiling = serviceResponse.getAttribute("M_CalcolaProfilo.indiceProfilo").toString();
				}
			}
			dataRiferimento = (String) infoProfilo.getAttribute("DATCREAZIONEPROFILO");
			flgLinguaIta = StringUtils.getAttributeStrNotNull(infoProfilo, "Flgconoscenzaita");

			if(!skipDb && infoProfilo.containsAttribute("NUMVALOREPROFILO")){
				valoreCalcolato = (String) infoProfilo.getAttribute("NUMVALOREPROFILO");
				if(bdimensione1){
					dimensione1 = (String) infoProfilo.getAttribute("Numdim01");
				}if(bdimensione2){
					dimensione2 = (String) infoProfilo.getAttribute("Numdim02");
				}if(bdimensione3){
					dimensione3 = (String) infoProfilo.getAttribute("Numdim03");
				}if(bdimensione4){
					dimensione4 = (String) infoProfilo.getAttribute("Numdim04");
				}if(bdimensione5){
					dimensione5 = (String) infoProfilo.getAttribute("Numdim05");
				}if(bdimensione6){
					dimensione6 = (String) infoProfilo.getAttribute("Numdim06");
				}if(bdimensione7){
					dimensione7 = (String) infoProfilo.getAttribute("Numdim07");
				}strVchProfiling = (String) infoProfilo.getAttribute("indiceProfilo");
			}
			if(bdimensione4 && StringUtils.isFilledNoBlank(dimensione4) && dimensione4.startsWith("0")){
				dimensione4 = "ATTIVA: Non Calcolato";
			}
		}
		//segnalazione 5188 rendere non obbligatorie le risposte sulla personalità
		boolean consultaRisPersonalita = serviceResponse.containsAttribute("M_GetNumPersonalita.NUM_PERSONALITA");
 		if(consultaRisPersonalita){
			Integer numRis = (Integer) serviceResponse.getAttribute("M_GetNumPersonalita.NUM_PERSONALITA");
			numRisPers = numRis.intValue();
		}
	}else{
		canCalcolaProfilo = false;
	}
		
	String htmlStreamTop = StyleUtils.roundTopTable(true);
	String htmlStreamBottom = StyleUtils.roundBottomTable(true);

%>
 
	<html>
	<head>
	<title>Profilo Lavoratore</title>
	<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
	<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
	<link rel="stylesheet" type="text/css" href="../../css/progressBar.css" />
	
	<af:linkScript path="../../js/" />
	<%@ include file="../global/fieldChanged.inc" %>
	<script language="Javascript" src="../../js/progressBar.js"></script>
	
  	<script language="Javascript">
	
    	 window.top.menu.caricaMenuLav(<%=_funzione%>,<%=cdnLavoratore%>);

	</script>
	
	<script type="text/Javascript">
		<%//Genera il Javascript che si occuperà di inserire i links nel footer
		if (!cdnLavoratore.equals(""))
			attributi.showHyperLinks(out, requestContainer, responseContainer, "cdnLavoratore=" + cdnLavoratore);
		%>	
		function rinfrescaProfili(){
			rinfresca();
			<% if(consultaProfilo){ %>
				resizeProgressBar('progressDim1', '<%=dimensione1%>');
				resizeProgressBar('progressDim2', '<%=dimensione2%>');
				resizeProgressBar('progressDim3', '<%=dimensione3%>');
				resizeProgressBar('progressDim4', '<%=dimensione4%>');
				resizeProgressBar('progressDim5', '<%=dimensione5%>');
				resizeProgressBar('progressDim6', '<%=dimensione6%>');
				resizeProgressBar('progressDim7', '<%=dimensione7%>');
			<% }%>
		} 

	</script>
	<%@ include file="controlliProfiliLavoratore.inc" %> 
	</head>
	<body class="gestione" onload="rinfrescaProfili();">
	
	<%
	InfCorrentiLav testata = new InfCorrentiLav(RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
	testata.show(out);

	// LINGUETTE LAVORATORE
	if (StringUtils.isFilled(cdnLavoratore) && consultaProfilo) {
		Linguette linguette = new Linguette(user, (new Integer(_funzione)).intValue(), _page, new BigDecimal(cdnLavoratore), new BigDecimal(prgLavoratoreProfilo));
		linguette.setCodiceItemAggiuntivo("PRGLAVORATOREPROFILO");
		linguette.show(out);
	}else if (StringUtils.isFilled(cdnLavoratore)) {
		Linguette linguette = new Linguette(user, (new Integer(_funzione)).intValue(), _page, new BigDecimal(cdnLavoratore));
 		linguette.show(out);
	}
	%>
	
	<p>
	 	<font color="green">
	 		<af:showMessages prefix="M_CalcolaProfilo"/>
	 		<af:showMessages prefix="M_SalvaFlgLinguaProfilo"/>
	  	</font>
	  	<font color="red"><af:showErrors /></font>
	</p>

	<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="checkChangedCampi() && checkSumbitCalcolo()">
		
		<% out.print(htmlStreamTop); %>
		<br>
	 	<table class="main">
	 		<tr>
				<td class="etichetta" nowrap style="text-align: left !important;">Stato profilo&nbsp;</td>
				<td class="campo"  style="width: initial;">
				<af:textBox name="statoCompilazioneDescr" type="text" title="Stato profilo" readonly="true"
					 classNameBase="input"	size="25" value="<%=statoCompilazioneDescr%>" />
	            </td>
	            <td style="width: 100%;"></td>
			</tr>
			<tr>
				<td class="etichetta" nowrap style="text-align: left !important;">Data riferimento profilo&nbsp;</td>
				<td class="campo"  style="width: initial;">
				<af:textBox name="dataRiferimento" type="date" title="Data riferimento profilo" readonly="true"
					size="11" maxlength="10" classNameBase="input"	value="<%=dataRiferimento%>" />
	            </td><td style="width: 100%;"></td>
			</tr>
			<tr>
				<td class="etichetta" nowrap style="text-align: left !important;">Conoscenza lingua italiana&nbsp;</td>
				<td class="campo"  style="width: initial;">
				<af:comboBox name="flgLinguaIta"
			  	onChange="fieldChanged();"
               title="Conoscenza lingua italiana"
               required="false"
               moduleName="M_ComboSiNo"
               selectedValue="<%=flgLinguaIta%>"
               addBlank="true"
               classNameBase="input"
               disabled="<%= String.valueOf(readOnlyStr) %>"
               blankValue=""/>
	            </td><td style="width: 100%;"></td>
			</tr>

			<% if(consultaProfilo){ %>
				<tr>
					<td class="etichetta" nowrap style="text-align: left !important;">Valore profilo&nbsp;</td>
					<td class="campo" style="width: initial;">
					<af:textBox name="valoreProf" type="date" title="Valore profilo" readonly="true"
						size="30"   classNameBase="input"	value="<%=Utils.notNull(valoreCalcolato)%>" />
		            </td><td style="width: 100%;"></td>
				</tr>
				<tr>
					<td class="etichetta" nowrap style="text-align: left !important;">Indice profilatura&nbsp;</td>
					<td class="campo" style="width: initial;">
					<af:textBox name="indiceProf" type="date" title="Indice profilatura" readonly="true"
						size="30"   classNameBase="input"	value="<%=Utils.notNull(strVchProfiling)%>" />
		            </td><td style="width: 100%;"></td>
				</tr>
	
				<tr>
					<td class="etichetta"></td>
					<td class="campo" style="width: initial;"></td>
					<td style="width: 100%;"></td>
				</tr>
				<tr>
					<td class="etichetta" nowrap style="text-align: left !important;">Dimensione 1 - Profilo socio anagrafico&nbsp;</td>
					
		           <td class="campo" style="width: initial;">
		            	<div  class="bar"><div id="progressDim1" class="progress"></div></div>
		            </td>
		            <td class="campo" style="width: 100%;">
					<af:textBox name="dim1" type="date" title="Dimensione 1" readonly="true"
						size="30"  classNameBase="input"	value="<%=Utils.notNull(dimensione1)%>" />
		            </td>
				</tr>
				<tr>
					<td class="etichetta" nowrap style="text-align: left !important;">Dimensione 2 - Risorse professionali e coerenza&nbsp;</td>
					<td class="campo" style="width: initial;">
					
		            
		             <div  class="bar"><div id="progressDim2" class="progress"></div></div>
		            </td> <td class="campo"  style="width: 100%;">
		            	<af:textBox name="dim2" type="date" title="Dimensione 2" readonly="true"
						size="30"  classNameBase="input"	value="<%=Utils.notNull(dimensione2)%>" />
		            	
		            </td>
				</tr>
				<tr>
					<td class="etichetta" nowrap style="text-align: left !important;">Dimensione 3 - Qualifica ricercata&nbsp;</td>
					<td class="campo" style="width: initial;">
					<div  class="bar"><div id="progressDim3" class="progress"></div></div>
		            </td>
		             <td class="campo"  style="width: 100%;">
		             <af:textBox name="dim3" type="date" title="Dimensione 3" readonly="true"
						size="30" classNameBase="input"	value="<%=Utils.notNull(dimensione3)%>" />
		            	
		            </td>
				</tr>
				<tr>
					<td class="etichetta" nowrap style="text-align: left !important;">Dimensione 4 - Comportamenti di ricerca&nbsp;</td>
					<td class="campo" style="width: initial;">
						<div  class="bar"><div id="progressDim4" class="progress"></div></div>
		            </td>
		             <td class="campo"  style="width: 100%;">
		             <af:textBox name="dim4" type="date" title="Dimensione 4" readonly="true"
						size="30"  classNameBase="input"	value="<%=Utils.notNull(dimensione4)%>" />
		            
		            </td>
				</tr>
				<tr>
					<td class="etichetta" nowrap style="text-align: left !important;">Dimensione 5 - Barriere personali&nbsp;</td>
					<td class="campo" style="width: initial;">
					<div  class="bar"><div id="progressDim5" class="progress"></div></div>
		            </td>
		             <td class="campo"  style="width: 100%;">
		             <af:textBox name="dim5" type="date" title="Dimensione 5" readonly="true"
						size="30"  classNameBase="input"	value="<%=Utils.notNull(dimensione5)%>" />
		            	
		            </td>
				</tr>
				<tr>
					<td class="etichetta" nowrap style="text-align: left !important;">Dimensione 6 - Atteggiamenti (self efficacy)&nbsp;</td>
					<td class="campo" style="width: initial;">
					<div  class="bar"><div id="progressDim6" class="progress"></div></div>
				
		            </td>
		             <td class="campo"  style="width: 100%;">
		            		<af:textBox name="dim6" type="date" title="Dimensione 6" readonly="true"
						size="30"  classNameBase="input"	value="<%=Utils.notNull(dimensione6)%>" />
		            </td>
				</tr>
				<tr>
					<td class="etichetta" nowrap style="text-align: left !important;">Dimensione 7 - Parere esperto&nbsp;</td>
					<td class="campo" style="width: initial;">
					<div  class="bar"><div id="progressDim7" class="progress"></div></div>
				
		            </td>
		             <td class="campo"  style="width: 100%;">
		            		<af:textBox name="dim7" type="date" title="Dimensione 7" readonly="true"
						size="30"  classNameBase="input"	value="<%=Utils.notNull(dimensione7)%>" />
		            </td>
				</tr>
	 		<% } %>
		</table>
 
		<%if(canModify && canCalcolaProfilo){ %>
			<br>
          		<table align="center">
            		<tr align="center">
              		<td align="center">
                  	<input type="submit" onclick="onClickCalcola('CALCOLA');"  class="pulsanti" name="calcolaProfilo" value="Calcola Profilo">
                	</td>
                	<td align="center">
                  	<input type="submit" onclick="onClickCalcola('CALCOLA_SALVA');" class="pulsanti" name="calcolaProfilo" value="Calcola Profilo e Chiudi">
                	</td>
              		</tr>
              	</table>
             <%}%> 
             <%if(canSave){ %>
             <br>
          		<table align="center">
            		<tr align="center">
            		<td align="center">
                  	<input type="submit" onclick="onClickCalcola('SALVA_FLGLINGUA');"  class="pulsanti<%=((readOnlyStr)?"Disabled":"")%>" name="salvaFlgLingua" value="Salva" <%=(readOnlyStr)?"disabled=\"true\"":""%>>
                	</td>
              		<td align="center">
                  	<input type="button" onclick="calcolaScore();" class="pulsanti" name="calcolaProfilo" value="Score Personalità" >
                	</td>
              		</tr>
              	</table>
		<%}
		out.print(htmlStreamBottom); 
		%>
		<input type="hidden" name="PAGE" value="<%=Utils.notNull(_page)%>">
		<input type="hidden" name="cdnFunzione" value="<%=Utils.notNull(_funzione)%>">
		<input type="hidden" name="cdnLavoratore" value="<%=Utils.notNull(cdnLavoratore)%>">
		<%if(StringUtils.isFilledNoBlank(prgLavoratoreProfilo)){%>
			<input type="hidden" name="PRGLAVORATOREPROFILO" value="<%=prgLavoratoreProfilo%>">
		<%}%>
		<input type="hidden" name="TIPO_OPERAZIONE" value="<%=Utils.notNull(tipoOperazione)%>">

	</af:form>
	<%	boolean existPrecompilato = false; %>
	<%@ include file="cambioLinguetta.inc" %>
	</body>
	</html>