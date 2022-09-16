<script language="javascript">
<!--
 
function goTo(queryString) {
 var precomp = false;
 
 	<%if (existPrecompilato) {%>
	    precomp=true;
	<%}%>
	prepareSubmit(); 
	try{
		if (flagChanged==true || precomp == true) {
			if (! confirm("I dati sono cambiati.\r\nProcedere lo stesso ?")) {
				undoSubmit();
				return;
			}
		}
	}catch(e) {}
	window.location = "AdapterHTTP?" + queryString;
}

-->
</script>