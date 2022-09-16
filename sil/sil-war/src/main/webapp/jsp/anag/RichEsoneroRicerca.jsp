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
                  it.eng.afExt.utils.StringUtils,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
 // NOTE: Attributi della pagina (pulsanti e link) 
 //PageAttribs attributi = new PageAttribs(user, "AnagMainPage");
 //boolean canModify = attributi.containsButton("nuovo");

 
 String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
 
 String cercaAzienda     = StringUtils.getAttributeStrNotNull(serviceRequest,"cercaAzienda");
 
 String STRRAGIONESOCIALE = StringUtils.getAttributeStrNotNull(serviceRequest,"STRRAGIONESOCIALE");
 String STRCODICEFISCALE = StringUtils.getAttributeStrNotNull(serviceRequest,"STRCODICEFISCALE");
 String STRPARTITAIVA = StringUtils.getAttributeStrNotNull(serviceRequest,"STRPARTITAIVA"); 
 String DATAINIZIOVALIDITA_DAL = StringUtils.getAttributeStrNotNull(serviceRequest,"DATAINIZIOVALIDITA_DAL"); 
 String DATAINIZIOVALIDITA_AL = StringUtils.getAttributeStrNotNull(serviceRequest,"DATAINIZIOVALIDITA_AL");
 String CODSTATORICHIESTA = StringUtils.getAttributeStrNotNull(serviceRequest,"CODSTATORICHIESTA"); 
 String CODSTATOATTO = StringUtils.getAttributeStrNotNull(serviceRequest,"CODSTATOATTO");   
 String prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest,"prgAzienda");
 String PROVINCIA_ISCR = StringUtils.getAttributeStrNotNull(serviceRequest,"PROVINCIA_ISCR");

 String visualizza_display = "none";
 
 String  _page  = StringUtils.getAttributeStrNotNull(serviceRequest,"PAGE"); 
 
 ProfileDataFilter filter = new ProfileDataFilter(user, _page); 

 PageAttribs attributi = new PageAttribs(user, _page); 

 boolean canInsert = false; 

 if (! filter.canView()){
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
 } else {
  	canInsert = attributi.containsButton("INSERISCI"); 
 }

 if(!(prgAzienda == null || prgAzienda.equals(""))){
	  SourceBean rowAz = null;
	  Vector rowsAz= serviceResponse.getAttributeAsVector("M_Load_Azienda.ROWS.ROW");
	  // siamo in dettaglio per cui avro' al massimo una riga
	  if (rowsAz.size()==1) {
	        rowAz = (SourceBean)rowsAz.get(0);
				
		    STRRAGIONESOCIALE = Utils.notNull(rowAz.getAttribute("STRRAGIONESOCIALE"));		    
		    STRCODICEFISCALE = Utils.notNull(rowAz.getAttribute("STRCODICEFISCALE")); 
		    STRPARTITAIVA = Utils.notNull(rowAz.getAttribute("STRPARTITAIVA"));  
	  }	  	
 }
 	
%>

<%
/*
  NOTA: le pagine di ricerca devono avere lo stile prof_ro -> quindi invece di
  canModify si deve passare il valore false
*/
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>
<html>
<head>
	 <title>Ricerca richieste esonero</title>
   	 <link rel="stylesheet" href="../../css/stili.css" type="text/css">
	 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>    	 
   	 <af:linkScript path="../../js/" />
   	 <SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>

     <script language="Javascript">
	      <% 
	     	//Genera il Javascript che si occuperà di inserire i links nel footer
	        //attributi.showHyperLinks(out, requestContainer,responseContainer,"");
	      %>
		  
		  function checkDate (strdata1, strdata2) {
			  annoVar1 = strdata1.substr(6,4);
			  meseVar1 = strdata1.substr(3,2);
			  giornoVar1 = strdata1.substr(0,2);
			  dataVarInt1 = parseInt(annoVar1 + meseVar1 + giornoVar1, 10);

			  annoVar2 = strdata2.substr(6,4);
			  meseVar2 = strdata2.substr(3,2);
			  giornoVar2 = strdata2.substr(0,2);
			  dataVarInt2 = parseInt(annoVar2 + meseVar2 + giornoVar2, 10);
			  
			  if (dataVarInt1 < dataVarInt2) {
			      return 1;
			  }
			  else {
			      if (dataVarInt1 > dataVarInt2) {
			        return 2;
			      }
			      else {
			        return 0;
			      }
			  }
		  }
		  		  
		  function cercaRichiesta(esisteAz){
			var url;
 		    url = "AdapterHTTP?PAGE=CMRichEsoneroListaPage&fromRicerca=1";
 		    url = url + "&prgAzienda=<%=prgAzienda%>";
 		    url = url + "&CDNFUNZIONE=<%=_funzione%>";
 		    if(esisteAz!="S"){
				url = url + "&ragioneSoc=" + document.Frm1.STRRAGIONESOCIALE.value;
				url = url + "&CodFisc=" + document.Frm1.STRCODICEFISCALE.value;
				url = url + "&PIVA=" + document.Frm1.STRPARTITAIVA.value;
			}
		  	url = url + "&DATAINIZIOVALIDITA_DAL=" + document.Frm1.DATAINIZIOVALIDITA_DAL.value;
		  	url = url + "&DATAINIZIOVALIDITA_AL=" + document.Frm1.DATAINIZIOVALIDITA_AL.value;
		  	url = url + "&CODSTATORICHIESTA=" + document.Frm1.CODSTATORICHIESTA.value;
		  	url = url + "&CODSTATOATTO=" + document.Frm1.CODSTATOATTO.value;
		  	url = url + "&PROVINCIA_ISCR=" + document.Frm1.PROVINCIA_ISCR.value;
			
			// Controllo date
			var datSospDa = document.Frm1.DATAINIZIOVALIDITA_DAL.value;
			var datSospA = document.Frm1.DATAINIZIOVALIDITA_AL.value;
			
			var esitoData = false; 
			
			if((datSospDa != "") && (datSospA != "")){
				var date = checkDate(datSospDa,datSospA);
				if(date==1){
					esitoData = true;
				}
			}
			
			if((datSospDa == "") && (datSospA == "")){
			 	esitoData = true;
			}
			
			if(esitoData){
				document.location = url;
			}else{
				alert("La " + document.Frm1.DATAINIZIOVALIDITA_AL.title + " deve essere maggiore della " + document.Frm1.DATAINIZIOVALIDITA_DAL.title);
			}					
		  }

		  function nuovaRichiesta(){
			var url;
 		    url = "AdapterHTTP?PAGE=CMRichEsoneroDettPage&fromRicerca=1&nuovaRichiesta=1";
 		    url = url + "&CDNFUNZIONE=<%=_funzione%>";
 		    url = url + "&prgAzienda=<%=prgAzienda%>";
			url = url + "&nuovo=true";
			url = url + "&salva=false";
			url = url + "&CODSTATOATTO_P=NP";
			url = url + "&CODSTATORICHIESTA=DA";
 		    url = url + "&goBackListPage=CMRichEsoneroRicercaPage";
 		    
 		    document.location = url;
		  }
		  
		  
		  var imgChiusa = "../../img/chiuso.gif";
		  var imgAperta = "../../img/aperto.gif";	
		  
		  function cambia(immagine, sezione) {
		 	if (sezione.aperta==null) {
				sezione.aperta=true;
			}
			if (document.Frm1.STRCODICEFISCALE.value!="" || document.Frm1.STRPARTITAIVA.value != "" || 
				document.Frm1.STRRAGIONESOCIALE.value!= "" ){
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
    		var url = "AdapterHTTP?PAGE=IdoSelezionaAziendaPage&AGG_FUNZ=riempiDati&cdnFunzione=<%=_funzione%>";
        	var feat = "toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes," +
					   "width=600,height=500,top=50,left=100";
    		opened = window.open(url, "_blank", feat);
    		opened.focus();
		  }
		  
		  function riempiDati(prgAzienda, strCodiceFiscale, strPartitaIva, strRagioneSociale) 
		  {
			document.Frm1.prgAzienda.value = prgAzienda;
			document.Frm1.STRCODICEFISCALE.value = strCodiceFiscale;
			document.Frm1.STRPARTITAIVA.value = strPartitaIva;
			document.Frm1.STRRAGIONESOCIALE.value = strRagioneSociale;
			var imgV = document.getElementById("tendinaAzienda");
			var divVar = document.getElementById("aziendaSez");
			divVar.style.display = "inline";
			imgV.src=imgAperta;
			opened.close();
          }		  

   		  function azzeraAzienda (){
   			document.Frm1.prgAzienda.value = '';
   			document.Frm1.STRRAGIONESOCIALE.value = '';
			document.Frm1.STRCODICEFISCALE.value = '';
			document.Frm1.STRPARTITAIVA.value = '';
			var imgV = document.getElementById("tendinaAzienda");
			var divVar = document.getElementById("aziendaSez");
			divVar.style.display = "none";
			imgV.src=imgChiusa;
		  }
		  
     </script>
     
	 <script language="Javascript">
		<%-- if (prgAzienda == null || prgAzienda.equals("")) { %>
				if (window.top.menu != undefined){				
			  		window.top.menu.location="AdapterHTTP?PAGE=MenuCompletoPage";
				}
		<% } else {%>
				if (window.top.menu != undefined){
					window.top.menu.location="AdapterHTTP?PAGE=MenuCompletoPage&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=1";
				}
		<% } --%>
		<% if (prgAzienda != null && !prgAzienda.equals("")) { %>
			window.top.menu.caricaMenuAzienda(<%=_funzione%>, <%=prgAzienda%>, 1);
		<% } %>		
	 </script>
	
</head>
<body class="gestione" onload="rinfresca()">

  <p class="titolo">Ricerca richiesta di esonero</p>
    <%out.print(htmlStreamTop);%>
	  <af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="">
		  
		  <input type="hidden" name="prgAzienda" value="<%=prgAzienda%>" >
		  <input type="hidden" name="goBackListPage" value="CMRichEsoneroRicercaPage" >
		  
	      <table class="main" border="0">
	        <tr><td colspan="2"/>&nbsp;</td></tr>

			<% if (prgAzienda == null || prgAzienda.equals("")) { %> 
	        <tr>
	          <td colspan="2"><br/>
	          	<div class="sezione2">
	          		<img id='tendinaAzienda' alt="Chiudi" src="../../img/chiuso.gif" onclick='cambia(this,document.getElementById("aziendaSez"))'/>&nbsp;&nbsp;&nbsp;Azienda
       																													
			         &nbsp;&nbsp;
			  		<% if (!StringUtils.isFilled(prgAzienda)) { %> 
						<a href="#" onClick="apriAzienda();return false"><img src="../../img/binocolo.gif" alt="Cerca"></a>
			  		<% } %>
			 		&nbsp;<a href="#" onClick="javascript:azzeraAzienda();"><img src="../../img/del.gif" alt="Azzera selezione"></a>
	          	</div>
	          </td>
	        </tr>	        
	        <tr>
	          <td colspan="2">
					<table id='aziendaSez' style='display:<%=visualizza_display%>'>   
			    	<script>new Sezione(document.getElementById('aziendaSez'),document.getElementById('tendinaAzienda'),false);</script>
						<tr>
							<td class="etichetta">Codice Fiscale</td>
							<td class="campo">
								<input type="text" name="STRCODICEFISCALE" class="inputView" readonly="true" value="<%=STRCODICEFISCALE%>" size="30" maxlength="16"/>
							</td>
						</tr>
						<tr valign="top">
							<td class="etichetta">Partita IVA</td>
							<td class="campo">
								<input type="text" name="STRPARTITAIVA" class="inputView" readonly="true" value="<%=STRPARTITAIVA%>" size="30" maxlength="30"/>
							</td>
						</tr>
						<tr valign="top">
							<td class="etichetta">Ragione Sociale</td>
							<td class="campo">
								<input type="text" name="STRRAGIONESOCIALE" class="inputView" readonly="true" value="<%=STRRAGIONESOCIALE%>" size="75" maxlength="120"/>
							</td>
						</tr>
					</table>
	          </td>
	        </tr>

			<% } %>

	        <tr>
	          <td colspan="2"><br/><div class="sezione2"></div></td>
	        </tr>
        	<tr>
			    <td class="etichetta">Ambito Territoriale</td>
			    <td colspan=3 class="campo">
			    	<af:comboBox name="PROVINCIA_ISCR" moduleName="CM_GET_PROVINCIA_ISCR" selectedValue="<%=PROVINCIA_ISCR%>"
			        	classNameBase="input" addBlank="true" title="Ambito Territoriale" required="false" />
			    </td>
			</tr>
	        <tr><td colspan="2">&nbsp;</td></tr>	        	        	        
	        <tr>
	          <td class="etichetta">Data inizio validit&agrave dal</td>
	          <td class="campo">
	          	<af:textBox validateOnPost="true" type="date" name="DATAINIZIOVALIDITA_DAL" value="<%=DATAINIZIOVALIDITA_DAL%>" title="Data inizio validità dal" size="10" maxlength="10" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	          	al&nbsp;&nbsp;&nbsp;<af:textBox validateOnPost="true" type="date" name="DATAINIZIOVALIDITA_AL" title="Data inizio validità al" value="<%=DATAINIZIOVALIDITA_AL%>" size="10" maxlength="10" />
	          </td>
	        </tr>
	        <tr>
          		<td class="etichetta">Stato dell'atto</td>
          		<td class="campo" >
		  			<af:comboBox title="Stato dell'atto" name="CODSTATOATTO" addBlank="true">
            			<OPTION value="NP">NON PROTOCOLLATO</OPTION>
            			<OPTION value="PR">PROTOCOLLATO</OPTION>
            			<OPTION value="AN">ANNULLATO</OPTION>
        			</af:comboBox>
		  		</td>
        	</tr>
			<tr>
			    <td class="etichetta">Stato richiesta</td>
			    <td class="campo">
			      <af:comboBox title="Stato richiesta" name="CODSTATORICHIESTA" moduleName="M_COMBO_STATO_RICH_ESON" addBlank="true" required="false" selectedValue="<%=CODSTATORICHIESTA%>"/>&nbsp;
			    </td>
			<tr>
	        <tr><td colspan="2">&nbsp;</td></tr>
	        <tr><td colspan="2">&nbsp;</td></tr>
	        <tr>
	          <td colspan="2" align="center">
		          <%
		          String esisteAz = "";
		          if (prgAzienda == null || prgAzienda.equals("")) { 
		          		esisteAz = "N";
		          } else {
		            	esisteAz = "S";
		          }%> 
		          <input class="pulsanti" type="button" name="cerca" value="Cerca" onClick="cercaRichiesta('<%=esisteAz%>');"/>
		          &nbsp;&nbsp;
		          <input type="reset" class="pulsanti" value="Annulla" />
	          </td>
	        </tr>
	        <tr><td colspan="2">&nbsp;</td></tr>
	        <tr>
	        	<td colspan="2" align="center">
				  <% if (canInsert) { %>
		          	<input type="button" class="pulsanti" value="Nuova richiesta" onClick="nuovaRichiesta();"/>
		          <% } %>			          			
	        	</td>
	        </tr>
	      </table>

      </af:form>
      
	<%out.print(htmlStreamBottom);%>
	</body>
</html>

