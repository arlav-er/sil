<%@page import="it.gov.mlps.DataModels.InformationDelivery.ConferimentoDID._1_0.TipoEvento"%>
<%@ page
	contentType="text/html;charset=utf-8"
	
	import="javax.xml.datatype.XMLGregorianCalendar,
			it.eng.sil.module.conf.did.ConferimentoUtility,
			com.engiweb.framework.base.*,
			it.eng.sil.pojo.yg.sap.due.*,
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
	String  titolo = "Esito Conferimento DID";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	// Lettura parametri dalla REQUEST
	String  _funzione   = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "cdnfunzione");
	String  cdnLavoratore    = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"cdnLavoratore");
 	String prgConferimentoDID =  null;
 	
 	String codTipoEvento = null;		
 	String codStatoInvio = null;
	String numKLoConfDid = null;
	String codiceFiscale = null;
	String entePromotore = null;
	String codiceEnteTit = null;
	String dataDichiarazione = null;
	
	String indiceSvantaggio = null;
	String idProfiling = null;
	String condOccupaz_calc = null;
	String mesiDisocc_calc = null;
	boolean showProfiling = true;
	
	boolean esitoAnpal = false;
	
	if(serviceResponse.containsAttribute("M_CCD_ConvalidaDIDMin.ESITO_ANPAL") 
			||  serviceResponse.containsAttribute("M_CCD_NuovoConfDIDMin.ESITO_ANPAL")
			||  serviceResponse.containsAttribute("M_CCD_RevocaDIDMin.ESITO_ANPAL") ){
		titolo = "Esito Anpal Conferimento DID";
		esitoAnpal = true;
	}
	 
	SourceBean dettaglioProfiling = (SourceBean) serviceResponse.getAttribute("M_CCD_GET_Conferimento_Did_From_Prg.ROWS.ROW");
	if(dettaglioProfiling != null){
		prgConferimentoDID =  dettaglioProfiling.getAttribute("PRGCONFERIMENTODID").toString();
        numKLoConfDid = dettaglioProfiling.getAttribute("NUMKLOCONFDID").toString();
        codiceEnteTit = StringUtils.getAttributeStrNotNull(dettaglioProfiling,"CODENTETIT");
        codTipoEvento = dettaglioProfiling.getAttribute("CODPFTIPOEVENTO").toString();
        dataDichiarazione = dettaglioProfiling.getAttribute("DATDID").toString();
        codStatoInvio =   StringUtils.getAttributeStrNotNull(dettaglioProfiling,"CODMONOSTATOINVIO");
        idProfiling = StringUtils.getAttributeStrNotNull(dettaglioProfiling,"IDSPROFILING"); 
		condOccupaz_calc = StringUtils.getAttributeStrNotNull(dettaglioProfiling,"CODPFCONDOCCUP_CALC"); 
		if(dettaglioProfiling.containsAttribute("NUMMESIDISOCC_CALC")){
        	BigDecimal nMesiCalc  = (BigDecimal)dettaglioProfiling.getAttribute("NUMMESIDISOCC_CALC") ;
        	mesiDisocc_calc = String.valueOf(nMesiCalc);
        }
		if(dettaglioProfiling.containsAttribute("DECPROFILING")){
        	BigDecimal nIndice  = (BigDecimal)dettaglioProfiling.getAttribute("DECPROFILING") ;
        	indiceSvantaggio = String.valueOf(nIndice);
        }
		codiceFiscale = StringUtils.getAttributeStrNotNull(dettaglioProfiling,"strcodicefiscale");
	}
	SourceBean didEnte =(SourceBean) serviceResponse.getAttribute("M_CCD_ENTE_PROMOTORE_FROM_CDNLAV.ROWS.ROW");
	if (didEnte != null ) {
		codiceEnteTit = StringUtils.getAttributeStrNotNull(didEnte, "codice");
		entePromotore = codiceEnteTit;
		if(StringUtils.isFilledNoBlank(entePromotore)){
			entePromotore += " - ";
		}
		entePromotore += StringUtils.getAttributeStrNotNull(didEnte, "descrizione"); 
	}
	
	if(codTipoEvento!=null && codTipoEvento.equals(ConferimentoUtility.EVENTOREVOCA)){
		showProfiling = false;
	}
	
	// Sola lettura: viene usato per tutti i campi di input
	String readonly = "true";
	boolean  canModify= false;
	// Stringhe con HTML per layout tabelle
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
	 
%>

<html>
<head>
<title><%= titolo %></title>
  	<link rel="stylesheet" type="text/css" href="../../css/stili.css">
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
 	}
	
	function goToCruscottoDid(){
		if (isInSubmit()) return;
	      url="AdapterHTTP?PAGE=SitAttualeConfDIDPage";
	      url += "&CDNFUNZIONE="+"<%=_funzione%>";      
	      url += "&cdnLavoratore="+"<%=cdnLavoratore%>";     
 	      setWindowLocation(url);
	}
 
</script>
</head>

<body class="gestione" onload="onLoad()">

<%
	// TESTATA LAVORATORE
	if (StringUtils.isFilled(cdnLavoratore)) {
		InfCorrentiLav testata = new InfCorrentiLav(cdnLavoratore, user);
		testata.show(out);
	}

%>
    <script language="Javascript">
      	if(window.top.menu != undefined){
    	    window.top.menu.caricaMenuLav( <%=_funzione%>,   <%=cdnLavoratore%>);
    	}
    </script>	
<p>
	<font color="green">
		<af:showMessages prefix="M_CCD_NuovoConfDIDMin"/>
		<af:showMessages prefix="M_CCD_ConvalidaDIDMin"/>
		<af:showMessages prefix="M_CCD_RevocaDIDMin"/>
  	</font>
  	<font color="red"><af:showErrors /></font>
</p>

<af:form name="Frm1" action="AdapterHTTP" method="POST">
<p class="titolo"><%= titolo %></p>

	<input type="hidden" name="PAGE" value="<%= _page %>"/>
	<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>">
	<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>" />
	 
 	
<%= htmlStreamTop %>
<table class="main">
<tr> 
    <td class="etichetta"> 
      Codice Fiscale
    </td>
    <td class="campo">    
      <af:textBox name="CODICEFISCALE" value="<%=Utils.notNull(codiceFiscale)%>" classNameBase="input"  
                  readonly="true" required="true" title="Codice Fiscale"  
                  size="20" />    
	</td>
	<td nowrap class="etichetta"> 
      Codice Ente Promotore
    </td>
    <td nowrap class="campo">     
      <af:textBox name="ENTEPROMOTORE" value="<%=Utils.notNull(entePromotore)%>" classNameBase="input"  
                  readonly="true" required="true" title="Codice Ente Promotore"  
                  size="40"/>
       
	</td>
</tr>
 <tr>
    <td  nowrap class="etichetta"> 
      Data Dichiarazione
    </td>
    <td class="campo">   
      <af:textBox name="DATDID" value="<%=Utils.notNull(dataDichiarazione)%>" 
      			classNameBase="input" type="date"
      			 validateOnPost="true" callBackDateFunction="controlliDataDid()"   
                  readonly="<%= readonly %>" required="true" title="Data Dichiarazione"  
                  size="12" maxlength="10" onKeyUp="fieldChanged()"/>    
	</td>
	 <td class="etichetta"> 
     Tipo Conferimento
    </td>
    <td class="campo">    
       <af:comboBox name="descrTipoEvento" classNameBase="input"
							moduleName="M_CCD_COMBO_MN_PF_TIPO_EVENTO" 
							selectedValue="<%=Utils.notNull(codTipoEvento)%>" 
							addBlank="true"
							title ="Tipo Conferimento"
							disabled ="true"
							required="false" />
	</td>
</tr>

<%if(showProfiling){ %>
<tr>
  <td colspan="4">
    <div class="sezione">Profiling</div>
  </td>
</tr>  

<%if(codStatoInvio!=null && codStatoInvio.equals(ConferimentoUtility.STATOINVIATO)){ %>
		<tr>
	 		<td  nowrap class="etichetta">Indice di svantaggio</td>
			<td class="campo">
				<af:textBox name="DECPROFILING" 
				type="text" 
				classNameBase="input"
				value="<%= Utils.notNull(indiceSvantaggio) %>"
 				readonly="<%= readonly %>" 
				size="11"
				title="Indice di svantaggio"
				required="false"
				 />
			</td>
	 		<td  nowrap class="etichetta">Identificativo Profiling</td>
			<td class="campo">
				<af:textBox name="IDSPROFILING" 
				type="text" 
				classNameBase="input"
				value="<%= Utils.notNull(idProfiling) %>"
 				readonly="<%= readonly %>" 
				size="5"
				title="Identificativo Profiling"
				required="false"
				/>
			</td>	
	</tr>
			<tr>
	 		<td  nowrap class="etichetta">Cond. Occupazionale 1 anno prima calc.</td>
			<td class="campo">
				<af:comboBox name="CODPFCONDOCCUP_CALC" classNameBase="input"
							moduleName="M_CCD_COMBO_PF_OCCUP" 
							selectedValue="<%= Utils.notNull(condOccupaz_calc) %>" 
							addBlank="true"
							title="Cond. Occupazionale 1 anno prima calc"
							disabled ="<%=readonly %>"
							required="false" />
			</td>
	 		<td  nowrap class="etichetta">Durata disoccupazione calcolata</td>
			<td class="campo">
			<af:textBox name="NUMMESIDISOCC_CALC" 
				type="text" 
				classNameBase="input"
				value="<%= Utils.notNull(mesiDisocc_calc) %>"
 				readonly="<%= readonly %>" 
				size="5"
				title="Durata disoccupazione calcolata"
				required="false"
				/>
			</td>	
	</tr>
<%	} %>
<%} %>

<tr>
	<td colspan="4">
	<center>
		<input type="button" class="pulsanti" name="chiudi" value="Chiudi" onclick="goToCruscottoDid();" />
		</center>
	</td>
</tr>

</table>
 
<%= htmlStreamBottom %>

</af:form>

</body>
</html>
