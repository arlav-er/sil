

<table width="100%">
	<tr class="note">
    	<td colspan="2">
      		<div class="sezione2">
          		<img id='tendinaNote' alt='Chiudi' src='../../img/chiuso.gif' onclick="cambiaTendinaNote(this,'NoteSez');" />&nbsp;&nbsp;&nbsp;
          		NOTA modifiche&nbsp;&nbsp;
      		</div>
    	</td>
	</tr>
	<tr>
  		<td colspan="2">
    		<div id="NoteSez" style="display: none;">
      			<%if (!strNotaModifica.equals("")){ %>
                	<ul><%= strNotaModifica %> </ul>
              	<%}%>
        </td>
    </tr>
</table>
      		
      		
<script language="javascript">
 
 var imgChiusa = "../../img/chiuso.gif";
	var imgAperta = "../../img/aperto.gif";
 
	function cambiaNote(elem,stato){
		divVar = document.getElementById(elem);
		divVar.style.display = stato;
	}

	function cambiaTendinaNote(immagine,sezione) {
		if (immagine.alt == "Apri"){
			cambiaNote(sezione,"inline");
	   		immagine.src=imgAperta;
	    	immagine.alt="Chiudi";
	  	} else {
			cambiaNote(sezione,"none");
	    	immagine.src=imgChiusa;
	    	immagine.alt="Apri";
	  	}
	}
    
    var imgV = document.getElementById("tendinaNote");
  
  <% if (!strNotaModifica.equals("")){%>
    cambiaNote("NoteSez","inline");
    imgV.src = imgAperta;
    imgV.alt="Chiudi";
  <%} else {%>
    cambiaTendinaNote(imgV,"NoteSez");
  <%}%>
  

 
</script>
    		