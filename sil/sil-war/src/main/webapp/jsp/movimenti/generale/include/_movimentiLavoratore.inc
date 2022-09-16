    <SCRIPT TYPE="text/javascript">
       function apriMovimentiLavoratore(cdnLavoratore){
          var f = "AdapterHTTP?PAGE=MovimentiLavoratore&CDNLAVORATORE="+cdnLavoratore;
          var t = "_blank";
          //var feat = "toolbar=yes,location=yes,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,width=800,height=800,top=75,left=100";
          var feat = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=800,height=400,top=75,left=100";
          if (cdnLavoratore != "")
	        opened = window.open(f, t, feat);
	      else
	        alert("Associare un lavoratore!");
      }
    </SCRIPT>
    
    &nbsp;<a title="Movimenti lavoratore" href="#" onClick="javascript:apriMovimentiLavoratore(document.forms[0].CDNLAVORATORE.value)"><img src="../../img/Reitera.gif" alt="Movimenti lavoratore"></a>

