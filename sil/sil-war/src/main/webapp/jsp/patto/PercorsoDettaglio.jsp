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
    //
    String prgAzioneRagg = null;
    String moduleAzione = "M_GETAZIONI";
/*    if (serviceRequest.containsAttribute("FROM_MAIN")) {
        moduleAzione = "M_DEAZIONE2";
        Vector v = serviceResponse.getAttributeAsVector("M_DEAZIONE2.ROWS.ROW");
        if (v.size()>0)
        prgAzioneRagg = Utils.notNull(((SourceBean)v.get(0)).getAttribute("PRGAZIONERAGG"));
    }
    else {
        prgAzioneRagg = (String)serviceRequest.getAttribute("PRGAZIONERAGG");
    }*/
    prgAzioneRagg = (String)serviceRequest.getAttribute("PRGAZIONERAGG");
    Object azioni = serviceResponse.getAttribute("M_GETAZIONI.ROWS");
    /////////////////////
    PRG_TAB_DA_ASSOCIARE= prgPercorso;
    //////////////////////////////////
    String _page = (String) serviceRequest.getAttribute("PAGE"); 
    PageAttribs attributi = new PageAttribs(user, _page);
    boolean canModify = attributi.containsButton("aggiorna");
    //////////////////////
 //   canModify = true;
    ///////////////////////
    boolean readOnlyStr = !canModify;
    String fieldReadOnly = canModify?"false":"true";
//////////////////////////  
%>

<html>

<head>
  <title>Percorso Concordato</title>

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

    <table class="main">
      <tr>
          <td valign="top">           
            <table align="center"  width="100%">  
              <tr>
                <td colspan="2" ><center><b>Nuovo Percorso</b></center></td>
              </tr>
              <tr>
                <td>
                  <br/>
                </td> 
              </tr>

              <%--@ include file="Indisp_Elemento.inc" --%>
              <!-- --- NOTE: Gestione Patto
              -->
              <tr>
                <td class="etichetta">Data stimata</td>
                <td class="campo">
                    <af:textBox onKeyUp="fieldChanged();" classNameBase="input" readonly="<%=fieldReadOnly%>"  type="date"
                        name="datStimata" value="<%=datStimata %>" validateOnPost="true" required = "true"
                        size="12" maxlength="10"/>
                </td>
              </tr>
              <tr>
                <td class="etichetta">Misura</td>
                <%
                Vector rows = serviceResponse.getAttributeAsVector("M_DEAZIONI.ROWS.ROW");
                ComboPair docComboPair = new ComboPair(rows,prgAzioni, "prgAzioneRagg");
                prgAzioneRagg= docComboPair.getCodRef();
                %>
                <td class="campo"><af:comboBox disabled="<%=fieldReadOnly%>"  name="prgAzioneRagg"  
                        moduleName="M_DEAZIONIRAGG" selectedValue="<%= prgAzioneRagg %>"
                        addBlank="true" required="true" onChange="comboPair.populate();fieldChanged();" title="Raggruppamento"/></td>
              </tr>
              <%--if (azioni!=null){--%>
              <tr>
                <td class="etichetta">Azione</td>
                <td class="campo">
                <%if (fieldReadOnly.equals("false")) { %>
                    <af:comboBox disabled="<%=fieldReadOnly%>" onChange="fieldChanged();" name="prgAzioni"                           
                        addBlank="true" required="true" title="Azione"/>
                </td>
                <script>
                var arrayFiglio = new Array();                
                <%= docComboPair.makeArrayJSChild() %>
                var comboPair = new ComboPair(document.Frm1.prgAzioneRagg, document.Frm1.prgAzioni, arrayFiglio,true);
                comboPair.populate('<%=docComboPair.getCodRef()%>', '<%=prgAzioni%>');
                
                </script>
                <%} else {%>                
                  <af:comboBox onChange="fieldChanged();" disabled="true"  name="prgAzioni"  
                         selectedValue="<%= prgAzioni %>" moduleName="M_DEAZIONI" classNameBase="input"
                        addBlank="true" required="true" title="Azione"/>
        <%}%>
              </tr>               
              <%--}--%>
              <tr>
                <td class="etichetta">Esito</td>
                <td class="campo">
                    <af:comboBox onChange="fieldChanged();" disabled="<%=fieldReadOnly%>"  name="codEsito"  
                        moduleName="M_DEESITO" selectedValue="<%= codEsito %>"
                        addBlank="true" required="false" title="Esito"  />
                </td>
              </tr>
              <tr>
                <td class="etichetta">Note</td>
                <td class="campo">
                    <textarea rows="5" cols="60" name="strNote"><%=strNote%></textarea>
                </td>
              </tr>   
              <tr><td colspan=2>&nbsp;</td></tr>
              <!--   -->
              <tr>
                <td colspan=2>               
                  <%@ include file="../patto/_associazioneDettaglioXPatto.inc" %>
                </td>
              </tr>
            </table>
        </td>
    </tr>
    <tr>
        <td>
            <br> 
            <center>
                <% if (canModify) {%>
                <input class="pulsante" type="submit" name="salva" value="Aggiorna">  
                <% } %>
                <input class="pulsante" type="button" value="Indietro" onclick="indietro()">  
            </center>
        </td>
    </tr>
</table>
</af:form>
</body>

</html>
