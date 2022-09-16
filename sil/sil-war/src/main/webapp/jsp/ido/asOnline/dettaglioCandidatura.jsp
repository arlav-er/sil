<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../../global/noCaching.inc" %>
<%@ include file="../../global/getCommonObjects.inc" %>

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
                  it.eng.sil.coop.webservices.art16online.istanze.xsd.types.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  java.lang.*,
                  java.text.*"%>
 

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%@ taglib uri="aftags" prefix="af"%>

<%
boolean canModify = false;
String readonly = String.valueOf( ! canModify );
String _page = (String) serviceRequest.getAttribute("PAGE");


String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
 PageAttribs attributi = new PageAttribs(user,_page);

ProfileDataFilter filter = new ProfileDataFilter(user, _page);

if (!filter.canView()) {
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	return;
}  

String titlePagina = "Dettaglio candidatura";
 
SourceBean dettaglioCandidatura = null;
String codiceFiscale ="";
String codEsito = "";
String descrEsito = "";
CandidaturaType candidatura = null;

if(serviceResponse.containsAttribute("M_GETDETTAGLIOCANDIDATURA.ROWS.ROW")){
	dettaglioCandidatura = (SourceBean) serviceResponse.getAttribute("M_GETDETTAGLIOCANDIDATURA.ROWS");
	codiceFiscale = dettaglioCandidatura.getAttribute("ROW.STRCODICEFISCALE").toString();
	codEsito = dettaglioCandidatura.getAttribute("ROW.CODESITO").toString();
	if(codEsito.equalsIgnoreCase("OK")){
		descrEsito ="Candidatura caricata con successo";
	}else if(codEsito.equalsIgnoreCase("01")){
		descrEsito ="Anagrafica non inseribile";
	}else if(codEsito.equalsIgnoreCase("02")){
		descrEsito ="Aggiornamento anagrafica non riuscito";
	}else if(codEsito.equalsIgnoreCase("03")){
		descrEsito ="Aggiornamento dati ISEE non riuscito";
	}else if(codEsito.equalsIgnoreCase("04")){
		descrEsito ="Errore aggiornamento dati graduatoria";
	}else if(codEsito.equalsIgnoreCase("99")){
		descrEsito ="Errore generico";
	}
}
	candidatura = (CandidaturaType) serviceResponse.getAttribute("M_GETDETTAGLIOCANDIDATURA.XSDCandidatura");
	
	String comuneNascita =  serviceResponse.getAttribute("M_GETDETTAGLIOCANDIDATURA.comuneNascita").toString();
	String cittadinanza = serviceResponse.getAttribute("M_GETDETTAGLIOCANDIDATURA.cittadinanza").toString();
	String comuneResidenza =  serviceResponse.getAttribute("M_GETDETTAGLIOCANDIDATURA.comuneRes").toString();
	
	AnagraficaType anagrafica = candidatura.getAnagrafica(); //required
	String dataNascita  =  DateUtils.formatXMLGregorian(anagrafica.getDatanascita());
	ResidenzaType residenza = candidatura.getResidenza(); //required
	ContattiType contatti = candidatura.getContatti(); //required
	ExtraUEType imm = candidatura.getExtraUE();
	String motivoPermesso = null;
	String titoloSoggiorno = null;
	String dataPermesso  = null;
	if(imm!=null){
		motivoPermesso =  serviceResponse.getAttribute("M_GETDETTAGLIOCANDIDATURA.descrMotivoPermesso").toString();
		titoloSoggiorno =  serviceResponse.getAttribute("M_GETDETTAGLIOCANDIDATURA.descrTitoloSoggiorno").toString();
		dataPermesso  =  DateUtils.formatXMLGregorian(imm.getScadenzatitolosogg());
	}
	ISEEType isee = candidatura.getISEE();
	String dataRif = null;
	if(isee!=null){
		dataRif =  DateUtils.formatXMLGregorian(isee.getDatainizio());
	}
	IstanzaType istanza = candidatura.getIstanza(); //required
	String dataCandidatura =  DateUtils.formatXMLGregorian(istanza.getDatacandidatura());
	
	String htmlStreamTop = StyleUtils.roundTopTable(true);
	String htmlStreamBottom = StyleUtils.roundBottomTable(true);
  	String htmlStreamTopCoop = StyleUtils.roundTopTable("prof_ro_coop");
  	String htmlStreamBottomCoop = StyleUtils.roundBottomTable("prof_ro_coop");
%>
 
	<html>
	<head>
	<title><%=titlePagina %></title>
	<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
	<link rel="stylesheet" type="text/css" href="../../css/listdetail.css" />
	<link rel="stylesheet" type="text/css" href="../../css/stiliTemplate.css"/>
 	
	<af:linkScript path="../../js/" />
 
	
	</head>
	<body class="gestione">
 	<p align="center">
	  <font color="green"><af:showMessages prefix="M_GetDettaglioCandidatura" /></font>
	  <font color="red"><af:showErrors /></font>
	</p>
	 
	<p class="titolo">Esito candidatura: <%=codEsito %>&nbsp;-&nbsp;<%=descrEsito%></p>
	<af:form name="form1" action="AdapterHTTP" method="POST">
	
	<% out.print(htmlStreamTopCoop); %>
 
	<table class="maincoop">
		<tr>
			<td colspan="2">
				<div class="sezione2">Anagrafica</div>
			</td>
		</tr>
		<tr>
			<td nowrap class="etichettacoop">Codice Fiscale</td>
			<td class="campocoop">
				<af:textBox name="cf" type="text" classNameBase="input"
						value="<%= anagrafica.getCodicefiscale() %>"
						size="20" 
						readonly="<%=readonly %>" />
			</td>
		</tr>
		<tr>
			<td nowrap class="etichettacoop">Cognome</td>
			<td class="campocoop">
				<af:textBox name="co" type="text" classNameBase="input"
						value="<%= anagrafica.getCognome() %>"
						size="50" 
						readonly="<%=readonly %>" />
			</td>
		</tr>
		 <tr>
			<td nowrap class="etichettacoop">Nome</td>
			<td class="campocoop">
				<af:textBox name="no" type="text" classNameBase="input"
						value="<%= anagrafica.getNome() %>"
						size="50" 
						readonly="<%=readonly %>" />
			</td>
		</tr>
		 <tr>
			<td nowrap class="etichettacoop">Sesso</td>
			<td class="campocoop">
				<af:textBox name="sess" type="text" classNameBase="input"
						value="<%= anagrafica.getSesso() %>"
						size="3" 
						readonly="<%=readonly %>" />
			</td>
		</tr>
		 <tr>
			<td nowrap class="etichettacoop">Luogo di nascita</td>
			<td class="campocoop">
				<af:textBox name="ln" type="text" classNameBase="input"
						value="<%= comuneNascita %>"
						size="50" 
						readonly="<%=readonly %>" />
			</td>
		</tr>
		 <tr>
			<td nowrap class="etichettacoop">Data di nascita</td>
			<td class="campocoop">
				<af:textBox name="dn" type="text" classNameBase="input"
						value="<%= dataNascita %>"
						size="15" 
						readonly="<%=readonly %>" />
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<div class="sezione2">Residenza</div>
			</td>
		</tr>
		<tr>
			<td nowrap class="etichettacoop">Comune</td>
			<td class="campocoop">
				<af:textBox name="cr" type="text" classNameBase="input"
						value="<%= comuneResidenza %>"
						size="50" 
						readonly="<%=readonly %>" />
			</td>
		</tr>
		<tr>
			<td nowrap class="etichettacoop">Indirizzo</td>
			<td class="campocoop">
				<af:textBox name="ind" type="text" classNameBase="input"
						value="<%= residenza.getIndirizzo() %>"
						size="90" 
						readonly="<%=readonly %>" />
			</td>
		</tr>
		<tr>
			<td nowrap class="etichettacoop">Cap</td>
			<td class="campocoop">
				<af:textBox name="cap" type="text" classNameBase="input"
						value="<%= residenza.getCap() %>"
						size="8" 
						readonly="<%=readonly %>" />
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<div class="sezione2">Contatti</div>
			</td>
		</tr>
		<tr>
			<td nowrap class="etichettacoop">Email</td>
			<td class="campocoop">
				<af:textBox name="email" type="text" classNameBase="input"
						value="<%= contatti.getEmail() %>"
						size="50" 
						readonly="<%=readonly %>" />
			</td>
		</tr>
		<tr>
			<td nowrap class="etichettacoop">Telefono</td>
			<td class="campocoop">
				<af:textBox name="tel" type="text" classNameBase="input"
						value="<%= contatti.getTelefono() %>"
						size="20" 
						readonly="<%=readonly %>" />
			</td>
		</tr>
		<tr>
			<td nowrap class="etichettacoop">Cellulare</td>
			<td class="campocoop">
				<af:textBox name="cell" type="text" classNameBase="input"
						value="<%= contatti.getCellulare() %>"
						size="20" 
						readonly="<%=readonly %>" />
			</td>
		</tr>
		<%if(imm!=null){ %>
		<tr>
			<td colspan="2">
				<div class="sezione2">Extra UE</div>
			</td>
		</tr>
		<tr>
			<td nowrap class="etichettacoop">Numero titolo di soggiorno</td>
			<td class="campocoop">
				<af:textBox name="titn" type="text" classNameBase="input"
						value="<%= imm.getNumerotitolosogg() %>"
						size="20" 
						readonly="<%=readonly %>" />
			</td>
		</tr>
		<tr>
			<td nowrap class="etichettacoop">Titolo di soggiorno</td>
			<td class="campocoop">
				<af:textBox name="tit" type="text" classNameBase="input"
						value="<%= titoloSoggiorno%>"
						size="30" 
						readonly="<%=readonly %>" />
			</td>
		</tr>
		<tr>
			<td nowrap class="etichettacoop">Motivo permesso</td>
			<td class="campocoop">
				<af:textBox name="perm" type="text" classNameBase="input"
						value="<%= motivoPermesso%>"
						size="30" 
						readonly="<%=readonly %>" />
			</td>
		</tr>
		<tr>
			<td nowrap class="etichettacoop">Data scadenza titolo</td>
			<td class="campocoop">
				<af:textBox name="dp" type="text" classNameBase="input"
						value="<%= dataPermesso%>"
						size="15" 
						readonly="<%=readonly %>" />
			</td>
		</tr>
		<%}%>
		<%if(isee!=null){ %>
		<tr>
			<td colspan="2">
				<div class="sezione2">ISEE</div>
			</td>
		</tr>
		<tr>
			<td nowrap class="etichettacoop">Valore</td>
			<td class="campocoop">
				<af:textBox name="val" type="text" classNameBase="input"
						value="<%= isee.getValore().toPlainString() %>"
						size="20" 
						readonly="<%=readonly %>" />
			</td>
		</tr>
		<tr>
			<td nowrap class="etichettacoop">Data</td>
			<td class="campocoop">
				<af:textBox name="di" type="text" classNameBase="input"
						value="<%= dataRif%>"
						size="15" 
						readonly="<%=readonly %>" />
			</td>
		</tr>
		<tr>
			<td nowrap class="etichettacoop">Anno reddito</td>
			<td class="campocoop">
				<af:textBox name="anno" type="text" classNameBase="input"
						value="<%= isee.getNumannoreddito()!= null? isee.getNumannoreddito().toString() : \"\"%>"
						size="6" 
						readonly="<%=readonly %>" />
			</td>
		</tr>
		<%}%>
		<tr>
			<td colspan="2">
				<div class="sezione2">Istanza</div>
			</td>
		</tr>
		<tr>
			<td nowrap class="etichettacoop">Data candidatura</td>
			<td class="campocoop">
				<af:textBox name="datC" type="text" classNameBase="input"
						value="<%= dataCandidatura%>"
						size="15" 
						readonly="<%=readonly %>" />
			</td>
		</tr>
		<tr>
			<td nowrap class="etichettacoop">Anno protocollo</td>
			<td class="campocoop">
				<af:textBox name="annoProt" type="text" classNameBase="input"
						value="<%= String.valueOf(istanza.getAnnoprotocollo()) %>"
						size="6"  
						readonly="<%=readonly %>" />
			</td>
		</tr>
		<tr>
			<td nowrap class="etichettacoop">Protocollo</td>
			<td class="campocoop">
				<af:textBox name="prot" type="text" classNameBase="input"
						value="<%= istanza.getProtocollo() %>"
						size="30"  
						readonly="<%=readonly %>" />
			</td>
		</tr>
		<tr>
			<td nowrap class="etichettacoop">Identificativo istanza</td>
			<td class="campocoop">
				<af:textBox name="idistanza" type="text" classNameBase="input"
						value="<%= istanza.getIdistanza() %>"
						size="30"  
						readonly="<%=readonly %>" />
			</td>
		</tr>
	</table>
	
	<% out.print(htmlStreamBottomCoop); %>
	<center><table>
			<tr>
            <td colspan="2"> 
                <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onclick="window.close();"/>
             </td>
        	</tr>
	</table>
	</center>
	</af:form>
	 
	</body>
	</html>