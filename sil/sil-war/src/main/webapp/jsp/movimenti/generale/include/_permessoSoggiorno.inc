    <SCRIPT TYPE="text/javascript">
       function apriPermessoSoggiorno(){
       	  var f = "AdapterHTTP?PAGE=PermessoSoggiornoPerMovimentiPage";
       	  if (contesto == 'valida') {
       	  	f = f + "&PRGMOVIMENTOAPP="+document.Frm1.PRGMOVIMENTOAPP.value;
       	  }
       	  else {
       	  	if (contesto == 'consulta') {
       	  		f = f + "&PRGMOVIMENTO="+document.Frm1.PRGMOVIMENTO.value;
       	  	}
       	  	else {
       	  		if (contesto == 'rettifica') {
       	  			f = f + "&PRGMOVIMENTO="+document.Frm1.PRGMOVIMENTORETT.value;
       	  		}
       	  	}
       	  }
       	  var t = "_blank";
          var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=400,top=75,left=100";
          opened = window.open(f, t, feat);
      }
    </SCRIPT>
    &nbsp;<a title="Permesso di soggiorno" href="#" onClick="javascript:apriPermessoSoggiorno()"><img src="../../img/carta_permesso.gif" alt="Permesso di soggiorno"></a>
                      
