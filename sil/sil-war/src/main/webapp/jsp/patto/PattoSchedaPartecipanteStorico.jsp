<%@page import="it.eng.afExt.utils.StringUtils"%>
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
	SourceBean pattoStorico = (SourceBean)serviceResponse.getAttribute("M_PattoDettaglioStorico.ROWS.ROW");
	BigDecimal cdnLavoratoreBD= (BigDecimal )pattoStorico.getAttribute("CDNLAVORATORE");
	String cdnLavoratore = cdnLavoratoreBD.toPlainString();
	String _current_page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _current_page);
	filter.setCdnLavoratore(cdnLavoratoreBD);
 	PageAttribs pageAtts = new PageAttribs(user, _current_page);
	
	boolean canModify = false;
	boolean isProtocollato = false;
	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
		return;
	} 
  
    String readOnlyStr   =canModify?"false":"true";

    String codContratto = "";
	String codDurata = "";
	String codStudio = "";
	String codSvantaggio = "";
	String codOccupazione = "";
	String strNoteScheda = "";
 	String numkLoPartecipante = "";
    SourceBean schedaPartecipante = (SourceBean)serviceResponse.getAttribute("M_SchedaPartecipantePatto.ROWS.ROW");
    String strNote = "";
   	boolean canSaveOrUpdate = false;
     String prgPattoLavoratore =  Utils.notNull((String)serviceRequest.getAttribute("prgPattoLavoratore"));
    if(StringUtils.isFilledNoBlank(prgPattoLavoratore) ){
    	canSaveOrUpdate = true;
    }
    if (schedaPartecipante!=null) {//
        codContratto = Utils.notNull(schedaPartecipante.getAttribute("CODCONTRATTO"));
    	codDurata = Utils.notNull(schedaPartecipante.getAttribute("CODDURATA"));
    	codStudio = Utils.notNull(schedaPartecipante.getAttribute("CODSTUDIO"));
    	codOccupazione = Utils.notNull(schedaPartecipante.getAttribute("CODOCCUPAZIONE"));
    	strNoteScheda = Utils.notNull(schedaPartecipante.getAttribute("STRNOTESCHEDA"));
    	numkLoPartecipante = Utils.notNull(schedaPartecipante.getAttribute("NUMKLOPARTECIPANTE"));
    }
    SourceBean codiciSvantaggioBean = (SourceBean)serviceResponse.getAttribute("M_SchedaSvantaggiPattoAperto.ROWS.ROW");
    if(codiciSvantaggioBean!=null && codiciSvantaggioBean.containsAttribute("CODICISVANTAGGIO")){
    	codSvantaggio = Utils.notNull(codiciSvantaggioBean.getAttribute("CODICISVANTAGGIO"));
    }
    ///////////////////////
    //String cdnLavoratore = (String) serviceRequest.getAttribute("cdnLavoratore");
    StringBuffer args = new StringBuffer();
    String cdnFunzione = (String)serviceRequest.getAttribute("CDNFUNZIONE");
    ////////////////////////
    PageProperties pageProperties = new PageProperties((String) serviceRequest.getAttribute("PAGE"),null);
    /*PageAttribs pageAtts = new PageAttribs((User) sessionContainer.getAttribute(User.USERID),(String) serviceRequest.getAttribute("PAGE"));
    boolean canModify = pageAtts.containsButton("aggiorna");
    String readOnlyStr   =canModify?"false":"true";*/
    //
    pageContext.setAttribute("pageProperties", pageProperties);
    pageContext.setAttribute("args", args);
    pageContext.setAttribute("isReadOnly", Boolean.valueOf(readOnlyStr));
   
    String htmlStreamTop = StyleUtils.roundTopTable(canModify);
    String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
    %>
<html>
<head>
<title>Scheda Partecipante</title>
<link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
<af:linkScript path="../../js/"/>

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

<font color="green">
 <af:showMessages />
</font>
<center>
<%out.print(htmlStreamTop);%>
  <table  class="main"  >

    <af:form method="post" name="Frm1" action="AdapterHTTP" >
    <%--onSubmit="updateStato(this)"--%>
        <input type="hidden" name="CDNLAVORATORE" value="<%=Utils.notNull(cdnLavoratore)%>">
        <input type="hidden" name="PAGE" value="<%=Utils.notNull(_current_page)%>">
        <input type="hidden" name="CDNFUNZIONE" value="<%=Utils.notNull(cdnFunzione)%>">        
        <input type="hidden" name="PRGPATTOLAVORATORE" value="<%=Utils.notNull(prgPattoLavoratore)%>">      
        <input type="hidden" name="NUMKLOPARTECIPANTE" value="<%=Utils.notNull(numkLoPartecipante)%>">  
     <tr><td colspan="4"><br><br></td></tr>
    <tr>
     <td colspan=4>
        <table width="100%">
     		<tr>
            	<td class="etichetta">Contratto&nbsp;</td> 
                <td class="campo">
                   <af:comboBox name="CODCONTRATTO" classNameBase="input"  addBlank="true" required="true" title="Contratto" 
                  		moduleName="M_Combo_Contratto" selectedValue="<%=codContratto%>"
                      	onChange="fieldChanged();" 
                      	disabled="<%=String.valueOf(!canModify || isProtocollato)%>" />
                </td>
            </tr>
            <tr>
            	<td class="etichetta">Titolo di studio&nbsp;</td> 
                <td class="campo" nowrap>
                   <af:comboBox name="CODSTUDIO" classNameBase="input"  addBlank="true" required="true" title="Titolo di studio" 
                  		moduleName="M_Combo_Studio" selectedValue="<%=codStudio%>"
                      	onChange="fieldChanged();" 
                      	disabled="<%=String.valueOf(!canModify || isProtocollato)%>" />
                </td>
            </tr>
            <tr>
            	<td class="etichetta">Condizione occupazionale&nbsp;</td> 
                <td class="campo">
                   <af:comboBox name="CODOCCUPAZIONE" classNameBase="input"  addBlank="true" required="true" title="Condizione occupazionale" 
                  		moduleName="M_Combo_Occupazione" selectedValue="<%=codOccupazione%>"
                      	onChange="fieldChanged();" 
                      	disabled="<%=String.valueOf(!canModify || isProtocollato)%>" />
                </td>
            </tr>
            <tr>
            	<td class="etichetta">Durata ricerca occupazionale&nbsp;</td> 
                <td class="campo">
                   <af:comboBox name="CODDURATA" classNameBase="input"  addBlank="true" required="true" title="Durata ricerca occupazionale" 
                  		moduleName="M_Combo_Durata" selectedValue="<%=codDurata%>"
                      	onChange="fieldChanged();" 
                      	disabled="<%=String.valueOf(!canModify || isProtocollato)%>" />
                </td>
            </tr>
            <tr>
           		<td class="etichetta">Svantaggi&nbsp;</td> 
               	<td class="campo">
                	<af:comboBox name="CODSVANTAGGIO" classNameBase="input"  addBlank="true" required="false" title="Svantaggi" 
               			moduleName="M_Combo_Svantaggio" selectedValue="<%=codSvantaggio%>" multiple="true" size="19"
                   		onChange="fieldChanged();" disabled="<%=String.valueOf(!canModify || isProtocollato)%>" />
               	</td>
           	</tr>            
            <tr>
                 <td class="etichetta">Nota scheda partecipante&nbsp;</td>
                 <td class="campo">
                 	<af:textArea onKeyUp="javascript:fieldChanged();" name="STRNOTESCHEDA" 
                 		value="<%=strNoteScheda%>" cols="60" rows="5" readonly="<%=readOnlyStr%>" maxlength="2000"/>
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
