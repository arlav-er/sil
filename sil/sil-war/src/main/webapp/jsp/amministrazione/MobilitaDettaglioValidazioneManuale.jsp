<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
                  com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.*,
                  com.engiweb.framework.util.*,
                  it.eng.sil.module.movimenti.*,
                  it.eng.afExt.utils.*,
                  it.eng.sil.security.*,
                  it.eng.sil.util.*,
                  it.eng.sil.*,
                  java.util.*,
                  java.text.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>
 
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
<%
	String elemRisultati = "";
    String elemScriptAlert = "";
    String elemScriptConfirm = "";
  	// NOTE: Attributi della pagina (pulsanti e link)
  	PageAttribs attributi = new PageAttribs(user, "ValidazioneMobilitaGeneralePage");
  	boolean canModify = attributi.containsButton("SALVA");
  	boolean esitoValidazione = serviceResponse.containsAttribute("M_MobValidaMobilita.RECORDS.VALIDAZIONE_MANUALE") || 
  						       serviceRequest.containsAttribute("VALIDAZIONE_MANUALE");
  	if (esitoValidazione) {
  		canModify = false;
  		NavigationCache sceltaUnitaAzienda = (NavigationCache) sessionContainer.getAttribute("SCELTAUNITAAZIENDA_VALIDAZIONEMOB");
		if (sceltaUnitaAzienda != null) {
			sessionContainer.delAttribute("SCELTAUNITAAZIENDA_VALIDAZIONEMOB");
		}
  	}
  	String configuarazioneMob = "0";  //configurazione di default
  	String labelDataCrt = "Data CRT";
	String labelRegioneCrt = "Regione CRT";
	String labelProvinciaCrt = "Provincia CRT";
	String labelNumeroCrt = "Numero CRT";
  	if (serviceResponse.containsAttribute("M_GetConfig_Mobilita.ROWS.ROW.NUM")) {
		configuarazioneMob = serviceResponse.getAttribute("M_GetConfig_Mobilita.ROWS.ROW.NUM").toString();
		if (configuarazioneMob.equals("1")) {
			labelDataCrt = "Data CPM/Delibera Provinciale";
			labelRegioneCrt = "Regione CPM";
			labelProvinciaCrt = "Provincia CPM";
			labelNumeroCrt = "Numero CPM";
		}		
	}
  	String _funzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
  	String prgMobilitaIscrApp = (String) serviceRequest.getAttribute("prgMobilitaIscrApp");
  	String cdnLavoratore = serviceRequest.containsAttribute("CDNLAVORATORE")? serviceRequest.getAttribute("CDNLAVORATORE").toString():"";
  	String flgCambiamentiDati = (String) serviceRequest.getAttribute("flgCambiamentiDati");
  	//Oggetti per l'applicazione dello stile
  	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
  	String strRagioneSocialeAz = serviceRequest.getAttribute("strRagioneSocialeAz").toString();
  	String strNumAlboInterinali = serviceRequest.getAttribute("STRNUMALBOINTERINALI").toString();
  	String codTipoAzienda = "AZI";
  	String strIndirizzoUAz = serviceRequest.getAttribute("strIndirizzoUAz").toString();
  	String strPartitaIvaAz = serviceRequest.getAttribute("strPartitaIvaAz").toString();
  	String strUACap = serviceRequest.getAttribute("strUACap").toString();
  	String strTelUAz = serviceRequest.getAttribute("strTelUAz").toString();
  	String strFaxUAz = serviceRequest.getAttribute("strFaxUAz").toString();
  	String codTipoMob = serviceRequest.getAttribute("codTipoMob").toString();
  	String dataInizioMov = serviceRequest.getAttribute("datInizMovHid").toString();
  	String dataFineMov = serviceRequest.getAttribute("datFineMovHid").toString();
  	String dataInizioMob = serviceRequest.getAttribute("datInizioHid").toString();
  	String dataFineMob = serviceRequest.getAttribute("datFine").toString();
  	String dataInizioIndennita = serviceRequest.getAttribute("dataInizioIndenn").toString();
  	String dataFineIndennita = serviceRequest.getAttribute("dataFineIndenn").toString();
  	String dataMaxDiff = serviceRequest.getAttribute("datMaxDiff").toString();
  	String dataCRT = serviceRequest.getAttribute("datCRT").toString();
  	String codMotivoFine = serviceRequest.getAttribute("MotDecad").toString();
  	String prgAzienda = serviceRequest.getAttribute("PRGAZIENDA").toString();
  	String prgUnita = serviceRequest.getAttribute("PRGUNITA").toString();
  	String indennita_flg = serviceRequest.getAttribute("flgIndennita").toString();
  	String descMansione = serviceRequest.getAttribute("DESCMANSIONE").toString();
  	String strCodiceFiscaleAz = serviceRequest.getAttribute("strCodiceFiscale").toString();
  	String strCodiceFiscaleLav = serviceRequest.getAttribute("strCodiceFiscaleLav").toString();
  	String codComune = serviceRequest.getAttribute("codComune").toString();
  	String strComuneUAz = serviceRequest.getAttribute("strComuneUAz").toString();
  	String strUAMail = serviceRequest.getAttribute("strUAMail").toString();
  	String codAtecoUAz = serviceRequest.getAttribute("codAtecoUAz").toString();
  	String codCCNLAz = serviceRequest.getAttribute("codCCNLAz").toString();
  	String descrCCNLAz = serviceRequest.getAttribute("descrCCNLAz").toString();
  	String codCCNLMov = serviceRequest.getAttribute("codCCNL").toString();
  	String descrCCNLMov = serviceRequest.getAttribute("strCCNL").toString();
  	String strPosInps = serviceRequest.getAttribute("STRPOSINPS").toString();
  	String patInail = serviceRequest.getAttribute("STRPATINAIL").toString();
  	String strNumRegistroCommitt = serviceRequest.getAttribute("STRNUMREGISTROCOMMITT").toString();
  	String strCognomeLav = serviceRequest.getAttribute("strCognomeLav").toString();
  	String strNomeLav = serviceRequest.getAttribute("strNomeLav").toString();
  	String strSessoLav = serviceRequest.getAttribute("strSessoLav").toString();
  	String datNascLav = serviceRequest.getAttribute("datNascLav").toString();
  	String codComNascLav = serviceRequest.getAttribute("codComNascLav").toString();
  	String codCittadinanzaLav = serviceRequest.getAttribute("codCittadinanzaLav").toString();
  	String codComDom = serviceRequest.getAttribute("codComDomLav").toString();
  	String strIndirizzoDom = serviceRequest.getAttribute("strIndirizzoDomLav").toString();
  	String strCapDom = serviceRequest.getAttribute("strCapDomLav").toString();
  	String strTelDom = serviceRequest.getAttribute("strTelDomLav").toString();
  	String codComRes = serviceRequest.getAttribute("codComResLav").toString();
  	String strIndirizzoRes = serviceRequest.getAttribute("strIndirizzoResLav").toString();
  	String strCapRes = serviceRequest.getAttribute("strCapResLav").toString();
  	String strTelRes = serviceRequest.getAttribute("strTelResLav").toString();
  	String datScadenza = serviceRequest.getAttribute("datScadenza").toString();
  	String codTipoTitolo = serviceRequest.getAttribute("codTipoTitolo").toString();
  	String codTipoTitoloGenerico = serviceRequest.getAttribute("codTipoTitoloGenerico").toString();
  	String strCellLav = serviceRequest.getAttribute("strCellLav").toString();
	String strMailLav = serviceRequest.getAttribute("strMailLav").toString();
	String codCpiLav = serviceRequest.getAttribute("codCpiLav").toString();
	String dataComunicaz = serviceRequest.getAttribute("dataComunicaz").toString();
	String codMansione = serviceRequest.getAttribute("codMansione").toString();
	String codGradoMov = serviceRequest.getAttribute("codGradoHid").toString();
	String strLivello = serviceRequest.getAttribute("strLivello").toString();
	String flagDiff = serviceRequest.getAttribute("flagDiff").toString();
	String natGiuridicaAz = serviceRequest.getAttribute("natGiuridicaAz").toString();
	String codNatGiuridicaAz = serviceRequest.getAttribute("CODNATGIURIDICAAZ").toString();
	String desctTipoAzienda = serviceRequest.getAttribute("DESCRTIPOAZIENDA").toString();
	String strDesAtecoUAz = serviceRequest.getAttribute("strDesAtecoUAz").toString();
	String numCRT = serviceRequest.getAttribute("numCRT").toString();
	String regCRT = serviceRequest.getAttribute("regioneCRT").toString();
	String provCRT = serviceRequest.getAttribute("provCRT").toString();
	String motScorrDataMaxDiff = serviceRequest.getAttribute("MotScorrDataMaxDiff").toString();
	String prgMovimento = serviceRequest.getAttribute("prgMovimento").toString();
	String numOreSett = serviceRequest.getAttribute("numOreSett").toString();
    String dataDomanda = serviceRequest.getAttribute("dataDomanda").toString();
	String codDomanda = StringUtils.getAttributeStrNotNull(serviceRequest, "CODDOMANDA");
	
	String flgNonImprenditore = "";
	if(serviceRequest.getAttribute("flgNonImprenditore") == null)
		flgNonImprenditore = "N";
	else {
		String flgTmp = serviceRequest.getAttribute("flgNonImprenditore").toString();
		if ("".equals(flgTmp.trim()))
			flgNonImprenditore = "N";
		else
			flgNonImprenditore = flgTmp;
	}	
	String flgCasoDubbio = "";
	
	String str_conf_MBDUBBIO = serviceResponse.containsAttribute("M_GetConfigValue.ROWS.ROW.NUM")?serviceResponse.getAttribute("M_GetConfigValue.ROWS.ROW.NUM").toString():"0";
	
	if ("1".equals(str_conf_MBDUBBIO)){
		if(serviceRequest.getAttribute("flgCasoDubbio") == null)
			flgCasoDubbio = "N";
		else {
			String flgCasiDubbiTmp = serviceRequest.getAttribute("flgCasoDubbio").toString();
			if ("".equals(flgCasiDubbiTmp.trim()))
				flgCasoDubbio = "N";
			else
				flgCasoDubbio = flgCasiDubbiTmp;
		}	
	}
	
	boolean sceltaMov = (prgMovimento.equals("")?true:false);
	if (esitoValidazione) {
		if (prgAzienda.equals("")) {
			prgAzienda = serviceResponse.containsAttribute("M_MobValidaMobilita.RECORDS.PRGAZIENDA_VALIDAZIONEMOB")?serviceResponse.getAttribute("M_MobValidaMobilita.RECORDS.PRGAZIENDA_VALIDAZIONEMOB").toString():"";
		}		
		if (prgUnita.equals("")) {
			prgUnita = serviceResponse.containsAttribute("M_MobValidaMobilita.RECORDS.PRGUNITA_VALIDAZIONEMOB")?serviceResponse.getAttribute("M_MobValidaMobilita.RECORDS.PRGUNITA_VALIDAZIONEMOB").toString():"";
		}
		if (cdnLavoratore.equals("")) {
			cdnLavoratore = serviceResponse.containsAttribute("M_MobValidaMobilita.RECORDS.CDNLAVORATORE_VALIDAZIONEMOB")?serviceResponse.getAttribute("M_MobValidaMobilita.RECORDS.CDNLAVORATORE_VALIDAZIONEMOB").toString():"";
		}	
	}
	String forzaInsEtaApprendista = StringUtils.getAttributeStrNotNull(serviceRequest, "FORZA_INSERIMENTO_ETA_APPRENDISTATO");
  	if (forzaInsEtaApprendista.equals("")) forzaInsEtaApprendista = "false";
%>

<html>
  <head>
    <%@ include file="../global/fieldChanged.inc" %>
    <%@ include file="fieldMobScript.inc" %>
    <link rel="stylesheet" href="../../css/stili.css" type="text/css">
    <af:linkScript path="../../js/"/>
    <title>Dettaglio Mobilità</title>
    <script language="Javascript">

    function selezionaComboAgevolazioni() {
    }    
    
    function controlloMobilitaValidazione() {
		if (document.Frm1.flgIndennita.value == "S") {
			if (document.Frm1.dataInizioIndenn.value == "" || document.Frm1.dataFineIndenn.value == "") {
	      		alert("Attenzione! Bisogna valorizzare data inizio e data fine indennità.");
	      		return false;
	      	}
	      	else {
	      		if (compDate(document.Frm1.dataFineIndenn.value, document.Frm1.dataInizioIndenn.value) < 0) {
					alert("Attenzione! Data fine indennità deve essere maggiore o uguale della data inizio indennità.");
			      	return false;	
				}
				if (compDate(document.Frm1.dataFineIndenn.value, document.Frm1.datMaxDiff.value) > 0) {
					alert("Attenzione! Data fine indennità deve essere minore o uguale della data max differimento.");
			      	return false;	
				}
	      	}
		}
		
		if (document.Frm1.provCRT.value != "" && document.Frm1.regioneCRT.value != "") {
			alert("Attenzione! Bisogna indicare come ente la regione o la provincia.");
	      	return false;
		}
		
		if (compDate(document.Frm1.datFineMov.value, document.Frm1.datInizMov.value) < 0) {
			alert("Attenzione! Data fine movimento deve essere maggiore o uguale della data inizio movimento.");
	      	return false;	
		}
		
		if (compDate(document.Frm1.datMaxDiff.value, document.Frm1.datFine.value) < 0) {
			alert("Attenzione! Data max differimento deve essere maggiore o uguale della data fine mobilità.");
	      	return false;	
		}
		
		document.Frm1.datInizMovHid.value = document.Frm1.datInizMov.value;
		document.Frm1.datFineMovHid.value = document.Frm1.datFineMov.value;
		document.Frm1.datInizioHid.value = document.Frm1.datInizio.value;
		document.Frm1.codGradoHid.value = document.Frm1.codGrado.value;
		
		if (document.Frm1.flagDisponibilita != null) {
			if (document.Frm1.flagDisponibilita.checked) {
				document.Frm1.flagDisponibilita.value = "S";	
			}
			else {
				document.Frm1.flagDisponibilita.value = "";
				if (document.Frm1.FLGSCHEDA.value == "S") {
					if (confirm("Verranno cancellate le disponibilità inserite, continuare ?")) {
						return true;
					}
					else {
						return false;
					}
				}
			}
		}	
			
		return true;
	}
    
    function gestioneGenerale () {
    	document.Frm1.PAGE.value = "ValidazioneMobilitaGeneralePage";
    	document.Frm1.datInizioHid.value = document.Frm1.datInizio.value;
    	document.Frm1.datInizMovHid.value = document.Frm1.datInizMov.value;
		document.Frm1.datFineMovHid.value = document.Frm1.datFineMov.value;
		document.Frm1.codGradoHid.value = document.Frm1.codGrado.value;
    	document.Frm1.submit();
    }
    
    function apriListaAziendeUnita(cdnLav, prgAzienda){
	    var f = "AdapterHTTP?PAGE=AmstrListaAziendePage&cdnLavoratore=" + cdnLav + "&prgAzienda=" + prgAzienda;
	    var t = "Ricerca_Azienda";
	    var feat = "toolbar=no,location=no,directories=no,status=NO,menubar=no,scrollbars=yes,resizable=yes,width=700,height=400,top=100,left=100"
	    window.open(f, t, feat);
	}
	
    function cancCampi() { 
      window.document.Frm1.flgIndennita.value = "";
      window.document.Frm1.dataInizioIndenn.value = "";
      window.document.Frm1.dataFineIndenn.value = "";
      var objIndennita = document.getElementById('opzioni_indennita');
      objIndennita.style.display = "none";
      window.document.Frm1.prgMovimento.value = "";
      //quando deassocio il movimento alla mobilità devo 
      //rendere editabili i seguenti campi
      window.document.Frm1.datInizMov.readOnly = false;
      window.document.Frm1.datFineMov.readOnly = false;
      window.document.Frm1.datInizio.readOnly = false;
      window.document.Frm1.datInizMov.disabled = false;
      window.document.Frm1.datFineMov.disabled = false;
      window.document.Frm1.datInizio.disabled = false;
      window.document.Frm1.datInizMov.className="inputEdit";
      window.document.Frm1.datFineMov.className="inputEdit";
      window.document.Frm1.datInizio.className="inputEdit";
      window.document.Frm1.datInizMov.className="inputEdit";
      window.document.Frm1.datFineMov.className="inputEdit";
      window.document.Frm1.datInizio.className="inputEdit";
      
      var objDate = window.document.getElementById('sezione_valorizza_date');
      objDate.style.display = "inline";
    }
    
    </script>
    <script type="text/javascript" src="../../js/movimenti/common/MovimentiSezioniATendina.js" language="JavaScript"></script>    
    <script type="text/javascript" src="../../js/movimenti/common/_confirmDaStOcc.js" language="JavaScript"></script>
  </head>
 
  <body class="gestione">
    <%@ include file="validazione/include/GestioneRisultati.inc" %>	
	<af:form name="Frm1" onSubmit="controlloMobilitaValidazione()" method="POST" action="AdapterHTTP">
	
	<div class="menu">
	    <a id="linguettaGenerale" href='#' onclick='gestioneGenerale();' class='bordato1'><span class='tr_round11'>&nbsp;Generale&nbsp;</span></a>
	    <a id="linguettaMobilita" href='#' class='sel1'><span class='tr_round1'>&nbsp;Mobilità&nbsp;</span></a>
  	</div>
	
	<%out.print(htmlStreamTop);%>
	<%@ include file="validazione/include/MobilitaCampiLayOut.inc"%>
	<%out.print(htmlStreamBottom);%>
	
	<%if (canModify) {%>
		<center>
		<table>
		<tr><td align="center">
  		<input type="submit" class="pulsanti" name="submitbutton" value="Valida" onclick="resetFlagForzatura();"/>
  		</td></tr>
  		</table>
  		</center>
	<%}%>&nbsp;
	 
	<input type="hidden" name="PAGE" value="MobEffettuaValidazioneMobilitaPage" />
	<input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
	<input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>"/>
	<input type="hidden" name="prgMobilitaIscrApp" value="<%=prgMobilitaIscrApp%>"/>
	<input type="hidden" name="strCodiceFiscale" value="<%=strCodiceFiscaleAz%>"/>
	<input type="hidden" name="codComune" value="<%=codComune%>"/>
	<input type="hidden" name="strComuneUAz" value="<%=strComuneUAz%>"/>
	<input type="hidden" name="strPartitaIvaAz" value="<%=strPartitaIvaAz%>"/>
	<input type="hidden" name="strRagioneSocialeAz" value="<%=strRagioneSocialeAz%>"/>
	<input type="hidden" name="STRNUMALBOINTERINALI" value="<%=strNumAlboInterinali%>"/>
	<input type="hidden" name="codTipoAzienda" value="<%=codTipoAzienda%>"/>
	<input type="hidden" name="DESCRTIPOAZIENDA" value="<%=desctTipoAzienda%>"/>
	<input type="hidden" name="strIndirizzoUAz" value="<%=strIndirizzoUAz%>"/>
	<input type="hidden" name="strTelUAz" value="<%=strTelUAz%>"/>
	<input type="hidden" name="strUACap" value="<%=strUACap%>"/>
	<input type="hidden" name="strFaxUAz" value="<%=strFaxUAz%>"/>
	<input type="hidden" name="strUAMail" value="<%=strUAMail%>"/>
	<input type="hidden" name="codAtecoUAz" value="<%=codAtecoUAz%>"/>
	<input type="hidden" name="strDesAtecoUAz" value="<%=strDesAtecoUAz%>"/>
	<input type="hidden" name="CODNATGIURIDICAAZ" value="<%=codNatGiuridicaAz%>"/>
	<input type="hidden" name="natGiuridicaAz" value="<%=natGiuridicaAz%>"/>
	<input type="hidden" name="STRPOSINPS" value="<%=strPosInps%>"/>
	<input type="hidden" name="STRPATINAIL" value="<%=patInail%>"/>
	<input type="hidden" name="STRNUMREGISTROCOMMITT" value="<%=strNumRegistroCommitt%>"/>
	<input type="hidden" name="strCodiceFiscaleLav" value="<%=strCodiceFiscaleLav%>"/>
	<input type="hidden" name="strCognomeLav" value="<%=strCognomeLav%>"/>
	<input type="hidden" name="strNomeLav" value="<%=strNomeLav%>"/>
	<input type="hidden" name="strSessoLav" value="<%=strSessoLav%>"/>
	<input type="hidden" name="datNascLav" value="<%=datNascLav%>"/>
	<input type="hidden" name="codComNascLav" value="<%=codComNascLav%>"/>
	<input type="hidden" name="codCittadinanzaLav" value="<%=codCittadinanzaLav%>"/>
	<input type="hidden" name="codComDomLav" value="<%=codComDom%>"/>
	<input type="hidden" name="strIndirizzoDomLav" value="<%=strIndirizzoDom%>"/>
	<input type="hidden" name="strCapDomLav" value="<%=strCapDom%>"/>
	<input type="hidden" name="strTelDomLav" value="<%=strTelDom%>"/>
	<input type="hidden" name="codComResLav" value="<%=codComRes%>"/>
	<input type="hidden" name="strIndirizzoResLav" value="<%=strIndirizzoRes%>"/>
	<input type="hidden" name="strCapResLav" value="<%=strCapRes%>"/>
	<input type="hidden" name="strTelResLav" value="<%=strTelRes%>"/>
	<input type="hidden" name="datScadenza" value="<%=datScadenza%>"/>
	<input type="hidden" name="codTipoTitolo" value="<%=codTipoTitolo%>"/>
 	<input type="hidden" name="codTipoTitoloGenerico" value="<%=codTipoTitoloGenerico%>"/>
	<input type="hidden" name="strCellLav" value="<%=strCellLav%>"/>
 	<input type="hidden" name="strMailLav" value="<%=strMailLav%>"/>
 	<input type="hidden" name="codCpiLav" value="<%=codCpiLav%>"/>
 	<input type="hidden" name="dataComunicaz" value="<%=dataComunicaz%>"/>
 	<input type="hidden" name="CODTIPOASS" value="NO0"/>
 	<input type="hidden" name="CODTIPOMOV" value="AVV"/>
 	<input type="hidden" name="CODMONOTEMPO" value="I"/>
 	<input type="hidden" name="flagDiff" value="<%=flagDiff%>"/>
 	<input type="hidden" name="CURRENTCONTEXT" value="valida"/>
	<input type="hidden" name="PROVENIENZA" value="linguetta"/>
	<input type="hidden" name="prgMovimento" value="<%=prgMovimento%>"/>
	<input type="hidden" name="codCCNLAz" value="<%=codCCNLAz%>"/>
	<input type="hidden" name="descrCCNLAz" value="<%=descrCCNLAz%>"/>
	<input type="hidden" name="flgCambiamentiDati" value="<%=flgCambiamentiDati%>">
	<input type="hidden" name="FORZA_INSERIMENTO" value="false"/>
	<input type="hidden" name="CONTINUA_CALCOLO_SOCC" value="false"/>
	<input type="hidden" name="FORZA_INSERIMENTO_ETA_APPRENDISTATO" value="<%=forzaInsEtaApprendista%>"/>
	<%if (esitoValidazione) {%>
		<input type="hidden" name="VALIDAZIONE_MANUALE" value="OK"/>
	<%}%>
	<center>
	<%@ include file="validazione/include/PulsanteRitornoLista.inc" %> 
	</center>
    </af:form>
    <%
	if(elemScriptAlert != null && !elemScriptAlert.equals("")) out.print(elemScriptAlert);
  	if(elemScriptConfirm != null && !elemScriptConfirm.equals("")) { 
  		%>
  		<script language="javascript">
  			document.Frm1.datInizMovHid.value = document.Frm1.datInizMov.value;
			document.Frm1.datFineMovHid.value = document.Frm1.datFineMov.value;
			document.Frm1.datInizioHid.value = document.Frm1.datInizio.value;
			document.Frm1.codGradoHid.value = document.Frm1.codGrado.value;
  		</script>
  		<%
  		out.print(elemScriptConfirm);
  	}%>
 </body>
</html>