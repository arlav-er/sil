<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ taglib uri="patto" prefix="pt" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.ProfileDataFilter,                   
                  it.eng.sil.util.*,
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
	SourceBean notePatto = (SourceBean)serviceResponse.getAttribute("NotePattoStoricizzato.ROWS.ROW");
	String cdnLavoratore= (String )notePatto.getAttribute("CDNLAVORATORE");
	String _current_page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _current_page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
  	PageAttribs pageAtts = new PageAttribs(user, _current_page);
	
	boolean canModify = false;
	
	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
    	boolean existsSalva = pageAtts.containsButton("aggiorna");
    	
    	if(!existsSalva){
    		canModify=false;
    	}else{
    		canModify=filter.canEditLavoratore();    	
    	}
    }
    canModify=false;
    String readOnlyStr   =canModify?"false":"true";
%>

<%
    String nomeCampo = "";
    /////////////////////////////////   
    String strNote = "";
    String prgPattoLavoratore = "";
    String numklopattolavoratore = "";
    if (notePatto!=null) {
        strNote = Utils.notNull(notePatto.getAttribute("strnoteimpegni"));
        prgPattoLavoratore = Utils.notNull(notePatto.getAttribute("prgPattoLavoratore"));
        numklopattolavoratore = String.valueOf(((BigDecimal)notePatto.getAttribute("numklopattolavoratore")).intValue()+1);
    }
    /////////////////////////////////
    Vector impegni= serviceResponse.getAttributeAsVector("PattoImpegniStorico.ROWS.ROW");
    ArrayList impegniLavoratore = new ArrayList();
    for (int i=0;i<impegni.size();i++) {
        SourceBean row = (SourceBean)impegni.get(i);
        String codiceImp = (String)row.getAttribute("codMonoImpegnoDi");
        if (Utils.notNull(codiceImp).equals("S"))
            impegniLavoratore.add(row);
    }
    impegni.removeAll(impegniLavoratore);
    ///////////////////////
    //String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
    StringBuffer args = new StringBuffer();
    String cdnFunzione = (String)serviceRequest.getAttribute("CDNFUNZIONE");
    String statoSezioni = (String)serviceRequest.getAttribute("statoSezioni");
    ////////////////////////
    PageProperties pageProperties = new PageProperties("PattoImpegnoPartiPage",null);
    PageAttribs pageAttsPattoAperto = new PageAttribs(user,"PattoImpegnoPartiPage");

    //
    pageContext.setAttribute("pageProperties", pageProperties);
    pageContext.setAttribute("sectionState",Utils.notNull(statoSezioni));
    pageContext.setAttribute("args", args);
    pageContext.setAttribute("isReadOnly", Boolean.valueOf(readOnlyStr));
    %>
    <%
    String htmlStreamTop = StyleUtils.roundTopTable(canModify);
    String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
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
//Array per la gestione delle note di ogni singolo impegno
var ogNote_Cod = new Array();
var ogNote_Nota = new Array();

var flagChanged = false;
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
	urlpage+="PAGE=PattoImpegnoPartiPage";
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
    urlpage+="cdnLavoratore=<%=cdnLavoratore %>&";
	urlpage+="CDNFUNZIONE=<%=cdnFunzione %>&";
    urlpage+="cancellaAssociazione=&";
    urlpage+="statoSezioni="+getStatoSezioni()+"&";
	urlpage+="PAGE=PattoImpegnoPartiPage";
    setWindowLocation(urlpage);
}
function apriTutte(page) {
    var urlpage="";
	urlpage+="AdapterHTTP?";
	urlpage+="cdnLavoratore=<%=cdnLavoratore %>&";
	urlpage+="CDNFUNZIONE=<%=cdnFunzione %>&";	
    urlpage+="COD_LST_TAB=DE_IMPE_L&";    
    urlpage+="COD_LST_TAB=DE_IMPE_C&";    
	urlpage+="statoSezioni="+getStatoSezioni()+"&";
    urlpage+="pageChiamante=PattoImpegnoPartiPage&";
	urlpage+="PAGE="+page;
	window.open(urlpage,"Associazioni", 'toolbar=0, scrollbars=1, resizable=1'); 
}
function apri(page, codLstTab) {
	var urlpage="";
	urlpage+="AdapterHTTP?";
	urlpage+="cdnLavoratore=<%=cdnLavoratore %>&";
	urlpage+="CDNFUNZIONE=<%=cdnFunzione %>&";
	urlpage+="COD_LST_TAB="+codLstTab+"&";
	urlpage+="statoSezioni="+getStatoSezioni()+"&";
	urlpage+="PAGE="+page+"&";
    urlpage+="pageChiamante=PattoImpegnoPartiPage";
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
            urlpage+="PRG_LAV_PATTO_SCELTA="+keysArray[i]+"&";       
        }
        urlpage+="cdnLavoratore=<%=cdnLavoratore %>&";
        urlpage+="CDNFUNZIONE=<%=cdnFunzione %>&";
        urlpage+="cancellaAssociazione=&";
        urlpage+="statoSezioni="+getStatoSezioni()+"&";
        urlpage+="PAGE=PattoImpegnoPartiPage";
        setWindowLocation(urlpage);
    }
}

function creaoggettoNote(obj){
  var i;
  var prg;
  var flgEs;
  if ((obj.value != "") || flagChanged){
    var indice = obj.name.indexOf("_");
    var indiceD = obj.name.indexOf("_",(indice+1));
    prg = obj.name.slice((indice+1),indiceD);
    flgEs = obj.name.slice((indiceD+1),obj.name.length);
    if (flgEs == "S"){
        if (isNaN(parseInt(obj.value,10))){
          alert("Non è stato inserito un valore numerico.");
          obj.value = "";
          //return false;
        }
    }
    i = ogNote_Cod.length;
    ogNote_Cod[i] = prg;
    //alert(ogNote_Cod.length + " - " + ogNote_Cod[i]);
    i = ogNote_Nota.length;
    ogNote_Nota[i] = obj.value;
    //alert(ogNote_Nota.length + " - " + ogNote_Nota[i]);
  }
}

function aggiornaTutto() {
	// Se la pagina è già in submit, ignoro questo nuovo invio!
	if (isInSubmit()) return;

    var maxNum = ogNote_Cod.length;
    var urlpage="";
    urlpage+="AdapterHTTP?";
    if ((ogNote_Cod.length>0) && (ogNote_Nota.length>0)){
      for (i=0;i<maxNum;i++) {
          urlpage += "PRGLAVPATTOSCELTA="+ogNote_Cod[i]+"&";
          urlpage += "STRALTREINF="+ogNote_Nota[i]+"&";
      }
    }
    urlpage+="cdnLavoratore=<%=cdnLavoratore %>&";
    urlpage+="CDNFUNZIONE=<%=cdnFunzione %>&";
    urlpage+="aggiornaNotePatto=&";
    urlpage+="STRNOTEIMPEGNI=" + document.Frm1.STRNOTEIMPEGNI.value + "&";
    urlpage+="PRGPATTOLAVORATORE=<%=prgPattoLavoratore %>&";
    urlpage+="NUMKLOPATTOLAVORATORE=<%=numklopattolavoratore %>&";
    urlpage+="statoSezioni="+getStatoSezioni()+"&";
    urlpage+="PAGE=PattoImpegnoPartiPage";
    //alert(urlpage);
    setWindowLocation(urlpage);
}

function fieldChanged() {
 <% if (!readOnlyStr.equalsIgnoreCase("true")){ %> 
    flagChanged = true;
 <%}%> 
}

function updateStato(thisIn) {
	thisIn.statoSezioni.value = getStatoSezioni();
	return true;
}

//-->
</script>

</head>
<body   class="gestione" onload="rinfresca()">
                
<%

 /*
 * stampa a video l' intestazione delle informazioni utente e 
 * della linguetta di navigazione
 */
User _user = (User) sessionContainer.getAttribute(User.USERID);
String _cdnFun = (String) serviceRequest.getAttribute("CDNFUNZIONE");
String _page = (String) serviceRequest.getAttribute("PAGE"); 
    
InfCorrentiLav _testata= new InfCorrentiLav(RequestContainer.getRequestContainer().getSessionContainer() ,cdnLavoratore, _user);
 
Linguette _linguetta = new Linguette(_user, Integer.parseInt(_cdnFun), _page, new BigDecimal(prgPattoLavoratore));   
_linguetta.setCodiceItem("prgPattoLavoratore");

_testata.setPaginaLista("PattoInformazioniStorichePage");

_testata.show(out);
_linguetta.show(out);

_user=null;
_cdnFun=null;
_page = null;
_testata=null;
_linguetta=null;

%>

<font color="red">
   <af:showErrors/>
</font>


<center>
<%out.print(htmlStreamTop);%>
  <table  class="main"  >
    <tr><td colspan=4>&nbsp;</td></tr>
    <%--
    <tr>
        <td colspan=4 class="bianco">
            <table width="100%">
                <tr>                
                    <td width="30%"><a href="#" onclick="chiudiSezioni()">Chiudi tutte</a></td> 
                    <td></td> 
                    <%if (canModify) {%>
                    <td align=right>
                        <a  href="#" onclick="apriTutte('AssociazioneAlPattoTemplatePage')"><img src="../../img/patto_ass.gif"></a>&nbsp;(associa informazioni al patto)
                    </td>
                    <td  align="right"><a href="#" onclick="cancellaProprioTutto()"><img src="../../img/patto_elim.gif"></a>&nbsp;(cancella tutti i legami)</td>            
                    <%}%>
                </tr>
            </table>
        </td>
    </tr>    
    --%>
    <tr><td colspan=4>&nbsp;</td></tr>
      <tr>
        <td colspan=4>
            <pt:sections pageAttribs="<%= pageAttsPattoAperto %>">
				<pt:dynamicSection name="DE_IMPE_L" titolo="Il lavoratore si impegna a" rows="<%=  impegniLavoratore %>">
        		<%--pt:sectionAction img="../../img/patto_ass.gif" onclick="apri(\"AssociazioneAlPattoTemplatePage\", \"DE_IMPE_L\")"/--%>
        		<%--pt:sectionAction img="../../img/patto_elim.gif" onclick="cancellaTutto()" addParams="true"/--%>
        		<pt:sectionBody>        
                  <tr>
                    <td></td>
                    <td class="etichetta2" style="text-align:right"><li></td>
                    <td class="campo2" style="font-weight:bold"><%=row.getAttribute("strDescrizione")%></td>
                    <td class="etichetta2" valign="top">
                      <script language="javascript">
                        <% 
                          nomeCampo = "STRALTREINF_" + row.getAttribute("prgLavPattoScelta") + "_" + row.getAttribute("FLGESITI");
                        %>
                      </script>
                      <af:textBox classNameBase="input" type="text" onKeyUp="javascript:fieldChanged();" name="<%=nomeCampo%>" title="Altre informazioni" required="false" size="30" maxlength="100" value="<%=Utils.notNull(row.getAttribute(\"STRALTREINF\"))%>" readonly="<%=String.valueOf(!canModify)%>" onBlur="javascript:creaoggettoNote(this);" />
                    </td>
                    <%--
                    <td class='pulsante_riga'>
                        <% if (Utils.notNull(row.getAttribute("prgLavPattoScelta")).length()>0) {
                            // valorizzo la sequenza di argomenti per la funzione js che cancella tutte le righe
                            args.append(Utils.notNull(row.getAttribute("prgLavPattoScelta")));
                            args.append(",");
                        %>
                        <script>allArgs+="<%=Utils.notNull(row.getAttribute("prgLavPattoScelta"))%>,";</script>
                          <%if (canModify){%>
                            <a  href="#" onclick="cancella('<%=row.getAttribute("prgLavPattoScelta")%>')"><img src="../../img/patto_elim.gif" ></a>
                        <%  }
                          }%>
                    </td>
                    --%>
                  </tr>
				</pt:sectionBody>
			</pt:dynamicSection>                  
        </td>
    </tr>
    <tr>
        <td colspan=4>
				<pt:dynamicSection name="DE_IMPE_C" titolo="Il Centro per l'Impiego si impegna a" rows="<%= impegni %>">
        		<%--pt:sectionAction img="../../img/patto_ass.gif" onclick="apri(\"AssociazioneAlPattoTemplatePage\", \"DE_IMPE_C\")"/--%>
        		<%--pt:sectionAction img="../../img/patto_elim.gif" onclick="cancellaTutto()" addParams="true"/--%>
        		<pt:sectionBody>                                  
                  <tr>
                    <td></td>
                    <td class="etichetta2" style="text-align:right"><li></td>
                    <td class="campo2" style="font-weight:bold"><%=row.getAttribute("strDescrizione")%></td>
                    <td class="etichetta2">
                      <script language="javascript">
                        <% 
                          nomeCampo = "STRALTREINF_" + row.getAttribute("prgLavPattoScelta") + "_" + row.getAttribute("FLGESITI");
                        %>
                      </script>
                      <af:textBox classNameBase="input" type="text" onKeyUp="javascript:fieldChanged();" name="<%=nomeCampo%>" title="Altre informazioni" required="false" size="30" maxlength="100" value="<%=Utils.notNull(row.getAttribute(\"STRALTREINF\"))%>" readonly="<%=String.valueOf(!canModify)%>" onBlur="javascript:creaoggettoNote(this);" />
                    </td>
                    <%--
                    <td class='pulsante_riga'>
                        <% if (Utils.notNull(row.getAttribute("prgLavPattoScelta")).length()>0) {
                            // valorizzo la sequenza di argomenti per la funzione js che cancella tutte le righe
                            args.append(Utils.notNull(row.getAttribute("prgLavPattoScelta")));
                            args.append(",");
                        %>
                        <script>allArgs+="<%=Utils.notNull(row.getAttribute("prgLavPattoScelta"))%>,";</script>
                          <%if (canModify){%>
                              <a  href="#" onclick="cancella('<%=row.getAttribute("prgLavPattoScelta")%>')"><img src="../../img/patto_elim.gif" ></a>
                        <%  }
                          }%>
                    </td>
                    --%>
                   </tr>
            </pt:sectionBody>
			</pt:dynamicSection>   
		</pt:sections>
        </td>
    </tr>

    <af:form method="post" name="Frm1" action="AdapterHTTP" dontValidate="true">
    <%--onSubmit="updateStato(this)"--%>
        <input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>">
        <input type="hidden" name="PAGE" value="PattoImpegnoPartiPage">
        <input type="hidden" name="statoSezioni" value="">
        <input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">        
        <input type="hidden" name="PRGPATTOLAVORATORE" value="<%=prgPattoLavoratore%>">        
        <input type="hidden" name="NUMKLOPATTOLAVORATORE" value="<%=numklopattolavoratore%>"> 
    <tr><td colspan="4"><br><br></td></tr>
    <tr>
        <td colspan=3>
            <table width="100%">
                <tr>
                    <td class="etichetta">Nota impegni</td>
                    <td class="campo"><af:textArea onKeyUp="javascript:fieldChanged();" name="STRNOTEIMPEGNI" 
                    value="<%=strNote%>" cols="60" rows="5" readonly="<%=readOnlyStr%>" classNameBase="textarea"/></td>
                    <td>
                    <% if (canModify&&notePatto!=null) { %>
                        <input type="button" name="aggiornaNotePatto" class="pulsanti" value="Aggiorna note" onClick="javascript:aggiornaTutto();">
                    <%}%>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    </af:form>   
</table>
<%out.print(htmlStreamBottom);%>
</center>

<br>

</body>
</html>
