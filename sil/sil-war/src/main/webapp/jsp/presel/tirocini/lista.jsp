
<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="/jsp/global/noCaching.inc"%>
<%@ page
	extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage"%>
<%@ include file="/jsp/global/getCommonObjects.inc"%>

<%@ page
	import="it.eng.afExt.utils.LogUtils.*,com.engiweb.framework.base.*,com.engiweb.framework.configuration.ConfigSingleton,it.eng.sil.util.*,it.eng.afExt.utils.StringUtils,it.eng.sil.security.ProfileDataFilter,java.lang.*,java.text.*,java.util.*,java.math.*,it.eng.sil.security.*,com.engiweb.framework.dbaccess.sql.TimestampDecorator"%>

<%@ include file="/jsp/global/Function_CommonRicercaComune.inc"%>

<%
	String cdnLavoratore = (String) serviceRequest
			.getAttribute("CDNLAVORATORE");
	String cdnLavoratoreEncrypt = EncryptDecryptUtils.encrypt(cdnLavoratore);
	String codCpiUt =  user.getCodRif();
	String _current_page = (String) serviceRequest.getAttribute("PAGE");
	Object modulo = serviceRequest.getAttribute("MODULE");

	//Dati per l'azienda
	String prgAzienda = StringUtils.getAttributeStrNotNull(
			serviceRequest, "PRGAZIENDA");
	String prgUnita = StringUtils.getAttributeStrNotNull(
			serviceRequest, "PRGUNITA");
	String ragioneSociale = "";
	String pIva = "";
	String codFiscaleAzienda = "";
	String IndirizzoAzienda = "";
	String comuneAzienda = "";
	String descrTipoAz = "";
	String codTipoAz = "";
	String codnatGiurAz = "";
	String strFlgCfOk = "";
	String strFlgDatiOk = "";
	boolean infoDatiNOLav = false;
	boolean infoDatiNOAzi = false;

	//Dati per il sogetto promotore
	String prgAziendaSP = "";//StringUtils.getAttributeStrNotNull(serviceRequest, "PRGAZIENDA");
	String prgUnitaSP = "";//StringUtils.getAttributeStrNotNull(serviceRequest, "PRGUNITA");
	String ragioneSocialeSP = "";
	String pIvaSP = "";
	String codFiscaleAziendaSP = "";
	String IndirizzoAziendaSP = "";
	String comuneAziendaSP = "";
	String descrTipoAzSP = "";
	String codTipoAzSP = "";
	String codnatGiurAzSP = "";
	String strFlgCfOkSP = "";
	String strFlgDatiOkSP = "";
	String cfAziendaSP = "";

	String prgTirocinio = "";
	String codiceAzione = "";
	String prgcolloquio = "";
	String codTipoTirocinio = "";
	String strDescrizione = "";
	String descrizioneTipoTirocinio = "";
	String cfAzienda = "";
	String dataInizioTirocinio = "";
	String dataFineTirocinio = "";
	String strNoteProgetto = "";
	String numDurataOre = "";
	String strSedePreferenza = "";
	String strCompetenzaAcquisizione = "";
	String STRAREAPROF = "";
	
	String flgDocumentoValidazione = "";
	String strNota = "";
	String dtmIns = "";
	String cdnUtIns = "";
	String dtmMod = "";
	String cdnUtMod = "";
	BigDecimal numKLoTirocinio = null;
	Format formatter = new SimpleDateFormat("dd/MM/yyyy");
	Date dtmModificaAttuale = GregorianCalendar.getInstance().getTime();

	int _funzione = Integer.parseInt((String) serviceRequest
			.getAttribute("CDNFUNZIONE"));
	InfCorrentiLav infCorrentiLav = new InfCorrentiLav(RequestContainer
			.getRequestContainer().getSessionContainer(),
			cdnLavoratore, user);
	Testata operatoreInfo = null;

	ProfileDataFilter filter = new ProfileDataFilter(user,
			_current_page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	PageAttribs attributi = new PageAttribs(user, _current_page);

	boolean canModify = true;
	boolean canDelete = true;
	boolean readOnlyStr = true;

	boolean canView = filter.canViewLavoratore();
	if (!canView) {
		response.sendRedirect(request.getContextPath()
				+ "/servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	} else {
		if (!canModify) {
			canModify = false;
		} else {
			canModify = filter.canEditLavoratore();
		}

		if (!canDelete) {
			canDelete = false;
		} else {
			canDelete = filter.canEditLavoratore();
		}
	}
	readOnlyStr = !canModify;

	//Se nella request il parametro MODULE e' uguale a M_GetTirocinio devo visualizzare il dettaglio del tirocinio
	boolean nuovo = true;
	String apriDiv = "none";

	//se c'e' un errore lascio il layer aperto
	if (responseContainer.getErrorHandler().getErrors().iterator()
			.hasNext()) {
		apriDiv = "";
	}
	//sto inserendo un nuovo tirocinio
	if ("M_NuovoTirocinio".equals(modulo)) {
		apriDiv = "";
		nuovo = true;
	}
	//sto visualizzando il dettaglio di un tirocinio
	if ("M_GetTirocinio".equals(modulo)) {
		apriDiv = "";
		nuovo = false;

		SourceBean rowTirocinio = (SourceBean) serviceResponse
				.getAttribute("M_GetTirocinio.ROWS.ROW");

		prgTirocinio = ((BigDecimal) rowTirocinio
				.getAttribute("PRGTIROCINIO")).toString();
		Object codiceAzioneObj = rowTirocinio
				.getAttribute("PRGPERCORSO");
		if (codiceAzioneObj != null) {
			codiceAzione = ((BigDecimal) rowTirocinio
					.getAttribute("PRGPERCORSO")).toString();
		}

		Object prgcolloquioObj = rowTirocinio
				.getAttribute("prgcolloquio");
		if (prgcolloquioObj != null) {
			prgcolloquio = ((BigDecimal) rowTirocinio
					.getAttribute("prgcolloquio")).toString();
		}
		//combox chiave doppia --- n c'era bisogno, ma....
		codiceAzione = prgcolloquio + "-" + codiceAzione;
		codTipoTirocinio = StringUtils.getAttributeStrNotNull(
				rowTirocinio, "CODTIPOTIROCINIO");
		strDescrizione = StringUtils.getAttributeStrNotNull(
				rowTirocinio, "STRDESCRIZIONE");
		descrizioneTipoTirocinio = StringUtils.getAttributeStrNotNull(
				rowTirocinio, "DESCRIZIONE_TIPO_TIROCINIO");
		//Azienda
		ragioneSociale = StringUtils.getAttributeStrNotNull(
				rowTirocinio, "RAG_SOC_AZIENDA");
		cfAzienda = StringUtils.getAttributeStrNotNull(rowTirocinio,
				"CF_AZIENDA");
		pIva = StringUtils
				.getAttributeStrNotNull(rowTirocinio, "P_IVA");
		strFlgDatiOk = StringUtils.getAttributeStrNotNull(rowTirocinio,
				"FLGDATIOK");
		descrTipoAz = StringUtils.getAttributeStrNotNull(rowTirocinio,
				"STRTIPOAZIENDA");
		codTipoAz = StringUtils.getAttributeStrNotNull(rowTirocinio,
				"CODTIPOAZIENDA");
		codFiscaleAzienda = StringUtils.getAttributeStrNotNull(
				rowTirocinio, "CF_AZIENDA");
		IndirizzoAzienda = StringUtils.getAttributeStrNotNull(
				rowTirocinio, "INDIRIZZO");
		comuneAzienda = StringUtils.getAttributeStrNotNull(
				rowTirocinio, "COMUNE");

		//
		dataInizioTirocinio = StringUtils.getAttributeStrNotNull(
				rowTirocinio, "DATINIZIOTIROCINIO");
		dataFineTirocinio = StringUtils.getAttributeStrNotNull(
				rowTirocinio, "DATFINETIROCINIO");
		strNoteProgetto = StringUtils.getAttributeStrNotNull(
				rowTirocinio, "STRNOTEPROGETTO");
		Object numDurataOreObj = rowTirocinio
				.getAttribute("NUMDURATAORE");
		if (numDurataOreObj != null) {
			numDurataOre = ((BigDecimal) numDurataOreObj).toString();
		}
		strSedePreferenza = StringUtils.getAttributeStrNotNull(
				rowTirocinio, "STRSEDEPREFERENZA");
		strCompetenzaAcquisizione = StringUtils.getAttributeStrNotNull(
				rowTirocinio, "STRCOMPETENZAACQUISIZIONE");

		STRAREAPROF = StringUtils.getAttributeStrNotNull(
				rowTirocinio, "STRAREAPROF");
		
		
		flgDocumentoValidazione = StringUtils.getAttributeStrNotNull(
				rowTirocinio, "FLGDOCUMENTOVALIDAZIONE");
		strNota = StringUtils.getAttributeStrNotNull(rowTirocinio,
				"STRNOTA");
		Object prgAziendaObj = rowTirocinio.getAttribute("PRGAZIENDA");
		if (prgAziendaObj != null) {
			prgAzienda = ((BigDecimal) prgAziendaObj).toString();
		}
		Object prgUnitaObj = rowTirocinio.getAttribute("PRGUNITA");
		if (prgUnitaObj != null) {
			prgUnita = ((BigDecimal) prgUnitaObj).toString();
		}
		TimestampDecorator dtmInsDate = (TimestampDecorator) rowTirocinio
				.getAttribute("DTMINS");
		dtmIns = formatter.format(dtmInsDate);
		cdnUtIns = ((BigDecimal) rowTirocinio.getAttribute("CDNUTINS"))
				.toString();
		TimestampDecorator dtmModDate = (TimestampDecorator) rowTirocinio
				.getAttribute("DTMMOD");
		dtmMod = formatter.format(dtmModDate);
		cdnUtMod = ((BigDecimal) rowTirocinio.getAttribute("CDNUTMOD"))
				.toString();
		numKLoTirocinio = (BigDecimal) rowTirocinio
				.getAttribute("NUMKLOTIROCINIO");
		//Soggetto Promotore
		Object prgAziendaSPObj = rowTirocinio
				.getAttribute("PRGAZIENDASOGGPROM");
		if (prgAziendaSPObj != null) {
			prgAziendaSP = ((BigDecimal) prgAziendaSPObj).toString();
		}
		Object prgUnitaSPObj = rowTirocinio
				.getAttribute("PRGUNITASOGGPROM");
		if (prgUnitaSPObj != null) {
			prgUnitaSP = ((BigDecimal) prgUnitaSPObj).toString();
		}
		ragioneSocialeSP = StringUtils.getAttributeStrNotNull(
				rowTirocinio, "RAG_SOC_SoggProm");
		cfAziendaSP = StringUtils.getAttributeStrNotNull(rowTirocinio,
				"cf_SoggProm");
		pIvaSP = StringUtils.getAttributeStrNotNull(rowTirocinio,
				"p_iva_SoggProm");
		strFlgDatiOkSP = StringUtils.getAttributeStrNotNull(
				rowTirocinio, "flgdati_ok_SoggProm");
		descrTipoAzSP = StringUtils.getAttributeStrNotNull(
				rowTirocinio, "strtipoaziendaSoggProm");
		codTipoAzSP = StringUtils.getAttributeStrNotNull(rowTirocinio,
				"codtipoaziendaSoggProm");
		codFiscaleAziendaSP = StringUtils.getAttributeStrNotNull(
				rowTirocinio, "cf_SoggProm");
		IndirizzoAziendaSP = StringUtils.getAttributeStrNotNull(
				rowTirocinio, "indirizzoSoggProm");
		comuneAziendaSP = StringUtils.getAttributeStrNotNull(
				rowTirocinio, "comuneSoggProm");

		operatoreInfo = new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
	}
%>

<html>

<head>
<title>Tirocini</title>

<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/stili.css" type="text/css">
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/css/listdetail.css" />
<SCRIPT language="JavaScript"
	src="<%=request.getContextPath()%>/js/script_comuni.js"></SCRIPT>
<SCRIPT language="JavaScript"
	src="<%=request.getContextPath()%>/js/FormCheck.js"></SCRIPT>
<SCRIPT language="JavaScript"
	src="<%=request.getContextPath()%>/js/customTL.js"></SCRIPT>
<SCRIPT language="JavaScript"
	src="<%=request.getContextPath()%>/js/calendar.js"></SCRIPT>
<SCRIPT language="JavaScript"
	src="<%=request.getContextPath()%>/js/layers.js"></SCRIPT>



<script language="Javascript" src="../../js/docAssocia.js"></script>
<SCRIPT TYPE="text/javascript">
        // --- NOTE: Gestione Patto
        <%@ include file="/jsp/patto/_sezioneDinamica_script.inc"%>
        function getFormObj() {return document.Frm1;}
        // ---
    
        var flagChanged = false;
        
        function DettaglioTirocinio(prgTirocinio) {
            var s= "AdapterHTTP?PAGE=ConoscenzeTirociniPage";
            s += "&MODULE=M_GetTirocinio";
            s += "&PRGTIROCINIO=" + prgTirocinio;
            s += "&CDNLAVORATORE=<%=cdnLavoratore%>";
            s += "&CDNFUNZIONE=<%=_funzione%>";
        
            setWindowLocation(s);
        }
    
        function DeleteTirocinio(prgTirocinio) {
            var s="Sicuri di voler rimuovere il tirocinio?";
            
            if ( confirm(s) ) {
                   var s= "AdapterHTTP?PAGE=ConoscenzeTirociniPage";
                   s += "&MODULE=M_DelTirocinio";
                   s += "&PRGTIROCINIO=" + prgTirocinio;
                   s += "&CDNLAVORATORE=<%=cdnLavoratore%>";
                   s += "&CDNFUNZIONE=<%=_funzione%>";
         
                   setWindowLocation(s);
            }
        }

        function fieldChanged() {
            // DEBUG: Scommentare per vedere "field changed !" ad ogni cambiamento
            //alert("field changed !")  
            
            // NOTE: field-check solo se canModify 
            <%if (canModify) {%> 
            flagChanged = true;
            <%}%> 
        }

        function apriMascheraInserimento(nomeDiv) {
               var collDiv = document.getElementsByName(nomeDiv);
               var objDiv = collDiv.item(0);
               objDiv.style.display = "";
        }
       <%attributi.showHyperLinks(out, requestContainer, responseContainer,
					"cdnLavoratore=" + cdnLavoratore);%>
                        window.top.menu.caricaMenuLav(<%=_funzione%>,<%=cdnLavoratore%>);
       var flgInsert = true;
    </script>
<%@ include file="/jsp/amministrazione/MovimentiRicercaSoggetto.inc"%>
<%@ include file="/jsp/amministrazione/MovimentiSezioniATendina.inc"%>
<%@ include file="/jsp/amministrazione/Common_Function_Mov.inc"%>
<%
	String queryString = null;
%>
<%@ include file="/jsp/documenti/_apriGestioneDoc.inc"%>
<script type="text/javascript">
    
    //Variabili per la memorizzazione dell'azienda e lavoratore corrente per le GET
    var prgAzienda = '<%=prgAzienda%>';
    var prgUnita = '<%=prgUnita%>';
    var cdnLavoratore = '<%=cdnLavoratore%>';




    

    function aggiornaAzienda(){
        document.Frm1.ragioneSociale.value = opened.dati.ragioneSociale;
        document.Frm1.pIva.value = opened.dati.partitaIva;
        document.Frm1.codFiscaleAzienda.value = opened.dati.codiceFiscaleAzienda;
        document.Frm1.IndirizzoAziendaSP.value = opened.dati.strIndirizzoAzienda + " (" + opened.dati.comuneAzienda + ")";
        document.Frm1.PRGAZIENDA.value = opened.dati.prgAzienda;
        document.Frm1.PRGUNITA.value = opened.dati.prgUnita;
        document.Frm1.codTipoAz.value = opened.dati.codTipoAz;
        document.Frm1.descrTipoAz.value = opened.dati.descrTipoAz;
        document.Frm1.FLGDATIOK.value = opened.dati.FLGDATIOK;
        //prgAzienda = opened.dati.prgAzienda;
        //prgUnita = opened.dati.prgUnita;
        if ( document.Frm1.FLGDATIOK.value == "S" ) {
            document.Frm1.FLGDATIOK.value = "Si";
        } else if ( document.Frm1.FLGDATIOK.value == "N" ) {
            document.Frm1.FLGDATIOK.value = "No";
        }
        document.Frm1.CODNATGIURIDICA.value = opened.dati.codNatGiurAz;
        opened.close();
        var imgV = document.getElementById("tendinaAzienda");
        cambiaLavMC("aziendaSez","inline");
        imgV.src=imgAperta;
    }

    function aggiornaAziendaDettaglio() {
        //document.Frm1.ragioneSociale = '<%=ragioneSociale%>';
        //document.Frm1.pIva.value = '<%=pIva%>';
        //document.Frm1.codFiscaleAzienda.value = '<%=codFiscaleAzienda%>';        
        //document.Frm1.IndirizzoAzienda.value = '<%=IndirizzoAzienda%>' + " (" + '<%=comuneAzienda%>' + ")";
        //document.Frm1.prgAzienda.value = '<%=prgAzienda%>';
        //document.Frm1.prgUnita.value = '<%=prgUnita%>';
        //document.Frm1.codTipoAz.value = '<%=codTipoAz%>';
        //document.Frm1.descrTipoAz.value = '<%=descrTipoAz%>';
        //document.Frm1.FLGDATIOK.value = '<%=strFlgDatiOk%>';
        //document.Frm1.CODNATGIURIDICA.value = '<%=codnatGiurAz%>';
        
        if ( document.Frm1.FLGDATIOK.value == "S" ) {
            document.Frm1.FLGDATIOK.value = "Si";
        } else if ( document.Frm1.FLGDATIOK.value == "N" ) {
            document.Frm1.FLGDATIOK.value = "No";
        }
        
        var imgV = document.getElementById("tendinaAzienda");
        imgV.src=imgAperta;
        //cambiaLavMC("aziendaSez","inline");
        var aziendaSez = document.getElementById("aziendaSez");
        aziendaSez.style.display = "inline";
    }

    //Soggetto Promotore
    
    var openedSP;
    
    function apriSelezionaSoggettoSP(soggetto, funzionediaggiornamento, prgAzienda, prgUnita, cdnLavoratore) {    	
        var f = "AdapterHTTP?PAGE=MovimentiSelezionaSoggettoPage&MOV_SOGG=" + soggetto + "&AGG_FUNZ=" + funzionediaggiornamento ;        
        if (prgAzienda != '' && (typeof prgAzienda) != "undefined") {f = f + "&PRGAZ=" + prgAzienda;}
        if (prgUnita != '' && (typeof prgUnita) != "undefined") {f = f + "&PRGUAZ=" + prgUnita;}
        if (cdnLavoratore != '' && (typeof cdnLavoratore) != "undefined") {f = f + "&CDNLAV=" + cdnLavoratore;}
        var t = "_blank";         
        var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=750,height=650,top=30,left=180";
        openedSP = window.open(f, t, feat);
    }
    
    function aggiornaSoggetoPromotore(){
    	
        document.Frm1.ragioneSocialeSP.value = openedSP.dati.ragioneSociale;
        document.Frm1.pIvaSP.value = openedSP.dati.partitaIva;
        document.Frm1.cfAziendaSP.value = openedSP.dati.codiceFiscaleAzienda;        
        document.Frm1.IndirizzoAziendaSP.value = openedSP.dati.strIndirizzoAzienda + " (" + openedSP.dati.comuneAzienda + ")";
        
        document.Frm1.PRGAZIENDASOGGPROM.value = openedSP.dati.prgAzienda;
        document.Frm1.PRGUNITASOGGPROM.value = openedSP.dati.prgUnita;
        
        document.Frm1.codTipoAzSP.value = openedSP.dati.codTipoAz;
        document.Frm1.descrTipoAzSP.value = openedSP.dati.descrTipoAz;
        document.Frm1.strFlgDatiOkSP.value = openedSP.dati.FLGDATIOK;
        
        //prgAzienda = openedSP.dati.prgAzienda;
        //prgUnita = openedSP.dati.prgUnita;
        if ( document.Frm1.strFlgDatiOkSP.value == "S" ) {
            document.Frm1.strFlgDatiOkSP.value = "Si";
        } else if ( document.Frm1.strFlgDatiOkSP.value == "N" ) {
            document.Frm1.strFlgDatiOkSP.value = "No";
        }
        
        document.Frm1.codnatGiurAzSP.value = openedSP.dati.codNatGiurAz;
        
        openedSP.close();
        var imgVSP = document.getElementById("tendinaAziendaSP");
        cambiaLavMC("aziendaSezSP","inline");
        imgVSP.src=imgAperta;
    }


    function azzeraSoggettoPromotore(){
        document.Frm1.ragioneSocialeSP.value = "";
        document.Frm1.pIvaSP.value = "";
        document.Frm1.cfAziendaSP.value = "";
        if (document.Frm1.IndirizzoAziendaSP != null) {
        	document.Frm1.IndirizzoAziendaSP.value = "";
        }	
        document.Frm1.PRGAZIENDASOGGPROM.value = "";
        document.Frm1.PRGUNITASOGGPROM.value = "";
        if (document.Frm1.codTipoAzSP != null) {
        	document.Frm1.codTipoAzSP.value = "";
        }
        if (document.Frm1.codTipoAzienda != null) {
        	document.Frm1.codTipoAzienda.value = "";
        }
        if (document.Frm1.descrTipoAz != null) {
        	document.Frm1.descrTipoAz.value = "";
        }
        document.Frm1.strFlgDatiOkSP.value = "";
        if (document.Frm1.codnatGiurAzSP != null) {
        	document.Frm1.codnatGiurAzSP.value = "";
        }
        var imgVSP = document.getElementById("tendinaAzienda");
        cambiaLavMC("aziendaSezSP","none");
        imgVSP.src=imgChiusa;
        imgVSP.alt = "Apri";
    }


    function aggiornaAziendaDettaglioSP() {
        
        if ( document.Frm1.strFlgDatiOkSP.value == "S" ) {
            document.Frm1.strFlgDatiOkSP.value = "Si";
        } else if ( document.Frm1.strFlgDatiOkSP.value == "N" ) {
            document.Frm1.strFlgDatiOkSP.value = "No";
        }
        
        var imgVSP = document.getElementById("tendinaAziendaSP");
        imgVSP.src=imgAperta;        
        var aziendaSezSP = document.getElementById("aziendaSezSP");
        aziendaSezSP.style.display = "inline";
    }

    function nuovoTirocinio() {
        if (<%=nuovo%>) {
            apriMascheraInserimento('divLayerDett');
            document.location='#aLayerIns';
        } else {
            var s= "AdapterHTTP?PAGE=ConoscenzeTirociniPage";
            s += "&MODULE=M_NuovoTirocinio";
            s += "&CDNLAVORATORE=<%=cdnLavoratore%>";
            s += "&CDNFUNZIONE=<%=_funzione%>";
        
            setWindowLocation(s);
        }
    }
</script>
</head>

<body class="gestione" onload="rinfresca();">
	<%
		Linguette l = new Linguette(user, _funzione, _current_page,
				new BigDecimal(cdnLavoratore));
		infCorrentiLav.show(out);
		l.show(out);
	%>

	<af:showMessages prefix="M_DelTirocinio" />
	<af:showMessages prefix="M_UpdateTirocinio" />
	<af:showMessages prefix="M_InsertTirocinio" />

	<p align="center">
		<af:list moduleName="M_GetTirociniLavoratore" skipNavigationButton="1"
			canDelete="<%=canDelete ?\"1\" :\"0\"%>"
			canInsert="<%=canModify ?\"1\" :\"0\"%>"
			jsSelect="DettaglioTirocinio" jsDelete="DeleteTirocinio" />
	</p>
	<%
		if (canModify) {
	%>
	<p align="center">
		<input type="button" class="pulsanti" onClick="nuovoTirocinio()"
			value="Nuovo Tirocinio" />
	</p>
	<%
		}
	%>

	<!-- LAYER -->
	<%
		String divStreamTop = StyleUtils.roundLayerTop(canModify);
		String divStreamBottom = StyleUtils.roundLayerBottom(canModify);
	%>
	<div id="divLayerDett" name="divLayerDett" class="t_layerDett"
		style="position:absolute; width:80%; left:50; top:100px; z-index:6; display:<%=apriDiv%>;">
		<!-- Stondature ELEMENTO TOP -->
		<a name="aLayerIns"></a>
		<%
			out.print(divStreamTop);
		%>

		<table width="100%">
			<tr>
				<td width="16" height="16" class="azzurro_bianco"><img
					src="<%=request.getContextPath()%>/img/move_layer.gif"
					onClick="return false"
					onMouseDown="engager(event,'divLayerDett');return false"></td>
				<td height="16" class="azzurro_bianco">
					<%
						if (nuovo) {
					%> Nuovo Tirocinio <%
						} else {
					%> Tirocinio <%
						}
					%>
				</td>
				<td width="16" height="16" onClick="ChiudiDivLayer('divLayerDett')"
					class="azzurro_bianco"><img src="../../img/chiudi_layer.gif"
					alt="Chiudi"></td>
			</tr>
		</table>

		<!-- TODO inserire i controlli!!!  -->
		<af:form name="Frm1" method="POST" action="AdapterHTTP"
			onSubmit="controllaInput()">

			<font color="red"> <af:showErrors />
			</font>

			<table align="center" width="100%" border="0">
				<!-- sezione azienda -->
				<tr class="note">
					<td colspan="2">
						<div class="sezione2">
							<img id='tendinaAzienda' alt="Chiudi" src="../../img/aperto.gif"
								onclick="cambiaTendina(this,'aziendaSez',document.Frm1.pIva);" />&nbsp;&nbsp;&nbsp;Azienda
							&nbsp;&nbsp; <a href="#"
								onClick="javascript:apriSelezionaSoggetto('Aziende', 'aggiornaAzienda','','','');"><img
								src="../../img/binocolo.gif" alt="Cerca"></a> &nbsp;<a
								href="#" onClick="javascript:azzeraAzienda();"><img
								src="../../img/del.gif" alt="Azzera selezione"></a>
						</div>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<div id="aziendaSez" style="display: none;">
							<table class="main" width="100%" border="0">
								<tr>
									<td class="etichetta">Codice Fiscale</td>
									<td class="campo"><af:textBox classNameBase="input"
											type="text" name="codFiscaleAzienda" readonly="true"
											value="<%=codFiscaleAzienda%>" size="30" maxlength="16" /></td>
								</tr>
								<tr>
									<td class="etichetta">Partita IVA</td>
									<td class="campo"><af:textBox classNameBase="input"
											type="text" name="pIva" readonly="true" value="<%=pIva%>"
											size="30" maxlength="11" /> &nbsp;&nbsp;&nbsp;Validità
										C.F./P. IVA&nbsp;&nbsp;<af:textBox classNameBase="input"
											type="text" name="FLGDATIOK" readonly="true"
											value="<%=strFlgDatiOk%>" size="3" maxlength="3" /></td>
								</tr>
								<tr>
									<td class="etichetta">Ragione Sociale</td>
									<td class="campo"><af:textBox classNameBase="input"
											type="text" name="ragioneSociale" readonly="true"
											value="<%=ragioneSociale%>" size="60" maxlength="100" /></td>
								</tr>
								<tr>
									<td class="etichetta">Indirizzo (Comune)</td>
									<td class="campo"><af:textBox classNameBase="input"
											type="text" name="IndirizzoAzienda" readonly="true"
											value="<%=IndirizzoAzienda%>" size="60" maxlength="100" /></td>
								</tr>
								<tr>
									<td class="etichetta">Tipo Azienda</td>
									<td class="campo"><af:textBox classNameBase="input"
											type="text" name="descrTipoAz" readonly="true"
											value="<%=descrTipoAz%>" size="30" maxlength="30" /> <af:textBox
											classNameBase="input" type="hidden" name="codTipoAz"
											readonly="true" value="<%=codTipoAz%>" size="10"
											maxlength="10" /> <af:textBox classNameBase="input"
											type="hidden" name="CODNATGIURIDICA" readonly="true"
											value="<%=codnatGiurAz%>" size="10" maxlength="10" /></td>
								</tr>
								<tr>
									<td colspan="2"><div class="sezione2">&nbsp;</div></td>
								</tr>
							</table>
						</div>
					</td>
				</tr>



<!-- sezione azienda Soggetto Promotore -->
	 	 		  
				<tr class="note">
					<td colspan="2">
						<div class="sezione2">
							<img id='tendinaAziendaSP' alt="Chiudi" src="../../img/aperto.gif"
								onclick="cambiaTendina(this,'aziendaSezSP',document.Frm1.pIva);" />&nbsp;&nbsp;&nbsp;Soggetto Promotore
							&nbsp;&nbsp; <a href="#"
								onClick="javascript:apriSelezionaSoggettoSP('Aziende', 'aggiornaSoggetoPromotore','','','');"><img
								src="../../img/binocolo.gif" alt="Cerca"></a> &nbsp;<a
								href="#" onClick="javascript:azzeraSoggettoPromotore();"><img
								src="../../img/del.gif" alt="Azzera selezione"></a>
						</div>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<div id="aziendaSezSP" style="display: none;">
							<table class="main" width="100%" border="0">
								<tr>
									<td class="etichetta">Codice Fiscale </td>
									<td class="campo"><af:textBox classNameBase="input"
											type="text" name="cfAziendaSP" readonly="true"
											value="<%=cfAziendaSP%>" size="30" maxlength="16" /></td>
								</tr>
								<tr>
									<td class="etichetta">Partita IVA</td>
									<td class="campo"><af:textBox classNameBase="input"
											type="text" name="pIvaSP" readonly="true" value="<%=pIvaSP%>"
											size="30" maxlength="11" /> &nbsp;&nbsp;&nbsp;Validità
										C.F./P. IVA&nbsp;&nbsp;<af:textBox classNameBase="input"
											type="text" name="strFlgDatiOkSP" readonly="true"
											value="<%=strFlgDatiOkSP%>" size="3" maxlength="3" /></td>
								</tr>
								<tr>
									<td class="etichetta">Ragione Sociale</td>
									<td class="campo"><af:textBox classNameBase="input"
											type="text" name="ragioneSocialeSP" readonly="true"
											value="<%=ragioneSocialeSP%>" size="60" maxlength="100" /></td>
								</tr>
								<tr>
									<td class="etichetta">Indirizzo (Comune)</td>
									<td class="campo"><af:textBox classNameBase="input"
											type="text" name="IndirizzoAziendaSP" readonly="true"
											value="<%=IndirizzoAziendaSP%>" size="60" maxlength="100" /></td>
								</tr>
								<tr>
									<td class="etichetta">Tipo Azienda</td>
									<td class="campo"><af:textBox classNameBase="input"
											type="text" name="descrTipoAzSP" readonly="true"
											value="<%=descrTipoAzSP%>" size="30" maxlength="30" /> <af:textBox
											classNameBase="input" type="hidden" name="codTipoAzSP"
											readonly="true" value="<%=codTipoAzSP%>" size="10"
											maxlength="10" /> <af:textBox classNameBase="input"
											type="hidden" name="codnatGiurAzSP" readonly="true"
											value="<%=codnatGiurAzSP%>" size="10" maxlength="10" /></td>
								</tr>
								<tr>
									<td colspan="2"><div class="sezione2">&nbsp;</div></td>
								</tr>
							</table>
						</div>
					</td>
				</tr>


				<tr class="note">
				<tr>
					<td class="etichetta">Tipo Tirocinio</td>
					<td class="campo"><af:comboBox
							selectedValue="<%=codTipoTirocinio%>"
							moduleName="M_GET_COMBO_DE_TIPO_TIROCINIO" addBlank="true"
							onChange="fieldChanged();"
							name="codtipotirocinio" title="Tipo Tirocinio" /></td>
				</tr>
				
				<tr>
					<td class="etichetta">Azioni Concordate (Azione - Data Stimata - Esito)</td>
					<td class="campo"><af:comboBox
							selectedValue="<%=codiceAzione%>"
							moduleName="M_GET_COMBO_AZIONE" addBlank="true"
							onChange="fieldChanged();"
							name="codice" title="Azioni Concordate" /></td>
				</tr>
				
				
				
				
				
				<tr>
					<td class="etichetta">Numero della durata in ore del tirocinio</td>
					<td class="campo" colspan="3"><af:textBox
							value="<%=numDurataOre%>" type="text" name="numDurataOre"
							maxlength="100" onKeyUp="fieldChanged();" /></td>
				</tr>
				<tr>
					<td class="etichetta">Data Inizio&nbsp;</td>
					<td class="campo"><af:textBox value="<%=dataInizioTirocinio%>"
							classNameBase="input" onKeyUp="fieldChanged();"
							title="Data di inzio tirocinio" type="date"
							name="datiniziotirocinio" size="11" maxlength="12"
							validateOnPost="true" /></td>
				</tr>
				<tr>
					<td class="etichetta">Data Fine&nbsp;</td>
					<td class="campo"><af:textBox value="<%=dataFineTirocinio%>"
							classNameBase="input" onKeyUp="fieldChanged();"
							title="Data di fine tirocinio" type="date"
							name="datfinetirocinio" size="11" maxlength="12"
							validateOnPost="true" /></td>
				</tr>

				<tr valign="top">
					<td class="etichetta">Progetto orientativo/formativo del
						tirocinio</td>
					<td class="campo"><af:textArea value="<%=strNoteProgetto%>"
							cols="50" rows="3" classNameBase="textarea"
							onKeyUp="fieldChanged();" title="Progetto" name="strnoteprogetto"
							maxlength="3000" /></td>
				</tr>
				<tr valign="top">
					<td class="etichetta">Sede di preferenza in modalità
						descrittiva</td>
					<td class="campo"><af:textArea value="<%=strSedePreferenza%>"
							onKeyUp="fieldChanged();" cols="50" rows="3"
							classNameBase="textarea" title="Sede" name="strsedepreferenza"
							maxlength="150" /></td>
				</tr>
				
				
				
					<tr valign="top">
					<td class="etichetta">Area Professionale </td>
					<td class="campo"><af:textArea value="<%=STRAREAPROF%>"
							onKeyUp="fieldChanged();" cols="50" rows="3"
							classNameBase="textarea" title="Area Profissionale" name="STRAREAPROF"
							maxlength="200" required="true" /></td>
				</tr>   
				
				
				<tr valign="top">
					<td class="etichetta">Competenza che si intende
						acquisire/implementare/rafforzare</td>
					<td class="campo"><af:textArea
							value="<%=strCompetenzaAcquisizione%>" onKeyUp="fieldChanged();"
							cols="50" rows="3" classNameBase="textarea" title="Competenza"
							name="strcompetenzaacquisizione" maxlength="2000"  /></td>
				</tr>
				<tr valign="top">
					<td class="etichetta">Documento di validazione</td>
					<td class="campo"><af:comboBox name="flgdocumentovalidazione"
							size="1" title="Documento di validazione" moduleName=""
							addBlank="true" classNameBase="input" onChange="fieldChanged()"
							blankValue="">
							<option
								<%="S".equals(flgDocumentoValidazione) ? "selected='true'"
							: ""%>
								value="S">S&igrave;</option>
							<option
								<%="N".equals(flgDocumentoValidazione) ? "selected='true'"
							: ""%>
								value="N">No</option>
						</af:comboBox></td>
				</tr>
				<tr valign="top">
					<td class="etichetta">Note</td>
					<td class="campo"><af:textArea value="<%=strNota%>"
							onKeyUp="fieldChanged();" cols="50" rows="3"
							classNameBase="textarea" title="Note" name="STRNOTA"
							maxlength="2000" /></td>
				</tr>
				<tr>
					<td colspan="4" align="center">
						<!--                <input type="hidden" name="PAGE" value="CurrStudiMainPage"> -->
						<input type="hidden" name="PAGE" value="<%=_current_page%>"> <input
						type="hidden" name="prgcolloquio" value="<%=prgcolloquio%>"> <input
						type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>" /> <input
						type="hidden" name="cdnFunzione" value="<%=_funzione%>" /> <input
						type="hidden" name="PRGAZIENDA" value="<%=prgAzienda%>" /> <input
						type="hidden" name="PRGUNITA" value="<%=prgUnita%>" /> <input
						type="hidden" name="PRGAZIENDASOGGPROM" value="<%=prgAziendaSP%>" />
						<input type="hidden" name="PRGUNITASOGGPROM"
						value="<%=prgUnitaSP%>" />
						
						 <% 	if (nuovo) { %> 
						 <input	type="hidden" name="MODULE" value="M_InsertTirocinio" /> 
						 <input	class="pulsante" type="submit" name="inserisci" value="Inserisci" />
						<input type="button" class="pulsanti" name="chiudi"						value="Chiudi senza inserire"						onClick="ChiudiDivLayer('divLayerDett')"> 
						<% 	} else { %> 
						<input	type="hidden" name="DTMMOD"						value="<%=formatter.format(dtmModificaAttuale)%>" /> 
						<input	type="hidden" name="CDNUTMOD" value="<%=cdnUtMod%>" />
						<input	type="hidden" name="NUMKLOTIROCINIO"						value="<%=numKLoTirocinio.add(new BigDecimal(1))%>" /> 
						<input	type="hidden" name="PRGTIROCINIO" value="<%=prgTirocinio%>" />
						<input	type="hidden" name="MODULE" value="M_UpdateTirocinio" /> 
						<input	class="pulsante" type="submit" name="aggiorna" value="Aggiorna" />
						<input type="button" class="pulsanti" name="chiudi_agg"						value="Chiudi senza aggiornare"						onClick="ChiudiDivLayer('divLayerDett')"> 
						<% 	String onClick = "apriGestioneDoc('RPT_TIROCINIO','&cdnLavoratoreEncrypt="+cdnLavoratoreEncrypt+"&cdnLavoratore="+ cdnLavoratore+ "&PRGTIROCINIO="+ prgTirocinio+"&CODCPIUT="+codCpiUt+"','ST_TNT')";
                                                                       %>
						<input class="pulsante" type="button" name="Stampa" value="Stampa"	onClick="<%=onClick%>" /> <%
                        	}
                         %>
						
			
        
        <input class="pulsante" type="button" name="Documenti associati" value="Documenti associati"
                onClick="docAssociati('<%=cdnLavoratore%>','<%=_current_page%>','<%=_funzione%>','','<%=prgTirocinio%>')">
        
      </td>
					</td>
				</tr>
			</table>
			<%
				if (!nuovo) {
			%>
			<table align="center">
				<tr>
					<td align="center">
						<%
							operatoreInfo.showHTML(out);
						%>
					</td>
				</tr>
			</table>
			<%
				}
			%>
		</af:form>

		<!-- Stondature ELEMENTO BOTTOM -->
		<%
			out.print(divStreamBottom);
		%>
	</div>
	<!-- LAYER - END -->

	<script language="javascript">
        function controllaInput() {
            return (controllaDate() && controllaDurata())
        }

        function controllaDate() {
            var dataInizio = document.Frm1.datiniziotirocinio.value;
            var dataFine = document.Frm1.datfinetirocinio.value;

            try {   
                if (confrontaDate(dataInizio, dataFine) < 0) {
                    alert('Specificare una data di fine tirocinio non precedente alla data di inizio.');
                    return false;
                }
            } catch (err) {
                //console.log("Date tirocinio non specificate");
            }
            return true;
        }

        function controllaDurata() {
            var durata = document.Frm1.numDurataOre.value;

            try {   
                if (isNaN(durata)) {
                    alert('Specificare un valore numerico per la durata del tirocinio.');
                    return false;
                }
            } catch (err) {
            }
            return true;
        }
    
        imgV = document.getElementById("tendinaAzienda");
        <%if (!pIva.equals("")) {%>
            cambiaLavMC("aziendaSez","inline");
            imgV.src = imgAperta;
            imgV.alt="Chiudi";
        <%} else {%>
            cambiaTendina(imgV,"aziendaSez",document.Frm1.pIva)
        <%}%>

        <%if (!prgAzienda.equals("") && !prgUnita.equals("") && !nuovo) {%>
            aggiornaAziendaDettaglio();
        <%}%>

		//Soggetto Promotore
		
		imgVSP = document.getElementById("tendinaAziendaSP");
        <%if (!pIvaSP.equals("")) {%>
            cambiaLavMC("aziendaSezSP","inline");
            imgVSP.src = imgAperta;
            imgVSP.alt="Chiudi";
        <%} else {%>
            cambiaTendina(imgVSP,"aziendaSezSP",document.Frm1.pIvaSP)
        <%}%>

        <%if (!prgAziendaSP.equals("") && !prgUnitaSP.equals("") && !nuovo) {%>
            aggiornaAziendaDettaglioSP();
        <%}%>

        
    </script>
</body>

</html>
