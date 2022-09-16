<script language="JavaScript">
    // Rilevazione Modifiche da parte dell'utente
    var flagChanged = false;
        
    function fieldChanged() 
    { <% if (!readOnlyStr) { %>
            //alert("CAMBIATO!!");
            flagChanged = true;
      <% } %>
    }
</script>
