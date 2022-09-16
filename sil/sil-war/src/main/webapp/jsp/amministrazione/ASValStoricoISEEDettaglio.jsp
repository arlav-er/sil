<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %> 
<%@ include file="../global/getCommonObjects.inc" %>


<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.util.*,
                  it.eng.sil.util.amministrazione.impatti.Controlli,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.security.ProfileDataFilter,                     
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean,
                  it.eng.sil.module.movimenti.InfoLavoratore,
                  com.engiweb.framework.security.*,
                  java.text.*,
                  it.eng.afExt.utils.*"
%> 


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String  titolo = "Dettaglio storico dei Valori ISEE";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 
	int _cdnFunz 			= 	Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
	String cdnFunzione 		= 	(String) serviceRequest.getAttribute("CDNFUNZIONE");
	String cdncomponente 	= 	(String) serviceRequest.getAttribute("CDNCOMPONENTE");
	String cdnLavoratore 	= 	(String) serviceRequest.getAttribute("cdnLavoratore");
	String prgValIseeStorico= 	(String) serviceRequest.getAttribute("PRGVALISEESTORICO");
	

	// CONTROLLO PERMESSI SULLA PAGINA
	PageAttribs attributi = new PageAttribs(user, _page);

	Object cdnUtCorrente    = sessionContainer.getAttribute("_CDUT_");
	String cdnUtCorrenteStr = StringUtils.getStringValueNotNull(cdnUtCorrente);

	// Recupero la ROW contenuta nella RISPOSTA DI UN MODULO
	String datfineval 		= 	"";
	String datinizioval  	= 	"";
	BigDecimal numvaloreisee= 	null;
	BigDecimal numanno		= 	null;
	BigDecimal puntValIsee	=	null;
	BigDecimal prgValoreIsee	=	null;
	String strNota 			= 	"";	
	String strIbanNazione 	= 	"";
	String strIbanControllo = 	"";
	String strCinLav 		= 	"";
	String strAbiLav 		= 	"";
	String strCabLav 		= 	"";
	String strCCLav 		= 	"";			
	String strNumValoreIsee = 	"";
	String strNumAnno = 	"";
	String strPuntValoreIsee = 	"";
	String strMotivoMod = 	"";
	String strPrgValoreIsee    ="";
	
	
	BigDecimal cdnUtIns 	= 	null;
	String dtmIns 			= 	"";
	BigDecimal cdnUtMod 	= 	null;
	String dtmMod 			= 	"";
	String strUtSto 	    = 	"";
	String dtmStohh			= 	"";
	String dtmModhh			= 	"";	
	String dtmInshh			= 	"";	
	
	BigDecimal prgValore 	= null;
	Vector vett 			= serviceResponse.getAttributeAsVector("M_Load_ISEEStorico.ROWS.ROW");		
	
	if( vett != null && vett.size() > 0 ) {
		SourceBean sbValIsee = (SourceBean) vett.get(0);

		datinizioval 	= StringUtils.getAttributeStrNotNull(sbValIsee, "DATINIZIOVAL");					
		datfineval	= StringUtils.getAttributeStrNotNull(sbValIsee, "DATFINEVAL");
		 					
		dtmInshh	= StringUtils.getAttributeStrNotNull(sbValIsee, "DTMINSHH");
		dtmModhh	= StringUtils.getAttributeStrNotNull(sbValIsee, "DTMMODHH");
		dtmStohh 		= StringUtils.getAttributeStrNotNull(sbValIsee, "DTMSTOHH");
		
		cdnUtIns 	= (BigDecimal) sbValIsee.getAttribute("CDNUTINS"); 
		cdnUtMod	= (BigDecimal) sbValIsee.getAttribute("CDNUTMOD");
		strUtSto	= StringUtils.getAttributeStrNotNull(sbValIsee, "UTENTESTOR");
		
		prgValoreIsee = (BigDecimal) sbValIsee.getAttribute("PRGVALOREISEE");
		
		strPrgValoreIsee = prgValoreIsee.toString();
		
		numvaloreisee = (BigDecimal) sbValIsee.getAttribute("NUMVALOREISEE");
		puntValIsee = (BigDecimal) sbValIsee.getAttribute("NUMPUNTIISEE");
		numanno = (BigDecimal) sbValIsee.getAttribute("NUMANNO");
		strNota = StringUtils.getAttributeStrNotNull(sbValIsee, "STRNOTA");
		strMotivoMod = StringUtils.getAttributeStrNotNull(sbValIsee, "CODMONOMOTIVOMODIFICA");

		strIbanNazione = StringUtils.getAttributeStrNotNull(sbValIsee, "STRIBANNAZIONE");
		strIbanControllo = StringUtils.getAttributeStrNotNull(sbValIsee, "STRIBANCONTROLLO");
		strCinLav = StringUtils.getAttributeStrNotNull(sbValIsee, "STRCINLAVORATORE");
		strAbiLav = StringUtils.getAttributeStrNotNull(sbValIsee, "STRABILAVORATORE");
		strCabLav = StringUtils.getAttributeStrNotNull(sbValIsee, "STRCABLAVORATORE");
		strCCLav = StringUtils.getAttributeStrNotNull(sbValIsee, "STRCCLAVORATORE");					

		if (numanno == null) {
			strNumAnno = "";
			} else {
				strNumAnno = numanno.toString();
			}	
		
		if (numvaloreisee == null) {
			strNumValoreIsee = "";
			} else {
				strNumValoreIsee = numvaloreisee.toString();
			}
			
		if (puntValIsee == null) {
			strPuntValoreIsee = "";
			} else {
				strPuntValoreIsee = puntValIsee.toString();
			}		
	}
	//INFORMAZIONI OPERATORE
	Testata operatoreInfo 	= 	null;
	
	boolean canInsert 		= 	false;		
	boolean	readOnlyStr		=	true;
	
	String configIBAN = serviceResponse.containsAttribute("M_GetConfigIBAN.ROWS.ROW.NUM")?serviceResponse.getAttribute("M_GetConfigIBAN.ROWS.ROW.NUM").toString():"0";
	
	operatoreInfo= new Testata(cdnUtIns, dtmInshh, cdnUtMod, dtmModhh);		
	
	String htmlStreamTop 	= 	StyleUtils.roundTopTable(false);
	String htmlStreamBottom = 	StyleUtils.roundBottomTable(false);

%>

<html>
<head>
<title><%= titolo %></title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />

<af:linkScript path="../../js/"/>
<%@ include file="CommonScript.inc"%>

<script language="Javascript">
<%
	// Genera il Javascript che si occuperà di inserire i links nel footer
	attributi.showHyperLinks(out, requestContainer, responseContainer, "cdnLavoratore="+cdnLavoratore);
%>
</script>
</head>

<body class="gestione" onload="rinfresca();">

<p class="titolo"><%= titolo %></p>

<af:showErrors />


<af:form name="formStor" action="AdapterHTTP" method="POST">

<%= htmlStreamTop %>


<table class="main">
	<tr>
    	<td class="etichetta">Data inizio validit&agrave;</td>
    	<td colspan="6" class="campo">
      		<af:textBox classNameBase="input" type="date" name="inizio" value="<%=datinizioval%>"   
                  size="11" maxlength="10" title="Data d'inizio" readonly="true"/>
        </td>
	</tr>
	<tr>
    	<td class="etichetta">Data fine validit&agrave;</td>  
    	<td colspan="6" class="campo">
      		<af:textBox classNameBase="input" type="date" name="fine" value="<%=datfineval%>" 
                 size="11" maxlength="10" title="Data di fine" readonly="true"/>
        </td>
	</tr>
	<% 
	String typeValIsee = "integer";
	if (configIBAN.equals("1")) { 
		typeValIsee = "float";
	} %>
	<tr>
    	<td class="etichetta">Valore ISEE</td>
    	<td colspan="6" class="campo"><af:textBox classNameBase="input" type="<%=typeValIsee%>" name="numvaloreisee" value="<%=strNumValoreIsee%>"  
                   size="20" maxlength="38" title="Valore ISEE" readonly="true"/>
        </td>
	</tr>
	<tr>
    	<td class="etichetta">Punteggio ISEE</td>
    	<td colspan="6" class="campo"><af:textBox classNameBase="input" type="integer" name="numpuntiisee" value="<%=strPuntValoreIsee%>"   
                    size="20" maxlength="38" title="Punteggio ISEE" readonly="true"/>
        </td>
	</tr>
	<tr>
    	<td class="etichetta">Anno di riferimento del reddito</td>
    	<td colspan="6" class="campo">
     		<af:textBox classNameBase="input" type="integer" name="numanno" value="<%=strNumAnno%>"   
        			required="false" size="5" maxlength="4" title="Anno di riferimento" readonly="true"/>
        </td>
	</tr>
	<% if (configIBAN.equals("1")) { %>
	<tr>
    	<td class="etichetta">IBAN</td>
    	<td colspan="6" class="campo">
			<af:textBox classNameBase="input" title="Codice nazione" type="text" name="strIbanNazione" value="<%=strIbanNazione%>" 
					size="3" maxlength="2" readonly="true"/>			
			<af:textBox classNameBase="input" title="Codice controllo" type="integer" name="strIbanControllo" 
					value="<%=strIbanControllo%>" size="3" maxlength="2" readonly="true"/>		
			<af:textBox classNameBase="input" title="CIN" type="text" name="strCinLav" value="<%=strCinLav%>" 
					size="2" maxlength="1" readonly="true"/>
			<af:textBox classNameBase="input" title="ABI"  type="integer" name="strAbiLav" value="<%=strAbiLav%>" 
					size="6" maxlength="5" readonly="true"/>
			<af:textBox classNameBase="input" title="CAB"  type="integer" name="strCabLav" value="<%=strCabLav%>" 
					size="6" maxlength="5" readonly="true"/>
			<af:textBox classNameBase="input" type="text" name="strCCLav" value="<%=strCCLav%>" 
					size="15" maxlength="12" readonly="true"/>
        </td>
	</tr>	
	<% } %>
	<tr>
    	<td class="etichetta">Note</td>
    	<td colspan="6" class="campo">	
			<af:textArea classNameBase="textarea" name="strnota" value="<%=strNota%>"
                 cols="60" rows="4" maxlength="2000" readonly="true"/>
      	</td>
	</tr>  	
	<tr>
    	<td class="etichetta">Motivo Modifica</td>
    	<td colspan="6" class="campo"><af:textBox classNameBase="input" type="text" name="motivomodificaisee" value="<%=strMotivoMod%>"   
                    size="20" maxlength="38" title="Motivo modifica ISEE" readonly="true"/>
        </td>
	</tr>
	<tr>
    	<td class="etichetta">Utente Modifica</td>
    	<td colspan="6" class="campo"><af:textBox classNameBase="input" type="text" name="utentemodificaisee" value="<%=strUtSto%>"   
                    size="20" maxlength="38" title="Utente modifica" readonly="true"/>
        </td>
	</tr>
		<tr>
    	<td class="etichetta">Data e ora Modifica</td>
    	<td colspan="6" class="campo"><af:textBox classNameBase="input" type="text"  name="dataoramodisee" value="<%=dtmStohh%>"   
                    size="20" maxlength="38" title="Data e ora modifica" readonly="true"/>
        </td>
	</tr>
  	
</table>

<table>
	<tr>
		<td>
			<input class="pulsante" type="button" name="lista" value="Torna alla lista"
       		onClick="checkChange('AsValoreISEEStoricoPage','&cdnLavoratore=<%=cdnLavoratore%>&prgValoreIsee=<%=strPrgValoreIsee%>&CDNFUNZIONE=<%=cdnFunzione%>')"/>
       		                                                
       	</td>
    </tr>	 
</table>

</table>
	<%out.print(htmlStreamBottom);
		if (operatoreInfo!=null) {
	%>
  	<center>
  		<table>
  			<tr>
  				<td align="center">
	<%  operatoreInfo.showHTML(out);%>
  				</td>
  			</tr>
  		</table>
  	</center>
	<%}%>

</af:form>

</body>
</html>
