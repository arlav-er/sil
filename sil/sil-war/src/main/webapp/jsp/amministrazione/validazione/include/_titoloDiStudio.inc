    <SCRIPT TYPE="text/javascript">
       function apriTitoloStudio(codTitolo){
          var f = "AdapterHTTP?PAGE=TitoloStudioPerMovimentiPAGE&CODTIPOTITOLOlav="+codTitolo+"&MODIFICATITOLO="+<%=modificaTitolo%>;
          var t = "_blank";
          var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=600,top=75,left=100";
          opened = window.open(f, t, feat);
      }
    </SCRIPT>
    &nbsp;<a href="#" onClick="javascript:apriTitoloStudio(document.forms[0].CODTIPOTITOLOlav.value)"><img src="../../img/libro.gif" alt="Titolo di studio"></a>
    <input type="hidden" name="CODTIPOTITOLOlav" value="<%=codTipoTitolo%>"/>                   
