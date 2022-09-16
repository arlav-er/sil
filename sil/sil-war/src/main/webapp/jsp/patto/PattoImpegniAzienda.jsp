<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ taglib uri="patto" prefix="pt" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.util.*,
                  it.eng.afExt.utils.StringUtils,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  java.lang.*,
                  java.text.*,it.eng.sil.security.PageAttribs,it.eng.sil.util.patto.PageProperties
                  "   %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
    /////////////////////////////////


    SourceBean notePatto = (SourceBean)serviceResponse.getAttribute("M_PattoApertoAziendaNote.ROWS.ROW");
    String strNote = "";
    String prgPattoSceltaUnitaAzienda = "";
    String numklopattounitaaziendale = "";
    Linguette l = null;

          //Testata azienda
      SourceBean aziendaRow=null;
      String strCodiceFiscale="";
      String strPartitaIva="";
      String strRagioneSociale="";
      InfCorrentiAzienda infCorrentiAzienda= null;
    if (notePatto!=null) {
        strNote = Utils.notNull(notePatto.getAttribute("strnoteimpegni"));
        prgPattoSceltaUnitaAzienda = Utils.notNull(notePatto.getAttribute("prgPattoSceltaUnitaAzienda"));
        numklopattounitaaziendale = String.valueOf(((BigDecimal)notePatto.getAttribute("numklopattounitaaziendale")).intValue()+1);
    }
    /////////////////////////////////
    Vector impegni= serviceResponse.getAttributeAsVector("M_GetImpegniAziendaLegatiAlPatto.ROWS.ROW");
    ArrayList impegniAzienda  = new ArrayList();
    ArrayList impegniCPI  = new ArrayList();
    for (int i=0;i<impegni.size();i++) {
        SourceBean row = (SourceBean)impegni.get(i);
        String codiceImp = (String)row.getAttribute("codMonoImpegnoDi");
        if (Utils.notNull(codiceImp).equals("S"))
            impegniAzienda.add(row);
        else impegniCPI.add(row);
    }
  //  impegni.removeAll(impegniAzienda);
    ///////////////////////
    String prgAzienda = (String) serviceRequest.getAttribute("prgAzienda");
    String prgUnita = (String) serviceRequest.getAttribute("prgUnita");
aziendaRow= (SourceBean) serviceResponse.getAttribute("M_GetTestataAzienda.ROWS.ROW");
strCodiceFiscale= StringUtils.getAttributeStrNotNull(aziendaRow, "strCodiceFiscale");
strPartitaIva=StringUtils.getAttributeStrNotNull(aziendaRow, "strPartitaIva");
strRagioneSociale=StringUtils.getAttributeStrNotNull(aziendaRow, "strRagioneSociale");
    BigDecimal prgAziendaDB = new BigDecimal(prgAzienda);
    StringBuffer args = new StringBuffer();
   infCorrentiAzienda= new InfCorrentiAzienda(prgAzienda,prgUnita);
    String statoSezioni = (String)serviceRequest.getAttribute("statoSezioni");
    ////////////////////////
    PageProperties pageProperties = new PageProperties((String) serviceRequest.getAttribute("PAGE"),null);
    PageAttribs pageAtts = new PageAttribs((User) sessionContainer.getAttribute(User.USERID),(String) serviceRequest.getAttribute("PAGE"));
    boolean canModify = true; //pageAtts.containsButton("aggiorna");
    String readOnlyStr   = "false";  //canModify?"false":"true";


    
    pageContext.setAttribute("pageProperties", pageProperties);
    pageContext.setAttribute("sectionState",Utils.notNull(statoSezioni));
    pageContext.setAttribute("args", args);
    pageContext.setAttribute("isReadOnly", Boolean.valueOf(readOnlyStr));
        

    int cdnFunzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
    String _page = (String) serviceRequest.getAttribute("PAGE"); 
    l = new Linguette(user, cdnFunzione, _page, prgAziendaDB);   
    l.setCodiceItem("prgUnita=" +prgUnita +  "&prgAzienda");

    
    %>
<html>
<head>
<title>Azioni</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
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
function cancella(prgPattoSceltaUnitaAzienda) {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    if (!confirm("Cancellare la associazione?")) return;
	var urlpage="";
	urlpage+="AdapterHTTP?";
	urlpage+="prgAzienda=<%=prgAzienda%>&";
	urlpage+="CDNFUNZIONE=<%=cdnFunzione%>&";
	urlpage+="cancellaAssociazione=&";
	urlpage+="PRG_PATTO_SCELTA_UNITA_AZIENDA="+prgPattoSceltaUnitaAzienda+"&";
	urlpage+="statoSezioni="+getStatoSezioni()+"&";
	urlpage+="PAGE=PattoImpegniAziendaPage";
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
        urlpage+="PRG_PATTO_SCELTA_UNITA_AZIENDA="+arguments[i]+"&";       
    }
    urlpage+="prgAzienda=<%=prgAzienda %>&";
	urlpage+="CDNFUNZIONE=<%=cdnFunzione %>&";
    urlpage+="cancellaAssociazione=&";
    urlpage+="statoSezioni="+getStatoSezioni()+"&";
	urlpage+="PAGE=PattoImpegniAziendaPage";
    setWindowLocation(urlpage);
}
function apriTutte(page) {
    var urlpage="";
	urlpage+="AdapterHTTP?";
	urlpage+="prgAzienda=<%=prgAzienda %>&";
	urlpage+="CDNFUNZIONE=<%=cdnFunzione %>&";	
    urlpage+="COD_LST_TAB=DE_IMPE_AZ&";    
    urlpage+="COD_LST_TAB=DE_IMPE_C_AZ&";    
	urlpage+="statoSezioni="+getStatoSezioni()+"&";
    urlpage+="pageChiamante=PattoImpegniAziendaPage&";
	urlpage+="PAGE="+page;
	window.open(urlpage,"Associazioni", 'toolbar=0, scrollbars=1, resizable=1'); 
}
function apri(page, codLstTab) {
	var urlpage="";
	urlpage+="AdapterHTTP?";
	urlpage+="prgAzienda=<%=prgAzienda %>&";
	urlpage+="CDNFUNZIONE=<%=cdnFunzione %>&";
	urlpage+="COD_LST_TAB="+codLstTab+"&";
	urlpage+="statoSezioni="+getStatoSezioni()+"&";
	urlpage+="PAGE="+page+"&";
    urlpage+="pageChiamante=PattoImpegniAziendaPage";
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
            urlpage+="PRG_PATTO_SCELTA_UNITA_AZIENDA="+keysArray[i]+"&";       
        }
        urlpage+="prgAzienda=<%=prgAzienda %>&";
        urlpage+="CDNFUNZIONE=<%=cdnFunzione %>&";
        urlpage+="cancellaAssociazione=&";
        urlpage+="statoSezioni="+getStatoSezioni()+"&";
        urlpage+="PAGE=PattoImpegniAziendaPage";
        setWindowLocation(urlpage);
    }
}

function updateStato(thisIn) {
	thisIn.statoSezioni.value = getStatoSezioni();
	return true;
}
//-->
</script>

<script language="JavaScript">
// Rilevazione Modifiche da parte dell'utente
var flagChanged = false;

function fieldChanged() {
 <% if (!readOnlyStr.equalsIgnoreCase("true")){ %> 
    flagChanged = true;
 <%}%> 
}
</script>

</head>

<%
String htmlStreamTopInfo = StyleUtils.roundTopTableInfo();
String htmlStreamBottomInfo = StyleUtils.roundBottomTableInfo();

String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>


<body   class="gestione" onload="rinfresca()">
                
<%--@ include  file="_intestazione.inc" --%>

<font color="red">
   <af:showErrors/>
</font>

<font color="green">
 <af:showMessages prefix="M_InsertAziendaPattoScelta"/>
 <af:showMessages prefix="M_DeleteAziendaPattoScelta"/>
 <af:showMessages prefix="M_UpdateNoteAziendaPatto"/>
</font>


<br>
<%out.print(htmlStreamTop);%>

<%
    if(infCorrentiAzienda != null) infCorrentiAzienda.show(out); 

%>


  <%out.print(htmlStreamBottomInfo);%> 
        <%
          if (l!=null)l.show(out);
        %>
    <br/>    

    <%out.print(htmlStreamTop);%>

<table class="main" width="96%">
<tr>
  <td colspan="2">
    <div align="center" style="border-width: 1px; border-style: solid; text-color: #000080; border-color: #000080; background-color:lightblue">


    <tr><td colspan=4>&nbsp;</td></tr>
        <tr>
            <td width="30%"><a href="#" onclick="chiudiSezioni()">Chiudi tutte</a></td> 
            <td></td> 
            <td align=right>
                <a  href="#" onclick="apriTutte('AssociazioneAlPattoTemplatePage')"><img src="../../img/binocolo.gif"></a>&nbsp;(associa informazioni al patto)
            </td>
            <td  align="right"><a href="#" onclick="cancellaProprioTutto()"><img src="../../img/del.gif"></a>(cancella tutti i legami)</td>            
    </tr>
        <tr><td colspan=4>&nbsp;</td></tr>
      <tr>
        <td colspan=4>
            <pt:sections pageAttribs="<%= null %>">
				<pt:dynamicSection name="DE_IMPE_AZ" titolo="L'azienda si impegna a" rows="<%=  impegniAzienda %>">
        		<pt:sectionAction img="../../img/binocolo.gif" onclick="apri(\"AssociazioneAlPattoTemplatePage\", \"DE_IMPE_AZ\")"/>
        		<pt:sectionAction img="../../img/del.gif" onclick="cancellaTutto()" addParams="true"/>
        		<pt:sectionBody>        
                  <tr>
                    <td></td>
                    <td class="etichetta2" style="text-align:right"></td>
                    <td class="campo2" style="font-weight:bold"><%=row.getAttribute("strDescrizione")%></td>
                    <td class="etichetta2"></td>
                    <td class='pulsante_riga'>
                        <% if (Utils.notNull(row.getAttribute("prgPattoSceltaUnitaAzienda")).length()>0) {
                            // valorizzo la sequenza di argomenti per la funzione js che cancella tutte le righe
                            args.append(Utils.notNull(row.getAttribute("prgPattoSceltaUnitaAzienda")));
                            args.append(",");
                        %>
                        <script>allArgs+="<%=Utils.notNull(row.getAttribute("prgPattoSceltaUnitaAzienda"))%>,";</script>
                        <a  href="#" onclick="cancella('<%=row.getAttribute("prgPattoSceltaUnitaAzienda")%>')"><img src="../../img/del.gif" ></a>
                        <%}%>
                    </td>
                  </tr>
				</pt:sectionBody>
			</pt:dynamicSection>                  
        </td>
    </tr>
    <tr>
        <td colspan=4>
				<pt:dynamicSection name="DE_IMPE_C_AZ" titolo="Il Centro per l'Impiego si impegna a" rows="<%= impegniCPI %>">
        		<pt:sectionAction img="../../img/binocolo.gif" onclick="apri(\"AssociazioneAlPattoTemplatePage\", \"DE_IMPE_C_AZ\")"/>
        		<pt:sectionAction img="../../img/del.gif" onclick="cancellaTutto()" addParams="true"/>
        		<pt:sectionBody>                                  
                  <tr>
                    <td></td>
                    <td class="etichetta2" style="text-align:right"></td>
                    <td class="campo2" style="font-weight:bold"><%=row.getAttribute("strDescrizione")%></td>
                    <td class="etichetta2"></td>
                    <td class='pulsante_riga'>
                        <% if (Utils.notNull(row.getAttribute("prgPattoSceltaUnitaAzienda")).length()>0) {
                            // valorizzo la sequenza di argomenti per la funzione js che cancella tutte le righe
                            args.append(Utils.notNull(row.getAttribute("prgPattoSceltaUnitaAzienda")));
                            args.append(",");
                        %>
                        <script>allArgs+="<%=Utils.notNull(row.getAttribute("prgPattoSceltaUnitaAzienda"))%>,";</script>
                        <a  href="#" onclick="cancella('<%=row.getAttribute("prgPattoSceltaUnitaAzienda")%>')"><img src="../../img/del.gif" ></a>
                        <%}%>
                    </td>
                   </tr>
            </pt:sectionBody>
			</pt:dynamicSection>   
		</pt:sections>
        </td>
    </tr>

    <af:form method = "post" action="AdapterHTTP" onSubmit="updateStato(this)" dontValidate="true">
        <input type="hidden" name="prgAzienda" value="<%=prgAzienda%>">
        <input type="hidden" name="PAGE" value="PattoImpegniAziendaPage">      
        <input type="hidden" name="numklopattounitaaziendale" value="<%=numklopattounitaaziendale%>"> 
    </af:form>    
    <tr><td colspan="4"><br><br></td></tr>
    <tr>
        <td colspan=3>
            <table width="100%">
                <tr>
                    <td class="etichetta">Nota impegni</td>
                    <td class="campo"><af:textArea onKeyUp="fieldChanged();" name="STRNOTEIMPEGNI" value="<%=strNote%>" cols="60" rows="5" readonly="<%=readOnlyStr%>"/></td>
                    <td>
                    <% if (canModify&&notePatto!=null) { %>
                        <input type="submit" name="aggiornaNotePatto" class="pulsanti" value="Aggiorna note">
                    <%}%>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    </form>    
</table>

</center>

<br>

</body>
</html>

