<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import=" com.engiweb.framework.base.*,
                  com.engiweb.framework.dispatching.module.AbstractModule,
                  
                  com.engiweb.framework.util.QueryExecutor,
                  it.eng.sil.security.User,
                  it.eng.sil.security.PageAttribs,
                  it.eng.sil.security.ProfileDataFilter,                  
                  it.eng.afExt.utils.*,
                  it.eng.sil.util.*,
                  java.util.*,
                  java.math.*,
                  java.io.*,
                  com.engiweb.framework.security.*" %>

      
<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String cdnLavoratore= (String )serviceRequest.getAttribute("CDNLAVORATORE");
	String _page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
 	PageAttribs attributi = new PageAttribs(user, _page);

	boolean canInsert = false;
  	boolean readOnlyStr=true;
  	boolean canDocumentiAss=false;
	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
      readOnlyStr = !attributi.containsButton("AGGIORNA");
      canInsert   =  attributi.containsButton("INSERISCI");
      canDocumentiAss   =  attributi.containsButton("DOC_ASSOCIATI");
    	if((!canInsert) && (readOnlyStr)){
    		//canInsert=false;
        //readOnlyStr=true;
    	}else{
        boolean canEdit=filter.canEditLavoratore();
        if (canInsert){
          canInsert=canEdit;
        }
        if (!readOnlyStr){
          readOnlyStr=!canEdit;
        }        
    	}
  }
%>

<% 

    //String     cdnLavoratore      =null;
    String     obblForma_flg      =null; 
    String     obblScolastico_flg =null; 
    String     codModalita        =null; 
    String     codMotivoArchiviazione=null; 
    String     oblFormaNote       =null; 
    String     dtmIns             =null; 
    String     dtmMod             =null;
    
    BigDecimal cdnUtIns           =null; 
    BigDecimal cdnUtMod           =null; 
    BigDecimal keyLock = new BigDecimal("-1");
    InfCorrentiLav testata= null;
    //boolean readOnlyStr  = false;
    //boolean canInsert    = false;
    boolean flag_insert  = false;
    Linguette l  = null;
    SourceBean row_obbligoForma  = (SourceBean) serviceResponse.getAttribute("M_GETOBBLIGOFORMATIVO.ROWS.ROW");
    SourceBean rowDataNascita = (SourceBean )serviceResponse.getAttribute("M_GETDATANASCITALAVORATORE.ROWS.ROW");
    Testata operatoreInfo = null;
    String dataNascita = (String)rowDataNascita.getAttribute("DATNASC");
    int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
    //cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
    ///////////////////////////////////////////
    String COD_LST_TAB="AM_OBBFO";
    String PRG_TAB_DA_ASSOCIARE=null;
    SourceBean row = null;
    ///////////////////////////////////////////    

    testata= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore,user);
    //"Creo" le linguette --
    int _cdnFunz = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE")); 
    //String _page = (String) serviceRequest.getAttribute("PAGE"); 
    l  = new Linguette(user,  _cdnFunz, _page, new BigDecimal(cdnLavoratore));
    if (row_obbligoForma != null)    { 
        obblForma_flg      = (String)     row_obbligoForma.getAttribute("FLGOBBLIGOFORMATIVO");
        obblScolastico_flg = (String)     row_obbligoForma.getAttribute("FLGOBBLIGOSCOLASTICO");
        codModalita        = (String)     row_obbligoForma.getAttribute("CODMODALITAASSOLV");
        codMotivoArchiviazione= (String)     row_obbligoForma.getAttribute("CODMOTIVOARCHIVIAZIONE");
        oblFormaNote       = (String)     row_obbligoForma.getAttribute("STRNOTE"); 
        keyLock            = (BigDecimal) row_obbligoForma.getAttribute("NUMKLOOBBLIGOFORM");
        cdnUtIns           = (BigDecimal) row_obbligoForma.getAttribute("CDNUTINS");
        cdnUtMod           = (BigDecimal) row_obbligoForma.getAttribute("CDNUTMOD");
        dtmIns             = (String)     row_obbligoForma.getAttribute("DTMINS");
        dtmMod             = (String)     row_obbligoForma.getAttribute("DTMMOD");        
        row  = row_obbligoForma;
        PRG_TAB_DA_ASSOCIARE=((BigDecimal)  row.getAttribute("CDNLAVORATORE")).toString();
    }
    else   {
        flag_insert = true;// il record non esiste -> siamo in inserimento
    }

    operatoreInfo= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);
    
    // NOTE: Attributi della pagina (pulsanti e link) 
    /*PageAttribs attributi = new PageAttribs(user, "AmministrObbligoFormaPage");
    readOnlyStr = !attributi.containsButton("AGGIORNA");
    canInsert   =  attributi.containsButton("INSERISCI");*/
    String htmlStreamTop = StyleUtils.roundTopTable(canInsert);
    String htmlStreamBottom = StyleUtils.roundBottomTable(canInsert);
%>
<html>
<head>
<title>Amministrazione Dettaglio</title>

<link rel="stylesheet" href="../../css/stili.css" type="text/css">
<%@ include file="CommonScript.inc"%>
<script language="Javascript" src="../../js/docAssocia.js"></script>

<script>
<!--
function getFormObj() {return document.form1;}
<%@ include file="../patto/_associazioneXPatto_scripts.inc" %>
//-->
<%@ include file="../patto/_sezioneDinamica_script.inc"%>
</script>
<af:linkScript path="../../js/"/>
<script language="Javascript">
<%
    attributi.showHyperLinks(out, requestContainer,
    responseContainer,"cdnLavoratore=" + cdnLavoratore);
%>
</script>  
<script>
<!--
var dataNascitaLavoratore = "<%=dataNascita%>";
// dataNascita String in formato "dd/mm/yyyy"
function calcolaEta(dataNascita) { 
    dataOdierna = new Date();
    var items = dataNascita.split("/");
    annoNascita = items[2];
    meseNascita = items[1];
    giornoNascita = items[0];

    anni = dataOdierna.getFullYear()-parseInt(annoNascita, 10);
    mesi = dataOdierna.getMonth()+1-parseInt(meseNascita, 10);// getMonth() restituisce 0..11
    giorni = dataOdierna.getDate()-parseInt(giornoNascita, 10);
    if (mesi<0 ||(mesi==0 && giorni<0)) anni--;
    return anni;
}
function controllaObbFormativo() {
    if (document.form1.obblFormativo.selectedIndex!=1 && calcolaEta(dataNascitaLavoratore)<=18 && calcolaEta(dataNascitaLavoratore)>=16) {
        alert("Si e' in obbligo formativo dai 16 ai 18 anni");
        return true;
    }
    return true;
}
function controllaObbScolastico () {
    if (document.form1.obblScolastico.selectedIndex!=1 && calcolaEta(dataNascitaLavoratore)<=15){
        alert("Si e' in obbligo di istruzione fino a 16 anni");
        return true;
    }
    if ((document.form1.obblFormativo.selectedIndex==1 && document.form1.codModalitaAssolv.selectedIndex==0)
        || (document.form1.obblFormativo.selectedIndex==0 && document.form1.codModalitaAssolv.selectedIndex>0)){
        alert("Assolvimento obbligo formativo e modalita' di assolvimento vanno valorizzati entrambi");
        return false;
    }
    return true;
}

function controllaCampi(){
    if (!controllaObbFormativo()) return false;
    if (!controllaObbScolastico()) return false;
    return true;
}
//-->

    window.top.menu.caricaMenuLav( <%=_funzione%>,<%=cdnLavoratore%>);
</script>
</head>
<body class="gestione" onload="rinfresca()">
<%
   testata.show(out);   
   l.show(out);
   //----------------------
%>
    

<font color="red"><af:showErrors/></font>
<font color="green">
 <af:showMessages prefix="M_SaveObbligoFormativo"/>
 <af:showMessages prefix="M_InsertObbligoFormativo"/>
</font>

<script language="javascript">
  var flgInsert = <%=flag_insert%>;
</script>
<af:form name="form1" method="POST" action="AdapterHTTP" onSubmit="controllaCampi() && controllaPatto() && controllaStatoAtto(flgInsert,this)">
<p align="center">
<%out.print(htmlStreamTop);%>
<table class="main">
<!-- <tr>
    <td class="titolo" colspan="4"><center><b>Notizie sull'obbligo formativo</b></center></td>
</tr> -->
<tr><td><br/></td></tr>
<tr>
    <td class="etichetta">Diritto/dovere di istruzione assolto</td>
    <td class="campo">      
      <af:comboBox name="obblScolastico"
                   addBlank="false"
                   disabled="<%=String.valueOf(readOnlyStr)%>"
                   classNameBase="input"
                   onChange="fieldChanged();">
          <OPTION value="" <%if (obblScolastico_flg == null) out.print("SELECTED=\"true\"");%>></OPTION>
          <OPTION value="S" <%if (obblScolastico_flg != null && obblScolastico_flg.equalsIgnoreCase("S")) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
          <OPTION value="N" <%if (obblScolastico_flg != null && obblScolastico_flg.equalsIgnoreCase("N")) out.print("SELECTED=\"true\"");%>>No</OPTION>
      </af:comboBox>  
      
    </td>
</tr>
<tr><td colspan=2>&nbsp;</td></tr>
<tr>
    <td class="etichetta">Diritto/dovere di formazione assolto</td>
    <td class="campo">
      <af:comboBox name="obblFormativo"
                   title="Obbligo formativo assolto"
                   classNameBase="input"
                   selectedValue="<%=obblForma_flg%>"
                   required="false" addBlank="false" disabled="<%=String.valueOf(readOnlyStr)%>" onChange="fieldChanged();">
          <OPTION value="" <%if (obblForma_flg == null) out.print("SELECTED=\"true\"");%>></OPTION>
          <OPTION value="S" <%if (obblForma_flg != null && obblForma_flg.equalsIgnoreCase("S")) out.print("SELECTED=\"true\"");%>>Sì</OPTION>
          <OPTION value="N" <%if (obblForma_flg != null && obblForma_flg.equalsIgnoreCase("N")) out.print("SELECTED=\"true\"");%>>No</OPTION>
      </af:comboBox>     
    </td>
</tr>
<tr>
    <td class="etichetta">Modalità di assolvimento obbligo</td>
    <td class="campo"><af:comboBox name="codModalitaAssolv" moduleName="M_GETMODALITAASSOLV" selectedValue="<%=codModalita%>"
                     classNameBase="input" addBlank="true" disabled="<%=String.valueOf(readOnlyStr)%>" onChange="fieldChanged();"/>
    </td>
</tr>
<tr>
    <td class="etichetta">Motivi di Archiviazione</td>
    <td class="campo"><af:comboBox name="codMotivoArchiviazione" moduleName="M_GetMotivoArchiviazione" selectedValue="<%=codMotivoArchiviazione%>"
                     classNameBase="input" addBlank="true" disabled="<%=String.valueOf(readOnlyStr)%>" onChange="fieldChanged();"/>
    </td>
</tr>
<tr ><td colspan="2" ><hr width="90%"/></td></tr>
<tr><td colspan="2">
  <table border="0" width="100%">
    <tr><td class="etichetta">Note<br/></td>
        <td colspan="3">
          <af:textArea classNameBase="textarea" onKeyUp="fieldChanged();" name="strNote" value="<%=oblFormaNote%>"
                       cols="60" rows="4" maxlength="3000"  readonly="<%=String.valueOf(readOnlyStr)%>" />
        </td>
    </tr>
  </table>
</td></tr>
</table>

<br>
<%@ include file="../patto/_associazioneDettaglioXPatto.inc"%>

<br/>
  <table class="main"  width="100%">
  <tr><td width="33%"></td>
      <td width="34%">
        <%if(!readOnlyStr)
          { if(flag_insert)
            { if(canInsert){%><input class="pulsante" type="submit" name="insertDatiAmministr" value="Inserisci"><%}
            } 
            else
            { keyLock= keyLock.add(new BigDecimal(1));%>
              <input class="pulsante" type="submit" name="salvaDatiAmministr" value="Aggiorna">
          <%}//else
          }//if(!readOnlyStr)%>
      </td>
      
      	<td width="33%" <%if(flag_insert) out.print("style=\"display:'none'\"");%> >
        <%if(canDocumentiAss){%>
        <input class="pulsante" type="button" name="Documenti associati" value="Documenti associati"
                onClick="docAssociati('<%=cdnLavoratore%>','<%=_page%>','<%=_cdnFunz%>','','<%=cdnLavoratore%>')">
        <%}%>
      </td>
      
  </tr>
  </table>
  <%out.print(htmlStreamBottom);%>
  
  <center><table><tr><td align="center">
  <% operatoreInfo.showHTML(out); %>
  </td></tr></table></center>
  
  <input type="hidden" name="PAGE" value="AmministrObbligoFormaPage"/>
  <input type="hidden" name="cdnLavoratore" value="<%=cdnLavoratore%>"/>
  <input type="hidden" name="CDNFUNZIONE" value="<%=_cdnFunz%>"/>
  <input type="hidden" name="numKloObbligoForm" value="<%=Utils.notNull(keyLock)%>"/>
</af:form>
<br/>
</body>
</html>