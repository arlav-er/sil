<SCRIPT TYPE="text/javascript">
function controllaMansione(codMansione) {
  if (codMansione == '') {
    alert("Prima è necessario selezionare una mansione");
    return false;
  }
  else {
    var strCodMansione = new String(codMansione);
    if (strCodMansione.substring(strCodMansione.length-2,strCodMansione.length) != '00') {
      return true;
    }
    else {
      if (confirm('Non è stata indicata una mansione specifica, continuare ?')) {
        return true;
      }
      else {
        return false;
      }
    }
  }
}
</SCRIPT>

  