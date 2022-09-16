<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ page contentType="text/html;charset=utf-8"	
	import="com.engiweb.framework.base.*,
			it.eng.afExt.utils.*,
			it.eng.sil.security.*,
			it.eng.sil.security.User,
			it.eng.sil.util.*,
			it.eng.sil.module.collocamentoMirato.constant.*,
			java.math.*,
			java.util.*"
	
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"
%>
<%@ taglib uri="aftags" prefix="af" %>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%
	ProfileDataFilter filter = new ProfileDataFilter(user, "CMProspRicercaPage");
	boolean canView = filter.canView();
	if ( !canView ){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}
	
	InfoProvinciaSingleton provincia = InfoProvinciaSingleton.getInstance(); 
	String codiceProv = "";
	String flgPoloReg = provincia.getFlgPoloReg();
	String codiceReg = provincia.getCodiceRegione();
	if (flgPoloReg == null || flgPoloReg.equals("") || flgPoloReg.equalsIgnoreCase("N")) {
		codiceProv = provincia.getCodice();	
	}
	
	String prgAzienda 			= "";
	String prgAziendaApp		= "";
	String prgUnita 			= "";
	String codiceFiscale 		= "";
	String pIva					= ""; 
	String ragSociale			= "";
	String visualizza_display 	= "none";
	String codCpi="";
	int cdnfunzione = SourceBeanUtils.getAttrInt(serviceRequest, "cdnfunzione");
	
	String titolo = "Ricerca Prospetti";
	String  _page  = SourceBeanUtils.getAttrStrNotNull(serviceRequest,"PAGE"); 

	codCpi = user.getCodRif();   	
	int cdnTipoGruppo = user.getCdnTipoGruppo();
	PageAttribs attributi = new PageAttribs(user, "CMProspRicercaPage");
	boolean canInsert =	false;
	canInsert =	attributi.containsButton("INSERISCI");
	
	
	prgAziendaApp = (String)serviceRequest.getAttribute("prgAzienda");
	prgAzienda = (String)serviceRequest.getAttribute("prgAzienda");				
	prgUnita = (String)serviceRequest.getAttribute("prgUnita");				

	String codFiscaleAzienda = "";
	String ragioneSociale = "";
	String partitaIva = "";
	Vector infoAzi = serviceResponse.getAttributeAsVector("M_SelectAziendaProspetto.ROWS.ROW");
	if(infoAzi != null && !infoAzi.isEmpty()) { 
		SourceBean rowInfoAzi = (SourceBean) infoAzi.elementAt(0);
	
		codFiscaleAzienda = SourceBeanUtils.getAttrStrNotNull(rowInfoAzi, "codFiscAzi");
		ragioneSociale = SourceBeanUtils.getAttrStrNotNull(rowInfoAzi, "ragSoc");
		partitaIva = SourceBeanUtils.getAttrStrNotNull(rowInfoAzi, "PIva");	  			 
	}

	String token = "_TOKEN_" + "CMProspListaPage";
	String urlDiLista = (String) sessionContainer.getAttribute(token.toUpperCase());
	if (urlDiLista != null) {
		sessionContainer.delAttribute(token.toUpperCase());
	}

	String htmlStreamTop    = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>
<head>
<title>Ricerca Prospetti</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css" />
<af:linkScript path="../../js/"/>  
<script>
	
    <% if (prgAzienda!=null) {
       //Genera il Javascript che si occuperà di inserire i links nel footer
       		attributi.showHyperLinks(out, requestContainer,responseContainer,"");
       }%>
</script>
<script language="Javascript">
<% if (prgAzienda != null && !("").equals(prgAzienda)) { %>
	window.top.menu.caricaMenuAzienda(<%=cdnfunzione%>, <%=prgAzienda%>, <%=prgUnita%>);
<% } %>
</script>    
<SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>
<script language="Javascript">

   		  var imgChiusa = "../../img/chiuso.gif";
		  var imgAperta = "../../img/aperto.gif";	
		  
		  function cambia(immagine, sezione) {
		 	if (sezione.aperta==null) {
				sezione.aperta=true;
			}
			if (document.Frm1.strCodiceFiscale.value!="" || document.Frm1.strPartitaIva.value != "" || 
				document.Frm1.strRagioneSociale.value!= "" ){
					if (sezione.aperta) {
						sezione.style.display="inline";
						sezione.aperta=false;
						immagine.src="../../img/aperto.gif";
					}
					else {
						sezione.style.display="none";
						sezione.aperta=true;
						immagine.src="../../img/chiuso.gif";
					}
			}
		  }	
		function Sezione(sezione, img,aperta){ 
			this.sezione=sezione;
	    	this.sezione.aperta=aperta;
	    	this.img=img;
		}
		
		function apriAzienda() {
    		var url = "AdapterHTTP?PAGE=ProspettiSelezionaAziendaPage&AGG_FUNZ=riempiDati&cdnFunzione=<%=cdnfunzione%>";
        	var feat = "toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes," +
					   "width=600,height=500,top=50,left=100";
    		opened = window.open(url, "_blank", feat);
    		opened.focus();
		}
		
		function riempiDati(prgAziendaApp, prgUnita, strCodiceFiscale, strPartitaIva, strRagioneSociale) 
		{			
			document.Frm1.prgAziendaApp.value = prgAziendaApp;
			document.Frm1.prgAzienda.value = prgAziendaApp;
			document.Frm1.prgUnita.value = prgUnita;
			document.Frm1.strCodiceFiscale.value = strCodiceFiscale;
			document.Frm1.strPartitaIva.value = strPartitaIva;
			document.Frm1.strRagioneSociale.value = strRagioneSociale;
			var imgV = document.getElementById("tendinaAzienda");
			var divVar = document.getElementById("aziendaSez");
			divVar.style.display = "inline";
			imgV.src=imgAperta;
			opened.close();
        }
		
		function ripristina(){
			if (document.Frm1.prgAziendaApp.value == null || document.Frm1.prgAziendaApp.value == "") {		   
				var sezione = document.getElementById("aziendaSez");
				var image = document.getElementById("tendinaAzienda");
		   		sezione.style.display="none";
		   		image.src="../../img/chiuso.gif";
			}	   
   		}
   		
   		function azzeraAzienda (){
   			document.Frm1.prgAziendaApp.value = '';   			
   			document.Frm1.prgUnita.value = '';
   			document.Frm1.strRagioneSociale.value = '';
			document.Frm1.strCodiceFiscale.value = '';
			document.Frm1.strPartitaIva.value = '';
			var imgV = document.getElementById("tendinaAzienda");
			var divVar = document.getElementById("aziendaSez");
			divVar.style.display = "none";
			imgV.src=imgChiusa;
		}

   		function controllaDati(){
   			if (document.Frm1.flgScopertura.value != '') {
   				if (document.Frm1.flgConvenzione.value == '') {
   					alert("E' necessario indicare insieme alla Scopertura se con convenzione o senza!");
   					return false;
   				}
   			}
   			
    		var dataConsDa = document.Frm1.datConsDal;
    		var dataConsA = document.Frm1.datConsAl; 
    		if ((dataConsDa.value != "") && (dataConsA.value != "")) {
      			if (compareDate(dataConsDa.value,dataConsA.value) > 0) {
      				alert(dataConsDa.title + " deve essere minore o uguale di " + dataConsA.title);
      				dataConsDa.focus();
	    			return false;
	  			}	
	  		}

   			return true;
   		}	

		function gestisciComboFlgConvenzione(){
		
		    if(document.Frm1.flgScopertura.value != ""){
		        document.Frm1.flgConvenzione.disabled = false;
		    } else {
		        document.Frm1.flgConvenzione.value = "";
		        document.Frm1.flgConvenzione.disabled = true;
		    }
		}	

</script>	

</head>

<body class="gestione" onload="rinfresca();gestisciComboFlgConvenzione();">

<af:showErrors/>

<p class="titolo"><%=titolo%></p>

<af:form name="Frm1" action="AdapterHTTP" method="POST" onSubmit="controllaDati()">
<input type="hidden" name="PAGE"          value="CMProspListaPage" />
<input type="hidden" name="BACK_PAGE"     value="CMProspRicercaPage" />
<input type="hidden" name="cdnFunzione"   value="<%=cdnfunzione%>" />
<input type="hidden" name="prgAziendaApp" value="<%=prgAziendaApp==null ? "" : prgAziendaApp%>" />
<input type="hidden" name="prgAzienda" 	  value="<%=prgAziendaApp==null ? "" : prgAziendaApp%>" />
<input type="hidden" name="prgUnita" 	  value="<%=prgUnita==null ? "" : prgUnita%>" />

<%= htmlStreamTop %> 
<table class="main" border="0">    
 <% if (prgAziendaApp == null || ("").equals(prgAziendaApp)) { %> 
   <tr class="note">
   	<td colspan="2">
   	<div class="sezione2">
    <img id='tendinaAzienda' alt="Chiudi" src="../../img/chiuso.gif" onclick='cambia(this,document.getElementById("aziendaSez"))'/>&nbsp;&nbsp;&nbsp;Azienda
       																													
         &nbsp;&nbsp;
  		<% if (!StringUtils.isFilled(prgAzienda)) { %> 
			<a href="#" onClick="apriAzienda();return false"><img src="../../img/binocolo.gif" alt="Cerca"></a>
  		<% } %>
 		&nbsp;<a href="#" onClick="javascript:azzeraAzienda();"><img src="../../img/del.gif" alt="Azzera selezione"></a>
     </td>
     </tr>
     <tr>
		<td colspan="2">
		<table id='aziendaSez' style='display:<%=visualizza_display%>'>   
    	<script>new Sezione(document.getElementById('aziendaSez'),document.getElementById('tendinaAzienda'),false);</script>
			<tr>
				<td class="etichetta">Codice Fiscale</td>
				<td class="campo">
					<input type="text" name="strCodiceFiscale" class="inputView" readonly="true" value="<%=codiceFiscale%>" size="30" maxlength="16"/>
				</td>
			</tr>
			<tr valign="top">
				<td class="etichetta">Partita IVA</td>
				<td class="campo">
					<input type="text" name="strPartitaIva" class="inputView" readonly="true" value="<%=pIva%>" size="30" maxlength="30"/>
				</td>
			</tr>
			<tr valign="top">
				<td class="etichetta">Ragione Sociale</td>
				<td class="campo">
					<input type="text" name="strRagioneSociale" class="inputView" readonly="true" value="<%=ragSociale%>" size="75" maxlength="120"/>
				</td>
			</tr>
		</table>
		</td>
	</tr>
<%  } 
	else {
%>
	<tr class="note">
   		<td colspan="2">
   		<div class="sezione2">
    	&nbsp;&nbsp;&nbsp;Azienda
     	</td>
    </tr>
    <tr>
		<td colspan="2">
		<table>       	
			<tr>
				<td class="etichetta">Codice Fiscale</td>
				<td class="campo">
					<input type="text" name="strCodiceFiscale" class="inputView" readonly="true" value="<%=codFiscaleAzienda%>" size="30" maxlength="16"/>
				</td>
			</tr>
			<tr valign="top">
				<td class="etichetta">Partita IVA</td>
				<td class="campo">
					<input type="text" name="strPartitaIva" class="inputView" readonly="true" value="<%=partitaIva%>" size="30" maxlength="30"/>
				</td>
			</tr>
			<tr valign="top">
				<td class="etichetta">Ragione Sociale</td>
				<td class="campo">
					<input type="text" name="strRagioneSociale" class="inputView" readonly="true" value="<%=ragioneSociale%>" size="75" maxlength="120"/>
				</td>
			</tr>
		</table>
		</td>
	</tr>
<%
	}
%>
	<tr><td colspan="2">
	<table class="main" border="0">
	<tr><td colspan="2">&nbsp;</td></tr>
	    <tr><td colspan="2" ><div class="sezione2"/></td></tr>
		<tr><td colspan="2">&nbsp;</td></tr>	    	    	
		
		<tr>
			<td class="etichetta">Verificato da Operatore</td>
			<td class="campo">
				<af:comboBox name="flgVerOperatore" classNameBase="input">	  	
				    <option value=""></option>            
		            <option value="N">No</option> 
		            <option value="S">Sì</option>               
		        </af:comboBox> 
		    </td>
		</tr>			
		<tr>
		    <td class="etichetta">Fascia di appartenenza</td>
		    <td class="campo">
		    	<af:comboBox name="codMonoCategoria" classNameBase="input">	  
	                <option value="" ></option>
	                <option value="<%=ProspettiConstant.CATEGORIA_NULLA%>">Categoria nulla</option>
	                <option value="A">più di 50 dipendenti</option>
	            	<option value="B">da 36 a 50 dipendenti</option>               
	            	<option value="C">da 15 a 35 dipendenti</option> 
	        	</af:comboBox>  
		    </td>   
		</tr>    
		<tr>
			<td class="etichetta">Anno</td>
			<td class="campo">
				<af:textBox type="integer" title="anno" name="anno"	value="" size="4" maxlength="4" validateOnPost="true" />
		    </td>
		</tr>
       	<tr>
      		<td class="etichetta">Data consegna&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;dal</td>
			<td class="campo">
				<af:textBox type="date" title="Data consegna dal" name="datConsDal" size="12" maxlength="10" validateOnPost="true"/>
			 	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;al&nbsp;&nbsp;
			 	<af:textBox type="date" title="Data consegna al" name="datConsAl" size="12" maxlength="10" validateOnPost="true"/>
			</td>
		</tr>
		<tr>
      		<td class="etichetta">Data inserimento&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;dal</td>
			<td class="campo">
				<af:textBox type="date" title="Data inserimento dal" name="datInsDal" size="12" maxlength="10" validateOnPost="true"/>
			 	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;al&nbsp;&nbsp;
			 	<af:textBox type="date" title="Data inserimento al" name="datInsAl" size="12" maxlength="10" validateOnPost="true"/>
			</td>
		</tr>
		<tr>
        	<td class="etichetta2">Codice comunicazione</td>
			<td class="campo2">
				<af:textBox type="text" name="codComunicazione" value="" size="20" maxlength="16" validateOnPost="true" />
				(finisce con)
			</td> 
        </tr>   
		<tr>
		    <td class="etichetta">Stato prospetto</td>
		    <td class="campo">
		    	<af:comboBox name="codMonoStatoProspetto" classNameBase="input">	  
	                <option value="" ></option>
	                <option value="A">In corso d'anno</option>
	            	<option value="S">Storicizzato</option>               
	            	<option value="V">SARE: storicizzato</option>
	            	<option value="U">Storicizzato: uscita dall'obbligo</option>
	        	</af:comboBox> 
		    </td>
		</tr>
		<tr>
        	<td class="etichetta">Stato dell'atto</td>
          	<td class="campo" >
		  		<af:comboBox title="Stato dell'atto" name="StatoAtto" addBlank="true">
            		<option value="NP">NON PROTOCOLLATO</option>
            		<option value="PR">PROTOCOLLATO</option>
            		<option value="AN">ANNULLATO</option>
        		</af:comboBox>
		  	</td>
        </tr>
		<tr>
		    <td class="etichetta">Provenienza</td>
		    <td class="campo">
		    	<af:comboBox name="codMonoProv" classNameBase="input">	  
	                <option value="" ></option>
	                <option value="S">Sare</option>
	            	<option value="M">Manuale</option>  
	        	</af:comboBox>  
		    </td>   
		</tr>		
		<tr>
		    <td class="etichetta">Presenza di scopertura</td>
		    <td class="campo">
		    	<af:comboBox name="flgScopertura" classNameBase="input" onChange="gestisciComboFlgConvenzione()">  
                	<option value=""></option>
                	<option value="1">Sì</option>
                	<option value="0">No</option>               
            	</af:comboBox>
		    	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Scopertura con convenzione&nbsp;&nbsp;
				<af:comboBox name="flgConvenzione" title="Scopertura con convenzione" classNameBase="input" addBlank="true">
		 						<OPTION value="S">Sì</OPTION>
		    					<OPTION value="N">No</OPTION>
				</af:comboBox>
			</td>
		</tr>
		
		<tr>
		    <td class="etichetta">Sospensione nazionale</td>
		    <td class="campo">
		    	<af:comboBox name="flgSospensioneMob" title="Sospensione nazionale" classNameBase="input" addBlank="true">
	                <option value="S">Si</option>
	            	<option value="N">No</option>  
	        	</af:comboBox>  
		    </td>   
		</tr>
		
		<tr>
		    <td class="etichetta">Provincia</td>
		    <td class="campo">
		    	<af:comboBox name="codProvincia" multiple="false" addBlank="true" moduleName="M_GetIDOProvince" selectedValue="<%=codiceProv%>"/>
		    </td>
		</tr>
		
		<tr>
		    <td class="etichetta">Prospetto presentato dalla Capogruppo</td>
		    <td class="campo">
		    	<af:comboBox name="flgCapoGruppo" title="Prospetto presentato dalla Capogruppo" classNameBase="input" addBlank="true">
	                <option value="S">Si</option>
	            	<option value="N">No</option>  
	        	</af:comboBox>  
		    </td>   
		</tr>
		
		<tr>
		    <td class="etichetta">Codice fiscale capogruppo</td>
		    <td class="campo">
            	<af:textBox type="text" name="strCFAZCapogruppo" value="" size="50" maxlength="100"/>
          	</td> 
		</tr>
		
		<tr>
		    <td class="etichetta">Prospetto di competenza</td>
		    <td class="campo">
		    	<af:comboBox name="flgCompetenzaProsp" title="Prospetto di competenza" classNameBase="input" addBlank="true">
	                <option value="S" selected="true">Si</option>
	            	<option value="N">No</option>  
	        	</af:comboBox>  
		    </td> 
		</tr>
			
		<tr><td colspan=2>&nbsp;</td></tr>
		<tr>
			<td colspan="2">
				<input type="submit" class="pulsanti" name="cerca" value="Cerca" />
				&nbsp;&nbsp;
				<input type="reset" class="pulsanti" name="annulla" value="Annulla" onClick="ripristina()"/>
			</td>
		</tr>
		<tr>
			<td colspan="2">					
			</td>
		</tr>
	</table>
	</td></tr>
	</table>
</af:form>
<%
if (canInsert) {
%>
	<af:form name="Frm2" method="POST" action="AdapterHTTP" >
		<input type="hidden" name="PAGE"              	value="CMProspDettPage" />
		<input type="hidden" name="BACK_PAGE"     value="CMProspRicercaPage" />
		<input type="hidden" name="cdnFunzione"       	value="<%=cdnfunzione%>" />
		<input type="hidden" name="nuovo"             	value="true" />	
		<input type="hidden" name="prgAzienda"         	value="<%=prgAziendaApp==null ? "" : prgAziendaApp%>" />	
		<input type="hidden" name="prgUnita"         	value="<%=prgUnita==null ? "" : prgUnita%>" />	
	
		<center><input type="submit" class="pulsanti" name="inserisci" value="Nuovo Prospetto" /></center>
	</af:form>
<%
}
%>
<%= htmlStreamBottom %>

</body>
</html>