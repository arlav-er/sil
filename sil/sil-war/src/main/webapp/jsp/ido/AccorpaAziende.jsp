<!-- @author: Maurizio Discepolo - Settembre 2004 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.module.movimenti.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>


<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
    // NOTE: Attributi della pagina (pulsanti e link) 

	PageAttribs attributi = new PageAttribs(user, (String) serviceRequest.getAttribute("PAGE"));

    String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
    //Oggetti per l'applicazione dello stile grafico
    String htmlStreamTop = StyleUtils.roundTopTable(false);
    String htmlStreamBottom = StyleUtils.roundBottomTable(false);
    
    String prgAzienda1 = null;
	String ragioneSocialeAz1 = null;
	String pIvaAz1 = null;
	String codFiscaleAz1 = null;
	String descrTipoAz1 = null;

	String prgAzienda2 = null;
	String ragioneSocialeAz2 = null;
	String pIvaAz2 = null;
	String codFiscaleAz2 = null;
	String descrTipoAz2 = null;
    
	//Recupero eventuali dati delle aziende già impostate  
	if ( serviceRequest.containsAttribute("prgAzienda1") && serviceRequest.containsAttribute("prgAzienda2") ) {
		 prgAzienda1 = (String)serviceRequest.getAttribute("prgAzienda1");
		 ragioneSocialeAz1 = (String)serviceRequest.getAttribute("ragioneSocialeAz1");
		 pIvaAz1 = (String)serviceRequest.getAttribute("pIvaAz1");
		 codFiscaleAz1 = (String)serviceRequest.getAttribute("codFiscaleAz1");
		 descrTipoAz1 = (String)serviceRequest.getAttribute("descrTipoAz1");

		 prgAzienda2 = (String)serviceRequest.getAttribute("prgAzienda2");
		 ragioneSocialeAz2 = (String)serviceRequest.getAttribute("ragioneSocialeAz2");
		 pIvaAz2 = (String)serviceRequest.getAttribute("pIvaAz2");
		 codFiscaleAz2 = (String)serviceRequest.getAttribute("codFiscaleAz2");
		 descrTipoAz2 = (String)serviceRequest.getAttribute("descrTipoAz2");
	}
  

%>

<html>
	<head>
	  	<link rel="stylesheet" href="../../css/stili.css" type="text/css">
	  	<af:linkScript path="../../js/"/>
	  	<title>Accorpamento Aziende</title>
	
	  	<script language="Javascript">
		<% 
			//Genera il Javascript che si occuperà di inserire i links nel footer
//			attributi.showHyperLinks(out, requestContainer,responseContainer,"");
		%>
	  	</SCRIPT>

  <%@ include file="../movimenti/MovimentiRicercaSoggetto.inc" %>
  <%@ include file="../movimenti/MovimentiSezioniATendina.inc" %>
  <%@ include file="../movimenti/DynamicRefreshCombo.inc" %> 

		<!--Funzioni per l'aggiornamento della form -->
		<script type="text/javascript">
		<!--

			function botAccorpaTestata() {
			  // Se la pagina è già in submit, ignoro questo nuovo invio!
		  	  if (isInSubmit()) return;
			  
			  if (document.Frm1.PRGAZIENDA1!=null && document.Frm1.PRGAZIENDA2!=null && document.Frm1.PRGAZIENDA1.value!="" && document.Frm1.PRGAZIENDA2.value!="") {
			    prgAzienda1 = document.Frm1.PRGAZIENDA1.value;
			    prgAzienda2 = document.Frm1.PRGAZIENDA2.value;
			    
			    var url = "AdapterHTTP?PAGE=AccorpamentoTestataPage" +
						    "&CDNFUNZIONE=<%=_funzione%>" +
						    "&PRGAZIENDA1=" + prgAzienda1 +
						    "&PRGAZIENDA2=" + prgAzienda2;
			    
			    setWindowLocation(url);
			  }
			  else {
			    alert("Seleziona due aziende");
			    return false;
			  }
			}


		    function aggiornaAzienda1(){
		    	//Controllo di non aver scelto la stessa azienda della 2
		        if (!(document.Frm1.PRGAZIENDA2.value == opened.dati.prgAzienda)) {
			        
			        document.Frm1.ragioneSocialeAz1.value = opened.dati.ragioneSociale;
			        document.Frm1.pIvaAz1.value = opened.dati.partitaIva;
			        document.Frm1.codFiscaleAz1.value = opened.dati.codiceFiscaleAzienda;
			        document.Frm1.PRGAZIENDA1.value = opened.dati.prgAzienda;
			        document.Frm1.descrTipoAz1.value = opened.dati.descrTipoAz;
			        opened.close();
			        //Se la sezione è chiusa la apro
			        if (document.getElementById("sezioneAzienda1").style.display == "none") {
			        	cambia(document.getElementById('tendinaAzienda1'),
			        			document.getElementById('sezioneAzienda1'));	
			        }
			    } else {
			    	alert("Impossibile scegliere due aziende uguali!");
			    	opened.close();
			    }
		    }
		    
		    function aggiornaAzienda2(){
		    	//Controllo di non aver scelto la stessa azienda della 1
		        if (!(document.Frm1.PRGAZIENDA1.value == opened.dati.prgAzienda)) {	
		        	 	    
			        document.Frm1.ragioneSocialeAz2.value = opened.dati.ragioneSociale;
			        document.Frm1.pIvaAz2.value = opened.dati.partitaIva;
			        document.Frm1.codFiscaleAz2.value = opened.dati.codiceFiscaleAzienda;
			        document.Frm1.PRGAZIENDA2.value = opened.dati.prgAzienda;
			        document.Frm1.descrTipoAz2.value = opened.dati.descrTipoAz;
			        opened.close();
			        //Se la sezione è chiusa la apro
			        if (document.getElementById("sezioneAzienda2").style.display == "none") {
			        	cambia(document.getElementById('tendinaAzienda2'), 
			        			document.getElementById('sezioneAzienda2'));	
			        }
			    } else {
			    	alert("Impossibile scegliere due aziende uguali!");
			    	opened.close();
			    }
		    }

			function azzeraAzienda1() {
		      document.Frm1.PRGAZIENDA1.value = "";
		      document.Frm1.ragioneSocialeAz1.value = "";
		      document.Frm1.pIvaAz1.value = "";
		      document.Frm1.codFiscaleAz1.value = "";
		      document.Frm1.descrTipoAz1.value = "";
		      
  	          //Se la sezione è aperta la chiudo
	          if (document.getElementById("sezioneAzienda1").style.display == "inline") {
	          	  cambia(document.getElementById('tendinaAzienda1'),
	        			  document.getElementById('sezioneAzienda1'));
	          }
			}

			function azzeraAzienda2() {
		      document.Frm1.PRGAZIENDA2.value = "";
		      document.Frm1.ragioneSocialeAz2.value = "";
		      document.Frm1.pIvaAz2.value = "";
		      document.Frm1.codFiscaleAz2.value = "";
		      document.Frm1.descrTipoAz2.value = "";
		      
  	          //Se la sezione è aperta la chiudo
	          if (document.getElementById("sezioneAzienda2").style.display == "inline") {
	          	  cambia(document.getElementById('tendinaAzienda2'),
	        			  document.getElementById('sezioneAzienda2'));	
	          }
			}
		    
		    //Navigazione tra le linguette
		    function avanti() {
				//CONTROLLI DOPPI: IL CONTROLLO SULLA CONSISTENZA DEI DATI
				//VIENE FATTO DALLA botAccorpaTestata.
				//Ho commentato le parti in più. AR 09/05/2005
		    	//if (controllaFunzTL() && riportaControlloUtente( controllaScelte() )) {
		    		//doFormSubmit(document.Frm1);
		    		botAccorpaTestata();
		    	//}
		    }
		    
/*   QUESTE FUNZIONI NON SONO UTILIZZATE. COMMENTATE DA AR 09/05/2005

		    //Controlla che siano state scelte entrambe le aziende
		    function controllaScelte() {
		    	if (document.Frm1.PRGAZIENDA1.value == "" ||
		    		document.Frm1.PRGAZIENDA2.value == "") {
		    			alert("Occorre prima selezionare entrambe le aziende coinvolte");
		    			return false;
		    	} else return true;
		    }
		    
		    //Funzione che consente la navigazione tra le linguette
		    function goToLinguetta(page, checkForm) {
		    	// Se la pagina è già in submit, ignoro questo nuovo invio!
	  			if (isInSubmit()) return;
		    
		    	document.Frm1.PAGE.value = page;
		    	if (checkForm) {
		    		if (controllaFunzTL() && controllaScelte()) {
		    			doFormSubmit(document.Frm1);
		    		}
		    	} else doFormSubmit(document.Frm1);
		    }
*/
		-->
	 </script>
	</head>    
	<body class="gestione" onload="rinfresca();">
    <h2>Accorpamento Aziende</h2>

		<center>
			<af:form name="Frm1" method="POST" action="AdapterHTTP">
        	<input type="hidden" name="PAGE" value=""/>
        	<input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
        	<input type="hidden" name="prgAzienda1" value="<%=prgAzienda1%>"/>
        	<input type="hidden" name="prgAzienda2" value="<%=prgAzienda2%>"/>
        				
			<%@ include file="CampiComuniAccorpaAziende.inc" %>						
      		
      		<%out.print(htmlStreamTop);%>
      		<table class="main" border="0">
	      		<tr>
	      			<td>
            			<div class="sezione2">
              				<img id='tendinaAzienda1' 
              					alt="Chiudi" 
              					src="../../img/chiuso.gif" 
              					onclick="cambia(this,document.getElementById('sezioneAzienda1'));"/>&nbsp;&nbsp;&nbsp;Azienda 1
            				&nbsp;&nbsp;
              				<a href="#" onClick="javascript:apriSelezionaSoggetto('Testate', 'aggiornaAzienda1','','','');"><img src="../../img/binocolo.gif" alt="Cerca"/></a>
					        &nbsp;&nbsp;
					        <a href="#" onClick="javascript:azzeraAzienda1();"> <img src="../../img/del.gif" alt="Azzera selezione"/></a>

						  	<SCRIPT language="Javascript">
			                //if (document.Frm1.prgAzienda1!="") {
			                  	<!--<a href="#" onClick="javascript:apriTestataAziendale(<%=prgAzienda1%>,<%=_funzione%>);"><img src="../../img/detail.gif" alt="Dettaglio azienda"></a>-->
            				//}
            				</SCRIPT>
            			</div>	      				
	      			</td>
	      		</tr>
	      		<tr>
	      			<td>
              			<div id="sezioneAzienda1" style="display: none;">
                			<table class="main" width="100%" border="0">
                    			<tr>
                      				<td class="etichetta">Codice Fiscale</td>
                      				<td class="campo">
                        				<af:textBox classNameBase="input" 
                        							type="text" 
                        							name="codFiscaleAz1" 
                        							readonly="true" 
                        							value="<%=codFiscaleAz1%>" 
                        							size="30" 
                        							maxlength="16"/>                       
                      				</td>
                    			</tr>
                    			<tr valign="top">
                      				<td class="etichetta">Partita IVA</td>
                      				<td class="campo">
			                        <af:textBox classNameBase="input" 
			                        			type="text" 
			                        			name="pIvaAz1" 
			                        			readonly="true" 
			                        			value="<%=pIvaAz1%>" 
			                        			size="30" 
			                        			maxlength="11"/>
			                      	</td>
			                    </tr>
			                    <tr>
			                      	<td class="etichetta">Ragione Sociale</td>
			                      	<td class="campo">
			                        	<af:textBox classNameBase="input" 
			                        				type="text" 
			                        				name="ragioneSocialeAz1" 
			                        				readonly="true" 
			                        				value="<%=ragioneSocialeAz1%>" 
			                        				size="60" 
			                        				maxlength="100"/>
			                      	</td>
			                    </tr>
			                    <tr>
			                      	<td class="etichetta">Tipo Azienda</td>
			                      	<td class="campo">
			                        	<af:textBox classNameBase="input" 
			                        				type="text" 
			                        				name="descrTipoAz1" 
			                        				readonly="true" 
			                        				value="<%=descrTipoAz1%>" 
			                        				size="30" 
			                        				maxlength="30"/>
			                      	</td>
			                    </tr>
							</table>
			             </div>	      				
	      			</td>
	      		</tr>
	      		<tr>
	      			<td>
            			<div class="sezione2">
              				<img id='tendinaAzienda2' 
              					alt="Chiudi" 
              					src="../../img/chiuso.gif" 
              					onclick="cambia(this,document.getElementById('sezioneAzienda2'));"/>&nbsp;&nbsp;&nbsp;Azienda 2
            				&nbsp;&nbsp;
              				<a href="#" onClick="javascript:apriSelezionaSoggetto('Testate', 'aggiornaAzienda2','','','');"><img src="../../img/binocolo.gif" alt="Cerca"></a>
					        &nbsp;&nbsp;
					        <a href="#" onClick="javascript:azzeraAzienda2();"><img src="../../img/del.gif" alt="Azzera selezione"></a>
            			</div>	      				
	      			</td>
	      		</tr>
	      		<tr>
	      			<td>
              			<div id="sezioneAzienda2" style="display: none;">
                			<table class="main" width="100%" border="0">
                    			<tr>
                      				<td class="etichetta">Codice Fiscale</td>
                      				<td class="campo">
                        				<af:textBox classNameBase="input" 
                        							type="text" 
                        							name="codFiscaleAz2" 
                        							readonly="true" 
                        							value="<%=codFiscaleAz2%>" 
                        							size="30" 
                        							maxlength="16"/>                       
                      				</td>
                    			</tr>
                    			<tr valign="top">
                      				<td class="etichetta">Partita IVA</td>
                      				<td class="campo">
			                        <af:textBox classNameBase="input" 
			                        			type="text" 
			                        			name="pIvaAz2" 
			                        			readonly="true" 
			                        			value="<%=pIvaAz2%>" 
			                        			size="30" 
			                        			maxlength="11"/>
			                      	</td>
			                    </tr>
			                    <tr>
			                      	<td class="etichetta">Ragione Sociale</td>
			                      	<td class="campo">
			                        	<af:textBox classNameBase="input" 
			                        				type="text" 
			                        				name="ragioneSocialeAz2" 
			                        				readonly="true" 
			                        				value="<%=ragioneSocialeAz2%>" 
			                        				size="60" 
			                        				maxlength="100"/>
			                      	</td>
			                    </tr>
			                    <tr>
			                      	<td class="etichetta">Tipo Azienda</td>
			                      	<td class="campo">
			                        	<af:textBox classNameBase="input" 
			                        				type="text" 
			                        				name="descrTipoAz2" 
			                        				readonly="true" 
			                        				value="<%=descrTipoAz2%>" 
			                        				size="30" 
			                        				maxlength="30"/>
			                      	</td>
			                    </tr>
							</table>
			             </div>	      				
	      			</td>
	      		</tr>
	      		<tr><td align="center">
		      		<input class="pulsante" type="button" name="accorpaTestata" value="Visualizza e confronta"
	    			onclick="botAccorpaTestata()" /><br/>
				</td></tr>	      		
	      		
      		</table>
      		<%out.print(htmlStreamBottom);%>		  
			</af:form>
		</center>
		<script language="javascript">
			if (document.Frm1.PRGAZIENDA1.value != "") {
				cambia(document.getElementById('tendinaAzienda1'), 
						document.getElementById('sezioneAzienda1'));
			}
			if (document.Frm1.PRGAZIENDA2.value != "") {
				cambia(document.getElementById('tendinaAzienda2'), 
						document.getElementById('sezioneAzienda2'));
			}			
		</script>	
	</body>
</html>