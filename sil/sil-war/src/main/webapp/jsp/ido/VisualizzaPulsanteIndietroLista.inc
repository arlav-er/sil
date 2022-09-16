<center>
<div id="tornaAllaLista" >
<table><tr><td>
<%  if (pagina_lista==null) pagina_lista = "IdoListaAziendePage";
	String htmlToList = InfCorrentiAzienda.formatBackList(sessionContainer,pagina_lista); 
	if (htmlToList!=null) out.print(htmlToList);
%>
</td></tr></table>
</div>
</center>
<%if (htmlToList!=null ) {%>
<script>
	if (window.opener!=null) document.getElementById('tornaAllaLista').style.display='none';
</script>
<%}%>