<!-- @author: Paolo Roccetti - Luglio 2004 -->
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
  //Lo stile della lista
  String stile = "LISTA";

  // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, "AggiornaDatiAziendaPage");
  String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");

  //Oggetti per l'applicazione dello stile grafico
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);
  
  //Controllo se ho appena eseguito un aggiornamento
  String aggiornaAzienda = (String) serviceRequest.getAttribute("AggiornaAzienda");
  String aggiornaUnita = (String) serviceRequest.getAttribute("AggiornaUnita");  
  boolean hoAppenaAggiornato = false;
  if (aggiornaAzienda != null || aggiornaUnita != null) {
  	hoAppenaAggiornato = true;
  } 
  
  //Recupero dati
  String prgAzienda = StringUtils.getAttributeStrNotNull(serviceRequest, "PrgAzienda");
  String prgUnita = StringUtils.getAttributeStrNotNull(serviceRequest, "prgUnita");
  String prgMovimentoApp = StringUtils.getAttributeStrNotNull(serviceRequest, "prgMovimentoApp");
  String prgMobilitaApp = StringUtils.getAttributeStrNotNull(serviceRequest, "prgMobilitaIscrApp");
  String funz_agg = StringUtils.getAttributeStrNotNull(serviceRequest, "FUNZ_AGGIORNAMENTO");  
  
  String differenze = StringUtils.getAttributeStrNotNull(serviceRequest, "differenze");

  //Mi dice se sto operando per l'azienda principale o per quella utilizzatrice
  String contesto = StringUtils.getAttributeStrNotNull(serviceRequest, "CONTESTO");
  String daMovimentiNew = StringUtils.getAttributeStrNotNull(serviceRequest, "daMovimentiNew");
  boolean contestoAzienda = contesto.equalsIgnoreCase("AZIENDA"); 
  boolean contestoAzUtil = contesto.equalsIgnoreCase("AZUTIL");
  SourceBean datiMovimento = null;
  if (!prgMobilitaApp.equals("")) {
  	datiMovimento = (SourceBean) serviceResponse.getAttribute("M_MobilitaGetDettaglioMobApp.ROWS.ROW");
  }
  else {
  	datiMovimento = (SourceBean) serviceResponse.getAttribute("M_MovGetDettMovApp.ROWS.ROW");
  }
  SourceBean datiAzienda = (SourceBean) serviceResponse.getAttribute("M_GetTestataAzienda.ROWS.ROW");
  SourceBean datiUnita = (SourceBean) serviceResponse.getAttribute("M_GetUnitaAzienda.ROWS.ROW");

  String pIvaMov = "";
  String ragSocMov = "";
  String codCcnlMov = "";
  String descrCcnlMov = "";
  String numIscrAlboIntMov = "";
  String indirMov = "";
  String capMov = "";
  String codAtecoMov = "";
  String telMov = "";
  String FaxMov = "";
  String emailMov = "";
  String codTipoAzMov = "";
  String cfAzEsteraMov = "";
  String ragSocEsteraMov = "";
  
  //Dati del movimento a seconda del contesto
  if (contestoAzienda) {
  	  if (!prgMobilitaApp.equals("")) {
  	      pIvaMov = StringUtils.getAttributeStrNotNull(datiMovimento, "STRAZPARTITAIVA");
		  ragSocMov = StringUtils.getAttributeStrNotNull(datiMovimento, "STRAZRAGIONESOCIALE");
		  codCcnlMov = StringUtils.getAttributeStrNotNull(datiMovimento, "CODAZCCNL");
		  descrCcnlMov = StringUtils.getAttributeStrNotNull(datiMovimento, "DESCCCNL");
		  numIscrAlboIntMov = StringUtils.getAttributeStrNotNull(datiMovimento, "STRAZNUMALBOINTERINALI");
		  indirMov = StringUtils.getAttributeStrNotNull(datiMovimento, "STRUAINDIRIZZO");
		  capMov = StringUtils.getAttributeStrNotNull(datiMovimento, "STRUACAP");
		  codAtecoMov = StringUtils.getAttributeStrNotNull(datiMovimento, "CODAZATECO");
		  telMov = StringUtils.getAttributeStrNotNull(datiMovimento, "STRUATEL");
		  FaxMov = StringUtils.getAttributeStrNotNull(datiMovimento, "STRUAFAX");
		  emailMov = StringUtils.getAttributeStrNotNull(datiMovimento, "STRUAEMAIL");
  	  }
  	  else {
		  pIvaMov = StringUtils.getAttributeStrNotNull(datiMovimento, "strPartitaIvaAz");
		  ragSocMov = StringUtils.getAttributeStrNotNull(datiMovimento, "strRagioneSocialeAz");
		  codTipoAzMov = StringUtils.getAttributeStrNotNull(datiMovimento, "codTipoAzienda");
		  codCcnlMov = StringUtils.getAttributeStrNotNull(datiMovimento, "codCCNLAz");
		  descrCcnlMov = StringUtils.getAttributeStrNotNull(datiMovimento, "descrCCNLAz");
		  numIscrAlboIntMov = StringUtils.getAttributeStrNotNull(datiMovimento, "strNumAlboInterinali");
		  indirMov = StringUtils.getAttributeStrNotNull(datiMovimento, "strIndirizzoUAz");
		  capMov = StringUtils.getAttributeStrNotNull(datiMovimento, "strCapUAz");
		  codAtecoMov = StringUtils.getAttributeStrNotNull(datiMovimento, "codAtecoUAz");
		  telMov = StringUtils.getAttributeStrNotNull(datiMovimento, "strTelUAz");
		  FaxMov = StringUtils.getAttributeStrNotNull(datiMovimento, "strFaxUAz");
		  emailMov = StringUtils.getAttributeStrNotNull(datiMovimento, "strEmailUAz");
		  cfAzEsteraMov = StringUtils.getAttributeStrNotNull(datiMovimento, "CODFISCAZESTERA");
		  ragSocEsteraMov = StringUtils.getAttributeStrNotNull(datiMovimento, "RAGSOCAZESTERA");
	  }
  } else if (contestoAzUtil) {
	  pIvaMov = StringUtils.getAttributeStrNotNull(datiMovimento, "strPartitaIvaAzUtil");
	  ragSocMov = StringUtils.getAttributeStrNotNull(datiMovimento, "strRagioneSocialeAzUtil");
	  indirMov = StringUtils.getAttributeStrNotNull(datiMovimento, "strIndirizzoUAzUtil");
	  capMov = StringUtils.getAttributeStrNotNull(datiMovimento, "STRUAINTCAP");
	  codAtecoMov = StringUtils.getAttributeStrNotNull(datiMovimento, "CODAZINTATECO"); 	  
  }
  
  String pIvaAz = StringUtils.getAttributeStrNotNull(datiAzienda, "strPartitaIva");
  String ragSocAz = StringUtils.getAttributeStrNotNull(datiAzienda, "strRagioneSociale");
  String codTipoAz = StringUtils.getAttributeStrNotNull(datiAzienda, "codTipoAzienda");
  String numIscrAlboIntAz = StringUtils.getAttributeStrNotNull(datiAzienda, "strNumAlboInterinali");
  String indirAz = StringUtils.getAttributeStrNotNull(datiUnita, "strIndirizzo");
  String capAz = StringUtils.getAttributeStrNotNull(datiUnita, "strCap");
  String codAtecoAz = StringUtils.getAttributeStrNotNull(datiUnita, "codAteco");
  String strDesAteco = StringUtils.getAttributeStrNotNull(datiUnita, "strDesAteco");
  String telAz = StringUtils.getAttributeStrNotNull(datiUnita, "strTel");
  String FaxAz = StringUtils.getAttributeStrNotNull(datiUnita, "strFax");
  String emailAz = StringUtils.getAttributeStrNotNull(datiUnita, "strEmail");
  String ccnlAz = StringUtils.getAttributeStrNotNull(datiUnita, "codCCNL");
  String descrCcnlAz = StringUtils.getAttributeStrNotNull(datiUnita, "desCCNL");
  String comune = StringUtils.getAttributeStrNotNull(datiUnita, "strDenominazione");
  BigDecimal numkloAzienda = (BigDecimal) datiAzienda.getAttribute("numKloAzienda");
  String cfAzEsteraAz = StringUtils.getAttributeStrNotNull(datiUnita, "CODFISCAZESTERA");
  String ragSocEsteraAz = StringUtils.getAttributeStrNotNull(datiUnita, "RAGSOCAZESTERA");
  
  if (numkloAzienda != null) {numkloAzienda = numkloAzienda.add(new BigDecimal(1));}
  BigDecimal numkloUnitaAzienda = (BigDecimal) datiUnita.getAttribute("numKloUnitaAzienda");  
  if (numkloUnitaAzienda != null) {numkloUnitaAzienda = numkloUnitaAzienda.add(new BigDecimal(1));} 
   
  //Conta quante differenze ho
  int diffCounter = 0;
  if (!pIvaMov.equals("") && !pIvaMov.equalsIgnoreCase(pIvaAz)) {diffCounter += 1;}
  if (!ragSocMov.equals("") && !ragSocMov.equalsIgnoreCase(ragSocAz)) {diffCounter += 1;}
  if (!codTipoAzMov.equals("") && !codTipoAzMov.equalsIgnoreCase(codTipoAz)) {diffCounter += 1;}
  if (!codCcnlMov.equals("") && !codCcnlMov.equalsIgnoreCase(ccnlAz)) {diffCounter += 1;}
  if (!numIscrAlboIntMov.equals("") && !numIscrAlboIntMov.equalsIgnoreCase(numIscrAlboIntAz)) {diffCounter += 1;}
  if (!indirMov.equals("") && !indirMov.equalsIgnoreCase(indirAz)) {diffCounter += 1;}
  if (!capMov.equals("") && !capMov.equalsIgnoreCase(capAz)) {diffCounter += 1;}
  if (!codAtecoMov.equals("") && !codAtecoMov.equalsIgnoreCase(codAtecoAz)){diffCounter += 1;}
  if (!telMov.equals("") && !telMov.equalsIgnoreCase(telAz)){diffCounter += 1;}
  if (!FaxMov.equals("") && !FaxMov.equalsIgnoreCase(FaxAz)) {diffCounter += 1;}
  if (!emailMov.equals("") && !emailMov.equalsIgnoreCase(emailAz)) {diffCounter += 1;} 
  
  //Guardo se ho delle differenze
  boolean esistonoDifferenze = false;
  if (diffCounter > 0) { esistonoDifferenze = true; }

%>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Aggiornamento dati Azienda</title>
  <script language="Javascript">
     <% 
     	//Genera il Javascript che si occuperà di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
     %>
	var numRighe = <%=diffCounter%>;
    var numCheckedTestata = 0;
    var numCheckedUnita = 0;    
    //seleziona tutta la lista
    function selectPage() {
    	//Guardo se devo selezionare o deselezionare
    	var checkValue = document.FrmAggiornamentoDati.selectAll.checked;
    	//Itero sulla form cercando i tag input che sono checkbox e li modifico
        for(var elem = 0; elem < document.FrmAggiornamentoDati.length; elem ++) {
        	var element = document.FrmAggiornamentoDati.elements[elem];
            if(element.type == "checkbox" && element.checked != checkValue) {
            	//Per ogni elemento diverso imposto il corretto valore
                element.click;
                element.checked = checkValue;
                //Gestione del numero di checkbox correntemente selezionate
                if (element.tipo == "Testata") {
                	if (checkValue) { 
                    	numCheckedTestata = numCheckedTestata + 1;
                    	if (element.name == 'codTipoAzMov') {
                    		numCheckedUnita = numCheckedUnita + 1;	
                    	}	 
                    } 
                	else { 
                    	numCheckedTestata = numCheckedTestata - 1;
                    	if (element.name == 'codTipoAzMov') {
                    		numCheckedUnita = numCheckedUnita - 1;	
                    	}
                    }
                } else if (element.tipo == "Unita") {
                	if (checkValue) { numCheckedUnita = numCheckedUnita + 1; } 
                	else { numCheckedUnita = numCheckedUnita - 1; }
                }
            }
        }
    }
    
    //Se deseleziono una checkbox rimuove anche la selezione globale se c'è
    function controllaCheckBox(checkbox, tipo) {
    	if (checkbox.checked) {
    		if (tipo == 'Testata') {
	    		numCheckedTestata = numCheckedTestata + 1;
				if (checkbox.name == 'codTipoAzMov') {
					numCheckedUnita = numCheckedUnita + 1;	
				}	
    		} else if (tipo == 'Unita') {
	    		numCheckedUnita = numCheckedUnita + 1;    		
    		}   		
    	} else {
	    	if (document.FrmAggiornamentoDati.selectAll.checked) {
	    		document.FrmAggiornamentoDati.selectAll.checked = "";
	    	}
    		if (tipo == 'Testata') {
	    		numCheckedTestata = numCheckedTestata - 1;
	    		if (checkbox.name == 'codTipoAzMov') {
					numCheckedUnita = numCheckedUnita - 1;	
				}     		
    		} else if (tipo == 'Unita') {
	    		numCheckedUnita = numCheckedUnita - 1;    		
    		}
    	}
    } 
    
    //funzione che controlla che ci sia almeno una checkbox selezionata
    function controllaScelta() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		var count=0;
		if (isInSubmit()) return;

    	if (numCheckedTestata > 0 || numCheckedUnita > 0) {
    		if (numCheckedTestata > 0 ) {
    			document.FrmAggiornamentoDati.AggiornaAzienda.value = "true";
    		} else document.FrmAggiornamentoDati.AggiornaAzienda.value = "false";
    		if (numCheckedUnita > 0 ) {
    			document.FrmAggiornamentoDati.AggiornaUnita.value = "true";
    		} else document.FrmAggiornamentoDati.AggiornaUnita.value = "false";
    		
    		for(var elem = 0; elem < document.FrmAggiornamentoDati.length; elem ++) {
        	var element = document.FrmAggiornamentoDati.elements[elem];
            	if(element.checked) { count++; }
            }
            if(document.FrmAggiornamentoDati.selectAll.checked) {count = count - 1; }
             
            if(count == numRighe) {
            	document.FrmAggiornamentoDati.differenze.value = "true";
            }
           
    		doFormSubmit(document.FrmAggiornamentoDati); 
		
		} else alert("Occorre selezionare almeno un campo da aggiornare");
    }
    
    <% if (hoAppenaAggiornato && daMovimentiNew.equals("S")) {%>
    	var datacontainer = new Object();
    	window.dati = datacontainer;
    	datacontainer.ragSocAz = "<%=ragSocAz%>";
    	datacontainer.pIvaAz = "<%=pIvaAz%>";
    	datacontainer.numIscrAlboIntAz = "<%=numIscrAlboIntAz%>";
    	datacontainer.indirAz = "<%=indirAz%>";
    	datacontainer.capAz = "<%=capAz%>";
    	datacontainer.codAtecoAz = "<%=codAtecoAz%>";
    	datacontainer.strDesAteco = "<%=strDesAteco%>";
    	datacontainer.comune = "<%=comune%>";
    	datacontainer.telAz = "<%=telAz%>";
    	datacontainer.FaxAz = "<%=FaxAz%>";
    	datacontainer.emailAz = "<%=emailAz%>";
    	datacontainer.ccnlAz = "<%=ccnlAz%>";
    	datacontainer.descrCcnlAz = "<%=descrCcnlAz%>";
    	datacontainer.cfAzEstera = "<%=cfAzEsteraAz%>";
    	datacontainer.ragSocAzEstera = "<%=ragSocEsteraAz%>";
    	datacontainer.differenze = "<%=differenze%>";
    <%}%>

</script>
</head>

<body class="gestione" onload="rinfresca();<%=(hoAppenaAggiornato ? "window.opener." + funz_agg + "();" : "")%><%= (!esistonoDifferenze ? "window.close();" : "")%>">
<CENTER>
  <br/>
  <p class="titolo">Aggiornamento dati Azienda</p>
  <br/>
  <font color="green">
  	<af:showMessages prefix="M_MovAggiornaDatiTestata"/>
  	<af:showMessages prefix="M_MovAggiornaDatiUnita"/>  	
  </font>
  <font color="red">
	<af:showErrors/>
  </font>  
	<br/>
<% if (esistonoDifferenze) {%>
	<af:form name="FrmAggiornamentoDati" method="POST" action="AdapterHTTP">
      <input type="hidden" name="PAGE" value="AggiornaDatiAziendaPage"/>
      <input type="hidden" name="NUMKLOAZIENDA" value="<%=numkloAzienda%>"/>
      <input type="hidden" name="NUMKLOUNITAAZIENDA" value="<%=numkloUnitaAzienda%>"/>      
      <input type="hidden" name="cdnFunzione" value="<%=_funzione%>"/>
      <%out.print(htmlStreamTop);%>
      <table class="<%=stile%>" border="0" cellspacing="5" cellpadding="5">
        <tr class="<%=stile%>">
          <th class="<%=stile%>">Campo</th>
          <th class="<%=stile%>">Dato Comunicazione</th>
          <th class="<%=stile%>">Dato SIL</th>       
        </tr>
        
    	<% if (!pIvaMov.equals("") && !pIvaMov.equalsIgnoreCase(pIvaAz)) {%>
    		<tr class="<%=stile%>">
           		<td class="<%=stile%>">   		
					<input type="checkbox" onchange="window.controllaCheckBox(this, this.tipo);"  
							name="pIvaMov" value="<%=pIvaMov%>"/>Partita Iva
					<SCRIPT language="Javascript">
						document.FrmAggiornamentoDati.pIvaMov.tipo = "Testata";
					</SCRIPT>
				</td>				
          		<td class="<%=stile%>"><%=pIvaMov%></td>				
          		<td class="<%=stile%>"><%=pIvaAz%></td>
			</tr>
    	<%}%>
    	<% if (!ragSocMov.equals("") && !ragSocMov.equalsIgnoreCase(ragSocAz)) {%>
    		<tr class="<%=stile%>">
           		<td class="<%=stile%>">   		
					<input type="checkbox" onchange="window.controllaCheckBox(this, this.tipo);" 
							name="ragSocMov" value="<%=ragSocMov%>"/>Ragione Sociale
					<SCRIPT language="Javascript">
						document.FrmAggiornamentoDati.ragSocMov.tipo = "Testata";
					</SCRIPT>
				</td>				
          		<td class="<%=stile%>"><%=ragSocMov%></td>				
          		<td class="<%=stile%>"><%=ragSocAz%></td>
			</tr>
    	<%}%>
    	<% if (!codTipoAzMov.equals("") && !codTipoAzMov.equalsIgnoreCase(codTipoAz)) {%>
    		<tr class="<%=stile%>">
           		<td class="<%=stile%>">   		
					<input type="checkbox" onchange="window.controllaCheckBox(this, this.tipo);" 
							name="codTipoAzMov" value="<%=codTipoAzMov%>"/>Tipologia Azienda
					<SCRIPT language="Javascript">
						document.FrmAggiornamentoDati.codTipoAzMov.tipo = "Testata";
					</SCRIPT>
				</td>				
          		<td class="<%=stile%>"><%=codTipoAzMov%></td>				
          		<td class="<%=stile%>"><%=codTipoAz%></td>
			</tr>
    	<%}%>
    	<% if (!codCcnlMov.equals("") && !codCcnlMov.equalsIgnoreCase(ccnlAz)) {%>
    		<tr class="<%=stile%>">
           		<td class="<%=stile%>">   		
					<input type="checkbox" onchange="window.controllaCheckBox(this, this.tipo);" 
							name="codCcnlMov" value="<%=codCcnlMov%>"/>CCNL
					<SCRIPT language="Javascript">
						document.FrmAggiornamentoDati.codCcnlMov.tipo = "Unita";
					</SCRIPT>
				</td>			
          		<td class="<%=stile%>"><%=descrCcnlMov%></td>				
          		<td class="<%=stile%>"><%=descrCcnlAz%></td>
			</tr>
    	<%}%>
    	<% if (!numIscrAlboIntMov.equals("") && !numIscrAlboIntMov.equalsIgnoreCase(numIscrAlboIntAz)) {%>
    		<tr class="<%=stile%>">
           		<td class="<%=stile%>">   		
					<input type="checkbox" onchange="window.controllaCheckBox(this, this.tipo);" 
							name="numIscrAlboIntMov" value="<%=numIscrAlboIntMov%>"/>Num. Iscr. Albo Interinali
					<SCRIPT language="Javascript">
						document.FrmAggiornamentoDati.numIscrAlboIntMov.tipo = "Testata";
					</SCRIPT>
				</td>			
          		<td class="<%=stile%>"><%=numIscrAlboIntMov%></td>				
          		<td class="<%=stile%>"><%=numIscrAlboIntAz%></td>
			</tr>
    	<%}%> 
    	<% if (!indirMov.equals("") && !indirMov.equalsIgnoreCase(indirAz)) {%>
    		<tr class="<%=stile%>">
           		<td class="<%=stile%>">   		
					<input type="checkbox" onchange="window.controllaCheckBox(this, this.tipo);" 
							name="indirMov" value="<%=indirMov%>"/>Indirizzo
					<SCRIPT language="Javascript">
						document.FrmAggiornamentoDati.indirMov.tipo = "Unita";
					</SCRIPT>
				</td>				
          		<td class="<%=stile%>"><%=indirMov%></td>				
          		<td class="<%=stile%>"><%=indirAz%></td>
			</tr>
    	<%}%>
    	<% if (!capMov.equals("") && !capMov.equalsIgnoreCase(capAz)) {%>
    		<tr class="<%=stile%>">
           		<td class="<%=stile%>">   		
					<input type="checkbox" onchange="window.controllaCheckBox(this, this.tipo);" 
							name="capMov" value="<%=capMov%>"/>Cap
					<SCRIPT language="Javascript">
						document.FrmAggiornamentoDati.capMov.tipo = "Unita";
					</SCRIPT>
				</td>			
          		<td class="<%=stile%>"><%=capMov%></td>				
          		<td class="<%=stile%>"><%=capAz%></td>
			</tr>
    	<%}%>
    	<% if (!codAtecoMov.equals("") && !codAtecoMov.equalsIgnoreCase(codAtecoAz)) {%>
    		<tr class="<%=stile%>">
           		<td class="<%=stile%>">   		
					<input type="checkbox" onchange="window.controllaCheckBox(this, this.tipo);" 
							name="codAtecoMov" value="<%=codAtecoMov%>"/>Codice Ateco
					<SCRIPT language="Javascript">
						document.FrmAggiornamentoDati.codAtecoMov.tipo = "Unita";
					</SCRIPT>
				</td>				
          		<td class="<%=stile%>"><%=codAtecoMov%></td>				
          		<td class="<%=stile%>"><%=codAtecoAz%></td>
			</tr>
    	<%}%>
    	<% if (!telMov.equals("") && !telMov.equalsIgnoreCase(telAz)) {%>
    		<tr class="<%=stile%>">
           		<td class="<%=stile%>">   		
					<input type="checkbox" onchange="window.controllaCheckBox(this, this.tipo);" 
							name="telMov" value="<%=telMov%>"/>Telefono
					<SCRIPT language="Javascript">
						document.FrmAggiornamentoDati.telMov.tipo = "Unita";
					</SCRIPT>
				</td>			
          		<td class="<%=stile%>"><%=telMov%></td>				
          		<td class="<%=stile%>"><%=telAz%></td>
			</tr>
    	<%}%>    	    	   	   	             	
    	<% if (!FaxMov.equals("") && !FaxMov.equalsIgnoreCase(FaxAz)) {%>
    		<tr class="<%=stile%>">
           		<td class="<%=stile%>">   		
					<input type="checkbox" onchange="window.controllaCheckBox(this, this.tipo);" 
							name="FaxMov" value="<%=FaxMov%>"/>Fax
					<SCRIPT language="Javascript">
						document.FrmAggiornamentoDati.FaxMov.tipo = "Unita";
					</SCRIPT>
				</td>				
          		<td class="<%=stile%>"><%=FaxMov%></td>				
          		<td class="<%=stile%>"><%=FaxAz%></td>
			</tr>
    	<%}%> 
    	<% if (!emailMov.equals("") && !emailMov.equalsIgnoreCase(emailAz)) {%>
    		<tr class="<%=stile%>">
           		<td class="<%=stile%>">   		
					<input type="checkbox" onchange="window.controllaCheckBox(this, this.tipo);" 
							name="emailMov" value="<%=emailMov%>"/>E-Mail
					<SCRIPT language="Javascript">
						document.FrmAggiornamentoDati.emailMov.tipo = "Unita";
					</SCRIPT>
				</td>				
          		<td class="<%=stile%>"><%=emailMov%></td>				
          		<td class="<%=stile%>"><%=emailAz%></td>
			</tr>
    	<%}%>
    	<tr class="<%=stile%>">
           		<td class="<%=stile%>" colspan="3"> 
           			<div class="sezione2"></div>
           		</td>    	
    	</tr>
    	<tr class="<%=stile%>">
           		<td class="<%=stile%>">   		
					<input type="checkbox" name="selectAll" onclick="selectPage();"/>Seleziona tutti
				</td>    		
    	</tr>
      </table>
      <%out.print(htmlStreamBottom);%> 
      <input type="button" class="pulsante" onclick="controllaScelta();" value="Aggiorna"/>
      <input type="hidden" name="AggiornaAzienda" value="false"/>
      <input type="hidden" name="AggiornaUnita" value="false"/>
      <input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>"/>      
      <input type="hidden" name="PRGUNITA" value="<%=prgUnita%>"/>      
      <input type="hidden" name="PRGMOVIMENTOAPP" value="<%=prgMovimentoApp%>"/>
      <input type="hidden" name="PRGMOBILITAISCRAPP" value="<%=prgMobilitaApp%>"/>
	  <input type="hidden" name="FUNZ_AGGIORNAMENTO" value="<%=funz_agg%>">
	  <input type="hidden" name="strDesAteco" value="">
	  <input type="hidden" name="comune" value="">
	  <input type="hidden" name="differenze" value="false"/>
	  <input type="hidden" name="daMovimentiNew" value="<%=daMovimentiNew%>">
	  <input type="hidden" name="CODFISCAZESTERA" value="<%=cfAzEsteraMov%>">
	  <input type="hidden" name="RAGSOCAZESTERA" value="<%=ragSocEsteraMov%>"> 
	  &nbsp;&nbsp;
	  <input type="button" class="pulsante" value="Chiudi" onClick="window.close();"/>      
	</af:form>
<%}%>

</CENTER>    
</body>  
 