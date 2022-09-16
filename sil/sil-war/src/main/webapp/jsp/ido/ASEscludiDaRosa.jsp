<%@ page contentType="text/html;charset=utf-8"%>


<%@ page import="com.engiweb.framework.base.*, 
                com.engiweb.framework.configuration.ConfigSingleton,
                 java.lang.*,
                java.text.*, java.util.*,it.eng.sil.util.*, 
                it.eng.afExt.utils.StringUtils,
                java.math.*, it.eng.sil.security.* "%>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af"%>



<%

String _page = (String) serviceRequest.getAttribute("PAGE"); 


ProfileDataFilter filter = new ProfileDataFilter(user, _page);

if (! filter.canView()){
	response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	return;
}

int _funzione=0;
    
    boolean aggiornamento=true;
    
  //inizializzo i campi
  //BigDecimal cdnLavoratore=new BigDecimal(0);
  String strCognome="";
  String strNome="";
  String prgNom="";
  BigDecimal prgTipoRosa=new BigDecimal(0);
  BigDecimal prgRosa=new BigDecimal(0);
  //String prgRosa = StringUtils.getAttributeStrNotNull(serviceRequest, "PRGROSA");
  String codCpi="";
  BigDecimal numKlo=new BigDecimal(0);
  
  String strMotiv="";
  String codMotivCanc="";
  //----------
  String prgRichiestaAz="";
  String prgAzienda="";
  String prgUnita="";
  String prgIncrocioParam="";
  String prgTipoRosaParam="";
  String concatenaCpiParam="";
  String calcPosizioneParam="";
  String cdnStatoRichParam="";
  String prgTipoIncrocioParam="";
  String cdnLavoratoreParam="";
  String prgRosaParam="";
  String moduleParam="";
  //----------
  String cdnUtins="";
  String dtmins="";
  String cdnUtmod="";
  String dtmmod="";
  InfCorrentiLav infCorrentiLav= null;
  Testata operatoreInfo = null;
    
    _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
    prgNom        = (String) serviceRequest.getAttribute("PRGNOMINATIVO");
    codCpi        = (String) serviceRequest.getAttribute("codCpi");
    //-----------
    prgRichiestaAz  = serviceRequest.getAttribute("PRGRICHIESTAAZ").toString();
    prgAzienda      = serviceRequest.getAttribute("PRGAZIENDA").toString();
    prgUnita        = serviceRequest.getAttribute("PRGUNITA").toString();
    prgIncrocioParam  = serviceRequest.getAttribute("PRGINCROCIO").toString();
    prgTipoRosaParam  = serviceRequest.getAttribute("prgTipoRosa").toString();
    concatenaCpiParam = serviceRequest.getAttribute("ConcatenaCpi").toString();
    calcPosizioneParam = serviceRequest.getAttribute("CALC_POSIZIONE").toString();
    cdnStatoRichParam  = serviceRequest.getAttribute("CDNSTATORICH").toString();
    prgTipoIncrocioParam = serviceRequest.getAttribute("PRGTIPOINCROCIO").toString();
    cdnLavoratoreParam = serviceRequest.getAttribute("CDNLAVORATORE").toString();
    prgRosaParam = serviceRequest.getAttribute("PRGROSA").toString();
    moduleParam = serviceRequest.getAttribute("MODULE").toString();
    
    //-----------
    SourceBean lavInfo = (SourceBean) serviceResponse.getAttribute("M_GET_NOMINATIVO.ROWS.ROW");
    
    if  ( lavInfo != null )  {
      strCognome       = (String) lavInfo.getAttribute("strCognome");
      strNome       = (String) lavInfo.getAttribute("strNome");
      prgTipoRosa  = (BigDecimal) lavInfo.getAttribute("prgTipoRosa");
      prgRosa  = (BigDecimal) lavInfo.getAttribute("prgRosa");
      numKlo  = (BigDecimal) lavInfo.getAttribute("numKloNominativo");
     // inc = new BigDecimal(numKlo.intValue()+1);
      //cdnLavoratore = (BigDecimal) lavInfo.getAttribute("cdnLavoratore");
      strMotiv = (String) lavInfo.getAttribute("STRMOTIVOCANC");
      codMotivCanc = (String) lavInfo.getAttribute("CODTIPOCANC");
      if(StringUtils.isFilledNoBlank(codMotivCanc) &&  codMotivCanc.equalsIgnoreCase("M")) {
    	  aggiornamento = false;
      }
    }     
    
    
   // NOTE: Attributi della pagina (pulsanti e link) 
  PageAttribs attributi = new PageAttribs(user, "ASEscludiDaRosaPage");
  boolean canModify = attributi.containsButton("rimuovi");
 %>

<%
String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

String mess = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE");
String listPage = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE");

String strTitle = "Esclusione";

String indietro =  "PAGE=ASMatchDettGraduatoriaPage&codCpi=" + codCpi + "&CDNFUNZIONE=" + _funzione;
indietro += "&PRGRICHIESTAAZ=" + prgRichiestaAz + "&PRGAZIENDA=" + prgAzienda + "&PRGUNITA=" +prgUnita;
indietro += "&PRGINCROCIO=" + prgIncrocioParam + "&prgTipoRosa=" + prgTipoRosaParam + "&ConcatenaCpi=" + concatenaCpiParam;
indietro += "&CALC_POSIZIONE=" + calcPosizioneParam + "&CDNSTATORICH=" + cdnStatoRichParam + "&PRGTIPOINCROCIO=" + prgTipoIncrocioParam;
indietro += "&MODULE=" + moduleParam + "&PRGROSA=" + prgRosaParam + "&PRGNOMINATIVO=" + prgNom +  "&CDNLAVORATORE="  + cdnLavoratoreParam;
%>

<html>
<head>
<title><%=strTitle%></title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">

<af:linkScript path="../../js/"/>
 <%@ include file="../global/fieldChanged.inc" %>

<SCRIPT language="javascript">
  function controllaCampo(){
    if (trim(document.Frm1.STRMOTIVOCANC.value) == ""){
      alert ("Il campo Motivazione non può essere vuoto.");
      return false;
    }
    return true;
  }
</script>

<script language="JavaScript">

// function openPage(pagina,parametri)
// { //alert(pagina+"\r\n"+parametri);
//   // Se la pagina è già in submit, ignoro questo nuovo invio!
//   if (isInSubmit()) return;

//   setWindowLocation("AdapterHTTP?PAGE="+pagina +parametri);
// }
function cambioValore(){ 
	<%if (canModify) {out.print("flagChanged = true;");}%>
	}
function indietro(){  	
	ok = true;
	if (flagChanged) {
		ok = confirm("I dati sono cambiati.\nProcedere lo stesso?");

		// Vogliamo chiudere il layer.
		// Pongo il flag=false per evitare che mi venga riproposto un "confirm"
		// quando poi navigo con le linguette nella pagina principale
		flagChanged = false;
	}
	if(ok) {
		window.location="<%=indietro%>";     	
	}
	
}
function go(url, alertFlag) {      
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;
	  
	  var _url = "AdapterHTTP?" + url;
	  if (alertFlag == 'TRUE' ) {
	    if (confirm('Confermi operazione')) {
	      setWindowLocation(_url);
	    }
	  } else {
	    setWindowLocation(_url);
	  }
	}
function tornaIndietro(){  	
	ok = true;
	if (flagChanged) {
		ok = confirm("I dati sono cambiati.\nProcedere lo stesso?");

		// Vogliamo chiudere il layer.
		// Pongo il flag=false per evitare che mi venga riproposto un "confirm"
		// quando poi navigo con le linguette nella pagina principale
		flagChanged = false;
	}
	if(ok) {
		var param = "<%=indietro%>";
		var _url = "AdapterHTTP?";
		_url += param;
		setWindowLocation(_url);
	}
	
}
</script>
</head>

<body class="gestione">

<%
    if (cdnLavoratoreParam != null) {         
        InfCorrentiLav _testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratoreParam, user);
        _testata.setSkipLista(true);
        _testata.show(out);
	}
%>
 
<font color="green">
  <af:showMessages prefix="M_ASESCLUDI"/>
</font>
<font color="red">
  <af:showErrors/>
</font>
<p class="titolo"><b><%=strTitle%></b></p>
<%out.print(htmlStreamTop);%>
<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="controllaCampo()">
      
      <input type="hidden" name="PAGE" value="ASEscludiDaRosaPage">
      <input type="hidden" name="CDNFUNZIONE" value="<%= _funzione %>"/>
      <input type="hidden" name="PRGROSA" value="<%= prgRosa %>"/>
      <input type="hidden" name="codCpi" value="<%= codCpi %>"/>
      <input type="hidden" name="PRGTIPOROSA" value="<%= prgTipoRosa %>"/>
      <input type="hidden" name="NUMKLONOMINATIVO" value="<%= numKlo %>"/>
      
      <input type="hidden" name="PRGNOMINATIVO" value="<%= prgNom %>"/>

      <input type="hidden" name="PRGRICHIESTAAZ" value="<%= prgRichiestaAz %>"/>
      <input type="hidden" name="PRGAZIENDA" value="<%= prgAzienda %>"/>
      <input type="hidden" name="PRGUNITA" value="<%= prgUnita %>"/>
      <input type="hidden" name="PRGINCROCIO" value="<%= prgIncrocioParam %>"/>
      <input type="hidden" name="prgTipoRosa" value="<%= prgTipoRosaParam %>"/>
      
      <input type="hidden" name="ConcatenaCpi" value="<%= concatenaCpiParam %>"/>
      <input type="hidden" name="CALC_POSIZIONE" value="<%= calcPosizioneParam %>"/>
      <input type="hidden" name="CDNSTATORICH" value="<%= cdnStatoRichParam %>"/>
      <input type="hidden" name="PRGTIPOINCROCIO" value="<%= prgTipoIncrocioParam %>"/>
      <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratoreParam %>"/>
      <input type="hidden" name="MODULE" value="<%= moduleParam %>"/>

      <table width="100%" class="main">
         <tr>
          <td class="etichetta">Candidato</td>
          <td class="campo">
            <af:textBox classNameBase="input" name="candidato" size="100" value="<%= strCognome + ' ' + strNome %>" readonly="true" />               
          </td>
        </tr>
        <tr><td><br/></td></tr>
         <tr valign="top">
          <td class="etichetta">Motivazione</td>
          <td class="campo">
              <af:textArea classNameBase="textarea" onKeyUp="cambioValore();"  name="STRMOTIVOCANC" maxlength="100" cols="50" value="<%= strMotiv %>" readonly="<%= String.valueOf(!aggiornamento) %>" title="Motivazione" required="true"/>
          </td>
        </tr>  
        <tr><td><br/></td></tr>
        <tr>
          <% if ( canModify && aggiornamento) { %>            
            <td colspan="2" align="center">
                <input type="submit" class="pulsanti" name="salva" value="Escludi">
                <input type="reset" class="pulsanti" name="annulla" value="Annulla">
            </td>
          <%}%>
        </tr>
        <tr>
          <td colspan="2" align="center">
            <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onclick="tornaIndietro();">
          </td>
        </tr>
    </table>
</af:form>
<%out.print(htmlStreamBottom);%>
</body>
</html>
