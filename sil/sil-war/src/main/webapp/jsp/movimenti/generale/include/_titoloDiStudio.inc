    <SCRIPT TYPE="text/javascript">
       function apriTitoloStudio(codTitolo){
       	  var f = "AdapterHTTP?PAGE=TitoloStudioPerMovimentiPAGE&CODTIPOTITOLOlav="+codTitolo+"&MODIFICATITOLO="+<%=modificaTitolo%>;
          var t = "_blank";
          //var feat = "toolbar=yes,location=yes,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,width=800,height=600,top=75,left=100";
          //var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=300,top=100";
          //var feat = "toolbar=0, scrollbars=1";
          
          var feat = "toolbar=0,location=no,directories=no,status=no,width=1000,height=250,top=60,left=10";
          
          opened = window.open(f, t, feat);
      }
    </SCRIPT>
    &nbsp;<a title="Titolo di studio" href="#" onClick="javascript:apriTitoloStudio(document.forms[0].CODTIPOTITOLOlav.value)"><img src="../../img/libro.gif" alt="Titolo di studio"></a>
    <input type="hidden" name="CODTIPOTITOLOlav" value="<%=codTipoTitoloStudio%>"/>                   
