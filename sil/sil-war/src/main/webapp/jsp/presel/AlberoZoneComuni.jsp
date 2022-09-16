<%@ page contentType="text/html;charset=utf-8"%>

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
  com.engiweb.framework.security.*" %>

<%@ page extends="com.engiweb.framework.dispatching.httpchannel.AbstractHttpJspPage" %>

<%@ taglib uri="aftags" prefix="af" %>

<%
  Vector vectListaZone= serviceResponse.getAttributeAsVector("M_LISTZONECOMUNI.ROWS.ROW");

  /* TODO Commentare 
  Utils.dumpObject("vectListaZone", vectListaZone, out);
  */
%>

<html>

  <head>

    <link rel="stylesheet" type="text/css" href="../../css/stili.css"/>
    <link rel="stylesheet" type="text/css" href="../../css/listdetail.css"/>
	<af:linkScript path="../../js/"/>

    <script type="text/javascript" src="../../js/document.js">
    </script>

    <script type="text/javascript" src="../../js/tree_codifiche.js">
    </script>

    <script type="text/javascript">

      function AggiornaForm(codZona, descZona) {

        window.opener.document.Frm1.CODCPI.value = codZona;
        window.opener.document.Frm1.DESCZONA.value = descZona.replace(/\^/g, '\'');
        window.close();
      }

      var objTree = new jsTree;
      
      //create the root
      objTree.createRoot("","","","");

      function doLoad() {
        objTree.buildDOM();
      } 

  
    <%
      String lastCodCpi= "", cpiZonaCorrente= "";
      
      for (Iterator iter= vectListaZone.iterator(); iter.hasNext();) {

        SourceBean bean= (SourceBean)iter.next();

        String codCpi     = (String)bean.getAttribute("CODICE_CPI");
        String descComune = (String)bean.getAttribute("DESC_COMUNE");
        String descZona   = (String)bean.getAttribute("DESC_ZONA");

        if ( !lastCodCpi.equals(codCpi) ) {

          if ( lastCodCpi.length() == 0 ) {

            // Primissimo nodo : inserisce la root
            %>
              var desc_Main="Zone e comuni";
              var obj_Main=objTree.root.addChild("", "Zone e comuni", "", "");
            <%
          }

          // Primo nodo di una nuova zona : inserisce la zona
          %>
            var desc_<%= codCpi %>="<%= descZona %>";
            var obj_<%= codCpi %>= obj_Main.addChild("", "<%= descZona %>", "javascript:AggiornaForm('<%= codCpi %>', desc_<%= codCpi %>.replace('\\\'','^'));", "");
          <%
          
          cpiZonaCorrente= codCpi;
        }

        // Inserisce il comune 
        %>
          obj_<%= cpiZonaCorrente %>.addChild("", "<%= descComune %>", "", "");
        <%
        
        lastCodCpi= codCpi;
      }
    %>
    </script>

  </head>


  <body onload="doLoad();objLocalTree.toggleExpand('root_0');" class="gestione">
  <br/>
  <center><input type="button" name="chiudi" value="chiudi" class="pulsante" onClick="javascript:window.close();"/></center>
  </body>

</html>
