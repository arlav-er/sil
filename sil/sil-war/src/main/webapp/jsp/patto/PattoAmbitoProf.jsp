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
                  it.eng.afExt.utils.*,
                  java.util.*,
                  java.math.*,
                  java.io.*, 
                  com.engiweb.framework.security.*,
                  com.engiweb.framework.configuration.ConfigSingleton,
                  java.text.*,it.eng.sil.util.patto.PageProperties,it.eng.sil.security.PageAttribs
                  "   %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String cdnLavoratore= (String )serviceRequest.getAttribute("CDNLAVORATORE");
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
%>

<%
    String cdnFunzione = (String)serviceRequest.getAttribute("CDNFUNZIONE");
    //String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");
    String statoSezioni = (String)serviceRequest.getAttribute("statoSezioni");    
    StringBuffer args = new StringBuffer();
//    pageContext.setAttribute("prgSection", BigInteger.ZERO);
    ///////////////////////////////
    Vector mansioniRows= serviceResponse.getAttributeAsVector("M_ElencoMansioni.ROWS.ROW");
    ///////////////////////////////
    Vector elencoIndisponibilita = serviceResponse.getAttributeAsVector("M_ElencoIndisponibilita.ROWS.ROW");
    ///////////////////////////////
    SourceBean notePatto = (SourceBean)serviceResponse.getAttribute("M_PATTOAPERTONOTE.ROWS.ROW");
    String strNote = "";
    String prgPattoLavoratore = "";
    String numklopattolavoratore = "";
    String codTipoPatto = "";
    if (notePatto!=null) {
        strNote = Utils.notNull(notePatto.getAttribute("strnoteambitoprof"));
        prgPattoLavoratore = Utils.notNull(notePatto.getAttribute("prgPattoLavoratore"));
    	codTipoPatto = Utils.notNull(notePatto.getAttribute("codTipoPatto"));
        numklopattolavoratore = String.valueOf(((BigDecimal)notePatto.getAttribute("numklopattolavoratore")).intValue()+1);
    }
    
    String[] tipi_patti_check = {MessageCodes.General.PATTO_L14, MessageCodes.General.PATTO_DOTE, MessageCodes.General.PATTO_DOTE_IA}; 
    
	if (Arrays.asList(tipi_patti_check).contains(codTipoPatto)) {
    	canModify = false;
    }    	
    String readOnlyStr = canModify ? "false" : "true";   
    
    ////////////////////////
    PageProperties pageProperties = new PageProperties((String) serviceRequest.getAttribute("PAGE"),null);
    /*PageAttribs pageAtts = new PageAttribs((User) sessionContainer.getAttribute(User.USERID),(String) serviceRequest.getAttribute("PAGE"));
    boolean canModify = pageAtts.containsButton("aggiorna");
    String readOnlyStr   =canModify?"false":"true";*/
    //
    pageContext.setAttribute("pageProperties", pageProperties);
    pageContext.setAttribute("sectionState",Utils.notNull(statoSezioni));
    pageContext.setAttribute("args", args);
    pageContext.setAttribute("isReadOnly", Boolean.valueOf(readOnlyStr));
%>

        <%

        SourceBean row_Mansioni= null;
        //
        String Mansione= null;
        String esperienza= null;
        String Misura_concordata= null;          
        String Contratti= null;                    
        String Orario= null;                              
        String Turni= null;
        String Territorio= null;
        String Comuni= null;          
        String Province= null;                    
        String Regioni= null;                              
        String Stato= null;                
        String mansioneATempo = null;
        String disponibile = null;
        String formazione = null;
        String inserimentoProfessionale = null;
        //          String indisponibilita = null;
        // variabili mobilita' geografica
        String utilizzoAuto = null;
        String utilizzoMoto = null;
        String pendolarismoGiornaliero = null;
        String mobilitaSett = null;
        String tipoTrasferta = null;
        String durataPercorrenzaMax = null;
        String MansionePrec=null;
        
        //
         
        boolean nuovaMansione=false;
        ArrayList mansioni=new ArrayList();
        SourceBean  rowBean = new SourceBean("ROW");
        if (mansioniRows.size()>0) {            
            MansionePrec= (String)((SourceBean) mansioniRows.elementAt(0)).getAttribute("MANSIONE");     
        }
        ArrayList contratti = new ArrayList();
        ArrayList orari = new ArrayList();
        ArrayList turni = new ArrayList();
        ArrayList comuni = new ArrayList();
        ArrayList provincie = new ArrayList();
        ArrayList regioni = new ArrayList();
        ArrayList stati = new ArrayList();                 
        //String MansionePrec=null;
        MobilitaGeoWrapper geo = new MobilitaGeoWrapper();%><%--
        // int duegiri=2;
        boolean stampa = true;
        for(int n=0; n<mansioniRows.size(); n++)  { 
        // if (mansioniRows.size()>0) {                                    
            row_Mansioni = (SourceBean) mansioniRows.elementAt(n);                                      
            int ordine = ((BigDecimal)row_Mansioni.getAttribute("ORDINE")).intValue();
        //                    if (ordine==1) {
        //                      MansionePrec=(String)row_Mansioni.getAttribute("MANSIONE");
        //                }

                  switch (ordine) {                            
                    case 2:
                      contratti.add(row_Mansioni.getAttribute("VINCOLO"));
                      if (mansioniRows.size()==n+1) {//MansionePrec = "";ordine=1;
                            stampa=false;
                            row_Mansioni.updAttribute("ORDINE", new BigDecimal("1"));
                            row_Mansioni.updAttribute("MANSIONE", "");
                            n--;
                        }
                        break;
                    case 3:
                      orari.add(row_Mansioni.getAttribute("VINCOLO"));
                      if (mansioniRows.size()==n+1) {//MansionePrec = "";ordine=1;
                            stampa=false;
                            row_Mansioni.updAttribute("ORDINE", new BigDecimal("1"));
                            row_Mansioni.updAttribute("MANSIONE", "");
                            n--;
                        }
                        break;
                    case 4:
                      turni.add(row_Mansioni.getAttribute("VINCOLO"));
                      if (mansioniRows.size()==n+1) {//MansionePrec = "";ordine=1;
                            stampa=false;
                            row_Mansioni.updAttribute("ORDINE", new BigDecimal("1"));
                            row_Mansioni.updAttribute("MANSIONE", "");
                            n--;
                        }
                        break;
                    case 5:
                      comuni.add(row_Mansioni.getAttribute("VINCOLO"));
                      if (mansioniRows.size()==n+1) {//MansionePrec = "";ordine=1;
                            stampa=false;
                            row_Mansioni.updAttribute("ORDINE", new BigDecimal("1"));
                            row_Mansioni.updAttribute("MANSIONE", "");
                            n--;
                        }
                        break;
                    case 6:
                      provincie.add(row_Mansioni.getAttribute("VINCOLO"));
                      if (mansioniRows.size()==n+1) {//MansionePrec = "";ordine=1;
                            stampa=false;
                            row_Mansioni.updAttribute("ORDINE", new BigDecimal("1"));
                            row_Mansioni.updAttribute("MANSIONE", "");
                            n--;
                        }
                        break;
                    case 7:
                      regioni.add(row_Mansioni.getAttribute("VINCOLO"));
                      if (mansioniRows.size()==n+1) {//MansionePrec = "";ordine=1;
                            stampa=false;
                            row_Mansioni.updAttribute("ORDINE", new BigDecimal("1"));
                            row_Mansioni.updAttribute("MANSIONE", "");
                            n--;
                        }
                        break;
                    case 8:
                      stati.add(row_Mansioni.getAttribute("VINCOLO"));
                      if (mansioniRows.size()==n+1) {//MansionePrec = "";ordine=1;
                            stampa=false;
                            row_Mansioni.updAttribute("ORDINE", new BigDecimal("1"));
                            row_Mansioni.updAttribute("MANSIONE", "");
                            n--;
                        }
                        break;
                    case 9:
                      //
                        utilizzoAuto = (String)row_Mansioni.getAttribute("FLGDISPAUTO");
                        utilizzoAuto = (utilizzoAuto==null)? "&nbsp;":utilizzoAuto;
                        utilizzoMoto = (String)row_Mansioni.getAttribute("flgDispMoto");
                        utilizzoMoto = (utilizzoMoto==null)? "&nbsp;":utilizzoMoto;
                        pendolarismoGiornaliero = (String)row_Mansioni.getAttribute("flgPendolarismo");
                        pendolarismoGiornaliero = (pendolarismoGiornaliero==null)? "&nbsp;":pendolarismoGiornaliero;
                        mobilitaSett = (String)row_Mansioni.getAttribute("FLGMOBSETT");
                        mobilitaSett = (mobilitaSett==null)? "&nbsp;":mobilitaSett;
                        tipoTrasferta = (String)row_Mansioni.getAttribute("VINCOLO");
                        durataPercorrenzaMax =Utils.notNull(row_Mansioni.getAttribute("NUMOREPERC"));
                        durataPercorrenzaMax = (durataPercorrenzaMax==null)? "":durataPercorrenzaMax;
                        //
                        geo.pendGiornaliero=pendolarismoGiornaliero;
                        geo.percentuale=durataPercorrenzaMax;
                        geo.tipoTrasferta=tipoTrasferta;
                        geo.utilizzoAuto=utilizzoAuto;
                        geo.utilizzoMoto=utilizzoMoto;
                        geo.mobSett=mobilitaSett;
                        if (mansioniRows.size()!=n+1) 
                                break;
                        else {MansionePrec = "";ordine=1;stampa=false;}
                        // se sono alla fine del ciclo debbo registrare le info accumulate
                        // otterro' che la mansione letta e quella precedente saranno diverse
                     case 1:
                        Mansione = (String)row_Mansioni.getAttribute("MANSIONE");
                        if (!Mansione.equals(MansionePrec)) {                                        
                                rowBean.setAttribute("contratti", contratti);
                                rowBean.setAttribute("orari", orari);
                                rowBean.setAttribute("turni", turni);
                                rowBean.setAttribute("comuni", comuni);
                                rowBean.setAttribute("provincie", provincie);
                                rowBean.setAttribute("regioni",regioni);
                                rowBean.setAttribute("stati", stati);
                                rowBean.setAttribute("mobilitaGeo", geo);
                                        
                                mansioni.add(rowBean);
                                //
                                MansionePrec=Mansione;
                                        
                        }
                        mansioneATempo = (String)row_Mansioni.getAttribute("TEMPO");                                        
                       // mansioneATempo = (mansioneATempo.equals(""))? "9" :mansioneATempo;
                        if (mansioneATempo!=null) {
                            switch (mansioneATempo.charAt(0)) {
                                case 'D':
                                    mansioneATempo = "Tempo determinato";
                                    break;
                                case 'I':
                                    mansioneATempo = "Tempo indeterminato";
                                    break;
                                case 'E':
                                    mansioneATempo = "Tempo det/indet";
                                    break;                                               
                            }
                        }
                        esperienza=Utils.notNull(row_Mansioni.getAttribute("ESPERIENZA"));
                        disponibile = (String)row_Mansioni.getAttribute("FLGDISPONIBILE");
                        inserimentoProfessionale = (String)row_Mansioni.getAttribute("FLGPIP");
                        formazione = (String)row_Mansioni.getAttribute("FLGDISPFORMAZIONE");
                        String prgMansione = row_Mansioni.getAttribute("prgMansione").toString();
                        String prgLavPattoScelta =  row_Mansioni.getAttribute("prgLavPattoScelta").toString();
                        ArrayList al  = new ArrayList(3);
                        if (inserimentoProfessionale!=null) al.add("Progetti di inserimento");
                        if (formazione!=null) al.add("Percorsi formativi");
                        if (disponibile!=null) al.add("Attività lavorativa");
                        rowBean= new SourceBean("ROW");
                        rowBean.setAttribute("mansioneATempo", Utils.notNull(mansioneATempo));
                        rowBean.setAttribute("esperienza",esperienza);
                        rowBean.setAttribute("mansione", Mansione);
                        rowBean.setAttribute("prgLavPattoScelta", prgLavPattoScelta);
                        rowBean.setAttribute("prgMansione",prgMansione);
                        rowBean.setAttribute("misura",al);
                          // inizializza arrays      
                        contratti=new ArrayList();
                        orari = new ArrayList();
                        turni= new ArrayList();
                        comuni = new ArrayList();
                        provincie = new ArrayList();
                        regioni = new ArrayList();
                        stati =new ArrayList();
                        geo = new MobilitaGeoWrapper();
                        // caso particolare in cui siamo nell' ultima mansione ed un solo ordine=1
                        if (mansioniRows.size()==n+1 && stampa) {
                            rowBean.setAttribute("contratti", contratti);
                            rowBean.setAttribute("orari", orari);
                            rowBean.setAttribute("turni", turni);
                            rowBean.setAttribute("comuni", comuni);
                            rowBean.setAttribute("provincie", provincie);
                            rowBean.setAttribute("regioni",regioni);
                            rowBean.setAttribute("stati", stati);
                            rowBean.setAttribute("mobilitaGeo", geo);
                            mansioni.add(rowBean);
                        }
                        break;
                    default: // per il momento non fare niente. Una situazione del genere non deve verificarsi
                 }// switch                            
             }
      --%>
<%
String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
%>
<html>
<head>
<title>Azioni</title> 
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
<af:linkScript path="../../js/"/>
<SCRIPT TYPE="text/javascript">
<!--



    // *****************************
  // Serve per il menu contestuale
  // *****************************
  function caricaMenuDX(cdnLavoratore, strNome, strCognome){

    var url = "AdapterHTTP?PAGE=MenuCompletoPage"
    url += "&CDNLAVORATORE=" + cdnLavoratore;
    url += "&STRNOME=" + strNome;
    url += "&STRCOGNOME=" + strCognome;

    window.top.menu.location = url;
  } 
//-->
</SCRIPT>

   <script language="JavaScript">
        // Rilevazione Modifiche da parte dell'utente
        var flagChanged = false;
        
        function fieldChanged() {
         <% if (!readOnlyStr.equalsIgnoreCase("true")){ %> 
            flagChanged = true;
         <%}%> 
        }
    </script>


<script>
<!--
var flagChanged = false;
var sezioni = new Array();

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
	urlpage+="PAGE=PattoAmbitoProfLinguettaPage";
	setWindowLocation(urlpage);
}

function cancellaTutto() {
    // Se la pagina è già in submit, ignoro questo nuovo invio!
    if (isInSubmit()) return;

    if (!confirm("Cancellare la associazione?")) return;
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
	urlpage+="PAGE=PattoAmbitoProfLinguettaPage";
    setWindowLocation(urlpage);
}

function apriTutte(page) {
    var urlpage="";
	urlpage+="AdapterHTTP?";
	urlpage+="cdnLavoratore=<%=cdnLavoratore %>&";
	urlpage+="CDNFUNZIONE=<%=cdnFunzione %>&";
	urlpage+="COD_LST_TAB=PR_MAN&";
    urlpage+="COD_LST_TAB=PR_IND&";
	urlpage+="statoSezioni="+getStatoSezioni()+"&";
    urlpage+="pageChiamante=PattoAmbitoProfLinguettaPage&";
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
    urlpage+="pageChiamante=PattoAmbitoProfLinguettaPage";
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
        urlpage+="PAGE=PattoAmbitoProfLinguettaPage";
        setWindowLocation(urlpage);
    }
}

function aggiorna() {
    var urlpage ="AdapterHTTP?";
    urlpage+="CDNFUNZIONE=<%=cdnFunzione %>&";
    urlpage+="PAGE=PattoAmbitoProfLinguettaPage&";
    urlpage+="cdnLavoratore=<%=cdnLavoratore %>&";
    urlpage+="MODULE=";
}

function updateStato(thisIn) {
	thisIn.statoSezioni.value = getStatoSezioni();
	return true;
}

//-->
</script>

<script language="Javascript">
//Per la visualizzazione dei links nel footer
<% pageAtts.showHyperLinks(out, requestContainer, responseContainer,"cdnLavoratore=" + cdnLavoratore); %>
</script>

<style>
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

<% mansioni = getMansioni(mansioniRows);%>
<center>
<%out.print(htmlStreamTop);%>
<table  class="main" >
    <tr><td colspan="3">&nbsp;</td></tr>
    <tr>
        <td colspan=3 class="bianco">
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
    <tr><td colspan="3">&nbsp;</td></tr>
    <tr><td colspan=3>
    <pt:sections pageAttribs="<%= pageAtts %>">
	<pt:dynamicSection name="PR_MAN" titolo="Mansione" rows="<%= mansioni %>">
        <pt:sectionAction img="../../img/patto_ass.gif" onclick="apri(\"AssociazioneAlPattoTemplatePage\", \"PR_MAN\")"/>
        <pt:sectionAction img="../../img/patto_elim.gif" onclick="cancellaTutto()" addParams="true"/>
        <pt:sectionBody>        
        
            <table width="100%" border=0 style="background:white">
                    <tr>
                        <td>
                          <%
                            esperienza = (String)row.getAttribute("esperienza");
                            mansioneATempo = (String)row.getAttribute("mansioneATempo");
                            List misure = (List)row.getAttribute("misura");
                            String mansione = (String)row.getAttribute("mansione");
                            String prgLavPattoScelta = (String)row.getAttribute("prgLavPattoScelta");
                            String prgMansione = (String)row.getAttribute("prgMansione");
                            String dataProtocollo = (String)row.getAttribute("datProtocollo");
                            
                            if (mansione!=null) {
                          %>
                            <table cellspacing=0 cellpadding=0 border=0 width="100%">
                            	<tr><td  class="etichetta2">Data protocollo &nbsp;</td>
                            		<td class="campo2" style="font-weight:bold"><%=Utils.notNull(dataProtocollo)%></td>
                            		<td colspan="3"></td></tr>
                                <tr>
                                    <td class="etichetta2" style="text-align:right;width:170">Mansione &nbsp;</td>
                                    <td class="campo2" style="width:50%;font-weight:bold"><%= mansione  %></td>               
                                    <td class="etichetta2">Esperienza&nbsp;</td>
                                    <td  class="campo2" style="font-weight:bold"><%= esperienza.equals("E")?"ND":esperienza  %></td>
                                    <td class='pulsante_riga'>
                                    <% if (Utils.notNull(prgLavPattoScelta).length()>0) {
                                        // valorizzo la sequenza di argomenti per la funzione js che cancella tutte le righe
                                        args.append(Utils.notNull(prgLavPattoScelta));
                                        args.append(",");
                                    %>
                                    <script>allArgs+="<%=Utils.notNull(prgLavPattoScelta)%>,";</script>
                                      <%if (canModify) {%>
                                        <a  href="#" onclick="cancella('<%=prgLavPattoScelta%>')"><img src="../../img/patto_elim.gif" ></a>
                                    <%  }
                                      }%>
                                    </td>
                                </tr>  
                                <% if (mansioneATempo!=null) {%>
                                <tr>        
                                    <td  class="etichetta2">Mansione a tempo &nbsp;</td>
                                    <td class="campo2" style="font-weight:bold"><%= mansioneATempo %></td>
                                    <td>&nbsp;</td>
                                    <td>&nbsp;</td>        
                                    <td>&nbsp;</td>                                            
                                </tr>
                              <% } // if mansioneATempo %>
                              <% //if (inserimentoProfessionale!=null && formazione!=null && disponibile!=null){ 

                              %>
                                <tr><td colspan="5"></td></tr>                      
                              <%
                                   boolean first = true;
                              %>
                              
                                <pt:coppia titolo="Misura concordata&nbsp;" nome="" valori="<%=misure%>"  sezioneVuota="<%=false%>">
                                    <tr>
                                        <td class="etichetta2" style="vertical-align:middle"><%=_titolo%>&nbsp;</td>
                                        <td class=campo2 style="font-weight:bold;left:10"><%=_campo%>&nbsp;</td>                                                                          
                                        <td class="etichetta2" style="text-align:left;color:blue"><% if (first && !_campo.equals("")) { first=false; %>
                                            <a href="#" onclick="alert('Funzione non implementata')">dettaglio</a>
                                            <%}%>
                                        </td>
                                        <td>&nbsp;</td>
                                        <td>&nbsp;</td>
                                    </tr>
                                </pt:coppia>
                              <%//}%>
                            </table>
                            <%}//mansione!=null
                                else {
                                // messaggio a video che non ce'e nessuna mansione
                            %>
                            <table cellspacing=0 cellpadding=0 border=0>
                              <tr>
                                <td class="etichetta2" style="text-align:right;width:170">Mansione &nbsp;</td>
                                <td class="campo2" style="width:50%;font-weight:bold"></td>               
                                <td class="etichetta2">&nbsp;</td>
                                <td  class="campo2" style="font-weight:bold"></td>
                              </tr>
                                
                            <%} // nessuna mansione %>
                          </td>
                    </tr>        
                    <tr><td>&nbsp;</td></tr>
                    <tr>
                        <td>
                            <table border=0 cellpadding=1 cellspacing=0>
                                <col  style="width:17">
                                <col  width="59">
                                <col class="etichetta2" style="text-align:right">
                                <col  class="campo2" style="font-weight:bold;text-indent:10">
                                <col>
                                <tr>
                                <%
                                    contratti = (ArrayList)row.getAttribute("contratti");
                                    orari = (ArrayList)row.getAttribute("orari");
                                    turni = (ArrayList)row.getAttribute("turni");
                                %>
                                    <td>&nbsp;</td>
                                    <td colspan=2  class="etichetta2" style="text-align:left;font-weight:bold">Disponibilita' &nbsp;</td>
                                    <td>&nbsp;</td>
                                    <td>&nbsp;</td>
                                    <td>&nbsp;</td>
                                </tr>                                      
                                <pt:coppia titolo="Contratti&nbsp;" nome="PR_D_CON" valori="<%=contratti%>" >
                                <tr>
                                    <td></td>
                                    <td>&nbsp;</td>
                                    <td style="vertical-align:top"><%=_titolo %></td>
                                    <td><%= _campo %></td>
                                    <td>&nbsp;</td>
                                    <td>&nbsp;</td>
                                </tr>
                                </pt:coppia>
                                <pt:coppia titolo="Orario&nbsp;" nome="PR_D_ORA" valori="<%=orari%>" >
                                <tr>
                                    <td></td>
                                    <td>&nbsp;</td>
                                    <td style="vertical-align:top"><%=_titolo %></td>
                                    <td><%= _campo %></td>
                                    <td>&nbsp;</td>
                                    <td>&nbsp;</td>
                                </tr>
                                </pt:coppia>
                                <pt:coppia titolo="Turni&nbsp;" nome="PR_D_TUR" valori="<%=turni%>" >
                                    <tr>
                                        <td></td>
                                        <td>&nbsp;</td>
                                        <td style="vertical-align:top"><%=_titolo %></td>
                                        <td><%= _campo %></td>
                                        <td>&nbsp;</td>
                                        <td>&nbsp;</td>
                                    </tr>
                                </pt:coppia>                                    
                                <tr>    
                                    <td style="width:17"></td>
                                      <td  width="59">&nbsp;</td>
                                       <td  class="etichetta2" style="font-weight:bold">Territorio  &nbsp;</td>
                                       <td>&nbsp;</td>
                                       <td>&nbsp;</td>
                                       <td>&nbsp;</td>
                                 </tr>                                      
                            </table>    
                        </td>
                    </tr>  
                    <tr>
                          <td>
                                <table>
                                <%
                                    comuni = (ArrayList)row.getAttribute("comuni");
                                    provincie = (ArrayList)row.getAttribute("provincie");
                                    regioni = (ArrayList)row.getAttribute("regioni");
                                    stati = (ArrayList)row.getAttribute("stati");
                                %>
                                <col width="110">
                                <col class="etichetta2" style="vertical-align:top; text-align:right">
                                <col class="campo" style="font-weight:bold;padding-left:30">                                                                         
                                <pt:coppia titolo="Comune&nbsp;" nome="PR_D_COM" valori="<%=comuni%>" >
                                    <tr>
                                        <td >&nbsp;</td>
                                        <td style="vertical-align:top"><%=_titolo %></td>
                                        <td><%= _campo %></td>
                                    </tr>
                                </pt:coppia>
                                 <pt:coppia titolo="Province&nbsp;" nome="PR_D_PRO" valori="<%=provincie%>" >
                                    <tr>
                                        <td >&nbsp;</td>
                                        <td style="vertical-align:top"><%=_titolo %></td>
                                        <td><%= _campo %></td>
                                    </tr>
                                </pt:coppia>
                                <pt:coppia titolo="Regioni&nbsp;" nome="PR_D_REG" valori="<%=regioni%>" >
                                    <tr>
                                        <td >&nbsp;</td>
                                        <td style="vertical-align:top"><%=_titolo %></td>
                                        <td><%= _campo %></td>
                                    </tr>
                                </pt:coppia>
                                <pt:coppia titolo="Stati&nbsp;" nome="PR_D_STA" valori="<%=stati%>" >
                                    <tr>
                                        <td >&nbsp;</td>
                                        <td style="vertical-align:top"><%=_titolo %></td>
                                        <td><%= _campo %></td>
                                    </tr>
                                </pt:coppia>  
                            </table>  
                          </td>    
                    </tr>                          
                    <tr>
                        <td>
                            <table border=0 width="170">
                                <tr>
                                    <td  class="etichetta2" style="text-align:right;font-weight:bold">Mobilità geografica &nbsp;</td>                                
                                </tr>
                            </table>
                        </td>
                      </tr>
                        <%
                        geo = (MobilitaGeoWrapper)row.getAttribute("mobilitaGeo");
                        if (mansione!=null && geo.utilizzoMoto!=null) {
                          %>
                        <tr>    
                          <td>
                            <table border=0>                            
                              <tr>
                                <td class="etichetta2" style="width:170">Utilizzo automobile &nbsp;</td>
                                <td class="campo2" style="width:35%;font-weight:bold"><%= geo.utilizzoAuto %></td>
                                <td width="10">&nbsp;</td>
                                <td class="etichetta2">Utilizzo  motoveicolo &nbsp;</td>
                                <td  class="campo2" style="font-weight:bold"><%= geo.utilizzoMoto %></td>
                              </tr>                              
                              <tr>
                                <td class="etichetta2">Pend. giornaliero  &nbsp;</td>
                                <td class="campo2"  style="font-weight:bold"><%= geo.pendGiornaliero %></td>
                                <td>&nbsp;</td>
                                <td class="etichetta2">Durata di perc. max  &nbsp;</td>
                                <td class="campo2"><font  style="font-weight:bold"><%=geo.percentuale%></font>&nbsp;ore</td>
                              </tr>
                              <tr>
                                <td class="etichetta2">Mob. settimanale &nbsp;</td>
                                <td  class="campo2"  style="font-weight:bold"><%= geo.mobSett %></td>                                
                                <td>&nbsp;</td>
                                <td class="etichetta2">&nbsp;</td>
                                <td  class="campo2"  style="font-weight:bold">&nbsp;</td>
                              </tr>                              
                              <tr>
                                <td class="etichetta2">Tipo di trasferta  &nbsp;</td>
                                <td class="campo2"  style="font-weight:bold"><%= geo.tipoTrasferta %></td>
                                <td>&nbsp;</td>
                                <td>&nbsp;</td>
                                <td>&nbsp;</td>
                              </tr>
                            </table>
                          </td>
                    </tr> 
                    <%}%>
                  </table>
                  <br>
            </pt:sectionBody>
            </pt:dynamicSection>
            
             <%--
        <% if (i+1<mansioni.size()) { %>
        <tr><td><hr style="border:1 solid blue;width:80%; text-align:left"></td></tr>
        <%}%>--%>
    </td></tr>
    <tr><td colspan=3>
        <pt:dynamicSection name="PR_IND" titolo="Indisponibilità presso Imprese" rows="<%= elencoIndisponibilita %>">
        <pt:sectionAction img="../../img/patto_ass.gif" onclick="apri(\"AssociazioneAlPattoTemplatePage\", \"PR_IND\")"/>
        <pt:sectionAction img="../../img/patto_elim.gif" onclick="cancellaTutto()" addParams="true"/>
        <pt:sectionBody>        
            <table width="100%"> 
            <tr>
            	<td width="100">&nbsp;</td><td class="campo2" style="font-weight:bold"><%= Utils.notNull(row.getAttribute("STRRAGSOCIALEAZIENDA"))%></td>
                <td class='pulsante_riga'>
            	<% if (Utils.notNull(row.getAttribute("prgLavPattoScelta")).length()>0) {
	                // valorizzo la sequenza di argomenti per la funzione js che cancella tutte le righe
	                args.append(Utils.notNull(row.getAttribute("prgLavPattoScelta")));
	                args.append(",");
	            %>
	            <script>allArgs+="<%=Utils.notNull(row.getAttribute("prgLavPattoScelta"))%>,";</script>
                <%if (canModify){%>
                  <a  href="#" onclick="cancella('<%=row.getAttribute("prgLavPattoScelta")%>')"><img src="../../img/patto_elim.gif" ></a>
	            <% }
                }%>
                </td>
            </tr>
            </table>
        </pt:sectionBody>
        </pt:dynamicSection>
    </pt:sections>
    </td></tr>
    <tr><td colspan="3"><br><br></td></tr>
    <af:form method="post" action="AdapterHTTP" onSubmit="updateStato(this)" dontValidate="true">
        <input type="hidden" name="CDNLAVORATORE" value="<%=cdnLavoratore%>">
        <input type="hidden" name="PAGE" value="PattoAmbitoProfLinguettaPage">
        <input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">        
        <input type="hidden" name="statoSezioni" value="">
        <input type="hidden" name="PRGPATTOLAVORATORE" value="<%=prgPattoLavoratore%>">        
        <input type="hidden" name="NUMKLOPATTOLAVORATORE" value="<%=numklopattolavoratore%>">
    <tr>
        <td colspan=3>
            <table width="100%">
                <tr>
                    <td class="etichetta">Nota ambito professionale</td>
                    <td class="campo"><af:textArea onKeyUp="fieldChanged();" name="STRNOTEAMBITOPROF" value="<%=strNote%>" cols="60" rows="5" readonly="<%=readOnlyStr%>"/></td>
                    <td>
                    <% if (canModify&& notePatto!=null) { %>
                        <input type="submit" name="aggiornaNotePatto" class="pulsanti" value="Aggiorna note">
                    <%}%>
                    </td>
                </tr>
                <tr><td colspan=3 style="campo2">N.B.: se non sono indicate mansioni nel patto viene stampata la nota in relazione all'ambito professionale</td></tr>
            </table>
        </td>
    </tr>
  </table>
<%out.print(htmlStreamBottom);%>
</center>
</af:form>
</body>
</html>


<%! 
    private static final boolean COMPACT=true; 

    private class MobilitaGeoWrapper {
        String utilizzoAuto;
        String pendGiornaliero;
        String mobSett;
        String tipoTrasferta;
        String utilizzoMoto;
        String percentuale;
    }
    private ArrayList getMansioni(Vector mansioniRows) throws Exception {

        //
        SourceBean row_Mansioni= null;
        String Mansione= null;
        String esperienza= null;
        String dataProtocollo = null;
        String Misura_concordata= null;          
        String Contratti= null;                    
        String Orario= null;                              
        String Turni= null;
        String Territorio= null;
        String Comuni= null;          
        String Province= null;                    
        String Regioni= null;                              
        String Stato= null;                
        String mansioneATempo = null;
        String disponibile = null;
        String formazione = null;
        String inserimentoProfessionale = null;
        //          String indisponibilita = null;
        // variabili mobilita' geografica
        String utilizzoAuto = null;
        String utilizzoMoto = null;
        String pendolarismoGiornaliero = null;
        String mobilitaSett = null;
        String tipoTrasferta = null;
        String durataPercorrenzaMax = null;
        String MansionePrec=null;
        //
         
        boolean nuovaMansione=false;
        ArrayList mansioni=new ArrayList();
        SourceBean  rowBean = new SourceBean("ROW");
        if (mansioniRows.size()>0) {            
            MansionePrec= (String)((SourceBean) mansioniRows.elementAt(0)).getAttribute("MANSIONE");     
        }
        ArrayList contratti = new ArrayList();
        ArrayList orari = new ArrayList();
        ArrayList turni = new ArrayList();
        ArrayList comuni = new ArrayList();
        ArrayList provincie = new ArrayList();
        ArrayList regioni = new ArrayList();
        ArrayList stati = new ArrayList();                 
        //String MansionePrec=null;
        MobilitaGeoWrapper geo = new MobilitaGeoWrapper();
        // int duegiri=2;
        boolean stampa = true;
        for(int n=0; n<mansioniRows.size(); n++)  { 
        // if (mansioniRows.size()>0) {                                    
            row_Mansioni = (SourceBean) mansioniRows.elementAt(n);                                      
            int ordine = ((BigDecimal)row_Mansioni.getAttribute("ORDINE")).intValue();
        //                    if (ordine==1) {
        //                      MansionePrec=(String)row_Mansioni.getAttribute("MANSIONE");
        //                }

                  switch (ordine) {                            
                    case 2:
                      contratti.add(row_Mansioni.getAttribute("VINCOLO"));
                      if (mansioniRows.size()==n+1) {//MansionePrec = "";ordine=1;
                            stampa=false;
                            row_Mansioni.updAttribute("ORDINE", new BigDecimal("1"));
                            row_Mansioni.updAttribute("MANSIONE", "");
                            n--;
                        }
                        break;
                    case 3:
                      orari.add(row_Mansioni.getAttribute("VINCOLO"));
                      if (mansioniRows.size()==n+1) {//MansionePrec = "";ordine=1;
                            stampa=false;
                            row_Mansioni.updAttribute("ORDINE", new BigDecimal("1"));
                            row_Mansioni.updAttribute("MANSIONE", "");
                            n--;
                        }
                        break;
                    case 4:
                      turni.add(row_Mansioni.getAttribute("VINCOLO"));
                      if (mansioniRows.size()==n+1) {//MansionePrec = "";ordine=1;
                            stampa=false;
                            row_Mansioni.updAttribute("ORDINE", new BigDecimal("1"));
                            row_Mansioni.updAttribute("MANSIONE", "");
                            n--;
                        }
                        break;
                    case 5:
                      comuni.add(row_Mansioni.getAttribute("VINCOLO"));
                      if (mansioniRows.size()==n+1) {//MansionePrec = "";ordine=1;
                            stampa=false;
                            row_Mansioni.updAttribute("ORDINE", new BigDecimal("1"));
                            row_Mansioni.updAttribute("MANSIONE", "");
                            n--;
                        }
                        break;
                    case 6:
                      provincie.add(row_Mansioni.getAttribute("VINCOLO"));
                      if (mansioniRows.size()==n+1) {//MansionePrec = "";ordine=1;
                            stampa=false;
                            row_Mansioni.updAttribute("ORDINE", new BigDecimal("1"));
                            row_Mansioni.updAttribute("MANSIONE", "");
                            n--;
                        }
                        break;
                    case 7:
                      regioni.add(row_Mansioni.getAttribute("VINCOLO"));
                      if (mansioniRows.size()==n+1) {//MansionePrec = "";ordine=1;
                            stampa=false;
                            row_Mansioni.updAttribute("ORDINE", new BigDecimal("1"));
                            row_Mansioni.updAttribute("MANSIONE", "");
                            n--;
                        }
                        break;
                    case 8:
                      stati.add(row_Mansioni.getAttribute("VINCOLO"));
                      if (mansioniRows.size()==n+1) {//MansionePrec = "";ordine=1;
                            stampa=false;
                            row_Mansioni.updAttribute("ORDINE", new BigDecimal("1"));
                            row_Mansioni.updAttribute("MANSIONE", "");
                            n--;
                        }
                        break;
                    case 9:
                      //
                        utilizzoAuto = (String)row_Mansioni.getAttribute("FLGDISPAUTO");
                        utilizzoAuto = (utilizzoAuto==null)? "&nbsp;":utilizzoAuto;
                        utilizzoMoto = (String)row_Mansioni.getAttribute("flgDispMoto");
                        utilizzoMoto = (utilizzoMoto==null)? "&nbsp;":utilizzoMoto;
                        pendolarismoGiornaliero = (String)row_Mansioni.getAttribute("flgPendolarismo");
                        pendolarismoGiornaliero = (pendolarismoGiornaliero==null)? "&nbsp;":pendolarismoGiornaliero;
                        mobilitaSett = (String)row_Mansioni.getAttribute("FLGMOBSETT");
                        mobilitaSett = (mobilitaSett==null)? "&nbsp;":mobilitaSett;
                        tipoTrasferta = (String)row_Mansioni.getAttribute("VINCOLO");
                        durataPercorrenzaMax =Utils.notNull(row_Mansioni.getAttribute("NUMOREPERC"));
                        durataPercorrenzaMax = (durataPercorrenzaMax==null)? "":durataPercorrenzaMax;
                        //
                        geo.pendGiornaliero=pendolarismoGiornaliero;
                        geo.percentuale=durataPercorrenzaMax;
                        geo.tipoTrasferta=tipoTrasferta;
                        geo.utilizzoAuto=utilizzoAuto;
                        geo.utilizzoMoto=utilizzoMoto;
                        geo.mobSett=mobilitaSett;
                        if (mansioniRows.size()!=n+1) 
                                break;
                        else {MansionePrec = "";ordine=1;stampa=false;}
                        // se sono alla fine del ciclo debbo registrare le info accumulate
                        // otterro' che la mansione letta e quella precedente saranno diverse
                     case 1:
                        Mansione = (String)row_Mansioni.getAttribute("MANSIONE");
                        if (!Mansione.equals(MansionePrec)) {                                        
                                rowBean.setAttribute("contratti", contratti);
                                rowBean.setAttribute("orari", orari);
                                rowBean.setAttribute("turni", turni);
                                rowBean.setAttribute("comuni", comuni);
                                rowBean.setAttribute("provincie", provincie);
                                rowBean.setAttribute("regioni",regioni);
                                rowBean.setAttribute("stati", stati);
                                rowBean.setAttribute("mobilitaGeo", geo);
                                        
                                mansioni.add(rowBean);
                                //
                                MansionePrec=Mansione;
                                        
                        }
                        mansioneATempo = (String)row_Mansioni.getAttribute("TEMPO");                                        
                       // mansioneATempo = (mansioneATempo.equals(""))? "9" :mansioneATempo;
                        if (mansioneATempo!=null) {
                            switch (mansioneATempo.charAt(0)) {
                                case 'D':
                                    mansioneATempo = "Tempo determinato";
                                    break;
                                case 'I':
                                    mansioneATempo = "Tempo indeterminato";
                                    break;
                                case 'E':
                                    mansioneATempo = "Tempo det/indet";
                                    break;                                               
                            }
                        }
                        esperienza=Utils.notNull(row_Mansioni.getAttribute("ESPERIENZA"));
                        disponibile = (String)row_Mansioni.getAttribute("FLGDISPONIBILE");
                        inserimentoProfessionale = (String)row_Mansioni.getAttribute("FLGPIP");
                        formazione = (String)row_Mansioni.getAttribute("FLGDISPFORMAZIONE");
                        dataProtocollo = (String)row_Mansioni.getAttribute("datProtocollo");
                        String prgMansione = row_Mansioni.getAttribute("prgMansione").toString();
                        String prgLavPattoScelta =  row_Mansioni.getAttribute("prgLavPattoScelta").toString();
                        ArrayList al  = new ArrayList(3);
                        if (inserimentoProfessionale!=null) al.add("Progetti di inserimento");
                        if (formazione!=null) al.add("Percorsi formativi");
                        if (disponibile!=null) al.add("Attività lavorativa");
                        rowBean= new SourceBean("ROW");
                        rowBean.setAttribute("mansioneATempo", Utils.notNull(mansioneATempo));
                        rowBean.setAttribute("esperienza",esperienza);
                        rowBean.setAttribute("mansione", Mansione);
                        if (dataProtocollo !=null)rowBean.setAttribute("datProtocollo", dataProtocollo);
                        rowBean.setAttribute("prgLavPattoScelta", prgLavPattoScelta);
                        rowBean.setAttribute("prgMansione",prgMansione);
                        rowBean.setAttribute("misura",al);
                          // inizializza arrays      
                        contratti=new ArrayList();
                        orari = new ArrayList();
                        turni= new ArrayList();
                        comuni = new ArrayList();
                        provincie = new ArrayList();
                        regioni = new ArrayList();
                        stati =new ArrayList();
                        geo = new MobilitaGeoWrapper();
                        // caso particolare in cui siamo nell' ultima mansione ed un solo ordine=1
                        if (mansioniRows.size()==n+1 && stampa) {
                            rowBean.setAttribute("contratti", contratti);
                            rowBean.setAttribute("orari", orari);
                            rowBean.setAttribute("turni", turni);
                            rowBean.setAttribute("comuni", comuni);
                            rowBean.setAttribute("provincie", provincie);
                            rowBean.setAttribute("regioni",regioni);
                            rowBean.setAttribute("stati", stati);
                            rowBean.setAttribute("mobilitaGeo", geo);
                            mansioni.add(rowBean);
                        }
                        break;
                    default: // per il momento non fare niente. Una situazione del genere non deve verificarsi
                 }// switch                            
             }
        return mansioni;
    }
    
%>