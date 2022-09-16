<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<!-- @author: Giordano Gritti -->

<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %> 

        
<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs, 
                  it.eng.sil.security.ProfileDataFilter,
                  it.eng.sil.util.*,
                  java.util.*,
                  it.eng.sil.util.amministrazione.impatti.Controlli,
                  com.engiweb.framework.error.*,
                  it.eng.afExt.utils.*,
                  com.engiweb.framework.security.*,
                  java.text.DateFormat,
                  java.text.SimpleDateFormat,
                  it.eng.sil.module.movimenti.InfoLavoratore,
                  java.math.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String cdnLavoratore 	= StringUtils.getAttributeStrNotNull(serviceRequest,"CDNLAVORATORE");
	String _page 			= (String) serviceRequest.getAttribute("PAGE"); 
	int _funzione=0;
	_funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
	
	String dataInizio	= StringUtils.getAttributeStrNotNull(serviceRequest,"DATAINIZIO");
	String ente			= StringUtils.getAttributeStrNotNull(serviceRequest,"ENTE");
	String cf_Ente		= StringUtils.getAttributeStrNotNull(serviceRequest,"CF_ENTE");
	String ind_Ente		= StringUtils.getAttributeStrNotNull(serviceRequest,"IND_ENTE");
	String numRich		= StringUtils.getAttributeStrNotNull(serviceRequest,"NUMRICH");
    String numAnno		= StringUtils.getAttributeStrNotNull(serviceRequest,"NUMANNO");
    String prgAvvSelezione	= StringUtils.getAttributeStrNotNull(serviceRequest,"PRGAVVSELEZIONE");
    //String qualifica	= StringUtils.getAttributeStrNotNull(serviceRequest,"QUALIFICA");
    //String rapporto	= StringUtils.getAttributeStrNotNull(serviceRequest,"RAPPORTO");
    //String esito		= StringUtils.getAttributeStrNotNull(serviceRequest,"ESITO");
    //String strnote	= StringUtils.getAttributeStrNotNull(serviceRequest,"STRNOTE");
    String rif			= "";
    String prgRichAz    = StringUtils.getAttributeStrNotNull(serviceRequest,"PRGRICHIESTAAZ");
    
    //protocollo
    String statoAtto	= "";
    BigDecimal anno		= null;
    BigDecimal num		= null;
    String data			= "";
    String ora			= "";
    String tipoDoc		= "";
    String rifDoc		= "";
    
    
    String onUpdate		= StringUtils.getAttributeStrNotNull(serviceRequest,"BTNAGGIORNA");    
    if(onUpdate.equals("Aggiorna")){
        rif = StringUtils.getAttributeStrNotNull(serviceRequest,"RIF");
    } else {        
        rif = numRich + "/" + numAnno;
    }
        
    String _backToList 	= StringUtils.getAttributeStrNotNull(serviceRequest,"_BACKTOLIST");
    
    //TESTATA 
    InfCorrentiLav infCorrentiLav	= null;
    Testata operatoreInfo 			= null;
    
    //profilatura
    boolean readOnlyStr		= false;
    boolean canModify		= true;
    boolean btnAggiorna		= true;
    
    
    PageAttribs attributi 	= 	new PageAttribs(user, _page);
    
    readOnlyStr 			=   !attributi.containsButton("AGGIORNA");
    canModify     			=	attributi.containsButton("AGGIORNA");
	btnAggiorna				=	canModify && btnAggiorna;
	
	Vector row = serviceResponse.getAttributeAsVector("M_AS_AVVIAMENTI_STATOATTO.ROWS.ROW");
	SourceBean sb = null;
	//questi due moduli servono per selected value nella combo, controllano il record nella AS_AVV_SELEZIONE
	Vector vet = serviceResponse.getAttributeAsVector("M_AS_AVVIAMENTI_QUALIFICA_SEL.ROWS.ROW");
	SourceBean qual = null;	
	Vector vect = serviceResponse.getAttributeAsVector("M_AS_AVVIAMENTI_RAPPORTO_SEL.ROWS.ROW");
	SourceBean rap = null;
	Vector ve = serviceResponse.getAttributeAsVector("M_AS_AVVIAMENTI_ESITO_SEL.ROWS.ROW");
	SourceBean esit = null;
	Vector vt = serviceResponse.getAttributeAsVector("M_AS_AVVIAMENTI_NOTA_SEL.ROWS.ROW");
	SourceBean not = null;
	
%>
<%
	String htmlStreamTop 	= StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
	
	// POPUP EVIDENZE
	
	String strApriEv = StringUtils.getAttributeStrNotNull(serviceRequest, "APRI_EV");
	int _fun = 1;
	if(_funzione>0) { _fun = _funzione; }
	EvidenzePopUp jsEvid = null;
	boolean bevid = attributi.containsButton("EVIDENZE");
	if(strApriEv.equals("1") && bevid) {
		jsEvid = new EvidenzePopUp(cdnLavoratore, _fun, user.getCdnGruppo(), user.getCdnProfilo());
	}
	
%> 

<html>
<head>
<title>Avviamento a selezione / a lavoro</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
<af:linkScript path="../../js/"/>
<%if(strApriEv.equals("1") && bevid) { jsEvid.show(out); }%>
<script language="Javascript">
<%
    //attributi.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore);
%>

function fieldChanged() {
   	<%if (canModify) {out.print("flagChanged = true;");}%>
}  

</script>
<style>
table.sezione2 {
	border-collapse:collapse;
	font-family: Verdana,"Times New Roman", Arial, Sans-serif; 
	font-size: 12px;
	font-weight: bold;
	border-bottom-width: 2px; 
	border-bottom-style: solid;
	border-bottom-color: #000080;
	color:#000080; 
	position: relative;
	width: 94%;
	left: 4%;
	text-align: left;
	text-decoration: none;	
}
</style>
</head>
<body  class="gestione" onLoad="rinfresca();<%if(strApriEv.equals("1") && bevid) { %> apriEvidenze() <%}%>">
<%
   // TESTATA
	InfCorrentiLav testata = new InfCorrentiLav(cdnLavoratore, user);
	testata.show(out);
	
%>

<font color="red"><af:showErrors/></font>
<div><font color="red">
</font></div>
<font color="green">
<af:showMessages prefix="M_AS_UPD_AVVIAMENTI"/></font>

<af:form name="Frm1" method="POST" action="AdapterHTTP" >
<input type="hidden" name="PAGE" value="AS_DETTAGLIO_AVVIAMENTI_PAGE"/>
<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
<input name="cdnLavoratore" type="hidden" value="<%=cdnLavoratore%>"/>
<input name="prgAvvSelezione" type="hidden" value="<%=prgAvvSelezione%>"/>
<input name="PRGRICHIESTAAZ" type="hidden" value="<%=prgRichAz%>"/>

<input name="_backToList" type="hidden" value="<%=_backToList%>"/>


<p align="center">
<table class="main">
	<tr>
		<td class="titolo" colspan="5"><p class="titolo">Avviamento a selezione / a lavoro</p>
		</td>
	</tr>
	<tr>
		<td><br/>
		</td>
	</tr>
</table>

<%out.print(htmlStreamTop);%>
<!-- protocollo -->
<%
   for(int i=0;i<row.size();i++){
   		sb 			= (SourceBean)row.get(i);
   		statoAtto 	= StringUtils.getAttributeStrNotNull(sb,"ATTO"); 
   		anno 		= (BigDecimal) sb.getAttribute("NUMANNOPROT");   		
	    num			= (BigDecimal) sb.getAttribute("NUMPROTOCOLLO");
	    data		= StringUtils.getAttributeStrNotNull(sb,"DATAPROT");
	    ora			= StringUtils.getAttributeStrNotNull(sb,"ORAPROT");
	    tipoDoc		= StringUtils.getAttributeStrNotNull(sb,"DOC");
	    rifDoc		= StringUtils.getAttributeStrNotNull(sb,"RIF");
	    
	   	String strNumAnno =	"";
			if (anno == null) {
				strNumAnno = "";
			} else {
					strNumAnno = anno.toString();
			}    
			
	   	String strNumProt =	"";
			if (num == null) {
				strNumProt = "";
			} else {
					strNumProt = num.toString();
			}							           		
    
%>	
<table class="main" border="0" width="100%">
<tr>
  <td colspan="6" class="azzurro_bianco">    
		<table cellpadding="0" cellspacing="0" border="0" >
		    <tr>
		    	<td class="etichetta2">Stato atto</td>
		        <td>
		        	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		        		<tr>        					
		        			<td>
		        				<af:textBox classNameBase="input" type="text" name="statoAtto" value="<%=statoAtto!=null ? statoAtto : \"\"%>"
		                                     readonly="true" title="Stato Atto" size="30" maxlength="30" onKeyUp="fieldChanged();"/>
		        			</td>
		        			<td align="right">anno&nbsp;</td>
		        			<td>
		        				<af:textBox classNameBase="input" type="text" name="anno" value="<%=strNumAnno%>"
		                                     readonly="true" title="Anno protocollo" size="5" maxlength="5" onKeyUp="fieldChanged();"/>
							</td>
		        			<td align="right"> &nbsp;&nbsp;num.&nbsp;</td>
		        			<td><af:textBox classNameBase="input" type="text" name="numProt" value="<%=strNumProt%>"
		                                     readonly="true" title="Numero protocollo" size="8" maxlength="100" onKeyUp="fieldChanged();"/>
		                    </td>
		        			<td class="etichetta2">data
		        		        <af:textBox name="dataProt" type="date" value="<%=data%>" size="11" maxlength="10"
		                       				title="data di protocollazione" classNameBase="input" readonly="true" validateOnPost="true" 
		                       				required="false" trim ="false" onKeyUp="cambiAnnoProt(this,annoProt)" onBlur="checkFormatDate(this)"/>
		            		</td>
		        			<td class="etichetta2">ora
		           				<af:textBox name="oraProt" type="text" value="<%=ora%>" size="6" maxlength="5" title="data di protocollazione"  
		                       				classNameBase="input" readonly="true" validateOnPost="false" required="false" trim ="false"/>
		                    </td>
		   				 </tr>
		    		</table>
		    	</td>
		    </tr>		    
		    <tr>
				<td class="etichetta2">Doc. di		    	
		    	</td>    	
		    	<td>		    	
		    		<table cellpadding="0" cellspacing="0" border="0" width="100%">
		        		<tr>		
		    				<td class="campo2">
				               <af:textBox 	name="tipoDoc" type="text" value="<%= tipoDoc%>" size="6"  maxlength="6"
		                       			   	title="tipo documento" classNameBase="input" readonly="true" validateOnPost="false" 
		                       				required="false" trim ="false" />				               
				            </td>
		            		<td class="etichetta2">Rif.</td>
		            		<td class="campo2">
		               			<af:textBox name="rifDoc" type="text" value="<%=rifDoc%>" 
		                           			title="riferimento" classNameBase="input" readonly="true"
		                           			validateOnPost="false" required="false" trim ="false"/>
		                    </td>
		        		</tr>
		       		</table>
		    	</td>
		    </tr>    
		</table>       
      </td>
    </tr>
</table>
<%}%>
<table class="main">
	<tr>
	  <td class="etichetta">Data sel. </td>
	  <td class="campo">
	    <af:textBox name="dataInizio"
		      		 title="Data Avviamento"
		      		 type="date" 
		             size="11"
		             maxlength="10"			             
		             validateOnPost="true"
		             onKeyUp="fieldChanged();"
		             classNameBase="input"
		             readonly="true"
		             value="<%=dataInizio%>"/>
		</td>
	</tr>
	
	<tr>
	  <td class="etichetta">Ente</td>
	  <td class="campo">
	    <af:textBox name="ente"
		      		 title="Ente"
		      		 type="text"
		      		 size="50"
		             maxlength="1000"			             
		             validateOnPost="true"
		             onKeyUp="fieldChanged();"
		             classNameBase="input"
		             readonly="true"
		             value="<%=ente%>"/>
		</td>
	</tr>    	
	<tr>
	  <td class="etichetta">CF Ente</td>
	  <td class="campo">
	    <af:textBox name="cf_Ente"
		      		 title="CF Ente"
		      		 type="text"
		      		 size="50"			             
		             maxlength="1000"			             
		             validateOnPost="true"
		             onKeyUp="fieldChanged();"
		             classNameBase="input"
		             readonly="true"
		             value="<%=cf_Ente%>"/>
		</td>
	</tr>
	<tr>
	  <td class="etichetta">Indirizzo Ente</td>
	  <td class="campo">
	    <af:textBox name="ind_Ente"
		      		 title="Indirizzo Ente"
		      		 type="text"
		      		 size="50"			             
		             maxlength="1000"			             
		             validateOnPost="true"
		             onKeyUp="fieldChanged();"
		             classNameBase="input"
		             readonly="true"
		             value="<%=ind_Ente%>"/>
		</td>
	</tr>

	<tr>
	  <td class="etichetta">Rif. richiesta</td>
	  <td class="campo">
	    <af:textBox name="rif"
		      		 title="Rif. richiesta"
		      		 type="text"
		      		 size="50"			             
		             maxlength="1000"			            
		             validateOnPost="true"
		             onKeyUp="fieldChanged();"
		             classNameBase="input"
		             readonly="true"
		             value="<%=rif%>"/>
		</td>
	</tr>
<%
   String qualif = "";  
   for(int j=0;j<vet.size();j++){
   		qual 	= (SourceBean)vet.get(j);
   		qualif 	= StringUtils.getAttributeStrNotNull(qual,"CODICE");		
   }    
%>
	<tr>
	  <td class="etichetta">Qualifica</td>
	  <td class="campo">
	  	<af:comboBox name="qualifica" 
	                 size="1" 
	                 title="qualifica"
	                 multiple="false"
	                 required="false"
	                 focusOn="false"		                 
	                 moduleName="M_AS_AVVIAMENTI_QUALIFICA"
	                 addBlank="true"
	                 blankValue="" 
	                 selectedValue="<%=qualif%>"
	                 disabled="<%=String.valueOf(readOnlyStr)%>"
	    />
	  </td>
	</tr>

<%
   String rapp = "";
   for(int z=0;z<vect.size();z++){
   		rap 	= (SourceBean)vect.get(z);
   		rapp	= StringUtils.getAttributeStrNotNull(rap,"CODICE");		
   }    
%>
	<tr>
	  <td class="etichetta">Rapporto</td>
	  <td class="campo">
	    <af:comboBox name="rapporto" 
	                 size="1"
	                 title="rapporto"
	                 multiple="false"
	                 required="false"
	                 focusOn="false"		                 
	                 moduleName="M_AS_AVVIAMENTI_RAPPORTO"
	                 addBlank="true"
	                 blankValue=""
	                 selectedValue="<%=rapp%>"
	                 disabled="<%=String.valueOf(readOnlyStr)%>"
	    />
	  </td>
	</tr>
<%
   String esi = "";   
   for(int i=0;i<ve.size();i++){
   		esit = (SourceBean)ve.get(i);
   		esi	 = StringUtils.getAttributeStrNotNull(esit,"CODICE");
   				
   }    
%>	
	<tr>
	  <td class="etichetta">Esito</td>
	  <td class="campo">
	    <af:comboBox name="esito" 
	                 size="1"
	                 title="esito"
	                 multiple="false"
	                 required="false"
	                 focusOn="false"		                 
	                 moduleName="M_AS_AVVIAMENTI_ESITO"
	                 addBlank="true"
	                 blankValue=""
	                 selectedValue="<%=esi%>"
	                 disabled="<%=String.valueOf(readOnlyStr)%>"
	    />
	  </td>
	</tr>
<%
   String nota = "";
   for(int i=0;i<vt.size();i++){
   		not = (SourceBean)vt.get(i);
   		nota  = StringUtils.getAttributeStrNotNull(not,"STRNOTA");		
   } 
%>
	<tr>
      <td class="etichetta">Note</td>
      <td colspan=3 class="campo">	
	    <af:textArea classNameBase="textarea" name="strnote" value="<%=nota%>"
                     cols="60" rows="4" maxlength="100" onKeyUp="fieldChanged();"
                     readonly="false"  />
    </td>
	</tr>
	<tr>
		<td>&nbsp;</td>
	</tr>
<%if (btnAggiorna){%>
	<tr>
		<td align="center" colspan="2">
			<input type="submit" class="pulsante" value="Aggiorna" name="btnAggiorna">
		</td>
	</tr>	
	<tr>
		<td>&nbsp;</td>
	</tr>	
<%}%>
<%
if(_backToList.equals("GENERALE")){	// questo parametro "GENERALE" lo imposto come fisso nella pagina di ricerca 
								// si riferisce alla lista ottenuto dal menu generale								
%>	
	<tr>
		<td colspan="2">
		<%
			String urlDiLista = (String) sessionContainer.getAttribute("_TOKEN_ASLISTAAVVSELEZIONEPAGE");
			if (urlDiLista != null) {
				out.println("<div align=\"center\"><a href=\"#\" onClick=\"goTo('" + urlDiLista +"')\"><img src=\"../../img/rit_lista.gif\" border=\"0\"></div>");
			}
		%>
		</td>
	</tr>
<%
} else {						// se il parametro è diverso da GENERALE la lista ottenuta è riferita al menu contestuale
%>
	<tr>
		<td colspan="2">
		<%
			String urlDiLista = (String) sessionContainer.getAttribute("_TOKEN_ASLISTACONTAVVSELEZIONEPAGE");
			if (urlDiLista != null) {
				out.println("<div align=\"center\"><a href=\"#\" onClick=\"goTo('" + urlDiLista +"')\"><img src=\"../../img/rit_lista.gif\" border=\"0\"></div>");
			}
		%>
		</td>
	</tr>
<%}%>	
</table>
<%out.print(htmlStreamBottom);%>
<center>
</center>
</af:form>
</body>
</html>