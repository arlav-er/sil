<%
 
	boolean canSave = attributi.containsButton("SALVA");
	boolean canModify = canSave;
	boolean readOnlyStr = !canModify;
	String titoloBottoneSave = "Salva";
 	String codMansione= "",  descMansione= "", desTipoMansione="";
	String tipoOperazione = "";
	String titlePagina = "";
	int numRisPers = -1;
	String prgLavoratoreProfilo = "";
	int numDomande = 0;
	Vector domandeL1 = null;

	boolean consultaProfilo = serviceRequest.containsAttribute("PRGLAVORATOREPROFILO");
	if(!consultaProfilo){
		consultaProfilo = serviceResponse.containsAttribute("M_SalvaProfiloLinguetta.PRGLAVORATOREPROFILO");
	}
	if (consultaProfilo) {
		titlePagina = "Dettaglio profilo";
		titoloBottoneSave = "Aggiorna";
		prgLavoratoreProfilo = serviceRequest.containsAttribute("PRGLAVORATOREPROFILO")? serviceRequest.getAttribute("PRGLAVORATOREPROFILO").toString() :
			serviceResponse.getAttribute("M_SalvaProfiloLinguetta.PRGLAVORATOREPROFILO").toString();
		
		SourceBean infoProfilo =(SourceBean) serviceResponse.getAttribute("M_GetInfoGeneraliProfilo.ROWS.ROW");
		if(infoProfilo != null){
			String statoCompilazione =(String) infoProfilo.getAttribute("codmonostatoprof");
			if(StringUtils.isFilledNoBlank(statoCompilazione) && statoCompilazione.equalsIgnoreCase(Decodifica.StatoProfilo.CHIUSO_CALCOLATO)){
				canModify = false;
				readOnlyStr = !canModify;
			}
		}
		domandeL1 = (Vector) serviceResponse.getAttributeAsVector("M_GetDomandeRisposteLinguetta.ROWS.ROW");
	}
	else {
 		domandeL1 = (Vector) serviceResponse.getAttributeAsVector("M_GetDomandeLinguetta.ROWS.ROW");
	 
		titlePagina = "Nuovo profilo";	
	}
	numDomande = domandeL1.size();
	
	
	String htmlStreamTop = StyleUtils.roundTopTable(true);
	String htmlStreamBottom = StyleUtils.roundBottomTable(true);

%>
 
	<html>
	<head>
	<title>Profilo Lavoratore</title>
	<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
	<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
	<af:linkScript path="../../js/" />
	<%@ include file="../global/fieldChanged.inc" %>
	
	<script type="text/Javascript">
		<%//Genera il Javascript che si occuperà di inserire i links nel footer
		if (!cdnLavoratore.equals(""))
			attributi.showHyperLinks(out, requestContainer, responseContainer, "cdnLavoratore=" + cdnLavoratore);
		%>	
 
	</script>
	
 	<%@ include file="controlliProfiliLavoratore.inc" %>
	</head>
	<%@ include file="../presel/Mansioni_CommonScripts.inc" %>
	<body class="gestione" onload="gestisciViewDomande();">
	
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
	 		<af:showMessages prefix="M_SalvaProfiloLinguetta"/>
	  	</font>
	  	<font color="red"><af:showErrors /></font>
	</p>
	
	<af:form method="POST" action="AdapterHTTP" name="Frm1"  onSubmit="checkSumbitCalcolo()">
		
		<% out.print(htmlStreamTop); %>
		<br>
		
	 	<table class="main">
	 	<%if(StringUtils.isFilledNoBlank(linguetta) && linguetta.equalsIgnoreCase("8")){ %>
	 		<tr><td class="campo" style="font-weight: bold;">QUESTIONARIO PERSONALITA'</td></tr>
	 		<tr/>
	 		<tr><td class="campo" style="font-weight: bold;"> Mi vedo come una persona che...</td></tr> 
		<%}%>	
		
		<% String codDomandaProfPrev= "";
		String tipoDomaPrev ="";
		for (int num=0;num<numDomande;num++) {
			
			boolean newDomanda = false;
			SourceBean domandaBean = (SourceBean)domandeL1.get(num);
			String codDomandaProf = domandaBean.getAttribute("codDomandaProf").toString();
			String tipoDomanda =  domandaBean.getAttribute("strTipoInput").toString();
			BigDecimal posizioneRisposta = (BigDecimal) domandaBean.getAttribute("posizioneRisposta");
			if(num==0){
				codDomandaProfPrev = codDomandaProf;
				newDomanda= true;
 			}else{
				if(!codDomandaProfPrev.equalsIgnoreCase(codDomandaProf)){
					newDomanda = true;
				}else{
					newDomanda = false;
				}
			}
			String testoDomanda = domandaBean.getAttribute("testoDomanda").toString();
 			String campoRisposta =  StringUtils.getAttributeStrNotNull(domandaBean, "codiceRisposta");
 			String testoDescrRisposta =  domandaBean.getAttribute("testoDescrRisposta").toString();
			String numvalorerisposta =  null;
			if(domandaBean.getAttribute("numvalorerisposta")!=null){
				numvalorerisposta = domandaBean.getAttribute("numvalorerisposta").toString();
			}
			String strvalorerisposta =  null;
			if(domandaBean.getAttribute("strvalorerisposta")!=null){
				strvalorerisposta = domandaBean.getAttribute("strvalorerisposta").toString();
			}
 			String valueRis = (numvalorerisposta == null)? strvalorerisposta : numvalorerisposta;
			String rispostaDomanda = "";
			String strRispostaLav ="";
			String strOsservazione = "";
		 	
			if(consultaProfilo){
				rispostaDomanda = StringUtils.getAttributeStrNotNull(domandaBean, "codRispostaLav"); 
				valueRis = campoRisposta;
				strRispostaLav =  StringUtils.getAttributeStrNotNull(domandaBean, "strRispostaLav");
				strOsservazione =  StringUtils.getAttributeStrNotNull(domandaBean, "strOsservazione");
				//GESTIONE DOMANDE PARTICOLARI
				if(codDomandaProf.equalsIgnoreCase("D08")){
					codMansione =  StringUtils.getAttributeStrNotNull(domandaBean, "CODMANSIONE"); 
					desTipoMansione= StringUtils.getAttributeStrNotNull(domandaBean, "desTipoMansione");
					descMansione= StringUtils.getAttributeStrNotNull(domandaBean, "DESC_MANSIONE");
					if(StringUtils.isFilledNoBlank(codMansione)){
						descMansione = codMansione + " - " + descMansione;
					}
				}
				//if(codDomandaProf.equalsIgnoreCase("D07") && rispostaDomanda.equalsIgnoreCase("Decodifica.DomandaProfilo.RD07")){}
				 
			}
			if (newDomanda) {
			%>
			 <%if(tipoDomaPrev.equalsIgnoreCase("RG") ){%>
					&nbsp;Molto</center></td></tr>
			<%}%>
			 <%if(tipoDomaPrev.equalsIgnoreCase("RGP") ){%>
					&nbsp;Del tutto d'accordo</td></tr>
			<%}%>
			  
				
				<%if(!tipoDomanda.equalsIgnoreCase("RGP")){%>
				<tr><td class="campo" style="font-weight: bold;">
					<%out.println(testoDomanda);%>
						</td></tr>
				<%}%>
				 <%if(tipoDomanda.equalsIgnoreCase("RG")){%>
								<tr><td class="campo" style="padding-left:5%;"> 
							<%}%>
							<%if(tipoDomanda.equalsIgnoreCase("RGP")){%>
								<tr><td class="campo" style="width: 30% !important;font-weight: bold;"><%out.println(testoDomanda);%></td>
							<%}%>
							<%if(tipoDomanda.equalsIgnoreCase("C")){%>
								<input type="hidden" name="<%=codDomandaProf%>" id="<%=codDomandaProf%>"  value="">
							<%}%>
						
					<%} 
					if(tipoDomanda.equalsIgnoreCase("RG")  || tipoDomanda.equalsIgnoreCase("RGP")){%>
						
							<%if(posizioneRisposta.intValue()==1 && tipoDomanda.equalsIgnoreCase("RG")){%>
								Poco&nbsp;
 							<%}%>
 							<%if(posizioneRisposta.intValue()==1 && tipoDomanda.equalsIgnoreCase("RGP")){%>
								<td nowrap>Per niente d'accordo&nbsp;
 							<%}%>
							 <input type="radio"  <%if (!canModify){%>disabled="true" <%}%>
								<%if (consultaProfilo && valueRis.equalsIgnoreCase(rispostaDomanda)){%>checked <%}%>
								name="<%=codDomandaProf%>" value="<%=campoRisposta%>" onChange="fieldChanged();"/><%=testoDescrRisposta%>&nbsp;&nbsp;
							 <%if(num==(numDomande-1) && tipoDomaPrev.equalsIgnoreCase("RG")){%>
								&nbsp;Molto
							<%}%>
							 <%if(num==(numDomande-1) && tipoDomaPrev.equalsIgnoreCase("RGP")){%>
								&nbsp;Del tutto d'accordo</td></tr>
							<%}%>
 							
						<%}else if(tipoDomanda.equalsIgnoreCase("R")){%>
							<tr><td class="campo" style="padding-left:5%;"> 
							<input type="radio"  <%if (!canModify){%>disabled="true" <%}%>
								<%if ( consultaProfilo && valueRis.equalsIgnoreCase(rispostaDomanda)){%>checked <%}%>
								name="<%=codDomandaProf%>" value="<%=campoRisposta%>" onchange="onChangeRadio(this);"/><%=testoDescrRisposta%>
							</td></tr>
						<%}else if(tipoDomanda.equalsIgnoreCase("RAL")){%>
							<tr><td class="campo" style="padding-left:5%;"> 
							<input type="radio"  <%if (!canModify){%>disabled="true" <%}%>
								<%if ( consultaProfilo && valueRis.equalsIgnoreCase(rispostaDomanda)){%>checked <%}%>
								name="<%=codDomandaProf%>" value="<%=campoRisposta%>" onchange="onChangeRadio(this);"/><%=testoDescrRisposta%>&nbsp;&nbsp;
							<input type="text"  <%if (!canModify){%>disabled="true" <%}%> id="<%=campoRisposta%>_altro" name="<%=campoRisposta%>_altro" style="display:none"  onChange="fieldChanged();" value="<%=strOsservazione %>">
							</td></tr>
 						<%}else if(tipoDomanda.equalsIgnoreCase("TA")){%>
							<tr><td class="campo" style="padding-left:5%;"> 
							<%=testoDescrRisposta%>&nbsp;&nbsp;<af:textArea name="<%=codDomandaProf%>" cols="40" maxlength="2000"
							rows="4" title="<%=campoRisposta%>" readonly="<%= String.valueOf(readOnlyStr) %>" classNameBase="input"
							value="<%=strOsservazione%>" />
							</td></tr>
						<%}else if(tipoDomanda.equalsIgnoreCase("C")){%>
							<tr><td class="campo" style="padding-left:5%;"> 
							<input type="checkbox" <%if (!canModify){%>disabled="true" <%}%>
								<%if (consultaProfilo && valueRis.equalsIgnoreCase(rispostaDomanda)){%>checked <%}%>
								name="<%=codDomandaProf%>_chb" value="<%=campoRisposta%>"  onChange="fieldChanged();"/><%=testoDescrRisposta%>&nbsp;&nbsp;
							 </td></tr>
						<%}else if(tipoDomanda.equalsIgnoreCase("Q")){%>
							<tr><td class="campo"> 
								<%@ include file="mansione.inc"%> </td>
							<td class="campo"> </td></tr>	
						<%} %>
						 
						
			<%
			codDomandaProfPrev = codDomandaProf;
			tipoDomaPrev = tipoDomanda;
			}
		%>
		</table>
		<%if(numDomande>0 && (canSave && canModify)){ %>
			<br>
          		<table align="center">
            		<tr align="center">
              		<td align="center">
                  	<input type="submit" class="pulsanti" name="salvaProfilo" value="<%=titoloBottoneSave %>" >
                	</td>
                	<td align="center">
                		<input type="reset" class="pulsanti" name="BTNRESET" value="Annulla" />
              		</td>
              		</tr>
              	</table>
		<%} %>
	 
	<%	if(StringUtils.isFilledNoBlank(linguetta) && linguetta.equalsIgnoreCase("8") && numDomande>0 && canSave ){ %>
			<br>
          		<table align="center">
            		<tr align="center">
              		<td align="center">
                  	<input type="button" onclick="calcolaScore();" class="pulsanti" name="salvaProfilo" value="Score Personalità" >
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
		<input type="hidden" name="numLinguetta" value="<%=Utils.notNull(linguetta)%>">
		<input type="hidden" name="TIPO_OPERAZIONE" value="<%=Utils.notNull(tipoOperazione)%>">
	</af:form>
	</body>
	</html>