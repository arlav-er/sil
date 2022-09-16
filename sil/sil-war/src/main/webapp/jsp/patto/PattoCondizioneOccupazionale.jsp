<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ taglib uri="patto" prefix="pt" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>


<%@ page import="com.engiweb.framework.base.*,com.engiweb.framework.dispatching.module.AbstractModule,com.engiweb.framework.util.QueryExecutor,it.eng.sil.security.User,it.eng.sil.security.ProfileDataFilter,java.util.*,java.math.*,java.io.*,com.engiweb.framework.security.*,it.eng.sil.util.*,it.eng.afExt.utils.*, it.eng.sil.security.PageAttribs,it.eng.sil.util.patto.PageProperties" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String cdnLavoratore = (String) serviceRequest
			.getAttribute("CDNLAVORATORE");
	String _current_page = (String) serviceRequest.getAttribute("PAGE");
	ProfileDataFilter filter = new ProfileDataFilter(user,
			_current_page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
	PageAttribs pageAtts = new PageAttribs(user, _current_page);

	boolean canModify = false;

	boolean canView = filter.canViewLavoratore();
	if (!canView) {
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	} else {
		boolean existsSalva = pageAtts.containsButton("aggiorna");

		if (!existsSalva) {
			canModify = false;
		} else {
			canModify = filter.canEditLavoratore();
		}
	}
%>

<%
	SourceBean moduleBean = (SourceBean) serviceResponse
			.getAttribute("M_CONDIZIONEOCCUPAZIONALE");
	//
	Vector row_obbligoForma = moduleBean
			.getAttributeAsVector("OBBLIGO_FORMATIVO.ROWS.ROW");
	Vector statoOccRows = moduleBean
			.getAttributeAsVector("STATO_OCCUPAZIONALE.ROWS.ROW");
	Vector permSoggiornoRows = moduleBean
			.getAttributeAsVector("PERMESSO_SOGGIORNO.ROWS.ROW");
	Vector mobilitaRows = moduleBean
			.getAttributeAsVector("MOBILITA.ROWS.ROW");
	Vector collMiratoRows = moduleBean
			.getAttributeAsVector("COLLOCAMENTO_MIRATO.ROWS.ROW");
	Vector indispTempRows = moduleBean
			.getAttributeAsVector("INDISPONIBILITA_TEMP.ROWS.ROW");
	Vector movimentiRows = moduleBean
			.getAttributeAsVector("MOVIMENTI_PRECEDENTI.ROWS.ROW");
	Vector titoloStudioRows = moduleBean
			.getAttributeAsVector("TITOLI_LAVORATORE.ROWS.ROW");
	Vector formazProfRows = moduleBean
			.getAttributeAsVector("FORMAZIONE_PROFESSIONALE.ROWS.ROW");
	/////////////////////////////////
	SourceBean notePatto = (SourceBean) serviceResponse
			.getAttribute("M_PATTOAPERTONOTE.ROWS.ROW");
	if (notePatto == null)
		notePatto = new SourceBean("EMPTY");
	String strNote = Utils.notNull(notePatto
			.getAttribute("strnotesituazioneamm"));
	String prgPattoLavoratore = Utils.notNull(notePatto
			.getAttribute("prgPattoLavoratore"));
	String codTipoPatto = Utils.notNull(notePatto
			.getAttribute("codTipoPatto"));
	BigDecimal klo = (BigDecimal) notePatto
			.getAttribute("numklopattolavoratore");
	String numklopattolavoratore = "";
	if (klo != null)
		numklopattolavoratore = String.valueOf((klo).intValue() + 1);

    String[] tipi_patti_check = {MessageCodes.General.PATTO_L14, MessageCodes.General.PATTO_DOTE, MessageCodes.General.PATTO_DOTE_IA}; 
    
	if (Arrays.asList(tipi_patti_check).contains(codTipoPatto)) {	
    	canModify = false;
    }    	
    String readOnlyStr = canModify ? "false" : "true";
    
	/////////////////////////////////
	String cdnFunzione = (String) serviceRequest
			.getAttribute("CDNFUNZIONE");
	//String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");
	String statoSezioni = (String) serviceRequest
			.getAttribute("statoSezioni");
	StringBuffer args = new StringBuffer();
	////////////////////////
	PageProperties pageProperties = new PageProperties(
			(String) serviceRequest.getAttribute("PAGE"), null);
	/*PageAttribs pageAtts = new PageAttribs((User) sessionContainer.getAttribute(User.USERID),(String) serviceRequest.getAttribute("PAGE"));
	boolean canModify = pageAtts.containsButton("aggiorna");
	String readOnlyStr   =canModify?"false":"true";*/
	//
	pageContext.setAttribute("pageProperties", pageProperties);
	pageContext.setAttribute("sectionState",
			Utils.notNull(statoSezioni));
	pageContext.setAttribute("args", args);
	pageContext
			.setAttribute("isReadOnly", Boolean.valueOf(readOnlyStr));
%>
<%
	String htmlStreamTop = StyleUtils.roundTopTable(canModify);
	String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>
<html>
<head>
<title>Informazioni Correnti</title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<style type="text/css">
table.sezione {
	border-collapse:collapse;
	font-family: Verdana,"Times New Roman", Arial, Sans-serif; 
	font-size: 12px;
	font-weight: bold;
	border-bottom-width: 2px; 
	border-bottom-style: solid;
	border-bottom-color: #000080;
	color:#000080;
	position: relative;
	width: 98%;
	left: 1%;
	text-align: left;
	text-decoration: none;
	
}
td.pulsante_riga{
    text-align: right;
    padding-right:10;
    width:40;
}
</style>
<af:linkScript path="../../js/"/>
<script>
<!--
var flagChanged = false;

function fieldChanged() {
 <%if (!readOnlyStr.equalsIgnoreCase("true")) {%> 
    flagChanged = true;
 <%}%> 
}

var sezioni = new Array();
var i=0;
function chiudiSezioni() {
    for (i=0;i<sezioni.length;i++) {
        if (sezioni[i].sezione.aperta) {
            sezioni[i].sezione.style.display="none";
            sezioni[i].sezione.aperta=false;
            sezioni[i].img.src="../../img/chiuso.gif";
        }
    }
}
function cancella(prgLavPattoScelta) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if (!confirm("Cancellare la associazione?")) return;
	var urlpage="";
	urlpage+="AdapterHTTP?";
	urlpage+="cdnLavoratore=<%=cdnLavoratore%>&";
	urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&";
	urlpage+="cancellaAssociazione=&";
	urlpage+="PRG_LAV_PATTO_SCELTA="+prgLavPattoScelta+"&";
	urlpage+="statoSezioni="+getStatoSezioni()+"&";
	urlpage+="PAGE=PattoCondizioneOccupazionalePage";
	setWindowLocation(urlpage);
}
function cancellaTutto() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if (!confirm("Cancellare tutte le associazioni della sezione?")) return;
    var urlpage="";
    urlpage+="AdapterHTTP?";
    if (arguments.length<=0) {
        alert('nessun legame col patto da cancellare');
        return;
    }
    for (i=0;i<arguments.length;i++) {
        urlpage+="PRG_LAV_PATTO_SCELTA="+arguments[i]+"&";       
    }
    urlpage+="cdnLavoratore=<%=cdnLavoratore%>&";
	urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&";
    urlpage+="cancellaAssociazione=&";
    urlpage+="statoSezioni="+getStatoSezioni()+"&";
	urlpage+="PAGE=PattoCondizioneOccupazionalePage";
    setWindowLocation(urlpage);
}
function apriTutte(page) {
    var urlpage="";
	urlpage+="AdapterHTTP?";
	urlpage+="cdnLavoratore=<%=cdnLavoratore%>&";
	urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&";
	urlpage+="COD_LST_TAB=AM_IND_T&";
    urlpage+="COD_LST_TAB=AM_CM_IS&";
    urlpage+="COD_LST_TAB=PR_STU&";
    urlpage+="COD_LST_TAB=PR_ESP_L&";
   	urlpage+="COD_LST_TAB=PR_COR&";
    urlpage+="COD_LST_TAB=AM_MB_IS&";    
    urlpage+="COD_LST_TAB=AM_OBBFO&";    
    urlpage+="COD_LST_TAB=AM_EX_PS&";    
	urlpage+="statoSezioni="+getStatoSezioni()+"&";
    urlpage+="pageChiamante=PattoCondizioneOccupazionalePage&";
	urlpage+="PAGE="+page;
	window.open(urlpage,"Associazioni", 'toolbar=0, scrollbars=1, resizable=1'); 
}
function apri(page, codLstTab) {
	var urlpage="";
	urlpage+="AdapterHTTP?";
	urlpage+="cdnLavoratore=<%=cdnLavoratore%>&";
	urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&";
	urlpage+="COD_LST_TAB="+codLstTab+"&";
	urlpage+="statoSezioni="+getStatoSezioni()+"&";
	urlpage+="PAGE="+page+"&";
    urlpage+="pageChiamante=PattoCondizioneOccupazionalePage";
	window.open(urlpage,"Associazioni", 'toolbar=0, scrollbars=1, resizable=1'); 
}
function cambia(immagine, sezione) {
	if (sezione.aperta==null) sezione.aperta=true;
	if (sezione.aperta) {
		sezione.style.display="none";
		sezione.aperta=false;
		immagine.src="../../img/chiuso.gif";
	}
	else {
		sezione.style.display="inline";
		sezione.aperta=true;
		immagine.src="../../img/aperto.gif";
	}
}
function getStatoSezioni() {
	var s="";    
	for (j=0;j<sezioni.length;j++) {
		//alert(sezioni[j].aperta);
		if (sezioni[j].sezione.aperta) 
			s+="1";
		else s+="0";
	}
	return s;
}
function Sezione(sezione, img,aperta){    
    this.sezione=sezione;
    this.sezione.aperta=aperta;
    this.img=img;
}

function initSezioni(sezione){
	sezioni.push(sezione);
}

var allArgs="";
function cancellaProprioTutto() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if (allArgs.length==0){
        alert('non ci sono dati da slegare al patto');
        return;
    }
    else {
        var keys=allArgs.substring(0,allArgs.length-1);

        var keysArray = keys.split(",");
       if (!confirm("Cancellare tutte le associazioni della sezione?")) return;
        var urlpage="";
        urlpage+="AdapterHTTP?";
        if (keysArray.length<=0) {
            alert('nessun legame col patto da cancellare');
            return;
        }
        for (i=0;i<keysArray.length;i++) {
            urlpage+="PRG_LAV_PATTO_SCELTA="+keysArray[i]+"&";       
        }
        urlpage+="cdnLavoratore=<%=cdnLavoratore%>&";
        urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&";
        urlpage+="cancellaAssociazione=&";
        urlpage+="statoSezioni="+getStatoSezioni()+"&";
        urlpage+="PAGE=PattoCondizioneOccupazionalePage";
        setWindowLocation(urlpage);
    }
}

function updateStato(thisIn) {
	thisIn.statoSezioni.value = getStatoSezioni();
	return true;
}

//-->
</script>


</head>
<body   class="gestione" onload="rinfresca()">

<%@ include  file="_intestazione.inc" %>

<font color="red">
   <af:showErrors/>
</font>

<font color="green">
 <af:showMessages prefix="M_InsertAmLavPattoScelta"/>
 <af:showMessages prefix="M_DeleteAmLavPattoScelta"/>
 <af:showMessages prefix="M_UpdateNotePatto"/>
</font>


<CENTER> 
<br/>
<%
	out.print(htmlStreamTop);
%>
<table class="main">
<tr><td colspan=4>&nbsp;</tr>
<tr>
    <td colspan="4" class="bianco">
        <table width="100%">
            <tr>                
                <td width="30%"><a href="#" onclick="chiudiSezioni()">Chiudi tutte</a></td> 
                <td></td> 
                <%
                 	if (canModify) {
                 %>
                <td align=right>
                    <a  href="#" onclick="apriTutte('AssociazioneAlPattoTemplatePage')"><img src="../../img/patto_ass.gif"></a>&nbsp;(associa informazioni al patto)
                </td>
                <td  align="right"><a href="#" onclick="cancellaProprioTutto()"><img src="../../img/patto_elim.gif"></a>&nbsp;(cancella tutti i legami)</td>            
                <%
                            	}
                            %>
            </tr>
        </table>
    </td>
</tr>
<tr><td colspan="4">&nbsp;</td></tr>
<tr><td colspan=4>
<pt:sections pageAttribs="<%= pageAtts%>">
    <pt:dynamicSection name="AM_OBBFO" titolo="Notizie sul diritto/dovere di istruzione e formazione" rows="<%= row_obbligoForma %>">
        <pt:sectionAction img="../../img/patto_ass.gif" onclick="apri(\"AssociazioneAlPattoTemplatePage\", \"AM_OBBFO\")"/>
        <pt:sectionAction img="../../img/patto_elim.gif" onclick="cancellaTutto()" addParams="true"/>
        <pt:sectionBody>
        <%
        	String obblForma_flg = (String) row
        						.getAttribute("FLGOBBLIGOFORMATIVO");
        				String obblScolastico_flg = (String) row
        						.getAttribute("FLGOBBLIGOSCOLASTICO");
        				String descMod = (String) row
        						.getAttribute("DESCRIZIONE");
        %>
        <table width="100%">
            <tr>
                <td class="etichetta" >Diritto/dovere di istruzione assolto </td><td><b><%=Utils.notNull(obblScolastico_flg)%></b></td>
                <td class="etichetta" >Diritto/dovere di formazione assolto </td><td><b><%=Utils.notNull(obblForma_flg)%></b></td>
                <td class='pulsante_riga'>
                <%
                	if (Utils.notNull(row.getAttribute("cdnLavoratore"))
                						.length() > 0) {
                					// valorizzo la sequenza di argomenti per la funzione js che cancella tutte le righe
                					args.append(Utils.notNull(row
                							.getAttribute("PRGLAVPATTOSCELTA")));
                					args.append(",");
                %>
                    <script>allArgs+="<%=Utils.notNull(row
									.getAttribute("PRGLAVPATTOSCELTA"))%>,";</script>
                    <a  href="#" onclick="cancella('<%=Utils.notNull(row
									.getAttribute("PRGLAVPATTOSCELTA"))%>','AM_OBBFO')"><img src="../../img/patto_elim.gif" ></a>
                    <%
                    	}
                    %>
                </td>
            </tr>
            <tr>
                <td class="etichetta" >Modalità di assolvimento obbligo </td><td colspan="3"><b><%=Utils.notNull(descMod)%></b></td>
            </tr>
        </table>
        </pt:sectionBody>
    </pt:dynamicSection>
    <pt:dynamicSection name="AM_S_OCC" titolo="Notizie sullo stato occupazionale" rows="<%= statoOccRows %>">
    <%
    	StatoOccupazionaleWrapper so = new StatoOccupazionaleWrapper();
    %>
        <pt:sectionBody>
        <%
        	so.setRow(row);
        %>
        <table width="100%">
            <tr>
              <td class="etichetta" >Inizio </td><td colspan="3"><b><%=so.getDataInizio()%></b></td>
            </tr>
            <tr>
              <td class="etichetta" >Stato occupazionale </td><td colspan="3"><b><%=so.getStatoOccupazionale()%></b></td>
            </tr>
            <!--
            <tr>
              <td class="etichetta"  nowrap>Categoria dlg&nbsp;181 </td><td><b><%=so.getCategoria181()%></b></td>
              <td class="etichetta" >Pensionato </td><td><b><%=so.getPensionato()%></b></td>
            </tr>
            <tr>
              <td class="etichetta" >Reddito </td><td colspan="3"><b><%=so.getReddito()%></b></td>
            </tr>
            <tr>
              <td colspan="4"><table cellpadding="0" cellspacing="0" border="0" width="100%">
                               <tr><td style="text-align:right;font-weight:bold" width="29%"><br/><b>Anzianità di disoccupazione</b></td><td>&nbsp;</td></tr>
                              </table></td>
            </tr>
            <tr>
              <td class="etichetta" >dal </td><td><b><%=so.getDataAnzianitaDisoccupazione()%></b></td>
              <td class="etichetta" >mesi&nbsp;sosp. </td><td><b><%=so.getMesiSospensione()%></b></td>
            </tr>
            <tr>
              <td class="etichetta" >Mesi anzianita </td><td><b><%=so.getMesiAnzianita()%></b></td>
              <td colspan="2"><b><%=so.getTipoAnzianita()%></b></td>
            </tr>
            <tr>
              <td class="etichetta" >Indennizzato </td><td colspan="3"><b><%=so.getIndennizzato()%></b></td>
            </tr>
            -->
        </table>
        </pt:sectionBody>
	</pt:dynamicSection>
    <pt:dynamicSection name="AM_EX_PS" titolo="Notizie sui cittadini stranieri" rows="<%= permSoggiornoRows %>">  
        <pt:sectionAction img="../../img/patto_ass.gif" onclick="apri(\"AssociazioneAlPattoTemplatePage\", \"AM_EX_PS\")"/>
        <pt:sectionAction img="../../img/patto_elim.gif" onclick="cancellaTutto()" addParams="true"/>
        <pt:sectionBody>
        <%--
         datScad         = (String)      ;
         datRichiesta    = (String)      ;
         motivoRilDesc   = (String)      ;
         statoRicDesc    = (String)      );
        --%>
        <table width="100%">
            <tr>
              <td class="etichetta">Tipologia</td>
              <%
              	String statusDescr = Utils.notNull(row
              						.getAttribute("statusDescr"));
              				String codStatus = Utils.notNull(row
              						.getAttribute("codStatus"));
              %>
              <td><b><%=statusDescr%></b></td>
              
              <td class="etichetta2"><%=(codStatus.equals("2") || codStatus
								.equals("5")) ? "Revoca carta di soggiorno"
								: "Scadenza documento"%></td>
              <td><b><%=Utils.notNull(row.getAttribute("DATSCADENZA"))%>&nbsp;</b></td>
              <td class='pulsante_riga'>
                <%
                	if (Utils.notNull(row.getAttribute("prgpermsogg"))
                						.length() > 0) {
                					// valorizzo la sequenza di argomenti per la funzione js che cancella tutte le righe
                					args.append(Utils.notNull(row
                							.getAttribute("PRGLAVPATTOSCELTA")));
                					args.append(",");
                %>
                    <script>allArgs+="<%=Utils.notNull(row
									.getAttribute("PRGLAVPATTOSCELTA"))%>,";</script>
                    <a  href="#" onclick="cancella('<%=Utils.notNull(row
									.getAttribute("PRGLAVPATTOSCELTA"))%>','AM_EX_PS')"><img src="../../img/patto_elim.gif" ></a>
                    <%
                    	}
                    %>
                </td>
            </tr>
            <tr>
              <td class="etichetta">Data richiesta/Sanatoria</td>
              <td colspan="3"><b><%=Utils.notNull(row
								.getAttribute("DATRICHIESTA"))%></b></td><td></td>
            </tr>
            <tr>
              <td class="etichetta">Stato richiesta</td>
              <td colspan="3"><b><%=Utils.notNull(row
								.getAttribute("DESCRIZIONERICH"))%></b></td><td></td>
            </tr>
            <tr>
              <td class="etichetta">Motivo richiesta</td>
              <td colspan="3"><b><%=Utils.notNull(row
								.getAttribute("DESCRIZIONEMOT"))%></b></td><td></td>
            </tr>
        </table>
    </pt:sectionBody>
	</pt:dynamicSection>
    <pt:dynamicSection name="AM_CM_IS" titolo="Liste speciali: collocamento mirato" rows="<%= collMiratoRows %>">
        <pt:sectionAction img="../../img/patto_ass.gif" onclick="apri(\"AssociazioneAlPattoTemplatePage\", \"AM_CM_IS\")"/>
        <pt:sectionAction img="../../img/patto_elim.gif" onclick="cancellaTutto()" addParams="true"/>
        <pt:sectionBody>    
		<table width="100%">
			<tr>
                <td class="etichetta" >Data Inizio </td>
                <td colspan="3"><b><%=Utils.notNull(row.getAttribute("DATINIZIO"))%></b></td>
                <td class='pulsante_riga'>
                <%
                	if (Utils.notNull(row.getAttribute("PRGCMISCR"))
                						.length() > 0) {
                					// valorizzo la sequenza di argomenti per la funzione js che cancella tutte le righe
                					args.append(Utils.notNull(row
                							.getAttribute("PRGLAVPATTOSCELTA")));
                					args.append(",");
                %>
                    <script>allArgs+="<%=Utils.notNull(row
									.getAttribute("PRGLAVPATTOSCELTA"))%>,";</script>
                    <a  href="#" onclick="cancella('<%=Utils.notNull(row
									.getAttribute("PRGLAVPATTOSCELTA"))%>','AM_CM_IS')"><img src="../../img/patto_elim.gif" ></a>
                    <%
                    	}
                    %>
                </td>
			</tr>
			<tr>
			   <td class="etichetta" >Categoria </td>
			   <td><b><%=Utils.notNull(row
								.getAttribute("DESCRIZIONEISCR"))%></b></td>
               <td class="etichetta2">Tipo </td>
                <%
                	String tipoRaggiunto = null;
                				String codMonoTipoRagg = (String) row
                						.getAttribute("CODMONOTIPORAGG");
                				if (codMonoTipoRagg != null
                						&& codMonoTipoRagg.equals("D"))
                					tipoRaggiunto = "Disabili";
                				else if (codMonoTipoRagg != null
                						&& codMonoTipoRagg.equals("A"))
                					tipoRaggiunto = "Altri";
                %>
               <td width="30%"><b><%=Utils.notNull(tipoRaggiunto)%></b></td>
               <td></td>
			</tr>
			<tr>
			   <td class="etichetta" >Tipo invalidità</td>
			   <td><b><%=Utils.notNull(row
								.getAttribute("DESCRIZIONEINV"))%></b></td>
			   <td></td><td></td>
               <td></td>
			</tr>
            <tr>
			   <td class="etichetta" >Percentuale invalidità</td>
			   <td><b><%=Utils.notNull(row
								.getAttribute("NUMPERCINVALIDITA"))%></b>&nbsp;%</td>
               <td></td><td></td><td></td>
			</tr>
		</table>
    	</pt:sectionBody>
    </pt:dynamicSection>
    <pt:dynamicSection name="AM_MB_IS" titolo="Liste speciali: mobilità" rows="<%= mobilitaRows %>">
        <pt:sectionAction img="../../img/patto_ass.gif" onclick="apri(\"AssociazioneAlPattoTemplatePage\", \"AM_MB_IS\")"/>
        <pt:sectionAction img="../../img/patto_elim.gif" onclick="cancellaTutto()" addParams="true"/>
        <pt:sectionBody>
		<table  width="100%">
			<tr>
			   <td class="etichetta" >Tipo lista</td>
			   <td colspan="3"><b><%=Utils.notNull(row.getAttribute("DESCRIZIONE"))%></b></td><td></td>			   
			   <td  class='pulsante_riga'>
                <%
                	if (Utils.notNull(row.getAttribute("prgmobilitaiscr"))
                						.length() > 0) {
                					// valorizzo la sequenza di argomenti per la funzione js che cancella tutte le righe
                					args.append(Utils.notNull(row
                							.getAttribute("PRGLAVPATTOSCELTA")));
                					args.append(",");
                %>
                    <script>allArgs+="<%=Utils.notNull(row
									.getAttribute("PRGLAVPATTOSCELTA"))%>,";</script>
                    <a  href="#" onclick="cancella('<%=Utils.notNull(row
									.getAttribute("PRGLAVPATTOSCELTA"))%>','AM_MB_IS')"><img src="../../img/patto_elim.gif" ></a>
                    <%
                    	}
                    %>
                </td>
			</tr>			
           <tr>
                <td class="etichetta" >Data inizio </td><td><b><%=Utils.notNull(row.getAttribute("DATINIZIO"))%></b></td>
               <td colspan=2></td>
			</tr>
			<tr>
			   <td class="etichetta" >Indennità</td>
			   <td colspan="3"><b><%=Utils.notNull(row
								.getAttribute("FLGINDENNITA"))%></b></td><td></td>
			</tr>
		</table>
	</pt:sectionBody>
    </pt:dynamicSection>
    <pt:dynamicSection name="AM_IND_T" titolo="Condizioni" rows="<%= indispTempRows %>">
        <pt:sectionAction img="../../img/patto_ass.gif" onclick="apri(\"AssociazioneAlPattoTemplatePage\", \"AM_IND_T\")"/>
        <pt:sectionAction img="../../img/patto_elim.gif" onclick="cancellaTutto()" addParams="true"/>
        <pt:sectionBody>        
            <table width="100%">
                <tr>
                  <td class="etichetta">Tipo Indisp. </td>
                  <td colspan="3">
                   <table width="100%" border="0" cellpadding="0" cellspacing="0" >
                      <tr>
                        <td width="30%"><b><%=Utils.notNull(row.getAttribute("DESCRIZIONE"))%></b></td>
                        <td width="20%">dal&nbsp;<b><%=Utils.notNull(row.getAttribute("DATINIZIO"))%></b></td>
                        <td> &nbsp;&nbsp;al&nbsp;<b><%=Utils.notNull(row.getAttribute("DATFINE"))%></b></td>
                        <td class='pulsante_riga'>
                        <%
                        	if (Utils.notNull(row.getAttribute("PRGINDISPTEMP"))
                        						.length() > 0) {
                        					// valorizzo la sequenza di argomenti per la funzione js che cancella tutte le righe
                        					args.append(Utils.notNull(row
                        							.getAttribute("PRGLAVPATTOSCELTA")));
                        					args.append(",");
                        %>
                        <script>allArgs+="<%=Utils.notNull(row
									.getAttribute("PRGLAVPATTOSCELTA"))%>,";</script>
                        <a  href="#" onclick="cancella('<%=Utils.notNull(row
									.getAttribute("PRGLAVPATTOSCELTA"))%>','AM_IND_T')"><img src="../../img/patto_elim.gif" ></a>
                        <%
                        	}
                        %>
                        </td>
                      </tr>      
                   </table>
                   </td>
                </tr>
            </table>
        </pt:sectionBody>
	 </pt:dynamicSection>
     <pt:dynamicSection name="PR_ESP_L" titolo="Ultime esperienze professionali: precedenti o in corso" rows="<%= movimentiRows %>">
        <pt:sectionAction img="../../img/patto_ass.gif" onclick="apri(\"AssociazioneAlPattoTemplatePage\", \"PR_ESP_L\")"/>
        <pt:sectionAction img="../../img/patto_elim.gif" onclick="cancellaTutto()" addParams="true"/>
        <pt:sectionBody>        
        <%
                	EsperienzeProfWrapper esp = new EsperienzeProfWrapper();
                				esp.setRow(row);
                %>
        <table width="100%">
            <tr>
              <td class="etichetta">Tipo esperienza</td>
              <td colspan="3"><b><%--=Utils.notNull(esperienza)--%><%=esp.getTipoEsperienza()%></b></td>
<td class='pulsante_riga'>
<%
	if (esp.getPrgEspLavoro().length() > 0) {
					// valorizzo la sequenza di argomenti per la funzione js che cancella tutte le righe
					args.append(esp.getPrgLavPattoScelta());
					args.append(",");
%>
<script type="text/javascript">
allArgs+="<%=esp.getPrgLavPattoScelta()%>,";
</script>

<a href="#"	onclick="cancella('<%=esp.getPrgLavPattoScelta()%>','PR_ESP_L')">
<img src="../../img/patto_elim.gif">
</a> <%
 	}
%>
	</td>
</tr>
            <tr>
              <td class="etichetta">Mansione</td>
              <td colspan="3"><b><%=esp.getMansione()%></b></td><td></td>
            </tr>
            <tr>
              <td class="etichetta" width="30%">Mese/anno di inizio</td><td>
              <td><b><%=esp.getMeseInizio()%></b>&nbsp;/&nbsp;<b><%=esp.getAnnoInizio()%></b></td>
              <td class="etichetta" width="30%">Mese/anno fine</td>
              <td><b><%=esp.getMeseFine()%></b>&nbsp;/&nbsp;<b><%=esp.getAnnoFine()%></b></td>
              <td></td>
            </tr>
            <tr>
              <td class="etichetta">Retribuzione lorda annua </td><td colspan="4"><b><%=esp.getRetribuzioneAnnua()%></b></td>
            </tr>
        </table>    
        </pt:sectionBody>
    </pt:dynamicSection>
    <pt:dynamicSection name="PR_STU" titolo="Titolo di studio" rows="<%= titoloStudioRows %>">
        <pt:sectionAction img="../../img/patto_ass.gif" onclick='apri("AssociazioneAlPattoTemplatePage", "PR_STU")'/>
        <pt:sectionAction img="../../img/patto_elim.gif" onclick="cancellaTutto()" addParams="true"/>
        <pt:sectionBody>        
            <table width="100%">
<%
	TitoloStudioWrapper tit = new TitoloStudioWrapper();
				tit.setRow(row);
%>
			<tr>
			  <td class="etichetta">Tipo</td>
			  <td colspan="3"><b><%=Utils.notNull(tit.getTipo())%></b></td>
              <td class='pulsante_riga'>
                <%
                	if (tit.getPrgStudio().length() > 0) {
                					// valorizzo la sequenza di argomenti per la funzione js che cancella tutte le righe
                					args.append(tit.getPrgLavPattoScelta());
                					args.append(",");
                %>
                    <script>allArgs+="<%=tit.getPrgLavPattoScelta()%>,";</script>
                    <a  href="#" onclick="cancella('<%=tit.getPrgLavPattoScelta()%>','PR_STU')"><img src="../../img/patto_elim.gif" ></a>
                <%
                	}
                %>
              </td>
			</tr>
			<tr>
			  <td class="etichetta">Titolo di studio</td>
			  <td><b><%=Utils.notNull(tit.getDescrizione())%></b></td>
			  <td class="etichetta">Anno</td>
			  <td><b><%=Utils.notNull(tit.getAnno())%></b></td>
			</tr>
            <tr><td colspan=4></td></tr>
		</table>
    	</pt:sectionBody>
    </pt:dynamicSection>
    <pt:dynamicSection name="PR_COR" titolo="Formazione professionale: precedente o in corso" rows="<%= formazProfRows %>">
        <pt:sectionAction img="../../img/patto_ass.gif" onclick="apri(\"AssociazioneAlPattoTemplatePage\", \"PR_COR\")"/>
        <pt:sectionAction img="../../img/patto_elim.gif" onclick="cancellaTutto()" addParams="true"/>
        <% int idx =0; %>
        <pt:sectionBody>
        <table  width="100%">
            <tr>
                <td class="etichetta">Nome del corso</td>
                <td colspan="3"><b><%=Utils.notNull((String) row
								.getAttribute("CORSO"))%></b></td>
                <td class='pulsante_riga'>
                <%
                	if (Utils
                						.notNull(row.getAttribute("PRGLAVPATTOSCELTA"))
                						.length() > 0) {
                					args.append(Utils.notNull(row
                							.getAttribute("PRGLAVPATTOSCELTA")));
                					args.append(",");
                %>
                <script>allArgs+="<%=Utils.notNull(row
									.getAttribute("PRGLAVPATTOSCELTA"))%>,";</script>
                <a  href="#" onclick="cancella('<%=Utils.notNull(row
									.getAttribute("PRGLAVPATTOSCELTA"))%>','PR_COR')"><img src="../../img/patto_elim.gif" ></a>
                <%
                	}
                %>
                </td>
            </tr>
            <tr>
                <td class="etichetta">Anno corso</td><td ><b><%=Utils.notNull((BigDecimal) row
								.getAttribute("NUMANNO"))%></b></td>
                <td class="etichetta">Percorso completato</td><td ><b><%=Utils.notNull((String) row
								.getAttribute("FLGCOMPLETATO"))%></b></td>
                <td></td>
            </tr>

        </table>
<!-- formazProfRows size<%=formazProfRows.size() %> idx <%=idx %>  -->
 <%
Object stampaPattoConf=serviceResponse.getAttribute("M_ST_CONF_STAMPA_PATTO.ROWS.ROW.NUM");
if ("1".equals(stampaPattoConf) && formazProfRows.size() <= ++idx ){
SourceBean	rowPattoLav=(SourceBean) serviceResponse.getAttribute("M_GETPATTOLAV.ROWS.ROW");
String STRNOTEFORMPROF = Utils.notNull((String) rowPattoLav.getAttribute("STRNOTEFORMPROF"));
%>
<tr>
<td colspan=3>
<af:form method="post" action="AdapterHTTP" onSubmit="updateStato(this)" dontValidate="true">
        <input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>">
        <input type="hidden" name="PAGE" value="PattoCondizioneOccupazionalePage">
        <input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">  
        <input type="hidden" name="statoSezioni" value="">
        <input type="hidden" name="PRGPATTOLAVORATORE" value="<%=prgPattoLavoratore%>">        
        <input type="hidden" name="NUMKLOPATTOLAVORATORE" value="<%=numklopattolavoratore%>">
            <table width="100%">
                <tr>
    <td class="etichetta">Nota</td>
    <td class="campo"><af:textArea onKeyUp="fieldChanged();" name="STRNOTEFORMPROF" value="<%=STRNOTEFORMPROF%>" cols="60" rows="5" readonly="<%=readOnlyStr%>"/></td>
                    <td>
                    <%
                    	if (canModify && prgPattoLavoratore.length() > 0) {
                    %>
                        <input type="submit" name="aggiornaNotePatto" class="pulsanti" value="Aggiorna note">
                    <%
                    	}
                    %>
                    </td>
                </tr>
            </table>
    </af:form>
        </td>
    </tr>
<%}
// if ("1".equals(stampaPattoConf))
%>
        </pt:sectionBody>
    </pt:dynamicSection>
</pt:sections>
   
</td></tr>
<tr><td colspan="3"><br><br></td></tr>
    <af:form method="post" action="AdapterHTTP" onSubmit="updateStato(this)" dontValidate="true">
        <input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>">
        <input type="hidden" name="PAGE" value="PattoCondizioneOccupazionalePage">
        <input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">  
        <input type="hidden" name="statoSezioni" value="">
        <input type="hidden" name="PRGPATTOLAVORATORE" value="<%=prgPattoLavoratore%>">        
        <input type="hidden" name="NUMKLOPATTOLAVORATORE" value="<%=numklopattolavoratore%>"> 
    <tr>
        <td colspan=3>
            <table width="100%">
                <tr>
                    <td class="etichetta">Nota situazione occupazionale</td>
                    <td class="campo"><af:textArea onKeyUp="fieldChanged();" name="STRNOTESITUAZIONEAMM" value="<%=strNote%>" cols="60" rows="5" readonly="<%=readOnlyStr%>"/></td>
                    <td>
                    <%
                    	if (canModify && prgPattoLavoratore.length() > 0) {
                    %>
                        <input type="submit" name="aggiornaNotePatto" class="pulsanti" value="Aggiorna note">
                    <%
                    	}
                    %>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    </af:form>

</table>
<%
	out.print(htmlStreamBottom);
%>
</CENTER>

</body>
</html>

<%!/**
	 */
	private class EsperienzeProfWrapper {
		SourceBean row;

		String getTipoEsperienza() {
			return Utils.notNull((String) row.getAttribute("DESCRIZIONECONTR"));
		}

		String getMansione() {
			return Utils.notNull((String) row.getAttribute("DESCRIZIONEMANS"));
		}

		String getAnnoInizio() {
			return Utils
					.notNull((BigDecimal) row.getAttribute("NUMANNOINIZIO"));
		}

		String getAnnoFine() {
			return Utils.notNull((BigDecimal) row.getAttribute("NUMANNOFINE"));
		}

		String getMeseFine() {
			return Utils.notNull((BigDecimal) row.getAttribute("NUMMESEFINE"));
		}

		String getMeseInizio() {
			return Utils
					.notNull((BigDecimal) row.getAttribute("NUMMESEINIZIO"));
		}

		String getRetribuzioneAnnua() {
			return Utils.notNull((BigDecimal) row.getAttribute("RETRIBANNUA"));
		}

		String getPrgLavPattoScelta() {
			return Utils.notNull(row.getAttribute("PRGLAVPATTOSCELTA"));
		}

		String getPrgPattoLavoratore() {
			return Utils.notNull(row.getAttribute("PRGPATTOLAVORATORE"));
		}

		String getPrgEspLavoro() {
			return Utils.notNull(row.getAttribute("PRGESPLAVORO"));
		}

		void setRow(SourceBean row) {
			this.row = row;
		}
	}

	/**/
	private class TitoloStudioWrapper {
		private SourceBean row;

		String getDescrizione() {
			return Utils.notNull((String) row.getAttribute("destitolo"));
		}

		String getTipo() {
			return Utils.notNull((String) row.getAttribute("destipotitolo"));
		}

		String getAnno() {
			return Utils.notNull((BigDecimal) row.getAttribute("NUMANNO"));
		}

		String getPrgLavPattoScelta() {
			return Utils.notNull(row.getAttribute("PRGLAVPATTOSCELTA"));
		}

		String getPrgStudio() {
			return Utils.notNull(row.getAttribute("PRGSTUDIO"));
		}

		void setRow(SourceBean row) {
			this.row = row;
		}

		String getPrgPattoLavoratore() {
			return Utils.notNull(row.getAttribute("PRGPATTOLAVORATORE"));
		}
	}

	/**/
	private class StatoOccupazionaleWrapper {
		private SourceBean row;

		void setRow(SourceBean row) {
			this.row = row;
		}

		String getDataInizio() {
			return Utils.notNull((String) row.getAttribute("DATINIZIO"));
		}

		String getDataFine() {
			return Utils.notNull((String) row.getAttribute("DATFINE"));
		}

		String getStatoOccupazionale() {
			return Utils.notNull((String) row.getAttribute("DESCRIZIONESTATO"));
		}

		String getTipoAnzianita() {
			String cod181 = (String) row.getAttribute("CODCATEGORIA181");
			BigDecimal mesiAnz = (BigDecimal) row.getAttribute("MESI_ANZ");
			if (mesiAnz != null) {
				if (mesiAnz.compareTo(new BigDecimal(12)) > 0
						|| (mesiAnz.compareTo(new BigDecimal(6)) > 0
								&& cod181 != null && cod181
									.equalsIgnoreCase("G")))
					return "disoccupato/inoccupato di lunga durata";
				if (mesiAnz.compareTo(new BigDecimal(24)) > 0)
					return "soggetto alla legge&nbsp407/90";
			}
			return "";
		}

		String getCategoria181() {
			return Utils.notNull((String) row.getAttribute("DESCRIZIONE181"));
		}

		String getIndennizzato() {
			return Utils.notNull((String) row.getAttribute("FLGINDENNIZZATO"));
		}

		String getPensionato() {
			return Utils.notNull((String) row.getAttribute("FLGPENSIONATO"));
		}

		String getMesiSospensione() {
			return Utils.notNull((BigDecimal) row.getAttribute("NUMMESISOSP"));
		}

		String getReddito() {
			return Utils.notNull((BigDecimal) row.getAttribute("NUMREDDITO"));
		}

		String getDataAnzianitaDisoccupazione() {
			return Utils
					.notNull((String) row.getAttribute("DATANZIANITADISOC"));
		}

		String getMesiAnzianita() {
			return Utils.notNull((BigDecimal) row.getAttribute("MESI_ANZ"));
		}

		String getNumeroAtto() {
			return Utils.notNull((String) row.getAttribute("STRNUMATTO"));
		}

		String getDataAtto() {
			return Utils.notNull((String) row.getAttribute("DATATTO"));
		}

		String getDataRichiestaRevisione() {
			return Utils.notNull((String) row.getAttribute("DATRICHREVISIONE"));
		}

		String getDataRicorsoGiudiziario() {
			return Utils.notNull((String) row
					.getAttribute("DATRICORSOGIURISDIZ"));
		}

		String getStatoAtto() {
			return Utils.notNull((String) row.getAttribute("DESCRIZIONERICH"));
		}
	}%>
