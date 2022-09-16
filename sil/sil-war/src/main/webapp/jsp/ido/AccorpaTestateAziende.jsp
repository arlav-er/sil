<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="
  com.engiweb.framework.base.*, 
  com.engiweb.framework.configuration.ConfigSingleton,
   
  it.eng.sil.util.*, 
  it.eng.afExt.utils.*,
  java.lang.*,
  java.text.*, 
  java.util.*,
  java.math.*,
  it.eng.sil.security.*"%>

<%@ taglib uri="aftags" prefix="af"%>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc"%>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%

String _page = SourceBeanUtils.getAttrStrNotNull(serviceRequest, "PAGE");
// NOTE: Attributi della pagina (pulsanti e link)
ProfileDataFilter filter = new ProfileDataFilter(user, "IDOACCORPAAZIENDEPAGE");
boolean canView = filter.canView();
if (! canView){
	response.sendRedirect("../../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	return;
}




PageAttribs attributi = new PageAttribs(user, "IDOACCORPAAZIENDEPAGE");
boolean canManage = attributi.containsButton("ACCORPA");
int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));

//boolean canModify = false;

boolean dopoAccorpamento = serviceRequest.containsAttribute("DOACCORPAMENTO");

String prgAzienda1="";
String strCodiceFiscale="";
String strPartitaIva="";
String strRagioneSociale="";
String strNote="";
String codNatGiuridica="";
String codTipoAzienda="";
String descTipoAzienda="";
String strSitoInternet="";
//String codAteco="";
String strDesAteco="";
String tipo_ateco="";
String strDescAttivita="";
String numSoci="";
String numDipendenti="";
String numCollaboratori="";
String numAltraPosizione="";
//String numREA="";
String datInizio="";
String datAggInformazione="";
String datFine="";
String strFlgDatiOk="";
String cdnUtins="";
String dtmins="";
String cdnUtmod="";
String dtmmod="";
String prgMov="";
String strNumAlboInterinali="";
String strRepartoInail="";
String patInail="";
String patInail1 = "";
String patInail2 = "";

String prgAzienda2="";
String strCodiceFiscale_2     =  "";
String strPartitaIva_2        =  "";
String strRagioneSociale_2    =  "";
String strNote_2              =  "";
String codNatGiuridica_2      =  "";
String codTipoAzienda_2       =  "";
String descTipoAzienda_2      =  "";
String strSitoInternet_2      =  "";
//String codAteco_2            = "";
String strDesAteco_2          =  "";
String tipo_ateco_2           =  "";

String strDescAttivita_2      =  "";
String numSoci_2              =  "";
String numDipendenti_2        =  "";
String numCollaboratori_2     =  "";
String numAltraPosizione_2    =  "";
//String numREA_2              = "";
String datInizio_2            =  "";
String datAggInformazione_2   =  "";
String datFine_2              =  "";
String strFlgDatiOk_2         =  "";

String cdnUtins_2             =  "";
String dtmins_2               =  "";
String cdnUtmod_2             =  "";
String dtmmod_2               =  "";
String strNumAlboInterinali_2 =  "";
String strRepartoInail_2      =  "";
String patInail_2             =  "";

String patInail1_2            = "";
String patInail2_2            = "";


// Testata operatoreInfo = null;

// da request, utili per il bottone di CHIUDI
prgAzienda1 = StringUtils.notNull( (String)serviceRequest.getAttribute("prgAziendaAccorpante") );
prgAzienda2 = StringUtils.notNull( (String)serviceRequest.getAttribute("prgAziendaAccorpata") );

if (! dopoAccorpamento) {

  SourceBean moduleResponse = (SourceBean) serviceResponse.getAttribute("M_GetTestateAziendaAccorpamento");
  if (moduleResponse == null) moduleResponse = new SourceBean("dummy");
  
  SourceBean testata1row = (SourceBean) moduleResponse.getAttribute("TESTATA1.ROWS.ROW");
  SourceBean testata2row = (SourceBean) moduleResponse.getAttribute("TESTATA2.ROWS.ROW");

  Vector unita1vec = moduleResponse.getAttributeAsVector("UNITA1.ROWS.ROW");
  Vector unita2vec = moduleResponse.getAttributeAsVector("UNITA2.ROWS.ROW");

  // Recupero gli ID dal SB di risposta poiche' le due aziende sono
  // rese in ordine per data di inserimento (la "1" e' la piu' vecchia).
  prgAzienda1 = ((BigDecimal)testata1row.getAttribute("prgAzienda")).toString();
  prgAzienda2 = ((BigDecimal)testata2row.getAttribute("prgAzienda")).toString();

  
  strCodiceFiscale     = StringUtils.getAttributeStrNotNull(testata1row, "strCodiceFiscale");
  strPartitaIva        = StringUtils.getAttributeStrNotNull(testata1row, "strPartitaIva");
  strRagioneSociale    = StringUtils.getAttributeStrNotNull(testata1row, "strRagioneSociale");
  strNote              = StringUtils.getAttributeStrNotNull(testata1row, "strNote");
  codNatGiuridica      = StringUtils.getAttributeStrNotNull(testata1row, "codNatGiuridica");
  codTipoAzienda       = StringUtils.getAttributeStrNotNull(testata1row, "codTipoAzienda");
  descTipoAzienda      = StringUtils.getAttributeStrNotNull(testata1row, "descTipoAzienda");


  strSitoInternet      = StringUtils.getAttributeStrNotNull(testata1row, "strSitoInternet");
  //codAteco           = StringUtils.getAttributeStrNotNull(testata1row, "codAteco");
  strDesAteco          = StringUtils.getAttributeStrNotNull(testata1row, "strDesAteco");
  tipo_ateco           = StringUtils.getAttributeStrNotNull(testata1row, "tipo_ateco");
                       
  strDescAttivita      = StringUtils.getAttributeStrNotNull(testata1row, "strDescAttivita");
  numSoci              = testata1row.containsAttribute("numSoci") ? testata1row.getAttribute("numSoci").toString() : "";
  numDipendenti        = testata1row.containsAttribute("numDipendenti") ? testata1row.getAttribute("numDipendenti").toString() : "";
  numCollaboratori     = testata1row.containsAttribute("numCollaboratori") ? testata1row.getAttribute("numCollaboratori").toString() : "";
  numAltraPosizione    = testata1row.containsAttribute("numAltraPosizione") ? testata1row.getAttribute("numAltraPosizione").toString() : "";
  //numREA              = testata1row.containsAttribute("numREA") ? testata1row.getAttribute("numREA").toString() : "";
  datInizio=StringUtils.getAttributeStrNotNull(testata1row, "datInizio");
  //numREA_2              = (BigDecimal) testata1row.getAttribute("numREA2");
  datInizio            = StringUtils.getAttributeStrNotNull(testata1row, "datInizio");
  datAggInformazione   = StringUtils.getAttributeStrNotNull(testata1row, "datAggInformazione");  
  datFine              = StringUtils.getAttributeStrNotNull(testata1row, "datFine");
  strFlgDatiOk         = StringUtils.getAttributeStrNotNull(testata1row, "FLGDATIOK");
                       
  cdnUtins             = StringUtils.getStringValueNotNull(testata1row.getAttribute("cdnUtins"));
  dtmins               = StringUtils.getAttributeStrNotNull(testata1row, "dtmins");
  cdnUtmod             = StringUtils.getStringValueNotNull(testata1row.getAttribute("cdnUtmod"));
  dtmmod               = StringUtils.getAttributeStrNotNull(testata1row, "dtmmod");

  /* NON mi servono:
    numKloAzienda        = testata1row.containsAttribute("numKloAzienda") ? (BigDecimal) testata1row.getAttribute("numKloAzienda") : null;
    numKloAzienda        = numKloAzienda.add(new BigDecimal(1));
  */
  
  strNumAlboInterinali = StringUtils.getAttributeStrNotNull(testata1row, "strNumAlboInterinali");
  strRepartoInail      = StringUtils.getAttributeStrNotNull(testata1row, "strRepartoInail");
  patInail             = StringUtils.getAttributeStrNotNull(testata1row, "strPatInail");

  //Gestione spezzatino Pat Inail
  patInail1 = patInail.substring(0, (patInail.length() >= 8 ? 8 : patInail.length())  );
  patInail2 = patInail.substring( (patInail.length() >= 8 ? 8 : patInail.length() ), ( patInail.length() >= 10 ? 10 : patInail.length()  ));


  strCodiceFiscale_2     = StringUtils.getAttributeStrNotNull(testata2row, "strCodiceFiscale");
  strPartitaIva_2        = StringUtils.getAttributeStrNotNull(testata2row, "strPartitaIva");
  strRagioneSociale_2    = StringUtils.getAttributeStrNotNull(testata2row, "strRagioneSociale");
  strNote_2              = StringUtils.getAttributeStrNotNull(testata2row, "strNote");
  codNatGiuridica_2      = StringUtils.getAttributeStrNotNull(testata2row, "codNatGiuridica");
  codTipoAzienda_2       = StringUtils.getAttributeStrNotNull(testata2row, "codTipoAzienda");
  descTipoAzienda_2      = StringUtils.getAttributeStrNotNull(testata1row, "descTipoAzienda");
  strSitoInternet_2      = StringUtils.getAttributeStrNotNull(testata2row, "strSitoInternet");
  //codAteco_2            = StringUtils.getAttributeStrNotNull(testata2row, "codAteco");
  strDesAteco_2          = StringUtils.getAttributeStrNotNull(testata2row, "strDesAteco");
  tipo_ateco_2           = StringUtils.getAttributeStrNotNull(testata2row, "tipo_ateco");
                       
  strDescAttivita_2      = StringUtils.getAttributeStrNotNull(testata2row, "strDescAttivita");
  numSoci_2              = testata2row.containsAttribute("numSoci") ? testata2row.getAttribute("numSoci").toString() : "";                    
  numDipendenti_2        = testata2row.containsAttribute("numDipendenti") ? testata2row.getAttribute("numDipendenti").toString() : "";        
  numCollaboratori_2     = testata2row.containsAttribute("numCollaboratori") ? testata2row.getAttribute("numCollaboratori").toString() : "";  
  numAltraPosizione_2    = testata2row.containsAttribute("numAltraPosizione") ? testata2row.getAttribute("numAltraPosizione").toString() : "";
  //numREA_2              = testata2row.containsAttribute("numREA") ? testata2row.getAttribute("numREA").toString() : "";                     
  datInizio_2            = StringUtils.getAttributeStrNotNull(testata2row, "datInizio");
  datAggInformazione_2   = StringUtils.getAttributeStrNotNull(testata2row, "datAggInformazione");  
  datFine_2              = StringUtils.getAttributeStrNotNull(testata2row, "datFine");
  strFlgDatiOk_2         = StringUtils.getAttributeStrNotNull(testata2row, "FLGDATIOK");
                       
  cdnUtins_2             = StringUtils.getStringValueNotNull(testata1row.getAttribute("cdnUtins"));
  dtmins_2               = StringUtils.getAttributeStrNotNull(testata2row, "dtmins");
  cdnUtmod_2             = StringUtils.getStringValueNotNull(testata1row.getAttribute("cdnUtmod"));
  dtmmod_2               = StringUtils.getAttributeStrNotNull(testata2row, "dtmmod");

  /* NON mi servono:
    numKloAzienda_2        = testata2row.containsAttribute("numKloAzienda") ? (BigDecimal) testata2row.getAttribute("numKloAzienda") : null;
    numKloAzienda_2        = numKloAzienda.add(new BigDecimal(1));
  */
  
  strNumAlboInterinali_2 = StringUtils.getAttributeStrNotNull(testata2row, "strNumAlboInterinali");
  strRepartoInail_2      = StringUtils.getAttributeStrNotNull(testata2row, "strRepartoInail");
  patInail_2             = StringUtils.getAttributeStrNotNull(testata2row, "strPatInail");

  //Gestione spezzatino Pat Inail
  patInail1_2 = patInail_2.substring(0, (patInail_2.length() >= 8 ? 8 : patInail_2.length())  );
  patInail2_2 = patInail_2.substring( (patInail_2.length() >= 8 ? 8 : patInail_2.length() ), ( patInail_2.length() >= 10 ? 10 : patInail_2.length()  ));

} // if !dopoAccorpamento


//InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
//operatoreInfo = new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
    
String htmlStreamTop    = StyleUtils.roundTopTable(canManage);
String htmlStreamBottom = StyleUtils.roundBottomTable(canManage);
%>

<html>

<head>
  <title>Accorpa Testate Azienda</title>
  <!-- ..jsp/ido/AccorpaTestateAzienda.jsp -->

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css">
  <af:linkScript path="../../js/"/>
   
<SCRIPT TYPE="text/javascript">

	function cambia(immagine, sezione) {
		if (sezione.aperta==null) sezione.aperta=false;
		if (sezione.aperta) {
			sezione.style.display="none";
			sezione.aperta=false;
			immagine.src="../../img/chiuso.gif";
		}
		else {
			sezione.style.display="";
			sezione.aperta=true;
			immagine.src="../../img/aperto.gif";
		}
	}
	
	function cambiaPro(immagine, sezione) {
		if (sezione.chiusa==null) sezione.chiusa=false;
		if (sezione.chiusa) {
			sezione.style.display="";
			sezione.chiusa=false;
			immagine.src="../../img/aperto.gif";
		}
		else {
			sezione.style.display="none";
			sezione.chiusa=true;
			immagine.src="../../img/chiuso.gif";
		}
	}	
	
	function accorpa(prgAccorpata, prgAccorpante) {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

		target = "AdapterHTTP?PAGE=AccorpamentoTestataPage" +
					"&DOACCORPAMENTO=true" +
					"&cdnFunzione=<%=_funzione%>";
	  	
	  	if (confirm("Verrà accorpata la testata " + prgAccorpata + " alla " + prgAccorpante + ".\r\n"+
	  					"La testata " + prgAccorpata + " verrà cancellata.\r\n"+
	  					"Procedere con l'accorpamento?" )) {
	  		
	  		if (prgAccorpata == 1) {
				target+="&prgAziendaAccorpante=<%= prgAzienda2 %>"+
						"&prgAziendaAccorpata=<%=  prgAzienda1 %>";
			}
			else {
				target+="&prgAziendaAccorpante=<%= prgAzienda1 %>"+
						"&prgAziendaAccorpata=<%=  prgAzienda2 %>";
			}
						
			setWindowLocation(target);
		}
	}
	
	function goBackLista() {
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

		<%
//		String token = "_TOKEN_" + "IDOACCORPAAZIENDEPAGE";
//		String urlDiLista = (String) sessionContainer.getAttribute(token.toUpperCase());
//		if (urlDiLista != null) {
//			urlDiLista = QueryString.removeParameter(urlDiLista, new String[]{"exSelezione1","exSelezione2"});
			// Aggiungo le due aziende selezionate
			String urlDiLista="";
			urlDiLista +="&PAGE=IDOACCORPAAZIENDEPAGE";
		    urlDiLista +="&CDNFUNZIONE="+_funzione;
		     if (! dopoAccorpamento) {
			    urlDiLista +="&PRGAZIENDA1=" + prgAzienda1;
				urlDiLista +="&ragioneSocialeAz1=" + StringUtils.replace( strRagioneSociale.replace('&','e'), "\"" , "\\\"");
				urlDiLista +="&codFiscaleAz1=" + strCodiceFiscale;
				urlDiLista +="&pIvaAz1=" + strPartitaIva;
				urlDiLista +="&descrTipoAz1=" + descTipoAzienda;
				urlDiLista +="&PRGAZIENDA2=" + prgAzienda2;
				urlDiLista +="&ragioneSocialeAz2=" + StringUtils.replace( strRagioneSociale_2.replace('&','e') , "\"" , "\\\"");
				urlDiLista +="&codFiscaleAz2=" + strCodiceFiscale_2;
				urlDiLista +="&pIvaAz2=" + strPartitaIva_2;
				urlDiLista +="&descrTipoAz2=" + descTipoAzienda_2;
			}
			%>
			setWindowLocation("AdapterHTTP?<%= urlDiLista %>");
	}		

</SCRIPT>
<script language="Javascript">
     <% 
     	//Genera il Javascript che si occupera' di inserire i links nel footer
        attributi.showHyperLinks(out, requestContainer,responseContainer,"");
      %>
</script>
</head>

<body class="gestione" onload="rinfresca();">
<br/>
<h2>Accorpa Testate Azienda</h2>
<p>
  <font color="green">
	<af:showMessages prefix="M_GetTestateAziendaAccorpamento" />
	<af:showMessages prefix="M_DoAccorpamentoTestata" />
  </font>
  <font color="red"><af:showErrors /></font>
</p>

<af:form name="Frm1" method="POST" action="AdapterHTTP">

<%
if (! dopoAccorpamento) {
%>

<%= htmlStreamTop %>
<table class="main">
    <tr><td class="etichetta2" width="20%"></td><td class="campo2" width="40%">Testata Azienda 1</td><td class="campo2" width="40%">Testata Azienda 2</td></tr>
    <tr><td>&nbsp;</td></tr>
    <tr valign="top">
      <td class="etichetta2">Codice fiscale</td>
      <td class="campo2">
        <af:textBox classNameBase="input" name="strCodiceFiscale" title="Codice Fiscale" size="20" value="<%=strCodiceFiscale%>" readonly="true" maxlength="16" />
      </td>
      <td class="campo2">
        <af:textBox classNameBase="input" name="strCodiceFiscale_2" title="Codice Fiscale" size="20" value="<%=strCodiceFiscale_2%>" readonly="true" maxlength="16" />
      </td>
    </tr>

    <tr valign="top">
      <td class="etichetta2">Partita IVA </td>
      <td class="campo2">
        <af:textBox classNameBase="input" name="strPartitaIva" title="Partita Iva"  size="13" validateWithFunction="checkPIVA" value="<%=strPartitaIva%>" readonly="true" maxlength="11" />
      </td>
      <td class="campo2">
        <af:textBox classNameBase="input" name="strPartitaIva_2" title="Partita Iva"  size="13" validateWithFunction="checkPIVA" value="<%=strPartitaIva_2%>" readonly="true" maxlength="11" />
      </td>
    </tr>
    <tr valign="top">
      <td class="etichetta2">Validit&agrave; C.F./P.IVA</td>
      <td class="campo2">
        <af:comboBox 
          name="FLGDATIOK"
          classNameBase="input"
          disabled="true">
          <option value=""  <% if ( "".equalsIgnoreCase(strFlgDatiOk) )  { %>SELECTED="true"<% } %> ></option>
          <option value="S" <% if ( "S".equalsIgnoreCase(strFlgDatiOk) ) { %>SELECTED="true"<% } %> >Sì</option>
          <option value="N" <% if ( "N".equalsIgnoreCase(strFlgDatiOk) ) { %>SELECTED="true"<% } %> >No</option>
        </af:comboBox>
      </td>
      <td class="campo2">
        <af:comboBox 
          name="FLGDATIOK_2"
          classNameBase="input"
          disabled="true">
          <option value=""  <% if ( "".equalsIgnoreCase(strFlgDatiOk_2) )  { %>SELECTED="true"<% } %> ></option>
          <option value="S" <% if ( "S".equalsIgnoreCase(strFlgDatiOk_2) ) { %>SELECTED="true"<% } %> >Sì</option>
          <option value="N" <% if ( "N".equalsIgnoreCase(strFlgDatiOk_2) ) { %>SELECTED="true"<% } %> >No</option>
        </af:comboBox>
      </td>
    </tr>

     <%-- <tr valign="top">
      <td class="etichetta2">Numero REA</td>
      <td class="campo2">
        <af:textBox classNameBase="input" name="strREA" title="Numero REA" size="12" value="<%=numREA%>" readonly="true" />
      </td>
    </tr> --%>

    <tr valign="top">
      <td class="etichetta2">Ragione sociale</td>
      <td class="campo2">
        <af:textBox classNameBase="input" name="strRagioneSociale" title="Ragione Sociale" size="40" value="<%=strRagioneSociale%>" readonly="true" />
      </td>
      <td class="campo2">
        <af:textBox classNameBase="input" name="strRagioneSociale_2" title="Ragione Sociale" size="40" value="<%=strRagioneSociale_2%>" readonly="true" />
      </td>
    </tr>


    <tr valign="top">
      <td class="etichetta2">Natura azienda</td>
      <td class="campo2">
        <af:comboBox classNameBase="input" name="codNatGiuridica" title="Natura Giuridica" selectedValue="<%=codNatGiuridica%>" disabled="true" moduleName="M_GETTIPINATGIURIDICA" addBlank="true" />
      </td>
      <td class="campo2">
        <af:comboBox classNameBase="input" name="codNatGiuridica_2" title="Natura Giuridica" selectedValue="<%=codNatGiuridica_2%>" disabled="true" moduleName="M_GETTIPINATGIURIDICA" addBlank="true" />
      </td>
    </tr>

    <tr valign="top">
      <td class="etichetta2">Tipo di azienda</td>
      <td class="campo2">
        <af:comboBox classNameBase="input" name="codTipoAzienda" title="Tipo di Azienda" selectedValue="<%=codTipoAzienda%>" disabled="true" moduleName="M_GETTIPIAZIENDA" addBlank="true" />
      </td>
      <td class="campo2">
        <af:comboBox classNameBase="input" name="codTipoAzienda_2" title="Tipo di Azienda" selectedValue="<%=codTipoAzienda_2%>" disabled="true" moduleName="M_GETTIPIAZIENDA" addBlank="true" />
      </td>
    </tr>

    <tr valign="top">
      <td class="etichetta2">Sito internet</td>
      <td class="campo2">
        <af:textBox classNameBase="input" name="strSitoInternet" title="Sito Internet" size="40"  maxlength="100" 
                    value="<%=strSitoInternet%>" readonly="true" />
      </td>
      <td class="campo2">
        <af:textBox classNameBase="input" name="strSitoInternet_2" title="Sito Internet" size="40"  maxlength="100" 
                    value="<%=strSitoInternet_2%>" readonly="true" />
      </td>
    </tr>

    <%--<tr>
       <td class="etichetta2">Codice Attivit&agrave;</td>
      <td class="campo2">
        <af:textBox classNameBase="input" name="codAteco" title="Codice di Attivit&agrave;" size="6" maxlength="5" value="<%=codAteco%>" readonly="true" />
        <af:textBox type="hidden" name="codAtecoHid" value="<%=codAteco%>" />
        <% if (canModify) { %>
            <a href="javascript:selectATECO_onClick(Frm1.codAteco, Frm1.codAtecoHid, Frm1.strAteco,  Frm1.strTipoAteco);"><img src="../../img/binocolo.gif" alt="Cerca"></A>&nbsp;&nbsp;
            <A href="javascript:ricercaAvanzataAteco();">
                Ricerca avanzata
            </A>
        <%}%>
      </td>
    </tr> --%>       
    <%--<tr valign="top">
      <td class="etichetta2">Tipo</td>
      <td class="campo2">
        <af:textBox classNameBase="input" name="strTipoAteco" value="<%=tipo_ateco%>" readonly="true" size="48" />
      </td>
    </tr>

    <tr>
      <td class="etichetta2">Attivit&agrave;</td>
      <td class="campo2">
           <af:textBox classNameBase="input" name="strAteco" size="48" readonly="true" value="<%=strDesAteco%>" />
      </td>
    </tr> --%>

    <tr valign="top">
      <td class="etichetta2">Descrizione dell'attivit&agrave;</td>
      <td class="campo2">
        <af:textArea classNameBase="textarea" name="strDescAttivita" title="Descirizone dell'Attività" cols="40" value="<%=strDescAttivita%>" readonly="true"  maxlength="100"/>
      </td>
      <td class="campo2">
        <af:textArea classNameBase="textarea" name="strDescAttivita_2" title="Descirizone dell'Attività" cols="40" value="<%=strDescAttivita_2%>" readonly="true"  maxlength="100"/>
      </td>
    </tr>

    <tr>
    <td colspan="3">
        <table class="main" cellspacing=0 cellpadding=0>
          <tr>
            <td>
              <div class="sezione2">
              <img id='tendinaProspetti' alt='chiudi' src='../../img/aperto.gif' onclick="cambiaPro(this,document.getElementById('TBLPro'));" />&nbsp;&nbsp;&nbsp;Prospetti informativi L.68 che verranno annullati
            </td>
         </tr>
        </table>
    </td>
    </tr>
    <tr>
    	<td colspan="3">&nbsp;
    	<table class="main" id='TBLPro' style='display:''" width="100%">
			<tr valign="top">
    			<td class="etichetta2"></td>
    			<td>
    				<af:list moduleName="M_GestisciStatoProspetto"
         			 		 skipNavigationButton="1" />
				</td>
			</tr>
    	</table>
    	</td>
  	</tr>
    <tr>
    <td colspan="3">
        <table class="main" cellspacing=0 cellpadding=0>
          <tr>
            <td>
              <div class="sezione2">
              <img id='tendinaOrganico' alt='Apri' src='../../img/chiuso.gif' onclick="cambia(this,document.getElementById('TBL1'));" />&nbsp;&nbsp;&nbsp;Organico
            </td>
         </tr>
        </table>
    </td>
    </tr>

    <tr>
      <td colspan="3">
      <table class="main" id='TBL1' style='display:none'" width="100%">
        <tr valign="top">
          <td class="etichetta2" width="20%">Numero di soci</td>
          <td class="campo2" width="40%">
          <af:textBox classNameBase="input" type="integer" name="numSoci" title="Numero di Soci" value="<%=numSoci%>" readonly="true" size="5" maxlength="38" />
          </td>
          <td class="campo2" width="40%">
          <af:textBox classNameBase="input" type="integer" name="numSoci_2" title="Numero di Soci" value="<%=numSoci_2%>" readonly="true" size="5" maxlength="38" />
          </td>
        </tr>
        <tr valign="top">
          <td class="etichetta2">Numero di dipendenti</td>
          <td class="campo2">
            <af:textBox classNameBase="input" type="integer" name="numDipendenti" title="Numero di Dipendenti" size="5" value="<%=numDipendenti%>" readonly="true" maxlength="38"/>
          </td>
          <td class="campo2">
            <af:textBox classNameBase="input" type="integer" name="numDipendenti_2" title="Numero di Dipendenti" size="5" value="<%=numDipendenti_2%>" readonly="true" maxlength="38"/>
          </td>
        </tr>
        <tr valign="top">
          <td class="etichetta2">Numero di collaboratori</td>
          <td class="campo2">
            <af:textBox classNameBase="input" type="integer" name="numCollaboratori" title="Numero di collaboratori" size="5" value="<%=numCollaboratori%>" readonly="true" maxlength="38"/>
          </td>
          <td class="campo2">
            <af:textBox classNameBase="input" type="integer" name="numCollaboratori_2" title="Numero di collaboratori" size="5" value="<%=numCollaboratori_2%>" readonly="true" maxlength="38"/>
          </td>
        </tr>
        <tr valign="top">
          <td class="etichetta2">Numero di personale in altre posizioni</td>
          <td class="campo2">
            <af:textBox classNameBase="input" type="integer" name="numAltraPosizione" title="Numero di personale in altre posizioni" size="5" value="<%=numAltraPosizione%>" readonly="true" maxlength="38"/>
          </td>
          <td class="campo2">
            <af:textBox classNameBase="input" type="integer" name="numAltraPosizione_2" title="Numero di personale in altre posizioni" size="5" value="<%=numAltraPosizione_2%>" readonly="true" maxlength="38"/>
          </td>
        </tr>
        <tr>
          <td class="etichetta2">Data di aggiornamento</td>
          <td class="campo2">
            <af:textBox classNameBase="input" type="date" title="Data di aggiornamento" name="DATAGGINFORMAZIONE" size="11" maxlength="10" value="<%=datAggInformazione%>" readonly="true" />
          </td>
          <td class="campo2">
            <af:textBox classNameBase="input" type="date" title="Data di aggiornamento" name="DATAGGINFORMAZIONE_2" size="11" maxlength="10" value="<%=datAggInformazione_2%>" readonly="true" />
          </td>
        </tr>        
    </table>
    </td>
  </tr>


  <tr>
  <td colspan="3">
      <table class="main" cellspacing=0 cellpadding=0>
        <tr>
          <td>
            <div class="sezione2">
            <img id='tendinaAltreInfo' alt='Apri' src='../../img/chiuso.gif' onclick="cambia(this,document.getElementById('TBL2'));" />&nbsp;&nbsp;&nbsp;Altre Informazioni
          </td>
       </tr>
      </table>
  </td>
  </tr>


  <tr>
    <td colspan="3">
    <table class="main" id='TBL2' style='display:none'" width="100%">
      <tr valign="top">      
        <td class="etichetta2" width="10%">INAIL</td>
        <td class="etichetta2" width="10%">PAT</td>
        <td class="campo2"  width="40%" nowrap>
          <af:textBox classNameBase="input" type="integer" name="STRPATINAIL1" title="Pat INAIL" size="10" maxlength="8" value="<%=patInail1%>" readonly="true"/> - 
          <af:textBox classNameBase="input" type="integer" name="STRPATINAIL2" title="Pat INAIL" size="3" maxlength="2" value="<%=patInail2%>" readonly="true"/>
          <input type="hidden" name="STRPATINAIL" value="<%=patInail%>"/>                    
        </td>
        <td class="campo2" width="40%" nowrap>
          <af:textBox classNameBase="input" type="integer" name="STRPATINAIL1_2" title="Pat INAIL" size="10" maxlength="8" value="<%=patInail1_2%>" readonly="true"/> - 
          <af:textBox classNameBase="input" type="integer" name="STRPATINAIL2_2" title="Pat INAIL" size="3" maxlength="2" value="<%=patInail2_2%>" readonly="true"/>
          <input type="hidden" name="STRPATINAIL_2" value="<%=patInail_2%>"/>                    
        </td>
      </tr>
      <tr valign="top">      
        <td></td>
        <td class="etichetta2">Reparto</td>
        <td class="campo2">
          <af:textBox classNameBase="input" name="strRepartoInail" title="Reparto INAIL" size="30" maxlength="100" value="<%=strRepartoInail%>" readonly="true" />
        </td>
        <td class="campo2">
          <af:textBox classNameBase="input" name="strRepartoInail_2" title="Reparto INAIL" size="30" maxlength="100" value="<%=strRepartoInail_2%>" readonly="true" />
        </td>
      </tr>
      <tr valign="top">        
        <td></td>
        <td class="etichetta2">N&deg; albo imprese interinali</td>
        <td align="left">
          <af:textBox classNameBase="input" name="strNumAlboInterinali" title="Numero albo imprese interinali" size="30" maxlength="100" value="<%=strNumAlboInterinali%>" readonly="true" />
        </td>
        <td align="left">
          <af:textBox classNameBase="input" name="strNumAlboInterinali_2" title="Numero albo imprese interinali" size="30" maxlength="100" value="<%=strNumAlboInterinali_2%>" readonly="true" />
        </td>
      </tr>    
    </table>
    </td>
  </tr>

  <tr><td>&nbsp;</td></tr>
  <tr valign="top">
    <td class="etichetta2" width="20%">Data inizio attivit&agrave;</td>
    <td width="40%">
      <af:textBox classNameBase="input" type="date" title="Data di inizio dell'attività" name="datInizio" size="11" maxlength="10" value="<%=datInizio%>" readonly="true" />
    </td>
    <td width="40%">
      <af:textBox classNameBase="input" type="date_2" title="Data di inizio dell'attività" name="datInizio" size="11" maxlength="10" value="<%=datInizio_2%>" readonly="true" />
    </td>
  </tr>
  <tr valign="top">
    <td class="etichetta2">Data fine attivit&agrave;</td>
    <td>
      <af:textBox classNameBase="input" type="date" title="Data di fine dell'attività" name="datFine" size="11" maxlength="10" value="<%=datFine%>" readonly="true" validateWithFunction="checkDataInzioFine" />
    </td>
    <td>
      <af:textBox classNameBase="input" type="date" title="Data di fine dell'attività" name="datFine_2" size="11" maxlength="10" value="<%=datFine_2%>" readonly="true" validateWithFunction="checkDataInzioFine" />
    </td>
  </tr>
  <tr valign="top">
    <td class="etichetta2">Note</td>
    <td class="campo2">
       <af:textArea classNameBase="textarea" name="strNote" cols="40" value="<%=strNote%>" readonly="true" />
    </td>
    <td class="campo2">
       <af:textArea classNameBase="textarea" name="strNote_2" cols="40" value="<%=strNote_2%>" readonly="true" />
    </td>
  </tr>

<tr><td colspan="3">&nbsp;</td></tr>


  <tr valign="top">
    <td class="etichetta2">Unit&agrave; Locali</td>
    <td>

         <af:list moduleName="M_GetTestateAziendaAccorpamento.UNITA1"
         		  configProviderClass="it.eng.sil.module.ido.DynamicRicUnitaAziAccorpaConfig"
         		  skipNavigationButton="1" />

    </td>
    <td>

         <af:list moduleName="M_GetTestateAziendaAccorpamento.UNITA2"
         		  configProviderClass="it.eng.sil.module.ido.DynamicRicUnitaAziAccorpaConfig"
         		  skipNavigationButton="1" />

    </td>
  </tr>

<%
  // if (operatoreInfo != null) operatoreInfo.showHTML(out);
%>
<%if(canManage) {%>
  <tr valign="top">
    <td class="etichetta">&nbsp;</td>
    <td class="campo">
    	<br/>        
		<input class="pulsanti" type="button"  name="Accorpa1su2"
				value="Accorpa e cancella &gt;&gt;" onClick="accorpa(1, 2);"/>
    </td>
    <td class="campo">
    	<br/>
    	<input class="pulsanti" type="button" name="Accorpa2su1"
    			value="&lt;&lt; Accorpa e cancella" onClick="accorpa(2, 1);"/>
    </td>
  </tr>    
<%}%>
</table>
<%= htmlStreamBottom %>

<%
} // if !dopoAccorpamento
%>

<center>
	<input type="button" name="torna" class="pulsanti" value="  Chiudi  "
			onClick="goBackLista()" />
</center>
<br/>

</af:form>

</body>

</html>
