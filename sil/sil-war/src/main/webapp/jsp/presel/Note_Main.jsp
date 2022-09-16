<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="aftags" prefix="af" %>
<%@ include file="../global/noCaching.inc" %>
<%@ include file="../global/getCommonObjects.inc" %>

<%@ page import="
  com.engiweb.framework.base.*,
  com.engiweb.framework.dispatching.module.AbstractModule,
  
  com.engiweb.framework.util.QueryExecutor,
  it.eng.sil.security.User,
  it.eng.sil.security.ProfileDataFilter,  
  it.eng.afExt.utils.*,
  it.eng.sil.util.*,
  java.util.*,
  java.math.*,
  java.io.*,
  it.eng.sil.security.PageAttribs,   
  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%
	String cdnLavoratore= (String )serviceRequest.getAttribute("CDNLAVORATORE");
	String _current_page = (String) serviceRequest.getAttribute("PAGE"); 
	ProfileDataFilter filter = new ProfileDataFilter(user, _current_page);
	filter.setCdnLavoratore(new BigDecimal(cdnLavoratore));
  PageAttribs attributi = new PageAttribs(user, _current_page);
	
	boolean canModify = false;
	boolean canDelete = false;

	boolean canView=filter.canViewLavoratore();
	if (! canView){
		response.sendRedirect("../../servlet/fv/AdapterHTTP?ACTION_NAME=accesso_negato_action");
	}else{
	  canModify = attributi.containsButton("aggiorna");
    	
    	if(!canModify){
    		canModify=false;
    	}else{
    		canModify=filter.canEditLavoratore();
    	}

    	if(!canDelete){
    		canDelete=false;
    	}else{
    		canDelete=filter.canEditLavoratore();
    	}
      
    }
%>

<%  
  //String cdnLavoratore = (String)serviceRequest.getAttribute("CDNLAVORATORE");

  String flgAutoMunito= "";
  String flgMotoMunito= "";
  String flgDispFormazione= "";
  String txtNoteCurriculum= "";
  String txtNoteCpi= "";
  String flgExArt16="";
  BigDecimal cdnUtIns=new BigDecimal(0);
  Object dtmIns="";
  BigDecimal cdnUtMod=new BigDecimal(0);;
  Object dtmMod="";

  Vector vectConoInfo= serviceResponse.getAttributeAsVector("M_LOADNOTE.ROWS.ROW");
  if ( (vectConoInfo != null) && (vectConoInfo.size() > 0) ) {

    SourceBean beanLastInsert = (SourceBean)vectConoInfo.get(0);

    flgAutoMunito       = StringUtils.getAttributeStrNotNull(beanLastInsert, "FLGAUTOMUNITO");
    flgMotoMunito       = StringUtils.getAttributeStrNotNull(beanLastInsert, "FLGMOTOMUNITO");
    flgDispFormazione   = StringUtils.getAttributeStrNotNull(beanLastInsert, "FLGDISPFORMAZIONE");
    txtNoteCurriculum   = StringUtils.getAttributeStrNotNull(beanLastInsert, "TXTNOTECURRICULUM");
    txtNoteCpi          = StringUtils.getAttributeStrNotNull(beanLastInsert, "TXTNOTECPI");
    flgExArt16          = StringUtils.getAttributeStrNotNull(beanLastInsert, "FLGEXART16");    

    cdnUtIns        = (BigDecimal)beanLastInsert.getAttribute("CDNUTINS");
    dtmIns          = beanLastInsert.getAttribute("DTMINS");
    cdnUtMod        = (BigDecimal)beanLastInsert.getAttribute("CDNUTMOD");
    dtmMod          = beanLastInsert.getAttribute("DTMMOD"); 
  }
  
  Object codiceUtenteCorrente= sessionContainer.getAttribute("_CDUT_");

  InfCorrentiLav infCorrentiLav= new InfCorrentiLav(  RequestContainer.getRequestContainer().getSessionContainer(), cdnLavoratore, user);
  Testata testata= new Testata(cdnUtIns, dtmIns, cdnUtMod, dtmMod);

  String _page = (String) serviceRequest.getAttribute("PAGE"); 
  int _funzione = Integer.parseInt((String) serviceRequest.getAttribute("CDNFUNZIONE"));
  Linguette linguette = new Linguette( user, _funzione, _page, new BigDecimal(cdnLavoratore));

  /*
  PageAttribs attributi = new PageAttribs(user, _page);
  boolean canModify= attributi.containsButton("inserisci");
  boolean canDelete= attributi.containsButton("rimuovi");  
  */

  String fieldReadOnly;
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
  
  if (canModify) {fieldReadOnly="false";}
  else {fieldReadOnly="true";}
  
%>

<html>

<head>
  <title>Conoscenza Informatica</title>

  <link rel="stylesheet" href="../../css/stili.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>

  <af:linkScript path="../../js/"/>

  <SCRIPT TYPE="text/javascript">
    function elaboraForm(modalita){
	  // Se la pagina è già in submit, ignoro questo nuovo invio!
	  if (isInSubmit()) return;

      if (modalita=="DEL"){
        if ( confirm("Sicuri di voler rimuovere questa annotazione ?") ) {
          document.MainForm.MODULE.value="M_DeleteNote";
          doFormSubmit(document.MainForm);
        }
      }
      else{
        document.MainForm.MODULE.value="M_InsertUpdateNote";
        doFormSubmit(document.MainForm);
      }
    }

    var flagChanged = false;
  
    function fieldChanged() {
      //alert("field changed !")      
      <% if ( canModify ) { %> 
        flagChanged = true;
      <% } %> 
    }


           window.top.menu.caricaMenuLav( <%=_funzione%>,   <%=cdnLavoratore%>);
  </SCRIPT>
<script language="Javascript">
<!--Contiene il javascript che si occupa di aggiornare i link del footer-->
  <% 
       //Genera il Javascript che si occuperà di inserire i links nel footer
       attributi.showHyperLinks(out, requestContainer,responseContainer,"cdnLavoratore=" + cdnLavoratore);
  %>
</script>

</head>

<body class="gestione" onload="rinfresca()">

  <%
    infCorrentiLav.show(out); 
    linguette.show(out);
  %>

  <af:form method="POST" action="AdapterHTTP" name="MainForm" id="MainForm" dontValidate="true">

    <input type="hidden" name="PAGE" value="NotePage">
    <input type="hidden" name="MODULE" value=""/> <!--Valorizzato nel javascript-->
    <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"%>
    <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>

    <input type="hidden" name="CDNUTINS" value="<%= codiceUtenteCorrente %>"/>
    <input type="hidden" name="CDNUTMOD" value="<%= codiceUtenteCorrente %>"/>

    <center>
      <font color="green">
        <af:showMessages prefix="M_INSERTUPDATENOTE"/>
        <af:showMessages prefix="M_DELETENOTE"/>
      </font>
      <font color="red">
        <af:showErrors/>
      </font>
    </center>

    <p align="center">
    <%out.print(htmlStreamTop);%>
    <table class="main">
      <tr>
        <td/></td>
      </tr>
      <tr>
        <td colspan="2">
          <center>
            <font color="green">
              <af:showMessages prefix="M_InsertNote"/>
            </font>
          </center>
        </td>
      </tr>
      <!-- <tr>
        <td class="etichetta">Possesso Autoveicolo</td>
        <td class="campo">
        <af:comboBox 
          title="Conoscenza certificata" 
          name="FLGAUTOMUNITO"
          classNameBase="combobox"
          disabled="false"
          onChange="fieldChanged()">

            <option value=""  <% if ( "".equals(flgAutoMunito) )  { %>SELECTED<% } %> ></option>
            <option value="S" <% if ( "S".equals(flgAutoMunito) ) { %>SELECTED<% } %> >Si</option>
            <option value="N" <% if ( "N".equals(flgAutoMunito) ) { %>SELECTED<% } %> >No</option>
        </af:comboBox>
        </td>
      </tr>
      <tr>
        <td class="etichetta">Possesso Motoveicolo</td>
        <td class="campo">
        <af:comboBox 
          title="Conoscenza certificata" 
          name="FLGMOTOMUNITO"
          classNameBase="combobox"
          disabled="false"
          onChange="fieldChanged()">
        
            <option value=""  <% if ( "".equals(flgMotoMunito) )  { %>SELECTED<% } %> ></option>
            <option value="S" <% if ( "S".equals(flgMotoMunito) ) { %>SELECTED<% } %> >Si</option>
            <option value="N" <% if ( "N".equals(flgMotoMunito) ) { %>SELECTED<% } %> >No</option>
         </af:comboBox>
        </td>
      </tr>
      <tr>
        <td class="etichetta">Disponibilit&agrave; Formazione</td>
        <td class="campo">
        <af:comboBox 
          title="Conoscenza certificata" 
          name="FLGDISPFORMAZIONE"
          classNameBase="combobox"
          disabled="false"
          onChange="fieldChanged()">
        
            <option value=""  <% if ( "".equals(flgDispFormazione) )  { %>SELECTED<% } %> ></option>
            <option value="S" <% if ( "S".equals(flgDispFormazione) ) { %>SELECTED<% } %> >Si</option>
            <option value="N" <% if ( "N".equals(flgDispFormazione) ) { %>SELECTED<% } %> >No</option>
        </af:comboBox>
        </td> -->
      </tr>
      <tr>
        <!-- NOTE: "Ex art 16" viene nascosto all'utente 
        <td class="etichetta">Ex art 16</td>
        <td class="campo">
          <SELECT name="FLGEXART16">
            <option value=""  <% if ( "".equals(flgExArt16) )  { %>SELECTED<% } %> ></option>
            <option value="S" <% if ( "S".equals(flgExArt16) ) { %>SELECTED<% } %> >Si</option>
            <option value="N" <% if ( "N".equals(flgExArt16) ) { %>SELECTED<% } %> >No</option>
          </SELECT>
        </td>
        -->
        <input type="hidden" name="FLGEXART16" value= "" />
      </tr>      
      <tr>
        <td class="etichetta">Altre Competenze<br>Altre Informazioni(SP)</td>
        <td class="campo">
        <af:textArea cols="50" 
                  rows="4" 
                  name="TXTNOTECURRICULUM"
                  classNameBase="textarea"
                  readonly="<%= fieldReadOnly %>"
                  onKeyUp="fieldChanged()"                  
                  maxlength="2000" value="<%= txtNoteCurriculum %>"/>
        </td>
      </tr>
      <tr>
        <td class="etichetta">Annotazioni del CPI</td>
        <td class="campo">
        <af:textArea cols="50" 
                  rows="4" 
                  name="TXTNOTECPI"
                  classNameBase="textarea"
                  readonly="<%= fieldReadOnly %>"
                  onKeyUp="fieldChanged()"                  
                  maxlength="3000" value="<%= txtNoteCpi %>"/>        
        </td>
      </tr>
    </table>
    <br/>

    <% if ( canModify ) { %> 
      <center>
          <input class="pulsante" type="button" name="salva" value="Aggiorna" onclick="javascript:elaboraForm('MOD');">
      </center>
    <%}
    out.print(htmlStreamBottom);
    %>
    <br/>
    <center>
      <% testata.showHTML(out); %>
    </center>
  </af:form>
</body>

</html>
