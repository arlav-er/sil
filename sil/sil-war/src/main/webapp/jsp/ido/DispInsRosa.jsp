<%@ page contentType="text/html;charset=utf-8"%>
<%@ include file="../amministrazione/openPage.inc" %>

<%@ page import="com.engiweb.framework.base.*, 
                com.engiweb.framework.configuration.ConfigSingleton,
                java.text.*, java.util.*,it.eng.sil.util.*,
                java.util.GregorianCalendar,
                it.eng.afExt.utils.DateUtils,
                it.eng.afExt.utils.StringUtils,
                java.math.*, it.eng.sil.security.* "%>

<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>
                  
<%@ taglib uri="aftags" prefix="af"%>

<%
    int _funzione=0;
    boolean aggiornamento=false;

  Calendar cal = new GregorianCalendar();
  int hour24 = cal.get(Calendar.HOUR_OF_DAY);
  int min = cal.get(Calendar.MINUTE);
  String minuti=String.valueOf(min);
  if (minuti.length()==1){
    minuti="0" + minuti;
  }
  String strOraContatto=String.valueOf(hour24)+":"+ minuti;

  //----------
  String prgRichiestaAz = "";
  String prgOriginale = "";
  String prgAzienda = "";
  String prgUnita = "";
  String numAnno = "";
  String numRichiesta = "";
  //----------
  String prgSpi="";
  
  //inizializzo i campi
  String cdnLavoratore="";
  String codDispRosa="";
  String dataRilevazione = DateUtils.getNow();
  BigDecimal numContaNonRintracciato = new BigDecimal("0");  

  String prgRosa="";
  String cpiRose="";
  String prgNom="";
  String cdnUtins="";
  String dtmins="";
  String cdnUtmod="";
  String dtmmod="";
  InfCorrentiLav infCorrentiLav= null;
  Testata operatoreInfo = null;
  boolean ins=true;
  boolean modify=false;
  boolean goBack=false;
  
  String codGradoOcc = "";
  //String prgSpiContatto="89";
    
    //Recupero CODCPI
    int cdnUt = user.getCodut();
    int cdnTipoGruppo = user.getCdnTipoGruppo();
    String codCpi="";
    if(cdnTipoGruppo == 1) {
      codCpi =  user.getCodRif();
    }
    //---------------
    
    _funzione     = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
    //prgNom        = (String) serviceRequest.getAttribute("PRGNOMINATIVO");
    String mod    = (String) serviceRequest.getAttribute("MODULE");
    cpiRose       = (String) serviceRequest.getAttribute("CPIROSE");
    prgRosa       = (String) serviceRequest.getAttribute("PRGROSA");

    //-----------
    prgRichiestaAz  = serviceRequest.getAttribute("PRGRICHIESTAAZ").toString();
    /* Li prelevo da un modulo, in modo da non passarlo negli appuntamenti e contatti
    prgAzienda      = serviceRequest.getAttribute("PRGAZIENDA").toString();
    prgUnita        = serviceRequest.getAttribute("PRGUNITA").toString();
    */
    SourceBean rigaRichOriginale = (SourceBean) serviceResponse.getAttribute("M_GET_PRGORIGINALE.ROW");
    if(rigaRichOriginale != null) {
        prgOriginale = rigaRichOriginale.getAttribute("PRGORIGINALE").toString();
    }

    SourceBean prgSpiUt = (SourceBean) serviceResponse.getAttribute("M_GETPRGSPIUT.ROWS.ROW");
    if (prgSpiUt != null) {
      	prgSpi = prgSpiUt.containsAttribute("PRGSPI") ? prgSpiUt.getAttribute("PRGSPI").toString() : "";
    }

    SourceBean dispoL68SB = (SourceBean) serviceResponse.getAttribute("M_Load_DispL68.ROWS.ROW");
	if (dispoL68SB != null) {
   		codGradoOcc = dispoL68SB.containsAttribute("CODGRADOOCC") ? dispoL68SB.getAttribute("CODGRADOOCC").toString() : "";
    }
    
    SourceBean rigaTestata = (SourceBean) serviceResponse.getAttribute("M_GETTESTATARICHIESTA.ROWS.ROW");
    if (rigaTestata != null) {
      prgAzienda = rigaTestata.getAttribute("PRGAZIENDA").toString();
      prgUnita = rigaTestata.getAttribute("PRGUNITA").toString();
      numRichiesta = rigaTestata.getAttribute("NUMRICHIESTA").toString();
      if(numRichiesta==null) {numRichiesta = ""; }
      numAnno = rigaTestata.getAttribute("NUMANNO").toString();
      if(numAnno==null) { numAnno = ""; }
      /*if (rigaTestata.containsAttribute("prgSpi")) {
        prgSpi = rigaTestata.getAttribute("prgSpi").toString();      
      }*/
    }
    //-----------
    cdnLavoratore   = (String) serviceRequest.getAttribute("cdnLavoratore");
    //Set di una variabile di sessione per la navigazione a contatto ed appuntamento
    /*if ( (prgNom !=null) && (prgNom != "") ){
      sessionContainer.setAttribute("_PRGNOMINATIVO_",prgNom);
    }*/
    /*if ( (prgNom == null) || (prgNom=="") ){
      prgNom=(String)sessionContainer.getAttribute("_PRGNOMINATIVO_");
    }*/

    
    if ( mod != null ) {
      goBack=true; //Parametro impostato a true, ossia che il controllo deve ritornare alla lista dei nominativi
    }
    
    SourceBean lavInfo= (SourceBean)serviceResponse.getAttribute("M_GET_DISPONIBILITA.ROWS.ROW");
    
    if  (lavInfo != null)  {
      modify=true;
      ins=false;
      dataRilevazione       = (String) lavInfo.getAttribute("DATDISPONIBILITA");
      codDispRosa           = (String) lavInfo.getAttribute("CODDISPONIBILITAROSA");
      prgOriginale          = (String) lavInfo.getAttribute("PRGORIGINALE");
      //cdnLavoratore         = (BigDecimal) lavInfo.getAttribute("cdnLavoratore");

      if (lavInfo.containsAttribute("NUMCONTANONRINTRACCIATO")) {
      numContaNonRintracciato = (BigDecimal) lavInfo.getAttribute("NUMCONTANONRINTRACCIATO");
      }
      if (lavInfo.containsAttribute("cdnUtins")) {
        cdnUtins=lavInfo.getAttribute("cdnUtins").toString();
      }
      if (lavInfo.containsAttribute("dtmins")) {
        dtmins=lavInfo.getAttribute("dtmins").toString();
      }
      if (lavInfo.containsAttribute("cdnUtmod")) {
        cdnUtmod=lavInfo.getAttribute("cdnUtmod").toString();
      }
      if (lavInfo.containsAttribute("dtmmod")) {
        dtmmod=lavInfo.getAttribute("dtmmod").toString();
      }
    }
    infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
    operatoreInfo= new Testata(cdnUtins, dtmins, cdnUtmod, dtmmod);
    
    // NOTE: Attributi della pagina (pulsanti e link) 
    PageAttribs attributi = new PageAttribs(user, "DispInsRosaPage");
    boolean canModify = attributi.containsButton("aggiorna");
    boolean canInsert = attributi.containsButton("inserisci");

    //if  (lavInfo != null)  {canModify=false;} //Si può anche aggiornare il record
 %>

<%
String htmlStreamTop = StyleUtils.roundTopTable(canModify);
String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);

String mess = StringUtils.getAttributeStrNotNull(serviceRequest, "MESSAGE");
String listPage = StringUtils.getAttributeStrNotNull(serviceRequest, "LIST_PAGE");

String openStr = "&PRGROSA=" + prgRosa + "&CPIROSE=" + cpiRose + "&CDNFUNZIONE=" + _funzione;
openStr += "&PRGRICHIESTAAZ=" + prgRichiestaAz + "&PRGAZIENDA=" + prgAzienda + "&PRGUNITA=" +prgUnita;
if(!mess.equals("")) {
	openStr += "&MESSAGE=" + mess;
	if(!listPage.equals("")) { openStr += "&LIST_PAGE=" + listPage; }
}
%>

<%
// POPUP EVIDENZE
String strApriEv = StringUtils.getAttributeStrNotNull(serviceRequest, "APRI_EV");
int _fun = 1;
if(_funzione>0) { _fun = _funzione; }
EvidenzePopUp jsEvid = null;
boolean bevid = attributi.containsButton("EVIDENZE");
if(strApriEv.equals("1") && bevid) {
	jsEvid = new EvidenzePopUp(cdnLavoratore, _fun, user.getCdnGruppo(), user.getCdnProfilo());
}	
%> 

<html>
<head>
<title>Gestione Disponibilità</title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">

<af:linkScript path="../../js/"/>

<%if(strApriEv.equals("1") && bevid) { jsEvid.show(out); }%>

<SCRIPT language="javascript">
  var flagChanged = false;
  function controllaCampi(){
    if (document.Frm1.CREAUTCONT.checked){
      if(trim(document.Frm1.STRORACONTATTO.value) == ""){
        alert("L'ora del contatto deve essere valorizzata")
        return false;
      }
      var d=new Date("October 12, 1988 " + document.Frm1.STRORACONTATTO.value + ":00");
      if (isNaN(d.getHours()) || isNaN(d.getMinutes())){
        alert("L'ora deve essere in formato hh:mm");
        return false;
      }
      var ore=d.getHours();
      var min=d.getMinutes();
      if (ore<10){
        ore="0" + ore;
      }      
      if (min<10){
        min="0" + min;     
      }
      document.Frm1.STRORACONTATTO.value=ore + ":" + min;      
    }

    if (trim(document.Frm1.DATDISPONIBILITA.value) == ""){
      alert ("Il campo 'Data rilevazione' non può essere vuoto.");
      return false;
    }
    else
      if (trim(document.Frm1.CODDISPONIBILITAROSA.value) == ""){
        alert ("Il campo 'Esito' non può essere vuoto.");
        return false;
      }  
      else if((document.Frm1.CREAUTCONT.checked) && (document.Frm1.PRGSPICONTATTO.options[document.Frm1.PRGSPICONTATTO.selectedIndex].text == "")){
      	alert("In caso di creazione automatica del contatto il campo Operatore è obbligatorio");
      	return false;
      }else
        return true;
  }
  
  function fieldChanged() {
    flagChanged = true;
  }
  
  function Insert(){
   <%if( codGradoOcc!=null){%> 
  	if (document.Frm1.CODDISPONIBILITAROSA.value == "C") {
  		if (confirm("Attenzione: si vuole cambiare il grado di occupabilità in 'In Sospeso'?")){
        	document.Frm1.CHECKMODDISPOL68.value = 1;
      	}
  	}
   <%}%>
    UpdateValidita();
    document.Frm1.MODULE.value="M_INSERT_DISP_ROSA";
  }

  function IncContaNonRintracciato() {
    if (document.Frm1.CODDISPONIBILITAROSA.value == "D") {
      document.Frm1.NUMCONTANONRINTRACCIATO.value = <%= numContaNonRintracciato.longValue() + 1 %>;
    }
  }

  
  function Update(){
    <%if( codDispRosa!=null && codDispRosa.equals("C")){%>
      if (document.Frm1.CODDISPONIBILITAROSA.value != "C"){
        alert("Lo stato del curriculum del lavoratore non viene modificato.");
      }
    <%}%>
    
    <%if( codGradoOcc!=null){%>    
	    if (document.Frm1.CODDISPONIBILITAROSA.value == "C") {
	  		if (confirm("Attenzione: si vuole cambiare il grado di occupabilità in 'In Sospeso'?")){
	        	document.Frm1.CHECKMODDISPOL68.value = 1;
	      	}
	  	}
    <%}%>
    UpdateValidita();
    document.Frm1.MODULE.value="M_UPDATE_DISP_ROSA";
    IncContaNonRintracciato();    
  }

  function UpdateValidita(){
    var validitaCV = "";
    if (document.Frm1.CODDISPONIBILITAROSA.value == "C"){
      if(confirm("Lo stato del curriculum del lavoratore va messo 'in attesa di verifica'?")){
        validitaCV = "SI";
      }
    }
    document.Frm1.VALIDITA.value = validitaCV;
    IncContaNonRintracciato();
  }
  
  function esci(){
    if (flagChanged) {
      if (confirm("I campi sono stati modificati. Si desidera continuare senza aggiornare?")){
          //openPage('MatchDettRosaPage','&PRGROSA=<%=prgRosa%>&CPIROSE=<%=cpiRose%>&CDNFUNZIONE=<%= _funzione %>&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>');
          openPage('MatchDettRosaPage','<%=openStr%>');
      }
    } else {
          //openPage('MatchDettRosaPage','&PRGROSA=<%=prgRosa%>&CPIROSE=<%=cpiRose%>&CDNFUNZIONE=<%= _funzione %>&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&PRGAZIENDA=<%=prgAzienda%>&PRGUNITA=<%=prgUnita%>');
          openPage('MatchDettRosaPage','<%=openStr%>');
      }
  }

  function abilitaOra(){
    //document.Frm1.STRORACONTATTO.disabled=!document.Frm1.STRORACONTATTO.disabled;
    if (document.Frm1.CREAUTCONT.checked){
      document.Frm1.STRORACONTATTO.disabled=false;
      document.Frm1.PRGSPICONTATTO.disabled=false;
    }
    else{
      document.Frm1.STRORACONTATTO.disabled=true;
      document.Frm1.PRGSPICONTATTO.disabled=true;
    }
  }
</script>
</head>

<body class="gestione" onLoad="javascript:document.Frm1.PRGSPICONTATTO.disabled=true;abilitaOra();<%if(strApriEv.equals("1") && bevid) { %> apriEvidenze() <%}%>" >
<% if ( !goBack ){%>
<% infCorrentiLav.setSkipLista(true);
  infCorrentiLav.show(out); %>
<font color="green">
  <af:showMessages prefix="M_INSERT_DISP_ROSA"/>
  <af:showMessages prefix="M_UPDATE_DISP_ROSA"/>
</font>
<font color="red">
  <af:showErrors/>
</font>
<p class="titolo"><b>Gestione Disponibilità</b></p>
<%out.print(htmlStreamTop);%>      
<af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="controllaCampi()">

          <input type="hidden" name="PAGE" value="DispInsRosaPage"/>
          <input type="hidden" name="MODULE" value="modulo"/>
          <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>
          <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"/>
          <!--<input type="hidden" name="PRGNOMINATIVO" value="<%= prgNom %>"/>-->

          <input type="hidden" name="PRGRICHIESTAAZ" value="<%= prgRichiestaAz %>"/>
          <input type="hidden" name="PRGORIGINALE" value="<%=prgOriginale%>"/>
          <input type="hidden" name="PRGAZIENDA" value="<%= prgAzienda %>"/>
          <input type="hidden" name="PRGROSA" value="<%= prgRosa %>"/>
          <input type="hidden" name="CPIROSE" value="<%= cpiRose %>"/>
          <input type="hidden" name="PRGUNITA" value="<%= prgUnita %>"/>
          <input type="hidden" name="NUMRICHIESTA" value="<%=numRichiesta%>"/>
          <input type="hidden" name="NUMANNO" value="<%=numAnno%>"/>
          <input type="hidden" name="VALIDITA" value=""/>
          <input type="hidden" name="NUMCONTANONRINTRACCIATO" value="<%= numContaNonRintracciato %>"/>
          <input type="hidden" name="CODCPICONTATTO" value="<%= codCpi %>"/>
          
          <input type="hidden" name="CHECKMODDISPOL68" value="0"/>
          
          <%if(!mess.equals("")) {%>
				<input type="hidden" name="MESSAGE" value="<%=mess%>"/>
				<%if(!listPage.equals("")) {%>
					<input type="hidden" name="LIST_PAGE" value="<%=listPage%>"/>
		 		<%}%>
		  <%}%>
	      
	      <table width="100%" class="main">
          <!-- Pulsanti di contatti ed appuntamento -->
          <tr>
            <td colspan="2" width="100%" class="bianco"> 
                <A HREF="AdapterHTTP?PAGE=ScadContattoPage&PageProvenienza=DispInsRosaPage&CDNLAVORATORE=<%= cdnLavoratore %>&CDNFUNZIONE=<%=_funzione %>&DIREZIONE=O&TIPO=4&codcpi=<%= codCpi %>&PRGROSA=<%=prgRosa%>&CPIROSE=<%=cpiRose%>&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&RECUPERAINFO=LAVORATORE&MESSAGE_ROSA=<%=mess%>&LIST_PAGE_ROSA=<%=listPage%>"><IMG name="image" border="0" src="../../img/contatti.gif" alt="Contatto"/></a>&nbsp;Contatto
                &nbsp;&nbsp;&nbsp;
                <A HREF="AdapterHTTP?PAGE=ScadAppuntamentoPage&PageProvenienza=DispInsRosaPage&CDNLAVORATORE=<%= cdnLavoratore %>&CDNFUNZIONE=<%=_funzione %>&codcpi=<%= codCpi %>&PRGROSA=<%=prgRosa%>&CPIROSE=<%=cpiRose%>&PRGRICHIESTAAZ=<%=prgRichiestaAz%>&MESSAGE_ROSA=<%=mess%>&LIST_PAGE_ROSA=<%=listPage%>"><IMG name="image" border="0" src="../../img/agendina.gif" alt="Appuntamento"/></A>&nbsp;Appuntamento
            </td>
          </tr>
          <!-- Pulsanti di contatti ed appuntamento -->
          <tr><td><br/></td></tr><tr><td><br/></td></tr>
           <tr>
            <% if (numContaNonRintracciato != null && numContaNonRintracciato.longValue() != 0) { %>
             <td class="etichetta" valign="middle">Num. volte non rintracciato</td>
             <td class="campo">
              <af:textBox classNameBase="input" 
              			  title="Num. volte non rintracciato" 
              			  type="integer" 
              			  name="NUMVOLTENONRINTRACCIATO" 
              			  value="<%=numContaNonRintracciato.toString()%>" size="10" maxlength="12" 
              			  validateOnPost="true" readonly="true"/> 
             </td>
            <% } else{%>
             <td colspan="2">&nbsp;</td>
            <% } %>            
            <td class="etichetta" align="left">Creazione automatica contatto</td>
             <td class="campo">
                <input type="checkbox" name="CREAUTCONT" value="AUT" alt="Creazione automatica contatto" checked="TRUE" onClick="abilitaOra();"/>
             </td>             
           </tr>
          <tr><td><br/></td></tr>          
          <tr>
            <td class="etichetta" valign="middle">Data rilevazione</td>
            <td class="campo">
              <af:textBox classNameBase="input" onKeyUp="fieldChanged();" title="Data rilevazione" type="date" required="true" name="DATDISPONIBILITA" value="<%= dataRilevazione %>" size="11" maxlength="12" validateOnPost="true" readonly="<%= String.valueOf(!canModify) %>"/> 
            </td>
            <td class="etichetta">Ora del contatto</td>
            <td class="campo">
                <input type="text" class="input" onKeyUp="fieldChanged();" name="STRORACONTATTO" title="Ora contatto" type="date" value="<%= strOraContatto %>" size="5" maxlength="5" disabled="true" />
            </td>            
          </tr>
          <tr><td><br/></td></tr>
          <tr valign="top">
            <td class="etichetta" valign="middle">Esito</td>
            <td class="campo">
                <af:comboBox name="CODDISPONIBILITAROSA" title="Esito" moduleName="M_GET_TIPI_DISP" addBlank="true" required="true" selectedValue="<%= codDispRosa %>" disabled="<%= String.valueOf(!canModify) %>" onChange="fieldChanged();"/>
            </td>
            <td class="etichetta">Operatore</td>
            <td class="campo">
              <af:comboBox name="PRGSPICONTATTO" size="1" title="Operatore"
                             multiple="false" disabled="false"
                             classNameBase="input"
                             focusOn="false" moduleName="COMBO_SPI_SCAD"
                             addBlank="true" selectedValue="<%=prgSpi%>"/>
            </td>
          </tr>

          <tr><td><br/></td></tr>
          <tr>
            <% if ( canModify ) { %>
              <td align="center" colspan="2">
              <% if ( modify ) { %>            
                    <input type="submit" class="pulsanti" name="salva" value="Aggiorna" onClick="javascript:Update();">
              <%} else {
                    if ( ins ){%>
                        <input type="submit" class="pulsanti" name="salva" value="Inserisci" onClick="javascript:Insert();">
                    <%}
                  }
              }%>
            <% if ( !canModify ) { %>
              <td colspan="2">
                <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onclick="javascript:esci();">
              </td>
            <% } else { %>
            	<td colspan="2">
                  <input type="button" class="pulsanti" name="chiudi" value="Chiudi" onclick="javascript:esci();">
                </td>
              <%}%>
          </tr>
    </table>
</af:form>
<%out.print(htmlStreamBottom);%>
<p align="center">
  <%operatoreInfo.showHTML(out);%>
</p>
<%} //fine (!goBack)
  else {%>
    <script language="javascript">
      openPage('MatchDettRosaPage','<%=openStr%>');
    </script>
  <%}%>
</body>
</html>