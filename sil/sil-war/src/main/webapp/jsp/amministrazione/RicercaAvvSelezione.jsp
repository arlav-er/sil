<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
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
  PageAttribs attributi = new PageAttribs(user, "ASRicercaAvvSelezionePage");
    
  // variabili richieste dalla sezione del lavoratore  (vedi commento su SezioneLavoratore.inc)
  String fSelLav = ""; 
      
  String _funzione=(String) serviceRequest.getAttribute("CDNFUNZIONE");
  
  
  //Oggetti per l'applicazione dello stile grafico
  String htmlStreamTop = StyleUtils.roundTopTable(false);
  String htmlStreamBottom = StyleUtils.roundBottomTable(false);

  //Genero informazioni sul lavoratore se sono in un menu contestuale
  String cdnLavoratore = StringUtils.getAttributeStrNotNull(serviceRequest, "CDNLAVORATORE");
  String strCodiceFiscaleLav = "";
  String strNomeLav = "";
  String strCognomeLav = "";

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
  
  // variabile per il torna alla lista 
  String _backToList = "GENERALE";

  
  // ritornando alla lista dalla ricerca si parte dalla pagina vuota
    prgAzienda = "";
    prgUnita = "";
    cdnLavoratore = "";

  ///////////////
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
      } else if (strFlgCfOk.equalsIgnoreCase("N")){
            strFlgCfOk = "No";
      }
    }
  }
  if (!prgAzienda.equals("") && !prgUnita.equals("")){
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
        } else if (strFlgDatiOk.equalsIgnoreCase("N")){
              strFlgDatiOk = "No";
        }
    }
  }
%>
<%

%>

<html>
<head>
<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<af:linkScript path="../../js/" />

<title>Ricerca Movimenti</title>
<%@ include file="../movimenti/DynamicRefreshCombo.inc" %>
<script language="JavaScript" src="../../js/script_comuni.js"></script>
<script language="Javascript">
<% 
 	//Genera il Javascript che si occuperà di inserire i links nel footer
    attributi.showHyperLinks(out, requestContainer,responseContainer,"");
%>
</script>

<%@ include file="MovimentiRicercaSoggetto.inc" %>
<%@ include file="MovimentiSezioniATendina.inc" %>
<%@ include file="Common_Function_Mov.inc" %>
<%@ include file="../movimenti/DynamicRefreshCombo.inc" %>


<script type="text/javascript">
<!--
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
        provenienza = "lavoratore";
        
    }

	function controllaDateRange(di, df, range){
	    var dataI = "";
	    var dataF = "";
	    var ONE_DAY = 1000 * 60 * 60 * 24;
	
	    if (di != "") {
	      dataI = new String(di);
	      var annoDataDal = dataI.substr(6,4);
	      var meseDataDal = dataI.substr(3,2);
	      var giornoDataDal = dataI.substr(0,2);
	    } else {
	    	return false;
	    }
	
	    if (df != "") {
	      dataF = new String(df);
	      var annoDataAl = dataF.substr(6,4);
	      var meseDataAl = dataF.substr(3,2);
	      var giornoDataAl = dataF.substr(0,2);
	    } else {
	    	return false;
	    }
	
	    var dataDal = new Date(annoDataDal, meseDataDal-1, giornoDataDal);
		var dataAl = new Date(annoDataAl, meseDataAl-1, giornoDataAl);
	
		var dataDal_ms = dataDal.getTime();
	    var dataAl_ms = dataAl.getTime();
	    var difference_ms = Math.abs(dataDal_ms - dataAl_ms);
	    var delta = 1 + Math.round(difference_ms/ONE_DAY);   
	
		if (delta > range) {
			return false;
		}
		
	    return true;
	  }
	// data inizio > data fine
	function dateSuccessive() {
		var di = document.Frm1.datAvvDal.value;
        var df = document.Frm1.datAvvAl.value;
        if (di=="" || df=="") return true;
        diS = di.split("/");
        dfS = df.split("/");
        return  (diS[2]+""+diS[1]+""+diS[0] <= dfS[2]+""+dfS[1]+""+dfS[0]);
	    
	}
	function controllaParametri(){
    	var msg;
	    // dati lavoratore
	    var lav_cognome = document.Frm1.cognome.value;
	    var lav_nome = document.Frm1.nome.value;
	    var lav_cf = document.Frm1.codiceFiscaleLavoratore.value; 
	    // dati azienda	    
	    var codFiscaleAzienda = document.Frm1.codFiscaleAzienda.value;
	    var pIva = document.Frm1.pIva.value;
	    var FLGDATIOK = document.Frm1.FLGDATIOK.value;
	    var ragioneSociale = document.Frm1.ragioneSociale.value;
	    var IndirizzoAzienda = document.Frm1.IndirizzoAzienda.value;    
	    var descrTipoAz = document.Frm1.descrTipoAz.value;
        var codTipoAz = document.Frm1.codTipoAz.value;
		var CODNATGIURIDICA = document.Frm1.CODNATGIURIDICA.value;
		// dati richiesta
		var prgRichiestaAz = document.Frm1.prgRichiestaAz.value;
		// date dell' avviamento
		var di = document.Frm1.datAvvDal.value;
        var df = document.Frm1.datAvvAl.value;
        var ok = "";
		
	    // caso 1
	    if ((lav_cognome!="") || (lav_nome!="") || (lav_cf!="") ||
	    	(codFiscaleAzienda!="") || (pIva!="") ||  (ragioneSociale!="") || 
	    	(prgRichiestaAz!="")){
	    		if (!dateSuccessive()) {
	    			alert("La 'data a' deve essere uguale o successiva alla 'data da'");
	    			return false;
	    		}else return true;
	    	
	    } else {
	    // caso 2
	    	ok = controllaDateRange(di,df,60) && dateSuccessive();
	    } 

	    if (ok) return (true);

		msg = "Parametri generici.\n"+
			"OPZIONE 1 - Uno dei seguenti valori: Lavoratore / Azienda / Richiesta di personale\n"+
			"OPZIONE 2 - Periodo data avv. a selezione da... a... con periodo max 60 gg. \n";
			
	    alert(msg);
	    return (false);
    }
		
	//FUNZIONE NECESSARIA PER LA DECODIFICA NELLA MASCHERA PARAMETRI DI RICERCA	
	function valorizzaHidden() {
						 
		  document.Frm1.strStatoAvv.value = document.Frm1.statoAvv.options[document.Frm1.statoAvv.selectedIndex].text;
		  document.Frm1.strCPI.value = document.Frm1.sel_cpi.options[document.Frm1.sel_cpi.selectedIndex].text;
		  
		  return true;		  
	}     
    
-->
</script>
</head>

<body class="gestione" onload="rinfresca()">
<br />
<p class="titolo">Ricerca avviamenti a selezione / a lavoro</p>
<br />
<center><af:form name="Frm1" method="POST" action="AdapterHTTP" onSubmit="controllaParametri(), valorizzaHidden()">
	
	<input type="hidden" name="PAGE" value="ASListaAvvSelezionePage" />
	<input type="hidden" name="cdnFunzione" value="<%=_funzione%>" />
	<input type="hidden" name="APRI_EV" value="1" />
	<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>" />
	<input type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>" />
	<input type="hidden" name="PRGUNITA" value="<%=prgUnita%>" />
	<input type="hidden" name="strStatoAvv" value="" />
	<input type="hidden" name="strCPI" value="" />
	<input type="hidden" name="_backToList" value="<%=_backToList%>" />
	
		
	<%out.print(htmlStreamTop);%>
	<table class="main" border="0">
		<!-- sezione lav se si proviene dal menu contestuale del lav. -->
		<%@ include file="SezioneLavoratore.inc" %>
		<!-- sezione azienda -->
		<tr class="note">
			<td colspan="2">
			<div class="sezione2"><img id='tendinaAzienda' alt="Chiudi"
				src="../../img/aperto.gif"
				onclick="cambiaTendina(this,'aziendaSez',document.Frm1.pIva);" />&nbsp;&nbsp;&nbsp;Azienda
			&nbsp;&nbsp; <%if (prgAzienda.equals("") && prgUnita.equals("")) {%>
			<a href="#"
				onClick="javascript:apriSelezionaSoggetto('Aziende', 'aggiornaAzienda','','','');"><img
				src="../../img/binocolo.gif" alt="Cerca"></a> <%}%></div>
			</td>
		</tr>
		<tr>
			<td colspan="2">
			<div id="aziendaSez" style="display: none">
			<table class="main" width="100%" border="0">
				<tr>
					<td class="etichetta">Codice Fiscale</td>
					<td class="campo"><af:textBox classNameBase="input" type="text"
						name="codFiscaleAzienda" readonly="true"
						value="<%=codFiscaleAzienda%>" size="30" maxlength="16" /></td>
				</tr>
				<tr>
					<td class="etichetta">Partita IVA</td>
					<td class="campo"><af:textBox classNameBase="input" type="text"
						name="pIva" readonly="true" value="<%=pIva%>" size="30"
						maxlength="11" /> &nbsp;&nbsp;&nbsp;Validità C.F./P.
					IVA&nbsp;&nbsp;<af:textBox classNameBase="input" type="text"
						name="FLGDATIOK" readonly="true" value="<%=strFlgDatiOk%>"
						size="3" maxlength="3" /></td>
				</tr>
				<tr>
					<td class="etichetta">Ragione Sociale</td>
					<td class="campo"><af:textBox classNameBase="input" type="text"
						name="ragioneSociale" readonly="true" value="<%=ragioneSociale%>"
						size="60" maxlength="100" /></td>
				</tr>
				<tr>
					<td class="etichetta">Indirizzo (Comune)</td>
					<td class="campo"><af:textBox classNameBase="input" type="text"
						name="IndirizzoAzienda" readonly="true"
						value="<%=IndirizzoAzienda%>" size="60" maxlength="100" /></td>
				</tr>
				<tr>
					<td class="etichetta">Tipo Azienda</td>
					<td class="campo"><af:textBox classNameBase="input" type="text"
						name="descrTipoAz" readonly="true" value="<%=descrTipoAz%>"
						size="30" maxlength="30" /> <af:textBox classNameBase="input"
						type="hidden" name="codTipoAz" readonly="true"
						value="<%=codTipoAz%>" size="10" maxlength="10" /> <af:textBox
						classNameBase="input" type="hidden" name="CODNATGIURIDICA"
						readonly="true" value="<%=codnatGiurAz%>" size="10" maxlength="10" />
					</td>
				</tr>
			</table>
			</div>
			</td>
		</tr>
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="2">
			<div class="sezione2">Richiesta di personale</div>
			</td>
		</tr>
		<tr>
			<td class="etichetta">Numero richiesta</td>
			<td class="campo"><af:textBox name="prgRichiestaAz" value=""
				size="12" maxlength="10" />&nbsp;&nbsp; Anno <af:textBox
				name="anno" type="integer" value="" validateOnPost="true" size="4"
				maxlength="4" /></td>
		</tr>
		<tr>
			<td colspan="2">
			<div class="sezione2">Avv. a selezione / a lavoro</div>
			</td>
		</tr>
		<tr>
			<td class="etichetta">Data avv. da</td>
			<td class="campo"><af:textBox type="date" name="datAvvDal"
				title="Data avviamento" value="" size="12" maxlength="10"
				validateOnPost="true" />&nbsp;&nbsp; a <af:textBox type="date"
				name="datAvvAl" title="Data avviamento" value="" size="12"
				maxlength="10" validateOnPost="true" /></td>
		</tr>
		<tr>
		  <td class="etichetta">Stato dell'avv.</td>
		  <td class="campo">
		    <af:comboBox name="statoAvv" 
		                 size="1"
		                 title="statoAvv"
		                 multiple="false"
		                 required="false"
		                 focusOn="false"		                 
		                 moduleName="M_STATO_AVVIAMENTO"
		                 addBlank="true"
		                 blankValue=""
		    />
		  </td>
		</tr>	
		<tr>
		  	<td class="etichetta">Solo avv. senza stato</td>
		  	<td class="campo">
		    <input type="checkbox" name="flgAvvNoStato">		    	
		  	</td>
		</tr>
		<!-- utilizzo lo stesso modulo usato nella ricerca degli appuntamenti per l'invio SMS -->
		<tr>
		  <td class="etichetta">Centro per l'Impiego</td>
		  <td class="campo">
		    <af:comboBox name="sel_cpi"
		                 size="1"
		                 title="Scelta CPI"
		                 multiple="false"
		                 required="false"
		                 focusOn="false"		                 
		                 moduleName="M_SMS_COMBO_CPI"
		                 addBlank="true"
		                 blankValue=""
		    />
		  </td>
		</tr>						
		<tr>
			<td colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="2" align="center"><!--<input class="pulsanti" type="submit" id="pulsanteRicerca" name="azione" value="Cerca"/>-->
			<input class="pulsanti" type="submit" name="azione" value="Cerca" /> &nbsp;&nbsp; <input class="pulsanti"
				type="reset" name="azione" value="Annulla" />
		<tr>
			<td colspan="2" align="center">&nbsp;</td>
		</tr>
	</table>
	<%out.print(htmlStreamBottom);%>
</af:form></center>
<script language="javascript">
  var imgV = document.getElementById("tendinaLav");
  <%if (!strNomeLav.equals("")) {%>
    cambiaLavMC("lavoratoreSez","inline");
    imgV.src = imgAperta;
    imgV.alt="Chiudi";
  <%} else {%>
    cambiaTendina(imgV,"lavoratoreSez",document.Frm1.nome);
  <%}%>
        
  imgV = document.getElementById("tendinaAzienda");
  <%if (!pIva.equals("")) {%>
    cambiaLavMC("aziendaSez","inline");
    imgV.src = imgAperta;
    imgV.alt="Chiudi";
  <%} else {%>
    cambiaTendina(imgV,"aziendaSez",document.Frm1.pIva)
  <%}%>

</script>
</body>
</html>
