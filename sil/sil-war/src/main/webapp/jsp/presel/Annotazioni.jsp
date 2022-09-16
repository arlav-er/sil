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

  String txtNotaDisponibilita= "";
  String txtNotaCV= "";

  BigDecimal cdnUtIns=new BigDecimal(0);
  Object dtmIns="";
  BigDecimal cdnUtMod=new BigDecimal(0);;
  Object dtmMod="";
  
  //Vector vectConoInfo= serviceResponse.getAttributeAsVector("M_LOADNOTE.ROWS.ROW");
  Vector vectConoInfo= serviceResponse.getAttributeAsVector("M_LOADANNOTAZIONI.ROWS.ROW");
  if ( (vectConoInfo != null) && (vectConoInfo.size() > 0) ) {

    SourceBean beanLastInsert = (SourceBean)vectConoInfo.get(0);

    txtNotaDisponibilita = StringUtils.getAttributeStrNotNull(beanLastInsert, "TXTNOTADISPONIBILITA");
    txtNotaCV           = StringUtils.getAttributeStrNotNull(beanLastInsert, "TXTNOTACV");

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

  String fieldReadOnly = "";
  String htmlStreamTop = StyleUtils.roundTopTable(canModify);
  String htmlStreamBottom = StyleUtils.roundBottomTable(canModify);
  
  if (canModify) {fieldReadOnly="false";}
  else {fieldReadOnly="true";}

%>

<html>

<head>
  <title>Annotazioni lavoratore</title>

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


           //window.top.menu.caricaMenuLav( <%=_funzione%>,   <%=cdnLavoratore%>);
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

    <input type="hidden" name="PAGE" value="AnnotazioniPage">
    <input type="hidden" name="MODULE" value="M_InsertUpdateNote"/>
    <input type="hidden" name="CDNFUNZIONE" value="<%=_funzione%>"%>
    <input type="hidden" name="CDNLAVORATORE" value="<%= cdnLavoratore %>"/>

    <input type="hidden" name="CDNUTINS" value="<%= codiceUtenteCorrente %>"/>
    <input type="hidden" name="CDNUTMOD" value="<%= codiceUtenteCorrente %>"/>

    <center>
      <font color="green">
        <af:showMessages prefix="M_INSERTUPDATEANNOTAZIONI"/>
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
              <af:showMessages prefix="M_InsertAnnotazioni"/>
            </font>
          </center>
        </td>
      </tr>
      <tr>
        <td class="etichetta">Limitazioni della disponibilit&agrave;</td>
        <td class="campo">
        <af:textArea cols="50" 
                  rows="4" 
                  name="TXTNOTADISPONIBILITA"
                  classNameBase="textarea"                  
                  readonly="<%=fieldReadOnly%>"
                  onKeyUp="fieldChanged()"                  
                  maxlength="3000"
                  value="<%=txtNotaDisponibilita%>" />
        </td>
      </tr>
      <tr>
        <td class="etichetta">Note Curriculum Vitae</td>
        <td class="campo">
        <af:textArea cols="50" 
                  rows="4" 
                  name="TXTNOTACV"
                  classNameBase="textarea"
                  readonly="<%=fieldReadOnly%>"
                  onKeyUp="fieldChanged()"                  
                  maxlength="3000"
                  value="<%=txtNotaCV%>" />
        </td>
      </tr>
    </table>
    <br/>

    <% if ( canModify ) { %> 
      <center>
          <input class="pulsante" type="submit" name="salva" value="Aggiorna">
      </center>
    <%}
    out.print(htmlStreamBottom);
    testata.showHTML(out);
    %>
  </af:form>
</body>

</html>
