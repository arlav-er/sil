<script language="JavaScript">

function openPage(pagina,parametri)
{ //alert(pagina+"\r\n"+parametri);
  // Se la pagina è già in submit, ignoro questo nuovo invio!
  if (isInSubmit()) return;

  setWindowLocation("AdapterHTTP?PAGE="+pagina +parametri);
}

</script>
