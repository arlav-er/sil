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
PageAttribs attributi = new PageAttribs(user,"ProfiloLavPage");

ProfileDataFilter filter = new ProfileDataFilter(user,_page );
 
if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	return;
} 
	String linguetta ="1";
	int numRisPers = -1;
	boolean canSave = attributi.containsButton("SALVA");
	boolean canModify = canSave;
	boolean readOnlyStr = !canModify;
	String titoloBottoneSave = "Salva";
 	String codMansione= "",  descMansione= "", desTipoMansione="";
 
	String titlePagina = "";
	boolean existPrecompilato = false;
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
	<body class="gestione" onload="rinfresca();">
	
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

	<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="checkCampiAggiuntiva()">
		
		<% out.print(htmlStreamTop); %>
		<br>
	 	<table class="main">
		
		<% 
		String codDomandaProfPrev= "";
		boolean isPrecompilato = false;
		boolean skipPrecompila = false;
		String tipoDomaPrev ="";
  		SourceBean datiPrecompilazione =(SourceBean) serviceResponse.getAttribute("M_GetInfoPrecompilateProfilo.ROWS.ROW");
		for (int num=0;num<numDomande;num++) {
			 
			boolean newDomanda = false;
			SourceBean domandaBean = (SourceBean)domandeL1.get(num);
			String codDomandaProf = domandaBean.getAttribute("codDomandaProf").toString();
			String tipoDomanda =  domandaBean.getAttribute("strTipoInput").toString();
			BigDecimal posizioneRisposta = (BigDecimal) domandaBean.getAttribute("posizioneRisposta");
			if(num==0 || !codDomandaProfPrev.equalsIgnoreCase(codDomandaProf)){
				codDomandaProfPrev = codDomandaProf;
				newDomanda= true;
				isPrecompilato = false;
				skipPrecompila = false;
				 
			}else if(codDomandaProfPrev.equalsIgnoreCase(codDomandaProf)){
				newDomanda = false;
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
 			String valueRis = campoRisposta;
			String rispostaDomanda = "";
			String strRispostaLav ="";
			String strOsservazione = "";
		 	
			if(consultaProfilo){
  				rispostaDomanda = StringUtils.getAttributeStrNotNull(domandaBean, "codRispostaLav"); 
				if(StringUtils.isFilledNoBlank(rispostaDomanda)){
					skipPrecompila = true;
				}
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
			}
			//casistica precompilazione
			if(!skipPrecompila && datiPrecompilazione != null){
				if(codDomandaProf.equalsIgnoreCase("D01") ){
						if(StringUtils.isEmptyNoBlank(rispostaDomanda)){
							rispostaDomanda = (String) datiPrecompilazione.getAttribute("sessoLav");
							if(valueRis.equalsIgnoreCase(rispostaDomanda)){
								isPrecompilato = true;
								existPrecompilato = true;
							}
						} 
				}
				if(codDomandaProf.equalsIgnoreCase("D02") ){
						if(StringUtils.isEmptyNoBlank(rispostaDomanda)){
							BigDecimal etaLavBD = (BigDecimal) datiPrecompilazione.getAttribute("etaLav");
							int etaLav = (etaLavBD != null)? etaLavBD.intValue() : 0;
							if(etaLav >= 15 && etaLav <= 24){
								rispostaDomanda = Decodifica.DomandaProfilo.E_15_24;
								if(valueRis.equalsIgnoreCase(rispostaDomanda)){
									isPrecompilato = true;
									existPrecompilato = true;
								}
							}else if(etaLav >= 25 && etaLav <= 34){
								rispostaDomanda = Decodifica.DomandaProfilo.E_25_34;
								if(valueRis.equalsIgnoreCase(rispostaDomanda)){
									isPrecompilato = true;
									existPrecompilato = true;
								}
							}else if(etaLav >= 35 && etaLav <= 44){
								rispostaDomanda = Decodifica.DomandaProfilo.E_35_44;
								if(valueRis.equalsIgnoreCase(rispostaDomanda)){
									isPrecompilato = true;
									existPrecompilato = true;
								}
							}else if(etaLav >= 45 && etaLav <= 54){
								rispostaDomanda = Decodifica.DomandaProfilo.E_45_54;
								if(valueRis.equalsIgnoreCase(rispostaDomanda)){
									isPrecompilato = true;
									existPrecompilato = true;
								}
							}else if(etaLav >= 55){
								rispostaDomanda = Decodifica.DomandaProfilo.E_55;
								if(valueRis.equalsIgnoreCase(rispostaDomanda)){
									isPrecompilato = true;
									existPrecompilato = true;
								}
							}
						} 
				}
				if(codDomandaProf.equalsIgnoreCase("D04") ){	
						if(StringUtils.isEmptyNoBlank(rispostaDomanda)){
							String statoOccupazLav = (String) datiPrecompilazione.getAttribute("statoOccupazLav");
							if(StringUtils.isFilledNoBlank(statoOccupazLav) && statoOccupazLav.equalsIgnoreCase(Decodifica.StatoOccupazionale.OCCUPATO)){
								rispostaDomanda = Decodifica.StatoOccupazionale.RD04_OCC;
								if(valueRis.equalsIgnoreCase(rispostaDomanda)){
									isPrecompilato = true;
									existPrecompilato = true;
								}
							}else if(StringUtils.isFilledNoBlank(statoOccupazLav) &&
									(statoOccupazLav.equalsIgnoreCase(Decodifica.StatoOccupazionale.DISOCCUPATO_I) || statoOccupazLav.equalsIgnoreCase(Decodifica.StatoOccupazionale.DISOCCUPATO_D)))
									{
										BigDecimal mesiDisocc = (BigDecimal) datiPrecompilazione.getAttribute("mesiDisoccLav");
										if(mesiDisocc.intValue() > 12){
											rispostaDomanda = Decodifica.StatoOccupazionale.RD04_DISOCC_12;
											if(valueRis.equalsIgnoreCase(rispostaDomanda)){
												isPrecompilato = true;
												existPrecompilato = true;
											}
										}else{
											rispostaDomanda = Decodifica.StatoOccupazionale.RD04_DISOCC;
											if(valueRis.equalsIgnoreCase(rispostaDomanda)){
												isPrecompilato = true;
												existPrecompilato = true;
											}
										}
									}
						} 
					
				}
				if(codDomandaProf.equalsIgnoreCase("D06") ){
					if(StringUtils.isEmptyNoBlank(rispostaDomanda)){
						BigDecimal numeroSi = (BigDecimal) datiPrecompilazione.getAttribute("numeroAutoSi");
						if( numeroSi.intValue() > 0){
								rispostaDomanda = Decodifica.DomandaProfilo.RD06_SI;
								if(valueRis.equalsIgnoreCase(rispostaDomanda)){
									isPrecompilato = true;
									existPrecompilato = true;
								}
						}
					}
				} 
			}
			if (newDomanda) {
			%>
	 
				<tr><td class="campo" style="font-weight: bold;">
					<%out.println(testoDomanda);%>
					</td></tr>
			 			
						
					<%} 
					if(tipoDomanda.equalsIgnoreCase("R")){%>
							<tr><td class="campo" style="padding-left:5%;"> 
							<input type="radio"  <%if (!canModify){%>disabled="true" <%}%>
								<%if ((isPrecompilato || consultaProfilo) && valueRis.equalsIgnoreCase(rispostaDomanda)){%>checked <%}%>
								name="<%=codDomandaProf%>" value="<%=campoRisposta%>" onchange="onChangeRadio(this);"/><%=testoDescrRisposta%>
							 </td></tr>
						<%}else if(tipoDomanda.equalsIgnoreCase("RAL")){%>
							<tr><td class="etichetta"><center>
							<input type="radio"  <%if (!canModify){%>disabled="true" <%}%>
								<%if ((isPrecompilato || consultaProfilo) && valueRis.equalsIgnoreCase(rispostaDomanda)){%>checked <%}%>
								name="<%=codDomandaProf%>" value="<%=campoRisposta%>" onchange="onChangeRadio(this);"/><%=testoDescrRisposta%>&nbsp;&nbsp;
							<input type="text"  <%if (!canModify){%>disabled="true" <%}%> id="<%=campoRisposta%>_altro" name="<%=campoRisposta%>_altro" style="display:none"  onChange="fieldChanged();" value="<%=strOsservazione %>">
							</center></td></tr>
 						<%}else if(tipoDomanda.equalsIgnoreCase("TA")){%>
							<tr><td class="etichetta"><center>
							<%=testoDescrRisposta%>&nbsp;&nbsp;<af:textArea name="<%=codDomandaProf%>" cols="40" maxlength="2000"
							rows="4" title="<%=campoRisposta%>" readonly="<%= String.valueOf(readOnlyStr) %>" classNameBase="input"
							value="<%=strOsservazione%>" />
							</center></td></tr>
						<%}else if(tipoDomanda.equalsIgnoreCase("C")){%>
							<tr><td class="etichetta"><center>
							<input type="checkbox" <%if (!canModify){%>disabled="true" <%}%>
								<%if ((isPrecompilato || consultaProfilo)  && valueRis.equalsIgnoreCase(rispostaDomanda)){%>checked <%}%>
								name="<%=codDomandaProf%>_chb" value="<%=campoRisposta%>"  onChange="fieldChanged();"/><%=testoDescrRisposta%>&nbsp;&nbsp;
							</center></td></tr>
						<%}%>
						
						
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

	</af:form>
	<%@ include file="cambioLinguetta.inc" %>
	</body>
	</html>