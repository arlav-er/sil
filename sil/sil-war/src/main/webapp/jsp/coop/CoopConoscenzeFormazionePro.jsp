<%@ page contentType="text/html;charset=utf-8"%>

<%@ taglib uri="aftags" prefix="af"%>
<%@ taglib uri="presel" prefix="ps"%>

<%@ include file="../global/noCaching.inc"%>
<%@ include file="../global/getCommonObjects.inc"%>


<%@ page
	import="com.engiweb.framework.base.*,com.engiweb.framework.dispatching.module.AbstractModule,com.engiweb.framework.util.QueryExecutor,it.eng.sil.security.*,it.eng.afExt.utils.*,it.eng.sil.security.ProfileDataFilter,it.eng.sil.util.*,java.util.*,java.math.*,java.io.*,it.eng.sil.module.coop.GetDatiPersonali,com.engiweb.framework.security.*"%>

<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>

<%
	String cdnLavoratore = "0";//(String )serviceRequest.getAttribute("CDNLAVORATORE");
	String _current_page = (String) serviceRequest.getAttribute("PAGE");
	ProfileDataFilter filter = new ProfileDataFilter(user,
			_current_page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	PageAttribs attributi = new PageAttribs(user, _current_page);

	boolean canInsert = false;
	boolean canDelete = false;
	boolean canModify = false;
	boolean readOnlyStr = true;
	boolean readOnly;

	boolean canView = filter.canView();
	if (!canView) {
		response
				.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}
	readOnlyStr = !canInsert;
	readOnly = readOnlyStr;
	canModify = canInsert;
%>

<%
	Vector rows = serviceResponse
			.getAttributeAsVector("M_COOP_ListForPro_dalla_cache.ROWS.ROW");
	// recupero dalla response il cdnLavoratore
	Vector listaForPro = serviceResponse
			.getAttributeAsVector("M_COOP_ListForPro_dalla_cache.ROWS.ROW");
	if (listaForPro.size() > 0) {
		SourceBean unaForPro = (SourceBean) listaForPro.get(0);
		cdnLavoratore = StringUtils.getAttributeStrNotNull(unaForPro,
				"cdnLavoratore");
	}

	Vector vectListaConoscenze = serviceResponse
			.getAttributeAsVector("M_COOP_ListForPro_dalla_cache.ROWS.ROW");

	SourceBean row = null;

	//Object codiceUtente= sessionContainer.getAttribute("_CDUT_");

	String prgCorso = "0";
	String codCorso = "";
	String codTipoCertificato = "";
	String descCorso = "";
	String strDescrizione = "";
	String strEnte = "";
	String flgCompletato = "";
	String flgStage = "";
	String strContenuto = "";
	String codComEnte = "";
	String strDescComEnte = "";
	String strLocalitaEnte = "";
	String strIndirizzoEnte = "";

	String numAnno = null;
	String numMesi = null;
	String numOre = null;
	String numOreSpese = null;
	String strMotCessazione = "";
	String codTipoCorso = "";
	String cdnAmbitoDisciplinare = null;
	String strAmbitoDisciplinare = "";
	String strAzienda = "";
	String codComAzienda = "";
	String strDescComAzienda = "";
	String strLocalitaAzienda = "";
	String strIndirizzoAzienda = "";
	String strNotaCorso = "";
	String numOreStage = null;
	BigDecimal cdnUtIns = null;
	String dtmIns = "";
	BigDecimal cdnUtMod = null;
	String dtmMod = "";
	Testata operatoreInfo = null;

	//Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");

	String PRG_TAB_DA_ASSOCIARE = null;
	String COD_LST_TAB = "PR_COR";

	int _cdnFunz = Integer.parseInt((String) serviceRequest
			.getAttribute("CDNFUNZIONE"));
	String cdnFunzione = (String) serviceRequest
			.getAttribute("CDNFUNZIONE");
	if (rows != null && !rows.isEmpty()) {

	}
%>
<%
	String _page = (String) serviceRequest.getAttribute("PAGE");
	int _funzione = Integer.parseInt((String) serviceRequest
			.getAttribute("CDNFUNZIONE"));
	boolean nuovo = false;

	String apriDiv = (String) serviceRequest.getAttribute("APRIDIV");
	if (apriDiv == null) {
		apriDiv = "none";
	} else {
		apriDiv = "";
	}

	String url_nuovo = "AdapterHTTP?PAGE=ForProPage"
			+ "&CDNLAVORATORE=" + cdnLavoratore + "&CDNFUNZIONE="
			+ _funzione + "&APRIDIV=1";

	String SPRGCORSO = "";
	String cdngruppo = "";
	String datautorizzazione = "";
	String strautorizzazione = "";
	if (!nuovo) {
		// Sono in modalità "Dettaglio"
		Vector vectConoInfo = serviceResponse
				.getAttributeAsVector("M_COOP_DETTAGLIOFORPRO_DALLE_CACHE.ROWS.ROW");
		if ((vectConoInfo != null) && (vectConoInfo.size() > 0)) {

			SourceBean beanLastInsert = (SourceBean) vectConoInfo
					.get(0);

			//prgCorso           = (BigDecimal)beanLastInsert.getAttribute("PRGCORSO");
			prgCorso = StringUtils.getAttributeStrNotNull(
					beanLastInsert, "PRGCORSO");
			codCorso = StringUtils.getAttributeStrNotNull(
					beanLastInsert, "CODCORSO");
			codTipoCertificato = StringUtils.getAttributeStrNotNull(
					beanLastInsert, "CODTIPOCERTIFICATO");
			descCorso = StringUtils.getAttributeStrNotNull(
					beanLastInsert, "STRDESCDECOD");
			strDescrizione = StringUtils.getAttributeStrNotNull(
					beanLastInsert, "STRDESCRIZIONE");
			strEnte = StringUtils.getAttributeStrNotNull(
					beanLastInsert, "STRENTE");
			//numAnno         = (BigDecimal)beanLastInsert.getAttribute("NUMANNO");
			numAnno = StringUtils.getAttributeStrNotNull(
					beanLastInsert, "NUMANNO");
			flgCompletato = StringUtils.getAttributeStrNotNull(
					beanLastInsert, "FLGCOMPLETATO");
			flgStage = StringUtils.getAttributeStrNotNull(
					beanLastInsert, "FLGSTAGE");
			strContenuto = StringUtils.getAttributeStrNotNull(
					beanLastInsert, "STRCONTENUTO");
			codComEnte = StringUtils.getAttributeStrNotNull(
					beanLastInsert, "CODCOMENTE");
			strDescComEnte = StringUtils.getAttributeStrNotNull(
					beanLastInsert, "STRDESCCOMENTE");
			strLocalitaEnte = StringUtils.getAttributeStrNotNull(
					beanLastInsert, "STRLOCALITAENTE");
			strIndirizzoEnte = StringUtils.getAttributeStrNotNull(
					beanLastInsert, "STRINDIRIZZOENTE");
			//numAnno         = (BigDecimal)beanLastInsert.getAttribute("NUMANNO");
			numAnno = StringUtils.getAttributeStrNotNull(
					beanLastInsert, "NUMANNO");
			//numMesi         = (BigDecimal)beanLastInsert.getAttribute("NUMMESI");
			numMesi = StringUtils.getAttributeStrNotNull(
					beanLastInsert, "NUMMESI");
			//numOre          = (BigDecimal)beanLastInsert.getAttribute("NUMORE");
			numOre = StringUtils.getAttributeStrNotNull(beanLastInsert,
					"NUMORE");
			//numOreSpese     = (BigDecimal)beanLastInsert.getAttribute("NUMORESPESE");
			numOreSpese = StringUtils.getAttributeStrNotNull(
					beanLastInsert, "NUMORESPESE");
			strMotCessazione = StringUtils.getAttributeStrNotNull(
					beanLastInsert, "STRMOTCESSAZIONE");
			codTipoCorso = StringUtils.getAttributeStrNotNull(
					beanLastInsert, "CODTIPOCORSO");
			//cdnAmbitoDisciplinare=(BigDecimal)beanLastInsert.getAttribute("CDNAMBITODISCIPLINARE");
			cdnAmbitoDisciplinare = StringUtils.getAttributeStrNotNull(
					beanLastInsert, "CDNAMBITODISCIPLINARE");
			//		if (cdnAmbitoDisciplinare != null) {
			//		    strAmbitoDisciplinare = cdnAmbitoDisciplinare.toString();
			//		}
			strAzienda = StringUtils.getAttributeStrNotNull(
					beanLastInsert, "STRAZIENDA");
			codComAzienda = StringUtils.getAttributeStrNotNull(
					beanLastInsert, "CODCOMAZIENDA");
			strDescComAzienda = StringUtils.getAttributeStrNotNull(
					beanLastInsert, "STRDESCCOMAZIENDA");
			strLocalitaAzienda = StringUtils.getAttributeStrNotNull(
					beanLastInsert, "STRLOCALITAAZIENDA");
			strIndirizzoAzienda = StringUtils.getAttributeStrNotNull(
					beanLastInsert, "STRINDIRIZZOAZIENDA");
			strNotaCorso = StringUtils.getAttributeStrNotNull(
					beanLastInsert, "STRNOTACORSO");
			//		numOreStage     = (BigDecimal)beanLastInsert.getAttribute("NUMORESTAGE");
			numOreStage = StringUtils.getAttributeStrNotNull(
					beanLastInsert, "NUMORESTAGE");
			//cdnUtIns        = (BigDecimal)beanLastInsert.getAttribute("CDNUTINS");
			//dtmIns          = beanLastInsert.containsAttribute("DTMINS") ? beanLastInsert.getAttribute("DTMINS").toString() : "";
			//dtmMod          = beanLastInsert.containsAttribute("DTMMOD") ? beanLastInsert.getAttribute("DTMMOD").toString() : "";
			//cdnUtMod        = (BigDecimal)beanLastInsert.getAttribute("CDNUTMOD");		
			// --- NOTE: Gestione Patto
			PRG_TAB_DA_ASSOCIARE = prgCorso.toString();
			row = beanLastInsert;
			// ---
		}
	}
	//operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);

	String divStreamTop = StyleUtils.roundLayerTop(canModify);
	String divStreamBottom = StyleUtils.roundLayerBottom(canModify);
%>
<html>

<head>
<title>Formazione professionale</title>

<link rel="stylesheet" href="../../css/stiliCoop.css" type="text/css">
<link rel="stylesheet" type="text/css"
	href="../../css/listdetailCoop.css" />

<af:linkScript path="../../js/" />
<%@ include file="../global/Function_CommonRicercaComune.inc"%>
<SCRIPT language="JavaScript" src="../../js/layers.js"></SCRIPT>
<SCRIPT TYPE="text/javascript">
  
  var flagChanged = false;
  
  function Select(prg){
  	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;
  
      var s= "AdapterHTTP?PAGE=CoopConoscenzeFormazioneProPage";
      s += "&MODULE=M_LoadForPro";
      s += "&PRGCORSO=" + prg;
      s += "&CDNLAVORATORE=<%=cdnLavoratore%>";
      s += "&CDNFUNZIONE=<%=_funzione%>";
      s += "&APRIDIV=1";
      setWindowLocation(s);
  }
  
  
function chiudiLayer() {

  ok=true;  
  if (ok) {
     ChiudiDivLayer('divLayerDett');
  }
}

var showButtonImg = new Image();
var hideButtonImg = new Image();
showButtonImg.src="../../img/chiuso.gif";
hideButtonImg.src="../../img/aperto.gif"



function onOff(){	
  var div = document.getElementById("dett");
  var idImm = document.getElementById("imm1");
  if (div.style.display=="")  {	
    nascondi("dett");
    mostra  ("labelVisulizza");
    nascondi("labelNascondi");
    idImm.src = showButtonImg.src;
  } 
  else  {	
    mostra  ("dett");
    nascondi("labelVisulizza");
    mostra  ("labelNascondi");
    idImm.src = hideButtonImg.src
  }
}//onOff()

function mostra(id){ 
  var div = document.getElementById(id);
  div.style.display="";
}

function nascondi(id){ 
  var div = document.getElementById(id);
  div.style.display="none";
}

function onOff1(){	
  var div = document.getElementById("dett1");
  var idImm = document.getElementById("imm2");
  
  CorsoConStage_onClick();
  
  
  if (div.style.display=="")  {	
     nascondi("dett1");
     mostra  ("labelVisulizza1");
     nascondi("labelNascondi1");
     idImm.src = showButtonImg.src;
	 //nascondo gli eventuali dettagli dello stage
	 //document.Frm1.STRAZIENDA.value="";
     document.Frm1.STRAZIENDA.disabled=true;
     document.getElementById("str_azienda").style.display="none";

     //document.Frm1.strComAzienda.value="";
     document.Frm1.strComAzienda.disabled=true;
     document.getElementById("str_com_azienda").style.display="none";

     //document.Frm1.NUMORESTAGE.value="";      
     document.Frm1.NUMORESTAGE.disabled=true;

     //document.Frm1.STRLOCALITAAZIENDA.value="";
     document.Frm1.STRLOCALITAAZIENDA.disabled=true;
     document.getElementById("str_localita").style.display="none";

     //document.Frm1.STRINDIRIZZOAZIENDA.value="";
     document.Frm1.STRINDIRIZZOAZIENDA.disabled=true;
     document.getElementById("str_indirizzo").style.display="none";
  } 
  else   {	
    mostra  ("dett1");
    nascondi("labelVisulizza1");
    mostra  ("labelNascondi1");
    idImm.src = hideButtonImg.src
  }
}//onOff()
  function AbilitaCessazione_onClick() {	
    if (document.Frm1.FLGCOMPLETATO.value=="N"){
      document.Frm1.STRMOTCESSAZIONE.disabled=false;
      document.getElementById("mot_ces").style.display="";              
    }else{
      //document.Frm1.STRMOTCESSAZIONE.value="";
      document.Frm1.STRMOTCESSAZIONE.disabled=true;  
      document.getElementById("mot_ces").style.display="none";        
    }
  }
function AbilitaStage_onClick() {	
    if (document.Frm1.FLGSTAGE.value=="S") {
      document.Frm1.STRAZIENDA.disabled=false;
      document.getElementById("str_azienda").style.display="";

      document.Frm1.strComAzienda.disabled=false;
      document.getElementById("str_com_azienda").style.display="";

      document.Frm1.NUMORESTAGE.disabled=false;

      document.Frm1.STRLOCALITAAZIENDA.disabled=false;
      document.getElementById("str_localita").style.display="";

      document.Frm1.STRINDIRIZZOAZIENDA.disabled=false;
      document.getElementById("str_indirizzo").style.display="";
      var idImm = document.getElementById("imm2");
      idImm.src = showButtonImg.src;
    }else{
      //document.Frm1.STRAZIENDA.value="";
      document.Frm1.STRAZIENDA.disabled=true;
      document.getElementById("str_azienda").style.display="none";

      //document.Frm1.strComAzienda.value="";
      document.Frm1.strComAzienda.disabled=true;
      document.getElementById("str_com_azienda").style.display="none";

      //document.Frm1.NUMORESTAGE.value="";      
      document.Frm1.NUMORESTAGE.disabled=true;

      //document.Frm1.STRLOCALITAAZIENDA.value="";
      document.Frm1.STRLOCALITAAZIENDA.disabled=true;
      document.getElementById("str_localita").style.display="none";

      //document.Frm1.STRINDIRIZZOAZIENDA.value="";
      document.Frm1.STRINDIRIZZOAZIENDA.disabled=true;
      document.getElementById("str_indirizzo").style.display="none";
      var idImm = document.getElementById("imm2");
      idImm.src = hideButtonImg.src;
      //document.Frm1.CODCOMAZIENDA.value="";
      //document.Frm1.CODCOMAZIENDAHid.value="";
    }
    
  }
   function CorsoConStage_onClick() {
    AbilitaStage_onClick();   
  }
</SCRIPT>
<script language="Javascript" src="../../js/docAssocia.js"></script>
</head>

<body class="gestione"
	onload="AbilitaCessazione_onClick();CorsoConStage_onClick()">

<%@ include file="_testataLavoratore.inc"%>
<%@ include file="_linguetta.inc"%>

<script language="javascript">
    var flgInsert = <%=nuovo%>;
  </script>
<af:form method="POST" action="AdapterHTTP" name="Frm1">
	<input type="hidden" name="APRIDIV" value="1" />
	<p align="center">
	<center><font color="red"> <af:showErrors /> </font></center>
	<div align="center"><af:list
		moduleName="M_COOP_ListForPro_dalla_cache" skipNavigationButton="1"
		jsSelect="Select" /></div>
	</p>

	<!-- LAYER -->

	<div id="divLayerDett" name="divLayerDett" class="t_layerDett"
		style="position:absolute; width:80%; left:50; top:20px; z-index:6; display:<%=apriDiv%>;">
	<!-- Stondature ELEMENTO TOP --> <a name="aLayerIns"></a> <%
 	out.print(divStreamTop);
 %>
	<p><font color="red"> <af:showErrors /> </font></p>


	<table width="100%" cellpadding="0" cellspacing="0">
		<tr width="100%">
			<td width="16" height="16" class="azzurro_bianco"><img
				src="../../img/move_layer.gif" onClick="return false"
				onMouseDown="engager(event,'divLayerDett');return false"></td>
			<td height="16" class="azzurro_bianco">Formazione professionale
			</td>
			<td width="16" height="16" onClick="chiudiLayer()"
				class="azzurro_bianco"><img src="../../img/chiudi_layer.gif"
				alt="Chiudi"></td>
		</tr>
	</table>

	<table width="100%" cellpadding="0" cellspacing="0">
		<%
			if (!nuovo) {
		%>
		<tr>
			<td class="etichetta">Codice</td>
			<td class="campo"><af:textBox name="CODCORSO"
				title="Codice Corso" classNameBase="input" value="<%=codCorso%>"
				size="14" readonly="true" maxlength="13" /></td>
		</tr>
		<tr>
			<td class="etichetta">Corso</td>
			<td class="campo"><af:textArea cols="45" rows="5"
				name="DESCCORSO" classNameBase="textArea"
				readonly="<%=String.valueOf(readOnlyStr)%>" maxlength="300"
				value="<%=descCorso%>" /> <input type="hidden" name="descCorsoHid"
				value="<%=descCorso%>" /></td>
		</tr>
		<tr>
			<td class="etichetta">Anno</td>
			<td class="campo"><af:textBox name="NUMANNO" title="Anno"
				classNameBase="input" value="<%=Utils.notNull(numAnno)%>" size="4"
				type="integer" readonly="<%=String.valueOf(readOnlyStr)%>" required="true"
				maxlength="5" /> <input type="hidden" name="numAnnoHid"
				value="<%=Utils.notNull(numAnno)%>" /></td>
		</tr>
		<tr>
			<td class="etichetta">Descrizione</td>
			<td class="campo"><af:textBox name="STRDESCRIZIONE"
				classNameBase="input" title="Descrizione"
				value="<%=strDescrizione%>" size="50" maxlength="100"
				readonly="<%=String.valueOf(readOnlyStr)%>" /></td>
		</tr>
		<tr>
			<td class="etichetta">Contenuto</td>
			<td class="campo"><af:textBox name="STRCONTENUTO"
				classNameBase="input" title="Contenuto" value="<%=strContenuto%>"
				size="50" maxlength="100"
				readonly="<%=String.valueOf(readOnlyStr)%>" /></td>
		</tr>
		<tr>
			<td colspan="2">
			<HR>
			</td>
		</tr>

		<!-- Gestione Linguetta a scomparsa -->
		<tr>
			<td colspan="2">
			<table cellpadding="1" cellspacing="0" width="100%" border="0">
				<tr>
					<td width="40%" align="right"><b>Ente</b></td>
					<td><a href="#" onClick="onOff()" style="CURSOR: hand;"> <img
						id="imm1" alt="mostra/nascondi" src="../../img/chiuso.gif"
						border="0"> </a></td>
					<td width="70%">
					<div id="labelVisulizza" style="display: ">(visualizza)</div>
					<div id="labelNascondi" style="display: none">(nascondi)</div>
					</td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
		<tr>
			<td colspan="2">
			<div id="dett" style="display: none">
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td class="etichetta">Ente</td>
					<td class="campo"><af:textBox name="STRENTE"
						classNameBase="input" title="Ente" value="<%=strEnte%>" size="50"
						maxlength="100" readonly="<%=String.valueOf(readOnlyStr)%>" /></td>
				</tr>
				<tr>
					<td class="etichetta">Comune</td>
					<td class="campo"><af:textBox name="CODCOMENTE"
						value="<%=codComEnte%>" size="4" classNameBase="input"
						maxlength="4" readonly="<%=String.valueOf(readOnlyStr)%>"
						disabled="false" />&nbsp; <INPUT type="hidden"
						name="CODCOMENTEHid" value="<%=codComEnte%>"> <af:textBox
						name="strComEnte" value="<%=strDescComEnte%>" size="30"
						maxlength="50" classNameBase="input"
						readonly="<%=String.valueOf(readOnlyStr)%>" /> <INPUT
						type="hidden" name="strComEnteHid" value="">&nbsp;</td>
				</tr>
				<tr>
					<td class="etichetta">Località</td>
					<td class="campo"><af:textBox name="STRLOCALITAENTE"
						classNameBase="input" title="Località Ente"
						value="<%=strLocalitaEnte%>" size="50" maxlength="50"
						readonly="<%=String.valueOf(readOnlyStr)%>" /></td>
				</tr>
				<tr>
					<td class="etichetta">Indirizzo</td>
					<td class="campo"><af:textBox name="STRINDIRIZZOENTE"
						classNameBase="input" title="Indirizzo Ente"
						value="<%=strIndirizzoEnte%>" size="50" maxlength="60"
						readonly="<%=String.valueOf(readOnlyStr)%>" /></td>
				</tr>
			</table>
			</div>
			</td>
		</tr>
		<!-- Fine gestione linguetta a scoparsa -->
		<tr>
			<td colspan="2">
			<HR>
			</td>
		</tr>
		<tr>
			<td class="etichetta">Mesi</td>
			<td class="campo"><af:textBox name="NUMMESI" type="integer"
				classNameBase="input" title="Mesi"
				value="<%=Utils.notNull(numMesi)%>"
				readonly="<%=String.valueOf(readOnlyStr)%>" /></td>
		</tr>
		<tr>
			<td class="etichetta">Ore</td>
			<td class="campo"><af:textBox name="NUMORE" type="integer"
				classNameBase="input" title="Ore" value="<%=Utils.notNull(numOre)%>"
				size="4" readonly="<%=String.valueOf(readOnlyStr)%>" /> di cui
			effettive <af:textBox name="NUMORESPESE" type="integer"
				classNameBase="input" title="Ore effettive"
				value="<%=Utils.notNull(numOreSpese)%>" size="4"
				readonly="<%=String.valueOf(readOnlyStr)%>" /></td>
		</tr>
		<tr>
			<td class="etichetta">Completato</td>
			<td class="campo"><af:comboBox title="Completato"
				name="FLGCOMPLETATO" classNameBase="input"
				disabled="<%=String.valueOf(readOnlyStr)%>">

				<option value="" <%if ("".equals(flgCompletato)) {%> SELECTED="true"
					<%}%>></option>
				<option value="S" <%if ("S".equals(flgCompletato)) {%>
					SELECTED="true" <%}%>>Si</option>
				<option value="N" <%if ("N".equals(flgCompletato)) {%>
					SELECTED="true" <%}%>>No</option>
			</af:comboBox></td>
		</tr>
		<tr id="mot_ces" name="mot_ces" style="display: none">
			<td class="etichetta">Motivo cessazione</td>
			<td class="campo"><af:textBox name="STRMOTCESSAZIONE"
				classNameBase="input" title="Ore Spese"
				value="<%=strMotCessazione%>" size="50" maxlength="100"
				readonly="<%=String.valueOf(readOnlyStr)%>" /></td>
		</tr>
		<tr>
			<td class="etichetta">Tipo Certificazione</td>
			<td class="campo"><af:comboBox name="CODTIPOCERTIFICATO"
				title="Tipo Certificazione" classNameBase="input"
				moduleName="M_ListForProTipoCorso"
				disabled="<%=String.valueOf(readOnlyStr)%>" addBlank="true"
				blankValue="" selectedValue="<%=codTipoCertificato.toString()%>" />

			</td>
		</tr>
		<tr>
			<td class="etichetta">Ambito disciplinare</td>
			<td class="campo"><af:comboBox name="CDNAMBITODISCIPLINARE"
				title="Codice ambito disciplinare" classNameBase="input"
				moduleName="M_ListForProAmbDiscip"
				disabled="<%=String.valueOf(readOnlyStr)%>" addBlank="true"
				blankValue="" selectedValue="<%=strAmbitoDisciplinare%>" /></td>
		</tr>
		<tr>
			<td colspan="2">
			<HR>
			</td>
		</tr>

		<tr>
			<td colspan="2">
			<table cellpadding="1" cellspacing="0" width="100%" border="0">
				<tr>
					<td width="40%" align="right"><b>Stage</b></td>
					<td><a href="#" onClick="onOff1()" style="CURSOR: hand;">
					<img id="imm2" alt="mostra1/nascondi1" src="../../img/chiuso.gif"
						border="0"> </a></td>
					<td width="70%">
					<div id="labelVisulizza1" style="display: ">(visualizza)</div>
					<div id="labelNascondi1" style="display: none">(nascondi)</div>
					</td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td colspan="2">
			<div id="dett1" style="display: none">
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td class="etichetta">Corso con Stage</td>
					<td class="campo"><af:comboBox title="Conoscenza certificata"
						name="FLGSTAGE" classNameBase="input"
						disabled="<%=String.valueOf(readOnlyStr)%>">
						<option value="" <%if ("".equals(flgStage)) {%> SELECTED="true"
							<%}%>></option>
						<option value="S" <%if ("S".equals(flgStage)) {%> SELECTED="true"
							<%}%>>Si</option>
						<option value="N" <%if ("N".equals(flgStage)) {%> SELECTED="true"
							<%}%>>No</option>
					</af:comboBox> di ore <af:textBox name="NUMORESTAGE" classNameBase="input"
						title="Numero ore di stage"
						value="<%=Utils.notNull(numOreStage)%>" size="4" maxlength="100"
						readonly="<%=String.valueOf(readOnlyStr)%>" classNameBase="input" />
					</td>
				</tr>
			</table>
			</div>
			</td>
		</tr>
		<tr id="str_azienda" name="str_azienda" style="display: none">
			<td class="etichetta">Azienda</td>
			<td class="campo"><af:textBox name="STRAZIENDA"
				title="Ragione Sociale dell'Azienda di Stage"
				value="<%=strAzienda%>" size="50" maxlength="100"
				readonly="<%=String.valueOf(readOnlyStr)%>" classNameBase="input" />
			</td>
		</tr>
		<tr id="str_com_azienda" name="str_com_azienda" style="display: none">
			<td class="etichetta">Comune</td>
			<td class="campo"><af:textBox name="CODCOMAZIENDA"
				value="<%=codComAzienda%>" size="4" maxlength="4" readonly="true"
				classNameBase="input" />&nbsp; <INPUT type="hidden"
				id="CODCOMAZIENDAHid" name="CODCOMAZIENDAHid"
				value="<%=codComAzienda%>"> <af:textBox name="strComAzienda"
				value="<%=strDescComAzienda%>" size="30" maxlength="50"
				readonly="<%=String.valueOf(readOnlyStr)%>" classNameBase="input" />
			<INPUT type="hidden" id="strComAziendaHid" name="strComAziendaHid"
				value="">&nbsp;</td>
		</tr>
		<tr id="str_localita" name="str_localita" style="display: none">
			<td class="etichetta">Località</td>
			<td class="campo"><af:textBox name="STRLOCALITAAZIENDA"
				title="Località dell'Azienda in Stage"
				value="<%=strLocalitaAzienda%>" size="50" maxlength="50"
				readonly="<%=String.valueOf(readOnlyStr)%>" classNameBase="input" />
			</td>
		</tr>
		<tr id="str_indirizzo" name="str_indirizzo" style="display: none">
			<td class="etichetta">Indirizzo</td>
			<td class="campo"><af:textBox name="STRINDIRIZZOAZIENDA"
				title="Indirizzo dell'Azienda in Stage"
				value="<%=strIndirizzoAzienda%>" size="50" maxlength="60"
				readonly="<%=String.valueOf(readOnlyStr)%>" classNameBase="input" />
			</td>
		</tr>
		<tr>
			<td colspan="2"><br>
			</td>
		</tr>
		<tr>
			<td align="center" colspan="2"><input type="button"
				value="Chiudi" class="pulsanti" onclick="chiudiLayer()"></td>
		</tr>
		<%
			} // (!NUOVO)
		%>
	</table>
	<!-- Stondature ELEMENTO BOTTOM --> <%
 	out.print(divStreamBottom);
 %>
	</div>
	<!-- LAYER - END -->
</af:form>
</body>

</html>