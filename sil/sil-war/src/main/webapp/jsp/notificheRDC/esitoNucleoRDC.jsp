<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
			it.eng.sil.security.*,
			it.eng.afExt.utils.*,
			it.eng.sil.util.*,
			java.math.*,
			java.util.*,
			it.gov.lavoro.servizi.serviceRDC.types.*,
			it.eng.sil.rdc.servizi.RDCBean"
	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>

<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%
 	String  titolo = "";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 
	boolean canModify = false;

	// Lettura parametri dalla REQUEST
	String  cdnfunzioneStr   = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");
	String  cdnLavoratore    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
	
	boolean noLavoratore = false;
	String provenienza = null;
	if(serviceRequest.containsAttribute("PROVENIENZA") && serviceRequest.getAttribute("PROVENIENZA")!=null){
		noLavoratore = true;
		provenienza = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PROVENIENZA"); 
	}

	// CONTROLLO ACCESSO ALLA PAGINA
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	if(!noLavoratore){
		filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	}
	
	boolean canView = filter.canViewLavoratore();
	if(noLavoratore){
		canView = filter.canView();
	}
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}

	// CONTROLLO PERMESSI SULLA PAGINA
	PageAttribs attributi = new PageAttribs(user, _page);

	boolean canAddMembri = attributi.containsButton("ADD_FAM_RDC");
	
	Object cdnUtCorrente    = sessionContainer.getAttribute("_CDUT_");
	String cdnUtCorrenteStr = StringUtils.getStringValueNotNull(cdnUtCorrente);
	
	String protocolloInps = null;
	String statoDomInps = null;
	String strDataDomanda = null;
	String strDataRendicontazione = null;
	String strCodiceFiscaleRich = null;
	String codRuolo = null;
	String strCodiceFiscale = null;
	String strCognome = null;
	String strNome = null;
	String codCittadinanza = null;
	String codComuneNas = null;
	String strDataNas = null;
	String strSesso = null;
	String strIndirizzoRes = null;
	String codComuneRes = null;
	String strCapRes = null;
	String strIndirizzoDom = null;
	String codComuneDom = null;
	String strCapDom = null;
	String strCodSap = null;
	String strTelefono = null;
	String strEmail = null;
	String fonte = null;
	String strDataPassaggio= null;
	
	String codTipoDomanda = null;
	String strDataVariazioneStato = null;
	
	String codDomandaInps = null;
	
	titolo = "Nucelo Familiare RDC";
	Beneficiario_Type[] allNucleo;
	Integer size = new Integer(0);
	if(serviceResponse.containsAttribute("M_NucleoFamiliareRDC.RDC_NUCELO_FAM")){
		
		size = (Integer) serviceResponse.getAttribute("M_NucleoFamiliareRDC.SIZE");
		if(size.intValue() >0){
			allNucleo =  new Beneficiario_Type[size.intValue()];
			allNucleo = (Beneficiario_Type[]) serviceResponse.getAttribute("M_NucleoFamiliareRDC.RDC_NUCELO_FAM");
		}else{
			allNucleo = null;
		}
	}else{
		allNucleo = null;
	}

	String codiceDescErrore = "";
	 if(serviceResponse.containsAttribute("M_NucleoFamiliareRDC.RDC_ERRORE")){
			
		 codiceDescErrore = (String)serviceResponse.getAttribute("M_NucleoFamiliareRDC.RDC_ERRORE");
	}

	// Sola lettura: viene usato per tutti i campi di input
	String nomeClass = "main";
	
	// Stringhe con HTML per layout tabelle
 
  	String htmlStreamTopCoop = StyleUtils.roundTopTable("prof_ro_coop");
  	String htmlStreamBottomCoop = StyleUtils.roundBottomTable("prof_ro_coop");
  	
%>

<html>
<head>
<title><%= titolo %></title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<link rel="stylesheet" type="text/css" href="../../css/stiliTemplate.css"/>

<af:linkScript path="../../js/"/>

<%-- INCLUDERE QUI ALTRI JAVASRIPT CON CLAUSOLE DEL TIPO:
<script language="Javascript" src="../../js/xxx.js"></script>
--%>
 
<script language="Javascript">
	
	/* Funzione chiamata al caricamento della pagina */
	function onLoad() {
		rinfresca();
		// altri funzioni da richiamare sulla onLoad...
	}
    function gestisciAddMembroRDC(indiceSel) {
    	var insertForm=document.forms["Frm1"];
  	    insertForm.bottonePremuto.value ="addMembroRdc";
  	    insertForm.indiceSelezionato.value =indiceSel;
    	insertForm.submit();
	}
	

<%
	if(!noLavoratore){
		// Genera il Javascript che si occuperà di inserire i links nel footer
			attributi.showHyperLinks(out, requestContainer, responseContainer, "cdnLavoratore="+cdnLavoratore);
	}
%>

</script>

<style type="text/css">
td.etichetta{
vertical-align: top;
}
input.inputView{
 vertical-align: top;
}
td.campo {
 vertical-align: top;
}
</style>
	

</head>

<body class="gestione" onload="onLoad()">
 
<br/>
<p class="titolo"><%= titolo %></p>

	<p>
	 	<font color="green">
	 		<af:showMessages prefix="M_NucleoFamiliareRDC"/>
	 		<af:showMessages prefix="M_AddNucleoFamiliareRDC"/>
	  	</font>
	  	<font color="red"><af:showErrors /><%= codiceDescErrore %></font>
	</p>
	<af:form name="Frm1" action="AdapterHTTP" method="POST">
		
	<%
	if(allNucleo!=null && allNucleo.length>0){
		RDCBean support = new RDCBean();
	%>
		<input type="hidden" name="PAGE" value="<%=_page%>">
		<input type="hidden" name="PROVENIENZA" value="<%=Utils.notNull(provenienza)%>">
		<input type="hidden" name="cdnLavoratore" value="<%=Utils.notNull(cdnLavoratore)%>">
		<input type="hidden" name="cdnFunzione" value="<%=cdnfunzioneStr%>">
		<input type="hidden" name="bottonePremuto" value="">
		<input type="hidden" name="indiceSelezionato" value="">

	<%out.print(htmlStreamTopCoop);%>
	
	<% 
		for(int i=0; i< allNucleo.length; i++){
			Beneficiario_Type beneficiario = allNucleo[i];
			protocolloInps= beneficiario.getCod_protocollo_inps();
			statoDomInps= beneficiario.getCod_stato();
			strDataDomanda = support.formatDate(beneficiario.getDtt_domanda());
			strDataRendicontazione = support.formatDate(beneficiario.getDtt_rendicontazione());
			strCodiceFiscaleRich = beneficiario.getCod_fiscale_richiedente();
			codRuolo = beneficiario.getCod_ruolo_beneficiario();
			strCodiceFiscale = beneficiario.getCod_fiscale();
			strCognome= beneficiario.getDes_cognome();
			strNome =  beneficiario.getDes_nome(); 
			strDataNas = support.formatDate(beneficiario.getDtt_nascita());
			if(StringUtils.isFilledNoBlank(beneficiario.getCod_cittadinanza())){
				codCittadinanza = beneficiario.getCod_cittadinanza(); 
			}else{
				codCittadinanza = RDCBean.COD_NULL_DEFAULT;
			}
			
			if(StringUtils.isFilledNoBlank(beneficiario.getCod_comune_nascita())){
				codComuneNas= beneficiario.getCod_comune_nascita();
			}else{
				codComuneNas = RDCBean.COD_NULL_DEFAULT;
			}

			strSesso= beneficiario.getCod_sesso();
			strIndirizzoRes= beneficiario.getDes_indirizzo_residenza();
			codComuneRes = beneficiario.getCod_comune_residenza();
			strCapRes = beneficiario.getCod_cap_residenza();
			strIndirizzoDom = beneficiario.getDes_indirizzo_domicilio();
			codComuneDom = 	beneficiario.getCod_comune_domicilio();
			strCapDom = beneficiario.getCod_cap_domicilio();
			strCodSap= beneficiario.getCod_sap();
			strTelefono = beneficiario.getDes_telefono();
			strEmail= beneficiario.getDes_email();
			fonte = beneficiario.getFonte();
			strDataPassaggio = support.formatDate(beneficiario.getDtt_trasformazione());
			codTipoDomanda= beneficiario.getTipo_domanda();
			if(StringUtils.isFilledNoBlank(beneficiario.getDtt_variazione_stato())){
				strDataVariazioneStato = support.formatDate(beneficiario.getDtt_variazione_stato());
			}
		
	%>
		
 		<input type="hidden" name="cognome" value="<%=strCognome%>">
		<input type="hidden" name="nome" value="<%=strNome%>">
		<input type="hidden" name="codiceFiscale" value="<%=strCodiceFiscale%>">
	<div align="center">
		
 	<table class="main">
 			<tr>
				<td><br /></td>
			</tr>
			<tr>
				<td colspan="4"><p class="titolo"><%=strCodiceFiscale.toUpperCase() %>&nbsp;-&nbsp;<%=strCognome.toUpperCase() %>&nbsp;<%=strNome.toUpperCase() %>
				<%if(canAddMembri){ %>
  					<a href="#" onClick="javascript:gestisciAddMembroRDC('<%=i%>');"><img src="../../img/add3.gif"></a>
  					
  				<%} %>
  				</p></td>
			</tr>
			<tr ><td colspan="4" ><hr width="90%"/></td></tr>
		</table>
	</div>	

	<table class="main">
		<tr>
	        <td class="etichetta">
	       		Cittadinanza
	        </td>
			<td class="campo">
				 <af:textBox name="codCittad" type="text"
							title="cittadinanza"
							value="<%= codCittadinanza %>"
							size="5"  
							classNameBase="input"
							readonly="true"
				/>&nbsp;-&nbsp;
			  	<af:comboBox name="descrCitt" size="1" title="Descr cittad"
				  multiple="false" disabled="true" classNameBase="input"
				  moduleName="COMBO_CITTADINANZA" selectedValue="<%=codCittadinanza%>"
				/>
	 		</td>
		</tr>
		<tr>
	        <td class="etichetta">
	       		Comune di Nascita
	        </td>
			<td class="campo">
				 <af:textBox name="codcomNas" type="text"
							title="com nascita"
							value="<%= codComuneNas %>"
							size="5"  
							classNameBase="input"
							readonly="true"
				/>&nbsp;-&nbsp;
			  	<af:comboBox name="descrcomNas" size="1" title="Descr com nas"
				  multiple="false" disabled="true" classNameBase="input"
				  moduleName="COMBO_COMUNE" selectedValue="<%=codComuneNas%>"
				/>
	 		</td>
		</tr>	
		<tr>
	        <td class="etichetta">
	       		Data di Nascita
	        </td>
			<td class="campo">
			 <af:textBox name="datanas" type="text"
							title="data nasctia"
							value="<%= strDataNas %>"
							size="15" maxlength="101"
							classNameBase="input"
							readonly="true"
				/>
	 		</td>
		</tr>	
		<tr>
	        <td class="etichetta">
	       		Sesso
	        </td>
			<td class="campo">
			 <af:textBox name="sesso" type="text"
							title="sesso"
							value="<%= strSesso %>"
							size="15" maxlength="101"
							classNameBase="input"
							readonly="true"
				/>
	 		</td>
		</tr>
		<tr>
	        <td class="etichetta" rowspan="3">
	       		Residenza
	        </td>
			<td class="campo">
			 <af:textBox name="indirizzoRes" type="text"
							title="indirizzo residenza"
							value="<%= strIndirizzoRes %>"
							size="60" maxlength="101"
							classNameBase="input"
							readonly="true"
				/>
	 		</td>
		</tr>
		<tr>
			<td class="campo">
				 <af:textBox name="codComRes" type="text"
							title="com residenza"
							value="<%= codComuneRes %>"
							size="5"  
							classNameBase="input"
							readonly="true"
				/>&nbsp;-&nbsp;
			  	<af:comboBox name="descrcomRes" size="1" title="Descr com res"
				  multiple="false" disabled="true" classNameBase="input"
				  moduleName="COMBO_COMUNE" selectedValue="<%=codComuneRes%>"
				/>
	 		</td>
		</tr>
		<tr>
			<td class="campo">
				 <af:textBox name="capComRes" type="text"
							title="cap residenza"
							value="<%= strCapRes %>"
							size="5"  
							classNameBase="input"
							readonly="true"
				/>
	 		</td>
		</tr>
		<tr>
	        <td class="etichetta" rowspan="3">
	       		Domicilio
	        </td>
			<td class="campo">
			 <af:textBox name="indirizzoDom" type="text"
							title="indirizzo domicilio"
							value="<%= strIndirizzoDom %>"
							size="60" maxlength="101"
							classNameBase="input"
							readonly="true"
				/>
	 		</td>
		</tr>
		<tr>
			<td class="campo">
				 <af:textBox name="codComDom" type="text"
							title="com domicilio"
							value="<%= codComuneDom %>"
							size="5"  
							classNameBase="input"
							readonly="true"
				/>&nbsp;-&nbsp;
			  	<af:comboBox name="descrcomDom" size="1" title="Descr com dom"
				  multiple="false" disabled="true" classNameBase="input"
				  moduleName="COMBO_COMUNE" selectedValue="<%=codComuneDom%>"
				/>
	 		</td>
		</tr>
		<tr>
			<td class="campo">
				 <af:textBox name="capDom" type="text"
							title="cap domicilio"
							value="<%= strCapDom %>"
							size="5"  
							classNameBase="input"
							readonly="true"
				/>
	 		</td>
		</tr>
		<tr>
	        <td class="etichetta">
	       		Codice SAP
	        </td>
			<td class="campo">
			 <af:textBox name="codSap" type="text"
							title="codice Sap"
							value="<%= strCodSap %>"
							size="15" maxlength="101"
							classNameBase="input"
							readonly="true"
				/>
	 		</td>
		</tr>
		<tr>
	        <td class="etichetta">
	       		Contatto Telefonico
	        </td>
			<td class="campo">
			 <af:textBox name="tel" type="text"
							title="telefono"
							value="<%= strTelefono %>"
							size="20" maxlength="101"
							classNameBase="input"
							readonly="true"
				/>
	 		</td>
		</tr>
		<tr>
	        <td class="etichetta">
	       		Contatto E-mail
	        </td>
			<td class="campo">
			 <af:textBox name="email" type="text"
							title="email"
							value="<%= strEmail %>"
							size="60" maxlength="101"
							classNameBase="input"
							readonly="true"
				/>
	 		</td>
		</tr>
		<tr>
	        <td class="etichetta">
	       		Protocollo INPS
	        </td>
			<td class="campo">
			 <af:textBox name="protInps" type="text"
							title="Protocollo INPS"
							value="<%= protocolloInps %>"
							size="45" maxlength="101"
							classNameBase="input"
							readonly="true"
				/>
	 		</td>
		</tr>
		<tr>
	        <td class="etichetta">
	       		Stato Domanda INPS
	        </td>
			<td class="campo">
				 <af:textBox name="domandaInps" type="text"
							title="Stato Domanda INPS"
							value="<%= statoDomInps %>"
							size="30" maxlength="101"
							classNameBase="input"
							readonly="true"
				/>&nbsp;-&nbsp;
			  	<af:comboBox name="descrDomandaInps" size="1" title="Descr domanda inps"
				  multiple="false" disabled="true" classNameBase="input"
				  moduleName="M_ComboStatoRdc" selectedValue="<%=statoDomInps%>"
				/>
	 		</td>
		</tr>
		<tr>
	        <td class="etichetta">
	       		Tipo Domanda INPS
	        </td>
			<td class="campo">
			  	<af:comboBox name="codTipoDomanda" size="1" title="Tipo domanda inps"
				  multiple="false" classNameBase="input" addBlank="true" disabled="true" 
				  moduleName="M_ComboTipoDomDescrRdc" selectedValue="<%=codTipoDomanda%>"
				/>
	 		</td>
		</tr>
		<tr>
	        <td class="etichetta">
	       		Data Variazione Stato
	        </td>
			<td class="campo">
			 <af:textBox name="dataVariazioneStato" type="text"
							title="Data Variazione Stato"
							value="<%= strDataVariazioneStato %>"
							size="15" maxlength="101"
							classNameBase="input"
							readonly="true"
				/>
	 		</td>
		</tr>
		<tr>
	        <td class="etichetta">
	       		Data Domanda
	        </td>
			<td class="campo">
			 <af:textBox name="dataDomanda" type="text"
							title="data Domanda INPS"
							value="<%= strDataDomanda %>"
							size="15" maxlength="101"
							classNameBase="input"
							readonly="true"
				/>
	 		</td>
		</tr>
		<tr>
	        <td class="etichetta">
	       		Data Rendicontazione
	        </td>
			<td class="campo">
			 <af:textBox name="dataRned" type="text"
							title="data Rendicontazione"
							value="<%= strDataRendicontazione %>"
							size="15" maxlength="101"
							classNameBase="input"
							readonly="true"
				/>
	 		</td>
		</tr>
		<tr>
	        <td class="etichetta">
	       		CF Richiedente
	        </td>
			<td class="campo">
			 <af:textBox name="cfRich" type="text"
							title="cf richiedente"
							value="<%= strCodiceFiscaleRich %>"
							size="20" maxlength="101"
							classNameBase="input"
							readonly="true"
				/>
	 		</td>
		</tr>
		<tr>
	        <td class="etichetta">
	       		Ruolo
	        </td>
			<td class="campo">
				 <af:textBox name="codRuolo" type="text"
							title="codRuolo"
							value="<%= codRuolo %>"
							size="5"  
							classNameBase="input"
							readonly="true"
				/>&nbsp;-&nbsp;
			  	<af:comboBox name="descrRuolo" size="1" title="Descr ruolo"
				  multiple="false" disabled="true" classNameBase="input"
				  moduleName="M_ComboRuoloRdc" selectedValue="<%=codRuolo%>"
				/>
	 		</td>
		</tr>
		<tr>
	        <td class="etichetta">
	       		Fonte
	        </td>
			<td class="campo">
			 <af:textBox name="fonte" type="text"
							title="Fonte"
							value="<%= fonte %>"
							size="50" maxlength="101"
							classNameBase="input"
							readonly="true"
				/>
	 		</td>
		</tr>
		<tr>
	        <td class="etichetta">
	       		Data passaggio da patto per inclusione al patto per il lavoro
	        </td>
			<td class="campo">
			 <af:textBox name="dataPassaggio" type="text"
							title="data passaggio"
							value="<%= strDataPassaggio %>"
							size="15" maxlength="101"
							classNameBase="input"
							readonly="true"
				/>
	 		</td>
		</tr>
</table>
 <%} 
	out.print(htmlStreamBottomCoop);
 } %>
 
 </af:form>
 <center>
	<br>
	<input type="button" class="pulsanti" value="Chiudi" onclick="window.close()">
	<br>&nbsp;
</center>
</body>
</html>
