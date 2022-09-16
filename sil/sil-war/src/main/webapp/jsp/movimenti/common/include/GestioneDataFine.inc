<%/* 
	 La gestione del ReadOnlyDataFine è stata spostata nelle jsp di inserimento e 
     validazione perchè dipendente dal contesto
*/%>
<script language="Javascript">
function setDatFine(tempo){
  var tempovalue = tempo.options[tempo.selectedIndex].value;
  <%if(codTipoMov.equals("TRA")){%>
      if (tempovalue == "D")
        document.Frm1.datFineMov.value = "<%=datFineMov%>";    
  <%}%>  
}
</script>