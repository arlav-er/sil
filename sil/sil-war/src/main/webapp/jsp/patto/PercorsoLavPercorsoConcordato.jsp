<%@ page contentType="text/html;charset=utf-8"%>

<%@ taglib uri="aftags" prefix="af" %>
<%@ taglib uri="patto" prefix="pt" %>
<!-- -->
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %> 

<%@ page import="
  com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.User,
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  it.eng.sil.security.PageAttribs,
  com.engiweb.framework.security.*"%>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %> 

<%  
    // --- NOTE: Gestione Patto
    String PRG_TAB_DA_ASSOCIARE = null;
    String COD_LST_TAB = "OR_PER";
    // ---
    String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");
    String cdnFunzione = (String) serviceRequest.getAttribute("CDNFUNZIONE");
    ///////////////////////////////////////////////////////////    
    Vector percorsiConcordati = serviceResponse.getAttributeAsVector("M_GETPERCORSO.ROWS.ROW");
    SourceBean row = (SourceBean)percorsiConcordati.get(0);
    String datStimata = Utils.notNull(row.getAttribute("DATSTIMATA"));      
    String prgAzioni = Utils.notNull(row.getAttribute("PRGAZIONI"));    
    String codEsito = Utils.notNull(row.getAttribute("CODESITO"));      
    String strNote = Utils.notNull(row.getAttribute("STRNOTE"));        
    String prgPercorso = Utils.notNull(row.getAttribute("PRGPERCORSO"));
    String prgColloquio = Utils.notNull(row.getAttribute("PRGCOLLOQUIO"));
    String datEffettiva = Utils.notNull(row.getAttribute("datEffettiva"));
    //
    BigDecimal cdnUtIns             = null;
	String     dtmIns               = null;
	BigDecimal cdnUtMod             = null;
	String     dtmMod               = null;
	cdnUtIns             =  (BigDecimal) row.getAttribute("CDNUTINS");
	dtmIns               =  (String)     row.getAttribute("DTMINS");
	cdnUtMod             =  (BigDecimal) row.getAttribute("CDNUTMOD");
	dtmMod               =  (String)     row.getAttribute("DTMMOD");
	
    //
    String prgAzioneRagg = null;
    String moduleAzione = "M_GETAZIONI";

    prgAzioneRagg = (String)serviceRequest.getAttribute("PRGAZIONERAGG");
    Object azioni = serviceResponse.getAttribute("M_GETAZIONI.ROWS");
    /////////////////////
    PRG_TAB_DA_ASSOCIARE= prgPercorso;
    //////////////////////////////////
    String _page = (String) serviceRequest.getAttribute("PAGE"); 
    //
    if (serviceResponse.getAttribute("M_GetPattoInfoCollegate.rows.row")!=null)
	    row = (SourceBean)serviceResponse.getAttribute("M_GetPattoInfoCollegate.rows.row");
	else row = new SourceBean("EMPTY");
    ///////////////////////
    Testata operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
    boolean canModify = false;
    boolean readOnlyStr = !canModify;
    String fieldReadOnly = canModify?"false":"true";
//////////////////////////  
String htmlStreamTop = StyleUtils.roundTopTable(false);
String htmlStreamBottom = StyleUtils.roundBottomTable(false);
%>

<html>

<head>
  <title>Percorso lavoratore: Percorso concordato</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" href="../../css/listdetail.css" type="text/css"/> 
  
  <af:linkScript path="../../js/"/>
  <SCRIPT TYPE="text/javascript">
<!--
    // --- NOTE: Gestione Patto --------------------------
    <%@ include file="../patto/_sezioneDinamica_script.inc" %>
    <%@ include file="../patto/_associazioneXPatto_scripts.inc" %>
    
    function getFormObj() {return document.Frm1;}
    
    // ----------------------------------------------
    
    function indietro() {
    	// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;
    
        var s= "AdapterHTTP?";
        s += "PAGE=PERCORSIPAGE&";
        s += "PRGCOLLOQUIO=<%=prgColloquio%>&";
        s += "CDNLAVORATORE=<%= cdnLavoratore %>&";
        s += "CDNFUNZIONE=<%=cdnFunzione%>&";
        setWindowLocation(s);
    }
//-->
  </SCRIPT>

   <script language="JavaScript">
        // Rilevazione Modifiche da parte dell'utente
        var flagChanged = false;
        
        function fieldChanged() {
         <% if (!fieldReadOnly.equalsIgnoreCase("true")){ %> 
            flagChanged = true;
         <%}%> 
        }
    </script>

  <script>
    <!--
    function showAzioni(){
		// Se la pagina è già in submit, ignoro questo nuovo invio!
		if (isInSubmit()) return;

        document.Frm1.PAGE.value="PERCORSODETTAGLIOPAGE";
        doFormSubmit(document.Frm1);
    }
    //-->
  </script>
  <script src="../../js/ComboPair.js"></script>
</head>

<body class="gestione">
<br><br>
  <script language="javascript">
    var flgInsert = false;
  </script>
  <af:form method="POST" action="AdapterHTTP" name="Frm1" onSubmit="controllaPatto() && controllaStatoAtto(flgInsert,this)" >

    <input type="hidden" name="PAGE" value="PERCORSIPAGE">
    <input type="hidden" name="MODULE" value="M_UPDATEPERCORSO"/>
    <input type="hidden" name="CDNFUNZIONE" value="<%=cdnFunzione%>">
    <input type="hidden" name="PRGPERCORSO" value="<%=prgPercorso%>">
    <input type="hidden" name="PRGCOLLOQUIO" value="<%=prgColloquio%>">
    <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>
    
    <center>
      <font color="red">
        <af:showErrors/>
      </font>
    </center>
	<%out.print(htmlStreamTop);%>
    <table class="main">
      <tr>
          <td valign="top">           
            <table align="center"  width="100%">  
              <tr><td colspan="2"><p class="titolo">Azione concordata</p></td></tr>
              <tr><td>&nbsp;</td></tr>              
              <!-- --- NOTE: Gestione Patto
              -->
              <tr>
                <td class="etichetta">Obiettivo</td>
                <%
                Vector rows = serviceResponse.getAttributeAsVector("M_DEAZIONI.ROWS.ROW");
                ComboPair docComboPair = new ComboPair(rows,prgAzioni, "prgAzioneRagg");
                prgAzioneRagg= docComboPair.getCodRef();
                %>
                <td class="campo"><af:comboBox disabled="<%=fieldReadOnly%>"  name="prgAzioneRagg"  
                        moduleName="M_DEAZIONIRAGG" selectedValue="<%= prgAzioneRagg %>" classNameBase="input"
                        addBlank="true" required="true" onChange="comboPair.populate();fieldChanged();" 
                        title="Raggruppamento"/></td>
              </tr>
              <tr>
                <td class="etichetta">Azione</td>
                <td class="campo" nowrap="nowrap">
                
                  <af:comboBox  disabled="true"  name="prgAzioni"  
                         selectedValue="<%= prgAzioni %>" moduleName="M_DEAZIONI" classNameBase="input"
                         addBlank="true" required="true" title="Azione"/>
              </tr>
              <tr>
                <td class="etichetta">Data stimata</td>
                <td class="campo">
                    <af:textBox  classNameBase="input" readonly="<%=fieldReadOnly%>"  type="date"
                        name="datStimata" value="<%=datStimata %>" validateOnPost="true" required = "true"
                        size="12" maxlength="10"/>
                </td>
              </tr> 
              <%if (!datEffettiva.equals("")) {%>
              <tr>
                <td class="etichetta">Data di svolgimento/conclusione</td>
                <td class="campo">
                    <af:textBox  classNameBase="input" readonly="<%=fieldReadOnly%>"  type="date"
                        name="datEffettiva" value="<%=datEffettiva %>" 
                        size="12" maxlength="10"/>
                </td>
              </tr>                      
              <%}%>       
              <tr>
                <td class="etichetta">Esito</td>
                <td class="campo">
                    <af:comboBox disabled="<%=fieldReadOnly%>"  name="codEsito"  
                        moduleName="M_DEESITO" selectedValue="<%= codEsito %>" classNameBase="input"
                        addBlank="true" required="false" title="Esito"  />
                </td>
              </tr>
              <tr>
                <td class="etichetta">Note</td>
                <td class="campo">
        	        <af:textArea classNameBase="textarea" name="strNote" value="<%=strNote%>"
	                 cols="60" rows="5" maxlength="500"
    	             readonly="true"/></td>
              </tr>   
              <tr><td colspan=2>&nbsp;</td></tr>
              <!--   -->
              <tr>
                <td colspan=2>               
                  <%@ include file="_associazioneDettaglioXPattoChiuso.inc" %>
                </td> 
              </tr>
            </table>
        </td>
    </tr>
    
</table>
<%out.print(htmlStreamBottom);%>

<center>
	<% operatoreInfo.showHTML(out);%>
	<br>
	<input class="pulsante" type="button" value="Chiudi" onclick="window.close()">  
</center>


</af:form>
</body>

</html>
