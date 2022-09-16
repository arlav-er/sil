<SCRIPT TYPE="text/javascript">
  function btFindNotificaRDC_onclick(prgRDCCampo) {

    var s= "AdapterHTTP?PAGE=RicercaNotificheRDCPage&PROVENIENZA=Patto";
	 
    if (prgRDCCampo != null) {
	    s += "&prgRDCCampo="+prgRDCCampo.name;
	}
     window.open(s,"Notifiche RDC", 'toolbar=0, scrollbars=1');

  }//end function
 

</SCRIPT>
