<%@ page contentType="text/html;charset=utf-8"%>

<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  it.eng.afExt.utils.*,
                  com.engiweb.framework.security.*,
                  it.eng.sil.util.*, it.eng.sil.security.PageAttribs" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>
Associazioni al patto/accordo
</title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
<af:linkScript path="../../js/"/>

<style type="text/css">
img {border:0;width:18;heigth:18}
td.campo3 {font-weight: bold}
table.lista_ass{border-collapse:collapse; width: 90% }
tr.lista_ass {border: 1 solid #000080; color: #000080;  font-size: 12px;font-weight: normal;text-align: center;vertical-align:middle}
td.lista_ass {border: 1 solid;text-align:center}
td.lista_ass2 {border: 1 solid #000080; color: #000080;  font-size: 12px;font-weight: normal;text-align: center;vertical-align:middle}
</style>



<script>
<!--
var inputs = new Array(0);
var i=0;


/**
* Per le mansioni e' possibile, in via sperimentale, aggiungere una mansione direttamente dal template di associazione al patto
* sempre nello stesso frame.
*/
function aggiungi(page) {
    var urlpage="";
    urlpage+="AdapterHTTP?";
    urlpage+="cdnLavoratore="+<%=(String)serviceRequest.getAttribute("CDNLAVORATORE")%>+"&";
    // serve per il caricamento della linguetta
    urlpage+="CDNFUNZIONE="+<%=(String)serviceRequest.getAttribute("CDNFUNZIONE")%>+"&";
    // il parametro che attiva il modulo di associazione al patto
    <%
    /*
	    List p = new ArrayList();
	    Object o = serviceRequest.getAttribute("COD_LST_TAB");
	    List cods = null;
	    if (o instanceof Vector) cods = (List)o;
	    else {cods = new ArrayList(1);cods.add(o);}
	*/
	    List cods = getCodsLstTab(requestContainer.getServiceRequest());
	    for (int i=0;i<cods.size();i++) {
    %>
        urlpage+="COD_LST_TAB=<%=cods.get(i)%>&";
    <%} %>
            
    // serve per determinare se una sezione, al caricamento della pagina, va mostrata aperta o chiusa (cosi' come al momento della 
    //       chiamata di questa pagina)
    urlpage+="statoSezioni=<%=(String)serviceRequest.getAttribute("statoSezioni")%>&";
    urlpage+="pageChiamante=<%= serviceRequest.getAttribute("pageChiamante") %>&";
    urlpage+="PAGE="+page+"&";
    urlpage+="ONLY_INSERT=1";
    /*
    pp=window.open();
    pp.document.open("","dfijsdfjdsi");
    pp.document.write(urlpage);
    pp.document.close();
   // Se la pagina è già in submit, ignoro questo nuovo invio!
   //if (isInSubmit()) return;
   //setWindowLocation(urlpage);
   */
   window.open(urlpage,"_self");
   return false;
}
    /**
    * Cambia lo stato di selezione di una riga e della immagine corrispondente
    */
    function cambia(n){	
        if (inputs[n].selected) {
            inputs[n].selected=false;
            inputs[n].img.src=iDesel;
    //		inputsIndT[n].img.style.border="1 outset";
            inputs[n].img.style.border="0";
        }
        else {
            inputs[n].selected=true;
            inputs[n].img.src=iSel;
            inputs[n].img.style.border="1 inset";

        }
    }
    /**
    * Seleziona tutte le righe di quella tabella 
    */
	function tutti(el, cod) {
		for (j=0;j<inputs.length;j++) { 
            if (inputs[j].group==cod) {
                //inputs[j].checked=el.checked;
    			inputs[j].item.checked=el.checked;
                //inputs[j].img.style.border="1 inset";  
            }
		}
	}
    /** non lo utilizzo piu' dato che riesco a fare tutto con lo stato del checkbox che scatena l' evento
    * Deseleziona tutte le righe di quella tabella 
    */
    /*
	function nessuno(cod) {
		for (j=0;j<inputs.length;j++) { 
            if (inputs[j].group==cod) {
                inputs[j].selected=false;
                //inputs[j].img.src=iDesel;
                inputs[j].img.style.border="0 outset";
            }
		}
	}*/
	/**
    * Imposta i parametri della richiesta di associazione
    */
    function associa() {
    <%--
        var prgPattoLavoratore="<%= sessionContainer.getAttribute("PRGPATTOLAVORATORE")==null?"":"1"%>";
        if (prgPattoLavoratore=="") {
            alert('Non esiste nessun patto aperto');
            return;
        }
     --%>
        var urlpage="";
        urlpage+="AdapterHTTP?";        
        var nSel = 0;
        for (t=0;t<inputs.length;t++) {
            if (inputs[t].item.checked) {
                urlpage+="PRG_"+inputs[t].codLstTab +"="+ inputs[t].value+"&";
                nSel++;
            }
		}
        if (nSel==0) {
            alert("Nessuna riga selezionata");
            return;
        }
       
        // serve per il caricamento della linguetta
        <%
            if (serviceRequest.containsAttribute("CDNLAVORATORE")) {
        %>
        urlpage+="cdnLavoratore="+<%=(String)serviceRequest.getAttribute("CDNLAVORATORE")%>+"&";
        <%} else {%>
        urlpage+="prgAzienda="+<%=(String)serviceRequest.getAttribute("PRGAZIENDA")%>+"&";
        <%}%>
        // serve per il caricamento della linguetta
        urlpage+="CDNFUNZIONE="+<%=(String)serviceRequest.getAttribute("CDNFUNZIONE")%>+"&";
        // il parametro che attiva il modulo di associazione al patto
        urlpage+="inserisciAssociazione=&";
        // urlpage+="COD_LST_TAB=<%=serviceRequest.getAttribute("COD_LST_TAB")%>&";
        // serve per determinare il focus nella pagina di sisposta
        urlpage+="focusID=<%=(String)serviceRequest.getAttribute("focusID")%>&";
        // serve per determinare se una sezione, al caricamento della pagina, va mostrata aperta o chiusa (cosi' come al momento della 
        //       chiamata di questa pagina)
        urlpage+="statoSezioni=<%=(String)serviceRequest.getAttribute("statoSezioni")%>&";
        urlpage+="PAGE=<%= serviceRequest.getAttribute("pageChiamante") %>";     
        <%if (serviceRequest.containsAttribute("NONFILTRARE")) {%>    
        //Serve per fare in modo che non si filtri per data corrente nelle azioni, dopo l'associazione
        urlpage+="&NONFILTRARE=<%=serviceRequest.getAttribute("NONFILTRARE")%>";
        <%}%>

        <%//if(serviceRequest.getAttribute("COD_LST_TAB").toString().equals("OR_PER")){%>
        	//if (document.form1.datscadconferma.value != "") {
	            //var msg = "Si vuole aggiornare la data di scadenza del patto/accordo con la data stimata maggiore tra le\nazioni selezionate?";
	            //msg += "\n(OK = si prosegue con l'associazione aggiornando la data di scadenza del patto/accordo\nAnnulla = si prosegue con l'associazione senza aggiornare la data di scadenza del patto/accordo)";
	            //if(confirm(msg)){          
	              //urlpage += "&AGGIORNADATASCADPATTO=true";
	            //}
        	//}
        <%//}%>
        window.open(urlpage,"main");
        close();
	}
	
	/* Funzione per la chiusura della pagina di associazione */
	function chiudiPagina(){
		var urlpage="";
        urlpage+="AdapterHTTP?";        
        
       
        // serve per il caricamento della linguetta
        <%
            if (serviceRequest.containsAttribute("CDNLAVORATORE")) {
        %>
        urlpage+="cdnLavoratore="+<%=(String)serviceRequest.getAttribute("CDNLAVORATORE")%>+"&";
        <%} else {%>
        urlpage+="prgAzienda="+<%=(String)serviceRequest.getAttribute("PRGAZIENDA")%>+"&";
        <%}%>
        // serve per il caricamento della linguetta
        urlpage+="CDNFUNZIONE="+<%=(String)serviceRequest.getAttribute("CDNFUNZIONE")%>+"&";
        // urlpage+="COD_LST_TAB=<%=serviceRequest.getAttribute("COD_LST_TAB")%>&";
        // serve per determinare il focus nella pagina di sisposta
        urlpage+="focusID=<%=(String)serviceRequest.getAttribute("focusID")%>&";
        // serve per determinare se una sezione, al caricamento della pagina, va mostrata aperta o chiusa (cosi' come al momento della 
        //       chiamata di questa pagina)
        urlpage+="statoSezioni=<%=(String)serviceRequest.getAttribute("statoSezioni")%>&";
        urlpage+="PAGE=<%= serviceRequest.getAttribute("pageChiamante") %>";         
        <%if (serviceRequest.containsAttribute("NONFILTRARE")) {%>    
        //Serve per fare in modo che non si filtri per data corrente nelle azioni, dopo l'associazione
        urlpage+="&NONFILTRARE=<%=serviceRequest.getAttribute("NONFILTRARE")%>";
        <%}%>
        window.open(urlpage,"main");
        close();
	}
	
	
    /**
    * Classe che individua una riga di una table. 
    * Parametri: 
    *   codLstTab e' il COD_LST_TAB oramai famoso
    *   newValue valore della chiave della info che dovra' essere associata al patto
    *   newItem ------immagine da cambiare quando di selezione o deseleziona il pulsante ad essa associato
    *   flag stato iniziale della riga (true selezionata, false deselezionata). Default false
    */
    function Riga (newCodLstTab, newGroup, newValue,newItem, flag) {
        this.codLstTab = newCodLstTab; 
        this.group= newGroup;
		this.value =newValue;
		this.item=newItem;
        if (arguments.length==4) this.selected=false;
        else 
		this.selected = flag;
	}
//-->
</script>

</head>
<body class="gestione">
<br>
<center>
<p class=titolo>Associazione al Patto/Accordo</p><br>
<af:form name="form1" action="" onSubmit="false" dontValidate="true">
<%-- 
    leggo i codici delle tabelle di cui bisogna fare la associazione
    poi eseguo un bel ciclo for e faccio le include. Ho bisogno di un metodo che mi permetta di fare questo controllo
--%>
<% 
/*
********* NON PIU' UTILIZZATO GESTISCO  GLI ID DEGLI OGGETTI IN MODO DIVERSO ********
BigInteger count = new BigInteger("0");
request.setAttribute("COUNT_IMG", count);
*/ 
String[] jspPages = getPages(requestContainer.getServiceRequest());
SourceBean sb = (SourceBean)serviceResponse.getAttribute("M_GETTABELLENONLEGATEALPATTO");
SourceBean pattoAperto = (SourceBean)sb.getAttribute("PATTO_APERTO.ROWS");

// passo il sessionContainer alle pagine incluse perche' potrebbero avere bisogno di accedere all'oggetto user
// necessario per ottenere i permessi della profilatura della pagina
request.setAttribute("SESSION_CONTAINER", sessionContainer );
//
request.setAttribute("SOURCE_BEAN", sb);
request.setAttribute("SERVICE_REQUEST", serviceRequest);
for (int i=0;i<jspPages.length;i++) { %>
    <jsp:include page="<%=jspPages[i]%>" flush="true"/>
    <br><br>
<%} %>

<% 
String datscadconferma = "";
if (pattoAperto!=null) {
	if (pattoAperto.containsAttribute("row.datscadconferma")) {
		datscadconferma = SourceBeanUtils.getAttrStrNotNull(pattoAperto, "row.datscadconferma");
	}
%>
<script>if (inputs.length>0) document.write('<input type="button" class=pulsanti onclick="associa()" value="associa al patto/accordo">');</script>
<%
}
%>
<input type="hidden" name="datscadconferma" value="<%=datscadconferma%>">
<input type="button" class="pulsante" name="Chiudi" value="Chiudi" onClick="javascript: chiudiPagina();" >
</center>

</af:form>

</body>
</html>

<%!
/**
* restituisce un array di stringhe contenenti il nome delle pagine jsp da includere nella pagina
*/
private String[] getPages(SourceBean request) {
/*
    List p = new ArrayList();
    Object o = request.getAttribute("COD_LST_TAB");
    List cods = null;
    if (o instanceof Vector) cods = (List)o;
    else {cods = new ArrayList(1);cods.add(o);}
 */

    List cods = getCodsLstTab(request);
    List pageDaCaricare = new ArrayList(cods.size());
    for (int i=0;i<cods.size();i++) {
        pageDaCaricare.add(map.get(cods.get(i)));
    }
    return (String [])pageDaCaricare.toArray(new String[0]);
}
/**
* Restituisce una List di codici cod_lst_tab passati alla http request
*/
private List getCodsLstTab(SourceBean request) {
	List p = new ArrayList();
    Object o = request.getAttribute("COD_LST_TAB");
    List cods = null;
    if (o instanceof Vector) cods = (List)o;
    else {
    	cods = new ArrayList(1);
    	cods.add(o);
    }
    return cods;
}
/*
* Mappa di associazione tra un codice cod_lst_tab e la pagina jsp da includere nel template
*/
private static Map map=new HashMap(5);
static {
    map.put("AM_IND_T","_amIndTDaAssociare.jsp");
    map.put("AM_CM_IS","_amCmIsDaAssociare.jsp");
    map.put("PR_STU","_prStuDaAssociare.jsp");
    map.put("PR_ESP_L","_prEspLDaAssociare.jsp");
    map.put("PR_COR","_prCorDaAssociare.jsp");
    map.put("PR_MAN","_prManDaAssociare.jsp");
    map.put("AM_MB_IS","_amMobilitaIscrDaAssociare.jsp");
    map.put("AM_EX_PS","_amExPSoggiornoDaAssociare.jsp");
    map.put("AM_OBBFO","_amObbligoFormativoDaAssociare.jsp");
    map.put("AM_EX_PS","_amExPermessoSoggiornoDaAssociare.jsp");
    map.put("PR_IND","_prIndisponibilitaDaAssociare.jsp");
    map.put("DE_IMPE_L","_deImpegniLavoratoreDaAssociare.jsp");
    map.put("DE_IMPE_C","_deImpegniCpiDaAssociare.jsp");  
    map.put("AG_LAV","_agLavAppuntamentiDaAssociare.jsp");
    map.put("OR_PER","_orPercorsoConcordatoDaAssociare.jsp");
    map.put("DE_IMPE_C_AZ","_deImpegniCpiDaAssociareAzienda.jsp");
    map.put("DE_IMPE_AZ","_deImpegniAziendaDaAssociare.jsp");
}
%>