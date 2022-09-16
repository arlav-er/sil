<script language="javascript">
function controllaStatoAtto(flag_insert,form1){
  //alert (form1.StatoPatto.value + " - " + flag_insert);
  if (form1.StatoPatto.value != ""){
        if (!flag_insert) {
          return true;
        } else {
            if (form1.StatoPatto.value.substr(0,12) == "PROTOCOLLATO"){
            	return true;
            } 
          } 
   }
   return true;
}
</script>