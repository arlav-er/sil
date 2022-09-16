<%@ page
	contentType="text/html;charset=utf-8"
	
	import="com.engiweb.framework.base.*,
			it.eng.sil.security.*,
			it.eng.afExt.utils.*,
			it.eng.sil.util.*,
			java.math.*,
			java.util.*"
	
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
	

	// CONTROLLO ACCESSO ALLA PAGINA
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	if(StringUtils.isFilledNoBlank(cdnLavoratore ))
		filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	
	boolean canView = filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	}

	// CONTROLLO PERMESSI SULLA PAGINA
	PageAttribs attributi = new PageAttribs(user, _page);

	boolean fromMenuGenerale = false;
	boolean fromCruscotto = false;
	String paginaIndietro = "";
	if(serviceRequest.containsAttribute("PROVENIENZA")){
		fromMenuGenerale = true;
		paginaIndietro = "ListaNotificheRDCPage";
	}else{
		fromMenuGenerale = false;
		paginaIndietro = "LinguettaNotificheRDCPage";
	}
	
	
	Object cdnUtCorrente    = sessionContainer.getAttribute("_CDUT_");
	String cdnUtCorrenteStr = StringUtils.getStringValueNotNull(cdnUtCorrente);
	
	BigDecimal prgAmRDC =null;
	String idComunicazione= null; 
	String strDataInvio =  null;
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
	String codCpi = null;
	String strTelefono = null;
	String strEmail = null;
	String nazionalita = null;
	String descrComNas = null;
	String descrComRes = null;
	String descrComDom = null;
	String descrCpiMin = null;
	String descrCodMonoRuolo = null;
	String utenteInserimento = null;
	String utenteModifica = null;
			
	if(serviceRequest.containsAttribute("TIPO_RICERCA")){
		fromCruscotto = true;
	}
			
	SourceBean row = null;
	//
	titolo = "Dettaglio Notifica RDC";
	 if(serviceResponse.containsAttribute("M_DettaglioNotificaRDC")){
		
		row= (SourceBean) serviceResponse.getAttribute("M_DettaglioNotificaRDC.ROWS.ROW");
		if(row!=null){
			if(StringUtils.isEmptyNoBlank(cdnLavoratore)){
				cdnLavoratore = SourceBeanUtils.getAttrStrNotNull(row, "CDNLAVORATORE");
				if(StringUtils.isFilledNoBlank(cdnLavoratore))
					filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
			}
			
			prgAmRDC = SourceBeanUtils.getAttrBigDecimal(row, "PRGRDC", null);
			idComunicazione= SourceBeanUtils.getAttrStrNotNull(row, "STRIDCOMUNICAZIONE");
			strDataInvio = SourceBeanUtils.getAttrStrNotNull(row, "STRDATINVIO");
			protocolloInps= SourceBeanUtils.getAttrStrNotNull(row, "STRPROTOCOLLOINPS");
			statoDomInps= SourceBeanUtils.getAttrStrNotNull(row, "CODSTATOINPS");
			strDataDomanda = SourceBeanUtils.getAttrStrNotNull(row, "STRDATDOMANDA");
			strDataRendicontazione = SourceBeanUtils.getAttrStrNotNull(row, "STRDATRENDICONTAZIONE");
			strCodiceFiscaleRich = SourceBeanUtils.getAttrStrNotNull(row, "STRCFRICHIEDENTE");
			codRuolo = SourceBeanUtils.getAttrStrNotNull(row, "CODMONORUOLO");
			strCodiceFiscale = SourceBeanUtils.getAttrStrNotNull(row, "STRCODICEFISCALE");
			strCognome= SourceBeanUtils.getAttrStrNotNull(row, "STRCOGNOME");
			strNome =  SourceBeanUtils.getAttrStrNotNull(row, "STRNOME"); 
			codCittadinanza =  SourceBeanUtils.getAttrStrNotNull(row, "CODCITTADINANZA"); 
			codComuneNas= SourceBeanUtils.getAttrStrNotNull(row, "CODCOMNASC");
			strDataNas = SourceBeanUtils.getAttrStrNotNull(row, "STRDATNASC");
			strSesso= SourceBeanUtils.getAttrStrNotNull(row, "STRSESSO");
			strIndirizzoRes= SourceBeanUtils.getAttrStrNotNull(row, "STRINDIRIZZORES");
			codComuneRes = SourceBeanUtils.getAttrStrNotNull(row, "CODCOMRES");
			strCapRes = SourceBeanUtils.getAttrStrNotNull(row, "STRCAPRES");
			strIndirizzoDom = SourceBeanUtils.getAttrStrNotNull(row, "STRINDIRIZZODOM");
			codComuneDom = SourceBeanUtils.getAttrStrNotNull(row, "CODCOMDOM");
			strCapDom = SourceBeanUtils.getAttrStrNotNull(row, "STRCAPDOM");
			strCodSap= SourceBeanUtils.getAttrStrNotNull(row, "STRCODSAP");
			codCpi =  SourceBeanUtils.getAttrStrNotNull(row, "CODCPIMIN");
			strTelefono = SourceBeanUtils.getAttrStrNotNull(row, "STRTEL");
			strEmail= SourceBeanUtils.getAttrStrNotNull(row, "STREMAIL");
			nazionalita = SourceBeanUtils.getAttrStrNotNull(row, "NAZIONALITA");
			descrComNas = SourceBeanUtils.getAttrStrNotNull(row, "STRCOMNAS");
			descrComRes = SourceBeanUtils.getAttrStrNotNull(row, "STRCOMRES");
			descrComDom = SourceBeanUtils.getAttrStrNotNull(row, "STRCOMDOM");
			descrCpiMin = SourceBeanUtils.getAttrStrNotNull(row, "DESCRCPIMIN");
			descrCodMonoRuolo = SourceBeanUtils.getAttrStrNotNull(row, "DESCRCODMONORUOLO");
			utenteInserimento = SourceBeanUtils.getAttrStrNotNull(row, "utenteInserimento");
			utenteModifica = SourceBeanUtils.getAttrStrNotNull(row, "utenteModifica");
		}
	}		

	// Sola lettura: viene usato per tutti i campi di input
	String nomeClass = "main";
	
	// Stringhe con HTML per layout tabelle
  	
  	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
  	
%>

<html>
<head>
<title><%= titolo %></title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
<link rel="stylesheet" type="text/css" href="../../css/stiliTemplate.css"/>

<af:linkScript path="../../js/"/>
<%@ include file="../global/fieldChanged.inc" %>

<%-- INCLUDERE QUI ALTRI JAVASRIPT CON CLAUSOLE DEL TIPO:
<script language="Javascript" src="../../js/xxx.js"></script>
--%>
 
<script language="Javascript">

	/* Funzione per tornare alla pagina precedente */
  function tornaLista() {
	if (isInSubmit()) return;
	<%
	// Recupero l'eventuale URL generato dalla LISTA precedente
	String token = "_TOKEN_" + paginaIndietro;
	String goBackListUrl = (String) sessionContainer.getAttribute(token.toUpperCase());	
	%>
	setWindowLocation("AdapterHTTP?<%= StringUtils.formatValue4Javascript(goBackListUrl) %>");
  }
	
	/* Funzione chiamata al caricamento della pagina */
	function onLoad() {
		rinfresca();
		// altri funzioni da richiamare sulla onLoad...
	}
	
	function tornaAlCruscotto(){
		document.Frm1.PAGE.value= "CruscottoRDCPage"; 
		return true;
	}

<%
	// Genera il Javascript che si occuperà di inserire i links nel footer
	if (StringUtils.isFilledNoBlank(cdnLavoratore)){
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

<body class="gestione">
<%
//if(!fromMenuGenerale){
	if(StringUtils.isFilledNoBlank(cdnLavoratore)){
		InfCorrentiLav testata = new InfCorrentiLav(RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
		testata.show(out);
	}
	
	if(!fromCruscotto && !fromMenuGenerale){
		Linguette l  = new Linguette(user, Integer.parseInt(cdnfunzioneStr), paginaIndietro, new BigDecimal(cdnLavoratore));
		l.show(out);
	}
//}else{
%>
<%if(StringUtils.isFilledNoBlank(cdnLavoratore)){ %>
<script language="Javascript">
			window.top.menu.caricaMenuLav(<%=cdnfunzioneStr%>,<%=cdnLavoratore%>);
		</script>
<%}%>
<br/>
<p class="titolo"><%= titolo %></p>

	<p>
	 	<font color="green">
	 		<%-- <af:showMessages prefix="M_DettaglioNotificaRDC"/> --%>
	  	</font>
	  	<font color="red"><af:showErrors /></font>
	</p>

<af:form name="Frm1" action="AdapterHTTP" method="POST"  >
<input type="hidden" name="PAGE" value="CruscottoRDCPage" />
<af:textBox type="hidden" name="cdnfunzione" value="<%= cdnfunzioneStr %>" />
<af:textBox type="hidden" name="cdnLavoratore" value="<%= Utils.notNull(cdnLavoratore) %>" />
 
	<%out.print(htmlStreamTop);%>
	<table class="main">
		<tr>
	        <td class="etichetta">
	       		ID Comunicazione
	        </td>
			<td class="campo">
			 <af:textBox name="idComunicazione" type="text"
							title="ID Comunicazione"
							value="<%= idComunicazione %>"
							size="40" maxlength="101"
							classNameBase="input"
							readonly="true"
				/>
	 		</td>
		</tr>
		<tr>
	        <td class="etichetta">
	       		Data Invio
	        </td>
			<td class="campo">
			 <af:textBox name="dataInvio" type="text"
							title="Data Invio"
							value="<%= strDataInvio %>"
							size="15" maxlength="101"
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
							size="30" maxlength="101"
							classNameBase="input"
							readonly="true"
				/>
	 		</td>
		</tr>
		<tr>
	        <td class="etichetta">
	       		Stado Domanda INPS
	        </td>
			<td class="campo">
			 <af:textBox name="domandaInps" type="text"
							title="Stato Domanda INPS"
							value="<%= statoDomInps %>"
							size="30" maxlength="101"
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
				 <af:textBox name="descrRuolo" type="text"
							 title="Descr ruolo"
							value="<%= descrCodMonoRuolo %>"
							size="50"  
							classNameBase="input"
							readonly="true"
				/>
	 		</td>
		</tr>
	</table>
	
	<div align="center">
 	<table class="main">
 			<tr>
				<td><br /></td>
			</tr>
			<tr>
				<td colspan="4"><p class="titolo"><%=strCodiceFiscale.toUpperCase() %>&nbsp;-&nbsp;<%=strCognome.toUpperCase() %>&nbsp;-&nbsp;<%=strNome.toUpperCase() %></p></td>
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
			    <af:textBox name="codCittadDescr" type="text"
							title="nazionalita"
							value="<%= nazionalita %>"
							size="50"  
							classNameBase="input"
							readonly="true"
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
				<af:textBox name="descrcomNas" type="text"
							title="Descr com nas"
							value="<%= descrComNas %>"
							size="50"  
							classNameBase="input"
							readonly="true"
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
				<af:textBox name="descrcomRes" type="text"
							title="Descr com res"
							value="<%= descrComRes %>"
							size="50"  
							classNameBase="input"
							readonly="true"
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
				<af:textBox name="descrcomDom" type="text"
							title="Descr com dom"
							value="<%= descrComDom %>"
							size="50"  
							classNameBase="input"
							readonly="true"
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
	       		CPI
	        </td>
			<td class="campo">
				 <af:textBox name="codCPI" type="text"
							title="CPI"
							value="<%= codCpi %>"
							size="15"  
							classNameBase="input"
							readonly="true"
				/>&nbsp;-&nbsp;
				<af:textBox name="descrCpiMin" type="text"
							title="Descr cpi min"
							value="<%= descrCpiMin %>"
							size="50"  
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
				<td colspan="2" align="center">
				<%if(!fromCruscotto){ %>
					<input class="pulsante" type = "button" name="torna" value="Torna alla lista" onClick="tornaLista();"/>
				<%}else{ %>
					<input class="pulsante" type = "submit" name="torna" value="Torna al cruscotto" />
				<%}%>
				</td>
			</tr>
</table>
	 <%out.print(htmlStreamBottom);%>

<center>
  	<table>
  		<tbody>
  			<tr>
	  			<td align="center">
					<table class="info_mod" align="center">
						<tr>
							<td class="info_mod">Inserimento</td>
							<td class="info_mod"><b><%= utenteInserimento %></b></td>
							<%if(StringUtils.isFilledNoBlank(utenteModifica)){ %>
							<td class="info_mod">Ultima Modifica</td>
							<td class="info_mod"><b><%= utenteModifica %></b></td>
							<%}%>
						</tr>
					</table>
				</td>
			</tr>
		</tbody>
	</table>
</center>
</af:form>

</body>
</html>
