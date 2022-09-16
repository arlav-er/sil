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
                  it.eng.sil.module.movimenti.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
 // NOTE: Attributi della pagina (pulsanti e link) 
 PageAttribs attributi = new PageAttribs(user, "CMComputoRicPage");

 String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
  
 //Dati per l'azienda
 String prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGAZIENDA");
 String prgUnita = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGUNITA");
 String ragioneSociale = "";
 String pIva = "";
 String codFiscaleAzienda = "";
 String IndirizzoAzienda = "";
 String descrTipoAz = "";
 String codTipoAz = "";
 String codnatGiurAz = "";
 String strFlgCfOk = "";
 String strFlgDatiOk = "";

 //dati lavoratore
 String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "CDNLAVORATORE");
 String strCodiceFiscaleLav = "";
 String strNomeLav = "";
 String strCognomeLav = "";
 
 String tipoMenu = ""; 
 
 if (!cdnLavoratore.equals("")) {
    //Oggetto per la generazione delle informazioni sul lavoratore
    InfoLavoratore datiLav = new InfoLavoratore(new BigDecimal(cdnLavoratore));
    strCodiceFiscaleLav = datiLav.getCodFisc();
    strNomeLav = datiLav.getNome();
    strCognomeLav = datiLav.getCognome();
    strFlgCfOk = datiLav.getFlgCfOk();
    if (strFlgCfOk!=null){
      if (strFlgCfOk.equalsIgnoreCase("S")){
        strFlgCfOk = "Si";
      }else
          if (strFlgCfOk.equalsIgnoreCase("N")){
            strFlgCfOk = "No";
          }
    }
    tipoMenu = "lavoratore";
 } 

 if (!prgAzienda.equals("") && !prgUnita.equals("")){
 //if (!prgAzienda.equals("")){
    InfCorrentiAzienda currAz = new InfCorrentiAzienda(prgAzienda,prgUnita);
    ragioneSociale = currAz.getRagioneSociale();
    pIva = currAz.getPIva();
    codFiscaleAzienda = currAz.getCodiceFiscale();
    IndirizzoAzienda = currAz.getIndirizzo();
    descrTipoAz = currAz.getDescrTipoAz();
    codTipoAz = currAz.getTipoAz();
    codnatGiurAz = currAz.getCodNatGiurAz();
    strFlgDatiOk = currAz.getFlgDatiOk();
    if (strFlgDatiOk!=null) {
        if (strFlgDatiOk.equalsIgnoreCase("S")){
          strFlgDatiOk = "Si";
        }else
            if (strFlgDatiOk.equalsIgnoreCase("N")){
              strFlgDatiOk = "No";
            }
    }
    tipoMenu = "azienda";
 } 
 
%>

<%
/*
  NOTA: le pagine di ricerca devono avere lo stile prof_ro -> quindi invece di
  canModify si deve passare il valore false
*/
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);

String PROVINCIA_ISCR = StringUtils.getAttributeStrNotNull(serviceRequest, "PROVINCIA_ISCR");


%>
<html>
<head>
	 <link rel="stylesheet" href="../../css/stili.css" type="text/css">
	 <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>    	 
   	 <af:linkScript path="../../js/" />
   	 <SCRIPT language="JavaScript" src="../../js/script_comuni.js"></SCRIPT>

	 <%@ include file="MovimentiRicercaSoggetto.inc" %>
	 <%@ include file="MovimentiSezioniATendina.inc" %>
	 <%@ include file="Common_Function_Mov.inc" %>
	 <%@ include file="../movimenti/DynamicRefreshCombo.inc" %> 
	
	 <SCRIPT language="JavaScript" src="../../js/Function_CommonRicercaCCNL.js"></SCRIPT>

     <script language="Javascript">
	      <% 
	     	//Genera il Javascript che si occuperà di inserire i links nel footer
	        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
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

		  function cercaComputo(){
			var url;
 		    url = "AdapterHTTP?PAGE=CMComputiListaPage&fromRicerca=1";

 		    url = url + "&PRGAZIENDA=" + document.Frm1.PRGAZIENDA.value;
 		    url = url + "&PRGUNITA=" + document.Frm1.PRGUNITA.value;
 		    url = url + "&CDNLAVORATORE=" + document.Frm1.CDNLAVORATORE.value;
 		    url = url + "&tipoMenu=" + document.Frm1.tipoMenu.value;
 		    
 		    url = url + "&CDNFUNZIONE=<%=_funzione%>";
 		    
 		    url = url + "&codFiscaleAzienda=" + document.Frm1.codFiscaleAzienda.value;
 		    url = url + "&codiceFiscaleLavoratore=" + document.Frm1.codiceFiscaleLavoratore.value;
 		        
 		    url = url + "&DATASSUNZIONE_DAL=" + document.Frm1.DATASSUNZIONE_DAL.value;
 		    url = url + "&DATASSUNZIONE_AL=" + document.Frm1.DATASSUNZIONE_AL.value;
 		    url = url + "&CODSTATODOCUMENTO=" + document.Frm1.CODSTATODOCUMENTO.value;
 		   url = url + "&PROVINCIA_ISCR=" + document.Frm1.PROVINCIA_ISCR.value;
 		   
			url = url + "&goBackListPage=CMComputoRicPage";


 		    url = url + "&tipiComputo=" + document.Frm1.tipiComputo.value;
			var motivoStr = '';
 			for (i=0;i<document.Frm1.CODMOTCOMPUTO.length;i++) {
   				if (document.Frm1.CODMOTCOMPUTO[i].selected) {
   					if (motivoStr == '') {
						motivoStr = motivoStr + document.Frm1.CODMOTCOMPUTO[i].value;
					}
					else {
						motivoStr = motivoStr + ',' + document.Frm1.CODMOTCOMPUTO[i].value;
					}
		 		}
			}
			url = url + "&CODMOTCOMPUTO=" + motivoStr;


			// Controllo date
			var datAssDa = document.Frm1.DATASSUNZIONE_DAL.value;
			var datAssA = document.Frm1.DATASSUNZIONE_AL.value;
			
			var dataDiOggi = new Date();
			var giornoOggi=dataDiOggi.getDate().toString();
			var meseOggi=(dataDiOggi.getMonth() +1).toString();
			
			if(giornoOggi.length == 1){
				giornoOggi = '0' + giornoOggi;
			}
			if(meseOggi.length == 1){
				meseOggi = '0' + meseOggi;	 	
			}
			dataDiOggi = giornoOggi + '/' + meseOggi + '/' + dataDiOggi.getFullYear().toString();
			var esitoData = false; 
			
			if((datAssDa != "") && (datAssA != "")){
				var date = checkDate(datAssDa,datAssA);
				if(date==1){
					esitoData = true;
				}
			}
			if((datAssDa != "") && (datAssA == "")){
				var date = checkDate(datAssDa,dataDiOggi);
				if(date==1){
				 	esitoData = true;
				}
			}
			if((datAssDa == "") && (datAssA == "")){
			 	esitoData = true;
			}

		  	
		  	var esito;
	  		if(esitoData)
	  	    	esito = true;
	  	    else 
	  	    	esito = false;
			
			var msg;
			if(esito){
				document.location = url;
			}else{
				msg = "Date di assunzione errate";
				alert (msg);
			}			
		  }


		  function nuovoComputo(){
			var url;
 		    url = "AdapterHTTP?PAGE=CMComputoDettPage&fromRicerca=1&nuovoComputo=1";
 		    url = url + "&CDNFUNZIONE=<%=_funzione%>";
 		    url = url + "&prgAzienda=<%=prgAzienda%>";
 		    url = url + "&PRGUNITA=<%=prgUnita%>";
 		    url = url + "&CDNLAVORATORE=<%=cdnLavoratore%>";
 		    url = url + "&tipoMenu=" + document.Frm1.tipoMenu.value;
			url = url + "&nuovo=true";
			url = url + "&salva=false";
 		    url = url + "&goBackListPage=CMComputoRicPage"; 
 		    
 		    document.location = url;
		  }

		  // -----------------------------------------------------------------------------------------------------------------		  

		  //Variabili per la memorizzazione dell'azienda e lavoratore corrente per le GET
		  var prgAzienda = '<%=prgAzienda%>';
		  var prgUnita = '<%=prgUnita%>';
		  var cdnLavoratore = '<%=cdnLavoratore%>';
			  
	      function aggiornaAzienda(){
	        document.Frm1.ragioneSociale.value = opened.dati.ragioneSociale;
	        document.Frm1.pIva.value = opened.dati.partitaIva;
	        document.Frm1.codFiscaleAzienda.value = opened.dati.codiceFiscaleAzienda;
	        document.Frm1.IndirizzoAzienda.value = opened.dati.strIndirizzoAzienda + " (" + opened.dati.comuneAzienda + ")";
	        document.Frm1.PRGAZIENDA.value = opened.dati.prgAzienda;
	        document.Frm1.PRGUNITA.value = opened.dati.prgUnita;
	        document.Frm1.codTipoAz.value = opened.dati.codTipoAz;
	        document.Frm1.descrTipoAz.value = opened.dati.descrTipoAz;
	        document.Frm1.FLGDATIOK.value = opened.dati.FLGDATIOK;
	        prgAzienda = opened.dati.prgAzienda;
	        prgUnita = opened.dati.prgUnita;
	        if ( document.Frm1.FLGDATIOK.value == "S" ){ 
	                    document.Frm1.FLGDATIOK.value = "Si";
	        }else 
	              if ( document.Frm1.FLGDATIOK.value != "" ){
	                document.Frm1.FLGDATIOK.value = "No";
	              }
	        document.Frm1.CODNATGIURIDICA.value = opened.dati.codNatGiurAz;
	        opened.close();
	        var imgV = document.getElementById("tendinaAzienda");
	        cambiaLavMC("aziendaSez","inline");
	        imgV.src=imgAperta;	        
	        prgAzienda = opened.dati.prgAzienda;
	      }
	    	
	      function aggiornaLavoratore(){
	        document.Frm1.codiceFiscaleLavoratore.value = opened.dati.codiceFiscaleLavoratore;
	        document.Frm1.cognome.value = opened.dati.cognome;
	        document.Frm1.nome.value = opened.dati.nome;
	        document.Frm1.CDNLAVORATORE.value = opened.dati.cdnLavoratore;
	        document.Frm1.FLGCFOK.value = opened.dati.FLGCFOK;
	        if ( document.Frm1.FLGCFOK.value == "S" ){ 
	                    document.Frm1.FLGCFOK.value = "Si";
	        }else 
	              if ( document.Frm1.FLGCFOK.value != "" ){
	                document.Frm1.FLGCFOK.value = "No";
	              }
	        opened.close();
	        var imgV = document.getElementById("tendinaLav");
	        cambiaLavMC("lavoratoreSez","inline");
	        imgV.src=imgAperta;
	        cdnLavoratore = opened.dati.cdnLavoratore; 
	      }

	      // -----------------------------------------------------------------------------------------------------------------		  

		  function cambiaTendinaComputo(immagine,sezione,campo) {
			  if (immagine.alt == "Apri"){
			    //apri
			    cambiaLavMC(sezione,"inline");
			    immagine.src=imgAperta;
			    immagine.alt="Chiudi";
			  } else {
			    //chiudi
			    cambiaLavMC(sezione,"none");
			    immagine.src=imgChiusa;
			    immagine.alt="Apri";
			  }
		  }
	
     </script>
     
</head>
<body class="gestione" onload="rinfresca()">

  <p class="titolo">Ricerca computi</p>
    <%out.print(htmlStreamTop);%>
	  <af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="">
		  
		  <input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>" >
		  <input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>" >
	      <input type="hidden" name="PRGUNITA" value="<%=prgUnita%>" >
		  
		  <input type="hidden" name="goBackListPage" value="CMComputoRicPage" >
		  
		  <input type="hidden" name="tipoMenu" value="<%=tipoMenu%>" >
		  
	      <table class="main" border="0">
	        <tr><td colspan="2"/>&nbsp;</td></tr>

	        <!-- sezione lavoratore -->
	        <tr class="note">
			    <td colspan="2">
			      <div class="sezione2">
			          <img id='tendinaLav' alt='Chiudi' src='../../img/aperto.gif' onclick="cambiaTendina(this,'lavoratoreSez', document.Frm1.nome);" />&nbsp;&nbsp;&nbsp;Lavoratore
			      &nbsp;&nbsp;
			      <% if (!tipoMenu.equals("lavoratore")){%>
			        <a href="#" onClick="javascript:apriSelezionaSoggetto('Lavoratori', 'aggiornaLavoratore');"><img src="../../img/binocolo.gif" alt="Cerca"></a>
			        &nbsp;<a href="#" onClick="javascript:azzeraLavoratore();"><img src="../../img/del.gif" alt="Azzera selezione"></a>
			      <%}%>
			      </div>
			    </td>
			</tr>
			<tr>
			  <td colspan="2">
			    <div id="lavoratoreSez" style="display: none;">
			      <table class="main" width="100%" border="0">
			          <tr>
			            <td class="etichetta">Codice Fiscale</td>
			            <td class="campo" valign="bottom">
			              <af:textBox classNameBase="input" type="text" name="codiceFiscaleLavoratore" readonly="true" value="<%=strCodiceFiscaleLav%>" size="30" maxlength="16"/>
			              &nbsp;&nbsp;&nbsp;Validità C.F.&nbsp;&nbsp;<af:textBox classNameBase="input" type="text" name="FLGCFOK" readonly="true" value="<%=strFlgCfOk%>" size="3" maxlength="3"/>
			            </td>
			          </tr>
			          <tr >
			            <td class="etichetta">Cognome</td>
			            <td class="campo">
			              <af:textBox classNameBase="input" type="text" name="cognome" readonly="true" value="<%=strCognomeLav%>" size="30" maxlength="50"/>
			            </td>
			          </tr>
			          <tr>
			            <td class="etichetta">Nome</td>
			            <td class="campo">
			              <af:textBox classNameBase="input" type="text" name="nome" readonly="true" value="<%=strNomeLav%>" size="30" maxlength="50"/>
			            </td>
			          </tr>
			      </table>
			    </div>
			  </td>
			</tr>
			
	        <!-- sezione azienda -->
	        <tr class="note">
	          <td colspan="2">
	          <div class="sezione2">
	            <img id='tendinaAzienda' alt="Chiudi" src="../../img/aperto.gif" onclick="cambiaTendina(this,'aziendaSez',document.Frm1.pIva);"/>&nbsp;&nbsp;&nbsp;Azienda
	          &nbsp;&nbsp;
	          <% if (!tipoMenu.equals("azienda")){%>
	            <a href="#" onClick="javascript:apriSelezionaSoggetto('Aziende', 'aggiornaAzienda','','','');"><img src="../../img/binocolo.gif" alt="Cerca"></a>
	          	&nbsp;<a href="#" onClick="javascript:azzeraAzienda();"><img src="../../img/del.gif" alt="Azzera selezione"></a>
	          <%}%>          
	          </div>
	          </td>
	        <tr>
	          <td colspan="2">
	            <div id="aziendaSez" style="display: none;">
	              <table class="main" width="100%" border="0">
	                  <tr>
	                    <td class="etichetta">Codice Fiscale</td>
	                    <td class="campo">
	                      <af:textBox classNameBase="input" type="text" name="codFiscaleAzienda" readonly="true" value="<%=codFiscaleAzienda%>" size="30" maxlength="16"  />                       
	                    </td>
	                  </tr>
	                  <tr>
	                    <td class="etichetta">Partita IVA</td>
	                    <td class="campo">
	                      <af:textBox classNameBase="input" type="text" name="pIva" readonly="true" value="<%=pIva%>" size="30" maxlength="11"/>
	                      &nbsp;&nbsp;&nbsp;Validità C.F./P. IVA&nbsp;&nbsp;<af:textBox classNameBase="input" type="text" name="FLGDATIOK" readonly="true" value="<%=strFlgDatiOk%>" size="3" maxlength="3"/>
	                    </td>
	                  </tr>
	                  <tr>
	                    <td class="etichetta">Ragione Sociale</td>
	                    <td class="campo">
	                      <af:textBox classNameBase="input" type="text" name="ragioneSociale" readonly="true" value="<%=ragioneSociale%>" size="60" maxlength="100"/>
	                    </td>
	                  </tr>
	                  <tr>
	                    <td class="etichetta">Indirizzo (Comune)</td>
	                    <td class="campo">
	                      <af:textBox classNameBase="input" type="text" name="IndirizzoAzienda" readonly="true" value="<%=IndirizzoAzienda%>" size="60" maxlength="100"/>
	                    </td>
	                  </tr>
	                  <tr>
	                    <td class="etichetta">Tipo Azienda</td>
	                    <td class="campo">
	                      <af:textBox classNameBase="input" type="text" name="descrTipoAz" readonly="true" value="<%=descrTipoAz%>" size="30" maxlength="30"/>
	                      <af:textBox classNameBase="input" type="hidden" name="codTipoAz" readonly="true" value="<%=codTipoAz%>" size="10" maxlength="10"/>
	                      <af:textBox classNameBase="input" type="hidden" name="CODNATGIURIDICA" readonly="true" value="<%=codnatGiurAz%>" size="10" maxlength="10"/>
	                    </td>
	                  </tr>
	                </table>
	              </div>
	          </td>
	        </tr>
			<tr class="note">
		       <td colspan="2">
			      <div class="sezione2">
			        <img id='tendinaMov' alt="Chiudi" src="../../img/aperto.gif" onclick="cambiaTendinaComputo(this,'movimentoSez',document.Frm1.CODSTATODOCUMENTO);"/>&nbsp;&nbsp;&nbsp;
			        Dati Computo
			      </div>
		       </td>
		    </tr>
		    <tr>
		       <td colspan="2">		       
				    <div id="movimentoSez" style="display: none;">
				    	<table class="main" width="100%" border="0" cellpadding="2" celspacing="0">
							<tr>
							    <td class="etichetta">Ambito Territoriale</td>
							    <td colspan=3 class="campo">
							    	<af:comboBox name="PROVINCIA_ISCR" moduleName="CM_GET_PROVINCIA_ISCR" selectedValue="<%=PROVINCIA_ISCR%>"
							        	classNameBase="input" addBlank="true" />
							    </td>
							</tr>
							<tr>
							    <td class="etichetta">Stato del documento</td>
							    <td class="campo">
							      <af:comboBox title="Stato" name="CODSTATODOCUMENTO" moduleName="M_ComboStatoAtto" addBlank="true" selectedValue=""/>
							    </td>
							<tr>
					        <tr>
					          <td class="etichetta">Data assunzione&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;dal</td>
					          <td class="campo">
					          	<af:textBox validateOnPost="true" type="date" name="DATASSUNZIONE_DAL" size="10" maxlength="10" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					          	al&nbsp;&nbsp;&nbsp;<af:textBox validateOnPost="true" type="date" name="DATASSUNZIONE_AL" size="10" maxlength="10" />
					          </td>
					        </tr>
					    	<tr >
					    		<td class="etichetta">Tipo di computo</td>
							    <td class="campo">
							      <input type="hidden" name="tipiComputo"/>
							      <af:comboBox name="CODMOTCOMPUTO" moduleName="M_ComboTipiComputo" multiple="true" />
							    </td>	
				    		</tr>				        	        
			        	</table>
			        </div>
		       </td>
		    </tr>			        
			<tr><td colspan="2">&nbsp;</td></tr>
			<tr><td colspan="2">&nbsp;</td></tr>
			<tr>
			   <td colspan="2" align="center">
			       <input class="pulsanti" type="button" name="cerca" value="Cerca" onClick="cercaComputo();"/>
			       &nbsp;&nbsp;
			       <input type="reset" class="pulsanti" value="Annulla" />
			       &nbsp;&nbsp;
			       <input type="button" class="pulsanti" value="Nuovo computo" onClick="nuovoComputo();"/>			          			
			   </td>
			</tr>
	      </table>
      </af:form>
      
	<%out.print(htmlStreamBottom);%>

	<script language="javascript">
	  var imgV = document.getElementById("tendinaLav");
	  <% if (!strNomeLav.equals("")){%>
	    cambiaLavMC("lavoratoreSez","inline");
	    imgV.src = imgAperta;
	    imgV.alt="Chiudi";
	  <%} else {%>
	    cambiaTendina(imgV,"lavoratoreSez",document.Frm1.nome);
	  <%}%>
	        
	  imgV = document.getElementById("tendinaAzienda");
	  <% if (!pIva.equals("")){%>
	    cambiaLavMC("aziendaSez","inline");
	    imgV.src = imgAperta;
	    imgV.alt="Chiudi";
	  <%} else {%>
	    cambiaTendina(imgV,"aziendaSez",document.Frm1.pIva)
	  <%}%>	

	  imgV = document.getElementById("tendinaMov");
	  cambiaLavMC("movimentoSez","inline");
	  imgV.src = imgAperta;
	  imgV.alt="Chiudi";	  
	</script> 
	
	</body>
</html>


