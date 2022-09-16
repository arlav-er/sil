<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/Function_CommonRicercaComune.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String _funzione=(String) serviceRequest.getAttribute("cdnFunzione");
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	// CONTROLLO ACCESSO ALLA PAGINA
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	
	boolean canView = filter.canView();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}
	PageAttribs attributi = new PageAttribs(user, _page);

	boolean canRichiediNucleoRDC = attributi.containsButton("VIEWNUCLEORDC");

 boolean isFromPatto = false;
 String ricercaDaPatto = StringUtils.getAttributeStrNotNull(serviceRequest,"PROVENIENZA");
 String codPrgRdcCampo = StringUtils.getAttributeStrNotNull(serviceRequest,"prgRDCCampo");
 
 if(StringUtils.isFilledNoBlank(ricercaDaPatto) && ricercaDaPatto.equalsIgnoreCase("PATTO")){
	 isFromPatto = true;
 }

 String strCodiceFiscale = StringUtils.getAttributeStrNotNull(serviceRequest,"strCodiceFiscale");
 String strCognome = StringUtils.getAttributeStrNotNull(serviceRequest,"strCognome");
 String strNome = StringUtils.getAttributeStrNotNull(serviceRequest,"strNome");
 String tipoRicerca = StringUtils.getAttributeStrNotNull(serviceRequest,"tipoRicerca");
 String protocolloInps = StringUtils.getAttributeStrNotNull(serviceRequest, "protocolloInps");
 String finisceCon = StringUtils.getAttributeStrNotNull(serviceRequest,"finisceCon"); 
 String dataInvioDa	= StringUtils.getAttributeStrNotNull(serviceRequest, "dataInvioDa");
 String dataInvioA = StringUtils.getAttributeStrNotNull(serviceRequest, "dataInvioA");
 String dataRendicontazioneDa	= StringUtils.getAttributeStrNotNull(serviceRequest, "dataRendicontazioneDa");
 String dataRendicontazioneA = StringUtils.getAttributeStrNotNull(serviceRequest, "dataRendicontazioneA");
 String dataRicezioneSILDa	= StringUtils.getAttributeStrNotNull(serviceRequest, "dataRicezioneSILDa");
 String dataRicezioneSILA = StringUtils.getAttributeStrNotNull(serviceRequest, "dataRicezioneSILA");
 String codComRes = StringUtils.getAttributeStrNotNull(serviceRequest, "codComResHid");
 String strComRes = StringUtils.getAttributeStrNotNull(serviceRequest, "descrComuneResHid");
 String codRuolo = StringUtils.getAttributeStrNotNull(serviceRequest, "codRuolo");
 String codStatoDomanda = StringUtils.getAttributeStrNotNull(serviceRequest, "codStatoDomanda");
 String ordDatRendDC = StringUtils.getAttributeStrNotNull(serviceRequest, "ordDatRendDC");
 String ordDatRend = StringUtils.getAttributeStrNotNull(serviceRequest, "ordDatRend");
 String ordNucleoDC = StringUtils.getAttributeStrNotNull(serviceRequest, "ordNucleoDC");
 String ordNucleo = StringUtils.getAttributeStrNotNull(serviceRequest, "ordNucleo");
 String ordDataDomDC = StringUtils.getAttributeStrNotNull(serviceRequest, "ordDataDomDC");
 String ordDataDom = StringUtils.getAttributeStrNotNull(serviceRequest, "ordDataDom");
 String codTipoDomanda =  StringUtils.getAttributeStrNotNull(serviceRequest, "codTipoDomanda");
/*  String ordDataRicSILDC = StringUtils.getAttributeStrNotNull(serviceRequest, "ordDataRicSILDC");
 String ordDataRicSIL = StringUtils.getAttributeStrNotNull(serviceRequest, "ordDataRicSIL"); */
 
 String isComeBack = StringUtils.getAttributeStrNotNull(serviceRequest, "isComeBack");
 if(StringUtils.isEmptyNoBlank(isComeBack)){
	 ordDatRend = "si";
	 ordDatRendDC ="D";
	 ordNucleo = "si";
	 ordNucleoDC ="C";
	 ordDataDom = "si";
	 ordDataDomDC ="D";
/* 	 ordDataRicSILDC ="D";
	 ordDataRicSIL ="si"; */
	 codStatoDomanda = "AC";
 } 
%>

<%
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>
<head>
   	 <link rel="stylesheet" href="../../css/stili.css" type="text/css">
	 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>    	 
   	 <af:linkScript path="../../js/" />
   	 <SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>

     <script language="Javascript">

	     function resetCampi() {
	    	 document.Frm1.reset();
	    	 document.Frm1.codStatoDomanda[0].checked=true;
	    	 try {
		    	 document.Frm1.codRuolo[2].checked=true;
	    	}catch(error) {}
	     }
	     
	     function gestisciOrdinamento(obj){
	    	 if(!obj.checked){
		    	 var nameRadio = obj.name+"DC";
		    	 var radios =  document.getElementsByName(nameRadio);
		    	 var i;
		    	 for (i = 0; i < radios.length; i++) {
		    		 radios[i].checked = false;
		    	 }
	    	 }
	     }
     
     	function controllaCampi() {
			var cf = document.Frm1.strCodiceFiscale.value;
			var cognome = document.Frm1.strCognome.value;
			var nome = document.Frm1.strNome.value;
			var protocolloInps = document.Frm1.protocolloInps.value;
			var dataInvioDa = document.Frm1.dataInvioDa.value;
			var dataInvioA = document.Frm1.dataInvioA.value;
			var dataRendicontazioneDa = document.Frm1.dataRendicontazioneDa.value;
			var dataRendicontazioneA = document.Frm1.dataRendicontazioneA.value;
			var dataRicezioneSILDa	=  document.Frm1.dataRicezioneSILDa.value;
			var dataRicezioneSILA = document.Frm1.dataRicezioneSILA.value;
			var codComune = document.Frm1.codComResHid.value;
			var descrComune = document.Frm1.strComResHid.value;
			var codRuolo = document.Frm1.codRuolo.value;
			var codStatoDomanda = document.Frm1.codStatoDomanda.value;
				
			var checkDate =  confrontaDate(dataInvioDa, dataInvioA);
			if(checkDate<0){
				alert("Data domanda da maggiore della Data domanda a"); 
				document.Frm1.dataInvioDa.focus();
			    return false;
			}
			checkDate =  confrontaDate(dataRendicontazioneDa, dataRendicontazioneA);
			if(checkDate<0){
				alert("Data rendicontazione da maggiore della Data rendicontazione a"); 
				document.Frm1.dataRendicontazioneDa.focus();
			    return false;
			}
			checkDate =  confrontaDate(dataRicezioneSILDa, dataRicezioneSILA);
			if(checkDate<0){
				alert("Data ricezione SIL da maggiore della Data ricezione SIL a"); 
				document.Frm1.dataRicezioneSILDa.focus();
			    return false;
			}
			return true;
   		}
     	
     	function apriPaginaRicercaAnpal(){
     		if (isInSubmit()) return;
    	    var urlpage="AdapterHTTP?";
    	    urlpage+="CDNFUNZIONE=<%=_funzione%>&PAGE=RichiestaNucleoRDCPage";
    		setWindowLocation(urlpage);
     	}
		  

</script>	    
</head>
<body class="gestione" onload="rinfresca()">
<br>
<p class="titolo">Ricerca Notifiche RDC</p>
<p align="center">
<af:form action="AdapterHTTP" method="POST"	name="Frm1" onSubmit="controllaCampi()">
	<%out.print(htmlStreamTop);%>
	
	<table class="main">
		<tr><td colspan="2"/>&nbsp;</td></tr>
	   	<tr>
	    	<td class="etichetta">Codice Fiscale</td>
	        <td class="campo">
	        	<af:textBox type="text" name="strCodiceFiscale" value="<%=strCodiceFiscale%>" size="20" maxlength="16" />
	        </td>
	   	</tr>
	   	<tr>
	   		<td class="etichetta">Cognome</td>
	    	<td class="campo">
	    		<af:textBox type="text" name="strCognome" value="<%=strCognome%>" size="20" maxlength="50" />
	    	</td>
	    </tr>
	    <tr>
	    	<td class="etichetta">Nome</td>
	        <td class="campo">
	        	<af:textBox type="text" name="strNome" value="<%=strNome%>" size="20" maxlength="50" />
	       </td>
	   	</tr>
		<tr>
			<td class="etichetta">tipo ricerca</td>
		    <td class="campo">
		    	<table colspacing="0" colpadding="0" border="0">
		      		<tr>
		      			<%if (tipoRicerca.equals("iniziaPer")) {%>
		       				<td>
		       					<input type="radio" name="tipoRicerca" value="esatta"/> esatta&nbsp;&nbsp;&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="tipoRicerca" value="iniziaPer" CHECKED /> inizia per
		       				</td>
		      			<%} else {%>
		       				<td>
		       					<input type="radio" name="tipoRicerca" value="esatta" CHECKED /> esatta&nbsp;&nbsp;&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="tipoRicerca" value="iniziaPer" /> inizia per
		       				</td>
		      			<%}%>
		        	</tr>
		        </table>
		    </td>
		</tr>	        
	  	<tr><td colspan="2"><hr width="90%"/></td></tr>
	  	<%if(!isFromPatto){ %>
		<tr>
			<td class="etichetta">Ruolo</td>
		    <td class="campo">
		    	<table colspacing="0" colpadding="0" border="0">
		      		<tr>
		      			<%if (codRuolo.equals("M")) {%>
		       				<td>
		       					<input type="radio" name="codRuolo" value="R"/> Richiedente&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="codRuolo" value="M" CHECKED /> Membro&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="codRuolo" value="" /> Tutti
		       				</td>
		      			<%} else if (codRuolo.equals("R"))  {%>
		       				<td>
		       					<input type="radio" name="codRuolo" value="R" CHECKED /> Richiedente&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="codRuolo" value="M" /> Membro&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="codRuolo" value="" /> Tutti
		       				</td>
		       			<%} else {%>
		       				<td>
		       					<input type="radio" name="codRuolo" value="R" /> Richiedente&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="codRuolo" value="M" /> Membro&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="codRuolo" value="" CHECKED /> Tutti
		       				</td>
		      			<%}%>
		        	</tr>
		        </table>
		    </td>
		</tr>	        
	  	<%} %>	
		<tr>
			<td class="etichetta">Protocollo INPS</td>
				<td class="campo">
					<af:textBox validateOnPost="true" type="text" name="protocolloInps" title="Protocollo INPS" value="<%=protocolloInps%>" 
						classNameBase="input" size="45" maxlength="101" />&nbsp; 
					 
     			<input type="checkbox" name="finisceCon" value="si"
     			<%if (finisceCon.equalsIgnoreCase("si")) { out.print("CHECKED"); }%>>finisce con
				</td>		
			</td>
		</tr>	
		<tr>
			<td class="etichetta">Stato dom. INPS</td>
		    <td class="campo">
		    	<table colspacing="0" colpadding="0" border="0">
		      		<tr>
		      			<%if (codStatoDomanda.equals("AC")) {%>
		       				<td>
		       					<input type="radio" name="codStatoDomanda" value="AC" CHECKED /> AC - Accolto&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="codStatoDomanda" value="OT" /> Altri stati
		       				</td>
		       				<td>
		       					<input type="radio" name="codStatoDomanda" value="" /> Tutti
		       				</td>
		      			<%} else if (codStatoDomanda.equals("OT"))  {%>
		       				<td>
		       					<input type="radio" name="codStatoDomanda" value="AC"  /> AC - Accolto&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="codStatoDomanda" value="OT" CHECKED /> Altri stati
		       				</td>
		       				<td>
		       					<input type="radio" name="codStatoDomanda" value="" /> Tutti
		       				</td>
		       			<%} else {%>
		       				<td>
		       					<input type="radio" name="codStatoDomanda" value="AC" /> AC - Accolto&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="codStatoDomanda" value="OT" /> Altri stati
		       				</td>
		       				<td>
		       					<input type="radio" name="codStatoDomanda" value="" CHECKED /> Tutti
		       				</td>
		      			<%}%>
		        	</tr>
		        </table>
		    </td>
		</tr>  
		<tr>
	        <td class="etichetta">
	       		Tipo di Domanda
	        </td>
			<td class="campo">
			  	<af:comboBox name="codTipoDomanda" size="1" title="Tipo domanda inps"
				  multiple="false" classNameBase="input" addBlank="true"
				  moduleName="M_ComboTipoDomDescrRdc" selectedValue="<%=codTipoDomanda%>"
				/>
	 		</td>
		</tr>
		<%if(!isFromPatto){ %>

				<tr>
					<td class="etichetta">Data domanda da</td>
					<td class="campo"><af:textBox validateOnPost="true"
							type="date" name="dataInvioDa" title="Data domanda da"
							value="<%=dataInvioDa%>" size="10" maxlength="10" />
						&nbsp;&nbsp;a&nbsp;&nbsp; <af:textBox validateOnPost="true"
							type="date" name="dataInvioA" title="Data domanda a"
							value="<%=dataInvioA%>" size="10" maxlength="10" /></td>
				</tr>
				<tr>
					<td class="etichetta">Data rendicontazione da</td>
					<td class="campo"><af:textBox validateOnPost="true" type="date" name="dataRendicontazioneDa" 
					title="Data rendicontazione da" value="<%=dataRendicontazioneDa%>" 
	        	size="10" maxlength="10" />
						&nbsp;&nbsp;a&nbsp;&nbsp; 
						<af:textBox validateOnPost="true" type="date" name="dataRendicontazioneA" 
	        	title="Data rendicontazione a" value="<%=dataRendicontazioneA%>" size="10" maxlength="10" /></td>
				</tr>
				<tr>
					<td class="etichetta">Data ricezione SIL da</td>
					<td class="campo"><af:textBox validateOnPost="true" type="date" name="dataRicezioneSILDa" 
					title="Data ricezione SIL da" value="<%=dataRicezioneSILDa%>" 
	        	size="10" maxlength="10" />
						&nbsp;&nbsp;a&nbsp;&nbsp; 
						<af:textBox validateOnPost="true" type="date" name="dataRicezioneSILA" 
	        	title="Data ricezione SIL a" value="<%=dataRicezioneSILA%>" size="10" maxlength="10" /></td>
				</tr>
     	<tr>
				<td class="etichetta">Comune di residenza&nbsp;</td>
				<td class="campo">
					<af:textBox classNameBase="input"
					title="Comune di residenza"
					onKeyUp="PulisciRicerca(document.Frm1.codComRes, document.Frm1.codComResHid, document.Frm1.strComRes, document.Frm1.strComResHid, null, null, 'codice');"
					type="text" name="codComRes" value="<%=codComRes%>" size="4" maxlength="4"
					readonly="false"/>&nbsp; 
				<A
					HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codComRes, document.Frm1.strComRes, null, 'codice','');"><IMG
					name="image" border="0" src="../../img/binocolo.gif"
					alt="cerca per codice"/></a>&nbsp; 
					<af:textBox type="hidden"
								name="codComResHid" value="" />
	
	
					
					<af:textBox type="text"
								classNameBase="input"
								onKeyUp="PulisciRicerca(document.Frm1.codComRes, document.Frm1.codComResHid, document.Frm1.strComRes, document.Frm1.strComResHid, null, null, 'descrizione');"
								name="strComRes" value="<%=strComRes%>" size="30" maxlength="50"
								readonly="false"/>
				<A
					HREF="javascript:btFindComuneCAP_onclick(document.Frm1.codComRes, document.Frm1.strComRes, null, 'descrizione','');"><IMG
					name="image" border="0" src="../../img/binocolo.gif"
					alt="cerca per descrizione"/></a>&nbsp; 
			   <af:textBox type="hidden" 
			    				  name="strComResHid" value=""/>
			   </td>
			</tr> 	     	
			<%}else{ %>
				 <input type="hidden" name="dataInvioA" value=""/>
				 <input type="hidden" name="codRuolo" value=""/>
				 <input type="hidden" name="dataInvioDa" value=""/>
				 <input type="hidden" name="dataRendicontazioneDa" value=""/>
				 <input type="hidden" name="dataRendicontazioneA" value=""/>
				 <input type="hidden" name="dataRicezioneSILDa" value=""/>
				 <input type="hidden" name="dataRicezioneSILA" value=""/>
				 <input type="hidden" name="codComRes" value=""/>
			<%} %>    
		<tr><td colspan="2"><hr width="90%"/></td></tr>
	  	<%if(!isFromPatto){ %>
		<tr>
			<td class="etichetta" rowspan="4">Ordinamento per</td>
		</tr>	
		<tr>
		    <td class="campo">
		    	<table colspacing="0" colpadding="0" border="0">
		      		<tr>
		      		<td>
		      		<input type="checkbox" name="ordDatRend" value="si" onclick="gestisciOrdinamento(this)"
     			<%if (ordDatRend.equalsIgnoreCase("si")) { out.print("CHECKED"); }%>>Data rendicontazione
					</td>			
		      			<%if (ordDatRendDC.equals("C")) {%>
		       				<td>
		       					<input type="radio" name="ordDatRendDC" value="D"/> Descrescente&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="ordDatRendDC" value="C" CHECKED /> Crescente&nbsp;&nbsp;
		       				</td>
		      			<%} else if (ordDatRendDC.equals("D"))  {%>
		       				<td>
		       					<input type="radio" name="ordDatRendDC" value="D" CHECKED /> Descrescente&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="ordDatRendDC" value="C" /> Crescente&nbsp;&nbsp;
		       				</td>
		       			<%} else{%>
		       				<td>
		       					<input type="radio" name="ordDatRendDC" value="D" /> Descrescente&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="ordDatRendDC" value="C" /> Crescente&nbsp;&nbsp;
		       				</td>
		       			<%}%>
		        	</tr>
		        </table>
		    </td>
		</tr>	        
		<tr>
		    <td class="campo">
		    	<table colspacing="0" colpadding="0" border="0">
		      		<tr><td>
		      		<input type="checkbox" name="ordNucleo" value="si" onclick="gestisciOrdinamento(this)"
     			<%if (ordNucleo.equalsIgnoreCase("si")) { out.print("CHECKED"); }%>>Nucleo fam. (Prot. INPS)
				</td>			
		      		
		      			<%if (ordNucleoDC.equals("C")) {%>
		       				<td>
		       					<input type="radio" name="ordNucleoDC" value="D"/> Descrescente&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="ordNucleoDC" value="C" CHECKED /> Crescente&nbsp;&nbsp;
		       				</td>
		      			<%} else if (ordNucleoDC.equals("D"))  {%>
		       				<td>
		       					<input type="radio" name="ordNucleoDC" value="D" CHECKED /> Descrescente&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="ordNucleoDC" value="C" /> Crescente&nbsp;&nbsp;
		       				</td>
		       			<%} else  {%>
		       				<td>
		       					<input type="radio" name="ordNucleoDC" value="D" /> Descrescente&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="ordNucleoDC" value="C" /> Crescente&nbsp;&nbsp;
		       				</td>
		       			<%}%>
		        	</tr>
		        </table>
		    </td>
		</tr>
		<tr>
		    <td class="campo">
		    	<table colspacing="0" colpadding="0" border="0">
		      		<tr><td>
		      		<input type="checkbox" name="ordDataDom" value="si" onclick="gestisciOrdinamento(this)"
     			<%if (ordDataDom.equalsIgnoreCase("si")) { out.print("CHECKED"); }%>>Data domanda
				</td>			
		      		
		      			<%if (ordDataDomDC.equals("C")) {%>
		       				<td>
		       					<input type="radio" name="ordDataDomDC" value="D"/> Descrescente&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="ordDataDomDC" value="C" CHECKED /> Crescente&nbsp;&nbsp;
		       				</td>
		      			<%} else if (ordDataDomDC.equals("D"))  {%>
		       				<td>
		       					<input type="radio" name="ordDataDomDC" value="D" CHECKED /> Descrescente&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="ordDataDomDC" value="C" /> Crescente&nbsp;&nbsp;
		       				</td>
		       			<%} else  {%>
		       				<td>
		       					<input type="radio" name="ordDataDomDC" value="D" /> Descrescente&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="ordDataDomDC" value="C" /> Crescente&nbsp;&nbsp;
		       				</td>
		       			<%}%>
		        	</tr>
		        </table>
		    </td>
		</tr>	
	<%-- 	<tr>
		    <td class="campo">
		    	<table colspacing="0" colpadding="0" border="0">
		      		<tr><td>
		      		<input type="checkbox" name="ordDataRicSIL" value="si" onclick="gestisciOrdinamento(this)"
     			<%if (ordDataRicSIL.equalsIgnoreCase("si")) { out.print("CHECKED"); }%>>Data ricezione SIL
					</td>			
		      		
		      			<%if (ordDataRicSILDC.equals("C")) {%>
		       				<td>
		       					<input type="radio" name="ordDataRicSILDC" value="D"/> Descrescente&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="ordDataRicSILDC" value="C" CHECKED /> Crescente&nbsp;&nbsp;
		       				</td>
		      			<%} else if (ordDataRicSILDC.equals("D"))  {%>
		       				<td>
		       					<input type="radio" name="ordDataRicSILDC" value="D" CHECKED /> Descrescente&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="ordDataRicSILDC" value="C" /> Crescente&nbsp;&nbsp;
		       				</td>
		       			<%} else  {%>
		       				<td>
		       					<input type="radio" name="ordDataRicSILDC" value="D" /> Descrescente&nbsp;&nbsp;
		       				</td>
		       				<td>
		       					<input type="radio" name="ordDataRicSILDC" value="C" /> Crescente&nbsp;&nbsp;
		       				</td>
		       			<%}%>
		        	</tr>
		        </table>
		    </td>
		</tr>         --%>
		 
	  <%}else{ %>
				 <input type="hidden" name="ordDatRendDC" value="D"/>
				 <input type="hidden" name="ordDatRend" value="si"/>
				 <input type="hidden" name="ordNucleoDC" value="C"/>
				 <input type="hidden" name="ordNucleo" value="si"/>
				 <input type="hidden" name="ordDataDomDC" value="D"/>
				 <input type="hidden" name="ordDataDom" value="si"/>
<!-- 				 <input type="hidden" name="ordDataRicSILDC" value="D"/>
				 <input type="hidden" name="ordDataRicSIL" value="si"/> -->
			<%} %>   		
		
		<tr><td colspan="2">&nbsp;</td></tr>
	    <tr>
	    	<td colspan="2" align="center">
		    	<input class="pulsanti" type="submit" name="cerca" value="Cerca" />
		          &nbsp;&nbsp;
		          <input type="reset" class="pulsanti" value="Annulla" onclick="resetCampi()"/>				
	        </td>
	    </tr>
	    <input type="hidden" name="PAGE" value="ListaNotificheRDCPage"/>
	    <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
	    <input type="hidden" name="PROVENIENZA" value="<%=ricercaDaPatto%>"/>
	    <input type="hidden" name="campoPrgRdc" value="<%=codPrgRdcCampo%>"/>
	    
	     <%if(canRichiediNucleoRDC){ %>
	    <tr><td colspan="2">&nbsp;</td></tr>
	    <tr>
	    	<td colspan="2" align="center">
		        <input type="button" class="pulsanti" value="Richiesta notifica ad ANPAL" onclick="apriPaginaRicercaAnpal()"/>			
	        </td>
	    </tr>
	    <%} %>  
	    <%if(isFromPatto){ %>
	    <tr><td colspan="2">&nbsp;</td></tr>
	    <tr>
	    	<td colspan="2" align="center">
		        <input type="button" class="pulsanti" value="Chiudi" onclick="window.close()"/>			
	        </td>
	    </tr>
	    <%} %>  
		</table>
	</af:form>
	<%out.print(htmlStreamBottom);%>
	
	</body>
</html>