<script language="javascript">
<!--
  // Per rilevare la modifica dei dati da parte dell'utente
  var flagChanged = false;  
  function fieldChanged() {
    <%if (canModify) {
        out.print("flagChanged = true;");
      }
    %>
  }
-->
</script>