<!-- @author: Paolo Roccetti - Gennaio 2004 -->
<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ taglib uri="aftags" prefix="af"%>

<%@ page import="
    com.engiweb.framework.base.*,
    com.engiweb.framework.configuration.ConfigSingleton,
    
    com.engiweb.framework.error.EMFErrorHandler,
    com.engiweb.framework.util.JavaScript,
    it.eng.afExt.utils.*,
    it.eng.sil.security.User,
    it.eng.sil.util.*,
    it.eng.sil.module.movimenti.*,
    java.lang.*,
    java.text.*,
    java.math.*,
    java.sql.*,
    oracle.sql.*,
    java.util.*"%> 
<%  
    //Estraggo dalla request il nome della funzione a cui passare i dati trovati
    String updateFunctionName = StringUtils.getAttributeStrNotNull(serviceRequest, "updateFunctionName");
	String datFineMovStr = StringUtils.getAttributeStrNotNull(serviceRequest, "DATFINEMOV");
	//Decodifica del tipo movimento
	String codTipoMov = StringUtils.getAttributeStrNotNull(serviceRequest, "CODTIPOMOV");
	String decTipoMov = "trasformazione";
	if (codTipoMov.equalsIgnoreCase("CES")) {
		decTipoMov = "cessazione";
	}
	else {
		if (codTipoMov.equalsIgnoreCase("PRO")) {
			decTipoMov = "proroga";	
		}
	}
	String function_name = "";
	String daMovimentiNew = StringUtils.getAttributeStrNotNull(serviceRequest, "daMovimentiNew");
	boolean trasferimento = serviceRequest.containsAttribute("TRASFERIMENTOAZIENDA");
	String tempo = "";
	if (trasferimento) {
		function_name = "window.riportaMovimento";
	}
	else {
		function_name = "window.riportaMovimentoBIS";
	}	
	//Controllo se il lavoratore ha delle DID
	AbstractList didList = serviceResponse.getAttributeAsVector("CercaDIDPerMovimentiNonCollegati.ROWS.ROW");
	String confirmScollegato = "Attenzione: si sta tentando di inserire un movimento di " + decTipoMov + 
							   " senza il movimento precedente.\\n";

	if (didList.size() > 0) {
		confirmScollegato = "Attenzione: si sta tentando di inserire un movimento di " + decTipoMov + 
							" senza il movimento precedente " +
							"per un lavoratore che ha stipulato DID. " +
							"L\\'inserimento del movimento sarà possibile solo se la sua data di inizio " +
							"non cade all\\'interno del periodo di validità della DID.\\n";
	}
	
	confirmScollegato = confirmScollegato + " Si desidera continuare?";
	boolean insScollegato = false;
	boolean gestisciProScoll = false;
	String contesto = StringUtils.getAttributeStrNotNull(serviceRequest, "context");
	if (contesto.equalsIgnoreCase("valida") && codTipoMov.equalsIgnoreCase("PRO")) {
		gestisciProScoll = true;	
	}
	
	
	if (gestisciProScoll || codTipoMov.equalsIgnoreCase("TRA") || codTipoMov.equalsIgnoreCase("CES")) {
		insScollegato = true;
	}

	//Estraggo il numero di movimenti collegati con stessa testata e sede aziendale
	SourceBean rows = (SourceBean) serviceResponse.getAttribute("ListaCollegatiCompatibili.ROWS");
	int numMovColl  = ((Integer) rows.getAttribute("NUM_RECORDS")).intValue();
	if (rows.getAttribute("ROWS_X_PAGE_PARTIAL") != null) {
		numMovColl = 0;	
	}
    int numMovCollXLavoratore = 0; 
	int numMovCollXTestata = 0;
	if (numMovColl == 0) {
	  	//Estraggo il numero di movimenti collegati con stessa testata
		rows = (SourceBean) serviceResponse.getAttribute("LISTACOLLEGATICOMPATIBILIXTESTATA.ROWS");
		numMovCollXTestata = ((Integer) rows.getAttribute("NUM_RECORDS")).intValue();
		if (rows.getAttribute("ROWS_X_PAGE_PARTIAL") != null) {
			numMovCollXTestata = 0;	
		}
		if (numMovCollXTestata == 0) {
		  	//Estraggo il numero di movimenti collegati per quel lavoratore
			rows = (SourceBean) serviceResponse.getAttribute("ListaCollegatiCompatibiliLavoratore.ROWS");
			numMovCollXLavoratore = ((Integer) rows.getAttribute("NUM_RECORDS")).intValue();
			if (rows.getAttribute("ROWS_X_PAGE_PARTIAL") != null) {
				numMovCollXLavoratore = 0;	
			}
		}
	}
	
	SourceBean tipoContrattoRows = (SourceBean)serviceResponse.getAttribute("ControllaTipoContrattoTempo.ROWS");
	if (tipoContrattoRows != null) {
		Vector row_contratto = (Vector) tipoContrattoRows.getAttributeAsVector("ROW");
		if(row_contratto != null) {
			if(row_contratto.size() == 1)
				tempo = (String) ((SourceBean)row_contratto.get(0)).getAttribute("TEMPO");
			if(row_contratto.size() > 1)
			{
				if(datFineMovStr == null || datFineMovStr.equals(""))
					tempo = "LP";
				else
					tempo = "LT";
			}		
		}
		if (tempo == null) {tempo = "";}
	}
	
	//Oggetti per l'applicazione dello stile grafico
	String htmlStreamTop = StyleUtils.roundTopTable(false);
	String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<head>
  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
  <title>Lista Movimenti Compatibili</title>

    <script type="text/javascript">
    
<!--
    var scelto = false;
    var daMovimentiNew = '<%=daMovimentiNew%>';
    function riportaMovimento(prgMovColl, codTipoMovColl, numKloMovColl, dataInizioAvv, dataInizioMovPrec, codMonoTempoAvv, dataFineMovPrec, codTipoAss, prgAziendaColl, prgUnitaColl, codMonoTipo, flgTI, datFinePF, datFinePFGGSucc) {
	  if (window.opener.document.Frm1.CODMONOTIPO) {
	  	window.opener.document.Frm1.CODMONOTIPO.value = codMonoTipo;
	  }
	  if (window.opener.document.Frm1.FLGCONTRATTOTI) {
	  	window.opener.document.Frm1.FLGCONTRATTOTI.value = flgTI;
	  }
	  if (datFinePFGGSucc != "" &&
		  window.opener.document.Frm1.CODTIPOMOV &&
   		  window.opener.document.Frm1.CODTIPOMOV.value == "TRA" &&
   		  window.opener.document.Frm1.CODTIPOTRASF &&
   		  window.opener.document.Frm1.CODTIPOTRASF.value == "ZZ" &&
   		  window.opener.document.Frm1.datInizioMovTra && 
  	      window.opener.document.Frm1.datInizioMovTra.value == "") {
  		  window.opener.document.Frm1.datInizioMovTra.value = datFinePFGGSucc;
  	  }
	  if (datFinePF != "" &&
	      window.opener.document.Frm1.CODTIPOMOV &&
   		  window.opener.document.Frm1.CODTIPOMOV.value == "TRA" &&
   		  window.opener.document.Frm1.CODTIPOTRASF &&
   		  window.opener.document.Frm1.CODTIPOTRASF.value != "ZZ" &&
   		  window.opener.document.Frm1.datFinePF && 
  	      window.opener.document.Frm1.datFinePF.value == "") {
  		  window.opener.document.Frm1.datFinePF.value = datFinePF;
  	  }
	  else {
		  if (datFinePF != "" &&
			  window.opener.document.Frm1.CODTIPOMOV &&
	   		  window.opener.document.Frm1.CODTIPOMOV.value == "CES" &&
	   		  window.opener.document.Frm1.datFinePF && 
	  	      window.opener.document.Frm1.datFinePF.value == "") {
	  		  window.opener.document.Frm1.datFinePF.value = datFinePF;
		  }
	  }
	  window.opener.visualizzaPulsanteApprendistato(codMonoTipo, flgTI);
      if (window.opener.document.Frm1.MODIFICATAAZIENDA) { window.opener.document.Frm1.MODIFICATAAZIENDA.value = "true"; }
      window.opener.<%=updateFunctionName%>(prgMovColl, codTipoMovColl, numKloMovColl, dataInizioAvv, dataInizioMovPrec, codMonoTempoAvv, dataFineMovPrec, codTipoAss, false, prgAziendaColl, prgUnitaColl, daMovimentiNew);
      scelto = true;
      window.close();
    }

    function riportaMovimentoBIS(prgMovColl, codTipoMovColl, numKloMovColl, dataInizioAvv, dataInizioMovPrec, codMonoTempoAvv, dataFineMovPrec, codTipoAss, prgAziendaColl, prgUnitaColl, codMonoTipo, flgTI, datFinePF, datFinePFGGSucc) {
      if (window.opener.document.Frm1.CODMONOTIPO) {
   	  	window.opener.document.Frm1.CODMONOTIPO.value = codMonoTipo;
   	  }
   	  if (window.opener.document.Frm1.FLGCONTRATTOTI) {
   	  	window.opener.document.Frm1.FLGCONTRATTOTI.value = flgTI;
   	  }
   	  if (datFinePFGGSucc != "" &&
  		  window.opener.document.Frm1.CODTIPOMOV &&
          window.opener.document.Frm1.CODTIPOMOV.value == "TRA" &&
    	  window.opener.document.Frm1.CODTIPOTRASF &&
    	  window.opener.document.Frm1.CODTIPOTRASF.value == "ZZ" &&
    	  window.opener.document.Frm1.datInizioMovTra && 
   	      window.opener.document.Frm1.datInizioMovTra.value == "") {
   		  window.opener.document.Frm1.datInizioMovTra.value = datFinePFGGSucc;
   	  }
  	  if (datFinePF != "" &&
  	      window.opener.document.Frm1.CODTIPOMOV &&
   		  window.opener.document.Frm1.CODTIPOMOV.value == "TRA" &&
   		  window.opener.document.Frm1.CODTIPOTRASF &&
   		  window.opener.document.Frm1.CODTIPOTRASF.value != "ZZ" &&
   		  window.opener.document.Frm1.datFinePF && 
   	      window.opener.document.Frm1.datFinePF.value == "") {
   		  window.opener.document.Frm1.datFinePF.value = datFinePF;
      }
  	  else {
  		  if (datFinePF != "" &&
  			  window.opener.document.Frm1.CODTIPOMOV &&
  	   		  window.opener.document.Frm1.CODTIPOMOV.value == "CES" &&
  	   		  window.opener.document.Frm1.datFinePF && 
  	  	      window.opener.document.Frm1.datFinePF.value == "") {
  	  		  window.opener.document.Frm1.datFinePF.value = datFinePF;
  		  }
  	  }
   	  window.opener.visualizzaPulsanteApprendistato(codMonoTipo, flgTI);  
      if(window.opener.document.Frm1.MODIFICATAAZIENDA) { window.opener.document.Frm1.MODIFICATAAZIENDA.value = "true"; }
      window.opener.aggiornaPrecedenteBIS(prgMovColl, codTipoMovColl, numKloMovColl, dataInizioAvv, dataInizioMovPrec, codMonoTempoAvv, dataFineMovPrec, codTipoAss, false, prgAziendaColl, prgUnitaColl, daMovimentiNew);
      scelto = true;
      window.close();
    }

    //Controlla se l'utente sta uscendo a seguito di una scelta o se sta chiudendo la finestra 
    //senza scegliere
    function controllaScelta() {
      if (!scelto) {
        	window.opener.<%=updateFunctionName%>('', '', '', '', '', '', '', '', false, '', '');	
	  }
    }
    
    //Imposta l'inserimento/validazione non collegata
    function settaScollegato() {
      if (confirm('<%=confirmScollegato%>')) {
      	var funzione = '<%=updateFunctionName%>';
      	var tempo = '';
      	<% if(tempo.equalsIgnoreCase("LP")) { %>
			tempo = 'I';
		<% } else if(tempo.equalsIgnoreCase("LT")) { %>
			tempo = 'D';
		<% } %>
		window.opener.<%=updateFunctionName%>('', '', '', '', '', tempo, '', '', true, '', '');	    	
    	scelto = true;
      	window.close();
      }
    }
-->
    </script>
</head>
<body class="gestione" onunload='controllaScelta();' onload="this.focus()">
<center>
<%if (numMovColl != 0) {%>
  <af:JSButtonList moduleName="ListaCollegatiCompatibili" jsSelect="window.riportaMovimento"/>
<% } else if (numMovCollXTestata != 0) {%>
  <br/>
<%out.print(htmlStreamTop);%>
  <table class="main"><tr><td>&nbsp;</td></tr><tr><td><b>NON sono stati trovati movimenti collegati aventi la stessa unit&agrave; aziendale</b></td></tr></table>
<%out.print(htmlStreamBottom);%>
  <af:JSButtonList moduleName="ListaCollegatiCompatibiliXTestata" jsSelect="<%=function_name%>"/>  
<% } else if (numMovCollXLavoratore != 0) {%>
  <br/>
<%out.print(htmlStreamTop);%>
  <table class="main"><tr><td>&nbsp;</td></tr><tr><td><b>NON sono stati trovati movimenti collegati aventi la stessa azienda</b></td></tr></table>
<%out.print(htmlStreamBottom);%>
  <af:JSButtonList moduleName="ListaCollegatiCompatibiliLavoratore" jsSelect="<%=function_name%>"/>
<% } else {%>
  <br/><br/>
<%out.print(htmlStreamTop);%>
  <table class="main"><tr><td>&nbsp;</td></tr><tr><td><b>NON sono stati trovati movimenti collegati </b></td></tr><tr><td>&nbsp;</td></tr></table>
<%out.print(htmlStreamBottom);%>
  <br/><br/>
<%} if (insScollegato) {%>
  <input type="button" class="pulsanti" value="Inserisci movimento non collegato" onClick="window.settaScollegato();"/>
  <br/><br/>
<%}%>
  <input type="button" class="pulsanti" value="Chiudi" onClick="window.close();"/>
</center>
</body>
</html>