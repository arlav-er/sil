<script language="javascript">
  function aggiorna(){
    window.opener.<%=funzione%>();
  }
  
  function impostaValoreCheck(obj){
    if(obj.checked)
      obj.value = "S"
    else
      obj.value = "N"

    if (obj.name == "FLGTITOLARETUTORE"){
      cambia(document.getElementById("esperienza"));
      cambia(document.getElementById("qualifica"));
      cambia(document.getElementById("tipo"));
      cambia(document.getElementById("mansioneD"));
    }
  }

  function cambia(object){
    if (object.style.display == "none"){
      object.style.display = "";
    } else{ 
        object.style.display = "none";
        azzeraCampi();
      }
  }

  function azzeraCampi(){
    document.Frm1.NUMANNIESPTUTORE.value="";
    document.Frm1.STRLIVELLOTUTORE.value="";
    document.Frm1.CODMANSIONE.value="";
    document.Frm1.DESCMANSIONE.value="";
    document.Frm1.CODTIPOMANSIONE.value="";
  }
  
</script>