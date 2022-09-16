<SCRIPT language="javascript">
	function gestVisualGiorniRitardo(){
		var obj = document.getElementById("campoRit");
	    var obj2 = document.getElementById("labelRit");
		<%if(!codMonoMovDich.equals("O")){%>
			if(obj != null)
			obj.style.display = "none";
	   	  	obj2.style.display = "none";
		<%} else {%>
			if(obj != null)
			obj.style.display = "inline";
	   		obj2.style.display = "inline";
		 <% } %>
	}

  	function gestisciRitardo(){
    	if(document.Frm1.codMonoMovDich.value != 'O'){nascondiRitardo();}
  	}
  	
  	function selezionaComboAgevolazioni() {
  		var obj = document.Frm1.codAgevolazione;
  		if (obj != undefined) {
		 	var codiciBenefici = '<%=codAgevolazione%>';
		 	var tokens = codiciBenefici.split(",");
		 	for(var i=0;i<tokens.length;i++) {
		 		var selected = tokens[i];
		 		if (selected != "") {
			 		for(var j=0;j<document.Frm1.codAgevolazione.length;j++) {
			 			if(document.Frm1.codAgevolazione[j].value==selected) {
			 				document.Frm1.codAgevolazione[j].selected=true;
			 			}
			 		}
		 		}
		 	}
		 }
	 }
  	
</SCRIPT>